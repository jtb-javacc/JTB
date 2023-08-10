# How JTB builds the nodes trees and the default visitors

This section aims at explaining the main things about the node generation performed by JTB.

JTB does different things: starting with a `.jtb` grammar file:
- it **generates** **syntaxtree** classes corresponding to the nodes
- it **annotates** the `.jtb` grammar file to produce a JavaCC grammar file `.jj` with blocks of Java code
   that creates the nodes and assembles them in the tree
- it **generates** **visitor** *interfaces* (with `visit(...)` methods) and default **visitor** *classes*
   (with `accept(...)`methods) which implement a full traversal of the trees in a depth first manner
   (i.e. it walks downward before walking righward)

Each **syntaxtree** class / node builds its own (sub-)tree; the grammar usually has one *entry* point, which
 will be the root of the grammar tree; but the grammar can be used with different *entry* points, resulting
 to different grammar trees, but there is nothing specifically generated for this; the *root* of the tree
 is the node corresponding to the *entry* point used when parsing.

## JavaCC & JTB schematic grammars

We first present a schematic layout of parts of interest of the JavaCC grammar, in order to fix terms involved
 in node generation (in **bold** and *italic*).<br>
User grammar writers do not have to know about the JavaCC grammar itself, but as this section aims at
 explaining things to the JTB contributors as well as to these user grammar writers, we needed to go through
 the (parts of interest of the) JavaCC/JTB grammars; those are for contributors and examples are for writers).

<pre><code>
<b>JavaCodeProduction</b><br>
  "JAVACODE"<br>
  AccessModifier()<br>
  ResultType()<br>
  IdentifierAsString() <code class="green">// the name of the JavaCodeProduction, used in other productions</code><br>
  FormalParameters()<br>
  [ "throws" Name() ( "," Name() )* ]<br>
  Block()<br>
<br>
<b>BNFProduction</b><br>
  AccessModifier()<br>
  ResultType()<br>
  IdentifierAsString() <code class="green">// the name of the BNFProduction, used in other productions</code><br>
  FormalParameters()<br>
  [ "throws" Name() ( "," Name() )* ]<br>
  ":"<br>
  Block()<br>
  "{"<br>
  <code class="NavajoWhite">ExpansionChoices()</code><br>
  "}"<br>
<br>
<b>ExpansionChoices</b><br>
  <code class="NavajoWhite">Expansion()</code><br>
  ( "|" <code class="NavajoWhite">Expansion()</code> )*<br>
</code></pre>

We can distinguish two types of *ExpansionChoices*, one without choice and one with choices, as they will
 lead to different node generations:
 
<pre><code>
<b>ExpansionChoicesWithoutChoices</b><br>
  Expansion()<br>
<br>
<b>ExpansionChoicesWithChoices</b><br>
  Expansion()<br>
  ( "|" Expansion() )+<br>
</code></pre>

Now further down the grammar.

<pre><code>
<br>
<b>Expansion</b><br>
  ( "LOOKAHEAD" "(" LocalLookahead() ")" )?<br>
  ( <code class="NavajoWhite">ExpansionUnit() )+</code><br>
<br>
<b>ExpansionUnit</b><br>
    "LOOKAHEAD" "(" LocalLookahead() ")"<br>
  | Block()<br>
  | "[" <code class="NavajoWhite">ExpansionChoices()</code> "]"<br>
  | ExpansionUnitTCF()<br>
  | [ PrimaryExpression() "=" ]<br>
    ( <code class="GreenYellow">IdentifierAsString()</code> Arguments()
 <code class="green">// the name of a JavacodeProduction or a BNFProduction</code><br>
    | <code class="GreenYellow">RegularExpression()</code> [ "." < IDENTIFIER > ] )
 <code class="green">// any of the 3 ways for a token</code><br>
  | "(" <code class="NavajoWhite">ExpansionChoices()</code> ")" ( "+" | "*" | "?" )?<br>
<br>
<b>ExpansionUnitTCF</b><br>
  "try" "{" <code class="NavajoWhite">ExpansionChoices()</code> "}"<br>
  ( catch" "(" [ "final" ] Name() < IDENTIFIER > ")" Block() )*<br>
  [ "finally" Block() ]<br>
</code></pre>

A few words on *ExpansionUnit*:<br>
- the case `"LOOKAHEAD" "(" LocalLookahead() ")"` looks to be an artifice to avoid an error when a
 *LOOKAHEAD* appears at a non choice location (a *LOOKAHEAD* at a choice location is handled by the clause in
  the *Expansion*): this enables JavaCC to not fail in error in this case (it outputs a warning instead) and
  continue parsing<br>
- the case `Block()` (holding *lexical actions*) has some impact on the place of node generation<br>
- the case `"[" ExpansionChoices() "]"` is called EU type 2 / bracketed EU<br>
- the case `ExpansionUnitTCF()` is called EU type 3 / EuTCF, and adds specific processing<br>
- the sub case `IdentifierAsString() Arguments()` is called EU type 4a / IdentifierAsString
   EU, and refers to a call to a Javacode production or a BNF production<br>
- the sub case `RegularExpression() [ "." < IDENTIFIER > ]` is called EU type 4b /
   RegularExpression EU, and refers to a JavaCC token<br>
- the case `"(" ExpansionChoices() ")" ( "+" | "*" | "?" )?` is called EU type 5 / 
   parenthesized EU<br>

About *ExpansionUnitTCF*: this case has been extracted (in JTB comparing to JavaCC) as a separate production
 for cosmetic reasons (to shorten the generating methods) (the cases 2, 4 - 4a/4b & 5 could have also been
 separated).

Further down the grammar.

<pre><code>
<b>RegularExprProduction</b><br>
  [ "<" "*" ">" | "<" < IDENTIFIER > ( "," < IDENTIFIER > )* ">" ] <code class="green">// lexical states list</code><br>
  RegExprKind() [ "[" "IGNORE_CASE" "]" ] ":" <code class="green">// TOKEN, SKIP, MORE, ...</code><br>
  "{"<br>
  RegExprSpec()<br>
  ( "|" RegExprSpec() )*<br>
  "}"<br>
<br>
<b>RegExprSpec</b><br>
  RegularExpression()<br>
  [ Block() ]<br>
  [ ":" < IDENTIFIER > ] <code class="green">// lexical state to switch to</code><br>
<br>
<b>RegularExpression</b><br>
    StringLiteral() <code class="green">// like "abc"</code><br>
  | "<" [ [ "#" ] IdentifierAsString() ":" ] ComplexRegularExpressionChoices() ">"
 <code class="green">// like &lt; ID : "a" | "b" &gt;</code><br>
  | "<" IdentifierAsString() ">" <code class="green">// like &lt; NUMBER &gt;</code><br>
  | "<" "EOF" ">"<br>
</code></pre>

We see that we have recursive cycles of *ExpansionChoices* -> *Expansion*(s) -> *ExpansionUnit*(s)
 [ -> *ExpansionUnitTCF*(s) ] -> *ExpansionChoices*(s) -> ... which end on a call to a *BNFProduction* or
 a *RegularExpression*.<br>
And that an *ExpansionChoices* may have no choice and so reduce to single *Expansion*.<br>
And that an *Expansion* may be a list of only one *ExpansionUnit* and so reduce to single *ExpansionUnit*.<br>
So even if a *BNProduction* reduces to single *ExpansionUnit*, it is handled by JTB as a chain of
 *ExpansionChoices* (with no choices) -> *Expansion* (with no *LOOKAHEAD* and a single *ExpansionUnit*) -> *ExpansionUnit*.

Note also that an *Expansion* can have no element of type 2, 3, 4 and 5 (no inner *ExpansionChoices* nor
 *BNFProduction* nor *RegularExpression*), but 0 or 1 *Lookahead* and 1 or more *Block*. This will impact on
 node creation.<br>


## JTB base nodes, user nodes, leaf nodes, inner nodes and nullable nodes

A specific concept of JTB is to build nodes - as long as it is meaningful - for the technical constructs
<table>
<colgroup><col width="160px"><col width="150px"><col width="120px"></colgroup>
<tr>
<th>Node</th><th>Grammar</th><th>Generated class</th>
</tr>
<tr>
<td>a *nodes choice*</td><td>`A | B`</td>
<td><b><code>[NodeChoice](../../target/generated-tests/jtb/grammars/b/syntaxtree/NodeChoice.java)</code></b></td>
</tr>
<tr>
<td>an *optional node*</td><td>`[ A ]` and `( A )?`</td>
<td><b><code>[NodeOptional](../../target/generated-tests/jtb/grammars/b/syntaxtree/NodeOptional.java)</code></b></td>
</tr>
<tr>
<td>a *nodes sequence*</td><td>`A B` and `( A )`</td>
<td><b><code>[NodeSequence](../../target/generated-tests/jtb/grammars/b/syntaxtree/NodeSequence.java)</code></b></td>
</tr>
<tr>
<td>a *list of nodes*</td><td>`( A )+`</td>
<td><b><code>[NodeList](../../target/generated-tests/jtb/grammars/b/syntaxtree/NodeList.java)</code></b></td>
</tr>
<tr>
<td>an *optional list of nodes*</td><td>`( A )&ast;`</td>
<td><b><code>[NodeListOptional](../../target/generated-tests/jtb/grammars/b/syntaxtree/NodeListOptional.java)</code></b></td>
</tr>
<tr>
</table>

and that will intertwin with nodes that derive from:
<table>
<colgroup><col width="160px"><col width="150px"><col width="120px"></colgroup>
<tr>
<th>Node</th><th>Grammar</th><th>Generated class</th>
</tr>
<tr>
<td>a *BNFProduction*</td><td>`bp()`</td><td><b><code>bp</code></b></td>
</tr>
<tr>
<td>a *token node*</td><td>`"abc"` and `< ABC >`</td>
<td><b><code>[NodeToken](../../target/generated-tests/jtb/grammars/b/syntaxtree/NodeToken.java)</code></b></td>
</tr>
</table>

Note that an *ExpansionUnit type 5 with no modifier* `( A )` gives a node of the inner node type
 (here `A`) which can be another base node or a user node. If `A` is not a *NodeSequence* nor a
 *NodeChoice*, then the parentheses are superfluous. 

*BNFProductions* nodes will be handled by different generated classes, and are called **user** nodes.<br>
*Nodes choice*, *optional node*, *nodes sequence*, *list of nodes*, *optional list of nodes* and *token nodes*
 are called **base** nodes and are handled by the corresponding generic classes.

*User* nodes (*BNFProductions*) and *token nodes* (*RegularExpressions* are the **leaf** nodes of the built
 tree, and the other nodes of the tree (called **internal** nodes) are of *nodes choice*, *optional node*,
 *nodes sequence*, *list of nodes* and *optional list of nodes*.

At the *syntaxtree* classes level, you can have one or more nodes; in that last case JTB does not generate a
 *NodeSequence*, but directly as many class fields as nodes, and they may be leaf nodes or inner nodes.

When a node is not under (the scope of) an *ExpansionUnitTCF*, it always exist, it is always non null (as if
 an exception is thrown the whole parsing will fail and the tree will not extend further the previous node).
 
But any node under an *ExpansionUnitTCF* can potentially not be built, as when an exception is thrown and
 caught (by the `catch(...)` part) before successful parsing of the corresponding construct.<br>
So everwhere under an *ExpansionUnitTCF* (even many expansion levels under) any inner branch can fully exist
(if no exception is thrown), partially exist (if an exception is thrown in the middle of the branch building),
 or not exist (if the exception is thrown before any node of the branch is built), and that depends on the
 (parsing of the) actual data. We call these nodes **nullable** nodes.<br>
Note that having nested *ExpansionUnitTCFs* does not change much things: an inner *ExpansionUnitTCF* branch
 can totally exist, partially exist or not exist depending on which `catch(...)` handles the exception.

## How to declare nodes to be built or not?

### Default behavior

By default, i.e. without any specific notation (= with pure JavaCC syntax as above):
- all occurrences marked above in <code class="GreenYellow">GreenYellow</code> produce each a **leaf**
 node: tokens and calls to *BNFProductions* in *ExpansionUnit* each give a node, but calls to
 *JavacodeProductions* do not give nodes (this last rule is more a design choice than the result of a
 convincing logic)
- all occurrences marked above in <code class="NavajoWhite">NavajoWhite</code> produce each an
 **internal** node

### JTB Custom behavior

JTB allows to add indicators to customize which nodes are built or not. So the JTB grammar can  be slightly
 different from the JavaCC grammar, but do not panic, JTB removes (comments) these indicators when outputting
 the `.jj` file, which is compiled by JavaCC.<br>


#### Build JavacodeProduction nodes

By adding a <code class="PaleTurquoise">`%`</code> indicator to the *JavacodeProduction*
 definition (before the *Block*), you tell JTB to generate a user node everywhere an *ExpansionUnit type 4a*
 (IdentifierAsString()) refers to it.<br>
Note that the generated user class will have no field, so in essence will be of poor use; you will likely
 have to overwrite this (derived) class with your custom class. (Note: we plan to add in the future the
 ability to specify the fields the generated class must have, like `% NodeToken f0; NodeToken msg; %`).<br>
This can be useful to build tools that are tolerant to the grammar errors (a code editor for example), or to
 build tools that chosse to enrich a parsed file directly in the tree.<br>

<pre><code>
<b>JavaCodeProduction</b><br>
  "JAVACODE"<br>
  AccessModifier()<br>
  ResultType()<br>
  IdentifierAsString() <code class="green">// the name of the JavaCodeProduction, used in other productions</code><br>
  FormalParameters()<br>
  [ "throws" Name() ( "," Name() )* ]<br>
  <code class="PaleTurquoise">[ "%" ]</code><br>
  Block()<br>
</code></pre>

<span class="bold-red">Example</span> (assuming the generated class is overwritten with 2 fields
 <code>f0</code> & <code>msg</code> of type <code>NodeToken</code>)

<pre><code>
JAVACODE void skipButBuild() <code class="PaleTurquoise">%</code> {<br>
  Token tk = getNextToken(); <code class="green">// eat a token</code><br>
  f0 = new NodeToken(12, tk); <code class="green">// memorise it</code><br>
  msg = new NodeToken(87, "extra token eated"); <code class="green">// with some message</code><br>
}<br>
</code></pre>

#### Do not build all occurrences of some leaf nodes

This feature can be useful to build tools that focus on a small part of the grammar and therefore do not
 need full trees.

By adding a <code class="Plum">`!`</code> indicator to the *BNFProduction*
 definition (before the *:*), you tell JTB to not generate a user node everywhere an *ExpansionUnit type 4a*
 (IdentifierAsString()) refers to it.<br>

<pre><code>
<b>BNFProduction</b><br>
  AccessModifier()<br>
  ResultType()<br>
  IdentifierAsString() <code class="green">// the name of the BNFProduction, used in other productions</code><br>
  FormalParameters()<br>
  [ "throws" Name() ( "," Name() )* ]<br>
  <code class="Plum">[ "!" ]</code><br>
  ":"<br>
  Block()<br>
  "{"<br>
  ExpansionChoices()<br>
  "}"<br>
</code></pre>

<span class="bold-red">Example</span>

<pre><code>
<code class="green">// no need to process these modifiers in my tool</code><br>
void inOutClause() <code class="Plum">"!"</code> : {<br>
  "IN" | "OUT" | "INOUT"<br>
}<br>
</code></pre>

Similarly, by adding a <code class="Plum">`!`</code> indicator to the *RegExprSpec*
 definition (after the *RegularExpression*), you tell JTB to not generate a leaf node everywhere an
 *ExpansionUnit type 4b* (RegularExpression()) refers to it.<br>

<pre><code>
<b>RegExprSpec</b><br>
  RegularExpression()<br>
  <code class="Plum">[ "!" ]</code><br>
  [ Block() ]<br>
  [ ":" < IDENTIFIER > ] <code class="green">// lexical state to switch to</code><br>
</code></pre>

<span class="bold-red">Example</span>

<pre><code>
<code class="green">// no need to process some wierd tokens in my tool</code><br>
TOKEN : {<br>
  < ES : "\u00e9\u00e8\u00ea" > <code class="Plum">"!"</code><br>
| < #SYN_ESC : "\u0016\u001b" > <code class="Plum">"!"</code><br>
| < ID : ([ "a"-"z", "A"-"Z" ])([ "a"-"z", "A"-"Z", "0"-"9" ])* ><br>
}<br>
</code></pre>

#### Do not build some occurrences of some leaf nodes

You may want to not build only some occurrences of some leaf nodes and still build the others. For this just
 add the indicator where (after) the construct appears, that is in an *ExpansionUnit type 4a* or *4b*.

<pre><code>
<b>ExpansionUnit</b><br>
    "LOOKAHEAD" "(" LocalLookahead() ")"<br>
  | Block()<br>
  | "[" ExpansionChoices() "]"<br>
  | ExpansionUnitTCF()<br>
  | [ PrimaryExpression() "=" ]<br>
    ( IdentifierAsString()</i> Arguments() <code class="Plum">"!"</code>
 <code class="green">// type 4a</code><br>
    | RegularExpression() [ "." < IDENTIFIER > ] <code class="Plum">"!"</code> )
 <code class="green">// type 4b</code><br>
  | "(" ExpansionChoices() ")" ( "+" | "*" | "?" )?<br>
</code></pre>

<span class="bold-red">Example</span>

<pre><code>
<code class="green">// keep only the < ID > and my_prod() nodes</code><br>
void less_nodes() :<br>
{}<br>
{<br>
  < A_BS_B > <code class="Plum">"!"</code><br>
  < ID ><br>
  prod_node()<br>
  prod_no_node() <code class="Plum">"!"</code><br>
}<br>
</code></pre>

Note that *case 4a* applies only to *BNFProduction*, not to *JavacodeProduction*. TODO check and complete.<br>
Note also that for the moment you cannot build some occurrences of a *JavacodeProduction* in that *case 4a*
 (by adding the <code class="PaleTurquoise">"%"</code> indicator). TODO check and complete.

#### Building different trees on the same grammar

For the moment, if you want to build different tools with JTB, you are likely to use one grammar file and
 create the full tree, or at least the smallest tree that matches all your tools needs, and subclass the
 default visitors for each of your tools.

You can of course duplicate the grammar file and modify the indicators in the grammar files to produce
 different trees for your different tools, at the expense of more maintenance work to keep all the grammars
 up-to-date.

We think to add in the future the ability to specify the build nodes indicators outside the (main) JTB file
 and therefore enable generating different trees with a single JavaCC grammar.

#### What happens to the base nodes when sub nodes are not built? Null nodes and empty nodes

When a leaf node is added (for a *JavacodeProduction*), things are the same as if it was a *BNFProdution*,
 the *ExpansionUnit* counts for the upper inner node.

When a leaf node is not built, similarly the *ExpansionUnit* does not count for the upper inner node.

And JTB tries to avoid as much as possible at creating 'permanent' **null** nodes in an inner node (nodes that
 should never exist, which is different from the **nullable** nodes under an *ExpansionUnitTCF**.<br>
When a node is not built, you can conceptually think of it as an **empty** node, and if it must stay (in a
 `NodeChoice` it becomes a **null** node (with a real `null` reference), otherwise it disappears, but the
 construct above is impacted, and may change to another **base** node type, or even become **empty**, and this
 can propagate up to the top (the root), in which case there will no root so no tree.<br>
This happens in parallel of the specific processing for an *ExpansionUnitTCF*.

Let's say `B` and `A` give a node and `N` and `M` do not give a node:
- `B | A` gives a `NodeChoice` with `B` or `A`
- `B | N` gives a `NodeChoice` with `B` or a `null` reference (a *null* node)
- `M | N` gives an *empty* node which will impact the upper inner node
- `B A` gives a `NodeSequence` with `B` then `A`
- `B N` gives a node `B`
- `B N A` gives a `NodeSequence` with `B` then `A`
- `M N` gives a *empty* node which will impact the upper inner node
- `[B]` and `(B)?` give a `NodeOptional` with `B`
- `[N]` and `(N)?` give a *empty* node which will impact the upper inner node
- `(B)+` give a `NodeList` with `B`
- `(N)+` give a *empty* node which will impact the upper inner node
- `(B)*` give a `NodeListOptional` with `B`
- `(N)*` give a *empty* node which will impact the upper inner node

If we are in an *ExpansionUnitTCF*, things are the same, just remember that besides this nodes `B` and `A`
 are **nullable**, i.e. can be `null`.

#### What happens to the base nodes when an Expansion is made of only Java blocks?

As noted above, an *Expansion* can have no element of type 2, 3, 4 and 5 (no inner *ExpansionChoices* nor
 *BNFProduction* nor *RegularExpression*), but 0 or 1 *Lookahead* and 1 or more *Block*.<br>
In that case, we are with an **empty** node, like above, this propagates up.

Let's say `B` gives a node, `N` does not give a node, `{}` is a *Block*, and `LH` is a *Lookahead*:
- `B | {}` gives a `NodeChoice` with `B` or a `null` reference (a *null* node)
- `{} | B` gives a `NodeChoice` with a `null` reference (a *null* node) or `B`, but as JavaCC will always
 match the 0-length token the `null` reference will always be the case (note that we chosed not to 
 manage this as an *empty* node in order to not increase the algorithms complexity)
- `LH {} | B` gives a `NodeChoice` with a `null` reference (a *null* node) if `LH` is true, or `B`
 otherwise
- `N | {}` gives an *empty* node which will impact the upper inner node
- `{} | N` gives an *empty* node which will impact the upper inner node
- `LH {} | N` gives an *empty* node which will impact the upper inner node
- `B {}` and `{} B` give a node `B`
- `N {}` and `{} N` give an *empty* node which will impact the upper inner node
- `[{}]` and `({})?` give a *empty* node which will impact the upper inner node
- `({})+` give a *empty* node which will impact the upper inner node
- `({})*` give a *empty* node which will impact the upper inner node

## How JTB creates the syntaxtree classes?

A *BNFProduction* which is declared not to produce a node does not lead to a *syntaxtree* class.

A *BNFProduction* which produces a node becomes a method that returns an instance of the *syntaxtree* class.

This class implements an `INode` interface that declares the different `accept()` methods for the different
 visitors. These `accept()` methods take as arguments the visitor and the visit arguments if they have been
 requested, and just call back the `visit()` methods on the visitors with the given arguments.

This *syntaxtree* node class will have fields (aka **child**nodes) that are the first level inner nodes
 (the class instance is considered as the root of the tree).<br>
These fields are usually named `f0`, `f1`...`fn`, and are of the appropriate base node or leaf node type.

<span class="bold-red">Example bp_v</span>

**grammar.jtb**

<pre><code>
<code class="green">// bnf production returning void</code><br>
void bp_v() :<br>
{}<br>
{ < ID > }<br>
</code></pre>

**bp_v.java**

<pre><code>
<code class="green">// Node class</code><br>
public class bp_v implements INode {<br>
  <code class="green">// First field, corresponding to < ID > */</code><br>
  public NodeToken f0;<br>
  <code class="green">// constructor</code><br>
  public bp_v(final NodeToken n0) {<br>
    f0 = n0;<br>
  }<br>
  <code class="green">// Accepts a void visitor with no arguments</code><br>
  @Override<br>
  public void accept(final IVoidVisitor vis) {<br>
    vis.visit(this);<br>
  }<br>
</code></pre>

## How JTB annotates the JJ file for building the nodes?

### Nodes types and BNFProduction return type

A *BNFProduction* which is declared not to produce a node will not be annotated by JTB, i.e. its code stays
 the same, and the corresponding method returns the declared grammar type.

But a *BNFProduction* which produces a node becomes a method that returns an instance of the *syntaxtree*
 class.<br>
So JTB transforms the *BNFProduction* code to return a different type than the declared grammar type.

When the grammar return type is `void`, JTB:
- at the beginning of the method, adds a variable named `jtbNode` of the node type
- at the end of the method, creates the node (with its appropriate fields) and assigns it to the node
 variable
- and finally returns the node created.

<span class="bold-red">Example bp_v</span> (void return type)

**grammar.jj**

<pre><code>
<code class="green">// changed bnf production return type</code><br>
bp_v bp_v() :<br>
{<br>
  ...<br>
  <code class="green">// added variable declaration returning the node</code><br>
  bp_v jtbNode = null;<br>
}<br>
{<br>
  ...<br>
  <code class="green">// added node creation & assignment to the node variable</code><br>
  { jtbNode = new bp_v(n0); }<br>
  <code class="green">// added return statement</code><br>
  { return jtbNode; }<br>
}<br>
</code></pre>

When the grammar return type is not `void`, JTB:
- at the beginning of the class, adds a global member variable of the grammar return type, whose name is this
 grammar return type prefixed by `jtbrt_`
- at the beginning of the method, adds a variable named `jtbNode` of the node type
- anywhere in the method, changes the `return var;` statement by an assignment to the member variable,
 surrounded by save / restore statements for this member variable in a local variable, to manage recursion
 (remember that we can have cycles)
- anywhere in the `.jj` file, changes the calls to the method to a reference to the member variable
- at the end of the method, creates the node (with its appropriate fields) and assigns it to the node
 variable
- and finally returns the node created.

<span class="bold-red">Example bp_i / bp_i2</span> (int return type)

**grammar.jtb**

<pre><code>
<code class="green">// bnf production returning an int</code><br>
int bp_i() :<br>
{ Token tk = null; }<br>
{<br>
  tk = < ID ><br>
  { return tk.image.length(); }<br>
}<br>
<br>
<code class="green">// bnf production using the previous one</code><br>
void bp_i2() :<br>
{ int i = 0; }<br>
{<br>
  < ID ><br>
  i = bp_i()<br>
}<br>
</code></pre>

**grammar.jj**

<pre><code>
<code class="green">// added global return variable declaration of the grammar return type</code><br>
int jtbrt_bp_i;<br>
...<br>
<code class="green">// changed bnf production return type</code><br>
bp_i bp_i() :<br>
{<br>
  ...<br>
  <code class="green">// added variable returning the node</code><br>
  bp_i jtbNode = null;<br>
  ...<br>
}<br>
{<br>
  ...<br>
  <code class="green">// changed return statement into an assignment to the global variable</code><br>
  { jtbrt_bp_i = tk.image.length(); }<br>
  <code class="green">// added node creation & assignment to the node variable</code><br>
  { jtbNode = new bp_i(n0); }<br>
  <code class="green">// added return statement</code><br>
  { return jtbNode; }<br>
}<br>
<br>
<code class="green">// changed bnf production return type</code><br>
bp_i2 bp_i2() :<br>
{<br>
  ...<br>
  <code class="green">// added variable returning the node</code><br>
  bp_i2 jtbNode = null;<br>
  ...<br>
}<br>
{<br>
  ...<br>
  <code class="green">// added node creation & assignment to the node variable</code><br>
  { jtbNode = new bp_i2(n0); }<br>
  <code class="green">// added return statement</code><br>
  { return jtbNode; }<br>
}<br>
</code></pre>

### Node fields and sub-nodes

For the node fields creation and the tree building, in the *BNFProduction* code, JTB:
- at the beginning of the method, adds the fields and variables for each sub-node with their corresponding
 types
- once a field or a sub-node "available" (i.e. the corresponding JavaCC constructs have been fully
 parsed without error), creates it and links it to its parent (for sub-nodes)
- splits all assignments of a *BNFProduction* or a *RegularExpression* into assignments to the
 corresponding field or sub-node, and assigments of this later to the initial variable (see example bp_i)

<span class="bold-red">Example bp_v</span>

**grammar.jj**

<pre><code>
<code class="green">// changed bnf production return type<br></code>
bp_v bp_v() :<br>
{<br>
  <code class="green">// aded variable for the field<br></code>
  NodeToken n0 = null;<br>
  <code class="green">// added variable for the JavaCC token giving the JTB NodeToken<br></code>
  Token n1 = null;<br>
  <code class="green">// added variable returning the node<br></code>
  bp_v jtbNode = null;<br>
}<br>
{<br>
  <code class="green">// splitted JavaCC token variable assignment, first part<br></code>
  n1 = < ID ><br>
  <code class="green">// added JTB NodeToken creation (here a single cast due to how is defined
 the JavaCC class Token in JTB)<br></code>
  { n0 = (NodeToken) n1; }<br>
  <code class="green">// splitted JavaCC token variable assignment, second part<br></code>
  { tk = n1; }<br>
  <code class="green">// changed return statement into an assignment to the global variable<br></code>
  { jtbrt_bp_i = tk.image.length(); }<br>
  <code class="green">// added node creation & assignment to the node variable<br></code>
  { jtbNode = new bp_v(n0); }<br>
  <code class="green">// added return statement<br></code>
  { return jtbNode; }<br>
}<br>
</code></pre>

<span class="bold-red">Example bp_i / bp_i2</span> (int return type)

**grammar.jj**

<pre><code>
<code class="green">// added global return variable declaration of the grammar return type<br></code>
int jtbrt_bp_i;<br>
...<br>
<code class="green">// changed bnf production return type<br></code>
bp_i bp_i() :<br>
{<br>
  <code class="green">// added variable for the field<br></code>
  NodeToken n0 = null;<br>
  <code class="green">// variable for the JavaCC token giving the JTB NodeToken<br></code>
  Token n1 = null;<br>
  <code class="green">// added variable returning the node<br></code>
  bp_i jtbNode = null;<br>
  <code class="green">// user variable unchanged<br></code>
  Token tk = null;<br>
}<br>
{<br>
  <code class="green">// splitted JavaCC token variable assignment, first part<br></code>
  n1 = < ID ><br>
  <code class="green">// added JTB NodeToken creation (here a single cast due to how is defined
 the JavaCC class Token in JTB)<br></code>
  { n0 = (NodeToken) n1; }<br>
  <code class="green">// splitted JavaCC token variable assignment, second part<br></code>
  { tk = n1; }<br>
  <code class="green">// changed return statement into initial return variable assignment<br></code>
  { jtbrt_bp_i = tk.image.length(); }<br>
  <code class="green">// added node creation & assignment to the node variable<br></code>
  { jtbNode = new bp_i(n0); }<br>
  <code class="green">// added return statement<br></code>
  { return jtbNode; }<br>
}<br>
<br>
<code class="green">// changed bnf production return type<br></code>
bp_i2 bp_i2() :<br>
{<br>
  <code class="green">// added variable for the field<br></code>
  bp_i n0 = null;<br>
  <code class="green">// added variable returning the node<br></code>
  bp_i2 jtbNode = null;<br>
  <code class="green">// user variable unchanged<br></code>
  int i = 0;<br>
}<br>
{<br>
  <code class="green">// added save of the returning variable (for recursion)<br></code>
  { int oldJtbrt_bp_i_1 = jtbrt_bp_i; }<br>
  <code class="green">// splitted JavaCC BNFProduction variable assignment; first part<br></code>
  n0 = bp_i()<br>
  <code class="green">// splitted JavaCC BNFProduction variable assignment; second part, getting
 the result through the global variable<br></code>
  { i = jtbrt_bp_i; }<br>
  <code class="green">// added restore of the returning variable<br></code>
  { jtbrt_bp_i = oldJtbrt_bp_i_1; }<br>
  <code class="green">// added node creation & assignment to the node variable<br></code>
  { jtbNode = new bp_i2(n0); }<br>
  <code class="green">// added return statement<br></code>
  { return jtbNode; }<br>
}<br>
</code></pre>

JTB adds statements each within java block, but JavaCC removes the enclosing braces, so there are no scope
 issues.

## How are default visitors generated?

JTB allows to specify which (list of) visitors must be generated, and for each visitor its return type and
 its optional argument. See [How to use](How_to_use.html) [Visitors](How_to_use.html#visitors).

JTB generates default visitors with the code that fully walks trough the tree, doing nothing else than this.<br>
These default visitors can be sub-classed to perform more limited tree traversals and / or any logic on part
 or all of the nodes. These default visitors avoid typing the tree traversal code.

We have seen above that in generating the node classes JTB generates `accept()` methods on visitors that
 simply call the `visit()` methods of the visitors.

In a symetrical way, in the visitors JTB generates the `visit()` methods on the nodes that call the
 `accept()` methods on all of their fields (their **child** nodes), except on **node tokens*, where the
  visit will stop (as there will be no child), and the default method does nothing.

For **inner** nodes, the user can ask JTB to **inline** or not the `accept()` call, that is to generate the
 traversal of the inner node a level down. We found that it is very convenient to code the logic at the
 caller level; coding the logic at the called level often requires to test which parent called the node.
 This saves the user typing the **inner** nodes traversal code in his visitor sub-classes, he just has to
 copy / paste the code from the default visitor and to modify it.

<span class="bold-red">Example</span> (int return type)

**grammar.jtb**

<pre><code>
<code class="green">// BNFProduction with NodeToken, NodeOptional, NodeListOptional, NodeChoice and inner NodeSequence<br></code>
void bp_acc() :<br>
{}<br>
{<br>
  < ID ><br>
  [ "xyz" ]<br>
  ( bp_i() )*<br>
  ( bp_v() | ( bp_w() bp_x() ) )<br>
}<br>
</code></pre>

**DefpthFirstVoidVisitor.java** (not inlined)

<pre><code><code class="green">
  /**<br>
   * Visits a {@link bp_acc} node, whose children are the following :<br>
   * <br>
   * f0 -> < ID ><br>
   * f1 -> [ "xyz" ]<br>
   * f2 -> ( bp_i() )*<br>
   * f3 -> ( %0 bp_v()<br>
   * .. .. | %1 ( #0 bp_w() #1 bp_x() ) )<br>
   *<br>
   * @param n - the node to visit<br>
   */<br></code/
  @Override<br>
  public void visit(final bp_acc n) {<br>
    // f0 -> < ID ><br>
    <code class="green">// here the NodeToken will accept this visitor<br></code>
    n.f0.accept(this);<br>
    // f1 -> [ "xyz" ]<br>
    <code class="green">// here the NodeOptional will accept this visitor<br></code>
    n.f1.accept(this);<br>
    // f2 -> ( bp_i() )*<br>
    <code class="green">// here the NodeListOptional will accept this visitor<br></code>
    n.f2.accept(this);<br>
    // f3 -> ( %0 bp_v()<br>
    // .. .. | %1 ( #0 bp_w() #1 bp_x() ) )<br>
    <code class="green">// here the NodeChoice will accept this visitor<br></code>
    n.f3.accept(this);<br>
  }<br>
</code></pre>

**DefpthFirstVoidVisitor.java** (inlined)

<pre><code><code class="green">
  /**<br>
   * Visits a {@link bp_acc} node, whose children are the following :<br>
   * <br>
   * f0 -> < ID ><br>
   * f1 -> [ "xyz" ]<br>
   * f2 -> ( bp_i() )*<br>
   * f3 -> ( %0 bp_v()<br>
   * .. .. | %1 ( #0 bp_w() #1 bp_x() ) )<br>
   *<br>
   * @param n - the node to visit<br>
   */<br></code/
  @Override<br>
  public void visit(final bp_acc n) {<br>
    // f0 -> < ID ><br>
    <code class="green">// here the NodeToken will accept this visitor<br></code>
    final NodeToken n0 = n.f0;<br>
    n0.accept(this);<br>
    // f1 -> [ "xyz" ]<br>
    <code class="green">// here the NodeOptional is inlined: if present, whatever is under</code><br>
    <code class="green">// (here a NodeToken) will accept the visitor<br></code>
    final NodeOptional n1 = n.f1;<br>
    if (n1.present()) {<br>
      n1.accept(this);<br>
    }<br>
    // f2 -> ( bp_i() )*<br>
    <code class="green">// here the NodeListOptional is inlined: if present, all elements of the list,</code><br>
    <code class="green">// whatever they are (here BNFProductions), will accept the visitor<br></code>
    final NodeListOptional n2 = n.f2;<br>
    if (n2.present()) {<br>
      for (int i = 0; i < n2.size(); i++) {<br>
        final INode nloeai = n2.elementAt(i);<br>
        nloeai.accept(this);<br>
      }<br>
    }<br>
    // f3 -> ( %0 bp_v()<br>
    // .. .. | | %1 ( #0 bp_w() #1 bp_x() ) )<br>
    <code class="green">// here the NodeChoice is inlined: elements for each choice under</code><br>
    <code class="green">// (here a BNFProduction for case 0 and the 2 elements - BNFProductions -</code><br>
    <code class="green">// of the NodeSequence for case 1)
 will accept the visitor<br></code>
    final NodeChoice n3 = n.f3;<br>
    final NodeChoice nch = n3;<br>
    final INode ich = nch.choice;<br>
    switch (nch.which) {<br>
      case 0:<br>
        //%0 bp_v()<br>
        ich.accept(this);<br>
        break;<br>
      case 1:<br>
        //%1 ( #0 bp_w() #1 bp_x() )<br>
        <code class="green">// here the NodeSequence is also inlined</code><br>
        final NodeSequence seq = (NodeSequence) ich;<br>
        //#0 bp_w()<br>
        final INode nd = seq.elementAt(0);<br>
        nd.accept(this);<br>
        //#1 bp_x()<br>
        final INode nd1 = seq.elementAt(1);<br>
        nd1.accept(this);<br>
        break;<br>
      default:<br>
        // should not occur !!!<br>
        throw new ShouldNotOccurException(nch);<br>
    }<br>
  }<br>
</code></pre>

Note that JTB outputs a class javadoc comment describing the grammar and the associated fields, and java
 single line comments for each set of code lines for the fields and inlined nodes.


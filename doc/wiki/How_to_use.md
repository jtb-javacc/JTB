# Java Tree Builder (JTB) - How to use JTB

## Environment
JTB 1.5.0+ is meant to be used as a front end for JavaCC 5.0+, with a 1.8.0+ JDK, and so is command line by nature.  
Previous versions required a JDK1.7+.

 But it has been integrated within the JavaCC Eclipse Plugin, which embeds the latest JTB version each time
  the plugin is released, and avoids running the tool on the command line.

It also exists as a Maven artifact in Maven Central (`edu.purdue.cs.jtb`). 

## Run from the command line
to view all available options: 
- `java -jar jtb-1.x.y.jar -h`
- `java -cp jtb-1.x.y.jar[;<other_jar_or_dir>] EDU.purdue.jtb.JTB -h`

to parse **system.in** as the input file: 
- `java -jar jtb-1.x.x.jar [CL_Option] -si`
- `java -cp jtb-1.x.y.jar[;<other_jar_or_dir>] EDU.purdue.jtb.JTB[CL_Option] -si`

to parse **input-file** as the input file: 
- `java -jar jtb-1.x.y.jar [CL_Option] [input-file]`
- `java -cp jtb-1.x.y.jar[;<other_jar_or_dir>] EDU.purdue.jtb.JTB[CL_Option] [input-file]`

### Command line options
See the following table. Note that since 1.5.0 command line options still can take the same form as before (like `-chm`),
 but also can take the file form (like JavaCC: `-JTB_CHM=true`) (without spaces), and even a mixed form (`-chm=true`). 

### Input file options section
`options {(JTB_BOOL_OPT=(true|false); | JTB_STR_OPT="str";)*}`

Note that in the generated annotated **.jj** file these JTB options are removed but the JavaCC options are kept unchanged.

Note also that command line options do overwrite input file options since 1.5.0, as opposed to before, to follow JavaCC behavior. 
In other words, input file options are applied only when the corresponding command line options are not explicitly set. 
Therefore, if an option setting with the default value is mandatory for proper functioning of the generated parser
 (e.g. `STATIC=false;`), it is recommended to explicitly set it in the input file, and not set it in the command line setting
 (which can come from theJavaCC Eclipse plugin, an Ant or Maven script...). 

## Options are the following

 <table>
  <thead>
   <tr>
    <td><span style="color:blue">Command line</span></td>
    <td><span style="color:blue">File and command line</span></td>
    <td><span style="color:blue">Description</span></td>
    <td><span style="color:blue">Since</span></td>
    <td><span style="color:blue">Default</span></td>
   </tr>
  </thead>
  <tbody>
   <tr>
    <td>-chm</td>
    <td>JTB_CHM=(false|true)</td>
    <td>Generate nodes children methods</td>
    <td>1.5.0</td>
    <td>false</td>
   </tr>
   <tr>
    <td>-cl</td>
    <td>JTB_CL=(false|true)</td>
    <td>Print a list of the classes generated to standard out</td>
    <td>&nbsp;</td>
    <td>false</td>
   </tr>
   <tr>
    <td>-d dir</td>
    <td>JTB_D="dir"</td>
    <td>Short for (and overwrites) "-nd dir/syntaxtree -vd dir/visitor -hkd dir/hook"</td>
    <td>&nbsp;</td>
    <td>none</td>
   </tr>
   <tr>
    <td>-dl</td>
    <td>JTB_DL=(false|true)</td>
    <td>Generate depth level info</td>
    <td>&nbsp;</td>
    <td>false</td>
   </tr>
   <tr>
    <td>-do</td>
    <td>JTB_DO=(false|true)</td>
    <td>Print a list of resulting (file and command line) options to standard out</td>
    <td>1.5.0</td>
    <td>false</td>
   </tr>
   <tr>
    <td>-e</td>
    <td>JTB_E=(false|true)</td>
    <td>Suppress JTB semantic error checking</td>
    <td>&nbsp;</td>
    <td>false</td>
   </tr>
   <tr>
    <td>-eg class</td>
    <td>JTB_EG="class"</td>
    <td>Calls class (to be found in the classpath) to run a user supplied generator</td>
    <td>1.5.0</td>
    <td>none</td>
   </tr>
   <tr>
    <td>-f</td>
    <td>JTB_F=(false|true)</td>
    <td>Use descriptive node class field names</td>
    <td>&nbsp;</td>
    <td>false</td>
   </tr>
   <tr>
    <td>-h</td>
    <td>N/A</td>
    <td>Display this help message and quit</td>
    <td>&nbsp;</td>
    <td>&nbsp;</td>
   </tr>
   <tr>
    <td>-hk</td>
    <td>JTB_HK=(false|true)</td>
    <td>Generate node scope hook interface, default implementation and method calls</td>
    <td>1.5.0</td>
    <td>false</td>
   </tr>
   <tr>
    <td>-hkd</td>
    <td>JTB_HKD="dir"</td>
    <td>Use dir as the directory for the node scope hook interface and class</td>
    <td>1.5.0</td>
    <td>hook</td>
   </tr>
   <tr>
    <td>-hkp</td>
    <td>JTB_HKP="pkg"</td>
    <td>Use pkg as the package for the node scope hook interface and class</td>
    <td>1.5.0</td>
    <td>hook</td>
   </tr>
   <tr>
    <td>-ia</td>
    <td>JTB_IA=(false|true)</td>
    <td>Inline visitors accept methods in syntax tree classes</td>
    <td>&nbsp;</td>
    <td>false</td>
   </tr>
   <tr>
    <td>-jd</td>
    <td>JTB_JD=(false|true)</td>
    <td>Generate JavaDoc-friendly comments in the generated files</td>
    <td>&nbsp;</td>
    <td>false</td>
   </tr>
   <tr>
    <td>-nd dir</td>
    <td>JTB_ND="dir"</td>
    <td>Use dir as the directory for the syntax tree nodes</td>
    <td>&nbsp;</td>
    <td>syntaxtree</td>
   </tr>
   <tr>
    <td>-noplg</td>
    <td>JTB_NOPLG=(false|true)</td>
    <td>Do not parallelize user nodes classes generation</td>
    <td>1.5.0</td>
    <td>false</td>
   </tr>
   <tr>
    <td>-nosig</td>
    <td>JTB_NOSIG=(false|true)</td>
    <td>Do not generate signature annotations and classes</td>
    <td>1.5.0</td>
    <td>false</td>
   </tr>
   <tr>
    <td>-novis</td>
    <td>JTB_NOVIS=(false|true)</td>
    <td>Do not generate visitors interfaces and classes</td>
    <td>1.5.0</td>
    <td>false</td>
   </tr>
   <tr>
    <td>-np pkg</td>
    <td>JTB_NP="pkg"</td>
    <td>Use pkg as the package for the syntax tree nodes</td>
    <td>&nbsp;</td>
    <td>syntaxtree</td>
   </tr>
   <tr>
    <td>-npfx str</td>
    <td>JTB_NPFX="str"</td>
    <td>Use str as prefix for the syntax tree nodes</td>
    <td>&nbsp;</td>
    <td>none</td>
   </tr>
   <tr>
    <td>-ns class</td>
    <td>JTB_NS="class"</td>
    <td>Use class as the class which all node classes will extend</td>
    <td>&nbsp;</td>
    <td>none</td>
   </tr>
   <tr>
    <td>-nsfx str</td>
    <td>JTB_NSFX="str"</td>
    <td>Use str as suffix for the syntax tree nodes</td>
    <td>&nbsp;</td>
    <td>none</td>
   </tr>
   <tr>
    <td>-o file</td>
    <td>JTB_o="file"</td>
    <td>Use file as the filename for the annotated output grammar</td>
    <td>&nbsp;</td>
    <td>jtb.out.jj</td>
   </tr>
   <tr>
    <td>-p pkg</td>
    <td>JTB_P="pkg"</td>
    <td>Short for (and overwrites) "-np pkg.syntaxtree -vp pkg.visitor -hkp pkg.hook"</td>
    <td>&nbsp;</td>
    <td>none</td>
   </tr>
   <tr>
    <td>-pp</td>
    <td>JTB_PP=(false|true)</td>
    <td>Generate parent pointers in all node classes</td>
    <td>&nbsp;</td>
    <td>false</td>
   </tr>
   <tr>
    <td>-printer</td>
    <td>JTB_PRINTER=(false|true)</td>
    <td>Generate syntax tree dumping and formatter visitors</td>
    <td>&nbsp;</td>
    <td>false</td>
   </tr>
   <tr>
    <td>-si</td>
    <td>N/A</td>
    <td>Read from standard input rather than a file</td>
    <td>&nbsp;</td>
    <td>&nbsp;</td>
   </tr>
   <tr>
    <td>-tk</td>
    <td>JTB_TK=(false|true)</td>
    <td>Generate (store) special tokens in the tree's NodeTokens</td>
    <td>&nbsp;</td>
    <td>false</td>
   </tr>
   <tr>
    <td>-tkjj</td>
    <td>JTB_TKJJ=(false|true)</td>
    <td>Generate (print) special tokens in the annotated grammar (implies -tk)</td>
    <td>&nbsp;</td>
    <td>false</td>
   </tr>
   <tr>
    <td>-vd dir</td>
    <td>JTB_VD="dir"</td>
    <td>Use dir as the directory for the default visitor classes</td>
    <td>&nbsp;</td>
    <td>visitor</td>
   </tr>
   <tr>
    <td>-vis str</td>
    <td>JTB_VIS="str"</td>
    <td>Use str as the specification string for the visitor class(es)</td>
    <td>1.5.0</td>
    <td>Void,void,None;VoidArgu,void,A;Ret,R,None;RetArgu,R,A</td>
   </tr>
   <tr>
    <td>-vp pkg</td>
    <td>JTB_VP="pkg"</td>
    <td>Use pkg as the package for the default visitor classes</td>
    <td>&nbsp;</td>
    <td>visitor</td>
   </tr>
   <tr>
    <td>-w</td>
    <td>JTB_W=(false|true )</td>
    <td>Do not overwrite existing files</td>
    <td>&nbsp;</td>
    <td>false</td>
   </tr>
  </tbody>
 </table>

### Examples

Below will be referenced different examples, which are part of the JTB tests suite:

1. a small example named <a href="../../src/test/jtb/examples/java/ex1jtb/eg1.jtb"><code>eg1.jtb</code></a>,
 and its generated files under <a href="../../target/generated-tests/jtb/examples/java/ex1jtb"><code>ex1jtb</code></a>,
 similar to a JJTree example <a href="<https://github.com/javacc/javacc/blob/master/examples/JJTreeExamples/java/eg1.jjt>"><code>eg1.jjt</code></a>

- a small test grammar (using other options) called <a href="../../src/test/jtb/grammars/a/SmallGrammar.jtb"><code>SmallGrammar.jtb</code></a>,
 and its generated files under <a href="../../target/generated-tests/jtb/grammars/a/"><code>grammars/a</code></a>

- a bigger test grammar (using other options) called <a href="../../src/test/jtb/grammars/b/FullGrammar.jtb"><code>FullGrammar.jtb</code></a>,
 and its generated files under <a href="../../target/generated-tests/jtb/grammars/b/"><code>grammars/b</code></a>

- a specific test grammar for the external generator call called <a href="../../src/test/jtb/grammars/fm/EGTGrammar.jtb"><code>EGTGrammar.jtb</code></a>,
 and its generated files under <a href="../../target/generated-tests/jtb/grammars/fm/"><code>grammars/fm</code></a>

### Syntax tree nodes

Two types of **syntax tree nodes** are generated, in the <a href="../../target/generated-tests/jtb/examples/java/ex1jtb/syntaxtree/"><code>syntaxtree</code></a> directory:
1. one, called **user node**, for each
    + **BNFProduction**, unless it is tagged by the `!` indicator
    + **JavaCodeProduction**, only if it is tagged by the `%` indicator

- one, called **base node**, for each (unless it is tagged by the `!` indicator)
    + different JavaCC **expansion_choices** surroundings:
        - `[]` - <a href="../../target/generated-tests/jtb/examples/java/ex1jtb/syntaxtree/NodeChoice.java"><code>NodeChoice</code></a>
       - `()?` - <a href="../../target/generated-tests/jtb/examples/java/ex1jtb/syntaxtree/NodeOptional.java"><code>NodeOptional</code></a>
       - `()*` - <a href="../../target/generated-tests/jtb/examples/java/ex1jtb/syntaxtree/NodeListOptional.java"><code>NodeListOptional</code></a>
       - `()+` - <a href="../../target/generated-tests/jtb/examples/java/ex1jtb/syntaxtree/NodeList.java"><code>NodeList</code></a>
    + nodes sequence - <a href="../../target/generated-tests/jtb/examples/java/ex1jtb/syntaxtree/NodeSequence.java"><code>NodeSequence</code></a>
    + node reflecting a JavaCC Token - <a href="../../target/generated-tests/jtb/examples/java/ex1jtb/syntaxtree/NodeToken.java"><code>NodeToken</code></a>
    + node reflecting a **try-catch-finally** around an **expansion_unit** - `NodeTCF` TODO remove

Base nodes are invariant (i.e. they do not depend on the input grammar; they may vary over JTB's versions).

If no (JavaCC) `TOKEN_EXTENDS` option is set (in the grammar or in the command line arguments), JTB generates a `TOKEN_EXTENDS="<syntaxtree-package>.NodeToken";` option in the **.jj** grammar; if the user wants its own super class for the JavaCC **Token.java** class by putting a `TOKEN_EXTENDS="<user-Token-superclass>";` option in the JTB grammar, this superclass must extend the JTB **NodeToken.java** class.

User nodes are generated with as many (class) fields of (base or user) nodes as the nodes' children. See the 2 examples.

These fields are by default named `f0`, `f1`, ... `fn`, but can be named like their type (class name) through the `-f` option. See the **SmallGrammar.jtb** example.  

Syntax tree default sub directory and sub package (relative to the grammar / parser directory /package) are
 `syntaxtree` and can be changed by the `-np` and `-nd` options. See the **SmallGrammar.jtb** example.  

The nodes can be optionally prefixed `-npfx str` or suffixed `-nsfx str` by a string (usually to distinguish them better),
 and can optionally inherit from a super class `-ns class`. See the **SmallGrammar.jtb** example.  

By default JTB generates the syntax tree user nodes in parallel (through the JDK 1.8 stream mechanism;
 the `-noplg` option tells JTB to generate them in serial. See the **SmallGrammar.jtb** example.  

For examples using the the `!` and `%` indicators on **BNFProductions** and **JavaCodeProductions**, see **FullGrammar.jtb** example.  
Note that using the `!` and `%` indicators breaks the grammar compatibility with a pure JavaCC grammar.  
These features have been added to mimic corresponding JJTree features but should be used very seldom.  

### Visitors

Before 1.5.0 by default 4 visitor interfaces and 4 default implementations were generated. They covered the combination of
 with or without argument and with or without (void) return type.  

Since 1.5.0 the `-vis` / `JTB_VIS` option enables to customize the generated visitor(s): one can set:
- (part of) the class name
- the return type (or `void`)
- the optional arguments types

These types can be:
- primitive and non primitive types
- full qualified types
- even arrays and varargs
- parameter types (of one uppercase letter)

Syntax is: `"Namepart,(Ret|void),(Arg(,Arg)*)?(;Namepart,(Ret|void),(Arg(,Arg)*)?)*"`, meaning it is:
* a semicolon `;` separated list for the different visitors
* for each visitor a comma *,* separated list of 2 to n parts.

The class names will be: **<Prefix><Namepart>Visitor**, where **Prefix** will be:
- **I** for interfaces
- **DepthFirst** for classes

Ex: `"Void,void,None;Vis2,R,A,a.b.MyClass[],short..."` will create:
* `IVoidVisitor.java` & `DepthFirstVoidVistor.java` with a `void` return type and no arguments, and
* `IVis2Visitor.java` & `DepthFirstVis2Visitor.java` with parameter types, a `R` return type, and 3 arguments `A`, `a.b.MyClass[]` and `short...`

Visitors generation can be suppressed by the `-novis` option. 

Visitors default sub directory and sub package (relative to the grammar / parser directory / package) are `visitor`
 and can be changed by the `-vd` and `-vp` options. See the **SmallGrammar.jtb** example.  

Default implementation is of type **Depth first**, or **Child first**, which means that a node's children will be
 visited before the node's brothers, and they just walk all the tree doing nothing. 

Writing new visitors is easy as one has just to extend a default visitor and override the appropriate methods. 

The `-dl` option enables generating a field reflecting the depth level. See the **SmallGrammar.jtb** example.  

The `-ia` option enables generating visit methods with **inlined** code, that is the `accept()` call of each field
 of a production is replaced by complete piece of code that walks one level deeper in the grammar on the corresponding field. See the **FullGrammar.jtb** example.  

### Control signature

User nodes have each different children deriving from the grammar, and the default visitors generated code walks
 through these children in a non generic way (that is referencing fields 0, 1, ... n);  
usually the user visitors are coded by first copying the default visitors code and then modifying it,
 so they also access the children in a non generic way.
 
Therefore if one starts coding a visitor, and then modifies some productions definitions in the grammar,
 some user visitor code is likely to no more work on the new children: most of time a `ClassCastException` will occur.

So a mechanism inspired from the Java serialization version control has been added:
- for every user node a **int** hash signature value based on the node's fields is computed and generated
   in the javadoc methods comments and in the `NodeConstants.java` class
- on each visit method a `@NodeFieldsSignature` annotation is added, with an array of 3 **int** values:
    + the value itself of the signature value (named the **old** or **copied** signature value)
    + the variable (constant) of the signature value defined in `NodeConstants` (named the **new** or **generated** signature value)
    + the index of the node in `NodeConstants.java`

- in the signature subpackage of the visitor package, two files are generated:
 (see <a href="../../target/generated-tests/jtb/examples/java/ex1jtb/visitor/signature"><code>visitor/signature</code></a>):
    + a `ControlSignatureProcessor.java` annotation processor class file 
    + a `NodeFieldsSignature.java` annotation class file

To use this mechanism compile using the `ControlSignatureProcessor.java` annotation processor, in the JDK 6+ way:  
you will get compile errors on methods for which the **old** signature value in the **old** user visitor
 does not match any more the **new** generated signature value in the newly generated `NodeConstants.java` class.  

See targets `compile_ap_pkg`, `make_ap_jar` and `compile_visitors_with_ap` in <a href="../../build.xml"><code>build.xml</code></a> in JTB's source distribution,
 for an example on how to use the annotation processor within javac compilation:
- `compile_ap_pkg`: compile the annotation processor classes
- `make_ap_jar`: create a jar with the annotation processor classes and the services entry (see below)
- `compile_visitors_with_ap`: compile the visitors processing the annotations through the `-proc:only` and `-processorpath <path>` arguments

Do not forget to create / update the <a href="../../META-INF/services/javax.annotation.processing.Processor"><code>META-INF/services/javax.annotation.processing.Processor</code></a> file for your project,
 with your `<pkg>.visitor.signature.ControlSignatureProcessor` class name, or use the alternative javac options.  

The signature values are always generated when visitors are generated.  

Signature files and annotations generation can be suppressed by the `-nosig` option; once they are generated,
 they are not overwritten, so the user has to delete them if he needs JTB to regenerate them.  

### Special tokens and comments

In a JavaCC grammar some tokens are called special tokens and are recognized by the TokenManager and passed
 to the Parser, which just links them to the next non special tokens; usually this feature is used to handle comments.  

In JTB these special tokens can be stored in the parse tree's NodeToken nodes through the `-tk` option. See the **eg1.jtb** example.  
These special tokens can also be printed in the annotated **.jj** file through the `-tkjj` option. 
 
By default, JTB prints, in the syntaxtree nodes and in the visitors, user-friendly javadoc comments reminding the productions grammatical structures;
 this can be turned off through the `-jd` option. See the **SmallGrammar.jtb** example.  

### Parent pointer

The `-pp` option enables generating in the syntax tree nodes (see the **SmallGrammar.jtb** example):  
- references (set by additional generated methods in the annotated grammar file)
- methods for accessing the nodes' parent 

### Children methods

The `-chm` option enables generating, in the syntax tree nodes, methods for accessing generically the children
 (through lists and numbers, and for all children, base only or user only children). See the **SmallGrammar.jtb** example.  

### Node hooks

The `-hk` option enables generating (see the **SmallGrammar.jtb** example):
- in the annotated **.jj** grammar calls to **hook** methods upon entering or exiting a production
  (similar - but not exactly the same - to JJTree node scope hook)
  (that is at the beginning of a production, before anything is done,
   and after the node has been created / just before returning to the caller) 
- an interface and an empty implementation for the hook class and the methods

The directory and package for these files are by default `hook` and can be changed by the `-hkd` and `-hkp` options. 

### External generator method call

Setting a class name in the `-eg` class option and setting the **classpath** (for the directory or jarfile for
 the external classes and tools) enables JTB to call, through the Java Reflection API, this class's method
 `int generate(Map<String, Object>)`, passing it a **data model** map holding some JTB global information
  (see `JTB#callExternalGenerator(finalList<UserClassInfo> aClasses)`):
- the current JTB version
- a cloned map of the JTB grammar options
- two lists of base interfaces and classes (why not ?)
- the list of user classes
- a cloned map of the nodes that must not be created
- a cloned map of all the **BNFProductions** and the **JavaCodeProductions**
- a cloned map of the nodes with their number of sub-nodes to be created

This allows for example to use a template processor like **Freemarker** to generate different visitors...  

A small test class `EDU.purdue.jtb.freemarker.egt.ExternalGeneratorTester.java` dumping the data model
 and trying to configure a **Freemarker** environment is embedded in the JTB jar and can be used to test
 the generation, mainly the classpath setting. Note that the **Freemarker** jar file is not provided with JTB. 

Example: for a custom class `my.gen.generator.java` compiled into `<path-to-classes-directory>`:
- the JTB option should be `JTB_EG="my.gen.generator"` 
- the classpath should be `<path-to-freemarker-jar-file>;<path-to-classes-directory>` 

 See the **EGTGrammar.jtb** example. 

## Try ExpansionChoices / catch / finally

### In JavaCC

The JavaCC grammar allows a special **ExpansionUnit** called here **ExpansionUnitTCF** which includes an
 **ExpansionChoices** within a `try {...}`, followed by an optional list of `catch(VarDecl()) {Block()}`
  and an optional `finally {Block()}` (named here **TC*F?**).  
In the generated parser JavaCC will output these lines of code unchanged around those ones generated for the
 **ExpansionChoices**; usually they are used to take specific actions on a lexical or parse error.  

In the JTB tests suite there are 2 examples of this syntax (in production named `Tcf()`,
 in grammars named **TcfGrammar**, one in JTB and its equivalent in JJTree:

1. <a href="../../src/test/jtb/grammars/tcf/TcfGrammar.jtb"><code>TcfGrammar.jtb</code></a>,
 and its generated files under <a href="../../target/generated-tests/jtb/grammars/tcf"><code>jtb-tcf</code></a>,
 and  under <a href="../../target/generated-tests/jj/grammars/tcf"><code>jj-tcf</code></a>

- <a href="../../src/test/jjt/grammars/jjttcf/TcfGrammar.jjt"><code>TcfGrammar.jjt</code></a>,
 and its generated files under <a href="../../target/generated-tests/jj/grammars/jjttcf/ast_vis"><code>jjt-jjttcf</code></a>,
 and  under <a href="../../target/generated-tests/jj/grammars/jjttcf"><code>jj-jjttcf</code></a>

### In JJTree & JTB

You can use these **ExpansionUnitTCF** constructs in **JJTree** and **JTB**: they are kept in the annotated
 **.jj** file, which parses the input and builds along the corresponding abstract syntax tree (the **AST**).  
 
There is a **significant** difference between **JJTree** and **JTB** here: during parsing:
 - **JJTree** will create the nodes **inside the production**
    and construct all the sub-trees unless an uncaught *Exception* is encountered;
    so if an **Exception** is caught (and not rethrown) within a **TC*F?** the node corresponding to the expansion
    that raised the **Exception** will be created and inserted;
    to not create it one has to add the action `jjtree.popNode();` or rethrow the **Exception**
 - **JTB** will create the **user nodes** and **base nodes** **on returning from the production**
    and construct the sub-trees progressively,
    so if an **Exception** is caught within a **TC*F?** the **user node** corresponding to the expansion
    that raised the **Exception** will not be created (and therefore not inserted in the **base node** - and this
    later will most of the time be **null**)  

The key aspects of **JJTree** are its concept of **node scope** and its **bottom-up** building of the tree;
 **JTB**'s ones are its concept of **user** and **base nodes** and its **top-down** building of the tree.

Note that, if you want to change the default behaviors:
- in **JJTree**, you have to manipulate the constructed tree on the fly
- in **JTB**, you can surround by the **TC*F?** the minimum sequence of productions or tokens, and if this is
 not enough construct and add a node manually.

### In Visitors

But what about the visitors (the default generated ones or the user implementations)?  
**Should they contain the** `try / catch* / finally?` **or not?**  

**JJTree** does not generate them in the generated default visitor.

**JTB** 1.4.x generated them in the generated default visitors, considering that the user could need to catch
 exceptions while navigating in the tree.  
This has been removed in 1.5.0, considering the previous reason would occur very seldom, and that the user
 would only use a **TC*F?** for parse exception handling purposes and not for tree building or tree walking
 purposes, and would extend the default visitor with a custom one if he has specific needs.  


# Java Tree Builder (JTB) - Overview

## What is JTB

JTB (Java Tree Builder) is a syntax tree builder and visitors generator to be used in front of JavaCC (Java Compiler Compiler).

It takes a JavaCC grammar file as input (you just have to change the extension from **.jj** to **.jtb** and add a few options)
 and it automatically generates the following:
- a set of **syntax tree classes** based on the BNF and Javacode productions in the grammar (called **user nodes**)
- a set of **base tree** interfaces & classes (called **base nodes**) allowing to manage the tree classes links in the tree
   (**INode**, **INodeList**, **NodeChoice**, **NodeConstants** (since 1.5.0), **NodeList**, **NodeListOptional**,
   **NodeOptional**, **NodeSequence**, **NodeToken**)
- any number of **interfaces** allowing to use the **Visitor pattern** (e.g. **IVoidVisitor**, **IMyFancyVisitor**), 
- the corresponding **depth-first default visitors** (e.g. **DepthFirstVoidVisitor**, **DepthFirstMyFancyVisitor**),
   whose methods simply visit the children of the current node
- a JavaCC grammar **.jj** file (the famous **jtb.out.jj** by default), with the proper additional code lines
   to build the syntax tree during parsing (and which then must be compiled with JavaCC), 
- if requested (since 1.5.0):
    + a **node scope hook** interface **IEnterExitHook** and calls to 2 **node scope hook** methods
       **xxxxEnter** and **xxxxExit** of this interface upon entry and exit of each BNF production
       which lead to a JTB node creation
    + and an empty implementation of this interface **EmptyEnterExitHook** 

New visitors subclassing any generated one and overriding the default methods can perform various operations
 on and manipulate the generated syntax tree.<br>
So usually one implements a new visitor for each new need.

## JTB // JavaCC

JTB has its own options, and uses some of JavaCC options; it controls its own options and comments them in the
 generated annotated JavaCC grammar file.

JTB behaves like JavaCC 5.0 / 7.0.x in the sense that the grammar:
- produces a JavaCC grammar with Java parts, not a JavaCC grammar with other language parts
 (there has been some development of a C++ version which has not yet been merged)
- controls the syntax of the Java parts through a Java grammar

JTB 1.5.0 introduces a first step to generate its own classes through a template engine,
 but the whole chain of base templates has not yet been produced.

JTB generates, along with its own classes, an (annotated) JavaCC grammar, which should be compilable through any JavaCC version;
 it has been tested with JavaCC 4.2, 5.0 & 7.0.x; although not tested, it should work with JavaCC 8.

So the JTB grammar is exactly the JavaCC grammar (although it is a modified clone of some JavaCC grammar), except:
- a few more options,
- the possibility to add node creation indicators (like in JJTree you can customize node creation):
 but one working with JTB would very seldom find the need to customize the nodes creation (see below)

## JTB // JJTree

**JTB** can be compared to **JJTree** as following:
- JJTree was designed for allowing manipulation of the abstract syntax tree and the tokens on the fly,
   and the visitors partial support was added as a complement
<p>
- JTB fits with the ideas that, at least for big or reusable projects:
    + only one grammar file should be coded for a language, taking care of only the parsing,<br> 
      so this grammar file should reflect only the grammar and not be polluted by nodes and tree generation tweaks<br>
      (otherwise one is likely to have to code another grammar file for new needs:
        * an example is **pmd-java**, which has a **Java.jjt** grammar, tailored to PMD's needs,
       * another example is **javaparser**, which has a **java.jj** grammar, tailored to nothing more than just parsing:
       * the first one is not very much reusable for other needs - like JTB! -, the second one is much more reusable)
    + the abstract syntax tree built upon parsing should reflect the grammar, and if other trees are need
       for subsequent phases they should be built after the parsing tree has been built
        (but the need for other trees is thought not frequent)
    + the visitors pattern use should be the preferred way of coding analysis or generation logic,
       so one should be able to develop different visitors to handle these different aspects
<p>
- in JJTree you can access and work with the nodes but also with the tokens, whereas in JTB you only work with the nodes
<p>
- JJTree's multi mode corresponds to what JTB generates
<p>
- in JJTree you explicitly define the nodes you want to build; JTB builds by default all of them
 (but you can tell it not to build some of them)
<p>
- the children of a JJTree node are other JJTree nodes, but the children of a JTB user node are other user nodes
  or base nodes (NodeChoice, NodeList, NodeListOptional, NodeOptional, NodeSequence,NodeTCF, NodeToken),
  whose children are other JTB user or base nodes,
<p>
- JJTree's node scope hook is quite similar to JTB node scope hook
<p>
- JJTree generates only interfaces for visitors and you have to code the visitors, so you "fill" them;
   JTB generates interfaces and **full** visitors (i.e. with all the code to visit the tree),
   so you just "modify" them, sometimes just "reducing" them (for not visiting sub-branches),
   so in a sense it is faster to develop on an already generated - bullet proof - code,
<p>
- in JJTree, you are tempted to tune the tree, i.e. define only the nodes you need, but if you want to develop
   different visitors, you have to think about all the nodes you want to traverse for the different purposes
   and so you may find yourself sometimes having to enrich the grammar and defeat what you have previously done;<br>
   in JTB you have all the tree right from the beginning so adding a new visitor is not disruptive
<p>
- JTB can **inline** the **accept()** methods, i.e. it generates, for each node, the code to work directly on its children,
   which is usually the right place to take decisions and to reduce the control logic
   (otherwise you usually - in JJTree also - have to access the fathers and check their types);<br>
   this also limits the number of class casts one has to do
<p>
- JJTree allows to provide custom classes for the nodes (which must extend **SimpleNode**),
   for extending the default behavior node by node;<br>
   JTB allows only to define a user nodes' superclass, for extending the default behavior (for all nodes)
<p>
- So JJTree may be more adequate when only small trees or only one visitor or tokens modification are needed,<br>
  and JTB may be more adequate when you have to work on big trees and build many visitors.

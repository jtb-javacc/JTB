# Java Tree Builder (JTB) - Release notes (dd/mm/yy)

### Version 1.5.1 - jtb-1.5.1.jar of xx/08/2023, not yet committed on GitHub, included in the Eclipse JavaCC Plug-in v1.6.1
- fixed node choice generation issue
- changed package, grammar and out file names to be more aligned
- improved imports generation with different combinations of options
- improved build of no options grammars to align with Eclipse build
- added optional annotations in catch of ExpansionUnitTCF
- enriched test cases with no package grammars and more combinations of options

### Version 1.5.0 - jtb-1.5.0.jar of 03/05/2021, never committed on GitHub, included in the Eclipse JavaCC Plug-in v1.6.0
- updated docs (README.md, doc/Release_notes.md, doc/HowToBuild.md), added doc/wiki/*
- needs JDK 1.8 now; project in UTF-8; tested with JavaCC 7.0.10
- internal refactoring and code changes (removed some warnings and used try-with-resource, added Java nature,
   renamed unused classes, put back generated serialVersionUID to the JTB Version, reworked ClassInfo and
   added FieldInfo, replaced iterator loops by enhanced loops, renamed classes, restructured packages,
   used stderr and stdout, suppressed class FileExistsException, replaced deprecated calls)
- suppressed the JTBToolkit class generated in the parser's file but after the parser's class, normalized file names
- fixed grammar for optional TypeArguments in AllocationExpression
- added generation of the NodeConstants class
- added optional generation of node scope hook class, interface & method calls in BNF Productions
- added the do not generate visitors option (novis)
- rewrote the visitors generation, allowing the user to specify their names, return and argument types
- added generation of a control signature mechanism in classes and visit methods (for helping detecting at
   compile time changes impacting the visitors), and the do not generate signature annotations and classes option
- added optional generation of nodes children methods
- added optional custom classes generation through call to a method of an external class (e.g. class using FreeMarker
   templates)
- parallelized user nodes classes generation
- modified handling of special tokens (JTB_TK store special tokens in JTB nodes and JTB_TKJJ prints them)
- made JTB non static
- removed generation of NodeTCF (nodes for try ExpansionChoices / catch / finally) and TCF in visitors
- added a significant test suite with tests and examples grammars (compilation and run)
- enriched / changed build.xml targets (suppressWarnings, code coverage with JaCoCo)
- reworked processing of nodes not to be created
- created a data model for the FreeMarker template engine
- fixed an issue in method names generation (with prefix / suffix and node not to be generated)
- fixed an issue in array return types generation
- passed Expression as optional in semantic lookahead
- changed displayed paths in absolute, without ../..
- added a test with ("xxx"|{}) (issue #86)

### Version 1.4.13 - 12/01/2017
- fixed printing a local variable declaration with a modifier
- moved properties from build.xml to jtb.xml (dev)
- added options to generate correctly the grammar in Eclipse (dev)

### Version 1.4.12
- publish JTB as a Maven artifact to Maven Central

### Version 1.4.11 - xx/04/2016
- fixed (added) spaces between the '<' and '>' and the token labels in the generated javadoc comments
- fixed the conditions on displaying the "warning:  Empty choice : a NodeChoice with a 'null' choice member ..."
- fixed column numbers in messages

### Version 1.4.10 - 13/04/2015
- JTB#main() returns error codes

### Version 1.4.9 - ??/??/2015
- same as 1.4.8.fix_a

### Version 1.4.8.fix_a - ??/01/2015
- fixed regression (Annotator#bnfFinalActions())

### Version 1.4.8 - 30/12/2014
- removed warnings everywhere except in some big classes in parser
- removed the ordinal field in RegularExpression which was hiding the one in the superclass Expansion_
- renamed TreeWalkerOp in ITreeWalkerOp
- switched from implementing interfaces to import static for constants
- changed ### Versions tags from 4.2.j.m to 5.0
- improved printing specials
- fixed the ExplicitConstructorInvocation production
- fixed java blocs generation (Annotator visit LocalVariableDeclaration)
- modified the warning message on ignored LOOKAHEAD (as it can actually be not ignored)
- added short names generation for generated intermediate variables (default)
- fixed printing specials in generating JTB options
- added node_descriptor() and node_descriptor_expression() and modified JavaCodeProduction, BNFProduction,
   Expansion & MethodDeclaration to allow a node descriptor
- added @Override and @SuppressWarnings("unused") annotations in the generated visitors and base classes
- added ReferenceType() in ExplicitConstructorInvocation() in the grammar
- added AnnotationTypeDeclaration() in ClassOrInterfaceBodyDeclaration() in the grammar
- changed in NodeToken.java  public ArrayList<NodeToken> specialTokens; into public List<NodeToken> specialTokens;
- fixed prefix / suffix in lookahead
- added "%" syntax on javacode
- fixed bug 3164860
- added control that a javacode production is not declared more than once
- fixed problem "null choice for an empty choice"

### Version 1.4.7 - 25/09/2012
- added sub comments in the generated visitors java code
- did some optimizations
- fixed bugs JTB-1 & JTB-2
- fixed some generation errors
- added a check when 'n = prod()' and 'void prod()'
- added the '!' syntax for not generating a node creation (in a BNFProduction, RegExprSpec and ExpansionUnit)
- fixed some Java grammar issues and did some refactoring
- added column numbers in messages
- performed tests on more limit cases
- fixed missing <EOF> in JavaCCInput() and parentheses in Annotation()

### Version 1.4.6 - 25/01/2011
- added -va and -npfx and -nsfx options (Francis Andre)
- added java code formatter & clean up preference files

### Version 1.4.5 - 06/12/2010
- Convert nodes and visitors output directories to absolute paths

### Version 1.4.4 - 12/07/2010
- Fixed missing space after throws issue in visiting MethodDeclaration & ConstructorDeclaration of JavaPrinter
- Fixed index issue in visiting TypeArguments of JavaPrinter

### Version 1.4.3.2 - 26/04/2010
- Fixed index issue in visiting Expansion of AcceptInliner (bug #2991455)

### Version 1.4.3.1 - 23/04/2010
- Fixed output of AcceptInliner for -f option (bug #2989497)
- Fixed case 4 of getExpUnitBaseNodeType of CommentsPrinter (bug #2990962) and tuned number of dots

### Version 1.4.3 - 31/03/2010
- Added node declarations initialization in all cases
- Fixed output of else bug in Annotator
- Fixed output of constructor in TreeFormatter

### Version 1.4.2 - 25/02/2010
- Fixed missing "java.util package" in generated JTBToolkit class
- Fixed issue in visiting ExpansionChoices of AcceptInliner

### Version 1.4.1 - 17/02/2010
- Fixed unprocessed n.f0.which == 0 case in visit(ExpansionUnit) in SemanticChecker
- Fixed missing static modifier for JTB generated return variables in case static = true
   in Annotator & JTB & Globals

### Version 1.4.0.2 - 21/01/2010
- Fixed output of else bug in JavaPrinter

### Version 1.4.0.1 - 20/01/2010
- Fixed command line options overwrite bug

### Version 1.4.0 - 19/11/2009
- Fixed NPE bug in visitors generation when no -jd is set
- Added JTB options commenting in the output file
- Added parent directories creation

### Version 1.4.j.m.15 - 16/11/2009
- Back to JDK 1.5 (for use under the JavaCC Eclipse Plug-in)
- Changed to JavaCC 5.0 the JavaCC jar used to compile the jtb.out.jj file
- Added -d, -nd & -vd options, modified -p, -pd & -vp options (to separate directories from packages)

### Version 1.4.j.m.14 - 16/11/2009
- Copied and optimized visit(ExpansionChoices) and visit(Expansion) from
   DepthFirstVoidVisitor to ExpansionUnitTypeCounter
- Extracted ExpansionUnitInTCF production in grammar
- Modified visitors and generators accordingly
- Finished ExpansionUnitInTCF management in DepthFirstVisitorGenerator

### Version 1.4.j.m.13 - 25/08/2009 15:01
- Added part of ExpansionUnit type 3 management in visitors

### Version 1.4.j.m.12 - 24/08/2009 18.07
- Inlined accept methods in generated visitors with new class AcceptInliner
- Added -dl option for depth level management generation
- Fixed bug in CommentPrinter
- Fixed bug in Annotator

### Version 1.4.j.m.11 - 29/07/2009 18:21
- Fixed visit ReturnStatement in Annotator

### Version 1.4.j.m.10 - 29/07/2009 14:22
- Added which value in javadoc comments for productions with choices
- Removed arguments in javadoc comments for productions
- Print on an indented new line regular expressions in javadoc comments for productions
- Changed behavior of option -jd: now turns on printing classes, methods and fields javadoc comments
- Added NodeList and NodeListOptional list of nodes allocation increment algorithm
- Fixed parent pointer methods
- Added -ia option for inlining visitor accept methods on base nodes
- Added switch statement in inlining a NodeChoice
- Renamed CommentPrinter into CommentsPrinter and moved it from misc.ClassInfo to visitor
- Added fieldComments in ClassInfo and associated methods
- Enhanced field comments with indentation
- Added StringBuilders and javadoc in TreeFormatterGenerator and TreeDumperGenerator
- Changed \n into line.separator in all generation classes
- Updated all grammar comments in visitors

### Version 1.4.j.m.9 - 12/07/2009 20:48
- Removed "only non void JavaCodeProduction" limitation
- Fixed some errors
- Added proper handling of PrimaryExpression() = Identifier() ... | RegularExpression() ...
- Cleaned grammar with temporary modifications for generics, return types and pe() = id() | re()

### Version 1.4.j.m.8 - 10/07/2009 17:19
- Removed "only void BNFProduction result type" limitation
- Removed ModMMa comments, fixed some javadoc comments

### Version 1.4.j.m.7 - 05/07/2009 09:05
- Fixed calls to AccessModifiers
- Fixed some specials printing
- Added missing methods in JavaPrinter
- Renamed Printer in JavaCCPrinter
- Enhanced generated comments
- Added javadoc comments to visitors and generators
- Added javadoc comments to JTB classes

### Version 1.4.j.m.6 - 25/06/2009 09:18
- "instanceof" changed into "int variable" in ExpansionTreeWalker forgotten method
- Removed jtbgram.jtb temporary changes for being processed by JTB 1.3.2 on generics
- Generics types put back in jtbgram.jtb

### Version 1.4.j.m.5 - 22/06/2009 18:45
- Refactoring ClassInfo, Printer, JavaPrinter, Annotator, JavaStringMaker (StringBuilders, BufferedWriters,
   methods signatures)
- Renames JavaStringMaker into JavaBranchPrinter
- Vectors changed into ArrayLists
- "instanceof" changed into "int variable" in ExpansionTreeWalker and Expansion_ subclasses
- Changed direct use of node.tokenImage in node.accept to be able to print specials
- Moved addUnicodeEscapes to UnicodeConverter and use it for printing tokens in literals
- Corrections in JavaPrinter

### Version 1.4.j.m.4 - 18/06/2009 12:41
- Refactoring misc files : 
- Added base classes and visitors related constants in Globals
- Auto classes refactored (StringBuilders, BufferedWriters, renamed, common code, ...)
   and all merged in BaseClasses
- Depth First Visitor Generators refactored (StringBuilders, BufferedWriters, renamed, common code, ...)
   and all merged in DepthFirstVisitorsGenerator
- toolkit files deleted or renamed and moved up to misc

### Version 1.4.j.m.3 - 12/06/2009 18:03
- Work on syntaxtree classes : node interfaces and classes for visitors
- Renamed the Node and NodeListInterface interfaces into INode and INodeList
- jar generated and jtb parser looks functional

### Version 1.4.j.m.2 - 12/06/2009 12:06
- Work on ClassInfo, and JavaPrinter, Printer & Annotator visitors :
   adapted to JavaCC 4.2 grammar, performance improvements
- jar generated and jtb parser looks functional

### Version 1.4.j.m.1 - 11/06/2009 15:26
- parser classes taken from JavaCC 4.2 classes and adapted :
   no generics, no assignation of bnfproduction return info
- build_jtb.xml and build_jtb_props.xml adapted
- jar generated but jtb parser not functional





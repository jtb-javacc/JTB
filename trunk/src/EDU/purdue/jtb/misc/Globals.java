/**
 * Copyright (c) 2004,2005 UCLA Compilers Group.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  Redistributions of source code must retain the above copyright
 *  notice, this list of conditions and the following disclaimer.
 *
 *  Redistributions in binary form must reproduce the above copyright
 *  notice, this list of conditions and the following disclaimer in the
 *  documentation and/or other materials provided with the distribution.
 *
 *  Neither UCLA nor the names of its contributors may be used to endorse
 *  or promote products derived from this software without specific prior
 *  written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 **/
/*
 * All files in the distribution of JTB, The Java Tree Builder are
 * Copyright 1997, 1998, 1999 by the Purdue Research Foundation of Purdue
 * University.  All rights reserved.
 *
 * Redistribution and use in source and binary forms are permitted
 * provided that this entire copyright notice is duplicated in all
 * such copies, and that any documentation, announcements, and
 * other materials related to such distribution and use acknowledge
 * that the software was developed at Purdue University, West Lafayette,
 * Indiana by Kevin Tao, Wanjun Wang and Jens Palsberg.  No charge may
 * be made for copies, derivations, or distributions of this material
 * without the express written consent of the copyright holder.
 * Neither the name of the University nor the name of the author
 * may be used to endorse or promote products derived from this
 * material without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND WITHOUT ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, WITHOUT LIMITATION, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR ANY PARTICULAR PURPOSE.
 */
package EDU.purdue.jtb.misc;

/**
 * Class Globals contains global program information.
 * 
 * @author Marc Mazas, mmazas@sopragroup.com
 * @version 1.4.0 : 05-11/2009 : MMa : adapted to JavaCC v4.2 grammar and JDK 1.5
 * @version 1.4.1 : 02/2010 : MMa : added static flag
 */
public class Globals {

  /*
   * Global constants
   */

  /** The java program name */
  public static final String  PROG_NAME              = "JTB";
  /** The shell script name */
  public static final String  SCRIPT_NAME            = "jtb";
  /** The program version */
  public static final String  VERSION                = "1.4.6";
  /** The serial uid version */
  public static final long    SERIAL_UID             = 146L;
  /** Some of the authors */
  public static final String  AUTHORS                = "Wanjun Wang, Kevin Tao, Vids Samanta, Marc Mazas";
  /** An indication in case of JTB internal errors */
  public static final String  SUPPORT                = "support";
  /** Turns on / off debugging class comment printing */
  public static final boolean PRINT_CLASS_COMMENT    = false;
  /** The indentation default number of characters */
  public static final int     INDENT_AMT             = 2;

  /*
   * Classes / interfaces names (constants)
   */

  /** Node interface that all tree nodes implement */
  public static final String  iNodeName              = "INode";
  /** List interface that NodeList, NodeListOptional and NodeSequence implement */
  public static final String  iNodeListName          = "INodeList";

  /** Node representing a grammar choice such as ( A | B ) */
  public static final String  nodeChoiceName         = "NodeChoice";
  /** Node representing a list such as ( A )+ */
  public static final String  nodeListName           = "NodeList";
  /** Node representing an optional list such as (A )* */
  public static final String  nodeListOptName        = "NodeListOptional";
  /** Node representing an optional such as [ A ] or ( A )? */
  public static final String  nodeOptName            = "NodeOptional";
  /** Node representing a nested sequence of nodes */
  public static final String  nodeSeqName            = "NodeSequence";
  /** Node representing a token string such as "package" */
  public static final String  nodeTokenName          = "NodeToken";

  /** Fake node type representing the tokens "try {" in an ExpansionUnit type 3 */
  public static final String  fakeNodeLeftTry        = "fakeNodeLeftTry";
  /** Fake node type representing the tokens "}" in an ExpansionUnit type 3 */
  public static final String  fakeNodeRightTry       = "fakeNodeRightTry";
  /** Fake node type representing the tokens "catch (...) {...}" in an ExpansionUnit type 3 */
  public static final String  fakeNodeCatch          = "fakeNodeCatch";
  /** Fake node type representing the tokens "finally {...}" in an ExpansionUnit type 3 */
  public static final String  fakeNodeFinally        = "fakeNodeFinally";

  /** "RetArgu" visitor interface name (with return type and a user object argument) */
  public static final String  iRetArguVisitorName    = "IRetArguVisitor";
  /** Depth First "RetArgu" visitor class name (with return type and a user object argument) */
  public static final String  dFRetArguVisitorName   = "DepthFirstRetArguVisitor";
  /** Javadoc comment fragment for "RetArgu" visitor */
  public static final String  retArguVisitorComment  = "RetArgu";

  /** "Ret" visitor interface name (with return type and no user object argument) */
  public static final String  iRetVisitorName        = "IRetVisitor";
  /** Depth First "Ret" visitor class name (with return type and no user object argument) */
  public static final String  dFRetVisitorName       = "DepthFirstRetVisitor";
  /** Javadoc comment fragment for "Ret" visitor */
  public static final String  retVisitorComment      = "Ret";

  /** "VoidArgu" visitor interface name (with no return type and a user object argument) */
  public static final String  iVoidArguVisitorName   = "IVoidArguVisitor";
  /** Depth First "VoidArgu" visitor class name (with no return type and a user object argument) */
  public static final String  dFVoidArguVisitorName  = "DepthFirstVoidArguVisitor";
  /** Javadoc comment fragment for "VoidArgu" visitor */
  public static final String  voidArguVisitorComment = "VoidArgu";

  /** "Void" visitor interface name (with no return type and no user object argument)) */
  public static final String  iVoidVisitorName       = "IVoidVisitor";
  /** Depth First "Void" visitor class name (with no return type and no user object argument) */
  public static final String  dFVoidVisitorName      = "DepthFirstVoidVisitor";
  /** Javadoc comment fragment for "Void" visitor */
  public static final String  voidVisitorComment     = "Void";

  /** Generics visitor methods return type */
  public static final String  genRetType             = "R";
  /** Generics visitor methods user argument (second argument) type */
  public static final String  genArguType            = "A";
  /** Generics visitor methods user argument (second arguments) type */
  public static final String  genArgusType           = "A...";

  /** The JTB result type variables prefix */
  public static final String  JTBRT_PREFIX           = "jtbrt_";

  /*
   * Changeable flags (command line options)
   */

  /**
   * -va option which generates a return/argument visitor with a variable number of argument
   */
  public static boolean       varargs               = false;
  /**
   * -cl option which prints the generated classes list to System.out
   */
  public static boolean       printClassList         = false;
  /**
   * -w options which prevents JTB from overwriting existing files
   */
  public static boolean       noOverwrite            = false;
  /**
   * -e option which suppresses JTB semantic error checking
   */
  public static boolean       noSemanticCheck        = false;
  /**
   * -jd option which generates JavaDoc-friendly comments in generated visitors and syntax tree classes
   */
  public static boolean       javaDocComments        = false;
  /**
   * -f option which generates descriptive node class child field names such as whileStatement, nodeToken2,
   * ... rather than f0, f1, ...
   */
  public static boolean       descriptiveFieldNames  = false;
  /**
   * -pp option which generates parent pointer and getParent() and setParent() methods in all node classes
   */
  public static boolean       parentPointer          = false;
  /**
   * -dl option which generates depthLevel field in all visitor classes
   */
  public static boolean       depthLevel             = false;
  /**
   * -tk option which stores special tokens in the parse tree
   */
  public static boolean       keepSpecialTokens      = false;
  /**
   * -ia option which "inlines" the visitors accept methods on base classes
   */
  public static boolean       inlineAcceptMethods    = false;
  /**
   * -scheme option which generates the Scheme programming language record definitions file records.scm and
   * the SchemeTreeBuilder visitor
   */
  public static boolean       schemeToolkit          = false;
  /**
   * -printer option which generates TreeDumper and TreeFormatter visitors
   */
  public static boolean       printerToolkit         = false;
  /**
   * static or not option that comes from JavaCC
   */
  public static boolean       staticFlag             = false;

  /*
   * Default names
   */
  /** default nodes prefix */
  public static final String  DEF_ND_PREFIX          = "";
  /** default nodes suffixe */
  public static final String  DEF_ND_SUFFIX          = "";
  /** default nodes package name */
  public static final String  DEF_ND_PKG_NAME        = "syntaxtree";
  /** default nodes package name */
  public static final String  DEF_VIS_PKG_NAME       = "visitor";
  /** default nodes package name */
  public static final String  DEF_ND_DIR_NAME        = "syntaxtree";
  /** default nodes package name */
  public static final String  DEF_VIS_DIR_NAME       = "visitor";
  /** default nodes package name */
  public static final String  DEF_OUT_FILE_NAME      = "jtb.out.jj";
  /*
   * Changeable names
   */

  /**
   * -fqn option which produces full qualified node type
   */
  public static Boolean       fullQualifiedName      = false;
  /**
   * -npfx & -nsfx options which defines the node' prefix
   */
  public static String        nodePrefix             = DEF_ND_PREFIX;
  /**
   * -npfx & -nsfx options which defines the node' suffix
   */
  public static String        nodeSuffix             = DEF_ND_SUFFIX;
  /**
   * -np & -p options which defines the nodes package name (default is syntaxtree)
   */
  public static String        nodesPackageName       = DEF_ND_PKG_NAME;
  /**
   * -vp & -p options which defines the visitors package name (default is visitor)
   */
  public static String        visitorsPackageName    = DEF_VIS_PKG_NAME;
  /**
   * -nd & -d options which defines the nodes directory name (default is syntaxtree)
   */
  public static String        nodesDirName           = DEF_ND_DIR_NAME;
  /**
   * -vd & -d options which defines the visitors directory name (default is visitor)
   */
  public static String        visitorsDirName        = DEF_VIS_DIR_NAME;
  /**
   * -ns option which defines the nodes superclass
   */
  public static String        nodesSuperclass        = null;
  /**
   * the input (jtb) file name (must be set in main)
   */
  public static String        jtbInputFileName;
  /**
   * -o option which defines the output (generated) file name (default is jtb.out.jj)
   */
  public static String        jtbOutputFileName      = DEF_OUT_FILE_NAME;

  /*
   * Convenience methods
   */

  /**
   * Builds a file header comment ("Generated by JTB version").
   * 
   * @return a file header comment
   */
  public static String genFileHeaderComment() {
    return "/* Generated by " + PROG_NAME + " " + VERSION + " */";
  }

  /**
   * Build a name with default prefix and/or suffix
   * 
   * @param name string to suffix or infix
   * @return the suffixed or infixed name
   */
  public static String getClassName(final String name) {
    final StringBuilder sb = new StringBuilder();
    if (name.equals("Token") || name.equals("NodeToken") || name.equals("NodeChoice")
        || name.equals("NodeList") || name.equals("NodeListOptional") || name.equals("NodeSequence")
        || name.equals("NodeOptional")) {
      sb.append(name);
    }
    else {
      if (Globals.nodePrefix != null) {
        sb.append(Globals.nodePrefix);
      }
      sb.append(name);
      if (Globals.nodeSuffix != null) {
        sb.append(Globals.nodeSuffix);
      }
    }
    return sb.toString();
  }

 /**
   * Build a  full qualified name with default prefix and/or suffix
   * 
   * @param name string to suffix or infix
   * @return the suffixed or infixed name
   */
  @SuppressWarnings("boxing")
  public static String getQualifiedName(final String name) {
    final StringBuilder sb = new StringBuilder();
    if (Globals.fullQualifiedName) {
      sb.append(Globals.nodesPackageName);
      sb.append('.');
    }
    sb.append(getClassName(name));
    return sb.toString();
  }

}

/**
 * Copyright (c) 2004,2005 UCLA Compilers Group. All rights reserved. Redistribution and use in source and
 * binary forms, with or without modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this list of conditions and the
 * following disclaimer. Redistributions in binary form must reproduce the above copyright notice, this list
 * of conditions and the following disclaimer in the documentation and/or other materials provided with the
 * distribution. Neither UCLA nor the names of its contributors may be used to endorse or promote products
 * derived from this software without specific prior written permission. THIS SOFTWARE IS PROVIDED BY THE
 * COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE
 * GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 **/
/*
 * All files in the distribution of JTB, The Java Tree Builder are Copyright 1997, 1998, 1999 by the Purdue
 * Research Foundation of Purdue University. All rights reserved.
 *
 * Redistribution and use in source and binary forms are permitted provided that this entire copyright notice
 * is duplicated in all such copies, and that any documentation, announcements, and other materials related to
 * such distribution and use acknowledge that the software was developed at Purdue University, West Lafayette,
 * Indiana by Kevin Tao, Wanjun Wang and Jens Palsberg. No charge may be made for copies, derivations, or
 * distributions of this material without the express written consent of the copyright holder. Neither the
 * name of the University nor the name of the author may be used to endorse or promote products derived from
 * this material without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND WITHOUT ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, WITHOUT
 * LIMITATION, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR ANY PARTICULAR PURPOSE.
 */
package EDU.purdue.jtb.common;

import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import EDU.purdue.jtb.parser.Token;
import EDU.purdue.jtb.parser.syntaxtree.INode;
import EDU.purdue.jtb.parser.syntaxtree.INodeList;
import EDU.purdue.jtb.parser.syntaxtree.NodeChoice;
import EDU.purdue.jtb.parser.syntaxtree.NodeConstants;
import EDU.purdue.jtb.parser.syntaxtree.NodeList;
import EDU.purdue.jtb.parser.syntaxtree.NodeListOptional;
import EDU.purdue.jtb.parser.syntaxtree.NodeOptional;
import EDU.purdue.jtb.parser.syntaxtree.NodeSequence;
import EDU.purdue.jtb.parser.syntaxtree.NodeToken;
import EDU.purdue.jtb.parser.visitor.signature.ControlSignatureProcessor;
import EDU.purdue.jtb.parser.visitor.signature.NodeFieldsSignature;

/**
 * Class {@link Constants} contains static global flags, constants and names.
 *
 * @author Marc Mazas
 * @version 1.4.0 : 05-11/2009 : MMa : adapted to JavaCC v4.2 grammar and JDK 1.5
 * @version 1.4.1 : 02/2010 : MMa : added static flag
 * @version 1.4.6 : 01/2011 : FA/MMa : added -va and -npfx and -nsfx options
 * @version 1.5.0 : 01-03/2017 : MMa : SERIALVERSIONUID set to the product version number (without dots) ;
 *          added node scope hook option ; added NodeConstants elements ; added noVisitors and childrenMethods
 *          options ; added call external generator options ; added signature related constants ; enhanced to
 *          {@link VisitorInfo} based visitor generation; renamed from Globals to Constants; splitted with
 *          JTBOptions<br>
 *          1.5.0 : 10/2020 : MMa : modified nodeTCF as an {@link INodeList} (close to a
 *          {@link NodeSequence})<br>
 *          1.5.0 : 04/2021 : MMa : removed NodeTCF related code<br>
 */
public class Constants {
  
  /**
   * Constructor added to prevent javac to add an implicit public constructor.
   */
  private Constants() {
    throw new IllegalStateException("Utility class should not be instantiated");
  }
  
  /*
   * Debug flags / versions constants
   */
  
  /** Turns on / off printing class debug comments (useful to find where each line is produced) */
  public static final boolean DEBUG_CLASS   = false;
  /** Turns on / off printing field and sub comment debug comments */
  public static final boolean DEBUG_COMMENT = false;
  /** The program version */
  public static final String  JTB_VERSION   = "1.5.1";
  /** The serial version uid */
  public static final long    SERIALVERSIONUID;
  /** Initialize the serial uid version as the VERSION without the dots */
  static {
    final StringTokenizer st = new StringTokenizer(JTB_VERSION, ".");
    long v = 0;
    while (st.hasMoreTokens()) {
      v = (v * 10) + Long.parseLong(st.nextToken());
    }
    SERIALVERSIONUID = v;
  }
  /*
   * Miscellaneous constants
   */
  
  /** The java program name */
  public static final String                              PROG_NAME         = "JTB";
  /** File header comment */
  public static final String                              fileHeaderComment = "/* Generated by " + PROG_NAME
      + " " + JTB_VERSION + " */";
  /** Some of the authors */
  @SuppressWarnings("unused") private static final String AUTHORS           = "Jens Palsberg, Wanjun Wang, Kevin Tao, Vids Samanta,"
      + " Marc Mazas, Francis Andre";
  /** An indication in case of JTB internal errors */
  public static final String                              SUPPORT           = "JTB support (https://github.com/jtb-javacc/JTB)";
  /** The indentation default number of characters */
  public static final int                                 INDENT_AMT        = 2;
  /** The OS line separator string */
  public static final String                              LS                = System
      .getProperty("line.separator");
  /** The OS line separator first character */
  public static final char                                LS0               = LS.charAt(0);
  /** The OS line separator string length */
  public static final int                                 LSLEN             = LS.length();
  /** The javadoc break */
  private static final String                             BR                = "<br>";
  /** The javadoc break plus string length */
  public static final int                                 BRLEN             = BR.length();
  /** The javadoc break plus the OS line separator */
  public static final String                              BRLS              = BR + LS;
  /** The javadoc break plus the OS line separator string length */
  public static final int                                 BRLSLEN           = BRLS.length();
  /** The "OK" return code */
  public static final int                                 OK_RC             = 0;
  /** The "file exists" return code */
  public static final int                                 FILE_EXISTS_RC    = 1;
  
  /*
   * Classes / interfaces / variables names (constants) CODEJAVA
   */
  
  /** Name of the node interface that all tree nodes implement */
  public static final String iNode     = INode.class.getSimpleName();
  /** Name of the list interface that NodeList, NodeListOptional and NodeSequence implement */
  public static final String iNodeList = INodeList.class.getSimpleName();
  
  /** Name of the node class representing a grammar choice such as ( A | B ) */
  public static final String   nodeChoice          = NodeChoice.class.getSimpleName();
  /** Name of the node class representing a list such as ( A )+ */
  public static final String   nodeList            = NodeList.class.getSimpleName();
  /** Name of the node class representing an optional list such as (A )* */
  public static final String   nodeListOptional    = NodeListOptional.class.getSimpleName();
  /** Name of the node class representing an optional such as [ A ] or ( A )? */
  public static final String   nodeOptional        = NodeOptional.class.getSimpleName();
  /** Name of the node class representing a nested sequence of nodes */
  public static final String   nodeSequence        = NodeSequence.class.getSimpleName();
  /** Name of the node class representing a token string such as "package" */
  public static final String   nodeToken           = NodeToken.class.getSimpleName();
  /** Name of the (generated by JavaCC) class representing a token */
  public static final String   jjToken             = Token.class.getSimpleName();
  /** The array of base nodes interfaces */
  public static final String[] baseNodesInterfaces = new String[] {
      iNode, iNodeList
  };
  /** The list of base nodes classes */
  public static final String[] baseNodesClasses    = new String[] {
      nodeChoice, nodeList, nodeListOptional, nodeOptional, nodeSequence, nodeToken
  };
  /** Name of the node class holding the constants reflecting the generated classes " */
  public static final String   nodeConstants       = NodeConstants.class.getSimpleName();
  /** The TreeFormatter visitor class name */
  public static final String   treeFormatterName   = "TreeFormatter";
  /** The TreeDumpervisitor class name */
  public static final String   treeDumperName      = "TreeDumper";
  /** Accept methods visitor variable */
  public static final String   genVisVar           = "vis";
  /** Visitor methods return variable */
  public static final String   genRetVar           = "nRes";
  /** Visitor methods node argument (first argument) variable */
  public static final String   genNodeVar          = "n";
  /** Visitor methods node argument (first argument) variable plus dot */
  public static final String   genNodeVarDot       = genNodeVar + ".";
  /** Visitor methods user argument (second and more arguments) variable */
  public static final String   genArguVar          = "argu";
  /** Visitor methods depth level local variable */
  public static final String   genDepthLevelVar    = "depthLevel";
  
  /** The JTB node scope hook variable name */
  public static final String jtbHookVar         = "jtb_eeh";
  /** The node scope hook interface name */
  public static final String iEnterExitHook     = "IEnterExitHook";
  /** The node scope hook empty class name */
  public static final String emptyEnterExitHook = "EmptyEnterExitHook";
  /** The enter node scope hook method name */
  public static final String jtbHookEnter       = "Enter";
  /** The exit node scope hook method name */
  public static final String jtbHookExit        = "Exit";
  
  /** The external generator class method to be invoked */
  public static final String jtbEgInvokedMethode        = "generate";
  /** The jtb constants map key in the data model */
  public static final String jtb_constants_dm_key       = "jtb_constants";
  /** The jtb options map key in the data model */
  public static final String jtb_options_dm_key         = "jtb_options";
  /** The jtb base nodes interfaces list key in the data model */
  public static final String jtb_base_interfaces_dm_key = "jtb_base_interfaces";
  /** The jtb base nodes classes list key in the data model */
  public static final String jtb_base_nodes_dm_key      = "jtb_base_nodes";
  /** The jtb user nodes map key in the data model */
  public static final String jtb_user_nodes_dm_key      = "jtb_user_nodes";
  /** The jtb nodes which must not be created map key in the data model */
  public static final String jtb_notTbcNodes_dm_key     = "jtb_notTbcNodes";
  /** The jtb all nodes map key in the data model */
  public static final String jtb_prod_dm_key            = "jtb_prod";
  /** The jtb number of sub-nodes to be created map key in the data model */
  public static final String jtb_nbSubNodesTbc_dm_key   = "jtb_nbSubNodesTbc";
  
  /** The JTB result type variables prefix */
  public static final String jtbRtPrefix    = "jtbrt_";
  /** The JTB result type save variables prefix */
  public static final String jtbRtOld       = "oldJtbrt_";
  /** The JTB created node variable */
  public static final String jtbNodeVar     = "jtbNode";
  /** The JTB created user node variable prefix */
  public static final String jtbUserPfx     = "JTB_USER_";
  /** The JTB created control signature variable prefix */
  public static final String jtbSigPfx      = "JTB_SIG_";
  /** The signature annotation name */
  public static final String sigAnnName     = NodeFieldsSignature.class.getSimpleName();
  /** The signature annotation processor name */
  public static final String sigAnnProcName = ControlSignatureProcessor.class.getSimpleName();
  
  /*
   * Default names CODEJAVA
   */
  
  /** Default nodes prefix */
  public static final String DEF_ND_PREFIX     = "";                                                     // NO_UCD
                                                                                                         // (use
                                                                                                         // default)
  /** Default nodes suffix */
  public static final String DEF_ND_SUFFIX     = "";                                                     // NO_UCD
                                                                                                         // (use
                                                                                                         // default)
  /** Default nodes package name */
  public static final String DEF_ND_PKG_NAME   = "syntaxtree";
  /** Default visitors package name */
  public static final String DEF_VIS_PKG_NAME  = "visitor";
  /** Default visitors specification */
  static final String        DEF_VIS_SPEC      = "Void,void,None;VoidArgu,void,A;Ret,R,None;RetArgu,R,A";
  /** Default signature package name */
  public static final String DEF_SIG_PKG_NAME  = "signature";
  /** Default node scope hook package name */
  public static final String DEF_HOOK_PKG_NAME = "hook";
  /** Default nodes directory name */
  public static final String DEF_ND_DIR_NAME   = DEF_ND_PKG_NAME;
  /** Default visitors directory name */
  public static final String DEF_VIS_DIR_NAME  = DEF_VIS_PKG_NAME;
  /** Default signature directory name */
  public static final String DEF_SIG_DIR_NAME  = DEF_SIG_PKG_NAME;
  /** Default node scope hook directory name */
  public static final String DEF_HOOK_DIR_NAME = DEF_HOOK_PKG_NAME;
  /** Default JavaCC generated grammar file name */
  public static final String DEF_OUT_FILE_NAME = "jtb.out.jj";                                           // NO_UCD
                                                                                                         // (use
                                                                                                         // default)
  
  /*
   * Miscellaneous CODEJAVA
   */
  
  /** A map holding the primitive types (except void) */
  public static final Map<String, String> ptHM = new HashMap<>();
  /** The node class comment prefix es */
  static {
    ptHM.put("boolean", null);
    ptHM.put("char", null);
    ptHM.put("byte", null);
    ptHM.put("short", null);
    ptHM.put("int", null);
    ptHM.put("long", null);
    ptHM.put("float", null);
    ptHM.put("double", null);
  }
  
}

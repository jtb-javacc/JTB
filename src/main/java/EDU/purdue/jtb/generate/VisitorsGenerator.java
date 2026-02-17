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
package EDU.purdue.jtb.generate;

import static EDU.purdue.jtb.common.Constants.FILE_EXISTS_RC;
import static EDU.purdue.jtb.common.Constants.INDENT_AMT;
import static EDU.purdue.jtb.common.Constants.LS;
import static EDU.purdue.jtb.common.Constants.OK_RC;
import static EDU.purdue.jtb.common.Constants.beginHeaderComment;
import static EDU.purdue.jtb.common.Constants.genDepthLevelVar;
import static EDU.purdue.jtb.common.Constants.*;
import static EDU.purdue.jtb.common.Constants.genRetVar;
import static EDU.purdue.jtb.common.Constants.iNode;
import static EDU.purdue.jtb.common.Constants.jtbSigPfx;
import static EDU.purdue.jtb.common.Constants.nodeChoice;
import static EDU.purdue.jtb.common.Constants.nodeList;
import static EDU.purdue.jtb.common.Constants.nodeListOptional;
import static EDU.purdue.jtb.common.Constants.nodeOptional;
import static EDU.purdue.jtb.common.Constants.nodeSequence;
import static EDU.purdue.jtb.common.Constants.nodeToken;
import static EDU.purdue.jtb.common.Constants.sigAnnName;
import static EDU.purdue.jtb.common.Constants.sigAnnProcName;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import EDU.purdue.jtb.analyse.GlobalDataBuilder;
import EDU.purdue.jtb.common.Constants;
import EDU.purdue.jtb.common.JTBOptions;
import EDU.purdue.jtb.common.Messages;
import EDU.purdue.jtb.common.Spacing;
import EDU.purdue.jtb.common.UserClassInfo;
import EDU.purdue.jtb.common.UserClassInfo.FieldInfo;
import EDU.purdue.jtb.common.VisitorInfo;
import EDU.purdue.jtb.parser.syntaxtree.INodeList;
import EDU.purdue.jtb.parser.syntaxtree.NodeChoice;
import EDU.purdue.jtb.parser.syntaxtree.NodeList;
import EDU.purdue.jtb.parser.syntaxtree.NodeListOptional;
import EDU.purdue.jtb.parser.syntaxtree.NodeOptional;
import EDU.purdue.jtb.parser.syntaxtree.NodeSequence;
import EDU.purdue.jtb.parser.syntaxtree.NodeToken;

/**
 * Class {@link VisitorsGenerator} contains methods to generate: CODEJAVA
 * <ul>
 * <li>the different IXxxVisitor interfaces and DepthFirstXxxVisitor classes files,</li>
 * <li>the signature files.</li>
 * </ul>
 * <p>
 * Class maintains a state, and is not supposed to be run in parallel threads (on the same grammar). It does
 * not generate the files in parallel.
 * </p>
 * TODO add test runs on generated default visitors
 *
 * @author Marc Mazas
 * @version 1.4.0 : 05-08/2009 : MMa : adapted to JavaCC v4.2 grammar and JDK 1.5
 * @version 1.4.3.1 : 20/04/2009 : MMa : removed unnecessary @SuppressWarnings("unused") in genNodeTokenVisit
 * @version 1.4.3.3 : 27/04/2009 : MMa : put back @SuppressWarnings("unused") in genNodeTokenVisit
 * @version 1.4.6 : 01/2011 : FA : added -va and -npfx and -nsfx options
 * @version 1.4.7 : 09/2012 : MMa : extracted constants and methods ; added the reference to the
 *          {@link GlobalDataBuilder}
 * @version 1.4.8 : 10/2012 : MMa : tuned javadoc comments for nodes with no child<br>
 *          1.4.8 : 11/2014 : MMa : added @Override on generated visit methods,<br>
 *          and @SuppressWarnings("unused") on unused parameters
 * @version 1.5.0 : 01-06/2017 : MMa : used try-with-resource ; removed the suppress flag parameter ; added
 *          control signature code ; applied changes following new class FieldInfo ; added generation of an
 *          exception class for invalid switch values ; enhanced to VisitorInfo based visitor generation ;
 *          renamed from DepthFirstVisitorsGenerator ; subject to global packages and classes refactoring ;
 *          added final in ExpansionUnitTCF's catch<br>
 *          1.5.0 : 10/2020 : MMa : modified nodeTCF as an {@link INodeList} (close to a
 *          {@link NodeSequence})<br>
 *          1.5.0 : 04/2021 : MMa : added null conditions on accept calls in TCF ; removed NodeTCF related
 *          code<br>
 * @version 1.5.1 : 07/2023 : MMa : fixed issues with imports / packages on grammars with no package<br>
 *          1.5.1 : 08/2023 : MMa : editing changes for coverage analysis; changes due to the NodeToken
 *          replacement by Token
 * @version 1.5.3 : 11/2025 : MMa : NodeConstants replaced by &lt;Parser&gt;NodeConstants and signature code
 *          made independent of parser
 */
public class VisitorsGenerator {
  
  /** The messages handler */
  final Messages                    mess;
  /** The global JTB options */
  private final JTBOptions          jopt;
  /** The {@link GlobalDataBuilder} visitor */
  private final GlobalDataBuilder   gdbv;
  /** The {@link CommonCodeGenerator} */
  final CommonCodeGenerator         ccg;
  /** The accept methods inliner visitor */
  private final AcceptInliner       accInl;
  /** The classes list */
  private final List<UserClassInfo> classes;
  /** The indentation object */
  private Spacing                   spc       = new Spacing(INDENT_AMT);
  /** The (reused) buffer to write into */
  private final StringBuilder       gsb;
  /** The BufferedWriter buffer size */
  static final int                  BR_BUF_SZ = 16 * 1024;
  
  /**
   * Constructor. Creates the visitor directory if it does not exist.
   *
   * @param aGdbv - the {@link GlobalDataBuilder} visitor
   * @param aCcg - the {@link CommonCodeGenerator}
   * @param aClasses - the classes list
   */
  public VisitorsGenerator(final GlobalDataBuilder aGdbv, final CommonCodeGenerator aCcg,
      final List<UserClassInfo> aClasses) {
    gdbv = aGdbv;
    jopt = aGdbv.jopt;
    mess = jopt.mess;
    ccg = aCcg;
    classes = aClasses;
    gsb = new StringBuilder(dfVisBufferSize());
    accInl = jopt.inlineAcceptMethods ? new AcceptInliner(gdbv, ccg) : null;
  }
  
  /*
   * Visitors files generation methods
   */
  
  /**
   * Generates a DepthFirstXxxVisitor (class source) file.
   *
   * @param aVi - a VisitorInfo defining the visitor to generate
   * @param aVisitorDir - the visitor directory File
   * @return OK_RC or FILE_EXISTS_RC
   * @throws IOException if IO problem
   */
  public int genDepthFirstVisitorFile(final VisitorInfo aVi, final File aVisitorDir) throws IOException {
    final String clName = aVi.dfVisitorName + ".java";
    final File file = new File(aVisitorDir, clName);
    if (jopt.noOverwrite //
        && file.exists()) {
      mess.warning("File " + file.getPath() + ".java exists and was not overwritten");
      return FILE_EXISTS_RC;
    }
    try (PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(file), BR_BUF_SZ))) {
      gsb.setLength(0);
      pw.print(genDepthFirstVisitor(gsb, aVi));
      return OK_RC;
    } catch (final IOException e) {
      Messages.hardErr("IOException on " + file.getPath(), e);
      throw e;
    }
  }
  
  /*
   * Visitors source classes generation methods
   */
  
  /**
   * Generates a DepthFirstXxxVisitor class source.
   *
   * @param aSb - the buffer to append to (will be allocated if null)
   * @param aVi - a VisitorInfo defining the visitor to generate
   * @return the buffer with the DepthFirst visitor class source
   */
  StringBuilder genDepthFirstVisitor(final StringBuilder aSb, final VisitorInfo aVi) {
    
    StringBuilder sb = aSb;
    if (sb == null) {
      sb = new StringBuilder(dfVisBufferSize());
    }
    
    sb.append(beginHeaderComment).append(" (").append(this.getClass().getSimpleName()).append(") */")
        .append(LS);
    
    if (jopt.visitorsPkgName != null) {
      sb.append("package ").append(jopt.visitorsPkgName).append(';').append(LS).append(LS);
    }
    
    if (!jopt.noSignature) {
      if (jopt.nodesPkgName != null) {
        sb.append("import static ").append(jopt.nodesPkgName).append(".").append(jopt.parserName)
            .append(Constants.nodeConstants).append(".*;").append(LS);
      }
    }
    ccg.nodeTokenImport(sb);
    ccg.jjTokenImport(sb);
    if (jopt.visitorsPkgName != null //
        && jopt.nodesPkgName != null //
        && !jopt.visitorsPkgName.equals(jopt.nodesPkgName)) {
      sb.append("import ").append(jopt.nodesPkgName).append(".").append("*;").append(LS);
    }
    if (jopt.visitorsPkgName != null //
        && !jopt.noSignature) {
      sb.append("import ").append(jopt.signaturePkgName).append(".").append(sigAnnName).append(";")
          .append(LS);
    }
    sb.append(LS);
    
    if (jopt.javaDocComments) {
      sb.append("/**").append(LS);
      sb.append(" * Provides default methods which visit each node in the tree in depth-first order.<br>")
          .append(LS);
      sb.append(" * In your \"").append(aVi.suffix)
          .append("\" visitors extend this class and override part or all of these methods.").append(LS);
      sb.append(" *").append(LS);
      CommonCodeGenerator.genTypeParametersComment(sb, aVi);
      sb.append(" */").append(LS);
    } else {
      sb.append("@SuppressWarnings(\"javadoc\")").append(LS);
    }
    sb.append("public class ").append(aVi.dfVisitorName).append(aVi.classTypeParameters)
        .append(" implements ").append(aVi.interfaceName).append(aVi.classTypeParameters).append(" {")
        .append(LS).append(LS);
    
    spc.updateSpc(+1);
    
    if (jopt.depthLevel) {
      if (jopt.javaDocComments) {
        sb.append(spc.spc).append("/** The depth level (0, 1, ...) */").append(LS);
      }
      sb.append(spc.spc).append("int ").append(genDepthLevelVar).append(" = 0;").append(LS);
    }
    
    genBaseNodesVisitMethods(sb, spc, aVi);
    
    if (jopt.javaDocComments) {
      sb.append(spc.spc).append("/*").append(LS);
      sb.append(spc.spc).append(" * User grammar generated visit methods (to be overridden if necessary)")
          .append(LS);
      sb.append(spc.spc).append(" */").append(LS).append(LS);
    }
    for (final UserClassInfo uci : classes) {
      genUserNodeVisitMethod(sb, spc, uci, aVi, accInl);
    }
    
    if (jopt.inlineAcceptMethods) {
      genSNOE(sb);
    }
    
    // end of (visitor) class
    spc.updateSpc(-1);
    sb.append('}').append(LS);
    
    return sb;
  }
  
  /**
   * Generates a user node class visit method.
   *
   * @param aSb - the buffer to append to (will be allocated if null)
   * @param aSpc - the indentation
   * @param aUserClassInfo - the class data for the node to visit
   * @param aVi - a VisitorInfo defining the visitor to generate
   * @param aAccInl - an AcceptInliner
   */
  void genUserNodeVisitMethod(final StringBuilder aSb, final Spacing aSpc, final UserClassInfo aUserClassInfo,
      final VisitorInfo aVi, final AcceptInliner aAccInl) {
    final UserClassInfo uci = aUserClassInfo;
    final String className = uci.fixedClassName;
    StringBuilder sb = aSb;
    if (sb == null) {
      sb = new StringBuilder(4400);
    }
    
    if (jopt.javaDocComments) {
      sb.append(aSpc.spc).append("/**").append(LS);
      sb.append(aSpc.spc).append(" * Visits a {@link ").append(className).append("} node, ");
      sb.append(uci.astEcNode == null ? "with no child :"
          : uci.fields.size() == 1 ? "whose child is the following :" : "whose children are the following :")
          .append(LS);
      sb.append(aSpc.spc).append(" * <p>").append(LS);
      // generate the javadoc for the class fields, with indentation of 1
      ccg.fmtAllFieldsJavadocCmts(sb, aSpc, uci);
      CommonCodeGenerator.genParametersComment(sb, aSpc, aVi);
    }
    
    final String upperCN = className.toUpperCase();
    if (uci.astEcNode == null) {
      // empty node, unused arguments
      sb.append(aSpc.spc).append("@SuppressWarnings(\"unused\")").append(LS);
    }
    sb.append(aSpc.spc).append("@Override").append(LS);
    if (!jopt.noSignature) {
      sb.append(aSpc.spc).append("@").append(sigAnnName).append("(old_sig=").append(uci.fieldsHashSig)
          .append(", new_sig=");
      if (jopt.nodesPkgName == null) {
        sb.append(jopt.parserName).append(Constants.nodeConstants).append(".");
      }
      sb.append(jtbSigPfx).append(upperCN).append(", name=\"").append(className).append("\")").append(LS);
    }
    sb.append(aSpc.spc).append("public ").append(aVi.retInfo.fullType).append(" visit(final ")
        .append(className).append(' ').append(genNodeVar).append(aVi.userParameters).append(")").append(" {")
        .append(LS);
    
    aSpc.updateSpc(+1);
    
    if (!aVi.retInfo.isVoid) {
      sb.append(aSpc.spc).append(aVi.retInfo.fullType).append(' ').append(genRetVar).append(" = ")
          .append(aVi.retInfo.initializer).append(";").append(LS);
    }
    if (uci.astEcNode == null) {
      // empty node, just print comments
      sb.append(aSpc.spc).append("/* empty node, nothing that can be generated so far */").append(LS);
    } else
    // non empty node, generate the code to visit it
    if (jopt.inlineAcceptMethods) {
      // inline, call visitor
      aAccInl.genAcceptMethods(sb, aSpc, uci, aVi);
    } else {
      // no inlining, just direct accept calls
      // 0 = not in catch condition, 1 = at the beginning, 2 = inside
      int k = 0;
      for (final FieldInfo i : uci.fields) {
        final String name = i.name;
        ccg.fmtOneJavaCodeFieldCmt(sb, aSpc, k++, null, uci);
        if (jopt.depthLevel) {
          CommonCodeGenerator.increaseDepthLevel(sb, aSpc);
        }
        sb.append(aSpc.spc).append(genNodeVar).append(".").append(name);
        sb.append(".accept(this").append(aVi.userArguments);
        sb.append(");").append(LS);
        if (jopt.depthLevel) {
          CommonCodeGenerator.decreaseDepthLevel(sb, aSpc);
          // }
        }
      }
    }
    if (!aVi.retInfo.isVoid) {
      sb.append(aSpc.spc).append("return ").append(genRetVar).append(';').append(LS);
    }
    
    aSpc.updateSpc(-1);
    sb.append(aSpc.spc).append('}').append(LS).append(LS);
  }
  
  /**
   * Generates the base nodes classes visit methods.
   *
   * @param aSb - the buffer to append to (will be allocated if null)
   * @param aSpc - the indentation
   * @param aVi - a VisitorInfo defining the visitor to generate
   * @return the buffer with the DepthFirst visitor class source
   */
  StringBuilder genBaseNodesVisitMethods(final StringBuilder aSb, final Spacing aSpc, final VisitorInfo aVi) {
    StringBuilder sb = aSb;
    if (sb == null) {
      sb = new StringBuilder(4400);
    }
    
    sb.append(LS);
    if (jopt.javaDocComments) {
      sb.append(aSpc.spc).append("/*").append(LS);
      sb.append(aSpc.spc).append(" * Base nodes classes visit methods (to be overridden if necessary)")
          .append(LS);
      sb.append(aSpc.spc).append(" */").append(LS).append(LS);
    }
    genNodeChoiceVisit(sb, aSpc, aVi);
    sb.append(LS);
    genNodeListVisit(sb, aSpc, aVi);
    sb.append(LS);
    genNodeListOptionalVisit(sb, aSpc, aVi);
    sb.append(LS);
    genNodeOptionalVisit(sb, aSpc, aVi);
    sb.append(LS);
    genNodeSequenceVisit(sb, aSpc, aVi);
    sb.append(LS);
    genNodeTokenVisit(sb, aSpc, aVi);
    sb.append(LS);
    
    return sb;
  }
  
  /**
   * Generates the base node {@link NodeChoice} visit method.
   *
   * @param aSb - the buffer to append to (will be allocated if null)
   * @param aSpc - the indentation
   * @param aVi - a VisitorInfo defining the visitor to generate
   * @return the buffer with the DepthFirst visitor class source
   */
  StringBuilder genNodeChoiceVisit(final StringBuilder aSb, final Spacing aSpc, final VisitorInfo aVi) {
    StringBuilder sb = aSb;
    if (sb == null) {
      sb = new StringBuilder(900);
    }
    
    baseNodeVisitMethodBegin(sb, aSpc, aVi, nodeChoice, false);
    if (jopt.depthLevel) {
      CommonCodeGenerator.increaseDepthLevel(sb, aSpc);
    }
    sb.append(aSpc.spc);
    if (!aVi.retInfo.isVoid) {
      sb.append("final ").append(aVi.retInfo.fullType).append(' ').append(genRetVar).append(" = ");
    }
    sb.append(genNodeVar).append(".choice.accept(this").append(aVi.userArguments).append(");").append(LS);
    if (jopt.depthLevel) {
      CommonCodeGenerator.decreaseDepthLevel(sb, aSpc);
    }
    sb.append(aSpc.spc).append("return");
    if (!aVi.retInfo.isVoid) {
      sb.append(' ').append(genRetVar);
    }
    sb.append(';').append(LS);
    baseNodeVisitMethodCloseBrace(sb, aSpc);
    
    return sb;
  }
  
  /**
   * Generates the base node {@link NodeList} visit method.
   *
   * @param aSb - the buffer to append to (will be allocated if null)
   * @param aSpc - the indentation
   * @param aVi - a VisitorInfo defining the visitor to generate
   * @return the buffer with the DepthFirst visitor class source
   */
  StringBuilder genNodeListVisit(final StringBuilder aSb, final Spacing aSpc, final VisitorInfo aVi) {
    StringBuilder sb = aSb;
    if (sb == null) {
      sb = new StringBuilder(900);
    }
    
    baseNodeVisitMethodBegin(sb, aSpc, aVi, nodeList, false);
    if (!aVi.retInfo.isVoid) {
      sb.append(aSpc.spc).append(aVi.retInfo.fullType).append(' ').append(genRetVar).append(" = ")
          .append(aVi.retInfo.initializer).append(";").append(LS);
    }
    sb.append(aSpc.spc).append("for (").append(iNode).append(" e : ").append(genNodeVar).append(".nodes) {")
        .append(LS);
    aSpc.updateSpc(+1);
    if (jopt.depthLevel) {
      CommonCodeGenerator.increaseDepthLevel(sb, aSpc);
    }
    sb.append(aSpc.spc);
    if (!aVi.retInfo.isVoid) {
      sb.append("@SuppressWarnings(\"unused\")").append(LS);
      sb.append(aSpc.spc).append("final ").append(aVi.retInfo.fullType).append(" sRes = ");
    }
    sb.append("e.accept(this").append(aVi.userArguments).append(");").append(LS);
    if (jopt.depthLevel) {
      CommonCodeGenerator.decreaseDepthLevel(sb, aSpc);
    }
    baseNodeVisitMethodCloseBrace(sb, aSpc);
    sb.append(aSpc.spc).append("return");
    if (!aVi.retInfo.isVoid) {
      sb.append(' ').append(genRetVar);
    }
    sb.append(';').append(LS);
    baseNodeVisitMethodCloseBrace(sb, aSpc);
    
    return sb;
  }
  
  /**
   * Generates the base node {@link NodeListOptional} visit method.
   *
   * @param aSb - the buffer to append to (will be allocated if null)
   * @param aSpc - the indentation
   * @param aVi - a VisitorInfo defining the visitor to generate
   * @return the buffer with the DepthFirst visitor class source
   */
  StringBuilder genNodeListOptionalVisit(final StringBuilder aSb, final Spacing aSpc, final VisitorInfo aVi) {
    StringBuilder sb = aSb;
    if (sb == null) {
      sb = new StringBuilder(1100);
    }
    
    baseNodeVisitMethodBegin(sb, aSpc, aVi, nodeListOptional, false);
    sb.append(aSpc.spc).append("if (").append(genNodeVar).append(".present()) {").append(LS);
    aSpc.updateSpc(+1);
    if (!aVi.retInfo.isVoid) {
      sb.append(aSpc.spc).append(aVi.retInfo.fullType).append(' ').append(genRetVar).append(" = ")
          .append(aVi.retInfo.initializer).append(";").append(LS);
    }
    sb.append(aSpc.spc).append("for (").append(iNode).append(" e : ").append(genNodeVar).append(".nodes) {")
        .append(LS);
    aSpc.updateSpc(+1);
    if (jopt.depthLevel) {
      CommonCodeGenerator.increaseDepthLevel(sb, aSpc);
    }
    sb.append(aSpc.spc);
    if (!aVi.retInfo.isVoid) {
      sb.append("@SuppressWarnings(\"unused\")").append(LS);
      sb.append(aSpc.spc).append(aVi.retInfo.fullType).append(" sRes = ");
    }
    sb.append("e.accept(this").append(aVi.userArguments).append(");").append(LS);
    if (jopt.depthLevel) {
      CommonCodeGenerator.decreaseDepthLevel(sb, aSpc);
    }
    aSpc.updateSpc(-1);
    sb.append(aSpc.spc).append('}').append(LS);
    sb.append(aSpc.spc).append("return");
    if (!aVi.retInfo.isVoid) {
      sb.append(' ').append(genRetVar);
    }
    sb.append(';').append(LS);
    aSpc.updateSpc(-1);
    sb.append(aSpc.spc).append("}").append(LS);
    sb.append(aSpc.spc).append("return");
    if (!aVi.retInfo.isVoid) {
      sb.append(' ').append(aVi.retInfo.initializer);
    }
    sb.append(';').append(LS);
    baseNodeVisitMethodCloseBrace(sb, aSpc);
    
    return sb;
  }
  
  /**
   * Generates the base node {@link NodeOptional} visit method.
   *
   * @param aSb - the buffer to append to (will be allocated if null)
   * @param aSpc - the indentation
   * @param aVi - a VisitorInfo defining the visitor to generate
   * @return the buffer with the DepthFirst visitor class source
   */
  StringBuilder genNodeOptionalVisit(final StringBuilder aSb, final Spacing aSpc, final VisitorInfo aVi) {
    StringBuilder sb = aSb;
    if (sb == null) {
      sb = new StringBuilder(720);
    }
    
    baseNodeVisitMethodBegin(sb, aSpc, aVi, nodeOptional, false);
    sb.append(aSpc.spc).append("if (").append(genNodeVar).append(".present()) {").append(LS);
    aSpc.updateSpc(+1);
    if (jopt.depthLevel) {
      CommonCodeGenerator.increaseDepthLevel(sb, aSpc);
    }
    sb.append(aSpc.spc);
    if (!aVi.retInfo.isVoid) {
      sb.append("final ").append(aVi.retInfo.fullType).append(' ').append(genRetVar).append(" = ");
    }
    sb.append(genNodeVar).append(".node.accept(this").append(aVi.userArguments).append(");").append(LS);
    if (jopt.depthLevel) {
      CommonCodeGenerator.decreaseDepthLevel(sb, aSpc);
    }
    sb.append(aSpc.spc).append("return");
    if (!aVi.retInfo.isVoid) {
      sb.append(' ').append(genRetVar);
    }
    sb.append(';').append(LS);
    aSpc.updateSpc(-1);
    sb.append(aSpc.spc).append("}").append(LS);
    sb.append(aSpc.spc).append("return");
    if (!aVi.retInfo.isVoid) {
      sb.append(' ').append(aVi.retInfo.initializer);
    }
    sb.append(';').append(LS);
    baseNodeVisitMethodCloseBrace(sb, aSpc);
    
    return sb;
  }
  
  /**
   * Generates the base node {@link NodeSequence} visit method.
   *
   * @param aSb - the buffer to append to (will be allocated if null)
   * @param aSpc - the indentation
   * @param aVi - a VisitorInfo defining the visitor to generate
   * @return the buffer with the DepthFirst visitor class source
   */
  StringBuilder genNodeSequenceVisit(final StringBuilder aSb, final Spacing aSpc, final VisitorInfo aVi) {
    StringBuilder sb = aSb;
    if (sb == null) {
      sb = new StringBuilder(920);
    }
    
    baseNodeVisitMethodBegin(sb, aSpc, aVi, nodeSequence, false);
    if (!aVi.retInfo.isVoid) {
      sb.append(aSpc.spc).append(aVi.retInfo.fullType).append(' ').append(genRetVar).append(" = ")
          .append(aVi.retInfo.initializer).append(";").append(LS);
    }
    sb.append(aSpc.spc).append("for (").append(iNode).append(" e : ").append(genNodeVar).append(".nodes) {")
        .append(LS);
    aSpc.updateSpc(+1);
    if (jopt.depthLevel) {
      CommonCodeGenerator.increaseDepthLevel(sb, aSpc);
    }
    sb.append(aSpc.spc);
    if (!aVi.retInfo.isVoid) {
      sb.append("@SuppressWarnings(\"unused\")").append(LS);
      sb.append(aSpc.spc).append(aVi.retInfo.fullType).append(" subRet = ");
    }
    sb.append("e.accept(this").append(aVi.userArguments).append(");").append(LS);
    if (jopt.depthLevel) {
      CommonCodeGenerator.decreaseDepthLevel(sb, aSpc);
    }
    baseNodeVisitMethodCloseBrace(sb, aSpc);
    sb.append(aSpc.spc).append("return");
    if (!aVi.retInfo.isVoid) {
      sb.append(' ').append(genRetVar);
    }
    sb.append(';').append(LS);
    baseNodeVisitMethodCloseBrace(sb, aSpc);
    
    return sb;
  }
  
  /**
   * Generates the base node {@link NodeToken} visit method.
   *
   * @param aSb - the buffer to append to (will be allocated if null)
   * @param aSpc - the indentation
   * @param aVi - a VisitorInfo defining the visitor to generate
   * @return the buffer with the DepthFirst visitor class source
   */
  StringBuilder genNodeTokenVisit(final StringBuilder aSb, final Spacing aSpc, final VisitorInfo aVi) {
    StringBuilder sb = aSb;
    if (sb == null) {
      sb = new StringBuilder(680);
    }
    
    baseNodeVisitMethodBegin(sb, aSpc, aVi, nodeToken, true);
    if (!aVi.retInfo.isVoid) {
      sb.append(aSpc.spc).append(aVi.retInfo.fullType).append(' ').append(genRetVar).append(" = ")
          .append(aVi.retInfo.initializer).append(";").append(LS);
    }
    sb.append(aSpc.spc).append("@SuppressWarnings(\"unused\") final String tkIm = ((").append(jjToken)
        .append(") ").append(genNodeVar).append(").image;").append(LS);
    sb.append(aSpc.spc).append("return");
    if (!aVi.retInfo.isVoid) {
      sb.append(' ').append(genRetVar);
    }
    sb.append(';').append(LS);
    baseNodeVisitMethodCloseBrace(sb, aSpc);
    
    return sb;
  }
  
  /**
   * Outputs the beginning of a visit method for a base node.
   *
   * @param aSb - the buffer to append to (must be non null)
   * @param aSpc - the indentation
   * @param aVi - a VisitorInfo defining the visitor to generate
   * @param aNodeName - the node name
   * @param aSuppWarn - true to add the suppress warning annotation, false otherwise
   */
  void baseNodeVisitMethodBegin(final StringBuilder aSb, final Spacing aSpc, final VisitorInfo aVi,
      final String aNodeName, final boolean aSuppWarn) {
    if (jopt.javaDocComments) {
      baseNodeVisitMethodJavadoc(aSb, aSpc, aVi, aNodeName);
    }
    aSb.append(aSpc.spc).append("@Override").append(LS);
    aSb.append(aSpc.spc).append("public ").append(aVi.retInfo.fullType).append(" visit(final ")
        .append(aNodeName).append(' ').append(genNodeVar)
        .append(aSuppWarn ? aVi.userParametersSuppWarn : aVi.userParameters);
    aSb.append(") {").append(LS);
    aSpc.updateSpc(+1);
    if (!aVi.retInfo.isVoid) {
      aSb.append(aSpc.spc).append("/* You have to adapt which data is returned")
          .append(" (result variables below are just examples) */").append(LS);
    }
  }
  
  /**
   * Outputs the visit method javadoc comment for a base node.
   *
   * @param aSb - the buffer to append to (must be non null)
   * @param aSpc - the indentation
   * @param aVi - a VisitorInfo defining the visitor to generate
   * @param aNodeName - the node name
   */
  static void baseNodeVisitMethodJavadoc(final StringBuilder aSb, final Spacing aSpc, final VisitorInfo aVi,
      final String aNodeName) {
    aSb.append(aSpc.spc).append("/**").append(LS);
    aSb.append(aSpc.spc).append(" * Visits a {@link ").append(aNodeName).append("} node.").append(LS);
    CommonCodeGenerator.genParametersComment(aSb, aSpc, aVi);
  }
  
  /**
   * Outputs the closing of a brace.
   *
   * @param aSb - the buffer to append to (must be non null)
   * @param aSpc - the indentation
   */
  static void baseNodeVisitMethodCloseBrace(final StringBuilder aSb, final Spacing aSpc) {
    aSpc.updateSpc(-1);
    aSb.append(aSpc.spc).append('}').append(LS);
  }
  
  /**
   * Generates the ShouldNotOccurException class.
   *
   * @param aSb - the buffer to append to (must be non null)
   */
  private void genSNOE(final StringBuilder aSb) {
    if (jopt.javaDocComments) {
      aSb.append("  /**").append(LS);
      aSb.append("   * Class handling a programmatic exception. Static for generic outer classes.")
          .append(LS);
      aSb.append("   */").append(LS);
    } else {
      aSb.append("@SuppressWarnings(\"javadoc\")").append(LS);
    }
    aSb.append("  public static class ShouldNotOccurException extends RuntimeException {").append(LS)
        .append(LS);
    
    if (jopt.javaDocComments) {
      aSb.append("    /** Default serialVersionUID */").append(LS);
    }
    aSb.append("    private static final long serialVersionUID = 1L;").append(LS).append(LS);
    
    if (jopt.javaDocComments) {
      aSb.append("    /**").append(LS);
      aSb.append("     * Constructor with no message.").append(LS);
      aSb.append("     */").append(LS);
    }
    aSb.append("    public ShouldNotOccurException() {").append(LS);
    aSb.append("      super();").append(LS);
    aSb.append("    }").append(LS).append(LS);
    
    if (jopt.javaDocComments) {
      aSb.append("    /**").append(LS);
      aSb.append("     * Constructor which outputs a message.").append(LS);
      aSb.append("     *").append(LS);
      aSb.append("     * @param ch - a NodeChoice whose which value is invalid or lead to a fall-through")
          .append(LS);
      aSb.append("     */").append(LS);
    }
    aSb.append("    public ShouldNotOccurException(final NodeChoice ch) {").append(LS);
    aSb.append("      super(\"Invalid switch value (\" + ch.which + \") or fall-through\");").append(LS);
    aSb.append("    }").append(LS).append(LS);
    
    aSb.append("  }").append(LS).append(LS);
  }
  
  /**
   * Generates an IXxxVisitor (interface source) file.
   *
   * @param aVi - a VisitorInfo defining the visitor to generate
   * @param aVisitorDir - the visitor directory File
   * @return OK_RC or FILE_EXISTS_RC
   * @throws IOException - if IO problem
   */
  public int genIVisitorFile(final VisitorInfo aVi, final File aVisitorDir) throws IOException {
    final String ifName = aVi.interfaceName + ".java";
    final File fif = new File(aVisitorDir, ifName);
    if (jopt.noOverwrite //
        && fif.exists()) {
      mess.warning("File " + aVi.interfaceName + ".java exists and was not overwritten");
      return FILE_EXISTS_RC;
    }
    try (final PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(fif), BR_BUF_SZ))) {
      gsb.setLength(0);
      pw.print(genIVisitor(gsb, aVi));
      return OK_RC;
    } catch (final IOException e) {
      final String msg = "Could not generate interface visitor file  " + ifName + ": " + e;
      Messages.hardErr(msg);
      throw new IOException(msg, e);
    }
  }
  
  /**
   * Generates an IXxxVisitor interface source.
   *
   * @param aSb - the buffer to append to (will be allocated if null)
   * @param aVi - a VisitorInfo defining the visitor to generate
   * @return StringBuilder the given one if not null, or a new allocated one if null, completed with the
   *         visitor class source
   */
  StringBuilder genIVisitor(final StringBuilder aSb, final VisitorInfo aVi) {
    StringBuilder sb = aSb;
    if (sb == null) {
      sb = new StringBuilder(intfVisBufferSize());
    }
    
    genIVisitorBeg(sb, aVi);
    
    genBaseVisitMethods(sb, spc, aVi);
    
    genIVisitorEnd(sb, spc, aVi, classes);
    
    return sb;
  }
  
  /**
   * Generates the start of an IXxxVisitor interface.
   *
   * @param aSb - the buffer to append to (must be non null)
   * @param aVi - a VisitorInfo defining the visitor to generate
   */
  private void genIVisitorBeg(final StringBuilder aSb, final VisitorInfo aVi) {
    aSb.append(beginHeaderComment).append(" (").append(this.getClass().getSimpleName()).append(") */")
        .append(LS);
    if (jopt.visitorsPkgName != null) {
      aSb.append("package ").append(jopt.visitorsPkgName).append(";").append(LS).append(LS);
    }
    if (jopt.nodesPkgName != null) {
      if (!jopt.nodesPkgName.equals(jopt.visitorsPkgName)) {
        aSb.append("import ").append(jopt.nodesPkgName).append(".*;").append(LS);
      }
    }
    ccg.nodeTokenImport(aSb);
    aSb.append(aVi.imports);
    aSb.append(LS);
    if (jopt.javaDocComments) {
      aSb.append("/**").append(LS);
      aSb.append(" * All \"").append(aVi.interfaceName).append("\" visitors must implement this interface.")
          .append(LS).append(LS);
      CommonCodeGenerator.genTypeParametersComment(aSb, aVi);
      aSb.append(" */").append(LS);
    } else {
      aSb.append("@SuppressWarnings(\"javadoc\")").append(LS);
    }
    aSb.append("public interface ").append(aVi.interfaceName).append(aVi.classTypeParameters).append(" {")
        .append(LS);
  }
  
  /**
   * Generates the base visit methods.
   *
   * @param aSb - the buffer to append to (must be non null)
   * @param aSpc - the indentation
   * @param aVi - a VisitorInfo defining the visitor to generate
   */
  private void genBaseVisitMethods(final StringBuilder aSb, final Spacing aSpc, final VisitorInfo aVi) {
    aSb.append(LS);
    aSpc.updateSpc(+1);
    if (jopt.javaDocComments) {
      aSb.append(aSpc.spc).append("/*").append(LS);
      aSb.append(aSpc.spc).append(" * Base nodes visit methods").append(LS);
      aSb.append(aSpc.spc).append(" */").append(LS).append(LS);
    }
    genVisitAnyBaseNode(aSb, aSpc, aVi, nodeChoice);
    aSb.append(LS);
    genVisitAnyBaseNode(aSb, aSpc, aVi, nodeList);
    aSb.append(LS);
    genVisitAnyBaseNode(aSb, aSpc, aVi, nodeListOptional);
    aSb.append(LS);
    genVisitAnyBaseNode(aSb, aSpc, aVi, nodeOptional);
    aSb.append(LS);
    genVisitAnyBaseNode(aSb, aSpc, aVi, nodeSequence);
    aSb.append(LS);
    genVisitAnyBaseNode(aSb, aSpc, aVi, nodeToken);
    aSb.append(LS);
    aSpc.updateSpc(-1);
  }
  
  /**
   * Generates the end of an IXxxVisitor interface.
   *
   * @param aSb - the buffer to append to (must be non null)
   * @param aSpc - the indentation
   * @param aVi - a VisitorInfo defining the visitor to generate
   * @param aClasses - the list of {@link UserClassInfo} classes instances
   */
  private void genIVisitorEnd(final StringBuilder aSb, final Spacing aSpc, final VisitorInfo aVi,
      final List<UserClassInfo> aClasses) {
    aSpc.updateSpc(+1);
    if (jopt.javaDocComments) {
      aSb.append(aSpc.spc).append("/*").append(LS);
      aSb.append(aSpc.spc).append(" * User grammar generated visit methods").append(LS);
      aSb.append(aSpc.spc).append(" */").append(LS).append(LS);
    }
    // small optimization
    StringBuilder pcSb = null;
    if (jopt.javaDocComments) {
      pcSb = new StringBuilder(256);
      CommonCodeGenerator.genParametersComment(pcSb, aSpc, aVi);
    }
    for (final UserClassInfo uci : aClasses) {
      final String className = uci.fixedClassName;
      if (jopt.javaDocComments) {
        aSb.append(aSpc.spc).append("/**").append(LS);
        aSb.append(aSpc.spc).append(" * Visits a {@link ").append(className).append("} node, ");
        aSb.append(uci.astEcNode == null ? "with no child :"
            : uci.fields.size() == 1 ? "whose child is the following :"
                : "whose children are the following :")
            .append(LS);
        aSb.append(aSpc.spc).append(" * <p>").append(LS);
        // generate the javadoc for the class fields, with indentation of 1
        ccg.fmtAllFieldsJavadocCmts(aSb, aSpc, uci);
        aSb.append(pcSb);
      }
      aSb.append(aSpc.spc).append("public ").append(aVi.retInfo.fullType).append(" visit(final ")
          .append(className).append(' ').append(genNodeVar).append(aVi.userParameters).append(");").append(LS)
          .append(LS);
    }
    aSpc.updateSpc(-1);
    aSb.append("}").append(LS);
  }
  
  /**
   * Generates the visit method declaration on a given node type for a visitor with user Return and Argument
   * data.
   *
   * @param aSb - the buffer to append to (must be non null)
   * @param aSpc - the indentation
   * @param aVi - a VisitorInfo defining the visitor to generate
   * @param aNodeType - the node type
   */
  private void genVisitAnyBaseNode(final StringBuilder aSb, final Spacing aSpc, final VisitorInfo aVi,
      final String aNodeType) {
    if (jopt.javaDocComments) {
      aSb.append(aSpc.spc).append("/**").append(LS);
      aSb.append(aSpc.spc).append(" * Visits a {@link ").append(aNodeType).append("} node");
      if (!aVi.argInfoList.isEmpty()) {
        aSb.append("), passing it argument(s)");
      }
      aSb.append(".").append(LS);
      CommonCodeGenerator.genParametersComment(aSb, aSpc, aVi);
    }
    aSb.append(aSpc.spc).append("public ").append(aVi.retInfo.type).append(" visit(final ").append(aNodeType)
        .append(' ').append(genNodeVar).append(aVi.userParameters).append(");").append(LS);
  }
  
  /**
   * Estimates the depth first visitors files size.
   *
   * @return the estimated size
   */
  private int dfVisBufferSize() {
    return (jopt.inlineAcceptMethods ? 1000 : 250) * classes.size();
  }
  
  /**
   * Estimates the interface visitors files size.
   *
   * @return the estimated size
   */
  private int intfVisBufferSize() {
    return 100 * classes.size();
  }
  
  /**
   * Generates the NodeFieldsSignature (annotation source) file.
   *
   * @param aSignatureDir - the signature directory File
   * @return OK_RC or FILE_EXISTS_RC
   * @throws IOException - if IO problem
   */
  public int genSigAnnFile(final File aSignatureDir) throws IOException {
    final File file = new File(aSignatureDir, sigAnnName + ".java");
    if (file.exists()) {
      mess.warning("File " + sigAnnName + ".java exists and was not overwritten");
      return FILE_EXISTS_RC;
    }
    try (PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(file), 1550))) {
      gsb.setLength(0);
      pw.print(genSigAnn(gsb));
      return OK_RC;
    } catch (final IOException e) {
      final String msg = "Could not generate signature annotation file  " + sigAnnName + ".java" + ": " + e;
      Messages.hardErr(msg);
      throw e;
    }
  }
  
  /**
   * Generates the NodeFieldsSignature annotation source.
   *
   * @param aSb - the buffer to append to (will be allocated if null)
   * @return StringBuilder the given one if not null, or a new allocated one if null, completed with the hook
   *         interface source
   */
  StringBuilder genSigAnn(final StringBuilder aSb) {
    StringBuilder sb = aSb;
    if (sb == null) {
      sb = new StringBuilder(1550);
    }
    
    sb.append(beginHeaderComment).append(" (").append(this.getClass().getSimpleName()).append(") */")
        .append(LS);
    if (jopt.signaturePkgName != null) {
      sb.append("package ").append(jopt.signaturePkgName).append(";").append(LS).append(LS);
    }
    sb.append("import java.lang.annotation.ElementType;").append(LS);
    sb.append("import java.lang.annotation.Retention;").append(LS);
    sb.append("import java.lang.annotation.RetentionPolicy;").append(LS);
    sb.append("import java.lang.annotation.Target;").append(LS).append(LS);
    sb.append("import javax.annotation.processing.SupportedAnnotationTypes;").append(LS).append(LS);
    if (jopt.javaDocComments) {
      sb.append("/** ").append(LS);
      sb.append(
          " * Annotation {@link NodeFieldsSignature} enables the {@link ControlSignatureProcessor} annotation")
          .append(LS);
      sb.append(
          " * processor to issue a compile error if the user visitors' visit methods are not coded against the")
          .append(LS);
      sb.append(" * last nodes definitions.<br>").append(LS);
      sb.append(" * The user nodes signatures are generated in the &lt;Parser&gt;NodeConstants class,<br>")
          .append(LS);
      sb.append(" * the default visitors' visit methods are generated with the {@link NodeFieldsSignature}")
          .append(LS);
      sb.append(
          " * annotation, with the 3 values {@link #old_sig()}, {@link #new_sig()}, {@link #name()},<br>")
          .append(LS);
      sb.append(" * and the user visitors' visit methods can be annotated with the same annotation.")
          .append(LS);
      sb.append(" * <p>").append(LS);
      sb.append(" * Note: the fully qualified name of this class is a parameter in the").append(LS);
      sb.append(" * {@link SupportedAnnotationTypes} annotation in {@link ControlSignatureProcessor}.")
          .append(LS);
      sb.append(" * </p>").append(LS);
      sb.append(" *").append(LS);
      sb.append(" * @author Marc Mazas").append(LS);
      sb.append(" *  @version 1.5.0 : 02/2017 : MMa : created").append(LS);
      sb.append(" *  @version 1.5.3 : 11/2025 : MMa : NodeConstants replaced by &lt;Parser&gt;NodeConstants")
          .append(LS);
      sb.append(" */").append(LS);
    } else {
      sb.append("@SuppressWarnings({\"javadoc\",\"unused\"})").append(LS);
    }
    sb.append("@Target(ElementType.METHOD)").append(LS);
    sb.append("@Retention(RetentionPolicy.SOURCE)").append(LS);
    sb.append("public @interface ").append(sigAnnName).append(" {").append(LS).append(LS);
    
    spc = new Spacing(INDENT_AMT);
    spc.updateSpc(+1);
    if (jopt.javaDocComments) {
      sb.append(spc.spc).append("/**").append(LS);
      sb.append(spc.spc).append(" * The \"old\" (usually copied) node fields signature").append(LS);
      sb.append(spc.spc).append(" */").append(LS);
    }
    sb.append(spc.spc).append("int old_sig();").append(LS).append(LS);
    if (jopt.javaDocComments) {
      sb.append(spc.spc).append("/**").append(LS);
      sb.append(spc.spc).append(" * The \"new\" (newly computed) node fields signature").append(LS);
      sb.append(spc.spc).append(" */").append(LS);
    }
    sb.append(spc.spc).append("int new_sig();").append(LS).append(LS);
    if (jopt.javaDocComments) {
      sb.append(spc.spc).append("/**").append(LS);
      sb.append(spc.spc).append(" * The JTB node name").append(LS);
      sb.append(spc.spc).append(" */").append(LS);
    }
    sb.append(spc.spc).append("String name();").append(LS).append(LS);
    spc.updateSpc(-1);
    sb.append(spc.spc).append("}").append(LS);
    return sb;
  }
  
  /**
   * Generates the ControlSignatureProcessor (annotation processor source) file.
   *
   * @param aSignatureDir - the signature directory File
   * @return OK_RC or FILE_EXISTS_RC
   * @throws IOException - if IO problem
   */
  public int genSigAnnProcFile(final File aSignatureDir) throws IOException {
    final File file = new File(aSignatureDir, sigAnnProcName + ".java");
    if (file.exists()) {
      mess.warning("File " + sigAnnProcName + ".java exists and was not overwritten");
      return FILE_EXISTS_RC;
    }
    try (PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(file), 2500))) {
      gsb.setLength(0);
      pw.print(genSigAnnProc(gsb));
      return OK_RC;
    } catch (final IOException e) {
      final String msg = "Could not generate signature annotation processor file  " + sigAnnProcName + ".java"
          + ": " + e;
      Messages.hardErr(msg);
      throw e;
    }
  }
  
  /**
   * Generates the ControlSignatureProcessor annotation processor source.
   *
   * @param aSb - the buffer to append to (will be allocated if null)
   * @return StringBuilder the given one if not null, or a new allocated one if null, completed with the hook
   *         interface source
   */
  StringBuilder genSigAnnProc(final StringBuilder aSb) {
    StringBuilder sb = aSb;
    if (sb == null) {
      sb = new StringBuilder(2500);
    }
    
    sb.append(beginHeaderComment).append(" (").append(this.getClass().getSimpleName()).append(") */")
        .append(LS);
    if (jopt.signaturePkgName != null) {
      sb.append("package ").append(jopt.signaturePkgName).append(";").append(LS).append(LS);
    }
    sb.append("import java.util.Set;").append(LS).append(LS);
    sb.append("import javax.annotation.processing.AbstractProcessor;").append(LS);
    sb.append("import javax.annotation.processing.RoundEnvironment;").append(LS);
    sb.append("import javax.annotation.processing.SupportedAnnotationTypes;").append(LS);
    sb.append("import javax.annotation.processing.SupportedSourceVersion;").append(LS);
    sb.append("import javax.lang.model.SourceVersion;").append(LS);
    sb.append("import javax.lang.model.element.Element;").append(LS);
    sb.append("import javax.lang.model.element.TypeElement;").append(LS);
    sb.append("import javax.tools.Diagnostic;").append(LS).append(LS);
    if (jopt.javaDocComments) {
      sb.append("/** ").append(LS);
      sb.append(
          " * The {@link ControlSignatureProcessor} annotation processor issues a compile error when the user")
          .append(LS);
      sb.append(
          " * visitors' visit methods annotated {@link NodeFieldsSignature} are not coded against the last")
          .append(LS);
      sb.append(" * nodes definitions.").append(LS);
      sb.append(" * <p>").append(LS);
      sb.append(" * Note: the fully qualified name of this class is a line in file").append(LS);
      sb.append(" * META-INF/services/javax.annotation.processing.Processor.").append(LS);
      sb.append(" * </p>").append(LS);
      sb.append(" *").append(LS);
      sb.append(" * @author Marc Mazas").append(LS);
      sb.append(" *  @version 1.5.0 : 02/2017 : MMa : created").append(LS);
      sb.append(" *  @version 1.5.3 : 11/2025 : MMa : NodeConstants replaced by &lt;Parser&gt;NodeConstants")
          .append(LS);
      sb.append(" */").append(LS);
    } else {
      sb.append("@SuppressWarnings(\"javadoc\")").append(LS);
    }
    sb.append("@SupportedAnnotationTypes(\"").append(jopt.signaturePkgName).append(".").append(sigAnnName)
        .append("\")").append(LS);
    sb.append("// Adapt the release to your compiler level").append(LS);
    sb.append("@SupportedSourceVersion(SourceVersion.RELEASE_8)").append(LS);
    sb.append("public class ").append(sigAnnProcName).append(" extends AbstractProcessor {").append(LS)
        .append(LS);
    
    spc = new Spacing(INDENT_AMT);
    spc.updateSpc(+1);
    if (jopt.javaDocComments) {
      sb.append(spc.spc).append("/** Standard constructor */").append(LS);
    }
    sb.append(spc.spc).append("public ").append(sigAnnProcName).append("() {").append(LS);
    spc.updateSpc(+1);
    sb.append(spc.spc).append("super();").append(LS);
    spc.updateSpc(-1);
    sb.append(spc.spc).append("}").append(LS).append(LS);
    
    spc.updateSpc(+1);
    if (jopt.javaDocComments) {
      sb.append(spc.spc).append("/** {@inheritDoc} */").append(LS);
    }
    sb.append(spc.spc).append("@Override").append(LS);
    sb.append(spc.spc).append(
        "public boolean process(@SuppressWarnings(\"unused\") final Set<? extends TypeElement> annotations,")
        .append(LS);
    sb.append(spc.spc).append("                       final RoundEnvironment roundEnv) {").append(LS);
    spc.updateSpc(+1);
    sb.append(spc.spc)
        .append("for (final Element elem : roundEnv.getElementsAnnotatedWith(NodeFieldsSignature.class)) {")
        .append(LS);
    spc.updateSpc(+1);
    sb.append(spc.spc)
        .append("final NodeFieldsSignature nfs = elem.getAnnotation(NodeFieldsSignature.class);").append(LS);
    sb.append(spc.spc).append("final int osig = nfs.old_sig();").append(LS);
    sb.append(spc.spc).append("final int nsig = nfs.new_sig();").append(LS);
    sb.append(spc.spc).append("if (osig != nsig) {").append(LS);
    spc.updateSpc(+1);
    sb.append(spc.spc)
        .append(
            "final String message = \"Different node fields signatures (old=\" + osig+ \", new=\" + nsig +")
        .append(LS);
    sb.append(spc.spc).append("    \") in ").append(jopt.visitorsPkgName)
        .append(".\" + elem.getEnclosingElement().getSimpleName() + \"#\" +").append(LS);
    sb.append(spc.spc).append("    elem.getSimpleName() + \"(final \" + nfs.name() + \" n)\";").append(LS);
    sb.append(spc.spc).append("processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, message);")
        .append(LS);
    spc.updateSpc(-1);
    sb.append(spc.spc).append("}").append(LS);
    spc.updateSpc(-1);
    sb.append(spc.spc).append("}").append(LS);
    sb.append(spc.spc).append("return true; // no further processing of this annotation type").append(LS);
    spc.updateSpc(-1);
    sb.append(spc.spc).append("}").append(LS).append(LS);
    
    spc.updateSpc(-1);
    sb.append(spc.spc).append("}").append(LS);
    return sb;
  }
}

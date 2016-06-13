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

import static EDU.purdue.jtb.misc.Globals.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;

import EDU.purdue.jtb.syntaxtree.NodeChoice;
import EDU.purdue.jtb.syntaxtree.NodeList;
import EDU.purdue.jtb.syntaxtree.NodeListOptional;
import EDU.purdue.jtb.syntaxtree.NodeOptional;
import EDU.purdue.jtb.syntaxtree.NodeSequence;
import EDU.purdue.jtb.syntaxtree.NodeTCF;
import EDU.purdue.jtb.syntaxtree.NodeToken;
import EDU.purdue.jtb.visitor.GlobalDataBuilder;

/**
 * Class DepthFirstVisitorsGeneratorForJava contains methods to generate the different DepthFirstXXXVisitor
 * visitors classes files.<br>
 * It must be constructed with the list of the grammar {@link ClassInfoForJava} classes.<br>
 * 
 * @author Marc Mazas
 * @version 1.4.0 : 05-08/2009 : MMa : adapted to JavaCC v4.2 grammar and JDK 1.5
 * @version 1.4.3.1 : 20/04/2009 : MMa : removed unnecessary @SuppressWarnings("unused") in
 *          genNodeTokenVisit
 * @version 1.4.3.3 : 27/04/2009 : MMa : put back @SuppressWarnings("unused") in genNodeTokenVisit
 * @version 1.4.6 : 01/2011 : FA : added -va and -npfx and -nsfx options
 * @version 1.4.7 : 09/2012 : MMa : extracted constants and methods ; added the reference to the
 *          {@link GlobalDataBuilder}
 */
public class DepthFirstVisitorsGeneratorForCpp extends AbstractDepthFirstVisitorsGenerator{

  /**
   * Constructor. Creates the visitor directory if it does not exist.
   * 
   * @param aClassesList - the classes list
   * @param aGdbv - the global data builder visitor
   */
  public DepthFirstVisitorsGeneratorForCpp(final List<ClassInfo> aClassesList,
                                     final GlobalDataBuilder aGdbv) {
    super(aClassesList, aGdbv);
  }

  /*
   * Visitors files generation methods
   */

  /**
   * Generates the DepthFirstRetArguVisitor file.
   * 
   * @throws FileExistsException - if the file already exists and the no overwrite flag has been set
   */
  @Override
  public void genDepthFirstRetArguVisitorFile() throws FileExistsException {
    final String outFilename = retArguVisitor.getOutfileName();
    try {
      final File file = new File(visitorDir, outFilename);
      if (noOverwrite && file.exists())
        throw new FileExistsException(outFilename);
      final PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(file), BR_BUF_SZ));
      out.print(genDepthFirstRetArguVisitor());
      out.close();
    }
    catch (final IOException e) {
      Messages.hardErr("Could not generate " + outFilename);
    }
  }

  /**
   * Generates the DepthFirstRetVisitor file.
   * 
   * @throws FileExistsException - if the file already exists and the no overwrite flag has been set
   */
  @Override
  public void genDepthFirstRetVisitorFile() throws FileExistsException {
    final String outFilename = retVisitor.getOutfileName();
   if (true)
      return;
    else
    try {
      final File file = new File(visitorDir, outFilename);
      if (noOverwrite && file.exists())
        throw new FileExistsException(outFilename);
      final PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(file), BR_BUF_SZ));
      out.print(genDepthFirstRetVisitor());
      out.close();
    }
    catch (final IOException e) {
      Messages.hardErr("Could not generate " + outFilename);
    }
  }

  /**
   * Generates the DepthFirstVoidArguVisitor file.
   * 
   * @throws FileExistsException - if the file already exists and the no overwrite flag has been set
   */
  @Override
  public void genDepthFirstVoidArguVisitorFile() throws FileExistsException {
    final String outFilename = voidArguVisitor.getOutfileName();
    if (true)
      return;
    else
    try {
      final File file = new File(visitorDir, outFilename);
      if (noOverwrite && file.exists())
        throw new FileExistsException(outFilename);
      final PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(file), BR_BUF_SZ));
      out.print(genDepthFirstVoidArguVisitor());
      out.close();
    }
    catch (final IOException e) {
      Messages.hardErr("Could not generate " + outFilename);
    }
  }

  /**
   * Generates the DepthFirstVoidVisitor file.
   * 
   * @throws FileExistsException - if the file already exists and the no overwrite flag has been set
   */
  @Override
  public void genDepthFirstVoidVisitorFile() throws FileExistsException {
    final String outFilename = voidVisitor.getOutfileName();
    if (true)
      return;
    else
    try {
      final File file = new File(visitorDir, outFilename);
      if (noOverwrite && file.exists())
        throw new FileExistsException(outFilename);
      final PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(file), BR_BUF_SZ));
      out.print(genDepthFirstVoidVisitor());
      out.close();
    }
    catch (final IOException e) {
      Messages.hardErr("Could not generate " + outFilename);
    }
  }

  /*
   * Visitors source classes generation methods
   */

  /**
   * Generates a DepthFirstRetArguVisitor class source.
   * 
   * @return the buffer with the DepthFirstRetArguVisitor class source
   */
  public StringBuilder genDepthFirstRetArguVisitor() {
    buf.setLength(0);
    buf.append(iRetArguVisitor.getClassPrefix()).append(LS);
    buf.append("class ").append(retArguVisitor);
    buf.append(": public ").append(iRetArguVisitor.getClassName()).append(iRetArguVisitor.getClassParamType());
    final String clDecl = buf.toString();
    
    final String consBeg = genRetType.concat(" visit(");
    buf.setLength(0);
    buf.append(iRetArguVisitor.getClassParamType()).append("& ").append(genNodeVar).append(", ")
       .append(varargs ? genArgusType : genArguType).append(" ").append(genArguVar).append(")");
    final String consEnd = buf.toString();
    return genAnyDepthFirstVisitor(retArguVisitor, retArguVisitorCmt, clDecl, consBeg, consEnd, true, true);
  }

  /**
   * Generates a DepthFirstRetVisitor class source.
   * 
   * @return the buffer with the DepthFirstRetArguVisitor class source
   */
  public StringBuilder genDepthFirstRetVisitor() {
    buf.setLength(0);
    buf.append(retVisitor).append("<").append(genRetType).append("> : public ")
       .append(iRetVisitor).append("<").append(genRetType).append(">");
    final String clDecl = buf.toString();
    final String consBeg = genRetType.concat(" visit(const ");
    final String consEnd = "* ".concat(genNodeVar).concat(")");
    return genAnyDepthFirstVisitor(retVisitor, retVisitorCmt, clDecl, consBeg, consEnd, true, false);
  }

  /**
   * Generates a DepthFirstVoidArguVisitor class source.
   * 
   * @return the buffer with the DepthFirstRetArguVisitor class source
   */
  public StringBuilder genDepthFirstVoidArguVisitor() {
    buf.setLength(0);
    buf.append(voidArguVisitor).append("<").append(genArguType).append("> : public ")
       .append(iVoidArguVisitor).append("<").append(genArguType).append(">");
    final String clDecl = buf.toString();
    final String consBeg = "void visit(const ";
    buf.setLength(0);
    buf.append("* ").append(genNodeVar).append(", const ")
       .append(varargs ? genArgusType : genArguType).append("* ").append(genArguVar).append(")");
    final String consEnd = buf.toString();
    return genAnyDepthFirstVisitor(voidArguVisitor, voidArguVisitorCmt, clDecl, consBeg, consEnd, false, true);
  }

  /**
   * Generates a DepthFirstVoidVisitor class source.
   * 
   * @return the buffer with the DepthFirstRetArguVisitor class source
   */
  public StringBuilder genDepthFirstVoidVisitor() {
    final String clDecl = voidVisitor.toString().concat(" : public ").concat(iVoidVisitor.getClassName());
    final String consBeg = "void visit(const ";
    final String consEnd = "* ".concat(genNodeVar).concat(")");
    return genAnyDepthFirstVisitor(voidVisitor, voidVisitorCmt, clDecl, consBeg, consEnd, false, false);
  }

  /**
   * Common method to generate all the DepthFirst visitors.
   * @param visitor TODO
   * @param aComment - a visitor type that will be inserted in the class comment
   * @param aClDecl - the class declaration
   * @param aConsBeg - the beginning of the visit methods
   * @param aConsEnd - the end of the visit methods
   * @param aRet - true if there is a user return parameter type, false otherwise
   * @param aArgu - true if there is a user argument parameter type, false otherwise
   * 
   * @return the buffer with the DepthFirst visitor class source
   */
  public StringBuilder genAnyDepthFirstVisitor(VisitorClass visitor, final String aComment,
                                               final String aClDecl, final String aConsBeg,
                                               final String aConsEnd, final boolean aRet, final boolean aArgu) {

    sb.setLength(0);
    sb.append(genFileHeaderComment()).append(LS);

//    sb.append("package ").append(visitorsPackageName).append(';').append(LS).append(LS);
//    if (!visitorsPackageName.equals(nodesPackageName))
//      sb.append("import ").append(nodesPackageName).append(".*;").append(LS);
//    sb.append("import java.util.*;").append(LS).append(LS);

    sb.append("#ifndef ").append(visitor).append("_h_").append(LS);
    sb.append("#define ").append(visitor).append("_h_").append(LS);
    sb.append("#include \"").append(visitor.getInterface().getClassName()).append(".h\"").append(LS);
    
    sb.append(LS);
    NodeClass nodes[] = { nodeChoice, nodeList, nodeListOpt, nodeOpt, nodeSeq, nodeTCF, nodeToken}; 
    for(NodeClass node : nodes) {
      sb.append("#include \"").append("../").append(nodesPackageName).append("/").append(node).append(".h\"").append(LS);
    }
    sb.append(LS);
    for (final ClassInfo classInfo : classesList) {
      sb.append("#include \"").append("../").append(nodesPackageName).append("/").append(classInfo).append(".h\"").append(LS);
    }
    sb.append(LS);
    
    if (javaDocComments) {
      sb.append("/**").append(LS);
      sb.append(" * Provides default methods which visit each node in the tree in depth-first order.<br>")
        .append(LS);
      sb.append(" * In your \"").append(aComment)
        .append("\" visitors extend this class and override part or all of these methods.")
        .append(LS);
      sb.append(" *").append(LS);
      if (aRet)
        sb.append(" * @param <").append(genRetType).append("> - The user return information type")
          .append(LS);
      if (aArgu)
        sb.append(" * @param <").append(genArguType).append("> - The user argument type")
          .append(LS);
      sb.append(" */").append(LS);
    }
    sb.append(aClDecl).append(" {").append(LS);

    spc.updateSpc(+1);

    if (depthLevel) {
      if (javaDocComments) {
        sb.append(spc.spc).append("/** The depth level (0, 1, ...) */").append(LS);
      }
      sb.append(spc.spc).append("int ").append(genDepthLevelVar).append(" = 0;").append(LS);
    }

    genBaseNodesVisitMethods(sb, spc, aRet, aArgu);

    if (javaDocComments) {
      sb.append(spc.spc).append("/*").append(LS);
      sb.append(spc.spc)
        .append(" * User grammar generated visit methods (to be overridden if necessary)")
        .append(LS);
      sb.append(spc.spc).append(" */").append(LS).append(LS);
    }
    for (final ClassInfo classInfo : classesList) {
      userNodeVisitMethod(classInfo, aConsBeg, aConsEnd, aRet, aArgu);
    }

    // end of (visitor) class
    spc.updateSpc(-1);
    sb.append("};").append(LS);
    sb.append("#endif ").append(LS);

    return sb;
  }

  /**
   * Generates a user node class visit method.
   * 
   * @param classInfo - the class data for the node to visit
   * @param aConsBeg - the beginning of the visit methods
   * @param aConsEnd - the end of the visit methods
   * @param aRet - true if there is a user return parameter type, false otherwise
   * @param aArgu - true if there is a user argument parameter type, false otherwise
   */
  private void userNodeVisitMethod(final ClassInfo classInfo, final String aConsBeg,
                           final String aConsEnd, final boolean aRet, final boolean aArgu) {
    final String className = classInfo.getClassName();

    if (javaDocComments) {
      sb.append(spc.spc).append("/**").append(LS);
      sb.append(spc.spc).append(" * Visits a {@link ").append(className)
        .append("} node, whose children are the following :").append(LS);
      sb.append(spc.spc).append(" * <p>").append(LS);
      // generate the javadoc for the class fields, with indentation of 1
      classInfo.fmtFieldsJavadocCmts(sb, spc);
      sb.append(spc.spc).append(" *").append(LS);
      sb.append(spc.spc).append(" * @param ").append(genNodeVar).append(" - the node to visit")
        .append(LS);
      if (aArgu)
        sb.append(spc.spc).append(" * @param ").append(genArguVar).append(" - the user argument")
          .append(LS);
      if (aRet)
        sb.append(spc.spc).append(" * @return the user return information").append(LS);
      sb.append(spc.spc).append(" */").append(LS);
    }

    sb.append(spc.spc).append(aConsBeg).append(className).append(aConsEnd).append(" {").append(LS);

    spc.updateSpc(+1);

    if (aRet)
      sb.append(spc.spc).append(genRetType).append(" ").append(genRetVar).append(";").append(LS);
    if (inlineAcceptMethods) {
      // inline, call visitor
      accInl.genAcceptMethods(sb, spc, classInfo, aRet, aArgu);
    } else {
      // no inlining, just direct accept calls
      final Iterator<String> fni = classInfo.getFieldNames().iterator();
      final Iterator<String> fti = classInfo.getFieldTypes().iterator();
      final Iterator<String> fii = classInfo.getFieldInitializers().iterator();
      final Iterator<String> fei = classInfo.getFieldEUTCFCodes().iterator();
      // 0 = not in catch condition, 1 = at the beginning, 2 = inside
      int inCatch = 0;
      int k = 0;
      for (; fni.hasNext();) {
        final String fn = fni.next();
        final String ft = fti.next();
        final String fi = fii.next();
        final String fe = fei.next();

        if (nodeTCF.equals(ft)) {
          // a TCF
          if (fe != null) {
            // java block
            if (inCatch == 1)
              inCatch = 2;
            else if (inCatch == 2)
              sb.append(' ');
            sb.append(fe);
            if (inCatch == 0)
              sb.append(LS);
          } else {
            final String tcfStr = fi.substring(6 + nodeTCF.getName().length(), fi.length() - 2);
            if ("try".equals(tcfStr)) {
              sb.append(spc.spc).append("try");
            } else if ("{".equals(tcfStr)) {
              sb.append(" {").append(LS);
              spc.updateSpc(+1);
            } else if ("}".equals(tcfStr)) {
              spc.updateSpc(-1);
              sb.append(spc.spc).append('}').append(LS);
            } else if ("catch".equals(tcfStr)) {
              sb.append(spc.spc).append("catch");
            } else if ("(".equals(tcfStr)) {
              inCatch = 1;
              sb.append(" (");
            } else if (")".equals(tcfStr)) {
              inCatch = 0;
              sb.append(") ");
            } else if ("finally".equals(tcfStr)) {
              sb.append(spc.spc).append("finally ");
            } else {
              // should not come here !
            }
          }
          // skip TCF comment
          k++;
        } else {
          // not a TCF
          classInfo.fmt1JavacodeFieldCmt(sb, spc, k++);
          if (depthLevel)
            increaseDepthLevel(sb, spc);
          sb.append(spc.spc);
          if (aRet) {
            sb.append(genRetVar).append(" = ");
          }

          sb.append(genNodeVar).append(".").append(fn);
          sb.append("->").append("accept(*this");
          if (aArgu)
            sb.append(", ").append(varargs? genArgsVar : genArguVar);
          sb.append(");").append(LS);
          if (depthLevel)
            decreaseDepthLevel(sb, spc);
        }
      }
    }
    if (aRet)
      sb.append(spc.spc).append("return ").append(genRetVar).append(';').append(LS);

    spc.updateSpc(-1);
    sb.append(spc.spc).append('}').append(LS).append(LS);
  }

  /**
   * Generates the base nodes classes visit methods.
   * 
   * @param aSb - the buffer to output into (will be allocated if null)
   * @param aSpc - the indentation
   * @param aRet - true if there is a user return parameter type, false otherwise
   * @param aArgu - true if there is a user argument parameter type, false otherwise
   * @return the buffer with the DepthFirst visitor class source
   */
  private StringBuilder genBaseNodesVisitMethods(final StringBuilder aSb, final Spacing aSpc,
                                                final boolean aRet, final boolean aArgu) {
    StringBuilder sb = aSb;
    if (sb == null)
      sb = new StringBuilder(4400);

    sb.append(LS);
    if (javaDocComments) {
      sb.append(aSpc.spc).append("/*").append(LS);
      sb.append(aSpc.spc)
        .append(" * Base nodes classes visit methods (to be overridden if necessary)").append(LS);
      sb.append(aSpc.spc).append(" */").append(LS).append(LS);
    }
    genNodeChoiceVisit(sb, aSpc, aRet, aArgu);
    sb.append(LS);
    genNodeListVisit(sb, aSpc, aRet, aArgu);
    sb.append(LS);
    genNodeListOptVisit(sb, aSpc, aRet, aArgu);
    sb.append(LS);
    genNodeOptVisit(sb, aSpc, aRet, aArgu);
    sb.append(LS);
    genNodeSeqVisit(sb, aSpc, aRet, aArgu);
    sb.append(LS);
    genNodeTCFVisit(sb, aSpc, aRet, aArgu);
    sb.append(LS);
    genNodeTokenVisit(sb, aSpc, aRet, aArgu);
    sb.append(LS);

    return sb;
  }

  /**
   * Generates the base node {@link NodeChoice} visit method.
   * 
   * @param aSb - the buffer to output into (will be allocated if null)
   * @param aSpc - the indentation
   * @param aRet - true if there is a user return parameter type, false otherwise
   * @param aArgu - true if there is a user argument parameter type, false otherwise
   * @return the buffer with the DepthFirst visitor class source
   */
   private StringBuilder genNodeChoiceVisit(final StringBuilder aSb, final Spacing aSpc,
                                          final boolean aRet, final boolean aArgu) {
    StringBuilder sb = aSb;
    if (sb == null)
      sb = new StringBuilder(900);

    baseNodeVisitMethodBegin(sb, aSpc, aRet, aArgu, nodeChoice);
    if (depthLevel)
      increaseDepthLevel(sb, aSpc);
    sb.append(aSpc.spc);
    if (aRet)
      sb.append(genRetType).append(" ").append(genRetVar).append(" = ");
    sb.append(genNodeVar).append(".").append("choice").append("->").append("accept(*this");
    if (aArgu)
      sb.append(", ").append(varargs ? genArgsVar : genArguVar);
    sb.append(");").append(LS);
    if (depthLevel)
      decreaseDepthLevel(sb, aSpc);
    sb.append(aSpc.spc).append("return");
    if (aRet)
      sb.append(" ").append(genRetVar);
    sb.append(';').append(LS);
    baseNodeVisitMethodCloseBrace(sb, aSpc);

    return sb;
  }

  /**
   * Generates the base node {@link NodeList} visit method.
   * 
   * @param aSb - the buffer to output into (will be allocated if null)
   * @param aSpc - the indentation
   * @param aRet - true if there is a user return parameter type, false otherwise
   * @param aArgu - true if there is a user argument parameter type, false otherwise
   * @return the buffer with the DepthFirst visitor class source
   */
   @Override
  StringBuilder genNodeListVisit(final StringBuilder aSb, final Spacing aSpc,
                                        final boolean aRet, final boolean aArgu) {
    StringBuilder sb = aSb;
    if (sb == null)
      sb = new StringBuilder(900);

    baseNodeVisitMethodBegin(sb, aSpc, aRet, aArgu, nodeList);
    if (aRet)
      sb.append(aSpc.spc).append(genRetType).append(" ").append(genRetVar).append(";")
        .append(LS);
    sb.append(aSpc.spc);
    sb.append("for (auto it = n.elements().begin(); it != n.elements().end(); it++) {").append(LS);
    aSpc.updateSpc(+1);
    if (depthLevel)
      increaseDepthLevel(sb, aSpc);
    sb.append(aSpc.spc);
    if (aRet) {
      sb.append(genRetVar).append(" = ");
    }
    sb.append("(*it)->accept(*this");
    if (aArgu)
      sb.append(", ").append(varargs ? genArgsVar : genArguVar);
    sb.append(");").append(LS);
    if (depthLevel)
      decreaseDepthLevel(sb, aSpc);
    baseNodeVisitMethodCloseBrace(sb, aSpc);
    sb.append(aSpc.spc).append("return");
    if (aRet)
      sb.append(' ').append(genRetVar);
    sb.append(';').append(LS);
    baseNodeVisitMethodCloseBrace(sb, aSpc);

    return sb;
  }

  private void beginComment(StringBuilder sb) {
     sb.append("/*").append(LS);
   }
  private void endComment(StringBuilder sb) {
     sb.append("*/").append(LS);
   }
  /**
   * Generates the base node {@link NodeListOptional} visit method.
   * 
   * @param aSb - the buffer to output into (will be allocated if null)
   * @param aSpc - the indentation
   * @param aRet - true if there is a user return parameter type, false otherwise
   * @param aArgu - true if there is a user argument parameter type, false otherwise
   * @return the buffer with the DepthFirst visitor class source
   */
   private StringBuilder genNodeListOptVisit(final StringBuilder aSb, final Spacing aSpc,
                                           final boolean aRet, final boolean aArgu) {
    StringBuilder sb = aSb;
    if (sb == null)
      sb = new StringBuilder(1100);

    baseNodeVisitMethodBegin(sb, aSpc, aRet, aArgu, nodeListOpt);
    if (aRet)
      sb.append(aSpc.spc).append(genRetType).append(" ").append(genRetVar).append(";")
        .append(LS);
    sb.append(aSpc.spc).append("if (").append(genNodeVar).append(".").append("present()) {").append(LS);
    aSpc.updateSpc(+1);
    sb.append(aSpc.spc);
    sb.append("for (auto it = n.elements().begin(); it != n.elements().end(); it++) {").append(LS);
    aSpc.updateSpc(+1);
    if (depthLevel)
      increaseDepthLevel(sb, aSpc);
    sb.append(aSpc.spc);
    if (aRet) {
      sb.append(genRetVar).append(" = ");
    }
    sb.append("(*it)->accept(*this");
    if (aArgu)
      sb.append(", ").append(varargs ? genArgsVar : genArguVar);
    sb.append(");").append(LS);
    if (depthLevel)
      decreaseDepthLevel(sb, aSpc);
    aSpc.updateSpc(-1);
    sb.append(aSpc.spc).append('}').append(LS);
    aSpc.updateSpc(-1);
    sb.append(aSpc.spc).append("}").append(LS);
    sb.append(aSpc.spc).append("return");
    if (aRet)
      sb.append(' ').append(genRetVar);
    sb.append(';').append(LS);
    baseNodeVisitMethodCloseBrace(sb, aSpc);

    return sb;
  }

  /**
   * Generates the base node {@link NodeOptional} visit method.
   * 
   * @param aSb - the buffer to output into (will be allocated if null)
   * @param aSpc - the indentation
   * @param aRet - true if there is a user return parameter type, false otherwise
   * @param aArgu - true if there is a user argument parameter type, false otherwise
   * @return the buffer with the DepthFirst visitor class source
   */
   StringBuilder genNodeOptVisit(final StringBuilder aSb, final Spacing aSpc,
                                       final boolean aRet, final boolean aArgu) {
    StringBuilder sb = aSb;
    if (sb == null)
      sb = new StringBuilder(720);

    baseNodeVisitMethodBegin(sb, aSpc, aRet, aArgu, nodeOpt);
    if (aRet)
      sb.append("  ").append(genRetType).append(" ").append(genRetVar).append(";").append(LS);
    sb.append(aSpc.spc).append("if (").append(genNodeVar).append(".").append("present()) {").append(LS);
    aSpc.updateSpc(+1);
    if (depthLevel)
      increaseDepthLevel(sb, aSpc);
    sb.append(aSpc.spc);
    sb.append(genRetVar).append(" = ").append(genNodeVar).append(".").append("node->accept(*this");
    if (aArgu)
      sb.append(", ").append(varargs ? genArgsVar : genArguVar);
    sb.append(");").append(LS);
    if (depthLevel)
      decreaseDepthLevel(sb, aSpc);
    aSpc.updateSpc(-1);
    sb.append(aSpc.spc).append("}").append(LS);
//    aSpc.updateSpc(+1);
//    sb.append(aSpc.spc).append("return").append(aRet ? " null" : "").append(';').append(LS);
//    aSpc.updateSpc(-1);
    sb.append(aSpc.spc).append("return");
    if (aRet)
      sb.append(' ').append(genRetVar);
    sb.append(';').append(LS);
    baseNodeVisitMethodCloseBrace(sb, aSpc);

    return sb;
  }

  /**
   * Generates the base node {@link NodeSequence} visit method.
   * 
   * @param aSb - the buffer to output into (will be allocated if null)
   * @param aSpc - the indentation
   * @param aRet - true if there is a user return parameter type, false otherwise
   * @param aArgu - true if there is a user argument parameter type, false otherwise
   * @return the buffer with the DepthFirst visitor class source
   */
   @Override
  StringBuilder genNodeSeqVisit(final StringBuilder aSb, final Spacing aSpc,
                                       final boolean aRet, final boolean aArgu) {
    StringBuilder sb = aSb;
    if (sb == null)
      sb = new StringBuilder(920);

    baseNodeVisitMethodBegin(sb, aSpc, aRet, aArgu, nodeSeq);
    if (aRet)
      sb.append(aSpc.spc).append(genRetType).append(" ").append(genRetVar).append(";")
        .append(LS);
    /*
     *  for(auto it = n.elements().begin(); it != n.elements().end(); it++) {
     *     r = (*it)->accept(*this, a...);
     *   }
     */
    sb.append(aSpc.spc);
    sb.append("for (auto it = n.elements().begin(); it != n.elements().end(); it++) {").append(LS);
    aSpc.updateSpc(+1);
    if (depthLevel)
      increaseDepthLevel(sb, aSpc);
    sb.append(aSpc.spc);
    if (aRet) {
      sb.append(aSpc.spc).append(genRetVar).append(" = ");
    }
    sb.append("(*it)->accept(*this");
    if (aArgu)
      sb.append(", ").append(varargs ? genArgsVar : genArguVar);
    sb.append(");").append(LS);
    if (depthLevel)
      decreaseDepthLevel(sb, aSpc);
    baseNodeVisitMethodCloseBrace(sb, aSpc);
    sb.append(aSpc.spc).append("return");
    if (aRet)
      sb.append(' ').append(genRetVar);
    sb.append(';').append(LS);
    baseNodeVisitMethodCloseBrace(sb, aSpc);

    return sb;
  }

  /**
   * Generates the base node {@link NodeToken} visit method.
   * 
   * @param aSb - the buffer to output into (will be allocated if null)
   * @param aSpc - the indentation
   * @param aRet - true if there is a user return parameter type, false otherwise
   * @param aArgu - true if there is a user argument parameter type, false otherwise
   * @return the buffer with the DepthFirst visitor class source
   */
   private StringBuilder genNodeTokenVisit(final StringBuilder aSb, final Spacing aSpc,
                                         final boolean aRet, final boolean aArgu) {
    StringBuilder sb = aSb;
    if (sb == null)
      sb = new StringBuilder(680);

    baseNodeVisitMethodBegin(sb, aSpc, aRet, aArgu, nodeToken);
    if (aRet)
      sb.append(aSpc.spc).append(genRetType).append(" ").append(genRetVar).append(";")
        .append(LS);
    sb.append(aSpc.spc).append("const string tkIm = ").append(genNodeVar).append(".").append("tokenImage;")
      .append(LS);
    sb.append(aSpc.spc).append("return");
    if (aRet)
      sb.append(' ').append(genRetVar);
    sb.append(';').append(LS);
    baseNodeVisitMethodCloseBrace(sb, aSpc);

    return sb;
  }

  /**
   * Generates the base node {@link NodeTCF} visit method.
   * 
   * @param aSb - the buffer to output into (will be allocated if null)
   * @param aSpc - the indentation
   * @param aRet - true if there is a user return parameter type, false otherwise
   * @param aArgu - true if there is a user argument parameter type, false otherwise
   * @return the buffer with the DepthFirst visitor class source
   */
   private StringBuilder genNodeTCFVisit(final StringBuilder aSb, final Spacing aSpc,
                                       final boolean aRet, final boolean aArgu) {
    StringBuilder sb = aSb;
    if (sb == null)
      sb = new StringBuilder(680);

    baseNodeVisitMethodBegin(sb, aSpc, aRet, aArgu, nodeTCF);
    if (aRet)
      sb.append(aSpc.spc).append(genRetType).append(" ").append(genRetVar).append(";")
        .append(LS);
    sb.append(aSpc.spc).append("const string tkIm = ").append(genNodeVar).append(".").append("tokenImage;")
      .append(LS);
    sb.append(aSpc.spc).append("return");
    if (aRet)
      sb.append(' ').append(genRetVar);
    sb.append(';').append(LS);
    baseNodeVisitMethodCloseBrace(sb, aSpc);

    return sb;
  }

  /**
   * Outputs the beginning of a visit method for a base node.
   * 
   * @param aSb - the buffer to output into (will be allocated if null)
   * @param aSpc - the indentation
   * @param aRet - true if there is a user return parameter type, false otherwise
   * @param aArgu - true if there is a user argument parameter type, false otherwise
   * @param nodelist - the node name
   */
  static void baseNodeVisitMethodBegin(final StringBuilder aSb, final Spacing aSpc,
                                       final boolean aRet, final boolean aArgu,
                                       final NodeClass node) {
    if (javaDocComments) {
      baseNodeVisitMethodJavadoc(aSb, aSpc, aRet, aArgu, node);
    }
    aSb.append(aSpc.spc).append(aRet ? genRetType : "void")
       .append(" visit(").append(node.getParamType(iRetArguVisitor)).append("& ").append(genNodeVar);
    if (aArgu)
      aSb.append(", ").append(varargs ? genArgusType : genArguType).append(" ")
         .append(genArguVar);
    aSb.append(") {").append(LS);
    aSpc.updateSpc(+1);
  }

  /**
   * Outputs the visit method javadoc comment for a base node.
   * 
   * @param aSb - the buffer to output into (will be allocated if null)
   * @param aSpc - the indentation
   * @param aRet - true if there is a user return parameter type, false otherwise
   * @param aArgu - true if there is a user argument parameter type, false otherwise
   * @param node - the node name
   */
  static void baseNodeVisitMethodJavadoc(final StringBuilder aSb, final Spacing aSpc,
                                         final boolean aRet, final boolean aArgu,
                                         final NodeClass node) {
    aSb.append(aSpc.spc).append("/**").append(LS);
    aSb.append(aSpc.spc).append(" * Visits a {@link ").append(node.getName()).append("} node.")
       .append(LS);
    aSb.append(aSpc.spc).append(" *").append(LS);
    aSb.append(aSpc.spc).append(" * @param ").append(genNodeVar).append(" - the node to visit")
       .append(LS);
    if (aArgu)
      aSb.append(aSpc.spc).append(" * @param ").append(genArguVar).append(" - the user argument")
         .append(LS);
    if (aRet)
      aSb.append(aSpc.spc).append(" * @return the user return information").append(LS);
    aSb.append(aSpc.spc).append(" */").append(LS);
  }

  /**
   * Outputs the closing of a brace.
   * 
   * @param aSb - the buffer to output into (will be allocated if null)
   * @param aSpc - the indentation
   */
   @Override
  void baseNodeVisitMethodCloseBrace(final StringBuilder aSb, final Spacing aSpc) {
    aSpc.updateSpc(-1);
    aSb.append(aSpc.spc).append('}').append(LS);
  }

  /**
   * Output code to decrease the depth level.
   * 
   * @param aSb - the buffer to output into (will be allocated if null)
   * @param aSpc - the indentation
   */
  @Override
  public  void increaseDepthLevel(final StringBuilder aSb, final Spacing aSpc) {
    aSb.append(aSpc.spc).append("++").append(genDepthLevelVar).append(';').append(LS);
  }

  /**
   * Output code to increase the depth level.
   * 
   * @param aSb - the buffer to output into (will be allocated if null)
   * @param aSpc - the indentation
   */
  @Override
  public  void decreaseDepthLevel(final StringBuilder aSb, final Spacing aSpc) {
    aSb.append(aSpc.spc).append("--").append(genDepthLevelVar).append(';').append(LS);
  }

  /**
   * Estimates the visitors files size.
   * 
   * @return the estimated size
   */
  @Override
  int sbBufferSize() {
    return (javaDocComments ? 600 : 300) * classesList.size();
  }

  @Override
  public String getQualifier() {
    return "->";
  }
}

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

import static EDU.purdue.jtb.misc.Globals.INDENT_AMT;
import static EDU.purdue.jtb.misc.Globals.LS;
import static EDU.purdue.jtb.misc.Globals.dFRetArguVisitor;
import static EDU.purdue.jtb.misc.Globals.dFRetVisitor;
import static EDU.purdue.jtb.misc.Globals.dFVoidArguVisitor;
import static EDU.purdue.jtb.misc.Globals.dFVoidVisitor;
import static EDU.purdue.jtb.misc.Globals.depthLevel;
import static EDU.purdue.jtb.misc.Globals.genArguType;
import static EDU.purdue.jtb.misc.Globals.genArguVar;
import static EDU.purdue.jtb.misc.Globals.genArgusType;
import static EDU.purdue.jtb.misc.Globals.genDepthLevelVar;
import static EDU.purdue.jtb.misc.Globals.genFileHeaderComment;
import static EDU.purdue.jtb.misc.Globals.genNodeVar;
import static EDU.purdue.jtb.misc.Globals.genRetType;
import static EDU.purdue.jtb.misc.Globals.genRetVar;
import static EDU.purdue.jtb.misc.Globals.iNode;
import static EDU.purdue.jtb.misc.Globals.iRetArguVisitor;
import static EDU.purdue.jtb.misc.Globals.iRetVisitor;
import static EDU.purdue.jtb.misc.Globals.iVoidArguVisitor;
import static EDU.purdue.jtb.misc.Globals.iVoidVisitor;
import static EDU.purdue.jtb.misc.Globals.inlineAcceptMethods;
import static EDU.purdue.jtb.misc.Globals.javaDocComments;
import static EDU.purdue.jtb.misc.Globals.noOverwrite;
import static EDU.purdue.jtb.misc.Globals.nodeChoice;
import static EDU.purdue.jtb.misc.Globals.nodeList;
import static EDU.purdue.jtb.misc.Globals.nodeListOpt;
import static EDU.purdue.jtb.misc.Globals.nodeOpt;
import static EDU.purdue.jtb.misc.Globals.nodeSeq;
import static EDU.purdue.jtb.misc.Globals.nodeTCF;
import static EDU.purdue.jtb.misc.Globals.nodeToken;
import static EDU.purdue.jtb.misc.Globals.nodesPackageName;
import static EDU.purdue.jtb.misc.Globals.retArguVisitorCmt;
import static EDU.purdue.jtb.misc.Globals.retVisitorCmt;
import static EDU.purdue.jtb.misc.Globals.varargs;
import static EDU.purdue.jtb.misc.Globals.visitorsDirName;
import static EDU.purdue.jtb.misc.Globals.visitorsPackageName;
import static EDU.purdue.jtb.misc.Globals.voidArguVisitorCmt;
import static EDU.purdue.jtb.misc.Globals.voidVisitorCmt;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import EDU.purdue.jtb.syntaxtree.NodeChoice;
import EDU.purdue.jtb.syntaxtree.NodeList;
import EDU.purdue.jtb.syntaxtree.NodeListOptional;
import EDU.purdue.jtb.syntaxtree.NodeOptional;
import EDU.purdue.jtb.syntaxtree.NodeSequence;
import EDU.purdue.jtb.syntaxtree.NodeTCF;
import EDU.purdue.jtb.syntaxtree.NodeToken;
import EDU.purdue.jtb.visitor.AcceptInliner;
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
public abstract class AbstractDepthFirstVisitorsGenerator implements DepthFirstVisitorsGenerator {

  /** The reference to the global data builder visitor */
  private final GlobalDataBuilder            gdbv;
  /** The classes list */
  protected final List<ClassInfo> classesList;
  /** The (generated) visitors directory */
  protected final File                 visitorDir;
  /** The BufferedReaders buffer size */
  protected static final int            BR_BUF_SZ = 16 * 1024;
  /** The (reused) buffer for reformatting javadoc comments into single line ones */
  private final static StringBuilder         cb        = new StringBuilder(512);
  /** The accept methods inliner visitor */
  protected static AcceptInliner               accInl    = null;
  /** The indentation object */
  protected final Spacing                      spc       = new Spacing(INDENT_AMT);
  /** The buffer to write to */
  protected StringBuilder                      sb        = null;
  /** An auxiliary buffer */
  protected StringBuilder                      buf       = new StringBuilder(128);

  /**
   * Constructor. Creates the visitor directory if it does not exist.
   * 
   * @param aClassesList - the classes list
   * @param aGdbv - the global data builder visitor
   */
  public AbstractDepthFirstVisitorsGenerator(final List<ClassInfo> aClassesList,
                                     final GlobalDataBuilder aGdbv) {
    classesList = aClassesList;
    gdbv = aGdbv;
    sb = new StringBuilder(sbBufferSize());

    visitorDir = new File(visitorsDirName);
    if (!visitorDir.exists())
      visitorDir.mkdir();

    if (inlineAcceptMethods)
      accInl = new AcceptInliner(this, gdbv);

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
   StringBuilder genNodeListVisit(final StringBuilder aSb, final Spacing aSpc,
                                        final boolean aRet, final boolean aArgu) {
    StringBuilder sb = aSb;
    if (sb == null)
      sb = new StringBuilder(900);

    baseNodeVisitMethodBegin(sb, aSpc, aRet, aArgu, nodeList);
    if (aRet)
      sb.append(aSpc.spc).append(genRetType).append(' ').append(genRetVar).append(" = null;")
        .append(LS);
    sb.append(aSpc.spc).append("for (final Iterator<").append(iNode).append("> e = ")
      .append(genNodeVar).append(".elements(); e.hasNext();) {").append(LS);
    aSpc.update(+1);
    if (depthLevel)
      increaseDepthLevel(sb, aSpc);
    sb.append(aSpc.spc);
    if (aRet) {
      sb.append("@SuppressWarnings(\"unused\")").append(LS);
      sb.append(aSpc.spc).append("final ").append(genRetType).append(" sRes = ");
    }
    sb.append("e.next().accept(this");
    if (aArgu)
      sb.append(", ").append(genArguVar);
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
   * Generates the base node {@link NodeSequence} visit method.
   * 
   * @param aSb - the buffer to output into (will be allocated if null)
   * @param aSpc - the indentation
   * @param aRet - true if there is a user return parameter type, false otherwise
   * @param aArgu - true if there is a user argument parameter type, false otherwise
   * @return the buffer with the DepthFirst visitor class source
   */
   StringBuilder genNodeSeqVisit(final StringBuilder aSb, final Spacing aSpc,
                                       final boolean aRet, final boolean aArgu) {
    StringBuilder sb = aSb;
    if (sb == null)
      sb = new StringBuilder(920);

    baseNodeVisitMethodBegin(sb, aSpc, aRet, aArgu, nodeSeq);
    if (aRet)
      sb.append(aSpc.spc).append(genRetType).append(' ').append(genRetVar).append(" = null;")
        .append(LS);
    sb.append(aSpc.spc).append("for (final Iterator<").append(iNode).append("> e = ")
      .append(genNodeVar).append(".elements(); e.hasNext();) {").append(LS);
    aSpc.update(+1);
    if (depthLevel)
      increaseDepthLevel(sb, aSpc);
    sb.append(aSpc.spc);
    if (aRet) {
      sb.append("@SuppressWarnings(\"unused\")").append(LS);
      sb.append(aSpc.spc).append(genRetType).append(" subRet = ");
    }
    sb.append("e.next().accept(this");
    if (aArgu)
      sb.append(", ").append(genArguVar);
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
   * Outputs the beginning of a visit method for a base node.
   * 
   * @param aSb - the buffer to output into (will be allocated if null)
   * @param aSpc - the indentation
   * @param aRet - true if there is a user return parameter type, false otherwise
   * @param aArgu - true if there is a user argument parameter type, false otherwise
   * @param aNodeName - the node name
   */
  static void baseNodeVisitMethodBegin(final StringBuilder aSb, final Spacing aSpc,
                                       final boolean aRet, final boolean aArgu,
                                       final NodeClass aNodeName) {
    if (javaDocComments) {
      baseNodeVisitMethodJavadoc(aSb, aSpc, aRet, aArgu, aNodeName);
    }
    aSb.append(aSpc.spc).append("public ").append(aRet ? genRetType : "void")
       .append(" visit(final ").append(aNodeName.getName()).append(' ').append(genNodeVar);
    if (aArgu)
      aSb.append(", final ").append(varargs ? genArgusType : genArguType).append(' ')
         .append(genArguVar);
    aSb.append(") {").append(LS);
    aSpc.update(+1);
    if (aRet) {
      aSb.append(aSpc.spc).append("/* You have to adapt which data is returned")
         .append(" (result variables below are just examples) */").append(LS);
    }
  }

  /**
   * Outputs the visit method javadoc comment for a base node.
   * 
   * @param aSb - the buffer to output into (will be allocated if null)
   * @param aSpc - the indentation
   * @param aRet - true if there is a user return parameter type, false otherwise
   * @param aArgu - true if there is a user argument parameter type, false otherwise
   * @param aNodeName - the node name
   */
  static void baseNodeVisitMethodJavadoc(final StringBuilder aSb, final Spacing aSpc,
                                         final boolean aRet, final boolean aArgu,
                                         final NodeClass aNodeName) {
    aSb.append(aSpc.spc).append("/**").append(LS);
    aSb.append(aSpc.spc).append(" * Visits a {@link ").append(aNodeName.getName()).append("} node.")
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
   void baseNodeVisitMethodCloseBrace(final StringBuilder aSb, final Spacing aSpc) {
    aSpc.update(-1);
    aSb.append(aSpc.spc).append('}').append(LS);
  }

  /**
   * Output code to decrease the depth level.
   * 
   * @param aSb - the buffer to output into (will be allocated if null)
   * @param aSpc - the indentation
   */
  public  void increaseDepthLevel(final StringBuilder aSb, final Spacing aSpc) {
    aSb.append(aSpc.spc).append("++").append(genDepthLevelVar).append(';').append(LS);
  }

  /**
   * Output code to increase the depth level.
   * 
   * @param aSb - the buffer to output into (will be allocated if null)
   * @param aSpc - the indentation
   */
  public void decreaseDepthLevel(final StringBuilder aSb, final Spacing aSpc) {
    aSb.append(aSpc.spc).append("--").append(genDepthLevelVar).append(';').append(LS);
  }

  /**
   * Estimates the visitors files size.
   * 
   * @return the estimated size
   */
  int sbBufferSize() {
    return (javaDocComments ? 600 : 300) * classesList.size();
  }
}

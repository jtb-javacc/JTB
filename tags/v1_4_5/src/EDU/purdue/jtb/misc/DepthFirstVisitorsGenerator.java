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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;

import EDU.purdue.jtb.syntaxtree.NodeChoice;
import EDU.purdue.jtb.syntaxtree.NodeList;
import EDU.purdue.jtb.syntaxtree.NodeListOptional;
import EDU.purdue.jtb.syntaxtree.NodeOptional;
import EDU.purdue.jtb.syntaxtree.NodeSequence;
import EDU.purdue.jtb.syntaxtree.NodeToken;
import EDU.purdue.jtb.visitor.AcceptInliner;

/**
 * Class DepthFirstVisitorsGenerator contains methods to generate the different DepthFirstXXXVisitor
 * visitors classes files.<br>
 * It must be constructed with the list of the grammar {@link ClassInfo} classes.<br>
 * 
 * @author Marc Mazas, mmazas@sopragroup.com
 * @version 1.4.0 : 05-08/2009 : MMa : adapted to JavaCC v4.2 grammar and JDK 1.5
 * @version 1.4.3.1 : 20/04/2009 : MMa : removed unnecessary @SuppressWarnings("unused") in
 *          genNodeTokenVisit
 * @version 1.4.3.3 : 27/04/2009 : MMa : put back @SuppressWarnings("unused") in genNodeTokenVisit
 */
public class DepthFirstVisitorsGenerator {

  /** The classes list */
  private final ArrayList<ClassInfo> classesList;
  /** The (generated) visitors directory */
  private final File                 visitorDir;
  /** The BufferedReaders buffer size */
  public static final int            BR_BUF_SZ = 16 * 1024;
  /** The OS line separator */
  public static final String         LS        = System.getProperty("line.separator");
  /** The javadoc break plus the OS line separator */
  public static final String         BRLS      = "<br>" + LS;
  /** The javadoc break plus the OS line separator string lenght */
  public static final int            BRLSLEN   = BRLS.length();
  /** The (reused) buffer for reformatting javadoc comments into single line ones */
  final StringBuilder                cb        = new StringBuilder(512);
  /** The accept methods inliner visitor */
  static AcceptInliner               al        = null;
  /** The indentation object */
  final Spacing                      spc       = new Spacing(Globals.INDENT_AMT);
  /** The buffer to write to */
  StringBuilder                      sb        = null;

  /**
   * Constructor. Creates the visitor directory if it does not exist.
   * 
   * @param classes the classes list
   */
  public DepthFirstVisitorsGenerator(final ArrayList<ClassInfo> classes) {
    classesList = classes;
    sb = new StringBuilder(sbBufferSize());

    visitorDir = new File(Globals.visitorsDirName);
    if (!visitorDir.exists())
      visitorDir.mkdir();

    if (Globals.inlineAcceptMethods)
      al = new AcceptInliner();
  }

  /*
   * Visitors files generation methods
   */

  /**
   * Generates the DepthFirstRetArguVisitor file.
   * 
   * @throws FileExistsException if the file already exists and the no overwrite flag has been set
   */
  public void genDepthFirstRetArguVisitorFile() throws FileExistsException {
    final String outFilename = Globals.dFRetArguVisitorName + ".java";
    try {
      final File file = new File(visitorDir, outFilename);
      if (Globals.noOverwrite && file.exists())
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
   * @throws FileExistsException if the file already exists and the no overwrite flag has been set
   */
  public void genDepthFirstRetVisitorFile() throws FileExistsException {
    final String outFilename = Globals.dFRetVisitorName + ".java";
    try {
      final File file = new File(visitorDir, outFilename);
      if (Globals.noOverwrite && file.exists())
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
   * @throws FileExistsException if the file already exists and the no overwrite flag has been set
   */
  public void genDepthFirstVoidArguVisitorFile() throws FileExistsException {
    final String outFilename = Globals.dFVoidArguVisitorName + ".java";
    try {
      final File file = new File(visitorDir, outFilename);
      if (Globals.noOverwrite && file.exists())
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
   * @throws FileExistsException if the file already exists and the no overwrite flag has been set
   */
  public void genDepthFirstVoidVisitorFile() throws FileExistsException {
    final String outFilename = Globals.dFVoidVisitorName + ".java";
    try {
      final File file = new File(visitorDir, outFilename);
      if (Globals.noOverwrite && file.exists())
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
    final String clDecl = Globals.dFRetArguVisitorName.concat("<").concat(Globals.genRetType)
                                                      .concat(", ").concat(Globals.genArguType)
                                                      .concat("> implements ")
                                                      .concat(Globals.iRetArguVisitorName)
                                                      .concat("<").concat(Globals.genRetType)
                                                      .concat(", ").concat(Globals.genArguType)
                                                      .concat(">");
    final String consBeg = Globals.genRetType.concat(" visit(final ");
    final String consEnd = " n, final ".concat(Globals.genArguType).concat(" argu)");
    return genAnyDepthFirstVisitor(Globals.retArguVisitorComment, clDecl, consBeg, consEnd, true,
                                   true);
  }

  /**
   * Generates a DepthFirstRetVisitor class source.
   * 
   * @return the buffer with the DepthFirstRetArguVisitor class source
   */
  public StringBuilder genDepthFirstRetVisitor() {
    final String clDecl = Globals.dFRetVisitorName.concat("<").concat(Globals.genRetType)
                                                  .concat("> implements ")
                                                  .concat(Globals.iRetVisitorName).concat("<")
                                                  .concat(Globals.genRetType).concat(">");
    final String consBeg = Globals.genRetType.concat(" visit(final ");
    final String consEnd = " n)";
    return genAnyDepthFirstVisitor(Globals.retVisitorComment, clDecl, consBeg, consEnd, true, false);
  }

  /**
   * Generates a DepthFirstVoidArguVisitor class source.
   * 
   * @return the buffer with the DepthFirstRetArguVisitor class source
   */
  public StringBuilder genDepthFirstVoidArguVisitor() {
    final String clDecl = Globals.dFVoidArguVisitorName.concat("<").concat(Globals.genArguType)
                                                       .concat("> implements ")
                                                       .concat(Globals.iVoidArguVisitorName)
                                                       .concat("<").concat(Globals.genArguType)
                                                       .concat(">");
    final String consBeg = "void visit(final ";
    final String consEnd = " n, final ".concat(Globals.genArguType).concat(" argu)");
    return genAnyDepthFirstVisitor(Globals.voidArguVisitorComment, clDecl, consBeg, consEnd, false,
                                   true);
  }

  /**
   * Generates a DepthFirstVoidVisitor class source.
   * 
   * @return the buffer with the DepthFirstRetArguVisitor class source
   */
  public StringBuilder genDepthFirstVoidVisitor() {
    final String clDecl = Globals.dFVoidVisitorName.concat(" implements ")
                                                   .concat(Globals.iVoidVisitorName);
    final String consBeg = "void visit(final ";
    final String consEnd = " n)";
    return genAnyDepthFirstVisitor(Globals.voidVisitorComment, clDecl, consBeg, consEnd, false,
                                   false);
  }

  /**
   * Common method to generate all the DepthFirst visitors.
   * 
   * @param aComment a visitor type that will be inserted in the class comment
   * @param aClDecl the class declaration
   * @param aConsBeg the beginning of the visit methods
   * @param aConsEnd the end of the visit methods
   * @param aRet true if there is a user return parameter type, false otherwise
   * @param aArgu true if there is a user argument parameter type, false otherwise
   * @return the buffer with the DepthFirst visitor class source
   */
  public StringBuilder genAnyDepthFirstVisitor(final String aComment, final String aClDecl,
                                               final String aConsBeg, final String aConsEnd,
                                               final boolean aRet, final boolean aArgu) {

    sb.setLength(0);
    sb.append(Globals.genFileHeaderComment()).append(LS);

    sb.append("package ").append(Globals.visitorsPackageName).append(";").append(LS).append(LS);
    if (!Globals.visitorsPackageName.equals(Globals.nodesPackageName))
      sb.append("import ").append(Globals.nodesPackageName).append(".*;").append(LS);
    sb.append("import java.util.*;").append(LS).append(LS);

    if (Globals.javaDocComments) {
      sb.append("/**").append(LS);
      sb
        .append(
                " * Provides default methods which visit each node in the tree in depth-first order.<br>")
        .append(LS);
      sb.append(" * In your \"").append(aComment)
        .append("\" visitors extend this class and override part or all of these methods.")
        .append(LS);
      sb.append(" *").append(LS);
      if (aRet)
        sb.append(" * @param <R> The user return information type").append(LS);
      if (aArgu)
        sb.append(" * @param <A> The user argument type").append(LS);
      sb.append(" */").append(LS);
    }
    sb.append("public class ").append(aClDecl).append(" {").append(LS).append(LS);

    spc.updateSpc(+1);

    if (Globals.depthLevel) {
      if (Globals.javaDocComments) {
        sb.append(spc.spc).append("/** The depth level (0, 1, ...) */").append(LS);
      }
      sb.append(spc.spc).append("int depthLevel = 0;").append(LS);
    }

    genBaseNodesVisitMethods(sb, spc, aRet, aArgu);

    if (Globals.javaDocComments) {
      sb.append(spc.spc).append("/*").append(LS);
      sb.append(spc.spc)
        .append(" * User grammar generated visit methods (to be overridden if necessary)")
        .append(LS);
      sb.append(spc.spc).append(" */").append(LS).append(LS);
    }
    for (final Iterator<ClassInfo> e = classesList.iterator(); e.hasNext();) {
      final ClassInfo classInfo = e.next();
      final String className = classInfo.getClassName();

      if (Globals.javaDocComments) {
        sb.append(spc.spc).append("/**").append(LS);
        sb.append(spc.spc).append(" * Visits a {@link ").append(className)
          .append("} node, whose children are the following :").append(LS);
        sb.append(spc.spc).append(" * <p>").append(LS);
        sb.append(classInfo.genAllFieldsComment(spc));
        sb.append(spc.spc).append(" *").append(LS);
        sb.append(spc.spc).append(" * @param n the node to visit").append(LS);
        if (aArgu)
          sb.append(spc.spc).append(" * @param argu the user argument").append(LS);
        if (aRet)
          sb.append(spc.spc).append(" * @return the user return information").append(LS);
        sb.append(spc.spc).append(" */").append(LS);
      }

      sb.append(spc.spc).append("public ").append(aConsBeg).append(className).append(aConsEnd)
        .append(" {").append(LS);

      spc.updateSpc(+1);

      if (aRet)
        sb.append(spc.spc).append(Globals.genRetType).append(" nRes = null;").append(LS);
      if (Globals.inlineAcceptMethods) {
        // inline, call visitor
        al.genAcceptMethods(sb, spc, classInfo, aRet, aArgu);
      } else {
        // no inlining, just direct accept calls
        // TODO for EUT3, try / catch / finally java code should be printed !!!
        final Iterator<String> fni = classInfo.getFieldNames().iterator();
        Iterator<String> fci = null;
        if (Globals.javaDocComments)
          fci = classInfo.getFieldComments().iterator();
        for (; fni.hasNext();) {
          final String fn = fni.next();
          if (fci != null)
            sb.append(spc.spc).append(fmtComment(fci.next())).append(LS);
          if (Globals.depthLevel)
            sb.append(spc.spc).append("++depthLevel;").append(LS);
          sb.append(spc.spc).append("n.").append(fn);
          sb.append(".accept(this").append(aArgu ? ", argu" : "").append(");").append(LS);
          if (Globals.depthLevel)
            sb.append(spc.spc).append("--depthLevel;").append(LS);
        }
      }
      if (aRet)
        sb.append(spc.spc).append("return nRes;").append(LS);

      spc.updateSpc(-1);
      sb.append(spc.spc).append("}").append(LS).append(LS);
    }

    spc.updateSpc(-1);
    sb.append("}").append(LS);

    return sb;
  }

  /**
   * Reformats javadoc comments into single line ones.
   * 
   * @param com the javadoc comment
   * @return the buffer
   */
  StringBuilder fmtComment(final String com) {
    cb.setLength(0);
    final int cl = com.length();
    for (int i = 0; i < cl - 3;) {
      if (" * ".equals(com.substring(i, i + 3))) {
        cb.append("// ");
        i += 3;
      } else if (BRLS.equals(com.substring(i, i + BRLSLEN))) {
        if (i + BRLSLEN < cl)
          cb.append(LS).append(spc.spc);
        i += BRLSLEN;
      } else {
        cb.append(com.charAt(i));
        i++;
      }
    }
    return cb;
  }

  /*
   * The base classes visit methods
   */

  /**
   * Generates the base nodes classes visit methods.
   * 
   * @param aSB the buffer to output into (will be allocated if null)
   * @param spc the indentation
   * @param aRet true if there is a user return parameter type, false otherwise
   * @param aArgu true if there is a user argument parameter type, false otherwise
   * @return the buffer with the DepthFirst visitor class source
   */
  static StringBuilder genBaseNodesVisitMethods(final StringBuilder aSB, final Spacing spc,
                                                final boolean aRet, final boolean aArgu) {
    StringBuilder sb = aSB;
    if (sb == null)
      sb = new StringBuilder(4400);

    sb.append(LS);
    if (Globals.javaDocComments) {
      sb.append(spc.spc).append("/*").append(LS);
      sb.append(spc.spc)
        .append(" * Base nodes classes visit methods (to be overridden if necessary)").append(LS);
      sb.append(spc.spc).append(" */").append(LS).append(LS);
    }
    genNodeChoiceVisit(sb, spc, aRet, aArgu);
    sb.append(LS);
    genNodeListVisit(sb, spc, aRet, aArgu);
    sb.append(LS);
    genNodeListOptVisit(sb, spc, aRet, aArgu);
    sb.append(LS);
    genNodeOptVisit(sb, spc, aRet, aArgu);
    sb.append(LS);
    genNodeSeqVisit(sb, spc, aRet, aArgu);
    sb.append(LS);
    genNodeTokenVisit(sb, spc, aRet, aArgu);
    sb.append(LS);

    return sb;
  }

  /**
   * Generates the base node {@link NodeChoice} visit method.
   * 
   * @param aSB the buffer to output into (will be allocated if null)
   * @param spc the indentation
   * @param aRet true if there is a user return parameter type, false otherwise
   * @param aArgu true if there is a user argument parameter type, false otherwise
   * @return the buffer with the DepthFirst visitor class source
   */
  static StringBuilder genNodeChoiceVisit(final StringBuilder aSB, final Spacing spc,
                                          final boolean aRet, final boolean aArgu) {
    StringBuilder sb = aSB;
    if (sb == null)
      sb = new StringBuilder(900);

    if (Globals.javaDocComments) {
      sb.append(spc.spc).append("/**").append(LS);
      sb.append(spc.spc).append(" * Visits a {@link NodeChoice} node")
        .append(aArgu ? ", passing it an argument." : ".").append(LS);
      sb.append(spc.spc).append(" *").append(LS);
      sb.append(spc.spc).append(" * @param n the node to visit").append(LS);
      if (aArgu)
        sb.append(spc.spc).append(" * @param argu the user argument").append(LS);
      if (aRet)
        sb.append(spc.spc).append(" * @return the user return information").append(LS);
      sb.append(spc.spc).append(" */").append(LS);
    }
    sb.append(spc.spc).append("public ").append(aRet ? Globals.genRetType : "void")
      .append(" visit(final ").append(Globals.nodeChoiceName).append(" n");
    if (aArgu)
      sb.append(", final ").append(Globals.genArguType).append(" argu");
    sb.append(") {").append(LS);
    spc.updateSpc(+1);
    if (Globals.depthLevel)
      sb.append(spc.spc).append("++depthLevel;").append(LS);
    if (aRet) {
      sb.append(spc.spc).append("final ").append(Globals.genRetType)
        .append(" nRes = n.choice.accept(this").append(aArgu ? ", argu" : "").append(");")
        .append(LS);
      if (Globals.depthLevel)
        sb.append(spc.spc).append("--depthLevel;").append(LS);
      sb.append(spc.spc).append("return nRes;").append(LS);
    } else {
      sb.append(spc.spc).append("n.choice.accept(this").append(aArgu ? ", argu" : "").append(");")
        .append(LS);
      if (Globals.depthLevel)
        sb.append(spc.spc).append("--depthLevel;").append(LS);
      sb.append(spc.spc).append("return;").append(LS);
    }
    spc.updateSpc(-1);
    sb.append(spc.spc).append("}").append(LS);

    return sb;
  }

  /**
   * Generates the base node {@link NodeList} visit method.
   * 
   * @param aSB the buffer to output into (will be allocated if null)
   * @param spc the indentation
   * @param aRet true if there is a user return parameter type, false otherwise
   * @param aArgu true if there is a user argument parameter type, false otherwise
   * @return the buffer with the DepthFirst visitor class source
   */
  static StringBuilder genNodeListVisit(final StringBuilder aSB, final Spacing spc,
                                        final boolean aRet, final boolean aArgu) {
    StringBuilder sb = aSB;
    if (sb == null)
      sb = new StringBuilder(900);

    if (Globals.javaDocComments) {
      sb.append(spc.spc).append("/**").append(LS);
      sb.append(spc.spc).append(" * Visits a {@link NodeList} node")
        .append(aArgu ? ", passing it an argument." : ".").append(LS);
      sb.append(spc.spc).append(" *").append(LS);
      sb.append(spc.spc).append(" * @param n the node to visit").append(LS);
      if (aArgu)
        sb.append(spc.spc).append(" * @param argu the user argument").append(LS);
      if (aRet)
        sb.append(spc.spc).append(" * @return the user return information").append(LS);
      sb.append(spc.spc).append(" */").append(LS);
    }
    sb.append(spc.spc).append("public ").append(aRet ? Globals.genRetType : "void")
      .append(" visit(final ").append(Globals.nodeListName).append(" n");
    if (aArgu)
      sb.append(", final ").append(Globals.genArguType).append(" argu");
    sb.append(") {").append(LS);
    spc.updateSpc(+1);
    if (aRet)
      sb.append(spc.spc).append(Globals.genRetType).append(" nRes = null;").append(LS);
    sb.append(spc.spc).append("for (final Iterator<").append(Globals.iNodeName)
      .append("> e = n.elements(); e.hasNext();) {").append(LS);
    spc.updateSpc(+1);
    if (Globals.depthLevel)
      sb.append(spc.spc).append("++depthLevel;").append(LS);
    sb.append(spc.spc);
    if (aRet) {
      sb.append("@SuppressWarnings(\"unused\")").append(LS);
      sb.append(spc.spc).append("final ").append(Globals.genRetType).append(" sRes = ");
    }
    sb.append("e.next().accept(this").append(aArgu ? ", argu" : "").append(");").append(LS);
    if (Globals.depthLevel)
      sb.append(spc.spc).append("--depthLevel;").append(LS);
    spc.updateSpc(-1);
    sb.append(spc.spc).append("}").append(LS);
    sb.append(spc.spc).append("return").append(aRet ? " nRes" : "").append(";").append(LS);
    spc.updateSpc(-1);
    sb.append(spc.spc).append("}").append(LS);

    return sb;
  }

  /**
   * Generates the base node {@link NodeListOptional} visit method.
   * 
   * @param aSB the buffer to output into (will be allocated if null)
   * @param spc the indentation
   * @param aRet true if there is a user return parameter type, false otherwise
   * @param aArgu true if there is a user argument parameter type, false otherwise
   * @return the buffer with the DepthFirst visitor class source
   */
  static StringBuilder genNodeListOptVisit(final StringBuilder aSB, final Spacing spc,
                                           final boolean aRet, final boolean aArgu) {
    StringBuilder sb = aSB;
    if (sb == null)
      sb = new StringBuilder(1100);

    if (Globals.javaDocComments) {
      sb.append(spc.spc).append("/**").append(LS);
      sb.append(spc.spc).append(" * Visits a {@link NodeListOptional} node")
        .append(aArgu ? ", passing it an argument." : ".").append(LS);
      sb.append(spc.spc).append(" *").append(LS);
      sb.append(spc.spc).append(" * @param n the node to visit").append(LS);
      if (aArgu)
        sb.append(spc.spc).append(" * @param argu the user argument").append(LS);
      if (aRet)
        sb.append(spc.spc).append(" * @return the user return information").append(LS);
      sb.append(spc.spc).append(" */").append(LS);
    }
    sb.append(spc.spc).append("public ").append(aRet ? Globals.genRetType : "void")
      .append(" visit(final ").append(Globals.nodeListOptName).append(" n");
    if (aArgu)
      sb.append(", final ").append(Globals.genArguType).append(" argu");
    sb.append(") {").append(LS);
    spc.updateSpc(+1);
    sb.append(spc.spc).append("if (n.present()) {").append(LS);
    spc.updateSpc(+1);
    if (aRet)
      sb.append(spc.spc).append(Globals.genRetType).append(" nRes = null;").append(LS);
    sb.append(spc.spc).append("for (final Iterator<").append(Globals.iNodeName)
      .append("> e = n.elements(); e.hasNext();) {").append(LS);
    spc.updateSpc(+1);
    if (Globals.depthLevel)
      sb.append(spc.spc).append("++depthLevel;").append(LS);
    sb.append(spc.spc);
    if (aRet) {
      sb.append("@SuppressWarnings(\"unused\")").append(LS);
      sb.append(spc.spc).append(Globals.genRetType).append(" sRes = ");
    }
    sb.append("e.next().accept(this").append(aArgu ? ", argu" : "").append(");").append(LS);
    if (Globals.depthLevel)
      sb.append(spc.spc).append("--depthLevel;").append(LS);
    sb.append(spc.spc).append("}").append(LS);
    spc.updateSpc(-1);
    sb.append(spc.spc).append("return").append(aRet ? " nRes" : "").append(";").append(LS);
    spc.updateSpc(-1);
    sb.append(spc.spc).append("} else").append(LS);
    spc.updateSpc(+1);
    sb.append(spc.spc).append("return").append(aRet ? " null" : "").append(";").append(LS);
    spc.updateSpc(-1);
    spc.updateSpc(-1);
    sb.append(spc.spc).append("}").append(LS);

    return sb;
  }

  /**
   * Generates the base node {@link NodeOptional} visit method.
   * 
   * @param aSB the buffer to output into (will be allocated if null)
   * @param spc the indentation
   * @param aRet true if there is a user return parameter type, false otherwise
   * @param aArgu true if there is a user argument parameter type, false otherwise
   * @return the buffer with the DepthFirst visitor class source
   */
  static StringBuilder genNodeOptVisit(final StringBuilder aSB, final Spacing spc,
                                       final boolean aRet, final boolean aArgu) {
    StringBuilder sb = aSB;
    if (sb == null)
      sb = new StringBuilder(720);

    if (Globals.javaDocComments) {
      sb.append(spc.spc).append("/**").append(LS);
      sb.append(spc.spc).append(" * Visits a {@link NodeOptional} node")
        .append(aArgu ? ", passing it an argument." : ".").append(LS);
      sb.append(spc.spc).append(" *").append(LS);
      sb.append(spc.spc).append(" * @param n the node to visit").append(LS);
      if (aArgu)
        sb.append(spc.spc).append(" * @param argu the user argument").append(LS);
      if (aRet)
        sb.append(spc.spc).append(" * @return the user return information").append(LS);
      sb.append(spc.spc).append(" */").append(LS);
    }
    sb.append(spc.spc).append("public ").append(aRet ? Globals.genRetType : "void")
      .append(" visit(final ").append(Globals.nodeOptName).append(" n");
    if (aArgu)
      sb.append(", final ").append(Globals.genArguType).append(" argu");
    sb.append(") {").append(LS);
    spc.updateSpc(+1);
    if (aRet) {
      sb.append(spc.spc).append("if (n.present()) {").append(LS);
      spc.updateSpc(+1);
      if (Globals.depthLevel)
        sb.append(spc.spc).append("++depthLevel;").append(LS);
      sb.append(spc.spc).append("final ").append(Globals.genRetType)
        .append(" nRes = n.node.accept(this").append(aArgu ? ", argu" : "").append(");").append(LS);
      if (Globals.depthLevel)
        sb.append(spc.spc).append("--depthLevel;").append(LS);
      sb.append(spc.spc).append("return nRes;").append(LS);
      spc.updateSpc(-1);
      sb.append(spc.spc).append("} else").append(LS);
    } else {
      sb.append(spc.spc).append("if (n.present()) {").append(LS);
      spc.updateSpc(+1);
      if (Globals.depthLevel)
        sb.append(spc.spc).append("++depthLevel;").append(LS);
      sb.append(spc.spc).append("n.node.accept(this").append(aArgu ? ", argu" : "").append(");")
        .append(LS);
      if (Globals.depthLevel)
        sb.append(spc.spc).append("--depthLevel;").append(LS);
      sb.append(spc.spc).append("return;").append(LS);
      spc.updateSpc(-1);
      sb.append(spc.spc).append("} else").append(LS);
    }
    sb.append(spc.spc).append("return").append(aRet ? " null" : "").append(";").append(LS);
    spc.updateSpc(-1);
    sb.append(spc.spc).append("}").append(LS);

    return sb;
  }

  /**
   * Generates the base node {@link NodeSequence} visit method.
   * 
   * @param aSB the buffer to output into (will be allocated if null)
   * @param spc the indentation
   * @param aRet true if there is a user return parameter type, false otherwise
   * @param aArgu true if there is a user argument parameter type, false otherwise
   * @return the buffer with the DepthFirst visitor class source
   */
  static StringBuilder genNodeSeqVisit(final StringBuilder aSB, final Spacing spc,
                                       final boolean aRet, final boolean aArgu) {
    StringBuilder sb = aSB;
    if (sb == null)
      sb = new StringBuilder(920);

    if (Globals.javaDocComments) {
      sb.append(spc.spc).append("/**").append(LS);
      sb.append(spc.spc).append(" * Visits a {@link NodeSequence} node")
        .append(aArgu ? ", passing it an argument." : ".").append(LS);
      sb.append(spc.spc).append(" *").append(LS);
      sb.append(spc.spc).append(" * @param n the node to visit").append(LS);
      if (aArgu)
        sb.append(spc.spc).append(" * @param argu the user argument").append(LS);
      if (aRet)
        sb.append(spc.spc).append(" * @return the user return information").append(LS);
      sb.append(spc.spc).append(" */").append(LS);
    }
    sb.append(spc.spc).append("public ").append(aRet ? Globals.genRetType : "void")
      .append(" visit(final ").append(Globals.nodeSeqName).append(" n");
    if (aArgu)
      sb.append(", final ").append(Globals.genArguType).append(" argu");
    sb.append(") {").append(LS);
    spc.updateSpc(+1);
    if (aRet)
      sb.append(spc.spc).append(Globals.genRetType).append(" nRes = null;").append(LS);
    sb.append(spc.spc).append("for (final Iterator<").append(Globals.iNodeName)
      .append("> e = n.elements(); e.hasNext();) {").append(LS);
    spc.updateSpc(+1);
    if (Globals.depthLevel)
      sb.append(spc.spc).append("++depthLevel;").append(LS);
    sb.append(spc.spc);
    if (aRet) {
      sb.append("@SuppressWarnings(\"unused\")").append(LS);
      sb.append(spc.spc).append(Globals.genRetType).append(" subRet = ");
    }
    sb.append("e.next().accept(this").append(aArgu ? ", argu" : "").append(");").append(LS);
    if (Globals.depthLevel)
      sb.append(spc.spc).append("--depthLevel;").append(LS);
    spc.updateSpc(-1);
    sb.append(spc.spc).append("}").append(LS);
    sb.append(spc.spc).append("return").append(aRet ? " nRes" : "").append(";").append(LS);
    spc.updateSpc(-1);
    sb.append(spc.spc).append("}").append(LS);

    return sb;
  }

  /**
   * Generates the base node {@link NodeToken} visit method.
   * 
   * @param aSB the buffer to output into (will be allocated if null)
   * @param spc the indentation
   * @param aRet true if there is a user return parameter type, false otherwise
   * @param aArgu true if there is a user argument parameter type, false otherwise
   * @return the buffer with the DepthFirst visitor class source
   */
  static StringBuilder genNodeTokenVisit(final StringBuilder aSB, final Spacing spc,
                                         final boolean aRet, final boolean aArgu) {
    StringBuilder sb = aSB;
    if (sb == null)
      sb = new StringBuilder(680);

    if (Globals.javaDocComments) {
      sb.append(spc.spc).append("/**").append(LS);
      sb.append(spc.spc).append(" * Visits a {@link NodeToken} node")
        .append(aArgu ? ", passing it an argument." : ".").append(LS);
      sb.append(spc.spc).append(" *").append(LS);
      sb.append(spc.spc).append(" * @param n the node to visit").append(LS);
      if (aArgu)
        sb.append(spc.spc).append(" * @param argu the user argument").append(LS);
      if (aRet)
        sb.append(spc.spc).append(" * @return the user return information").append(LS);
      sb.append(spc.spc).append(" */").append(LS);
    }
    sb.append(spc.spc).append("public ").append(aRet ? Globals.genRetType : "void")
      .append(" visit(final ").append(Globals.nodeTokenName).append(" n");
    if (aArgu)
      sb.append(", @SuppressWarnings(\"unused\") final ").append(Globals.genArguType)
        .append(" argu");
    sb.append(") {").append(LS);
    spc.updateSpc(+1);
    if (aRet)
      sb.append(spc.spc).append(Globals.genRetType).append(" nRes = null;").append(LS);
    sb.append(spc.spc).append("@SuppressWarnings(\"unused\")").append(LS);
    sb.append(spc.spc).append("final String tkIm = n.tokenImage;").append(LS);
    sb.append(spc.spc).append("return").append(aRet ? " nRes" : "").append(";").append(LS);
    spc.updateSpc(-1);
    sb.append(spc.spc).append("}").append(LS);

    return sb;
  }

  /**
   * Estimates the visitors files size.
   * 
   * @return the estimated size
   */
  int sbBufferSize() {
    return (Globals.javaDocComments ? 600 : 300) * classesList.size();
  }
}

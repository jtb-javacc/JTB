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
import static EDU.purdue.jtb.misc.Globals.genArguType;
import static EDU.purdue.jtb.misc.Globals.genArgusType;
import static EDU.purdue.jtb.misc.Globals.genFileHeaderComment;
import static EDU.purdue.jtb.misc.Globals.genRetType;
import static EDU.purdue.jtb.misc.Globals.iNode;
import static EDU.purdue.jtb.misc.Globals.iNodeList;
import static EDU.purdue.jtb.misc.Globals.iRetArguVisitor;
import static EDU.purdue.jtb.misc.Globals.iRetVisitor;
import static EDU.purdue.jtb.misc.Globals.iVoidArguVisitor;
import static EDU.purdue.jtb.misc.Globals.iVoidVisitor;
import static EDU.purdue.jtb.misc.Globals.javaDocComments;
import static EDU.purdue.jtb.misc.Globals.noOverwrite;
import static EDU.purdue.jtb.misc.Globals.nodeChoice;
import static EDU.purdue.jtb.misc.Globals.nodeList;
import static EDU.purdue.jtb.misc.Globals.nodeListOpt;
import static EDU.purdue.jtb.misc.Globals.nodeOpt;
import static EDU.purdue.jtb.misc.Globals.nodeSeq;
import static EDU.purdue.jtb.misc.Globals.nodeTCF;
import static EDU.purdue.jtb.misc.Globals.nodeToken;
import static EDU.purdue.jtb.misc.Globals.astNodesDirName;
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

/**
 * Class FilesGeneratorForJava contains methods to generate the grammar (user) nodes java files, the base
 * nodes java files, the visitor interfaces and the default visitors classes files.<br>
 * It must be constructed with the list of the grammar {@link ClassInfoForJava} classes.
 * 
 * @author Marc Mazas
 * @version 1.4.0 : 05-08/2009 : MMa : adapted to JavaCC v4.2 grammar and JDK 1.5<br>
 *          1.4.0 : 11/2009 : MMa : fixed directories creation errors
 * @version 1.4.6 : 01/2011 : FA/MMa : added -va and -npfx and -nsfx options
 * @version 1.4.7 : 09/2012 : MMa : added missing generated visit methods (NodeChoice and NodeTCF)
 */
@SuppressWarnings("javadoc")
public class FilesGeneratorForJava extends AbstractFilesGenerator {
  private BaseClasses                baseClasses;

  public FilesGeneratorForJava(final List<ClassInfo> classesList) {
    super(classesList);
    baseClasses = new BaseClassesForJava();

  }

  /**
   * Outputs the formatted nodes classes list.
   * 
   * @param out - a PrintWriter to output on
   */
  @Override
  public void outputFormattedNodesClassesList(final PrintWriter out) {
    final StringBuilder sb = null;
    out.print(formatNodesClassesList(sb));
    out.flush();
  }

  /**
   * Formats the nodes classes list.
   * 
   * @param aSb - a buffer, used if not null
   * @return StringBuilder the given one if not null, or a new allocated one if null, completed with
   *         the formatted nodes classes list
   */
  public StringBuilder formatNodesClassesList(final StringBuilder aSb) {
    final Spacing spc = new Spacing(INDENT_AMT);

    StringBuilder sb = aSb;
    if (sb == null)
      sb = new StringBuilder(classes.size() * 100);

    for (final Iterator<ClassInfo> e = classes.iterator(); e.hasNext();) {
      final ClassInfo classInfo = e.next();
      final String className = classInfo.getClassName();

      sb.append("class ").append(className).append(":").append(LS);
      spc.update(+1);

      final Iterator<String> types = classInfo.getFieldTypes().iterator();
      final Iterator<String> names = classInfo.getFieldNames().iterator();

      for (; types.hasNext();)
        sb.append(spc.spc).append(types.next()).append(" ").append(names.next()).append(LS);

      sb.append(LS);
      spc.update(-1);
    }
    return sb;
  }

  /**
   * Generates nodes (classes source) files.
   * 
   * @throws FileExistsException - if one or more files exist and no overwrite flag has been set
   */
  @Override
  public void genNodesFiles() throws FileExistsException {
    try {
      boolean exists = false;

      for (final Iterator<ClassInfo> e = classes.iterator(); e.hasNext();) {
        final ClassInfo classInfo = e.next();
        final File file = new File(astNodesDir, classInfo.getClassName() + ".java");
        if (noOverwrite && file.exists()) {
          Messages.softErr(classInfo.getClassName() + " exists but no overwrite flag has been set");
          exists = true;
          break;
        }
        final PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(file), 2048));
        out.print(genNodeClass(null, classInfo));
        out.close();
      }
      if (noOverwrite && exists)
        throw new FileExistsException("Some of the generated nodes classes files exist");
    }
    catch (final IOException e) {
      Messages.hardErr(e);
    }
  }

  /**
   * Generates a node class source string.
   * 
   * @param aSb - a buffer, used if not null
   * @param aClassInfo - the class to generate the source string
   * @return StringBuilder the given one if not null, or a new allocated one if null, completed with
   *         the node class source
   */
  public StringBuilder genNodeClass(final StringBuilder aSb, final ClassInfo aClassInfo) {
    final Spacing spc = new Spacing(INDENT_AMT);
    StringBuilder sb = aSb;
    if (sb == null)
      sb = new StringBuilder(2048);

    sb.append(genFileHeaderComment()).append(LS);
    sb.append(genFileHeader()).append(LS);
    if (javaDocComments) {
      sb.append("/**").append(LS);
      sb.append(" * JTB node class for the production ").append(aClassInfo.getClassName())
        .append(":<br>").append(LS);
      sb.append(" * Corresponding grammar:<br>").append(LS);
      // generate the javadoc for the class fields, with no indentation
      aClassInfo.fmtFieldsJavadocCmts(sb, spc);
      sb.append(" */").append(LS);
    }
    sb.append(aClassInfo.genClassString(spc, null));
    return sb;
  }

  private String genFileHeader() {
    StringBuilder sb = new StringBuilder();
    sb.append("package ").append(nodesPackageName).append(";").append(LS).append(LS);
    sb.append("import ").append(visitorsPackageName).append(".*;").append(LS).append(LS);
    return sb.toString();
  }

  /**
   * Generates the base nodes source files.
   * 
   * @throws FileExistsException - if one or more files exist and no overwrite flag has been set
   */
  @Override
  public void genBaseNodesFiles() throws FileExistsException {
    try {
      boolean b = true;
      
      b = b && fillFile(iNode + ".java", baseClasses.genINodeInterface(null, iNode));
      b = b && fillFile(iNodeList + ".java", baseClasses.genINodeListInterface(null, iNodeList));
      b = b && fillFile(nodeChoice + ".java", baseClasses.genNodeChoiceClass(null, nodeChoice));
      b = b && fillFile(nodeList + ".java", baseClasses.genNodeListClass(null, nodeList));
      b = b && fillFile(nodeListOpt + ".java", baseClasses.genNodeListOptClass(null, nodeListOpt));
      b = b && fillFile(nodeOpt + ".java", baseClasses.genNodeOptClass(null, nodeOpt));
      b = b && fillFile(nodeSeq + ".java", baseClasses.genNodeSeqClass(null, nodeSeq));
      b = b && fillFile(nodeToken + ".java", baseClasses.genNodeTokenClass(null, nodeToken));
      b = b && fillFile(nodeTCF + ".java", baseClasses.genNodeTCFClass(null, nodeTCF));

      if (noOverwrite && !b)
        throw new FileExistsException("Some of the base nodes classes files exist");
    }
    catch (final IOException e) {
      Messages.hardErr(e);
    }
  }

  /**
   * Fills a class file given its class source. It adds the file header comment
   * ("Generated by JTB version").
   * 
   * @param fileName - the class file name
   * @param classSource - the class source
   * @throws IOException - if any IO Exception
   * @return false if the file exists and the no overwrite flag is set, true otherwise
   */
  public boolean fillFile(final String fileName, final StringBuilder classSource)
                                                                                 throws IOException {
    final File file = new File(astNodesDirName, fileName);

    if (noOverwrite && file.exists())
      return false;

    final PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(file), 2048));
    out.println(genFileHeaderComment());
    out.print(classSource);
    out.close();
    return true;
  }

  /**
   * Generates the "RetArgu" IVisitor interface source (with return type and a user object
   * argument).
   * 
   * @param aSb - a buffer, used if not null
   * @return StringBuilder the given one if not null, or a new allocated one if null, completed with
   *         the visitor class source
   */
  public StringBuilder genRetArguIVisitor(final StringBuilder aSb) {
//    final String intf = iRetArguVisitor.concat(genClassParamType(true, true));
    final String consBeg = genClassBegArgList(true, iRetArguVisitor);
    final String consEnd = genClassEndArgList(true, iRetArguVisitor);
    StringBuilder sb = aSb;
    if (sb == null)
      sb = new StringBuilder(1500);

    genAnyIVisitorBeg(sb, retArguVisitorCmt, iRetArguVisitor, true, true);
    genBaseRetArguVisitMethods(sb, iRetArguVisitor);
    genAnyIVisitorEnd(sb, consBeg, consEnd, true, true);
    return sb;
  }

  /**
   * Generates the "Ret" IVisitor interface source (with return type and no user object argument).
   * 
   * @param aSb - a buffer, used if not null
   * @return StringBuilder the given one if not null, or a new allocated one if null, completed with
   *         the visitor class source
   */
  public StringBuilder genRetIVisitor(final StringBuilder aSb) {
//    final String intf = iRetVisitor.concat(genClassParamType(true, false));
    final String consBeg = genClassBegArgList(true, iRetVisitor);
    final String consEnd = genClassEndArgList(false, iRetVisitor);
    StringBuilder sb = aSb;
    if (sb == null)
      sb = new StringBuilder(1500);

    genAnyIVisitorBeg(sb, retVisitorCmt, iRetVisitor, true, false);
    genBaseRetVisitMethods(sb);
    genAnyIVisitorEnd(sb, consBeg, consEnd, true, false);
    return sb;
  }

  /**
   * Generates the "VoidArgu" IVisitor interface source (with no return type and a user object
   * argument).
   * 
   * @param aSb - a buffer, used if not null
   * @return StringBuilder the given one if not null, or a new allocated one if null, completed with
   *         the visitor class source
   */
  public StringBuilder genVoidArguIVisitor(final StringBuilder aSb) {
//    final String intf = iVoidArguVisitor.concat(genClassParamType(false, true));
    final String consBeg = genClassBegArgList(false, iVoidArguVisitor);
    final String consEnd = genClassEndArgList(true, iVoidArguVisitor);
    StringBuilder sb = aSb;
    if (sb == null)
      sb = new StringBuilder(1500);

    genAnyIVisitorBeg(sb, voidArguVisitorCmt, iVoidArguVisitor, false, true);
    genBaseVoidArguVisitMethods(sb, iVoidArguVisitor);
    genAnyIVisitorEnd(sb, consBeg, consEnd, false, true);
    return sb;
  }

  /**
   * Generates the "Void" IVisitor interface source (with no return type and no user object
   * argument).
   * 
   * @param aSb - a buffer, used if not null
   * @return StringBuilder the given one if not null, or a new allocated one if null, completed with
   *         the visitor class source
   */
  public StringBuilder genVoidIVisitor(final StringBuilder aSb) {
//    final String intf = iVoidVisitor.concat(genClassParamType(false, false));
    final String consBeg = genClassBegArgList(false, iVoidVisitor);
    final String consEnd = genClassEndArgList(false, iVoidVisitor);
    StringBuilder sb = aSb;
    if (sb == null)
      sb = new StringBuilder(1500);

    genAnyIVisitorBeg(sb, voidVisitorCmt, iVoidVisitor, false, false);
    genBaseVoidVisitMethods(sb);
    genAnyIVisitorEnd(sb, consBeg, consEnd, false, false);
    return sb;
  }

  /**
   * Generates the start for all visitor interfaces.
   * 
   * @param aSb - the buffer to output into (must be non null)
   * @param aComment - the target visitors names to insert in the interface comment
   * @param aIntf - the interface name
   * @param aRet - true if there is a user return parameter type, false otherwise
   * @param aArgu - true if there is a user argument parameter type, false otherwise
   */
  void genAnyIVisitorBeg(final StringBuilder aSb, final String aComment, final IVisitorClass aIntf,
                         final boolean aRet, final boolean aArgu) {
    aSb.append(genFileHeaderComment()).append(LS);
    if (Globals.target == Language.java) {
    aSb.append("package ").append(visitorsPackageName).append(";").append(LS).append(LS);
    if (!visitorsPackageName.equals(nodesPackageName))
      aSb.append("import ").append(nodesPackageName).append(".*;").append(LS).append(LS);
    }
    if (javaDocComments) {
      aSb.append("/**").append(LS);
      aSb.append(" * All \"").append(aComment).append("\" visitors must implement this interface.")
         .append(LS);
      if (aRet)
        aSb.append(" * @param <R> - The user return information type").append(LS);
      if (aArgu)
        aSb.append(" * @param <A> - The user argument type").append(LS);
      aSb.append(" */").append(LS);
    }
    switch(Globals.target) {
      case java:
        aSb.append("public interface ").append(aIntf).append(" {").append(LS);
        break;
      case cpp:
        aSb.append("public ").append(aIntf).append(" {").append(LS);
       break;
    }
  }

  /**
   * Generates the end for all visitor interfaces.
   * 
   * @param aSb - the buffer to output into (must be non null)
   * @param aConsBeg - the beginning of the visit methods
   * @param aConsEnd - the end of the visit methods
   * @param aRet - true if there is a user return parameter type, false otherwise
   * @param aArgu - true if there is a user argument parameter type, false otherwise
   */
  void genAnyIVisitorEnd(final StringBuilder aSb, final String aConsBeg, final String aConsEnd,
                         final boolean aRet, final boolean aArgu) {
    final Spacing spc = new Spacing(INDENT_AMT);
    spc.update(+1);
    if (javaDocComments) {
      aSb.append(spc.spc).append("/*").append(LS);
      aSb.append(spc.spc).append(" * User grammar generated visit methods").append(LS);
      aSb.append(spc.spc).append(" */").append(LS).append(LS);
    }
    for (final Iterator<ClassInfo> e = classes.iterator(); e.hasNext();) {
      final ClassInfo classInfo = e.next();
      final String className = classInfo.getClassName();
      if (javaDocComments) {
        aSb.append(spc.spc).append("/**").append(LS);
        aSb.append(spc.spc).append(" * Visits a {@link ").append(className)
           .append("} node, whose children are the following :").append(LS);
        aSb.append(spc.spc).append(" * <p>").append(LS);
        // generate the javadoc for the class fields, with indentation of 1
        classInfo.fmtFieldsJavadocCmts(aSb, spc);
        aSb.append(spc.spc).append(" *").append(LS);
        aSb.append(spc.spc).append(" * @param n - the node to visit").append(LS);
        if (aArgu)
          aSb.append(spc.spc).append(" * @param argu - the user argument").append(LS);
        if (aRet)
          aSb.append(spc.spc).append(" * @return the user return information").append(LS);
        aSb.append(spc.spc).append(" */").append(LS);
      }
      aSb.append(spc.spc).append("public ").append(aConsBeg).append(className).append(aConsEnd)
         .append(";").append(LS).append(LS);
    }
    spc.update(-1);
    aSb.append("}").append(LS);
  }

  /**
   * Generates the "RetArgu" IVisitor (interface source) file.
   * 
   * @throws FileExistsException - if the file already exists and the no overwrite flag has been set
   */
  @Override
  public void genRetArguIVisitorFile() throws FileExistsException {
    try {
      final File file = new File(visitorsDir, iRetArguVisitor + ".java");

      if (noOverwrite && file.exists())
        throw new FileExistsException(iRetArguVisitor + ".java");

      final PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(file), 1500));
      out.print(genRetArguIVisitor(null));
      out.close();
    }
    catch (final IOException e) {
      Messages.hardErr(e);
    }
  }

  /**
   * Generates the "Ret" IVisitor (interface source) file.
   * 
   * @throws FileExistsException - if the file already exists and the no overwrite flag has been set
   */
  @Override
  public void genRetIVisitorFile() throws FileExistsException {
    try {
      final File file = new File(visitorsDir, iRetVisitor + ".java");

      if (noOverwrite && file.exists())
        throw new FileExistsException(iRetVisitor + ".java");

      final PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(file), 1500));
      out.print(genRetIVisitor(null));
      out.close();
    }
    catch (final IOException e) {
      Messages.hardErr(e);
    }
  }

  /**
   * Generates the "VoidArgu" IVisitor (interface source) file.
   * 
   * @throws FileExistsException - if the file already exists and the no overwrite flag has been set
   */
  @Override
  public void genVoidArguIVisitorFile() throws FileExistsException {
    try {
      final File file = new File(visitorsDir, iVoidArguVisitor + ".java");

      if (noOverwrite && file.exists())
        throw new FileExistsException(iVoidArguVisitor + ".java");

      final PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(file), 1500));
      out.print(genVoidArguIVisitor(null));
      out.close();
    }
    catch (final IOException e) {
      Messages.hardErr(e);
    }
  }

  /**
   * Generates the "Void" IVisitor (interface source) file.
   * 
   * @throws FileExistsException - if the file already exists and the no overwrite flag has been set
   */
  @Override
  public void genVoidIVisitorFile() throws FileExistsException {
    try {
      final File file = new File(visitorsDir, iVoidVisitor + ".java");

      if (noOverwrite && file.exists())
        throw new FileExistsException(iVoidVisitor + ".java");

      final PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(file), 1500));
      out.print(genVoidIVisitor(null));
      out.close();
    }
    catch (final IOException e) {
      Messages.hardErr(e);
    }
  }

  /**
   * Generates the base "RetArgu" visit methods.
   * 
   * @param aSb - a buffer, used if not null
   * @param intf TODO
   * @return StringBuilder the given one if not null, or a new allocated one if null, completed with
   *         the base visitor methods source
   */
  StringBuilder genBaseRetArguVisitMethods(final StringBuilder aSb, IVisitorClass intf) {
    StringBuilder sb = aSb;
    if (sb == null)
      sb = new StringBuilder(100);
    sb.append(LS);
    if (javaDocComments) {
      sb.append("  /*").append(LS);
      sb.append("   * Base \"RetArgu\" visit methods").append(LS);
      sb.append("   */").append(LS).append(LS);
    }
    baseClasses.genRetArguVisitNodeChoice(sb, intf);
    sb.append(LS);
    baseClasses.genRetArguVisitNodeList(sb, intf);
    sb.append(LS);
    baseClasses.genRetArguVisitNodeListOpt(sb, intf);
    sb.append(LS);
    baseClasses.genRetArguVisitNodeOpt(sb, intf);
    sb.append(LS);
    baseClasses.genRetArguVisitNodeSeq(sb, intf);
    sb.append(LS);
    baseClasses.genRetArguVisitNodeTCF(sb, intf);
    sb.append(LS);
    baseClasses.genRetArguVisitNodeToken(sb, intf);
    sb.append(LS);
    return sb;
  }

  /**
   * Generates the base "Ret" visit methods.
   * 
   * @param aSb - a buffer, used if not null
   * @return StringBuilder the given one if not null, or a new allocated one if null, completed with
   *         the base visitor methods source
   */
  StringBuilder genBaseRetVisitMethods(final StringBuilder aSb) {
    StringBuilder sb = aSb;
    if (sb == null)
      sb = new StringBuilder(100);
    sb.append(LS);
    if (javaDocComments) {
      sb.append("  /*").append(LS);
      sb.append("   * Base \"Ret\" visit methods").append(LS);
      sb.append("   */").append(LS).append(LS);
    }
    baseClasses.genRetVisitNodeChoice(sb);
    sb.append(LS);
    baseClasses.genRetVisitNodeList(sb);
    sb.append(LS);
    baseClasses.genRetVisitNodeListOpt(sb);
    sb.append(LS);
    baseClasses.genRetVisitNodeOpt(sb);
    sb.append(LS);
    baseClasses.genRetVisitNodeSeq(sb);
    sb.append(LS);
    baseClasses.genRetVisitNodeTCF(sb);
    sb.append(LS);
    baseClasses.genRetVisitNodeToken(sb);
    sb.append(LS);
    return sb;
  }

  /**
   * Generates the base "VoidArgu" visit methods.
   * 
   * @param aSb - a buffer, used if not null
   * @param intf TODO
   * @return StringBuilder the given one if not null, or a new allocated one if null, completed with
   *         the base visitor methods source
   */
  StringBuilder genBaseVoidArguVisitMethods(final StringBuilder aSb, IVisitorClass intf) {
    StringBuilder sb = aSb;
    if (sb == null)
      sb = new StringBuilder(100);
    sb.append(LS);
    if (javaDocComments) {
      sb.append("  /*").append(LS);
      sb.append("   * Base \"VoidArgu\" visit methods").append(LS);
      sb.append("   */").append(LS).append(LS);
    }
    baseClasses.genVoidArguVisitNodeChoice(sb, intf);
    sb.append(LS);
    baseClasses.genVoidArguVisitNodeList(sb, intf);
    sb.append(LS);
    baseClasses.genVoidArguVisitNodeListOpt(sb, intf);
    sb.append(LS);
    baseClasses.genVoidArguVisitNodeOpt(sb, intf);
    sb.append(LS);
    baseClasses.genVoidArguVisitNodeSeq(sb, intf);
    sb.append(LS);
    baseClasses.genVoidArguVisitNodeTCF(sb, intf);
    sb.append(LS);
    baseClasses.genVoidArguVisitNodeToken(sb, intf);
    sb.append(LS);
    return sb;
  }

  /**
   * Generates the base "Void" visit methods.
   * 
   * @param aSb - a buffer, used if not null
   * @return StringBuilder the given one if not null, or a new allocated one if null, completed with
   *         the base visitor methods source
   */
  StringBuilder genBaseVoidVisitMethods(final StringBuilder aSb) {
    StringBuilder sb = aSb;
    if (sb == null)
      sb = new StringBuilder(100);
    sb.append(LS);
    if (javaDocComments) {
      sb.append("  /*").append(LS);
      sb.append("   * Base \"Void\" visit methods").append(LS);
      sb.append("   */").append(LS).append(LS);
    }
    baseClasses.genVoidVisitNodeChoice(sb);
    sb.append(LS);
    baseClasses.genVoidVisitNodeList(sb);
    sb.append(LS);
    baseClasses.genVoidVisitNodeListOpt(sb);
    sb.append(LS);
    baseClasses.genVoidVisitNodeOpt(sb);
    sb.append(LS);
    baseClasses.genVoidVisitNodeSeq(sb);
    sb.append(LS);
    baseClasses.genVoidVisitNodeTCF(sb);
    sb.append(LS);
    baseClasses.genVoidVisitNodeToken(sb);
    sb.append(LS);
    return sb;
  }

  /**
   * Generates the class parameter types.
   * 
   * @param ret - true if there is a user return parameter type, false otherwise
   * @param arg - true if there is a user argument parameter type, false otherwise
   * @return the class parameter types string
   */
  static String genClassParamType(final boolean ret, final boolean arg) {
    if (ret) {
      if (arg) {
        return "<".concat(genRetType).concat(", ").concat(genArguType).concat(">");
      } else {
        return "<".concat(genRetType).concat(">");
      }
    } else {
      if (arg) {
        return "<".concat(genArguType).concat(">");
      } else {
        return "";
      }
    }
  }

  /**
   * Generates the beginning of the visit methods.
   * 
   * @param arg - true if there is a user argument parameter type, false otherwise
   * @return the beginning of the visit methods
   */
   @Override
  public String genClassBegArgList(final boolean arg, IVisitorClass intf) {
    if (arg) {
      return genRetType + " visit(final ";
    } else {
      return "void visit(final ";
    }
  }

  /**
   * Generates the end of the visit methods.
   * 
   * @param arg - true if there is a user argument parameter type, false otherwise
   * @return the end of the visit methods
   */
   @Override
  public String genClassEndArgList(final boolean arg, IVisitorClass intf) {
    if (arg) {
      return " n, final ".concat(varargs ? genArgusType : genArguType).concat(" argu)");
    } else {
      return " n)";
    }
  }
}
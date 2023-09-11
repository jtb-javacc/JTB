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
import static EDU.purdue.jtb.misc.Globals.nodesDirName;
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
import java.util.Iterator;
import java.util.List;

/**
 * Class FilesGenerator contains methods to generate the grammar (user) nodes java files, the base
 * nodes java files, the visitor interfaces and the default visitors classes files.<br>
 * It must be constructed with the list of the grammar {@link ClassInfo} classes.
 *
 * @author Marc Mazas
 * @version 1.4.0 : 05-08/2009 : MMa : adapted to JavaCC v4.2 grammar and JDK 1.5<br>
 *          1.4.0 : 11/2009 : MMa : fixed directories creation errors
 * @version 1.4.6 : 01/2011 : FA/MMa : added -va and -npfx and -nsfx options
 * @version 1.4.7 : 09/2012 : MMa : added missing generated visit methods (NodeChoice and NodeTCF)
 * @version 1.4.8 : 10/2012 : MMa : tuned javadoc comments for nodes with no child<br>
 *          1.4.8 : 10/2014 : MMa : fixed NPE on classes without fields
 * @version 1.4.14 : 01/2017 : MMa : used try-with-resource
 */
public class FilesGenerator {

  /** The classes list */
  private final List<ClassInfo> classes;
  /** The (generated) nodes directory */
  private final File            nodesDir;
  /** The (generated) visitors directory */
  private final File            visitorsDir;

  /**
   * Constructor. Creates the nodes and visitors directories if they do not exist.
   *
   * @param aClasses - the list of {@link ClassInfo} classes instances
   */
  public FilesGenerator(final List<ClassInfo> aClasses) {
    classes = aClasses;

    nodesDir = new File(nodesDirName);
    visitorsDir = new File(visitorsDirName);

    if (!nodesDir.exists())
      if (nodesDir.mkdirs())
        Messages.info("\"" + nodesDirName + "\" directory created.");
      else
        Messages.softErr("Unable to create \"" + nodesDirName + "\" directory.");
    else if (!nodesDir.isDirectory())
      Messages.softErr("\"" + nodesDirName + "\" exists but is not a directory.");

    if (!visitorsDir.exists())
      if (visitorsDir.mkdirs())
        Messages.info("\"" + visitorsDirName + "\" directory created.");
      else
        Messages.softErr("Unable to create \"" + visitorsDirName + "\" directory.");
    else if (!visitorsDir.isDirectory())
      Messages.softErr("\"" + visitorsDirName + "\" exists but is not a directory.");
  }

  /**
   * Outputs the formatted nodes classes list.
   *
   * @param out - a PrintWriter to output on
   */
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
      final ClassInfo ci = e.next();
      final String className = ci.className;

      sb.append("class ").append(className).append(":").append(LS);
      spc.updateSpc(+1);

      if (ci.fieldTypes != null) {
        final Iterator<String> types = ci.fieldTypes.iterator();
        final Iterator<String> names = ci.fieldNames.iterator();

        for (; types.hasNext();)
          sb.append(spc.spc).append(types.next()).append(" ").append(names.next()).append(LS);
      }

      sb.append(LS);
      spc.updateSpc(-1);
    }
    return sb;
  }

  /**
   * Generates nodes (classes source) files.
   *
   * @throws FileExistsException if file exists
   * @throws IOException if IO problem
   */
  public void genNodesFiles() throws FileExistsException, IOException {
    boolean exists = false;
    for (final Iterator<ClassInfo> e = classes.iterator(); e.hasNext();) {
      final ClassInfo ci = e.next();
      final File file = new File(nodesDir, ci.className + ".java");
      if (noOverwrite && file.exists()) {
        Messages.softErr(ci.className + " exists but no overwrite flag has been set");
        exists = true;
        break;
      }
      try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(file), 2048))) {
        out.print(genNodeClass(null, ci));
      }
    }
    if (noOverwrite && exists)
      throw new FileExistsException("Some of the generated nodes classes files exist");
  }

  /**
   * Generates a node class source string.
   *
   * @param aSb - a buffer, used if not null
   * @param aClassInfo - the class to generate the source string
   * @return StringBuilder the given one if not null, or a new allocated one if null, completed with
   *         the node class source
   */
  public static StringBuilder genNodeClass(final StringBuilder aSb, final ClassInfo aClassInfo) {
    final Spacing spc = new Spacing(INDENT_AMT);
    StringBuilder sb = aSb;
    if (sb == null)
      sb = new StringBuilder(2048);

    sb.append(genFileHeaderComment()).append(LS);
    sb.append("package ").append(nodesPackageName).append(";").append(LS).append(LS);
    sb.append("import ").append(visitorsPackageName).append(".*;").append(LS).append(LS);
    if (javaDocComments) {
      sb.append("/**").append(LS);
      sb.append(" * JTB node class for the production ").append(aClassInfo.className)
        .append(":<br>").append(LS);
      sb.append(" * Corresponding grammar:<br>").append(LS);
      // generate the javadoc for the class fields, with no indentation
      aClassInfo.fmtFieldsJavadocCmts(sb, spc);
      sb.append(" */").append(LS);
    }
    sb.append(aClassInfo.genClassString(spc));
    return sb;
  }

  /**
   * Generates the base nodes source files.
   *
   * @throws FileExistsException - if one or more files exist and no overwrite flag has been set
   * @throws IOException if IO problem
   */
  public static void genBaseNodesFiles() throws FileExistsException, IOException {
    try {
      boolean b = true;

      b = b && fillFile(iNode + ".java", BaseClasses.genINodeInterface(null));
      b = b && fillFile(iNodeList + ".java", BaseClasses.genINodeListInterface(null));
      b = b && fillFile(nodeChoice + ".java", BaseClasses.genNodeChoiceClass(null));
      b = b && fillFile(nodeList + ".java", BaseClasses.genNodeListClass(null));
      b = b && fillFile(nodeListOpt + ".java", BaseClasses.genNodeListOptClass(null));
      b = b && fillFile(nodeOpt + ".java", BaseClasses.genNodeOptClass(null));
      b = b && fillFile(nodeSeq + ".java", BaseClasses.genNodeSeqClass(null));
      b = b && fillFile(nodeToken + ".java", BaseClasses.genNodeTokenClass(null));
      b = b && fillFile(nodeTCF + ".java", BaseClasses.genNodeTCFClass(null));

      if (noOverwrite && !b)
        throw new FileExistsException("Some of the base nodes classes files exist");
    }
    catch (final IOException e) {
      Messages.hardErr(e);
      throw e;
    }
  }

  /**
   * Fills a class file given its class source. It adds the file header comment ("Generated by JTB
   * version").
   *
   * @param fileName - the class file name
   * @param classSource - the class source
   * @throws IOException - if any IO Exception
   * @return false if the file exists and the no overwrite flag is set, true otherwise
   */
  public static boolean fillFile(final String fileName,
                                 final StringBuilder classSource) throws IOException {
    final File file = new File(nodesDirName, fileName);

    if (noOverwrite && file.exists())
      return false;

    try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(file), 2048))) {
      out.println(genFileHeaderComment());
      out.print(classSource);
    }
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
    final String intf = iRetArguVisitor.concat(genClassParamType(true, true));
    final String consBeg = genClassBegArgList(true);
    final String consEnd = genClassEndArgList(true);
    StringBuilder sb = aSb;
    if (sb == null)
      sb = new StringBuilder(1500);

    genAnyIVisitorBeg(sb, retArguVisitorCmt, intf, true, true);
    genBaseRetArguVisitMethods(sb);
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
    final String intf = iRetVisitor.concat(genClassParamType(true, false));
    final String consBeg = genClassBegArgList(true);
    final String consEnd = genClassEndArgList(false);
    StringBuilder sb = aSb;
    if (sb == null)
      sb = new StringBuilder(1500);

    genAnyIVisitorBeg(sb, retVisitorCmt, intf, true, false);
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
    final String intf = iVoidArguVisitor.concat(genClassParamType(false, true));
    final String consBeg = genClassBegArgList(false);
    final String consEnd = genClassEndArgList(true);
    StringBuilder sb = aSb;
    if (sb == null)
      sb = new StringBuilder(1500);

    genAnyIVisitorBeg(sb, voidArguVisitorCmt, intf, false, true);
    genBaseVoidArguVisitMethods(sb);
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
    final String intf = iVoidVisitor.concat(genClassParamType(false, false));
    final String consBeg = genClassBegArgList(false);
    final String consEnd = genClassEndArgList(false);
    StringBuilder sb = aSb;
    if (sb == null)
      sb = new StringBuilder(1500);

    genAnyIVisitorBeg(sb, voidVisitorCmt, intf, false, false);
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
  static void genAnyIVisitorBeg(final StringBuilder aSb, final String aComment, final String aIntf,
                                final boolean aRet, final boolean aArgu) {
    aSb.append(genFileHeaderComment()).append(LS);
    aSb.append("package ").append(visitorsPackageName).append(";").append(LS).append(LS);
    if (!visitorsPackageName.equals(nodesPackageName))
      aSb.append("import ").append(nodesPackageName).append(".*;").append(LS).append(LS);
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
    aSb.append("public interface ").append(aIntf).append(" {").append(LS);
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
    spc.updateSpc(+1);
    if (javaDocComments) {
      aSb.append(spc.spc).append("/*").append(LS);
      aSb.append(spc.spc).append(" * User grammar generated visit methods").append(LS);
      aSb.append(spc.spc).append(" */").append(LS).append(LS);
    }
    for (final Iterator<ClassInfo> e = classes.iterator(); e.hasNext();) {
      final ClassInfo ci = e.next();
      final String className = ci.className;
      if (javaDocComments) {
        aSb.append(spc.spc).append("/**").append(LS);
        aSb.append(spc.spc).append(" * Visits a {@link ").append(className).append("} node, ");
        aSb.append(ci.astEcNode == null ? "with no child :"
                                        : ci.fieldNames.size() == 1 ? "whose child is the following :"
                                                                    : "whose children are the following :")
           .append(LS);
        aSb.append(spc.spc).append(" * <p>").append(LS);
        // generate the javadoc for the class fields, with indentation of 1
        ci.fmtFieldsJavadocCmts(aSb, spc);
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
    spc.updateSpc(-1);
    aSb.append("}").append(LS);
  }

  /**
   * Generates the "RetArgu" IVisitor (interface source) file.
   *
   * @throws FileExistsException - if the file already exists and the no overwrite flag has been set
   * @throws IOException if IO problem
   */
  public void genRetArguIVisitorFile() throws FileExistsException, IOException {
    try {
      final File file = new File(visitorsDir, iRetArguVisitor + ".java");

      if (noOverwrite && file.exists())
        throw new FileExistsException(iRetArguVisitor + ".java");

      try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(file), 1500))) {
        out.print(genRetArguIVisitor(null));
      }
    }
    catch (final IOException e) {
      Messages.hardErr(e);
      throw e;
    }
  }

  /**
   * Generates the "Ret" IVisitor (interface source) file.
   *
   * @throws FileExistsException - if the file already exists and the no overwrite flag has been set
   * @throws IOException if IO problem
   */
  public void genRetIVisitorFile() throws FileExistsException, IOException {
    try {
      final File file = new File(visitorsDir, iRetVisitor + ".java");

      if (noOverwrite && file.exists())
        throw new FileExistsException(iRetVisitor + ".java");

      try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(file), 1500))) {
        out.print(genRetIVisitor(null));
      }
    }
    catch (final IOException e) {
      Messages.hardErr(e);
      throw e;
    }
  }

  /**
   * Generates the "VoidArgu" IVisitor (interface source) file.
   *
   * @throws FileExistsException - if the file already exists and the no overwrite flag has been set
   * @throws IOException if IO problem
   */
  public void genVoidArguIVisitorFile() throws FileExistsException, IOException {
    try {
      final File file = new File(visitorsDir, iVoidArguVisitor + ".java");

      if (noOverwrite && file.exists())
        throw new FileExistsException(iVoidArguVisitor + ".java");

      try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(file), 1500))) {
        out.print(genVoidArguIVisitor(null));
      }
    }
    catch (final IOException e) {
      Messages.hardErr(e);
      throw e;
    }
  }

  /**
   * Generates the "Void" IVisitor (interface source) file.
   *
   * @throws FileExistsException - if the file already exists and the no overwrite flag has been set
   * @throws IOException if IO problem
   */
  public void genVoidIVisitorFile() throws FileExistsException, IOException {
    try {
      final File file = new File(visitorsDir, iVoidVisitor + ".java");

      if (noOverwrite && file.exists())
        throw new FileExistsException(iVoidVisitor + ".java");

      try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(file), 1500))) {
        out.print(genVoidIVisitor(null));
      }
    }
    catch (final IOException e) {
      Messages.hardErr(e);
      throw e;
    }
  }

  /**
   * Generates the base "RetArgu" visit methods.
   *
   * @param aSb - a buffer, used if not null
   * @return StringBuilder the given one if not null, or a new allocated one if null, completed with
   *         the base visitor methods source
   */
  static StringBuilder genBaseRetArguVisitMethods(final StringBuilder aSb) {
    StringBuilder sb = aSb;
    if (sb == null)
      sb = new StringBuilder(100);
    sb.append(LS);
    if (javaDocComments) {
      sb.append("  /*").append(LS);
      sb.append("   * Base \"RetArgu\" visit methods").append(LS);
      sb.append("   */").append(LS).append(LS);
    }
    BaseClasses.genRetArguVisitNodeChoice(sb);
    sb.append(LS);
    BaseClasses.genRetArguVisitNodeList(sb);
    sb.append(LS);
    BaseClasses.genRetArguVisitNodeListOpt(sb);
    sb.append(LS);
    BaseClasses.genRetArguVisitNodeOpt(sb);
    sb.append(LS);
    BaseClasses.genRetArguVisitNodeSeq(sb);
    sb.append(LS);
    BaseClasses.genRetArguVisitNodeTCF(sb);
    sb.append(LS);
    BaseClasses.genRetArguVisitNodeToken(sb);
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
  static StringBuilder genBaseRetVisitMethods(final StringBuilder aSb) {
    StringBuilder sb = aSb;
    if (sb == null)
      sb = new StringBuilder(100);
    sb.append(LS);
    if (javaDocComments) {
      sb.append("  /*").append(LS);
      sb.append("   * Base \"Ret\" visit methods").append(LS);
      sb.append("   */").append(LS).append(LS);
    }
    BaseClasses.genRetVisitNodeChoice(sb);
    sb.append(LS);
    BaseClasses.genRetVisitNodeList(sb);
    sb.append(LS);
    BaseClasses.genRetVisitNodeListOpt(sb);
    sb.append(LS);
    BaseClasses.genRetVisitNodeOpt(sb);
    sb.append(LS);
    BaseClasses.genRetVisitNodeSeq(sb);
    sb.append(LS);
    BaseClasses.genRetVisitNodeTCF(sb);
    sb.append(LS);
    BaseClasses.genRetVisitNodeToken(sb);
    sb.append(LS);
    return sb;
  }

  /**
   * Generates the base "VoidArgu" visit methods.
   *
   * @param aSb - a buffer, used if not null
   * @return StringBuilder the given one if not null, or a new allocated one if null, completed with
   *         the base visitor methods source
   */
  static StringBuilder genBaseVoidArguVisitMethods(final StringBuilder aSb) {
    StringBuilder sb = aSb;
    if (sb == null)
      sb = new StringBuilder(100);
    sb.append(LS);
    if (javaDocComments) {
      sb.append("  /*").append(LS);
      sb.append("   * Base \"VoidArgu\" visit methods").append(LS);
      sb.append("   */").append(LS).append(LS);
    }
    BaseClasses.genVoidArguVisitNodeChoice(sb);
    sb.append(LS);
    BaseClasses.genVoidArguVisitNodeList(sb);
    sb.append(LS);
    BaseClasses.genVoidArguVisitNodeListOpt(sb);
    sb.append(LS);
    BaseClasses.genVoidArguVisitNodeOpt(sb);
    sb.append(LS);
    BaseClasses.genVoidArguVisitNodeSeq(sb);
    sb.append(LS);
    BaseClasses.genVoidArguVisitNodeTCF(sb);
    sb.append(LS);
    BaseClasses.genVoidArguVisitNodeToken(sb);
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
  static StringBuilder genBaseVoidVisitMethods(final StringBuilder aSb) {
    StringBuilder sb = aSb;
    if (sb == null)
      sb = new StringBuilder(100);
    sb.append(LS);
    if (javaDocComments) {
      sb.append("  /*").append(LS);
      sb.append("   * Base \"Void\" visit methods").append(LS);
      sb.append("   */").append(LS).append(LS);
    }
    BaseClasses.genVoidVisitNodeChoice(sb);
    sb.append(LS);
    BaseClasses.genVoidVisitNodeList(sb);
    sb.append(LS);
    BaseClasses.genVoidVisitNodeListOpt(sb);
    sb.append(LS);
    BaseClasses.genVoidVisitNodeOpt(sb);
    sb.append(LS);
    BaseClasses.genVoidVisitNodeSeq(sb);
    sb.append(LS);
    BaseClasses.genVoidVisitNodeTCF(sb);
    sb.append(LS);
    BaseClasses.genVoidVisitNodeToken(sb);
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
  static String genClassBegArgList(final boolean arg) {
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
  static String genClassEndArgList(final boolean arg) {
    if (arg) {
      return " n, final ".concat(varargs ? genArgusType : genArguType).concat(" argu)");
    } else {
      return " n)";
    }
  }
}
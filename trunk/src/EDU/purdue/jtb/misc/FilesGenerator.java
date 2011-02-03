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

/**
 * Class FilesGenerator contains methods to generate the grammar (user) nodes java files, the base
 * nodes java files, the visitor interfaces and the default visitors classes files.<br>
 * It must be constructed with the list of the grammar {@link ClassInfo} classes.
 * 
 * @author Marc Mazas, mmazas@sopragroup.com
 * @version 1.4.0 : 05-08/2009 : MMa : adapted to JavaCC v4.2 grammar and JDK 1.5<br>
 *          1.4.0 : 11/09 : MMa : fixed directories creation errors
 */
public class FilesGenerator {

  /** The classes list */
  private final ArrayList<ClassInfo> classes;
  /** The (generated) nodes directory */
  private final File                 nodesDir;
  /** The (generated) visitors directory */
  private final File                 visitorsDir;
  /** The OS line separator */
  public static final String         LS = System.getProperty("line.separator");

  /**
   * Constructor. Creates the nodes and visitors directories if they do not exist.
   * 
   * @param classesList the list of {@link ClassInfo} classes instances
   */
  public FilesGenerator(final ArrayList<ClassInfo> classesList) {
    classes = classesList;

    nodesDir = new File(Globals.nodesDirName);
    visitorsDir = new File(Globals.visitorsDirName);

    if (!nodesDir.exists())
      if (nodesDir.mkdirs())
        Messages.info("\"" + Globals.nodesDirName + "\" directory created.");
      else
        Messages.softErr("Unable to create \"" + Globals.nodesDirName + "\" directory.");
    else if (!nodesDir.isDirectory())
      Messages.softErr("\"" + Globals.nodesDirName + "\" exists but is not a directory.");

    if (!visitorsDir.exists())
      if (visitorsDir.mkdirs())
        Messages.info("\"" + Globals.visitorsDirName + "\" directory created.");
      else
        Messages.softErr("Unable to create \"" + Globals.visitorsDirName + "\" directory.");
    else if (!visitorsDir.isDirectory())
      Messages.softErr("\"" + Globals.visitorsDirName + "\" exists but is not a directory.");
  }

  /**
   * Outputs the formatted nodes classes list.
   * 
   * @param out a PrintWriter to output on
   */
  public void outputFormattedNodesClassesList(final PrintWriter out) {
    final StringBuilder sb = null;
    out.print(formatNodesClassesList(sb));
    out.flush();
  }

  /**
   * Formats the nodes classes list.
   * 
   * @param aSB a StringBuilder, used if not null
   * @return StringBuilder the given one if not null, or a new allocated one if null, completed with
   *         the formatted nodes classes list
   */
  public StringBuilder formatNodesClassesList(final StringBuilder aSB) {
    final Spacing spc = new Spacing(Globals.INDENT_AMT);

    StringBuilder sb = aSB;
    if (sb == null)
      sb = new StringBuilder(classes.size() * 100);

    for (final Iterator<ClassInfo> e = classes.iterator(); e.hasNext();) {
      final ClassInfo classInfo = e.next();
      final String className = classInfo.getClassName();

      sb.append("class ").append(className).append(":").append(LS);
      spc.updateSpc(+1);

      final Iterator<String> types = classInfo.getFieldTypes().iterator();
      final Iterator<String> names = classInfo.getFieldNames().iterator();

      for (; types.hasNext();)
        sb.append(spc.spc).append(types.next()).append(" ").append(names.next()).append(LS);

      sb.append(LS);
      spc.updateSpc(-1);
    }
    return sb;
  }

  /**
   * Generates nodes (classes source) files.
   * 
   * @throws FileExistsException if one or more files exist and no overwrite flag has been set
   */
  public void genNodesFiles() throws FileExistsException {
    try {
      boolean exists = false;

      for (final Iterator<ClassInfo> e = classes.iterator(); e.hasNext();) {
        final ClassInfo classInfo = e.next();
        final File file = new File(nodesDir, classInfo.getClassName() + ".java");
        if (Globals.noOverwrite && file.exists()) {
          Messages.softErr(classInfo.getClassName() + " exists but no overwrite flag has been set");
          exists = true;
          break;
        }
        final PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(file), 2048));
        out.print(genNodeClass(null, classInfo));
        out.close();
      }
      if (Globals.noOverwrite && exists)
        throw new FileExistsException("Some of the generated nodes classes files exist");
    }
    catch (final IOException e) {
      Messages.hardErr(e);
    }
  }

  /**
   * Generates a node class source string.
   * 
   * @param aSB a StringBuilder, used if not null
   * @param aClassInfo the class to generate the source string
   * @return StringBuilder the given one if not null, or a new allocated one if null, completed with
   *         the node class source
   */
  public StringBuilder genNodeClass(final StringBuilder aSB, final ClassInfo aClassInfo) {
    final Spacing spc = new Spacing(Globals.INDENT_AMT);
    StringBuilder sb = aSB;
    if (sb == null)
      sb = new StringBuilder(2048);

    sb.append(Globals.genFileHeaderComment()).append(LS);
    sb.append("package ").append(Globals.nodesPackageName).append(";").append(LS).append(LS);
    sb.append("import ").append(Globals.visitorsPackageName).append(".*;").append(LS).append(LS);
    if (Globals.javaDocComments) {
      sb.append("/**").append(LS);
      sb.append(" * JTB node class for the production ").append(aClassInfo.getClassName())
        .append(":<br>").append(LS);
      sb.append(" * Corresponding grammar :<br>").append(LS);
      sb.append(aClassInfo.genAllFieldsComment(spc));
      sb.append(" */").append(LS);
    }
    sb.append(aClassInfo.genClassString(spc));
    return sb;
  }

  /**
   * Generates the base nodes source files.
   * 
   * @throws FileExistsException if one or more files exist and no overwrite flag has been set
   */
  public void genBaseNodesFiles() throws FileExistsException {
    try {
      boolean b = true;

      b = b && fillFile(Globals.iNodeName + ".java", BaseClasses.genINodeInterface(null));
      b = b && fillFile(Globals.iNodeListName + ".java", BaseClasses.genINodeListInterface(null));
      b = b && fillFile(Globals.nodeChoiceName + ".java", BaseClasses.genNodeChoiceClass(null));
      b = b && fillFile(Globals.nodeListName + ".java", BaseClasses.genNodeListClass(null));
      b = b && fillFile(Globals.nodeListOptName + ".java", BaseClasses.genNodeListOptClass(null));
      b = b && fillFile(Globals.nodeOptName + ".java", BaseClasses.genNodeOptClass(null));
      b = b && fillFile(Globals.nodeSeqName + ".java", BaseClasses.genNodeSeqClass(null));
      b = b && fillFile(Globals.nodeTokenName + ".java", BaseClasses.genNodeTokenClass(null));

      if (Globals.noOverwrite && !b)
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
   * @param fileName the class file name
   * @param classSource the class source
   * @throws IOException if any IO Exception
   * @return false if the file exists and the no overwrite flag is set, true otherwise
   */
  public boolean fillFile(final String fileName, final StringBuilder classSource)
                                                                                 throws IOException {
    final File file = new File(Globals.nodesDirName, fileName);

    if (Globals.noOverwrite && file.exists())
      return false;

    final PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(file), 2048));
    out.println(Globals.genFileHeaderComment());
    out.print(classSource);
    out.close();
    return true;
  }

  /**
   * Generates the "RetArgu" IVisitor interface source (with return type and a user object
   * argument).
   * 
   * @param aSB a StringBuilder, used if not null
   * @return StringBuilder the given one if not null, or a new allocated one if null, completed with
   *         the visitor class source
   */
  public StringBuilder genRetArguIVisitor(final StringBuilder aSB) {
    final String intf = Globals.iRetArguVisitorName.concat(genClassParamType(true, true));
    final String consBeg = genClassBegArgList(true);
    final String consEnd = genClassEndArgList(true);
    StringBuilder sb = aSB;
    if (sb == null)
      sb = new StringBuilder(1500);

    genAnyIVisitorBeg(sb, Globals.retArguVisitorComment, intf, true, true);
    genBaseRetArguVisitMethods(sb);
    genAnyIVisitorEnd(sb, consBeg, consEnd, true, true);
    return sb;
  }

  /**
   * Generates the "Ret" IVisitor interface source (with return type and no user object argument).
   * 
   * @param aSB a StringBuilder, used if not null
   * @return StringBuilder the given one if not null, or a new allocated one if null, completed with
   *         the visitor class source
   */
  public StringBuilder genRetIVisitor(final StringBuilder aSB) {
    final String intf = Globals.iRetVisitorName.concat(genClassParamType(true, false));
    final String consBeg = genClassBegArgList(true);
    final String consEnd = genClassEndArgList(false);
    StringBuilder sb = aSB;
    if (sb == null)
      sb = new StringBuilder(1500);

    genAnyIVisitorBeg(sb, Globals.retVisitorComment, intf, true, false);
    genBaseRetVisitMethods(sb);
    genAnyIVisitorEnd(sb, consBeg, consEnd, true, false);
    return sb;
  }

  /**
   * Generates the "VoidArgu" IVisitor interface source (with no return type and a user object
   * argument).
   * 
   * @param aSB a StringBuilder, used if not null
   * @return StringBuilder the given one if not null, or a new allocated one if null, completed with
   *         the visitor class source
   */
  public StringBuilder genVoidArguIVisitor(final StringBuilder aSB) {
    final String intf = Globals.iVoidArguVisitorName.concat(genClassParamType(false, true));
    final String consBeg = genClassBegArgList(false);
    final String consEnd = genClassEndArgList(true);
    StringBuilder sb = aSB;
    if (sb == null)
      sb = new StringBuilder(1500);

    genAnyIVisitorBeg(sb, Globals.voidArguVisitorComment, intf, false, true);
    genBaseVoidArguVisitMethods(sb);
    genAnyIVisitorEnd(sb, consBeg, consEnd, false, true);
    return sb;
  }

  /**
   * Generates the "Void" IVisitor interface source (with no return type and no user object
   * argument).
   * 
   * @param aSB a StringBuilder, used if not null
   * @return StringBuilder the given one if not null, or a new allocated one if null, completed with
   *         the visitor class source
   */
  public StringBuilder genVoidIVisitor(final StringBuilder aSB) {
    final String intf = Globals.iVoidVisitorName.concat(genClassParamType(false, false));
    final String consBeg = genClassBegArgList(false);
    final String consEnd = genClassEndArgList(false);
    StringBuilder sb = aSB;
    if (sb == null)
      sb = new StringBuilder(1500);

    genAnyIVisitorBeg(sb, Globals.voidVisitorComment, intf, false, false);
    genBaseVoidVisitMethods(sb);
    genAnyIVisitorEnd(sb, consBeg, consEnd, false, false);
    return sb;
  }

  /**
   * Generates the start for all visitor interfaces.
   * 
   * @param aSB the buffer to output into (must be non null)
   * @param aComment the target visitors names to insert in the interface comment
   * @param aIntf the interface name
   * @param aRet true if there is a user return parameter type, false otherwise
   * @param aArgu true if there is a user argument parameter type, false otherwise
   */
  void genAnyIVisitorBeg(final StringBuilder aSB, final String aComment, final String aIntf,
                         final boolean aRet, final boolean aArgu) {
    aSB.append(Globals.genFileHeaderComment()).append(LS);
    aSB.append("package ").append(Globals.visitorsPackageName).append(";").append(LS).append(LS);
    if (!Globals.visitorsPackageName.equals(Globals.nodesPackageName))
      aSB.append("import ").append(Globals.nodesPackageName).append(".*;").append(LS).append(LS);
    if (Globals.javaDocComments) {
      aSB.append("/**").append(LS);
      aSB.append(" * All \"").append(aComment).append("\" visitors must implement this interface.")
         .append(LS);
      if (aRet)
        aSB.append(" * @param <R> The user return information type").append(LS);
      if (aArgu)
        aSB.append(" * @param <A> The user argument type").append(LS);
      aSB.append(" */").append(LS);
    }
    aSB.append("public interface ").append(aIntf).append(" {").append(LS);
  }

  /**
   * Generates the end for all visitor interfaces.
   * 
   * @param aSB the buffer to output into (must be non null)
   * @param aConsBeg the beginning of the visit methods
   * @param aConsEnd the end of the visit methods
   * @param aRet true if there is a user return parameter type, false otherwise
   * @param aArgu true if there is a user argument parameter type, false otherwise
   */
  void genAnyIVisitorEnd(final StringBuilder aSB, final String aConsBeg, final String aConsEnd,
                         final boolean aRet, final boolean aArgu) {
    final Spacing spc = new Spacing(Globals.INDENT_AMT);
    spc.updateSpc(+1);
    if (Globals.javaDocComments) {
      aSB.append(spc.spc).append("/*").append(LS);
      aSB.append(spc.spc).append(" * User grammar generated visit methods").append(LS);
      aSB.append(spc.spc).append(" */").append(LS).append(LS);
    }
    for (final Iterator<ClassInfo> e = classes.iterator(); e.hasNext();) {
      final ClassInfo classInfo = e.next();
      final String className = classInfo.getClassName();
      if (Globals.javaDocComments) {
        aSB.append(spc.spc).append("/**").append(LS);
        aSB.append(spc.spc).append(" * Visits a {@link ").append(className)
           .append("} node, whose children are the following :").append(LS);
        aSB.append(spc.spc).append(" * <p>").append(LS);
        aSB.append(classInfo.genAllFieldsComment(spc));
        aSB.append(spc.spc).append(" *").append(LS);
        aSB.append(spc.spc).append(" * @param n the node to visit").append(LS);
        if (aArgu)
          aSB.append(spc.spc).append(" * @param argu the user argument").append(LS);
        if (aRet)
          aSB.append(spc.spc).append(" * @return the user return information").append(LS);
        aSB.append(spc.spc).append(" */").append(LS);
      }
      aSB.append(spc.spc).append("public ").append(aConsBeg).append(classInfo.getQualifiedName()).append(aConsEnd)
         .append(";").append(LS).append(LS);
    }
    spc.updateSpc(-1);
    aSB.append("}").append(LS);
  }

  /**
   * Generates the "RetArgu" IVisitor (interface source) file.
   * 
   * @throws FileExistsException if the file already exists and the no overwrite flag has been set
   */
  public void genRetArguIVisitorFile() throws FileExistsException {
    try {
      final File file = new File(visitorsDir, Globals.iRetArguVisitorName + ".java");

      if (Globals.noOverwrite && file.exists())
        throw new FileExistsException(Globals.iRetArguVisitorName + ".java");

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
   * @throws FileExistsException if the file already exists and the no overwrite flag has been set
   */
  public void genRetIVisitorFile() throws FileExistsException {
    try {
      final File file = new File(visitorsDir, Globals.iRetVisitorName + ".java");

      if (Globals.noOverwrite && file.exists())
        throw new FileExistsException(Globals.iRetVisitorName + ".java");

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
   * @throws FileExistsException if the file already exists and the no overwrite flag has been set
   */
  public void genVoidArguIVisitorFile() throws FileExistsException {
    try {
      final File file = new File(visitorsDir, Globals.iVoidArguVisitorName + ".java");

      if (Globals.noOverwrite && file.exists())
        throw new FileExistsException(Globals.iVoidArguVisitorName + ".java");

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
   * @throws FileExistsException if the file already exists and the no overwrite flag has been set
   */
  public void genVoidIVisitorFile() throws FileExistsException {
    try {
      final File file = new File(visitorsDir, Globals.iVoidVisitorName + ".java");

      if (Globals.noOverwrite && file.exists())
        throw new FileExistsException(Globals.iVoidVisitorName + ".java");

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
   * @param aSB a buffer, used if not null
   * @return StringBuilder the given one if not null, or a new allocated one if null, completed with
   *         the base visitor methods source
   */
  StringBuilder genBaseRetArguVisitMethods(final StringBuilder aSB) {
    StringBuilder sb = aSB;
    if (sb == null)
      sb = new StringBuilder(100);
    sb.append(LS);
    if (Globals.javaDocComments) {
      sb.append("  /*").append(LS);
      sb.append("   * Base \"RetArgu\" visit methods").append(LS);
      sb.append("   */").append(LS).append(LS);
    }
    BaseClasses.genRetArguVisitNodeList(sb);
    sb.append(LS);
    BaseClasses.genRetArguVisitNodeListOpt(sb);
    sb.append(LS);
    BaseClasses.genRetArguVisitNodeOpt(sb);
    sb.append(LS);
    BaseClasses.genRetArguVisitNodeSeq(sb);
    sb.append(LS);
    BaseClasses.genRetArguVisitNodeToken(sb);
    sb.append(LS);
    return sb;
  }

  /**
   * Generates the base "Ret" visit methods.
   * 
   * @param aSB a buffer, used if not null
   * @return StringBuilder the given one if not null, or a new allocated one if null, completed with
   *         the base visitor methods source
   */
  StringBuilder genBaseRetVisitMethods(final StringBuilder aSB) {
    StringBuilder sb = aSB;
    if (sb == null)
      sb = new StringBuilder(100);
    sb.append(LS);
    if (Globals.javaDocComments) {
      sb.append("  /*").append(LS);
      sb.append("   * Base \"Ret\" visit methods").append(LS);
      sb.append("   */").append(LS).append(LS);
    }
    BaseClasses.genRetVisitNodeList(sb);
    sb.append(LS);
    BaseClasses.genRetVisitNodeListOpt(sb);
    sb.append(LS);
    BaseClasses.genRetVisitNodeOpt(sb);
    sb.append(LS);
    BaseClasses.genRetVisitNodeSeq(sb);
    sb.append(LS);
    BaseClasses.genRetVisitNodeToken(sb);
    sb.append(LS);
    return sb;
  }

  /**
   * Generates the base "VoidArgu" visit methods.
   * 
   * @param aSB a buffer, used if not null
   * @return StringBuilder the given one if not null, or a new allocated one if null, completed with
   *         the base visitor methods source
   */
  StringBuilder genBaseVoidArguVisitMethods(final StringBuilder aSB) {
    StringBuilder sb = aSB;
    if (sb == null)
      sb = new StringBuilder(100);
    sb.append(LS);
    if (Globals.javaDocComments) {
      sb.append("  /*").append(LS);
      sb.append("   * Base \"VoidArgu\" visit methods").append(LS);
      sb.append("   */").append(LS).append(LS);
    }
    BaseClasses.genVoidArguVisitNodeList(sb);
    sb.append(LS);
    BaseClasses.genVoidArguVisitNodeListOpt(sb);
    sb.append(LS);
    BaseClasses.genVoidArguVisitNodeOpt(sb);
    sb.append(LS);
    sb.append(LS);
    BaseClasses.genVoidArguVisitNodeSeq(sb);
    sb.append(LS);
    BaseClasses.genVoidArguVisitNodeToken(sb);
    sb.append(LS);
    return sb;
  }

  /**
   * Generates the base "Void" visit methods.
   * 
   * @param aSB a buffer, used if not null
   * @return StringBuilder the given one if not null, or a new allocated one if null, completed with
   *         the base visitor methods source
   */
  StringBuilder genBaseVoidVisitMethods(final StringBuilder aSB) {
    StringBuilder sb = aSB;
    if (sb == null)
      sb = new StringBuilder(100);
    sb.append(LS);
    if (Globals.javaDocComments) {
      sb.append("  /*").append(LS);
      sb.append("   * Base \"Void\" visit methods").append(LS);
      sb.append("   */").append(LS).append(LS);
    }
    BaseClasses.genVoidVisitNodeList(sb);
    sb.append(LS);
    BaseClasses.genVoidVisitNodeListOpt(sb);
    sb.append(LS);
    BaseClasses.genVoidVisitNodeOpt(sb);
    sb.append(LS);
    BaseClasses.genVoidVisitNodeSeq(sb);
    sb.append(LS);
    BaseClasses.genVoidVisitNodeToken(sb);
    sb.append(LS);
    return sb;
  }

  /**
   * Generates the class parameter types.
   * 
   * @param ret true if there is a user return parameter type, false otherwise
   * @param arg true if there is a user argument parameter type, false otherwise
   * @return the class parameter types string
   */
  static String genClassParamType(final boolean ret, final boolean arg) {
    if (ret) {
      if (arg) {
        return "<" + Globals.genRetType + ", " + Globals.genArguType + ">";
      } else {
        return "<" + Globals.genRetType + ">";
      }
    } else {
      if (arg) {
        return "<" + Globals.genArguType + ">";
      } else {
        return "";
      }
    }
  }

  /**
   * Generates the beginning of the visit methods.
   * 
   * @param arg true if there is a user argument parameter type, false otherwise
   * @return the beginning of the visit methods
   */
  static String genClassBegArgList(final boolean arg) {
    if (arg) {
      return Globals.genRetType + " visit(final ";
    } else {
      return "void visit(final ";
    }
  }

  /**
   * Generates the end of the visit methods.
   * 
   * @param arg true if there is a user argument parameter type, false otherwise
   * @return the end of the visit methods
   */
  static String genClassEndArgList(final boolean arg) {
    if (arg) {
      return " n, final " + (Globals.varargs ? Globals.genArgusType : Globals.genArguType) + " argu)";
    } else {
      return " n)";
    }
  }
}
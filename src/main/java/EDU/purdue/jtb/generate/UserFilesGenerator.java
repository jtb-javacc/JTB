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
import static EDU.purdue.jtb.common.Constants.emptyEnterExitHook;
import static EDU.purdue.jtb.common.Constants.fileHeaderComment;
import static EDU.purdue.jtb.common.Constants.genNodeVar;
import static EDU.purdue.jtb.common.Constants.iEnterExitHook;
import static EDU.purdue.jtb.common.Constants.iNode;
import static EDU.purdue.jtb.common.Constants.jtbHookEnter;
import static EDU.purdue.jtb.common.Constants.jtbHookExit;
import static EDU.purdue.jtb.common.Constants.nodeChoice;
import static EDU.purdue.jtb.common.Constants.nodeList;
import static EDU.purdue.jtb.common.Constants.nodeListOptional;
import static EDU.purdue.jtb.common.Constants.nodeOptional;
import static EDU.purdue.jtb.common.Constants.nodeSequence;
import static EDU.purdue.jtb.common.Constants.nodeToken;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import EDU.purdue.jtb.common.JTBOptions;
import EDU.purdue.jtb.common.Messages;
import EDU.purdue.jtb.common.Spacing;
import EDU.purdue.jtb.common.UserClassInfo;
import EDU.purdue.jtb.common.UserClassInfo.FieldInfo;
import EDU.purdue.jtb.parser.syntaxtree.NodeConstants;

/**
 * Class {@link UserFilesGenerator} contains methods to generate: CODEJAVA
 * <ul>
 * <li>the (grammar) user nodes classes (genUserXxx, using {@link UserClassInfo}),</li>
 * <li>the hook interface and empty class files (genXxxEnterExitHook).</li>
 * </ul>
 * <p>
 * Class maintains a state, and is not supposed to be run in parallel threads (on the same grammar); however
 * it internally uses different threads (streams) to generate the different user classes in parallel.
 * </p>
 * TESTCASE some to add
 *
 * @author Marc Mazas
 * @version 1.4.0 : 05-08/2009 : MMa : adapted to JavaCC v4.2 grammar and JDK 1.5<br>
 *          1.4.0 : 11/2009 : MMa : fixed directories creation errors
 * @version 1.4.6 : 01/2011 : FA/MMa : added -va and -npfx and -nsfx options
 * @version 1.4.7 : 09/2012 : MMa : added missing generated visit methods (NodeChoice and NodeTCF)
 * @version 1.4.8 : 10/2012 : MMa : tuned javadoc comments for nodes with no child<br>
 *          1.4.8 : 10/2014 : MMa : fixed NPE on classes without fields
 * @version 1.5.0 : 01-03/2017 : MMa : used try-with-resource ; added NodeConstants generation ; added
 *          children methods generation ; applied changes following new class FieldInfo ; added signature
 *          files generation ; enhanced to VisitorInfo based visitor generation ; renamed from FilesGenerator
 *          ; subject to global packages and classes refactoring ; removed NodeTCF related code
 * @version 1.5.1 : 07-08/2023 : MMa : changed no overwrite management<br>
 * @version 1.5.1 : 08/2023 : MMa : editing changes for coverage analysis; changes due to the NodeToken
 *          replacement by Token
 */
public class UserFilesGenerator {
  
  /** The global JTB options */
  private final JTBOptions                 jopt;
  /** The messages handler */
  final Messages                           mess;
  /** The {@link CommonCodeGenerator} */
  private final CommonCodeGenerator        ccg;
  /** The user classes list */
  private final List<UserClassInfo>        classes;
  /** The common code (that does not depend of the class) */
  private final StringBuilder              commonCode;
  /** The buffer to write to (for sequential generation) */
  private final StringBuilder              gsb;
  /** The buffer to write to (for parallel generation) */
  private final ThreadLocal<StringBuilder> tlgsb;
  /** The BufferedWriter buffer size */
  private static final int                 BR_BUF_SZ = 16 * 1024;
  
  /**
   * Constructor. Creates the syntaxtree and hook directories if they do not exist.
   *
   * @param aJopt - the JTB options
   * @param aCcg - the {@link CommonCodeGenerator}
   * @param aClasses - the list of {@link UserClassInfo} classes instances
   */
  public UserFilesGenerator(final JTBOptions aJopt, final CommonCodeGenerator aCcg,
      final List<UserClassInfo> aClasses) {
    jopt = aJopt;
    mess = aJopt.mess;
    ccg = aCcg;
    classes = aClasses;
    gsb = jopt.noParallel ? new StringBuilder(BR_BUF_SZ) : null;
    tlgsb = jopt.noParallel ? null : ThreadLocal.withInitial(() -> new StringBuilder(BR_BUF_SZ));
    commonCode = genCommonCode();
  }
  
  /**
   * Outputs the formatted nodes classes list.
   *
   * @param aPw - a PrintWriter to output on
   */
  public void outputFormattedNodesClassesList(final PrintWriter aPw) {
    aPw.print(formatNodesClassesList(classes));
    aPw.flush();
  }
  
  /**
   * Formats the nodes classes list.
   *
   * @param aClasses - the list of {@link UserClassInfo} classes instances
   * @return StringBuilder a new allocated buffer filled with the formatted nodes classes list
   */
  static StringBuilder formatNodesClassesList(final List<UserClassInfo> aClasses) {
    final Spacing spc = new Spacing(INDENT_AMT);
    final StringBuilder sb = new StringBuilder(aClasses.size() * 100);
    for (final UserClassInfo uci : aClasses) {
      final String className = uci.fixedClassName;
      sb.append("class ").append(className).append(":").append(LS);
      spc.updateSpc(+1);
      if (uci.fields != null) {
        for (final FieldInfo fi : uci.fields) {
          sb.append(spc.spc).append(fi.fixedType).append(' ').append(fi.name).append(LS);
        }
      }
      sb.append(LS);
      spc.updateSpc(-1);
    }
    return sb;
  }
  
  /**
   * Generates user nodes (classes source) files.
   *
   * @param aNodesDir - the nodes directory File
   * @return the number of generated files
   * @throws Exception - on any exception
   */
  public int genUserNodesFiles(final File aNodesDir) throws Exception {
    if (jopt.noParallel) {
      // generate classes in sequence
      int n = 0;
      for (final UserClassInfo uci : classes) {
        final File file = new File(aNodesDir, uci.fixedClassName + ".java");
        if (jopt.noOverwrite //
            && file.exists()) {
          mess.warning("File " + uci.fixedClassName
              + " exists and is not overwritten as the no overwrite flag has been set");
          continue;
        }
        // in case of exception something is printed :-(
        @SuppressWarnings("resource") PrintWriter pw = null;
        try {
          // TODO change to OutputStreamWriter on a FileOutputStream for handling UTF-8 names like bp_v£
          pw = new PrintWriter(new BufferedWriter(new FileWriter(file), BR_BUF_SZ));
          gsb.setLength(0);
          genUserNodeClass(gsb, uci);
          mess.info("File " + uci.fixedClassName + " generated");
          n++;
        } finally {
          if (pw != null) {
            pw.print(gsb);
            pw.close();
          }
        }
      }
      return n;
    } else {
      // generate classes in parallel ; the IntStream mapToInt(ToIntFunction<? super UserClassInfo> mapper)
      // function applies the function between the {} which works on each class (uci) and returns an int,
      // and the results are summed by sum(); needs a ThreadLocal buffer
      final int res = classes.stream().parallel().mapToInt(uci -> {
        // final int res = classes.stream().sequential().mapToInt(uci -> {
        final File file = new File(aNodesDir, uci.fixedClassName + ".java");
        if (jopt.noOverwrite //
            && file.exists()) {
          mess.warning("File " + uci.fixedClassName
              + " exists and is not overwritten as the no overwrite flag has been set");
          return 0;
        }
        // in case of exception something is printed :-(
        @SuppressWarnings("resource") PrintWriter pw = null;
        final StringBuilder sb = tlgsb.get();
        sb.setLength(0);
        try {
          // TODO change to OutputStreamWriter on a FileOutputStream for handling UTF-8 names like bp_v£
          pw = new PrintWriter(new BufferedWriter(new FileWriter(file), BR_BUF_SZ));
          genUserNodeClass(sb, uci);
          return 1;
        } catch (final IOException ex) {
          Messages.hardErr("IOException on " + uci.fixedClassName, ex);
          return 1 + NodeConstants.NB_JTB_USER_NODES;
        } catch (final Exception ex) {
          Messages.hardErr("Exception on " + uci.fixedClassName, ex);
          return 1 + (3 * NodeConstants.NB_JTB_USER_NODES);
        } finally {
          if (pw != null) {
            pw.print(sb);
            pw.close();
          }
        }
        // end processing one stream item uci
      }).sum(); // sums the return codes
      if (res > (3 * NodeConstants.NB_JTB_USER_NODES)) {
        throw new Exception("Exception(s) on some user nodes class files");
      }
      if (res > NodeConstants.NB_JTB_USER_NODES) {
        throw new IOException("IOException(s) on some user nodes class files");
      }
      return res;
    }
  }
  
  /**
   * Generates a user node class source string.
   *
   * @param aSb - a buffer to append to (will be allocated if null)
   * @param aUci - the class to generate the source string
   */
  void genUserNodeClass(final StringBuilder aSb, final UserClassInfo aUci) {
    StringBuilder sb = aSb;
    if (sb == null) {
      sb = new StringBuilder(BR_BUF_SZ);
    }
    final Spacing spc = new Spacing(INDENT_AMT);
    
    sb.append(fileHeaderComment).append(LS);
    if (jopt.nodesPackageName != null)
      sb.append("package ").append(jopt.nodesPackageName).append(";").append(LS).append(LS);
    if (jopt.childrenMethods) {
      sb.append("import ").append("java.util.ArrayList").append(';').append(LS);
      sb.append("import ").append("java.util.List").append(';').append(LS);
    }
    ccg.tokenImport(aSb);
    ccg.interfaceVisitorsImports(sb);
    sb.append(LS);
    if (jopt.javaDocComments) {
      sb.append("/**").append(LS);
      sb.append(" * JTB node class for the production ").append(aUci.fixedClassName).append(":<br>")
          .append(LS);
      sb.append(" * Corresponding grammar:<br>").append(LS);
      // generate the javadoc for the class fields, with no indentation
      ccg.fmtAllFieldsJavadocCmts(sb, spc, aUci);
      sb.append(" */").append(LS);
    } else {
      sb.append("@SuppressWarnings(\"javadoc\")").append(LS);
    }
    genClassString(sb, spc, aUci);
  }
  
  /**
   * Generates the IEnterExitHook (interface source) file.
   *
   * @param aHookDir - the hook directory File
   * @return OK_RC or FILE_EXISTS_RC
   * @throws IOException - if IO problem
   */
  public int genIEnterExitHookFile(final File aHookDir) throws IOException {
    final File file = new File(aHookDir, iEnterExitHook + ".java");
    if (jopt.noOverwrite //
        && file.exists()) {
      return FILE_EXISTS_RC;
    }
    try (PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(file), 1500))) {
      final StringBuilder sb = jopt.noParallel ? gsb : tlgsb.get();
      sb.setLength(0);
      pw.print(genIEnterExitHook(sb, classes));
      return OK_RC;
    } catch (final IOException e) {
      Messages.hardErr("IOException on " + file.getPath(), e);
      throw e;
    }
  }
  
  /**
   * Generates the IEnterExitHook interface source.
   *
   * @param aSb - a buffer to append to (will be allocated if null)
   * @param aClasses - the list of {@link UserClassInfo} classes instances
   * @return StringBuilder the given one if not null, or a new allocated one if null, completed with the hook
   *         interface source
   */
  StringBuilder genIEnterExitHook(final StringBuilder aSb, final List<UserClassInfo> aClasses) {
    StringBuilder sb = aSb;
    if (sb == null) {
      sb = new StringBuilder(1500);
    }
    
    sb.append(fileHeaderComment).append(LS);
    if (jopt.hookPackageName != null) {
      sb.append("package ").append(jopt.hookPackageName).append(";").append(LS).append(LS);
      if (!jopt.hookPackageName.equals(jopt.nodesPackageName)) {
        sb.append("import ").append(jopt.nodesPackageName).append(".*;").append(LS).append(LS);
      }
    } else {
      sb.append("import ").append("*;").append(LS).append(LS);
    }
    if (jopt.javaDocComments) {
      sb.append("/** All hooks must implement this interface. */").append(LS);
    } else {
      sb.append("@SuppressWarnings(\"javadoc\")").append(LS);
    }
    sb.append("public interface ").append(iEnterExitHook).append(" {").append(LS);
    
    final Spacing spc = new Spacing(INDENT_AMT);
    spc.updateSpc(+1);
    // all enter methods first
    sb.append(spc.spc).append("/*").append(LS);
    sb.append(spc.spc).append(" * Enter methods").append(LS);
    sb.append(spc.spc).append(" */").append(LS).append(LS);
    for (final UserClassInfo uci : aClasses) {
      if (jopt.javaDocComments) {
        sb.append(spc.spc).append("/** Called upon entering a {@link ").append(uci.fixedClassName)
            .append("} node. */").append(LS);
      }
      sb.append(spc.spc).append("public void ").append(uci.fixedClassName).append(jtbHookEnter).append("();")
          .append(LS).append(LS);
    }
    // then all exit methods
    sb.append(spc.spc).append("/*").append(LS);
    sb.append(spc.spc).append(" * Exit methods").append(LS);
    sb.append(spc.spc).append(" */").append(LS).append(LS);
    for (final UserClassInfo uci : aClasses) {
      if (jopt.javaDocComments) {
        sb.append(spc.spc).append("/** Called upon exiting a {@link ").append(uci.fixedClassName)
            .append("} node. *").append(LS);
        sb.append(spc.spc).append(" * @param ").append(genNodeVar).append(" - the node created").append(LS);
        sb.append(spc.spc).append(" */").append(LS);
      }
      sb.append(spc.spc).append("public void ").append(uci.fixedClassName).append(jtbHookExit)
          .append("(final ").append(uci.fixedClassName).append(' ').append(genNodeVar).append(");").append(LS)
          .append(LS);
    }
    spc.updateSpc(-1);
    sb.append(spc.spc).append("}").append(LS);
    return sb;
  }
  
  /**
   * Generates the genEmtpyEnterExitHookFile (class source) file.
   *
   * @param aHookDir - the nodes directory File
   * @return OK_RC or FILE_EXISTS_RC
   * @throws IOException - if IO problem
   */
  public int genEmtpyEnterExitHookFile(final File aHookDir) throws IOException {
    final File file = new File(aHookDir, emptyEnterExitHook + ".java");
    if (jopt.noOverwrite //
        && file.exists()) {
      return FILE_EXISTS_RC;
    }
    try (PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(file), 1500))) {
      final StringBuilder sb = jopt.noParallel ? gsb : tlgsb.get();
      sb.setLength(0);
      pw.print(genEmtpyEnterExitHook(sb, classes));
      return OK_RC;
    } catch (final IOException e) {
      Messages.hardErr("IOException on " + file.getPath(), e);
      throw e;
    }
  }
  
  /**
   * Generates the EmtpyEnterExitHook class source.
   *
   * @param aSb - a buffer to append to (will be allocated if null)
   * @param aClasses - the list of {@link UserClassInfo} classes instances
   * @return StringBuilder the given one if not null, or a new allocated one if null, completed with the hook
   *         interface source
   */
  StringBuilder genEmtpyEnterExitHook(final StringBuilder aSb, final List<UserClassInfo> aClasses) {
    StringBuilder sb = aSb;
    if (sb == null) {
      sb = new StringBuilder(1500);
    }
    
    sb.append(fileHeaderComment).append(LS);
    if (jopt.hookPackageName != null) {
      sb.append("package ").append(jopt.hookPackageName).append(";").append(LS).append(LS);
      if (!jopt.hookPackageName.equals(jopt.nodesPackageName)) {
        sb.append("import ").append(jopt.nodesPackageName).append(".*;").append(LS).append(LS);
      }
    } else {
      sb.append("import ").append("*;").append(LS).append(LS);
    }
    if (jopt.javaDocComments) {
      sb.append("/**").append(LS);
      sb.append(" * An empty implementation of {@link ").append(iEnterExitHook).append("}.").append(LS);
      sb.append(" */").append(LS);
      sb.append("@SuppressWarnings(\"unused\")").append(LS);
    } else {
      sb.append("@SuppressWarnings({\"javadoc\",\"unused\"})").append(LS);
    }
    sb.append("public class ").append(emptyEnterExitHook).append(" implements ").append(iEnterExitHook)
        .append(" {").append(LS);
    
    final Spacing spc = new Spacing(INDENT_AMT);
    spc.updateSpc(+1);
    // all enter methods first
    sb.append(spc.spc).append("/*").append(LS);
    sb.append(spc.spc).append(" * Enter methods").append(LS);
    sb.append(spc.spc).append(" */").append(LS).append(LS);
    for (final UserClassInfo uci : aClasses) {
      if (jopt.javaDocComments) {
        sb.append(spc.spc).append("/** Called upon entering a {@link ").append(uci.fixedClassName)
            .append("} node. */").append(LS);
      }
      sb.append(spc.spc).append("@Override").append(LS);
      sb.append(spc.spc).append("public void ").append(uci.fixedClassName).append(jtbHookEnter).append("() {")
          .append(LS);
      spc.updateSpc(+1);
      sb.append(spc.spc).append("// to be filled if necessary").append(LS);
      spc.updateSpc(-1);
      sb.append(spc.spc).append("}").append(LS).append(LS);
    }
    // then all exit methods
    sb.append(spc.spc).append("/*").append(LS);
    sb.append(spc.spc).append(" * Exit methods").append(LS);
    sb.append(spc.spc).append(" */").append(LS).append(LS);
    for (final UserClassInfo uci : aClasses) {
      if (jopt.javaDocComments) {
        sb.append(spc.spc).append("/** Called upon exiting a {@link ").append(uci.fixedClassName)
            .append("} node. *").append(LS);
        sb.append(spc.spc).append(" * @param ").append(genNodeVar).append(" - the node created").append(LS);
        sb.append(spc.spc).append(" */").append(LS);
      }
      sb.append(spc.spc).append("@Override").append(LS);
      sb.append(spc.spc).append("public void ").append(uci.fixedClassName).append(jtbHookExit)
          .append("(final ").append(uci.fixedClassName).append(' ').append(genNodeVar).append(") {")
          .append(LS);
      spc.updateSpc(+1);
      sb.append(spc.spc).append("// to be filled if necessary").append(LS);
      spc.updateSpc(-1);
      sb.append(spc.spc).append("}").append(LS).append(LS);
    }
    spc.updateSpc(-1);
    sb.append(spc.spc).append("}").append(LS);
    return sb;
  }
  
  /**
   * Generates the node class code into a newly allocated buffer.
   *
   * @param aSb - a buffer to append to (will be allocated if null)
   * @param aSpc - the current indentation
   * @param aUci - the class to generate the source string
   */
  private void genClassString(final StringBuilder aSb, final Spacing aSpc, final UserClassInfo aUci) {
    if (aUci.fields != null) {
      // normal case, for BNFProductions
      genBNFProductionClassString(aSb, aSpc, aUci);
    } else {
      // specific case for JavaCodeProductions
      genJavaCodeProductionClassString(aSb, aSpc, aUci);
    }
  }
  
  /**
   * Generates the node class code for a BNFProduction into a newly allocated buffer.
   *
   * @param aSb - a buffer to append to (will be allocated if null)
   * @param aSpc - the current indentation
   * @return the buffer with the node class code
   * @param aUci - the class to generate the source string
   */
  private StringBuilder genBNFProductionClassString(final StringBuilder aSb, final Spacing aSpc,
      final UserClassInfo aUci) {
    
    StringBuilder sb = aSb;
    if (sb == null) {
      sb = new StringBuilder(2048);
    }
    final int nbf = aUci.fields.size();
    
    /*
     * class declaration
     */
    
    sb.append(aSpc.spc).append("public class " + aUci.fixedClassName);
    
    if (jopt.nodesSuperclass != null) {
      sb.append(" extends ").append(jopt.nodesSuperclass);
    }
    sb.append(" implements ").append(iNode).append(" {").append(LS).append(LS);
    aSpc.updateSpc(+1);
    
    /*
     * data fields declarations
     */
    
    for (int i = 0; i < nbf; i++) {
      final FieldInfo fi = aUci.fields.get(i);
      if (jopt.javaDocComments) {
        sb.append(aSpc.spc).append("/** Child node " + i + " */").append(LS);
      }
      sb.append(aSpc.spc).append("public ").append(fi.fixedType).append(' ').append(fi.name).append(";")
          .append(LS).append(LS);
    }
    
    ccg.parentPointerDeclaration(sb);
    
    ccg.genSerialUIDDeclaration(sb);
    
    /*
     * standard constructor (header + body)
     */
    
    // header
    if (jopt.javaDocComments) {
      sb.append(aSpc.spc).append("/**").append(LS);
      sb.append(aSpc.spc).append(" * Constructs the node with ");
      if (nbf > 1) {
        sb.append("all its children nodes.").append(LS);
      } else if (nbf == 1) {
        sb.append("its child node.").append(LS);
      } else {
        sb.append("no child node.").append(LS);
      }
      sb.append(aSpc.spc).append(" *").append(LS);
      if (nbf > 0) {
        sb.append(aSpc.spc).append(" * @param n0 - ").append(nbf > 1 ? "first" : "the").append(" child node")
            .append(LS);
      }
      for (int i = 1; i < nbf; i++) {
        sb.append(aSpc.spc).append(" * @param ").append(genNodeVar).append(i).append(" - next child node")
            .append(LS);
      }
      sb.append(aSpc.spc).append(" */").append(LS);
    }
    sb.append(aSpc.spc).append("public ").append(aUci.fixedClassName).append("(");
    for (int i = 0; i < nbf; i++) {
      final FieldInfo fi = aUci.fields.get(i);
      if (i > 0) {
        sb.append(", ");
      }
      sb.append("final ").append(fi.fixedType).append(' ').append(genNodeVar).append(i);
    }
    sb.append(") {").append(LS);
    
    // body
    aSpc.updateSpc(+1);
    for (int i = 0; i < nbf; i++) {
      final String name = aUci.fields.get(i).name;
      sb.append(aSpc.spc).append(name).append(" = ").append(genNodeVar).append(i).append(";").append(LS);
      ccg.parentPointerSetCall(sb, name);
    }
    
    aSpc.updateSpc(-1);
    sb.append(aSpc.spc).append("}").append(LS);
    
    sb.append(commonCode);
    
    /*
     * (class specific) children methods
     */
    
    if (jopt.childrenMethods) {
      
      int nbLbc = 0;
      int nbLuc = 0;
      for (final FieldInfo fi : aUci.fields) {
        if (isBaseNode(fi.type)) {
          nbLbc++;
        } else {
          nbLuc++;
        }
      }
      
      if (jopt.javaDocComments) {
        CommonCodeGenerator.childrenAllComment(sb, "number", "(always " + nbf + "))");
      }
      sb.append("  @Override").append(LS);
      sb.append("  public int getNbAllChildren() {").append(LS);
      sb.append("    return ").append(nbf).append(";").append(LS);
      sb.append("  }").append(LS).append(LS);
      
      if (jopt.javaDocComments) {
        CommonCodeGenerator.childrenBaseorUserComment(sb, "number", "base", "(always " + nbLbc + "))");
      }
      sb.append("  @Override").append(LS);
      sb.append("  public int getNbBaseChildren() {").append(LS);
      sb.append("    return ").append(nbLbc).append(";").append(LS);
      sb.append("  }").append(LS).append(LS);
      
      if (jopt.javaDocComments) {
        CommonCodeGenerator.childrenBaseorUserComment(sb, "number", "user", "(always " + nbLuc + "))");
      }
      sb.append("  @Override").append(LS);
      sb.append("  public int getNbUserChildren() {").append(LS);
      sb.append("    return ").append(nbLuc).append(";").append(LS);
      sb.append("  }").append(LS).append(LS);
      
      if (jopt.javaDocComments) {
        CommonCodeGenerator.childrenAllComment(sb, "list", "(always " + nbf + " nodes))");
      }
      sb.append("  @Override").append(LS);
      sb.append("  public List<INode> getAllChildren() {").append(LS);
      sb.append("    if (lac == null) {").append(LS);
      sb.append("      lac = new ArrayList<>(").append(nbf).append(");").append(LS);
      for (final FieldInfo fi : aUci.fields) {
        sb.append("      lac.add(").append(fi.name).append(");").append(LS);
      }
      sb.append("    }").append(LS);
      sb.append("    return lac;").append(LS);
      sb.append("  }").append(LS).append(LS);
      
      if (jopt.javaDocComments) {
        CommonCodeGenerator.childrenAllComment(sb, "list", "(always " + nbLbc + " nodes))");
      }
      sb.append("  @Override").append(LS);
      sb.append("  public List<INode> getBaseChildren() {").append(LS);
      sb.append("    if (lbc == null) {").append(LS);
      sb.append("      lbc = new ArrayList<>(").append(nbLbc).append(");").append(LS);
      for (final FieldInfo fi : aUci.fields) {
        if (isBaseNode(fi.type)) {
          sb.append("      lbc.add(").append(fi.name).append(");").append(LS);
        }
      }
      sb.append("    }").append(LS);
      sb.append("    return lbc;").append(LS);
      sb.append("  }").append(LS).append(LS);
      
      if (jopt.javaDocComments) {
        CommonCodeGenerator.childrenAllComment(sb, "list", "(always " + nbLuc + " nodes))");
      }
      sb.append("  @Override").append(LS);
      sb.append("  public List<INode> getUserChildren() {").append(LS);
      sb.append("    if (luc == null) {").append(LS);
      sb.append("      luc = new ArrayList<>(").append(nbLuc).append(");").append(LS);
      for (final FieldInfo fi : aUci.fields) {
        if (!isBaseNode(fi.type)) {
          sb.append("      luc.add(").append(fi.name).append(");").append(LS);
        }
      }
      sb.append("    }").append(LS);
      sb.append("    return luc;").append(LS);
      sb.append("  }").append(LS).append(LS);
      
    }
    
    aSpc.updateSpc(-1);
    sb.append(aSpc.spc).append("}").append(LS);
    return sb;
    
  }
  
  /**
   * Generates the node class code for a JavaCodeProduction into a newly allocated buffer.
   *
   * @param aSb - a buffer to append to (will be allocated if null)
   * @param aSpc - the current indentation
   * @return the buffer with the node class code
   * @param aUci - the class to generate the source string
   */
  private StringBuilder genJavaCodeProductionClassString(final StringBuilder aSb, final Spacing aSpc,
      final UserClassInfo aUci) {
    
    StringBuilder sb = aSb;
    if (sb == null) {
      sb = new StringBuilder(1024);
    }
    
    /*
     * class declaration
     */
    
    sb.append(aSpc.spc).append("public class " + aUci.fixedClassName);
    
    if (jopt.nodesSuperclass != null) {
      sb.append(" extends ").append(jopt.nodesSuperclass);
    }
    sb.append(" implements ").append(iNode).append(" {").append(LS).append(LS);
    aSpc.updateSpc(+1);
    
    /*
     * data fields declarations
     */
    
    ccg.parentPointerDeclaration(sb);
    
    ccg.genSerialUIDDeclaration(sb);
    
    /*
     * standard constructor
     */
    
    if (jopt.javaDocComments) {
      sb.append(aSpc.spc).append("/**").append(LS);
      sb.append(aSpc.spc).append(" * Constructs the node (which has no child).").append(LS);
      sb.append(aSpc.spc).append(" */").append(LS);
    }
    sb.append(aSpc.spc).append("public ").append(aUci.fixedClassName).append("() {").append(LS);
    sb.append(aSpc.spc).append("}").append(LS);
    
    /*
     * Visit methods, parent methods, end class
     */
    
    sb.append(commonCode);
    
    /*
     * (class specific) children methods
     */
    
    if (jopt.childrenMethods) {
      
      if (jopt.javaDocComments) {
        CommonCodeGenerator.childrenAllComment(sb, "number", "(0))");
      }
      sb.append("  @Override").append(LS);
      sb.append("  public int getNbAllChildren() {").append(LS);
      sb.append("    return 0;").append(LS);
      sb.append("  }").append(LS).append(LS);
      
      if (jopt.javaDocComments) {
        CommonCodeGenerator.childrenBaseorUserComment(sb, "number", "base", "(0))");
      }
      sb.append("  @Override").append(LS);
      sb.append("  public int getNbBaseChildren() {").append(LS);
      sb.append("    return 0;").append(LS);
      sb.append("  }").append(LS).append(LS);
      
      if (jopt.javaDocComments) {
        CommonCodeGenerator.childrenBaseorUserComment(sb, "number", "user", "(0))");
      }
      sb.append("  @Override").append(LS);
      sb.append("  public int getNbUserChildren() {").append(LS);
      sb.append("    return 0;").append(LS);
      sb.append("  }").append(LS).append(LS);
      
      if (jopt.javaDocComments) {
        CommonCodeGenerator.childrenAllComment(sb, "list", "(always 0 node))");
      }
      sb.append("  @Override").append(LS);
      sb.append("  public List<INode> getAllChildren() {").append(LS);
      sb.append("    if (lac == null) {").append(LS);
      sb.append("      lac = new ArrayList<>(0);").append(LS);
      sb.append("    }").append(LS);
      sb.append("    return lac;").append(LS);
      sb.append("  }").append(LS).append(LS);
      
      if (jopt.javaDocComments) {
        CommonCodeGenerator.childrenAllComment(sb, "list", "(always 0 node))");
      }
      sb.append("  @Override").append(LS);
      sb.append("  public List<INode> getBaseChildren() {").append(LS);
      sb.append("    if (lbc == null) {").append(LS);
      sb.append("      lbc = new ArrayList<>(0);").append(LS);
      sb.append("    }").append(LS);
      sb.append("    return lbc;").append(LS);
      sb.append("  }").append(LS).append(LS);
      
      if (jopt.javaDocComments) {
        CommonCodeGenerator.childrenAllComment(sb, "list", "(always 0 node))");
      }
      sb.append("  @Override").append(LS);
      sb.append("  public List<INode> getUserChildren() {").append(LS);
      sb.append("    if (luc == null) {").append(LS);
      sb.append("      luc = new ArrayList<>(0);").append(LS);
      sb.append("    }").append(LS);
      sb.append("    return luc;").append(LS);
      sb.append("  }").append(LS).append(LS);
      
    }
    
    sb.append(aSpc.spc).append("}").append(LS);
    return sb;
  }
  
  /**
   * Generates common code (visit methods, parent methods, end class).
   *
   * @return the generated common code
   */
  private StringBuilder genCommonCode() {
    
    final StringBuilder sb = new StringBuilder(1024);
    ccg.classesAcceptMethods(sb);
    ccg.parentPointerGetterSetterImpl(sb);
    if (jopt.childrenMethods) {
      CommonCodeGenerator.cmtHeaderChildrenMethods(sb);
      ccg.listAllChildren(sb);
      ccg.listBaseChildren(sb);
      ccg.listUserChildren(sb);
      ccg.falseBaseNode(sb);
    }
    sb.append(LS);
    return sb;
  }
  
  /**
   * @param aNodeName - the Node name
   * @return - true if the node is a base node, false if a user node
   */
  private static boolean isBaseNode(final String aNodeName) {
    if (aNodeName.startsWith("Node")) {
      if (aNodeName.equals(nodeToken)) {
        return true;
      }
      if (aNodeName.equals(nodeSequence)) {
        return true;
      }
      if (aNodeName.equals(nodeChoice)) {
        return true;
      }
      if (aNodeName.equals(nodeListOptional)) {
        return true;
      }
      if (aNodeName.equals(nodeList)) {
        return true;
      }
      if (aNodeName.equals(nodeOptional)) {
        return true;
      }
    }
    return false;
  }
}

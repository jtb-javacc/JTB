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
 * Indiana by Kevin Tao and Jens Palsberg. No charge may be made for copies, derivations, or distributions of
 * this material without the express written consent of the copyright holder. Neither the name of the
 * University nor the name of the author may be used to endorse or promote products derived from this material
 * without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND WITHOUT ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, WITHOUT
 * LIMITATION, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR ANY PARTICULAR PURPOSE.
 */
package EDU.purdue.jtb.generate;

import static EDU.purdue.jtb.common.Constants.FILE_EXISTS_RC;
import static EDU.purdue.jtb.common.Constants.INDENT_AMT;
import static EDU.purdue.jtb.common.Constants.LS;
import static EDU.purdue.jtb.common.Constants.OK_RC;
import static EDU.purdue.jtb.common.Constants.fileHeaderComment;
import static EDU.purdue.jtb.common.Constants.genNodeVar;
import static EDU.purdue.jtb.common.Constants.iNode;
import static EDU.purdue.jtb.common.Constants.iNodeList;
import static EDU.purdue.jtb.common.Constants.nodeList;
import static EDU.purdue.jtb.common.Constants.nodeListOptional;
import static EDU.purdue.jtb.common.Constants.nodeOptional;
import static EDU.purdue.jtb.common.Constants.nodeToken;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import EDU.purdue.jtb.common.Constants;
import EDU.purdue.jtb.common.JTBOptions;
import EDU.purdue.jtb.common.Messages;
import EDU.purdue.jtb.common.Spacing;
import EDU.purdue.jtb.common.UserClassInfo;
import EDU.purdue.jtb.common.UserClassInfo.FieldInfo;

/**
 * Class {@link TreeFormatterGenerator} generates the TreeFormatter visitor which is a skeleton
 * pretty-printer.<br>
 * Using some pre-defined methods, users can quickly and easily create a formatter for their grammar.<br>
 * The formatter will then take a tree, insert token location information into the Tokens of the tree.<br>
 * TreeDumper can then be used to output the result.<br>
 * Note that unlike the other automatically generated file, since this one must be edited to be useful, JTB
 * will not overwrite this file automatically.<br>
 * JTB will take this precaution for the other files only if the "-w" command-line parameter is used. CODEJAVA
 * <p>
 * This visitor is supposed to be run once and not supposed to be run in parallel threads (on the same
 * grammar).
 * </p>
 *
 * @author Marc Mazas
 * @version 1.4.0 : 05/2009 : MMa : adapted to JavaCC v4.2 grammar and JDK 1.5
 * @version 1.4.3 : 03/2010 : MMa : fixed output of constructor
 * @version 1.4.8 : 10/2014 : MMa : fixed NPE on classes without fields
 * @version 1.5.0 : 01-06/2017 : MMa : used try-with-resource ; applied changes following new class FieldInfo
 *          ; passed some generated methods static ; always generate all javadoc comments<br>
 * @version 1.5.1 : 08/2023 : MMa : editing changes for coverage analysis; changes due to the NodeToken
 *          replacement by Token
 */
public class TreeFormatterGenerator {
  
  /** The global JTB options */
  private final JTBOptions          jopt;
  /** The {@link CommonCodeGenerator} */
  private final CommonCodeGenerator ccg;
  /** The messages handler */
  final Messages                    mess;
  /** The visitor source file name */
  public static final String        outFilename = Constants.treeFormatterName + ".java";
  /** The visitors directory */
  private final File                visitorDir;
  /** The classes list */
  private final List<UserClassInfo> classList;
  /** The buffer to print into */
  private final StringBuilder       sb;
  
  /**
   * Constructor with a given list of classes. Will create the visitors directory if it does not exist.
   *
   * @param aJopt - the JTB options
   * @param aCcg - the {@link CommonCodeGenerator}
   * @param classes - the list of classes
   * @param aMess - the messages handler
   */
  public TreeFormatterGenerator(final JTBOptions aJopt, final CommonCodeGenerator aCcg, final Messages aMess,
      final List<UserClassInfo> classes) {
    jopt = aJopt;
    ccg = aCcg;
    mess = aMess;
    classList = classes;
    sb = new StringBuilder(500 * classes.size());
    visitorDir = jopt.visitorsDirName == null ? null : new File(jopt.visitorsDirName);
    if (visitorDir != null && !visitorDir.exists()) {
      visitorDir.mkdir();
    }
  }
  
  /**
   * Saves the current buffer in the output file (global variable).<br>
   * Since the user is expected to edit and customize this file, this method will never overwrite the file if
   * it exists, regardless of the global no overwrite flag.
   *
   * @return OK_RC or FILE_EXISTS_RC
   * @throws IOException if IO problem
   */
  public int saveToFile() throws IOException {
    final File file = new File(visitorDir, outFilename);
    if (jopt.noOverwrite && file.exists()) {
      mess.warning("File " + file.getPath() + " exists and was not overwritten");
      return FILE_EXISTS_RC;
    }
    try (PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(file), sb.length()))) {
      pw.print(sb);
      return OK_RC;
    } catch (final IOException e) {
      Messages.hardErr("IOException on " + file.getPath(), e);
      throw e;
    }
  }
  
  // TO DO change the following methods with spc.spc
  
  /**
   * Generates the tree formatter visitor source in its file.<br>
   */
  public void generateTreeFormatter() {
    
    sb.append(fileHeaderComment).append(LS);
    sb.append("package ").append(jopt.visitorsPackageName).append(";").append(LS).append(LS);
    
    sb.append("import ");
    if (jopt.grammarPackageName != null)
      sb.append(jopt.grammarPackageName).append('.');
    sb.append(nodeToken).append(';').append(LS);
    sb.append("import java.util.ArrayList;").append(LS);
    sb.append("import java.util.Iterator;").append(LS).append(LS);
    sb.append("import ");
    if (jopt.nodesPackageName != null)
      sb.append(jopt.nodesPackageName).append(".");
    sb.append("*;").append(LS).append(LS);
    sb.append("/**").append(LS);
    sb.append(" * A skeleton output formatter for your language grammar.<br>").append(LS);
    sb.append(" * Using the add() method along with force(), indent(), and outdent(),<br>").append(LS);
    sb.append(" * you can easily specify how this visitor will format the given syntax tree.<br>").append(LS);
    sb.append(" * See the JTB documentation for more details.").append(LS);
    sb.append(" * <p>").append(LS);
    sb.append(" * Pass your syntax tree to this visitor, and then to the TreeDumper visitor<br>").append(LS);
    sb.append(" * in order to \"pretty print\" your tree.").append(LS);
    sb.append(" */").append(LS);
    sb.append("public class TreeFormatter extends DepthFirstVoidVisitor {").append(LS).append(LS);
    
    sb.append("  /** The list of formatting commands */").append(LS);
    sb.append("  private final ArrayList<FormatCommand> cmdQueue = new ArrayList<>();").append(LS);
    sb.append("  /** True if line to be wrapped, false otherwise */").append(LS);
    sb.append("  private boolean lineWrap;").append(LS);
    sb.append("  /** The wrap width */").append(LS);
    sb.append("  private final int wrapWidth;").append(LS);
    sb.append("  /** The indentation amount */").append(LS);
    sb.append("  private final int indentAmt;").append(LS);
    sb.append("  /** The current line number */").append(LS);
    sb.append("  private int curLine = 1;").append(LS);
    sb.append("  /** The current column number */").append(LS);
    sb.append("  private int curColumn = 1;").append(LS);
    sb.append("  /** The current indentation */").append(LS);
    sb.append("  private int curIndent = 0;").append(LS);
    sb.append("  /** The default indentation */").append(LS);
    sb.append("  private static int INDENT_AMT = 2;").append(LS).append(LS);
    
    sb.append("  /**").append(LS);
    sb.append("   * Constructor with a default indentation amount of {@link #INDENT_AMT} and no line-wrap.")
        .append(LS);
    sb.append("   */").append(LS);
    sb.append("  public TreeFormatter() { this(INDENT_AMT, 0); }").append(LS).append(LS);
    
    sb.append("  /**").append(LS);
    sb.append("   * Constructor using an indent amount and a line width used to wrap long lines.<br>")
        .append(LS);
    sb.append("   * If a token's beginColumn value is greater than the specified wrapWidth,<br>").append(LS);
    sb.append("   * it will be moved to the next line andindented one extra level.<br>").append(LS);
    sb.append("   * To turn off line-wrapping, specify a wrapWidth of 0.").append(LS);
    sb.append("   *").append(LS);
    sb.append("   * @param aIndentAmt - Amount of spaces per indentation level").append(LS);
    sb.append("   * @param aWrapWidth - Wrap lines longer than wrapWidth. 0 for no wrap").append(LS);
    sb.append("   */").append(LS);
    sb.append("  public TreeFormatter(final int aIndentAmt, final int aWrapWidth) {").append(LS);
    sb.append("    this.indentAmt = aIndentAmt;").append(LS);
    sb.append("    this.wrapWidth = aWrapWidth;").append(LS).append(LS);
    sb.append("    if (wrapWidth > 0)").append(LS);
    sb.append("       lineWrap = true;").append(LS);
    sb.append("    else").append(LS);
    sb.append("       lineWrap = false;").append(LS);
    sb.append("  }").append(LS).append(LS);
    
    sb.append("  /**").append(LS);
    sb.append("   * Accepts a ").append(iNodeList).append(" object.").append(LS);
    sb.append("   *").append(LS);
    sb.append("   * @param ").append(genNodeVar).append(" - the node list to process").append(LS);
    sb.append("   */").append(LS);
    sb.append("  protected void processList(final ").append(iNodeList).append(" ").append(genNodeVar)
        .append(") {").append(LS);
    sb.append("    processList(").append(genNodeVar).append(", null);").append(LS);
    sb.append("  }").append(LS).append(LS);
    
    sb.append("  /**").append(LS);
    sb.append("   * Accepts a ").append(iNodeList + " object and performs a format command (if non null)<br>")
        .append(LS);
    sb.append("   * between each node in the list (but not after the last node).").append(LS);
    sb.append("   *").append(LS);
    sb.append("   * @param ").append(genNodeVar).append(" - the node list to process").append(LS);
    sb.append("   * @param cmd - the format command").append(LS);
    sb.append("   */").append(LS);
    sb.append("  protected void processList(final ").append(iNodeList).append(" ").append(genNodeVar)
        .append(", final FormatCommand cmd) {").append(LS);
    sb.append("    for (final Iterator<").append(iNode).append("> e = ").append(genNodeVar)
        .append(".elements(); e.hasNext();) {").append(LS);
    sb.append("       e.next().accept(this);").append(LS);
    sb.append("       if (cmd != null && e.hasNext())").append(LS);
    sb.append("        cmdQueue.add(cmd);").append(LS);
    sb.append("    }").append(LS);
    sb.append("  }").append(LS).append(LS);
    
    sb.append("  /**").append(LS);
    sb.append("   * Inserts one line break and indents the next line to the current indentation level.<br>")
        .append(LS);
    sb.append("   * Use \"add(force());\".").append(LS);
    sb.append("   *").append(LS);
    sb.append("   * @return the corresponding FormatCommand").append(LS);
    sb.append("   */").append(LS);
    sb.append("  protected static FormatCommand force() { return force(1); }").append(LS).append(LS);
    
    sb.append("  /**").append(LS);
    sb.append(
        "   * Inserts a given number of line breaks and indents the next line to the current indentation level.<br>")
        .append(LS);
    sb.append("   * Use \"add(force(i));\".").append(LS);
    sb.append("   *").append(LS);
    sb.append("   * @param i - the number of line breaks").append(LS);
    sb.append("   * @return the corresponding FormatCommand").append(LS);
    sb.append("   */").append(LS);
    sb.append("  protected static FormatCommand force(final int i) {").append(LS);
    sb.append("    return new FormatCommand(FormatCommand.FORCE, i);").append(LS);
    sb.append("  }").append(LS).append(LS);
    
    sb.append("  /**").append(LS);
    sb.append("   * Increases the indentation level by one.<br>").append(LS);
    sb.append("   * Use \"add(indent());\".").append(LS);
    sb.append("   *").append(LS);
    sb.append("   * @return the corresponding FormatCommand").append(LS);
    sb.append("   */").append(LS);
    sb.append("  protected static FormatCommand indent() { return indent(1); }").append(LS).append(LS);
    
    sb.append("  /**").append(LS);
    sb.append("   * Increases the indentation level by a given number.<br>").append(LS);
    sb.append("   * Use \"add(indent(i));\".").append(LS);
    sb.append("   *").append(LS);
    sb.append("   * @param i - the number of indentation levels to add").append(LS);
    sb.append("   * @return the corresponding FormatCommand").append(LS);
    sb.append("   */").append(LS);
    sb.append("  protected static FormatCommand indent(final int i) {").append(LS);
    sb.append("    return new FormatCommand(FormatCommand.INDENT, i);").append(LS);
    sb.append("  }").append(LS).append(LS);
    
    sb.append("  /**").append(LS);
    sb.append("   * Reduces the indentation level by one.<br>").append(LS);
    sb.append("   * Use \"add(outdent());\".").append(LS);
    sb.append("   *").append(LS);
    sb.append("   * @return the corresponding FormatCommand").append(LS);
    sb.append("   */").append(LS);
    sb.append("  protected static FormatCommand outdent() { return outdent(1); }").append(LS).append(LS);
    
    sb.append("  /**").append(LS);
    sb.append("   * Reduces the indentation level by a given number.<br>").append(LS);
    sb.append("   * Use \"add(outdent(i));\".").append(LS);
    sb.append("   *").append(LS);
    sb.append("   * @param i - the number of indentation levels to substract").append(LS);
    sb.append("   * @return the corresponding FormatCommand").append(LS);
    sb.append("   */").append(LS);
    sb.append("  protected static FormatCommand outdent(final int i) {").append(LS);
    sb.append("    return new FormatCommand(FormatCommand.OUTDENT, i);").append(LS);
    sb.append("  }").append(LS).append(LS);
    
    sb.append("  /**").append(LS);
    sb.append("   * Adds one space between tokens.<br>").append(LS);
    sb.append("   * Use \"add(space());\".").append(LS);
    sb.append("   *").append(LS);
    sb.append("   * @return the corresponding FormatCommand").append(LS);
    sb.append("   */").append(LS);
    sb.append("  protected static FormatCommand space() { return space(1); }").append(LS).append(LS);
    
    sb.append("  /**").append(LS);
    sb.append("   * Adds a given number of spaces between tokens.<br>").append(LS);
    sb.append("   * Use \"add(space(i));\".").append(LS);
    sb.append("   *").append(LS);
    sb.append("   * @param i - the number of spaces to add").append(LS);
    sb.append("   * @return the corresponding FormatCommand").append(LS);
    sb.append("   */").append(LS);
    sb.append("  protected static FormatCommand space(final int i) {").append(LS);
    sb.append("    return new FormatCommand(FormatCommand.SPACE, i);").append(LS);
    sb.append("  }").append(LS).append(LS);
    
    sb.append("  /**").append(LS);
    sb.append("   * Use this method to add FormatCommands to the command queue to be executed<br>")
        .append(LS);
    sb.append("   * when the next token in the tree is visited.").append(LS);
    sb.append("   *").append(LS);
    sb.append("   * @param cmd - the FormatCommand to be added").append(LS);
    sb.append("   */").append(LS);
    sb.append("  protected void add(final FormatCommand cmd) {").append(LS);
    sb.append("    cmdQueue.add(cmd);").append(LS);
    sb.append("  }").append(LS).append(LS);
    
    sb.append("  /**").append(LS);
    sb.append("   * Executes the commands waiting in the command queue,<br>").append(LS);
    sb.append("   * then inserts the proper location information into the current Token.").append(LS);
    sb.append("   * <p>").append(LS);
    sb.append("   * If there are any special tokens preceding this token,<br>").append(LS);
    sb.append("   * they will be given the current location information.<br>").append(LS);
    sb.append("   * The token will follow on the next line, at the proper indentation level.<br>").append(LS);
    sb.append("   * If this is not the behavior you want from special tokens,<br>").append(LS);
    sb.append("   * feel free to modify this method.").append(LS);
    sb.append("   */").append(LS);
    sb.append("  @Override").append(LS);
    sb.append("  public void visit(final ").append(nodeToken).append(" ").append(genNodeVar).append(") {")
        .append(LS);
    sb.append("    for (FormatCommand cmd : cmdQueue) {").append(LS);
    sb.append("      switch (cmd.getCommand()) {").append(LS);
    sb.append("      case FormatCommand.FORCE :").append(LS);
    sb.append("        curLine += cmd.getNumCommands();").append(LS);
    sb.append("        curColumn = curIndent + 1;").append(LS);
    sb.append("        break;").append(LS);
    sb.append("      case FormatCommand.INDENT :").append(LS);
    sb.append("        curIndent += indentAmt * cmd.getNumCommands();").append(LS);
    sb.append("        break;").append(LS);
    sb.append("      case FormatCommand.OUTDENT :").append(LS);
    sb.append("        if (curIndent >= indentAmt)").append(LS);
    sb.append("        curIndent -= indentAmt * cmd.getNumCommands();").append(LS);
    sb.append("        break;").append(LS);
    sb.append("      case FormatCommand.SPACE :").append(LS);
    sb.append("        curColumn += cmd.getNumCommands();").append(LS);
    sb.append("        break;").append(LS);
    sb.append("      default :").append(LS);
    sb.append("        throw new TreeFormatterException(\"Invalid value in command queue.\");").append(LS);
    sb.append("      }").append(LS);
    sb.append("    }").append(LS).append(LS);
    sb.append("    cmdQueue.removeAll(cmdQueue);").append(LS).append(LS);
    sb.append("    //").append(LS);
    sb.append("    // Handle all special tokens preceding this Token").append(LS);
    sb.append("    //").append(LS);
    sb.append("    if (").append(genNodeVar).append(".numSpecials() > 0)").append(LS);
    sb.append("      for (").append(nodeToken).append(" e : ").append(genNodeVar).append(".specialTokens) {")
        .append(LS);
    sb.append("       Token special = e;").append(LS).append(LS);
    sb.append("       //").append(LS);
    sb.append("       // Place the token").append(LS);
    sb.append("       // Move cursor to next line after the special token").append(LS);
    sb.append("       // Don't update curColumn - want to keep current indent level").append(LS);
    sb.append("       //").append(LS);
    sb.append("       placeToken(special, curLine, curColumn);").append(LS);
    sb.append("       curLine = special.endLine + 1;").append(LS);
    sb.append("      }").append(LS).append(LS);
    sb.append("    placeToken(").append(genNodeVar).append(", curLine, curColumn);").append(LS);
    sb.append("    curLine = ").append(genNodeVar).append(".endLine;").append(LS);
    sb.append("    curColumn = ").append(genNodeVar).append(".endColumn;").append(LS);
    sb.append("  }").append(LS).append(LS);
    
    sb.append("  /**").append(LS);
    sb.append("   * Inserts token location (beginLine, beginColumn, endLine, endColumn)<br>").append(LS);
    sb.append("   * information into the Token.<br>").append(LS);
    sb.append("   * Takes into account line-wrap. Does not update curLine and curColumn.").append(LS);
    sb.append("   *").append(LS);
    sb.append("   * @param ").append(genNodeVar).append(" - the Token to insert").append(LS);
    sb.append("   * @param aLine - the insertion line number").append(LS);
    sb.append("   * @param aColumn - the insertion column number").append(LS);
    sb.append("   */").append(LS);
    sb.append("  private void placeToken(final ").append(nodeToken).append(" ").append(genNodeVar)
        .append(", final int aLine, final int aColumn) {").append(LS);
    sb.append("    final int length = ").append(genNodeVar).append(".image.length();").append(LS);
    sb.append("    int line = aLine;").append(LS);
    sb.append("    int column = aColumn;").append(LS).append(LS);
    sb.append("    //").append(LS);
    sb.append("    // Find beginning of token.  Only line-wrap for single-line tokens").append(LS);
    sb.append("    //").append(LS);
    sb.append("    if (!lineWrap || ").append(genNodeVar).append(".image.indexOf('\\n') != -1 ||").append(LS);
    sb.append("       column + length <= wrapWidth)").append(LS);
    sb.append("       ").append(genNodeVar).append(".beginColumn = column;").append(LS);
    sb.append("    else {").append(LS);
    sb.append("       ++line;").append(LS);
    sb.append("       column = curIndent + indentAmt + 1;").append(LS);
    sb.append("       ").append(genNodeVar).append(".beginColumn = column;").append(LS);
    sb.append("    }").append(LS).append(LS);
    sb.append("    ").append(genNodeVar).append(".beginLine = line;").append(LS).append(LS);
    sb.append("    //").append(LS);
    sb.append("    // Find end of token; don't count '\\n' if it's the last character").append(LS);
    sb.append("    //").append(LS);
    sb.append("    for (int i = 0; i < length; ++i) {").append(LS);
    sb.append("       if (").append(genNodeVar).append(".image.charAt(i) == '\\n' && i < length - 1) {")
        .append(LS);
    sb.append("        ++line;").append(LS);
    sb.append("        column = 1;").append(LS);
    sb.append("       }").append(LS);
    sb.append("       else").append(LS);
    sb.append("        ++column;").append(LS);
    sb.append("    }").append(LS).append(LS);
    sb.append("    ").append(genNodeVar).append(".endLine = line;").append(LS);
    sb.append("    ").append(genNodeVar).append(".endColumn = column;").append(LS);
    sb.append("  }").append(LS).append(LS);
    
    sb.append("  //").append(LS);
    sb.append("  // User-generated visitor methods below").append(LS);
    sb.append("  //").append(LS).append(LS);
    final Spacing spc = new Spacing(INDENT_AMT);
    spc.updateSpc(+1);
    
    for (final UserClassInfo uci : classList) {
      final String className = uci.fixedClassName;
      
      sb.append(spc.spc).append("/**").append(LS);
      // generate the javadoc for the class fields, with indentation of 1
      ccg.fmtAllFieldsJavadocCmts(sb, spc, uci);
      sb.append(spc.spc).append(" */").append(LS);
      if (uci.fields == null) {
        sb.append(spc.spc).append("@SuppressWarnings(\"unused\")").append(LS);
      }
      sb.append(spc.spc).append("@Override").append(LS);
      sb.append(spc.spc).append("public void visit");
      sb.append("(final ").append(className).append(' ').append(genNodeVar).append(") {").append(LS);
      
      spc.updateSpc(+1);
      
      if (uci.fields != null) {
        for (final FieldInfo fi : uci.fields) {
          final String name = fi.name;
          final String type = fi.fixedType;
          if (name != null) {
            if (type.equals(nodeList)) {
              sb.append(spc.spc).append("processList(").append(genNodeVar).append(".").append(name)
                  .append(");").append(LS);
            } else if (type.equals(nodeListOptional)) {
              sb.append(spc.spc).append("if (").append(genNodeVar).append(".").append(name)
                  .append(".present()) {").append(LS);
              spc.updateSpc(+1);
              sb.append(spc.spc).append("processList(").append(genNodeVar).append(".").append(name)
                  .append(");").append(LS);
              spc.updateSpc(-1);
              sb.append(spc.spc).append("}").append(LS);
            } else if (type.equals(nodeOptional)) {
              sb.append(spc.spc).append("if (").append(genNodeVar).append(".").append(name)
                  .append(".present()) {").append(LS);
              spc.updateSpc(+1);
              sb.append(spc.spc).append(genNodeVar).append(".").append(name).append(".accept(this);")
                  .append(LS);
              
              spc.updateSpc(-1);
              sb.append(spc.spc).append("}").append(LS);
            } else {
              sb.append(spc.spc).append(genNodeVar).append(".").append(name).append(".accept(this);")
                  .append(LS);
            }
          }
        }
      } else {
        sb.append(spc.spc).append("// empty node, nothing to generate").append(LS);
      }
      
      spc.updateSpc(-1);
      sb.append(spc.spc).append("}").append(LS).append(LS);
    }
    
    spc.updateSpc(-1);
    sb.append(spc.spc).append("}").append(LS).append(LS);
    
    //
    // Print class FormatCommand
    //
    
    sb.append("/**").append(LS);
    sb.append(" * Stores a format command.").append(LS);
    sb.append(" */").append(LS);
    sb.append("class FormatCommand {").append(LS).append(LS);
    
    sb.append("  /** Line break format code */").append(LS);
    sb.append("  public static final int FORCE = 0;").append(LS);
    sb.append("  /** Indentation format code */").append(LS);
    sb.append("  public static final int INDENT = 1;").append(LS);
    sb.append("  /** Unindentation format code */").append(LS);
    sb.append("  public static final int OUTDENT = 2;").append(LS);
    sb.append("  /** Spacing format code */").append(LS);
    sb.append("  public static final int SPACE = 3;").append(LS).append(LS);
    sb.append("  /** The format command code */").append(LS);
    sb.append("  private int command;").append(LS);
    sb.append("  /** The format command repetition number */").append(LS);
    sb.append("  private int numCommands;").append(LS).append(LS);
    
    sb.append("  /**").append(LS);
    sb.append("   * Constructor with class members.").append(LS);
    sb.append("   *").append(LS);
    sb.append("   * @param aCmd - the command code").append(LS);
    sb.append("   * @param aNumCmd - the command repetition number").append(LS);
    sb.append("   */").append(LS);
    sb.append("  FormatCommand(final int aCmd, final int aNumCmd) {").append(LS);
    sb.append("    this.command = aCmd;").append(LS);
    sb.append("    this.numCommands = aNumCmd;").append(LS);
    sb.append("  }").append(LS).append(LS);
    
    sb.append("  /**").append(LS);
    sb.append("   * @return the command code").append(LS);
    sb.append("   */").append(LS);
    sb.append("  public int getCommand()  { return command; }").append(LS).append(LS);
    
    sb.append("  /**").append(LS);
    sb.append("   * @return the command repetition number").append(LS);
    sb.append("   */").append(LS);
    sb.append("  public int getNumCommands()  { return numCommands; }").append(LS).append(LS);
    
    sb.append("  /**").append(LS);
    sb.append("   * Sets the command code.").append(LS);
    sb.append("   *").append(LS);
    sb.append("   * @param i - the command code").append(LS);
    sb.append("   */").append(LS);
    sb.append("  public void setCommand(final int i)  { command = i; }").append(LS).append(LS);
    
    sb.append("  /**").append(LS);
    sb.append("   * Sets the command repetition number.").append(LS);
    sb.append("   *").append(LS);
    sb.append("   * @param i - the command repetition number").append(LS);
    sb.append("   */").append(LS);
    sb.append("  public void setNumCommands(final int i)  { numCommands = i; }").append(LS).append(LS);
    sb.append("}").append(LS).append(LS);
    
    //
    // Print class TreeFormatterException
    //
    
    sb.append("/**").append(LS);
    sb.append(" * The TreeFormatter exception class.").append(LS);
    sb.append(" */").append(LS);
    sb.append("class TreeFormatterException extends RuntimeException {").append(LS).append(LS);
    
    sb.append("  /** The serial version UID */").append(LS);
    sb.append("  private static final long serialVersionUID = 1L;").append(LS).append(LS);
    
    sb.append("  /**").append(LS);
    sb.append("   * Constructor with no message.").append(LS);
    sb.append("   */").append(LS);
    sb.append("  TreeFormatterException()  { super(); }").append(LS).append(LS);
    
    sb.append("  /**").append(LS);
    sb.append("   * Constructor with a given message.").append(LS);
    sb.append("   *").append(LS);
    sb.append("   * @param s - the exception message").append(LS);
    sb.append("   */").append(LS);
    sb.append("  TreeFormatterException(final String s)  { super(s); }").append(LS).append(LS);
    sb.append("}").append(LS);
    
  }
}

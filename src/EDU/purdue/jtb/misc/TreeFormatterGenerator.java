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
 * Indiana by Kevin Tao and Jens Palsberg.  No charge may be made
 * for copies, derivations, or distributions of this material
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
import static EDU.purdue.jtb.misc.Globals.dFVoidVisitor;
import static EDU.purdue.jtb.misc.Globals.genFileHeaderComment;
import static EDU.purdue.jtb.misc.Globals.iNode;
import static EDU.purdue.jtb.misc.Globals.iNodeList;
import static EDU.purdue.jtb.misc.Globals.javaDocComments;
import static EDU.purdue.jtb.misc.Globals.nodeList;
import static EDU.purdue.jtb.misc.Globals.nodeListOpt;
import static EDU.purdue.jtb.misc.Globals.nodeOpt;
import static EDU.purdue.jtb.misc.Globals.nodeToken;
import static EDU.purdue.jtb.misc.Globals.nodesPackageName;
import static EDU.purdue.jtb.misc.Globals.visitorsDirName;
import static EDU.purdue.jtb.misc.Globals.visitorsPackageName;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Class TreeFormatterBuilder generates the TreeFormatter visitor which is a skeleton
 * pretty-printer.<br>
 * Using some pre-defined methods, users can quickly and easily create a formatter for their
 * grammar.<br>
 * The formatter will then take a tree, insert token location information into the NodeTokens of the
 * tree.<br>
 * TreeDumper can then be used to output the result.<br>
 * Note that unlike the other automatically generated file, since this one must be edited to be
 * useful, JTB will not overwrite this file automatically.<br>
 * JTB will take this precaution for the other files only if the "-ow" command-line parameter is
 * used.
 * 
 * @author Marc Mazas
 * @version 1.4.0 : 05/2009 : MMa : adapted to JavaCC v4.2 grammar and JDK 1.5
 * @version 1.4.3 : 03/2010 : MMa : fixed output of constructor
 */
public class TreeFormatterGenerator {

  /** The visitor class name */
  public static final String         visitorName = "TreeFormatter";
  /** The visitor source file name */
  public static final String         outFilename = visitorName + ".java";
  /** The visitors directory */
  private final File                 visitorDir;
  /** The classes list */
  private final List<ClassInfo> classList;
  /** The buffer to print into */
  protected StringBuilder            sb;

  /**
   * Constructor with a given list of classes. Will create the visitors directory if it does not
   * exist.
   * 
   * @param classes - the list of classes
   */
  public TreeFormatterGenerator(final List<ClassInfo> classes) {
    classList = classes;
    sb = new StringBuilder(500 * classes.size());
    visitorDir = new File(visitorsDirName);

    if (!visitorDir.exists())
      visitorDir.mkdir();
  }

  /**
   * Saves the current buffer in the output file (global variable).<br>
   * Since the user is expected to edit and customize this file, this method will never overwrite
   * the file if it exists, regardless of the global no overwrite flag.
   * 
   * @throws FileExistsException - if the file exists
   */
  public void saveToFile() throws FileExistsException {
    try {
      final File file = new File(visitorDir, outFilename);

      if (file.exists())
        throw new FileExistsException(outFilename);

      final PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(file), sb.length()));
      out.print(sb);
      out.close();
    }
    catch (final IOException e) {
      Messages.hardErr(e);
    }
  }

  // TODO change the following methods with spc.spc

  /**
   * Generates the tree formatter visitor source in its file.<br>
   * 
   * @throws FileExistsException - if the file exists
   */
  public void generateTreeFormatter() throws FileExistsException {

    sb.append(genFileHeaderComment()).append(LS);
    sb.append("package " + visitorsPackageName + ";").append(LS).append(LS);
    sb.append("import java.util.ArrayList;").append(LS);
    sb.append("import java.util.Iterator;").append(LS).append(LS);
    sb.append("import " + nodesPackageName + ".*;").append(LS).append(LS);
    sb.append("/**").append(LS);
    sb.append(" * A skeleton output formatter for your language grammar.<br>").append(LS);
    sb.append(" * Using the add() method along with force(), indent(), and outdent(),<br>")
      .append(LS);
    sb.append(" * you can easily specify how this visitor will format the given syntax tree.<br>")
      .append(LS);
    sb.append(" * See the JTB documentation for more details.").append(LS);
    sb.append(" * <p>").append(LS);
    sb.append(" * Pass your syntax tree to this visitor, and then to the TreeDumper visitor<br>")
      .append(LS);
    sb.append(" * in order to \"pretty print\" your tree.").append(LS);
    sb.append(" */").append(LS);
    sb.append("public class TreeFormatter extends " + dFVoidVisitor + " {").append(LS).append(LS);

    sb.append("  /** The list of formatting commands */").append(LS);
    sb.append("  private final ArrayList<FormatCommand> cmdQueue = new ArrayList<FormatCommand>();")
      .append(LS);
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
    sb.append("   * If a token's beginColumn value is greater than the specified wrapWidth,<br>")
      .append(LS);
    sb.append("   * it will be moved to the next line andindented one extra level.<br>").append(LS);
    sb.append("   * To turn off line-wrapping, specify a wrapWidth of 0.").append(LS);
    sb.append("   *").append(LS);
    sb.append("   * @param aIndentAmt - Amount of spaces per indentation level").append(LS);
    sb.append("   * @param aWrapWidth - Wrap lines longer than wrapWidth. 0 for no wrap")
      .append(LS);
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
    sb.append("   * Accepts a " + iNodeList + " object.").append(LS);
    sb.append("   *").append(LS);
    sb.append("   * @param n - the node list to process").append(LS);
    sb.append("   */").append(LS);
    sb.append("  protected void processList(final " + iNodeList + " n) {").append(LS);
    sb.append("    processList(n, null);").append(LS);
    sb.append("  }").append(LS).append(LS);

    sb.append("  /**").append(LS);
    sb.append("   * Accepts a " + iNodeList +
                  " object and performs a format command (if non null)<br>").append(LS);
    sb.append("   * between each node in the list (but not after the last node).").append(LS);
    sb.append("   *").append(LS);
    sb.append("   * @param n - the node list to process").append(LS);
    sb.append("   * @param cmd - the format command").append(LS);
    sb.append("   */").append(LS);
    sb.append("  protected void processList(final " + iNodeList + " n, final FormatCommand cmd) {")
      .append(LS);
    sb.append("    for (final Iterator<" + iNode + "> e = n.elements(); e.hasNext();) {")
      .append(LS);
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
    sb.append("  protected FormatCommand force() { return force(1); }").append(LS).append(LS);

    sb.append("  /**").append(LS);
    sb.append("   * Inserts a given number of line breaks and indents the next line to the current indentation level.<br>")
      .append(LS);
    sb.append("   * Use \"add(force(i));\".").append(LS);
    sb.append("   *").append(LS);
    sb.append("   * @param i - the number of line breaks").append(LS);
    sb.append("   * @return the corresponding FormatCommand").append(LS);
    sb.append("   */").append(LS);
    sb.append("  protected FormatCommand force(final int i) {").append(LS);
    sb.append("    return new FormatCommand(FormatCommand.FORCE, i);").append(LS);
    sb.append("  }").append(LS).append(LS);

    sb.append("  /**").append(LS);
    sb.append("   * Increases the indentation level by one.<br>").append(LS);
    sb.append("   * Use \"add(indent());\".").append(LS);
    sb.append("   *").append(LS);
    sb.append("   * @return the corresponding FormatCommand").append(LS);
    sb.append("   */").append(LS);
    sb.append("  protected FormatCommand indent() { return indent(1); }").append(LS).append(LS);

    sb.append("  /**").append(LS);
    sb.append("   * Increases the indentation level by a given number.<br>").append(LS);
    sb.append("   * Use \"add(indent(i));\".").append(LS);
    sb.append("   *").append(LS);
    sb.append("   * @param i - the number of indentation levels to add").append(LS);
    sb.append("   * @return the corresponding FormatCommand").append(LS);
    sb.append("   */").append(LS);
    sb.append("  protected FormatCommand indent(final int i) {").append(LS);
    sb.append("    return new FormatCommand(FormatCommand.INDENT, i);").append(LS);
    sb.append("  }").append(LS).append(LS);

    sb.append("  /**").append(LS);
    sb.append("   * Reduces the indentation level by one.<br>").append(LS);
    sb.append("   * Use \"add(outdent());\".").append(LS);
    sb.append("   *").append(LS);
    sb.append("   * @return the corresponding FormatCommand").append(LS);
    sb.append("   */").append(LS);
    sb.append("  protected FormatCommand outdent() { return outdent(1); }").append(LS).append(LS);

    sb.append("  /**").append(LS);
    sb.append("   * Reduces the indentation level by a given number.<br>").append(LS);
    sb.append("   * Use \"add(outdent(i));\".").append(LS);
    sb.append("   *").append(LS);
    sb.append("   * @param i - the number of indentation levels to substract").append(LS);
    sb.append("   * @return the corresponding FormatCommand").append(LS);
    sb.append("   */").append(LS);
    sb.append("  protected FormatCommand outdent(final int i) {").append(LS);
    sb.append("    return new FormatCommand(FormatCommand.OUTDENT, i);").append(LS);
    sb.append("  }").append(LS).append(LS);

    sb.append("  /**").append(LS);
    sb.append("   * Adds one space between tokens.<br>").append(LS);
    sb.append("   * Use \"add(space());\".").append(LS);
    sb.append("   *").append(LS);
    sb.append("   * @return the corresponding FormatCommand").append(LS);
    sb.append("   */").append(LS);
    sb.append("  protected FormatCommand space() { return space(1); }").append(LS).append(LS);

    sb.append("  /**").append(LS);
    sb.append("   * Adds a given number of spaces between tokens.<br>").append(LS);
    sb.append("   * Use \"add(space(i));\".").append(LS);
    sb.append("   *").append(LS);
    sb.append("   * @param i - the number of spaces to add").append(LS);
    sb.append("   * @return the corresponding FormatCommand").append(LS);
    sb.append("   */").append(LS);
    sb.append("  protected FormatCommand space(final int i) {").append(LS);
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
    sb.append("   * then inserts the proper location information into the current NodeToken.")
      .append(LS);
    sb.append("   * <p>").append(LS);
    sb.append("   * If there are any special tokens preceding this token,<br>").append(LS);
    sb.append("   * they will be given the current location information.<br>").append(LS);
    sb.append("   * The token will follow on the next line, at the proper indentation level.<br>")
      .append(LS);
    sb.append("   * If this is not the behavior you want from special tokens,<br>").append(LS);
    sb.append("   * feel free to modify this method.").append(LS);
    sb.append("   */").append(LS);
    sb.append("  @Override").append(LS);
    sb.append("  public void visit(final " + nodeToken + " n) {").append(LS);
    sb.append("    for (Iterator<FormatCommand> e = cmdQueue.iterator(); e.hasNext();) {")
      .append(LS);
    sb.append("      final FormatCommand cmd = e.next();").append(LS);
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
    sb.append("        throw new TreeFormatterException(\"Invalid value in command queue.\");")
      .append(LS);
    sb.append("      }").append(LS);
    sb.append("    }").append(LS).append(LS);
    sb.append("    cmdQueue.removeAll(cmdQueue);").append(LS).append(LS);
    sb.append("    //").append(LS);
    sb.append("    // Handle all special tokens preceding this NodeToken").append(LS);
    sb.append("    //").append(LS);
    sb.append("    if (n.numSpecials() > 0)").append(LS);
    sb.append("      for (final Iterator<" + nodeToken +
                  "> e = n.specialTokens.iterator(); e.hasNext();) {").append(LS);
    sb.append("       NodeToken special = e.next();").append(LS).append(LS);
    sb.append("       //").append(LS);
    sb.append("       // Place the token").append(LS);
    sb.append("       // Move cursor to next line after the special token").append(LS);
    sb.append("       // Don't update curColumn - want to keep current indent level").append(LS);
    sb.append("       //").append(LS);
    sb.append("       placeToken(special, curLine, curColumn);").append(LS);
    sb.append("       curLine = special.endLine + 1;").append(LS);
    sb.append("      }").append(LS).append(LS);
    sb.append("    placeToken(n, curLine, curColumn);").append(LS);
    sb.append("    curLine = n.endLine;").append(LS);
    sb.append("    curColumn = n.endColumn;").append(LS);
    sb.append("  }").append(LS).append(LS);

    sb.append("  /**").append(LS);
    sb.append("   * Inserts token location (beginLine, beginColumn, endLine, endColumn)<br>")
      .append(LS);
    sb.append("   * information into the NodeToken.<br>").append(LS);
    sb.append("   * Takes into account line-wrap. Does not update curLine and curColumn.")
      .append(LS);
    sb.append("   *").append(LS);
    sb.append("   * @param n - the NodeToken to insert").append(LS);
    sb.append("   * @param aLine - the insertion line number").append(LS);
    sb.append("   * @param aColumn - the insertion column number").append(LS);
    sb.append("   */").append(LS);
    sb.append("  private void placeToken(final " + nodeToken +
                  " n, final int aLine, final int aColumn) {").append(LS);
    sb.append("    final int length = n.tokenImage.length();").append(LS);
    sb.append("    int line = aLine;").append(LS);
    sb.append("    int column = aColumn;").append(LS).append(LS);
    sb.append("    //").append(LS);
    sb.append("    // Find beginning of token.  Only line-wrap for single-line tokens").append(LS);
    sb.append("    //").append(LS);
    sb.append("    if (!lineWrap || n.tokenImage.indexOf('\\n') != -1 ||").append(LS);
    sb.append("       column + length <= wrapWidth)").append(LS);
    sb.append("       n.beginColumn = column;").append(LS);
    sb.append("    else {").append(LS);
    sb.append("       ++line;").append(LS);
    sb.append("       column = curIndent + indentAmt + 1;").append(LS);
    sb.append("       n.beginColumn = column;").append(LS);
    sb.append("    }").append(LS).append(LS);
    sb.append("    n.beginLine = line;").append(LS).append(LS);
    sb.append("    //").append(LS);
    sb.append("    // Find end of token; don't count '\\n' if it's the last character").append(LS);
    sb.append("    //").append(LS);
    sb.append("    for (int i = 0; i < length; ++i) {").append(LS);
    sb.append("       if (n.tokenImage.charAt(i) == '\\n' && i < length - 1) {").append(LS);
    sb.append("        ++line;").append(LS);
    sb.append("        column = 1;").append(LS);
    sb.append("       }").append(LS);
    sb.append("       else").append(LS);
    sb.append("        ++column;").append(LS);
    sb.append("    }").append(LS).append(LS);
    sb.append("    n.endLine = line;").append(LS);
    sb.append("    n.endColumn = column;").append(LS);
    sb.append("  }").append(LS).append(LS);

    sb.append("  //").append(LS);
    sb.append("  // User-generated visitor methods below").append(LS);
    sb.append("  //").append(LS).append(LS);
    final Spacing spc = new Spacing(INDENT_AMT);
    spc.update(+1);

    for (final Iterator<ClassInfo> e = classList.iterator(); e.hasNext();) {
      final ClassInfo cur = e.next();
      final String className = cur.getClassName();

      if (javaDocComments) {
        sb.append(spc.spc).append("/**").append(LS);
        // generate the javadoc for the class fields, with indentation of 1
        cur.fmtFieldsJavadocCmts(sb, spc);
        sb.append(spc.spc).append(" */").append(LS);
        sb.append(spc.spc).append("@Override").append(LS);
      }
      sb.append(spc.spc).append("public void visit");
      sb.append("(final ").append(className).append(" n) {").append(LS);

      spc.update(+1);

      final Iterator<String> names = cur.getFieldNames().iterator();
      final Iterator<String> types = cur.getFieldTypes().iterator();

      while (names.hasNext() && types.hasNext()) {
        final String name = names.next();
        final String type = types.next();

        if (name != null)
          if (type.equals(nodeList))
            sb.append(spc.spc).append("processList(n.").append(name).append(");").append(LS);
          else if (type.equals(nodeListOpt)) {
            sb.append(spc.spc).append("if (n.").append(name).append(".").append("present()) {").append(LS);
            spc.update(+1);
            sb.append(spc.spc).append("processList(n.").append(name).append(");").append(LS);
            spc.update(-1);
            sb.append(spc.spc).append("}").append(LS);
          } else if (type.equals(nodeOpt)) {
            sb.append(spc.spc).append("if (n.").append(name).append(".").append("present()) {").append(LS);
            spc.update(+1);
            sb.append(spc.spc).append("n.").append(name).append(".").append("accept(this);").append(LS);

            spc.update(-1);
            sb.append(spc.spc).append("}").append(LS);
          } else
            sb.append(spc.spc).append("n.").append(name).append(".").append("accept(this);").append(LS);
      }

      spc.update(-1);
      sb.append(spc.spc).append("}").append(LS).append(LS);
    }

    spc.update(-1);
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
    sb.append("  public void setNumCommands(final int i)  { numCommands = i; }").append(LS)
      .append(LS);
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

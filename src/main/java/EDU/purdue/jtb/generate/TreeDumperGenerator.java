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
import static EDU.purdue.jtb.common.Constants.LS;
import static EDU.purdue.jtb.common.Constants.OK_RC;
import static EDU.purdue.jtb.common.Constants.fileHeaderComment;
import static EDU.purdue.jtb.common.Constants.genNodeVar;
import static EDU.purdue.jtb.common.Constants.nodeToken;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import EDU.purdue.jtb.common.Constants;
import EDU.purdue.jtb.common.JTBOptions;
import EDU.purdue.jtb.common.Messages;

/**
 * Class {@link TreeDumperGenerator} generates the TreeDumper visitor which simply prints all the tokens in
 * the tree at the locations given in their beginLine and beginColumn member variables. <br>
 * Similar to UserFilesGenerator class. CODEJAVA
 * <p>
 * This visitor is supposed to be run once and not supposed to be run in parallel threads (on the same
 * grammar).
 * </p>
 *
 * @author Marc Mazas
 * @version 1.4.0 : 05/2009 : MMa : adapted to JavaCC v4.2 grammar and JDK 1.5
 * @version 1.5.0 : 01/2017 : MMa : used try-with-resource
 */
public class TreeDumperGenerator {
  
  /** The global JTB options */
  private final JTBOptions    jopt;
  /** The messages handler */
  final Messages              mess;
  /** The visitor source file name */
  public static final String  outFilename = Constants.treeDumperName + ".java";
  /** The visitors directory */
  private final File          visitorDir;
  /** The buffer to print into */
  private final StringBuilder sb;
  
  /**
   * Constructor. Will create the visitors directory if it does not exist.
   *
   * @param aJopt - the JTB options
   * @param aMess - the messages handler
   */
  public TreeDumperGenerator(final JTBOptions aJopt, final Messages aMess) {
    jopt = aJopt;
    mess = aMess;
    sb = new StringBuilder(5 * 1024);
    visitorDir = jopt.visitorsDirName == null ? null : new File(jopt.visitorsDirName);
    if (visitorDir != null && !visitorDir.exists()) {
      visitorDir.mkdir();
    }
  }
  
  /**
   * Saves the current buffer in the output file (global variable).
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
   * Generates the tree dumper visitor source in its file.<br>
   */
  public void generateTreeDumper() {
    sb.append(fileHeaderComment).append(LS);
    sb.append("package ").append(jopt.visitorsPackageName).append(";").append(LS).append(LS);
    sb.append("import ");
    if (jopt.nodesPackageName != null)
      sb.append(jopt.nodesPackageName).append(".");
    sb.append("*;").append(LS);
    sb.append("import java.io.OutputStream;").append(LS);
    sb.append("import java.io.PrintWriter;").append(LS);
    sb.append("import java.io.Writer;").append(LS).append(LS);
    
    sb.append("/**").append(LS);
    sb.append(" * Dumps the syntax tree using the location information in each NodeToken.").append(LS);
    sb.append(" */").append(LS);
    sb.append("public class TreeDumper extends DepthFirstVoidVisitor {").append(LS).append(LS);
    
    sb.append("  /** The PrintWriter to write to */").append(LS);
    sb.append("  protected PrintWriter out;").append(LS);
    sb.append("  /** The current line */").append(LS);
    sb.append("  private int curLine = 1;").append(LS);
    sb.append("  /** The current column */").append(LS);
    sb.append("  private int curColumn = 1;").append(LS);
    sb.append("  /** True to start dumping at the next token visited, false otherwise */").append(LS);
    sb.append("  private boolean startAtNextToken = false;").append(LS);
    sb.append("  /** True to print specials (comments), false otherwise */").append(LS);
    sb.append("  private boolean printSpecials = true;").append(LS).append(LS);
    
    sb.append("  /**").append(LS);
    sb.append("   * Constructor using System.out as its output location.").append(LS);
    sb.append("   */").append(LS);
    sb.append("  public TreeDumper()  { out = new PrintWriter(System.out, true); }").append(LS).append(LS);
    
    sb.append("  /**").append(LS);
    sb.append("   * Constructor using the given Writer as its output location.").append(LS);
    sb.append("   *").append(LS);
    sb.append("   * @param o - the output Writer to write to").append(LS);
    sb.append("   */").append(LS);
    sb.append("  public TreeDumper(final Writer o)  { out = new PrintWriter(o, true); }").append(LS)
        .append(LS);
    
    sb.append("  /**").append(LS);
    sb.append("   * Constructor using the given OutputStream as its output location.").append(LS);
    sb.append("   *").append(LS);
    sb.append("   * @param o - the output OutputStream to write to").append(LS);
    sb.append("   */").append(LS);
    sb.append("  public TreeDumper(final OutputStream o)  { out = new PrintWriter(o, true); }").append(LS)
        .append(LS);
    
    sb.append("  /**").append(LS);
    sb.append("   * Flushes the OutputStream or Writer that this TreeDumper is using.").append(LS);
    sb.append("   */").append(LS);
    sb.append("  public void flushWriter()  { out.flush(); }").append(LS).append(LS);
    
    sb.append("  /**").append(LS);
    sb.append("   * Allows you to specify whether or not to print special tokens.").append(LS);
    sb.append("   *").append(LS);
    sb.append("   * @param b - true to print specials, false otherwise").append(LS).append(LS);
    sb.append("   */").append(LS);
    sb.append("  public void printSpecials(final boolean b)  { printSpecials = b; }").append(LS).append(LS);
    
    sb.append("  /**").append(LS);
    sb.append("   * Starts the tree dumper on the line containing the next token").append(LS);
    sb.append("   * visited.  For example, if the next token begins on line 50 and the").append(LS);
    sb.append("   * dumper is currently on line 1 of the file, it will set its current").append(LS);
    sb.append("   * line to 50 and continue printing from there, as opposed to").append(LS);
    sb.append("   * printing 49 blank lines and then printing the token.").append(LS);
    sb.append("   */").append(LS);
    sb.append("  public void startAtNextToken()  { startAtNextToken = true; }").append(LS).append(LS);
    
    sb.append("  /**").append(LS);
    sb.append("   * Resets the position of the output \"cursor\" to the first line and").append(LS);
    sb.append("   * column.  When using a dumper on a syntax tree more than once, you").append(LS);
    sb.append("   * either need to call this method or startAtNextToken() between each").append(LS);
    sb.append("   * dump.").append(LS);
    sb.append("   */").append(LS);
    sb.append("  public void resetPosition()  { curLine = curColumn = 1; }").append(LS).append(LS);
    
    sb.append("  /**").append(LS);
    sb.append("   * Dumps the current NodeToken to the output stream being used.").append(LS);
    sb.append("   *").append(LS);
    sb.append("   * @throws  IllegalStateException   if the token position is invalid").append(LS);
    sb.append("   *   relative to the current position, i.e. its location places it").append(LS);
    sb.append("   *   before the previous token.").append(LS);
    sb.append("   */").append(LS);
    sb.append("  @Override").append(LS);
    sb.append("  public void visit(final ").append(nodeToken).append(" ").append(genNodeVar).append(") {")
        .append(LS);
    sb.append("    if (").append(genNodeVar).append(".beginLine == -1 || ").append(genNodeVar)
        .append(".beginColumn == -1) {").append(LS);
    sb.append("      printToken(").append(genNodeVar).append(".tokenImage);").append(LS);
    sb.append("      return;").append(LS);
    sb.append("    }").append(LS).append(LS);
    sb.append("    //").append(LS);
    sb.append("    // Handle special tokens").append(LS);
    sb.append("    //").append(LS);
    sb.append("    if (printSpecials && ").append(genNodeVar).append(".numSpecials() > 0)").append(LS);
    sb.append("      for (").append(nodeToken).append(" e : ").append(genNodeVar).append(".specialTokens)")
        .append(LS);
    sb.append("        visit(e);").append(LS).append(LS);
    sb.append("    //").append(LS);
    sb.append("    // Handle startAtNextToken option").append(LS);
    sb.append("    //").append(LS);
    sb.append("    if (startAtNextToken) {").append(LS);
    sb.append("      curLine = ").append(genNodeVar).append(".beginLine;").append(LS);
    sb.append("      curColumn = 1;").append(LS);
    sb.append("      startAtNextToken = false;").append(LS).append(LS);
    sb.append("      if (").append(genNodeVar).append(".beginColumn < curColumn)").append(LS);
    sb.append("        out.println();").append(LS);
    sb.append("    }").append(LS).append(LS);
    sb.append("    //").append(LS);
    sb.append("    // Check for invalid token position relative to current position.").append(LS);
    sb.append("    //").append(LS);
    sb.append("    if (").append(genNodeVar).append(".beginLine < curLine)").append(LS);
    sb.append("      throw new IllegalStateException(\"at token \\\"\" + ").append(genNodeVar)
        .append(".tokenImage +").append(LS);
    sb.append("        \"\\\", ").append(genNodeVar).append(".beginLine = \" + Integer.toString(")
        .append(genNodeVar).append(".beginLine) +").append(LS);
    sb.append("        \", curLine = \" + Integer.toString(curLine));").append(LS);
    sb.append("    else if (").append(genNodeVar).append(".beginLine == curLine && ").append(genNodeVar)
        .append(".beginColumn < curColumn)").append(LS);
    sb.append("      throw new IllegalStateException(\"at token \\\"\" + ").append(genNodeVar)
        .append(".tokenImage +").append(LS);
    sb.append("        \"\\\", ").append(genNodeVar).append(".beginColumn = \" +").append(LS);
    sb.append("        Integer.toString(").append(genNodeVar).append(".beginColumn) + \", curColumn = \" +")
        .append(LS);
    sb.append("        Integer.toString(curColumn));").append(LS).append(LS);
    sb.append("    //").append(LS);
    sb.append("    // Move output \"cursor\" to proper location, then print the token").append(LS);
    sb.append("    //").append(LS);
    sb.append("    if (curLine < ").append(genNodeVar).append(".beginLine) {").append(LS);
    sb.append("      curColumn = 1;").append(LS);
    sb.append("      for (; curLine < ").append(genNodeVar).append(".beginLine; ++curLine)").append(LS);
    sb.append("        out.println();").append(LS);
    sb.append("    }").append(LS).append(LS);
    sb.append("    for (; curColumn < ").append(genNodeVar).append(".beginColumn; ++curColumn)").append(LS);
    sb.append("      out.print(\" \");").append(LS).append(LS);
    sb.append("    printToken(").append(genNodeVar).append(".tokenImage);").append(LS);
    sb.append("  }").append(LS).append(LS);
    
    sb.append("  /**").append(LS);
    sb.append("   * Prints a given String, updating line and column numbers.").append(LS);
    sb.append("   *").append(LS);
    sb.append("   * @param s - the String to print").append(LS);
    sb.append("   */").append(LS);
    sb.append("  private void printToken(final String s) {").append(LS);
    sb.append("    for (int i = 0; i < s.length(); ++i) { ").append(LS);
    sb.append("      if (s.charAt(i) == '\\").append(genNodeVar).append("') {").append(LS);
    sb.append("        ++curLine;").append(LS);
    sb.append("        curColumn = 1;").append(LS);
    sb.append("      }").append(LS);
    sb.append("      else").append(LS);
    sb.append("        curColumn++;").append(LS).append(LS);
    sb.append("      out.print(s.charAt(i));").append(LS);
    sb.append("    }").append(LS).append(LS);
    sb.append("    out.flush();").append(LS);
    sb.append("  }").append(LS).append(LS);
    sb.append("}").append(LS);
  }
}

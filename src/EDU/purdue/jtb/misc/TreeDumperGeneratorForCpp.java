package EDU.purdue.jtb.misc;

import static EDU.purdue.jtb.misc.Globals.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Class TreeDumperBuilder generates the TreeDumper visitor which simply prints all the tokens in
 * the tree at the locations given in their beginLine and beginColumn member variables.<br>
 * Similar to {@link FilesGeneratorForJava} class.
 * 
 * @author Marc Mazas
 * @version 1.4.0 : 05/2009 : MMa : adapted to JavaCC v4.2 grammar and JDK 1.5
 */
@SuppressWarnings("javadoc")
public class TreeDumperGeneratorForCpp implements TreeDumperGenerator {

  /** The visitor class name */
  public static final String visitorName = "TreeDumper";
  /** The visitor source file name */
  public static final String outFilename = visitorName + ".h";
  /** The visitors directory */
  private final File         visitorDir;
  /** The buffer to print into */
  protected StringBuilder    sb;

  /**
   * Constructor. Will create the visitors directory if it does not exist.
   */
  public TreeDumperGeneratorForCpp() {
    visitorDir = new File(visitorsDirName);
    sb = new StringBuilder(5 * 1024);

    if (!visitorDir.exists())
      visitorDir.mkdir();
  }

  /**
   * Saves the current buffer in the output file (global variable).
   * 
   * @throws FileExistsException - if the file exists and the noOverwrite flag is set
   */
  @Override
  public void saveToFile() throws FileExistsException {
    try {
      final File file = new File(visitorDir, outFilename);

      if (noOverwrite && file.exists())
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
   * Generates the tree dumper visitor source in its file.<br>
   * 
   * @throws FileExistsException - if the file exists and the no overwrite flag has been set
   */
  @Override
  public void generateTreeDumper() throws FileExistsException {
    sb.append(genFileHeaderComment() + LS);

    sb.append("#ifndef TREEDUMPER_H_").append(LS);
    sb.append("#define TREEDUMPER_H_").append(LS);
    sb.append("#include <string>").append(LS);
    sb.append("#include <iostream>").append(LS);
    sb.append("#include <sstream>").append(LS);
    sb.append("#include <exception>").append(LS);
    sb.append("#include \"DepthFirstRetArguVisitor.h\"").append(LS);
    sb.append(LS);

    sb.append("using namespace std;").append(LS);
    sb.append(LS);

    sb.append("namespace ").append(Globals.astNamespace).append(" {").append(LS);
    sb.append(LS);

    sb.append("class IllegalStateException : public exception  {").append(LS);
    sb.append("  private:").append(LS);
    sb.append("    const string message;").append(LS);
    sb.append("  public:").append(LS);
    sb.append("    IllegalStateException(const string& message) : message(message) { }").append(LS);
    sb.append("    virtual ~IllegalStateException() { }").append(LS);
    sb.append("  };").append(LS);
    sb.append(LS);

    sb.append("/**").append(LS);
    sb.append(" * Dumps the syntax tree using the location information in each NodeToken.")
      .append(LS);
    sb.append(" */").append(LS);

    sb.append(retArguVisitor.getInterface().getClassPrefix()).append(LS);
    sb.append("class TreeDumper: public ");
    sb.append(retArguVisitor).append(retArguVisitor.getInterface().getClassParamType())
      .append(" {").append(LS);
    sb.append("private:").append(LS);

    sb.append("  /** The outputstream to write to */").append(LS);
    sb.append("  ostream& out;").append(LS);
    sb.append("  /** The current line */").append(LS);
    sb.append("  int curLine = 1;").append(LS);
    sb.append("  /** The current column */").append(LS);
    sb.append("  int curColumn = 1;").append(LS);
    sb.append("  /** True to start dumping at the next token visited, false otherwise */")
      .append(LS);
    sb.append("  bool doStartAtNextToken = false;").append(LS);
    sb.append("  /** True to print specials (comments), false otherwise */").append(LS);
    sb.append("  bool doPrintSpecials = true;").append(LS).append(LS);

    if (javaDocComments) {
      sb.append("  /**").append(LS);
      sb.append("   * Constructor using System.out as its output location.").append(LS);
      sb.append("   */").append(LS);
    }
    sb.append("public:").append(LS);
    sb.append("  TreeDumper(streambuf*sb) : out(sb) { }").append(LS);

    if (javaDocComments) {
      sb.append("  /**").append(LS);
      sb.append("   * Constructor using the given output stream as its output location.")
        .append(LS);
      sb.append("   *").append(LS);
      sb.append("   * @param o - the output stream to write to").append(LS);
      sb.append("   */").append(LS);
    }
    sb.append("  TreeDumper(ostream& os)  : out(os) { }").append(LS);

    if (javaDocComments) {
      sb.append("  /**").append(LS);
      sb.append("   * Flushes the OutputStream or Writer that this TreeDumper is using.")
        .append(LS);
      sb.append("   */").append(LS);
    }
    sb.append("  void flushWriter()  { out.flush(); }").append(LS);

    if (javaDocComments) {
      sb.append("  /**").append(LS);
      sb.append("   * Allows you to specify whether or not to print special tokens.").append(LS);
      sb.append("   *").append(LS);
      sb.append("   * @param b - true to print specials, false otherwise").append(LS).append(LS);
      sb.append("   */").append(LS);
    }
    sb.append("  void printSpecials(bool b)  { doPrintSpecials = b; }").append(LS);

    if (javaDocComments) {
      sb.append("  /**").append(LS);
      sb.append("   * Starts the tree dumper on the line containing the next token").append(LS);
      sb.append("   * visited.  For example, if the next token begins on line 50 and the")
        .append(LS);
      sb.append("   * dumper is currently on line 1 of the file, it will set its current")
        .append(LS);
      sb.append("   * line to 50 and continue printing from there, as opposed to").append(LS);
      sb.append("   * printing 49 blank lines and then printing the token.").append(LS);
      sb.append("   */").append(LS);
    }
    sb.append("  void startAtNextToken()  { doStartAtNextToken = true; }").append(LS);

    if (javaDocComments) {
      sb.append("  /**").append(LS);
      sb.append("   * Resets the position of the output \"cursor\" to the first line and")
        .append(LS);
      sb.append("   * column.  When using a dumper on a syntax tree more than once, you")
        .append(LS);
      sb.append("   * either need to call this method or startAtNextToken() between each")
        .append(LS);
      sb.append("   * dump.").append(LS);
      sb.append("   */").append(LS);
    }
    sb.append("  void resetPosition()  { curLine = curColumn = 1; }").append(LS);
    sb.append(LS);
    if (javaDocComments) {
      sb.append("  /**").append(LS);
      sb.append("   * Dumps the current NodeToken to the output stream being used.").append(LS);
      sb.append("   *").append(LS);
      sb.append("   * @throws  IllegalStateException   if the token position is invalid")
        .append(LS);
      sb.append("   *   relative to the current position, i.e. its location places it").append(LS);
      sb.append("   *   before the previous token.").append(LS);
      sb.append("   */").append(LS);
    }
    sb.append("  R visit(").append(nodeToken)
      .append(retArguVisitor.getInterface().getClassParamType()).append("& n, A... a) {")
      .append(LS);
    sb.append("    R r;").append(LS);
    sb.append("    if (n.beginLine == -1 || n.beginColumn == -1) {").append(LS);
    sb.append("      printToken(n.tokenImage);").append(LS);
    sb.append("      return r;").append(LS);
    sb.append("    }").append(LS).append(LS);
    if (javaDocComments) {
      sb.append("    //").append(LS);
      sb.append("    // Handle special tokens").append(LS);
      sb.append("    //").append(LS);
    }
    sb.append("    if (doPrintSpecials && n.numSpecials() > 0)").append(LS);
    sb.append("      for (auto e : n.specialTokens)").append(LS);
    sb.append("        visit(*e, a...);").append(LS).append(LS);
    if (javaDocComments) {
      sb.append("    //").append(LS);
      sb.append("    // Handle startAtNextToken option").append(LS);
      sb.append("    //").append(LS);
    }
    sb.append("    if (doStartAtNextToken) {").append(LS);
    sb.append("      curLine = n.beginLine;").append(LS);
    sb.append("      curColumn = 1;").append(LS);
    sb.append("      doStartAtNextToken = false;").append(LS).append(LS);
    sb.append("      if (n.beginColumn < curColumn)").append(LS);
    sb.append("        out << endl;").append(LS);
    sb.append("    }").append(LS).append(LS);
    if (javaDocComments) {
      sb.append("    //").append(LS);
      sb.append("    // Check for invalid token position relative to current position.").append(LS);
      sb.append("    //").append(LS);
    }
    sb.append("    if (n.beginLine < curLine) {").append(LS);
    sb.append("      ostringstream oss(\"at token \");").append(LS);
    sb.append("      oss << n.tokenImage << \", n.beginLine = \";").append(LS);
    sb.append("      oss << n.beginLine <<  \", curLine = \" << curLine;").append(LS);
    sb.append("      throw new IllegalStateException(oss.str());").append(LS);
    sb.append("    } else if (n.beginLine == curLine && n.beginColumn < curColumn) {").append(LS);
    sb.append("      ostringstream oss(\"at token \");").append(LS);
    sb.append("      oss << n.tokenImage << \", n.beginColumn = \";").append(LS);
    sb.append("      oss << n.beginLine <<  \", curColumn = \" << curColumn;").append(LS);
    sb.append("      throw new IllegalStateException(oss.str());").append(LS).append(LS);
    sb.append("    }").append(LS);
    if (javaDocComments) {
      sb.append("    //").append(LS);
      sb.append("    // Move output \"cursor\" to proper location, then print the token")
        .append(LS);
      sb.append("    //").append(LS);
    }
    sb.append("    if (curLine < n.beginLine) {").append(LS);
    sb.append("      curColumn = 1;").append(LS);
    sb.append("      for (; curLine < n.beginLine; ++curLine)").append(LS);
    sb.append("        out << endl;").append(LS);
    sb.append("    }").append(LS).append(LS);
    sb.append("    for (; curColumn < n.beginColumn; ++curColumn)").append(LS);
    sb.append("      out << ' ';").append(LS).append(LS);
    sb.append("    printToken(n.tokenImage);").append(LS);
    sb.append("    return r;").append(LS);
    sb.append("  }").append(LS).append(LS);

    sb.append("private:").append(LS);
    if (javaDocComments) {
      sb.append("  /**").append(LS);
      sb.append("   * Prints a given String, updating line and column numbers.").append(LS);
      sb.append("   *").append(LS);
      sb.append("   * @param s - the String to print").append(LS);
      sb.append("   */").append(LS);
    }

    sb.append("   void printToken(const String& s) {").append(LS);
    sb.append("    for (int i = 0; i < s.length(); ++i) { ").append(LS);
    sb.append("      if (s.at(i) == '\\n') {").append(LS);
    sb.append("        ++curLine;").append(LS);
    sb.append("        curColumn = 1;").append(LS);
    sb.append("      }").append(LS);
    sb.append("      else").append(LS);
    sb.append("        curColumn++;").append(LS).append(LS);
    sb.append("      out << s.at(i);").append(LS);
    sb.append("    }").append(LS).append(LS);
    sb.append("    out.flush();").append(LS);
    sb.append("  }").append(LS).append(LS);
    sb.append("};").append(LS);
    sb.append("}").append(LS);
    sb.append("#endif").append(LS);
  }

  @Override
  public String outFilename() {
    return "TreeDumper.h";
  }
}

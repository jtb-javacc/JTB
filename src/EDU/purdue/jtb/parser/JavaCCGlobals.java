/* Copyright (c) 2006, Sun Microsystems, Inc.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *     * Redistributions of source code must retain the above copyright notice,
 *       this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the Sun Microsystems, Inc. nor the names of its
 *       contributors may be used to endorse or promote products derived from
 *       this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
 * THE POSSIBILITY OF SUCH DAMAGE.
 */
package EDU.purdue.jtb.parser;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * This package contains data created as a result of parsing and semanticizing a JavaCC input file.
 * This data is what is used by the back-ends of JavaCC as well as any other back-end of JavaCC
 * related tools such as JJTree.
 * 
 * @author Marc Mazas
 * @version 1.4.0 : 05/2009 : MMa : adapted to JavaCC v4.2 grammar
 * @version 1.4.8 : 12/2014 : MMa : improved javadoc
 */
public class JavaCCGlobals {

  /**
   * String that identifies the JavaCC generated files.
   */
  protected static final String toolName = "JavaCC";

  /**
   * The name of the grammar file being processed.
   */
  static public String          fileName;

  /**
   * The name of the original file (before processing by JJTree). Currently this is the same as
   * fileName.
   */
  static public String          origFileName;

  /**
   * Set to true if this file has been processed by JJTree.
   */
  static public boolean         jjtreeGenerated;

  /**
   * The list of tools that have participated in generating the input grammar file.
   */
  static public List<String>    toolNames;

  /**
   * This prints the banner line when the various tools are invoked.
   * 
   * @param fullName - the tool full name
   * @param ver - the tool version
   */
  static public void bannerLine(final String fullName, final String ver) {
    System.out.print("Java Compiler Compiler Version " + Version.version + " (" + fullName);
    if (!ver.equals("")) {
      System.out.print(" Version " + ver);
    }
    System.out.println(")");
  }

  /**
   * The name of the parser class (what appears in PARSER_BEGIN and PARSER_END).
   */
  static public String        cu_name;

  /**
   * This is a list of tokens that appear after "PARSER_BEGIN(name)" all the way until (but not
   * including) the opening brace "{" of the class "name".
   */
  static public List<Token>   cu_to_insertion_point_1   = new ArrayList<Token>();

  /**
   * This is the list of all tokens that appear after the tokens in "cu_to_insertion_point_1" and
   * until (but not including) the closing brace "}" of the class "name".
   */
  static public List<Token>   cu_to_insertion_point_2   = new ArrayList<Token>();

  /**
   * This is the list of all tokens that appear after the tokens in "cu_to_insertion_point_2" and
   * until "PARSER_END(name)".
   */
  static public List<Token>  cu_from_insertion_point_2 = new ArrayList<Token>();

  /**
   * 
   */
  static public Token cppDclTokenBeg;
  /**
   * 
   */
  static public Token cppDclTokenEnd;
  /**
   * A list of all grammar productions - normal and JAVACODE - in the order they appear in the input
   * file. Each entry here will be a subclass of "NormalProduction".
   */
  static public List<NormalProduction>   bnfproductions            = new ArrayList<NormalProduction>();

  /**
   * A symbol table of all grammar productions - normal and JAVACODE. The symbol table is indexed by
   * the name of the left hand side non-terminal. Its contents are of type "NormalProduction".
   */
  static public Map<String, NormalProduction>    production_table          = new HashMap<String, NormalProduction>();

  /**
   * A mapping of lexical state strings to their integer internal representation. Integers are
   * stored as java.lang.Integer's.
   */
  static public Hashtable<String, Integer>   lexstate_S2I              = new Hashtable<String, Integer>();

  /**
   * A mapping of the internal integer representations of lexical states to their strings. Integers
   * are stored as java.lang.Integer's.
   */
  static public Hashtable<Integer, String>   lexstate_I2S              = new Hashtable<Integer, String>();

  /**
   * The declarations to be inserted into the TokenManager class.
   */
  static public List<Token>                  token_mgr_decls;

  /**
   * The list of all TokenProductions from the input file. This list includes implicit
   * TokenProductions that are created for uses of regular expressions within BNF productions.
   */
  static public List<TokenProduction>        rexprlist                 = new ArrayList<TokenProduction>();

  /**
   * The total number of distinct tokens. This is therefore one more than the largest assigned token
   * ordinal.
   */
  static public int                          tokenCount;

  /**
   * This is a symbol table that contains all named tokens (those that are defined with a label).
   * The index to the table is the image of the label and the contents of the table are of type
   * "RegularExpression_".
   */
  static public Map<String, RegularExpression_>                                       named_tokens_table        = new HashMap<String, RegularExpression_>();

  /**
   * Contains the same entries as "named_tokens_table", but this is an ordered list which is ordered
   * by the order of appearance in the input file.
   */
  static public List<RegularExpression_>                                              ordered_named_tokens      = new ArrayList<RegularExpression_>();

  /**
   * A mapping of ordinal values (represented as objects of type "Integer") to the corresponding
   * labels (of type "String"). An entry exists for an ordinal value only if there is a labeled
   * token corresponding to this entry. If there are multiple labels representing the same ordinal
   * value, then only one label is stored.
   */
  static public Map<Integer, String>                                                  names_of_tokens           = new HashMap<Integer, String>();

  /**
   * A mapping of ordinal values (represented as objects of type "Integer") to the corresponding
   * RegularExpression_'s.
   */
  static public Map<Integer, RegularExpression_>                                      rexps_of_tokens           = new HashMap<Integer, RegularExpression_>();

  /**
   * This is a three-level symbol table that contains all simple tokens (those that are defined
   * using a single string (with or without a label). The index to the first level table is a
   * lexical state which maps to a second level hashtable. The index to the second level hashtable
   * is the string of the simple token converted to upper case, and this maps to a third level
   * hashtable. This third level hashtable contains the actual string of the simple token and maps
   * it to its RegularExpression_.
   */
  static public Map<String, Hashtable<String, Hashtable<String, RegularExpression_>>> simple_tokens_table       = new Hashtable<String, Hashtable<String, Hashtable<String, RegularExpression_>>>();

  /** Mask index shared between ParseEngine and ParseGen */
  static protected int                                                                maskindex                 = 0;
  /** jj2 methods calls index shared between ParseEngine and ParseGen */
  static protected int                                                                jj2index                  = 0;
  /** True if lookahead needed, false otherwise */
  public static boolean                                                               lookaheadNeeded;
  /** Mask values shared between ParseEngine and ParseGen */
  static protected List<int[]>                                                        maskVals                  = new ArrayList<int[]>();

  /** The {@link Action} on end of file */
  static Action                                                                       actForEof;
  /** The next state for end of file */
  static String                                                                       nextStateForEof;

  // Some general purpose utilities follow.

  /**
   * @param tn - a toolname
   * @param fn - a file name
   * @return the identifying string for the file name, given a toolname used to generate it
   */
  public static String getIdString(final String tn, final String fn) {
    final List<String> tns = new ArrayList<String>();
    tns.add(tn);
    return getIdString(tns, fn);
  }

  /**
   * @param tns - a list of toolnames
   * @param fn - a file name
   * @return the identifying string for the file name, given a set of tool names that are used to
   *         generate it
   */
  public static String getIdString(final List<String> tns, final String fn) {
    int i;
    String toolNamePrefix = "Generated By:";

    for (i = 0; i < tns.size() - 1; i++)
      toolNamePrefix += tns.get(i) + "&";
    toolNamePrefix += tns.get(i) + ":";

    if (toolNamePrefix.length() > 200) {
      System.out.println("Tool names too long.");
      throw new Error();
    }

    return toolNamePrefix + " Do not edit this line. " + addUnicodeEscapes(fn);
  }

  /**
   * @param tn - a toolname
   * @param fn - a file name
   * @return true if tool name passed is one of the tool names returned by
   *         {@link #getToolNames(String)}getToolNames
   */
  public static boolean isGeneratedBy(final String tn, final String fn) {
    final List<String> v = getToolNames(fn);

    for (int i = 0; i < v.size(); i++)
      if (tn.equals(v.get(i)))
        return true;

    return false;
  }

  /**
   * @param str - a string containing toolnames
   * @return - a list of toolnames from the string splitted around ':' and '&'
   */
  private static List<String> makeToolNameList(final String str) {
    final List<String> retVal = new ArrayList<String>();

    int limit1 = str.indexOf('\n');
    if (limit1 == -1)
      limit1 = 1000;
    int limit2 = str.indexOf('\r');
    if (limit2 == -1)
      limit2 = 1000;
    final int limit = (limit1 < limit2) ? limit1 : limit2;

    String tmp;
    if (limit == 1000) {
      tmp = str;
    } else {
      tmp = str.substring(0, limit);
    }

    if (tmp.indexOf(':') == -1)
      return retVal;

    tmp = tmp.substring(tmp.indexOf(':') + 1);

    if (tmp.indexOf(':') == -1)
      return retVal;

    tmp = tmp.substring(0, tmp.indexOf(':'));

    int i = 0, j = 0;

    while (j < tmp.length() && (i = tmp.indexOf('&', j)) != -1) {
      retVal.add(tmp.substring(j, i));
      j = i + 1;
    }

    if (j < tmp.length())
      retVal.add(tmp.substring(j));

    return retVal;
  }

  /**
   * @param fn - a file name
   * @return the list of names of the tools that have been used to generate the given file
   */
  public static List<String> getToolNames(final String fn) {
    final char[] buf = new char[256];
    java.io.FileReader stream = null;
    int read, total = 0;

    try {
      stream = new java.io.FileReader(fn);

      for (;;)
        if ((read = stream.read(buf, total, buf.length - total)) != -1) {
          if ((total += read) == buf.length)
            break;
        } else
          break;

      return makeToolNameList(new String(buf, 0, total));
    }
    catch (final java.io.FileNotFoundException e1) {
    }
    catch (final java.io.IOException e2) {
      if (total > 0)
        return makeToolNameList(new String(buf, 0, total));
    }
    finally {
      if (stream != null)
        try {
          stream.close();
        }
        catch (final Exception e3) {
        }
    }

    return new ArrayList<String>();
  }

  /**
   * Creates an output directory.
   * 
   * @param outputDir - the output directory to be created
   */
  public static void createOutputDir(final File outputDir) {
    if (!outputDir.exists()) {
      JavaCCErrors.warning("Output directory \"" + outputDir +
                           "\" does not exist. Creating the directory.");

      if (!outputDir.mkdirs()) {
        JavaCCErrors.semantic_error("Cannot create the output directory : " + outputDir);
        return;
      }
    }

    if (!outputDir.isDirectory()) {
      JavaCCErrors.semantic_error("\"" + outputDir + " is not a valid output directory.");
      return;
    }

    if (!outputDir.canWrite()) {
      JavaCCErrors.semantic_error("Cannot write to the output output directory : \"" + outputDir +
                                  "\"");
      return;
    }
  }

  /**
   * @return the "static " string if the static option is set, otherwise the empty string
   */
  static public String staticOpt() {
    if (Options.getStatic()) {
      return "static ";
    } else {
      return "";
    }
  }

  /**
   * @param str - a string
   * @return the escaped string for common characters
   */
  static public String add_escapes(final String str) {
    String retval = "";
    char ch;
    for (int i = 0; i < str.length(); i++) {
      ch = str.charAt(i);
      if (ch == '\b') {
        retval += "\\b";
      } else if (ch == '\t') {
        retval += "\\t";
      } else if (ch == '\n') {
        retval += "\\n";
      } else if (ch == '\f') {
        retval += "\\f";
      } else if (ch == '\r') {
        retval += "\\r";
      } else if (ch == '\"') {
        retval += "\\\"";
      } else if (ch == '\'') {
        retval += "\\\'";
      } else if (ch == '\\') {
        retval += "\\\\";
      } else if (ch < 0x20 || ch > 0x7e) {
        final String s = "0000" + Integer.toString(ch, 16);
        retval += "\\u" + s.substring(s.length() - 4, s.length());
      } else {
        retval += ch;
      }
    }
    return retval;
  }

  /**
   * @param str - a string
   * @return the escaped string for unicode characters
   */
  static public String addUnicodeEscapes(final String str) {
    final int strlen = str.length();
    final StringBuilder retval = new StringBuilder(2 * strlen);
    char ch;
    for (int i = 0; i < strlen; i++) {
      ch = str.charAt(i);
      if (ch < 0x20 || ch > 0x7e || ch == '\\') {
        final String s = "0000" + Integer.toString(ch, 16);
        retval.append("\\u").append(s.substring(s.length() - 4, s.length()));
      } else {
        retval.append(ch);
      }
    }
    return retval.toString();
  }

  /** The character's line */
  static protected int cline;
  /** The character's column */
  static protected int ccol;

  /**
   * Sets up line and column information for a given token.
   * 
   * @param t - a token
   */
  static protected void printTokenSetup(final Token t) {
    Token tt = t;
    while (tt.specialToken != null)
      tt = tt.specialToken;
    cline = tt.beginLine;
    ccol = tt.beginColumn;
  }

  /**
   * Prints a token on a {@link PrintWriter} without the specials.
   * 
   * @param t - a token
   * @param out - a {@link PrintWriter}
   */
  static protected void printTokenOnly(final Token t, final PrintWriter out) {
    for (; cline < t.beginLine; cline++) {
      out.println();
      ccol = 1;
    }
    for (; ccol < t.beginColumn; ccol++) {
      out.print(" ");
    }
    if (t.kind == JavaCCParserConstants.STRING_LITERAL ||
        t.kind == JavaCCParserConstants.CHARACTER_LITERAL)
      out.print(addUnicodeEscapes(t.image));
    else
      out.print(t.image);
    cline = t.endLine;
    ccol = t.endColumn + 1;
    final char last = t.image.charAt(t.image.length() - 1);
    if (last == '\n' || last == '\r') {
      cline++;
      ccol = 1;
    }
  }

  /**
   * Prints a token on a {@link PrintWriter} including the specials.
   * 
   * @param t - a token
   * @param out - a {@link PrintWriter}
   */
  static protected void printToken(final Token t, final PrintWriter out) {
    Token tt = t.specialToken;
    if (tt != null) {
      while (tt.specialToken != null)
        tt = tt.specialToken;
      while (tt != null) {
        printTokenOnly(tt, out);
        tt = tt.next;
      }
    }
    printTokenOnly(t, out);
  }

  /**
   * Prints a list of tokens on a {@link PrintWriter} without the specials.
   * 
   * @param list - a tokens list
   * @param out - a {@link PrintWriter}
   */
  static protected void printTokenList(final List<Token> list, final PrintWriter out) {
    Token t = null;
    for (final Iterator<Token> it = list.iterator(); it.hasNext();) {
      t = it.next();
      printToken(t, out);
    }

    if (t != null)
      printTrailingComments(t, out);
  }

  /**
   * Prints the leading specials of a token on a {@link PrintWriter}.
   * 
   * @param t - a token
   * @param out - a {@link PrintWriter}
   */
  static protected void printLeadingComments(final Token t, final PrintWriter out) {
    if (t.specialToken == null)
      return;
    Token tt = t.specialToken;
    while (tt.specialToken != null)
      tt = tt.specialToken;
    while (tt != null) {
      printTokenOnly(tt, out);
      tt = tt.next;
    }
    if (ccol != 1 && cline != t.beginLine) {
      out.println("");
      cline++;
      ccol = 1;
    }
  }

  /**
   * Prints the trailing specials of a token on a {@link PrintWriter}.
   * 
   * @param t - a token
   * @param out - a {@link PrintWriter}
   */
  static protected void printTrailingComments(final Token t, final PrintWriter out) {
    if (t.next == null)
      return;
    // printLeadingComments(t.next);
    printLeadingComments(t.next, out);
  }

  /**
   * @param t - a token
   * @return a printing of a token without the specials
   */
  static protected String printTokenOnly(final Token t) {
    String retval = "";
    for (; cline < t.beginLine; cline++) {
      retval += "\n";
      ccol = 1;
    }
    for (; ccol < t.beginColumn; ccol++) {
      retval += " ";
    }
    if (t.kind == JavaCCParserConstants.STRING_LITERAL ||
        t.kind == JavaCCParserConstants.CHARACTER_LITERAL)
      retval += addUnicodeEscapes(t.image);
    else
      retval += t.image;
    cline = t.endLine;
    ccol = t.endColumn + 1;
    final char last = t.image.charAt(t.image.length() - 1);
    if (last == '\n' || last == '\r') {
      cline++;
      ccol = 1;
    }
    return retval;
  }

  /**
   * @param t - a token
   * @return a printing of a token including the specials
   */
  static protected String printToken(final Token t) {
    String retval = "";
    Token tt = t.specialToken;
    if (tt != null) {
      while (tt.specialToken != null)
        tt = tt.specialToken;
      while (tt != null) {
        retval += printTokenOnly(tt);
        tt = tt.next;
      }
    }
    retval += printTokenOnly(t);
    return retval;
  }

  /**
   * @param t - a token
   * @return a printing of the leading specials of a token
   */
  static protected String printLeadingComments(final Token t) {
    String retval = "";
    if (t.specialToken == null)
      return retval;
    Token tt = t.specialToken;
    while (tt.specialToken != null)
      tt = tt.specialToken;
    while (tt != null) {
      retval += printTokenOnly(tt);
      tt = tt.next;
    }
    if (ccol != 1 && cline != t.beginLine) {
      retval += "\n";
      cline++;
      ccol = 1;
    }
    return retval;
  }

  /**
   * @param t - a token
   * @return a printing of the trailing specials of a token
   */
  static protected String printTrailingComments(final Token t) {
    if (t.next == null)
      return "";
    return printLeadingComments(t.next);
  }

  /** Reinitializes */
  public static void reInit() {
    fileName = null;
    origFileName = null;
    jjtreeGenerated = false;
    toolNames = null;
    cu_name = null;
    cu_to_insertion_point_1 = new ArrayList<Token>();
    cu_to_insertion_point_2 = new ArrayList<Token>();
    cu_from_insertion_point_2 = new ArrayList<Token>();
    bnfproductions = new ArrayList<NormalProduction>();
    production_table = new HashMap<String, NormalProduction>();
    lexstate_S2I = new Hashtable<String, Integer>();
    lexstate_I2S = new Hashtable<Integer, String>();
    token_mgr_decls = null;
    rexprlist = new ArrayList<TokenProduction>();
    tokenCount = 0;
    named_tokens_table = new HashMap<String, RegularExpression_>();
    ordered_named_tokens = new ArrayList<RegularExpression_>();
    names_of_tokens = new HashMap<Integer, String>();
    rexps_of_tokens = new HashMap<Integer, RegularExpression_>();
    simple_tokens_table = new Hashtable<String, Hashtable<String, Hashtable<String, RegularExpression_>>>();
    maskindex = 0;
    jj2index = 0;
    maskVals = new ArrayList<int[]>();
    cline = 0;
    ccol = 0;
    actForEof = null;
    nextStateForEof = null;
  }

}

/* Copyright (c) 2008, Paul Cager.
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
package EDU.purdue.jtb.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * Generates boiler-plate files from templates.<br>
 * Only very basic template processing is supplied - if we need something more sophisticated I
 * suggest we use a third-party library.
 * 
 * @author Paul Cager, Marc Mazas
 * @version 1.4.0 : 05/2009 : MMa : adapted to JavaCC v4.2 grammar and JDK 1.5
 * @version 1.4.8 : 12/2014 : MMa : improved javadoc
 */
public class JavaFileGenerator {

  /**
   * Constructor with parameters
   * 
   * @param tn - the name of the template. E.g. "/templates/Token.template".
   * @param opt - the processing options in force, such as "STATIC=yes"
   */
  public JavaFileGenerator(final String tn, final Map<String, Object> opt) {
    templateName = tn;
    options = opt;
  }

  /** The template file name */
  private final String              templateName;
  /** The options map */
  private final Map<String, Object> options;
  /** The current line */
  private String                    currentLine;

  /**
   * Generates the output file.
   * 
   * @param out - the PrintWriter to output into
   * @throws IOException - if the template file name is invalid
   */
  public void generate(final PrintWriter out) throws IOException {
    final InputStream is = getClass().getResourceAsStream(templateName);
    if (is == null)
      throw new IOException("Invalid template name: " + templateName);
    final BufferedReader in = new BufferedReader(new InputStreamReader(is));
    process(in, out, false);
  }

  /**
   * Returns the internal current line if not null, otherwise reads a new line, assigns it to the
   * internal current line, and returns it.
   * 
   * @param in - the BufferedReader to read from
   * @return the current line
   * @throws IOException - if any IO exception occurs
   */
  private String peekLine(final BufferedReader in) throws IOException {
    if (currentLine == null)
      currentLine = in.readLine();
    return currentLine;
  }

  /**
   * Reads a new line if the internal current line is not null, sets the internal current line to
   * null, and returns the internal current line if not null or the new line read if null.
   * 
   * @param in - the BufferedReader to read from
   * @return the current line
   * @throws IOException - if any IO exception occurs
   */
  private String getLine(final BufferedReader in) throws IOException {
    final String line = currentLine;
    currentLine = null;
    if (line == null)
      in.readLine();
    return line;
  }

  /**
   * Evaluates whether a given condition maps to true or false.
   * 
   * @param condition - the condition (as a String)
   * @return true or false
   */
  private boolean evaluate(final String condition) {
    final String cond = condition.trim();
    final Object obj = options.get(cond);
    if (obj == null) {
      return cond.equalsIgnoreCase("true") || cond.equalsIgnoreCase("yes");
    }
    if (obj instanceof Boolean) {
      return ((Boolean) obj).booleanValue();
    } else if (obj instanceof String) {
      final String string = ((String) obj).trim();
      return string.length() > 0 && !string.equalsIgnoreCase("false") &&
             !string.equalsIgnoreCase("no");
    }
    return false;
  }

  /**
   * Substitutes a template string with the corresponding text.
   * 
   * @param text - the template string
   * @return the substituted text
   * @throws IOException - if any exception occurs
   */
  private String substitute(final String text) throws IOException {
    int startPos;

    // find the start of template string
    if ((startPos = text.indexOf("${")) == -1) {
      return text;
    }

    // find the matching "}"
    int braceDepth = 1;
    int endPos = startPos + 2;

    while (endPos < text.length() && braceDepth > 0) {
      if (text.charAt(endPos) == '{')
        braceDepth++;
      else if (text.charAt(endPos) == '}')
        braceDepth--;

      endPos++;
    }
    if (braceDepth != 0)
      throw new IOException("Mismatched \"{}\" in template string: " + text);

    final String variableExpression = text.substring(startPos + 2, endPos - 1);

    // Find the end of the variable name
    String value = null;

    for (int i = 0; i < variableExpression.length(); i++) {
      final char ch = variableExpression.charAt(i);

      if (ch == ':' && i < variableExpression.length() - 1 &&
          variableExpression.charAt(i + 1) == '-') {
        value = substituteWithDefault(variableExpression.substring(0, i),
                                      variableExpression.substring(i + 2));
        break;
      } else if (ch == '?') {
        value = substituteWithConditional(variableExpression.substring(0, i),
                                          variableExpression.substring(i + 1));
        break;
      } else if (ch != '_' && !Character.isJavaIdentifierPart(ch)) {
        throw new IOException("Invalid variable in " + text);
      }
    }

    if (value == null) {
      value = substituteWithDefault(variableExpression, "");
    }

    return text.substring(0, startPos) + value + text.substring(endPos);
  }

  /**
   * Substitutes a template string with the corresponding conditional text:<br>
   * if the template evaluates to true, it will substituted with the values substring up to the ':',<br>
   * if the template evaluates to false, it will substituted with the values substring after the
   * ':'.<br>
   * 
   * @param variableName - the text to be substituted
   * @param values - a pair of substituting strings separated by a ':'
   * @return the substituted text
   * @throws IOException - if any exception occurs
   */
  private String substituteWithConditional(final String variableName, final String values)
                                                                                          throws IOException {
    // Split values into true and false values.
    final int pos = values.indexOf(':');
    if (pos == -1)
      throw new IOException("No ':' separator in " + values);
    if (evaluate(variableName))
      return substitute(values.substring(0, pos));
    else
      return substitute(values.substring(pos + 1));
  }

  /**
   * Substitutes a given text with the corresponding option, or with a given default value if the
   * option is not present or empty.
   * 
   * @param variableName - the text to be substituted
   * @param defaultValue - the default value
   * @return the substituted text
   * @throws IOException - if any exception occurs
   */
  private String substituteWithDefault(final String variableName, final String defaultValue)
                                                                                            throws IOException {
    final Object obj = options.get(variableName.trim());
    if (obj == null || obj.toString().length() == 0)
      return substitute(defaultValue);
    return obj.toString();
  }

  /**
   * Writes a text, substituting templates parts if necessary.
   * 
   * @param out - the PrintWriter to write to
   * @param text - the text to write
   * @throws IOException - if any exception occurs
   */
  private void write(final PrintWriter out, final String text) throws IOException {
    String txt = text;
    while (txt.indexOf("${") != -1) {
      txt = substitute(txt);
    }
    out.println(txt);
  }

  /**
   * Processes the template.
   * 
   * @param in - the BufferedReader to read from
   * @param out - the PrintWriter to write to
   * @param ignoring - true if line must not be output, false otherwise
   * @throws IOException - if any exception occurs
   */
  private void process(final BufferedReader in, final PrintWriter out, final boolean ignoring)
                                                                                              throws IOException {
    // out.println("*** process ignore=" + ignoring + " : " + peekLine(in));
    while (peekLine(in) != null) {
      if (peekLine(in).trim().startsWith("#if")) {
        String line = getLine(in).trim();
        final boolean condition = evaluate(line.substring(3).trim());

        process(in, out, ignoring || !condition);
        if (peekLine(in) != null && peekLine(in).trim().startsWith("#else")) {
          getLine(in); // Discard the #else line
          process(in, out, ignoring || condition);
        }

        line = getLine(in);

        if (line == null)
          throw new IOException("Missing \"#fi\"");

        if (!line.trim().startsWith("#fi"))
          throw new IOException("Expected \"#fi\", got: " + line);
      } else if (peekLine(in).trim().startsWith("#")) {
        break;
      } else {
        final String line = getLine(in);
        if (!ignoring)
          write(out, line);
      }
    }

    out.flush();
  }

  /**
   * Test main method.
   * 
   * @param args - command line arguments
   * @throws Exception - if any exception occur
   */
  public static void main(final String[] args) throws Exception {
    final Map<String, Object> map = new HashMap<String, Object>();
    map.put("falseArg", Boolean.FALSE);
    map.put("trueArg", Boolean.TRUE);
    map.put("stringValue", "someString");

    new JavaFileGenerator(args[0], map).generate(new PrintWriter(args[1]));
  }
}

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

/**
 * Output error messages and keep track of totals.
 *
 * @author Marc Mazas
 * @version 1.4.0 : 05/2009 : MMa : adapted to JavaCC v4.2 grammar
 */
public final class JavaCCErrors {

  private static int parse_error_count = 0, semantic_error_count = 0, warning_count = 0;
  private JavaCCErrors() {}

  private static void printLocationInfo(final Object node) {
    if (node instanceof NormalProduction) {
      final NormalProduction n = (NormalProduction)node;
      System.err.print("Line " + n.getLine() + ", Column " + n.getColumn() + ": ");
    } else if (node instanceof TokenProduction) {
      final TokenProduction n = (TokenProduction)node;
      System.err.print("Line " + n.getLine() + ", Column " + n.getColumn() + ": ");
    } else if (node instanceof Expansion_) {
      final Expansion_ n = (Expansion_)node;
      System.err.print("Line " + n.getLine() + ", Column " + n.getColumn() + ": ");
    } else if (node instanceof CharacterRange) {
      final CharacterRange n = (CharacterRange)node;
      System.err.print("Line " + n.getLine() + ", Column " + n.getColumn() + ": ");
    } else if (node instanceof SingleCharacter) {
      final SingleCharacter n = (SingleCharacter)node;
      System.err.print("Line " + n.getLine() + ", Column " + n.getColumn() + ": ");
    } else if (node instanceof Token) {
      final Token t = (Token)node;
      System.err.print("Line " + t.beginLine + ", Column " + t.beginColumn + ": ");
    }
  }

  public static void parse_error(final Object node, final String mess) {
    System.err.print("Error: ");
    printLocationInfo(node);
    System.err.println(mess);
    parse_error_count++;
  }

  public static void parse_error(final String mess) {
    System.err.print("Error: ");
    System.err.println(mess);
    parse_error_count++;
  }

  public static int get_parse_error_count() {
    return parse_error_count;
  }

  public static void semantic_error(final Object node, final String mess) {
    System.err.print("Error: ");
    printLocationInfo(node);
    System.err.println(mess);
    semantic_error_count++;
  }

  public static void semantic_error(final String mess) {
    System.err.print("Error: ");
    System.err.println(mess);
    semantic_error_count++;
  }

  public static final int get_semantic_error_count() {
    return semantic_error_count;
  }

  public static void warning(final Object node, final String mess) {
    System.err.print("Warning: ");
    printLocationInfo(node);
    System.err.println(mess);
    warning_count++;
  }

  public static void warning(final String mess) {
    System.err.print("Warning: ");
    System.err.println(mess);
    warning_count++;
  }

  public static final int get_warning_count() {
    return warning_count;
  }

  public static final int get_error_count() {
    return parse_error_count + semantic_error_count;
  }

   public static void reInit()
   {
      parse_error_count = 0;
      semantic_error_count = 0;
      warning_count = 0;
   }

}

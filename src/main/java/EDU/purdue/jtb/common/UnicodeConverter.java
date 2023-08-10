/**
 * Copyright (c) 2004,2005 UCLA Compilers Group. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided
 * that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this list of conditions and the
 * following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the
 * following disclaimer in the documentation and/or other materials provided with the distribution.
 *
 * Neither UCLA nor the names of its contributors may be used to endorse or promote products derived from this
 * software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 * PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
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

package EDU.purdue.jtb.common;

/**
 * Class {@link UnicodeConverter} contains some static methods to convert unicode chars into their escape
 * sequence form (provided by James Huang from the JavaCC mailing list).
 * <p>
 * Class is thread-safe.
 * </p>
 * TESTCASE some to add
 *
 * @author Marc Mazas
 * @version 1.4.0 : 05-08/2009 : MMa : adapted to JavaCC v4.2 grammar and JDK 1.5
 * @version 1.5.0 : 02-03/2017 : MMa : moved from misc ; added \b in non converted characters
 */
public class UnicodeConverter {
  
  /**
   * Private constructor to prevent javac to create an implicit public constructor.
   */
  private UnicodeConverter() {
    throw new IllegalStateException("Utility class not meant to be instantiated");
  }
  // ModMMa 2017/03 commented as unused (UCDetector)
  // /**
  // * Prints on System.out a string, converting its non ASCII characters in their Unicode
  // * representation.
  // *
  // * @param s - a String to print
  // */
  // static void printout(final String s) {
  // System.out.print(convertString(s));
  // }
  
  // ModMMa 2017/03 commented as unused (UCDetector)
  // /**
  // * Tells whether a character is an ASCII character or not. <br>
  // * ASCII characters are between 0x20 and 0x7e included, or tab, new line, carriage return. *
  // *
  // * @param c - a character
  // * @return true if ASCII, false otherwise
  // */
  // private static boolean isASCII(final char c) {
  // return (c >= 32 && c <= 126) || (c == '\r') || (c == '\n') || (c == '\t');
  // }
  
  // ModMMa 2017/03 commented as unused (UCDetector)
  // /**
  // * Converts an hexadecimal value into a character.
  // *
  // * @param hex - the hexadecimal value
  // * @return the corresponding character
  // */
  // private static char hexToChar(final int hex) {
  // if (hex < 10)
  // return (char) (hex + '0');
  // else
  // return (char) (hex - 10 + 'a');
  // }
  
  // ModMMa 2017/03 commented as unused (UCDetector)
  // /**
  // * Converts a character in its Unicode representation.
  // *
  // * @param c - a character
  // * @return the converted character array
  // */
  // private static char[] unicodeToString(final char c) {
  // final char[] ca = { '\\', 'u', '\0', '\0', '\0', '\0' };
  // ca[2] = hexToChar((c >> 12) & 0x0f);
  // ca[3] = hexToChar((c >> 8) & 0x0f);
  // ca[4] = hexToChar((c >> 4) & 0x0f);
  // ca[5] = hexToChar(c & 0x0f);
  // return ca;
  // }
  
  // ModMMa 2017/03 commented as unused (UCDetector)
  // /**
  // * Converts a string by replacing non ASCII characters by their Unicode representation.
  // *
  // * @param s - the string to convert to Unicode
  // * @return the converted string
  // */
  // private static String convertString(final String s) {
  // final int len = s.length();
  // final StringBuilder sb = new StringBuilder();
  // for (int i = 0; i < len; i++) {
  // final char c = s.charAt(i);
  // if (isASCII(c))
  // sb.append(c);
  // else
  // sb.append(unicodeToString(c));
  // }
  // return sb.toString();
  // }
  
  /**
   * Converts a string by replacing non ASCII characters with their Unicode representation.<br>
   * ASCII characters are between 0x20 and 0x7e included, or tab, new line, carriage return? form feed and
   * backspace.<br>
   * Should be the same as (the commented) {@link #convertString(String)} but comes from JavaCC. CODEJAVA ?
   *
   * @param str - a string
   * @return the converted string
   */
  @SuppressWarnings("javadoc")
  public static String addUnicodeEscapes(final String str) {
    final int strlen = str.length();
    int found = 0;
    for (int i = 0; i < strlen; i++) {
      final char ch = str.charAt(i);
      if ((ch < 0x20 //
          || ch > 0x7e) //
          && ch != '\t' //
          && ch != '\n' //
          && ch != '\r' //
          && ch != '\f' //
          && ch != '\b') {
        found++;
      }
    }
    if (found > 0) {
      final StringBuilder buf = new StringBuilder(strlen + found * 5);
      for (int i = 0; i < strlen; i++) {
        final char ch = str.charAt(i);
        if ((ch < 0x20 //
            || ch > 0x7e) //
            && ch != '\t' //
            && ch != '\n' //
            && ch != '\r' //
            && ch != '\f' //
            && ch != '\b') {
          final String chstr = Integer.toString(ch, 16);
          final int chstrlen = chstr.length();
          buf.append("\\u");
          for (int j = chstrlen; j < 4; j++) {
            buf.append("0");
          }
          buf.append(chstr);
        } else {
          buf.append(ch);
        }
      }
      return buf.toString();
    } else {
      return str;
    }
  }
}

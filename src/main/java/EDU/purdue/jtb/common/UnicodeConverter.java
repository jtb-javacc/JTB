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
 * @version 1.5.3 : 11/2025 : MMa : removed commented code
 */
public class UnicodeConverter {
  
  /**
   * Private constructor to prevent javac to create an implicit public constructor.
   */
  private UnicodeConverter() {
    throw new IllegalStateException("Utility class not meant to be instantiated");
  }

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

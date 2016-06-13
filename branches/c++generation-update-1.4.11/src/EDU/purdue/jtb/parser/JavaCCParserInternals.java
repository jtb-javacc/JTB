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

import java.util.Hashtable;
import java.util.List;

/**
 * Utilities.
 * 
 * @author Marc Mazas
 * @version 1.4.0 : 05/2009 : MMa : adapted to JavaCC v4.2 grammar
 * @version 1.4.8 : 12/2014 : MMa : improved javadoc
 */
public abstract class JavaCCParserInternals extends JavaCCGlobals {

  /** Initializes */
  static protected void initialize() {
    final Integer i = new Integer(0);
    lexstate_S2I.put("DEFAULT", i);
    lexstate_I2S.put(i, "DEFAULT");
    simple_tokens_table.put("DEFAULT",
                            new Hashtable<String, Hashtable<String, RegularExpression_>>());
  }

  /**
   * @param id - the Compilation Unit name
   */
  static protected void addcuname(final String id) {
    cu_name = id;
  }

  /**
   * Compares the second id with the first one and raises a parse error if not identical.
   * 
   * @param t - the token to pass to the error
   * @param id1 - the first id to compare
   * @param id2 - the second id to compare
   */
  static protected void compare(final Token t, final String id1, final String id2) {
    if (!id2.equals(id1)) {
      JavaCCErrors.parse_error(t, "Name " + id2 +
                                  " must be the same as that used at PARSER_BEGIN (" + id1 + ")");
    }
  }

  static private List<Token> add_cu_token_here  = cu_to_insertion_point_1;
  static private Token       first_cu_token;
  static private boolean     insertionpoint1set = false;
  static private boolean     insertionpoint2set = false;

  static protected void setinsertionpoint(final Token t, final int no) {
    do {
      add_cu_token_here.add(first_cu_token);
      first_cu_token = first_cu_token.next;
    }
    while (first_cu_token != t);
    if (no == 1) {
      if (insertionpoint1set) {
        JavaCCErrors.parse_error(t, "Multiple declaration of parser class.");
      } else {
        insertionpoint1set = true;
        add_cu_token_here = cu_to_insertion_point_2;
      }
    } else {
      add_cu_token_here = cu_from_insertion_point_2;
      insertionpoint2set = true;
    }
    first_cu_token = t;
  }

  static protected void insertionpointerrors(final Token t) {
    while (first_cu_token != t) {
      add_cu_token_here.add(first_cu_token);
      first_cu_token = first_cu_token.next;
    }
    if (!insertionpoint1set || !insertionpoint2set) {
      JavaCCErrors.parse_error(t,
                               "Parser class has not been defined between PARSER_BEGIN and PARSER_END.");
    }
  }

  static protected void set_initial_cu_token(final Token t) {
    first_cu_token = t;
  }

  static protected void addproduction(final NormalProduction p) {
    bnfproductions.add(p);
  }

  static protected void production_addexpansion(final BNFProduction_ p, final Expansion_ e) {
    e.parent = p;
    p.setExpansion(e);
  }

  static private int nextFreeLexState = 1;

  static protected void addregexpr(final TokenProduction p) {
    Integer ii;
    rexprlist.add(p);
    if (Options.getUserTokenManager()) {
      if (p.lexStates == null || p.lexStates.length != 1 || !p.lexStates[0].equals("DEFAULT")) {
        JavaCCErrors.warning(p, "Ignoring lexical state specifications since option "
                                + "USER_TOKEN_MANAGER has been set to true.");
      }
    }
    if (p.lexStates == null) {
      return;
    }
    for (int i = 0; i < p.lexStates.length; i++) {
      for (int j = 0; j < i; j++) {
        if (p.lexStates[i].equals(p.lexStates[j])) {
          JavaCCErrors.parse_error(p, "Multiple occurrence of \"" + p.lexStates[i] +
                                      "\" in lexical state list.");
        }
      }
      if (lexstate_S2I.get(p.lexStates[i]) == null) {
        ii = new Integer(nextFreeLexState++);
        lexstate_S2I.put(p.lexStates[i], ii);
        lexstate_I2S.put(ii, p.lexStates[i]);
        simple_tokens_table.put(p.lexStates[i],
                                new Hashtable<String, Hashtable<String, RegularExpression_>>());
      }
    }
  }

  static protected void add_token_manager_decls(final Token t, final List<Token> decls) {
    if (token_mgr_decls != null) {
      JavaCCErrors.parse_error(t, "Multiple occurrence of \"TOKEN_MGR_DECLS\".");
    } else {
      token_mgr_decls = decls;
      if (Options.getUserTokenManager()) {
        JavaCCErrors.warning(t, "Ignoring declarations in \"TOKEN_MGR_DECLS\" since option "
                                + "USER_TOKEN_MANAGER has been set to true.");
      }
    }
  }

  static protected void add_inline_regexpr(final RegularExpression_ r) {
    if (!(r instanceof REndOfFile)) {
      final TokenProduction p = new TokenProduction();
      p.isExplicit = false;
      p.lexStates = new String[] {
        "DEFAULT" };
      p.kind = TokenProduction.TOKEN;
      final RegExprSpec_ res = new RegExprSpec_();
      res.rexp = r;
      res.rexp.tpContext = p;
      res.act = new Action();
      res.nextState = null;
      res.nsTok = null;
      p.respecs.add(res);
      rexprlist.add(p);
    }
  }

  static protected boolean hexchar(final char ch) {
    if (ch >= '0' && ch <= '9')
      return true;
    if (ch >= 'A' && ch <= 'F')
      return true;
    if (ch >= 'a' && ch <= 'f')
      return true;
    return false;
  }

  static protected int hexval(final char ch) {
    if (ch >= '0' && ch <= '9')
      return ch - '0';
    if (ch >= 'A' && ch <= 'F')
      return ch - 'A' + 10;
    return ch - 'a' + 10;
  }

  static protected String remove_escapes_and_quotes(final Token t, final String str) {
    final int strlen = str.length();
    final StringBuilder buff = new StringBuilder(strlen);
    int index = 1;
    char ch, ch1;
    int ordinal;
    while (index < str.length() - 1) {
      if (str.charAt(index) != '\\') {
        buff.append(str.charAt(index));
        index++;
        continue;
      }
      index++;
      ch = str.charAt(index);
      if (ch == 'b') {
        buff.append('\b');
        index++;
        continue;
      }
      if (ch == 't') {
        buff.append('\t');
        index++;
        continue;
      }
      if (ch == 'n') {
        buff.append('\n');
        index++;
        continue;
      }
      if (ch == 'f') {
        buff.append('\f');
        index++;
        continue;
      }
      if (ch == 'r') {
        buff.append('\r');
        index++;
        continue;
      }
      if (ch == '"') {
        buff.append('\"');
        index++;
        continue;
      }
      if (ch == '\'') {
        buff.append('\'');
        index++;
        continue;
      }
      if (ch == '\\') {
        buff.append('\\');
        index++;
        continue;
      }
      if (ch >= '0' && ch <= '7') {
        ordinal = ch - '0';
        index++;
        ch1 = str.charAt(index);
        if (ch1 >= '0' && ch1 <= '7') {
          ordinal = ordinal * 8 + ch1 - '0';
          index++;
          ch1 = str.charAt(index);
          if (ch <= '3' && ch1 >= '0' && ch1 <= '7') {
            ordinal = ordinal * 8 + ch1 - '0';
            index++;
          }
        }
        buff.append((char) ordinal);
        continue;
      }
      if (ch == 'u') {
        index++;
        ch = str.charAt(index);
        if (hexchar(ch)) {
          ordinal = hexval(ch);
          index++;
          ch = str.charAt(index);
          if (hexchar(ch)) {
            ordinal = ordinal * 16 + hexval(ch);
            index++;
            ch = str.charAt(index);
            if (hexchar(ch)) {
              ordinal = ordinal * 16 + hexval(ch);
              index++;
              ch = str.charAt(index);
              if (hexchar(ch)) {
                ordinal = ordinal * 16 + hexval(ch);
                index++;
                continue;
              }
            }
          }
        }
        JavaCCErrors.parse_error(t, "Encountered non-hex character '" + ch + "' at position " +
                                    index + " of string " +
                                    "- Unicode escape must have 4 hex digits after it.");
        return buff.toString();
      }
      JavaCCErrors.parse_error(t, "Illegal escape sequence '\\" + ch + "' at position " + index +
                                  " of string.");
      return buff.toString();
    }
    return buff.toString();
  }

  static protected char character_descriptor_assign(final Token t, final String s) {
    if (s.length() != 1) {
      JavaCCErrors.parse_error(t, "String in character list may contain only one character.");
      return ' ';
    } else {
      return s.charAt(0);
    }
  }

  static protected char character_descriptor_assign(final Token t, final String s, final String left) {
    if (s.length() != 1) {
      JavaCCErrors.parse_error(t, "String in character list may contain only one character.");
      return ' ';
    } else if ((left.charAt(0)) > (s.charAt(0))) {
      JavaCCErrors.parse_error(t,
                               "Right end of character range \'" +
                                   s +
                                   "\' has a lower ordinal value than the left end of character range \'" +
                                   left + "\'.");
      return left.charAt(0);
    } else {
      return s.charAt(0);
    }
  }

  static protected void makeTryBlock(final Token tryLoc, final Container result,
                                     final Container nestedExp, final List<List<Token>> types,
                                     final List<Token> ids, final List<List<Token>> catchblks,
                                     final List<Token> finallyblk) {
    if (catchblks.size() == 0 && finallyblk == null) {
      JavaCCErrors.parse_error(tryLoc,
                               "Try block must contain at least one catch or finally block.");
      return;
    }
    final TryBlock tblk = new TryBlock();
    tblk.setLine(tryLoc.beginLine);
    tblk.setColumn(tryLoc.beginColumn);
    tblk.exp = (Expansion_) (nestedExp.member);
    tblk.exp.parent = tblk;
    tblk.exp.ordinal = 0;
    tblk.types = types;
    tblk.ids = ids;
    tblk.catchblks = catchblks;
    tblk.finallyblk = finallyblk;
    result.member = tblk;
  }

  public static void reInit() {
    add_cu_token_here = cu_to_insertion_point_1;
    first_cu_token = null;
    insertionpoint1set = false;
    insertionpoint2set = false;
    nextFreeLexState = 1;
  }
}

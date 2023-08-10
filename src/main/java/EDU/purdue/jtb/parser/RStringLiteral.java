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

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

/**
 * Describes string literals.
 *
 * @author Marc Mazas
 * @version 1.5.0 : 01/2017 : MMa : added suppress warnings
 */
@SuppressWarnings({ "javadoc", "unused" })
public class RStringLiteral extends RegularExpression_ {

  /** The string image of the literal */
  public String image;

  /** Standard constructor */
  public RStringLiteral() {
  }

  /**
   * Constructor with parameters.
   *
   * @param t - the token
   * @param im - the token image
   */
  public RStringLiteral(final Token t, final String im) {
    setLine(t.beginLine);
    setColumn(t.beginColumn);
    image = im;
  }

  private static int                               maxStrKind      = 0;
  private static int                               maxLen          = 0;
  private static int                               charCnt         = 0;
  private static List<Hashtable<String, KindInfo>> charPosKind     = new ArrayList<>(); // Elements are Hashtables
  // with single char keys;
  private static int[]                             maxLenForActive = new int[100];      // 6400 tokens
  public static String[]                           allImages;
  private static int[][]                           intermediateKinds;
  private static int[][]                           intermediateMatchedPos;

  //  private static int startStateCnt = 0;
  private static boolean                           subString[];
  private static boolean                           subStringAtPos[];
  private static Hashtable<String, long[]>[]       statesForPos;

  /**
   * Initialize all the static variables, so that there is no interference between the various
   * states of the lexer. Need to call this method after generating code for each lexical state.
   */
  public static void ReInit() {
    maxStrKind = 0;
    maxLen = 0;
    charPosKind = new ArrayList<>();
    maxLenForActive = new int[100]; // 6400 tokens
    intermediateKinds = null;
    intermediateMatchedPos = null;
    //    startStateCnt = 0;
    subString = null;
    subStringAtPos = null;
    statesForPos = null;
  }

  //ModMMa 2017/03 commented as unused and with static access
  //  public static void DumpStrLiteralImages(final PrintWriter out) {
  //    String image;
  //    int i;
  //    charCnt = 0; // Set to zero in reInit() but just to be sure
  //
  //    out.println("");
  //    out.println("/** Token literal values. */");
  //    out.println("public static final String[] jjstrLiteralImages = {");
  //
  //    if (allImages == null || allImages.length == 0) {
  //      out.println("};");
  //      return;
  //    }
  //
  //    allImages[0] = "";
  //    for (i = 0; i < allImages.length; i++) {
  //      if ((image = allImages[i]) == null ||
  //          ((LexGen.toSkip[i / 64] & (1L << (i % 64))) == 0L &&
  //           (LexGen.toMore[i / 64] & (1L << (i % 64))) == 0L &&
  //           (LexGen.toToken[i / 64] & (1L << (i % 64))) == 0L) ||
  //          (LexGen.toSkip[i / 64] & (1L << (i % 64))) != 0L ||
  //          (LexGen.toMore[i / 64] & (1L << (i % 64))) != 0L ||
  //          LexGen.canReachOnMore[LexGen.lexStates[i]] ||
  //          ((Options.getIgnoreCase() || LexGen.ignoreCase[i]) &&
  //           (!image.equals(image.toLowerCase()) || !image.equals(image.toUpperCase())))) {
  //        allImages[i] = null;
  //        if ((charCnt += 6) > 80) {
  //          out.println("");
  //          charCnt = 0;
  //        }
  //
  //        out.print("null, ");
  //        continue;
  //      }
  //
  //      final StringBuilder buff = new StringBuilder(64);
  //      buff.append("\"");
  //
  //      for (int j = 0; j < image.length(); j++) {
  //        final char cij = image.charAt(j);
  //        if (cij <= 0xff)
  //          buff.append("\\").append(Integer.toOctalString(cij));
  //        else {
  //          final String hexVal = Integer.toHexString(cij);
  //
  //          buff.append("\\u");
  //          if (hexVal.length() == 3)
  //            buff.append("0");
  //          buff.append(hexVal);
  //        }
  //      }
  //
  //      buff.append("\", ");
  //      final String toPrint = buff.toString();
  //
  //      if ((charCnt += toPrint.length()) >= 80) {
  //        out.println("");
  //        charCnt = 0;
  //      }
  //
  //      out.print(toPrint);
  //    }
  //
  //    while (++i < LexGen.maxOrdinal) {
  //      if ((charCnt += 6) > 80) {
  //        out.println("");
  //        charCnt = 0;
  //      }
  //
  //      out.print("null, ");
  //      continue;
  //    }
  //
  //    out.println("};");
  //  }

  /**
   * Used for top level string literals.
   */
  //ModMMa 2017/03 commented as unused and with static access
  // // public void GenerateDfa(final PrintWriter out, final int kind) {
  //  public void GenerateDfa() {
  //    String s;
  //    Hashtable<String, KindInfo> temp;
  //    KindInfo info;
  //    int len;
  //
  //    if (maxStrKind <= ordinal)
  //      maxStrKind = ordinal + 1;
  //
  //    if ((len = image.length()) > maxLen)
  //      maxLen = len;
  //
  //    char c;
  //    for (int i = 0; i < len; i++) {
  //      if (Options.getIgnoreCase())
  //        s = ("" + (c = image.charAt(i))).toLowerCase();
  //      else
  //        s = "" + (c = image.charAt(i));
  //
  //      if (!NfaState.unicodeWarningGiven && c > 0xff && !Options.getJavaUnicodeEscape() &&
  //          !Options.getUserCharStream()) {
  //        NfaState.unicodeWarningGiven = true;
  //        JavaCCErrors.warning(LexGen.curRE,
  //                             "Non-ASCII characters used in regular expression." +
  //                                           "Please make sure you use the correct Reader when you create the parser, " +
  //                                           "one that can handle your character set.");
  //      }
  //
  //      if (i >= charPosKind.size()) // Kludge, but OK
  //        charPosKind.add(temp = new Hashtable<>());
  //      else
  //        temp = charPosKind.get(i);
  //
  //      if ((info = temp.get(s)) == null)
  //        temp.put(s, info = new KindInfo(LexGen.maxOrdinal));
  //
  //      if (i + 1 == len)
  //        info.InsertFinalKind(ordinal);
  //      else
  //        info.InsertValidKind(ordinal);
  //
  //      if (!Options.getIgnoreCase() && LexGen.ignoreCase[ordinal] && c != Character.toLowerCase(c)) {
  //        s = ("" + image.charAt(i)).toLowerCase();
  //
  //        if (i >= charPosKind.size()) // Kludge, but OK
  //          charPosKind.add(temp = new Hashtable<>());
  //        else
  //          temp = charPosKind.get(i);
  //
  //        if ((info = temp.get(s)) == null)
  //          temp.put(s, info = new KindInfo(LexGen.maxOrdinal));
  //
  //        if (i + 1 == len)
  //          info.InsertFinalKind(ordinal);
  //        else
  //          info.InsertValidKind(ordinal);
  //      }
  //
  //      if (!Options.getIgnoreCase() && LexGen.ignoreCase[ordinal] && c != Character.toUpperCase(c)) {
  //        s = ("" + image.charAt(i)).toUpperCase();
  //
  //        if (i >= charPosKind.size()) // Kludge, but OK
  //          charPosKind.add(temp = new Hashtable<>());
  //        else
  //          temp = charPosKind.get(i);
  //
  //        if ((info = temp.get(s)) == null)
  //          temp.put(s, info = new KindInfo(LexGen.maxOrdinal));
  //
  //        if (i + 1 == len)
  //          info.InsertFinalKind(ordinal);
  //        else
  //          info.InsertValidKind(ordinal);
  //      }
  //    }
  //
  //    maxLenForActive[ordinal / 64] = Math.max(maxLenForActive[ordinal / 64], len - 1);
  //    allImages[ordinal] = image;
  //  }

  //ModMMa 2017/03 commented as unused and with static access
  //  /** {@inheritDoc} */
  //  @Override
  //  public Nfa GenerateNfa(final boolean ignoreCase) {
  //    if (image.length() == 1) {
  //      final RCharacterList temp = new RCharacterList(image.charAt(0));
  //      return temp.GenerateNfa(ignoreCase);
  //    }
  //
  //    NfaState startState = new NfaState();
  //    final NfaState theStartState = startState;
  //    NfaState finalState = null;
  //
  //    if (image.length() == 0)
  //      return new Nfa(theStartState, theStartState);
  //
  //    int i;
  //
  //    for (i = 0; i < image.length(); i++) {
  //      finalState = new NfaState();
  //      startState.charMoves = new char[1];
  //      startState.AddChar(image.charAt(i));
  //
  //      if (Options.getIgnoreCase() || ignoreCase) {
  //        startState.AddChar(Character.toLowerCase(image.charAt(i)));
  //        startState.AddChar(Character.toUpperCase(image.charAt(i)));
  //      }
  //
  //      startState.next = finalState;
  //      startState = finalState;
  //    }
  //
  //    return new Nfa(theStartState, finalState);
  //  }

  //ModMMa 2017/03 commented as unused and with static access
  //  static void DumpNullStrLiterals(final PrintWriter out) {
  //    out.println("{");
  //
  //    if (NfaState.generatedStates != 0)
  //      out.println("   return jjMoveNfa" + LexGen.lexStateSuffix + "(" + NfaState.InitStateName() +
  //                  ", 0);");
  //    else
  //      out.println("   return 1;");
  //
  //    out.println("}");
  //  }

  private static int GetStateSetForKind(final int pos, final int kind) {
    if (LexGen.mixed[LexGen.lexStateIndex] || NfaState.generatedStates == 0) {
      return -1;
    }

    final Hashtable<String, long[]> allStateSets = statesForPos[pos];

    if (allStateSets == null) {
      return -1;
    }

    final Enumeration<String> e = allStateSets.keys();

    while (e.hasMoreElements()) {
      String s = e.nextElement();
      final long[] actives = allStateSets.get(s);

      s = s.substring(s.indexOf(", ") + 2);
      s = s.substring(s.indexOf(", ") + 2);

      if (s.equals("null;")) {
        continue;
      }

      if (actives != null && (actives[kind / 64] & 1L << kind % 64) != 0L) {
        return NfaState.AddStartStateSet(s);
      }
    }

    return -1;
  }

  static String GetLabel(final int kind) {
    final RegularExpression_ re = LexGen.rexprs[kind];

    if (re instanceof RStringLiteral) {
      return " \"" + JavaCCGlobals.add_escapes(((RStringLiteral) re).image) + "\"";
    } else if (!re.label.equals("")) {
      return " <" + re.label + ">";
    } else {
      return " <token of kind " + kind + ">";
    }
  }

  static final int GetLine(final int kind) {
    return LexGen.rexprs[kind].getLine();
  }

  static final int GetColumn(final int kind) {
    return LexGen.rexprs[kind].getColumn();
  }

  /**
   * @param s1 - a first string
   * @param s2 - a second string
   * @return true if s1 starts with s2 (ignoring case for each character)
   */
  static private boolean StartsWithIgnoreCase(final String s1, final String s2) {
    if (s1.length() < s2.length()) {
      return false;
    }

    for (int i = 0; i < s2.length(); i++) {
      final char c1 = s1.charAt(i), c2 = s2.charAt(i);

      if (c1 != c2 && Character.toLowerCase(c2) != c1 && Character.toUpperCase(c2) != c1) {
        return false;
      }
    }

    return true;
  }

  //ModMMa 2017/03 commented as unused and with static access
  //  static void FillSubString() {
  //    String image;
  //    subString = new boolean[maxStrKind + 1];
  //    subStringAtPos = new boolean[maxLen];
  //
  //    for (int i = 0; i < maxStrKind; i++) {
  //      subString[i] = false;
  //
  //      if ((image = allImages[i]) == null || LexGen.lexStates[i] != LexGen.lexStateIndex)
  //        continue;
  //
  //      if (LexGen.mixed[LexGen.lexStateIndex]) {
  //        // We will not optimize for mixed case
  //        subString[i] = true;
  //        subStringAtPos[image.length() - 1] = true;
  //        continue;
  //      }
  //
  //      for (int j = 0; j < maxStrKind; j++) {
  //        if (j != i && LexGen.lexStates[j] == LexGen.lexStateIndex && (allImages[j]) != null) {
  //          if ((allImages[j]).indexOf(image) == 0) {
  //            subString[i] = true;
  //            subStringAtPos[image.length() - 1] = true;
  //            break;
  //          } else if (Options.getIgnoreCase() && StartsWithIgnoreCase(allImages[j], image)) {
  //            subString[i] = true;
  //            subStringAtPos[image.length() - 1] = true;
  //            break;
  //          }
  //        }
  //      }
  //    }
  //  }

  //ModMMa 2017/03 commented as unused and with static access
  //  static void DumpStartWithStates(final PrintWriter out) {
  //    out.println((Options.getStatic() ? "static " : "") + "private int " + "jjStartNfaWithStates" +
  //                LexGen.lexStateSuffix + "(int pos, int kind, int state)");
  //    out.println("{");
  //    out.println("   jjmatchedKind = kind;");
  //    out.println("   jjmatchedPos = pos;");
  //
  //    if (Options.getDebugTokenManager()) {
  //      out.println("   debugStream.println(\"   No more string literal token matches are possible.\");");
  //      out.println("   debugStream.println(\"   Currently matched the first \" " +
  //                  "+ (jjmatchedPos + 1) + \" characters as a \" + tokenImage[jjmatchedKind] + \" token.\");");
  //    }
  //
  //    out.println("   try { curChar = input_stream.readChar(); }");
  //    out.println("   catch(java.io.IOException e) { return pos + 1; }");
  //
  //    if (Options.getDebugTokenManager())
  //      out.println("   debugStream.println(" +
  //                  (LexGen.maxLexStates > 1 ? "\"<\" + lexStateNames[curLexState] + \">\" + " : "") +
  //                  "\"Current character : \" + " +
  //                  "TokenMgrError.addEscapes(String.valueOf(curChar)) + \" (\" + (int)curChar + \") " +
  //                  "at line \" + input_stream.getEndLine() + \" column \" + input_stream.getEndColumn());");
  //
  //    out.println("   return jjMoveNfa" + LexGen.lexStateSuffix + "(state, pos + 1);");
  //    out.println("}");
  //  }

  private static boolean boilerPlateDumped = false;

  //ModMMa 2017/03 commented as unused and with static access
  //  static void DumpBoilerPlate(final PrintWriter out) {
  //    out.println((Options.getStatic() ? "static " : "") + "private int " +
  //                "jjStopAtPos(int pos, int kind)");
  //    out.println("{");
  //    out.println("   jjmatchedKind = kind;");
  //    out.println("   jjmatchedPos = pos;");
  //
  //    if (Options.getDebugTokenManager()) {
  //      out.println("   debugStream.println(\"   No more string literal token matches are possible.\");");
  //      out.println("   debugStream.println(\"   Currently matched the first \" + (jjmatchedPos + 1) + " +
  //                  "\" characters as a \" + tokenImage[jjmatchedKind] + \" token.\");");
  //    }
  //
  //    out.println("   return pos + 1;");
  //    out.println("}");
  //  }
  //
  //  static String[] ReArrange(final Hashtable<String, KindInfo> tab) {
  //    final String[] ret = new String[tab.size()];
  //    final Enumeration<String> e = tab.keys();
  //    int cnt = 0;
  //
  //    while (e.hasMoreElements()) {
  //      int i = 0, j;
  //      String s;
  //      final char c = (s = e.nextElement()).charAt(0);
  //
  //      while (i < cnt && ret[i].charAt(0) < c)
  //        i++;
  //
  //      if (i < cnt)
  //        for (j = cnt - 1; j >= i; j--)
  //          ret[j + 1] = ret[j];
  //
  //      ret[i] = s;
  //      cnt++;
  //    }
  //
  //    return ret;
  //  }

  //ModMMa 2017/03 commented as unused and with static access
  //  static void DumpDfaCode(final PrintWriter out) {
  //    Hashtable<String, KindInfo> tab;
  //    String key;
  //    KindInfo info;
  //    final int maxLongsReqd = maxStrKind / 64 + 1;
  //    int i, j, k;
  //    boolean ifGenerated;
  //    LexGen.maxLongsReqd[LexGen.lexStateIndex] = maxLongsReqd;
  //
  //    if (maxLen == 0) {
  //      out.println((Options.getStatic() ? "static " : "") + "private int " +
  //                  "jjMoveStringLiteralDfa0" + LexGen.lexStateSuffix + "()");
  //
  //      DumpNullStrLiterals(out);
  //      return;
  //    }
  //
  //    if (!boilerPlateDumped) {
  //      DumpBoilerPlate(out);
  //      boilerPlateDumped = true;
  //    }
  //
  //    boolean createStartNfa = false;
  //    ;
  //    for (i = 0; i < maxLen; i++) {
  //      boolean atLeastOne = false;
  //      boolean startNfaNeeded = false;
  //      tab = charPosKind.get(i);
  //      final String[] keys = ReArrange(tab);
  //
  //      out.print((Options.getStatic() ? "static " : "") + "private int " + "jjMoveStringLiteralDfa" +
  //                i + LexGen.lexStateSuffix + "(");
  //
  //      if (i != 0) {
  //        if (i == 1) {
  //          for (j = 0; j < maxLongsReqd - 1; j++)
  //            if (i <= maxLenForActive[j]) {
  //              if (atLeastOne)
  //                out.print(", ");
  //              else
  //                atLeastOne = true;
  //              out.print("final long active" + j);
  //            }
  //
  //          if (i <= maxLenForActive[j]) {
  //            if (atLeastOne)
  //              out.print(", ");
  //            out.print("final long active" + j);
  //          }
  //        } else {
  //          for (j = 0; j < maxLongsReqd - 1; j++)
  //            if (i <= maxLenForActive[j] + 1) {
  //              if (atLeastOne)
  //                out.print(", ");
  //              else
  //                atLeastOne = true;
  //              out.print("final long old" + j + ", final long act" + j);
  //            }
  //
  //          if (i <= maxLenForActive[j] + 1) {
  //            if (atLeastOne)
  //              out.print(", ");
  //            out.print("final long old" + j + ", final long act" + j);
  //          }
  //        }
  //      }
  //      out.println(")");
  //      out.println("{");
  //
  //      if (i != 0) {
  //        if (i > 1) {
  //
  //          for (j = 0; j < maxLongsReqd - 1; j++)
  //            if (i <= maxLenForActive[j] + 1) {
  //              out.println("   long active" + j + " = act" + j + ";");
  //            }
  //
  //          if (i <= maxLenForActive[j] + 1) {
  //            out.println("   long active" + j + " = act" + j + ";");
  //          }
  //
  //          atLeastOne = false;
  //          out.print("   if ((");
  //
  //          for (j = 0; j < maxLongsReqd - 1; j++)
  //            if (i <= maxLenForActive[j] + 1) {
  //              if (atLeastOne)
  //                out.print(" | ");
  //              else
  //                atLeastOne = true;
  //              out.print("(active" + j + " &= old" + j + ")");
  //            }
  //
  //          if (i <= maxLenForActive[j] + 1) {
  //            if (atLeastOne)
  //              out.print(" | ");
  //            out.print("(active" + j + " &= old" + j + ")");
  //          }
  //
  //          out.println(") == 0L)");
  //          if (!LexGen.mixed[LexGen.lexStateIndex] && NfaState.generatedStates != 0) {
  //            out.print("      return jjStartNfa" + LexGen.lexStateSuffix + "(" + (i - 2) + ", ");
  //            for (j = 0; j < maxLongsReqd - 1; j++)
  //              if (i <= maxLenForActive[j] + 1)
  //                out.print("old" + j + ", ");
  //              else
  //                out.print("0L, ");
  //            if (i <= maxLenForActive[j] + 1)
  //              out.println("old" + j + ");");
  //            else
  //              out.println("0L);");
  //          } else if (NfaState.generatedStates != 0)
  //            out.println("      return jjMoveNfa" + LexGen.lexStateSuffix + "(" +
  //                        NfaState.InitStateName() + ", " + (i - 1) + ");");
  //          else
  //            out.println("      return " + i + ";");
  //        }
  //
  //        if (i != 0 && Options.getDebugTokenManager()) {
  //          out.println("   if (jjmatchedKind != 0 && jjmatchedKind != 0x" +
  //                      Integer.toHexString(Integer.MAX_VALUE) + ")");
  //          out.println("      debugStream.println(\"   Currently matched the first \" + " +
  //                      "(jjmatchedPos + 1) + \" characters as a \" + tokenImage[jjmatchedKind] + \" token.\");");
  //
  //          out.println("   debugStream.println(\"   Possible string literal matches : { \"");
  //
  //          for (int vecs = 0; vecs < maxStrKind / 64 + 1; vecs++) {
  //            if (i <= maxLenForActive[vecs]) {
  //              out.println(" +");
  //              out.print("         jjKindsForBitVector(" + vecs + ", ");
  //              out.print("active" + vecs + ") ");
  //            }
  //          }
  //
  //          out.println(" + \" } \");");
  //        }
  //
  //        out.println("   try { curChar = input_stream.readChar(); }");
  //        out.println("   catch(java.io.IOException e) {");
  //
  //        if (!LexGen.mixed[LexGen.lexStateIndex] && NfaState.generatedStates != 0) {
  //          out.print("      jjStopStringLiteralDfa" + LexGen.lexStateSuffix + "(" + (i - 1) + ", ");
  //          for (k = 0; k < maxLongsReqd - 1; k++)
  //            if (i <= maxLenForActive[k])
  //              out.print("active" + k + ", ");
  //            else
  //              out.print("0L, ");
  //
  //          if (i <= maxLenForActive[k])
  //            out.println("active" + k + ");");
  //          else
  //            out.println("0L);");
  //
  //          if (i != 0 && Options.getDebugTokenManager()) {
  //            out.println("      if (jjmatchedKind != 0 && jjmatchedKind != 0x" +
  //                        Integer.toHexString(Integer.MAX_VALUE) + ")");
  //            out.println("         debugStream.println(\"   Currently matched the first \" + " +
  //                        "(jjmatchedPos + 1) + \" characters as a \" + tokenImage[jjmatchedKind] + \" token.\");");
  //          }
  //          out.println("      return " + i + ";");
  //        } else if (NfaState.generatedStates != 0)
  //          out.println("   return jjMoveNfa" + LexGen.lexStateSuffix + "(" +
  //                      NfaState.InitStateName() + ", " + (i - 1) + ");");
  //        else
  //          out.println("      return " + i + ";");
  //
  //        out.println("   }");
  //      }
  //
  //      if (i != 0 && Options.getDebugTokenManager())
  //        out.println("   debugStream.println(" +
  //                    (LexGen.maxLexStates > 1 ? "\"<\" + lexStateNames[curLexState] + \">\" + "
  //                                             : "") +
  //                    "\"Current character : \" + " +
  //                    "TokenMgrError.addEscapes(String.valueOf(curChar)) + \" (\" + (int)curChar + \") " +
  //                    "at line \" + input_stream.getEndLine() + \" column \" + input_stream.getEndColumn());");
  //
  //      out.println("   switch(curChar)");
  //      out.println("   {");
  //
  //      CaseLoop: for (int q = 0; q < keys.length; q++) {
  //        key = keys[q];
  //        info = tab.get(key);
  //        ifGenerated = false;
  //        final char c = key.charAt(0);
  //
  //        if (i == 0 && c < 128 && info.finalKindCnt != 0 &&
  //            (NfaState.generatedStates == 0 || !NfaState.CanStartNfaUsingAscii(c))) {
  //          int kind;
  //          for (j = 0; j < maxLongsReqd; j++)
  //            if (info.finalKinds[j] != 0L)
  //              break;
  //
  //          for (k = 0; k < 64; k++)
  //            if ((info.finalKinds[j] & (1L << k)) != 0L && !subString[kind = (j * 64 + k)]) {
  //              if ((intermediateKinds != null && intermediateKinds[(j * 64 + k)] != null &&
  //                   intermediateKinds[(j * 64 + k)][i] < (j * 64 + k) &&
  //                   intermediateMatchedPos != null &&
  //                   intermediateMatchedPos[(j * 64 + k)][i] == i) ||
  //                  (LexGen.canMatchAnyChar[LexGen.lexStateIndex] >= 0 &&
  //                   LexGen.canMatchAnyChar[LexGen.lexStateIndex] < (j * 64 + k)))
  //                break;
  //              else if ((LexGen.toSkip[kind / 64] & (1L << (kind % 64))) != 0L &&
  //                       (LexGen.toSpecial[kind / 64] & (1L << (kind % 64))) == 0L &&
  //                       LexGen.actions[kind] == null && LexGen.newLexState[kind] == null) {
  //                LexGen.AddCharToSkip(c, kind);
  //
  //                if (Options.getIgnoreCase()) {
  //                  if (c != Character.toUpperCase(c))
  //                    LexGen.AddCharToSkip(Character.toUpperCase(c), kind);
  //
  //                  if (c != Character.toLowerCase(c))
  //                    LexGen.AddCharToSkip(Character.toLowerCase(c), kind);
  //                }
  //                continue CaseLoop;
  //              }
  //            }
  //        }
  //
  //        // Since we know key is a single character ...
  //        if (Options.getIgnoreCase()) {
  //          if (c != Character.toUpperCase(c))
  //            out.println("      case " + (int) Character.toUpperCase(c) + ":");
  //
  //          if (c != Character.toLowerCase(c))
  //            out.println("      case " + (int) Character.toLowerCase(c) + ":");
  //        }
  //
  //        out.println("      case " + (int) c + ":");
  //
  //        long matchedKind;
  //        final String prefix = (i == 0) ? "         " : "            ";
  //
  //        if (info.finalKindCnt != 0) {
  //          for (j = 0; j < maxLongsReqd; j++) {
  //            if ((matchedKind = info.finalKinds[j]) == 0L)
  //              continue;
  //
  //            for (k = 0; k < 64; k++) {
  //              if ((matchedKind & (1L << k)) == 0L)
  //                continue;
  //
  //              if (ifGenerated) {
  //                out.print("         else if ");
  //              } else if (i != 0)
  //                out.print("         if ");
  //
  //              ifGenerated = true;
  //
  //              int kindToPrint;
  //              if (i != 0) {
  //                out.println("((active" + j + " & 0x" + Long.toHexString(1L << k) + "L) != 0L)");
  //              }
  //
  //              if (intermediateKinds != null && intermediateKinds[(j * 64 + k)] != null &&
  //                  intermediateKinds[(j * 64 + k)][i] < (j * 64 + k) &&
  //                  intermediateMatchedPos != null && intermediateMatchedPos[(j * 64 + k)][i] == i) {
  //                JavaCCErrors.warning(" \"" + JavaCCGlobals.add_escapes(allImages[j * 64 + k]) +
  //                                     "\" cannot be matched as a string literal token " +
  //                                     "at line " + GetLine(j * 64 + k) + ", column " +
  //                                     GetColumn(j * 64 + k) + ". It will be matched as " +
  //                                     GetLabel(intermediateKinds[(j * 64 + k)][i]) + ".");
  //                kindToPrint = intermediateKinds[(j * 64 + k)][i];
  //              } else if (i == 0 && LexGen.canMatchAnyChar[LexGen.lexStateIndex] >= 0 &&
  //                         LexGen.canMatchAnyChar[LexGen.lexStateIndex] < (j * 64 + k)) {
  //                JavaCCErrors.warning(" \"" + JavaCCGlobals.add_escapes(allImages[j * 64 + k]) +
  //                                     "\" cannot be matched as a string literal token " +
  //                                     "at line " + GetLine(j * 64 + k) + ", column " +
  //                                     GetColumn(j * 64 + k) + ". It will be matched as " +
  //                                     GetLabel(LexGen.canMatchAnyChar[LexGen.lexStateIndex]) + ".");
  //                kindToPrint = LexGen.canMatchAnyChar[LexGen.lexStateIndex];
  //              } else
  //                kindToPrint = j * 64 + k;
  //
  //              if (!subString[(j * 64 + k)]) {
  //                final int stateSetName = GetStateSetForKind(i, j * 64 + k);
  //
  //                if (stateSetName != -1) {
  //                  createStartNfa = true;
  //                  out.println(prefix + "return jjStartNfaWithStates" + LexGen.lexStateSuffix + "(" +
  //                              i + ", " + kindToPrint + ", " + stateSetName + ");");
  //                } else
  //                  out.println(prefix + "return jjStopAtPos" + "(" + i + ", " + kindToPrint + ");");
  //              } else {
  //                if ((LexGen.initMatch[LexGen.lexStateIndex] != 0 &&
  //                     LexGen.initMatch[LexGen.lexStateIndex] != Integer.MAX_VALUE) ||
  //                    i != 0) {
  //                  out.println("         {");
  //                  out.println(prefix + "jjmatchedKind = " + kindToPrint + ";");
  //                  out.println(prefix + "jjmatchedPos = " + i + ";");
  //                  out.println("         }");
  //                } else
  //                  out.println(prefix + "jjmatchedKind = " + kindToPrint + ";");
  //              }
  //            }
  //          }
  //        }
  //
  //        if (info.validKindCnt != 0) {
  //          atLeastOne = false;
  //
  //          if (i == 0) {
  //            out.print("         return ");
  //
  //            out.print("jjMoveStringLiteralDfa" + (i + 1) + LexGen.lexStateSuffix + "(");
  //            for (j = 0; j < maxLongsReqd - 1; j++)
  //              if ((i + 1) <= maxLenForActive[j]) {
  //                if (atLeastOne)
  //                  out.print(", ");
  //                else
  //                  atLeastOne = true;
  //
  //                out.print("0x" + Long.toHexString(info.validKinds[j]) + "L");
  //              }
  //
  //            if ((i + 1) <= maxLenForActive[j]) {
  //              if (atLeastOne)
  //                out.print(", ");
  //
  //              out.print("0x" + Long.toHexString(info.validKinds[j]) + "L");
  //            }
  //            out.println(");");
  //          } else {
  //            out.print("         return ");
  //
  //            out.print("jjMoveStringLiteralDfa" + (i + 1) + LexGen.lexStateSuffix + "(");
  //
  //            for (j = 0; j < maxLongsReqd - 1; j++)
  //              if ((i + 1) <= maxLenForActive[j] + 1) {
  //                if (atLeastOne)
  //                  out.print(", ");
  //                else
  //                  atLeastOne = true;
  //
  //                if (info.validKinds[j] != 0L)
  //                  out.print("active" + j + ", 0x" + Long.toHexString(info.validKinds[j]) + "L");
  //                else
  //                  out.print("active" + j + ", 0L");
  //              }
  //
  //            if ((i + 1) <= maxLenForActive[j] + 1) {
  //              if (atLeastOne)
  //                out.print(", ");
  //              if (info.validKinds[j] != 0L)
  //                out.print("active" + j + ", 0x" + Long.toHexString(info.validKinds[j]) + "L");
  //              else
  //                out.print("active" + j + ", 0L");
  //            }
  //
  //            out.println(");");
  //          }
  //        } else {
  //          // A very special case.
  //          if (i == 0 && LexGen.mixed[LexGen.lexStateIndex]) {
  //
  //            if (NfaState.generatedStates != 0)
  //              out.println("         return jjMoveNfa" + LexGen.lexStateSuffix + "(" +
  //                          NfaState.InitStateName() + ", 0);");
  //            else
  //              out.println("         return 1;");
  //          } else if (i != 0) // No more str literals to look for
  //          {
  //            out.println("         break;");
  //            startNfaNeeded = true;
  //          }
  //        }
  //      }
  //
  //      /* default means that the current character is not in any of the
  //         strings at this position. */
  //      out.println("      default :");
  //
  //      if (Options.getDebugTokenManager())
  //        out.println("      debugStream.println(\"   No string literal matches possible.\");");
  //
  //      if (NfaState.generatedStates != 0) {
  //        if (i == 0) {
  //          /* This means no string literal is possible. Just move nfa with
  //             this guy and return. */
  //          out.println("         return jjMoveNfa" + LexGen.lexStateSuffix + "(" +
  //                      NfaState.InitStateName() + ", 0);");
  //        } else {
  //          out.println("         break;");
  //          startNfaNeeded = true;
  //        }
  //      } else {
  //        out.println("         return " + (i + 1) + ";");
  //      }
  //
  //      out.println("   }");
  //
  //      if (i != 0) {
  //        if (startNfaNeeded) {
  //          if (!LexGen.mixed[LexGen.lexStateIndex] && NfaState.generatedStates != 0) {
  //            /* Here, a string literal is successfully matched and no more
  //               string literals are possible. So set the kind and state set
  //               upto and including this position for the matched string. */
  //
  //            out.print("   return jjStartNfa" + LexGen.lexStateSuffix + "(" + (i - 1) + ", ");
  //            for (k = 0; k < maxLongsReqd - 1; k++)
  //              if (i <= maxLenForActive[k])
  //                out.print("active" + k + ", ");
  //              else
  //                out.print("0L, ");
  //            if (i <= maxLenForActive[k])
  //              out.println("active" + k + ");");
  //            else
  //              out.println("0L);");
  //          } else if (NfaState.generatedStates != 0)
  //            out.println("   return jjMoveNfa" + LexGen.lexStateSuffix + "(" +
  //                        NfaState.InitStateName() + ", " + i + ");");
  //          else
  //            out.println("   return " + (i + 1) + ";");
  //        }
  //      }
  //
  //      out.println("}");
  //    }
  //
  //    if (!LexGen.mixed[LexGen.lexStateIndex] && NfaState.generatedStates != 0 && createStartNfa)
  //      DumpStartWithStates(out);
  //  }

  //ModMMa 2017/03 commented as unused and with static access
  //  static final int GetStrKind(final String str) {
  //    for (int i = 0; i < maxStrKind; i++) {
  //      if (LexGen.lexStates[i] != LexGen.lexStateIndex)
  //        continue;
  //
  //      final String image = allImages[i];
  //      if (image != null && image.equals(str))
  //        return i;
  //    }
  //
  //    return Integer.MAX_VALUE;
  //  }

  //ModMMa 2017/03 commented as unused and with static access
  //  @SuppressWarnings("unchecked")
  //  // for statesForPos = new Hashtable[maxLen];
  //  static void GenerateNfaStartStates(final PrintWriter out, final NfaState initialState) {
  //    final boolean[] seen = new boolean[NfaState.generatedStates];
  //    final Map<String, String> stateSets = new Hashtable<>();
  //    String stateSetString = "";
  //    int i, j, kind, jjmatchedPos = 0;
  //    final int maxKindsReqd = maxStrKind / 64 + 1;
  //    long[] actives;
  //    List<NfaState> newStates = new ArrayList<>();
  //    List<NfaState> oldStates = null, jjtmpStates;
  //
  //    statesForPos = new Hashtable[maxLen];
  //    intermediateKinds = new int[maxStrKind + 1][];
  //    intermediateMatchedPos = new int[maxStrKind + 1][];
  //
  //    for (i = 0; i < maxStrKind; i++) {
  //      if (LexGen.lexStates[i] != LexGen.lexStateIndex)
  //        continue;
  //
  //      final String image = allImages[i];
  //
  //      if (image == null || image.length() < 1)
  //        continue;
  //
  //      try {
  //        if ((oldStates = (List<NfaState>) initialState.epsilonMoves.clone()) == null ||
  //            oldStates.size() == 0) {
  //          DumpNfaStartStatesCode(statesForPos, out);
  //          return;
  //        }
  //      }
  //      catch (@SuppressWarnings("unused") final Exception e) {
  //        JavaCCErrors.semantic_error("Error cloning state vector");
  //      }
  //
  //      intermediateKinds[i] = new int[image.length()];
  //      intermediateMatchedPos[i] = new int[image.length()];
  //      jjmatchedPos = 0;
  //      kind = Integer.MAX_VALUE;
  //
  //      for (j = 0; j < image.length(); j++) {
  //        if (oldStates == null || oldStates.size() <= 0) {
  //          // Here, j > 0
  //          kind = intermediateKinds[i][j] = intermediateKinds[i][j - 1];
  //          jjmatchedPos = intermediateMatchedPos[i][j] = intermediateMatchedPos[i][j - 1];
  //        } else {
  //          kind = NfaState.MoveFromSet(image.charAt(j), oldStates, newStates);
  //          oldStates.clear();
  //
  //          if (j == 0 && kind != Integer.MAX_VALUE &&
  //              LexGen.canMatchAnyChar[LexGen.lexStateIndex] != -1 &&
  //              kind > LexGen.canMatchAnyChar[LexGen.lexStateIndex])
  //            kind = LexGen.canMatchAnyChar[LexGen.lexStateIndex];
  //
  //          if (GetStrKind(image.substring(0, j + 1)) < kind) {
  //            intermediateKinds[i][j] = kind = Integer.MAX_VALUE;
  //            jjmatchedPos = 0;
  //          } else if (kind != Integer.MAX_VALUE) {
  //            intermediateKinds[i][j] = kind;
  //            jjmatchedPos = intermediateMatchedPos[i][j] = j;
  //          } else if (j == 0)
  //            kind = intermediateKinds[i][j] = Integer.MAX_VALUE;
  //          else {
  //            kind = intermediateKinds[i][j] = intermediateKinds[i][j - 1];
  //            jjmatchedPos = intermediateMatchedPos[i][j] = intermediateMatchedPos[i][j - 1];
  //          }
  //
  //          stateSetString = NfaState.GetStateSetString(newStates);
  //        }
  //
  //        if (kind == Integer.MAX_VALUE && (newStates == null || newStates.size() == 0))
  //          continue;
  //
  //        int p;
  //        if (stateSets.get(stateSetString) == null) {
  //          stateSets.put(stateSetString, stateSetString);
  //          for (p = 0; p < newStates.size(); p++) {
  //            if (seen[newStates.get(p).stateName])
  //              newStates.get(p).inNextOf++;
  //            else
  //              seen[newStates.get(p).stateName] = true;
  //          }
  //        } else {
  //          for (p = 0; p < newStates.size(); p++)
  //            seen[newStates.get(p).stateName] = true;
  //        }
  //
  //        jjtmpStates = oldStates;
  //        oldStates = newStates;
  //        (newStates = jjtmpStates).clear();
  //
  //        if (statesForPos[j] == null)
  //          statesForPos[j] = new Hashtable<>();
  //
  //        if ((actives = statesForPos[j].get(kind + ", " + jjmatchedPos + ", " +
  //                                           stateSetString)) == null) {
  //          actives = new long[maxKindsReqd];
  //          statesForPos[j].put(kind + ", " + jjmatchedPos + ", " + stateSetString, actives);
  //        }
  //
  //        actives[i / 64] |= 1L << (i % 64);
  //        //String name = NfaState.StoreStateSet(stateSetString);
  //      }
  //    }
  //
  //    DumpNfaStartStatesCode(statesForPos, out);
  //  }

  //ModMMa 2017/03 commented as unused and with static access
  // static void DumpNfaStartStatesCode(final Hashtable<String, long[]>[] states,
  //                                     final PrintWriter out) {
  //    if (maxStrKind == 0) { // No need to generate this function
  //      return;
  //    }
  //
  //    int i;
  //    final int maxKindsReqd = maxStrKind / 64 + 1;
  //    boolean condGenerated = false;
  //    int ind = 0;
  //
  //    out.print("private" + (Options.getStatic() ? " static" : "") +
  //              " final int jjStopStringLiteralDfa" + LexGen.lexStateSuffix + "(int pos, ");
  //    for (i = 0; i < maxKindsReqd - 1; i++)
  //      out.print("long active" + i + ", ");
  //    out.println("long active" + i + ")\n{");
  //
  //    if (Options.getDebugTokenManager())
  //      out.println("      debugStream.println(\"   No more string literal token matches are possible.\");");
  //
  //    out.println("   switch (pos)\n   {");
  //
  //    for (i = 0; i < maxLen - 1; i++) {
  //      if (states[i] == null)
  //        continue;
  //
  //      out.println("      case " + i + ":");
  //
  //      final Enumeration<String> e = states[i].keys();
  //      while (e.hasMoreElements()) {
  //        String stateSetString = e.nextElement();
  //        final long[] actives = states[i].get(stateSetString);
  //
  //        for (int j = 0; j < maxKindsReqd; j++) {
  //          if (actives[j] == 0L)
  //            continue;
  //
  //          if (condGenerated)
  //            out.print(" || ");
  //          else
  //            out.print("         if (");
  //
  //          condGenerated = true;
  //
  //          out.print("(active" + j + " & 0x" + Long.toHexString(actives[j]) + "L) != 0L");
  //        }
  //
  //        if (condGenerated) {
  //          out.println(")");
  //
  //          String kindStr = stateSetString.substring(0, ind = stateSetString.indexOf(", "));
  //          String afterKind = stateSetString.substring(ind + 2);
  //          final int jjmatchedPos = Integer.parseInt(afterKind.substring(0,
  //                                                                        afterKind.indexOf(", ")));
  //
  //          if (!kindStr.equals(String.valueOf(Integer.MAX_VALUE)))
  //            out.println("         {");
  //
  //          if (!kindStr.equals(String.valueOf(Integer.MAX_VALUE))) {
  //            if (i == 0) {
  //              out.println("            jjmatchedKind = " + kindStr + ";");
  //
  //              if ((LexGen.initMatch[LexGen.lexStateIndex] != 0 &&
  //                   LexGen.initMatch[LexGen.lexStateIndex] != Integer.MAX_VALUE))
  //                out.println("            jjmatchedPos = 0;");
  //            } else if (i == jjmatchedPos) {
  //              if (subStringAtPos[i]) {
  //                out.println("            if (jjmatchedPos != " + i + ")");
  //                out.println("            {");
  //                out.println("               jjmatchedKind = " + kindStr + ";");
  //                out.println("               jjmatchedPos = " + i + ";");
  //                out.println("            }");
  //              } else {
  //                out.println("            jjmatchedKind = " + kindStr + ";");
  //                out.println("            jjmatchedPos = " + i + ";");
  //              }
  //            } else {
  //              if (jjmatchedPos > 0)
  //                out.println("            if (jjmatchedPos < " + jjmatchedPos + ")");
  //              else
  //                out.println("            if (jjmatchedPos == 0)");
  //              out.println("            {");
  //              out.println("               jjmatchedKind = " + kindStr + ";");
  //              out.println("               jjmatchedPos = " + jjmatchedPos + ";");
  //              out.println("            }");
  //            }
  //          }
  //
  //          kindStr = stateSetString.substring(0, ind = stateSetString.indexOf(", "));
  //          afterKind = stateSetString.substring(ind + 2);
  //          stateSetString = afterKind.substring(afterKind.indexOf(", ") + 2);
  //
  //          if (stateSetString.equals("null;"))
  //            out.println("            return -1;");
  //          else
  //            out.println("            return " + NfaState.AddStartStateSet(stateSetString) + ";");
  //
  //          if (!kindStr.equals(String.valueOf(Integer.MAX_VALUE)))
  //            out.println("         }");
  //          condGenerated = false;
  //        }
  //      }
  //
  //      out.println("         return -1;");
  //    }
  //
  //    out.println("      default :");
  //    out.println("         return -1;");
  //    out.println("   }");
  //    out.println("}");
  //
  //    out.print("private" + (Options.getStatic() ? " static" : "") + " final int jjStartNfa" +
  //              LexGen.lexStateSuffix + "(int pos, ");
  //    for (i = 0; i < maxKindsReqd - 1; i++)
  //      out.print("long active" + i + ", ");
  //    out.println("long active" + i + ")\n{");
  //
  //    if (LexGen.mixed[LexGen.lexStateIndex]) {
  //      if (NfaState.generatedStates != 0)
  //        out.println("   return jjMoveNfa" + LexGen.lexStateSuffix + "(" + NfaState.InitStateName() +
  //                    ", pos + 1);");
  //      else
  //        out.println("   return pos + 1;");
  //
  //      out.println("}");
  //      return;
  //    }
  //
  //    out.print("   return jjMoveNfa" + LexGen.lexStateSuffix + "(" + "jjStopStringLiteralDfa" +
  //              LexGen.lexStateSuffix + "(pos, ");
  //    for (i = 0; i < maxKindsReqd - 1; i++)
  //      out.print("active" + i + ", ");
  //    out.print("active" + i + ")");
  //    out.println(", pos + 1);");
  //    out.println("}");
  //  }

  /**
   * Return to original state.
   */
  //ModMMa 2017/03 commented as unused and with static access
  //  public static void reInit() {
  //    ReInit();
  //
  //    charCnt = 0;
  //    allImages = null;
  //    boilerPlateDumped = false;
  //  }

  /** {@inheritDoc} */
  @Override
  public StringBuilder dump(final int indent, final Set<Object> alreadyDumped) {
    final StringBuilder sb = super.dump(indent, alreadyDumped).append(' ').append(image);
    return sb;
  }

  /** {@inheritDoc} */
  @Override
  public String toString() {
    return super.toString() + " - " + image;
  }

  final class KindInfo {

    long[] validKinds;
    long[] finalKinds;
    int    validKindCnt = 0;
    int    finalKindCnt = 0;

    KindInfo(final int maxKind) {
      validKinds = new long[maxKind / 64 + 1];
      finalKinds = new long[maxKind / 64 + 1];
    }

    public void InsertValidKind(final int kind) {
      validKinds[kind / 64] |= 1L << kind % 64;
      validKindCnt++;
    }

    public void InsertFinalKind(final int kind) {
      finalKinds[kind / 64] |= 1L << kind % 64;
      finalKindCnt++;
    }
  }

}

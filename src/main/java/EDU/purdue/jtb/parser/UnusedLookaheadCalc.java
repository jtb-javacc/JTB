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

//import java.util.ArrayList;
//import java.util.List;

/**
 * Not used by JTB.
 *
 * @author Marc Mazas
 * @version 1.4.0 : 05/2009 : MMa : adapted to JavaCC v4.2 grammar
 * @version 1.5.0 : 01/2017 : MMa : added suppress warnings javadoc ; renamed class
 */
//@SuppressWarnings("javadoc")
public class UnusedLookaheadCalc extends JavaCCGlobals {

  //ModMMa 2017/03 commented as unused and with static access
  //  static UnusedMatchInfo overlap(final List<UnusedMatchInfo> v1, final List<UnusedMatchInfo> v2) {
  //    UnusedMatchInfo m1, m2, m3;
  //    int size;
  //    boolean diff;
  //    for (int i = 0; i < v1.size(); i++) {
  //      m1 = v1.get(i);
  //      for (int j = 0; j < v2.size(); j++) {
  //        m2 = v2.get(j);
  //        size = m1.firstFreeLoc;
  //        m3 = m1;
  //        if (size > m2.firstFreeLoc) {
  //          size = m2.firstFreeLoc;
  //          m3 = m2;
  //        }
  //        if (size == 0)
  //          return null;
  //        // we wish to ignore empty expansions and the JAVACODE stuff here.
  //        diff = false;
  //        for (int k = 0; k < size; k++) {
  //          if (m1.match[k] != m2.match[k]) {
  //            diff = true;
  //            break;
  //          }
  //        }
  //        if (!diff)
  //          return m3;
  //      }
  //    }
  //    return null;
  //  }
  //
  //  static boolean javaCodeCheck(final List<UnusedMatchInfo> v) {
  //    for (int i = 0; i < v.size(); i++) {
  //      if (v.get(i).firstFreeLoc == 0) {
  //        return true;
  //      }
  //    }
  //    return false;
  //  }
  //
  //  static String image(final UnusedMatchInfo m) {
  //    String ret = "";
  //    for (int i = 0; i < m.firstFreeLoc; i++) {
  //      if (m.match[i] == 0) {
  //        ret += " <EOF>";
  //      } else {
  //        final RegularExpression_ re = rexps_of_tokens.get(new Integer(m.match[i]));
  //        if (re instanceof RStringLiteral) {
  //          ret += " \"" + add_escapes(((RStringLiteral) re).image) + "\"";
  //        } else if (re.label != null && !re.label.equals("")) {
  //          ret += " <" + re.label + ">";
  //        } else {
  //          ret += " <token of kind " + i + ">";
  //        }
  //      }
  //    }
  //    if (m.firstFreeLoc == 0) {
  //      return "";
  //    } else {
  //      return ret.substring(1);
  //    }
  //  }
  //
  //  @SuppressWarnings("unchecked")
  //  public static void choiceCalc(final Choice ch) {
  //    final int first = firstChoice(ch);
  //    // dbl[i] and dbr[i] are lists of size limited matches for choice i
  //    // of ch. dbl ignores matches with semantic lookaheads (when force_la_check
  //    // is false), while dbr ignores semantic lookahead.
  //    final List<UnusedMatchInfo>[] dbl = new ArrayList[ch.getChoices().size()];
  //    final List<UnusedMatchInfo>[] dbr = new ArrayList[ch.getChoices().size()];
  //    final int[] minLA = new int[ch.getChoices().size() - 1];
  //    final UnusedMatchInfo[] overlapInfo = new UnusedMatchInfo[ch.getChoices().size() - 1];
  //    final int[] other = new int[ch.getChoices().size() - 1];
  //    UnusedMatchInfo m;
  //    List<UnusedMatchInfo> v;
  //    boolean overlapDetected;
  //    for (int la = 1; la <= Options.getChoiceAmbiguityCheck(); la++) {
  //      UnusedMatchInfo.laLimit = la;
  //      UnusedLookaheadWalk.considerSemanticLA = !Options.getForceLaCheck();
  //      for (int i = first; i < ch.getChoices().size() - 1; i++) {
  //        UnusedLookaheadWalk.sizeLimitedMatches = new ArrayList<>();
  //        m = new UnusedMatchInfo();
  //        m.firstFreeLoc = 0;
  //        v = new ArrayList<>();
  //        v.add(m);
  //        UnusedLookaheadWalk.genFirstSet(v, ch.getChoices().get(i));
  //        dbl[i] = UnusedLookaheadWalk.sizeLimitedMatches;
  //      }
  //      UnusedLookaheadWalk.considerSemanticLA = false;
  //      for (int i = first + 1; i < ch.getChoices().size(); i++) {
  //        UnusedLookaheadWalk.sizeLimitedMatches = new ArrayList<>();
  //        m = new UnusedMatchInfo();
  //        m.firstFreeLoc = 0;
  //        v = new ArrayList<>();
  //        v.add(m);
  //        UnusedLookaheadWalk.genFirstSet(v, ch.getChoices().get(i));
  //        dbr[i] = UnusedLookaheadWalk.sizeLimitedMatches;
  //      }
  //      if (la == 1) {
  //        for (int i = first; i < ch.getChoices().size() - 1; i++) {
  //          final Expansion_ exp = ch.getChoices().get(i);
  //          if (UnusedSemanticize.emptyExpansionExists(exp)) {
  //            JavaCCErrors.warning(exp, "This choice can expand to the empty token sequence " +
  //                                      "and will therefore always be taken in favor of the choices appearing later.");
  //            break;
  //          } else if (javaCodeCheck(dbl[i])) {
  //            JavaCCErrors.warning(exp, "JAVACODE non-terminal will force this choice to be taken " +
  //                                      "in favor of the choices appearing later.");
  //            break;
  //          }
  //        }
  //      }
  //      overlapDetected = false;
  //      for (int i = first; i < ch.getChoices().size() - 1; i++) {
  //        for (int j = i + 1; j < ch.getChoices().size(); j++) {
  //          if ((m = overlap(dbl[i], dbr[j])) != null) {
  //            minLA[i] = la + 1;
  //            overlapInfo[i] = m;
  //            other[i] = j;
  //            overlapDetected = true;
  //            break;
  //          }
  //        }
  //      }
  //      if (!overlapDetected) {
  //        break;
  //      }
  //    }
  //    for (int i = first; i < ch.getChoices().size() - 1; i++) {
  //      if (explicitLA(ch.getChoices().get(i)) && !Options.getForceLaCheck()) {
  //        continue;
  //      }
  //      if (minLA[i] > Options.getChoiceAmbiguityCheck()) {
  //        JavaCCErrors.warning("Choice conflict involving two expansions at");
  //        System.err.print("         line " + (ch.getChoices().get(i)).getLine());
  //        System.err.print(", column " + (ch.getChoices().get(i)).getColumn());
  //        System.err.print(" and line " + (ch.getChoices().get(other[i])).getLine());
  //        System.err.print(", column " + (ch.getChoices().get(other[i])).getColumn());
  //        System.err.println(" respectively.");
  //        System.err.println("         A common prefix is: " + image(overlapInfo[i]));
  //        System.err.println("         Consider using a lookahead of " + minLA[i] +
  //                           " or more for earlier expansion.");
  //      } else if (minLA[i] > 1) {
  //        JavaCCErrors.warning("Choice conflict involving two expansions at");
  //        System.err.print("         line " + (ch.getChoices().get(i)).getLine());
  //        System.err.print(", column " + (ch.getChoices().get(i)).getColumn());
  //        System.err.print(" and line " + (ch.getChoices().get(other[i])).getLine());
  //        System.err.print(", column " + (ch.getChoices().get(other[i])).getColumn());
  //        System.err.println(" respectively.");
  //        System.err.println("         A common prefix is: " + image(overlapInfo[i]));
  //        System.err.println("         Consider using a lookahead of " + minLA[i] +
  //                           " for earlier expansion.");
  //      }
  //    }
  //  }
  //
  //  static boolean explicitLA(final Expansion_ exp) {
  //    if (!(exp instanceof Sequence)) {
  //      return false;
  //    }
  //    final Sequence seq = (Sequence) exp;
  //    final Object obj = seq.units.get(0);
  //    if (!(obj instanceof Lookahead)) {
  //      return false;
  //    }
  //    final Lookahead la = (Lookahead) obj;
  //    return la.isExplicit();
  //  }
  //
  //  static int firstChoice(final Choice ch) {
  //    if (Options.getForceLaCheck()) {
  //      return 0;
  //    }
  //    for (int i = 0; i < ch.getChoices().size(); i++) {
  //      if (!explicitLA(ch.getChoices().get(i))) {
  //        return i;
  //      }
  //    }
  //    return ch.getChoices().size();
  //  }
  //
  //  private static String image(final Expansion_ exp) {
  //    if (exp instanceof OneOrMore) {
  //      return "(...)+";
  //    } else if (exp instanceof ZeroOrMore) {
  //      return "(...)*";
  //    } else /* if (exp instanceof ZeroOrOne) */ {
  //      return "[...]";
  //    }
  //  }
  //
  //  public static void ebnfCalc(final Expansion_ exp, final Expansion_ nested) {
  //    // exp is one of OneOrMore, ZeroOrMore, ZeroOrOne
  //    UnusedMatchInfo m, m1 = null;
  //    List<UnusedMatchInfo> v, first, follow;
  //    int la;
  //    for (la = 1; la <= Options.getOtherAmbiguityCheck(); la++) {
  //      UnusedMatchInfo.laLimit = la;
  //      UnusedLookaheadWalk.sizeLimitedMatches = new ArrayList<>();
  //      m = new UnusedMatchInfo();
  //      m.firstFreeLoc = 0;
  //      v = new ArrayList<>();
  //      v.add(m);
  //      UnusedLookaheadWalk.considerSemanticLA = !Options.getForceLaCheck();
  //      UnusedLookaheadWalk.genFirstSet(v, nested);
  //      first = UnusedLookaheadWalk.sizeLimitedMatches;
  //      UnusedLookaheadWalk.sizeLimitedMatches = new ArrayList<>();
  //      UnusedLookaheadWalk.considerSemanticLA = false;
  //      UnusedLookaheadWalk.genFollowSet(v, exp, Expansion_.nextGenerationIndex++);
  //      follow = UnusedLookaheadWalk.sizeLimitedMatches;
  //      if (la == 1) {
  //        if (javaCodeCheck(first)) {
  //          JavaCCErrors.warning(nested,
  //                               "JAVACODE non-terminal within " + image(exp) +
  //                                       " construct will force this construct to be entered in favor of " +
  //                                       "expansions occurring after construct.");
  //        }
  //      }
  //      if ((m = overlap(first, follow)) == null) {
  //        break;
  //      }
  //      m1 = m;
  //    }
  //    if (la > Options.getOtherAmbiguityCheck()) {
  //      JavaCCErrors.warning("Choice conflict in " + image(exp) + " construct " + "at line " +
  //                           exp.getLine() + ", column " + exp.getColumn() + ".");
  //      System.err.println("         Expansion_ nested within construct and expansion following construct");
  //      System.err.println("         have common prefixes, one of which is: " + image(m1));
  //      System.err.println("         Consider using a lookahead of " + la +
  //                         " or more for nested expansion.");
  //    } else if (la > 1) {
  //      JavaCCErrors.warning("Choice conflict in " + image(exp) + " construct " + "at line " +
  //                           exp.getLine() + ", column " + exp.getColumn() + ".");
  //      System.err.println("         Expansion_ nested within construct and expansion following construct");
  //      System.err.println("         have common prefixes, one of which is: " + image(m1));
  //      System.err.println("         Consider using a lookahead of " + la + " for nested expansion.");
  //    }
  //  }
}

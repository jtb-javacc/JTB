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
import java.util.List;

/**
 * @author Marc Mazas
 * @version 1.4.0 : 05/2009 : MMa : adapted to JavaCC v4.2 grammar
 * @version 1.4.8 : 12/2014 : MMa : improved javadoc
 */
public final class LookaheadWalk {

  public static boolean         considerSemanticLA;
  public static List<MatchInfo> sizeLimitedMatches;

  /** Standard constructor */
  private LookaheadWalk() {
  }

  private static void listAppend(final List<MatchInfo> vToAppendTo, final List<MatchInfo> vToAppend) {
    for (int i = 0; i < vToAppend.size(); i++) {
      vToAppendTo.add(vToAppend.get(i));
    }
  }

  public static List<MatchInfo> genFirstSet(final List<MatchInfo> partialMatches,
                                            final Expansion_ exp) {
    if (exp instanceof RegularExpression_) {
      final List<MatchInfo> retval = new ArrayList<MatchInfo>();
      for (int i = 0; i < partialMatches.size(); i++) {
        final MatchInfo m = partialMatches.get(i);
        final MatchInfo mnew = new MatchInfo();
        for (int j = 0; j < m.firstFreeLoc; j++) {
          mnew.match[j] = m.match[j];
        }
        mnew.firstFreeLoc = m.firstFreeLoc;
        mnew.match[mnew.firstFreeLoc++] = ((RegularExpression_) exp).ordinal;
        if (mnew.firstFreeLoc == MatchInfo.laLimit) {
          sizeLimitedMatches.add(mnew);
        } else {
          retval.add(mnew);
        }
      }
      return retval;
    } else if (exp instanceof NonTerminal) {
      final NormalProduction prod = ((NonTerminal) exp).getProd();
      if (prod instanceof JavaCodeProduction_) {
        return new ArrayList<MatchInfo>();
      } else {
        return genFirstSet(partialMatches, prod.getExpansion());
      }
    } else if (exp instanceof Choice) {
      final List<MatchInfo> retval = new ArrayList<MatchInfo>();
      final Choice ch = (Choice) exp;
      for (int i = 0; i < ch.getChoices().size(); i++) {
        final List<MatchInfo> v = genFirstSet(partialMatches, ch.getChoices().get(i));
        listAppend(retval, v);
      }
      return retval;
    } else if (exp instanceof Sequence) {
      List<MatchInfo> v = partialMatches;
      final Sequence seq = (Sequence) exp;
      for (int i = 0; i < seq.units.size(); i++) {
        v = genFirstSet(v, seq.units.get(i));
        if (v.size() == 0)
          break;
      }
      return v;
    } else if (exp instanceof OneOrMore) {
      final List<MatchInfo> retval = new ArrayList<MatchInfo>();
      List<MatchInfo> v = partialMatches;
      final OneOrMore om = (OneOrMore) exp;
      while (true) {
        v = genFirstSet(v, om.expansion);
        if (v.size() == 0)
          break;
        listAppend(retval, v);
      }
      return retval;
    } else if (exp instanceof ZeroOrMore) {
      final List<MatchInfo> retval = new ArrayList<MatchInfo>();
      listAppend(retval, partialMatches);
      List<MatchInfo> v = partialMatches;
      final ZeroOrMore zm = (ZeroOrMore) exp;
      while (true) {
        v = genFirstSet(v, zm.expansion);
        if (v.size() == 0)
          break;
        listAppend(retval, v);
      }
      return retval;
    } else if (exp instanceof ZeroOrOne) {
      final List<MatchInfo> retval = new ArrayList<MatchInfo>();
      listAppend(retval, partialMatches);
      listAppend(retval, genFirstSet(partialMatches, ((ZeroOrOne) exp).expansion));
      return retval;
    } else if (exp instanceof TryBlock) {
      return genFirstSet(partialMatches, ((TryBlock) exp).exp);
    } else if (considerSemanticLA && exp instanceof Lookahead &&
               ((Lookahead) exp).getActionTokens().size() != 0) {
      return new ArrayList<MatchInfo>();
    } else {
      final List<MatchInfo> retval = new ArrayList<MatchInfo>();
      listAppend(retval, partialMatches);
      return retval;
    }
  }

  private static void listSplit(final List<MatchInfo> toSplit, final List<MatchInfo> mask,
                                final List<MatchInfo> partInMask, final List<MatchInfo> rest) {
    OuterLoop: for (int i = 0; i < toSplit.size(); i++) {
      for (int j = 0; j < mask.size(); j++) {
        if (toSplit.get(i) == mask.get(j)) {
          partInMask.add(toSplit.get(i));
          continue OuterLoop;
        }
      }
      rest.add(toSplit.get(i));
    }
  }

  public static List<MatchInfo> genFollowSet(final List<MatchInfo> partialMatches,
                                             final Expansion_ exp, final long generation) {
    if (exp.myGeneration == generation) {
      return new ArrayList<MatchInfo>();
    }
    // System.out.println("*** Parent: " + exp.parent);
    exp.myGeneration = generation;
    if (exp.parent == null) {
      final List<MatchInfo> retval = new ArrayList<MatchInfo>();
      listAppend(retval, partialMatches);
      return retval;
    } else if (exp.parent instanceof NormalProduction) {
      final List<Object> parents = ((NormalProduction) exp.parent).getParents();
      final List<MatchInfo> retval = new ArrayList<MatchInfo>();
      // System.out.println("1; gen: " + generation + "; exp: " + exp);
      for (int i = 0; i < parents.size(); i++) {
        final List<MatchInfo> v = genFollowSet(partialMatches, (Expansion_) parents.get(i),
                                               generation);
        listAppend(retval, v);
      }
      return retval;
    } else if (exp.parent instanceof Sequence) {
      final Sequence seq = (Sequence) exp.parent;
      List<MatchInfo> v = partialMatches;
      for (int i = exp.ordinal + 1; i < seq.units.size(); i++) {
        v = genFirstSet(v, seq.units.get(i));
        if (v.size() == 0)
          return v;
      }
      List<MatchInfo> v1 = new ArrayList<MatchInfo>();
      List<MatchInfo> v2 = new ArrayList<MatchInfo>();
      listSplit(v, partialMatches, v1, v2);
      if (v1.size() != 0) {
        // System.out.println("2; gen: " + generation + "; exp: " + exp);
        v1 = genFollowSet(v1, seq, generation);
      }
      if (v2.size() != 0) {
        // System.out.println("3; gen: " + generation + "; exp: " + exp);
        v2 = genFollowSet(v2, seq, Expansion_.nextGenerationIndex++);
      }
      listAppend(v2, v1);
      return v2;
    } else if (exp.parent instanceof OneOrMore || exp.parent instanceof ZeroOrMore) {
      final List<MatchInfo> moreMatches = new ArrayList<MatchInfo>();
      listAppend(moreMatches, partialMatches);
      List<MatchInfo> v = partialMatches;
      while (true) {
        v = genFirstSet(v, exp);
        if (v.size() == 0)
          break;
        listAppend(moreMatches, v);
      }
      List<MatchInfo> v1 = new ArrayList<MatchInfo>();
      List<MatchInfo> v2 = new ArrayList<MatchInfo>();
      listSplit(moreMatches, partialMatches, v1, v2);
      if (v1.size() != 0) {
        // System.out.println("4; gen: " + generation + "; exp: " + exp);
        v1 = genFollowSet(v1, (Expansion_) exp.parent, generation);
      }
      if (v2.size() != 0) {
        // System.out.println("5; gen: " + generation + "; exp: " + exp);
        v2 = genFollowSet(v2, (Expansion_) exp.parent, Expansion_.nextGenerationIndex++);
      }
      listAppend(v2, v1);
      return v2;
    } else {
      // System.out.println("6; gen: " + generation + "; exp: " + exp);
      return genFollowSet(partialMatches, (Expansion_) exp.parent, generation);
    }
  }

  public static void reInit() {
    considerSemanticLA = false;
    sizeLimitedMatches = null;
  }
}

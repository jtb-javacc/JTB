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
 * Describes one-or-more regular expressions (<foo+>).
 *
 * @author Marc Mazas, mmazas@sopragroup.com
 * @version 1.4.0 : 05/2009 : MMa : adapted to JavaCC v4.2 grammar
 */
public class RRepetitionRange extends RegularExpression_ {

  /**
   * The regular expression which is repeated one or more times.
   */
  public RegularExpression_ regexpr;
  public int                min = 0;
  public int                max = -1;
  public boolean            hasMax;

  // ModMMa : added to get rid of 'instanceof' in ExpansionTreeWalker
  public RRepetitionRange() {
    expType = EXP_TYPE.R_REPETITION_RANGE;
  }

  @Override
  public Nfa GenerateNfa(final boolean ignoreCase) {
    final List<RegularExpression_> units = new ArrayList<RegularExpression_>();
    RSequence seq;
    int i;
    for (i = 0; i < min; i++) {
      units.add(regexpr);
    }
    if (hasMax && max == -1) // Unlimited
    {
      final RZeroOrMore zoo = new RZeroOrMore();
      zoo.regexpr = regexpr;
      units.add(zoo);
    }
    while (i++ < max) {
      final RZeroOrOne zoo = new RZeroOrOne();
      zoo.regexpr = regexpr;
      units.add(zoo);
    }
    seq = new RSequence(units);
    return seq.GenerateNfa(ignoreCase);
  }
}

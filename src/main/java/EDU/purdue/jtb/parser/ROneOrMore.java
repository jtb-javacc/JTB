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
 * Describes one-or-more regular expressions (<foo+>).
 *
 * @author Marc Mazas
 * @version 1.4.0 : 05/2009 : MMa : adapted to JavaCC v4.2 grammar
 * @version 1.4.8 : 12/2014 : MMa : improved javadoc
 */
public class ROneOrMore extends RegularExpression_ {

  /**
   * The regular expression which is repeated one or more times.
   */
  public RegularExpression_ regexpr;

  //ModMMa 2017/03 commented as unused and with static access
  //  /** {@inheritDoc} */
  //  @Override
  //  public Nfa GenerateNfa(final boolean ignoreCase) {
  //    final Nfa retVal = new Nfa();
  //    final NfaState startState = retVal.startNfaState;
  //    final NfaState finalState = retVal.endNfaState;
  //
  //    final Nfa temp = regexpr.GenerateNfa(ignoreCase);
  //
  //    startState.AddMove(temp.startNfaState);
  //    temp.endNfaState.AddMove(temp.startNfaState);
  //    temp.endNfaState.AddMove(finalState);
  //
  //    return retVal;
  //  }

  /** Standard constructor */
  public ROneOrMore() {
    expType = EXP_TYPE.R_ONE_OR_MORE;
  }

  /**
   * Constructor with parameters
   *
   * @param t - the token
   * @param re - the regular expression
   */
  public ROneOrMore(final Token t, final RegularExpression_ re) {
    this();
    this.setLine(t.beginLine);
    this.setColumn(t.beginColumn);
    this.regexpr = re;
  }
}

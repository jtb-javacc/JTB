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
import java.util.Set;

/**
 * Describes lookahead rule for a particular expansion or expansion sequence (See Sequence.java). In
 * case this describes the lookahead rule for a single expansion unit, then a sequence is created
 * with this node as the first element, and the expansion unit as the second and last element.
 * 
 * @author Marc Mazas
 * @version 1.4.0 : 05/2009 : MMa : adapted to JavaCC v4.2 grammar
 * @version 1.4.8 : 12/2014 : MMa : improved javadoc
 */
public class Lookahead extends Expansion_ {

  /**
   * Contains the list of tokens that make up the semantic lookahead if any. If this node represents
   * a different kind of lookahead (other than semantic lookahead), then this list contains nothing.
   * If this list contains something, then it is the boolean expression that forms the semantic
   * lookahead. In this case, the following fields "amount" and "la_expansion" are ignored.
   */
  private final List<Token> action_tokens = new ArrayList<Token>();

  /**
   * The lookahead amount. Its default value essentially gives us infinite lookahead.
   */
  private int               amount        = Integer.MAX_VALUE;

  /**
   * The expansion used to determine whether or not to choose the corresponding parse option. This
   * expansion is parsed up to "amount" tokens of lookahead or until a complete match for it is
   * found. Usually, this is the same as the expansion to be parsed.
   */
  private Expansion_        la_expansion;

  /**
   * Is set to true if this is an explicit lookahead specification.
   */
  private boolean           isExplicit;

  /** Standard constructor */
  public Lookahead() {
    expType = EXP_TYPE.LOOKAHEAD;
  }

  /** {@inheritDoc} */
  @Override
  public StringBuilder dump(final int indent, final Set<Object> alreadyDumped) {
    final StringBuilder sb = super.dump(indent, alreadyDumped).append(isExplicit ? " explicit"
                                                                                : " implicit");
    if (alreadyDumped.contains(this))
      return sb;
    alreadyDumped.add(this);
    sb.append(EOL).append(la_expansion.dump(indent + 1, alreadyDumped));
    return sb;
  }

  /**
   * @return the action_tokens
   */
  public final List<Token> getActionTokens() {
    return action_tokens;
  }

  /**
   * @param am - the amount to set
   */
  public final void setAmount(final int am) {
    amount = am;
  }

  /**
   * @return the amount
   */
  public final int getAmount() {
    return amount;
  }

  /**
   * @param laexp - the la_expansion to set
   */
  public final void setLaExpansion(final Expansion_ laexp) {
    la_expansion = laexp;
  }

  /**
   * @return the la_expansion
   */
  public final Expansion_ getLaExpansion() {
    return la_expansion;
  }

  /**
   * @param ise - the isExplicit to set
   */
  public final void setExplicit(final boolean ise) {
    isExplicit = ise;
  }

  /**
   * @return the isExplicit
   */
  public final boolean isExplicit() {
    return isExplicit;
  }
}

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
 * Describes non terminals.
 * 
 * @author Marc Mazas
 * @version 1.4.0 : 05/2009 : MMa : adapted to JavaCC v4.2 grammar
 */
public class NonTerminal extends Expansion_ {

  /**
   * The LHS to which the return value of the non-terminal is assigned. In case there is no LHS,
   * then the vector remains empty.
   */
  private List<Token>      lhsTokens       = new ArrayList<Token>();
  /**
   * The name of the non-terminal.
   */
  private String           name;
  /**
   * The list of all tokens in the argument list.
   */
  private List<Token>      argument_tokens = new ArrayList<Token>();
  /**
   * The production this non-terminal corresponds to.
   */
  private NormalProduction prod;

  /** {@inheritDoc} */
  @Override
  public StringBuilder dump(final int indent, final Set<Object> alreadyDumped) {
    final StringBuilder value = super.dump(indent, alreadyDumped).append(' ').append(name);
    return value;
  }

  /**
   * @param lhsTokens - the lhsTokens to set
   */
  public final void setLhsTokens(final List<Token> lt) {
    lhsTokens = lt;
  }

  /**
   * @return the lhsTokens
   */
  public final List<Token> getLhsTokens() {
    return lhsTokens;
  }

  /**
   * @param ref - the name to set
   */
  public final void setName(final String nm) {
    name = nm;
  }

  /**
   * @return the name
   */
  public final String getName() {
    return name;
  }

  /**
   * @param argument_tokens - the argument_tokens to set
   */
  public final void setArgumentTokens(final List<Token> tk) {
    argument_tokens = tk;
  }

  /**
   * @return the argument_tokens
   */
  public final List<Token> getArgumentTokens() {
    return argument_tokens;
  }

  /**
   * @param prod - the prod to set
   */
  public final NormalProduction setProd(final NormalProduction pr) {
    return prod = pr;
  }

  /**
   * @return the prod
   */
  public final NormalProduction getProd() {
    return prod;
  }
}

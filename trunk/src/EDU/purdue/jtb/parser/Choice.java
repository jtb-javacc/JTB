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
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Describes expansions where one of many choices is taken (c1 | c2 | ...).
 * 
 * @author Marc Mazas
 * @version 1.4.0 : 05/2009 : MMa : adapted to JavaCC v4.2 grammar
 * @version 1.4.8 : 12/2014 : MMa : improved javadoc
 */
public class Choice extends Expansion_ {

  /**
   * The list of choices of this expansion unit. Each List component will narrow to ExpansionUnit.
   */
  private List<Expansion_> choices = new ArrayList<Expansion_>();

  /** Standard constructor */
  public Choice() {
    expType = EXP_TYPE.CHOICE;
  }

  /**
   * @param token - the {@link Token}
   */
  public Choice(final Token token) {
    this();
    this.setLine(token.beginLine);
    this.setColumn(token.beginColumn);

  }

  /**
   * @param expansion - the {@link Expansion_}
   */
  public Choice(final Expansion_ expansion) {
    this();
    this.setLine(expansion.getLine());
    this.setColumn(expansion.getColumn());
    this.getChoices().add(expansion);

  }

  /**
   * @param ch - the choices to set
   */
  public final void setChoices(final List<Expansion_> ch) {
    choices = ch;
  }

  /**
   * @return the choices
   */
  public final List<Expansion_> getChoices() {
    return choices;
  }

  /** {@inheritDoc} */
  @Override
  public StringBuilder dump(final int indent, final Set<Object> alreadyDumped) {
    final StringBuilder sb = super.dump(indent, alreadyDumped);
    if (alreadyDumped.contains(this))
      return sb;
    alreadyDumped.add(this);
    for (final Iterator<Expansion_> it = getChoices().iterator(); it.hasNext();) {
      final Expansion_ next = it.next();
      sb.append(EOL).append(next.dump(indent + 1, alreadyDumped));
    }
    return sb;
  }
}

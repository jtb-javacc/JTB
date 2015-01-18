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
 * Describes expansions that are sequences of expansion
 * units.  (c1 c2 ...)
 *
 * @author Marc Mazas, mmazas@sopragroup.com
 * @version 1.4.0 : 05/2009 : MMa : adapted to JavaCC v4.2 grammar
 */
public class Sequence extends Expansion_ {

  /**
   * The list of units in this expansion sequence.  Each
   * List component will narrow to Expansion_.
   */
  public List<Expansion_> units = new ArrayList<Expansion_>();

  public Sequence() {
    // ModMMa : added to get rid of 'instanceof' in ExpansionTreeWalker
    expType = EXP_TYPE.SEQUENCE;
  }

  public Sequence(final Token token, final Lookahead lookahead) {
    // ModMMa : added to get rid of 'instanceof' in ExpansionTreeWalker
    this();
    this.setLine(token.beginLine);
    this.setColumn(token.beginColumn);
    this.units.add(lookahead);
  }

  @Override
  public StringBuffer dump(final int indent, final Set<Object> alreadyDumped) {
    if (alreadyDumped.contains(this)) {
      return super.dump(0, alreadyDumped).insert(0, '[').append(']').insert(0, dumpPrefix(indent));
    }
    alreadyDumped.add(this);
    final StringBuffer sb = super.dump(indent, alreadyDumped);
    for (final Iterator<Expansion_> it = units.iterator(); it.hasNext();) {
      final Expansion_ next = it.next();
      sb.append(eol).append(next.dump(indent + 1, alreadyDumped));
    }
    return sb;
  }
}

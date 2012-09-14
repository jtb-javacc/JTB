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

import java.util.List;
import java.util.Set;

/**
 * Describes expansions of the form "try {...} ...".
 *
 * @author Marc Mazas, mmazas@sopragroup.com
 * @version 1.4.0 : 05/2009 : MMa : adapted to JavaCC v4.2 grammar
 */
public class TryBlock extends Expansion_ {

  /**
   * The expansion contained within the try block.
   */
  public Expansion_        exp;
  /**
   * The types of each catch block.  Each list entry is itself a
   * list which in turn contains tokens as entries.
   */
  public List<List<Token>> types;
  /**
   * The exception identifiers of each catch block.  Each list entry
   * is a token.
   */
  public List<Token>       ids;
  /**
   * The block part of each catch block.  Each list entry is itself a
   * list which in turn contains tokens as entries.
   */
  public List<List<Token>> catchblks;
  /**
   * The block part of the finally block.  Each list entry is a token.
   * If there is no finally block, this is null.
   */
  public List<Token>       finallyblk;

  // ModMMa : added to get rid of 'instanceof' in ExpansionTreeWalker
  public TryBlock() {
    expType = EXP_TYPE.TRY_BLOCK;
  }

  @Override
  public StringBuffer dump(final int indent, final Set<Object> alreadyDumped) {
    final StringBuffer sb = super.dump(indent, alreadyDumped);
    if (alreadyDumped.contains(this))
      return sb;
    alreadyDumped.add(this);
    sb.append(eol).append(exp.dump(indent + 1, alreadyDumped));
    return sb;
  }
}

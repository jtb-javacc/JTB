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

import java.util.Iterator;

import EDU.purdue.jtb.parser.Expansion_.EXP_TYPE;

/**
 * A set of routines that walk down the Expansion_ tree in various ways.
 * 
 * @author Marc Mazas
 * @version 1.4.0 : 05/2009 : MMa : adapted to JavaCC v4.2 grammar
 */
public final class ExpansionTreeWalker {

  private ExpansionTreeWalker() {
  }

  /**
   * Visits the nodes of the tree rooted at "node" in pre-order. i.e., it executes opObj.action
   * first and then visits the children.
   */
  static void preOrderWalk(final Expansion_ node, final TreeWalkerOp opObj) {
    opObj.action(node);
    if (opObj.goDeeper(node)) {
      if (node.expType == EXP_TYPE.CHOICE) {
        for (final Iterator<Expansion_> it = ((Choice) node).getChoices().iterator(); it.hasNext();) {
          preOrderWalk(it.next(), opObj);
        }
      } else if (node.expType == EXP_TYPE.SEQUENCE) {
        for (final Iterator<Expansion_> it = ((Sequence) node).units.iterator(); it.hasNext();) {
          preOrderWalk(it.next(), opObj);
        }
      } else if (node.expType == EXP_TYPE.ONE_OR_MORE) {
        preOrderWalk(((OneOrMore) node).expansion, opObj);
      } else if (node.expType == EXP_TYPE.ZERO_OR_MORE) {
        preOrderWalk(((ZeroOrMore) node).expansion, opObj);
      } else if (node.expType == EXP_TYPE.ZERO_OR_ONE) {
        preOrderWalk(((ZeroOrOne) node).expansion, opObj);
      } else if (node.expType == EXP_TYPE.LOOKAHEAD) {
        final Expansion_ nested_e = ((Lookahead) node).getLaExpansion();
        if (!(nested_e.expType == EXP_TYPE.SEQUENCE && (((Sequence) nested_e).units.get(0)) == node)) {
          preOrderWalk(nested_e, opObj);
        }
      } else if (node.expType == EXP_TYPE.TRY_BLOCK) {
        preOrderWalk(((TryBlock) node).exp, opObj);
      } else if (node.expType == EXP_TYPE.R_CHOICE) {
        for (final Iterator<RegularExpression_> it = ((RChoice) node).getChoices().iterator(); it.hasNext();) {
          preOrderWalk(it.next(), opObj);
        }
      } else if (node.expType == EXP_TYPE.R_SEQUENCE) {
        for (final Iterator<RegularExpression_> it = ((RSequence) node).units.iterator(); it.hasNext();) {
          preOrderWalk(it.next(), opObj);
        }
      } else if (node.expType == EXP_TYPE.R_ONE_OR_MORE) {
        preOrderWalk(((ROneOrMore) node).regexpr, opObj);
      } else if (node.expType == EXP_TYPE.R_ZERO_OR_MORE) {
        preOrderWalk(((RZeroOrMore) node).regexpr, opObj);
      } else if (node.expType == EXP_TYPE.R_ZERO_OR_ONE) {
        preOrderWalk(((RZeroOrOne) node).regexpr, opObj);
      } else if (node.expType == EXP_TYPE.R_REPETITION_RANGE) {
        preOrderWalk(((RRepetitionRange) node).regexpr, opObj);
      }
    }
  }

  /**
   * Visits the nodes of the tree rooted at "node" in post-order. i.e., it visits the children first
   * and then executes opObj.action.
   */
  static void postOrderWalk(final Expansion_ node, final TreeWalkerOp opObj) {
    if (opObj.goDeeper(node)) {
      if (node.expType == EXP_TYPE.CHOICE) {
        for (final Iterator<Expansion_> it = ((Choice) node).getChoices().iterator(); it.hasNext();) {
          postOrderWalk(it.next(), opObj);
        }
      } else if (node.expType == EXP_TYPE.SEQUENCE) {
        for (final Iterator<Expansion_> it = ((Sequence) node).units.iterator(); it.hasNext();) {
          postOrderWalk(it.next(), opObj);
        }
      } else if (node.expType == EXP_TYPE.ONE_OR_MORE) {
        postOrderWalk(((OneOrMore) node).expansion, opObj);
      } else if (node.expType == EXP_TYPE.ZERO_OR_MORE) {
        postOrderWalk(((ZeroOrMore) node).expansion, opObj);
      } else if (node.expType == EXP_TYPE.ZERO_OR_ONE) {
        postOrderWalk(((ZeroOrOne) node).expansion, opObj);
      } else if (node.expType == EXP_TYPE.LOOKAHEAD) {
        final Expansion_ nested_e = ((Lookahead) node).getLaExpansion();
        if (!(nested_e.expType == EXP_TYPE.SEQUENCE && (((Sequence) nested_e).units.get(0)) == node)) {
          postOrderWalk(nested_e, opObj);
        }
      } else if (node.expType == EXP_TYPE.TRY_BLOCK) {
        postOrderWalk(((TryBlock) node).exp, opObj);
      } else if (node.expType == EXP_TYPE.R_CHOICE) {
        for (final Iterator<RegularExpression_> it = ((RChoice) node).getChoices().iterator(); it.hasNext();) {
          postOrderWalk(it.next(), opObj);
        }
      } else if (node.expType == EXP_TYPE.R_SEQUENCE) {
        for (final Iterator<RegularExpression_> it = ((RSequence) node).units.iterator(); it.hasNext();) {
          postOrderWalk(it.next(), opObj);
        }
      } else if (node.expType == EXP_TYPE.R_ONE_OR_MORE) {
        postOrderWalk(((ROneOrMore) node).regexpr, opObj);
      } else if (node.expType == EXP_TYPE.R_ZERO_OR_MORE) {
        postOrderWalk(((RZeroOrMore) node).regexpr, opObj);
      } else if (node.expType == EXP_TYPE.R_ZERO_OR_ONE) {
        postOrderWalk(((RZeroOrOne) node).regexpr, opObj);
      } else if (node.expType == EXP_TYPE.R_REPETITION_RANGE) {
        postOrderWalk(((RRepetitionRange) node).regexpr, opObj);
      }
    }
    opObj.action(node);
  }
}

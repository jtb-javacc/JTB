/**
 * Copyright (c) 2004,2005 UCLA Compilers Group.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  Redistributions of source code must retain the above copyright
 *  notice, this list of conditions and the following disclaimer.
 *
 *  Redistributions in binary form must reproduce the above copyright
 *  notice, this list of conditions and the following disclaimer in the
 *  documentation and/or other materials provided with the distribution.
 *
 *  Neither UCLA nor the names of its contributors may be used to endorse
 *  or promote products derived from this software without specific prior
 *  written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 **/

/*
 * All files in the distribution of JTB, The Java Tree Builder are
 * Copyright 1997, 1998, 1999 by the Purdue Research Foundation of Purdue
 * University.  All rights reserved.
 *
 * Redistribution and use in source and binary forms are permitted
 * provided that this entire copyright notice is duplicated in all
 * such copies, and that any documentation, announcements, and
 * other materials related to such distribution and use acknowledge
 * that the software was developed at Purdue University, West Lafayette,
 * Indiana by Kevin Tao and Jens Palsberg.  No charge may be made
 * for copies, derivations, or distributions of this material
 * without the express written consent of the copyright holder.
 * Neither the name of the University nor the name of the author
 * may be used to endorse or promote products derived from this
 * material without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND WITHOUT ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, WITHOUT LIMITATION, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR ANY PARTICULAR PURPOSE.
 */

package EDU.purdue.jtb.visitor;

import EDU.purdue.jtb.misc.Messages;
import EDU.purdue.jtb.syntaxtree.Expansion;
import EDU.purdue.jtb.syntaxtree.ExpansionChoices;
import EDU.purdue.jtb.syntaxtree.ExpansionUnit;
import EDU.purdue.jtb.syntaxtree.NodeListOptional;
import EDU.purdue.jtb.syntaxtree.NodeSequence;

/**
 * The ExpansionUnitTypeCounter visitor counts the different types of the ExpansionUnits found in an
 * ExpansionChoices subtree, without descending further into the tree than the first ExpansionUnit
 * level.<br>
 *
 * @author Marc Mazas, mmazas@sopragroup.com
 * @version 1.4.0 : 05-08/2009 : MMa : adapted to JavaCC v4.2 grammar and JDK 1.5
 */
public class ExpansionUnitTypeCounter extends DepthFirstVoidVisitor {

  /** Number of lookaheads counter */
  private int numLookaheads = 0;
  /** Number of blocks counter */
  private int numBlocks     = 0;
  /** Number of options counter */
  private int numBrackets   = 0;
  /** Number of trys counter */
  private int numTrys       = 0;
  /** Number of BNF modifiers counter */
  private int numModifiers  = 0;
  /** Number of terms counter */
  private int numTerms      = 0;

  /**
   * Visits a {@link ExpansionChoices} node, whose children are the following :
   * <p>
   * f0 -> Expansion()<br>
   * f1 -> ( #0 "|" #1 Expansion() )*<br>
   *
   * @param n the node to visit
   */
  @Override
  public void visit(final ExpansionChoices n) {
    // f0 -> Expansion()
    n.f0.accept(this);
    // f1 -> ( #0 "|" #1 Expansion() )*
    final NodeListOptional nlo = n.f1;
    if (nlo.present()) {
      for (int i = 0; i < nlo.size(); i++) {
        final NodeSequence seq = (NodeSequence) nlo.elementAt(i);
        seq.elementAt(0).accept(this);
        seq.elementAt(1).accept(this);
      }
    }
  }

  /**
   * Visits a {@link Expansion} node, whose children are the following :
   * <p>
   * f0 -> ( #0 "LOOKAHEAD" #1 "(" #2 LocalLookahead() #3 ")" )?<br>
   * f1 -> ( ExpansionUnit() )+<br>
   *
   * @param n the node to visit
   */
  @Override
  public void visit(final Expansion n) {
    // f0 -> ( #0 "LOOKAHEAD" #1 "(" #2 LocalLookahead() #3 ")" )?
    // don't visit
    // f1 -> ( ExpansionUnit() )+
    for (int i = 0; i < n.f1.size(); i++) {
      n.f1.elementAt(i).accept(this);
    }
  }

  /**
   * Visits the {@link ExpansionUnit}<br>
   * f0 -> . %0 #0 "LOOKAHEAD" #1 "(" #2 LocalLookahead() #3 ")"<br>
   * .. .. | %1 Block()<br>
   * .. .. | %2 #0 "[" #1 ExpansionChoices() #2 "]"<br>
   * .. .. | %3 ExpansionUnitInTCF()<br>
   * .. .. | %4 #0 [ $0 PrimaryExpression() $1 "=" ]<br>
   * .. .. . .. #1 ( &0 $0 Identifier() $1 Arguments()<br>
   * .. .. . .. .. | &1 $0 RegularExpression()<br>
   * .. .. . .. .. . .. $1 [ £0 "." £1 < IDENTIFIER > ] )<br>
   * .. .. | %5 #0 "(" #1 ExpansionChoices() #2 ")"<br>
   * .. .. . .. #3 ( &0 "+"<br>
   * .. .. . .. .. | &1 "*"<br>
   * .. .. . .. .. | &2 "?" )?<br>
   *
   * @param n the node to visit
   */
  @Override
  public void visit(final ExpansionUnit n) {
    switch (n.f0.which) {
      case 0:
        ++numLookaheads;
        break;
      case 1:
        ++numBlocks;
        break;
      case 2:
        ++numBrackets;
        break;
      case 3:
        ++numTrys;
        break;
      case 4:
        ++numTerms;
        break;
      case 5:
        ++numModifiers;
        break;
      default:
        Messages.hardErr("which = " + String.valueOf(n.f0.which));
    }
  }

  /**
   * Resets the counters.
   */
  public void reset() {
    numLookaheads = numBlocks = numBrackets = numTrys = numTerms = numModifiers = 0;
  }

  /**
   * @return the number of goupings (brackets, trys and modifiers) plus the number of terms
   */
  public int getNumNormals() {
    return numBrackets + numTrys + numModifiers + numTerms;
  }

  /**
   * @return the number of goupings (brackets, trys and modifiers)
   */
  public int getNumGroupings() {
    return numBrackets + numTrys + numModifiers;
  }

  /**
   * @return the number of lookaheads
   */
  public int getNumLookaheads() {
    return numLookaheads;
  }

  /**
   * @return the number of blocks
   */
  public int getNumBlocks() {
    return numBlocks;
  }

  /**
   * @return the number of brackets
   */
  public int getNumBrackets() {
    return numBrackets;
  }

  /**
   * @return the number of trys
   */
  public int getNumTrys() {
    return numTrys;
  }

  /**
   * @return the number of terms
   */
  public int getNumTerms() {
    return numTerms;
  }

  /**
   * @return the number of modifiers
   */
  public int getNumModifiers() {
    return numModifiers;
  }
}

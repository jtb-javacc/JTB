/*
 * Copyright (c) 2006, Sun Microsystems, Inc. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided
 * that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this list of conditions and the
 * following disclaimer. * Redistributions in binary form must reproduce the above copyright notice, this list
 * of conditions and the following disclaimer in the documentation and/or other materials provided with the
 * distribution. * Neither the name of the Sun Microsystems, Inc. nor the names of its contributors may be
 * used to endorse or promote products derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 * PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package EDU.purdue.jtb.parser;

import java.util.ArrayList;
import java.util.List;

/**
 * Describes regular expressions which are choices from from among included regular expressions.
 *
 * @author Marc Mazas
 * @version 1.4.0 : 05/2009 : MMa : adapted to JavaCC v4.2 grammar
 * @version 1.4.8 : 12/2014 : MMa : improved javadoc
 */
public class RChoice extends RegularExpression_ {
  
  /**
   * The list of choices of this regular expression. Each list component will narrow to RegularExpression_.
   */
  private List<RegularExpression_> choices = new ArrayList<>();
  
  /** Standard constructor */
  public RChoice() {
    expType = EXP_TYPE.R_CHOICE;
  }
  
  /**
   * @param ch - the choices to set
   */
  public final void setChoices(final List<RegularExpression_> ch) {
    choices = ch;
  }
  
  /**
   * @return the choices
   */
  public final List<RegularExpression_> getChoices() {
    return choices;
  }
  
  // ModMMa 2017/03 commented as unused and with static access
  // /** {@inheritDoc} */
  // @Override
  // public Nfa GenerateNfa(final boolean ignoreCase) {
  // CompressCharLists();
  // if (getChoices().size() == 1)
  // return getChoices().get(0).GenerateNfa(ignoreCase);
  // final Nfa retVal = new Nfa();
  // final NfaState startState = retVal.startNfaState;
  // final NfaState finalState = retVal.endNfaState;
  // for (int i = 0; i < getChoices().size(); i++) {
  // Nfa temp;
  // final RegularExpression_ curRE = getChoices().get(i);
  // temp = curRE.GenerateNfa(ignoreCase);
  // startState.AddMove(temp.startNfaState);
  // temp.endNfaState.AddMove(finalState);
  // }
  // return retVal;
  // }
  
  // ModMMa 2017/03 commented as unused and with static access
  // /**
  // * Compresses the choices and the character lists.
  // */
  // void CompressCharLists() {
  // CompressChoices(); // Unroll nested choices
  // RegularExpression_ curRE;
  // RCharacterList curCharList = null;
  // for (int i = 0; i < getChoices().size(); i++) {
  // curRE = getChoices().get(i);
  // while (curRE instanceof RJustName)
  // curRE = ((RJustName) curRE).regexpr;
  // if (curRE instanceof RStringLiteral && ((RStringLiteral) curRE).image.length() == 1)
  // getChoices().set(i, curRE = new RCharacterList(((RStringLiteral) curRE).image.charAt(0)));
  // if (curRE instanceof RCharacterList) {
  // if (((RCharacterList) curRE).negated_list)
  // ((RCharacterList) curRE).RemoveNegation();
  // final List<Object> tmp = ((RCharacterList) curRE).descriptors;
  // if (curCharList == null)
  // getChoices().set(i, curRE = curCharList = new RCharacterList());
  // else
  // getChoices().remove(i--);
  // for (int j = tmp.size(); j-- > 0;)
  // curCharList.descriptors.add(tmp.get(j));
  // }
  // }
  // }
  
  /**
   * Compresses (unrolls) the choices.
   */
  void CompressChoices() {
    RegularExpression_ curRE;
    for (int i = 0; i < getChoices().size(); i++) {
      curRE = getChoices().get(i);
      while (curRE instanceof RJustName) {
        curRE = ((RJustName) curRE).regexpr;
      }
      if (curRE instanceof RChoice) {
        getChoices().remove(i--);
        for (int j = ((RChoice) curRE).getChoices().size(); j-- > 0;) {
          getChoices().add(((RChoice) curRE).getChoices().get(j));
        }
      }
    }
  }
  
  /**
   * Checks whether Regular Expression Choices can be matched.
   */
  public void CheckUnmatchability() {
    RegularExpression_ curRE;
    @SuppressWarnings("unused") int numStrings = 0;
    for (int i = 0; i < getChoices().size(); i++) {
      if (!(curRE = getChoices().get(i)).private_rexp && (// curRE instanceof RJustName &&
      curRE.ordinal > 0) && (curRE.ordinal < ordinal)
          && (LexGen.lexStates[curRE.ordinal] == LexGen.lexStates[ordinal])) {
        if (label != null) {
          JavaCCErrors.warning(this,
              "Regular Expression choice : " + curRE.label + " can never be matched as : " + label);
        } else {
          JavaCCErrors.warning(this, "Regular Expression choice : " + curRE.label
              + " can never be matched as token of kind : " + ordinal);
        }
      }
      if (!curRE.private_rexp && (curRE instanceof RStringLiteral)) {
        numStrings++;
      }
    }
  }
}

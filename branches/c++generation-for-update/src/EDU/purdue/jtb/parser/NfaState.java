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

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;

/**
 * The state of a Non-deterministic Finite Automaton.
 * 
 * @author Marc Mazas
 * @version 1.4.0 : 05/2009 : MMa : adapted to JavaCC v4.2 grammar
 * @version 1.4.8 : 12/2014 : MMa : improved javadoc
 */
public class NfaState {

  public static boolean                     unicodeWarningGiven          = false;
  public static int                         generatedStates              = 0;

  private static int                        idCnt                        = 0;
  private static int                        lohiByteCnt;
  private static int                        dummyStateIndex              = -1;
  private static boolean                    done;
  private static boolean                    mark[];
  private static boolean                    stateDone[];
  private static List<NfaState>             allStates                    = new ArrayList<NfaState>();
  private static List<NfaState>             indexedAllStates             = new ArrayList<NfaState>();
  private static List<NfaState>             nonAsciiTableForMethod       = new ArrayList<NfaState>();
  private static Map<String, NfaState>      equivStatesTable             = new Hashtable<String, NfaState>();
  private static Hashtable<String, int[]>   allNextStates                = new Hashtable<String, int[]>();
  private static Map<String, Integer>       lohiByteTab                  = new Hashtable<String, Integer>();
  private static Hashtable<String, Integer> stateNameForComposite        = new Hashtable<String, Integer>();
  private static Hashtable<String, int[]>   compositeStateTable          = new Hashtable<String, int[]>();
  private static Map<String, String>        stateBlockTable              = new Hashtable<String, String>();
  private static Hashtable<String, int[]>   stateSetsToFix               = new Hashtable<String, int[]>();
  private static boolean                    jjCheckNAddStatesUnaryNeeded = false;
  private static boolean                    jjCheckNAddStatesDualNeeded  = false;

  public static void ReInit() {
    generatedStates = 0;
    idCnt = 0;
    dummyStateIndex = -1;
    done = false;
    mark = null;
    stateDone = null;

    allStates.clear();
    indexedAllStates.clear();
    equivStatesTable.clear();
    allNextStates.clear();
    compositeStateTable.clear();
    stateBlockTable.clear();
    stateNameForComposite.clear();
    stateSetsToFix.clear();
  }

  long[]                  asciiMoves         = new long[2];
  char[]                  charMoves          = null;
  private char[]          rangeMoves         = null;
  NfaState                next               = null;
  private NfaState        stateForCase;
  Vector<NfaState>        epsilonMoves       = new Vector<NfaState>();
  private String          epsilonMovesString;
  private NfaState[]      epsilonMoveArray;

  private final int       id;
  int                     stateName          = -1;
  int                     kind               = Integer.MAX_VALUE;
  private int             lookingFor;
  private int             usefulEpsilonMoves = 0;
  int                     inNextOf;
  private int             lexState;
  private int             nonAsciiMethod     = -1;
  private int             kindToPrint        = Integer.MAX_VALUE;
  boolean                 dummy              = false;
  private boolean         isComposite        = false;
  private int[]           compositeStates    = null;
  boolean                 isFinal            = false;
  private Vector<Integer> loByteVec;
  private int[]           nonAsciiMoveIndices;
  private int             round              = 0;
  private int             onlyChar           = 0;
  private char            matchSingleChar;

  /** Standard constructor */
  NfaState() {
    id = idCnt++;
    allStates.add(this);
    lexState = LexGen.lexStateIndex;
    lookingFor = LexGen.curKind;
  }

  NfaState CreateClone() {
    final NfaState retVal = new NfaState();

    retVal.isFinal = isFinal;
    retVal.kind = kind;
    retVal.lookingFor = lookingFor;
    retVal.lexState = lexState;
    retVal.inNextOf = inNextOf;

    retVal.MergeMoves(this);

    return retVal;
  }

  static void InsertInOrder(final List<NfaState> v, final NfaState s) {
    int j;

    for (j = 0; j < v.size(); j++)
      if (v.get(j).id > s.id)
        break;
      else if (v.get(j).id == s.id)
        return;

    v.add(j, s);
  }

  private static char[] ExpandCharArr(final char[] oldArr, final int incr) {
    final char[] ret = new char[oldArr.length + incr];
    System.arraycopy(oldArr, 0, ret, 0, oldArr.length);
    return ret;
  }

  void AddMove(final NfaState newState) {
    if (!epsilonMoves.contains(newState))
      InsertInOrder(epsilonMoves, newState);
  }

  private final void AddASCIIMove(final char c) {
    asciiMoves[c / 64] |= (1L << (c % 64));
  }

  void AddChar(final char c) {
    onlyChar++;
    matchSingleChar = c;
    int i;
    char temp;
    char temp1;

    if (c < 128) // ASCII char
    {
      AddASCIIMove(c);
      return;
    }

    if (charMoves == null)
      charMoves = new char[10];

    int len = charMoves.length;

    if (charMoves[len - 1] != 0) {
      charMoves = ExpandCharArr(charMoves, 10);
      len += 10;
    }

    for (i = 0; i < len; i++)
      if (charMoves[i] == 0 || charMoves[i] > c)
        break;

    if (!unicodeWarningGiven && c > 0xff && !Options.getJavaUnicodeEscape() &&
        !Options.getUserCharStream()) {
      unicodeWarningGiven = true;
      JavaCCErrors.warning(LexGen.curRE,
                           "Non-ASCII characters used in regular expression.\n"
                               + "Please make sure you use the correct Reader when you create the parser, "
                               + "one that can handle your character set.");
    }

    temp = charMoves[i];
    charMoves[i] = c;

    for (i++; i < len; i++) {
      if (temp == 0)
        break;

      temp1 = charMoves[i];
      charMoves[i] = temp;
      temp = temp1;
    }
  }

  void AddRange(final char left, final char right) {
    char lft = left;
    onlyChar = 2;
    int i;
    char tempLeft1, tempLeft2, tempRight1, tempRight2;

    if (lft < 128) {
      if (right < 128) {
        for (; lft <= right; lft++)
          AddASCIIMove(lft);

        return;
      }

      for (; lft < 128; lft++)
        AddASCIIMove(lft);
    }

    if (!unicodeWarningGiven && (lft > 0xff || right > 0xff) && !Options.getJavaUnicodeEscape() &&
        !Options.getUserCharStream()) {
      unicodeWarningGiven = true;
      JavaCCErrors.warning(LexGen.curRE,
                           "Non-ASCII characters used in regular expression.\n"
                               + "Please make sure you use the correct Reader when you create the parser, "
                               + "one that can handle your character set.");
    }

    if (rangeMoves == null)
      rangeMoves = new char[20];

    int len = rangeMoves.length;

    if (rangeMoves[len - 1] != 0) {
      rangeMoves = ExpandCharArr(rangeMoves, 20);
      len += 20;
    }

    for (i = 0; i < len; i += 2)
      if (rangeMoves[i] == 0 || (rangeMoves[i] > lft) ||
          ((rangeMoves[i] == lft) && (rangeMoves[i + 1] > right)))
        break;

    tempLeft1 = rangeMoves[i];
    tempRight1 = rangeMoves[i + 1];
    rangeMoves[i] = lft;
    rangeMoves[i + 1] = right;

    for (i += 2; i < len; i += 2) {
      if (tempLeft1 == 0)
        break;

      tempLeft2 = rangeMoves[i];
      tempRight2 = rangeMoves[i + 1];
      rangeMoves[i] = tempLeft1;
      rangeMoves[i + 1] = tempRight1;
      tempLeft1 = tempLeft2;
      tempRight1 = tempRight2;
    }
  }

  // From hereon down all the functions are used for code generation

  private static boolean EqualCharArr(final char[] arr1, final char[] arr2) {
    if (arr1 == arr2)
      return true;

    if (arr1 != null && arr2 != null && arr1.length == arr2.length) {
      for (int i = arr1.length; i-- > 0;)
        if (arr1[i] != arr2[i])
          return false;

      return true;
    }

    return false;
  }

  private boolean closureDone = false;

  /**
   * This function computes the closure and also updates the kind so that any time there is a move
   * to this state, it can go on epsilon to a new state in the epsilon moves that might have a lower
   * kind of token number for the same length.
   */

  private void EpsilonClosure() {
    int i = 0;

    if (closureDone || mark[id])
      return;

    mark[id] = true;

    // Recursively do closure
    for (i = 0; i < epsilonMoves.size(); i++)
      (epsilonMoves.get(i)).EpsilonClosure();
    final Enumeration<NfaState> e = epsilonMoves.elements();
    while (e.hasMoreElements()) {
      final NfaState tmp = e.nextElement();
      for (i = 0; i < tmp.epsilonMoves.size(); i++) {
        final NfaState tmp1 = tmp.epsilonMoves.get(i);
        if (tmp1.UsefulState() && !epsilonMoves.contains(tmp1)) {
          InsertInOrder(epsilonMoves, tmp1);
          done = false;
        }
      }

      if (kind > tmp.kind)
        kind = tmp.kind;
    }

    if (HasTransitions() && !epsilonMoves.contains(this))
      InsertInOrder(epsilonMoves, this);
  }

  private boolean UsefulState() {
    return isFinal || HasTransitions();
  }

  public boolean HasTransitions() {
    return (asciiMoves[0] != 0L || asciiMoves[1] != 0L || (charMoves != null && charMoves[0] != 0) || (rangeMoves != null && rangeMoves[0] != 0));
  }

  void MergeMoves(final NfaState other) {
    // Warning : This function does not merge epsilon moves
    if (asciiMoves == other.asciiMoves) {
      JavaCCErrors.semantic_error("Bug in JavaCC : Please send "
                                  + "a report along with the input that caused this. Thank you.");
      throw new Error();
    }

    asciiMoves[0] = asciiMoves[0] | other.asciiMoves[0];
    asciiMoves[1] = asciiMoves[1] | other.asciiMoves[1];

    if (other.charMoves != null) {
      if (charMoves == null)
        charMoves = other.charMoves;
      else {
        final char[] tmpCharMoves = new char[charMoves.length + other.charMoves.length];
        System.arraycopy(charMoves, 0, tmpCharMoves, 0, charMoves.length);
        charMoves = tmpCharMoves;

        for (int i = 0; i < other.charMoves.length; i++)
          AddChar(other.charMoves[i]);
      }
    }

    if (other.rangeMoves != null) {
      if (rangeMoves == null)
        rangeMoves = other.rangeMoves;
      else {
        final char[] tmpRangeMoves = new char[rangeMoves.length + other.rangeMoves.length];
        System.arraycopy(rangeMoves, 0, tmpRangeMoves, 0, rangeMoves.length);
        rangeMoves = tmpRangeMoves;
        for (int i = 0; i < other.rangeMoves.length; i += 2)
          AddRange(other.rangeMoves[i], other.rangeMoves[i + 1]);
      }
    }

    if (other.kind < kind)
      kind = other.kind;

    if (other.kindToPrint < kindToPrint)
      kindToPrint = other.kindToPrint;

    isFinal |= other.isFinal;
  }

  static NfaState CreateEquivState(final List<NfaState> states) {
    final NfaState newState = (states.get(0)).CreateClone();
    newState.next = new NfaState();

    InsertInOrder(newState.next.epsilonMoves, (states.get(0)).next);

    for (int i = 1; i < states.size(); i++) {
      final NfaState tmp2 = (states.get(i));

      if (tmp2.kind < newState.kind)
        newState.kind = tmp2.kind;

      newState.isFinal |= tmp2.isFinal;

      InsertInOrder(newState.next.epsilonMoves, tmp2.next);
    }

    return newState;
  }

  private NfaState GetEquivalentRunTimeState() {
    Outer: for (int i = allStates.size(); i-- > 0;) {
      final NfaState other = allStates.get(i);

      if (this != other && other.stateName != -1 && kindToPrint == other.kindToPrint &&
          asciiMoves[0] == other.asciiMoves[0] && asciiMoves[1] == other.asciiMoves[1] &&
          EqualCharArr(charMoves, other.charMoves) && EqualCharArr(rangeMoves, other.rangeMoves)) {
        if (next == other.next)
          return other;
        else if (next != null && other.next != null) {
          if (next.epsilonMoves.size() == other.next.epsilonMoves.size()) {
            for (int j = 0; j < next.epsilonMoves.size(); j++)
              if (next.epsilonMoves.get(j) != other.next.epsilonMoves.get(j))
                continue Outer;

            return other;
          }
        }
      }
    }

    return null;
  }

  // generates code (without outputting it) and returns the name used.
  void GenerateCode() {
    if (stateName != -1)
      return;

    if (next != null) {
      next.GenerateCode();
      if (next.kind != Integer.MAX_VALUE)
        kindToPrint = next.kind;
    }

    if (stateName == -1 && HasTransitions()) {
      final NfaState tmp = GetEquivalentRunTimeState();

      if (tmp != null) {
        stateName = tmp.stateName;
        //????
        //tmp.inNextOf += inNextOf;
        //????
        dummy = true;
        return;
      }

      stateName = generatedStates++;
      indexedAllStates.add(this);
      GenerateNextStatesCode();
    }
  }

  public static void ComputeClosures() {
    for (int i = allStates.size(); i-- > 0;) {
      final NfaState tmp = allStates.get(i);
      if (!tmp.closureDone)
        tmp.OptimizeEpsilonMoves(true);
    }
    for (int i = 0; i < allStates.size(); i++) {
      final NfaState tmp = allStates.get(i);
      if (!tmp.closureDone)
        tmp.OptimizeEpsilonMoves(false);
    }
    for (int i = 0; i < allStates.size(); i++) {
      final NfaState tmp = allStates.get(i);
      tmp.epsilonMoveArray = new NfaState[tmp.epsilonMoves.size()];
      tmp.epsilonMoves.copyInto(tmp.epsilonMoveArray);
    }
  }

  void OptimizeEpsilonMoves(final boolean optReqd) {
    int i;
    final int ass = allStates.size();

    // First do epsilon closure
    done = false;
    while (!done) {
      if (mark == null || mark.length < ass)
        mark = new boolean[ass];

      for (i = ass; i-- > 0;)
        mark[i] = false;

      done = true;
      EpsilonClosure();
    }
    for (i = allStates.size(); i-- > 0;)
      (allStates.get(i)).closureDone = mark[(allStates.get(i)).id];
    // Warning : The following piece of code is just an optimization.
    // in case of trouble, just remove this piece.

    boolean somethingOptimized = true;

    NfaState newState = null;
    NfaState tmp1, tmp2;
    int j;
    List<NfaState> equivStates = null;
    while (somethingOptimized) {
      somethingOptimized = false;
      for (i = 0; optReqd && i < epsilonMoves.size(); i++) {
        if ((tmp1 = epsilonMoves.get(i)).HasTransitions()) {
          for (j = i + 1; j < epsilonMoves.size(); j++) {
            if ((tmp2 = epsilonMoves.get(j)).HasTransitions() &&
                (tmp1.asciiMoves[0] == tmp2.asciiMoves[0] &&
                 tmp1.asciiMoves[1] == tmp2.asciiMoves[1] &&
                 EqualCharArr(tmp1.charMoves, tmp2.charMoves) && EqualCharArr(tmp1.rangeMoves,
                                                                              tmp2.rangeMoves))) {
              if (equivStates == null) {
                equivStates = new ArrayList<NfaState>();
                equivStates.add(tmp1);
              }

              InsertInOrder(equivStates, tmp2);
              epsilonMoves.removeElementAt(j--);
            }
          }
        }

        if (equivStates != null) {
          somethingOptimized = true;
          final int ess = equivStates.size();
          final StringBuilder tmpsb = new StringBuilder(8 * ess);
          for (int l = 0; l < ess; l++)
            tmpsb.append(String.valueOf((equivStates.get(l)).id)).append(", ");
          final String tmp = tmpsb.toString();
          if ((newState = equivStatesTable.get(tmp)) == null) {
            newState = CreateEquivState(equivStates);
            equivStatesTable.put(tmp, newState);
          }

          epsilonMoves.removeElementAt(i--);
          epsilonMoves.add(newState);
          equivStates = null;
          newState = null;
        }
      }

      for (i = 0; i < epsilonMoves.size(); i++) {
        //if ((tmp1 = (NfaState)epsilonMoves.elementAt(i)).next == null)
        //continue;
        tmp1 = epsilonMoves.get(i);
        for (j = i + 1; j < epsilonMoves.size(); j++) {
          tmp2 = epsilonMoves.get(j);
          if (tmp1.next == tmp2.next) {
            if (newState == null) {
              newState = tmp1.CreateClone();
              newState.next = tmp1.next;
              somethingOptimized = true;
            }

            newState.MergeMoves(tmp2);
            epsilonMoves.removeElementAt(j--);
          }
        }

        if (newState != null) {
          epsilonMoves.removeElementAt(i--);
          epsilonMoves.add(newState);
          newState = null;
        }
      }
    }

    // End Warning

    // Generate an array of states for epsilon moves (not vector)
    if (epsilonMoves.size() > 0) {
      for (i = 0; i < epsilonMoves.size(); i++)
        // Since we are doing a closure, just epsilon moves are unncessary
        if ((epsilonMoves.get(i)).HasTransitions())
          usefulEpsilonMoves++;
        else
          epsilonMoves.removeElementAt(i--);
    }
  }

  void GenerateNextStatesCode() {
    if (next.usefulEpsilonMoves > 0)
      next.GetEpsilonMovesString();
  }

  String GetEpsilonMovesString() {
    final int[] stateNames = new int[usefulEpsilonMoves];
    int cnt = 0;

    if (epsilonMovesString != null)
      return epsilonMovesString;

    if (usefulEpsilonMoves > 0) {
      NfaState tempState;
      epsilonMovesString = "{ ";
      for (int i = 0; i < epsilonMoves.size(); i++) {
        if ((tempState = epsilonMoves.get(i)).HasTransitions()) {
          if (tempState.stateName == -1)
            tempState.GenerateCode();
          (indexedAllStates.get(tempState.stateName)).inNextOf++;
          stateNames[cnt] = tempState.stateName;
          epsilonMovesString += tempState.stateName + ", ";
          if (cnt++ > 0 && cnt % 16 == 0)
            epsilonMovesString += "\n";
        }
      }

      epsilonMovesString += "};";
    }

    usefulEpsilonMoves = cnt;
    if (epsilonMovesString != null && allNextStates.get(epsilonMovesString) == null) {
      final int[] statesToPut = new int[usefulEpsilonMoves];

      System.arraycopy(stateNames, 0, statesToPut, 0, cnt);
      allNextStates.put(epsilonMovesString, statesToPut);
    }

    return epsilonMovesString;
  }

  public static boolean CanStartNfaUsingAscii(final char c) {
    if (c >= 128)
      throw new Error("JavaCC Bug: Please send mail to sankar@cs.stanford.edu");

    final String s = LexGen.initialState.GetEpsilonMovesString();

    if (s == null || s.equals("null;"))
      return false;

    final int[] states = allNextStates.get(s);

    for (int i = 0; i < states.length; i++) {
      final NfaState tmp = indexedAllStates.get(states[i]);

      if ((tmp.asciiMoves[c / 64] & (1L << c % 64)) != 0L)
        return true;
    }

    return false;
  }

  final boolean CanMoveUsingChar(final char c) {
    int i;

    if (onlyChar == 1)
      return c == matchSingleChar;

    if (c < 128)
      return ((asciiMoves[c / 64] & (1L << c % 64)) != 0L);

    // Just check directly if there is a move for this char
    if (charMoves != null && charMoves[0] != 0) {
      for (i = 0; i < charMoves.length; i++) {
        if (c == charMoves[i])
          return true;
        else if (c < charMoves[i] || charMoves[i] == 0)
          break;
      }
    }

    // For ranges, iterate thru the table to see if the current char
    // is in some range
    if (rangeMoves != null && rangeMoves[0] != 0)
      for (i = 0; i < rangeMoves.length; i += 2)
        if (c >= rangeMoves[i] && c <= rangeMoves[i + 1])
          return true;
        else if (c < rangeMoves[i] || rangeMoves[i] == 0)
          break;

    //return (nextForNegatedList != null);
    return false;
  }

  public int getFirstValidPos(final String s, final int j, final int len) {
    int i = j;
    if (onlyChar == 1) {
      final char c = matchSingleChar;
      while (c != s.charAt(i) && ++i < len)
        ;
      return i;
    }

    do {
      if (CanMoveUsingChar(s.charAt(i)))
        return i;
    }
    while (++i < len);

    return i;
  }

  public int MoveFrom(final char c, final List<NfaState> newStates) {
    if (CanMoveUsingChar(c)) {
      for (int i = next.epsilonMoves.size(); i-- > 0;)
        InsertInOrder(newStates, next.epsilonMoves.get(i));

      return kindToPrint;
    }

    return Integer.MAX_VALUE;
  }

  public static int MoveFromSet(final char c, final List<NfaState> states,
                                final List<NfaState> newStates) {
    int tmp;
    int retVal = Integer.MAX_VALUE;

    for (int i = states.size(); i-- > 0;)
      if (retVal > (tmp = (states.get(i)).MoveFrom(c, newStates)))
        retVal = tmp;

    return retVal;
  }

  public static int moveFromSetForRegEx(final char c, final NfaState[] states,
                                        final NfaState[] newStates, final int round) {
    int start = 0;
    final int sz = states.length;

    for (int i = 0; i < sz; i++) {
      NfaState tmp1, tmp2;

      if ((tmp1 = states[i]) == null)
        break;

      if (tmp1.CanMoveUsingChar(c)) {
        if (tmp1.kindToPrint != Integer.MAX_VALUE) {
          newStates[start] = null;
          return 1;
        }

        final NfaState[] v = tmp1.next.epsilonMoveArray;
        for (int j = v.length; j-- > 0;) {
          if ((tmp2 = v[j]).round != round) {
            tmp2.round = round;
            newStates[start++] = tmp2;
          }
        }
      }
    }

    newStates[start] = null;
    return Integer.MAX_VALUE;
  }

  static List<String> allBitVectors = new ArrayList<String>();

  /* This function generates the bit vectors of low and hi bytes for common
     bit vectors and returns those that are not common with anything (in
     loBytes) and returns an array of indices that can be used to generate
     the function names for char matching using the common bit vectors.
     It also generates code to match a char with the common bit vectors.
     (Need a better comment). */

  static int[]        tmpIndices    = new int[512];           // 2 * 256

  void GenerateNonAsciiMoves(final PrintWriter out) {
    int i = 0, j = 0;
    char hiByte;
    int cnt = 0;
    final long[][] loBytes = new long[256][4];

    if ((charMoves == null || charMoves[0] == 0) && (rangeMoves == null || rangeMoves[0] == 0))
      return;

    if (charMoves != null) {
      for (i = 0; i < charMoves.length; i++) {
        if (charMoves[i] == 0)
          break;

        hiByte = (char) (charMoves[i] >> 8);
        loBytes[hiByte][(charMoves[i] & 0xff) / 64] |= (1L << ((charMoves[i] & 0xff) % 64));
      }
    }

    if (rangeMoves != null) {
      for (i = 0; i < rangeMoves.length; i += 2) {
        if (rangeMoves[i] == 0)
          break;

        char c, r;

        r = (char) (rangeMoves[i + 1] & 0xff);
        hiByte = (char) (rangeMoves[i] >> 8);

        if (hiByte == (char) (rangeMoves[i + 1] >> 8)) {
          for (c = (char) (rangeMoves[i] & 0xff); c <= r; c++)
            loBytes[hiByte][c / 64] |= (1L << (c % 64));

          continue;
        }

        for (c = (char) (rangeMoves[i] & 0xff); c <= 0xff; c++)
          loBytes[hiByte][c / 64] |= (1L << (c % 64));

        while (++hiByte < (char) (rangeMoves[i + 1] >> 8)) {
          loBytes[hiByte][0] |= 0xffffffffffffffffL;
          loBytes[hiByte][1] |= 0xffffffffffffffffL;
          loBytes[hiByte][2] |= 0xffffffffffffffffL;
          loBytes[hiByte][3] |= 0xffffffffffffffffL;
        }

        for (c = 0; c <= r; c++)
          loBytes[hiByte][c / 64] |= (1L << (c % 64));
      }
    }

    long[] common = null;
    final boolean[] fini = new boolean[256];

    for (i = 0; i <= 255; i++) {
      if (fini[i] ||
          (fini[i] = loBytes[i][0] == 0 && loBytes[i][1] == 0 && loBytes[i][2] == 0 &&
                     loBytes[i][3] == 0))
        continue;

      for (j = i + 1; j < 256; j++) {
        if (fini[j])
          continue;

        if (loBytes[i][0] == loBytes[j][0] && loBytes[i][1] == loBytes[j][1] &&
            loBytes[i][2] == loBytes[j][2] && loBytes[i][3] == loBytes[j][3]) {
          fini[j] = true;
          if (common == null) {
            fini[i] = true;
            common = new long[4];
            common[i / 64] |= (1L << (i % 64));
          }

          common[j / 64] |= (1L << (j % 64));
        }
      }

      if (common != null) {
        Integer ind;
        String tmp;

        tmp = "{\n   0x" + Long.toHexString(common[0]) + "L, " + "0x" +
              Long.toHexString(common[1]) + "L, " + "0x" + Long.toHexString(common[2]) + "L, " +
              "0x" + Long.toHexString(common[3]) + "L\n};";
        if ((ind = lohiByteTab.get(tmp)) == null) {
          allBitVectors.add(tmp);

          if (!AllBitsSet(tmp))
            out.println("static final long[] jjbitVec" + lohiByteCnt + " = " + tmp);
          lohiByteTab.put(tmp, ind = new Integer(lohiByteCnt++));
        }

        tmpIndices[cnt++] = ind.intValue();

        tmp = "{\n   0x" + Long.toHexString(loBytes[i][0]) + "L, " + "0x" +
              Long.toHexString(loBytes[i][1]) + "L, " + "0x" + Long.toHexString(loBytes[i][2]) +
              "L, " + "0x" + Long.toHexString(loBytes[i][3]) + "L\n};";
        if ((ind = lohiByteTab.get(tmp)) == null) {
          allBitVectors.add(tmp);

          if (!AllBitsSet(tmp))
            out.println("static final long[] jjbitVec" + lohiByteCnt + " = " + tmp);
          lohiByteTab.put(tmp, ind = new Integer(lohiByteCnt++));
        }

        tmpIndices[cnt++] = ind.intValue();

        common = null;
      }
    }

    nonAsciiMoveIndices = new int[cnt];
    System.arraycopy(tmpIndices, 0, nonAsciiMoveIndices, 0, cnt);

    /*
          System.out.println("state : " + stateName + " cnt : " + cnt);
          while (cnt > 0)
          {
             System.out.print(nonAsciiMoveIndices[cnt - 1] + ", " + nonAsciiMoveIndices[cnt - 2] + ", ");
             cnt -= 2;
          }
          System.out.println("");
    */

    for (i = 0; i < 256; i++) {
      if (fini[i])
        loBytes[i] = null;
      else {
        //System.out.print(i + ", ");
        String tmp;
        Integer ind;

        tmp = "{\n   0x" + Long.toHexString(loBytes[i][0]) + "L, " + "0x" +
              Long.toHexString(loBytes[i][1]) + "L, " + "0x" + Long.toHexString(loBytes[i][2]) +
              "L, " + "0x" + Long.toHexString(loBytes[i][3]) + "L\n};";

        if ((ind = lohiByteTab.get(tmp)) == null) {
          allBitVectors.add(tmp);

          if (!AllBitsSet(tmp))
            out.println("static final long[] jjbitVec" + lohiByteCnt + " = " + tmp);
          lohiByteTab.put(tmp, ind = new Integer(lohiByteCnt++));
        }

        if (loByteVec == null)
          loByteVec = new Vector<Integer>();

        loByteVec.add(new Integer(i));
        loByteVec.add(ind);
      }
    }
    //System.out.println("");
    UpdateDuplicateNonAsciiMoves();
  }

  private void UpdateDuplicateNonAsciiMoves() {
    for (int i = 0; i < nonAsciiTableForMethod.size(); i++) {
      final NfaState tmp = nonAsciiTableForMethod.get(i);
      if (EqualLoByteVectors(loByteVec, tmp.loByteVec) &&
          EqualNonAsciiMoveIndices(nonAsciiMoveIndices, tmp.nonAsciiMoveIndices)) {
        nonAsciiMethod = i;
        return;
      }
    }

    nonAsciiMethod = nonAsciiTableForMethod.size();
    nonAsciiTableForMethod.add(this);
  }

  private static boolean EqualLoByteVectors(final List<Integer> vec1, final List<Integer> vec2) {
    if (vec1 == null || vec2 == null)
      return false;

    if (vec1 == vec2)
      return true;

    if (vec1.size() != vec2.size())
      return false;

    for (int i = 0; i < vec1.size(); i++) {
      if ((vec1.get(i)).intValue() != (vec2.get(i)).intValue())
        return false;
    }

    return true;
  }

  private static boolean EqualNonAsciiMoveIndices(final int[] moves1, final int[] moves2) {
    if (moves1 == moves2)
      return true;

    if (moves1 == null || moves2 == null)
      return false;

    if (moves1.length != moves2.length)
      return false;

    for (int i = 0; i < moves1.length; i++) {
      if (moves1[i] != moves2[i])
        return false;
    }

    return true;
  }

  static String allBits = "{\n   0xffffffffffffffffL, " + "0xffffffffffffffffL, "
                          + "0xffffffffffffffffL, " + "0xffffffffffffffffL\n};";

  static boolean AllBitsSet(final String bitVec) {
    return bitVec.equals(allBits);
  }

  static int AddStartStateSet(final String stateSetString) {
    return AddCompositeStateSet(stateSetString, true);
  }

  private static int AddCompositeStateSet(final String stateSetString, final boolean starts) {
    Integer stateNameToReturn;

    if ((stateNameToReturn = stateNameForComposite.get(stateSetString)) != null)
      return stateNameToReturn.intValue();

    int toRet = 0;
    final int[] nameSet = allNextStates.get(stateSetString);

    if (!starts)
      stateBlockTable.put(stateSetString, stateSetString);

    if (nameSet == null)
      throw new Error(
                      "JavaCC Bug: Please send mail to sankar@cs.stanford.edu; nameSet null for : " +
                          stateSetString);

    if (nameSet.length == 1) {
      stateNameToReturn = new Integer(nameSet[0]);
      stateNameForComposite.put(stateSetString, stateNameToReturn);
      return nameSet[0];
    }

    for (int i = 0; i < nameSet.length; i++) {
      if (nameSet[i] == -1)
        continue;

      final NfaState st = indexedAllStates.get(nameSet[i]);
      st.isComposite = true;
      st.compositeStates = nameSet;
    }

    while (toRet < nameSet.length &&
           (starts && (indexedAllStates.get(nameSet[toRet])).inNextOf > 1))
      toRet++;

    final Enumeration<String> e = compositeStateTable.keys();
    String s;
    while (e.hasMoreElements()) {
      s = e.nextElement();
      if (!s.equals(stateSetString) && Intersect(stateSetString, s)) {
        final int[] other = compositeStateTable.get(s);

        while (toRet < nameSet.length &&
               ((starts && (indexedAllStates.get(nameSet[toRet])).inNextOf > 1) || ElemOccurs(nameSet[toRet],
                                                                                              other) >= 0))
          toRet++;
      }
    }

    int tmp;

    if (toRet >= nameSet.length) {
      if (dummyStateIndex == -1)
        tmp = dummyStateIndex = generatedStates;
      else
        tmp = ++dummyStateIndex;
    } else
      tmp = nameSet[toRet];

    stateNameToReturn = new Integer(tmp);
    stateNameForComposite.put(stateSetString, stateNameToReturn);
    compositeStateTable.put(stateSetString, nameSet);

    return tmp;
  }

  private static int StateNameForComposite(final String stateSetString) {
    return (stateNameForComposite.get(stateSetString)).intValue();
  }

  static int InitStateName() {
    final String s = LexGen.initialState.GetEpsilonMovesString();

    if (LexGen.initialState.usefulEpsilonMoves != 0)
      return StateNameForComposite(s);
    return -1;
  }

  // public void GenerateInitMoves(final PrintWriter out) {
  public void GenerateInitMoves() {
    GetEpsilonMovesString();

    if (epsilonMovesString == null)
      epsilonMovesString = "null;";

    AddStartStateSet(epsilonMovesString);
  }

  static Map<String, int[]> tableToDump     = new Hashtable<String, int[]>();
  static List<int[]>        orderedStateSet = new ArrayList<int[]>();
  static int                lastIndex       = 0;

  private static int[] GetStateSetIndicesForUse(final String arrayString) {
    int[] ret;
    final int[] set = allNextStates.get(arrayString);
    if ((ret = tableToDump.get(arrayString)) == null) {
      ret = new int[2];
      ret[0] = lastIndex;
      ret[1] = lastIndex + set.length - 1;
      lastIndex += set.length;
      tableToDump.put(arrayString, ret);
      orderedStateSet.add(set);
    }

    return ret;
  }

  public static void DumpStateSets(final PrintWriter out) {
    int cnt = 0;

    out.print("static final int[] jjnextStates = {");
    for (int i = 0; i < orderedStateSet.size(); i++) {
      final int[] set = orderedStateSet.get(i);
      for (int j = 0; j < set.length; j++) {
        if (cnt++ % 16 == 0)
          out.print("\n   ");

        out.print(set[j] + ", ");
      }
    }

    out.println("\n};");
  }

  static String GetStateSetString(final int[] states) {
    String retVal = "{ ";
    for (int i = 0; i < states.length;) {
      retVal += states[i] + ", ";

      if (i++ > 0 && i % 16 == 0)
        retVal += "\n";
    }

    retVal += "};";
    allNextStates.put(retVal, states);
    return retVal;
  }

  static String GetStateSetString(final List<NfaState> states) {
    if (states == null || states.size() == 0)
      return "null;";

    final int[] set = new int[states.size()];
    String retVal = "{ ";
    for (int i = 0; i < states.size();) {
      int k;
      retVal += (k = (states.get(i)).stateName) + ", ";
      set[i] = k;

      if (i++ > 0 && i % 16 == 0)
        retVal += "\n";
    }

    retVal += "};";
    allNextStates.put(retVal, set);
    return retVal;
  }

  static int NumberOfBitsSet(final long l) {
    int ret = 0;
    for (int i = 0; i < 63; i++)
      if (((l >> i) & 1L) != 0L)
        ret++;

    return ret;
  }

  static int OnlyOneBitSet(final long l) {
    int oneSeen = -1;
    for (int i = 0; i < 64; i++)
      if (((l >> i) & 1L) != 0L) {
        if (oneSeen >= 0)
          return -1;
        oneSeen = i;
      }

    return oneSeen;
  }

  private static int ElemOccurs(final int elem, final int[] arr) {
    for (int i = arr.length; i-- > 0;)
      if (arr[i] == elem)
        return i;

    return -1;
  }

  @SuppressWarnings("unused")
  private boolean FindCommonBlocks() {
    if (next == null || next.usefulEpsilonMoves <= 1)
      return false;

    if (stateDone == null)
      stateDone = new boolean[generatedStates];

    final String set = next.epsilonMovesString;

    final int[] nameSet = allNextStates.get(set);

    if (nameSet.length <= 2 || compositeStateTable.get(set) != null)
      return false;

    int i;
    final int freq[] = new int[nameSet.length];
    final boolean live[] = new boolean[nameSet.length];
    final int[] count = new int[allNextStates.size()];

    for (i = 0; i < nameSet.length; i++) {
      if (nameSet[i] != -1) {
        live[i] = !stateDone[nameSet[i]];
        if (live[i])
          count[0]++;
      }
    }

    int j, blockLen = 0, commonFreq = 0;
    Enumeration<String> e = allNextStates.keys();
    boolean needUpdate;

    while (e.hasMoreElements()) {
      final int[] tmpSet = allNextStates.get(e.nextElement());
      if (tmpSet == nameSet)
        continue;

      needUpdate = false;
      for (j = 0; j < nameSet.length; j++) {
        if (nameSet[j] == -1)
          continue;

        if (live[j] && ElemOccurs(nameSet[j], tmpSet) >= 0) {
          if (!needUpdate) {
            needUpdate = true;
            commonFreq++;
          }

          count[freq[j]]--;
          count[commonFreq]++;
          freq[j] = commonFreq;
        }
      }

      if (needUpdate) {
        int foundFreq = -1;
        blockLen = 0;

        for (j = 0; j <= commonFreq; j++)
          if (count[j] > blockLen) {
            foundFreq = j;
            blockLen = count[j];
          }

        if (blockLen <= 1)
          return false;

        for (j = 0; j < nameSet.length; j++)
          if (nameSet[j] != -1 && freq[j] != foundFreq) {
            live[j] = false;
            count[freq[j]]--;
          }
      }
    }

    if (blockLen <= 1)
      return false;

    final int[] commonBlock = new int[blockLen];
    int cnt = 0;
    //System.out.println("Common Block for " + set + " :");
    for (i = 0; i < nameSet.length; i++) {
      if (live[i]) {
        if ((indexedAllStates.get(nameSet[i])).isComposite)
          return false;

        stateDone[nameSet[i]] = true;
        commonBlock[cnt++] = nameSet[i];
        //System.out.print(nameSet[i] + ", ");
      }
    }

    //System.out.println("");

    final String s = GetStateSetString(commonBlock);
    e = allNextStates.keys();

    Outer: while (e.hasMoreElements()) {
      int at;
      boolean firstOne = true;
      String stringToFix;
      final int[] setToFix = allNextStates.get(stringToFix = e.nextElement());

      if (setToFix == commonBlock)
        continue;

      for (int k = 0; k < cnt; k++) {
        if ((at = ElemOccurs(commonBlock[k], setToFix)) >= 0) {
          if (!firstOne)
            setToFix[at] = -1;
          firstOne = false;
        } else
          continue Outer;
      }

      if (stateSetsToFix.get(stringToFix) == null)
        stateSetsToFix.put(stringToFix, setToFix);
    }

    next.usefulEpsilonMoves -= blockLen - 1;
    AddCompositeStateSet(s, false);
    return true;
  }

  @SuppressWarnings("unused")
  private boolean CheckNextOccursTogether() {
    if (next == null || next.usefulEpsilonMoves <= 1)
      return true;

    final String set = next.epsilonMovesString;

    final int[] nameSet = allNextStates.get(set);

    if (nameSet.length == 1 || compositeStateTable.get(set) != null ||
        stateSetsToFix.get(set) != null)
      return false;

    int i;
    final Hashtable<String, int[]> occursIn = new Hashtable<String, int[]>();
    final NfaState tmp = allStates.get(nameSet[0]);

    for (i = 1; i < nameSet.length; i++) {
      final NfaState tmp1 = allStates.get(nameSet[i]);

      if (tmp.inNextOf != tmp1.inNextOf)
        return false;
    }

    int isPresent, j;
    Enumeration<String> e = allNextStates.keys();
    while (e.hasMoreElements()) {
      String s;
      final int[] tmpSet = allNextStates.get(s = e.nextElement());

      if (tmpSet == nameSet)
        continue;

      isPresent = 0;
      for (j = 0; j < nameSet.length; j++) {
        if (ElemOccurs(nameSet[j], tmpSet) >= 0)
          isPresent++;
        else if (isPresent > 0)
          return false;
      }

      if (isPresent == j) {
        if (tmpSet.length > nameSet.length)
          occursIn.put(s, tmpSet);

        //May not need. But safe.
        if (compositeStateTable.get(s) != null || stateSetsToFix.get(s) != null)
          return false;
      } else if (isPresent != 0)
        return false;
    }

    e = occursIn.keys();
    while (e.hasMoreElements()) {
      String s;
      final int[] setToFix = occursIn.get(s = e.nextElement());

      if (stateSetsToFix.get(s) == null)
        stateSetsToFix.put(s, setToFix);

      for (int k = 0; k < setToFix.length; k++)
        if (ElemOccurs(setToFix[k], nameSet) > 0) // Not >= since need the first one (0)
          setToFix[k] = -1;
    }

    next.usefulEpsilonMoves = 1;
    AddCompositeStateSet(next.epsilonMovesString, false);
    return true;
  }

  private static void FixStateSets() {
    final Map<String, int[]> fixedSets = new Hashtable<String, int[]>();
    final Enumeration<String> e = stateSetsToFix.keys();
    final int[] tmp = new int[generatedStates];
    int i;

    while (e.hasMoreElements()) {
      String s;
      final int[] toFix = stateSetsToFix.get(s = e.nextElement());
      int cnt = 0;

      //System.out.print("Fixing : ");
      for (i = 0; i < toFix.length; i++) {
        //System.out.print(toFix[i] + ", ");
        if (toFix[i] != -1)
          tmp[cnt++] = toFix[i];
      }

      final int[] fixed = new int[cnt];
      System.arraycopy(tmp, 0, fixed, 0, cnt);
      fixedSets.put(s, fixed);
      allNextStates.put(s, fixed);
      //System.out.println(" as " + GetStateSetString(fixed));
    }

    for (i = 0; i < allStates.size(); i++) {
      final NfaState tmpState = allStates.get(i);
      int[] newSet;

      if (tmpState.next == null || tmpState.next.usefulEpsilonMoves == 0)
        continue;

      /*if (compositeStateTable.get(tmpState.next.epsilonMovesString) != null)
         tmpState.next.usefulEpsilonMoves = 1;
      else*/if ((newSet = fixedSets.get(tmpState.next.epsilonMovesString)) != null)
        tmpState.FixNextStates(newSet);
    }
  }

  private final void FixNextStates(final int[] newSet) {
    next.usefulEpsilonMoves = newSet.length;
    //next.epsilonMovesString = GetStateSetString(newSet);
  }

  private static boolean Intersect(final String set1, final String set2) {
    if (set1 == null || set2 == null)
      return false;

    final int[] nameSet1 = allNextStates.get(set1);
    final int[] nameSet2 = allNextStates.get(set2);

    if (nameSet1 == null || nameSet2 == null)
      return false;

    if (nameSet1 == nameSet2)
      return true;

    for (int i = nameSet1.length; i-- > 0;)
      for (int j = nameSet2.length; j-- > 0;)
        if (nameSet1[i] == nameSet2[j])
          return true;

    return false;
  }

  private static void DumpHeadForCase(final PrintWriter out, final int byteNum) {
    if (byteNum == 0)
      out.println("         long l = 1L << curChar;");
    else if (byteNum == 1)
      out.println("         long l = 1L << (curChar & 077);");

    else {
      if (Options.getJavaUnicodeEscape() || unicodeWarningGiven) {
        out.println("         int hiByte = (curChar >> 8);");
        out.println("         int i1 = hiByte >> 6;");
        out.println("         long l1 = 1L << (hiByte & 077);");
      }

      out.println("         int i2 = (curChar & 0xff) >> 6;");
      out.println("         long l2 = 1L << (curChar & 077);");
    }

    //out.println("         MatchLoop: do");
    out.println("         do");
    out.println("         {");

    out.println("            switch(jjstateSet[--i])");
    out.println("            {");
  }

  private static Vector<List<NfaState>> PartitionStatesSetForAscii(final int[] states,
                                                                   final int byteNum) {
    final int[] cardinalities = new int[states.length];
    final Vector<NfaState> original = new Vector<NfaState>();
    final Vector<List<NfaState>> partition = new Vector<List<NfaState>>();
    NfaState tmp;

    original.setSize(states.length);
    int cnt = 0;
    for (int i = 0; i < states.length; i++) {
      tmp = allStates.get(states[i]);

      if (tmp.asciiMoves[byteNum] != 0L) {
        int j;
        final int p = NumberOfBitsSet(tmp.asciiMoves[byteNum]);

        for (j = 0; j < i; j++)
          if (cardinalities[j] <= p)
            break;

        for (int k = i; k > j; k--)
          cardinalities[k] = cardinalities[k - 1];

        cardinalities[j] = p;

        original.insertElementAt(tmp, j);
        cnt++;
      }
    }

    original.setSize(cnt);
    while (original.size() > 0) {
      tmp = original.get(0);
      original.removeElement(tmp);

      long bitVec = tmp.asciiMoves[byteNum];
      final List<NfaState> subSet = new ArrayList<NfaState>();
      subSet.add(tmp);
      for (int j = 0; j < original.size(); j++) {
        final NfaState tmp1 = original.get(j);
        if ((tmp1.asciiMoves[byteNum] & bitVec) == 0L) {
          bitVec |= tmp1.asciiMoves[byteNum];
          subSet.add(tmp1);
          original.removeElementAt(j--);
        }
      }

      partition.add(subSet);
    }

    return partition;
  }

  private String PrintNoBreak(final PrintWriter out, final int byteNum, final boolean[] dumped) {
    if (inNextOf != 1)
      throw new Error("JavaCC Bug: Please send mail to sankar@cs.stanford.edu");

    dumped[stateName] = true;

    if (byteNum >= 0) {
      if (asciiMoves[byteNum] != 0L) {
        out.println("               case " + stateName + ":");
        DumpAsciiMoveForCompositeState(out, byteNum, false);
        return "";
      }
    } else if (nonAsciiMethod != -1) {
      out.println("               case " + stateName + ":");
      DumpNonAsciiMoveForCompositeState(out);
      return "";
    }

    return ("               case " + stateName + ":\n");
  }

  private static void DumpCompositeStatesAsciiMoves(final PrintWriter out, final String key,
                                                    final int byteNum, final boolean[] dumped) {
    int i;

    final int[] nameSet = allNextStates.get(key);

    if (nameSet.length == 1 || dumped[StateNameForComposite(key)])
      return;

    NfaState toBePrinted = null;
    int neededStates = 0;
    NfaState tmp;
    NfaState stateForCase = null;
    String toPrint = "";
    final boolean stateBlock = (stateBlockTable.get(key) != null);

    for (i = 0; i < nameSet.length; i++) {
      tmp = allStates.get(nameSet[i]);

      if (tmp.asciiMoves[byteNum] != 0L) {
        if (neededStates++ == 1)
          break;
        else
          toBePrinted = tmp;
      } else
        dumped[tmp.stateName] = true;

      if (tmp.stateForCase != null) {
        if (stateForCase != null)
          throw new Error("JavaCC Bug: Please send mail to sankar@cs.stanford.edu : ");

        stateForCase = tmp.stateForCase;
      }
    }

    if (stateForCase != null)
      toPrint = stateForCase.PrintNoBreak(out, byteNum, dumped);

    if (neededStates == 0) {
      if (stateForCase != null && toPrint.equals(""))
        out.println("                  break;");
      return;
    }

    if (neededStates == 1) {
      //if (byteNum == 1)
      //System.out.println(toBePrinted.stateName + " is the only state for "
      //+ key + " ; and key is : " + StateNameForComposite(key));

      if (!toPrint.equals(""))
        out.print(toPrint);

      out.println("               case " + StateNameForComposite(key) + ":");

      if (!dumped[toBePrinted.stateName] && !stateBlock && toBePrinted.inNextOf > 1)
        out.println("               case " + toBePrinted.stateName + ":");

      dumped[toBePrinted.stateName] = true;
      toBePrinted.DumpAsciiMove(out, byteNum, dumped);
      return;
    }

    final List<List<NfaState>> partition = PartitionStatesSetForAscii(nameSet, byteNum);

    if (!toPrint.equals(""))
      out.print(toPrint);

    final int keyState = StateNameForComposite(key);
    out.println("               case " + keyState + ":");
    if (keyState < generatedStates)
      dumped[keyState] = true;

    for (i = 0; i < partition.size(); i++) {
      final List<NfaState> subSet = partition.get(i);

      for (int j = 0; j < subSet.size(); j++) {
        tmp = subSet.get(j);

        if (stateBlock)
          dumped[tmp.stateName] = true;
        tmp.DumpAsciiMoveForCompositeState(out, byteNum, j != 0);
      }
    }

    if (stateBlock)
      out.println("                  break;");
    else
      out.println("                  break;");
  }

  private boolean selfLoop() {
    if (next == null || next.epsilonMovesString == null)
      return false;

    final int[] set = allNextStates.get(next.epsilonMovesString);
    return ElemOccurs(stateName, set) >= 0;
  }

  private void DumpAsciiMoveForCompositeState(final PrintWriter out, final int byteNum,
                                              final boolean elseNeeded) {
    boolean nextIntersects = selfLoop();

    for (int j = 0; j < allStates.size(); j++) {
      final NfaState temp1 = allStates.get(j);

      if (this == temp1 || temp1.stateName == -1 || temp1.dummy || stateName == temp1.stateName ||
          temp1.asciiMoves[byteNum] == 0L)
        continue;

      if (!nextIntersects && Intersect(temp1.next.epsilonMovesString, next.epsilonMovesString)) {
        nextIntersects = true;
        break;
      }
    }

    //System.out.println(stateName + " \'s nextIntersects : " + nextIntersects);
    String prefix = "";
    if (asciiMoves[byteNum] != 0xffffffffffffffffL) {
      final int oneBit = OnlyOneBitSet(asciiMoves[byteNum]);

      if (oneBit != -1)
        out.println("                  " + (elseNeeded ? "else " : "") + "if (curChar == " +
                    (64 * byteNum + oneBit) + ")");
      else
        out.println("                  " + (elseNeeded ? "else " : "") + "if ((0x" +
                    Long.toHexString(asciiMoves[byteNum]) + "L & l) != 0L)");
      prefix = "   ";
    }

    if (kindToPrint != Integer.MAX_VALUE) {
      if (asciiMoves[byteNum] != 0xffffffffffffffffL) {
        out.println("                  {");
      }

      out.println(prefix + "                  if (kind > " + kindToPrint + ")");
      out.println(prefix + "                     kind = " + kindToPrint + ";");
    }

    if (next != null && next.usefulEpsilonMoves > 0) {
      final int[] stateNames = allNextStates.get(next.epsilonMovesString);
      if (next.usefulEpsilonMoves == 1) {
        final int name = stateNames[0];

        if (nextIntersects)
          out.println(prefix + "                  jjCheckNAdd(" + name + ");");
        else
          out.println(prefix + "                  jjstateSet[jjnewStateCnt++] = " + name + ";");
      } else if (next.usefulEpsilonMoves == 2 && nextIntersects) {
        out.println(prefix + "                  jjCheckNAddTwoStates(" + stateNames[0] + ", " +
                    stateNames[1] + ");");
      } else {
        final int[] indices = GetStateSetIndicesForUse(next.epsilonMovesString);
        final boolean notTwo = (indices[0] + 1 != indices[1]);

        if (nextIntersects) {
          out.print(prefix + "                  jjCheckNAddStates(" + indices[0]);
          if (notTwo) {
            jjCheckNAddStatesDualNeeded = true;
            out.print(", " + indices[1]);
          } else {
            jjCheckNAddStatesUnaryNeeded = true;
          }
          out.println(");");
        } else
          out.println(prefix + "                  jjAddStates(" + indices[0] + ", " + indices[1] +
                      ");");
      }
    }

    if (asciiMoves[byteNum] != 0xffffffffffffffffL && kindToPrint != Integer.MAX_VALUE)
      out.println("                  }");
  }

  private void DumpAsciiMove(final PrintWriter out, final int byteNum, final boolean dumped[]) {
    boolean nextIntersects = selfLoop() && isComposite;
    boolean onlyState = true;

    for (int j = 0; j < allStates.size(); j++) {
      final NfaState temp1 = allStates.get(j);

      if (this == temp1 || temp1.stateName == -1 || temp1.dummy || stateName == temp1.stateName ||
          temp1.asciiMoves[byteNum] == 0L)
        continue;

      if (onlyState && (asciiMoves[byteNum] & temp1.asciiMoves[byteNum]) != 0L)
        onlyState = false;

      if (!nextIntersects && Intersect(temp1.next.epsilonMovesString, next.epsilonMovesString))
        nextIntersects = true;

      if (!dumped[temp1.stateName] &&
          !temp1.isComposite &&
          asciiMoves[byteNum] == temp1.asciiMoves[byteNum] &&
          kindToPrint == temp1.kindToPrint &&
          (next.epsilonMovesString == temp1.next.epsilonMovesString || (next.epsilonMovesString != null &&
                                                                        temp1.next.epsilonMovesString != null && next.epsilonMovesString.equals(temp1.next.epsilonMovesString)))) {
        dumped[temp1.stateName] = true;
        out.println("               case " + temp1.stateName + ":");
      }
    }

    //if (onlyState)
    //nextIntersects = false;

    final int oneBit = OnlyOneBitSet(asciiMoves[byteNum]);
    if (asciiMoves[byteNum] != 0xffffffffffffffffL) {
      if ((next == null || next.usefulEpsilonMoves == 0) && kindToPrint != Integer.MAX_VALUE) {
        String kindCheck = "";

        if (!onlyState)
          kindCheck = " && kind > " + kindToPrint;

        if (oneBit != -1)
          out.println("                  if (curChar == " + (64 * byteNum + oneBit) + kindCheck +
                      ")");
        else
          out.println("                  if ((0x" + Long.toHexString(asciiMoves[byteNum]) +
                      "L & l) != 0L" + kindCheck + ")");

        out.println("                     kind = " + kindToPrint + ";");

        if (onlyState)
          out.println("                  break;");
        else
          out.println("                  break;");

        return;
      }
    }

    String prefix = "";
    if (kindToPrint != Integer.MAX_VALUE) {

      if (oneBit != -1) {
        out.println("                  if (curChar != " + (64 * byteNum + oneBit) + ")");
        out.println("                     break;");
      } else if (asciiMoves[byteNum] != 0xffffffffffffffffL) {
        out.println("                  if ((0x" + Long.toHexString(asciiMoves[byteNum]) +
                    "L & l) == 0L)");
        out.println("                     break;");
      }

      if (onlyState) {
        out.println("                  kind = " + kindToPrint + ";");
      } else {
        out.println("                  if (kind > " + kindToPrint + ")");
        out.println("                     kind = " + kindToPrint + ";");
      }
    } else {
      if (oneBit != -1) {
        out.println("                  if (curChar == " + (64 * byteNum + oneBit) + ")");
        prefix = "   ";
      } else if (asciiMoves[byteNum] != 0xffffffffffffffffL) {
        out.println("                  if ((0x" + Long.toHexString(asciiMoves[byteNum]) +
                    "L & l) != 0L)");
        prefix = "   ";
      }
    }

    if (next != null && next.usefulEpsilonMoves > 0) {
      final int[] stateNames = allNextStates.get(next.epsilonMovesString);
      if (next.usefulEpsilonMoves == 1) {
        final int name = stateNames[0];
        if (nextIntersects)
          out.println(prefix + "                  jjCheckNAdd(" + name + ");");
        else
          out.println(prefix + "                  jjstateSet[jjnewStateCnt++] = " + name + ";");
      } else if (next.usefulEpsilonMoves == 2 && nextIntersects) {
        out.println(prefix + "                  jjCheckNAddTwoStates(" + stateNames[0] + ", " +
                    stateNames[1] + ");");
      } else {
        final int[] indices = GetStateSetIndicesForUse(next.epsilonMovesString);
        final boolean notTwo = (indices[0] + 1 != indices[1]);

        if (nextIntersects) {
          out.print(prefix + "                  jjCheckNAddStates(" + indices[0]);
          if (notTwo) {
            jjCheckNAddStatesDualNeeded = true;
            out.print(", " + indices[1]);
          } else {
            jjCheckNAddStatesUnaryNeeded = true;
          }
          out.println(");");
        } else
          out.println(prefix + "                  jjAddStates(" + indices[0] + ", " + indices[1] +
                      ");");
      }
    }

    if (onlyState)
      out.println("                  break;");
    else
      out.println("                  break;");
  }

  private static void DumpAsciiMoves(final PrintWriter out, final int byteNum) {
    final boolean[] dumped = new boolean[Math.max(generatedStates, dummyStateIndex + 1)];
    final Enumeration<String> e = compositeStateTable.keys();

    DumpHeadForCase(out, byteNum);

    while (e.hasMoreElements())
      DumpCompositeStatesAsciiMoves(out, e.nextElement(), byteNum, dumped);

    for (int i = 0; i < allStates.size(); i++) {
      final NfaState temp = allStates.get(i);

      if (dumped[temp.stateName] || temp.lexState != LexGen.lexStateIndex ||
          !temp.HasTransitions() || temp.dummy || temp.stateName == -1)
        continue;

      String toPrint = "";

      if (temp.stateForCase != null) {
        if (temp.inNextOf == 1)
          continue;

        if (dumped[temp.stateForCase.stateName])
          continue;

        toPrint = (temp.stateForCase.PrintNoBreak(out, byteNum, dumped));

        if (temp.asciiMoves[byteNum] == 0L) {
          if (toPrint.equals(""))
            out.println("                  break;");

          continue;
        }
      }

      if (temp.asciiMoves[byteNum] == 0L)
        continue;

      if (!toPrint.equals(""))
        out.print(toPrint);

      dumped[temp.stateName] = true;
      out.println("               case " + temp.stateName + ":");
      temp.DumpAsciiMove(out, byteNum, dumped);
    }

    out.println("               default : break;");
    out.println("            }");
    out.println("         } while(i != startsAt);");
  }

  private static void DumpCompositeStatesNonAsciiMoves(final PrintWriter out, final String key,
                                                       final boolean[] dumped) {
    int i;
    final int[] nameSet = allNextStates.get(key);

    if (nameSet.length == 1 || dumped[StateNameForComposite(key)])
      return;

    NfaState toBePrinted = null;
    int neededStates = 0;
    NfaState tmp;
    NfaState stateForCase = null;
    String toPrint = "";
    final boolean stateBlock = (stateBlockTable.get(key) != null);

    for (i = 0; i < nameSet.length; i++) {
      tmp = allStates.get(nameSet[i]);

      if (tmp.nonAsciiMethod != -1) {
        if (neededStates++ == 1)
          break;
        else
          toBePrinted = tmp;
      } else
        dumped[tmp.stateName] = true;

      if (tmp.stateForCase != null) {
        if (stateForCase != null)
          throw new Error("JavaCC Bug: Please send mail to sankar@cs.stanford.edu : ");

        stateForCase = tmp.stateForCase;
      }
    }

    if (stateForCase != null)
      toPrint = stateForCase.PrintNoBreak(out, -1, dumped);

    if (neededStates == 0) {
      if (stateForCase != null && toPrint.equals(""))
        out.println("                  break;");

      return;
    }

    if (neededStates == 1) {
      if (!toPrint.equals(""))
        out.print(toPrint);

      out.println("               case " + StateNameForComposite(key) + ":");

      if (!dumped[toBePrinted.stateName] && !stateBlock && toBePrinted.inNextOf > 1)
        out.println("               case " + toBePrinted.stateName + ":");

      dumped[toBePrinted.stateName] = true;
      toBePrinted.DumpNonAsciiMove(out, dumped);
      return;
    }

    if (!toPrint.equals(""))
      out.print(toPrint);

    final int keyState = StateNameForComposite(key);
    out.println("               case " + keyState + ":");
    if (keyState < generatedStates)
      dumped[keyState] = true;

    for (i = 0; i < nameSet.length; i++) {
      tmp = allStates.get(nameSet[i]);

      if (tmp.nonAsciiMethod != -1) {
        if (stateBlock)
          dumped[tmp.stateName] = true;
        tmp.DumpNonAsciiMoveForCompositeState(out);
      }
    }

    if (stateBlock)
      out.println("                  break;");
    else
      out.println("                  break;");
  }

  private final void DumpNonAsciiMoveForCompositeState(final PrintWriter out) {
    boolean nextIntersects = selfLoop();
    for (int j = 0; j < allStates.size(); j++) {
      final NfaState temp1 = allStates.get(j);

      if (this == temp1 || temp1.stateName == -1 || temp1.dummy || stateName == temp1.stateName ||
          (temp1.nonAsciiMethod == -1))
        continue;

      if (!nextIntersects && Intersect(temp1.next.epsilonMovesString, next.epsilonMovesString)) {
        nextIntersects = true;
        break;
      }
    }

    if (!Options.getJavaUnicodeEscape() && !unicodeWarningGiven) {
      if (loByteVec != null && loByteVec.size() > 1)
        out.println("                  if ((jjbitVec" + loByteVec.get(1).intValue() + "[i2" +
                    "] & l2) != 0L)");
    } else {
      out.println("                  if (jjCanMove_" + nonAsciiMethod + "(hiByte, i1, i2, l1, l2))");
    }

    if (kindToPrint != Integer.MAX_VALUE) {
      out.println("                  {");
      out.println("                     if (kind > " + kindToPrint + ")");
      out.println("                        kind = " + kindToPrint + ";");
    }

    if (next != null && next.usefulEpsilonMoves > 0) {
      final int[] stateNames = allNextStates.get(next.epsilonMovesString);
      if (next.usefulEpsilonMoves == 1) {
        final int name = stateNames[0];
        if (nextIntersects)
          out.println("                     jjCheckNAdd(" + name + ");");
        else
          out.println("                     jjstateSet[jjnewStateCnt++] = " + name + ";");
      } else if (next.usefulEpsilonMoves == 2 && nextIntersects) {
        out.println("                     jjCheckNAddTwoStates(" + stateNames[0] + ", " +
                    stateNames[1] + ");");
      } else {
        final int[] indices = GetStateSetIndicesForUse(next.epsilonMovesString);
        final boolean notTwo = (indices[0] + 1 != indices[1]);

        if (nextIntersects) {
          out.print("                     jjCheckNAddStates(" + indices[0]);
          if (notTwo) {
            jjCheckNAddStatesDualNeeded = true;
            out.print(", " + indices[1]);
          } else {
            jjCheckNAddStatesUnaryNeeded = true;
          }
          out.println(");");
        } else
          out.println("                     jjAddStates(" + indices[0] + ", " + indices[1] + ");");
      }
    }

    if (kindToPrint != Integer.MAX_VALUE)
      out.println("                  }");
  }

  private final void DumpNonAsciiMove(final PrintWriter out, final boolean dumped[]) {
    boolean nextIntersects = selfLoop() && isComposite;

    for (int j = 0; j < allStates.size(); j++) {
      final NfaState temp1 = allStates.get(j);

      if (this == temp1 || temp1.stateName == -1 || temp1.dummy || stateName == temp1.stateName ||
          (temp1.nonAsciiMethod == -1))
        continue;

      if (!nextIntersects && Intersect(temp1.next.epsilonMovesString, next.epsilonMovesString))
        nextIntersects = true;

      if (!dumped[temp1.stateName] &&
          !temp1.isComposite &&
          nonAsciiMethod == temp1.nonAsciiMethod &&
          kindToPrint == temp1.kindToPrint &&
          (next.epsilonMovesString == temp1.next.epsilonMovesString || (next.epsilonMovesString != null &&
                                                                        temp1.next.epsilonMovesString != null && next.epsilonMovesString.equals(temp1.next.epsilonMovesString)))) {
        dumped[temp1.stateName] = true;
        out.println("               case " + temp1.stateName + ":");
      }
    }

    if (next == null || next.usefulEpsilonMoves <= 0) {
      final String kindCheck = " && kind > " + kindToPrint;

      if (!Options.getJavaUnicodeEscape() && !unicodeWarningGiven) {
        if (loByteVec != null && loByteVec.size() > 1)
          out.println("                  if ((jjbitVec" + loByteVec.get(1).intValue() + "[i2" +
                      "] & l2) != 0L" + kindCheck + ")");
      } else {
        out.println("                  if (jjCanMove_" + nonAsciiMethod +
                    "(hiByte, i1, i2, l1, l2)" + kindCheck + ")");
      }
      out.println("                     kind = " + kindToPrint + ";");
      out.println("                  break;");
      return;
    }

    String prefix = "   ";
    if (kindToPrint != Integer.MAX_VALUE) {
      if (!Options.getJavaUnicodeEscape() && !unicodeWarningGiven) {
        if (loByteVec != null && loByteVec.size() > 1) {
          out.println("                  if ((jjbitVec" + loByteVec.get(1).intValue() + "[i2" +
                      "] & l2) == 0L)");
          out.println("                     break;");
        }
      } else {
        out.println("                  if (!jjCanMove_" + nonAsciiMethod +
                    "(hiByte, i1, i2, l1, l2))");
        out.println("                     break;");
      }

      out.println("                  if (kind > " + kindToPrint + ")");
      out.println("                     kind = " + kindToPrint + ";");
      prefix = "";
    } else if (!Options.getJavaUnicodeEscape() && !unicodeWarningGiven) {
      if (loByteVec != null && loByteVec.size() > 1)
        out.println("                  if ((jjbitVec" + loByteVec.get(1).intValue() + "[i2" +
                    "] & l2) != 0L)");
    } else {
      out.println("                  if (jjCanMove_" + nonAsciiMethod + "(hiByte, i1, i2, l1, l2))");
    }

    if (next != null && next.usefulEpsilonMoves > 0) {
      final int[] stateNames = allNextStates.get(next.epsilonMovesString);
      if (next.usefulEpsilonMoves == 1) {
        final int name = stateNames[0];
        if (nextIntersects)
          out.println(prefix + "                  jjCheckNAdd(" + name + ");");
        else
          out.println(prefix + "                  jjstateSet[jjnewStateCnt++] = " + name + ";");
      } else if (next.usefulEpsilonMoves == 2 && nextIntersects) {
        out.println(prefix + "                  jjCheckNAddTwoStates(" + stateNames[0] + ", " +
                    stateNames[1] + ");");
      } else {
        final int[] indices = GetStateSetIndicesForUse(next.epsilonMovesString);
        final boolean notTwo = (indices[0] + 1 != indices[1]);

        if (nextIntersects) {
          out.print(prefix + "                  jjCheckNAddStates(" + indices[0]);
          if (notTwo) {
            jjCheckNAddStatesDualNeeded = true;
            out.print(", " + indices[1]);
          } else {
            jjCheckNAddStatesUnaryNeeded = true;
          }
          out.println(");");
        } else
          out.println(prefix + "                  jjAddStates(" + indices[0] + ", " + indices[1] +
                      ");");
      }
    }

    out.println("                  break;");
  }

  public static void DumpCharAndRangeMoves(final PrintWriter out) {
    final boolean[] dumped = new boolean[Math.max(generatedStates, dummyStateIndex + 1)];
    final Enumeration<String> e = compositeStateTable.keys();
    int i;

    DumpHeadForCase(out, -1);

    while (e.hasMoreElements())
      DumpCompositeStatesNonAsciiMoves(out, e.nextElement(), dumped);

    for (i = 0; i < allStates.size(); i++) {
      final NfaState temp = allStates.get(i);

      if (temp.stateName == -1 || dumped[temp.stateName] || temp.lexState != LexGen.lexStateIndex ||
          !temp.HasTransitions() || temp.dummy)
        continue;

      String toPrint = "";

      if (temp.stateForCase != null) {
        if (temp.inNextOf == 1)
          continue;

        if (dumped[temp.stateForCase.stateName])
          continue;

        toPrint = (temp.stateForCase.PrintNoBreak(out, -1, dumped));

        if (temp.nonAsciiMethod == -1) {
          if (toPrint.equals(""))
            out.println("                  break;");

          continue;
        }
      }

      if (temp.nonAsciiMethod == -1)
        continue;

      if (!toPrint.equals(""))
        out.print(toPrint);

      dumped[temp.stateName] = true;
      //System.out.println("case : " + temp.stateName);
      out.println("               case " + temp.stateName + ":");
      temp.DumpNonAsciiMove(out, dumped);
    }

    out.println("               default : break;");
    out.println("            }");
    out.println("         } while(i != startsAt);");
  }

  public static void DumpNonAsciiMoveMethods(final PrintWriter out) {
    if (!Options.getJavaUnicodeEscape() && !unicodeWarningGiven)
      return;

    if (nonAsciiTableForMethod.size() <= 0)
      return;

    for (int i = 0; i < nonAsciiTableForMethod.size(); i++) {
      final NfaState tmp = nonAsciiTableForMethod.get(i);
      tmp.DumpNonAsciiMoveMethod(out);
    }
  }

  void DumpNonAsciiMoveMethod(final PrintWriter out) {
    int j;
    out.println("private static final boolean jjCanMove_" + nonAsciiMethod +
                "(int hiByte, int i1, int i2, long l1, long l2)");
    out.println("{");
    out.println("   switch(hiByte)");
    out.println("   {");

    if (loByteVec != null && loByteVec.size() > 0) {
      for (j = 0; j < loByteVec.size(); j += 2) {
        out.println("      case " + loByteVec.get(j).intValue() + ":");
        if (!AllBitsSet(allBitVectors.get(loByteVec.get(j + 1).intValue()))) {
          out.println("         return ((jjbitVec" + loByteVec.get(j + 1).intValue() + "[i2" +
                      "] & l2) != 0L);");
        } else
          out.println("            return true;");
      }
    }

    out.println("      default :");

    if (nonAsciiMoveIndices != null && (j = nonAsciiMoveIndices.length) > 0) {
      do {
        if (!AllBitsSet(allBitVectors.get(nonAsciiMoveIndices[j - 2])))
          out.println("         if ((jjbitVec" + nonAsciiMoveIndices[j - 2] + "[i1] & l1) != 0L)");
        if (!AllBitsSet(allBitVectors.get(nonAsciiMoveIndices[j - 1]))) {
          out.println("            if ((jjbitVec" + nonAsciiMoveIndices[j - 1] +
                      "[i2] & l2) == 0L)");
          out.println("               return false;");
          out.println("            else");
        }
        out.println("            return true;");
      }
      while ((j -= 2) > 0);
    }

    out.println("         return false;");
    out.println("   }");
    out.println("}");
  }

  private static void ReArrange() {
    final List<NfaState> v = allStates;
    final List<NfaState> l = Collections.nCopies(generatedStates, null);
    allStates = new ArrayList<NfaState>(l);

    if (allStates.size() != generatedStates)
      throw new Error("What??");

    for (int j = 0; j < v.size(); j++) {
      final NfaState tmp = v.get(j);
      if (tmp.stateName != -1 && !tmp.dummy)
        allStates.set(tmp.stateName, tmp);
    }
  }

  //private static boolean boilerPlateDumped = false;
  static void PrintBoilerPlate(final PrintWriter out) {
    out.println((Options.getStatic() ? "static " : "") + "private void " + "jjCheckNAdd(int state)");
    out.println("{");
    out.println("   if (jjrounds[state] != jjround)");
    out.println("   {");
    out.println("      jjstateSet[jjnewStateCnt++] = state;");
    out.println("      jjrounds[state] = jjround;");
    out.println("   }");
    out.println("}");

    out.println((Options.getStatic() ? "static " : "") + "private void " +
                "jjAddStates(int start, int end)");
    out.println("{");
    out.println("   do {");
    out.println("      jjstateSet[jjnewStateCnt++] = jjnextStates[start];");
    out.println("   } while (start++ != end);");
    out.println("}");

    out.println((Options.getStatic() ? "static " : "") + "private void " +
                "jjCheckNAddTwoStates(int state1, int state2)");
    out.println("{");
    out.println("   jjCheckNAdd(state1);");
    out.println("   jjCheckNAdd(state2);");
    out.println("}");
    out.println("");
    if (jjCheckNAddStatesDualNeeded) {
      out.println((Options.getStatic() ? "static " : "") + "private void " +
                  "jjCheckNAddStates(int start, int end)");
      out.println("{");
      out.println("   do {");
      out.println("      jjCheckNAdd(jjnextStates[start]);");
      out.println("   } while (start++ != end);");
      out.println("}");
      out.println("");
    }

    if (jjCheckNAddStatesUnaryNeeded) {
      out.println((Options.getStatic() ? "static " : "") + "private void " +
                  "jjCheckNAddStates(int start)");
      out.println("{");
      out.println("   jjCheckNAdd(jjnextStates[start]);");
      out.println("   jjCheckNAdd(jjnextStates[start + 1]);");
      out.println("}");
      out.println("");
    }
  }

  @SuppressWarnings("unused")
  private static void FindStatesWithNoBreak() {
    final Map<String, String> printed = new Hashtable<String, String>();
    final boolean[] put = new boolean[generatedStates];
    int cnt = 0;
    int i, j, foundAt = 0;

    Outer: for (j = 0; j < allStates.size(); j++) {
      NfaState stateForCase = null;
      final NfaState tmpState = allStates.get(j);

      if (tmpState.stateName == -1 || tmpState.dummy || !tmpState.UsefulState() ||
          tmpState.next == null || tmpState.next.usefulEpsilonMoves < 1)
        continue;

      final String s = tmpState.next.epsilonMovesString;

      if (compositeStateTable.get(s) != null || printed.get(s) != null)
        continue;

      printed.put(s, s);
      final int[] nexts = allNextStates.get(s);

      if (nexts.length == 1)
        continue;

      int state = cnt;
      //System.out.println("State " + tmpState.stateName + " : " + s);
      for (i = 0; i < nexts.length; i++) {
        if ((state = nexts[i]) == -1)
          continue;

        final NfaState tmp = allStates.get(state);

        if (!tmp.isComposite && tmp.inNextOf == 1) {
          if (put[state])
            throw new Error("JavaCC Bug: Please send mail to sankar@cs.stanford.edu");

          foundAt = i;
          cnt++;
          stateForCase = tmp;
          put[state] = true;

          //System.out.print(state + " : " + tmp.inNextOf + ", ");
          break;
        }
      }
      //System.out.println("");

      if (stateForCase == null)
        continue;

      for (i = 0; i < nexts.length; i++) {
        if ((state = nexts[i]) == -1)
          continue;

        final NfaState tmp = allStates.get(state);

        if (!put[state] && tmp.inNextOf > 1 && !tmp.isComposite && tmp.stateForCase == null) {
          cnt++;
          nexts[i] = -1;
          put[state] = true;

          final int toSwap = nexts[0];
          nexts[0] = nexts[foundAt];
          nexts[foundAt] = toSwap;

          tmp.stateForCase = stateForCase;
          stateForCase.stateForCase = tmp;
          stateSetsToFix.put(s, nexts);

          //System.out.println("For : " + s + "; " + stateForCase.stateName +
          //" and " + tmp.stateName);

          continue Outer;
        }
      }

      for (i = 0; i < nexts.length; i++) {
        if ((state = nexts[i]) == -1)
          continue;

        final NfaState tmp = allStates.get(state);
        if (tmp.inNextOf <= 1)
          put[state] = false;
      }
    }
  }

  static int[][]   kinds;
  static int[][][] statesForState;

  public static void DumpMoveNfa(final PrintWriter out) {
    //if (!boilerPlateDumped)
    //   PrintBoilerPlate(out);

    //boilerPlateDumped = true;
    int i;
    int[] kindsForStates = null;

    if (kinds == null) {
      kinds = new int[LexGen.maxLexStates][];
      statesForState = new int[LexGen.maxLexStates][][];
    }

    ReArrange();

    for (i = 0; i < allStates.size(); i++) {
      final NfaState temp = allStates.get(i);

      if (temp.lexState != LexGen.lexStateIndex || !temp.HasTransitions() || temp.dummy ||
          temp.stateName == -1)
        continue;

      if (kindsForStates == null) {
        kindsForStates = new int[generatedStates];
        statesForState[LexGen.lexStateIndex] = new int[Math.max(generatedStates,
                                                                dummyStateIndex + 1)][];
      }

      kindsForStates[temp.stateName] = temp.lookingFor;
      statesForState[LexGen.lexStateIndex][temp.stateName] = temp.compositeStates;

      temp.GenerateNonAsciiMoves(out);
    }

    final Enumeration<String> e = stateNameForComposite.keys();

    while (e.hasMoreElements()) {
      final String s = e.nextElement();
      final int state = stateNameForComposite.get(s).intValue();

      if (state >= generatedStates)
        statesForState[LexGen.lexStateIndex][state] = allNextStates.get(s);
    }

    if (stateSetsToFix.size() != 0)
      FixStateSets();

    kinds[LexGen.lexStateIndex] = kindsForStates;

    out.println((Options.getStatic() ? "static " : "") + "private int " + "jjMoveNfa" +
                LexGen.lexStateSuffix + "(int startState, int curPos)");
    out.println("{");

    if (generatedStates == 0) {
      out.println("   return curPos;");
      out.println("}");
      return;
    }

    if (LexGen.mixed[LexGen.lexStateIndex]) {
      out.println("   int strKind = jjmatchedKind;");
      out.println("   int strPos = jjmatchedPos;");
      out.println("   int seenUpto;");
      out.println("   input_stream.backup(seenUpto = curPos + 1);");
      out.println("   try { curChar = input_stream.readChar(); }");
      out.println("   catch(java.io.IOException e) { throw new Error(\"Internal Error\"); }");
      out.println("   curPos = 0;");
    }

    out.println("   int startsAt = 0;");
    out.println("   jjnewStateCnt = " + generatedStates + ";");
    out.println("   int i = 1;");
    out.println("   jjstateSet[0] = startState;");

    if (Options.getDebugTokenManager())
      out.println("      debugStream.println(\"   Starting NFA to match one of : \" + "
                  + "jjKindsForStateVector(curLexState, jjstateSet, 0, 1));");

    if (Options.getDebugTokenManager())
      out.println("      debugStream.println(" +
                  (LexGen.maxLexStates > 1 ? "\"<\" + lexStateNames[curLexState] + \">\" + " : "") +
                  "\"Current character : \" + " +
                  "TokenMgrError.addEscapes(String.valueOf(curChar)) + \" (\" + (int)curChar + \") " +
                  "at line \" + input_stream.getEndLine() + \" column \" + input_stream.getEndColumn());");

    out.println("   int kind = 0x" + Integer.toHexString(Integer.MAX_VALUE) + ";");
    out.println("   for (;;)");
    out.println("   {");
    out.println("      if (++jjround == 0x" + Integer.toHexString(Integer.MAX_VALUE) + ")");
    out.println("         ReInitRounds();");
    out.println("      if (curChar < 64)");
    out.println("      {");

    DumpAsciiMoves(out, 0);

    out.println("      }");

    out.println("      else if (curChar < 128)");

    out.println("      {");

    DumpAsciiMoves(out, 1);

    out.println("      }");

    out.println("      else");
    out.println("      {");

    DumpCharAndRangeMoves(out);

    out.println("      }");

    out.println("      if (kind != 0x" + Integer.toHexString(Integer.MAX_VALUE) + ")");
    out.println("      {");
    out.println("         jjmatchedKind = kind;");
    out.println("         jjmatchedPos = curPos;");
    out.println("         kind = 0x" + Integer.toHexString(Integer.MAX_VALUE) + ";");
    out.println("      }");
    out.println("      ++curPos;");

    if (Options.getDebugTokenManager()) {
      out.println("      if (jjmatchedKind != 0 && jjmatchedKind != 0x" +
                  Integer.toHexString(Integer.MAX_VALUE) + ")");
      out.println("         debugStream.println("
                  + "\"   Currently matched the first \" + (jjmatchedPos + 1) + \" characters as"
                  + " a \" + tokenImage[jjmatchedKind] + \" token.\");");
    }

    out.println("      if ((i = jjnewStateCnt) == (startsAt = " + generatedStates +
                " - (jjnewStateCnt = startsAt)))");
    if (LexGen.mixed[LexGen.lexStateIndex])
      out.println("         break;");
    else
      out.println("         return curPos;");

    if (Options.getDebugTokenManager())
      out.println("      debugStream.println(\"   Possible kinds of longer matches : \" + "
                  + "jjKindsForStateVector(curLexState, jjstateSet, startsAt, i));");

    out.println("      try { curChar = input_stream.readChar(); }");

    if (LexGen.mixed[LexGen.lexStateIndex])
      out.println("      catch(java.io.IOException e) { break; }");
    else
      out.println("      catch(java.io.IOException e) { return curPos; }");

    if (Options.getDebugTokenManager())
      out.println("      debugStream.println(" +
                  (LexGen.maxLexStates > 1 ? "\"<\" + lexStateNames[curLexState] + \">\" + " : "") +
                  "\"Current character : \" + " +
                  "TokenMgrError.addEscapes(String.valueOf(curChar)) + \" (\" + (int)curChar + \") " +
                  "at line \" + input_stream.getEndLine() + \" column \" + input_stream.getEndColumn());");

    out.println("   }");

    if (LexGen.mixed[LexGen.lexStateIndex]) {
      out.println("   if (jjmatchedPos > strPos)");
      out.println("      return curPos;");
      out.println("");
      out.println("   int toRet = Math.max(curPos, seenUpto);");
      out.println("");
      out.println("   if (curPos < toRet)");
      out.println("      for (i = toRet - Math.min(curPos, seenUpto); i-- > 0; )");
      out.println("         try { curChar = input_stream.readChar(); }");
      out.println("         catch(java.io.IOException e) { "
                  + "throw new Error(\"Internal Error : Please send a bug report.\"); }");
      out.println("");
      out.println("   if (jjmatchedPos < strPos)");
      out.println("   {");
      out.println("      jjmatchedKind = strKind;");
      out.println("      jjmatchedPos = strPos;");
      out.println("   }");
      out.println("   else if (jjmatchedPos == strPos && jjmatchedKind > strKind)");
      out.println("      jjmatchedKind = strKind;");
      out.println("");
      out.println("   return toRet;");
    }

    out.println("}");
    allStates.clear();
  }

  public static void DumpStatesForState(final PrintWriter out) {
    out.print("protected static final int[][][] statesForState = ");

    if (statesForState == null) {
      out.println("null;");
      return;
    } else
      out.println("{");

    for (int i = 0; i < statesForState.length; i++) {

      if (statesForState[i] == null) {
        out.println(" null,");
        continue;
      }

      out.println(" {");

      for (int j = 0; j < statesForState[i].length; j++) {
        final int[] stateSet = statesForState[i][j];

        if (stateSet == null) {
          out.println("   { " + j + " },");
          continue;
        }

        out.print("   { ");

        for (int k = 0; k < stateSet.length; k++)
          out.print(stateSet[k] + ", ");

        out.println("},");
      }
      out.println(" },");
    }
    out.println("\n};");
  }

  public static void DumpStatesForKind(final PrintWriter out) {
    DumpStatesForState(out);
    boolean moreThanOne = false;
    int cnt = 0;

    out.print("protected static final int[][] kindForState = ");

    if (kinds == null) {
      out.println("null;");
      return;
    } else
      out.println("{");

    for (int i = 0; i < kinds.length; i++) {
      if (moreThanOne)
        out.println(",");
      moreThanOne = true;

      if (kinds[i] == null)
        out.println("null");
      else {
        cnt = 0;
        out.print("{ ");
        for (int j = 0; j < kinds[i].length; j++) {
          if (cnt++ > 0)
            out.print(",");

          if (cnt % 15 == 0)
            out.print("\n  ");
          else if (cnt > 1)
            out.print(" ");

          out.print(kinds[i][j]);
        }
        out.print("}");
      }
    }
    out.println("\n};");
  }

  public static void reInit() {
    unicodeWarningGiven = false;
    generatedStates = 0;
    idCnt = 0;
    lohiByteCnt = 0;
    dummyStateIndex = -1;
    done = false;
    mark = null;
    stateDone = null;
    allStates = new ArrayList<NfaState>();
    indexedAllStates = new ArrayList<NfaState>();
    nonAsciiTableForMethod = new ArrayList<NfaState>();
    equivStatesTable = new Hashtable<String, NfaState>();
    allNextStates = new Hashtable<String, int[]>();
    lohiByteTab = new Hashtable<String, Integer>();
    stateNameForComposite = new Hashtable<String, Integer>();
    compositeStateTable = new Hashtable<String, int[]>();
    stateBlockTable = new Hashtable<String, String>();
    stateSetsToFix = new Hashtable<String, int[]>();
    allBitVectors = new ArrayList<String>();
    tmpIndices = new int[512];
    allBits = "{\n   0xffffffffffffffffL, " + "0xffffffffffffffffL, " + "0xffffffffffffffffL, "
              + "0xffffffffffffffffL\n};";
    tableToDump = new Hashtable<String, int[]>();
    orderedStateSet = new ArrayList<int[]>();
    lastIndex = 0;
    //boilerPlateDumped = false;
    jjCheckNAddStatesUnaryNeeded = false;
    jjCheckNAddStatesDualNeeded = false;
    kinds = null;
    statesForState = null;
  }

}

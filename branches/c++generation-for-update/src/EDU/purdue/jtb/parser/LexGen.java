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

import static EDU.purdue.jtb.parser.JavaCCParserConstants.*;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Generate lexer.
 * 
 * @author Marc Mazas
 * @version 1.4.0 : 05/2009 : MMa : adapted to JavaCC v4.2 grammar
 * @version 1.4.8 : 12/2014 : MMa : moved to imports static
 */
public class LexGen extends JavaCCGlobals {

  static private PrintWriter                      out;
  static private String                           staticString;
  static private String                           tokMgrClassName;

  // Hashtable of vectors
  static Hashtable<String, List<TokenProduction>> allTpsForState  = new Hashtable<String, List<TokenProduction>>();
  public static int                               lexStateIndex   = 0;
  static int[]                                    kinds;
  public static int                               maxOrdinal      = 1;
  public static String                            lexStateSuffix;
  static String[]                                 newLexState;
  public static int[]                             lexStates;
  public static boolean[]                         ignoreCase;
  public static Action[]                          actions;
  public static Map<String, NfaState>             initStates      = new Hashtable<String, NfaState>();
  public static int                               stateSetSize;
  public static int                               maxLexStates;
  public static String[]                          lexStateName;
  static NfaState[]                               singlesToSkip;
  public static long[]                            toSkip;
  public static long[]                            toSpecial;
  public static long[]                            toMore;
  public static long[]                            toToken;
  public static int                               defaultLexState;
  public static RegularExpression_[]              rexprs;
  public static int[]                             maxLongsReqd;
  public static int[]                             initMatch;
  public static int[]                             canMatchAnyChar;
  public static boolean                           hasEmptyMatch;
  public static boolean[]                         canLoop;
  public static boolean[]                         stateHasActions;
  public static boolean                           hasLoop         = false;
  public static boolean[]                         canReachOnMore;
  public static boolean[]                         hasNfa;
  public static boolean[]                         mixed;
  public static NfaState                          initialState;
  public static int                               curKind;
  static boolean                                  hasSkipActions  = false;
  static boolean                                  hasMoreActions  = false;
  static boolean                                  hasTokenActions = false;
  static boolean                                  hasSpecial      = false;
  static boolean                                  hasSkip         = false;
  static boolean                                  hasMore         = false;
  public static RegularExpression_                curRE;
  public static boolean                           keepLineCol;

  static void PrintClassHead() {
    int i, j;

    try {
      final File tmp = new File(Options.getOutputDirectory(), tokMgrClassName + ".java");
      out = new java.io.PrintWriter(new java.io.BufferedWriter(new java.io.FileWriter(tmp),
                                                               12 * 8192));
      final List<String> tn = new ArrayList<String>(toolNames);
      tn.add(toolName);

      out.println("/* " + getIdString(tn, tokMgrClassName + ".java") + " */");

      int l = 0, kind;
      i = 1;
      for (;;) {
        if (cu_to_insertion_point_1.size() <= l)
          break;

        kind = cu_to_insertion_point_1.get(l).kind;
        if (kind == PACKAGE || kind == IMPORT) {
          for (; i < cu_to_insertion_point_1.size(); i++) {
            kind = cu_to_insertion_point_1.get(i).kind;
            if (kind == SEMICOLON || kind == ABSTRACT || kind == FINAL || kind == PUBLIC ||
                kind == CLASS || kind == INTERFACE) {
              cline = ((cu_to_insertion_point_1.get(l))).beginLine;
              ccol = ((cu_to_insertion_point_1.get(l))).beginColumn;
              for (j = l; j < i; j++) {
                printToken((cu_to_insertion_point_1.get(j)), out);
              }
              if (kind == SEMICOLON)
                printToken((cu_to_insertion_point_1.get(j)), out);
              out.println("");
              break;
            }
          }
          l = ++i;
        } else
          break;
      }

      out.println("");
      out.println("/** Token Manager. */");
      if (Options.getSupportClassVisibilityPublic()) {
        out.print("public ");
      }
      out.println("class " + tokMgrClassName + " implements " + cu_name + "Constants");
      out.println("{"); // }
    }
    catch (final java.io.IOException err) {
      JavaCCErrors.semantic_error("Could not create file : " + tokMgrClassName + ".java\n");
      throw new Error();
    }

    if (token_mgr_decls != null && token_mgr_decls.size() > 0) {
      Token t = token_mgr_decls.get(0);
      boolean commonTokenActionSeen = false;
      final boolean commonTokenActionNeeded = Options.getCommonTokenAction();

      printTokenSetup(token_mgr_decls.get(0));
      ccol = 1;

      for (j = 0; j < token_mgr_decls.size(); j++) {
        t = token_mgr_decls.get(j);
        if (t.kind == IDENTIFIER && commonTokenActionNeeded && !commonTokenActionSeen)
          commonTokenActionSeen = t.image.equals("CommonTokenAction");

        printToken(t, out);
      }

      out.println("");
      if (commonTokenActionNeeded && !commonTokenActionSeen)
        JavaCCErrors.warning("You have the COMMON_TOKEN_ACTION option set. " +
                             "But it appears you have not defined the method :\n" + "      " +
                             staticString + "void CommonTokenAction(Token t)\n" +
                             "in your TOKEN_MGR_DECLS. The generated token manager will not compile.");

    } else if (Options.getCommonTokenAction()) {
      JavaCCErrors.warning("You have the COMMON_TOKEN_ACTION option set. " +
                           "But you have not defined the method :\n" + "      " + staticString +
                           "void CommonTokenAction(Token t)\n" +
                           "in your TOKEN_MGR_DECLS. The generated token manager will not compile.");
    }

    out.println("");
    out.println("  /** Debug output. */");
    out.println("  public " + staticString + " java.io.PrintStream debugStream = System.out;");
    out.println("  /** Set debug output. */");
    out.println("  public " + staticString +
                " void setDebugStream(java.io.PrintStream ds) { debugStream = ds; }");

    if (Options.getTokenManagerUsesParser() && !Options.getStatic()) {
      out.println("");
      out.println("  /** The parser. */");
      out.println("  public " + cu_name + " parser = null;");
    }
  }

  static void DumpDebugMethods() {

    out.println("  " + staticString + " int kindCnt = 0;");
    out.println("  protected " + staticString +
                " final String jjKindsForBitVector(int i, long vec)");
    out.println("  {");
    out.println("    String retVal = \"\";");
    out.println("    if (i == 0)");
    out.println("       kindCnt = 0;");
    out.println("    for (int j = 0; j < 64; j++)");
    out.println("    {");
    out.println("       if ((vec & (1L << j)) != 0L)");
    out.println("       {");
    out.println("          if (kindCnt++ > 0)");
    out.println("             retVal += \", \";");
    out.println("          if (kindCnt % 5 == 0)");
    out.println("             retVal += \"\\n     \";");
    out.println("          retVal += tokenImage[i * 64 + j];");
    out.println("       }");
    out.println("    }");
    out.println("    return retVal;");
    out.println("  }");
    out.println("");

    out.println("  protected " + staticString + " final String jjKindsForStateVector(" +
                "int lexState, int[] vec, int start, int end)");
    out.println("  {");
    out.println("    boolean[] kindDone = new boolean[" + maxOrdinal + "];");
    out.println("    String retVal = \"\";");
    out.println("    int cnt = 0;");
    out.println("    for (int i = start; i < end; i++)");
    out.println("    {");
    out.println("     if (vec[i] == -1)");
    out.println("       continue;");
    out.println("     int[] stateSet = statesForState[curLexState][vec[i]];");
    out.println("     for (int j = 0; j < stateSet.length; j++)");
    out.println("     {");
    out.println("       int state = stateSet[j];");
    out.println("       if (!kindDone[kindForState[lexState][state]])");
    out.println("       {");
    out.println("          kindDone[kindForState[lexState][state]] = true;");
    out.println("          if (cnt++ > 0)");
    out.println("             retVal += \", \";");
    out.println("          if (cnt % 5 == 0)");
    out.println("             retVal += \"\\n     \";");
    out.println("          retVal += tokenImage[kindForState[lexState][state]];");
    out.println("       }");
    out.println("     }");
    out.println("    }");
    out.println("    if (cnt == 0)");
    out.println("       return \"{  }\";");
    out.println("    else");
    out.println("       return \"{ \" + retVal + \" }\";");
    out.println("  }");
    out.println("");
  }

  static void BuildLexStatesTable() {
    final Iterator<TokenProduction> it = rexprlist.iterator();
    TokenProduction tp;
    int i;

    final String[] tmpLexStateName = new String[lexstate_I2S.size()];
    while (it.hasNext()) {
      tp = it.next();
      final List<RegExprSpec_> respecs = tp.respecs;
      List<TokenProduction> tps;

      for (i = 0; i < tp.lexStates.length; i++) {
        if ((tps = allTpsForState.get(tp.lexStates[i])) == null) {
          tmpLexStateName[maxLexStates++] = tp.lexStates[i];
          allTpsForState.put(tp.lexStates[i], tps = new ArrayList<TokenProduction>());
        }

        tps.add(tp);
      }

      if (respecs == null || respecs.size() == 0)
        continue;

      RegularExpression_ re;
      for (i = 0; i < respecs.size(); i++)
        if (maxOrdinal <= (re = (respecs.get(i)).rexp).ordinal)
          maxOrdinal = re.ordinal + 1;
    }

    kinds = new int[maxOrdinal];
    toSkip = new long[maxOrdinal / 64 + 1];
    toSpecial = new long[maxOrdinal / 64 + 1];
    toMore = new long[maxOrdinal / 64 + 1];
    toToken = new long[maxOrdinal / 64 + 1];
    toToken[0] = 1L;
    actions = new Action[maxOrdinal];
    actions[0] = actForEof;
    hasTokenActions = actForEof != null;
    initStates = new Hashtable<String, NfaState>();
    canMatchAnyChar = new int[maxLexStates];
    canLoop = new boolean[maxLexStates];
    stateHasActions = new boolean[maxLexStates];
    lexStateName = new String[maxLexStates];
    singlesToSkip = new NfaState[maxLexStates];
    System.arraycopy(tmpLexStateName, 0, lexStateName, 0, maxLexStates);

    for (i = 0; i < maxLexStates; i++)
      canMatchAnyChar[i] = -1;

    hasNfa = new boolean[maxLexStates];
    mixed = new boolean[maxLexStates];
    maxLongsReqd = new int[maxLexStates];
    initMatch = new int[maxLexStates];
    newLexState = new String[maxOrdinal];
    newLexState[0] = nextStateForEof;
    hasEmptyMatch = false;
    lexStates = new int[maxOrdinal];
    ignoreCase = new boolean[maxOrdinal];
    rexprs = new RegularExpression_[maxOrdinal];
    RStringLiteral.allImages = new String[maxOrdinal];
    canReachOnMore = new boolean[maxLexStates];
  }

  static int GetIndex(final String name) {
    for (int i = 0; i < lexStateName.length; i++)
      if (lexStateName[i] != null && lexStateName[i].equals(name))
        return i;

    throw new Error(); // Should never come here
  }

  public static void AddCharToSkip(final char c, final int kind) {
    singlesToSkip[lexStateIndex].AddChar(c);
    singlesToSkip[lexStateIndex].kind = kind;
  }

  public static void start() {
    if (!Options.getBuildTokenManager() || Options.getUserTokenManager() ||
        JavaCCErrors.get_error_count() > 0)
      return;

    keepLineCol = Options.getKeepLineColumn();
    final List<RegularExpression_> choices = new ArrayList<RegularExpression_>();
    Enumeration<String> e;
    TokenProduction tp;
    int i, j;

    staticString = (Options.getStatic() ? "static " : "");
    tokMgrClassName = cu_name + "TokenManager";

    PrintClassHead();
    BuildLexStatesTable();

    e = allTpsForState.keys();

    boolean ignoring = false;

    while (e.hasMoreElements()) {
      NfaState.ReInit();
      RStringLiteral.ReInit();

      final String key = e.nextElement();

      lexStateIndex = GetIndex(key);
      lexStateSuffix = "_" + lexStateIndex;
      final List<TokenProduction> allTps = allTpsForState.get(key);
      initStates.put(key, initialState = new NfaState());
      ignoring = false;

      singlesToSkip[lexStateIndex] = new NfaState();
      singlesToSkip[lexStateIndex].dummy = true;

      if (key.equals("DEFAULT"))
        defaultLexState = lexStateIndex;

      for (i = 0; i < allTps.size(); i++) {
        tp = allTps.get(i);
        final int kind = tp.kind;
        final boolean ignore = tp.ignoreCase;
        final List<RegExprSpec_> rexps = tp.respecs;

        if (i == 0)
          ignoring = ignore;

        for (j = 0; j < rexps.size(); j++) {
          final RegExprSpec_ respec = rexps.get(j);
          curRE = respec.rexp;

          rexprs[curKind = curRE.ordinal] = curRE;
          lexStates[curRE.ordinal] = lexStateIndex;
          ignoreCase[curRE.ordinal] = ignore;

          if (curRE.private_rexp) {
            kinds[curRE.ordinal] = -1;
            continue;
          }

          if (curRE instanceof RStringLiteral && !((RStringLiteral) curRE).image.equals("")) {
            // ((RStringLiteral) curRE).GenerateDfa(out, curRE.ordinal);
            ((RStringLiteral) curRE).GenerateDfa();
            if (i != 0 && !mixed[lexStateIndex] && ignoring != ignore)
              mixed[lexStateIndex] = true;
          } else if (curRE.CanMatchAnyChar()) {
            if (canMatchAnyChar[lexStateIndex] == -1 ||
                canMatchAnyChar[lexStateIndex] > curRE.ordinal)
              canMatchAnyChar[lexStateIndex] = curRE.ordinal;
          } else {
            Nfa temp;

            if (curRE instanceof RChoice)
              choices.add(curRE);

            temp = curRE.GenerateNfa(ignore);
            temp.endNfaState.isFinal = true;
            temp.endNfaState.kind = curRE.ordinal;
            initialState.AddMove(temp.startNfaState);
          }

          if (kinds.length < curRE.ordinal) {
            final int[] tmp = new int[curRE.ordinal + 1];

            System.arraycopy(kinds, 0, tmp, 0, kinds.length);
            kinds = tmp;
          }
          //System.out.println("   ordina : " + curRE.ordinal);

          kinds[curRE.ordinal] = kind;

          if (respec.nextState != null && !respec.nextState.equals(lexStateName[lexStateIndex]))
            newLexState[curRE.ordinal] = respec.nextState;

          if (respec.act != null && respec.act.getActionTokens() != null &&
              respec.act.getActionTokens().size() > 0)
            actions[curRE.ordinal] = respec.act;

          switch (kind) {
            case TokenProduction.SPECIAL:
              hasSkipActions |= (actions[curRE.ordinal] != null) ||
                                (newLexState[curRE.ordinal] != null);
              hasSpecial = true;
              toSpecial[curRE.ordinal / 64] |= 1L << (curRE.ordinal % 64);
              toSkip[curRE.ordinal / 64] |= 1L << (curRE.ordinal % 64);
              break;
            case TokenProduction.SKIP:
              hasSkipActions |= (actions[curRE.ordinal] != null);
              hasSkip = true;
              toSkip[curRE.ordinal / 64] |= 1L << (curRE.ordinal % 64);
              break;
            case TokenProduction.MORE:
              hasMoreActions |= (actions[curRE.ordinal] != null);
              hasMore = true;
              toMore[curRE.ordinal / 64] |= 1L << (curRE.ordinal % 64);

              if (newLexState[curRE.ordinal] != null)
                canReachOnMore[GetIndex(newLexState[curRE.ordinal])] = true;
              else
                canReachOnMore[lexStateIndex] = true;

              break;
            case TokenProduction.TOKEN:
              hasTokenActions |= (actions[curRE.ordinal] != null);
              toToken[curRE.ordinal / 64] |= 1L << (curRE.ordinal % 64);
              break;
          }
        }
      }

      // Generate a static block for initializing the nfa transitions
      NfaState.ComputeClosures();

      for (i = 0; i < initialState.epsilonMoves.size(); i++)
        (initialState.epsilonMoves.elementAt(i)).GenerateCode();

      hasNfa[lexStateIndex] = (NfaState.generatedStates != 0);
      if (NfaState.generatedStates != 0) {
        initialState.GenerateCode();
        // initialState.GenerateInitMoves(out);
        initialState.GenerateInitMoves();
      }

      if (initialState.kind != Integer.MAX_VALUE && initialState.kind != 0) {
        if ((toSkip[initialState.kind / 64] & (1L << initialState.kind)) != 0L ||
            (toSpecial[initialState.kind / 64] & (1L << initialState.kind)) != 0L)
          hasSkipActions = true;
        else if ((toMore[initialState.kind / 64] & (1L << initialState.kind)) != 0L)
          hasMoreActions = true;
        else
          hasTokenActions = true;

        if (initMatch[lexStateIndex] == 0 || initMatch[lexStateIndex] > initialState.kind) {
          initMatch[lexStateIndex] = initialState.kind;
          hasEmptyMatch = true;
        }
      } else if (initMatch[lexStateIndex] == 0)
        initMatch[lexStateIndex] = Integer.MAX_VALUE;

      RStringLiteral.FillSubString();

      if (hasNfa[lexStateIndex] && !mixed[lexStateIndex])
        RStringLiteral.GenerateNfaStartStates(out, initialState);

      RStringLiteral.DumpDfaCode(out);

      if (hasNfa[lexStateIndex])
        NfaState.DumpMoveNfa(out);

      if (stateSetSize < NfaState.generatedStates)
        stateSetSize = NfaState.generatedStates;
    }

    for (i = 0; i < choices.size(); i++)
      ((RChoice) choices.get(i)).CheckUnmatchability();

    NfaState.DumpStateSets(out);
    CheckEmptyStringMatch();
    NfaState.DumpNonAsciiMoveMethods(out);
    RStringLiteral.DumpStrLiteralImages(out);
    DumpStaticVarDeclarations();
    DumpFillToken();
    DumpGetNextToken();

    if (Options.getDebugTokenManager()) {
      NfaState.DumpStatesForKind(out);
      DumpDebugMethods();
    }

    if (hasLoop) {
      out.println(staticString + "int[] jjemptyLineNo = new int[" + maxLexStates + "];");
      out.println(staticString + "int[] jjemptyColNo = new int[" + maxLexStates + "];");
      out.println(staticString + "boolean[] jjbeenHere = new boolean[" + maxLexStates + "];");
    }

    if (hasSkipActions)
      DumpSkipActions();
    if (hasMoreActions)
      DumpMoreActions();
    if (hasTokenActions)
      DumpTokenActions();

    NfaState.PrintBoilerPlate(out);
    out.println(/*{*/"}");
    out.close();
  }

  static void CheckEmptyStringMatch() {
    int i, j, k, len;
    final boolean[] seen = new boolean[maxLexStates];
    final boolean[] done = new boolean[maxLexStates];
    String cycle;
    String reList;

    Outer: for (i = 0; i < maxLexStates; i++) {
      if (done[i] || initMatch[i] == 0 || initMatch[i] == Integer.MAX_VALUE ||
          canMatchAnyChar[i] != -1)
        continue;

      done[i] = true;
      len = 0;
      cycle = "";
      reList = "";

      for (k = 0; k < maxLexStates; k++)
        seen[k] = false;

      j = i;
      seen[i] = true;
      cycle += lexStateName[j] + "-->";
      while (newLexState[initMatch[j]] != null) {
        cycle += newLexState[initMatch[j]];
        if (seen[j = GetIndex(newLexState[initMatch[j]])])
          break;

        cycle += "-->";
        done[j] = true;
        seen[j] = true;
        if (initMatch[j] == 0 || initMatch[j] == Integer.MAX_VALUE || canMatchAnyChar[j] != -1)
          continue Outer;
        if (len != 0)
          reList += "; ";
        reList += "line " + rexprs[initMatch[j]].getLine() + ", column " +
                  rexprs[initMatch[j]].getColumn();
        len++;
      }

      if (newLexState[initMatch[j]] == null)
        cycle += lexStateName[lexStates[initMatch[j]]];

      for (k = 0; k < maxLexStates; k++)
        canLoop[k] |= seen[k];

      hasLoop = true;
      if (len == 0)
        JavaCCErrors.warning(rexprs[initMatch[i]],
                             "Regular expression" +
                                 ((rexprs[initMatch[i]].label.equals(""))
                                                                         ? ""
                                                                         : (" for " + rexprs[initMatch[i]].label)) +
                                 " can be matched by the empty string (\"\") in lexical state " +
                                 lexStateName[i] + ". This can result in an endless loop of " +
                                 "empty string matches.");
      else {
        JavaCCErrors.warning(rexprs[initMatch[i]],
                             "Regular expression" +
                                 ((rexprs[initMatch[i]].label.equals(""))
                                                                         ? ""
                                                                         : (" for " + rexprs[initMatch[i]].label)) +
                                 " can be matched by the empty string (\"\") in lexical state " +
                                 lexStateName[i] + ". This regular expression along with the " +
                                 "regular expressions at " + reList + " forms the cycle \n   " +
                                 cycle + "\ncontaining regular expressions with empty matches." +
                                 " This can result in an endless loop of empty string matches.");
      }
    }
  }

  static void PrintArrayInitializer(final int noElems) {
    out.print("{");
    for (int i = 0; i < noElems; i++) {
      if (i % 25 == 0)
        out.print("\n   ");
      out.print("0, ");
    }
    out.println("\n};");
  }

  static void DumpStaticVarDeclarations() {
    int i;
    String charStreamName;

    out.println("");
    out.println("/** Lexer state names. */");
    out.println("public static final String[] lexStateNames = {");
    for (i = 0; i < maxLexStates; i++)
      out.println("   \"" + lexStateName[i] + "\",");
    out.println("};");

    if (maxLexStates > 1) {
      out.println("");
      out.println("/** Lex State array. */");
      out.print("public static final int[] jjnewLexState = {");

      for (i = 0; i < maxOrdinal; i++) {
        if (i % 25 == 0)
          out.print("\n   ");

        if (newLexState[i] == null)
          out.print("-1, ");
        else
          out.print(GetIndex(newLexState[i]) + ", ");
      }
      out.println("\n};");
    }

    if (hasSkip || hasMore || hasSpecial) {
      // Bit vector for TOKEN
      out.print("static final long[] jjtoToken = {");
      for (i = 0; i < maxOrdinal / 64 + 1; i++) {
        if (i % 4 == 0)
          out.print("\n   ");
        out.print("0x" + Long.toHexString(toToken[i]) + "L, ");
      }
      out.println("\n};");
    }

    if (hasSkip || hasSpecial) {
      // Bit vector for SKIP
      out.print("static final long[] jjtoSkip = {");
      for (i = 0; i < maxOrdinal / 64 + 1; i++) {
        if (i % 4 == 0)
          out.print("\n   ");
        out.print("0x" + Long.toHexString(toSkip[i]) + "L, ");
      }
      out.println("\n};");
    }

    if (hasSpecial) {
      // Bit vector for SPECIAL
      out.print("static final long[] jjtoSpecial = {");
      for (i = 0; i < maxOrdinal / 64 + 1; i++) {
        if (i % 4 == 0)
          out.print("\n   ");
        out.print("0x" + Long.toHexString(toSpecial[i]) + "L, ");
      }
      out.println("\n};");
    }

    if (hasMore) {
      // Bit vector for MORE
      out.print("static final long[] jjtoMore = {");
      for (i = 0; i < maxOrdinal / 64 + 1; i++) {
        if (i % 4 == 0)
          out.print("\n   ");
        out.print("0x" + Long.toHexString(toMore[i]) + "L, ");
      }
      out.println("\n};");
    }

    if (Options.getUserCharStream())
      charStreamName = "CharStream";
    else {
      if (Options.getJavaUnicodeEscape())
        charStreamName = "JavaCharStream";
      else
        charStreamName = "SimpleCharStream";
    }

    out.println(staticString + "protected " + charStreamName + " input_stream;");

    out.println(staticString + "private final int[] jjrounds = " + "new int[" + stateSetSize + "];");
    out.println(staticString + "private final int[] jjstateSet = " + "new int[" +
                (2 * stateSetSize) + "];");

    if (hasMoreActions || hasSkipActions || hasTokenActions) {
      out.println("private " + staticString + "final " + Options.stringBufOrBuild() +
                  " jjimage = new " + Options.stringBufOrBuild() + "();");
      out.println("private " + staticString + Options.stringBufOrBuild() + " image = jjimage;");
      out.println("private " + staticString + "int jjimageLen;");
      out.println("private " + staticString + "int lengthOfMatch;");
    }

    out.println(staticString + "protected char curChar;");

    if (Options.getTokenManagerUsesParser() && !Options.getStatic()) {
      out.println("");
      out.println("/** Constructor with parser. */");
      out.println("public " + tokMgrClassName + "(" + cu_name + " parserArg, " + charStreamName +
                  " stream){");
      out.println("   parser = parserArg;");
    } else {
      out.println("/** Constructor. */");
      out.println("public " + tokMgrClassName + "(" + charStreamName + " stream){");
    }

    if (Options.getStatic() && !Options.getUserCharStream()) {
      out.println("   if (input_stream != null)");
      out.println("      throw new TokenMgrError(\"ERROR: Second call to constructor of static lexer. "
                  + "You must use ReInit() to initialize the static variables.\", TokenMgrError.STATIC_LEXER_ERROR);");
    } else if (!Options.getUserCharStream()) {
      if (Options.getJavaUnicodeEscape())
        out.println("   if (JavaCharStream.staticFlag)");
      else
        out.println("   if (SimpleCharStream.staticFlag)");

      out.println("      throw new Error(\"ERROR: Cannot use a static CharStream class with a "
                  + "non-static lexical analyzer.\");");
    }

    out.println("   input_stream = stream;");

    out.println("}");

    if (Options.getTokenManagerUsesParser() && !Options.getStatic()) {
      out.println("");
      out.println("/** Constructor with parser. */");
      out.println("public " + tokMgrClassName + "(" + cu_name + " parserArg, " + charStreamName +
                  " stream, int lexState){");
      out.println("   this(parserArg, stream);");
    } else {
      out.println("");
      out.println("/** Constructor. */");
      out.println("public " + tokMgrClassName + "(" + charStreamName + " stream, int lexState){");
      out.println("   this(stream);");
    }
    out.println("   SwitchTo(lexState);");
    out.println("}");

    // Reinit method for reinitializing the parser (for static parsers).
    out.println("");
    out.println("/** Reinitialise parser. */");
    out.println(staticString + "public void ReInit(" + charStreamName + " stream)");
    out.println("{");
    out.println("   jjmatchedPos = jjnewStateCnt = 0;");
    out.println("   curLexState = defaultLexState;");
    out.println("   input_stream = stream;");
    out.println("   ReInitRounds();");
    out.println("}");

    // Method to reinitialize the jjrounds array.
    out.println(staticString + "private void ReInitRounds()");
    out.println("{");
    out.println("   int i;");
    out.println("   jjround = 0x" + Integer.toHexString(Integer.MIN_VALUE + 1) + ";");
    out.println("   for (i = " + stateSetSize + "; i-- > 0;)");
    out.println("      jjrounds[i] = 0x" + Integer.toHexString(Integer.MIN_VALUE) + ";");
    out.println("}");

    // Reinit method for reinitializing the parser (for static parsers).
    out.println("");
    out.println("/** Reinitialise parser. */");
    out.println(staticString + "public void ReInit(" + charStreamName + " stream, int lexState)");
    out.println("{");
    out.println("   ReInit(stream);");
    out.println("   SwitchTo(lexState);");
    out.println("}");

    out.println("");
    out.println("/** Switch to specified lex state. */");
    out.println(staticString + "public void SwitchTo(int lexState)");
    out.println("{");
    out.println("   if (lexState >= " + lexStateName.length + " || lexState < 0)");
    out.println("      throw new TokenMgrError(\"Error: Ignoring invalid lexical state : \""
                + " + lexState + \". State unchanged.\", TokenMgrError.INVALID_LEXICAL_STATE);");
    out.println("   else");
    out.println("      curLexState = lexState;");
    out.println("}");

    out.println("");
  }

  // Assumes l != 0L
  static char MaxChar(final long l) {
    for (int i = 64; i-- > 0;)
      if ((l & (1L << i)) != 0L)
        return (char) i;

    return 0xffff;
  }

  static void DumpFillToken() {
    final double tokenVersion = JavaFiles.getVersion("Token.java");
    final boolean hasBinaryNewToken = tokenVersion > 4.09;

    out.println(staticString + "protected Token jjFillToken()");
    out.println("{");
    out.println("   final Token t;");
    out.println("   final String curTokenImage;");
    if (keepLineCol) {
      out.println("   final int startLine;");
      out.println("   final int endLine;");
      out.println("   final int beginColumn;");
      out.println("   final int endColumn;");
    }

    if (hasEmptyMatch) {
      out.println("   if (jjmatchedPos < 0)");
      out.println("   {");
      out.println("      if (image == null)");
      out.println("         curTokenImage = \"\";");
      out.println("      else");
      out.println("         curTokenImage = image.toString();");

      if (keepLineCol) {
        out.println("      startLine = endLine = input_stream.getBeginLine();");
        out.println("      beginColumn = endColumn = input_stream.getBeginColumn();");
      }

      out.println("   }");
      out.println("   else");
      out.println("   {");
      out.println("      String im = jjstrLiteralImages[jjmatchedKind];");
      out.println("      curTokenImage = (im == null) ? input_stream.GetImage() : im;");

      if (keepLineCol) {
        out.println("      startLine = input_stream.getBeginLine();");
        out.println("      beginColumn = input_stream.getBeginColumn();");
        out.println("      endLine = input_stream.getEndLine();");
        out.println("      endColumn = input_stream.getEndColumn();");
      }

      out.println("   }");
    } else {
      out.println("   String im = jjstrLiteralImages[jjmatchedKind];");
      out.println("   curTokenImage = (im == null) ? input_stream.GetImage() : im;");
      if (keepLineCol) {
        out.println("   startLine = input_stream.getBeginLine();");
        out.println("   beginColumn = input_stream.getBeginColumn();");
        out.println("   endLine = input_stream.getEndLine();");
        out.println("   endColumn = input_stream.getEndColumn();");
      }
    }

    if (Options.getTokenFactory().length() > 0) {
      out.println("   t = " + Options.getTokenFactory() +
                  ".newToken(jjmatchedKind, curTokenImage);");
    } else if (hasBinaryNewToken) {
      out.println("   t = Token.newToken(jjmatchedKind, curTokenImage);");
    } else {
      out.println("   t = Token.newToken(jjmatchedKind);");
      out.println("   t.kind = jjmatchedKind;");
      out.println("   t.image = curTokenImage;");
    }

    if (keepLineCol) {
      out.println("");
      out.println("   t.beginLine = startLine;");
      out.println("   t.endLine = endLine;");
      out.println("   t.beginColumn = beginColumn;");
      out.println("   t.endColumn = endColumn;");
    }

    out.println("");
    out.println("   return t;");
    out.println("}");
  }

  static void DumpGetNextToken() {
    int i;

    out.println("");
    out.println(staticString + "int curLexState = " + defaultLexState + ";");
    out.println(staticString + "int defaultLexState = " + defaultLexState + ";");
    out.println(staticString + "int jjnewStateCnt;");
    out.println(staticString + "int jjround;");
    out.println(staticString + "int jjmatchedPos;");
    out.println(staticString + "int jjmatchedKind;");
    out.println("");
    out.println("/** Get the next Token. */");
    out.println("public " + staticString + "Token getNextToken()" + " ");
    out.println("{");
    if (hasSpecial) {
      out.println("  Token specialToken = null;");
    }
    out.println("  Token matchedToken;");
    out.println("  int curPos = 0;");
    out.println("");
    out.println("  EOFLoop :\n  for (;;)");
    out.println("  {");
    out.println("   try");
    out.println("   {");
    out.println("      curChar = input_stream.BeginToken();");
    out.println("   }");
    out.println("   catch(java.io.IOException e)");
    out.println("   {");

    if (Options.getDebugTokenManager())
      out.println("      debugStream.println(\"Returning the <EOF> token.\");");

    out.println("      jjmatchedKind = 0;");
    out.println("      matchedToken = jjFillToken();");

    if (hasSpecial)
      out.println("      matchedToken.specialToken = specialToken;");

    if (nextStateForEof != null || actForEof != null)
      out.println("      TokenLexicalActions(matchedToken);");

    if (Options.getCommonTokenAction())
      out.println("      CommonTokenAction(matchedToken);");

    out.println("      return matchedToken;");
    out.println("   }");

    if (hasMoreActions || hasSkipActions || hasTokenActions) {
      out.println("   image = jjimage;");
      out.println("   image.setLength(0);");
      out.println("   jjimageLen = 0;");
    }

    out.println("");

    String prefix = "";
    if (hasMore) {
      out.println("   for (;;)");
      out.println("   {");
      prefix = "  ";
    }

    String endSwitch = "";
    String caseStr = "";
    // this also sets up the start state of the nfa
    if (maxLexStates > 1) {
      out.println(prefix + "   switch(curLexState)");
      out.println(prefix + "   {");
      endSwitch = prefix + "   }";
      caseStr = prefix + "     case ";
      prefix += "    ";
    }

    prefix += "   ";
    for (i = 0; i < maxLexStates; i++) {
      if (maxLexStates > 1)
        out.println(caseStr + i + ":");

      if (singlesToSkip[i].HasTransitions()) {
        // added the backup(0) to make JIT happy
        out.println(prefix + "try { input_stream.backup(0);");
        if (singlesToSkip[i].asciiMoves[0] != 0L && singlesToSkip[i].asciiMoves[1] != 0L) {
          out.println(prefix + "   while ((curChar < 64" + " && (0x" +
                      Long.toHexString(singlesToSkip[i].asciiMoves[0]) +
                      "L & (1L << curChar)) != 0L) || \n" + prefix +
                      "          (curChar >> 6) == 1" + " && (0x" +
                      Long.toHexString(singlesToSkip[i].asciiMoves[1]) +
                      "L & (1L << (curChar & 077))) != 0L)");
        } else if (singlesToSkip[i].asciiMoves[1] == 0L) {
          out.println(prefix + "   while (curChar <= " +
                      (int) MaxChar(singlesToSkip[i].asciiMoves[0]) + " && (0x" +
                      Long.toHexString(singlesToSkip[i].asciiMoves[0]) +
                      "L & (1L << curChar)) != 0L)");
        } else if (singlesToSkip[i].asciiMoves[0] == 0L) {
          out.println(prefix + "   while (curChar > 63 && curChar <= " +
                      (MaxChar(singlesToSkip[i].asciiMoves[1]) + 64) + " && (0x" +
                      Long.toHexString(singlesToSkip[i].asciiMoves[1]) +
                      "L & (1L << (curChar & 077))) != 0L)");
        }

        if (Options.getDebugTokenManager()) {
          out.println(prefix + "{");
          out.println("      debugStream.println(" +
                      (maxLexStates > 1 ? "\"<\" + lexStateNames[curLexState] + \">\" + " : "") +
                      "\"Skipping character : \" + " +
                      "TokenMgrError.addEscapes(String.valueOf(curChar)) + \" (\" + (int)curChar + \")\");");
        }
        out.println(prefix + "      curChar = input_stream.BeginToken();");

        if (Options.getDebugTokenManager())
          out.println(prefix + "}");

        out.println(prefix + "}");
        out.println(prefix + "catch (java.io.IOException e1) { continue EOFLoop; }");
      }

      if (initMatch[i] != Integer.MAX_VALUE && initMatch[i] != 0) {
        if (Options.getDebugTokenManager())
          out.println("      debugStream.println(\"   Matched the empty string as \" + tokenImage[" +
                      initMatch[i] + "] + \" token.\");");

        out.println(prefix + "jjmatchedKind = " + initMatch[i] + ";");
        out.println(prefix + "jjmatchedPos = -1;");
        out.println(prefix + "curPos = 0;");
      } else {
        out.println(prefix + "jjmatchedKind = 0x" + Integer.toHexString(Integer.MAX_VALUE) + ";");
        out.println(prefix + "jjmatchedPos = 0;");
      }

      if (Options.getDebugTokenManager())
        out.println("      debugStream.println(" +
                    (maxLexStates > 1 ? "\"<\" + lexStateNames[curLexState] + \">\" + " : "") +
                    "\"Current character : \" + " +
                    "TokenMgrError.addEscapes(String.valueOf(curChar)) + \" (\" + (int)curChar + \") " +
                    "at line \" + input_stream.getEndLine() + \" column \" + input_stream.getEndColumn());");

      out.println(prefix + "curPos = jjMoveStringLiteralDfa0_" + i + "();");

      if (canMatchAnyChar[i] != -1) {
        if (initMatch[i] != Integer.MAX_VALUE && initMatch[i] != 0)
          out.println(prefix + "if (jjmatchedPos < 0 || (jjmatchedPos == 0 && jjmatchedKind > " +
                      canMatchAnyChar[i] + "))");
        else
          out.println(prefix + "if (jjmatchedPos == 0 && jjmatchedKind > " + canMatchAnyChar[i] +
                      ")");
        out.println(prefix + "{");

        if (Options.getDebugTokenManager())
          out.println("           debugStream.println(\"   Current character matched as a \" + tokenImage[" +
                      canMatchAnyChar[i] + "] + \" token.\");");
        out.println(prefix + "   jjmatchedKind = " + canMatchAnyChar[i] + ";");

        if (initMatch[i] != Integer.MAX_VALUE && initMatch[i] != 0)
          out.println(prefix + "   jjmatchedPos = 0;");

        out.println(prefix + "}");
      }

      if (maxLexStates > 1)
        out.println(prefix + "break;");
    }

    if (maxLexStates > 1)
      out.println(endSwitch);
    else if (maxLexStates == 0)
      out.println("       jjmatchedKind = 0x" + Integer.toHexString(Integer.MAX_VALUE) + ";");

    if (maxLexStates > 1)
      prefix = "  ";
    else
      prefix = "";

    if (maxLexStates > 0) {
      out.println(prefix + "   if (jjmatchedKind != 0x" + Integer.toHexString(Integer.MAX_VALUE) +
                  ")");
      out.println(prefix + "   {");
      out.println(prefix + "      if (jjmatchedPos + 1 < curPos)");

      if (Options.getDebugTokenManager()) {
        out.println(prefix + "      {");
        out.println(prefix + "         debugStream.println(" +
                    "\"   Putting back \" + (curPos - jjmatchedPos - 1) + \" characters into the input stream.\");");
      }

      out.println(prefix + "         input_stream.backup(curPos - jjmatchedPos - 1);");

      if (Options.getDebugTokenManager())
        out.println(prefix + "      }");

      if (Options.getDebugTokenManager()) {
        if (Options.getJavaUnicodeEscape() || Options.getUserCharStream())
          out.println("    debugStream.println("
                      + "\"****** FOUND A \" + tokenImage[jjmatchedKind] + \" MATCH "
                      + "(\" + TokenMgrError.addEscapes(new String(input_stream.GetSuffix(jjmatchedPos + 1))) + "
                      + "\") ******\\n\");");
        else
          out.println("    debugStream.println("
                      + "\"****** FOUND A \" + tokenImage[jjmatchedKind] + \" MATCH "
                      + "(\" + TokenMgrError.addEscapes(new String(input_stream.GetSuffix(jjmatchedPos + 1))) + "
                      + "\") ******\\n\");");
      }

      if (hasSkip || hasMore || hasSpecial) {
        out.println(prefix + "      if ((jjtoToken[jjmatchedKind >> 6] & " +
                    "(1L << (jjmatchedKind & 077))) != 0L)");
        out.println(prefix + "      {");
      }

      out.println(prefix + "         matchedToken = jjFillToken();");

      if (hasSpecial)
        out.println(prefix + "         matchedToken.specialToken = specialToken;");

      if (hasTokenActions)
        out.println(prefix + "         TokenLexicalActions(matchedToken);");

      if (maxLexStates > 1) {
        out.println("       if (jjnewLexState[jjmatchedKind] != -1)");
        out.println(prefix + "       curLexState = jjnewLexState[jjmatchedKind];");
      }

      if (Options.getCommonTokenAction())
        out.println(prefix + "         CommonTokenAction(matchedToken);");

      out.println(prefix + "         return matchedToken;");

      if (hasSkip || hasMore || hasSpecial) {
        out.println(prefix + "      }");

        if (hasSkip || hasSpecial) {
          if (hasMore) {
            out.println(prefix + "      else if ((jjtoSkip[jjmatchedKind >> 6] & " +
                        "(1L << (jjmatchedKind & 077))) != 0L)");
          } else
            out.println(prefix + "      else");

          out.println(prefix + "      {");

          if (hasSpecial) {
            out.println(prefix + "         if ((jjtoSpecial[jjmatchedKind >> 6] & " +
                        "(1L << (jjmatchedKind & 077))) != 0L)");
            out.println(prefix + "         {");

            out.println(prefix + "            matchedToken = jjFillToken();");

            out.println(prefix + "            if (specialToken == null)");
            out.println(prefix + "               specialToken = matchedToken;");
            out.println(prefix + "            else");
            out.println(prefix + "            {");
            out.println(prefix + "               matchedToken.specialToken = specialToken;");
            out.println(prefix +
                        "               specialToken = (specialToken.next = matchedToken);");
            out.println(prefix + "            }");

            if (hasSkipActions)
              out.println(prefix + "            SkipLexicalActions(matchedToken);");

            out.println(prefix + "         }");

            if (hasSkipActions) {
              out.println(prefix + "         else");
              out.println(prefix + "            SkipLexicalActions(null);");
            }
          } else if (hasSkipActions)
            out.println(prefix + "         SkipLexicalActions(null);");

          if (maxLexStates > 1) {
            out.println("         if (jjnewLexState[jjmatchedKind] != -1)");
            out.println(prefix + "         curLexState = jjnewLexState[jjmatchedKind];");
          }

          out.println(prefix + "         continue EOFLoop;");
          out.println(prefix + "      }");
        }

        if (hasMore) {
          if (hasMoreActions)
            out.println(prefix + "      MoreLexicalActions();");
          else if (hasSkipActions || hasTokenActions)
            out.println(prefix + "      jjimageLen += jjmatchedPos + 1;");

          if (maxLexStates > 1) {
            out.println("      if (jjnewLexState[jjmatchedKind] != -1)");
            out.println(prefix + "      curLexState = jjnewLexState[jjmatchedKind];");
          }
          out.println(prefix + "      curPos = 0;");
          out.println(prefix + "      jjmatchedKind = 0x" + Integer.toHexString(Integer.MAX_VALUE) +
                      ";");

          out.println(prefix + "      try {");
          out.println(prefix + "         curChar = input_stream.readChar();");

          if (Options.getDebugTokenManager())
            out.println("   debugStream.println(" +
                        (maxLexStates > 1 ? "\"<\" + lexStateNames[curLexState] + \">\" + " : "") +
                        "\"Current character : \" + " +
                        "TokenMgrError.addEscapes(String.valueOf(curChar)) + \" (\" + (int)curChar + \") " +
                        "at line \" + input_stream.getEndLine() + \" column \" + input_stream.getEndColumn());");
          out.println(prefix + "         continue;");
          out.println(prefix + "      }");
          out.println(prefix + "      catch (java.io.IOException e1) { }");
        }
      }

      out.println(prefix + "   }");
      out.println(prefix + "   int error_line = input_stream.getEndLine();");
      out.println(prefix + "   int error_column = input_stream.getEndColumn();");
      out.println(prefix + "   String error_after = null;");
      out.println(prefix + "   boolean EOFSeen = false;");
      out.println(prefix + "   try { input_stream.readChar(); input_stream.backup(1); }");
      out.println(prefix + "   catch (java.io.IOException e1) {");
      out.println(prefix + "      EOFSeen = true;");
      out.println(prefix + "      error_after = curPos <= 1 ? \"\" : input_stream.GetImage();");
      out.println(prefix + "      if (curChar == '\\n' || curChar == '\\r') {");
      out.println(prefix + "         error_line++;");
      out.println(prefix + "         error_column = 0;");
      out.println(prefix + "      }");
      out.println(prefix + "      else");
      out.println(prefix + "         error_column++;");
      out.println(prefix + "   }");
      out.println(prefix + "   if (!EOFSeen) {");
      out.println(prefix + "      input_stream.backup(1);");
      out.println(prefix + "      error_after = curPos <= 1 ? \"\" : input_stream.GetImage();");
      out.println(prefix + "   }");
      out.println(prefix +
                  "   throw new TokenMgrError(" +
                  "EOFSeen, curLexState, error_line, error_column, error_after, curChar, TokenMgrError.LEXICAL_ERROR);");
    }

    if (hasMore)
      out.println(prefix + " }");

    out.println("  }");
    out.println("}");
    out.println("");
  }

  public static void DumpSkipActions() {
    Action act;

    out.println(staticString + "void SkipLexicalActions(Token matchedToken)");
    out.println("{");
    out.println("   switch(jjmatchedKind)");
    out.println("   {");

    Outer: for (int i = 0; i < maxOrdinal; i++) {
      if ((toSkip[i / 64] & (1L << (i % 64))) == 0L)
        continue;

      for (;;) {
        if (((act = actions[i]) == null || act.getActionTokens() == null || act.getActionTokens()
                                                                               .size() == 0) &&
            !canLoop[lexStates[i]])
          continue Outer;

        out.println("      case " + i + " :");

        if (initMatch[lexStates[i]] == i && canLoop[lexStates[i]]) {
          out.println("         if (jjmatchedPos == -1)");
          out.println("         {");
          out.println("            if (jjbeenHere[" + lexStates[i] + "] &&");
          out.println("                jjemptyLineNo[" + lexStates[i] +
                      "] == input_stream.getBeginLine() &&");
          out.println("                jjemptyColNo[" + lexStates[i] +
                      "] == input_stream.getBeginColumn())");
          out.println("               throw new TokenMgrError("
                      + "(\"Error: Bailing out of infinite loop caused by repeated empty string matches "
                      + "at line \" + input_stream.getBeginLine() + \", "
                      + "column \" + input_stream.getBeginColumn() + \".\"), TokenMgrError.LOOP_DETECTED);");
          out.println("            jjemptyLineNo[" + lexStates[i] +
                      "] = input_stream.getBeginLine();");
          out.println("            jjemptyColNo[" + lexStates[i] +
                      "] = input_stream.getBeginColumn();");
          out.println("            jjbeenHere[" + lexStates[i] + "] = true;");
          out.println("         }");
        }

        if ((act = actions[i]) == null || act.getActionTokens().size() == 0)
          break;

        out.print("         image.append");
        if (RStringLiteral.allImages[i] != null) {
          out.println("(jjstrLiteralImages[" + i + "]);");
          out.println("        lengthOfMatch = jjstrLiteralImages[" + i + "].length();");
        } else {
          out.println("(input_stream.GetSuffix(jjimageLen + (lengthOfMatch = jjmatchedPos + 1)));");
        }

        printTokenSetup(act.getActionTokens().get(0));
        ccol = 1;

        for (int j = 0; j < act.getActionTokens().size(); j++)
          printToken(act.getActionTokens().get(j), out);
        out.println("");

        break;
      }

      out.println("         break;");
    }

    out.println("      default :");
    out.println("         break;");
    out.println("   }");
    out.println("}");
  }

  public static void DumpMoreActions() {
    Action act;

    out.println(staticString + "void MoreLexicalActions()");
    out.println("{");
    out.println("   jjimageLen += (lengthOfMatch = jjmatchedPos + 1);");
    out.println("   switch(jjmatchedKind)");
    out.println("   {");

    Outer: for (int i = 0; i < maxOrdinal; i++) {
      if ((toMore[i / 64] & (1L << (i % 64))) == 0L)
        continue;

      for (;;) {
        if (((act = actions[i]) == null || act.getActionTokens() == null || act.getActionTokens()
                                                                               .size() == 0) &&
            !canLoop[lexStates[i]])
          continue Outer;

        out.println("      case " + i + " :");

        if (initMatch[lexStates[i]] == i && canLoop[lexStates[i]]) {
          out.println("         if (jjmatchedPos == -1)");
          out.println("         {");
          out.println("            if (jjbeenHere[" + lexStates[i] + "] &&");
          out.println("                jjemptyLineNo[" + lexStates[i] +
                      "] == input_stream.getBeginLine() &&");
          out.println("                jjemptyColNo[" + lexStates[i] +
                      "] == input_stream.getBeginColumn())");
          out.println("               throw new TokenMgrError("
                      + "(\"Error: Bailing out of infinite loop caused by repeated empty string matches "
                      + "at line \" + input_stream.getBeginLine() + \", "
                      + "column \" + input_stream.getBeginColumn() + \".\"), TokenMgrError.LOOP_DETECTED);");
          out.println("            jjemptyLineNo[" + lexStates[i] +
                      "] = input_stream.getBeginLine();");
          out.println("            jjemptyColNo[" + lexStates[i] +
                      "] = input_stream.getBeginColumn();");
          out.println("            jjbeenHere[" + lexStates[i] + "] = true;");
          out.println("         }");
        }

        if ((act = actions[i]) == null || act.getActionTokens().size() == 0) {
          break;
        }

        out.print("         image.append");

        if (RStringLiteral.allImages[i] != null)
          out.println("(jjstrLiteralImages[" + i + "]);");
        else
          out.println("(input_stream.GetSuffix(jjimageLen));");

        out.println("         jjimageLen = 0;");
        printTokenSetup(act.getActionTokens().get(0));
        ccol = 1;

        for (int j = 0; j < act.getActionTokens().size(); j++)
          printToken(act.getActionTokens().get(j), out);
        out.println("");

        break;
      }

      out.println("         break;");
    }

    out.println("      default :");
    out.println("         break;");

    out.println("   }");
    out.println("}");
  }

  public static void DumpTokenActions() {
    Action act;
    int i;

    out.println(staticString + "void TokenLexicalActions(Token matchedToken)");
    out.println("{");
    out.println("   switch(jjmatchedKind)");
    out.println("   {");

    Outer: for (i = 0; i < maxOrdinal; i++) {
      if ((toToken[i / 64] & (1L << (i % 64))) == 0L)
        continue;

      for (;;) {
        if (((act = actions[i]) == null || act.getActionTokens() == null || act.getActionTokens()
                                                                               .size() == 0) &&
            !canLoop[lexStates[i]])
          continue Outer;

        out.println("      case " + i + " :");

        if (initMatch[lexStates[i]] == i && canLoop[lexStates[i]]) {
          out.println("         if (jjmatchedPos == -1)");
          out.println("         {");
          out.println("            if (jjbeenHere[" + lexStates[i] + "] &&");
          out.println("                jjemptyLineNo[" + lexStates[i] +
                      "] == input_stream.getBeginLine() &&");
          out.println("                jjemptyColNo[" + lexStates[i] +
                      "] == input_stream.getBeginColumn())");
          out.println("               throw new TokenMgrError("
                      + "(\"Error: Bailing out of infinite loop caused by repeated empty string matches "
                      + "at line \" + input_stream.getBeginLine() + \", "
                      + "column \" + input_stream.getBeginColumn() + \".\"), TokenMgrError.LOOP_DETECTED);");
          out.println("            jjemptyLineNo[" + lexStates[i] +
                      "] = input_stream.getBeginLine();");
          out.println("            jjemptyColNo[" + lexStates[i] +
                      "] = input_stream.getBeginColumn();");
          out.println("            jjbeenHere[" + lexStates[i] + "] = true;");
          out.println("         }");
        }

        if ((act = actions[i]) == null || act.getActionTokens().size() == 0)
          break;

        if (i == 0) {
          out.println("      image.setLength(0);"); // For EOF no image is there
        } else {
          out.print("        image.append");

          if (RStringLiteral.allImages[i] != null) {
            out.println("(jjstrLiteralImages[" + i + "]);");
            out.println("        lengthOfMatch = jjstrLiteralImages[" + i + "].length();");
          } else {
            out.println("(input_stream.GetSuffix(jjimageLen + (lengthOfMatch = jjmatchedPos + 1)));");
          }
        }

        printTokenSetup(act.getActionTokens().get(0));
        ccol = 1;

        for (int j = 0; j < act.getActionTokens().size(); j++)
          printToken(act.getActionTokens().get(j), out);
        out.println("");

        break;
      }

      out.println("         break;");
    }

    out.println("      default :");
    out.println("         break;");
    out.println("   }");
    out.println("}");
  }

  public static void reInit() {
    out = null;
    staticString = null;
    tokMgrClassName = null;
    allTpsForState = new Hashtable<String, List<TokenProduction>>();
    lexStateIndex = 0;
    kinds = null;
    maxOrdinal = 1;
    lexStateSuffix = null;
    newLexState = null;
    lexStates = null;
    ignoreCase = null;
    actions = null;
    initStates = new Hashtable<String, NfaState>();
    stateSetSize = 0;
    maxLexStates = 0;
    lexStateName = null;
    singlesToSkip = null;
    toSkip = null;
    toSpecial = null;
    toMore = null;
    toToken = null;
    defaultLexState = 0;
    rexprs = null;
    maxLongsReqd = null;
    initMatch = null;
    canMatchAnyChar = null;
    hasEmptyMatch = false;
    canLoop = null;
    stateHasActions = null;
    hasLoop = false;
    canReachOnMore = null;
    hasNfa = null;
    mixed = null;
    initialState = null;
    curKind = 0;
    hasSkipActions = false;
    hasMoreActions = false;
    hasTokenActions = false;
    hasSpecial = false;
    hasSkip = false;
    hasMore = false;
    curRE = null;
  }

}

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
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

/**
 * @author Marc Mazas
 * @version 1.4.0 : 05/2009 : MMa : adapted to JavaCC v4.2 grammar
 * @version 1.4.8 : 12/2014 : MMa : improved javadoc
 * @version 1.4.14 : 01/2017 : MMa : added suppress warnings
 */
@SuppressWarnings("javadoc")
public class ParseEngine extends JavaCCGlobals {

  static private java.io.PrintWriter               out;
  static private int                               gensymindex = 0;
  static private int                               indentamt;
  static private boolean                           jj2LA;

  /**
   * These lists are used to maintain expansions for which code generation in phase 2 and phase 3 is
   * required. Whenever a call is generated to a phase 2 or phase 3 routine, a corresponding entry
   * is added here if it has not already been added. The phase 3 routines have been optimized in
   * version 0.7pre2. Essentially only those methods (and only those portions of these methods) are
   * generated that are required. The lookahead amount is used to determine this. This change
   * requires the use of a hash table because it is now possible for the same phase 3 routine to be
   * requested multiple times with different lookaheads. The hash table provides a easily searchable
   * capability to determine the previous requests. The phase 3 routines now are performed in a two
   * step process - the first step gathers the requests (replacing requests with lower lookaheads
   * with those requiring larger lookaheads). The second step then generates these methods. This
   * optimization and the hashtable makes it look like we do not need the flag "phase3done" any
   * more. But this has not been removed yet.
   */
  static private List<Lookahead>                   phase2list  = new ArrayList<>();
  static private List<Phase3Data>                  phase3list  = new ArrayList<>();
  static private Hashtable<Expansion_, Phase3Data> phase3table = new Hashtable<>();

  /**
   * The phase 1 routines generates their output into String's and dumps these String's once for
   * each method. These String's contain the special characters '\u0001' to indicate a positive
   * indent, and '\u0002' to indicate a negative indent. '\n' is used to indicate a line terminator.
   * The characters '\u0003' and '\u0004' are used to delineate portions of text where '\n's should
   * not be followed by an indentation.
   */

  /**
   * @param exp - the expansion node
   * @return true if there is a JAVACODE production that the argument expansion may directly expand
   *         to (without consuming tokens or encountering lookahead)
   */
  static private boolean javaCodeCheck(final Expansion_ exp) {
    if (exp instanceof RegularExpression_) {
      return false;
    } else if (exp instanceof NonTerminal) {
      final NormalProduction prod = ((NonTerminal) exp).getProd();
      if (prod instanceof JavaCodeProduction_) {
        return true;
      } else {
        return javaCodeCheck(prod.getExpansion());
      }
    } else if (exp instanceof Choice) {
      final Choice ch = (Choice) exp;
      for (int i = 0; i < ch.getChoices().size(); i++) {
        if (javaCodeCheck((ch.getChoices().get(i)))) {
          return true;
        }
      }
      return false;
    } else if (exp instanceof Sequence) {
      final Sequence seq = (Sequence) exp;
      for (int i = 0; i < seq.units.size(); i++) {
        final Expansion_[] units = seq.units.toArray(new Expansion_[seq.units.size()]);
        if (units[i] instanceof Lookahead && ((Lookahead) units[i]).isExplicit()) {
          // An explicit lookahead (rather than one generated implicitly). Assume
          // the user knows what he / she is doing, e.g.
          //    "A" ( "B" | LOOKAHEAD("X") jcode() | "C" )* "D"
          return false;
        } else if (javaCodeCheck((units[i]))) {
          return true;
        } else if (!Semanticize.emptyExpansionExists(units[i])) {
          return false;
        }
      }
      return false;
    } else if (exp instanceof OneOrMore) {
      final OneOrMore om = (OneOrMore) exp;
      return javaCodeCheck(om.expansion);
    } else if (exp instanceof ZeroOrMore) {
      final ZeroOrMore zm = (ZeroOrMore) exp;
      return javaCodeCheck(zm.expansion);
    } else if (exp instanceof ZeroOrOne) {
      final ZeroOrOne zo = (ZeroOrOne) exp;
      return javaCodeCheck(zo.expansion);
    } else if (exp instanceof TryBlock) {
      final TryBlock tb = (TryBlock) exp;
      return javaCodeCheck(tb.exp);
    } else {
      return false;
    }
  }

  /**
   * An array used to store the first sets generated by the following method. A true entry means
   * that the corresponding token is in the first set.
   */
  static private boolean[] firstSet;

  /**
   * Sets up the array "firstSet" above based on the Expansion_ argument passed to it. Since this is
   * a recursive function, it assumes that "firstSet" has been reset before the first call.
   *
   * @param exp - the expansion node
   */
  static private void genFirstSet(final Expansion_ exp) {
    if (exp instanceof RegularExpression_) {
      firstSet[((RegularExpression_) exp).ordinal] = true;
    } else if (exp instanceof NonTerminal) {
      if (!(((NonTerminal) exp).getProd() instanceof JavaCodeProduction_)) {
        genFirstSet(((BNFProduction_) (((NonTerminal) exp).getProd())).getExpansion());
      }
    } else if (exp instanceof Choice) {
      final Choice ch = (Choice) exp;
      for (int i = 0; i < ch.getChoices().size(); i++) {
        genFirstSet((ch.getChoices().get(i)));
      }
    } else if (exp instanceof Sequence) {
      final Sequence seq = (Sequence) exp;
      final Object obj = seq.units.get(0);
      if ((obj instanceof Lookahead) && (((Lookahead) obj).getActionTokens().size() != 0)) {
        jj2LA = true;
      }
      for (int i = 0; i < seq.units.size(); i++) {
        final Expansion_ unit = seq.units.get(i);
        // Javacode productions can not have FIRST sets. Instead we generate the FIRST set
        // for the preceding LOOKAHEAD (the semantic checks should have made sure that
        // the LOOKAHEAD is suitable).
        if (unit instanceof NonTerminal &&
            ((NonTerminal) unit).getProd() instanceof JavaCodeProduction_) {
          if (i > 0 && seq.units.get(i - 1) instanceof Lookahead) {
            final Lookahead la = (Lookahead) seq.units.get(i - 1);
            genFirstSet(la.getLaExpansion());
          }
        } else {
          genFirstSet((seq.units.get(i)));
        }
        if (!Semanticize.emptyExpansionExists((seq.units.get(i)))) {
          break;
        }
      }
    } else if (exp instanceof OneOrMore) {
      final OneOrMore om = (OneOrMore) exp;
      genFirstSet(om.expansion);
    } else if (exp instanceof ZeroOrMore) {
      final ZeroOrMore zm = (ZeroOrMore) exp;
      genFirstSet(zm.expansion);
    } else if (exp instanceof ZeroOrOne) {
      final ZeroOrOne zo = (ZeroOrOne) exp;
      genFirstSet(zo.expansion);
    } else if (exp instanceof TryBlock) {
      final TryBlock tb = (TryBlock) exp;
      genFirstSet(tb.exp);
    }
  }

  /**
   * Constants used in the following method "buildLookaheadChecker".
   */
  static final int NOOPENSTM  = 0;
  static final int OPENIF     = 1;
  static final int OPENSWITCH = 2;

  @SuppressWarnings("unused")
  private static void dumpLookaheads(final Lookahead[] conds, final String[] actions) {
    for (int i = 0; i < conds.length; i++) {
      System.err.println("Lookahead: " + i);
      System.err.println(conds[i].dump(0, new HashSet<>()));
      System.err.println();
    }
  }

  /**
   * This method takes two parameters - an array of Lookahead's "conds", and an array of String's
   * "actions".<br>
   * "actions" contains exactly one element more than "conds". "actions" are Java source code, and
   * "conds" translate to conditions - so lets say "f(conds[i])" is true if the lookahead required
   * by "conds[i]" is indeed the case.<br>
   * This method returns a string corresponding to the Java code for: if (f(conds[0]) actions[0]
   * else if (f(conds[1]) actions[1] . . . else actions[action.length-1].<br>
   * A particular action entry ("actions[i]") can be null, in which case, a noop is generated for
   * that action.
   *
   * @param conds - the lookahead conditions
   * @param actions - the lookahead actions
   * @return the corresponding java code
   */
  // for           case NOOPENSTM:
  static String buildLookaheadChecker(final Lookahead[] conds, final String[] actions) {

    // The state variables.
    int state = NOOPENSTM;
    int indentAmt = 0;
    final boolean[] casedValues = new boolean[tokenCount];
    final StringBuilder retval = new StringBuilder(1024);
    Lookahead la;
    Token t = null;
    final int tokenMaskSize = (tokenCount - 1) / 32 + 1;
    int[] tokenMask = null;

    // Iterate over all the conditions.
    int index = 0;
    while (index < conds.length) {

      la = conds[index];
      jj2LA = false;

      if ((la.getAmount() == 0) || Semanticize.emptyExpansionExists(la.getLaExpansion()) ||
          javaCodeCheck(la.getLaExpansion())) {

        // This handles the following cases:
        // . If syntactic lookahead is not wanted (and hence explicitly specified
        //   as 0).
        // . If it is possible for the lookahead expansion to recognize the empty
        //   string - in which case the lookahead trivially passes.
        // . If the lookahead expansion has a JAVACODE production that it directly
        //   expands to - in which case the lookahead trivially passes.
        if (la.getActionTokens().size() == 0) {
          // In addition, if there is no semantic lookahead, then the
          // lookahead trivially succeeds.  So break the main loop and
          // treat this case as the default last action.
          break;
        } else {
          // This case is when there is only semantic lookahead
          // (without any preceding syntactic lookahead).  In this
          // case, an "if" statement is generated.
          switch (state) {
            case NOOPENSTM:
              retval.append("\n").append("if (");
              indentAmt++;
              break;
            case OPENIF:
              retval.append("\u0002\n" + "} else if (");
              break;
            case OPENSWITCH:
              retval.append("\u0002\n" + "default:" + "\u0001");
              if (Options.getErrorReporting()) {
                retval.append("\njj_la1[").append(maskindex).append("] = jj_gen;");
                maskindex++;
              }
              maskVals.add(tokenMask);
              retval.append("\n" + "if (");
              indentAmt++;
              break;
            default:
              // ModMMa 01/2017 default case added to avoid warning
              break;
          }
          printTokenSetup((la.getActionTokens().get(0)));
          for (final Iterator<Token> it = la.getActionTokens().iterator(); it.hasNext();) {
            t = it.next();
            retval.append(printToken(t));
          }
          retval.append(printTrailingComments(t));
          retval.append(") {\u0001").append(actions[index]);
          state = OPENIF;
        }

      } else if (la.getAmount() == 1 && la.getActionTokens().size() == 0) {
        // Special optimal processing when the lookahead is exactly 1, and there
        // is no semantic lookahead.

        if (firstSet == null) {
          firstSet = new boolean[tokenCount];
        }
        for (int i = 0; i < tokenCount; i++) {
          firstSet[i] = false;
        }
        // jj2LA is set to false at the beginning of the containing "if" statement.
        // It is checked immediately after the end of the same statement to determine
        // if lookaheads are to be performed using calls to the jj2 methods.
        genFirstSet(la.getLaExpansion());
        // genFirstSet may find that semantic attributes are appropriate for the next
        // token.  In which case, it sets jj2LA to true.
        if (!jj2LA) {

          // This case is if there is no applicable semantic lookahead and the lookahead
          // is one (excluding the earlier cases such as JAVACODE, etc.).
          switch (state) {
            case OPENIF:
              retval.append("\u0002\n" + "} else {\u0001");
              // Control flows through to next case.
              //$FALL-THROUGH$
            case NOOPENSTM:
              retval.append("\n" + "switch (");
              if (Options.getCacheTokens()) {
                retval.append("jj_nt.kind) {\u0001");
              } else {
                retval.append("(jj_ntk==-1)?jj_ntk():jj_ntk) {\u0001");
              }
              for (int i = 0; i < tokenCount; i++) {
                casedValues[i] = false;
              }
              indentAmt++;
              tokenMask = new int[tokenMaskSize];
              for (int i = 0; i < tokenMaskSize; i++) {
                tokenMask[i] = 0;
              }
              // Don't need to do anything if state is OPENSWITCH.
              break;
            default:
              // ModMMa 01/2017 default case added to avoid warning and allocation added to avoid the potential null pointer access warning
              tokenMask = new int[0];
              break;
          }
          for (int i = 0; i < tokenCount; i++) {
            if (firstSet[i]) {
              if (!casedValues[i]) {
                casedValues[i] = true;
                retval.append("\u0002\ncase ");
                final int j1 = i / 32;
                final int j2 = i % 32;
                tokenMask[j1] |= 1 << j2;
                final String s = (names_of_tokens.get(new Integer(i)));
                if (s == null) {
                  retval.append(i);
                } else {
                  retval.append(s);
                }
                retval.append(":\u0001");
              }
            }
          }
          retval.append(actions[index]);
          retval.append("\nbreak;");
          state = OPENSWITCH;

        }

      } else {
        // This is the case when lookahead is determined through calls to
        // jj2 methods.  The other case is when lookahead is 1, but semantic
        // attributes need to be evaluated.  Hence this crazy control structure.

        jj2LA = true;

      }

      if (jj2LA) {
        // In this case lookahead is determined by the jj2 methods.

        switch (state) {
          case NOOPENSTM:
            retval.append("\n" + "if (");
            indentAmt++;
            break;
          case OPENIF:
            retval.append("\u0002\n" + "} else if (");
            break;
          case OPENSWITCH:
            retval.append("\u0002\n" + "default:" + "\u0001");
            if (Options.getErrorReporting()) {
              retval.append("\njj_la1[").append(maskindex).append("] = jj_gen;");
              maskindex++;
            }
            maskVals.add(tokenMask);
            retval.append("\n" + "if (");
            indentAmt++;
            break;
          default:
            // ModMMa 01/2017 default case added to avoid warning
            break;
        }
        jj2index++;
        // At this point, la.la_expansion.internal_name must be "".
        la.getLaExpansion().internal_name = "_" + jj2index;
        phase2list.add(la);
        retval.append("jj_2").append(la.getLaExpansion().internal_name).append("(")
              .append(la.getAmount() + ")");
        if (la.getActionTokens().size() != 0) {
          // In addition, there is also a semantic lookahead.  So concatenate
          // the semantic check with the syntactic one.
          retval.append(" && (");
          printTokenSetup((la.getActionTokens().get(0)));
          for (final Iterator<Token> it = la.getActionTokens().iterator(); it.hasNext();) {
            t = it.next();
            retval.append(printToken(t));
          }
          retval.append(printTrailingComments(t));
          retval.append(")");
        }
        retval.append(") {\u0001").append(actions[index]);
        state = OPENIF;
      }

      index++;
    }

    // Generate code for the default case.  Note this may not
    // be the last entry of "actions" if any condition can be
    // statically determined to be always "true".

    switch (state) {
      case NOOPENSTM:
        retval.append(actions[index]);
        break;
      case OPENIF:
        retval.append("\u0002\n" + "} else {\u0001").append(actions[index]);
        break;
      case OPENSWITCH:
        retval.append("\u0002\n" + "default:" + "\u0001");
        if (Options.getErrorReporting()) {
          retval.append("\njj_la1[").append(maskindex).append("] = jj_gen;");
          maskVals.add(tokenMask);
          maskindex++;
        }
        retval.append(actions[index]);
        break;
      default:
        // ModMMa 01/2017 default case added to avoid warning
        break;
    }
    for (int i = 0; i < indentAmt; i++) {
      retval.append("\u0002\n}");
    }

    return retval.toString();

  }

  static void dumpFormattedString(final String str) {
    char ch = ' ';
    char prevChar;
    boolean indentOn = true;
    for (int i = 0; i < str.length(); i++) {
      prevChar = ch;
      ch = str.charAt(i);
      if (ch == '\n' && prevChar == '\r') {
        // do nothing - we've already printed a new line for the '\r'
        // during the previous iteration.
      } else if (ch == '\n' || ch == '\r') {
        if (indentOn) {
          phase1NewLine();
        } else {
          out.println("");
        }
      } else if (ch == '\u0001') {
        indentamt += 2;
      } else if (ch == '\u0002') {
        indentamt -= 2;
      } else if (ch == '\u0003') {
        indentOn = false;
      } else if (ch == '\u0004') {
        indentOn = true;
      } else {
        out.print(ch);
      }
    }
  }

  static void buildPhase1Routine(final BNFProduction_ p) {
    Token t;
    t = (p.getReturnTypeTokens().get(0));
    boolean voidReturn = false;
    if (t.kind == JavaCCParserConstants.VOID) {
      voidReturn = true;
    }
    printTokenSetup(t);
    ccol = 1;
    printLeadingComments(t, out);
    out.print("  " + staticOpt() + "final " +
              (p.getAccessMod() != null ? p.getAccessMod() : "public") + " ");
    cline = t.beginLine;
    ccol = t.beginColumn;
    printTokenOnly(t, out);
    for (int i = 1; i < p.getReturnTypeTokens().size(); i++) {
      t = (p.getReturnTypeTokens().get(i));
      printToken(t, out);
    }
    printTrailingComments(t, out);
    out.print(" " + p.getLhs() + "(");
    if (p.getParameterListTokens().size() != 0) {
      printTokenSetup((p.getParameterListTokens().get(0)));
      for (final Iterator<Token> it = p.getParameterListTokens().iterator(); it.hasNext();) {
        t = it.next();
        printToken(t, out);
      }
      printTrailingComments(t, out);
    }
    out.print(") throws ParseException");
    for (final Iterator<List<Token>> it = p.getThrowsList().iterator(); it.hasNext();) {
      out.print(", ");
      final List<Token> name = it.next();
      for (final Iterator<Token> it2 = name.iterator(); it2.hasNext();) {
        t = it2.next();
        out.print(t.image);
      }
    }
    out.print(" {");
    indentamt = 4;
    if (Options.getDebugParser()) {
      out.println("");
      out.println("    trace_call(\"" + p.getLhs() + "\");");
      out.print("    try {");
      indentamt = 6;
    }
    if (p.getDeclarationTokens().size() != 0) {
      printTokenSetup((p.getDeclarationTokens().get(0)));
      cline--;
      for (final Iterator<Token> it = p.getDeclarationTokens().iterator(); it.hasNext();) {
        t = it.next();
        out.print("  ");
        printToken(t, out);
      }
      printTrailingComments(t, out);
    }
    final String code = phase1ExpansionGen(p.getExpansion());
    dumpFormattedString(code);
    out.println("");
    if (p.isJumpPatched() && !voidReturn) {
      out.println("    throw new Error(\"Missing return statement in function\");");
    }
    if (Options.getDebugParser()) {
      out.println("    } finally {");
      out.println("      trace_return(\"" + p.getLhs() + "\");");
      out.println("    }");
    }
    out.println("  }");
    out.println("");
  }

  static void phase1NewLine() {
    out.println("");
    for (int i = 0; i < indentamt; i++) {
      out.print(" ");
    }
  }

  static String phase1ExpansionGen(final Expansion_ e) {
    String retval = "";
    Token t = null;
    Lookahead[] conds;
    String[] actions;
    if (e instanceof RegularExpression_) {
      final RegularExpression_ e_nrw = (RegularExpression_) e;
      retval += "\n";
      if (e_nrw.lhsTokens.size() != 0) {
        printTokenSetup((e_nrw.lhsTokens.get(0)));
        for (final Iterator<Token> it = e_nrw.lhsTokens.iterator(); it.hasNext();) {
          t = it.next();
          retval += printToken(t);
        }
        retval += printTrailingComments(t);
        retval += " = ";
      }
      final String tail = e_nrw.rhsToken == null ? ");" : ")." + e_nrw.rhsToken.image + ";";
      if (e_nrw.label.equals("")) {
        final Object label = names_of_tokens.get(new Integer(e_nrw.ordinal));
        if (label != null) {
          retval += "jj_consume_token(" + (String) label + tail;
        } else {
          retval += "jj_consume_token(" + e_nrw.ordinal + tail;
        }
      } else {
        retval += "jj_consume_token(" + e_nrw.label + tail;
      }
    } else if (e instanceof NonTerminal) {
      final NonTerminal e_nrw = (NonTerminal) e;
      retval += "\n";
      if (e_nrw.getLhsTokens().size() != 0) {
        printTokenSetup((e_nrw.getLhsTokens().get(0)));
        for (final Iterator<Token> it = e_nrw.getLhsTokens().iterator(); it.hasNext();) {
          t = it.next();
          retval += printToken(t);
        }
        retval += printTrailingComments(t);
        retval += " = ";
      }
      retval += e_nrw.getName() + "(";
      if (e_nrw.getArgumentTokens().size() != 0) {
        printTokenSetup((e_nrw.getArgumentTokens().get(0)));
        for (final Iterator<Token> it = e_nrw.getArgumentTokens().iterator(); it.hasNext();) {
          t = it.next();
          retval += printToken(t);
        }
        retval += printTrailingComments(t);
      }
      retval += ");";
    } else if (e instanceof Action) {
      final Action e_nrw = (Action) e;
      retval += "\u0003\n";
      if (e_nrw.getActionTokens().size() != 0) {
        printTokenSetup((e_nrw.getActionTokens().get(0)));
        ccol = 1;
        for (final Iterator<Token> it = e_nrw.getActionTokens().iterator(); it.hasNext();) {
          t = it.next();
          retval += printToken(t);
        }
        retval += printTrailingComments(t);
      }
      retval += "\u0004";
    } else if (e instanceof Choice) {
      final Choice e_nrw = (Choice) e;
      conds = new Lookahead[e_nrw.getChoices().size()];
      actions = new String[e_nrw.getChoices().size() + 1];
      actions[e_nrw.getChoices().size()] = "\n" + "jj_consume_token(-1);\n" +
                                           "throw new ParseException();";
      // In previous line, the "throw" never throws an exception since the
      // evaluation of jj_consume_token(-1) causes ParseException to be
      // thrown first.
      Sequence nestedSeq;
      for (int i = 0; i < e_nrw.getChoices().size(); i++) {
        nestedSeq = (Sequence) (e_nrw.getChoices().get(i));
        actions[i] = phase1ExpansionGen(nestedSeq);
        conds[i] = (Lookahead) (nestedSeq.units.get(0));
      }
      retval = buildLookaheadChecker(conds, actions);
    } else if (e instanceof Sequence) {
      final Sequence e_nrw = (Sequence) e;
      // We skip the first element in the following iteration since it is the
      // Lookahead object.
      for (int i = 1; i < e_nrw.units.size(); i++) {
        retval += phase1ExpansionGen((e_nrw.units.get(i)));
      }
    } else if (e instanceof OneOrMore) {
      final OneOrMore e_nrw = (OneOrMore) e;
      final Expansion_ nested_e = e_nrw.expansion;
      Lookahead la;
      if (nested_e instanceof Sequence) {
        la = (Lookahead) (((Sequence) nested_e).units.get(0));
      } else {
        la = new Lookahead();
        la.setAmount(Options.getLookahead());
        la.setLaExpansion(nested_e);
      }
      retval += "\n";
      final int labelIndex = ++gensymindex;
      retval += "label_" + labelIndex + ":\n";
      retval += "while (true) {\u0001";
      retval += phase1ExpansionGen(nested_e);
      conds = new Lookahead[1];
      conds[0] = la;
      actions = new String[2];
      actions[0] = "\n;";
      actions[1] = "\nbreak label_" + labelIndex + ";";
      retval += buildLookaheadChecker(conds, actions);
      retval += "\u0002\n" + "}";
    } else if (e instanceof ZeroOrMore) {
      final ZeroOrMore e_nrw = (ZeroOrMore) e;
      final Expansion_ nested_e = e_nrw.expansion;
      Lookahead la;
      if (nested_e instanceof Sequence) {
        la = (Lookahead) (((Sequence) nested_e).units.get(0));
      } else {
        la = new Lookahead();
        la.setAmount(Options.getLookahead());
        la.setLaExpansion(nested_e);
      }
      retval += "\n";
      final int labelIndex = ++gensymindex;
      retval += "label_" + labelIndex + ":\n";
      retval += "while (true) {\u0001";
      conds = new Lookahead[1];
      conds[0] = la;
      actions = new String[2];
      actions[0] = "\n;";
      actions[1] = "\nbreak label_" + labelIndex + ";";
      retval += buildLookaheadChecker(conds, actions);
      retval += phase1ExpansionGen(nested_e);
      retval += "\u0002\n" + "}";
    } else if (e instanceof ZeroOrOne) {
      final ZeroOrOne e_nrw = (ZeroOrOne) e;
      final Expansion_ nested_e = e_nrw.expansion;
      Lookahead la;
      if (nested_e instanceof Sequence) {
        la = (Lookahead) (((Sequence) nested_e).units.get(0));
      } else {
        la = new Lookahead();
        la.setAmount(Options.getLookahead());
        la.setLaExpansion(nested_e);
      }
      conds = new Lookahead[1];
      conds[0] = la;
      actions = new String[2];
      actions[0] = phase1ExpansionGen(nested_e);
      actions[1] = "\n;";
      retval += buildLookaheadChecker(conds, actions);
    } else if (e instanceof TryBlock) {
      final TryBlock e_nrw = (TryBlock) e;
      final Expansion_ nested_e = e_nrw.exp;
      List<Token> list;
      retval += "\n";
      retval += "try {\u0001";
      retval += phase1ExpansionGen(nested_e);
      retval += "\u0002\n" + "}";
      for (int i = 0; i < e_nrw.catchblks.size(); i++) {
        retval += " catch (";
        list = (e_nrw.types.get(i));
        if (list.size() != 0) {
          printTokenSetup((list.get(0)));
          for (final Iterator<Token> it = list.iterator(); it.hasNext();) {
            t = it.next();
            retval += printToken(t);
          }
          retval += printTrailingComments(t);
        }
        retval += " ";
        t = (e_nrw.ids.get(i));
        printTokenSetup(t);
        retval += printToken(t);
        retval += printTrailingComments(t);
        retval += ") {\u0003\n";
        list = (e_nrw.catchblks.get(i));
        if (list.size() != 0) {
          printTokenSetup((list.get(0)));
          ccol = 1;
          for (final Iterator<Token> it = list.iterator(); it.hasNext();) {
            t = it.next();
            retval += printToken(t);
          }
          retval += printTrailingComments(t);
        }
        retval += "\u0004\n" + "}";
      }
      if (e_nrw.finallyblk != null) {
        retval += " finally {\u0003\n";
        if (e_nrw.finallyblk.size() != 0) {
          printTokenSetup((e_nrw.finallyblk.get(0)));
          ccol = 1;
          for (final Iterator<Token> it = e_nrw.finallyblk.iterator(); it.hasNext();) {
            t = it.next();
            retval += printToken(t);
          }
          retval += printTrailingComments(t);
        }
        retval += "\u0004\n" + "}";
      }
    }
    return retval;
  }

  static void buildPhase2Routine(final Lookahead la) {
    final Expansion_ e = la.getLaExpansion();
    out.println("  " + staticOpt() + "private boolean jj_2" + e.internal_name + "(int xla) {");
    out.println("    jj_la = xla; jj_lastpos = jj_scanpos = token;");
    out.println("    try { return !jj_3" + e.internal_name + "(); }");
    out.println("    catch(LookaheadSuccess ls) { return true; }");
    if (Options.getErrorReporting())
      out.println("    finally { jj_save(" + (Integer.parseInt(e.internal_name.substring(1)) - 1) +
                  ", xla); }");
    out.println("  }");
    out.println("");
    final Phase3Data p3d = new Phase3Data(e, la.getAmount());
    phase3list.add(p3d);
    phase3table.put(e, p3d);
  }

  static private boolean xsp_declared;

  static Expansion_      jj3_expansion;

  static String genReturn(final boolean value) {
    final String retval = (value ? "true" : "false");
    if (Options.getDebugLookahead() && jj3_expansion != null) {
      String tracecode = "trace_return(\"" + ((NormalProduction) jj3_expansion.parent).getLhs() +
                         "(LOOKAHEAD " + (value ? "FAILED" : "SUCCEEDED") + ")\");";
      if (Options.getErrorReporting()) {
        tracecode = "if (!jj_rescan) " + tracecode;
      }
      return "{ " + tracecode + " return " + retval + "; }";
    } else {
      return "return " + retval + ";";
    }
  }

  private static void generate3R(final Expansion_ e, final Phase3Data inf) {
    Expansion_ seq = e;
    if (e.internal_name.equals("")) {
      while (true) {
        if (seq instanceof Sequence && ((Sequence) seq).units.size() == 2) {
          seq = ((Sequence) seq).units.get(1);
        } else if (seq instanceof NonTerminal) {
          final NonTerminal e_nrw = (NonTerminal) seq;
          final NormalProduction ntprod = (production_table.get(e_nrw.getName()));
          if (ntprod instanceof JavaCodeProduction_) {
            break; // nothing to do here
          } else {
            seq = ntprod.getExpansion();
          }
        } else
          break;
      }

      if (seq instanceof RegularExpression_) {
        e.internal_name = "jj_scan_token(" + ((RegularExpression_) seq).ordinal + ")";
        return;
      }

      gensymindex++;
      //    if (gensymindex == 100)
      //    {
      //    new Error().printStackTrace();
      //    System.out.println(" ***** seq: " + seq.internal_name + "; size: " + ((Sequence)seq).units.size());
      //    }
      e.internal_name = "R_" + gensymindex;
    }
    Phase3Data p3d = (phase3table.get(e));
    if (p3d == null || p3d.count < inf.count) {
      p3d = new Phase3Data(e, inf.count);
      phase3list.add(p3d);
      phase3table.put(e, p3d);
    }
  }

  static void setupPhase3Builds(final Phase3Data inf) {
    final Expansion_ e = inf.exp;
    if (e instanceof RegularExpression_) {
      ; // nothing to here
    } else if (e instanceof NonTerminal) {
      // All expansions of non-terminals have the "name" fields set.  So
      // there's no need to check it below for "e_nrw" and "ntexp".  In
      // fact, we rely here on the fact that the "name" fields of both these
      // variables are the same.
      final NonTerminal e_nrw = (NonTerminal) e;
      final NormalProduction ntprod = (production_table.get(e_nrw.getName()));
      if (ntprod instanceof JavaCodeProduction_) {
        ; // nothing to do here
      } else {
        generate3R(ntprod.getExpansion(), inf);
      }
    } else if (e instanceof Choice) {
      final Choice e_nrw = (Choice) e;
      for (int i = 0; i < e_nrw.getChoices().size(); i++) {
        generate3R((e_nrw.getChoices().get(i)), inf);
      }
    } else if (e instanceof Sequence) {
      final Sequence e_nrw = (Sequence) e;
      // We skip the first element in the following iteration since it is the
      // Lookahead object.
      int cnt = inf.count;
      for (int i = 1; i < e_nrw.units.size(); i++) {
        final Expansion_ eseq = (e_nrw.units.get(i));
        setupPhase3Builds(new Phase3Data(eseq, cnt));
        cnt -= minimumSize(eseq);
        if (cnt <= 0)
          break;
      }
    } else if (e instanceof TryBlock) {
      final TryBlock e_nrw = (TryBlock) e;
      setupPhase3Builds(new Phase3Data(e_nrw.exp, inf.count));
    } else if (e instanceof OneOrMore) {
      final OneOrMore e_nrw = (OneOrMore) e;
      generate3R(e_nrw.expansion, inf);
    } else if (e instanceof ZeroOrMore) {
      final ZeroOrMore e_nrw = (ZeroOrMore) e;
      generate3R(e_nrw.expansion, inf);
    } else if (e instanceof ZeroOrOne) {
      final ZeroOrOne e_nrw = (ZeroOrOne) e;
      generate3R(e_nrw.expansion, inf);
    }
  }

  private static String genjj_3Call(final Expansion_ e) {
    if (e.internal_name.startsWith("jj_scan_token"))
      return e.internal_name;
    else
      return "jj_3" + e.internal_name + "()";
  }

  // static Hashtable<Object, Object> generated = new Hashtable<Object, Object>();
  static void buildPhase3Routine(final Phase3Data inf, final boolean recursive_call) {
    final Expansion_ e = inf.exp;
    Token t = null;
    if (e.internal_name.startsWith("jj_scan_token"))
      return;

    if (!recursive_call) {
      out.println("  " + staticOpt() + "private boolean jj_3" + e.internal_name + "() {");
      xsp_declared = false;
      if (Options.getDebugLookahead() && e.parent instanceof NormalProduction) {
        out.print("    ");
        if (Options.getErrorReporting()) {
          out.print("if (!jj_rescan) ");
        }
        out.println("trace_call(\"" + ((NormalProduction) e.parent).getLhs() +
                    "(LOOKING AHEAD...)\");");
        jj3_expansion = e;
      } else {
        jj3_expansion = null;
      }
    }
    if (e instanceof RegularExpression_) {
      final RegularExpression_ e_nrw = (RegularExpression_) e;
      if (e_nrw.label.equals("")) {
        final Object label = names_of_tokens.get(new Integer(e_nrw.ordinal));
        if (label != null) {
          out.println("    if (jj_scan_token(" + (String) label + ")) " + genReturn(true));
        } else {
          out.println("    if (jj_scan_token(" + e_nrw.ordinal + ")) " + genReturn(true));
        }
      } else {
        out.println("    if (jj_scan_token(" + e_nrw.label + ")) " + genReturn(true));
      }
      //out.println("    if (jj_la == 0 && jj_scanpos == jj_lastpos) " + genReturn(false));
    } else if (e instanceof NonTerminal) {
      // All expansions of non-terminals have the "name" fields set.  So
      // there's no need to check it below for "e_nrw" and "ntexp".  In
      // fact, we rely here on the fact that the "name" fields of both these
      // variables are the same.
      final NonTerminal e_nrw = (NonTerminal) e;
      final NormalProduction ntprod = (production_table.get(e_nrw.getName()));
      if (ntprod instanceof JavaCodeProduction_) {
        out.println("    if (true) { jj_la = 0; jj_scanpos = jj_lastpos; " + genReturn(false) +
                    "}");
      } else {
        final Expansion_ ntexp = ntprod.getExpansion();
        //out.println("    if (jj_3" + ntexp.internal_name + "()) " + genReturn(true));
        out.println("    if (" + genjj_3Call(ntexp) + ") " + genReturn(true));
        //out.println("    if (jj_la == 0 && jj_scanpos == jj_lastpos) " + genReturn(false));
      }
    } else if (e instanceof Choice) {
      Sequence nested_seq;
      final Choice e_nrw = (Choice) e;
      if (e_nrw.getChoices().size() != 1) {
        if (!xsp_declared) {
          xsp_declared = true;
          out.println("    Token xsp;");
        }
        out.println("    xsp = jj_scanpos;");
      }
      for (int i = 0; i < e_nrw.getChoices().size(); i++) {
        nested_seq = (Sequence) (e_nrw.getChoices().get(i));
        final Lookahead la = (Lookahead) (nested_seq.units.get(0));
        if (la.getActionTokens().size() != 0) {
          // We have semantic lookahead that must be evaluated.
          lookaheadNeeded = true;
          out.println("    jj_lookingAhead = true;");
          out.print("    jj_semLA = ");
          printTokenSetup((la.getActionTokens().get(0)));
          for (final Iterator<Token> it = la.getActionTokens().iterator(); it.hasNext();) {
            t = it.next();
            printToken(t, out);
          }
          printTrailingComments(t, out);
          out.println(";");
          out.println("    jj_lookingAhead = false;");
        }
        out.print("    if (");
        if (la.getActionTokens().size() != 0) {
          out.print("!jj_semLA || ");
        }
        if (i != e_nrw.getChoices().size() - 1) {
          //out.println("jj_3" + nested_seq.internal_name + "()) {");
          out.println(genjj_3Call(nested_seq) + ") {");
          out.println("    jj_scanpos = xsp;");
        } else {
          //out.println("jj_3" + nested_seq.internal_name + "()) " + genReturn(true));
          out.println(genjj_3Call(nested_seq) + ") " + genReturn(true));
          //out.println("    if (jj_la == 0 && jj_scanpos == jj_lastpos) " + genReturn(false));
        }
      }
      for (int i = 1; i < e_nrw.getChoices().size(); i++) {
        //out.println("    } else if (jj_la == 0 && jj_scanpos == jj_lastpos) " + genReturn(false));
        out.println("    }");
      }
    } else if (e instanceof Sequence) {
      final Sequence e_nrw = (Sequence) e;
      // We skip the first element in the following iteration since it is the
      // Lookahead object.
      int cnt = inf.count;
      for (int i = 1; i < e_nrw.units.size(); i++) {
        final Expansion_ eseq = (e_nrw.units.get(i));
        buildPhase3Routine(new Phase3Data(eseq, cnt), true);

        //      System.out.println("minimumSize: line: " + eseq.line + ", column: " + eseq.column + ": " +
        //      minimumSize(eseq));//Test Code

        cnt -= minimumSize(eseq);
        if (cnt <= 0)
          break;
      }
    } else if (e instanceof TryBlock) {
      final TryBlock e_nrw = (TryBlock) e;
      buildPhase3Routine(new Phase3Data(e_nrw.exp, inf.count), true);
    } else if (e instanceof OneOrMore) {
      if (!xsp_declared) {
        xsp_declared = true;
        out.println("    Token xsp;");
      }
      final OneOrMore e_nrw = (OneOrMore) e;
      final Expansion_ nested_e = e_nrw.expansion;
      //out.println("    if (jj_3" + nested_e.internal_name + "()) " + genReturn(true));
      out.println("    if (" + genjj_3Call(nested_e) + ") " + genReturn(true));
      //out.println("    if (jj_la == 0 && jj_scanpos == jj_lastpos) " + genReturn(false));
      out.println("    while (true) {");
      out.println("      xsp = jj_scanpos;");
      //out.println("      if (jj_3" + nested_e.internal_name + "()) { jj_scanpos = xsp; break; }");
      out.println("      if (" + genjj_3Call(nested_e) + ") { jj_scanpos = xsp; break; }");
      //out.println("      if (jj_la == 0 && jj_scanpos == jj_lastpos) " + genReturn(false));
      out.println("    }");
    } else if (e instanceof ZeroOrMore) {
      if (!xsp_declared) {
        xsp_declared = true;
        out.println("    Token xsp;");
      }
      final ZeroOrMore e_nrw = (ZeroOrMore) e;
      final Expansion_ nested_e = e_nrw.expansion;
      out.println("    while (true) {");
      out.println("      xsp = jj_scanpos;");
      //out.println("      if (jj_3" + nested_e.internal_name + "()) { jj_scanpos = xsp; break; }");
      out.println("      if (" + genjj_3Call(nested_e) + ") { jj_scanpos = xsp; break; }");
      //out.println("      if (jj_la == 0 && jj_scanpos == jj_lastpos) " + genReturn(false));
      out.println("    }");
    } else if (e instanceof ZeroOrOne) {
      if (!xsp_declared) {
        xsp_declared = true;
        out.println("    Token xsp;");
      }
      final ZeroOrOne e_nrw = (ZeroOrOne) e;
      final Expansion_ nested_e = e_nrw.expansion;
      out.println("    xsp = jj_scanpos;");
      //out.println("    if (jj_3" + nested_e.internal_name + "()) jj_scanpos = xsp;");
      out.println("    if (" + genjj_3Call(nested_e) + ") jj_scanpos = xsp;");
      //out.println("    else if (jj_la == 0 && jj_scanpos == jj_lastpos) " + genReturn(false));
    }
    if (!recursive_call) {
      out.println("    " + genReturn(false));
      out.println("  }");
      out.println("");
    }
  }

  static int minimumSize(final Expansion_ e) {
    return minimumSize(e, Integer.MAX_VALUE);
  }

  /*
   * Returns the minimum number of tokens that can parse to this expansion.
   */
  static int minimumSize(final Expansion_ e, final int oldMin) {
    int retval = 0; // should never be used.  Will be bad if it is.
    if (e.inMinimumSize) {
      // recursive search for minimum size unnecessary.
      return Integer.MAX_VALUE;
    }
    e.inMinimumSize = true;
    if (e instanceof RegularExpression_) {
      retval = 1;
    } else if (e instanceof NonTerminal) {
      final NonTerminal e_nrw = (NonTerminal) e;
      final NormalProduction ntprod = (production_table.get(e_nrw.getName()));
      if (ntprod instanceof JavaCodeProduction_) {
        retval = Integer.MAX_VALUE;
        // Make caller think this is unending (for we do not go beyond JAVACODE during
        // phase3 execution).
      } else {
        final Expansion_ ntexp = ntprod.getExpansion();
        retval = minimumSize(ntexp);
      }
    } else if (e instanceof Choice) {
      int min = oldMin;
      Expansion_ nested_e;
      final Choice e_nrw = (Choice) e;
      for (int i = 0; min > 1 && i < e_nrw.getChoices().size(); i++) {
        nested_e = (e_nrw.getChoices().get(i));
        final int min1 = minimumSize(nested_e, min);
        if (min > min1)
          min = min1;
      }
      retval = min;
    } else if (e instanceof Sequence) {
      int min = 0;
      final Sequence e_nrw = (Sequence) e;
      // We skip the first element in the following iteration since it is the
      // Lookahead object.
      for (int i = 1; i < e_nrw.units.size(); i++) {
        final Expansion_ eseq = (e_nrw.units.get(i));
        final int mineseq = minimumSize(eseq);
        if (min == Integer.MAX_VALUE || mineseq == Integer.MAX_VALUE) {
          min = Integer.MAX_VALUE; // Adding infinity to something results in infinity.
        } else {
          min += mineseq;
          if (min > oldMin)
            break;
        }
      }
      retval = min;
    } else if (e instanceof TryBlock) {
      final TryBlock e_nrw = (TryBlock) e;
      retval = minimumSize(e_nrw.exp);
    } else if (e instanceof OneOrMore) {
      final OneOrMore e_nrw = (OneOrMore) e;
      retval = minimumSize(e_nrw.expansion);
    } else if (e instanceof ZeroOrMore) {
      retval = 0;
    } else if (e instanceof ZeroOrOne) {
      retval = 0;
    } else if (e instanceof Lookahead) {
      retval = 0;
    } else if (e instanceof Action) {
      retval = 0;
    }
    e.inMinimumSize = false;
    return retval;
  }

  static void build(final PrintWriter ps) {
    NormalProduction p;
    JavaCodeProduction_ jp;
    Token t = null;

    out = ps;

    for (final Iterator<NormalProduction> prodIterator = bnfproductions.iterator(); prodIterator.hasNext();) {
      p = prodIterator.next();
      if (p instanceof JavaCodeProduction_) {
        jp = (JavaCodeProduction_) p;
        t = (jp.getReturnTypeTokens().get(0));
        printTokenSetup(t);
        ccol = 1;
        printLeadingComments(t, out);
        out.print("  " + staticOpt() + (p.getAccessMod() != null ? p.getAccessMod() + " " : ""));
        cline = t.beginLine;
        ccol = t.beginColumn;
        printTokenOnly(t, out);
        for (int i = 1; i < jp.getReturnTypeTokens().size(); i++) {
          t = (jp.getReturnTypeTokens().get(i));
          printToken(t, out);
        }
        printTrailingComments(t, out);
        out.print(" " + jp.getLhs() + "(");
        if (jp.getParameterListTokens().size() != 0) {
          printTokenSetup((jp.getParameterListTokens().get(0)));
          for (final Iterator<Token> it = jp.getParameterListTokens().iterator(); it.hasNext();) {
            t = it.next();
            printToken(t, out);
          }
          printTrailingComments(t, out);
        }
        out.print(") throws ParseException");
        for (final Iterator<List<Token>> it = jp.getThrowsList().iterator(); it.hasNext();) {
          out.print(", ");
          final List<Token> name = it.next();
          for (final Iterator<Token> it2 = name.iterator(); it2.hasNext();) {
            t = it2.next();
            out.print(t.image);
          }
        }
        out.print(" {");
        if (Options.getDebugParser()) {
          out.println("");
          out.println("    trace_call(\"" + jp.getLhs() + "\");");
          out.print("    try {");
        }
        if (jp.getCodeTokens().size() != 0) {
          printTokenSetup((jp.getCodeTokens().get(0)));
          cline--;
          printTokenList(jp.getCodeTokens(), out);
        }
        out.println("");
        if (Options.getDebugParser()) {
          out.println("    } finally {");
          out.println("      trace_return(\"" + jp.getLhs() + "\");");
          out.println("    }");
        }
        out.println("  }");
        out.println("");
      } else {
        buildPhase1Routine((BNFProduction_) p);
      }
    }

    for (int phase2index = 0; phase2index < phase2list.size(); phase2index++) {
      buildPhase2Routine((phase2list.get(phase2index)));
    }

    int phase3index = 0;

    while (phase3index < phase3list.size()) {
      for (; phase3index < phase3list.size(); phase3index++) {
        setupPhase3Builds((phase3list.get(phase3index)));
      }
    }

    for (final Enumeration<Phase3Data> enumeration = phase3table.elements(); enumeration.hasMoreElements();) {
      buildPhase3Routine((enumeration.nextElement()), false);
    }

  }

  public static void reInit() {
    out = null;
    gensymindex = 0;
    indentamt = 0;
    jj2LA = false;
    phase2list = new ArrayList<>();
    phase3list = new ArrayList<>();
    phase3table = new Hashtable<>();
    firstSet = null;
    xsp_declared = false;
    jj3_expansion = null;
  }

}

/**
 * Stores information to pass from phase 2 to phase 3.
 */
class Phase3Data {

  /** This is the expansion to generate the jj3 method for */
  Expansion_ exp;

  /**
   * The number of tokens that can still be consumed. This number is used to limit the number of jj3
   * methods generated
   */
  int        count;

  /**
   * Constructor with parameters
   *
   * @param e - the node
   * @param c - the number of tokens
   */
  Phase3Data(final Expansion_ e, final int c) {
    exp = e;
    count = c;
  }

}

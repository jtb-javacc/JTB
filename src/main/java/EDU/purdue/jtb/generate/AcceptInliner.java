package EDU.purdue.jtb.generate;

import static EDU.purdue.jtb.common.Constants.genNodeVar;
import static EDU.purdue.jtb.common.Constants.genNodeVarDot;
import static EDU.purdue.jtb.common.Constants.genRetVar;
import static EDU.purdue.jtb.common.Constants.iNode;
import static EDU.purdue.jtb.common.Constants.nodeChoice;
import static EDU.purdue.jtb.common.Constants.nodeList;
import static EDU.purdue.jtb.common.Constants.nodeListOptional;
import static EDU.purdue.jtb.common.Constants.nodeOptional;
import static EDU.purdue.jtb.common.Constants.nodeSequence;
import static EDU.purdue.jtb.parser.syntaxtree.JTBParserNodeConstants.JTB_SIG_EXPANSION;
import static EDU.purdue.jtb.parser.syntaxtree.JTBParserNodeConstants.JTB_SIG_EXPANSIONCHOICES;
import static EDU.purdue.jtb.parser.syntaxtree.JTBParserNodeConstants.JTB_SIG_EXPANSIONUNIT;
import static EDU.purdue.jtb.parser.syntaxtree.JTBParserNodeConstants.JTB_SIG_EXPANSIONUNITTCF;
import EDU.purdue.jtb.analyse.GlobalDataBuilder;
import EDU.purdue.jtb.common.JavaBranchPrinter;
import EDU.purdue.jtb.common.Messages;
import EDU.purdue.jtb.common.ProgrammaticError;
import EDU.purdue.jtb.common.Spacing;
import EDU.purdue.jtb.common.UserClassInfo;
import EDU.purdue.jtb.common.UserClassInfo.FieldInfo;
import EDU.purdue.jtb.common.VisitorInfo;
import EDU.purdue.jtb.parser.Token;
import EDU.purdue.jtb.parser.syntaxtree.Expansion;
import EDU.purdue.jtb.parser.syntaxtree.ExpansionChoices;
import EDU.purdue.jtb.parser.syntaxtree.ExpansionUnit;
import EDU.purdue.jtb.parser.syntaxtree.ExpansionUnitTCF;
import EDU.purdue.jtb.parser.syntaxtree.INode;
import EDU.purdue.jtb.parser.syntaxtree.NodeChoice;
import EDU.purdue.jtb.parser.syntaxtree.NodeList;
import EDU.purdue.jtb.parser.syntaxtree.NodeListOptional;
import EDU.purdue.jtb.parser.syntaxtree.NodeOptional;
import EDU.purdue.jtb.parser.syntaxtree.NodeSequence;
import EDU.purdue.jtb.parser.visitor.signature.NodeFieldsSignature;

/**
 * The {@link AcceptInliner} visitor (an extension of {@link JavaCCPrinter visitor}) is called by
 * {@link VisitorsGenerator} (which calls
 * {@link #genAcceptMethods(StringBuilder, Spacing, UserClassInfo, VisitorInfo)} to "inline" the accept
 * methods on the user classes nodes.<br>
 * This facilitates the user customization work by preparing all the lines of code the user wants to keep or
 * to modify. CODEJAVA
 * <p>
 * Intermediate variables are generated within the visit methods to walk into the syntax tree of the
 * production each method visits.<br>
 * They are of the proper type (always for the {@link NodeList}, {@link NodeListOptional},
 * {@link NodeOptional}, {@link NodeSequence}, {@link Token} types, or the production type if at the first
 * level), or of the {@link INode} type for a production type below the first level.<br>
 * They are generated (through the {@link AcceptInliner#LONGNAMES} build constant flag) in a long format
 * (reflecting all the types and levels of the parent variables) or in a short one (reflecting only their
 * types).
 * <p>
 * This visitor maintains state (for a grammar), is supposed to be run once and not supposed to be run in
 * parallel threads (on the same grammar).
 * </p>
 * TESTCASE some to add
 *
 * @author Marc Mazas
 * @version 1.4.0 : 05-08/2009 : MMa : creation
 * @version 1.4.2 : 20/02/2010 : MMa : fixed inlining issue in {@link #visit(ExpansionChoices)}
 * @version 1.4.3.1 : 20/04/2010 : MMa : fixed descriptive field name issues in
 *          {@link #visit(ExpansionChoices)} and {@link #visit(Expansion)}
 * @version 1.4.3.2 : 26/04/2010 : MMa : fixed index issue in {@link #visit (Expansion)}
 * @version 1.4.7 : xx/05/2012 : MMa : fixed issues in {@link #visit(ExpansionUnitTCF)}<br>
 *          1.4.7 : 07/2012 : MMa : followed changes in jtbgram.jtb (IndentifierAsString())<br>
 *          1.4.7 : 08-09/2012 : MMa : fixed generation problems on variables for ExpansionUnitTCF and
 *          ExpansionUnit (bug JTB-2), generated sub comments, extracted constants ; added the reference to
 *          the {@link GlobalDataBuilder}
 * @version 1.4.8 : 10/2012 : MMa : added JavaCodeProduction class generation if requested<br>
 *          1.4.8 : 12/2014 : MMa : added variables short names generation ;<br>
 *          commented visit(IdentifierAsString) as it seems not used ; improved some debug printing ; applied
 *          changes following new class {@link FieldInfo}
 * @version 1.5.0 : 02-06/2017 : MMa : added generation of throwing an exception for invalid switch values ;
 *          enhanced to VisitorInfo based visitor generation ; added final in ExpansionUnitTCF's catch 1.5.0 :
 *          04/2021 : MMa : added null conditions on accept calls in TCF ; removed NodeTCF related code
 *          11/2022 : MMa : removed NodeTCF related code, fixed many issues while improving test grammars
 * @version 1.5.1 : 08/2023 : MMa : added optional annotations in catch of ExpansionUnitTCF ; fixed node
 *          choice generation issue; editing changes for coverage analysis; changes due to the NodeToken
 *          replacement by Token
 * @version 1.5.3 : 11/2025 : MMa : signature code made independent of parser
 */
public class AcceptInliner extends JavaCCPrinter {

  /** The processed {@link UserClassInfo} */
  private UserClassInfo        uci;
  /** The {@link UserClassInfo} field number */
  private int                  fn;
  /** The sub comment number */
  private int                  scn;
  /** The visitor's VisitorInfo to generate */
  private VisitorInfo          vi;
  /** The JTB node reference type (e.g. NodeChoice, INode, ...) */
  private String               type;
  /** The variable (e.g. nF0, nF0Ch, ...) to refer to the JTB node reference with proper cast */
  private String               var;
  /**
   * The JTB node reference (e.g. n.f0, nF0.choice, ...) (the previous level 'var' variable qualified with one
   * field)
   */
  private String               ref;
  /**
   * The number of the current ExpansionUnit (not a LocalLookahead nor Block) in the list of ExpansionUnits
   */
  private int                  nbEu      = 0;
  /**
   * The index of the current ExpansionUnit (not a LocalLookahead nor Block) in the list of ExpansionUnits
   * giving a node
   */
  private int                  ixEuOk    = 0;
  /** The loop variables (depth) index : 0, 1, 2, ... -> i, i1, i2, ... */
  private int                  loopIx;
  /** The cases index : -1 for none, or 0, 1, 2, ... */
  private int                  caseIx;
  /**
   * The flag to govern the variable names scheme : true for long names, false for short ones<br>
   * (for the moment the choice is made by myself and not the user ; a new option would be needed)
   */
  private static final boolean LONGNAMES = false;
  /**
   * The {@link INode} variables index : 0, 1, 2, ... -> N, N1, N2, ... for variables long names and nd, nd1,
   * nd2, ... for variables short names
   */
  private int                  indIx;
  /**
   * The {@link NodeSequence} variables index : 0, 1, 2, ... -> S, S1, S2, ... for variables long names and
   * seq, seq1, seq2, ... for variables short names
   */
  private int                  seqIx;
  /**
   * The {@link NodeList} variables index : 0, 1, 2, ... -> L, L1, L2, ... for variables long names and lst,
   * lst1, lst2, ... for variables short names
   */
  private int                  listIx;
  /**
   * The {@link NodeListOptional} variables index : 0, 1, 2, ... -> T, T1, T2, ... for variables long names
   * and nlo, nlo1, nlo2, ... for variables short names
   */
  private int                  listOptIx;
  /**
   * The {@link NodeOptional} variables index : 0, 1, 2, ... -> P, P1, P2, ... for variables long names
   */
  private int                  optIx;
  /** The {@link NodeChoice} index 0, 1, 2, ... -> nch, nch1, nch2, ... for variables short names */
  private int                  nchJx;
  /**
   * The {@link INode} choice index 0, 1, 2, ... -> ich, ich1, ich2, ... for variables short names
   */
  private int                  ichJx;
  /**
   * The {@link NodeOptional} index 0, 1, 2, ... -> opt, opt1, opt2, ... for variables short names
   */
  private int                  optJx;
  /**
   * The {@link NodeListOptional} elementAt(i) variables index : 0, 1, 2, ... -> nloeai, nloeai1, nloeai2, ...
   * for variables short names
   */
  private int                  nloeaiJx;
  /**
   * The Expansion level we are in : 0, 1, ... : first, second, ... ; incremented at each level, except in an
   * ExpansionChoice with no choice and in ExpansionUnitTCF
   */
  private int                  expLvl;
  /** The ExpansionUnitTCF level we are in : -1 : none; 0, 1, ... : first, second, ... */
  private int                  tcfLvl;

  /** The node class debug comment prefix */
  {
    JJNCDCP = " //ai ";
  }

  /**
   * Constructor which does nothing.
   *
   * @param aGdbv - the {@link GlobalDataBuilder} visitor
   * @param aCcg - the {@link CommonCodeGenerator}
   */
  AcceptInliner(final GlobalDataBuilder aGdbv, final CommonCodeGenerator aCcg) {
    super(aGdbv, aCcg);
    jopt = aGdbv.jopt;
  }

  /**
   * Generates the accept methods for all the node tree.<br>
   *
   * @param aSb - the buffer to write into (must be allocated)
   * @param aSpc - an indentation (must be valid)
   * @param aCI - the ClassInfo to work on (must be fully initialized, in particular field comments)
   * @param aVi - a VisitorInfo defining the visitor to generate
   */
  void genAcceptMethods(final StringBuilder aSb, final Spacing aSpc, final UserClassInfo aCI,
      final VisitorInfo aVi) {
    sb = aSb;
    spc = aSpc;
    expLvl = 0;
    jbp = new JavaBranchPrinter(gdbv.jopt, spc);
    uci = aCI;
    fn = 0;
    scn = 0;
    vi = aVi;
    type = "";
    var = ref = genNodeVar;
    loopIx = indIx = seqIx = listIx = listOptIx = optIx = 0;
    nchJx = ichJx = optJx = 0;
    caseIx = -1;
    tcfLvl = -1;
    ixEuOk = 0;
    aCI.astEcNode.accept(this);
  }

  /*
   * Convenience methods
   */

  // /**
  // * Outputs the current field comment or sub comment depending on the tcf level.
  // *
  // * @param aStr - a label to output
  // */
  // @SuppressWarnings("unused")
  // private void outputComment(final String aStr) {
  // if (tcfLvl == 0) {
  // outputFieldComment("tcfLvl == 0, expLvl = " + expLvl + ", " + aStr);
  // } else if (tcfLvl > 0) {
  // outputSubComment("tcfLvl > 0, " + aStr);
  // }
  // }

  /**
   * Outputs the current field comment on a line.
   *
   * @param aStr - a label to output
   */
  private void outputFieldComment(final String aStr) {
    ccg.fmtOneJavaCodeFieldCmt(sb, spc, fn, aStr, uci);
  }

  /**
   * Outputs the current sub comment on a line.
   *
   * @param aStr - a label to output
   */
  private void outputSubComment(final String aStr) {
    CommonCodeGenerator.fmtOneJavaCodeSubCmt(sb, spc, scn, aStr, uci);
    scn++;
  }

  /*
   * User grammar generated and overridden visit methods below
   */

  /**
   * Visits a {@link ExpansionChoices} node, whose children are the following :
   * <p>
   * f0 -> Expansion()<br>
   * f1 -> ( #0 "|" #1 Expansion() )*<br>
   * s: -1726831935<br>
   *
   * @param n - the node to visit
   */
  @Override
  @NodeFieldsSignature(old_sig = -1726831935, new_sig = JTB_SIG_EXPANSIONCHOICES, name = "ExpansionChoices")
  public void visit(final ExpansionChoices n) {
    String oldRef = ref;
    String oldVar = var;
    String oldType = type;
    int oldCaseIx;
    int oldIxEuOk;

    oneDebugClassNewLine(n,
        "a, expLvl = " + expLvl + ", loopIx = " + loopIx + ", fn = " + fn + ", tcfLvl = " + tcfLvl,
        ", ufs = " + uci.fields.size());

    // only f0
    if (!n.f1.present()) {
      oneDebugClassNewLine(n, "b, only f0, ref = " + ref + ", var = " + var + ", type = " + type
          + ", caseIx = " + caseIx + ", ixEuOk = " + ixEuOk);

      // visit Expansion()
      oldCaseIx = caseIx = -1;
      oldIxEuOk = ixEuOk;
      n.f0.accept(this);
      ixEuOk = oldIxEuOk;

      oneDebugClassNewLine(n, "c, only f0, ixEuOk = " + ixEuOk);

      return;
    }

    // f0 and f1 : generate switch choice

    // f0 -> Expansion() : generate variables and case 0

    boolean genNode = true;
    String var1 = null;

    if (expLvl == 0) {
      genNode = fn < uci.fields.size();
      if (genNode) {
        final FieldInfo field = uci.fields.get(fn);
        type = field.fixedType;
        if (LONGNAMES) {
          var = var.concat(String.valueOf(fn)).concat("C");
        } else {
          var = "nch";
          // at expLvl 0 nchJx is always 0
          nchJx++;
        }
        ref = ref.concat(".").concat(field.name);
        outputFieldComment("ExpansionChoices d");
        sb.append(spc.spc).append("final ").append(type).append(' ').append(var).append(" = ").append(ref)
            .append(';');
        oneNewLine(n, "d, expLvl == 0");
        ref = var.concat(".choice");
        var1 = var;
        fn++;
      }
    } else if ((tcfLvl >= 0) //
        && "".equals(type)) {
      genNode = fn < uci.fields.size();
      if (genNode) {
        // case within TCF at first level
        var = genNodeVarDot.concat(uci.fields.get(fn).name);
        type = nodeChoice;
        var1 = var;
        ref = var1.concat(".choice");
        fn++;
      }
    } else {
      // all other cases : not within TCF, or within TCF not at first level
      if (nodeSequence.equals(type)) {
        ref = var.concat(".elementAt(").concat(String.valueOf(ixEuOk)).concat(");");
        outputSubComment("ExpansionChoice, elementAt");
      }
      if (LONGNAMES) {
        var += "C";
      } else {
        var = "nch";
        if (nchJx > 0) {
          var += nchJx;
        }
        nchJx++;
      }
      sb.append(spc.spc).append("final ").append(nodeChoice).append(' ').append(var).append(" = ");
      if (!nodeChoice.equals(type)) {
        sb.append('(').append(nodeChoice).append(") ");
      }
      sb.append(ref).append(';');
      oneNewLine(n, "e, expLvl (" + expLvl + ") != 0 && type (", type, ") --> ", nodeChoice);
      type = nodeChoice;
      ref = var.concat(".choice");
      var1 = var;
    }

    if (genNode) {

      if (tcfLvl >= 0) {
        sb.append(spc.spc).append("if (").append(var).append(" != null) {");
        oneNewLine(n, "f, if (tcfLvl >= 0)");
        spc.updateSpc(+1);
      }

      if (LONGNAMES) {
        var += "H";
      } else {
        var = "ich";
        if (ichJx > 0) {
          var += ichJx;
        }
        ichJx++;
      }
      sb.append(spc.spc).append("final ").append(iNode).append(' ').append(var).append(" = ").append(ref)
          .append(';');
      oneNewLine(n, "g, choice, type (", type, ") --> ", iNode);
      sb.append(spc.spc).append("switch (").append(var1).append(".which) {");
      oneNewLine(n, "h, switch");
      type = iNode;
      ref = var;

      spc.updateSpc(+1);
      sb.append(spc.spc).append("case 0:");
      oneNewLine(n, "i, case 0, ixEuOk = " + ixEuOk);
      spc.updateSpc(+1);
      outputSubComment("ExpansionChoices, h");
    }

    // visit Expansion
    oldRef = ref;
    oldVar = var;
    oldType = type;
    oldCaseIx = caseIx = 0;
    oldIxEuOk = ixEuOk;
    ++expLvl;
    n.f0.accept(this);
    --expLvl;
    ref = oldRef;
    var = oldVar;
    type = oldType;
    caseIx = oldCaseIx;
    ixEuOk = oldIxEuOk;

    if (genNode) {
      sb.append(spc.spc).append("break;");
      oneNewLine(n, "j, break 0");
      spc.updateSpc(-1);
    }

    // f1 -> ( "|" Expansion() )* : generate other cases
    for (int i = 0; i < n.f1.size();) {
      final NodeSequence seq = (NodeSequence) n.f1.elementAt(i);
      i++;

      if (genNode) {
        sb.append(spc.spc).append("case ").append(i).append(":");
        oneNewLine(n, "k, case, ixEuOk = " + ixEuOk);
        spc.updateSpc(+1);
        outputSubComment("ExpansionChoices, j");
      }

      // visit Expansion
      oldCaseIx = caseIx = i;
      oldIxEuOk = ixEuOk;
      ++expLvl;
      seq.elementAt(1).accept(this);
      --expLvl;
      ref = oldRef;
      var = oldVar;
      type = oldType;
      caseIx = oldCaseIx;
      ixEuOk = oldIxEuOk;

      if (genNode) {
        sb.append(spc.spc).append("break;");
        oneNewLine(n, "l, break n , ixEuOk = " + ixEuOk);
        spc.updateSpc(-1);
      }
    }

    if (genNode) {
      // generate default and end of switch
      sb.append(spc.spc).append("default:");
      oneNewLine(n, "m, default");
      spc.updateSpc(+1);
      sb.append(spc.spc).append("// should not occur !!!");
      oneNewLine(n, "n, should");
      sb.append(spc.spc).append("throw new ShouldNotOccurException(").append(var1).append(");");
      oneNewLine(n, "o, throw");
      spc.updateSpc(-1);

      spc.updateSpc(-1);
      sb.append(spc.spc).append("}");
      oneNewLine(n, "p, }");

      if (tcfLvl >= 0) {
        spc.updateSpc(-1);
        sb.append(spc.spc).append("}");
        oneNewLine(n, "q, if (tcfLvl >= 0)");
      }
    }
  }

  /**
   * Visits a {@link Expansion} node, whose children are the following :
   * <p>
   * f0 -> ( #0 "LOOKAHEAD" #1 "(" #2 LocalLookahead() #3 ")" )?<br>
   * f1 -> ( ExpansionUnit() )+<br>
   * s: -2134365682<br>
   *
   * @param n - the node to visit
   */
  @Override
  @NodeFieldsSignature(old_sig = -2134365682, new_sig = JTB_SIG_EXPANSION, name = "Expansion")
  public void visit(final Expansion n) {
    // don't take f0, only f1

    // f1 -> ( ExpansionUnit() )+ : visit something within a sequence (nbEu > 1) or directly (nbEu == 1)

    // count the number of non LocalLookahead nor Block nor not to be created nodes
    nbEu = 0;
    for (final INode e : n.f1.nodes) {
      final ExpansionUnit expUnit = (ExpansionUnit) e;
      if (gdbv.getNbSubNodesTbc(expUnit) != 0) {
        nbEu++;
      }
    }
    final int sz = n.f1.size();

    oneDebugClassNewLine(n, "a, expLvl = " + expLvl + ", nbEu = " + nbEu + ", loopIx = " + loopIx + ", fn = "
        + fn + ", tcfLvl = " + tcfLvl + ", type = " + type + ", ixEuOk = " + ixEuOk + ", sz = " + sz);

    if ((tcfLvl == -1) //
        && (expLvl > 0) //
        && (nbEu > 1) //
        && !nodeSequence.equals(type)) {
      if (LONGNAMES) {
        var += "S";
      } else {
        var = "seq";
      }
      if (seqIx > 0) {
        var += seqIx;
      }
      seqIx++;
      sb.append(spc.spc).append("final ").append(nodeSequence).append(' ').append(var).append(" = (")
          .append(nodeSequence).append(") ").append(ref).append(';');
      oneNewLine(n, "b, expLvl (" + expLvl + ") > 0 && nbEu (" + nbEu + ") > 1 && !tns, type (", type,
          ") --> ", nodeSequence);
      type = nodeSequence;
    }

    final String oldRef = ref;
    final String oldVar = var;
    final String oldType = type;
    final int oldNbEu = nbEu;

    for (int i = 0; i < sz; i++) {
      final ExpansionUnit expUnit = (ExpansionUnit) n.f1.elementAt(i);
      // don't process LocalLookahead nor Block nor not to be created nodes
      if (gdbv.getNbSubNodesTbc(expUnit) != 0) {

        // generate variables except for ExpansionUnitTCF
        if (expUnit.f0.which != 3) {
          // case an ExpansionUnit not an ExpansionUnitTCF
          if ((tcfLvl >= 0) //
              && "".equals(type)) {
            // case within an ExpansionUnitTCF at first level
            if (expUnit.f0.which == 4) {
              // just a RegularExpression
              outputFieldComment("Expansion, EU type 2 (RegularExpression)");
              var = ref.concat(".").concat(uci.fields.get(fn).name);
              fn++;
            }
          } else if (expLvl == 0) {
            // cases not within TCF, or within TCF not at first level : at first Expansion level ; proper type
            final FieldInfo field = uci.fields.get(fn);
            ref = ref.concat(".").concat(field.name);
            var += fn;
            type = field.fixedType;
            outputFieldComment("Expansion, c");
            sb.append(spc.spc).append("final ").append(type).append(' ').append(var).append(" = ").append(ref)
                .append(';');
            oneNewLine(n, "c, expLvl == 0, fn = " + fn);
            ref = var;
            fn++;
          } else {
            // cases not within TCF, or within TCF not at first level : at other Expansion levels ; INode type
            if (nbEu > 1) {
              final String ixEuOkStr = String.valueOf(ixEuOk);
              if (nodeSequence.equals(type)) {
                ref = var.concat(".elementAt(").concat(ixEuOkStr).concat(")");
              } else {
                ref = "((".concat(nodeSequence).concat(") ").concat(var).concat(").elementAt(")
                    .concat(ixEuOkStr).concat(")");
              }
              if (LONGNAMES) {
                if (caseIx >= 0) {
                  var += caseIx;
                }
                var = var.concat("A").concat(ixEuOkStr);
              } else {
                var = "nd";
                if (indIx > 0) {
                  var += indIx;
                }
                indIx++;
              }
              outputSubComment("Expansion, d");
              sb.append(spc.spc).append("final ").append(iNode).append(' ').append(var).append(" = ")
                  .append(ref).append(';');
              oneNewLine(n, "d, expLvl (" + expLvl + ") != 0 && nbEu (" + nbEu + ") > 1, type (", type,
                  ") >-- ", iNode);
              type = iNode;
              ref = var;
            } else if (nbEu == 1) {
              if (nodeSequence.equals(type)) {
                final String ixEuOkStr = String.valueOf(ixEuOk);
                ref = var.concat(".elementAt(").concat(ixEuOkStr).concat(")");
                if (LONGNAMES) {
                  if (caseIx >= 0) {
                    var += caseIx;
                  }
                  var = var.concat("A").concat(ixEuOkStr);
                } else {
                  var = "nd";
                  if (indIx > 0) {
                    var += indIx;
                  }
                  indIx++;
                }
                outputSubComment("Expansion, e");
                sb.append(spc.spc).append("final ").append(iNode).append(' ').append(var).append(" = ")
                    .append(ref).append(';');
                oneNewLine(n, "e, expLvl (" + expLvl + ") != 0 && nbEu == 1, type = ", type);
                ref = var;
              }
            } else {
              // nbEu == 0 ; can occur ?
              oneDebugClassNewLine(n, "e, expLvl (" + expLvl + ") != 0 && nbEu == 0, type = ", type);
            }
          }
        } else {
          // case in ExpansionUnitTCF
          oneDebugClassNewLine(n, "g, euTCF, expLvl = " + expLvl + ", nbEu = " + nbEu + ", loopIx = " + loopIx
              + ", fn = " + fn + ", tcfLvl = " + tcfLvl + ", ixEuOk = " + ixEuOk + ", type = " + type);
        }

        // visit ExpansionUnit
        final int oldNloeaiJx = nloeaiJx;
        ++expLvl;
        expUnit.accept(this);
        --expLvl;
        ref = oldRef;
        var = oldVar;
        type = oldType;
        nbEu = oldNbEu;
        nloeaiJx = oldNloeaiJx;

        ixEuOk++;

      }
    }

  }

  /**
   * Visits a {@link ExpansionUnit} node, whose child is the following :
   * <p>
   * f0 -> . %0 #0 "LOOKAHEAD" #1 "(" #2 LocalLookahead() #3 ")"<br>
   * .. .. | %1 Block()<br>
   * .. .. | %2 #0 "[" #1 ExpansionChoices() #2 "]"<br>
   * .. .. | %3 ExpansionUnitTCF()<br>
   * .. .. | %4 #0 [ $0 PrimaryExpression() $1 "=" ]<br>
   * .. .. . .. #1 ( &0 $0 IdentifierAsString() $1 Arguments()<br>
   * .. .. . .. .. . .. $2 [ "!" ]<br>
   * .. .. . .. .. | &1 $0 RegularExpression()<br>
   * .. .. . .. .. . .. $1 [ ?0 "." ?1 < IDENTIFIER > ]<br>
   * .. .. . .. .. . .. $2 [ "!" ] )<br>
   * .. .. | %5 #0 "(" #1 ExpansionChoices() #2 ")"<br>
   * .. .. . .. #3 ( &0 "+"<br>
   * .. .. . .. .. | &1 "*"<br>
   * .. .. . .. .. | &2 "?" )?<br>
   * s: 1116287061<br>
   *
   * @param n - the node to visit
   */
  @Override
  @NodeFieldsSignature(old_sig = 1116287061, new_sig = JTB_SIG_EXPANSIONUNIT, name = "ExpansionUnit")
  public void visit(final ExpansionUnit n) {
    final NodeSequence seq;
    NodeOptional opt;
    NodeChoice ch;
    final String oldRef = ref;
    final String oldVar = var;
    final String oldType = type;
    int oldLoopIx;
    String loopIxStr;
    final int oldIxEuOk = ixEuOk;

    switch (n.f0.which) {
    case 0:
      // %0 #0 "LOOKAHEAD" #1 "(" #2 LocalLookahead() #3 ")"
      // should not be called !
      String msg = "visit ExpansionUnit type 0 should not occur !";
      Messages.hardErr(msg);
      throw new ProgrammaticError(msg);

    case 1:
      // %1 Block()
      // should not be called !
      msg = "visit ExpansionUnit type 1 should not occur !";
      Messages.hardErr(msg);
      throw new ProgrammaticError(msg);

    case 2:
      // %2 #0 "[" #1 ExpansionChoices() #2 "]"
      // visit something within a node optional
      // (similar to case 5 modifier 2)
      oneDebugClassNewLine(n,
          "2, beg, ixEuOk = " + ixEuOk + " <- 0, tcfLvl = " + tcfLvl + ", expLvl = " + expLvl);
      ixEuOk = 0;

      seq = (NodeSequence) n.f0.choice;

      if ((tcfLvl == 0) //
          && (expLvl == 2)) {
        outputFieldComment("ExpansionUnit, EU type 2 [EC]");
      }
      if ((tcfLvl >= 0) //
          && "".equals(type)) {
        type = nodeOptional;
        var = genNodeVarDot.concat(uci.fields.get(fn).name);
        fn++;
      } else if ((tcfLvl >= 0) //
          || !nodeOptional.equals(type)) {
        final String var1 = var;
        if (LONGNAMES) {
          var += "P";
        } else {
          var = "opt";
          if (optJx > 0) {
            var += optJx;
          }
          optJx++;
        }
        sb.append(spc.spc).append("final ").append(nodeOptional).append(' ').append(var).append(" = (")
            .append(nodeOptional).append(") ").append(var1).append(';');
        oneNewLine(n, "2, type (", type, ") --> ", nodeOptional);
        type = nodeOptional;
      }

      if (tcfLvl >= 0) {
        sb.append(spc.spc).append("if ((").append(var).append(" != null) && ").append(var)
            .append(".present()) {");
      } else {
        sb.append(spc.spc).append("if (").append(var).append(".present()) {");
      }
      oneNewLine(n, "2, if");
      spc.updateSpc(+1);
      ref = var.concat(".node");

      // visit ExpansionChoices
      ++expLvl;
      seq.elementAt(1).accept(this);
      --expLvl;
      ref = oldRef;
      var = oldVar;
      type = oldType;

      spc.updateSpc(-1);
      sb.append(spc.spc).append("}");
      oneNewLine(n, "2, end");
      break;

    case 3:
      // %3 ExpansionUnitTCF()
      oneDebugClassNewLine(n, "3_beg, ixEuOk = " + ixEuOk + ", tcfLvl = " + tcfLvl);
      // visit ExpansionUnitTCF
      n.f0.choice.accept(this);
      // as we visit ExpansionChoices in ExpansionUnitTCF
      ref = oldRef;
      var = oldVar;
      type = oldType;
      break;

    case 4:
      // %4 #0 [ $0 PrimaryExpression() $1 "=" ]
      // .. #1 ( &0 $0 IdentifierAsString() $1 Arguments()
      // .. .. . .. $2 [ "!" ]
      // .. .. | &1 $0 RegularExpression()
      // .. .. . .. $1 [ ?0 "." ?1 < IDENTIFIER > ]
      // .. .. . .. $2 [ "!" ] )
      seq = (NodeSequence) n.f0.choice;

      // #0 [ $0 PrimaryExpression() $1 "=" ]
      opt = (NodeOptional) seq.elementAt(0);
      if (opt.present()) {
        // we believe that we do not need to process this, as it is used in the generated .jj, but it does
        // not look it is useful in the visitors
        // if yes, we would need to generate the first Block() of a BnfProduction() (for the variable
        // declaration),
        // generate this assignment after the accept method, using a generated return value
        // sb.append(spc.spc);
        // ((NodeSequence) opt.node).elementAt(0).accept(this);
        // sb.append(" = ");
        // sb.append("// Please report to support : TO DO #0 [ $0 PrimaryExpression() $1 \"=\" ]");
        // oneNewLine(n, "4_._PrimaryExpression()");
      }

      // #1 (&0 ... | &1 ...)
      ch = (NodeChoice) seq.elementAt(1);
      final NodeSequence seq1 = (NodeSequence) ch.choice;
      if (ch.which == 0) {
        if (!((NodeOptional) seq1.elementAt(2)).present()) {
          // $0 IdentifierAsString() $1 Arguments() $2 [ "!" ]
          // generate node creation if not requested not to do so
          if (jopt.depthLevel) {
            CommonCodeGenerator.increaseDepthLevel(sb, spc);
          }
          if (tcfLvl >= 0) {
            sb.append(spc.spc).append("if (").append(var).append(" != null) {");
            oneNewLine(n, "4_0_a_IdentifierAsString");
            spc.updateSpc(+1);
          }
          sb.append(spc.spc);
          if (!vi.retInfo.isVoid) {
            sb.append(genRetVar).append(" = ");
          }
          sb.append(var).append(".accept(this").append(vi.userArguments).append(");");
          oneNewLine(n, "4_0_b_IdentifierAsString");
          if (jopt.depthLevel) {
            CommonCodeGenerator.decreaseDepthLevel(sb, spc);
          }
          if (tcfLvl >= 0) {
            spc.updateSpc(-1);
            sb.append(spc.spc).append("}");
            oneNewLine(n, "4_0_c_IdentifierAsString");
          }
        }
      } else {
        // $0 RegularExpression() $1 [ ?0 "." ?1 < IDENTIFIER > ] $2 [ "!" ]
        if (!((NodeOptional) seq1.elementAt(2)).present()) {
          // generate node creation if not requested not to do so
          if (jopt.depthLevel) {
            CommonCodeGenerator.increaseDepthLevel(sb, spc);
          }
          if (tcfLvl >= 0) {
            sb.append(spc.spc).append("if (").append(var).append(" != null) {");
            oneNewLine(n, "4_1_a_RegularExpression");
            spc.updateSpc(+1);
          }
          sb.append(spc.spc);
          if (!vi.retInfo.isVoid) {
            sb.append(genRetVar).append(" = ");
          }
          sb.append(var).append(".accept(this").append(vi.userArguments).append(");");
          oneNewLine(n, "4_1_b_RegularExpression");
          if (jopt.depthLevel) {
            CommonCodeGenerator.decreaseDepthLevel(sb, spc);
          }
          if (tcfLvl >= 0) {
            spc.updateSpc(-1);
            sb.append(spc.spc).append("}");
            oneNewLine(n, "4_1_c_RegularExpression");
          }
          // $1 [ ?0 "." ?1 < IDENTIFIER > ]
          opt = (NodeOptional) seq1.elementAt(1);
          if (opt.present()) {
            sb.append("// Please report to support with a real example of your grammar "
                + "if you want the suffix to be generated : $1 [ ?0 \".\" ?1 < IDENTIFIER > ]");
            oneNewLine(n, "4_._<_IDENTIFIER_>");
          }
        }
      }
      break;

    case 5:
      // %5 #0 "(" #1 ExpansionChoices() #2 ")"
      // .. #3 ( &0 "+" | &1 "*" | &2 "?" )?
      // visit something with a node list for +, with a node list optional for *,
      // with a node optional for ?, and with a node sequence or a to be found deeper for nothing
      oneDebugClassNewLine(n, "5_beg, ixEuOk = " + ixEuOk + " <- 0");
      ixEuOk = 0;

      seq = (NodeSequence) n.f0.choice;

      // #3 ( &0 "+" | &1 "*" | &2 "?" )?
      opt = (NodeOptional) seq.elementAt(3);
      if (opt.present()) {
        ch = (NodeChoice) opt.node;
        if (ch.which == 0) {
          // &0 "+" modifier : visit something within a node list
          oneDebugClassNewLine(n, "5_+ beg; tcfLvl = " + tcfLvl + ", expLvl = " + expLvl);

          String var1 = var;
          if ((tcfLvl == 0) //
              && (expLvl == 2)) {
            outputFieldComment("ExpansionUnit, EU type 5_+ (EC)+");
          }
          if ((tcfLvl >= 0) //
              && "".equals(type)) {
            type = "NodeList";
            var = uci.fields.get(fn).name;
            fn++;
            ref = genNodeVarDot.concat(var);
            var1 = ref;
          } else if ((tcfLvl >= 0) //
              || !"NodeList".equals(type)) {
            ref = var;
            if (LONGNAMES) {
              var = var.concat("L");
            } else {
              var = "lst";
            }
            if (listIx > 0) {
              var += listIx;
            }
            listIx++;
            var1 = var;
            sb.append(spc.spc).append("final ").append(nodeList).append(' ').append(var).append(" = (")
                .append(nodeList).append(") ").append(ref).append(';');
            oneNewLine(n, "tcf || type (", type, ") --> ", nodeList);
            type = nodeList;
          }

          oldLoopIx = loopIx;
          loopIxStr = loopIx == 0 ? "" : String.valueOf(loopIx);
          loopIx++;
          sb.append(spc.spc).append("for (int i").append(loopIxStr).append(" = 0; i").append(loopIxStr)
              .append(" < ").append(var1).append(".size(); i").append(loopIxStr).append("++) {");
          oneNewLine(n, "5_+_for");
          spc.updateSpc(+1);

          ref = var1.concat(".elementAt(i").concat(String.valueOf(loopIxStr)).concat(")");
          if (LONGNAMES) {
            var = var.concat("Ei");
          } else {
            var = "lsteai";
          }
          if (nloeaiJx > 0) {
            var += nloeaiJx;
          }
          nloeaiJx++;
          sb.append(spc.spc).append("final ").append(iNode).append(' ').append(var).append(" = ").append(ref)
              .append(';');
          oneNewLine(n, "5_+_elem");
          ref = var;
          type = iNode;

          // visit ExpansionChoices
          ++expLvl;
          seq.elementAt(1).accept(this);
          --expLvl;
          ref = oldRef;
          var = oldVar;
          type = oldType;
          loopIx = oldLoopIx;

          spc.updateSpc(-1);
          sb.append(spc.spc).append("}");
          oneNewLine(n, "5_+_}");

        } else if (ch.which == 1) {
          // &1 "*" modifier : visit something within a node list optional
          oneDebugClassNewLine(n, "5_*, beg; tcfLvl = " + tcfLvl + ", expLvl = " + expLvl);

          ref = var;
          if ((tcfLvl == 0) //
              && (expLvl == 2)) {
            outputFieldComment("ExpansionUnit, EU type 5_* (EC)*");
          }
          if ((tcfLvl >= 0) //
              && "".equals(type)) {
            type = nodeListOptional;
            var = genNodeVarDot.concat(uci.fields.get(fn).name);
            fn++;
          } else if ((tcfLvl >= 0) //
              || !nodeListOptional.equals(type)) {
            if (LONGNAMES) {
              var = var.concat("T");
            } else {
              var = "nlo";
            }
            if (listOptIx > 0) {
              var += listOptIx;
            }
            listOptIx++;
            sb.append(spc.spc).append("final ").append(nodeListOptional).append(' ').append(var)
                .append(" = (").append(nodeListOptional).append(") ").append(ref).append(';');
            oneNewLine(n, "5_tcf || type (", type, ") --> ", nodeListOptional);
            type = nodeListOptional;
          }

          if (tcfLvl >= 0) {
            sb.append(spc.spc).append("if ((").append(var).append(" != null) && ").append(var)
                .append(".present()) {");
          } else {
            sb.append(spc.spc).append("if (").append(var).append(".present()) {");
          }
          oneNewLine(n, "5_*_if");
          spc.updateSpc(+1);

          oldLoopIx = loopIx;
          loopIxStr = loopIx == 0 ? "" : String.valueOf(loopIx);
          loopIx++;
          sb.append(spc.spc).append("for (int i").append(loopIxStr).append(" = 0; i").append(loopIxStr)
              .append(" < ").append(var).append(".size(); i").append(loopIxStr).append("++) {");
          oneNewLine(n, "5_*_for");
          spc.updateSpc(+1);

          ref = var.concat(".elementAt(i").concat(String.valueOf(loopIxStr)).concat(")");
          if (LONGNAMES) {
            var = var.concat("Mi");
          } else {
            var = "nloeai";
          }
          if (nloeaiJx > 0) {
            var += nloeaiJx;
          }
          nloeaiJx++;
          if (var.startsWith(genNodeVarDot)) {
            var = var.substring(genNodeVarDot.length());
          }
          sb.append(spc.spc).append("final ").append(iNode).append(' ').append(var).append(" = ").append(ref)
              .append(';');
          oneNewLine(n, "5_*_elem");
          ref = var;

          // visit ExpansionChoices
          ++expLvl;
          seq.elementAt(1).accept(this);
          --expLvl;
          ref = oldRef;
          var = oldVar;
          type = oldType;
          loopIx = oldLoopIx;

          spc.updateSpc(-1);
          sb.append(spc.spc).append("}");
          oneNewLine(n, "5_*_}_1");

          spc.updateSpc(-1);
          sb.append(spc.spc).append("}");
          oneNewLine(n, "5_*_}_2");

        } else {
          // &2 "?" modifier : visit something within a node optional
          // (similar to case 2)
          oneDebugClassNewLine(n, "5_?, beg; tcfLvl = " + tcfLvl + ", expLvl = " + expLvl);

          if ((tcfLvl == 0) //
              && (expLvl == 2)) {
            outputFieldComment("ExpansionUnit, EU type 5_? (EC)?");
          }
          if ((tcfLvl >= 0) //
              && "".equals(type)) {
            type = nodeOptional;
            var = genNodeVarDot.concat(uci.fields.get(fn).name);
            fn++;
          } else if ((tcfLvl >= 0) //
              || !nodeOptional.equals(type)) {
            final String var1 = var;
            if (LONGNAMES) {
              var = var.concat("P");
              if (optIx > 0) {
                var += optIx;
              }
              optIx++;
            } else {
              var = "opt";
              if (optJx > 0) {
                var += optJx;
              }
              optJx++;
            }
            sb.append(spc.spc).append("final ").append(nodeOptional).append(' ').append(var).append(" = (")
                .append(nodeOptional).append(") ").append(var1).append(';');
            oneNewLine(n, "5_?, type (", type, ") --> ", nodeOptional);
            type = nodeOptional;
          }

          if (tcfLvl >= 0) {
            sb.append(spc.spc).append("if ((").append(var).append(" != null) && ").append(var)
                .append(".present()) {");
          } else {
            sb.append(spc.spc).append("if (").append(var).append(".present()) {");
          }
          oneNewLine(n, "5_?, if");
          spc.updateSpc(+1);
          ref = var.concat(".node");

          // visit ExpansionChoices
          ++expLvl;
          seq.elementAt(1).accept(this);
          --expLvl;
          ref = oldRef;
          var = oldVar;
          type = oldType;

          spc.updateSpc(-1);
          sb.append(spc.spc).append("}");
          oneNewLine(n, "5_?, end");
        }

      } else {
        // no modifier : visit something with a node sequence
        oneDebugClassNewLine(n, "5_no, beg; tcfLvl = " + tcfLvl + ", expLvl = " + expLvl);

        if ((tcfLvl == 0) //
            && (expLvl == 2)) {
          outputFieldComment("ExpansionUnit, EU type 5 (EC)");
        }
        if ((tcfLvl >= 0) //
            && "".equals(type) //
            && !((ExpansionChoices) seq.elementAt(1)).f1.present()) {
          if (LONGNAMES) {
            var = var.concat("Tcf");
          } else {
            var = "tcf";
          }
          if (fn != 0) {
            var += fn;
          }
          ref += ".".concat(uci.fields.get(fn).name);
          fn++;
          sb.append(spc.spc).append("final ").append(nodeSequence).append(' ').append(var).append(" = ")
              .append(ref).append(';');
          oneNewLine(n, "tcf, expLvl = ", String.valueOf(expLvl), ", type (", type, ") --> ", nodeSequence);
          type = nodeSequence;
          ref = var;
        }

        // visit ExpansionChoices
        ++expLvl;
        seq.elementAt(1).accept(this);
        --expLvl;
        ref = oldRef;
        var = oldVar;
        type = oldType;
      }
      break;

    default:
      msg = "Invalid n.f0.which = " + n.f0.which;
      Messages.hardErr(msg);
      throw new ProgrammaticError(msg);

    }

    ixEuOk = oldIxEuOk;

  }

  /**
   * Visits a {@link ExpansionUnitTCF} node, whose children are the following :
   * <p>
   * f0 -> "try"<br>
   * f1 -> "{"<br>
   * f2 -> ExpansionChoices()<br>
   * f3 -> "}"<br>
   * f4 -> ( #0 "catch" #1 "("<br>
   * .. .. . #2 ( Annotation() )*<br>
   * .. .. . #3 [ "final" ]<br>
   * .. .. . #4 Name() #5 < IDENTIFIER > #6 ")" #7 Block() )*<br>
   * f5 -> [ #0 "finally" #1 Block() ]<br>
   * s: 1601707097<br>
   *
   * @param n - the node to visit
   */
  @Override
  @NodeFieldsSignature(old_sig = 1601707097, new_sig = JTB_SIG_EXPANSIONUNITTCF, name = "ExpansionUnitTCF")
  public void visit(final ExpansionUnitTCF n) {
    tcfLvl++;
    if (tcfLvl > 0) {
      outputSubComment("ExpansionUnitTCF");
    }
    n.f2.accept(this);
    tcfLvl--;
  }

}

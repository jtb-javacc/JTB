package EDU.purdue.jtb.visitor;

import static EDU.purdue.jtb.misc.Globals.*;

import java.util.Iterator;

import EDU.purdue.jtb.misc.ClassInfo;
import EDU.purdue.jtb.misc.DepthFirstVisitorsGenerator;
import EDU.purdue.jtb.misc.JavaBranchPrinter;
import EDU.purdue.jtb.misc.Messages;
import EDU.purdue.jtb.misc.Spacing;
import EDU.purdue.jtb.syntaxtree.Expansion;
import EDU.purdue.jtb.syntaxtree.ExpansionChoices;
import EDU.purdue.jtb.syntaxtree.ExpansionUnit;
import EDU.purdue.jtb.syntaxtree.ExpansionUnitTCF;
import EDU.purdue.jtb.syntaxtree.INode;
import EDU.purdue.jtb.syntaxtree.NodeChoice;
import EDU.purdue.jtb.syntaxtree.NodeList;
import EDU.purdue.jtb.syntaxtree.NodeListOptional;
import EDU.purdue.jtb.syntaxtree.NodeOptional;
import EDU.purdue.jtb.syntaxtree.NodeSequence;
import EDU.purdue.jtb.syntaxtree.NodeToken;

/**
 * The {@link AcceptInliner} visitor (an extension of {@link JavaCCPrinter visitor}) is called by
 * {@link DepthFirstVisitorsGenerator} (which calls
 * {@link #genAcceptMethods(StringBuilder, Spacing, ClassInfo, boolean, boolean)} to "inline" the
 * accept methods on the base classes nodes (in order to facilitate the user customization work by
 * preparing all the lines of code the user wants to keep or to modify).
 * <p>
 * Intermediate variables are generated within the visit methods to walk into the syntax tree of the
 * production each method visits. They are of the proper type (always for the {@link NodeList},
 * {@link NodeListOptional}, {@link NodeOptional}, {@link NodeSequence}, {@link NodeToken} types, or
 * the production type if at the first level), or of the {@link INode} type for a production type
 * below the first level. They are generated (through the {@link AcceptInliner#LONGNAMES} build
 * constant flag) in a long format (reflecting all the types and levels of the parent variables) or
 * in a short one (reflecting only their types).
 * <p>
 * 
 * @author Marc Mazas
 * @version 1.4.0 : 05-08/2009 : MMa : creation
 * @version 1.4.2 : 20/02/2010 : MMa : fixed inlining issue in {@link #visit(ExpansionChoices)}
 * @version 1.4.3.1 : 20/04/2010 : MMa : fixed descriptive field name issues in
 *          {@link #visit(ExpansionChoices)} and {@link #visit(Expansion)}
 * @version 1.4.3.2 : 26/04/2010 : MMa : fixed index issue in {@link #visit (Expansion)}
 * @version 1.4.7 : xx/05/2012 : MMa : fixed issues in {@link #visit(ExpansionUnitTCF)}<br>
 *          1.4.7 : 07/2012 : MMa : followed changes in jtbgram.jtb (IndentifierAsString())<br>
 *          1.4.7 : 08-09/2012 : MMa : fixed generation problems on variables for ExpansionUnitTCF
 *          and ExpansionUnit (bug JTB-2), generated sub comments, extracted constants ; added the
 *          reference to the {@link GlobalDataBuilder}
 * @version 1.4.8 : 10/2012 : MMa : added JavaCodeProduction class generation if requested<br>
 *          1.4.8 : 12/2014 : MMa : added variables short names generation ;<br>
 *          commented visit(IdentifierAsString) as it seems not used ; improved some debug printing
 */
public class AcceptInliner extends JavaCCPrinter {

  /** The processed {@link ClassInfo} */
  ClassInfo            ci;
  /** The {@link ClassInfo} field number */
  int                  fn;
  /** The sub comment number */
  int                  scn;
  /** The user return type flag */
  boolean              ret;
  /** The user argument flag */
  boolean              argu;
  /** The JTB node reference type (e.g. NodeChoice, INode, ...) */
  String               type;
  /** The variable (e.g. nF0, nF0Ch, ...) to refer to the JTB node reference with proper cast */
  String               var;
  /**
   * The JTB node reference (e.g. n.f0, nF0.choice, ...) (the previous level 'var' variable
   * qualified with one field)
   */
  String               ref;
  /**
   * The number of the current ExpansionUnit (not a LocalLookahead nor Block) in the list of
   * ExpansionUnits
   */
  int                  nbEu      = 0;
  /** The loop variables (depth) index : 0, 1, 2, ... -> i, i1, i2, ... */
  int                  loopIx;
  /** The cases index : -1 for none, or 0, 1, 2, ... */
  int                  caseIx;
  /**
   * The flag to govern the variable names scheme : true for long names, false for short ones<br>
   * (for the moment the choice is made by myself and not the user ; a new option would be needed)
   */
  final static boolean LONGNAMES = false;
  /**
   * The {@link NodeSequence} variables index : 0, 1, 2, ... -> S, S1, S2, ... for variables long
   * names and seq, seq1, seq2, ... for variables short names
   */
  int                  seqIx;
  /**
   * The {@link NodeList} variables index : 0, 1, 2, ... -> L, L1, L2, ... for variables long names
   * and lst, lst1, lst2, ... for variables short names
   */
  int                  listIx;
  /**
   * The {@link NodeListOptional} variables index : 0, 1, 2, ... -> T, T1, T2, ... for variables
   * long names and nlo, nlo1, nlo2, ... for variables short names
   */
  int                  listOptIx;
  /**
   * The {@link NodeOptional} variables index : 0, 1, 2, ... -> P, P1, P2, ... for variables long
   * names
   */
  int                  optIx;
  /** The {@link NodeChoice} index 0, 1, 2, ... -> nch, nch1, nch2, ... for variables short names */
  int                  nchJx;
  /** The {@link INode} choice index 0, 1, 2, ... -> ich, ich1, ich2, ... for variables short names */
  int                  ichJx;
  /** The {@link NodeOptional} index 0, 1, 2, ... -> opt, opt1, opt2, ... for variables short names */
  int                  optJx;
  /**
   * The {@link NodeListOptional} elementAt(i) variables index : 0, 1, 2, ... -> nloeai, nloeai1,
   * nloeai2, ... for variables short names
   */
  int                  nloeaiJx;
  /**
   * The Expansion level we are in : 0, 1, ... : first, second, ... ; incremented at each level,
   * except in an ExpansionChoice with no choice and in ExpansionUnitTCF
   */
  int                  expLvl;
  /** The ExpansionUnitTCF level we are in : -1 : none; 0, 1, ... : first, second, ... */
  int                  tcfLvl;

  /**
   * Constructor which does nothing.
   * 
   * @param aGdbv - the global data builder visitor
   */
  public AcceptInliner(final GlobalDataBuilder aGdbv) {
    super(aGdbv);
  }

  /**
   * Generates the accept methods for all the node tree.<br>
   * 
   * @param aSb - the buffer to write into (must be allocated)
   * @param aSpc - an indentation (must be valid)
   * @param aCI - the ClassInfo to work on (must be fully initialized, in particular field comments)
   * @param aRet - the user return type flag
   * @param aArgu - the user argument flag
   */
  public void genAcceptMethods(final StringBuilder aSb, final Spacing aSpc, final ClassInfo aCI,
                               final boolean aRet, final boolean aArgu) {
    sb = aSb;
    spc = aSpc;
    expLvl = 0;
    jbp = new JavaBranchPrinter(spc);
    ci = aCI;
    fn = 0;
    scn = 0;
    ret = aRet;
    argu = aArgu;
    type = "";
    var = ref = genNodeVar;
    loopIx = seqIx = listIx = listOptIx = optIx = 0;
    nchJx = ichJx = optJx = 0;
    caseIx = -1;
    tcfLvl = -1;
    aCI.astEcNode.accept(this);
  }

  /*
   * Convenience methods
   */

  /** {@inheritDoc} */
  @Override
  void oneNewLine(final INode n) {
    if (DEBUG_CLASS_COMMENTS)
      sb.append(nodeClassComment(n));
    sb.append(LS);
  }

  /** {@inheritDoc} */
  @Override
  void oneNewLine(final INode n, final String str) {
    if (DEBUG_CLASS_COMMENTS)
      sb.append(nodeClassComment(n, str));
    sb.append(LS);
  }

  /** {@inheritDoc} */
  @Override
  void oneNewLine(final INode n, final Object... str) {
    if (DEBUG_CLASS_COMMENTS)
      sb.append(nodeClassComment(n, str));
    sb.append(LS);
  }

  /** {@inheritDoc} */
  @Override
  void twoNewLines(final INode n) {
    oneNewLine(n);
    oneNewLine(n);
  }

  /** {@inheritDoc} */
  @Override
  void threeNewLines(final INode n) {
    oneNewLine(n);
    oneNewLine(n);
    oneNewLine(n);
  }

  /**
   * Returns a node class comment (a //acc followed by the node class short name if global flag set,
   * null otherwise).
   * 
   * @param n - the node for the node class comment
   * @return the node class comment
   */
  private String nodeClassComment(final INode n) {
    if (DEBUG_CLASS_COMMENTS) {
      final String s = n.toString();
      final int b = s.lastIndexOf('.') + 1;
      final int e = s.indexOf('@');
      if (b == -1 || e == -1)
        return " //acc " + s;
      else
        return " //acc " + s.substring(b, e);
    } else
      return null;
  }

  /**
   * Returns a node class comment with an extra comment (a //acc followed by the node class short
   * name plus the extra comment if global flag set, null otherwise).
   * 
   * @param n - the node for the node class comment
   * @param str - the extra comment
   * @return the node class comment
   */
  private String nodeClassComment(final INode n, final String str) {
    if (DEBUG_CLASS_COMMENTS)
      return nodeClassComment(n).concat(" ").concat(str);
    else
      return null;
  }

  /**
   * Returns a node class comment with extra comments (a //acc followed by the node class short name
   * plus the extra comment if global flag set, null otherwise).
   * 
   * @param n - the node for the node class comment
   * @param obj - the extra comments
   * @return the node class comment
   */
  private String nodeClassComment(final INode n, final Object... obj) {
    if (DEBUG_CLASS_COMMENTS) {
      int len = 0;
      for (final Object o : obj)
        len += o.toString().length();
      final StringBuilder buf = new StringBuilder(len);
      for (final Object o : obj)
        buf.append(o.toString());
      return nodeClassComment(n, buf.toString());
    } else
      return null;
  }

  /**
   * Skips the current sub comment within a TCF.
   */
  void skipTcfComment() {
    if (tcfLvl == 0)
      fn++;
    else if (tcfLvl > 0)
      scn++;
  }

  /**
   * Outputs the current field comment or sub comment within a TCF.
   * 
   * @param aStr - a label to output
   */
  void outputTcfComment(final String aStr) {
    if (tcfLvl == 0)
      outputFieldComment("Tcf, expLvl = " + expLvl + ", " + aStr);
    else if (tcfLvl > 0)
      outputSubComment("Tcf " + aStr);
  }

  /**
   * Outputs the current field comment on a line.
   * 
   * @param aStr - a label to output
   */
  void outputFieldComment(final String aStr) {
    //    sb.append(spc.spc).append("/* printed field comment (").append(fn).append(") ").append(aStr)
    //      .append(" */").append(LS);
    ci.fmt1JavacodeFieldCmt(sb, spc, fn, aStr);
  }

  /**
   * Outputs the current sub comment on a line.
   * 
   * @param aStr - a label to output
   */
  void outputSubComment(final String aStr) {
    //    sb.append(spc.spc).append("/* printed sub comment (").append(scn).append(") ").append(aStr)
    //      .append(" */").append(LS);
    ci.fmt1JavacodeSubCmt(sb, spc, scn, aStr);
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
   * 
   * @param n - the node to visit
   */
  @SuppressWarnings("boxing")
  @Override
  public void visit(final ExpansionChoices n) {
    String oldRef = ref;
    String oldVar = var;
    String oldType = type;
    int oldCaseIx;

    if (DEBUG_CLASS_COMMENTS) {
      sb.append(spc.spc).append("// expLvl = ").append(expLvl).append(", lvix = ").append(loopIx)
        .append(", fn = ").append(fn).append(", tcfLvl = ").append(tcfLvl);
      oneNewLine(n, "dbg");
    }

    // only f0
    if (!n.f1.present()) {
      if (DEBUG_CLASS_COMMENTS) {
        sb.append(spc.spc).append("// only f0");
        oneNewLine(n, "a");
      }

      // visit Expansion()
      // no new values for type / var / ref
      oldCaseIx = caseIx = -1;
      n.f0.accept(this);
      ref = oldRef;
      var = oldVar;
      type = oldType;
      caseIx = oldCaseIx;

      if (DEBUG_CLASS_COMMENTS) {
        sb.append(spc.spc).append("// only f0");
        oneNewLine(n, "b");
      }
      return;
    }

    // f0 and f1 : generate switch choice

    // f0 -> Expansion() : generate variables and case 0

    String var1;
    if (expLvl == 0) {
      type = ci.fieldTypes.get(fn);
      if (LONGNAMES)
        var = var.concat(String.valueOf(fn)).concat("C");
      else {
        var = "nch";
        if (nchJx > 0)
          var += nchJx;
        nchJx++;
      }
      ref = ref.concat(".").concat(ci.fieldNames.get(fn));
      if (tcfLvl == -1)
        outputFieldComment("ExpansionChoices");
      sb.append(spc.spc).append("final ").append(type).append(' ').append(var).append(" = ")
        .append(ref).append(';');
      oneNewLine(n, "expLvl == 0");
      fn++;
      ref = var.concat(".choice");
      var1 = var;
    } else {
      if (tcfLvl >= 0 && "".equals(type)) {
        // case within TCF at first level
        var = ci.fieldNames.get(fn);
        fn++;
        type = nodeChoice;
        var1 = genNodeVarDot.concat(var);
        ref = var1.concat(".choice");
      } else {
        // all other cases : not within TCF, or within TCF not at first level
        if (LONGNAMES)
          var += "C";
        else {
          var = "nch";
          if (nchJx > 0)
            var += nchJx;
          nchJx++;
        }
        sb.append(spc.spc).append("final ").append(nodeChoice).append(' ').append(var)
          .append(" = ");
        if (!nodeChoice.equals(type))
          sb.append('(').append(nodeChoice).append(") ");
        sb.append(ref).append(';');
        oneNewLine(n, "expLvl (", expLvl, ") != 0 && type (", type, ") --> ", nodeChoice);
        type = nodeChoice;
        ref = var.concat(".choice");
        var1 = var;
      }
    }

    if (LONGNAMES)
      var += "H";
    else {
      var = "ich";
      if (ichJx > 0)
        var += ichJx;
      ichJx++;
    }
    sb.append(spc.spc).append("final ").append(iNode).append(' ').append(var).append(" = ")
      .append(ref).append(';');
    oneNewLine(n, "choice, type (", type, ") --> ", iNode);
    sb.append(spc.spc).append("switch (").append(var1).append(".which) {");
    oneNewLine(n, "switch");
    type = iNode;
    ref = var;

    spc.updateSpc(+1);
    sb.append(spc.spc).append("case 0:");
    oneNewLine(n, "case 0");
    spc.updateSpc(+1);
    outputSubComment("Choice a");

    // visit Expansion
    oldCaseIx = caseIx = 0;
    oldRef = ref;
    oldVar = var;
    oldType = type;
    ++expLvl;
    n.f0.accept(this);
    --expLvl;
    ref = oldRef;
    var = oldVar;
    type = oldType;
    caseIx = oldCaseIx;

    sb.append(spc.spc).append("break;");
    oneNewLine(n, "break 0");
    spc.updateSpc(-1);

    // f1 -> ( "|" Expansion() )* : generate other cases
    for (int i = 0; i < n.f1.size();) {
      final NodeSequence seq = (NodeSequence) n.f1.elementAt(i);
      i++;

      sb.append(spc.spc).append("case ").append(i).append(":");
      oneNewLine(n, "case");
      spc.updateSpc(+1);
      outputSubComment("Choice b");

      // visit Expansion
      oldCaseIx = caseIx = i;
      ++expLvl;
      seq.elementAt(1).accept(this);
      --expLvl;
      ref = oldRef;
      var = oldVar;
      type = oldType;
      caseIx = oldCaseIx;

      sb.append(spc.spc).append("break;");
      oneNewLine(n, "break n");
      spc.updateSpc(-1);

    }

    // generate default and end of switch
    sb.append(spc.spc).append("default:");
    oneNewLine(n, "default");
    spc.updateSpc(+1);
    sb.append(spc.spc).append("// should not occur !!!");
    oneNewLine(n, "should");
    sb.append(spc.spc).append("break;");
    oneNewLine(n, "break def");
    spc.updateSpc(-1);

    spc.updateSpc(-1);
    sb.append(spc.spc).append("}");
    oneNewLine(n, "}");

  }

  /**
   * Visits a {@link Expansion} node, whose children are the following :
   * <p>
   * f0 -> ( #0 "LOOKAHEAD" #1 "(" #2 LocalLookahead() #3 ")" )?<br>
   * f1 -> ( ExpansionUnit() )+<br>
   * 
   * @param n - the node to visit
   */
  @SuppressWarnings("boxing")
  @Override
  public void visit(final Expansion n) {
    // don't take f0, only f1

    // f1 -> ( ExpansionUnit() )+ : visit something within a sequence (nbEu > 1) or directly (nbEu == 1)

    // count the number of non LocalLookahead nor Block nor not to be created nodes
    nbEu = 0;
    for (final Iterator<INode> e = n.f1.elements(); e.hasNext();) {
      final ExpansionUnit expUnit = (ExpansionUnit) e.next();
      if (gdbv.isEuOk(expUnit))
        nbEu++;
    }

    if (DEBUG_CLASS_COMMENTS) {
      sb.append(spc.spc).append("// expLvl = ").append(expLvl).append(", nbEu = ").append(nbEu)
        .append(", lvix = ").append(loopIx).append(", fn = ").append(fn).append(", tcfLvl = ")
        .append(tcfLvl).append(", type = ").append(type);
      oneNewLine(n, "dbg");
    }

    if (tcfLvl == -1 && (expLvl > 0) && (nbEu > 1) && !nodeSeq.equals(type)) {
      if (LONGNAMES)
        var += "S";
      else
        var = "seq";
      if (seqIx > 0)
        var += seqIx;
      seqIx++;
      sb.append(spc.spc).append("final ").append(nodeSeq).append(' ').append(var).append(" = (")
        .append(nodeSeq).append(") ").append(ref).append(';');
      oneNewLine(n, "expLvl (", expLvl, ") > 0 && nbEu (", nbEu, ") > 1 && !tns, type (", type,
                 ") --> ", nodeSeq);
      type = nodeSeq;
    }

    final String oldRef = ref;
    final String oldVar = var;
    final String oldType = type;
    final int oldNbEu = nbEu;
    int oldNloeaiJx = nloeaiJx;

    int numEuOk = 0;
    final int sz = n.f1.size();
    for (int i = 0; i < sz; i++) {
      final ExpansionUnit expUnit = (ExpansionUnit) n.f1.elementAt(i);
      // don't process LocalLookahead nor Block nor not to be created nodes
      if (gdbv.isEuOk(expUnit)) {

        // generate variables except for ExpansionUnitTCF
        if (expUnit.f0.which != 3) {
          if (tcfLvl >= 0 && "".equals(type)) {
            // case within TCF at first level
            if (expUnit.f0.which == 4) {
              // just a RegularExpression
              outputTcfComment("Token");
              var = ref.concat(".").concat(ci.fieldNames.get(fn));
              fn++;
            }
          } else if (expLvl == 0) {
            // cases not within TCF, or within TCF not at first level : at first Expansion level ; proper type
            ref = ref.concat(".").concat(ci.fieldNames.get(fn));
            var += fn;
            type = ci.fieldTypes.get(fn);
            outputFieldComment("Expansion");
            sb.append(spc.spc).append("final ").append(type).append(' ').append(var).append(" = ")
              .append(ref).append(';');
            oneNewLine(n, "bnflvl == 0, fn = ", fn);
            ref = var;
            fn++;
          } else {
            // cases not within TCF, or within TCF not at first level : at other Expansion levels ; INode type
            if (nbEu > 1) {
              final String numEuOkStr = String.valueOf(numEuOk);
              if (nodeSeq.equals(type))
                ref = var.concat(".elementAt(").concat(numEuOkStr).concat(")");
              else
                ref = "((".concat(nodeSeq).concat(") ").concat(var).concat(").elementAt(")
                          .concat(numEuOkStr).concat(")");
              if (LONGNAMES) {
                if (caseIx >= 0)
                  var += caseIx;
                var = var.concat("A").concat(numEuOkStr);
              } else {
                var = "seq";
                if (seqIx > 0)
                  var += seqIx;
                seqIx++;
              }
              outputSubComment("Sequence");
              sb.append(spc.spc).append("final ").append(iNode).append(' ').append(var)
                .append(" = ").append(ref).append(';');
              oneNewLine(n, "bnflvl (", expLvl, ") != 0 && nbEu (", nbEu, ") > 1, type (", type,
                         ") >-- ", iNode);
              type = iNode;
              ref = var;
            }
          }
        }

        // visit ExpansionUnit
        oldNloeaiJx = nloeaiJx;
        ++expLvl;
        expUnit.accept(this);
        --expLvl;
        ref = oldRef;
        var = oldVar;
        type = oldType;
        nbEu = oldNbEu;
        nloeaiJx = oldNloeaiJx;

        numEuOk++;

      }
    }

  }

  /**
   * Visits a {@link ExpansionUnit} node, whose children are the following :
   * <p>
   * f0 -> . %0 #0 "LOOKAHEAD" #1 "(" #2 LocalLookahead() #3 ")"<br>
   * f0 -> . %0 #0 "LOOKAHEAD" #1 "(" #2 LocalLookahead() #3 ")"<br>
   * .. .. | %1 Block()<br>
   * .. .. | %2 #0 "[" #1 ExpansionChoices() #2 "]"<br>
   * .. .. | %3 ExpansionUnitTCF()<br>
   * .. .. | %4 #0 [ $0 PrimaryExpression() $1 "=" ]<br>
   * .. .. . .. #1 ( &0 $0 IdentifierAsString() $1 Arguments()<br>
   * .. .. . .. .. . .. $2 [ "!" ]<br>
   * .. .. . .. .. | &1 $0 RegularExpression()<br>
   * .. .. . .. .. . .. $1 [ ?0 "." ?1 <IDENTIFIER> ]<br>
   * .. .. . .. .. . .. $2 [ "!" ] )<br>
   * .. .. | %5 #0 "(" #1 ExpansionChoices() #2 ")"<br>
   * .. .. . .. #3 ( &0 "+"<br>
   * .. .. . .. .. | &1 "*"<br>
   * .. .. . .. .. | &2 "?" )?<br>
   * 
   * @param n - the node to visit
   */
  @SuppressWarnings("boxing")
  @Override
  public void visit(final ExpansionUnit n) {
    final NodeSequence seq;
    NodeOptional opt;
    NodeChoice ch;
    final String oldRef = ref;
    final String oldVar = var;
    final String oldType = type;
    int oldLoopIx = loopIx;
    String loopIxStr;

    switch (n.f0.which) {
      case 0:
        // %0 #0 "LOOKAHEAD" #1 "(" #2 LocalLookahead() #3 ")"
        // should not be called !
        Messages.hardErr("visit ExpansionUnit type 0 should not occur !");
        break;

      case 1:
        // %1 Block()
        // should not be called !
        Messages.hardErr("visit ExpansionUnit type 1 should not occur !");
        break;

      case 2:
        // %2 #0 "[" #1 ExpansionChoices() #2 "]"
        // visit something within a node optional
        if (DEBUG_CLASS_COMMENTS) {
          sb.append(spc.spc).append("// ExpansionUnit type 2");
          oneNewLine(n, "2_beg");
        }

        seq = (NodeSequence) n.f0.choice;

        if (tcfLvl == 0 && expLvl == 2)
          outputTcfComment("eu type 2");
        if (tcfLvl >= 0 && "".equals(type)) {
          type = nodeOpt;
          var = genNodeVarDot.concat(ci.fieldNames.get(fn));
          fn++;
        } else if (tcfLvl >= 0 || !nodeOpt.equals(type)) {
          final String var1 = var;
          if (LONGNAMES)
            var += "P";
          else {
            var = "opt";
            if (optJx > 0)
              var += optJx;
            optJx++;
          }
          sb.append(spc.spc).append("final ").append(nodeOpt).append(' ').append(var)
            .append(" = (").append(nodeOpt).append(") ").append(var1).append(';');
          oneNewLine(n, "tcf || type (", type, ") --> ", nodeOpt);
          type = nodeOpt;
        }

        sb.append(spc.spc).append("if (").append(var).append(".present()) {");
        oneNewLine(n, "if");
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
        oneNewLine(n, "2_end");
        break;

      case 3:
        // %3 ExpansionUnitTCF()
        if (DEBUG_CLASS_COMMENTS) {
          sb.append(spc.spc).append("// ExpansionUnit type 3");
          oneNewLine(n, "3_beg");
        }
        // visit ExpansionUnitTCF
        n.f0.choice.accept(this);
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
          // if yes, we would need to generate the first Block() of a BnfProduction() (for the variable declaration),
          // generate this assignment after the accept method, using a generated return value
          //          sb.append(spc.spc);
          //          ((NodeSequence) opt.node).elementAt(0).accept(this);
          //          sb.append(" = ");
          //          sb.append("// Please report to support : TO DO #0 [ $0 PrimaryExpression() $1 \"=\" ]");
          //          oneNewLine(n, "4_._PrimaryExpression()");
        }

        // #1 (&0 | &1)
        ch = (NodeChoice) seq.elementAt(1);
        final NodeSequence seq1 = (NodeSequence) ch.choice;
        if (ch.which == 0) {
          if (!((NodeOptional) seq1.elementAt(2)).present()) {
            // generate node creation if not requested not to do so
            // $0 IdentifierAsString() $1 Arguments() $2 [ "!" ]
            if (depthLevel)
              DepthFirstVisitorsGenerator.increaseDepthLevel(sb, spc);
            sb.append(spc.spc);
            if (ret)
              sb.append(genRetVar).append(" = ");
            sb.append(var).append(".accept(this");
            if (argu)
              sb.append(", ").append(genArguVar);
            sb.append(");");
            oneNewLine(n, "4_0_RegularExpression");
            if (depthLevel)
              DepthFirstVisitorsGenerator.decreaseDepthLevel(sb, spc);
          }
        } else {
          // $0 RegularExpression() $1 [ ?0 "." ?1 < IDENTIFIER > ] $2 [ "!" ]
          if (!((NodeOptional) seq1.elementAt(2)).present()) {
            // generate node creation if not requested not to do so
            if (depthLevel)
              DepthFirstVisitorsGenerator.increaseDepthLevel(sb, spc);
            sb.append(spc.spc);
            if (ret)
              sb.append(genRetVar).append(" = ");
            sb.append(var).append(".accept(this");
            if (argu)
              sb.append(", ").append(genArguVar);
            sb.append(");");
            oneNewLine(n, "4_1_RegularExpression");
            if (depthLevel)
              DepthFirstVisitorsGenerator.decreaseDepthLevel(sb, spc);
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

        seq = (NodeSequence) n.f0.choice;
        // #3 ( &0 "+" | &1 "*" | &2 "?" )?
        opt = (NodeOptional) seq.elementAt(3);
        if (opt.present()) {
          ch = (NodeChoice) opt.node;
          if (ch.which == 0) {
            // &0 "+" modifier : visit something within a node list
            if (DEBUG_CLASS_COMMENTS) {
              sb.append(spc.spc).append("// ExpansionUnit type 5 with '+' modifier");
              oneNewLine(n, "5_+, expLvl = ", expLvl);
            }

            String var1 = var;
            if (tcfLvl == 0 && expLvl == 2)
              outputTcfComment("eu type 5_+");
            if (tcfLvl >= 0 && "".equals(type)) {
              type = "NodeList";
              var = ci.fieldNames.get(fn);
              fn++;
              ref = genNodeVarDot.concat(var);
              var1 = ref;
            } else if (tcfLvl >= 0 || !"NodeList".equals(type)) {
              ref = var;
              if (LONGNAMES)
                var = var.concat("L");
              else
                var = "lst";
              if (listIx > 0)
                var += listIx;
              listIx++;
              var1 = var;
              sb.append(spc.spc).append("final ").append(nodeList).append(' ').append(var)
                .append(" = (").append(nodeList).append(") ").append(ref).append(';');
              oneNewLine(n, "tcf || type (", type, ") --> ", nodeList);
              type = nodeList;
            }

            oldLoopIx = loopIx;
            loopIxStr = loopIx == 0 ? "" : String.valueOf(loopIx);
            loopIx++;
            sb.append(spc.spc).append("for (int i").append(loopIxStr).append(" = 0; i")
              .append(loopIxStr).append(" < ").append(var1).append(".size(); i").append(loopIxStr)
              .append("++) {");
            oneNewLine(n, "for +");
            spc.updateSpc(+1);

            ref = var1.concat(".elementAt(i").concat(String.valueOf(loopIxStr)).concat(")");
            if (LONGNAMES)
              var = var.concat("Ei");
            else
              var = "lsteai";
            sb.append(spc.spc).append("final ").append(iNode).append(' ').append(var).append(" = ")
              .append(ref).append(';');
            oneNewLine(n, "elem +");
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
            oneNewLine(n, "}");

          } else if (ch.which == 1) {
            // &1 "*" modifier : visit something within a node list optional
            if (DEBUG_CLASS_COMMENTS) {
              sb.append(spc.spc).append("// ExpansionUnit type 5 with '*' modifier");
              oneNewLine(n, "5_*, expLvl = ", expLvl);
            }

            ref = var;
            if (tcfLvl == 0 && expLvl == 2)
              outputTcfComment("eu type 5_*");
            if (tcfLvl >= 0 && "".equals(type)) {
              type = nodeListOpt;
              var = genNodeVarDot.concat(ci.fieldNames.get(fn));
              fn++;
            } else if (tcfLvl >= 0 || !nodeListOpt.equals(type)) {
              if (LONGNAMES)
                var = var.concat("T");
              else
                var = "nlo";
              if (listOptIx > 0)
                var += listOptIx;
              listOptIx++;
              sb.append(spc.spc).append("final ").append(nodeListOpt).append(' ').append(var)
                .append(" = (").append(nodeListOpt).append(") ").append(ref).append(';');
              oneNewLine(n, "tcf || type (", type, ") --> ", nodeListOpt);
              type = nodeListOpt;
            }

            sb.append(spc.spc).append("if (").append(var).append(".present()) {");
            oneNewLine(n, "if");
            spc.updateSpc(+1);

            oldLoopIx = loopIx;
            loopIxStr = loopIx == 0 ? "" : String.valueOf(loopIx);
            loopIx++;
            sb.append(spc.spc).append("for (int i").append(loopIxStr).append(" = 0; i")
              .append(loopIxStr).append(" < ").append(var).append(".size(); i").append(loopIxStr)
              .append("++) {");
            oneNewLine(n, "for *");
            spc.updateSpc(+1);

            ref = var.concat(".elementAt(i").concat(String.valueOf(loopIxStr)).concat(")");
            if (LONGNAMES)
              var = var.concat("Mi");
            else {
              var = "nloeai";
              if (nloeaiJx > 0)
                var += nloeaiJx;
              nloeaiJx++;
            }
            if (var.startsWith(genNodeVarDot))
              var = var.substring(genNodeVarDot.length());
            sb.append(spc.spc).append("final ").append(iNode).append(' ').append(var).append(" = ")
              .append(ref).append(';');
            oneNewLine(n, "elem *");
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
            oneNewLine(n, "1}");

            spc.updateSpc(-1);
            sb.append(spc.spc).append("}");
            oneNewLine(n, "2}");

          } else {
            // &2 "?" modifier : visit something within a node optional
            if (DEBUG_CLASS_COMMENTS) {
              sb.append(spc.spc).append("// ExpansionUnit type 5 with '?' modifier");
              oneNewLine(n, "5_?, expLvl = ", expLvl);
            }

            ref = var;
            if (tcfLvl == 0 && expLvl == 2)
              outputTcfComment("eu type 5_?");
            if (tcfLvl >= 0 && "".equals(type)) {
              type = nodeOpt;
              var = genNodeVarDot.concat(ci.fieldNames.get(fn));
              fn++;
            } else if (tcfLvl >= 0 || !nodeOpt.equals(type)) {
              if (LONGNAMES) {
                var = var.concat("P");
                if (optIx > 0)
                  var += optIx;
                optIx++;
              } else {
                var = "opt";
                if (optJx > 0)
                  var += optJx;
                optJx++;
              }
              sb.append(spc.spc).append("final ").append(nodeOpt).append(' ').append(var)
                .append(" = (").append(nodeOpt).append(") ").append(ref).append(';');
              oneNewLine(n, "type (", type, ") --> ", nodeOpt);
              type = nodeOpt;
            }

            sb.append(spc.spc).append("if (").append(var).append(".present()) {");
            oneNewLine(n, "if");
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
            oneNewLine(n, "}");
          }

        } else {
          // no modifier : visit something with a node sequence
          if (DEBUG_CLASS_COMMENTS) {
            sb.append(spc.spc).append("// ExpansionUnit type 5 with no modifiers");
            oneNewLine(n, "5_no, expLvl = ", expLvl, ", nbEu = ", nbEu);
          }

          if (tcfLvl == 0 && expLvl == 2)
            outputTcfComment("eu type 5");
          if (tcfLvl >= 0 && "".equals(type) && !((ExpansionChoices) seq.elementAt(1)).f1.present()) {
            if (LONGNAMES)
              var = var.concat("Tcf");
            else
              var = "tcf";
            if (fn != 0)
              var += fn;
            ref += ".".concat(ci.fieldNames.get(fn));
            fn++;
            sb.append(spc.spc).append("final ").append(nodeSeq).append(' ').append(var)
              .append(" = ").append(ref).append(';');
            oneNewLine(n, "tcf, expLvl = ", expLvl, ", type (", type, ") --> ", nodeSeq);
            type = nodeSeq;
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
        Messages.hardErr("unknow n.f0.which = " + String.valueOf(n.f0.which));
        break;

    }

  }

  /**
   * Visits a {@link ExpansionUnitTCF} node, whose children are the following :
   * <p>
   * f0 -> "try"<br>
   * f1 -> "{"<br>
   * f2 -> ExpansionChoices()<br>
   * f3 -> "}"<br>
   * f4 -> ( #0 "catch" #1 "(" #2 Name() #3 < IDENTIFIER > #4 ")" #5 Block() )*<br>
   * f5 -> [ #0 "finally" #1 Block() ]<br>
   * 
   * @param n - the node to visit
   */
  @Override
  public void visit(final ExpansionUnitTCF n) {
    tcfLvl++;
    // f0 -> "try"
    skipTcfComment();
    sb.append(spc.spc).append(genJavaBranch(n.f0)).append(' ');
    // f1 -> "{"
    skipTcfComment();
    sb.append(genJavaBranch(n.f1));
    oneNewLine(n);
    spc.updateSpc(+1);
    // f2 -> ExpansionChoices()
    if (tcfLvl > 0)
      outputTcfComment("ExpansionChoices");
    // visit ExpansionChoices
    n.f2.accept(this);

    // f3 -> "}"
    spc.updateSpc(-1);
    skipTcfComment();
    sb.append(spc.spc).append(genJavaBranch(n.f3));
    oneNewLine(n);
    // f4 -> ( #0 "catch" #1 "(" #2 Name() #3 < IDENTIFIER > #4 ")" #5 Block() )*
    if (n.f4.present())
      for (int i = 0; i < n.f4.size(); i++) {
        final NodeSequence seq = (NodeSequence) n.f4.elementAt(i);
        // #0 "catch"
        skipTcfComment();
        sb.append(spc.spc).append(genJavaBranch(seq.elementAt(0))).append(' ');
        // #1 "("
        skipTcfComment();
        sb.append(' ').append(genJavaBranch(seq.elementAt(1))).append(' ');
        // #2 Name()
        skipTcfComment();
        sb.append(genJavaBranch(seq.elementAt(2))).append(' ');
        // #3 < IDENTIFIER >
        skipTcfComment();
        sb.append(' ').append(genJavaBranch(seq.elementAt(3))).append(' ');
        // #4 ")"
        skipTcfComment();
        sb.append(genJavaBranch(seq.elementAt(4))).append(' ');
        // #5 Block()
        skipTcfComment();
        sb.append(genJavaBranch(seq.elementAt(5)));
        oneNewLine(n);
      }
    // f5 -> [ #0 "finally" #1 Block() ]
    if (n.f5.present()) {
      final NodeSequence seq = (NodeSequence) n.f5.node;
      // #0 "finally"
      skipTcfComment();
      sb.append(spc.spc).append(genJavaBranch(seq.elementAt(0))).append(' ');
      // #1 Block()
      skipTcfComment();
      sb.append(genJavaBranch(seq.elementAt(1)));
      oneNewLine(n);
    }
    tcfLvl--;
  }

  //  /**
  //   * Visits a {@link IdentifierAsString} node, whose children are the following :
  //   * <p>
  //   * f0 -> < IDENTIFIER ><br>
  //   * 
  //   * @param n - the node to visit
  //   */
  //  @Override
  //  public void visit(final IdentifierAsString n) {
  //    sb.append(UnicodeConverter.addUnicodeEscapes(n.f0.tokenImage));
  //  }

}

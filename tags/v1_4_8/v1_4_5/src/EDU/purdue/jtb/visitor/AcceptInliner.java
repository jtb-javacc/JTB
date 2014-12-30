package EDU.purdue.jtb.visitor;

import java.util.Iterator;

import EDU.purdue.jtb.misc.ClassInfo;
import EDU.purdue.jtb.misc.DepthFirstVisitorsGenerator;
import EDU.purdue.jtb.misc.FieldNameGenerator;
import EDU.purdue.jtb.misc.Globals;
import EDU.purdue.jtb.misc.JavaBranchPrinter;
import EDU.purdue.jtb.misc.Messages;
import EDU.purdue.jtb.misc.Spacing;
import EDU.purdue.jtb.misc.UnicodeConverter;
import EDU.purdue.jtb.syntaxtree.Expansion;
import EDU.purdue.jtb.syntaxtree.ExpansionChoices;
import EDU.purdue.jtb.syntaxtree.ExpansionUnit;
import EDU.purdue.jtb.syntaxtree.ExpansionUnitInTCF;
import EDU.purdue.jtb.syntaxtree.INode;
import EDU.purdue.jtb.syntaxtree.Identifier;
import EDU.purdue.jtb.syntaxtree.NodeChoice;
import EDU.purdue.jtb.syntaxtree.NodeOptional;
import EDU.purdue.jtb.syntaxtree.NodeSequence;

/**
 * The AcceptInliner visitor (an extension of {@link JavaCCPrinter visitor}) is called by
 * {@link DepthFirstVisitorsGenerator} (which calls
 * {@link #genAcceptMethods(StringBuilder, Spacing, ClassInfo, boolean, boolean)} to "inline" the
 * accept methods on the base classes nodes (in order to facilitate the user customization work).
 * <p>
 * TODO add field comments and replace INode with field type
 * 
 * @author Marc Mazas, mmazas@sopragroup.com
 * @version 1.4.0 : 05-08/2009 : MMa : creation
 * @version 1.4.2 : 20/02/2010 : MMa : fixed inlining issue in visit ExpansionChoices
 * @version 1.4.3.1 : 20/04/2010 : MMa : fixed descriptive field name issues in visit
 *          ExpansionChoices and Expansion
 * @version 1.4.3.2 : 26/04/2010 : MMa : fixed index issue in visit Expansion
 */
public class AcceptInliner extends JavaCCPrinter {

  /** The processed ClassInfo */
  ClassInfo                ci;
  /** The ClassInfo field number */
  int                      fn;
  /** The user return type flag */
  boolean                  ret;
  /** The user argument flag */
  boolean                  argu;
  /** The JTB node reference (e.g. n.f0, nF0.choice, ...) */
  String                   ref;
  /** The JTB node reference type (e.g. NodeChoice, INode, ...) */
  String                   type;
  /** The variable (e.g. nF0, nF0Ch, ...) to refer to the JTB node reference with proper cast */
  String                   var;
  /** The loop variable (depth) number (0, 1, 2 ... -> i, i1, i2, ...) */
  int                      lvn;
  /** The sequence variable number (-1, 0, 1, 2 ... -> Seq, Seq0, Seq1, Seq2, ...) */
  int                      svn;
  /** The field name generator */
  final FieldNameGenerator nameGen = new FieldNameGenerator();

  /**
   * Constructor which does nothing.
   */
  public AcceptInliner() {
    // nothing done here
  }

  /**
   * Generates the accept methods for all the node tree.<br>
   * 
   *@param aSB the buffer to write into (must be allocated)
   * @param aSpc an indentation (must be valid)
   * @param aCI the ClassInfo to work on (must be fully initialized, in particular field comments)
   * @param aRet the user return type flag
   * @param aArgu the user argument flag
   */
  public void genAcceptMethods(final StringBuilder aSB, final Spacing aSpc, final ClassInfo aCI,
                               final boolean aRet, final boolean aArgu) {
    sb = aSB;
    spc = aSpc;
    bnfLvl = 0;
    jbpv = new JavaBranchPrinter(spc);
    ci = aCI;
    fn = 0;
    ret = aRet;
    argu = aArgu;
    ref = "n";
    type = "";
    var = "n";
    lvn = 0;
    svn = 0;
    nameGen.reset();
    aCI.getAstNode().accept(this);
  }

  /*
   * Convenience methods
   */

  /**
   * Prints into the current buffer a node class comment and a new line.
   * 
   * @param n the node for the node class comment
   */
  @Override
  void oneNewLine(final INode n) {
    sb.append(nodeClassComment(n)).append(LS);
  }

  /**
   * Prints into the current buffer a node class comment, an extra given comment, and a new line.
   * 
   * @param n the node for the node class comment
   * @param str the extra comment
   */
  @Override
  void oneNewLine(final INode n, final String str) {
    sb.append(nodeClassComment(n, str)).append(LS);
  }

  /**
   * Prints twice into the current buffer a node class comment and a new line.
   * 
   * @param n the node for the node class comment
   */
  @Override
  void twoNewLines(final INode n) {
    oneNewLine(n);
    oneNewLine(n);
  }

  /**
   * Prints three times into the current buffer a node class comment and a new line.
   * 
   * @param n the node for the node class comment
   */
  @Override
  void threeNewLines(final INode n) {
    oneNewLine(n);
    oneNewLine(n);
    oneNewLine(n);
  }

  /**
   * Returns a node class comment (a //== followed by the node class short name if global flag set,
   * nothing otherwise).
   * 
   * @param n the node for the node class comment
   * @return the node class comment
   */
  private String nodeClassComment(final INode n) {
    if (Globals.PRINT_CLASS_COMMENT) {
      final String s = n.toString();
      final int b = s.lastIndexOf('.') + 1;
      final int e = s.indexOf('@');
      if (b == -1 || e == -1)
        return " //== " + s;
      else
        return " //== " + s.substring(b, e);
    } else
      return "";
  }

  /**
   * Returns a node class comment with an extra comment (a //== followed by the node class short
   * name plus the extra comment if global flag set, nothing otherwise).
   * 
   * @param n the node for the node class comment
   * @param str the extra comment
   * @return the node class comment
   */
  private String nodeClassComment(final INode n, final String str) {
    if (Globals.PRINT_CLASS_COMMENT)
      return nodeClassComment(n) + " " + str;
    else
      return "";
  }

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
    String oldRef = ref;
    String oldVar = var;
    String oldType = type;
    int oldSvn = -1;

    if (Globals.PRINT_CLASS_COMMENT) {
      sb.append("// bnfLvl = ").append(bnfLvl);
      oneNewLine(n, "dbg");
    }

    // only f0
    if (!n.f1.present()) {
      if (Globals.PRINT_CLASS_COMMENT)
        oneNewLine(n, "f0_a");

      svn = -1;

      // Expansion()
      n.f0.accept(this);

      ref = oldRef;
      var = oldVar;
      type = oldType;
      svn = oldSvn;

      if (Globals.PRINT_CLASS_COMMENT)
        oneNewLine(n, "f0_b");
      return;
    }

    // f0 and f1 : generate switch choice

    // f0 -> Expansion(c1)

    if (bnfLvl == 0) {
      type = ci.getFieldTypes().get(fn); // should always be NodeChoice
      //      if (!Globals.descriptiveFieldNames)
      //        ref = ref.concat(".f").concat(String.valueOf(fn));
      //      else
      ref = ref.concat(".").concat(ci.getFieldNames().get(fn));
      var = var.concat(String.valueOf(fn)).concat("Ch");
      sb.append(spc.spc).append("final ").append(type).append(" ").append(var).append(" = ")
        .append(ref).append(";");
      oneNewLine(n);
      fn++;
    } else {
      if (!"NodeChoice".equals(type)) {
        var = var.concat("Ch");
        type = "NodeChoice";
        sb.append(spc.spc).append("final NodeChoice ").append(var).append(" = (NodeChoice) ")
          .append(ref).append(";");
        oneNewLine(n);
      }
    }

    ref = var.concat(".choice");
    sb.append(spc.spc).append("final INode ").append(var).append("N = ").append(ref).append(";");
    type = "INode";
    oneNewLine(n);
    sb.append(spc.spc).append("switch (").append(var).append(".which) {");
    oneNewLine(n);
    var = var.concat("N");
    ref = var;

    spc.updateSpc(+1);
    sb.append(spc.spc).append("case 0:");
    oneNewLine(n);
    spc.updateSpc(+1);
    svn = 0;

    oldRef = ref;
    oldVar = var;
    oldType = type;
    oldSvn = svn;

    ++bnfLvl;
    // Expansion()
    n.f0.accept(this);
    --bnfLvl;
    ref = oldRef;
    var = oldVar;
    type = oldType;
    svn = oldSvn;

    sb.append(spc.spc).append("break;");
    oneNewLine(n);
    spc.updateSpc(-1);

    // f1 -> ( "|" Expansion(c2) )*
    for (int i = 0; i < n.f1.size();) {
      final NodeSequence seq = (NodeSequence) n.f1.elementAt(i);
      i++;

      sb.append(spc.spc).append("case ").append(i).append(":");
      oneNewLine(n);
      spc.updateSpc(+1);
      svn = i;

      ++bnfLvl;
      // Expansion(c2)
      seq.elementAt(1).accept(this);
      --bnfLvl;

      ref = oldRef;
      var = oldVar;
      type = oldType;
      svn = oldSvn;

      sb.append(spc.spc).append("break;");
      oneNewLine(n);
      spc.updateSpc(-1);

    }

    sb.append(spc.spc).append("default:");
    oneNewLine(n);
    spc.updateSpc(+1);
    sb.append(spc.spc).append("// should not occur !!!");
    oneNewLine(n);
    sb.append(spc.spc).append("break;");
    oneNewLine(n);
    spc.updateSpc(-1);

    spc.updateSpc(-1);
    sb.append(spc.spc).append("}");
    oneNewLine(n);

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
    // don't take f0, only f1

    // f1 -> ( ExpansionUnit(c2) )+ : visit something within a sequence (nbEuOk > 1) or directly (nbEuOk == 1)

    // count the number of non LocalLookahead nor Block ExpansionUnits
    int nbEuOk = 0;
    for (final Iterator<INode> e = n.f1.elements(); e.hasNext();) {
      final ExpansionUnit expUnit = (ExpansionUnit) e.next();
      if (expUnit.f0.which > 1)
        nbEuOk++;
    }

    if (Globals.PRINT_CLASS_COMMENT) {
      sb.append("// bnfLvl = ").append(bnfLvl).append(", nbEuOk = ").append(nbEuOk);
      oneNewLine(n, "dbg");
    }

    if ((bnfLvl > 0) && (nbEuOk > 1) && !"NodeSequence".equals(type)) {
      var = var.concat("Seq").concat(svn == -1 ? "" : String.valueOf(svn));
      type = "NodeSequence";
      sb.append(spc.spc).append("final NodeSequence ").append(var).append(" = (NodeSequence) ")
        .append(ref).append(";");
      oneNewLine(n);
    }

    int numEuOk = 0;
    final String oldRef = ref;
    final String oldVar = var;
    final String oldType = type;

    for (int i = 0; i < n.f1.size(); i++) {
      final ExpansionUnit expUnit = (ExpansionUnit) n.f1.elementAt(i);
      // don't process LocalLookahead nor Block in ExpansionUnit
      if (expUnit.f0.which > 1) {

        if (bnfLvl == 0) {
          // at first Expansion level
          //          if (!Globals.descriptiveFieldNames)
          //            ref = ref.concat(".f").concat(String.valueOf(fn));
          //          else
          ref = ref.concat(".").concat(ci.getFieldNames().get(fn));
          var = var.concat(String.valueOf(fn));
          type = ci.getFieldTypes().get(fn);
          sb.append(spc.spc).append("final ").append(type).append(" ").append(var).append(" = ")
            .append(ref).append(";");
          oneNewLine(n);
          fn++;
        } else {
          // at other Expansion levels
          if (nbEuOk > 1) {
            final String numEuOkStr = String.valueOf(numEuOk);
            ref = var.concat(".elementAt(").concat(numEuOkStr).concat(")");
            var = var.concat("A").concat(numEuOkStr);
            type = "INode";
            sb.append(spc.spc).append("final INode ").append(var).append(" = ").append(ref)
              .append(";");
            oneNewLine(n);
            ref = var;
          }
        }

        // visit
        ++bnfLvl;
        expUnit.accept(this);
        --bnfLvl;

        ref = oldRef;
        var = oldVar;
        type = oldType;

        numEuOk++;
      }
    }

  }

  /**
   * Visits a {@link ExpansionUnit} node, whose children are the following :
   * <p>
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
    final NodeSequence seq;
    NodeOptional opt;
    NodeChoice ch;
    final String oldRef = ref;
    final String oldVar = var;
    final String oldType = type;
    int oldLvn = lvn;
    String lvnStr;

    switch (n.f0.which) {
      case 0:
        // %0 #0 "LOOKAHEAD" #1 "(" #2 LocalLookahead() #3 ")"
        // should not be called !
        break;

      case 1:
        // %1 Block()
        // should not be called !
        break;

      case 2:
        // %2 #0 "[" #1 ExpansionChoices() #2 "]"
        // visit something within a node optional
        if (Globals.PRINT_CLASS_COMMENT) {
          sb.append(spc.spc).append("// ExpansionUnit type 2");
          oneNewLine(n, "2_beg");
        }

        seq = (NodeSequence) n.f0.choice;

        if (!"NodeOptional".equals(type)) {
          type = "NodeOptional";
          sb.append(spc.spc).append("final NodeOptional ").append(var)
            .append("Opt = (NodeOptional) ").append(var).append(";");
          oneNewLine(n);
          var = var.concat("Opt");
        }

        sb.append(spc.spc).append("if (").append(var).append(".present()) {");
        oneNewLine(n);
        spc.updateSpc(+1);
        ref = var.concat(".node");

        ++bnfLvl;
        seq.elementAt(1).accept(this);
        --bnfLvl;
        ref = oldRef;
        var = oldVar;
        type = oldType;

        spc.updateSpc(-1);
        sb.append(spc.spc).append("}");
        oneNewLine(n, "2_end");
        break;

      case 3:
        // %3 ExpansionUnitInTCF()
        if (Globals.PRINT_CLASS_COMMENT) {
          sb.append(spc.spc).append("// ExpansionUnit type 3");
          oneNewLine(n, "3_beg");
        }

        n.f0.choice.accept(this);
        break;

      case 4:
        // %4 #0 [ $0 PrimaryExpression() $1 "=" ]
        // .. #1 ( &0 $0 Identifier() $1 Arguments()
        // .. .. | &1 $0 RegularExpression()
        // .. .. . .. $1 [ £0 "." £1 < IDENTIFIER > ] )

        // #0 [ $0 PrimaryExpression() $1 "=" ]
        // TODO process ???
        //        opt = (NodeOptional) seq.elementAt(0);
        //        if (opt.present()) {
        //          sb.append(spc.spc);
        //          ((NodeSequence) opt.node).elementAt(0).accept(this);
        //          sb.append(" = ");
        //        }
        // #1 ...
        seq = (NodeSequence) n.f0.choice;
        ch = (NodeChoice) seq.elementAt(1);
        final NodeSequence seq1 = (NodeSequence) ch.choice;
        if (ch.which == 0) {
          // $0 Identifier() $1 Arguments()
          if (Globals.depthLevel) {
            sb.append(spc.spc).append("++depthLevel;");
            oneNewLine(n);
          }
          sb.append(spc.spc).append(ret ? "nRes = " : "").append(var).append(".accept(this")
            .append(argu ? ", argu);" : ");");
          oneNewLine(n, "4_Identifier");
          if (Globals.depthLevel) {
            sb.append(spc.spc).append("--depthLevel;");
            oneNewLine(n);
          }
        } else {
          // $0 RegularExpression() $1 [ £0 "." £1 < IDENTIFIER > ]
          if (Globals.depthLevel) {
            sb.append(spc.spc).append("++depthLevel;");
            oneNewLine(n);
          }
          sb.append(spc.spc).append(ret ? "nRes = " : "").append(var).append(".accept(this")
            .append(argu ? ", argu);" : ");");
          oneNewLine(n, "4_RegularExpression");
          if (Globals.depthLevel) {
            sb.append(spc.spc).append("--depthLevel;");
            oneNewLine(n);
          }
          opt = (NodeOptional) seq1.elementAt(1);
          if (opt.present()) {
            // TODO process ???
            sb.append("// Please report to support : TODO $1 [ £0 \".\" £1 < IDENTIFIER > ]");
            oneNewLine(n, "4_._<_IDENTIFIER_>");
          }
        }
        break;

      case 5:
        // %5 #0 "(" #1 ExpansionChoices() #2 ")"
        // .. #3 ( &0 "+" | &1 "*" | &2 "?" )?
        // visit something within a node list for +, a node list optional for *,
        // a node optional for ?, and something directly for nothing

        seq = (NodeSequence) n.f0.choice;
        opt = (NodeOptional) seq.elementAt(3);
        if (opt.present()) {
          ch = (NodeChoice) opt.node;
          if (ch.which == 0) {
            // "+" modifier : visit something within a node list
            if (Globals.PRINT_CLASS_COMMENT) {
              sb.append(spc.spc).append("// ExpansionUnit type 5 with '+' modifier");
              oneNewLine(n, "5_+");
            }

            if (!"NodeList".equals(type)) {
              type = "NodeList";
              sb.append(spc.spc).append("final NodeList ").append(var).append("Lst = (NodeList) ")
                .append(var).append(";");
              oneNewLine(n);
              var = var.concat("Lst");
            }

            oldLvn = lvn;
            lvnStr = lvn == 0 ? "" : String.valueOf(lvn);
            lvn++;
            sb.append(spc.spc).append("for (int i").append(lvnStr).append(" = 0; i").append(lvnStr)
              .append(" < ").append(var).append(".size(); i").append(lvnStr).append("++) {");
            oneNewLine(n);
            spc.updateSpc(+1);

            ref = var.concat(".elementAt(i").concat(String.valueOf(lvnStr)).concat(")");
            var = var.concat("Ei");
            sb.append(spc.spc).append("final INode ").append(var).append(" = ").append(ref)
              .append(";");
            oneNewLine(n);
            ref = var;

            ++bnfLvl;
            seq.elementAt(1).accept(this);
            --bnfLvl;
            ref = oldRef;
            var = oldVar;
            type = oldType;
            lvn = oldLvn;

            spc.updateSpc(-1);
            sb.append(spc.spc).append("}");
            oneNewLine(n);

          } else if (ch.which == 1) {
            // "*" modifier : visit something within a node list optional
            if (Globals.PRINT_CLASS_COMMENT) {
              sb.append(spc.spc).append("// ExpansionUnit type 5 with '*' modifier");
              oneNewLine(n, "5_*");
            }

            if (!"NodeListOptional".equals(type)) {
              type = "NodeListOptional";
              sb.append(spc.spc).append("final NodeListOptional ").append(var)
                .append("Nlo = (NodeListOptional) ").append(var).append(";");
              oneNewLine(n);
              var = var.concat("Nlo");
            }

            sb.append(spc.spc).append("if (").append(var).append(".present()) {");
            oneNewLine(n);
            spc.updateSpc(+1);

            oldLvn = lvn;
            lvnStr = lvn == 0 ? "" : String.valueOf(lvn);
            lvn++;
            sb.append(spc.spc).append("for (int i").append(lvnStr).append(" = 0; i").append(lvnStr)
              .append(" < ").append(var).append(".size(); i").append(lvnStr).append("++) {");
            oneNewLine(n);
            spc.updateSpc(+1);

            ref = var.concat(".elementAt(i").concat(String.valueOf(lvnStr)).concat(")");
            var = var.concat("Ei");
            sb.append(spc.spc).append("final INode ").append(var).append(" = ").append(ref)
              .append(";");
            oneNewLine(n);
            ref = var;

            ++bnfLvl;
            seq.elementAt(1).accept(this);
            --bnfLvl;
            ref = oldRef;
            var = oldVar;
            type = oldType;
            lvn = oldLvn;

            spc.updateSpc(-1);
            sb.append(spc.spc).append("}");
            oneNewLine(n);

            spc.updateSpc(-1);
            sb.append(spc.spc).append("}");
            oneNewLine(n);

          } else {
            // "?" modifier : visit something within a node optional
            if (Globals.PRINT_CLASS_COMMENT) {
              sb.append(spc.spc).append("// ExpansionUnit type 5 with '?' modifier");
              oneNewLine(n, "5_?");
            }

            if (!"NodeOptional".equals(type)) {
              type = "NodeOptional";
              sb.append(spc.spc).append("final NodeOptional ").append(var)
                .append("Opt = (NodeOptional) ").append(var).append(";");
              oneNewLine(n);
              var = var.concat("Opt");
            }

            sb.append(spc.spc).append("if (").append(var).append(".present()) {");
            oneNewLine(n);
            spc.updateSpc(+1);
            ref = var.concat(".node");

            ++bnfLvl;
            seq.elementAt(1).accept(this);
            --bnfLvl;
            ref = oldRef;
            var = oldVar;
            type = oldType;

            spc.updateSpc(-1);
            sb.append(spc.spc).append("}");
            oneNewLine(n);
          }

        } else {
          // no modifier : visit something directly
          if (Globals.PRINT_CLASS_COMMENT) {
            sb.append(spc.spc).append("// ExpansionUnit type 5 with no modifiers");
            oneNewLine(n, "5_no");
          }

          ++bnfLvl;
          seq.elementAt(1).accept(this);
          --bnfLvl;
          ref = oldRef;
          var = oldVar;
          type = oldType;
        }
        break;

      default:
        Messages.hardErr("n.f0.which = " + String.valueOf(n.f0.which));
        break;

    }

  }

  /**
   * Visits a {@link ExpansionUnitInTCF} node, whose children are the following :
   * <p>
   * f0 -> "try"<br>
   * f1 -> "{"<br>
   * f2 -> ExpansionChoices()<br>
   * f3 -> "}"<br>
   * f4 -> ( #0 "catch" #1 "(" #2 Name() #3 < IDENTIFIER > #4 ")" #5 Block() )*<br>
   * f5 -> [ #0 "finally" #1 Block() ]<br>
   * 
   * @param n the node to visit
   */
  @Override
  public void visit(final ExpansionUnitInTCF n) {
    // f0 -> "try"
    sb.append(spc.spc).append(genJavaBranch(n.f0));
    // f1 -> "{"
    sb.append(" ").append(genJavaBranch(n.f1));
    oneNewLine(n);
    spc.updateSpc(+1);
    // f2 -> ExpansionChoices()
    n.f2.accept(this);
    // f3 -> "}"
    spc.updateSpc(-1);
    sb.append(spc.spc).append("}");
    oneNewLine(n);
    // f4 -> ( #0 "catch" #1 "(" #2 Name() #3 < IDENTIFIER > #4 ")" #5 Block() )*
    if (n.f4.present())
      for (int i = 0; i < n.f4.size(); i++) {
        final NodeSequence seq = (NodeSequence) n.f4.elementAt(i);
        // #0 "catch"
        sb.append(spc.spc).append(genJavaBranch(seq.elementAt(0)));
        // #1 "("
        sb.append(" ").append(genJavaBranch(seq.elementAt(1)));
        // #2 Name()
        sb.append(genJavaBranch(seq.elementAt(2)));
        // #3 < IDENTIFIER >
        sb.append(" ").append(genJavaBranch(seq.elementAt(3)));
        // #4 ")"
        sb.append(genJavaBranch(seq.elementAt(4)));
        // #5 Block()
        sb.append(spc.spc).append(genJavaBranch(seq.elementAt(5)));
        oneNewLine(n);
      }
    // f5 -> [ #0 "finally" #1 Block() ]
    if (n.f5.present()) {
      final NodeSequence seq = (NodeSequence) n.f5.node;
      // #0 "finally"
      sb.append(spc.spc).append(genJavaBranch(seq.elementAt(0)));
      // #1 Block()
      sb.append(" ").append(genJavaBranch(seq.elementAt(1)));
      oneNewLine(n);
    }
  }

  /**
   * Visits a {@link Identifier} node, whose children are the following :
   * <p>
   * f0 -> < IDENTIFIER ><br>
   * 
   * @param n the node to visit
   */
  @Override
  public void visit(final Identifier n) {
    sb.append(UnicodeConverter.addUnicodeEscapes(n.f0.tokenImage));
  }

  //  /**
  //   * Visits a {@link IntegerLiteral} node, whose children are the following :
  //   * <p>
  //   * f0 -> < INTEGER_LITERAL ><br>
  //   *
  //   * @param n the node to visit
  //   */
  //  @Override
  //  public void visit(final IntegerLiteral n) {
  //    sb.append(UnicodeConverter.addUnicodeEscapes(n.f0.tokenImage));
  //  }
  //
  //  /**
  //   * Visits a {@link StringLiteral} node, whose children are the following :
  //   * <p>
  //   * f0 -> < STRING_LITERAL ><br>
  //   *
  //   * @param n the node to visit
  //   */
  //  @Override
  //  public void visit(final StringLiteral n) {
  //    sb.append(UnicodeConverter.addUnicodeEscapes(n.f0.tokenImage));
  //  }
  //
}

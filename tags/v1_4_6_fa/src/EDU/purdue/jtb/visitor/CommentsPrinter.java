package EDU.purdue.jtb.visitor;

import java.util.ArrayList;
import java.util.Iterator;

import EDU.purdue.jtb.misc.ClassInfo;
import EDU.purdue.jtb.misc.FieldNameGenerator;
import EDU.purdue.jtb.misc.Globals;
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
import EDU.purdue.jtb.syntaxtree.NodeToken;
import EDU.purdue.jtb.syntaxtree.RegExprSpec;
import EDU.purdue.jtb.syntaxtree.RegularExprProduction;
import EDU.purdue.jtb.syntaxtree.RegularExpression;
import EDU.purdue.jtb.syntaxtree.StringLiteral;

/**
 * The CommentsPrinter visitor (an extension of {@link JavaCCPrinter visitor}) is called by
 * {@link ClassInfo} (through {@link #visit(ExpansionChoices)} to find which part of the production
 * each field corresponds, and to format the class, method or field corresponding javadoc comments.
 * <p>
 * Each field comment is terminated by a break tag and a newline, and may be splitted, for better
 * readability, in different lines, with proper indentation (with spaces " " and dots "."), on a new
 * choice (after a "|") or on a new expansion choice ("("...")" or "["..."]).
 * <p>
 * Examples:
 * <p>
 * 1 - Expansion at first BNF level: <code>f0...fn</code> show each ExpansionUnit:<br>
 * <code>
 * f0 -> "TOKEN_MGR_DECLS"<br>
 * f1 -> ":"<br>
 * f2 -> ClassOrInterfaceBody()<br>
 * </code>
 * <p>
 * 2 - Choices at first BNF level (not included in an ExpansionUnit "(...)": <code>%0...%n</code>
 * show the which indicator for each Expansion (note the dummy dot on first line):<br>
 * <code>
 * f0 -> . %0 JavaCodeProduction()<br>
 * .. .. | %1 RegularExprProduction()<br>
 * .. .. | %2 TokenManagerDecls()<br>
 * .. .. | %3 BNFProduction()<br>
 * </code>
 * <p>
 * 3 - Choices at non first BNF level (included in an ExpansionUnit with parentheses "(...)":
 * <code>%0...%n</code> show the which indicator for each Expansion (note the parentheses):<br>
 * <code>
 * f0 -> ( %0 "public"<br>
 * .. .. | %1 "protected"<br>
 * .. .. | %2 "private" )?<br>
 * </code>
 * <p>
 * 4 - Choices at two levels: <code>%0...%n</code> show the which indicator for level 1,
 * <code>%0...%n</code> show the which indicator for level 1, <code>&0...&n</code> show the which
 * indicator for level 2, <code>~1...~n</code> show the which indicator for level 3, and back again
 * for the next levels:<br>
 * <code>
 * f0 -> . %0 #0 ( &0 "+"<br>
 * .. .. . .. .. | &1 "-" ) #1 UnaryExpression()<br>
 * .. .. | %1 PreIncrementExpression()<br>
 * .. .. | %2 PreDecrementExpression()<br>
 * .. .. | %3 UnaryExpressionNotPlusMinus()<br>
 * </code>
 * <p>
 * 5 - Sequence of ExpansionUnits (in an Expansion in an ExpansionChoice in a ExpansionUnit):
 * <code>#0...#n</code> show the sequence number for each ExpansionUnit (notice that here in f1
 * there is no Sequence, so no number is shown, unlike in f2 where there are 2 nodes):<br>
 * <code>
 * f0 -> RegularExpression()<br>
 * f1 -> [ Block() ]<br>
 * f2 -> [ #0 ":" #1 < IDENTIFIER > ]<br>
 * </code>
 * <p>
 * 6 - Sequence of ExpansionUnits (in an Expansion in an ExpansionChoice in a ExpansionUnit) at two
 * or more levels: <code>#0...#n</code> show the sequence number for level 1, <code>$0...$n</code>
 * show the sequence number for level 2, <code>£0...£n</code> show the sequence number for next
 * level, and back again for the next levels:<br>
 * <code>
 * f0 -> "JAVACODE"<br>
 * f1 -> AccessModifier()<br>
 * f2 -> ResultType()<br>
 * f3 -> Identifier()<br>
 * f4 -> FormalParameters()<br>
 * f5 -> [ #0 "throws" #1 Name()<br>
 * .. .. . #2 ( $0 "," $1 Name() )* ]<br>
 * f6 -> Block()<br>
 * </code>
 * <p>
 * 7 - Example showing almost all features (%and & for choices, #, $ and £ for sequences):<br>
 * <code>
 * f0 -> . %0 #0 "LOOKAHEAD" #1 "(" #2 LocalLookahead() #3 ")"<br>
 * .. .. | %1 Block()<br>
 * .. .. | %2 #0 "[" #1 ExpansionChoices() #2 "]"<br>
 * .. .. | %3 #0 "try" #1 "{" #2 ExpansionChoices() #3 "}"<br>
 * .. .. . .. #4 ( $0 "catch" $1 "(" $2 Name() $3 < IDENTIFIER > $4 ")" $5 Block() )*<br>
 * .. .. . .. #5 [ $0 "finally" $1 Block() ]<br>
 * .. .. | %4 #0 [ $0 PrimaryExpression() $1 "=" ]<br>
 * .. .. . .. #1 ( &0 $0 Identifier() $1 Arguments()<br>
 * .. .. . .. .. | &1 $0 RegularExpression()<br>
 * .. .. . .. .. . .. $1 [ £0 "." £1 < IDENTIFIER > ] )<br>
 * .. .. | %5 #0 "(" #1 ExpansionChoices() #2 ")"<br>
 * .. .. . .. #3 ( &0 "+"<br>
 * .. .. . .. .. | &1 "*"<br>
 * .. .. . .. .. | &2 "?" )?<br>
 * </code>
 * 
 * @author Marc Mazas, mmazas@sopragroup.com
 * @version 1.4.0 : 05-08/2009 : MMa : adapted to JavaCC v4.2 grammar and JDK 1.5<br>
 * @version 1.4.3.1 : 04/2010 : MMa : fixed case 4 of getExpUnitBaseNodeType (bug n°2990962)<br>
 *          TODO changer les .. et ... en nombre adéquats si Globals.descriptiveFieldNames
 */
public class CommentsPrinter extends JavaCCPrinter {

  /** The comment sequence level (0 -> none, 1 -> first, 2 -> second, ...) */
  int                          seqLvl  = 0;
  /** The choice sequence level (0 -> none, 1 -> first, 2 -> second, ...) */
  int                          chLvl   = 0;
  /** The comment prefix */
  StringBuffer                 prefix  = new StringBuffer(32);
  /** The which indicator characters */
  public static final String[] WHCH    = {
      "%", "&", "~"                   };
  /** The sequence characters */
  public static final String[] SEQCH   = {
      "#", "$", "£"                   };
  /** The field name generator */
  final FieldNameGenerator     nameGen = new FieldNameGenerator();
  /**
   * The list of class fields for receiving the javadoc comments (must be valid)<br>
   * Start with " * " but without indentation, and may be on multiple lines
   */
  public ArrayList<String>     fc      = null;

  /**
   * Constructor which just allocates the internal buffer.
   */
  public CommentsPrinter() {
    sb = new StringBuilder(512);
  }

  /*
   * Base classes visit methods
   */

/**
   * Prints a NodeToken image taking care of '<' and '>' characters.
   *
   * @param n the NodeToken
   */
  @Override
  public void visit(final NodeToken n) {
    // change <XXX> into < XXX > for proper display of comments in Eclipse
    for (int i = 0; i < n.tokenImage.length(); ++i) {
      final char c = n.tokenImage.charAt(i);
      if (c == '<') {
        i++;
        if (i < n.tokenImage.length()) {
          final char d = n.tokenImage.charAt(i);
          sb.append("< ");
          if (d != ' ') {
            sb.append(d);
          }
        } else {
          sb.append('<');
        }
      } else if (c == '>') {
        if (' ' != sb.charAt(sb.length() - 1)) {
          sb.append(" ");
        }
        sb.append(">");
      } else {
        sb.append(c);
      }
    }
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
   * @param n the node to visit
   */
  @Override
  public void visit(final ExpansionChoices n) {

    // only f0
    if (!n.f1.present()) {
      n.f0.accept(this);
      if (bnfLvl == 0) {
        sb.append("<br>").append(LS);
        fc.add(sb.toString());
        sb.setLength(0);
      }
      return;
    }

    // f0 and f1
    final boolean bigWh = n.f1.size() > 9;
    String locPrefix = prefix.toString();

    // f0 -> Expansion(c1)
    if (bnfLvl == 0) {
      // if first ExpansionChoices level, generate field name
      final String fx = nameGen.genCommentFieldName(Globals.nodeChoiceName);
      // output which indicator
      sb.append(" * ").append(fx).append(" -> . %0").append(bigWh ? "0 " : " ");
      // set and save prefix
      reinitPrefix(fx);
      locPrefix = prefix.toString();
      prefix.append(bigWh ? ". ... " : ". .. ");
    } else {
      // if non first ExpansionChoices levels, output which indicator
      sb.append(WHCH[(chLvl % 3)]).append(bigWh ? "00 " : "0 ");
    }

    // save state info
    final int oldChLvl = chLvl++;

    // visit
    ++bnfLvl;
    n.f0.accept(this);
    --bnfLvl;

    // restore state info
    chLvl = oldChLvl;
    restorePrefix(locPrefix);

    // f1 -> ( "|" Expansion(c2) )*
    int fi = 1;
    for (final Iterator<INode> e = n.f1.elements(); e.hasNext();) {
      final NodeSequence seq = (NodeSequence) e.next();

      // new line and prefix
      sb.append("<br>").append(LS);
      sb.append(prefix);

      // "|"
      sb.append("| ");
      // output which indicator
      sb.append(WHCH[(chLvl++ % 3)]).append(bigWh ? (fi > 9 ? "" : "0") : "").append(fi)
        .append(" ");
      // update prefix
      prefix.append(bigWh ? ". ... " : ". .. ");
      fi++;

      // visit
      ++bnfLvl;
      // Expansion(c2)
      seq.elementAt(1).accept(this);
      --bnfLvl;

      // restore state info
      chLvl = oldChLvl;
      restorePrefix(locPrefix);

    }

    // close comment with last field
    if (bnfLvl == 0) {
      sb.append("<br>").append(LS);
      fc.add(sb.toString());
      sb.setLength(0);
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
    // don't take f0, only f1

    // count the number of non LocalLookahead nor Block ExpansionUnits
    int nbEuOk = 0;
    for (final Iterator<INode> e = n.f1.elements(); e.hasNext();) {
      final ExpansionUnit expUnit = (ExpansionUnit) e.next();
      if (expUnit.f0.which > 1)
        nbEuOk++;
    }

    final boolean outSeq = nbEuOk > 1;
    final boolean bigSeq = nbEuOk > 10;
    int numEuOk = 0;

    // save state info
    final int oldSeqLvl = seqLvl;
    String locPrefix = prefix.toString();

    // f1 -> ( ExpansionUnit(c2) )+
    for (final Iterator<INode> e = n.f1.elements(); e.hasNext();) {
      final ExpansionUnit expUnit = (ExpansionUnit) e.next();
      // don't process LocalLookahead nor Block in ExpansionUnit
      if (expUnit.f0.which > 1) {

        if (bnfLvl == 0) {
          // at first Expansion level
          if (sb.length() > 0) {
            // if already something, new comment
            sb.append("<br>").append(LS);
            fc.add(sb.toString());
            sb.setLength(0);
          }
          // output generated field
          final String fx = nameGen.genCommentFieldName(getExpUnitBaseNodeType(expUnit));
          sb.append(" * ").append(fx).append(" -> ");
          // set and save prefix
          reinitPrefix(fx);
          locPrefix = prefix.toString();
        } else {
          // at non first Expansion levels
          if (numEuOk > 0 && (expUnit.f0.which == 5 || expUnit.f0.which == 2)) {
            // new line if (...) or [...] and not first
            sb.append("<br>").append(LS);
            sb.append(prefix);
          } else {
            // otherwise on same line (don't double space)
            if (sb.charAt(sb.length() - 1) != ' ')
              sb.append(" ");
          }
        }

        // if real sequence output sequence number except at first Expansion level
        if (bnfLvl > 0 && outSeq) {
          sb.append(SEQCH[(seqLvl % 3)]).append(bigSeq && numEuOk < 10 ? "0" : "").append(numEuOk)
            .append(" ");
          seqLvl++;
          // pre-update prefix with sequence number
          prefix.append(bigSeq ? "... " : ".. ");
        }

        // pre-update prefix with delimiter location, only for ExpansionChoices with no choice
        if (expUnit.f0.which == 5 || expUnit.f0.which == 2) {
          final NodeSequence seq = (NodeSequence) expUnit.f0.choice;
          final ExpansionChoices ec = (ExpansionChoices) seq.elementAt(1);
          if (!ec.f1.present())
            prefix.append(". ");
        }

        // visit
        ++bnfLvl;
        expUnit.accept(this);
        --bnfLvl;

        // restore state info
        seqLvl = oldSeqLvl;
        restorePrefix(locPrefix);

        numEuOk++;
      }
    }

  }

  /**
   * Returns the base node type corresponding to the given ExpansionUnit.<br>
   * 4.2 Grammar production for ExpansionUnit:<br>
   * f0 -> . %0 #0 "LOOKAHEAD" #1 "(" #2 LocalLookahead() #3 ")"<br>
   * .. .. | %1 Block()<br>
   * .. .. | %2 #0 "[" #1 ExpansionChoices() #2 "]"<br>
   * .. .. | %3 #0 "try" #1 "{" #2 ExpansionChoices() #3 "}"<br>
   * .. .. . .. #4 ( $0 "catch" $1 "(" $2 Name() $3 < IDENTIFIER > $4 ")" $5 Block() )*<br>
   * .. .. . .. #5 [ $0 "finally" $1 Block() ]<br>
   * .. .. | %4 #0 [ $0 PrimaryExpression() $1 "=" ]<br>
   * .. .. . .. #1 ( &0 $0 Identifier() $1 Arguments()<br>
   * .. .. . .. .. | &1 $0 RegularExpression()<br>
   * .. .. . .. .. . .. $1 [ £0 "." £1 < IDENTIFIER > ] )<br>
   * .. .. | %5 #0 "(" #1 ExpansionChoices() #2 ")"<br>
   * .. .. . .. #3 ( &0 "+"<br>
   * .. .. . .. .. | &1 "*"<br>
   * .. .. . .. .. | &2 "?" )?<br>
   * 
   * @param n the node
   * @return the base node type
   */
  //  private String getUnitName(final ExpansionUnit n) {
  private String getExpUnitBaseNodeType(final ExpansionUnit n) {

    NodeSequence seq;
    ExpansionChoices ec;
    switch (n.f0.which) {
      //
      // cases 0 and 1 should not occur as getUnitName() is not called for LocalLookahead nor Block
      //

      case 2:
        // %2 #0 "[" #1 ExpansionChoices() #2 "]"
        return Globals.nodeOptName;

      case 3:
        // %3 #0 "try" #1 "{" #2 ExpansionChoices() #3 "}"
        // .. #4 ( $0 "catch" $1 "(" $2 Name() $3 < IDENTIFIER > $4 ")" $5 Block() )*
        // .. #5 [ $0 "finally" $1 Block() ]
        //        seq = (NodeSequence) n.f0.choice;
        //        ec = (ExpansionChoices) seq.elementAt(2);
        //        if (ec.f1.present())
        //          // f1 -> ( "|" Expansion(c2) )*
        //          return Globals.nodeChoiceName;
        //        else
        //          // f0 -> Expansion(c1)
        //          return Globals.nodeSeqName;
        final ExpansionUnitInTCF eut3 = (ExpansionUnitInTCF) n.f0.choice;
        ec = eut3.f2;
        if (ec.f1.present())
          // f1 -> ( "|" Expansion(c2) )*
          return Globals.nodeChoiceName;
        else
          // f0 -> Expansion(c1)
          return Globals.nodeSeqName;

      case 4:
        // %4 #0 [ $0 PrimaryExpression() $1 "=" ]
        // .. #1 ( &0 $0 Identifier() $1 Arguments()
        // .. .. | &1 $0 RegularExpression()
        // .. .. . .. $1 [ £0 "." £1 < IDENTIFIER > ] )
        seq = (NodeSequence) n.f0.choice;
        final NodeChoice ch = (NodeChoice) seq.elementAt(1);
        final NodeSequence seq1 = (NodeSequence) ch.choice;
        if (ch.which == 0) {
          // $0 Identifier() $1 Arguments()
          return ((Identifier) seq1.elementAt(0)).f0.tokenImage;
        } else {
          // $0 RegularExpression() $1 [ £0 "." £1 < IDENTIFIER > ]
          // grammar for RegularExpression
          // f0 -> . %0 StringLiteral()<br>
          // .. .. | %1 #0 < LANGLE : "<" ><br>
          // .. .. . .. #1 [ $0 [ "#" ] $1 Identifier() $2 ":" ] #2 ComplexRegularExpressionChoices() #3 < RANGLE : ">" ><br>
          // .. .. | %2 #0 "<" #1 Identifier() #2 ">"<br>
          // .. .. | %3 #0 "<" #1 "EOF" #2 ">"<br>
          return Globals.nodeTokenName;
        }

      case 5:
        // %5 #0 "(" #1 ExpansionChoices() #2 ")"
        // .. #3 ( &0 "+" | &1 "*" | &2 "?" )?
        seq = (NodeSequence) n.f0.choice;
        final NodeOptional ebnfMod = (NodeOptional) seq.elementAt(3);
        if (ebnfMod.present()) {
          // case there is a "+" or "*" or "?"
          final NodeChoice modChoice = (NodeChoice) ebnfMod.node;
          final String mod = ((NodeToken) modChoice.choice).tokenImage;
          return nameGen.getNameForMod(mod);
        } else {
          ec = (ExpansionChoices) seq.elementAt(1);
          if (ec.f1.present())
            return Globals.nodeChoiceName;
          else
            return Globals.nodeSeqName;
        }

      default:
        Messages.hardErr("n.f0.which = " + String.valueOf(n.f0.which));
        break;
    }
    throw new Error("Error in CommentsPrinter.getExpUnitBaseNodeType()");
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
    NodeSequence seq;
    NodeOptional opt;
    NodeChoice ch;

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
        seq = (NodeSequence) n.f0.choice;
        sb.append("[ ");
        ++bnfLvl;
        seq.elementAt(1).accept(this);
        --bnfLvl;
        sb.append(" ]");
        break;

      case 3:
        // %3 ExpansionUnitInTCF()
        n.f0.choice.accept(this);
        break;

      case 4:
        // %4 #0 [ $0 PrimaryExpression() $1 "=" ]
        // .. #1 ( &0 $0 Identifier() $1 Arguments()
        // .. .. | &1 $0 RegularExpression()
        // .. .. . .. $1 [ £0 "." £1 < IDENTIFIER > ] )
        seq = (NodeSequence) n.f0.choice;
        ch = (NodeChoice) seq.elementAt(1);
        final NodeSequence seq1 = (NodeSequence) ch.choice;
        if (ch.which == 0) {
          // $0 Identifier() $1 Arguments()
          seq1.elementAt(0).accept(this);
          // don't print arguments, only parenthesis (for lisibility)
          //          sb.append(genJavaBranch(seq1.elementAt(1)));
          sb.append("()");
        } else {
          // $0 RegularExpression() $1 [ £0 "." £1 < IDENTIFIER > ]
          seq1.elementAt(0).accept(this);
          final NodeOptional opt1 = (NodeOptional) seq1.elementAt(1);
          if (opt1.present()) {
            sb.append(".");
            ((NodeSequence) opt1.node).elementAt(1).accept(this);
          }
        }
        break;

      case 5:
        // %5 #0 "(" #1 ExpansionChoices() #2 ")"
        // .. #3 ( &0 "+" | &1 "*" | &2 "?" )?
        seq = (NodeSequence) n.f0.choice;
        // #0 "("
        sb.append("( ");
        // #1 ExpansionChoices()
        ++bnfLvl;
        seq.elementAt(1).accept(this);
        --bnfLvl;
        // #2 ")"
        sb.append(" )");
        // #3 ( &0 "+" | &1 "*" | &2 "?" )?
        opt = (NodeOptional) seq.elementAt(3);
        if (opt.present()) {
          ((NodeChoice) opt.node).choice.accept(this);
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
    // f1 -> "{"
    sb.insert(0, " * try {<br>".concat(LS));
    // f2 -> ExpansionChoices()
    n.f2.accept(this);
    // f3 -> "}"
    sb.append("<br>").append(LS).append(" * }");
    // f4 -> ( #0 "catch" #1 "(" #2 Name() #3 < IDENTIFIER > #4 ")" #5 Block() )*
    for (int i = 0; i < n.f4.size(); i++)
      sb.append("<br>").append(LS).append(" * catch (Name() < IDENTIFIER >) Block()");
    // f5 -> [ #0 "finally" #1 Block() ]
    if (n.f5.present())
      sb.append("<br>").append(LS).append(" * finally Block()");
  }

/**
   * Visits a {@link RegularExprProduction} node, whose children are the following :
   * <p>
   * f0 -> [ %0 #0 "<" #1 "*" #2 ">"<br>
   * .. .. | %1 #0 "<" #1 < IDENTIFIER ><br>
   * .. .. . .. #2 ( $0 "," $1 < IDENTIFIER > )* #3 ">" ]<br>
   * f1 -> RegExprKind()<br>
   * f2 -> [ #0 "[" #1 "IGNORE_CASE" #2 "]" ]<br>
   * f3 -> ":"<br>
   * f4 -> "{"<br>
   * f5 -> RegExprSpec()<br>
   * f6 -> ( #0 "|" #1 RegExprSpec() )*<br>
   * f7 -> "}"<br>
   *
   * @param n the node to visit
   */
  @Override
  public void visit(final RegularExprProduction n) {
    // f5 -> RegExprSpec()
    n.f5.accept(this);
    // f6 -> ( "|" RegExprSpec(p) )*
    if (n.f6.present()) {
      for (final Iterator<INode> e = n.f6.elements(); e.hasNext();) {
        final NodeSequence seq = (NodeSequence) e.next();
        // "|"
        sb.append("| ");
        // RegExprSpec(p)
        seq.elementAt(1).accept(this);
      }
    }
  }

  /**
   * Visits a {@link RegExprSpec} node, whose children are the following :
   * <p>
   * f0 -> RegularExpression()<br>
   * f1 -> [ Block() ]<br>
   * f2 -> [ #0 ":" #1 < IDENTIFIER > ]<br>
   * 
   * @param n the node to visit
   */
  @Override
  public void visit(final RegExprSpec n) {
    // f0 -> RegularExpression(c)
    n.f0.accept(this);
  }

/**
   * Visits a {@link RegularExpression} node, whose children are the following :
   * <p>
   * f0 -> . %0 StringLiteral()<br>
   * .. .. | %1 #0 < LANGLE : "<" ><br>
   * .. .. . .. #1 [ $0 [ "#" ] $1 Identifier() $2 ":" ] #2 ComplexRegularExpressionChoices() #3 < RANGLE : ">" ><br>
   * .. .. | %2 #0 "<" #1 Identifier() #2 ">"<br>
   * .. .. | %3 #0 "<" #1 "EOF" #2 ">"<br>
   *
   * @param n the node to visit
   */
  @Override
  public void visit(final RegularExpression n) {
    if (n.f0.which == 0)
      // StringLiteral()
      n.f0.choice.accept(this);
    else if (n.f0.which == 1) {
      // <LANGLE: "<"> [ [ "#" ] Identifier() ":" ] ComplexRegularExpressionChoices(c) <RANGLE: ">">
      final NodeSequence seq = (NodeSequence) n.f0.choice;
      // "<"
      sb.append("< ");
      final NodeOptional opt = (NodeOptional) seq.elementAt(1);
      if (opt.present()) {
        final NodeSequence seq1 = (NodeSequence) opt.node;
        if (((NodeOptional) seq1.elementAt(0)).present())
          // "#"
          sb.append("#");
        // Identifier()
        seq1.elementAt(1).accept(this);
        sb.append(" ");
        // ":"
        sb.append(": ");
      }
      // ComplexRegularExpressionChoices(c)
      // here we can use super class (JavaCCPrinter) methods which do not add newlines nor indentation
      seq.elementAt(2).accept(this);
      // ">"
      sb.append(" >");
    } else if (n.f0.which == 2) {
      // "<" Identifier() ">"
      // "<"
      sb.append("< ");
      // Identifier()
      ((NodeSequence) n.f0.choice).elementAt(1).accept(this);
      // ">"
      sb.append(" >");
    } else {
      // "<" "EOF" ">"
      // "<"
      sb.append("< ");
      // "EOF"
      ((NodeSequence) n.f0.choice).elementAt(1).accept(this);
      // ">"
      sb.append(" >");
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
  /**
   * Visits a {@link StringLiteral} node, whose children are the following :
   * <p>
   * f0 -> < STRING_LITERAL ><br>
   * 
   * @param n the node to visit
   */
  @Override
  public void visit(final StringLiteral n) {
    sb.append(UnicodeConverter.addUnicodeEscapes(n.f0.tokenImage));
  }

  /*
   * Methods to manage prefix
   */

  /**
   * Reinitializes the prefix with the appropriate number of leading dots.
   * 
   * @param fx the current leading field name
   */
  void reinitPrefix(final String fx) {
    final int fxl = fx.length();
    prefix.setLength(0);
    prefix.append(" * ");
    for (int i = 0; i < fxl; i++)
      prefix.append(".");
    prefix.append(" .. ");
  }

  /**
   * Restores the prefix with the given saved string.
   * 
   * @param locPrefix the saved string
   */
  void restorePrefix(final String locPrefix) {
    prefix.setLength(0);
    prefix.append(locPrefix);
  }

  /*
   * Methods to format javadoc comments
   */

  /**
   * Formats the javadoc comment for all fields.<br>
   * Does not add the javadoc opening and closing delimiters.
   * 
   * @param aSpc an indentation (must be valid)
   * @param aCI the ClassInfo to work on
   * @return the javadoc comment
   */
  public String formatAllFieldsComment(final Spacing aSpc, final ClassInfo aCI) {
    sb.setLength(0);
    fc = aCI.getFieldComments();
    if (fc.size() == 0)
      genFieldsComments(aCI);
    for (int i = 0; i < fc.size(); i++) {
      sb.append(formatAFieldComment(aSpc, aCI, i));
    }
    return sb.toString();
  }

  /**
   * Formats the javadoc comment for a single field.<br>
   * Does not add the javadoc opening and closing delimiters.
   * 
   * @param aSpc an indentation (must be valid)
   * @param fi the field index (must be valid)
   * @param aCI the ClassInfo to work on
   * @return the javadoc comment
   */
  public String formatAFieldComment(final Spacing aSpc, final ClassInfo aCI, final int fi) {
    fc = aCI.getFieldComments();
    if (fc.size() == 0)
      genFieldsComments(aCI);
    final String fci = fc.get(fi);
    final int lsl = LS.length();
    if (aSpc.indentLevel == 0) {
      // no need to add indentation
      return fci;
    } else {
      // need to add indentation
      int k = fci.indexOf(LS);
      final int lm1 = fci.length() - 1;
      if (k >= 0 && k < lm1) {
        // need to break out lines to add indentation
        int j = 0;
        final StringBuilder buf = new StringBuilder(128);
        while (k >= 0) {
          k = k + lsl;
          buf.append(aSpc.spc).append(fci.substring(j, k));
          j = k;
          k = fci.indexOf(LS, j);
        }
        if (j <= lm1)
          buf.append(aSpc.spc).append(fci.substring(j));
        return buf.toString();
      } else {
        // just one line, add indentation
        return aSpc.spc.concat(fci);
      }
    }
  }

  /**
   * Generates the fields comments.
   * 
   * @param aCI the ClassInfo to work on
   */
  void genFieldsComments(final ClassInfo aCI) {
    nameGen.reset();
    bnfLvl = seqLvl = chLvl = 0;
    prefix.setLength(0);
    aCI.getAstNode().accept(this);
    aCI.setFieldComments(fc);
  }

}

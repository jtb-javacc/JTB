package EDU.purdue.jtb.visitor;

import static EDU.purdue.jtb.misc.Globals.PRINT_CLASS_COMMENT;
import static EDU.purdue.jtb.misc.Globals.inlineAcceptMethods;

import java.util.ArrayList;
import java.util.Iterator;

import EDU.purdue.jtb.misc.ClassInfo;
import EDU.purdue.jtb.misc.CommentData;
import EDU.purdue.jtb.misc.CommentLineData;
import EDU.purdue.jtb.misc.ClassInfoForJava;
import EDU.purdue.jtb.misc.Messages;
import EDU.purdue.jtb.misc.UnicodeConverter;
import EDU.purdue.jtb.syntaxtree.Expansion;
import EDU.purdue.jtb.syntaxtree.ExpansionChoices;
import EDU.purdue.jtb.syntaxtree.ExpansionUnit;
import EDU.purdue.jtb.syntaxtree.ExpansionUnitTCF;
import EDU.purdue.jtb.syntaxtree.INode;
import EDU.purdue.jtb.syntaxtree.IdentifierAsString;
import EDU.purdue.jtb.syntaxtree.NodeChoice;
import EDU.purdue.jtb.syntaxtree.NodeOptional;
import EDU.purdue.jtb.syntaxtree.NodeSequence;
import EDU.purdue.jtb.syntaxtree.NodeToken;
import EDU.purdue.jtb.syntaxtree.StringLiteral;

/**
 * The {@link CommentsPrinter} visitor (an extension of {@link JavaCCPrinter visitor}) is called by
 * {@link ClassInfoForJava} (through {@link #visit(ExpansionChoices)} to find which part of the production
 * each field corresponds, and to format the class, method or field corresponding javadoc comments.
 * <p>
 * {@link AnnotatorForJava}, {@link CommentsPrinter}, {@link ClassesFinder} depend on each other to create
 * and use classes. A field comment should be created for each field, but visiting methods of
 * CommentsPrinter and {@link ClassesFinder} have been coded quite independently, so some checking
 * and rewriting could be welcomed.<br>
 * <p>
 * Each field comment is terminated by a break tag and a newline, and may be splitted, for better
 * readability, in different lines, with proper indentation (with spaces ' ' and dots '.'), on a new
 * {@link Expansion} in an {@link ExpansionChoices} (after a '|') or on a new {@link ExpansionUnit}
 * of type 5 ('('...')', '('...')''?', '('...')''+', '('...')''*') or of type 2 ('['...']').
 * <p>
 * Examples:
 * <p>
 * 1 - Expansion at first BNF level: <code>f0...fN</code> show each ExpansionUnit:<br>
 * <code>
 * f0 -> "TOKEN_MGR_DECLS"<br>
 * f1 -> ":"<br>
 * f2 -> ClassOrInterfaceBody()<br>
 * </code>
 * <p>
 * 2 - Choices at first BNF level (not included in an ExpansionUnit "(...)": <code>%0...%N</code>
 * show the which indicator for each Expansion (note the dummy dot on first line):<br>
 * <code>
 * f0 -> . %0 JavaCodeProduction()<br>
 * .. .. | %1 RegularExprProduction()<br>
 * .. .. | %2 TokenManagerDecls()<br>
 * .. .. | %3 BNFProduction()<br>
 * </code>
 * <p>
 * 3 - Choices at non first BNF level (included in an ExpansionUnit with parentheses "(...)":
 * <code>%0...%N</code> show the which indicator for each Expansion (note the parentheses):<br>
 * <code>
 * f0 -> ( %0 "public"<br>
 * .. .. | %1 "protected"<br>
 * .. .. | %2 "private" )?<br>
 * </code>
 * <p>
 * 4 - Choices at two levels: <code>%0...%n</code> show the which indicator for level 1,
 * <code>%0...%N</code> show the which indicator for level 1, <code>&0...&N</code> show the which
 * indicator for level 2, <code>~1...~N</code> show the which indicator for level 3, and back again
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
 * <code>#0...#N</code> show the sequence number for each ExpansionUnit (notice that here in f1
 * there is no Sequence, so no number is shown, unlike in f2 where there are 2 nodes):<br>
 * <code>
 * f0 -> RegularExpression()<br>
 * f1 -> [ Block() ]<br>
 * f2 -> [ #0 ":" #1 < IDENTIFIER > ]<br>
 * </code>
 * <p>
 * 6 - Sequence of ExpansionUnits (in an Expansion in an ExpansionChoice in a ExpansionUnit) at two
 * or more levels: <code>#0...#N</code> show the sequence number for level 1, <code>$0...$N</code>
 * show the sequence number for level 2, <code>?0...?N</code> show the sequence number for next
 * level, and back again for the next levels:<br>
 * <code>
 * f0 -> "JAVACODE"<br>
 * f1 -> AccessModifier()<br>
 * f2 -> ResultType()<br>
 * f3 -> IdentifierAsString()<br>
 * f4 -> FormalParameters()<br>
 * f5 -> [ #0 "throws" #1 Name()<br>
 * .. .. . #2 ( $0 "," $1 Name() )* ]<br>
 * f6 -> Block()<br>
 * </code>
 * <p>
 * 7 - Example showing almost all previous features (% and & for choices, #, $ and ? for sequences):
 * <br>
 * <code>
 * f0 -> . %0 #0 "LOOKAHEAD" #1 "(" #2 LocalLookahead() #3 ")"<br>
 * .. .. | %1 Block()<br>
 * .. .. | %2 #0 "[" #1 ExpansionChoices() #2 "]"<br>
 * .. .. | %3 #0 "try" #1 "{" #2 ExpansionChoices() #3 "}"<br>
 * .. .. . .. #4 ( $0 "catch" $1 "(" $2 Name() $3 < IDENTIFIER > $4 ")" $5 Block() )*<br>
 * .. .. . .. #5 [ $0 "finally" $1 Block() ]<br>
 * .. .. | %4 #0 [ $0 PrimaryExpression() $1 "=" ]<br>
 * .. .. . .. #1 ( &0 $0 IdentifierAsString() $1 Arguments()<br>
 * .. .. . .. .. | &1 $0 RegularExpression()<br>
 * .. .. . .. .. . .. $1 [ ?0 "." ?1 < IDENTIFIER > ] )<br>
 * .. .. | %5 #0 "(" #1 ExpansionChoices() #2 ")"<br>
 * .. .. . .. #3 ( &0 "+"<br>
 * .. .. . .. .. | &1 "*"<br>
 * .. .. . .. .. | &2 "?" )?<br>
 * </code>
 * <p>
 * 8 - In case of ExpansionUnit with Try / Catch / Finally, each syntax element is on its own line,
 * and <code>!0...!N</code> show the element number for the first inner level, <code>^0...^N</code>
 * and then <code>@0...@N</code> show the element number for the 2 next levels, and back again for
 * the next levels:<br>
 * <code>
 * f0 -> "try"<br>
 * f1 -> "{"<br>
 * f2 -> #0 ( $0 ( ExtendsList() )?<br>
 * .. .. .. . $1 ( ImplementsList() )+<br>
 * .. .. .. . $2 ( Annotation() )*<br>
 * .. .. .. . $3 [ MarkerAnnotation() ]<br>
 * .. .. .. . $4 ( %0 LabeledStatement()<br>
 * .. .. .. . .. | %1 AssertStatement()<br>
 * .. .. .. . .. | %2 ?0 StatementExpression() ?1 ";" ) )<br>
 * .. .. #1 ( TypeParameters() )?<br>
 * .. .. #2 ( VariableDeclarator() )+<br>
 * .. .. #3 ( TypeBound() )*<br>
 * .. .. #4 [ TypeParameter() ]<br>
 * .. .. #5 ( %0 ( !00 "try"<br>
 * .. .. .. . .. . !01 "{"<br>
 * .. .. .. . .. . !02 TryStatement()<br>
 * .. .. .. . .. . !03 "}"<br>
 * .. .. .. . .. . !04 "catch"<br>
 * .. .. .. . .. . !05 "("<br>
 * .. .. .. . .. . !06 Name()<br>
 * .. .. .. . .. . !07 < IDENTIFIER ><br>
 * .. .. .. . .. . !08 ")"<br>
 * .. .. .. . .. . !09 Block()<br>
 * .. .. .. . .. . !10 "catch"<br>
 * .. .. .. . .. . !11 "("<br>
 * .. .. .. . .. . !12 Name()<br>
 * .. .. .. . .. . !13 < IDENTIFIER ><br>
 * .. .. .. . .. . !14 ")"<br>
 * .. .. .. . .. . !15 Block() )<br>
 * .. .. .. | %1 ( !0 "try"<br>
 * .. .. .. . .. . !1 "{"<br>
 * .. .. .. . .. . !2 ( &0 ForStatement()<br>
 * .. .. .. . .. . .. | &1 $0 WhileStatement() $1 ";" )<br>
 * .. .. .. . .. . !3 "}"<br>
 * .. .. .. . .. . !4 "finally"<br>
 * .. .. .. . .. . !5 Block() ) )<br>
 * f3 -> "}"<br>
 * f4 -> "catch"<br>
 * f5 -> "("<br>
 * f6 -> Name()<br>
 * f7 -> < IDENTIFIER ><br>
 * f8 -> ")"<br>
 * f9 -> Block()<br>
 * f10 -> "catch"<br>
 * f11 -> "("<br>
 * f12 -> Name()<br>
 * f13 -> < IDENTIFIER ><br>
 * f14 -> ")"<br>
 * f15 -> Block()<br>
 * f16 -> "finally"<br>
 * f17 -> Block()<br>
 * </code>
 * <p>
 * 
 * @author Marc Mazas
 * @version 1.4.0 : 05-08/2009 : MMa : adapted to JavaCC v4.2 grammar and JDK 1.5
 * @version 1.4.3.1 : 04/2010 : MMa : fixed case 4 of getExpUnitBaseNodeType (bug n°2990962)
 * @version 1.4.7 : 07/2012 : MMa : followed changes in jtbgram.jtb (IndentifierAsString())<br>
 *          1.4.7 : 08-09/2012 : MMa : fixed problems in ExpansionUnitTCF, fixed new lines in
 *          Expansion, generated sub comments ; added non node creation and the reference to the
 *          {@link GlobalDataBuilder}
 */
public class CommentsPrinter extends JavaCCPrinter {

  /** The reference to the global data builder visitor */
  final GlobalDataBuilder     gdbv;
  /**
   * The Expansion level we are in : 0, 1, ... : first, second, ... ; incremented at each level,
   * except in an ExpansionChoice with no choice and in ExpansionUnitTCF
   */
  int                         expLvl;
  /** The ExpansionUnitTCF level we are in : -1 : none; 0, 1, ... : first, second, ... */
  int                         tcfLvl      = -1;
  /** The comment sequence level (0 -> none, 1 -> first, 2 -> second, ...) */
  int                         seqLvl      = 0;
  /** The choice sequence level (0 -> none, 1 -> first, 2 -> second, ...) */
  int                         chLvl       = 0;
  /** The comment prefix */
  StringBuilder               prefix      = new StringBuilder(32);
  /** The which indicator characters */
  public static final char[]  WHCH        = {
      '%', '&', '~'                      };
  /** The sequence indicator characters */
  public static final char[]  SEQCH       = {
      '#', '$', '?'                      };
  /** The (inner) ExpansionUnitTCF indicator characters */
  public static final char[]  TCFCH       = {
      '@', '!', '^'                      };
  /** The separator character between sequence elements */
  public static final char    SEPCH       = ' ';
  /** The prefix for TCF elements */
  public static final String  PFXSTR      = ". ";
  /** The field name index */
  int                         fni;
  /**
   * The roots of the comments (and sub comments if inlining accept methods) trees (one for each
   * field)
   */
  ArrayList<CommentsTreeNode> roots;
  /** * The current (field or sub)= comment tree node we are working on */
  CommentsTreeNode            curCtn;
  /** The current field index */
  int                         fld;
  /** An auxiliary buffer for the field comments */
  StringBuilder               fcb         = null;
  /** An auxiliary buffer for the sub comments */
  StringBuilder               scb         = null;
  /** The ClassInfoForJava the visitor is working on */
  ClassInfo                   classInfo;
  /** A debug switch, to change prefix and field names strings */
  public static final boolean DEBUG_CHARS = false;

  /**
   * Constructor which just allocates the internal buffer.
   * 
   * @param aGdbv - the global data builder visitor
   */
  public CommentsPrinter(final GlobalDataBuilder aGdbv) {
    sb = new StringBuilder(512);
    fcb = new StringBuilder(128);
    if (inlineAcceptMethods)
      scb = new StringBuilder(128);
    gdbv = aGdbv;
  }

  /*
   * New line and comments methods
   */

  /**
   * Prints into the current buffer a node class comment and a new line.
   * 
   * @param n - the node for the node class comment
   */
  @Override
  void oneNewLine(final INode n) {
    if (PRINT_CLASS_COMMENT)
      curCtn.addDebug(nodeClassComment(n));
    curCtn.setNL();
  }

  /**
   * Prints into the current buffer a node class comment, an extra given comment, and a new line.
   * 
   * @param n - the node for the node class comment
   * @param str - the extra comment
   */
  @Override
  void oneNewLine(final INode n, final String str) {
    if (PRINT_CLASS_COMMENT)
      curCtn.addDebug(nodeClassComment(n, str));
    curCtn.setNL();
  }

  /**
   * Ends all current sub comments and switches to the next field comment.
   * 
   * @param n - the node for the node class comment
   * @param str - the extra comment
   */
  void oneNewLineSwitchFieldCmt(final INode n, final String str) {
    oneNewLine(n, str);
    // end field comment
    while (curCtn != null)
      curCtn = curCtn.parent;
    // switch to next field
    fld++;
    if (fld < roots.size()) {
      curCtn = roots.get(fld);
    } else
      // normally no further comment processing should be done if algorithm is OK :) !
      curCtn = null;
  }

  /**
   * Prints into the current buffer a node class comment and a new line, and create a new field
   * comment if at first tcfLvl.
   * 
   * @param n - the node for the node class comment
   */
  void oneNewLineCreFieldCmtAtFirstTcfLvl(final INode n) {
    if (tcfLvl == 0) {
      oneNewLineSwitchFieldCmt(n, "tcfLvl is 0");
    } else
      oneNewLine(n, "tcfLvl is not 0");
  }

  /**
   * Returns a node class comment (a //-- followed by the node class short name if global flag set,
   * null otherwise).
   * 
   * @param n - the node for the node class comment
   * @return the node class comment or null
   */
  String nodeClassComment(final INode n) {
    if (PRINT_CLASS_COMMENT) {
      final String s = n.toString();
      final int b = s.lastIndexOf('.') + 1;
      final int e = s.indexOf('@');
      if (b == -1 || e == -1)
        return " //-- " + s;
      else
        return " //-- " + s.substring(b, e);
    } else
      return null;
  }

  /**
   * Returns a node class comment with an extra comment (a //-- followed by the node class short
   * name plus the extra comment if global flag set, null otherwise).
   * 
   * @param n - the node for the node class comment
   * @param str - the extra comment
   * @return the node class comment or null
   */
  String nodeClassComment(final INode n, final String str) {
    if (PRINT_CLASS_COMMENT)
      return nodeClassComment(n).concat(" ").concat(str);
    else
      return null;
  }

/**
   * Prints a NodeToken image taking care of '<' and '>' characters  (change &lt;XXX&gt; into < XXX >
   * for proper display of comments in Eclipse javadoc views).
   *
   * @param n - the NodeToken
   */
  @Override
  public void visit(final NodeToken n) {
    final int len = n.tokenImage.length();
    for (int i = 0; i < len; ++i) {
      final char c = n.tokenImage.charAt(i);
      if (c == '<') {
        i++;
        if (i < len) {
          final char d = n.tokenImage.charAt(i);
          curCtn.addId("< ");
          if (d != ' ') {
            curCtn.addId(d);
          }
        } else {
          curCtn.addId('<');
        }
      } else if (c == '>') {
        if (i != 0 && ' ' != n.tokenImage.charAt(i - 1)) {
          curCtn.addId(' ');
        }
        curCtn.addId('>');
      } else {
        curCtn.addId(c);
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
   * @param n - the node to visit
   */
  @Override
  public void visit(final ExpansionChoices n) {

    // if only f0
    if (!n.f1.present()) {
      // f0 -> Expansion()
      n.f0.accept(this);
      if (expLvl == 0) {
        // if at first bnf level, end the field comment
        // see ExtendsList, MethodDeclaration
        oneNewLineSwitchFieldCmt(n, "only f0");
      }
      return;
    }

    // else f0 and f1
    final boolean bigWh = n.f1.size() > 9;

    // f0 -> Expansion()
    if (expLvl == 0) {
      // if at first ExpansionChoices level
      // output generated field name (on the new field comment)
      final String fx = classInfo.getFieldNames().get(fni++);
      //  see ReferenceType
      if (DEBUG_CHARS)
        curCtn.addFn(fx, " ?> . ");
      else
        curCtn.addFn(fx, " -> . ");
      reinitPrefix(fx);
    }
    // start a new choice sub comment (that will end on ExpansionChoices element)
    // see UnaryExpression, ReferenceType
    curCtn.newSubComment();
    // output which indicator
    curCtn.addTag(String.valueOf(WHCH[(chLvl % 3)]) + (bigWh ? "00 " : "0 "));
    if (DEBUG_CHARS)
      curCtn.addPfx(bigWh ? "` ``` " : "` `` ");
    else
      curCtn.addPfx(bigWh ? ". ... " : ". .. ");

    // visit Expansion()
    final int oldChLvl = chLvl++;
    ++expLvl;
    n.f0.accept(this);
    --expLvl;
    chLvl = oldChLvl;

    // f1 -> ( #0 "|" #1 Expansion() )*
    int fi = 1;
    for (final Iterator<INode> e = n.f1.elements(); e.hasNext();) {
      final NodeSequence seq = (NodeSequence) e.next();

      // end the previous choice sub comment
      // see TypeDeclaration, MethodDeclaration, ATestTCFProduction
      oneNewLine(n, "element");
      // start a new choice sub comment (that will end on ExpansionChoices element and may be on multiple lines)
      curCtn.endSubComment();
      curCtn.newSubComment();

      // "|"
      curCtn.addChoice("| ");
      // output which indicator
      // see UnaryExpression
      curCtn.addTag(String.valueOf(WHCH[(chLvl % 3)]) + (bigWh && fi < 10 ? "0" : "") + fi + " ");
      chLvl++;
      if (DEBUG_CHARS)
        curCtn.addPfx(bigWh ? "' ''' " : "' '' ");
      else
        curCtn.addPfx(bigWh ? ". ... " : ". .. ");
      fi++;

      // visit Expansion()
      ++expLvl;
      seq.elementAt(1).accept(this);
      --expLvl;
      chLvl = oldChLvl;

    } // end for loop on elements

    // end the previous choice sub comment
    curCtn.endSubComment();

    if (expLvl == 0) {
      // if at first bnf level, end the field comment
      // see ExplicitConstructorInvocation, ReferenceType
      oneNewLineSwitchFieldCmt(n, "last");
    }

  }

  /**
   * Visits a {@link Expansion} node, whose children are the following :
   * <p>
   * f0 -> ( #0 "LOOKAHEAD" #1 "(" #2 LocalLookahead() #3 ")" )?<br>
   * f1 -> ( ExpansionUnit() )+<br>
   * 
   * @param n - the node to visit
   */
  @Override
  public void visit(final Expansion n) {
    // don't take f0, only f1

    // count the number of non LocalLookahead nor Block nor not to be created nodes
    int nbEuOk = 0;
    for (final Iterator<INode> e = n.f1.elements(); e.hasNext();) {
      final ExpansionUnit expUnit = (ExpansionUnit) e.next();
      if (gdbv.isEuOk(expUnit)) {
        nbEuOk++;
      }
    }
    final boolean outSeq = nbEuOk > 1;
    final boolean bigSeq = nbEuOk > 10;
    int numEuOk = 0;

    // save state info early
    final int oldSeqLvl = seqLvl;

    // f1 -> ( ExpansionUnit() )+
    boolean wantedToBeOnNewLine = false;
    final int sz = n.f1.size();
    for (int i = 0; i < sz; i++) {
      final ExpansionUnit expUnit = (ExpansionUnit) n.f1.elementAt(i);
      // don't process LocalLookahead nor Block nor not to be created nodes
      if (gdbv.isEuOk(expUnit)) {

        boolean addSpace = false;
        if (expLvl == 0) {
          // at first Expansion level
          // end the previous field comment if already something (that is when it is not first one), 
          if (numEuOk > 0) {
            // see ImportDeclaration, ClassOrInterfaceDeclaration, EnumBody
            oneNewLineSwitchFieldCmt(n, "b==0");
          }
          // output generated field name (on the new field comment)
          final String fx = classInfo.getFieldNames().get(fni++);
          if (DEBUG_CHARS)
            curCtn.addFn(fx, " :> ");
          else
            curCtn.addFn(fx, " -> ");
          reinitPrefix(fx);
        } else {
          // at non first Expansion levels
          if (expUnit.f0.which == 3) {
            // ExpansionUnitTCF
            curCtn.addPfx(PFXSTR);
          } else if (numEuOk > 0 && (expUnit.f0.which == 2 || expUnit.f0.which == 5)) {
            // new line if (...), (...)?, (...)+, (...)* or [...] and not first ExpansionUnit
            if (tcfLvl == 0 && expLvl == 1) {
              // if in an ExpansionUnitTCF at first level, end the previous field comment
              // see ATestTCFProduction
              oneNewLineSwitchFieldCmt(n, "b!=0, 2&5 Com");
            } else {
              // if not in an ExpansionUnitTCF at first level, end the previous sub comment
              // see TypeDeclaration, EnumBody, ClassOrInterfaceType
              oneNewLine(n, "b!=0, 2&5 noCom");
            }
          } else if (wantedToBeOnNewLine) {
            // end the previous sub comment leaving it on its own line
            // see ExplicitConstructorInvocation
            oneNewLine(n, "wantedToBeOnNewLine");
          } else if (numEuOk > 0 && expUnit.f0.which == 4) {
            // new line if RegularExpression and not first ExpansionUnit
            if (tcfLvl == 0 && expLvl == 1) {
              // if in an ExpansionUnitTCF at first level, end the previous field comment
              // see ATestTCFProduction
              oneNewLineSwitchFieldCmt(n, "b!=0, 4 Com");
            } else {
              // if not in an ExpansionUnitTCF at first level, otherwise on same line
              addSpace = true;
            }
          } else {
            // otherwise on same line (and add a space but don't double space)
            if (numEuOk > 0)
              addSpace = true;
          }
        }

        // if more than one ExpansionUnit, output sequence number except at first Expansion level
        if (outSeq && expLvl > 0) {
          if (tcfLvl == 0 && expLvl == 1) {
            // if in ExpansionUnitTCF at first level
            if (numEuOk > 0) {
              // and if not first ExpansionUnit,
              // output generated field name (on the new field comment)
              final String fx = classInfo.getFieldNames().get(fni++);
              if (DEBUG_CHARS)
                curCtn.addFn(fx, " >> ");
              else
                curCtn.addFn(fx, " -> ");
              reinitPrefix(fx);
            }
          } else {
            // if not in ExpansionUnitTCF at first level
            // start a new sequence sub comment
            //  see everywhere, and specially ExplicitConstructorInvocation
            if (numEuOk > 0)
              curCtn.endSubComment();
            curCtn.newSubComment();
            curCtn.addTag(String.valueOf(SEQCH[(seqLvl % 3)]) +
                          (bigSeq && numEuOk < 10 ? "0" : "") + numEuOk + " ");
            if (addSpace)
              curCtn.addFn(String.valueOf(SEPCH));
            seqLvl++;
            if (DEBUG_CHARS)
              curCtn.addPfx(bigSeq ? "--- " : "-- ");
            else
              curCtn.addPfx(bigSeq ? "... " : ".. ");
          }
        }

        // update prefix with delimiter location, only for ExpansionChoices with no choice
        if (expUnit.f0.which == 5 || expUnit.f0.which == 2) {
          final NodeSequence seq = (NodeSequence) expUnit.f0.choice;
          final ExpansionChoices ec = (ExpansionChoices) seq.elementAt(1);
          if (!ec.f1.present())
            if (DEBUG_CHARS)
              curCtn.addPfx("= ");
            else
              curCtn.addPfx(". ");
        }

        // visit ExpansionUnit
        ++expLvl;
        expUnit.accept(this);
        --expLvl;
        seqLvl = oldSeqLvl;
        wantedToBeOnNewLine = expUnit.f0.which == 5 || expUnit.f0.which == 2;
        numEuOk++;

      } // end don't process LocalLookahead or Block nor not to be generated nodes
    } // end for loop on all ExpansionUnits

    // close last sub comment
    if (outSeq && expLvl > 0 && !(tcfLvl == 0 && expLvl == 1))
      curCtn.endSubComment();

  }

  /**
   * Visits a {@link ExpansionUnit} node, whose children are the following :
   * <p>
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
        curCtn.addSeq("[ ");
        // visit ExpansionChoices
        ++expLvl;
        seq.elementAt(1).accept(this);
        --expLvl;
        curCtn.addEnd(" ]");
        // see ExplicitConstructorInvocation
        break;

      case 3:
        // %3 ExpansionUnitTCF()
        // visit
        n.f0.choice.accept(this);
        break;

      case 4:
        // %4 #0 [ $0 PrimaryExpression() $1 "=" ]
        // .. #1 ( &0 $0 IdentifierAsString() $1 Arguments()
        // .. .. . $2 [ "!" ]
        // .. .. | &1 $0 RegularExpression()
        // .. .. . .. $1 [ ?0 "." ?1 < IDENTIFIER > ]
        // .. .. . .. $2 [ "!" ] )
        seq = (NodeSequence) n.f0.choice;
        ch = (NodeChoice) seq.elementAt(1);
        final NodeSequence seq1 = (NodeSequence) ch.choice;
        if (ch.which == 0) {
          // $0 IdentifierAsString() $1 Arguments() $2 [ "!" ]
          // add the comment only if node creation is not requested not to be generated
          if (!((NodeOptional) seq1.elementAt(2)).present()) {
            seq1.elementAt(0).accept(this);
            // don't visit for printing arguments, only parenthesis (for readability)
            curCtn.addId("()");
          }
        } else {
          // $0 RegularExpression() $1 [ ?0 "." ?1 < IDENTIFIER > ] $2 [ "!" ]
          // add the comment only if node creation is not requested not to be generated
          if (!((NodeOptional) seq1.elementAt(2)).present()) {
            seq1.elementAt(0).accept(this);
            final NodeOptional opt1 = (NodeOptional) seq1.elementAt(1);
            if (opt1.present()) {
              curCtn.addId('.');
              // visit < IDENTIFIER >
              ((NodeSequence) opt1.node).elementAt(1).accept(this);
            }
          }
        }
        break;

      case 5:
        // %5 #0 "(" #1 ExpansionChoices() #2 ")"
        // .. #3 ( &0 "+" | &1 "*" | &2 "?" )?
        seq = (NodeSequence) n.f0.choice;
        // #0 "("
        curCtn.addSeq("( ");
        // #1 ExpansionChoices()
        ++expLvl;
        seq.elementAt(1).accept(this);
        --expLvl;
        // #2 ")"
        curCtn.addEnd(" )");
        // #3 ( &0 "+" | &1 "*" | &2 "?" )?
        opt = (NodeOptional) seq.elementAt(3);
        if (opt.present()) {
          // ((NodeChoice) opt.node).choice.accept(this);
          // don't visit, take it directly to add it to the end member, not to the id member
          curCtn.addEnd(((NodeToken) ((NodeChoice) opt.node).choice).tokenImage);
        }
        //  see Modifiers, TypeDeclaration, EqualityExpression
        break;

      default:
        Messages.hardErr("n.f0.which = " + String.valueOf(n.f0.which));
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
    String fx;
    final String cs = String.valueOf(TCFCH[tcfLvl % 3]);
    int k = -1;
    boolean bigTcf = (4 + (n.f4.present() ? 5 * n.f4.size() : 0) + (n.f5.present() ? 2 : 0)) > 9;

    // f0 -> "try"
    if (tcfLvl == 0) {
      // field name already generated in ExpansionUnit
      curCtn.addId("\"try\"");
      // end the field comment
      oneNewLineSwitchFieldCmt(n, "tcfLvl==0");
    } else {
      curCtn.addSeq("( ");
      // start a new sub comment
      curCtn.newSubComment();
      curCtn.addTag(compTcfTag(cs, ++k, bigTcf));
      curCtn.addId("\"try\"");
      oneNewLine(n, "tcfLvl = " + tcfLvl);
    }

    // f1 -> "{"
    if (tcfLvl == 0) {
      fx = classInfo.getFieldNames().get(fni++);
      curCtn.addFn(fx, " -> ");
      curCtn.addId("\"{\"");
      // end the field comment
      oneNewLineSwitchFieldCmt(n, "expLvl = " + expLvl);
      // no real need for prefix
    } else {
      // end sub comment and start a new sub comment
      curCtn.endSubComment();
      curCtn.newSubComment();
      curCtn.addTag(compTcfTag(cs, ++k, bigTcf));
      curCtn.addId("\"{\"");
      oneNewLine(n, "tcfLvl = " + tcfLvl + ", expLvl = " + expLvl);
    }

    // f2 -> ExpansionChoices()
    if (tcfLvl == 0) {
      fx = classInfo.getFieldNames().get(fni++);
      if (DEBUG_CHARS)
        curCtn.addFn(fx, " ~> ");
      else
        curCtn.addFn(fx, " -> ");
      reinitPrefix(fx);
    } else {
      // end sub comment and start a new sub comment
      curCtn.endSubComment();
      curCtn.newSubComment();
      curCtn.addTag(compTcfTag(cs, ++k, bigTcf));
      if (DEBUG_CHARS)
        curCtn.addPfx(bigTcf ? "\"\"\" " : "\"\" ");
      else
        curCtn.addPfx(bigTcf ? "... " : ".. ");
    }
    // visit ExpansionChoices
    final boolean oldBigTcf = bigTcf;
    n.f2.accept(this);
    bigTcf = oldBigTcf;
    // end the field comment only at first level
    oneNewLineCreFieldCmtAtFirstTcfLvl(n);

    // f3 -> "}"
    if (tcfLvl == 0) {
      fx = classInfo.getFieldNames().get(fni++);
      curCtn.addFn(fx, " -> ");
      // no real need for prefix
    } else {
      // end sub comment and start a new sub comment
      curCtn.endSubComment();
      curCtn.newSubComment();
      curCtn.addTag(compTcfTag(cs, ++k, bigTcf));
    }
    curCtn.addId("\"}\"");

    // f4 -> ( #0 "catch" #1 "(" #2 Name() #3 < IDENTIFIER > #4 ")" #5 Block() )*
    if (n.f4.present()) {
      for (final Iterator<INode> e = n.f4.elements(); e.hasNext();) {
        e.next();
        // save a (previous) field comment only at first level
        oneNewLineCreFieldCmtAtFirstTcfLvl(n);

        // "catch"
        if (tcfLvl == 0) {
          fx = classInfo.getFieldNames().get(fni++);
          curCtn.addFn(fx, " -> ");
          // no real need for prefix
        } else {
          // end sub comment and start a new sub comment
          curCtn.endSubComment();
          curCtn.newSubComment();
          curCtn.addTag(compTcfTag(cs, ++k, bigTcf));
        }
        curCtn.addId("\"catch\"");

        // "("
        // save a (previous) field comment only at first level
        oneNewLineCreFieldCmtAtFirstTcfLvl(n);
        if (tcfLvl == 0) {
          fx = classInfo.getFieldNames().get(fni++);
          curCtn.addFn(fx, " -> ");
          // no real need for prefix
        } else {
          // end sub comment and start a new sub comment
          curCtn.endSubComment();
          curCtn.newSubComment();
          curCtn.addTag(compTcfTag(cs, ++k, bigTcf));
        }
        curCtn.addId("\"(\"");

        // Name()
        // save a (previous) field comment only at first level
        oneNewLineCreFieldCmtAtFirstTcfLvl(n);
        if (tcfLvl == 0) {
          fx = classInfo.getFieldNames().get(fni++);
          curCtn.addFn(fx, " -> ");
          // no real need for prefix
        } else {
          // end sub comment and start a new sub comment
          curCtn.endSubComment();
          curCtn.newSubComment();
          curCtn.addTag(compTcfTag(cs, ++k, bigTcf));
        }
        curCtn.addId("Name()");

        // < IDENTIFIER >
        // save a (previous) field comment only at first level
        oneNewLineCreFieldCmtAtFirstTcfLvl(n);
        if (tcfLvl == 0) {
          fx = classInfo.getFieldNames().get(fni++);
          curCtn.addFn(fx, " -> ");
          // no real need for prefix
        } else {
          // end sub comment and start a new sub comment
          curCtn.endSubComment();
          curCtn.newSubComment();
          curCtn.addTag(compTcfTag(cs, ++k, bigTcf));
        }
        curCtn.addId("< IDENTIFIER >");

        // ")"
        // save a (previous) field comment only at first level
        oneNewLineCreFieldCmtAtFirstTcfLvl(n);
        if (tcfLvl == 0) {
          fx = classInfo.getFieldNames().get(fni++);
          curCtn.addFn(fx, " -> ");
          // no real need for prefix
        } else {
          // end sub comment and start a new sub comment
          curCtn.endSubComment();
          curCtn.newSubComment();
          curCtn.addTag(compTcfTag(cs, ++k, bigTcf));
        }
        curCtn.addId("\")\"");

        // Block()
        // save a (previous) field comment only at first level
        oneNewLineCreFieldCmtAtFirstTcfLvl(n);
        if (tcfLvl == 0) {
          fx = classInfo.getFieldNames().get(fni++);
          curCtn.addFn(fx, " -> ");
          // no real need for prefix
        } else {
          // end sub comment and start a new sub comment
          curCtn.endSubComment();
          curCtn.newSubComment();
          curCtn.addTag(compTcfTag(cs, ++k, bigTcf));
        }
        curCtn.addId("Block()");
      }
    }

    // f5 -> [ #0 "finally" #1 Block() ]
    if (n.f5.present()) {
      // save a (previous) field comment only at first level
      oneNewLineCreFieldCmtAtFirstTcfLvl(n);

      // #0 "finally"
      if (tcfLvl == 0) {
        fx = classInfo.getFieldNames().get(fni++);
        curCtn.addFn(fx, " -> ");
        // no real need for prefix
      } else {
        // end sub comment and start a new sub comment
        curCtn.endSubComment();
        curCtn.newSubComment();
        curCtn.addTag(compTcfTag(cs, ++k, bigTcf));
      }
      curCtn.addId("finally");
      // save a (previous) field comment only at first level
      oneNewLineCreFieldCmtAtFirstTcfLvl(n);

      // #1 Block()
      if (tcfLvl == 0) {
        fx = classInfo.getFieldNames().get(fni++);
        curCtn.addFn(fx, " -> ");
        // no real need for prefix
      } else {
        // end sub comment and start a new sub comment
        curCtn.endSubComment();
        curCtn.newSubComment();
        curCtn.addTag(compTcfTag(cs, ++k, bigTcf));
      }
      curCtn.addId("Block()");
    }
    if (tcfLvl > 0) {
      curCtn.addEnd(" )");
      // end last sub comment
      curCtn.endSubComment();
    }
    tcfLvl--;
  }

  /**
   * Visits a {@link IdentifierAsString} node, whose children are the following :
   * <p>
   * f0 -> < IDENTIFIER ><br>
   * 
   * @param n - the node to visit
   */
  @Override
  public void visit(final IdentifierAsString n) {
    curCtn.addId(UnicodeConverter.addUnicodeEscapes(n.f0.tokenImage));
  }

  /**
   * Visits a {@link StringLiteral} node, whose children are the following :
   * <p>
   * f0 -> < STRING_LITERAL ><br>
   * 
   * @param n - the node to visit
   */
  @Override
  public void visit(final StringLiteral n) {
    curCtn.addId(UnicodeConverter.addUnicodeEscapes(n.f0.tokenImage));
  }

  /*
   * Miscellaneous
   */

  /**
   * Computes the tag for a TCF comment.
   * 
   * @param aChar - the TCF indicator character
   * @param aIx - the TCF tag index
   * @param aBigTag - true when more than 9 cases, false otherwise
   * @return the tag
   */
  String compTcfTag(final String aChar, final int aIx, final boolean aBigTag) {
    return aChar.concat(aIx < 10 && aBigTag ? "0" + String.valueOf(aIx) : String.valueOf(aIx))
                .concat(" ");
  }

  /**
   * Reinitializes the prefix with the appropriate number of leading dots.
   * 
   * @param fx - the current leading field name
   */
  void reinitPrefix(final String fx) {
    final int fxl = fx.length();
    prefix.setLength(0);
    if (DEBUG_CHARS) {
      for (int i = 0; i < fxl; i++)
        prefix.append(";");
      prefix.append(" ;; ");
    } else {
      for (int i = 0; i < fxl; i++)
        prefix.append(".");
      prefix.append(" .. ");
    }
    curCtn.addPfx(prefix);
  }

  /*
   * Called and post processing
   */

  /**
   * Generates the field comments and sub comments data. Called by {@link ClassInfoForJava}.
   * 
   * @param classInfo - the ClassInfoForJava to work on
   */
  public void genCommentsData(final ClassInfo classInfo) {
    // various initializations
    this.classInfo = classInfo;
    fni = expLvl = seqLvl = chLvl = 0;
    tcfLvl = -1;
    prefix.setLength(0);
    // allocate structures
    final int sz = classInfo.getFieldNames().size();
    roots = new ArrayList<CommentsTreeNode>(sz);
    for (int i = 0; i < sz; i++)
      roots.add(new CommentsTreeNode(null));
    fld = 0;
    if (roots.size() == 0)
      return;
    curCtn = roots.get(0);
    //    assert curCtn != null : "curCtn is null !";
    // produce comments data
    classInfo.getASTECNode().accept(this);
    // end comment processing
    storeClassInfoComments();
  }

  /**
   * Stores the comments tree into the current {@link ClassInfoForJava} corresponding fields.
   */
  void storeClassInfoComments() {
    //    assert fld == roots.size() : "fld (" + fld + ") is not equal to roots.size() (" + roots.size();

    //      System.out.println("classInfo : " + classInfo.className);
    //      final int k = 1;
    //      for (final CommentsTreeNode root : roots)
    //        printCtn(root, k);

//    // allocate lists
//    classInfo.fieldCmts = new ArrayList<CommentData>(fld);
//    if (inlineAcceptMethods)
//      classInfo.subCmts = new ArrayList<CommentData>(2 * fld);
    // loop on all fields
    for (final CommentsTreeNode root : roots) {
      // allocate structures for the field comment, not (yet) for the sub comments
      final CommentData fldCmtData = new CommentData();
      classInfo.getFieldCmts().add(fldCmtData);
      final int nbLines = countNbLines(root);
      fldCmtData.lines = new ArrayList<CommentLineData>(nbLines);
      processNode(root, fldCmtData, true, null, false);
    }

    //      System.out.println(" fc.sz : " + classInfo.fieldCmts.size() + ", sc.sz : " +
    //                         (classInfo.subCmts == null ? -1 : classInfo.subCmts.size()));
    //      for (final CommentData fcd : classInfo.fieldCmts) {
    //        System.out.print(" fc : " + fcd.lines.size());
    //        for (final CommentLineData fcld : fcd.lines)
    //          System.out.print(" <" + fcld.bare + ">");
    //        System.out.println();
    //      }
    //      if (inlineAcceptMethods) {
    //        for (final CommentData scd : classInfo.subCmts) {
    //          System.out.print(" sc=" + scd.lines.size());
    //          for (final CommentLineData scld : scd.lines)
    //            System.out.print(" <" + scld.bare + ">");
    //          System.out.println();
    //        }
    //      }
    //      System.out.println();

  }

  //  /**
  //   * Prints a comment tree node.
  //   * 
  //   * @param aCtn - the comments tree node to print
  //   * @param aIndent - the indentation level
  //   */
  //  void printCtn(final CommentsTreeNode aCtn, final int aIndent) {
  //    for (int i = 0; i < aIndent; i++)
  //      System.out.print(' ');
  //    if (aCtn.children != null)
  //      System.out.print("ch.sz : " + aCtn.children.size() + " ; ");
  //    System.out.println("ch : " + aCtn.fn + ", " + aCtn.pfx + ", " + aCtn.choice + ", " + aCtn.tag +
  //                       ", " + aCtn.seq + ", " + aCtn.id + ", " + aCtn.end + ", " + aCtn.hasNL);
  //    if (aCtn.children != null)
  //      for (final CommentsTreeNode child : aCtn.children)
  //        printCtn(child, aIndent + 1);
  //  }

  /**
   * Recursively processes a given comments tree node to store its data into the current
   * {@link ClassInfoForJava} field comments data and sub comments data members.
   * 
   * @param aCtn - a comments tree node
   * @param aFcd - the ClassInfoForJava field comment data
   * @param aWithSubComments - true if create also sub comments, false otherwise
   * @param aPrefix - the prefix of the calling level
   * @param aApplyPrefix - true if the passed prefix is to be applied, false otherwise
   */
  void processNode(final CommentsTreeNode aCtn, final CommentData aFcd,
                   final boolean aWithSubComments, final StringBuilder aPrefix,
                   final boolean aApplyPrefix) {
    /* process this field comment first */
    // copy first members
    if (aCtn.fn != null && (aCtn.fn.charAt(0) != SEPCH || fcb.length() != 0))
      fcb.append(aCtn.fn);
    else if (aApplyPrefix && aPrefix != null)
      fcb.append(aPrefix);
    if (aCtn.choice != null)
      fcb.append(aCtn.choice);
    if (aCtn.tag != null)
      fcb.append(aCtn.tag);
    if (aCtn.seq != null)
      fcb.append(aCtn.seq);

    // copy id member or walk down the tree to "fill the inside"
    //    final boolean opp = (aCtn.id == null && aCtn.children != null) ||
    //                        (aCtn.id != null && aCtn.children == null);
    //    assert opp : "id (" + (aCtn.id == null ? "null" : aCtn.id.toString()) + ") and children (" +
    //                 (aCtn.children == null ? "null" : "not null, size = " + aCtn.children.size()) +
    //                 ") not opposed !";
    if (aCtn.children == null)
      fcb.append(aCtn.id);
    else {
      StringBuilder pfx = null;
      final int len = (aPrefix == null ? 0 : aPrefix.length()) +
                      (aCtn.pfx == null ? 0 : aCtn.pfx.length());
      if (len > 0) {
        pfx = new StringBuilder(len);
        if (aPrefix != null)
          pfx.append(aPrefix);
        if (aCtn.pfx != null)
          pfx.append(aCtn.pfx);
      }
      boolean apply = false;
      for (final CommentsTreeNode child : aCtn.children) {
        // just fill the "inside" of the field comment
        processNode(child, aFcd, false, pfx, apply);
        apply = true;
      }
    }

    // copy end member
    if (aCtn.end != null)
      fcb.append(aCtn.end);

    // change line if needed and copy debug member
    //    assert aCtn.debug == null || aCtn.hasNL : "no new line but debug (" + aCtn.debug +
    //                                              ") not null !";
    if (aCtn.hasNL) {
      final CommentLineData fcld = new CommentLineData();
      aFcd.lines.add(fcld);
      fcld.bare = fcb.toString();
      if (aCtn.debug != null)
        fcld.debug = aCtn.debug.toString();
      fcb.setLength(0);
    }

    /* now create direct sub comments of this field comment */
    if (inlineAcceptMethods && aWithSubComments && aCtn.children != null) {
      for (final CommentsTreeNode child : aCtn.children)
        processSubComments(child, true, null, false);
    }
  }

  /**
   * Recursively processes a given comments tree node to store its data into the current
   * {@link ClassInfoForJava} sub comments data member.
   * 
   * @param aCtn - a comments tree node
   * @param aNewSubComment - true to create a new sub comment, false otherwise
   * @param aPrefix - the prefix of the calling level
   * @param aApplyPrefix - true if the passed prefix is to be applied, false otherwise
   */
  void processSubComments(final CommentsTreeNode aCtn, final boolean aNewSubComment,
                          final StringBuilder aPrefix, final boolean aApplyPrefix) {

    /* process this sub comment first */
    // create a new sub comment at first call level
    if (aNewSubComment) {
      final CommentData subCmtData = new CommentData();
      classInfo.getSubCmts().add(subCmtData);
      subCmtData.lines = new ArrayList<CommentLineData>();
      scb.setLength(0);
    }

    // copy first members
    if (aCtn.fn != null && (aCtn.fn.charAt(0) != SEPCH || scb.length() != 0))
      scb.append(aCtn.fn);
    else if (aApplyPrefix && aPrefix != null)
      scb.append(aPrefix);
    // don't output choice string if at the beginning of the line
    if (aCtn.choice != null && scb.length() != 0)
      scb.append(aCtn.choice);
    if (aCtn.tag != null)
      scb.append(aCtn.tag);
    if (aCtn.seq != null)
      scb.append(aCtn.seq);

    // copy id member or walk down the sub comments
    if (aCtn.children == null)
      scb.append(aCtn.id);
    else {
      StringBuilder pfx = null;
      final int len = (aPrefix == null ? 0 : aPrefix.length()) +
                      (aCtn.pfx == null ? 0 : aCtn.pfx.length());
      if (len > 0) {
        pfx = new StringBuilder(len);
        if (aPrefix != null)
          pfx.append(aPrefix);
        if (aCtn.pfx != null) {
          // don't output the choice string if at the beginning of the line
          // a trick is used here (only choice prefix has only one character before the first space)
          if (aCtn.pfx.charAt(1) == ' ')
            pfx.append(aCtn.pfx.subSequence(2, aCtn.pfx.length()));
          else
            pfx.append(aCtn.pfx);
        }
      }
      boolean apply = false;
      for (final CommentsTreeNode child : aCtn.children) {
        // just fill the "inside" of the sub comment
        processSubComments(child, false, pfx, apply);
        apply = true;
      }
    }

    // copy end member
    if (aCtn.end != null)
      scb.append(aCtn.end);

    // create sub comment line if needed
    if (aNewSubComment || aCtn.hasNL) {
      final CommentLineData scld = new CommentLineData();
      classInfo.getSubCmts().get(classInfo.getSubCmts().size() - 1).lines.add(scld);
      scld.bare = scb.toString();
      scb.setLength(0);
    }

    /* now create direct sub comments of this sub comment */
    if (aNewSubComment && aCtn.children != null) {
      for (final CommentsTreeNode child : aCtn.children)
        processSubComments(child, true, null, false);
    }
  }

  /**
   * Counts the total number of lines in a node and its sub tree.
   * 
   * @param aCtn - a node of the comments tree
   * @return the total number of lines in a node and its sub tree
   */
  int countNbLines(final CommentsTreeNode aCtn) {
    int nb = 0;
    if (aCtn.getNL())
      nb++;
    if (aCtn.children != null) {
      for (final CommentsTreeNode child : aCtn.children) {
        nb += countNbLines(child);
      }
    }
    return nb;
  }

  /**
   * A node of the tree of the comments.<br>
   * Members should hold all final separating spaces.
   */
  class CommentsTreeNode {

    /** The parent, null if root */
    CommentsTreeNode            parent   = null;
    /** The children, null if none */
    ArrayList<CommentsTreeNode> children = null;
    /** The field name */
    String                      fn       = null;
    /** The prefix (for the following nodes at the same level) */
    String                      pfx      = null;
    /** The choice string */
    String                      choice   = null;
    /**
     * The tag ({@link CommentsPrinter#WHCH},{@link CommentsPrinter#SEQCH},
     * {@link CommentsPrinter#TCFCH} and an index 0, 1, ...)
     */
    String                      tag      = null;
    /** The sequence string */
    String                      seq      = null;
    /** The body : identifier or production */
    StringBuilder               id       = null;
    /** The end string */
    String                      end      = null;
    /** The debug string */
    String                      debug    = null;
    /** The flag telling there is a new line or not */
    boolean                     hasNL    = false;

    /**
     * Constructs a new node.
     */
    CommentsTreeNode() {
    }

    /**
     * Constructs a new node, child of a given parent.
     * 
     * @param aParent - the node's parent
     */
    CommentsTreeNode(final CommentsTreeNode aParent) {
      parent = aParent;
    }

    /**
     * Creates, links new node for a new sub comment and refers it to be the current.
     */
    void newSubComment() {
      final CommentsTreeNode child = new CommentsTreeNode(this);
      if (children == null)
        children = new ArrayList<CommentsTreeNode>(4);
      children.add(child);
      curCtn = child;
    }

    /**
     * Get the parent comment as the current.
     */
    void endSubComment() {
      //      assert curCtn.parent != null : "trying to end a sub comment when on a field comment";
      curCtn = curCtn.parent;
    }

    /**
     * Adds a String to the field name.
     * 
     * @param aStr - what to be added
     */
    void addFn(final String aStr) {
      if (fn == null)
        fn = aStr;
      else
        fn += aStr;
    }

    /**
     * Adds two String to the field name.
     * 
     * @param aStr1 - what to be added
     * @param aStr2 - what to be added
     */
    void addFn(final String aStr1, final String aStr2) {
      if (fn == null)
        fn = aStr1 + aStr2;
      else {
        fn += aStr1;
        fn += aStr2;
      }
    }

    /**
     * Adds a {@link CharSequence} to the prefix member.
     * 
     * @param aChSeq - what to be added
     */
    void addPfx(final CharSequence aChSeq) {
      if (pfx == null)
        pfx = aChSeq.toString();
      else
        pfx += aChSeq.toString();
    }

    /**
     * Adds a String to the choice string member.
     * 
     * @param aStr - what to be added
     */
    void addChoice(final String aStr) {
      if (choice == null)
        choice = aStr;
      else
        choice += aStr;
    }

    /**
     * Adds a String to the sequence string member.
     * 
     * @param aStr - what to be added
     */
    void addSeq(final String aStr) {
      if (seq == null)
        seq = aStr;
      else
        seq += aStr;
    }

    /**
     * Adds a String to the tag member.
     * 
     * @param aStr - what to be added
     */
    void addTag(final String aStr) {
      if (tag == null)
        tag = aStr;
      else
        tag += aStr;
    }

    /**
     * Adds a char to the id member.
     * 
     * @param aCh - what to be added
     */
    void addId(final char aCh) {
      if (id == null)
        id = new StringBuilder(16);
      id.append(aCh);
    }

    /**
     * Adds a {@link CharSequence} to the id member.
     * 
     * @param aChSeq - what to be added
     */
    void addId(final CharSequence aChSeq) {
      if (id == null)
        id = new StringBuilder(aChSeq.length() + 16);
      id.append(aChSeq);
    }

    /**
     * Adds a String to the end string member.
     * 
     * @param aStr - what to be added
     */
    void addEnd(final String aStr) {
      if (end == null)
        end = aStr;
      else
        end += aStr;
    }

    /**
     * Adds a String to the debug string member.
     * 
     * @param aStr - what to be added
     */
    void addDebug(final String aStr) {
      if (debug == null)
        debug = aStr;
      else
        debug += aStr;
    }

    /**
     * Setter for the new line member.
     */
    void setNL() {
      hasNL = true;
    }

    /**
     * Getter for the new line member.
     * 
     * @return the new line member
     */
    boolean getNL() {
      return hasNL;
    }

  } // end of class CommentsTreeNode

} // end of class CommentsPrinter

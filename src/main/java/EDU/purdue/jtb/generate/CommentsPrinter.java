package EDU.purdue.jtb.generate;

import static EDU.purdue.jtb.common.Constants.DEBUG_CLASS;
import static EDU.purdue.jtb.common.Constants.DEBUG_COMMENT;
import static EDU.purdue.jtb.parser.syntaxtree.JTBParserNodeConstants.JTB_SIG_EXPANSION;
import static EDU.purdue.jtb.parser.syntaxtree.JTBParserNodeConstants.JTB_SIG_EXPANSIONCHOICES;
import static EDU.purdue.jtb.parser.syntaxtree.JTBParserNodeConstants.JTB_SIG_EXPANSIONUNIT;
import static EDU.purdue.jtb.parser.syntaxtree.JTBParserNodeConstants.JTB_SIG_EXPANSIONUNITTCF;
import static EDU.purdue.jtb.parser.syntaxtree.JTBParserNodeConstants.JTB_SIG_IDENTIFIERASSTRING;
import static EDU.purdue.jtb.parser.syntaxtree.JTBParserNodeConstants.JTB_SIG_STRINGLITERAL;
import java.util.ArrayList;
import java.util.List;
import EDU.purdue.jtb.analyse.GlobalDataBuilder;
import EDU.purdue.jtb.common.Messages;
import EDU.purdue.jtb.common.ProgrammaticError;
import EDU.purdue.jtb.common.UnicodeConverter;
import EDU.purdue.jtb.common.UserClassInfo;
import EDU.purdue.jtb.common.UserClassInfo.CommentData;
import EDU.purdue.jtb.common.UserClassInfo.CommentLineData;
import EDU.purdue.jtb.common.UserClassInfo.FieldInfo;
import EDU.purdue.jtb.parser.Token;
import EDU.purdue.jtb.parser.syntaxtree.Expansion;
import EDU.purdue.jtb.parser.syntaxtree.ExpansionChoices;
import EDU.purdue.jtb.parser.syntaxtree.ExpansionUnit;
import EDU.purdue.jtb.parser.syntaxtree.ExpansionUnitTCF;
import EDU.purdue.jtb.parser.syntaxtree.INode;
import EDU.purdue.jtb.parser.syntaxtree.IdentifierAsString;
import EDU.purdue.jtb.parser.syntaxtree.NodeChoice;
import EDU.purdue.jtb.parser.syntaxtree.NodeOptional;
import EDU.purdue.jtb.parser.syntaxtree.NodeSequence;
import EDU.purdue.jtb.parser.syntaxtree.NodeToken;
import EDU.purdue.jtb.parser.syntaxtree.StringLiteral;
import EDU.purdue.jtb.parser.visitor.signature.NodeFieldsSignature;

/**
 * The {@link CommentsPrinter} visitor (an extension of {@link JavaCCPrinter visitor}) finds which part of the
 * production each field corresponds, and to format the class, method or field corresponding javadoc comments.
 * CODEJAVA
 * <p>
 * Visitor maintains state (for a user class), and is not supposed to be run in parallel threads (on the same
 * user class).
 * </p>
 * <p>
 * Each field comment is terminated by a break tag and a newline, and may be splitted, for better readability,
 * in different lines, with proper indentation (with spaces ' ' and dots '.'), on a new {@link Expansion} in
 * an {@link ExpansionChoices} (after a '|') or on a new {@link ExpansionUnit} of type 5 ('('...')',
 * '('...')''?', '('...')''+', '('...')''*') or of type 2 ('['...']').
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
 * 2 - Choices at first BNF level (not included in an ExpansionUnit "(...)": <code>%0...%N</code> show the
 * which indicator for each Expansion (note the dummy dot on first line):<br>
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
 * 4 - Choices at two levels: <code>%0...%n</code> show the which indicator for level 1, <code>%0...%N</code>
 * show the which indicator for level 1, <code>&0...&N</code> show the which indicator for level 2,
 * <code>~1...~N</code> show the which indicator for level 3, and back again for the next levels:<br>
 * <code>
 * f0 -> . %0 #0 ( &0 "+"<br>
 * .. .. . .. .. | &1 "-" ) #1 UnaryExpression()<br>
 * .. .. | %1 PreIncrementExpression()<br>
 * .. .. | %2 PreDecrementExpression()<br>
 * .. .. | %3 UnaryExpressionNotPlusMinus()<br>
 * </code>
 * <p>
 * 5 - Sequence of ExpansionUnits (in an Expansion in an ExpansionChoice in a ExpansionUnit):
 * <code>#0...#N</code> show the sequence number for each ExpansionUnit (notice that here in f1 there is no
 * Sequence, so no number is shown, unlike in f2 where there are 2 nodes):<br>
 * <code>
 * f0 -> RegularExpression()<br>
 * f1 -> [ Block() ]<br>
 * f2 -> [ #0 ":" #1 < IDENTIFIER > ]<br>
 * </code>
 * <p>
 * 6 - Sequence of ExpansionUnits (in an Expansion in an ExpansionChoice in a ExpansionUnit) at two or more
 * levels: <code>#0...#N</code> show the sequence number for level 1, <code>$0...$N</code> show the sequence
 * number for level 2, <code>?0...?N</code> show the sequence number for next level, and back again for the
 * next levels:<br>
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
 * 7 - Example showing almost all previous features (% and & for choices, #, $ and ? for sequences): <br>
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
 * 8 - In case of ExpansionUnit with Try / Catch / Finally elements, each element does not appear, only inner
 * ExpansionChoices / Expansion / ExpansionUnit / IdentifierAsString / RegularExpression / ... appear:<br>
 * example: for a BnfProduction:<br>
 * <code>
 * . try {<br>
 * . . { ... }<br>
 * . . Identifier()<br>
 * . . (<br>
 * . . . Identifier()<br>
 * . . |<br>
 * . . . try {<br>
 * . . . . { ... }<br>
 * . . . . Integer_litteral()<br>
 * . . . }<br>
 * . . . catch (NullPointerException npe) { ... }<br>
 * . . ) *<br>
 * . }<br>
 * . catch (Exception e) { ... }<br>
 * . finally { ... }<br>
 * </code><br>
 * we get:<br>
 * <code>
 * f0 -> Identifier()<br>
 * f1 -> ( %0 Identifier()<br>
 * .. .. | %1 !0 Integer_litteral() )*<br>
 * </code>
 * <p>
 * 9 - TODO add this javadoc with an example for "foo" | {}<br>
 * <p>
 * TESTCASE many to add
 *
 * @author Marc Mazas
 * @version 1.4.0 : 05-08/2009 : MMa : adapted to JavaCC v4.2 grammar and JDK 1.5
 * @version 1.4.3.1 : 04/2010 : MMa : fixed case 4 of {@link #visit(ExpansionUnit)} (bug n�2990962)
 * @version 1.4.7 : 07/2012 : MMa : followed changes in jtbgram.jtb (IndentifierAsString())<br>
 *          1.4.7 : 08-09/2012 : MMa : fixed problems in {@link #visit(ExpansionUnitTCF)}, fixed new lines in
 *          {@link #visit(Expansion)}, generated sub comments ; added non node creation and the reference to
 *          the GlobalDataBuilder
 * @version 1.4.8 : 10/2012 : MMa : updated for JavaCodeProduction class generation (no field so no field
 *          comments)<br>
 *          1.4.8 : 11/2014 : MMa : fixed IOOBE & NPE issues around TCF handling<br>
 *          1.4.8 : 11/2014 : MMa : updated for NodeDescriptor in {@link #visit(Expansion)} 1.4.8 : 12/2014 :
 *          MMa : improved some debug printing ; added some finals
 * @version 1.4.11 : 03/2016 : MMa : fixed spaces between brackets and identifiers in generated comments
 * @version 1.5.0 : 01-06/2017 : MMa : added fields hash signature ; was "recreated" as Eclipse showed
 *          problems regarding a non ASCII character in the first element of {@link #TCFCH} ; changed some
 *          iterator based for loops to enhanced for loops ; applied changes following new class FieldInfo ;
 *          fixed processing of not to be created nodes ; added final in ExpansionUnitTCF's catch ; removed
 *          NodeTCF related code<br>
 *          10-11/2022 : MMa : removed NodeTCF related code ; fixed many issues while improving test
 *          grammars<br>
 * @version 1.5.1 : 08/2023 : MMa : editing changes for coverage analysis; changes due to the NodeToken
 *          replacement by Token
 * @version 1.5.3 : 11/2025 : MMa : signature code made independent of parser
 */
class CommentsPrinter extends JavaCCPrinter {
  
  /**
   * The (comment) expansion level we are in : 0, 1, ... : first, second, ... ; incremented at each level,
   * except in an ExpansionChoice with no choice and in ExpansionUnitTCF
   */
  int                         expLvl;
  /** The (comment) expansionUnitTCF level we are in : -1 : none; 0, 1, ... : first, second, ... */
  int                         tcfLvl = -1;
  /** The (comment) sequence level (0 -> none, 1 -> first, 2 -> second, ...) */
  private int                 seqLvl = 0;
  /** The (comment) choice level (0 -> none, 1 -> first, 2 -> second, ...) */
  private int                 chLvl  = 0;
  /** The comment prefix */
  private final StringBuilder prefix = new StringBuilder(32);
  /** The which indicator characters ('%', '&', '~') */
  private static final char[] WHCH   = {
      '%', '&', '~'
  };
  /** The sequence indicator characters ('#', '$', '?') */
  private static final char[] SEQCH  = {
      '#', '$', '?'
  };
  /** The (inner) ExpansionUnitTCF indicator characters (':', '!', '^') */
  private static final char[] TCFCH  = {
      ':', '!', '^'
  };
  /** The separator character between sequence elements (' ') */
  private static final char   SEPCH  = ' ';
  // /** The prefix for TCF elements */
  // private static final String PFXSTR = ". ";
  /** The field name index */
  private int                    fni;
  /**
   * The roots of the comments (and sub comments if inlining accept methods) trees (one for each field)
   */
  private List<CommentsTreeNode> roots;
  /** * The current (field or sub) comment tree node we are working on */
  CommentsTreeNode               curCtn;
  /** The current field index */
  private int                    fld;
  /** An auxiliary buffer for the field comments */
  private final StringBuilder    fcb;
  /** An auxiliary buffer for the sub comments */
  private final StringBuilder    scb;
  /** An auxiliary buffer for the hash signature */
  private final StringBuilder    hb;
  /** The {@link UserClassInfo} of the class the visitor is working on */
  UserClassInfo                  classInfo;
  /**
   * A debug switch, to change prefix and field names strings:<br>
   * note: will need to change build.xml/try_rebuild_jtbparser
   */
  private static final boolean   DEBUG_CHARS = false;
  /** The node class debug comment prefix */
  {
    JJNCDCP = " //cp ";
  }
  
  /**
   * Constructor which just allocates the internal buffer.
   *
   * @param aGdbv - the {@link GlobalDataBuilder} visitor
   * @param aCcg - the {@link CommonCodeGenerator}
   */
  CommentsPrinter(final GlobalDataBuilder aGdbv, final CommonCodeGenerator aCcg) {
    super(aGdbv, aCcg);
    sb = new StringBuilder(512);
    fcb = new StringBuilder(128);
    hb = new StringBuilder(128);
    scb = aGdbv.jopt.inlineAcceptMethods ? new StringBuilder(128) : null;
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
    if (DEBUG_CLASS) {
      curCtn.addDebug(nodeClassComment(n));
    }
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
    if (DEBUG_CLASS) {
      curCtn.addDebug(nodeClassComment(n, str));
    }
    curCtn.setNL();
  }
  
  /**
   * Ends all current sub comments and switches to the next field comment.
   *
   * @param n - the node for the node class comment
   * @param str - the extra comment
   */
  private void oneNewLineSwitchFieldCmt(final INode n, final String str) {
    oneNewLine(n, str);
    // end field comment
    while (curCtn != null) {
      curCtn = curCtn.parent;
    }
    // switch to next field
    fld++;
    if (fld < roots.size()) {
      curCtn = roots.get(fld);
    } else {
      // normally no further comment processing should be done if algorithm is OK :) !
      curCtn = null;
    }
    if (DEBUG_COMMENT) {
      System.out.flush();
      final String msg = "onlsfc : " + CommentsPrinter.this.classInfo.fixedClassName + " : this : "
          + toString().substring(23) + ", curCtn : "
          + (curCtn == null ? null : curCtn.toString().substring(23)) + ", cp.expLvl : "
          + CommentsPrinter.this.expLvl + ", cp.tcfLvl : " + CommentsPrinter.this.tcfLvl + "; " + str;
      System.err.println(msg);
      System.err.flush();
      if (curCtn != null) {
        curCtn.addDebug("<br" + LS + "   * ");
        curCtn.addDebug(msg);
      }
    }
  }
  
  /**
   * Prints a Token image taking care of '<' and '>' characters (change &lt;ABC&gt; into < ABC > for proper
   * display of comments in Eclipse javadoc views).
   *
   * @param n - the Token
   */
  @Override
  public void visit(final NodeToken n) {
    final int len = ((Token) n).image.length();
    for (int i = 0; i < len; ++i) {
      final char c = ((Token) n).image.charAt(i);
      if (c == '<') {
        // image is single character, add space !
        curCtn.addId("< ");
      } else if (c == '>') {
        // image is single character, add space !
        curCtn.addId(" >");
      } else {
        // there should be no '<' nor '>'
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
   * s: -1726831935<br>
   *
   * @param n - the node to visit
   */
  @Override
  @NodeFieldsSignature(old_sig = -1726831935, new_sig = JTB_SIG_EXPANSIONCHOICES, name = "ExpansionChoices")
  public void visit(final ExpansionChoices n) {
    
    // if only f0
    if (!n.f1.present()) {
      // f0 -> Expansion()
      // if > 0 let do it under, if = 0 nothing
      if (gdbv.getNbSubNodesTbc(n) > 0) {
        n.f0.accept(this);
        if (expLvl == 0) {
          // if at first bnf level, end the field comment
          // see ExtendsList, MethodDeclaration
          oneNewLineSwitchFieldCmt(n, "only f0");
        }
      }
      return;
    }
    
    // else f0 and f1
    if (gdbv.getNbSubNodesTbc(n) > 0) {
      final boolean bigWh = n.f1.size() > 9;
      
      // f0 -> Expansion()
      if (expLvl == 0) {
        // if at first ExpansionChoices level
        // output generated field name (on the new field comment)
        if (DEBUG_COMMENT) {
          System.out.flush();
          System.err.println("*** EC fni = " + fni + " ***");
          System.err.flush();
        }
        final String fx = classInfo.fields.get(fni++).name;
        // see ReferenceType
        if (DEBUG_CHARS) {
          curCtn.addFn(fx == null ? "{ec}" : fx, " ?> ? ");
        } else {
          curCtn.addFn(fx == null ? "{}" : fx, " -> . ");
        }
        reinitPrefix(fx);
      }
      // start a new choice sub comment (that will end on ExpansionChoices element)
      // see UnaryExpression, ReferenceType
      curCtn.newSubComment();
      // output which indicator
      curCtn.addTag(String.valueOf(WHCH[chLvl % 3]) + (bigWh ? "00 " : "0 "));
      if (DEBUG_CHARS) {
        curCtn.addPfx(bigWh ? "` ``` " : "` `` ");
      } else {
        curCtn.addPfx(bigWh ? ". ... " : ". .. ");
      }
      
      // visit Expansion()
      final int oldChLvl = chLvl++;
      ++expLvl;
      n.f0.accept(this);
      --expLvl;
      chLvl = oldChLvl;
      
      // f1 -> ( #0 "|" #1 Expansion() )*
      int fi = 1;
      for (final INode e : n.f1.nodes) {
        final NodeSequence seq = (NodeSequence) e;
        
        // end the previous choice sub comment
        // see TypeDeclaration, MethodDeclaration, ATestTCFProduction
        oneNewLine(n, "element");
        // start a new choice sub comment (that will end on ExpansionChoices element and may be on multiple
        // lines)
        curCtn.endSubComment();
        curCtn.newSubComment();
        
        // "|"
        curCtn.addChoice("| ");
        // output which indicator
        // see UnaryExpression
        curCtn.addTag(String.valueOf(WHCH[chLvl % 3]) + (bigWh && (fi < 10) ? "0" : "") + fi + " ");
        chLvl++;
        if (DEBUG_CHARS) {
          curCtn.addPfx(bigWh ? "' ''' " : "' '' ");
        } else {
          curCtn.addPfx(bigWh ? ". ... " : ". .. ");
        }
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
    
    // count the number of non LocalLookahead nor Block nor not to be created nodes
    int nbEuOk = 0;
    for (final INode e : n.f1.nodes) {
      final ExpansionUnit expUnit = (ExpansionUnit) e;
      if (gdbv.getNbSubNodesTbc(expUnit) != 0) {
        nbEuOk++;
      }
    }
    final boolean outSeq = nbEuOk > 1;
    final boolean bigSeq = nbEuOk > 9;
    int numEuOk = 0;
    
    // save state info early
    final int oldSeqLvl = seqLvl;
    
    // f1 -> ( ExpansionUnit() )+
    boolean wantedToBeOnNewLine = false;
    int nbOnALine = 0;
    final int sz = n.f1.size();
    for (int i = 0; i < sz; i++) {
      final ExpansionUnit expUnit = (ExpansionUnit) n.f1.elementAt(i);
      // don't process LocalLookahead nor Block nor not to be created nodes
      if (gdbv.getNbSubNodesTbc(expUnit) != 0) {
        
        boolean addSpace = false;
        if (expLvl == 0) {
          // at first Expansion level
          // end the previous field comment if already something (that is when it is not first one),
          if (numEuOk > 0) {
            // see ImportDeclaration, ClassOrInterfaceDeclaration, EnumBody
            oneNewLineSwitchFieldCmt(n, "expLvl==0");
          }
          // output generated field name (on the new field comment)
          if (DEBUG_COMMENT) {
            System.out.flush();
            System.err.println("*** E 1 fni = " + fni + ", tcfLvl = " + tcfLvl + " ***");
            System.err.flush();
          }
          final String fx = classInfo.fields.get(fni++).name;
          if (DEBUG_CHARS) {
            curCtn.addFn(fx == null ? "{e0}" : fx, " :> ");
          } else {
            curCtn.addFn(fx == null ? "{}" : fx, " -> ");
          }
          reinitPrefix(fx);
        } else // at non first Expansion levels
        if ((numEuOk > 0)
            && ((expUnit.f0.which == 2) || (expUnit.f0.which == 5) || (expUnit.f0.which == 3))) {
          // new line if (...), (...)?, (...)+, (...)* or [...] and not first ExpansionUnit
          if ((tcfLvl >= 0) && (expLvl == 1)) {
            // if in an ExpansionUnitTCF at first level, end the previous field comment
            // see ATestTCFProduction
            oneNewLineSwitchFieldCmt(n, "expLvl!=0, 2&5 Com");
          } else {
            // if not in an ExpansionUnitTCF at first level, end the previous sub comment
            // see TypeDeclaration, EnumBody, ClassOrInterfaceType
            oneNewLine(n, "b!=0, 2&5 noCom");
          }
        } else if (wantedToBeOnNewLine) {
          // end the previous sub comment leaving it on its own line
          // see ExplicitConstructorInvocation
          oneNewLine(n, "wantedToBeOnNewLine");
        } else if ((numEuOk > 0) && (expUnit.f0.which == 4)) {
          // new line if RegularExpression and not first ExpansionUnit
          if ((tcfLvl >= 0) && (expLvl == 1)) {
            // if in an ExpansionUnitTCF at first level, end the previous field comment
            // see ATestTCFProduction
            oneNewLineSwitchFieldCmt(n, "expLvl!=0, 4 Com");
          } else {
            // if not in an ExpansionUnitTCF at first level, otherwise on same line
            addSpace = true;
          }
        } else if (numEuOk > 0) {
          // otherwise on same line (and add a space but don't double space)
          addSpace = true;
        }
        
        // if more than one ExpansionUnit, output sequence number except at first Expansion level
        if (outSeq && (expLvl > 0)) {
          if ((tcfLvl >= 0) && (expLvl == 1)) {
            // if in ExpansionUnitTCF at first level
            if (numEuOk > 0) {
              // and if not first ExpansionUnit,
              // output generated field name (on the new field comment)
              if (DEBUG_COMMENT) {
                System.out.flush();
                System.err.println("*** E 2 fni = " + fni + ", tcfLvl = " + tcfLvl + " ***");
                System.err.flush();
              }
              final String fx = classInfo.fields.get(fni++).name;
              if (DEBUG_CHARS) {
                curCtn.addFn(fx == null ? "{e>0}" : fx, " >> ");
              } else {
                curCtn.addFn(fx == null ? "{}" : fx, " -> ");
              }
              reinitPrefix(fx);
            }
          } else {
            // if not in ExpansionUnitTCF at first level
            // start a new sequence sub comment
            // see everywhere, and specially ExplicitConstructorInvocation
            if (numEuOk > 0) {
              curCtn.endSubComment();
            }
            curCtn.newSubComment();
            curCtn.addTag(
                String.valueOf(SEQCH[seqLvl % 3]) + (bigSeq && (numEuOk < 10) ? "0" : "") + numEuOk + " ");
            if (addSpace) {
              curCtn.addFn(String.valueOf(SEPCH));
            }
            seqLvl++;
            if (DEBUG_CHARS) {
              curCtn.addPfx(bigSeq ? "--- " : "-- ");
            } else {
              curCtn.addPfx(bigSeq ? "... " : ".. ");
            }
          }
        }
        
        // update prefix with delimiter location, only for ExpansionChoices with no choice
        if ((expUnit.f0.which == 5) || (expUnit.f0.which == 2)) {
          final NodeSequence seq = (NodeSequence) expUnit.f0.choice;
          final ExpansionChoices ec = (ExpansionChoices) seq.elementAt(1);
          if (!ec.f1.present()) {
            if (DEBUG_CHARS) {
              curCtn.addPfx("= ");
            } else {
              curCtn.addPfx(". ");
            }
          }
          // } else if (expUnit.f0.which == 3) {
          // ExpansionUnitTCF
          // curCtn.addPfx(PFXSTR);
        }
        
        // visit ExpansionUnit
        ++expLvl;
        expUnit.accept(this);
        --expLvl;
        seqLvl = oldSeqLvl;
        wantedToBeOnNewLine = (expUnit.f0.which == 5) || (expUnit.f0.which == 2) || (expUnit.f0.which == 3);
        numEuOk++;
        
      } // end don't process LocalLookahead or Block nor not to be generated nodes
      if (nbOnALine > 4) {
        wantedToBeOnNewLine = true;
      }
      if (wantedToBeOnNewLine) {
        nbOnALine = 0;
      } else {
        nbOnALine++;
      }
    } // end for loop on all ExpansionUnits
    
    // close last sub comment
    if (outSeq && (expLvl > 0) && !((tcfLvl >= 0) && (expLvl == 1))) {
      curCtn.endSubComment();
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
    NodeSequence seq;
    final NodeOptional opt;
    
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
      final NodeChoice ch = (NodeChoice) seq.elementAt(1);
      final NodeSequence seq1 = (NodeSequence) ch.choice;
      if (ch.which == 0) {
        // $0 IdentifierAsString() $1 Arguments() $2 [ "!" ]
        // add the comment only if node creation is not requested not to be generated
        if (!((NodeOptional) seq1.elementAt(2)).present()) {
          seq1.elementAt(0).accept(this);
          // don't visit for printing arguments, only parenthesis (for readability)
          curCtn.addId("()");
        }
      } else // $0 RegularExpression() $1 [ ?0 "." ?1 < IDENTIFIER > ] $2 [ "!" ]
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
      break;
    
    case 5:
      // %5 #0 "(" #1 ExpansionChoices() #2 ")"
      // .. #3 ( &0 "+" | &1 "*" | &2 "?" )?
      seq = (NodeSequence) n.f0.choice;
      // #1 ExpansionChoices()
      final int res = gdbv.getNbSubNodesTbc((ExpansionChoices) seq.elementAt(1));
      // #3 ( &0 "+" | &1 "*" | &2 "?" )?
      opt = (NodeOptional) seq.elementAt(3);
      if (!opt.present()) {
        if (res > 0) {
          // #0 "("
          curCtn.addSeq("( ");
          // #1 ExpansionChoices()
          ++expLvl;
          seq.elementAt(1).accept(this);
          --expLvl;
          // #2 ")"
          curCtn.addEnd(" )");
        }
      } else if (res > 0) {
        // #0 "("
        curCtn.addSeq("( ");
        // #1 ExpansionChoices()
        ++expLvl;
        seq.elementAt(1).accept(this);
        --expLvl;
        // #2 ")"
        curCtn.addEnd(" )");
        // #3 ( &0 "+" | &1 "*" | &2 "?" )?
        // don't visit #3, take it directly to add it to the end member, not to the id member
        final String image = ((Token) ((NodeChoice) opt.node).choice).image;
        if (image == null) {
          curCtn.addEnd("{im})");
        } else {
          curCtn.addEnd(image);
        }
      }
      
      // see Modifiers, TypeDeclaration, EqualityExpression
      break;
    
    default:
      final String msg = "Invalid n.f0.which = " + n.f0.which;
      Messages.hardErr(msg);
      throw new ProgrammaticError(msg);
    
    }
    
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
    // if > 0 all the NodeTCF, if == 0 nothing
    if (gdbv.getNbSubNodesTbc(n.f2) > 0) {
      tcfLvl++;
      if ((tcfLvl >= 0) //
          && (expLvl == 1)) {
        // field name already generated above
      } else {
        curCtn.changeTagToTcf(TCFCH[tcfLvl % 3], n.f2.f1.present());
      }
      // f2 -> ExpansionChoices()
      // if ExpansionChoices with choices, add spaces to align with next line with |
      if (n.f2.f1.present()) {
        if (DEBUG_CHARS) {
          curCtn.addChoice("! ");
        } else {
          curCtn.addChoice("  ");
        }
      }
      n.f2.accept(this);
      tcfLvl--;
    }
  }
  
  /**
   * Visits a {@link IdentifierAsString} node, whose child is the following :
   * <p>
   * f0 -> < IDENTIFIER ><br>
   * s: -1580059612<br>
   *
   * @param n - the node to visit
   */
  @Override
  @NodeFieldsSignature(old_sig = -1580059612, new_sig = JTB_SIG_IDENTIFIERASSTRING, name = "IdentifierAsString")
  public void visit(final IdentifierAsString n) {
    curCtn.addId(UnicodeConverter.addUnicodeEscapes(((Token) n.f0).image));
  }
  
  /**
   * Visits a {@link StringLiteral} node, whose child is the following :
   * <p>
   * f0 -> < STRING_LITERAL ><br>
   * s: 241433948<br>
   *
   * @param n - the node to visit
   */
  @Override
  @NodeFieldsSignature(old_sig = 241433948, new_sig = JTB_SIG_STRINGLITERAL, name = "StringLiteral")
  public void visit(final StringLiteral n) {
    curCtn.addId(UnicodeConverter.addUnicodeEscapes(((Token) n.f0).image));
  }
  
  /*
   * Miscellaneous
   */
  
  // /**
  // * Computes the tag for a TCF comment.
  // *
  // * @param aChar - the TCF indicator character
  // * @param aIx - the TCF tag index
  // * @param aBigTag - true when more than 9 cases, false otherwise
  // * @return the tag
  // */
  // private static String compTcfTag(final String aChar, final int aIx, final boolean aBigTag) {
  // return aChar.concat((aIx < 10) && aBigTag ? "0" + aIx : String.valueOf(aIx)).concat(" ");
  // }
  
  /**
   * Reinitializes the prefix with the appropriate number of leading dots.
   *
   * @param fx - the current leading field name
   */
  private void reinitPrefix(final String fx) {
    final int fxl = fx.length();
    prefix.setLength(0);
    if (DEBUG_CHARS) {
      for (int i = 0; i < fxl; i++) {
        prefix.append(";");
      }
      prefix.append(" ;; ");
    } else {
      for (int i = 0; i < fxl; i++) {
        prefix.append(".");
      }
      prefix.append(" .. ");
    }
    curCtn.addPfx(prefix);
  }
  
  /*
   * Called and post processing
   */
  
  /**
   * Generates the field comments and sub comments data. Called by {@link UserClassInfo}.
   *
   * @param aCI - the ClassInfo to work on
   */
  void genCommentsData(final UserClassInfo aCI) {
    classInfo = aCI;
    if (classInfo.fields == null) {
      // empty node, no comments
      return;
    }
    // various initializations
    final int nbf = classInfo.fields.size();
    if (DEBUG_COMMENT) {
      System.out.flush();
      System.err.print("*** GCD fni = " + fni + ", nbf = " + nbf);
      if (nbf > 0) {
        System.err.print(" : ");
        for (int i = 0; i < nbf; i++) {
          final FieldInfo fi = classInfo.fields.get(i);
          System.err.print(fi.name + "/" + fi.type + (i == (nbf - 1) ? " ***" + LS : ", "));
        }
      } else {
        System.err.println(" ***");
      }
      System.err.flush();
    }
    fni = expLvl = seqLvl = chLvl = 0;
    tcfLvl = -1;
    prefix.setLength(0);
    // allocate structures
    roots = new ArrayList<>(nbf);
    for (int i = 0; i < nbf; i++) {
      roots.add(new CommentsTreeNode(null));
    }
    fld = 0;
    if (roots.isEmpty()) {
      return;
    }
    curCtn = roots.get(0);
    // produce comments data
    classInfo.astEcNode.accept(this);
    // end comment processing
    storeClassInfoComments();
  }
  
  /**
   * Stores the comments tree into the current {@link UserClassInfo} corresponding fields.
   */
  private void storeClassInfoComments() {
    
    if (DEBUG_COMMENT) {
      System.out.flush();
      final String msg = "classInfo : " + classInfo.fixedClassName;
      System.err.println(msg);
      System.err.flush();
      final int k = 1;
      for (final CommentsTreeNode root : roots) {
        printCtn(root, k);
      }
    }
    
    // allocate lists
    classInfo.fieldCmts = new ArrayList<>(fld);
    if (jopt.inlineAcceptMethods) {
      classInfo.fieldSubCmts = new ArrayList<>(2 * fld);
    }
    // loop on all fields
    hb.setLength(0);
    for (final CommentsTreeNode root : roots) {
      // allocate structures for the field comment, not (yet) for the sub comments
      final CommentData fldCmtData = classInfo.new CommentData();
      classInfo.fieldCmts.add(fldCmtData);
      final int nbLines = countNbLines(root);
      fldCmtData.lines = new ArrayList<>(nbLines);
      processNode(root, fldCmtData, true, null, false);
      // will be null if hb is empty, i.e. no fields
      classInfo.fieldsHashSig = hb.toString().hashCode();
    }
    
    if (DEBUG_COMMENT) {
      System.out.flush();
      String msg = " ci.fc.sz = " + classInfo.fieldCmts.size() + ", ci.fsc.sz = "
          + (classInfo.fieldSubCmts == null ? -1 : classInfo.fieldSubCmts.size());
      System.err.println(msg);
      for (final CommentData fcd : classInfo.fieldCmts) {
        msg = "  fcd = " + fcd.lines.size();
        System.err.print(msg);
        for (final CommentLineData fcld : fcd.lines) {
          msg = " <" + fcld.bare + ">";
          System.err.print(msg);
        }
        System.err.println();
      }
      if (jopt.inlineAcceptMethods) {
        for (final CommentData scd : classInfo.fieldSubCmts) {
          msg = "  scd = " + scd.lines.size();
          System.err.print(msg);
          for (final CommentLineData scld : scd.lines) {
            msg = " <" + scld.bare + ">";
            System.err.print(msg);
          }
          System.err.println();
        }
      }
      System.err.println();
      System.err.flush();
    }
    
  }
  
  /**
   * Prints a comment tree node.
   *
   * @param aCtn - the comments tree node to print
   * @param aIndent - the indentation level
   */
  private void printCtn(final CommentsTreeNode aCtn, final int aIndent) {
    if (DEBUG_COMMENT) {
      String msg;
      System.out.flush();
      for (int i = 0; i < aIndent; i++) {
        System.err.print(' ');
      }
      if (aCtn.children != null) {
        msg = "ctn.ch.sz = " + aCtn.children.size() + " ; ";
        System.err.print(msg);
      }
      msg = "ctn : " + aCtn.fn + ", " + aCtn.pfx + ", " + aCtn.choice + ", " + aCtn.tag + ", " + aCtn.seq
          + ", " + aCtn.id + ", " + aCtn.end + ", " + aCtn.hasNL;
      System.err.println(msg);
      if (aCtn.children != null) {
        for (final CommentsTreeNode child : aCtn.children) {
          printCtn(child, aIndent + 1);
        }
      }
      System.err.flush();
    }
  }
  
  /**
   * Recursively processes a given comments tree node to store its data into the current {@link UserClassInfo}
   * field comments data and sub comments data members.
   *
   * @param aCtn - a comments tree node
   * @param aFcd - the ClassInfo field comment data
   * @param aWithSubComments - true if create also sub comments, false otherwise
   * @param aPrefix - the prefix of the calling level
   * @param aApplyPrefix - true if the passed prefix is to be applied, false otherwise
   */
  private void processNode(final CommentsTreeNode aCtn, final CommentData aFcd,
      final boolean aWithSubComments, final StringBuilder aPrefix, final boolean aApplyPrefix) {
    /* process this field comment first */
    // copy first members
    if ((aCtn.fn != null) && ((aCtn.fn.charAt(0) != SEPCH) || (fcb.length() != 0))) {
      fcb.append(aCtn.fn);
    } else if (aApplyPrefix && (aPrefix != null)) {
      fcb.append(aPrefix);
    }
    if (aCtn.choice != null) {
      fcb.append(aCtn.choice);
    }
    if (aCtn.tag != null) {
      fcb.append(aCtn.tag);
    }
    if (aCtn.seq != null) {
      fcb.append(aCtn.seq);
    }
    
    // copy id member or walk down the tree to "fill the inside"
    if (aCtn.children == null) {
      if (DEBUG_CHARS) {
        fcb.append(aCtn.id == null ? "{fid}" : aCtn.id);
      } else {
        fcb.append(aCtn.id == null ? "{}" : aCtn.id);
      }
    } else {
      StringBuilder pfx = null;
      final int len = (aPrefix == null ? 0 : aPrefix.length()) + (aCtn.pfx == null ? 0 : aCtn.pfx.length());
      if (len > 0) {
        pfx = new StringBuilder(len);
        if (aPrefix != null) {
          pfx.append(aPrefix);
        }
        if (aCtn.pfx != null) {
          pfx.append(aCtn.pfx);
        }
      }
      boolean apply = false;
      for (final CommentsTreeNode child : aCtn.children) {
        // just fill the "inside" of the field comment
        processNode(child, aFcd, false, pfx, apply);
        apply = true;
      }
    }
    
    // copy end member
    if (aCtn.end != null) {
      fcb.append(aCtn.end);
    }
    
    // change line if needed and copy debug member
    if (aCtn.hasNL) {
      final CommentLineData fcld = classInfo.new CommentLineData();
      aFcd.lines.add(fcld);
      fcld.bare = fcb.toString();
      if (aCtn.debug != null) {
        fcld.debug = aCtn.debug;
      }
      hb.append(fcb);
      fcb.setLength(0);
    }
    
    /* now create direct sub comments of this field comment */
    if (jopt.inlineAcceptMethods && aWithSubComments && (aCtn.children != null)) {
      for (final CommentsTreeNode child : aCtn.children) {
        processSubComments(child, true, null, false);
      }
    }
  }
  
  /**
   * Recursively processes a given comments tree node to store its data into the current {@link UserClassInfo}
   * sub comments data member.
   *
   * @param aCtn - a comments tree node
   * @param aNewSubComment - true to create a new sub comment, false otherwise
   * @param aPrefix - the prefix of the calling level
   * @param aApplyPrefix - true if the passed prefix is to be applied, false otherwise
   */
  private void processSubComments(final CommentsTreeNode aCtn, final boolean aNewSubComment,
      final StringBuilder aPrefix, final boolean aApplyPrefix) {
    
    /* process this sub comment first */
    // create a new sub comment at first call level
    if (aNewSubComment) {
      final CommentData subCmtData = classInfo.new CommentData();
      classInfo.fieldSubCmts.add(subCmtData);
      subCmtData.lines = new ArrayList<>();
      scb.setLength(0);
    }
    
    // copy first members
    if ((aCtn.fn != null) && ((aCtn.fn.charAt(0) != SEPCH) || (scb.length() != 0))) {
      scb.append(aCtn.fn);
    } else if (aApplyPrefix && (aPrefix != null)) {
      scb.append(aPrefix);
    }
    // don't output choice string if at the beginning of the line
    if ((aCtn.choice != null) && (scb.length() != 0)) {
      scb.append(aCtn.choice);
    }
    if (aCtn.tag != null) {
      scb.append(aCtn.tag);
    }
    if (aCtn.seq != null) {
      scb.append(aCtn.seq);
    }
    
    // copy id member or walk down the sub comments
    if (aCtn.children == null) {
      if (DEBUG_CHARS) {
        scb.append(aCtn.id == null ? "{sid}" : aCtn.id);
      } else {
        scb.append(aCtn.id == null ? "{}" : aCtn.id);
      }
    } else {
      StringBuilder pfx = null;
      final int len = (aPrefix == null ? 0 : aPrefix.length()) + (aCtn.pfx == null ? 0 : aCtn.pfx.length());
      if (len > 0) {
        pfx = new StringBuilder(len);
        if (aPrefix != null) {
          pfx.append(aPrefix);
        }
        if (aCtn.pfx != null) {
          // don't output the choice string if at the beginning of the line
          // a trick is used here (only choice prefix has only one character before the first space)
          if (aCtn.pfx.charAt(1) == ' ') {
            pfx.append(aCtn.pfx.subSequence(2, aCtn.pfx.length()));
          } else {
            pfx.append(aCtn.pfx);
          }
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
    if (aCtn.end != null) {
      scb.append(aCtn.end);
    }
    
    // create sub comment line if needed
    if (aNewSubComment || aCtn.hasNL) {
      final CommentLineData scld = classInfo.new CommentLineData();
      classInfo.fieldSubCmts.get(classInfo.fieldSubCmts.size() - 1).lines.add(scld);
      scld.bare = scb.toString();
      scb.setLength(0);
    }
    
    /* now create direct sub comments of this sub comment */
    if (aNewSubComment && (aCtn.children != null)) {
      for (final CommentsTreeNode child : aCtn.children) {
        processSubComments(child, true, null, false);
      }
    }
  }
  
  /**
   * Counts the total number of lines in a node and its sub tree.
   *
   * @param aCtn - a node of the comments tree
   * @return the total number of lines in a node and its sub tree
   */
  private int countNbLines(final CommentsTreeNode aCtn) {
    int nb = 0;
    if (aCtn.getNL()) {
      nb++;
    }
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
  private class CommentsTreeNode {
    
    /** The parent, null if root */
    CommentsTreeNode       parent   = null;
    /** The children, null if none */
    List<CommentsTreeNode> children = null;
    /** The field name */
    String                 fn       = null;
    /** The prefix (for the following nodes at the same level) */
    String                 pfx      = null;
    /** The choice string */
    String                 choice   = null;
    /**
     * The tag ({@link #WHCH}, {@link #SEQCH}, {@link #TCFCH} with an index 0, 1, ...)
     */
    String                 tag      = null;
    /** The sequence string */
    String                 seq      = null;
    /** The body : identifier or production */
    StringBuilder          id       = null;
    /** The end string */
    String                 end      = null;
    /** The debug string */
    String                 debug    = null;
    /** The flag telling there is a new line or not */
    boolean                hasNL    = false;
    
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
      if (children == null) {
        children = new ArrayList<>(4);
      }
      children.add(child);
      curCtn = child;
      if (DEBUG_COMMENT) {
        System.out.flush();
        final String msg = "nsc : " + classInfo.fixedClassName + " : this : " + toString().substring(23)
            + ", child : " + (curCtn == null ? null : curCtn.toString().substring(23)) + ", cp.expLvl : "
            + expLvl + ", cp.tcfLvl : " + tcfLvl;
        System.err.println(msg);
        System.err.flush();
        if (curCtn != null) {
          curCtn.addDebug("<br>" + LS + "   * ");
          curCtn.addDebug(msg);
        }
      }
    }
    
    /**
     * Get the parent comment as the current.
     */
    void endSubComment() {
      assert curCtn.parent != null : "trying to end a sub comment when on a field comment";
      curCtn = curCtn.parent;
      if (DEBUG_COMMENT) {
        System.out.flush();
        final String msg = "esc : " + classInfo.fixedClassName + " : this : " + toString().substring(23)
            + ", parent : " + (curCtn == null ? null : curCtn.toString().substring(23)) + ", cp.expLvl : "
            + expLvl + ", cp.tcfLvl : " + tcfLvl;
        System.err.println(msg);
        System.err.flush();
        if (curCtn != null) {
          curCtn.addDebug("<br>" + LS + "   * ");
          curCtn.addDebug(msg);
        }
      }
    }
    
    /**
     * Adds a String to the field name.
     *
     * @param aStr - what to be added
     */
    final void addFn(final String aStr) {
      if (fn == null) {
        fn = aStr;
      } else {
        fn += aStr;
      }
    }
    
    /**
     * Adds two String to the field name.
     *
     * @param aStr1 - what to be added
     * @param aStr2 - what to be added
     */
    final void addFn(final String aStr1, final String aStr2) {
      if (fn == null) {
        fn = aStr1 + aStr2;
      } else {
        fn += aStr1;
        fn += aStr2;
      }
    }
    
    /**
     * Adds a {@link CharSequence} to the prefix member.
     *
     * @param aChSeq - what to be added
     */
    final void addPfx(final CharSequence aChSeq) {
      if (pfx == null) {
        pfx = aChSeq.toString();
      } else {
        pfx += aChSeq.toString();
      }
    }
    
    /**
     * Adds a String to the choice string member.
     *
     * @param aStr - what to be added
     */
    final void addChoice(final String aStr) {
      if (choice == null) {
        choice = aStr;
      } else {
        choice += aStr;
      }
    }
    
    /**
     * Adds a String to the sequence string member.
     *
     * @param aStr - what to be added
     */
    final void addSeq(final String aStr) {
      if (seq == null) {
        seq = aStr;
      } else {
        seq += aStr;
      }
    }
    
    /**
     * Adds a String to the tag member.
     *
     * @param aStr - what to be added
     */
    final void addTag(final String aStr) {
      if (tag == null) {
        tag = aStr;
      } else {
        tag += aStr;
      }
    }
    
    /**
     * Changes the first character of the tag member, and add an optional suffix.
     *
     * @param aChar - the remplacement character
     * @param aAddSfx - true for adding the suffix, false otherwise
     */
    final void changeTagToTcf(final char aChar, final boolean aAddSfx) {
      // assert tag != null : "trying to change a null tag";
      if (tag != null) {
        tag = aChar + tag.substring(1);
        if (aAddSfx) {
          if (DEBUG_CHARS) {
            tag += "~ ";
          } else {
            tag += ". ";
          }
        }
      } else {
        // FIXME tbc
      }
    }
    
    /**
     * Adds a char to the id member.
     *
     * @param aCh - what to be added
     */
    final void addId(final char aCh) {
      if (id == null) {
        id = new StringBuilder(16);
      }
      id.append(aCh);
    }
    
    /**
     * Adds a {@link CharSequence} to the id member.
     *
     * @param aChSeq - what to be added
     */
    final void addId(final CharSequence aChSeq) {
      if (id == null) {
        id = new StringBuilder(aChSeq.length() + 16);
      }
      id.append(aChSeq);
    }
    
    /**
     * Adds a String to the end string member.
     *
     * @param aStr - what to be added
     */
    final void addEnd(final String aStr) {
      if (end == null) {
        end = aStr;
      } else {
        end += aStr;
      }
    }
    
    /**
     * Adds a String to the debug string member.
     *
     * @param aStr - what to be added
     */
    final void addDebug(final String aStr) {
      if (debug == null) {
        debug = aStr;
      } else {
        debug += aStr;
      }
    }
    
    /**
     * Setter for the new line member.
     */
    final void setNL() {
      hasNL = true;
    }
    
    /**
     * Getter for the new line member.
     *
     * @return the new line member
     */
    final boolean getNL() {
      return hasNL;
    }
    
  } // end of class CommentsTreeNode
  
} // end of class CommentsPrinter

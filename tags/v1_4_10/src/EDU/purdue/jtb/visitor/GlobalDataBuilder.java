package EDU.purdue.jtb.visitor;

import static EDU.purdue.jtb.misc.Globals.*;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import EDU.purdue.jtb.JTB;
import EDU.purdue.jtb.misc.ClassInfo;
import EDU.purdue.jtb.misc.DepthFirstVisitorsGenerator;
import EDU.purdue.jtb.misc.Messages;
import EDU.purdue.jtb.syntaxtree.BNFProduction;
import EDU.purdue.jtb.syntaxtree.ClassOrInterfaceType;
import EDU.purdue.jtb.syntaxtree.ComplexRegularExpression;
import EDU.purdue.jtb.syntaxtree.ComplexRegularExpressionChoices;
import EDU.purdue.jtb.syntaxtree.ComplexRegularExpressionUnit;
import EDU.purdue.jtb.syntaxtree.ExpansionUnit;
import EDU.purdue.jtb.syntaxtree.INode;
import EDU.purdue.jtb.syntaxtree.IdentifierAsString;
import EDU.purdue.jtb.syntaxtree.JavaCCInput;
import EDU.purdue.jtb.syntaxtree.JavaCodeProduction;
import EDU.purdue.jtb.syntaxtree.NodeChoice;
import EDU.purdue.jtb.syntaxtree.NodeOptional;
import EDU.purdue.jtb.syntaxtree.NodeSequence;
import EDU.purdue.jtb.syntaxtree.NodeToken;
import EDU.purdue.jtb.syntaxtree.PrimitiveType;
import EDU.purdue.jtb.syntaxtree.Production;
import EDU.purdue.jtb.syntaxtree.ReferenceType;
import EDU.purdue.jtb.syntaxtree.RegExprSpec;
import EDU.purdue.jtb.syntaxtree.RegularExprProduction;
import EDU.purdue.jtb.syntaxtree.RegularExpression;
import EDU.purdue.jtb.syntaxtree.ResultType;
import EDU.purdue.jtb.syntaxtree.StringLiteral;
import EDU.purdue.jtb.syntaxtree.Type;

/**
 * The {@link GlobalDataBuilder} visitor performs, at the beginning of the {@link JTB} processing,
 * some error checking and build objects needed by other classes ({@link ClassesFinder},
 * {@link ClassInfo}, {@link SemanticChecker}, {@link CommentsPrinter} ), {@link Annotator},
 * {@link AcceptInliner}), {@link DepthFirstVisitorsGenerator} :
 * <ul>
 * <li>a {@link CommentsPrinter} (to be used by all {@link ClassInfo}),</li>
 * <li>a Hashtable ({@link #nsnHT}) of JavaCodeProductions whose nodes must be created ("%" syntax)
 * and of BNFProductions whose nodes must not be created ("!" syntax),</li>
 * <li>a Hashtable ({@link #prodHT}) of all JavaCodeProductions and BNFProductions identifiers and
 * their result type,</li>
 * <li>a list ({@link #retVarDecl}) of return variables declarations (for all non "void"
 * JavaCodeProductions and BNFProductions for which the node creation has not been forbidden - "!"
 * syntax)</li>
 * <li>a Hashtable ({@link #tokenHT}) of tokens which have a constant regular expression, e.g. <
 * PLUS : "+" >, which will be used to generate a default constructor.</li>
 * </ul>
 * 
 * @version 1.4.7 : 09/2012 : MMa : created from the old GlobalDataFinder in Annotator and
 *          TokenTableBuilder.
 * @version 1.4.8 : 10/2012 : MMa : added JavaCodeProduction class generation if requested ;
 *          modified error checking and messages
 */
public class GlobalDataBuilder extends DepthFirstVoidVisitor {

  /** The {@link CommentsPrinter} visitor */
  CommentsPrinter                   cpv;

  /**
   * The table of non standard nodes : JavaCodeProductions whose nodes must be created ("%" syntax)
   * and BNFProductions whose nodes must not be created ("!" syntax)<br>
   * key = identifier, value = {@link #JC_IND} for JavaCodeProductions or {@link #BNF_IND} for
   * BNFProduction
   */
  Map<String, String>               nsnHT       = new Hashtable<String, String>();

  /**
   * The table of all nodes : JavaCodeProductions and BNFProductions<br>
   * key = identifier, value = {@link #JC_IND} or {@link #BNF_IND} + ResultType
   */
  Map<String, String>               prodHT      = new Hashtable<String, String>(100);

  /** The indicator for JavaCodeProduction in the {@link #nsnHT} and {@link #prodHT} tables */
  public static final String        JC_IND      = "µ";

  /** The indicator for BNFProduction in the {@link #nsnHT} and {@link #prodHT} tables */
  public static final String        BNF_IND     = "£";

  /**
   * The list of all return variables pairs of comments and declarations (for all non "void"
   * JavaCodeProductions and BNFProductions for which the node creation has not been forbidden)
   */
  List<String>                      retVarDecl  = new ArrayList<String>();

  /**
   * The table of tokens<br>
   * key = token name, value = regular expression or {@link #DONT_CREATE} for nodes not to be
   * created
   */
  private final Map<String, String> tokenHT     = new Hashtable<String, String>();

  /** The current token's name */
  private String                    name        = "";

  /**
   * The current token's regular expression ; set to {@link #DONT_CREATE} for a node not to be
   * created
   */
  private String                    regExpr     = "";

  /** The specific regular expression for a node not to be created */
  public static final String        DONT_CREATE = "!";

  /** True to tell from RegExprSpec to create a node, false otherwise */
  boolean                           cnfres      = true;

  /** True for first pass, false for the second */
  boolean                           firstPass   = true;

  /**
   * Constructor.
   */
  public GlobalDataBuilder() {
    super();
    cpv = new CommentsPrinter(this);
  }

  /**
   * Visits a {@link JavaCCInput} node, whose children are the following :
   * <p>
   * f0 -> JavaCCOptions()<br>
   * f1 -> "PARSER_BEGIN"<br>
   * f2 -> "("<br>
   * f3 -> IdentifierAsString()<br>
   * f4 -> ")"<br>
   * f5 -> CompilationUnit()<br>
   * f6 -> "PARSER_END"<br>
   * f7 -> "("<br>
   * f8 -> IdentifierAsString()<br>
   * f9 -> ")"<br>
   * f10 -> ( Production() )+<br>
   * 
   * @param n - the node to visit
   */
  @Override
  public void visit(final JavaCCInput n) {
    // visit only Productions
    // f10 -> ( Production() )+
    for (final Iterator<INode> e = n.f10.elements(); e.hasNext();) {
      e.next().accept(this);
    }
    firstPass = false;
    for (final Iterator<INode> e = n.f10.elements(); e.hasNext();) {
      e.next().accept(this);
    }
  }

  /**
   * Visits a {@link Production} node, whose children are the following :
   * <p>
   * f0 -> . %0 JavaCodeProduction()<br>
   * .. .. | %1 RegularExprProduction()<br>
   * .. .. | %2 TokenManagerDecls()<br>
   * .. .. | %3 BNFProduction()<br>
   * 
   * @param n - the node to visit
   */
  @Override
  public void visit(final Production n) {
    // do not visit TokenManagerDecls
    if (n.f0.which != 2)
      n.f0.accept(this);
  }

  /**
   * Visits a {@link JavaCodeProduction} node, whose children are the following :
   * <p>
   * f0 -> "JAVACODE"<br>
   * f1 -> AccessModifier()<br>
   * f2 -> ResultType()<br>
   * f3 -> IdentifierAsString()<br>
   * f4 -> FormalParameters()<br>
   * f5 -> [ #0 "throws" #1 Name()<br>
   * .. .. . #2 ( $0 "," $1 Name() )* ]<br>
   * f6 -> [ "%" ]<br>
   * f7 -> Block()<br>
   * 
   * @param n - the node to visit
   */
  @Override
  public void visit(final JavaCodeProduction n) {
    // f3 -> IdentifierAsString()
    final String ident = n.f3.f0.tokenImage;
    if (firstPass) {
      // controls and global data
      if (prodHT.containsKey(ident))
        Messages.softErr("This JavaCodeProduction has the same name '" + ident +
                             "' as another BNFProduction or JavaCodeProduction.",
                         n.f3.f0.beginLine,
                         n.f3.f0.beginColumn);
      else if (ident.equals(iNode) || ident.equals(iNodeList) || ident.equals(nodeList) ||
               ident.equals(nodeListOpt) || ident.equals(nodeOpt) || ident.equals(nodeSeq) ||
               ident.equals(nodeToken) || ident.equals(nodeChoice))
        Messages.softErr("JavaCodeProduction '" + ident +
                             "()' has the same name as a JTB generated class.", n.f3.f0.beginLine,
                         n.f3.f0.beginColumn);
      else {
        // f2 -> ResultType()
        final String resType = getResultType(n.f2);
        if ((!"void".equals(resType))) {
          if (resType.equals(ident))
            Messages.softErr("JavaCodeProduction '" + ident +
                                 "()' has a return type of the same name," +
                                 " that would conflict with the generated node class.",
                             n.f3.f0.beginLine, n.f3.f0.beginColumn);
        }
        prodHT.put(ident, JC_IND + resType);
        // f6 -> [ "%" ]
        if (n.f6.present()) {
          // add this node to the list of non standard nodes
          nsnHT.put(ident, JC_IND);
        }
      }
    } else {
      // return variable declaration
      if (n.f6.present()) {
        final String resType = getResultType(n.f2);
        if ((!"void".equals(resType)))
          if (prodHT.containsKey(resType)) {
            Messages.softErr("This JavaCodeProduction '" + ident + "'has a ResultType '" + resType +
                                 "' of the same name as another BNFProduction or JavaCodeProduction.",
                             n.f3.f0.beginLine, n.f3.f0.beginColumn);
          } else {
            // generate return variable declaration
            final String comm = "/** Return variable for the {@link #".concat(ident)
                                                                      .concat("} JavaCodeProduction */");
            retVarDecl.add(comm);
            final String rt = fixResultType(ident, resType);
            final String decl = (staticFlag ? "static " : "").concat(rt).concat(" ")
                                                             .concat(jtbRtPrefix).concat(ident)
                                                             .concat(";");
            retVarDecl.add(decl);
          }
      }
    }
  }

  /**
   * Visits a {@link BNFProduction} node, whose children are the following :
   * <p>
   * f0 -> AccessModifier()<br>
   * f1 -> ResultType()<br>
   * f2 -> IdentifierAsString()<br>
   * f3 -> FormalParameters()<br>
   * f4 -> [ #0 "throws" #1 Name()<br>
   * .. .. . #2 ( $0 "," $1 Name() )* ]<br>
   * f5 -> [ "!" ]<br>
   * f6 -> ":"<br>
   * f7 -> Block()<br>
   * f8 -> "{"<br>
   * f9 -> ExpansionChoices()<br>
   * f10 -> "}"<br>
   * 
   * @param n - the node to visit
   */
  @Override
  public void visit(final BNFProduction n) {
    // f2 -> IdentifierAsString()
    final String ident = n.f2.f0.tokenImage;
    if (firstPass) {
      // controls and global data
      if (prodHT.containsKey(ident))
        Messages.softErr("This BNFProduction has the same name '" + ident +
                             "' as another BNFProduction or JavaCodeProduction.",
                         n.f2.f0.beginLine,
                         n.f2.f0.beginColumn);
      else if (ident.equals(iNode) || ident.equals(iNodeList) || ident.equals(nodeList) ||
               ident.equals(nodeListOpt) || ident.equals(nodeOpt) || ident.equals(nodeSeq) ||
               ident.equals(nodeToken) || ident.equals(nodeChoice))
        Messages.softErr("BNFProduction '" + ident +
                             "()' has the same name as a JTB generated class.", n.f2.f0.beginLine,
                         n.f2.f0.beginColumn);
      else {
        // f1 -> ResultType()
        final String resType = getResultType(n.f1);
        if ((!"void".equals(resType)))
          if (resType.equals(ident))
            Messages.softErr("BNFProduction '" + ident + "()' has a return type of the same name," +
                                 " that would conflict with the generated node class.",
                             n.f2.f0.beginLine, n.f2.f0.beginColumn);
        prodHT.put(ident, BNF_IND + resType);
        // f5 -> [ "!" ]
        if (n.f5.present()) {
          // add this node to the list of non standard nodes
          nsnHT.put(ident, BNF_IND);
        }
      }
    } else {
      // return variable declaration
      if (!n.f5.present()) {
        final String resType = getResultType(n.f1);
        if ((!"void".equals(resType)))
          if (prodHT.containsKey(resType)) {
            Messages.softErr("This BNFProduction '" + ident + "'has a ResultType '" + resType +
                                 "' of the same name as another BNFProduction or JavaCodeProduction.",
                             n.f2.f0.beginLine, n.f2.f0.beginColumn);
          } else {
            final String comm = "/** Return variable for the {@link #".concat(ident)
                                                                      .concat("} BNFProduction */");
            retVarDecl.add(comm);
            final String rt = fixResultType(ident, resType);
            final String decl = (staticFlag ? "static " : "").concat(rt).concat(" ")
                                                             .concat(jtbRtPrefix).concat(ident)
                                                             .concat(";");
            retVarDecl.add(decl);
          }
      }
    }
  }

  /**
   * Gets the ResultType from the grammar. Walks down the tree to find the first token.
   * <p>
   * {@link ResultType}<br>
   * f0 -> ( %0 "void"<br>
   * .. .. | %1 Type() )<br>
   * <p>
   * {@link Type}<br>
   * f0 -> . %0 ReferenceType()<br>
   * .. .. | %1 PrimitiveType()<br>
   * <p>
   * {@link ReferenceType}<br>
   * f0 -> . %0 #0 PrimitiveType()<br>
   * .. .. . .. #1 ( $0 "[" $1 "]" )+<br>
   * .. .. | %1 #0 ClassOrInterfaceType()<br>
   * .. .. . .. #1 ( $0 "[" $1 "]" )*<br>
   * <p>
   * {@link PrimitiveType}<br>
   * f0 -> . %0 "boolean"<br>
   * .. .. | %1 "char"<br>
   * .. .. | %2 "byte"<br>
   * .. .. | %3 "short"<br>
   * .. .. | %4 "int"<br>
   * .. .. | %5 "long"<br>
   * .. .. | %6 "float"<br>
   * .. .. | %7 "double"<br>
   * <p>
   * {@link ClassOrInterfaceType}<br>
   * f0 -> < IDENTIFIER ><br>
   * f1 -> [ TypeArguments() ]<br>
   * f2 -> ( #0 "." #1 < IDENTIFIER ><br>
   * .. .. . #2 [ TypeArguments() ] )*<br>
   * 
   * @param rt - the node to process
   * @return the result type token image
   */
  static String getResultType(final ResultType rt) {
    NodeToken tk;
    final INode n = rt.f0.choice;
    if (rt.f0.which == 0) {
      // "void"
      tk = (NodeToken) n;
    } else {
      // Type(
      final NodeChoice ch = ((Type) n).f0;
      if (ch.which == 0) {
        // ReferenceType()
        final NodeChoice ch1 = ((ReferenceType) ch.choice).f0;
        if (ch1.which == 0) {
          // PrimitiveType() ( "[" "]" )+
          tk = (NodeToken) ((PrimitiveType) ch1.choice).f0.choice;
        } else {
          // ClassOrInterfaceType() ( "[" "]" )*
          tk = ((ClassOrInterfaceType) ((NodeSequence) ch1.choice).elementAt(0)).f0;
        }
      } else {
        // PrimitiveType()
        tk = (NodeToken) ((PrimitiveType) ch.choice).f0.choice;
      }
    }
    return tk.tokenImage;
  }

  /**
   * Returns the right result type to be output, with or without prefix / suffix.
   * 
   * @param aIdent - the production identifier
   * @param aResType - the production result type
   * @return the right result type, with or without prefix / suffix
   */
  String fixResultType(final String aIdent, final String aResType) {
    String rt = prodHT.get(aIdent);
    if (rt == null)
      return aResType;
    rt = rt.substring(1);
    if ("void".equals(rt))
      return getFixedName(aResType);
    return prodHT.containsKey(rt) ? getFixedName(aResType) : aResType;
  }

/**
   * Visits a {@link RegularExprProduction} node, whose children are the following :
   * <p>
   * f0 -> [ %0 #0 "<"<br>
   * .. .. . .. #1 "*"<br>
   * .. .. . .. #2 ">"<br>
   * .. .. | %1 #0 "<"<br>
   * .. .. . .. #1 < IDENTIFIER ><br>
   * .. .. . .. #2 ( $0 ","<br>
   * .. .. . .. .. . $1 < IDENTIFIER > )*<br>
   * .. .. . .. #3 ">" ]<br>
   * f1 -> RegExprKind()<br>
   * f2 -> [ #0 "["<br>
   * .. .. . #1 "IGNORE_CASE"<br>
   * .. .. . #2 "]" ]<br>
   * f3 -> ":"<br>
   * f4 -> "{"<br>
   * f5 -> RegExprSpec()<br>
   * f6 -> ( #0 "|"<br>
   * .. .. . #1 RegExprSpec() )*<br>
   * f7 -> "}"<br>
   *
   * @param n - the node to visit
   */
  @Override
  public void visit(final RegularExprProduction n) {
    // f5 -> RegExprSpec()
    n.f5.accept(this);
    // f6 -> ( #0 "|" #1 RegExprSpec() )*
    if (n.f6.present())
      for (final Iterator<INode> e = n.f6.elements(); e.hasNext();) {
        // #1 RegExprSpec()
        ((NodeSequence) e.next()).elementAt(1).accept(this);
      }
  }

  /**
   * Visits a {@link RegExprSpec} node, whose children are the following :
   * <p>
   * f0 -> RegularExpression()<br>
   * f1 -> [ "!" ]<br>
   * f2 -> [ Block() ]<br>
   * f3 -> [ #0 ":" #1 < IDENTIFIER > ]<br>
   * 
   * @param n - the node to visit
   */
  @Override
  public void visit(final RegExprSpec n) {
    // f1 -> [ "!" ]
    // tell lower levels whether to create a node or not
    cnfres = !n.f1.present();
    // visit only f0 -> RegularExpression()
    n.f0.accept(this);
  }

/**
   * Visits a {@link RegularExpression} node, whose children are the following :
   * <p>
   * f0 -> . %0 StringLiteral()<br>
   * .. .. | %1 #0 "<"<br>
   * .. .. . .. #1 [ $0 [ "#" ]<br>
   * .. .. . .. .. . $1 IdentifierAsString() $2 ":" ]<br>
   * .. .. . .. #2 ComplexRegularExpressionChoices() #3 ">"<br>
   * .. .. | %2 #0 "<" #1 IdentifierAsString() #2 ">"<br>
   * .. .. | %3 #0 "<" #1 "EOF" #2 ">"<br>
   *
   * @param n - the node to visit
   */
  @Override
  public void visit(final RegularExpression n) {
    if (n.f0.which == 1) {
      // %1 #0 "<"
      //    #1 [ $0 [ "#" ] $1 IdentifierAsString() $2 ":" ]
      //    #2 ComplexRegularExpressionChoices() #3 ">"
      final NodeSequence seq = (NodeSequence) n.f0.choice;
      final NodeOptional opt = (NodeOptional) seq.elementAt(1);
      if (opt.present()) {
        // #1 [ $0 [ "#" ] $1 IdentifierAsString() $2 ":" ]
        // name is set further down the tree
        seq.elementAt(1).accept(this);
        // #2 ComplexRegularExpressionChoices()
        // regExpr is set further down the tree (if not requested not to create the node)
        if (cnfres) {
          seq.elementAt(2).accept(this);
          tokenHT.put(name, regExpr);
        } else
          tokenHT.put(name, DONT_CREATE);
        // reset for next pass
        name = "";
        regExpr = "";
      }
    }
  }

  /**
   * Visits a {@link ComplexRegularExpressionChoices} node, whose children are the following :
   * <p>
   * f0 -> ComplexRegularExpression()<br>
   * f1 -> ( #0 "|" #1 ComplexRegularExpression() )*<br>
   * 
   * @param n - the node to visit
   */
  @Override
  public void visit(final ComplexRegularExpressionChoices n) {
    if (n.f1.present())
      // if f1 -> ( #0 "|" #1 ComplexRegularExpression() )* is present, this isn't a constant regexpr
      regExpr = "";
    else
      // f0 -> ComplexRegularExpression()
      n.f0.accept(this);
  }

  /**
   * Visits a {@link ComplexRegularExpression} node, whose children are the following :
   * <p>
   * f0 -> ( ComplexRegularExpressionUnit() )+<br>
   * 
   * @param n - the node to visit
   */
  @Override
  public void visit(final ComplexRegularExpression n) {
    // no difference with super class
    n.f0.accept(this);
  }

  /**
   * Visits a {@link ComplexRegularExpressionUnit} node, whose children are the following :
   * <p>
   * f0 -> . %0 StringLiteral()<br>
   * .. .. | %1 #0 "<" #1 IdentifierAsString() #2 ">"<br>
   * .. .. | %2 CharacterList()<br>
   * .. .. | %3 #0 "(" #1 ComplexRegularExpressionChoices() #2 ")"<br>
   * .. .. . .. #3 ( &0 "+"<br>
   * .. .. . .. .. | &1 "*"<br>
   * .. .. . .. .. | &2 "?"<br>
   * .. .. . .. .. | &3 $0 "{" $1 IntegerLiteral()<br>
   * .. .. . .. .. . .. $2 [ ?0 ","<br>
   * .. .. . .. .. . .. .. . ?1 [ IntegerLiteral() ] ]<br>
   * .. .. . .. .. . .. $3 "}" )?<br>
   * 
   * @param n - the node to visit
   */
  @Override
  public void visit(final ComplexRegularExpressionUnit n) {
    if (n.f0.which == 0)
      // %0 StringLiteral()
      n.f0.accept(this);
    else
      // others
      regExpr = "";
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
    name = n.f0.tokenImage;
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
    regExpr = n.f0.tokenImage;
  }

  /*
   * Getters
   */

  /**
   * @return the table of JavaCodeProductions whose nodes must be created ("%" syntax) and of
   *         BNFProductions whose nodes must not be created ("!" syntax)
   */
  public final Map<String, String> getNsnHT() {
    return nsnHT;
  }

  /**
   * @return the table of all BNFProductions and JavaCodeProductions
   */
  public final Map<String, String> getProdHT() {
    return prodHT;
  }

  /**
   * @return the list of all return variables pairs of comments and declarations
   */
  public final List<String> getRetVarDecl() {
    return retVarDecl;
  }

  /**
   * @return the comments printer visitor
   */
  public final CommentsPrinter getCpv() {
    return cpv;
  }

  /**
   * The returned Hashtable has the names of the tokens as the keys and the constant regular
   * expressions as the values or "" if the regular expression is not constant.
   * 
   * @return the Hashtable
   */
  public Map<String, String> getTokenHT() {
    return tokenHT;
  }

/**
   * Returns true if not LocalLookahead nor Block nor a not to be created node.<br>
   * ExpansionUnit grammar:<br>
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
   * RegularExpression grammar:<br>
   * f0 -> . %0 StringLiteral()<br>
   * .. .. | %1 #0 "<"<br>
   * .. .. . .. #1 [ $0 [ "#" ]<br>
   * .. .. . .. .. . $1 IdentifierAsString() $2 ":" ]<br>
   * .. .. . .. #2 ComplexRegularExpressionChoices() #3 ">"<br>
   * .. .. | %2 #0 "<" #1 IdentifierAsString() #2 ">"<br>
   * .. .. | %3 #0 "<" #1 "EOF" #2 ">"<br>
   * 
   * @param expUnit - the ExpansionUnit
   * @return true if not LocalLookahead nor Block nor a not to be created node, false otherwise
   */
  boolean isEuOk(final ExpansionUnit expUnit) {
    if (expUnit.f0.which > 1) {
      if (expUnit.f0.which == 4) {
        // ExpansionUnit type 4
        final NodeSequence seq = (NodeSequence) expUnit.f0.choice;
        final NodeChoice ch = (NodeChoice) seq.elementAt(1);
        // #1 ( &0 $0 IdentifierAsString() $1 Arguments()
        // .. . .. $2 [ "!" ]
        // .. | &1 $0 RegularExpression()
        // .. . .. $1 [ ?0 "." ?1 < IDENTIFIER > ]
        // .. . .. $2 [ "!" ] )
        if (ch.which == 0) {
          // $0 IdentifierAsString() $1 Arguments() $2 [ "!" ]
          final NodeSequence seq1 = (NodeSequence) ch.choice;
          final String ident = ((IdentifierAsString) seq1.elementAt(0)).f0.tokenImage;
          if (nsnHT.containsKey(ident) || ((NodeOptional) seq1.elementAt(2)).present())
            // node not to be created
            return false;
          else
            return true;
        } else {
          // $0 RegularExpression() $1 [ ?0 "." ?1 < IDENTIFIER > ] $2 [ "!" ]
          final NodeSequence seq1 = (NodeSequence) ch.choice;
          if (((NodeOptional) seq1.elementAt(2)).present())
            // node not to be created
            return false;
          else {
            final RegularExpression re = ((RegularExpression) seq1.elementAt(0));
            if (re.f0.which == 2) {
              // #0 "<" #1 IdentifierAsString() #2 ">"
              final NodeSequence seq2 = (NodeSequence) re.f0.choice;
              final String ident = ((IdentifierAsString) seq2.elementAt(1)).f0.tokenImage;
              final String val = tokenHT.get(ident);
              //            if (val == null)
              //              throw new AssertionError("val is null");
              if (DONT_CREATE.equals(val))
                // node not to be created
                return false;
              else
                return true;
            } else
              // RegularExpression type 0, 1 & 3
              return true;
          }
        }
      } else
        // ExpansionUnit type 2, 3 & 5
        return true;
    }
    // ExpansionUnit type 0 & 1 (LocalLookahead and Block)
    return false;
  }

}

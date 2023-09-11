package EDU.purdue.jtb.analyse;

import static EDU.purdue.jtb.common.Constants.iNode;
import static EDU.purdue.jtb.common.Constants.iNodeList;
import static EDU.purdue.jtb.common.Constants.jjToken;
import static EDU.purdue.jtb.common.Constants.nodeChoice;
import static EDU.purdue.jtb.common.Constants.nodeList;
import static EDU.purdue.jtb.common.Constants.nodeListOptional;
import static EDU.purdue.jtb.common.Constants.nodeOptional;
import static EDU.purdue.jtb.common.Constants.nodeSequence;
import static EDU.purdue.jtb.common.Constants.nodeToken;
import static EDU.purdue.jtb.parser.syntaxtree.NodeConstants.JTB_SIG_BNFPRODUCTION;
import static EDU.purdue.jtb.parser.syntaxtree.NodeConstants.JTB_SIG_CLASSORINTERFACETYPE;
import static EDU.purdue.jtb.parser.syntaxtree.NodeConstants.JTB_SIG_COMPILATIONUNIT;
import static EDU.purdue.jtb.parser.syntaxtree.NodeConstants.JTB_SIG_COMPLEXREGULAREXPRESSION;
import static EDU.purdue.jtb.parser.syntaxtree.NodeConstants.JTB_SIG_COMPLEXREGULAREXPRESSIONCHOICES;
import static EDU.purdue.jtb.parser.syntaxtree.NodeConstants.JTB_SIG_COMPLEXREGULAREXPRESSIONUNIT;
import static EDU.purdue.jtb.parser.syntaxtree.NodeConstants.JTB_SIG_EXPANSION;
import static EDU.purdue.jtb.parser.syntaxtree.NodeConstants.JTB_SIG_EXPANSIONCHOICES;
import static EDU.purdue.jtb.parser.syntaxtree.NodeConstants.JTB_SIG_EXPANSIONUNIT;
import static EDU.purdue.jtb.parser.syntaxtree.NodeConstants.JTB_SIG_EXPANSIONUNITTCF;
import static EDU.purdue.jtb.parser.syntaxtree.NodeConstants.JTB_SIG_IDENTIFIERASSTRING;
import static EDU.purdue.jtb.parser.syntaxtree.NodeConstants.JTB_SIG_JAVACCINPUT;
import static EDU.purdue.jtb.parser.syntaxtree.NodeConstants.JTB_SIG_JAVACODEPRODUCTION;
import static EDU.purdue.jtb.parser.syntaxtree.NodeConstants.JTB_SIG_PRIMITIVETYPE;
import static EDU.purdue.jtb.parser.syntaxtree.NodeConstants.JTB_SIG_PRODUCTION;
import static EDU.purdue.jtb.parser.syntaxtree.NodeConstants.JTB_SIG_REFERENCETYPE;
import static EDU.purdue.jtb.parser.syntaxtree.NodeConstants.JTB_SIG_REGEXPRSPEC;
import static EDU.purdue.jtb.parser.syntaxtree.NodeConstants.JTB_SIG_REGULAREXPRESSION;
import static EDU.purdue.jtb.parser.syntaxtree.NodeConstants.JTB_SIG_REGULAREXPRPRODUCTION;
import static EDU.purdue.jtb.parser.syntaxtree.NodeConstants.JTB_SIG_RESULTTYPE;
import static EDU.purdue.jtb.parser.syntaxtree.NodeConstants.JTB_SIG_STRINGLITERAL;
import static EDU.purdue.jtb.parser.syntaxtree.NodeConstants.JTB_SIG_TYPE;
import static EDU.purdue.jtb.parser.syntaxtree.NodeConstants.JTB_SIG_TYPEARGUMENT;
import static EDU.purdue.jtb.parser.syntaxtree.NodeConstants.JTB_SIG_TYPEARGUMENTS;
import static EDU.purdue.jtb.parser.syntaxtree.NodeConstants.JTB_SIG_WILDCARDBOUNDS;
import static EDU.purdue.jtb.parser.syntaxtree.NodeConstants.JTB_USER_BNFPRODUCTION;
import static EDU.purdue.jtb.parser.syntaxtree.NodeConstants.JTB_USER_CLASSORINTERFACETYPE;
import static EDU.purdue.jtb.parser.syntaxtree.NodeConstants.JTB_USER_COMPILATIONUNIT;
import static EDU.purdue.jtb.parser.syntaxtree.NodeConstants.JTB_USER_COMPLEXREGULAREXPRESSION;
import static EDU.purdue.jtb.parser.syntaxtree.NodeConstants.JTB_USER_COMPLEXREGULAREXPRESSIONCHOICES;
import static EDU.purdue.jtb.parser.syntaxtree.NodeConstants.JTB_USER_COMPLEXREGULAREXPRESSIONUNIT;
import static EDU.purdue.jtb.parser.syntaxtree.NodeConstants.JTB_USER_EXPANSION;
import static EDU.purdue.jtb.parser.syntaxtree.NodeConstants.JTB_USER_EXPANSIONCHOICES;
import static EDU.purdue.jtb.parser.syntaxtree.NodeConstants.JTB_USER_EXPANSIONUNIT;
import static EDU.purdue.jtb.parser.syntaxtree.NodeConstants.JTB_USER_EXPANSIONUNITTCF;
import static EDU.purdue.jtb.parser.syntaxtree.NodeConstants.JTB_USER_IDENTIFIERASSTRING;
import static EDU.purdue.jtb.parser.syntaxtree.NodeConstants.JTB_USER_JAVACCINPUT;
import static EDU.purdue.jtb.parser.syntaxtree.NodeConstants.JTB_USER_JAVACODEPRODUCTION;
import static EDU.purdue.jtb.parser.syntaxtree.NodeConstants.JTB_USER_PRIMITIVETYPE;
import static EDU.purdue.jtb.parser.syntaxtree.NodeConstants.JTB_USER_PRODUCTION;
import static EDU.purdue.jtb.parser.syntaxtree.NodeConstants.JTB_USER_REFERENCETYPE;
import static EDU.purdue.jtb.parser.syntaxtree.NodeConstants.JTB_USER_REGEXPRSPEC;
import static EDU.purdue.jtb.parser.syntaxtree.NodeConstants.JTB_USER_REGULAREXPRESSION;
import static EDU.purdue.jtb.parser.syntaxtree.NodeConstants.JTB_USER_REGULAREXPRPRODUCTION;
import static EDU.purdue.jtb.parser.syntaxtree.NodeConstants.JTB_USER_RESULTTYPE;
import static EDU.purdue.jtb.parser.syntaxtree.NodeConstants.JTB_USER_STRINGLITERAL;
import static EDU.purdue.jtb.parser.syntaxtree.NodeConstants.JTB_USER_TYPE;
import static EDU.purdue.jtb.parser.syntaxtree.NodeConstants.JTB_USER_TYPEARGUMENT;
import static EDU.purdue.jtb.parser.syntaxtree.NodeConstants.JTB_USER_TYPEARGUMENTS;
import static EDU.purdue.jtb.parser.syntaxtree.NodeConstants.JTB_USER_WILDCARDBOUNDS;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import EDU.purdue.jtb.common.JTBOptions;
import EDU.purdue.jtb.common.Messages;
import EDU.purdue.jtb.common.ProgrammaticError;
import EDU.purdue.jtb.parser.syntaxtree.BNFProduction;
import EDU.purdue.jtb.parser.syntaxtree.ClassOrInterfaceType;
import EDU.purdue.jtb.parser.syntaxtree.CompilationUnit;
import EDU.purdue.jtb.parser.syntaxtree.ComplexRegularExpression;
import EDU.purdue.jtb.parser.syntaxtree.ComplexRegularExpressionChoices;
import EDU.purdue.jtb.parser.syntaxtree.ComplexRegularExpressionUnit;
import EDU.purdue.jtb.parser.syntaxtree.Expansion;
import EDU.purdue.jtb.parser.syntaxtree.ExpansionChoices;
import EDU.purdue.jtb.parser.syntaxtree.ExpansionUnit;
import EDU.purdue.jtb.parser.syntaxtree.ExpansionUnitTCF;
import EDU.purdue.jtb.parser.syntaxtree.INode;
import EDU.purdue.jtb.parser.syntaxtree.IdentifierAsString;
import EDU.purdue.jtb.parser.syntaxtree.JavaCCInput;
import EDU.purdue.jtb.parser.syntaxtree.JavaCodeProduction;
import EDU.purdue.jtb.parser.syntaxtree.JavaIdentifier;
import EDU.purdue.jtb.parser.syntaxtree.NodeChoice;
import EDU.purdue.jtb.parser.syntaxtree.NodeList;
import EDU.purdue.jtb.parser.syntaxtree.NodeListOptional;
import EDU.purdue.jtb.parser.syntaxtree.NodeOptional;
import EDU.purdue.jtb.parser.syntaxtree.NodeSequence;
import EDU.purdue.jtb.parser.Token;
import EDU.purdue.jtb.parser.syntaxtree.PackageDeclaration;
import EDU.purdue.jtb.parser.syntaxtree.PrimitiveType;
import EDU.purdue.jtb.parser.syntaxtree.Production;
import EDU.purdue.jtb.parser.syntaxtree.ReferenceType;
import EDU.purdue.jtb.parser.syntaxtree.RegExprSpec;
import EDU.purdue.jtb.parser.syntaxtree.RegularExprProduction;
import EDU.purdue.jtb.parser.syntaxtree.RegularExpression;
import EDU.purdue.jtb.parser.syntaxtree.ResultType;
import EDU.purdue.jtb.parser.syntaxtree.StringLiteral;
import EDU.purdue.jtb.parser.syntaxtree.Type;
import EDU.purdue.jtb.parser.syntaxtree.TypeArgument;
import EDU.purdue.jtb.parser.syntaxtree.TypeArguments;
import EDU.purdue.jtb.parser.syntaxtree.WildcardBounds;
import EDU.purdue.jtb.parser.visitor.DepthFirstIntVisitor;
import EDU.purdue.jtb.parser.visitor.DepthFirstVoidVisitor;
import EDU.purdue.jtb.parser.visitor.signature.NodeFieldsSignature;

/**
 * The {@link GlobalDataBuilder} visitor performs, at the beginning of the JTB processing, some error checking
 * and builds and stores objects needed by other classes:
 * <ul>
 * <li>a HashMap ({@link #notTbcNodesHM}) of JavaCodeProductions whose nodes must be created ("%" syntax) and
 * of BNFProductions whose nodes must not be created ("!" syntax),</li>
 * <li>a HashMap ({@link #prodHM}) of all JavaCodeProductions and BNFProductions identifiers and their result
 * type,</li>
 * <li>a list ({@link #retVarInfo}) of return variables declarations (for all non "void" JavaCodeProductions
 * for which the node creation has been asked and BNFProductions for which the node creation has not been
 * forbidden)</li>
 * <li>a HashMap ({@link #tokenHM}) of tokens which have a constant regular expression, e.g. < PLUS : "+" >,
 * which will be used to generate a default constructor,</li>
 * <li>a HashMap ({@link #nbSubNodesTbcHM}) of
 * ({@link ExpansionChoices}/{@link Expansion}/{@link ExpansionUnit}) nodes with their number of sub-nodes to
 * be created.</li>
 * </ul>
 * <p>
 * This visitor is supposed to be run once and not supposed to be run in parallel threads (on the same
 * grammar).
 * </p>
 * TESTCASE some to add
 *
 * @version 1.4.7 : 09/2012 : MMa : created from the old GlobalDataFinder in Annotator and TokenTableBuilder.
 * @version 1.4.8 : 10/2012 : MMa : added JavaCodeProduction class generation if requested ; modified error
 *          checking and messages
 * @version 1.5.0 : 01-06/2017 : MMa : changed some iterator based for loops to enhanced for loops ; fixed
 *          processing of nodes not to be created<br>
 * @version 1.5.1 : 08/2023 : MMa : editing changes for coverage analysis; changes due to the NodeToken
 *          replacement by Token
 */
public class GlobalDataBuilder extends DepthFirstVoidVisitor {
  
  /** The global JTB options (not thread safe but used only in read-access) */
  public final JTBOptions jopt;
  
  /** The {@link SubNodesToBeCreatedCounter} visitor */
  private final SubNodesToBeCreatedCounter sntbccv;
  
  /** The parser name */
  public String parserName;
  
  /** The parser's package name (from the grammar or the command line) */
  public String packageName;
  
  /**
   * The map of nodes which must not be created : JavaCodeProductions with no "%" indicator and BNFProductions
   * with "!" indicator<br>
   * key = identifier, value = {@link #JC_IND} for JavaCodeProductions or {@link #BNF_IND} for BNFProduction
   */
  private final Map<String, String> notTbcNodesHM = new HashMap<>();
  
  /**
   * The map of all nodes : JavaCodeProductions and BNFProductions<br>
   * key = identifier, value = {@link #JC_IND} or {@link #BNF_IND} + ResultType (which can be an array)
   */
  private final Map<String, String> prodHM = new HashMap<>(100);
  
  /** The map of nodes with their number of sub-nodes to be created */
  final Map<INode, Integer> nbSubNodesTbcHM = new HashMap<>(100);
  
  /**
   * The indicator for JavaCodeProduction in the {@link #notTbcNodesHM} and {@link #prodHM} tables
   */
  public static final String JC_IND = "/";
  
  /** The indicator for BNFProduction in the {@link #notTbcNodesHM} and {@link #prodHM} tables */
  public static final String BNF_IND = "&";
  
  /**
   * The list of all return variables information (for all non "void" JavaCodeProductions and BNFProductions
   * for which the node creation has not been forbidden)
   */
  private final List<RetVarInfo> retVarInfo = new ArrayList<>();
  
  /**
   * The map of tokens (key = token name, value = regular expression or {@link #DONT_CREATE} for tokens not to
   * be created as Token nodes)
   */
  private final Map<String, String> tokenHM = new HashMap<>();
  
  /** The current token's name */
  private String tokenName = "";
  
  /**
   * The current token's regular expression ; set to {@link #DONT_CREATE} for a Token node not to be created
   */
  private String regExpr = "";
  
  /** The specific regular expression for a token node not to be created */
  public static final String DONT_CREATE = "!";
  
  /** True to tell to create a node from RegExprSpec, false otherwise */
  private boolean cnfres = true;
  
  /** True for first pass, false for the second */
  private boolean firstPass = true;
  
  /** The JavaCodeProduction or BNFProduction result type */
  private String resultType;
  
  /**
   * Constructor.
   *
   * @param aJopt - the JTB options
   */
  public GlobalDataBuilder(final JTBOptions aJopt) {
    super();
    jopt = aJopt;
    sntbccv = new SubNodesToBeCreatedCounter(this);
  }
  
  /**
   * The map caching the fixed class names
   */
  private final Map<String, String> fnMap = new HashMap<>();
  
  /**
   * Builds a (class) name with the default prefix and/or suffix, except for the base (class) names.
   *
   * @param aName - string to prefix or suffix
   * @return the prefixed and/or suffixed name
   */
  public String getFixedName(final String aName) {
    // see if no prefix and no suffix
    if (!jopt.isPfxOrSfx) {
      return aName;
    }
    
    // see in cache if already computed
    String fn = fnMap.get(aName);
    if (fn != null) {
      return fn;
    }
    
    // see if base class or node not to be created: no prefix / suffix for them CODEJAVA ?
    if (aName.equals(jjToken) //
        || aName.equals(nodeToken) //
        || aName.equals(nodeChoice) //
        || aName.equals(nodeList) //
        || aName.equals(nodeListOptional) //
        || aName.equals(nodeSequence) //
        || aName.equals(nodeOptional) //
        || getNotTbcNodesHM().containsKey(aName)) {
      fnMap.put(aName, aName);
      return aName;
    }
    
    // for others : add prefix / suffix and put the result in the cache
    final int len = aName.length() + jopt.nodePrefix.length() + jopt.nodeSuffix.length();
    final StringBuilder sb = new StringBuilder(len);
    sb.append(jopt.nodePrefix);
    sb.append(aName);
    sb.append(jopt.nodeSuffix);
    fn = sb.toString();
    fnMap.put(aName, fn);
    return fn;
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
   * f11 -> < EOF ><br>
   * s: 1465207473<br>
   *
   * @param n - the node to visit
   */
  @Override
  @NodeFieldsSignature({
      1465207473, JTB_SIG_JAVACCINPUT, JTB_USER_JAVACCINPUT
  })
  public void visit(final JavaCCInput n) {
    // retrieve parser name
    parserName = n.f3.f0.image;
    // visit CompilationUnit for package name
    n.f5.accept(this);
    // visit Productions
    // f10 -> ( Production() )+
    // first pass
    for (final INode e : n.f10.nodes) {
      e.accept(this);
    }
    firstPass = false;
    // second pass
    for (final INode e : n.f10.nodes) {
      e.accept(this);
    }
  }
  
  /**
   * Visits a {@link Production} node, whose child is the following :
   * <p>
   * f0 -> . %0 JavaCodeProduction()<br>
   * .. .. | %1 RegularExprProduction()<br>
   * .. .. | %2 TokenManagerDecls()<br>
   * .. .. | %3 BNFProduction()<br>
   * s: -120615333<br>
   *
   * @param n - the node to visit
   */
  @Override
  @NodeFieldsSignature({
      -120615333, JTB_SIG_PRODUCTION, JTB_USER_PRODUCTION
  })
  public void visit(final Production n) {
    // do not visit TokenManagerDecls
    if (n.f0.which != 2) {
      n.f0.accept(this);
    }
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
   * s: -763138104<br>
   *
   * @param n - the node to visit
   */
  @Override
  @NodeFieldsSignature({
      -763138104, JTB_SIG_JAVACODEPRODUCTION, JTB_USER_JAVACODEPRODUCTION
  })
  public void visit(final JavaCodeProduction n) {
    // f3 -> IdentifierAsString()
    final String ident = n.f3.f0.image;
    if (firstPass) {
      // controls and global data
      if (prodHM.containsKey(ident)) {
        jopt.mess.softErr(
            "This JavaCodeProduction has the same name '" + ident
                + "' as another BNFProduction or JavaCodeProduction.",
            n.f3.f0.beginLine, n.f3.f0.beginColumn);
      } else if (ident.equals(iNode) //
          || ident.equals(iNodeList) //
          || ident.equals(nodeList) //
          || ident.equals(nodeListOptional) //
          || ident.equals(nodeOptional) //
          || ident.equals(nodeSequence) //
          || ident.equals(nodeToken) //
          || ident.equals(nodeChoice)) {
        jopt.mess.softErr("JavaCodeProduction '" + ident + "()' has the same name as a JTB generated class.",
            n.f3.f0.beginLine, n.f3.f0.beginColumn);
      } else {
        // f2 -> ResultType()
        resultType = "";
        n.f2.accept(this);
        if (!"void".equals(resultType)) {
          if (resultType.equals(ident)) {
            jopt.mess.softErr(
                "JavaCodeProduction '" + ident + "()' has a return type of the same name,"
                    + " that would conflict with the generated node class.",
                n.f3.f0.beginLine, n.f3.f0.beginColumn);
          }
        }
        prodHM.put(ident, JC_IND + resultType);
        // f6 -> [ "%" ]
        if (!n.f6.present()) {
          // add this node to the list of not created nodes
          notTbcNodesHM.put(ident, JC_IND);
        }
      }
    } else // return variable declaration
    if (n.f6.present()) {
      resultType = "";
      n.f2.accept(this);
      if (!"void".equals(resultType)) {
        if (prodHM.containsKey(resultType)) {
          jopt.mess.softErr(
              "This JavaCodeProduction '" + ident + "'has a ResultType '" + resultType
                  + "' of the same name as another BNFProduction or JavaCodeProduction.",
              n.f3.f0.beginLine, n.f3.f0.beginColumn);
        } else {
          // store return variable information
          retVarInfo.add(new RetVarInfo("JavaCodeProduction", ident, resultType));
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
   * s: 1323482450<br>
   *
   * @param n - the node to visit
   */
  @Override
  @NodeFieldsSignature({
      1323482450, JTB_SIG_BNFPRODUCTION, JTB_USER_BNFPRODUCTION
  })
  public void visit(final BNFProduction n) {
    // f2 -> IdentifierAsString()
    final String ident = n.f2.f0.image;
    if (firstPass) {
      // controls and global data
      if (prodHM.containsKey(ident)) {
        jopt.mess.softErr(
            "This BNFProduction has the same name '" + ident
                + "' as another BNFProduction or JavaCodeProduction.",
            n.f2.f0.beginLine, n.f2.f0.beginColumn);
      } else if (ident.equals(iNode) //
          || ident.equals(iNodeList) //
          || ident.equals(nodeList) //
          || ident.equals(nodeListOptional) //
          || ident.equals(nodeOptional) //
          || ident.equals(nodeSequence) //
          || ident.equals(nodeToken) //
          || ident.equals(nodeChoice)) {
        jopt.mess.softErr("BNFProduction '" + ident + "()' has the same name as a JTB generated class.",
            n.f2.f0.beginLine, n.f2.f0.beginColumn);
      } else {
        // f1 -> ResultType()
        resultType = "";
        n.f1.accept(this);
        if (!"void".equals(resultType)) {
          if (resultType.equals(ident)) {
            jopt.mess.softErr(
                "BNFProduction '" + ident + "()' has a return type of the same name,"
                    + " that would conflict with the generated node class.",
                n.f2.f0.beginLine, n.f2.f0.beginColumn);
          }
        }
        prodHM.put(ident, BNF_IND + resultType);
        // f5 -> [ "!" ]
        if (n.f5.present()) {
          // add this node to the list of not created nodes
          notTbcNodesHM.put(ident, BNF_IND);
        }
      }
    } else {
      // second pass: return variable declaration
      if (!n.f5.present()) {
        resultType = "";
        n.f1.accept(this);
        if (!"void".equals(resultType)) {
          if (prodHM.containsKey(resultType)) {
            jopt.mess.softErr(
                "This BNFProduction '" + ident + "'has a ResultType '" + resultType
                    + "' of the same name as another BNFProduction or JavaCodeProduction.",
                n.f2.f0.beginLine, n.f2.f0.beginColumn);
          } else {
            // store return variable information
            retVarInfo.add(new RetVarInfo("BNFProduction", ident, resultType));
          }
        }
      }
      // f9 -> ExpansionChoices()
      sntbccv.visit(n.f9);
    }
  }
  
  /**
   * Visits a {@link CompilationUnit} node, whose children are the following :
   * <p>
   * f0 -> [ PackageDeclaration() ]<br>
   * f1 -> ( ImportDeclaration() )*<br>
   * f2 -> ( TypeDeclaration() )*<br>
   * s: 1761039264<br>
   *
   * @param n - the node to visit
   */
  @Override
  @NodeFieldsSignature({
      1761039264, JTB_SIG_COMPILATIONUNIT, JTB_USER_COMPILATIONUNIT
  })
  public void visit(final CompilationUnit n) {
    // f0 -> [ PackageDeclaration() ]
    final NodeOptional n0 = n.f0;
    if (n0.present()) {
      // from Name()
      String s;
      // f0 -> JavaIdentifier()
      final JavaIdentifier ji0 = ((PackageDeclaration) n0.node).f1.f0;
      s = ((Token) ji0.f0.choice).image;
      // f1 -> ( #0 "." #1 JavaIdentifier() )*
      final NodeListOptional nlo = ((PackageDeclaration) n0.node).f1.f1;
      if (nlo.present()) {
        for (int i = 0; i < nlo.size(); i++) {
          final INode nloeai = nlo.elementAt(i);
          final NodeSequence seq = (NodeSequence) nloeai;
          // #0 "."
          // final INode seq1 = seq.elementAt(0);
          // seq1.accept(this);
          s += ".";
          // #1 JavaIdentifier()
          final INode seq2 = seq.elementAt(1);
          // seq2.accept(this);
          final JavaIdentifier ji1 = (JavaIdentifier) seq2;
          s += ((Token) ji1.f0.choice).image;
        }
      }
      packageName = s;
    }
    // f1 -> ( ImportDeclaration() )*
    // final NodeListOptional n1 = n.f1;
    // if (n1.present()) {
    // for (int i = 0; i < n1.size(); i++) {
    // final INode nloeai = n1.elementAt(i);
    // nloeai.accept(this);
    // }
    // }
    // f2 -> ( TypeDeclaration() )*
    // final NodeListOptional n2 = n.f2;
    // if (n2.present()) {
    // for (int i = 0; i < n2.size(); i++) {
    // final INode nloeai = n2.elementAt(i);
    // nloeai.accept(this);
    // }
    // }
  }
  
  /**
   * Visits a {@link ResultType} node, whose child is the following :
   * <p>
   * f0 -> ( %0 "void"<br>
   * .. .. | %1 Type() )<br>
   * s: 805291204<br>
   *
   * @param n - the node to visit
   */
  @Override
  @NodeFieldsSignature({
      805291204, JTB_SIG_RESULTTYPE, JTB_USER_RESULTTYPE
  })
  public void visit(final ResultType n) {
    switch (n.f0.which) {
    case 0:
      // %0 "void"
      resultType = "void";
      break;
    case 1:
      // %1 Type()
      n.f0.choice.accept(this);
      break;
    default:
      // should not occur !!!
      throw new ShouldNotOccurException(n.f0);
    }
  }
  
  /**
   * Visits a {@link Type} node, whose child is the following :
   * <p>
   * f0 -> . %0 ReferenceType()<br>
   * .. .. | %1 PrimitiveType()<br>
   * s: -1143267570<br>
   *
   * @param n - the node to visit
   */
  @Override
  @NodeFieldsSignature({
      -1143267570, JTB_SIG_TYPE, JTB_USER_TYPE
  })
  public void visit(final Type n) {
    // f0 -> . %0 ReferenceType()
    // .. .. | %1 PrimitiveType()
    switch (n.f0.which) {
    case 0:
      // %0 ReferenceType()
      n.f0.choice.accept(this);
      break;
    case 1:
      // %1 PrimitiveType()
      n.f0.choice.accept(this);
      break;
    default:
      // should not occur !!!
      throw new ShouldNotOccurException(n.f0);
    }
  }
  
  /**
   * Visits a {@link ReferenceType} node, whose child is the following :
   * <p>
   * f0 -> . %0 #0 PrimitiveType()<br>
   * .. .. . .. #1 ( $0 "[" $1 "]" )+<br>
   * .. .. | %1 #0 ClassOrInterfaceType()<br>
   * .. .. . .. #1 ( $0 "[" $1 "]" )*<br>
   * s: -275468366<br>
   *
   * @param n - the node to visit
   */
  @Override
  @NodeFieldsSignature({
      -275468366, JTB_SIG_REFERENCETYPE, JTB_USER_REFERENCETYPE
  })
  public void visit(final ReferenceType n) {
    switch (n.f0.which) {
    case 0:
      // %0 #0 PrimitiveType()
      // .. #1 ( $0 "[" $1 "]" )+
      final NodeSequence seq = (NodeSequence) n.f0.choice;
      final INode seq1 = seq.elementAt(0);
      seq1.accept(this);
      final NodeList lst = (NodeList) seq.elementAt(1);
      for (int i = 0; i < lst.size(); i++) {
        resultType = resultType + "[]";
      }
      break;
    case 1:
      // %1 #0 ClassOrInterfaceType()
      // .. #1 ( $0 "[" $1 "]" )*
      final NodeSequence seq2 = (NodeSequence) n.f0.choice;
      seq2.elementAt(0).accept(this);
      final NodeListOptional nlo = (NodeListOptional) seq2.elementAt(1);
      if (nlo.present()) {
        for (int i = 0; i < nlo.size(); i++) {
          resultType = resultType + "[]";
        }
      }
      break;
    default:
      // should not occur !!!
      throw new ShouldNotOccurException(n.f0);
    }
  }
  
  /**
   * Visits a {@link PrimitiveType} node, whose child is the following :
   * <p>
   * f0 -> . %0 "boolean"<br>
   * .. .. | %1 "char"<br>
   * .. .. | %2 "byte"<br>
   * .. .. | %3 "short"<br>
   * .. .. | %4 "int"<br>
   * .. .. | %5 "long"<br>
   * .. .. | %6 "float"<br>
   * .. .. | %7 "double"<br>
   * s: 427914477<br>
   *
   * @param n - the node to visit
   */
  @Override
  @NodeFieldsSignature({
      427914477, JTB_SIG_PRIMITIVETYPE, JTB_USER_PRIMITIVETYPE
  })
  public void visit(final PrimitiveType n) {
    resultType = resultType + ((Token) n.f0.choice).image;
  }
  
  /**
   * Visits a {@link ClassOrInterfaceType} node, whose children are the following :
   * <p>
   * f0 -> < IDENTIFIER ><br>
   * f1 -> [ TypeArguments() ]<br>
   * f2 -> ( #0 "." #1 < IDENTIFIER ><br>
   * .. .. . #2 [ TypeArguments() ] )*<br>
   * s: -1178309727<br>
   *
   * @param n - the node to visit
   */
  @Override
  @NodeFieldsSignature({
      -1178309727, JTB_SIG_CLASSORINTERFACETYPE, JTB_USER_CLASSORINTERFACETYPE
  })
  public void visit(final ClassOrInterfaceType n) {
    resultType = resultType + n.f0.image;
    // f1 -> [ TypeArguments() ]
    final NodeOptional n1 = n.f1;
    if (n1.present()) {
      n1.accept(this);
    }
    // f2 -> ( #0 "." #1 < IDENTIFIER >
    // .. .. . #2 [ TypeArguments() ] )*
    final NodeListOptional n2 = n.f2;
    if (n2.present()) {
      for (int i = 0; i < n2.size(); i++) {
        final NodeSequence seq = (NodeSequence) n2.elementAt(i);
        // #0 "."
        resultType = resultType + ".";
        // #1 < IDENTIFIER >
        final INode seq2 = seq.elementAt(1);
        seq2.accept(this);
        resultType = resultType + ((Token) seq2).image;
        final NodeOptional opt = (NodeOptional) seq.elementAt(2);
        if (opt.present()) {
          opt.accept(this);
        }
      }
    }
  }
  
  /**
   * Visits a {@link TypeArguments} node, whose children are the following :
   * <p>
   * f0 -> "<"<br>
   * f1 -> TypeArgument()<br>
   * f2 -> ( #0 "," #1 TypeArgument() )*<br>
   * f3 -> ">"<br>
   * s: 131755052<br>
   *
   * @param n - the node to visit
   */
  @Override
  @NodeFieldsSignature({
      131755052, JTB_SIG_TYPEARGUMENTS, JTB_USER_TYPEARGUMENTS
  })
  public void visit(final TypeArguments n) {
    // f0 -> "<"
    resultType = resultType + "<";
    n.f1.accept(this);
    // f2 -> ( #0 "," #1 TypeArgument() )*
    final NodeListOptional n2 = n.f2;
    if (n2.present()) {
      for (int i = 0; i < n2.size(); i++) {
        final NodeSequence seq = (NodeSequence) n2.elementAt(i);
        // #0 ","
        resultType = resultType + ", ";
        // #1 TypeArgument()
        seq.elementAt(1).accept(this);
      }
    }
    // f3 -> ">"
    resultType = resultType + ">";
  }
  
  /**
   * Visits a {@link TypeArgument} node, whose child is the following :
   * <p>
   * f0 -> . %0 ReferenceType()<br>
   * .. .. | %1 #0 "?"<br>
   * .. .. . .. #1 [ WildcardBounds() ]<br>
   * s: 36461692<br>
   *
   * @param n - the node to visit
   */
  @Override
  @NodeFieldsSignature({
      36461692, JTB_SIG_TYPEARGUMENT, JTB_USER_TYPEARGUMENT
  })
  public void visit(final TypeArgument n) {
    // f0 -> . %0 ReferenceType()
    // .. .. | %1 #0 "?"
    // .. .. . .. #1 [ WildcardBounds() ]
    final NodeChoice nch = n.f0;
    final INode ich = nch.choice;
    switch (nch.which) {
    case 0:
      // %0 ReferenceType()
      ich.accept(this);
      break;
    case 1:
      // #0 "?"
      resultType = resultType + "?";
      // #1 [ WildcardBounds() ]
      final NodeOptional opt = (NodeOptional) ((NodeSequence) ich).elementAt(1);
      if (opt.present()) {
        opt.accept(this);
      }
      break;
    default:
      // should not occur !!!
      throw new ShouldNotOccurException(nch);
    }
  }
  
  /**
   * Visits a {@link WildcardBounds} node, whose child is the following :
   * <p>
   * f0 -> . %0 #0 "extends" #1 ReferenceType()<br>
   * .. .. | %1 #0 "super" #1 ReferenceType()<br>
   * s: 122808000<br>
   *
   * @param n - the node to visit
   */
  @Override
  @NodeFieldsSignature({
      122808000, JTB_SIG_WILDCARDBOUNDS, JTB_USER_WILDCARDBOUNDS
  })
  public void visit(final WildcardBounds n) {
    // f0 -> . %0 #0 "extends" #1 ReferenceType()
    // .. .. | %1 #0 "super" #1 ReferenceType()
    final NodeChoice nch = n.f0;
    switch (nch.which) {
    case 0:
      // %0 #0 "extends" #1 ReferenceType()
      // #0 "extends"
      resultType = resultType + " extends ";
      // #1 ReferenceType()
      ((NodeSequence) nch.choice).elementAt(1).accept(this);
      break;
    case 1:
      // %1 #0 "super" #1 ReferenceType()
      // #0 "super"
      resultType = resultType + " super ";
      // #1 ReferenceType()
      ((NodeSequence) nch.choice).elementAt(1).accept(this);
      break;
    default:
      // should not occur !!!
      throw new ShouldNotOccurException(nch);
    }
  }
  
  /**
   * Visits a {@link RegularExprProduction} node, whose children are the following :
   * <p>
   * f0 -> [ %0 #0 "<" #1 "*" #2 ">"<br>
   * .. .. | %1 #0 "<" #1 < IDENTIFIER ><br>
   * .. .. . .. #2 ( $0 "," $1 < IDENTIFIER > )*<br>
   * .. .. . .. #3 ">" ]<br>
   * f1 -> RegExprKind()<br>
   * f2 -> [ #0 "[" #1 "IGNORE_CASE" #2 "]" ]<br>
   * f3 -> ":"<br>
   * f4 -> "{"<br>
   * f5 -> RegExprSpec()<br>
   * f6 -> ( #0 "|" #1 RegExprSpec() )*<br>
   * f7 -> "}"<br>
   * s: 484788342<br>
   *
   * @param n - the node to visit
   */
  @Override
  @NodeFieldsSignature({
      484788342, JTB_SIG_REGULAREXPRPRODUCTION, JTB_USER_REGULAREXPRPRODUCTION
  })
  public void visit(final RegularExprProduction n) {
    // f5 -> RegExprSpec()
    n.f5.accept(this);
    // f6 -> ( #0 "|" #1 RegExprSpec() )*
    if (n.f6.present()) {
      for (final INode e : n.f6.nodes) {
        // #1 RegExprSpec()
        ((NodeSequence) e).elementAt(1).accept(this);
      }
    }
  }
  
  /**
   * Visits a {@link RegExprSpec} node, whose children are the following :
   * <p>
   * f0 -> RegularExpression()<br>
   * f1 -> [ "!" ]<br>
   * f2 -> [ Block() ]<br>
   * f3 -> [ #0 ":" #1 < IDENTIFIER > ]<br>
   * s: -1949948808<br>
   *
   * @param n - the node to visit
   */
  @Override
  @NodeFieldsSignature({
      -1949948808, JTB_SIG_REGEXPRSPEC, JTB_USER_REGEXPRSPEC
  })
  public void visit(final RegExprSpec n) {
    // f1 -> [ "!" ]
    // tell lower levels whether to create a node or not
    cnfres = !n.f1.present();
    // visit only f0 -> RegularExpression()
    n.f0.accept(this);
  }
  
  /**
   * Visits a {@link RegularExpression} node, whose child is the following :
   * <p>
   * f0 -> . %0 StringLiteral()<br>
   * .. .. | %1 #0 "<"<br>
   * .. .. . .. #1 [ $0 [ "#" ]<br>
   * .. .. . .. .. . $1 IdentifierAsString() $2 ":" ]<br>
   * .. .. . .. #2 ComplexRegularExpressionChoices() #3 ">"<br>
   * .. .. | %2 #0 "<" #1 IdentifierAsString() #2 ">"<br>
   * .. .. | %3 #0 "<" #1 "EOF" #2 ">"<br>
   * s: 1719627151<br>
   *
   * @param n - the node to visit
   */
  @Override
  @NodeFieldsSignature({
      1719627151, JTB_SIG_REGULAREXPRESSION, JTB_USER_REGULAREXPRESSION
  })
  public void visit(final RegularExpression n) {
    // TODO voir le not create pour tous les cas eu5
    if (n.f0.which == 1) {
      // %1 #0 "<"
      // .. #1 [ $0 [ "#" ] $1 IdentifierAsString() $2 ":" ]
      // .. #2 ComplexRegularExpressionChoices() #3 ">"
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
          tokenHM.put(tokenName, regExpr);
        } else {
          tokenHM.put(tokenName, DONT_CREATE);
        }
        // reset for next pass
        tokenName = "";
        regExpr = "";
      }
    }
  }
  
  /**
   * Visits a {@link ComplexRegularExpressionChoices} node, whose children are the following :
   * <p>
   * f0 -> ComplexRegularExpression()<br>
   * f1 -> ( #0 "|" #1 ComplexRegularExpression() )*<br>
   * s: -1240933595<br>
   *
   * @param n - the node to visit
   */
  @Override
  @NodeFieldsSignature({
      -1240933595, JTB_SIG_COMPLEXREGULAREXPRESSIONCHOICES, JTB_USER_COMPLEXREGULAREXPRESSIONCHOICES
  })
  public void visit(final ComplexRegularExpressionChoices n) {
    if (n.f1.present()) {
      // if f1 -> ( #0 "|" #1 ComplexRegularExpression() )* is present, this isn't a constant regexpr
      regExpr = "";
    } else {
      // f0 -> ComplexRegularExpression()
      n.f0.accept(this);
    }
  }
  
  /**
   * Visits a {@link ComplexRegularExpression} node, whose child is the following :
   * <p>
   * f0 -> ( ComplexRegularExpressionUnit() )+<br>
   * s: 896313544<br>
   *
   * @param n - the node to visit
   */
  @Override
  @NodeFieldsSignature({
      896313544, JTB_SIG_COMPLEXREGULAREXPRESSION, JTB_USER_COMPLEXREGULAREXPRESSION
  })
  public void visit(final ComplexRegularExpression n) {
    // f0 -> ( ComplexRegularExpressionUnit() )+
    final NodeList n0 = n.f0;
    for (int i = 0; i < n0.size(); i++) {
      final INode lsteai = n0.elementAt(i);
      lsteai.accept(this);
    }
  }
  
  /**
   * Visits a {@link ComplexRegularExpressionUnit} node, whose child is the following :
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
   * s: -1507427530<br>
   *
   * @param n - the node to visit
   */
  @Override
  @NodeFieldsSignature({
      -1507427530, JTB_SIG_COMPLEXREGULAREXPRESSIONUNIT, JTB_USER_COMPLEXREGULAREXPRESSIONUNIT
  })
  public void visit(final ComplexRegularExpressionUnit n) {
    if (n.f0.which == 0) {
      // %0 StringLiteral()
      n.f0.accept(this);
    } else {
      // others
      regExpr = "";
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
  @NodeFieldsSignature({
      -1580059612, JTB_SIG_IDENTIFIERASSTRING, JTB_USER_IDENTIFIERASSTRING
  })
  public void visit(final IdentifierAsString n) {
    tokenName = n.f0.image;
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
  @NodeFieldsSignature({
      241433948, JTB_SIG_STRINGLITERAL, JTB_USER_STRINGLITERAL
  })
  public void visit(final StringLiteral n) {
    regExpr = n.f0.image;
  }
  
  /*
   * Getters
   */
  
  /**
   * @return the table of nodes which must not be created : JavaCodeProductions with no "%" indicator and
   *         BNFProductions with "!" indicator
   */
  public final Map<String, String> getNotTbcNodesHM() {
    return notTbcNodesHM;
  }
  
  /**
   * @return the table of all BNFProductions and JavaCodeProductions
   */
  public final Map<String, String> getProdHM() {
    return prodHM;
  }
  
  /**
   * @return the map of nodes with their number of sub-nodes to be created
   */
  public final Map<INode, Integer> getNbSubNodesTbcHM() {
    return nbSubNodesTbcHM;
  }
  
  /**
   * @return the list of all return variables information
   */
  public final List<RetVarInfo> getRetVarInfo() {
    return retVarInfo;
  }
  
  /**
   * @return * The map of tokens (key = token name, value = regular expression or {@link #DONT_CREATE} for
   *         tokens not to be created as Token nodes)
   */
  public Map<String, String> getTokenHM() {
    return tokenHM;
  }
  
  /**
   * Returns the count of the nodes to be created below a an {@link ExpansionChoices}.
   *
   * @param n - an {@link ExpansionChoices}
   * @return the number of nodes to be created
   */
  public int getNbSubNodesTbc(final ExpansionChoices n) {
    final Integer res = nbSubNodesTbcHM.get(n);
    if (res == null) {
      throw new InvalidCountException(n);
    }
    return res.intValue();
  }
  
  /**
   * Returns the count of the nodes to be created below a an {@link Expansion}.
   *
   * @param n - an {@link Expansion}
   * @return the number of nodes to be created
   */
  public int getNbSubNodesTbc(final Expansion n) {
    final Integer res = nbSubNodesTbcHM.get(n);
    if (res == null) {
      throw new InvalidCountException(n);
    }
    return res.intValue();
  }
  
  /**
   * Returns the count of the nodes to be created below a an {@link ExpansionUnit}.
   *
   * @param n - an {@link ExpansionUnit}
   * @return the number of nodes to be created
   */
  public int getNbSubNodesTbc(final ExpansionUnit n) {
    final Integer res = nbSubNodesTbcHM.get(n);
    if (res == null) {
      throw new InvalidCountException(n);
    }
    return res.intValue();
  }
  
  /**
   * Return variable information.
   *
   * @author Marc Mazas
   * @version 1.5.0 : 03/2017 : MMa : created after moving generation code from {@link GlobalDataBuilder} to
   *          annotator.
   */
  public class RetVarInfo {
    
    /** The production (JavaCodeProduction or BNFProduction) */
    public final String production;
    /** The identifier */
    public final String ident;
    /** The return type */
    public final String type;
    
    /**
     * Standard constructor.
     *
     * @param aProduction - the origin
     * @param aIdent - the identifier
     * @param aType - the return type
     */
    RetVarInfo(final String aProduction, final String aIdent, final String aType) {
      production = aProduction;
      ident = aIdent;
      type = aType;
    }
  }
  
  /**
   * The {@link SubNodesToBeCreatedCounter} visitor walks down an {@link ExpansionChoices} or an
   * {@link Expansion} or an {@link ExpansionUnit} and tells how many nodes must be created.<br>
   * This is used to create the appropriate base nodes which can disappear or change when nodes are indicated
   * locally or globally not to be created.
   *
   * @author Marc Mazas
   * @version 1.5.0 : 02-06/2017 : MMa : created ; added final in ExpansionUnitTCF's catch
   */
  private class SubNodesToBeCreatedCounter extends DepthFirstIntVisitor {
    
    /** The {@link GlobalDataBuilder} visitor */
    private final GlobalDataBuilder gdbv;
    
    /**
     * Constructor.
     *
     * @param Agdbv - The {@link GlobalDataBuilder} visitor to use
     */
    SubNodesToBeCreatedCounter(final GlobalDataBuilder Agdbv) {
      gdbv = Agdbv;
    }
    
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
    @NodeFieldsSignature({
        -1726831935, JTB_SIG_EXPANSIONCHOICES, JTB_USER_EXPANSIONCHOICES
    })
    public int visit(final ExpansionChoices n) {
      
      Integer res = nbSubNodesTbcHM.get(n);
      if (res != null) {
        return res.intValue();
      }
      
      int nRes = 0;
      // f0 -> Expansion()
      if (n.f0.accept(this) > 0) {
        nRes++;
      }
      // f1 -> ( #0 "|" #1 Expansion() )*
      final NodeListOptional n1 = n.f1;
      if (n1.present()) {
        for (int i = 0; i < n1.size(); i++) {
          final NodeSequence seq = (NodeSequence) n1.elementAt(i);
          // #1 Expansion()
          if (seq.elementAt(1).accept(this) > 0) {
            nRes++;
          }
        }
      }
      res = Integer.valueOf(nRes);
      nbSubNodesTbcHM.put(n, res);
      return nRes;
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
    @NodeFieldsSignature({
        -2134365682, JTB_SIG_EXPANSION, JTB_USER_EXPANSION
    })
    public int visit(final Expansion n) {
      
      Integer res = nbSubNodesTbcHM.get(n);
      if (res != null) {
        return res.intValue();
      }
      
      int nRes = 0;
      
      // f0 -> ( #0 "LOOKAHEAD" #1 "(" #2 LocalLookahead() #3 ")" )?<
      // we do not create trees for the ExpansionChoices in a LocalLookahead()
      
      // f1 -> ( ExpansionUnit() )+
      final NodeList n1 = n.f1;
      for (int i = 0; i < n1.size(); i++) {
        if (n1.elementAt(i).accept(this) > 0) {
          nRes++;
        }
      }
      res = Integer.valueOf(nRes);
      nbSubNodesTbcHM.put(n, res);
      return nRes;
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
    @NodeFieldsSignature({
        1116287061, JTB_SIG_EXPANSIONUNIT, JTB_USER_EXPANSIONUNIT
    })
    public int visit(final ExpansionUnit n) {
      
      Integer res = nbSubNodesTbcHM.get(n);
      if (res != null) {
        return res.intValue();
      }
      
      int nRes = 0;
      final NodeChoice nch = n.f0;
      final INode ich = nch.choice;
      switch (nch.which) {
      case 0:
        // we do not create trees for the ExpansionChoices in a LocalLookahead()
        nRes = 0;
        break;
      case 1:
        // we do not create trees for blocks
        nRes = 0;
        break;
      case 2:
        // %2 #0 "[" #1 ExpansionChoices() #2 "]"
        // #1 ExpansionChoices()
        nRes = ((NodeSequence) ich).elementAt(1).accept(this) > 0 ? 1 : 0;
        break;
      case 3:
        // %3 ExpansionUnitTCF()
        nRes = ich.accept(this) > 0 ? 1 : 0;
        break;
      case 4:
        // %4 #0 [ $0 PrimaryExpression() $1 "=" ]
        // .. #1 ( &0 $0 IdentifierAsString() $1 Arguments()
        // .. .. .. $2 [ "!" ]
        // .. .. | &1 $0 RegularExpression()
        // .. .. .. $1 [ ?0 "." ?1 < IDENTIFIER > ]
        // .. .. .. $2 [ "!" ] )
        final NodeSequence seq = (NodeSequence) ich;
        // #1 ( &0 $0 IdentifierAsString() $1 Arguments()
        // .. .. $2 [ "!" ]
        // .. | &1 $0 RegularExpression()
        // .. .. $1 [ ?0 "." ?1 < IDENTIFIER > ]
        // .. .. $2 [ "!" ] )
        final NodeChoice nch1 = (NodeChoice) seq.elementAt(1);
        final INode ich1 = nch1.choice;
        switch (nch1.which) {
        case 0:
          // &0 $0 IdentifierAsString() $1 Arguments()
          // .. $2 [ "!" ]
          if (((NodeOptional) ((NodeSequence) ich1).elementAt(2)).present()) {
            nRes = 0;
            break;
          }
          final IdentifierAsString ias = (IdentifierAsString) ((NodeSequence) ich1).elementAt(0);
          if (gdbv.getNotTbcNodesHM().containsKey(ias.f0.image)) {
            nRes = 0;
            break;
          } else {
            nRes = 1;
            break;
          }
        case 1:
          // &1 $0 RegularExpression()
          // .. $1 [ ?0 "." ?1 < IDENTIFIER > ]
          // .. $2 [ "!" ]
          if (((NodeOptional) ((NodeSequence) ich1).elementAt(2)).present()) {
            // do not create node so do not walk down
            nRes = 0;
            break;
          }
          // $0 RegularExpression()
          nRes = ((NodeSequence) ich1).elementAt(0).accept(this) > 0 ? 1 : 0;
          break;
        default:
          final String msg = "Invalid nch1.which = " + nch1.which;
          Messages.hardErr(msg);
          throw new ProgrammaticError(msg);
        }
        break;
      case 5:
        // %5 #0 "(" #1 ExpansionChoices() #2 ")"
        // .. #3 ( &0 "+"
        // .. .. | &1 "*"
        // .. .. | &2 "?" )?
        // #1 ExpansionChoices()
        nRes = ((NodeSequence) ich).elementAt(1).accept(this) > 0 ? 1 : 0;
        break;
      default:
        final String msg = "Invalid nch.which = " + nch.which;
        Messages.hardErr(msg);
        throw new ProgrammaticError(msg);
      }
      res = Integer.valueOf(nRes);
      nbSubNodesTbcHM.put(n, res);
      return nRes;
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
    @NodeFieldsSignature({
        1601707097, JTB_SIG_EXPANSIONUNITTCF, JTB_USER_EXPANSIONUNITTCF
    })
    public int visit(final ExpansionUnitTCF n) {
      return n.f2.accept(this) > 0 ? 1 : 0;
    }
    
    /**
     * Visits a {@link RegularExpression} node, whose child is the following :
     * <p>
     * f0 -> . %0 StringLiteral()<br>
     * .. .. | %1 #0 "<"<br>
     * .. .. . .. #1 [ $0 [ "#" ]<br>
     * .. .. . .. .. . $1 IdentifierAsString() $2 ":" ]<br>
     * .. .. . .. #2 ComplexRegularExpressionChoices() #3 ">"<br>
     * .. .. | %2 #0 "<" #1 IdentifierAsString() #2 ">"<br>
     * .. .. | %3 #0 "<" #1 "EOF" #2 ">"<br>
     * s: 1719627151<br>
     *
     * @param n - the node to visit
     */
    @Override
    @NodeFieldsSignature({
        1719627151, JTB_SIG_REGULAREXPRESSION, JTB_USER_REGULAREXPRESSION
    })
    public int visit(final RegularExpression n) {
      // note : if we came down to here it means we have to create the node
      // f0 -> . %0 StringLiteral()
      // .. .. | %1 #0 "<"
      // .. .. . .. #1 [ $0 [ "#" ]
      // .. .. . .. .. . $1 IdentifierAsString() $2 ":" ]
      // .. .. . .. #2 ComplexRegularExpressionChoices() #3 ">"
      // .. .. | %2 #0 "<" #1 IdentifierAsString() #2 ">"
      // .. .. | %3 #0 "<" #1 "EOF" #2 ">"
      final NodeChoice nch = n.f0;
      final INode ich = nch.choice;
      switch (nch.which) {
      case 0:
        // %0 StringLiteral()
        return 1;
      case 1:
        // %1 #0 "<"
        // .. #1 [ $0 [ "#" ]
        // .. .. . $1 IdentifierAsString() $2 ":" ]
        // .. #2 ComplexRegularExpressionChoices() #3 ">"
        return 1;
      case 2:
        // %2 #0 "<" #1 IdentifierAsString() #2 ">"
        final NodeSequence seq9 = (NodeSequence) ich;
        // #1 IdentifierAsString()
        final INode seq11 = seq9.elementAt(1);
        final String ias = ((IdentifierAsString) seq11).f0.image;
        int ret = DONT_CREATE.equals(gdbv.getTokenHM().get(ias)) //
            || gdbv.getNotTbcNodesHM().containsKey(ias) //
                ? 0 //
                : 1;
        return ret;
      case 3:
        // %3 #0 "<" #1 "EOF" #2 ">"
        // Annotator creates a token node for < EOF > (but it can be suppressed)
        return 1;
      default:
        final String msg = "Invalid nch.which = " + nch.which;
        Messages.hardErr(msg);
        throw new ProgrammaticError(msg);
      }
    }
    
  }
  
  /**
   * Class handling a programmatic exception. Static for generic outer classes.
   *
   * @author Marc Mazas
   * @version 1.5.0 : 06/2017 : MMa : created
   */
  private class InvalidCountException extends RuntimeException {
    
    /** Default serialVersionUID */
    private static final long serialVersionUID = 1L;
    
    /**
     * Constructor which outputs a message.
     *
     * @param n - a node encountering the error
     */
    InvalidCountException(final INode n) {
      super("Non computed count value for node " + n);
    }
    
  }
  
}

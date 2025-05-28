/**
 * Copyright (c) 2004,2005 UCLA Compilers Group. All rights reserved. Redistribution and use in source and
 * binary forms, with or without modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this list of conditions and the
 * following disclaimer. Redistributions in binary form must reproduce the above copyright notice, this list
 * of conditions and the following disclaimer in the documentation and/or other materials provided with the
 * distribution. Neither UCLA nor the names of its contributors may be used to endorse or promote products
 * derived from this software without specific prior written permission. THIS SOFTWARE IS PROVIDED BY THE
 * COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE
 * GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 **/
/*
 * All files in the distribution of JTB, The Java Tree Builder are Copyright 1997, 1998, 1999 by the Purdue
 * Research Foundation of Purdue University. All rights reserved.
 *
 * Redistribution and use in source and binary forms are permitted provided that this entire copyright notice
 * is duplicated in all such copies, and that any documentation, announcements, and other materials related to
 * such distribution and use acknowledge that the software was developed at Purdue University, West Lafayette,
 * Indiana by Kevin Tao and Jens Palsberg. No charge may be made for copies, derivations, or distributions of
 * this material without the express written consent of the copyright holder. Neither the name of the
 * University nor the name of the author may be used to endorse or promote products derived from this material
 * without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND WITHOUT ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, WITHOUT
 * LIMITATION, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR ANY PARTICULAR PURPOSE.
 */
package EDU.purdue.jtb.generate;

import static EDU.purdue.jtb.analyse.GlobalDataBuilder.DONT_CREATE;
import static EDU.purdue.jtb.common.Constants.DEBUG_CLASS;
import static EDU.purdue.jtb.common.Constants.fileHeaderComment;
import static EDU.purdue.jtb.common.Constants.iEnterExitHook;
import static EDU.purdue.jtb.common.Constants.jjToken;
import static EDU.purdue.jtb.common.Constants.jtbHookEnter;
import static EDU.purdue.jtb.common.Constants.jtbHookExit;
import static EDU.purdue.jtb.common.Constants.jtbHookVar;
import static EDU.purdue.jtb.common.Constants.jtbNodeVar;
import static EDU.purdue.jtb.common.Constants.jtbRtOld;
import static EDU.purdue.jtb.common.Constants.jtbRtPrefix;
import static EDU.purdue.jtb.common.Constants.nodeChoice;
import static EDU.purdue.jtb.common.Constants.nodeList;
import static EDU.purdue.jtb.common.Constants.nodeListOptional;
import static EDU.purdue.jtb.common.Constants.nodeOptional;
import static EDU.purdue.jtb.common.Constants.nodeSequence;
import static EDU.purdue.jtb.common.Constants.ptHM;
import static EDU.purdue.jtb.parser.syntaxtree.NodeConstants.JTB_SIG_BLOCK;
import static EDU.purdue.jtb.parser.syntaxtree.NodeConstants.JTB_SIG_BLOCKSTATEMENT;
import static EDU.purdue.jtb.parser.syntaxtree.NodeConstants.JTB_SIG_BNFPRODUCTION;
import static EDU.purdue.jtb.parser.syntaxtree.NodeConstants.JTB_SIG_CLASSORINTERFACEBODY;
import static EDU.purdue.jtb.parser.syntaxtree.NodeConstants.JTB_SIG_CLASSORINTERFACETYPE;
import static EDU.purdue.jtb.parser.syntaxtree.NodeConstants.JTB_SIG_COMPILATIONUNIT;
import static EDU.purdue.jtb.parser.syntaxtree.NodeConstants.JTB_SIG_DOSTATEMENT;
import static EDU.purdue.jtb.parser.syntaxtree.NodeConstants.JTB_SIG_EXPANSION;
import static EDU.purdue.jtb.parser.syntaxtree.NodeConstants.JTB_SIG_EXPANSIONCHOICES;
import static EDU.purdue.jtb.parser.syntaxtree.NodeConstants.JTB_SIG_EXPANSIONUNIT;
import static EDU.purdue.jtb.parser.syntaxtree.NodeConstants.JTB_SIG_EXPANSIONUNITTCF;
import static EDU.purdue.jtb.parser.syntaxtree.NodeConstants.JTB_SIG_FORSTATEMENT;
import static EDU.purdue.jtb.parser.syntaxtree.NodeConstants.JTB_SIG_IDENTIFIERASSTRING;
import static EDU.purdue.jtb.parser.syntaxtree.NodeConstants.JTB_SIG_IFSTATEMENT;
import static EDU.purdue.jtb.parser.syntaxtree.NodeConstants.JTB_SIG_IMPORTDECLARATION;
import static EDU.purdue.jtb.parser.syntaxtree.NodeConstants.JTB_SIG_JAVACCINPUT;
import static EDU.purdue.jtb.parser.syntaxtree.NodeConstants.JTB_SIG_JAVACODEPRODUCTION;
import static EDU.purdue.jtb.parser.syntaxtree.NodeConstants.JTB_SIG_LABELEDSTATEMENT;
import static EDU.purdue.jtb.parser.syntaxtree.NodeConstants.JTB_SIG_LOCALLOOKAHEAD;
import static EDU.purdue.jtb.parser.syntaxtree.NodeConstants.JTB_SIG_LOCALVARIABLEDECLARATION;
import static EDU.purdue.jtb.parser.syntaxtree.NodeConstants.JTB_SIG_PRIMITIVETYPE;
import static EDU.purdue.jtb.parser.syntaxtree.NodeConstants.JTB_SIG_REFERENCETYPE;
import static EDU.purdue.jtb.parser.syntaxtree.NodeConstants.JTB_SIG_REGULAREXPRESSION;
import static EDU.purdue.jtb.parser.syntaxtree.NodeConstants.JTB_SIG_REGULAREXPRPRODUCTION;
import static EDU.purdue.jtb.parser.syntaxtree.NodeConstants.JTB_SIG_RESULTTYPE;
import static EDU.purdue.jtb.parser.syntaxtree.NodeConstants.JTB_SIG_RETURNSTATEMENT;
import static EDU.purdue.jtb.parser.syntaxtree.NodeConstants.JTB_SIG_STATEMENT;
import static EDU.purdue.jtb.parser.syntaxtree.NodeConstants.JTB_SIG_STRINGLITERAL;
import static EDU.purdue.jtb.parser.syntaxtree.NodeConstants.JTB_SIG_SWITCHSTATEMENT;
import static EDU.purdue.jtb.parser.syntaxtree.NodeConstants.JTB_SIG_SYNCHRONIZEDSTATEMENT;
import static EDU.purdue.jtb.parser.syntaxtree.NodeConstants.JTB_SIG_TRYSTATEMENT;
import static EDU.purdue.jtb.parser.syntaxtree.NodeConstants.JTB_SIG_TYPE;
import static EDU.purdue.jtb.parser.syntaxtree.NodeConstants.JTB_SIG_TYPEARGUMENT;
import static EDU.purdue.jtb.parser.syntaxtree.NodeConstants.JTB_SIG_TYPEARGUMENTS;
import static EDU.purdue.jtb.parser.syntaxtree.NodeConstants.JTB_SIG_VARIABLEMODIFIERS;
import static EDU.purdue.jtb.parser.syntaxtree.NodeConstants.JTB_SIG_WHILESTATEMENT;
import static EDU.purdue.jtb.parser.syntaxtree.NodeConstants.JTB_SIG_WILDCARDBOUNDS;
import static EDU.purdue.jtb.parser.syntaxtree.NodeConstants.JTB_USER_BLOCK;
import static EDU.purdue.jtb.parser.syntaxtree.NodeConstants.JTB_USER_BLOCKSTATEMENT;
import static EDU.purdue.jtb.parser.syntaxtree.NodeConstants.JTB_USER_BNFPRODUCTION;
import static EDU.purdue.jtb.parser.syntaxtree.NodeConstants.JTB_USER_CLASSORINTERFACEBODY;
import static EDU.purdue.jtb.parser.syntaxtree.NodeConstants.JTB_USER_CLASSORINTERFACETYPE;
import static EDU.purdue.jtb.parser.syntaxtree.NodeConstants.JTB_USER_COMPILATIONUNIT;
import static EDU.purdue.jtb.parser.syntaxtree.NodeConstants.JTB_USER_DOSTATEMENT;
import static EDU.purdue.jtb.parser.syntaxtree.NodeConstants.JTB_USER_EXPANSION;
import static EDU.purdue.jtb.parser.syntaxtree.NodeConstants.JTB_USER_EXPANSIONCHOICES;
import static EDU.purdue.jtb.parser.syntaxtree.NodeConstants.JTB_USER_EXPANSIONUNIT;
import static EDU.purdue.jtb.parser.syntaxtree.NodeConstants.JTB_USER_EXPANSIONUNITTCF;
import static EDU.purdue.jtb.parser.syntaxtree.NodeConstants.JTB_USER_FORSTATEMENT;
import static EDU.purdue.jtb.parser.syntaxtree.NodeConstants.JTB_USER_IDENTIFIERASSTRING;
import static EDU.purdue.jtb.parser.syntaxtree.NodeConstants.JTB_USER_IFSTATEMENT;
import static EDU.purdue.jtb.parser.syntaxtree.NodeConstants.JTB_USER_IMPORTDECLARATION;
import static EDU.purdue.jtb.parser.syntaxtree.NodeConstants.JTB_USER_JAVACCINPUT;
import static EDU.purdue.jtb.parser.syntaxtree.NodeConstants.JTB_USER_JAVACODEPRODUCTION;
import static EDU.purdue.jtb.parser.syntaxtree.NodeConstants.JTB_USER_LABELEDSTATEMENT;
import static EDU.purdue.jtb.parser.syntaxtree.NodeConstants.JTB_USER_LOCALLOOKAHEAD;
import static EDU.purdue.jtb.parser.syntaxtree.NodeConstants.JTB_USER_LOCALVARIABLEDECLARATION;
import static EDU.purdue.jtb.parser.syntaxtree.NodeConstants.JTB_USER_PRIMITIVETYPE;
import static EDU.purdue.jtb.parser.syntaxtree.NodeConstants.JTB_USER_REFERENCETYPE;
import static EDU.purdue.jtb.parser.syntaxtree.NodeConstants.JTB_USER_REGULAREXPRESSION;
import static EDU.purdue.jtb.parser.syntaxtree.NodeConstants.JTB_USER_REGULAREXPRPRODUCTION;
import static EDU.purdue.jtb.parser.syntaxtree.NodeConstants.JTB_USER_RESULTTYPE;
import static EDU.purdue.jtb.parser.syntaxtree.NodeConstants.JTB_USER_RETURNSTATEMENT;
import static EDU.purdue.jtb.parser.syntaxtree.NodeConstants.JTB_USER_STATEMENT;
import static EDU.purdue.jtb.parser.syntaxtree.NodeConstants.JTB_USER_STRINGLITERAL;
import static EDU.purdue.jtb.parser.syntaxtree.NodeConstants.JTB_USER_SWITCHSTATEMENT;
import static EDU.purdue.jtb.parser.syntaxtree.NodeConstants.JTB_USER_SYNCHRONIZEDSTATEMENT;
import static EDU.purdue.jtb.parser.syntaxtree.NodeConstants.JTB_USER_TRYSTATEMENT;
import static EDU.purdue.jtb.parser.syntaxtree.NodeConstants.JTB_USER_TYPE;
import static EDU.purdue.jtb.parser.syntaxtree.NodeConstants.JTB_USER_TYPEARGUMENT;
import static EDU.purdue.jtb.parser.syntaxtree.NodeConstants.JTB_USER_TYPEARGUMENTS;
import static EDU.purdue.jtb.parser.syntaxtree.NodeConstants.JTB_USER_VARIABLEMODIFIERS;
import static EDU.purdue.jtb.parser.syntaxtree.NodeConstants.JTB_USER_WHILESTATEMENT;
import static EDU.purdue.jtb.parser.syntaxtree.NodeConstants.JTB_USER_WILDCARDBOUNDS;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import EDU.purdue.jtb.analyse.GlobalDataBuilder;
import EDU.purdue.jtb.analyse.GlobalDataBuilder.RetVarInfo;
import EDU.purdue.jtb.common.JTBOptions;
import EDU.purdue.jtb.common.JavaPrinter;
import EDU.purdue.jtb.common.Messages;
import EDU.purdue.jtb.common.ProgrammaticError;
import EDU.purdue.jtb.common.Spacing;
import EDU.purdue.jtb.parser.Token;
import EDU.purdue.jtb.parser.syntaxtree.BNFProduction;
import EDU.purdue.jtb.parser.syntaxtree.Block;
import EDU.purdue.jtb.parser.syntaxtree.BlockStatement;
import EDU.purdue.jtb.parser.syntaxtree.ClassOrInterfaceBody;
import EDU.purdue.jtb.parser.syntaxtree.ClassOrInterfaceType;
import EDU.purdue.jtb.parser.syntaxtree.CompilationUnit;
import EDU.purdue.jtb.parser.syntaxtree.ComplexRegularExpressionChoices;
import EDU.purdue.jtb.parser.syntaxtree.DoStatement;
import EDU.purdue.jtb.parser.syntaxtree.Expansion;
import EDU.purdue.jtb.parser.syntaxtree.ExpansionChoices;
import EDU.purdue.jtb.parser.syntaxtree.ExpansionUnit;
import EDU.purdue.jtb.parser.syntaxtree.ExpansionUnitTCF;
import EDU.purdue.jtb.parser.syntaxtree.ForStatement;
import EDU.purdue.jtb.parser.syntaxtree.INode;
import EDU.purdue.jtb.parser.syntaxtree.IdentifierAsString;
import EDU.purdue.jtb.parser.syntaxtree.IfStatement;
import EDU.purdue.jtb.parser.syntaxtree.ImportDeclaration;
import EDU.purdue.jtb.parser.syntaxtree.JavaCCInput;
import EDU.purdue.jtb.parser.syntaxtree.JavaCodeProduction;
import EDU.purdue.jtb.parser.syntaxtree.LabeledStatement;
import EDU.purdue.jtb.parser.syntaxtree.LocalLookahead;
import EDU.purdue.jtb.parser.syntaxtree.LocalVariableDeclaration;
import EDU.purdue.jtb.parser.syntaxtree.NodeChoice;
import EDU.purdue.jtb.parser.syntaxtree.NodeList;
import EDU.purdue.jtb.parser.syntaxtree.NodeListOptional;
import EDU.purdue.jtb.parser.syntaxtree.NodeOptional;
import EDU.purdue.jtb.parser.syntaxtree.NodeSequence;
import EDU.purdue.jtb.parser.syntaxtree.PrimitiveType;
import EDU.purdue.jtb.parser.syntaxtree.ReferenceType;
import EDU.purdue.jtb.parser.syntaxtree.RegularExprProduction;
import EDU.purdue.jtb.parser.syntaxtree.RegularExpression;
import EDU.purdue.jtb.parser.syntaxtree.ResultType;
import EDU.purdue.jtb.parser.syntaxtree.ReturnStatement;
import EDU.purdue.jtb.parser.syntaxtree.Statement;
import EDU.purdue.jtb.parser.syntaxtree.StringLiteral;
import EDU.purdue.jtb.parser.syntaxtree.SwitchStatement;
import EDU.purdue.jtb.parser.syntaxtree.SynchronizedStatement;
import EDU.purdue.jtb.parser.syntaxtree.TryStatement;
import EDU.purdue.jtb.parser.syntaxtree.Type;
import EDU.purdue.jtb.parser.syntaxtree.TypeArgument;
import EDU.purdue.jtb.parser.syntaxtree.TypeArguments;
import EDU.purdue.jtb.parser.syntaxtree.VariableModifiers;
import EDU.purdue.jtb.parser.syntaxtree.WhileStatement;
import EDU.purdue.jtb.parser.syntaxtree.WildcardBounds;
import EDU.purdue.jtb.parser.visitor.signature.NodeFieldsSignature;

/**
 * The {@link JJFileAnnotator} visitor generates the (jtb) annotated .jj file containing the tree-building
 * code.
 * <p>
 * Code is printed in a buffer and {@link #saveToFile} is called to save it in the output file.
 * <p>
 * {@link JJFileAnnotator} works as follows:
 * <ul>
 * <li>it gets and memorizes the result type of a {@link JavaCodeProduction} or a {@link BNFProduction},</li>
 * <li>in {@link #generateJcRHS(JavaCodeProduction)} and {@link #generateBnfRHS(BNFProduction)}, it redirects
 * output to a temporary buffer,
 * <li>it walks down the tree, prints the RHS into the temporary buffer, and builds the varList,
 * <li>it traverses varList, prints the variable declarations to the main buffer
 * <li>it prints the Block (for a {@link BNFProduction} to the main buffer, then the temporary buffer into the
 * main buffer.
 * </ul>
 * When it wants to print a node and its subtree without annotating it, it uses an instance of the
 * {@link JavaCCPrinter} to visit the node.
 * <p>
 * This visitor maintains state (for a grammar), is supposed to be run once and not supposed to be run in
 * parallel threads (on the same grammar).
 * </p>
 *
 * @author Marc Mazas
 * @version 1.4.0 : 05-08/2009 : MMa : adapted to JavaCC v4.2 grammar and JDK 1.5
 * @version 1.4.0.2 : 01/2010 : MMa : fixed output of else in IfStatement
 * @version 1.4.6 : 01/2011 : FA/MMa : add -va and -npfx and -nsfx options
 * @version 1.4.7 : 12/2011 : MMa : commented the JavaCodeProduction visit method ;<br>
 *          optimized JTBToolkit class's code<br>
 *          1.4.7 : 07/2012 : MMa : followed changes in JTBParser.jtb (IndentifierAsString(), ForStatement()),
 *          updated grammar comments in the code<br>
 *          1.4.7 : 08-09/2012 : MMa : fixed soft errors for empty NodeChoice (bug JTB-1) ;<br>
 *          fixed error on return statement for a void production ; added non node creation ;<br>
 *          tuned messages labels and added the line number finder visitor ; moved the inner GlobalDataFinder
 *          to {@link GlobalDataBuilder}
 * @version 1.4.8 : 10/2012 : MMa : added JavaCodeProduction class generation if requested ;<br>
 *          fixed visit LocalVariableDeclaration ; improved specials printing
 * @version 1.4.9 : 01/2015 : MMa : fixed regression in {@link #bnfFinalActions(VarInfo)}
 * @version 1.4.11 : 03/2016 : MMa : fixed column numbers in warnings, and conditions for warning "Empty
 *          choice : a NodeChoice with a 'null' choice member ..."
 * @version 1.4.13 : 01/2017 : MMa : fixed missing space in visiting VariableModifiers
 * @version 1.5.0 : 01-06/2017 : MMa : removed the JTBToolkit class ; added node scope hook methods calls
 *          generation ; changed some iterator based for loops to enhanced for loops ; fixed processing of "%"
 *          indicator in JavaCodeProduction ; fixed processing of not to be created nodes ; removed
 *          ExpressionUnitTypeCounter reference ; fixed lines indentations and newlines ; added final in
 *          ExpansionUnitTCF's catch ; removed NodeTCF related code<br>
 *          10-11/2022 : MMa : fixed many issues while improving test grammars
 * @version 1.5.1 : 07/2023 : MMa : fixed issue on expansion choices with choices and on expansion unit ;
 *          added optional annotations in catch of ExpansionUnitTCF<br>
 *          1.5.1 : 08/2023 : MMa : editing changes for coverage analysis; changes due to the NodeToken
 *          replacement by Token, suppressed Token nodes generation
 * @version 1.5.1 : 09/2023 : MMa : dropped attempts to join annotated &amp; user blocks
 */
public class JJFileAnnotator extends JavaCCPrinter {

  /** The messages handler */
  final Messages                       mess;
  /** The {@link CompilationUnitPrinter} visitor for printing the compilation unit */
  private final CompilationUnitPrinter cupv;
  /** The {@link JavaCCPrinter} visitor for lower nodes which don't need annotation */
  private final JavaCCPrinter          jccpv;
  /**
   * The "outer variables" nesting level: incremented/decremented:<br>
   * <ul>
   * <li>for each new nested {@link ExpansionChoices} (so starts at 0 (in {@link #visit(BNFProduction)})), and
   * <li>for each new nested {@link Expansion} except in an {@link ExpansionChoices} with no choices
   * <li>for each new nested {@link ExpansionUnit} which is not a field (varLvl == 0) and which is part of a
   * NodeSequence
   * </ul>
   * Used to control nodes annotations.
   */
  protected int                        varLvl            = 0;
  /** The counter for generated variables names */
  private int                          varNum;
  /** The list of all variables to be declared */
  private final List<VarInfo>          varList           = new ArrayList<>();
  /** The list of additional variables to initialize */
  private List<VarInfo>                extraVarsList     = null;
  /**
   * The list of all "outer" variables (production's children nodes) for the production default constructor
   */
  private final List<VarInfo>          outerVars         = new ArrayList<>();
  /** The last variable info generated so far (to be added to the parent - the current variable) */
  private VarInfo                      lastVar;
  /** The RegularExpression generated token name */
  private String                       reTokenName       = null;
  /**
   * True to tell lower layers to create the RegularExpression node, false otherwise; set in ExpansionUnit and
   * used in RegularExpression
   */
  private boolean                      createRENode      = true;
  /**
   * True to tell the upper layers that a child ExpansionUnit or NodeSequence node has just been created or
   * filled (so to add it to the NodeSequence), false otherwise
   */
  private boolean                      isEUNSNodeCreated = false;
  /** The name of the current production (JavaCodeProduction or BNFProduction) */
  private String                       curProd;
  /** The fixed name of the current production (JavaCodeProduction or BNFProduction) */
  private String                       curProdFixed;
  /** The JavaCodeProduction or BNFProduction result type */
  private String                       resultType        = null;
  /** The flag to print only once (in the parser main class) the makeToken method */
  boolean                              inMainClass       = true;
  /** The number of direct ExpansionUnit (s) under the top level Expansion, 0 otherwise */
  int                                  nbEU;
  /** The index of the current direct ExpansionUnit under the top level Expansion */
  int                                  curEU;
  /** The flag for a top level Expansion */
  boolean                              isTopExp;
  /**
   * The flag for deferring appending the last Block of an ExpansionUnit (after the NodeChoice and user node
   */
  boolean                              deferLB;
  /** A buffer to hold the last Block when deferred */
  StringBuilder                        sbLB              = new StringBuilder(100);
  /** The index of current appended variable saving the return value */
  int                                  ixOldJtbRt;
  /** The flag telling whether the user node has already be generated or not */
  boolean                              isUserNodeGenerated;

  /**
   * Constructor which will allocate a default buffer and indentation.
   *
   * @param aGdbv - the {@link GlobalDataBuilder} visitor
   * @param aCcg - the {@link CommonCodeGenerator}
   */
  public JJFileAnnotator(final GlobalDataBuilder aGdbv, final CommonCodeGenerator aCcg) {
    super(aGdbv, aCcg);
    JJNCDCP = " //ann ";
    mess = aGdbv.jopt.mess;
    cupv = new CompilationUnitPrinter(aGdbv.jopt, sb, spc);
    jccpv = new JavaCCPrinter(aGdbv, aCcg, sb, spc);
    jccpv.JJNCDCP = " //jccpa ";
  }

  /*
   * Convenience methods
   */

  /**
   * Generates a new variable name (n0, n1, ...)
   *
   * @return the new variable name
   */
  private final String genNewVarName() {
    return "n" + varNum++;
  }

  /**
   * Returns the java block for adding a node to its parent.
   *
   * @param parent - the parent node's info
   * @param var - the node's info
   * @return the java block
   */
  private static final String addNodeToParent(final VarInfo parent, final VarInfo var) {
    if (DEBUG_CLASS) {
      return "{ /* ".concat(parent.getType()).concat(" */ ").concat(parent.getName()).concat(".addNode( /* ")
          .concat(var.getType()).concat(" */ ").concat(var.getName()).concat(" ); }");
    } else {
      return "{ ".concat(parent.getName()).concat(".addNode(").concat(var.getName()).concat("); }");
    }
  }

  /*
   * User grammar generated and overridden visit methods below
   */

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
    // generate now output
    sb.append(spc.spc);
    sb.append(fileHeaderComment);
    oneNewLine(n);
    // f0 -> JavaCCOptions() : don't want to annotate under
    sb.append(spc.spc);
    n.f0.accept(jccpv);
    oneNewLine(n);
    // f1 -> "PARSER_BEGIN"
    sb.append(spc.spc);
    n.f1.accept(this);
    // f2 -> "("
    n.f2.accept(this);
    // f3 -> IdentifierAsString()
    n.f3.accept(this);
    // f4 -> ")"
    n.f4.accept(this);
    oneNewLine(n);
    // f5 -> CompilationUnit()
    sb.append(spc.spc);
    n.f5.accept(cupv);
    oneNewLine(n);
    // f6 -> "PARSER_END"
    sb.append(spc.spc);
    n.f6.accept(this);
    // f7 -> "("
    n.f7.accept(this);
    // f8 -> IdentifierAsString()
    n.f8.accept(this);
    // f9 -> ")"
    n.f9.accept(this);
    twoNewLines(n);
    // f10 -> ( Production() )+
    for (final INode e : n.f10.nodes) {
      sb.append(spc.spc);
      e.accept(this);
      oneNewLine(n);
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
    if (!n.f6.present()) {
      // node not to be generated, don't want to annotate under; let JavaCCPrinter do the work
      n.accept(jccpv);
      return;
    }
    // node to be generated
    curProd = n.f3.f0.image;
    curProdFixed = gdbv.getFixedName(curProd);
    // f0 -> "JAVACODE"
    sb.append(spc.spc);
    n.f0.accept(this);
    // f1 -> AccessModifier()
    sb.append(' ');
    n.f1.accept(this);
    // f2 -> ResultType()
    // first print the f1 specials, then print the (fixed) IdentifierAsString instead of the ResultType
    resultType = getResultType(n.f2);
    // if (jopt.printSpecialTokensJJ) {
    // sb.append(resultTypeSpecials);
    // }
    sb.append(curProdFixed);
    sb.append(' ');
    // f3 -> IdentifierAsString()
    sb.append(curProdFixed);
    // f4 -> FormalParameters()
    sb.append(genJavaBranch(n.f4));
    // f5 -> [ #0 "throws" #1 Name() #2 ( $0 "," $1 Name() )* ]
    if (n.f5.present()) {
      final NodeSequence seq = (NodeSequence) n.f5.node;
      sb.append(' ');
      // #0 "throws"
      seq.elementAt(0).accept(this);
      sb.append(' ');
      // #1 Name()
      sb.append(genJavaBranch(seq.elementAt(1)));
      // #2 ( $0 "," $1 Name() )*
      final NodeListOptional opt = (NodeListOptional) seq.elementAt(2);
      if (opt.present()) {
        for (final INode e : opt.nodes) {
          final NodeSequence seq1 = (NodeSequence) e;
          // $0 ","
          sb.append(genJavaBranch(seq1.elementAt(0)));
          sb.append(' ');
          // $1 Name()
          sb.append(genJavaBranch(seq1.elementAt(1)));
        }
      }
    }
    // f6 -> [ "%" ]
    // print it in a block comment; here always present, see the first if of the method
    // if (n.f6.present()) {
    sb.append(" /*%*/ ");
    // }
    oneNewLine(n, "b");
    generateJcRHS(n);
    // reset global variable
    resultType = null;
  }

  /**
   * Prints the RHS (the Block) of the current JavaCodeProduction.<br>
   * When this function returns, varList and outerVars will have been built and will be used by the calling
   * method.
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
   * @param n - the node to process
   */
  @NodeFieldsSignature({
      -763138104, JTB_SIG_JAVACODEPRODUCTION, JTB_USER_JAVACODEPRODUCTION
  })
  private void generateJcRHS(final JavaCodeProduction n) {
    // node to be generated, specific processing
    // in Block f0 -> "{"
    sb.append(spc.spc);
    n.f7.f0.accept(this);
    oneNewLine(n, "generateJcRHS a");
    spc.updateSpc(+1);
    if (jopt.hook) {
      // add enter node scope hook method
      sb.append(spc.spc);
      sb.append("if (").append(jtbHookVar).append(" != null) ").append(jtbHookVar).append(".")
          .append(curProdFixed).append(jtbHookEnter).append("();");
      oneNewLine(n, "generateJcRHS b");
    }
    // in Block f1 -> ( BlockStatement() )*
    sb.append(spc.spc);
    n.f7.f1.accept(this);
    oneNewLine(n, "generateJcRHS c");
    if (jopt.hook) {
      // add exit node scope hook method
      sb.append(spc.spc);
      sb.append(curProdFixed).append(' ').append(jtbNodeVar).append(" = new ")
          .append(gdbv.getFixedName(n.f3.f0.image)).append("();");
      oneNewLine(n, "generateJcRHS d");
      sb.append(spc.spc);
      sb.append("if (").append(jtbHookVar).append(" != null) ").append(jtbHookVar).append(".")
          .append(curProdFixed).append(jtbHookExit).append("(").append(jtbNodeVar).append(");");
      oneNewLine(n, "generateJcRHS e");
      sb.append(spc.spc);
      sb.append("return ").append(jtbNodeVar).append(";");
    } else {
      sb.append(spc.spc);
      sb.append("return new ").append(gdbv.getFixedName(n.f3.f0.image)).append("();");
    }
    oneNewLine(n, "generateJcRHS f");
    // in Block f2 -> "}"
    spc.updateSpc(-1);
    sb.append(spc.spc);
    n.f7.f2.accept(this);
    oneNewLine(n, "generateJcRHS g");
    return;
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
    if (n.f5.present()) {
      // node not to be generated, don't want to annotate much under; let JavaCCPrinter do the work
      n.accept(jccpv);
      return;
    }

    // node to be generated
    varList.clear();
    outerVars.clear();
    lastVar = null;
    varNum = 0;
    varLvl = 0;
    isTopExp = false;
    curProd = n.f2.f0.image;
    curProdFixed = gdbv.getFixedName(curProd);
    isUserNodeGenerated = false;

    // f0 -> AccessModifier()
    n.f0.accept(this);

    // f1 -> ResultType()
    // node to be generated
    // first print the f1 specials, then print the (fixed) IdentifierAsString instead of the ResultType
    resultType = getResultType(n.f1);
    // if (jopt.printSpecialTokensJJ) {
    // sb.append(resultTypeSpecials);
    // }
    sb.append(curProdFixed);
    sb.append(' ');

    // f2 -> IdentifierAsString()
    sb.append(curProdFixed);

    // f3 -> FormalParameters()
    sb.append(genJavaBranch(n.f3));

    // f4 -> [ #0 "throws" #1 Name() #2 ( $0 "," $1 Name() )* ]
    if (n.f4.present()) {
      final NodeSequence seq = (NodeSequence) n.f4.node;
      sb.append(' ');
      seq.elementAt(0).accept(this);
      sb.append(' ');
      seq.elementAt(1).accept(this);
      final NodeListOptional nlo = (NodeListOptional) seq.elementAt(2);
      if (nlo.present()) {
        for (final INode e : nlo.nodes) {
          final NodeSequence seq1 = (NodeSequence) e;
          seq1.elementAt(0).accept(this);
          sb.append(' ');
          seq1.elementAt(1).accept(this);
        }
      }
    }

    // f5 -> [ "!" ]
    // should not occur due to first test in the method

    // f6 -> ":"
    sb.append(' ');
    n.f6.accept(this);
    oneNewLine(n, ":");

    // generate the RHS (f8 -> "{" f9 -> ExpansionChoices() f10 -> "}") into a temporary buffer
    // and collect variables
    ixOldJtbRt = 0;
    final StringBuilder rhsSB = generateBnfRHS(n);

    // f7 -> Block() (left brace)
    sb.append(spc.spc);
    n.f7.f0.accept(this);
    oneNewLine(n, "{");
    spc.updateSpc(+1);

    // print variables (from RHS plus node)
    if (jopt.printSpecialTokensJJ) {
      sb.append(spc.spc);
      sb.append("// --- JTB generated node declarations ---");
      oneNewLine(n, "jgnd");
    }
    for (final Iterator<VarInfo> e = varList.iterator(); e.hasNext();) {
      sb.append(spc.spc);
      sb.append(e.next().generateNodeDeclaration());
      oneNewLine(n, "gnd");
    }
    // print the user node declaration
    sb.append(spc.spc);
    sb.append(curProdFixed).append(' ').append(jtbNodeVar).append(" = null;");
    oneNewLine(n, "nv");

    // f7 -> Block() (user variables declarations)
    if (n.f7.f1.present()) {
      // print block declarations only if non empty
      // don't print "{" and "}" otherwise the resulting inner scope will prevent to use declarations
      if (jopt.printSpecialTokensJJ) {
        sb.append(spc.spc);
        sb.append("// --- user BNFProduction java block ---");
        oneNewLine(n, "ubjb");
      }
      for (final Iterator<INode> e = n.f7.f1.elements(); e.hasNext();) {
        // BlockStatement()
        sb.append(spc.spc);
        e.next().accept(this);
        oneNewLine(n, "bs");
      }
    }
    spc.updateSpc(-1);

    // f7 -> Block() (right brace)
    sb.append(spc.spc);
    n.f7.f2.accept(this);
    oneNewLine(n, "}");

    // f8 -> "{"
    // f9 -> ExpansionChoices()
    // f10 -> "}"
    // print the RHS buffer generated above
    sb.append(rhsSB);

    // reset global variable
    resultType = null;
  }

  /**
   * Gets a {@link ResultType} node string, whose child is the following :
   * <p>
   * f0 -> ( %0 "void"<br>
   * .. .. | %1 Type() )<br>
   * s: 805291204<br>
   *
   * @param n - the node to process
   * @return the ResultType string
   */
  @NodeFieldsSignature({
      805291204, JTB_SIG_RESULTTYPE, JTB_USER_RESULTTYPE
  })
  private String getResultType(final ResultType n) {
    // resultTypeSpecials = null;
    switch (n.f0.which) {
    case 0:
      // %0 "void"
      final Token tk = (Token) n.f0.choice;
      // resultTypeSpecials = tk.getSpecials(spc.spc);
      return tk.image;
    case 1:
      // %1 Type()
      return getType((Type) n.f0.choice);
    default:
      // should not occur !!!
      throw new ShouldNotOccurException(n.f0);
    }
  }

  /**
   * Gets a {@link Type} node string, whose child is the following :
   * <p>
   * f0 -> . %0 ReferenceType()<br>
   * .. .. | %1 PrimitiveType()<br>
   * s: -1143267570<br>
   *
   * @param n - the node to visit
   * @return the Type string
   */
  @NodeFieldsSignature({
      -1143267570, JTB_SIG_TYPE, JTB_USER_TYPE
  })
  private String getType(final Type n) {
    // f0 -> . %0 ReferenceType()
    // .. .. | %1 PrimitiveType()
    switch (n.f0.which) {
    case 0:
      // %0 ReferenceType()
      return getReferenceType((ReferenceType) n.f0.choice);
    case 1:
      // %1 PrimitiveType()
      return getPrimitiveType((PrimitiveType) n.f0.choice);
    default:
      // should not occur !!!
      throw new ShouldNotOccurException(n.f0);
    }
  }

  /**
   * Gets a {@link ReferenceType} node string, whose child is the following :
   * <p>
   * f0 -> . %0 #0 PrimitiveType()<br>
   * .. .. . .. #1 ( $0 "[" $1 "]" )+<br>
   * .. .. | %1 #0 ClassOrInterfaceType()<br>
   * .. .. . .. #1 ( $0 "[" $1 "]" )*<br>
   * s: -275468366<br>
   *
   * @param n - the node to visit
   * @return the ReferenceType string
   */
  @NodeFieldsSignature({
      -275468366, JTB_SIG_REFERENCETYPE, JTB_USER_REFERENCETYPE
  })
  private String getReferenceType(final ReferenceType n) {
    switch (n.f0.which) {
    case 0:
      // %0 #0 PrimitiveType()
      // .. #1 ( $0 "[" $1 "]" )+
      final NodeSequence seq = (NodeSequence) n.f0.choice;
      String pts = getPrimitiveType((PrimitiveType) seq.elementAt(0));
      final NodeList lst = (NodeList) seq.elementAt(1);
      for (int i = 0; i < lst.size(); i++) {
        pts = pts + "[]";
      }
      return pts;
    case 1:
      // %1 #0 ClassOrInterfaceType()
      // .. #1 ( $0 "[" $1 "]" )*
      final NodeSequence seq2 = (NodeSequence) n.f0.choice;
      String coits = getClassOrInterfaceType((ClassOrInterfaceType) seq2.elementAt(0));
      final NodeListOptional nlo = (NodeListOptional) seq2.elementAt(1);
      if (nlo.present()) {
        for (int i = 0; i < nlo.size(); i++) {
          coits = coits + "[]";
        }
      }
      return coits;
    default:
      // should not occur !!!
      throw new ShouldNotOccurException(n.f0);
    }
  }

  /**
   * Gets a {@link PrimitiveType} node string, whose child is the following :
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
   * @return the PrimitiveType string
   */
  @NodeFieldsSignature({
      427914477, JTB_SIG_PRIMITIVETYPE, JTB_USER_PRIMITIVETYPE
  })
  private static String getPrimitiveType(final PrimitiveType n) {
    final Token tk = (Token) n.f0.choice;
    // resultTypeSpecials = tk.getSpecials(spc.spc);
    return tk.image;
  }

  /**
   * Gets a {@link ClassOrInterfaceType} node string, whose children are the following :
   * <p>
   * f0 -> < IDENTIFIER ><br>
   * f1 -> [ TypeArguments() ]<br>
   * f2 -> ( #0 "." #1 < IDENTIFIER ><br>
   * .. .. . #2 [ TypeArguments() ] )*<br>
   * s: -1178309727<br>
   *
   * @param n - the node to visit
   * @return the ClassOrInterfaceType string
   */
  @NodeFieldsSignature({
      -1178309727, JTB_SIG_CLASSORINTERFACETYPE, JTB_USER_CLASSORINTERFACETYPE
  })
  private String getClassOrInterfaceType(final ClassOrInterfaceType n) {
    // resultTypeSpecials = n.f0.getSpecials(spc.spc);
    String coits = n.f0.image;
    // f1 -> [ TypeArguments() ]
    final NodeOptional n1 = n.f1;
    if (n1.present()) {
      coits = coits + getTypeArguments((TypeArguments) n1.node);
    }
    // f2 -> ( #0 "." #1 < IDENTIFIER >
    // .. .. . #2 [ TypeArguments() ] )*
    final NodeListOptional n2 = n.f2;
    if (n2.present()) {
      for (int i = 0; i < n2.size(); i++) {
        final NodeSequence seq = (NodeSequence) n2.elementAt(i);
        // #0 "."
        coits = coits + ".";
        // #1 < IDENTIFIER >
        coits = coits + ((Token) seq.elementAt(1)).image;
        // #2 [ TypeArguments() ] )*
        final NodeOptional opt = (NodeOptional) seq.elementAt(2);
        if (opt.present()) {
          coits = coits + getTypeArguments((TypeArguments) opt.node);
        }
      }
    }
    return coits;
  }

  /**
   * Gets a {@link TypeArguments} node string, whose children are the following :
   * <p>
   * f0 -> "<"<br>
   * f1 -> TypeArgument()<br>
   * f2 -> ( #0 "," #1 TypeArgument() )*<br>
   * f3 -> ">"<br>
   * s: 131755052<br>
   *
   * @param n - the node to visit
   * @return the TypeArguments string
   */
  @NodeFieldsSignature({
      131755052, JTB_SIG_TYPEARGUMENTS, JTB_USER_TYPEARGUMENTS
  })
  private String getTypeArguments(final TypeArguments n) {
    // f0 -> "<"
    String tas = "<";
    tas = tas + getTypeArgument(n.f1);
    // f2 -> ( #0 "," #1 TypeArgument() )*
    final NodeListOptional n2 = n.f2;
    if (n2.present()) {
      for (int i = 0; i < n2.size(); i++) {
        final NodeSequence seq = (NodeSequence) n2.elementAt(i);
        // #0 ","
        tas = tas + ", ";
        // #1 TypeArgument()
        tas = tas + getTypeArgument((TypeArgument) seq.elementAt(1));
      }
    }
    // f3 -> ">"
    tas = tas + ">";
    return tas;
  }

  /**
   * Gets a {@link TypeArgument} node string, whose child is the following :
   * <p>
   * f0 -> . %0 ReferenceType()<br>
   * .. .. | %1 #0 "?"<br>
   * .. .. . .. #1 [ WildcardBounds() ]<br>
   * s: 36461692<br>
   *
   * @param n - the node to visit
   * @return the TypeArgument string
   */
  @NodeFieldsSignature({
      36461692, JTB_SIG_TYPEARGUMENT, JTB_USER_TYPEARGUMENT
  })
  private String getTypeArgument(final TypeArgument n) {
    // f0 -> . %0 ReferenceType()
    // .. .. | %1 #0 "?"
    // .. .. . .. #1 [ WildcardBounds() ]
    final NodeChoice nch = n.f0;
    final INode ich = nch.choice;
    switch (nch.which) {
    case 0:
      // %0 ReferenceType()
      return getReferenceType((ReferenceType) ich);
    case 1:
      // #0 "?"
      String wbs = "";
      // #1 [ WildcardBounds() ]
      final NodeOptional opt = (NodeOptional) ((NodeSequence) ich).elementAt(1);
      if (opt.present()) {
        wbs = getWildcardBounds((WildcardBounds) opt.node);
      }
      return " ? " + wbs;
    default:
      // should not occur !!!
      throw new ShouldNotOccurException(nch);
    }
  }

  /**
   * Gets a {@link WildcardBounds} node string, whose child is the following :
   * <p>
   * f0 -> . %0 #0 "extends" #1 ReferenceType()<br>
   * .. .. | %1 #0 "super" #1 ReferenceType()<br>
   * s: 122808000<br>
   *
   * @param n - the node to visit
   * @return the WildcardBounds string
   */
  @NodeFieldsSignature({
      122808000, JTB_SIG_WILDCARDBOUNDS, JTB_USER_WILDCARDBOUNDS
  })
  private String getWildcardBounds(final WildcardBounds n) {
    // f0 -> . %0 #0 "extends" #1 ReferenceType()
    // .. .. | %1 #0 "super" #1 ReferenceType()
    final NodeChoice nch = n.f0;
    switch (nch.which) {
    case 0:
      // %0 #0 "extends" #1 ReferenceType()
      return " extends " + getReferenceType((ReferenceType) ((NodeSequence) nch.choice).elementAt(1));
    case 1:
      // %1 #0 "super" #1 ReferenceType()
      return " super " + getReferenceType((ReferenceType) ((NodeSequence) nch.choice).elementAt(1));
    default:
      // should not occur !!!
      throw new ShouldNotOccurException(nch);
    }
  }

  /**
   * Returns a string with the RHS (after the ":") of the current BNFProduction.<br>
   * When this function returns, varList and outerVars will have been built and will be used by the calling
   * method.
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
   * @param n - the node to process
   * @return the generated buffer
   */
  @NodeFieldsSignature({
      1323482450, JTB_SIG_BNFPRODUCTION, JTB_USER_BNFPRODUCTION
  })
  private StringBuilder generateBnfRHS(final BNFProduction n) {
    final StringBuilder mainSB = sb;
    final StringBuilder newSB = new StringBuilder(512);
    sb = jccpv.sb = newSB;
    // node to be generated, specific processing
    // f8 -> "{"
    sb.append(spc.spc);
    n.f8.accept(this);
    oneNewLine(n, "generateBnfRHS a");
    spc.updateSpc(+1);
    if (jopt.printSpecialTokensJJ) {
      sb.append(spc.spc);
      sb.append("// --- user BNFProduction ExpansionChoices ---");
      oneNewLine(n, "generateBnfRHS b");
    }

    // we do not know at this point the outerVars, so we need to switch to a temporary buffer
    final StringBuilder oldSB = sb;
    StringBuilder tempSB = new StringBuilder(512);
    sb = jccpv.sb = tempSB;
    // outerVars will be set further down the tree in finalActions
    // f9 -> ExpansionChoices()
    n.f9.accept(this);
    // switch back buffers
    tempSB = sb;
    sb = jccpv.sb = oldSB;
    if (jopt.hook) {
      // add enter node scope hook method
      sb.append(spc.spc);
      sb.append("{ if (").append(jtbHookVar).append(" != null) ").append(jtbHookVar).append(".")
          .append(curProdFixed).append(jtbHookEnter).append("(); }");
      oneNewLine(n, "generateBnfRHS d");
    }
    // add the expansion choices generation from the temporary buffer
    sb.append(tempSB);
    // generate the node
    genUserNodeCreation(n);
    if (jopt.hook) {
      // add exit node scope hook method
      sb.append(spc.spc);
      sb.append("{ if (").append(jtbHookVar).append(" != null) ").append(jtbHookVar).append(".")
          .append(curProdFixed).append(jtbHookExit).append("(").append(jtbNodeVar).append("); }");
      oneNewLine(n, "generateBnfRHS e");
    }
    // add the return
    sb.append(spc.spc);
    sb.append("{ return ").append(jtbNodeVar).append("; }");
    oneNewLine(n, "generateBnfRHS f");
    spc.updateSpc(-1);

    // f10 -> "}"
    sb.append(spc.spc);
    n.f10.accept(this);
    oneNewLine(n, "generateBnfRHS g");
    sb = jccpv.sb = mainSB;
    return newSB;
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
    // Don't want to annotate under
    n.accept(jccpv);
  }

  /**
   * Common end-code for annotation methods
   *
   * @param varInfo - the variable to annotate
   */
  private void bnfFinalActions(final VarInfo varInfo) {
    if (varLvl == 0) {
      outerVars.add(varInfo);
    } else {
      lastVar = varInfo;
    }
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
  public void visit(final ExpansionChoices n) {
    final boolean oldIsTopExp = isTopExp;
    isTopExp = !isTopExp && (varLvl <= 1);
    final int nbSubNodesTbc = gdbv.getNbSubNodesTbc(n);
    oneDebugClassNewLine(n, "a, oldIsTopExp = " + oldIsTopExp + ", isTopExp = " + isTopExp + ", varLvl = "
        + varLvl + ", nbSubNodesTbc = " + nbSubNodesTbc);
    if (nbSubNodesTbc > 0) {
      // here we have to generate a node ...
      if (!n.f1.present()) {
        // ... which has no choices so will be known and added under the Expansion
        // f0 -> Expansion()
        n.f0.accept(this);
      } else {
        // ... which has choices so is a NodeChoice, which must be added now
        final String varName = genNewVarName();
        final VarInfo varInfo = new VarInfo(nodeChoice, varName, "null");
        varList.add(varInfo);
        // bnfFinalActions() needs to be splitted
        if (varLvl == 0) {
          outerVars.add(varInfo);
        }
        genExpChoicesWithChoices(n, varName);
        if (varLvl != 0) {
          lastVar = varInfo;
        }
      }
    } else {
      // here we have zero node to generate (under a generated node at the parent level)
      // extra parenthesis needed!
      sb.append(spc.spc);
      sb.append("(");
      oneNewLine(n, "b, extra (");
      spc.updateSpc(+1);
      // f0 -> Expansion()
      n.f0.accept(this);
      // f1 -> ( #0 "|" #1 Expansion() )*
      for (final INode e : n.f1.nodes) {
        // #0 "|"
        sb.append(spc.spc);
        ((NodeSequence) e).elementAt(0).accept(this);
        oneNewLine(n, "b |");
        // #1 Expansion()
        ((NodeSequence) e).elementAt(1).accept(this);
      }
      // extra parenthesis needed!
      spc.updateSpc(-1);
      sb.append(spc.spc);
      sb.append(")");
      oneNewLine(n, "c, extra )");
    }
    isTopExp = oldIsTopExp;
    oneDebugClassNewLine(n, "d, isTopExp <- " + isTopExp);
  }

  /**
   * Visits the {@link ExpansionChoices}, and adds the NodeChoice variable declaration<br>
   * (called only when there is a a node to create and a choice).
   * <p>
   * f0 -> Expansion()<br>
   * f1 -> ( #0 "|" #1 Expansion() )*<br>
   * s: -1726831935<br>
   *
   * @param n - the node to process
   * @param varName - the variable name
   */
  private @NodeFieldsSignature({
      -1726831935, JTB_SIG_EXPANSIONCHOICES, JTB_USER_EXPANSIONCHOICES
  }) void genExpChoicesWithChoices(final ExpansionChoices n, final String varName) {
    int which = 0;
    final int total = n.f1.size() + 1;
    // visit the first choice (f0)
    // f0 -> Expansion()
    // extra parenthesis needed!
    sb.append(spc.spc);
    sb.append("(");
    oneNewLine(n, "genExpChWithChoices extra (");
    spc.updateSpc(+1);

    // NodeChoice must be created before the last Block
    deferLB = true;
    oneDebugClassNewLine(n, "f0, deferLB <- true");
    ++varLvl;
    n.f0.accept(this);
    --varLvl;
    genNodeChoiceCreation(n, varName, which, total);
    appendSbLB();
    spc.updateSpc(-1);
    ++which;
    lastVar = null;

    // visit the remaining choices (f1)
    // f1 -> ( #0 "|" #1 Expansion() )*
    final int sz = n.f1.nodes.size();
    for (int i = 0; i < sz; i++) {
      final NodeSequence seq = (NodeSequence) n.f1.nodes.get(i);
      // #0 "|"
      sb.append(spc.spc);
      seq.elementAt(0).accept(this);
      oneNewLine(n, "genExpChWithChoices |");
      spc.updateSpc(+1);
      // #1 Expansion()
      // NodeChoice must be created before the last Block
      deferLB = true;
      oneDebugClassNewLine(n, "fi, deferLB <- true");
      ++varLvl;
      seq.elementAt(1).accept(this);
      --varLvl;
      genNodeChoiceCreation(n, varName, which, total);
      appendSbLB();
      spc.updateSpc(-1);
      ++which;
      lastVar = null;
    }
    isEUNSNodeCreated = true;

    // extra parenthesis needed!
    sb.append(spc.spc);
    sb.append(")");
    oneNewLine(n, "genExpChWithChoices extra ), isEUNSNodeCreated <= " + isEUNSNodeCreated);
  }

  /**
   * Appends the last Block creation buffer and does corresponding resets.
   */
  private void appendSbLB() {
    deferLB = false;
    sb.append(sbLB);
    sbLB.setLength(0);
  }

  /**
   * Generates a new NodeChoice declaration / creation.
   *
   * @param n - the node to process
   * @param varName - the variable name
   * @param which - the current choice for the node
   * @param total - the total number of choices
   */
  private void genNodeChoiceCreation(final ExpansionChoices n, final String varName, final int which,
      final int total) {
    sb.append(spc.spc);
    sb.append("{ ").append(varName).append(" = new NodeChoice(")
        .append(lastVar == null ? "null" : lastVar.getName()).append(", ").append(String.valueOf(which))
        .append(", ").append(String.valueOf(total)).append("); }");
    oneNewLine(n, "genNodeChoiceCreation yes, isEUNSNodeCreated = " + isEUNSNodeCreated);
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
  public void visit(final Expansion n) {
    if (isTopExp) {
      curEU = 0;
      nbEU = n.f1.size();
    }
    oneDebugClassNewLine(n, "a, isTopExp = " + isTopExp + ", nbEU = " + nbEU + ", varLvl = " + varLvl);

    // f0 -> ( #0 "LOOKAHEAD" #1 "(" #2 LocalLookahead() #3 ")" )?
    if (n.f0.present()) {
      sb.append(spc.spc);
      final NodeSequence seq = (NodeSequence) n.f0.node;
      seq.elementAt(0).accept(this);
      seq.elementAt(1).accept(this);
      sb.append(' ');
      seq.elementAt(2).accept(this);
      sb.append(' ');
      seq.elementAt(3).accept(this);
      oneNewLine(n, "b, LocalLookahead in Expansion");
    }

    // f1 -> ( ExpansionUnit() )+
    final int sz = n.f1.size();
    if (varLvl == 0) {
      // here we'll have fields f0, f1, ...
      for (int i = 0; i < sz; i++) {
        n.f1.elementAt(i).accept(this);
      }
    } else {
      // here we are under a field
      // count for varList
      final int nbSubNodesTbc = gdbv.getNbSubNodesTbc(n);
      oneDebugClassNewLine(n, "c, sz = " + sz + ", nbSubNodesTbc = " + nbSubNodesTbc);
      if (nbSubNodesTbc == 0) {
        // no node to generate
        for (int i = 0; i < sz; i++) {
          n.f1.elementAt(i).accept(this);
        }
      } else if (nbSubNodesTbc == 1) {
        // one node to generate
        for (int i = 0; i < sz; i++) {
          // could we inc/dec only for nodes to be constructed?
          ++varLvl;
          n.f1.elementAt(i).accept(this);
          --varLvl;
        }
      } else {
        // a NodeSequence to generate
        final String varName = genNewVarName();
        final VarInfo varInfo = new VarInfo(nodeSequence, varName, "null");
        varList.add(varInfo);
        // genExpSequence(n, varName);
        genExpSequence(n, varInfo);
        lastVar = varInfo;
      }
    }
  }

  /**
   * Visits the {@link Expansion}, and adds the NodeSequence variable declaration for a given identifier, and
   * adds the nodes to the parent.
   * <p>
   * f0 -> ( #0 "LOOKAHEAD" #1 "(" #2 LocalLookahead() #3 ")" )?<br>
   * f1 -> ( ExpansionUnit() )+<br>
   * s: -2134365682<br>
   *
   * @param n - the node to process
   * @param var - the variable info
   */
  @NodeFieldsSignature({
      -2134365682, JTB_SIG_EXPANSION, JTB_USER_EXPANSION
  })
  private void genExpSequence(final Expansion n, final VarInfo var) {
    // f0 -> ( #0 "LOOKAHEAD" #1 "(" #2 LocalLookahead() #3 ")" )?
    // nothing done for f0 (done in visit(Expansion))

    // f1 -> ( ExpansionUnit() )+
    final Iterator<INode> e = n.f1.elements();
    int i = 0;
    // process the first ExpansionUnit, to generate the NodeSequence variable declaration
    final ExpansionUnit firstExpUnit = (ExpansionUnit) e.next();

    if (firstExpUnit.f0.which <= 1) {

      // if the ExpansionUnit is a LOOKAHEAD or a Block, visit it before generating the declaration
      // and do not increment the bnf level
      firstExpUnit.accept(this);
      sb.append(spc.spc);
      genNewNodeSequenceVarDecl(var.getName(), gdbv.getNbSubNodesTbc(n));
      oneNewLine(n, "genExpSequence vardecl lh||b, i = 0");

    } else {

      final boolean oldIsEUNSNodeCreated = isEUNSNodeCreated;
      // the ExpansionUnit is not a LOOKAHEAD nor a Block, generate the declaration and then visit it
      sb.append(spc.spc);
      genNewNodeSequenceVarDecl(var.getName(), gdbv.getNbSubNodesTbc(n));
      oneNewLine(n, "genExpSequence vardecl !(lh||b), i = 0" + ", isEUNSNodeCreated = " + isEUNSNodeCreated
          + " <- false");
      isEUNSNodeCreated = false;
      ++varLvl;
      firstExpUnit.accept(this);
      --varLvl;
      if (isEUNSNodeCreated) { // TESTCASE looks it is always true
        sb.append(spc.spc);
        sb.append(addNodeToParent(var, lastVar));
        oneNewLine(n, "genExpSequence antp c, isEUNSNodeCreated == true, i = 0");
      } else {
        oneDebugClassNewLine(n, "genExpSequence d, isEUNSNodeCreated == false, i = 0");
      }
      isEUNSNodeCreated = oldIsEUNSNodeCreated;
      oneDebugClassNewLine(n, "genExpSequence e, isEUNSNodeCreated <= " + isEUNSNodeCreated);

    }

    // visit the other ExpansionUnits that need to be
    while (e.hasNext()) {
      i++;
      final ExpansionUnit expUnit = (ExpansionUnit) e.next();
      final boolean oldIsEUNSNodeCreated = isEUNSNodeCreated;
      isEUNSNodeCreated = false;
      ++varLvl;
      expUnit.accept(this);
      --varLvl;
      if (expUnit.f0.which > 1) {
        // add node only if not lookahead nor block
        if (isEUNSNodeCreated) {
          sb.append(spc.spc);
          sb.append(addNodeToParent(var, lastVar));
          oneNewLine(n, "genExpSequence antp f, isEUNSNodeCreated == true, , i = " + i);
        } else {
          oneDebugClassNewLine(n, "genExpSequence g, isEUNSNodeCreated == false, , expUnit.f0.which = "
              + expUnit.f0.which + ", i = " + i);
        }
      } else {
        oneDebugClassNewLine(n, "genExpSequence h, expUnit.f0.which = " + expUnit.f0.which + ", i = " + i);
      }
      isEUNSNodeCreated = oldIsEUNSNodeCreated;
      oneDebugClassNewLine(n, "genExpSequence i, isEUNSNodeCreated <= " + isEUNSNodeCreated);
    }
  }

  /**
   * Generates a new NodeSequence variable declaration.
   *
   * @param varName - the variable name
   * @param nbNodes - the number of nodes
   */
  private void genNewNodeSequenceVarDecl(final String varName, final int nbNodes) {
    sb.append("{ ").append(varName).append(" = new NodeSequence(").append(nbNodes).append("); }");
  }

  /**
   * Visits a {@link LocalLookahead} node, whose children are the following :
   * <p>
   * f0 -> [ IntegerLiteral() ]<br>
   * f1 -> [ "," ]<br>
   * f2 -> [ ExpansionChoices() ]<br>
   * f3 -> [ "," ]<br>
   * f4 -> [ #0 "{"<br>
   * .. .. . #1 [ Expression() ]<br>
   * .. .. . #2 "}" ]<br>
   * s: -1879920786<br>
   *
   * @param n - the node to visit
   */
  @Override
  @NodeFieldsSignature({
      -1879920786, JTB_SIG_LOCALLOOKAHEAD, JTB_USER_LOCALLOOKAHEAD
  })
  public void visit(final LocalLookahead n) {
    // Don't want to annotate under
    n.accept(jccpv);
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
  public void visit(final ExpansionUnit n) {
    NodeSequence seq;
    if (isTopExp) {
      curEU++;
    }
    oneDebugClassNewLine(n,
        "isTopExp = " + isTopExp + ", nbEU = " + nbEU + ", curEU = " + curEU + ", varLvl = " + varLvl);

    switch (n.f0.which) {
    case 0:
      // %0 #0 "LOOKAHEAD" #1 "(" #2 LocalLookahead() #3 ")"
      sb.append(spc.spc);
      seq = (NodeSequence) n.f0.choice;
      seq.elementAt(0).accept(this);
      seq.elementAt(1).accept(this);
      sb.append(' ');
      seq.elementAt(2).accept(this);
      sb.append(' ');
      seq.elementAt(3).accept(this);
      oneNewLine(n, "0, LocalLookahead in ExpansionUnit");
      genUserNodeCreation(n);
      return;

    case 1:
      // %1 Block()
      // node(s) must always be created before the last block
      genUserNodeCreation(n);
      genExpUnitCase1Block(n);
      return;

    case 2:
      // %2 #0 "[" #1 ExpansionChoices() #2 "]"
      oneDebugClassNewLine(n, "2a, isEUNSNodeCreated = " + isEUNSNodeCreated);
      genExpUnitCase2Bracket(n);
      genUserNodeCreation(n);
      return;

    case 3:
      // %3 ExpansionUnitTCF()
      oneDebugClassNewLine(n, "3a, isEUNSNodeCreated = " + isEUNSNodeCreated);
      n.f0.choice.accept(this);
      genUserNodeCreation(n);
      return;

    case 4:
      // %4 #0 [ $0 PrimaryExpression() $1 "=" ]
      // .. #1 ( &0 $0 IdentifierAsString() $1 Arguments()
      // .. .. . .. $2 [ "!" ]
      // .. .. | &1 $0 RegularExpression()
      // .. .. . .. $1 [ ?0 "." ?1 < IDENTIFIER > ]
      // .. .. . .. $2 [ "!" ] )
      oneDebugClassNewLine(n, "4a, isEUNSNodeCreated = " + isEUNSNodeCreated);
      genExpUnitCase4IasRe(n);
      genUserNodeCreation(n);
      return;

    case 5:
      // %5 #0 "(" #1 ExpansionChoices() #2 ")"
      // .. #3 ( &0 "+" | &1 "*" | &2 "?" )?
      oneDebugClassNewLine(n, "5a, isEUNSNodeCreated = " + isEUNSNodeCreated);
      genExpUnitCase5Parenth(n);
      genUserNodeCreation(n);
      return;

    default:
      final String msg = "Invalid n.f0.which = " + n.f0.which;
      Messages.hardErr(msg);
      throw new ProgrammaticError(msg);

    }
  }

  /**
   * Generates the user node creation if it is time, ie in the last ExpansionUnit.
   *
   * @param n - the visited ExpansionUnit
   */
  private void genUserNodeCreation(final ExpansionUnit n) {
    oneDebugClassNewLine(n, "genUserNodeCreation isTopExp = " + isTopExp + ", varLvl = " + varLvl + //
        ", nbEU = " + nbEU + ", curEU = " + curEU);
    oneDebugClassNewLine(n, "genUserNodeCreation deferLB = " + deferLB + ", curProd = " + curProd
        + ", resultType = " + resultType + ", needNode = " + needNode());
    if (isTopExp) {
      if (varLvl == 0) {
        if (curEU == nbEU) {
          // print the user node creation
          if (needNode()) {
            sb.append(spc.spc);
            sb.append("{ ").append(jtbNodeVar).append(" = new ").append(curProdFixed).append("(");
            isUserNodeGenerated = true;
            final Iterator<VarInfo> e = outerVars.iterator();
            if (e.hasNext()) {
              sb.append(e.next().getName());
              while (e.hasNext()) { // TESTCASE looks never more than 1 outervar ?
                sb.append(", ").append(e.next().getName());
              }
            }
            sb.append("); }");
            oneNewLine(n, "genUserNodeCreation neednode() == true");
          }
        }
      }
    }
  }

  /**
   * Generates the user node creation.
   *
   * @param n - the visited BNFProduction
   */
  private void genUserNodeCreation(final BNFProduction n) {
    oneDebugClassNewLine(n, "genUserNodeCreation BNF a, isUserNodeGenerated = " + isUserNodeGenerated);
    if (!isUserNodeGenerated) {
      sb.append(spc.spc);
      sb.append("{ ").append(jtbNodeVar).append(" = new ").append(curProdFixed).append("(");
      isUserNodeGenerated = true;
      final Iterator<VarInfo> e = outerVars.iterator();
      if (e.hasNext()) {
        sb.append(e.next().getName());
        while (e.hasNext()) {
          sb.append(", ").append(e.next().getName());
        }
      }
      sb.append("); }");
      oneNewLine(n, "genUserNodeCreation BNF b");
    }
  }

  /**
   * @return true if user node must be declared or is needed, false otherwise (void, primitive type).<br>
   *         note that we do not come here if the node is not to be created.)
   */
  private boolean needNode() {
    if ("void".equals(resultType)) {
      return false;
    }
    if (ptHM.containsKey(resultType)) {
      return false;
    }
    return true;
  }

  /**
   * Generates the last Block creation.
   *
   * @param n - the visited ExpansionUnit
   */
  private void genExpUnitCase1Block(final ExpansionUnit n) {
    oneDebugClassNewLine(n, "genExpUnitCase1Block 1, deferLB = " + deferLB + ", isTopExp = " + isTopExp
        + ", nbEU = " + nbEU + ", curEU = " + curEU);
    final StringBuilder oldSb = sb;
    if (deferLB) {
      if (isTopExp) {
        if (curEU == nbEU) {
          sb = sbLB;
        }
      }
    }
    sb.append(spc.spc);
    n.f0.choice.accept(this);
    oneNewLine(n, "genExpUnitCase1Block 2");
    sb = oldSb;
  }

  /**
   * Generates the ExpansionUnit case 2 (bracketed ExpansionChoices fragment).<br>
   * <p>
   * .. .. | %2 #0 "[" #1 ExpansionChoices() #2 "]"<br>
   * s: 1116287061<br>
   *
   * @param n - the node to process
   */
  @NodeFieldsSignature({
      1116287061, JTB_SIG_EXPANSIONUNIT, JTB_USER_EXPANSIONUNIT
  })
  private void genExpUnitCase2Bracket(final ExpansionUnit n) {
    // (comment different from case 5 but statement similar)
    // #0 "[" #1 ExpansionChoices() #2 "]"
    final NodeSequence seq = (NodeSequence) n.f0.choice;

    // (similar to case 5)
    // #1 ExpansionChoices()
    final ExpansionChoices expCh = (ExpansionChoices) seq.elementAt(1);
    final NodeOptional expLA = expCh.f0.f0;
    final boolean isExpChWithCh = expCh.f1.present();
    final ExpansionUnit firstExpUnit = (ExpansionUnit) expCh.f0.f1.nodes.get(0);
    // count for varList & final actions
    final int nbSubNodesTbc = gdbv.getNbSubNodesTbc(expCh);

    // (comment different from case 5 but statements similar)
    // #0 "["
    sb.append(spc.spc);
    seq.elementAt(0).accept(this);
    oneNewLine(n, "genExpUnitCase2Bracket 1, nbSubNodesTbc = " + nbSubNodesTbc,
        ", expLA.present() = " + expLA.present() + ", isExpChWithCh = " + isExpChWithCh
            + ", firstExpUnit.f0.which = " + firstExpUnit.f0.which);
    spc.updateSpc(+1);

    // (similar to case 5, but with no modifier, so by default is NodeOptional)
    final String varName = genNewVarName();
    VarInfo varInfo = null;
    if (nbSubNodesTbc > 0) {
      if (extraVarsList == null) {
        // top level additional variables
        varInfo = creNewVarInfoForBracket(varName, true);
      } else { // TESTCASE this branch
        // nested additional variables
        varInfo = creNewVarInfoForBracket(varName, false);
        extraVarsList.add(varInfo);
      }
      varList.add(varInfo);
    }
    if (!isExpChWithCh // TESTCASE the other branch
        && expLA.present()) {
      // without an ExpansionChoices choice and with an Expansion Lookahead
      sb.append(spc.spc);
      final NodeSequence seq1 = (NodeSequence) expLA.node;
      seq1.elementAt(0).accept(this);
      seq1.elementAt(1).accept(this);
      sb.append(' ');
      seq1.elementAt(2).accept(this);
      sb.append(' ');
      seq1.elementAt(3).accept(this);
      oneNewLine(n, "genExpUnitCase2Bracket 2");
      // don't print lookahead twice, so remove temporarily the ec.f0.f0 node
      expCh.f0.f0.node = null;
      genExpChoicesInExpUnit(expCh);
      // restore ec.f0.f0 node
      expCh.f0.f0.node = expLA.node;
      // } else if (!isExpChWithCh //
      // && (firstExpUnit.f0.which == 0)) {
      // // without an ExpansionChoices choice and with an ExpansionUnit Lookahead
      // // MMa 02/2017 : I do not see how we could fall into this case as the Lookahead
      // // would have been matched in the Expansion above, so I raise an error
      // Token ntk = (Token) seq.elementAt(0); // #0 "["
      // mess.softErr(
      // "Unexpected case 2 'without an ExpansionChoices choice and with an ExpansionUnit Lookahead'",
      // ntk.beginLine, ntk.beginColumn);
    } else {
      // with an ExpansionChoices choice or without an Expansion Lookahead
      genExpChoicesInExpUnit(expCh);
    }
    if (nbSubNodesTbc > 0) {
      sb.append(spc.spc);
      sb.append(addNodeToParent(varInfo, lastVar));
      isEUNSNodeCreated = true;
      bnfFinalActions(varInfo);
    }
    oneNewLine(n, "genExpUnitCase2Bracket 3");

    // (comment different from case 5, statements similar, 1 char instead of 2)
    // #2 "]"
    spc.updateSpc(-1);
    sb.append(spc.spc);
    seq.elementAt(2).accept(this);
    oneNewLine(n, "genExpUnitCase2Bracket 4");

  }

  /**
   * Generates the ExpansionUnit case 4 (IdentifierAsString or RegularExpression fragment).<br>
   * <p>
   * .. .. | %4 #0 [ $0 PrimaryExpression() $1 "=" ]<br>
   * .. .. . .. #1 ( &0 $0 IdentifierAsString() $1 Arguments()<br>
   * .. .. . .. .. . .. $2 [ "!" ]<br>
   * .. .. . .. .. | &1 $0 RegularExpression()<br>
   * .. .. . .. .. . .. $1 [ ?0 "." ?1 < IDENTIFIER > ]<br>
   * .. .. . .. .. . .. $2 [ "!" ] )<br>
   * s: 1116287061<br>
   *
   * @param n - the node to process
   */
  @NodeFieldsSignature({
      1116287061, JTB_SIG_EXPANSIONUNIT, JTB_USER_EXPANSIONUNIT
  })
  private void genExpUnitCase4IasRe(final ExpansionUnit n) {
    // some common variables
    NodeSequence seq;
    seq = (NodeSequence) n.f0.choice;
    // #0 [ $0 PrimaryExpression() $1 "=" ]
    final NodeOptional opt0 = (NodeOptional) seq.elementAt(0);
    // #1 ...
    final NodeChoice ch = (NodeChoice) seq.elementAt(1);
    final NodeSequence seq1 = (NodeSequence) ch.choice;
    // $2 [ "!" ] (for cases &0 & &1)
    final boolean noDoNotCreate = !((NodeOptional) seq1.elementAt(2)).present();

    // #1 ...
    switch (ch.which) {
    case 0:
      // &0 $0 IdentifierAsString() $1 Arguments() $2 [ "!" ]
      final Token nt = ((IdentifierAsString) seq1.elementAt(0)).f0;
      final String ident = nt.image;
      // $0 IdentifierAsString()
      // generate 'ni = JavaCodeProduction()' if node is to be created, otherwise 'JavaCodeProduction()'
      // generate 'BNFProduction()' if node is not to be created, otherwise 'ni = Production()'
      final boolean creThisIASNode = !gdbv.getNotTbcNodesHM().containsKey(ident) //
          && noDoNotCreate;
      oneDebugClassNewLine(n,
          "genExpUnitCase4IasRe 0a : NotTbcNodesHM().containsKey(ident) = "
              + gdbv.getNotTbcNodesHM().containsKey(ident) + ", noDoNotCreate = " + noDoNotCreate
              + ", creThisIASNode = " + creThisIASNode);
      sb.append(spc.spc);
      if (jopt.printSpecialTokensJJ) {
        final String spStr = nt.getSpecials(spc.spc);
        sb.append(spStr);
      }
      VarInfo varInfo = null;
      if (creThisIASNode) {
        // save return variable
        // #0 [ $0 PrimaryExpression() $1 "=" ]
        if (opt0.present()) {
          // IdentifierAsString() -> jtbrt_Identifier
          final String rt = gdbv.getProdHM().get(ident).substring(1);
          ixOldJtbRt++;
          sb.append("{ ").append(rt).append(' ').append(jtbRtOld).append(ident).append('_').append(ixOldJtbRt)
              .append(" = ").append(jtbRtPrefix).append(ident).append("; }");
          oneNewLine(n, "genExpUnitCase4IasRe 0b");
          sb.append(spc.spc);
        }
        final String varName = genNewVarName();
        varInfo = new VarInfo(ident, varName);
        varList.add(varInfo);
        sb.append(varName).append(" = ");
      } else {
        // #0 [ $0 PrimaryExpression() $1 "=" ]
        if (opt0.present()) {
          opt0.accept(jccpv);
        }
      }
      sb.append(gdbv.getFixedName(ident));
      // $1 Arguments()
      sb.append(genJavaBranch(seq1.elementAt(1)));
      if (creThisIASNode) {
        bnfFinalActions(varInfo);
      }
      oneNewLine(n, "genExpUnitCase4IasRe 0c");
      if (creThisIASNode) {
        if (opt0.present()) {
          sb.append(spc.spc);
          sb.append("{ ");
          // $0 PrimaryExpression()
          final NodeSequence seq2 = (NodeSequence) opt0.node;
          sb.append(genJavaBranch(seq2.elementAt(0)));
          sb.append(' ');
          // $1 "="
          seq2.elementAt(1).accept(this);
          sb.append(' ');
          // IdentifierAsString() -> jtbrt_Identifier
          sb.append(jtbRtPrefix).append(ident).append("; }");
          oneNewLine(n, "genExpUnitCase4IasRe 0d");
        }
        // restore return variable
        if (opt0.present()) {
          sb.append(spc.spc);
          // IdentifierAsString() -> jtbrt_Identifier
          sb.append("{ ").append(jtbRtPrefix).append(ident).append(" = ").append(jtbRtOld).append(ident)
              .append('_').append(ixOldJtbRt).append("; }");
          oneNewLine(n, "genExpUnitCase4IasRe 0e");
        }
      }
      isEUNSNodeCreated = creThisIASNode;
      oneDebugClassNewLine(n, "genExpUnitCase4IasRe 0f, isEUNSNodeCreated <= " + isEUNSNodeCreated
          + ", creThisIASNode = " + creThisIASNode);
      break;

    case 1:
      // &1 $0 RegularExpression() $1 [ ?0 "." ?1 < IDENTIFIER > ] $2 [ "!" ]
      createRENode = noDoNotCreate;
      oneDebugClassNewLine(n, "genExpUnitCase4IasRe 1a : createRENode = " + createRENode);
      // $1 [ ?0 "." ?1 < IDENTIFIER > ]
      final NodeOptional opt1 = (NodeOptional) seq1.elementAt(1);
      if (createRENode) {
        // $0 RegularExpression()
        seq1.elementAt(0).accept(this);
        if (opt0.present()) {
          // above has generated "ni = RegularExpression;" and generate now "{ p = ni; } or { p = ni.id; }"
          sb.append(spc.spc);
          final NodeSequence seq0 = (NodeSequence) opt0.node;
          sb.append("{ ");
          // $0 PrimaryExpression()
          sb.append(genJavaBranch(seq0.elementAt(0)));
          sb.append(' ');
          // $1 "="
          seq0.elementAt(1).accept(this);
          sb.append(' ');
          // variable generated for RegularExpression()
          sb.append(reTokenName);
          // $1 [ ?0 "." ?1 < IDENTIFIER > ]
          if (opt1.present()) {
            sb.append(".");
            ((NodeSequence) opt1.node).elementAt(1).accept(this);
          }
          sb.append("; }");
          oneNewLine(n, "genExpUnitCase4IasRe 1b");
        } else {
          // above has not generated "ni = RegularExpression;", and JavaCC will not allow opt1
          if (opt1.present()) {
            // coverage: we could have added a testcase, but we would have to manage the JJ compile failure
            final Token ntk = (Token) ((NodeSequence) opt1.node).elementAt(0); // ?0 "."
            mess.softErr(
                "JavaCC will not allow RegularExpression.IDENTIFIER without assigning it to a PrimaryExpression",
                ntk.beginLine, ntk.beginColumn);
          }
        }
      } else {
        // no node to create, do not annotate under
        sb.append(spc.spc);
        // #0 [ $0 PrimaryExpression() $1 "=" ]
        if (opt0.present()) {
          final NodeSequence seq0 = (NodeSequence) opt0.node;
          // $0 PrimaryExpression()
          sb.append(genJavaBranch(seq0.elementAt(0)));
          sb.append(' ');
          // $1 "="
          seq0.elementAt(1).accept(this);
          sb.append(' ');
        }
        // $0 RegularExpression()
        seq1.elementAt(0).accept(jccpv);
        // $1 [ ?0 "." ?1 < IDENTIFIER > ]
        if (opt1.present()) {
          sb.append(".");
          ((NodeSequence) opt1.node).elementAt(1).accept(this);
        }
        oneNewLine(n, "genExpUnitCase4IasRe 1d");
      }
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
  @NodeFieldsSignature({
      1601707097, JTB_SIG_EXPANSIONUNITTCF, JTB_USER_EXPANSIONUNITTCF
  })
  public void visit(final ExpansionUnitTCF n) {
    // f0 -> "try"
    sb.append(spc.spc);
    n.f0.accept(this);
    sb.append(' ');
    // f1 -> "{"
    n.f1.accept(this);
    spc.updateSpc(+1);
    oneNewLine(n, "a");

    // f2 -> ExpansionChoices()
    n.f2.accept(this);
    oneDebugClassNewLine(n, "b");
    spc.updateSpc(-1);
    sb.append(spc.spc);

    // f3 -> "}"
    n.f3.accept(this);
    // f4 -> ( #0 "catch" #1 "("
    // .. .. . #2 ( Annotation() )*
    // .. .. . #3 [ "final" ]
    // .. .. . #4 Name() #5 < IDENTIFIER > #6 ")" #7 Block() )*
    if (n.f4.present()) {
      for (final INode e : n.f4.nodes) {
        final NodeSequence seq = (NodeSequence) e;
        oneNewLine(n, "c");
        sb.append(spc.spc);
        // #0 "catch"
        seq.elementAt(0).accept(this);
        sb.append(' ');
        // #1 "("
        seq.elementAt(1).accept(this);
        // #2 ( Annotation() )*
        final NodeListOptional nlo = (NodeListOptional) seq.elementAt(2);
        if (nlo.present()) {
          // JavaCC does not accept Annotations for the moment
          sb.append("/* ");
          for (int i = 0; i < nlo.size(); i++) {
            final INode nloeai = nlo.elementAt(i);
            nloeai.accept(this);
            sb.append(" ");
          }
          sb.append("*/ ");
        }
        // #3 [ "final" ]
        if (((NodeOptional) seq.elementAt(3)).present()) {
          // JavaCC does not accept final for the moment
          sb.append("/* ");
          seq.elementAt(3).accept(this);
          sb.append(" */ ");
        }
        // #4 Name()
        seq.elementAt(4).accept(this);
        sb.append(' ');
        // #5 < IDENTIFIER >
        seq.elementAt(5).accept(this);
        // #6 ")"
        seq.elementAt(6).accept(this);
        sb.append(' ');
        // #7 Block()
        seq.elementAt(7).accept(this);
      }
    }
    // f5 -> [ #0 "finally" #1 Block() ]
    if (n.f5.present()) {
      final NodeSequence seq = (NodeSequence) n.f5.node;
      oneNewLine(n, "d");
      sb.append(spc.spc);
      // #0 "finally"
      seq.elementAt(0).accept(this);
      sb.append(' ');
      // #1 Block()
      seq.elementAt(1).accept(this);
    }
    oneNewLine(n, "e");
  }

  /**
   * Generates the ExpansionUnit case 5 (parenthesized ExpansionChoices fragment).<br>
   * <p>
   * .. .. | %5 #0 "(" #1 ExpansionChoices() #2 ")"<br>
   * .. .. . .. #3 ( &0 "+"<br>
   * .. .. . .. .. | &1 "*"<br>
   * .. .. . .. .. | &2 "?" )?<br>
   * s: 1116287061<br>
   *
   * @param n - the node to process
   */
  @NodeFieldsSignature({
      1116287061, JTB_SIG_EXPANSIONUNIT, JTB_USER_EXPANSIONUNIT
  })
  private void genExpUnitCase5Parenth(final ExpansionUnit n) {
    // (comment different from case 2 but statement similar)
    // #0 "(" #1 ExpansionChoices() #2 ")" #3 ( &0 "+" | &1 "*" | &2 "?" )?
    final NodeSequence seq = (NodeSequence) n.f0.choice;

    // (similar to case 2)
    // #1 ExpansionChoices()
    final ExpansionChoices expCh = (ExpansionChoices) seq.elementAt(1);
    final NodeOptional expLA = expCh.f0.f0;
    final boolean isExpChWithCh = expCh.f1.present();
    final ExpansionUnit firstExpUnit = (ExpansionUnit) expCh.f0.f1.nodes.get(0);
    // count for varList & final actions
    final int nbSubNodesTbc = gdbv.getNbSubNodesTbc(expCh);

    // (comment different from case 2 but statements similar)
    // "("
    sb.append(spc.spc);
    seq.elementAt(0).accept(this);
    oneNewLine(n, "genExpUnitCase5Parenth 1, nbSubNodesTbc = " + nbSubNodesTbc,
        ", expLA.present() = " + expLA.present() + ", isExpChWithCh = " + isExpChWithCh
            + ", firstExpUnit.f0.which = " + firstExpUnit.f0.which);
    spc.updateSpc(+1);

    // (this test - modifier present - and first branch - yes - do not exist en case 2)
    // #3 ( &0 "+" | &1 "*" | &2 "?" )?
    final NodeOptional nlo = (NodeOptional) seq.elementAt(3);
    if (!nlo.present()) {

      // No BNF modifier present, so generate a NodeChoice or a NodeSequence
      if (isExpChWithCh) {
        // ExpansionChoice with choices, so generate a NodeChoice
        expCh.accept(this);
      } else {
        // ExpansionChoice with no choices
        if (nbSubNodesTbc > 0) {
          // generate a NodeSequence (of 1 or more nodes)
          final String varName = genNewVarName();
          final VarInfo varInfo = new VarInfo(nodeSequence, varName);
          varList.add(varInfo);
          sb.append(spc.spc);
          genExpSequence(expCh.f0, varInfo);
          bnfFinalActions(varInfo);
          oneNewLine(n, "genExpUnitCase5Parenth 2");
        } else {
          // don't annotate under
          expCh.accept(jccpv);
        }
      }
      // ")"
      spc.updateSpc(-1);
      sb.append(spc.spc);
      seq.elementAt(2).accept(this);
      oneNewLine(n, "genExpUnitCase5Parenth 3");

    } else {

      // a BNF modifier is present so generate the appropriate structure
      // #3 ( &0 "+" | &1 "*" | &2 "?" )?
      final NodeChoice ch = (NodeChoice) nlo.node;

      // (similar to case 2, but with modifier)
      final String varName = genNewVarName();
      VarInfo varInfo = null;
      if (nbSubNodesTbc > 0) {
        if (extraVarsList == null) {
          // top level additional variable
          varInfo = creNewVarInfoForMod(ch, varName, true);
        } else {
          // nested additional variable
          varInfo = creNewVarInfoForMod(ch, varName, false);
          extraVarsList.add(varInfo);
        }
        varList.add(varInfo);
      }
      if (!isExpChWithCh //
          && expLA.present()) {
        // without an ExpansionChoices choice and with an Expansion Lookahead
        sb.append(spc.spc);
        final NodeSequence seq1 = (NodeSequence) expLA.node;
        seq1.elementAt(0).accept(this);
        seq1.elementAt(1).accept(this);
        sb.append(' ');
        seq1.elementAt(2).accept(this);
        sb.append(' ');
        seq1.elementAt(3).accept(this);
        oneNewLine(n, "genExpUnitCase5Parenth 4");
        // don't print lookahead twice, so remove temporarily the ec.f0.f0 node
        expCh.f0.f0.node = null;
        genExpChoicesInExpUnit(expCh);
        // restore ec.f0.f0 node
        expCh.f0.f0.node = expLA.node;
        // } else if (!isExpChWithCh //
        // && (firstExpUnit.f0.which == 0)) {
        // // without an ExpansionChoices choice and with an ExpansionUnit Lookahead
        // // MMa 02/2017 : I do not see how we could fall into this case as the Lookahead
        // // would have been matched above in the Expansion, so I raise an error
        // // %0 #0 "LOOKAHEAD" #1 "(" #2 LocalLookahead() #3 ")"
        // Token ntk = (Token) ((NodeSequence) firstExpUnit.f0.choice).elementAt(0);
        // mess.softErr(
        // "Unexpected case 5 'without an ExpansionChoices choice and with an ExpansionUnit Lookahead'",
        // ntk.beginLine, ntk.beginColumn);
      } else {
        // with an ExpansionChoices choice or without an Expansion Lookahead
        genExpChoicesInExpUnit(expCh);
      }
      if (nbSubNodesTbc > 0) {
        sb.append(spc.spc);
        sb.append(addNodeToParent(varInfo, lastVar));
        oneNewLine(n, "genExpUnitCase5Parenth 6");
        isEUNSNodeCreated = true;
        bnfFinalActions(varInfo);
      }

      // (comment different from case 2, statements similar, 2 chars instead of 1)
      // ")"
      spc.updateSpc(-1);
      sb.append(spc.spc);
      seq.elementAt(2).accept(this);
      // "+" or "*" or "?"
      ch.choice.accept(this);
      oneNewLine(n, "genExpUnitCase5Parenth 7");

      // (does not exist in case 2)
      if (nbSubNodesTbc > 0) {
        if (ch.which != 2) {
          sb.append(spc.spc);
          sb.append("{ ").append(varName).append(".nodes.trimToSize(); }");
          oneNewLine(n, "genExpUnitCase5Parenth 8");
        }
      }
    }
  }

  /**
   * Common code for generating the ExpansionChoices in choices 2, 3 and 5 of ExpansionUnit.
   * <p>
   * f0 -> Expansion()<br>
   * f1 -> ( #0 "|" #1 Expansion() )*<br>
   * s: -1726831935<br>
   *
   * @param n - the node to process
   */
  @NodeFieldsSignature({
      -1726831935, JTB_SIG_EXPANSIONCHOICES, JTB_USER_EXPANSIONCHOICES
  })
  private void genExpChoicesInExpUnit(final ExpansionChoices n) {
    // put apart "main" buffer, create a new temporary buffer to generate a list of extra variables nested
    // into
    final StringBuilder mainSB = sb;
    final StringBuilder tempSB = new StringBuilder(512);
    sb = jccpv.sb = tempSB;
    // put apart "extraVarsList", create a new "tempExtraVarsList" to generate a new list
    final List<VarInfo> tempExtraVarsList = extraVarsList;
    extraVarsList = new ArrayList<>();
    // new buffer will be fed by this ExpansionChoices accept
    ++varLvl;
    n.accept(this);
    --varLvl;
    if (!extraVarsList.isEmpty()) {
      // we have nested extra variables, generate them in the "main" buffer
      for (final VarInfo e : extraVarsList) {
        mainSB.append(spc.spc);
        genNewNodeOptOrListOrListOptVarDecl(mainSB, e);
        oneNewLine(mainSB, n, "genExpChoicesInExpUnit");
      }
    }
    // restore original "main" buffer and extraVarsList
    sb = jccpv.sb = mainSB;
    extraVarsList = tempExtraVarsList;
    // print temporary buffer
    sb.append(tempSB);
  }

  /**
   * Generates a new NodeOptional or NodeList or NodeListOptional variable declaration.
   *
   * @param aSb - the buffer to print into
   * @param aVarInfo - the VarInfo variable
   */
  private static void genNewNodeOptOrListOrListOptVarDecl(final StringBuilder aSb, final VarInfo aVarInfo) {
    aSb.append("{ ").append(aVarInfo.getName()).append(" = new ").append(aVarInfo.getType()).append("(); }");
  }

  /**
   * Creates a new VarInfo object (with the appropriate node type) for a given BNF modifier.
   *
   * @param modifier - the modifier (which directs the node type)
   * @param varName - the variable name to store
   * @param initialize - the need to initialize flag
   * @return the new VarInfo object
   */
  private VarInfo creNewVarInfoForMod(final NodeChoice modifier, final String varName,
      final boolean initialize) {
    if (initialize) {
      if (modifier.which == 0) {
        return new VarInfo(nodeList, varName, "new ".concat(nodeList).concat("()"));
      } else if (modifier.which == 1) {
        return new VarInfo(nodeListOptional, varName, "new ".concat(nodeListOptional).concat("()"));
      } else if (modifier.which == 2) {
        return new VarInfo(nodeOptional, varName, "new ".concat(nodeOptional).concat("()"));
      } else {
        final String msg = "Illegal EBNF modifier: '" + modifier.choice.toString() + "'.";
        Messages.hardErr(msg);
        throw new ProgrammaticError(msg);
      }
    } else if (modifier.which == 0) {
      return new VarInfo(nodeList, varName);
    } else if (modifier.which == 1) {
      return new VarInfo(nodeListOptional, varName);
    } else if (modifier.which == 2) {
      return new VarInfo(nodeOptional, varName);
    } else {
      final String msg = "Illegal EBNF modifier: '" + modifier.choice.toString() + "'.";
      Messages.hardErr(msg);
      throw new ProgrammaticError(msg);
    }
  }

  /**
   * Creates a new VarInfo object for a NodeOptional node.
   *
   * @param varName - the variable name to store
   * @param initializer - the initializer presence flag
   * @return the new VarInfo object
   */
  private VarInfo creNewVarInfoForBracket(final String varName, final boolean initializer) {
    if (initializer) {
      return new VarInfo(nodeOptional, varName, "new ".concat(nodeOptional).concat("()"));
    } else { // TESTCASE see the call in genExpUnitCase2Bracket
      return new VarInfo(nodeOptional, varName);
    }
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
    // // find if the node must be created
    // if we come here, createRENode is true (in the caller genExpUnitCase4IasRe(), if createRENode is false,
    // we use JavaCCPrinter to not annotate the node)
    boolean creThisRENode = true;
    if (n.f0.which == 2) {
      // %2 #0 "<" #1 IdentifierAsString() #2 ">"
      final NodeSequence seq1 = (NodeSequence) n.f0.choice;
      // create the node only if not requested not to do so
      final String ident = ((IdentifierAsString) seq1.elementAt(1)).f0.image;
      if (DONT_CREATE.equals(gdbv.getTokenHM().get(ident))) {
        creThisRENode = false;
      }
    }
    VarInfo tokenNameInfo = null;
    sb.append(spc.spc);
    // if the node must be created, create the variable which will be inserted after the specials
    // down further in the tree
    if (creThisRENode) {
      reTokenName = genNewVarName();
      tokenNameInfo = new VarInfo(jjToken, reTokenName);
      varList.add(tokenNameInfo);
      jccpv.gvaStr = reTokenName + " = ";
      lastVar = tokenNameInfo;
    }

    switch (n.f0.which) {
    case 0:
      // %0 StringLiteral()
      n.f0.choice.accept(jccpv);
      oneNewLine(n, "0a");
      break;

    case 1:
      // %1 #0 "<" #1 [ $0 [ "#" ] $1 IdentifierAsString() $2 ":" ]
      // #2 ComplexRegularExpressionChoices() #3 ">"
      final NodeSequence seq = (NodeSequence) n.f0.choice;
      final ComplexRegularExpressionChoices crec = (ComplexRegularExpressionChoices) seq.elementAt(2);
      // #0 "<"
      seq.elementAt(0).accept(jccpv);
      sb.append(' ');
      // #1 [ $0 [ "#" ] $1 IdentifierAsString() $2 ":" ]
      final NodeOptional opt = (NodeOptional) seq.elementAt(1);
      if (opt.present()) {
        final NodeSequence seq1 = (NodeSequence) opt.node;
        // $0 [ "#" ]
        // case never occurring, as JavaCC does not accept private regular expression within the grammar,
        // only within RegExprSpec (which is managed by JavaCCPrinter)
        // $1 IdentifierAsString()
        seq1.elementAt(1).accept(jccpv);
        sb.append(' ');
        // $2 ":"
        seq1.elementAt(2).accept(jccpv);
        if (crec.f1.present()) {
          oneNewLine(n, "1a");
          spc.updateSpc(+1);
          sb.append(spc.spc).append("  ");
        } else {
          sb.append(' ');
        }
      }
      // #2 ComplexRegularExpressionChoices()
      crec.accept(jccpv);
      // #3 ">"
      if (crec.f1.present()) {
        oneNewLine(n, "1b");
        spc.updateSpc(-1);
        sb.append(spc.spc);
      } else {
        sb.append(' ');
      }
      seq.elementAt(3).accept(jccpv);
      oneNewLine(n, "1c");
      break;

    case 2:
      // %2 #0 "<" #1 IdentifierAsString() #2 ">"
      final NodeSequence seq1 = (NodeSequence) n.f0.choice;
      // print the production in all cases
      // #0 "<"
      seq1.elementAt(0).accept(jccpv);
      sb.append(' ');
      // #1 IdentifierAsString()
      seq1.elementAt(1).accept(jccpv);
      sb.append(' ');
      // #2 ">"
      seq1.elementAt(2).accept(jccpv);
      oneNewLine(n, "2e");
      break;

    case 3:
      // %3 #0 "<" #1 "EOF" #2 ">"
      final NodeSequence seq2 = (NodeSequence) n.f0.choice;
      final Token nt = (Token) seq2.elementAt(0);
      if (jopt.printSpecialTokensJJ) {
        sb.append(nt.getSpecials(spc.spc));
      }
      // JTBParser.jtb does not offer to not build the < EOF > token as a node
      sb.append(spc.spc);
      sb.append(reTokenName).append(" = ");
      jccpv.gvaStr = null;
      sb.append("< EOF >");
      oneNewLine(n, "3eof");
      sb.append(spc.spc);
      sb.append("{ ").append(reTokenName).append(".beginColumn++; }");
      oneNewLine(n, "3h");
      sb.append(spc.spc);
      sb.append("{ ").append(reTokenName).append(".endColumn++; }");
      oneNewLine(n, "3i");
      break;

    default:
      final String msg = "Invalid n.f0.which = " + n.f0.which;
      Messages.hardErr(msg);
      throw new ProgrammaticError(msg);

    }
    if (creThisRENode) {
      bnfFinalActions(tokenNameInfo);
    }
    isEUNSNodeCreated = creThisRENode;
    oneDebugClassNewLine(n,
        "re, isEUNSNodeCreated <= " + isEUNSNodeCreated + ", creThisRENode = " + creThisRENode);
  }

  /**
   * Visits a {@link Statement} node, whose child is the following :
   * <p>
   * f0 -> . %00 LabeledStatement()<br>
   * .. .. | %01 AssertStatement()<br>
   * .. .. | %02 Block()<br>
   * .. .. | %03 EmptyStatement()<br>
   * .. .. | %04 #0 StatementExpression() #1 ";"<br>
   * .. .. | %05 SwitchStatement()<br>
   * .. .. | %06 IfStatement()<br>
   * .. .. | %07 WhileStatement()<br>
   * .. .. | %08 DoStatement()<br>
   * .. .. | %09 ForStatement()<br>
   * .. .. | %10 BreakStatement()<br>
   * .. .. | %11 ContinueStatement()<br>
   * .. .. | %12 ReturnStatement()<br>
   * .. .. | %13 ThrowStatement()<br>
   * .. .. | %14 SynchronizedStatement()<br>
   * .. .. | %15 TryStatement()<br>
   * s: 1394695492<br>
   *
   * @param n - the node to visit
   */
  @Override
  @NodeFieldsSignature({
      1394695492, JTB_SIG_STATEMENT, JTB_USER_STATEMENT
  })
  public void visit(final Statement n) {
    switch (n.f0.which) {
    case 1:
    case 3:
    case 4:
    case 10:
    case 11:
    case 13:
      // all statements which do not lead to a ReturnStatement()
      sb.append(genJavaBranch(n.f0.choice));
      break;
    default:
      // others
      n.f0.choice.accept(this);
    }
  }

  /**
   * Visits a {@link LabeledStatement} node, whose children are the following :
   * <p>
   * f0 -> < IDENTIFIER ><br>
   * f1 -> ":"<br>
   * f2 -> Statement()<br>
   * s: -1956923191<br>
   *
   * @param n - the node to visit
   */
  @Override
  @NodeFieldsSignature({
      -1956923191, JTB_SIG_LABELEDSTATEMENT, JTB_USER_LABELEDSTATEMENT
  })
  public void visit(final LabeledStatement n) {
    // f0 -> < IDENTIFIER >
    n.f0.accept(this);
    // f1 -> ":"
    n.f1.accept(this);
    oneNewLine(n);
    sb.append(spc.spc);
    // f2 -> Statement()
    n.f2.accept(this);
  }

  /**
   * Visits a {@link Block} node, whose children are the following :
   * <p>
   * f0 -> "{"<br>
   * f1 -> ( BlockStatement() )*<br>
   * f2 -> "}"<br>
   * s: -47169424<br>
   *
   * @param n - the node to visit
   */
  @Override
  @NodeFieldsSignature({
      -47169424, JTB_SIG_BLOCK, JTB_USER_BLOCK
  })
  public void visit(final Block n) {
    // f0 -> "{"
    n.f0.accept(this);
    // f1 -> ( BlockStatement() )*
    if (n.f1.present()) {
      oneNewLine(n, "x");
      spc.updateSpc(+1);
      for (final Iterator<INode> e = n.f1.elements(); e.hasNext();) {
        // BlockStatement()
        sb.append(spc.spc);
        e.next().accept(this);
        oneNewLine(n, "y");
      }
      spc.updateSpc(-1);
      sb.append(spc.spc);
    }
    // f2 -> "}"
    n.f2.accept(this);
  }

  /**
   * Visits a {@link BlockStatement} node, whose child is the following :
   * <p>
   * f0 -> . %0 #0 LocalVariableDeclaration() #1 ";"<br>
   * .. .. | %1 Statement()<br>
   * .. .. | %2 ClassOrInterfaceDeclaration()<br>
   * s: -1009630136<br>
   *
   * @param n - the node to visit
   */
  @Override
  @NodeFieldsSignature({
      -1009630136, JTB_SIG_BLOCKSTATEMENT, JTB_USER_BLOCKSTATEMENT
  })
  public void visit(final BlockStatement n) {
    if (n.f0.which != 2) {
      // %0 #0 LocalVariableDeclaration() #1 ";" | %1 Statement()
      n.f0.choice.accept(this);
    } else {
      // %2 ClassOrInterfaceDeclaration()
      sb.append(genJavaBranch(n.f0.choice));
    }
  }

  /**
   * Visits a {@link LocalVariableDeclaration} node, whose children are the following :
   * <p>
   * f0 -> VariableModifiers()<br>
   * f1 -> Type()<br>
   * f2 -> VariableDeclarator()<br>
   * f3 -> ( #0 "," #1 VariableDeclarator() )*<br>
   * s: 225808290<br>
   *
   * @param n - the node to visit
   */
  @Override
  @NodeFieldsSignature({
      225808290, JTB_SIG_LOCALVARIABLEDECLARATION, JTB_USER_LOCALVARIABLEDECLARATION
  })
  public void visit(final LocalVariableDeclaration n) {
    // f0 -> VariableModifiers()
    n.f0.accept(this);
    // VariableModifiers print the last space if not empty
    // f1 -> Type()
    sb.append(genJavaBranch(n.f1));
    sb.append(' ');
    // f2 -> VariableDeclarator()
    sb.append(genJavaBranch(n.f2));
    // f3 -> ( #0 "," #1 VariableDeclarator() )*
    if (n.f3.present()) {
      for (int i = 0; i < n.f3.size(); i++) {
        final NodeSequence seq = (NodeSequence) n.f3.elementAt(i);
        // #0 ","
        sb.append(", ");
        // #1 VariableDeclarator()
        sb.append(genJavaBranch(seq.elementAt(1)));
      }
    }
  }

  /**
   * Visits a {@link VariableModifiers} node, whose child is the following :
   * <p>
   * f0 -> ( ( %0 "final"<br>
   * .. .. . | %1 Annotation() ) )*<br>
   * s: 2076055340<br>
   *
   * @param n - the node to visit
   */
  @Override
  @NodeFieldsSignature({
      2076055340, JTB_SIG_VARIABLEMODIFIERS, JTB_USER_VARIABLEMODIFIERS
  })
  public void visit(final VariableModifiers n) {
    // f0 -> ( ( %0 "final"
    // .. .. . | %1 Annotation() ) )*
    if (n.f0.present()) {
      for (final INode e : n.f0.nodes) {
        e.accept(this);
        // VariableModifiers print the last space if not empty
        sb.append(' ');
      }
    }
  }

  /**
   * Visits a {@link TryStatement} node, whose children are the following :
   * <p>
   * f0 -> "try"<br>
   * f1 -> Block()<br>
   * f2 -> ( #0 "catch" #1 "(" #2 FormalParameter() #3 ")" #4 Block() )*<br>
   * f3 -> [ #0 "finally" #1 Block() ]<br>
   * s: 1108527850<br>
   *
   * @param n - the node to visit
   */
  @Override
  @NodeFieldsSignature({
      1108527850, JTB_SIG_TRYSTATEMENT, JTB_USER_TRYSTATEMENT
  })
  public void visit(final TryStatement n) {
    // f0 -> "try"
    n.f0.accept(this);
    sb.append(' ');
    // f1 -> Block()
    n.f1.accept(this);
    // f2 -> ( #0 "catch" #1 "(" #2 FormalParameter() #3 ")" #4 Block() )*
    for (final INode e : n.f2.nodes) {
      final NodeSequence seq = (NodeSequence) e;
      oneNewLine(n);
      sb.append(spc.spc);
      // #0 "catch"
      seq.elementAt(0).accept(this);
      sb.append(' ');
      // #1 "("
      seq.elementAt(1).accept(this);
      // #2 FormalParameter()
      sb.append(genJavaBranch(seq.elementAt(2)));
      // #3 ")"
      seq.elementAt(3).accept(this);
      sb.append(' ');
      // #4 Block()
      seq.elementAt(4).accept(this);
    }
    // f3 -> [ #0 "finally" #1 Block() ]
    if (n.f3.present()) {
      final NodeSequence seq = (NodeSequence) n.f3.node;
      oneNewLine(n);
      sb.append(spc.spc);
      // #0 "finally"
      seq.elementAt(0).accept(this);
      sb.append(' ');
      // #1 Block(
      seq.elementAt(1).accept(this);
    }
  }

  /**
   * Visits a {@link SwitchStatement} node, whose children are the following :
   * <p>
   * f0 -> "switch"<br>
   * f1 -> "("<br>
   * f2 -> Expression()<br>
   * f3 -> ")"<br>
   * f4 -> "{"<br>
   * f5 -> ( #0 SwitchLabel()<br>
   * .. .. . #1 ( BlockStatement() )* )*<br>
   * f6 -> "}"<br>
   * s: 645895087<br>
   *
   * @param n - the node to visit
   */
  @Override
  @NodeFieldsSignature({
      645895087, JTB_SIG_SWITCHSTATEMENT, JTB_USER_SWITCHSTATEMENT
  })
  public void visit(final SwitchStatement n) {
    // f0 -> "switch"
    n.f0.accept(this);
    sb.append(' ');
    // f1 -> "("
    n.f1.accept(this);
    // f2 -> Expression()
    sb.append(genJavaBranch(n.f2));
    // f3 -> ")"
    n.f3.accept(this);
    sb.append(' ');
    // f4 -> "{"
    n.f4.accept(this);
    spc.updateSpc(+1);
    oneNewLine(n, "a");
    // f5 -> ( #0 SwitchLabel() #1 ( BlockStatement() )* )*
    for (final INode e : n.f5.nodes) {
      final NodeSequence seq = (NodeSequence) e;
      // #0 SwitchLabel()
      sb.append(spc.spc);
      sb.append(genJavaBranch(seq.elementAt(0)));
      spc.updateSpc(+1);
      // #1 ( BlockStatement() )* )*
      final NodeListOptional nlo = (NodeListOptional) seq.elementAt(1);
      if (nlo.present()) {
        if (nlo.size() == 1) {
          sb.append(' ');
        } else {
          oneNewLine(n, "b");
          sb.append(spc.spc);
        }
        for (final Iterator<INode> e1 = nlo.elements(); e1.hasNext();) {
          // BlockStatement()
          e1.next().accept(this);
          if (e1.hasNext()) {
            oneNewLine(n, "c");
            sb.append(spc.spc);
          }
        }
      }
      oneNewLine(n, "d");
      spc.updateSpc(-1);
    }
    spc.updateSpc(-1);
    sb.append(spc.spc);
    // f6 -> "}"
    n.f6.accept(this);
  }

  /**
   * Visits a {@link IfStatement} node, whose children are the following :
   * <p>
   * f0 -> "if"<br>
   * f1 -> "("<br>
   * f2 -> Expression()<br>
   * f3 -> ")"<br>
   * f4 -> Statement()<br>
   * f5 -> [ #0 "else" #1 Statement() ]<br>
   * s: -1906079982<br>
   *
   * @param n - the node to visit
   */
  @Override
  @NodeFieldsSignature({
      -1906079982, JTB_SIG_IFSTATEMENT, JTB_USER_IFSTATEMENT
  })
  public void visit(final IfStatement n) {
    // f0 -> "if"
    n.f0.accept(this);
    sb.append(' ');
    // f1 -> "("
    n.f1.accept(this);
    // f2 -> Expression()
    sb.append(genJavaBranch(n.f2));
    // f3 -> ")"
    n.f3.accept(this);
    // f4 -> Statement()
    if (n.f4.f0.which != 2) { // "if" Statement() is not a Block
      oneNewLine(n, "a");
      spc.updateSpc(+1);
      sb.append(spc.spc);
    } else {
      sb.append(' ');
    }
    n.f4.accept(this);
    if (n.f4.f0.which != 2) { // "if" Statement() is not a Block
      spc.updateSpc(-1);
    }
    // f5 -> [ #0 "else" #1 Statement() ]
    if (n.f5.present()) {
      final NodeSequence seq = (NodeSequence) n.f5.node;
      if (n.f4.f0.which != 2) { // "if" Statement() is not a Block
        oneNewLine(n, "b");
        sb.append(spc.spc);
      } else {
        sb.append(' ');
      }
      // #0 "else"
      seq.elementAt(0).accept(this);
      // #1 Statement()
      final Statement st = (Statement) seq.elementAt(1);
      if (st.f0.which != 2) {
        // "else" Statement() is not a Block()
        oneNewLine(n, "c");
        spc.updateSpc(+1);
        sb.append(spc.spc);
        // #1 Statement()
        st.accept(this);
        spc.updateSpc(-1);
      } else {
        // "else" Statement() is a Block()
        sb.append(' ');
        // #1 Statement()
        st.accept(this);
      }
    }
  }

  /**
   * Visits a {@link WhileStatement} node, whose children are the following :
   * <p>
   * f0 -> "while"<br>
   * f1 -> "("<br>
   * f2 -> Expression()<br>
   * f3 -> ")"<br>
   * f4 -> Statement()<br>
   * s: 503551312<br>
   *
   * @param n - the node to visit
   */
  @Override
  @NodeFieldsSignature({
      503551312, JTB_SIG_WHILESTATEMENT, JTB_USER_WHILESTATEMENT
  })
  public void visit(final WhileStatement n) {
    // f0 -> "while"
    n.f0.accept(this);
    sb.append(' ');
    // f1 -> "("
    n.f1.accept(this);
    // f2 -> Expression()
    sb.append(genJavaBranch(n.f2));
    // f3 -> ")"
    n.f3.accept(this);
    // f4 -> Statement()
    genStatement(n.f4);
  }

  /**
   * Visits a {@link DoStatement} node, whose children are the following :
   * <p>
   * f0 -> "do"<br>
   * f1 -> Statement()<br>
   * f2 -> "while"<br>
   * f3 -> "("<br>
   * f4 -> Expression()<br>
   * f5 -> ")"<br>
   * f6 -> ";"<br>
   * s: 1162769715<br>
   *
   * @param n - the node to visit
   */
  @Override
  @NodeFieldsSignature({
      1162769715, JTB_SIG_DOSTATEMENT, JTB_USER_DOSTATEMENT
  })
  public void visit(final DoStatement n) {
    // f0 -> "do"
    n.f0.accept(this);
    // f1 -> Statement()
    genStatement(n.f1);
    // f2 -> "while"
    n.f2.accept(this);
    sb.append(' ');
    // f3 -> "("
    n.f3.accept(this);
    // f4 -> Expression()
    sb.append(genJavaBranch(n.f4));
    // f5 -> ")"
    n.f5.accept(this);
    // f6 -> ";"
    n.f6.accept(this);
  }

  /**
   * Visits a {@link ForStatement} node, whose children are the following :
   * <p>
   * f0 -> "for"<br>
   * f1 -> "("<br>
   * f2 -> ( %0 #0 VariableModifiers() #1 Type() #2 < IDENTIFIER > #3 ":" #4 Expression()<br>
   * .. .. | %1 #0 [ ForInit() ]<br>
   * .. .. . .. #1 ";"<br>
   * .. .. . .. #2 [ Expression() ]<br>
   * .. .. . .. #3 ";"<br>
   * .. .. . .. #4 [ ForUpdate() ] )<br>
   * f3 -> ")"<br>
   * f4 -> Statement()<br>
   * s: 755358653<br>
   *
   * @param n - the node to visit
   */
  @Override
  @NodeFieldsSignature({
      755358653, JTB_SIG_FORSTATEMENT, JTB_USER_FORSTATEMENT
  })
  public void visit(final ForStatement n) {
    // f0 -> "for"
    n.f0.accept(this);
    sb.append(' ');
    // f1 -> "("
    n.f1.accept(this);
    // f2 -> ( %0 #0 VariableModifiers() #1 Type() #2 < IDENTIFIER > #3 ":" #4 Expression() | %1 #0 [
    // ForInit() ] #1 ";" #2 [ Expression() ] #3 ";" #4 [ ForUpdate() ] )
    final NodeSequence seq = (NodeSequence) n.f2.choice;
    if (n.f2.which == 0) {
      // %0 #0 VariableModifiers() #1 Type() #2 < IDENTIFIER > #3 ":" #4 Expression()
      // #0 VariableModifiers print the last space if not empty
      sb.append(genJavaBranch(seq.elementAt(0)));
      // #1 Type()
      sb.append(genJavaBranch(seq.elementAt(1)));
      sb.append(' ');
      // #2 < IDENTIFIER >
      seq.elementAt(2).accept(this);
      // #3 ":"
      sb.append(" : ");
      // #4 Expression()
      sb.append(genJavaBranch(seq.elementAt(4)));
    } else {
      NodeOptional opt;
      // %1 #0 [ ForInit() ] #1 ";" #2 [ Expression() ] #3 ";" #4 [ ForUpdate() ]
      // #0 [ ForInit() ]
      opt = (NodeOptional) seq.elementAt(0);
      if (opt.present()) {
        sb.append(genJavaBranch(opt.node));
      }
      // #1 ";"
      seq.elementAt(1).accept(this);
      sb.append(' ');
      // #2 [ Expression() ]
      opt = (NodeOptional) seq.elementAt(2);
      if (opt.present()) {
        sb.append(genJavaBranch(opt.node));
      }
      // #3 ";"
      seq.elementAt(3).accept(this);
      sb.append(' ');
      // #4 [ ForUpdate() ]
      opt = (NodeOptional) seq.elementAt(4);
      if (opt.present()) {
        sb.append(genJavaBranch(opt.node));
      }
    }
    // f3 -> ")"
    n.f3.accept(this);
    // f4 -> Statement()
    genStatement(n.f4);
  }

  /**
   * Visits a {@link ReturnStatement} node, whose children are the following :
   * <p>
   * f0 -> "return"<br>
   * f1 -> [ Expression() ]<br>
   * f2 -> ";"<br>
   * s: -1971167888<br>
   *
   * @param n - the node to visit
   */
  @Override
  @NodeFieldsSignature({
      -1971167888, JTB_SIG_RETURNSTATEMENT, JTB_USER_RETURNSTATEMENT
  })
  public void visit(final ReturnStatement n) {
    // change return statement only if something to return,
    if (n.f1.present()) {
      sb.append(jtbRtPrefix).append(curProd).append(" = ");
      // f1 -> [ Expression() ]
      sb.append(genJavaBranch(n.f1));
      // f2 -> ";"
      n.f2.accept(this);
    } else {
      // otherwise do not generate anything (for example for void productions)
      sb.append("/* return; statement commented out by JTB */");
    }
  }

  /**
   * Visits a {@link SynchronizedStatement} node, whose children are the following :
   * <p>
   * f0 -> "synchronized"<br>
   * f1 -> "("<br>
   * f2 -> Expression()<br>
   * f3 -> ")"<br>
   * f4 -> Block()<br>
   * s: 2040551171<br>
   *
   * @param n - the node to visit
   */
  @Override
  @NodeFieldsSignature({
      2040551171, JTB_SIG_SYNCHRONIZEDSTATEMENT, JTB_USER_SYNCHRONIZEDSTATEMENT
  })
  public void visit(final SynchronizedStatement n) {
    // f0 -> "synchronized"
    n.f0.accept(this);
    sb.append(' ');
    // f1 -> "("
    n.f1.accept(this);
    // f2 -> Expression()
    sb.append(genJavaBranch(n.f2));
    // f3 -> ")"
    n.f3.accept(this);
    spc.updateSpc(+1);
    oneNewLine(n);
    sb.append(spc.spc);
    // f4 -> Block()
    n.f4.accept(this);
    spc.updateSpc(-1);
  }

  /**
   * Generates the source code corresponding to a {@link Statement} node, whose children are the following :
   * <p>
   * f0 -> . %00 LabeledStatement()<br>
   * .. .. | %01 AssertStatement()<br>
   * .. .. | %02 Block()<br>
   * .. .. | %03 EmptyStatement()<br>
   * .. .. | %04 #0 StatementExpression() #1 ";"<br>
   * .. .. | %05 SwitchStatement()<br>
   * .. .. | %06 IfStatement()<br>
   * .. .. | %07 WhileStatement()<br>
   * .. .. | %08 DoStatement()<br>
   * .. .. | %09 ForStatement()<br>
   * .. .. | %10 BreakStatement()<br>
   * .. .. | %11 ContinueStatement()<br>
   * .. .. | %12 ReturnStatement()<br>
   * .. .. | %13 ThrowStatement()<br>
   * .. .. | %14 SynchronizedStatement()<br>
   * .. .. | %15 TryStatement()<br>
   * s: 1394695492<br>
   *
   * @param n - the Statement node
   */
  @NodeFieldsSignature({
      1394695492, JTB_SIG_STATEMENT, JTB_USER_STATEMENT
  })
  private void genStatement(final Statement n) {
    if (n.f0.which != 2) {
      // case Statement is not a %02 Block()
      spc.updateSpc(+1);
      oneNewLine(n);
      sb.append(spc.spc);
      // Statement()
      n.accept(this);
      spc.updateSpc(-1);
      oneNewLine(n);
      sb.append(spc.spc);
    } else {
      // case Statement is a %02 Block()
      sb.append(' ');
      // Statement()
      n.accept(this);
      sb.append(' ');
    }
  }

  /**
   * Class {@link VarInfo} stores information for a variable : type, name, initializer, and constructs its
   * declaration.<br>
   *
   * @author Marc Mazas
   * @version 1.4.0 : 05-08/2009 : MMa : enhanced
   * @version 1.4.3 : 03/2010 : MMa : added node declarations initialization in all cases
   * @version 1.4.6 : 01/2011 : FA/MMa : added -va and -npfx and -nsfx options
   * @version 1.5.0 : 02/2017 : MMa : moved from misc to inner class
   */
  private class VarInfo {

    /** The variable type */
    private final String type;
    /** The variable name */
    private final String name;
    /** The variable initializer */
    private final String initializer;
    // /** The whole variable declaration */
    // private String declaration;

    /**
     * Creates a new instance with no initializer.
     *
     * @param tp - the variable type
     * @param nm - the variable name
     */
    VarInfo(final String tp, final String nm) {
      this(tp, nm, null);
    }

    /**
     * Creates a new instance with an initializer.
     *
     * @param tp - the variable type
     * @param nm - the variable name
     * @param init - the variable initializer
     */
    VarInfo(final String tp, final String nm, final String init) {
      type = tp;
      name = nm;
      initializer = init;
      // declaration = null;
    }

    /**
     * @return the variable type
     */
    String getType() {
      return type;
    }

    /**
     * @return the variable name
     */
    String getName() {
      return name;
    }

    // /**
    // * Same as {@link #generateNodeDeclaration()}.
    // *
    // * @return the whole variable declaration string
    // */
    // @Override
    // public String toString() {
    // return generateNodeDeclaration();
    // }

    /**
     * Generates and stores, if not yet done, and returns the variable declaration string.
     *
     * @return the variable declaration string
     */
    String generateNodeDeclaration() {
      // if (declaration == null) {
      final StringBuilder buf = new StringBuilder(64);
      // always initialize even if initializer is null
      final String fullName = gdbv.getFixedName(type);
      buf.append(fullName).append(' ').append(name).append(" = ").append(initializer).append(";");
      // declaration = buf.toString();
      // }
      // return declaration;
      return buf.toString();
    }
  }

  // /**
  // * The {@link ExpChoicesFirstTokenCoordFinder} visitor finds the line number and column number of the
  // first
  // * token of a production under an {@link ExpansionChoices}.
  // */
  // @SuppressWarnings("unused")
  // private class ExpChoicesFirstTokenCoordFinder extends DepthFirstVoidVisitor {
  //
  // /** Standard constructor. */
  // public ExpChoicesFirstTokenCoordFinder() {
  // // needed for warnings "Access to enclosing constructor ... is emulated by a synthetic accessor method"
  // }
  //
  // /**
  // * Resets the global variable.
  // */
  // public void reset() {
  // lnft = 0;
  // cnft = 0;
  // }
  //
  // /**
  // * Visits a {@link ExpansionChoices} node, whose children are the following :
  // * <p>
  // * f0 -> Expansion()<br>
  // * f1 -> ( #0 "|" #1 Expansion() )*<br>
  // * s: -1726831935<br>
  // *
  // * @param n - the node to visit
  // */
  // @Override
  // @NodeFieldsSignature({
  // -1726831935, JTB_SIG_EXPANSIONCHOICES, JTB_USER_EXPANSIONCHOICES
  // })
  // public void visit(final ExpansionChoices n) {
  // // f0 -> Expansion()
  // n.f0.accept(this);
  // // result should be always found after visiting f0
  // }
  //
  // /**
  // * Visits a {@link Expansion} node, whose children are the following :
  // * <p>
  // * f0 -> ( #0 "LOOKAHEAD" #1 "(" #2 LocalLookahead() #3 ")" )?<br>
  // * f1 -> ( ExpansionUnit() )+<br>
  // * s: -2134365682<br>
  // *
  // * @param n - the node to visit
  // */
  // @Override
  // @NodeFieldsSignature({
  // -2134365682, JTB_SIG_EXPANSION, JTB_USER_EXPANSION
  // })
  // public void visit(final Expansion n) {
  // // f0 -> ( #0 "LOOKAHEAD" #1 "(" #2 LocalLookahead() #3 ")" )?
  // if (n.f0.present()) {
  // final NodeSequence seq = (NodeSequence) n.f0.node;
  // lnft = ((Token) seq.elementAt(0)).beginLine;
  // cnft = ((Token) seq.elementAt(0)).beginColumn;
  // } else {
  // // f1 -> ( ExpansionUnit() )+
  // n.f1.accept(this);
  // }
  // }
  //
  // /**
  // * Visits a {@link ExpansionUnit} node, whose child is the following :
  // * <p>
  // * f0 -> . %0 #0 "LOOKAHEAD" #1 "(" #2 LocalLookahead() #3 ")"<br>
  // * .. .. | %1 Block()<br>
  // * .. .. | %2 #0 "[" #1 ExpansionChoices() #2 "]"<br>
  // * .. .. | %3 ExpansionUnitTCF()<br>
  // * .. .. | %4 #0 [ $0 PrimaryExpression() $1 "=" ]<br>
  // * .. .. . .. #1 ( &0 $0 IdentifierAsString() $1 Arguments()<br>
  // * .. .. . .. .. . .. $2 [ "!" ]<br>
  // * .. .. . .. .. | &1 $0 RegularExpression()<br>
  // * .. .. . .. .. . .. $1 [ ?0 "." ?1 < IDENTIFIER > ]<br>
  // * .. .. . .. .. . .. $2 [ "!" ] )<br>
  // * .. .. | %5 #0 "(" #1 ExpansionChoices() #2 ")"<br>
  // * .. .. . .. #3 ( &0 "+"<br>
  // * .. .. . .. .. | &1 "*"<br>
  // * .. .. . .. .. | &2 "?" )?<br>
  // * s: 1116287061<br>
  // *
  // * @param n - the node to visit
  // */
  // @Override
  // @NodeFieldsSignature({
  // 1116287061, JTB_SIG_EXPANSIONUNIT, JTB_USER_EXPANSIONUNIT
  // })
  // public void visit(final ExpansionUnit n) {
  // NodeSequence seq;
  // Token tk;
  // switch (n.f0.which) {
  // case 0:
  // // %0 #0 "LOOKAHEAD" #1 "(" #2 LocalLookahead() #3 ")"
  // case 2:
  // // %2 #0 "[" #1 ExpansionChoices() #2 "]"
  // case 5:
  // // %5 #0 "(" #1 ExpansionChoices() #2 ")"
  // // .. #3 ( &0 "+" | &1 "*" | &2 "?" )?
  // seq = (NodeSequence) n.f0.choice;
  // tk = (Token) seq.elementAt(0);
  // lnft = tk.beginLine;
  // cnft = tk.beginColumn;
  // return;
  //
  // case 1:
  // // %1 Block()
  // case 3:
  // // %3 ExpansionUnitTCF()
  // n.f0.choice.accept(this);
  // return;
  //
  // case 4:
  // // %4 #0 [ $0 PrimaryExpression() $1 "=" ]
  // // .. #1 ( &0 $0 IdentifierAsString() $1 Arguments()
  // // .. .. | &1 $0 RegularExpression()
  // // .. .. . .. $1 [ ?0 "." ?1 < IDENTIFIER > ]
  // // .. .. . .. $2 [ "!" ] )
  // seq = (NodeSequence) n.f0.choice;
  // final NodeChoice ch = (NodeChoice) seq.elementAt(1);
  // final NodeSequence seq1 = (NodeSequence) ch.choice;
  // if (ch.which == 0) {
  // // &0 $0 IdentifierAsString() $1 Arguments()
  // tk = ((IdentifierAsString) seq1.elementAt(0)).f0;
  // lnft = tk.beginLine;
  // cnft = tk.beginColumn;
  // } else {
  // // &1 $0 RegularExpression() $1 [ ?0 "." ?1 < IDENTIFIER > ] )
  // seq1.elementAt(0).accept(this);
  // }
  // return;
  //
  // default:
  // final String msg = "Invalid n.f0.which = " + n.f0.which;
  // Messages.hardErr(msg);
  // throw new ProgrammaticError(msg);
  //
  // }
  // }
  //
  // /**
  // * Visits a {@link ExpansionUnitTCF} node, whose children are the following :
  // * <p>
  // * f0 -> "try"<br>
  // * f1 -> "{"<br>
  // * f2 -> ExpansionChoices()<br>
  // * f3 -> "}"<br>
  // * f4 -> ( #0 "catch" #1 "("<br>
  // * .. .. . #2 [ "final" ]<br>
  // * .. .. . #3 Name() #4 < IDENTIFIER > #5 ")" #6 Block() )*<br>
  // * f5 -> [ #0 "finally" #1 Block() ]<br>
  // * s: -1347962218<br>
  // *
  // * @param n - the node to visit
  // */
  // @Override
  // @NodeFieldsSignature({
  // -1347962218, JTB_SIG_EXPANSIONUNITTCF, JTB_USER_EXPANSIONUNITTCF
  // })
  // public void visit(final ExpansionUnitTCF n) {
  // // f0 -> "try"
  // lnft = n.f0.beginLine;
  // cnft = n.f0.beginColumn;
  // }
  //
  // /**
  // * Visits a {@link RegularExpression} node, whose child is the following :
  // * <p>
  // * f0 -> . %0 StringLiteral()<br>
  // * .. .. | %1 #0 "<"<br>
  // * .. .. . .. #1 [ $0 [ "#" ]<br>
  // * .. .. . .. .. . $1 IdentifierAsString() $2 ":" ]<br>
  // * .. .. . .. #2 ComplexRegularExpressionChoices() #3 ">"<br>
  // * .. .. | %2 #0 "<" #1 IdentifierAsString() #2 ">"<br>
  // * .. .. | %3 #0 "<" #1 "EOF" #2 ">"<br>
  // * s: 1719627151<br>
  // *
  // * @param n - the node to visit
  // */
  // @Override
  // @NodeFieldsSignature({
  // 1719627151, JTB_SIG_REGULAREXPRESSION, JTB_USER_REGULAREXPRESSION
  // })
  // public void visit(final RegularExpression n) {
  // if (n.f0.which == 0) {
  // // %0 StringLiteral()
  // lnft = ((StringLiteral) n.f0.choice).f0.beginLine;
  // cnft = ((StringLiteral) n.f0.choice).f0.beginColumn;
  // } else {
  // // %1 #0 "<" #1 [ $0 [ "#" ] $1 IdentifierAsString() $2 ":" ] #2 ComplexRegularExpressionChoices() #3
  // // ">"
  // // %2 #0 "<" #1 IdentifierAsString() #2 ">"
  // // %3 #0 "<" #1 "EOF" #2 ">"
  // final NodeSequence seq = (NodeSequence) n.f0.choice;
  // lnft = ((Token) seq.elementAt(0)).beginLine;
  // cnft = ((Token) seq.elementAt(0)).beginColumn;
  // }
  // }
  //
  // /**
  // * Visits a {@link Block} node, whose children are the following :
  // * <p>
  // * f0 -> "{"<br>
  // * f1 -> ( BlockStatement() )*<br>
  // * f2 -> "}"<br>
  // * s: -47169424<br>
  // *
  // * @param n - the node to visit
  // */
  // @Override
  // @NodeFieldsSignature({
  // -47169424, JTB_SIG_BLOCK, JTB_USER_BLOCK
  // })
  // public void visit(final Block n) {
  // // f0 -> "{"
  // lnft = n.f0.beginLine;
  // cnft = n.f0.beginColumn;
  // }
  //
  // } // end ExpansionChoicesLineNumber

  /**
   * The {@link CompilationUnitPrinter} visitor<br>
   * determines if import statements for the syntax tree and node scope hook packages are needed in the
   * grammar file,<br>
   * prints the compilation unit (with appropriate additional field and method), inserting the import
   * statements if necessary.
   */
  private class CompilationUnitPrinter extends JavaPrinter {

    /**
     * Constructor, with a given buffer and a default indentation.
     *
     * @param aJopt - the JTB options
     * @param aSb - the buffer to print into (will be allocated if null)
     * @param aSPC - the Spacing indentation object (will be allocated and set to a default if null)
     */
    CompilationUnitPrinter(final JTBOptions aJopt, final StringBuilder aSb, final Spacing aSPC) {
      super(aJopt, aSb, aSPC);
      JNCDCP = " //cup ";
    }

    /*
     * User grammar generated and overridden visit methods below
     */

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
      if (n.f0.present()) {
        n.f0.node.accept(this);
        twoNewLines(n);
      } else if (jopt.grammarPackageName != null) {
        // case no PackageDeclaration in the grammar but a package name set in the command line:
        // add a JTB generated line
        sb.append("package ").append(jopt.grammarPackageName)
            .append("; // generated by JTB from the command line setting").append(LS);
        jopt.mess.warning("No package declaration in the grammar but a package name set in the command line;"
            + " JTB added a package declaration in the grammar");
      }
      // f1 -> ( ImportDeclaration() )*
      printImports(n.f1);
      twoNewLines(n);
      // f2 -> ( TypeDeclaration() )*
      if (n.f2.present()) {
        // coverage: although the JavaCC grammar says a grammar can have no TypeDeclaration(),
        // JavaCC has a control that says it must have a parser class;
        // so JTB does not have a testcase for the no TypeDeclaration branch
        for (final Iterator<INode> e = n.f2.elements(); e.hasNext();) {
          e.next().accept(this);
          if (e.hasNext()) {
            twoNewLines(n);
          }
        }
      }
    }

    /**
     * Prints all the {@link ImportDeclaration} nodes and the other needed imports<br>
     * n -> ( ImportDeclaration() )*"<br>
     *
     * @param n - the node to process
     */
    private void printImports(final NodeListOptional n) {

      final StringBuilder mainSB = sb;

      sb = new StringBuilder(128);
      for (final INode e : n.nodes) {
        final ImportDeclaration dec = (ImportDeclaration) e;
        sb.setLength(0);
        dec.accept(this);
        final String s = sb.toString();
        // gives a strange //ann NodeListOptional Y1
        mainSB.append(s).append(nodeClassComment(n, "Y1")).append(LS);
      }

      if (jopt.nodesPackageName != null) {
        boolean foundTreeImport = false;
        final String npn = "import " + jopt.nodesPackageName + ".*;";
        for (final INode e : n.nodes) {
          final ImportDeclaration dec = (ImportDeclaration) e;
          sb.setLength(0);
          dec.accept(this);
          final String s = sb.toString();
          if (s.contains(npn)) {
            foundTreeImport = true;
            break;
          }
        }
        if (!foundTreeImport) {
          mainSB.append(npn).append(nodeClassComment(n, "Z1")).append(LS);
        }
      }

      if (jopt.hookPackageName != null //
          && jopt.hook) {
        boolean foundHookImport = false;
        final String hpn = "import " + jopt.hookPackageName + ".*;";
        for (final INode e : n.nodes) {
          final ImportDeclaration dec = (ImportDeclaration) e;
          sb.setLength(0);
          dec.accept(this);
          final String s = sb.toString();
          if (s.contains(hpn)) {
            foundHookImport = true;
            break;
          }
        }
        if (!foundHookImport) {
          mainSB.append(hpn).append(nodeClassComment(n, "Z2")).append(LS);
        }
      }

      sb = mainSB;
    }

    /**
     * Visits a {@link ImportDeclaration} node, whose children are the following :
     * <p>
     * f0 -> "import"<br>
     * f1 -> [ "static" ]<br>
     * f2 -> Name()<br>
     * f3 -> [ #0 "." #1 "*" ]<br>
     * f4 -> ";"<br>
     * s: -1592912780<br>
     *
     * @param n - the node to visit
     */
    @Override
    @NodeFieldsSignature({
        -1592912780, JTB_SIG_IMPORTDECLARATION, JTB_USER_IMPORTDECLARATION
    })
    public void visit(final ImportDeclaration n) {
      // f0 -> "import"
      n.f0.accept(this);
      sb.append(' ');
      // f1 -> [ "static" ]
      if (n.f1.present()) {
        n.f1.accept(this);
        sb.append(' ');
      }
      // f2 -> Name()
      n.f2.accept(this);
      // f3 -> [ #0 "." #1 "*" ]
      if (n.f3.present()) {
        n.f3.accept(this);
      }
      // f4 -> ";"
      n.f4.accept(this);
    }

    /**
     * Visits a {@link ClassOrInterfaceBody} node, whose children are the following :
     * <p>
     * f0 -> "{"<br>
     * f1 -> ( ClassOrInterfaceBodyDeclaration() )*<br>
     * f2 -> "}"<br>
     * s: 1154515364<br>
     *
     * @param n - the node to visit
     */
    @Override
    @NodeFieldsSignature({
        1154515364, JTB_SIG_CLASSORINTERFACEBODY, JTB_USER_CLASSORINTERFACEBODY
    })
    public void visit(final ClassOrInterfaceBody n) {
      // f0 -> "{"
      n.f0.accept(this);

      // add main class
      if (inMainClass) {

        inMainClass = false;
        twoNewLines(n, "a");
        if (jopt.hook) {
          // add node scope hook variable declaration
          sb.append("  /** The hook for enter / exit node scope hook methods (to be instantiated) */")
              .append(LS);
          sb.append(jopt.isStatic ? "static " : "").append("  public ").append(iEnterExitHook).append(' ')
              .append(jtbHookVar).append(";").append(LS).append(LS);
        }
      }

      // add return variables declarations
      final List<RetVarInfo> rvil = gdbv.getRetVarInfo();
      final int rvds = rvil.size();
      if (rvds > 0) {
        spc.updateSpc(+1);
        if (jopt.printSpecialTokensJJ) {
          oneNewLine(n, "c");
          sb.append(spc.spc);
          sb.append("/* --- JTB generated return variables declarations --- */");
          oneNewLine(n, "d");
        }
        oneNewLine(n, "e");
        sb.append(spc.spc);
        for (int i = 0; i < rvds; i++) {
          final RetVarInfo rvii = rvil.get(i);
          // comment
          sb.append("/** Return variable for the {@link #").append(rvii.ident).append("} ")
              .append(rvii.production).append(") */");
          oneNewLine(n, "f");
          sb.append(spc.spc);
          // declaration
          sb.append(jopt.isStatic ? "static " : "").append(rvii.type).append(" ").append(jtbRtPrefix)
              .append(rvii.ident).append(";");
          if (i < (rvds - 1)) {
            twoNewLines(n, "g");
            sb.append(spc.spc);
          } else {
            oneNewLine(n, "h");
          }
        }
        gdbv.getRetVarInfo().clear();
        spc.updateSpc(-1);
      }

      // f1 -> ( ClassOrInterfaceBodyDeclaration() )*
      if (n.f1.present()) {
        spc.updateSpc(+1);
        if (jopt.printSpecialTokensJJ) {
          oneNewLine(n, "i");
          sb.append(spc.spc);
          sb.append("/* --- User code --- */");
          oneNewLine(n, "j");
        }
        oneNewLine(n, "k");
        sb.append(spc.spc);
        for (final Iterator<INode> e = n.f1.elements(); e.hasNext();) {
          e.next().accept(this);
          oneNewLine(n, "l");
          if (e.hasNext()) {
            oneNewLine(n, "m");
            sb.append(spc.spc);
          }
        }
        spc.updateSpc(-1);
      }
      sb.append(spc.spc);

      // f2 -> "}"
      n.f2.accept(this);
      oneNewLine(n, "n");
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
      n.f0.accept(this);
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
      n.f0.accept(this);
    }

  } // end CompilationUnitPrinter

}

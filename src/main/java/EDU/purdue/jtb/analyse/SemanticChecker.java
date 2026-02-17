/**
 * Copyright (c) 2004,2005 UCLA Compilers Group. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided
 * that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this list of conditions and the
 * following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the
 * following disclaimer in the documentation and/or other materials provided with the distribution.
 *
 * Neither UCLA nor the names of its contributors may be used to endorse or promote products derived from this
 * software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 * PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
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
package EDU.purdue.jtb.analyse;

import static EDU.purdue.jtb.analyse.GlobalDataBuilder.BNF_IND;
import static EDU.purdue.jtb.analyse.GlobalDataBuilder.DONT_CREATE;
import static EDU.purdue.jtb.analyse.GlobalDataBuilder.JC_IND;
import static EDU.purdue.jtb.common.Constants.jtbRtPrefix;
import static EDU.purdue.jtb.parser.syntaxtree.JTBParserNodeConstants.JTB_SIG_BLOCK;
import static EDU.purdue.jtb.parser.syntaxtree.JTBParserNodeConstants.JTB_SIG_BLOCKSTATEMENT;
import static EDU.purdue.jtb.parser.syntaxtree.JTBParserNodeConstants.JTB_SIG_BNFPRODUCTION;
import static EDU.purdue.jtb.parser.syntaxtree.JTBParserNodeConstants.JTB_SIG_EXPANSION;
import static EDU.purdue.jtb.parser.syntaxtree.JTBParserNodeConstants.JTB_SIG_EXPANSIONCHOICES;
import static EDU.purdue.jtb.parser.syntaxtree.JTBParserNodeConstants.JTB_SIG_EXPANSIONUNIT;
import static EDU.purdue.jtb.parser.syntaxtree.JTBParserNodeConstants.JTB_SIG_EXPANSIONUNITTCF;
import static EDU.purdue.jtb.parser.syntaxtree.JTBParserNodeConstants.JTB_SIG_JAVACCINPUT;
import static EDU.purdue.jtb.parser.syntaxtree.JTBParserNodeConstants.JTB_SIG_JAVACODEPRODUCTION;
import static EDU.purdue.jtb.parser.syntaxtree.JTBParserNodeConstants.JTB_SIG_LOCALLOOKAHEAD;
import static EDU.purdue.jtb.parser.syntaxtree.JTBParserNodeConstants.JTB_SIG_LOCALVARIABLEDECLARATION;
import static EDU.purdue.jtb.parser.syntaxtree.JTBParserNodeConstants.JTB_SIG_PRODUCTION;
import static EDU.purdue.jtb.parser.syntaxtree.JTBParserNodeConstants.JTB_SIG_REGEXPRSPEC;
import static EDU.purdue.jtb.parser.syntaxtree.JTBParserNodeConstants.JTB_SIG_REGULAREXPRPRODUCTION;
import static EDU.purdue.jtb.parser.syntaxtree.JTBParserNodeConstants.JTB_SIG_STATEMENT;
import static EDU.purdue.jtb.parser.syntaxtree.JTBParserNodeConstants.JTB_SIG_TOKENMANAGERDECLS;
import static EDU.purdue.jtb.parser.syntaxtree.JTBParserNodeConstants.JTB_SIG_VARIABLEDECLARATOR;
import EDU.purdue.jtb.common.Messages;
import EDU.purdue.jtb.common.ProgrammaticError;
import EDU.purdue.jtb.parser.Token;
import EDU.purdue.jtb.parser.syntaxtree.BNFProduction;
import EDU.purdue.jtb.parser.syntaxtree.Block;
import EDU.purdue.jtb.parser.syntaxtree.BlockStatement;
import EDU.purdue.jtb.parser.syntaxtree.ClassOrInterfaceType;
import EDU.purdue.jtb.parser.syntaxtree.DoStatement;
import EDU.purdue.jtb.parser.syntaxtree.Expansion;
import EDU.purdue.jtb.parser.syntaxtree.ExpansionChoices;
import EDU.purdue.jtb.parser.syntaxtree.ExpansionUnit;
import EDU.purdue.jtb.parser.syntaxtree.ExpansionUnitTCF;
import EDU.purdue.jtb.parser.syntaxtree.ForStatement;
import EDU.purdue.jtb.parser.syntaxtree.INode;
import EDU.purdue.jtb.parser.syntaxtree.IdentifierAsString;
import EDU.purdue.jtb.parser.syntaxtree.IfStatement;
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
import EDU.purdue.jtb.parser.syntaxtree.OptionBinding;
import EDU.purdue.jtb.parser.syntaxtree.PrimitiveType;
import EDU.purdue.jtb.parser.syntaxtree.Production;
import EDU.purdue.jtb.parser.syntaxtree.ReferenceType;
import EDU.purdue.jtb.parser.syntaxtree.RegExprSpec;
import EDU.purdue.jtb.parser.syntaxtree.RegularExprProduction;
import EDU.purdue.jtb.parser.syntaxtree.RegularExpression;
import EDU.purdue.jtb.parser.syntaxtree.ReturnStatement;
import EDU.purdue.jtb.parser.syntaxtree.Statement;
import EDU.purdue.jtb.parser.syntaxtree.StringLiteral;
import EDU.purdue.jtb.parser.syntaxtree.SwitchStatement;
import EDU.purdue.jtb.parser.syntaxtree.SynchronizedStatement;
import EDU.purdue.jtb.parser.syntaxtree.TokenManagerDecls;
import EDU.purdue.jtb.parser.syntaxtree.TryStatement;
import EDU.purdue.jtb.parser.syntaxtree.Type;
import EDU.purdue.jtb.parser.syntaxtree.VariableDeclarator;
import EDU.purdue.jtb.parser.syntaxtree.WhileStatement;
import EDU.purdue.jtb.parser.visitor.DepthFirstVoidVisitor;
import EDU.purdue.jtb.parser.visitor.signature.NodeFieldsSignature;

/**
 * The {@link SemanticChecker} visitor checks and report informations, warnings or errors for the following
 * conditions:<br>
 * <ul>
 * <li>when a JavaCodeProduction is to be generated (warning if return type is non "void", information
 * otherwise) (JTB will alter the code),</li>
 * <li>when a BNFProduction is not to be generated (information),</li>
 * <li>when a to be generated BNFProduction has a return value other than "void" (warning) (JTB will alter the
 * code),</li>
 * <li>when a JavaCodeProduction or a BNFProduction is not to be generated locally but is also not to be
 * generated globally (warning) (as unnecessary),</li>
 * <li>when a JavaCodeProduction or a BNFProduction has a name reserved for an automatically generated JTB
 * class (e.g. INode, INodeList, ...) or (error) (the project will not compile),</li>
 * <li>when a "void" JavaCodeProduction or BNFProduction is used in an assignment (error) (the project will
 * not compile),</li>
 * <li>when a "void" JavaCodeProduction or BNFProduction is used in a return statement with expression (error)
 * (the project will not compile),</li>
 * <li>when a user declared variable is not initialized (warning) (javac may complain while compiling the
 * generated parser),</li>
 * <li>when there are extraneous parentheses in a production (warning) (should be better to remove them),</li>
 * <li>when a no node creation indicator appears in an ExpansionChoices in a syntactic lookahead</li>
 * <li>when a return statement is transformed (information).</li>
 * </ul>
 * <p>
 * Note: the warning:<br>
 * "No blocks of Java code must exist within ExpansionUnit" (since the JTB first authors believed they are
 * generally unnecessary in JTB grammars)<br>
 * has been replaced by the following new information:<br>
 * "Return statement in a Java block in production '...' . It will be transformed in an assign statement to
 * the corresponding new parser class variable."<br>
 * These blocks are now allowed - as for example JavaCC 4.2 grammar has a lot of Java code blocks - <br>
 * but as JTB generates return statements to create and return the nodes corresponding to the productions and
 * changes the production return types, JTB now creates an additional parser class variable to store the user
 * return information and changes the corresponding return statements.
 * <p>
 * Note: the warning:<br>
 * "JavaCodeProduction blocks must be specially handled."<br>
 * has been replaced by the following new information:<br>
 * "Non "void" JavaCodeProduction. Result type '...' will be changed into '...', and a parser class variable
 * 'jtbrt_...' of type '...'will be added to hold the return value."<br>
 * JTB now creates an additional parser class variable to store the user return information and changes the
 * corresponding return statements.
 * <p>
 * Note: the warning:<br>
 * "Non initialized user variable '...'. May lead to compiler error(s). Check in generated parser." <br>
 * has been added (it may lead to unnecessary warnings).
 * <p>
 * Note : could be done: check that the JTB generated return variables do not collide with user variables.
 * <p>
 * This visitor is supposed to be run once and not supposed to be run in parallel threads (on the same
 * grammar).
 * </p>
 * TODO check coverage
 *
 * @author Marc Mazas
 * @version 1.4.0 : 05-08/2009 : MMa : adapted to JavaCC v4.2 grammar and JDK 1.5
 * @version 1.4.1 : 02/2010 : MMa : fixed unprocessed n.f0.which == 0 case in visit(ExpansionUnit)
 * @version 1.4.7 : 07/2012 : MMa : followed changes in jtbgram.jtb (LocalVariableDeclaration())<br>
 *          1.4.7 : 09/2012 : MMa : added control on void production on the RHS of an assignment ; added non
 *          node creation ; changed from warning to info the message for unnecessary parenthesis ; fixed
 *          message for non void BNFProductions ; tuned messages labels
 * @version 1.4.8 : 10/2012 : MMa : added JavaCodeProduction class generation if requested ; modified checks
 *          and messages
 * @version 1.5.0 : 01-06/2017 : MMa : changed some iterator based for loops to enhanced for loops ; fixed
 *          processing of nodes not to be created ; fixed missing accept call in ExpansionUnit ; added final
 *          in ExpansionUnitTCF's catch<br>
 * @version 1.5.1 : 08/2023 : MMa : changes due to the NodeToken replacement by Token ; added warning in no
 *          node creation in a syntactic lookahead
 * @version 1.5.3 : 11/2025 : MMa : signature code made independent of parser
 */
public class SemanticChecker extends DepthFirstVoidVisitor {
  
  /** The {@link GlobalDataBuilder} visitor */
  private final GlobalDataBuilder gdbv;
  /** The messages handler */
  final Messages                  mess;
  /** The name of the current production */
  private String                  prod;
  /** The flag telling we are in a LocalLookahead () */
  private boolean                 inLA;
  
  /**
   * Constructor.
   *
   * @param aGdbv - the GlobalDataBuilder visitor
   */
  public SemanticChecker(final GlobalDataBuilder aGdbv) {
    gdbv = aGdbv;
    mess = aGdbv.jopt.mess;
    inLA = false;
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
  @NodeFieldsSignature(old_sig = 1465207473, new_sig = JTB_SIG_JAVACCINPUT, name = "JavaCCInput")
  public void visit(final JavaCCInput n) {
    // visit only f10 -> ( Production() )+
    for (final INode e : n.f10.nodes) {
      e.accept(this);
    }
  }
  
  /**
   * Visits a {@link OptionBinding} node, whose children are the following :
   * <p>
   * f0 -> ( %0 < IDENTIFIER ><br>
   * .. .. | %1 "LOOKAHEAD"<br>
   * .. .. | %2 "IGNORE_CASE"<br>
   * .. .. | %3 "static" )<br>
   * f1 -> "="<br>
   * f2 -> ( %0 IntegerLiteral()<br>
   * .. .. | %1 BooleanLiteral()<br>
   * .. .. | %2 StringLiteral() )<br>
   * f3 -> ";"<br>
   *
   * @param n - the node to visit
   */
  @Override
  public void visit(@SuppressWarnings("unused") final OptionBinding n) {
    // should not be called !
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
  @NodeFieldsSignature(old_sig = -120615333, new_sig = JTB_SIG_PRODUCTION, name = "Production")
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
  @NodeFieldsSignature(old_sig = -763138104, new_sig = JTB_SIG_JAVACODEPRODUCTION, name = "JavaCodeProduction")
  public void visit(final JavaCodeProduction n) {
    // f3 -> IdentifierAsString()
    prod = ((Token) n.f3.f0).image;
    // f2 -> ResultType()
    Token tk;
    final INode in = n.f2.f0.choice;
    if (n.f2.f0.which == 0) {
      // "void" type
      // f6 -> [ "%" ]
      if (n.f6.present()) {
        mess.info(
            "The corresponding JTB node creation will BE generated "
                + "in all places where this JavaCodeProduction '" + prod + "' is used.",
            ((Token) n.f3.f0).beginLine, ((Token) n.f3.f0).beginColumn);
      }
    } else {
      // Type(
      final NodeChoice ch = ((Type) in).f0;
      if (ch.which == 0) {
        // ReferenceType()
        final NodeChoice ch1 = ((ReferenceType) ch.choice).f0;
        if (ch1.which == 0) {
          // PrimitiveType() ( "[" "]" )+
          tk = (Token) ((PrimitiveType) ((NodeSequence) ch1.choice).elementAt(0)).f0.choice;
        } else {
          // ClassOrInterfaceType() ( "[" "]" )*
          tk = (Token) ((ClassOrInterfaceType) ((NodeSequence) ch1.choice).elementAt(0)).f0;
        }
      } else {
        // PrimitiveType()
        tk = (Token) ((PrimitiveType) ch.choice).f0.choice;
      }
      final String resType = tk.image;
      // messages in decreasing severity order for proper displaying in the plugin
      // f6 -> [ "%" ]
      if (n.f6.present()) {
        mess.warning("The corresponding JTB node creation will BE generated in all places where this "
            + "JavaCodeProduction is used, but this JavaCodeProduction should normally be of type 'void'. "
            + "Check if this is not an error.", ((Token) n.f3.f0).beginLine, ((Token) n.f3.f0).beginColumn);
        if (!resType.equals(prod)) {
          mess.warning("Non 'void' JavaCodeProduction. Result type '" + resType + "' will be changed into '"
              + prod + "', and a parser class variable '" + jtbRtPrefix + prod + "' of type '" + resType
              + "' will be added to hold the return values.", tk.beginLine, tk.beginColumn);
        } else {
          mess.info("A parser class variable '" + jtbRtPrefix + prod + "' of type '" + resType
              + "' will be added to hold the return values.", tk.beginLine, tk.beginColumn);
        }
      }
    }
    // f7 -> Block()
    if (n.f7.f1.present()) {
      // visit block declarations only if non empty
      for (final INode e : n.f7.f1.nodes) {
        // BlockStatement(), not Block() !
        e.accept(this);
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
  @NodeFieldsSignature(old_sig = 1323482450, new_sig = JTB_SIG_BNFPRODUCTION, name = "BNFProduction")
  public void visit(final BNFProduction n) {
    // f2 -> IdentifierAsString()
    prod = ((Token) n.f2.f0).image;
    // f1 -> ResultType()
    Token tk;
    final INode in = n.f1.f0.choice;
    if (n.f1.f0.which == 0) {
      // "void" type
      // f5 -> [ "!" ]
      if (n.f5.present()) {
        mess.info("The corresponding JTB node creation will NOT be generated in all places where this " //
            + "BNFProduction '" + prod + "' is used.", ((Token) n.f2.f0).beginLine,
            ((Token) n.f2.f0).beginColumn);
      }
    } else {
      // Type(
      final NodeChoice ch = ((Type) in).f0;
      if (ch.which == 0) {
        // ReferenceType()
        final NodeChoice ch1 = ((ReferenceType) ch.choice).f0;
        if (ch1.which == 0) {
          // PrimitiveType() ( "[" "]" )+
          tk = (Token) ((PrimitiveType) ((NodeSequence) ch1.choice).elementAt(0)).f0.choice;
        } else {
          // ClassOrInterfaceType() ( "[" "]" )*
          tk = (Token) ((ClassOrInterfaceType) ((NodeSequence) ch1.choice).elementAt(0)).f0;
        }
      } else {
        // PrimitiveType()
        tk = (Token) ((PrimitiveType) ch.choice).f0.choice;
      }
      final String resType = tk.image;
      // messages in decreasing severity order for proper displaying in the plugin
      // f5 -> [ "!" ]
      if (n.f5.present()) {
        mess.warning(
            "The corresponding JTB node creation will NOT be generated in all places where this "
                + "BNFProduction is used, but this BNFProduction should normally be of type 'void'. "
                + "Check if this is not an error.",
            ((Token) n.f2.f0).beginLine, ((Token) n.f2.f0).beginColumn);
      } else if (!"void".equals(resType)) {
        mess.warning("Non 'void' BNFProduction. Result type '" + resType + "' will be changed into '" + prod
            + "', and a parser class variable '" + jtbRtPrefix + prod + "' of type '" + resType
            + "' will be added to hold the return values.", tk.beginLine, tk.beginColumn);
      }
    }
    // f7 -> Block()
    if (n.f7.f1.present()) {
      // visit block declarations only if non empty
      for (final INode e : n.f7.f1.nodes) {
        // BlockStatement(), not Block() !
        e.accept(this);
      }
    }
    // f9 -> ExpansionChoices()
    n.f9.accept(this);
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
  @NodeFieldsSignature(old_sig = -1726831935, new_sig = JTB_SIG_EXPANSIONCHOICES, name = "ExpansionChoices")
  public void visit(final ExpansionChoices n) {
    // f0 -> Expansion()
    n.f0.accept(this);
    // f1 -> ( #0 "|" #1 Expansion() )*
    final NodeListOptional nlo = n.f1;
    if (nlo.present()) {
      for (final INode e : nlo.nodes) {
        ((NodeSequence) e).elementAt(1).accept(this);
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
    // f0 -> ( #0 "LOOKAHEAD" #1 "(" #2 LocalLookahead() #3 ")" )?
    final NodeOptional n0 = n.f0;
    if (n0.present()) {
      // #2 LocalLookahead()
      // Token tk = (Token) ((NodeSequence) n0.node).elementAt(0);
      // mess.info(" Exp: inLA = " + inLA + " <- true", tk.beginLine, tk.beginColumn);
      ((NodeSequence) n0.node).elementAt(2).accept(this);
    }
    // f1 -> ( ExpansionUnit() )+
    final NodeList n1 = n.f1;
    for (int i = 0; i < n1.size(); i++) {
      final INode lsteai = n1.elementAt(i);
      lsteai.accept(this);
    }
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
  @NodeFieldsSignature(old_sig = -1879920786, new_sig = JTB_SIG_LOCALLOOKAHEAD, name = "LocalLookahead")
  public void visit(final LocalLookahead n) {
    // f2 -> [ ExpansionChoices() ]
    final NodeOptional n2 = n.f2;
    if (n2.present()) {
      final boolean saveInLA = inLA;
      inLA = true;
      n2.accept(this);
      inLA = saveInLA;
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
    switch (n.f0.which) {
    case 0:
      // %0 #0 "LOOKAHEAD" #1 "(" #2 LocalLookahead() #3 ")"
      // Token tk2 = (Token) ((NodeSequence) n.f0.choice).elementAt(0);
      // mess.info(" EU: inLA = " + inLA + " <- true", tk2.beginLine, tk2.beginColumn);
      ((NodeSequence) n.f0.choice).elementAt(2).accept(this);
      return;
    
    case 1:
      // %1 Block()
    case 3:
      // %3 ExpansionUnitTCF()
      n.f0.choice.accept(this);
      return;
    
    case 2:
      // %2 #0 "[" #1 ExpansionChoices() #2 "]"
      ((NodeSequence) n.f0.choice).elementAt(1).accept(this);
      return;
    
    case 4:
      // %4 #0 [ $0 PrimaryExpression() $1 "=" ]
      // .. #1 ( &0 $0 IdentifierAsString() $1 Arguments()
      // .. .. . .. $2 [ "!" ]
      // .. .. | &1 $0 RegularExpression()
      // .. .. . .. $1 [ ?0 "." ?1 < IDENTIFIER > ]
      // .. .. . .. $2 [ "!" ] )
      seq = (NodeSequence) n.f0.choice;
      
      final NodeChoice ch = (NodeChoice) seq.elementAt(1);
      final NodeSequence seq1 = (NodeSequence) ch.choice;
      
      // #0 [ $0 PrimaryExpression() $1 "=" ]
      final NodeOptional opt = (NodeOptional) seq.elementAt(0);
      if (opt.present()) {
        if (ch.which == 0) {
          // IdentifierAsString() -> jtbrt_Identifier
          // $0 IdentifierAsString() $1 Arguments() $2 [ "!" ]
          final Token tk = (Token) ((IdentifierAsString) seq1.elementAt(0)).f0;
          final String ident = tk.image;
          final String val = gdbv.getProdHM().get(ident);
          if ((JC_IND + "void").equals(val)) {
            mess.softErr("Use in an assignment of the JavaCodeProduction '" + ident + "()' of type void.",
                tk.beginLine, tk.beginColumn);
          }
          if ((BNF_IND + "void").equals(val)) {
            mess.softErr("Use in an assignment of the BNFProduction '" + ident + "()' of type void.",
                tk.beginLine, tk.beginColumn);
          }
        }
      }
      
      if (ch.which == 0) {
        // $0 IdentifierAsString() $1 Arguments() $2 [ "!" ]
        // display messages
        final Token tk = (Token) ((IdentifierAsString) seq1.elementAt(0)).f0;
        final String ident = tk.image;
        final String val = gdbv.getProdHM().get(ident);
        if (val == null) {
          mess.softErr("Use (in an ExpansionUnit) of an identifier '" + ident
              + "' which is not a BNF Production or a JavaCodeProduction.", tk.beginLine, tk.beginColumn);
        } else {
          final String indProd = val.substring(0, 1);
          // messages in decreasing severity order for proper displaying in the plugin
          final NodeOptional opt1 = (NodeOptional) seq1.elementAt(2);
          if (opt1.present()) {
            if (inLA) {
              final Token tk1 = (Token) opt1.node;
              mess.warning("Invalid no node creation indication ('!' character) as no tree "
                  + "is generated inside a syntactic lookahead.", tk1.beginLine, tk1.beginColumn);
            } else if (JC_IND.equals(indProd)) {
              if (gdbv.getNotTbcNodesHM().containsKey(ident)) {
                mess.warning("Unnecessary no node creation indication ('!' character) as the " //
                    + "JavaCodeProduction '" + prod + "()' is not indicated to be generated "
                    + "(no '%' character).", tk.beginLine, tk.beginColumn);
              } else {
                mess.info("The corresponding JTB (JavaCode) node creation will NOT be generated here "
                    + "(as requested here).", tk.beginLine, tk.beginColumn);
              }
            } else if (BNF_IND.equals(indProd)) {
              if (gdbv.getNotTbcNodesHM().containsKey(ident)) {
                mess.warning("Unnecessary no node creation indication ('!' character) as the " //
                    + "BNFProduction '" + prod + "()' is indicated not to " + "be generated ('!' character).",
                    tk.beginLine, tk.beginColumn);
              } else {
                mess.info("The corresponding JTB (BNF) node creation will NOT be generated here "
                    + "(as requested here).", tk.beginLine, tk.beginColumn);
              }
            }
          }
        }
      } else {
        // $0 RegularExpression() $1 [ ?0 "." ?1 < IDENTIFIER > ] $2 [ "!" ]
        // display messages
        Token tk = null;
        int lnre = 0;
        int cnre = 0;
        final RegularExpression re = (RegularExpression) seq1.elementAt(0);
        if (re.f0.which == 0) {
          // %0 StringLiteral()
          tk = (Token) ((StringLiteral) re.f0.choice).f0;
          lnre = tk.beginLine;
          cnre = tk.beginColumn;
        } else {
          // %1 #0 "<" #1 [ $0 [ "#" ] $1 IdentifierAsString() $2 ":" ] #2 ComplexRegularExpressionChoices()
          // #3 ">"
          // %2 #0 "<" #1 IdentifierAsString() #2 ">"
          // %3 #0 "<" #1 "EOF" #2 ">"
          final NodeSequence seq2 = (NodeSequence) re.f0.choice;
          if (re.f0.which == 2) {
            // %2 #0 "<" #1 IdentifierAsString() #2 ">"
            tk = (Token) ((IdentifierAsString) seq2.elementAt(1)).f0;
          }
          lnre = ((Token) seq2.elementAt(0)).beginLine;
          cnre = ((Token) seq2.elementAt(0)).beginColumn;
        }
        // messages in decreasing severity order for proper displaying in the plugin
        final NodeOptional opt1 = (NodeOptional) seq1.elementAt(2);
        if (opt1.present()) {
          if (inLA) {
            final Token tk1 = (Token) opt1.node;
            mess.warning("Invalid no node creation indication ('!' character) as no tree "
                + "is generated inside a syntactic lookahead.", tk1.beginLine, tk1.beginColumn);
          } else {
            mess.info("The corresponding JTB Token creation will NOT be generated here (as requested here).",
                lnre, cnre);
          }
        }
        if ((tk != null) && DONT_CREATE.equals(gdbv.getTokenHM().get(tk.image))) {
          mess.info("The corresponding JTB Token creation will NOT be generated here "
              + "(as requested in the RegExprSpec declaration).", lnre, cnre);
        }
      }
      
      return;
    
    case 5:
      // %5 #0 "(" #1 ExpansionChoices() #2 ")"
      // .. #3 ( &0 "+" | &1 "*" | &2 "?" )?
      seq = (NodeSequence) n.f0.choice;
      final ExpansionChoices choice = (ExpansionChoices) seq.elementAt(1);
      final NodeOptional mod = (NodeOptional) seq.elementAt(3);
      if (!mod.present() && !choice.f1.present()) {
        final Token tk = (Token) ((NodeSequence) n.f0.choice).elementAt(0);
        mess.info("Unnecessary parentheses in '" + prod + "()'.", tk.beginLine, tk.beginColumn);
      }
      choice.accept(this);
      return;
    
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
    // f2 -> ExpansionChoices()
    n.f2.accept(this);
    // f4 -> ( #0 "catch" #1 "("
    // .. .. . #2 ( Annotation() )*
    // .. .. . #3 [ "final" ]
    // .. .. . #4 Name() #5 < IDENTIFIER > #6 ")" #7 Block() )*
    if (n.f4.present()) {
      for (int i = 0; i < n.f4.size(); i++) {
        // #7 Block()
        ((NodeSequence) n.f4.elementAt(i)).elementAt(7).accept(this);
      }
    }
    // f5 -> [ #0 "finally" #1 Block() ]
    if (n.f5.present()) {
      // #1 Block()
      ((NodeSequence) n.f5.node).elementAt(1).accept(this);
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
  @NodeFieldsSignature(old_sig = 484788342, new_sig = JTB_SIG_REGULAREXPRPRODUCTION, name = "RegularExprProduction")
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
   * Visits a {@link TokenManagerDecls} node, whose children are the following :
   * <p>
   * f0 -> "TOKEN_MGR_DECLS"<br>
   * f1 -> ":"<br>
   * f2 -> ClassOrInterfaceBody()<br>
   * s: -1566997219<br>
   *
   * @param n - the node to visit
   */
  @Override
  @NodeFieldsSignature(old_sig = -1566997219, new_sig = JTB_SIG_TOKENMANAGERDECLS, name = "TokenManagerDecls")
  public void visit(@SuppressWarnings("unused") final TokenManagerDecls n) {
    // should not be called !
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
  @NodeFieldsSignature(old_sig = -1949948808, new_sig = JTB_SIG_REGEXPRSPEC, name = "RegExprSpec")
  public void visit(final RegExprSpec n) {
    // f1 -> [ "!" ]
    // display an info message
    if (n.f1.present()) {
      Token tk = null;
      // f0 -> RegularExpression()
      final NodeChoice ch = n.f0.f0;
      if (ch.which == 0) {
        tk = (Token) ((StringLiteral) ch.choice).f0;
      } else {
        tk = (Token) ((NodeSequence) ch.choice).elementAt(0);
      }
      mess.info("The corresponding JTB node creation will NOT be generated "
          + "in all places where this RegExprSpec is used.", tk.beginLine, tk.beginColumn);
    }
  }
  
  /**
   * Visits a {@link VariableDeclarator} node, whose children are the following :
   * <p>
   * f0 -> VariableDeclaratorId()<br>
   * f1 -> [ #0 "=" #1 VariableInitializer() ]<br>
   * s: -484955779<br>
   *
   * @param n - the node to visit
   */
  @Override
  @NodeFieldsSignature(old_sig = -484955779, new_sig = JTB_SIG_VARIABLEDECLARATOR, name = "VariableDeclarator")
  public void visit(final VariableDeclarator n) {
    // f1 -> [ #0 "=" #1 VariableInitializer() ]
    if (!n.f1.present()) {
      final String var = ((Token) n.f0.f0).image;
      mess.warning("Non initialized user variable '" + var
          + "'. May lead to compiler error(s) (specially for 'Token' variables). "
          + "Check in generated parser.", ((Token) n.f0.f0).beginLine, ((Token) n.f0.f0).beginColumn);
    }
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
  @NodeFieldsSignature(old_sig = 1394695492, new_sig = JTB_SIG_STATEMENT, name = "Statement")
  public void visit(final Statement n) {
    switch (n.f0.which) {
    case 0:
      // %00 LabeledStatement() : f2 -> Statement()
      ((LabeledStatement) n.f0.choice).f2.accept(this);
      break;
    case 1:
      // %01 AssertStatement()
      break;
    case 2:
      // %02 Block() : f1 -> ( BlockStatement() )*
      if (((Block) n.f0.choice).f1.present()) {
        ((Block) n.f0.choice).f1.accept(this);
      }
      break;
    case 3:
      // %03 EmptyStatement()
      break;
    case 4:
      // %04 #0 StatementExpression() #1 ";"
      break;
    case 5:
      // %05 SwitchStatement() : f5 -> ( SwitchLabel() ( BlockStatement() )* )*<br>
      final NodeListOptional nlo5 = ((SwitchStatement) n.f0.choice).f5;
      if (nlo5.present()) {
        for (final INode e : nlo5.nodes) {
          final NodeSequence seq5 = (NodeSequence) e;
          if (((NodeListOptional) seq5.elementAt(1)).present()) {
            seq5.elementAt(1).accept(this);
          }
        }
      }
      break;
    case 6:
      // %06 IfStatement() : f4 -> Statement() and f5 -> [ "else" Statement() ]
      ((IfStatement) n.f0.choice).f4.accept(this);
      final NodeOptional opt6 = ((IfStatement) n.f0.choice).f5;
      if (opt6.present()) {
        ((NodeSequence) opt6.node).elementAt(1).accept(this);
      }
      break;
    case 7:
      // %07 WhileStatement()
      ((WhileStatement) n.f0.choice).f4.accept(this);
      break;
    case 8:
      // %08 DoStatement()
      ((DoStatement) n.f0.choice).f1.accept(this);
      break;
    case 9:
      // %09 ForStatement()
      ((ForStatement) n.f0.choice).f4.accept(this);
      break;
    case 10:
      // %10 BreakStatement()
      break;
    case 11:
      // %11 ContinueStatement()
      break;
    case 12:
      // %12 ReturnStatement()
      final ReturnStatement rs = (ReturnStatement) n.f0.choice;
      // f1 -> [ Expression() ]
      final String val = gdbv.getProdHM().get(prod);
      if (val == null) {
        mess.softErr(
            "Use (in a ReturnStatement) of an identifier '" + prod
                + "' which is not a BNF Production or a JavaCodeProduction.",
            ((Token) rs.f0).beginLine, ((Token) rs.f0).beginColumn);
      } else {
        final String resType = val.substring(1);
        if (!gdbv.getNotTbcNodesHM().containsKey(prod)) {
          if (rs.f1.present()) {
            if ("void".equals(resType)) {
              mess.softErr("Return with expression statement in a Java block in production '" + prod
                  + "()' of type 'void'.", ((Token) rs.f0).beginLine, ((Token) rs.f0).beginColumn);
            } else {
              mess.info(
                  "This return statement will be transformed in an assign statement to the "
                      + "corresponding new parser class variable and a return statement of the node.",
                  ((Token) rs.f0).beginLine, ((Token) rs.f0).beginColumn);
            }
          } else if (!"void".equals(resType)) {
            mess.softErr("Return without expression statement in a Java block in production '" + prod
                + "()' of type non 'void'.", ((Token) rs.f0).beginLine, ((Token) rs.f0).beginColumn);
          } else {
            mess.info("This return statement will be transformed in a return statement of the node.",
                ((Token) rs.f0).beginLine, ((Token) rs.f0).beginColumn);
          }
        }
      }
      break;
    case 13:
      // %13 ThrowStatement()
      break;
    case 14:
      // %14 SynchronizedStatement()
      ((SynchronizedStatement) n.f0.choice).f4.accept(this);
      break;
    case 15:
      // %15 TryStatement()
      // f1 -> Block()
      ((TryStatement) n.f0.choice).f1.accept(this);
      // f2 -> ( "catch" "(" FormalParameter() ")" Block() )*
      final NodeListOptional nlo15 = ((TryStatement) n.f0.choice).f2;
      if (nlo15.present()) {
        for (final INode e : nlo15.nodes) {
          ((NodeSequence) e).elementAt(4).accept(this);
        }
      }
      // f3 -> [ "finally" Block() ]
      final NodeOptional opt15 = ((TryStatement) n.f0.choice).f3;
      if (opt15.present()) {
        ((NodeSequence) opt15.node).elementAt(1).accept(this);
      }
      break;
    default:
      final String msg = "Invalid n.f0.which = " + n.f0.which;
      Messages.hardErr(msg);
      throw new ProgrammaticError(msg);
    }
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
  @NodeFieldsSignature(old_sig = -47169424, new_sig = JTB_SIG_BLOCK, name = "Block")
  public void visit(final Block n) {
    // f1 -> ( BlockStatement() )*
    if (n.f1.present()) {
      for (final INode e : n.f1.nodes) {
        // BlockStatement()
        e.accept(this);
      }
    }
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
  @NodeFieldsSignature(old_sig = -1009630136, new_sig = JTB_SIG_BLOCKSTATEMENT, name = "BlockStatement")
  public void visit(final BlockStatement n) {
    if (n.f0.which <= 1) {
      // %0 #0 LocalVariableDeclaration() #1 ";"
      n.f0.choice.accept(this);
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
  @NodeFieldsSignature(old_sig = 225808290, new_sig = JTB_SIG_LOCALVARIABLEDECLARATION, name = "LocalVariableDeclaration")
  public void visit(final LocalVariableDeclaration n) {
    // f2 -> VariableDeclarator()
    n.f2.accept(this);
    // f3 -> ( #0 "," #1 VariableDeclarator() )*
    if (n.f3.present()) {
      for (final INode e : n.f3.nodes) {
        // #1 VariableDeclarator()
        ((NodeSequence) e).elementAt(1).accept(this);
      }
    }
  }
  
}

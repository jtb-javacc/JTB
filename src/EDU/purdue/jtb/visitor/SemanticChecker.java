/**
 * Copyright (c) 2004,2005 UCLA Compilers Group.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  Redistributions of source code must retain the above copyright
 *  notice, this list of conditions and the following disclaimer.
 *
 *  Redistributions in binary form must reproduce the above copyright
 *  notice, this list of conditions and the following disclaimer in the
 *  documentation and/or other materials provided with the distribution.
 *
 *  Neither UCLA nor the names of its contributors may be used to endorse
 *  or promote products derived from this software without specific prior
 *  written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 **/
/*
 * All files in the distribution of JTB, The Java Tree Builder are
 * Copyright 1997, 1998, 1999 by the Purdue Research Foundation of Purdue
 * University.  All rights reserved.
 *
 * Redistribution and use in source and binary forms are permitted
 * provided that this entire copyright notice is duplicated in all
 * such copies, and that any documentation, announcements, and
 * other materials related to such distribution and use acknowledge
 * that the software was developed at Purdue University, West Lafayette,
 * Indiana by Kevin Tao and Jens Palsberg.  No charge may be made
 * for copies, derivations, or distributions of this material
 * without the express written consent of the copyright holder.
 * Neither the name of the University nor the name of the author
 * may be used to endorse or promote products derived from this
 * material without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND WITHOUT ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, WITHOUT LIMITATION, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR ANY PARTICULAR PURPOSE.
 */
package EDU.purdue.jtb.visitor;

import static EDU.purdue.jtb.misc.Globals.iNode;
import static EDU.purdue.jtb.misc.Globals.iNodeList;
import static EDU.purdue.jtb.misc.Globals.JTBRT_PREFIX;
import static EDU.purdue.jtb.misc.Globals.nodeChoice;
import static EDU.purdue.jtb.misc.Globals.nodeList;
import static EDU.purdue.jtb.misc.Globals.nodeListOpt;
import static EDU.purdue.jtb.misc.Globals.nodeOpt;
import static EDU.purdue.jtb.misc.Globals.nodeSeq;
import static EDU.purdue.jtb.misc.Globals.nodeToken;
import static EDU.purdue.jtb.visitor.GlobalDataBuilder.DONT_CREATE_NODE_STR;

import java.util.Iterator;

import EDU.purdue.jtb.misc.Messages;
import EDU.purdue.jtb.syntaxtree.BNFProduction;
import EDU.purdue.jtb.syntaxtree.Block;
import EDU.purdue.jtb.syntaxtree.BlockStatement;
import EDU.purdue.jtb.syntaxtree.ClassOrInterfaceType;
import EDU.purdue.jtb.syntaxtree.DoStatement;
import EDU.purdue.jtb.syntaxtree.Expansion;
import EDU.purdue.jtb.syntaxtree.ExpansionChoices;
import EDU.purdue.jtb.syntaxtree.ExpansionUnit;
import EDU.purdue.jtb.syntaxtree.ExpansionUnitTCF;
import EDU.purdue.jtb.syntaxtree.ForStatement;
import EDU.purdue.jtb.syntaxtree.INode;
import EDU.purdue.jtb.syntaxtree.IdentifierAsString;
import EDU.purdue.jtb.syntaxtree.IfStatement;
import EDU.purdue.jtb.syntaxtree.JavaCCInput;
import EDU.purdue.jtb.syntaxtree.JavaCodeProduction;
import EDU.purdue.jtb.syntaxtree.LocalLookahead;
import EDU.purdue.jtb.syntaxtree.LocalVariableDeclaration;
import EDU.purdue.jtb.syntaxtree.NodeChoice;
import EDU.purdue.jtb.syntaxtree.NodeListOptional;
import EDU.purdue.jtb.syntaxtree.NodeOptional;
import EDU.purdue.jtb.syntaxtree.NodeSequence;
import EDU.purdue.jtb.syntaxtree.NodeToken;
import EDU.purdue.jtb.syntaxtree.OptionBinding;
import EDU.purdue.jtb.syntaxtree.PrimitiveType;
import EDU.purdue.jtb.syntaxtree.Production;
import EDU.purdue.jtb.syntaxtree.ReferenceType;
import EDU.purdue.jtb.syntaxtree.RegExprSpec;
import EDU.purdue.jtb.syntaxtree.RegularExprProduction;
import EDU.purdue.jtb.syntaxtree.RegularExpression;
import EDU.purdue.jtb.syntaxtree.ResultType;
import EDU.purdue.jtb.syntaxtree.ReturnStatement;
import EDU.purdue.jtb.syntaxtree.Statement;
import EDU.purdue.jtb.syntaxtree.StringLiteral;
import EDU.purdue.jtb.syntaxtree.SwitchStatement;
import EDU.purdue.jtb.syntaxtree.SynchronizedStatement;
import EDU.purdue.jtb.syntaxtree.TokenManagerDecls;
import EDU.purdue.jtb.syntaxtree.TryStatement;
import EDU.purdue.jtb.syntaxtree.Type;
import EDU.purdue.jtb.syntaxtree.VariableDeclarator;
import EDU.purdue.jtb.syntaxtree.WhileStatement;

/**
 * SemanticChecker visitor checks and report informations, warnings or errors for the following
 * conditions}<br>
 * <ul>
 * <li>When productions have a return value other than "void" (information) (JTB will alter the
 * code).
 * <li>When JavaCode productions exist (information) (JTB will alter the code).
 * <li>When a user declared variable is not initialized (warning) (javac may complain while
 * compiling the generated parser).
 * <li>When there are extraneous parentheses in a production (warning) (should be better to remove
 * them).
 * <li>When a production has a name reserved by an automatically generated JTB class (e.g. INode,
 * INodeList, ...) (error) (the project will not compile).
 * </ul>
 * <p>
 * Note: the warning}<br>
 * "No blocks of Java code must exist within ExpansionUnit" (since the JTB first authors believed
 * they are generally unnecessary in JTB grammars)<br>
 * has been replaced by the following new information}<br>
 * "Return statement in a Java block in production '...' . It will be transformed in an assign
 * statement to the corresponding new parser class variable."<br>
 * These blocks are now allowed - as for example JavaCC 4.2 grammar has a lot of Java code blocks -<br>
 * but as JTB generates return statements to create and return the nodes corresponding to the
 * productions and changes the production return types, JTB now creates an additional parser class
 * variable to store the user return information and changes the corresponding return statements.
 * <p>
 * Note: the warning}<br>
 * "JavaCodeProduction blocks must be specially handled."<br>
 * has been replaced by the following new information}<br>
 * "Non "void" JavaCodeProduction. Result type '...' will be changed into '...', and a parser class
 * variable 'jtbrt_...' of type '...'will be added to hold the return value."<br>
 * JTB now creates an additional parser class variable to store the user return information and
 * changes the corresponding return statements.
 * <p>
 * Note: the warning}<br>
 * "Non initialized user variable '...'. May lead to compiler error(s). Check in generated parser."<br>
 * has been added (it may lead to unnecessary warnings).
 * <p>
 * To be Done: check that the JTB generated return variables do not collide with user variables.
 * 
 * @author Marc Mazas
 * @version 1.4.0 : 05-08/2009 : MMa : adapted to JavaCC v4.2 grammar and JDK 1.5
 * @version 1.4.1 : 02/2010 : MMa : fixed unprocessed n.f0.which == 0 case in visit(ExpansionUnit)
 * @version 1.4.7 : 07/2012 : MMa : followed changes in jtbgram.jtb (LocalVariableDeclaration())<br>
 *          1.4.7 : 09/2012 : MMa : added control on void production on the RHS of an assignment ;
 *          added non node creation ; changed from warning to info the message for unnecessary
 *          parenthesis ; fixed message for non void BNF productions ; tuned messages labels
 */
public class SemanticChecker extends DepthFirstVoidVisitor {

  /** The reference to the global data finder visitor */
  final GlobalDataBuilder gdfv;
  /** The name of the current production */
  String                  prod;

  /**
   * Constructor.
   * 
   * @param aGdfv - the global data finder visitor
   */
  public SemanticChecker(final GlobalDataBuilder aGdfv) {
    gdfv = aGdfv;
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
    // visit only f10 -> ( Production() )+
    for (final Iterator<INode> e = n.f10.elements(); e.hasNext();) {
      e.next().accept(this);
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
   * f6 -> Block()<br>
   * 
   * @param n - the node to visit
   */
  @Override
  public void visit(final JavaCodeProduction n) {
    // f3 -> IdentifierAsString()
    final String ident = n.f3.f0.tokenImage;
    final String resType = getResultType(n.f2);
    if (!"void".equals(resType)) {
      Messages.info("Non 'void' JavaCode Production. Result type '" + resType +
                        "' will be changed into '" + ident +
                        "', and a parser class variable 'jtbrt_" + resType + "' of type '" +
                        resType + "' will be added to hold the return value.", n.f0.beginLine,
                    n.f0.beginColumn);
    }
  }

  /**
   * Gets the ResultType. Walks down the tree to find the first token.
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
  String getResultType(final ResultType rt) {
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
    // display an info message
    if (n.f1.present()) {
      NodeToken tk = null;
      // f0 -> RegularExpression()
      final NodeChoice ch = n.f0.f0;
      if (ch.which == 0) {
        tk = ((StringLiteral) ch.choice).f0;

      } else {
        tk = (NodeToken) ((NodeSequence) ch.choice).elementAt(0);
      }
      Messages.info("The corresponding JTB node creation will NOT be generated "
                    + "in all places where this RegExprSpec is used.", tk.beginLine, tk.beginColumn);
    }
  }

  /**
   * Visits a {@link TokenManagerDecls} node, whose children are the following :
   * <p>
   * f0 -> "TOKEN_MGR_DECLS"<br>
   * f1 -> ":"<br>
   * f2 -> ClassOrInterfaceBody()<br>
   * 
   * @param n - the node to visit
   */
  @Override
  public void visit(@SuppressWarnings("unused") final TokenManagerDecls n) {
    // should not be called !
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
    prod = n.f2.f0.tokenImage;
    // f1 -> ResultType()
    final ResultType rt = n.f1;
    NodeToken tk;
    final INode in = rt.f0.choice;
    if (rt.f0.which == 0) {
      // "void" : no need for return variable, but need to check variable initialization
      // f5 -> [ "!" ]
      if (n.f5.present())
        Messages.info("The corresponding JTB node creation will NOT be generated "
                          + "in all places where this BNFProduction is used.", n.f2.f0.beginLine,
                      n.f2.f0.beginColumn);
    } else {
      // Type(
      final NodeChoice ch = ((Type) in).f0;
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
      final String resType = tk.tokenImage;
      final String ident = n.f2.f0.tokenImage;
      // messages in decreasing severity order for proper displaying in the plugin
      if (prod.equals(iNode) || prod.equals(iNodeList) || prod.equals(nodeList) ||
          prod.equals(nodeListOpt) || prod.equals(nodeOpt) || prod.equals(nodeSeq) ||
          prod.equals(nodeToken) || prod.equals(nodeChoice))
        Messages.softErr("Production '" + prod + "()' has the same name as a JTB generated class.",
                         n.f2.f0.beginLine, n.f2.f0.beginColumn);
      // f5 -> [ "!" ]
      if (n.f5.present())
        Messages.warning("The corresponding JTB node creation will NOT be generated in all places where this "
                             + "BNFProduction is used, but this BNFProduction should be of type 'void'. "
                             + "Check if this is not an error.", n.f2.f0.beginLine,
                         n.f2.f0.beginColumn);
      if (!resType.equals(ident))
        Messages.warning("Non 'void' BNF production. Result type '" + resType +
                         "' will be changed into '" + ident + "', and a parser class variable '" +
                         JTBRT_PREFIX + ident + "' of type '" + resType +
                         "' will be added to hold the return values.", tk.beginLine, tk.beginColumn);
      else
        Messages.info("A parser class variable '" + JTBRT_PREFIX + ident + "' of type '" + resType +
                      "' will be added to hold the return values.", tk.beginLine, tk.beginColumn);
    }
    // f7 -> Block()
    if (n.f7.f1.present()) {
      // visit block declarations only if non empty
      for (final Iterator<INode> e = n.f7.f1.elements(); e.hasNext();)
        // BlockStatement(), not Block() !
        e.next().accept(this);
    }
    // f9 -> ExpansionChoices()
    n.f9.accept(this);
  }

  /**
   * Visits a {@link Block} node, whose children are the following :
   * <p>
   * f0 -> "{"<br>
   * f1 -> ( BlockStatement() )*<br>
   * f2 -> "}"<br>
   * 
   * @param n - the node to visit
   */
  @Override
  public void visit(final Block n) {
    // f1 -> ( BlockStatement() )*
    if (n.f1.present())
      for (final Iterator<INode> e = n.f1.elements(); e.hasNext();)
        // BlockStatement()
        e.next().accept(this);
  }

  /**
   * Visits a {@link BlockStatement} node, whose children are the following :
   * <p>
   * f0 -> . %0 #0 LocalVariableDeclaration() #1 ";"<br>
   * .. .. | %1 Statement()<br>
   * .. .. | %2 ClassOrInterfaceDeclaration()<br>
   * 
   * @param n - the node to visit
   */
  @Override
  public void visit(final BlockStatement n) {
    if (n.f0.which <= 1)
      // %0 #0 LocalVariableDeclaration() #1 ";"
      n.f0.choice.accept(this);
  }

  /**
   * Visits a {@link LocalVariableDeclaration} node, whose children are the following :
   * <p>
   * f0 -> VariableModifiers()<br>
   * f1 -> Type()<br>
   * f2 -> VariableDeclarator()<br>
   * f3 -> ( #0 "," #1 VariableDeclarator() )*<br>
   * 
   * @param n - the node to visit
   */
  @Override
  public void visit(final LocalVariableDeclaration n) {
    // f2 -> VariableDeclarator()
    n.f2.accept(this);
    // f3 -> ( #0 "," #1 VariableDeclarator() )*
    if (n.f3.present()) {
      for (final Iterator<INode> e = n.f3.elements(); e.hasNext();) {
        // #1 VariableDeclarator()
        ((NodeSequence) e.next()).elementAt(1).accept(this);
      }
    }
  }

  /**
   * Visits a {@link VariableDeclarator} node, whose children are the following :
   * <p>
   * f0 -> VariableDeclaratorId()<br>
   * f1 -> [ #0 "=" #1 VariableInitializer() ]<br>
   * 
   * @param n - the node to visit
   */
  @Override
  public void visit(final VariableDeclarator n) {
    // f1 -> [ #0 "=" #1 VariableInitializer() ]
    if (!n.f1.present()) {
      final String var = n.f0.f0.tokenImage;
      Messages.warning("Non initialized user variable '" + var +
                       "'. May lead to compiler error(s) (specially for 'Token' variables). " +
                       "Check in generated parser.", n.f0.f0.beginLine, n.f0.f0.beginColumn);
    }
  }

  /**
   * Visits a {@link Statement} node, whose children are the following :
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
   * 
   * @param n - the node to visit
   */
  @Override
  public void visit(final Statement n) {
    switch (n.f0.which) {
      case 0:
        // %00 LabeledStatement() : f2 -> Statement()
        ((NodeSequence) n.f0.choice).elementAt(2).accept(this);
        break;
      case 2:
        // %02 Block() : f1 -> ( BlockStatement() )*
        if (((Block) n.f0.choice).f1.present())
          ((Block) n.f0.choice).f1.accept(this);
        break;
      case 5:
        // %05 SwitchStatement() : f5 -> ( SwitchLabel() ( BlockStatement() )* )*<br>
        final NodeListOptional nlo5 = ((SwitchStatement) n.f0.choice).f5;
        if (nlo5.present()) {
          for (final Iterator<INode> e = nlo5.elements(); e.hasNext();) {
            final NodeSequence seq5 = (NodeSequence) e.next();
            if (((NodeListOptional) seq5.elementAt(1)).present())
              seq5.elementAt(1).accept(this);
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
      case 12:
        // %12 ReturnStatement()
        final ReturnStatement rs = (ReturnStatement) n.f0.choice;
        // f1 -> [ Expression() ]
        if (rs.f1.present())
          if (gdfv.getNcnHT().get(prod) != null)
            Messages.softErr("Non empty return statement in a Java block in Production '" + prod +
                             "()' for which it has been requested in its declaration " +
                             "not to create the node.", rs.f0.beginLine, rs.f0.beginColumn);
          else
            Messages.info("Non empty return statement in a Java block in Production '" + prod +
                              "()'. It will be transformed in an assign statement to the " +
                              "corresponding new parser class variable.", rs.f0.beginLine,
                          rs.f0.beginColumn);
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
          for (final Iterator<INode> e = nlo15.elements(); e.hasNext();) {
            ((NodeSequence) e.next()).elementAt(4).accept(this);
          }
        }
        // f3 -> [ "finally" Block() ]
        final NodeOptional opt15 = ((TryStatement) n.f0.choice).f3;
        if (opt15.present()) {
          ((NodeSequence) opt15.node).elementAt(1).accept(this);
        }
        break;
      default:
        break;
    }
  }

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
    // f0 -> Expansion()
    n.f0.accept(this);
    // f1 -> ( #0 "|" #1 Expansion() )*
    final NodeListOptional nlo = n.f1;
    if (nlo.present()) {
      for (final Iterator<INode> e = nlo.elements(); e.hasNext();) {
        ((NodeSequence) e.next()).elementAt(1).accept(this);
      }
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
    // visit only f1 -> ( ExpansionUnit() )+
    n.f1.accept(this);
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
    switch (n.f0.which) {
      case 0:
        // %0 #0 "LOOKAHEAD" #1 "(" #2 LocalLookahead() #3 ")"
        return;

      case 1:
        // %1 Block()
        n.f0.choice.accept(this);
        return;

      case 2:
        // %2 #0 "[" #1 ExpansionChoices() #2 "]"
        ((NodeSequence) n.f0.choice).elementAt(1).accept(this);
        return;

      case 3:
        // %3 ExpansionUnitTCF()
        n.f0.choice.accept(this);
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
            final NodeToken iasToken = ((IdentifierAsString) seq1.elementAt(0)).f0;
            final String ident = iasToken.tokenImage;
            if (gdfv.getRetVarIdentHT().get(ident) == null) {
              if (gdfv.getNcnHT().get(ident) == null)
                Messages.softErr("Use in an assignment of the (BNF or Javacode) Production '" +
                                     ident + "()' of type void.", iasToken.beginLine,
                                 iasToken.beginColumn);
              else
                Messages.softErr("Use in an assignment of the (BNF or Javacode) Production '" +
                                     ident + "()' for which it has been requested in its " +
                                     "declaration not to create the node.", iasToken.beginLine,
                                 iasToken.beginColumn);
            }
          }
        }

        if (ch.which == 0) {
          // $0 IdentifierAsString() $1 Arguments() $2 [ "!" ]
          // display messages
          final NodeToken iasToken = ((IdentifierAsString) seq1.elementAt(0)).f0;
          final String ident = iasToken.tokenImage;
          // messages in decreasing severity order for proper displaying in the plugin
          if (((NodeOptional) seq1.elementAt(2)).present())
            if (opt.present()) {
              Messages.softErr("Use in an assignment of a BNFProduction '" + ident +
                               "()' for which it has been requested here " +
                               "not to create the node.", iasToken.beginLine, iasToken.beginColumn);
            } else {
              Messages.info("The corresponding JTB node creation will NOT be generated here "
                            + "(as requested here).", iasToken.beginLine, iasToken.beginColumn);
            }
          if (gdfv.getNcnHT().get(ident) != null)
            Messages.info("The corresponding JTB node creation will NOT be generated here "
                              + "(as requested in the BNFProduction declaration).",
                          iasToken.beginLine,
                          iasToken.beginColumn);
        } else {
          // $0 RegularExpression() $1 [ ?0 "." ?1 < IDENTIFIER > ] $2 [ "!" ]
          // display messages
          NodeToken tk = null;
          int lnre = 0;
          int cnre = 0;
          final RegularExpression re = (RegularExpression) seq1.elementAt(0);
          if (re.f0.which == 0) {
            // %0 StringLiteral()
            tk = ((StringLiteral) re.f0.choice).f0;
            lnre = tk.beginLine;
            cnre = tk.beginColumn;
          } else {
            // %1 #0 "<" #1 [ $0 [ "#" ] $1 IdentifierAsString() $2 ":" ] #2 ComplexRegularExpressionChoices() #3 ">"
            // %2 #0 "<" #1 IdentifierAsString() #2 ">"
            // %3 #0 "<" #1 "EOF" #2 ">"
            final NodeSequence seq2 = (NodeSequence) re.f0.choice;
            if (re.f0.which == 2)
              // %2 #0 "<" #1 IdentifierAsString() #2 ">"
              tk = ((IdentifierAsString) seq2.elementAt(1)).f0;
            lnre = ((NodeToken) seq2.elementAt(0)).beginLine;
            cnre = ((NodeToken) seq2.elementAt(0)).beginColumn;
          }
          // messages in decreasing severity order for proper displaying in the plugin
          if (((NodeOptional) seq1.elementAt(2)).present())
            if (opt.present())
              Messages.softErr("Use in an assignment of a RegularExpression for which it has been "
                               + "requested here not to create the node.", lnre, cnre);
            else
              Messages.info("The corresponding JTB node creation will NOT be generated here "
                            + "(as requested here).", lnre, cnre);
          if (tk != null && DONT_CREATE_NODE_STR.equals(gdfv.getTokenHT().get(tk.tokenImage)))
            Messages.info("The corresponding JTB node creation will NOT be generated here "
                          + "(as requested in the RegExprSpec declaration).", lnre, cnre);
        }

        return;

      case 5:
        // %5 #0 "(" #1 ExpansionChoices() #2 ")"
        // .. #3 ( &0 "+" | &1 "*" | &2 "?" )?
        seq = (NodeSequence) n.f0.choice;
        final ExpansionChoices choice = (ExpansionChoices) seq.elementAt(1);
        final NodeOptional mod = (NodeOptional) seq.elementAt(3);
        if (!mod.present() && !choice.f1.present()) {
          final NodeToken tk = (NodeToken) ((NodeSequence) n.f0.choice).elementAt(0);
          Messages.info("Unnecessary parentheses in '" + prod + "()'.", tk.beginLine,
                        tk.beginColumn);
        }
        return;

      default:
        Messages.hardErr("invalid n.f0.which = " + String.valueOf(n.f0.which));
        return;
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
    // f2 -> ExpansionChoices()
    n.f2.accept(this);
    // f4 -> ( #0 "catch" #1 "(" #2 Name() #3 < IDENTIFIER > #4 ")" #5 Block() )*
    if (n.f4.present()) {
      for (int i = 0; i < n.f4.size(); i++) {
        // #5 Block()
        ((NodeSequence) n.f4.elementAt(i)).elementAt(5).accept(this);
      }
    }
    // f5 -> [ #0 "finally" #1 Block() ]
    if (n.f5.present()) {
      // #1 Block()
      ((NodeSequence) n.f5.node).elementAt(1).accept(this);
    }
  }

  /**
   * Visits a {@link LocalLookahead} node, whose children are the following :
   * <p>
   * f0 -> [ IntegerLiteral() ]<br>
   * f1 -> [ "," ]<br>
   * f2 -> [ ExpansionChoices() ]<br>
   * f3 -> [ "," ]<br>
   * f4 -> [ #0 "{" #1 Expression() #2 "}" ]<br>
   * 
   * @param n - the node to visit
   */
  @Override
  public void visit(@SuppressWarnings("unused") final LocalLookahead n) {
    // should not be called !
  }

/**
   * Visits the {@link RegularExpression node}<br>
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
  public void visit(@SuppressWarnings("unused") final RegularExpression n) {
    // should not be called !
  }

}
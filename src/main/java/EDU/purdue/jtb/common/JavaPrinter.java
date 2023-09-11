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
//
// Modified for use with JTB bootstrap.
//
// Pretty printer for the Java grammar.
// Author: Kevin Tao, taokr@cs.purdue.edu
//
// (reminders for myself):
// - spc.spc should be printed after every LS or println().
// - println() should not be the last thing printed in a visit method.
// - always copy this file from the JTB source and remove the comments around
// Spacing at the bottom. Remove the import for misc.Spacing.
//
package EDU.purdue.jtb.common;

import static EDU.purdue.jtb.common.Constants.DEBUG_CLASS;
import static EDU.purdue.jtb.common.Constants.INDENT_AMT;
import static EDU.purdue.jtb.parser.syntaxtree.NodeConstants.*;
import java.util.Iterator;
import EDU.purdue.jtb.parser.syntaxtree.AdditiveExpression;
import EDU.purdue.jtb.parser.syntaxtree.AllocationExpression;
import EDU.purdue.jtb.parser.syntaxtree.AndExpression;
import EDU.purdue.jtb.parser.syntaxtree.AnnotationTypeBody;
import EDU.purdue.jtb.parser.syntaxtree.AnnotationTypeDeclaration;
import EDU.purdue.jtb.parser.syntaxtree.AnnotationTypeMemberDeclaration;
import EDU.purdue.jtb.parser.syntaxtree.ArgumentList;
import EDU.purdue.jtb.parser.syntaxtree.ArrayInitializer;
import EDU.purdue.jtb.parser.syntaxtree.AssertStatement;
import EDU.purdue.jtb.parser.syntaxtree.Block;
import EDU.purdue.jtb.parser.syntaxtree.BooleanLiteral;
import EDU.purdue.jtb.parser.syntaxtree.BreakStatement;
import EDU.purdue.jtb.parser.syntaxtree.CastLookahead;
import EDU.purdue.jtb.parser.syntaxtree.ClassOrInterfaceBody;
import EDU.purdue.jtb.parser.syntaxtree.ClassOrInterfaceDeclaration;
import EDU.purdue.jtb.parser.syntaxtree.CompilationUnit;
import EDU.purdue.jtb.parser.syntaxtree.ConditionalAndExpression;
import EDU.purdue.jtb.parser.syntaxtree.ConditionalExpression;
import EDU.purdue.jtb.parser.syntaxtree.ConditionalOrExpression;
import EDU.purdue.jtb.parser.syntaxtree.ConstructorDeclaration;
import EDU.purdue.jtb.parser.syntaxtree.ContinueStatement;
import EDU.purdue.jtb.parser.syntaxtree.DoStatement;
import EDU.purdue.jtb.parser.syntaxtree.EnumBody;
import EDU.purdue.jtb.parser.syntaxtree.EnumDeclaration;
import EDU.purdue.jtb.parser.syntaxtree.EqualityExpression;
import EDU.purdue.jtb.parser.syntaxtree.ExclusiveOrExpression;
import EDU.purdue.jtb.parser.syntaxtree.ExplicitConstructorInvocation;
import EDU.purdue.jtb.parser.syntaxtree.Expression;
import EDU.purdue.jtb.parser.syntaxtree.ExtendsList;
import EDU.purdue.jtb.parser.syntaxtree.FieldDeclaration;
import EDU.purdue.jtb.parser.syntaxtree.ForStatement;
import EDU.purdue.jtb.parser.syntaxtree.FormalParameter;
import EDU.purdue.jtb.parser.syntaxtree.FormalParameters;
import EDU.purdue.jtb.parser.syntaxtree.INode;
import EDU.purdue.jtb.parser.syntaxtree.IfStatement;
import EDU.purdue.jtb.parser.syntaxtree.ImplementsList;
import EDU.purdue.jtb.parser.syntaxtree.ImportDeclaration;
import EDU.purdue.jtb.parser.syntaxtree.InclusiveOrExpression;
import EDU.purdue.jtb.parser.syntaxtree.Initializer;
import EDU.purdue.jtb.parser.syntaxtree.InstanceOfExpression;
import EDU.purdue.jtb.parser.syntaxtree.IntegerLiteral;
import EDU.purdue.jtb.parser.syntaxtree.LabeledStatement;
import EDU.purdue.jtb.parser.syntaxtree.Literal;
import EDU.purdue.jtb.parser.syntaxtree.LocalVariableDeclaration;
import EDU.purdue.jtb.parser.syntaxtree.MemberValue;
import EDU.purdue.jtb.parser.syntaxtree.MemberValueArrayInitializer;
import EDU.purdue.jtb.parser.syntaxtree.MemberValuePair;
import EDU.purdue.jtb.parser.syntaxtree.MemberValuePairs;
import EDU.purdue.jtb.parser.syntaxtree.MethodDeclaration;
import EDU.purdue.jtb.parser.syntaxtree.Modifiers;
import EDU.purdue.jtb.parser.syntaxtree.MultiplicativeExpression;
import EDU.purdue.jtb.parser.syntaxtree.NameList;
import EDU.purdue.jtb.parser.syntaxtree.NodeChoice;
import EDU.purdue.jtb.parser.syntaxtree.NodeListOptional;
import EDU.purdue.jtb.parser.syntaxtree.NodeOptional;
import EDU.purdue.jtb.parser.syntaxtree.NodeSequence;
import EDU.purdue.jtb.parser.Token;
import EDU.purdue.jtb.parser.syntaxtree.NullLiteral;
import EDU.purdue.jtb.parser.syntaxtree.PackageDeclaration;
import EDU.purdue.jtb.parser.syntaxtree.RelationalExpression;
import EDU.purdue.jtb.parser.syntaxtree.ReturnStatement;
import EDU.purdue.jtb.parser.syntaxtree.ShiftExpression;
import EDU.purdue.jtb.parser.syntaxtree.Statement;
import EDU.purdue.jtb.parser.syntaxtree.StatementExpression;
import EDU.purdue.jtb.parser.syntaxtree.StatementExpressionList;
import EDU.purdue.jtb.parser.syntaxtree.StringLiteral;
import EDU.purdue.jtb.parser.syntaxtree.SwitchLabel;
import EDU.purdue.jtb.parser.syntaxtree.SwitchStatement;
import EDU.purdue.jtb.parser.syntaxtree.SynchronizedStatement;
import EDU.purdue.jtb.parser.syntaxtree.ThrowStatement;
import EDU.purdue.jtb.parser.syntaxtree.TryStatement;
import EDU.purdue.jtb.parser.syntaxtree.TypeArgument;
import EDU.purdue.jtb.parser.syntaxtree.TypeArguments;
import EDU.purdue.jtb.parser.syntaxtree.TypeBound;
import EDU.purdue.jtb.parser.syntaxtree.TypeParameter;
import EDU.purdue.jtb.parser.syntaxtree.TypeParameters;
import EDU.purdue.jtb.parser.syntaxtree.VariableDeclarator;
import EDU.purdue.jtb.parser.syntaxtree.VariableModifiers;
import EDU.purdue.jtb.parser.syntaxtree.WhileStatement;
import EDU.purdue.jtb.parser.syntaxtree.WildcardBounds;
import EDU.purdue.jtb.parser.visitor.DepthFirstVoidVisitor;
import EDU.purdue.jtb.parser.visitor.signature.NodeFieldsSignature;

/**
 * The {@link JavaPrinter} visitor reprints (with indentation) JavaCC grammar Java specific productions.
 * CODEJAVA
 * <p>
 * Notes :
 * <ul>
 * <li>it merely adds spaces and new lines (LS) to the default visitor between
 * identifiers/keywords/punctuation</li>
 * <li>so methods have been copied from the default visitor, variables have been inlined, and for index-loops
 * have been refactored to for-each loops</li>
 * <li>sb.append(spc.spc), sb.append(' ') and sb.append(LS) are done at the highest (calling) level (except
 * for Modifiers() and VariableModifiers() which prints the last space if not empty)</li>
 * <li>sb.append(spc.spc) is done after sb.append(LS)</li>
 * <li>sb.append(' ') is not merged with printing punctuation / operators (to prepare evolutions for other
 * formatting preferences), but is indeed merged with printing keywords</li>
 * </ul>
 * <p>
 * Visitor maintains state (for a grammar), and not supposed to be run in parallel threads (on the same
 * grammar).
 * </p>
 * TODO extract / refactor methods for custom formatting<br>
 * TESTCASE some to add
 *
 * @author Marc Mazas
 * @version 1.4.0 : 05-08/2009 : MMa : adapted to JavaCC v4.2 grammar and JDK 1.5
 * @version 1.4.3 : 03/2010 : MMa : fixed output of else in IfStatement
 * @version 1.4.4 : 07/2010 : MMa : fixed output after throws in MethodDeclaration, ConstructorDeclaration,
 *          and wrong index in TypeArguments
 * @version 1.4.7 : 07/2012 : MMa : followed changes in jtbgram.jtb (on PackageDeclaration(),
 *          VariableModifiers() IndentifierAsString())
 * @version 1.4.8 : 11/2014 : MMa : followed changes in jtbgram.jtb (on ClassOrInterfaceBodyDeclaration(),
 *          ExplicitConstructorInvocation())
 * @version 1.5.0 : 01-03/2017 : MMa : used try-with-resource ; changed some iterator based for loops to
 *          enhanced for loops ; applied UCDetector advices<br>
 *          1.5.0 02/2018 : MMa : removed methods identical to superclass<br>
 *          1.5.0 04/2021 : MMa : added missing visit(final TypeParameter n)<br>
 * @version 1.5.1 : 08/2023 : MMa : editing changes for coverage analysis; changes due to the NodeToken
 *          replacement by Token
 * @version 1.5.1 : 09/2023 : MMa : removed noDebugComment flag
 */
public class JavaPrinter extends DepthFirstVoidVisitor {
  
  /** The global JTB options */
  private final JTBOptions jopt;
  /** The buffer to print into */
  protected StringBuilder  sb;
  /** The indentation object */
  protected Spacing        spc;
  /** The OS line separator */
  static final String      LS     = System.getProperty("line.separator");
  /** The node class comment prefix */
  public String            JNCDCP = " //jp ";
  
  /**
   * Constructor with a given buffer and indentation.
   *
   * @param aJopt - the JTB options
   * @param aSb - the buffer to print into (will be allocated if null)
   * @param aSPC - the Spacing indentation object (will be allocated and set to a default if null)
   */
  protected JavaPrinter(final JTBOptions aJopt, final StringBuilder aSb, final Spacing aSPC) {
    jopt = aJopt;
    reset(aSb, aSPC);
  }
  
  /**
   * Resets the buffer and the indentation.
   *
   * @param aSb - the buffer to print into (will be allocated if null)
   * @param aSPC - the Spacing indentation object (will be allocated and set to a default if null)
   */
  final void reset(final StringBuilder aSb, final Spacing aSPC) {
    sb = aSb;
    if (sb == null) {
      sb = new StringBuilder(2048);
    }
    spc = aSPC;
    if (spc == null) {
      spc = new Spacing(INDENT_AMT);
    }
  }
  
  /*
   * Base classes visit methods
   */
  
  /**
   * Visits a Token.
   *
   * @param n - the node to visit
   */
  @Override
  public void visit(final Token n) {
    if (jopt.printSpecialTokensJJ) {
      sb.append(n.withSpecials(spc.spc));
    } else {
      sb.append(n.image);
    }
  }
  
  /*
   * Convenience methods
   */
  
  /**
   * Prints into the current buffer a node class comment and a new line.
   *
   * @param n - the node for the node class comment
   */
  protected void oneNewLine(final INode n) {
    sb.append(nodeClassComment(n)).append(LS);
  }
  
  /**
   * Prints into the current buffer a node class comment, an extra given comment, and a new line.
   *
   * @param n - the node for the node class comment
   * @param str - the extra comment
   */
  protected void oneNewLine(final INode n, final String str) {
    sb.append(nodeClassComment(n, str)).append(LS);
  }
  
  /**
   * Prints twice into the current buffer a node class comment and a new line.
   *
   * @param n - the node for the node class comment
   */
  protected void twoNewLines(final INode n) {
    oneNewLine(n);
    oneNewLine(n);
  }
  
  /**
   * Prints twice into the current buffer a node class comment, an extra given comment, and a new line.
   *
   * @param n - the node for the node class comment
   * @param str - the extra comment
   */
  protected void twoNewLines(final INode n, final String str) {
    oneNewLine(n, str);
    oneNewLine(n, str);
  }
  
  /**
   * Returns a node class comment with an extra comment (a //jvp followed by the node class short name if
   * global flag set, nothing otherwise).
   *
   * @param n - the node to process
   * @return the comment
   */
  private String nodeClassComment(final INode n) {
    if (DEBUG_CLASS) {
      final String s = n.toString();
      final int b = s.lastIndexOf('.') + 1;
      final int e = s.indexOf('@');
      if ((b == -1) || (e == -1)) {
        // this case is only if the INode toString() has been overriden
        return JNCDCP + s;
      } else {
        return JNCDCP + s.substring(b, e);
      }
    } else {
      return "";
    }
  }
  
  /**
   * Returns a node class comment with an extra comment (a //jvp followed by the node class short name plus
   * the extra comment if global flag set, nothing otherwise).
   *
   * @param n - the node to process
   * @param str - the string to add to the comment
   * @return the comment
   */
  private String nodeClassComment(final INode n, final String str) {
    if (DEBUG_CLASS) {
      return nodeClassComment(n) + " " + str;
    } else {
      return "";
    }
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
    // coverage: probably never used as JJFileAnnotator$CompilationUnitPrinter should be used
    // we do not use sb.append(spc.spc) as indent level should be 0 at this point
    // f0 -> [ PackageDeclaration() ]
    if (n.f0.present()) {
      n.f0.accept(this);
      oneNewLine(n);
    }
    // f1 -> ( ImportDeclaration() )*
    if (n.f1.present()) {
      for (final INode e : n.f1.nodes) {
        e.accept(this);
        oneNewLine(n);
      }
      oneNewLine(n);
    }
    // f2 -> ( TypeDeclaration() )*
    if (n.f2.present()) {
      for (final INode e : n.f2.nodes) {
        e.accept(this);
        oneNewLine(n);
      }
      oneNewLine(n);
    }
  }
  
  /**
   * Visits a {@link PackageDeclaration} node, whose children are the following :
   * <p>
   * f0 -> "package"<br>
   * f1 -> Name()<br>
   * f2 -> ";"<br>
   * s: -2133750237<br>
   *
   * @param n - the node to visit
   */
  @Override
  @NodeFieldsSignature({
      -2133750237, JTB_SIG_PACKAGEDECLARATION, JTB_USER_PACKAGEDECLARATION
  })
  public void visit(final PackageDeclaration n) {
    // f0 -> "package"
    n.f0.accept(this);
    sb.append(' ');
    // f1 -> Name()
    n.f1.accept(this);
    // f2 -> ";"
    n.f2.accept(this);
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
    // probably never used as JJFileAnnotator$CompilationUnitPrinter should be used
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
   * Visits a {@link Modifiers} node, whose child is the following :
   * <p>
   * f0 -> ( ( %00 "public"<br>
   * .. .. . | %01 "static"<br>
   * .. .. . | %02 "protected"<br>
   * .. .. . | %03 "private"<br>
   * .. .. . | %04 "final"<br>
   * .. .. . | %05 "abstract"<br>
   * .. .. . | %06 "synchronized"<br>
   * .. .. . | %07 "native"<br>
   * .. .. . | %08 "transient"<br>
   * .. .. . | %09 "volatile"<br>
   * .. .. . | %10 "strictfp"<br>
   * .. .. . | %11 Annotation() ) )*<br>
   * s: -2047145049<br>
   *
   * @param n - the node to visit
   */
  @Override
  @NodeFieldsSignature({
      -2047145049, JTB_SIG_MODIFIERS, JTB_USER_MODIFIERS
  })
  public void visit(final Modifiers n) {
    if (n.f0.present()) {
      for (final INode e : n.f0.nodes) {
        e.accept(this);
        // Modifiers print the last space if not empty
        sb.append(' ');
      }
    }
  }
  
  /**
   * Visits a {@link ClassOrInterfaceDeclaration} node, whose children are the following :
   * <p>
   * f0 -> ( %0 "class"<br>
   * .. .. | %1 "interface" )<br>
   * f1 -> < IDENTIFIER ><br>
   * f2 -> [ TypeParameters() ]<br>
   * f3 -> [ ExtendsList() ]<br>
   * f4 -> [ ImplementsList() ]<br>
   * f5 -> ClassOrInterfaceBody()<br>
   * s: 37426766<br>
   *
   * @param n - the node to visit
   */
  @Override
  @NodeFieldsSignature({
      37426766, JTB_SIG_CLASSORINTERFACEDECLARATION, JTB_USER_CLASSORINTERFACEDECLARATION
  })
  public void visit(final ClassOrInterfaceDeclaration n) {
    // f0 -> ( %0 "class" | %1 "interface" )
    n.f0.accept(this);
    sb.append(' ');
    // f1 -> < IDENTIFIER >
    n.f1.accept(this);
    sb.append(' ');
    // f2 -> [ TypeParameters() ]
    if (n.f2.present()) {
      n.f2.accept(this);
      sb.append(' ');
    }
    // f3 -> [ ExtendsList() ]
    if (n.f3.present()) {
      n.f3.accept(this);
      sb.append(' ');
    }
    // f4 -> [ ImplementsList() ]
    if (n.f4.present()) {
      n.f4.accept(this);
      sb.append(' ');
    }
    // f5 -> ClassOrInterfaceBody()
    n.f5.accept(this);
  }
  
  /**
   * Visits a {@link ExtendsList} node, whose children are the following :
   * <p>
   * f0 -> "extends"<br>
   * f1 -> ClassOrInterfaceType()<br>
   * f2 -> ( #0 "," #1 ClassOrInterfaceType() )*<br>
   * s: 24279225<br>
   *
   * @param n - the node to visit
   */
  @Override
  @NodeFieldsSignature({
      24279225, JTB_SIG_EXTENDSLIST, JTB_USER_EXTENDSLIST
  })
  public void visit(final ExtendsList n) {
    // f0 -> "extends"
    n.f0.accept(this);
    sb.append(' ');
    // f1 -> ClassOrInterfaceType()
    n.f1.accept(this);
    // f2 -> ( #0 "," #1 ClassOrInterfaceType() )*
    if (n.f2.present()) {
      for (final INode e : n.f2.nodes) {
        sb.append(' ');
        final NodeSequence seq = (NodeSequence) e;
        // #0 "&"
        seq.elementAt(0).accept(this);
        sb.append(' ');
        // #1 ClassOrInterfaceType()
        seq.elementAt(1).accept(this);
      }
    }
  }
  
  /**
   * Visits a {@link ImplementsList} node, whose children are the following :
   * <p>
   * f0 -> "implements"<br>
   * f1 -> ClassOrInterfaceType()<br>
   * f2 -> ( #0 "," #1 ClassOrInterfaceType() )*<br>
   * s: 1830366786<br>
   *
   * @param n - the node to visit
   */
  @Override
  @NodeFieldsSignature({
      1830366786, JTB_SIG_IMPLEMENTSLIST, JTB_USER_IMPLEMENTSLIST
  })
  public void visit(final ImplementsList n) {
    // f0 -> "implements"
    n.f0.accept(this);
    sb.append(' ');
    // f1 -> ClassOrInterfaceType()
    n.f1.accept(this);
    // f2 -> ( #0 "," #1 ClassOrInterfaceType() )*
    if (n.f2.present()) {
      for (final INode e : n.f2.nodes) {
        sb.append(' ');
        final NodeSequence seq = (NodeSequence) e;
        // #0 ","
        seq.elementAt(0).accept(this);
        sb.append(' ');
        // #1 ClassOrInterfaceType()
        seq.elementAt(1).accept(this);
      }
    }
  }
  
  /**
   * Visits a {@link EnumDeclaration} node, whose children are the following :
   * <p>
   * f0 -> "enum"<br>
   * f1 -> < IDENTIFIER ><br>
   * f2 -> [ ImplementsList() ]<br>
   * f3 -> EnumBody()<br>
   * s: 359041865<br>
   *
   * @param n - the node to visit
   */
  @Override
  @NodeFieldsSignature({
      359041865, JTB_SIG_ENUMDECLARATION, JTB_USER_ENUMDECLARATION
  })
  public void visit(final EnumDeclaration n) {
    // f0 -> "enum"
    n.f0.accept(this);
    sb.append(' ');
    // f1 -> < IDENTIFIER >
    n.f1.accept(this);
    sb.append(' ');
    // f2 -> [ ImplementsList() ]
    if (n.f2.present()) {
      n.f2.accept(this);
      sb.append(' ');
    }
    // f3 -> EnumBody()
    n.f3.accept(this);
  }
  
  /**
   * Visits a {@link EnumBody} node, whose children are the following :
   * <p>
   * f0 -> "{"<br>
   * f1 -> [ #0 EnumConstant()<br>
   * .. .. . #1 ( $0 "," $1 EnumConstant() )* ]<br>
   * f2 -> [ "," ]<br>
   * f3 -> [ #0 ";"<br>
   * .. .. . #1 ( ClassOrInterfaceBodyDeclaration() )* ]<br>
   * f4 -> "}"<br>
   * s: -1338633176<br>
   *
   * @param n - the node to visit
   */
  @Override
  @NodeFieldsSignature({
      -1338633176, JTB_SIG_ENUMBODY, JTB_USER_ENUMBODY
  })
  public void visit(final EnumBody n) {
    // f0 -> "{"
    n.f0.accept(this);
    spc.updateSpc(+1);
    oneNewLine(n);
    sb.append(spc.spc);
    // f1 -> [ #0 EnumConstant() #1 ( $0 "," $1 EnumConstant() )* ]
    if (n.f1.present()) {
      final NodeSequence seq = (NodeSequence) n.f1.node;
      // #0 EnumConstant()
      seq.elementAt(0).accept(this);
      // #1 ( $0 "," $1 EnumConstant() )*
      final NodeListOptional nlo = (NodeListOptional) seq.elementAt(1);
      if (nlo.present()) {
        for (final INode e : nlo.nodes) {
          final NodeSequence seq1 = (NodeSequence) e;
          // $0 ","
          seq1.elementAt(0).accept(this);
          sb.append(' ');
          // $1 EnumConstant()
          seq1.elementAt(1).accept(this);
        }
      }
    }
    // f2 -> [ "," ]
    if (n.f2.present()) {
      n.f2.accept(this);
      sb.append(' ');
    }
    // f3 -> [ #0 ";" #1 ( ClassOrInterfaceBodyDeclaration() )* ]
    if (n.f3.present()) {
      // #0 ";"
      ((NodeSequence) n.f3.node).elementAt(0).accept(this);
      oneNewLine(n);
      sb.append(spc.spc);
      // #1 ( ClassOrInterfaceBodyDeclaration() )*
      final NodeListOptional nlo = (NodeListOptional) ((NodeSequence) n.f3.node).elementAt(1);
      if (nlo.present()) {
        for (final INode e : nlo.nodes) {
          // ClassOrInterfaceBodyDeclaration()
          e.accept(this);
        }
      }
    }
    spc.updateSpc(-1);
    oneNewLine(n);
    sb.append(spc.spc);
    // f4 -> "}"
    n.f4.accept(this);
  }
  
  /**
   * Visits a {@link TypeParameters} node, whose children are the following :
   * <p>
   * f0 -> "<"<br>
   * f1 -> TypeParameter()<br>
   * f2 -> ( #0 "," #1 TypeParameter() )*<br>
   * f3 -> ">"<br>
   * s: 1962566888<br>
   *
   * @param n - the node to visit
   */
  @Override
  @NodeFieldsSignature({
      1962566888, JTB_SIG_TYPEPARAMETERS, JTB_USER_TYPEPARAMETERS
  })
  public void visit(final TypeParameters n) {
    // f0 -> "<"
    n.f0.accept(this);
    // f1 -> TypeParameter()
    n.f1.accept(this);
    // f2 -> ( #0 "," #1 TypeParameter() )*
    if (n.f2.present()) {
      for (final INode e : n.f2.nodes) {
        final NodeSequence seq = (NodeSequence) e;
        // #0 ","
        seq.elementAt(0).accept(this);
        sb.append(' ');
        // #1 TypeParameter()
        seq.elementAt(1).accept(this);
      }
    }
    // f3 -> ">"
    n.f3.accept(this);
  }
  
  /**
   * Visits a {@link TypeParameter} node, whose children are the following :
   * <p>
   * f0 -> < IDENTIFIER > //cp Expansion expLvl==0<br>
   * f1 -> [ TypeBound() ] //cp ExpansionChoices only f0<br>
   * s: 1306471903<br>
   *
   * @param n - the node to visit
   */
  @Override
  @NodeFieldsSignature({
      1306471903, JTB_SIG_TYPEPARAMETER, JTB_USER_TYPEPARAMETER
  })
  public void visit(final TypeParameter n) {
    // f0 -> < IDENTIFIER >
    final Token n0 = n.f0;
    n0.accept(this);
    // f1 -> [ TypeBound() ]
    final NodeOptional n1 = n.f1;
    if (n1.present()) {
      sb.append(' ');
      n1.accept(this);
    }
  }
  
  /**
   * Visits a {@link TypeBound} node, whose children are the following :
   * <p>
   * f0 -> "extends"<br>
   * f1 -> ClassOrInterfaceType()<br>
   * f2 -> ( #0 "&" #1 ClassOrInterfaceType() )*<br>
   * s: -2080520397<br>
   *
   * @param n - the node to visit
   */
  @Override
  @NodeFieldsSignature({
      -2080520397, JTB_SIG_TYPEBOUND, JTB_USER_TYPEBOUND
  })
  public void visit(final TypeBound n) {
    // f0 -> "extends"
    n.f0.accept(this);
    sb.append(' ');
    // f1 -> ClassOrInterfaceType()
    n.f1.accept(this);
    // f2 -> ( #0 "&" #1 ClassOrInterfaceType() )*
    if (n.f2.present()) {
      for (final INode e : n.f2.nodes) {
        sb.append(' ');
        final NodeSequence seq = (NodeSequence) e;
        // #0 "&"
        seq.elementAt(0).accept(this);
        sb.append(' ');
        // #1 ClassOrInterfaceType()
        seq.elementAt(1).accept(this);
      }
    }
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
    // f1 -> ( ClassOrInterfaceBodyDeclaration() )*
    if (n.f1.present()) {
      oneNewLine(n, "a");
      spc.updateSpc(+1);
      oneNewLine(n, "b");
      sb.append(spc.spc);
      for (final Iterator<INode> e = n.f1.elements(); e.hasNext();) {
        e.next().accept(this);
        oneNewLine(n, "c");
        if (e.hasNext()) {
          oneNewLine(n, "d");
          sb.append(spc.spc);
        }
      }
      spc.updateSpc(-1);
    }
    sb.append(spc.spc);
    // f2 -> "}"
    n.f2.accept(this);
    oneNewLine(n, "e");
  }
  
  /**
   * Visits a {@link FieldDeclaration} node, whose children are the following :
   * <p>
   * f0 -> Type()<br>
   * f1 -> VariableDeclarator()<br>
   * f2 -> ( #0 "," #1 VariableDeclarator() )*<br>
   * f3 -> ";"<br>
   * s: 1567612384<br>
   *
   * @param n - the node to visit
   */
  @Override
  @NodeFieldsSignature({
      1567612384, JTB_SIG_FIELDDECLARATION, JTB_USER_FIELDDECLARATION
  })
  public void visit(final FieldDeclaration n) {
    // f0 -> Type()
    n.f0.accept(this);
    sb.append(' ');
    // f1 -> VariableDeclarator()
    n.f1.accept(this);
    // f2 -> ( #0 "," #1 VariableDeclarator() )*
    if (n.f2.present()) {
      for (final INode e : n.f2.nodes) {
        // #0 ","
        final NodeSequence seq = (NodeSequence) e;
        seq.elementAt(0).accept(this);
        sb.append(' ');
        // #1 VariableDeclarator()
        seq.elementAt(1).accept(this);
      }
    }
    // f3 -> ";"
    n.f3.accept(this);
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
  @NodeFieldsSignature({
      -484955779, JTB_SIG_VARIABLEDECLARATOR, JTB_USER_VARIABLEDECLARATOR
  })
  public void visit(final VariableDeclarator n) {
    // f0 -> VariableDeclaratorId()
    n.f0.accept(this);
    // f1 -> [ #0 "=" #1 VariableInitializer() ]
    if (n.f1.present()) {
      // #0 "="
      sb.append(' ');
      ((NodeSequence) n.f1.node).elementAt(0).accept(this);
      sb.append(' ');
      // #1 VariableInitializer()
      ((NodeSequence) n.f1.node).elementAt(1).accept(this);
    }
  }
  
  /**
   * Visits a {@link ArrayInitializer} node, whose children are the following :
   * <p>
   * f0 -> "{"<br>
   * f1 -> [ #0 VariableInitializer()<br>
   * .. .. . #1 ( $0 "," $1 VariableInitializer() )* ]<br>
   * f2 -> [ "," ]<br>
   * f3 -> "}"<br>
   * s: -251326055<br>
   *
   * @param n - the node to visit
   */
  @Override
  @NodeFieldsSignature({
      -251326055, JTB_SIG_ARRAYINITIALIZER, JTB_USER_ARRAYINITIALIZER
  })
  public void visit(final ArrayInitializer n) {
    // f0 -> "{"
    n.f0.accept(this);
    sb.append(' ');
    // f1 -> [ #0 VariableInitializer() #1 ( $0 "," $1 VariableInitializer() )* ]
    n.f1.accept(this);
    // f2 -> [ "," ]
    if (n.f2.present()) {
      n.f2.accept(this);
      sb.append(' ');
    }
    sb.append(' ');
    // f3 -> "}"
    n.f3.accept(this);
  }
  
  /**
   * Visits a {@link MethodDeclaration} node, whose children are the following :
   * <p>
   * f0 -> [ TypeParameters() ]<br>
   * f1 -> ResultType()<br>
   * f2 -> MethodDeclarator()<br>
   * f3 -> [ #0 "throws" #1 NameList() ]<br>
   * f4 -> ( %0 Block()<br>
   * .. .. | %1 ";" )<br>
   * s: -418256626<br>
   *
   * @param n - the node to visit
   */
  @Override
  @NodeFieldsSignature({
      -418256626, JTB_SIG_METHODDECLARATION, JTB_USER_METHODDECLARATION
  })
  public void visit(final MethodDeclaration n) {
    // f0 -> [ TypeParameters() ]
    if (n.f0.present()) {
      n.f0.accept(this);
      sb.append(' ');
    }
    // f1 -> ResultType()
    n.f1.accept(this);
    sb.append(' ');
    // f2 -> MethodDeclarator()
    n.f2.accept(this);
    sb.append(' ');
    // f3 -> [ #0 "throws" #1 NameList() ]
    if (n.f3.present()) {
      // #0 "throws"
      ((NodeSequence) n.f3.node).elementAt(0).accept(this);
      sb.append(' ');
      // #1 NameList()
      ((NodeSequence) n.f3.node).elementAt(1).accept(this);
    }
    // f4 -> ( %0 Block() | %1 ";" )
    n.f4.accept(this);
  }
  
  /**
   * Visits a {@link FormalParameters} node, whose children are the following :
   * <p>
   * f0 -> "("<br>
   * f1 -> [ #0 FormalParameter()<br>
   * .. .. . #1 ( $0 "," $1 FormalParameter() )* ]<br>
   * f2 -> ")"<br>
   * s: -97312104<br>
   *
   * @param n - the node to visit
   */
  @Override
  @NodeFieldsSignature({
      -97312104, JTB_SIG_FORMALPARAMETERS, JTB_USER_FORMALPARAMETERS
  })
  public void visit(final FormalParameters n) {
    // f0 -> "("
    n.f0.accept(this);
    // f1 -> [ #0 FormalParameter() #1 ( $0 "," $1 FormalParameter() )* ]
    if (n.f1.present()) {
      final NodeSequence seq = (NodeSequence) n.f1.node;
      // #0 FormalParameter()
      seq.elementAt(0).accept(this);
      // #1 ( $0 "," $1 FormalParameter() )*
      for (final INode e : ((NodeListOptional) seq.elementAt(1)).nodes) {
        final NodeSequence seq1 = (NodeSequence) e;
        // $0 ","
        seq1.elementAt(0).accept(this);
        sb.append(' ');
        // $1 FormalParameter()
        seq1.elementAt(1).accept(this);
      }
    }
    // f2 -> ")"
    n.f2.accept(this);
  }
  
  /**
   * Visits a {@link FormalParameter} node, whose children are the following :
   * <p>
   * f0 -> Modifiers()<br>
   * f1 -> Type()<br>
   * f2 -> [ "..." ]<br>
   * f3 -> VariableDeclaratorId()<br>
   * s: -1358852705<br>
   *
   * @param n - the node to visit
   */
  @Override
  @NodeFieldsSignature({
      -1358852705, JTB_SIG_FORMALPARAMETER, JTB_USER_FORMALPARAMETER
  })
  public void visit(final FormalParameter n) {
    // f0 -> Modifiers()
    n.f0.accept(this);
    // Modifiers print the last space if not empty
    // f1 -> Type()
    n.f1.accept(this);
    // f2 -> [ "..." ]
    if (n.f2.present()) {
      n.f2.accept(this);
    }
    sb.append(' ');
    // f3 -> VariableDeclaratorId()
    n.f3.accept(this);
  }
  
  /**
   * Visits a {@link ConstructorDeclaration} node, whose children are the following :
   * <p>
   * f0 -> [ TypeParameters() ]<br>
   * f1 -> < IDENTIFIER ><br>
   * f2 -> FormalParameters()<br>
   * f3 -> [ #0 "throws" #1 NameList() ]<br>
   * f4 -> "{"<br>
   * f5 -> [ ExplicitConstructorInvocation() ]<br>
   * f6 -> ( BlockStatement() )*<br>
   * f7 -> "}"<br>
   * s: 1258397065<br>
   *
   * @param n - the node to visit
   */
  @Override
  @NodeFieldsSignature({
      1258397065, JTB_SIG_CONSTRUCTORDECLARATION, JTB_USER_CONSTRUCTORDECLARATION
  })
  public void visit(final ConstructorDeclaration n) {
    // f0 -> [ TypeParameters() ]
    if (n.f0.present()) {
      n.f0.accept(this);
      sb.append(' ');
    }
    // f1 -> < IDENTIFIER >
    n.f1.accept(this);
    // f2 -> FormalParameters()
    n.f2.accept(this);
    sb.append(' ');
    // f3 -> [ #0 "throws" #1 NameList() ]
    if (n.f3.present()) {
      // #0 "throws"
      ((NodeSequence) n.f3.node).elementAt(0).accept(this);
      sb.append(' ');
      // #1 NameList()
      ((NodeSequence) n.f3.node).elementAt(1).accept(this);
    }
    sb.append(' ');
    // f4 -> "{"
    n.f4.accept(this);
    spc.updateSpc(+1);
    oneNewLine(n);
    sb.append(spc.spc);
    // f5 -> [ ExplicitConstructorInvocation() ]
    if (n.f5.present()) {
      n.f5.accept(this);
      oneNewLine(n);
      sb.append(spc.spc);
    }
    // f6 -> ( BlockStatement() )*
    if (n.f6.present()) {
      for (final Iterator<INode> e = n.f6.elements(); e.hasNext();) {
        e.next().accept(this);
        if (e.hasNext()) {
          oneNewLine(n);
          sb.append(spc.spc);
        }
      }
    }
    spc.updateSpc(-1);
    oneNewLine(n);
    sb.append(spc.spc);
    // f7 -> "}"
    n.f7.accept(this);
  }
  
  /**
   * Visits a {@link ExplicitConstructorInvocation} node, whose child is the following :
   * <p>
   * f0 -> ( %0 #0 [ $0 "<" $1 ReferenceType()<br>
   * .. .. . .. .. . $2 ( ?0 "," ?1 ReferenceType() )*<br>
   * .. .. . .. .. . $3 ">" ]<br>
   * .. .. . .. #1 ( &0 $0 "this" $1 Arguments() $2 ";"<br>
   * .. .. . .. .. | &1 $0 "super" $1 Arguments() $2 ";" )<br>
   * .. .. | %1 ( #0 PrimaryExpression() #1 "." #2 "super" #3 Arguments() #4 ";" ) )<br>
   * s: -492225557<br>
   *
   * @param n - the node to visit
   */
  @Override
  @NodeFieldsSignature({
      -492225557, JTB_SIG_EXPLICITCONSTRUCTORINVOCATION, JTB_USER_EXPLICITCONSTRUCTORINVOCATION
  })
  public void visit(final ExplicitConstructorInvocation n) {
    // f0 -> ( %0 #0 [ $0 "<" $1 ReferenceType()
    // .. .. . .. .. . $2 ( ?0 "," ?1 ReferenceType() )*
    // .. .. . .. .. . $3 ">" ]
    // .. .. . .. #1 ( &0 $0 "this" $1 Arguments() $2 ";"
    // .. .. . .. .. | &1 $0 "super" $1 Arguments() $2 ";" )
    // .. .. | %1 ( #0 PrimaryExpression() #1 "." #2 "super" #3 Arguments() #4 ";" ) )
    final NodeChoice n0 = n.f0;
    final NodeChoice nch = n0;
    final INode ich = nch.choice;
    switch (nch.which) {
    case 0:
      // %0 #0 [ $0 "<" $1 ReferenceType()
      // .. .. . $2 ( ?0 "," ?1 ReferenceType() )*
      // .. .. . $3 ">" ]
      // .. #1 ( &0 $0 "this" $1 Arguments() $2 ";"
      // .. .. | &1 $0 "super" $1 Arguments() $2 ";" )
      final NodeSequence seq = (NodeSequence) ich;
      // #0 [ $0 "<" $1 ReferenceType()
      // .. . $2 ( ?0 "," ?1 ReferenceType() )*
      // .. . $3 ">" ]
      final NodeOptional opt = (NodeOptional) seq.elementAt(0);
      if (opt.present()) {
        final NodeSequence seq1 = (NodeSequence) opt.node;
        // $0 "<"
        seq1.elementAt(0).accept(this);
        sb.append(' ');
        // $1 ReferenceType()
        seq1.elementAt(1).accept(this);
        // $2 ( ?0 "," ?1 ReferenceType() )*
        final NodeListOptional nlo = (NodeListOptional) seq1.elementAt(2);
        if (nlo.present()) {
          for (int i = 0; i < nlo.size(); i++) {
            final INode nloeai = nlo.elementAt(i);
            final NodeSequence seq2 = (NodeSequence) nloeai;
            // ?0 ","
            seq2.elementAt(0).accept(this);
            sb.append(' ');
            // ?1 ReferenceType()
            seq2.elementAt(1).accept(this);
          }
        }
        // $3 ">"
        sb.append(' ');
        seq1.elementAt(3).accept(this);
        sb.append(' ');
      }
      // #1 ( &0 $0 "this" $1 Arguments() $2 ";"
      // .. | &1 $0 "super" $1 Arguments() $2 ";" )
      final NodeChoice nch1 = (NodeChoice) seq.elementAt(1);
      final INode ich1 = nch1.choice;
      switch (nch1.which) {
      case 0:
        // $0 "this" $1 Arguments() $2 ";"
        final NodeSequence seq1 = (NodeSequence) ich1;
        // $0 "this"
        seq1.elementAt(0).accept(this);
        sb.append(' ');
        // $1 Arguments()
        seq1.elementAt(1).accept(this);
        // $2 ";"
        sb.append(' ');
        seq1.elementAt(2).accept(this);
        sb.append(' ');
        break;
      case 1:
        // $0 "super" $1 Arguments() $2 ";"
        final NodeSequence seq2 = (NodeSequence) ich1;
        seq2.elementAt(0).accept(this);
        sb.append(' ');
        // $1 Arguments()
        seq2.elementAt(1).accept(this);
        // $2 ";"
        sb.append(' ');
        seq2.elementAt(2).accept(this);
        sb.append(' ');
        break;
      default:
        final String msg = "Invalid nch1.which = " + nch1.which;
        Messages.hardErr(msg);
        throw new ProgrammaticError(msg);
      }
      break;
    case 1:
      // #0 PrimaryExpression() #1 "." #2 "super" #3 Arguments() #4 ";"
      final NodeSequence seq1 = (NodeSequence) ich;
      // #0 PrimaryExpression()
      seq1.elementAt(0).accept(this);
      // #1 "."
      seq1.elementAt(1).accept(this);
      // "super"
      seq1.elementAt(2).accept(this);
      // #3 Arguments()
      seq1.elementAt(3).accept(this);
      // #4 ";"
      seq1.elementAt(4).accept(this);
      break;
    default:
      final String msg = "Invalid nch.which = " + nch.which;
      Messages.hardErr(msg);
      throw new ProgrammaticError(msg);
    }
    
  }
  
  /**
   * Visits a {@link Initializer} node, whose children are the following :
   * <p>
   * f0 -> [ "static" ]<br>
   * f1 -> Block()<br>
   * s: -423135641<br>
   *
   * @param n - the node to visit
   */
  @Override
  @NodeFieldsSignature({
      -423135641, JTB_SIG_INITIALIZER, JTB_USER_INITIALIZER
  })
  public void visit(final Initializer n) {
    // f0 -> [ "static" ]
    if (n.f0.present()) {
      n.f0.accept(this);
      sb.append(' ');
    }
    // f1 -> Block()
    n.f1.accept(this);
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
    n.f0.accept(this);
    // f1 -> TypeArgument()
    n.f1.accept(this);
    // f2 -> ( #0 "," #1 TypeArgument() )*
    if (n.f2.present()) {
      sb.append(' ');
      for (final INode e : n.f2.nodes) {
        final NodeSequence seq = (NodeSequence) e;
        // #0 ","
        seq.elementAt(0).accept(this);
        sb.append(' ');
        // #1 TypeArgument()
        seq.elementAt(1).accept(this);
      }
    }
    // f3 -> ">"
    n.f3.accept(this);
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
    if (n.f0.which == 0) {
      // %0 ReferenceType()
      n.f0.choice.accept(this);
    } else {
      // %1 #0 "?" #1 [ WildcardBounds() ]
      final NodeSequence seq = (NodeSequence) n.f0.choice;
      // #0 "?"
      seq.elementAt(0).accept(this);
      // #1 [ WildcardBounds() ]
      final NodeOptional opt = (NodeOptional) seq.elementAt(1);
      if (opt.present()) {
        sb.append(' ');
        opt.node.accept(this);
      }
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
    final NodeSequence seq = (NodeSequence) n.f0.choice;
    // #0 "extends" | #0 "super"
    seq.elementAt(0).accept(this);
    sb.append(' ');
    // #1 ReferenceType()
    seq.elementAt(1).accept(this);
  }
  
  /**
   * Visits a {@link NameList} node, whose children are the following :
   * <p>
   * f0 -> Name()<br>
   * f1 -> ( #0 "," #1 Name() )*<br>
   * s: -1147957113<br>
   *
   * @param n - the node to visit
   */
  @Override
  @NodeFieldsSignature({
      -1147957113, JTB_SIG_NAMELIST, JTB_USER_NAMELIST
  })
  public void visit(final NameList n) {
    // f0 -> Name()
    n.f0.accept(this);
    // f1 -> ( #0 "," #1 Name() )*
    if (n.f1.present()) {
      for (final INode e : n.f1.nodes) {
        // #0 ","
        final NodeSequence seq = (NodeSequence) e;
        seq.elementAt(0).accept(this);
        sb.append(' ');
        // #1 Name()
        seq.elementAt(1).accept(this);
      }
    }
  }
  
  /**
   * Visits a {@link Expression} node, whose children are the following :
   * <p>
   * f0 -> ConditionalExpression()<br>
   * f1 -> [ #0 AssignmentOperator() #1 Expression() ]<br>
   * s: -1186270200<br>
   *
   * @param n - the node to visit
   */
  @Override
  @NodeFieldsSignature({
      -1186270200, JTB_SIG_EXPRESSION, JTB_USER_EXPRESSION
  })
  public void visit(final Expression n) {
    // f0 -> ConditionalExpression()
    n.f0.accept(this);
    // f1 -> [ #0 AssignmentOperator() #1 Expression() ]
    if (n.f1.present()) {
      final NodeSequence seq = (NodeSequence) n.f1.node;
      sb.append(' ');
      // #0 AssignmentOperator()
      seq.elementAt(0).accept(this);
      sb.append(' ');
      // #1 Expression()
      seq.elementAt(1).accept(this);
    }
  }
  
  /**
   * Visits a {@link ConditionalExpression} node, whose children are the following :
   * <p>
   * f0 -> ConditionalOrExpression()<br>
   * f1 -> [ #0 "?" #1 Expression() #2 ":" #3 Expression() ]<br>
   * s: -1150694214<br>
   *
   * @param n - the node to visit
   */
  @Override
  @NodeFieldsSignature({
      -1150694214, JTB_SIG_CONDITIONALEXPRESSION, JTB_USER_CONDITIONALEXPRESSION
  })
  public void visit(final ConditionalExpression n) {
    // f0 -> ConditionalOrExpression()
    n.f0.accept(this);
    // f1 -> [ #0 "?" #1 Expression() #2 ":" #3 Expression() ]
    if (n.f1.present()) {
      final NodeSequence seq = (NodeSequence) n.f1.node;
      sb.append(' ');
      // #0 "?"
      seq.elementAt(0).accept(this);
      sb.append(' ');
      // #1 Expression()
      seq.elementAt(1).accept(this);
      sb.append(' ');
      // #2 ":"
      seq.elementAt(2).accept(this);
      sb.append(' ');
      // #3 Expression()
      seq.elementAt(3).accept(this);
    }
  }
  
  /**
   * Visits a {@link ConditionalOrExpression} node, whose children are the following :
   * <p>
   * f0 -> ConditionalAndExpression()<br>
   * f1 -> ( #0 "||" #1 ConditionalAndExpression() )*<br>
   * s: -1592298777<br>
   *
   * @param n - the node to visit
   */
  @Override
  @NodeFieldsSignature({
      -1592298777, JTB_SIG_CONDITIONALOREXPRESSION, JTB_USER_CONDITIONALOREXPRESSION
  })
  public void visit(final ConditionalOrExpression n) {
    // f0 -> ConditionalAndExpression()
    n.f0.accept(this);
    // f1 -> ( #0 "||" #1 ConditionalAndExpression() )*
    if (n.f1.present()) {
      for (final INode e : n.f1.nodes) {
        final NodeSequence seq = (NodeSequence) e;
        sb.append(' ');
        // #0 "||"
        seq.elementAt(0).accept(this);
        sb.append(' ');
        // #1 ConditionalAndExpression()
        seq.elementAt(1).accept(this);
      }
    }
  }
  
  /**
   * Visits a {@link ConditionalAndExpression} node, whose children are the following :
   * <p>
   * f0 -> InclusiveOrExpression()<br>
   * f1 -> ( #0 "&&" #1 InclusiveOrExpression() )*<br>
   * s: -1425815203<br>
   *
   * @param n - the node to visit
   */
  @Override
  @NodeFieldsSignature({
      -1425815203, JTB_SIG_CONDITIONALANDEXPRESSION, JTB_USER_CONDITIONALANDEXPRESSION
  })
  public void visit(final ConditionalAndExpression n) {
    // f0 -> InclusiveOrExpression()
    n.f0.accept(this);
    // f1 -> ( #0 "&&" #1 InclusiveOrExpression() )*
    if (n.f1.present()) {
      for (final INode e : n.f1.nodes) {
        final NodeSequence seq = (NodeSequence) e;
        sb.append(' ');
        // #0 "&&"
        seq.elementAt(0).accept(this);
        sb.append(' ');
        // #1 InclusiveOrExpression()
        seq.elementAt(1).accept(this);
      }
    }
  }
  
  /**
   * Visits a {@link InclusiveOrExpression} node, whose children are the following :
   * <p>
   * f0 -> ExclusiveOrExpression()<br>
   * f1 -> ( #0 "|" #1 ExclusiveOrExpression() )*<br>
   * s: 963402497<br>
   *
   * @param n - the node to visit
   */
  @Override
  @NodeFieldsSignature({
      963402497, JTB_SIG_INCLUSIVEOREXPRESSION, JTB_USER_INCLUSIVEOREXPRESSION
  })
  public void visit(final InclusiveOrExpression n) {
    // f0 -> ExclusiveOrExpression()
    n.f0.accept(this);
    // f1 -> ( #0 "|" #1 ExclusiveOrExpression() )*
    if (n.f1.present()) {
      for (final INode e : n.f1.nodes) {
        final NodeSequence seq = (NodeSequence) e;
        sb.append(' ');
        // #0 "|"
        seq.elementAt(0).accept(this);
        sb.append(' ');
        // #1 ExclusiveOrExpression()
        seq.elementAt(1).accept(this);
      }
    }
  }
  
  /**
   * Visits a {@link ExclusiveOrExpression} node, whose children are the following :
   * <p>
   * f0 -> AndExpression()<br>
   * f1 -> ( #0 "^" #1 AndExpression() )*<br>
   * s: -1241708769<br>
   *
   * @param n - the node to visit
   */
  @Override
  @NodeFieldsSignature({
      -1241708769, JTB_SIG_EXCLUSIVEOREXPRESSION, JTB_USER_EXCLUSIVEOREXPRESSION
  })
  public void visit(final ExclusiveOrExpression n) {
    // f0 -> AndExpression()
    n.f0.accept(this);
    // f1 -> ( #0 "^" #1 AndExpression() )*
    if (n.f1.present()) {
      for (final INode e : n.f1.nodes) {
        final NodeSequence seq = (NodeSequence) e;
        sb.append(' ');
        // #0 "^"
        seq.elementAt(0).accept(this);
        sb.append(' ');
        // #1 AndExpression()
        seq.elementAt(1).accept(this);
      }
    }
  }
  
  /**
   * Visits a {@link AndExpression} node, whose children are the following :
   * <p>
   * f0 -> EqualityExpression()<br>
   * f1 -> ( #0 "&" #1 EqualityExpression() )*<br>
   * s: -629554573<br>
   *
   * @param n - the node to visit
   */
  @Override
  @NodeFieldsSignature({
      -629554573, JTB_SIG_ANDEXPRESSION, JTB_USER_ANDEXPRESSION
  })
  public void visit(final AndExpression n) {
    // f0 -> EqualityExpression()
    n.f0.accept(this);
    // f1 -> ( #0 "&" #1 EqualityExpression() )*
    if (n.f1.present()) {
      for (final INode e : n.f1.nodes) {
        final NodeSequence seq = (NodeSequence) e;
        sb.append(' ');
        // #0 "&"
        seq.elementAt(0).accept(this);
        sb.append(' ');
        // #0 EqualityExpression()
        seq.elementAt(1).accept(this);
      }
    }
  }
  
  /**
   * Visits a {@link EqualityExpression} node, whose children are the following :
   * <p>
   * f0 -> InstanceOfExpression()<br>
   * f1 -> ( #0 ( %0 "=="<br>
   * .. .. . .. | %1 "!=" )<br>
   * .. .. . #1 InstanceOfExpression() )*<br>
   * s: 1053085061<br>
   *
   * @param n - the node to visit
   */
  @Override
  @NodeFieldsSignature({
      1053085061, JTB_SIG_EQUALITYEXPRESSION, JTB_USER_EQUALITYEXPRESSION
  })
  public void visit(final EqualityExpression n) {
    // f0 -> InstanceOfExpression()
    n.f0.accept(this);
    // f1 -> ( #0 ( %0 "==" | %1 "!=" ) #1 InstanceOfExpression() )*
    if (n.f1.present()) {
      for (final INode e : n.f1.nodes) {
        final NodeSequence seq = (NodeSequence) e;
        sb.append(' ');
        // %0 "==" | %1 "!="
        seq.elementAt(0).accept(this);
        sb.append(' ');
        // #1 InstanceOfExpression()
        seq.elementAt(1).accept(this);
      }
    }
  }
  
  /**
   * Visits a {@link InstanceOfExpression} node, whose children are the following :
   * <p>
   * f0 -> RelationalExpression()<br>
   * f1 -> [ #0 "instanceof" #1 Type() ]<br>
   * s: 933354553<br>
   *
   * @param n - the node to visit
   */
  @Override
  @NodeFieldsSignature({
      933354553, JTB_SIG_INSTANCEOFEXPRESSION, JTB_USER_INSTANCEOFEXPRESSION
  })
  public void visit(final InstanceOfExpression n) {
    // f0 -> RelationalExpression()
    n.f0.accept(this);
    // f1 -> [ #0 "instanceof" #1 Type() ]
    if (n.f1.present()) {
      sb.append(' ');
      // #0 "instanceof"
      ((NodeSequence) n.f1.node).elementAt(0).accept(this);
      sb.append(' ');
      // #1 Type()
      ((NodeSequence) n.f1.node).elementAt(1).accept(this);
    }
  }
  
  /**
   * Visits a {@link RelationalExpression} node, whose children are the following :
   * <p>
   * f0 -> ShiftExpression()<br>
   * f1 -> ( #0 ( %0 "<"<br>
   * .. .. . .. | %1 ">"<br>
   * .. .. . .. | %2 "<="<br>
   * .. .. . .. | %3 ">=" )<br>
   * .. .. . #1 ShiftExpression() )*<br>
   * s: 1473482530<br>
   *
   * @param n - the node to visit
   */
  @Override
  @NodeFieldsSignature({
      1473482530, JTB_SIG_RELATIONALEXPRESSION, JTB_USER_RELATIONALEXPRESSION
  })
  public void visit(final RelationalExpression n) {
    // f0 -> ShiftExpression()
    n.f0.accept(this);
    // f1 -> ( 0 ( %0 "<" | %1 ">" | %2 "<=" | %3 ">=" ) #1 ShiftExpression() )*
    if (n.f1.present()) {
      for (final INode e : n.f1.nodes) {
        final NodeSequence seq = (NodeSequence) e;
        sb.append(' ');
        // %0 "<" | %1 ">" | %2 "<=" | %3 ">="
        seq.elementAt(0).accept(this);
        sb.append(' ');
        // #1 ShiftExpression()
        seq.elementAt(1).accept(this);
      }
    }
  }
  
  /**
   * Visits a {@link ShiftExpression} node, whose children are the following :
   * <p>
   * f0 -> AdditiveExpression()<br>
   * f1 -> ( #0 ( %0 "<<"<br>
   * .. .. . .. | %1 RUnsignedShift()<br>
   * .. .. . .. | %2 RSignedShift() )<br>
   * .. .. . #1 AdditiveExpression() )*<br>
   * s: 1210478291<br>
   *
   * @param n - the node to visit
   */
  @Override
  @NodeFieldsSignature({
      1210478291, JTB_SIG_SHIFTEXPRESSION, JTB_USER_SHIFTEXPRESSION
  })
  public void visit(final ShiftExpression n) {
    // f0 -> AdditiveExpression()
    n.f0.accept(this);
    // f1 -> ( #0 ( %0 "<<" | %1 RUnsignedShift() | %2 RSignedShift() ) #1 AdditiveExpression() )*
    if (n.f1.present()) {
      for (final INode e : n.f1.nodes) {
        final NodeSequence seq = (NodeSequence) e;
        sb.append(' ');
        // #0 ( %0 "<<" | %1 RUnsignedShift() | %2 RSignedShift() )
        seq.elementAt(0).accept(this);
        sb.append(' ');
        // #1 AdditiveExpression()
        seq.elementAt(1).accept(this);
      }
    }
  }
  
  /**
   * Visits a {@link AdditiveExpression} node, whose children are the following :
   * <p>
   * f0 -> MultiplicativeExpression()<br>
   * f1 -> ( #0 ( %0 "+"<br>
   * .. .. . .. | %1 "-" )<br>
   * .. .. . #1 MultiplicativeExpression() )*<br>
   * s: -1807059397<br>
   *
   * @param n - the node to visit
   */
  @Override
  @NodeFieldsSignature({
      -1807059397, JTB_SIG_ADDITIVEEXPRESSION, JTB_USER_ADDITIVEEXPRESSION
  })
  public void visit(final AdditiveExpression n) {
    // f0 -> MultiplicativeExpression()
    n.f0.accept(this);
    // f1 -> ( #0 ( %0 "+" | %1 "-" ) #1 MultiplicativeExpression() )*
    if (n.f1.present()) {
      for (final INode e : n.f1.nodes) {
        final NodeSequence seq = (NodeSequence) e;
        sb.append(' ');
        // #0 ( %0 "+" | %1 "-" )
        seq.elementAt(0).accept(this);
        sb.append(' ');
        // #1 MultiplicativeExpression()
        seq.elementAt(1).accept(this);
      }
    }
  }
  
  /**
   * Visits a {@link MultiplicativeExpression} node, whose children are the following :
   * <p>
   * f0 -> UnaryExpression()<br>
   * f1 -> ( #0 ( %0 "*"<br>
   * .. .. . .. | %1 "/"<br>
   * .. .. . .. | %2 "%" )<br>
   * .. .. . #1 UnaryExpression() )*<br>
   * s: 853643830<br>
   *
   * @param n - the node to visit
   */
  @Override
  @NodeFieldsSignature({
      853643830, JTB_SIG_MULTIPLICATIVEEXPRESSION, JTB_USER_MULTIPLICATIVEEXPRESSION
  })
  public void visit(final MultiplicativeExpression n) {
    // f0 -> UnaryExpression()
    n.f0.accept(this);
    // f1 -> ( #0 ( %0 "*" | %1 "/" | %2 "%" ) #1 UnaryExpression() )*
    if (n.f1.present()) {
      for (final INode e : n.f1.nodes) {
        final NodeSequence seq = (NodeSequence) e;
        sb.append(' ');
        // %0 "*" | %1 "/" | %2 "%"
        seq.elementAt(0).accept(this);
        sb.append(' ');
        // #1 UnaryExpression()
        seq.elementAt(1).accept(this);
      }
    }
  }
  
  /**
   * Visits a {@link CastLookahead} node, whose child is the following :
   * <p>
   * f0 -> . %0 #0 "(" #1 PrimitiveType()<br>
   * .. .. | %1 #0 "(" #1 Type() #2 "[" #3 "]"<br>
   * .. .. | %2 #0 "(" #1 Type() #2 ")"<br>
   * .. .. . .. #3 ( &0 "~"<br>
   * .. .. . .. .. | &1 "!"<br>
   * .. .. . .. .. | &2 "("<br>
   * .. .. . .. .. | &3 < IDENTIFIER ><br>
   * .. .. . .. .. | &4 "this"<br>
   * .. .. . .. .. | &5 "super"<br>
   * .. .. . .. .. | &6 "new"<br>
   * .. .. . .. .. | &7 Literal() )<br>
   * s: 611584359<br>
   *
   * @param n - the node to visit
   */
  @Override
  @NodeFieldsSignature({
      611584359, JTB_SIG_CASTLOOKAHEAD, JTB_USER_CASTLOOKAHEAD
  })
  public void visit(@SuppressWarnings("unused") final CastLookahead n) {
    sb.append("/* !!! CastLookahead visited in JavaPrinter but should not,"
        + " as it should be called only during parsing !!! */");
  }
  
  /**
   * Visits a {@link Literal} node, whose child is the following :
   * <p>
   * f0 -> . %0 < INTEGER_LITERAL ><br>
   * .. .. | %1 < FLOATING_POINT_LITERAL ><br>
   * .. .. | %2 < CHARACTER_LITERAL ><br>
   * .. .. | %3 < STRING_LITERAL ><br>
   * .. .. | %4 BooleanLiteral()<br>
   * .. .. | %5 NullLiteral()<br>
   * s: 454259936<br>
   *
   * @param n - the node to visit
   */
  @Override
  @NodeFieldsSignature({
      454259936, JTB_SIG_LITERAL, JTB_USER_LITERAL
  })
  public void visit(final Literal n) {
    if (n.f0.which <= 1) {
      // %0 < INTEGER_LITERAL > | %1 < FLOATING_POINT_LITERAL >
      sb.append(jopt.printSpecialTokensJJ ? ((Token) n.f0.choice).withSpecials(spc.spc)
          : ((Token) n.f0.choice).image);
    } else if (n.f0.which <= 3) {
      // %2 < CHARACTER_LITERAL > | %3 < STRING_LITERAL >
      sb.append(UnicodeConverter
          .addUnicodeEscapes(jopt.printSpecialTokensJJ ? ((Token) n.f0.choice).withSpecials(spc.spc)
              : ((Token) n.f0.choice).image));
    } else {
      // %4 BooleanLiteral() | %5 NullLiteral()
      n.f0.choice.accept(this);
    }
  }
  
  /**
   * Visits a {@link IntegerLiteral} node, whose child is the following :
   * <p>
   * f0 -> < INTEGER_LITERAL ><br>
   * s: -1048223857<br>
   *
   * @param n - the node to visit
   */
  @Override
  public void visit(final IntegerLiteral n) {
    final String str = jopt.printSpecialTokensJJ ? n.f0.withSpecials(spc.spc) : n.f0.image;
    sb.append(str);
  }
  
  /**
   * Visits a {@link BooleanLiteral} node, whose child is the following :
   * <p>
   * f0 -> . %0 "true"<br>
   * .. .. | %1 "false"<br>
   * s: -1365265107<br>
   *
   * @param n - the node to visit
   */
  @Override
  @NodeFieldsSignature({
      -1365265107, JTB_SIG_BOOLEANLITERAL, JTB_USER_BOOLEANLITERAL
  })
  public void visit(final BooleanLiteral n) {
    final String str = jopt.printSpecialTokensJJ ? ((Token) n.f0.choice).withSpecials(spc.spc)
        : ((Token) n.f0.choice).image;
    sb.append(str);
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
    final String str = jopt.printSpecialTokensJJ ? n.f0.withSpecials(spc.spc) : n.f0.image;
    sb.append(UnicodeConverter.addUnicodeEscapes(str));
  }
  
  /**
   * Visits a {@link NullLiteral} node, whose child is the following :
   * <p>
   * f0 -> "null"<br>
   * s: -1703344686<br>
   *
   * @param n - the node to visit
   */
  @Override
  @NodeFieldsSignature({
      -1703344686, JTB_SIG_NULLLITERAL, JTB_USER_NULLLITERAL
  })
  public void visit(final NullLiteral n) {
    final String str = jopt.printSpecialTokensJJ ? n.f0.withSpecials(spc.spc) : n.f0.image;
    sb.append(str);
  }
  
  /**
   * Visits a {@link ArgumentList} node, whose children are the following :
   * <p>
   * f0 -> Expression()<br>
   * f1 -> ( #0 "," #1 Expression() )*<br>
   * s: -662366547<br>
   *
   * @param n - the node to visit
   */
  @Override
  @NodeFieldsSignature({
      -662366547, JTB_SIG_ARGUMENTLIST, JTB_USER_ARGUMENTLIST
  })
  public void visit(final ArgumentList n) {
    // f0 -> Expression()
    n.f0.accept(this);
    // f1 -> ( #0 "," #1 Expression() )*
    if (n.f1.present()) {
      for (final INode e : n.f1.nodes) {
        final NodeSequence seq = (NodeSequence) e;
        // #0 ","
        seq.elementAt(0).accept(this);
        sb.append(' ');
        // #1 Expression()
        seq.elementAt(1).accept(this);
      }
    }
  }
  
  /**
   * Visits a {@link AllocationExpression} node, whose child is the following :
   * <p>
   * f0 -> . %0 #0 "new" #1 PrimitiveType() #2 ArrayDimsAndInits()<br>
   * .. .. | %1 #0 "new" #1 ClassOrInterfaceType()<br>
   * .. .. . .. #2 [ &0 EmptyTypeArguments()<br>
   * .. .. . .. .. | &1 TypeArguments() ]<br>
   * .. .. . .. #3 ( &0 ArrayDimsAndInits()<br>
   * .. .. . .. .. | &1 $0 Arguments()<br>
   * .. .. . .. .. . .. $1 [ ClassOrInterfaceBody() ] )<br>
   * s: 1688598744<br>
   *
   * @param n - the node to visit
   */
  @Override
  @NodeFieldsSignature({
      1688598744, JTB_SIG_ALLOCATIONEXPRESSION, JTB_USER_ALLOCATIONEXPRESSION
  })
  public void visit(final AllocationExpression n) {
    final NodeSequence seq = (NodeSequence) n.f0.choice;
    if (n.f0.which == 0) {
      // %0 #0 "new" #1 PrimitiveType() #2 ArrayDimsAndInits()
      seq.elementAt(0).accept(this);
      sb.append(' ');
      seq.elementAt(1).accept(this);
      seq.elementAt(2).accept(this);
    } else {
      // %1 #0 "new" #1 ClassOrInterfaceType()
      // .. #2 [ &0 EmptyTypeArguments()
      // .. .. | &1 TypeArguments() ]
      // .. #3 ( &0 ArrayDimsAndInits()
      // .. .. | &1 $0 Arguments()
      // .. .. .. $1 [ ClassOrInterfaceBody() ] )
      // #0 "new"
      seq.elementAt(0).accept(this);
      sb.append(' ');
      // #1 ClassOrInterfaceType()
      seq.elementAt(1).accept(this);
      // #2 [ &0 EmptyTypeArguments()
      // .. | &1 TypeArguments() ]
      if (((NodeOptional) seq.elementAt(2)).present()) {
        seq.elementAt(2).accept(this);
      }
      // #3 ( &0 ArrayDimsAndInits() | &1 $0 Arguments() $1 [ ClassOrInterfaceBody() ] )
      final NodeChoice ch = (NodeChoice) seq.elementAt(3);
      if (ch.which == 0) {
        // &0 ArrayDimsAndInits()
        ch.choice.accept(this);
      } else {
        // &1 $0 Arguments() $1 [ ClassOrInterfaceBody() ]
        final NodeSequence seq1 = (NodeSequence) ch.choice;
        // $0 Arguments()
        seq1.elementAt(0).accept(this);
        // $1 [ ClassOrInterfaceBody() ]
        if (((NodeOptional) seq1.elementAt(1)).present()) {
          seq1.elementAt(1).accept(this);
        }
      }
    }
  }
  
  /**
   * Visits a {@link AssertStatement} node, whose children are the following :
   * <p>
   * f0 -> "assert"<br>
   * f1 -> Expression()<br>
   * f2 -> [ #0 ":" #1 Expression() ]<br>
   * f3 -> ";"<br>
   * s: -579866328<br>
   *
   * @param n - the node to visit
   */
  @Override
  @NodeFieldsSignature({
      -579866328, JTB_SIG_ASSERTSTATEMENT, JTB_USER_ASSERTSTATEMENT
  })
  public void visit(final AssertStatement n) {
    // f0 -> "assert"
    n.f0.accept(this);
    sb.append(' ');
    // f1 -> Expression()
    n.f1.accept(this);
    sb.append(' ');
    // f2 -> [ #0 ":" #1 Expression() ]
    if (n.f2.present()) {
      final NodeSequence seq = (NodeSequence) n.f2.node;
      seq.elementAt(0).accept(this);
      sb.append(' ');
      seq.elementAt(1).accept(this);
    }
    // f3 -> ";"
    n.f3.accept(this);
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
    n.f1.accept(this);
    sb.append(' ');
    // f2 -> VariableDeclarator()
    n.f2.accept(this);
    // f3 -> ( #0 "," #1 VariableDeclarator() )*
    if (n.f3.present()) {
      for (final INode e : n.f3.nodes) {
        final NodeSequence seq = (NodeSequence) e;
        // #0 ","
        seq.elementAt(0).accept(this);
        sb.append(' ');
        // #1 VariableDeclarator()
        seq.elementAt(1).accept(this);
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
    if (n.f0.present()) {
      for (final INode e : n.f0.nodes) {
        e.accept(this);
        // VariableModifiers print the last space if not empty
        sb.append(' ');
      }
    }
  }
  
  /**
   * Visits a {@link StatementExpression} node, whose child is the following :
   * <p>
   * f0 -> . %0 PreIncrementExpression()<br>
   * .. .. | %1 PreDecrementExpression()<br>
   * .. .. | %2 #0 PrimaryExpression()<br>
   * .. .. . .. #1 [ &0 "++"<br>
   * .. .. . .. .. | &1 "--"<br>
   * .. .. . .. .. | &2 $0 AssignmentOperator() $1 Expression() ]<br>
   * s: 757890000<br>
   *
   * @param n - the node to visit
   */
  @Override
  @NodeFieldsSignature({
      757890000, JTB_SIG_STATEMENTEXPRESSION, JTB_USER_STATEMENTEXPRESSION
  })
  public void visit(final StatementExpression n) {
    if (n.f0.which < 2) {
      // %0 PreIncrementExpression() | %1 PreDecrementExpression()
      n.f0.accept(this);
    } else {
      // %2 #0 PrimaryExpression() #1 [ &0 "++" | &1 "--" | &2 $0 AssignmentOperator() $1 Expression() ]
      // #0 PrimaryExpression()
      ((NodeSequence) n.f0.choice).elementAt(0).accept(this);
      // #1 [ &0 "++" | &1 "--" | &2 $0 AssignmentOperator() $1 Expression() ]
      final NodeOptional opt = (NodeOptional) ((NodeSequence) n.f0.choice).elementAt(1);
      if (opt.present()) {
        // &0 "++" | &1 "--" | &2 $0 AssignmentOperator() $1 Expression()
        final NodeChoice ch = (NodeChoice) opt.node;
        if (ch.which <= 1) {
          // &0 "++" | &1 "--"
          ch.choice.accept(this);
        } else {
          // &2 $0 AssignmentOperator() $1 Expression()
          final NodeSequence seq1 = (NodeSequence) ch.choice;
          sb.append(' ');
          // $0 AssignmentOperator()
          seq1.elementAt(0).accept(this);
          sb.append(' ');
          // $1 Expression()
          seq1.elementAt(1).accept(this);
        }
      }
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
    n.f2.accept(this);
    // f3 -> ")"
    n.f3.accept(this);
    sb.append(' ');
    // f4 -> "{"
    n.f4.accept(this);
    spc.updateSpc(+1);
    oneNewLine(n);
    sb.append(spc.spc);
    // ( #0 SwitchLabel() #1 ( BlockStatement() )* )*
    for (final INode e : n.f5.nodes) {
      final NodeSequence seq = (NodeSequence) e;
      // #0 SwitchLabel()
      seq.elementAt(0).accept(this);
      spc.updateSpc(+1);
      // #1 ( BlockStatement() )*
      final NodeListOptional nlo = (NodeListOptional) seq.elementAt(1);
      if (nlo.present()) {
        if (nlo.size() == 1) {
          sb.append(' ');
        } else {
          oneNewLine(n);
          sb.append(spc.spc);
        }
        for (final Iterator<INode> e1 = nlo.elements(); e1.hasNext();) {
          // BlockStatement()
          e1.next().accept(this);
          if (e1.hasNext()) {
            oneNewLine(n);
            sb.append(spc.spc);
          }
        }
      }
      oneNewLine(n);
      spc.updateSpc(-1);
    }
    spc.updateSpc(-1);
    sb.append(spc.spc);
    // f6 -> "}"
    n.f6.accept(this);
    oneNewLine(n);
  }
  
  /**
   * Visits a {@link SwitchLabel} node, whose child is the following :
   * <p>
   * f0 -> . %0 #0 "case" #1 Expression() #2 ":"<br>
   * .. .. | %1 #0 "default" #1 ":"<br>
   * s: 63513165<br>
   *
   * @param n - the node to visit
   */
  @Override
  @NodeFieldsSignature({
      63513165, JTB_SIG_SWITCHLABEL, JTB_USER_SWITCHLABEL
  })
  public void visit(final SwitchLabel n) {
    final NodeSequence seq = (NodeSequence) n.f0.choice;
    if (n.f0.which == 0) {
      // %0 #0 "case" #1 Expression() #2 ":"
      seq.elementAt(0).accept(this);
      sb.append(' ');
      seq.elementAt(1).accept(this);
      sb.append(' ');
      seq.elementAt(2).accept(this);
    } else {
      // %1 #0 "default" #1 ":"
      seq.elementAt(0).accept(this);
      sb.append(' ');
      seq.elementAt(1).accept(this);
    }
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
    n.f2.accept(this);
    // f3 -> ")"
    n.f3.accept(this);
    // f4 -> Statement()
    if (n.f4.f0.which != 2) { // if Statement() not a Block
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
      if (n.f4.f0.which != 2) { // if Statement() not a Block
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
        // else Statement() is not a Block()
        oneNewLine(n, "c");
        spc.updateSpc(+1);
        sb.append(spc.spc);
        // Statement()
        st.accept(this);
        spc.updateSpc(-1);
      } else {
        // else Statement() is a Block()
        sb.append(' ');
        // Statement()
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
    n.f2.accept(this);
    // f3 -> ")"
    n.f3.accept(this);
    // f4 -> Statement()
    n.f4.accept(this);
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
    n.f1.accept(this);
    // f2 -> "while"
    n.f2.accept(this);
    sb.append(' ');
    // f3 -> "("
    n.f3.accept(this);
    // f4 -> Expression()
    n.f4.accept(this);
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
    // f2 -> ( %0 #0 VariableModifiers() #1 Type() #2 < IDENTIFIER > #3 ":" #4 Expression()
    // | %1 #0 [ ForInit() ] #1 ";" #2 [ Expression() ] #3 ";" #4 [ ForUpdate() ] )
    final NodeSequence seq = (NodeSequence) n.f2.choice;
    if (n.f2.which == 0) {
      // %0 #0 VariableModifiers() #1 Type() #2 < IDENTIFIER > #3 ":" #4 Expression(
      seq.elementAt(0).accept(this);
      // #0 VariableModifiers print the last space if not empty
      // #1 Type()
      seq.elementAt(1).accept(this);
      sb.append(' ');
      // #2 < IDENTIFIER >
      seq.elementAt(2).accept(this);
      sb.append(' ');
      // #3 ":"
      seq.elementAt(3).accept(this);
      sb.append(' ');
      // #4 Expression()
      seq.elementAt(4).accept(this);
    } else {
      // %1 #0 [ ForInit() ] #1 ";" #2 [ Expression() ] #3 ";" #4 [ ForUpdate() ]
      NodeOptional opt;
      // #0 [ ForInit() ]
      opt = (NodeOptional) seq.elementAt(0);
      if (opt.present()) {
        opt.node.accept(this);
      }
      // #1 ";"
      seq.elementAt(1).accept(this);
      sb.append(' ');
      // #2 [ Expression() ]
      opt = (NodeOptional) seq.elementAt(2);
      if (opt.present()) {
        opt.node.accept(this);
      }
      // #3 ";"
      seq.elementAt(3).accept(this);
      sb.append(' ');
      // #4 [ ForUpdate() ]
      opt = (NodeOptional) seq.elementAt(4);
      if (opt.present()) {
        opt.node.accept(this);
      }
    }
    // f3 -> ")"
    n.f3.accept(this);
    // f4 -> Statement()
    n.f4.accept(this);
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
    n.f0.choice.accept(this);
  }
  
  /**
   * Visits a {@link StatementExpressionList} node, whose children are the following :
   * <p>
   * f0 -> StatementExpression()<br>
   * f1 -> ( #0 "," #1 StatementExpression() )*<br>
   * s: 186773841<br>
   *
   * @param n - the node to visit
   */
  @Override
  @NodeFieldsSignature({
      186773841, JTB_SIG_STATEMENTEXPRESSIONLIST, JTB_USER_STATEMENTEXPRESSIONLIST
  })
  public void visit(final StatementExpressionList n) {
    // f0 -> StatementExpression()
    n.f0.accept(this);
    // f1 -> ( #0 "," #1 StatementExpression() )*
    if (n.f1.present()) {
      for (final INode e : n.f1.nodes) {
        // #0 ","
        ((NodeSequence) e).elementAt(0).accept(this);
        sb.append(' ');
        // #1 StatementExpression()
        ((NodeSequence) e).elementAt(1).accept(this);
      }
    }
  }
  
  /**
   * Visits a {@link BreakStatement} node, whose children are the following :
   * <p>
   * f0 -> "break"<br>
   * f1 -> [ < IDENTIFIER > ]<br>
   * f2 -> ";"<br>
   * s: 2096828507<br>
   *
   * @param n - the node to visit
   */
  @Override
  @NodeFieldsSignature({
      2096828507, JTB_SIG_BREAKSTATEMENT, JTB_USER_BREAKSTATEMENT
  })
  public void visit(final BreakStatement n) {
    // f0 -> "break"
    n.f0.accept(this);
    if (n.f1.present()) {
      sb.append(' ');
      // f1 -> [ < IDENTIFIER > ]
      n.f1.accept(this);
    }
    // f2 -> ";"
    n.f2.accept(this);
  }
  
  /**
   * Visits a {@link ContinueStatement} node, whose children are the following :
   * <p>
   * f0 -> "continue"<br>
   * f1 -> [ < IDENTIFIER > ]<br>
   * f2 -> ";"<br>
   * s: -1991535243<br>
   *
   * @param n - the node to visit
   */
  @Override
  @NodeFieldsSignature({
      -1991535243, JTB_SIG_CONTINUESTATEMENT, JTB_USER_CONTINUESTATEMENT
  })
  public void visit(final ContinueStatement n) {
    // f0 -> "continue"
    n.f0.accept(this);
    if (n.f1.present()) {
      sb.append(' ');
      // f1 -> [ < IDENTIFIER > ]
      n.f1.accept(this);
    }
    // f2 -> ";"
    n.f2.accept(this);
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
    // f0 -> "return"
    n.f0.accept(this);
    if (n.f1.present()) {
      sb.append(' ');
      // f1 -> [ Expression() ]
      n.f1.accept(this);
    }
    // f2 -> ";"
    n.f2.accept(this);
  }
  
  /**
   * Visits a {@link ThrowStatement} node, whose children are the following :
   * <p>
   * f0 -> "throw"<br>
   * f1 -> Expression()<br>
   * f2 -> ";"<br>
   * s: 568421270<br>
   *
   * @param n - the node to visit
   */
  @Override
  @NodeFieldsSignature({
      568421270, JTB_SIG_THROWSTATEMENT, JTB_USER_THROWSTATEMENT
  })
  public void visit(final ThrowStatement n) {
    // f0 -> "throw"
    n.f0.accept(this);
    sb.append(' ');
    // f1 -> Expression()
    n.f1.accept(this);
    // f2 -> ";"
    n.f2.accept(this);
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
    n.f2.accept(this);
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
      seq.elementAt(2).accept(this);
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
      // #1 Block()
      seq.elementAt(1).accept(this);
    }
  }
  
  /**
   * Visits a {@link MemberValuePairs} node, whose children are the following :
   * <p>
   * f0 -> MemberValuePair()<br>
   * f1 -> ( #0 "," #1 MemberValuePair() )*<br>
   * s: -113472239<br>
   *
   * @param n - the node to visit
   */
  @Override
  @NodeFieldsSignature({
      -113472239, JTB_SIG_MEMBERVALUEPAIRS, JTB_USER_MEMBERVALUEPAIRS
  })
  public void visit(final MemberValuePairs n) {
    // f0 -> MemberValuePair()
    n.f0.accept(this);
    // f1 -> ( #0 "," #1 MemberValuePair() )*
    if (n.f1.present()) {
      for (final INode e : n.f1.nodes) {
        final NodeSequence seq = (NodeSequence) e;
        // #0 ","
        seq.elementAt(0).accept(this);
        sb.append(' ');
        // #1 MemberValuePair()
        seq.elementAt(1).accept(this);
      }
    }
  }
  
  /**
   * Visits a {@link MemberValuePair} node, whose children are the following :
   * <p>
   * f0 -> < IDENTIFIER ><br>
   * f1 -> "="<br>
   * f2 -> MemberValue()<br>
   * s: -476335468<br>
   *
   * @param n - the node to visit
   */
  @Override
  @NodeFieldsSignature({
      -476335468, JTB_SIG_MEMBERVALUEPAIR, JTB_USER_MEMBERVALUEPAIR
  })
  public void visit(final MemberValuePair n) {
    // f0 -> < IDENTIFIER >
    n.f0.accept(this);
    // f1 -> "="
    sb.append(' ');
    n.f1.accept(this);
    sb.append(' ');
    // f2 -> MemberValue()
    n.f2.accept(this);
  }
  
  /**
   * Visits a {@link MemberValue} node, whose child is the following :
   * <p>
   * f0 -> . %0 Annotation()<br>
   * .. .. | %1 MemberValueArrayInitializer()<br>
   * .. .. | %2 ConditionalExpression()<br>
   * s: -1120846693<br>
   *
   * @param n - the node to visit
   */
  @Override
  @NodeFieldsSignature({
      -1120846693, JTB_SIG_MEMBERVALUE, JTB_USER_MEMBERVALUE
  })
  public void visit(final MemberValue n) {
    n.f0.choice.accept(this);
  }
  
  /**
   * Visits a {@link MemberValueArrayInitializer} node, whose children are the following :
   * <p>
   * f0 -> "{"<br>
   * f1 -> MemberValue()<br>
   * f2 -> ( #0 "," #1 MemberValue() )*<br>
   * f3 -> [ "," ]<br>
   * f4 -> "}"<br>
   * s: 111140055<br>
   *
   * @param n - the node to visit
   */
  @Override
  @NodeFieldsSignature({
      111140055, JTB_SIG_MEMBERVALUEARRAYINITIALIZER, JTB_USER_MEMBERVALUEARRAYINITIALIZER
  })
  public void visit(final MemberValueArrayInitializer n) {
    // f0 -> "{"
    n.f0.accept(this);
    sb.append(' ');
    // f1 -> MemberValue()
    n.f1.accept(this);
    // f2 -> ( #0 "," #1 MemberValue() )*
    if (n.f2.present()) {
      for (final INode e : n.f2.nodes) {
        final NodeSequence seq = (NodeSequence) e;
        // #0 ","
        seq.elementAt(0).accept(this);
        sb.append(' ');
        // #1 MemberValuePair()
        seq.elementAt(1).accept(this);
      }
    }
    // f3 -> [ "," ]
    if (n.f3.present()) {
      n.f3.node.accept(this);
    }
    // f4 -> "}"
    sb.append(' ');
    n.f4.accept(this);
  }
  
  /**
   * Visits a {@link AnnotationTypeDeclaration} node, whose children are the following :
   * <p>
   * f0 -> "@"<br>
   * f1 -> "interface"<br>
   * f2 -> < IDENTIFIER ><br>
   * f3 -> AnnotationTypeBody()<br>
   * s: 383718196<br>
   *
   * @param n - the node to visit
   */
  @Override
  @NodeFieldsSignature({
      383718196, JTB_SIG_ANNOTATIONTYPEDECLARATION, JTB_USER_ANNOTATIONTYPEDECLARATION
  })
  public void visit(final AnnotationTypeDeclaration n) {
    // f0 -> "@"
    n.f0.accept(this);
    // f1 -> "interface"
    n.f1.accept(this);
    sb.append(' ');
    // f2 -> < IDENTIFIER >
    n.f2.accept(this);
    sb.append(' ');
    // f3 -> AnnotationTypeBody()
    n.f3.accept(this);
  }
  
  /**
   * Visits a {@link AnnotationTypeBody} node, whose children are the following :
   * <p>
   * f0 -> "{"<br>
   * f1 -> ( AnnotationTypeMemberDeclaration() )*<br>
   * f2 -> "}"<br>
   * s: -667465535<br>
   *
   * @param n - the node to visit
   */
  @Override
  @NodeFieldsSignature({
      -667465535, JTB_SIG_ANNOTATIONTYPEBODY, JTB_USER_ANNOTATIONTYPEBODY
  })
  public void visit(final AnnotationTypeBody n) {
    // f0 -> "{"
    n.f0.accept(this);
    // f1 -> ( AnnotationTypeMemberDeclaration() )*
    if (n.f1.present()) {
      oneNewLine(n, "a");
      spc.updateSpc(+1);
      oneNewLine(n, "b");
      sb.append(spc.spc);
      for (final Iterator<INode> e = n.f1.elements(); e.hasNext();) {
        e.next().accept(this);
        oneNewLine(n, "c");
        if (e.hasNext()) {
          oneNewLine(n, "d");
          sb.append(spc.spc);
        }
      }
      spc.updateSpc(-1);
    }
    sb.append(spc.spc);
    // f2 -> "}"
    n.f2.accept(this);
    oneNewLine(n, "e");
  }
  
  /**
   * Visits a {@link AnnotationTypeMemberDeclaration} node, whose child is the following :
   * <p>
   * f0 -> . %0 #0 Modifiers()<br>
   * .. .. . .. #1 ( &0 $0 Type() $1 < IDENTIFIER > $2 "(" $3 ")"<br>
   * .. .. . .. .. . .. $4 [ DefaultValue() ]<br>
   * .. .. . .. .. . .. $5 ";"<br>
   * .. .. . .. .. | &1 ClassOrInterfaceDeclaration()<br>
   * .. .. . .. .. | &2 EnumDeclaration()<br>
   * .. .. . .. .. | &3 AnnotationTypeDeclaration()<br>
   * .. .. . .. .. | &4 FieldDeclaration() )<br>
   * .. .. | %1 ";"<br>
   * s: -1120210008<br>
   *
   * @param n - the node to visit
   */
  @Override
  @NodeFieldsSignature({
      -1120210008, JTB_SIG_ANNOTATIONTYPEMEMBERDECLARATION, JTB_USER_ANNOTATIONTYPEMEMBERDECLARATION
  })
  public void visit(final AnnotationTypeMemberDeclaration n) {
    if (n.f0.which == 0) {
      // %0 #0 Modifiers() #1 ( &0 $0 Type() $1 < IDENTIFIER > $2 "(" $3 ")"$4 [ DefaultValue() ] $5 ";"
      // | &1 ClassOrInterfaceDeclaration() | &2 EnumDeclaration()
      // | &3 AnnotationTypeDeclaration()| &4 FieldDeclaration() )
      final NodeSequence seq = (NodeSequence) n.f0.choice;
      // #0 Modifiers print the last space if not empty
      seq.elementAt(0).accept(this);
      // #1 ( &0 $0 Type() $1 < IDENTIFIER > $2 "(" $3 ")"$4 [ DefaultValue() ] $5 ";"
      // | &1 ClassOrInterfaceDeclaration()| &2 EnumDeclaration() | &3 AnnotationTypeDeclaration()
      // | &4 FieldDeclaration() )
      final NodeChoice ch = (NodeChoice) seq.elementAt(1);
      if (ch.which == 0) {
        // &0 $0 Type() $1 < IDENTIFIER > $2 "(" $3 ")"$4 [ DefaultValue() ] $5 ";"
        final NodeSequence seq1 = (NodeSequence) ch.choice;
        // $0 Type()
        seq1.elementAt(0).accept(this);
        sb.append(' ');
        // $1 < IDENTIFIER >
        seq1.elementAt(1).accept(this);
        // $2 "("
        seq1.elementAt(2).accept(this);
        // $3 ")"
        seq1.elementAt(3).accept(this);
        // $4 [ DefaultValue() ]
        final NodeOptional opt = (NodeOptional) seq1.elementAt(3);
        if (opt.present()) {
          sb.append(' ');
          opt.node.accept(this);
        }
        // $5 ";"
        seq1.elementAt(5).accept(this);
      } else {
        // &1 ClassOrInterfaceDeclaration()| &2 EnumDeclaration()| &3 AnnotationTypeDeclaration()| &4
        // FieldDeclaration()
        ch.choice.accept(this);
      }
    } else {
      // %1 ";"
      n.f0.choice.accept(this);
    }
  }
  
}

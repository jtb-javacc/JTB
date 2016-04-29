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

import static EDU.purdue.jtb.misc.Globals.*;
import static EDU.purdue.jtb.visitor.GlobalDataBuilder.BNF_IND;
import static EDU.purdue.jtb.visitor.GlobalDataBuilder.DONT_CREATE;
import static EDU.purdue.jtb.visitor.GlobalDataBuilder.JC_IND;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import EDU.purdue.jtb.misc.Messages;
import EDU.purdue.jtb.misc.Spacing;
import EDU.purdue.jtb.misc.VarInfo;
import EDU.purdue.jtb.syntaxtree.BNFProduction;
import EDU.purdue.jtb.syntaxtree.Block;
import EDU.purdue.jtb.syntaxtree.BlockStatement;
import EDU.purdue.jtb.syntaxtree.ClassOrInterfaceBody;
import EDU.purdue.jtb.syntaxtree.ClassOrInterfaceType;
import EDU.purdue.jtb.syntaxtree.CompilationUnit;
import EDU.purdue.jtb.syntaxtree.DoStatement;
import EDU.purdue.jtb.syntaxtree.Expansion;
import EDU.purdue.jtb.syntaxtree.ExpansionChoices;
import EDU.purdue.jtb.syntaxtree.ExpansionUnit;
import EDU.purdue.jtb.syntaxtree.ExpansionUnitTCF;
import EDU.purdue.jtb.syntaxtree.ForStatement;
import EDU.purdue.jtb.syntaxtree.INode;
import EDU.purdue.jtb.syntaxtree.IdentifierAsString;
import EDU.purdue.jtb.syntaxtree.IfStatement;
import EDU.purdue.jtb.syntaxtree.ImportDeclaration;
import EDU.purdue.jtb.syntaxtree.JavaCCInput;
import EDU.purdue.jtb.syntaxtree.JavaCodeProduction;
import EDU.purdue.jtb.syntaxtree.LabeledStatement;
import EDU.purdue.jtb.syntaxtree.LocalLookahead;
import EDU.purdue.jtb.syntaxtree.LocalVariableDeclaration;
import EDU.purdue.jtb.syntaxtree.NodeChoice;
import EDU.purdue.jtb.syntaxtree.NodeList;
import EDU.purdue.jtb.syntaxtree.NodeListOptional;
import EDU.purdue.jtb.syntaxtree.NodeOptional;
import EDU.purdue.jtb.syntaxtree.NodeSequence;
import EDU.purdue.jtb.syntaxtree.NodeToken;
import EDU.purdue.jtb.syntaxtree.PrimitiveType;
import EDU.purdue.jtb.syntaxtree.ReferenceType;
import EDU.purdue.jtb.syntaxtree.RegularExprProduction;
import EDU.purdue.jtb.syntaxtree.RegularExpression;
import EDU.purdue.jtb.syntaxtree.ResultType;
import EDU.purdue.jtb.syntaxtree.ReturnStatement;
import EDU.purdue.jtb.syntaxtree.Statement;
import EDU.purdue.jtb.syntaxtree.StringLiteral;
import EDU.purdue.jtb.syntaxtree.SwitchStatement;
import EDU.purdue.jtb.syntaxtree.SynchronizedStatement;
import EDU.purdue.jtb.syntaxtree.TryStatement;
import EDU.purdue.jtb.syntaxtree.Type;
import EDU.purdue.jtb.syntaxtree.WhileStatement;

/**
 * The {@link Annotator} visitor generates the (jtb) annotated .jj file containing the tree-building
 * code.
 * <p>
 * {@link Annotator}, {@link CommentsPrinter} and {@link ClassesFinder} depend on each other to
 * create and use classes.
 * <p>
 * Code is printed in a buffer and {@link #saveToFile} is called to save it in the output file.
 * <p>
 * {@link Annotator} works as follows:
 * <ul>
 * <li>it gets and memorizes the result type of a {@link JavaCodeProduction} or a
 * {@link BNFProduction},</li>
 * <li>in {@link #generateJcRHS(JavaCodeProduction)} and {@link #generateBnfRHS(BNFProduction)}, it
 * redirects output to a temporary buffer,
 * <li>it walks down the tree, prints the RHS into the temporary buffer, and builds the varList,
 * <li>it traverses varList, prints the variable declarations to the main buffer
 * <li>it prints the Block (for a {@link BNFProduction} to the main buffer, then the temporary
 * buffer into the main buffer.
 * </ul>
 * When it wants to print a node and its subtree without annotating it, it uses an instance of the
 * {@link JavaCCPrinter} to visit the node.
 * <p>
 * 
 * @author Marc Mazas
 * @version 1.4.0 : 05-08/2009 : MMa : adapted to JavaCC v4.2 grammar and JDK 1.5
 * @version 1.4.0.2 : 01/2010 : MMa : fixed output of else in IfStatement
 * @version 1.4.6 : 01/2011 : FA/MMa : add -va and -npfx and -nsfx options
 * @version 1.4.7 : 12/2011 : MMa : commented the JavaCodeProduction visit method ;<br>
 *          optimized JTBToolkit class's code<br>
 *          1.4.7 : 07/2012 : MMa : followed changes in jtbgram.jtb (IndentifierAsString(),
 *          ForStatement()), updated grammar comments in the code<br>
 *          1.4.7 : 08-09/2012 : MMa : fixed soft errors for empty NodeChoice (bug JTB-1) ;<br>
 *          fixed error on return statement for a void production ; added non node creation ;<br>
 *          tuned messages labels and added the line number finder visitor ; moved the inner
 *          GlobalDataFinder to {@link GlobalDataBuilder}
 * @version 1.4.8 : 10/2012 : MMa : added JavaCodeProduction class generation if requested ;<br>
 *          fixed visit LocalVariableDeclaration ; improved specials printing
 * @version 1.4.9 : 01/2015 : MMa : fixed regression in {@link #bnfFinalActions(VarInfo)}
 * @version 1.4.11 : 03/2016 : MMa : fixed column numbers in warnings, and conditions for warning
 *          "Empty choice : a NodeChoice with a 'null' choice member ..."
 */
public class Annotator extends JavaCCPrinter {

  /** The {@link JavaCCPrinter} visitor for lower nodes which don't need annotation */
  final JavaCCPrinter              jccpv;
  /** The {@link CompilationUnitPrinter} visitor for printing the compilation unit */
  final CompilationUnitPrinter     cupv;
  /** The counter for generated variables names */
  int                              varNum;
  /** The list of all variables to be declared */
  List<VarInfo>                    varList            = new ArrayList<VarInfo>();
  /**
   * The list of all "outer" variables (production's children nodes) for the production default
   * constructor
   */
  List<VarInfo>                    outerVars          = new ArrayList<VarInfo>();
  /** The RegularExpression generated token name */
  String                           reTokenName        = null;
  /** True to tell lower layers whether to create the RegularExpression node, false otherwise */
  boolean                          createRENode       = true;
  /** The last variable (this of the parent) generated so far */
  VarInfo                          parentVar;
  /**
   * True to annotate the node (generate the additional java code to build the sequences and the
   * choices), false otherwise (for Blocks, LocalLookaheads, "void" JavaCodeProduction)
   */
  boolean                          annotateNode;
  /** The name of the current production */
  String                           curProduction;
  /** The list of additional variables to initialize */
  List<VarInfo>                    extraVarsList      = null;
  /** The JavaCodeProduction or BNFProduction result type */
  String                           resultType         = null;
  /** The JavaCodeProduction or BNFProduction result type specials */
  String                           resultTypeSpecials = null;
  /** The LocalVariableDeclaration or ClassOrInterfaceType type */
  String                           type               = null;
  /** The LocalVariableDeclaration or ClassOrInterfaceType type specials */
  String                           typeSpecials       = null;
  /** The {@link ExpansionUnitTypeCounter} visitor to count ExpansionUnit types */
  final ExpansionUnitTypeCounter   eutcv              = new ExpansionUnitTypeCounter();
  /** True if in ExpansionUnit type 3 (ExpansionUnitTCF), false otherwise */
  boolean                          inEUT3;
  /** The line number of the first token in an ExpansionChoices */
  int                              lnft;
  /** The column number of the first token in an ExpansionChoices */
  int                              cnft;
  /**
   * The {@link ExpansionChoicesLineNumber} visitor to find the line number of the first token of a
   * production under an ExpansionChoices
   */
  final ExpansionChoicesLineNumber lnftfv             = new ExpansionChoicesLineNumber();

  /**
   * Constructor which will allocate a default buffer and indentation.
   * 
   * @param aGdbv - the global data builder visitor
   */
  public Annotator(final GlobalDataBuilder aGdbv) {
    super(aGdbv);
    jccpv = new JavaCCPrinter(aGdbv, sb, spc);
    cupv = new CompilationUnitPrinter(sb, spc);
    inEUT3 = false;
  }

  /*
   * Convenience methods
   */

  /**
   * Generates a new variable name (n0, n1, ...)
   * 
   * @return the new variable name
   */
  final String genNewVarName() {
    return "n" + String.valueOf(varNum++);
  }

  /**
   * Reset the variable generator counter
   */
  final void resetVarNum() {
    varNum = 0;
  }

  /** {@inheritDoc} */
  @Override
  void oneNewLine(final INode n) {
    sb.append(nodeClassComment(n)).append(LS);
  }

  /** {@inheritDoc} */
  @Override
  void oneNewLine(final INode n, final String str) {
    sb.append(nodeClassComment(n, str)).append(LS);
  }

  /**
   * Prints into a given buffer a node class comment, an extra given comment, and a new line.
   * 
   * @param n the node for the node class comment
   * @param str the extra comment
   * @param aSb the buffer to print into
   */
  static void oneNewLine(final INode n, final String str, final StringBuilder aSb) {
    aSb.append(nodeClassComment(n, str)).append(LS);
  }

  /** {@inheritDoc} */
  @Override
  void twoNewLines(final INode n) {
    oneNewLine(n);
    oneNewLine(n);
  }

  /**
   * Returns a node class comment (a //ann followed by the node class short name if global flag set,
   * nothing otherwise).
   * 
   * @param n - the node for the node class comment
   * @return the node class comment
   */
  private static String nodeClassComment(final INode n) {
    if (DEBUG_CLASS_COMMENTS) {
      final String s = n.toString();
      final int b = s.lastIndexOf('.') + 1;
      final int e = s.indexOf('@');
      if (b == -1 || e == -1)
        return " //ann " + s;
      else
        return " //ann " + s.substring(b, e);
    } else
      return "";
  }

  /**
   * Returns a node class comment with an extra comment (a //ann followed by the node class short
   * name plus the extra comment if global flag set, nothing otherwise).
   * 
   * @param n - the node for the node class comment
   * @param str - the extra comment
   * @return the node class comment
   */
  private static String nodeClassComment(final INode n, final String str) {
    if (DEBUG_CLASS_COMMENTS)
      return nodeClassComment(n).concat(" ").concat(str);
    else
      return "";
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
   * 
   * @param n - the node to visit
   */
  @Override
  public void visit(final JavaCCInput n) {
    // generate now output
    sb.append(spc.spc);
    sb.append(genFileHeaderComment());
    oneNewLine(n);
    sb.append(spc.spc);
    // f0 -> JavaCCOptions() : don't want to annotate under
    n.f0.accept(jccpv);
    twoNewLines(n);
    sb.append(spc.spc);
    // f1 -> "PARSER_BEGIN"
    n.f1.accept(this);
    // f2 -> "("
    n.f2.accept(this);
    // f3 -> IdentifierAsString()
    n.f3.accept(this);
    // f4 -> ")"
    n.f4.accept(this);
    oneNewLine(n);
    sb.append(spc.spc);
    // f5 -> CompilationUnit()
    n.f5.accept(cupv);
    oneNewLine(n);
    sb.append(spc.spc);
    // f6 -> "PARSER_END"
    n.f6.accept(this);
    // f7 -> "("
    n.f7.accept(this);
    // f8 -> IdentifierAsString()
    n.f8.accept(this);
    // f9 -> ")"
    n.f9.accept(this);
    twoNewLines(n);
    sb.append(spc.spc);
    // f10 -> ( Production() )+
    for (final Iterator<INode> e = n.f10.elements(); e.hasNext();) {
      e.next().accept(this);
      if (e.hasNext()) {
        sb.append(spc.spc);
        twoNewLines(n);
      }
    }
    oneNewLine(n);
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
    if (!n.f6.present()) {
      // node not to be generated, don't want to annotate under
      n.accept(jccpv);
      return;
    }
    // node to be generated
    curProduction = n.f3.f0.tokenImage;
    // f0 -> "JAVACODE"
    n.f0.accept(this);
    oneNewLine(n, "a");
    sb.append(spc.spc);
    // f1 -> AccessModifier()
    n.f1.accept(this);
    // f2 -> ResultType()
    getResultTypeSpecials(n.f2);
    sb.append(resultTypeSpecials);
    sb.append(getFixedName(curProduction));
    sb.append(" ");
    // f3 -> IdentifierAsString()
    // must be prefixed / suffixed
    sb.append(getFixedName(n.f3.f0.tokenImage));
    // f4 -> FormalParameters()
    sb.append(genJavaBranch(n.f4));
    // f5 -> [ #0 "throws" #1 Name() #2 ( $0 "," $1 Name() )* ]
    if (n.f5.present()) {
      final NodeSequence seq = (NodeSequence) n.f5.node;
      sb.append(" ");
      // #0 "throws"
      seq.elementAt(0).accept(this);
      sb.append(" ");
      // #1 Name()
      sb.append(genJavaBranch(seq.elementAt(1)));
      // #2 ( $0 "," $1 Name() )*
      final NodeListOptional opt = ((NodeListOptional) seq.elementAt(2));
      if (opt.present()) {
        for (final Iterator<INode> e = opt.elements(); e.hasNext();) {
          final NodeSequence seq1 = (NodeSequence) e.next();
          //  $0 ","
          sb.append(genJavaBranch(seq1.elementAt(0)));
          sb.append(" ");
          // $1 Name()
          sb.append(genJavaBranch(seq1.elementAt(1)));
        }
      }
    }
    // f6 -> [ "%" ]
    // print it in a block comment
    if (n.f6.present())
      sb.append(" /*%*/ ");
    oneNewLine(n, "b");
    sb.append(spc.spc);
    // generate the JcRHS into a temporary buffer and collect variables
    final StringBuilder rhsSB = generateJcRHS(n);
    // print the variables declarations
    sb.append(spc.spc);
    // print the JcRHS buffer
    sb.append(rhsSB);
    // reset global variable
    resultType = null;
  }

  /**
   * Returns a string with the RHS (after the ":") of the current BNFProduction.<br>
   * When this function returns, varList and outerVars will have been built and will be used by the
   * calling method.
   * <p>
   * Visits the {@link JavaCodeProduction}<br>
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
   * @param n - the node to process
   * @return the generated buffer
   */
  StringBuilder generateJcRHS(final JavaCodeProduction n) {
    final StringBuilder mainSB = sb;
    final StringBuilder newSB = new StringBuilder(512);
    sb = jccpv.sb = newSB;
    // node to be generated, specific processing
    // in Block f0 -> "{"
    n.f7.f0.accept(this);
    oneNewLine(n, "generateJcRHS a");
    spc.updateSpc(+1);
    sb.append(spc.spc);
    // in Block f1 -> ( BlockStatement() )*
    n.f7.f1.accept(this);
    // must be prefixed / suffixed
    oneNewLine(n, "generateJcRHS b");
    sb.append(spc.spc);
    sb.append("{ return new ").append(getFixedName(n.f3.f0.tokenImage)).append("(); }");
    oneNewLine(n, "generateJcRHS c");
    spc.updateSpc(-1);
    sb.append(spc.spc);
    // in Block f2 -> "}"
    n.f7.f2.accept(this);
    sb = jccpv.sb = mainSB;
    return newSB;
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
    if (n.f5.present()) {
      // node not to be generated, don't want to annotate under
      n.accept(jccpv);
      return;
    }
    // node to be generated
    varList.clear();
    outerVars.clear();
    parentVar = null;
    resetVarNum();
    bnfLvl = 0;
    annotateNode = true;
    curProduction = n.f2.f0.tokenImage;
    // f0 -> AccessModifier()
    n.f0.accept(this);
    // f1 -> ResultType()
    // node to be generated
    // just print the f1 specials, then print the IdentifierAsString instead of the ResultType
    getResultTypeSpecials(n.f1);
    sb.append(resultTypeSpecials);
    sb.append(getFixedName(curProduction));
    sb.append(" ");
    // f2 -> IdentifierAsString()
    // must be prefixed / suffixed
    sb.append(getFixedName(n.f2.f0.tokenImage));
    // f3 -> FormalParameters()
    sb.append(genJavaBranch(n.f3));
    // f4 -> [ #0 "throws" #1 Name() #2 ( $0 "," $1 Name() )* ]
    if (n.f4.present()) {
      final NodeSequence seq = (NodeSequence) n.f4.node;
      sb.append(" ");
      seq.elementAt(0).accept(this);
      sb.append(" ");
      seq.elementAt(1).accept(this);
      final NodeListOptional nlo = (NodeListOptional) seq.elementAt(2);
      if (nlo.present()) {
        for (final Iterator<INode> e = nlo.elements(); e.hasNext();) {
          final NodeSequence seq1 = (NodeSequence) e.next();
          seq1.elementAt(0).accept(this);
          sb.append(" ");
          seq1.elementAt(1).accept(this);
        }
      }
    }
    // f5 -> [ "!" ]
    // should not occur due to first test in the method
    // f6 -> ":"
    sb.append(" ");
    n.f6.accept(this);
    oneNewLine(n, "a");
    // generate the RHS (f9 -> ExpansionChoices()) into a temporary buffer and collect variables
    final StringBuilder rhsSB = generateBnfRHS(n);
    // print the variables declarations
    sb.append(spc.spc);
    // block left brace
    n.f7.f0.accept(this);
    spc.updateSpc(+1);
    oneNewLine(n, "b");
    sb.append(spc.spc);
    sb.append("// --- JTB generated node declarations ---");
    oneNewLine(n, "c");
    sb.append(spc.spc);
    for (final Iterator<VarInfo> e = varList.iterator(); e.hasNext();) {
      sb.append(e.next().generateNodeDeclaration());
      if (e.hasNext()) {
        oneNewLine(n, "d");
        sb.append(spc.spc);
      }
    }
    // f7 -> Block() (user variables declarations)
    if (n.f7.f1.present()) {
      // print block declarations only if non empty
      // don't print "{" and "}" otherwise the resulting inner scope will prevent to use declarations
      oneNewLine(n, "e");
      sb.append(spc.spc);
      sb.append("// --- user BNFProduction java block ---");
      oneNewLine(n, "f");
      sb.append(spc.spc);
      for (final Iterator<INode> e = n.f7.f1.elements(); e.hasNext();) {
        // BlockStatement()
        e.next().accept(this);
        if (e.hasNext()) {
          oneNewLine(n, "g");
          sb.append(spc.spc);
        }
      }
    }
    spc.updateSpc(-1);
    oneNewLine(n, "h");
    sb.append(spc.spc);
    // block right brace
    n.f7.f2.accept(this);
    oneNewLine(n, "i");
    sb.append(spc.spc);
    // print the RHS buffer (f9 -> ExpansionChoices())
    sb.append(rhsSB);
    // reset global variable
    resultType = null;
  }

  /**
   * Memorizes the ResultType specials and the ResultType. Walks down the tree to find the first
   * token.
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
   */
  void getResultTypeSpecials(final ResultType rt) {
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
    resultTypeSpecials = tk.getSpecials(spc.spc);
    resultType = tk.tokenImage;
    return;
  }

  /**
   * Returns a string with the RHS (after the ":") of the current BNFProduction.<br>
   * When this function returns, varList and outerVars will have been built and will be used by the
   * calling method.
   * <p>
   * Visits the {@link BNFProduction}<br>
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
   * @param n - the node to process
   * @return the generated buffer
   */
  StringBuilder generateBnfRHS(final BNFProduction n) {
    final StringBuilder mainSB = sb;
    final StringBuilder newSB = new StringBuilder(512);
    sb = jccpv.sb = newSB;
    // node to be generated, specific processing
    // f8 -> "{"
    n.f8.accept(this);
    oneNewLine(n, "generateBnfRHS a");
    spc.updateSpc(+1);
    sb.append(spc.spc);
    // outerVars will be set further down the tree in finalActions
    // f9 -> ExpansionChoices()
    n.f9.accept(this);
    // must be prefixed / suffixed
    sb.append("{ return new ").append(getFixedName(n.f2.f0.tokenImage)).append("(");
    final Iterator<VarInfo> e = outerVars.iterator();
    if (e.hasNext()) {
      sb.append(e.next().getName());
      for (; e.hasNext();)
        sb.append(", ").append(e.next().getName());
    }
    sb.append("); }");
    oneNewLine(n, "generateBnfRHS b");
    spc.updateSpc(-1);
    sb.append(spc.spc);
    // f10 -> "}"
    n.f10.accept(this);
    sb = jccpv.sb = mainSB;
    return newSB;
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
    // Don't want to annotate under
    n.accept(jccpv);
  }

  /**
   * Common end-code for annotation methods
   * 
   * @param varInfo - the variable to annotate
   */
  void bnfFinalActions(final VarInfo varInfo) {
    if (bnfLvl == 0)
      outerVars.add(varInfo);
    else {
      parentVar = varInfo;
      annotateNode = true;
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
    if (!n.f1.present())
      // f0 -> Expansion()
      n.f0.accept(this);
    else {
      final String name = genNewVarName();
      final VarInfo varInfo = new VarInfo(nodeChoice, name, "null");
      varList.add(varInfo);
      genExpChWithChoices(n, name);
      bnfFinalActions(varInfo);
    }
  }

  /**
   * Visits the {@link ExpansionChoices}, and adds the NodeChoice variable declaration<br>
   * (called only when there is a choice, i.e. f1 is present).
   * <p>
   * f0 -> Expansion()<br>
   * f1 -> ( #0 "|" #1 Expansion() )*<br>
   * 
   * @param n - the node to process
   * @param varName - the NodeChoice variable name
   */
  void genExpChWithChoices(final ExpansionChoices n, final String varName) {
    int whichVal = 0;
    // visit the first choice (f0)
    // f0 -> Expansion()
    // extra parenthesis needed !
    sb.append("(");
    oneNewLine(n, "genExpChWithChoices a");
    spc.updateSpc(+1);
    sb.append(spc.spc);
    ++bnfLvl;
    n.f0.accept(this);
    --bnfLvl;
    final int totalVal = n.f1.size() + 1;
    if (!annotateNode && parentVar == null) {
      lnftfv.reset();
      n.f0.accept(lnftfv);
      Messages.warning("Empty choice : a NodeChoice with a 'null' choice member will be " +
                       "generated for choice '" + whichVal + "' in '" + curProduction +
                       "()'. To avoid this add an empty (fake) node.", lnft, cnft);
    }
    genNewNodeChoiceVarDecl(varName, whichVal, totalVal);
    oneNewLine(n, "genExpChWithChoices b");
    spc.updateSpc(-1);
    sb.append(spc.spc);
    ++whichVal;

    // visit the remaining choices (f1)
    // f1 -> ( #0 "|" #1 Expansion() )*
    for (final Iterator<INode> e = n.f1.elements(); e.hasNext();) {
      final NodeSequence seq = (NodeSequence) e.next();
      // "|"
      seq.elementAt(0).accept(this);
      oneNewLine(n, "genExpChWithChoices c");
      spc.updateSpc(+1);
      sb.append(spc.spc);
      // Expansion()
      ++bnfLvl;
      seq.elementAt(1).accept(this);
      --bnfLvl;
      if (!annotateNode && parentVar == null) {
        lnft = ((NodeToken) seq.elementAt(0)).beginLine;
        cnft = ((NodeToken) seq.elementAt(0)).beginColumn;
        Messages.warning("Empty choice : a NodeChoice with a 'null' choice member will be " +
                         "generated for choice '" + whichVal + "' in '" + curProduction +
                         "()'. To avoid this add an empty (fake) node.", lnft, cnft);
      }
      genNewNodeChoiceVarDecl(varName, whichVal, totalVal);
      oneNewLine(n, "genExpChWithChoices d");
      spc.updateSpc(-1);
      sb.append(spc.spc);
      ++whichVal;
      parentVar = null;
    }
    // extra parenthesis needed !
    sb.append(")");
    oneNewLine(n, "genExpChWithChoices e");
    sb.append(spc.spc);
  }

  /**
   * Generates a new NodeChoice variable declaration.
   * 
   * @param varName - the variable name
   * @param whichVal - the value of the which field
   * @param totalVal - the value of the total field
   */
  void genNewNodeChoiceVarDecl(final String varName, final int whichVal, final int totalVal) {
    sb.append("{ ").append(varName).append(" = new NodeChoice(")
      .append(parentVar == null ? null : parentVar.getName()).append(", ")
      .append(String.valueOf(whichVal)).append(", ").append(String.valueOf(totalVal))
      .append("); }");
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
    // f0 -> ( #0 "LOOKAHEAD" #1 "(" #2 LocalLookahead() #3 ")" )?
    if (n.f0.present()) {
      final NodeSequence seq = (NodeSequence) n.f0.node;
      seq.elementAt(0).accept(this);
      seq.elementAt(1).accept(this);
      sb.append(" ");
      seq.elementAt(2).accept(this);
      sb.append(" ");
      seq.elementAt(3).accept(this);
      oneNewLine(n, "a");
      sb.append(spc.spc);
    }
    // f1 -> ( ExpansionUnit() )+
    if (bnfLvl == 0) {
      n.f1.accept(this);
    } else {
      final ExpansionUnitTypeCounter v = new ExpansionUnitTypeCounter();
      n.f1.accept(v);
      if (v.getNbNormals() == 0) {
        // LocalLookahead or Block, generate it
        n.f1.accept(this);
      } else if (v.getNbNormals() == 1) {
        n.f1.accept(this);
        // The line below fixes the C grammar bug where something like
        // ( A() | B() | C() { someJavaCode(); } ) would cause an "Empty NodeChoice" error
        // the previous comment is not understood
        // the following line, coming from the original JTB, is now commented out as seems useless
        //        annotateNode = true;
      } else {
        final String name = genNewVarName();
        final VarInfo varInfo = new VarInfo(nodeSeq, name, "null");
        varList.add(varInfo);
        genExpSequence(n, name);
        parentVar = varInfo;
        annotateNode = true;
      }
    }
  }

  /**
   * Visits the {@link ExpansionChoices}, and adds the NodeSequence variable declaration for a given
   * identifier, and adds the nodes to the parent.
   * <p>
   * f0 -> ( #0 "LOOKAHEAD" #1 "(" #2 LocalLookahead() #3 ")" )?<br>
   * f1 -> ( ExpansionUnit() )+<br>
   * 
   * @param n - the node to process
   * @param ident - the identifier
   */
  void genExpSequence(final Expansion n, final String ident) {
    // f0 -> ( #0 "LOOKAHEAD" #1 "(" #2 LocalLookahead() #3 ")" )?
    // nothing done for f0 (done in Expansion)

    // f1 -> ( ExpansionUnit() )+
    final Iterator<INode> e = n.f1.elements();
    // process first ExpansionUnit (to generate the NodeSequence variable declaration)
    final ExpansionUnit firstExpUnit = (ExpansionUnit) e.next();
    //    if (expUnit.f0.which == 0) {
    //      // if the unit is a LOOKAHEAD, visit it first
    if (firstExpUnit.f0.which <= 1) {
      // if the ExpansionUnit is a LOOKAHEAD or a Block, visit it first
      firstExpUnit.accept(this);
      genNewNodeSequenceVarDecl(ident, n.f1.size());
      oneNewLine(n, "genExpSequence a");
      sb.append(spc.spc);
    } else {
      // the ExpansionUnit is not a LOOKAHEAD nor a Block
      genNewNodeSequenceVarDecl(ident, n.f1.size());
      oneNewLine(n, "genExpSequence b");
      sb.append(spc.spc);
      ++bnfLvl;
      firstExpUnit.accept(this);
      --bnfLvl;
      if (annotateNode) {
        sb.append(addNodeToParent(ident, parentVar.getName()));
        oneNewLine(n, "genExpSequence c");
        sb.append(spc.spc);
      }
    }
    // visit the other ExpansionUnits that need to be
    for (; e.hasNext();) {
      ++bnfLvl;
      (e.next()).accept(this);
      --bnfLvl;
      if (annotateNode) {
        sb.append(addNodeToParent(ident, parentVar.getName()));
        oneNewLine(n, "genExpSequence d");
        sb.append(spc.spc);
      }
    }
  }

  /**
   * Generates a new NodeSequence variable declaration.
   * 
   * @param varName - the variable name
   * @param nbNodes - the number of nodes
   */
  void genNewNodeSequenceVarDecl(final String varName, final int nbNodes) {
    sb.append("{ ").append(varName).append(" = new NodeSequence(").append(nbNodes).append("); }");
  }

  /**
   * Returns the java block for adding a node to its parent.
   * 
   * @param parentName - the parent node
   * @param varName - the node's variable name
   * @return the java block
   */
  final static String addNodeToParent(final String parentName, final String varName) {
    return "{ ".concat(parentName).concat(".addNode(").concat(varName).concat("); }");
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
  public void visit(final LocalLookahead n) {
    // Don't want to annotate under
    n.accept(jccpv);
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
    annotateNode = true;
    switch (n.f0.which) {
      case 0:
        // %0 #0 "LOOKAHEAD" #1 "(" #2 LocalLookahead() #3 ")"
        seq = (NodeSequence) n.f0.choice;
        seq.elementAt(0).accept(this);
        seq.elementAt(1).accept(this);
        sb.append(" ");
        seq.elementAt(2).accept(this);
        sb.append(" ");
        seq.elementAt(3).accept(this);
        oneNewLine(n, "0");
        sb.append(spc.spc);
        return;

      case 1:
        // %1 Block()
        n.f0.choice.accept(this);
        oneNewLine(n, "1");
        sb.append(spc.spc);
        annotateNode = false;
        return;

      case 2:
        // %2 #0 "[" #1 ExpansionChoices() #2 "]"
        genBracketExpCh(n);
        return;

      case 3:
        // %3 ExpansionUnitTCF()
        n.f0.choice.accept(this);
        return;

      case 4:
        // %4 #0 [ $0 PrimaryExpression() $1 "=" ]
        // .. #1 ( &0 $0 IdentifierAsString() $1 Arguments()
        // .. .. . $2 [ "!" ]
        // .. .. | &1 $0 RegularExpression()
        // .. .. . .. $1 [ ?0 "." ?1 < IDENTIFIER > ]
        // .. .. . .. $2 [ "!" ] )
        seq = (NodeSequence) n.f0.choice;
        final NodeOptional opt1 = (NodeOptional) seq.elementAt(0);
        final NodeChoice ch = (NodeChoice) seq.elementAt(1);
        final NodeSequence seq1 = (NodeSequence) ch.choice;
        String name;
        if (ch.which == 0) {
          final NodeToken nt = ((IdentifierAsString) seq1.elementAt(0)).f0;
          // &0 $0 IdentifierAsString() $1 Arguments() $2 [ "!" ]
          final String ident = nt.tokenImage;
          // annotate if not non standard BNFProduction or if non standard JavaCodeProduction
          annotateNode = true;
          final String prod = gdbv.getProdHT().get(ident);
          if (prod != null) {
            final String indProd = prod.substring(0, 1);
            if (BNF_IND.equals(indProd)) {
              if (gdbv.getNsnHT().containsKey(ident))
                annotateNode = false;
            } else if (JC_IND.equals(indProd)) {
              if (!gdbv.getNsnHT().containsKey(ident))
                annotateNode = false;
            }
          }
          // $0 IdentifierAsString()
          // must be prefixed / suffixed if to be created
          // generate 'ni = JavaCodeProduction()' if node is to be created, otherwise 'JavaCodeProduction()'
          // generate 'BNFProduction()' if node is not to be created, otherwise 'ni = Production()'
          final boolean creLocNode = annotateNode && !((NodeOptional) seq1.elementAt(2)).present();
          boolean lastCharIsSpace = true;
          if (keepSpecialTokens) {
            final String spStr = nt.getSpecials(spc.spc);
            sb.append(spStr);
            final int len = spStr.length();
            if (len > 0)
              lastCharIsSpace = (spStr.charAt(len - 1) == ' ');
          }
          VarInfo varInfo = null;
          if (creLocNode) {
            name = genNewVarName();
            varInfo = inEUT3 ? new VarInfo(ident, name, "null") : new VarInfo(ident, name);
            varList.add(varInfo);
            if (!lastCharIsSpace)
              sb.append(' ');
            sb.append(name);
            sb.append(" = ");
          }
          final String ias = nt.tokenImage;
          sb.append(creLocNode ? getFixedName(ias) : ias);
          // $1 Arguments()
          sb.append(genJavaBranch(seq1.elementAt(1)));
          if (creLocNode)
            bnfFinalActions(varInfo);
          oneNewLine(n, "4a");
          sb.append(spc.spc);
          if (opt1.present()) {
            // generate p = jtbrt_Identifier;
            final NodeSequence seq2 = (NodeSequence) opt1.node;
            sb.append("{ ");
            // $0 PrimaryExpression()
            sb.append(genJavaBranch(seq2.elementAt(0)));
            sb.append(" ");
            // $1 "="
            seq2.elementAt(1).accept(this);
            sb.append(" ");
            // IdentifierAsString() -> jtbrt_Identifier
            sb.append(jtbRtPrefix).append(ident);
            sb.append("; }");
            oneNewLine(n, "4b");
            sb.append(spc.spc);
          }
        } else {
          // &1 $0 RegularExpression() $1 [ ?0 "." ?1 < IDENTIFIER > ] $2 [ "!" ]
          // $2 [ "!" ]
          createRENode = !((NodeOptional) seq1.elementAt(2)).present();
          // $0 RegularExpression()
          seq1.elementAt(0).accept(this);
          // $1 [ ?0 "." ?1 < IDENTIFIER > ]
          final NodeOptional opt2 = (NodeOptional) seq1.elementAt(1);
          if (opt2.present()) {
            sb.append(".");
            ((NodeSequence) opt2.node).elementAt(1).accept(this);
          }
          if (createRENode && opt1.present()) {
            // above has generated ni = RegularExpression and generate now { p = ni; }
            oneNewLine(n, "4c");
            sb.append(spc.spc);
            final NodeSequence seq2 = (NodeSequence) opt1.node;
            sb.append("{ ");
            // $0 PrimaryExpression()
            sb.append(genJavaBranch(seq2.elementAt(0)));
            sb.append(" ");
            // $1 "="
            seq2.elementAt(1).accept(this);
            sb.append(" ");
            // variable generated for RegularExpression()
            sb.append(reTokenName);
            sb.append("; }");
            oneNewLine(n, "4d");
            sb.append(spc.spc);
          }
          createRENode = true;
        }
        return;

      case 5:
        // %5 #0 "(" #1 ExpansionChoices() #2 ")"
        // .. #3 ( &0 "+" | &1 "*" | &2 "?" )?
        genParenExpCh(n);
        return;

      default:
        final String msg = "Invalid n.f0.which = " + String.valueOf(n.f0.which);
        Messages.hardErr(msg);
        throw new InternalError(msg);

    }
  }

  /**
   * Generates the bracketed expansion choices fragment<br>
   * (choice 2 of {@link Expansion Unit}.<br>
   * .. .. | %2 #0 "[" #1 ExpansionChoices() #2 "]"<br>
   * 
   * @param n - the node to process
   */
  void genBracketExpCh(final ExpansionUnit n) {
    // #0 "[" #1 ExpansionChoices() #2 "]"
    final NodeSequence seq = (NodeSequence) n.f0.choice;
    // ExpansionChoices()
    final ExpansionChoices ec = (ExpansionChoices) seq.elementAt(1);
    String name;
    // "["
    seq.elementAt(0).accept(this);
    oneNewLine(n, "genBracketExpCh 1");
    spc.updateSpc(+1);
    sb.append(spc.spc);
    name = genNewVarName();
    VarInfo varInfo;
    // build the list of expansion units
    final NodeList list = ec.f0.f1;
    // count the number of ExpansionUnits of each type (choice)
    eutcv.reset();
    ec.accept(eutcv);
    // print the first item first if it's a lookahead and not in a choice
    if (eutcv.getNbNormals() == 0) {
      // technically, we should only generate an error if it's not a choice,
      // but that greatly complicates things and an empty choice is probably useless
      final NodeToken tk = (NodeToken) seq.elementAt(0);
      Messages.softErr("Empty BNF expansion in production '" + curProduction + "()'.",
                       tk.beginLine, tk.beginColumn);
    } else {
      final ExpansionUnit firstExpUnit = (ExpansionUnit) list.nodes.get(0);
      if (extraVarsList == null)
        // top level additional variables
        varInfo = creNewVarInfoForBracket(name, true);
      else {
        // nested additional variables
        varInfo = creNewVarInfoForBracket(name, false);
        extraVarsList.add(varInfo);
      }
      varList.add(varInfo);
      final NodeOptional expLA = ec.f0.f0;
      if (!ec.f1.present() && expLA.present()) {
        // not in an ExpansionChoices choice and with an Expansion Lookahead
        final NodeSequence seq1 = (NodeSequence) expLA.node;
        seq1.elementAt(0).accept(this);
        seq1.elementAt(1).accept(this);
        sb.append(" ");
        seq1.elementAt(2).accept(this);
        sb.append(" ");
        seq1.elementAt(3).accept(this);
        oneNewLine(n, "genBracketExpCh 2");
        sb.append(spc.spc);
        // don't print lookahead twice, so remove temporary the ec.f0.f0 node
        ec.f0.f0.node = null;
        genExpChInExpUnit(ec);
        // restore ec.f0.f0 node
        ec.f0.f0.node = expLA.node;
      } else if (!ec.f1.present() && (firstExpUnit.f0.which == 0)) {
        // not in an ExpansionChoices choice and with an ExpansionUnit Lookahead
        final NodeSequence seq1 = (NodeSequence) firstExpUnit.f0.choice;
        seq1.elementAt(0).accept(this);
        seq1.elementAt(1).accept(this);
        sb.append(" ");
        seq1.elementAt(2).accept(this);
        sb.append(" ");
        seq1.elementAt(3).accept(this);
        oneNewLine(n, "genBracketExpCh 3 : ??? should we go through here ???");
        sb.append(spc.spc);
        // don't print lookahead twice, so remove it temporary from the ec.f0.f1 list of nodes
        list.nodes.get(0);
        genExpChInExpUnit(ec);
        // restore the ec.f0.f1 list of nodes
        list.nodes.add(0, firstExpUnit);
      } else {
        // in an ExpansionChoices choice or without an Expansion or ExpansionUnit Lookahead
        genExpChInExpUnit(ec);
      }
      sb.append(addNodeToParent(name, parentVar.getName()));
      bnfFinalActions(varInfo);
    }
    oneNewLine(n, "genBracketExpCh 4");
    spc.updateSpc(-1);
    sb.append(spc.spc);
    // "]"
    seq.elementAt(2).accept(this);
    oneNewLine(n, "genBracketExpCh 5");

    sb.append(spc.spc);
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
    // f0 -> "try"
    n.f0.accept(this);
    sb.append(" ");
    // f1 -> "{"
    n.f1.accept(this);
    spc.updateSpc(+1);
    oneNewLine(n, "a");
    sb.append(spc.spc);
    // f2 -> ExpansionChoices()
    inEUT3 = true;
    n.f2.accept(this);
    inEUT3 = false;
    spc.updateSpc(-1);
    oneNewLine(n, "b");
    sb.append(spc.spc);
    // f3 -> "}"
    n.f3.accept(this);
    // f4 -> ( #0 "catch" #1 "(" #2 Name() #3 < IDENTIFIER > #4 ")" #5 Block() )*
    if (n.f4.present()) {
      for (final Iterator<INode> e = n.f4.elements(); e.hasNext();) {
        final NodeSequence seq = (NodeSequence) e.next();
        oneNewLine(n, "c");
        sb.append(spc.spc);
        // #0 "catch"
        seq.elementAt(0).accept(this);
        sb.append(" ");
        // #1 "("
        seq.elementAt(1).accept(this);
        // #2 Name()
        sb.append(genJavaBranch(seq.elementAt(2)));
        sb.append(" ");
        // #3 < IDENTIFIER >
        seq.elementAt(3).accept(this);
        // #4 ")"
        seq.elementAt(4).accept(this);
        sb.append(" ");
        // #5 Block()
        seq.elementAt(5).accept(this);
      }
    }
    // f5 -> [ #0 "finally" #1 Block() ]
    if (n.f5.present()) {
      final NodeSequence seq = (NodeSequence) n.f5.node;
      oneNewLine(n, "d");
      sb.append(spc.spc);
      // #0 "finally"
      seq.elementAt(0).accept(this);
      sb.append(" ");
      // #1 Block()
      seq.elementAt(1).accept(this);
    }
    oneNewLine(n, "e");
    sb.append(spc.spc);
  }

  /**
   * Generates the parenthesized expansion choices fragment<br>
   * (choice 5 of {@link Expansion Unit}.<br>
   * .. .. | %5 #0 "(" #1 ExpansionChoices() #2 ")"<br>
   * .. .. . .. #3 ( &0 "+"<br>
   * .. .. . .. .. | &1 "*"<br>
   * .. .. . .. .. | &2 "?" )?<br>
   * 
   * @param n - the node to process
   */
  void genParenExpCh(final ExpansionUnit n) {
    // #0 "(" #1 ExpansionChoices() #2 ")" #3 ( &0 "+" | &1 "*" | &2 "?" )?
    final NodeSequence seq = (NodeSequence) n.f0.choice;
    // #1 ExpansionChoices()
    final ExpansionChoices ec = (ExpansionChoices) seq.elementAt(1);
    String name;
    // "("
    seq.elementAt(0).accept(this);
    oneNewLine(n, "genParenExpCh 1");
    spc.updateSpc(+1);
    sb.append(spc.spc);
    // seq.elementAt(3) -> #3 ( &0 "+" | &1 "*" | &2 "?" )?
    if (!((NodeOptional) seq.elementAt(3)).present()) {
      // No BNF modifier present, so either generate a NodeChoice or a NodeSequence
      if (ec.f1.present())
        // generate a NodeChoice
        ec.accept(this);
      else {
        // generate a NodeSequence
        // count the number of ExpansionUnits of each type (choice)
        eutcv.reset();
        ec.accept(eutcv);
        if (eutcv.getNbNormals() == 0) {
          final NodeToken tk = (NodeToken) seq.elementAt(0);
          Messages.warning("Empty parentheses in production '" + curProduction + "()'.",
                           tk.beginLine, tk.beginColumn);
        }
        name = genNewVarName();
        final VarInfo varInfo = inEUT3 ? new VarInfo(nodeSeq, name, "null") : new VarInfo(nodeSeq,
                                                                                          name);
        varList.add(varInfo);
        genExpSequence(ec.f0, name);
        bnfFinalActions(varInfo);
        oneNewLine(n, "genParenExpCh 2");
      }
      spc.updateSpc(-1);
      sb.append(spc.spc);
      // ")"
      seq.elementAt(2).accept(this);
      oneNewLine(n, "genParenExpCh 3");
    } else {
      // an BNF modifier is present so generate the appropriate structure
      // #3 ( &0 "+" | &1 "*" | &2 "?" )?
      final NodeChoice ch = (NodeChoice) ((NodeOptional) seq.elementAt(3)).node;
      name = genNewVarName();
      VarInfo varInfo;
      // build the list of expansion units
      final NodeList list = ec.f0.f1;
      // count the number of ExpansionUnits of each type (choice)
      eutcv.reset();
      ec.accept(eutcv);
      // print the first item first if it's a lookahead and not in a choice
      if (eutcv.getNbNormals() == 0) {
        // technically, we should only generate an error if it's not a choice,
        // but that greatly complicates things and an empty choice is probably useless
        final NodeToken tk = (NodeToken) seq.elementAt(0);
        Messages.softErr("Empty BNF expansion in production '" + curProduction + "()'.",
                         tk.beginLine, tk.beginColumn);
      } else {
        final ExpansionUnit firstExpUnit = (ExpansionUnit) list.nodes.get(0);
        if (extraVarsList == null)
          // top level additional variables
          varInfo = creNewVarInfoForMod(ch, name, true);
        else {
          // nested additional variables
          varInfo = creNewVarInfoForMod(ch, name, false);
          extraVarsList.add(varInfo);
        }
        varList.add(varInfo);
        final NodeOptional expLA = ec.f0.f0;
        if (!ec.f1.present() && expLA.present()) {
          // not in an ExpansionChoices choice and with an Expansion Lookahead
          final NodeSequence seq1 = (NodeSequence) expLA.node;
          seq1.elementAt(0).accept(this);
          seq1.elementAt(1).accept(this);
          sb.append(" ");
          seq1.elementAt(2).accept(this);
          sb.append(" ");
          seq1.elementAt(3).accept(this);
          oneNewLine(n, "genParenExpCh 4");
          sb.append(spc.spc);
          // don't print lookahead twice, so remove temporary the ec.f0.f0 node
          ec.f0.f0.node = null;
          genExpChInExpUnit(ec);
          // restore ec.f0.f0 node
          ec.f0.f0.node = expLA.node;
        } else if (!ec.f1.present() && (firstExpUnit.f0.which == 0)) {
          // not in an ExpansionChoices choice and with an ExpansionUnit Lookahead
          final NodeSequence seq1 = (NodeSequence) firstExpUnit.f0.choice;
          seq1.elementAt(0).accept(this);
          seq1.elementAt(1).accept(this);
          sb.append(" ");
          seq1.elementAt(2).accept(this);
          sb.append(" ");
          seq1.elementAt(3).accept(this);
          oneNewLine(n, "genParenExpCh 5 : ??? should we go through here ???");
          sb.append(spc.spc);
          // don't print lookahead twice, so remove it temporary from the ec.f0.f1 list of nodes
          list.nodes.get(0);
          genExpChInExpUnit(ec);
          // restore the ec.f0.f1 list of nodes
          list.nodes.add(0, firstExpUnit);
        } else {
          // in an ExpansionChoices choice or without an Expansion or ExpansionUnit Lookahead
          genExpChInExpUnit(ec);
        }
        sb.append(addNodeToParent(name, parentVar.getName()));
        bnfFinalActions(varInfo);
      }
      oneNewLine(n, "genParenExpCh 6");
      spc.updateSpc(-1);
      sb.append(spc.spc);
      // ")"
      seq.elementAt(2).accept(this);
      // "+" or "*" or "?"
      ch.choice.accept(this);
      oneNewLine(n, "genParenExpCh 7");
      if (ch.which != 2) {
        //        // temporary displays : production;node;size;normals
        //        sb.append(spc.spc);
        //        sb.append("{ System.out.println(\"").append(curProduction).append(";").append(name)
        //          .append(";\" + ").append(name).append(".nodes.size() + \";\" + ")
        //          .append(eutc.getNumNormals()).append("); }");
        //        oneNewLine(n);
        sb.append(spc.spc);
        sb.append("{ ").append(name).append(".nodes.trimToSize(); }");
        oneNewLine(n, "genParenExpCh 8");
      }
    }
    sb.append(spc.spc);
  }

  /**
   * Common code for generating the ExpansionChoices in choices 2, 3 and 5 of ExpansionUnit.
   * 
   * @param n - the node to process
   */
  void genExpChInExpUnit(final ExpansionChoices n) {
    // put apart "main" buffer, create a new temporary buffer to generate a list of extra variables nested into
    final StringBuilder mainSB = sb;
    final StringBuilder tempSB = new StringBuilder(512);
    sb = jccpv.sb = tempSB;
    // put apart "extraVarsList", create a new "tempExtraVarsList" to generate a new list
    final List<VarInfo> tempExtraVarsList = extraVarsList;
    extraVarsList = new ArrayList<VarInfo>();
    // new buffer will be fed by this ExpansionChoices accept
    ++bnfLvl;
    n.accept(this);
    --bnfLvl;
    if (extraVarsList.size() > 0) {
      // we have nested extra variables, generate them in the "main" buffer
      for (final Iterator<VarInfo> e = extraVarsList.iterator(); e.hasNext();) {
        genNewNodeOptOrListOrListOptVarDecl(mainSB, e.next());
        oneNewLine(n, "K", mainSB);
        mainSB.append(spc.spc);
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
  static void genNewNodeOptOrListOrListOptVarDecl(final StringBuilder aSb, final VarInfo aVarInfo) {
    aSb.append("{ ").append(aVarInfo.getName()).append(" = new ").append(aVarInfo.getType())
       .append("(); }");
  }

  /**
   * Creates a new VarInfo object (with the appropriate node type) for a given BNF modifier.
   * 
   * @param modifier - the modifier (which directs the node type)
   * @param varName - the variable name to store
   * @param initialize - the need to initialize flag
   * @return the new VarInfo object
   */
  static VarInfo creNewVarInfoForMod(final NodeChoice modifier, final String varName,
                                     final boolean initialize) {
    if (initialize) {
      if (modifier.which == 0) // "+"
        return new VarInfo(nodeList, varName, "new ".concat(nodeList).concat("()"));
      else if (modifier.which == 1) // "*"
        return new VarInfo(nodeListOpt, varName, "new ".concat(nodeListOpt).concat("()"));
      else if (modifier.which == 2) // "?"
        return new VarInfo(nodeOpt, varName, "new ".concat(nodeOpt).concat("()"));
      else {
        final String msg = "Illegal EBNF modifier: '" + modifier.choice.toString() + "'.";
        Messages.hardErr(msg);
        throw new InternalError(msg);
      }
    } else {
      if (modifier.which == 0) // "+"
        return new VarInfo(nodeList, varName);
      else if (modifier.which == 1) // "*"
        return new VarInfo(nodeListOpt, varName);
      else if (modifier.which == 2) // "?"
        return new VarInfo(nodeOpt, varName);
      else {
        final String msg = "Illegal EBNF modifier: '" + modifier.choice.toString() + "'.";
        Messages.hardErr(msg);
        throw new InternalError(msg);
      }
    }
  }

  /**
   * Creates a new VarInfo object for a NodeOptional node.
   * 
   * @param varName - the variable name to store
   * @param initializer - the initializer presence flag
   * @return the new VarInfo object
   */
  static VarInfo creNewVarInfoForBracket(final String varName, final boolean initializer) {
    if (initializer) {
      return new VarInfo(nodeOpt, varName, "new ".concat(nodeOpt).concat("()"));
    } else {
      return new VarInfo(nodeOpt, varName);
    }
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
    // find if the node must be created
    boolean creThisNode = createRENode;
    if (createRENode && n.f0.which == 2) {
      // %2 #0 "<" #1 IdentifierAsString() #2 ">"
      final NodeSequence seq1 = (NodeSequence) n.f0.choice;
      // create the node only if not requested not to do so
      final String ident = ((IdentifierAsString) seq1.elementAt(1)).f0.tokenImage;
      final String val = gdbv.getTokenHT().get(ident);
      if (DONT_CREATE.equals(val)) {
        creThisNode = false;
      }
    }
    String nodeName = null;
    VarInfo nodeTokenInfo = null;
    // if the node must be created, create the variable which will be inserted after the specials
    // down further in the tree
    if (creThisNode) {
      nodeName = genNewVarName();
      reTokenName = genNewVarName();
      nodeTokenInfo = new VarInfo(nodeToken, nodeName);
      final VarInfo tokenNameInfo = new VarInfo(jjToken, reTokenName);
      varList.add(nodeTokenInfo);
      varList.add(tokenNameInfo);
      jccpv.gvaStr = reTokenName + " = ";
    }
    if (n.f0.which == 0) {
      // %0 StringLiteral()
      n.f0.choice.accept(jccpv);
      oneNewLine(n, "a");
      if (creThisNode) {
        sb.append(spc.spc);
        sb.append("{ ").append(nodeName).append(" = JTBToolkit.makeNodeToken(").append(reTokenName)
          .append("); }");
        oneNewLine(n, "b");
      }
    } else if (n.f0.which == 1) {
      // %1 #0 "<" #1 [ $0 [ "#" ] $1 IdentifierAsString() $2 ":" ] #2 ComplexRegularExpressionChoices() #3 ">"
      final NodeSequence seq = (NodeSequence) n.f0.choice;
      // #0 "<"
      seq.elementAt(0).accept(jccpv);
      // opt -> #1 [ $0 [ "#" ] $1 IdentifierAsString() $2 ":" ]
      final NodeOptional opt = (NodeOptional) seq.elementAt(1);
      if (opt.present()) {
        final NodeSequence seq1 = (NodeSequence) opt.node;
        if (((NodeOptional) seq1.elementAt(0)).present())
          // $0 "#"
          seq1.elementAt(0).accept(jccpv);
        // $1 IdentifierAsString()
        seq1.elementAt(1).accept(jccpv);
        sb.append(" ");
        // $2 ":"
        seq1.elementAt(2).accept(jccpv);
        sb.append(" ");
      }
      // #2 ComplexRegularExpressionChoices()
      seq.elementAt(2).accept(jccpv);
      // #3 ">"
      seq.elementAt(3).accept(jccpv);
      sb.append(" ");
      oneNewLine(n, "c");
      if (creThisNode) {
        sb.append(spc.spc);
        sb.append("{ ").append(nodeName).append(" = JTBToolkit.makeNodeToken(").append(reTokenName)
          .append("); }");
        oneNewLine(n, "d");
      }
    } else if (n.f0.which == 2) {
      // %2 #0 "<" #1 IdentifierAsString() #2 ">"
      final NodeSequence seq1 = (NodeSequence) n.f0.choice;
      // print the RegularExpression in all cases
      // #0 "<"
      seq1.elementAt(0).accept(jccpv);
      sb.append(" ");
      // #1 IdentifierAsString()
      seq1.elementAt(1).accept(jccpv);
      sb.append(" ");
      // #2 ">"
      seq1.elementAt(2).accept(jccpv);
      oneNewLine(n, "e");
      if (creThisNode) {
        sb.append(spc.spc);
        sb.append("{ ").append(nodeName).append(" = JTBToolkit.makeNodeToken(").append(reTokenName)
          .append("); }");
        oneNewLine(n, "f");
      }
    } else {
      // %3 #0 "<" #1 "EOF" #2 ">"
      if (creThisNode) {
        sb.append(reTokenName).append(" = ");
        jccpv.gvaStr = null;
      }
      sb.append("< EOF >");
      oneNewLine(n, "eof");
      sb.append(spc.spc);
      sb.append("{");
      oneNewLine(n, "g");
      spc.updateSpc(+1);
      sb.append(spc.spc);
      sb.append(reTokenName).append(".beginColumn++;");
      oneNewLine(n, "h");
      sb.append(spc.spc);
      sb.append(reTokenName).append(".endColumn++;");
      oneNewLine(n, "i");
      sb.append(spc.spc);
      sb.append("{ ").append(nodeName).append(" = JTBToolkit.makeNodeToken(").append(reTokenName)
        .append("); }");
      oneNewLine(n, "j");
      spc.updateSpc(-1);
      sb.append(spc.spc);
      sb.append("}");
      oneNewLine(n, "k");
    }
    sb.append(spc.spc);
    if (creThisNode)
      bnfFinalActions(nodeTokenInfo);
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
    // f0 -> "{"
    n.f0.accept(this);
    // f1 -> ( BlockStatement() )*
    if (n.f1.present()) {
      spc.updateSpc(+1);
      oneNewLine(n, "x");
      sb.append(spc.spc);
      for (final Iterator<INode> e = n.f1.elements(); e.hasNext();) {
        // BlockStatement()
        e.next().accept(this);
        if (e.hasNext()) {
          oneNewLine(n, "y");
          sb.append(spc.spc);
        }
      }
      spc.updateSpc(-1);
      oneNewLine(n, "z");
      sb.append(spc.spc);
    }
    //  f2 -> "}"
    n.f2.accept(this);
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
   * 
   * @param n - the node to visit
   */
  @Override
  public void visit(final LocalVariableDeclaration n) {
    // f0 -> VariableModifiers()
    n.f0.accept(this);
    // VariableModifiers print the last space if not empty
    // f1 -> Type()
    n.f1.accept(this);
    sb.append(" ");
    // f2 -> VariableDeclarator()
    sb.append(genJavaBranch(n.f2));
    // f3 -> ( #0 "," #1 VariableDeclarator() )*
    if (n.f3.present()) {
      sb.append(genJavaBranch(n.f3));
    }
  }

  /**
   * Memorizes the ResultType specials and the ResultType. Walks down the tree to find the first
   * token.
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
   * @param ty - the node to process
   */
  void getTypeSpecials(final Type ty) {
    NodeToken tk;
    // Type(
    final NodeChoice ch = ty.f0;
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
    typeSpecials = tk.getSpecials(spc.spc);
    type = tk.tokenImage;
    return;
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
    if (n.f0.which == 1 || n.f0.which == 3 || n.f0.which == 4 || n.f0.which == 10 ||
        n.f0.which == 11 || n.f0.which == 13) {
      // all statements which do not lead to a ReturnStatement()
      sb.append(genJavaBranch(n.f0.choice));
    } else {
      // others
      n.f0.choice.accept(this);
    }
  }

  /**
   * Visits the {@link TryStatement}<br>
   * f0 -> "try"<br>
   * f1 -> Block()<br>
   * f2 -> ( #0 "catch" #1 "(" #2 FormalParameter() #3 ")" #4 Block() )*<br>
   * f3 -> [ #0 "finally" #1 Block() ]<br>
   * 
   * @param n - the node to visit
   */
  @Override
  public void visit(final TryStatement n) {
    // f0 -> "try"
    n.f0.accept(this);
    sb.append(" ");
    // f1 -> Block()
    n.f1.accept(this);
    // f2 -> ( #0 "catch" #1 "(" #2 FormalParameter() #3 ")" #4 Block() )*
    for (final Iterator<INode> e = n.f2.elements(); e.hasNext();) {
      final NodeSequence seq = (NodeSequence) e.next();
      oneNewLine(n);
      sb.append(spc.spc);
      // #0 "catch"
      seq.elementAt(0).accept(this);
      sb.append(" ");
      // #1 "("
      seq.elementAt(1).accept(this);
      // #2 FormalParameter()
      sb.append(genJavaBranch(seq.elementAt(2)));
      // #3 ")"
      seq.elementAt(3).accept(this);
      sb.append(" ");
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
      sb.append(" ");
      // #1 Block(
      seq.elementAt(1).accept(this);
    }
  }

  /**
   * Visits the {@link LabeledStatement}<br>
   * f0 -> < IDENTIFIER ><br>
   * f1 -> ":"<br>
   * f2 -> Statement() <br>
   * 
   * @param n - the node to visit
   */
  @Override
  public void visit(final LabeledStatement n) {
    //  f0 -> < IDENTIFIER >
    n.f0.accept(this);
    //  f1 -> ":"
    n.f1.accept(this);
    oneNewLine(n);
    sb.append(spc.spc);
    // f2 -> Statement()
    n.f2.accept(this);
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
   * 
   * @param n - the node to visit
   */
  @Override
  public void visit(final SwitchStatement n) {
    // f0 -> "switch"
    n.f0.accept(this);
    sb.append(" ");
    // f1 -> "("
    n.f1.accept(this);
    // f2 -> Expression()
    sb.append(genJavaBranch(n.f2));
    // f3 -> ")"
    n.f3.accept(this);
    sb.append(" ");
    // f4 -> "{"
    n.f4.accept(this);
    spc.updateSpc(+1);
    oneNewLine(n);
    sb.append(spc.spc);
    // f5 -> ( #0 SwitchLabel() #1 ( BlockStatement() )* )*
    for (final Iterator<INode> e = n.f5.elements(); e.hasNext();) {
      final NodeSequence seq = (NodeSequence) e.next();
      // #0 SwitchLabel()
      sb.append(genJavaBranch(seq.elementAt(0)));
      spc.updateSpc(+1);
      // #1 ( BlockStatement() )* )*
      final NodeListOptional nlo = (NodeListOptional) seq.elementAt(1);
      if ((nlo).present()) {
        if (nlo.size() == 1)
          sb.append(" ");
        else {
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
   * Visits a {@link IfStatement} node, whose children are the following :
   * <p>
   * f0 -> "if"<br>
   * f1 -> "("<br>
   * f2 -> Expression()<br>
   * f3 -> ")"<br>
   * f4 -> Statement()<br>
   * f5 -> [ #0 "else" #1 Statement() ]<br>
   * 
   * @param n - the node to visit
   */
  @Override
  public void visit(final IfStatement n) {
    // f0 -> "if"
    n.f0.accept(this);
    sb.append(" ");
    // f1 -> "("
    n.f1.accept(this);
    // f2 -> Expression()
    sb.append(genJavaBranch(n.f2));
    // f3 -> ")"
    n.f3.accept(this);
    // f4 -> Statement()
    if (n.f4.f0.which != 2) { // if Statement() not a Block
      spc.updateSpc(+1);
      oneNewLine(n);
      sb.append(spc.spc);
    } else { // if Statement() is a Block
      sb.append(" ");
    }
    n.f4.accept(this);
    if (n.f4.f0.which != 2) // if Statement() not a Block
      spc.updateSpc(-1);
    // f5 -> [ #0 "else" #1 Statement() ]
    if (n.f5.present()) {
      if (n.f4.f0.which != 2) {// if Statement() not a Block
        oneNewLine(n);
        sb.append(spc.spc);
      } else { // if Statement() is a Block
        sb.append(" ");
      }
      // #0 "else"
      ((NodeSequence) n.f5.node).elementAt(0).accept(this);
      // #1 Statement()
      final Statement st = (Statement) ((NodeSequence) n.f5.node).elementAt(1);
      if (st.f0.which != 2) {
        // else Statement() is not a Block()
        spc.updateSpc(+1);
        oneNewLine(n);
        sb.append(spc.spc);
        // #1 Statement()
        st.f0.choice.accept(this);
        spc.updateSpc(-1);
        oneNewLine(n);
        sb.append(spc.spc);
      } else {
        // else Statement() is a Block()
        sb.append(" ");
        // #1 Statement()
        st.f0.choice.accept(this);
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
   * 
   * @param n - the node to visit
   */
  @Override
  public void visit(final WhileStatement n) {
    // f0 -> "while"
    n.f0.accept(this);
    sb.append(" ");
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
   * 
   * @param n - the node to visit
   */
  @Override
  public void visit(final DoStatement n) {
    // f0 -> "do"
    n.f0.accept(this);
    // f1 -> Statement()
    genStatement(n.f1);
    // f2 -> "while"
    n.f2.accept(this);
    sb.append(" ");
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
   * .. .. | %1 #0 [ ForInit() ] #1 ";"<br>
   * .. .. . .. #2 [ Expression() ] #3 ";"<br>
   * .. .. . .. #4 [ ForUpdate() ] )<br>
   * f3 -> ")"<br>
   * f4 -> Statement()<br>
   * 
   * @param n - the node to visit
   */
  @Override
  public void visit(final ForStatement n) {
    // f0 -> "for"
    n.f0.accept(this);
    sb.append(" ");
    // f1 -> "("
    n.f1.accept(this);
    // f2 -> ( %0 #0 VariableModifiers() #1 Type() #2 < IDENTIFIER > #3 ":" #4 Expression() | %1 #0 [ ForInit() ] #1 ";" #2 [ Expression() ] #3 ";" #4 [ ForUpdate() ] )
    final NodeSequence seq = (NodeSequence) n.f2.choice;
    if (n.f2.which == 0) {
      // %0 #0 VariableModifiers() #1 Type() #2 < IDENTIFIER > #3 ":" #4 Expression()
      // #0 VariableModifiers print the last space if not empty
      sb.append(genJavaBranch(seq.elementAt(0)));
      // #1 Type()
      sb.append(genJavaBranch(seq.elementAt(1)));
      sb.append(" ");
      // #2 < IDENTIFIER >
      seq.elementAt(2).accept(this);
      sb.append(" ");
      // #4 Expression()
      sb.append(genJavaBranch(seq.elementAt(3)));
    } else {
      NodeOptional opt;
      // %1 #0 [ ForInit() ] #1 ";" #2 [ Expression() ] #3 ";" #4 [ ForUpdate() ]
      // #0 [ ForInit() ]
      opt = (NodeOptional) seq.elementAt(0);
      if (opt.present())
        sb.append(genJavaBranch(opt.node));
      // #1 ";"
      seq.elementAt(1).accept(this);
      sb.append(" ");
      // #2 [ Expression() ]
      opt = (NodeOptional) seq.elementAt(2);
      if (opt.present())
        sb.append(genJavaBranch(opt.node));
      // #3 ";"
      seq.elementAt(3).accept(this);
      sb.append(" ");
      // #4 [ ForUpdate() ]
      opt = (NodeOptional) seq.elementAt(4);
      if (opt.present())
        sb.append(genJavaBranch(opt.node));
    }
    // f3 -> ")"
    n.f3.accept(this);
    // f4 -> Statement()
    genStatement(n.f4);
  }

  /**
   * Generates the source code corresponding to a {@link Statement} node, whose children are the
   * following :
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
   * @param n - the Statement node
   */
  void genStatement(final Statement n) {
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
      sb.append(" ");
      // Statement()
      n.accept(this);
      sb.append(" ");
    }
  }

  /**
   * Visits a {@link ReturnStatement} node, whose children are the following :
   * <p>
   * f0 -> "return"<br>
   * f1 -> [ Expression() ]<br>
   * f2 -> ";"<br>
   * 
   * @param n - the node to visit
   */
  @Override
  public void visit(final ReturnStatement n) {
    if (resultType == null) {
      // keep return statement (don't know any case like 'resultType == null' however)
      // f0 -> "return"
      n.f0.accept(this);
      if (n.f1.present()) {
        sb.append(" ");
        // f1 -> [ Expression() ]
        sb.append(genJavaBranch(n.f1));
      }
      // f2 -> ";"
      n.f2.accept(this);
    } else {
      // change return statement only if something to return,
      // otherwise do not generate anything (for example for void productions)
      if (n.f1.present()) {
        sb.append(jtbRtPrefix).append(curProduction).append(" = ");
        // f1 -> [ Expression() ]
        sb.append(genJavaBranch(n.f1));
        // f2 -> ";"
        n.f2.accept(this);
      }
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
   * 
   * @param n - the node to visit
   */
  @Override
  public void visit(final SynchronizedStatement n) {
    // f0 -> "synchronized"
    n.f0.accept(this);
    sb.append(" ");
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
   * The {@link ExpansionChoicesLineNumber} visitor finds the line number of the first token of a
   * production under an {@link ExpansionChoices}.
   */
  class ExpansionChoicesLineNumber extends DepthFirstVoidVisitor {

    /**
     * Resets the global variable.
     */
    public void reset() {
      lnft = 0;
      cnft = 0;
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
      // result should be always found after visiting f0
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
      // f0 -> ( #0 "LOOKAHEAD" #1 "(" #2 LocalLookahead() #3 ")" )?
      if (n.f0.present()) {
        final NodeSequence seq = (NodeSequence) n.f0.node;
        lnft = ((NodeToken) seq.elementAt(0)).beginLine;
        cnft = ((NodeToken) seq.elementAt(0)).beginColumn;
      } else
        // f1 -> ( ExpansionUnit() )+
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
      NodeToken tk;
      switch (n.f0.which) {
        case 0:
          // %0 #0 "LOOKAHEAD" #1 "(" #2 LocalLookahead() #3 ")"
          seq = (NodeSequence) n.f0.choice;
          tk = (NodeToken) seq.elementAt(0);
          lnft = tk.beginLine;
          cnft = tk.beginColumn;
          return;

        case 1:
          // %1 Block()
          n.f0.choice.accept(this);
          return;

        case 2:
          // %2 #0 "[" #1 ExpansionChoices() #2 "]"
          seq = (NodeSequence) n.f0.choice;
          tk = (NodeToken) seq.elementAt(0);
          lnft = tk.beginLine;
          cnft = tk.beginColumn;
          return;

        case 3:
          // %3 ExpansionUnitTCF()
          n.f0.choice.accept(this);
          return;

        case 4:
          // %4 #0 [ $0 PrimaryExpression() $1 "=" ]
          // .. #1 ( &0 $0 IdentifierAsString() $1 Arguments()
          // .. .. | &1 $0 RegularExpression()
          // .. .. . .. $1 [ ?0 "." ?1 < IDENTIFIER > ]
          // .. .. . .. $2 [ "!" ] )
          seq = (NodeSequence) n.f0.choice;
          //          final NodeOptional opt1 = (NodeOptional) seq.elementAt(0);
          //          if (opt1.present()) {
          //            // $0 PrimaryExpression() $1 "="
          //            // here we take a shortcut : we do not implement and go down PrimaryExpression
          //            tk = (NodeToken) ((NodeSequence) opt1.node).elementAt(1);
          //            lnft = tk.beginLine;
          //            cnft = tk.beginColumn;
          //            return;
          //          }
          final NodeChoice ch = (NodeChoice) seq.elementAt(1);
          final NodeSequence seq1 = (NodeSequence) ch.choice;
          if (ch.which == 0) {
            // &0 $0 IdentifierAsString() $1 Arguments()
            tk = ((IdentifierAsString) seq1.elementAt(0)).f0;
            lnft = tk.beginLine;
            cnft = tk.beginColumn;
          } else {
            // &1 $0 RegularExpression() $1 [ ?0 "." ?1 < IDENTIFIER > ] )
            seq1.elementAt(0).accept(this);
          }
          return;

        case 5:
          // %5 #0 "(" #1 ExpansionChoices() #2 ")"
          // .. #3 ( &0 "+" | &1 "*" | &2 "?" )?
          seq = (NodeSequence) n.f0.choice;
          tk = (NodeToken) seq.elementAt(0);
          lnft = tk.beginLine;
          cnft = tk.beginColumn;
          return;

        default:
          final String msg = "Invalid n.f0.which = " + String.valueOf(n.f0.which);
          Messages.hardErr(msg);
          throw new InternalError(msg);

      }
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
      // f0 -> "{"
      lnft = n.f0.beginLine;
      cnft = n.f0.beginColumn;
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
      // f0 -> "try"
      lnft = n.f0.beginLine;
      cnft = n.f0.beginColumn;
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
      if (n.f0.which == 0) {
        // %0 StringLiteral()
        lnft = ((StringLiteral) n.f0.choice).f0.beginLine;
        cnft = ((StringLiteral) n.f0.choice).f0.beginColumn;
      } else {
        // %1 #0 "<" #1 [ $0 [ "#" ] $1 IdentifierAsString() $2 ":" ] #2 ComplexRegularExpressionChoices() #3 ">"
        // %2 #0 "<" #1 IdentifierAsString() #2 ">"
        // %3 #0 "<" #1 "EOF" #2 ">"
        final NodeSequence seq = (NodeSequence) n.f0.choice;
        lnft = ((NodeToken) seq.elementAt(0)).beginLine;
        cnft = ((NodeToken) seq.elementAt(0)).beginColumn;
      }
    }

  }

  /**
   * The {@link CompilationUnitPrinter} visitor<br>
   * determines if an import statement for the syntax tree package is needed in the grammar file,<br>
   * prints the compilation unit (with appropriate tool kit methods), inserting the import
   * statements if necessary.
   */
  class CompilationUnitPrinter extends JavaPrinter {

    /**
     * Constructor, with a given buffer and a default indentation.
     * 
     * @param aSb - the buffer to print into (will be allocated if null)
     * @param aSPC - the Spacing indentation object (will be allocated and set to a default if null)
     */
    CompilationUnitPrinter(final StringBuilder aSb, final Spacing aSPC) {
      super(aSb, aSPC);
    }

    /*
     * Convenience methods
     */

    /**
     * Prints into the current buffer a node class comment and a new line.
     * 
     * @param n - the node for the node class comment
     */
    @Override
    void oneNewLine(final INode n) {
      sb.append(nodeClassComment(n)).append(LS);
    }

    /**
     * Prints into the current buffer a node class comment, an extra given comment, and a new line.
     * 
     * @param n - the node for the node class comment
     * @param str - the extra comment
     */
    @Override
    void oneNewLine(final INode n, final String str) {
      sb.append(nodeClassComment(n, str)).append(LS);
    }

    /**
     * Prints twice into the current buffer a node class comment and a new line.
     * 
     * @param n - the node for the node class comment
     */
    @Override
    void twoNewLines(final INode n) {
      oneNewLine(n);
      oneNewLine(n);
    }

    /**
     * Returns a node class comment (a //!! followed by the node class short name if global flag
     * set, nothing otherwise).
     * 
     * @param n - the node for the node class comment
     * @return the node class comment
     */
    String nodeClassComment(final INode n) {
      if (DEBUG_CLASS_COMMENTS) {
        final String s = n.toString();
        final int b = s.lastIndexOf('.') + 1;
        final int e = s.indexOf('@');
        if (b == -1 || e == -1)
          return " //!! " + s;
        else
          return " //!! " + s.substring(b, e);
      } else
        return "";
    }

    /**
     * Returns a node class comment with an extra comment (a //!! followed by the node class short
     * name plus the extra comment if global flag set, nothing otherwise).
     * 
     * @param n - the node for the node class comment
     * @param str - the extra comment
     * @return the node class comment
     */
    String nodeClassComment(final INode n, final String str) {
      if (DEBUG_CLASS_COMMENTS)
        return nodeClassComment(n).concat(" ").concat(str);
      else
        return "";
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
     * 
     * @param n - the node to visit
     */
    @Override
    public void visit(final CompilationUnit n) {
      // f0 -> [ PackageDeclaration() ]
      if (n.f0.present()) {
        n.f0.node.accept(this);
        twoNewLines(n);
      }
      // f1 -> ( ImportDeclaration() )*
      printImports(n.f1);
      twoNewLines(n);
      // f2 -> ( TypeDeclaration() )*
      if (n.f2.present()) {
        for (final Iterator<INode> e = n.f2.elements(); e.hasNext();) {
          e.next().accept(this);
          if (e.hasNext()) {
            twoNewLines(n);
          }
        }
      }
      // builds specials into tree
      if (!keepSpecialTokens) {
        sb.append(LS);
        sb.append("class JTBToolkit {").append(LS);
        sb.append(LS);
        sb.append("  static NodeToken makeNodeToken(final Token t) {").append(LS);
        sb.append("    return new NodeToken(t.image.intern(), t.kind, t.beginLine, t.beginColumn, t.endLine, t.endColumn);")
          .append(LS);
        sb.append("  }").append(LS);
        sb.append("}");
        oneNewLine(n);
      } else {
        sb.append(LS);
        sb.append("class JTBToolkit {").append(LS);
        sb.append(LS);
        sb.append("  static NodeToken makeNodeToken(final Token tok) {").append(LS);
        sb.append("    final NodeToken node = new NodeToken(tok.image.intern(), tok.kind, tok.beginLine, tok.beginColumn, tok.endLine, tok.endColumn);")
          .append(LS);
        sb.append("    if (tok.specialToken == null)").append(LS);
        sb.append("      return node;").append(LS);
        sb.append("    Token t = tok;").append(LS);
        sb.append("    int nbt = 0;").append(LS);
        sb.append("    while (t.specialToken != null) {").append(LS);
        sb.append("      t = t.specialToken;").append(LS);
        sb.append("      nbt++;").append(LS);
        sb.append("    }").append(LS);
        sb.append("    final java.util.ArrayList<NodeToken> temp = new java.util.ArrayList<NodeToken>(nbt);")
          .append(LS);
        sb.append("    t = tok;").append(LS);
        sb.append("    while (t.specialToken != null) {").append(LS);
        sb.append("      t = t.specialToken;").append(LS);
        sb.append("      temp.add(new NodeToken(t.image.intern(), t.kind, t.beginLine, t.beginColumn, t.endLine, t.endColumn));")
          .append(LS);
        sb.append("    }").append(LS);
        sb.append("    for (int i = nbt - 1; i >= 0; --i)").append(LS);
        sb.append("      node.addSpecial(temp.get(i));").append(LS);
        sb.append("    // node.trimSpecials();").append(LS);
        sb.append("    return node;").append(LS);
        sb.append("  }").append(LS);
        sb.append("}");
        oneNewLine(n);
      }
    }

    /**
     * Visits the {@link ImportDeclaration}<br>
     * f0 -> "import"<br>
     * f1 -> [ "static" ]<br>
     * f2 -> Name(null)<br>
     * f3 -> [ "." "*" ]<br>
     * f4 -> ";"<br>
     * 
     * @param n - the node to process
     */
    void printImports(final NodeListOptional n) {
      if ("".equals(nodesPackageName))
        return;
      boolean foundTreeImport = false;
      final StringBuilder mainSB = sb;
      final StringBuilder newSB = new StringBuilder(128);
      sb = newSB;
      final String npns = nodesPackageName + ".*;";
      for (final Iterator<?> e = n.elements(); e.hasNext();) {
        final ImportDeclaration dec = (ImportDeclaration) e.next();

        newSB.setLength(0);
        dec.accept(this);
        final String s = newSB.toString();
        mainSB.append(s).append(nodeClassComment(n, " Y")).append(LS);

        if (s.equals(npns))
          foundTreeImport = true;
      }
      sb = mainSB;
      if (!foundTreeImport) {
        sb.append("import ").append(nodesPackageName).append(".*;");
        oneNewLine(n, "Z");
      }
    }

    /**
     * Visits a {@link ImportDeclaration} node, whose children are the following :
     * <p>
     * f0 -> "import"<br>
     * f1 -> [ "static" ]<br>
     * f2 -> Name()<br>
     * f3 -> [ #0 "." #1 "*" ]<br>
     * f4 -> ";"<br>
     * 
     * @param n - the node to visit
     */
    @Override
    public void visit(final ImportDeclaration n) {
      // f0 -> "import"
      n.f0.accept(this);
      sb.append(" ");
      // f1 -> [ "static" ]
      if (n.f1.present()) {
        n.f1.accept(this);
        sb.append(" ");
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
     * 
     * @param n - the node to visit
     */
    @Override
    public void visit(final ClassOrInterfaceBody n) {
      // f0 -> "{"
      n.f0.accept(this);
      // add return variables declarations
      final int rvds = gdbv.getRetVarDecl().size();
      if (rvds > 0) {
        spc.updateSpc(+1);
        twoNewLines(n);
        sb.append(spc.spc);
        sb.append("/* --- JTB generated return variables declarations --- */");
        twoNewLines(n);
        sb.append(spc.spc);
        for (int i = 0; i < rvds; i++) {
          // comment
          sb.append(gdbv.getRetVarDecl().get(i));
          oneNewLine(n, "b");
          sb.append(spc.spc);
          // declaration
          sb.append(gdbv.getRetVarDecl().get(++i));
          if (i < rvds - 2) {
            twoNewLines(n);
            sb.append(spc.spc);
          }
        }
        gdbv.getRetVarDecl().clear();
        spc.updateSpc(-1);
      }
      // f1 -> ( ClassOrInterfaceBodyDeclaration() )*
      if (n.f1.present()) {
        spc.updateSpc(+1);
        twoNewLines(n);
        sb.append(spc.spc);
        sb.append("/* --- User code --- */");
        twoNewLines(n);
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
     * Visits a {@link IdentifierAsString} node, whose children are the following :
     * <p>
     * f0 -> < IDENTIFIER ><br>
     * 
     * @param n - the node to visit
     */
    @Override
    public void visit(final IdentifierAsString n) {
      n.f0.accept(this);
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
      n.f0.accept(this);
    }

  }
}

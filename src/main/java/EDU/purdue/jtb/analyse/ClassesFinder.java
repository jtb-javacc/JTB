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
 * Research Foundation of Purdue University. All rights reserved. Redistribution and use in source and binary
 * forms are permitted provided that this entire copyright notice is duplicated in all such copies, and that
 * any documentation, announcements, and other materials related to such distribution and use acknowledge that
 * the software was developed at Purdue University, West Lafayette, Indiana by Kevin Tao and Jens Palsberg. No
 * charge may be made for copies, derivations, or distributions of this material without the express written
 * consent of the copyright holder. Neither the name of the University nor the name of the author may be used
 * to endorse or promote products derived from this material without specific prior written permission. THIS
 * SOFTWARE IS PROVIDED ``AS IS'' AND WITHOUT ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, WITHOUT
 * LIMITATION, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR ANY PARTICULAR PURPOSE.
 */
package EDU.purdue.jtb.analyse;

import static EDU.purdue.jtb.analyse.GlobalDataBuilder.DONT_CREATE;
import static EDU.purdue.jtb.common.Constants.nodeChoice;
import static EDU.purdue.jtb.common.Constants.nodeList;
import static EDU.purdue.jtb.common.Constants.nodeListOptional;
import static EDU.purdue.jtb.common.Constants.nodeOptional;
import static EDU.purdue.jtb.common.Constants.nodeSequence;
import static EDU.purdue.jtb.common.Constants.nodeToken;
import static EDU.purdue.jtb.parser.syntaxtree.NodeConstants.JTB_SIG_BNFPRODUCTION;
import static EDU.purdue.jtb.parser.syntaxtree.NodeConstants.JTB_SIG_EXPANSION;
import static EDU.purdue.jtb.parser.syntaxtree.NodeConstants.JTB_SIG_EXPANSIONCHOICES;
import static EDU.purdue.jtb.parser.syntaxtree.NodeConstants.JTB_SIG_EXPANSIONUNIT;
import static EDU.purdue.jtb.parser.syntaxtree.NodeConstants.JTB_SIG_EXPANSIONUNITTCF;
import static EDU.purdue.jtb.parser.syntaxtree.NodeConstants.JTB_SIG_IDENTIFIERASSTRING;
import static EDU.purdue.jtb.parser.syntaxtree.NodeConstants.JTB_SIG_JAVACCINPUT;
import static EDU.purdue.jtb.parser.syntaxtree.NodeConstants.JTB_SIG_JAVACODEPRODUCTION;
import static EDU.purdue.jtb.parser.syntaxtree.NodeConstants.JTB_SIG_LOCALLOOKAHEAD;
import static EDU.purdue.jtb.parser.syntaxtree.NodeConstants.JTB_SIG_REGULAREXPRESSION;
import static EDU.purdue.jtb.parser.syntaxtree.NodeConstants.JTB_SIG_REGULAREXPRPRODUCTION;
import static EDU.purdue.jtb.parser.syntaxtree.NodeConstants.JTB_SIG_TOKENMANAGERDECLS;
import static EDU.purdue.jtb.parser.syntaxtree.NodeConstants.JTB_USER_BNFPRODUCTION;
import static EDU.purdue.jtb.parser.syntaxtree.NodeConstants.JTB_USER_EXPANSION;
import static EDU.purdue.jtb.parser.syntaxtree.NodeConstants.JTB_USER_EXPANSIONCHOICES;
import static EDU.purdue.jtb.parser.syntaxtree.NodeConstants.JTB_USER_EXPANSIONUNIT;
import static EDU.purdue.jtb.parser.syntaxtree.NodeConstants.JTB_USER_EXPANSIONUNITTCF;
import static EDU.purdue.jtb.parser.syntaxtree.NodeConstants.JTB_USER_IDENTIFIERASSTRING;
import static EDU.purdue.jtb.parser.syntaxtree.NodeConstants.JTB_USER_JAVACCINPUT;
import static EDU.purdue.jtb.parser.syntaxtree.NodeConstants.JTB_USER_JAVACODEPRODUCTION;
import static EDU.purdue.jtb.parser.syntaxtree.NodeConstants.JTB_USER_LOCALLOOKAHEAD;
import static EDU.purdue.jtb.parser.syntaxtree.NodeConstants.JTB_USER_REGULAREXPRESSION;
import static EDU.purdue.jtb.parser.syntaxtree.NodeConstants.JTB_USER_REGULAREXPRPRODUCTION;
import static EDU.purdue.jtb.parser.syntaxtree.NodeConstants.JTB_USER_TOKENMANAGERDECLS;
import java.util.ArrayList;
import java.util.List;
import EDU.purdue.jtb.common.JTBOptions;
import EDU.purdue.jtb.common.Messages;
import EDU.purdue.jtb.common.ProgrammaticError;
import EDU.purdue.jtb.common.UserClassInfo;
import EDU.purdue.jtb.parser.syntaxtree.BNFProduction;
import EDU.purdue.jtb.parser.syntaxtree.Expansion;
import EDU.purdue.jtb.parser.syntaxtree.ExpansionChoices;
import EDU.purdue.jtb.parser.syntaxtree.ExpansionUnit;
import EDU.purdue.jtb.parser.syntaxtree.ExpansionUnitTCF;
import EDU.purdue.jtb.parser.syntaxtree.INode;
import EDU.purdue.jtb.parser.syntaxtree.IdentifierAsString;
import EDU.purdue.jtb.parser.syntaxtree.JavaCCInput;
import EDU.purdue.jtb.parser.syntaxtree.JavaCodeProduction;
import EDU.purdue.jtb.parser.syntaxtree.LocalLookahead;
import EDU.purdue.jtb.parser.syntaxtree.NodeChoice;
import EDU.purdue.jtb.parser.syntaxtree.NodeOptional;
import EDU.purdue.jtb.parser.syntaxtree.NodeSequence;
import EDU.purdue.jtb.parser.Token;
import EDU.purdue.jtb.parser.syntaxtree.RegularExprProduction;
import EDU.purdue.jtb.parser.syntaxtree.RegularExpression;
import EDU.purdue.jtb.parser.syntaxtree.TokenManagerDecls;
import EDU.purdue.jtb.parser.visitor.DepthFirstVoidVisitor;
import EDU.purdue.jtb.parser.visitor.signature.NodeFieldsSignature;

/**
 * The {@link ClassesFinder} visitor creates a list of {@link UserClassInfo} objects describing every class to
 * be generated.
 * <p>
 * This visitor is supposed to be run once and not supposed to be run in parallel threads (on the same
 * grammar).
 * </p>
 * <p>
 * Programming note: we do not continue down the tree once a new field has been added to curClass, as we only
 * worry about top-level expansions.
 * </p>
 * TESTCASE some to add
 *
 * @author Marc Mazas
 * @version 1.4.0 : 05-08/2009 : MMa : adapted to JavaCC v4.2 grammar and JDK 1.5
 * @version 1.4.6 : 01/2011 : FA : add -va and -npfx and -nsfx options
 * @version 1.4.7 : 12/2011-05/2012 : MMa : fixed problem in fmtJavaNodeCode (removed in 1.5.0)<br>
 *          added bareJavaNodeCode (removed in 1.5.0) and fixed problems in {@link #visit(ExpansionUnitTCF)}
 *          <br>
 *          1.4.7 : 07/2012 : MMa : followed changes in jtbgram.jtb (IndentifierAsString())<br>
 *          1.4.7 : 09/2012 : MMa : changed some comments and removed one JavaBranchPrinter visitor ; removed
 *          printToken and regExpr as of no use ; added non node creation and the reference to
 *          theGlobalDataBuilder
 * @version 1.4.8 : 10/2012 : MMa : added JavaCodeProduction class generation ; changed BNFProduction class
 *          generation to unless not requested
 * @version 1.5.0 : 01-06/2017 : MMa : changed some iterator based for loops to enhanced for loops ; fixed
 *          processing of nodes not to be created ; added final in ExpansionUnitTCF's catch ; removed NodeTCF
 *          related code<br>
 * @version 1.5.1 : 08/2023 : MMa : editing changes for coverage analysis; changes due to the NodeToken
 *          replacement by Token
 */
public class ClassesFinder extends DepthFirstVoidVisitor {
  
  /** The {@link GlobalDataBuilder} visitor */
  private final GlobalDataBuilder   gdbv;
  /** The messages handler */
  final Messages                    mess;
  /** The global JTB options */
  private final JTBOptions          jopt;
  /** The {@link UserClassInfo} for the current generated class */
  private UserClassInfo             uci;
  /** The list of generated classes */
  private final List<UserClassInfo> uciList = new ArrayList<>();
  /** The field names generator (descriptive or not, depending on -f option) */
  private final FieldNameGenerator  fng;
  /** The global variable to pass IdentifierAsString info between methods (as they are recursive) */
  private String                    ident   = "";
  
  /**
   * Constructor.
   *
   * @param aGdbv - the {@link GlobalDataBuilder} visitor
   */
  public ClassesFinder(final GlobalDataBuilder aGdbv) {
    gdbv = aGdbv;
    jopt = aGdbv.jopt;
    mess = jopt.mess;
    fng = new FieldNameGenerator(jopt);
  }
  
  /**
   * Getter for the class list.
   *
   * @return the class list
   */
  public List<UserClassInfo> getClasses() {
    return uciList;
  }
  
  /*
   * User generated visitor methods below
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
    // f10 -> ( Production() )+
    for (final INode e : n.f10.nodes) {
      e.accept(this);
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
    // generate the class only if the node creation is requested
    if (n.f6.present()) {
      fng.reset();
      // f6 -> [ "%" ]
      final String cn = n.f3.f0.image;
      final String fcn = gdbv.getFixedName(cn);
      uci = new UserClassInfo(null, 0, cn, fcn);
      uciList.add(uci);
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
    // generate the class unless the node creation is not requested
    // f5 -> [ "!" ]
    if (!n.f5.present()) {
      fng.reset();
      final int nbFields = gdbv.getNbSubNodesTbc(n.f9);
      final String cn = n.f2.f0.image;
      final String fcn = gdbv.getFixedName(cn);
      uci = new UserClassInfo(n.f9, nbFields, cn, fcn);
      uciList.add(uci);
      // f9 -> ExpansionChoices()
      n.f9.accept(this);
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
  public void visit(@SuppressWarnings("unused") final RegularExprProduction n) {
    // Don't visit : don't want to generate NodeTokens inside RegularExpression
    // if it's visited from a RegularExprProduction
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
  @NodeFieldsSignature({
      -1566997219, JTB_SIG_TOKENMANAGERDECLS, JTB_USER_TOKENMANAGERDECLS
  })
  public void visit(@SuppressWarnings("unused") final TokenManagerDecls n) {
    // Don't visit
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
    if (!n.f1.present()) {
      // f0 -> Expansion()
      n.f0.accept(this);
    } else // f1 -> ( #0 "|" #1 Expansion() )*
    // if > 0 NodeChoice, if = 0 nothing
    if (gdbv.getNbSubNodesTbc(n) > 0) {
      uci.addField(nodeChoice, fng.genFieldName(nodeChoice));
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
  @NodeFieldsSignature({
      -2134365682, JTB_SIG_EXPANSION, JTB_USER_EXPANSION
  })
  public void visit(final Expansion n) {
    // Don't visit LocalLookahead
    // f1 -> ( ExpansionUnit() )+
    n.f1.accept(this);
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
  public void visit(@SuppressWarnings("unused") final LocalLookahead n) {
    // Don't visit
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
    NodeChoice ch;
    switch (n.f0.which) {
    case 0:
      // %0 #0 "LOOKAHEAD" #1 "(" #2 LocalLookahead() #3 ")"
      return;
    
    case 1:
      // %1 Block()
      return;
    
    case 2:
      // %2 #0 "[" #1 ExpansionChoices() #2 "]"
      final NodeSequence seq5 = (NodeSequence) n.f0.choice;
      // if > 0 NodeOptional, if = 0 nothing
      if (gdbv.getNbSubNodesTbc((ExpansionChoices) seq5.elementAt(1)) > 0) {
        uci.addField(nodeOptional, fng.genFieldName(nodeOptional));
      }
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
      ch = (NodeChoice) seq.elementAt(1);
      final NodeSequence seq1 = (NodeSequence) ch.choice;
      if (ch.which == 0) {
        // &0 $0 IdentifierAsString() $1 Arguments() $2 [ "!" ]
        // ident will be set further down the tree
        seq1.elementAt(0).accept(this);
        // add field only if ...
        final boolean createField = !gdbv.getNotTbcNodesHM().containsKey(ident);
        if (createField && !((NodeOptional) seq1.elementAt(2)).present()) {
          uci.addField(ident, gdbv.getFixedName(ident), fng.genFieldName(ident));
        }
      } else // &1 $0 RegularExpression() $1 [ ?0 "." ?1 < IDENTIFIER > ] $2 [ "!" ]
      // add field only if node creation is not requested not to be generated
      if (!((NodeOptional) seq1.elementAt(2)).present()) {
        seq1.elementAt(0).accept(this);
      }
      return;
    
    case 5:
      // %5 #0 "(" #1 ExpansionChoices() #2 ")"
      // .. #3 ( &0 "+" | &1 "*" | &2 "?" )?
      seq = (NodeSequence) n.f0.choice;
      final NodeOptional opt = (NodeOptional) seq.elementAt(3);
      final int nbSubNodesTbc = gdbv.getNbSubNodesTbc((ExpansionChoices) seq.elementAt(1));
      if (opt.present()) {
        ch = (NodeChoice) opt.node;
        final String type = getNodeNameForMod(ch.which);
        // if > 0 node of type, if = 0 nothing
        if (nbSubNodesTbc > 0) {
          uci.addField(type, gdbv.getFixedName(type), fng.genFieldName(type));
        }
      } else if (((ExpansionChoices) seq.elementAt(1)).f1.present()) {
        // f1 -> ( "|" Expansion() )*
        // id > 0 NodeChoice, if = 0 nothing
        if (nbSubNodesTbc > 0) {
          uci.addField(nodeChoice, fng.genFieldName(nodeChoice));
        }
      } else // f0 -> Expansion()
      // if > 0 NodeSequence, if = 0 nothing
      if (nbSubNodesTbc > 0) {
        uci.addField(nodeSequence, fng.genFieldName(nodeSequence));
      }
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
  @NodeFieldsSignature({
      1601707097, JTB_SIG_EXPANSIONUNITTCF, JTB_USER_EXPANSIONUNITTCF
  })
  public void visit(final ExpansionUnitTCF n) {
    // if > 0 all the ExpansionChoices, if = 0 nothing
    if (gdbv.getNbSubNodesTbc(n.f2) > 0) {
      // f2 -> ExpansionChoices()
      n.f2.accept(this);
    }
  }
  
  /**
   * Returns the base node name for a given modifier.
   *
   * @param mod - the modifier
   * @return the corresponding base node name
   */
  private static String getNodeNameForMod(final int mod) {
    if (mod == 0) {
      return nodeList;
    } else if (mod == 1) {
      return nodeListOptional;
    } else if (mod == 2) {
      return nodeOptional;
    } else {
      final String msg = "Illegal EBNF modifier in " + "ExpansionUnit: mod = " + mod;
      Messages.hardErr(msg);
      throw new ProgrammaticError(msg);
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
    boolean addNodeToken = true;
    switch (n.f0.which) {
    case 0: // %0 StringLiteral()
      break;
    
    case 1: // %1 #0 "<" #1 [ $0 [ "#" ] $1 IdentifierAsString() $2 ":" ] #2 ComplexRegularExpressionChoices()
            // #3 ">"
      break;
    
    case 2: // %2 #0 "<" #1 IdentifierAsString() #2 ">"
      final NodeSequence seq = (NodeSequence) n.f0.choice;
      // ident will be set further down the tree of IdentifierAsString
      seq.elementAt(1).accept(this);
      final String regExpr = gdbv.getTokenHM().get(ident);
      if (regExpr == null) {
        final Token ident_nt = ((IdentifierAsString) seq.elementAt(1)).f0;
        mess.softErr("Undefined token \"" + ident_nt + "\".", ident_nt.beginLine, ident_nt.beginColumn);
        ident = "";
      } else if (DONT_CREATE.equals(regExpr)) {
        // requested not to create the node
        addNodeToken = false;
      } else if (gdbv.getNotTbcNodesHM().containsKey(ident)) {
        // requested not to create the node
        addNodeToken = false;
      }
      break;
    
    case 3: // %3 #0 "<" #1 "EOF" #2 ">"
      break;
    
    default:
      final String msg = "Unreachable code executed!";
      Messages.hardErr(msg);
      throw new ProgrammaticError(msg);
    
    }
    if (addNodeToken) {
      uci.addField(nodeToken, fng.genFieldName(nodeToken));
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
    ident = n.f0.image;
  }
  
}

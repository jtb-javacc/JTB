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

import java.util.Hashtable;

import EDU.purdue.jtb.syntaxtree.ComplexRegularExpression;
import EDU.purdue.jtb.syntaxtree.ComplexRegularExpressionChoices;
import EDU.purdue.jtb.syntaxtree.ComplexRegularExpressionUnit;
import EDU.purdue.jtb.syntaxtree.Identifier;
import EDU.purdue.jtb.syntaxtree.JavaCCInput;
import EDU.purdue.jtb.syntaxtree.NodeOptional;
import EDU.purdue.jtb.syntaxtree.NodeSequence;
import EDU.purdue.jtb.syntaxtree.Production;
import EDU.purdue.jtb.syntaxtree.RegExprSpec;
import EDU.purdue.jtb.syntaxtree.RegularExprProduction;
import EDU.purdue.jtb.syntaxtree.RegularExpression;
import EDU.purdue.jtb.syntaxtree.StringLiteral;

/**
 * Generates a symbol lookup table of tokens which have a constant regular expression, e.g. < PLUS :
 * "+" >, which will be used to generate a default constructor.
 *
 * @author Marc Mazas, mmazas@sopragroup.com
 * @version 1.4.0 : 05-08/2009 : MMa : adapted to JavaCC v4.2 grammar and JDK 1.5
 */
public class TokenTableBuilder extends DepthFirstVoidVisitor {

  /** The Hashtable keys = token names, values = regular expressions */
  private final Hashtable<String, String> table   = new Hashtable<String, String>();
  /** Stores the current name */
  private String                          name    = "";
  /** Stores the current regular expression */
  private String                          regExpr = "";

  /**
   * The returned Hashtable has the names of the tokens as the keys and the constant regular
   * expressions as the values or "" if the regular expression is not constant.
   *
   * @return the Hashtable
   */
  public Hashtable<String, String> getTokenTable() {
    return table;
  }

  /**
   * Visits a {@link JavaCCInput} node, whose children are the following :
   * <p>
   * f0 -> JavaCCOptions()<br>
   * f1 -> "PARSER_BEGIN"<br>
   * f2 -> "("<br>
   * f3 -> Identifier()<br>
   * f4 -> ")"<br>
   * f5 -> CompilationUnit()<br>
   * f6 -> "PARSER_END"<br>
   * f7 -> "("<br>
   * f8 -> Identifier()<br>
   * f9 -> ")"<br>
   * f10 -> ( Production() )+<br>
   *
   * @param n the node to visit
   */
  @Override
  public void visit(final JavaCCInput n) {
    // visit only Production
    n.f10.accept(this);
  }

  /**
   * Visits a {@link Production} node, whose children are the following :
   * <p>
   * f0 -> . %0 JavaCodeProduction()<br>
   * .. .. | %1 RegularExprProduction()<br>
   * .. .. | %2 TokenManagerDecls()<br>
   * .. .. | %3 BNFProduction()<br>
   *
   * @param n the node to visit
   */
  @Override
  public void visit(final Production n) {
    // visit only RegularExprProduction
    if (n.f0.which == 1)
      n.f0.accept(this);
  }

/**
   * Visits a {@link RegularExprProduction} node, whose children are the following :
   * <p>
   * f0 -> [ %0 #0 "<" #1 "*" #2 ">"<br>
   * .. .. | %1 #0 "<" #1 < IDENTIFIER ><br>
   * .. .. . .. #2 ( $0 "," $1 < IDENTIFIER > )* #3 ">" ]<br>
   * f1 -> RegExprKind()<br>
   * f2 -> [ #0 "[" #1 "IGNORE_CASE" #2 "]" ]<br>
   * f3 -> ":"<br>
   * f4 -> "{"<br>
   * f5 -> RegExprSpec()<br>
   * f6 -> ( #0 "|" #1 RegExprSpec() )*<br>
   * f7 -> "}"<br>
   *
   * @param n the node to visit
   */
  @Override
  public void visit(final RegularExprProduction n) {
    // visit only RegExprSpec
    n.f5.accept(this);
    n.f6.accept(this);
  }

  /**
   * Visits a {@link RegExprSpec} node, whose children are the following :
   * <p>
   * f0 -> RegularExpression()<br>
   * f1 -> [ Block() ]<br>
   * f2 -> [ #0 ":" #1 < IDENTIFIER > ]<br>
   *
   * @param n the node to visit
   */
  @Override
  public void visit(final RegExprSpec n) {
    // visit only RegularExpression
    n.f0.accept(this);
  }

/**
   * Visits a {@link RegularExpression} node, whose children are the following :
   * <p>
   * f0 -> . %0 StringLiteral()<br>
   * .. .. | %1 #0 < LANGLE : "<" ><br>
   * .. .. . .. #1 [ $0 [ "#" ] $1 Identifier() $2 ":" ] #2 ComplexRegularExpressionChoices() #3 < RANGLE : ">" ><br>
   * .. .. | %2 #0 "<" #1 Identifier() #2 ">"<br>
   * .. .. | %3 #0 "<" #1 "EOF" #2 ">"<br>
   *
   * @param n the node to visit
   */
  @Override
  public void visit(final RegularExpression n) {
    if (n.f0.which == 1) {
      // <LANGLE: "<"> [ [ "#" ] Identifier() ":" ] ComplexRegularExpressionChoices(c) <RANGLE: ">">
      final NodeSequence seq = (NodeSequence) n.f0.choice;
      final NodeOptional opt = (NodeOptional) seq.elementAt(1);
      if (opt.present()) {
        // name is set further down the tree
        seq.elementAt(1).accept(this);
        // regExpr is set further down the tree
        seq.elementAt(2).accept(this);
        table.put(name, regExpr);
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
   * @param n the node to visit
   */
  @Override
  public void visit(final ComplexRegularExpressionChoices n) {
    if (n.f1.present())
      // if f1 is present, this isn't a constant regexpr
      regExpr = "";
    else
      n.f0.accept(this);
  }

  /**
   * Visits a {@link ComplexRegularExpression} node, whose children are the following :
   * <p>
   * f0 -> ( ComplexRegularExpressionUnit() )+<br>
   *
   * @param n the node to visit
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
   * .. .. | %1 #0 "<" #1 Identifier() #2 ">"<br>
   * .. .. | %2 CharacterList()<br>
   * .. .. | %3 #0 "(" #1 ComplexRegularExpressionChoices() #2 ")"<br>
   * .. .. . .. #3 ( &0 "+"<br>
   * .. .. . .. .. | &1 "*"<br>
   * .. .. . .. .. | &2 "?"<br>
   * .. .. . .. .. | &3 $0 "{" $1 IntegerLiteral()<br>
   * .. .. . .. .. . .. $2 [ £0 ","<br>
   * .. .. . .. .. . .. .. . £1 [ IntegerLiteral() ] ] $3 "}" )?<br>
   *
   * @param n the node to visit
   */
  @Override
  public void visit(final ComplexRegularExpressionUnit n) {
    if (n.f0.which == 0)
      // StringLiteral()
      n.f0.accept(this);
    else
      // others
      regExpr = "";
  }

  /**
   * Visits a {@link Identifier} node, whose children are the following :
   * <p>
   * f0 -> < IDENTIFIER ><br>
   *
   * @param n the node to visit
   */
  @Override
  public void visit(final Identifier n) {
    name = n.f0.tokenImage;
  }

  /**
   * Visits a {@link StringLiteral} node, whose children are the following :
   * <p>
   * f0 -> < STRING_LITERAL ><br>
   *
   * @param n the node to visit
   */
  @Override
  public void visit(final StringLiteral n) {
    regExpr = n.f0.tokenImage;
  }

}

/**
 * Copyright (c) 2004,2005 UCLA Compilers Group. All rights reserved. Redistribution and use in
 * source and binary forms, with or without modification, are permitted provided that the following
 * conditions are met: Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer. Redistributions in binary form must reproduce
 * the above copyright notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution. Neither UCLA nor the names
 * of its contributors may be used to endorse or promote products derived from this software without
 * specific prior written permission. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND
 * CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO
 * EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
 * THE POSSIBILITY OF SUCH DAMAGE.
 **/
/*
 * All files in the distribution of JTB, The Java Tree Builder are Copyright 1997, 1998, 1999 by the
 * Purdue Research Foundation of Purdue University. All rights reserved. Redistribution and use in
 * source and binary forms are permitted provided that this entire copyright notice is duplicated in
 * all such copies, and that any documentation, announcements, and other materials related to such
 * distribution and use acknowledge that the software was developed at Purdue University, West
 * Lafayette, Indiana by Kevin Tao and Jens Palsberg. No charge may be made for copies, derivations,
 * or distributions of this material without the express written consent of the copyright holder.
 * Neither the name of the University nor the name of the author may be used to endorse or promote
 * products derived from this material without specific prior written permission. THIS SOFTWARE IS
 * PROVIDED ``AS IS'' AND WITHOUT ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, WITHOUT LIMITATION,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR ANY PARTICULAR PURPOSE.
 */
package EDU.purdue.jtb.visitor;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;

import EDU.purdue.jtb.misc.ClassInfo;
import EDU.purdue.jtb.misc.FieldNameGenerator;
import EDU.purdue.jtb.misc.Globals;
import EDU.purdue.jtb.misc.Messages;
import EDU.purdue.jtb.syntaxtree.BNFProduction;
import EDU.purdue.jtb.syntaxtree.Expansion;
import EDU.purdue.jtb.syntaxtree.ExpansionChoices;
import EDU.purdue.jtb.syntaxtree.ExpansionUnit;
import EDU.purdue.jtb.syntaxtree.ExpansionUnitInTCF;
import EDU.purdue.jtb.syntaxtree.INode;
import EDU.purdue.jtb.syntaxtree.Identifier;
import EDU.purdue.jtb.syntaxtree.JavaCCInput;
import EDU.purdue.jtb.syntaxtree.JavaCodeProduction;
import EDU.purdue.jtb.syntaxtree.LocalLookahead;
import EDU.purdue.jtb.syntaxtree.NodeChoice;
import EDU.purdue.jtb.syntaxtree.NodeOptional;
import EDU.purdue.jtb.syntaxtree.NodeSequence;
import EDU.purdue.jtb.syntaxtree.NodeToken;
import EDU.purdue.jtb.syntaxtree.Production;
import EDU.purdue.jtb.syntaxtree.RegularExprProduction;
import EDU.purdue.jtb.syntaxtree.RegularExpression;
import EDU.purdue.jtb.syntaxtree.StringLiteral;
import EDU.purdue.jtb.syntaxtree.TokenManagerDecls;

/**
 * The ClassGenerator visitor creates a list of ClassInfo objects describing every class to be
 * generated.<br> {@link Annotator} and ClassGenerator depend on each other to create and use classes.<br>
 * Programming note: we do not continue down the tree once a new field has been added to curClass,
 * as we only worry about top-level expansions.<br>
 *
 * @author Marc Mazas, mmazas@sopragroup.com
 * @version 1.4.0 : 05-08/2009 : MMa : adapted to JavaCC v4.2 grammar and JDK 1.5
 */
public class ClassGenerator extends DepthFirstVoidVisitor {

  /** Visitor for finding return variables declarations */
  final GlobalDataFinder             gdfv       = new GlobalDataFinder();
  /** Flag to print the token or not */
  private boolean                    printToken = false;
  /** The table used to generate default constructors if a token has a constant regexpr */
  private Hashtable<String, String>  tokenTable;
  /** The current generated class */
  private ClassInfo                  curClass;
  /** The list of generated classes */
  private final ArrayList<ClassInfo> classList  = new ArrayList<ClassInfo>();
  /** The field names generator (descriptive or not, depending on -f option) */
  private final FieldNameGenerator   nameGen    = new FieldNameGenerator();
  /** Global variable to pass RegularExpression info between methods (as they are recursive) */
  String                             regExpr    = "";
  /** Global variable to pass Identifier info between methods (as they are recursive) */
  String                             ident      = "";
  /** The JavaCodeProductions table */
  Hashtable<String, String>          jcpHT      = new Hashtable<String, String>();

  /**
   * Getter for the class list.
   *
   * @return the class list
   */
  public ArrayList<ClassInfo> getClassList() {
    return classList;
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
    // find first global data
    n.accept(gdfv);
    // build the token table and visit only Production
    final TokenTableBuilder builder = new TokenTableBuilder();
    n.accept(builder);
    tokenTable = builder.getTokenTable();
    n.f10.accept(this);
  }

  /**
   * Visits a {@link JavaCodeProduction} node, whose children are the following :
   * <p>
   * f0 -> "JAVACODE"<br>
   * f1 -> AccessModifier()<br>
   * f2 -> ResultType()<br>
   * f3 -> Identifier()<br>
   * f4 -> FormalParameters()<br>
   * f5 -> [ #0 "throws" #1 Name()<br>
   * .. .. . #2 ( $0 "," $1 Name() )* ]<br>
   * f6 -> Block()<br>
   *
   * @param n the node to visit
   */
  @Override
  public void visit(@SuppressWarnings("unused") final JavaCodeProduction n) {
    // Don't visit
  }

  /**
   * Visits a {@link BNFProduction} node, whose children are the following :
   * <p>
   * f0 -> AccessModifier()<br>
   * f1 -> ResultType()<br>
   * f2 -> Identifier()<br>
   * f3 -> FormalParameters()<br>
   * f4 -> [ #0 "throws" #1 Name()<br>
   * .. .. . #2 ( $0 "," $1 Name() )* ]<br>
   * f5 -> ":"<br>
   * f6 -> Block()<br>
   * f7 -> "{"<br>
   * f8 -> ExpansionChoices()<br>
   * f9 -> "}"<br>
   *
   * @param n the node to visit
   */
  @Override
  public void visit(final BNFProduction n) {
    nameGen.reset();
    printToken = true;
    curClass = new ClassInfo(n.f8, n.f2.f0.tokenImage);
    classList.add(curClass);
    n.f8.accept(this);
    printToken = false;
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
  public void visit(@SuppressWarnings("unused") final RegularExprProduction n) {
    // Don't visit : don't want to generate NodeTokens inside RegularExpression
    // if it's visited from a RegularExpressionProduction
  }

  /**
   * Visits a {@link TokenManagerDecls} node, whose children are the following :
   * <p>
   * f0 -> "TOKEN_MGR_DECLS"<br>
   * f1 -> ":"<br>
   * f2 -> ClassOrInterfaceBody()<br>
   *
   * @param n the node to visit
   */
  @Override
  public void visit(@SuppressWarnings("unused") final TokenManagerDecls n) {
    // Don't visit
  }

  /**
   * Visits a {@link ExpansionChoices} node, whose children are the following :
   * <p>
   * f0 -> Expansion()<br>
   * f1 -> ( #0 "|" #1 Expansion() )*<br>
   *
   * @param n the node to visit
   */
  @Override
  public void visit(final ExpansionChoices n) {
    if (!n.f1.present())
      n.f0.accept(this);
    else
      curClass
              .addField(Globals.nodeChoiceName, nameGen.genCommentFieldName(Globals.nodeChoiceName));
  }

  /**
   * Visits a {@link Expansion} node, whose children are the following :
   * <p>
   * f0 -> ( #0 "LOOKAHEAD" #1 "(" #2 LocalLookahead() #3 ")" )?<br>
   * f1 -> ( ExpansionUnit() )+<br>
   *
   * @param n the node to visit
   */
  @Override
  public void visit(final Expansion n) {
    // don't visit LocalLookahead
    n.f1.accept(this);
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
   * @param n the node to visit
   */
  @Override
  public void visit(@SuppressWarnings("unused") final LocalLookahead n) {
    // Don't visit...ignore lookaheads
  }

  /**
   * Visits a {@link ExpansionUnit} node, whose children are the following :
   * <p>
   * f0 -> . %0 #0 "LOOKAHEAD" #1 "(" #2 LocalLookahead() #3 ")"<br>
   * .. .. | %1 Block()<br>
   * .. .. | %2 #0 "[" #1 ExpansionChoices() #2 "]"<br>
   * .. .. | %3 ExpansionUnitInTCF()<br>
   * .. .. | %4 #0 [ $0 PrimaryExpression() $1 "=" ]<br>
   * .. .. . .. #1 ( &0 $0 Identifier() $1 Arguments()<br>
   * .. .. . .. .. | &1 $0 RegularExpression()<br>
   * .. .. . .. .. . .. $1 [ £0 "." £1 < IDENTIFIER > ] )<br>
   * .. .. | %5 #0 "(" #1 ExpansionChoices() #2 ")"<br>
   * .. .. . .. #3 ( &0 "+"<br>
   * .. .. . .. .. | &1 "*"<br>
   * .. .. . .. .. | &2 "?" )?<br>
   *
   * @param n the node to visit
   */
  @Override
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
        curClass.addField(Globals.nodeOptName, nameGen.genCommentFieldName(Globals.nodeOptName));
        return;

      case 3:
        // %3 ExpansionUnitInTCF()
        n.f0.choice.accept(this);
        return;

      case 4:
        // %4 #0 [ $0 PrimaryExpression() $1 "=" ]
        // .. #1 ( &0 $0 Identifier() $1 Arguments()
        // .. .. | &1 $0 RegularExpression()
        // .. .. . .. $1 [ £0 "." £1 < IDENTIFIER > ] )
        seq = (NodeSequence) n.f0.choice;
        ch = (NodeChoice) seq.elementAt(1);
        final NodeSequence seq1 = (NodeSequence) ch.choice;
        if (ch.which == 0) {
          // &0 $0 Identifier() $1 Arguments()
          // ident will be set further down the tree
          seq1.elementAt(0).accept(this);
          // add the field if not a JavaCodeProduction
          if (!jcpHT.containsKey(ident)) {
            curClass.addField(ident, nameGen.genCommentFieldName(ident));
          }
        } else {
          // &1 $0 RegularExpression() $1 [ £0 "." £1 < IDENTIFIER > ] )
          seq1.elementAt(0).accept(this);
        }
        return;

      case 5:
        // %5 #0 "(" #1 ExpansionChoices() #2 ")"
        // .. #3 ( &0 "+" | &1 "*" | &2 "?" )?
        seq = (NodeSequence) n.f0.choice;
        final NodeOptional opt = (NodeOptional) seq.elementAt(3);
        if (opt.present()) {
          ch = (NodeChoice) opt.node;
          final String name = getNodeNameForMod(ch.which);
          curClass.addField(name, nameGen.genCommentFieldName(name));
        } else {
          if (((ExpansionChoices) seq.elementAt(1)).f1.present())
            // f1 -> ( "|" Expansion(c2) )*
            curClass.addField(Globals.nodeChoiceName,
                              nameGen.genCommentFieldName(Globals.nodeChoiceName));
          else
            // f0 -> Expansion(c1)
            curClass
                    .addField(Globals.nodeSeqName, nameGen.genCommentFieldName(Globals.nodeSeqName));
        }
        return;

      default:
        Messages.hardErr("n.f0.which = " + String.valueOf(n.f0.which));
        return;
    }
  }

  /**
   * Visits a {@link ExpansionUnitInTCF} node, whose children are the following :
   * <p>
   * f0 -> "try"<br>
   * f1 -> "{"<br>
   * f2 -> ExpansionChoices()<br>
   * f3 -> "}"<br>
   * f4 -> ( #0 "catch" #1 "(" #2 Name() #3 < IDENTIFIER > #4 ")" #5 Block() )*<br>
   * f5 -> [ #0 "finally" #1 Block() ]<br>
   *
   * @param n the node to visit
   */
  @Override
  public void visit(final ExpansionUnitInTCF n) {
    // f2 -> ExpansionChoices()
    n.f2.accept(this);
  }

  /**
   * Returns the base node name for a given modifier.
   *
   * @param mod the modifier
   * @return the corresponding base node name
   */
  private String getNodeNameForMod(final int mod) {
    if (mod == 0)
      return Globals.nodeListName;
    else if (mod == 1)
      return Globals.nodeListOptName;
    else if (mod == 2)
      return Globals.nodeOptName;
    else {
      Messages.hardErr("Illegal EBNF modifier in " + "ExpansionUnit: mod = " + mod);
      return "";
    }
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
    regExpr = "";
    String initialValue = null;
    boolean isEOF = false;
    if (!printToken)
      return;
    switch (n.f0.which) {
      case 0: // StringLiteral()
        n.f0.choice.accept(this);
        break;
      case 1: // <LANGLE: "<"> [ [ "#" ] Identifier() ":" ] ComplexRegularExpressionChoices(c) <RANGLE: ">">
        regExpr = "";
        break;
      case 2: // "<" Identifier() ">"
        final NodeSequence seq = (NodeSequence) n.f0.choice;
        // ident will be set further down the tree
        seq.elementAt(1).accept(this);
        regExpr = tokenTable.get(ident);
        if (regExpr == null) {
          final NodeToken ident_nt = ((Identifier) seq.elementAt(1)).f0;
          Messages.softErr("Undefined token \"" + ident_nt + "\".", ident_nt.beginLine);
          regExpr = "";
          ident = "";
        }
        break;
      case 3: // "<" "EOF" ">"
        regExpr = "";
        isEOF = true;
        break;
      default:
        Messages.hardErr("Unreachable code executed!");
    }
    if (isEOF)
      initialValue = "new " + Globals.nodeTokenName + "(\"\")";
    else if (regExpr.length() != 0)
      initialValue = "new " + Globals.nodeTokenName + "(" + regExpr + ")";
    curClass.addField(Globals.nodeTokenName, nameGen.genCommentFieldName(Globals.nodeTokenName),
                      initialValue);
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
    ident = n.f0.tokenImage;
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

  /**
   * The GlobalDataFinder visitor finds:<br>
   * all "void" JavaCodeProductions and adds them to a Hashtable (which will be used to avoid
   * creating later invalid child nodes).
   */
  class GlobalDataFinder extends DepthFirstVoidVisitor {

    /**
     * Constructor, with a given buffer and a default indentation.
     */
    GlobalDataFinder() {
      super();
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
      // visits only Productions
      // f10 -> ( Production() )+
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
     * @param n the node to visit
     */
    @Override
    public void visit(final Production n) {
      // visits only JavaCodeProduction
      if (n.f0.which == 0)
        n.f0.accept(this);
    }

    /**
     * Visits a {@link JavaCodeProduction} node, whose children are the following :
     * <p>
     * f0 -> "JAVACODE"<br>
     * f1 -> AccessModifier()<br>
     * f2 -> ResultType()<br>
     * f3 -> Identifier()<br>
     * f4 -> FormalParameters()<br>
     * f5 -> [ #0 "throws" #1 Name()<br>
     * .. .. . #2 ( $0 "," $1 Name() )* ]<br>
     * f6 -> Block()<br>
     *
     * @param n the node to visit
     */
    @Override
    public void visit(final JavaCodeProduction n) {
      // store it in the hashtable
      // f3 -> Identifier()
      final String id = n.f3.f0.tokenImage;
      jcpHT.put(id, id);
    }
  }
}

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

import static EDU.purdue.jtb.misc.Globals.*;
import static EDU.purdue.jtb.visitor.GlobalDataBuilder.DONT_CREATE_NODE_STR;

import java.util.ArrayList;
import java.util.Iterator;

import EDU.purdue.jtb.misc.ClassInfo;
import EDU.purdue.jtb.misc.FieldNameGenerator;
import EDU.purdue.jtb.misc.JavaBranchPrinter;
import EDU.purdue.jtb.misc.Messages;
import EDU.purdue.jtb.syntaxtree.BNFProduction;
import EDU.purdue.jtb.syntaxtree.Expansion;
import EDU.purdue.jtb.syntaxtree.ExpansionChoices;
import EDU.purdue.jtb.syntaxtree.ExpansionUnit;
import EDU.purdue.jtb.syntaxtree.ExpansionUnitTCF;
import EDU.purdue.jtb.syntaxtree.INode;
import EDU.purdue.jtb.syntaxtree.IdentifierAsString;
import EDU.purdue.jtb.syntaxtree.JavaCCInput;
import EDU.purdue.jtb.syntaxtree.JavaCodeProduction;
import EDU.purdue.jtb.syntaxtree.LocalLookahead;
import EDU.purdue.jtb.syntaxtree.NodeChoice;
import EDU.purdue.jtb.syntaxtree.NodeOptional;
import EDU.purdue.jtb.syntaxtree.NodeSequence;
import EDU.purdue.jtb.syntaxtree.NodeToken;
import EDU.purdue.jtb.syntaxtree.RegularExprProduction;
import EDU.purdue.jtb.syntaxtree.RegularExpression;
import EDU.purdue.jtb.syntaxtree.TokenManagerDecls;

/**
 * The {@link ClassesFinder} visitor creates a list of {@link ClassInfo} objects describing every
 * class to be generated.
 * <p>
 * {@link Annotator}, {@link CommentsPrinter} and {@link ClassesFinder} depend on each other to
 * create and use classes.
 * <p>
 * Programming note: we do not continue down the tree once a new field has been added to curClass,
 * as we only worry about top-level expansions.<br>
 * 
 * @author Marc Mazas
 * @version 1.4.0 : 05-08/2009 : MMa : adapted to JavaCC v4.2 grammar and JDK 1.5
 * @version 1.4.6 : 01/2011 : FA : add -va and -npfx and -nsfx options
 * @version 1.4.7 : 12/2011-05/2012 : MMa : fixed problem in fmtJavaNodeCode<br>
 *          added bareJavaNodeCode and fixed problems in visiting ExpansionUnitTCF<br>
 *          1.4.7 : 07/2012 : MMa : followed changes in jtbgram.jtb (IndentifierAsString())<br>
 *          1.4.7 : 09/2012 : MMa : changed some comments and removed one JavaBranchPrinter visitor
 *          ; removed printToken and regExpr as of no use ; added non node creation and the
 *          reference to the {@link GlobalDataBuilder}
 */
public class ClassesFinder extends DepthFirstVoidVisitor {

  /** The reference to the global data builder visitor */
  final GlobalDataBuilder            gdbv;
  /** Visitor to print a java node and its subtree with default indentation */
  final JavaBranchPrinter            jbpv   = new JavaBranchPrinter(null);
  /** The current generated class */
  private ClassInfo                  ci;
  /** The list of generated classes */
  private final ArrayList<ClassInfo> ciList = new ArrayList<ClassInfo>();
  /** The field names generator (descriptive or not, depending on -f option) */
  private final FieldNameGenerator   gen    = new FieldNameGenerator();
  /** Global variable to pass IdentifierAsString info between methods (as they are recursive) */
  String                             ident  = "";
  /** The OS line separator as a string */
  public static final String         LS     = System.getProperty("line.separator");
  /** The OS line separator string length */
  public static final int            LS_LEN = LS.length();
  /** The OS line separator first character */
  public static final char           LS0    = LS.charAt(0);

  /**
   * Constructor.
   * 
   * @param aGdbv - the global data builder visitor
   */
  public ClassesFinder(final GlobalDataBuilder aGdbv) {
    gdbv = aGdbv;
  }

  /**
   * Getter for the class list.
   * 
   * @return the class list
   */
  public ArrayList<ClassInfo> getClassList() {
    return ciList;
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
   * 
   * @param n - the node to visit
   */
  @Override
  public void visit(final JavaCCInput n) {
    // f10 -> ( Production() )+
    for (final Iterator<INode> e = n.f10.elements(); e.hasNext();) {
      e.next().accept(this);
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
   * f6 -> Block()<br>
   * 
   * @param n - the node to visit
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
    gen.reset();
    // f5 -> [ "!" ]
    // generate the class even if the node generation is not requested
    ci = new ClassInfo(n.f9, n.f2.f0.tokenImage, gdbv);
    ciList.add(ci);
    // f9 -> ExpansionChoices()
    n.f9.accept(this);
  }

/**
   * Visits a {@link RegularExprProduction} node, whose children are the following :
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
  public void visit(@SuppressWarnings("unused") final RegularExprProduction n) {
    // Don't visit : don't want to generate NodeTokens inside RegularExpression
    //  if it's visited from a RegularExprProduction
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
    // Don't visit
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
      ci.addField(nodeChoice, gen.genFieldName(nodeChoice));
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
   * f4 -> [ #0 "{" #1 Expression() #2 "}" ]<br>
   * 
   * @param n - the node to visit
   */
  @Override
  public void visit(@SuppressWarnings("unused") final LocalLookahead n) {
    // Don't visit
  }

  /**
   * Visits a {@link ExpansionUnit} node, whose children are the following :
   * <p>
   * f0 -> . %0 #0 "LOOKAHEAD" #1 "(" #2 LocalLookahead() #3 ")"<br>
   * .. .. | %1 Block()<br>
   * .. .. | %2 #0 "[" #1 ExpansionChoices() #2 "]"<br>
   * .. .. | %3 ExpansionUnitTCF()<br>
   * .. .. | %4 #0 [ $0 PrimaryExpression() $1 "=" ]<br>
   * .. .. . .. .. . .. $2 [ "!" ]<br>
   * .. .. . .. #1 ( &0 $0 IdentifierAsString() $1 Arguments()<br>
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
        ci.addField(nodeOpt, gen.genFieldName(nodeOpt));
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
        ch = (NodeChoice) seq.elementAt(1);
        final NodeSequence seq1 = (NodeSequence) ch.choice;
        if (ch.which == 0) {
          // &0 $0 IdentifierAsString() $1 Arguments() $2 [ "!" ]
          // ident will be set further down the tree
          seq1.elementAt(0).accept(this);
          // add field only if node creation is not requested not to be generated
          if (!gdbv.getNcnHT().containsKey(ident) && !((NodeOptional) seq1.elementAt(2)).present()) {
            ci.addField(getFixedName(ident), gen.genFieldName(ident));
          }
        } else {
          // &1 $0 RegularExpression() $1 [ ?0 "." ?1 < IDENTIFIER > ] $2 [ "!" ]
          // add field only if node creation is not requested not to be generated
          if (!((NodeOptional) seq1.elementAt(2)).present())
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
          final String type = getNodeNameForMod(ch.which);
          ci.addField(type, gen.genFieldName(type));
        } else {
          if (((ExpansionChoices) seq.elementAt(1)).f1.present()) {
            // f1 -> ( "|" Expansion() )*
            ci.addField(nodeChoice, gen.genFieldName(nodeChoice));
          } else {
            // f0 -> Expansion()
            ci.addField(nodeSeq, gen.genFieldName(nodeSeq));
          }
        }
        return;

      default:
        Messages.hardErr("n.f0.which = " + String.valueOf(n.f0.which));
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
    String fmtStr;
    INode inode;
    final String newNodeTCF = "new " + nodeTCF;
    // f0 -> "try"
    ci.addField(nodeTCF, gen.genFieldName(nodeTCF), newNodeTCF + "(\"try\")", null);
    // f1 -> "{"
    ci.addField(nodeTCF, gen.genFieldName(nodeTCF), newNodeTCF + "(\"{\")", null);
    // f2 -> ExpansionChoices()
    n.f2.accept(this);
    // f3 -> "}"
    ci.addField(nodeTCF, gen.genFieldName(nodeTCF), newNodeTCF + "(\"}\")", null);

    // f4 -> ( #0 "catch" #1 "(" #2 Name() #3 < IDENTIFIER > #4 ")" #5 Block() )*
    if (n.f4.present()) {
      for (int i = 0; i < n.f4.size(); i++) {
        // #0 "catch"
        ci.addField(nodeTCF, gen.genFieldName(nodeTCF), newNodeTCF + "(\"catch\")", null);
        // #1 "("
        ci.addField(nodeTCF, gen.genFieldName(nodeTCF), newNodeTCF + "(\"(\")", null);
        // #2 Name()
        inode = ((NodeSequence) n.f4.elementAt(i)).elementAt(2);
        fmtStr = newNodeTCF + "(\"" + fmtJavaNodeCode(inode) + "\")";
        ci.addField(nodeTCF, gen.genFieldName(nodeTCF), fmtStr, bareJavaNodeCode(inode));
        // #3 < IDENTIFIER >
        inode = ((NodeSequence) n.f4.elementAt(i)).elementAt(3);
        fmtStr = newNodeTCF + "(\"" + fmtJavaNodeCode(inode) + "\")";
        ci.addField(nodeTCF, gen.genFieldName(nodeTCF), fmtStr, bareJavaNodeCode(inode));
        // #4 ")"
        ci.addField(nodeTCF, gen.genFieldName(nodeTCF), newNodeTCF + "(\")\")", null);
        // #5 Block()
        inode = ((NodeSequence) n.f4.elementAt(i)).elementAt(5);
        fmtStr = newNodeTCF + "(\"" + fmtJavaNodeCode(inode) + "\")";
        ci.addField(nodeTCF, gen.genFieldName(nodeTCF), fmtStr, bareJavaNodeCode(inode));
      }
    }

    // f5 -> [ #0 "finally" #1 Block() ]
    if (n.f5.present()) {
      // #0 "finally"
      ci.addField(nodeTCF, gen.genFieldName(nodeTCF), newNodeTCF + "(\"finally\")", null);
      // #1 Block()
      inode = ((NodeSequence) n.f5.node).elementAt(1);
      fmtStr = newNodeTCF + "(\"" + fmtJavaNodeCode(inode) + "\")";
      ci.addField(nodeTCF, gen.genFieldName(nodeTCF), fmtStr, bareJavaNodeCode(inode));
    }
  }

  /**
   * Returns the base node name for a given modifier.
   * 
   * @param mod - the modifier
   * @return the corresponding base node name
   */
  private String getNodeNameForMod(final int mod) {
    if (mod == 0)
      return nodeList;
    else if (mod == 1)
      return nodeListOpt;
    else if (mod == 2)
      return nodeOpt;
    else {
      Messages.hardErr("Illegal EBNF modifier in " + "ExpansionUnit: mod = " + mod);
      return "";
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
    switch (n.f0.which) {
      case 0: // %0 StringLiteral()
        // we indeed fall here while building JTB
        //        throw new AssertionError("CG RE 0");
        break;

      case 1: // %1 #0 "<" #1 [ $0 [ "#" ] $1 IdentifierAsString() $2 ":" ] #2 ComplexRegularExpressionChoices() #3 ">"
        // we indeed fall here while building JTB
        //        throw new AssertionError("CG RE 1");
        break;

      case 2: // %2 #0 "<" #1 IdentifierAsString() #2 ">"
        // we indeed fall here while building JTB
        //        throw new AssertionError("CG RE 2");
        final NodeSequence seq = (NodeSequence) n.f0.choice;
        // ident will be set further down the tree of IdentifierAsString
        seq.elementAt(1).accept(this);
        String regExpr = gdbv.getTokenHT().get(ident);
        if (regExpr == null) {
          final NodeToken ident_nt = ((IdentifierAsString) seq.elementAt(1)).f0;
          Messages.softErr("Undefined token \"" + ident_nt + "\".", ident_nt.beginLine,
                           ident_nt.beginColumn);
          regExpr = "";
          ident = "";
        } else if (DONT_CREATE_NODE_STR.equals(regExpr)) {
          // requested not to create the node
          return;
        }
        break;

      case 3: // %3 #0 "<" #1 "EOF" #2 ">"
        // we do not fall here while building JTB but we do when generating from a grammar with EOF
        //        assert false : "case 3 RegularExpression";
        //        throw new AssertionError("CG RE 3");
        break;
      default:
        Messages.hardErr("Unreachable code executed!");
    }
    ci.addField(nodeToken, gen.genFieldName(nodeToken));
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
    ident = n.f0.tokenImage;
  }

  /**
   * Visits a given (java code) node branch of the tree and returns a formatted string
   * representation of the subtree, escaping double quotes and replacing end of lines by the OS line
   * separator, and with an indentation of 1.
   * 
   * @param javaNode - the node to walk down and format as a string
   * @return the formatted string
   */
  String fmtJavaNodeCode(final INode javaNode) {
    final StringBuilder sb = jbpv.genJavaBranch(javaNode, true);
    final StringBuilder res = new StringBuilder(2 * sb.length());
    final int len = sb.length();
    for (int i = 0; i < len; i++) {
      final char c = sb.charAt(i);
      if (c == '"') {
        res.append("\\\"");
      } else if (c == LS0) {
        if (LS_LEN > 1) {
          if (i < len) {
            i++;
            if (LS.charAt(1) != sb.charAt(i))
              i--;
          }
        }
        res.append(eosPlusEolSpacesEos);
      } else {
        res.append(c);
      }
    }
    return res.toString();
  }

  /**
   * Visits a given (java code) node branch of the tree and returns a bare string representation of
   * the subtree with an indentation level of 0.
   * 
   * @param javaNode - the node to walk down and return as a string
   * @return the bare string
   */
  String bareJavaNodeCode(final INode javaNode) {
    return jbpv.genJavaBranch(javaNode, true).toString();
  }

  /** NewLine, EndOfString (escaped double quotes), Plus */
  public static String eolEosPlus;
  static {
    eolEosPlus = "\\n\" +";
  }

  /** NewLine, EndOfString (escaped double quotes), Plus, EndOfLine */
  public static String eolEosPlusEol;
  static {
    eolEosPlusEol = eolEosPlus + LS;
  }

  /** Indentation spaces, EndOfString (escaped double quotes) */
  public static String spacesEos;
  static {
    final StringBuilder res = new StringBuilder(nodeTCF.length() + 21);
    for (int j = 0; j < nodeTCF.length(); j++)
      res.append(" ");
    res.append("                    \"");
    spacesEos = res.toString();
  }

  /** A string */
  public static String eosPlusEolSpacesEos;
  static {
    eosPlusEolSpacesEos = eolEosPlusEol + spacesEos;
  }

}

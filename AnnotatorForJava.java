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

import static EDU.purdue.jtb.misc.Globals.getFixedName;
import static EDU.purdue.jtb.misc.Globals.getQualifiedName;
import static EDU.purdue.jtb.misc.Globals.jtbRtPrefix;
import static EDU.purdue.jtb.misc.Globals.keepSpecialTokens;
import static EDU.purdue.jtb.misc.Globals.nodeChoice;
import static EDU.purdue.jtb.misc.Globals.nodeList;
import static EDU.purdue.jtb.misc.Globals.nodeListOpt;
import static EDU.purdue.jtb.misc.Globals.nodeOpt;

import java.util.Iterator;

import EDU.purdue.jtb.misc.Messages;
import EDU.purdue.jtb.misc.VarInfo;
import EDU.purdue.jtb.misc.VarInfoFactory;
import EDU.purdue.jtb.misc.VarInfoForJava;
import EDU.purdue.jtb.syntaxtree.BNFProduction;
import EDU.purdue.jtb.syntaxtree.CompilationUnit;
import EDU.purdue.jtb.syntaxtree.ExpansionChoices;
import EDU.purdue.jtb.syntaxtree.ExpansionUnit;
import EDU.purdue.jtb.syntaxtree.INode;
import EDU.purdue.jtb.syntaxtree.IdentifierAsString;
import EDU.purdue.jtb.syntaxtree.NodeChoice;
import EDU.purdue.jtb.syntaxtree.NodeListOptional;
import EDU.purdue.jtb.syntaxtree.NodeOptional;
import EDU.purdue.jtb.syntaxtree.NodeSequence;

/**
 * The {@link AnnotatorForJava} visitor generates the (jtb) annotated .jj file containing the tree-building
 * code.
 * <p>
 * {@link AnnotatorForJava}, {@link CommentsPrinter} and {@link ClassesFinder} depend on each other to
 * create and use classes.
 * <p>
 * Code is printed in a buffer and {@link #saveToFile} is called to save it in the output file.
 * <p>
 * {@link AnnotatorForJava} works as follows:
 * <ul>
 * <li>in generateRHS in visit(BNFProduction) and others, it redirects output to a temporary buffer,
 * <li>it walks down the tree, prints the RHS into the temporary buffer, and builds the varList,
 * <li>it traverses varList, prints the variable declarations to the main buffer
 * <li>it prints the Block to the main buffer, then the temporary buffer into the main buffer.
 * </ul>
 * When it wants to print a node and its subtree without annotating it, it uses
 * n.accept(JavaCCPrinter).
 * <p>
 * 
 * @author Marc Mazas
 * @version 1.4.0 : 05-08/2009 : MMa : adapted to JavaCC v4.2 grammar and JDK 1.5
 * @version 1.4.0.2 : 01/2010 : MMa : fixed output of else in IfStatement
 * @version 1.4.6 : 01/2011 : FA/MMa : add -va and -npfx and -nsfx options
 * @version 1.4.7 : 12/2011 : MMa : commented the JavaCodeProduction visit method ; optimized
 *          JTBToolkit class's code<br>
 *          1.4.7 : 07/2012 : MMa : followed changes in jtbgram.jtb (IndentifierAsString(),
 *          ForStatement()), updated grammar comments in the code<br>
 *          1.4.7 : 08-09/2012 : MMa : fixed soft errors for empty NodeChoice (bug JTB-1) ; fixed
 *          error on return statement for a void production ; added non node creation ; tuned
 *          messages labels and added the line number finder visitor ; moved the inner
 *          GlobalDataFinder to {@link GlobalDataBuilder}
 */
@SuppressWarnings("javadoc")
public class AnnotatorForJava extends AbstractAnnotator {

  public AnnotatorForJava(GlobalDataBuilder aGdbv) {
    super(aGdbv);
  }
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
    cupv.printImports(n.f1);
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
    if (keepSpecialTokens) {
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
    } else {
      sb.append(LS);
      sb.append("class JTBToolkit {").append(LS);
      sb.append(LS);
      sb.append("  static NodeToken makeNodeToken(final Token t) {").append(LS);
      sb.append("    return new NodeToken(t.image.intern(), t.kind, t.beginLine, t.beginColumn, t.endLine, t.endColumn);")
        .append(LS);
      sb.append("  }").append(LS);
      sb.append("}");
      oneNewLine(n);
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
   * 
   * @param n - the node to visit
   */
  @Override
  public void visit(final BNFProduction n) {
    getVarList().clear();
    outerVars.clear();
    prevVar = null;
    resetVarNum();
    bnfLvl = 0;
    annotateNode = true;
    curProduction = n.f2.f0.tokenImage;
    // f0 -> AccessModifier()
    n.f0.accept(this);
    // f1 -> ResultType()
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
    // print it in a block comment
    if (n.f5.present())
      sb.append(" /*!*/ ");
    // f6 -> ":"
    sb.append(" ");
    n.f6.accept(this);
    oneNewLine(n, "a");
    // generate the RHS into a temporary buffer and collect variables
    final StringBuilder rhsSB = generateRHS(n);
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
    for (final Iterator<VarInfo> e = getVarList().iterator(); e.hasNext();) {
      sb.append(e.next().generateNodeDeclaration());
      if (e.hasNext()) {
        oneNewLine(n, "d");
        sb.append(spc.spc);
      }
    }
    // f7 -> Block()
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
    // print the RHS buffer
    sb.append(rhsSB);
    // reset global variable
    resultType = null;
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
          // &0 $0 IdentifierAsString() $1 Arguments() $2 [ "!" ]
          final String ident = ((IdentifierAsString) seq1.elementAt(0)).f0.tokenImage;
          final boolean creLocNode = !gdbv.getNcnHT().containsKey(ident) &&
                                     !((NodeOptional) seq1.elementAt(2)).present();
          // generate 'JavaCodeProduction(') or 'BNFProduction()' if node is not to be created
          // otherwise generate 'ni = Production()'
          VarInfo varInfo = null;
          if (creLocNode) {
            name = genNewVarName();
            if (inEUT3)
              varInfo = VarInfoFactory.newVarInfo(ident, name, "null");
            else
              varInfo = VarInfoFactory.newVarInfo(ident, name);
            addVarList(varInfo);
            sb.append(name);
            sb.append(" = ");
            sb.append(" ");
          }
          // $0 IdentifierAsString()
          // must be prefixed / suffixed
          sb.append(getFixedName(((IdentifierAsString) seq1.elementAt(0)).f0.tokenImage));
          // $1 Arguments()
          sb.append(genJavaBranch(seq1.elementAt(1)));
          if (creLocNode)
            finalActions(varInfo);
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
            sb.append(qualifier());
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
        Messages.hardErr("n.f0.which = " + String.valueOf(n.f0.which));
        return;
    }
  }

  /**
   * Returns a string with the RHS of the current BNF production.<br>
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
  StringBuilder generateRHS(final BNFProduction n) {
    final StringBuilder mainSB = sb;
    final StringBuilder newSB = new StringBuilder(512);
    sb = jccpv.sb = newSB;
    // f5 -> [ "!" ]
    // do nothing (here we are in the callee)
    // f8 -> "{"
    n.f8.accept(this);
    oneNewLine(n, "generateRHS a");
    spc.updateSpc(+1);
    sb.append(spc.spc);
    // outerVars will be set further down the tree in finalActions
    // f9 -> ExpansionChoices()
    n.f9.accept(this);
    // must be prefixed / suffixed
    sb.append("{ return new ").append(getQualifiedName(n.f2.f0.tokenImage)).append("(");
    final Iterator<VarInfo> e = outerVars.iterator();
    if (e.hasNext()) {
      sb.append(e.next().getName());
      for (; e.hasNext();)
        sb.append(", ").append(e.next().getName());
    }
    sb.append("); }");
    oneNewLine(n, "generateRHS b");
    spc.updateSpc(-1);
    sb.append(spc.spc);
    // f10 -> "}"
    n.f10.accept(this);
    sb = jccpv.sb = mainSB;
    return newSB;
  }


  @Override
  public String newConstructor() {
    return "";
  }

  @Override
  public String qualifier() {
    return ".";
  }
  
  @Override
  public String makeNodeToken() {
    return "JTBToolkit.makeNodeToken";
  }
  @Override
  public String niew() {
    return "new";
  }

  
}

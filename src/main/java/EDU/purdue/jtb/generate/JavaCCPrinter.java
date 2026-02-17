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

import static EDU.purdue.jtb.common.Constants.*;
import static EDU.purdue.jtb.common.Constants.FILE_EXISTS_RC;
import static EDU.purdue.jtb.common.Constants.INDENT_AMT;
import static EDU.purdue.jtb.common.Constants.OK_RC;
import static EDU.purdue.jtb.parser.syntaxtree.JTBParserNodeConstants.JTB_SIG_ACCESSMODIFIER;
import static EDU.purdue.jtb.parser.syntaxtree.JTBParserNodeConstants.JTB_SIG_BNFPRODUCTION;
import static EDU.purdue.jtb.parser.syntaxtree.JTBParserNodeConstants.JTB_SIG_CHARACTERDESCRIPTOR;
import static EDU.purdue.jtb.parser.syntaxtree.JTBParserNodeConstants.JTB_SIG_CHARACTERLIST;
import static EDU.purdue.jtb.parser.syntaxtree.JTBParserNodeConstants.JTB_SIG_COMPLEXREGULAREXPRESSION;
import static EDU.purdue.jtb.parser.syntaxtree.JTBParserNodeConstants.JTB_SIG_COMPLEXREGULAREXPRESSIONCHOICES;
import static EDU.purdue.jtb.parser.syntaxtree.JTBParserNodeConstants.JTB_SIG_COMPLEXREGULAREXPRESSIONUNIT;
import static EDU.purdue.jtb.parser.syntaxtree.JTBParserNodeConstants.JTB_SIG_EXPANSION;
import static EDU.purdue.jtb.parser.syntaxtree.JTBParserNodeConstants.JTB_SIG_EXPANSIONCHOICES;
import static EDU.purdue.jtb.parser.syntaxtree.JTBParserNodeConstants.JTB_SIG_EXPANSIONUNIT;
import static EDU.purdue.jtb.parser.syntaxtree.JTBParserNodeConstants.JTB_SIG_EXPANSIONUNITTCF;
import static EDU.purdue.jtb.parser.syntaxtree.JTBParserNodeConstants.JTB_SIG_IDENTIFIERASSTRING;
import static EDU.purdue.jtb.parser.syntaxtree.JTBParserNodeConstants.JTB_SIG_INTEGERLITERAL;
import static EDU.purdue.jtb.parser.syntaxtree.JTBParserNodeConstants.JTB_SIG_JAVACCOPTIONS;
import static EDU.purdue.jtb.parser.syntaxtree.JTBParserNodeConstants.JTB_SIG_JAVACODEPRODUCTION;
import static EDU.purdue.jtb.parser.syntaxtree.JTBParserNodeConstants.JTB_SIG_LOCALLOOKAHEAD;
import static EDU.purdue.jtb.parser.syntaxtree.JTBParserNodeConstants.JTB_SIG_OPTIONBINDING;
import static EDU.purdue.jtb.parser.syntaxtree.JTBParserNodeConstants.JTB_SIG_PRODUCTION;
import static EDU.purdue.jtb.parser.syntaxtree.JTBParserNodeConstants.JTB_SIG_REGEXPRKIND;
import static EDU.purdue.jtb.parser.syntaxtree.JTBParserNodeConstants.JTB_SIG_REGEXPRSPEC;
import static EDU.purdue.jtb.parser.syntaxtree.JTBParserNodeConstants.JTB_SIG_REGULAREXPRESSION;
import static EDU.purdue.jtb.parser.syntaxtree.JTBParserNodeConstants.JTB_SIG_REGULAREXPRPRODUCTION;
import static EDU.purdue.jtb.parser.syntaxtree.JTBParserNodeConstants.JTB_SIG_STRINGLITERAL;
import static EDU.purdue.jtb.parser.syntaxtree.JTBParserNodeConstants.JTB_SIG_TOKENMANAGERDECLS;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import EDU.purdue.jtb.analyse.GlobalDataBuilder;
import EDU.purdue.jtb.common.JTBOptions;
import EDU.purdue.jtb.common.JavaBranchPrinter;
import EDU.purdue.jtb.common.Messages;
import EDU.purdue.jtb.common.ProgrammaticError;
import EDU.purdue.jtb.common.Spacing;
import EDU.purdue.jtb.common.UnicodeConverter;
import EDU.purdue.jtb.parser.Token;
import EDU.purdue.jtb.parser.syntaxtree.AccessModifier;
import EDU.purdue.jtb.parser.syntaxtree.BNFProduction;
import EDU.purdue.jtb.parser.syntaxtree.CharacterDescriptor;
import EDU.purdue.jtb.parser.syntaxtree.CharacterList;
import EDU.purdue.jtb.parser.syntaxtree.ComplexRegularExpression;
import EDU.purdue.jtb.parser.syntaxtree.ComplexRegularExpressionChoices;
import EDU.purdue.jtb.parser.syntaxtree.ComplexRegularExpressionUnit;
import EDU.purdue.jtb.parser.syntaxtree.Expansion;
import EDU.purdue.jtb.parser.syntaxtree.ExpansionChoices;
import EDU.purdue.jtb.parser.syntaxtree.ExpansionUnit;
import EDU.purdue.jtb.parser.syntaxtree.ExpansionUnitTCF;
import EDU.purdue.jtb.parser.syntaxtree.INode;
import EDU.purdue.jtb.parser.syntaxtree.IdentifierAsString;
import EDU.purdue.jtb.parser.syntaxtree.IntegerLiteral;
import EDU.purdue.jtb.parser.syntaxtree.JavaCCOptions;
import EDU.purdue.jtb.parser.syntaxtree.JavaCodeProduction;
import EDU.purdue.jtb.parser.syntaxtree.LocalLookahead;
import EDU.purdue.jtb.parser.syntaxtree.NodeChoice;
import EDU.purdue.jtb.parser.syntaxtree.NodeListOptional;
import EDU.purdue.jtb.parser.syntaxtree.NodeOptional;
import EDU.purdue.jtb.parser.syntaxtree.NodeSequence;
import EDU.purdue.jtb.parser.syntaxtree.NodeToken;
import EDU.purdue.jtb.parser.syntaxtree.OptionBinding;
import EDU.purdue.jtb.parser.syntaxtree.Production;
import EDU.purdue.jtb.parser.syntaxtree.RegExprKind;
import EDU.purdue.jtb.parser.syntaxtree.RegExprSpec;
import EDU.purdue.jtb.parser.syntaxtree.RegularExprProduction;
import EDU.purdue.jtb.parser.syntaxtree.RegularExpression;
import EDU.purdue.jtb.parser.syntaxtree.StringLiteral;
import EDU.purdue.jtb.parser.syntaxtree.TokenManagerDecls;
import EDU.purdue.jtb.parser.visitor.DepthFirstVoidVisitor;
import EDU.purdue.jtb.parser.visitor.signature.NodeFieldsSignature;

/**
 * The {@link JavaCCPrinter} visitor reprints (with indentation) the JavaCC grammar's JavaCC specific
 * productions.<br>
 * <p>
 * Implementation notes : CODEJAVA
 * <ul>
 * <li>sb.append(spc.spc), sb.append(' ') and sb.append(LS) are done at the highest (calling) level (except
 * for Modifiers which prints its last space as it can be empty)
 * <li>sb.append(spc.spc) is normally done after sb.append(LS)
 * <li>sb.append(' ') is not merged with printing punctuation / operators / keywords (to prepare evolutions
 * for other formatting preferences)
 * </ul>
 * <p>
 * Visitor maintains state (for a grammar), and not supposed to be run in parallel threads (on the same
 * grammar).
 * </p>
 * TODO extract / refactor methods for custom formatting<br>
 *
 * @author Marc Mazas
 * @version 1.4.0 : 05-08/2009 : MMa : adapted to JavaCC v4.2 grammar and JDK 1.5<br>
 *          1.4.0 : 11/2009 : MMa : output commented JTB options
 * @version 1.4.7 : 12/2011 : MMa : added comments in the JavaCodeProduction visit method<br>
 *          1.4.7 : 07/2012 : MMa : followed changes in jtbgram.jtb (AccessModifier(),
 *          IndentifierAsString())<br>
 *          1.4.7 : 09/2012 : MMa : added non node creation
 * @version 1.4.8 : 10/2012 : MMa : updated for JavaCodeProduction class generation if requested 1.4.8 :
 *          12/2014 : MMa : fixed commenting specials in JTB options ;<br>
 *          improved specials printing
 * @version 1.5.0 : 01-06/2017 : MMa : used try-with-resource ; fixed processing of nodes not to be created ;
 *          simplified special tokens processing ; fixed lines indentations and newlines ; fixed missing 'ni =
 *          "xxx"' when JTB_TK=false ; fixed specials printing ; added final in ExpansionUnitTCF's catch
 * @version 1.5.1 : 08/2023 : MMa : added optional annotations in catch of ExpansionUnitTCF<br>
 *          1.5.1 : 08/2023 : MMa : editing changes for coverage analysis; changes due to the NodeToken
 *          replacement by Token<br>
 *          1.5.1 : 09/2023 : MMa : removed noDebugComment flag
 * @version 1.5.3 : 11/2025 : MMa : signature code made independent of parser
 */
class JavaCCPrinter extends DepthFirstVoidVisitor {

  /** The {@link GlobalDataBuilder} visitor */
  protected final GlobalDataBuilder   gdbv;
  /** The {@link CommonCodeGenerator} */
  protected final CommonCodeGenerator ccg;
  /** The global JTB options (not thread safe but used only in read-access) */
  JTBOptions                          jopt;
  /** The (current) buffer to print into */
  protected StringBuilder             sb;
  /** The indentation object */
  protected Spacing                   spc;
  /** The {@link JavaBranchPrinter} printer to print a java node and its subtree */
  protected JavaBranchPrinter         jbp;
  /** The generated variable assignment string to be inserted */
  protected String                    gvaStr;
  /**
   * The "BNF" nesting level: incremented/decremented:<br>
   * <ul>
   * <li>for each new nested {@link ExpansionChoices} (so starts at 0 (in {@link #visit(BNFProduction)})), and
   * <li>for each new nested {@link Expansion} except in an {@link ExpansionChoices} with no choices
   * </ul>
   * Used to control spaces / new lines.
   */
  protected int                       bnfLvl            = 0;
  /** The OS line separator */
  static final String                 LS                = System.getProperty("line.separator");
  /** The node class debug comment prefix */
  String                              JJNCDCP           = " //jccp ";
  /** The flag telling that the grammar contains the JavaCC option TOKEN_EXTENDS */
  boolean                             foundTokenExtends = false;

  /*
   * Constructors
   */

  /**
   * Constructor with a global data builder visitor reference, a given buffer and indentation.
   *
   * @param aGdbv - the {@link GlobalDataBuilder} visitor
   * @param aCcg - the {@link CommonCodeGenerator}
   * @param aSb - the buffer to print into (will be allocated if null)
   * @param aSPC - the Spacing indentation object (will be allocated and set to a default if null)
   */
  JavaCCPrinter(final GlobalDataBuilder aGdbv, final CommonCodeGenerator aCcg, final StringBuilder aSb,
      final Spacing aSPC) {
    gdbv = aGdbv;
    ccg = aCcg;
    jopt = aGdbv.jopt;
    sb = aSb;
    if (sb == null) {
      sb = new StringBuilder(16 * 1024);
    }
    spc = aSPC;
    if (spc == null) {
      spc = new Spacing(INDENT_AMT);
    }
    jbp = new JavaBranchPrinter(aGdbv.jopt, spc);
  }

  /**
   * Constructor which will allocate a default buffer and indentation.
   *
   * @param aGdbv - the {@link GlobalDataBuilder} visitor
   * @param aCcg - the {@link CommonCodeGenerator}
   */
  JavaCCPrinter(final GlobalDataBuilder aGdbv, final CommonCodeGenerator aCcg) {
    this(aGdbv, aCcg, null, null);
  }

  /*
   * Convenience methods
   */

  /**
   * Prints into the current buffer a node class debug comment and a new line.
   *
   * @param n - the node for the node class comment
   */
  void oneNewLine(final INode n) {
    sb.append(nodeClassComment(n)).append(LS);
  }

  /**
   * Prints into the current buffer a node class debug comment, an extra given debug comment, and a new line.
   *
   * @param n - the node for the node class comment
   * @param str - the extra comment
   */
  void oneNewLine(final INode n, final String str) {
    sb.append(nodeClassComment(n, str)).append(LS);
  }

  /**
   * Prints into the current buffer a full debug class block comment line with an indentation, a node class
   * comment, extra given comments, and a new line.
   *
   * @param n - the node for the node class comment
   * @param str - the extra comment
   */
  void oneDebugClassNewLine(final INode n, final Object... str) {
    if (DEBUG_CLASS) {
      sb.append(spc.spc).append("/*").append(nodeClassComment(n, str)).append(" */").append(LS);
    }
  }

  /**
   * Prints into the current buffer a full debug class block comment line with an indentation, a node class
   * comment, extra given comments, and a new line.
   *
   * @param aSb - the buffer to print into
   * @param n - the node for the node class comment
   * @param str - the extra comment
   */
  void oneDebugClassNewLine(final StringBuilder aSb, final INode n, final Object... str) {
    if (DEBUG_CLASS) {
      aSb.append(spc.spc).append("/*").append(nodeClassComment(n, str)).append(" */").append(LS);
    }
  }

  /**
   * Prints into a given buffer a node class debug comment, an extra given debug comment, and a new line.
   *
   * @param aSb - the buffer to print into
   * @param n - the node for the node class comment
   * @param str - the extra comment
   */
  void oneNewLine(final StringBuilder aSb, final INode n, final String str) {
    aSb.append(nodeClassComment(n, str)).append(LS);
  }

  /**
   * Prints into the current buffer a node class debug comment, extra given debug comments, and a new line.
   *
   * @param n - the node for the node class comment
   * @param str - the extra comments
   */
  void oneNewLine(final INode n, final Object... str) {
    sb.append(nodeClassComment(n, str)).append(LS);
  }

  /**
   * Prints twice into the current buffer a node class comment and a new line.
   *
   * @param n - the node for the node class comment
   */
  void twoNewLines(final INode n) {
    oneNewLine(n);
    oneNewLine(n);
  }

  /**
   * Returns a node class comment with extra comments (a //jcp followed by the node class short name plus the
   * extra comment if global flag set, nothing otherwise).
   *
   * @param n - the node for the node class comment
   * @param obj - the extra comments
   * @return the node class comment
   */
  private String nodeClassComment(final INode n, final Object... obj) {
    if (DEBUG_CLASS) {
      int len = 0;
      for (final Object o : obj) {
        len += o.toString().length();
      }
      final StringBuilder buf = new StringBuilder(len);
      for (final Object o : obj) {
        buf.append(o.toString());
      }
      return nodeClassComment(n, buf.toString());
    } else {
      return "";
    }
  }

  /**
   * Returns a node class comment with an extra comment (a //jcp followed by the node class short name plus
   * the extra comment if global flag set, nothing otherwise).
   *
   * @param n - the node for the node class comment
   * @param str - the extra comment
   * @return the node class comment
   */
  protected String nodeClassComment(final INode n, final String str) {
    if (DEBUG_CLASS) {
      return nodeClassComment(n).concat(" ").concat(str);
    } else {
      return "";
    }
  }

  /**
   * Returns a node class comment (a //jcp followed by the node class short name if global flag set, nothing
   * otherwise).
   *
   * @param n - the node for the node class comment
   * @return the node class comment
   */
  protected String nodeClassComment(final INode n) {
    if (DEBUG_CLASS) {
      final String s = n.toString();
      final int b = s.lastIndexOf('.') + 1;
      final int e = s.indexOf('@');
      if ((b == -1) //
          || (e == -1)) {
        // coverage: this case is only if a JTBParser's node's toString() has been overriden, which happens
        // only in Token, where this method is not called; we keep this branch for potential future use
        return JJNCDCP + s;
      } else {
        return JJNCDCP + s.substring(b, e);
      }
    } else {
      return "";
    }
  }

  /**
   * Saves the current buffer to an output file.
   *
   * @param outFile - the output file
   * @return OK_RC or FILE_EXISTS_RC
   * @throws IOException if IO problem
   */
  public final int saveToFile(final String outFile) throws IOException {
    final File file = new File(outFile);
    if (gdbv.jopt.noOverwrite //
        && file.exists()) {
      // coverage: our build.xml testcases deletes the outFiles first, so this case does not happen
      return FILE_EXISTS_RC;
    }
    try (PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(file), sb.length()))) {
      pw.print(sb);
      return OK_RC;
    } catch (final IOException e) {
      Messages.hardErr("IOException on " + file.getPath(), e);
      throw e;
    }
  }

  /**
   * Generates a java node and its subtree with a JavaPrinter.
   *
   * @param n - the node to process
   * @return a buffer with the generated source
   */
  protected final StringBuilder genJavaBranch(final INode n) {
    return jbp.genJavaBranch(n);
  }

  /*
   * Base classes visit methods
   */

  /**
   * Prints into the current buffer a Token image and its specials before if global flag set.
   *
   * @param n - the node to visit
   */
  @Override
  public void visit(final NodeToken n) {
    if (gdbv.jopt.printSpecialTokensJJ) {
      sb.append(n.withSpecials(spc.spc, gvaStr));
    } else {
      if (gvaStr != null) {
        sb.append(gvaStr);
      }
      sb.append(((Token) n).image);
    }
    gvaStr = null;
  }

  /**
   * Visits a {@link JavaCCOptions} node, whose child is the following :
   * <p>
   * f0 -> [ #0 "options" #1 "{"<br>
   * .. .. . #2 ( OptionBinding() )*<br>
   * .. .. . #3 "}" ]<br>
   * s: -1270729337<br>
   *
   * @param n - the node to visit
   */
  @Override
  @NodeFieldsSignature(old_sig = -1270729337, new_sig = JTB_SIG_JAVACCOPTIONS, name = "JavaCCOptions")
  public void visit(final JavaCCOptions n) {
    if (n.f0.present()) {
      final NodeSequence seq = (NodeSequence) n.f0.node;
      // #0 "options"
      seq.elementAt(0).accept(this);
      oneNewLine(n, "a");
      // #1 "{"
      sb.append(spc.spc);
      seq.elementAt(1).accept(this);
      // #2 ( OptionBinding() )*
      final NodeListOptional nlo = (NodeListOptional) seq.elementAt(2);
      if (nlo.present()) {
        spc.updateSpc(+1);
        oneNewLine(n, "b");
        for (final Iterator<INode> e = nlo.elements(); e.hasNext();) {
          e.next().accept(this);
          if (e.hasNext()) {
            oneNewLine(n, "c");
          }
        }
        spc.updateSpc(-1);
        oneNewLine(n, "d");
      }
      if (!foundTokenExtends) {
        spc.updateSpc(+1);
        oneNewLine(n, "e");
        sb.append(spc.spc).append("TOKEN_EXTENDS = \"");
        if (jopt.nodesPkgName != null) {
          sb.append(jopt.nodesPkgName).append(".");
        }
        sb.append(nodeToken).append("\"; // added by JTB");
        spc.updateSpc(-1);
        oneNewLine(n, "f");
      }
      // #3 "}"
      sb.append(spc.spc);
      seq.elementAt(3).accept(this);
      oneNewLine(n, "g");
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
   * s: -1998174573<br>
   *
   * @param n - the node to visit
   */
  @Override
  @NodeFieldsSignature(old_sig = -1998174573, new_sig = JTB_SIG_OPTIONBINDING, name = "OptionBinding")
  public void visit(final OptionBinding n) {
    // f0 -> ( %0 < IDENTIFIER > | %1 "LOOKAHEAD" | %2 "IGNORE_CASE" | %3 "static" )
    final Token nt0 = (Token) n.f0.choice;
    sb.append(spc.spc);
    if (n.f0.which == 0) {
      if (nt0.image.startsWith("JTB_")) {
        // f0 -> %0 < IDENTIFIER >
        // comment JTB options ; to easily handle possible previous special tokens,
        // just temporarily change (comment) the token image, print everything and restore the image
        final String tkim = nt0.image;
        nt0.image = "// " + tkim;
        nt0.accept(this);
        nt0.image = tkim;
      } else if (nt0.image.equals("TOKEN_EXTENDS")) {
        jopt.mess.warning(
            "Existing TOKEN_EXTENDS option, so JTB will not generate its default one (to NodeToken); "
                + "ensure that this class extends / replaces properly NodeToken",
            nt0.beginLine, nt0.beginColumn);
        nt0.accept(this);
        foundTokenExtends = true;
      } else if (nt0.image.equals("TOKEN_FACTORY")) {
        jopt.mess.warning(
            "Existing TOKEN_FACTORY option; "
                + "ensure that this class implements proper Token newToken(...)",
            nt0.beginLine, nt0.beginColumn);
        nt0.accept(this);
      } else {
        nt0.accept(this);
      }
    } else {
      nt0.accept(this);
    }
    sb.append(' ');
    // f1 -> "="
    n.f1.accept(this);
    sb.append(' ');
    // f2 -> ( %0 IntegerLiteral() | %1 BooleanLiteral() | %2 StringLiteral() )
    n.f2.accept(this);
    // f3 -> ";"
    n.f3.accept(this);
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
    // no difference with super class
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
   * f6 -> [ "%" ]<br>
   * f7 -> Block()<br>
   * s: -763138104<br>
   *
   * @param n - the node to visit
   */
  @Override
  @NodeFieldsSignature(old_sig = -763138104, new_sig = JTB_SIG_JAVACODEPRODUCTION, name = "JavaCodeProduction")
  public void visit(final JavaCodeProduction n) {
    // f0 -> "JAVACODE"
    n.f0.accept(this);
    sb.append(' ');
    // f1 -> AccessModifier()
    n.f1.accept(this);
    // f2 -> ResultType()
    sb.append(genJavaBranch(n.f2));
    sb.append(' ');
    // f3 -> IdentifierAsString()
    n.f3.accept(this);
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
    oneNewLine(n, "0");
    sb.append(spc.spc);
    // f6 -> [ "%" ]
    // print it in a block comment
    if (n.f6.present()) {
      // coverage: this branch should never happen as we come from JJFileAnnotator visit(JavaCodeProduction)
      // where the opposite test that leads to JavaCCPrinter results to true; but we keep this test for safety
      sb.append(" /*%*/ ");
    }
    // f7 -> Block()
    sb.append(genJavaBranch(n.f7));
    oneNewLine(n);
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
    bnfLvl = -1;
    // f0 -> AccessModifier()
    n.f0.accept(this);
    // f1 -> ResultType()
    sb.append(genJavaBranch(n.f1));
    sb.append(' ');
    // f2 -> IdentifierAsString()
    final String ident = ((Token) n.f2.f0).image;
    // must be prefixed / suffixed
    final String idfn = gdbv.getFixedName(ident);
    sb.append(idfn);
    sb.append(' ');
    // f3 -> FormalParameters()
    sb.append(genJavaBranch(n.f3));
    // f4 -> [ #0 "throws" #1 Name() #2 ( $0 "," $1 Name() )* ]
    if (n.f4.present()) {
      final NodeSequence seq = (NodeSequence) n.f4.node;
      sb.append(' ');
      // #0 "throws"
      seq.elementAt(0).accept(this);
      sb.append(' ');
      // #1 Name()
      sb.append(genJavaBranch(seq.elementAt(1)));
      // #2 ( $0 "," $1 Name() )* ]
      final NodeListOptional opt = (NodeListOptional) seq.elementAt(2);
      if (opt.present()) {
        for (final INode e : opt.nodes) {
          final NodeSequence seq1 = (NodeSequence) e;
          // $0 ","
          seq1.elementAt(0).accept(this);
          sb.append(' ');
          // $1 Name()
          sb.append(genJavaBranch(seq1.elementAt(1)));
        }
      }
    }
    // f5 -> [ "!" ]
    // print it in a block comment
    if (n.f5.present()) {
      // coverage: the other branch should never happen as we come from JJFileAnnotator visit(BNFProduction)
      // where the same test that leads to JavaCCPrinter results to true; but we keep this test for safety
      sb.append(" /*!*/ ");
    }
    // f6 -> ":"
    n.f6.accept(this);
    oneNewLine(n);
    sb.append(spc.spc);
    // f7 -> Block()
    sb.append(genJavaBranch(n.f7));
    oneNewLine(n);
    sb.append(spc.spc);
    // f8 -> "{"
    n.f8.accept(this);
    oneNewLine(n);
    spc.updateSpc(+1);
    sb.append(spc.spc);
    // f9 -> ExpansionChoices()
    ++bnfLvl;
    n.f9.accept(this);
    --bnfLvl;
    oneNewLine(n);
    spc.updateSpc(-1);
    sb.append(spc.spc);
    // f10 -> "}"
    n.f10.accept(this);
    oneNewLine(n);
  }

  /**
   * Visits a {@link AccessModifier} node, whose child is the following :
   * <p>
   * f0 -> ( %0 "public"<br>
   * .. .. | %1 "protected"<br>
   * .. .. | %2 "private"<br>
   * .. .. | %3 "final" )*<br>
   * s: -1053437682<br>
   *
   * @param n - the node to visit
   */
  @Override
  @NodeFieldsSignature(old_sig = -1053437682, new_sig = JTB_SIG_ACCESSMODIFIER, name = "AccessModifier")
  public void visit(final AccessModifier n) {
    if (n.f0.present()) {
      for (final INode e : n.f0.nodes) {
        ((NodeChoice) e).accept(this);
        sb.append(' ');
      }
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
    // f0 -> [ %0 #0 "<" #1 "*" #2 ">" | %1 #0 "<" #1 < IDENTIFIER > #2 ( $0 "," $1 < IDENTIFIER > )* #3 ">" ]
    if (n.f0.present()) {
      n.f0.node.accept(this);
      oneNewLine(n);
    }
    // f1 -> RegExprKind()
    n.f1.accept(this);
    // f2 -> [ #0 "[" #1 "IGNORE_CASE" #2 "]" ]
    if (n.f2.present()) {
      sb.append(' ');
      n.f2.node.accept(this);
    }
    sb.append(' ');
    // f3 -> ":"
    n.f3.accept(this);
    oneNewLine(n);
    // f4 -> "{"
    n.f4.accept(this);
    spc.updateSpc(+1);
    oneNewLine(n);
    sb.append(spc.spc);
    // f5 -> RegExprSpec()
    n.f5.accept(this);
    // f6 -> ( #0 "|" #1 RegExprSpec() )*
    if (n.f6.present()) {
      for (final INode e : n.f6.nodes) {
        final NodeSequence seq = (NodeSequence) e;
        oneNewLine(n);
        // #0 "|"
        seq.elementAt(0).accept(this);
        sb.append(' ');
        // #1 RegExprSpec()
        seq.elementAt(1).accept(this);
      }
    }
    spc.updateSpc(-1);
    oneNewLine(n);
    sb.append(spc.spc);
    // f7 -> "}"
    n.f7.accept(this);
    oneNewLine(n);
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
  public void visit(final TokenManagerDecls n) {
    // f0 -> "TOKEN_MGR_DECLS"
    n.f0.accept(this);
    sb.append(' ');
    // f1 -> ":"
    n.f1.accept(this);
    sb.append(' ');
    // f2 -> ClassOrInterfaceBody()
    sb.append(genJavaBranch(n.f2));
    oneNewLine(n);
  }

  /**
   * Visits a {@link RegExprKind} node, whose child is the following :
   * <p>
   * f0 -> . %0 "TOKEN"<br>
   * .. .. | %1 "SPECIAL_TOKEN"<br>
   * .. .. | %2 "SKIP"<br>
   * .. .. | %3 "MORE"<br>
   * s: -1874441621<br>
   *
   * @param n - the node to visit
   */
  @Override
  @NodeFieldsSignature(old_sig = -1874441621, new_sig = JTB_SIG_REGEXPRKIND, name = "RegExprKind")
  public void visit(final RegExprKind n) {
    // no difference with superclass
    n.f0.accept(this);
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
    // f0 -> RegularExpression()
    n.f0.accept(this);
    // f1 -> [ "!" ]
    // print it in a block comment
    if (n.f1.present()) {
      sb.append(" /*!*/");
    }
    // f2 -> [ Block() ]
    if (n.f2.present()) {
      oneNewLine(n);
      spc.updateSpc(+1);
      sb.append(spc.spc);
      sb.append(genJavaBranch(n.f2.node));
      spc.updateSpc(-1);
    }
    // f3 -> [ #0 ":" #1 < IDENTIFIER > ]
    if (n.f3.present()) {
      final NodeSequence seq = (NodeSequence) n.f3.node;
      sb.append(' ');
      // #0 ":"
      sb.append(seq.elementAt(0));
      sb.append(' ');
      // #1 < IDENTIFIER >
      sb.append(seq.elementAt(1));
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
  @NodeFieldsSignature(old_sig = -1726831935, new_sig = JTB_SIG_EXPANSIONCHOICES, name = "ExpansionChoices")
  public void visit(final ExpansionChoices n) {
    if (!n.f1.present()) {
      // f0 -> Expansion()
      n.f0.accept(this);
    } else {
      // f0 -> Expansion()
      ++bnfLvl;
      n.f0.accept(this);
      --bnfLvl;
      // f1 -> ( #0 "|" #1 Expansion() )*
      for (final INode e : n.f1.nodes) {
        final NodeSequence seq = (NodeSequence) e;
        oneNewLine(n, "exp");
        // found only inside a LOOKAHEAD
        spc.updateSpc(+4);
        sb.append(spc.spc);
        // #0 "|"
        sb.append(' ');
        seq.elementAt(0).accept(this);
        sb.append(' ');
        // #1 Expansion()
        ++bnfLvl;
        seq.elementAt(1).accept(this);
        --bnfLvl;
        spc.updateSpc(-4);
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
    if (n.f0.present()) {
      final NodeSequence seq = (NodeSequence) n.f0.node;
      // #0 "LOOKAHEAD"
      seq.elementAt(0).accept(this);
      sb.append(' ');
      // #1 "("
      seq.elementAt(1).accept(this);
      sb.append(' ');
      // #2 LocalLookahead()
      seq.elementAt(2).accept(this);
      sb.append(' ');
      // #3 ")"
      seq.elementAt(3).accept(this);
      oneNewLine(n, "la");
      sb.append(spc.spc);
    }
    // f1 -> ( ExpansionUnit() )+
    for (final Iterator<INode> e = n.f1.elements(); e.hasNext();) {
      // ExpansionUnit()
      e.next().accept(this);
      if (e.hasNext()) {
        sb.append(' ');
        // oneNewLine(n, "eu");
        // sb.append(spc.spc);
      }
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
    // f0 -> [ IntegerLiteral() ]
    if (n.f0.present()) {
      n.f0.accept(this);
    }
    // f1 -> [ "," ]
    if (n.f1.present()) {
      n.f1.accept(this);
      sb.append(' ');
    }
    // f2 -> [ ExpansionChoices() ]
    if (n.f2.present()) {
      // ++bnfLvl;
      n.f2.node.accept(this);
      // --bnfLvl;
    }
    // f3 -> [ "," ]
    if (n.f3.present()) {
      n.f3.accept(this);
      sb.append(' ');
    }
    // f4 -> [ #0 "{" #1 Expression() #2 "}" ]
    if (n.f4.present()) {
      final NodeSequence seq = (NodeSequence) n.f4.node;
      // #0 "{"
      seq.elementAt(0).accept(this);
      sb.append(' ');
      // #1 Expression()
      sb.append(genJavaBranch(seq.elementAt(1)));
      sb.append(' ');
      // #2 "}"
      seq.elementAt(2).accept(this);
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
    NodeOptional opt;
    NodeChoice ch;
    switch (n.f0.which) {
    case 0:
      // %0 #0 "LOOKAHEAD" #1 "(" #2 LocalLookahead() #3 ")"
      // #0 "LOOKAHEAD"
      seq = (NodeSequence) n.f0.choice;
      seq.elementAt(0).accept(this);
      sb.append(' ');
      // #1 "("
      seq.elementAt(1).accept(this);
      sb.append(' ');
      // #2 LocalLookahead()
      seq.elementAt(2).accept(this);
      sb.append(' ');
      // #3 ")"
      seq.elementAt(3).accept(this);
      oneNewLine(n, "la");
      break;

    case 1:
      // %1 Block()
      sb.append(genJavaBranch(n.f0.choice));
      break;

    case 2:
      // %2 #0 "[" #1 ExpansionChoices() #2 "]"
      seq = (NodeSequence) n.f0.choice;
      // #0 "["
      seq.elementAt(0).accept(this);
      oneNewLine(n, "[");
      spc.updateSpc(+1);
      // %#1 ExpansionChoices()
      sb.append(spc.spc);
      ++bnfLvl;
      seq.elementAt(1).accept(this);
      --bnfLvl;
      oneNewLine(n, "ec2");
      // #2 "]"
      spc.updateSpc(-1);
      sb.append(spc.spc);
      seq.elementAt(2).accept(this);
      oneNewLine(n, "]");
      break;

    case 3:
      // %3 ExpansionUnitTCF()
      n.f0.choice.accept(this);
      break;

    case 4:
      // %4 #0 [ $0 PrimaryExpression() $1 "=" ]
      // .. #1 ( &0 $0 IdentifierAsString() $1 Arguments()
      // .. .. . .. $2 [ "!" ]
      // .. .. | &1 $0 RegularExpression()
      // .. .. . .. $1 [ ?0 "." ?1 < IDENTIFIER > ] )
      // .. .. . .. $2 [ "!" ] )
      seq = (NodeSequence) n.f0.choice;
      // #0 [ $0 PrimaryExpression() $1 "=" ]
      opt = (NodeOptional) seq.elementAt(0);
      if (opt.present()) {
        // $0 PrimaryExpression()
        ((NodeSequence) opt.node).elementAt(0).accept(this);
        sb.append(' ');
        // $1 "="
        ((NodeSequence) opt.node).elementAt(1).accept(this);
        sb.append(' ');
      }
      // #1 ( &0 $0 IdentifierAsString() $1 Arguments()
      // .. .. . $2 [ "!" ]
      // .. | &1 $0 RegularExpression()
      // .. . .. $1 [ ?0 "." ?1 < IDENTIFIER > ]
      // .. . .. $2 [ "!" ] )
      ch = (NodeChoice) seq.elementAt(1);
      final NodeSequence seq1 = (NodeSequence) ch.choice;
      if (ch.which == 0) {
        // $0 IdentifierAsString()
        final String ias = ((Token) ((IdentifierAsString) seq1.elementAt(0)).f0).image;
        sb.append(gdbv.getFixedName(ias));
        // $1 Arguments()
        sb.append(genJavaBranch(seq1.elementAt(1)));
        // $2 [ "!" ]
        // print it in a block comment
        if (((NodeOptional) seq1.elementAt(2)).present()) {
          sb.append(" /*!*/ ");
        }
      } else {
        // $0 RegularExpression()
        seq1.elementAt(0).accept(this);
        // $1 [ ?0 "." ?1 < IDENTIFIER > ]
        final NodeOptional opt1 = (NodeOptional) seq1.elementAt(1);
        if (opt1.present()) {
          ((Token) ((NodeSequence) opt1.node).elementAt(0)).accept(this);
          ((Token) ((NodeSequence) opt1.node).elementAt(1)).accept(this);
        }
        // $2 [ "!" ]
        // print it in a block comment
        if (((NodeOptional) seq1.elementAt(2)).present()) {
          sb.append(" /*!*/ ");
        }
      }
      break;

    case 5:
      // %5 #0 "(" #1 ExpansionChoices() #2 ")"
      // .. #3 ( &0 "+" | &1 "*" | &2 "?" )?
      seq = (NodeSequence) n.f0.choice;
      // #0 "("
      seq.elementAt(0).accept(this);
      oneNewLine(n, "(");
      spc.updateSpc(+1);
      // #1 ExpansionChoices()
      sb.append(spc.spc);
      ++bnfLvl;
      seq.elementAt(1).accept(this);
      --bnfLvl;
      oneNewLine(n, "ec5");
      // #2 ")"
      spc.updateSpc(-1);
      sb.append(spc.spc);
      seq.elementAt(2).accept(this);
      // #3 ( &0 "+" | &1 "*" | &2 "?" )?
      opt = (NodeOptional) seq.elementAt(3);
      if (opt.present()) {
        opt.node.accept(this);
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
  @NodeFieldsSignature(old_sig = 1601707097, new_sig = JTB_SIG_EXPANSIONUNITTCF, name = "ExpansionUnitTCF")
  public void visit(final ExpansionUnitTCF n) {
    // f0 -> "try"
    n.f0.accept(this);
    sb.append(' ');
    // f1 -> "{"
    n.f1.accept(this);
    oneNewLine(n, "{");
    spc.updateSpc(+1);
    sb.append(spc.spc);
    // f2 -> ExpansionChoices()
    ++bnfLvl;
    n.f2.accept(this);
    --bnfLvl;
    oneNewLine(n, "ec");
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
        // #0 "catch"
        sb.append(' ');
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
        sb.append(genJavaBranch(seq.elementAt(4)));
        sb.append(' ');
        // #5 < IDENTIFIER >
        sb.append(((Token) seq.elementAt(5)).image);
        // #6 ")"
        seq.elementAt(6).accept(this);
        sb.append(' ');
        // #7 Block()
        sb.append(genJavaBranch(seq.elementAt(7)));
      }
    }
    // f5 -> [ #0 "finally" #1 Block() ]
    if (n.f5.present()) {
      // #0 "finally"
      sb.append(" ");
      ((NodeSequence) n.f5.node).elementAt(0).accept(this);
      sb.append(' ');
      // #1 Block()
      sb.append(genJavaBranch(((NodeSequence) n.f5.node).elementAt(1)));
      oneNewLine(n);
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
  @NodeFieldsSignature(old_sig = 1719627151, new_sig = JTB_SIG_REGULAREXPRESSION, name = "RegularExpression")
  public void visit(final RegularExpression n) {
    if (n.f0.which == 0) {
      // %0 StringLiteral()
      n.f0.choice.accept(this);
    } else if (n.f0.which == 1) {
      // %1 #0 "<" #1 [ $0 [ "#" ] $1 IdentifierAsString() $2 ":" ]
      // #2 ComplexRegularExpressionChoices() #3 ">"
      final NodeSequence seq = (NodeSequence) n.f0.choice;
      final ComplexRegularExpressionChoices crec = (ComplexRegularExpressionChoices) seq.elementAt(2);
      // #0 "<"
      seq.elementAt(0).accept(this);
      sb.append(' ');
      // #1 [ $0 [ "#" ] $1 IdentifierAsString() $2 ":" ]
      final NodeOptional opt = (NodeOptional) seq.elementAt(1);
      if (opt.present()) {
        final NodeSequence seq1 = (NodeSequence) opt.node;
        // $0 [ "#" ]
        if (((NodeOptional) seq1.elementAt(0)).present()) {
          seq1.elementAt(0).accept(this);
        }
        // $1 IdentifierAsString()
        seq1.elementAt(1).accept(this);
        sb.append(' ');
        // $2 ":"
        seq1.elementAt(2).accept(this);
        if (crec.f1.present()) {
          oneNewLine(n, "1a");
          spc.updateSpc(+1);
          sb.append(spc.spc).append("  ");
        } else {
          sb.append(' ');
        }
      }
      // #2 ComplexRegularExpressionChoices(c)
      crec.accept(this);
      // #3 ">"
      if (crec.f1.present()) {
        oneNewLine(n, "1b");
        spc.updateSpc(-1);
      } else {
        sb.append(' ');
      }
      seq.elementAt(3).accept(this);
    } else {
      // %2 #0 "<" #1 IdentifierAsString() #2 ">" OR %3 #0 "<" #1 "EOF" #2 ">"
      // #0 "<"
      ((NodeSequence) n.f0.choice).elementAt(0).accept(this);
      sb.append(' ');
      // #1 IdentifierAsString() OR #1 "EOF"
      ((NodeSequence) n.f0.choice).elementAt(1).accept(this);
      sb.append(' ');
      // #2 ">"
      ((NodeSequence) n.f0.choice).elementAt(2).accept(this);
    }
  }

  /**
   * Visits a {@link ComplexRegularExpressionChoices} node, whose children are the following :
   * <p>
   * f0 -> ComplexRegularExpression()<br>
   * f1 -> ( #0 "|" #1 ComplexRegularExpression() )*<br>
   * s: -1240933595<br>
   *
   * @param n - the node to visit
   */
  @Override
  @NodeFieldsSignature(old_sig = -1240933595, new_sig = JTB_SIG_COMPLEXREGULAREXPRESSIONCHOICES, name = "ComplexRegularExpressionChoices")
  public void visit(final ComplexRegularExpressionChoices n) {
    // f0 -> ComplexRegularExpression()
    n.f0.accept(this);
    // f1 -> ( #0 "|" #1 ComplexRegularExpression() )*
    if (n.f1.present()) {
      for (final INode e : n.f1.nodes) {
        final NodeSequence seq = (NodeSequence) e;
        // #0 "|"
        oneNewLine(n, "|");
        sb.append(spc.spc);
        seq.elementAt(0).accept(this);
        sb.append(' ');
        // #1 ComplexRegularExpression()
        seq.elementAt(1).accept(this);
      }
    }
  }

  /**
   * Visits a {@link ComplexRegularExpression} node, whose child is the following :
   * <p>
   * f0 -> ( ComplexRegularExpressionUnit() )+<br>
   * s: 896313544<br>
   *
   * @param n - the node to visit
   */
  @Override
  @NodeFieldsSignature(old_sig = 896313544, new_sig = JTB_SIG_COMPLEXREGULAREXPRESSION, name = "ComplexRegularExpression")
  public void visit(final ComplexRegularExpression n) {
    // f0 -> ( ComplexRegularExpressionUnit() )+
    for (int i = 0; i < n.f0.size(); i++) {
      final ComplexRegularExpressionUnit creu = (ComplexRegularExpressionUnit) n.f0.elementAt(i);
      final NodeChoice ch = creu.f0;
      if (ch.which == 3) {
        final ComplexRegularExpressionChoices crec = (ComplexRegularExpressionChoices) ((NodeSequence) ch.choice)
            .elementAt(1);
        if (crec.f1.present()) {
          oneNewLine(n, "w");
          spc.updateSpc(+1);
          sb.append(spc.spc);
          // } else {
          // sb.append(' ');
        }
      } else if (i > 0) {
        sb.append(' ');
      }
      creu.accept(this);
    }
  }

  /**
   * Visits a {@link ComplexRegularExpressionUnit} node, whose child is the following :
   * <p>
   * f0 -> . %0 StringLiteral()<br>
   * .. .. | %1 #0 "<" #1 IdentifierAsString() #2 ">"<br>
   * .. .. | %2 CharacterList()<br>
   * .. .. | %3 #0 "(" #1 ComplexRegularExpressionChoices() #2 ")"<br>
   * .. .. . .. #3 ( &0 "+"<br>
   * .. .. . .. .. | &1 "*"<br>
   * .. .. . .. .. | &2 "?"<br>
   * .. .. . .. .. | &3 $0 "{" $1 IntegerLiteral()<br>
   * .. .. . .. .. . .. $2 [ ?0 ","<br>
   * .. .. . .. .. . .. .. . ?1 [ IntegerLiteral() ] ]<br>
   * .. .. . .. .. . .. $3 "}" )?<br>
   * s: -1507427530<br>
   *
   * @param n - the node to visit
   */
  @Override
  @NodeFieldsSignature(old_sig = -1507427530, new_sig = JTB_SIG_COMPLEXREGULAREXPRESSIONUNIT, name = "ComplexRegularExpressionUnit")
  public void visit(final ComplexRegularExpressionUnit n) {
    if ((n.f0.which == 0) || (n.f0.which == 2)) {
      // %0 StringLiteral() OR %2 CharacterList(c)
      n.f0.choice.accept(this);
    } else if (n.f0.which == 1) {
      // %1 #0 "<" #1 IdentifierAsString() #2 ">"
      // #0 "<"
      ((NodeSequence) n.f0.choice).elementAt(0).accept(this);
      // #1 IdentifierAsString()
      ((NodeSequence) n.f0.choice).elementAt(1).accept(this);
      // #2 ">"
      ((NodeSequence) n.f0.choice).elementAt(2).accept(this);
    } else {
      // %3 #0 "(" #1 ComplexRegularExpressionChoices() #2 ")" #3 ( &0 "+" | &1 "*" | &2 "?"
      // | &3 $0 "{" $1 IntegerLiteral() $2 [ ?0 "," ?1 [ IntegerLiteral() ] ] $3 "}" )?
      final NodeSequence seq = (NodeSequence) n.f0.choice;
      final ComplexRegularExpressionChoices crec = (ComplexRegularExpressionChoices) seq.elementAt(1);
      // #0 "("
      seq.elementAt(0).accept(this);
      if (crec.f1.present()) {
        oneNewLine(n, "(");
        // spc.updateSpc(+1);
        sb.append(spc.spc).append("  ");
      } else {
        sb.append(' ');
      }
      // #1 ComplexRegularExpressionChoices()
      crec.accept(this);
      // #2 ")"
      if (crec.f1.present()) {
        oneNewLine(n, ")");
        sb.append(spc.spc);
      } else {
        sb.append(' ');
      }
      seq.elementAt(2).accept(this);
      // #3 ( &0 "+" | &1 "*" | &2 "?" | &3 $0 "{" $1 IntegerLiteral() $2 [ ?0 "," ?1 [ IntegerLiteral() ] ]
      // $3 "}" )?
      final NodeOptional opt = (NodeOptional) seq.elementAt(3);
      if (opt.present()) {
        final NodeChoice ch = (NodeChoice) opt.node;
        if (ch.which <= 2) {
          // &0 "+" | &1 "*" | &2 "?"
          ch.choice.accept(this);
        } else {
          // &3 $0 "{" $1 IntegerLiteral() $2 [ ?0 "," ?1 [ IntegerLiteral() ] ] $3 "}"
          final NodeSequence seq1 = (NodeSequence) ch.choice;
          // $0 "{"
          seq1.elementAt(0).accept(this);
          // $1 IntegerLiteral()
          seq1.elementAt(1).accept(this);
          // $2 [ ?0 "," ?1 [ IntegerLiteral() ] ]
          final NodeOptional opt1 = (NodeOptional) seq1.elementAt(2);
          if (opt1.present()) {
            // ?0 ","
            ((NodeSequence) opt1.node).elementAt(0).accept(this);
            sb.append(' ');
            // ?1 IntegerLiteral()
            ((NodeSequence) opt1.node).elementAt(1).accept(this);
          }
          // $3 "}"
          seq1.elementAt(3).accept(this);
        }
      }
      if (crec.f1.present()) {
        spc.updateSpc(-1);
      }
    }
  }

  /**
   * Visits a {@link CharacterList} node, whose children are the following :
   * <p>
   * f0 -> [ "~" ]<br>
   * f1 -> "["<br>
   * f2 -> [ #0 CharacterDescriptor()<br>
   * .. .. . #1 ( $0 "," $1 CharacterDescriptor() )* ]<br>
   * f3 -> "]"<br>
   * s: -966448889<br>
   *
   * @param n - the node to visit
   */
  @Override
  @NodeFieldsSignature(old_sig = -966448889, new_sig = JTB_SIG_CHARACTERLIST, name = "CharacterList")
  public void visit(final CharacterList n) {
    // f0 -> [ "~" ]
    if (n.f0.present()) {
      n.f0.accept(this);
    }
    // f1 -> "["
    n.f1.accept(this);
    // f2 -> [ #0 CharacterDescriptor() #1 ( $0 "," $1 CharacterDescriptor() )* ]
    if (n.f2.present()) {
      final NodeSequence seq = (NodeSequence) n.f2.node;
      // #0 CharacterDescriptor()
      seq.elementAt(0).accept(this);
      // #1 ( $0 "," $1 CharacterDescriptor() )* ]
      final NodeListOptional nlo = (NodeListOptional) seq.elementAt(1);
      for (final INode e : nlo.nodes) {
        final NodeSequence seq1 = (NodeSequence) e;
        // $0 ","
        seq1.elementAt(0).accept(this);
        sb.append(' ');
        // $1 CharacterDescriptor()
        seq1.elementAt(1).accept(this);
      }
    }
    // f3 -> "]"
    n.f3.accept(this);
  }

  /**
   * Visits a {@link CharacterDescriptor} node, whose children are the following :
   * <p>
   * f0 -> StringLiteral()<br>
   * f1 -> [ #0 "-" #1 StringLiteral() ]<br>
   * s: 895087809<br>
   *
   * @param n - the node to visit
   */
  @Override
  @NodeFieldsSignature(old_sig = 895087809, new_sig = JTB_SIG_CHARACTERDESCRIPTOR, name = "CharacterDescriptor")
  public void visit(final CharacterDescriptor n) {
    // f0 -> StringLiteral()
    n.f0.accept(this);
    // f1 -> [ #0 "-" #1 StringLiteral() ]
    if (n.f1.present()) {
      final NodeSequence seq = (NodeSequence) n.f1.node;
      // #0 "-"
      seq.elementAt(0).accept(this);
      // #1 StringLiteral()
      seq.elementAt(1).accept(this);
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
  @NodeFieldsSignature(old_sig = -1580059612, new_sig = JTB_SIG_IDENTIFIERASSTRING, name = "IdentifierAsString")
  public void visit(final IdentifierAsString n) {
    final String str = gdbv.jopt.printSpecialTokensJJ ? n.f0.withSpecials(spc.spc) : ((Token) n.f0).image;
    sb.append(str);
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
  @NodeFieldsSignature(old_sig = -1048223857, new_sig = JTB_SIG_INTEGERLITERAL, name = "IntegerLiteral")
  public void visit(final IntegerLiteral n) {
    final String str = gdbv.jopt.printSpecialTokensJJ ? n.f0.withSpecials(spc.spc) : ((Token) n.f0).image;
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
  @NodeFieldsSignature(old_sig = 241433948, new_sig = JTB_SIG_STRINGLITERAL, name = "StringLiteral")
  public void visit(final StringLiteral n) {
    if (gdbv.jopt.printSpecialTokensJJ) {
      sb.append(UnicodeConverter.addUnicodeEscapes(n.f0.withSpecials(spc.spc, gvaStr)));
    } else {
      if (gvaStr != null) {
        sb.append(gvaStr);
      }
      sb.append(UnicodeConverter.addUnicodeEscapes(((Token) n.f0).image));
    }
    gvaStr = null;
  }

}

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

import static EDU.purdue.jtb.misc.Globals.DEBUG_CLASS_COMMENTS;
import static EDU.purdue.jtb.misc.Globals.INDENT_AMT;
import static EDU.purdue.jtb.misc.Globals.getFixedName;
import static EDU.purdue.jtb.misc.Globals.keepSpecialTokens;
import static EDU.purdue.jtb.misc.Globals.noOverwrite;
import static EDU.purdue.jtb.visitor.GlobalDataBuilder.BNF_IND;
import static EDU.purdue.jtb.visitor.GlobalDataBuilder.JC_IND;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.regex.Pattern;

import EDU.purdue.jtb.misc.FileExistsException;
import EDU.purdue.jtb.misc.JavaBranchPrinter;
import EDU.purdue.jtb.misc.Messages;
import EDU.purdue.jtb.misc.Spacing;
import EDU.purdue.jtb.misc.UnicodeConverter;
import EDU.purdue.jtb.syntaxtree.AccessModifier;
import EDU.purdue.jtb.syntaxtree.BNFProduction;
import EDU.purdue.jtb.syntaxtree.BooleanLiteral;
import EDU.purdue.jtb.syntaxtree.CharacterDescriptor;
import EDU.purdue.jtb.syntaxtree.CharacterList;
import EDU.purdue.jtb.syntaxtree.ComplexRegularExpression;
import EDU.purdue.jtb.syntaxtree.ComplexRegularExpressionChoices;
import EDU.purdue.jtb.syntaxtree.ComplexRegularExpressionUnit;
import EDU.purdue.jtb.syntaxtree.Expansion;
import EDU.purdue.jtb.syntaxtree.ExpansionChoices;
import EDU.purdue.jtb.syntaxtree.ExpansionUnit;
import EDU.purdue.jtb.syntaxtree.ExpansionUnitTCF;
import EDU.purdue.jtb.syntaxtree.INode;
import EDU.purdue.jtb.syntaxtree.IdentifierAsString;
import EDU.purdue.jtb.syntaxtree.IntegerLiteral;
import EDU.purdue.jtb.syntaxtree.JavaCCInput;
import EDU.purdue.jtb.syntaxtree.JavaCCOptions;
import EDU.purdue.jtb.syntaxtree.JavaCodeProduction;
import EDU.purdue.jtb.syntaxtree.LocalLookahead;
import EDU.purdue.jtb.syntaxtree.NodeChoice;
import EDU.purdue.jtb.syntaxtree.NodeListOptional;
import EDU.purdue.jtb.syntaxtree.NodeOptional;
import EDU.purdue.jtb.syntaxtree.NodeSequence;
import EDU.purdue.jtb.syntaxtree.NodeToken;
import EDU.purdue.jtb.syntaxtree.OptionBinding;
import EDU.purdue.jtb.syntaxtree.Production;
import EDU.purdue.jtb.syntaxtree.RegExprKind;
import EDU.purdue.jtb.syntaxtree.RegExprSpec;
import EDU.purdue.jtb.syntaxtree.RegularExprProduction;
import EDU.purdue.jtb.syntaxtree.RegularExpression;
import EDU.purdue.jtb.syntaxtree.StringLiteral;
import EDU.purdue.jtb.syntaxtree.TokenManagerDecls;

/**
 * The {@link JavaCCPrinter} visitor reprints (with indentation) the JavaCC grammar JavaCC specific
 * productions.<br>
 * (The JavaCC grammar Java productions are handled by the {@link JavaPrinter} visitor.)
 * <p>
 * Implementation notes :
 * <ul>
 * <li>sb.append(spc.spc), sb.append(" ") and sb.append(LS) are done at the highest (calling) level
 * (except for Modifiers which prints its last space as it can be empty)
 * <li>sb.append(spc.spc) is normally done after sb.append(LS)
 * <li>sb.append(" ") is not merged with printing punctuation / operators / keywords (to prepare
 * evolutions for other formatting preferences)
 * </ul>
 * TODO : extract / refactor methods for custom formatting<br>
 *
 * @author Marc Mazas
 * @version 1.4.0 : 05-08/2009 : MMa : adapted to JavaCC v4.2 grammar and JDK 1.5<br>
 *          1.4.0 : 11/2009 : MMa : output commented JTB options
 * @version 1.4.7 : 12/2011 : MMa : added comments in the JavaCodeProduction visit method<br>
 *          1.4.7 : 07/2012 : MMa : followed changes in jtbgram.jtb (AccessModifier(),
 *          IndentifierAsString())<br>
 *          1.4.7 : 09/2012 : MMa : added non node creation
 * @version 1.4.8 : 10/2012 : MMa : updated for JavaCodeProduction class generation if requested
 *          1.4.8 : 12/2014 : MMa : fixed commenting specials in JTB options ;<br>
 *          improved specials printing
 * @version 1.4.14 : 01/2017 : MMa : used try-with-resource
 */
public class JavaCCPrinter extends DepthFirstVoidVisitor {

  /** The {@link GlobalDataBuilder} visitor */
  final GlobalDataBuilder     gdbv;
  /** The buffer to print into */
  protected StringBuilder     sb;
  /** The indentation object */
  protected Spacing           spc;
  /** True to not take the specials, false otherwise */
  protected boolean           withoutSpecials = false;
  /** True to comment the specials, false otherwise */
  boolean                     commentSpecials = false;
  /** The {@link JavaBranchPrinter} printer to print a java node and its subtree */
  protected JavaBranchPrinter jbp;
  /** The generated variable assignment string to be inserted */
  protected String            gvaStr;
  /**
   * The BNF nesting level, starts at 0 (in {@link #visit(BNFProduction)}), incremented for each new
   * level, except in an {@link ExpansionChoices} with no choice
   */
  protected int               bnfLvl          = 0;
  /** The OS line separator */
  public static final String  LS              = System.getProperty("line.separator");
  /** The OS line separator length */
  public static final int     LSLEN           = LS.length();

  /*
   * Constructors
   */

  /** The pattern to break the specials into lines */
  public static final Pattern PATT_LS         = Pattern.compile(LS);

  /**
   * Constructor with a global data builder visitor reference, a given buffer and indentation.
   *
   * @param aGdbv - the global data builder visitor
   * @param aSb - the buffer to print into (will be allocated if null)
   * @param aSPC - the Spacing indentation object (will be allocated and set to a default if null)
   */
  public JavaCCPrinter(final GlobalDataBuilder aGdbv, final StringBuilder aSb, final Spacing aSPC) {
    sb = aSb;
    if (sb == null) {
      sb = new StringBuilder(2048);
    }
    spc = aSPC;
    if (spc == null) {
      spc = new Spacing(INDENT_AMT);
    }
    jbp = new JavaBranchPrinter(spc);
    gdbv = aGdbv;
  }

  /**
   * Constructor which will allocate a default buffer and indentation.
   *
   * @param aGdbv - the global data builder visitor
   */
  public JavaCCPrinter(final GlobalDataBuilder aGdbv) {
    this(aGdbv, null, null);
  }

  /**
   * Constructor with a global data builder visitor reference, a given buffer and which will
   * allocate a default indentation.
   *
   * @param aGdbv - the global data builder visitor
   * @param aSb - the buffer to print into (will be allocated if null)
   */
  public JavaCCPrinter(final GlobalDataBuilder aGdbv, final StringBuilder aSb) {
    this(aGdbv, aSb, null);
  }

  /**
   * Constructor with a global data builder visitor reference, a given indentation which will
   * allocate a default buffer.
   *
   * @param aGdbv - the global data builder visitor
   * @param aSPC - the Spacing indentation object
   */
  public JavaCCPrinter(final GlobalDataBuilder aGdbv, final Spacing aSPC) {
    this(aGdbv, null, aSPC);
  }

  /**
   * Saves the current buffer to an output file.
   *
   * @param outFile - the output file
   * @throws FileExistsException - if the file exists and the noOverwrite flag is set
   * @throws IOException if IO problem
   */
  public void saveToFile(final String outFile) throws FileExistsException, IOException {
    try {
      final File file = new File(outFile);
      if (noOverwrite && file.exists()) {
        throw new FileExistsException(outFile);
      } else {
        try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(file),
                                                                  sb.length()))) {
          out.print(sb);
        }
      }
    }
    catch (final IOException e) {
      Messages.hardErr(e);
      throw e;
    }
  }

  /*
   * Base classes visit methods
   */

  /**
   * Prints into the current buffer a NodeToken image and its specials before if global flag set.
   *
   * @param n - the node to visit
   */
  @Override
  public void visit(final NodeToken n) {
    //    sb.append(keepSpecialTokens ? n.withSpecials(spc.spc) : n.tokenImage);
    final String str = keepSpecialTokens && !withoutSpecials ? n.withSpecials(spc.spc, gvaStr)
                                                             : n.tokenImage;
    sb.append(str);
    gvaStr = null;
  }

  /*
   * Convenience methods
   */

  /**
   * Generates a java node and its subtree with a JavaPrinter.
   *
   * @param n - the node to process
   * @return a buffer with the generated source
   */
  protected StringBuilder genJavaBranch(final INode n) {
    return jbp.genJavaBranch(n);
  }

  /**
   * Generates a java node and its subtree with a JavaPrinter.
   *
   * @param n - the node to process
   * @param noDebugComments - true to suppress debug comments, false otherwise
   * @return a buffer with the generated source
   */
  protected StringBuilder genJavaBranch(final INode n, final boolean noDebugComments) {
    return jbp.genJavaBranch(n, noDebugComments);
  }

  /**
   * Prints into the current buffer a node class comment and a new line.
   *
   * @param n - the node for the node class comment
   */
  void oneNewLine(final INode n) {
    sb.append(nodeClassComment(n)).append(LS);
  }

  /**
   * Prints into the current buffer a node class comment, an extra given comment, and a new line.
   *
   * @param n - the node for the node class comment
   * @param str - the extra comment
   */
  void oneNewLine(final INode n, final String str) {
    sb.append(nodeClassComment(n, str)).append(LS);
  }

  /**
   * Prints into the current buffer a node class comment, extra given comments, and a new line.
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
   * Prints three times into the current buffer a node class comment and a new line.
   *
   * @param n - the node for the node class comment
   */
  void threeNewLines(final INode n) {
    oneNewLine(n);
    oneNewLine(n);
    oneNewLine(n);
  }

  /**
   * @param nt - the node token whose specials will be commented and appended to the buffer
   */
  void appendSpecials(final NodeToken nt) {
    String spe;
    spe = nt.getSpecials("");
    if (!commentSpecials) {
      sb.append(spe);
    } else {
      // for each line in the specials, comment it if not starting with //
      final String lines[] = PATT_LS.split(spe);
      for (final String line : lines) {
        final String stripped = line.trim();
        if (stripped.startsWith("//")) {
          sb.append(line);
        } else {
          sb.append(spc.spc).append("// ").append(line);
        }
        sb.append(LS);
      }
      sb.setLength(sb.length() - LSLEN);
    }
  }

  /**
   * Returns a node class comment (a //jcp followed by the node class short name if global flag set,
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
      if (b == -1 || e == -1) {
        return " //jcp " + s;
      } else {
        return " //jcp " + s.substring(b, e);
      }
    } else {
      return "";
    }
  }

  /**
   * Returns a node class comment with an extra comment (a //jcp followed by the node class short
   * name plus the extra comment if global flag set, nothing otherwise).
   *
   * @param n - the node for the node class comment
   * @param str - the extra comment
   * @return the node class comment
   */
  private static String nodeClassComment(final INode n, final String str) {
    if (DEBUG_CLASS_COMMENTS) {
      return nodeClassComment(n).concat(" ").concat(str);
    } else {
      return "";
    }
  }

  /**
   * Returns a node class comment with extra comments (a //jcp followed by the node class short name
   * plus the extra comment if global flag set, nothing otherwise).
   *
   * @param n - the node for the node class comment
   * @param obj - the extra comments
   * @return the node class comment
   */
  private static String nodeClassComment(final INode n, final Object... obj) {
    if (DEBUG_CLASS_COMMENTS) {
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
    // we do not use sb.append(spc.spc) as indent level should be 0 at this point
    //  f0 -> JavaCCOptions()
    n.f0.accept(this);
    oneNewLine(n);
    oneNewLine(n);
    oneNewLine(n);
    // f1 -> "PARSER_BEGIN"
    n.f1.accept(this);
    // f2 -> "("
    n.f2.accept(this);
    // f3 -> IdentifierAsString()
    n.f3.accept(this);
    // f4 -> ")"
    n.f4.accept(this);
    oneNewLine(n);
    // f5 -> CompilationUnit()
    sb.append(genJavaBranch(n.f5));
    // f6 -> "PARSER_END"
    n.f6.accept(this);
    // f7 -> "("
    n.f7.accept(this);
    // f8 -> IdentifierAsString()
    n.f8.accept(this);
    // f9 -> ")"
    n.f9.accept(this);
    oneNewLine(n);
    oneNewLine(n);
    oneNewLine(n);
    // f10 -> ( Production() )+
    for (final Iterator<INode> e = n.f10.elements(); e.hasNext();) {
      e.next().accept(this);
      if (e.hasNext()) {
        oneNewLine(n);
        oneNewLine(n);
      }
    }
    oneNewLine(n);
  }

  /**
   * Visits a {@link JavaCCOptions} node, whose children are the following :
   * <p>
   * f0 -> [ #0 "options" #1 "{"<br>
   * .. .. . #2 ( OptionBinding() )*<br>
   * .. .. . #3 "}" ]<br>
   *
   * @param n - the node to visit
   */
  @Override
  public void visit(final JavaCCOptions n) {
    if (n.f0.present()) {
      final NodeSequence seq = (NodeSequence) n.f0.node;
      // #0 "options"
      seq.elementAt(0).accept(this);
      oneNewLine(n);
      // #1 "{"
      seq.elementAt(1).accept(this);
      withoutSpecials = true;
      // #2 ( OptionBinding() )*
      final NodeListOptional nlo = (NodeListOptional) seq.elementAt(2);
      if (nlo.present()) {
        spc.updateSpc(+1);
        oneNewLine(n);
        //        sb.append(spc.spc);
        for (final Iterator<INode> e = nlo.elements(); e.hasNext();) {
          // OptionBinding()
          commentSpecials = false;
          e.next().accept(this);
          if (e.hasNext()) {
            oneNewLine(n);
            sb.append(spc.spc);
          }
        }
        spc.updateSpc(-1);
        oneNewLine(n);
        sb.append(spc.spc);
      }
      // #3 "}"
      seq.elementAt(3).accept(this);
      withoutSpecials = false;
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
  public void visit(final OptionBinding n) {
    // f0 -> ( %0 < IDENTIFIER > | %1 "LOOKAHEAD" | %2 "IGNORE_CASE" | %3 "static" )
    if (n.f0.which == 0) {
      // f0 -> %0 < IDENTIFIER
      final NodeToken nt = (NodeToken) n.f0.choice;
      if (nt.tokenImage.startsWith("JTB_")) {
        // comment JTB options : need to comment not only one line but all the lines
        if (keepSpecialTokens) {
          appendSpecials(nt);
        }
        sb.append(spc.spc).append("// ").append(nt.tokenImage);
        commentSpecials = true;
      } else {
        // not a JTB option
        if (keepSpecialTokens) {
          appendSpecials(nt);
        }
        n.f0.choice.accept(this);
      }
    } else {
      // f0 -> %1 "LOOKAHEAD" | %2 "IGNORE_CASE" | %3 "static"
      if (keepSpecialTokens) {
        final NodeToken nt = (NodeToken) n.f0.choice;
        appendSpecials(nt);
      }
      n.f0.choice.accept(this);
    }
    sb.append(" ");
    // f1 -> "="
    if (keepSpecialTokens) {
      final NodeToken nt = n.f1;
      appendSpecials(nt);
    }
    n.f1.accept(this);
    sb.append(" ");
    // f2 -> ( %0 IntegerLiteral() | %1 BooleanLiteral() | %2 StringLiteral() )
    if (keepSpecialTokens) {
      NodeToken nt = null;
      final INode nd = n.f2.choice;
      if (nd instanceof StringLiteral) {
        nt = ((StringLiteral) nd).f0;
      } else if (nd instanceof IntegerLiteral) {
        nt = ((IntegerLiteral) nd).f0;
      } else if (nd instanceof BooleanLiteral) {
        nt = (NodeToken) ((BooleanLiteral) nd).f0.choice;
      }
      appendSpecials(nt);
    }
    n.f2.accept(this);
    // f3 -> ";"
    if (keepSpecialTokens) {
      final NodeToken nt = n.f3;
      appendSpecials(nt);
    }
    n.f3.accept(this);
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
   *
   * @param n - the node to visit
   */
  @Override
  public void visit(final JavaCodeProduction n) {
    // f0 -> "JAVACODE"
    n.f0.accept(this);
    oneNewLine(n);
    sb.append(spc.spc);
    // f1 -> AccessModifier()
    n.f1.accept(this);
    // f2 -> ResultType()
    sb.append(genJavaBranch(n.f2));
    sb.append(" ");
    // f3 -> IdentifierAsString()
    n.f3.accept(this);
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
      final NodeListOptional opt = (NodeListOptional) seq.elementAt(2);
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
    oneNewLine(n);
    sb.append(spc.spc);
    // f6 -> [ "%" ]
    // print it in a block comment
    if (n.f6.present()) {
      sb.append(" /*%*/ ");
    }
    // f7 -> Block()
    sb.append(genJavaBranch(n.f7));
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
    // note : it looks we never fall here
    //    throw new AssertionError("in JavaCCPrinter visiting BNFProduction");
    bnfLvl = 0;
    // f0 -> AccessModifier()
    n.f0.accept(this);
    // f1 -> ResultType()
    sb.append(genJavaBranch(n.f1));
    sb.append(" ");
    // f2 -> IdentifierAsString()
    n.f2.accept(this);
    // f3 -> FormalParameters()
    sb.append(genJavaBranch(n.f3));
    // f4 -> [ #0 "throws" #1 Name() #2 ( $0 "," $1 Name() )* ]
    if (n.f4.present()) {
      final NodeSequence seq = (NodeSequence) n.f4.node;
      sb.append(" ");
      // #0 "throws"
      seq.elementAt(0).accept(this);
      sb.append(" ");
      // #1 Name()
      sb.append(genJavaBranch(seq.elementAt(1)));
      // #2 ( $0 "," $1 Name() )* ]
      final NodeListOptional opt = (NodeListOptional) seq.elementAt(2);
      if (opt.present()) {
        for (final Iterator<INode> e = opt.elements(); e.hasNext();) {
          final NodeSequence seq1 = (NodeSequence) e.next();
          // $0 ","
          seq1.elementAt(0).accept(this);
          sb.append(" ");
          // $1 Name()
          sb.append(genJavaBranch(seq1.elementAt(1)));
        }
      }
    }
    // f5 -> [ "!" ]
    // print it in a block comment
    if (n.f5.present()) {
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
    n.f9.accept(this);
    oneNewLine(n);
    spc.updateSpc(-1);
    sb.append(spc.spc);
    // f10 -> "}"
    n.f10.accept(this);
    oneNewLine(n);
  }

  /**
   * Visits a {@link AccessModifier} node, whose children are the following :
   * <p>
   * f0 -> ( %0 "public"<br>
   * .. .. | %1 "protected"<br>
   * .. .. | %2 "private"<br>
   * .. .. | %3 "final" )*<br>
   *
   * @param n - the node to visit
   */
  @Override
  public void visit(final AccessModifier n) {
    if (n.f0.present()) {
      for (final Iterator<INode> e = n.f0.elements(); e.hasNext();) {
        ((NodeChoice) e.next()).accept(this);
        sb.append(" ");
      }
    }
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
    // f0 -> [ %0 #0 "<" #1 "*" #2 ">" | %1 #0 "<" #1 < IDENTIFIER > #2 ( $0 "," $1 < IDENTIFIER > )* #3 ">" ]
    if (n.f0.present()) {
      n.f0.node.accept(this);
      oneNewLine(n);
    }
    // f1 -> RegExprKind()
    n.f1.accept(this);
    // f2 -> [ #0 "[" #1 "IGNORE_CASE" #2 "]" ]
    if (n.f2.present()) {
      sb.append(" ");
      n.f2.node.accept(this);
    }
    sb.append(" ");
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
      for (final Iterator<INode> e = n.f6.elements(); e.hasNext();) {
        final NodeSequence seq = (NodeSequence) e.next();
        oneNewLine(n);
        // #0 "|"
        seq.elementAt(0).accept(this);
        sb.append(" ");
        // #1 RegExprSpec()
        seq.elementAt(1).accept(this);
      }
    }
    spc.updateSpc(-1);
    oneNewLine(n);
    sb.append(spc.spc);
    // f7 -> "}"
    n.f7.accept(this);
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
  public void visit(final TokenManagerDecls n) {
    // f0 -> "TOKEN_MGR_DECLS"
    n.f0.accept(this);
    sb.append(" ");
    // f1 -> ":"
    n.f1.accept(this);
    sb.append(" ");
    // f2 -> ClassOrInterfaceBody()
    sb.append(genJavaBranch(n.f2));
  }

  /**
   * Visits a {@link RegExprKind} node, whose children are the following :
   * <p>
   * f0 -> . %0 "TOKEN"<br>
   * .. .. | %1 "SPECIAL_TOKEN"<br>
   * .. .. | %2 "SKIP"<br>
   * .. .. | %3 "MORE"<br>
   *
   * @param n - the node to visit
   */
  @Override
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
   *
   * @param n - the node to visit
   */
  @Override
  public void visit(final RegExprSpec n) {
    // f0 -> RegularExpression()
    n.f0.accept(this);
    // f1 -> [ "!" ]
    // print it in a block comment
    if (n.f1.present()) {
      sb.append(" /*!*/");
    }
    // f2 -> [  Block() ]
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
      sb.append(" ");
      // #0 ":"
      sb.append(seq.elementAt(0));
      sb.append(" ");
      // #1 < IDENTIFIER >
      sb.append(seq.elementAt(1));
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
    if (!n.f1.present()) {
      // f0 -> Expansion()
      n.f0.accept(this);
    } else {
      //  f0 -> Expansion()
      ++bnfLvl;
      n.f0.accept(this);
      --bnfLvl;
      // f1 -> ( #0 "|" #1 Expansion() )*
      for (final Iterator<INode> e = n.f1.elements(); e.hasNext();) {
        final NodeSequence seq = (NodeSequence) e.next();
        if (bnfLvl != 0) {
          sb.append(" ");
        } else {
          oneNewLine(n);
          sb.append(spc.spc);
        }
        // #0 "|"
        seq.elementAt(0).accept(this);
        sb.append(" ");
        // #1 Expansion()
        ++bnfLvl;
        seq.elementAt(1).accept(this);
        --bnfLvl;
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
    // f0 -> ( #0 "LOOKAHEAD" #1 "(" #2 LocalLookahead() #3 ")" )?
    if (n.f0.present()) {
      final NodeSequence seq = (NodeSequence) n.f0.node;
      // #0 "LOOKAHEAD"
      seq.elementAt(0).accept(this);
      sb.append(" ");
      // #1 "("
      seq.elementAt(1).accept(this);
      sb.append(" ");
      // #2 LocalLookahead()
      seq.elementAt(2).accept(this);
      sb.append(" ");
      // #3 ")"
      seq.elementAt(3).accept(this);
    }
    // f1 -> ( ExpansionUnit() )+
    for (final Iterator<INode> e = n.f1.elements(); e.hasNext();) {
      // ExpansionUnit()
      e.next().accept(this);
      if (e.hasNext()) {
        if (bnfLvl == 0) {
          oneNewLine(n);
          sb.append(spc.spc);
        } else {
          sb.append(" ");
        }
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
   * f4 -> [ #0 "{" #1 Expression() #2 "}" ]<br>
   *
   * @param n - the node to visit
   */
  @Override
  public void visit(final LocalLookahead n) {
    // f0 -> [ IntegerLiteral() ]
    if (n.f0.present()) {
      n.f0.accept(this);
    }
    // f1 -> [ "," ]
    if (n.f1.present()) {
      n.f1.accept(this);
      sb.append(" ");
    }
    // f2 -> [ ExpansionChoices() ]
    if (n.f2.present()) {
      ++bnfLvl;
      n.f2.node.accept(this);
      --bnfLvl;
    }
    // f3 -> [ "," ]
    if (n.f3.present()) {
      n.f3.accept(this);
      sb.append(" ");
    }
    // f4 -> [ #0 "{" #1 Expression() #2 "}" ]
    if (n.f4.present()) {
      final NodeSequence seq = (NodeSequence) n.f4.node;
      // #0 "{"
      seq.elementAt(0).accept(this);
      sb.append(" ");
      // #1 Expression()
      sb.append(genJavaBranch(seq.elementAt(1)));
      sb.append(" ");
      // #2 "}"
      seq.elementAt(2).accept(this);
    }
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
    NodeOptional opt;
    NodeChoice ch;
    switch (n.f0.which) {
      case 0:
        // %0 #0 "LOOKAHEAD" #1 "(" #2 LocalLookahead() #3 ")"
        // #0 "LOOKAHEAD"
        seq = (NodeSequence) n.f0.choice;
        seq.elementAt(0).accept(this);
        sb.append(" ");
        // #1 "("
        seq.elementAt(1).accept(this);
        sb.append(" ");
        // #2 LocalLookahead()
        seq.elementAt(2).accept(this);
        sb.append(" ");
        // #3 ")"
        seq.elementAt(3).accept(this);
        break;

      case 1:
        // %1 Block()
        sb.append(genJavaBranch(n.f0.choice));
        break;

      case 2:
        // %2 #0 "[" #1 ExpansionChoices() #2 "]"
        seq = (NodeSequence) n.f0.choice;
        seq.elementAt(0).accept(this);
        sb.append(" ");
        ++bnfLvl;
        seq.elementAt(1).accept(this);
        --bnfLvl;
        sb.append(" ");
        seq.elementAt(2).accept(this);
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
          sb.append(" ");
          // $1 "="
          ((NodeSequence) opt.node).elementAt(1).accept(this);
          sb.append(" ");
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
          // must be prefixed / suffixed if to be created
          boolean creLocNode = true;
          final String ias = ((IdentifierAsString) seq1.elementAt(0)).f0.tokenImage;
          final String prod = gdbv.getProdHT().get(ias);
          if (prod != null) {
            final String indProd = prod.substring(0, 1);
            if (BNF_IND.equals(indProd)) {
              if (gdbv.getNsnHT().containsKey(ias)) {
                creLocNode = false;
              }
            } else if (JC_IND.equals(indProd)) {
              if (!gdbv.getNsnHT().containsKey(ias)) {
                creLocNode = false;
              }
            }
          }
          if (((NodeOptional) seq1.elementAt(2)).present()) {
            creLocNode = false;
          }
          sb.append(creLocNode ? getFixedName(ias) : ias);
          //) $1 Arguments()
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
            ((NodeToken) ((NodeSequence) opt1.node).elementAt(0)).accept(this);
            ((NodeToken) ((NodeSequence) opt1.node).elementAt(1)).accept(this);
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
        sb.append(" ");
        // #1 ExpansionChoices()
        ++bnfLvl;
        seq.elementAt(1).accept(this);
        --bnfLvl;
        sb.append(" ");
        // #2 ")"
        seq.elementAt(2).accept(this);
        // #3 ( &0 "+" | &1 "*" | &2 "?" )?
        opt = (NodeOptional) seq.elementAt(3);
        if (opt.present()) {
          opt.node.accept(this);
        }
        break;

      default:
        final String msg = "Invalid n.f0.which = " + String.valueOf(n.f0.which);
        Messages.hardErr(msg);
        throw new InternalError(msg);

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
    // f0 -> "try"
    n.f0.accept(this);
    sb.append(" ");
    //f1 -> "{"
    n.f1.accept(this);
    spc.updateSpc(+1);
    oneNewLine(n);
    sb.append(spc.spc);
    // f2 -> ExpansionChoices()
    n.f2.accept(this);
    sb.append(" ");
    spc.updateSpc(-1);
    // f3 -> "}"
    n.f3.accept(this);
    oneNewLine(n);
    sb.append(spc.spc);
    // f4 -> ( #0 "catch" #1 "(" #2 Name() #3 < IDENTIFIER > #4 ")" #5 Block() )*
    if (n.f4.present()) {
      for (int i = 0; i < n.f4.size(); i++) {
        final NodeSequence seq = (NodeSequence) n.f4.elementAt(i);
        // #0 "catch"
        seq.elementAt(0).accept(this);
        sb.append(" ");
        // #1 "("
        seq.elementAt(1).accept(this);
        // #2 Name()
        sb.append(genJavaBranch(seq.elementAt(2)));
        sb.append(" ");
        // #3 < IDENTIFIER >
        sb.append(((NodeToken) seq.elementAt(3)).tokenImage);
        // #4 ")"
        seq.elementAt(3).accept(this);
        // #5 Block()
        sb.append(genJavaBranch(seq.elementAt(4)));
        oneNewLine(n);
      }
    }
    // f5 -> [ #0 "finally" #1 Block() ]
    if (n.f5.present()) {
      // #0 "finally"
      ((NodeSequence) n.f5.node).elementAt(0).accept(this);
      sb.append(" ");
      // #1 Block()
      sb.append(genJavaBranch(((NodeSequence) n.f5.node).elementAt(1)));
      oneNewLine(n);
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
    if (n.f0.which == 0) {
      // %0 StringLiteral()
      n.f0.choice.accept(this);
    } else if (n.f0.which == 1) {
      // %1 #0 "<" #1 [ $0 [ "#" ] $1 IdentifierAsString() $2 ":" ] #2 ComplexRegularExpressionChoices() #3 ">"
      final NodeSequence seq = (NodeSequence) n.f0.choice;
      // #0 "<"
      seq.elementAt(0).accept(this);
      sb.append(" ");
      final NodeOptional opt = (NodeOptional) seq.elementAt(1);
      // #1 [ $0 [ "#" ] $1 IdentifierAsString() $2 ":" ]
      if (opt.present()) {
        final NodeSequence seq1 = (NodeSequence) opt.node;
        if (((NodeOptional) seq1.elementAt(0)).present()) {
          // $0 [ "#" ]
          seq1.elementAt(0).accept(this);
        }
        // $1 IdentifierAsString()
        seq1.elementAt(1).accept(this);
        sb.append(" ");
        // $2 ":"
        seq1.elementAt(2).accept(this);
        sb.append(" ");
      }
      // #2 ComplexRegularExpressionChoices(c)
      seq.elementAt(2).accept(this);
      sb.append(" ");
      // #3 ">"
      seq.elementAt(3).accept(this);
    } else {
      // %2 #0 "<" #1 IdentifierAsString() #2 ">" OR %3 #0 "<" #1 "EOF" #2 ">"
      // #0 "<"
      ((NodeSequence) n.f0.choice).elementAt(0).accept(this);
      sb.append(" ");
      // #1 IdentifierAsString() OR #1 "EOF"
      ((NodeSequence) n.f0.choice).elementAt(1).accept(this);
      sb.append(" ");
      // #2 ">"
      ((NodeSequence) n.f0.choice).elementAt(2).accept(this);
    }
  }

  /**
   * Visits a {@link ComplexRegularExpressionChoices} node, whose children are the following :
   * <p>
   * f0 -> ComplexRegularExpression()<br>
   * f1 -> ( #0 "|" #1 ComplexRegularExpression() )*<br>
   *
   * @param n - the node to visit
   */
  @Override
  public void visit(final ComplexRegularExpressionChoices n) {
    // f0 -> ComplexRegularExpression()
    n.f0.accept(this);
    // f1 -> ( #0 "|" #1 ComplexRegularExpression() )*
    if (n.f1.present()) {
      for (final Iterator<INode> e = n.f1.elements(); e.hasNext();) {
        final NodeSequence seq = (NodeSequence) e.next();
        // #0 "|"
        seq.elementAt(0).accept(this);
        sb.append(" ");
        // #1 ComplexRegularExpression()
        seq.elementAt(1).accept(this);
      }
    }
  }

  /**
   * Visits a {@link ComplexRegularExpression} node, whose children are the following :
   * <p>
   * f0 -> ( ComplexRegularExpressionUnit() )+<br>
   *
   * @param n - the node to visit
   */
  @Override
  public void visit(final ComplexRegularExpression n) {
    for (final Iterator<INode> e = n.f0.elements(); e.hasNext();) {
      // ComplexRegularExpressionUnit()
      e.next().accept(this);
      if (e.hasNext()) {
        sb.append(" ");
      }
    }
  }

  /**
   * Visits a {@link ComplexRegularExpressionUnit} node, whose children are the following :
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
   *
   * @param n - the node to visit
   */
  @Override
  public void visit(final ComplexRegularExpressionUnit n) {
    if (n.f0.which == 0 || n.f0.which == 2) {
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
      // %3 #0 "(" #1 ComplexRegularExpressionChoices() #2 ")" #3 ( &0 "+" | &1 "*" | &2 "?" | &3 $0 "{" $1 IntegerLiteral() $2 [ ?0 "," ?1 [ IntegerLiteral() ] ] $3 "}" )?
      final NodeSequence seq = (NodeSequence) n.f0.choice;
      // #0 "("
      seq.elementAt(0).accept(this);
      sb.append(" ");
      // #1 ComplexRegularExpressionChoices()
      seq.elementAt(1).accept(this);
      sb.append(" ");
      // #2 ")"
      seq.elementAt(2).accept(this);
      // #3 ( &0 "+" | &1 "*" | &2 "?" | &3 $0 "{" $1 IntegerLiteral() $2 [ ?0 "," ?1 [ IntegerLiteral() ] ] $3 "}" )?
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
            sb.append(" ");
            // ?1 IntegerLiteral()
            ((NodeSequence) opt1.node).elementAt(1).accept(this);
          }
          // $3 "}"
          seq1.elementAt(3).accept(this);
        }
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
   *
   * @param n - the node to visit
   */
  @Override
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
      for (final Iterator<INode> e = nlo.elements(); e.hasNext();) {
        final NodeSequence seq1 = (NodeSequence) e.next();
        // $0 ","
        seq1.elementAt(0).accept(this);
        sb.append(" ");
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
   *
   * @param n - the node to visit
   */
  @Override
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
   * Visits a {@link IdentifierAsString} node, whose children are the following :
   * <p>
   * f0 -> < IDENTIFIER ><br>
   *
   * @param n - the node to visit
   */
  @Override
  public void visit(final IdentifierAsString n) {
    final String str = keepSpecialTokens && !withoutSpecials ? n.f0.withSpecials(spc.spc)
                                                             : n.f0.tokenImage;
    sb.append(UnicodeConverter.addUnicodeEscapes(str));
  }

  /**
   * Visits a {@link IntegerLiteral} node, whose children are the following :
   * <p>
   * f0 -> < INTEGER_LITERAL ><br>
   *
   * @param n - the node to visit
   */
  @Override
  public void visit(final IntegerLiteral n) {
    final String str = keepSpecialTokens && !withoutSpecials ? n.f0.withSpecials(spc.spc)
                                                             : n.f0.tokenImage;
    sb.append(UnicodeConverter.addUnicodeEscapes(str));
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
    final String str = keepSpecialTokens && !withoutSpecials ? n.f0.withSpecials(spc.spc, gvaStr)
                                                             : n.f0.tokenImage;
    sb.append(UnicodeConverter.addUnicodeEscapes(str));
    gvaStr = null;
  }

}

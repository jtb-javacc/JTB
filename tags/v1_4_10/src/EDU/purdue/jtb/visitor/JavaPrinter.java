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
package EDU.purdue.jtb.visitor;

import static EDU.purdue.jtb.misc.Globals.DEBUG_CLASS_COMMENTS;
import static EDU.purdue.jtb.misc.Globals.INDENT_AMT;
import static EDU.purdue.jtb.misc.Globals.keepSpecialTokens;
import static EDU.purdue.jtb.misc.Globals.noOverwrite;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;

import EDU.purdue.jtb.misc.FileExistsException;
import EDU.purdue.jtb.misc.Messages;
import EDU.purdue.jtb.misc.Spacing;
import EDU.purdue.jtb.misc.UnicodeConverter;
import EDU.purdue.jtb.syntaxtree.*;

/**
 * The {@link JavaPrinter} visitor reprints (with indentation) JavaCC grammar Java specific
 * productions.<br>
 * (The JavaCC grammar JavaCC productions are handled by the {@link JavaCCPrinter} visitor
 * superclass.)
 * <p>
 * Notes :
 * <ul>
 * <li>sb.append(spc.spc), sb.append(" ") and sb.append(LS) are done at the highest (calling) level
 * (except for Modifiers() and VariableModifiers() which prints the last space if not empty)
 * <li>sb.append(spc.spc) is done after sb.append(LS)
 * <li>sb.append(" ") is not merged with printing punctuation / operators (to prepare evolutions for
 * other formatting preferences), but is indeed merged with printing keywords
 * </ul>
 * TODO : extract / refactor methods for custom formatting<br>
 * 
 * @author Marc Mazas
 * @version 1.4.0 : 05-08/2009 : MMa : adapted to JavaCC v4.2 grammar and JDK 1.5
 * @version 1.4.3 : 03/2010 : MMa : fixed output of else in IfStatement
 * @version 1.4.4 : 07/2010 : MMa : fixed output after throws in MethodDeclaration,
 *          ConstructorDeclaration, and wrong index in TypeArguments
 * @version 1.4.7 : 07/2012 : MMa : followed changes in jtbgram.jtb (on PackageDeclaration(),
 *          VariableModifiers() IndentifierAsString())
 * @version 1.4.8 : 11/2014 : MMa : followed changes in jtbgram.jtb (on
 *          ClassOrInterfaceBodyDeclaration(), ExplicitConstructorInvocation())
 */
public class JavaPrinter extends DepthFirstVoidVisitor {

  /** The buffer to print into */
  protected StringBuilder    sb;
  /** The indentation object */
  protected Spacing          spc;
  /** The OS line separator */
  public static final String LS              = System.getProperty("line.separator");
  /** True to suppress printing of debug comments, false otherwise */
  public boolean             noDebugComments = false;

  /**
   * Constructor with a given buffer and indentation.
   * 
   * @param aSb - the buffer to print into (will be allocated if null)
   * @param aSPC - the Spacing indentation object (will be allocated and set to a default if null)
   */
  public JavaPrinter(final StringBuilder aSb, final Spacing aSPC) {
    reset(aSb, aSPC);
  }

  /**
   * Resets the buffer and the indentation.
   * 
   * @param aSb - the buffer to print into (will be allocated if null)
   * @param aSPC - the Spacing indentation object (will be allocated and set to a default if null)
   */
  public void reset(final StringBuilder aSb, final Spacing aSPC) {
    sb = aSb;
    if (sb == null)
      sb = new StringBuilder(2048);
    spc = aSPC;
    if (spc == null)
      spc = new Spacing(INDENT_AMT);
  }

  /**
   * Constructor which will allocate a default buffer and indentation.
   */
  public JavaPrinter() {
    this(null, null);
  }

  /**
   * Constructor with a given buffer and which will allocate a default indentation.
   * 
   * @param aSb - the buffer to print into (will be allocated if null)
   */
  public JavaPrinter(final StringBuilder aSb) {
    this(aSb, null);
  }

  /**
   * Constructor with a given indentation which will allocate a default buffer.
   * 
   * @param aSPC - the Spacing indentation object
   */
  public JavaPrinter(final Spacing aSPC) {
    this(null, aSPC);
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
      if (noOverwrite && file.exists())
        throw new FileExistsException(outFile);
      else {
        final PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(file),
                                                                   sb.length()));
        out.print(sb);
        out.close();
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
   * Visits a NodeToken.
   * 
   * @param n - the node to visit
   */
  @Override
  public void visit(final NodeToken n) {
    if (keepSpecialTokens) {
      sb.append(n.withSpecials(spc.spc));
    } else {
      sb.append(n.tokenImage);
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
   * Returns a node class comment with an extra comment (a //jvp followed by the node class short
   * name if global flag set, nothing otherwise).
   * 
   * @param n - the node to process
   * @return the comment
   */
  @SuppressWarnings("unused")
  private String nodeClassComment(final INode n) {
    if (!noDebugComments && DEBUG_CLASS_COMMENTS) {
      final String s = n.toString();
      final int b = s.lastIndexOf('.') + 1;
      final int e = s.indexOf('@');
      if (b == -1 || e == -1)
        return " //jvp " + s;
      else
        return " //jvp " + s.substring(b, e);
    } else
      return "";
  }

  /**
   * Returns a node class comment with an extra comment (a //jvp followed by the node class short
   * name plus the extra comment if global flag set, nothing otherwise).
   * 
   * @param n - the node to process
   * @param str - the string to add to the comment
   * @return the comment
   */
  @SuppressWarnings("unused")
  private String nodeClassComment(final INode n, final String str) {
    if (!noDebugComments && DEBUG_CLASS_COMMENTS)
      return nodeClassComment(n) + " " + str;
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
    // we do not use out.print(spc.spc) as indent level should be 0 at this point
    // f0 -> [ PackageDeclaration() ]
    if (n.f0.present()) {
      n.f0.accept(this);
      oneNewLine(n);
    }
    // f1 -> ( ImportDeclaration() )*
    if (n.f1.present()) {
      for (final Iterator<INode> e = n.f1.elements(); e.hasNext();) {
        e.next().accept(this);
        oneNewLine(n);
      }
      oneNewLine(n);
    }
    // f2 -> ( TypeDeclaration() )*
    if (n.f2.present()) {
      for (final Iterator<INode> e = n.f2.elements(); e.hasNext();) {
        e.next().accept(this);
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
   * 
   * @param n - the node to visit
   */
  @Override
  public void visit(final PackageDeclaration n) {
    // f0 -> "package"
    n.f0.accept(this);
    sb.append(" ");
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
    if (n.f3.present())
      n.f3.accept(this);
    // f4 -> ";"
    n.f4.accept(this);
  }

  /**
   * Visits a {@link Modifiers} node, whose children are the following :
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
   */
  @Override
  public void visit(final Modifiers n) {
    if (n.f0.present()) {
      for (final Iterator<INode> e = n.f0.elements(); e.hasNext();) {
        e.next().accept(this);
        // Modifiers print the last space if not empty
        sb.append(" ");
      }
    }
  }

  /**
   * Visits a {@link TypeDeclaration} node, whose children are the following :
   * <p>
   * f0 -> . %0 ";"<br>
   * .. .. | %1 #0 Modifiers()<br>
   * .. .. . .. #1 ( &0 ClassOrInterfaceDeclaration()<br>
   * .. .. . .. .. | &1 EnumDeclaration()<br>
   * .. .. . .. .. | &2 AnnotationTypeDeclaration() )<br>
   * 
   * @param n - the node to visit
   */
  @Override
  public void visit(final TypeDeclaration n) {
    if (n.f0.which == 0) {
      // %0 ";"
      n.f0.choice.accept(this);
    } else {
      final NodeSequence seq = (NodeSequence) n.f0.choice;
      // #0 Modifiers()
      seq.elementAt(0).accept(this);
      // Modifiers print the last space if not empty
      // #1 ( &0 ClassOrInterfaceDeclaration() | &1 EnumDeclaration() | &2 AnnotationTypeDeclaration() )
      seq.elementAt(1).accept(this);
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
   * 
   * @param n - the node to visit
   */
  @Override
  public void visit(final ClassOrInterfaceDeclaration n) {
    // f0 -> ( %0 "class" | %1 "interface" )
    n.f0.accept(this);
    sb.append(" ");
    // f1 -> < IDENTIFIER >
    n.f1.accept(this);
    sb.append(" ");
    // f2 -> [ TypeParameters() ]
    if (n.f2.present()) {
      n.f2.accept(this);
      sb.append(" ");
    }
    // f3 -> [ ExtendsList() ]
    if (n.f3.present()) {
      n.f3.accept(this);
      sb.append(" ");
    }
    // f4 -> [ ImplementsList() ]
    if (n.f4.present()) {
      n.f4.accept(this);
      sb.append(" ");
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
   * 
   * @param n - the node to visit
   */
  @Override
  public void visit(final ExtendsList n) {
    // f0 -> "extends"
    n.f0.accept(this);
    sb.append(" ");
    // f1 -> ClassOrInterfaceType()
    n.f1.accept(this);
    // f2 -> ( #0 "," #1 ClassOrInterfaceType() )*
    if (n.f2.present()) {
      for (final Iterator<INode> e = n.f2.elements(); e.hasNext();) {
        sb.append(" ");
        final NodeSequence seq = (NodeSequence) e.next();
        // #0 "&"
        seq.elementAt(0).accept(this);
        sb.append(" ");
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
   * 
   * @param n - the node to visit
   */
  @Override
  public void visit(final ImplementsList n) {
    // f0 -> "implements"
    n.f0.accept(this);
    sb.append(" ");
    // f1 -> ClassOrInterfaceType()
    n.f1.accept(this);
    // f2 -> ( #0 "," #1 ClassOrInterfaceType() )*
    if (n.f2.present()) {
      for (final Iterator<INode> e = n.f2.elements(); e.hasNext();) {
        sb.append(" ");
        final NodeSequence seq = (NodeSequence) e.next();
        // #0 ","
        seq.elementAt(0).accept(this);
        sb.append(" ");
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
   * 
   * @param n - the node to visit
   */
  @Override
  public void visit(final EnumDeclaration n) {
    // f0 -> "enum"
    n.f0.accept(this);
    sb.append(" ");
    // f1 -> < IDENTIFIER >
    n.f1.accept(this);
    sb.append(" ");
    // f2 -> [ ImplementsList() ]
    if (n.f2.present()) {
      n.f2.accept(this);
      sb.append(" ");
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
   * 
   * @param n - the node to visit
   */
  @Override
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
        for (final Iterator<INode> e = nlo.elements(); e.hasNext();) {
          final NodeSequence seq1 = (NodeSequence) e.next();
          // $0 ","
          seq1.elementAt(0).accept(this);
          sb.append(" ");
          // $1 EnumConstant()
          seq1.elementAt(1).accept(this);
        }
      }
    }
    // f2 -> [ "," ]
    if (n.f2.present()) {
      n.f2.accept(this);
      sb.append(" ");
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
        for (final Iterator<INode> e = nlo.elements(); e.hasNext();) {
          // ClassOrInterfaceBodyDeclaration()
          e.next().accept(this);
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
   * Visits a {@link EnumConstant} node, whose children are the following :
   * <p>
   * f0 -> Modifiers()<br>
   * f1 -> < IDENTIFIER ><br>
   * f2 -> [ Arguments() ]<br>
   * f3 -> [ ClassOrInterfaceBody() ]<br>
   * 
   * @param n - the node to visit
   */
  @Override
  public void visit(final EnumConstant n) {
    // f0 -> Modifiers()
    n.f0.accept(this);
    // f1 -> < IDENTIFIER >
    n.f1.accept(this);
    // f2 -> [ Arguments() ]
    if (n.f2.present())
      n.f2.accept(this);
    // f3 -> [ ClassOrInterfaceBody() ]
    if (n.f3.present())
      n.f3.accept(this);
  }

/**
   * Visits a {@link TypeParameters} node, whose children are the following :
   * <p>
   * f0 -> "<"<br>
   * f1 -> TypeParameter()<br>
   * f2 -> ( #0 "," #1 TypeParameter() )*<br>
   * f3 -> ">"<br>
   *
   * @param n - the node to visit
   */
  @Override
  public void visit(final TypeParameters n) {
    // f0 -> "<"
    n.f0.accept(this);
    // f1 -> TypeParameter()
    n.f1.accept(this);
    // f2 -> ( #0 "," #1 TypeParameter() )*
    if (n.f2.present()) {
      for (final Iterator<INode> e = n.f2.elements(); e.hasNext();) {
        final NodeSequence seq = (NodeSequence) e.next();
        // #0 ","
        seq.elementAt(0).accept(this);
        sb.append(" ");
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
   * f0 -> < IDENTIFIER ><br>
   * f1 -> [ TypeBound() ]<br>
   * 
   * @param n - the node to visit
   */
  @Override
  public void visit(final TypeParameter n) {
    // f0 -> < IDENTIFIER >
    n.f0.accept(this);
    // f1 -> [ TypeBound() ]
    if (n.f1.present()) {
      n.f1.accept(this);
    }
  }

  /**
   * Visits a {@link TypeBound} node, whose children are the following :
   * <p>
   * f0 -> "extends"<br>
   * f1 -> ClassOrInterfaceType()<br>
   * f2 -> ( #0 "&" #1 ClassOrInterfaceType() )*<br>
   * 
   * @param n - the node to visit
   */
  @Override
  public void visit(final TypeBound n) {
    // f0 -> "extends"
    n.f0.accept(this);
    sb.append(" ");
    // f1 -> ClassOrInterfaceType()
    n.f1.accept(this);
    // f2 -> ( #0 "&" #1 ClassOrInterfaceType() )*
    if (n.f2.present()) {
      for (final Iterator<INode> e = n.f2.elements(); e.hasNext();) {
        sb.append(" ");
        final NodeSequence seq = (NodeSequence) e.next();
        // #0 "&"
        seq.elementAt(0).accept(this);
        sb.append(" ");
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
   * 
   * @param n - the node to visit
   */
  @Override
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
   * Visits a {@link ClassOrInterfaceBodyDeclaration} node, whose children are the following :
   * <p>
   * f0 -> . %0 Initializer()<br>
   * .. .. | %1 #0 Modifiers()<br>
   * .. .. . .. #1 ( &0 ClassOrInterfaceDeclaration()<br>
   * .. .. . .. .. | &1 EnumDeclaration()<br>
   * .. .. . .. .. | &2 ConstructorDeclaration()<br>
   * .. .. . .. .. | &3 FieldDeclaration()<br>
   * .. .. . .. .. | &4 MethodDeclaration() )<br>
   * .. .. | %2 ";"<br>
   */
  /**
   * Visits a {@link ClassOrInterfaceBodyDeclaration} node, whose child is the following :
   * <p>
   * f0 -> . %0 Initializer()<br>
   * .. .. | %1 #0 Modifiers()<br>
   * .. .. . .. #1 ( &0 ClassOrInterfaceDeclaration()<br>
   * .. .. . .. .. | &1 EnumDeclaration()<br>
   * .. .. . .. .. | &2 AnnotationTypeDeclaration()<br>
   * .. .. . .. .. | &3 ConstructorDeclaration()<br>
   * .. .. . .. .. | &4 FieldDeclaration()<br>
   * .. .. . .. .. | &5 MethodDeclaration() )<br>
   * .. .. | %2 ";"<br>
   * 
   * @param n - the node to visit
   */
  @Override
  public void visit(final ClassOrInterfaceBodyDeclaration n) {
    if (n.f0.which != 1) {
      // %0 Initializer() | %2  ";"
      n.f0.choice.accept(this);
    } else {
      // %1 #0 Modifiers() #1 ( &0 ClassOrInterfaceDeclaration() | &1 EnumDeclaration() | &2 AnnotationTypeDeclaration()
      //                      | &3 ConstructorDeclaration() | &4 FieldDeclaration() | &5 MethodDeclaration() )
      final NodeSequence seq = (NodeSequence) n.f0.choice;
      // #0 Modifiers print the last space if not empty
      seq.elementAt(0).accept(this);
      // #1 ( &0 ClassOrInterfaceDeclaration() | &1 EnumDeclaration() | &2 AnnotationTypeDeclaration()
      //    | &3 ConstructorDeclaration() | &4 FieldDeclaration() | &5 MethodDeclaration() )
      ((NodeChoice) seq.elementAt(1)).accept(this);
    }
  }

  /**
   * Visits a {@link FieldDeclaration} node, whose children are the following :
   * <p>
   * f0 -> Type()<br>
   * f1 -> VariableDeclarator()<br>
   * f2 -> ( #0 "," #1 VariableDeclarator() )*<br>
   * f3 -> ";"<br>
   * 
   * @param n - the node to visit
   */
  @Override
  public void visit(final FieldDeclaration n) {
    // f0 -> Type()
    n.f0.accept(this);
    sb.append(" ");
    // f1 -> VariableDeclarator()
    n.f1.accept(this);
    // f2 -> ( #0 "," #1 VariableDeclarator() )*
    if (n.f2.present()) {
      for (final Iterator<INode> e = n.f2.elements(); e.hasNext();) {
        // #0 ","
        final NodeSequence seq = (NodeSequence) e.next();
        seq.elementAt(0).accept(this);
        sb.append(" ");
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
   * 
   * @param n - the node to visit
   */
  @Override
  public void visit(final VariableDeclarator n) {
    // f0 -> VariableDeclaratorId()
    n.f0.accept(this);
    // f1 -> [ #0 "=" #1 VariableInitializer() ]
    if (n.f1.present()) {
      // #0 "="
      sb.append(" ");
      ((NodeSequence) n.f1.node).elementAt(0).accept(this);
      sb.append(" ");
      // #1 VariableInitializer()
      ((NodeSequence) n.f1.node).elementAt(1).accept(this);
    }
  }

  /**
   * Visits a {@link VariableDeclaratorId} node, whose children are the following :
   * <p>
   * f0 -> < IDENTIFIER ><br>
   * f1 -> ( #0 "[" #1 "]" )*<br>
   * 
   * @param n - the node to visit
   */
  @Override
  public void visit(final VariableDeclaratorId n) {
    // f0 -> < IDENTIFIER >
    n.f0.accept(this);
    // f1 -> ( #0 "[" #1 "]" )*
    if (n.f1.present()) {
      for (final Iterator<INode> e = n.f1.elements(); e.hasNext();) {
        final NodeSequence seq = (NodeSequence) e.next();
        // #0 "["
        seq.elementAt(0).accept(this);
        // #1 "]"
        seq.elementAt(1).accept(this);
      }
    }
  }

  /**
   * Visits a {@link VariableInitializer} node, whose children are the following :
   * <p>
   * f0 -> . %0 ArrayInitializer()<br>
   * .. .. | %1 Expression()<br>
   * 
   * @param n - the node to visit
   */
  @Override
  public void visit(final VariableInitializer n) {
    // no difference with super class
    n.f0.accept(this);
  }

  /**
   * Visits a {@link ArrayInitializer} node, whose children are the following :
   * <p>
   * f0 -> "{"<br>
   * f1 -> [ #0 VariableInitializer()<br>
   * .. .. . #1 ( $0 "," $1 VariableInitializer() )* ]<br>
   * f2 -> [ "," ]<br>
   * f3 -> "}"<br>
   * 
   * @param n - the node to visit
   */
  @Override
  public void visit(final ArrayInitializer n) {
    // f0 -> "{"
    n.f0.accept(this);
    sb.append(" ");
    // f1 -> [ #0 VariableInitializer() #1 ( $0 "," $1 VariableInitializer() )* ]
    n.f1.accept(this);
    // f2 -> [ "," ]
    if (n.f2.present()) {
      n.f2.accept(this);
      sb.append(" ");
    }
    sb.append(" ");
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
   * 
   * @param n - the node to visit
   */
  @Override
  public void visit(final MethodDeclaration n) {
    // f0 -> [ TypeParameters() ]
    if (n.f0.present()) {
      n.f0.accept(this);
      sb.append(" ");
    }
    // f1 -> ResultType()
    n.f1.accept(this);
    sb.append(" ");
    // f2 -> MethodDeclarator()
    n.f2.accept(this);
    sb.append(" ");
    // f3 -> [ #0 "throws" #1 NameList() ]
    if (n.f3.present()) {
      // #0 "throws"
      ((NodeSequence) n.f3.node).elementAt(0).accept(this);
      sb.append(" ");
      // #1 NameList()
      ((NodeSequence) n.f3.node).elementAt(1).accept(this);
    }
    // f4 -> ( %0 Block() | %1 ";" )
    n.f4.accept(this);
  }

  /**
   * Visits a {@link MethodDeclarator} node, whose children are the following :
   * <p>
   * f0 -> < IDENTIFIER ><br>
   * f1 -> FormalParameters()<br>
   * f2 -> ( #0 "[" #1 "]" )*<br>
   * 
   * @param n - the node to visit
   */
  @Override
  public void visit(final MethodDeclarator n) {
    // f0 -> < IDENTIFIER >
    n.f0.accept(this);
    // f1 -> FormalParameters()
    n.f1.accept(this);
    // f2 -> ( #0 "[" #1 "]" )*
    if (n.f2.present()) {
      for (final Iterator<INode> e = n.f2.elements(); e.hasNext();) {
        // #0 "["
        ((NodeSequence) e).elementAt(0).accept(this);
        // #1 "]"
        ((NodeSequence) e).elementAt(1).accept(this);
      }
    }
  }

  /**
   * Visits a {@link FormalParameters} node, whose children are the following :
   * <p>
   * f0 -> "("<br>
   * f1 -> [ #0 FormalParameter()<br>
   * .. .. . #1 ( $0 "," $1 FormalParameter() )* ]<br>
   * f2 -> ")"<br>
   * 
   * @param n - the node to visit
   */
  @Override
  public void visit(final FormalParameters n) {
    // f0 -> "("
    n.f0.accept(this);
    // f1 -> [ #0 FormalParameter() #1 ( $0 "," $1 FormalParameter() )* ]
    if (n.f1.present()) {
      final NodeSequence seq = (NodeSequence) n.f1.node;
      // #0 FormalParameter()
      seq.elementAt(0).accept(this);
      // #1 ( $0 "," $1 FormalParameter() )*
      for (final Iterator<INode> e = ((NodeListOptional) seq.elementAt(1)).elements(); e.hasNext();) {
        final NodeSequence seq1 = (NodeSequence) e.next();
        // $0 ","
        seq1.elementAt(0).accept(this);
        sb.append(" ");
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
   * 
   * @param n - the node to visit
   */
  @Override
  public void visit(final FormalParameter n) {
    // f0 -> Modifiers()
    n.f0.accept(this);
    // Modifiers print the last space if not empty
    // f1 -> Type()
    n.f1.accept(this);
    // f2 -> [ "..." ]
    if (n.f2.present())
      n.f2.accept(this);
    sb.append(" ");
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
   * 
   * @param n - the node to visit
   */
  @Override
  public void visit(final ConstructorDeclaration n) {
    // f0 -> [ TypeParameters() ]
    if (n.f0.present()) {
      n.f0.accept(this);
      sb.append(" ");
    }
    // f1 -> < IDENTIFIER >
    n.f1.accept(this);
    // f2 -> FormalParameters()
    n.f2.accept(this);
    sb.append(" ");
    // f3 -> [ #0 "throws" #1 NameList() ]
    if (n.f3.present()) {
      // #0 "throws"
      ((NodeSequence) n.f3.node).elementAt(0).accept(this);
      sb.append(" ");
      // #1 NameList()
      ((NodeSequence) n.f3.node).elementAt(1).accept(this);
    }
    sb.append(" ");
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
   *
   * @param n - the node to visit
   */
  @Override
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
          sb.append(" ");
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
              sb.append(" ");
              // ?1 ReferenceType()
              seq2.elementAt(1).accept(this);
            }
          }
          // $3 ">"
          sb.append(" ");
          seq1.elementAt(3).accept(this);
          sb.append(" ");
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
            sb.append(" ");
            // $1 Arguments()
            seq1.elementAt(1).accept(this);
            // $2 ";"
            sb.append(" ");
            seq1.elementAt(2).accept(this);
            sb.append(" ");
            break;
          case 1:
            // $0 "super" $1 Arguments() $2 ";" 
            final NodeSequence seq2 = (NodeSequence) ich1;
            seq2.elementAt(0).accept(this);
            sb.append(" ");
            // $1 Arguments()
            seq2.elementAt(1).accept(this);
            // $2 ";"
            sb.append(" ");
            seq2.elementAt(2).accept(this);
            sb.append(" ");
            break;
          default:
            // should not occur !!!
            break;
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
        // should not occur !!!
        break;
    }

    // old
    //    * f0 -> . %0 #0 [ $0 "<" $1 ReferenceType()<br>
    //    * .. .. . .. .. . $2 ( ?0 "," ?1 ReferenceType() )*<br>
    //    * .. .. . .. .. . $3 ">" ]<br>
    //    * .. .. . .. #1 "this" #2 Arguments() #3 ";"<br>
    //    * .. .. | %1 #0 [ $0 PrimaryExpression() $1 "." ]<br>
    //    * .. .. . .. #1 "super" #2 Arguments() #3 ";"<br>
    //    *
    //    final NodeSequence seq = (NodeSequence) n.f0.choice;
    //    if (n.f0.which == 0) {
    //      // %0 #0 [ $0 "<" $1 ReferenceType() $2 ( ?0 "," ?1 ReferenceType() )* $3 ">" ] #1 "this" #2 Arguments() #3 ";"
    //      final NodeOptional opt = (NodeOptional) seq.elementAt(0);
    //      if (opt.present()) {
    //        // $0 "<" $1 ReferenceType() $2 ( ?0 "," ?1 ReferenceType() )* $3 ">"
    //        final NodeSequence seq1 = (NodeSequence) opt.node;
    //        // $0 "<"
    //        seq1.elementAt(0).accept(this);
    //        sb.append(" ");
    //        // $1 ReferenceType()
    //        seq1.elementAt(1).accept(this);
    //        // $2 ( ?0 "," ?1 ReferenceType() )*
    //        final NodeListOptional nlo1 = (NodeListOptional) seq1.elementAt(2);
    //        if (nlo1.present())
    //          for (final Iterator<INode> e1 = nlo1.elements(); e1.hasNext();) {
    //            final NodeSequence seq2 = (NodeSequence) e1.next();
    //            // ?0 ","
    //            seq2.elementAt(0).accept(this);
    //            sb.append(" ");
    //            // ?1 ReferenceType()
    //            seq2.elementAt(1).accept(this);
    //          }
    //        // $3 ">"
    //        sb.append(" ");
    //        seq1.elementAt(3).accept(this);
    //      }
    //      // #1 "this"
    //      seq.elementAt(1).accept(this);
    //      // #2 Arguments()
    //      seq.elementAt(2).accept(this);
    //      // #3 ";"
    //      seq.elementAt(3).accept(this);
    //    } else {
    //      // %1 #0 [ $0 PrimaryExpression() $1 "." ] #1 "super" #2 Arguments() #3 ";"
    //      final NodeOptional opt = (NodeOptional) seq.elementAt(0);
    //      // #0 [ $0 PrimaryExpression() $1 "." ]
    //      if (opt.present()) {
    //        // $0 PrimaryExpression()
    //        ((NodeSequence) opt.node).elementAt(0).accept(this);
    //        // $1 "."
    //        ((NodeSequence) opt.node).elementAt(1).accept(this);
    //      }
    //      // #1 "super"
    //      seq.elementAt(1).accept(this);
    //      // #2 Arguments()
    //      seq.elementAt(2).accept(this);
    //      // #3 ";"
    //      seq.elementAt(3).accept(this);
    //    }
  }

  /**
   * Visits a {@link Initializer} node, whose children are the following :
   * <p>
   * f0 -> [ "static" ]<br>
   * f1 -> Block()<br>
   * 
   * @param n - the node to visit
   */
  @Override
  public void visit(final Initializer n) {
    // f0 -> [ "static" ]
    if (n.f0.present()) {
      n.f0.accept(this);
      sb.append(" ");
    }
    // f1 -> Block()
    n.f1.accept(this);
  }

  /**
   * Visits a {@link Type} node, whose children are the following :
   * <p>
   * f0 -> . %0 ReferenceType()<br>
   * .. .. | %1 PrimitiveType()<br>
   * 
   * @param n - the node to visit
   */
  @Override
  public void visit(final Type n) {
    n.f0.accept(this);
  }

  /**
   * Visits a {@link ReferenceType} node, whose children are the following :
   * <p>
   * f0 -> . %0 #0 PrimitiveType()<br>
   * .. .. . .. #1 ( $0 "[" $1 "]" )+<br>
   * .. .. | %1 #0 ClassOrInterfaceType()<br>
   * .. .. . .. #1 ( $0 "[" $1 "]" )*<br>
   * 
   * @param n - the node to visit
   */
  @Override
  public void visit(final ReferenceType n) {
    final NodeSequence seq = (NodeSequence) n.f0.choice;
    if (n.f0.which == 0) {
      // %0 #0 PrimitiveType() #1 ( $0 "[" $1 "]" )+
      // #0 PrimitiveType()
      seq.elementAt(0).accept(this);
      // #1 ( $0 "[" $1 "]" )+
      final NodeList nl = (NodeList) seq.elementAt(1);
      for (final Iterator<INode> e = nl.elements(); e.hasNext();) {
        final NodeSequence seq1 = (NodeSequence) e.next();
        // $0 "["
        seq1.elementAt(0).accept(this);
        // $1 "]"
        seq1.elementAt(1).accept(this);
      }
    } else {
      // %1 #0 ClassOrInterfaceType() #1 ( $0 "[" $1 "]" )*
      // #0 ClassOrInterfaceType()
      seq.elementAt(0).accept(this);
      // #1 ( $0 "[" $1 "]" )*
      final NodeListOptional nlo = (NodeListOptional) seq.elementAt(1);
      if (nlo.present())
        for (final Iterator<INode> e = nlo.elements(); e.hasNext();) {
          final NodeSequence seq1 = (NodeSequence) e.next();
          // $0 "["
          seq1.elementAt(0).accept(this);
          // $1 "]"
          seq1.elementAt(1).accept(this);
        }
    }
  }

  /**
   * Visits a {@link ClassOrInterfaceType} node, whose children are the following :
   * <p>
   * f0 -> < IDENTIFIER ><br>
   * f1 -> [ TypeArguments() ]<br>
   * f2 -> ( #0 "." #1 < IDENTIFIER ><br>
   * .. .. . #2 [ TypeArguments() ] )*<br>
   * 
   * @param n - the node to visit
   */
  @Override
  public void visit(final ClassOrInterfaceType n) {
    // f0 -> < IDENTIFIER >
    n.f0.accept(this);
    // f1 -> [ TypeArguments() ]
    if (n.f1.present())
      n.f1.accept(this);
    // f2 -> ( #0 "." #1 < IDENTIFIER > #2 [ TypeArguments() ] )*
    if (n.f2.present()) {
      for (final Iterator<INode> e = n.f2.elements(); e.hasNext();) {
        final NodeSequence seq = (NodeSequence) e.next();
        // #0 "."
        seq.elementAt(0).accept(this);
        // #1 < IDENTIFIER >
        seq.elementAt(1).accept(this);
        // #2 TypeArguments()
        seq.elementAt(2).accept(this);
      }
    }
  }

/**
   * Visits a {@link TypeArguments} node, whose children are the following :
   * <p>
   * f0 -> "<"<br>
   * f1 -> TypeArgument()<br>
   * f2 -> ( #0 "," #1 TypeArgument() )*<br>
   * f3 -> ">"<br>
   *
   * @param n - the node to visit
   */
  @Override
  public void visit(final TypeArguments n) {
    // f0 -> "<"
    n.f0.accept(this);
    // f1 -> TypeArgument()
    n.f1.accept(this);
    // f2 -> ( #0 "," #1 TypeArgument() )*
    if (n.f2.present()) {
      sb.append(" ");
      for (final Iterator<INode> e = n.f2.elements(); e.hasNext();) {
        final NodeSequence seq = (NodeSequence) e.next();
        // #0 ","
        seq.elementAt(0).accept(this);
        sb.append(" ");
        // #1 TypeArgument()
        seq.elementAt(1).accept(this);
      }
    }
    // f3 -> ">"
    n.f3.accept(this);
  }

  /**
   * Visits a {@link TypeArgument} node, whose children are the following :
   * <p>
   * f0 -> . %0 ReferenceType()<br>
   * .. .. | %1 #0 "?"<br>
   * .. .. . .. #1 [ WildcardBounds() ]<br>
   * 
   * @param n - the node to visit
   */
  @Override
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
        sb.append(" ");
        opt.node.accept(this);
      }
    }
  }

  /**
   * Visits a {@link WildcardBounds} node, whose children are the following :
   * <p>
   * f0 -> . %0 #0 "extends" #1 ReferenceType()<br>
   * .. .. | %1 #0 "super" #1 ReferenceType()<br>
   * 
   * @param n - the node to visit
   */
  @Override
  public void visit(final WildcardBounds n) {
    final NodeSequence seq = (NodeSequence) n.f0.choice;
    // #0 "extends" | #0 "super"
    seq.elementAt(0).accept(this);
    sb.append(" ");
    // #1 ReferenceType()
    seq.elementAt(1).accept(this);
  }

  /**
   * Visits a {@link PrimitiveType} node, whose children are the following :
   * <p>
   * f0 -> . %0 "boolean"<br>
   * .. .. | %1 "char"<br>
   * .. .. | %2 "byte"<br>
   * .. .. | %3 "short"<br>
   * .. .. | %4 "int"<br>
   * .. .. | %5 "long"<br>
   * .. .. | %6 "float"<br>
   * .. .. | %7 "double"<br>
   * 
   * @param n - the node to visit
   */
  @Override
  public void visit(final PrimitiveType n) {
    // no difference with superclass
    n.f0.accept(this);
  }

  /**
   * Visits a {@link ResultType} node, whose children are the following :
   * <p>
   * f0 -> ( %0 "void"<br>
   * .. .. | %1 Type() )<br>
   * 
   * @param n - the node to visit
   */
  @Override
  public void visit(final ResultType n) {
    // no difference with super class
    n.f0.accept(this);
  }

  /**
   * Visits a {@link Name} node, whose children are the following :
   * <p>
   * f0 -> JavaIdentifier()<br>
   * f1 -> ( #0 "." #1 JavaIdentifier() )*<br>
   * 
   * @param n - the node to visit
   */
  @Override
  public void visit(final Name n) {
    // f0 -> JavaIdentifier()
    n.f0.accept(this);
    // f1 -> ( #0 "." #1 JavaIdentifier() )*
    if (n.f1.present()) {
      for (final Iterator<INode> e = n.f1.elements(); e.hasNext();) {
        final NodeSequence seq = (NodeSequence) e.next();
        // #0 "."
        seq.elementAt(0).accept(this);
        // #1 JavaIdentifier()
        seq.elementAt(1).accept(this);
      }
    }
  }

  /**
   * Visits a {@link NameList} node, whose children are the following :
   * <p>
   * f0 -> Name()<br>
   * f1 -> ( #0 "," #1 Name() )*<br>
   * 
   * @param n - the node to visit
   */
  @Override
  public void visit(final NameList n) {
    // f0 -> Name()
    n.f0.accept(this);
    // f1 -> ( #0 "," #1 Name() )*
    if (n.f1.present()) {
      for (final Iterator<INode> e = n.f1.elements(); e.hasNext();) {
        // #0 ","
        final NodeSequence seq = (NodeSequence) e.next();
        seq.elementAt(0).accept(this);
        sb.append(" ");
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
   * 
   * @param n - the node to visit
   */
  @Override
  public void visit(final Expression n) {
    // f0 -> ConditionalExpression()
    n.f0.accept(this);
    // f1 -> [ #0 AssignmentOperator() #1 Expression() ]
    if (n.f1.present()) {
      final NodeSequence seq = (NodeSequence) n.f1.node;
      sb.append(" ");
      // #0 AssignmentOperator()
      seq.elementAt(0).accept(this);
      sb.append(" ");
      // #1 Expression()
      seq.elementAt(1).accept(this);
    }
  }

  /**
   * Visits a {@link AssignmentOperator} node, whose children are the following :
   * <p>
   * f0 -> . %00 "="<br>
   * .. .. | %01 "*="<br>
   * .. .. | %02 "/="<br>
   * .. .. | %03 "%="<br>
   * .. .. | %04 "+="<br>
   * .. .. | %05 "-="<br>
   * .. .. | %06 "<<="<br>
   * .. .. | %07 ">>="<br>
   * .. .. | %08 ">>>="<br>
   * .. .. | %09 "&="<br>
   * .. .. | %10 "^="<br>
   * .. .. | %11 "|="<br>
   * 
   * @param n - the node to visit
   */
  @Override
  public void visit(final AssignmentOperator n) {
    // no difference with super class
    n.f0.accept(this);
  }

  /**
   * Visits a {@link ConditionalExpression} node, whose children are the following :
   * <p>
   * f0 -> ConditionalOrExpression()<br>
   * f1 -> [ #0 "?" #1 Expression() #2 ":" #3 Expression() ]<br>
   * 
   * @param n - the node to visit
   */
  @Override
  public void visit(final ConditionalExpression n) {
    // f0 -> ConditionalOrExpression()
    n.f0.accept(this);
    // f1 -> [ #0 "?" #1 Expression() #2 ":" #3 Expression() ]
    if (n.f1.present()) {
      final NodeSequence seq = (NodeSequence) n.f1.node;
      sb.append(" ");
      // #0 "?"
      seq.elementAt(0).accept(this);
      sb.append(" ");
      // #1 Expression()
      seq.elementAt(1).accept(this);
      sb.append(" ");
      // #2 ":"
      seq.elementAt(2).accept(this);
      sb.append(" ");
      // #3 Expression()
      seq.elementAt(3).accept(this);
    }
  }

  /**
   * Visits a {@link ConditionalOrExpression} node, whose children are the following :
   * <p>
   * f0 -> ConditionalAndExpression()<br>
   * f1 -> ( #0 "||" #1 ConditionalAndExpression() )*<br>
   * 
   * @param n - the node to visit
   */
  @Override
  public void visit(final ConditionalOrExpression n) {
    // f0 -> ConditionalAndExpression()
    n.f0.accept(this);
    // f1 -> ( #0 "||" #1 ConditionalAndExpression() )*
    if (n.f1.present()) {
      for (final Iterator<INode> e = n.f1.elements(); e.hasNext();) {
        final NodeSequence seq = (NodeSequence) e.next();
        sb.append(" ");
        // #0 "||"
        seq.elementAt(0).accept(this);
        sb.append(" ");
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
   * 
   * @param n - the node to visit
   */
  @Override
  public void visit(final ConditionalAndExpression n) {
    // f0 -> InclusiveOrExpression()
    n.f0.accept(this);
    // f1 -> ( #0 "&&" #1 InclusiveOrExpression() )*
    if (n.f1.present()) {
      for (final Iterator<INode> e = n.f1.elements(); e.hasNext();) {
        final NodeSequence seq = (NodeSequence) e.next();
        sb.append(" ");
        // #0 "&&"
        seq.elementAt(0).accept(this);
        sb.append(" ");
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
   * 
   * @param n - the node to visit
   */
  @Override
  public void visit(final InclusiveOrExpression n) {
    // f0 -> ExclusiveOrExpression()
    n.f0.accept(this);
    // f1 -> ( #0 "|" #1 ExclusiveOrExpression() )*
    if (n.f1.present()) {
      for (final Iterator<INode> e = n.f1.elements(); e.hasNext();) {
        final NodeSequence seq = (NodeSequence) e.next();
        sb.append(" ");
        // #0 "|"
        seq.elementAt(0).accept(this);
        sb.append(" ");
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
   * 
   * @param n - the node to visit
   */
  @Override
  public void visit(final ExclusiveOrExpression n) {
    // f0 -> AndExpression()
    n.f0.accept(this);
    // f1 -> ( #0 "^" #1 AndExpression() )*
    if (n.f1.present()) {
      for (final Iterator<INode> e = n.f1.elements(); e.hasNext();) {
        final NodeSequence seq = (NodeSequence) e.next();
        sb.append(" ");
        // #0 "^"
        seq.elementAt(0).accept(this);
        sb.append(" ");
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
   * 
   * @param n - the node to visit
   */
  @Override
  public void visit(final AndExpression n) {
    // f0 -> EqualityExpression()
    n.f0.accept(this);
    // f1 -> ( #0 "&" #1 EqualityExpression() )*
    if (n.f1.present()) {
      for (final Iterator<INode> e = n.f1.elements(); e.hasNext();) {
        final NodeSequence seq = (NodeSequence) e.next();
        sb.append(" ");
        // #0 "&"
        seq.elementAt(0).accept(this);
        sb.append(" ");
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
   * .. .. . .. | %1 "!=" ) #1 InstanceOfExpression() )*<br>
   * 
   * @param n - the node to visit
   */
  @Override
  public void visit(final EqualityExpression n) {
    // f0 -> InstanceOfExpression()
    n.f0.accept(this);
    // f1 -> ( #0 ( %0 "==" | %1 "!=" ) #1 InstanceOfExpression() )*
    if (n.f1.present()) {
      for (final Iterator<INode> e = n.f1.elements(); e.hasNext();) {
        final NodeSequence seq = (NodeSequence) e.next();
        sb.append(" ");
        // %0 "==" | %1 "!="
        seq.elementAt(0).accept(this);
        sb.append(" ");
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
   * 
   * @param n - the node to visit
   */
  @Override
  public void visit(final InstanceOfExpression n) {
    // f0 -> RelationalExpression()
    n.f0.accept(this);
    // f1 -> [ #0 "instanceof" #1 Type() ]
    if (n.f1.present()) {
      sb.append(" ");
      // #0 "instanceof"
      ((NodeSequence) n.f1.node).elementAt(0).accept(this);
      sb.append(" ");
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
   * .. .. . .. | %3 ">=" ) #1 ShiftExpression() )*<br>
   *
   * @param n - the node to visit
   */
  @Override
  public void visit(final RelationalExpression n) {
    // f0 -> ShiftExpression()
    n.f0.accept(this);
    // f1 -> ( 0 ( %0 "<" | %1 ">" | %2 "<=" | %3 ">=" ) #1 ShiftExpression() )*
    if (n.f1.present()) {
      for (final Iterator<INode> e = n.f1.elements(); e.hasNext();) {
        final NodeSequence seq = (NodeSequence) e.next();
        sb.append(" ");
        // %0 "<" | %1 ">" | %2 "<=" | %3 ">="
        seq.elementAt(0).accept(this);
        sb.append(" ");
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
 * .. .. . .. | %2 RSignedShift() ) #1 AdditiveExpression() )*<br>
   *
   * @param n - the node to visit
   */
  @Override
  public void visit(final ShiftExpression n) {
    // f0 -> AdditiveExpression()
    n.f0.accept(this);
    // f1 -> ( #0 ( %0 "<<" | %1 RUnsignedShift() | %2 RSignedShift() ) #1 AdditiveExpression() )*
    if (n.f1.present()) {
      for (final Iterator<INode> e = n.f1.elements(); e.hasNext();) {
        final NodeSequence seq = (NodeSequence) e.next();
        sb.append(" ");
        // #0 ( %0 "<<" | %1 RUnsignedShift() | %2 RSignedShift() )
        seq.elementAt(0).accept(this);
        sb.append(" ");
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
   * .. .. . .. | %1 "-" ) #1 MultiplicativeExpression() )*<br>
   * 
   * @param n - the node to visit
   */
  @Override
  public void visit(final AdditiveExpression n) {
    // f0 -> MultiplicativeExpression()
    n.f0.accept(this);
    // f1 -> ( #0 ( %0 "+" | %1 "-" ) #1 MultiplicativeExpression() )*
    if (n.f1.present()) {
      for (final Iterator<INode> e = n.f1.elements(); e.hasNext();) {
        final NodeSequence seq = (NodeSequence) e.next();
        sb.append(" ");
        // #0 ( %0 "+" | %1 "-" )
        seq.elementAt(0).accept(this);
        sb.append(" ");
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
   * .. .. . .. | %2 "%" ) #1 UnaryExpression() )*<br>
   * 
   * @param n - the node to visit
   */
  @Override
  public void visit(final MultiplicativeExpression n) {
    // f0 -> UnaryExpression()
    n.f0.accept(this);
    // f1 -> ( #0 ( %0 "*" | %1 "/" | %2 "%" ) #1 UnaryExpression() )*
    if (n.f1.present()) {
      for (final Iterator<INode> e = n.f1.elements(); e.hasNext();) {
        final NodeSequence seq = (NodeSequence) e.next();
        sb.append(" ");
        // %0 "*" | %1 "/" | %2 "%"
        seq.elementAt(0).accept(this);
        sb.append(" ");
        // #1 UnaryExpression()
        seq.elementAt(1).accept(this);
      }
    }
  }

  /**
   * Visits a {@link UnaryExpression} node, whose children are the following :
   * <p>
   * f0 -> . %0 #0 ( &0 "+"<br>
   * .. .. . .. .. | &1 "-" ) #1 UnaryExpression()<br>
   * .. .. | %1 PreIncrementExpression()<br>
   * .. .. | %2 PreDecrementExpression()<br>
   * .. .. | %3 UnaryExpressionNotPlusMinus()<br>
   * 
   * @param n - the node to visit
   */
  @Override
  public void visit(final UnaryExpression n) {
    if (n.f0.which == 0) {
      // %0 #0 ( &0 "+" | &1 "-" ) #1 UnaryExpression()
      final NodeSequence seq = (NodeSequence) n.f0.choice;
      // &0 "+" | &1 "-"
      seq.elementAt(0).accept(this);
      // #1 UnaryExpression()
      seq.elementAt(1).accept(this);
    } else
      // %1 PreIncrementExpression() | %2 PreDecrementExpression() | %3 UnaryExpressionNotPlusMinus()
      n.f0.accept(this);
  }

  /**
   * Visits a {@link PreIncrementExpression} node, whose children are the following :
   * <p>
   * f0 -> "++"<br>
   * f1 -> PrimaryExpression()<br>
   * 
   * @param n - the node to visit
   */
  @Override
  public void visit(final PreIncrementExpression n) {
    // f0 -> "++"
    n.f0.accept(this);
    // f1 -> PrimaryExpression()
    n.f1.accept(this);
  }

  /**
   * Visits a {@link PreDecrementExpression} node, whose children are the following :
   * <p>
   * f0 -> "--"<br>
   * f1 -> PrimaryExpression()<br>
   * 
   * @param n - the node to visit
   */
  @Override
  public void visit(final PreDecrementExpression n) {
    // f0 -> "--"
    n.f0.accept(this);
    // f1 -> PrimaryExpression()
    n.f1.accept(this);
  }

  /**
   * Visits a {@link UnaryExpressionNotPlusMinus} node, whose children are the following :
   * <p>
   * f0 -> . %0 #0 ( &0 "~"<br>
   * .. .. . .. .. | &1 "!" ) #1 UnaryExpression()<br>
   * .. .. | %1 CastExpression()<br>
   * .. .. | %2 PostfixExpression()<br>
   * 
   * @param n - the node to visit
   */
  @Override
  public void visit(final UnaryExpressionNotPlusMinus n) {
    if (n.f0.which == 0) {
      // %0 #0 ( &0 "~" | &1 "!" ) #1 UnaryExpression()
      final NodeSequence seq = (NodeSequence) n.f0.choice;
      // &0 "~" | &1 "!"
      seq.elementAt(0).accept(this);
      // #1 UnaryExpression()
      seq.elementAt(1).accept(this);
    } else
      // %1 CastExpression() | %2 PostfixExpression()
      n.f0.accept(this);
  }

  /**
   * Visits a {@link CastLookahead} node, whose children are the following :
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
   * 
   * @param n - the node to visit
   */
  @Override
  public void visit(@SuppressWarnings("unused") final CastLookahead n) {
    sb.append("/* !!! CastLookahead visited but should not !!! */");
  }

  /**
   * Visits a {@link PostfixExpression} node, whose children are the following :
   * <p>
   * f0 -> PrimaryExpression()<br>
   * f1 -> [ %0 "++"<br>
   * .. .. | %1 "--" ]<br>
   * 
   * @param n - the node to visit
   */
  @Override
  public void visit(final PostfixExpression n) {
    // f0 -> PrimaryExpression()
    n.f0.accept(this);
    // f1 -> [ %0 "++" | %1 "--" ]
    n.f1.accept(this);
  }

  /**
   * Visits a {@link CastExpression} node, whose children are the following :
   * <p>
   * f0 -> . %0 #0 "(" #1 Type() #2 ")" #3 UnaryExpression()<br>
   * .. .. | %1 #0 "(" #1 Type() #2 ")" #3 UnaryExpressionNotPlusMinus()<br>
   * 
   * @param n - the node to visit
   */
  @Override
  public void visit(final CastExpression n) {
    final NodeSequence seq = (NodeSequence) n.f0.choice;
    // #0 "("
    seq.elementAt(0).accept(this);
    // #1 Type()
    seq.elementAt(1).accept(this);
    // #2 ")"
    seq.elementAt(2).accept(this);
    // #3 UnaryExpression() | #3 UnaryExpressionNotPlusMinus()
    seq.elementAt(3).accept(this);
  }

  /**
   * Visits a {@link PrimaryExpression} node, whose children are the following :
   * <p>
   * f0 -> PrimaryPrefix()<br>
   * f1 -> ( PrimarySuffix() )*<br>
   * 
   * @param n - the node to visit
   */
  @Override
  public void visit(final PrimaryExpression n) {
    // f0 -> PrimaryPrefix()
    n.f0.accept(this);
    // f1 -> ( PrimarySuffix() )*
    if (n.f1.present()) {
      for (final Iterator<INode> e = n.f1.elements(); e.hasNext();) {
        e.next().accept(this);
      }
    }
  }

  /**
   * Visits a {@link MemberSelector} node, whose children are the following :
   * <p>
   * f0 -> "."<br>
   * f1 -> TypeArguments()<br>
   * f2 -> < IDENTIFIER ><br>
   * 
   * @param n - the node to visit
   */
  @Override
  public void visit(final MemberSelector n) {
    // f0 -> "."
    n.f0.accept(this);
    // f1 -> TypeArguments()
    n.f1.accept(this);
    // f2 -> < IDENTIFIER >
    n.f2.accept(this);
  }

  /**
   * Visits a {@link PrimaryPrefix} node, whose children are the following :
   * <p>
   * f0 -> . %0 Literal()<br>
   * .. .. | %1 "this"<br>
   * .. .. | %2 #0 "super" #1 "." #2 < IDENTIFIER ><br>
   * .. .. | %3 #0 "(" #1 Expression() #2 ")"<br>
   * .. .. | %4 AllocationExpression()<br>
   * .. .. | %5 #0 ResultType() #1 "." #2 "class"<br>
   * .. .. | %6 Name()<br>
   * 
   * @param n - the node to visit
   */
  @Override
  public void visit(final PrimaryPrefix n) {
    if (n.f0.which == 2 || n.f0.which == 3 || n.f0.which == 5) {
      // #0 "super" #1 "." #2 < IDENTIFIER > | %3 #0 "(" #1 Expression() #2 ")" | #0 ResultType() #1 "." #2 "class"
      // #0 "super" | #0 "(" | #0 ResultType()
      (((NodeSequence) n.f0.choice).elementAt(0)).accept(this);
      // #1 "." | #1 Expression() | #1 "."
      (((NodeSequence) n.f0.choice).elementAt(1)).accept(this);
      // #2 < IDENTIFIER > | #2 ")" | #2 "class"
      (((NodeSequence) n.f0.choice).elementAt(2)).accept(this);
    } else
      // %0 Literal() | %1 "this" | %4 AllocationExpression() | %6 Name()
      n.f0.choice.accept(this);
  }

  /**
   * Visits a {@link PrimarySuffix} node, whose children are the following :
   * <p>
   * f0 -> . %0 #0 "." #1 "this"<br>
   * .. .. | %1 #0 "." #1 AllocationExpression()<br>
   * .. .. | %2 MemberSelector()<br>
   * .. .. | %3 #0 "[" #1 Expression() #2 "]"<br>
   * .. .. | %4 #0 "." #1 < IDENTIFIER ><br>
   * .. .. | %5 Arguments()<br>
   * 
   * @param n - the node to visit
   */
  @Override
  public void visit(final PrimarySuffix n) {
    if (n.f0.which == 0 || n.f0.which == 1 || n.f0.which == 4) {
      // %0 #0 "." #1 "this" | %1 #0 "." #1 AllocationExpression() | %4 #0 "." #1 < IDENTIFIER >
      // #0 "."
      ((NodeSequence) n.f0.choice).elementAt(0).accept(this);
      // #1 "this" | #1 AllocationExpression() | #1 < IDENTIFIER >
      ((NodeSequence) n.f0.choice).elementAt(1).accept(this);
    } else if (n.f0.which == 3) {
      // %3 #0 "[" #1 Expression() #2 "]"
      // #0 "["
      ((NodeSequence) n.f0.choice).elementAt(0).accept(this);
      // #1 Expression()
      ((NodeSequence) n.f0.choice).elementAt(1).accept(this);
      // #2 "]"
      ((NodeSequence) n.f0.choice).elementAt(2).accept(this);
    } else
      // %2 MemberSelector()
      n.f0.accept(this);
  }

  /**
   * Visits a {@link Literal} node, whose children are the following :
   * <p>
   * f0 -> . %0 < INTEGER_LITERAL ><br>
   * .. .. | %1 < FLOATING_POINT_LITERAL ><br>
   * .. .. | %2 < CHARACTER_LITERAL ><br>
   * .. .. | %3 < STRING_LITERAL ><br>
   * .. .. | %4 BooleanLiteral()<br>
   * .. .. | %5 NullLiteral()<br>
   * 
   * @param n - the node to visit
   */
  @Override
  public void visit(final Literal n) {
    if (n.f0.which <= 3) {
      // %0 < INTEGER_LITERAL > | %1 < FLOATING_POINT_LITERAL > | %2 < CHARACTER_LITERAL > | %3 < STRING_LITERAL >
      sb.append(UnicodeConverter.addUnicodeEscapes(((NodeToken) n.f0.choice).withSpecials(spc.spc)));
    } else {
      // %4 BooleanLiteral() | %5 NullLiteral()
      n.f0.choice.accept(this);
    }
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
    sb.append(n.f0.withSpecials(spc.spc));
  }

  /**
   * Visits a {@link BooleanLiteral} node, whose children are the following :
   * <p>
   * f0 -> . %0 "true"<br>
   * .. .. | %1 "false"<br>
   * 
   * @param n - the node to visit
   */
  @Override
  public void visit(final BooleanLiteral n) {
    sb.append(((NodeToken) n.f0.choice).withSpecials(spc.spc));
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
    sb.append(n.f0.withSpecials(spc.spc));
  }

  /**
   * Visits a {@link NullLiteral} node, whose children are the following :
   * <p>
   * f0 -> "null"<br>
   * 
   * @param n - the node to visit
   */
  @Override
  public void visit(final NullLiteral n) {
    sb.append(n.f0.withSpecials(spc.spc));
  }

  /**
   * Visits a {@link Arguments} node, whose children are the following :
   * <p>
   * f0 -> "("<br>
   * f1 -> [ ArgumentList() ]<br>
   * f2 -> ")"<br>
   * 
   * @param n - the node to visit
   */
  @Override
  public void visit(final Arguments n) {
    // f0 -> "("
    n.f0.accept(this);
    // f1 -> [ ArgumentList() ]
    if (n.f1.present())
      n.f1.accept(this);
    // f2 -> ")"
    n.f2.accept(this);
  }

  /**
   * Visits a {@link ArgumentList} node, whose children are the following :
   * <p>
   * f0 -> Expression()<br>
   * f1 -> ( #0 "," #1 Expression() )*<br>
   * 
   * @param n - the node to visit
   */
  @Override
  public void visit(final ArgumentList n) {
    // f0 -> Expression()
    n.f0.accept(this);
    // f1 -> ( #0 "," #1 Expression() )*
    if (n.f1.present()) {
      for (final Iterator<INode> e = n.f1.elements(); e.hasNext();) {
        final NodeSequence seq = (NodeSequence) e.next();
        // #0 ","
        seq.elementAt(0).accept(this);
        sb.append(" ");
        // #1 Expression()
        seq.elementAt(1).accept(this);
      }
    }
  }

  /**
   * Visits a {@link AllocationExpression} node, whose children are the following :
   * <p>
   * f0 -> . %0 #0 "new" #1 PrimitiveType() #2 ArrayDimsAndInits()<br>
   * .. .. | %1 #0 "new" #1 ClassOrInterfaceType()<br>
   * .. .. . .. #2 [ TypeArguments() ]<br>
   * .. .. . .. #3 ( &0 ArrayDimsAndInits()<br>
   * .. .. . .. .. | &1 $0 Arguments()<br>
   * .. .. . .. .. . .. $1 [ ClassOrInterfaceBody() ] )<br>
   * 
   * @param n - the node to visit
   */
  @Override
  public void visit(final AllocationExpression n) {
    final NodeSequence seq = (NodeSequence) n.f0.choice;
    if (n.f0.which == 0) {
      // %0 #0 "new" #1 PrimitiveType() #2 ArrayDimsAndInits()
      seq.elementAt(0).accept(this);
      sb.append(" ");
      seq.elementAt(1).accept(this);
      seq.elementAt(2).accept(this);
    } else {
      // %1 #0 "new" #1 ClassOrInterfaceType() #2 [ TypeArguments() ]
      //    #3 ( &0 ArrayDimsAndInits() | &1 $0 Arguments() $1 [ ClassOrInterfaceBody() ] )
      // #0 "new"
      seq.elementAt(0).accept(this);
      sb.append(" ");
      // #1 ClassOrInterfaceType()
      seq.elementAt(1).accept(this);
      // #2 [ TypeArguments() ]
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
        //$0  Arguments()
        seq1.elementAt(0).accept(this);
        // $1 [ ClassOrInterfaceBody() ]
        if (((NodeOptional) seq1.elementAt(1)).present())
          seq1.elementAt(1).accept(this);
      }
    }
  }

  /**
   * Visits a {@link ArrayDimsAndInits} node, whose children are the following :
   * <p>
   * f0 -> . %0 #0 ( $0 "[" $1 Expression() $2 "]" )+<br>
   * .. .. . .. #1 ( $0 "[" $1 "]" )*<br>
   * .. .. | %1 #0 ( $0 "[" $1 "]" )+ #1 ArrayInitializer()<br>
   * 
   * @param n - the node to visit
   */
  @Override
  public void visit(final ArrayDimsAndInits n) {
    final NodeSequence seq = (NodeSequence) n.f0.choice;
    if (n.f0.which == 0) {
      // %0 #0 ( $0 "[" $1 Expression() $2 "]" )+ #1 ( $0 "[" $1 "]" )*
      final NodeList nl1 = (NodeList) seq.elementAt(0);
      for (final Iterator<INode> e = nl1.elements(); e.hasNext();) {
        final NodeSequence seq1 = (NodeSequence) e.next();
        // $0 "["
        seq1.elementAt(0).accept(this);
        // $1 Expression()
        seq1.elementAt(1).accept(this);
        // $2 "]"
        seq1.elementAt(2).accept(this);
      }
      // #1 ( $0 "[" $1 "]" )*
      final NodeListOptional nlo2 = (NodeListOptional) seq.elementAt(1);
      if (nlo2.present()) {
        for (final Iterator<INode> e = nlo2.elements(); e.hasNext();) {
          final NodeSequence seq2 = (NodeSequence) e.next();
          // $0 "["
          seq2.elementAt(0).accept(this);
          // $1 "]"
          seq2.elementAt(1).accept(this);
        }
      }
    } else {
      // %1 #0 ( $0 "[" $1 "]" )+ #1 ArrayInitializer()
      final NodeList nl3 = (NodeList) seq.elementAt(0);
      //#0 ( $0 "[" $1 "]" )+
      for (final Iterator<INode> e = nl3.elements(); e.hasNext();) {
        final NodeSequence seq1 = (NodeSequence) e.next();
        // $0 "["
        seq1.elementAt(0).accept(this);
        // $1 "]"
        seq1.elementAt(1).accept(this);
      }
      // #1 ArrayInitializer()
      seq.elementAt(1).accept(this);
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
    if (n.f0.which == 4) {
      // %04 #0 StatementExpression() #1 ";"
      ((NodeSequence) n.f0.choice).elementAt(0).accept(this);
      ((NodeSequence) n.f0.choice).elementAt(1).accept(this);
    } else {
      // others
      n.f0.choice.accept(this);
    }
  }

  /**
   * Visits a {@link AssertStatement} node, whose children are the following :
   * <p>
   * f0 -> "assert"<br>
   * f1 -> Expression()<br>
   * f2 -> [ #0 ":" #1 Expression() ]<br>
   * f3 -> ";"<br>
   * 
   * @param n - the node to visit
   */
  @Override
  public void visit(final AssertStatement n) {
    // f0 -> "assert"
    n.f0.accept(this);
    sb.append(" ");
    // f1 -> Expression()
    n.f1.accept(this);
    sb.append(" ");
    // f2 -> [ #0 ":" #1 Expression() ]
    if (n.f2.present()) {
      final NodeSequence seq = (NodeSequence) n.f2.node;
      seq.elementAt(0).accept(this);
      sb.append(" ");
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
      oneNewLine(n);
      sb.append(spc.spc);
      for (final Iterator<INode> e = n.f1.elements(); e.hasNext();) {
        // BlockStatement()
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
    if (n.f0.which == 0) {
      // %0 #0 LocalVariableDeclaration() #1 ";"
      ((NodeSequence) n.f0.choice).elementAt(0).accept(this);
      ((NodeSequence) n.f0.choice).elementAt(1).accept(this);
    } else {
      // others
      n.f0.choice.accept(this);
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
    n.f2.accept(this);
    // f3 -> ( #0 "," #1 VariableDeclarator() )*
    if (n.f3.present()) {
      for (final Iterator<INode> e = n.f3.elements(); e.hasNext();) {
        final NodeSequence seq = (NodeSequence) e.next();
        // #0 ","
        seq.elementAt(0).accept(this);
        sb.append(" ");
        // #1 VariableDeclarator()
        seq.elementAt(1).accept(this);
      }
    }
  }

  /**
   * Visits a {@link VariableModifiers} node, whose children are the following :
   * <p>
   * f0 -> ( ( %0 "final"<br>
   * .. .. . | %1 Annotation() ) )*<br>
   */
  @Override
  public void visit(final VariableModifiers n) {
    if (n.f0.present()) {
      for (final Iterator<INode> e = n.f0.elements(); e.hasNext();) {
        e.next().accept(this);
        // VariableModifiers print the last space if not empty
        sb.append(" ");
      }
    }
  }

  /**
   * Visits a {@link EmptyStatement} node, whose children are the following :
   * <p>
   * f0 -> ";"<br>
   * 
   * @param n - the node to visit
   */
  @Override
  public void visit(final EmptyStatement n) {
    // no difference with superclass
    n.f0.accept(this);
  }

  /**
   * Visits a {@link StatementExpression} node, whose children are the following :
   * <p>
   * f0 -> . %0 PreIncrementExpression()<br>
   * .. .. | %1 PreDecrementExpression()<br>
   * .. .. | %2 #0 PrimaryExpression()<br>
   * .. .. . .. #1 [ &0 "++"<br>
   * .. .. . .. .. | &1 "--"<br>
   * .. .. . .. .. | &2 $0 AssignmentOperator() $1 Expression() ]<br>
   * 
   * @param n - the node to visit
   */
  @Override
  public void visit(final StatementExpression n) {
    if (n.f0.which < 2)
      // %0 PreIncrementExpression() | %1 PreDecrementExpression()
      n.f0.accept(this);
    else {
      //%2 #0 PrimaryExpression() #1 [ &0 "++" | &1 "--" | &2 $0 AssignmentOperator() $1 Expression() ]
      // #0 PrimaryExpression()
      ((NodeSequence) n.f0.choice).elementAt(0).accept(this);
      // #1 [ &0 "++" | &1 "--" | &2 $0 AssignmentOperator() $1 Expression() ]
      final NodeOptional opt = (NodeOptional) ((NodeSequence) n.f0.choice).elementAt(1);
      if (opt.present()) {
        // &0 "++" | &1 "--" | &2 $0 AssignmentOperator() $1 Expression()
        final NodeChoice ch = (NodeChoice) opt.node;
        if (ch.which <= 1)
          // &0 "++" | &1 "--"
          ch.choice.accept(this);
        else {
          // &2 $0 AssignmentOperator() $1 Expression()
          final NodeSequence seq1 = (NodeSequence) ch.choice;
          sb.append(" ");
          // $0 AssignmentOperator()
          seq1.elementAt(0).accept(this);
          sb.append(" ");
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
    n.f2.accept(this);
    // f3 -> ")"
    n.f3.accept(this);
    sb.append(" ");
    // f4 -> "{"
    n.f4.accept(this);
    spc.updateSpc(+1);
    oneNewLine(n);
    sb.append(spc.spc);
    // ( #0 SwitchLabel() #1 ( BlockStatement() )* )*
    for (final Iterator<INode> e = n.f5.elements(); e.hasNext();) {
      final NodeSequence seq = (NodeSequence) e.next();
      // #0 SwitchLabel()
      seq.elementAt(0).accept(this);
      spc.updateSpc(+1);
      // #1 ( BlockStatement() )*
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
   * Visits a {@link SwitchLabel} node, whose children are the following :
   * <p>
   * f0 -> . %0 #0 "case" #1 Expression() #2 ":"<br>
   * .. .. | %1 #0 "default" #1 ":"<br>
   * 
   * @param n - the node to visit
   */
  @Override
  public void visit(final SwitchLabel n) {
    final NodeSequence seq = (NodeSequence) n.f0.choice;
    if (n.f0.which == 0) {
      // %0 #0 "case" #1 Expression() #2 ":"
      seq.elementAt(0).accept(this);
      sb.append(" ");
      seq.elementAt(1).accept(this);
      sb.append(" ");
      seq.elementAt(2).accept(this);
    } else {
      // %1 #0 "default" #1 ":"
      seq.elementAt(0).accept(this);
      sb.append(" ");
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
    n.f2.accept(this);
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
      if (n.f4.f0.which != 2) { // if Statement() not a Block
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
        // Statement()
        st.f0.choice.accept(this);
        spc.updateSpc(-1);
        oneNewLine(n);
        sb.append(spc.spc);
      } else {
        // else Statement() is a Block()
        sb.append(" ");
        // Statement()
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
    n.f2.accept(this);
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
    // f2 -> ( %0 #0 VariableModifiers() #1 Type() #2 < IDENTIFIER > #3 ":" #4 Expression()
    //       | %1 #0 [ ForInit() ] #1 ";" #2 [ Expression() ] #3 ";" #4 [ ForUpdate() ] )
    final NodeSequence seq = (NodeSequence) n.f2.choice;
    if (n.f2.which == 0) {
      //%0 #0 VariableModifiers() #1 Type() #2 < IDENTIFIER > #3 ":" #4 Expression(
      seq.elementAt(0).accept(this);
      // #0 VariableModifiers print the last space if not empty
      // #1 Type()
      seq.elementAt(1).accept(this);
      sb.append(" ");
      // #2 < IDENTIFIER >
      seq.elementAt(2).accept(this);
      sb.append(" ");
      // #3 ":"
      seq.elementAt(3).accept(this);
      sb.append(" ");
      // #4 Expression()
      seq.elementAt(4).accept(this);
    } else {
      // %1 #0 [ ForInit() ] #1 ";" #2 [ Expression() ] #3 ";" #4 [ ForUpdate() ]
      NodeOptional opt;
      // #0 [ ForInit() ]
      opt = (NodeOptional) seq.elementAt(0);
      if (opt.present())
        opt.node.accept(this);
      // #1 ";"
      seq.elementAt(1).accept(this);
      sb.append(" ");
      // #2 [ Expression() ]
      opt = (NodeOptional) seq.elementAt(2);
      if (opt.present())
        opt.node.accept(this);
      // #3 ";"
      seq.elementAt(3).accept(this);
      sb.append(" ");
      // #4 [ ForUpdate() ]
      opt = (NodeOptional) seq.elementAt(4);
      if (opt.present())
        opt.node.accept(this);
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
      // case Statement is not a %02 Block
      spc.updateSpc(+1);
      oneNewLine(n);
      sb.append(spc.spc);
      // Statement()
      n.accept(this);
      spc.updateSpc(-1);
      oneNewLine(n);
      sb.append(spc.spc);
    } else {
      // case Statement is a %02 Block
      sb.append(" ");
      // Statement()
      n.accept(this);
      sb.append(" ");
    }
  }

  /**
   * Visits a {@link ForInit} node, whose children are the following :
   * <p>
   * f0 -> . %0 LocalVariableDeclaration()<br>
   * .. .. | %1 StatementExpressionList()<br>
   * 
   * @param n - the node to visit
   */
  @Override
  public void visit(final ForInit n) {
    // no difference with super class
    n.f0.accept(this);
  }

  /**
   * Visits a {@link StatementExpressionList} node, whose children are the following :
   * <p>
   * f0 -> StatementExpression()<br>
   * f1 -> ( #0 "," #1 StatementExpression() )*<br>
   * 
   * @param n - the node to visit
   */
  @Override
  public void visit(final StatementExpressionList n) {
    // f0 -> StatementExpression()
    n.f0.accept(this);
    // f1 -> ( #0 "," #1 StatementExpression() )*
    if (n.f1.present()) {
      for (final Iterator<INode> e = n.f1.elements(); e.hasNext();) {
        // #0 ","
        ((NodeSequence) e.next()).elementAt(0).accept(this);
        sb.append(" ");
        // #1 StatementExpression()
        ((NodeSequence) e).elementAt(1).accept(this);
      }
    }
  }

  /**
   * Visits a {@link ForUpdate} node, whose children are the following :
   * <p>
   * f0 -> StatementExpressionList()<br>
   * 
   * @param n - the node to visit
   */
  @Override
  public void visit(final ForUpdate n) {
    // no difference with super class
    n.f0.accept(this);
  }

  /**
   * Visits a {@link BreakStatement} node, whose children are the following :
   * <p>
   * f0 -> "break"<br>
   * f1 -> [ < IDENTIFIER > ]<br>
   * f2 -> ";"<br>
   * 
   * @param n - the node to visit
   */
  @Override
  public void visit(final BreakStatement n) {
    // f0 -> "break"
    n.f0.accept(this);
    if (n.f1.present()) {
      sb.append(" ");
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
   * 
   * @param n - the node to visit
   */
  @Override
  public void visit(final ContinueStatement n) {
    // f0 -> "continue"
    n.f0.accept(this);
    if (n.f1.present()) {
      sb.append(" ");
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
   * 
   * @param n - the node to visit
   */
  @Override
  public void visit(final ReturnStatement n) {
    // f0 -> "return"
    n.f0.accept(this);
    if (n.f1.present()) {
      sb.append(" ");
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
   * 
   * @param n - the node to visit
   */
  @Override
  public void visit(final ThrowStatement n) {
    // f0 -> "throw"
    n.f0.accept(this);
    sb.append(" ");
    // f1 -> Expression()
    n.f1.accept(this);
    //  f2 -> ";"
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
      seq.elementAt(2).accept(this);
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
      // #1 Block()
      seq.elementAt(1).accept(this);
    }
  }

  /**
   * Visits a {@link RUnsignedShift} node, whose children are the following :
   * <p>
   * f0 -> ">>>"<br>
   * 
   * @param n - the node to visit
   */
  @Override
  public void visit(final RUnsignedShift n) {

    // no difference with superclass
    n.f0.accept(this);
  }

  /**
   * Visits a {@link RSignedShift} node, whose children are the following :
   * <p>
   * f0 -> ">>"<br>
   * 
   * @param n - the node to visit
   */
  @Override
  public void visit(final RSignedShift n) {

    // no difference with superclass
    n.f0.accept(this);
  }

  /**
   * Visits a {@link Annotation} node, whose children are the following :
   * <p>
   * f0 -> . %0 NormalAnnotation()<br>
   * .. .. | %1 SingleMemberAnnotation()<br>
   * .. .. | %2 MarkerAnnotation()<br>
   * 
   * @param n - the node to visit
   */
  @Override
  public void visit(final Annotation n) {
    // no difference with superclass
    n.f0.accept(this);
  }

  /**
   * Visits a {@link NormalAnnotation} node, whose children are the following :
   * <p>
   * f0 -> "@"<br>
   * f1 -> Name()<br>
   * f2 -> "("<br>
   * f3 -> [ MemberValuePairs() ]<br>
   * f4 -> ")"<br>
   * 
   * @param n - the node to visit
   */
  @Override
  public void visit(final NormalAnnotation n) {
    // f0 -> "@"
    n.f0.accept(this);
    // f1 -> Name()
    n.f1.accept(this);
    // f2 -> "("
    n.f2.accept(this);
    // f3 -> [ MemberValuePairs() ]
    if (n.f3.present())
      n.f3.node.accept(this);
    // f4 -> ")"
    n.f4.accept(this);
  }

  /**
   * Visits a {@link MarkerAnnotation} node, whose children are the following :
   * <p>
   * f0 -> "@"<br>
   * f1 -> Name()<br>
   * 
   * @param n - the node to visit
   */
  @Override
  public void visit(final MarkerAnnotation n) {
    // f0 -> "@"
    n.f0.accept(this);
    // f1 -> Name()
    n.f1.accept(this);
  }

  /**
   * Visits a {@link SingleMemberAnnotation} node, whose children are the following :
   * <p>
   * f0 -> "@"<br>
   * f1 -> Name()<br>
   * f2 -> "("<br>
   * f3 -> MemberValue()<br>
   * f4 -> ")"<br>
   * 
   * @param n - the node to visit
   */
  @Override
  public void visit(final SingleMemberAnnotation n) {
    // f0 -> "@"
    n.f0.accept(this);
    // f1 -> Name()
    n.f1.accept(this);
    // f2 -> "("
    n.f2.accept(this);
    // f3 -> MemberValue()
    n.f3.accept(this);
    // f4 -> ")"
    n.f4.accept(this);
  }

  /**
   * Visits a {@link MemberValuePairs} node, whose children are the following :
   * <p>
   * f0 -> MemberValuePair()<br>
   * f1 -> ( #0 "," #1 MemberValuePair() )*<br>
   * 
   * @param n - the node to visit
   */
  @Override
  public void visit(final MemberValuePairs n) {
    // f0 -> MemberValuePair()
    n.f0.accept(this);
    // f1 -> ( #0 "," #1 MemberValuePair() )*
    if (n.f1.present()) {
      for (final Iterator<INode> e = n.f1.elements(); e.hasNext();) {
        final NodeSequence seq = (NodeSequence) e.next();
        // #0 ","
        seq.elementAt(0).accept(this);
        sb.append(" ");
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
   * 
   * @param n - the node to visit
   */
  @Override
  public void visit(final MemberValuePair n) {
    // f0 -> < IDENTIFIER >
    n.f0.accept(this);
    // f1 -> "="
    sb.append(" ");
    n.f1.accept(this);
    sb.append(" ");
    // f2 -> MemberValue()
    n.f2.accept(this);
  }

  /**
   * Visits a {@link MemberValue} node, whose children are the following :
   * <p>
   * f0 -> . %0 Annotation()<br>
   * .. .. | %1 MemberValueArrayInitializer()<br>
   * .. .. | %2 ConditionalExpression()<br>
   * 
   * @param n - the node to visit
   */
  @Override
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
   * 
   * @param n - the node to visit
   */
  @Override
  public void visit(final MemberValueArrayInitializer n) {
    // f0 -> "{"
    n.f0.accept(this);
    sb.append(" ");
    // f1 -> MemberValue()
    n.f1.accept(this);
    // f2 -> ( #0 "," #1 MemberValue() )*
    if (n.f2.present()) {
      for (final Iterator<INode> e = n.f2.elements(); e.hasNext();) {
        final NodeSequence seq = (NodeSequence) e.next();
        // #0 ","
        seq.elementAt(0).accept(this);
        sb.append(" ");
        // #1 MemberValuePair()
        seq.elementAt(1).accept(this);
      }
    }
    // f3 -> [ "," ]
    if (n.f3.present())
      n.f3.node.accept(this);
    // f4 -> "}"
    sb.append(" ");
    n.f4.accept(this);
  }

  /**
   * Visits a {@link AnnotationTypeDeclaration} node, whose children are the following :
   * <p>
   * f0 -> "@"<br>
   * f1 -> "interface"<br>
   * f2 -> < IDENTIFIER ><br>
   * f3 -> AnnotationTypeBody()<br>
   * 
   * @param n - the node to visit
   */
  @Override
  public void visit(final AnnotationTypeDeclaration n) {
    // f0 -> "@"
    n.f0.accept(this);
    // f1 -> "interface"
    n.f1.accept(this);
    sb.append(" ");
    // f2 -> < IDENTIFIER >
    n.f2.accept(this);
    sb.append(" ");
    // f3 -> AnnotationTypeBody()
    n.f3.accept(this);
  }

  /**
   * Visits a {@link AnnotationTypeBody} node, whose children are the following :
   * <p>
   * f0 -> "{"<br>
   * f1 -> ( AnnotationTypeMemberDeclaration() )*<br>
   * f2 -> "}"<br>
   * 
   * @param n - the node to visit
   */
  @Override
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
   * Visits a {@link AnnotationTypeMemberDeclaration} node, whose children are the following :
   * <p>
   * f0 -> . %0 #0 Modifiers()<br>
   * .. .. . .. #1 ( &0 $0 Type() $1 < IDENTIFIER > $2 "(" $3 ")"<br>
   * .. .. . .. .. $4 [ DefaultValue() ] $5 ";"<br>
   * .. .. . .. .. | &1 ClassOrInterfaceDeclaration()<br>
   * .. .. . .. .. | &2 EnumDeclaration()<br>
   * .. .. . .. .. | &3 AnnotationTypeDeclaration()<br>
   * .. .. . .. .. | &4 FieldDeclaration() )<br>
   * .. .. | %1 ";"<br>
   * 
   * @param n - the node to visit
   */
  @Override
  public void visit(final AnnotationTypeMemberDeclaration n) {
    if (n.f0.which == 0) {
      // %0 #0 Modifiers() #1 ( &0 $0 Type() $1 < IDENTIFIER > $2 "(" $3 ")"$4 [ DefaultValue() ] $5 ";"
      //                      | &1 ClassOrInterfaceDeclaration() | &2 EnumDeclaration()
      //                      | &3 AnnotationTypeDeclaration()| &4 FieldDeclaration() )
      final NodeSequence seq = (NodeSequence) n.f0.choice;
      // #0 Modifiers print the last space if not empty
      seq.elementAt(0).accept(this);
      // #1 ( &0 $0 Type() $1 < IDENTIFIER > $2 "(" $3 ")"$4 [ DefaultValue() ] $5 ";"
      //    | &1 ClassOrInterfaceDeclaration()| &2 EnumDeclaration() | &3 AnnotationTypeDeclaration()
      //    | &4 FieldDeclaration() )
      final NodeChoice ch = (NodeChoice) seq.elementAt(1);
      if (ch.which == 0) {
        // &0 $0 Type() $1 < IDENTIFIER > $2 "(" $3 ")"$4 [ DefaultValue() ] $5 ";"
        final NodeSequence seq1 = (NodeSequence) ch.choice;
        // $0 Type()
        seq1.elementAt(0).accept(this);
        sb.append(" ");
        // $1 < IDENTIFIER >
        seq1.elementAt(1).accept(this);
        // $2 "("
        seq1.elementAt(2).accept(this);
        // $3 ")"
        seq1.elementAt(3).accept(this);
        // $4 [ DefaultValue() ]
        final NodeOptional opt = (NodeOptional) seq1.elementAt(3);
        if (opt.present()) {
          sb.append(" ");
          opt.node.accept(this);
        }
        // $5 ";"
        seq1.elementAt(5).accept(this);
      } else {
        // &1 ClassOrInterfaceDeclaration()| &2 EnumDeclaration()| &3 AnnotationTypeDeclaration()| &4 FieldDeclaration()
        ch.choice.accept(this);
      }
    } else {
      // %1 ";"
      n.f0.choice.accept(this);
    }
  }

  /**
   * Visits a {@link DefaultValue} node, whose children are the following :
   * <p>
   * f0 -> "default"<br>
   * f1 -> MemberValue()<br>
   * 
   * @param n - the node to visit
   */
  @Override
  public void visit(final DefaultValue n) {
    // f0 -> "default"
    n.f0.accept(this);
    // f1 -> MemberValue()
    n.f1.accept(this);
  }

}

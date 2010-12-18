/**
 * Copyright (c) 2004,2005 UCLO Compilers Group.
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
 *  Neither UCLO nor the names of its contributors may be used to endorse
 *  or promote products derived from this software without specific prior
 *  written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OV IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * O PARTICULAV PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNEV OV CONTRIBUTORS BE LIABLE FOV ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OV CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OV SERVICES; LOSS OF USE,
 * DATA, OV PROFITS; OV BUSINESS INTERRUPTION) HOWEVEV CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHEV IN CONTRACT, STRICT LIABILITY, OV TORT
 * (INCLUDING NEGLIGENCE OV OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
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
 * Indiana by Kevin Tao, Wanjun Wang and Jens Palsberg.  No charge may
 * be made for copies, derivations, or distributions of this material
 * without the express written consent of the copyright holder.
 * Neither the name of the University nor the name of the author
 * may be used to endorse or promote products derived from this
 * material without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND WITHOUT ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, WITHOUT LIMITATION, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOV ANY PARTICULAV PURPOSE.
 */

package EDU.purdue.jtb.misc;

import EDU.purdue.jtb.syntaxtree.INode;
import EDU.purdue.jtb.syntaxtree.INodeList;
import EDU.purdue.jtb.syntaxtree.NodeChoice;
import EDU.purdue.jtb.syntaxtree.NodeList;
import EDU.purdue.jtb.syntaxtree.NodeListOptional;
import EDU.purdue.jtb.syntaxtree.NodeOptional;
import EDU.purdue.jtb.syntaxtree.NodeSequence;
import EDU.purdue.jtb.syntaxtree.NodeToken;

/**
 * Class BaseClasses contains static methods to generated string representations of the base classes
 * (nodes and visitors).<br>
 *
 * @author Marc Mazas, mmazas@sopragroup.com
 * @version 1.4.0 : 05-08/2009 : MMa : adapted to JavaCC v4.2 grammar and JDK 1.5
 */
class BaseClasses {

  /** Return and Argument parameter types */
  final static String        parRetArgu  = genClassParamType(true, true);
  /** Beginning of argument list for Return and Argument parameter types */
  final static String        begRetArgu  = begArgList(true);
  /** End of argument list for Return and Argument parameter types */
  final static String        endRetArgu  = endArgList(true);
  /** Return and no Argument parameter type */
  final static String        parRet      = genClassParamType(true, false);
  /** Beginning of argument list for Return and no Argument parameter types */
  final static String        begRet      = begArgList(true);
  /** End of argument list for Return and no Argument parameter types */
  final static String        endRet      = endArgList(false);
  /** No Return and Argument parameter types */
  final static String        parVoidArgu = genClassParamType(false, true);
  /** Beginning of argument list for no Return and Argument parameter types */
  final static String        begVoidArgu = begArgList(false);
  /** End of argument list for no Return and Argument parameter types */
  final static String        endVoidArgu = endArgList(true);
  /** No Return and no Argument parameter types */
  final static String        parVoid     = genClassParamType(false, false);
  /** Beginning of argument list for no Return and no Argument parameter types */
  final static String        begVoid     = begArgList(false);
  /** End of argument list for no Return and no Argument parameter types */
  final static String        endVoid     = endArgList(false);
  /** The OS line separator */
  public static final String LS          = System.getProperty("line.separator");

  /*
   * Node interfaces methods
   */

  /**
   * Generates the {@link INode} interface.
   *
   * @param aSB a buffer to print into (will be allocated if null)
   * @return the generated interface
   */
  static StringBuilder genINodeInterface(final StringBuilder aSB) {
    StringBuilder sb = aSB;
    if (sb == null)
      sb = new StringBuilder(1900);

    packageAndImports(sb);
    sb.append(LS);

    if (Globals.javaDocComments) {
      sb.append("/**").append(LS);
      sb.append(" * The interface which all syntax tree classes must implement.").append(LS);
      sb.append(" */").append(LS);
    }
    sb.append("public interface ").append(Globals.iNodeName)
      .append(" extends java.io.Serializable {").append(LS).append(LS);

    interfacesAcceptMethods(sb);

    if (Globals.parentPointer) {
      if (Globals.javaDocComments) {
        sb.append("  /**").append(LS);
        sb.append("   * Gets the parent node.").append(LS);
        sb.append("   *").append(LS);
        sb.append("   * @return the parent node").append(LS);
        sb.append("   */").append(LS);
      }
      sb.append("  public ").append(Globals.iNodeName).append(" getParent();").append(LS)
        .append(LS);
      if (Globals.javaDocComments) {
        sb.append("  /**").append(LS);
        sb
          .append("   * Sets the parent node. (It is the responsibility of each implementing class")
          .append(LS);
        sb.append("   * to call setParent() on each of its child nodes.)").append(LS);
        sb.append("   *").append(LS);
        sb.append("   * @param n the parent node").append(LS);
        sb.append("   */").append(LS);
      }
      sb.append("  public void setParent(final ").append(Globals.iNodeName).append(" n);")
        .append(LS);
    }

    sb.append("}").append(LS);

    return sb;
  }

  /**
   * Generates the {@link INodeList} interface.
   *
   * @param aSB a buffer to print into (will be allocated if null)
   * @return the generated interface
   */
  static StringBuilder genINodeListInterface(final StringBuilder aSB) {
    StringBuilder sb = aSB;
    if (sb == null)
      sb = new StringBuilder(1850);

    packageAndImports(sb);
    sb.append(LS);

    if (Globals.javaDocComments) {
      sb.append("/**").append(LS);
      sb.append(" * The interface which {@link ").append(Globals.nodeListName).append("}, {@link ")
        .append(Globals.nodeListOptName).append("} and {@link ").append(Globals.nodeSeqName)
        .append("} must implement.").append(LS);
      sb.append(" */").append(LS);
    }
    sb.append("public interface ").append(Globals.iNodeListName).append(" extends ")
      .append(Globals.iNodeName).append(" {").append(LS).append(LS);

    if (Globals.javaDocComments) {
      sb.append("  /**").append(LS);
      sb.append("   * Adds a node to the list.").append(LS);
      sb.append("   *").append(LS);
      sb.append("   * @param n the node to add").append(LS);
      sb.append("   */").append(LS);
    }
    sb.append("  public void addNode(final ").append(Globals.iNodeName).append(" n);").append(LS)
      .append(LS);

    if (Globals.javaDocComments) {
      sb.append("  /**").append(LS);
      sb.append("   * @param i the element index").append(LS);
      sb.append("   * @return the element at the given index").append(LS);
      sb.append("   */").append(LS);
    }
    sb.append("  public ").append(Globals.iNodeName).append(" elementAt(int i);").append(LS)
      .append(LS);

    if (Globals.javaDocComments) {
      sb.append("  /**").append(LS);
      sb.append("   * @return the iterator on the node list").append(LS);
      sb.append("   */").append(LS);
    }
    sb.append("  public java.util.Iterator<").append(Globals.iNodeName).append("> elements();")
      .append(LS).append(LS);

    if (Globals.javaDocComments) {
      sb.append("  /**").append(LS);
      sb.append("   * @return the list size").append(LS);
      sb.append("   */").append(LS);
    }
    sb.append("  public int size();").append(LS).append(LS);

    interfacesAcceptMethods(sb);

    sb.append("}").append(LS);
    return sb;
  }

  /*
   * Node classes methods
   */

  /**
   * Generates the {@link NodeChoice} class.
   *
   * @param aSB a buffer to print into (will be allocated if null)
   * @return the generated class
   */
  static StringBuilder genNodeChoiceClass(final StringBuilder aSB) {
    StringBuilder sb = aSB;
    if (sb == null)
      sb = new StringBuilder(2700);

    packageAndImports(sb);
    sb.append(LS);

    if (Globals.javaDocComments) {
      sb.append("/**").append(LS);
      sb.append(" * Represents a grammar choice (|), e.g. ' ( A | B ) '.<br>").append(LS);
      sb.append(" * The class stores the node and the \"which\" choice indicator (0, 1, ...).")
        .append(LS);
      sb.append(" */").append(LS);
    }
    sb.append("public class ").append(Globals.nodeChoiceName).append(extendsClause())
      .append(" implements ").append(Globals.iNodeName).append(" {").append(LS).append(LS);

    if (Globals.javaDocComments)
      sb.append("  /** The real node */").append(LS);
    sb.append("  public ").append(Globals.iNodeName).append(" choice;").append(LS).append(LS);

    if (Globals.javaDocComments)
      sb.append("  /** The \"which\" choice indicator */").append(LS);
    sb.append("  public int which;").append(LS).append(LS);

    if (Globals.javaDocComments)
      sb.append("  /** The total number of choices */").append(LS);
    sb.append("  public int total;").append(LS).append(LS);

    parentPointerDeclaration(sb);

    serialUIDDeclaration(sb);

    if (Globals.javaDocComments) {
      sb.append("  /**").append(LS);
      sb.append("   * Constructs the {@link NodeChoice} with a given node and non standard (-1) ")
        .append("which choice and total number of choices.").append(LS);
      sb.append("   *").append(LS);
      sb.append("   * @param node the node").append(LS);
      sb.append("   */").append(LS);
    }
    sb.append("  public ").append(Globals.nodeChoiceName).append("(final ")
      .append(Globals.iNodeName).append(" node) {").append(LS);
    sb.append("   this(node, -1, -1);").append(LS);
    sb.append("  }").append(LS).append(LS);

    if (Globals.javaDocComments) {
      sb.append("  /**").append(LS);
      sb.append("   * Constructs the {@link NodeChoice} with a given node, a which choice and ")
        .append("a total (not controlled).").append(LS);
      sb.append("   *").append(LS);
      sb.append("   * @param node the node").append(LS);
      sb.append("   * @param whichChoice the which choice").append(LS);
      sb.append("   * @param totalChoices the total number of choices").append(LS);
      sb.append("   */").append(LS);
    }
    sb.append("  public ").append(Globals.nodeChoiceName).append("(final ")
      .append(Globals.iNodeName).append(" node, final int whichChoice, final int totalChoices) {")
      .append(LS);
    sb.append("    choice = node;").append(LS);
    sb.append("    which = whichChoice;").append(LS);
    sb.append("    total = totalChoices;").append(LS);
    sb.append((Globals.parentPointer ? "    choice.setParent(this);" : ""));
    sb.append((Globals.parentPointer ? LS : ""));
    sb.append("  }").append(LS).append(LS);

    if (Globals.javaDocComments)
      genAcceptRetArguComment(sb);
    sb.append("  public ").append(parRetArgu).append(" ").append(begRetArgu)
      .append(Globals.iRetArguVisitorName).append(parRetArgu).append(endRetArgu).append(" {")
      .append(LS);
    sb.append("    return choice.accept(vis, argu);").append(LS);
    sb.append("  }").append(LS).append(LS);

    if (Globals.javaDocComments)
      genAcceptRetComment(sb);
    sb.append("  public ").append(parRet).append(" ").append(begRet)
      .append(Globals.iRetVisitorName).append(parRet).append(endRet).append(" {").append(LS);
    sb.append("    return choice.accept(vis);").append(LS);
    sb.append("  }").append(LS).append(LS);

    if (Globals.javaDocComments)
      genAcceptVoidArguComment(sb);
    sb.append("  public ").append(parVoidArgu).append(" ").append(begVoidArgu)
      .append(Globals.iVoidArguVisitorName).append(parVoidArgu).append(endVoidArgu).append(" {")
      .append(LS);
    sb.append("    choice.accept(vis, argu);").append(LS);
    sb.append("  }").append(LS).append(LS);

    if (Globals.javaDocComments)
      genAcceptVoidComment(sb);
    sb.append("  public ").append(parVoid).append(begVoid).append(Globals.iVoidVisitorName)
      .append(parVoid).append(endVoid).append(" {").append(LS);
    sb.append("    choice.accept(vis);").append(LS);
    sb.append("  }").append(LS).append(LS);

    parentPointerGetterSetter(sb);

    sb.append("}").append(LS);
    return sb;
  }

  /**
   * Generates the {@link NodeList} class.
   *
   * @param aSB a buffer to print into (will be allocated if null)
   * @return the generated class
   */
  static StringBuilder genNodeListClass(final StringBuilder aSB) {
    StringBuilder sb = aSB;
    if (sb == null)
      sb = new StringBuilder(4050);

    packageAndImports(sb);
    sb.append("import java.util.*;").append(LS).append(LS);

    if (Globals.javaDocComments) {
      sb.append("/**").append(LS);
      sb.append(" * Represents a grammar list (+), e.g. ' ( A )+ '.<br>").append(LS);
      sb.append(" * The class stores the nodes list in an ArrayList.").append(LS);
      sb.append(" */").append(LS);
    }
    sb.append("public class ").append(Globals.nodeListName).append(extendsClause())
      .append(" implements ").append(Globals.iNodeListName).append(" {").append(LS).append(LS);

    if (Globals.javaDocComments)
      sb.append("  /** The list of nodes */").append(LS);
    sb.append("  public ArrayList<").append(Globals.iNodeName).append("> nodes;").append(LS)
      .append(LS);

    if (Globals.javaDocComments) {
      sb.append("  /** The allocation sizes table */").append(LS);
    }
    sb.append("  private static final int allocTb[] = {1, 2, 3, 4, 5, 10, 20, 50};").append(LS)
      .append(LS);

    if (Globals.javaDocComments) {
      sb.append("  /** The allocation number */").append(LS);
    }
    sb.append("  private int allocNb = 0;").append(LS).append(LS);

    parentPointerDeclaration(sb);

    serialUIDDeclaration(sb);

    if (Globals.javaDocComments) {
      sb.append("  /**").append(LS);
      sb.append("   * Initializes an empty list of nodes with a default first allocation.")
        .append(LS);
      sb.append("   */").append(LS);
    }
    sb.append("  public ").append(Globals.nodeListName).append("() {").append(LS);
    sb.append("    nodes = new ArrayList<").append(Globals.iNodeName)
      .append(">(allocTb[allocNb]);").append(LS);
    sb.append("  }").append(LS).append(LS);

    if (Globals.javaDocComments) {
      sb.append("  /**").append(LS);
      sb.append("   * Initializes an empty list of nodes with a given allocation.").append(LS);
      sb.append("   *").append(LS);
      sb.append("   * @param sz the list size").append(LS);
      sb.append("   */").append(LS);
    }
    sb.append("  public ").append(Globals.nodeListName).append("(final int sz) {").append(LS);
    sb.append("    nodes = new ArrayList<").append(Globals.iNodeName).append(">(sz);").append(LS);
    sb.append("  }").append(LS).append(LS);

    if (Globals.javaDocComments) {
      sb.append("  /**").append(LS);
      sb.append("   * Initializes an empty list of nodes with a default first allocation ")
        .append("and adds a first node.").append(LS);
      sb.append("   *").append(LS);
      sb.append("   * @param firstNode the node to add").append(LS);
      sb.append("   */").append(LS);
    }
    sb.append("  public ").append(Globals.nodeListName).append("(final ").append(Globals.iNodeName)
      .append(" firstNode) {").append(LS);
    sb.append("    nodes = new ArrayList<").append(Globals.iNodeName)
      .append(">(allocTb[allocNb]);").append(LS);
    sb.append("    addNode(firstNode);").append(LS);
    sb.append("  }").append(LS).append(LS);

    if (Globals.javaDocComments) {
      sb.append("  /**").append(LS);
      sb.append("   * Initializes an empty list of nodes with a given allocation and ")
        .append("adds a first node.").append(LS);
      sb.append("   *").append(LS);
      sb.append("   * @param sz the list size").append(LS);
      sb.append("   * @param firstNode the node to add").append(LS);
      sb.append("   */").append(LS);
    }
    sb.append("  public ").append(Globals.nodeListName).append("(final int sz, final ")
      .append(Globals.iNodeName).append(" firstNode) {").append(LS);
    sb.append("    nodes = new ArrayList<").append(Globals.iNodeName).append(">(sz);").append(LS);
    sb.append("    addNode(firstNode);").append(LS);
    sb.append("  }").append(LS).append(LS);

    if (Globals.javaDocComments) {
      sb.append("  /**").append(LS);
      sb.append("   * Adds a node to the list of nodes, managing progressive ")
        .append("allocation increments.").append(LS);
      sb.append("   *").append(LS);
      sb.append("   * @param n the node to add").append(LS);
      sb.append("   */").append(LS);
    }
    sb.append("  public void addNode(final ").append(Globals.iNodeName).append(" n) {").append(LS);
    sb.append("    if (++allocNb < allocTb.length)").append(LS);
    sb.append("      nodes.ensureCapacity(allocTb[allocNb]);").append(LS);
    sb.append("    else").append(LS);
    sb.append("      nodes.ensureCapacity((allocNb - allocTb.length + 2) * ")
      .append("allocTb[(allocTb.length - 1)]);").append(LS);
    sb.append("    nodes.add(n);").append(LS);
    parentPointerSetCall(sb);
    sb.append("  }").append(LS).append(LS);

    listMethods(sb);

    classesAcceptMethods(sb);

    parentPointerGetterSetter(sb);

    sb.append("}").append(LS);
    return sb;
  }

  /**
   * Generates the {@link NodeListOptional} class.
   *
   * @param aSB a buffer to print into (will be allocated if null)
   * @return the generated class
   */
  static StringBuilder genNodeListOptClass(final StringBuilder aSB) {
    StringBuilder sb = aSB;
    if (sb == null)
      sb = new StringBuilder(4250);

    packageAndImports(sb);
    sb.append("import java.util.*;").append(LS).append(LS);

    if (Globals.javaDocComments) {
      sb.append("/**").append(LS);
      sb.append(" * Represents an optional grammar list (*), e.g. ' ( A )* '.<br>").append(LS);
      sb.append(" * The class stores the nodes list in an ArrayList.").append(LS);
      sb.append(" */").append(LS);
    }
    sb.append("public class ").append(Globals.nodeListOptName).append(extendsClause())
      .append(" implements ").append(Globals.iNodeListName).append(" {").append(LS).append(LS);

    if (Globals.javaDocComments)
      sb.append("  /** The list of nodes */").append(LS);
    sb.append("  public ArrayList<").append(Globals.iNodeName).append("> nodes;").append(LS)
      .append(LS);

    if (Globals.javaDocComments) {
      sb.append("  /** The allocation sizes table */").append(LS);
    }
    sb.append("  private static final int allocTb[] = {0, 1, 2, 3, 4, 5, 10, 20, 50};").append(LS)
      .append(LS);

    if (Globals.javaDocComments) {
      sb.append("  /** The allocation number */").append(LS);
    }
    sb.append("  private int allocNb = 0;").append(LS).append(LS);

    parentPointerDeclaration(sb);

    serialUIDDeclaration(sb);

    if (Globals.javaDocComments) {
      sb.append("  /**").append(LS);
      sb.append("   * Initializes an empty list of nodes with a default first allocation.")
        .append(LS);
      sb.append("   */").append(LS);
    }
    sb.append("  public ").append(Globals.nodeListOptName).append("() {").append(LS);
    sb.append("    nodes = new ArrayList<").append(Globals.iNodeName)
      .append(">(allocTb[allocNb]);").append(LS);
    sb.append("  }").append(LS).append(LS);

    if (Globals.javaDocComments) {
      sb.append("  /**").append(LS);
      sb.append("   * Initializes an empty list of nodes with a given allocation.").append(LS);
      sb.append("   *").append(LS);
      sb.append("   * @param sz the list size").append(LS);
      sb.append("   */").append(LS);
    }
    sb.append("  public ").append(Globals.nodeListOptName).append("(final int sz) {").append(LS);
    sb.append("    nodes = new ArrayList<").append(Globals.iNodeName).append(">(sz);").append(LS);
    sb.append("  }").append(LS).append(LS);

    if (Globals.javaDocComments) {
      sb.append("  /**").append(LS);
      sb.append("   * Initializes an empty list of nodes with a default first allocation and ")
        .append("adds a first node.").append(LS);
      sb.append("   *").append(LS);
      sb.append("   * @param firstNode the node to add").append(LS);
      sb.append("   */").append(LS);
    }
    sb.append("  public ").append(Globals.nodeListOptName).append("(final ")
      .append(Globals.iNodeName).append(" firstNode) {").append(LS);
    sb.append("    nodes = new ArrayList<").append(Globals.iNodeName)
      .append(">(allocTb[allocNb]);").append(LS);
    sb.append("    addNode(firstNode);").append(LS);
    sb.append("  }").append(LS).append(LS);

    if (Globals.javaDocComments) {
      sb.append("  /**").append(LS);
      sb.append("   * Initializes an empty list of nodes with a given allocation and ")
        .append("adds a first node.").append(LS);
      sb.append("   *").append(LS);
      sb.append("   * @param sz the list size").append(LS);
      sb.append("   * @param firstNode the node to add").append(LS);
      sb.append("   */").append(LS);
    }
    sb.append("  public ").append(Globals.nodeListOptName).append("(final int sz, final ")
      .append(Globals.iNodeName).append(" firstNode) {").append(LS);
    sb.append("    nodes = new ArrayList<").append(Globals.iNodeName).append(">(sz);").append(LS);
    sb.append("    addNode(firstNode);").append(LS);
    sb.append("  }").append(LS).append(LS);

    if (Globals.javaDocComments) {
      sb.append("  /**").append(LS);
      sb.append("   * Adds a node to the list of nodes, managing progressive ")
        .append("allocation increments.").append(LS);
      sb.append("   *").append(LS);
      sb.append("   * @param n the node to add").append(LS);
      sb.append("   */").append(LS);
    }
    sb.append("  public void addNode(final ").append(Globals.iNodeName).append(" n) {").append(LS);
    sb.append("    if (++allocNb < allocTb.length)").append(LS);
    sb.append("      nodes.ensureCapacity(allocTb[allocNb]);").append(LS);
    sb.append("    else").append(LS);
    sb.append("      nodes.ensureCapacity((allocNb - allocTb.length + 2) * ")
      .append("allocTb[(allocTb.length - 1)]);").append(LS);
    sb.append("    nodes.add(n);").append(LS);
    parentPointerSetCall(sb);
    sb.append("  }").append(LS).append(LS);

    listMethods(sb);

    if (Globals.javaDocComments) {
      sb.append("  /**").append(LS);
      sb.append("   * @return true if there is at least one node, false otherwise").append(LS);
      sb.append("   */").append(LS);
    }
    sb.append("  public boolean present() {").append(LS);
    sb.append("    return (nodes.size() != 0); }").append(LS).append(LS);

    classesAcceptMethods(sb);

    parentPointerGetterSetter(sb);

    sb.append("}").append(LS);
    return sb;
  }

  /**
   * Generates the {@link NodeOptional} class.
   *
   * @param aSB a buffer to print into (will be allocated if null)
   * @return the generated class
   */
  static StringBuilder genNodeOptClass(final StringBuilder aSB) {
    StringBuilder sb = aSB;
    if (sb == null)
      sb = new StringBuilder(2700);

    packageAndImports(sb);
    sb.append(LS);

    if (Globals.javaDocComments) {
      sb.append("/**").append(LS);
      sb
        .append(
                " * Represents a grammar optional node (? or []), e.g. ' ( A )? ' or ' [ A ] '.<br>")
        .append(LS);
      sb.append(" * The class stores the node.").append(LS);
      sb.append(" */").append(LS);
    }
    sb.append("public class ").append(Globals.nodeOptName).append(extendsClause())
      .append(" implements ").append(Globals.iNodeName).append(" {").append(LS).append(LS);

    if (Globals.javaDocComments)
      sb.append("  /** The node (if null there is no node) */").append(LS);
    sb.append("  public ").append(Globals.iNodeName).append(" node;").append(LS).append(LS);

    parentPointerDeclaration(sb);

    serialUIDDeclaration(sb);

    if (Globals.javaDocComments) {
      sb.append("  /**").append(LS);
      sb.append("   * Initializes an empty {@link NodeOptional}.").append(LS);
      sb.append("   */").append(LS);
    }
    sb.append("  public ").append(Globals.nodeOptName).append("() {").append(LS);
    sb.append("    node = null;").append(LS);
    sb.append("  }").append(LS).append(LS);

    if (Globals.javaDocComments) {
      sb.append("  /**").append(LS);
      sb.append("   * Initializes a {@link NodeOptional} with a node.").append(LS);
      sb.append("   *").append(LS);
      sb.append("   * @param n the node").append(LS);
      sb.append("   */").append(LS);
    }
    sb.append("  public ").append(Globals.nodeOptName).append("(final ").append(Globals.iNodeName)
      .append(" n) {").append(LS);
    sb.append("    addNode(n);").append(LS);
    sb.append("  }").append(LS).append(LS);

    if (Globals.javaDocComments) {
      sb.append("  /**").append(LS);
      sb.append("   * Adds a node to the {@link NodeOptional}.").append(LS);
      sb.append("   *").append(LS);
      sb.append("   * @param n the node to add").append(LS);
      sb.append("   */").append(LS);
    }
    sb.append("  public void addNode(final ").append(Globals.iNodeName).append(" n) {").append(LS);
    sb.append("    if (node != null)").append(LS)
      .append("      throw new Error(\"Attempt to set optional node twice\");").append(LS);
    sb.append("    node = n;").append(LS);
    parentPointerSetCall(sb);
    sb.append("  }").append(LS).append(LS);

    if (Globals.javaDocComments) {
      sb.append("  /**").append(LS);
      sb.append("   * @return true if the node exists, false otherwise").append(LS);
      sb.append("   */").append(LS);
    }
    sb.append("  public boolean present() {").append(LS);
    sb.append("    return (node != null); }").append(LS).append(LS);

    classesAcceptMethods(sb);

    parentPointerGetterSetter(sb);

    sb.append("}").append(LS);
    return sb;
  }

  /**
   * Generates the {@link NodeSequence} class.
   *
   * @param aSB a buffer to print into (will be allocated if null)
   * @return the generated class
   */
  static StringBuilder genNodeSeqClass(final StringBuilder aSB) {
    StringBuilder sb = aSB;
    if (sb == null)
      sb = new StringBuilder(3800);

    packageAndImports(sb);
    sb.append(LS);

    sb.append("import java.util.*;").append(LS).append(LS);
    if (Globals.javaDocComments) {
      sb.append("/**").append(LS);
      sb.append(" * Represents a sequence of nodes (x y z ...) nested ")
        .append("within a choice (|), list (+),").append(LS)
        .append(" * optional list (*), or optional node (? or []), e.g. ' ( A B )+ ' ")
        .append("or ' [ C D E ] '.<br>").append(LS);
      sb.append(" * The class stores the nodes list in an ArrayList.").append(LS);
      sb.append(" */").append(LS);
    }
    sb.append("public class ").append(Globals.nodeSeqName).append(extendsClause())
      .append(" implements ").append(Globals.iNodeListName).append(" {").append(LS).append(LS);

    if (Globals.javaDocComments)
      sb.append("  /** The list of nodes */").append(LS);
    sb.append("  public ArrayList<").append(Globals.iNodeName).append("> nodes;").append(LS)
      .append(LS);

    parentPointerDeclaration(sb);

    serialUIDDeclaration(sb);

    if (Globals.javaDocComments) {
      sb.append("  /**").append(LS);
      sb.append("   * Initializes an empty {@link NodeSequence} with a default allocation.")
        .append(LS);
      sb.append("   */").append(LS);
    }
    sb.append("  public ").append(Globals.nodeSeqName).append("() {").append(LS);
    sb.append("    nodes = new ArrayList<").append(Globals.iNodeName).append(">();").append(LS);
    sb.append("  }").append(LS).append(LS);

    if (Globals.javaDocComments) {
      sb.append("  /**").append(LS);
      sb.append("   * Initializes an empty {@link NodeSequence} with a given allocation.")
        .append(LS);
      sb.append("   *").append(LS);
      sb.append("   * @param sz the list size").append(LS);
      sb.append("   */").append(LS);
    }
    sb.append("  public ").append(Globals.nodeSeqName).append("(final int sz) {").append(LS);
    sb.append("    nodes = new ArrayList<").append(Globals.iNodeName).append(">(sz);").append(LS);
    sb.append("  }").append(LS).append(LS);

    if (Globals.javaDocComments) {
      sb.append("  /**").append(LS);
      sb.append("   * Initializes an empty {@link NodeSequence} with a default allocation ")
        .append("and adds a first node.").append(LS);
      sb.append("   *").append(LS);
      sb.append("   * @param firstNode the node to add").append(LS);
      sb.append("   */").append(LS);
    }
    sb.append("  public ").append(Globals.nodeSeqName).append("(final ").append(Globals.iNodeName)
      .append(" firstNode) {").append(LS);
    sb.append("    nodes = new ArrayList<").append(Globals.iNodeName).append(">();").append(LS);
    sb.append("    addNode(firstNode);").append(LS);
    sb.append("  }").append(LS).append(LS);

    if (Globals.javaDocComments) {
      sb.append("  /**").append(LS);
      sb.append("   * Initializes an empty {@link NodeSequence} with a given allocation ")
        .append("and adds a first node.").append(LS);
      sb.append("   *").append(LS);
      sb.append("   * @param sz the list size").append(LS);
      sb.append("   * @param firstNode the node to add").append(LS);
      sb.append("   */").append(LS);
    }
    sb.append("  public ").append(Globals.nodeSeqName).append("(final int sz, final ")
      .append(Globals.iNodeName).append(" firstNode) {").append(LS);
    sb.append("    nodes = new ArrayList<").append(Globals.iNodeName).append(">(sz);").append(LS);
    sb.append("    addNode(firstNode);").append(LS);
    sb.append("  }").append(LS).append(LS);

    if (Globals.javaDocComments) {
      sb.append("  /**").append(LS);
      sb.append("   * Adds a node to the {@link NodeSequence}.").append(LS);
      sb.append("   *").append(LS);
      sb.append("   * @param n the node to add").append(LS);
      sb.append("   */").append(LS);
    }
    sb.append("  public void addNode(final ").append(Globals.iNodeName).append(" n) {").append(LS);
    sb.append("    nodes.add(n);").append(LS);
    parentPointerSetCall(sb);
    sb.append("  }").append(LS).append(LS);

    listMethods(sb);

    classesAcceptMethods(sb);

    parentPointerGetterSetter(sb);

    sb.append("}").append(LS);
    return sb;
  }

  /**
   * Generates the {@link NodeToken} class.
   *
   * @param aSB a buffer to print into (will be allocated if null)
   * @return the generated class
   */
  static StringBuilder genNodeTokenClass(final StringBuilder aSB) {
    StringBuilder sb = aSB;
    if (sb == null)
      sb = new StringBuilder(5970);

    packageAndImports(sb);
    sb.append(LS);

    sb.append("import java.util.*;").append(LS);
    if (Globals.javaDocComments) {
      sb.append("/**").append(LS);
      sb.append(" * Represents a single token in the grammar.<br>").append(LS);
      sb.append(" * If the \"-tk\" option is used, also contains a ArrayList of preceding ")
        .append("special tokens.<br>").append(LS);
      sb.append(" * The class stores the token image, kind and position information, ")
        .append("and the special tokens list.<br>").append(LS);
      sb.append(" */").append(LS);
    }
    sb.append("public class ").append(Globals.nodeTokenName).append(
                                                                    extendsClause() +
                                                                        " implements ")
      .append(Globals.iNodeName).append(" {").append(LS).append(LS);

    if (Globals.javaDocComments)
      sb.append("  /** The token image */").append(LS);
    sb.append("  public String tokenImage;").append(LS).append(LS);

    if (Globals.javaDocComments)
      sb.append("  /** The list of special tokens */").append(LS);
    sb.append("  public ArrayList<").append(Globals.nodeTokenName).append("> specialTokens;")
      .append(LS).append(LS);

    if (Globals.javaDocComments)
      sb.append("  /** The token first line (-1 means not available) */").append(LS);
    sb.append("  public int beginLine;").append(LS).append(LS);
    if (Globals.javaDocComments)
      sb.append("  /** The token first column (-1 means not available) */").append(LS);
    sb.append("  public int beginColumn;").append(LS).append(LS);
    if (Globals.javaDocComments)
      sb.append("  /** The token last line (-1 means not available) */").append(LS);
    sb.append("  public int endLine;").append(LS).append(LS);
    if (Globals.javaDocComments)
      sb.append("  /** The token last column (-1 means not available) */").append(LS);
    sb.append("  public int endColumn;").append(LS).append(LS);

    if (Globals.javaDocComments)
      sb.append("  /** The JavaCC token \"kind\" integer (-1 means not available) */").append(LS);
    sb.append("  public int kind;").append(LS).append(LS);

    parentPointerDeclaration(sb);

    serialUIDDeclaration(sb);

    lineSeparatorDeclaration(sb);

    if (Globals.javaDocComments) {
      sb.append("  /**").append(LS);
      sb.append("   * Initializes a {@link NodeToken} with a given string and ")
        .append("no position information.").append(LS);
      sb.append("   *").append(LS);
      sb.append("   * @param s the token string").append(LS);
      sb.append("   */").append(LS);
    }
    sb.append("  public ").append(Globals.nodeTokenName).append("(String s) {").append(LS);
    sb.append("    this(s, -1, -1, -1, -1, -1);").append(LS);
    sb.append("  }").append(LS).append(LS);

    if (Globals.javaDocComments) {
      sb.append("  /**").append(LS);
      sb.append("   * Initializes a {@link NodeToken} with a given string and ")
        .append("position information.").append(LS);
      sb.append("   *").append(LS);
      sb.append("   * @param s the token string").append(LS);
      sb.append("   * @param kn the token kind").append(LS);
      sb.append("   * @param bl the first line").append(LS);
      sb.append("   * @param bc the first column").append(LS);
      sb.append("   * @param el the last line").append(LS);
      sb.append("   * @param ec the last column").append(LS);
      sb.append("   */").append(LS);
    }
    sb.append("  public ").append(Globals.nodeTokenName)
      .append("(String s, final int kn, final int bl, final int bc, ")
      .append("final int el, final int ec) {").append(LS);
    sb.append("    tokenImage = s;").append(LS);
    sb.append("    specialTokens = null;").append(LS);
    sb.append("    kind = kn;").append(LS);
    sb.append("    beginLine = bl;").append(LS);
    sb.append("    beginColumn = bc;").append(LS);
    sb.append("    endLine = el;").append(LS);
    sb.append("    endColumn = ec;").append(LS);
    sb.append("  }").append(LS).append(LS);

    if (Globals.javaDocComments) {
      sb.append("  /**").append(LS);
      sb.append("   * Gets the special token in the special tokens list at a given position.")
        .append(LS);
      sb.append("   *").append(LS);
      sb.append("   * @param i the special token's position").append(LS);
      sb.append("   * @return the special token").append(LS);
      sb.append("   */").append(LS);
    }
    sb.append("  public ").append(Globals.nodeTokenName).append(" getSpecialAt(final int i) {")
      .append(LS);
    sb.append("    if (specialTokens == null)").append(LS);
    sb.append("      throw new NoSuchElementException(\"No specialTokens in token\");").append(LS);
    sb.append("    return specialTokens.get(i);").append(LS);
    sb.append("  }").append(LS).append(LS);

    if (Globals.javaDocComments) {
      sb.append("  /**").append(LS);
      sb.append("   * @return the number of special tokens").append(LS);
      sb.append("   */").append(LS);
    }
    sb.append("  public int numSpecials() {").append(LS).append("    if (specialTokens == null)")
      .append(LS);
    sb.append("      return 0;").append(LS).append("    return specialTokens.size();").append(LS);
    sb.append("  }").append(LS).append(LS);

    if (Globals.javaDocComments) {
      sb.append("  /**").append(LS);
      sb.append("   * Adds a special token to the special tokens list.").append(LS);
      sb.append("   *").append(LS);
      sb.append("   * @param s the special token to add").append(LS);
      sb.append("   */").append(LS);
    }
    sb.append("  public void addSpecial(final ").append(Globals.nodeTokenName).append(" s) {")
      .append(LS);
    sb.append("    if (specialTokens == null)").append(LS);
    sb.append("      specialTokens = new ArrayList<").append(Globals.nodeTokenName).append(">();")
      .append(LS);
    sb.append("    specialTokens.add(s);").append(LS);
    sb.append((Globals.parentPointer ? "    s.setParent(this);" : ""));
    sb.append((Globals.parentPointer ? LS : ""));
    sb.append("  }").append(LS).append(LS);

    if (Globals.javaDocComments) {
      sb.append("  /**").append(LS);
      sb.append("   * Trims the special tokens list.").append(LS);
      sb.append("   */").append(LS);
    }
    sb.append("  public void trimSpecials() {").append(LS);
    sb.append("    if (specialTokens == null)").append(LS);
    sb.append("      return;").append(LS);
    sb.append("    specialTokens.trimToSize();").append(LS);
    sb.append("  }").append(LS).append(LS);

    if (Globals.javaDocComments) {
      sb.append("  /**").append(LS);
      sb.append("   * @return the token image").append(LS);
      sb.append("   */").append(LS);
    }
    sb.append("  @Override").append(LS);
    sb.append("  public String toString() {").append(LS);
    sb.append("    return tokenImage;").append(LS);
    sb.append("  }").append(LS).append(LS);

    if (Globals.javaDocComments) {
      sb.append("  /**").append(LS);
      sb.append("   * Returns the list of special tokens of the current {@link NodeToken} ")
        .append("as a string,<br>").append(LS);
      sb.append("   * taking in account a given indentation.").append(LS);
      sb.append("   *").append(LS);
      sb.append("   * @param spc the indentation").append(LS);
      sb.append("   * @return the string representing the special tokens list").append(LS);
      sb.append("   */").append(LS);
    }
    sb.append("  public String getSpecials(final String spc) {").append(LS);
    sb.append("    if (specialTokens == null)").append(LS);
    sb.append("      return \"\";").append(LS);
    sb.append("    StringBuilder buf = new StringBuilder(64);").append(LS);
    sb.append("    for (final Iterator<").append(Globals.nodeTokenName)
      .append("> e = specialTokens.iterator(); e.hasNext();) {").append(LS);
    sb.append("      final String s = e.next().tokenImage;").append(LS);
    sb.append("      final int p = s.length() - 1;").append(LS);
    sb.append("      final char c = s.charAt(p);").append(LS);
    sb.append("      buf.append(s);").append(LS);
    sb.append("      // TODO modifier specials pour inclure fins de ligne").append(LS);
    sb.append("      if (c == '\\n' || c == '\\r')").append(LS);
    sb.append("        buf.append(spc);").append(LS);
    sb.append("      else").append(LS);
    sb.append("        buf.append(LS).append(spc);").append(LS);
    sb.append("    }").append(LS);
    sb.append("    return buf.toString();").append(LS);
    sb.append("  }").append(LS).append(LS);

    if (Globals.javaDocComments) {
      sb.append("  /**").append(LS);
      sb.append("   * Returns the list of special tokens of the current {@link NodeToken} ")
        .append("and the current<br>").append(LS);
      sb.append("   * {@link NodeToken} as a string, taking in account a given indentation.")
        .append(LS);
      sb.append("   *").append(LS);
      sb.append("   * @param spc the indentation").append(LS);
      sb.append("   * @return the string representing the special tokens list and the token")
        .append(LS);
      sb.append("   */").append(LS);
    }
    sb.append("  public String withSpecials(final String spc) {").append(LS);
    sb.append("    final String specials = getSpecials(spc);").append(LS);
    sb.append("    final int len = specials.length();").append(LS);
    sb.append("    if (len == 0)").append(LS);
    sb.append("      return tokenImage;").append(LS);
    sb.append("    StringBuilder buf = new StringBuilder(len + tokenImage.length());").append(LS);
    sb.append("    buf.append(specials).append(tokenImage);").append(LS);
    sb.append("    return buf.toString();").append(LS);
    sb.append("  }").append(LS).append(LS);

    classesAcceptMethods(sb);

    parentPointerGetterSetter(sb);

    sb.append("}").append(LS);
    return sb;
  }

  /*
   * "RetArgu" visit methods
   */

  /**
   * Generates the visit method declaration on a {@link NodeList} for a visitor with user Return and
   * Argument data.
   *
   * @param aSB a buffer to print into (will be allocated if null)
   * @return the generated visit method
   */
  static StringBuilder genRetArguVisitNodeList(final StringBuilder aSB) {
    StringBuilder sb = aSB;
    if (sb == null)
      sb = new StringBuilder(280);
    if (Globals.javaDocComments) {
      sb.append("  /**").append(LS);
      sb.append("   * Visits a {@link NodeList} node, passing it an argument.").append(LS);
      sb.append("   *").append(LS);
      sb.append("   * @param n the node to visit").append(LS);
      sb.append("   * @param argu the user argument").append(LS);
      sb.append("   * @return the user return information").append(LS);
      sb.append("   */").append(LS);
    }
    sb.append("  public ").append(Globals.genRetType).append(" visit(final ")
      .append(Globals.nodeListName).append(" n, final ").append(Globals.genArguType)
      .append(" argu);").append(LS);
    return sb;
  }

  /**
   * Generates the visit method declaration on a {@link NodeListOptional} for a visitor with user
   * Return and Argument data.
   *
   * @param aSB a buffer to print into (will be allocated if null)
   * @return the generated visit method
   */
  static StringBuilder genRetArguVisitNodeListOpt(final StringBuilder aSB) {
    StringBuilder sb = aSB;
    if (sb == null)
      sb = new StringBuilder(280);
    if (Globals.javaDocComments) {
      sb.append("  /**").append(LS);
      sb.append("   * Visits a {@link NodeListOptional} node, passing it an argument.").append(LS);
      sb.append("   *").append(LS);
      sb.append("   * @param n the node to visit").append(LS);
      sb.append("   * @param argu the user argument").append(LS);
      sb.append("   * @return the user return information").append(LS);
      sb.append("   */").append(LS);
    }
    sb.append("  public ").append(Globals.genRetType).append(" visit(final ")
      .append(Globals.nodeListOptName).append(" n, final ").append(Globals.genArguType)
      .append(" argu);").append(LS);
    return sb;
  }

  /**
   * Generates the visit method declaration on a {@link NodeOptional} for a visitor with user Return
   * and Argument data.
   *
   * @param aSB a buffer to print into (will be allocated if null)
   * @return the generated visit method
   */
  static StringBuilder genRetArguVisitNodeOpt(final StringBuilder aSB) {
    StringBuilder sb = aSB;
    if (sb == null)
      sb = new StringBuilder(280);
    if (Globals.javaDocComments) {
      sb.append("  /**").append(LS);
      sb.append("   * Visits a {@link NodeOptional} node, passing it an argument.").append(LS);
      sb.append("   *").append(LS);
      sb.append("   * @param n the node to visit").append(LS);
      sb.append("   * @param argu the user argument").append(LS);
      sb.append("   * @return the user return information").append(LS);
      sb.append("   */").append(LS);
    }
    sb.append("  public ").append(Globals.genRetType).append(" visit(final ")
      .append(Globals.nodeOptName).append(" n, final ").append(Globals.genArguType)
      .append(" argu);").append(LS);
    return sb;
  }

  /**
   * Generates the visit method declaration on a {@link NodeSequence} for a visitor with user Return
   * and Argument data.
   *
   * @param aSB a buffer to print into (will be allocated if null)
   * @return the generated visit method
   */
  static StringBuilder genRetArguVisitNodeSeq(final StringBuilder aSB) {
    StringBuilder sb = aSB;
    if (sb == null)
      sb = new StringBuilder(280);
    if (Globals.javaDocComments) {
      sb.append("  /**").append(LS);
      sb.append("   * Visits a {@link NodeSequence} node, passing it an argument.").append(LS);
      sb.append("   *").append(LS);
      sb.append("   * @param n the node to visit").append(LS);
      sb.append("   * @param argu the user argument").append(LS);
      sb.append("   * @return the user return information").append(LS);
      sb.append("   */").append(LS);
    }
    sb.append("  public ").append(Globals.genRetType).append(" visit(final ")
      .append(Globals.nodeSeqName).append(" n, final ").append(Globals.genArguType)
      .append(" argu);").append(LS);
    return sb;
  }

  /**
   * Generates the visit method declaration on a {@link NodeToken} for a visitor with user Return
   * and Argument data.
   *
   * @param aSB a buffer to print into (will be allocated if null)
   * @return the generated visit method
   */
  static StringBuilder genRetArguVisitNodeToken(final StringBuilder aSB) {
    StringBuilder sb = aSB;
    if (sb == null)
      sb = new StringBuilder(280);
    if (Globals.javaDocComments) {
      sb.append("  /**").append(LS);
      sb.append("   * Visits a {@link NodeToken} node, passing it an argument.").append(LS);
      sb.append("   *").append(LS);
      sb.append("   * @param n the node to visit").append(LS);
      sb.append("   * @param argu the user argument").append(LS);
      sb.append("   * @return the user return information").append(LS);
      sb.append("   */").append(LS);
    }
    sb.append("  public ").append(Globals.genRetType).append(" visit(final ")
      .append(Globals.nodeTokenName).append(" n, final ").append(Globals.genArguType)
      .append(" argu);").append(LS);
    return sb;
  }

  /*
   * "Ret" visit methods
   */

  /**
   * Generates the visit method declaration on a {@link NodeList} for a visitor with user Return and
   * no Argument data.
   *
   * @param aSB a buffer to print into (will be allocated if null)
   * @return the generated visit method
   */
  static StringBuilder genRetVisitNodeList(final StringBuilder aSB) {
    StringBuilder sb = aSB;
    if (sb == null)
      sb = new StringBuilder(250);
    if (Globals.javaDocComments) {
      sb.append("  /**").append(LS);
      sb.append("   * Visits a {@link NodeList} node.").append(LS);
      sb.append("   *").append(LS);
      sb.append("   * @param n the node to visit").append(LS);
      sb.append("   * @return the user return information").append(LS);
      sb.append("   */").append(LS);
    }
    sb.append("  public ").append(Globals.genRetType).append(" visit(final ")
      .append(Globals.nodeListName).append(" n);").append(LS);
    return sb;
  }

  /**
   * Generates the visit method declaration on a {@link NodeListOptional} for a visitor with user
   * Return and no Argument data.
   *
   * @param aSB a buffer to print into (will be allocated if null)
   * @return the generated visit method
   */
  static StringBuilder genRetVisitNodeListOpt(final StringBuilder aSB) {
    StringBuilder sb = aSB;
    if (sb == null)
      sb = new StringBuilder(250);
    if (Globals.javaDocComments) {
      sb.append("  /**").append(LS);
      sb.append("   * Visits a {@link NodeListOptional} node.").append(LS);
      sb.append("   *").append(LS);
      sb.append("   * @param n the node to visit").append(LS);
      sb.append("   * @return the user return information").append(LS);
      sb.append("   */").append(LS);
    }
    sb.append("  public ").append(Globals.genRetType).append(" visit(final ")
      .append(Globals.nodeListOptName).append(" n);").append(LS);
    return sb;
  }

  /**
   * Generates the visit method declaration on a {@link NodeOptional} for a visitor with user Return
   * and no Argument data.
   *
   * @param aSB a buffer to print into (will be allocated if null)
   * @return the generated visit method
   */
  static StringBuilder genRetVisitNodeOpt(final StringBuilder aSB) {
    StringBuilder sb = aSB;
    if (sb == null)
      sb = new StringBuilder(250);
    if (Globals.javaDocComments) {
      sb.append("  /**").append(LS);
      sb.append("   * Visits a {@link NodeOptional} node.").append(LS);
      sb.append("   *").append(LS);
      sb.append("   * @param n the node to visit").append(LS);
      sb.append("   * @return the user return information").append(LS);
      sb.append("   */").append(LS);
    }
    sb.append("  public ").append(Globals.genRetType).append(" visit(final ")
      .append(Globals.nodeOptName).append(" n);").append(LS);
    return sb;
  }

  /**
   * Generates the visit method declaration on a {@link NodeSequence} for a visitor with user Return
   * and no Argument data.
   *
   * @param aSB a buffer to print into (will be allocated if null)
   * @return the generated visit method
   */
  static StringBuilder genRetVisitNodeSeq(final StringBuilder aSB) {
    StringBuilder sb = aSB;
    if (sb == null)
      sb = new StringBuilder(250);
    if (Globals.javaDocComments) {
      sb.append("  /**").append(LS);
      sb.append("   * Visits a {@link NodeSequence} node.").append(LS);
      sb.append("   *").append(LS);
      sb.append("   * @param n the node to visit").append(LS);
      sb.append("   * @return the user return information").append(LS);
      sb.append("   */").append(LS);
    }
    sb.append("  public ").append(Globals.genRetType).append(" visit(final ")
      .append(Globals.nodeSeqName).append(" n);").append(LS);
    return sb;
  }

  /**
   * Generates the visit method declaration on a {@link NodeToken} for a visitor with user Return
   * and no Argument data.
   *
   * @param aSB a buffer to print into (will be allocated if null)
   * @return the generated visit method
   */
  static StringBuilder genRetVisitNodeToken(final StringBuilder aSB) {
    StringBuilder sb = aSB;
    if (sb == null)
      sb = new StringBuilder(250);
    if (Globals.javaDocComments) {
      sb.append("  /**").append(LS);
      sb.append("   * Visits a {@link NodeToken} node.").append(LS);
      sb.append("   *").append(LS);
      sb.append("   * @param n the node to visit").append(LS);
      sb.append("   * @return the user return information").append(LS);
      sb.append("   */").append(LS);
    }
    sb.append("  public ").append(Globals.genRetType).append(" visit(final ")
      .append(Globals.nodeTokenName).append(" n);").append(LS);
    return sb;
  }

  /*
   * "VoidArgu" visit methods
   */

  /**
   * Generates the visit method declaration on a {@link NodeList} for a visitor with user no Return
   * and Argument data.
   *
   * @param aSB a buffer to print into (will be allocated if null)
   * @return the generated visit method
   */
  static StringBuilder genVoidArguVisitNodeList(final StringBuilder aSB) {
    StringBuilder sb = aSB;
    if (sb == null)
      sb = new StringBuilder(260);
    if (Globals.javaDocComments) {
      sb.append("  /**").append(LS);
      sb.append("   * Visits a {@link NodeList} node, passing it an argument.").append(LS);
      sb.append("   *").append(LS);
      sb.append("   * @param n the node to visit").append(LS);
      sb.append("   * @param argu the user argument").append(LS);
      sb.append("   */").append(LS);
    }
    sb.append("  public void visit(final ").append(Globals.nodeListName).append(" n, final ")
      .append(Globals.genArguType).append(" argu);").append(LS);
    return sb;
  }

  /**
   * Generates the visit method declaration on a {@link NodeListOptional} for a visitor with user no
   * Return and Argument data.
   *
   * @param aSB a buffer to print into (will be allocated if null)
   * @return the generated visit method
   */
  static StringBuilder genVoidArguVisitNodeListOpt(final StringBuilder aSB) {
    StringBuilder sb = aSB;
    if (sb == null)
      sb = new StringBuilder(260);
    if (Globals.javaDocComments) {
      sb.append("  /**").append(LS);
      sb.append("   * Visits a {@link NodeListOptional} node, passing it an argument.").append(LS);
      sb.append("   *").append(LS);
      sb.append("   * @param n the node to visit").append(LS);
      sb.append("   * @param argu the user argument").append(LS);
      sb.append("   */").append(LS);
    }
    sb.append("  public void visit(final ").append(Globals.nodeListOptName).append(" n, final ")
      .append(Globals.genArguType).append(" argu);").append(LS);
    return sb;
  }

  /**
   * Generates the visit method declaration on a {@link NodeOptional} for a visitor with user no
   * Return and Argument data.
   *
   * @param aSB a buffer to print into (will be allocated if null)
   * @return the generated visit method
   */
  static StringBuilder genVoidArguVisitNodeOpt(final StringBuilder aSB) {
    StringBuilder sb = aSB;
    if (sb == null)
      sb = new StringBuilder(260);
    if (Globals.javaDocComments) {
      sb.append("  /**").append(LS);
      sb.append("   * Visits a {@link NodeOptional} node, passing it an argument.").append(LS);
      sb.append("   *").append(LS);
      sb.append("   * @param n the node to visit").append(LS);
      sb.append("   * @param argu the user argument").append(LS);
      sb.append("   */").append(LS);
    }
    sb.append("  public void visit(final ").append(Globals.nodeOptName).append(" n, final ")
      .append(Globals.genArguType).append(" argu);").append(LS);
    return sb;
  }

  /**
   * Generates the visit method declaration on a {@link NodeSequence} for a visitor with user no
   * Return and Argument data.
   *
   * @param aSB a buffer to print into (will be allocated if null)
   * @return the generated visit method
   */
  static StringBuilder genVoidArguVisitNodeSeq(final StringBuilder aSB) {
    StringBuilder sb = aSB;
    if (sb == null)
      sb = new StringBuilder(100);
    if (Globals.javaDocComments) {
      sb.append("  /**").append(LS);
      sb.append("   * Visits a {@link NodeSequence} node, passing it an argument.").append(LS);
      sb.append("   *").append(LS);
      sb.append("   * @param n the node to visit").append(LS);
      sb.append("   * @param argu the user argument").append(LS);
      sb.append("   */").append(LS);
    }
    sb.append("  public void visit(final ").append(Globals.nodeSeqName).append(" n, final ")
      .append(Globals.genArguType).append(" argu);").append(LS);
    return sb;
  }

  /**
   * Generates the visit method declaration on a {@link NodeToken} for a visitor with user no Return
   * and Argument data.
   *
   * @param aSB a buffer to print into (will be allocated if null)
   * @return the generated visit method
   */
  static StringBuilder genVoidArguVisitNodeToken(final StringBuilder aSB) {
    StringBuilder sb = aSB;
    if (sb == null)
      sb = new StringBuilder(260);
    if (Globals.javaDocComments) {
      sb.append("  /**").append(LS);
      sb.append("   * Visits a {@link NodeToken} node, passing it an argument.").append(LS);
      sb.append("   *").append(LS);
      sb.append("   * @param n the node to visit").append(LS);
      sb.append("   * @param argu the user argument").append(LS);
      sb.append("   */").append(LS);
    }
    sb.append("  public void visit(final ").append(Globals.nodeTokenName).append(" n, final ")
      .append(Globals.genArguType).append(" argu);").append(LS);
    return sb;
  }

  /*
   * "Void" visit methods
   */

  /**
   * Generates the visit method declaration on a {@link NodeList} for a visitor with user no Return
   * and no Argument data.
   *
   * @param aSB a buffer to print into (will be allocated if null)
   * @return the generated visit method
   */
  static StringBuilder genVoidVisitNodeList(final StringBuilder aSB) {
    StringBuilder sb = aSB;
    if (sb == null)
      sb = new StringBuilder(230);
    if (Globals.javaDocComments) {
      sb.append("  /**").append(LS);
      sb.append("   * Visits a {@link NodeList} node.").append(LS);
      sb.append("   *").append(LS);
      sb.append("   * @param n the node to visit").append(LS);
      sb.append("   */").append(LS);
    }
    sb.append("  public void visit(final ").append(Globals.nodeListName).append(" n);").append(LS);
    return sb;
  }

  /**
   * Generates the visit method declaration on a {@link NodeListOptional} for a visitor with user no
   * Return and no Argument data.
   *
   * @param aSB a buffer to print into (will be allocated if null)
   * @return the generated visit method
   */
  static StringBuilder genVoidVisitNodeListOpt(final StringBuilder aSB) {
    StringBuilder sb = aSB;
    if (sb == null)
      sb = new StringBuilder(230);
    if (Globals.javaDocComments) {
      sb.append("  /**").append(LS);
      sb.append("   * Visits a {@link NodeListOptional} node.").append(LS);
      sb.append("   *").append(LS);
      sb.append("   * @param n the node to visit").append(LS);
      sb.append("   */").append(LS);
    }
    sb.append("  public void visit(final ").append(Globals.nodeListOptName).append(" n);")
      .append(LS);
    return sb;
  }

  /**
   * Generates the visit method declaration on a {@link NodeOptional} for a visitor with user no
   * Return and no Argument data.
   *
   * @param aSB a buffer to print into (will be allocated if null)
   * @return the generated visit method
   */
  static StringBuilder genVoidVisitNodeOpt(final StringBuilder aSB) {
    StringBuilder sb = aSB;
    if (sb == null)
      sb = new StringBuilder(230);
    if (Globals.javaDocComments) {
      sb.append("  /**").append(LS);
      sb.append("   * Visits a {@link NodeOptional} node.").append(LS);
      sb.append("   *").append(LS);
      sb.append("   * @param n the node to visit").append(LS);
      sb.append("   */").append(LS);
    }
    sb.append("  public void visit(final ").append(Globals.nodeOptName).append(" n);").append(LS);
    return sb;
  }

  /**
   * Generates the visit method declaration on a {@link NodeSequence} for a visitor with user no
   * Return and no Argument data.
   *
   * @param aSB a buffer to print into (will be allocated if null)
   * @return the generated visit method
   */
  static StringBuilder genVoidVisitNodeSeq(final StringBuilder aSB) {
    StringBuilder sb = aSB;
    if (sb == null)
      sb = new StringBuilder(230);
    if (Globals.javaDocComments) {
      sb.append("  /**").append(LS);
      sb.append("   * Visits a {@link NodeSequence} node.").append(LS);
      sb.append("   *").append(LS);
      sb.append("   * @param n the node to visit").append(LS);
      sb.append("   */").append(LS);
    }
    sb.append("  public void visit(final ").append(Globals.nodeSeqName).append(" n);").append(LS);
    return sb;
  }

  /**
   * Generates the visit method declaration on a {@link NodeToken} for a visitor with user no Return
   * and no Argument data.
   *
   * @param aSB a buffer to print into (will be allocated if null)
   * @return the generated visit method
   */
  static StringBuilder genVoidVisitNodeToken(final StringBuilder aSB) {
    StringBuilder sb = aSB;
    if (sb == null)
      sb = new StringBuilder(230);
    if (Globals.javaDocComments) {
      sb.append("  /**").append(LS);
      sb.append("   * Visits a {@link NodeToken} node.").append(LS);
      sb.append("   *").append(LS);
      sb.append("   * @param n the node to visit").append(LS);
      sb.append("   */").append(LS);
    }
    sb.append("  public void visit(final ").append(Globals.nodeTokenName).append(" n);").append(LS);
    return sb;
  }

  /**
   * Generates package and visitor classes imports.
   *
   * @param aSB a buffer to print into (will be allocated if null)
   */
  static void packageAndImports(final StringBuilder aSB) {
    aSB.append("package ").append(Globals.nodesPackageName).append(";").append(LS).append(LS);
    aSB.append("import ").append(Globals.visitorsPackageName).append(".IRetArguVisitor;").append(LS);
    aSB.append("import ").append(Globals.visitorsPackageName).append(".IRetVisitor;").append(LS);
    aSB.append("import ").append(Globals.visitorsPackageName).append(".IVoidArguVisitor;").append(LS);
    aSB.append("import ").append(Globals.visitorsPackageName).append(".IVoidVisitor;").append(LS);
  }

  /**
   * Generates the serial uid member.
   *
   * @param aSB a buffer to print into (will be allocated if null)
   */
  static void serialUIDDeclaration(final StringBuilder aSB) {
    if (Globals.javaDocComments)
      aSB.append("  /** The serial version uid */").append(LS);
    aSB.append("  private static final long serialVersionUID = " + Globals.SERIAL_UID + "L;")
       .append(LS).append(LS);
  }

  /**
   * Generates the line separator member.
   *
   * @param aSB a buffer to print into (will be allocated if null)
   */
  static void lineSeparatorDeclaration(final StringBuilder aSB) {
    if (Globals.javaDocComments)
      aSB.append("  /** The OS line separator */").append(LS);
    aSB.append("  public static final String LS = System.getProperty(\"line.separator\");")
       .append(LS).append(LS);
  }

  /**
   * Generates parent pointer field.
   *
   * @param aSB a buffer to print into (will be allocated if null)
   * @return the generated declaration
   */
  static StringBuilder parentPointerDeclaration(final StringBuilder aSB) {
    StringBuilder sb = aSB;
    if (Globals.parentPointer) {
      if (sb == null)
        sb = new StringBuilder(50);
      if (Globals.javaDocComments)
        aSB.append("  /** The parent node */").append(LS);
      sb.append("  private ").append(Globals.iNodeName).append(" parent;").append(LS).append(LS);
    } else if (sb == null)
      sb = new StringBuilder(0);
    return sb;
  }

  /**
   * Generates parent pointer getter and setter methods.
   *
   * @param aSB a buffer to print into (will be allocated if null)
   * @return the generated methods
   */
  static StringBuilder parentPointerGetterSetter(final StringBuilder aSB) {
    StringBuilder sb = aSB;
    if (Globals.parentPointer) {
      if (sb == null)
        sb = new StringBuilder(200);
      if (Globals.javaDocComments) {
        sb.append("  /**").append(LS);
        sb.append("   * Sets the parent node.").append(LS);
        sb.append("   *").append(LS);
        sb.append("   * @param n the parent node").append(LS);
        sb.append("   */").append(LS);
      }
      sb.append("  public void setParent(final ").append(Globals.iNodeName).append(" n) {")
        .append(LS);
      sb.append("    parent = n;").append(LS);
      sb.append("  }").append(LS).append(LS);
      if (Globals.javaDocComments) {
        sb.append("  /**").append(LS);
        sb.append("   * Gets the parent node.").append(LS);
        sb.append("   *").append(LS);
        sb.append("   * @return the parent node").append(LS);
        sb.append("   */").append(LS);
      }
      sb.append("  public ").append(Globals.iNodeName).append(" getParent() {").append(LS);
      sb.append("    return parent;").append(LS);
      sb.append("  }").append(LS);
    } else if (sb == null)
      sb = new StringBuilder(0);
    return sb;
  }

  /**
   * Generates parent pointer set call.
   *
   * @param aSB a buffer to print into (must be non null)
   */
  static void parentPointerSetCall(final StringBuilder aSB) {
    aSB.append((Globals.parentPointer ? "    n.setParent(this);\n" : ""));
    aSB.append((Globals.parentPointer ? LS : ""));
  }

  /**
   * Generates the node list methods (for list nodes).
   *
   * @param aSB a buffer to print into (must be non null)
   */
  static void listMethods(final StringBuilder aSB) {
    if (Globals.javaDocComments) {
      aSB.append("  /**").append(LS);
      aSB.append("   * Gets the node in the list at a given position.").append(LS);
      aSB.append("   *").append(LS);
      aSB.append("   * @param i the node's position").append(LS);
      aSB.append("   * @return the node").append(LS);
      aSB.append("   */").append(LS);
    }
    aSB.append("  public ").append(Globals.iNodeName).append(" elementAt(final int i) {")
       .append(LS);
    aSB.append("    return nodes.get(i); }").append(LS).append(LS);

    if (Globals.javaDocComments) {
      aSB.append("  /**").append(LS);
      aSB.append("   * Returns an iterator on the nodes list.").append(LS);
      aSB.append("   *").append(LS);
      aSB.append("   * @return the iterator").append(LS);
      aSB.append("   */").append(LS);
    }
    aSB.append("  public Iterator<").append(Globals.iNodeName).append("> elements() {").append(LS);
    aSB.append("    return nodes.iterator(); }").append(LS).append(LS);

    if (Globals.javaDocComments) {
      aSB.append("  /**").append(LS);
      aSB.append("   * Returns the number of nodes in the list.").append(LS);
      aSB.append("   *").append(LS);
      aSB.append("   * @return the list size").append(LS);
      aSB.append("   */").append(LS);
    }
    aSB.append("  public int size() {").append(LS);
    aSB.append("    return nodes.size(); }").append(LS).append(LS);
  }

  /**
   * Generates the node interfaces accept methods.
   *
   * @param aSB a buffer to print into (must be non null)
   */
  static void interfacesAcceptMethods(final StringBuilder aSB) {
    if (Globals.javaDocComments)
      genAcceptRetArguComment(aSB);
    aSB.append("  public ").append(parRetArgu).append(" ").append(begRetArgu)
       .append(Globals.iRetArguVisitorName).append(parRetArgu).append(endRetArgu).append(";")
       .append(LS).append(LS);

    if (Globals.javaDocComments)
      genAcceptRetComment(aSB);
    aSB.append("  public ").append(parRet).append(" ").append(begRet)
       .append(Globals.iRetVisitorName).append(parRet).append(endRet).append(";").append(LS)
       .append(LS);

    if (Globals.javaDocComments)
      genAcceptVoidArguComment(aSB);
    aSB.append("  public ").append(parVoidArgu).append(" ").append(begVoidArgu)
       .append(Globals.iVoidArguVisitorName).append(parVoidArgu).append(endVoidArgu).append(";")
       .append(LS).append(LS);

    if (Globals.javaDocComments)
      genAcceptVoidComment(aSB);
    aSB.append("  public ").append(parVoid).append(begVoid).append(Globals.iVoidVisitorName)
       .append(parVoid).append(endVoid).append(";").append(LS).append(LS);
  }

  /**
   * Generates the javadoc comment for a method with user Return and Argument data.
   *
   * @param aSB a buffer to print into (must be non null)
   */
  static void genAcceptRetArguComment(final StringBuilder aSB) {
    aSB.append("  /**").append(LS);
    aSB
       .append("   * Accepts a {@link IRetArguVisitor} visitor with user Return and Argument data.")
       .append(LS);
    aSB.append("   *").append(LS);
    aSB.append("   * @param <R> the user Return type").append(LS);
    aSB.append("   * @param <A> the user Argument type").append(LS);
    aSB.append("   * @param vis the visitor").append(LS);
    aSB.append("   * @param argu the user Argument data").append(LS);
    aSB.append("   * @return the user Return data").append(LS);
    aSB.append("   */").append(LS);
  }

  /**
   * Generates the javadoc comment for a method with Return data.
   *
   * @param aSB a buffer to print into (must be non null)
   */
  static void genAcceptRetComment(final StringBuilder aSB) {
    aSB.append("  /**").append(LS);
    aSB.append("   * Accepts a {@link IRetVisitor} visitor with user Return data.").append(LS);
    aSB.append("   *").append(LS);
    aSB.append("   * @param <R> the user Return type").append(LS);
    aSB.append("   * @param vis the visitor").append(LS);
    aSB.append("   * @return the user Return data").append(LS);
    aSB.append("   */").append(LS);
  }

  /**
   * Generates the javadoc comment for a method with user Argument data.
   *
   * @param aSB a buffer to print into (must be non null)
   */
  static void genAcceptVoidArguComment(final StringBuilder aSB) {
    aSB.append("  /**").append(LS);
    aSB.append("   * Accepts a {@link IVoidArguVisitor} visitor with user Argument data.")
       .append(LS);
    aSB.append("   *").append(LS);
    aSB.append("   * @param <A> the user Argument type").append(LS);
    aSB.append("   * @param vis the visitor").append(LS);
    aSB.append("   * @param argu the user Argument data").append(LS);
    aSB.append("   */").append(LS);
  }

  /**
   * Generates the javadoc comment for a method with no user Return nor Argument data.
   *
   * @param aSB a buffer to print into (must be non null)
   */
  static void genAcceptVoidComment(final StringBuilder aSB) {
    aSB.append("  /**").append(LS);
    aSB
       .append("   * Accepts a {@link IVoidVisitor} visitor with no user Return nor Argument data.")
       .append(LS);
    aSB.append("   *").append(LS);
    aSB.append("   * @param vis the visitor").append(LS);
    aSB.append("   */").append(LS);
  }

  /**
   * Generates the node classes accept methods.
   *
   * @param aSB a buffer to print into (must be non null)
   */
  static void classesAcceptMethods(final StringBuilder aSB) {
    if (Globals.javaDocComments)
      genAcceptRetArguComment(aSB);
    aSB.append("  public ").append(parRetArgu).append(" ").append(begRetArgu)
       .append(Globals.iRetArguVisitorName).append(parRetArgu).append(endRetArgu).append(" {")
       .append(LS);
    aSB.append("    return vis.visit(this, argu);").append(LS);
    aSB.append("  }").append(LS).append(LS);

    if (Globals.javaDocComments)
      genAcceptRetComment(aSB);
    aSB.append("  public ").append(parRet).append(" ").append(begRet)
       .append(Globals.iRetVisitorName).append(parRet).append(endRet).append(" {").append(LS);
    aSB.append("    return vis.visit(this);").append(LS);
    aSB.append("  }").append(LS).append(LS);

    if (Globals.javaDocComments)
      genAcceptVoidArguComment(aSB);
    aSB.append("  public ").append(parVoidArgu).append(" ").append(begVoidArgu)
       .append(Globals.iVoidArguVisitorName).append(parVoidArgu).append(endVoidArgu).append(" {")
       .append(LS);
    aSB.append("    vis.visit(this, argu);").append(LS);
    aSB.append("  }").append(LS).append(LS);

    if (Globals.javaDocComments)
      genAcceptVoidComment(aSB);
    aSB.append("  public ").append(parVoid).append(begVoid).append(Globals.iVoidVisitorName)
       .append(parVoid).append(endVoid).append(" {").append(LS);
    aSB.append("    vis.visit(this);").append(LS);
    aSB.append("  }").append(LS).append(LS);
  }

  /**
   * Generates the javadoc comment for a method with user Return and Argument data.
   *
   * @param aSB a buffer to print into (must be non null)
   */
  static void genVisitRetArguComment(final StringBuilder aSB) {
    aSB.append("  /**").append(LS);
    aSB.append("   * Visits a node with user Return and Argument data.").append(LS);
    aSB.append("   *").append(LS);
    aSB.append("   * @param n the node to visit").append(LS);
    aSB.append("   * @param argu the user Argument data").append(LS);
    aSB.append("   * @return the user Return data").append(LS);
    aSB.append("   */").append(LS);
  }

  /**
   * Generates the javadoc comment for a method with user Return data.
   *
   * @param aSB a buffer to print into (must be non null)
   */
  static void genVisitRetComment(final StringBuilder aSB) {
    aSB.append("  /**").append(LS);
    aSB.append("   * Visits a node with user Return data.").append(LS);
    aSB.append("   *").append(LS);
    aSB.append("   * @param n the node to visit").append(LS);
    aSB.append("   * @return the user Return data").append(LS);
    aSB.append("   */").append(LS);
  }

  /**
   * Generates the javadoc comment for a method with user Argument data.
   *
   * @param aSB a buffer to print into (must be non null)
   */
  static void genVisitVoidArguComment(final StringBuilder aSB) {
    aSB.append("  /**").append(LS);
    aSB.append("   * Visits a node with user Argument data.").append(LS);
    aSB.append("   *").append(LS);
    aSB.append("   * @param n the node to visit").append(LS);
    aSB.append("   * @param argu the user Argument data").append(LS);
    aSB.append("   */").append(LS);
  }

  /**
   * Generates the javadoc comment for a method with no user Return nor Argument data.
   *
   * @param aSB a buffer to print into (must be non null)
   */
  static void genVisitVoidComment(final StringBuilder aSB) {
    aSB.append("  /**").append(LS);
    aSB.append("   * Visits a node with a user no user Return nor Argument data.").append(LS);
    aSB.append("   *").append(LS);
    aSB.append("   * @param n the node to visit").append(LS);
    aSB.append("   */").append(LS);
  }

  /*
   * Utility methods
   */

  /**
   * @return the extends clause.
   */
  static String extendsClause() {
    return (Globals.nodesSuperclass != null ? " extends " + Globals.nodesSuperclass : "");
  }

  /**
   * Generates the class parameter type(s).
   *
   * @param ret true if with a Return type, false if void
   * @param arg true if with a user Argument, false otherwise
   * @return the class parameter(s) string
   */
  static String genClassParamType(final boolean ret, final boolean arg) {
    if (ret) {
      if (arg) {
        return "<" + Globals.genRetType + ", " + Globals.genArguType + ">";
      } else {
        return "<" + Globals.genRetType + ">";
      }
    } else {
      if (arg) {
        return "<" + Globals.genArguType + ">";
      } else {
        return "";
      }
    }
  }

  /**
   * @param ret true if with a Return type, false if void
   * @return the beginning of the argument(s) list
   */
  static String begArgList(final boolean ret) {
    if (ret) {
      return Globals.genRetType + " accept(final ";
    } else {
      return "void accept(final ";
    }
  }

  /**
   * @param arg true if with a user Argument, false otherwise
   * @return the end of the argument(s) list
   */
  static String endArgList(final boolean arg) {
    if (arg) {
      return " vis, final " + Globals.genArguType + " argu)";
    } else {
      return " vis)";
    }
  }
}

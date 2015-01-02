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

import static EDU.purdue.jtb.misc.Globals.*;
import EDU.purdue.jtb.syntaxtree.INode;
import EDU.purdue.jtb.syntaxtree.INodeList;
import EDU.purdue.jtb.syntaxtree.NodeChoice;
import EDU.purdue.jtb.syntaxtree.NodeList;
import EDU.purdue.jtb.syntaxtree.NodeListOptional;
import EDU.purdue.jtb.syntaxtree.NodeOptional;
import EDU.purdue.jtb.syntaxtree.NodeSequence;
import EDU.purdue.jtb.syntaxtree.NodeTCF;
import EDU.purdue.jtb.syntaxtree.NodeToken;

/**
 * Class BaseClasses contains static methods to generated string representations of the base classes
 * (nodes and visitors).<br>
 * 
 * @author Marc Mazas
 * @version 1.4.0 : 05-08/2009 : MMa : adapted to JavaCC v4.2 grammar and JDK 1.5
 * @version 1.4.6 : 01/2011 : FA : added -va and -npfx and -nsfx options
 * @version 1.4.7 : 12/2011 : MMa : fixed extendsClause() in genNodeTCFClass<br>
 *          1.4.7 : 09/2012 : MMa : extracted constants, added missing visit methods (NodeChoice and
 *          NodeTCF)
 * @version 1.4.8 : 10/2014 : MMa : minor fix<br>
 *          1.4.8 : 12/2014 : MMa : added printing override annotations ;<br>
 *          improved specials printing in {@link NodeToken}
 * @version 1.4.9 : 12/2014 : MMa : fixed generated code NodeToken.withSpecials
 */
class BaseClasses {

  /** The Return and Argument parameter types */
  final static String parRetArgu  = genClassParamType(true, true);
  /** The beginning of argument list for Return and Argument parameter types */
  final static String begRetArgu  = begArgList(true);
  /** The end of argument list for Return and Argument parameter types */
  final static String endRetArgu  = endArgList(true);
  /** The Return and no Argument parameter type */
  final static String parRet      = genClassParamType(true, false);
  /** The beginning of argument list for Return and no Argument parameter types */
  final static String begRet      = begArgList(true);
  /** The end of argument list for Return and no Argument parameter types */
  final static String endRet      = endArgList(false);
  /** The No Return and Argument parameter types */
  final static String parVoidArgu = genClassParamType(false, true);
  /** The beginning of argument list for no Return and Argument parameter types */
  final static String begVoidArgu = begArgList(false);
  /** The end of argument list for no Return and Argument parameter types */
  final static String endVoidArgu = endArgList(true);
  /** The No Return and no Argument parameter types */
  final static String parVoid     = genClassParamType(false, false);
  /** The beginning of argument list for no Return and no Argument parameter types */
  final static String begVoid     = begArgList(false);
  /** The end of argument list for no Return and no Argument parameter types */
  final static String endVoid     = endArgList(false);

  /*
   * Node interfaces methods
   */

  /**
   * Generates the {@link INode} interface.
   * 
   * @param aSb - a buffer to print into (will be allocated if null)
   * @return the generated interface
   */
  static StringBuilder genINodeInterface(final StringBuilder aSb) {
    StringBuilder sb = aSb;
    if (sb == null) {
      sb = new StringBuilder(1900);
    }

    packageAndImports(sb);
    sb.append(LS);

    if (javaDocComments) {
      sb.append("/**").append(LS);
      sb.append(" * The interface which all syntax tree classes must implement.").append(LS);
      sb.append(" */").append(LS);
    }
    sb.append("public interface ").append(iNode).append(" extends java.io.Serializable {")
      .append(LS).append(LS);

    sb.append("  /** The OS line separator */").append(LS);
    sb.append("  public static final String LS = System.getProperty(\"line.separator\");")
      .append(LS).append(LS);

    interfacesAcceptMethods(sb, false);

    if (parentPointer) {
      if (javaDocComments) {
        sb.append("  /**").append(LS);
        sb.append("   * Gets the parent node.").append(LS);
        sb.append("   *").append(LS);
        sb.append("   * @return the parent node").append(LS);
        sb.append("   */").append(LS);
      }
      sb.append("  public ").append(iNode).append(" getParent();").append(LS).append(LS);
      if (javaDocComments) {
        sb.append("  /**").append(LS);
        sb.append("   * Sets the parent node. (It is the responsibility of each implementing class")
          .append(LS);
        sb.append("   * to call setParent() on each of its child nodes.)").append(LS);
        sb.append("   *").append(LS);
        sb.append("   * @param n - the parent node").append(LS);
        sb.append("   */").append(LS);
      }
      sb.append("  public void setParent(final ").append(iNode).append(" n);").append(LS);
    }

    sb.append('}').append(LS);

    return sb;
  }

  /**
   * Generates the {@link INodeList} interface.
   * 
   * @param aSb - a buffer to print into (will be allocated if null)
   * @return the generated interface
   */
  static StringBuilder genINodeListInterface(final StringBuilder aSb) {
    StringBuilder sb = aSb;
    if (sb == null) {
      sb = new StringBuilder(1850);
    }

    packageAndImports(sb);
    sb.append(LS);

    if (javaDocComments) {
      sb.append("/**").append(LS);
      sb.append(" * The interface which {@link ").append(nodeList).append("}, {@link ")
        .append(nodeListOpt).append("} and {@link ").append(nodeSeq).append("} must implement.")
        .append(LS);
      sb.append(" */").append(LS);
    }
    sb.append("public interface ").append(iNodeList).append(" extends ").append(iNode).append(" {")
      .append(LS).append(LS);

    if (javaDocComments) {
      sb.append("  /**").append(LS);
      sb.append("   * Adds a node to the list.").append(LS);
      sb.append("   *").append(LS);
      sb.append("   * @param n - the node to add").append(LS);
      sb.append("   */").append(LS);
    }
    sb.append("  public void addNode(final ").append(iNode).append(" n);").append(LS).append(LS);

    if (javaDocComments) {
      sb.append("  /**").append(LS);
      sb.append("   * @param i - the element index").append(LS);
      sb.append("   * @return the element at the given index").append(LS);
      sb.append("   */").append(LS);
    }
    sb.append("  public ").append(iNode).append(" elementAt(int i);").append(LS).append(LS);

    if (javaDocComments) {
      sb.append("  /**").append(LS);
      sb.append("   * @return the iterator on the node list").append(LS);
      sb.append("   */").append(LS);
    }
    sb.append("  public java.util.Iterator<").append(iNode).append("> elements();").append(LS)
      .append(LS);

    if (javaDocComments) {
      sb.append("  /**").append(LS);
      sb.append("   * @return the list size").append(LS);
      sb.append("   */").append(LS);
    }
    sb.append("  public int size();").append(LS).append(LS);

    interfacesAcceptMethods(sb, true);

    sb.append('}').append(LS);
    return sb;
  }

  /*
   * Node classes methods
   */

  /**
   * Generates the {@link NodeChoice} class.
   * 
   * @param aSb - a buffer to print into (will be allocated if null)
   * @return the generated class
   */
  static StringBuilder genNodeChoiceClass(final StringBuilder aSb) {
    StringBuilder sb = aSb;
    if (sb == null) {
      sb = new StringBuilder(2700);
    }

    packageAndImports(sb);
    sb.append(LS);

    if (javaDocComments) {
      sb.append("/**").append(LS);
      sb.append(" * Represents a grammar choice (|), e.g. ' ( A | B ) '.<br>").append(LS);
      sb.append(" * The class stores the node and the \"which\" choice indicator (0, 1, ...).")
        .append(LS);
      sb.append(" */").append(LS);
    }
    sb.append("public class ").append(nodeChoice).append(extendsClause()).append(" implements ")
      .append(iNode).append(" {").append(LS).append(LS);

    if (javaDocComments) {
      sb.append("  /** The real node */").append(LS);
    }
    sb.append("  public ").append(iNode).append(" choice;").append(LS).append(LS);

    if (javaDocComments) {
      sb.append("  /** The \"which\" choice indicator */").append(LS);
    }
    sb.append("  public int which;").append(LS).append(LS);

    if (javaDocComments) {
      sb.append("  /** The total number of choices */").append(LS);
    }
    sb.append("  public int total;").append(LS).append(LS);

    parentPointerDeclaration(sb);

    serialUIDDeclaration(sb);

    if (javaDocComments) {
      sb.append("  /**").append(LS);
      sb.append("   * Constructs the {@link NodeChoice} with a given node and non standard (-1) ")
        .append("which choice and total number of choices.").append(LS);
      sb.append("   *").append(LS);
      sb.append("   * @param node - the node").append(LS);
      sb.append("   */").append(LS);
    }
    sb.append("  public ").append(nodeChoice).append("(final ").append(iNode).append(" node) {")
      .append(LS);
    sb.append("   this(node, -1, -1);").append(LS);
    sb.append("  }").append(LS).append(LS);

    if (javaDocComments) {
      sb.append("  /**").append(LS);
      sb.append("   * Constructs the {@link NodeChoice} with a given node, a which choice and ")
        .append("a total (not controlled).").append(LS);
      sb.append("   *").append(LS);
      sb.append("   * @param node - the node").append(LS);
      sb.append("   * @param whichChoice - the which choice").append(LS);
      sb.append("   * @param totalChoices - the total number of choices").append(LS);
      sb.append("   */").append(LS);
    }
    sb.append("  public ").append(nodeChoice).append("(final ").append(iNode)
      .append(" node, final int whichChoice, final int totalChoices) {").append(LS);
    sb.append("    choice = node;").append(LS);
    sb.append("    which = whichChoice;").append(LS);
    sb.append("    total = totalChoices;").append(LS);
    if (parentPointer)
      sb.append("    choice.setParent(this);").append(LS);
    sb.append("  }").append(LS).append(LS);

    if (javaDocComments) {
      genAcceptRetArguComment(sb);
    }
    sb.append("  @Override").append(LS);
    sb.append("  public ").append(parRetArgu).append(' ').append(begRetArgu)
      .append(iRetArguVisitor).append(parRetArgu).append(endRetArgu).append(" {").append(LS);
    sb.append("    return choice.accept(vis, argu);").append(LS);
    sb.append("  }").append(LS).append(LS);

    if (javaDocComments) {
      genAcceptRetComment(sb);
    }
    sb.append("  @Override").append(LS);
    sb.append("  public ").append(parRet).append(' ').append(begRet).append(iRetVisitor)
      .append(parRet).append(endRet).append(" {").append(LS);
    sb.append("    return choice.accept(vis);").append(LS);
    sb.append("  }").append(LS).append(LS);

    if (javaDocComments) {
      genAcceptVoidArguComment(sb);
    }
    sb.append("  @Override").append(LS);
    sb.append("  public ").append(parVoidArgu).append(' ').append(begVoidArgu)
      .append(iVoidArguVisitor).append(parVoidArgu).append(endVoidArgu).append(" {").append(LS);
    sb.append("    choice.accept(vis, argu);").append(LS);
    sb.append("  }").append(LS).append(LS);

    if (javaDocComments) {
      genAcceptVoidComment(sb);
    }
    sb.append("  @Override").append(LS);
    sb.append("  public ").append(parVoid).append(begVoid).append(iVoidVisitor).append(parVoid)
      .append(endVoid).append(" {").append(LS);
    sb.append("    choice.accept(vis);").append(LS);
    sb.append("  }").append(LS).append(LS);

    parentPointerGetterSetter(sb);

    sb.append('}').append(LS);
    return sb;
  }

  /**
   * Generates the {@link NodeList} class.
   * 
   * @param aSb - a buffer to print into (will be allocated if null)
   * @return the generated class
   */
  static StringBuilder genNodeListClass(final StringBuilder aSb) {
    StringBuilder sb = aSb;
    if (sb == null) {
      sb = new StringBuilder(4050);
    }

    packageAndImports(sb);
    sb.append("import java.util.*;").append(LS).append(LS);

    if (javaDocComments) {
      sb.append("/**").append(LS);
      sb.append(" * Represents a grammar list (+), e.g. ' ( A )+ '.<br>").append(LS);
      sb.append(" * The class stores the nodes list in an ArrayList.").append(LS);
      sb.append(" */").append(LS);
    }
    sb.append("public class ").append(nodeList).append(extendsClause()).append(" implements ")
      .append(iNodeList).append(" {").append(LS).append(LS);

    if (javaDocComments) {
      sb.append("  /** The list of nodes */").append(LS);
    }
    sb.append("  public ArrayList<").append(iNode).append("> nodes;").append(LS).append(LS);

    if (javaDocComments) {
      sb.append("  /** The allocation sizes table */").append(LS);
    }
    sb.append("  private static final int allocTb[] = {1, 2, 3, 4, 5, 10, 20, 50};").append(LS)
      .append(LS);

    if (javaDocComments) {
      sb.append("  /** The allocation number */").append(LS);
    }
    sb.append("  private int allocNb = 0;").append(LS).append(LS);

    parentPointerDeclaration(sb);

    serialUIDDeclaration(sb);

    if (javaDocComments) {
      sb.append("  /**").append(LS);
      sb.append("   * Initializes an empty list of nodes with a default first allocation.")
        .append(LS);
      sb.append("   */").append(LS);
    }
    sb.append("  public ").append(nodeList).append("() {").append(LS);
    sb.append("    nodes = new ArrayList<").append(iNode).append(">(allocTb[allocNb]);").append(LS);
    sb.append("  }").append(LS).append(LS);

    if (javaDocComments) {
      sb.append("  /**").append(LS);
      sb.append("   * Initializes an empty list of nodes with a given allocation.").append(LS);
      sb.append("   *").append(LS);
      sb.append("   * @param sz - the list size").append(LS);
      sb.append("   */").append(LS);
    }
    sb.append("  public ").append(nodeList).append("(final int sz) {").append(LS);
    sb.append("    nodes = new ArrayList<").append(iNode).append(">(sz);").append(LS);
    sb.append("  }").append(LS).append(LS);

    if (javaDocComments) {
      sb.append("  /**").append(LS);
      sb.append("   * Initializes an empty list of nodes with a default first allocation ")
        .append("and adds a first node.").append(LS);
      sb.append("   *").append(LS);
      sb.append("   * @param firstNode - the node to add").append(LS);
      sb.append("   */").append(LS);
    }
    sb.append("  public ").append(nodeList).append("(final ").append(iNode).append(" firstNode) {")
      .append(LS);
    sb.append("    nodes = new ArrayList<").append(iNode).append(">(allocTb[allocNb]);").append(LS);
    sb.append("    addNode(firstNode);").append(LS);
    sb.append("  }").append(LS).append(LS);

    if (javaDocComments) {
      sb.append("  /**").append(LS);
      sb.append("   * Initializes an empty list of nodes with a given allocation and ")
        .append("adds a first node.").append(LS);
      sb.append("   *").append(LS);
      sb.append("   * @param sz - the list size").append(LS);
      sb.append("   * @param firstNode - the node to add").append(LS);
      sb.append("   */").append(LS);
    }
    sb.append("  public ").append(nodeList).append("(final int sz, final ").append(iNode)
      .append(" firstNode) {").append(LS);
    sb.append("    nodes = new ArrayList<").append(iNode).append(">(sz);").append(LS);
    sb.append("    addNode(firstNode);").append(LS);
    sb.append("  }").append(LS).append(LS);

    if (javaDocComments) {
      sb.append("  /**").append(LS);
      sb.append("   * Adds a node to the list of nodes, managing progressive ")
        .append("allocation increments.").append(LS);
      sb.append("   *").append(LS);
      sb.append("   * @param n - the node to add").append(LS);
      sb.append("   */").append(LS);
    }
    sb.append("  @Override").append(LS);
    sb.append("  public void addNode(final ").append(iNode).append(" n) {").append(LS);
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

    sb.append('}').append(LS);
    return sb;
  }

  /**
   * Generates the {@link NodeListOptional} class.
   * 
   * @param aSb - a buffer to print into (will be allocated if null)
   * @return the generated class
   */
  static StringBuilder genNodeListOptClass(final StringBuilder aSb) {
    StringBuilder sb = aSb;
    if (sb == null) {
      sb = new StringBuilder(4250);
    }

    packageAndImports(sb);
    sb.append("import java.util.*;").append(LS).append(LS);

    if (javaDocComments) {
      sb.append("/**").append(LS);
      sb.append(" * Represents an optional grammar list (*), e.g. ' ( A )* '.<br>").append(LS);
      sb.append(" * The class stores the nodes list in an ArrayList.").append(LS);
      sb.append(" */").append(LS);
    }
    sb.append("public class ").append(nodeListOpt).append(extendsClause()).append(" implements ")
      .append(iNodeList).append(" {").append(LS).append(LS);

    if (javaDocComments) {
      sb.append("  /** The list of nodes */").append(LS);
    }
    sb.append("  public ArrayList<").append(iNode).append("> nodes;").append(LS).append(LS);

    if (javaDocComments) {
      sb.append("  /** The allocation sizes table */").append(LS);
    }
    sb.append("  private static final int allocTb[] = {0, 1, 2, 3, 4, 5, 10, 20, 50};").append(LS)
      .append(LS);

    if (javaDocComments) {
      sb.append("  /** The allocation number */").append(LS);
    }
    sb.append("  private int allocNb = 0;").append(LS).append(LS);

    parentPointerDeclaration(sb);

    serialUIDDeclaration(sb);

    if (javaDocComments) {
      sb.append("  /**").append(LS);
      sb.append("   * Initializes an empty list of nodes with a default first allocation.")
        .append(LS);
      sb.append("   */").append(LS);
    }
    sb.append("  public ").append(nodeListOpt).append("() {").append(LS);
    sb.append("    nodes = new ArrayList<").append(iNode).append(">(allocTb[allocNb]);").append(LS);
    sb.append("  }").append(LS).append(LS);

    if (javaDocComments) {
      sb.append("  /**").append(LS);
      sb.append("   * Initializes an empty list of nodes with a given allocation.").append(LS);
      sb.append("   *").append(LS);
      sb.append("   * @param sz - the list size").append(LS);
      sb.append("   */").append(LS);
    }
    sb.append("  public ").append(nodeListOpt).append("(final int sz) {").append(LS);
    sb.append("    nodes = new ArrayList<").append(iNode).append(">(sz);").append(LS);
    sb.append("  }").append(LS).append(LS);

    if (javaDocComments) {
      sb.append("  /**").append(LS);
      sb.append("   * Initializes an empty list of nodes with a default first allocation and ")
        .append("adds a first node.").append(LS);
      sb.append("   *").append(LS);
      sb.append("   * @param firstNode - the node to add").append(LS);
      sb.append("   */").append(LS);
    }
    sb.append("  public ").append(nodeListOpt).append("(final ").append(iNode)
      .append(" firstNode) {").append(LS);
    sb.append("    nodes = new ArrayList<").append(iNode).append(">(allocTb[allocNb]);").append(LS);
    sb.append("    addNode(firstNode);").append(LS);
    sb.append("  }").append(LS).append(LS);

    if (javaDocComments) {
      sb.append("  /**").append(LS);
      sb.append("   * Initializes an empty list of nodes with a given allocation and ")
        .append("adds a first node.").append(LS);
      sb.append("   *").append(LS);
      sb.append("   * @param sz - the list size").append(LS);
      sb.append("   * @param firstNode - the node to add").append(LS);
      sb.append("   */").append(LS);
    }
    sb.append("  public ").append(nodeListOpt).append("(final int sz, final ").append(iNode)
      .append(" firstNode) {").append(LS);
    sb.append("    nodes = new ArrayList<").append(iNode).append(">(sz);").append(LS);
    sb.append("    addNode(firstNode);").append(LS);
    sb.append("  }").append(LS).append(LS);

    if (javaDocComments) {
      sb.append("  /**").append(LS);
      sb.append("   * Adds a node to the list of nodes, managing progressive ")
        .append("allocation increments.").append(LS);
      sb.append("   *").append(LS);
      sb.append("   * @param n - the node to add").append(LS);
      sb.append("   */").append(LS);
    }
    sb.append("  @Override").append(LS);
    sb.append("  public void addNode(final ").append(iNode).append(" n) {").append(LS);
    sb.append("    if (++allocNb < allocTb.length)").append(LS);
    sb.append("      nodes.ensureCapacity(allocTb[allocNb]);").append(LS);
    sb.append("    else").append(LS);
    sb.append("      nodes.ensureCapacity((allocNb - allocTb.length + 2) * ")
      .append("allocTb[(allocTb.length - 1)]);").append(LS);
    sb.append("    nodes.add(n);").append(LS);
    parentPointerSetCall(sb);
    sb.append("  }").append(LS).append(LS);

    listMethods(sb);

    if (javaDocComments) {
      sb.append("  /**").append(LS);
      sb.append("   * @return true if there is at least one node, false otherwise").append(LS);
      sb.append("   */").append(LS);
    }
    sb.append("  public boolean present() {").append(LS);
    sb.append("    return (nodes.size() != 0); }").append(LS).append(LS);

    classesAcceptMethods(sb);

    parentPointerGetterSetter(sb);

    sb.append('}').append(LS);
    return sb;
  }

  /**
   * Generates the {@link NodeOptional} class.
   * 
   * @param aSb - a buffer to print into (will be allocated if null)
   * @return the generated class
   */
  static StringBuilder genNodeOptClass(final StringBuilder aSb) {
    StringBuilder sb = aSb;
    if (sb == null) {
      sb = new StringBuilder(2700);
    }

    packageAndImports(sb);
    sb.append(LS);

    if (javaDocComments) {
      sb.append("/**").append(LS);
      sb.append(" * Represents a grammar optional node (? or []), e.g. ' ( A )? ' or ' [ A ] '.<br>")
        .append(LS);
      sb.append(" * The class stores the node.").append(LS);
      sb.append(" */").append(LS);
    }
    sb.append("public class ").append(nodeOpt).append(extendsClause()).append(" implements ")
      .append(iNode).append(" {").append(LS).append(LS);

    if (javaDocComments) {
      sb.append("  /** The node (if null there is no node) */").append(LS);
    }
    sb.append("  public ").append(iNode).append(" node;").append(LS).append(LS);

    parentPointerDeclaration(sb);

    serialUIDDeclaration(sb);

    if (javaDocComments) {
      sb.append("  /**").append(LS);
      sb.append("   * Initializes an empty {@link NodeOptional}.").append(LS);
      sb.append("   */").append(LS);
    }
    sb.append("  public ").append(nodeOpt).append("() {").append(LS);
    sb.append("    node = null;").append(LS);
    sb.append("  }").append(LS).append(LS);

    if (javaDocComments) {
      sb.append("  /**").append(LS);
      sb.append("   * Initializes a {@link NodeOptional} with a node.").append(LS);
      sb.append("   *").append(LS);
      sb.append("   * @param n - the node").append(LS);
      sb.append("   */").append(LS);
    }
    sb.append("  public ").append(nodeOpt).append("(final ").append(iNode).append(" n) {")
      .append(LS);
    sb.append("    addNode(n);").append(LS);
    sb.append("  }").append(LS).append(LS);

    if (javaDocComments) {
      sb.append("  /**").append(LS);
      sb.append("   * Adds a node to the {@link NodeOptional}.").append(LS);
      sb.append("   *").append(LS);
      sb.append("   * @param n - the node to add").append(LS);
      sb.append("   */").append(LS);
    }
    sb.append("  public void addNode(final ").append(iNode).append(" n) {").append(LS);
    sb.append("    if (node != null)").append(LS)
      .append("      throw new Error(\"Attempt to set optional node twice\");").append(LS);
    sb.append("    node = n;").append(LS);
    parentPointerSetCall(sb);
    sb.append("  }").append(LS).append(LS);

    if (javaDocComments) {
      sb.append("  /**").append(LS);
      sb.append("   * @return true if the node exists, false otherwise").append(LS);
      sb.append("   */").append(LS);
    }
    sb.append("  public boolean present() {").append(LS);
    sb.append("    return (node != null); }").append(LS).append(LS);

    classesAcceptMethods(sb);

    parentPointerGetterSetter(sb);

    sb.append('}').append(LS);
    return sb;
  }

  /**
   * Generates the {@link NodeSequence} class.
   * 
   * @param aSb - a buffer to print into (will be allocated if null)
   * @return the generated class
   */
  static StringBuilder genNodeSeqClass(final StringBuilder aSb) {
    StringBuilder sb = aSb;
    if (sb == null) {
      sb = new StringBuilder(3800);
    }

    packageAndImports(sb);
    sb.append(LS);

    sb.append("import java.util.*;").append(LS).append(LS);
    if (javaDocComments) {
      sb.append("/**").append(LS);
      sb.append(" * Represents a sequence of nodes (x y z ...) nested ")
        .append("within a choice (|), list (+),").append(LS)
        .append(" * optional list (*), or optional node (? or []), e.g. ' ( A B )+ ' ")
        .append("or ' [ C D E ] '.<br>").append(LS);
      sb.append(" * The class stores the nodes list in an ArrayList.").append(LS);
      sb.append(" */").append(LS);
    }
    sb.append("public class ").append(nodeSeq).append(extendsClause()).append(" implements ")
      .append(iNodeList).append(" {").append(LS).append(LS);

    if (javaDocComments) {
      sb.append("  /** The list of nodes */").append(LS);
    }
    sb.append("  public ArrayList<").append(iNode).append("> nodes;").append(LS).append(LS);

    parentPointerDeclaration(sb);

    serialUIDDeclaration(sb);

    if (javaDocComments) {
      sb.append("  /**").append(LS);
      sb.append("   * Initializes an empty {@link NodeSequence} with a default allocation.")
        .append(LS);
      sb.append("   */").append(LS);
    }
    sb.append("  public ").append(nodeSeq).append("() {").append(LS);
    sb.append("    nodes = new ArrayList<").append(iNode).append(">();").append(LS);
    sb.append("  }").append(LS).append(LS);

    if (javaDocComments) {
      sb.append("  /**").append(LS);
      sb.append("   * Initializes an empty {@link NodeSequence} with a given allocation.")
        .append(LS);
      sb.append("   *").append(LS);
      sb.append("   * @param sz - the list size").append(LS);
      sb.append("   */").append(LS);
    }
    sb.append("  public ").append(nodeSeq).append("(final int sz) {").append(LS);
    sb.append("    nodes = new ArrayList<").append(iNode).append(">(sz);").append(LS);
    sb.append("  }").append(LS).append(LS);

    if (javaDocComments) {
      sb.append("  /**").append(LS);
      sb.append("   * Initializes an empty {@link NodeSequence} with a default allocation ")
        .append("and adds a first node.").append(LS);
      sb.append("   *").append(LS);
      sb.append("   * @param firstNode - the node to add").append(LS);
      sb.append("   */").append(LS);
    }
    sb.append("  public ").append(nodeSeq).append("(final ").append(iNode).append(" firstNode) {")
      .append(LS);
    sb.append("    nodes = new ArrayList<").append(iNode).append(">();").append(LS);
    sb.append("    addNode(firstNode);").append(LS);
    sb.append("  }").append(LS).append(LS);

    if (javaDocComments) {
      sb.append("  /**").append(LS);
      sb.append("   * Initializes an empty {@link NodeSequence} with a given allocation ")
        .append("and adds a first node.").append(LS);
      sb.append("   *").append(LS);
      sb.append("   * @param sz - the list size").append(LS);
      sb.append("   * @param firstNode - the node to add").append(LS);
      sb.append("   */").append(LS);
    }
    sb.append("  public ").append(nodeSeq).append("(final int sz, final ").append(iNode)
      .append(" firstNode) {").append(LS);
    sb.append("    nodes = new ArrayList<").append(iNode).append(">(sz);").append(LS);
    sb.append("    addNode(firstNode);").append(LS);
    sb.append("  }").append(LS).append(LS);

    if (javaDocComments) {
      sb.append("  /**").append(LS);
      sb.append("   * Adds a node to the {@link NodeSequence}.").append(LS);
      sb.append("   *").append(LS);
      sb.append("   * @param n - the node to add").append(LS);
      sb.append("   */").append(LS);
    }
    sb.append("  @Override").append(LS);
    sb.append("  public void addNode(final ").append(iNode).append(" n) {").append(LS);
    sb.append("    nodes.add(n);").append(LS);
    parentPointerSetCall(sb);
    sb.append("  }").append(LS).append(LS);

    listMethods(sb);

    classesAcceptMethods(sb);

    parentPointerGetterSetter(sb);

    sb.append('}').append(LS);
    return sb;
  }

  /**
   * Generates the {@link NodeToken} class.
   * 
   * @param aSb - a buffer to print into (will be allocated if null)
   * @return the generated class
   */
  static StringBuilder genNodeTokenClass(final StringBuilder aSb) {
    StringBuilder sb = aSb;
    if (sb == null) {
      sb = new StringBuilder(5970);
    }

    packageAndImports(sb);
    sb.append("import java.util.*;").append(LS).append(LS);

    if (javaDocComments) {
      sb.append("/**").append(LS);
      sb.append(" * Represents a single token in the grammar.<br>").append(LS);
      sb.append(" * If the \"-tk\" option is used, also contains a ArrayList of preceding ")
        .append("special tokens.<br>").append(LS);
      sb.append(" * The class stores the token image, kind and position information, ")
        .append("and the special tokens list.<br>").append(LS);
      sb.append(" */").append(LS);
    }
    sb.append("public class ").append(nodeToken).append(extendsClause()).append(" implements ")
      .append(iNode).append(" {").append(LS).append(LS);

    if (javaDocComments) {
      sb.append("  /** The token image */").append(LS);
    }
    sb.append("  public String tokenImage;").append(LS).append(LS);

    if (javaDocComments) {
      sb.append("  /** The list of special tokens */").append(LS);
    }
    sb.append("  public List<").append(nodeToken).append("> specialTokens;").append(LS).append(LS);

    if (javaDocComments) {
      sb.append("  /** The token first line (-1 means not available) */").append(LS);
    }
    sb.append("  public int beginLine;").append(LS).append(LS);
    if (javaDocComments) {
      sb.append("  /** The token first column (-1 means not available) */").append(LS);
    }
    sb.append("  public int beginColumn;").append(LS).append(LS);
    if (javaDocComments) {
      sb.append("  /** The token last line (-1 means not available) */").append(LS);
    }
    sb.append("  public int endLine;").append(LS).append(LS);
    if (javaDocComments) {
      sb.append("  /** The token last column (-1 means not available) */").append(LS);
    }
    sb.append("  public int endColumn;").append(LS).append(LS);

    if (javaDocComments) {
      sb.append("  /** The JavaCC token \"kind\" integer (-1 means not available) */").append(LS);
    }
    sb.append("  public int kind;").append(LS).append(LS);

    parentPointerDeclaration(sb);

    serialUIDDeclaration(sb);

    lineSeparatorDeclaration(sb);

    if (javaDocComments) {
      sb.append("  /**").append(LS);
      sb.append("   * Initializes a {@link NodeToken} with a given string and ")
        .append("no position information.").append(LS);
      sb.append("   *").append(LS);
      sb.append("   * @param s - the token string").append(LS);
      sb.append("   */").append(LS);
    }
    sb.append("  public ").append(nodeToken).append("(final String s) {").append(LS);
    sb.append("    this(s, -1, -1, -1, -1, -1);").append(LS);
    sb.append("  }").append(LS).append(LS);

    if (javaDocComments) {
      sb.append("  /**").append(LS);
      sb.append("   * Initializes a {@link NodeToken} with a given string and ")
        .append("position information.").append(LS);
      sb.append("   *").append(LS);
      sb.append("   * @param s - the token string").append(LS);
      sb.append("   * @param kn - the token kind").append(LS);
      sb.append("   * @param bl - the first line").append(LS);
      sb.append("   * @param bc - the first column").append(LS);
      sb.append("   * @param el - the last line").append(LS);
      sb.append("   * @param ec - the last column").append(LS);
      sb.append("   */").append(LS);
    }
    sb.append("  public ").append(nodeToken)
      .append("(final String s, final int kn, final int bl, final int bc, ")
      .append("final int el, final int ec) {").append(LS);
    sb.append("    tokenImage = s;").append(LS);
    sb.append("    specialTokens = null;").append(LS);
    sb.append("    kind = kn;").append(LS);
    sb.append("    beginLine = bl;").append(LS);
    sb.append("    beginColumn = bc;").append(LS);
    sb.append("    endLine = el;").append(LS);
    sb.append("    endColumn = ec;").append(LS);
    sb.append("  }").append(LS).append(LS);

    if (javaDocComments) {
      sb.append("  /**").append(LS);
      sb.append("   * Gets the special token in the special tokens list at a given position.")
        .append(LS);
      sb.append("   *").append(LS);
      sb.append("   * @param i - the special token's position").append(LS);
      sb.append("   * @return the special token").append(LS);
      sb.append("   */").append(LS);
    }
    sb.append("  public ").append(nodeToken).append(" getSpecialAt(final int i) {").append(LS);
    sb.append("    if (specialTokens == null)").append(LS);
    sb.append("      throw new NoSuchElementException(\"No specialTokens in token\");").append(LS);
    sb.append("    return specialTokens.get(i);").append(LS);
    sb.append("  }").append(LS).append(LS);

    if (javaDocComments) {
      sb.append("  /**").append(LS);
      sb.append("   * @return the number of special tokens").append(LS);
      sb.append("   */").append(LS);
    }
    sb.append("  public int numSpecials() {").append(LS).append("    if (specialTokens == null)")
      .append(LS);
    sb.append("      return 0;").append(LS).append("    return specialTokens.size();").append(LS);
    sb.append("  }").append(LS).append(LS);

    if (javaDocComments) {
      sb.append("  /**").append(LS);
      sb.append("   * Adds a special token to the special tokens list.").append(LS);
      sb.append("   *").append(LS);
      sb.append("   * @param s - the special token to add").append(LS);
      sb.append("   */").append(LS);
    }
    sb.append("  public void addSpecial(final ").append(nodeToken).append(" s) {").append(LS);
    sb.append("    if (specialTokens == null)").append(LS);
    sb.append("      specialTokens = new ArrayList<").append(nodeToken).append(">();").append(LS);
    sb.append("    specialTokens.add(s);").append(LS);
    if (parentPointer)
      sb.append("    s.setParent(this);").append(LS);
    sb.append("  }").append(LS).append(LS);

    if (javaDocComments) {
      sb.append("  /**").append(LS);
      sb.append("   * Trims the special tokens list.").append(LS);
      sb.append("   */").append(LS);
    }
    sb.append("  public void trimSpecials() {").append(LS);
    sb.append("    if (specialTokens == null)").append(LS);
    sb.append("      return;").append(LS);
    sb.append("    ((ArrayList<NodeToken>) specialTokens).trimToSize();").append(LS);
    sb.append("  }").append(LS).append(LS);

    if (javaDocComments) {
      sb.append("  /**").append(LS);
      sb.append("   * @return the token image").append(LS);
      sb.append("   */").append(LS);
    }
    sb.append("  @Override").append(LS);
    sb.append("  public String toString() {").append(LS);
    sb.append("    return tokenImage;").append(LS);
    sb.append("  }").append(LS).append(LS);

    if (javaDocComments) {
      sb.append("  /**").append(LS);
      sb.append("   * Returns the list of special tokens of the current {@link NodeToken} ")
        .append("as a string,<br>").append(LS);
      sb.append("   * taking in account a given indentation.").append(LS);
      sb.append("   *").append(LS);
      sb.append("   * @param spc - the indentation").append(LS);
      sb.append("   * @return the string representing the special tokens list").append(LS);
      sb.append("   */").append(LS);
    }
    sb.append("  public String getSpecials(final String spc) {").append(LS);
    sb.append("    if (specialTokens == null)").append(LS);
    sb.append("      return \"\";").append(LS);
    sb.append("    int stLastLine = -1;").append(LS);
    sb.append("    final StringBuilder buf = new StringBuilder(64);").append(LS);
    sb.append("    boolean hasEol = false;").append(LS);
    sb.append("    for (final Iterator<NodeToken> e = specialTokens.iterator(); e.hasNext();) {")
      .append(LS);
    sb.append("      final NodeToken st = e.next();").append(LS);
    sb.append("      final char c = st.tokenImage.charAt(st.tokenImage.length() - 1);").append(LS);
    sb.append("      hasEol = c == '\\n' || c == '\\r';").append(LS);
    //    sb.append("      buf.append(\" /* getSpecials stLastLine = \").append(stLastLine).append(\", st.beginLine = \")")
    //      .append(LS);
    //    sb.append("        .append(st.beginLine).append(\", c = \").append(hasEol ? \"eol\" : String.valueOf(c))")
    //      .append(LS);
    //    sb.append("        .append(\" */\");").append(LS);
    sb.append("      if (stLastLine != -1)").append(LS);
    sb.append("        // not first line ").append(LS);
    sb.append("        if (stLastLine != st.beginLine)").append(LS);
    sb.append("          // if not on the same line as the previous").append(LS);
    sb.append("          buf.append(spc);").append(LS);
    sb.append("        else").append(LS);
    sb.append("          // on the same line as the previous").append(LS);
    sb.append("          buf.append(' ');").append(LS);
    sb.append("      buf.append(st.tokenImage);").append(LS);
    sb.append("      if (!hasEol && e.hasNext()) {").append(LS);
    sb.append("        // not a single line comment and not the last one").append(LS);
    sb.append("        buf.append(LS);").append(LS);
    sb.append("      }").append(LS);
    sb.append("      stLastLine = st.endLine;").append(LS);
    sb.append("    }").append(LS);
    sb.append("    // keep the same number of blank lines before the current non special")
      .append(LS);
    sb.append("    for (int i = stLastLine + (hasEol ? 1 : 0); i < beginLine; i++) {").append(LS);
    sb.append("      buf.append(LS);").append(LS);
    sb.append("      if (i != beginLine - 1)").append(LS);
    sb.append("        buf.append(spc);").append(LS);
    sb.append("    }").append(LS);
    sb.append("    // indent if the current non special is not on the same line").append(LS);
    sb.append("    if (stLastLine != beginLine)").append(LS);
    sb.append("      buf.append(spc);").append(LS);
    //    sb.append("    buf.append(\" /* getSpecials stLastLine = \").append(stLastLine).append(\", beginLine = \")")
    //      .append(LS);
    //    sb.append("      .append(beginLine).append(\", size = \").append(specialTokens.size()).append(\", hasEol = \")")
    //      .append(LS);
    //    sb.append("      .append(hasEol).append(\" */ \");").append(LS);
    sb.append("    return buf.toString();").append(LS);
    sb.append("  }").append(LS);

    if (javaDocComments) {
      sb.append("  /**").append(LS);
      sb.append("   * Returns the list of special tokens of the current {@link NodeToken} ")
        .append("and the current<br>").append(LS);
      sb.append("   * {@link NodeToken} as a string, taking in account a given indentation.")
        .append(LS);
      sb.append("   *").append(LS);
      sb.append("   * @param spc - the indentation").append(LS);
      sb.append("   * @return the string representing the special tokens list and the token")
        .append(LS);
      sb.append("   */").append(LS);
    }
    sb.append("  public String withSpecials(final String spc) {").append(LS);
    sb.append("    return withSpecials(spc, null);").append(LS);
    sb.append("  }").append(LS).append(LS);

    if (javaDocComments) {
      sb.append("  /**").append(LS);
      sb.append("   * Returns the list of special tokens of the current {@link NodeToken} ")
        .append("and the current<br>").append(LS);
      sb.append("   * {@link NodeToken} as a string, taking in account a given indentation and a given ")
        .append("assignment.").append(LS);
      sb.append("   *").append(LS);
      sb.append("   * @param spc - the indentation").append(LS);
      sb.append("   * @param var - the variable assignment to be inserted").append(LS);
      sb.append("   * @return the string representing the special tokens list and the token")
        .append(LS);
      sb.append("   */").append(LS);
    }
    sb.append("  public String withSpecials(final String spc, final String var) {").append(LS);
    sb.append("    final String specials = getSpecials(spc);").append(LS);
    sb.append("    int len = specials.length();").append(LS);
    sb.append("    if (len == 0)").append(LS);
    sb.append("      return (var == null ? tokenImage : var + tokenImage);").append(LS);
    sb.append("    if (var != null)").append(LS);
    sb.append("      len += var.length();").append(LS);
    sb.append("    StringBuilder buf = new StringBuilder(len + tokenImage.length());").append(LS);
    sb.append("    buf.append(specials);").append(LS);
    sb.append("  // see if needed to add a space").append(LS);
    sb.append("  int stLastLine = -1;").append(LS);
    sb.append("  for (final Iterator<NodeToken> e = specialTokens.iterator(); e.hasNext();) {")
      .append(LS);
    sb.append("    stLastLine = e.next().endLine;").append(LS);
    sb.append("  }").append(LS);
    sb.append("  if (stLastLine == beginLine)").append(LS);
    sb.append("    buf.append(' ');").append(LS);
    sb.append("  if (var != null)").append(LS);
    sb.append("    buf.append(var);").append(LS);
    sb.append("  buf.append(tokenImage);").append(LS);
    //    sb.append("  buf.append(\" /* withSpecials lastLine = \").append(stLastLine).append(\", beginLine = \")")
    //      .append(LS);
    //    sb.append("    .append(beginLine).append(\" */ \");").append(LS);
    sb.append("    return buf.toString();").append(LS);
    sb.append("  }").append(LS).append(LS);

    classesAcceptMethods(sb);

    parentPointerGetterSetter(sb);

    sb.append('}').append(LS);

    return sb;
  }

  /**
   * Generates the {@link NodeToken} class.
   * 
   * @param aSb - a buffer to print into (will be allocated if null)
   * @return the generated class
   */
  static StringBuilder genNodeTCFClass(final StringBuilder aSb) {
    StringBuilder sb = aSb;
    if (sb == null) {
      sb = new StringBuilder(1000);
    }

    sb.append("package ").append(nodesPackageName).append(';').append(LS).append(LS);
    if (javaDocComments) {
      sb.append("/**").append(LS);
      sb.append(" * Represents a token in an ExpansionUnit type 3 in the grammar.<br>").append(LS);
      sb.append(" * If the \"-tk\" option is used, also contains a ArrayList of preceding ")
        .append("special tokens.<br>").append(LS);
      sb.append(" */").append(LS);
    }
    sb.append("public class ").append(nodeTCF).append(" extends ").append(nodeToken).append(" {")
      .append(LS).append(LS);

    serialUIDDeclaration(sb);

    if (javaDocComments) {
      sb.append("  /**").append(LS);
      sb.append("   * Initializes a {@link ").append(nodeTCF).append("} with a given string and ")
        .append("no position information.").append(LS);
      sb.append("   *").append(LS);
      sb.append("   * @param s - the token string").append(LS);
      sb.append("   */").append(LS);
    }
    sb.append("  public ").append(nodeTCF).append("(String s) {").append(LS);
    sb.append("    super(s, -1, -1, -1, -1, -1);").append(LS);
    sb.append("  }").append(LS).append(LS);

    if (javaDocComments) {
      sb.append("  /**").append(LS);
      sb.append("   * Initializes a {@link ").append(nodeTCF).append("} with a given string and ")
        .append("position information.").append(LS);
      sb.append("   *").append(LS);
      sb.append("   * @param s - the token string").append(LS);
      sb.append("   * @param kn - the token kind").append(LS);
      sb.append("   * @param bl - the first line").append(LS);
      sb.append("   * @param bc - the first column").append(LS);
      sb.append("   * @param el - the last line").append(LS);
      sb.append("   * @param ec - the last column").append(LS);
      sb.append("   */").append(LS);
    }
    sb.append("  public ").append(nodeTCF)
      .append("(String s, final int kn, final int bl, final int bc, ")
      .append("final int el, final int ec) {").append(LS);
    sb.append("    super(s, kn, bl, bc, el, ec);").append(LS);
    sb.append("  }").append(LS).append(LS);

    sb.append('}').append(LS);

    return sb;
  }

  /*
   * "RetArgu" visit methods
   */

  /**
   * Generates the visit method declaration on a given node type for a visitor with user Return and
   * Argument data.
   * 
   * @param aSb - a buffer to print into (will be allocated if null)
   * @param aBufSize - the initial buffer size
   * @param aNodeType - the node type
   * @return the generated visit method
   */
  private static StringBuilder genRetArguVisitAnyNode(final StringBuilder aSb, final int aBufSize,
                                                      final String aNodeType) {
    StringBuilder sb = aSb;
    if (sb == null) {
      sb = new StringBuilder(aBufSize);
    }
    if (javaDocComments) {
      sb.append("  /**").append(LS);
      sb.append("   * Visits a {@link ").append(aNodeType)
        .append("} node, passing it an argument.").append(LS);
      sb.append("   *").append(LS);
      sb.append("   * @param n - the node to visit").append(LS);
      sb.append("   * @param argu - the user argument").append(LS);
      sb.append("   * @return the user return information").append(LS);
      sb.append("   */").append(LS);
    }
    sb.append("  public ").append(genRetType).append(" visit(final ").append(aNodeType)
      .append(" n, final ").append(varargs ? genArgusType : genArguType).append(" argu);")
      .append(LS);
    return sb;
  }

  /**
   * Generates the visit method declaration on a {@link NodeChoice} for a visitor with user Return
   * and Argument data.
   * 
   * @param aSb - a buffer to print into (will be allocated if null)
   * @return the generated visit method
   */
  static StringBuilder genRetArguVisitNodeChoice(final StringBuilder aSb) {
    return genRetArguVisitAnyNode(aSb, 280, nodeChoice);
  }

  /**
   * Generates the visit method declaration on a {@link NodeList} for a visitor with user Return and
   * Argument data.
   * 
   * @param aSb - a buffer to print into (will be allocated if null)
   * @return the generated visit method
   */
  static StringBuilder genRetArguVisitNodeList(final StringBuilder aSb) {
    return genRetArguVisitAnyNode(aSb, 280, nodeList);
  }

  /**
   * Generates the visit method declaration on a {@link NodeListOptional} for a visitor with user
   * Return and Argument data.
   * 
   * @param aSb - a buffer to print into (will be allocated if null)
   * @return the generated visit method
   */
  static StringBuilder genRetArguVisitNodeListOpt(final StringBuilder aSb) {
    return genRetArguVisitAnyNode(aSb, 280, nodeListOpt);
  }

  /**
   * Generates the visit method declaration on a {@link NodeOptional} for a visitor with user Return
   * and Argument data.
   * 
   * @param aSb - a buffer to print into (will be allocated if null)
   * @return the generated visit method
   */
  static StringBuilder genRetArguVisitNodeOpt(final StringBuilder aSb) {
    return genRetArguVisitAnyNode(aSb, 280, nodeOpt);
  }

  /**
   * Generates the visit method declaration on a {@link NodeSequence} for a visitor with user Return
   * and Argument data.
   * 
   * @param aSb - a buffer to print into (will be allocated if null)
   * @return the generated visit method
   */
  static StringBuilder genRetArguVisitNodeSeq(final StringBuilder aSb) {
    return genRetArguVisitAnyNode(aSb, 280, nodeSeq);
  }

  /**
   * Generates the visit method declaration on a {@link NodeTCF} for a visitor with user Return and
   * Argument data.
   * 
   * @param aSb - a buffer to print into (will be allocated if null)
   * @return the generated visit method
   */
  static StringBuilder genRetArguVisitNodeTCF(final StringBuilder aSb) {
    return genRetArguVisitAnyNode(aSb, 280, nodeTCF);
  }

  /**
   * Generates the visit method declaration on a {@link NodeToken} for a visitor with user Return
   * and Argument data.
   * 
   * @param aSb - a buffer to print into (will be allocated if null)
   * @return the generated visit method
   */
  static StringBuilder genRetArguVisitNodeToken(final StringBuilder aSb) {
    return genRetArguVisitAnyNode(aSb, 280, nodeToken);
  }

  /*
   * "Ret" visit methods
   */

  /**
   * Generates the visit method declaration on a given node type for a visitor with user Return and
   * no Argument data.
   * 
   * @param aSb - a buffer to print into (will be allocated if null)
   * @param aBufSize - the initial buffer size
   * @param aNodeType - the node type
   * @return the generated visit method
   */
  private static StringBuilder genRetVisitAnyNode(final StringBuilder aSb, final int aBufSize,
                                                  final String aNodeType) {
    StringBuilder sb = aSb;
    if (sb == null) {
      sb = new StringBuilder(aBufSize);
    }
    if (javaDocComments) {
      sb.append("  /**").append(LS);
      sb.append("   * Visits a {@link ").append(aNodeType).append("} node.").append(LS);
      sb.append("   *").append(LS);
      sb.append("   * @param n - the node to visit").append(LS);
      sb.append("   * @return the user return information").append(LS);
      sb.append("   */").append(LS);
    }
    sb.append("  public ").append(genRetType).append(" visit(final ").append(aNodeType)
      .append(" n);").append(LS);
    return sb;
  }

  /**
   * Generates the visit method declaration on a {@link NodeChoice} for a visitor with user Return
   * and no Argument data.
   * 
   * @param aSb - a buffer to print into (will be allocated if null)
   * @return the generated visit method
   */
  static StringBuilder genRetVisitNodeChoice(final StringBuilder aSb) {
    return genRetVisitAnyNode(aSb, 250, nodeChoice);
  }

  /**
   * Generates the visit method declaration on a {@link NodeList} for a visitor with user Return and
   * no Argument data.
   * 
   * @param aSb - a buffer to print into (will be allocated if null)
   * @return the generated visit method
   */
  static StringBuilder genRetVisitNodeList(final StringBuilder aSb) {
    return genRetVisitAnyNode(aSb, 250, nodeList);
  }

  /**
   * Generates the visit method declaration on a {@link NodeListOptional} for a visitor with user
   * Return and no Argument data.
   * 
   * @param aSb - a buffer to print into (will be allocated if null)
   * @return the generated visit method
   */
  static StringBuilder genRetVisitNodeListOpt(final StringBuilder aSb) {
    return genRetVisitAnyNode(aSb, 250, nodeListOpt);
  }

  /**
   * Generates the visit method declaration on a {@link NodeOptional} for a visitor with user Return
   * and no Argument data.
   * 
   * @param aSb - a buffer to print into (will be allocated if null)
   * @return the generated visit method
   */
  static StringBuilder genRetVisitNodeOpt(final StringBuilder aSb) {
    return genRetVisitAnyNode(aSb, 250, nodeOpt);
  }

  /**
   * Generates the visit method declaration on a {@link NodeSequence} for a visitor with user Return
   * and no Argument data.
   * 
   * @param aSb - a buffer to print into (will be allocated if null)
   * @return the generated visit method
   */
  static StringBuilder genRetVisitNodeSeq(final StringBuilder aSb) {
    return genRetVisitAnyNode(aSb, 250, nodeSeq);
  }

  /**
   * Generates the visit method declaration on a {@link NodeTCF} for a visitor with user Return and
   * no Argument data.
   * 
   * @param aSb - a buffer to print into (will be allocated if null)
   * @return the generated visit method
   */
  static StringBuilder genRetVisitNodeTCF(final StringBuilder aSb) {
    return genRetVisitAnyNode(aSb, 250, nodeTCF);
  }

  /**
   * Generates the visit method declaration on a {@link NodeToken} for a visitor with user Return
   * and no Argument data.
   * 
   * @param aSb - a buffer to print into (will be allocated if null)
   * @return the generated visit method
   */
  static StringBuilder genRetVisitNodeToken(final StringBuilder aSb) {
    return genRetVisitAnyNode(aSb, 250, nodeToken);
  }

  /*
   * "VoidArgu" visit methods
   */

  /**
   * Generates the visit method declaration on a given node type for a visitor with user no Return
   * and Argument data.
   * 
   * @param aSb - a buffer to print into (will be allocated if null)
   * @param aBufSize - the initial buffer size
   * @param aNodeType - the node type
   * @return the generated visit method
   */
  private static StringBuilder genVoidArguVisitAnyNode(final StringBuilder aSb, final int aBufSize,
                                                       final String aNodeType) {
    StringBuilder sb = aSb;
    if (sb == null) {
      sb = new StringBuilder(aBufSize);
    }
    if (javaDocComments) {
      sb.append("  /**").append(LS);
      sb.append("   * Visits a {@link ").append(aNodeType)
        .append("} node, passing it an argument.").append(LS);
      sb.append("   *").append(LS);
      sb.append("   * @param n - the node to visit").append(LS);
      sb.append("   * @param argu - the user argument").append(LS);
      sb.append("   */").append(LS);
    }
    sb.append("  public void visit(final ").append(aNodeType).append(" n, final ")
      .append(varargs ? genArgusType : genArguType).append(" argu);").append(LS);
    return sb;
  }

  /**
   * Generates the visit method declaration on a {@link NodeChoice} for a visitor with user no
   * Return and Argument data.
   * 
   * @param aSb - a buffer to print into (will be allocated if null)
   * @return the generated visit method
   */
  static StringBuilder genVoidArguVisitNodeChoice(final StringBuilder aSb) {
    return genVoidArguVisitAnyNode(aSb, 260, nodeChoice);
  }

  /**
   * Generates the visit method declaration on a {@link NodeList} for a visitor with user no Return
   * and Argument data.
   * 
   * @param aSb - a buffer to print into (will be allocated if null)
   * @return the generated visit method
   */
  static StringBuilder genVoidArguVisitNodeList(final StringBuilder aSb) {
    return genVoidArguVisitAnyNode(aSb, 260, nodeList);
  }

  /**
   * Generates the visit method declaration on a {@link NodeListOptional} for a visitor with user no
   * Return and Argument data.
   * 
   * @param aSb - a buffer to print into (will be allocated if null)
   * @return the generated visit method
   */
  static StringBuilder genVoidArguVisitNodeListOpt(final StringBuilder aSb) {
    return genVoidArguVisitAnyNode(aSb, 260, nodeListOpt);
  }

  /**
   * Generates the visit method declaration on a {@link NodeOptional} for a visitor with user no
   * Return and Argument data.
   * 
   * @param aSb - a buffer to print into (will be allocated if null)
   * @return the generated visit method
   */
  static StringBuilder genVoidArguVisitNodeOpt(final StringBuilder aSb) {
    return genVoidArguVisitAnyNode(aSb, 260, nodeOpt);
  }

  /**
   * Generates the visit method declaration on a {@link NodeSequence} for a visitor with user no
   * Return and Argument data.
   * 
   * @param aSb - a buffer to print into (will be allocated if null)
   * @return the generated visit method
   */
  static StringBuilder genVoidArguVisitNodeSeq(final StringBuilder aSb) {
    return genVoidArguVisitAnyNode(aSb, 260, nodeSeq);
  }

  /**
   * Generates the visit method declaration on a {@link NodeTCF} for a visitor with user no Return
   * and Argument data.
   * 
   * @param aSb - a buffer to print into (will be allocated if null)
   * @return the generated visit method
   */
  static StringBuilder genVoidArguVisitNodeTCF(final StringBuilder aSb) {
    return genVoidArguVisitAnyNode(aSb, 260, nodeTCF);
  }

  /**
   * Generates the visit method declaration on a {@link NodeToken} for a visitor with user no Return
   * and Argument data.
   * 
   * @param aSb - a buffer to print into (will be allocated if null)
   * @return the generated visit method
   */
  static StringBuilder genVoidArguVisitNodeToken(final StringBuilder aSb) {
    return genVoidArguVisitAnyNode(aSb, 260, nodeToken);
  }

  /*
   * "Void" visit methods
   */

  /**
   * Generates the visit method declaration on a given node type for a visitor with user no Return
   * and no Argument data.
   * 
   * @param aSb - a buffer to print into (will be allocated if null)
   * @param aBufSize - the initial buffer size
   * @param aNodeType - the node type
   * @return the generated visit method
   */
  private static StringBuilder genVoidVisitAnyNode(final StringBuilder aSb, final int aBufSize,
                                                   final String aNodeType) {
    StringBuilder sb = aSb;
    if (sb == null) {
      sb = new StringBuilder(aBufSize);
    }
    if (javaDocComments) {
      sb.append("  /**").append(LS);
      sb.append("   * Visits a {@link ").append(aNodeType).append("} node.").append(LS);
      sb.append("   *").append(LS);
      sb.append("   * @param n - the node to visit").append(LS);
      sb.append("   */").append(LS);
    }
    sb.append("  public void visit(final ").append(aNodeType).append(" n);").append(LS);
    return sb;
  }

  /**
   * Generates the visit method declaration on a {@link NodeChoice} for a visitor with user no
   * Return and no Argument data.
   * 
   * @param aSb - a buffer to print into (will be allocated if null)
   * @return the generated visit method
   */
  static StringBuilder genVoidVisitNodeChoice(final StringBuilder aSb) {
    return genVoidVisitAnyNode(aSb, 230, nodeChoice);
  }

  /**
   * Generates the visit method declaration on a {@link NodeList} for a visitor with user no Return
   * and no Argument data.
   * 
   * @param aSb - a buffer to print into (will be allocated if null)
   * @return the generated visit method
   */
  static StringBuilder genVoidVisitNodeList(final StringBuilder aSb) {
    return genVoidVisitAnyNode(aSb, 230, nodeList);
  }

  /**
   * Generates the visit method declaration on a {@link NodeListOptional} for a visitor with user no
   * Return and no Argument data.
   * 
   * @param aSb - a buffer to print into (will be allocated if null)
   * @return the generated visit method
   */
  static StringBuilder genVoidVisitNodeListOpt(final StringBuilder aSb) {
    return genVoidVisitAnyNode(aSb, 230, nodeListOpt);
  }

  /**
   * Generates the visit method declaration on a {@link NodeOptional} for a visitor with user no
   * Return and no Argument data.
   * 
   * @param aSb - a buffer to print into (will be allocated if null)
   * @return the generated visit method
   */
  static StringBuilder genVoidVisitNodeOpt(final StringBuilder aSb) {
    return genVoidVisitAnyNode(aSb, 230, nodeOpt);
  }

  /**
   * Generates the visit method declaration on a {@link NodeSequence} for a visitor with user no
   * Return and no Argument data.
   * 
   * @param aSb - a buffer to print into (will be allocated if null)
   * @return the generated visit method
   */
  static StringBuilder genVoidVisitNodeSeq(final StringBuilder aSb) {
    return genVoidVisitAnyNode(aSb, 230, nodeSeq);
  }

  /**
   * Generates the visit method declaration on a {@link NodeTCF} for a visitor with user no Return
   * and no Argument data.
   * 
   * @param aSb - a buffer to print into (will be allocated if null)
   * @return the generated visit method
   */
  static StringBuilder genVoidVisitNodeTCF(final StringBuilder aSb) {
    return genVoidVisitAnyNode(aSb, 230, nodeTCF);
  }

  /**
   * Generates the visit method declaration on a {@link NodeToken} for a visitor with user no Return
   * and no Argument data.
   * 
   * @param aSb - a buffer to print into (will be allocated if null)
   * @return the generated visit method
   */
  static StringBuilder genVoidVisitNodeToken(final StringBuilder aSb) {
    return genVoidVisitAnyNode(aSb, 230, nodeToken);
  }

  /**
   * Generates package and visitor classes imports.
   * 
   * @param aSb - a buffer to print into (will be allocated if null)
   */
  static void packageAndImports(final StringBuilder aSb) {
    aSb.append("package ").append(nodesPackageName).append(';').append(LS).append(LS);
    aSb.append("import ").append(visitorsPackageName).append('.').append(iRetArguVisitor)
       .append(';').append(LS);
    aSb.append("import ").append(visitorsPackageName).append('.').append(iRetVisitor).append(';')
       .append(LS);
    aSb.append("import ").append(visitorsPackageName).append('.').append(iVoidArguVisitor)
       .append(';').append(LS);
    aSb.append("import ").append(visitorsPackageName).append('.').append(iVoidVisitor).append(';')
       .append(LS);
  }

  /**
   * Generates the serial version UID member.
   * 
   * @param aSb - a buffer to print into (will be allocated if null)
   */
  static void serialUIDDeclaration(final StringBuilder aSb) {
    if (javaDocComments) {
      aSb.append("  /** The serial version UID */").append(LS);
    }
    aSb.append("  private static final long serialVersionUID = ").append(SERIAL_UID).append("L;")
       .append(LS).append(LS);
  }

  /**
   * Generates the line separator member.
   * 
   * @param aSb - a buffer to print into (will be allocated if null)
   */
  static void lineSeparatorDeclaration(final StringBuilder aSb) {
    if (javaDocComments) {
      aSb.append("  /** The OS line separator */").append(LS);
    }
    aSb.append("  public static final String LS = System.getProperty(\"line.separator\");")
       .append(LS).append(LS);
  }

  /**
   * Generates parent pointer field.
   * 
   * @param aSb - a buffer to print into (will be allocated if null)
   * @return the generated declaration
   */
  static StringBuilder parentPointerDeclaration(final StringBuilder aSb) {
    StringBuilder sb = aSb;
    if (parentPointer) {
      if (sb == null) {
        sb = new StringBuilder(50);
      }
      if (javaDocComments) {
        aSb.append("  /** The parent node */").append(LS);
      }
      sb.append("  private ").append(iNode).append(" parent;").append(LS).append(LS);
    } else if (sb == null) {
      sb = new StringBuilder(0);
    }
    return sb;
  }

  /**
   * Generates parent pointer getter and setter methods.
   * 
   * @param aSb - a buffer to print into (will be allocated if null)
   * @return the generated methods
   */
  static StringBuilder parentPointerGetterSetter(final StringBuilder aSb) {
    StringBuilder sb = aSb;
    if (parentPointer) {
      if (sb == null) {
        sb = new StringBuilder(200);
      }
      if (javaDocComments) {
        sb.append("  /**").append(LS);
        sb.append("   * Sets the parent node.").append(LS);
        sb.append("   *").append(LS);
        sb.append("   * @param n - the parent node").append(LS);
        sb.append("   */").append(LS);
      }
      sb.append("  public void setParent(final ").append(iNode).append(" n) {").append(LS);
      sb.append("    parent = n;").append(LS);
      sb.append("  }").append(LS).append(LS);
      if (javaDocComments) {
        sb.append("  /**").append(LS);
        sb.append("   * Gets the parent node.").append(LS);
        sb.append("   *").append(LS);
        sb.append("   * @return the parent node").append(LS);
        sb.append("   */").append(LS);
      }
      sb.append("  public ").append(iNode).append(" getParent() {").append(LS);
      sb.append("    return parent;").append(LS);
      sb.append("  }").append(LS);
    } else if (sb == null) {
      sb = new StringBuilder(0);
    }
    return sb;
  }

  /**
   * Generates parent pointer set call.
   * 
   * @param aSb - a buffer to print into (must be non null)
   */
  static void parentPointerSetCall(final StringBuilder aSb) {
    if (parentPointer)
      aSb.append("    n.setParent(this);\n").append(LS);
  }

  /**
   * Generates the node list methods (for list nodes).
   * 
   * @param aSb - a buffer to print into (must be non null)
   */
  static void listMethods(final StringBuilder aSb) {
    if (javaDocComments) {
      aSb.append("  /**").append(LS);
      aSb.append("   * Gets the node in the list at a given position.").append(LS);
      aSb.append("   *").append(LS);
      aSb.append("   * @param i - the node's position").append(LS);
      aSb.append("   * @return the node").append(LS);
      aSb.append("   */").append(LS);
    }
    aSb.append("  @Override").append(LS);
    aSb.append("  public ").append(iNode).append(" elementAt(final int i) {").append(LS);
    aSb.append("    return nodes.get(i); }").append(LS).append(LS);

    if (javaDocComments) {
      aSb.append("  /**").append(LS);
      aSb.append("   * Returns an iterator on the nodes list.").append(LS);
      aSb.append("   *").append(LS);
      aSb.append("   * @return the iterator").append(LS);
      aSb.append("   */").append(LS);
    }
    aSb.append("  @Override").append(LS);
    aSb.append("  public Iterator<").append(iNode).append("> elements() {").append(LS);
    aSb.append("    return nodes.iterator(); }").append(LS).append(LS);

    if (javaDocComments) {
      aSb.append("  /**").append(LS);
      aSb.append("   * Returns the number of nodes in the list.").append(LS);
      aSb.append("   *").append(LS);
      aSb.append("   * @return the list size").append(LS);
      aSb.append("   */").append(LS);
    }
    aSb.append("  @Override").append(LS);
    aSb.append("  public int size() {").append(LS);
    aSb.append("    return nodes.size(); }").append(LS).append(LS);
  }

  /**
   * Generates the node interfaces accept methods.
   * 
   * @param aSb - a buffer to print into (must be non null)
   * @param withOverride - true if printing the override annotation, false otherwise
   */
  static void interfacesAcceptMethods(final StringBuilder aSb, final boolean withOverride) {
    if (javaDocComments) {
      genAcceptRetArguComment(aSb);
    }
    if (withOverride)
      aSb.append("  @Override").append(LS);
    aSb.append("  public ").append(parRetArgu).append(' ').append(begRetArgu)
       .append(iRetArguVisitor).append(parRetArgu).append(endRetArgu).append(';').append(LS)
       .append(LS);

    if (javaDocComments) {
      genAcceptRetComment(aSb);
    }
    if (withOverride)
      aSb.append("  @Override").append(LS);
    aSb.append("  public ").append(parRet).append(' ').append(begRet).append(iRetVisitor)
       .append(parRet).append(endRet).append(';').append(LS).append(LS);

    if (javaDocComments) {
      genAcceptVoidArguComment(aSb);
    }
    if (withOverride)
      aSb.append("  @Override").append(LS);
    aSb.append("  public ").append(parVoidArgu).append(' ').append(begVoidArgu)
       .append(iVoidArguVisitor).append(parVoidArgu).append(endVoidArgu).append(';').append(LS)
       .append(LS);

    if (javaDocComments) {
      genAcceptVoidComment(aSb);
    }
    if (withOverride)
      aSb.append("  @Override").append(LS);
    aSb.append("  public ").append(parVoid).append(begVoid).append(iVoidVisitor).append(parVoid)
       .append(endVoid).append(';').append(LS).append(LS);
  }

  /**
   * Generates the javadoc comment for a method with user Return and Argument data.
   * 
   * @param aSb - a buffer to print into (must be non null)
   */
  static void genAcceptRetArguComment(final StringBuilder aSb) {
    aSb.append("  /**").append(LS);
    aSb.append("   * Accepts a {@link ").append(iRetArguVisitor)
       .append("} visitor with user Return and Argument data.").append(LS);
    aSb.append("   *").append(LS);
    aSb.append("   * @param <R> - the user Return type").append(LS);
    aSb.append("   * @param <A> - the user Argument type").append(LS);
    aSb.append("   * @param vis - the visitor").append(LS);
    aSb.append("   * @param argu - the user Argument data").append(LS);
    aSb.append("   * @return the user Return data").append(LS);
    aSb.append("   */").append(LS);
  }

  /**
   * Generates the javadoc comment for a method with Return data.
   * 
   * @param aSb - a buffer to print into (must be non null)
   */
  static void genAcceptRetComment(final StringBuilder aSb) {
    aSb.append("  /**").append(LS);
    aSb.append("   * Accepts a {@link ").append(iRetVisitor)
       .append("} visitor with user Return data.").append(LS);
    aSb.append("   *").append(LS);
    aSb.append("   * @param <R> - the user Return type").append(LS);
    aSb.append("   * @param vis - the visitor").append(LS);
    aSb.append("   * @return the user Return data").append(LS);
    aSb.append("   */").append(LS);
  }

  /**
   * Generates the javadoc comment for a method with user Argument data.
   * 
   * @param aSb - a buffer to print into (must be non null)
   */
  static void genAcceptVoidArguComment(final StringBuilder aSb) {
    aSb.append("  /**").append(LS);
    aSb.append("   * Accepts a {@link ").append(iVoidArguVisitor)
       .append("} visitor with user Argument data.").append(LS);
    aSb.append("   *").append(LS);
    aSb.append("   * @param <A> - the user Argument type").append(LS);
    aSb.append("   * @param vis - the visitor").append(LS);
    aSb.append("   * @param argu - the user Argument data").append(LS);
    aSb.append("   */").append(LS);
  }

  /**
   * Generates the javadoc comment for a method with no user Return nor Argument data.
   * 
   * @param aSb - a buffer to print into (must be non null)
   */
  static void genAcceptVoidComment(final StringBuilder aSb) {
    aSb.append("  /**").append(LS);
    aSb.append("   * Accepts a {@link ").append(iVoidVisitor)
       .append("} visitor with no user Return nor Argument data.").append(LS);
    aSb.append("   *").append(LS);
    aSb.append("   * @param vis - the visitor").append(LS);
    aSb.append("   */").append(LS);
  }

  /**
   * Generates the node classes accept methods.
   * 
   * @param aSb - a buffer to print into (must be non null)
   */
  static void classesAcceptMethods(final StringBuilder aSb) {
    if (javaDocComments) {
      genAcceptRetArguComment(aSb);
    }
    aSb.append("  @Override").append(LS);
    aSb.append("  public ").append(parRetArgu).append(' ').append(begRetArgu)
       .append(iRetArguVisitor).append(parRetArgu).append(endRetArgu).append(" {").append(LS);
    aSb.append("    return vis.visit(this, argu);").append(LS);
    aSb.append("  }").append(LS).append(LS);

    if (javaDocComments) {
      genAcceptRetComment(aSb);
    }
    aSb.append("  @Override").append(LS);
    aSb.append("  public ").append(parRet).append(' ').append(begRet).append(iRetVisitor)
       .append(parRet).append(endRet).append(" {").append(LS);
    aSb.append("    return vis.visit(this);").append(LS);
    aSb.append("  }").append(LS).append(LS);

    if (javaDocComments) {
      genAcceptVoidArguComment(aSb);
    }
    aSb.append("  @Override").append(LS);
    aSb.append("  public ").append(parVoidArgu).append(' ').append(begVoidArgu)
       .append(iVoidArguVisitor).append(parVoidArgu).append(endVoidArgu).append(" {").append(LS);
    aSb.append("    vis.visit(this, argu);").append(LS);
    aSb.append("  }").append(LS).append(LS);

    if (javaDocComments) {
      genAcceptVoidComment(aSb);
    }
    aSb.append("  @Override").append(LS);
    aSb.append("  public ").append(parVoid).append(begVoid).append(iVoidVisitor).append(parVoid)
       .append(endVoid).append(" {").append(LS);
    aSb.append("    vis.visit(this);").append(LS);
    aSb.append("  }").append(LS).append(LS);
  }

  /**
   * Generates the javadoc comment for a method with user Return and Argument data.
   * 
   * @param aSb - a buffer to print into (must be non null)
   */
  static void genVisitRetArguComment(final StringBuilder aSb) {
    aSb.append("  /**").append(LS);
    aSb.append("   * Visits a node with user Return and Argument data.").append(LS);
    aSb.append("   *").append(LS);
    aSb.append("   * @param n - the node to visit").append(LS);
    aSb.append("   * @param argu - the user Argument data").append(LS);
    aSb.append("   * @return the user Return data").append(LS);
    aSb.append("   */").append(LS);
  }

  /**
   * Generates the javadoc comment for a method with user Return data.
   * 
   * @param aSb - a buffer to print into (must be non null)
   */
  static void genVisitRetComment(final StringBuilder aSb) {
    aSb.append("  /**").append(LS);
    aSb.append("   * Visits a node with user Return data.").append(LS);
    aSb.append("   *").append(LS);
    aSb.append("   * @param n - the node to visit").append(LS);
    aSb.append("   * @return the user Return data").append(LS);
    aSb.append("   */").append(LS);
  }

  /**
   * Generates the javadoc comment for a method with user Argument data.
   * 
   * @param aSb - a buffer to print into (must be non null)
   */
  static void genVisitVoidArguComment(final StringBuilder aSb) {
    aSb.append("  /**").append(LS);
    aSb.append("   * Visits a node with user Argument data.").append(LS);
    aSb.append("   *").append(LS);
    aSb.append("   * @param n - the node to visit").append(LS);
    aSb.append("   * @param argu - the user Argument data").append(LS);
    aSb.append("   */").append(LS);
  }

  /**
   * Generates the javadoc comment for a method with no user Return nor Argument data.
   * 
   * @param aSb - a buffer to print into (must be non null)
   */
  static void genVisitVoidComment(final StringBuilder aSb) {
    aSb.append("  /**").append(LS);
    aSb.append("   * Visits a node with a user no user Return nor Argument data.").append(LS);
    aSb.append("   *").append(LS);
    aSb.append("   * @param n - the node to visit").append(LS);
    aSb.append("   */").append(LS);
  }

  /*
   * Utility methods
   */

  /**
   * @return the extends clause.
   */
  static String extendsClause() {
    return (nodesSuperclass != null ? " extends " + nodesSuperclass : "");
  }

  /**
   * Generates the class parameter type(s).
   * 
   * @param ret - true if with a Return type, false if void
   * @param arg - true if with a user Argument, false otherwise
   * @return the class parameter(s) string
   */
  static String genClassParamType(final boolean ret, final boolean arg) {
    if (ret) {
      if (arg) {
        return "<".concat(genRetType).concat(", ").concat(genArguType).concat(">");
      } else {
        return "<".concat(genRetType).concat(">");
      }
    } else {
      if (arg) {
        return "<".concat(genArguType).concat(">");
      } else {
        return "";
      }
    }
  }

  /**
   * @param ret - true if with a Return type, false if void
   * @return the beginning of the argument(s) list
   */
  static String begArgList(final boolean ret) {
    if (ret) {
      return genRetType + " accept(final ";
    } else {
      return "void accept(final ";
    }
  }

  /**
   * @param arg - true if with a user Argument, false otherwise
   * @return the end of the argument(s) list
   */
  static String endArgList(final boolean arg) {
    if (arg) {
      return " vis, final ".concat(varargs ? genArgusType : genArguType).concat(" argu)");
    } else {
      return " vis)";
    }
  }
}

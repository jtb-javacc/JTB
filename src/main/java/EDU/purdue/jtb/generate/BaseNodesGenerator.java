/**
 * Copyright (c) 2004,2005 UCLO Compilers Group. All rights reserved. Redistribution and use in source and
 * binary forms, with or without modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this list of conditions and the
 * following disclaimer. Redistributions in binary form must reproduce the above copyright notice, this list
 * of conditions and the following disclaimer in the documentation and/or other materials provided with the
 * distribution. Neither UCLO nor the names of its contributors may be used to endorse or promote products
 * derived from this software without specific prior written permission. THIS SOFTWARE IS PROVIDED BY THE
 * COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OV IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR O PARTICULAV PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT OWNEV OV CONTRIBUTORS BE LIABLE FOV ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OV CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE
 * GOODS OV SERVICES; LOSS OF USE, DATA, OV PROFITS; OV BUSINESS INTERRUPTION) HOWEVEV CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHEV IN CONTRACT, STRICT LIABILITY, OV TORT (INCLUDING NEGLIGENCE OV OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 **/

/*
 * All files in the distribution of JTB, The Java Tree Builder are Copyright 1997, 1998, 1999 by the Purdue
 * Research Foundation of Purdue University. All rights reserved.
 *
 * Redistribution and use in source and binary forms are permitted provided that this entire copyright notice
 * is duplicated in all such copies, and that any documentation, announcements, and other materials related to
 * such distribution and use acknowledge that the software was developed at Purdue University, West Lafayette,
 * Indiana by Kevin Tao, Wanjun Wang and Jens Palsberg. No charge may be made for copies, derivations, or
 * distributions of this material without the express written consent of the copyright holder. Neither the
 * name of the University nor the name of the author may be used to endorse or promote products derived from
 * this material without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND WITHOUT ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, WITHOUT
 * LIMITATION, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOV ANY PARTICULAV PURPOSE.
 */

package EDU.purdue.jtb.generate;

import static EDU.purdue.jtb.common.Constants.FILE_EXISTS_RC;
import static EDU.purdue.jtb.common.Constants.LS;
import static EDU.purdue.jtb.common.Constants.OK_RC;
import static EDU.purdue.jtb.common.Constants.fileHeaderComment;
import static EDU.purdue.jtb.common.Constants.genNodeVar;
import static EDU.purdue.jtb.common.Constants.genVisVar;
import static EDU.purdue.jtb.common.Constants.iNode;
import static EDU.purdue.jtb.common.Constants.iNodeList;
import static EDU.purdue.jtb.common.Constants.jjToken;
import static EDU.purdue.jtb.common.Constants.jtbSigPfx;
import static EDU.purdue.jtb.common.Constants.jtbUserPfx;
import static EDU.purdue.jtb.common.Constants.nodeChoice;
import static EDU.purdue.jtb.common.Constants.nodeConstants;
import static EDU.purdue.jtb.common.Constants.nodeList;
import static EDU.purdue.jtb.common.Constants.nodeListOptional;
import static EDU.purdue.jtb.common.Constants.nodeOptional;
import static EDU.purdue.jtb.common.Constants.nodeSequence;
import static EDU.purdue.jtb.common.Constants.nodeToken;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import EDU.purdue.jtb.common.JTBOptions;
import EDU.purdue.jtb.common.Messages;
import EDU.purdue.jtb.common.UserClassInfo;
import EDU.purdue.jtb.common.VisitorInfo;
import EDU.purdue.jtb.parser.syntaxtree.INode;
import EDU.purdue.jtb.parser.syntaxtree.INodeList;
import EDU.purdue.jtb.parser.syntaxtree.NodeChoice;
import EDU.purdue.jtb.parser.syntaxtree.NodeConstants;
import EDU.purdue.jtb.parser.syntaxtree.NodeList;
import EDU.purdue.jtb.parser.syntaxtree.NodeListOptional;
import EDU.purdue.jtb.parser.syntaxtree.NodeOptional;
import EDU.purdue.jtb.parser.syntaxtree.NodeSequence;
import EDU.purdue.jtb.parser.syntaxtree.NodeToken;

/**
 * Class {@link BaseNodesGenerator} contains methods to generate the base nodes interfaces and classes.
 * CODEJAVA
 * <p>
 * Class maintains a state, and is not supposed to be run in parallel threads (on the same grammar). It does
 * not generate the files in parallel.
 * </p>
 *
 * @author Marc Mazas
 * @version 1.4.0 : 05-08/2009 : MMa : adapted to JavaCC v4.2 grammar and JDK 1.5
 * @version 1.4.6 : 01/2011 : FA : added -va and -npfx and -nsfx options
 * @version 1.4.7 : 12/2011 : MMa : fixed extendsClause() in genNodeTCFClass<br>
 *          1.4.7 : 09/2012 : MMa : extracted constants, added missing visit methods (NodeChoice and NodeTCF)
 * @version 1.4.8 : 10/2014 : MMa : minor fix<br>
 *          1.4.8 : 12/2014 : MMa : added printing override annotations ;<br>
 *          improved specials printing in {@link NodeToken}
 * @version 1.4.9 : 12/2014 : MMa : fixed generated code NodeToken.withSpecials
 * @version 1.5.0 : 01-06/2017 : MMa : added NodeConstants generation ; removed unused methods, renamed some
 *          and passed some to private ; updated {@link #genNodeChoiceClass} ; added jopt.noVisitors
 *          conditions ; removed buffer allocations and withOverride flags in sub methods ; added children
 *          methods generation ; changed generation of some iterator based for loops to enhanced for loops ;
 *          fixed generated NodeToken withSpecials ; enhanced to VisitorInfo based visitor generation ;
 *          renamed from BaseClasses ; subject to global packages and classes refactoring ; moved to non
 *          static ; fixed some issues with specials<br>
 *          1.5.0 : 10/2020-04/2021 : MMa : removed nodeTCF (managed as an {@link INodeList} (close to a
 *          {@link NodeSequence})) ; removed NodeTCF related code
 * @version 1.5.1 : 07/2023 : MMa : changed no overwrite management
 */
public class BaseNodesGenerator {
  
  /** The global JTB options */
  private final JTBOptions          jopt;
  /** The messages handler */
  final Messages                    mess;
  /** The {@link CommonCodeGenerator} */
  private final CommonCodeGenerator ccg;
  
  /**
   * Constructor.
   *
   * @param aJopt - the JTB options
   * @param aCcg - the {@link CommonCodeGenerator}
   * @param aMess - the messages handler
   */
  public BaseNodesGenerator(final JTBOptions aJopt, final CommonCodeGenerator aCcg, final Messages aMess) {
    jopt = aJopt;
    ccg = aCcg;
    mess = aMess;
  }
  
  /**
   * Generates the base nodes source files.
   *
   * @param aClasses - the list of {@link UserClassInfo} classes instances
   * @return OK_RC or FILE_EXISTS_RC
   * @throws IOException if IO problem
   */
  public int genBaseNodesFiles(final List<UserClassInfo> aClasses) throws IOException {
    boolean b = true;
    b &= fillFile(iNode + ".java", genINodeInterface(null));
    b &= fillFile(iNodeList + ".java", genINodeListInterface(null));
    b &= fillFile(nodeChoice + ".java", genNodeChoiceClass(null));
    b &= fillFile(nodeList + ".java", genNodeListClass(null));
    b &= fillFile(nodeListOptional + ".java", genNodeListOptionalClass(null));
    b &= fillFile(nodeOptional + ".java", genNodeOptionalClass(null));
    b &= fillFile(nodeSequence + ".java", genNodeSequenceClass(null));
    b &= fillFile(nodeToken + ".java", genNodeTokenClass(null));
    b &= fillFile(nodeConstants + ".java", genNodeConstantsClass(null, aClasses));
    if (jopt.noOverwrite && !b) {
      return FILE_EXISTS_RC;
    }
    return OK_RC;
  }
  
  /**
   * Fills a class file given its class source. It adds the file header comment ("Generated by JTB version").
   *
   * @param aFileName - the class file name
   * @param aClassSource - the buffer containing the class source
   * @throws IOException - if any IO Exception
   * @return false if the file exists and the no overwrite flag is set, true otherwise
   */
  private boolean fillFile(final String aFileName, final StringBuilder aClassSource) throws IOException {
    final File file = new File(jopt.nodesDirName, aFileName);
    if (jopt.noOverwrite && file.exists()) {
      mess.warning(
          "File " + file.getPath() + " exists and is not overwritten as the no overwrite flag has been set");
      return false;
    }
    try (PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(file), 2048))) {
      pw.println(fileHeaderComment);
      pw.print(aClassSource);
      if (jopt.noOverwrite) {
        mess.info("File " + file.getPath() + " generated");
      }
      return true;
    } catch (final IOException e) {
      Messages.hardErr("IOException on " + file.getPath(), e);
      throw e;
    }
  }
  
  /*
   * Node interfaces methods
   */
  
  /**
   * Generates the {@link INode} interface.
   *
   * @param aSb - a buffer to append to (will be allocated if null)
   * @return the generated interface
   */
  private StringBuilder genINodeInterface(final StringBuilder aSb) {
    StringBuilder sb = aSb;
    if (sb == null) {
      sb = new StringBuilder(20 + 500 + (jopt.noVisitors ? 0 : 1150) + (jopt.childrenMethods ? 850 : 0)
          + (jopt.parentPointer ? 400 : 0)); // 2900 / 2500 / 1650 / 500
    }
    if (jopt.nodesPackageName != null)
      sb.append("package ").append(jopt.nodesPackageName).append(';').append(LS).append(LS);
    if (jopt.childrenMethods) {
      sb.append("import java.util.List;").append(LS);
    }
    ccg.interfaceVisitorsImports(sb);
    
    if (jopt.javaDocComments) {
      sb.append("/**").append(LS);
      sb.append(" * The interface which all syntax tree classes must implement.").append(LS);
      sb.append(" */").append(LS);
    } else {
      sb.append("@SuppressWarnings(\"javadoc\")").append(LS);
    }
    sb.append("public interface ").append(iNode).append(" extends java.io.Serializable {").append(LS)
        .append(LS);
    
    // needed for NodeToken
    lineSeparatorDeclaration(sb);
    
    interfaceAcceptMethods(sb);
    
    ccg.parentPointerGetterSetterDecl(sb);
    
    interfaceChildrenMethods(sb);
    
    sb.append('}').append(LS);
    
    return sb;
  }
  
  /**
   * Generates the {@link INodeList} interface.
   *
   * @param aSb - a buffer to append to (will be allocated if null)
   * @return the generated interface
   */
  private StringBuilder genINodeListInterface(final StringBuilder aSb) {
    StringBuilder sb = aSb;
    if (sb == null) {
      sb = new StringBuilder(20 + 670); // 670
    }
    if (jopt.nodesPackageName != null)
      sb.append("package ").append(jopt.nodesPackageName).append(';').append(LS).append(LS);
    
    if (jopt.javaDocComments) {
      sb.append("/**").append(LS);
      sb.append(" * The interface which {@link ").append(nodeList).append("}, {@link ")
          .append(nodeListOptional).append("} and {@link ").append(nodeSequence).append("} must implement.")
          .append(LS);
      sb.append(" */").append(LS);
    } else {
      sb.append("@SuppressWarnings(\"javadoc\")").append(LS);
    }
    sb.append("public interface ").append(iNodeList).append(" extends ").append(iNode).append(" {").append(LS)
        .append(LS);
    
    if (jopt.javaDocComments) {
      sb.append("  /**").append(LS);
      sb.append("   * Adds a node to the list.").append(LS);
      sb.append("   *").append(LS);
      sb.append("   * @param ").append(genNodeVar).append(" - the node to add").append(LS);
      sb.append("   */").append(LS);
    }
    sb.append("  public void addNode(final ").append(iNode).append(' ').append(genNodeVar).append(");")
        .append(LS).append(LS);
    
    if (jopt.javaDocComments) {
      sb.append("  /**").append(LS);
      sb.append("   * @param i - the element index").append(LS);
      sb.append("   * @return the element at the given index").append(LS);
      sb.append("   */").append(LS);
    }
    sb.append("  public ").append(iNode).append(" elementAt(int i);").append(LS).append(LS);
    
    if (jopt.javaDocComments) {
      sb.append("  /**").append(LS);
      sb.append("   * @return the iterator on the node list").append(LS);
      sb.append("   */").append(LS);
    }
    sb.append("  public java.util.Iterator<").append(iNode).append("> elements();").append(LS).append(LS);
    
    if (jopt.javaDocComments) {
      sb.append("  /**").append(LS);
      sb.append("   * @return the list size").append(LS);
      sb.append("   */").append(LS);
    }
    sb.append("  public int size();").append(LS).append(LS);
    
    sb.append('}').append(LS);
    
    return sb;
  }
  
  /*
   * Node classes methods
   */
  
  /**
   * Generates the {@link NodeChoice} class.
   *
   * @param aSb - a buffer to append to (will be allocated if null)
   * @return the generated class
   */
  private StringBuilder genNodeChoiceClass(final StringBuilder aSb) {
    StringBuilder sb = aSb;
    if (sb == null) {
      sb = new StringBuilder(20 + 1470 + (jopt.noVisitors ? 0 : 1330) + (jopt.childrenMethods ? 1800 : 0)
          + (jopt.parentPointer ? 580 : 0)); // 5180 / 4600 / 2800 / 1470
    }
    
    if (jopt.nodesPackageName != null)
      sb.append("package ").append(jopt.nodesPackageName).append(';').append(LS).append(LS);
    if (jopt.childrenMethods) {
      sb.append("import java.util.ArrayList;").append(LS);
      sb.append("import java.util.List;").append(LS);
    }
    ccg.interfaceVisitorsImports(sb);
    
    if (jopt.javaDocComments) {
      sb.append("/**").append(LS);
      sb.append(" * Represents a grammar choice (|), e.g. ' ( A | B ) '.<br>").append(LS);
      sb.append(" * The class stores the node and the \"which\" choice indicator (0, 1, ...).").append(LS);
      sb.append(" */").append(LS);
    } else {
      sb.append("@SuppressWarnings(\"javadoc\")").append(LS);
    }
    sb.append("public class ").append(nodeChoice)
        .append(jopt.nodesSuperclass != null ? " extends " + jopt.nodesSuperclass : "").append(" implements ")
        .append(iNode).append(" {").append(LS).append(LS);
    
    if (jopt.javaDocComments) {
      sb.append("  /** The real node */").append(LS);
    }
    sb.append("  public ").append(iNode).append(" choice;").append(LS).append(LS);
    
    if (jopt.javaDocComments) {
      sb.append("  /** The \"which\" choice indicator */").append(LS);
    }
    sb.append("  public int which;").append(LS).append(LS);
    
    if (jopt.javaDocComments) {
      sb.append("  /** The total number of choices */").append(LS);
    }
    sb.append("  public int total;").append(LS).append(LS);
    
    ccg.parentPointerDeclaration(sb);
    
    ccg.genSerialUIDDeclaration(sb);
    
    if (jopt.javaDocComments) {
      sb.append("  /**").append(LS);
      sb.append("   * Constructs the {@link NodeChoice} with a given node and non standard (-1) ")
          .append("which choice and total number of choices.").append(LS);
      sb.append("   *").append(LS);
      sb.append("   * @param node - the node").append(LS);
      sb.append("   */").append(LS);
    }
    sb.append("  public ").append(nodeChoice).append("(final ").append(iNode).append(" node) {").append(LS);
    sb.append("   this(node, -1, -1);").append(LS);
    sb.append("  }").append(LS).append(LS);
    
    if (jopt.javaDocComments) {
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
    if (jopt.parentPointer) {
      sb.append("    choice.setParent(this);").append(LS);
    }
    sb.append("  }").append(LS).append(LS);
    
    ccg.classesAcceptMethods(sb);
    
    ccg.parentPointerGetterSetterImpl(sb);
    
    nodeChoiceChildrenMethods(sb);
    
    sb.append('}').append(LS);
    
    return sb;
  }
  
  /**
   * Generates the {@link NodeList} class.
   *
   * @param aSb - a buffer to append to (will be allocated if null)
   * @return the generated class
   */
  private StringBuilder genNodeListClass(final StringBuilder aSb) {
    StringBuilder sb = aSb;
    if (sb == null) {
      sb = new StringBuilder(20 + 2770 + (jopt.noVisitors ? 0 : 1330) + (jopt.childrenMethods ? 2180 : 0)
          + (jopt.parentPointer ? 580 : 0)); // 6860 / 6280 / 4100 / 2770
    }
    if (jopt.nodesPackageName != null)
      sb.append("package ").append(jopt.nodesPackageName).append(';').append(LS).append(LS);
    sb.append("import java.util.ArrayList;").append(LS);
    sb.append("import java.util.Iterator;").append(LS);
    if (jopt.childrenMethods) {
      sb.append("import java.util.List;").append(LS);
    }
    ccg.interfaceVisitorsImports(sb);
    
    if (jopt.javaDocComments) {
      sb.append("/**").append(LS);
      sb.append(" * Represents a grammar list (+), e.g. ' ( A )+ '.<br>").append(LS);
      sb.append(" * The class stores the nodes list in an ArrayList.").append(LS);
      sb.append(" */").append(LS);
    } else {
      sb.append("@SuppressWarnings(\"javadoc\")").append(LS);
    }
    sb.append("public class ").append(nodeList)
        .append(jopt.nodesSuperclass != null ? " extends " + jopt.nodesSuperclass : "").append(" implements ")
        .append(iNodeList).append(" {").append(LS).append(LS);
    
    if (jopt.javaDocComments) {
      sb.append("  /** The list of nodes */").append(LS);
    }
    sb.append("  public ArrayList<").append(iNode).append("> nodes;").append(LS).append(LS);
    
    if (jopt.javaDocComments) {
      sb.append("  /** The allocation sizes table */").append(LS);
    }
    sb.append("  private  final int allocTb[] = {1, 2, 3, 4, 5, 10, 20, 50};").append(LS).append(LS);
    
    if (jopt.javaDocComments) {
      sb.append("  /** The allocation number */").append(LS);
    }
    sb.append("  private int allocNb = 0;").append(LS).append(LS);
    
    ccg.parentPointerDeclaration(sb);
    
    ccg.genSerialUIDDeclaration(sb);
    
    if (jopt.javaDocComments) {
      sb.append("  /**").append(LS);
      sb.append("   * Initializes an empty list of nodes with a default first allocation.").append(LS);
      sb.append("   */").append(LS);
    }
    sb.append("  public ").append(nodeList).append("() {").append(LS);
    sb.append("    nodes = new ArrayList<>(allocTb[allocNb]);").append(LS);
    sb.append("  }").append(LS).append(LS);
    
    if (jopt.javaDocComments) {
      sb.append("  /**").append(LS);
      sb.append("   * Initializes an empty list of nodes with a given allocation.").append(LS);
      sb.append("   *").append(LS);
      sb.append("   * @param sz - the list size").append(LS);
      sb.append("   */").append(LS);
    }
    sb.append("  public ").append(nodeList).append("(final int sz) {").append(LS);
    sb.append("    nodes = new ArrayList<>(sz);").append(LS);
    sb.append("  }").append(LS).append(LS);
    
    if (jopt.javaDocComments) {
      sb.append("  /**").append(LS);
      sb.append("   * Initializes an empty list of nodes with a default first allocation ")
          .append("and adds a first node.").append(LS);
      sb.append("   *").append(LS);
      sb.append("   * @param firstNode - the node to add").append(LS);
      sb.append("   */").append(LS);
    }
    sb.append("  public ").append(nodeList).append("(final ").append(iNode).append(" firstNode) {")
        .append(LS);
    sb.append("    nodes = new ArrayList<>(allocTb[allocNb]);").append(LS);
    sb.append("    addNode(firstNode);").append(LS);
    sb.append("  }").append(LS).append(LS);
    
    if (jopt.javaDocComments) {
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
    sb.append("    nodes = new ArrayList<>(sz);").append(LS);
    sb.append("    addNode(firstNode);").append(LS);
    sb.append("  }").append(LS).append(LS);
    
    if (jopt.javaDocComments) {
      sb.append("  /**").append(LS);
      sb.append("   * Adds a node to the list of nodes, managing progressive ")
          .append("allocation increments.").append(LS);
      sb.append("   *").append(LS);
      sb.append("   * @param ").append(genNodeVar).append(" - the node to add").append(LS);
      sb.append("   */").append(LS);
    }
    sb.append("  @Override").append(LS);
    sb.append("  public void addNode(final ").append(iNode).append(' ').append(genNodeVar).append(") {")
        .append(LS);
    sb.append("    if (++allocNb < allocTb.length)").append(LS);
    sb.append("      nodes.ensureCapacity(allocTb[allocNb]);").append(LS);
    sb.append("    else").append(LS);
    sb.append("      nodes.ensureCapacity((allocNb - allocTb.length + 2) * ")
        .append("allocTb[(allocTb.length - 1)]);").append(LS);
    sb.append("    nodes.add(").append(genNodeVar).append(");").append(LS);
    ccg.parentPointerSetCall(sb, genNodeVar);
    sb.append("  }").append(LS).append(LS);
    
    listMethods(sb);
    
    ccg.classesAcceptMethods(sb);
    
    ccg.parentPointerGetterSetterImpl(sb);
    
    nodeListOrSequenceOrListOptionalChildrenMethods(sb, false);
    
    sb.append('}').append(LS);
    
    return sb;
  }
  
  /**
   * Generates the {@link NodeListOptional} class.
   *
   * @param aSb - a buffer to append to (will be allocated if null)
   * @return the generated class
   */
  private StringBuilder genNodeListOptionalClass(final StringBuilder aSb) {
    StringBuilder sb = aSb;
    if (sb == null) {
      sb = new StringBuilder(20 + 2970 + (jopt.noVisitors ? 0 : 1330) + (jopt.childrenMethods ? 2180 : 0)
          + (jopt.parentPointer ? 570 : 0)); // 7050 / 6480 / 4300 / 2970
    }
    
    if (jopt.nodesPackageName != null)
      sb.append("package ").append(jopt.nodesPackageName).append(';').append(LS).append(LS);
    sb.append("import java.util.ArrayList;").append(LS);
    sb.append("import java.util.Iterator;").append(LS);
    if (jopt.childrenMethods) {
      sb.append("import java.util.List;").append(LS);
    }
    ccg.interfaceVisitorsImports(sb);
    
    if (jopt.javaDocComments) {
      sb.append("/**").append(LS);
      sb.append(" * Represents an optional grammar list (*), e.g. ' ( A )* '.<br>").append(LS);
      sb.append(" * The class stores the nodes list in an ArrayList.").append(LS);
      sb.append(" */").append(LS);
    } else {
      sb.append("@SuppressWarnings(\"javadoc\")").append(LS);
    }
    sb.append("public class ").append(nodeListOptional)
        .append(jopt.nodesSuperclass != null ? " extends " + jopt.nodesSuperclass : "").append(" implements ")
        .append(iNodeList).append(" {").append(LS).append(LS);
    
    if (jopt.javaDocComments) {
      sb.append("  /** The list of nodes */").append(LS);
    }
    sb.append("  public ArrayList<").append(iNode).append("> nodes;").append(LS).append(LS);
    
    if (jopt.javaDocComments) {
      sb.append("  /** The allocation sizes table */").append(LS);
    }
    sb.append("  private static final int allocTb[] = {0, 1, 2, 3, 4, 5, 10, 20, 50};").append(LS).append(LS);
    
    if (jopt.javaDocComments) {
      sb.append("  /** The allocation number */").append(LS);
    }
    sb.append("  private int allocNb = 0;").append(LS).append(LS);
    
    ccg.parentPointerDeclaration(sb);
    
    ccg.genSerialUIDDeclaration(sb);
    
    if (jopt.javaDocComments) {
      sb.append("  /**").append(LS);
      sb.append("   * Initializes an empty list of nodes with a default first allocation.").append(LS);
      sb.append("   */").append(LS);
    }
    sb.append("  public ").append(nodeListOptional).append("() {").append(LS);
    sb.append("    nodes = new ArrayList<>(allocTb[allocNb]);").append(LS);
    sb.append("  }").append(LS).append(LS);
    
    if (jopt.javaDocComments) {
      sb.append("  /**").append(LS);
      sb.append("   * Initializes an empty list of nodes with a given allocation.").append(LS);
      sb.append("   *").append(LS);
      sb.append("   * @param sz - the list size").append(LS);
      sb.append("   */").append(LS);
    }
    sb.append("  public ").append(nodeListOptional).append("(final int sz) {").append(LS);
    sb.append("    nodes = new ArrayList<>(sz);").append(LS);
    sb.append("  }").append(LS).append(LS);
    
    if (jopt.javaDocComments) {
      sb.append("  /**").append(LS);
      sb.append("   * Initializes an empty list of nodes with a default first allocation and ")
          .append("adds a first node.").append(LS);
      sb.append("   *").append(LS);
      sb.append("   * @param firstNode - the node to add").append(LS);
      sb.append("   */").append(LS);
    }
    sb.append("  public ").append(nodeListOptional).append("(final ").append(iNode).append(" firstNode) {")
        .append(LS);
    sb.append("    nodes = new ArrayList<>(allocTb[allocNb]);").append(LS);
    sb.append("    addNode(firstNode);").append(LS);
    sb.append("  }").append(LS).append(LS);
    
    if (jopt.javaDocComments) {
      sb.append("  /**").append(LS);
      sb.append("   * Initializes an empty list of nodes with a given allocation and ")
          .append("adds a first node.").append(LS);
      sb.append("   *").append(LS);
      sb.append("   * @param sz - the list size").append(LS);
      sb.append("   * @param firstNode - the node to add").append(LS);
      sb.append("   */").append(LS);
    }
    sb.append("  public ").append(nodeListOptional).append("(final int sz, final ").append(iNode)
        .append(" firstNode) {").append(LS);
    sb.append("    nodes = new ArrayList<>(sz);").append(LS);
    sb.append("    addNode(firstNode);").append(LS);
    sb.append("  }").append(LS).append(LS);
    
    if (jopt.javaDocComments) {
      sb.append("  /**").append(LS);
      sb.append("   * Adds a node to the list of nodes, managing progressive ")
          .append("allocation increments.").append(LS);
      sb.append("   *").append(LS);
      sb.append("   * @param ").append(genNodeVar).append(" - the node to add").append(LS);
      sb.append("   */").append(LS);
    }
    sb.append("  @Override").append(LS);
    sb.append("  public void addNode(final ").append(iNode).append(' ').append(genNodeVar).append(") {")
        .append(LS);
    sb.append("    if (++allocNb < allocTb.length)").append(LS);
    sb.append("      nodes.ensureCapacity(allocTb[allocNb]);").append(LS);
    sb.append("    else").append(LS);
    sb.append("      nodes.ensureCapacity((allocNb - allocTb.length + 2) * ")
        .append("allocTb[(allocTb.length - 1)]);").append(LS);
    sb.append("    nodes.add(").append(genNodeVar).append(");").append(LS);
    ccg.parentPointerSetCall(sb, genNodeVar);
    sb.append("  }").append(LS).append(LS);
    
    listMethods(sb);
    
    if (jopt.javaDocComments) {
      sb.append("  /**").append(LS);
      sb.append("   * @return true if there is at least one node, false otherwise").append(LS);
      sb.append("   */").append(LS);
    }
    sb.append("  public boolean present() {").append(LS);
    sb.append("    return (nodes.size() != 0); }").append(LS).append(LS);
    
    ccg.classesAcceptMethods(sb);
    
    ccg.parentPointerGetterSetterImpl(sb);
    final StringBuilder aSb1 = sb;
    
    nodeListOrSequenceOrListOptionalChildrenMethods(aSb1, true);
    
    sb.append('}').append(LS);
    
    return sb;
  }
  
  /**
   * Generates the {@link NodeOptional} class.
   *
   * @param aSb - a buffer to append to (will be allocated if null)
   * @return the generated class
   */
  private StringBuilder genNodeOptionalClass(final StringBuilder aSb) {
    StringBuilder sb = aSb;
    if (sb == null) {
      sb = new StringBuilder(20 + 1360 + (jopt.noVisitors ? 0 : 1330) + (jopt.childrenMethods ? 2170 : 0)
          + (jopt.parentPointer ? 530 : 0)); // 5430 / 4860 / 2690 / 1360
    }
    
    if (jopt.nodesPackageName != null)
      sb.append("package ").append(jopt.nodesPackageName).append(';').append(LS).append(LS);
    if (jopt.childrenMethods) {
      sb.append("import java.util.ArrayList;").append(LS);
      sb.append("import java.util.List;").append(LS);
    }
    ccg.interfaceVisitorsImports(sb);
    
    if (jopt.javaDocComments) {
      sb.append("/**").append(LS);
      sb.append(" * Represents a grammar optional node (? or []), e.g. ' ( A )? ' or ' [ A ] '.<br>")
          .append(LS);
      sb.append(" * The class stores the node.").append(LS);
      sb.append(" */").append(LS);
    } else {
      sb.append("@SuppressWarnings(\"javadoc\")").append(LS);
    }
    sb.append("public class ").append(nodeOptional)
        .append(jopt.nodesSuperclass != null ? " extends " + jopt.nodesSuperclass : "").append(" implements ")
        .append(iNode).append(" {").append(LS).append(LS);
    
    if (jopt.javaDocComments) {
      sb.append("  /** The node (if null there is no node) */").append(LS);
    }
    sb.append("  public ").append(iNode).append(" node;").append(LS).append(LS);
    
    ccg.parentPointerDeclaration(sb);
    
    ccg.genSerialUIDDeclaration(sb);
    
    if (jopt.javaDocComments) {
      sb.append("  /**").append(LS);
      sb.append("   * Initializes an empty {@link NodeOptional}.").append(LS);
      sb.append("   */").append(LS);
    }
    sb.append("  public ").append(nodeOptional).append("() {").append(LS);
    sb.append("    node = null;").append(LS);
    sb.append("  }").append(LS).append(LS);
    
    if (jopt.javaDocComments) {
      sb.append("  /**").append(LS);
      sb.append("   * Initializes a {@link NodeOptional} with a node.").append(LS);
      sb.append("   *").append(LS);
      sb.append("   * @param ").append(genNodeVar).append(" - the node").append(LS);
      sb.append("   */").append(LS);
    }
    sb.append("  public ").append(nodeOptional).append("(final ").append(iNode).append(' ').append(genNodeVar)
        .append(") {").append(LS);
    sb.append("    addNode(").append(genNodeVar).append(");").append(LS);
    sb.append("  }").append(LS).append(LS);
    
    if (jopt.javaDocComments) {
      sb.append("  /**").append(LS);
      sb.append("   * Adds a node to the {@link NodeOptional}.").append(LS);
      sb.append("   *").append(LS);
      sb.append("   * @param ").append(genNodeVar).append(" - the node to add").append(LS);
      sb.append("   */").append(LS);
    }
    sb.append("  public void addNode(final ").append(iNode).append(' ').append(genNodeVar).append(") {")
        .append(LS);
    sb.append("    if (node != null)").append(LS)
        .append("      throw new Error(\"Attempt to set optional node twice\"); //$NON-NLS-1$").append(LS);
    sb.append("    node = ").append(genNodeVar).append(";").append(LS);
    ccg.parentPointerSetCall(sb, genNodeVar);
    sb.append("  }").append(LS).append(LS);
    
    if (jopt.javaDocComments) {
      sb.append("  /**").append(LS);
      sb.append("   * @return true if the node exists, false otherwise").append(LS);
      sb.append("   */").append(LS);
    }
    sb.append("  public boolean present() {").append(LS);
    sb.append("    return (node != null); }").append(LS).append(LS);
    
    ccg.classesAcceptMethods(sb);
    
    ccg.parentPointerGetterSetterImpl(sb);
    
    nodeOptionalChildrenMethods(sb);
    
    sb.append('}').append(LS);
    
    return sb;
  }
  
  /**
   * Generates the {@link NodeSequence} class.
   *
   * @param aSb - a buffer to append to (will be allocated if null)
   * @return the generated class
   */
  private StringBuilder genNodeSequenceClass(final StringBuilder aSb) {
    StringBuilder sb = aSb;
    if (sb == null) {
      sb = new StringBuilder(20 + 2500 + (jopt.noVisitors ? 0 : 1330) + (jopt.childrenMethods ? 2180 : 0)
          + (jopt.parentPointer ? 570 : 0)); // 6580 / 6010 / 3830 / 2500
    }
    
    if (jopt.nodesPackageName != null)
      sb.append("package ").append(jopt.nodesPackageName).append(';').append(LS).append(LS);
    sb.append("import java.util.ArrayList;").append(LS);
    sb.append("import java.util.Iterator;").append(LS);
    if (jopt.childrenMethods) {
      sb.append("import java.util.List;").append(LS);
    }
    ccg.interfaceVisitorsImports(sb);
    
    if (jopt.javaDocComments) {
      sb.append("/**").append(LS);
      sb.append(" * Represents a sequence of nodes (x y z ...) nested ")
          .append("within a choice (|), list (+),").append(LS)
          .append(" * optional list (*), or optional node (? or []), e.g. ' ( A B )+ ' ")
          .append("or ' [ C D E ] '.<br>").append(LS);
      sb.append(" * The class stores the nodes list in an ArrayList.").append(LS);
      sb.append(" */").append(LS);
    } else {
      sb.append("@SuppressWarnings(\"javadoc\")").append(LS);
    }
    sb.append("public class ").append(nodeSequence)
        .append(jopt.nodesSuperclass != null ? " extends " + jopt.nodesSuperclass : "").append(" implements ")
        .append(iNodeList).append(" {").append(LS).append(LS);
    
    if (jopt.javaDocComments) {
      sb.append("  /** The list of nodes */").append(LS);
    }
    sb.append("  public ArrayList<").append(iNode).append("> nodes;").append(LS).append(LS);
    
    ccg.parentPointerDeclaration(sb);
    
    ccg.genSerialUIDDeclaration(sb);
    
    if (jopt.javaDocComments) {
      sb.append("  /**").append(LS);
      sb.append("   * Initializes an empty {@link NodeSequence} with a default allocation.").append(LS);
      sb.append("   */").append(LS);
    }
    sb.append("  public ").append(nodeSequence).append("() {").append(LS);
    sb.append("    nodes = new ArrayList<>();").append(LS);
    sb.append("  }").append(LS).append(LS);
    
    if (jopt.javaDocComments) {
      sb.append("  /**").append(LS);
      sb.append("   * Initializes an empty {@link NodeSequence} with a given allocation.").append(LS);
      sb.append("   *").append(LS);
      sb.append("   * @param sz - the list size").append(LS);
      sb.append("   */").append(LS);
    }
    sb.append("  public ").append(nodeSequence).append("(final int sz) {").append(LS);
    sb.append("    nodes = new ArrayList<>(sz);").append(LS);
    sb.append("  }").append(LS).append(LS);
    
    if (jopt.javaDocComments) {
      sb.append("  /**").append(LS);
      sb.append("   * Initializes an empty {@link NodeSequence} with a default allocation ")
          .append("and adds a first node.").append(LS);
      sb.append("   *").append(LS);
      sb.append("   * @param firstNode - the node to add").append(LS);
      sb.append("   */").append(LS);
    }
    sb.append("  public ").append(nodeSequence).append("(final ").append(iNode).append(" firstNode) {")
        .append(LS);
    sb.append("    nodes = new ArrayList<>();").append(LS);
    sb.append("    addNode(firstNode);").append(LS);
    sb.append("  }").append(LS).append(LS);
    
    if (jopt.javaDocComments) {
      sb.append("  /**").append(LS);
      sb.append("   * Initializes an empty {@link NodeSequence} with a given allocation ")
          .append("and adds a first node.").append(LS);
      sb.append("   *").append(LS);
      sb.append("   * @param sz - the list size").append(LS);
      sb.append("   * @param firstNode - the node to add").append(LS);
      sb.append("   */").append(LS);
    }
    sb.append("  public ").append(nodeSequence).append("(final int sz, final ").append(iNode)
        .append(" firstNode) {").append(LS);
    sb.append("    nodes = new ArrayList<>(sz);").append(LS);
    sb.append("    addNode(firstNode);").append(LS);
    sb.append("  }").append(LS).append(LS);
    
    if (jopt.javaDocComments) {
      sb.append("  /**").append(LS);
      sb.append("   * Adds a node to the {@link NodeSequence}.").append(LS);
      sb.append("   *").append(LS);
      sb.append("   * @param ").append(genNodeVar).append(" - the node to add").append(LS);
      sb.append("   */").append(LS);
    }
    sb.append("  @Override").append(LS);
    sb.append("  public void addNode(final ").append(iNode).append(' ').append(genNodeVar).append(") {")
        .append(LS);
    sb.append("    nodes.add(").append(genNodeVar).append(");").append(LS);
    ccg.parentPointerSetCall(sb, genNodeVar);
    sb.append("  }").append(LS).append(LS);
    
    listMethods(sb);
    
    ccg.classesAcceptMethods(sb);
    
    ccg.parentPointerGetterSetterImpl(sb);
    final StringBuilder aSb1 = sb;
    
    nodeListOrSequenceOrListOptionalChildrenMethods(aSb1, false);
    
    sb.append('}').append(LS);
    
    return sb;
  }
  
  /**
   * Generates the {@link NodeToken} class.
   *
   * @param aSb - a buffer to append to (will be allocated if null)
   * @return the generated class
   */
  private StringBuilder genNodeTokenClass(final StringBuilder aSb) {
    StringBuilder sb = aSb;
    if (sb == null) {
      sb = new StringBuilder(20 + 6310 + (jopt.noVisitors ? 0 : 1330) + (jopt.childrenMethods ? 1320 : 0)
          + (jopt.parentPointer ? 570 : 0)); // 9330 / 8960 / 7640 / 6310
    }
    
    if (jopt.nodesPackageName != null)
      sb.append("package ").append(jopt.nodesPackageName).append(';').append(LS).append(LS);
    sb.append("import java.util.ArrayList;").append(LS);
    sb.append("import java.util.Iterator;").append(LS);
    sb.append("import java.util.NoSuchElementException;").append(LS);
    sb.append("import java.util.List;").append(LS);
    ccg.interfaceVisitorsImports(sb);
    ccg.javaCCTokenImport(sb);
    
    /* NodeToken class declaration */
    
    if (jopt.javaDocComments) {
      sb.append("/**").append(LS);
      sb.append(" * Represents a single token in the grammar.<br>").append(LS);
      sb.append(" * If the \"-tk\" option is used, also contains a ArrayList of preceding ")
          .append("special tokens.<br>").append(LS);
      sb.append(" * The class stores the token image, kind and position information, ")
          .append("and the special tokens list.<br>").append(LS);
      sb.append(" */").append(LS);
    } else {
      sb.append("@SuppressWarnings(\"javadoc\")").append(LS);
    }
    sb.append("public class ").append(nodeToken).append(" extends ")
        .append(jopt.nodesSuperclass != null ? jopt.nodesSuperclass : jjToken).append(" implements ")
        .append(iNode).append(" {").append(LS).append(LS);
    
    /* NodeToken members declarations */
    
    if (jopt.javaDocComments) {
      sb.append("  /** The token image */").append(LS);
    }
    sb.append("  public String tokenImage;").append(LS).append(LS);
    
    if (jopt.javaDocComments) {
      sb.append(" /** The list of special tokens */").append(LS);
    }
    sb.append(" public List<").append(nodeToken).append("> specialTokens;").append(LS).append(LS);
    
    ccg.parentPointerDeclaration(sb);
    
    ccg.genSerialUIDDeclaration(sb);
    
    /* NodeToken constructors */
    
    if (jopt.javaDocComments) {
      sb.append("  /**").append(LS);
      sb.append("   * Initializes a {@link NodeToken} with a given kind.").append(LS);
      sb.append("   *").append(LS);
      sb.append("   * @param aKind - the token kind").append(LS);
      sb.append("   */").append(LS);
    }
    sb.append("  public ").append(nodeToken).append("(final int aKind) {").append(LS);
    sb.append("    this(aKind, null);").append(LS);
    sb.append("  }").append(LS).append(LS);
    
    if (jopt.javaDocComments) {
      sb.append("  /**").append(LS);
      sb.append("   * Initializes a {@link NodeToken} with a given kind and image.").append(LS);
      sb.append("   *").append(LS);
      sb.append("   * @param aKind - the token kind").append(LS);
      sb.append("   * @param aImage - the token image").append(LS);
      sb.append("   */").append(LS);
    }
    sb.append("  public ").append(nodeToken).append("(final int aKind, final String aImage) {").append(LS);
    sb.append("    kind = aKind;").append(LS);
    sb.append("    tokenImage = image = aImage;").append(LS);
    sb.append("  }").append(LS).append(LS);
    
    /* NodeToken toString() method */
    
    if (jopt.javaDocComments) {
      sb.append("  /**").append(LS);
      sb.append("   * @return the token image").append(LS);
      sb.append("   */").append(LS);
    }
    sb.append("  @Override").append(LS);
    sb.append("  public String toString() {").append(LS);
    sb.append("    return tokenImage;").append(LS);
    sb.append("  }").append(LS).append(LS);
    
    /* NodeToken special tokens method */
    
    if (jopt.javaDocComments) {
      sb.append("  /**").append(LS);
      sb.append("  * Gets the special token in the special tokens list at a given position.").append(LS);
      sb.append("  *").append(LS);
      sb.append("  * @param i - the special token's position").append(LS);
      sb.append("  * @return the special token").append(LS);
      sb.append("  */").append(LS);
    }
    sb.append("  public ").append(nodeToken).append(" getSpecialAt(final int i) {").append(LS);
    sb.append("    if (specialTokens == null)").append(LS);
    sb.append("      throw new NoSuchElementException(\"No specialTokens in token\"); //$NON-NLS-1$")
        .append(LS);
    sb.append("    return specialTokens.get(i);").append(LS);
    sb.append("  }").append(LS).append(LS);
    
    if (jopt.javaDocComments) {
      sb.append("  /**").append(LS);
      sb.append("  * @return the number of special tokens").append(LS);
      sb.append("  */").append(LS);
    }
    sb.append("  public int numSpecials() {").append(LS);
    sb.append("    if (specialTokens == null)").append(LS);
    sb.append("      return 0;").append(LS);
    sb.append("    return specialTokens.size();").append(LS);
    sb.append("  }").append(LS).append(LS);
    
    if (jopt.javaDocComments) {
      sb.append("  /**").append(LS);
      sb.append("  * Adds a special token to the special tokens list.").append(LS);
      sb.append("  *").append(LS);
      sb.append("  * @param s - the special token to add").append(LS);
      sb.append("  */").append(LS);
    }
    sb.append("  public void addSpecial(final ").append(nodeToken).append(" s) {").append(LS);
    sb.append("    if (specialTokens == null)").append(LS);
    sb.append("     specialTokens = new ArrayList<>();").append(LS);
    sb.append("    specialTokens.add(s);").append(LS);
    if (jopt.parentPointer) {
      sb.append("  s.setParent(this);").append(LS);
    }
    sb.append("  }").append(LS).append(LS);
    
    if (jopt.javaDocComments) {
      sb.append("  /**").append(LS);
      sb.append("  * Trims the special tokens list.").append(LS);
      sb.append("  */").append(LS);
    }
    sb.append("  public void trimSpecials() {").append(LS);
    sb.append("    if (specialTokens == null)").append(LS);
    sb.append("      return;").append(LS);
    sb.append("    ((ArrayList<NodeToken>) specialTokens).trimToSize();").append(LS);
    sb.append("  }").append(LS).append(LS);
    
    if (jopt.javaDocComments) {
      sb.append("  /**").append(LS);
      sb.append("  * Returns the string of the special tokens of the current {@link NodeToken},").append(LS);
      sb.append("  * taking in account a given indentation.").append(LS);
      sb.append("  * @param spc - the indentation").append(LS);
      sb.append("  * @return the string representing the special tokens list").append(LS);
      sb.append("  */").append(LS);
    }
    sb.append("  public String getSpecials(final String spc) {").append(LS);
    sb.append("    if (specialTokens == null)").append(LS);
    sb.append("      return \"\"; //$NON-NLS-1$").append(LS);
    sb.append("    int stLastLine = -1;").append(LS);
    sb.append("    final StringBuilder buf = new StringBuilder(64);").append(LS);
    sb.append("    boolean hasEol = false;").append(LS);
    sb.append("    for (final Iterator<NodeToken> e = specialTokens.iterator(); e.hasNext();) {").append(LS);
    sb.append("      final NodeToken st = e.next();").append(LS);
    sb.append("      final char c = st.tokenImage.charAt(st.tokenImage.length() - 1);").append(LS);
    sb.append("      hasEol = c == '\\n' || c == '\\r';").append(LS);
    sb.append("      if (stLastLine != -1)").append(LS);
    sb.append("        // not first line ").append(LS);
    sb.append("        if (stLastLine != st.beginLine) {").append(LS);
    sb.append("          // if not on the same line as the previous").append(LS);
    sb.append("          for (int i = stLastLine + 1; i < st.beginLine; i++)").append(LS);
    sb.append("            // keep blank lines").append(LS);
    sb.append("          buf.append(LS);").append(LS);
    sb.append("          buf.append(spc);").append(LS);
    sb.append("        } else").append(LS);
    sb.append("          // on the same line as the previous").append(LS);
    sb.append("          buf.append(' ');").append(LS);
    sb.append("      buf.append(st.tokenImage);").append(LS);
    sb.append("      if (!hasEol && e.hasNext())").append(LS);
    sb.append("        // not a single line comment and not the last one").append(LS);
    sb.append("        buf.append(LS);").append(LS);
    sb.append("      stLastLine = st.endLine;").append(LS);
    sb.append("    }").append(LS);
    sb.append("    // keep the same number of blank lines before the current non special").append(LS);
    sb.append("    for (int i = stLastLine + (hasEol ? 1 : 0); i < beginLine; i++) {").append(LS);
    sb.append("      buf.append(LS);").append(LS);
    sb.append("      if (i != beginLine - 1)").append(LS);
    sb.append("      buf.append(spc);").append(LS);
    sb.append("    }").append(LS);
    sb.append("    // indent if the current non special is not on the same line").append(LS);
    sb.append("    if (stLastLine != beginLine)").append(LS);
    sb.append("      buf.append(spc);").append(LS);
    sb.append("    return buf.toString();").append(LS);
    sb.append("  }").append(LS).append(LS);
    
    if (jopt.javaDocComments) {
      sb.append("  /**").append(LS);
      sb.append("  * Returns the string of the special tokens and the normal token of the current ")
          .append("{@link NodeToken},").append(LS);
      sb.append("  * taking in account a given indentation.").append(LS);
      sb.append("  *").append(LS);
      sb.append("  * @param spc - the indentation").append(LS);
      sb.append("  * @return the string representing the special tokens list and the token").append(LS);
      sb.append("  */").append(LS);
    }
    sb.append("  public String withSpecials(final String spc) {").append(LS);
    sb.append("    return withSpecials(spc, null);").append(LS);
    sb.append("  }").append(LS).append(LS);
    
    if (jopt.javaDocComments) {
      sb.append("  /**").append(LS);
      sb.append("  * Returns the string of the special tokens and the normal token of the current ")
          .append("{@link NodeToken},").append(LS);
      sb.append("  * taking in account a given indentation and a given assignment.").append(LS);
      sb.append("  *").append(LS);
      sb.append("  * @param spc - the indentation").append(LS);
      sb.append("  * @param var - the variable assignment to be inserted").append(LS);
      sb.append("  * @return the string representing the special tokens list and the token").append(LS);
      sb.append("  */").append(LS);
    }
    sb.append("  public String withSpecials(final String spc, final String var) {").append(LS);
    sb.append("    final String specials = getSpecials(spc);").append(LS);
    sb.append("    int len = specials.length() + 1;").append(LS);
    sb.append("    if (len == 1)").append(LS);
    sb.append("      return (var == null ? tokenImage : var + tokenImage);").append(LS);
    sb.append("    if (var != null)").append(LS);
    sb.append("      len += var.length();").append(LS);
    sb.append("    StringBuilder buf = new StringBuilder(len + tokenImage.length());").append(LS);
    sb.append("    buf.append(specials);").append(LS);
    sb.append("    // see if needed to add a space").append(LS);
    sb.append("    int stLastLine = -1;").append(LS);
    sb.append("    if (specialTokens != null)").append(LS);
    sb.append("    for (NodeToken e : specialTokens) {").append(LS);
    sb.append("      stLastLine = e.endLine;").append(LS);
    sb.append("    }").append(LS);
    sb.append("    if (stLastLine == beginLine)").append(LS);
    sb.append("      buf.append(' ');").append(LS);
    sb.append("    if (var != null)").append(LS);
    sb.append("      buf.append(var);").append(LS);
    sb.append("    buf.append(tokenImage);").append(LS);
    sb.append("    return buf.toString();").append(LS);
    sb.append("  }").append(LS).append(LS);
    
    ccg.classesAcceptMethods(sb);
    
    ccg.parentPointerGetterSetterImpl(sb);
    
    nodeTokenChildrenMethods(sb);
    
    // TODO voir si cette classe devrait exister
    /* NodeGTToken class declaration */
    // et dans ce cas variabiliser NodeGTToken in Constants
    //
    // if (ccg.gdbv.parserName.equals("JTBParser")) {
    //
    // if (jopt.javaDocComments) {
    // sb.append(" /**").append(LS);
    // sb.append(" * NodeToken corresponding to the Greater than Token").append(LS);
    // sb.append(" */").append(LS);
    // }
    // sb.append(" public static class NodeGTToken extends NodeToken {").append(LS);
    // sb.append(LS);
    // if (jopt.javaDocComments) {
    // sb.append(" /**").append(LS);
    // sb.append(
    // " * The version identifier for this Serializable class. Increment only if the <i>serialized</i>")
    // .append(LS);
    // sb.append(" * form of the class changes.").append(LS);
    // sb.append(" */").append(LS);
    // }
    // sb.append(" private static final long serialVersionUID = 1L;").append(LS);
    // sb.append(LS);
    //
    // if (jopt.javaDocComments) {
    // sb.append(" /**").append(LS);
    // sb.append(" * Initializes a {@link NodeGTToken} with a given string and ")
    // .append("no position information.").append(LS);
    // sb.append(" *").append(LS);
    // sb.append(" * @param s - the token string").append(LS);
    // sb.append(" */").append(LS);
    // }
    // sb.append(" public ").append("NodeGTToken").append("(final String s) {").append(LS);
    // sb.append(" this(s, -1, -1, -1, -1, -1);").append(LS);
    // sb.append(" }").append(LS).append(LS);
    //
    // if (jopt.javaDocComments) {
    // sb.append(" /**").append(LS);
    // sb.append(" * Initializes a {@link NodeGTToken} with a given string and ")
    // .append("position information.").append(LS);
    // sb.append(" *").append(LS);
    // sb.append(" * @param s - the token string").append(LS);
    // sb.append(" * @param kn - the token kind").append(LS);
    // sb.append(" * @param bl - the first line").append(LS);
    // sb.append(" * @param bc - the first column").append(LS);
    // sb.append(" * @param el - the last line").append(LS);
    // sb.append(" * @param ec - the last column").append(LS);
    // sb.append(" */").append(LS);
    // }
    // sb.append(" public ").append("NodeGTToken")
    // .append("(final String s, final int kn, final int bl, final int bc, ")
    // .append("final int el, final int ec) {").append(LS);
    // sb.append(" super(s, kn, bl, bc, el, ec);").append(LS);
    // sb.append(" }").append(LS).append(LS);
    //
    // sb.append(" int realKind = JTBParserConstants.GT;").append(LS);
    // sb.append(" }").append(LS);
    //
    // }
    
    sb.append('}').append(LS);
    
    return sb;
  }
  
  /**
   * Generates the {@link NodeConstants} class.
   *
   * @param aSb - a buffer to append to (will be allocated if null)
   * @param aClasses - the list of {@link UserClassInfo} reflecting the generated classes
   * @return the generated class
   */
  private StringBuilder genNodeConstantsClass(final StringBuilder aSb, final List<UserClassInfo> aClasses) {
    StringBuilder sb = aSb;
    final int nb = aClasses.size();
    if (sb == null) {
      sb = new StringBuilder(420 * nb);
    }
    
    if (jopt.nodesPackageName != null)
      sb.append("package ").append(jopt.nodesPackageName).append(';').append(LS).append(LS);
    
    ccg.javaCCTokenImport(sb);
    
    if (jopt.javaDocComments) {
      sb.append("/**").append(LS);
      sb.append(
          " * Provides constants reflecting the JTB user nodes and a factory method for creating Tokens.<br>")
          .append(LS);
      sb.append(" */").append(LS);
    } else {
      sb.append("@SuppressWarnings(\"javadoc\")").append(LS);
    }
    sb.append("public class ").append(nodeConstants).append(" {").append(LS).append(LS);
    
    if (jopt.javaDocComments) {
      sb.append("  /**").append(LS);
      sb.append("   * Factory method used by JavaCC to create a new Token object,"
          + " which will be a (JTB) subclassed NodeToken object.").append(LS);
      sb.append("   *").append(LS);
      sb.append("   * @param ofKind - the token kind").append(LS);
      sb.append("   * @param image - the token image").append(LS);
      sb.append("   *").append(LS);
      sb.append("   * @return a new Token (which is also a NodeToken)").append(LS);
      sb.append("   */").append(LS);
    }
    sb.append("  public static Token newToken(int ofKind, String image) {").append(LS);
    // sb.append(" switch(ofKind) }").append(LS);
    // sb.append(" default : return new NodeToken(ofKind, image);").append(LS);
    // sb.append(" }").append(LS);
    sb.append("    return new NodeToken(ofKind, image);").append(LS);
    sb.append("  }").append(LS).append(LS);
    
    if (jopt.javaDocComments) {
      sb.append("  /** The number of JTB user nodes */").append(LS);
    }
    sb.append("  public static final int NB_JTB_USER_NODES = ").append(nb).append(";").append(LS).append(LS);
    
    for (int i = 0; i < nb; i++) {
      final String cn = aClasses.get(i).fixedClassName;
      if (jopt.javaDocComments) {
        sb.append("  /** The ").append(cn).append(" JTB user node's index */").append(LS);
      }
      sb.append("  public static final int ").append(jtbUserPfx).append(cn.toUpperCase()).append(" = ")
          .append(i).append(";").append(LS).append(LS);
      if (jopt.javaDocComments) {
        sb.append("  /** The ").append(cn).append(" JTB control signature */").append(LS);
      }
      final UserClassInfo uci = aClasses.get(i);
      sb.append("  public static final int ").append(jtbSigPfx).append(cn.toUpperCase()).append(" = ")
          .append(uci.fieldsHashSig).append(";").append(LS).append(LS);
    }
    
    if (jopt.javaDocComments) {
      sb.append("  /** The JTB user nodes' array */").append(LS);
    }
    sb.append("  public static final String[] JTB_USER_NODE_NAME = new String[NB_JTB_USER_NODES];").append(LS)
        .append(LS);
    
    if (jopt.javaDocComments) {
      sb.append("  /** Initialize the JTB user nodes' array */").append(LS);
    }
    sb.append("  static {").append(LS);
    for (int i = 0; i < nb; i++) {
      sb.append("  JTB_USER_NODE_NAME[").append(i).append("] = \"").append(aClasses.get(i).fixedClassName)
          .append("\"; //$NON-NLS-1$").append(LS);
    }
    sb.append("  }").append(LS).append(LS);
    
    if (jopt.javaDocComments) {
      sb.append("  /** The JTB control signatures' array */").append(LS);
    }
    sb.append("  public static final int[] JTB_SIGNATURE = new int[NB_JTB_USER_NODES];").append(LS)
        .append(LS);
    
    if (jopt.javaDocComments) {
      sb.append("  /** Initialize the JTB control signatures' array */").append(LS);
    }
    sb.append("  static {").append(LS);
    for (int i = 0; i < nb; i++) {
      sb.append("  JTB_SIGNATURE[").append(i).append("] = ").append(jtbSigPfx)
          .append(aClasses.get(i).fixedClassName.toUpperCase()).append(";").append(LS);
    }
    sb.append("  }").append(LS).append(LS);
    
    sb.append('}').append(LS);
    
    return sb;
    
  }
  
  /**
   * Generates the line separator member.
   *
   * @param aSb - a buffer to append to (will be allocated if null)
   */
  private void lineSeparatorDeclaration(final StringBuilder aSb) {
    if (jopt.javaDocComments) {
      aSb.append("  /** The OS line separator */").append(LS);
    }
    aSb.append("  public static final String LS = System.getProperty(\"line.separator\"); //$NON-NLS-1$")
        .append(LS).append(LS);
  }
  
  /**
   * Generates the list methods (for list nodes, i.e. {@link NodeList}, {@link NodeListOptional} and
   * {@link NodeSequence}).
   *
   * @param aSb - a buffer to append to (must be non null)
   */
  private void listMethods(final StringBuilder aSb) {
    if (jopt.javaDocComments) {
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
    
    if (jopt.javaDocComments) {
      aSb.append("  /**").append(LS);
      aSb.append("   * Returns an iterator on the nodes list.").append(LS);
      aSb.append("   *").append(LS);
      aSb.append("   * @return the iterator").append(LS);
      aSb.append("   */").append(LS);
    }
    aSb.append("  @Override").append(LS);
    aSb.append("  public Iterator<").append(iNode).append("> elements() {").append(LS);
    aSb.append("    return nodes.iterator(); }").append(LS).append(LS);
    
    if (jopt.javaDocComments) {
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
   * Generates the visitors interfaces accept methods (declarations).
   *
   * @param aSb - a buffer to append to (must be non null)
   */
  private void interfaceAcceptMethods(final StringBuilder aSb) {
    if (!jopt.noVisitors) {
      CommonCodeGenerator.cmtHeaderAccept(aSb, jopt.visitorsStr);
      for (final VisitorInfo vi : jopt.visitorsList) {
        if (jopt.javaDocComments) {
          CommonCodeGenerator.acceptComment(aSb, vi);
        }
        aSb.append("  public ").append(vi.classTypeParameters)
            .append(vi.classTypeParameters.length() == 0 ? "" : " ").append(vi.retInfo.fullType)
            .append(" accept(final ").append(vi.interfaceName).append(vi.classTypeParameters).append(' ')
            .append(genVisVar).append(vi.userParameters).append(");").append(LS).append(LS);
      }
    }
  }
  
  /*
   * Utility methods
   */
  
  /**
   * Generates the interfaces children methods (declarations).
   *
   * @param aSb - a buffer to append to (must be non null)
   */
  private void interfaceChildrenMethods(final StringBuilder aSb) {
    if (jopt.childrenMethods) {
      CommonCodeGenerator.cmtHeaderChildrenMethods(aSb);
      
      if (jopt.javaDocComments) {
        CommonCodeGenerator.childrenAllComment(aSb, "number", null);
      }
      aSb.append("  public int getNbAllChildren();").append(LS).append(LS);
      
      if (jopt.javaDocComments) {
        CommonCodeGenerator.childrenBaseorUserComment(aSb, "number", "base", null);
      }
      aSb.append("  public int getNbBaseChildren();").append(LS).append(LS);
      
      if (jopt.javaDocComments) {
        CommonCodeGenerator.childrenBaseorUserComment(aSb, "number", "user", null);
      }
      aSb.append("  public int getNbUserChildren();").append(LS).append(LS);
      
      if (jopt.javaDocComments) {
        CommonCodeGenerator.childrenAllComment(aSb, "list", null);
      }
      aSb.append("  public List<INode> getAllChildren();").append(LS).append(LS);
      
      if (jopt.javaDocComments) {
        CommonCodeGenerator.childrenBaseorUserComment(aSb, "list", "base", null);
      }
      aSb.append("  public List<INode> getBaseChildren();").append(LS).append(LS);
      
      if (jopt.javaDocComments) {
        CommonCodeGenerator.childrenBaseorUserComment(aSb, "list", "user", null);
      }
      aSb.append("  public List<INode> getUserChildren();").append(LS).append(LS);
      
      if (jopt.javaDocComments) {
        CommonCodeGenerator.isBaseNodeComment(aSb, null);
      }
      aSb.append("  public boolean isBaseNode();").append(LS).append(LS);
    }
  }
  
  /**
   * Generates the children methods (implementations) for the {@link NodeChoice} class.
   *
   * @param aSb - a buffer to append to (must be non null)
   */
  private void nodeChoiceChildrenMethods(final StringBuilder aSb) {
    if (jopt.childrenMethods) {
      CommonCodeGenerator.cmtHeaderChildrenMethods(aSb);
      
      ccg.listAllChildren(aSb);
      ccg.listBaseChildren(aSb);
      ccg.listUserChildren(aSb);
      
      if (jopt.javaDocComments) {
        CommonCodeGenerator.childrenAllComment(aSb, "number", "(always 1)");
      }
      aSb.append("  @Override").append(LS);
      aSb.append("  public int getNbAllChildren() {").append(LS);
      aSb.append("    return 1;").append(LS);
      aSb.append("  }").append(LS).append(LS);
      
      if (jopt.javaDocComments) {
        CommonCodeGenerator.childrenBaseorUserComment(aSb, "number", "base", "(0 or 1)");
      }
      aSb.append("  @Override").append(LS);
      aSb.append("  public int getNbBaseChildren() {").append(LS);
      aSb.append("    return choice.isBaseNode() ? 1 : 0;").append(LS);
      aSb.append("  }").append(LS).append(LS);
      
      if (jopt.javaDocComments) {
        CommonCodeGenerator.childrenBaseorUserComment(aSb, "number", "user", "(0 or 1)");
      }
      aSb.append("  @Override").append(LS);
      aSb.append("  public int getNbUserChildren() {").append(LS);
      aSb.append("    return choice.isBaseNode() ? 0 : 1;").append(LS);
      aSb.append("  }").append(LS).append(LS);
      
      if (jopt.javaDocComments) {
        CommonCodeGenerator.childrenAllComment(aSb, "list", "(always one node)");
      }
      aSb.append("  @Override").append(LS);
      aSb.append("  public List<INode> getAllChildren() {").append(LS);
      aSb.append("    if (lac == null) {").append(LS);
      aSb.append("      lac = new ArrayList<>(1);").append(LS);
      aSb.append("      lac.add(choice);").append(LS);
      aSb.append("    }").append(LS);
      aSb.append("    return lac;").append(LS);
      aSb.append("  }").append(LS).append(LS);
      
      if (jopt.javaDocComments) {
        CommonCodeGenerator.childrenBaseorUserComment(aSb, "list", "base", "(empty or with 1 node)");
      }
      aSb.append("  @Override").append(LS);
      aSb.append("  public List<INode> getBaseChildren() {").append(LS);
      aSb.append("    if (lbc == null) {").append(LS);
      aSb.append("      if (choice.isBaseNode()) {").append(LS);
      aSb.append("        lbc = new ArrayList<>(1);").append(LS);
      aSb.append("        lbc.add(choice);").append(LS);
      aSb.append("      } else").append(LS);
      aSb.append("        lbc = new ArrayList<>(0);").append(LS);
      aSb.append("    }").append(LS);
      aSb.append("    return lbc;").append(LS);
      aSb.append("  }").append(LS).append(LS);
      
      if (jopt.javaDocComments) {
        CommonCodeGenerator.childrenBaseorUserComment(aSb, "list", "user", "(empty or with 1 node)");
      }
      aSb.append("  @Override").append(LS);
      aSb.append("  public List<INode> getUserChildren() {").append(LS);
      aSb.append("    if (luc == null) {").append(LS);
      aSb.append("      if (!choice.isBaseNode()) {").append(LS);
      aSb.append("        luc = new ArrayList<>(1);").append(LS);
      aSb.append("        luc.add(choice);").append(LS);
      aSb.append("      } else").append(LS);
      aSb.append("        luc = new ArrayList<>(0);").append(LS);
      aSb.append("    }").append(LS);
      aSb.append("    return luc;").append(LS);
      aSb.append("  }").append(LS).append(LS);
      
      ccg.trueBaseNode(aSb);
    }
  }
  
  /**
   * Generates the children methods (implementations) for the {@link NodeList} or {@link NodeSequence} or
   * {@link NodeListOptional} classes.
   *
   * @param aSb - a buffer to append to (must be non null)
   * @param aOpt - true if for a NodeListOptional node, false for a NodeList or NodeSequence node
   */
  private void nodeListOrSequenceOrListOptionalChildrenMethods(final StringBuilder aSb, final boolean aOpt) {
    if (jopt.childrenMethods) {
      CommonCodeGenerator.cmtHeaderChildrenMethods(aSb);
      
      ccg.listAllChildren(aSb);
      ccg.listBaseChildren(aSb);
      ccg.listUserChildren(aSb);
      ccg.nbBaseChildren(aSb);
      ccg.nbUserChildren(aSb);
      
      if (jopt.javaDocComments) {
        CommonCodeGenerator.childrenAllComment(aSb, "number", aOpt ? "(0..N)" : "(1..N)");
      }
      aSb.append("  @Override").append(LS);
      aSb.append("  public int getNbAllChildren() {").append(LS);
      aSb.append("    return nodes.size();").append(LS);
      aSb.append("  }").append(LS).append(LS);
      
      if (jopt.javaDocComments) {
        CommonCodeGenerator.childrenBaseorUserComment(aSb, "number", "base", "(0..N)");
      }
      aSb.append("  @Override").append(LS);
      aSb.append("  public int getNbBaseChildren() {").append(LS);
      aSb.append("    if (lbc == null)").append(LS);
      aSb.append("      getBaseChildren();").append(LS);
      aSb.append("    return nbLbc;").append(LS);
      aSb.append("  }").append(LS).append(LS);
      
      if (jopt.javaDocComments) {
        CommonCodeGenerator.childrenBaseorUserComment(aSb, "number", "user", "(0..N)");
      }
      aSb.append("  @Override").append(LS);
      aSb.append("  public int getNbUserChildren() {").append(LS);
      aSb.append("    if (luc == null)").append(LS);
      aSb.append("      getUserChildren();").append(LS);
      aSb.append("    return nbLuc;").append(LS);
      aSb.append("  }").append(LS).append(LS);
      
      if (jopt.javaDocComments) {
        CommonCodeGenerator.childrenAllComment(aSb, "list", "(1..N)");
      }
      aSb.append("  @Override").append(LS);
      aSb.append("  public List<INode> getAllChildren() {").append(LS);
      aSb.append("    if (lac == null) {").append(LS);
      aSb.append("      lac = new ArrayList<>(nodes.size());").append(LS);
      aSb.append("        lac.addAll(nodes);").append(LS);
      aSb.append("    }").append(LS);
      aSb.append("    return lac;").append(LS);
      aSb.append("  }").append(LS).append(LS);
      
      if (jopt.javaDocComments) {
        CommonCodeGenerator.childrenBaseorUserComment(aSb, "list", "base", "(0..N)");
      }
      aSb.append("  @Override").append(LS);
      aSb.append("  public List<INode> getBaseChildren() {").append(LS);
      aSb.append("    if (lbc == null) {").append(LS);
      aSb.append("      nbLbc = 0;").append(LS);
      aSb.append("      for (final INode node : nodes)").append(LS);
      aSb.append("        if (node.isBaseNode())").append(LS);
      aSb.append("          nbLbc++;").append(LS);
      aSb.append("      lbc = new ArrayList<>(nbLbc);").append(LS);
      aSb.append("      for (final INode node : nodes)").append(LS);
      aSb.append("        if (node.isBaseNode())").append(LS);
      aSb.append("          lbc.add(node);").append(LS);
      aSb.append("    }").append(LS);
      aSb.append("    return lbc;").append(LS);
      aSb.append("  }").append(LS).append(LS);
      
      if (jopt.javaDocComments) {
        CommonCodeGenerator.childrenBaseorUserComment(aSb, "list", "user", "(0..N)");
      }
      aSb.append("  @Override").append(LS);
      aSb.append("  public List<INode> getUserChildren() {").append(LS);
      aSb.append("    if (luc == null) {").append(LS);
      aSb.append("      nbLuc = 0;").append(LS);
      aSb.append("      for (final INode node : nodes)").append(LS);
      aSb.append("        if (!node.isBaseNode())").append(LS);
      aSb.append("          nbLuc++;").append(LS);
      aSb.append("      luc = new ArrayList<>(nbLuc);").append(LS);
      aSb.append("      for (final INode node : nodes)").append(LS);
      aSb.append("        if (!node.isBaseNode())").append(LS);
      aSb.append("          luc.add(node);").append(LS);
      aSb.append("    }").append(LS);
      aSb.append("    return luc;").append(LS);
      aSb.append("  }").append(LS).append(LS);
      
      ccg.trueBaseNode(aSb);
    }
  }
  
  /**
   * Generates the children methods (implementations) for the {@link NodeOptional} classes.
   *
   * @param aSb - a buffer to append to (must be non null)
   */
  private void nodeOptionalChildrenMethods(final StringBuilder aSb) {
    if (jopt.childrenMethods) {
      CommonCodeGenerator.cmtHeaderChildrenMethods(aSb);
      
      ccg.listAllChildren(aSb);
      ccg.listBaseChildren(aSb);
      ccg.listUserChildren(aSb);
      ccg.nbBaseChildren(aSb);
      ccg.nbUserChildren(aSb);
      
      if (jopt.javaDocComments) {
        CommonCodeGenerator.childrenAllComment(aSb, "number", "(0 or 1)");
      }
      aSb.append("  @Override").append(LS);
      aSb.append("  public int getNbAllChildren() {").append(LS);
      aSb.append("    return node == null ? 0 : 1;").append(LS);
      aSb.append("  }").append(LS).append(LS);
      
      if (jopt.javaDocComments) {
        CommonCodeGenerator.childrenBaseorUserComment(aSb, "number", "base", "(0 or 1)");
      }
      aSb.append("  @Override").append(LS);
      aSb.append("  public int getNbBaseChildren() {").append(LS);
      aSb.append("    if (lbc == null)").append(LS);
      aSb.append("      getBaseChildren();").append(LS);
      aSb.append("    return nbLbc;").append(LS);
      aSb.append("  }").append(LS).append(LS);
      
      if (jopt.javaDocComments) {
        CommonCodeGenerator.childrenBaseorUserComment(aSb, "number", "user", "(0 or 1)");
      }
      aSb.append("  @Override").append(LS);
      aSb.append("  public int getNbUserChildren() {").append(LS);
      aSb.append("    if (luc == null)").append(LS);
      aSb.append("      getUserChildren();").append(LS);
      aSb.append("    return nbLuc;").append(LS);
      aSb.append("  }").append(LS).append(LS);
      
      if (jopt.javaDocComments) {
        CommonCodeGenerator.childrenAllComment(aSb, "list", "(0 or 1)");
      }
      aSb.append("  @Override").append(LS);
      aSb.append("  public List<INode> getAllChildren() {").append(LS);
      aSb.append("    if (lac == null) {").append(LS);
      aSb.append("      if (node == null) {").append(LS);
      aSb.append("        lac = new ArrayList<>(0);").append(LS);
      aSb.append("      } else {").append(LS);
      aSb.append("        lac = new ArrayList<>(1);").append(LS);
      aSb.append("        lac.add(node);").append(LS);
      aSb.append("      }").append(LS);
      aSb.append("    }").append(LS);
      aSb.append("    return lac;").append(LS);
      aSb.append("  }").append(LS).append(LS);
      
      if (jopt.javaDocComments) {
        CommonCodeGenerator.childrenBaseorUserComment(aSb, "list", "base", "(0 or 1)");
      }
      aSb.append("  @Override").append(LS);
      aSb.append("  public List<INode> getBaseChildren() {").append(LS);
      aSb.append("    if (lbc == null) {").append(LS);
      aSb.append("      nbLbc = 0;").append(LS);
      aSb.append("      if (node != null && node.isBaseNode())").append(LS);
      aSb.append("        nbLbc++;").append(LS);
      aSb.append("      lbc = new ArrayList<>(nbLbc);").append(LS);
      aSb.append("      if (node != null && node.isBaseNode())").append(LS);
      aSb.append("        lbc.add(node);").append(LS);
      aSb.append("    }").append(LS);
      aSb.append("    return lbc;").append(LS);
      aSb.append("  }").append(LS).append(LS);
      
      if (jopt.javaDocComments) {
        CommonCodeGenerator.childrenBaseorUserComment(aSb, "list", "user", "(0 or 1)");
      }
      aSb.append("  @Override").append(LS);
      aSb.append("  public List<INode> getUserChildren() {").append(LS);
      aSb.append("    if (luc == null) {").append(LS);
      aSb.append("      nbLuc = 0;").append(LS);
      aSb.append("      if (node != null && !node.isBaseNode())").append(LS);
      aSb.append("        nbLuc++;").append(LS);
      aSb.append("      luc = new ArrayList<>(nbLuc);").append(LS);
      aSb.append("      if (node != null && !node.isBaseNode())").append(LS);
      aSb.append("        luc.add(node);").append(LS);
      aSb.append("    }").append(LS);
      aSb.append("    return luc;").append(LS);
      aSb.append("  }").append(LS).append(LS);
      
      ccg.trueBaseNode(aSb);
    }
  }
  
  /**
   * Generates the children methods (implementations) for the {@link NodeToken} classes.
   *
   * @param aSb - a buffer to append to (must be non null)
   */
  private void nodeTokenChildrenMethods(final StringBuilder aSb) {
    if (jopt.childrenMethods) {
      CommonCodeGenerator.cmtHeaderChildrenMethods(aSb);
      
      if (jopt.javaDocComments) {
        aSb.append("  /** An empty list */").append(LS);
      }
      aSb.append("  private final List<INode> emptyList = new ArrayList<>(0);").append(LS).append(LS);
      
      if (jopt.javaDocComments) {
        CommonCodeGenerator.childrenAllComment(aSb, "number", "(always 0)");
      }
      aSb.append("  @Override").append(LS);
      aSb.append("  public int getNbAllChildren() {").append(LS);
      aSb.append("    return 0;").append(LS);
      aSb.append("  }").append(LS).append(LS);
      
      if (jopt.javaDocComments) {
        CommonCodeGenerator.childrenBaseorUserComment(aSb, "number", "base", "(always 0)");
      }
      aSb.append("  @Override").append(LS);
      aSb.append("  public int getNbBaseChildren() {").append(LS);
      aSb.append("    return 0;").append(LS);
      aSb.append("  }").append(LS).append(LS);
      
      if (jopt.javaDocComments) {
        CommonCodeGenerator.childrenBaseorUserComment(aSb, "number", "user", "(always 0)");
      }
      aSb.append("  @Override").append(LS);
      aSb.append("  public int getNbUserChildren() {").append(LS);
      aSb.append("    return 0;").append(LS);
      aSb.append("  }").append(LS).append(LS);
      
      if (jopt.javaDocComments) {
        CommonCodeGenerator.childrenAllComment(aSb, "list", "(always empty)");
      }
      aSb.append("  @Override").append(LS);
      aSb.append("  public List<INode> getAllChildren() {").append(LS);
      aSb.append("    return emptyList;").append(LS);
      aSb.append("  }").append(LS).append(LS);
      
      if (jopt.javaDocComments) {
        CommonCodeGenerator.childrenBaseorUserComment(aSb, "list", "base", "(always empty)");
      }
      aSb.append("  @Override").append(LS);
      aSb.append("  public List<INode> getBaseChildren() {").append(LS);
      aSb.append("    return emptyList;").append(LS);
      aSb.append("  }").append(LS).append(LS);
      
      if (jopt.javaDocComments) {
        CommonCodeGenerator.childrenBaseorUserComment(aSb, "list", "user", "(always empty)");
      }
      aSb.append("  @Override").append(LS);
      aSb.append("  public List<INode> getUserChildren() {").append(LS);
      aSb.append("    return emptyList;").append(LS);
      aSb.append("  }").append(LS).append(LS);
      
      ccg.trueBaseNode(aSb);
    }
  }
  
}

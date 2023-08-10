/*
 * Copyright (c) 2006, Sun Microsystems, Inc. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided
 * that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this list of conditions and the
 * following disclaimer. * Redistributions in binary form must reproduce the above copyright notice, this list
 * of conditions and the following disclaimer in the documentation and/or other materials provided with the
 * distribution. * Neither the name of the Sun Microsystems, Inc. nor the names of its contributors may be
 * used to endorse or promote products derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 * PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
// added to the original JJTree example
// enhanced from ex2jtb because of the children option
package examples.java.ex3jtb.syntaxtree;

import java.util.ArrayList;
import java.util.List;
import examples.java.ex3jtb.visitor.IGenVisitor;

/**
 * An node meant to overwrite (not extend!) a JTB generated node.
 */
public class ASTMyID implements INode {

  /** The parent node */
  private INode parent;

  /** The serial version UID */
  private static final long serialVersionUID = 151L;

  /** Added for customization example */
  private String name = "my name";

  /** Child node 0 */
  public NodeToken f0;

  /**
   * Constructs the node with its child node.
   *
   * @param n0 - the child node
   */
  public ASTMyID(final NodeToken n0) {
    f0 = n0;
  }

  /*
   * Visitors accept methods (no -novis option, visitors specification : Gen,void,java.lang.String)
   */
  /**
   * Accepts a {@link IGenVisitor} visitor with user argument data.
   *
   * @param vis - the visitor
   * @param argu - the user Argument data
   */
  @Override
  public void accept(final IGenVisitor vis, final String argu) {
    vis.visit(this, argu);
  }

  /**
   * @param n - the name
   */
  public void setName(final String n) {
    name = n;
  }

  /** {@inheritDoc} */
  @Override
  public String toString() {
    return "Identifier: " + name;
  }
  
  /*
   * Parent pointer getter and setter (-pp option)
   */

  /**
   * Gets the parent node.
   *
   * @return the parent node
   */
  @Override
  public INode getParent() {
    return parent;
  }

  /**
   * Sets the parent node.
   *
   * @param n - the parent node
   */
  @Override
  public void setParent(final INode n) {
    parent = n;
  }

  /*
   * Children methods (-chm option)
   */

  /** The list of all direct children (base + user nodes) */
  private List<INode> lac = null;

  /** The list of direct base nodes children */
  private List<INode> lbc = null;

  /** The list of direct user nodes children */
  private List<INode> luc = null;

  /**
   * @return true if the node is a base node, false otherwise (always false : the node is a user node)
   */
  @Override
  public boolean isBaseNode() {
    return false;
  }


  /**
   * @return the number of all direct children (base + user nodes) (always 1))
   */
  @Override
  public int getNbAllChildren() {
    return 1;
  }

  /**
   * @return the number of direct base nodes children (always 1))
   */
  @Override
  public int getNbBaseChildren() {
    return 1;
  }

  /**
   * @return the number of direct user nodes children (always 0))
   */
  @Override
  public int getNbUserChildren() {
    return 0;
  }

  /**
   * @return the list of all direct children (base + user nodes) (always 1 nodes))
   */
  @Override
  public List<INode> getAllChildren() {
    if (lac == null) {
      lac = new ArrayList<>(1);
      lac.add(f0);
    }
    return lac;
  }

  /**
   * @return the list of all direct children (base + user nodes) (always 1 nodes))
   */
  @Override
  public List<INode> getBaseChildren() {
    if (lbc == null) {
      lbc = new ArrayList<>(1);
      lbc.add(f0);
    }
    return lbc;
  }

  /**
   * @return the list of all direct children (base + user nodes) (always 0 nodes))
   */
  @Override
  public List<INode> getUserChildren() {
    if (luc == null) {
      luc = new ArrayList<>(0);
    }
    return luc;
  }

}

/* Generated by JTB 1.4.14 */
package EDU.purdue.jtb.syntaxtree;

import EDU.purdue.jtb.visitor.IRetArguVisitor;
import EDU.purdue.jtb.visitor.IRetVisitor;
import EDU.purdue.jtb.visitor.IVoidArguVisitor;
import EDU.purdue.jtb.visitor.IVoidVisitor;

import java.util.*;

/**
 * Represents a sequence of nodes (x y z ...) nested within a choice (|), list (+),
 * optional list (*), or optional node (? or []), e.g. ' ( A B )+ ' or ' [ C D E ] '.<br>
 * The class stores the nodes list in an ArrayList.
 */
public class NodeSequence implements INodeList {

  /** The list of nodes */
  public ArrayList<INode> nodes;

  /** The serial version UID */
  private static final long serialVersionUID = 1414L;

  /**
   * Initializes an empty {@link NodeSequence} with a default allocation.
   */
  public NodeSequence() {
    nodes = new ArrayList<INode>();
  }

  /**
   * Initializes an empty {@link NodeSequence} with a given allocation.
   *
   * @param sz - the list size
   */
  public NodeSequence(final int sz) {
    nodes = new ArrayList<INode>(sz);
  }

  /**
   * Initializes an empty {@link NodeSequence} with a default allocation and adds a first node.
   *
   * @param firstNode - the node to add
   */
  public NodeSequence(final INode firstNode) {
    nodes = new ArrayList<INode>();
    addNode(firstNode);
  }

  /**
   * Initializes an empty {@link NodeSequence} with a given allocation and adds a first node.
   *
   * @param sz - the list size
   * @param firstNode - the node to add
   */
  public NodeSequence(final int sz, final INode firstNode) {
    nodes = new ArrayList<INode>(sz);
    addNode(firstNode);
  }

  /**
   * Adds a node to the {@link NodeSequence}.
   *
   * @param n - the node to add
   */
  @Override
  public void addNode(final INode n) {
    nodes.add(n);
  }

  /**
   * Gets the node in the list at a given position.
   *
   * @param i - the node's position
   * @return the node
   */
  @Override
  public INode elementAt(final int i) {
    return nodes.get(i); }

  /**
   * Returns an iterator on the nodes list.
   *
   * @return the iterator
   */
  @Override
  public Iterator<INode> elements() {
    return nodes.iterator(); }

  /**
   * Returns the number of nodes in the list.
   *
   * @return the list size
   */
  @Override
  public int size() {
    return nodes.size(); }

  /**
   * Accepts a {@link IRetArguVisitor} visitor with user Return and Argument data.
   *
   * @param <R> - the user Return type
   * @param <A> - the user Argument type
   * @param vis - the visitor
   * @param argu - the user Argument data
   * @return the user Return data
   */
  @Override
  public <R, A> R accept(final IRetArguVisitor<R, A> vis, final A argu) {
    return vis.visit(this, argu);
  }

  /**
   * Accepts a {@link IRetVisitor} visitor with user Return data.
   *
   * @param <R> - the user Return type
   * @param vis - the visitor
   * @return the user Return data
   */
  @Override
  public <R> R accept(final IRetVisitor<R> vis) {
    return vis.visit(this);
  }

  /**
   * Accepts a {@link IVoidArguVisitor} visitor with user Argument data.
   *
   * @param <A> - the user Argument type
   * @param vis - the visitor
   * @param argu - the user Argument data
   */
  @Override
  public <A> void accept(final IVoidArguVisitor<A> vis, final A argu) {
    vis.visit(this, argu);
  }

  /**
   * Accepts a {@link IVoidVisitor} visitor with no user Return nor Argument data.
   *
   * @param vis - the visitor
   */
  @Override
  public void accept(final IVoidVisitor vis) {
    vis.visit(this);
  }

}

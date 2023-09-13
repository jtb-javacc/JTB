/* Generated by JTB 1.5.1 */
package grammars.q.syntaxtree;

import java.util.ArrayList;
import java.util.Iterator;
import grammars.q.visitor.IRetVisitor;
import grammars.q.visitor.IRetArguVisitor;
import grammars.q.visitor.IVoidVisitor;
import grammars.q.visitor.IVoidArguVisitor;

@SuppressWarnings("javadoc")
public class NodeList implements INodeList {

  public ArrayList<INode> nodes;

  private  final int allocTb[] = {1, 2, 3, 4, 5, 10, 20, 50};

  private int allocNb = 0;

  private static final long serialVersionUID = 151L;

  public NodeList() {
    nodes = new ArrayList<>(allocTb[allocNb]);
  }

  public NodeList(final int sz) {
    nodes = new ArrayList<>(sz);
  }

  public NodeList(final INode firstNode) {
    nodes = new ArrayList<>(allocTb[allocNb]);
    addNode(firstNode);
  }

  public NodeList(final int sz, final INode firstNode) {
    nodes = new ArrayList<>(sz);
    addNode(firstNode);
  }

  @Override
  public void addNode(final INode n) {
    if (++allocNb < allocTb.length)
      nodes.ensureCapacity(allocTb[allocNb]);
    else
      nodes.ensureCapacity((allocNb - allocTb.length + 2) * allocTb[(allocTb.length - 1)]);
    nodes.add(n);
  }

  @Override
  public INode elementAt(final int i) {
    return nodes.get(i); }

  @Override
  public Iterator<INode> elements() {
    return nodes.iterator(); }

  @Override
  public int size() {
    return nodes.size(); }

  /*
   * Visitors accept methods (no -novis option, visitors specification : Void,void,None;VoidArgu,void,A;Ret,R,None;RetArgu,R,A)
   */

  @Override
  public <R> R accept(final IRetVisitor<R> vis) {
    return vis.visit(this);
  }

  @Override
  public <R, A> R accept(final IRetArguVisitor<R, A> vis, final A argu) {
    return vis.visit(this, argu);
  }

  @Override
  public void accept(final IVoidVisitor vis) {
    vis.visit(this);
  }

  @Override
  public <A> void accept(final IVoidArguVisitor<A> vis, final A argu) {
    vis.visit(this, argu);
  }

}
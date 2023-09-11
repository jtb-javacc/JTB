/* Generated by JTB 1.5.1 */
package grammars.y.syntaxtree;

import grammars.y.visitor.IRetVisitor;
import grammars.y.visitor.IRetArguVisitor;
import grammars.y.visitor.IVoidVisitor;
import grammars.y.visitor.IVoidArguVisitor;

@SuppressWarnings("javadoc")
public class NodeChoice implements INode {

  public INode choice;

  public int which;

  public int total;

  private static final long serialVersionUID = 151L;

  public NodeChoice(final INode node) {
   this(node, -1, -1);
  }

  public NodeChoice(final INode node, final int whichChoice, final int totalChoices) {
    choice = node;
    which = whichChoice;
    total = totalChoices;
  }

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

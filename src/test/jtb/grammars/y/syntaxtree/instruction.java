/* Generated by JTB 1.5.1 */
package grammars.y.syntaxtree;

import grammars.y.Token;
import grammars.y.visitor.IRetVisitor;
import grammars.y.visitor.IRetArguVisitor;
import grammars.y.visitor.IVoidVisitor;
import grammars.y.visitor.IVoidArguVisitor;


/**
 * JTB node class for the production instruction:<br>
 * Corresponding grammar:<br>
 * f0 -> . %0 #0 < ID > #1 ";"<br>
 * .. .. | %1 ","<br>
 * s: 119476985<br>
 */
public class instruction implements INode {

  /** Child node 0 */
  public NodeChoice f0;

  /** The serial version UID */
  private static final long serialVersionUID = 151L;

  /**
   * Constructs the node with its child node.
   *
   * @param n0 - the child node
   */
  public instruction(final NodeChoice n0) {
    f0 = n0;
  }
  /*
   * Visitors accept methods (no -novis option, visitors specification : Void,void,None;VoidArgu,void,A;Ret,R,None;RetArgu,R,A)
   */

  /**
   * Accepts a {@link IRetVisitor} visitor with user return data.
   *
   * @param <R> - the return type parameter
   * @param vis - the visitor
   * @return the user Return data
   */
  @Override
  public <R> R accept(final IRetVisitor<R> vis) {
    return vis.visit(this);
  }

  /**
   * Accepts a {@link IRetArguVisitor} visitor with user return and argument data.
   *
   * @param <R> - the return type parameter
   * @param <A> - The argument 0 type parameter
   * @param vis - the visitor
   * @param argu - the user Argument data
   * @return the user Return data
   */
  @Override
  public <R, A> R accept(final IRetArguVisitor<R, A> vis, final A argu) {
    return vis.visit(this, argu);
  }

  /**
   * Accepts a {@link IVoidVisitor} visitor} visitor with user return data.
   *
   * @param vis - the visitor
   */
  @Override
  public void accept(final IVoidVisitor vis) {
    vis.visit(this);
  }

  /**
   * Accepts a {@link IVoidArguVisitor} visitor with user argument data.
   *
   * @param <A> - The argument 0 type parameter
   * @param vis - the visitor
   * @param argu - the user Argument data
   */
  @Override
  public <A> void accept(final IVoidArguVisitor<A> vis, final A argu) {
    vis.visit(this, argu);
  }


}

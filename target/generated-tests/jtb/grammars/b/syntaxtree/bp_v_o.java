/* Generated by JTB 1.5.1 */
package grammars.b.syntaxtree;

import grammars.b.Token;
import grammars.b.visitor.IRetVisitor;
import grammars.b.visitor.IRetArguVisitor;
import grammars.b.visitor.IVoidVisitor;
import grammars.b.visitor.IVoidArguVisitor;


/**
 * JTB node class for the production bp_v_o:<br>
 * Corresponding grammar:<br>
 * f0 -> ( %0 #0 ( &0 "_foo"<br>
 * .. .. . .. .. | &1 {} )<br>
 * .. .. . .. #1 "_bar" #2 < ID ><br>
 * .. .. | %1 #0 "_bar" #1 "_bar" )<br>
 * f1 -> ( %0 #0 ( &0 {}<br>
 * .. .. . .. .. | &1 "_foo" )<br>
 * .. .. . .. #1 "_bar" #2 < ID ><br>
 * .. .. | %1 #0 "_bar" #1 "_bar" )<br>
 * s: 1752869627<br>
 */
public class bp_v_o implements INode {

  /** Child node 0 */
  public NodeChoice f0;

  /** Child node 1 */
  public NodeChoice f1;

  /** The serial version UID */
  private static final long serialVersionUID = 151L;

  /**
   * Constructs the node with all its children nodes.
   *
   * @param n0 - first child node
   * @param n1 - next child node
   */
  public bp_v_o(final NodeChoice n0, final NodeChoice n1) {
    f0 = n0;
    f1 = n1;
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
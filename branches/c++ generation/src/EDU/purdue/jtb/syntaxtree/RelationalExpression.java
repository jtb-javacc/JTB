/* Generated by JTB 1.4.8 */
package EDU.purdue.jtb.syntaxtree;

import EDU.purdue.jtb.visitor.*;


/**
 * JTB node class for the production RelationalExpression:<br>
 * Corresponding grammar:<br>
 * f0 -> ShiftExpression()<br>
 * f1 -> ( #0 ( %0 "<"<br>
 * .. .. . .. | %1 ">"<br>
 * .. .. . .. | %2 "<="<br>
 * .. .. . .. | %3 ">=" )<br>
 * .. .. . #1 ShiftExpression() )*<br>
 */
public class RelationalExpression implements INode {

  /** Child node 1 */
  public ShiftExpression f0;

  /** Child node 2 */
  public NodeListOptional f1;

  /** The serial version UID */
  private static final long serialVersionUID = 148L;

  /**
   * Constructs the node with all its children nodes.
   *
   * @param n0 - first child node
   * @param n1 - next child node
   */
  public RelationalExpression(final ShiftExpression n0, final NodeListOptional n1) {
    f0 = n0;
    f1 = n1;
  }

  /**
   * Accepts the IRetArguVisitor visitor.
   *
   * @param <R> the user return type
   * @param <A> the user argument type
   * @param vis - the visitor
   * @param argu - a user chosen argument
   * @return a user chosen return information
   */
  public <R, A> R accept(final IRetArguVisitor<R, A> vis, final A argu) {
    return vis.visit(this, argu);
  }

  /**
   * Accepts the IRetVisitor visitor.
   *
   * @param <R> the user return type
   * @param vis - the visitor
   * @return a user chosen return information
   */
  public <R> R accept(final IRetVisitor<R> vis) {
    return vis.visit(this);
  }

  /**
   * Accepts the IVoidArguVisitor visitor.
   *
   * @param <A> the user argument type
   * @param vis - the visitor
   * @param argu - a user chosen argument
   */
  public <A> void accept(final IVoidArguVisitor<A> vis, final A argu) {
    vis.visit(this, argu);
  }

  /**
   * Accepts the IVoidVisitor visitor.
   *
   * @param vis - the visitor
   */
  public void accept(final IVoidVisitor vis) {
    vis.visit(this);
  }

}

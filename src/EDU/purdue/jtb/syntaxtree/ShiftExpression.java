/* Generated by JTB 1.4.13 */
package EDU.purdue.jtb.syntaxtree;

import EDU.purdue.jtb.visitor.*;

/**
 * JTB node class for the production ShiftExpression:<br>
 * Corresponding grammar:<br>
 * f0 -> AdditiveExpression()<br>
 * f1 -> ( #0 ( %0 "<<"<br>
 * .. .. . .. | %1 RUnsignedShift()<br>
 * .. .. . .. | %2 RSignedShift() )<br>
 * .. .. . #1 AdditiveExpression() )*<br>
 */
public class ShiftExpression implements INode {

  /** Child node 1 */
  public AdditiveExpression f0;

  /** Child node 2 */
  public NodeListOptional f1;

  /** The serial version UID */
  private static final long serialVersionUID = 1L;

  /**
   * Constructs the node with all its children nodes.
   *
   * @param n0 - first child node
   * @param n1 - next child node
   */
  public ShiftExpression(final AdditiveExpression n0, final NodeListOptional n1) {
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
  @Override
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
  @Override
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
  @Override
  public <A> void accept(final IVoidArguVisitor<A> vis, final A argu) {
    vis.visit(this, argu);
  }

  /**
   * Accepts the IVoidVisitor visitor.
   *
   * @param vis - the visitor
   */
  @Override
  public void accept(final IVoidVisitor vis) {
    vis.visit(this);
  }

}

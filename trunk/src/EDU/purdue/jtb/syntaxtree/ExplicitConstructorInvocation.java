/* Generated by JTB 1.4.8 */
package EDU.purdue.jtb.syntaxtree;

import EDU.purdue.jtb.visitor.*;

/**
 * JTB node class for the production ExplicitConstructorInvocation:<br>
 * Corresponding grammar:<br>
 * f0 -> ( %0 #0 [ $0 "<" $1 ReferenceType()<br>
 * .. .. . .. .. . $2 ( ?0 "," ?1 ReferenceType() )*<br>
 * .. .. . .. .. . $3 ">" ]<br>
 * .. .. . .. #1 ( &0 $0 "this" $1 Arguments() $2 ";"<br>
 * .. .. . .. .. | &1 $0 "super" $1 Arguments() $2 ";" )<br>
 * .. .. | %1 ( #0 PrimaryExpression() #1 "." #2 "super" #3 Arguments() #4 ";" ) )<br>
 */
public class ExplicitConstructorInvocation implements INode {

  /** Child node 1 */
  public NodeChoice f0;

  /** The serial version UID */
  private static final long serialVersionUID = 148L;

  /**
   * Constructs the node with its child node.
   *
   * @param n0 - the child node
   */
  public ExplicitConstructorInvocation(final NodeChoice n0) {
    f0 = n0;
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

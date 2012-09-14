/* Generated by JTB 1.4.7 */
package EDU.purdue.jtb.syntaxtree;

import EDU.purdue.jtb.visitor.*;

/**
 * JTB node class for the production ExpansionUnitTCF:<br>
 * Corresponding grammar:<br>
 * f0 -> "try"<br>
 * f1 -> "{"<br>
 * f2 -> ExpansionChoices()<br>
 * f3 -> "}"<br>
 * f4 -> ( #0 "catch" #1 "(" #2 Name() #3 < IDENTIFIER > #4 ")" #5 Block() )*<br>
 * f5 -> [ #0 "finally" #1 Block() ]<br>
 */
public class ExpansionUnitTCF implements INode {

  /** Child node 1 */
  public NodeToken f0;

  /** Child node 2 */
  public NodeToken f1;

  /** Child node 3 */
  public ExpansionChoices f2;

  /** Child node 4 */
  public NodeToken f3;

  /** Child node 5 */
  public NodeListOptional f4;

  /** Child node 6 */
  public NodeOptional f5;

  /** The serial version UID */
  private static final long serialVersionUID = 147L;

  /**
   * Constructs the node with all its children nodes.
   *
   * @param n0 - first child node
   * @param n1 - next child node
   * @param n2 - next child node
   * @param n3 - next child node
   * @param n4 - next child node
   * @param n5 - next child node
   */
  public ExpansionUnitTCF(final NodeToken n0, final NodeToken n1, final ExpansionChoices n2, final NodeToken n3, final NodeListOptional n4, final NodeOptional n5) {
    f0 = n0;
    f1 = n1;
    f2 = n2;
    f3 = n3;
    f4 = n4;
    f5 = n5;
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

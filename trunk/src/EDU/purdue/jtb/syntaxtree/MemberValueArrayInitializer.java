/* Generated by JTB 1.4.5 */
package EDU.purdue.jtb.syntaxtree;

import EDU.purdue.jtb.visitor.*;

/**
 * JTB node class for the production MemberValueArrayInitializer:<br>
 * Corresponding grammar :<br>
 * f0 -> "{"<br>
 * f1 -> MemberValue()<br>
 * f2 -> ( #0 "," #1 MemberValue() )*<br>
 * f3 -> [ "," ]<br>
 * f4 -> "}"<br>
 */
public class MemberValueArrayInitializer implements INode {

  /** A child node */
  public NodeToken f0;

  /** A child node */
  public MemberValue f1;

  /** A child node */
  public NodeListOptional f2;

  /** A child node */
  public NodeOptional f3;

  /** A child node */
  public NodeToken f4;

  /** The serial version uid */
  private static final long serialVersionUID = 145L;

  /**
   * Constructs the node with all its children nodes.
   *
   * @param n0 first child node
   * @param n1 next child node
   * @param n2 next child node
   * @param n3 next child node
   * @param n4 next child node
   */
  public MemberValueArrayInitializer(final NodeToken n0, final MemberValue n1, final NodeListOptional n2, final NodeOptional n3, final NodeToken n4) {
    f0 = n0;
    f1 = n1;
    f2 = n2;
    f3 = n3;
    f4 = n4;
  }

  /**
   * Constructs the node with only its non NodeToken child node(s).
   *
   * @param n0 first child node
   * @param n1 next child node
   * @param n2 next child node
   */
  public MemberValueArrayInitializer(final MemberValue n0, final NodeListOptional n1, final NodeOptional n2) {
    f0 = new NodeToken("{");
    f1 = n0;
    f2 = n1;
    f3 = n2;
    f4 = new NodeToken("}");
  }

  /**
   * Accepts the IRetArguVisitor visitor.
   *
   * @param <R> the user return type
   * @param <A> the user argument type
   * @param vis the visitor
   * @param argu a user chosen argument
   * @return a user chosen return information
   */
  public <R, A> R accept(final IRetArguVisitor<R, A> vis, final A argu) {
    return vis.visit(this, argu);
  }

  /**
   * Accepts the IRetVisitor visitor.
   *
   * @param <R> the user return type
   * @param vis the visitor
   * @return a user chosen return information
   */
  public <R> R accept(final IRetVisitor<R> vis) {
    return vis.visit(this);
  }

  /**
   * Accepts the IVoidArguVisitor visitor.
   *
   * @param <A> the user argument type
   * @param vis the visitor
   * @param argu a user chosen argument
   */
  public <A> void accept(final IVoidArguVisitor<A> vis, final A argu) {
    vis.visit(this, argu);
  }

  /**
   * Accepts the IVoidVisitor visitor.
   *
   * @param vis the visitor
   */
  public void accept(final IVoidVisitor vis) {
    vis.visit(this);
  }

}

/* Generated by JTB 1.4.5 */
package EDU.purdue.jtb.syntaxtree;

import EDU.purdue.jtb.visitor.*;

/**
 * JTB node class for the production TypeBound:<br>
 * Corresponding grammar :<br>
 * f0 -> "extends"<br>
 * f1 -> ClassOrInterfaceType()<br>
 * f2 -> ( #0 "&" #1 ClassOrInterfaceType() )*<br>
 */
public class TypeBound implements INode {

  /** A child node */
  public NodeToken f0;

  /** A child node */
  public ClassOrInterfaceType f1;

  /** A child node */
  public NodeListOptional f2;

  /** The serial version uid */
  private static final long serialVersionUID = 145L;

  /**
   * Constructs the node with all its children nodes.
   *
   * @param n0 first child node
   * @param n1 next child node
   * @param n2 next child node
   */
  public TypeBound(final NodeToken n0, final ClassOrInterfaceType n1, final NodeListOptional n2) {
    f0 = n0;
    f1 = n1;
    f2 = n2;
  }

  /**
   * Constructs the node with only its non NodeToken child node(s).
   *
   * @param n0 first child node
   * @param n1 next child node
   */
  public TypeBound(final ClassOrInterfaceType n0, final NodeListOptional n1) {
    f0 = new NodeToken("extends");
    f1 = n0;
    f2 = n1;
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

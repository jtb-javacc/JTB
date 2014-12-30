/* Generated by JTB 1.4.5 */
package EDU.purdue.jtb.syntaxtree;

import EDU.purdue.jtb.visitor.*;

/**
 * JTB node class for the production AnnotationTypeDeclaration:<br>
 * Corresponding grammar :<br>
 * f0 -> "@"<br>
 * f1 -> "interface"<br>
 * f2 -> < IDENTIFIER ><br>
 * f3 -> AnnotationTypeBody()<br>
 */
public class AnnotationTypeDeclaration implements INode {

  /** A child node */
  public NodeToken f0;

  /** A child node */
  public NodeToken f1;

  /** A child node */
  public NodeToken f2;

  /** A child node */
  public AnnotationTypeBody f3;

  /** The serial version uid */
  private static final long serialVersionUID = 145L;

  /**
   * Constructs the node with all its children nodes.
   *
   * @param n0 first child node
   * @param n1 next child node
   * @param n2 next child node
   * @param n3 next child node
   */
  public AnnotationTypeDeclaration(final NodeToken n0, final NodeToken n1, final NodeToken n2, final AnnotationTypeBody n3) {
    f0 = n0;
    f1 = n1;
    f2 = n2;
    f3 = n3;
  }

  /**
   * Constructs the node with only its non NodeToken child node(s).
   *
   * @param n0 first child node
   * @param n1 next child node
   */
  public AnnotationTypeDeclaration(final NodeToken n0, final AnnotationTypeBody n1) {
    f0 = new NodeToken("@");
    f1 = new NodeToken("interface");
    f2 = n0;
    f3 = n1;
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

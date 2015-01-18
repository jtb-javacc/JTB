/* Generated by JTB 1.4.5 */
package EDU.purdue.jtb.syntaxtree;

import EDU.purdue.jtb.visitor.*;

/**
 * JTB node class for the production TokenManagerDecls:<br>
 * Corresponding grammar :<br>
 * f0 -> "TOKEN_MGR_DECLS"<br>
 * f1 -> ":"<br>
 * f2 -> ClassOrInterfaceBody()<br>
 */
public class TokenManagerDecls implements INode {

  /** A child node */
  public NodeToken f0;

  /** A child node */
  public NodeToken f1;

  /** A child node */
  public ClassOrInterfaceBody f2;

  /** The serial version uid */
  private static final long serialVersionUID = 145L;

  /**
   * Constructs the node with all its children nodes.
   *
   * @param n0 first child node
   * @param n1 next child node
   * @param n2 next child node
   */
  public TokenManagerDecls(final NodeToken n0, final NodeToken n1, final ClassOrInterfaceBody n2) {
    f0 = n0;
    f1 = n1;
    f2 = n2;
  }

  /**
   * Constructs the node with only its non NodeToken child node(s).
   *
   * @param n0 first child node
   */
  public TokenManagerDecls(final ClassOrInterfaceBody n0) {
    f0 = new NodeToken("TOKEN_MGR_DECLS");
    f1 = new NodeToken(":");
    f2 = n0;
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
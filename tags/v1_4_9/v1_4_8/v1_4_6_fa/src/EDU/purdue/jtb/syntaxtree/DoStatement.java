/* Generated by JTB 1.4.5 */
package EDU.purdue.jtb.syntaxtree;

import EDU.purdue.jtb.visitor.*;

/**
 * JTB node class for the production DoStatement:<br>
 * Corresponding grammar :<br>
 * f0 -> "do"<br>
 * f1 -> Statement()<br>
 * f2 -> "while"<br>
 * f3 -> "("<br>
 * f4 -> Expression()<br>
 * f5 -> ")"<br>
 * f6 -> ";"<br>
 */
public class DoStatement implements INode {

  /** A child node */
  public NodeToken f0;

  /** A child node */
  public Statement f1;

  /** A child node */
  public NodeToken f2;

  /** A child node */
  public NodeToken f3;

  /** A child node */
  public Expression f4;

  /** A child node */
  public NodeToken f5;

  /** A child node */
  public NodeToken f6;

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
   * @param n5 next child node
   * @param n6 next child node
   */
  public DoStatement(final NodeToken n0, final Statement n1, final NodeToken n2, final NodeToken n3, final Expression n4, final NodeToken n5, final NodeToken n6) {
    f0 = n0;
    f1 = n1;
    f2 = n2;
    f3 = n3;
    f4 = n4;
    f5 = n5;
    f6 = n6;
  }

  /**
   * Constructs the node with only its non NodeToken child node(s).
   *
   * @param n0 first child node
   * @param n1 next child node
   */
  public DoStatement(final Statement n0, final Expression n1) {
    f0 = new NodeToken("do");
    f1 = n0;
    f2 = new NodeToken("while");
    f3 = new NodeToken("(");
    f4 = n1;
    f5 = new NodeToken(")");
    f6 = new NodeToken(";");
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
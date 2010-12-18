/* Generated by JTB 1.4.5 */
package EDU.purdue.jtb.syntaxtree;

import EDU.purdue.jtb.visitor.*;

/**
 * JTB node class for the production SwitchStatement:<br>
 * Corresponding grammar :<br>
 * f0 -> "switch"<br>
 * f1 -> "("<br>
 * f2 -> Expression()<br>
 * f3 -> ")"<br>
 * f4 -> "{"<br>
 * f5 -> ( #0 SwitchLabel()<br>
 * .. .. . #1 ( BlockStatement() )* )*<br>
 * f6 -> "}"<br>
 */
public class SwitchStatement implements INode {

  /** A child node */
  public NodeToken f0;

  /** A child node */
  public NodeToken f1;

  /** A child node */
  public Expression f2;

  /** A child node */
  public NodeToken f3;

  /** A child node */
  public NodeToken f4;

  /** A child node */
  public NodeListOptional f5;

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
  public SwitchStatement(final NodeToken n0, final NodeToken n1, final Expression n2, final NodeToken n3, final NodeToken n4, final NodeListOptional n5, final NodeToken n6) {
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
  public SwitchStatement(final Expression n0, final NodeListOptional n1) {
    f0 = new NodeToken("switch");
    f1 = new NodeToken("(");
    f2 = n0;
    f3 = new NodeToken(")");
    f4 = new NodeToken("{");
    f5 = n1;
    f6 = new NodeToken("}");
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

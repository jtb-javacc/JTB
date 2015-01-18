/* Generated by JTB 1.4.5 */
package EDU.purdue.jtb.syntaxtree;

import EDU.purdue.jtb.visitor.*;

/**
 * JTB node class for the production ConstructorDeclaration:<br>
 * Corresponding grammar :<br>
 * f0 -> [ TypeParameters() ]<br>
 * f1 -> < IDENTIFIER ><br>
 * f2 -> FormalParameters()<br>
 * f3 -> [ #0 "throws" #1 NameList() ]<br>
 * f4 -> "{"<br>
 * f5 -> [ ExplicitConstructorInvocation() ]<br>
 * f6 -> ( BlockStatement() )*<br>
 * f7 -> "}"<br>
 */
public class ConstructorDeclaration implements INode {

  /** A child node */
  public NodeOptional f0;

  /** A child node */
  public NodeToken f1;

  /** A child node */
  public FormalParameters f2;

  /** A child node */
  public NodeOptional f3;

  /** A child node */
  public NodeToken f4;

  /** A child node */
  public NodeOptional f5;

  /** A child node */
  public NodeListOptional f6;

  /** A child node */
  public NodeToken f7;

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
   * @param n7 next child node
   */
  public ConstructorDeclaration(final NodeOptional n0, final NodeToken n1, final FormalParameters n2, final NodeOptional n3, final NodeToken n4, final NodeOptional n5, final NodeListOptional n6, final NodeToken n7) {
    f0 = n0;
    f1 = n1;
    f2 = n2;
    f3 = n3;
    f4 = n4;
    f5 = n5;
    f6 = n6;
    f7 = n7;
  }

  /**
   * Constructs the node with only its non NodeToken child node(s).
   *
   * @param n0 first child node
   * @param n1 next child node
   * @param n2 next child node
   * @param n3 next child node
   * @param n4 next child node
   * @param n5 next child node
   */
  public ConstructorDeclaration(final NodeOptional n0, final NodeToken n1, final FormalParameters n2, final NodeOptional n3, final NodeOptional n4, final NodeListOptional n5) {
    f0 = n0;
    f1 = n1;
    f2 = n2;
    f3 = n3;
    f4 = new NodeToken("{");
    f5 = n4;
    f6 = n5;
    f7 = new NodeToken("}");
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
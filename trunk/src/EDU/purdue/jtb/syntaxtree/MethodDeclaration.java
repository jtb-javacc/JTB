/* Generated by JTB 1.4.5 */
package EDU.purdue.jtb.syntaxtree;

import EDU.purdue.jtb.visitor.*;

/**
 * JTB node class for the production MethodDeclaration:<br>
 * Corresponding grammar :<br>
 * f0 -> [ TypeParameters() ]<br>
 * f1 -> ResultType()<br>
 * f2 -> MethodDeclarator()<br>
 * f3 -> [ #0 "throws" #1 NameList() ]<br>
 * f4 -> ( %0 Block()<br>
 * .. .. | %1 ";" )<br>
 */
public class MethodDeclaration implements INode {

  /** A child node */
  public NodeOptional f0;

  /** A child node */
  public ResultType f1;

  /** A child node */
  public MethodDeclarator f2;

  /** A child node */
  public NodeOptional f3;

  /** A child node */
  public NodeChoice f4;

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
  public MethodDeclaration(final NodeOptional n0, final ResultType n1, final MethodDeclarator n2, final NodeOptional n3, final NodeChoice n4) {
    f0 = n0;
    f1 = n1;
    f2 = n2;
    f3 = n3;
    f4 = n4;
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

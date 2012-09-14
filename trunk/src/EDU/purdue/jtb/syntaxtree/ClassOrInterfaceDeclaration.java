/* Generated by JTB 1.4.7 */
package EDU.purdue.jtb.syntaxtree;

import EDU.purdue.jtb.visitor.*;

/**
 * JTB node class for the production ClassOrInterfaceDeclaration:<br>
 * Corresponding grammar:<br>
 * f0 -> ( %0 "class"<br>
 * .. .. | %1 "interface" )<br>
 * f1 -> < IDENTIFIER ><br>
 * f2 -> [ TypeParameters() ]<br>
 * f3 -> [ ExtendsList() ]<br>
 * f4 -> [ ImplementsList() ]<br>
 * f5 -> ClassOrInterfaceBody()<br>
 */
public class ClassOrInterfaceDeclaration implements INode {

  /** Child node 1 */
  public NodeChoice f0;

  /** Child node 2 */
  public NodeToken f1;

  /** Child node 3 */
  public NodeOptional f2;

  /** Child node 4 */
  public NodeOptional f3;

  /** Child node 5 */
  public NodeOptional f4;

  /** Child node 6 */
  public ClassOrInterfaceBody f5;

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
  public ClassOrInterfaceDeclaration(final NodeChoice n0, final NodeToken n1, final NodeOptional n2, final NodeOptional n3, final NodeOptional n4, final ClassOrInterfaceBody n5) {
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
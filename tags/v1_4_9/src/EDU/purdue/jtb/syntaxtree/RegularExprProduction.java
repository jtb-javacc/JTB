/* Generated by JTB 1.4.9 */
package EDU.purdue.jtb.syntaxtree;

import EDU.purdue.jtb.visitor.*;

/**
 * JTB node class for the production RegularExprProduction:<br>
 * Corresponding grammar:<br>
 * f0 -> [ %0 #0 "<" #1 "*" #2 ">"<br>
 * .. .. | %1 #0 "<" #1 <IDENTIFIER><br>
 * .. .. . .. #2 ( $0 "," $1 <IDENTIFIER> )*<br>
 * .. .. . .. #3 ">" ]<br>
 * f1 -> RegExprKind()<br>
 * f2 -> [ #0 "[" #1 "IGNORE_CASE" #2 "]" ]<br>
 * f3 -> ":"<br>
 * f4 -> "{"<br>
 * f5 -> RegExprSpec()<br>
 * f6 -> ( #0 "|" #1 RegExprSpec() )*<br>
 * f7 -> "}"<br>
 */
public class RegularExprProduction implements INode {

  /** Child node 1 */
  public NodeOptional f0;

  /** Child node 2 */
  public RegExprKind f1;

  /** Child node 3 */
  public NodeOptional f2;

  /** Child node 4 */
  public NodeToken f3;

  /** Child node 5 */
  public NodeToken f4;

  /** Child node 6 */
  public RegExprSpec f5;

  /** Child node 7 */
  public NodeListOptional f6;

  /** Child node 8 */
  public NodeToken f7;

  /** The serial version UID */
  private static final long serialVersionUID = 149L;

  /**
   * Constructs the node with all its children nodes.
   *
   * @param n0 - first child node
   * @param n1 - next child node
   * @param n2 - next child node
   * @param n3 - next child node
   * @param n4 - next child node
   * @param n5 - next child node
   * @param n6 - next child node
   * @param n7 - next child node
   */
  public RegularExprProduction(final NodeOptional n0, final RegExprKind n1, final NodeOptional n2, final NodeToken n3, final NodeToken n4, final RegExprSpec n5, final NodeListOptional n6, final NodeToken n7) {
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
   * Accepts the IRetArguVisitor visitor.
   *
   * @param <R> the user return type
   * @param <A> the user argument type
   * @param vis - the visitor
   * @param argu - a user chosen argument
   * @return a user chosen return information
   */
  @Override
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
  @Override
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
  @Override
  public <A> void accept(final IVoidArguVisitor<A> vis, final A argu) {
    vis.visit(this, argu);
  }

  /**
   * Accepts the IVoidVisitor visitor.
   *
   * @param vis - the visitor
   */
  @Override
  public void accept(final IVoidVisitor vis) {
    vis.visit(this);
  }

}

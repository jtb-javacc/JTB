/* Generated by JTB 1.4.14 */
package EDU.purdue.jtb.syntaxtree;

import EDU.purdue.jtb.visitor.*;

/**
 * JTB node class for the production RegularExpression:<br>
 * Corresponding grammar:<br>
 * f0 -> . %0 StringLiteral()<br>
 * .. .. | %1 #0 "<"<br>
 * .. .. . .. #1 [ $0 [ "#" ]<br>
 * .. .. . .. .. . $1 IdentifierAsString() $2 ":" ]<br>
 * .. .. . .. #2 ComplexRegularExpressionChoices() #3 ">"<br>
 * .. .. | %2 #0 "<" #1 IdentifierAsString() #2 ">"<br>
 * .. .. | %3 #0 "<" #1 "EOF" #2 ">"<br>
 */
public class RegularExpression implements INode {

  /** Child node 1 */
  public NodeChoice f0;

  /** The serial version UID */
  private static final long serialVersionUID = 1414L;

  /**
   * Constructs the node with its child node.
   *
   * @param n0 - the child node
   */
  public RegularExpression(final NodeChoice n0) {
    f0 = n0;
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

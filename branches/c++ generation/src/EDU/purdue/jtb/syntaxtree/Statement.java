/* Generated by JTB 1.4.8 */
package EDU.purdue.jtb.syntaxtree;

import EDU.purdue.jtb.visitor.*;


/**
 * JTB node class for the production Statement:<br>
 * Corresponding grammar:<br>
 * f0 -> . %00 LabeledStatement()<br>
 * .. .. | %01 AssertStatement()<br>
 * .. .. | %02 Block()<br>
 * .. .. | %03 EmptyStatement()<br>
 * .. .. | %04 #0 StatementExpression() #1 ";"<br>
 * .. .. | %05 SwitchStatement()<br>
 * .. .. | %06 IfStatement()<br>
 * .. .. | %07 WhileStatement()<br>
 * .. .. | %08 DoStatement()<br>
 * .. .. | %09 ForStatement()<br>
 * .. .. | %10 BreakStatement()<br>
 * .. .. | %11 ContinueStatement()<br>
 * .. .. | %12 ReturnStatement()<br>
 * .. .. | %13 ThrowStatement()<br>
 * .. .. | %14 SynchronizedStatement()<br>
 * .. .. | %15 TryStatement()<br>
 */
public class Statement implements INode {

  /** Child node 1 */
  public NodeChoice f0;

  /** The serial version UID */
  private static final long serialVersionUID = 148L;

  /**
   * Constructs the node with its child node.
   *
   * @param n0 - the child node
   */
  public Statement(final NodeChoice n0) {
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

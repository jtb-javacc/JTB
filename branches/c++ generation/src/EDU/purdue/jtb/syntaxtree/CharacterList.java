/* Generated by JTB 1.4.7 */
package EDU.purdue.jtb.syntaxtree;

import EDU.purdue.jtb.visitor.IRetArguVisitor;
import EDU.purdue.jtb.visitor.IRetVisitor;
import EDU.purdue.jtb.visitor.IVoidArguVisitor;
import EDU.purdue.jtb.visitor.IVoidVisitor;

/**
 * JTB node class for the production CharacterList:<br>
 * Corresponding grammar:<br>
 * f0 -> [ "~" ]<br>
 * f1 -> "["<br>
 * f2 -> [ #0 CharacterDescriptor()<br>
 * .. .. . #1 ( $0 "," $1 CharacterDescriptor() )* ]<br>
 * f3 -> "]"<br>
 */
public class CharacterList implements INode {

  /** Child node 1 */
  public NodeOptional f0;

  /** Child node 2 */
  public NodeToken f1;

  /** Child node 3 */
  public NodeOptional f2;

  /** Child node 4 */
  public NodeToken f3;

  /** The serial version UID */
  private static final long serialVersionUID = 147L;

  /**
   * Constructs the node with all its children nodes.
   *
   * @param n0 - first child node
   * @param n1 - next child node
   * @param n2 - next child node
   * @param n3 - next child node
   */
  public CharacterList(final NodeOptional n0, final NodeToken n1, final NodeOptional n2, final NodeToken n3) {
    f0 = n0;
    f1 = n1;
    f2 = n2;
    f3 = n3;
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

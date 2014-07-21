/* Generated by JTB 1.4.7 */
package EDU.purdue.jtb.syntaxtree;

import EDU.purdue.jtb.visitor.IRetArguVisitor;
import EDU.purdue.jtb.visitor.IRetVisitor;
import EDU.purdue.jtb.visitor.IVoidArguVisitor;
import EDU.purdue.jtb.visitor.IVoidVisitor;

/**
 * JTB node class for the production Modifiers:<br>
 * Corresponding grammar:<br>
 * f0 -> ( ( %00 "public"<br>
 * .. .. . | %01 "static"<br>
 * .. .. . | %02 "protected"<br>
 * .. .. . | %03 "private"<br>
 * .. .. . | %04 "final"<br>
 * .. .. . | %05 "abstract"<br>
 * .. .. . | %06 "synchronized"<br>
 * .. .. . | %07 "native"<br>
 * .. .. . | %08 "transient"<br>
 * .. .. . | %09 "volatile"<br>
 * .. .. . | %10 "strictfp"<br>
 * .. .. . | %11 Annotation() ) )*<br>
 */
public class Modifiers implements INode {

  /** Child node 1 */
  public NodeListOptional f0;

  /** The serial version UID */
  private static final long serialVersionUID = 147L;

  /**
   * Constructs the node with its child node.
   *
   * @param n0 - the child node
   */
  public Modifiers(final NodeListOptional n0) {
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

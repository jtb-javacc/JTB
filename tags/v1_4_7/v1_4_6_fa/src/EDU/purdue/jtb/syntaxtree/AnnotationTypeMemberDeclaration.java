/* Generated by JTB 1.4.5 */
package EDU.purdue.jtb.syntaxtree;

import EDU.purdue.jtb.visitor.*;

/**
 * JTB node class for the production AnnotationTypeMemberDeclaration:<br>
 * Corresponding grammar :<br>
 * f0 -> . %0 #0 Modifiers()<br>
 * .. .. . .. #1 ( &0 $0 Type() $1 < IDENTIFIER > $2 "(" $3 ")"<br>
 * .. .. . .. .. $4 [ DefaultValue() ] $5 ";"<br>
 * .. .. . .. .. | &1 ClassOrInterfaceDeclaration()<br>
 * .. .. . .. .. | &2 EnumDeclaration()<br>
 * .. .. . .. .. | &3 AnnotationTypeDeclaration()<br>
 * .. .. . .. .. | &4 FieldDeclaration() )<br>
 * .. .. | %1 ";"<br>
 */
public class AnnotationTypeMemberDeclaration implements INode {

  /** A child node */
  public NodeChoice f0;

  /** The serial version uid */
  private static final long serialVersionUID = 145L;

  /**
   * Constructs the node with its child node.
   *
   * @param n0 the child node
   */
  public AnnotationTypeMemberDeclaration(final NodeChoice n0) {
    f0 = n0;
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
/* Generated by JTB 1.4.14 */
package EDU.purdue.jtb.syntaxtree;

import EDU.purdue.jtb.visitor.*;

/**
 * JTB node class for the production JavaCodeProduction:<br>
 * Corresponding grammar:<br>
 * f0 -> "JAVACODE"<br>
 * f1 -> AccessModifier()<br>
 * f2 -> ResultType()<br>
 * f3 -> IdentifierAsString()<br>
 * f4 -> FormalParameters()<br>
 * f5 -> [ #0 "throws" #1 Name()<br>
 * .. .. . #2 ( $0 "," $1 Name() )* ]<br>
 * f6 -> [ "%" ]<br>
 * f7 -> Block()<br>
 */
public class JavaCodeProduction implements INode {

  /** Child node 1 */
  public NodeToken f0;

  /** Child node 2 */
  public AccessModifier f1;

  /** Child node 3 */
  public ResultType f2;

  /** Child node 4 */
  public IdentifierAsString f3;

  /** Child node 5 */
  public FormalParameters f4;

  /** Child node 6 */
  public NodeOptional f5;

  /** Child node 7 */
  public NodeOptional f6;

  /** Child node 8 */
  public Block f7;

  /** The serial version UID */
  private static final long serialVersionUID = 1414L;

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
  public JavaCodeProduction(final NodeToken n0, final AccessModifier n1, final ResultType n2, final IdentifierAsString n3, final FormalParameters n4, final NodeOptional n5, final NodeOptional n6, final Block n7) {
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

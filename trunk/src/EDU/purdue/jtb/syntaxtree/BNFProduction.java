/* Generated by JTB 1.4.7 */
package EDU.purdue.jtb.syntaxtree;

import EDU.purdue.jtb.visitor.*;

/**
 * JTB node class for the production BNFProduction:<br>
 * Corresponding grammar:<br>
 * f0 -> AccessModifier()<br>
 * f1 -> ResultType()<br>
 * f2 -> IdentifierAsString()<br>
 * f3 -> FormalParameters()<br>
 * f4 -> [ #0 "throws" #1 Name()<br>
 * .. .. . #2 ( $0 "," $1 Name() )* ]<br>
 * f5 -> ":"<br>
 * f6 -> Block()<br>
 * f7 -> "{"<br>
 * f8 -> ExpansionChoices()<br>
 * f9 -> "}"<br>
 */
public class BNFProduction implements INode {

  /** Child node 1 */
  public AccessModifier f0;

  /** Child node 2 */
  public ResultType f1;

  /** Child node 3 */
  public IdentifierAsString f2;

  /** Child node 4 */
  public FormalParameters f3;

  /** Child node 5 */
  public NodeOptional f4;

  /** Child node 6 */
  public NodeToken f5;

  /** Child node 7 */
  public Block f6;

  /** Child node 8 */
  public NodeToken f7;

  /** Child node 9 */
  public ExpansionChoices f8;

  /** Child node 10 */
  public NodeToken f9;

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
   * @param n6 - next child node
   * @param n7 - next child node
   * @param n8 - next child node
   * @param n9 - next child node
   */
  public BNFProduction(final AccessModifier n0, final ResultType n1, final IdentifierAsString n2, final FormalParameters n3, final NodeOptional n4, final NodeToken n5, final Block n6, final NodeToken n7, final ExpansionChoices n8, final NodeToken n9) {
    f0 = n0;
    f1 = n1;
    f2 = n2;
    f3 = n3;
    f4 = n4;
    f5 = n5;
    f6 = n6;
    f7 = n7;
    f8 = n8;
    f9 = n9;
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

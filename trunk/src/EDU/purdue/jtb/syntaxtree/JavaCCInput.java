/* Generated by JTB 1.4.5 */
package EDU.purdue.jtb.syntaxtree;

import EDU.purdue.jtb.visitor.*;

/**
 * JTB node class for the production JavaCCInput:<br>
 * Corresponding grammar :<br>
 * f0 -> JavaCCOptions()<br>
 * f1 -> "PARSER_BEGIN"<br>
 * f2 -> "("<br>
 * f3 -> Identifier()<br>
 * f4 -> ")"<br>
 * f5 -> CompilationUnit()<br>
 * f6 -> "PARSER_END"<br>
 * f7 -> "("<br>
 * f8 -> Identifier()<br>
 * f9 -> ")"<br>
 * f10 -> ( Production() )+<br>
 */
public class JavaCCInput implements INode {

  /** A child node */
  public JavaCCOptions f0;

  /** A child node */
  public NodeToken f1;

  /** A child node */
  public NodeToken f2;

  /** A child node */
  public Identifier f3;

  /** A child node */
  public NodeToken f4;

  /** A child node */
  public CompilationUnit f5;

  /** A child node */
  public NodeToken f6;

  /** A child node */
  public NodeToken f7;

  /** A child node */
  public Identifier f8;

  /** A child node */
  public NodeToken f9;

  /** A child node */
  public NodeList f10;

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
   * @param n5 next child node
   * @param n6 next child node
   * @param n7 next child node
   * @param n8 next child node
   * @param n9 next child node
   * @param n10 next child node
   */
  public JavaCCInput(final JavaCCOptions n0, final NodeToken n1, final NodeToken n2, final Identifier n3, final NodeToken n4, final CompilationUnit n5, final NodeToken n6, final NodeToken n7, final Identifier n8, final NodeToken n9, final NodeList n10) {
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
    f10 = n10;
  }

  /**
   * Constructs the node with only its non NodeToken child node(s).
   *
   * @param n0 first child node
   * @param n1 next child node
   * @param n2 next child node
   * @param n3 next child node
   * @param n4 next child node
   */
  public JavaCCInput(final JavaCCOptions n0, final Identifier n1, final CompilationUnit n2, final Identifier n3, final NodeList n4) {
    f0 = n0;
    f1 = new NodeToken("PARSER_BEGIN");
    f2 = new NodeToken("(");
    f3 = n1;
    f4 = new NodeToken(")");
    f5 = n2;
    f6 = new NodeToken("PARSER_END");
    f7 = new NodeToken("(");
    f8 = n3;
    f9 = new NodeToken(")");
    f10 = n4;
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

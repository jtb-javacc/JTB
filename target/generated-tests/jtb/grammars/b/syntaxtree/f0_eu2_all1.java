/* Generated by JTB 1.5.1 */
package grammars.b.syntaxtree;

import grammars.b.Token;
import grammars.b.visitor.IRetVisitor;
import grammars.b.visitor.IRetArguVisitor;
import grammars.b.visitor.IVoidVisitor;
import grammars.b.visitor.IVoidArguVisitor;


/**
 * JTB node class for the production f0_eu2_all1:<br>
 * Corresponding grammar:<br>
 * f0 -> [ ";".image ]<br>
 * f1 -> f0_eu1_b1()<br>
 * f2 -> ( #0 ";; "<br>
 * .. .. . #1 ( $0 ";;; " $1 ";;;; " ) )<br>
 * f3 -> ( < NUM_3_9 > )*<br>
 * f4 -> ( ( %0 ":"<br>
 * .. .. . | %1 < NUM_3_9 ><br>
 * .. .. . | %2 ":" ) )?<br>
 * f5 -> f0_eu1_peias2()<br>
 * f6 ->   %0 expch_a()<br>
 * .. .. | %1 expch_b()<br>
 * f7 -> ( %0 expch_2ch()<br>
 * .. .. | %1 expch_bl_a() )<br>
 * s: -1377841071<br>
 */
public class f0_eu2_all1 implements INode {

  /** Child node 0 */
  public NodeOptional f0;

  /** Child node 1 */
  public f0_eu1_b1 f1;

  /** Child node 2 */
  public NodeSequence f2;

  /** Child node 3 */
  public NodeListOptional f3;

  /** Child node 4 */
  public NodeOptional f4;

  /** Child node 5 */
  public f0_eu1_peias2 f5;

  /** Child node 6 */
  public NodeChoice f6;

  /** Child node 7 */
  public NodeChoice f7;

  /** The serial version UID */
  private static final long serialVersionUID = 151L;

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
  public f0_eu2_all1(final NodeOptional n0, final f0_eu1_b1 n1, final NodeSequence n2, final NodeListOptional n3, final NodeOptional n4, final f0_eu1_peias2 n5, final NodeChoice n6, final NodeChoice n7) {
    f0 = n0;
    f1 = n1;
    f2 = n2;
    f3 = n3;
    f4 = n4;
    f5 = n5;
    f6 = n6;
    f7 = n7;
  }
  /*
   * Visitors accept methods (no -novis option, visitors specification : Void,void,None;VoidArgu,void,A;Ret,R,None;RetArgu,R,A)
   */

  /**
   * Accepts a {@link IRetVisitor} visitor with user return data.
   *
   * @param <R> - the return type parameter
   * @param vis - the visitor
   * @return the user Return data
   */
  @Override
  public <R> R accept(final IRetVisitor<R> vis) {
    return vis.visit(this);
  }

  /**
   * Accepts a {@link IRetArguVisitor} visitor with user return and argument data.
   *
   * @param <R> - the return type parameter
   * @param <A> - The argument 0 type parameter
   * @param vis - the visitor
   * @param argu - the user Argument data
   * @return the user Return data
   */
  @Override
  public <R, A> R accept(final IRetArguVisitor<R, A> vis, final A argu) {
    return vis.visit(this, argu);
  }

  /**
   * Accepts a {@link IVoidVisitor} visitor} visitor with user return data.
   *
   * @param vis - the visitor
   */
  @Override
  public void accept(final IVoidVisitor vis) {
    vis.visit(this);
  }

  /**
   * Accepts a {@link IVoidArguVisitor} visitor with user argument data.
   *
   * @param <A> - The argument 0 type parameter
   * @param vis - the visitor
   * @param argu - the user Argument data
   */
  @Override
  public <A> void accept(final IVoidArguVisitor<A> vis, final A argu) {
    vis.visit(this, argu);
  }


}

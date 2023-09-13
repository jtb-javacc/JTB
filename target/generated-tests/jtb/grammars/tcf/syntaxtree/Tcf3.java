/* Generated by JTB 1.5.1 */
package grammars.tcf.syntaxtree;

import grammars.tcf.Token;
import grammars.tcf.visitor.IVoidVisitor;


/**
 * JTB node class for the production Tcf3:<br>
 * Corresponding grammar:<br>
 * f0 -> Identifier()<br>
 * f1 -> ( %0 {}<br>
 * .. .. | %1 Integer_literal_boum() )?<br>
 * f2 -> ":"<br>
 * s: 553976958<br>
 */
public class Tcf3 implements INode {

  /** Child node 0 */
  public Identifier f0;

  /** Child node 1 */
  public NodeOptional f1;

  /** Child node 2 */
  public Token f2;

  /** The serial version UID */
  private static final long serialVersionUID = 151L;

  /**
   * Constructs the node with all its children nodes.
   *
   * @param n0 - first child node
   * @param n1 - next child node
   * @param n2 - next child node
   */
  public Tcf3(final Identifier n0, final NodeOptional n1, final Token n2) {
    f0 = n0;
    f1 = n1;
    f2 = n2;
  }
  /*
   * Visitors accept methods (no -novis option, visitors specification : Void,void,None)
   */

  /**
   * Accepts a {@link IVoidVisitor} visitor} visitor with user return data.
   *
   * @param vis - the visitor
   */
  @Override
  public void accept(final IVoidVisitor vis) {
    vis.visit(this);
  }


}
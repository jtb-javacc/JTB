/* Generated by JTB 1.5.1 */
package EDU.purdue.jtb.parser.syntaxtree;

import EDU.purdue.jtb.parser.Token;
import EDU.purdue.jtb.parser.visitor.IIntVisitor;
import EDU.purdue.jtb.parser.visitor.IVoidVisitor;


/**
 * JTB node class for the production ReturnStatement:<br>
 * Corresponding grammar:<br>
 * f0 -> "return"<br>
 * f1 -> [ Expression() ]<br>
 * f2 -> ";"<br>
 * s: -1971167888<br>
 */
public class ReturnStatement implements INode {

  /** Child node 0 */
  public Token f0;

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
  public ReturnStatement(final Token n0, final NodeOptional n1, final Token n2) {
    f0 = n0;
    f1 = n1;
    f2 = n2;
  }
  /*
   * Visitors accept methods (no -novis option, visitors specification : Void,void,None;Int,int,None)
   */

  /**
   * Accepts a {@link IIntVisitor} visitor with user return data.
   *
   * @param vis - the visitor
   * @return the user Return data
   */
  @Override
  public int accept(final IIntVisitor vis) {
    return vis.visit(this);
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


}

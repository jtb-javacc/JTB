/* Generated by JTB 1.5.1 */
package EDU.purdue.jtb.parser.syntaxtree;

import EDU.purdue.jtb.parser.Token;
import EDU.purdue.jtb.parser.visitor.IIntVisitor;
import EDU.purdue.jtb.parser.visitor.IVoidVisitor;


/**
 * JTB node class for the production TypeArguments:<br>
 * Corresponding grammar:<br>
 * f0 -> "<"<br>
 * f1 -> TypeArgument()<br>
 * f2 -> ( #0 "," #1 TypeArgument() )*<br>
 * f3 -> ">"<br>
 * s: 131755052<br>
 */
public class TypeArguments implements INode {

  /** Child node 0 */
  public Token f0;

  /** Child node 1 */
  public TypeArgument f1;

  /** Child node 2 */
  public NodeListOptional f2;

  /** Child node 3 */
  public Token f3;

  /** The serial version UID */
  private static final long serialVersionUID = 151L;

  /**
   * Constructs the node with all its children nodes.
   *
   * @param n0 - first child node
   * @param n1 - next child node
   * @param n2 - next child node
   * @param n3 - next child node
   */
  public TypeArguments(final Token n0, final TypeArgument n1, final NodeListOptional n2, final Token n3) {
    f0 = n0;
    f1 = n1;
    f2 = n2;
    f3 = n3;
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

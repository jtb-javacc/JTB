/* Generated by JTB 1.5.1 */
package EDU.purdue.jtb.parser.syntaxtree;

import EDU.purdue.jtb.parser.Token;
import EDU.purdue.jtb.parser.visitor.IIntVisitor;
import EDU.purdue.jtb.parser.visitor.IVoidVisitor;


/**
 * JTB node class for the production LocalVariableDeclaration:<br>
 * Corresponding grammar:<br>
 * f0 -> VariableModifiers()<br>
 * f1 -> Type()<br>
 * f2 -> VariableDeclarator()<br>
 * f3 -> ( #0 "," #1 VariableDeclarator() )*<br>
 * s: 225808290<br>
 */
public class LocalVariableDeclaration implements INode {

  /** Child node 0 */
  public VariableModifiers f0;

  /** Child node 1 */
  public Type f1;

  /** Child node 2 */
  public VariableDeclarator f2;

  /** Child node 3 */
  public NodeListOptional f3;

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
  public LocalVariableDeclaration(final VariableModifiers n0, final Type n1, final VariableDeclarator n2, final NodeListOptional n3) {
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
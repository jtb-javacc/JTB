/* Generated by JTB 1.5.1 */
package EDU.purdue.jtb.parser.syntaxtree;

import EDU.purdue.jtb.parser.Token;
import EDU.purdue.jtb.parser.visitor.IIntVisitor;
import EDU.purdue.jtb.parser.visitor.IVoidVisitor;


/**
 * JTB node class for the production MemberSelector:<br>
 * Corresponding grammar:<br>
 * f0 -> "."<br>
 * f1 -> TypeArguments()<br>
 * f2 -> < IDENTIFIER ><br>
 * s: 257570924<br>
 */
public class MemberSelector implements INode {

  /** Child node 0 */
  public Token f0;

  /** Child node 1 */
  public TypeArguments f1;

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
  public MemberSelector(final Token n0, final TypeArguments n1, final Token n2) {
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

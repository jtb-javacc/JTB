/* Generated by JTB 1.5.1 */
package EDU.purdue.jtb.parser.syntaxtree;

import EDU.purdue.jtb.parser.Token;
import EDU.purdue.jtb.parser.visitor.IIntVisitor;
import EDU.purdue.jtb.parser.visitor.IVoidVisitor;


/**
 * JTB node class for the production ClassOrInterfaceDeclaration:<br>
 * Corresponding grammar:<br>
 * f0 -> ( %0 "class"<br>
 * .. .. | %1 "interface" )<br>
 * f1 -> < IDENTIFIER ><br>
 * f2 -> [ TypeParameters() ]<br>
 * f3 -> [ ExtendsList() ]<br>
 * f4 -> [ ImplementsList() ]<br>
 * f5 -> ClassOrInterfaceBody()<br>
 * s: 37426766<br>
 */
public class ClassOrInterfaceDeclaration implements INode {

  /** Child node 0 */
  public NodeChoice f0;

  /** Child node 1 */
  public Token f1;

  /** Child node 2 */
  public NodeOptional f2;

  /** Child node 3 */
  public NodeOptional f3;

  /** Child node 4 */
  public NodeOptional f4;

  /** Child node 5 */
  public ClassOrInterfaceBody f5;

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
   */
  public ClassOrInterfaceDeclaration(final NodeChoice n0, final Token n1, final NodeOptional n2, final NodeOptional n3, final NodeOptional n4, final ClassOrInterfaceBody n5) {
    f0 = n0;
    f1 = n1;
    f2 = n2;
    f3 = n3;
    f4 = n4;
    f5 = n5;
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

/* Generated by JTB 1.5.1 */
package EDU.purdue.jtb.parser.syntaxtree;

import EDU.purdue.jtb.parser.Token;
import EDU.purdue.jtb.parser.visitor.IIntVisitor;
import EDU.purdue.jtb.parser.visitor.IVoidVisitor;


/**
 * JTB node class for the production ForStatement:<br>
 * Corresponding grammar:<br>
 * f0 -> "for"<br>
 * f1 -> "("<br>
 * f2 -> ( %0 #0 VariableModifiers() #1 Type() #2 < IDENTIFIER > #3 ":" #4 Expression()<br>
 * .. .. | %1 #0 [ ForInit() ]<br>
 * .. .. . .. #1 ";"<br>
 * .. .. . .. #2 [ Expression() ]<br>
 * .. .. . .. #3 ";"<br>
 * .. .. . .. #4 [ ForUpdate() ] )<br>
 * f3 -> ")"<br>
 * f4 -> Statement()<br>
 * s: 755358653<br>
 */
public class ForStatement implements INode {

  /** Child node 0 */
  public Token f0;

  /** Child node 1 */
  public Token f1;

  /** Child node 2 */
  public NodeChoice f2;

  /** Child node 3 */
  public Token f3;

  /** Child node 4 */
  public Statement f4;

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
   */
  public ForStatement(final Token n0, final Token n1, final NodeChoice n2, final Token n3, final Statement n4) {
    f0 = n0;
    f1 = n1;
    f2 = n2;
    f3 = n3;
    f4 = n4;
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

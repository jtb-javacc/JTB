/* Generated by JTB 1.5.1 */
package EDU.purdue.jtb.parser.syntaxtree;

import EDU.purdue.jtb.parser.Token;
import EDU.purdue.jtb.parser.visitor.IIntVisitor;
import EDU.purdue.jtb.parser.visitor.IVoidVisitor;


/**
 * JTB node class for the production JavaIdentifier:<br>
 * Corresponding grammar:<br>
 * f0 -> ( %00 < IDENTIFIER ><br>
 * .. .. | %01 "LOOKAHEAD"<br>
 * .. .. | %02 "IGNORE_CASE"<br>
 * .. .. | %03 "PARSER_BEGIN"<br>
 * .. .. | %04 "PARSER_END"<br>
 * .. .. | %05 "JAVACODE"<br>
 * .. .. | %06 "TOKEN"<br>
 * .. .. | %07 "SPECIAL_TOKEN"<br>
 * .. .. | %08 "MORE"<br>
 * .. .. | %09 "SKIP"<br>
 * .. .. | %10 "TOKEN_MGR_DECLS"<br>
 * .. .. | %11 "EOF" )<br>
 * s: 1665786565<br>
 */
public class JavaIdentifier implements INode {

  /** Child node 0 */
  public NodeChoice f0;

  /** The serial version UID */
  private static final long serialVersionUID = 151L;

  /**
   * Constructs the node with its child node.
   *
   * @param n0 - the child node
   */
  public JavaIdentifier(final NodeChoice n0) {
    f0 = n0;
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

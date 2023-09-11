/* Generated by JTB 1.5.1 */
package grammars.z.syntaxtree;

import grammars.z.Token;
import grammars.z.visitor.IRetVisitor;
import grammars.z.visitor.IRetArguVisitor;
import grammars.z.visitor.IVoidVisitor;
import grammars.z.visitor.IVoidArguVisitor;


@SuppressWarnings("javadoc")
public class instruction implements INode {

  public Token f0;

  public Token f1;

  private static final long serialVersionUID = 151L;

  public instruction(final Token n0, final Token n1) {
    f0 = n0;
    f1 = n1;
  }
  /*
   * Visitors accept methods (no -novis option, visitors specification : Void,void,None;VoidArgu,void,A;Ret,R,None;RetArgu,R,A)
   */

  @Override
  public <R> R accept(final IRetVisitor<R> vis) {
    return vis.visit(this);
  }

  @Override
  public <R, A> R accept(final IRetArguVisitor<R, A> vis, final A argu) {
    return vis.visit(this, argu);
  }

  @Override
  public void accept(final IVoidVisitor vis) {
    vis.visit(this);
  }

  @Override
  public <A> void accept(final IVoidArguVisitor<A> vis, final A argu) {
    vis.visit(this, argu);
  }


}

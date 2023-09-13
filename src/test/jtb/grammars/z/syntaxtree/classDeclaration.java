/* Generated by JTB 1.5.1 */
package grammars.z.syntaxtree;

import grammars.z.Token;
import grammars.z.visitor.IRetVisitor;
import grammars.z.visitor.IRetArguVisitor;
import grammars.z.visitor.IVoidVisitor;
import grammars.z.visitor.IVoidArguVisitor;


@SuppressWarnings("javadoc")
public class classDeclaration implements INode {

  public Token f0;

  public className f1;

  public Token f2;

  public NodeListOptional f3;

  public Token f4;

  public Token f5;

  public Token f6;

  private static final long serialVersionUID = 151L;

  public classDeclaration(final Token n0, final className n1, final Token n2, final NodeListOptional n3, final Token n4, final Token n5, final Token n6) {
    f0 = n0;
    f1 = n1;
    f2 = n2;
    f3 = n3;
    f4 = n4;
    f5 = n5;
    f6 = n6;
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
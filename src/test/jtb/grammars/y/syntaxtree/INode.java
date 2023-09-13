/* Generated by JTB 1.5.1 */
package grammars.y.syntaxtree;

import grammars.y.visitor.IRetVisitor;
import grammars.y.visitor.IRetArguVisitor;
import grammars.y.visitor.IVoidVisitor;
import grammars.y.visitor.IVoidArguVisitor;

@SuppressWarnings("javadoc")
public interface INode extends java.io.Serializable {

  public static final String LS = System.getProperty("line.separator"); //$NON-NLS-1$

  /*
   * Visitors accept methods (no -novis option, visitors specification : Void,void,None;VoidArgu,void,A;Ret,R,None;RetArgu,R,A)
   */

  public <R> R accept(final IRetVisitor<R> vis);

  public <R, A> R accept(final IRetArguVisitor<R, A> vis, final A argu);

  public void accept(final IVoidVisitor vis);

  public <A> void accept(final IVoidArguVisitor<A> vis, final A argu);

}
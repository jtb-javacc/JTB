/* Generated by JTB 1.5.1 */
package grammars.z.syntaxtree;

import grammars.z.visitor.IRetVisitor;
import grammars.z.visitor.IRetArguVisitor;
import grammars.z.visitor.IVoidVisitor;
import grammars.z.visitor.IVoidArguVisitor;

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
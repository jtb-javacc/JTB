/* Generated by JTB 1.5.1 */
package grammars.z.syntaxtree;

import grammars.z.visitor.IRetVisitor;
import grammars.z.visitor.IRetArguVisitor;
import grammars.z.visitor.IVoidVisitor;
import grammars.z.visitor.IVoidArguVisitor;

/**
 * The interface which all syntax tree classes must implement.
 */
public interface INode extends java.io.Serializable {

  /** The OS line separator */
  public static final String LS = System.getProperty("line.separator"); //$NON-NLS-1$

  /*
   * Visitors accept methods (no -novis option, visitors specification : Void,void,None;VoidArgu,void,A;Ret,R,None;RetArgu,R,A)
   */

  /**
   * Accepts a {@link IRetVisitor} visitor with user return data.
   *
   * @param <R> - the return type parameter
   * @param vis - the visitor
   * @return the user Return data
   */
  public <R> R accept(final IRetVisitor<R> vis);

  /**
   * Accepts a {@link IRetArguVisitor} visitor with user return and argument data.
   *
   * @param <R> - the return type parameter
   * @param <A> - The argument 0 type parameter
   * @param vis - the visitor
   * @param argu - the user Argument data
   * @return the user Return data
   */
  public <R, A> R accept(final IRetArguVisitor<R, A> vis, final A argu);

  /**
   * Accepts a {@link IVoidVisitor} visitor} visitor with user return data.
   *
   * @param vis - the visitor
   */
  public void accept(final IVoidVisitor vis);

  /**
   * Accepts a {@link IVoidArguVisitor} visitor with user argument data.
   *
   * @param <A> - The argument 0 type parameter
   * @param vis - the visitor
   * @param argu - the user Argument data
   */
  public <A> void accept(final IVoidArguVisitor<A> vis, final A argu);

}

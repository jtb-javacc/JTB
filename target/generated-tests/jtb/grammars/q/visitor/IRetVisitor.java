/* Generated by JTB 1.5.1 */
package grammars.q.visitor;

import grammars.q.syntaxtree.*;
import grammars.q.Token;

@SuppressWarnings("javadoc")
public interface IRetVisitor<R> {

  public R visit(final NodeChoice n);

  public R visit(final NodeList n);

  public R visit(final NodeListOptional n);

  public R visit(final NodeOptional n);

  public R visit(final NodeSequence n);

  public R visit(final Token n);

  public R visit(final classDeclaration n);

  public R visit(final className n);

  public R visit(final method n);

  public R visit(final methodName n);

  public R visit(final instruction n);

  public R visit(final bp_jual n);

  public R visit(final bp_hm n);

  public R visit(final jc_0 n);

}

/* Generated by JTB 1.5.1 */
package grammars.z.visitor;

import grammars.z.syntaxtree.*;
import grammars.z.Token;

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

}
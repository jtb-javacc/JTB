/* Generated by JTB 1.5.1 */
package grammars.z.visitor;

import grammars.z.syntaxtree.*;
import grammars.z.Token;

@SuppressWarnings("javadoc")
public interface IVoidVisitor {

  public void visit(final NodeChoice n);

  public void visit(final NodeList n);

  public void visit(final NodeListOptional n);

  public void visit(final NodeOptional n);

  public void visit(final NodeSequence n);

  public void visit(final Token n);

  public void visit(final classDeclaration n);

  public void visit(final className n);

  public void visit(final method n);

  public void visit(final methodName n);

  public void visit(final instruction n);

}
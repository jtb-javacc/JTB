/* Generated by JTB 1.5.1 */
package grammars.y.visitor;

import static grammars.y.syntaxtree.NodeConstants.*;
import grammars.y.Token;
import grammars.y.syntaxtree.*;
import grammars.y.visitor.signature.NodeFieldsSignature;

@SuppressWarnings("javadoc")
public class DepthFirstRetVisitor<R> implements IRetVisitor<R> {


  @Override
  public R visit(final NodeChoice n) {
    /* You have to adapt which data is returned (result variables below are just examples) */
    final R nRes = n.choice.accept(this);
    return nRes;
  }

  @Override
  public R visit(final NodeList n) {
    /* You have to adapt which data is returned (result variables below are just examples) */
    R nRes = null;
    for (INode e : n.nodes) {
      @SuppressWarnings("unused")
      final R sRes = e.accept(this);
    }
    return nRes;
  }

  @Override
  public R visit(final NodeListOptional n) {
    /* You have to adapt which data is returned (result variables below are just examples) */
    if (n.present()) {
      R nRes = null;
      for (INode e : n.nodes) {
        @SuppressWarnings("unused")
        R sRes = e.accept(this);
      }
      return nRes;
    }
    return null;
  }

  @Override
  public R visit(final NodeOptional n) {
    /* You have to adapt which data is returned (result variables below are just examples) */
    if (n.present()) {
      final R nRes = n.node.accept(this);
      return nRes;
    }
    return null;
  }

  @Override
  public R visit(final NodeSequence n) {
    /* You have to adapt which data is returned (result variables below are just examples) */
    R nRes = null;
    for (INode e : n.nodes) {
      @SuppressWarnings("unused")
      R subRet = e.accept(this);
    }
    return nRes;
  }

  @Override
  public R visit(final Token n) {
    /* You have to adapt which data is returned (result variables below are just examples) */
    R nRes = null;
    @SuppressWarnings("unused")
    final String tkIm = n.image;
    return nRes;
  }

  @Override
  @NodeFieldsSignature({ 0, JTB_SIG_CLASSDECLARATION, JTB_USER_CLASSDECLARATION })
  public R visit(final classDeclaration n) {
    R nRes = null;
    n.f0.accept(this);
    n.f1.accept(this);
    n.f2.accept(this);
    n.f3.accept(this);
    n.f4.accept(this);
    n.f5.accept(this);
    n.f6.accept(this);
    n.f7.accept(this);
    return nRes;
  }

  @Override
  @NodeFieldsSignature({ 0, JTB_SIG_CLASSNAME, JTB_USER_CLASSNAME })
  public R visit(final className n) {
    R nRes = null;
    n.f0.accept(this);
    return nRes;
  }

  @Override
  @NodeFieldsSignature({ 0, JTB_SIG_METHOD, JTB_USER_METHOD })
  public R visit(final method n) {
    R nRes = null;
    n.f0.accept(this);
    n.f1.accept(this);
    n.f2.accept(this);
    n.f3.accept(this);
    return nRes;
  }

  @Override
  @NodeFieldsSignature({ 0, JTB_SIG_METHODNAME, JTB_USER_METHODNAME })
  public R visit(final methodName n) {
    R nRes = null;
    n.f0.accept(this);
    return nRes;
  }

  @Override
  @NodeFieldsSignature({ 0, JTB_SIG_INSTRUCTION, JTB_USER_INSTRUCTION })
  public R visit(final instruction n) {
    R nRes = null;
    n.f0.accept(this);
    return nRes;
  }

  @Override
  @NodeFieldsSignature({ 0, JTB_SIG_BP_JUAL, JTB_USER_BP_JUAL })
  public R visit(final bp_jual n) {
    R nRes = null;
    n.f0.accept(this);
    return nRes;
  }

  @Override
  @NodeFieldsSignature({ 0, JTB_SIG_BP_HM, JTB_USER_BP_HM })
  public R visit(final bp_hm n) {
    R nRes = null;
    n.f0.accept(this);
    return nRes;
  }

  @SuppressWarnings("unused")
  @Override
  @NodeFieldsSignature({ 0, JTB_SIG_JC_0, JTB_USER_JC_0 })
  public R visit(final jc_0 n) {
    R nRes = null;
    /* empty node, nothing that can be generated so far */
    return nRes;
  }

}

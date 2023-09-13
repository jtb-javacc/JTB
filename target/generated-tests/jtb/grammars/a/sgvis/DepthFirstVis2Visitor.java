/* Generated by JTB 1.5.1 */
package grammars.a.sgvis;

import grammars.a.Token;
import grammars.a.sgtree.*;

@SuppressWarnings("javadoc")
public class DepthFirstVis2Visitor<R, A> implements IVis2Visitor<R, A> {

  int depthLevel = 0;

  @Override
  public R visit(final NodeChoice n, final A argu, final int[] argu1, final short... argu2) {
    /* You have to adapt which data is returned (result variables below are just examples) */
    ++depthLevel;
    final R nRes = n.choice.accept(this, argu, argu1, argu2);
    --depthLevel;
    return nRes;
  }

  @Override
  public R visit(final NodeList n, final A argu, final int[] argu1, final short... argu2) {
    /* You have to adapt which data is returned (result variables below are just examples) */
    R nRes = null;
    for (INode e : n.nodes) {
      ++depthLevel;
      @SuppressWarnings("unused")
      final R sRes = e.accept(this, argu, argu1, argu2);
      --depthLevel;
    }
    return nRes;
  }

  @Override
  public R visit(final NodeListOptional n, final A argu, final int[] argu1, final short... argu2) {
    /* You have to adapt which data is returned (result variables below are just examples) */
    if (n.present()) {
      R nRes = null;
      for (INode e : n.nodes) {
        ++depthLevel;
        @SuppressWarnings("unused")
        R sRes = e.accept(this, argu, argu1, argu2);
        --depthLevel;
      }
      return nRes;
    }
    return null;
  }

  @Override
  public R visit(final NodeOptional n, final A argu, final int[] argu1, final short... argu2) {
    /* You have to adapt which data is returned (result variables below are just examples) */
    if (n.present()) {
      ++depthLevel;
      final R nRes = n.node.accept(this, argu, argu1, argu2);
      --depthLevel;
      return nRes;
    }
    return null;
  }

  @Override
  public R visit(final NodeSequence n, final A argu, final int[] argu1, final short... argu2) {
    /* You have to adapt which data is returned (result variables below are just examples) */
    R nRes = null;
    for (INode e : n.nodes) {
      ++depthLevel;
      @SuppressWarnings("unused")
      R subRet = e.accept(this, argu, argu1, argu2);
      --depthLevel;
    }
    return nRes;
  }

  @Override
  public R visit(final Token n, @SuppressWarnings("unused") final A argu, @SuppressWarnings("unused") final int[] argu1, @SuppressWarnings("unused") final short... argu2) {
    /* You have to adapt which data is returned (result variables below are just examples) */
    R nRes = null;
    @SuppressWarnings("unused")
    final String tkIm = n.image;
    return nRes;
  }

  @Override
  public R visit(final ASTclassDeclaration123 n, final A argu, final int[] argu1, final short... argu2) {
    R nRes = null;
    ++depthLevel;
    n.token.accept(this, argu, argu1, argu2);
    --depthLevel;
    ++depthLevel;
    n.className.accept(this, argu, argu1, argu2);
    --depthLevel;
    ++depthLevel;
    n.token1.accept(this, argu, argu1, argu2);
    --depthLevel;
    ++depthLevel;
    n.nodeListOptional.accept(this, argu, argu1, argu2);
    --depthLevel;
    ++depthLevel;
    n.token2.accept(this, argu, argu1, argu2);
    --depthLevel;
    ++depthLevel;
    n.token3.accept(this, argu, argu1, argu2);
    --depthLevel;
    ++depthLevel;
    n.jc_0.accept(this, argu, argu1, argu2);
    --depthLevel;
    ++depthLevel;
    n.token4.accept(this, argu, argu1, argu2);
    --depthLevel;
    return nRes;
  }

  @Override
  public R visit(final ASTclassName123 n, final A argu, final int[] argu1, final short... argu2) {
    R nRes = null;
    ++depthLevel;
    n.token.accept(this, argu, argu1, argu2);
    --depthLevel;
    return nRes;
  }

  @Override
  public R visit(final ASTmethod123 n, final A argu, final int[] argu1, final short... argu2) {
    R nRes = null;
    ++depthLevel;
    n.methodName.accept(this, argu, argu1, argu2);
    --depthLevel;
    ++depthLevel;
    n.token.accept(this, argu, argu1, argu2);
    --depthLevel;
    ++depthLevel;
    n.nodeList.accept(this, argu, argu1, argu2);
    --depthLevel;
    ++depthLevel;
    n.token1.accept(this, argu, argu1, argu2);
    --depthLevel;
    return nRes;
  }

  @Override
  public R visit(final ASTmethodName123 n, final A argu, final int[] argu1, final short... argu2) {
    R nRes = null;
    ++depthLevel;
    n.token.accept(this, argu, argu1, argu2);
    --depthLevel;
    return nRes;
  }

  @Override
  public R visit(final ASTinstruction123 n, final A argu, final int[] argu1, final short... argu2) {
    R nRes = null;
    ++depthLevel;
    n.nodeChoice.accept(this, argu, argu1, argu2);
    --depthLevel;
    return nRes;
  }

  @Override
  public R visit(final ASTbp_jual123 n, final A argu, final int[] argu1, final short... argu2) {
    R nRes = null;
    ++depthLevel;
    n.token.accept(this, argu, argu1, argu2);
    --depthLevel;
    return nRes;
  }

  @Override
  public R visit(final ASTbp_hm123 n, final A argu, final int[] argu1, final short... argu2) {
    R nRes = null;
    ++depthLevel;
    n.token.accept(this, argu, argu1, argu2);
    --depthLevel;
    return nRes;
  }

  @SuppressWarnings("unused")
  @Override
  public R visit(final ASTjc_0123 n, final A argu, final int[] argu1, final short... argu2) {
    R nRes = null;
    /* empty node, nothing that can be generated so far */
    return nRes;
  }

}
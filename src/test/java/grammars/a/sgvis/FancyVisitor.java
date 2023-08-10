package grammars.a.sgvis;

import grammars.a.sgtree.ASTclassDeclaration123;
import grammars.a.sgtree.NodeToken;

/**
 * A simple fancy visitor.
 */
@SuppressWarnings("javadoc")
public class FancyVisitor extends DepthFirstVis2Visitor<String, String> {

  @Override
  public String visit(final NodeToken n, final String argu, final int[] argu1,
                      final short... argu2) {
    final String tkIm = n.tokenImage;
    return tkIm + argu + argu1 + argu2;
  }

  @Override
  public String visit(final ASTclassDeclaration123 n, @SuppressWarnings("unused") final String argu,
                      final int[] argu1, final short... argu2) {
    // nodeToken -> "class"
    ++depthLevel;
    final String cla = n.nodeToken.accept(this, "nodeToken", new int[] { 7, 0 },
                                          (short) depthLevel);
    --depthLevel;
    // nodeToken1 -> "{"
    ++depthLevel;
    n.nodeToken1.accept(this, "nodeToken1", new int[] { 7, 1 }, (short) depthLevel);
    --depthLevel;
    // nodeListOptional -> ( method() )*
    ++depthLevel;
    n.nodeListOptional.accept(this, "nodeListOptional", new int[] { 3, 0 }, (short) depthLevel);
    --depthLevel;
    // nodeToken2 -> "}"
    ++depthLevel;
    n.nodeToken2.accept(this, "nodeToken2", new int[] { 7, 2 }, (short) depthLevel);
    --depthLevel;
    // nodeToken3 -> "."
    ++depthLevel;
    n.nodeToken3.accept(this, "nodeToken3", new int[] { 7, 3 }, (short) depthLevel);
    --depthLevel;
    // nodeToken4 -> < EOF >
    ++depthLevel;
    n.nodeToken4.accept(this, "nodeToken4", new int[] { 7, 4 }, (short) depthLevel);
    --depthLevel;
    return cla + argu1 + argu2;
  }

}

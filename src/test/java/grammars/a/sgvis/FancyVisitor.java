package grammars.a.sgvis;

import grammars.a.sgtree.ASTclassDeclaration123;
import grammars.a.Token;

/**
 * A simple fancy visitor.
 */
@SuppressWarnings("javadoc")
public class FancyVisitor extends DepthFirstVis2Visitor<String, String> {
  
  @Override
  public String visit(final Token n, final String argu, final int[] argu1, final short... argu2) {
    final String tkIm = n.image;
    return tkIm + argu + argu1 + argu2;
  }
  
  @Override
  public String visit(final ASTclassDeclaration123 n, @SuppressWarnings("unused") final String argu,
      final int[] argu1, final short... argu2) {
    // token -> "class"
    ++depthLevel;
    final String cla = n.token.accept(this, "token", new int[] {
        7, 0
    }, (short) depthLevel);
    --depthLevel;
    // token1 -> "{"
    ++depthLevel;
    n.token1.accept(this, "token1", new int[] {
        7, 1
    }, (short) depthLevel);
    --depthLevel;
    // nodeListOptional -> ( method() )*
    ++depthLevel;
    n.nodeListOptional.accept(this, "nodeListOptional", new int[] {
        3, 0
    }, (short) depthLevel);
    --depthLevel;
    // token2 -> "}"
    ++depthLevel;
    n.token2.accept(this, "token2", new int[] {
        7, 2
    }, (short) depthLevel);
    --depthLevel;
    // token3 -> "."
    ++depthLevel;
    n.token3.accept(this, "token3", new int[] {
        7, 3
    }, (short) depthLevel);
    --depthLevel;
    // token4 -> < EOF >
    ++depthLevel;
    n.token4.accept(this, "token4", new int[] {
        7, 4
    }, (short) depthLevel);
    --depthLevel;
    return cla + argu1 + argu2;
  }
  
}

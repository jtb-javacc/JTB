package grammars.a.sghook;

import grammars.a.SmallGrammar;
import grammars.a.Token;
import grammars.a.sgtree.ASTclassDeclaration123;

/** A fancy hook. */
// @SuppressWarnings({ "javadoc", "unused" })
@SuppressWarnings({
    "javadoc"
})
public class FancyEnterExitHook /* implements IEnterExitHook */ extends EmptyEnterExitHook {
  
  SmallGrammar sg  = null;
  String       str = null;
  
  public FancyEnterExitHook(final SmallGrammar aSG, final String aStr) {
    sg = aSG;
    str = aStr;
  }
  /*
   * Enter methods
   */
  
  @Override
  public void ASTclassDeclaration123Enter() {
    System.out.println("/// Entering hook :  str : " + str + ", sg : " + sg);
    str = str.toUpperCase();
  }
  
  // @Override
  // public void ASTclassName123Enter() {
  // // to be filled if necessary
  // }
  //
  // @Override
  // public void ASTmethod123Enter() {
  // // to be filled if necessary
  // }
  //
  // @Override
  // public void ASTmethodName123Enter() {
  // // to be filled if necessary
  // }
  //
  // @Override
  // public void ASTinstruction123Enter() {
  // // to be filled if necessary
  // }
  //
  // @Override
  // public void ASTskip123Enter() {
  // // to be filled if necessary
  // }
  
  /*
   * Exit methods
   */
  
  @Override
  public void ASTclassDeclaration123Exit(final ASTclassDeclaration123 n) {
    final String im = ((Token) n.className.nodeToken).image;
    System.out.println("/// Exiting hook : class name : " + im + ", str : " + str + ", sg : " + sg);
    ((Token) n.className.nodeToken).image = str.substring(0, im.length());
  }
  
  // @Override
  // public void ASTclassName123Exit(final ASTclassName123 n) {
  // // to be filled if necessary
  // }
  //
  // @Override
  // public void ASTmethod123Exit(final ASTmethod123 n) {
  // // to be filled if necessary
  // }
  //
  // @Override
  // public void ASTmethodName123Exit(final ASTmethodName123 n) {
  // // to be filled if necessary
  // }
  //
  // @Override
  // public void ASTinstruction123Exit(final ASTinstruction123 n) {
  // // to be filled if necessary
  // }
  //
  // @Override
  // public void ASTskip123Exit(final ASTskip123 n) {
  // // to be filled if necessary
  // }
  
}

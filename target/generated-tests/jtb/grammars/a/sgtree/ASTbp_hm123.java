/* Generated by JTB 1.5.1 */
package grammars.a.sgtree;

import java.util.ArrayList;
import java.util.List;
import grammars.a.Token;
import grammars.a.sgvis.IVis2Visitor;
import grammars.a.sgvis.IVoidVisitor;


@SuppressWarnings("javadoc")
public class ASTbp_hm123 implements INode {

  public Token token;

  private INode parent;

  private static final long serialVersionUID = 151L;

  public ASTbp_hm123(final Token n0) {
    token = n0;
    if (token != null)
      token.setParent(this);
  }
  /*
   * Visitors accept methods (no -novis option, visitors specification : Void,void,None;Vis2,R,A,int[],short...)
   */

  @Override
  public <R, A> R accept(final IVis2Visitor<R, A> vis, final A argu, final int[] argu1, final short... argu2) {
    return vis.visit(this, argu, argu1, argu2);
  }

  @Override
  public void accept(final IVoidVisitor vis) {
    vis.visit(this);
  }

  /*
   * Parent pointer getter and setter (-pp option)
   */

  @Override
  public INode getParent() {
    return parent;
  }

  @Override
  public void setParent(final INode n) {
    parent = n;
  }

  /*
   * Children methods (-chm option)
   */

  private List<INode> lac = null;

  private List<INode> lbc = null;

  private List<INode> luc = null;

  @Override
  public boolean isBaseNode() {
    return false;
  }


  @Override
  public int getNbAllChildren() {
    return 1;
  }

  @Override
  public int getNbBaseChildren() {
    return 0;
  }

  @Override
  public int getNbUserChildren() {
    return 1;
  }

  @Override
  public List<INode> getAllChildren() {
    if (lac == null) {
      lac = new ArrayList<>(1);
      lac.add(token);
    }
    return lac;
  }

  @Override
  public List<INode> getBaseChildren() {
    if (lbc == null) {
      lbc = new ArrayList<>(0);
    }
    return lbc;
  }

  @Override
  public List<INode> getUserChildren() {
    if (luc == null) {
      luc = new ArrayList<>(1);
      luc.add(token);
    }
    return luc;
  }

}

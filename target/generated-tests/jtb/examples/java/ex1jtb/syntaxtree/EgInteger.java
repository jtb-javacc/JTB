/* Generated by JTB 1.5.1 */
package examples.java.ex1jtb.syntaxtree;

import java.util.ArrayList;
import java.util.List;
import examples.java.ex1jtb.Token;
import examples.java.ex1jtb.visitor.IGenVisitor;


@SuppressWarnings("javadoc")
public class EgInteger implements INode {

  public Token f0;

  public jc_1 f1;

  private INode parent;

  private static final long serialVersionUID = 151L;

  public EgInteger(final Token n0, final jc_1 n1) {
    f0 = n0;
    if (f0 != null)
      f0.setParent(this);
    f1 = n1;
    if (f1 != null)
      f1.setParent(this);
  }
  /*
   * Visitors accept methods (no -novis option, visitors specification : Gen,void,java.lang.String)
   */

  @Override
  public void accept(final IGenVisitor vis, final String argu) {
    vis.visit(this, argu);
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
    return 2;
  }

  @Override
  public int getNbBaseChildren() {
    return 0;
  }

  @Override
  public int getNbUserChildren() {
    return 2;
  }

  @Override
  public List<INode> getAllChildren() {
    if (lac == null) {
      lac = new ArrayList<>(2);
      lac.add(f0);
      lac.add(f1);
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
      luc = new ArrayList<>(2);
      luc.add(f0);
      luc.add(f1);
    }
    return luc;
  }

}
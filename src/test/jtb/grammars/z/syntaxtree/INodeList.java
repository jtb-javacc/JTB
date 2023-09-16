/* Generated by JTB 1.5.1 */
package grammars.z.syntaxtree;

/**
 * The interface which {@link NodeList}, {@link NodeListOptional} and {@link NodeSequence} must implement.
 */
public interface INodeList extends INode {

  /**
   * Adds a node to the list.
   *
   * @param n - the node to add
   */
  public void addNode(final INode n);

  /**
   * @param i - the element index
   * @return the element at the given index
   */
  public INode elementAt(int i);

  /**
   * @return the iterator on the node list
   */
  public java.util.Iterator<INode> elements();

  /**
   * @return the list size
   */
  public int size();

}

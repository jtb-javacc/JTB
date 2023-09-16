/* Generated by JTB 1.5.1 */
package grammars.z.visitor;

import static grammars.z.syntaxtree.NodeConstants.*;
import grammars.z.Token;
import grammars.z.syntaxtree.*;
import grammars.z.visitor.signature.NodeFieldsSignature;

/**
 * Provides default methods which visit each node in the tree in depth-first order.<br>
 * In your "Void" visitors extend this class and override part or all of these methods.
 *
 */
public class DepthFirstVoidVisitor implements IVoidVisitor {


  /*
   * Base nodes classes visit methods (to be overridden if necessary)
   */

  /**
   * Visits a {@link NodeChoice} node.
   *
   * @param n - the node to visit
   */
  @Override
  public void visit(final NodeChoice n) {
    n.choice.accept(this);
    return;
  }

  /**
   * Visits a {@link NodeList} node.
   *
   * @param n - the node to visit
   */
  @Override
  public void visit(final NodeList n) {
    for (INode e : n.nodes) {
      e.accept(this);
    }
    return;
  }

  /**
   * Visits a {@link NodeListOptional} node.
   *
   * @param n - the node to visit
   */
  @Override
  public void visit(final NodeListOptional n) {
    if (n.present()) {
      for (INode e : n.nodes) {
        e.accept(this);
      }
      return;
    }
    return;
  }

  /**
   * Visits a {@link NodeOptional} node.
   *
   * @param n - the node to visit
   */
  @Override
  public void visit(final NodeOptional n) {
    if (n.present()) {
      n.node.accept(this);
      return;
    }
    return;
  }

  /**
   * Visits a {@link NodeSequence} node.
   *
   * @param n - the node to visit
   */
  @Override
  public void visit(final NodeSequence n) {
    for (INode e : n.nodes) {
      e.accept(this);
    }
    return;
  }

  /**
   * Visits a {@link Token} node.
   *
   * @param n - the node to visit
   */
  @Override
  public void visit(final Token n) {
    @SuppressWarnings("unused")
    final String tkIm = n.image;
    return;
  }

  /*
   * User grammar generated visit methods (to be overridden if necessary)
   */

  /**
   * Visits a {@link classDeclaration} node, whose children are the following :
   * <p>
   * f0 -> "class"<br>
   * f1 -> className()<br>
   * f2 -> "{"<br>
   * f3 -> ( %0 method()<br>
   * .. .. | %1 instruction() )*<br>
   * f4 -> "}"<br>
   * f5 -> "."<br>
   * f6 -> < EOF ><br>
   * s: -98135469<br>
   *
   * @param n - the node to visit
   */
  @Override
  @NodeFieldsSignature({ -98135469, JTB_SIG_CLASSDECLARATION, JTB_USER_CLASSDECLARATION })
  public void visit(final classDeclaration n) {
    // f0 -> "class"
    final Token n0 = n.f0;
    n0.accept(this);
    // f1 -> className()
    final className n1 = n.f1;
    n1.accept(this);
    // f2 -> "{"
    final Token n2 = n.f2;
    n2.accept(this);
    // f3 -> ( %0 method()
    // .. .. | %1 instruction() )*
    final NodeListOptional n3 = n.f3;
    if (n3.present()) {
      for (int i = 0; i < n3.size(); i++) {
        final INode nloeai = n3.elementAt(i);
        final NodeChoice nch = (NodeChoice) nloeai;
        final INode ich = nch.choice;
        switch (nch.which) {
          case 0:
            //%0 method()
            ich.accept(this);
            break;
          case 1:
            //%1 instruction()
            ich.accept(this);
            break;
          default:
            // should not occur !!!
            throw new ShouldNotOccurException(nch);
        }
      }
    }
    // f4 -> "}"
    final Token n4 = n.f4;
    n4.accept(this);
    // f5 -> "."
    final Token n5 = n.f5;
    n5.accept(this);
    // f6 -> < EOF >
    final Token n6 = n.f6;
    n6.accept(this);
  }

  /**
   * Visits a {@link className} node, whose child is the following :
   * <p>
   * f0 -> < ID ><br>
   * s: -1032372970<br>
   *
   * @param n - the node to visit
   */
  @Override
  @NodeFieldsSignature({ -1032372970, JTB_SIG_CLASSNAME, JTB_USER_CLASSNAME })
  public void visit(final className n) {
    // f0 -> < ID >
    final Token n0 = n.f0;
    n0.accept(this);
  }

  /**
   * Visits a {@link method} node, whose children are the following :
   * <p>
   * f0 -> methodName()<br>
   * f1 -> "("<br>
   * f2 -> ( instruction() )+<br>
   * f3 -> ")"<br>
   * s: 128623837<br>
   *
   * @param n - the node to visit
   */
  @Override
  @NodeFieldsSignature({ 128623837, JTB_SIG_METHOD, JTB_USER_METHOD })
  public void visit(final method n) {
    // f0 -> methodName()
    final methodName n0 = n.f0;
    n0.accept(this);
    // f1 -> "("
    final Token n1 = n.f1;
    n1.accept(this);
    // f2 -> ( instruction() )+
    final NodeList n2 = n.f2;
    for (int i = 0; i < n2.size(); i++) {
      final INode lsteai = n2.elementAt(i);
      lsteai.accept(this);
    }
    // f3 -> ")"
    final Token n3 = n.f3;
    n3.accept(this);
  }

  /**
   * Visits a {@link methodName} node, whose child is the following :
   * <p>
   * f0 -> < ID ><br>
   * s: -1032372970<br>
   *
   * @param n - the node to visit
   */
  @Override
  @NodeFieldsSignature({ -1032372970, JTB_SIG_METHODNAME, JTB_USER_METHODNAME })
  public void visit(final methodName n) {
    // f0 -> < ID >
    final Token n0 = n.f0;
    n0.accept(this);
  }

  /**
   * Visits a {@link instruction} node, whose children are the following :
   * <p>
   * f0 -> < ID ><br>
   * f1 -> ";"<br>
   * s: 1947544793<br>
   *
   * @param n - the node to visit
   */
  @Override
  @NodeFieldsSignature({ 1947544793, JTB_SIG_INSTRUCTION, JTB_USER_INSTRUCTION })
  public void visit(final instruction n) {
    // f0 -> < ID >
    final Token n0 = n.f0;
    n0.accept(this);
    // f1 -> ";"
    final Token n1 = n.f1;
    n1.accept(this);
  }

  /**
   * Class handling a programmatic exception. Static for generic outer classes.
   */
  public static class ShouldNotOccurException extends RuntimeException {

    /** Default serialVersionUID */
    private static final long serialVersionUID = 1L;

    /**
     * Constructor with no message.
     */
    public ShouldNotOccurException() {
      super();
    }

    /**
     * Constructor which outputs a message.
     *
     * @param ch - a NodeChoice whose which value is invalid or lead to a fall-through
     */
    public ShouldNotOccurException(final NodeChoice ch) {
      super("Invalid switch value (" + ch.which + ") or fall-through");
    }

  }

}

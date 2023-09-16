/* Generated by JTB 1.5.1 */
package grammars.z.visitor;

import static grammars.z.syntaxtree.NodeConstants.*;
import grammars.z.Token;
import grammars.z.syntaxtree.*;
import grammars.z.visitor.signature.NodeFieldsSignature;

/**
 * Provides default methods which visit each node in the tree in depth-first order.<br>
 * In your "RetArgu" visitors extend this class and override part or all of these methods.
 *
 * @param <R> - The return type parameter
 * @param <A> - The argument 0 type parameter
 */
public class DepthFirstRetArguVisitor<R, A> implements IRetArguVisitor<R, A> {


  /*
   * Base nodes classes visit methods (to be overridden if necessary)
   */

  /**
   * Visits a {@link NodeChoice} node.
   *
   * @param n - the node to visit
   * @param argu - the user argument 0
   * @return the user return information
   */
  @Override
  public R visit(final NodeChoice n, final A argu) {
    /* You have to adapt which data is returned (result variables below are just examples) */
    final R nRes = n.choice.accept(this, argu);
    return nRes;
  }

  /**
   * Visits a {@link NodeList} node.
   *
   * @param n - the node to visit
   * @param argu - the user argument 0
   * @return the user return information
   */
  @Override
  public R visit(final NodeList n, final A argu) {
    /* You have to adapt which data is returned (result variables below are just examples) */
    R nRes = null;
    for (INode e : n.nodes) {
      @SuppressWarnings("unused")
      final R sRes = e.accept(this, argu);
    }
    return nRes;
  }

  /**
   * Visits a {@link NodeListOptional} node.
   *
   * @param n - the node to visit
   * @param argu - the user argument 0
   * @return the user return information
   */
  @Override
  public R visit(final NodeListOptional n, final A argu) {
    /* You have to adapt which data is returned (result variables below are just examples) */
    if (n.present()) {
      R nRes = null;
      for (INode e : n.nodes) {
        @SuppressWarnings("unused")
        R sRes = e.accept(this, argu);
      }
      return nRes;
    }
    return null;
  }

  /**
   * Visits a {@link NodeOptional} node.
   *
   * @param n - the node to visit
   * @param argu - the user argument 0
   * @return the user return information
   */
  @Override
  public R visit(final NodeOptional n, final A argu) {
    /* You have to adapt which data is returned (result variables below are just examples) */
    if (n.present()) {
      final R nRes = n.node.accept(this, argu);
      return nRes;
    }
    return null;
  }

  /**
   * Visits a {@link NodeSequence} node.
   *
   * @param n - the node to visit
   * @param argu - the user argument 0
   * @return the user return information
   */
  @Override
  public R visit(final NodeSequence n, final A argu) {
    /* You have to adapt which data is returned (result variables below are just examples) */
    R nRes = null;
    for (INode e : n.nodes) {
      @SuppressWarnings("unused")
      R subRet = e.accept(this, argu);
    }
    return nRes;
  }

  /**
   * Visits a {@link Token} node.
   *
   * @param n - the node to visit
   * @param argu - the user argument 0
   * @return the user return information
   */
  @Override
  public R visit(final Token n, @SuppressWarnings("unused") final A argu) {
    /* You have to adapt which data is returned (result variables below are just examples) */
    R nRes = null;
    @SuppressWarnings("unused")
    final String tkIm = n.image;
    return nRes;
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
   * @param argu - the user argument 0
   * @return the user return information
   */
  @Override
  @NodeFieldsSignature({ -98135469, JTB_SIG_CLASSDECLARATION, JTB_USER_CLASSDECLARATION })
  public R visit(final classDeclaration n, final A argu) {
    R nRes = null;
    // f0 -> "class"
    final Token n0 = n.f0;
    nRes = n0.accept(this, argu);
    // f1 -> className()
    final className n1 = n.f1;
    nRes = n1.accept(this, argu);
    // f2 -> "{"
    final Token n2 = n.f2;
    nRes = n2.accept(this, argu);
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
            nRes = ich.accept(this, argu);
            break;
          case 1:
            //%1 instruction()
            nRes = ich.accept(this, argu);
            break;
          default:
            // should not occur !!!
            throw new ShouldNotOccurException(nch);
        }
      }
    }
    // f4 -> "}"
    final Token n4 = n.f4;
    nRes = n4.accept(this, argu);
    // f5 -> "."
    final Token n5 = n.f5;
    nRes = n5.accept(this, argu);
    // f6 -> < EOF >
    final Token n6 = n.f6;
    nRes = n6.accept(this, argu);
    return nRes;
  }

  /**
   * Visits a {@link className} node, whose child is the following :
   * <p>
   * f0 -> < ID ><br>
   * s: -1032372970<br>
   *
   * @param n - the node to visit
   * @param argu - the user argument 0
   * @return the user return information
   */
  @Override
  @NodeFieldsSignature({ -1032372970, JTB_SIG_CLASSNAME, JTB_USER_CLASSNAME })
  public R visit(final className n, final A argu) {
    R nRes = null;
    // f0 -> < ID >
    final Token n0 = n.f0;
    nRes = n0.accept(this, argu);
    return nRes;
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
   * @param argu - the user argument 0
   * @return the user return information
   */
  @Override
  @NodeFieldsSignature({ 128623837, JTB_SIG_METHOD, JTB_USER_METHOD })
  public R visit(final method n, final A argu) {
    R nRes = null;
    // f0 -> methodName()
    final methodName n0 = n.f0;
    nRes = n0.accept(this, argu);
    // f1 -> "("
    final Token n1 = n.f1;
    nRes = n1.accept(this, argu);
    // f2 -> ( instruction() )+
    final NodeList n2 = n.f2;
    for (int i = 0; i < n2.size(); i++) {
      final INode lsteai = n2.elementAt(i);
      nRes = lsteai.accept(this, argu);
    }
    // f3 -> ")"
    final Token n3 = n.f3;
    nRes = n3.accept(this, argu);
    return nRes;
  }

  /**
   * Visits a {@link methodName} node, whose child is the following :
   * <p>
   * f0 -> < ID ><br>
   * s: -1032372970<br>
   *
   * @param n - the node to visit
   * @param argu - the user argument 0
   * @return the user return information
   */
  @Override
  @NodeFieldsSignature({ -1032372970, JTB_SIG_METHODNAME, JTB_USER_METHODNAME })
  public R visit(final methodName n, final A argu) {
    R nRes = null;
    // f0 -> < ID >
    final Token n0 = n.f0;
    nRes = n0.accept(this, argu);
    return nRes;
  }

  /**
   * Visits a {@link instruction} node, whose children are the following :
   * <p>
   * f0 -> < ID ><br>
   * f1 -> ";"<br>
   * s: 1947544793<br>
   *
   * @param n - the node to visit
   * @param argu - the user argument 0
   * @return the user return information
   */
  @Override
  @NodeFieldsSignature({ 1947544793, JTB_SIG_INSTRUCTION, JTB_USER_INSTRUCTION })
  public R visit(final instruction n, final A argu) {
    R nRes = null;
    // f0 -> < ID >
    final Token n0 = n.f0;
    nRes = n0.accept(this, argu);
    // f1 -> ";"
    final Token n1 = n.f1;
    nRes = n1.accept(this, argu);
    return nRes;
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

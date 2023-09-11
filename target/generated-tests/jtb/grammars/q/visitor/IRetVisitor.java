/* Generated by JTB 1.5.1 */
package grammars.q.visitor;

import grammars.q.syntaxtree.*;
import grammars.q.Token;

/**
 * All "IRetVisitor" visitors must implement this interface.

 * @param <R> - The return type parameter
 */
public interface IRetVisitor<R> {

  /*
   * Base nodes visit methods
   */

  /**
   * Visits a {@link NodeChoice} node.
   *
   * @param n - the node to visit
   * @return the user return information
   */
  public R visit(final NodeChoice n);

  /**
   * Visits a {@link NodeList} node.
   *
   * @param n - the node to visit
   * @return the user return information
   */
  public R visit(final NodeList n);

  /**
   * Visits a {@link NodeListOptional} node.
   *
   * @param n - the node to visit
   * @return the user return information
   */
  public R visit(final NodeListOptional n);

  /**
   * Visits a {@link NodeOptional} node.
   *
   * @param n - the node to visit
   * @return the user return information
   */
  public R visit(final NodeOptional n);

  /**
   * Visits a {@link NodeSequence} node.
   *
   * @param n - the node to visit
   * @return the user return information
   */
  public R visit(final NodeSequence n);

  /**
   * Visits a {@link Token} node.
   *
   * @param n - the node to visit
   * @return the user return information
   */
  public R visit(final Token n);

  /*
   * User grammar generated visit methods
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
   * f6 -> jc_0()<br>
   * f7 -> < EOF ><br>
   * s: -1372830968<br>
   *
   * @param n - the node to visit
   * @return the user return information
   */
  public R visit(final classDeclaration n);

  /**
   * Visits a {@link className} node, whose child is the following :
   * <p>
   * f0 -> < ID ><br>
   * s: -1032372970<br>
   *
   * @param n - the node to visit
   * @return the user return information
   */
  public R visit(final className n);

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
   * @return the user return information
   */
  public R visit(final method n);

  /**
   * Visits a {@link methodName} node, whose child is the following :
   * <p>
   * f0 -> < ID ><br>
   * s: -1032372970<br>
   *
   * @param n - the node to visit
   * @return the user return information
   */
  public R visit(final methodName n);

  /**
   * Visits a {@link instruction} node, whose child is the following :
   * <p>
   * f0 -> . %0 #0 < ID > #1 ";"<br>
   * .. .. | %1 ","<br>
   * s: 119476985<br>
   *
   * @param n - the node to visit
   * @return the user return information
   */
  public R visit(final instruction n);

  /**
   * Visits a {@link bp_jual} node, whose child is the following :
   * <p>
   * f0 -> < ID ><br>
   * s: -1032372970<br>
   *
   * @param n - the node to visit
   * @return the user return information
   */
  public R visit(final bp_jual n);

  /**
   * Visits a {@link bp_hm} node, whose child is the following :
   * <p>
   * f0 -> < ID ><br>
   * s: -1032372970<br>
   *
   * @param n - the node to visit
   * @return the user return information
   */
  public R visit(final bp_hm n);

  /**
   * Visits a {@link jc_0} node, with no child :
   * <p>
   * s: 0<br>
   *
   * @param n - the node to visit
   * @return the user return information
   */
  public R visit(final jc_0 n);

}

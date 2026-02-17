package examples.java.ex1jtb.visitor;

import static examples.java.ex1jtb.syntaxtree.Eg1NodeConstants.*;

import examples.java.ex1jtb.syntaxtree.AdditiveExpression;
import examples.java.ex1jtb.syntaxtree.EgInteger;
import examples.java.ex1jtb.syntaxtree.Expression;
import examples.java.ex1jtb.syntaxtree.Identifier;
import examples.java.ex1jtb.syntaxtree.MultiplicativeExpression;
import examples.java.ex1jtb.syntaxtree.Start;
import examples.java.ex1jtb.syntaxtree.UnaryExpression;
import examples.java.ex1jtb.visitor.signature.NodeFieldsSignature;

/**
 * A simple dump visitor corresponding to the JJTree SimpleNode.dump() and others.<br>
 * The user nodes visit methods are overridden by adding the dump() call, incrementing/decrementing the
 * indentation, and for some stopping the walk-down.<br>
 * No need to check missing visit methods.
 */
public class DumpVisitor extends DepthFirstGenVisitor {
  
  /*
   * Added methods (come from JJTree examples)
   */
  
  @SuppressWarnings("javadoc") private int indent = 0;
  
  @SuppressWarnings("javadoc")
  private String indentString() {
    final StringBuffer sb = new StringBuffer();
    for (int i = 0; i < indent; ++i) {
      sb.append(' ');
    }
    return sb.toString();
  }
  
  @SuppressWarnings("javadoc")
  private void dump(final int nid, final String argu) {
    System.out.println(argu + indentString() + JTB_USER_NODE_NAME[nid]);
    return;
  }
  
  @SuppressWarnings("javadoc")
  private void dump(final String name, final String argu) {
    System.out.println(argu + indentString() + name);
    return;
  }
  
  /*
   * Copied then overriden user grammar generated visit methods added the dump() calls and ++/--indent, and
   * removed some walk-downs
   */
  
  /**
   * Visits a {@link Start} node, whose children are the following :
   * <p>
   * f0 -> Expression()<br>
   * f1 -> ";"<br>
   * s: 1859009853<br>
   *
   * @param n - the node to visit
   * @param argu - the user argument 0
   */
  @Override
  @NodeFieldsSignature(old_sig = 1859009853, new_sig = JTB_SIG_START, name = "Start")
  public void visit(final Start n, final String argu) {
    dump(JTB_USER_START, argu);
    ++indent;
    // f0 -> Expression()
    n.f0.accept(this, argu);
    // f1 -> ";"
    n.f1.accept(this, argu);
    --indent;
  }
  
  /**
   * Visits a {@link Expression} node, whose child is the following :
   * <p>
   * f0 -> AdditiveExpression()<br>
   * s: -762347234<br>
   *
   * @param n - the node to visit
   * @param argu - the user argument 0
   */
  @Override
  @NodeFieldsSignature(old_sig = -762347234, new_sig = JTB_SIG_EXPRESSION, name = "Expression")
  public void visit(final Expression n, final String argu) {
    dump(JTB_USER_EXPRESSION, argu);
    ++indent;
    // f0 -> AdditiveExpression()
    n.f0.accept(this, argu);
    --indent;
  }
  
  /**
   * Visits a {@link AdditiveExpression} node, whose children are the following :
   * <p>
   * f0 -> MultiplicativeExpression()<br>
   * f1 -> ( #0 ( %0 "+"<br>
   * .. .. . .. | %1 "-" )<br>
   * .. .. . #1 MultiplicativeExpression() )*<br>
   * s: -1807059397<br>
   *
   * @param n - the node to visit
   * @param argu - the user argument 0
   */
  @Override
  @NodeFieldsSignature(old_sig = -1807059397, new_sig = JTB_SIG_ADDITIVEEXPRESSION, name = "AdditiveExpression")
  public void visit(final AdditiveExpression n, final String argu) {
    dump(JTB_USER_ADDITIVEEXPRESSION, argu);
    ++indent;
    // f0 -> MultiplicativeExpression()
    n.f0.accept(this, argu);
    // f1 -> ( #0 ( %0 "+"
    // .. .. . .. | %1 "-" )
    // .. .. . #1 MultiplicativeExpression() )*
    n.f1.accept(this, argu);
    --indent;
  }
  
  /**
   * Visits a {@link MultiplicativeExpression} node, whose children are the following :
   * <p>
   * f0 -> UnaryExpression()<br>
   * f1 -> ( #0 ( %0 "*"<br>
   * .. .. . .. | %1 "/"<br>
   * .. .. . .. | %2 "%" )<br>
   * .. .. . #1 UnaryExpression() )*<br>
   * s: 853643830<br>
   *
   * @param n - the node to visit
   * @param argu - the user argument 0
   */
  @Override
  @NodeFieldsSignature(old_sig = 853643830, new_sig = JTB_SIG_MULTIPLICATIVEEXPRESSION, name = "MultiplicativeExpression")
  public void visit(final MultiplicativeExpression n, final String argu) {
    dump(JTB_USER_MULTIPLICATIVEEXPRESSION, argu);
    ++indent;
    // f0 -> UnaryExpression()
    n.f0.accept(this, argu);
    // f1 -> ( #0 ( %0 "*"
    // .. .. . .. | %1 "/"
    // .. .. . .. | %2 "%" )
    // .. .. . #1 UnaryExpression() )*
    n.f1.accept(this, argu);
    --indent;
  }
  
  /**
   * Visits a {@link UnaryExpression} node, whose child is the following :
   * <p>
   * f0 -> . %0 #0 "(" #1 Expression() #2 ")"<br>
   * .. .. | %1 Identifier()<br>
   * .. .. | %2 EgInteger()<br>
   * s: 190447292<br>
   *
   * @param n - the node to visit
   * @param argu - the user argument 0
   */
  @Override
  @NodeFieldsSignature(old_sig = 190447292, new_sig = JTB_SIG_UNARYEXPRESSION, name = "UnaryExpression")
  public void visit(final UnaryExpression n, final String argu) {
    dump(JTB_USER_UNARYEXPRESSION, argu);
    ++indent;
    // f0 -> . %0 #0 "(" #1 Expression() #2 ")"
    // .. .. | %1 Identifier()
    // .. .. | %2 EgInteger()
    n.f0.accept(this, argu);
    --indent;
  }
  
  /**
   * Visits a {@link Identifier} node, whose child is the following :
   * <p>
   * f0 -> < IDENTIFIER ><br>
   * s: -1580059612<br>
   *
   * @param n - the node to visit
   * @param argu - the user argument 0
   */
  @Override
  @NodeFieldsSignature(old_sig = -1580059612, new_sig = JTB_SIG_IDENTIFIER, name = "Identifier")
  public void visit(@SuppressWarnings("unused") final Identifier n, final String argu) {
    dump(JTB_USER_IDENTIFIER, argu);
    // no need to go further down
    // // f0 -> < IDENTIFIER >
    // n.f0.accept(this, argu);
  }
  
  /**
   * Visits a {@link EgInteger} node, whose child is the following :
   * <p>
   * f0 -> < INTEGER_LITERAL ><br>
   * s: -1048223857<br>
   *
   * @param n - the node to visit
   * @param argu - the user argument 0
   */
  @Override
  @NodeFieldsSignature(old_sig = -1048223857, new_sig = JTB_SIG_EGINTEGER, name = "EgInteger")
  public void visit(@SuppressWarnings("unused") final EgInteger n, final String argu) {
    dump("Integer", argu);
    // no need to go further down
    // // f0 -> < INTEGER_LITERAL >
    // n.f0.accept(this, argu);
  }
  
}

package examples.java.ex2jtb.visitor;

import static examples.java.ex2jtb.syntaxtree.NodeConstants.JTB_USER_NODE_NAME;
import static examples.java.ex2jtb.syntaxtree.NodeConstants.JTB_USER_ASTSTART;
import static examples.java.ex2jtb.syntaxtree.NodeConstants.JTB_SIG_ASTADDITIVEEXPRESSION;
import static examples.java.ex2jtb.syntaxtree.NodeConstants.JTB_SIG_ASTINTEGER;
import static examples.java.ex2jtb.syntaxtree.NodeConstants.JTB_SIG_ASTMULTIPLICATIVEEXPRESSION;
import static examples.java.ex2jtb.syntaxtree.NodeConstants.JTB_SIG_ASTMYID;
import static examples.java.ex2jtb.syntaxtree.NodeConstants.JTB_SIG_ASTSTART;
import static examples.java.ex2jtb.syntaxtree.NodeConstants.JTB_SIG_ASTUNARYEXPRESSION;
import static examples.java.ex2jtb.syntaxtree.NodeConstants.JTB_USER_ASTADDITIVEEXPRESSION;
import static examples.java.ex2jtb.syntaxtree.NodeConstants.JTB_USER_ASTINTEGER;
import static examples.java.ex2jtb.syntaxtree.NodeConstants.JTB_USER_ASTMULTIPLICATIVEEXPRESSION;
import static examples.java.ex2jtb.syntaxtree.NodeConstants.JTB_USER_ASTMYID;
import static examples.java.ex2jtb.syntaxtree.NodeConstants.JTB_USER_ASTUNARYEXPRESSION;
import examples.java.ex2jtb.visitor.signature.NodeFieldsSignature;
import examples.java.ex2jtb.syntaxtree.ASTAdditiveExpression;
import examples.java.ex2jtb.syntaxtree.ASTInteger;
import examples.java.ex2jtb.syntaxtree.ASTMultiplicativeExpression;
import examples.java.ex2jtb.syntaxtree.ASTMyID;
import examples.java.ex2jtb.syntaxtree.ASTStart;
import examples.java.ex2jtb.syntaxtree.ASTUnaryExpression;

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
   * Visits a {@link ASTStart} node, whose child is the following :
   * <p>
   * f0 -> ";"<br>
   * s: 2055660624<br>
   *
   * @param n - the node to visit
   * @param argu - the user argument 0
   */
  @Override
  @NodeFieldsSignature({
      2055660624, JTB_SIG_ASTSTART, JTB_USER_ASTSTART
  })
  public void visit(final ASTStart n, final String argu) {
    dump(JTB_USER_ASTSTART, argu);
    ++indent;
    // f0 -> ";"
    n.f0.accept(this, argu);
    --indent;
  }
  
  /**
   * Visits a {@link ASTAdditiveExpression} node, whose children are the following :
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
  @NodeFieldsSignature({
      -1807059397, JTB_SIG_ASTADDITIVEEXPRESSION, JTB_USER_ASTADDITIVEEXPRESSION
  })
  public void visit(final ASTAdditiveExpression n, final String argu) {
    if (n.f1.present()) {
      dump("Add", argu);
      ++indent;
      // f0 -> MultiplicativeExpression()
      n.f0.accept(this, argu);
      // f1 -> ( #0 ( %0 "+"
      // .. .. . .. | %1 "-" )
      // .. .. . #1 MultiplicativeExpression() )*
      n.f1.accept(this, argu);
      --indent;
    } else {
      // f0 -> MultiplicativeExpression()
      n.f0.accept(this, argu);
      // f1 -> ( #0 ( %0 "+"
      // .. .. . .. | %1 "-" )
      // .. .. . #1 MultiplicativeExpression() )*
      n.f1.accept(this, argu);
    }
  }
  
  /**
   * Visits a {@link ASTMultiplicativeExpression} node, whose children are the following :
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
  @NodeFieldsSignature({
      853643830, JTB_SIG_ASTMULTIPLICATIVEEXPRESSION, JTB_USER_ASTMULTIPLICATIVEEXPRESSION
  })
  public void visit(final ASTMultiplicativeExpression n, final String argu) {
    if (n.f1.present()) {
      dump("Mult", argu);
      ++indent;
      // f0 -> UnaryExpression()
      n.f0.accept(this, argu);
      // f1 -> ( #0 ( %0 "*"
      // .. .. . .. | %1 "/"
      // .. .. . .. | %2 "%" )
      // .. .. . #1 UnaryExpression() )*
      n.f1.accept(this, argu);
      --indent;
    } else {
      n.f0.accept(this, argu);
      // f1 -> ( #0 ( %0 "*"
      // .. .. . .. | %1 "/"
      // .. .. . .. | %2 "%" )
      // .. .. . #1 UnaryExpression() )*
      n.f1.accept(this, argu);
      
    }
  }
  
  /**
   * Visits a {@link ASTUnaryExpression} node, whose child is the following :
   * <p>
   * f0 -> . %0 #0 "(" #1 Expression() #2 ")" //cp ExpansionChoices element<br>
   * .. .. | %1 MyID() //cp ExpansionChoices element<br>
   * .. .. | %2 Integer() //cp ExpansionChoices last<br>
   * s: 953155740<br>
   *
   * @param n - the node to visit
   * @param argu - the user argument 0
   */
  @Override
  @NodeFieldsSignature({
      953155740, JTB_SIG_ASTUNARYEXPRESSION, JTB_USER_ASTUNARYEXPRESSION
  })
  public void visit(final ASTUnaryExpression n, final String argu) {
    dump(JTB_USER_ASTUNARYEXPRESSION, argu);
    ++indent;
    // f0 -> . %0 #0 "(" #1 Expression() #2 ")"
    // .. .. | %1 MyID()
    // .. .. | %2 Integer()
    n.f0.accept(this, argu);
    --indent;
  }
  
  /**
   * Visits a {@link ASTMyID} node, whose child is the following :
   * <p>
   * f0 -> < IDENTIFIER ><br>
   * s: -1580059612<br>
   *
   * @param n - the node to visit
   * @param argu - the user argument 0
   */
  @Override
  @NodeFieldsSignature({
      -1580059612, JTB_SIG_ASTMYID, JTB_USER_ASTMYID
  })
  public void visit(final ASTMyID n, final String argu) {
    dump("Identifier: " + n.f0.image, argu);
    ++indent;
    // f0 -> < IDENTIFIER >
    n.f0.accept(this, argu);
    --indent;
  }
  
  /**
   * Visits a {@link ASTInteger} node, whose child is the following :
   * <p>
   * f0 -> < INTEGER_LITERAL ><br>
   * s: -1048223857<br>
   *
   * @param n - the node to visit
   * @param argu - the user argument 0
   */
  @Override
  @NodeFieldsSignature({
      -1048223857, JTB_SIG_ASTINTEGER, JTB_USER_ASTINTEGER
  })
  public void visit(@SuppressWarnings("unused") final ASTInteger n, final String argu) {
    dump("Integer", argu);
    // no need to go further down
    // // f0 -> < INTEGER_LITERAL >
    // n.f0.accept(this, argu);
  }
  
}

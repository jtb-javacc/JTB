package EDU.purdue.jtb.visitor;

import static EDU.purdue.jtb.misc.Globals.PRINT_CLASS_COMMENT;
import static EDU.purdue.jtb.misc.Globals.nodesPackageName;

import java.util.Iterator;

import EDU.purdue.jtb.misc.Spacing;
import EDU.purdue.jtb.syntaxtree.ClassOrInterfaceBody;
import EDU.purdue.jtb.syntaxtree.INode;
import EDU.purdue.jtb.syntaxtree.IdentifierAsString;
import EDU.purdue.jtb.syntaxtree.ImportDeclaration;
import EDU.purdue.jtb.syntaxtree.NodeListOptional;
import EDU.purdue.jtb.syntaxtree.StringLiteral;


/**
 * The {@link CompilationUnitPrinter} visitor<br>
 * determines if an import statement for the syntax tree package is needed in the grammar file,<br>
 * prints the compilation unit (with appropriate tool kit methods), inserting the import
 * statements if necessary.
 */
@SuppressWarnings("javadoc")
class CompilationUnitPrinter extends JavaPrinter {

  private GlobalDataBuilder gdbv;

  /**
   * Constructor, with a given buffer and a default indentation.
   * 
   * @param aSb - the buffer to print into (will be allocated if null)
   * @param aSPC - the Spacing indentation object (will be allocated and set to a default if null)
   * @param gdbv TODO
   */
  CompilationUnitPrinter(final StringBuilder aSb, final Spacing aSPC, GlobalDataBuilder gdbv) {
    super(aSb, aSPC);
    this.gdbv = gdbv;
  }

  /*
   * Convenience methods
   */

  /**
   * Prints into the current buffer a node class comment and a new line.
   * 
   * @param n - the node for the node class comment
   */
  @Override
  void oneNewLine(final INode n) {
    sb.append(nodeClassComment(n)).append(LS);
  }

  /**
   * Prints into the current buffer a node class comment, an extra given comment, and a new line.
   * 
   * @param n - the node for the node class comment
   * @param str - the extra comment
   */
  @Override
  void oneNewLine(final INode n, final String str) {
    sb.append(nodeClassComment(n, str)).append(LS);
  }

  /**
   * Prints twice into the current buffer a node class comment and a new line.
   * 
   * @param n - the node for the node class comment
   */
  @Override
  void twoNewLines(final INode n) {
    oneNewLine(n);
    oneNewLine(n);
  }

  /**
   * Returns a node class comment (a //!! followed by the node class short name if global flag
   * set, nothing otherwise).
   * 
   * @param n - the node for the node class comment
   * @return the node class comment
   */
  String nodeClassComment(final INode n) {
    if (PRINT_CLASS_COMMENT) {
      final String s = n.toString();
      final int b = s.lastIndexOf('.') + 1;
      final int e = s.indexOf('@');
      if (b == -1 || e == -1)
        return " //!! " + s;
      else
        return " //!! " + s.substring(b, e);
    } else
      return "";
  }

  /**
   * Returns a node class comment with an extra comment (a //!! followed by the node class short
   * name plus the extra comment if global flag set, nothing otherwise).
   * 
   * @param n - the node for the node class comment
   * @param str - the extra comment
   * @return the node class comment
   */
  String nodeClassComment(final INode n, final String str) {
    if (PRINT_CLASS_COMMENT)
      return nodeClassComment(n).concat(" ").concat(str);
    else
      return "";
  }

  /*
   * User grammar generated and overridden visit methods below
   */

  /**
   * Visits the {@link ImportDeclaration}<br>
   * f0 -> "import"<br>
   * f1 -> [ "static" ]<br>
   * f2 -> Name(null)<br>
   * f3 -> [ "." "*" ]<br>
   * f4 -> ";"<br>
   * 
   * @param n - the node to process
   */
  void printImports(final NodeListOptional n) {
    if ("".equals(nodesPackageName))
      return;
    boolean foundTreeImport = false;
    final StringBuilder mainSB = sb;
    final StringBuilder newSB = new StringBuilder(128);
    sb = newSB;
    final String npns = nodesPackageName + ".*;";
    for (final Iterator<?> e = n.elements(); e.hasNext();) {
      final ImportDeclaration dec = (ImportDeclaration) e.next();

      newSB.setLength(0);
      dec.accept(this);
      final String s = newSB.toString();
      mainSB.append(s).append(nodeClassComment(n, " Y")).append(LS);

      if (s.equals(npns))
        foundTreeImport = true;
    }
    sb = mainSB;
    if (!foundTreeImport) {
      sb.append("import ").append(nodesPackageName).append(".*;");
      oneNewLine(n, "Z");
    }
  }

  /**
   * Visits a {@link ImportDeclaration} node, whose children are the following :
   * <p>
   * f0 -> "import"<br>
   * f1 -> [ "static" ]<br>
   * f2 -> Name()<br>
   * f3 -> [ #0 "." #1 "*" ]<br>
   * f4 -> ";"<br>
   * 
   * @param n - the node to visit
   */
  @Override
  public void visit(final ImportDeclaration n) {
    // f0 -> "import"
    n.f0.accept(this);
    sb.append(" ");
    // f1 -> [ "static" ]
    if (n.f1.present()) {
      n.f1.accept(this);
      sb.append(" ");
    }
    // f2 -> Name()
    n.f2.accept(this);
    // f3 -> [ #0 "." #1 "*" ]
    if (n.f3.present()) {
      n.f3.accept(this);
    }
    // f4 -> ";"
    n.f4.accept(this);
  }

  /**
   * Visits a {@link ClassOrInterfaceBody} node, whose children are the following :
   * <p>
   * f0 -> "{"<br>
   * f1 -> ( ClassOrInterfaceBodyDeclaration() )*<br>
   * f2 -> "}"<br>
   * 
   * @param n - the node to visit
   */
  @Override
  public void visit(final ClassOrInterfaceBody n) {
    // f0 -> "{"
    n.f0.accept(this);
    // add return variables declarations
    final int rvds = gdbv.getRetVarDecl().size();
    if (rvds > 0) {
      spc.update(+1);
      twoNewLines(n);
      sb.append(spc.spc);
      sb.append("/*");
      oneNewLine(n);
      sb.append(spc.spc);
      sb.append(" * JTB generated return variables declarations");
      oneNewLine(n);
      sb.append(spc.spc);
      sb.append(" */");
      twoNewLines(n);
      sb.append(spc.spc);
      for (int i = 0; i < rvds; i++) {
        // comment
        sb.append(gdbv.getRetVarDecl().get(i));
        oneNewLine(n, "b");
        sb.append(spc.spc);
        // declaration
        sb.append(gdbv.getRetVarDecl().get(++i));
        if (i < rvds - 2) {
          twoNewLines(n);
          sb.append(spc.spc);
        }
      }
      gdbv.getRetVarDecl().clear();
      spc.update(-1);
    }
    // f1 -> ( ClassOrInterfaceBodyDeclaration() )*
    if (n.f1.present()) {
      spc.update(+1);
      twoNewLines(n);
      sb.append(spc.spc);
      for (final Iterator<INode> e = n.f1.elements(); e.hasNext();) {
        e.next().accept(this);
        oneNewLine(n, "c");
        if (e.hasNext()) {
          oneNewLine(n, "d");
          sb.append(spc.spc);
        }
      }
      spc.update(-1);
    }
    sb.append(spc.spc);
    // f2 -> "}"
    n.f2.accept(this);
    oneNewLine(n, "e");
  }

  /**
   * Visits a {@link IdentifierAsString} node, whose children are the following :
   * <p>
   * f0 -> < IDENTIFIER ><br>
   * 
   * @param n - the node to visit
   */
  @Override
  public void visit(final IdentifierAsString n) {
    n.f0.accept(this);
  }

  /**
   * Visits a {@link StringLiteral} node, whose children are the following :
   * <p>
   * f0 -> < STRING_LITERAL ><br>
   * 
   * @param n - the node to visit
   */
  @Override
  public void visit(final StringLiteral n) {
    n.f0.accept(this);
  }

}


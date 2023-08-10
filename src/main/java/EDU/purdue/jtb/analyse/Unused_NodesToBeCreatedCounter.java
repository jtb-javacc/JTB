package EDU.purdue.jtb.analyse;

import static EDU.purdue.jtb.analyse.GlobalDataBuilder.DONT_CREATE;
import static EDU.purdue.jtb.parser.syntaxtree.NodeConstants.JTB_SIG_EXPANSION;
import static EDU.purdue.jtb.parser.syntaxtree.NodeConstants.JTB_SIG_EXPANSIONCHOICES;
import static EDU.purdue.jtb.parser.syntaxtree.NodeConstants.JTB_SIG_EXPANSIONUNIT;
import static EDU.purdue.jtb.parser.syntaxtree.NodeConstants.JTB_SIG_EXPANSIONUNITTCF;
import static EDU.purdue.jtb.parser.syntaxtree.NodeConstants.JTB_SIG_REGULAREXPRESSION;
import static EDU.purdue.jtb.parser.syntaxtree.NodeConstants.JTB_USER_EXPANSION;
import static EDU.purdue.jtb.parser.syntaxtree.NodeConstants.JTB_USER_EXPANSIONCHOICES;
import static EDU.purdue.jtb.parser.syntaxtree.NodeConstants.JTB_USER_EXPANSIONUNIT;
import static EDU.purdue.jtb.parser.syntaxtree.NodeConstants.JTB_USER_EXPANSIONUNITTCF;
import static EDU.purdue.jtb.parser.syntaxtree.NodeConstants.JTB_USER_REGULAREXPRESSION;
import EDU.purdue.jtb.common.Messages;
import EDU.purdue.jtb.common.ProgrammaticError;
import EDU.purdue.jtb.parser.syntaxtree.Expansion;
import EDU.purdue.jtb.parser.syntaxtree.ExpansionChoices;
import EDU.purdue.jtb.parser.syntaxtree.ExpansionUnit;
import EDU.purdue.jtb.parser.syntaxtree.ExpansionUnitTCF;
import EDU.purdue.jtb.parser.syntaxtree.INode;
import EDU.purdue.jtb.parser.syntaxtree.IdentifierAsString;
import EDU.purdue.jtb.parser.syntaxtree.NodeChoice;
import EDU.purdue.jtb.parser.syntaxtree.NodeList;
import EDU.purdue.jtb.parser.syntaxtree.NodeListOptional;
import EDU.purdue.jtb.parser.syntaxtree.NodeOptional;
import EDU.purdue.jtb.parser.syntaxtree.NodeSequence;
import EDU.purdue.jtb.parser.syntaxtree.RegularExpression;
import EDU.purdue.jtb.parser.visitor.DepthFirstIntVisitor;
import EDU.purdue.jtb.parser.visitor.signature.NodeFieldsSignature;

/**
 * The {@link Unused_NodesToBeCreatedCounter} visitor walks down an {@link ExpansionChoices} or an {@link Expansion}
 * or an {@link ExpansionUnit} and tells how many user nodes must be created. This is used to create the
 * appropriate base nodes which can disappear or change when user nodes are indicated locally or globally not
 * to be created.
 *
 * @author Marc Mazas
 * @version 1.5.0 : 02/2017 : MMa : created
 */
public class Unused_NodesToBeCreatedCounter extends DepthFirstIntVisitor {
  
  /** The {@link GlobalDataBuilder} visitor */
  private final GlobalDataBuilder gdbv;
  
  /**
   * Constructor
   *
   * @param Agdbv - The {@link GlobalDataBuilder} visitor to use
   */
  public Unused_NodesToBeCreatedCounter(final GlobalDataBuilder Agdbv) {
    gdbv = Agdbv;
  }
  
  /**
   * Counts the nodes to be created below a an {@link ExpansionChoices}.
   *
   * @param n - an {@link ExpansionChoices}
   * @return the number of nodes to be created
   */
  public int count(final ExpansionChoices n) {
    return visit(n);
  }
  
  /**
   * Counts the nodes to be created below a an {@link Expansion}.
   *
   * @param n - an {@link Expansion}
   * @return the number of nodes to be created
   */
  public int count(final Expansion n) {
    return visit(n);
  }
  
  /**
   * Counts the nodes to be created below a an {@link ExpansionUnit}.
   *
   * @param n - an {@link ExpansionUnit}
   * @return the number of nodes to be created
   */
  public int count(final ExpansionUnit n) {
    return visit(n);
  }
  
  /** Constant 0 */
  private final int ZERO = 0;
  
  /** Constant 1 */
  private final int ONE = 1;
  
  /**
   * Visits a {@link ExpansionChoices} node, whose children are the following :
   * <p>
   * f0 -> Expansion()<br>
   * f1 -> ( #0 "|" #1 Expansion() )*<br>
   * s: -1726831935<br>
   *
   * @param n - the node to visit
   */
  @Override
  @NodeFieldsSignature({
      -1726831935, JTB_SIG_EXPANSIONCHOICES, JTB_USER_EXPANSIONCHOICES
  })
  public int visit(final ExpansionChoices n) {
    int nbExpansions = 0;
    // f0 -> Expansion()
    if (n.f0.accept(this) > 0) {
      nbExpansions++;
    }
    // f1 -> ( #0 "|" #1 Expansion() )*
    final NodeListOptional n1 = n.f1;
    if (n1.present()) {
      for (int i = 0; i < n1.size(); i++) {
        final NodeSequence seq = (NodeSequence) n1.elementAt(i);
        // #1 Expansion()
        if (seq.elementAt(1).accept(this) > 0) {
          nbExpansions++;
        }
      }
    }
    return nbExpansions;
  }
  
  /**
   * Visits a {@link Expansion} node, whose children are the following :
   * <p>
   * f0 -> ( #0 "LOOKAHEAD" #1 "(" #2 LocalLookahead() #3 ")" )?<br>
   * f1 -> ( ExpansionUnit() )+<br>
   * s: -2134365682<br>
   *
   * @param n - the node to visit
   */
  @Override
  @NodeFieldsSignature({
      -2134365682, JTB_SIG_EXPANSION, JTB_USER_EXPANSION
  })
  public int visit(final Expansion n) {
    int nbExpansionUnits = 0;
    // f1 -> ( ExpansionUnit() )+
    final NodeList n1 = n.f1;
    for (int i = 0; i < n1.size(); i++) {
      if (n1.elementAt(i).accept(this) > 0) {
        nbExpansionUnits++;
      }
    }
    return nbExpansionUnits;
  }
  
  /**
   * Visits a {@link ExpansionUnit} node, whose child is the following :
   * <p>
   * f0 -> . %0 #0 "LOOKAHEAD" #1 "(" #2 LocalLookahead() #3 ")"<br>
   * .. .. | %1 Block()<br>
   * .. .. | %2 #0 "[" #1 ExpansionChoices() #2 "]"<br>
   * .. .. | %3 ExpansionUnitTCF()<br>
   * .. .. | %4 #0 [ $0 PrimaryExpression() $1 "=" ]<br>
   * .. .. . .. #1 ( &0 $0 IdentifierAsString() $1 Arguments()<br>
   * .. .. . .. .. . .. $2 [ "!" ]<br>
   * .. .. . .. .. | &1 $0 RegularExpression()<br>
   * .. .. . .. .. . .. $1 [ ?0 "." ?1 < IDENTIFIER > ]<br>
   * .. .. . .. .. . .. $2 [ "!" ] )<br>
   * .. .. | %5 #0 "(" #1 ExpansionChoices() #2 ")"<br>
   * .. .. . .. #3 ( &0 "+"<br>
   * .. .. . .. .. | &1 "*"<br>
   * .. .. . .. .. | &2 "?" )?<br>
   * s: 1116287061<br>
   *
   * @param n - the node to visit
   */
  @Override
  @NodeFieldsSignature({
      1116287061, JTB_SIG_EXPANSIONUNIT, JTB_USER_EXPANSIONUNIT
  })
  public int visit(final ExpansionUnit n) {
    final NodeChoice nch = n.f0;
    final INode ich = nch.choice;
    switch (nch.which) {
    case 0:
      return ZERO;
    case 1:
      return ZERO;
    case 2:
      // %2 #0 "[" #1 ExpansionChoices() #2 "]"
      // #1 ExpansionChoices()
      return ((NodeSequence) ich).elementAt(1).accept(this) > 0 ? ONE : ZERO;
    case 3:
      // %3 ExpansionUnitTCF()
      // return ich.accept(this) > 0 ? ONE : ZERO;
      return ich.accept(this);
    case 4:
      // %4 #0 [ $0 PrimaryExpression() $1 "=" ]
      // .. #1 ( &0 $0 IdentifierAsString() $1 Arguments()
      // .. .. .. $2 [ "!" ]
      // .. .. | &1 $0 RegularExpression()
      // .. .. .. $1 [ ?0 "." ?1 < IDENTIFIER > ]
      // .. .. .. $2 [ "!" ] )
      final NodeSequence seq = (NodeSequence) ich;
      // #1 ( &0 $0 IdentifierAsString() $1 Arguments()
      // .. .. $2 [ "!" ]
      // .. | &1 $0 RegularExpression()
      // .. .. $1 [ ?0 "." ?1 < IDENTIFIER > ]
      // .. .. $2 [ "!" ] )
      final NodeChoice nch1 = (NodeChoice) seq.elementAt(1);
      final INode ich1 = nch1.choice;
      switch (nch1.which) {
      case 0:
        // &0 $0 IdentifierAsString() $1 Arguments()
        // .. $2 [ "!" ]
        if (((NodeOptional) ((NodeSequence) ich1).elementAt(2)).present()) {
          return ZERO;
        }
        final IdentifierAsString ias = (IdentifierAsString) ((NodeSequence) ich1).elementAt(0);
        if (gdbv.getNotTbcNodesHM().containsKey(ias.f0.tokenImage)) {
          return ZERO;
        } else {
          return ONE;
        }
      case 1:
        // &1 $0 RegularExpression()
        // .. $1 [ ?0 "." ?1 < IDENTIFIER > ]
        // .. $2 [ "!" ]
        if (((NodeOptional) ((NodeSequence) ich1).elementAt(2)).present()) {
          // do not create node so do not walk down
          return ZERO;
        }
        // $0 RegularExpression()
        return ((NodeSequence) ich1).elementAt(0).accept(this) > 0 ? ONE : ZERO;
      default:
        final String msg = "Invalid nch1.which = " + String.valueOf(nch1.which);
        Messages.hardErr(msg);
        throw new ProgrammaticError(msg);
      }
    case 5:
      // %5 #0 "(" #1 ExpansionChoices() #2 ")"
      // .. #3 ( &0 "+"
      // .. .. | &1 "*"
      // .. .. | &2 "?" )?
      // #1 ExpansionChoices()
      return ((NodeSequence) ich).elementAt(1).accept(this) > 0 ? ONE : ZERO;
    default:
      final String msg = "Invalid nch.which = " + String.valueOf(nch.which);
      Messages.hardErr(msg);
      throw new ProgrammaticError(msg);
    }
  }
  
  /**
   * Visits a {@link ExpansionUnitTCF} node, whose children are the following :
   * <p>
   * f0 -> "try"<br>
   * f1 -> "{"<br>
   * f2 -> ExpansionChoices()<br>
   * f3 -> "}"<br>
   * f4 -> ( #0 "catch" #1 "("<br>
   * .. .. . #2 [ "final" ]<br>
   * .. .. . #3 Name() #4 < IDENTIFIER > #5 ")" #6 Block() )*<br>
   * f5 -> [ #0 "finally" #1 Block() ]<br>
   * s: -1347962218<br>
   *
   * @param n - the node to visit
   */
  @Override
  @NodeFieldsSignature({
      -1347962218, JTB_SIG_EXPANSIONUNITTCF, JTB_USER_EXPANSIONUNITTCF
  })
  public int visit(final ExpansionUnitTCF n) {
    // even with an ExpansionChoices with no choice but a sequence of 2 or more nodes, we return just one node
    // so it returns either a single NodeChoice of 1 or more choices,
    // or a single NodeSequence of 1 or more nodes,
    // or no node at all
    // the NodeChoice or NodeSequence should not be "merged" within the upper NodeChoice or NodeSequence,
    // the TCF acts like separators
    return n.f2.accept(this) > 0 ? ONE : ZERO;
  }
  
  /**
   * Visits a {@link RegularExpression} node, whose child is the following :
   * <p>
   * f0 -> . %0 StringLiteral()<br>
   * .. .. | %1 #0 "<"<br>
   * .. .. . .. #1 [ $0 [ "#" ]<br>
   * .. .. . .. .. . $1 IdentifierAsString() $2 ":" ]<br>
   * .. .. . .. #2 ComplexRegularExpressionChoices() #3 ">"<br>
   * .. .. | %2 #0 "<" #1 IdentifierAsString() #2 ">"<br>
   * .. .. | %3 #0 "<" #1 "EOF" #2 ">"<br>
   * s: 1719627151<br>
   *
   * @param n - the node to visit
   */
  @Override
  @NodeFieldsSignature({
      1719627151, JTB_SIG_REGULAREXPRESSION, JTB_USER_REGULAREXPRESSION
  })
  public int visit(final RegularExpression n) {
    // note : if we came down to here it means we have to create the node
    // f0 -> . %0 StringLiteral()
    // .. .. | %1 #0 "<"
    // .. .. . .. #1 [ $0 [ "#" ]
    // .. .. . .. .. . $1 IdentifierAsString() $2 ":" ]
    // .. .. . .. #2 ComplexRegularExpressionChoices() #3 ">"
    // .. .. | %2 #0 "<" #1 IdentifierAsString() #2 ">"
    // .. .. | %3 #0 "<" #1 "EOF" #2 ">"
    final NodeChoice nch = n.f0;
    final INode ich = nch.choice;
    switch (nch.which) {
    case 0:
      // %0 StringLiteral()
      return ONE;
    case 1:
      // %1 #0 "<"
      // .. #1 [ $0 [ "#" ]
      // .. .. . $1 IdentifierAsString() $2 ":" ]
      // .. #2 ComplexRegularExpressionChoices() #3 ">"
      return ONE;
    case 2:
      // %2 #0 "<" #1 IdentifierAsString() #2 ">"
      final NodeSequence seq9 = (NodeSequence) ich;
      // #1 IdentifierAsString()
      final INode seq11 = seq9.elementAt(1);
      final String ias = ((IdentifierAsString) seq11).f0.tokenImage;
      return DONT_CREATE.equals(gdbv.getTokenHM().get(ias)) || gdbv.getNotTbcNodesHM().containsKey(ias) ? ZERO
          : ONE;
    case 3:
      // %3 #0 "<" #1 "EOF" #2 ">"
      // Annotator creates a token node for < EOF > (but it can be suppressed)
      return ONE;
    default:
      final String msg = "Invalid nch.which = " + String.valueOf(nch.which);
      Messages.hardErr(msg);
      throw new ProgrammaticError(msg);
    }
  }
  
}

/* Generated by JTB 1.5.1 */
package grammars.q;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import grammars.q.syntaxtree.INode;
import grammars.q.visitor.IRetVisitor;
import grammars.q.visitor.IRetArguVisitor;
import grammars.q.visitor.IVoidVisitor;
import grammars.q.visitor.IVoidArguVisitor;

/**
 * Represents a JavaCC single token in the grammar and a JTB corresponding node.<br>
 * The class holds all the fields and methods generated normally by JavaCC, plus the ones required by JTB.<br>
 * If the "-tk" JTB option is used, it also contains an ArrayList of preceding special tokens.<br>
 */
public class Token  implements INode, java.io.Serializable {

  /* JavaCC members declarations */

  /**
   * The version identifier for this Serializable class.<br>
   * Increment only if the <i>serialized</i> form of the class changes.
   */
  private static final long serialVersionUID = 1L;

  /**
   * An integer that describes the kind of this token.<br>
   * This numbering system is determined by JavaCCParser,<br>
   * and a table of these numbers is stored in the class &l;ParserName&gt;Constants.java.
   */
  public int kind;

  /** The line number of the first character of this token. */
  public int beginLine;

  /** The column number of the first character of this token. */
  public int beginColumn;

  /** The line number of the last character of this token. */
  public int endLine;

  /** The column number of the last character of this token. */
  public int endColumn;

  /** The string image of the token. */
  public String image;

  /**
   * For a regular token, a reference to the next regular token from the input stream,<br>
   * or null if this is the last token from the input stream, or if the token manager<br>
   * has not (yet) read a regular token beyond this one.<p>
   * For a special token, a reference to the special token that just after it<br>
   * (without an intervening regular token) if it exists, or null otherwise.
   */
  public Token next;

  /**
   * For a regular token, a reference to the special token just before to this token,<br>
   * (without an intervening regular token), or null if there is no such special token.<p>
   * For a special token, a reference to the special token just after it<br>
   * (without an intervening regular token) if it exists, or null otherwise.
   */
  public Token specialToken;

  /**
   * An optional attribute value of the Token.<br>
   * Tokens which are not used as syntactic sugar will often contain meaningful values<br>
   * that will be used later on by the compiler or interpreter.<br>
   * This attribute value is often different from the image.<br>
   * Any subclass of Token that actually wants to return a non-null value<br>
   * can override this method as appropriate.<br>
   * Not used in JTB.
   * 
   * @return a value
   */
  public Object getValue() {
    return null;
  }

  /**
   * No-argument constructor.
   */
  public Token() {
    /* empty */
  }

  /**
   * Constructs a new {@link Token} for the specified kind, with a null image.<br>
   * Not used in JTB nor JavaCC.
   *
   * @param ki - the token kind
   */
  public Token(final int ki) {
    this(ki, null);
  }

  /**
   * Constructs a {@link Token} with a given kind and image.
   *
   * @param ki - the token kind
   * @param im - the token image
   */
  public Token(final int ki, final String im) {
    kind = ki;
    image = im;
  }

  /**
   * Factory method used by JavaCC to create a new {@link Token}<br>
   * (which is also a JTB node).
   * By default returns a new {@link Token} object.
   * You can override it to create and return subclass objects<br>
   * based on the value of ofKind.<br>
   * Simply add the cases to the switch for all those special cases.<br>
   * For example, if you have a subclass of Token called IDToken<br>
   * that you want to create if ofKind is ID, simply add something like:<br>
   * case MyParserConstants.ID : return new IDToken(ofKind, image);<br>
   * to the following switch statement.<br>
   * Then you can cast matchedToken variable to the appropriate type<br>
   * and use it in your lexical actions.
   *
   * @param ofKind - the token kind
   * @param image - the token image
   *
   * @return a new Token
   */
  public static Token newToken(int ofKind, String image) {
    switch(ofKind) {
      default:
        return new Token(ofKind, image);
    }
  }

  /**
   * Factory method calling {@link Token#newToken(int, String)} with a null image.
   *
   * @param ofKind - the token kind
   *
   * @return a new Token
   */
  public static Token newToken(int ofKind) {
    return newToken(ofKind, null);
  }

  /**
   * @return the token image
   */
  @Override
  public String toString() {
    return image;
  }

  /* JTB members declarations */

  /** The list of special tokens. TODO add explanation */
  public List<Token> specialTokens;

  /**
  * Gets the special token in the special tokens list at a given position.
  *
  * @param i - the special token's position
  * @return the special token
  */
  public Token getSpecialAt(final int i) {
    if (specialTokens == null)
      throw new NoSuchElementException("No specialTokens in token"); //$NON-NLS-1$
    return specialTokens.get(i);
  }

  /**
  * @return the number of special tokens
  */
  public int numSpecials() {
    if (specialTokens == null)
      return 0;
    return specialTokens.size();
  }

  /**
  * Adds a special token to the special tokens list.
  *
  * @param s - the special token to add
  */
  public void addSpecial(final Token s) {
    if (specialTokens == null)
     specialTokens = new ArrayList<>();
    specialTokens.add(s);
  }

  /**
  * Trims the special tokens list.
  */
  public void trimSpecials() {
    if (specialTokens == null)
      return;
    ((ArrayList<Token>) specialTokens).trimToSize();
  }

  /**
  * Returns the string of the special tokens of the current {@link Token},
  * taking in account a given indentation.
  * @param spc - the indentation
  * @return the string representing the special tokens list
  */
  public String getSpecials(final String spc) {
    if (specialTokens == null)
      return ""; //$NON-NLS-1$
    int stLastLine = -1;
    final StringBuilder buf = new StringBuilder(64);
    boolean hasEol = false;
    for (final Iterator<Token> e = specialTokens.iterator(); e.hasNext();) {
      final Token st = e.next();
      final char c = st.image.charAt(st.image.length() - 1);
      hasEol = c == '\n' || c == '\r';
      if (stLastLine != -1)
        // not first line 
        if (stLastLine != st.beginLine) {
          // if not on the same line as the previous
          for (int i = stLastLine + 1; i < st.beginLine; i++)
            // keep blank lines
          buf.append(LS);
          buf.append(spc);
        } else
          // on the same line as the previous
          buf.append(' ');
      buf.append(st.image);
      if (!hasEol && e.hasNext())
        // not a single line comment and not the last one
        buf.append(LS);
      stLastLine = st.endLine;
    }
    // keep the same number of blank lines before the current non special
    for (int i = stLastLine + (hasEol ? 1 : 0); i < beginLine; i++) {
      buf.append(LS);
      if (i != beginLine - 1)
      buf.append(spc);
    }
    // indent if the current non special is not on the same line
    if (stLastLine != beginLine)
      buf.append(spc);
    return buf.toString();
  }

  /**
  * Returns the string of the special tokens and the normal token of the current {@link Token},
  * taking in account a given indentation.
  *
  * @param spc - the indentation
  * @return the string representing the special tokens list and the token
  */
  public String withSpecials(final String spc) {
    return withSpecials(spc, null);
  }

  /**
  * Returns the string of the special tokens and the normal token of the current {@link Token},
  * taking in account a given indentation and a given assignment.
  *
  * @param spc - the indentation
  * @param var - the variable assignment to be inserted
  * @return the string representing the special tokens list and the token
  */
  public String withSpecials(final String spc, final String var) {
    final String specials = getSpecials(spc);
    int len = specials.length() + 1;
    if (len == 1)
      return (var == null ? image : var + image);
    if (var != null)
      len += var.length();
    StringBuilder buf = new StringBuilder(len + image.length());
    buf.append(specials);
    // see if needed to add a space
    int stLastLine = -1;
    if (specialTokens != null)
    for (Token e : specialTokens) {
      stLastLine = e.endLine;
    }
    if (stLastLine == beginLine)
      buf.append(' ');
    if (var != null)
      buf.append(var);
    buf.append(image);
    return buf.toString();
  }

  /*
   * Visitors accept methods (no -novis option, visitors specification : Void,void,None;VoidArgu,void,A;Ret,R,None;RetArgu,R,A)
   */

  /**
   * Accepts a {@link IRetVisitor} visitor with user return data.
   *
   * @param <R> - the return type parameter
   * @param vis - the visitor
   * @return the user Return data
   */
  @Override
  public <R> R accept(final IRetVisitor<R> vis) {
    return vis.visit(this);
  }

  /**
   * Accepts a {@link IRetArguVisitor} visitor with user return and argument data.
   *
   * @param <R> - the return type parameter
   * @param <A> - The argument 0 type parameter
   * @param vis - the visitor
   * @param argu - the user Argument data
   * @return the user Return data
   */
  @Override
  public <R, A> R accept(final IRetArguVisitor<R, A> vis, final A argu) {
    return vis.visit(this, argu);
  }

  /**
   * Accepts a {@link IVoidVisitor} visitor} visitor with user return data.
   *
   * @param vis - the visitor
   */
  @Override
  public void accept(final IVoidVisitor vis) {
    vis.visit(this);
  }

  /**
   * Accepts a {@link IVoidArguVisitor} visitor with user argument data.
   *
   * @param <A> - The argument 0 type parameter
   * @param vis - the visitor
   * @param argu - the user Argument data
   */
  @Override
  public <A> void accept(final IVoidArguVisitor<A> vis, final A argu) {
    vis.visit(this, argu);
  }

}

/* Copyright (c) 2006, Sun Microsystems, Inc.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *     * Redistributions of source code must retain the above copyright notice,
 *       this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the Sun Microsystems, Inc. nor the names of its
 *       contributors may be used to endorse or promote products derived from
 *       this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
 * THE POSSIBILITY OF SUCH DAMAGE.
 */
package EDU.purdue.jtb.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Describes JavaCC productions.
 * 
 * @author Marc Mazas
 * @version 1.4.0 : 05/2009 : MMa : adapted to JavaCC v4.2 grammar
 * @version 1.4.8 : 12/2014 : MMa : improved javadoc
 */
public class NormalProduction {

  /** The number of the construct that corresponds most closely to this node */
  private int                   column;
  /** The line number of the construct that corresponds most closely to this node */
  private int                   line;
  /** The NonTerminal nodes which refer to this production */
  private List<Object>          parents               = new ArrayList<Object>();
  /** The access modifier of this production */
  private String                accessMod;
  /** The name of the non-terminal of this production */
  private String                lhs;
  /** The tokens that make up the return type of this production */
  private final List<Token>     return_type_tokens    = new ArrayList<Token>();
  /** The tokens that make up the parameters of this production */
  private final List<Token>     parameter_list_tokens = new ArrayList<Token>();
  /**
   * Each entry in this list is a list of tokens that represents an exception in the throws list of
   * this production. This list does not include ParseException which is always thrown.
   */
  private List<List<Token>>     throws_list           = new ArrayList<List<Token>>();
  /** The RHS of this production. Not used for JavaCodeProduction */
  private Expansion_            expansion;
  /** This boolean flag is true if this production can expand to empty */
  private boolean               emptyPossible         = false;
  /** A list of all non-terminals that this one can expand to without having to consume any tokens */
  private NormalProduction[]    leftExpansions        = new NormalProduction[10];
  /** An index that shows how many pointers exist */
  int                           leIndex               = 0;
  /**
   * The following variable is used to maintain state information for the left-recursion
   * determination algorithm: It is initialized to 0, and set to -1 if this node has been visited in
   * a pre-order walk, and then it is set to 1 if the pre-order walk of the whole graph from this
   * node has been traversed. i.e., -1 indicates partially processed, and 1 indicates fully
   * processed.
   */
  private int                   walkStatus            = 0;
  /** The first token from the input stream that represent this production */
  private Token                 lastToken;
  /** The last token from the input stream that represent this production */
  private Token                 firstToken;
  /** The OS line separator */
  protected static final String EOL                   = System.getProperty("line.separator", "\n");

  /**
   * @param indent - the level of indentation
   * @return a number of spaces twice the level of indentation
   */
  protected StringBuilder dumpPrefix(final int indent) {
    final StringBuilder sb = new StringBuilder(2 * indent);
    for (int i = 0; i < indent; i++)
      sb.append("  ");
    return sb;
  }

  /**
   * @return the class name without the package name
   */
  protected String getSimpleName() {
    final String name = getClass().getName();
    return name.substring(name.lastIndexOf(".") + 1); // strip the package name
  }

  /**
   * @param indent - the level of indentation
   * @param alreadyDumped - a collection of already dumped classes
   * @return the formatted dump (indentation, class, lhs, expansion)
   */
  public StringBuilder dump(final int indent, final Set<Object> alreadyDumped) {
    final StringBuilder sb = dumpPrefix(indent).append(System.identityHashCode(this)).append(' ')
                                               .append(getSimpleName()).append(' ')
                                               .append(getLhs());
    if (!alreadyDumped.contains(this)) {
      alreadyDumped.add(this);
      if (getExpansion() != null) {
        sb.append(EOL).append(getExpansion().dump(indent + 1, alreadyDumped));
      }
    }
    return sb;
  }

  /**
   * @param ln - the line to set
   */
  public final void setLine(final int ln) {
    line = ln;
  }

  /**
   * @return the line
   */
  public final int getLine() {
    return line;
  }

  /**
   * @param cl - the column to set
   */
  public final void setColumn(final int cl) {
    column = cl;
  }

  /**
   * @return the column
   */
  public final int getColumn() {
    return column;
  }

  /**
   * @param pa - the parents to set
   */
  final void setParents(final List<Object> pa) {
    parents = pa;
  }

  /**
   * @return the parents
   */
  final List<Object> getParents() {
    return parents;
  }

  /**
   * @param am - the accessMod to set
   */
  public final void setAccessMod(final String am) {
    accessMod = am;
  }

  /**
   * @return the accessMod
   */
  public final String getAccessMod() {
    return accessMod;
  }

  /**
   * @param l - the lhs to set
   */
  public final void setLhs(final String l) {
    lhs = l;
  }

  /**
   * @return the lhs
   */
  public final String getLhs() {
    return lhs;
  }

  /**
   * @return the return_type_tokens
   */
  public final List<Token> getReturnTypeTokens() {
    return return_type_tokens;
  }

  /**
   * @return the parameter_list_tokens
   */
  public final List<Token> getParameterListTokens() {
    return parameter_list_tokens;
  }

  /**
   * @param tl - the throws_list to set
   */
  public final void setThrowsList(final List<List<Token>> tl) {
    throws_list = tl;
  }

  /**
   * @return the throws_list
   */
  public final List<List<Token>> getThrowsList() {
    return throws_list;
  }

  /**
   * @param ex - the expansion to set
   */
  public final void setExpansion(final Expansion_ ex) {
    expansion = ex;
  }

  /**
   * @return the expansion
   */
  public final Expansion_ getExpansion() {
    return expansion;
  }

  /**
   * @param ep - the emptyPossible to set
   */
  final void setEmptyPossible(final boolean ep) {
    emptyPossible = ep;
  }

  /**
   * @return the emptyPossible
   */
  final boolean isEmptyPossible() {
    return emptyPossible;
  }

  /**
   * @param le - the leftExpansions to set
   */
  final void setLeftExpansions(final NormalProduction[] le) {
    leftExpansions = le;
  }

  /**
   * @return the leftExpansions
   */
  final NormalProduction[] getLeftExpansions() {
    return leftExpansions;
  }

  /**
   * @param ws - the walkStatus to set
   */
  final void setWalkStatus(final int ws) {
    walkStatus = ws;
  }

  /**
   * @return the walkStatus
   */
  final int getWalkStatus() {
    return walkStatus;
  }

  /**
   * @param ft - the firstToken to set
   */
  public final void setFirstToken(final Token ft) {
    firstToken = ft;
  }

  /**
   * @return the firstToken
   */
  public final Token getFirstToken() {
    return firstToken;
  }

  /**
   * @param lt - the lastToken to set
   */
  public final void setLastToken(final Token lt) {
    lastToken = lt;
  }

  /**
   * @return the lastToken
   */
  public final Token getLastToken() {
    return lastToken;
  }
}

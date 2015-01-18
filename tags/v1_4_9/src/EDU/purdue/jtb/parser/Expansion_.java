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

import java.util.List;
import java.util.Set;

/**
 * Describes expansions - entities that may occur on the right hand sides of productions. This is
 * the base class of a bunch of other more specific classes.
 * 
 * @author Marc Mazas
 * @version 1.4.0 : 05/2009 : MMa : adapted to JavaCC v4.2 grammar
 * @version 1.4.8 : 12/2014 : MMa : improved javadoc ; removed overriding hashCode() method
 */
public class Expansion_ {

  /** The column number of the construct that corresponds most closely to this node */
  private int        column;
  /** The line number of the construct that corresponds most closely to this node */
  private int        line;

  //    /**
  //     * A override of {@link Object#hashCode()} to be more deterministic. This uses the line and column
  //     * fields to generate an arbitrary number - we assume that this method is called only after line
  //     * and column are set to their actual values.
  //     */
  //    @Override
  //    public int hashCode() {
  //      return getLine() + getColumn();
  //    }

  /**
   * An internal name for this expansion. This is used to generate parser routines.
   */
  String             internal_name       = "";
  /**
   * The parser routines are generated in three phases. The generation of the second and third phase
   * are on demand only, and the third phase can be recursive. This variable is used to keep track
   * of the expansions for which phase 3 generations have been already added to a list so that the
   * recursion can be terminated.
   */
  boolean            phase3done          = false;
  /**
   * The parent of this expansion node. In case this is the top level expansion of the production it
   * is a reference to the production node otherwise it is a reference to another Expansion node. In
   * case this is the top level of a lookahead expansion,then the parent is null.
   */
  public Object      parent;
  /**
   * The ordinal of this node with respect to its parent.
   */
  int                ordinal;
  /**
   * To avoid right-recursive loops when calculating follow sets, we use a generation number which
   * indicates if this expansion was visited by
   * {@link LookaheadWalk#genFollowSet(List, Expansion_, long)} in the same generation. New
   * generations are obtained by incrementing the static counter {@link #nextGenerationIndex}, and
   * the current generation is stored in the non-static variable {@link #myGeneration}.
   */
  public static long nextGenerationIndex = 1;
  /**
   * To avoid right-recursive loops when calculating follow sets, we use a generation number which
   * indicates if this expansion was visited by
   * {@link LookaheadWalk#genFollowSet(List, Expansion_, long)} in the same generation. New
   * generations are obtained by incrementing the static counter {@link #nextGenerationIndex}, and
   * the current generation is stored in the non-static variable {@link #myGeneration}.
   */
  public long        myGeneration        = 0;
  /**
   * This flag is used for bookkeeping by the minimumSize method in class {@link ParseEngine}.
   */
  public boolean     inMinimumSize       = false;

  /** Reinitializes */
  public static void reInit() {
    nextGenerationIndex = 1;
  }

  /**
   * @return the name of the class without the package part
   */
  private String getSimpleName() {
    final String name = getClass().getName();
    return name.substring(name.lastIndexOf(".") + 1); // strip the package name
  }

  /** {@inheritDoc} */
  @Override
  public String toString() {
    return "[" + getLine() + "," + getColumn() + " " + System.identityHashCode(this) + " " +
           getSimpleName() + "]";
  }

  /** The OS line separator */
  protected static final String EOL = System.getProperty("line.separator", "\n");

  /**
   * @param indent - the number of indentation level
   * @return a {@link StringBuilder} with twice the number of spaces given by the parameter
   */
  protected StringBuilder dumpPrefix(final int indent) {
    final StringBuilder sb = new StringBuilder(128);
    for (int i = 0; i < indent; i++)
      sb.append("  ");
    return sb;
  }

  /**
   * @param indent - the number of spaces for indenting
   * @param alreadyDumped - a Set of objects already dumped (unused)
   * @return the formatted string dumping the class
   */
  public StringBuilder dump(final int indent,
                            @SuppressWarnings("unused") final Set<Object> alreadyDumped) {
    final StringBuilder value = dumpPrefix(indent).append(System.identityHashCode(this))
                                                  .append(" ").append(getSimpleName());
    return value;
  }

  /**
   * @param cl - the column to set
   */
  final void setColumn(final int cl) {
    column = cl;
  }

  /**
   * @return the column
   */
  final int getColumn() {
    return column;
  }

  /**
   * @param ln - the line to set
   */
  final void setLine(final int ln) {
    line = ln;
  }

  /**
   * @return the line
   */
  final int getLine() {
    return line;
  }

  /**
   * Subclass type
   */
  EXP_TYPE expType;

  /**
   * Subclasses enum type
   */
  public enum EXP_TYPE {
    @SuppressWarnings("javadoc")
    CHOICE, @SuppressWarnings("javadoc")
    SEQUENCE, @SuppressWarnings("javadoc")
    ONE_OR_MORE, @SuppressWarnings("javadoc")
    ZERO_OR_MORE, @SuppressWarnings("javadoc")
    ZERO_OR_ONE, @SuppressWarnings("javadoc")
    LOOKAHEAD, @SuppressWarnings("javadoc")
    TRY_BLOCK, @SuppressWarnings("javadoc")
    R_CHOICE, @SuppressWarnings("javadoc")
    R_SEQUENCE, @SuppressWarnings("javadoc")
    R_ONE_OR_MORE, @SuppressWarnings("javadoc")
    R_ZERO_OR_MORE, @SuppressWarnings("javadoc")
    R_ZERO_OR_ONE, @SuppressWarnings("javadoc")
    R_REPETITION_RANGE
  };
}

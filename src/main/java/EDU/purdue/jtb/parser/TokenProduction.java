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

/**
 * Describes the various regular expression productions.
 *
 * @author Marc Mazas
 * @version 1.4.0 : 05/2009 : MMa : adapted to JavaCC v4.2 grammar
 * @version 1.4.8 : 12/2014 : MMa : improved javadoc
 */
public class TokenProduction {

  /** Definitions of constants that identify the kind of regular expression production this is */
  @SuppressWarnings("javadoc")
  public static final int      TOKEN      = 0, SKIP = 1, MORE = 2, SPECIAL = 3;
  /** The image of the above constants */
  public static final String[] kindImage  = { "TOKEN", "SKIP", "MORE", "SPECIAL" };
  /** The starting column of this token production */
  private int                  column;
  /** The starting line of this token production */
  private int                  line;
  /**
   * The states in which this regular expression production exists. If this array is null, then
   * "<*>" has been specified and this regular expression exists in all states. However, this null
   * value is replaced by a String array that includes all lexical state names during the
   * semanticization phase
   */
  public String[]              lexStates;
  /** The kind of this token production - TOKEN, SKIP, MORE, or SPECIAL */
  public int                   kind;
  /**
   * The list of regular expression specifications that comprise this production. Each entry is a
   * "RegExprSpec"
   */
  public List<RegExprSpec_>    respecs    = new ArrayList<>();
  /**
   * This is true if this corresponds to a production that actually appears in the input grammar.
   * Otherwise (if this is created to describe a regular expression that is part of the BNF) this is
   * set to false
   */
  public boolean               isExplicit = true;
  /**
   * This is true if case is to be ignored within the regular expressions of this token production
   */
  public boolean               ignoreCase = false;
  /** The first token from the input stream that represent this production */
  public Token                 firstToken;
  /** The last token from the input stream that represent this production */
  public Token                 lastToken;

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
}

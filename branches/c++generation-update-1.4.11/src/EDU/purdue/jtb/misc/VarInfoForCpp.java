/**
 * Copyright (c) 2004,2005 UCLA Compilers Group.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  Redistributions of source code must retain the above copyright
 *  notice, this list of conditions and the following disclaimer.
 *
 *  Redistributions in binary form must reproduce the above copyright
 *  notice, this list of conditions and the following disclaimer in the
 *  documentation and/or other materials provided with the distribution.
 *
 *  Neither UCLA nor the names of its contributors may be used to endorse
 *  or promote products derived from this software without specific prior
 *  written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 **/

/*
 * All files in the distribution of JTB, The Java Tree Builder are
 * Copyright 1997, 1998, 1999 by the Purdue Research Foundation of Purdue
 * University.  All rights reserved.
 *
 * Redistribution and use in source and binary forms are permitted
 * provided that this entire copyright notice is duplicated in all
 * such copies, and that any documentation, announcements, and
 * other materials related to such distribution and use acknowledge
 * that the software was developed at Purdue University, West Lafayette,
 * Indiana by Kevin Tao and Jens Palsberg.  No charge may be made
 * for copies, derivations, or distributions of this material
 * without the express written consent of the copyright holder.
 * Neither the name of the University nor the name of the author
 * may be used to endorse or promote products derived from this
 * material without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND WITHOUT ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, WITHOUT LIMITATION, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR ANY PARTICULAR PURPOSE.
 */

package EDU.purdue.jtb.misc;

/**
 * Class VarInfo stores information for a variable : type, name, initializer, and constructs its
 * declaration.
 * 
 * @author Marc Mazas
 * @version 1.4.0 : 05-08/2009 : MMa : enhanced
 * @version 1.4.3 : 03/2010 : MMa : added node declarations initialization in all cases
 * @version 1.4.6 : 01/2011 : FA/MMa : added -va and -npfx and -nsfx options
 */
@SuppressWarnings("javadoc")
public class VarInfoForCpp implements VarInfo {

  /** The variable resultType */
  private final String type;
  /** The variable type */
  private final String production;
  /** The variable name */
  private final String name;
  /** The variable initializer */
  private final String initializer;
  /** The whole variable declaration */
  private String       declaration;

  /**
   * @param result - the variable type
   * @param name - the variable name
   */
  public VarInfoForCpp(final NodeClass result, final String name) {
    this(result.getName(), name, null);
  }

  /**
   * Creates a new instance with no initializer.
   * 
   * @param type - the variable type
   * @param name - the variable name
   */
  public VarInfoForCpp(final String type, final String name) {
    this(type, name, null);
  }

  /**
   * Creates a new instance with an initializer.
   * 
   * @param type - the variable type
   * @param name - the variable name
   * @param init - the variable initializer
   */
  public VarInfoForCpp(final NodeClass type, final String name, final String init) {
    this.production = null;
    this.type = type.getName();
    this.name = name;
    initializer = init;
    declaration = null;
  }

  /**
   * Creates a new instance with an initializer.
   * 
   * @param type - the variable type
   * @param name - the variable name
   * @param init - the variable initializer
   */
  public VarInfoForCpp(final String type, final String name, final String init) {
    this.type = type;
    this.production = null;
    this.name = name;
    initializer = init;
    declaration = null;
  }

  public VarInfoForCpp(String type, String production, String name, String initialization) {
    this.type = type;
    this.production = production;
    this.name = name;
    this.initializer = initialization;
    declaration = null;
  }

  /**
   * @return the variable type
   */
  @Override
  public String getType() {
    return type;
  }

  @Override
  public String getProduction() {
    return production;
  }

  /**
   * @return the variable name
   */
  @Override
  public String getName() {
    return name;
  }

  /**
   * @return the variable initializer
   */
  public String getInitializer() {
    return initializer;
  }

  /**
   * Same as {@link #generateNodeDeclaration()}.
   * 
   * @return the whole variable declaration string
   */
  @Override
  public String toString() {
    return generateNodeDeclaration();
  }

  /**
   * Generates and stores, if not yet done, and returns the variable declaration string.
   * 
   * @return the variable declaration string
   */
  @Override
  public String generateNodeDeclaration() {
    if (declaration == null) {
      final StringBuilder buf = new StringBuilder(64);
      // always initialize even if initializer is null
      String fullName = null;
      fullName = Globals.getFixedName(type);
      buf.append(fullName);
      buf.append('*');
      buf.append(" ").append(name).append(" = ").append(initializer).append(";");
      declaration = buf.toString();
    }
    return declaration;
  }
}

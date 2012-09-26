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

import java.util.Hashtable;

/**
 * Class FieldNameGenerator generates the names of the fields of node classes depending on whether
 * the "-f" parameter for descriptive field names has been used or not.
 * <p>
 * By default, field will be named "fX" where X ascends from 0 to the number of children - 1.
 * <p>
 * If the "-f" parameter is used, the names will be based on the classes of the children.<br>
 * For example, a child of class "WhileStatement" will be called "whileStatementX" (note the
 * lowercase first letter), where X is either nothing if this is the first WhileStatement in this
 * production or 1 through the number of children - 1 for any additional children of the same type.
 * <p>
 *
 * @author Marc Mazas, mmazas@sopragroup.com
 * @version 1.4.0 : 05/2009 : MMa : adapted to JavaCC v4.2 grammar and JDK 1.5
 */
public class FieldNameGenerator {

  /** The field number */
  private int                        fieldNum = 0;

  /**
   * nameTable is used for the "-f" option (Descriptive field names).<br>
   * Key = field names in use in current production<br>
   * Value = int value of the last suffix used
   */
  private Hashtable<String, Integer> nameTable;

  /**
   * Constructor. Creates an Hashtable of descriptive field names if the corresponding option flag
   * is set.
   */
  public FieldNameGenerator() {
    if (Globals.descriptiveFieldNames)
      nameTable = new Hashtable<String, Integer>();
  }

  /**
   * Resets the instance.
   */
  public void reset() {
    fieldNum = 0;
    if (Globals.descriptiveFieldNames)
      nameTable.clear();
  }

  /**
   * Generates the field name used in the comment for a given class field name.
   *
   * @param fieldName the class field name
   * @return the comment field name
   */
  public String genCommentFieldName(final String fieldName) {
    if (!Globals.descriptiveFieldNames)
      return "f" + String.valueOf(fieldNum++);
    else {
      final String prefix = varNameForClass(fieldName);
      Integer suffix = nameTable.get(prefix);

      if (suffix == null) {
        suffix = new Integer(0);
        nameTable.put(prefix, suffix);
        return prefix;
      } else {
        suffix = new Integer(suffix.intValue() + 1);
        nameTable.put(prefix, suffix);
        return prefix + suffix.toString();
      }
    }
  }

  /**
   * Returns a variable name for the name of the given class.<br>
   * Ex : class is "TipTop", variable will be "tipTop".
   *
   * @param className the class name
   * @return the variable name
   */
  public String varNameForClass(final String className) {
    //      final StringBuffer buf = new StringBuffer(
    //         String.valueOf(Character.toLowerCase(className.charAt(0))));
    //      buf.append(className.substring(1, className.length()));
    //      return buf.toString();
    final StringBuffer buf = new StringBuffer(className);
    buf.setCharAt(0, Character.toLowerCase(className.charAt(0)));
    return buf.toString();
  }

  /**
   * Gets the node type corresponding to the modifier.
   *
   * @param mod the modifier
   * @return the node type
   */
  public String getNameForMod(final String mod) {
    if (mod.equals("+"))
      return Globals.nodeListName;
    else if (mod.equals("*"))
      return Globals.nodeListOptName;
    else if (mod.equals("?"))
      return Globals.nodeOptName;
    else {
      Messages.hardErr("Illegal EBNF modifier in " + "ExpansionUnit: mod = " + mod);
      return "";
    }
  }
}

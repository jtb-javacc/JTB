/**
 * Copyright (c) 2004,2005 UCLA Compilers Group. All rights reserved. Redistribution and use in source and
 * binary forms, with or without modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this list of conditions and the
 * following disclaimer. Redistributions in binary form must reproduce the above copyright notice, this list
 * of conditions and the following disclaimer in the documentation and/or other materials provided with the
 * distribution. Neither UCLA nor the names of its contributors may be used to endorse or promote products
 * derived from this software without specific prior written permission. THIS SOFTWARE IS PROVIDED BY THE
 * COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE
 * GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 **/
/*
 * All files in the distribution of JTB, The Java Tree Builder are Copyright 1997, 1998, 1999 by the Purdue
 * Research Foundation of Purdue University. All rights reserved. Redistribution and use in source and binary
 * forms are permitted provided that this entire copyright notice is duplicated in all such copies, and that
 * any documentation, announcements, and other materials related to such distribution and use acknowledge that
 * the software was developed at Purdue University, West Lafayette, Indiana by Kevin Tao and Jens Palsberg. No
 * charge may be made for copies, derivations, or distributions of this material without the express written
 * consent of the copyright holder. Neither the name of the University nor the name of the author may be used
 * to endorse or promote products derived from this material without specific prior written permission. THIS
 * SOFTWARE IS PROVIDED ``AS IS'' AND WITHOUT ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, WITHOUT
 * LIMITATION, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR ANY PARTICULAR PURPOSE.
 */
package EDU.purdue.jtb.misc;

/**
 * Class Spacing is used for of pretty printing to provide necessary indentation.
 *
 * @author Marc Mazas, mmazas@sopragroup.com
 * @version 1.4.0 : 05-08/2009 : MMa : adapted to JavaCC v4.2 grammar and JDK 1.5
 */
public class Spacing {

  /** the indentation amount */
  public final int INDENT_AMT;
  /** the indentation string */
  public String    spc         = "";
  /** the indentation level */
  public int       indentLevel = 0;

  /**
   * Constructor.
   *
   * @param indentAmt the indentation amount
   */
  public Spacing(final int indentAmt) {
    INDENT_AMT = indentAmt;
  }

  /**
   * Resets the instance.
   */
  public void reset() {
    spc = "";
    indentLevel = 0;
  }

  /**
   * Returns the indentation string corresponding to the indentation amount and level.
   */
  @Override
  public String toString() {
    return spc;
  }

  /**
   * Updates the indentation.
   *
   * @param numIndentLvls the (positive or negative) indentation level delta.
   */
  public void updateSpc(final int numIndentLvls) {
    indentLevel += numIndentLvls;
    if (numIndentLvls < 0)
      spc = spc.substring(-1 * numIndentLvls * INDENT_AMT);
    else if (numIndentLvls > 0) {
      final StringBuffer buf = new StringBuffer(spc);
      for (int i = 0; i < numIndentLvls * INDENT_AMT; ++i)
        buf.append(" ");
      spc = buf.toString();
    }
  }
}

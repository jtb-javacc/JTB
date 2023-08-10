/**
 * Copyright (c) 2004,2005 UCLA Compilers Group. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided
 * that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this list of conditions and the
 * following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the
 * following disclaimer in the documentation and/or other materials provided with the distribution.
 *
 * Neither UCLA nor the names of its contributors may be used to endorse or promote products derived from this
 * software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 * PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 **/

/*
 * All files in the distribution of JTB, The Java Tree Builder are Copyright 1997, 1998, 1999 by the Purdue
 * Research Foundation of Purdue University. All rights reserved.
 *
 * Redistribution and use in source and binary forms are permitted provided that this entire copyright notice
 * is duplicated in all such copies, and that any documentation, announcements, and other materials related to
 * such distribution and use acknowledge that the software was developed at Purdue University, West Lafayette,
 * Indiana by Kevin Tao and Jens Palsberg. No charge may be made for copies, derivations, or distributions of
 * this material without the express written consent of the copyright holder. Neither the name of the
 * University nor the name of the author may be used to endorse or promote products derived from this material
 * without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND WITHOUT ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, WITHOUT
 * LIMITATION, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR ANY PARTICULAR PURPOSE.
 */

package EDU.purdue.jtb.common;

import static EDU.purdue.jtb.common.Constants.INDENT_AMT;
import EDU.purdue.jtb.parser.syntaxtree.INode;

/**
 * Class {@link JavaBranchPrinter} is not itself a visitor but it uses a {@link JavaPrinter} visitor to visit
 * a java code branch of the tree and returns a pretty printed string representation of the subtree.
 * <p>
 * Class maintains state (for a grammar), and not supposed to be run in parallel threads (on the same
 * grammar).
 * </p>
 *
 * @author Marc Mazas
 * @version 1.4.0 : 05-08/2009 : MMa : adapted to JavaCC v4.2 grammar and JDK 1.5
 */
public class JavaBranchPrinter {
  
  /** The global JTB options */
  private final JTBOptions    jopt;
  /** The buffer to print into */
  private final StringBuilder sb;
  /** The indentation object */
  private final Spacing       spc;
  /** The {@link JavaPrinter} visitor */
  private final JavaPrinter   jpv;
  
  /**
   * Constructs a new instance with a default allocated buffer and a given indentation object.
   *
   * @param aJopt - the JTB options
   * @param aSpc - an indentation
   */
  public JavaBranchPrinter(final JTBOptions aJopt, final Spacing aSpc) {
    jopt = aJopt;
    sb = new StringBuilder(512);
    spc = aSpc == null ? new Spacing(INDENT_AMT) : aSpc;
    jpv = new JavaPrinter(jopt, sb, spc);
  }
  
  /**
   * Visits a given (java code) node branch of the tree and returns a pretty printed string representation of
   * the subtree.<br>
   * Implementation note : it reuses a class allocated StringBuilder buffer, which is therefore overwritten on
   * a next call.
   *
   * @param aJavaNode - the node to walk down and pretty print
   * @return the pretty print in a reused StringBuilder buffer
   */
  public StringBuilder genJavaBranch(final INode aJavaNode) {
    sb.setLength(0);
    jpv.reset(sb, spc);
    aJavaNode.accept(jpv);
    return sb;
  }
  
  /**
   * Visits a given (java code) node branch of the tree and returns a pretty printed string representation of
   * the subtree.<br>
   * Implementation note : it reuses a class allocated StringBuilder buffer, which is therefore overwritten on
   * a next call.
   *
   * @param aJavaNode - the node to walk down and pretty print
   * @param noDebugComments - true to suppress debug comments, false otherwise
   * @return the pretty print in a reused StringBuilder buffer
   */
  public StringBuilder genJavaBranch(final INode aJavaNode, final boolean noDebugComments) {
    sb.setLength(0);
    jpv.reset(sb, spc);
    jpv.noDebugComments = noDebugComments;
    aJavaNode.accept(jpv);
    jpv.noDebugComments = false;
    return sb;
  }
  
}

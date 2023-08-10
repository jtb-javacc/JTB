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

import static EDU.purdue.jtb.common.Constants.PROG_NAME;
import static EDU.purdue.jtb.common.Constants.SUPPORT;

/**
 * Class {@link Messages} handles messages (informations, warnings and fatal errors), printing a message to
 * the user and handling it appropriately.
 * <p>
 * Class maintains state, and is not supposed to be run in parallel threads (on the same grammar).
 * </p>
 *
 * @author Marc Mazas
 * @version 1.4.0 : 05-08/2009 : MMa : enhanced
 * @version 1.4.7 : 09/2012 : MMa : fixed missing "soft error" label ; added column numbers
 * @version 1.4.8 : 10/2014 : MMa : fixed extra space before column number for "soft error"
 * @version 1.4.10 : 04/2015 : MMa : removed System.exit() in hardErr
 */
public class Messages {
  
  /** The number of errors */
  private int numErrors   = 0;
  /** The number of warnings */
  private int numWarnings = 0;
  /** The number of informations */
  private int numInfos    = 0;
  
  /**
   * Prints on System.err the number of informations, warnings and errors
   */
  public void printSummary() {
    System.err.println(numInfos + " informations, " + numWarnings + " warnings, " + numErrors + " errors.");
  }
  
  /**
   * Prints on System.out an information text.
   *
   * @param s - the information text
   */
  public void info(final String s) {
    info(s, -1, 0);
  }
  
  /**
   * Prints on System.out an information text and its line number.
   *
   * @param s - the information text
   * @param lineNum - the information line number
   * @param colNum - the information column number
   */
  public void info(final String s, final int lineNum, final int colNum) {
    System.err.flush();
    System.out.println(PROG_NAME + ": info: "
        + (lineNum == -1 ? "" : "(" + Integer.toString(lineNum) + "," + Integer.toString(colNum) + "): ")
        + s);
    ++numInfos;
  }
  
  /**
   * Prints on System.err a warning text.
   *
   * @param s - the warning text
   */
  public void warning(final String s) { // NO_UCD (use default)
    warning(s, -1, 0);
  }
  
  /**
   * Prints on System.err a warning text and its line number.
   *
   * @param s - the warning text
   * @param lineNum - the warning line number
   * @param colNum - the warning column number
   */
  public void warning(final String s, final int lineNum, final int colNum) {
    System.err.flush();
    System.out.println(PROG_NAME + ": warning: "
        + (lineNum == -1 ? "" : "(" + Integer.toString(lineNum) + "," + Integer.toString(colNum) + "): ")
        + s);
    ++numWarnings;
  }
  
  /**
   * Prints on System.err a soft (non fatal) error text.
   *
   * @param s - an error text
   */
  public void softErr(final String s) {
    softErr(s, -1, -1);
  }
  
  /**
   * Prints on System.err a soft (non fatal) error text and its line number.
   *
   * @param s - the error text
   * @param lineNum - the error line number
   * @param colNum - the error column number
   */
  public void softErr(final String s, final int lineNum, final int colNum) {
    System.out.flush();
    System.err.println(PROG_NAME + ": soft error: "
        + (lineNum == -1 ? "" : "(" + Integer.toString(lineNum) + "," + Integer.toString(colNum) + "): ")
        + s);
    ++numErrors;
  }
  
  /**
   * Prints on System.err a fatal error message and the stack trace.
   *
   * @param s - a message
   */
  public static void hardErr(final String s) {
    System.out.flush();
    System.err.println();
    System.err.println(PROG_NAME + ": unexpected program error: " + s);
    System.err.println("Please report this to " + SUPPORT);
    System.err.println();
    new Throwable().printStackTrace();
  }
  
  /**
   * Prints on System.err a fatal error message and the stack trace.
   *
   * @param s - a message
   * @param t - a Throwable
   */
  public static void hardErr(final String s, final Throwable t) {
    System.err.println();
    System.err.println(PROG_NAME + ":  unexpected program error:  " + s);
    System.err.println("Please report this to " + SUPPORT);
    System.err.println();
    t.printStackTrace();
  }
  
  /**
   * @return the number of informations
   */
  public int infoCount() { // NO_UCD (unused code)
    return numInfos;
  }
  
  /**
   * @return the number of warnings
   */
  public int warningCount() {
    return numWarnings;
  }
  
  /**
   * @return the number of errors
   */
  public int errorCount() {
    return numErrors;
  }
  
  /**
   * Resets to zero the number of errors and warnings.
   */
  public void resetCounts() {
    numErrors = numWarnings = numInfos = 0;
  }
}

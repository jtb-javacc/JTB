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

import static EDU.purdue.jtb.misc.Globals.SUPPORT;
import static EDU.purdue.jtb.misc.Globals.jtbInputFileName;

/**
 * Class Messages handles messages (informations, warnings and fatal errors), printing a message to
 * the user and handling it appropriately.
 * 
 * @author Marc Mazas
 * @version 1.4.0 : 05-08/2009 : MMa : enhanced
 */
public class Messages {

  /** The number of errors */
  private static int numErrors   = 0;
  /** The number of warnings */
  private static int numWarnings = 0;
  /** The number of informations */
  private static int numInfos    = 0;

  /**
   * Prints on System.err the number of informations, warnings and errors
   */
  public static void printSummary() {
    System.err.println(numInfos + " informations, " + numWarnings + " warnings, " + numErrors +
                       " errors.");
  }

  /**
   * Prints on System.out an information text.
   * 
   * @param s - an information text
   */
  public static void info(final String s) {
    info(s, -1);
  }

  /**
   * Prints on System.out an information text and its line number.
   * 
   * @param s - an information text
   * @param lineNum - a warning line number
   */
  public static void info(final String s, final int lineNum) {
    if (lineNum == -1)
      System.out.println(jtbInputFileName + ":  info:  " + s);
    else
      System.out.println(jtbInputFileName + " (" + Integer.toString(lineNum) + "):  info:  " + s);

    ++numInfos;
  }

  /**
   * Prints on System.err a warning text.
   * 
   * @param s - a warning text
   */
  public static void warning(final String s) {
    warning(s, -1);
  }

  /**
   * Prints on System.err a warning text and its line number.
   * 
   * @param s - a warning text
   * @param lineNum - a warning line number
   */
  public static void warning(final String s, final int lineNum) {
    if (lineNum == -1)
      System.err.println(jtbInputFileName + ":  warning:  " + s);
    else
      System.err.println(jtbInputFileName + " (" + Integer.toString(lineNum) + "):  warning:  " + s);

    ++numWarnings;
  }

  /**
   * Prints on System.err a soft (non fatal) error text.
   * 
   * @param s - an error text
   */
  public static void softErr(final String s) {
    softErr(s, -1);
  }

  /**
   * Prints on System.err a soft (non fatal) error text and its line number.
   * 
   * @param s - an error text
   * @param lineNum - an error line number
   */
  public static void softErr(final String s, final int lineNum) {
    if (lineNum == -1)
      System.err.println(jtbInputFileName + ":  soft error:  " + s);
    else
      System.err.println(jtbInputFileName + " (" + Integer.toString(lineNum) + "):  " + s);

    ++numErrors;
  }

  /**
   * Prints on System.err a message.
   * 
   * @param s - a message
   */
  public static void notice(final String s) {
    System.err.println(jtbInputFileName + ":  " + s);
  }

  /**
   * Prints on System.err a fatal error message, its stack trace, and terminates the program.
   * 
   * @param s - a message
   */
  public static void hardErr(final String s) {
    System.err.println(jtbInputFileName + ":  unexpected program error:  " + s);
    System.err.println();
    System.err.println("Please report this to " + SUPPORT);
    System.err.println();

    try {
      throw new Exception();
    }
    catch (final Exception e) {
      e.printStackTrace();
      System.err.println();
      System.exit(-1);
    }
  }

  /**
   * Prints on System.err a fatal error message, its stack trace, and terminates the program.
   * 
   * @param t - a Throwable
   */
  public static void hardErr(final Throwable t) {
    System.err.println(jtbInputFileName + ":  unexpected program error:  " + t.getMessage());
    System.err.println();
    System.err.println("Please report this to " + SUPPORT);
    System.err.println();
    //    t.printStackTrace();
    System.exit(-1);
  }

  /**
   * @return the number of informations
   */
  public static int infoCount() {
    return numInfos;
  }

  /**
   * @return the number of warnings
   */
  public static int warningCount() {
    return numWarnings;
  }

  /**
   * @return the number of errors
   */
  public static int errorCount() {
    return numErrors;
  }

  /**
   * Sets the number of errors.
   * 
   * @param i - the number of errors
   */
  public static void setErrorCount(final int i) {
    numErrors = i;
  }

  /**
   * Sets the number of warnings.
   * 
   * @param i - the number of warnings
   */
  public static void setWarningCount(final int i) {
    numWarnings = i;
  }

  /**
   * Sets the number of informations.
   * 
   * @param i - the number of informations
   */
  public static void setInfoCount(final int i) {
    numInfos = i;
  }

  /**
   * Resets to zero the number of errors and warnings.
   */
  public static void resetCounts() {
    numErrors = numWarnings = numInfos = 0;
  }
}

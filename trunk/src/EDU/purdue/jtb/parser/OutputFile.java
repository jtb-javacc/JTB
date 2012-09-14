/* Copyright (c) 2007, Paul Cager.
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

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.security.DigestOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * This class handles the creation and maintenance of the boiler-plate classes, such as Token.java,
 * JavaCharStream.java etc. It is responsible for:
 * <ul>
 * <li>Writing the JavaCC header lines to the file.</li>
 * <li>Writing the checksum line.</li>
 * <li>Using the checksum to determine if an existing file has been changed by the user (and so
 * should be left alone).</li>
 * <li>Checking any existing file's version (if the file can not be overwritten).</li>
 * <li>Checking any existing file's creation options (if the file can not be overwritten).</li>
 * <li></li>
 * </ul>
 * 
 * @author Paul Cager
 * @author Marc Mazas
 * @version 1.4.0 : 05/2009 : MMa : adapted to JavaCC v4.2 grammar
 */
public class OutputFile {

  private static final String MD5_LINE_PART_1  = "/* JavaCC - OriginalChecksum=";
  private static final String MD5_LINE_PART_1q = "/\\* JavaCC - OriginalChecksum=";
  private static final String MD5_LINE_PART_2  = " (do not edit this line) */";
  private static final String MD5_LINE_PART_2q = " \\(do not edit this line\\) \\*/";
  TrapClosePrintWriter        pw;
  DigestOutputStream          dos;
  String                      toolName         = JavaCCGlobals.toolName;
  final File                  file;
  final String                compatibleVersion;
  final String[]              options;

  /**
   * Create a new OutputFile.
   * 
   * @param file the file to write to.
   * @param compatibleVersion the minimum compatible JavaCC version.
   * @param options if the file already exists, and cannot be overwritten, this is a list of options
   *          (such s STATIC=false) to check for changes.
   * @throws IOException
   */
  public OutputFile(final File fl, final String compVers, final String[] opt) throws IOException {
    file = fl;
    compatibleVersion = compVers;
    options = opt;
    if (fl.exists()) {
      // Generate the checksum of the file, and compare with any value
      // stored
      // in the file.
      final BufferedReader br = new BufferedReader(new FileReader(fl));
      MessageDigest digest;
      try {
        digest = MessageDigest.getInstance("MD5");
      }
      catch (final NoSuchAlgorithmException e) {
        throw (IOException) (new IOException("No MD5 implementation").initCause(e));
      }
      final DigestOutputStream digestStream = new DigestOutputStream(new NullOutputStream(), digest);
      final PrintWriter lpw = new PrintWriter(digestStream);
      String line;
      String existingMD5 = null;
      while ((line = br.readLine()) != null) {
        if (line.startsWith(MD5_LINE_PART_1)) {
          existingMD5 = line.replaceAll(MD5_LINE_PART_1q, "").replaceAll(MD5_LINE_PART_2q, "");
        } else {
          lpw.println(line);
        }
      }
      lpw.close();
      final String calculatedDigest = toHexString(digestStream.getMessageDigest().digest());
      if (existingMD5 == null || !existingMD5.equals(calculatedDigest)) {
        // No checksum in file, or checksum differs.
        needToWrite = false;
        if (compVers != null) {
          checkVersion(fl, compVers);
        }
        if (opt != null) {
          checkOptions(fl, opt);
        }
      } else {
        // The file has not been altered since JavaCC created it.
        // Rebuild it.
        System.out.println("File \"" + fl.getName() + "\" is being rebuilt.");
        needToWrite = true;
      }
    } else {
      // File does not exist
      System.out.println("File \"" + fl.getName() + "\" does not exist.  Will create one.");
      needToWrite = true;
    }
  }

  public OutputFile(final File fl) throws IOException {
    this(fl, null, null);
  }

  public boolean needToWrite = true;

  /**
   * Output a warning if the file was created with an incompatible version of JavaCC.
   * 
   * @param fileName
   * @param versionId
   */
  private void checkVersion(final File fl, final String versionId) {
    final String firstLine = "/* " + JavaCCGlobals.getIdString(toolName, fl.getName()) +
                             " Version ";
    try {
      final BufferedReader reader = new BufferedReader(new FileReader(fl));
      String line;
      while ((line = reader.readLine()) != null) {
        if (line.startsWith(firstLine)) {
          final String version = firstLine.replaceFirst(".* Version ", "").replaceAll(" \\*/", "");
          if (version != versionId) {
            JavaCCErrors.warning(fl.getName() +
                                 ": File is obsolete.  Please rename or delete this file so" +
                                 " that a new one can be generated for you.");
          }
          return;
        }
      }
      // If no version line is found, do not output the warning.
    }
    catch (final FileNotFoundException e1) {
      // This should never happen
      JavaCCErrors.semantic_error("Could not open file " + fl.getName() + " for writing.");
      throw new Error();
    }
    catch (final IOException e2) {
    }
  }

  /**
   * Read the options line from the file and compare to the options currently in use. Output a
   * warning if they are different.
   * 
   * @param fileName
   * @param options
   */
  private void checkOptions(final File fl, final String[] opt) {
    try {
      final BufferedReader reader = new BufferedReader(new FileReader(fl));
      String line;
      while ((line = reader.readLine()) != null) {
        if (line.startsWith("/* JavaCCOptions:")) {
          final String currentOptions = Options.getOptionsString(opt);
          if (line.indexOf(currentOptions) == -1) {
            JavaCCErrors.warning(fl.getName() +
                                 ": Generated using incompatible options. Please rename or delete this file so" +
                                 " that a new one can be generated for you.");
          }
          return;
        }
      }
    }
    catch (final FileNotFoundException e1) {
      // This should never happen
      JavaCCErrors.semantic_error("Could not open file " + fl.getName() + " for writing.");
      throw new Error();
    }
    catch (final IOException e2) {
    }
    // Not found so cannot check
  }

  /**
   * Return a PrintWriter object that may be used to write to this file. Any necessary header
   * information is written by this method.
   * 
   * @return
   * @throws IOException
   */
  public PrintWriter getPrintWriter() throws IOException {
    if (pw == null) {
      MessageDigest digest;
      try {
        digest = MessageDigest.getInstance("MD5");
      }
      catch (final NoSuchAlgorithmException e) {
        throw (IOException) (new IOException("No MD5 implementation").initCause(e));
      }
      dos = new DigestOutputStream(new BufferedOutputStream(new FileOutputStream(file)), digest);
      pw = new TrapClosePrintWriter(dos);
      // Write the headers....
      final String version = compatibleVersion == null ? Version.version : compatibleVersion;
      // pw.println("/* "
      // + JavaCCGlobals.getIdString(toolName, file.getName())
      // + " Version " + version + " */");
      // if (options != null) {
      // pw.println("/* JavaCCOptions:" + Options.getOptionsString(options) + " */");
      // }
      pw.println("/* ".concat(JavaCCGlobals.getIdString(toolName, file.getName()))
                      .concat(" Version ").concat(version).concat(" */"));
      if (options != null) {
        pw.println("/* JavaCCOptions:".concat(Options.getOptionsString(options)).concat(" */"));
      }
    }
    return pw;
  }

  /**
   * Close the OutputFile, writing any necessary trailer information (such as a checksum).
   * 
   * @throws IOException
   */
  public void close() {
    // Write the trailer (checksum).
    // Possibly rename the .java.tmp to .java??
    if (pw != null) {
      pw.print(MD5_LINE_PART_1);
      pw.print(getMD5sum());
      pw.println(MD5_LINE_PART_2);
      pw.closePrintWriter();
      // file.renameTo(dest)
    }
  }

  private String getMD5sum() {
    pw.flush();
    final byte[] digest = dos.getMessageDigest().digest();
    return toHexString(digest);
  }

  private final static char[] HEX_DIGITS = new char[] {
      '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

  private static final String toHexString(final byte[] bytes) {
    final StringBuilder sb = new StringBuilder(32);
    for (int i = 0; i < bytes.length; i++) {
      final byte b = bytes[i];
      sb.append(HEX_DIGITS[(b & 0xF0) >> 4]).append(HEX_DIGITS[b & 0x0F]);
    }
    return sb.toString();
  }

  static class NullOutputStream extends OutputStream {

    /** {@inheritDoc} */
    @Override
    public void write(@SuppressWarnings("unused") final byte[] arg0,
                      @SuppressWarnings("unused") final int arg1,
                      @SuppressWarnings("unused") final int arg2) {
    }

    /** {@inheritDoc} */
    @Override
    public void write(@SuppressWarnings("unused") final byte[] arg0) {
    }

    /** {@inheritDoc} */
    @Override
    public void write(@SuppressWarnings("unused") final int arg0) {
    }
  }

  private class TrapClosePrintWriter extends PrintWriter {

    public TrapClosePrintWriter(final OutputStream os) {
      super(os);
    }

    public void closePrintWriter() {
      super.close();
    }

    /** {@inheritDoc} */
    @Override
    public void close() {
      OutputFile.this.close();
    }
  }

  /**
   * @return the toolName
   */
  public final String getToolName() {
    return toolName;
  }

  /**
   * @param tn - the toolName to set
   */
  public final void setToolName(final String tn) {
    toolName = tn;
  }
}

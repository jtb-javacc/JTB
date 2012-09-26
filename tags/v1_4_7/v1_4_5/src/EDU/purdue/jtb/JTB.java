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
 * Indiana by Kevin Tao, Wanjun Wang and Jens Palsberg.  No charge may
 * be made for copies, derivations, or distributions of this material
 * without the express written consent of the copyright holder.
 * Neither the name of the University nor the name of the author
 * may be used to endorse or promote products derived from this
 * material without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND WITHOUT ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, WITHOUT LIMITATION, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR ANY PARTICULAR PURPOSE.
 */
package EDU.purdue.jtb;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Map;

import EDU.purdue.jtb.misc.ClassInfo;
import EDU.purdue.jtb.misc.DepthFirstVisitorsGenerator;
import EDU.purdue.jtb.misc.FileExistsException;
import EDU.purdue.jtb.misc.FilesGenerator;
import EDU.purdue.jtb.misc.Globals;
import EDU.purdue.jtb.misc.Messages;
import EDU.purdue.jtb.misc.TreeDumperGenerator;
import EDU.purdue.jtb.misc.TreeFormatterGenerator;
import EDU.purdue.jtb.parser.JTBParser;
import EDU.purdue.jtb.parser.Options;
import EDU.purdue.jtb.parser.ParseException;
import EDU.purdue.jtb.syntaxtree.INode;
import EDU.purdue.jtb.visitor.Annotator;
import EDU.purdue.jtb.visitor.ClassGenerator;
import EDU.purdue.jtb.visitor.SemanticChecker;

/**
 * Java Tree Builder (JTB) Driver.
 * <p>
 * Class JTB contains the main() method of the program as well as related methods.
 * 
 * @author Kevin Tao
 * @author Wanjun Wang, wanjun@purdue.edu
 * @author Marc Mazas, mmazas@sopragroup.com
 * @version 1.4.0 : 05/2009 : MMa : adapted to JavaCC v4.2 grammar and JDK 1.5<br>
 *          1.4.0 : 11/2009 : MMa : added input file options management
 * @version 1.4.0.3 : 02/2010 : MMa : added static flag
 * @version 1.4.5 : 12/2010 : MMa : convert nodes and visitors output directories to absolute paths
 */
public class JTB {

  /** the input file directory */
  private static String              inDir;
  /** the input file as an InputStream */
  private static InputStream         in;
  /** the program name (for display purposes) */
  private static String              progName   = Globals.PROG_NAME;
  /** the program version (for display purposes) */
  private static String              version    = Globals.VERSION;
  /** the script name */
  private static String              scriptName = Globals.SCRIPT_NAME;
  /** the input file options */
  private static Map<String, Object> jtbOpt     = null;

  /**
   * Standard main method.
   * 
   * @param args the command line arguments
   */
  public static void main(final String args[]) {

    try {
      // Get the command line arguments
      jtbOpt = Options.getOptions();
      if (!processCommandLine(args))
        return;

      // parse the input file
      System.err.println(progName + " version " + version);
      System.err
                .println(progName + ":  Reading jtb input file " + Globals.jtbInputFileName + "...");
      final JTBParser jtbParser = new JTBParser(in);
      final INode root = jtbParser.JavaCCInput();
      System.err.println(progName + ":  jtb input file parsed successfully.");

      // Get the input file options and overwrite command line options
      getFileOptionsAndOverwrite();

      //  Convert nodes and visitors output directories and jj output file to absolute paths
      convertPathsToAbsolute();

      //
      // Perform actions based on input file or command-line options
      //
      final ClassGenerator vcg = new ClassGenerator();
      ArrayList<ClassInfo> classes;
      FilesGenerator fg = null;

      Messages.resetCounts();

      if (!Globals.noSemanticCheck) {
        root.accept(new SemanticChecker());

        if (Messages.errorCount() > 0) {
          Messages.printSummary();
          return;
        }
      }
      root.accept(vcg); // create the class list
      classes = vcg.getClassList();

      if (Messages.errorCount() > 0) {
        Messages.printSummary();
        return;
      }

      if (Globals.printClassList) {
        fg = new FilesGenerator(classes);
        System.out.println("\nThe classes generated and the fields each "
                           + "contains are as follows:\n");
        fg.outputFormattedNodesClassesList(new PrintWriter(System.out, true));
      }

      try {
        final Annotator an = new Annotator();
        root.accept(an);
        an.saveToFile(Globals.jtbOutputFileName);

        if (Messages.errorCount() > 0) {
          Messages.printSummary();
          return;
        }

        System.err.println(progName + ":  jj output file \"" + Globals.jtbOutputFileName +
                           "\" generated.");

      }
      catch (final FileExistsException e) {
        System.err.println(progName + ":  \"" + Globals.jtbOutputFileName +
                           "\" already exists.  Won't overwrite.");
      }

      if (fg == null) {
        fg = new FilesGenerator(classes);

        if (Messages.errorCount() > 0) {
          Messages.printSummary();
          return;
        }
      }

      try {
        fg.genBaseNodesFiles();
        System.err.println(progName + ":  base node class files " + "generated into directory \"" +
                           Globals.nodesDirName + "\".");
      }
      catch (final FileExistsException e) {
        System.err.println(progName + ":  One or more of the base " +
                           "node class files already exists.  Won't overwrite.");
      }

      try {
        fg.genNodesFiles();
        System.err.println(progName + ":  " + classes.size() + " syntax tree node class files " +
                           "generated into directory \"" + Globals.nodesDirName + "\".");
      }
      catch (final FileExistsException e) {
        System.err.println(progName + ":  One or more of the generated " +
                           "node class files already exists.  Won't overwrite.");
      }

      System.err.println();

      try {
        fg.genRetArguIVisitorFile();
        System.err.println(progName + ":  Visitor interface \"" + Globals.iRetArguVisitorName +
                           ".java\" generated into directory \"" + Globals.visitorsDirName + "\".");
      }
      catch (final FileExistsException e) {
        System.err.println(progName + ":  \"" + Globals.iRetArguVisitorName +
                           "\" already exists.  Won't overwrite.");
      }

      try {
        fg.genVoidIVisitorFile();
        System.err.println(progName + ":  Visitor interface \"" + Globals.iVoidVisitorName +
                           ".java\" generated into directory \"" + Globals.visitorsDirName + "\".");
      }
      catch (final FileExistsException e) {
        System.err.println(progName + ":  \"" + Globals.iVoidVisitorName +
                           "\" already exists.  Won't overwrite.");
      }

      try {
        fg.genRetIVisitorFile();
        System.err.println(progName + ":  Visitor interface \"" + Globals.iRetVisitorName +
                           ".java\" generated into directory \"" + Globals.visitorsDirName + "\".");
      }
      catch (final FileExistsException e) {
        System.err.println(progName + ":  \"" + Globals.iRetVisitorName +
                           "\" already exists.  Won't overwrite.");
      }

      try {
        fg.genVoidArguIVisitorFile();
        System.err.println(progName + ":  Visitor interface \"" + Globals.iVoidArguVisitorName +
                           ".java\" generated into directory \"" + Globals.visitorsDirName + "\".");
      }
      catch (final FileExistsException e) {
        System.err.println(progName + ":  \"" + Globals.iVoidArguVisitorName +
                           "\" already exists.  Won't overwrite.");
      }

      final DepthFirstVisitorsGenerator dfvg = new DepthFirstVisitorsGenerator(classes);

      try {
        dfvg.genDepthFirstRetArguVisitorFile();
        System.err.println(progName + ":  Visitor class \"" + Globals.dFRetArguVisitorName +
                           ".java\" generated into directory \"" + Globals.visitorsDirName + "\".");
      }
      catch (final FileExistsException e) {
        System.err.println(progName + ":  \"" + Globals.dFRetArguVisitorName +
                           ".java\" already exists.  Won't overwrite.");
      }

      try {
        dfvg.genDepthFirstRetVisitorFile();
        System.err.println(progName + ":  Visitor class \"" + Globals.dFRetVisitorName +
                           ".java\" generated into directory \"" + Globals.visitorsDirName + "\".");
      }
      catch (final FileExistsException e) {
        System.err.println(progName + ":  \"" + Globals.dFRetVisitorName +
                           ".java\" already exists.  Won't overwrite.");
      }

      try {
        dfvg.genDepthFirstVoidArguVisitorFile();
        System.err.println(progName + ":  Visitor class \"" + Globals.dFVoidArguVisitorName +
                           ".java\" generated into directory \"" + Globals.visitorsDirName + "\".");
      }
      catch (final FileExistsException e) {
        System.err.println(progName + ":  \"" + Globals.dFVoidArguVisitorName +
                           ".java\" already exists.  Won't overwrite.");
      }

      try {
        dfvg.genDepthFirstVoidVisitorFile();
        System.err.println(progName + ":  Visitor class \"" + Globals.dFVoidVisitorName +
                           ".java\" generated into directory \"" + Globals.visitorsDirName + "\".");
      }
      catch (final FileExistsException e) {
        System.err.println(progName + ":  \"" + Globals.dFVoidVisitorName +
                           ".java\" already exists.  Won't overwrite.");
      }

      System.err.println();

      if (Globals.printerToolkit) {
        try {
          final TreeDumperGenerator tdg = new TreeDumperGenerator();
          tdg.generateTreeDumper();
          tdg.saveToFile();
          System.err.println(progName + ":  Visitor class \"" + TreeDumperGenerator.outFilename +
                             "\" generated into directory \"" + Globals.visitorsDirName + "\".");
        }
        catch (final FileExistsException e) {
          System.err.println(progName + ":  \"" + TreeDumperGenerator.outFilename +
                             "\" already exists.  Won't overwrite.");
        }

        try {
          final TreeFormatterGenerator tfg = new TreeFormatterGenerator(classes);
          tfg.generateTreeFormatter();
          tfg.saveToFile();
          System.err.println(progName + ":  Visitor class \"" + TreeFormatterGenerator.outFilename +
                             "\" generated into directory \"" + Globals.visitorsDirName + "\".");
        }
        catch (final FileExistsException e) {
          System.err.println(progName + ":  \"" + TreeFormatterGenerator.outFilename +
                             "\" already exists.  Won't overwrite.");
        }
        System.err.println();
      }
      if (Messages.errorCount() > 0 || Messages.warningCount() > 0)
        Messages.printSummary();
    }
    catch (final InvalCmdLineException e) {
      System.err.println(progName + ":  " + e.getMessage());
      return;
    }
    catch (final ParseException e) {
      System.err.println("\n" + e.getMessage() + "\n");
      System.err.println(progName + ":  Encountered error(s) during parsing.");
    }
    catch (final Exception e) {
      e.printStackTrace(System.err);
      Messages.hardErr(e);
    }
  }

  /**
   * Gets the input file options and overwrite command line ones if they are different.
   */
  private static void getFileOptionsAndOverwrite() {
    String str = null;

    Globals.printClassList = ((Boolean) jtbOpt.get("JTB_CL")).booleanValue();

    Globals.depthLevel = ((Boolean) jtbOpt.get("JTB_DL")).booleanValue();

    Globals.noSemanticCheck = ((Boolean) jtbOpt.get("JTB_E")).booleanValue();

    Globals.descriptiveFieldNames = ((Boolean) jtbOpt.get("JTB_F")).booleanValue();

    Globals.inlineAcceptMethods = ((Boolean) jtbOpt.get("JTB_IA")).booleanValue();

    Globals.javaDocComments = ((Boolean) jtbOpt.get("JTB_JD")).booleanValue();

    Globals.nodesDirName = (String) jtbOpt.get("JTB_ND");

    Globals.nodesPackageName = (String) jtbOpt.get("JTB_NP");

    str = (String) jtbOpt.get("JTB_NS");
    Globals.nodesSuperclass = "".equals(str) ? null : str;

    Globals.jtbOutputFileName = (String) jtbOpt.get("JTB_O");

    Globals.parentPointer = ((Boolean) jtbOpt.get("JTB_PP")).booleanValue();

    Globals.printerToolkit = ((Boolean) jtbOpt.get("JTB_PRINTER")).booleanValue();

    Globals.schemeToolkit = ((Boolean) jtbOpt.get("JTB_SCHEME")).booleanValue();

    Globals.keepSpecialTokens = ((Boolean) jtbOpt.get("JTB_TK")).booleanValue();

    Globals.visitorsDirName = (String) jtbOpt.get("JTB_VD");

    Globals.visitorsPackageName = (String) jtbOpt.get("JTB_VP");

    Globals.noOverwrite = ((Boolean) jtbOpt.get("JTB_W")).booleanValue();

    str = (String) jtbOpt.get("JTB_D");
    if (!"".equals(str)) {
      Globals.nodesDirName = str + "/" + Globals.DEF_ND_DIR_NAME;
      Globals.visitorsDirName = str + "/" + Globals.DEF_VIS_DIR_NAME;
    }

    str = (String) jtbOpt.get("JTB_P");
    if (!"".equals(str)) {
      Globals.nodesPackageName = str + "." + Globals.DEF_ND_PKG_NAME;
      Globals.visitorsPackageName = str + "." + Globals.DEF_VIS_PKG_NAME;
    }

    Globals.staticFlag = ((Boolean) jtbOpt.get("STATIC")).booleanValue();

  }

  /**
   * Processes command line arguments, putting options in the options object (to be overwritten by
   * input file options).
   * 
   * @param args the command line arguments
   * @return true if successful, false otherwise
   * @throws InvalCmdLineException if any problem with command line arguments
   */
  private static boolean processCommandLine(final String[] args) throws InvalCmdLineException {
    boolean returnVal = false;

    for (int i = 0; i < args.length; ++i) {
      if (args[i].charAt(0) != '-') {
        if (returnVal) {
          returnVal = false; // 2 filenames passed as arguments?
          break;
        } else {
          try {
            final File inFile = new File(args[i]);
            inDir = inFile.getAbsoluteFile().getParent();
            in = new FileInputStream(inFile);
          }
          catch (final FileNotFoundException e) {
            System.err.println(progName + ":  File \"" + args[i] + "\" not found.");
            return false;
          }

          Globals.jtbInputFileName = args[i];
          returnVal = true;
        }
      } else {
        if (args[i].length() <= 1)
          throw new InvalCmdLineException("Unknown option \"" + args[i] + "\".  Try \"" +
                                          scriptName + " -h\" for more " + "information.");
        if (args[i].equals("-h")) {
          returnVal = false;
          break;
        }

        else if (args[i].equals("-cl")) {
          Globals.printClassList = true;
          jtbOpt.put("JTB_CL", Boolean.TRUE);
        }

        else if (args[i].equals("-d")) {
          ++i;
          if (i >= args.length || args[i].charAt(0) == '-')
            throw new InvalCmdLineException("Option \"-d\" must be followed by a directory name.");
          else {
            Globals.nodesDirName = args[i] + "/" + Globals.DEF_ND_DIR_NAME;
            Globals.visitorsDirName = args[i] + "/" + Globals.DEF_VIS_DIR_NAME;
            jtbOpt.put("JTB_D", args[i]);
            jtbOpt.put("JTB_ND", Globals.nodesDirName);
            jtbOpt.put("JTB_VD", Globals.visitorsDirName);
          }
        }

        else if (args[i].equals("-dl")) {
          Globals.depthLevel = true;
          jtbOpt.put("JTB_DL", Boolean.TRUE);
        }

        else if (args[i].equals("-e")) {
          Globals.noSemanticCheck = true;
          jtbOpt.put("JTB_E", Boolean.TRUE);
        }

        else if (args[i].equals("-f")) {
          Globals.descriptiveFieldNames = true;
          jtbOpt.put("JTB_F", Boolean.TRUE);
        }

        else if (args[i].equals("-ia")) {
          Globals.inlineAcceptMethods = true;
          jtbOpt.put("JTB_IA", Boolean.TRUE);
        }

        else if (args[i].equals("-jd")) {
          Globals.javaDocComments = true;
          jtbOpt.put("JTB_JD", Boolean.TRUE);
        }

        else if (args[i].equals("-nd")) {
          ++i;
          if (i >= args.length || args[i].charAt(0) == '-')
            throw new InvalCmdLineException("Option \"-nd\" must be followed by a directory name.");
          else {
            Globals.nodesDirName = args[i];
            jtbOpt.put("JTB_ND", Globals.nodesDirName);
          }
        }

        else if (args[i].equals("-np")) {
          ++i;
          if (i >= args.length || args[i].charAt(0) == '-')
            throw new InvalCmdLineException("Option \"-np\" must be followed by a package name.");
          else {
            Globals.nodesPackageName = args[i];
            jtbOpt.put("JTB_NP", Globals.nodesPackageName);
          }
        }

        else if (args[i].equals("-ns")) {
          ++i;
          if (i >= args.length || args[i].charAt(0) == '-')
            throw new InvalCmdLineException("Option \"-ns\" must be followed by a class name.");
          else {
            Globals.nodesSuperclass = args[i];
            jtbOpt.put("JTB_NS", Globals.nodesSuperclass);
          }
        }

        else if (args[i].equals("-o")) {
          ++i;
          if (i >= args.length || args[i].charAt(0) == '-')
            throw new InvalCmdLineException("Option \"-o\" must be followed by a filename.");
          else {
            Globals.jtbOutputFileName = args[i];
            jtbOpt.put("JTB_O", Globals.jtbOutputFileName);
          }
        }

        else if (args[i].equals("-p")) {
          ++i;
          if (i >= args.length || args[i].charAt(0) == '-')
            throw new InvalCmdLineException("Option \"-p\" must be followed by a package name.");
          else {
            Globals.nodesPackageName = args[i] + "." + Globals.DEF_ND_PKG_NAME;
            Globals.visitorsPackageName = args[i] + "." + Globals.DEF_VIS_PKG_NAME;
            jtbOpt.put("JTB_P", args[i]);
            jtbOpt.put("JTB_NP", Globals.nodesPackageName);
            jtbOpt.put("JTB_VP", Globals.visitorsPackageName);
          }
        }

        else if (args[i].equals("-pp")) {
          Globals.parentPointer = true;
          jtbOpt.put("JTB_PP", Boolean.TRUE);
        }

        else if (args[i].equals("-printer")) {
          Globals.printerToolkit = true;
          jtbOpt.put("JTB_PRINTER", Boolean.TRUE);
        }

        else if (args[i].equals("-scheme")) {
          Globals.schemeToolkit = true;
          jtbOpt.put("JTB_SCHEME", Boolean.TRUE);
        }

        else if (args[i].equals("-si")) {
          in = System.in;
          Globals.jtbInputFileName = "standard input";
        }

        else if (args[i].equals("-tk")) {
          Globals.keepSpecialTokens = true;
          jtbOpt.put("JTB_TK", Boolean.TRUE);
        }

        else if (args[i].equals("-vd")) {
          ++i;
          if (i >= args.length || args[i].charAt(0) == '-')
            throw new InvalCmdLineException("Option \"-vd\" must be followed by a directory name.");
          else {
            Globals.visitorsDirName = args[i];
            jtbOpt.put("JTB_VD", Globals.visitorsDirName);
          }
        }

        else if (args[i].equals("-vp")) {
          ++i;
          if (i >= args.length || args[i].charAt(0) == '-')
            throw new InvalCmdLineException("Option \"-vp\" must be followed by a package name.");
          else {
            Globals.visitorsPackageName = args[i];
            jtbOpt.put("JTB_VP", Globals.visitorsPackageName);
          }
        }

        else if (args[i].equals("-w")) {
          Globals.noOverwrite = true;
          jtbOpt.put("JTB_W", Boolean.TRUE);
        }

        else
          throw new InvalCmdLineException("Unknown option \"" + args[i] + "\".  Try \"" +
                                          scriptName + " -h\" for more information.");
      }
    }

    if (returnVal)
      return true;
    else {
      printHelp();
      return false;
    }
  }

  /**
   * Convert nodes and visitors output directories and jj output file (relatives to the grammar
   * file) to absolute paths.
   */
  private static void convertPathsToAbsolute() {
    final String dir = inDir + File.separator;
    final File ndn = new File(Globals.nodesDirName);
    if (!ndn.isAbsolute())
      Globals.nodesDirName = dir + Globals.nodesDirName;
    final File vdn = new File(Globals.visitorsDirName);
    if (!vdn.isAbsolute())
      Globals.visitorsDirName = dir + Globals.visitorsDirName;
    final File jjf = new File(Globals.jtbOutputFileName);
    if (!jjf.isAbsolute())
      Globals.jtbOutputFileName = dir + Globals.jtbOutputFileName;
  }

  /**
   * Displays program usage.
   */
  private static void printHelp() {
    System.out
              .print(progName +
                     " version " +
                     version +
                     "\n" +
                     "\n" +
                     "Usage: " +
                     scriptName +
                     " [OPTIONS] " +
                     "[inputfile]\n" +
                     "\n" +
                     "Standard options:\n" +
                     "  -cl         Print a list of the classes generated to standard out.\n" +
                     "  -d dir     \"-d dir\" is short for (and overwrites) \"-nd dir/syntaxtree -vd dir/visitor\".\n" +
                     "  -dl         Generate depth level info.\n" +
                     "  -e          Suppress JTB semantic error checking.\n" +
                     "  -f          Use descriptive node class field names.\n" +
                     "  -h          Display this help message and quit.\n" +
                     "  -ia         Inline visitors accept methods on base classes.\n" +
                     "  -jd         Generate JavaDoc-friendly comments in the nodes and visitor.\n" +
                     "  -nd dir     Use dir as the package for the syntax tree nodes.\n" +
                     "  -np pkg     Use pkg as the package for the syntax tree nodes.\n" +
                     "  -ns class   Use class as the class which all node classes will extend.\n" +
                     "  -o file     Use file as the filename for the annotated output grammar.\n" +
                     "  -p pkg      \"-p pkg\" is short for (and overwrites) \"-np pkg.syntaxtree -vp pkg.visitor\".\n" +
                     "  -pp         Generate parent pointers in all node classes.\n" +
                     "  -printer    Generate a syntax tree dumping visitor.\n" +
                     "  -si         Read from standard input rather than a file.\n" +
                     "  -scheme     Generate Scheme records representing the grammar and a Scheme tree building visitor.\n" +
                     "  -tk         Generate special tokens into the tree.\n" +
                     "  -vd dir     Use dir as the package for the default visitor classes.\n" +
                     "  -vp pkg     Use pkg as the package for the default visitor classes.\n" +
                     "  -w          Do not overwrite existing files.\n" + "\n");
  }
}

/**
 * Inner class for managing command line errors.
 */
class InvalCmdLineException extends Exception {

  /** default serialVersionUID */
  private static final long serialVersionUID = 1L;

  /**
   * Standard constructor with no argument.
   */
  InvalCmdLineException() {
    super();
  }

  /**
   * Standard constructor with a message.
   * 
   * @param s the exception message
   */
  InvalCmdLineException(final String s) {
    super(s);
  }
}

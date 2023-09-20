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
 * Research Foundation of Purdue University. All rights reserved.
 *
 * Redistribution and use in source and binary forms are permitted provided that this entire copyright notice
 * is duplicated in all such copies, and that any documentation, announcements, and other materials related to
 * such distribution and use acknowledge that the software was developed at Purdue University, West Lafayette,
 * Indiana by Kevin Tao, Wanjun Wang and Jens Palsberg. No charge may be made for copies, derivations, or
 * distributions of this material without the express written consent of the copyright holder. Neither the
 * name of the University nor the name of the author may be used to endorse or promote products derived from
 * this material without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND WITHOUT ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, WITHOUT
 * LIMITATION, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR ANY PARTICULAR PURPOSE.
 */
package EDU.purdue.jtb;

import static EDU.purdue.jtb.common.Constants.DEF_HOOK_DIR_NAME;
import static EDU.purdue.jtb.common.Constants.DEF_HOOK_PKG_NAME;
import static EDU.purdue.jtb.common.Constants.DEF_ND_DIR_NAME;
import static EDU.purdue.jtb.common.Constants.DEF_ND_PKG_NAME;
import static EDU.purdue.jtb.common.Constants.DEF_VIS_DIR_NAME;
import static EDU.purdue.jtb.common.Constants.DEF_VIS_PKG_NAME;
import static EDU.purdue.jtb.common.Constants.JTB_VERSION;
import static EDU.purdue.jtb.common.Constants.PROG_NAME;
import static EDU.purdue.jtb.common.Constants.baseNodesClasses;
import static EDU.purdue.jtb.common.Constants.baseNodesInterfaces;
import static EDU.purdue.jtb.common.Constants.emptyEnterExitHook;
import static EDU.purdue.jtb.common.Constants.iEnterExitHook;
import static EDU.purdue.jtb.common.Constants.jtbEgInvokedMethode;
import static EDU.purdue.jtb.common.Constants.jtb_base_interfaces_dm_key;
import static EDU.purdue.jtb.common.Constants.jtb_base_nodes_dm_key;
import static EDU.purdue.jtb.common.Constants.jtb_constants_dm_key;
import static EDU.purdue.jtb.common.Constants.jtb_nbSubNodesTbc_dm_key;
import static EDU.purdue.jtb.common.Constants.jtb_notTbcNodes_dm_key;
import static EDU.purdue.jtb.common.Constants.jtb_options_dm_key;
import static EDU.purdue.jtb.common.Constants.jtb_prod_dm_key;
import static EDU.purdue.jtb.common.Constants.jtb_user_nodes_dm_key;
import static EDU.purdue.jtb.common.Constants.sigAnnName;
import static EDU.purdue.jtb.common.Constants.sigAnnProcName;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import EDU.purdue.jtb.analyse.ClassesFinder;
import EDU.purdue.jtb.analyse.GlobalDataBuilder;
import EDU.purdue.jtb.analyse.SemanticChecker;
import EDU.purdue.jtb.common.JTBOptions;
import EDU.purdue.jtb.common.Messages;
import EDU.purdue.jtb.common.ProgrammaticError;
import EDU.purdue.jtb.common.UserClassInfo;
import EDU.purdue.jtb.common.VisitorInfo;
import EDU.purdue.jtb.generate.BaseNodesGenerator;
import EDU.purdue.jtb.generate.CommonCodeGenerator;
import EDU.purdue.jtb.generate.JJFileAnnotator;
import EDU.purdue.jtb.generate.TreeDumperGenerator;
import EDU.purdue.jtb.generate.TreeFormatterGenerator;
import EDU.purdue.jtb.generate.UserFilesGenerator;
import EDU.purdue.jtb.generate.VisitorsGenerator;
import EDU.purdue.jtb.parser.JTBParser;
import EDU.purdue.jtb.parser.ParseException;
import EDU.purdue.jtb.parser.syntaxtree.INode;

/**
 * Java Tree Builder (JTB) Driver.
 * <p>
 * Class {@link JTB} contains the main() method of the program as well as related methods.
 * <p>
 * JTB enables to be run in parallel threads on different grammars.
 * </p>
 * TESTCASE some to add
 *
 * @author Kevin Tao, Wanjun Wang, Marc Mazas, Francis Andre
 * @version 1.4.0 : 05/2009 : MMa : adapted to JavaCC v4.2 grammar and JDK 1.5<br>
 *          1.4.0 : 11/2009 : MMa : added input file options management
 * @version 1.4.0.3 : 02/2010 : MMa : added static flag
 * @version 1.4.5 : 12/2010 : MMa : convert nodes and visitors output directories to absolute paths
 * @version 1.4.6 : 01/2011 : FA : added -va and -npfx and -nsfx options
 * @version 1.4.7 : 09/2012 : MMa : some renamings ; added the use of the GlobalDataBuilder
 * @version 1.5.0 : 01-03/2017 : MMa : added @SuppressWarnings for unused exceptions ; added node scope hook
 *          options ; added noVisitors and childrenMethods options ; removed scheme option ; added call
 *          external generator option ; splitted {@link #do_main} ; moved InvalidCmdLineException to a real
 *          inner class (and static) ; enhanced to VisitorInfo based visitor generation ; subject to global
 *          packages and classes refactoring ; separated info and error messages ; suppressed class
 *          FileExistsException ; moved to non static ; changed default true values to false<br>
 *          1.5.0 : 10-2020 : MMa : create directories only if needed ; normalized file names
 * @version 1.5.1 : 07/2023 : MMa : changed no overwrite management ; fixed issue on JTB_VP/JTB_VD<br>
 *          1.5.1 : 08/2023 : MMa : changes due to the NodeToken replacement by Token<br>
 *          1.5.1 : 09/2023 : MMa : fixed directories values
 */
public class JTB {
  
  /** The messages handler */
  private final Messages            mess = new Messages();
  /** The global JTB options */
  public final JTBOptions           jopt = new JTBOptions(mess);
  /** The {@link GlobalDataBuilder} visitor */
  final GlobalDataBuilder           gdbv = new GlobalDataBuilder(jopt);
  /** The {@link CommonCodeGenerator} */
  private final CommonCodeGenerator ccg  = new CommonCodeGenerator(gdbv);
  /** The input file directory */
  private String                    inDir;
  /** The input file as an InputStream */
  private InputStream               in;
  /**
   * the input (jtb) file name (must be set in main)
   */
  String                            jtbInputFileName;
  
  /** No error */
  public static final int OK        = 0;     // NO_UCD (unused code)
  /** Command line error */
  public static final int CL_ERR    = -1;    // NO_UCD (use private)
  /** {@link GlobalDataBuilder} error */
  public static final int GDB_ERR   = -2;    // NO_UCD (use private)
  /** {@link SemanticChecker} error */
  public static final int SC_ERR    = -4;    // NO_UCD (use private)
  /** {@link ClassesFinder} error */
  public static final int CF_ERR    = -8;    // NO_UCD (use private)
  /** {@link JJFileAnnotator} error */
  public static final int ANN_ERR   = -16;   // NO_UCD (use private)
  /** {@link UserFilesGenerator} & {@link VisitorsGenerator} directory creation error */
  public static final int DI_ERR    = -32;   // NO_UCD (use private)
  /** Parsing file options error */
  public static final int FO_ERR    = -64;   // NO_UCD (use private)
  /** {@link InvalidCmdLineException} exception */
  public static final int CL_EX     = -128;  // NO_UCD (use private)
  /** {@link ParseException} exception */
  public static final int PARSE_EX  = -256;  // NO_UCD (use private)
  /** Running external generator error */
  public static final int EG_ERR    = -512;  // NO_UCD (use private)
  /** IO exception */
  public static final int IO_EX     = -1024; // NO_UCD (use private)
  /** Programmatic error */
  public static final int OTHER_EX  = -2048; // NO_UCD (use private)
  /** Programmatic error */
  public static final int PROG_ERR  = -4096; // NO_UCD (use private)
  /** Other error */
  public static final int OTHER_THR = -9192; // NO_UCD (use private)
  
  /**
   * Standard main method.<br>
   * Calls {@link System#exit(int)} upon termination with the following status:
   * <li>for specific types of errors: {@link #CL_ERR}, {@link #GDB_ERR}, {@link #SC_ERR}, {@link #CF_ERR},
   * {@link #ANN_ERR}, {@link #DI_ERR}, {@link #FO_ERR}, {@link #CL_EX}, {@link #PARSE_EX}, {@link #EG_ERR},
   * {@link #IO_EX}), {@link #OTHER_EX}), {@link #PROG_ERR}), {@link #OTHER_THR}) or</li>
   * <li>0 if no error, or</li>
   * <li>the number of errors</li> Use {@link #do_main(String[])} to get directly the status without calling
   * {@link System#exit(int)}.
   *
   * @param aArgs - the command line arguments
   */
  public static void main(final String aArgs[]) {
    System.out.flush();
    System.err.flush();
    System.exit(new JTB().internal_main(aArgs));
  }
  
  /**
   * Non standard main static method returning an error code.
   *
   * @param aArgs - the command line arguments
   * @return the error code:
   *         <li>for specific types of errors: {@link #CL_ERR}, {@link #GDB_ERR}, {@link #SC_ERR},
   *         {@link #CF_ERR}, {@link #ANN_ERR}, {@link #DI_ERR}, {@link #FO_ERR}, {@link #CL_EX},
   *         {@link #PARSE_EX}, {@link #EG_ERR}, {@link #IO_EX}), {@link #OTHER_EX}), {@link #PROG_ERR}),
   *         {@link #OTHER_THR}) or</li>
   *         <li>0 if no error or</li>
   *         <li>the number of errors</li>
   */
  public static int do_main(final String aArgs[]) { // NO_UCD (unused code)
    return new JTB().internal_main(aArgs);
  }
  
  /**
   * Non standard and non static "main" method returning an error code.
   *
   * @param aArgs - the command line arguments
   * @return the error code:
   *         <li>for specific types of errors: {@link #CL_ERR}, {@link #GDB_ERR}, {@link #SC_ERR},
   *         {@link #CF_ERR}, {@link #ANN_ERR}, {@link #DI_ERR}, {@link #FO_ERR}, {@link #CL_EX},
   *         {@link #PARSE_EX}, {@link #EG_ERR}, {@link #IO_EX}), {@link #OTHER_EX}), {@link #PROG_ERR}),
   *         {@link #OTHER_THR}) or</li>
   *         <li>0 if no error or</li>
   *         <li>the number of errors</li>
   */
  private int internal_main(final String aArgs[]) {
    
    try {
      printlnInfo("Version " + JTB_VERSION);
      // get and store the command line arguments
      jopt.init();
      if (!processCommandLine(aArgs)) {
        return CL_ERR;
      }
      
      // parse the input file
      printlnInfo("Reading jtb input file " + jtbInputFileName);
      final JTBParser jtbParser = new JTBParser(in);
      final INode root = jtbParser.JavaCCInput(jopt);
      printlnInfo("jtb input file parsed successfully.");
      
      // Get the output directory JavaCC option (for generating Token.java)
      File jjOutDir = jtbParser.opt.getOutputDirectory();
      if (!jjOutDir.isAbsolute()) {
        jjOutDir = new File(inDir + File.separator + jjOutDir);
        jopt.jjOutDirName = jjOutDir.toPath().normalize().toString();
      }
      
      // Get the input file JTB options and overwrite command line options
      jopt.loadJTBGlobalOptions(jtbParser.grammarPackage);
      if (mess.errorCount() > 0) {
        mess.printSummary();
        return FO_ERR;
      }
      
      // convert directories and jj output file to absolute paths
      convertPathsToAbsolute();
      
      // create all directories if they do not exist
      createDirectory(jopt.jjOutDirName);
      createParentDirectory(jopt.jtbOutputFileName);
      File visitorDir = null;
      if (jopt.visitorsDirName != null && !jopt.noVisitors) {
        visitorDir = createDirectory(jopt.visitorsDirName);
      }
      File signatureDir = null;
      if (jopt.visitorsDirName != null && !jopt.noSignature) {
        signatureDir = createDirectory(jopt.signatureDirName);
      }
      File nodesDir = null;
      if (jopt.nodesDirName != null) {
        nodesDir = createDirectory(jopt.nodesDirName);
      }
      File hookDir = null;
      if (jopt.hookDirName != null && jopt.hook) {
        hookDir = createDirectory(jopt.hookDirName);
      }
      if (mess.errorCount() > 0) {
        mess.printSummary();
        return DI_ERR;
      }
      
      // gather global data
      mess.resetCounts();
      
      root.accept(gdbv);
      if (mess.errorCount() > 0) {
        mess.printSummary();
        return GDB_ERR;
      }
      
      // check semantics
      if (!jopt.noSemanticCheck) {
        root.accept(new SemanticChecker(gdbv));
        if (mess.errorCount() > 0) {
          mess.printSummary();
          return SC_ERR;
        }
      }
      
      // create the classes list
      final ClassesFinder cfv = new ClassesFinder(gdbv);
      root.accept(cfv);
      final List<UserClassInfo> classes = cfv.getClasses();
      if (mess.errorCount() > 0) {
        mess.printSummary();
        return CF_ERR;
      }
      
      // instantiate the UserFilesGenerator
      final UserFilesGenerator ufg = new UserFilesGenerator(jopt, ccg, classes);
      
      // output classes list
      if (jopt.printClassList) {
        printlnInfo("The generated classes and their fields are:");
        ufg.outputFormattedNodesClassesList(new PrintWriter(System.out, true));
      }
      
      // generate the jj annotated file
      final JJFileAnnotator jjfav = new JJFileAnnotator(gdbv, ccg);
      root.accept(jjfav);
      final int rc = jjfav.saveToFile(jopt.jtbOutputFileName);
      if (mess.errorCount() > 0) {
        mess.printSummary();
        return ANN_ERR;
      }
      if (rc == 0) {
        printlnInfo("jj annotated output file \"" + jopt.jtbOutputFileName + "\" generated.");
      } else {
        printlnInfo(PROG_NAME + ": " + jopt.jtbOutputFileName + " file already exists.  Won't overwrite.");
      }
      
      // generate user & base nodes (syntaxtree)
      generateSyntaxtreeNodes(classes, ufg, nodesDir);
      
      // generate visitors and signature annotations
      if (!jopt.noVisitors) {
        System.err.println();
        final VisitorsGenerator vg = new VisitorsGenerator(gdbv, ccg, classes);
        generateVisitors(vg, visitorDir);
        if (!jopt.noSignature) {
          generateSignatureFiles(vg, signatureDir);
        }
      }
      
      // generate hook files
      if (jopt.hook) {
        System.err.println();
        generateHookFiles(ufg, hookDir);
      }
      
      // call the external generator
      if (jopt.externalGeneratorClass != null) {
        System.err.println();
        final int rceg = callExternalGenerator(classes);
        if (rceg < 0) {
          return rceg;
        }
      }
      
      // generate TreeFormatter & Dumper
      if (jopt.printerToolkit) {
        System.err.println();
        generateTreeFormatterDumper(classes);
      }
      
      if ((mess.errorCount() > 0) || (mess.warningCount() > 0)) {
        mess.printSummary();
      }
      return mess.errorCount();
      
    } catch (final InvalidCmdLineException e) {
      printlnError(e.getMessage());
      return CL_EX;
    } catch (final ParseException e) {
      System.err.println("\n" + e.getMessage() + "\n");
      printlnError("Encountered error(s) during parsing.");
      return PARSE_EX;
    } catch (@SuppressWarnings("unused") final IOException e) {
      // exception rethrown, already reported
      return IO_EX;
    } catch (final Exception e) {
      e.printStackTrace();
      return OTHER_EX;
    } catch (@SuppressWarnings("unused") final ProgrammaticError e) {
      // exception rethrown, already reported
      return PROG_ERR;
    } catch (final Throwable e) {
      e.printStackTrace();
      return OTHER_THR;
    }
  }
  
  /**
   * Processes command line arguments, setting global variables and putting options keys/values in the options
   * map.<br>
   * Standalone and cross controls are applied on these command line arguments, leading to
   * {@link InvalidCmdLineException} exceptions or {@link Messages#warning(String)} warnings.
   *
   * @param aArgs - the command line arguments
   * @return true if successful, false otherwise
   * @throws InvalidCmdLineException - if any problem with command line arguments
   */
  private boolean processCommandLine(final String[] aArgs) throws InvalidCmdLineException {
    boolean returnVal = false;
    
    for (int i = 0; i < aArgs.length; ++i) {
      String arg = aArgs[i];
      final String uppArg = arg.toUpperCase();
      final String lowArg = arg.toLowerCase();
      if (arg.charAt(0) != '-') {
        if (returnVal) {
          returnVal = false; // 2 filenames passed as arguments?
          break;
        } else {
          try {
            final File inFile = new File(arg);
            inDir = inFile.getAbsoluteFile().getParent();
            in = new FileInputStream(inFile);
          } catch (@SuppressWarnings("unused") final FileNotFoundException e) {
            printlnError("File \"" + arg + "\" not found.");
            return false;
          }
          jtbInputFileName = arg;
          returnVal = true;
        }
        
      } else {
        if (arg.length() <= 1) {
          throw new InvalidCmdLineException(
              "Unknown option \"" + arg + "\".  Try \"" + PROG_NAME + " -h\" for more " + "information.");
        }
        
        if (lowArg.startsWith("-chm")) {
          if (lowArg.equals("-chm") || lowArg.equals("-chm=true")) {
            jopt.setCmdLineOption("JTB_CHM", true);
          } else if (lowArg.equals("-chm=false")) {
            jopt.setCmdLineOption("JTB_CHM", false);
          } else {
            throw new InvalidCmdLineException("Invalid option \"" + arg + "\".");
          }
        } else if (uppArg.startsWith("-JTB_CHM=")) {
          if (uppArg.equals("-JTB_CHM=TRUE")) {
            jopt.setCmdLineOption("JTB_CHM", true);
          } else if (uppArg.equals("-JTB_CHM=FALSE")) {
            jopt.setCmdLineOption("JTB_CHM", false);
          } else {
            throw new InvalidCmdLineException("Invalid option \"" + arg + "\".");
          }
        } else if (lowArg.startsWith("-cl")) {
          if (lowArg.equals("-cl") || lowArg.equals("-cl=true")) {
            jopt.setCmdLineOption("JTB_CL", true);
          } else if (lowArg.equals("-cl=false")) {
            jopt.setCmdLineOption("JTB_CL", false);
          } else {
            throw new InvalidCmdLineException("Invalid option \"" + arg + "\".");
          }
        } else if (uppArg.startsWith("-JTB_CL=")) {
          if (uppArg.equals("-JTB_CL=TRUE")) {
            jopt.setCmdLineOption("JTB_CL", true);
          } else if (uppArg.equals("-JTB_CL=FALSE")) {
            jopt.setCmdLineOption("JTB_CL", false);
          } else {
            throw new InvalidCmdLineException("Invalid option \"" + arg + "\".");
          }
        } else if (lowArg.startsWith("-d")) {
          if (lowArg.equals("-d")) {
            if (++i >= aArgs.length) {
              throw new InvalidCmdLineException("Missing directory name after option \"-d\".");
            }
            arg = aArgs[i];
            jopt.setCmdLineOption("JTB_D", arg);
            updateDirs(arg);
          } else if (lowArg.startsWith("-d=")) {
            jopt.setCmdLineOption("JTB_D", arg.substring(3));
            updateDirs(arg.substring(3));
          } else {
            throw new InvalidCmdLineException("Invalid option \"" + arg + "\".");
          }
        } else if (uppArg.startsWith("-JTB_D=")) {
          jopt.setCmdLineOption("JTB_D", arg.substring(7));
          updateDirs(arg.substring(7));
        }
        
        else if (lowArg.startsWith("-dl")) {
          if (lowArg.equals("-dl") || lowArg.equals("-dl=true")) {
            jopt.setCmdLineOption("JTB_DL", true);
          } else if (lowArg.equals("-dl=false")) {
            jopt.setCmdLineOption("JTB_DL", false);
          } else {
            throw new InvalidCmdLineException("Invalid option \"" + arg + "\".");
          }
        } else if (uppArg.startsWith("-JTB_DL=")) {
          if (uppArg.equals("-JTB_DL=TRUE")) {
            jopt.setCmdLineOption("JTB_DL", true);
          } else if (uppArg.equals("-JTB_DL=FALSE")) {
            jopt.setCmdLineOption("JTB_DL", false);
          } else {
            throw new InvalidCmdLineException("Invalid option \"" + arg + "\".");
          }
        } else if (lowArg.startsWith("-do")) {
          if (lowArg.equals("-do") || lowArg.equals("-do=true")) {
            jopt.setCmdLineOption("JTB_DO", true);
          } else if (lowArg.equals("-do=false")) {
            jopt.setCmdLineOption("JTB_DO", false);
          } else {
            throw new InvalidCmdLineException("Invalid option \"" + arg + "\".");
          }
        } else if (uppArg.startsWith("-JTB_DO=")) {
          if (uppArg.equals("-JTB_DO=TRUE")) {
            jopt.setCmdLineOption("JTB_DO", true);
          } else if (uppArg.equals("-JTB_DO=FALSE")) {
            jopt.setCmdLineOption("JTB_DO", false);
          } else {
            throw new InvalidCmdLineException("Invalid option \"" + arg + "\".");
          }
        } else if (lowArg.startsWith("-eg")) {
          if (lowArg.equals("-eg")) {
            if (++i >= aArgs.length) {
              throw new InvalidCmdLineException("Missing class name after option \"-eg\".");
            }
            jopt.setCmdLineOption("JTB_EG", aArgs[i]);
          } else if (lowArg.startsWith("-eg=")) {
            jopt.setCmdLineOption("JTB_EG", arg.substring(4));
          } else {
            throw new InvalidCmdLineException("Invalid option \"" + arg + "\".");
          }
        } else if (uppArg.startsWith("-JTB_EG=")) {
          jopt.setCmdLineOption("JTB_EG", arg.substring(8));
        } else if (lowArg.startsWith("-e")) {
          if (lowArg.equals("-e") || lowArg.equals("-e=true")) {
            jopt.setCmdLineOption("JTB_E", true);
          } else if (lowArg.equals("-e=false")) {
            jopt.setCmdLineOption("JTB_E", false);
          } else {
            throw new InvalidCmdLineException("Invalid option \"" + arg + "\".");
          }
        } else if (uppArg.startsWith("-JTB_E=")) {
          if (uppArg.equals("-JTB_E=TRUE")) {
            jopt.setCmdLineOption("JTB_E", true);
          } else if (uppArg.equals("-JTB_E=FALSE")) {
            jopt.setCmdLineOption("JTB_E", false);
          } else {
            throw new InvalidCmdLineException("Invalid option \"" + arg + "\".");
          }
        } else if (lowArg.startsWith("-f")) {
          if (lowArg.equals("-f") || lowArg.equals("-f=true")) {
            jopt.setCmdLineOption("JTB_F", true);
          } else if (lowArg.equals("-f=false")) {
            jopt.setCmdLineOption("JTB_F", false);
          } else {
            throw new InvalidCmdLineException("Invalid option \"" + arg + "\".");
          }
        } else if (uppArg.startsWith("-JTB_F=")) {
          if (uppArg.equals("-JTB_F=TRUE")) {
            jopt.setCmdLineOption("JTB_F", true);
          } else if (uppArg.equals("-JTB_F=FALSE")) {
            jopt.setCmdLineOption("JTB_F", false);
          } else {
            throw new InvalidCmdLineException("Invalid option \"" + arg + "\".");
          }
        } else if (lowArg.equals("-h")) {
          printHelp();
        } else if (lowArg.startsWith("-hkd")) {
          if (!DEF_HOOK_DIR_NAME.equals(jopt.getOptions().get("JTB_HKD"))) {
            mess.warning("Option \"-d\" is already set so option \"-hkd dir\" is not used.");
          } else if (lowArg.equals("-hkd")) {
            if (++i >= aArgs.length) {
              throw new InvalidCmdLineException("Missing directory name after option \"-hkd\".");
            }
            jopt.setCmdLineOption("JTB_HKD", aArgs[i]);
          } else if (lowArg.startsWith("-hkd=")) {
            jopt.setCmdLineOption("JTB_HKD", arg.substring(5));
          } else {
            throw new InvalidCmdLineException("Invalid option \"" + arg + "\".");
          }
        } else if (uppArg.startsWith("-JTB_HKD=")) {
          if (!DEF_HOOK_DIR_NAME.equals(jopt.getOptions().get("JTB_HKD"))) {
            mess.warning("Option \"-d\" is already set so option \"-hkd dir\" is not used.");
          } else {
            jopt.setCmdLineOption("JTB_HKD", arg.substring(9));
          }
        }
        
        else if (lowArg.startsWith("-hkp")) {
          if (!DEF_HOOK_PKG_NAME.equals(jopt.getOptions().get("JTB_HKP"))) {
            mess.warning("Option \"-p\" is already set so option \"-hkp pkg\" is not used.");
          } else if (lowArg.equals("-hkp")) {
            if (++i >= aArgs.length) {
              throw new InvalidCmdLineException("Missing package name after option \"-hkp\".");
            }
            jopt.setCmdLineOption("JTB_HKP", aArgs[i]);
          } else if (lowArg.startsWith("-hkp=")) {
            jopt.setCmdLineOption("JTB_HKP", arg.substring(5));
          } else {
            throw new InvalidCmdLineException("Invalid option \"" + arg + "\".");
          }
        } else if (uppArg.startsWith("-JTB_HKP=")) {
          if (!DEF_HOOK_PKG_NAME.equals(jopt.getOptions().get("JTB_HKP"))) {
            mess.warning("Option \"-p\" is already set so option \"-hkp pkg\" is not used.");
          } else {
            jopt.setCmdLineOption("JTB_HKP", arg.substring(9));
          }
        }
        
        else if (lowArg.startsWith("-hk")) {
          if (lowArg.equals("-hk") || lowArg.equals("-hk=true")) {
            jopt.setCmdLineOption("JTB_HK", true);
          } else if (lowArg.equals("-hk=false")) {
            jopt.setCmdLineOption("JTB_HK", false);
          } else {
            throw new InvalidCmdLineException("Invalid option \"" + arg + "\".");
          }
        } else if (uppArg.startsWith("-JTB_HK=")) {
          if (uppArg.equals("-JTB_HK=TRUE")) {
            jopt.setCmdLineOption("JTB_HK", true);
          } else if (uppArg.equals("-JTB_HK=FALSE")) {
            jopt.setCmdLineOption("JTB_HK", false);
          } else {
            throw new InvalidCmdLineException("Invalid option \"" + arg + "\".");
          }
        } else if (lowArg.startsWith("-ia")) {
          if (lowArg.equals("-ia") || lowArg.equals("-ia=true")) {
            jopt.setCmdLineOption("JTB_IA", true);
          } else if (lowArg.equals("-ia=false")) {
            jopt.setCmdLineOption("JTB_IA", false);
          } else {
            throw new InvalidCmdLineException("Invalid option \"" + arg + "\".");
          }
        } else if (uppArg.startsWith("-JTB_IA=")) {
          if (uppArg.equals("-JTB_IA=TRUE")) {
            jopt.setCmdLineOption("JTB_IA", true);
          } else if (uppArg.equals("-JTB_IA=FALSE")) {
            jopt.setCmdLineOption("JTB_IA", false);
          } else {
            throw new InvalidCmdLineException("Invalid option \"" + arg + "\".");
          }
        } else if (lowArg.startsWith("-jd")) {
          if (lowArg.equals("-jd") || lowArg.equals("-jd=true")) {
            jopt.setCmdLineOption("JTB_JD", true);
          } else if (lowArg.equals("-jd=false")) {
            jopt.setCmdLineOption("JTB_JD", false);
          } else {
            throw new InvalidCmdLineException("Invalid option \"" + arg + "\".");
          }
        } else if (uppArg.startsWith("-JTB_JD=")) {
          if (uppArg.equals("-JTB_JD=TRUE")) {
            jopt.setCmdLineOption("JTB_JD", true);
          } else if (uppArg.equals("-JTB_JD=FALSE")) {
            jopt.setCmdLineOption("JTB_JD", false);
          } else {
            throw new InvalidCmdLineException("Invalid option \"" + arg + "\".");
          }
        } else if (lowArg.startsWith("-nd")) {
          if (!DEF_ND_DIR_NAME.equals(jopt.getOptions().get("JTB_ND"))) {
            mess.warning("Option \"-d\" is already set so option \"-nd dir\" is not used.");
          } else if (lowArg.equals("-nd")) {
            if (++i >= aArgs.length) {
              throw new InvalidCmdLineException("Missing directory name after option \"-nd\".");
            }
            jopt.setCmdLineOption("JTB_ND", aArgs[i]);
          } else if (lowArg.startsWith("-nd=")) {
            jopt.setCmdLineOption("JTB_ND", arg.substring(4));
          } else {
            throw new InvalidCmdLineException("Invalid option \"" + arg + "\".");
          }
        } else if (uppArg.startsWith("-JTB_ND=")) {
          if (!DEF_ND_DIR_NAME.equals(jopt.getOptions().get("JTB_ND"))) {
            mess.warning("Option \"-d\" is already set so option \"-nd dir\" is not used.");
          } else {
            jopt.setCmdLineOption("JTB_ND", arg.substring(8));
          }
        }
        
        else if (lowArg.startsWith("-noplg")) {
          if (lowArg.equals("-noplg") || lowArg.equals("-noplg=true")) {
            jopt.setCmdLineOption("JTB_NOPLG", true);
          } else if (lowArg.equals("-noplg=false")) {
            jopt.setCmdLineOption("JTB_NOPLG", false);
          } else {
            throw new InvalidCmdLineException("Invalid option \"" + arg + "\".");
          }
        } else if (uppArg.startsWith("-JTB_NOPLG=")) {
          if (uppArg.equals("-JTB_NOPLG=TRUE")) {
            jopt.setCmdLineOption("JTB_NOPLG", true);
          } else if (uppArg.equals("-JTB_NOPLG=FALSE")) {
            jopt.setCmdLineOption("JTB_NOPLG", false);
          } else {
            throw new InvalidCmdLineException("Invalid option \"" + arg + "\".");
          }
        } else if (lowArg.startsWith("-nosig")) {
          if (lowArg.equals("-nosig") || lowArg.equals("-nosig=true")) {
            jopt.setCmdLineOption("JTB_NOSIG", true);
          } else if (lowArg.equals("-nosig=false")) {
            jopt.setCmdLineOption("JTB_NOSIG", false);
          } else {
            throw new InvalidCmdLineException("Invalid option \"" + arg + "\".");
          }
        } else if (uppArg.startsWith("-JTB_NOSIG=")) {
          if (uppArg.equals("-JTB_NOSIG=TRUE")) {
            jopt.setCmdLineOption("JTB_NOSIG", true);
          } else if (uppArg.equals("-JTB_NOSIG=FALSE")) {
            jopt.setCmdLineOption("JTB_NOSIG", false);
          } else {
            throw new InvalidCmdLineException("Invalid option \"" + arg + "\".");
          }
        } else if (lowArg.startsWith("-novis")) {
          if (lowArg.equals("-novis") || lowArg.equals("-novis=true")) {
            jopt.setCmdLineOption("JTB_NOVIS", true);
          } else if (lowArg.equals("-novis=false")) {
            jopt.setCmdLineOption("JTB_NOVIS", false);
          } else {
            throw new InvalidCmdLineException("Invalid option \"" + arg + "\".");
          }
        } else if (uppArg.startsWith("-JTB_NOVIS=")) {
          if (uppArg.equals("-JTB_NOVIS=TRUE")) {
            jopt.setCmdLineOption("JTB_NOVIS", true);
          } else if (uppArg.equals("-JTB_NOVIS=FALSE")) {
            jopt.setCmdLineOption("JTB_NOVIS", false);
          } else {
            throw new InvalidCmdLineException("Invalid option \"" + arg + "\".");
          }
        } else if (lowArg.startsWith("-npfx")) {
          if (lowArg.equals("-npfx")) {
            if (++i >= aArgs.length) {
              throw new InvalidCmdLineException("Missing prefix after option \"-npfx\".");
            }
            jopt.setCmdLineOption("JTB_NPFX", aArgs[i]);
          } else if (lowArg.startsWith("-npfx=")) {
            jopt.setCmdLineOption("JTB_NPFX", arg.substring(6));
          } else {
            throw new InvalidCmdLineException("Invalid option \"" + arg + "\".");
          }
        } else if (uppArg.startsWith("-JTB_NPFX=")) {
          jopt.setCmdLineOption("JTB_NPFX", arg.substring(10));
        } else if (lowArg.startsWith("-np")) {
          if (!DEF_ND_PKG_NAME.equals(jopt.getOptions().get("JTB_NP"))) {
            mess.warning("Option \"-p\" is already set so option \"-np pkg\" is not used.");
          } else if (lowArg.equals("-np")) {
            if (++i >= aArgs.length) {
              throw new InvalidCmdLineException("Missing package name after option \"-np\".");
            }
            jopt.setCmdLineOption("JTB_NP", aArgs[i]);
          } else if (lowArg.startsWith("-np=")) {
            jopt.setCmdLineOption("JTB_NP", arg.substring(4));
          } else {
            throw new InvalidCmdLineException("Invalid option \"" + arg + "\".");
          }
        } else if (uppArg.startsWith("-JTB_NP=")) {
          if (!DEF_ND_PKG_NAME.equals(jopt.getOptions().get("JTB_NP"))) {
            mess.warning("Option \"-p\" is already set so option \"-np pkg\" is not used.");
          } else {
            jopt.setCmdLineOption("JTB_NP", arg.substring(8));
          }
        }
        
        else if (lowArg.startsWith("-nsfx")) {
          if (lowArg.equals("-nsfx")) {
            if (++i >= aArgs.length) {
              throw new InvalidCmdLineException("Missing suffix after option \"-nsfx\".");
            }
            jopt.setCmdLineOption("JTB_NSFX", aArgs[i]);
          } else if (lowArg.startsWith("-nsfx=")) {
            jopt.setCmdLineOption("JTB_NSFX", arg.substring(6));
          } else {
            throw new InvalidCmdLineException("Invalid option \"" + arg + "\".");
          }
        } else if (uppArg.startsWith("-JTB_NSFX=")) {
          jopt.setCmdLineOption("JTB_NSFX", arg.substring(10));
        } else if (lowArg.startsWith("-ns")) {
          if (lowArg.equals("-ns")) {
            if (++i >= aArgs.length) {
              throw new InvalidCmdLineException("Missing class name after option \"-ns\".");
            }
            jopt.setCmdLineOption("JTB_NS", aArgs[i]);
          } else if (lowArg.startsWith("-ns=")) {
            jopt.setCmdLineOption("JTB_NS", arg.substring(4));
          } else {
            throw new InvalidCmdLineException("Invalid option \"" + arg + "\".");
          }
        } else if (uppArg.startsWith("-JTB_NS=")) {
          jopt.setCmdLineOption("JTB_NS", arg.substring(8));
        } else if (lowArg.startsWith("-o")) {
          if (lowArg.equals("-o")) {
            if (++i >= aArgs.length) {
              throw new InvalidCmdLineException("Missing file name after option \"-o\".");
            }
            jopt.setCmdLineOption("JTB_O", aArgs[i]);
          } else if (lowArg.startsWith("-o=")) {
            jopt.setCmdLineOption("JTB_O", arg.substring(3));
          } else {
            throw new InvalidCmdLineException("Invalid option \"" + arg + "\".");
          }
        } else if (uppArg.startsWith("-JTB_O=")) {
          jopt.setCmdLineOption("JTB_O", arg.substring(7));
        } else if (lowArg.startsWith("-p")) {
          if (lowArg.equals("-p")) {
            if (++i >= aArgs.length) {
              throw new InvalidCmdLineException("Missing package name after option \"-p\".");
            }
            arg = aArgs[i];
            jopt.setCmdLineOption("JTB_P", arg);
            updatePkgs(arg);
          } else if (lowArg.startsWith("-p=")) {
            jopt.setCmdLineOption("JTB_P", arg.substring(3));
            updatePkgs(arg.substring(3));
          } else {
            throw new InvalidCmdLineException("Invalid option \"" + arg + "\".");
          }
        } else if (uppArg.startsWith("-JTB_P=")) {
          jopt.setCmdLineOption("JTB_P", arg.substring(7));
          updatePkgs(arg.substring(7));
        }
        
        else if (lowArg.startsWith("-pp")) {
          if (lowArg.equals("-pp") || lowArg.equals("-pp=true")) {
            jopt.setCmdLineOption("JTB_PP", true);
          } else if (lowArg.equals("-pp=false")) {
            jopt.setCmdLineOption("JTB_PP", false);
          } else {
            throw new InvalidCmdLineException("Invalid option \"" + arg + "\".");
          }
        } else if (uppArg.startsWith("-JTB_PP=")) {
          if (uppArg.equals("-JTB_PP=TRUE")) {
            jopt.setCmdLineOption("JTB_PP", true);
          } else if (uppArg.equals("-JTB_PP=FALSE")) {
            jopt.setCmdLineOption("JTB_PP", false);
          } else {
            throw new InvalidCmdLineException("Invalid option \"" + arg + "\".");
          }
        } else if (lowArg.startsWith("-printer")) {
          if (lowArg.equals("-printer") || lowArg.equals("-printer=true")) {
            jopt.setCmdLineOption("JTB_PRINTER", true);
          } else if (lowArg.equals("-printer=false")) {
            jopt.setCmdLineOption("JTB_PRINTER", false);
          } else {
            throw new InvalidCmdLineException("Invalid option \"" + arg + "\".");
          }
        } else if (uppArg.startsWith("-JTB_PRINTER=")) {
          if (uppArg.equals("-JTB_PRINTER=TRUE")) {
            jopt.setCmdLineOption("JTB_PRINTER", true);
          } else if (uppArg.equals("-JTB_PRINTER=FALSE")) {
            jopt.setCmdLineOption("JTB_PRINTER", false);
          } else {
            throw new InvalidCmdLineException("Invalid option \"" + arg + "\".");
          }
        } else if (lowArg.equals("-si")) {
          in = System.in;
          jtbInputFileName = "standard input";
        }
        
        else if (lowArg.startsWith("-tkjj")) {
          if (lowArg.equals("-tkjj") || lowArg.equals("-tkjj=true")) {
            jopt.setCmdLineOption("JTB_TKJJ", true);
          } else if (lowArg.equals("-tkjj=false")) {
            jopt.setCmdLineOption("JTB_TKJJ", false);
          } else {
            throw new InvalidCmdLineException("Invalid option \"" + arg + "\".");
          }
        } else if (uppArg.startsWith("-JTB_TKJJ=")) {
          if (uppArg.equals("-JTB_TKJJ=TRUE")) {
            jopt.setCmdLineOption("JTB_TKJJ", true);
          } else if (uppArg.equals("-JTB_TKJJ=FALSE")) {
            jopt.setCmdLineOption("JTB_TKJJ", false);
          } else {
            throw new InvalidCmdLineException("Invalid option \"" + arg + "\".");
          }
        } else if (lowArg.startsWith("-tk")) {
          if (lowArg.equals("-tk") || lowArg.equals("-tk=true")) {
            jopt.setCmdLineOption("JTB_TK", true);
          } else if (lowArg.equals("-tk=false")) {
            jopt.setCmdLineOption("JTB_TK", false);
          } else {
            throw new InvalidCmdLineException("Invalid option \"" + arg + "\".");
          }
        } else if (uppArg.startsWith("-JTB_TK=")) {
          if (uppArg.equals("-JTB_TK=TRUE")) {
            jopt.setCmdLineOption("JTB_TK", true);
          } else if (uppArg.equals("-JTB_TK=FALSE")) {
            jopt.setCmdLineOption("JTB_TK", false);
          } else {
            throw new InvalidCmdLineException("Invalid option \"" + arg + "\".");
          }
        } else if (lowArg.startsWith("-vd")) {
          if (!DEF_VIS_DIR_NAME.equals(jopt.getOptions().get("JTB_VD"))) {
            mess.warning("Option \"-d\" is already set so option \"-vd dir\" is not used.");
          } else if (lowArg.equals("-vd")) {
            if (++i >= aArgs.length) {
              throw new InvalidCmdLineException("Missing directory name after option \"-vd\".");
            }
            jopt.setCmdLineOption("JTB_VD", aArgs[i]);
          } else if (lowArg.startsWith("-vd=")) {
            jopt.setCmdLineOption("JTB_VD", arg.substring(4));
          } else {
            throw new InvalidCmdLineException("Invalid option \"" + arg + "\".");
          }
        } else if (uppArg.startsWith("-JTB_VD=")) {
          if (!DEF_VIS_PKG_NAME.equals(jopt.getOptions().get("JTB_VD"))) {
            mess.warning("Option \"-d\" is already set so option \"-vp dir\" is not used.");
          } else {
            jopt.setCmdLineOption("JTB_VD", arg.substring(8));
          }
        }
        
        else if (lowArg.startsWith("-vis")) {
          if (lowArg.equals("-vis")) {
            if (++i >= aArgs.length) {
              throw new InvalidCmdLineException("Missing visitors specification after option \"-vis\".");
            }
            arg = aArgs[i];
            if (!jopt.createVisitorsList(arg)) {
              throw new InvalidCmdLineException("Invalid visitors specification \"" + arg + "\".");
            }
            jopt.setCmdLineOption("JTB_VIS", arg);
          } else if (lowArg.startsWith("-vis=")) {
            final String str = arg.substring(5);
            if (!jopt.createVisitorsList(str)) {
              throw new InvalidCmdLineException("Invalid visitors specification \"" + arg + "\".");
            }
            jopt.setCmdLineOption("JTB_VIS", str);
          } else {
            throw new InvalidCmdLineException("Invalid option \"" + arg + "\".");
          }
        } else if (uppArg.startsWith("-JTB_VIS=")) {
          if (!jopt.createVisitorsList(arg.substring(9))) {
            throw new InvalidCmdLineException("Invalid visitors specification \"" + arg + "\".");
          }
          jopt.setCmdLineOption("JTB_VIS", arg.substring(9));
        }
        
        else if (lowArg.startsWith("-vp")) {
          if (!DEF_VIS_PKG_NAME.equals(jopt.getOptions().get("JTB_VP"))) {
            mess.warning("Option \"-p\" is already set so option \"-vp pkg\" is not used.");
          } else if (lowArg.equals("-vp")) {
            if (++i >= aArgs.length) {
              throw new InvalidCmdLineException("Missing package name after option \"-vp\".");
            }
            jopt.setCmdLineOption("JTB_VP", aArgs[i]);
          } else if (lowArg.startsWith("-vp=")) {
            jopt.setCmdLineOption("JTB_VP", arg.substring(4));
          } else {
            throw new InvalidCmdLineException("Invalid option \"" + arg + "\".");
          }
        } else if (uppArg.startsWith("-JTB_VP=")) {
          if (!DEF_VIS_PKG_NAME.equals(jopt.getOptions().get("JTB_VP"))) {
            mess.warning("Option \"-p\" is already set so option \"-vp pkg\" is not used.");
          } else {
            jopt.setCmdLineOption("JTB_VP", arg.substring(8));
          }
        }
        
        else if (lowArg.startsWith("-w")) {
          if (lowArg.equals("-w") || lowArg.equals("-w=true")) {
            jopt.setCmdLineOption("JTB_W", true);
          } else if (lowArg.equals("-w=false")) {
            jopt.setCmdLineOption("JTB_W", false);
          } else {
            throw new InvalidCmdLineException("Invalid option \"" + arg + "\".");
          }
        } else if (uppArg.startsWith("-JTB_W=")) {
          if (uppArg.equals("-JTB_W=TRUE")) {
            jopt.setCmdLineOption("JTB_W", true);
          } else if (uppArg.equals("-JTB_W=FALSE")) {
            jopt.setCmdLineOption("JTB_W", false);
          } else {
            throw new InvalidCmdLineException("Invalid option \"" + arg + "\".");
          }
        }
        
      }
    }
    
    if (returnVal) {
      return true;
    } else {
      printHelp();
      return false;
    }
  }
  
  /**
   * Update nodes / visitors / hook directories.
   *
   * @param arg - the base directory
   */
  private void updateDirs(final String arg) {
    if (!DEF_HOOK_DIR_NAME.equals(jopt.getOptions().get("JTB_HKD"))) {
      mess.warning("Option \"-d\" overwrites option \"-hkd dir\".");
    }
    jopt.setCmdLineOption("JTB_HKD", arg + File.separator + DEF_HOOK_DIR_NAME);
    
    if (!DEF_ND_DIR_NAME.equals(jopt.getOptions().get("JTB_ND"))) {
      mess.warning("Option \"-d\" overwrites option \"-nd pkg\".");
    }
    jopt.setCmdLineOption("JTB_ND", arg + File.separator + DEF_ND_DIR_NAME);
    
    if (!DEF_VIS_DIR_NAME.equals(jopt.getOptions().get("JTB_VD"))) {
      mess.warning("Option \"-d\" overwrites option \"-vd dir\".");
    }
    jopt.setCmdLineOption("JTB_VD", arg + File.separator + DEF_VIS_DIR_NAME);
  }
  
  /**
   * Update nodes / visitors / hook packages.
   *
   * @param arg - the base package
   */
  private void updatePkgs(final String arg) {
    if (!DEF_HOOK_PKG_NAME.equals(jopt.getOptions().get("JTB_HKP"))) {
      mess.warning("Option \"-p\" overwrites option \"-hkp pkg\".");
    }
    jopt.setCmdLineOption("JTB_HKP", arg + "." + DEF_HOOK_PKG_NAME);
    
    if (!DEF_ND_PKG_NAME.equals(jopt.getOptions().get("JTB_NP"))) {
      mess.warning("Option \"-p\" overwrites option \"-np pkg\".");
    }
    jopt.setCmdLineOption("JTB_NP", arg + "." + DEF_ND_PKG_NAME);
    
    if (!DEF_VIS_PKG_NAME.equals(jopt.getOptions().get("JTB_VP"))) {
      mess.warning("Option \"-p\" overwrites option \"-vp pkg\".");
    }
    jopt.setCmdLineOption("JTB_VP", arg + "." + DEF_VIS_PKG_NAME);
  }
  
  /**
   * Convert nodes and visitors output directories and jj output file (relatives to the grammar file) to
   * normalized absolute paths.
   */
  private void convertPathsToAbsolute() {
    String dir = inDir + File.separator;
    if (jopt.grammarDirectoryName != null) {
      File ndn = new File(jopt.grammarDirectoryName);
      if (!ndn.isAbsolute()) {
        ndn = new File(dir + jopt.grammarDirectoryName);
        jopt.grammarDirectoryName = ndn.toPath().normalize().toString();
      }
    }
    dir = jopt.jjOutDirName + File.separator;
    if (jopt.nodesDirName != null) {
      File ndn = new File(jopt.nodesDirName);
      if (!ndn.isAbsolute()) {
        ndn = new File(dir + jopt.nodesDirName);
        jopt.nodesDirName = ndn.toPath().normalize().toString();
      }
    }
    if (jopt.visitorsDirName != null) {
      File vdn = new File(jopt.visitorsDirName);
      if (!vdn.isAbsolute()) {
        vdn = new File(dir + jopt.visitorsDirName);
        jopt.visitorsDirName = vdn.toPath().normalize().toString();
      }
    }
    if (jopt.signatureDirName != null) {
      File sdn = new File(jopt.signatureDirName);
      if (!sdn.isAbsolute()) {
        sdn = new File(dir + jopt.signatureDirName);
        jopt.signatureDirName = sdn.toPath().normalize().toString();
      }
    }
    if (jopt.hookDirName != null) {
      File hdn = new File(jopt.hookDirName);
      if (!hdn.isAbsolute()) {
        hdn = new File(dir + jopt.hookDirName);
        jopt.hookDirName = hdn.toPath().normalize().toString();
      }
    }
    File jjf = new File(jopt.jtbOutputFileName);
    if (!jjf.isAbsolute()) {
      jjf = new File(dir + jopt.jtbOutputFileName);
      jopt.jtbOutputFileName = jjf.toPath().normalize().toString();
    }
  }
  
  /**
   * Creates a directory if it does not exist.
   *
   * @param aDirName - the directory name
   * @return - the corresponding File
   */
  private File createDirectory(final String aDirName) {
    final File dir = new File(aDirName);
    if (!dir.exists()) {
      if (dir.mkdirs()) {
        mess.info("\"" + dir + "\" directory created.");
      } else {
        mess.softErr("Unable to create \"" + dir + "\" directory.");
      }
    } else if (!dir.isDirectory()) {
      mess.softErr("\"" + dir + "\" exists but is not a directory.");
    }
    return dir;
  }
  
  /**
   * Creates a parent directory if it does not exist.
   *
   * @param aFileName - the file name
   * @return - the corresponding File for the parent directory
   */
  private File createParentDirectory(final String aFileName) {
    final File fn = new File(aFileName);
    final File dir = fn.getParentFile();
    if (!dir.exists()) {
      if (dir.mkdirs()) {
        mess.info("\"" + dir + "\" parent directory created for " + fn.getName() + ".");
      } else {
        mess.softErr("Unable to create \"" + dir + "\" parent directory for " + fn.getName() + ".");
      }
    } else if (!dir.isDirectory()) {
      mess.softErr("\"" + dir + "\" exists but is not a directory.");
    }
    return dir;
  }
  
  /**
   * Generates the syntax tree nodes files.
   *
   * @param aClasses - the list of classes info
   * @param aFg - the {@link UserFilesGenerator} which will generate the user classes
   * @param aNodesDir - the nodes directory File
   * @throws Exception - if Exception generating the files
   */
  private void generateSyntaxtreeNodes(final List<UserClassInfo> aClasses, final UserFilesGenerator aFg,
      final File aNodesDir) throws Exception {
    // user nodes must be created before NodeConstants.java for signatures to be created
    int rc = aFg.genUserNodesFiles(aNodesDir);
    printlnInfo(
        rc + " syntax tree node class files " + "generated into directory \"" + jopt.nodesDirName + "\".");
    
    final BaseNodesGenerator bg = new BaseNodesGenerator(jopt, ccg, mess);
    rc = bg.genBaseNodesFiles(aClasses);
    printlnInfo(
        rc + " base node class+interface files " + "generated into directory \"" + jopt.nodesDirName + "\".");
  }
  
  /**
   * Generates visitors. CODEJAVA
   *
   * @param aVg - the {@link VisitorsGenerator}
   * @param aVisitorDir - the visitor directory File
   * @throws IOException - if IO Exception writing the files
   */
  private void generateVisitors(final VisitorsGenerator aVg, final File aVisitorDir) throws IOException {
    
    String fn = "";
    for (final VisitorInfo vi : jopt.visitorsList) {
      fn = vi.interfaceName + ".java";
      final int rc = aVg.genIVisitorFile(vi, aVisitorDir);
      if (rc == 0) {
        printlnInfo(
            "Visitor interface \"" + fn + "\" generated into directory \"" + jopt.visitorsDirName + "\".");
      } else {
        printlnInfo(
            "\"" + fn + "\" already exists in directory \"" + jopt.visitorsDirName + "\".  Won't overwrite.");
      }
    }
    
    for (final VisitorInfo vi : jopt.visitorsList) {
      fn = vi.dfVisitorName + ".java";
      final int rc = aVg.genDepthFirstVisitorFile(vi, aVisitorDir);
      if (rc == 0) {
        printlnInfo(
            "Visitor class \"" + fn + "\" generated into directory \"" + jopt.visitorsDirName + "\".");
      } else {
        printlnInfo(
            "\"" + fn + "\" already exists in directory \"" + jopt.visitorsDirName + "\".  Won't overwrite.");
      }
    }
  }
  
  /**
   * Generates the signature files. CODEJAVA
   *
   * @param aVg - the {@link VisitorsGenerator}
   * @param aSignatureDir - the signature directory File
   * @throws IOException - if IO Exception writing the files
   */
  private static void generateSignatureFiles(final VisitorsGenerator aVg, final File aSignatureDir)
      throws IOException {
    int rc = aVg.genSigAnnFile(aSignatureDir);
    if (rc == 0) {
      printlnInfo(
          "Signature annotation \"" + sigAnnName + ".java\" generated into directory \"" + aSignatureDir);
    } else {
      printlnInfo("\"" + sigAnnName + ".java\" already exists in directory \"" + aSignatureDir + "\"."
          + "  Won't overwrite.  Delete it if you want JTB to regenerate it.");
    }
    
    rc = aVg.genSigAnnProcFile(aSignatureDir);
    if (rc == 0) {
      printlnInfo("Signature annotation processor \"" + sigAnnProcName + ".java\" generated into directory \""
          + aSignatureDir);
    } else {
      printlnInfo("\"" + sigAnnProcName + ".java\" already exists in directory \"" + aSignatureDir + "\"."
          + "  Won't overwrite.  Delete it if you want JTB to regenerate it.");
    }
  }
  
  /**
   * Generates the node scope hook files.
   *
   * @param aFg - the {@link UserFilesGenerator}
   * @param aHookDir - the hook directory File
   * @throws IOException - if IO Exception writing the files
   */
  private void generateHookFiles(final UserFilesGenerator aFg, final File aHookDir) throws IOException {
    int rc = aFg.genIEnterExitHookFile(aHookDir);
    if (rc == 0) {
      printlnInfo("Hook interface \"" + iEnterExitHook + ".java\" generated into directory \""
          + jopt.hookDirName + "\".");
    } else {
      printlnInfo("\"" + iEnterExitHook + ".java\" already exists.  Won't overwrite.");
    }
    
    rc = aFg.genEmtpyEnterExitHookFile(aHookDir);
    if (rc == 0) {
      printlnInfo("Hook empty implementation \"" + emptyEnterExitHook + ".java\" generated into directory \""
          + jopt.hookDirName + "\".");
    } else {
      printlnInfo("\"" + emptyEnterExitHook + ".java\" already exists.  Won't overwrite.");
    }
  }
  
  /**
   * Calls the external generator class through Java Reflection API.
   *
   * @param aClasses - the list of classes info
   * @return >= 0 if generation successful, < 0 if error
   */
  private int callExternalGenerator(final List<UserClassInfo> aClasses) {
    try {
      final Class<?> egc = Class.forName(jopt.externalGeneratorClass);
      final Object egi = egc.getDeclaredConstructor().newInstance();
      final Method egm = egc.getMethod(jtbEgInvokedMethode, Map.class);
      // the "freemarker data model" root
      final Map<String, Object> dm = new HashMap<>();
      // put some of the JTB Constants
      final Map<String, Object> bag = new HashMap<>();
      // pass other members ???
      bag.put("jtb_version", JTB_VERSION);
      dm.put(jtb_constants_dm_key, bag);
      // put a clone of the jtb options map
      dm.put(jtb_options_dm_key, ((HashMap<String, Object>) jopt.getOptions()).clone());
      // put a clone of the jtb base nodes interfaces list
      dm.put(jtb_base_interfaces_dm_key, Arrays.asList(baseNodesInterfaces));
      // put a clone of the jtb base nodes classes list
      dm.put(jtb_base_nodes_dm_key, Arrays.asList(baseNodesClasses));
      // put a clone of the jtb user nodes list
      dm.put(jtb_user_nodes_dm_key, new ArrayList<>(aClasses));
      // put a clone of the jtb nodes which must not be created map
      dm.put(jtb_notTbcNodes_dm_key, ((HashMap<String, String>) gdbv.getNotTbcNodesHM()).clone());
      // put a clone of the jtb all nodes map
      dm.put(jtb_prod_dm_key, ((HashMap<String, String>) gdbv.getProdHM()).clone());
      // put a clone of the jtb number of sub-nodes to be created map
      dm.put(jtb_nbSubNodesTbc_dm_key, ((HashMap<INode, Integer>) gdbv.getNbSubNodesTbcHM()).clone());
      final Object rco = egm.invoke(egi, dm);
      if (rco instanceof Integer) {
        final int rci = ((Integer) rco).intValue();
        if (rci >= 0) {
          printlnInfo("method " + jopt.externalGeneratorClass + "." + jtbEgInvokedMethode
              + " was executed successfully (returned " + rci + ").");
        } else {
          printlnError("Error: method " + jopt.externalGeneratorClass + "." + jtbEgInvokedMethode
              + " was executed but returned and error (" + rci + ").");
        }
        return rci;
      } else {
        printlnError("Error: invoked method " + jopt.externalGeneratorClass + "." + jtbEgInvokedMethode
            + "(Map) returned a non Integer result (" + rco.getClass() + "); check its return definition.");
        return EG_ERR;
      }
    } catch (@SuppressWarnings("unused") final ClassNotFoundException ex) {
      printlnError("Error: class " + jopt.externalGeneratorClass
          + " was not found; check classpath and class in -eg (" + jopt.externalGeneratorClass + ").");
      return EG_ERR;
    } catch (@SuppressWarnings("unused") final InstantiationException ex) {
      printlnError("Error: class " + jopt.externalGeneratorClass
          + " could not be instantiated; check its constructor.");
      return EG_ERR;
    } catch (final IllegalAccessException ex) {
      printlnError("Error: class " + jopt.externalGeneratorClass + " raised an " + ex + "; check it all.");
      return EG_ERR;
    } catch (@SuppressWarnings("unused") final NoSuchMethodException ex) {
      printlnError("Error: method " + jopt.externalGeneratorClass + "." + jtbEgInvokedMethode
          + "(Map) was not found; check your code.");
      return EG_ERR;
    } catch (final SecurityException ex) {
      printlnError("Error: " + jopt.externalGeneratorClass + " raised a " + ex + "; check it all.");
      return EG_ERR;
    } catch (final IllegalArgumentException ex) {
      printlnError("Error: invoking method " + jopt.externalGeneratorClass + "." + jtbEgInvokedMethode
          + "(Map) raised a " + ex + "; check its argument declaration.");
      return EG_ERR;
    } catch (final InvocationTargetException ex) {
      printlnError("Error: invoking method " + jopt.externalGeneratorClass + "." + jtbEgInvokedMethode
          + "(Map) raised a " + ex + "; check it all.");
      return EG_ERR;
    }
  }
  
  /**
   * Generates the TreeFormatter and TreeDumper visitors.
   *
   * @param aClasses - the list of classes info
   * @throws IOException - if IO Exception writing the files
   */
  private void generateTreeFormatterDumper(final List<UserClassInfo> aClasses) throws IOException {
    final TreeDumperGenerator tdg = new TreeDumperGenerator(jopt, mess);
    tdg.generateTreeDumper();
    int rc = tdg.saveToFile();
    if (rc == 0) {
      printlnInfo("Visitor class \"" + TreeDumperGenerator.outFilename + "\" generated into directory \""
          + jopt.visitorsDirName + "\".");
    } else {
      printlnInfo("\"" + TreeDumperGenerator.outFilename + "\" already exists."
          + "  Won't overwrite.  Delete it if you want JTB to regenerate it.");
    }
    
    final TreeFormatterGenerator tfg = new TreeFormatterGenerator(jopt, ccg, mess, aClasses);
    tfg.generateTreeFormatter();
    rc = tfg.saveToFile();
    if (rc == 0) {
      printlnInfo("Visitor class \"" + TreeFormatterGenerator.outFilename + "\" generated into directory \""
          + jopt.visitorsDirName + "\".");
    } else {
      printlnInfo("\"" + TreeFormatterGenerator.outFilename + "\" already exists."
          + "  Won't overwrite.  Delete it if you want JTB to regenerate it.");
    }
  }
  
  /**
   * Prints an information message on stdout.
   *
   * @param aMsg - a message
   */
  public static void printlnInfo(final String aMsg) {
    System.err.flush();
    System.out.println(PROG_NAME + ": " + aMsg);
  }
  
  /**
   * Prints an error message on stderr.
   *
   * @param aMsg - a message
   */
  public static void printlnError(final String aMsg) {
    System.out.flush();
    System.err.println(PROG_NAME + ": error: " + aMsg);
  }
  
  /**
   * Displays program usage.
   */
  private static void printHelp() {
    System.out.print("Usage: " + PROG_NAME + " [OPTIONS] " + "[inputfile]\n" + "\n" + "Standard options:\n"
        + "  -chm           Generate nodes children methods - since 1.5.0\n"
        + "  -cl            Print a list of the classes generated to standard out\n"
        + "  -d dir         \"-d dir\" is short for (and overwrites) \"-nd dir/syntaxtree -vd dir/visitor -hkd dir/hook\"\n"
        + "  -dl            Generate depth level info\n"
        + "  -do            Print a list of resulting (file and command line) options to standard out - since 1.5.0\n"
        + "  -e             Suppress JTB semantic error checking\n"
        + "  -eg class      Calls class (to be found in the classpath) to run a user supplied generator - since 1.5.0\n"
        + "  -f             Use descriptive node class field names\n"
        + "  -h             Display this help message and quit\n"
        + "  -hk            Generate node scope hook inteface and implementation and method calls - since 1.5.0\n"
        + "  -hkd dir       Use dir as the directory for the node scope hook interface and class - since 1.5.0\n"
        + "  -hkp pkg       Use pkg as the package for the node scope hook interface and class - since 1.5.0\n"
        + "  -ia            Inline visitors accept methods on base classes\n"
        + "  -jd            Generate JavaDoc-friendly comments in the nodes and visitors\n"
        + "  -nd dir        Use dir as the package for the syntax tree nodes\n"
        + "  -noplg         Do not parallelize user nodes generation - since 1.5.0\n"
        + "  -nosig         Do not generate signature annotations in visitors - since 1.5.0\n"
        + "  -novis         Do not generate visitors interfaces and classes - since 1.5.0\n"
        + "  -np pkg        Use pkg as the package for the syntax tree nodes\n"
        + "  -npfx str      Use str as prefix for the syntax tree nodes\n"
        + "  -ns class      Use class as the class which all node classes will extend\n"
        + "  -nsfx str      Use str as suffix for the syntax tree nodes\n"
        + "  -o file        Use file as the filename for the annotated output grammar\n"
        + "  -p pkg         \"-p pkg\" is short for (and overwrites) \"-np pkg.syntaxtree -vp pkg.visitor -hkp pkg.hook\"\n"
        + "  -pp            Generate parent pointers in all node classes\n"
        + "  -printer       Generate a syntax tree dumping visitor\n"
        + "  -si            Read from standard input rather than a file\n"
        + "  -tk            Generate special tokens in the tree's NodeTokens\n"
        + "  -tkjj          Generate special tokens in the annotated grammar (implies -tk) - since 1.5.0\n"
        + "  -va            Generate visitors with an argument of a vararg type\n"
        + "  -vd dir        Use dir as the package for the default visitor classes\n"
        + "  -vis str       Generate visitors with specified suffix, return type and argument(s) type(s) - since 1.5.0\n"
        + "                 str: sfx,ret,args(;sfx,ret,args)* (ret can be 'void', args can be 'none')\n"
        + "  -vp pkg        Use pkg as the package for the default visitor classes\n"
        + "  -w             Do not overwrite existing files\n" + "\n");
  }
  
  /**
   * Inner class for managing command line errors.
   */
  class InvalidCmdLineException extends Exception {
    
    /** Default serialVersionUID */
    static final long serialVersionUID = 1L;
    
    /**
     * Standard constructor with a message.
     *
     * @param s - the exception message
     */
    InvalidCmdLineException(final String s) {
      super(s);
    }
  }
  
}

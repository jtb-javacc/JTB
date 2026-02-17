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
 *          1.5.1 : 08/2023 : MMa : changes due to NodeToken replaced by Token<br>
 *          1.5.1 : 09/2023 : MMa : fixed directories values
 * @version 1.5.3 : 10-12/2025 : MMa : fixed command line arguments handling, and changes due to Token split
 *          back to NodeToken and Token; added double quotes management on arguments<br>
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
   * The input (jtb) file name (must be set in main)
   */
  String                            jtbInputFileName;

  /** No error */
  public static final int OK        = 0;
  /** Command line error */
  public static final int CL_ERR    = -1;
  /** {@link GlobalDataBuilder} error */
  public static final int GDB_ERR   = -2;
  /** {@link SemanticChecker} error */
  public static final int SC_ERR    = -4;
  /** {@link ClassesFinder} error */
  public static final int CF_ERR    = -8;
  /** {@link JJFileAnnotator} error */
  public static final int ANN_ERR   = -16;
  /** {@link UserFilesGenerator} & {@link VisitorsGenerator} directory creation error */
  public static final int DI_ERR    = -32;
  /** Parsing file options error */
  public static final int FO_ERR    = -64;
  /** {@link InvalidCmdLineException} exception */
  public static final int CL_EX     = -128;
  /** {@link ParseException} exception */
  public static final int PARSE_EX  = -256;
  /** Running external generator error */
  public static final int EG_ERR    = -512;
  /** IO exception */
  public static final int IO_EX     = -1024;
  /** Programmatic error */
  public static final int OTHER_EX  = -2048;
  /** Programmatic error */
  public static final int PROG_ERR  = -4096;
  /** Other error */
  public static final int OTHER_THR = -9192;

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
  public static int do_main(final String aArgs[]) {
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

      // parse the input file (it will read the input file options)
      printlnInfo("Reading jtb input file " + jtbInputFileName);
      final JTBParser jtbParser = new JTBParser(in);
      final INode root = jtbParser.JavaCCInput(jopt);
      printlnInfo("jtb input file parsed successfully.");
      jopt.parserName = jtbParser.parser_class_name;

      // compute all the global variables from the (command line & grammar file) options
      jopt.computeGlobalVariablesFromOptions(jtbParser.grammarPackage, inDir,
          jtbParser.opt.getOutputDirectory());
      if (mess.errorCount() > 0) {
        mess.printSummary();
        return FO_ERR;
      }

      // convert directories and jj output file to absolute paths
      convertPathsToAbsolute();

      // create all directories if they do not exist
      createDirectory(jopt.jjOutputDirName);
      createParentDirectory(jopt.jtbOutputFileName);
      File visitorsDir = null;
      if (jopt.visitorsDirName != null && !jopt.noVisitors) {
        visitorsDir = createDirectory(jopt.visitorsDirName);
      }
      File signatureDir = null;
      if (jopt.signatureDirName != null && !jopt.noSignature) {
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
        final VisitorsGenerator vg = new VisitorsGenerator(gdbv, ccg, classes);
        generateVisitors(vg, visitorsDir);
        if (!jopt.noSignature) {
          generateSignatureFiles(vg, signatureDir);
        }
      }

      // generate hook files
      if (jopt.hook) {
        generateHookFiles(ufg, hookDir);
      }

      // call the external generator
      if (jopt.externalGeneratorClass != null) {
        final int rceg = callExternalGenerator(classes);
        if (rceg < 0) {
          return rceg;
        }
      }

      // generate TreeFormatter & Dumper
      if (jopt.printerToolkit) {
        generateTreeFormatterDumper(classes);
      }

      mess.printSummary();
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
   * Processes command line arguments, putting options keys/values in the options map.<br>
   * Standalone controls are applied on these command line arguments, leading to
   * {@link InvalidCmdLineException} exceptions or {@link Messages#warning(String)} warnings.<br>
   * No cross controls are performed here (they are done in
   * {@link JTBOptions#computeGlobalVariablesFromOptions(String, String, File)}.
   *
   * @param aArgs - the command line arguments
   * @return true if successful, false otherwise
   * @throws InvalidCmdLineException - if any problem with command line arguments
   */
  private boolean processCommandLine(final String[] aArgs) throws InvalidCmdLineException {
    boolean success = false;

    for (int i = 0; i < aArgs.length; ++i) {
      String arg = aArgs[i];
      final String uppArg = arg.toUpperCase();
      final String lowArg = arg.toLowerCase();

      if (arg.charAt(0) != '-') {
        if (success) {
          success = false; // 2 filenames passed as arguments?
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
          success = true;
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
            throw new InvalidCmdLineException("Invalid option \"" + arg + "\" (-chm).");
          }
        } else if (uppArg.startsWith("-JTB_CHM=")) {
          if (uppArg.equals("-JTB_CHM=TRUE")) {
            jopt.setCmdLineOption("JTB_CHM", true);
          } else if (uppArg.equals("-JTB_CHM=FALSE")) {
            jopt.setCmdLineOption("JTB_CHM", false);
          } else {
            throw new InvalidCmdLineException("Invalid option \"" + arg + "\" (-JTB_CHM=).");
          }
        }

        else if (lowArg.startsWith("-cl")) {
          if (lowArg.equals("-cl") || lowArg.equals("-cl=true")) {
            jopt.setCmdLineOption("JTB_CL", true);
          } else if (lowArg.equals("-cl=false")) {
            jopt.setCmdLineOption("JTB_CL", false);
          } else {
            throw new InvalidCmdLineException("Invalid option \"" + arg + "\" (-cl).");
          }
        } else if (uppArg.startsWith("-JTB_CL=")) {
          if (uppArg.equals("-JTB_CL=TRUE")) {
            jopt.setCmdLineOption("JTB_CL", true);
          } else if (uppArg.equals("-JTB_CL=FALSE")) {
            jopt.setCmdLineOption("JTB_CL", false);
          } else {
            throw new InvalidCmdLineException("Invalid option \"" + arg + "\" (-JTB_CL=).");
          }
        }

        else if (lowArg.startsWith("-dl")) {
          if (lowArg.equals("-dl") || lowArg.equals("-dl=true")) {
            jopt.setCmdLineOption("JTB_DL", true);
          } else if (lowArg.equals("-dl=false")) {
            jopt.setCmdLineOption("JTB_DL", false);
          } else {
            throw new InvalidCmdLineException("Invalid option \"" + arg + "\" (-dl).");
          }
        } else if (uppArg.startsWith("-JTB_DL=")) {
          if (uppArg.equals("-JTB_DL=TRUE")) {
            jopt.setCmdLineOption("JTB_DL", true);
          } else if (uppArg.equals("-JTB_DL=FALSE")) {
            jopt.setCmdLineOption("JTB_DL", false);
          } else {
            throw new InvalidCmdLineException("Invalid option \"" + arg + "\" (-JTB_DL=).");
          }
        }

        else if (lowArg.startsWith("-do")) {
          if (lowArg.equals("-do") || lowArg.equals("-do=true")) {
            jopt.setCmdLineOption("JTB_DO", true);
          } else if (lowArg.equals("-do=false")) {
            jopt.setCmdLineOption("JTB_DO", false);
          } else {
            throw new InvalidCmdLineException("Invalid option \"" + arg + "\" (-do).");
          }
        } else if (uppArg.startsWith("-JTB_DO=")) {
          if (uppArg.equals("-JTB_DO=TRUE")) {
            jopt.setCmdLineOption("JTB_DO", true);
          } else if (uppArg.equals("-JTB_DO=FALSE")) {
            jopt.setCmdLineOption("JTB_DO", false);
          } else {
            throw new InvalidCmdLineException("Invalid option \"" + arg + "\" (-JTB_DO=).");
          }
        }

        else if (lowArg.startsWith("-d")) { // after -dl & -do
          if (lowArg.equals("-d")) {
            if (++i >= aArgs.length) {
              throw new InvalidCmdLineException("Missing directory name after option \"-d\".");
            }
            arg = removeDoubleQuotes(arg + " " + aArgs[i], aArgs[i]);
            jopt.setCmdLineOption("JTB_D", arg);
          } else if (lowArg.startsWith("-d=")) {
            arg = removeDoubleQuotes(arg, arg.substring(3));
            jopt.setCmdLineOption("JTB_D", arg);
          } else {
            throw new InvalidCmdLineException("Invalid option \"" + arg + "\" (-d).");
          }
        } else if (uppArg.startsWith("-JTB_D=")) {
          arg = removeDoubleQuotes(arg, arg.substring(7));
          jopt.setCmdLineOption("JTB_D", arg);
        }

        else if (lowArg.startsWith("-eg")) {
          if (lowArg.equals("-eg")) {
            if (++i >= aArgs.length) {
              throw new InvalidCmdLineException("Missing class name after option \"-eg\".");
            }
            arg = removeDoubleQuotes(arg + " " + aArgs[i], aArgs[i]);
            jopt.setCmdLineOption("JTB_EG", arg);
          } else if (lowArg.startsWith("-eg=")) {
            arg = removeDoubleQuotes(arg, arg.substring(4));
            jopt.setCmdLineOption("JTB_EG", arg);
          } else {
            throw new InvalidCmdLineException("Invalid option \"" + arg + "\" (-eg).");
          }
        } else if (uppArg.startsWith("-JTB_EG=")) {
          arg = removeDoubleQuotes(arg, arg.substring(8));
          jopt.setCmdLineOption("JTB_EG", arg);
        }

        else if (lowArg.startsWith("-e")) { // after -eg
          if (lowArg.equals("-e") || lowArg.equals("-e=true")) {
            jopt.setCmdLineOption("JTB_E", true);
          } else if (lowArg.equals("-e=false")) {
            jopt.setCmdLineOption("JTB_E", false);
          } else {
            throw new InvalidCmdLineException("Invalid option \"" + arg + "\" (-e).");
          }
        } else if (uppArg.startsWith("-JTB_E=")) {
          if (uppArg.equals("-JTB_E=TRUE")) {
            jopt.setCmdLineOption("JTB_E", true);
          } else if (uppArg.equals("-JTB_E=FALSE")) {
            jopt.setCmdLineOption("JTB_E", false);
          } else {
            throw new InvalidCmdLineException("Invalid option \"" + arg + "\" (-JTB_E=).");
          }
        }

        else if (lowArg.startsWith("-f")) {
          if (lowArg.equals("-f") || lowArg.equals("-f=true")) {
            jopt.setCmdLineOption("JTB_F", true);
          } else if (lowArg.equals("-f=false")) {
            jopt.setCmdLineOption("JTB_F", false);
          } else {
            throw new InvalidCmdLineException("Invalid option \"" + arg + "\" (-f).");
          }
        } else if (uppArg.startsWith("-JTB_F=")) {
          if (uppArg.equals("-JTB_F=TRUE")) {
            jopt.setCmdLineOption("JTB_F", true);
          } else if (uppArg.equals("-JTB_F=FALSE")) {
            jopt.setCmdLineOption("JTB_F", false);
          } else {
            throw new InvalidCmdLineException("Invalid option \"" + arg + "\" (-JTB_F=).");
          }
        }

        else if (lowArg.equals("-h")) {
          printHelp();
        }

        else if (lowArg.startsWith("-hkd")) {
          if (lowArg.equals("-hkd")) {
            if (++i >= aArgs.length) {
              throw new InvalidCmdLineException("Missing directory name after option \"-hkd\".");
            }
            arg = removeDoubleQuotes(arg + " " + aArgs[i], aArgs[i]);
            jopt.setCmdLineOption("JTB_HKD", arg);
          } else if (lowArg.startsWith("-hkd=")) {
            arg = removeDoubleQuotes(arg, arg.substring(5));
            jopt.setCmdLineOption("JTB_HKD", arg);
          } else {
            throw new InvalidCmdLineException("Invalid option \"" + arg + "\" (-hkd).");
          }
        } else if (uppArg.startsWith("-JTB_HKD=")) {
          arg = removeDoubleQuotes(arg, arg.substring(9));
          jopt.setCmdLineOption("JTB_HKD", arg);
        }

        else if (lowArg.startsWith("-hkp")) {
          if (lowArg.equals("-hkp")) {
            if (++i >= aArgs.length) {
              throw new InvalidCmdLineException("Missing package name after option \"-hkp\".");
            }
            arg = removeDoubleQuotes(arg + " " + aArgs[i], aArgs[i]);
            jopt.setCmdLineOption("JTB_HKP", arg);
          } else if (lowArg.startsWith("-hkp=")) {
            arg = removeDoubleQuotes(arg, arg.substring(5));
            jopt.setCmdLineOption("JTB_HKP", arg);
          } else {
            throw new InvalidCmdLineException("Invalid option \"" + arg + "\" (-hkp).");
          }
        } else if (uppArg.startsWith("-JTB_HKP=")) {
          arg = removeDoubleQuotes(arg, arg.substring(9));
          jopt.setCmdLineOption("JTB_HKP", arg);
        }

        else if (lowArg.startsWith("-hk")) { // after -hkd & -hkp
          if (lowArg.equals("-hk") || lowArg.equals("-hk=true")) {
            jopt.setCmdLineOption("JTB_HK", true);
          } else if (lowArg.equals("-hk=false")) {
            jopt.setCmdLineOption("JTB_HK", false);
          } else {
            throw new InvalidCmdLineException("Invalid option \"" + arg + "\" (-hk).");
          }
        } else if (uppArg.startsWith("-JTB_HK=")) {
          if (uppArg.equals("-JTB_HK=TRUE")) {
            jopt.setCmdLineOption("JTB_HK", true);
          } else if (uppArg.equals("-JTB_HK=FALSE")) {
            jopt.setCmdLineOption("JTB_HK", false);
          } else {
            throw new InvalidCmdLineException("Invalid option \"" + arg + "\" (-JTB_HK=).");
          }
        }

        else if (lowArg.startsWith("-ia")) {
          if (lowArg.equals("-ia") || lowArg.equals("-ia=true")) {
            jopt.setCmdLineOption("JTB_IA", true);
          } else if (lowArg.equals("-ia=false")) {
            jopt.setCmdLineOption("JTB_IA", false);
          } else {
            throw new InvalidCmdLineException("Invalid option \"" + arg + "\" (-ia).");
          }
        } else if (uppArg.startsWith("-JTB_IA=")) {
          if (uppArg.equals("-JTB_IA=TRUE")) {
            jopt.setCmdLineOption("JTB_IA", true);
          } else if (uppArg.equals("-JTB_IA=FALSE")) {
            jopt.setCmdLineOption("JTB_IA", false);
          } else {
            throw new InvalidCmdLineException("Invalid option \"" + arg + "\" (-JTB_IA=).");
          }
        }

        else if (lowArg.startsWith("-jd")) {
          if (lowArg.equals("-jd") || lowArg.equals("-jd=true")) {
            jopt.setCmdLineOption("JTB_JD", true);
          } else if (lowArg.equals("-jd=false")) {
            jopt.setCmdLineOption("JTB_JD", false);
          } else {
            throw new InvalidCmdLineException("Invalid option \"" + arg + "\" (-jd).");
          }
        } else if (uppArg.startsWith("-JTB_JD=")) {
          if (uppArg.equals("-JTB_JD=TRUE")) {
            jopt.setCmdLineOption("JTB_JD", true);
          } else if (uppArg.equals("-JTB_JD=FALSE")) {
            jopt.setCmdLineOption("JTB_JD", false);
          } else {
            throw new InvalidCmdLineException("Invalid option \"" + arg + "\" (-JTB_JD=).");
          }
        }

        else if (lowArg.startsWith("-nd")) {
          if (lowArg.equals("-nd")) {
            if (++i >= aArgs.length) {
              throw new InvalidCmdLineException("Missing directory name after option \"-nd\".");
            }
            arg = removeDoubleQuotes(arg + " " + aArgs[i], aArgs[i]);
            jopt.setCmdLineOption("JTB_ND", arg);
          } else if (lowArg.startsWith("-nd=")) {
            arg = removeDoubleQuotes(arg, arg.substring(4));
            jopt.setCmdLineOption("JTB_ND", arg);
          } else {
            throw new InvalidCmdLineException("Invalid option \"" + arg + "\" (-JTB_ND=).");
          }
        } else if (uppArg.startsWith("-JTB_ND=")) {
          arg = removeDoubleQuotes(arg, arg.substring(8));
          jopt.setCmdLineOption("JTB_ND", arg);
        }

        else if (lowArg.startsWith("-noplg")) {
          if (lowArg.equals("-noplg") || lowArg.equals("-noplg=true")) {
            jopt.setCmdLineOption("JTB_NOPLG", true);
          } else if (lowArg.equals("-noplg=false")) {
            jopt.setCmdLineOption("JTB_NOPLG", false);
          } else {
            throw new InvalidCmdLineException("Invalid option \"" + arg + "\" (-noplg).");
          }
        } else if (uppArg.startsWith("-JTB_NOPLG=")) {
          if (uppArg.equals("-JTB_NOPLG=TRUE")) {
            jopt.setCmdLineOption("JTB_NOPLG", true);
          } else if (uppArg.equals("-JTB_NOPLG=FALSE")) {
            jopt.setCmdLineOption("JTB_NOPLG", false);
          } else {
            throw new InvalidCmdLineException("Invalid option \"" + arg + "\" (-JTB_NOPLG=).");
          }
        }

        else if (lowArg.startsWith("-nosig")) {
          if (lowArg.equals("-nosig") || lowArg.equals("-nosig=true")) {
            jopt.setCmdLineOption("JTB_NOSIG", true);
          } else if (lowArg.equals("-nosig=false")) {
            jopt.setCmdLineOption("JTB_NOSIG", false);
          } else {
            throw new InvalidCmdLineException("Invalid option \"" + arg + "\" (-nosig).");
          }
        } else if (uppArg.startsWith("-JTB_NOSIG=")) {
          if (uppArg.equals("-JTB_NOSIG=TRUE")) {
            jopt.setCmdLineOption("JTB_NOSIG", true);
          } else if (uppArg.equals("-JTB_NOSIG=FALSE")) {
            jopt.setCmdLineOption("JTB_NOSIG", false);
          } else {
            throw new InvalidCmdLineException("Invalid option \"" + arg + "\" (-nosig).");
          }
        }

        else if (lowArg.startsWith("-novis")) {
          if (lowArg.equals("-novis") || lowArg.equals("-novis=true")) {
            jopt.setCmdLineOption("JTB_NOVIS", true);
          } else if (lowArg.equals("-novis=false")) {
            jopt.setCmdLineOption("JTB_NOVIS", false);
          } else {
            throw new InvalidCmdLineException("Invalid option \"" + arg + "\" (-nosig).");
          }
        } else if (uppArg.startsWith("-JTB_NOVIS=")) {
          if (uppArg.equals("-JTB_NOVIS=TRUE")) {
            jopt.setCmdLineOption("JTB_NOVIS", true);
          } else if (uppArg.equals("-JTB_NOVIS=FALSE")) {
            jopt.setCmdLineOption("JTB_NOVIS", false);
          } else {
            throw new InvalidCmdLineException("Invalid option \"" + arg + "\" (-JTB_NOVIS=).");
          }
        }

        else if (lowArg.startsWith("-npfx")) {
          if (lowArg.equals("-npfx")) {
            if (++i >= aArgs.length) {
              throw new InvalidCmdLineException("Missing prefix after option \"-npfx\".");
            }
            arg = removeDoubleQuotes(arg + " " + aArgs[i], aArgs[i]);
            jopt.setCmdLineOption("JTB_NPFX", arg);
          } else if (lowArg.startsWith("-npfx=")) {
            arg = removeDoubleQuotes(arg, arg.substring(6));
            jopt.setCmdLineOption("JTB_NPFX", arg);
          } else {
            throw new InvalidCmdLineException("Invalid option \"" + arg + "\" (-npfx).");
          }
        } else if (uppArg.startsWith("-JTB_NPFX=")) {
          arg = removeDoubleQuotes(arg, arg.substring(10));
          jopt.setCmdLineOption("JTB_NPFX", arg);
        }

        else if (lowArg.startsWith("-np")) { // after -npfx
          if (lowArg.equals("-np")) {
            if (++i >= aArgs.length) {
              throw new InvalidCmdLineException("Missing package name after option \"-np\".");
            }
            arg = removeDoubleQuotes(arg + " " + aArgs[i], aArgs[i]);
            jopt.setCmdLineOption("JTB_NP", arg);
          } else if (lowArg.startsWith("-np=")) {
            arg = removeDoubleQuotes(arg, arg.substring(4));
            jopt.setCmdLineOption("JTB_NP", arg);
          } else {
            throw new InvalidCmdLineException("Invalid option \"" + arg + "\" (-np).");
          }
        } else if (uppArg.startsWith("-JTB_NP=")) {
          arg = removeDoubleQuotes(arg, arg.substring(8));
          jopt.setCmdLineOption("JTB_NP", arg);
        }

        else if (lowArg.startsWith("-nsfx")) {
          if (lowArg.equals("-nsfx")) {
            if (++i >= aArgs.length) {
              throw new InvalidCmdLineException("Missing suffix after option \"-nsfx\".");
            }
            arg = removeDoubleQuotes(arg + " " + aArgs[i], aArgs[i]);
            jopt.setCmdLineOption("JTB_NSFX", arg);
          } else if (lowArg.startsWith("-nsfx=")) {
            arg = removeDoubleQuotes(arg, arg.substring(6));
            jopt.setCmdLineOption("JTB_NSFX", arg);
          } else {
            throw new InvalidCmdLineException("Invalid option \"" + arg + "\" (-nsfx).");
          }
        } else if (uppArg.startsWith("-JTB_NSFX=")) {
          arg = removeDoubleQuotes(arg, arg.substring(10));
          jopt.setCmdLineOption("JTB_NSFX", arg);
        }

        else if (lowArg.startsWith("-ns")) { // after -nsfx
          if (lowArg.equals("-ns")) {
            if (++i >= aArgs.length) {
              throw new InvalidCmdLineException("Missing class name after option \"-ns\".");
            }
            arg = removeDoubleQuotes(arg + " " + aArgs[i], aArgs[i]);
            jopt.setCmdLineOption("JTB_NS", arg);
          } else if (lowArg.startsWith("-ns=")) {
            arg = removeDoubleQuotes(arg, arg.substring(4));
            jopt.setCmdLineOption("JTB_NS", arg);
          } else {
            throw new InvalidCmdLineException("Invalid option \"" + arg + "\" (-ns).");
          }
        } else if (uppArg.startsWith("-JTB_NS=")) {
          arg = removeDoubleQuotes(arg, arg.substring(8));
          jopt.setCmdLineOption("JTB_NS", arg);
        }

        else if (uppArg.startsWith("-OUTPUT_DIRECTORY")) {
          final String od = arg.substring("-OUTPUT_DIRECTORY".length() + 1);
          jopt.jjOutputDirName = removeDoubleQuotes(arg, od);
        }

        else if (lowArg.startsWith("-o")) {
          if (lowArg.equals("-o")) {
            if (++i >= aArgs.length) {
              throw new InvalidCmdLineException("Missing file name after option \"-o\".");
            }
            arg = removeDoubleQuotes(arg + " " + aArgs[i], aArgs[i]);
            jopt.setCmdLineOption("JTB_O", arg);
          } else if (lowArg.startsWith("-o=")) {
            arg = removeDoubleQuotes(arg, arg.substring(3));
            jopt.setCmdLineOption("JTB_O", arg);
          } else {
            throw new InvalidCmdLineException("Invalid option \"" + arg + "\" (-o).");
          }
        } else if (uppArg.startsWith("-JTB_O=")) {
          arg = removeDoubleQuotes(arg, arg.substring(7));
          jopt.setCmdLineOption("JTB_O", arg);
        }

        else if (lowArg.startsWith("-pp")) {
          if (lowArg.equals("-pp") || lowArg.equals("-pp=true")) {
            jopt.setCmdLineOption("JTB_PP", true);
          } else if (lowArg.equals("-pp=false")) {
            jopt.setCmdLineOption("JTB_PP", false);
          } else {
            throw new InvalidCmdLineException("Invalid option \"" + arg + "\" (-pp).");
          }
        } else if (uppArg.startsWith("-JTB_PP=")) {
          if (uppArg.equals("-JTB_PP=TRUE")) {
            jopt.setCmdLineOption("JTB_PP", true);
          } else if (uppArg.equals("-JTB_PP=FALSE")) {
            jopt.setCmdLineOption("JTB_PP", false);
          } else {
            throw new InvalidCmdLineException("Invalid option \"" + arg + "\" (-JTB_PP=).");
          }
        }

        else if (lowArg.startsWith("-printer")) {
          if (lowArg.equals("-printer") || lowArg.equals("-printer=true")) {
            jopt.setCmdLineOption("JTB_PRINTER", true);
          } else if (lowArg.equals("-printer=false")) {
            jopt.setCmdLineOption("JTB_PRINTER", false);
          } else {
            throw new InvalidCmdLineException("Invalid option \"" + arg + "\" (-printer).");
          }
        } else if (uppArg.startsWith("-JTB_PRINTER=")) {
          if (uppArg.equals("-JTB_PRINTER=TRUE")) {
            jopt.setCmdLineOption("JTB_PRINTER", true);
          } else if (uppArg.equals("-JTB_PRINTER=FALSE")) {
            jopt.setCmdLineOption("JTB_PRINTER", false);
          } else {
            throw new InvalidCmdLineException("Invalid option \"" + arg + "\" (-JTB_PRINTER=).");
          }
        }

        else if (lowArg.startsWith("-p")) { // after -pp & -printer
          if (lowArg.equals("-p")) {
            if (++i >= aArgs.length) {
              throw new InvalidCmdLineException("Missing package name after option \"-p\".");
            }
            arg = removeDoubleQuotes(arg + " " + aArgs[i], aArgs[i]);
            jopt.setCmdLineOption("JTB_P", arg);
          } else if (lowArg.startsWith("-p=")) {
            arg = removeDoubleQuotes(arg, arg.substring(3));
            jopt.setCmdLineOption("JTB_P", arg);
          } else {
            throw new InvalidCmdLineException("Invalid option \"" + arg + "\" (-p).");
          }
        } else if (uppArg.startsWith("-JTB_P=")) {
          arg = removeDoubleQuotes(arg, arg.substring(7));
          jopt.setCmdLineOption("JTB_P", arg);
        }

        else if (lowArg.equals("-si")) {
          in = System.in;
          jtbInputFileName = "standard input";
        }

        else if (lowArg.startsWith("-sigd")) {
          if (lowArg.equals("-sigd")) {
            if (++i >= aArgs.length) {
              throw new InvalidCmdLineException("Missing directory name after option \"-sigd\".");
            }
            arg = removeDoubleQuotes(arg + " " + aArgs[i], aArgs[i]);
            jopt.setCmdLineOption("JTB_SIGD", arg);
          } else if (lowArg.startsWith("-sigd=")) {
            arg = removeDoubleQuotes(arg, arg.substring(6));
            jopt.setCmdLineOption("JTB_SIGD", arg);
          } else {
            throw new InvalidCmdLineException("Invalid option \"" + arg + "\" (-sigd).");
          }
        } else if (uppArg.startsWith("-JTB_SIGD=")) {
          arg = removeDoubleQuotes(arg, arg.substring(10));
          jopt.setCmdLineOption("JTB_SIGD", arg);
        }

        else if (lowArg.startsWith("-sigp")) {
          if (lowArg.equals("-sigp")) {
            if (++i >= aArgs.length) {
              throw new InvalidCmdLineException("Missing package name after option \"-sigp\".");
            }
            arg = removeDoubleQuotes(arg + " " + aArgs[i], aArgs[i]);
            jopt.setCmdLineOption("JTB_SIGP", arg);
          } else if (lowArg.startsWith("-sigp=")) {
            arg = removeDoubleQuotes(arg, arg.substring(6));
            jopt.setCmdLineOption("JTB_SIGP", arg);
          } else {
            throw new InvalidCmdLineException("Invalid option \"" + arg + "\" (-sigp).");
          }
        } else if (uppArg.startsWith("-JTB_SIGP=")) {
          arg = removeDoubleQuotes(arg, arg.substring(10));
          jopt.setCmdLineOption("JTB_SIGP", arg);
        }

        else if (lowArg.startsWith("-tkjj")) {
          if (lowArg.equals("-tkjj") || lowArg.equals("-tkjj=true")) {
            jopt.setCmdLineOption("JTB_TKJJ", true);
          } else if (lowArg.equals("-tkjj=false")) {
            jopt.setCmdLineOption("JTB_TKJJ", false);
          } else {
            throw new InvalidCmdLineException("Invalid option \"" + arg + "\" (-tkjj).");
          }
        } else if (uppArg.startsWith("-JTB_TKJJ=")) {
          if (uppArg.equals("-JTB_TKJJ=TRUE")) {
            jopt.setCmdLineOption("JTB_TKJJ", true);
          } else if (uppArg.equals("-JTB_TKJJ=FALSE")) {
            jopt.setCmdLineOption("JTB_TKJJ", false);
          } else {
            throw new InvalidCmdLineException("Invalid option \"" + arg + "\" (-JTB_TKJJ=).");
          }
        }

        else if (lowArg.startsWith("-tk")) { // after -tkjj
          if (lowArg.equals("-tk") || lowArg.equals("-tk=true")) {
            jopt.setCmdLineOption("JTB_TK", true);
          } else if (lowArg.equals("-tk=false")) {
            jopt.setCmdLineOption("JTB_TK", false);
          } else {
            throw new InvalidCmdLineException("Invalid option \"" + arg + "\" (-tk).");
          }
        } else if (uppArg.startsWith("-JTB_TK=")) {
          if (uppArg.equals("-JTB_TK=TRUE")) {
            jopt.setCmdLineOption("JTB_TK", true);
          } else if (uppArg.equals("-JTB_TK=FALSE")) {
            jopt.setCmdLineOption("JTB_TK", false);
          } else {
            throw new InvalidCmdLineException("Invalid option \"" + arg + "\" (-JTB_TK=).");
          }
        }

        else if (lowArg.startsWith("-vd")) {
          if (lowArg.equals("-vd")) {
            if (++i >= aArgs.length) {
              throw new InvalidCmdLineException("Missing directory name after option \"-vd\".");
            }
            arg = removeDoubleQuotes(arg + " " + aArgs[i], aArgs[i]);
            jopt.setCmdLineOption("JTB_VD", arg);
          } else if (lowArg.startsWith("-vd=")) {
            arg = removeDoubleQuotes(arg, arg.substring(4));
            jopt.setCmdLineOption("JTB_VD", arg);
          } else {
            throw new InvalidCmdLineException("Invalid option \"" + arg + "\" (-vd).");
          }
        } else if (uppArg.startsWith("-JTB_VD=")) {
          arg = removeDoubleQuotes(arg, arg.substring(8));
          jopt.setCmdLineOption("JTB_VD", arg);
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
            throw new InvalidCmdLineException("Invalid option \"" + arg + "\" (-vis=).");
          }
        } else if (uppArg.startsWith("-JTB_VIS=")) {
          final String str = arg.substring(9);
          if (!jopt.createVisitorsList(str)) {
            throw new InvalidCmdLineException("Invalid visitors specification \"" + arg + "\".");
          }
          jopt.setCmdLineOption("JTB_VIS", str);
        }

        else if (lowArg.startsWith("-vp")) {
          if (lowArg.equals("-vp")) {
            if (++i >= aArgs.length) {
              throw new InvalidCmdLineException("Missing package name after option \"-vp\".");
            }
            arg = removeDoubleQuotes(arg + " " + aArgs[i], aArgs[i]);
            jopt.setCmdLineOption("JTB_VP", arg);
          } else if (lowArg.startsWith("-vp=")) {
            arg = removeDoubleQuotes(arg, arg.substring(4));
            jopt.setCmdLineOption("JTB_VP", arg);
          } else {
            throw new InvalidCmdLineException("Invalid option \"" + arg + "\" (-vp).");
          }
        } else if (uppArg.startsWith("-JTB_VP=")) {
          arg = removeDoubleQuotes(arg, arg.substring(4));
          jopt.setCmdLineOption("JTB_VP", arg);
        }

        else if (lowArg.startsWith("-w")) {
          if (lowArg.equals("-w") || lowArg.equals("-w=true")) {
            jopt.setCmdLineOption("JTB_W", true);
          } else if (lowArg.equals("-w=false")) {
            jopt.setCmdLineOption("JTB_W", false);
          } else {
            throw new InvalidCmdLineException("Invalid option \"" + arg + "\" (-w).");
          }
        } else if (uppArg.startsWith("-JTB_W=")) {
          if (uppArg.equals("-JTB_W=TRUE")) {
            jopt.setCmdLineOption("JTB_W", true);
          } else if (uppArg.equals("-JTB_W=FALSE")) {
            jopt.setCmdLineOption("JTB_W", false);
          } else {
            throw new InvalidCmdLineException("Invalid option \"" + arg + "\" (-JTB_W=).");
          }
        }

      } // end else -> arg.charAt(0) == '-'
    } // end for

    if (success) {
      return true;
    } else {
      printHelp();
      return false;
    }
  }

  /**
   * Removes double quotes surrounding a string if they exist.
   *
   * @param ctx - the context
   * @param str - the string surrounded or not by double quotes
   * @return the string with the double quotes removed
   * @throws InvalidCmdLineException - if incorrect surrounding quotes
   */
  private String removeDoubleQuotes(final String ctx, final String str) throws InvalidCmdLineException {
    if (str.length() < 2) {
      return str;
    }
    if (str.charAt(0) == '"') {
      if (str.charAt(str.length() - 1) == '"') {
        return str.substring(1, str.length() - 1);
      } else {
        throw new InvalidCmdLineException(
            "argument starting but not ending with a double quote (" + str + ") in '" + ctx + "'");
      }
    }
    if (str.charAt(str.length() - 1) == '"') {
      throw new InvalidCmdLineException(
          "argument ending but not starting with a double quote (" + str + ") in '" + ctx + "'");
    }
    return str;
  }

  /**
   * Convert the different input & output directories and the jj output file which are relative to the grammar
   * directory or the output directory to normalized absolute paths.
   */
  private void convertPathsToAbsolute() {
    String dir = inDir + File.separator;
    if (jopt.baseDirName != null) {
      File gdn = new File(jopt.baseDirName);
      if (!gdn.isAbsolute()) {
        gdn = new File(dir + jopt.baseDirName);
        jopt.baseDirName = gdn.toPath().normalize().toString();
      }
    }
    dir = jopt.jjOutputDirName + File.separator;
    if (jopt.nodesDirName != null) {
      File ndn = new File(jopt.nodesDirName);
      if (!ndn.isAbsolute()) {
        ndn = new File(dir + jopt.nodesDirName);
        jopt.nodesDirName = ndn.toPath().normalize().toString();
      }
    }
    if (!jopt.noVisitors && jopt.visitorsDirName != null) {
      File vdn = new File(jopt.visitorsDirName);
      if (!vdn.isAbsolute()) {
        vdn = new File(dir + jopt.visitorsDirName);
        jopt.visitorsDirName = vdn.toPath().normalize().toString();
      }
    }
    if (!jopt.noSignature && jopt.signatureDirName != null) {
      File sdn = new File(jopt.signatureDirName);
      if (!sdn.isAbsolute()) {
        sdn = new File(dir + jopt.signatureDirName);
        jopt.signatureDirName = sdn.toPath().normalize().toString();
      }
    }
    if (jopt.hook && jopt.hookDirName != null) {
      File hdn = new File(jopt.hookDirName);
      if (!hdn.isAbsolute()) {
        hdn = new File(dir + jopt.hookDirName);
        jopt.hookDirName = hdn.toPath().normalize().toString();
      }
    }
    File jjf = new File(jopt.jtbOutputFileName);
    if (!jjf.isAbsolute()) {
      jjf = new File(
          (jopt.baseDirName != null ? jopt.baseDirName + File.separator : dir) + jopt.jtbOutputFileName);
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

    // rc = bg.genTokenFile(jopt.jjOutputDirName);
    // printlnInfo(rc + " Token class file " + "generated into directory \"" + jopt.jjOutputDirName + "\".");
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
   * Displays program usage. TODO manage -va
   */
  private static void printHelp() {
    System.out.print("Usage: " + PROG_NAME + " [OPTIONS] " + "[inputfile]\n" //
        + "\n" //
        + "* Standard options:\n"//
        + "  -chm           Generate nodes children methods - since 1.5.0\n"
        + "  -cl            Print a list of the classes generated to standard out\n"
        + "  -d b_dir       Prepend b_dir (with a /) to h_dir, n_dir, s_dir & v_dir\n"
        + "  -dl            Generate depth level info\n"
        + "  -do            Print a list of resulting (file and command line) options to standard out - since 1.5.0\n"
        + "  -e             Suppress JTB semantic error checking\n"
        + "  -eg e_class    Calls e_class (to be found in the classpath) to run a user supplied generator - since 1.5.0\n"
        + "  -f             Use descriptive node class field names\n"
        + "  -h             Display this help message and quit\n"
        + "  -hk            Generate node scope hook interface and implementation and method calls - since 1.5.0\n"
        + "  -hkd h_dir     Use h_dir as the directory for the node scope hook interface and class - since 1.5.0\n"
        + "  -hkp h_pkg     Use h_pkg as the package for the node scope hook interface and class - since 1.5.0\n"
        + "  -ia            Inline visitors accept methods on base classes\n"
        + "  -jd            Generate JavaDoc-friendly comments in the nodes and visitors\n"
        + "  -nd n_dir      Use n_dir as the directory for the syntax tree nodes\n"
        + "  -noplg         Do not parallelize user nodes generation - since 1.5.0\n"
        + "  -nosig         Do not generate signature annotations in visitors - since 1.5.0\n"
        + "  -novis         Do not generate visitors interfaces and classes - since 1.5.0\n"
        + "  -np n_pkg      Use pkg as the package for the syntax tree nodes\n"
        + "  -npfx p_str    Use p_str as prefix for the syntax tree nodes\n"
        + "  -ns n_class    Use n_class as the class which all node classes will extend\n"
        + "  -nsfx s_str    Use s_str as suffix for the syntax tree nodes\n"
        + "  -o o_file      Use o_file as the filename for the annotated output grammar\n"
        + "  -p b_pkg       Prepend b_pkg (with a .) to h_pkg, n_pkg, s_pkg & v_pkg\n"
        + "  -pp            Generate parent pointers in all node classes\n"
        + "  -printer       Generate a syntax tree dumping visitor\n"
        + "  -si            Read from standard input rather than a file\n"
        + "  -sigd s_dir    Use s_dir as the directory for the signature interface and class - since 1.5.3\n"
        + "  -sigp s_pkg    Use s_pkg as the package for the signature interface and class - since 1.5.3\n"
        + "  -tk            Generate special tokens in the tree's NodeTokens\n"
        + "  -tkjj          Generate special tokens in the annotated grammar (implies -tk) - since 1.5.0\n"
        + "  -va            Generate visitors with an argument of a vararg type\n"
        + "  -vd v_dir      Use v_dir as the package for the default visitor classes\n"
        + "  -vis v_str     Generate visitors with specified suffix, return type and argument(s) type(s) - since 1.5.0\n"
        + "                 v_str is: sfx,ret,args(;sfx,ret,args)* (ret can be 'void', args can be 'none')\n"
        + "  -vp v_pkg      Use v_pkg as the package for the default visitor classes\n"
        + "  -w             Do not overwrite existing files\n" + "\n" //
        + "\n" //
        + "* All pairs (like -d d_dir) can be written with a '=' instead of a ' ' (-d=d_dir);" //
        + "   the second arguments are strings that must be enclosed within double quotes\n" //
        + "* All single argument options (like -e) are booleans set to true (-e is short for -e=true);" //
        + "   they can be set to false - their default value - (-e=false)\n" //
        + "* All options can be written in uppercase prefixed by JTB_ (-pp -> -JTB_PP);" //
        + "\n" //
    );
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

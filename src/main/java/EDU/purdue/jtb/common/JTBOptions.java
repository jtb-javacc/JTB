package EDU.purdue.jtb.common;

import static EDU.purdue.jtb.common.Constants.DEF_HOOK_DIR_NAME;
import static EDU.purdue.jtb.common.Constants.DEF_HOOK_PKG_NAME;
import static EDU.purdue.jtb.common.Constants.DEF_ND_DIR_NAME;
import static EDU.purdue.jtb.common.Constants.DEF_ND_PKG_NAME;
import static EDU.purdue.jtb.common.Constants.DEF_ND_PREFIX;
import static EDU.purdue.jtb.common.Constants.DEF_ND_SUFFIX;
import static EDU.purdue.jtb.common.Constants.DEF_OUT_FILE_NAME;
import static EDU.purdue.jtb.common.Constants.DEF_SIG_DIR_NAME;
import static EDU.purdue.jtb.common.Constants.DEF_SIG_PKG_NAME;
import static EDU.purdue.jtb.common.Constants.DEF_VIS_DIR_NAME;
import static EDU.purdue.jtb.common.Constants.DEF_VIS_PKG_NAME;
import static EDU.purdue.jtb.common.Constants.DEF_VIS_SPEC;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import EDU.purdue.jtb.parser.Options;
import EDU.purdue.jtb.parser.Token;

/**
 * A subclass of the JavaCC options class to extend it to the JTB options.<br>
 * It also handles the global variables reflecting the JTB options and some derived objects and methods.
 * <p>
 * Class is not supposed to be run in parallel threads (on the same grammar).
 * </p>
 * TESTCASE some to add
 *
 * @author Marc Mazas
 * @version 1.5.0 : 03-06/2017 : MMa : created when refactoring JTB, Globals and Options together ; added
 *          language
 * @version 1.5.1 : 07/2023 : MMa : fixed issue with imports / packages on grammars with no package<br>
 *          1.5.1 : 08/2023 : MMa : editing changes for coverage analysis; changes due to the NodeToken
 *          replacement by Token
 * @version 1.5.3 : 10/2025 : MMa : fixed some command line arguments handling; NodeConstants replaced by
 *          &lt;Parser&gt;NodeConstants
 */
public class JTBOptions extends Options {

  /** The messages handler */
  public final Messages mess;

  /** JavaCC option for Java language */
  static final String JJOPT_LANG_JAVA = "java";
  /** JavaCC option for C++ language */
  static final String JJOPT_LANG_CPP  = "c++";
  /** File extension for Java language */
  static final String EXT_LANG_JAVA   = ".java";
  /** File extension for C++ language */
  static final String EXT_LANG_CPP    = ".cpp";

  /*
   * JTB (boolean) flags (alphabetical order of options names)
   */

  /**
   * -chm option which generates children handling methods in base and user nodes
   */
  public boolean childrenMethods       = false;
  /**
   * -cl option which prints the generated classes list to System.out
   */
  public boolean printClassList        = false;
  /**
   * -dl option which generates depthLevel field in all visitor classes
   */
  public boolean depthLevel            = false;
  /**
   * -do option which dumps the options to System.out
   */
  public boolean dumpOptions           = false;
  /**
   * -e option which suppresses JTB semantic error checking
   */
  public boolean noSemanticCheck       = false;
  /**
   * -f option which generates descriptive node class child field names such as whileStatement, nodeToken2,
   * ... rather than f0, f1, ...
   */
  public boolean descriptiveFieldNames = false;
  /**
   * -hk option which generates enter and exit node scope hook methods in the grammar
   */
  public boolean hook                  = false;
  /**
   * -ia option which "inlines" the visitors accept methods on base classes
   */
  public boolean inlineAcceptMethods   = false;
  /**
   * -jd option which generates JavaDoc-friendly comments in generated visitors and syntax tree classes
   */
  public boolean javaDocComments       = false;
  /**
   * -noplg option which suppresses parallel generation of user files
   */
  public boolean noParallel            = false;
  /**
   * -nosig option which suppresses generating signature control in visitors
   */
  public boolean noSignature           = false;
  /**
   * -novis option which suppresses generating visitors
   */
  public boolean noVisitors            = false;
  /**
   * -pp option which generates parent pointer and getParent() and setParent() methods in all node classes
   */
  public boolean parentPointer         = false;
  /**
   * -printer option which generates TreeDumper and TreeFormatter visitors
   */
  public boolean printerToolkit        = false;
  /**
   * -tk option which stores special tokens in the tree's NodeTokens
   */
  public boolean storeSpecialTokens    = false;
  /**
   * -tkjj option which prints special tokens in the annotated JJ file
   */
  public boolean printSpecialTokensJJ  = false;
  /**
   * -w options which prevents JTB from overwriting existing files
   */
  public boolean noOverwrite           = false;

  /*
   * JTB (string) names and derived members (alphabetical order of options names)
   */

  /**
   * -d option which defines the base directory for the nodes, visitors, signature & hook directories,<br>
   * or if not set or is blank the grammar file parent directory
   */
  public String            baseDirName            = null;
  /**
   * -eg option which defines an external generator class
   */
  public String            externalGeneratorClass = null;
  /**
   * -hkd option which defines the node scope hook directory name (default is {@link #DEF_HOOK_DIR_NAME})
   */
  public String            hookDirName            = DEF_HOOK_DIR_NAME;
  /**
   * -hkp option which defines the node scope hook package name (default is {@link #DEF_HOOK_PKG_NAME})
   */
  public String            hookPkgName            = DEF_HOOK_PKG_NAME;
  /**
   * -nd option which defines the nodes directory name (default is {@link #DEF_ND_DIR_NAME})
   */
  public String            nodesDirName           = DEF_ND_DIR_NAME;
  /**
   * -np option which defines the nodes package name (default is {@link #DEF_ND_PKG_NAME})
   */
  public String            nodesPkgName           = DEF_ND_PKG_NAME;
  /**
   * -npfx option which defines the nodes prefix (default is {@link #DEF_ND_PREFIX})
   */
  public String            nodePrefix             = DEF_ND_PREFIX;
  /**
   * -nsfx option which defines the nodes suffix (default is {@link #DEF_ND_SUFFIX})
   */
  public String            nodeSuffix             = DEF_ND_SUFFIX;
  /**
   * The derived flag to tell if there is at least one prefix or one suffix
   */
  public boolean           isPfxOrSfx;
  /**
   * -ns option which defines the nodes superclass
   */
  public String            nodesSuperclass        = null;
  /**
   * -o option which defines the output (generated) file name (default is {@link #DEF_OUT_FILE_NAME})
   */
  public String            jtbOutputFileName      = DEF_OUT_FILE_NAME;
  /**
   * -p option which gives the base package for the nodes, visitors, signature & hook packages
   */
  public String            basePkgName            = null;
  /**
   * -sigd option which gives the signature directory name (default is
   * {@link #visitorsDirName}/{@link #DEF_SIG_DIR_NAME})
   */
  public String            signatureDirName       = DEF_VIS_DIR_NAME + "/" + DEF_SIG_DIR_NAME;
  /**
   * -sigp option which gives the signature package name (default is
   * {@link #visitorsPkgName}.{@link #DEF_SIG_PKG_NAME})
   */
  public String            signaturePkgName       = DEF_VIS_PKG_NAME + "." + DEF_SIG_PKG_NAME;
  /**
   * -vd option which defines the visitors directory name (default is {@link #DEF_VIS_DIR_NAME})
   */
  public String            visitorsDirName        = DEF_VIS_DIR_NAME;
  /**
   * -vis option which defines the visitors to be generated
   */
  public String            visitorsStr            = null;
  /**
   * The list of the visitors to be generated, deriving from the -vis option
   */
  public List<VisitorInfo> visitorsList           = null;
  /**
   * -vp option which defines the visitors package name (default is {@link #DEF_VIS_PKG_NAME})
   */
  public String            visitorsPkgName        = DEF_VIS_PKG_NAME;
  /**
   * The parser name, from the grammar file
   */
  public String            parserName             = null;

  /*
   * JavaCC options
   */

  /**
   * The JavaCC output directory option (OUTPUT_DIRECTORY) name (the directory where to generate
   * {@link Token})
   */
  public String  jjOutputDirName;
  /**
   * The JavaCC static or not static option
   */
  public boolean jjIsStatic = false;
  /**
   * The JavaCC language option (default is {@link Lang#JAVA})
   */
  public Lang    jjLang     = Lang.JAVA;

  /*
   * Miscellaneous
   */

  /** Helper flag for java output language */
  public boolean isJava;

  /** Helper flag for C++ output language */
  public boolean isCpp;

  /** File extension */
  public String fileExt;

  /**
   * Standard constructor.
   *
   * @param aMess - the messages handler
   */
  public JTBOptions(final Messages aMess) {
    super();
    mess = aMess;
  }

  /**
   * Initialize the JavaCC & JTB options.
   */
  @Override
  public void init() {
    // JavaCC
    super.init();
    // JTB Options (with default values)
    // -h & -si are not managed in an input file

    optionValues.put("JTB_CHM", Boolean.FALSE);
    optionValues.put("JTB_CL", Boolean.FALSE);
    optionValues.put("JTB_DL", Boolean.FALSE);
    optionValues.put("JTB_DO", Boolean.FALSE);
    optionValues.put("JTB_E", Boolean.FALSE);
    optionValues.put("JTB_F", Boolean.FALSE);
    optionValues.put("JTB_HK", Boolean.FALSE);
    optionValues.put("JTB_IA", Boolean.FALSE);
    optionValues.put("JTB_JD", Boolean.FALSE);
    optionValues.put("JTB_NOPLG", Boolean.FALSE);
    optionValues.put("JTB_NOSIG", Boolean.FALSE);
    optionValues.put("JTB_NOVIS", Boolean.FALSE);
    optionValues.put("JTB_PP", Boolean.FALSE);
    optionValues.put("JTB_PRINTER", Boolean.FALSE);
    optionValues.put("JTB_TK", Boolean.FALSE);
    optionValues.put("JTB_TKJJ", Boolean.FALSE);
    optionValues.put("JTB_VA", Boolean.FALSE);
    optionValues.put("JTB_W", Boolean.FALSE);

    optionValues.put("JTB_D", null);
    optionValues.put("JTB_EG", null);
    optionValues.put("JTB_HKD", DEF_HOOK_DIR_NAME);
    optionValues.put("JTB_HKP", DEF_HOOK_PKG_NAME);
    optionValues.put("JTB_ND", DEF_ND_DIR_NAME);
    optionValues.put("JTB_NP", DEF_ND_PKG_NAME);
    optionValues.put("JTB_NPFX", DEF_ND_PREFIX);
    optionValues.put("JTB_NSFX", DEF_ND_SUFFIX);
    optionValues.put("JTB_NS", null);
    optionValues.put("JTB_O", DEF_OUT_FILE_NAME);
    optionValues.put("JTB_P", null);
    optionValues.put("JTB_SIGD", DEF_SIG_DIR_NAME);
    optionValues.put("JTB_SIGP", DEF_SIG_PKG_NAME);
    optionValues.put("JTB_VD", DEF_VIS_DIR_NAME);
    optionValues.put("JTB_VIS", null);
    optionValues.put("JTB_VP", DEF_VIS_PKG_NAME);
  }

  /**
   * Stores a boolean option coming from a command line argument.
   *
   * @param aKey - the command line option name
   * @param aVal - the command line option value
   */
  public void setCmdLineOption(final String aKey, final boolean aVal) {
    optionValues.put(aKey, Boolean.valueOf(aVal));
    cmdLineSetting.add(aKey);
  }

  /**
   * Stores a String option deriving from another option.
   *
   * @param aKey - the command line option name
   * @param aVal - the command line option value
   */
  public void setDerivedOption(final String aKey, final String aVal) {
    optionValues.put(aKey, aVal);
  }

  /**
   * Stores a String option coming from a command line argument. Enclosing quotes are removed.
   *
   * @param aKey - the command line option name
   * @param aVal - the command line option value
   */
  public void setCmdLineOption(final String aKey, final String aVal) {
    optionValues.put(aKey, aVal);
    cmdLineSetting.add(aKey);
  }

  /**
   * Loads the global variables with the JTB global options from the parsed grammar (which was fed with the
   * command line options and the grammar file options); performs cross controls and cross settings.
   *
   * @param aGrammarPackage - the grammar package name (from the grammar)
   * @param aInDir - the grammar file parent directory
   * @param aJJOutputDirectory - the JavaCC output directory option
   */
  public void computeGlobalVariablesFromOptions(final String aGrammarPackage, final String aInDir,
      final File aJJOutputDirectory) {
    String str = null;
    final Map<String, Object> optMap = getOptions();

    /* JTB boolean options */

    childrenMethods = ((Boolean) optMap.get("JTB_CHM")).booleanValue();

    printClassList = ((Boolean) optMap.get("JTB_CL")).booleanValue();

    depthLevel = ((Boolean) optMap.get("JTB_DL")).booleanValue();

    dumpOptions = ((Boolean) optMap.get("JTB_DO")).booleanValue();

    noSemanticCheck = ((Boolean) optMap.get("JTB_E")).booleanValue();

    descriptiveFieldNames = ((Boolean) optMap.get("JTB_F")).booleanValue();

    hook = ((Boolean) optMap.get("JTB_HK")).booleanValue();

    inlineAcceptMethods = ((Boolean) optMap.get("JTB_IA")).booleanValue();

    javaDocComments = ((Boolean) optMap.get("JTB_JD")).booleanValue();

    noParallel = ((Boolean) optMap.get("JTB_NOPLG")).booleanValue();

    noSignature = ((Boolean) optMap.get("JTB_NOSIG")).booleanValue();

    noVisitors = ((Boolean) optMap.get("JTB_NOVIS")).booleanValue();

    parentPointer = ((Boolean) optMap.get("JTB_PP")).booleanValue();

    printerToolkit = ((Boolean) optMap.get("JTB_PRINTER")).booleanValue();

    storeSpecialTokens = ((Boolean) optMap.get("JTB_TK")).booleanValue();

    printSpecialTokensJJ = ((Boolean) optMap.get("JTB_TKJJ")).booleanValue();

    noOverwrite = ((Boolean) optMap.get("JTB_W")).booleanValue();

    /* JTB string options */

    baseDirName = (String) optMap.get("JTB_D");
    warnIfEmpty("JTB_D", baseDirName);

    str = (String) optMap.get("JTB_EG");
    externalGeneratorClass = "".equals(str) ? null : str;

    hookDirName = (String) optMap.get("JTB_HKD");
    // checkNotEmpty("JTB_HKD", hookDirName);

    hookPkgName = (String) optMap.get("JTB_HKP");
    // checkNotEmpty("JTB_HKP", hookPkgName);

    nodesDirName = (String) optMap.get("JTB_ND");
    // checkNotEmpty("JTB_ND", nodesDirName);

    nodesPkgName = (String) optMap.get("JTB_NP");
    // checkNotEmpty("JTB_NP", nodesPkgName);

    nodePrefix = (String) optMap.get("JTB_NPFX");

    nodeSuffix = (String) optMap.get("JTB_NSFX");

    isPfxOrSfx = (nodePrefix.length() != 0) || (nodeSuffix.length() != 0);

    str = (String) optMap.get("JTB_NS");
    nodesSuperclass = "".equals(str) ? null : str;

    jtbOutputFileName = (String) optMap.get("JTB_O");
    warnIfEmpty("JTB_O", jtbOutputFileName);

    basePkgName = (String) optMap.get("JTB_P");
    warnIfEmpty("JTB_P", basePkgName);

    signatureDirName = (String) optMap.get("JTB_SIGD");
    // checkNotEmpty("JTB_SIGD", signatureDirName);

    signaturePkgName = (String) optMap.get("JTB_SIGP");
    // checkNotEmpty("JTB_SIGP", signaturePkgName);

    visitorsDirName = (String) optMap.get("JTB_VD");
    // checkNotEmpty("JTB_VD", visitorsDirName);

    visitorsStr = (String) optMap.get("JTB_VIS");

    visitorsPkgName = (String) optMap.get("JTB_VP");
    // checkNotEmpty("JTB_VP", visitorsPkgName);

    /* cross controls and derived values */

    if (printSpecialTokensJJ) {
      // -tkjj implies -tk
      storeSpecialTokens = true;
    }

    // hook

    if (!hook) {
      if (isUserDefined("JTB_HKD")) {
        mess.warning("Option \"-hkd\" not used as option \"-hk\" is not set.");
      }
      if (isUserDefined("JTB_HKP")) {
        mess.warning("Option \"-hkp\" not used as option \"-hk\" is not set.");
      }
    }

    // visitors

    if (noVisitors) {
      if (printerToolkit) {
        mess.warning("Options \"-novis\" overwrites option \"-printer\".");
        printerToolkit = false;
      }
      if (visitorsStr != null) {
        mess.warning("Option \"-novis\" overwrites option \"-vis str\".");
        visitorsStr = null;
      }
      if (!noSignature) {
        mess.warning("Option \"-novis\" overwrites option \"-nosig=false\".");
        noSignature = true;
      }
      if (isUserDefined("JTB_SIGD")) {
        mess.warning("Option \"-sigd\" not used as option \"-novis\" is set.");
      }
      if (isUserDefined("JTB_SIGP")) {
        mess.warning("Option \"-sigp\" not used as option \"-novis\" is set.");
      }
      if (isUserDefined("JTB_VD")) {
        mess.warning("Option \"-vd\" not used as option \"-novis\" is set.");
      }
      if (isUserDefined("JTB_VP")) {
        mess.warning("Option \"-vp\" not used as option \"-novis\" is set.");
      }
    } else {
      if (visitorsStr == null) {
        visitorsStr = DEF_VIS_SPEC;
      }
      // do not parse the specification twice if it comes from the command line
      if ((visitorsList == null) && !createVisitorsList(visitorsStr)) {
        mess.softErr("Invalid visitors specification \"" + visitorsStr + "\".");
      }
    }

    // signatures

    if (noSignature) {
      if (isUserDefined("JTB_SIGD")) {
        mess.warning("Option \"-sigd\" not used as option \"-nosig\" is set.");
      }
      if (isUserDefined("JTB_SIGP")) {
        mess.warning("Option \"-sigp\" not used as option \"-nosig\" is set.");
      }
    }

    // base directory

    boolean nullBDN = false;
    if (baseDirName == null) {
      nullBDN = true;
      baseDirName = aInDir;
      mess.info("\"-d\" option is not set, so the base directory is set to the grammar file directory '"
          + baseDirName + "'");
    } else if (isBlank(baseDirName)) {
      baseDirName = aInDir;
      if (aGrammarPackage == null) {
        mess.info("\"-d\" option is set to blank and grammar has no defined package,"
            + " so base directory is changed to the grammar file directory '" + baseDirName + "'");
      } else {
        baseDirName += File.separator + aGrammarPackage.replace(".", File.separator);
        mess.info("\"-d\" option is set to blank, so base directory is changed to the grammar file directory"
            + " plus the grammar defined package sub directory '" + baseDirName + "'");
      }
    }

    // packages and directories

    boolean useBPNforGDP = false;

    if (aGrammarPackage == null) {

      // no grammar package defined in the grammar

      if (basePkgName == null) {
        // no base package option set, so no sub packages (and no sub directories) allowed
        // (as java forbids importing package-less types, as would be needed for Token.java in the nodes)
        warnIfNotUsed("JTB_D");
        warnIfNotUsed("JTB_P");
        warnIfNotUsed("JTB_HKD");
        warnIfNotUsed("JTB_HKP");
        warnIfNotUsed("JTB_ND");
        warnIfNotUsed("JTB_NP");
        warnIfNotUsed("JTB_VD");
        warnIfNotUsed("JTB_VP");
        warnIfNotUsed("JTB_SIGD");
        warnIfNotUsed("JTB_SIGP");
        hookPkgName = nodesPkgName = visitorsPkgName = signaturePkgName = null;
        hookDirName = nodesDirName = visitorsDirName = signatureDirName = baseDirName;
        mess.info("no grammar defined package, no base package option set, so no sub packages,"
            + " and all directories set to '" + baseDirName + "'");
      } else {
        // a package option set, so use it (logically) in replacement of the grammar declared package
        useBPNforGDP = true;
        mess.info("no grammar defined package, a base package option is set, "
            + "so it will be used for the grammar package");
      }

    }

    if ((aGrammarPackage != null) || //
        useBPNforGDP) {

      // a grammar package is defined in the grammar or a base package replaces it

      // packages
      if (basePkgName != null) {
        // a base package is defined
        if (!isBlank(basePkgName)) {
          // a base package is defined and is non blank: prepend it to non blank sub packages
          if (!isBlank(hookPkgName)) {
            hookPkgName = basePkgName + "." + hookPkgName;
          }
          if (!isBlank(nodesPkgName)) {
            nodesPkgName = basePkgName + "." + nodesPkgName;
          }
          if (!isBlank(visitorsPkgName)) {
            visitorsPkgName = basePkgName + "." + visitorsPkgName;
          }
          if (!isBlank(signaturePkgName)) {
            signaturePkgName = basePkgName + "." + signaturePkgName;
          }
        } else {
          // a base package is defined and is blank: do not prepend it
        }
      } else {
        // no base package defined, so set it to the grammar package
        // and prepend it to non user defined sub packages
        basePkgName = aGrammarPackage;
        if (!isUserDefined("JTB_HKP")) {
          hookPkgName = basePkgName + "." + hookPkgName;
        }
        if (!isUserDefined("JTB_NP")) {
          nodesPkgName = basePkgName + "." + nodesPkgName;
        }
        if (!isUserDefined("JTB_VP")) {
          visitorsPkgName = basePkgName + "." + visitorsPkgName;
        }
        if (!isUserDefined("JTB_SIGP")) {
          signaturePkgName = basePkgName + "." + signaturePkgName;
        }
      }

      // directories
      if (nullBDN) {
        // no base directory defined, so prepend the grammar file directory
        // to the non user defined sub directories
        if (!isUserDefined("JTB_HKD")) {
          hookDirName = baseDirName + File.separator + hookDirName;
        }
        if (!isUserDefined("JTB_ND")) {
          nodesDirName = baseDirName + File.separator + nodesDirName;
        }
        if (!isUserDefined("JTB_VD")) {
          visitorsDirName = baseDirName + File.separator + visitorsDirName;
        }
        if (!isUserDefined("JTB_SIGD")) {
          signatureDirName = baseDirName + File.separator + signatureDirName;
        }
      } else {
        // a non blank base directory defined, so prepend it to all sub directories,
        // or a blank base directory defined, so prepend it (it was changed to the grammar file directory)
        // to all sub directories
        hookDirName = baseDirName + File.separator + hookDirName;
        nodesDirName = baseDirName + File.separator + nodesDirName;
        visitorsDirName = baseDirName + File.separator + visitorsDirName;
        signatureDirName = baseDirName + File.separator + signatureDirName;
      }

    }

    // JavaCC options

    jjIsStatic = ((Boolean) optMap.get("STATIC")).booleanValue();

    final String outLang = ((String) optMap.get("OUTPUT_LANGUAGE")).toLowerCase();
    isCpp = outLang.equals(JJOPT_LANG_CPP);
    isJava = !isCpp;
    jjLang = isCpp ? Lang.CPP : Lang.JAVA;
    fileExt = isCpp ? EXT_LANG_CPP : EXT_LANG_JAVA;

    // get the output directory JavaCC option (where to generate Token.java)
    final String jjOutDirName = jjOutputDirName != null //
        ? jjOutputDirName// from command line
        : aJJOutputDirectory.toPath().normalize().toString(); // from grammar options
    // prepend the grammar file directory if not absolute
    File jjOutDirFile = new File(jjOutDirName);
    if (!jjOutDirFile.isAbsolute()) {
      jjOutDirFile = new File(aInDir + File.separator + jjOutDirFile);
    }
    jjOutputDirName = jjOutDirFile.toPath().normalize().toString();

    if (dumpOptions) {
      dumpAllOptions();
    }

  }

  /**
   * Tells if a given option has been set in the grammar file or in the command line.
   *
   * @param aOption - an option
   * @return true if set in the grammar file or in the command line, false otherwise
   */
  private boolean isUserDefined(final String aOption) {
    return cmdLineSetting.contains(aOption) || inputFileSetting.contains(aOption);
  }

  /**
   * Tells if the given option value is blank, or not.
   *
   * @param aValue : an option value (must not be null)
   * @return true given option value isblank, false otherwise
   */
  private static boolean isBlank(final String aValue) {
    if (aValue.trim().length() == 0) {
      return true;
    }
    return false;
  }

  /**
   * Prints a warning if the given option value is blank.
   *
   * @param aName : an option name
   * @param aValue : an option value
   */
  private void warnIfEmpty(final String aName, final String aValue) {
    if (aValue != null && "".equals(aValue.trim())) {
      mess.warning("Option (" + aName + ") value should not be blank");
    }
  }

  /**
   * Prints a warning if the given user defined option is not used as there is no package declared in the
   * grammar.
   *
   * @param aName : an option name
   */
  private void warnIfNotUsed(final String aName) {
    if (isUserDefined(aName)) {
      mess.warning("Option \"" + aName + "\" not used as no package declared in the grammar.");
    }
  }

  /**
   * Checks -vis / JTB_VIS option string and creates the list of {@link VisitorInfo}.
   *
   * @param aVisitorsStr - the string specifying the visitors info
   * @return true if valid data, false if null or invalid string
   */
  public boolean createVisitorsList(final String aVisitorsStr) {
    if (aVisitorsStr == null) {
      mess.softErr("null visitors specification.");
      return false;
    }
    mess.info("visitors specification : \"" + aVisitorsStr + "\".");
    visitorsList = new ArrayList<>();
    final boolean rc = VisitorInfo.extract(aVisitorsStr, visitorsList);
    if (!rc) {
      return false;
    }
    // sort for ordering imports, ...
    Collections.sort(visitorsList);
    return true;
  }

  /**
   * Dumps all the options (JavaCC & JTB).
   */
  void dumpAllOptions() {
    final TreeMap<String, Object> tm = new TreeMap<>(getOptions());
    mess.info("Options (from file and command line:");
    for (final String key : tm.keySet()) { // NOSONAR, want to have alphabetical order
      mess.info(key + " = " + tm.get(key));
    }
  }

  /**
   * Output languages enumeration (not really used).
   *
   * @author Marc Mazas
   * @version 1.5.0 : 06/2017 : MMa : created for future usage
   */
  enum Lang {
    /** Java language */
    JAVA(JJOPT_LANG_JAVA, EXT_LANG_JAVA),
    /** C++ language */
    CPP(JJOPT_LANG_CPP, EXT_LANG_CPP),
    //
    ;

    /** The (JavaCC option) value */
    final String val;
    /** The (OS) file extension */
    final String ext;

    /**
     * Constructor.
     *
     * @param aVal - the (JavaCC option) value
     * @param aExt - the (OS) file extension
     */
    Lang(final String aVal, final String aExt) {
      val = aVal;
      ext = aExt;
    }
  }

}

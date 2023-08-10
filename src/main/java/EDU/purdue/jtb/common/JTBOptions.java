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
 * @version 1.5.1 : 07/2023 : MMa : fixed issue with imports / packages on grammars with no package
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
   * Changeable flags (command line options) (alphabetical order of commands)
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
  boolean        dumpOptions           = false;
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
  /**
   * static or not static option that comes from JavaCC
   */
  public boolean isStatic              = false;
  /**
   * language option that comes from JavaCC (java by default)
   */
  public Lang    lang                  = Lang.JAVA;
  
  /*
   * Changeable names and derived lists
   */
  
  /**
   * -d option which gives the grammar directory
   */
  public String            grammarDirectoryName   = null;
  /**
   * -eg option which defines an external generator class
   */
  public String            externalGeneratorClass = null;
  /**
   * -hkd & -d options which defines the node scope hook directory name (default is
   * {@link #DEF_HOOK_DIR_NAME})
   */
  public String            hookDirName            = DEF_HOOK_DIR_NAME;
  /**
   * -hkp & -p options which defines the node scope hook package name (default is {@link #DEF_HOOK_PKG_NAME})
   */
  public String            hookPackageName        = DEF_HOOK_PKG_NAME;
  /**
   * -nd & -d options which defines the nodes directory name (default is {@link #DEF_ND_DIR_NAME})
   */
  public String            nodesDirName           = DEF_ND_DIR_NAME;
  /**
   * -npfx & -nsfx options which defines the node' prefix
   */
  public String            nodePrefix             = DEF_ND_PREFIX;
  /**
   * -npfx & -nsfx options which defines the node' suffix
   */
  public String            nodeSuffix             = DEF_ND_SUFFIX;
  /**
   * The flag to tell if there is at least one prefix or one suffix
   */
  public boolean           isPfxOrSfx;
  /**
   * -np & -p options which defines the nodes package name (default is {@link #DEF_ND_PKG_NAME})
   */
  public String            nodesPackageName       = DEF_ND_PKG_NAME;
  /**
   * -ns option which defines the nodes superclass
   */
  public String            nodesSuperclass        = null;
  /**
   * -p option which gives the grammar package name
   */
  public String            grammarPackageName     = null;
  /**
   * -vd & -d options which defines the visitors directory name (default is {@link #DEF_VIS_DIR_NAME})
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
   * -vp & -p options which defines the visitors package name (default is {@link #DEF_VIS_PKG_NAME})
   */
  public String            visitorsPackageName    = DEF_VIS_PKG_NAME;
  /**
   * -o option which defines the output (generated) file name (default is jtb.out.jj)
   */
  public String            jtbOutputFileName      = DEF_OUT_FILE_NAME;
  /**
   * Signature directory name (default is {@link #visitorsDirName}/{@link #DEF_SIG_DIR_NAME})
   */
  public String            signatureDirName       = null;
  /**
   * Signature package name (default is {@link #visitorsPackageName}.{@link #DEF_SIG_DIR_NAME})
   */
  public String            signaturePackageName   = null;
  
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
    optionValues.put("JTB_D", null);
    optionValues.put("JTB_DL", Boolean.FALSE);
    optionValues.put("JTB_DO", Boolean.FALSE);
    optionValues.put("JTB_E", Boolean.FALSE);
    optionValues.put("JTB_EG", null);
    optionValues.put("JTB_F", Boolean.FALSE);
    optionValues.put("JTB_HK", Boolean.FALSE);
    optionValues.put("JTB_HKD", DEF_HOOK_DIR_NAME);
    optionValues.put("JTB_HKP", DEF_HOOK_PKG_NAME);
    optionValues.put("JTB_IA", Boolean.FALSE);
    optionValues.put("JTB_JD", Boolean.FALSE);
    optionValues.put("JTB_ND", DEF_ND_DIR_NAME);
    optionValues.put("JTB_NOPLG", Boolean.FALSE);
    optionValues.put("JTB_NOSIG", Boolean.FALSE);
    optionValues.put("JTB_NOVIS", Boolean.FALSE);
    optionValues.put("JTB_NP", DEF_ND_PKG_NAME);
    optionValues.put("JTB_NPFX", DEF_ND_PREFIX);
    optionValues.put("JTB_NSFX", DEF_ND_SUFFIX);
    optionValues.put("JTB_NS", null);
    optionValues.put("JTB_O", DEF_OUT_FILE_NAME);
    optionValues.put("JTB_P", null);
    optionValues.put("JTB_PP", Boolean.FALSE);
    optionValues.put("JTB_PRINTER", Boolean.FALSE);
    optionValues.put("JTB_TK", Boolean.FALSE);
    optionValues.put("JTB_TKJJ", Boolean.FALSE);
    optionValues.put("JTB_VA", Boolean.FALSE);
    optionValues.put("JTB_VD", DEF_VIS_DIR_NAME);
    optionValues.put("JTB_VIS", null);
    optionValues.put("JTB_VP", DEF_VIS_PKG_NAME);
    optionValues.put("JTB_W", Boolean.FALSE);
  }
  
  /**
   * Stores a boolean option.
   *
   * @param aKey - the command line option name
   * @param aVal - the command line option value
   */
  public void setCmdLineOption(final String aKey, final boolean aVal) {
    optionValues.put(aKey, Boolean.valueOf(aVal));
  }
  
  /**
   * Process a single command line option. The option is parsed and stored in the optionValues map. (overriden
   * by JTB).
   *
   * @param aKey - the command line option name
   * @param aVal - the command line option value
   */
  public void setCmdLineOption(final String aKey, final String aVal) {
    optionValues.put(aKey, aVal);
  }
  
  /**
   * Loads the global variables with the JTB global options from the parsed grammar (which was fed with the
   * command line options and the grammar file options).
   * 
   * @param aGrammarDeclaredPackage - the grammar package name in the grammar
   */
  public void loadJTBGlobalOptions(String aGrammarDeclaredPackage) {
    String str = null;
    final Map<String, Object> optMap = getOptions();
    
    childrenMethods = ((Boolean) optMap.get("JTB_CHM")).booleanValue();
    
    printClassList = ((Boolean) optMap.get("JTB_CL")).booleanValue();
    
    grammarDirectoryName = (String) optMap.get("JTB_D");
    checkNotEmpty("JTB_D", grammarDirectoryName);
    
    depthLevel = ((Boolean) optMap.get("JTB_DL")).booleanValue();
    
    dumpOptions = ((Boolean) optMap.get("JTB_DO")).booleanValue();
    if (dumpOptions) {
      dumpAllOptions();
    }
    
    noSemanticCheck = ((Boolean) optMap.get("JTB_E")).booleanValue();
    
    str = (String) optMap.get("JTB_EG");
    externalGeneratorClass = "".equals(str) ? null : str;
    
    descriptiveFieldNames = ((Boolean) optMap.get("JTB_F")).booleanValue();
    
    hook = ((Boolean) optMap.get("JTB_HK")).booleanValue();
    
    hookDirName = (String) optMap.get("JTB_HKD");
    checkNotEmpty("JTB_HKD", hookDirName);
    
    hookPackageName = (String) optMap.get("JTB_HKP");
    checkNotEmpty("JTB_HKP", hookPackageName);
    
    // hook settings checked after output directory & package settings
    
    inlineAcceptMethods = ((Boolean) optMap.get("JTB_IA")).booleanValue();
    
    javaDocComments = ((Boolean) optMap.get("JTB_JD")).booleanValue();
    
    nodesDirName = (String) optMap.get("JTB_ND");
    checkNotEmpty("JTB_ND", nodesDirName);
    
    noParallel = ((Boolean) optMap.get("JTB_NOPLG")).booleanValue();
    
    noSignature = ((Boolean) optMap.get("JTB_NOSIG")).booleanValue();
    
    noVisitors = ((Boolean) optMap.get("JTB_NOVIS")).booleanValue();
    
    nodesPackageName = (String) optMap.get("JTB_NP");
    checkNotEmpty("JTB_NP", nodesPackageName);
    
    nodePrefix = (String) optMap.get("JTB_NPFX");
    
    nodeSuffix = (String) optMap.get("JTB_NSFX");
    
    isPfxOrSfx = (nodePrefix.length() != 0) || (nodeSuffix.length() != 0);
    
    str = (String) optMap.get("JTB_NS");
    nodesSuperclass = "".equals(str) ? null : str;
    
    jtbOutputFileName = (String) optMap.get("JTB_O");
    checkNotEmpty("JTB_O", jtbOutputFileName);
    
    grammarPackageName = (String) optMap.get("JTB_P");
    checkNotEmpty("JTB_P", grammarPackageName);
    
    parentPointer = ((Boolean) optMap.get("JTB_PP")).booleanValue();
    
    printerToolkit = ((Boolean) optMap.get("JTB_PRINTER")).booleanValue();
    
    storeSpecialTokens = ((Boolean) optMap.get("JTB_TK")).booleanValue();
    
    printSpecialTokensJJ = ((Boolean) optMap.get("JTB_TKJJ")).booleanValue();
    if (printSpecialTokensJJ) {
      // -tkjj implies -tk
      storeSpecialTokens = true;
    }
    
    visitorsDirName = (String) optMap.get("JTB_VD");
    checkNotEmpty("JTB_VD", visitorsDirName);
    
    if (noVisitors && printerToolkit) {
      mess.softErr("Options \"-novis\" and \"-printer\" are incompatible.");
    }
    
    visitorsStr = (String) optMap.get("JTB_VIS");
    if (noVisitors && (visitorsStr != null)) {
      mess.softErr("Options \"-novis\" and \"-vis str\" are incompatible.");
    }
    
    if (!noVisitors) {
      if (visitorsStr == null) {
        visitorsStr = DEF_VIS_SPEC;
      }
      // do not parse the specification twice if it comes from the command line
      if ((visitorsList == null) && !createVisitorsList(visitorsStr)) {
        mess.softErr("Invalid visitors specification \"" + visitorsStr + "\".");
      }
    }
    visitorsPackageName = (String) optMap.get("JTB_VP");
    checkNotEmpty("JTB_VP", visitorsPackageName);
    
    noOverwrite = ((Boolean) optMap.get("JTB_W")).booleanValue();
    
    boolean checkHDN = true;
    if ((grammarDirectoryName != null) && !"".equals(grammarDirectoryName)) {
      if (DEF_HOOK_DIR_NAME.equals(hookDirName))
        hookDirName = grammarDirectoryName + File.separator + DEF_HOOK_DIR_NAME;
      if (DEF_ND_DIR_NAME.equals(nodesDirName))
        nodesDirName = grammarDirectoryName + File.separator + DEF_ND_DIR_NAME;
      if (DEF_VIS_DIR_NAME.equals(visitorsDirName))
        visitorsDirName = grammarDirectoryName + File.separator + DEF_VIS_DIR_NAME;
      checkHDN = false;
    }
    signatureDirName = visitorsDirName + File.separator + DEF_SIG_DIR_NAME;
    
    boolean checkHPN = true;
    if ((grammarPackageName != null) && !"".equals(grammarPackageName)) {
      if (DEF_HOOK_PKG_NAME.equals(hookPackageName))
        hookPackageName = grammarPackageName + "." + DEF_HOOK_PKG_NAME;
      if (DEF_ND_PKG_NAME.equals(nodesPackageName))
        nodesPackageName = grammarPackageName + "." + DEF_ND_PKG_NAME;
      if (DEF_VIS_PKG_NAME.equals(visitorsPackageName))
        visitorsPackageName = grammarPackageName + "." + DEF_VIS_PKG_NAME;
      checkHPN = false;
    }
    signaturePackageName = visitorsPackageName + "." + DEF_SIG_PKG_NAME;
    
    if (aGrammarDeclaredPackage == null) {
      // no package declaration in the grammar
      if (grammarPackageName == null) {
        // no -p / JTB_P option set
        // no sub packages allowed: this is possible, but it will work just for one grammar
        // (the second one will generate classes that will conflict even if they are the same)
        hookDirName = nodesDirName = visitorsDirName = signatureDirName = grammarDirectoryName;
        hookPackageName = nodesPackageName = visitorsPackageName = signaturePackageName = null;
      } else {
        // a -p / JTB_P option set, use it
        mess.warning(
            "No package declaration in the grammar but a grammar package name set in a -p / JTB_P option \""
                + grammarPackageName + "\".");
      }
    } else {
      // a package declaration in the grammar
      if (grammarPackageName == null) {
        // no -p / JTB_P option set; take it in the grammar package declaration
        grammarPackageName = aGrammarDeclaredPackage;
        if (DEF_HOOK_PKG_NAME.equals(hookPackageName))
          hookPackageName = grammarPackageName + "." + DEF_HOOK_PKG_NAME;
        if (DEF_ND_PKG_NAME.equals(nodesPackageName))
          nodesPackageName = grammarPackageName + "." + DEF_ND_PKG_NAME;
        if (DEF_VIS_PKG_NAME.equals(visitorsPackageName))
          visitorsPackageName = grammarPackageName + "." + DEF_VIS_PKG_NAME;
        signaturePackageName = visitorsPackageName + "." + DEF_SIG_PKG_NAME;
      } else {
        if (!aGrammarDeclaredPackage.equals(grammarPackageName)) {
          // a -p / JTB_P option set and different
          mess.warning("Package declared in the grammar (" + aGrammarDeclaredPackage
              + ") different from the grammar package name set in a -p / JTB_P option \"" + grammarPackageName
              + "\".");
        }
      }
    }
    
    if (!hook) {
      if (checkHDN && !DEF_HOOK_DIR_NAME.equals(hookDirName)) {
        mess.warning("Hook directory name set but hook generation not set; ignored");
      }
      if (checkHPN && !DEF_HOOK_PKG_NAME.equals(hookPackageName)) {
        mess.warning("Hook package name set but hook generation not set; ignored");
      }
    }
    
    isStatic = ((Boolean) optMap.get("STATIC")).booleanValue();
    
    final String outLang = ((String) optMap.get("OUTPUT_LANGUAGE")).toLowerCase();
    isCpp = outLang.equals(JJOPT_LANG_CPP);
    isJava = !isCpp;
    lang = isCpp ? Lang.CPP : Lang.JAVA;
    fileExt = isCpp ? EXT_LANG_CPP : EXT_LANG_JAVA;
    
  }
  
  /**
   * Checks that the given option value is not blank, and prints a warning in this case.
   *
   * @param aName : an option name
   * @param aValue : an option value
   */
  private void checkNotEmpty(final String aName, final String aValue) {
    if (aValue != null && "".equals(aValue.trim())) {
      mess.warning("Option (" + aName + ") value should not be blank");
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

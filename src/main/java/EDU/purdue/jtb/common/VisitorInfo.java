package EDU.purdue.jtb.common;

import static EDU.purdue.jtb.common.Constants.LS;
import static EDU.purdue.jtb.common.Constants.PROG_NAME;
import static EDU.purdue.jtb.common.Constants.genArguVar;
import static EDU.purdue.jtb.common.Constants.ptHM;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class {@link VisitorInfo} holds the characteristics (name, return and arguments types) of a visitor to be
 * generated and the {@link #extract(String, List)} method to create the visitors list from the visitors
 * specification. CODEJAVA
 * <p>
 * Class and inner classes maintain state (for a visitor), and are not supposed to be run in parallel threads
 * (on the same visitor).
 * </p>
 * TESTCASE some to add
 *
 * @author Marc Mazas
 * @version 1.5.0 : 02/2017 : MMa : created
 */
public class VisitorInfo implements Comparable<VisitorInfo> {
  
  /** The interface and visitor names suffix */
  public String suffix = null;
  
  /** The interface name */
  public String interfaceName = null;
  
  /** The Depth First visitor name */
  public String dfVisitorName = null;
  
  /** The visitor return type info */
  public ReturnInfo retInfo = null;
  
  /** The list of the visitor arguments info */
  public final List<ArgumentInfo> argInfoList = new ArrayList<>();
  
  /** The class type parameters list string */
  public String classTypeParameters;
  
  /**
   * The accept / visit user parameters list string (e.g. ", final ABC[] argu, final DEF... argu1")
   */
  public String userParameters;
  
  /**
   * The accept / visit user parameters list string (e.g. ", final ABC[] argu, final DEF... argu1") with a
   * suppress warning annotation
   */
  public String userParametersSuppWarn;
  
  /** The accept / visit user arguments list string (e.g. ", argu, argu1") */
  public String userArguments;
  
  /** The imports */
  public String imports;
  
  // regex : (X) capturing group, (?:X) non capturing group
  // \\ escaped \, \w word character [a-zA-Z_0-9], \. dot, + 1..N, * 0..N, ? 0..1
  /** Visitors definition generated interface and class suffix pattern (I<sfx> & DepthFirst<sfx>) */
  private static final String VD_SFX      = "(\\w+)";
  /**
   * Visitors definition user class pattern (not exactly all possible characters, see
   * {@link Character#isJavaIdentifierStart(char)} and {@link Character#isJavaIdentifierPart(char)})
   */
  private static final String VD_CLA      = "\\w+(?:\\.\\w+)*(?:<\\w+>)?";
  /** Visitors definition types pattern */
  private static final String VD_TYPES    = "[A-Z]|boolean|byte|char|double|float|int|long|short|" + VD_CLA;
  /** Visitors definition return part pattern */
  private static final String VD_RET      = "(void|" + VD_TYPES + ")(\\[\\])?";
  /** Visitors definition first argument part pattern */
  private static final String VD_ARG_1    = "(None|" + VD_TYPES + ")(\\[\\]|\\.\\.\\.)?";
  /** Visitors definition other arguments part pattern */
  private static final String VD_ARG_2N   = "(" + VD_TYPES + ")(\\[\\]|\\.\\.\\.)?";
  /** Visitors definition single (suffix + return + arguments) part pattern */
  private static final String VD_SIN      = VD_SFX + "," + VD_RET + "," + VD_ARG_1 + "(?:,(.+))*";
  /** Visitors definition global pattern (multiple suffix + return + arguments) */
  private static final String VD_MUL      = VD_SIN + "(?:;(.+))*";
  /** Visitors definition global compiled pattern */
  public static final Pattern VD_MUL_PATT = Pattern.compile(VD_MUL);
  /** Visitors definition argument part compiled pattern */
  public static final Pattern VD_ARG_PATT = Pattern.compile(VD_ARG_2N + "(?:,(.+))*");
  
  /**
   * Parses the visitors info string and fill the visitors list.
   *
   * @param aStr - the string specifying the visitors info
   * @param aVisitorsList - the visitors list
   * @return true if valid data, false otherwise
   */
  static boolean extract(final String aStr, final List<VisitorInfo> aVisitorsList) {
    final Matcher multMatcher = VD_MUL_PATT.matcher(aStr);
    final int multCount = multMatcher.groupCount(); // should be 7
    if (!multMatcher.matches()) {
      System.err.println(PROG_NAME + ":  invalid visitor specification \"" + aStr + "\"");
      return false;
    }
    final VisitorInfo vi = new VisitorInfo();
    vi.retInfo = vi.new ReturnInfo();
    
    // suffix
    vi.suffix = multMatcher.group(1);
    vi.interfaceName = "I" + vi.suffix + "Visitor";
    vi.dfVisitorName = "DepthFirst" + vi.suffix + "Visitor";
    
    // return type
    final String retType = multMatcher.group(2);
    if ("void".equals(retType)) {
      vi.retInfo.type = retType;
      vi.retInfo.isVoid = true;
    } else if (ptHM.containsKey(retType)) {
      vi.retInfo.type = retType;
    } else if ((retType.length() == 1) //
        && Character.isUpperCase(retType.charAt(0))) {
      vi.retInfo.type = retType;
      vi.retInfo.isTypeParameter = true;
    } else {
      final int ix = retType.lastIndexOf('.');
      vi.retInfo.type = retType.substring(ix + 1);
      vi.retInfo.pkg = ix == -1 ? "" : retType.substring(0, ix);
    }
    vi.retInfo.fullType = vi.retInfo.type;
    final String retTypeArr = multMatcher.group(3);
    if ("[]".equals(retTypeArr)) {
      vi.retInfo.isArray = true;
      vi.retInfo.fullType = vi.retInfo.type + "[]";
    } else if (retTypeArr != null) {
      System.err.println(
          PROG_NAME + ":  \"" + retTypeArr + "\" : invalid return type array spec in visitor definition.");
      return false;
    }
    // initializer
    genInitializer(vi.retInfo);
    
    // "first" argument
    String arguType = multMatcher.group(4);
    if (!"None".equals(arguType)) {
      VisitorInfo.ArgumentInfo ai = vi.new ArgumentInfo();
      vi.argInfoList.add(ai);
      if (ptHM.containsKey(arguType)) {
        ai.type = arguType;
      } else if ((arguType.length() == 1) //
          && Character.isUpperCase(arguType.charAt(0))) {
        ai.type = arguType;
        ai.isTypeParameter = true;
      } else {
        final int ix = arguType.lastIndexOf('.');
        ai.type = arguType.substring(ix + 1);
        ai.pkg = ix == -1 ? "" : arguType.substring(0, ix);
      }
      ai.fullType = ai.type;
      String argTypeArrVar = multMatcher.group(5);
      if ("[]".equals(argTypeArrVar)) {
        ai.isArray = true;
      } else if ("...".equals(argTypeArrVar)) {
        ai.isVarargs = true;
      } else if (argTypeArrVar != null) {
        System.err.println(PROG_NAME + ":  \"" + argTypeArrVar
            + "\" : invalid 1 argument array or varargs spec in visitor definition.");
        return false;
      }
      
      // other arguments
      String args2nStr = multMatcher.group(multCount - 1);
      while (args2nStr != null) {
        final Matcher args2nMatcher = VD_ARG_PATT.matcher(args2nStr);
        final int ac = args2nMatcher.groupCount(); // should be 3
        if (args2nMatcher.matches()) {
          arguType = args2nMatcher.group(1);
          ai = vi.new ArgumentInfo();
          vi.argInfoList.add(ai);
          if (ptHM.containsKey(arguType)) {
            ai.type = arguType;
          } else if ((arguType.length() == 1) //
              && Character.isUpperCase(arguType.charAt(0))) {
            ai.type = arguType;
            ai.isTypeParameter = true;
          } else {
            final int ix = arguType.lastIndexOf('.');
            ai.type = arguType.substring(ix + 1);
            ai.pkg = ix == -1 ? "" : arguType.substring(0, ix);
          }
          ai.fullType = ai.type;
          argTypeArrVar = args2nMatcher.group(2);
          if ("[]".equals(argTypeArrVar)) {
            ai.isArray = true;
            ai.fullType = ai.type + "[]";
          } else if ("...".equals(argTypeArrVar)) {
            ai.fullType = ai.type + "...";
            ai.isVarargs = true;
          } else if (argTypeArrVar != null) {
            System.err.println(PROG_NAME + ":  \"" + argTypeArrVar
                + "\" : invalid 2-n argument array or varargs spec in visitor definition.");
            return false;
          }
          // next argument
          args2nStr = args2nMatcher.group(ac);
        } else {
          break;
        }
      }
    }
    
    // build convenience fields
    vi.classTypeParameters = genClassTypeParameters(vi);
    vi.userParameters = genUserParameters(vi, false);
    vi.userParametersSuppWarn = genUserParameters(vi, true);
    vi.userArguments = genUserArguments(vi);
    vi.imports = genImports(vi);
    
    aVisitorsList.add(vi);
    
    // other visitors
    final String multLastGroupStr = multMatcher.group(multCount);
    if (multLastGroupStr != null) {
      return extract(multLastGroupStr, aVisitorsList);
    }
    
    // return
    return true;
  }
  
  /**
   * Generates the imports.
   *
   * @param aVi - a VisitorInfo defining the visitor to generate
   * @return The "import ...;" lines reflecting the return and arguments classes
   */
  private static String genImports(final VisitorInfo aVi) {
    // build a map of the user packages/classes for return or arguments types, for de-duplication
    final Map<String, String> claList = new TreeMap<>();
    final String retPkg = aVi.retInfo.pkg;
    if (retPkg != null) {
      claList.put(retPkg + "." + aVi.retInfo.type, aVi.retInfo.type);
    }
    for (final ArgumentInfo ai : aVi.argInfoList) {
      final String arguPkg = ai.pkg;
      if (arguPkg != null) {
        claList.put(arguPkg + "." + ai.type, ai.type);
      }
    }
    int len = 0;
    for (final String cla : claList.keySet()) {
      len += cla.length() + 10;
    }
    final StringBuilder sb = new StringBuilder(len);
    for (final String cla : claList.keySet()) {
      sb.append("import ").append(cla).append(";").append(LS);
    }
    return sb.toString();
  }
  
  /**
   * Generates the proposed initializer.
   *
   * @param aRI - the ReturnInfo
   */
  private static void genInitializer(final ReturnInfo aRI) {
    final StringBuilder str = new StringBuilder(16);
    if ("boolean".equals(aRI.type)) {
      if (!aRI.isArray) {
        aRI.initializer = "false";
      } else {
        aRI.initializer = "{ false }";
      }
    } else if ("int".equals(aRI.type) || "char".equals(aRI.type) || "byte".equals(aRI.type)
        || "short".equals(aRI.type) || "long".equals(aRI.type) || "double".equals(aRI.type)
        || "float".equals(aRI.type)) {
      if (!aRI.isArray) {
        aRI.initializer = "0";
      } else {
        aRI.initializer = "{ 0 }";
      }
    } else if (!aRI.isArray) {
      str.append("null");
    } else {
      str.append("{ null }");
    }
  }
  
  /**
   * Generates the class type parameters list string (e.g. "<R[], A, B...>").
   *
   * @param aVi - a VisitorInfo defining the visitor to generate
   * @return the class type parameters list string
   */
  private static String genClassTypeParameters(final VisitorInfo aVi) {
    boolean found = false;
    final StringBuilder str = new StringBuilder(16);
    str.append('<');
    if (aVi.retInfo.isTypeParameter) {
      str.append(aVi.retInfo.type).append(aVi.retInfo.isArray ? "[]" : "");
      found = true;
    }
    for (final ArgumentInfo ai : aVi.argInfoList) {
      if (ai.isTypeParameter) {
        str.append(found ? ", " : "").append(ai.type).append(ai.isArray ? "[]" : ai.isVarargs ? "..." : "");
        found = true;
      }
    }
    if (found) {
      return str.append('>').toString();
    } else {
      return "";
    }
  }
  
  /**
   * Generates the end of a visit method (e.g. ", final A[] argu, final B... argu1").
   *
   * @param aVi - a VisitorInfo defining the visitor to generate
   * @param aSuppWarn - true if to add suppress warning annotations, false otherwise
   * @return the end of the visit methods
   */
  private static String genUserParameters(final VisitorInfo aVi, final boolean aSuppWarn) {
    final StringBuilder str = new StringBuilder(48);
    for (int p = 0; p < aVi.argInfoList.size(); p++) {
      str.append(", ").append(aSuppWarn ? "@SuppressWarnings(\"unused\") " : "").append("final ")
          .append(aVi.argInfoList.get(p).fullType).append(' ').append(genArguVar)
          .append(p == 0 ? "" : String.valueOf(p));
    }
    return str.toString();
  }
  
  /**
   * Generates the accept / visit user arguments (e.g. ", argu, argu1").
   *
   * @param aVi - a VisitorInfo defining the visitor to generate
   * @return the end of the visit methods
   */
  private static String genUserArguments(final VisitorInfo aVi) {
    final StringBuilder str = new StringBuilder(16);
    for (int p = 0; p < aVi.argInfoList.size(); p++) {
      str.append(", ").append(genArguVar).append(p == 0 ? "" : String.valueOf(p));
    }
    return str.toString();
  }
  
  /**
   * Enables ordering of visitors interface and class names.<br>
   */
  @Override
  public int compareTo(final VisitorInfo aVi) {
    return suffix.compareTo(aVi.suffix);
  }
  
  /**
   * Class holding a return type info.
   */
  public class ReturnInfo { // NO_UCD (use private)
    
    /** Return type : void, [A-Z] (type parameter), java primitive types, user class name */
    public String type = null;
    
    /** Full return type : type + [] for an array */
    public String fullType = null;
    
    /** Return type package name : only for user class name */
    String pkg = null;
    
    /** True if the return type is void, false otherwise */
    public boolean isVoid = false;
    
    /** True if the return type is a type parameter, false otherwise */
    public boolean isTypeParameter = false;
    
    /** True if the return type is an array ([]), false otherwise */
    boolean isArray = false;
    
    /** The return variable proposed initialiser */
    public String initializer;
    
  }
  
  /**
   * Class holding an argument type info.
   */
  public class ArgumentInfo {
    
    /** Argument type : [A-Z] (type parameter), java primitive types, user class name */
    public String type = null;
    
    /** Full argument type : argument type + "[]" or "..." */
    String fullType = null;
    
    /** Argument type package name : only for user class name */
    String pkg = null;
    
    /** True if the argument type is a type parameter, false otherwise */
    public boolean isTypeParameter = false;
    
    /** True if the argument type is an array ([]), false otherwise */
    boolean isArray = false;
    
    /** True if the argument type is a varargs (...), false otherwise */
    boolean isVarargs = false;
    
  }
}

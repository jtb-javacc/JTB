/* Copyright (c) 2006, Sun Microsystems, Inc.
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
 *     * Neither the name of the Sun Microsystems, Inc. nor the names of its
 *       contributors may be used to endorse or promote products derived from
 *       this software without specific prior written permission.
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

import static EDU.purdue.jtb.misc.Globals.DEF_OUT_FILE_NAME;
import static EDU.purdue.jtb.misc.Globals.depthLevel;
import static EDU.purdue.jtb.misc.Globals.descriptiveFieldNames;
import static EDU.purdue.jtb.misc.Globals.inlineAcceptMethods;
import static EDU.purdue.jtb.misc.Globals.javaDocComments;
import static EDU.purdue.jtb.misc.Globals.keepSpecialTokens;
import static EDU.purdue.jtb.misc.Globals.noOverwrite;
import static EDU.purdue.jtb.misc.Globals.noSemanticCheck;
import static EDU.purdue.jtb.misc.Globals.nodePrefix;
import static EDU.purdue.jtb.misc.Globals.nodeSuffix;
import static EDU.purdue.jtb.misc.Globals.astNodesDirName;
import static EDU.purdue.jtb.misc.Globals.nodesPackageName;
import static EDU.purdue.jtb.misc.Globals.parentPointer;
import static EDU.purdue.jtb.misc.Globals.printClassList;
import static EDU.purdue.jtb.misc.Globals.printerToolkit;
import static EDU.purdue.jtb.misc.Globals.schemeToolkit;
import static EDU.purdue.jtb.misc.Globals.visitorsDirName;
import static EDU.purdue.jtb.misc.Globals.visitorsPackageName;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * A class with static state that stores all option information.
 * 
 * @author Marc Mazas
 * @version 1.4.0 : 05/2009 : MMa : adapted to JavaCC v4.2 grammar<br>
 *          1.4.0 : 11/2009 : MMa : added JTB options
 * @version 1.4.6 : 01/2011 : FA/MMa : added JTB_VA and JTB_NPFX and JTB_NSFX options
 */
public class Options {

  /**
   * Limit sub classing to derived classes.
   */
  protected Options() {
  }

  /**
   * A mapping of option names (Strings) to values (Integer, Boolean, String). This table is
   * initialized by the main program. Its contents defines the set of legal options. Its initial
   * values define the default option values, and the option types can be determined from these
   * values too.<br>
   * MMa : modified (allocated here) for JTB
   */
  //  protected static Map<String, Object> optionValues = null; ;
  protected static Map<String, Object> optionValues = new HashMap<String, Object>(70); ;

  /**
   * Convenience method to retrieve integer options.
   * 
   * @param option - the option string key
   * @return the option integer value
   */
  protected static int intValue(final String option) {
    return ((Integer) optionValues.get(option)).intValue();
  }

  /**
   * Convenience method to retrieve boolean options.
   * 
   * @param option - the option string key
   * @return the option boolean value
   */
  protected static boolean booleanValue(final String option) {
    return ((Boolean) optionValues.get(option)).booleanValue();
  }

  /**
   * Convenience method to retrieve string options.
   * 
   * @param option - the option string key
   * @return the option string value
   */
  protected static String stringValue(final String option) {
    return (String) optionValues.get(option);
  }

  /**
   * @return the option string value corresponding to the option string key<br>
   *         MMa : modified (direct reference) for JTB
   */
  public static Map<String, Object> getOptions() {
    //    return new HashMap<String, Object>(optionValues);
    return optionValues;
  }

  /**
   * Keep track of what options were set as a command line argument. We use this to see if the
   * options set from the command line and the ones set in the input files clash in any way.
   */
  private static Set<String> cmdLineSetting   = null;
  /**
   * Keep track of what options were set from the grammar file. We use this to see if the options
   * set from the command line and the ones set in the input files clash in any way.
   */
  private static Set<String> inputFileSetting = null;

  /**
   * Initialize for JavaCC / JJTree / JTB
   */
  public static void init() {

    cmdLineSetting = new HashSet<String>();
    inputFileSetting = new HashSet<String>();
    // JavaCC & JJTree options
    optionValues.put("BUILD_PARSER", Boolean.TRUE);
    optionValues.put("BUILD_TOKEN_MANAGER", Boolean.TRUE);
    optionValues.put("CACHE_TOKENS", Boolean.FALSE);
    optionValues.put("CHOICE_AMBIGUITY_CHECK", new Integer(2));
    optionValues.put("COMMON_TOKEN_ACTION", Boolean.FALSE);
    optionValues.put("DEBUG_LOOKAHEAD", Boolean.FALSE);
    optionValues.put("DEBUG_PARSER", Boolean.FALSE);
    optionValues.put("DEBUG_TOKEN_MANAGER", Boolean.FALSE);
    optionValues.put("ERROR_REPORTING", Boolean.TRUE);
    optionValues.put("FORCE_LA_CHECK", Boolean.FALSE);
    optionValues.put("GENERATE_ANNOTATIONS", Boolean.FALSE);
    optionValues.put("GENERATE_CHAINED_EXCEPTION", Boolean.FALSE);
    optionValues.put("GENERATE_GENERICS", Boolean.FALSE);
    optionValues.put("GENERATE_STRING_BUILDER", Boolean.FALSE);
    optionValues.put("GENERATE_VARARG_ARGUMENT", Boolean.FALSE);
    optionValues.put("IGNORE_CASE", Boolean.FALSE);
    optionValues.put("JAVA_UNICODE_ESCAPE", Boolean.FALSE);
    optionValues.put("JDK_VERSION", "1.5");
    optionValues.put("KEEP_LINE_COLUMN", Boolean.TRUE);
    optionValues.put("LOOKAHEAD", new Integer(1));
    optionValues.put("NODE_PREFIX", "");
    optionValues.put("NODE_SUFFIX", "");
    optionValues.put("OTHER_AMBIGUITY_CHECK", new Integer(1));
    optionValues.put("OUTPUT_DIRECTORY", ".");
    optionValues.put("OUTPUT_LANGUAGE", "java");
    optionValues.put("NAMESPACE", "AST");
    optionValues.put("SANITY_CHECK", Boolean.TRUE);
    optionValues.put("STATIC", Boolean.TRUE);
    optionValues.put("SUPPORT_CLASS_VISIBILITY_PUBLIC", Boolean.TRUE);
    optionValues.put("TOKEN_EXTENDS", "");
    optionValues.put("TOKEN_FACTORY", "");
    optionValues.put("TOKEN_MANAGER_USES_PARSER", Boolean.FALSE);
    optionValues.put("UNICODE_INPUT", Boolean.FALSE);
    optionValues.put("USER_CHAR_STREAM", Boolean.FALSE);
    optionValues.put("USER_TOKEN_MANAGER", Boolean.FALSE);
    // JTB Options (with default values or command line arguments)
    // -h & -si are not managed in an input file
    if (optionValues.get("JTB_CL") == null)
      optionValues.put("JTB_CL", new Boolean(printClassList));
    if (optionValues.get("JTB_D") == null)
      optionValues.put("JTB_D", "");
    if (optionValues.get("JTB_DL") == null)
      optionValues.put("JTB_DL", new Boolean(depthLevel));
    if (optionValues.get("JTB_E") == null)
      optionValues.put("JTB_E", new Boolean(noSemanticCheck));
    if (optionValues.get("JTB_F") == null)
      optionValues.put("JTB_F", new Boolean(descriptiveFieldNames));
    if (optionValues.get("JTB_IA") == null)
      optionValues.put("JTB_IA", new Boolean(inlineAcceptMethods));
    if (optionValues.get("JTB_JD") == null)
      optionValues.put("JTB_JD", new Boolean(javaDocComments));
    if (optionValues.get("JTB_ND") == null)
      optionValues.put("JTB_ND", astNodesDirName);
    if (optionValues.get("JTB_NP") == null)
      optionValues.put("JTB_NP", nodesPackageName);
    if (optionValues.get("JTB_NPFX") == null)
      optionValues.put("JTB_NPFX", nodePrefix);
    if (optionValues.get("JTB_NSFX") == null)
      optionValues.put("JTB_NSFX", nodeSuffix);
    if (optionValues.get("JTB_NS") == null)
      optionValues.put("JTB_NS", "");
    if (optionValues.get("JTB_O") == null)
      optionValues.put("JTB_O", DEF_OUT_FILE_NAME);
    if (optionValues.get("JTB_P") == null)
      optionValues.put("JTB_P", "");
    if (optionValues.get("JTB_PP") == null)
      optionValues.put("JTB_PP", new Boolean(parentPointer));
    if (optionValues.get("JTB_PRINTER") == null)
      optionValues.put("JTB_PRINTER", new Boolean(printerToolkit));
    if (optionValues.get("JTB_SCHEME") == null)
      optionValues.put("JTB_SCHEME", new Boolean(schemeToolkit));
    if (optionValues.get("JTB_TK") == null)
      optionValues.put("JTB_TK", new Boolean(keepSpecialTokens));
    if (optionValues.get("JTB_VA") == null)
      optionValues.put("JTB_VA", Boolean.FALSE);
    if (optionValues.get("JTB_VD") == null)
      optionValues.put("JTB_VD", visitorsDirName);
    if (optionValues.get("JTB_VP") == null)
      optionValues.put("JTB_VP", visitorsPackageName);
    if (optionValues.get("JTB_W") == null)
      optionValues.put("JTB_W", new Boolean(noOverwrite));
  }

  /**
   * Returns a string representation of the specified options of interest. Used when, for example,
   * generating Token.java to record the JavaCC options that were used to generate the file. All of
   * the options must be boolean values.
   * 
   * @param interestingOptions - the options of interest, eg {"STATIC", "CACHE_TOKENS"}
   * @return the string representation of the options, eg "STATIC=true,CACHE_TOKENS=false"
   */
  public static String getOptionsString(final String[] interestingOptions) {
    final StringBuilder sb = new StringBuilder(128);
    for (int i = 0; i < interestingOptions.length; i++) {
      final String key = interestingOptions[i];
      sb.append(key);
      sb.append('=');
      sb.append(optionValues.get(key));
      if (i != interestingOptions.length - 1) {
        sb.append(',');
      }
    }
    return sb.toString();
  }

  /**
   * Determine if a given command line argument might be an option flag. Command line options start
   * with a dash&nbsp;(-).
   * 
   * @param opt - The command line argument to examine.
   * @return True when the argument looks like an option flag.
   */
  public static boolean isOption(final String opt) {
    return opt != null && opt.length() > 1 && opt.charAt(0) == '-';
  }

  /**
   * Help function to handle cases where the meaning of an option has changed over time. If the user
   * has supplied an option in the old format, it will be converted to the new format.
   * 
   * @param name - The name of the option being checked.
   * @param value - The option's value.
   * @return The upgraded value.
   */
  public static Object upgradeValue(final String name, final Object value) {
    Object val = value;
    if (name.equalsIgnoreCase("NODE_FACTORY") && value.getClass() == Boolean.class) {
      if (((Boolean) value).booleanValue()) {
        val = "*";
      } else {
        val = "";
      }
    }
    return val;
  }

  /**
   * Sets an option for the input file.
   * 
   * @param nameloc - the option name location
   * @param valueloc - the option value location
   * @param name - the option name
   * @param value - the option value
   */
  public static void setInputFileOption(final Object nameloc, final Object valueloc,
                                        final String name, final Object value) {
    Object val = value;
    final String s = name.toUpperCase();
    if (!optionValues.containsKey(s)) {
      JavaCCErrors.warning(nameloc, "Bad option name \"" + name +
                                    "\".  Option setting will be ignored.");
      return;
    }
    final Object existingValue = optionValues.get(s);
    val = upgradeValue(name, val);
    if (existingValue != null) {
      if ((existingValue.getClass() != val.getClass()) ||
          (val instanceof Integer && ((Integer) val).intValue() <= 0)) {
        JavaCCErrors.warning(valueloc, "Bad option value \"" + val + "\" for \"" + name +
                                       "\".  Option setting will be ignored.");
        return;
      }
      if (inputFileSetting.contains(s)) {
        JavaCCErrors.warning(nameloc, "Duplicate option setting for \"" + name +
                                      "\" will be ignored.");
        return;
      }
      if (cmdLineSetting.contains(s)) {
        if (!existingValue.equals(val)) {
          JavaCCErrors.warning(nameloc, "Command line setting of \"" + name +
                                        "\" modifies option value in file.");
        }
        return;
      }
    }
    optionValues.put(s, val);
    inputFileSetting.add(s);
  }

  /**
   * Process a single command line option. The option is parsed and stored in the optionValues map.
   * (Not used).
   * 
   * @param arg - the command line option
   */
  public static void setCmdLineOption(final String arg) {
    final String s;
    if (arg.charAt(0) == '-') {
      s = arg.substring(1);
    } else {
      s = arg;
    }
    String name;
    Object val;
    // Look for the first ":" or "=", which will separate the option name
    // from its value (if any).
    final int index1 = s.indexOf('=');
    final int index2 = s.indexOf(':');
    final int index;
    if (index1 < 0)
      index = index2;
    else if (index2 < 0)
      index = index1;
    else if (index1 < index2)
      index = index1;
    else
      index = index2;
    if (index < 0) {
      name = s.toUpperCase();
      if (optionValues.containsKey(name)) {
        val = Boolean.TRUE;
      } else if (name.length() > 2 && name.charAt(0) == 'N' && name.charAt(1) == 'O') {
        val = Boolean.FALSE;
        name = name.substring(2);
      } else {
        System.out.println("Warning: Bad option \"" + arg + "\" will be ignored.");
        return;
      }
    } else {
      name = s.substring(0, index).toUpperCase();
      if (s.substring(index + 1).equalsIgnoreCase("TRUE")) {
        val = Boolean.TRUE;
      } else if (s.substring(index + 1).equalsIgnoreCase("FALSE")) {
        val = Boolean.FALSE;
      } else {
        try {
          final int i = Integer.parseInt(s.substring(index + 1));
          if (i <= 0) {
            System.out.println("Warning: Bad option value in \"" + arg + "\" will be ignored.");
            return;
          }
          val = new Integer(i);
        }
        catch (final NumberFormatException e) {
          val = s.substring(index + 1);
          if (s.length() > index + 2) {
            // i.e., there is space for two '"'s in value
            if (s.charAt(index + 1) == '"' && s.charAt(s.length() - 1) == '"') {
              // remove the two '"'s.
              val = s.substring(index + 2, s.length() - 1);
            }
          }
        }
      }
    }
    if (!optionValues.containsKey(name)) {
      System.out.println("Warning: Bad option \"" + arg + "\" will be ignored.");
      return;
    }
    final Object valOrig = optionValues.get(name);
    if (val.getClass() != valOrig.getClass()) {
      System.out.println("Warning: Bad option value in \"" + arg + "\" will be ignored.");
      return;
    }
    if (cmdLineSetting.contains(name)) {
      System.out.println("Warning: Duplicate option setting \"" + arg + "\" will be ignored.");
      return;
    }
    val = upgradeValue(name, val);
    optionValues.put(name, val);
    cmdLineSetting.add(name);
  }

  /**
   * Normalizes the options (ie checks coherence and dependences).
   */
  // MMa : added public
  public static void normalize() {
    if (getDebugLookahead() && !getDebugParser()) {
      if (cmdLineSetting.contains("DEBUG_PARSER") || inputFileSetting.contains("DEBUG_PARSER")) {
        JavaCCErrors.warning("True setting of option DEBUG_LOOKAHEAD overrides "
                             + "false setting of option DEBUG_PARSER.");
      }
      optionValues.put("DEBUG_PARSER", Boolean.TRUE);
    }
    // Now set the "GENERATE" options from the supplied (or default) JDK version.
    optionValues.put("GENERATE_CHAINED_EXCEPTION", Boolean.valueOf(jdkVersionAtLeast(1.4)));
    optionValues.put("GENERATE_GENERICS", Boolean.valueOf(jdkVersionAtLeast(1.5)));
    optionValues.put("GENERATE_STRING_BUILDER", Boolean.valueOf(jdkVersionAtLeast(1.5)));
    optionValues.put("GENERATE_ANNOTATIONS", Boolean.valueOf(jdkVersionAtLeast(1.5)));
  }

  /**
   * Find the build parser value.
   * 
   * @return The requested build parser value.
   */
  public static boolean getBuildParser() {
    return booleanValue("BUILD_PARSER");
  }

  /**
   * Find the build token manager value.
   * 
   * @return The requested build token manager value.
   */
  public static boolean getBuildTokenManager() {
    return booleanValue("BUILD_TOKEN_MANAGER");
  }

  /**
   * Find the cache tokens value.
   * 
   * @return The requested cache tokens value.
   */
  public static boolean getCacheTokens() {
    return booleanValue("CACHE_TOKENS");
  }

  /**
   * Find the choice ambiguity check value.
   * 
   * @return The requested choice ambiguity check value.
   */
  public static int getChoiceAmbiguityCheck() {
    return intValue("CHOICE_AMBIGUITY_CHECK");
  }

  /**
   * Find the common token action value.
   * 
   * @return The requested common token action value.
   */
  public static boolean getCommonTokenAction() {
    return booleanValue("COMMON_TOKEN_ACTION");
  }

  /**
   * Find the debug parser value.
   * 
   * @return The requested debug parser value.
   */
  public static boolean getDebugParser() {
    return booleanValue("DEBUG_PARSER");
  }

  /**
   * Find the debug lookahead value.
   * 
   * @return The requested debug lookahead value.
   */
  public static boolean getDebugLookahead() {
    return booleanValue("DEBUG_LOOKAHEAD");
  }

  /**
   * Find the debug TokenManager value.
   * 
   * @return The requested debug TokenManager value.
   */
  public static boolean getDebugTokenManager() {
    return booleanValue("DEBUG_TOKEN_MANAGER");
  }

  /**
   * Find the error reporting value.
   * 
   * @return The requested error reporting value.
   */
  public static boolean getErrorReporting() {
    return booleanValue("ERROR_REPORTING");
  }

  /**
   * Find the force lookahead check value.
   * 
   * @return The requested force lookahead value.
   */
  public static boolean getForceLaCheck() {
    return booleanValue("FORCE_LA_CHECK");
  }

  /**
   * Should the generated code contain Annotations?
   * 
   * @return The generate annotations value
   */
  public static boolean getGenerateAnnotations() {
    return booleanValue("GENERATE_ANNOTATIONS");
  }

  /**
   * Should the generated code create Exceptions using a constructor taking a nested exception?
   * 
   * @return The generate chained exception value
   */
  public static boolean getGenerateChainedException() {
    return booleanValue("GENERATE_CHAINED_EXCEPTION");
  }

  /**
   * Should the generated code contain Generics?
   * 
   * @return The generate Generics value
   */
  public static boolean getGenerateGenerics() {
    return booleanValue("GENERATE_GENERICS");
  }

  /**
   * Should the generated code use StringBuilder rather than StringBuilder?
   * 
   * @return The generate StringBuilder exception value
   */
  public static boolean getGenerateStringBuilder() {
    return booleanValue("GENERATE_STRING_BUILDER");
  }

  /**
   * Find the ignore case value.
   * 
   * @return The requested ignore case value.
   */
  public static boolean getIgnoreCase() {
    return booleanValue("IGNORE_CASE");
  }

  /**
   * Find the Java Unicode escape value.
   * 
   * @return The requested Java Unicode escape value.
   */
  public static boolean getJavaUnicodeEscape() {
    return booleanValue("JAVA_UNICODE_ESCAPE");
  }

  /**
   * Find the JDK version.
   * 
   * @return The requested JDK version.
   */
  public static String getJdkVersion() {
    return stringValue("JDK_VERSION");
  }

  /**
   * Find the keep line column value.
   * 
   * @return The requested keep line column value.
   */
  public static boolean getKeepLineColumn() {
    return booleanValue("KEEP_LINE_COLUMN");
  }

  /**
   * Find the lookahead setting.
   * 
   * @return The requested lookahead value.
   */
  public static int getLookahead() {
    return intValue("LOOKAHEAD");
  }

  /**
   * Find the other ambiguity check value.
   * 
   * @return The requested other ambiguity check value.
   */
  public static int getOtherAmbiguityCheck() {
    return intValue("OTHER_AMBIGUITY_CHECK");
  }

  /**
   * Find the output directory.
   * 
   * @return The requested output directory.
   */
  public static File getOutputDirectory() {
    return new File(stringValue("OUTPUT_DIRECTORY"));
  }

  /**
   * Find the output directory.
   * 
   * @return The requested output directory.
   */
  public static File getOutputLanguage() {
    return new File(stringValue("OUTPUT_LANGUAGE"));
  }

  /**
   * Find the sanity check value.
   * 
   * @return The requested sanity check value.
   */
  public static boolean getSanityCheck() {
    return booleanValue("SANITY_CHECK");
  }

  /**
   * Find the static value.
   * 
   * @return The requested static value.
   */
  public static boolean getStatic() {
    return booleanValue("STATIC");
  }

  /**
   * Should the generated code class visibility public?
   * 
   * @return The class visibility public value
   */
  public static boolean getSupportClassVisibilityPublic() {
    return booleanValue("SUPPORT_CLASS_VISIBILITY_PUBLIC");
  }

  /**
   * Return the Token's superclass.
   * 
   * @return The required base class for Token.
   */
  public static String getTokenExtends() {
    return stringValue("TOKEN_EXTENDS");
  }

  /**
   * Find the token manager uses parser value.
   * 
   * @return The requested token manager uses parser value;
   */
  public static boolean getTokenManagerUsesParser() {
    return booleanValue("TOKEN_MANAGER_USES_PARSER");
  }

  /**
   * Return the Token's factory class.
   * 
   * @return The required factory class for Token.
   */
  public static String getTokenFactory() {
    return stringValue("TOKEN_FACTORY");
  }

  /**
   * Find the Unicode input value.
   * 
   * @return The requested Unicode input value.
   */
  public static boolean getUnicodeInput() {
    return booleanValue("UNICODE_INPUT");
  }

  /**
   * Find the user CharStream value.
   * 
   * @return The requested user CharStream value.
   */
  public static boolean getUserCharStream() {
    return booleanValue("USER_CHAR_STREAM");
  }

  /**
   * Find the user TokenManager value.
   * 
   * @return The requested user TokenManager value.
   */
  public static boolean getUserTokenManager() {
    return booleanValue("USER_TOKEN_MANAGER");
  }

  /**
   * Determine if the output language is at least the specified version.
   * 
   * @param version - the version to check against. E.g. <code>1.5</code>
   * @return true if the output version is at least the specified version.
   */
  public static boolean jdkVersionAtLeast(final double version) {
    final double jdkVersion = Double.parseDouble(getJdkVersion());
    // Comparing doubles is safe here, as it is two simple assignments.
    return jdkVersion >= version;
  }

  /**
   * @return the "StringBuilder" or "StringBuilder" string
   */
  public static String stringBufOrBuild() {
    if (getGenerateStringBuilder()) {
      return "StringBuilder";
    } else {
      return "StringBuilder";
    }
  }
}

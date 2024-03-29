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

//import static EDU.purdue.jtb.parser.JavaCCParserConstants.PACKAGE;
//import static EDU.purdue.jtb.parser.JavaCCParserConstants.SEMICOLON;
//
//import java.io.BufferedWriter;
//import java.io.File;
//import java.io.FileWriter;
//import java.io.PrintWriter;
//import java.util.ArrayList;
//import java.util.Iterator;
//import java.util.List;

/**
 * Generates the invariant files (TokenMgrError.java, ParseException.java,Token.java,
 * TokenManager.java, CharStream.java, JavaCharStream.java, SimpleCharStream.java) and the
 * 'Parser'Constants.java.<br>
 * Not used by JTB.
 *
 * @author Marc Mazas
 * @version 1.4.0 : 05/2009 : MMa : adapted to JavaCC v4.2 grammar
 * @version 1.4.8 : 12/2014 : MMa : improved javadoc ; moved to imports static
 * @version 1.5.0 : 01/2017 : MMa : added suppress warnings ; renamed class
 */
public class UnusedOtherFilesGen extends JavaCCGlobals {

  //ModMMa 2017/03 commented as unused and with static access
  // /** The JavaCC option */
  //  public static boolean      keepLineCol;
  //
  //  /** The PrintWriter */
  //  static private PrintWriter out;
  //
  //  /**
  //   * @param pn - the parser name
  //   * @throws UnusedMetaParseException - in case of parse or semantic error
  //   */
  //  // ModMMa : modified for Token.template with GTToken
  //  //static public void start() throws MetaParseException {
  //  static public void start(final String pn) throws UnusedMetaParseException {
  //    Token t = null;
  //    keepLineCol = Options.getKeepLineColumn();
  //    if (JavaCCErrors.get_error_count() != 0)
  //      throw new UnusedMetaParseException();
  //    UnusedJavaFiles.gen_TokenMgrError();
  //    UnusedJavaFiles.gen_ParseException();
  //    // ModMMa : modified for Token.template with GTToken
  //    //  JavaFiles.gen_Token();
  //    UnusedJavaFiles.gen_Token(pn);
  //    if (Options.getUserTokenManager()) {
  //      UnusedJavaFiles.gen_TokenManager();
  //    } else if (Options.getUserCharStream()) {
  //      UnusedJavaFiles.gen_CharStream();
  //    } else {
  //      if (Options.getJavaUnicodeEscape()) {
  //        UnusedJavaFiles.gen_JavaCharStream();
  //      } else {
  //        UnusedJavaFiles.gen_SimpleCharStream();
  //      }
  //    }
  //    try {
  //      final File file = new File(Options.getOutputDirectory(), cu_name + "Constants.java");
  //      out = new PrintWriter(new BufferedWriter(new FileWriter(file), 4 * 8192));
  //    }
  //    catch (@SuppressWarnings("unused") final java.io.IOException e) {
  //      JavaCCErrors.semantic_error("Could not open file " + cu_name + "Constants.java for writing.");
  //      throw new Error();
  //    }
  //    final List<String> tn = new ArrayList<>(toolNames);
  //    tn.add(toolName);
  //    out.println("/* " + getIdString(tn, cu_name + "Constants.java") + " */");
  //    if (cu_to_insertion_point_1.size() != 0 && cu_to_insertion_point_1.get(0).kind == PACKAGE) {
  //      for (int i = 1; i < cu_to_insertion_point_1.size(); i++) {
  //        if (cu_to_insertion_point_1.get(i).kind == SEMICOLON) {
  //          printTokenSetup((cu_to_insertion_point_1.get(0)));
  //          for (int j = 0; j <= i; j++) {
  //            t = (cu_to_insertion_point_1.get(j));
  //            printToken(t, out);
  //          }
  //          printTrailingComments(t, out);
  //          out.println("");
  //          out.println("");
  //          break;
  //        }
  //      }
  //    }
  //    out.println("");
  //    out.println("/**");
  //    out.println(" * Token literal values and constants.");
  //    out.println(" * Generated by org.javacc.parser.OtherFilesGen#start()");
  //    out.println(" */");
  //    if (Options.getSupportClassVisibilityPublic()) {
  //      out.print("public ");
  //    }
  //    out.println("interface " + cu_name + "Constants {");
  //    out.println("");
  //    RegularExpression_ re;
  //    out.println("  /** End of File. */");
  //    out.println("  int EOF = 0;");
  //    for (final Iterator<RegularExpression_> it = ordered_named_tokens.iterator(); it.hasNext();) {
  //      re = it.next();
  //      out.println("  /** RegularExpression_ Id. */");
  //      out.println("  int " + re.label + " = " + re.ordinal + ";");
  //    }
  //    out.println("");
  //    if (!Options.getUserTokenManager() && Options.getBuildTokenManager()) {
  //      for (int i = 0; i < LexGen.lexStateName.length; i++) {
  //        out.println("  /** Lexical state. */");
  //        out.println("  int " + LexGen.lexStateName[i] + " = " + i + ";");
  //      }
  //      out.println("");
  //    }
  //    out.println("  /** Literal token values. */");
  //    out.println("  String[] image = {");
  //    out.println("    \"<EOF>\",");
  //    for (final Iterator<TokenProduction> it = rexprlist.iterator(); it.hasNext();) {
  //      final TokenProduction tp = (it.next());
  //      final List<RegExprSpec_> respecs = tp.respecs;
  //      for (final Iterator<RegExprSpec_> it2 = respecs.iterator(); it2.hasNext();) {
  //        final RegExprSpec_ res = (it2.next());
  //        re = res.rexp;
  //        if (re instanceof RStringLiteral) {
  //          out.println("    \"\\\"" + add_escapes(add_escapes(((RStringLiteral) re).image)) +
  //                      "\\\"\",");
  //        } else if (!re.label.equals("")) {
  //          out.println("    \"<" + re.label + ">\",");
  //        } else {
  //          if (re.tpContext.kind == TokenProduction.TOKEN) {
  //            JavaCCErrors.warning(re,
  //                                 "Consider giving this non-string token a label for better error reporting.");
  //          }
  //          out.println("    \"<token of kind " + re.ordinal + ">\",");
  //        }
  //      }
  //    }
  //    out.println("  };");
  //    out.println("");
  //    out.println("}");
  //    out.close();
  //  }
  //
  //  /** Reinitializes */
  //  public static void reInit() {
  //    out = null;
  //  }
}

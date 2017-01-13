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

import static EDU.purdue.jtb.parser.JavaCCParserConstants.CLASS;
import static EDU.purdue.jtb.parser.JavaCCParserConstants.IMPLEMENTS;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Generate the parser.
 *
 * @author Marc Mazas
 * @version 1.4.0 : 05/2009 : MMa : adapted to JavaCC v4.2 grammar
 * @version 1.4.8 : 12/2014 : MMa : moved to imports static
 * @version 1.4.14 : 01/2017 : MMa : added suppress warnings
 */
public class ParseGen extends JavaCCGlobals {

  /** The PrintWriter. */
  static private PrintWriter out;

  /**
   * @return the compilation unit name
   * @throws MetaParseException - in case of parse or semantic error
   */
  // ModMMa : modified for Token.template with GTToken
  //static public void start() throws MetaParseException {
  static public String start() throws MetaParseException {
    Token t = null;
    if (JavaCCErrors.get_error_count() != 0)
      throw new MetaParseException();
    if (Options.getBuildParser()) {
      try {
        final File file = new File(Options.getOutputDirectory(), cu_name + ".java");
        out = new PrintWriter(new BufferedWriter(new FileWriter(file), 4 * 8192));
      }
      catch (@SuppressWarnings("unused") final IOException e) {
        JavaCCErrors.semantic_error("Could not open file " + cu_name + ".java for writing.");
        throw new Error();
      }
      final List<String> tn = new ArrayList<>(toolNames);
      tn.add(toolName);
      out.println("/* " + getIdString(tn, cu_name + ".java") + " */");
      boolean implementsExists = false;
      final String tsClassOrVar = (Options.getStatic() ? cu_name + "TokenManager" : "token_source");
      if (cu_to_insertion_point_1.size() != 0) {
        printTokenSetup((cu_to_insertion_point_1.get(0)));
        ccol = 1;
        for (final Iterator<Token> it = cu_to_insertion_point_1.iterator(); it.hasNext();) {
          t = it.next();
          if (t.kind == IMPLEMENTS) {
            implementsExists = true;
          } else if (t.kind == CLASS) {
            implementsExists = false;
          }
          printToken(t, out);
        }
      }
      if (implementsExists) {
        out.print(", ");
      } else {
        out.print(" implements ");
      }
      out.print(cu_name + "Constants ");
      if (cu_to_insertion_point_2.size() != 0) {
        printTokenSetup((cu_to_insertion_point_2.get(0)));
        for (final Iterator<Token> it = cu_to_insertion_point_2.iterator(); it.hasNext();) {
          t = it.next();
          printToken(t, out);
        }
      }
      out.println("");
      out.println("");
      ParseEngine.build(out);
      if (Options.getStatic()) {
        out.println("  static private boolean jj_initialized_once = false;");
      }
      if (Options.getUserTokenManager()) {
        out.println("  /** User defined Token Manager. */");
        out.println("  " + staticOpt() + "public TokenManager token_source;");
      } else {
        out.println("  /** Generated Token Manager. */");
        out.println("  " + staticOpt() + "public " + cu_name + "TokenManager token_source;");
        if (!Options.getUserCharStream()) {
          if (Options.getJavaUnicodeEscape()) {
            out.println("  " + staticOpt() + "JavaCharStream jj_input_stream;");
          } else {
            out.println("  " + staticOpt() + "SimpleCharStream jj_input_stream;");
          }
        }
      }
      out.println("  /** Current token. */");
      out.println("  " + staticOpt() + "public Token token;");
      out.println("  /** Next token. */");
      out.println("  " + staticOpt() + "public Token jj_nt;");
      if (!Options.getCacheTokens()) {
        out.println("  " + staticOpt() + "private int jj_ntk;");
      }
      if (jj2index != 0) {
        out.println("  " + staticOpt() + "private Token jj_scanpos, jj_lastpos;");
        out.println("  " + staticOpt() + "private int jj_la;");
        if (lookaheadNeeded) {
          out.println("  /** Whether we are looking ahead. */");
          out.println("  " + staticOpt() + "private boolean jj_lookingAhead = false;");
          out.println("  " + staticOpt() + "private boolean jj_semLA;");
        }
      }
      if (Options.getErrorReporting()) {
        out.println("  " + staticOpt() + "private int jj_gen;");
        out.println("  " + staticOpt() + "final private int[] jj_la1 = new int[" + maskindex +
                    "];");
        final int tokenMaskSize = (tokenCount - 1) / 32 + 1;
        for (int i = 0; i < tokenMaskSize; i++)
          out.println("  static private int[] jj_la1_" + i + ";");
        out.println("  static {");
        for (int i = 0; i < tokenMaskSize; i++)
          out.println("      jj_la1_init_" + i + "();");
        out.println("   }");
        for (int i = 0; i < tokenMaskSize; i++) {
          out.println("   private static void jj_la1_init_" + i + "() {");
          out.print("      jj_la1_" + i + " = new int[] {");
          for (final Iterator<int[]> it = maskVals.iterator(); it.hasNext();) {
            final int[] tokenMask = (it.next());
            out.print("0x" + Integer.toHexString(tokenMask[i]) + ",");
          }
          out.println("};");
          out.println("   }");
        }
      }
      if (jj2index != 0 && Options.getErrorReporting()) {
        out.println("  " + staticOpt() + "final private JJCalls[] jj_2_rtns = new JJCalls[" +
                    jj2index + "];");
        out.println("  " + staticOpt() + "private boolean jj_rescan = false;");
        out.println("  " + staticOpt() + "private int jj_gc = 0;");
      }
      out.println("");
      if (!Options.getUserTokenManager()) {
        if (Options.getUserCharStream()) {
          out.println("  /** Constructor with user supplied CharStream. */");
          out.println("  public " + cu_name + "(CharStream stream) {");
          if (Options.getStatic()) {
            out.println("    if (jj_initialized_once) {");
            out.println("      System.out.println(\"ERROR: Second call to constructor of static parser.  \");");
            out.println("      System.out.println(\"       You must either use ReInit() " +
                        "or set the JavaCC option STATIC to false\");");
            out.println("      System.out.println(\"       during parser generation.\");");
            out.println("      throw new Error();");
            out.println("    }");
            out.println("    jj_initialized_once = true;");
          }
          if (Options.getTokenManagerUsesParser() && !Options.getStatic()) {
            out.println("    token_source = new " + cu_name + "TokenManager(this, stream);");
          } else {
            out.println("    token_source = new " + cu_name + "TokenManager(stream);");
          }
          out.println("    token = new Token();");
          if (Options.getCacheTokens()) {
            out.println("    token.next = jj_nt = " + tsClassOrVar + ".getNextToken();");
          } else {
            out.println("    jj_ntk = -1;");
          }
          if (Options.getErrorReporting()) {
            out.println("    jj_gen = 0;");
            out.println("    for (int i = 0; i < " + maskindex + "; i++) jj_la1[i] = -1;");
            if (jj2index != 0) {
              out.println("    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();");
            }
          }
          out.println("  }");
          out.println("");
          out.println("  /** Reinitialize. */");
          out.println("  " + staticOpt() + "public void ReInit(final CharStream stream) {");
          out.println("    " + tsClassOrVar + ".ReInit(stream);");
          out.println("    token = new Token();");
          if (Options.getCacheTokens()) {
            out.println("    token.next = jj_nt = " + tsClassOrVar + ".getNextToken();");
          } else {
            out.println("    jj_ntk = -1;");
          }
          if (lookaheadNeeded) {
            out.println("    jj_lookingAhead = false;");
          }
          if (jjtreeGenerated) {
            out.println("    jjtree.reset();");
          }
          if (Options.getErrorReporting()) {
            out.println("    jj_gen = 0;");
            out.println("    for (int i = 0; i < " + maskindex + "; i++) jj_la1[i] = -1;");
            if (jj2index != 0) {
              out.println("    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();");
            }
          }
          out.println("  }");
        } else {
          out.println("  /** Constructor with InputStream. */");
          out.println("  public " + cu_name + "(java.io.InputStream stream) {");
          out.println("     this(stream, null);");
          out.println("  }");
          out.println("  /** Constructor with InputStream and supplied encoding */");
          out.println("  public " + cu_name + "(java.io.InputStream stream, String encoding) {");
          if (Options.getStatic()) {
            out.println("    if (jj_initialized_once) {");
            out.println("      System.out.println(\"ERROR: Second call to constructor of static parser.  \");");
            out.println("      System.out.println(\"       You must either use ReInit() or " +
                        "set the JavaCC option STATIC to false\");");
            out.println("      System.out.println(\"       during parser generation.\");");
            out.println("      throw new Error();");
            out.println("    }");
            out.println("    jj_initialized_once = true;");
          }
          if (Options.getJavaUnicodeEscape()) {
            if (!Options.getGenerateChainedException()) {
              out.println("    try { jj_input_stream = new JavaCharStream(stream, encoding, 1, 1); } " +
                          "catch(java.io.UnsupportedEncodingException e) {" +
                          " throw new RuntimeException(e.getMessage()); }");
            } else {
              out.println("    try { jj_input_stream = new JavaCharStream(stream, encoding, 1, 1); } " +
                          "catch(java.io.UnsupportedEncodingException e) { throw new RuntimeException(e); }");
            }
          } else {
            if (!Options.getGenerateChainedException()) {
              out.println("    try { jj_input_stream = new SimpleCharStream(stream, encoding, 1, 1); } " +
                          "catch(java.io.UnsupportedEncodingException e) { " +
                          "throw new RuntimeException(e.getMessage()); }");
            } else {
              out.println("    try { jj_input_stream = new SimpleCharStream(stream, encoding, 1, 1); } " +
                          "catch(java.io.UnsupportedEncodingException e) { throw new RuntimeException(e); }");
            }
          }
          if (Options.getTokenManagerUsesParser() && !Options.getStatic()) {
            out.println("    token_source = new " + cu_name +
                        "TokenManager(this, jj_input_stream);");
          } else {
            out.println("    token_source = new " + cu_name + "TokenManager(jj_input_stream);");
          }
          out.println("    token = new Token();");
          if (Options.getCacheTokens()) {
            out.println("    token.next = jj_nt = " + tsClassOrVar + ".getNextToken();");
          } else {
            out.println("    jj_ntk = -1;");
          }
          if (Options.getErrorReporting()) {
            out.println("    jj_gen = 0;");
            out.println("    for (int i = 0; i < " + maskindex + "; i++) jj_la1[i] = -1;");
            if (jj2index != 0) {
              out.println("    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();");
            }
          }
          out.println("  }");
          out.println("");
          out.println("  /** Reinitialize. */");
          out.println("  " + staticOpt() +
                      "public void ReInit(final java.io.InputStream stream) {");
          out.println("     ReInit(stream, null);");
          out.println("  }");
          out.println("  /** Reinitialize. */");
          out.println("  " + staticOpt() +
                      "public void ReInit(final java.io.InputStream stream, final String encoding) {");
          if (!Options.getGenerateChainedException()) {
            out.println("    try { jj_input_stream.ReInit(stream, encoding, 1, 1); } " +
                        "catch(java.io.UnsupportedEncodingException e) { " +
                        "throw new RuntimeException(e.getMessage()); }");
          } else {
            out.println("    try { jj_input_stream.ReInit(stream, encoding, 1, 1); } " +
                        "catch(java.io.UnsupportedEncodingException e) { throw new RuntimeException(e); }");
          }
          out.println("    " + tsClassOrVar + ".ReInit(jj_input_stream);");
          out.println("    token = new Token();");
          if (Options.getCacheTokens()) {
            out.println("    token.next = jj_nt = " + tsClassOrVar + ".getNextToken();");
          } else {
            out.println("    jj_ntk = -1;");
          }
          if (jjtreeGenerated) {
            out.println("    jjtree.reset();");
          }
          if (Options.getErrorReporting()) {
            out.println("    jj_gen = 0;");
            out.println("    for (int i = 0; i < " + maskindex + "; i++) jj_la1[i] = -1;");
            if (jj2index != 0) {
              out.println("    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();");
            }
          }
          out.println("  }");
          out.println("");
          out.println("  /** Constructor. */");
          out.println("  public " + cu_name + "(java.io.Reader stream) {");
          if (Options.getStatic()) {
            out.println("    if (jj_initialized_once) {");
            out.println("      System.out.println(\"ERROR: Second call to constructor of static parser. \");");
            out.println("      System.out.println(\"       You must either use ReInit() or " +
                        "set the JavaCC option STATIC to false\");");
            out.println("      System.out.println(\"       during parser generation.\");");
            out.println("      throw new Error();");
            out.println("    }");
            out.println("    jj_initialized_once = true;");
          }
          if (Options.getJavaUnicodeEscape()) {
            out.println("    jj_input_stream = new JavaCharStream(stream, 1, 1);");
          } else {
            out.println("    jj_input_stream = new SimpleCharStream(stream, 1, 1);");
          }
          if (Options.getTokenManagerUsesParser() && !Options.getStatic()) {
            out.println("    token_source = new " + cu_name +
                        "TokenManager(this, jj_input_stream);");
          } else {
            out.println("    token_source = new " + cu_name + "TokenManager(jj_input_stream);");
          }
          out.println("    token = new Token();");
          if (Options.getCacheTokens()) {
            out.println("    token.next = jj_nt = " + tsClassOrVar + ".getNextToken();");
          } else {
            out.println("    jj_ntk = -1;");
          }
          if (Options.getErrorReporting()) {
            out.println("    jj_gen = 0;");
            out.println("    for (int i = 0; i < " + maskindex + "; i++) jj_la1[i] = -1;");
            if (jj2index != 0) {
              out.println("    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();");
            }
          }
          out.println("  }");
          out.println("");
          out.println("  /** Reinitialize. */");
          out.println("  " + staticOpt() + "public void ReInit(final java.io.Reader stream) {");
          if (Options.getJavaUnicodeEscape()) {
            out.println("    jj_input_stream.ReInit(stream, 1, 1);");
          } else {
            out.println("    jj_input_stream.ReInit(stream, 1, 1);");
          }
          out.println("    " + tsClassOrVar + ".ReInit(jj_input_stream);");
          out.println("    token = new Token();");
          if (Options.getCacheTokens()) {
            out.println("    token.next = jj_nt = " + tsClassOrVar + ".getNextToken();");
          } else {
            out.println("    jj_ntk = -1;");
          }
          if (jjtreeGenerated) {
            out.println("    jjtree.reset();");
          }
          if (Options.getErrorReporting()) {
            out.println("    jj_gen = 0;");
            out.println("    for (int i = 0; i < " + maskindex + "; i++) jj_la1[i] = -1;");
            if (jj2index != 0) {
              out.println("    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();");
            }
          }
          out.println("  }");
        }
      }
      out.println("");
      if (Options.getUserTokenManager()) {
        out.println("  /** Constructor with user supplied Token Manager. */");
        out.println("  public " + cu_name + "(TokenManager tm) {");
      } else {
        out.println("  /** Constructor with generated Token Manager. */");
        out.println("  public " + cu_name + "(" + cu_name + "TokenManager tm) {");
      }
      if (Options.getStatic()) {
        out.println("    if (jj_initialized_once) {");
        out.println("      System.out.println(\"ERROR: Second call to constructor of static parser. \");");
        out.println("      System.out.println(\"       You must either use ReInit() or " +
                    "set the JavaCC option STATIC to false\");");
        out.println("      System.out.println(\"       during parser generation.\");");
        out.println("      throw new Error();");
        out.println("    }");
        out.println("    jj_initialized_once = true;");
      }
      out.println("    token_source = tm;");
      out.println("    token = new Token();");
      if (Options.getCacheTokens()) {
        out.println("    token.next = jj_nt = " + tsClassOrVar + ".getNextToken();");
      } else {
        out.println("    jj_ntk = -1;");
      }
      if (Options.getErrorReporting()) {
        out.println("    jj_gen = 0;");
        out.println("    for (int i = 0; i < " + maskindex + "; i++) jj_la1[i] = -1;");
        if (jj2index != 0) {
          out.println("    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();");
        }
      }
      out.println("  }");
      out.println("");
      if (Options.getUserTokenManager()) {
        out.println("  /** Reinitialize. */");
        out.println("  public void ReInit(final TokenManager tm) {");
      } else {
        out.println("  /** Reinitialize. */");
        out.println("  public void ReInit(final " + cu_name + "TokenManager tm) {");
      }
      out.println("    token_source = tm;");
      out.println("    token = new Token();");
      if (Options.getCacheTokens()) {
        out.println("    token.next = jj_nt = " + tsClassOrVar + ".getNextToken();");
      } else {
        out.println("    jj_ntk = -1;");
      }
      if (jjtreeGenerated) {
        out.println("    jjtree.reset();");
      }
      if (Options.getErrorReporting()) {
        out.println("    jj_gen = 0;");
        out.println("    for (int i = 0; i < " + maskindex + "; i++) jj_la1[i] = -1;");
        if (jj2index != 0) {
          out.println("    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();");
        }
      }
      out.println("  }");
      out.println("");
      out.println("  " + staticOpt() +
                  "private Token jj_consume_token(int kind) throws ParseException {");
      if (Options.getCacheTokens()) {
        out.println("    Token oldToken = token;");
        out.println("    if ((token = jj_nt).next != null) jj_nt = jj_nt.next;");
        out.println("    else jj_nt = jj_nt.next = " + tsClassOrVar + ".getNextToken();");
      } else {
        out.println("    Token oldToken;");
        out.println("    if ((oldToken = token).next != null) token = token.next;");
        out.println("    else token = token.next = " + tsClassOrVar + ".getNextToken();");
        out.println("    jj_ntk = -1;");
      }
      out.println("    if (token.kind == kind) {");
      if (Options.getErrorReporting()) {
        out.println("      jj_gen++;");
        if (jj2index != 0) {
          out.println("      if (++jj_gc > 100) {");
          out.println("        jj_gc = 0;");
          out.println("        for (int i = 0; i < jj_2_rtns.length; i++) {");
          out.println("          JJCalls c = jj_2_rtns[i];");
          out.println("          while (c != null) {");
          out.println("            if (c.gen < jj_gen) c.first = null;");
          out.println("            c = c.next;");
          out.println("          }");
          out.println("        }");
          out.println("      }");
        }
      }
      if (Options.getDebugParser()) {
        out.println("      trace_token(token, \"\");");
      }
      out.println("      return token;");
      out.println("    }");
      if (Options.getCacheTokens()) {
        out.println("    jj_nt = token;");
      }
      out.println("    token = oldToken;");
      if (Options.getErrorReporting()) {
        out.println("    jj_kind = kind;");
      }
      out.println("    throw generateParseException();");
      out.println("  }");
      out.println("");
      if (jj2index != 0) {
        out.println("  static private final class LookaheadSuccess extends java.lang.Error { }");
        out.println("  " + staticOpt() +
                    "final private LookaheadSuccess jj_ls = new LookaheadSuccess();");
        out.println("  " + staticOpt() + "private boolean jj_scan_token(int kind) {");
        out.println("    if (jj_scanpos == jj_lastpos) {");
        out.println("      jj_la--;");
        out.println("      if (jj_scanpos.next == null) {");
        out.println("        jj_lastpos = jj_scanpos = jj_scanpos.next = " + tsClassOrVar +
                    ".getNextToken();");
        out.println("      } else {");
        out.println("        jj_lastpos = jj_scanpos = jj_scanpos.next;");
        out.println("      }");
        out.println("    } else {");
        out.println("      jj_scanpos = jj_scanpos.next;");
        out.println("    }");
        if (Options.getErrorReporting()) {
          out.println("    if (jj_rescan) {");
          out.println("      int i = 0; Token tok = token;");
          out.println("      while (tok != null && tok != jj_scanpos) { i++; tok = tok.next; }");
          out.println("      if (tok != null) jj_add_error_token(kind, i);");
          if (Options.getDebugLookahead()) {
            out.println("    } else {");
            out.println("      trace_scan(jj_scanpos, kind);");
          }
          out.println("    }");
        } else if (Options.getDebugLookahead()) {
          out.println("    trace_scan(jj_scanpos, kind);");
        }
        out.println("    if (jj_scanpos.kind != kind) return true;");
        out.println("    if (jj_la == 0 && jj_scanpos == jj_lastpos) throw jj_ls;");
        out.println("    return false;");
        out.println("  }");
        out.println("");
      }
      out.println("");
      out.println("/** Get the next Token. */");
      out.println("  " + staticOpt() + "final public Token getNextToken() {");
      if (Options.getCacheTokens()) {
        out.println("    if ((token = jj_nt).next != null) jj_nt = jj_nt.next;");
        out.println("    else jj_nt = jj_nt.next = " + tsClassOrVar + ".getNextToken();");
      } else {
        out.println("    if (token.next != null) token = token.next;");
        out.println("    else token = token.next = " + tsClassOrVar + ".getNextToken();");
        out.println("    jj_ntk = -1;");
      }
      if (Options.getErrorReporting()) {
        out.println("    jj_gen++;");
      }
      if (Options.getDebugParser()) {
        out.println("      trace_token(token, \" (in getNextToken)\");");
      }
      out.println("    return token;");
      out.println("  }");
      out.println("");
      out.println("/** Get the specific Token. */");
      out.println("  " + staticOpt() + "final public Token getToken(int index) {");
      if (lookaheadNeeded) {
        out.println("    Token t = jj_lookingAhead ? jj_scanpos : token;");
      } else {
        out.println("    Token t = token;");
      }
      out.println("    for (int i = 0; i < index; i++) {");
      out.println("      if (t.next != null) t = t.next;");
      out.println("      else t = t.next = " + tsClassOrVar + ".getNextToken();");
      out.println("    }");
      out.println("    return t;");
      out.println("  }");
      out.println("");
      if (!Options.getCacheTokens()) {
        out.println("  " + staticOpt() + "private int jj_ntk() {");
        out.println("    if ((jj_nt=token.next) == null)");
        out.println("      return (jj_ntk = (token.next = " + tsClassOrVar +
                    ".getNextToken()).kind);");
        out.println("    else");
        out.println("      return (jj_ntk = jj_nt.kind);");
        out.println("  }");
        out.println("");
      }
      if (Options.getErrorReporting()) {
        if (!Options.getGenerateGenerics())
          out.println("  " + staticOpt() +
                      "private java.util.List jj_expentries = new java.util.ArrayList();");
        else
          out.println("  " + staticOpt() +
                      "private java.util.List<int[]> jj_expentries = new java.util.ArrayList<int[]>();");
        out.println("  " + staticOpt() + "private int[] jj_expentry;");
        out.println("  " + staticOpt() + "private int jj_kind = -1;");
        if (jj2index != 0) {
          out.println("  " + staticOpt() + "private int[] jj_lasttokens = new int[100];");
          out.println("  " + staticOpt() + "private int jj_endpos;");
          out.println("");
          out.println("  " + staticOpt() + "private void jj_add_error_token(int kind, int pos) {");
          out.println("    if (pos >= 100) return;");
          out.println("    if (pos == jj_endpos + 1) {");
          out.println("      jj_lasttokens[jj_endpos++] = kind;");
          out.println("    } else if (jj_endpos != 0) {");
          out.println("      jj_expentry = new int[jj_endpos];");
          out.println("      for (int i = 0; i < jj_endpos; i++) {");
          out.println("        jj_expentry[i] = jj_lasttokens[i];");
          out.println("      }");
          out.println("      jj_entries_loop: for (java.util.Iterator<int[]>  it = jj_expentries.iterator(); it.hasNext();) {");
          out.println("        int[] oldentry = (int[])(it.next());");
          out.println("        if (oldentry.length == jj_expentry.length) {");
          out.println("          for (int i = 0; i < jj_expentry.length; i++) {");
          out.println("            if (oldentry[i] != jj_expentry[i]) {");
          out.println("              continue jj_entries_loop;");
          out.println("            }");
          out.println("          }");
          out.println("          jj_expentries.add(jj_expentry);");
          out.println("          break jj_entries_loop;");
          out.println("        }");
          out.println("      }");
          out.println("      if (pos != 0) jj_lasttokens[(jj_endpos = pos) - 1] = kind;");
          out.println("    }");
          out.println("  }");
        }
        out.println("");
        out.println("  /** Generate ParseException. */");
        out.println("  " + staticOpt() + "public ParseException generateParseException() {");
        out.println("    jj_expentries.clear();");
        out.println("    boolean[] la1tokens = new boolean[" + tokenCount + "];");
        out.println("    if (jj_kind >= 0) {");
        out.println("      la1tokens[jj_kind] = true;");
        out.println("      jj_kind = -1;");
        out.println("    }");
        out.println("    for (int i = 0; i < " + maskindex + "; i++) {");
        out.println("      if (jj_la1[i] == jj_gen) {");
        out.println("        for (int j = 0; j < 32; j++) {");
        for (int i = 0; i < (tokenCount - 1) / 32 + 1; i++) {
          out.println("          if ((jj_la1_" + i + "[i] & (1<<j)) != 0) {");
          out.print("            la1tokens[");
          if (i != 0) {
            out.print((32 * i) + "+");
          }
          out.println("j] = true;");
          out.println("          }");
        }
        out.println("        }");
        out.println("      }");
        out.println("    }");
        out.println("    for (int i = 0; i < " + tokenCount + "; i++) {");
        out.println("      if (la1tokens[i]) {");
        out.println("        jj_expentry = new int[1];");
        out.println("        jj_expentry[0] = i;");
        out.println("        jj_expentries.add(jj_expentry);");
        out.println("      }");
        out.println("    }");
        if (jj2index != 0) {
          out.println("    jj_endpos = 0;");
          out.println("    jj_rescan_token();");
          out.println("    jj_add_error_token(0, 0);");
        }
        out.println("    int[][] exptokseq = new int[jj_expentries.size()][];");
        out.println("    for (int i = 0; i < jj_expentries.size(); i++) {");
        if (!Options.getGenerateGenerics())
          out.println("      exptokseq[i] = (int[])jj_expentries.get(i);");
        else
          out.println("      exptokseq[i] = jj_expentries.get(i);");
        out.println("    }");
        out.println("    return new ParseException(token, exptokseq, tokenImage);");
        out.println("  }");
      } else {
        out.println("  /** Generate ParseException. */");
        out.println("  " + staticOpt() + "public ParseException generateParseException() {");
        out.println("    Token errortok = token.next;");
        if (Options.getKeepLineColumn())
          out.println("    int line = errortok.beginLine, column = errortok.beginColumn;");
        out.println("    String mess = (errortok.kind == 0) ? tokenImage[0] : errortok.image;");
        if (Options.getKeepLineColumn())
          out.println("    return new ParseException(" +
                      "\"Parse error at line \" + line + \", column \" + column + \".  " +
                      "Encountered: \" + mess);");
        else
          out.println("    return new ParseException(\"Parse error at <unknown location>.  " +
                      "Encountered: \" + mess);");
        out.println("  }");
      }
      out.println("");
      if (Options.getDebugParser()) {
        out.println("  " + staticOpt() + "private int trace_indent = 0;");
        out.println("  " + staticOpt() + "private boolean trace_enabled = true;");
        out.println("");
        out.println("/** Enable tracing. */");
        out.println("  " + staticOpt() + "final public void enable_tracing() {");
        out.println("    trace_enabled = true;");
        out.println("  }");
        out.println("");
        out.println("/** Disable tracing. */");
        out.println("  " + staticOpt() + "final public void disable_tracing() {");
        out.println("    trace_enabled = false;");
        out.println("  }");
        out.println("");
        out.println("  " + staticOpt() + "private void trace_call(String s) {");
        out.println("    if (trace_enabled) {");
        out.println("      for (int i = 0; i < trace_indent; i++) { System.out.print(\" \"); }");
        out.println("      System.out.println(\"Call:   \" + s);");
        out.println("    }");
        out.println("    trace_indent = trace_indent + 2;");
        out.println("  }");
        out.println("");
        out.println("  " + staticOpt() + "private void trace_return(String s) {");
        out.println("    trace_indent = trace_indent - 2;");
        out.println("    if (trace_enabled) {");
        out.println("      for (int i = 0; i < trace_indent; i++) { System.out.print(\" \"); }");
        out.println("      System.out.println(\"Return: \" + s);");
        out.println("    }");
        out.println("  }");
        out.println("");
        out.println("  " + staticOpt() + "private void trace_token(Token t, String where) {");
        out.println("    if (trace_enabled) {");
        out.println("      for (int i = 0; i < trace_indent; i++) { System.out.print(\" \"); }");
        out.println("      System.out.print(\"Consumed token: <\" + tokenImage[t.kind]);");
        out.println("      if (t.kind != 0 && !tokenImage[t.kind].equals(\"\\\"\" + t.image + \"\\\"\")) {");
        out.println("        System.out.print(\": \\\"\" + t.image + \"\\\"\");");
        out.println("      }");
        out.println("      System.out.println(\" at line \" + t.beginLine + " +
                    "\" column \" + t.beginColumn + \">\" + where);");
        out.println("    }");
        out.println("  }");
        out.println("");
        out.println("  " + staticOpt() + "private void trace_scan(Token t1, int t2) {");
        out.println("    if (trace_enabled) {");
        out.println("      for (int i = 0; i < trace_indent; i++) { System.out.print(\" \"); }");
        out.println("      System.out.print(\"Visited token: <\" + tokenImage[t1.kind]);");
        out.println("      if (t1.kind != 0 && !tokenImage[t1.kind].equals(\"\\\"\" + t1.image + \"\\\"\")) {");
        out.println("        System.out.print(\": \\\"\" + t1.image + \"\\\"\");");
        out.println("      }");
        out.println("      System.out.println(\" at line \" + t1.beginLine + \"" +
                    " column \" + t1.beginColumn + \">; Expected token: <\" + tokenImage[t2] + \">\");");
        out.println("    }");
        out.println("  }");
        out.println("");
      } else {
        out.println("  /** Enable tracing. */");
        out.println("  " + staticOpt() + "final public void enable_tracing() {");
        out.println("  }");
        out.println("");
        out.println("  /** Disable tracing. */");
        out.println("  " + staticOpt() + "final public void disable_tracing() {");
        out.println("  }");
        out.println("");
      }
      if (jj2index != 0 && Options.getErrorReporting()) {
        out.println("  " + staticOpt() + "private void jj_rescan_token() {");
        out.println("    jj_rescan = true;");
        out.println("    for (int i = 0; i < " + jj2index + "; i++) {");
        out.println("    try {");
        out.println("      JJCalls p = jj_2_rtns[i];");
        out.println("      do {");
        out.println("        if (p.gen > jj_gen) {");
        out.println("          jj_la = p.arg; jj_lastpos = jj_scanpos = p.first;");
        out.println("          switch (i) {");
        for (int i = 0; i < jj2index; i++) {
          out.println("            case " + i + ": jj_3_" + (i + 1) + "(); break;");
        }
        out.println("          }");
        out.println("        }");
        out.println("        p = p.next;");
        out.println("      } while (p != null);");
        out.println("      } catch(LookaheadSuccess ls) { }");
        out.println("    }");
        out.println("    jj_rescan = false;");
        out.println("  }");
        out.println("");
        out.println("  " + staticOpt() + "private void jj_save(int index, int xla) {");
        out.println("    JJCalls p = jj_2_rtns[index];");
        out.println("    while (p.gen > jj_gen) {");
        out.println("      if (p.next == null) { p = p.next = new JJCalls(); break; }");
        out.println("      p = p.next;");
        out.println("    }");
        out.println("    p.gen = jj_gen + xla - jj_la; p.first = token; p.arg = xla;");
        out.println("  }");
        out.println("");
      }
      if (jj2index != 0 && Options.getErrorReporting()) {
        out.println("  static final class JJCalls {");
        out.println("    int gen;");
        out.println("    Token first;");
        out.println("    int arg;");
        out.println("    JJCalls next;");
        out.println("  }");
        out.println("");
      }
      if (cu_from_insertion_point_2.size() != 0) {
        printTokenSetup((cu_from_insertion_point_2.get(0)));
        ccol = 1;
        for (final Iterator<Token> it = cu_from_insertion_point_2.iterator(); it.hasNext();) {
          t = it.next();
          printToken(t, out);
        }
        printTrailingComments(t, out);
      }
      out.println("");
      out.close();
    } // matches "if (Options.getBuildParser())"
    // ModMMa : added for Token.template with GTToken
    return cu_name;
  }

  /** Reinitializes */
  public static void reInit() {
    out = null;
    lookaheadNeeded = false;
  }
}

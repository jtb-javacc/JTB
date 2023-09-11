/* NoOptionGrammar.java */
/* Generated By:JavaCC: Do not edit this line. NoOptionGrammar.java */
package grammars.y;

import grammars.y.visitor.*;
import java.util.ArrayList;
import static java.lang.Float.MIN_NORMAL;
import grammars.y.syntaxtree.*;


public class NoOptionGrammar implements NoOptionGrammarConstants {


  /** Return variable for the {@link #bp_jual} BNFProduction) */
  static java.util.ArrayList<Float> jtbrt_bp_jual;

  /** Return variable for the {@link #bp_hm} BNFProduction) */
  static java.util.HashMap<String, Float> jtbrt_bp_hm;

  /** Return variable for the {@link #jc_0} JavaCodeProduction) */
  static int jtbrt_jc_0;

  public static void main(String args[]) {
    System.err.flush();
    System.out.println("NoOptionGrammar Reading ...");
    NoOptionGrammar sg = new NoOptionGrammar(System.in);
    try {
      classDeclaration cd = sg.classDeclaration();
      DepthFirstVoidVisitor v = new DepthFirstVoidVisitor();
      System.out.println("... Visiting...");
      cd.accept(v);
      System.out.println("NoOptionGrammar ended.");
      System.exit(0);
    }
    catch (final Exception e) {
      System.out.flush();
      System.err.println("Oops!");
      System.err.println(e.getMessage());
      System.exit(-1);
    }
  }

  public static Token newToken(int ofKind, String image) {
    return new Token(ofKind, image);
  }

  static final public classDeclaration classDeclaration() throws ParseException {Token n0 = null;
  className n1 = null;
  Token n2 = null;
  NodeListOptional n3 = new NodeListOptional();
  NodeChoice n4 = null;
  method n5 = null;
  instruction n6 = null;
  Token n7 = null;
  Token n8 = null;
  jc_0 n9 = null;
  Token n10 = null;
  classDeclaration jtbNode = null;
    n0 = jj_consume_token(1);
    n1 = className();
    n2 = jj_consume_token(2);
    label_1:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk_f():jj_ntk) {
      case ID:{
        ;
        break;
        }
      default:
        jj_la1[0] = jj_gen;
        break label_1;
      }
      if (jj_2_1(2)) {
        n5 = method();
n4 = new NodeChoice(n5, 0, 2);
      } else {
        switch ((jj_ntk==-1)?jj_ntk_f():jj_ntk) {
        case ID:{
          n6 = instruction();
n4 = new NodeChoice(n6, 1, 2);
          break;
          }
        default:
          jj_la1[1] = jj_gen;
          jj_consume_token(-1);
          throw new ParseException();
        }
      }
n3.addNode(n4);
    }
n3.nodes.trimToSize();
    n7 = jj_consume_token(3);
    n8 = jj_consume_token(4);
    n9 = jc_0();
    n10 = jj_consume_token(0);
n10.beginColumn++;
n10.endColumn++;
jtbNode = new classDeclaration(n0, n1, n2, n3, n7, n8, n9, n10);
{if ("" != null) return jtbNode;}
    throw new Error("Missing return statement in function");
}

  static final public className className() throws ParseException {Token n0 = null;
  className jtbNode = null;
  char b, e;
  Token t = null;
    n0 = jj_consume_token(ID);
t = n0;
b = '\b';
jtbNode = new className(n0);
{if ("" != null) return jtbNode;}
    throw new Error("Missing return statement in function");
}

  static final public method method() throws ParseException {methodName n0 = null;
  Token n1 = null;
  NodeList n2 = new NodeList();
  instruction n3 = null;
  Token n4 = null;
  method jtbNode = null;
    n0 = methodName();
    n1 = jj_consume_token(5);
    label_2:
    while (true) {
      n3 = instruction();
n2.addNode(n3);
      switch ((jj_ntk==-1)?jj_ntk_f():jj_ntk) {
      case ID:{
        ;
        break;
        }
      default:
        jj_la1[2] = jj_gen;
        break label_2;
      }
    }
n2.nodes.trimToSize();
    n4 = jj_consume_token(6);
jtbNode = new method(n0, n1, n2, n4);
{if ("" != null) return jtbNode;}
    throw new Error("Missing return statement in function");
}

  static final public methodName methodName() throws ParseException {Token n0 = null;
  methodName jtbNode = null;
    n0 = jj_consume_token(ID);
jtbNode = new methodName(n0);
{if ("" != null) return jtbNode;}
    throw new Error("Missing return statement in function");
}

  static final public instruction instruction() throws ParseException {NodeChoice n0 = null;
  NodeSequence n1 = null;
  Token n2 = null;
  Token n3 = null;
  Token n4 = null;
  instruction jtbNode = null;
    if (jj_2_2(2)) {
n1 = new NodeSequence(2);
      n2 = jj_consume_token(ID);
n1.addNode(n2);
      n3 = jj_consume_token(7);
n1.addNode(n3);
n0 = new NodeChoice(n1, 0, 2);
    } else {
      switch ((jj_ntk==-1)?jj_ntk_f():jj_ntk) {
      case ID:{
        bp_al();
        n4 = jj_consume_token(8);
n0 = new NodeChoice(n4, 1, 2);
        break;
        }
      default:
        jj_la1[3] = jj_gen;
        jj_consume_token(-1);
        throw new ParseException();
      }
    }
jtbNode = new instruction(n0);
{if ("" != null) return jtbNode;}
    throw new Error("Missing return statement in function");
}

  static final public ArrayList<Float> bp_al() throws ParseException {ArrayList<Float> al = new ArrayList<Float>();
  java.util.ArrayList<Float> jual = null;
    jj_consume_token(ID);
al.add(-23.E-40F);
    jual = new java.util.ArrayList(al);
    jual.add(.12e+30F);
    {if ("" != null) return jual;}
    throw new Error("Missing return statement in function");
}

  static final public bp_jual bp_jual() throws ParseException {Token n0 = null;
  bp_jual jtbNode = null;
  ArrayList<Float> al = new ArrayList<Float>();
  java.util.ArrayList<Float> jual = null;
    n0 = jj_consume_token(ID);
jtbNode = new bp_jual(n0);
al.add(MIN_NORMAL);
    jual = new java.util.ArrayList(al);
    jual.add(.12e+30F);
    jtbrt_bp_jual = jual;
{if ("" != null) return jtbNode;}
    throw new Error("Missing return statement in function");
}

  static final public bp_hm bp_hm(final java.util.ArrayList<Float> jual) throws ParseException {Token n0 = null;
  bp_hm jtbNode = null;
  java.util.HashMap<String , Float> hm = new java.util.HashMap<String , Float>();
    n0 = jj_consume_token(ID);
jtbNode = new bp_hm(n0);
hm.put("nine", jual == null ? 9.9F : jual.get(9));
    jtbrt_bp_hm = hm;
{if ("" != null) return jtbNode;}
    throw new Error("Missing return statement in function");
}

  static jc_0 jc_0() throws ParseException {int i = 0;jtbrt_jc_0 = i;
  return new jc_0();
  }

  static private boolean jj_2_1(int xla)
 {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return (!jj_3_1()); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(0, xla); }
  }

  static private boolean jj_2_2(int xla)
 {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return (!jj_3_2()); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(1, xla); }
  }

  static private boolean jj_3R_methodName_139_3_4()
 {
    if (jj_scan_token(ID)) return true;
    return false;
  }

  static private boolean jj_3_2()
 {
    if (jj_scan_token(ID)) return true;
    if (jj_scan_token(7)) return true;
    return false;
  }

  static private boolean jj_3_1()
 {
    if (jj_3R_method_121_3_3()) return true;
    return false;
  }

  static private boolean jj_3R_method_121_3_3()
 {
    if (jj_3R_methodName_139_3_4()) return true;
    if (jj_scan_token(5)) return true;
    return false;
  }

  static private boolean jj_initialized_once = false;
  /** Generated Token Manager. */
  static public NoOptionGrammarTokenManager token_source;
  static SimpleCharStream jj_input_stream;
  /** Current token. */
  static public Token token;
  /** Next token. */
  static public Token jj_nt;
  static private int jj_ntk;
  static private Token jj_scanpos, jj_lastpos;
  static private int jj_la;
  static private int jj_gen;
  static final private int[] jj_la1 = new int[4];
  static private int[] jj_la1_0;
  static {
	   jj_la1_init_0();
	}
	private static void jj_la1_init_0() {
	   jj_la1_0 = new int[] {0x1000,0x1000,0x1000,0x1000,};
	}
  static final private JJCalls[] jj_2_rtns = new JJCalls[2];
  static private boolean jj_rescan = false;
  static private int jj_gc = 0;

  /** Constructor with InputStream. */
  public NoOptionGrammar(java.io.InputStream stream) {
	  this(stream, null);
  }
  /** Constructor with InputStream and supplied encoding */
  public NoOptionGrammar(java.io.InputStream stream, String encoding) {
	 if (jj_initialized_once) {
	   System.out.println("ERROR: Second call to constructor of static parser.  ");
	   System.out.println("	   You must either use ReInit() or set the JavaCC option STATIC to false");
	   System.out.println("	   during parser generation.");
	   throw new Error();
	 }
	 jj_initialized_once = true;
	 try { jj_input_stream = new SimpleCharStream(stream, encoding, 1, 1); } catch(java.io.UnsupportedEncodingException e) { throw new RuntimeException(e); }
	 token_source = new NoOptionGrammarTokenManager(jj_input_stream);
	 token = new Token();
	 jj_ntk = -1;
	 jj_gen = 0;
	 for (int i = 0; i < 4; i++) jj_la1[i] = -1;
	 for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  /** Reinitialise. */
  static public void ReInit(java.io.InputStream stream) {
	  ReInit(stream, null);
  }
  /** Reinitialise. */
  static public void ReInit(java.io.InputStream stream, String encoding) {
	 try { jj_input_stream.ReInit(stream, encoding, 1, 1); } catch(java.io.UnsupportedEncodingException e) { throw new RuntimeException(e); }
	 token_source.ReInit(jj_input_stream);
	 token = new Token();
	 jj_ntk = -1;
	 jj_gen = 0;
	 for (int i = 0; i < 4; i++) jj_la1[i] = -1;
	 for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  /** Constructor. */
  public NoOptionGrammar(java.io.Reader stream) {
	 if (jj_initialized_once) {
	   System.out.println("ERROR: Second call to constructor of static parser. ");
	   System.out.println("	   You must either use ReInit() or set the JavaCC option STATIC to false");
	   System.out.println("	   during parser generation.");
	   throw new Error();
	 }
	 jj_initialized_once = true;
	 jj_input_stream = new SimpleCharStream(stream, 1, 1);
	 token_source = new NoOptionGrammarTokenManager(jj_input_stream);
	 token = new Token();
	 jj_ntk = -1;
	 jj_gen = 0;
	 for (int i = 0; i < 4; i++) jj_la1[i] = -1;
	 for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  /** Reinitialise. */
  static public void ReInit(java.io.Reader stream) {
	if (jj_input_stream == null) {
	   jj_input_stream = new SimpleCharStream(stream, 1, 1);
	} else {
	   jj_input_stream.ReInit(stream, 1, 1);
	}
	if (token_source == null) {
 token_source = new NoOptionGrammarTokenManager(jj_input_stream);
	}

	 token_source.ReInit(jj_input_stream);
	 token = new Token();
	 jj_ntk = -1;
	 jj_gen = 0;
	 for (int i = 0; i < 4; i++) jj_la1[i] = -1;
	 for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  /** Constructor with generated Token Manager. */
  public NoOptionGrammar(NoOptionGrammarTokenManager tm) {
	 if (jj_initialized_once) {
	   System.out.println("ERROR: Second call to constructor of static parser. ");
	   System.out.println("	   You must either use ReInit() or set the JavaCC option STATIC to false");
	   System.out.println("	   during parser generation.");
	   throw new Error();
	 }
	 jj_initialized_once = true;
	 token_source = tm;
	 token = new Token();
	 jj_ntk = -1;
	 jj_gen = 0;
	 for (int i = 0; i < 4; i++) jj_la1[i] = -1;
	 for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  /** Reinitialise. */
  public void ReInit(NoOptionGrammarTokenManager tm) {
	 token_source = tm;
	 token = new Token();
	 jj_ntk = -1;
	 jj_gen = 0;
	 for (int i = 0; i < 4; i++) jj_la1[i] = -1;
	 for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  static private Token jj_consume_token(int kind) throws ParseException {
	 Token oldToken;
	 if ((oldToken = token).next != null) token = token.next;
	 else token = token.next = token_source.getNextToken();
	 jj_ntk = -1;
	 if (token.kind == kind) {
	   jj_gen++;
	   if (++jj_gc > 100) {
		 jj_gc = 0;
		 for (int i = 0; i < jj_2_rtns.length; i++) {
		   JJCalls c = jj_2_rtns[i];
		   while (c != null) {
			 if (c.gen < jj_gen) c.first = null;
			 c = c.next;
		   }
		 }
	   }
	   return token;
	 }
	 token = oldToken;
	 jj_kind = kind;
	 throw generateParseException();
  }

  @SuppressWarnings("serial")
  static private final class LookaheadSuccess extends java.lang.Error {
    @Override
    public Throwable fillInStackTrace() {
      return this;
    }
  }
  static private final LookaheadSuccess jj_ls = new LookaheadSuccess();
  static private boolean jj_scan_token(int kind) {
	 if (jj_scanpos == jj_lastpos) {
	   jj_la--;
	   if (jj_scanpos.next == null) {
		 jj_lastpos = jj_scanpos = jj_scanpos.next = token_source.getNextToken();
	   } else {
		 jj_lastpos = jj_scanpos = jj_scanpos.next;
	   }
	 } else {
	   jj_scanpos = jj_scanpos.next;
	 }
	 if (jj_rescan) {
	   int i = 0; Token tok = token;
	   while (tok != null && tok != jj_scanpos) { i++; tok = tok.next; }
	   if (tok != null) jj_add_error_token(kind, i);
	 }
	 if (jj_scanpos.kind != kind) return true;
	 if (jj_la == 0 && jj_scanpos == jj_lastpos) throw jj_ls;
	 return false;
  }


/** Get the next Token. */
  static final public Token getNextToken() {
	 if (token.next != null) token = token.next;
	 else token = token.next = token_source.getNextToken();
	 jj_ntk = -1;
	 jj_gen++;
	 return token;
  }

/** Get the specific Token. */
  static final public Token getToken(int index) {
	 Token t = token;
	 for (int i = 0; i < index; i++) {
	   if (t.next != null) t = t.next;
	   else t = t.next = token_source.getNextToken();
	 }
	 return t;
  }

  static private int jj_ntk_f() {
	 if ((jj_nt=token.next) == null)
	   return (jj_ntk = (token.next=token_source.getNextToken()).kind);
	 else
	   return (jj_ntk = jj_nt.kind);
  }

  static private java.util.List<int[]> jj_expentries = new java.util.ArrayList<int[]>();
  static private int[] jj_expentry;
  static private int jj_kind = -1;
  static private int[] jj_lasttokens = new int[100];
  static private int jj_endpos;

  static private void jj_add_error_token(int kind, int pos) {
	 if (pos >= 100) {
		return;
	 }

	 if (pos == jj_endpos + 1) {
	   jj_lasttokens[jj_endpos++] = kind;
	 } else if (jj_endpos != 0) {
	   jj_expentry = new int[jj_endpos];

	   for (int i = 0; i < jj_endpos; i++) {
		 jj_expentry[i] = jj_lasttokens[i];
	   }

	   for (int[] oldentry : jj_expentries) {
		 if (oldentry.length == jj_expentry.length) {
		   boolean isMatched = true;

		   for (int i = 0; i < jj_expentry.length; i++) {
			 if (oldentry[i] != jj_expentry[i]) {
			   isMatched = false;
			   break;
			 }

		   }
		   if (isMatched) {
			 jj_expentries.add(jj_expentry);
			 break;
		   }
		 }
	   }

	   if (pos != 0) {
		 jj_lasttokens[(jj_endpos = pos) - 1] = kind;
	   }
	 }
  }

  /** Generate ParseException. */
  static public ParseException generateParseException() {
	 jj_expentries.clear();
	 boolean[] la1tokens = new boolean[14];
	 if (jj_kind >= 0) {
	   la1tokens[jj_kind] = true;
	   jj_kind = -1;
	 }
	 for (int i = 0; i < 4; i++) {
	   if (jj_la1[i] == jj_gen) {
		 for (int j = 0; j < 32; j++) {
		   if ((jj_la1_0[i] & (1<<j)) != 0) {
			 la1tokens[j] = true;
		   }
		 }
	   }
	 }
	 for (int i = 0; i < 14; i++) {
	   if (la1tokens[i]) {
		 jj_expentry = new int[1];
		 jj_expentry[0] = i;
		 jj_expentries.add(jj_expentry);
	   }
	 }
	 jj_endpos = 0;
	 jj_rescan_token();
	 jj_add_error_token(0, 0);
	 int[][] exptokseq = new int[jj_expentries.size()][];
	 for (int i = 0; i < jj_expentries.size(); i++) {
	   exptokseq[i] = jj_expentries.get(i);
	 }
	 return new ParseException(token, exptokseq, tokenImage);
  }

  static private boolean trace_enabled;

/** Trace enabled. */
  static final public boolean trace_enabled() {
	 return trace_enabled;
  }

  /** Enable tracing. */
  static final public void enable_tracing() {
  }

  /** Disable tracing. */
  static final public void disable_tracing() {
  }

  static private void jj_rescan_token() {
	 jj_rescan = true;
	 for (int i = 0; i < 2; i++) {
	   try {
		 JJCalls p = jj_2_rtns[i];

		 do {
		   if (p.gen > jj_gen) {
			 jj_la = p.arg; jj_lastpos = jj_scanpos = p.first;
			 switch (i) {
			   case 0: jj_3_1(); break;
			   case 1: jj_3_2(); break;
			 }
		   }
		   p = p.next;
		 } while (p != null);

		 } catch(LookaheadSuccess ls) { }
	 }
	 jj_rescan = false;
  }

  static private void jj_save(int index, int xla) {
	 JJCalls p = jj_2_rtns[index];
	 while (p.gen > jj_gen) {
	   if (p.next == null) { p = p.next = new JJCalls(); break; }
	   p = p.next;
	 }

	 p.gen = jj_gen + xla - jj_la; 
	 p.first = token;
	 p.arg = xla;
  }

  static final class JJCalls {
	 int gen;
	 Token first;
	 int arg;
	 JJCalls next;
  }

}

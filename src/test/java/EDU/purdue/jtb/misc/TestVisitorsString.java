package EDU.purdue.jtb.misc;

import static EDU.purdue.jtb.common.VisitorInfo.VD_ARG_PATT;
import static EDU.purdue.jtb.common.VisitorInfo.VD_MUL_PATT;
import java.util.regex.Matcher;
import EDU.purdue.jtb.JTB;

/**
 * Class for development test of visitors specification string.
 *
 * @author Marc Mazas
 * @version 1.5.0 : 01/2017 : MMa : created
 */
public class TestVisitorsString { // NO_UCD (unused code)

  /**
   * Standard main method.
   *
   * @param args - the command line arguments (none)
   */
  public static void main(final String args[]) {
    final JTB jtb = new JTB();
    String s;
    System.out.println("pattern : " + VD_MUL_PATT.pattern());
    spc = "";
    s = "Vis,void,A";
    display(s);
    System.out.println("extract : " + jtb.jopt.createVisitorsList(s));
    spc = "";
    s = "Vis,void,A,int";
    display(s);
    System.out.println("extract : " + jtb.jopt.createVisitorsList(s));
    spc = "";
    s = "Vis,R[],None";
    display(s);
    System.out.println("extract : " + jtb.jopt.createVisitorsList(s));
    spc = "";
    s = "Vis,java.lang.System,A,boolean...";
    display(s);
    System.out.println("extract : " + jtb.jopt.createVisitorsList(s));
    spc = "";
    s = "Vis,java.util.List<String>,A,boolean...";
    display(s);
    System.out.println("extract : " + jtb.jopt.createVisitorsList(s));
    spc = "";
    s = "Vis,EDU.purdue.jtb.misc.ClassInfo[],EDU.purdue.jtb.misc.VarInfo,A,boolean...";
    display(s);
    System.out.println("extract : " + jtb.jopt.createVisitorsList(s));
    spc = "";
    s = "Vis1,void,None;Vis2,void,A[],int,short...";
    display(s);
    System.out.println("extract : " + jtb.jopt.createVisitorsList(s));
    spc = "";
    s = "Void,void,None;Ret,R,None;Int,int,None";
    display(s);
    System.out.println("extract : " + jtb.jopt.createVisitorsList(s));
    spc = "";
  }

  /** Indentation string */
  static String spc = "";

  /**
   * Parses a specification string and displays its elements.
   *
   * @param aStr - the string to be displayed
   */
  private static void display(final String aStr) {
    final Matcher mm = VD_MUL_PATT.matcher(aStr);
    final boolean mb = mm.matches();
    final int mc = mm.groupCount(); // should be 7
    System.out.println();
    System.out.println(spc + "\"" + aStr + "\"" + (mb //
        ? " : matched" //
        : " : not matched" //
    ) + ", groupCount = " + mc);
    if (mb) {
      for (int i = 0; i <= mc; i++) {
        System.out.print(spc + i + " : <" + mm.group(i) + (i < mc //
            ? ">, " //
            : ">" //
        ));
      }
      System.out.println();
      String a = mm.group(mc - 1);
      while (a != null) {
        final Matcher am = VD_ARG_PATT.matcher(a);
        final boolean ab = am.matches();
        final int ac = am.groupCount(); // should be 3
        System.out.println(spc + ", \"" + a + "\"" + (ab //
            ? " : matched" //
            : " : not matched" //
        ) + ", groupCount = " + ac);
        if (ab) {
          for (int j = 0; j <= ac; j++) {
            System.out.print(spc + j + " : <" + am.group(j) + (j < ac //
                ? ">, " //
                : ">" //
            ));
          }
          System.out.println();
          a = am.group(ac);
        } else {
          break;
        }
      }
      final String t = mm.group(mc);
      if (t != null) {
        spc += "; ";
        display(t);
      }
    }
  }

}

package EDU.purdue.jtb.generate;

import static EDU.purdue.jtb.common.Constants.BRLEN;
import static EDU.purdue.jtb.common.Constants.BRLS;
import static EDU.purdue.jtb.common.Constants.BRLSLEN;
import static EDU.purdue.jtb.common.Constants.DEBUG_COMMENT;
import static EDU.purdue.jtb.common.Constants.LS;
import static EDU.purdue.jtb.common.Constants.SERIALVERSIONUID;
import static EDU.purdue.jtb.common.Constants.genArguVar;
import static EDU.purdue.jtb.common.Constants.genDepthLevelVar;
import static EDU.purdue.jtb.common.Constants.genNodeVar;
import static EDU.purdue.jtb.common.Constants.genVisVar;
import static EDU.purdue.jtb.common.Constants.iNode;
import static EDU.purdue.jtb.common.Constants.nodeToken;
import EDU.purdue.jtb.analyse.GlobalDataBuilder;
import EDU.purdue.jtb.common.JTBOptions;
import EDU.purdue.jtb.common.Spacing;
import EDU.purdue.jtb.common.UserClassInfo;
import EDU.purdue.jtb.common.UserClassInfo.CommentData;
import EDU.purdue.jtb.common.UserClassInfo.CommentLineData;
import EDU.purdue.jtb.common.VisitorInfo;
import EDU.purdue.jtb.common.VisitorInfo.ArgumentInfo;

/**
 * Class {@link CommonCodeGenerator} contains methods to generate common pieces of code (fields and methods).
 * It delegates printing comments to {@link CommentsPrinter} (which could be an inner class). CODEJAVA
 * <p>
 * Class maintains a thread-local state (on its {@link CommentsPrinter} visitor), and can to be run in
 * parallel threads (on different generated classes).
 * </p>
 * TESTCASE some to add
 *
 * @author Marc Mazas
 * @version 1.5.0 : 01-03/2017 : MMa : created from refactoring of UserFilesGenerator and BaseNodesGenerator
 * @version 1.5.1 : 07/2023 : MMa : fixed issue on Token import<br>
 *          1.5.1 : 08/2023 : MMa : editing changes for coverage analysis; changes due to the NodeToken
 *          replacement by Token
 */
public class CommonCodeGenerator {
  
  /** The {@link GlobalDataBuilder} visitor */
  protected final GlobalDataBuilder          gdbv;
  /** The {@link CommentsPrinter} visitor */
  private final ThreadLocal<CommentsPrinter> tlcpv;
  /** The global JTB options (not thread safe but used only in read-access) */
  final JTBOptions                           jopt;
  
  /**
   * Constructor. s
   *
   * @param aGdbv - the {@link GlobalDataBuilder} visitor
   */
  public CommonCodeGenerator(final GlobalDataBuilder aGdbv) {
    gdbv = aGdbv;
    jopt = aGdbv.jopt;
    tlcpv = ThreadLocal.withInitial(() -> new CommentsPrinter(aGdbv, this));
  }
  
  /**
   * Generates the serial version UID member.
   *
   * @param aSb - a buffer to append to (must not be null)
   */
  void genSerialUIDDeclaration(final StringBuilder aSb) {
    if (jopt.javaDocComments) {
      aSb.append("  /** The serial version UID */").append(LS);
    }
    aSb.append("  private static final long serialVersionUID = ").append(SERIALVERSIONUID).append("L;")
        .append(LS).append(LS);
  }
  
  /**
   * Generates a comment header for visitors accept methods.
   *
   * @param aSb - a buffer to append to (must be non null)
   * @param aVisitorsStr - the visitors specification
   */
  static void cmtHeaderAccept(final StringBuilder aSb, final String aVisitorsStr) {
    aSb.append("  /*").append(LS);
    aSb.append("   * Visitors accept methods (no -novis option");
    if (aVisitorsStr == null) {
      aSb.append(", no visitors specification");
    } else {
      aSb.append(", visitors specification : " + aVisitorsStr);
    }
    aSb.append(")").append(LS);
    aSb.append("   */").append(LS).append(LS);
  }
  
  /**
   * Generates a comment header for parent pointer getter and setter methods.
   *
   * @param aSb - a buffer to append to (must be non null)
   */
  private static void cmtHeaderParentPointer(final StringBuilder aSb) {
    aSb.append("  /*").append(LS);
    aSb.append("   * Parent pointer getter and setter (-pp option)").append(LS);
    aSb.append("   */").append(LS).append(LS);
  }
  
  /**
   * Generates a comment header for children methods.
   *
   * @param aSb - a buffer to append to (must be non null)
   */
  static void cmtHeaderChildrenMethods(final StringBuilder aSb) {
    aSb.append("  /*").append(LS);
    aSb.append("   * Children methods (-chm option)").append(LS);
    aSb.append("   */").append(LS).append(LS);
  }
  
  /**
   * Generates the list of all children field.
   *
   * @param aSb - a buffer to append to (must be non null)
   */
  void listAllChildren(final StringBuilder aSb) {
    if (jopt.javaDocComments) {
      aSb.append("  /** The list of all direct children (base + user nodes) */").append(LS);
    }
    aSb.append("  private List<INode> lac = null;").append(LS).append(LS);
  }
  
  /**
   * Generates the number of base nodes children field.
   *
   * @param aSb - a buffer to append to (must be non null)
   */
  void nbBaseChildren(final StringBuilder aSb) {
    if (jopt.javaDocComments) {
      aSb.append("  /** The number of direct base nodes children */").append(LS);
    }
    aSb.append("  private int nbLbc = -1;").append(LS).append(LS);
  }
  
  /**
   * Generates the list of base nodes children field.
   *
   * @param aSb - a buffer to append to (must be non null)
   */
  void listBaseChildren(final StringBuilder aSb) {
    if (jopt.javaDocComments) {
      aSb.append("  /** The list of direct base nodes children */").append(LS);
    }
    aSb.append("  private List<INode> lbc = null;").append(LS).append(LS);
  }
  
  /**
   * Generates the number of user nodes children field.
   *
   * @param aSb - a buffer to append to (must be non null)
   */
  void nbUserChildren(final StringBuilder aSb) {
    if (jopt.javaDocComments) {
      aSb.append("  /** The number of direct user nodes children */").append(LS);
    }
    aSb.append("  private int nbLuc = -1;").append(LS).append(LS);
  }
  
  /**
   * Generates the list of user nodes children field.
   *
   * @param aSb - a buffer to append to (must be non null)
   */
  void listUserChildren(final StringBuilder aSb) {
    if (jopt.javaDocComments) {
      aSb.append("  /** The list of direct user nodes children */").append(LS);
    }
    aSb.append("  private List<INode> luc = null;").append(LS).append(LS);
  }
  
  /**
   * Generates an always true isBaseNode() method (implementation).
   *
   * @param aSb - a buffer to append to (must be non null)
   */
  void trueBaseNode(final StringBuilder aSb) {
    if (jopt.javaDocComments) {
      isBaseNodeComment(aSb, "(always true : the node is a base node)");
    }
    aSb.append("  @Override").append(LS);
    aSb.append("  public boolean isBaseNode() {").append(LS);
    aSb.append("    return true;").append(LS);
    aSb.append("  }").append(LS).append(LS);
  }
  
  /**
   * Generates an always false isBaseNode() method (implementation).
   *
   * @param aSb - a buffer to append to (must be non null)
   */
  void falseBaseNode(final StringBuilder aSb) {
    if (jopt.javaDocComments) {
      isBaseNodeComment(aSb, "(always false : the node is a user node)");
    }
    aSb.append("  @Override").append(LS);
    aSb.append("  public boolean isBaseNode() {").append(LS);
    aSb.append("    return false;").append(LS);
    aSb.append("  }").append(LS).append(LS);
  }
  
  /**
   * Generates the javadoc comment for the getXxxxAllChilren() method.
   *
   * @param aSb - a buffer to append to (must be non null)
   * @param aNLStr - the "number" or "list" string
   * @param aEndStr - a terminating string
   */
  static void childrenAllComment(final StringBuilder aSb, final String aNLStr, final String aEndStr) {
    aSb.append("  /**").append(LS);
    aSb.append("   * @return the ").append(aNLStr).append(" of all direct children (base + user nodes)");
    if (aEndStr != null) {
      aSb.append(' ').append(aEndStr);
    }
    aSb.append(LS);
    aSb.append("   */").append(LS);
  }
  
  /**
   * Generates the javadoc comment for the getXxxxBaseChilren() and getXxxxUserChilren() methods.
   *
   * @param aSb - a buffer to append to (must be non null)
   * @param aNLStr - the "number" or "list" string
   * @param aBUStr - the "base" or "user" string
   * @param aEndStr - a terminating string
   */
  static void childrenBaseorUserComment(final StringBuilder aSb, final String aNLStr, final String aBUStr,
      final String aEndStr) {
    aSb.append("  /**").append(LS);
    aSb.append("   * @return the ").append(aNLStr).append(" of direct ").append(aBUStr)
        .append(" nodes children");
    if (aEndStr != null) {
      aSb.append(' ').append(aEndStr);
    }
    aSb.append(LS);
    aSb.append("   */").append(LS);
  }
  
  /**
   * Generates the javadoc comment for the isBaseNode() method.
   *
   * @param aSb - a buffer to append to (must be non null)
   * @param aEndStr - a terminating string
   */
  static void isBaseNodeComment(final StringBuilder aSb, final String aEndStr) {
    aSb.append("  /**").append(LS);
    aSb.append("   * @return true if the node is a base node, false otherwise");
    if (aEndStr != null) {
      aSb.append(' ').append(aEndStr);
    }
    aSb.append(LS);
    aSb.append("   */").append(LS);
  }
  
  /**
   * Generates the parameters comment (same for all visit methods).<br>
   *
   * @param aSb - the buffer to output into (must be non null)
   * @param aSpc - the indentation
   * @param aVi - a VisitorInfo defining the visitor to generate
   */
  static void genParametersComment(final StringBuilder aSb, final Spacing aSpc, final VisitorInfo aVi) {
    aSb.append(aSpc.spc).append(" *").append(LS);
    aSb.append(aSpc.spc).append(" * @param ").append(genNodeVar).append(" - the node to visit").append(LS);
    for (int p = 0; p < aVi.argInfoList.size(); p++) {
      aSb.append(aSpc.spc).append(" * @param ").append(genArguVar).append(p == 0 ? "" : String.valueOf(p))
          .append(" - the user argument ").append(p).append(LS);
    }
    if (!aVi.retInfo.isVoid) {
      aSb.append(aSpc.spc).append(" * @return the user return information").append(LS);
    }
    aSb.append(aSpc.spc).append(" */").append(LS);
  }
  
  /**
   * Generates javadoc comments for the type parameters.
   *
   * @param aSb - the buffer to output into (must be non null)
   * @param aVi - a VisitorInfo defining the visitor to generate
   */
  static void genTypeParametersComment(final StringBuilder aSb, final VisitorInfo aVi) {
    if (aVi.retInfo.isTypeParameter) {
      aSb.append(" * @param <").append(aVi.retInfo.type).append("> - The return type parameter").append(LS);
    }
    int p = 0;
    for (final ArgumentInfo ai : aVi.argInfoList) {
      if (ai.isTypeParameter) {
        aSb.append(" * @param <").append(ai.type).append("> - The argument ").append(p++)
            .append(" type parameter").append(LS);
      }
    }
  }
  
  /**
   * Generates the visitors classes accept methods (implementations).
   *
   * @param aSb - a buffer to append to (must be non null)
   */
  void classesAcceptMethods(final StringBuilder aSb) {
    if (!jopt.noVisitors) {
      cmtHeaderAccept(aSb, jopt.visitorsStr);
      for (final VisitorInfo vi : jopt.visitorsList) {
        if (jopt.javaDocComments) {
          acceptComment(aSb, vi);
        }
        aSb.append("  @Override").append(LS);
        aSb.append("  public ").append(vi.classTypeParameters)
            .append(vi.classTypeParameters.length() == 0 ? "" : " ").append(vi.retInfo.fullType)
            .append(" accept(final ").append(vi.interfaceName).append(vi.classTypeParameters).append(' ')
            .append(genVisVar).append(vi.userParameters).append(") {").append(LS);
        aSb.append(!vi.retInfo.isVoid ? "    return " : "    ").append(genVisVar).append(".visit(this")
            .append(vi.userArguments).append(");").append(LS);
        aSb.append("  }").append(LS).append(LS);
      }
    }
  }
  
  /**
   * Generates the javadoc comment for an accept method.
   *
   * @param aSb - a buffer to append to (must be non null)
   * @param aVi - a VisitorInfo defining the visitor to generate
   */
  static void acceptComment(final StringBuilder aSb, final VisitorInfo aVi) {
    aSb.append("  /**").append(LS);
    aSb.append("   * Accepts a {@link ").append(aVi.interfaceName).append("} visitor");
    if (!aVi.retInfo.isVoid) {
      if (!aVi.argInfoList.isEmpty()) {
        aSb.append(" with user return and argument data.").append(LS);
      } else {
        aSb.append(" with user return data.").append(LS);
      }
    } else if (!aVi.argInfoList.isEmpty()) {
      aSb.append(" with user argument data.").append(LS);
    } else {
      aSb.append("} visitor with user return data.").append(LS);
    }
    aSb.append("   *").append(LS);
    if (aVi.retInfo.isTypeParameter) {
      aSb.append("   * @param <").append(aVi.retInfo.type).append("> - the return type parameter").append(LS);
    }
    int p = 0;
    for (final ArgumentInfo ai : aVi.argInfoList) {
      if (ai.isTypeParameter) {
        aSb.append("   * @param <").append(ai.type).append("> - The argument ").append(p++)
            .append(" type parameter").append(LS);
      }
    }
    aSb.append("   * @param ").append(genVisVar).append(" - the visitor").append(LS);
    for (p = 0; p < aVi.argInfoList.size(); p++) {
      aSb.append("   * @param ").append(genArguVar).append(p == 0 ? "" : String.valueOf(p))
          .append(" - the user Argument data").append(LS);
    }
    if (!aVi.retInfo.isVoid) {
      aSb.append("   * @return the user Return data").append(LS);
    }
    aSb.append("   */").append(LS);
  }
  
  /**
   * Generates JavaCC Token import.
   *
   * @param aSb - a buffer to append to (must be non null)
   */
  void javaCCTokenImport(final StringBuilder aSb) {
    if (gdbv.packageName != null) {
      aSb.append("import ").append(gdbv.packageName).append(".Token").append(';').append(LS);
      aSb.append(LS);
    } else if (jopt.grammarPackageName != null) {
      aSb.append("import ").append(jopt.grammarPackageName).append(".Token").append(';').append(LS);
      aSb.append(LS);
      // } else {
      // aSb.append("import Token").append(';').append(LS);
      // aSb.append(LS);
    }
  }
  
  /**
   * Generates visitor classes & interfaces imports.
   *
   * @param aSb - a buffer to append to (must be non null)
   */
  void interfaceVisitorsImports(final StringBuilder aSb) {
    if (jopt.visitorsPackageName != null && !jopt.noVisitors) {
      for (final VisitorInfo vi : jopt.visitorsList) {
        aSb.append("import ").append(jopt.visitorsPackageName).append(".").append(vi.interfaceName)
            .append(';').append(LS);
      }
    }
    aSb.append(LS);
  }
  
  /**
   * Generates the Token import.
   *
   * @param aSb - a buffer to append to (must be non null)
   */
  void tokenImport(final StringBuilder aSb) {
    if (jopt.grammarPackageName != null) {
      aSb.append("import ").append(jopt.grammarPackageName).append(".").append(nodeToken).append(";")
          .append(LS);
    }
  }
  
  /**
   * Generates the Token import.
   *
   * @param aSb - a buffer to append to (must be non null)
   */
  void inodeImport(final StringBuilder aSb) {
    if (jopt.grammarPackageName != null) {
      aSb.append("import ").append(jopt.nodesPackageName).append(".").append(iNode).append(";").append(LS);
    }
  }
  
  /**
   * Generates parent pointer field.
   *
   * @param aSb - a buffer to append to (will be allocated if null)
   */
  void parentPointerDeclaration(final StringBuilder aSb) {
    if (jopt.parentPointer) {
      if (jopt.javaDocComments) {
        aSb.append("  /** The parent node */").append(LS);
      }
      aSb.append("  private ").append(iNode).append(" parent;").append(LS).append(LS);
    }
  }
  
  /**
   * Generates parent pointer getter and setter methods (implementations in classes).
   *
   * @param aSb - a buffer to append to (will be allocated if null)
   */
  void parentPointerGetterSetterImpl(final StringBuilder aSb) {
    if (jopt.parentPointer) {
      cmtHeaderParentPointer(aSb);
      
      if (jopt.javaDocComments) {
        aSb.append("  /**").append(LS);
        aSb.append("   * Gets the parent node.").append(LS);
        aSb.append("   *").append(LS);
        aSb.append("   * @return the parent node").append(LS);
        aSb.append("   */").append(LS);
      }
      aSb.append("  @Override").append(LS);
      aSb.append("  public ").append(iNode).append(" getParent() {").append(LS);
      aSb.append("    return parent;").append(LS);
      aSb.append("  }").append(LS).append(LS);
      
      if (jopt.javaDocComments) {
        aSb.append("  /**").append(LS);
        aSb.append("   * Sets the parent node.").append(LS);
        aSb.append("   *").append(LS);
        aSb.append("   * @param ").append(genNodeVar).append(" - the parent node").append(LS);
        aSb.append("   */").append(LS);
      }
      aSb.append("  @Override").append(LS);
      aSb.append("  public void setParent(final ").append(iNode).append(' ').append(genNodeVar).append(") {")
          .append(LS);
      aSb.append("    parent = ").append(genNodeVar).append(";").append(LS);
      aSb.append("  }").append(LS).append(LS);
    }
  }
  
  /**
   * Generates parent pointer getter and setter methods (declaration in interface).
   *
   * @param aSb - a buffer to append to (must be non null)
   */
  void parentPointerGetterSetterDecl(final StringBuilder aSb) {
    if (jopt.parentPointer) {
      cmtHeaderParentPointer(aSb);
      
      if (jopt.javaDocComments) {
        aSb.append("  /**").append(LS);
        aSb.append("   * Gets the parent node.").append(LS);
        aSb.append("   *").append(LS);
        aSb.append("   * @return the parent node").append(LS);
        aSb.append("   */").append(LS);
      }
      aSb.append("  public ").append(iNode).append(" getParent();").append(LS).append(LS);
      
      if (jopt.javaDocComments) {
        aSb.append("  /**").append(LS);
        aSb.append("   * Sets the parent node. (It is the responsibility of each implementing class")
            .append(LS);
        aSb.append("   * to call setParent() on each of its child nodes.)").append(LS);
        aSb.append("   *").append(LS);
        aSb.append("   * @param ").append(genNodeVar).append(" - the parent node").append(LS);
        aSb.append("   */").append(LS);
      }
      aSb.append("  public void setParent(final ").append(iNode).append(' ').append(genNodeVar).append(");")
          .append(LS).append(LS);
    }
  }
  
  /**
   * Generates parent pointer set call.
   *
   * @param aSb - a buffer to append to (must be non null)
   * @param aNode - the child node
   */
  void parentPointerSetCall(final StringBuilder aSb, final String aNode) {
    if (jopt.parentPointer) {
      aSb.append("    if (").append(aNode).append(" != null)").append(LS);
      aSb.append("      ").append(aNode).append(".setParent(this);").append(LS);
    }
  }
  
  /**
   * Append to a given buffer a set of javadoc lines comments (*) showing a BNF description of the current
   * class. They include the debug comments (after the break tag) if they have been produced, and the fields
   * hash signature.
   *
   * @param aSb - the buffer to append the BNF description to (must be non null)
   * @param aSpc - the indentation
   * @param aUci - the user class information
   */
  void fmtAllFieldsJavadocCmts(final StringBuilder aSb, final Spacing aSpc, final UserClassInfo aUci) {
    if (DEBUG_COMMENT) {
      System.out.flush();
      System.err.println("fmtAllFieldsJavadocCmts on " + aUci.className);
      System.err.flush();
    }
    genCommentsData(aUci);
    if (aUci.fieldCmts != null) {
      if (aSpc.indentLevel == 1) {
        // for visit methods that have an indentation of 1, store the result
        StringBuilder sb = aUci.visitFieldCmtsSb;
        if (sb == null) {
          int len = 0;
          for (final CommentData fieldCmt : aUci.fieldCmts) {
            for (final CommentLineData line : fieldCmt.lines) {
              // 3 is length of " * "
              len += aSpc.spc.length() + 3 + line.bare.length() + BRLEN;
              if (line.debug != null) {
                len += line.debug.length();
              }
              len += BRLSLEN;
            }
          }
          sb = new StringBuilder(len);
          for (final CommentData fieldCmt : aUci.fieldCmts) {
            for (final CommentLineData line : fieldCmt.lines) {
              sb.append(aSpc.spc).append(" * ").append(line.bare);
              if (line.debug != null) {
                sb.append(line.debug);
              }
              sb.append(BRLS);
            }
          }
        }
        aUci.visitFieldCmtsSb = sb;
        aSb.append(sb);
      } else {
        // other cases
        for (final CommentData fieldCmt : aUci.fieldCmts) {
          for (final CommentLineData line : fieldCmt.lines) {
            aSb.append(aSpc.spc).append(" * ").append(line.bare);
            if (line.debug != null) {
              aSb.append(line.debug);
            }
            aSb.append(BRLS);
          }
        }
      }
    }
    aSb.append(aSpc.spc).append(" * s: ").append(aUci.fieldsHashSig).append(BRLS);
    return;
  }
  
  /**
   * Append to a given buffer a java code line comment (//) showing a BNF description of the current field. It
   * does not include the debug comments even if they have been produced.
   *
   * @param aSb - the buffer to append the BNF description to (must be non null)
   * @param aSpc - the indentation
   * @param i - the field index
   * @param aStr - an additional comment
   * @param aUci - the user class information
   */
  @SuppressWarnings("static-method")
  void fmtOneJavaCodeFieldCmt(final StringBuilder aSb, final Spacing aSpc, final int i, final String aStr,
      final UserClassInfo aUci) {
    // can this not already have been done in fmtAllFieldsJavadocCmts?
    // if (DEBUG_COMMENT) {
    // System.out.flush();
    // System.err.println("fmtOneJavaCodeFieldCmt on " + aUci.className);
    // System.err.flush();
    // }
    // genCommentsData(aUci);
    if (aUci.fieldCmts == null) {
      return;
    }
    final CommentData fieldCmt = aUci.fieldCmts.get(i);
    for (final CommentLineData line : fieldCmt.lines) {
      aSb.append(aSpc.spc).append("// ").append(line.bare);
      if (DEBUG_COMMENT) {
        if (aStr != null) {
          aSb.append(" ; ").append(aStr);
        }
      }
      aSb.append(LS);
    }
    return;
  }
  
  /**
   * Append to a given buffer a java code (//) comment showing a BNF description of the current part. They do
   * not include the debug comments even if they have been produced.
   *
   * @param aSb - the buffer to append the BNF description to (must be non null)
   * @param aSpc - the indentation
   * @param aIx - the sub comment index
   * @param aStr - an additional comment
   * @param aUci - the user class information
   */
  static void fmtOneJavaCodeSubCmt(final StringBuilder aSb, final Spacing aSpc, final int aIx,
      final String aStr, final UserClassInfo aUci) {
    if (aUci.fieldSubCmts == null) {
      return;
    }
    if (aIx >= aUci.fieldSubCmts.size()) {
      aSb.append(aSpc.spc).append("// invalid sub comment index (").append(aIx).append("), size = ")
          .append(aUci.fieldSubCmts.size());
      if (aStr != null) {
        aSb.append(" ; ").append(aStr);
      }
      aSb.append(LS);
    } else {
      final CommentData subCmt = aUci.fieldSubCmts.get(aIx);
      for (final CommentLineData line : subCmt.lines) {
        aSb.append(aSpc.spc).append("//").append(line.bare);
        if (DEBUG_COMMENT) {
          if (aStr != null) {
            aSb.append(" ; ").append(aStr);
          }
        }
        aSb.append(LS);
      }
    }
    return;
  }
  
  /**
   * Generates if not already done the comments trees by delegating this to the {@link CommentsPrinter}
   * visitor.
   *
   * @param aUci - the user class information
   */
  void genCommentsData(final UserClassInfo aUci) {
    if (aUci.fieldCmts == null) {
      tlcpv.get().genCommentsData(aUci);
    }
  }
  
  /**
   * Output code to decrease the depth level.
   *
   * @param aSb - the buffer to append to (must be non null)
   * @param aSpc - the indentation
   */
  static void increaseDepthLevel(final StringBuilder aSb, final Spacing aSpc) {
    aSb.append(aSpc.spc).append("++").append(genDepthLevelVar).append(';').append(LS);
  }
  
  /**
   * Output code to increase the depth level.
   *
   * @param aSb - the buffer to append to (must be non null)
   * @param aSpc - the indentation
   */
  static void decreaseDepthLevel(final StringBuilder aSb, final Spacing aSpc) {
    aSb.append(aSpc.spc).append("--").append(genDepthLevelVar).append(';').append(LS);
  }
  
}

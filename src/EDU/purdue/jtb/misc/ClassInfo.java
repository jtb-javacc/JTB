package EDU.purdue.jtb.misc;

import java.util.ArrayList;
import java.util.List;

import EDU.purdue.jtb.syntaxtree.INode;


@SuppressWarnings("javadoc")
public interface ClassInfo {
  void addField(final String aFT, final String aFN);
  void addField(final String aFT, final String aFN, final String aFI, final String aFEC);
  void fmt1JavacodeFieldCmt(final StringBuilder aSb, final Spacing aSpc, final int i);
  void fmt1JavacodeSubCmt(final StringBuilder aSb, final Spacing aSpc, final int i);
  void fmtFieldsJavadocCmts(final StringBuilder aSb, final Spacing aSpc);
  StringBuilder genClassString(final Spacing aSpc, IVisitorClass intf);
  String getClassName();
  INode getASTECNode();
  List<String> getFieldTypes();
  List<String>getFieldNames();
  List<String> getFieldInitializers();
  List<String> getFieldEUTCFCodes();
  List<CommentData> getFieldCmts();
  List<CommentData> getSubCmts();
}

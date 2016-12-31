package EDU.purdue.jtb.misc;



@SuppressWarnings("javadoc")
public interface BaseClasses {
  String begArgList(boolean b);
  void classesAcceptMethods(StringBuilder sb);
  String endArgList(boolean b);
  String extendsClause();
  void genAcceptRetArguComment(StringBuilder sb);
  void genAcceptRetComment(StringBuilder sb);
  void genAcceptVoidArguComment(StringBuilder sb);
  void genAcceptVoidComment(StringBuilder sb);
  String genClassParamType(boolean b1, boolean b2);
  StringBuilder genINodeInterface(StringBuilder sb, NodeClass node);
  StringBuilder genINodeListInterface(StringBuilder sb, NodeClass ndoe);
  StringBuilder genNodeChoiceClass(StringBuilder sb, NodeClass node);
  StringBuilder genNodeListClass(StringBuilder sb, NodeClass node);
  StringBuilder genNodeListOptClass(StringBuilder sb, NodeClass node);
  StringBuilder genNodeOptClass(StringBuilder sb, NodeClass node);
  StringBuilder genNodeSeqClass(StringBuilder sb, NodeClass node);
  StringBuilder genNodeTCFClass(StringBuilder sb, NodeClass node);
  StringBuilder genNodeTokenClass(StringBuilder sb, NodeClass node);
  StringBuilder genRetArguVisitNodeChoice(StringBuilder sb, IVisitorClass intf);
  StringBuilder genRetArguVisitNodeList(StringBuilder sb, IVisitorClass intf);
  StringBuilder genRetArguVisitNodeListOpt(StringBuilder sb, IVisitorClass intf);
  StringBuilder genRetArguVisitNodeOpt(StringBuilder sb, IVisitorClass intf);
  StringBuilder genRetArguVisitNodeSeq(StringBuilder sb, IVisitorClass intf);
  StringBuilder genRetArguVisitNodeTCF(StringBuilder sb, IVisitorClass intf);
  StringBuilder genRetArguVisitNodeToken(StringBuilder sb, IVisitorClass intf);
  StringBuilder genRetVisitNodeChoice(StringBuilder sb);
  StringBuilder genRetVisitNodeList(StringBuilder sb);
  StringBuilder genRetVisitNodeListOpt(StringBuilder sb);
  StringBuilder genRetVisitNodeOpt(StringBuilder sb);
  StringBuilder genRetVisitNodeSeq(StringBuilder sb);
  StringBuilder genRetVisitNodeTCF(StringBuilder sb);
  StringBuilder genRetVisitNodeToken(StringBuilder sb);
  void genVisitRetArguComment(StringBuilder sb);
  void genVisitRetComment(StringBuilder sb);
  void genVisitVoidArguComment(StringBuilder sb);
  void genVisitVoidComment(StringBuilder sb);
  StringBuilder genVoidArguVisitNodeChoice(StringBuilder sb, IVisitorClass intf);
  StringBuilder genVoidArguVisitNodeList(StringBuilder sb, IVisitorClass intf);
  StringBuilder genVoidArguVisitNodeListOpt(StringBuilder sb, IVisitorClass intf);
  StringBuilder genVoidArguVisitNodeOpt(StringBuilder sb, IVisitorClass intf);
  StringBuilder genVoidArguVisitNodeSeq(StringBuilder sb, IVisitorClass intf);
  StringBuilder genVoidArguVisitNodeTCF(StringBuilder sb, IVisitorClass intf);
  StringBuilder genVoidArguVisitNodeToken(StringBuilder sb, IVisitorClass intf);
  StringBuilder genVoidVisitNodeChoice(StringBuilder sb);
  StringBuilder genVoidVisitNodeList(StringBuilder sb);
  StringBuilder genVoidVisitNodeListOpt(StringBuilder sb);
  StringBuilder genVoidVisitNodeOpt(StringBuilder sb);
  StringBuilder genVoidVisitNodeSeq(StringBuilder sb);
  StringBuilder genVoidVisitNodeTCF(StringBuilder sb);
  StringBuilder genVoidVisitNodeToken(StringBuilder sb);
  void interfacesAcceptMethods(StringBuilder sb);
  void lineSeparatorDeclaration(StringBuilder sb);
  void listMethods(StringBuilder sb);
  void genFileHeader(StringBuilder sb, NodeClass node);
  StringBuilder parentPointerDeclaration(StringBuilder sb);
  StringBuilder parentPointerGetterSetter(StringBuilder sb);
  void parentPointerSetCall(StringBuilder sb);
  void serialUIDDeclaration(StringBuilder sb);
}

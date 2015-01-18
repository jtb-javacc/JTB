package EDU.purdue.jtb.misc;


@SuppressWarnings("javadoc")
public interface NodeClass {
  String getName();
  String getQualifiedName();
  String getParamType(IVisitorClass iVisitor);
  String getPrefix(IVisitorClass iVisitor);
  void genFileHeader(StringBuilder sb);

}

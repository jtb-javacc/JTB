package EDU.purdue.jtb.misc;


@SuppressWarnings("javadoc")
abstract public class AbstractIVisitorClassForCpp extends AbstractIVisitorClass {
  @Override
  public String getDefine() {
    return Globals.vstNamespace + "_" + getClassName() + "_h_";
  }
  @Override
  public String getIncludePath() {
    return "../" + Globals.visitorsDirName + "/" + getClassName();
  }
  @Override
  public String getASTdcl() {
    return Globals.astNodesDirName + "/ASTdcl.h";
  }
  @Override
  public String getVisitorFile() {
    return getClassName() + ".h";
  }

}

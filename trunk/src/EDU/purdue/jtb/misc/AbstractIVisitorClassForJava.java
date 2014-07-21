package EDU.purdue.jtb.misc;

import static EDU.purdue.jtb.misc.Globals.genArguType;
import static EDU.purdue.jtb.misc.Globals.genRetType;

@SuppressWarnings("javadoc")
abstract public class AbstractIVisitorClassForJava extends AbstractIVisitorClass {

  @Override
  public String getDefine() {
    return "";
  }

  @Override
  public String getIncludePath() {
    return null;
  }

  @Override
  public String getASTdcl() {
    return null;
  }

  @Override
  public String getVisitorFile() {
    return null;
  }

  @Override
  public String getClassPrefix() {
    return null;
  }

  @Override
  public String getClassParamType() {
    return null;
  }

  @Override
  public String getOutfileName() {
    return getClassName() + ".java";
  }
}

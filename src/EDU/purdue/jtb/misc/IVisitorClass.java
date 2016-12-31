package EDU.purdue.jtb.misc;


/**
 * @author FrancisANDRE
 *
 */
@SuppressWarnings("javadoc")
public interface IVisitorClass {
  String getClassParamType();
  String getClassName();
  String getClassPrefix();
  String getVisitorFile();
  String getDefine();
  String getIncludePath();
  String getASTdcl();
  String getOutfileName();
}

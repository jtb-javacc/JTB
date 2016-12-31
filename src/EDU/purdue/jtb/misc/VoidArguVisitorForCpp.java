package EDU.purdue.jtb.misc;


@SuppressWarnings("javadoc")
public class VoidArguVisitorForCpp extends VoidArguVisitor {

  @Override
  public String getClassName() {
    return "DepthFirstVoidArguVisitor";
  }
  @Override
  public String getOutfileName() {
    return getClassName() + ".h";
  }

}

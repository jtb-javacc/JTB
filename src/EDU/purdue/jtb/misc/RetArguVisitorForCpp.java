package EDU.purdue.jtb.misc;


@SuppressWarnings("javadoc")
public class RetArguVisitorForCpp extends RetArguVisitor{

  @Override
  public String getOutfileName() {
    return getClassName() + ".h";
  }


}

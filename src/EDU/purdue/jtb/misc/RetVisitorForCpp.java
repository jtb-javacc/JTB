package EDU.purdue.jtb.misc;


@SuppressWarnings("javadoc")
public class RetVisitorForCpp extends RetVisitor {

  @Override
  public String getOutfileName() {
    return getClassName() + ".h";
  }

}

package EDU.purdue.jtb.misc;


@SuppressWarnings("javadoc")
public class VoidVisitorForCpp extends VoidVisitor {

  @Override
  public String getOutfileName() {
    return getClassName() + ".h";
  }

}

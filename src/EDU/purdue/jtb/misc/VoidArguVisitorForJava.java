package EDU.purdue.jtb.misc;


@SuppressWarnings("javadoc")
public class VoidArguVisitorForJava extends VoidArguVisitor {

  @Override
  public String getOutfileName() {
    return getClassName() + ".java";
  }

}

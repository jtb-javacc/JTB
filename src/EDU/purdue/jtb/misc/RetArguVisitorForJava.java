package EDU.purdue.jtb.misc;


@SuppressWarnings("javadoc")
public class RetArguVisitorForJava extends RetArguVisitor{

  @Override
  public String getOutfileName() {
    return getClassName() + ".java";
  }

}

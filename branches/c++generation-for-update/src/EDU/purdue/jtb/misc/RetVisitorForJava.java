package EDU.purdue.jtb.misc;


@SuppressWarnings("javadoc")
public class RetVisitorForJava extends RetVisitor {

  @Override
  public String getOutfileName() {
    return getClassName() + ".java";
  }

}

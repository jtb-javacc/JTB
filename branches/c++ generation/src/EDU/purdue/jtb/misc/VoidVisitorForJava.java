package EDU.purdue.jtb.misc;


@SuppressWarnings("javadoc")
public class VoidVisitorForJava extends VoidVisitor {

  @Override
  public String getOutfileName() {
    return getClassName() + ".java";
  }

}
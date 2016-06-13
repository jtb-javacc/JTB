package EDU.purdue.jtb.misc;


@SuppressWarnings("javadoc")
abstract public class AbstractIVisitorClass implements IVisitorClass {
  @Override
  public String toString() {
    return getClassName();
  }

}

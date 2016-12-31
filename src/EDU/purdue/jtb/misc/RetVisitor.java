package EDU.purdue.jtb.misc;


@SuppressWarnings("javadoc")
abstract  class RetVisitor implements VisitorClass {

  @Override
  public String getClassName() {
    return "DepthFirstRetVisitor";
  }
  @Override
  public String toString() {
    return getClassName();
  }

  @Override
  public IVisitorClass getInterface() {
    return Globals.iRetVisitor;
  }

}

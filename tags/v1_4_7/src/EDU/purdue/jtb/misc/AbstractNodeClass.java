package EDU.purdue.jtb.misc;


@SuppressWarnings("javadoc")
abstract class AbstractNodeClass implements NodeClass{

  @Override
  public String toString() {
    return getName();
  }
  
  @Override
  public String getParamType(IVisitorClass iVisitor) {
    return getName() + iVisitor.getClassParamType();
  }

  @Override
  public String getPrefix(IVisitorClass iVisitor) {
    return iVisitor.getClassPrefix();
  }
  
  @Override
  public String getQualifiedName() {
    String qualifiedName = null;
    
    switch(Globals.target) {
      case java:
        qualifiedName =  getName();
        break;
      case cpp:
        qualifiedName =  Globals.astNamespace + "::" + getName() + "<R, A...>";
        break;
    }
    
    return qualifiedName;
  }

}

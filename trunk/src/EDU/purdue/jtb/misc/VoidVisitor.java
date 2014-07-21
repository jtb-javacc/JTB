package EDU.purdue.jtb.misc;


 @SuppressWarnings("javadoc")
abstract class VoidVisitor implements VisitorClass {

   @Override
  public String toString() {
     return getClassName();
   }

  @Override
  public IVisitorClass getInterface() {
    return Globals.iVoidVisitor;
  }
}

package EDU.purdue.jtb.misc;


 @SuppressWarnings("javadoc")
abstract class VoidArguVisitor implements VisitorClass {

   @Override
  public String toString() {
     return getClassName();
   }

  @Override
  public IVisitorClass getInterface() {
    return Globals.iVoidArguVisitor;
  }
}

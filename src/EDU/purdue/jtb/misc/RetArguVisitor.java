package EDU.purdue.jtb.misc;


 @SuppressWarnings("javadoc")
abstract class RetArguVisitor implements VisitorClass {

   @Override
  public String toString() {
     return getClassName();
   }


  @Override
  public IVisitorClass getInterface() {
    return Globals.iRetArguVisitor;
  }
}

package EDU.purdue.jtb.misc;

import static EDU.purdue.jtb.misc.Globals.genArguType;
import static EDU.purdue.jtb.misc.Globals.genArgusType;
import static EDU.purdue.jtb.misc.Globals.varargs;


/**
 * @author FrancisANDRE
 *
 */
@SuppressWarnings("javadoc")
public class IVoidVisitorForJava extends AbstractIVisitorClassForJava {

  @Override
  public String getClassName() {
    return "IVoidVisitor";
  }
  @Override
  public String getClassParamType() {
    return "";
  }
  @Override
  public String getClassPrefix() {
     return "";
  }

}

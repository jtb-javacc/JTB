package EDU.purdue.jtb.misc;

import static EDU.purdue.jtb.misc.Globals.genArguType;
import static EDU.purdue.jtb.misc.Globals.genArgusType;
import static EDU.purdue.jtb.misc.Globals.genRetType;
import static EDU.purdue.jtb.misc.Globals.varargs;


/**
 * @author FrancisANDRE
 *
 */
@SuppressWarnings("javadoc")
public class IRetVisitorForJava extends AbstractIVisitorClassForJava {

  @Override
  public String getClassName() {
    return "IRetVisitor";
  }
  @Override
  public String getClassParamType() {
    return "<".concat(genRetType).concat(">");
  }
  @Override
  public String getClassPrefix() {
     return "<" + genRetType + ">";
  }
}

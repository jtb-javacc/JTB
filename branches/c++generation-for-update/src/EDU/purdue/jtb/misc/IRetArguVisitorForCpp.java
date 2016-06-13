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
public class IRetArguVisitorForCpp extends AbstractIVisitorClassForCpp {

  @Override
  public String getClassParamType() {
    return "<".concat(genRetType).concat(",").concat(varargs ? genArgusType : genArguType).concat(">");
  }
  @Override
  public String getClassName() {
    return "IRetArguVisitor";
  }
  @Override
  public String getClassPrefix() {
     return "template<typename " + genRetType + ", typename... " + genArguType + ">";
  }
}

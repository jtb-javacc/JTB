package EDU.purdue.jtb.misc;

import static EDU.purdue.jtb.misc.Globals.LS;
import static EDU.purdue.jtb.misc.Globals.iRetArguVisitor;


@SuppressWarnings("javadoc")
public class INodeClass extends AbstractNodeClass{

  @Override
  public String getName() {
    return "INode";
  }

  @Override
  public void genFileHeader(StringBuilder sb) {
    sb.append(iRetArguVisitor.getClassPrefix()).append(" ");
    sb.append("class ").append(iRetArguVisitor).append(";").append(LS);
  }


}

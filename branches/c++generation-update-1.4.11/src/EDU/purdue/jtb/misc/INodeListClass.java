package EDU.purdue.jtb.misc;

import static EDU.purdue.jtb.misc.Globals.LS;
import static EDU.purdue.jtb.misc.Globals.iRetArguVisitor;


@SuppressWarnings("javadoc")
public class INodeListClass  extends AbstractNodeClass {

  @Override
  public String getName() {
    return "INodeList";
  }

  @Override
  public void genFileHeader(StringBuilder sb) {
    sb.append("#include ").append("<vector>").append(LS);
    sb.append("#include ").append('"').append("INode.h").append('"').append(LS);
    sb.append(iRetArguVisitor.getClassPrefix()).append(" ");
    sb.append("class ").append(iRetArguVisitor).append(";").append(LS);
    sb.append("using namespace std;").append(LS);
  }

}

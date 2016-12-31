package EDU.purdue.jtb.misc;

import static EDU.purdue.jtb.misc.Globals.LS;


@SuppressWarnings("javadoc")
public class NodeOptionalClass extends AbstractNodeClass {

  @Override
  public String getName() {
    return "NodeOptional";
  }

  @Override
  public void genFileHeader(StringBuilder sb) {
    sb.append("#include ").append("<vector>").append(LS);
    sb.append("#include ").append('"').append("Error.h").append('"').append(LS);
    sb.append("#include ").append('"').append("INode.h").append('"').append(LS);
    sb.append("#include ").append('"').append("IRetArguVisitor.h").append('"').append(LS);
    sb.append("using namespace std;").append(LS);
  }

}

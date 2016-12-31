package EDU.purdue.jtb.misc;

import static EDU.purdue.jtb.misc.Globals.LS;


@SuppressWarnings("javadoc")
public class INodeListOptionalClass  extends AbstractNodeClass {

  @Override
  public String getName() {
    return "INodeListOptional";
  }

  @Override
  public void genFileHeader(StringBuilder sb) {
    sb.append("#include ").append("<vector>").append(LS);
    sb.append("#include ").append('"').append("INode.h").append('"').append(LS);
    sb.append("using namespace std;").append(LS);
  }

}

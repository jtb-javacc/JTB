package EDU.purdue.jtb.misc;

import static EDU.purdue.jtb.misc.Globals.LS;


@SuppressWarnings("javadoc")
public class NodeSequenceClass extends AbstractNodeClass {

  @Override
  public String getName() {
    return "NodeSequence";
  }

  @Override
  public void genFileHeader(StringBuilder sb) {
    sb.append("#include ").append("<vector>").append(LS);
    sb.append("#include ").append('"').append("INodeList.h").append('"').append(LS);
    sb.append("#include ").append('"').append("IRetArguVisitor.h").append('"').append(LS);
    sb.append("using namespace std;").append(LS);
  }

}

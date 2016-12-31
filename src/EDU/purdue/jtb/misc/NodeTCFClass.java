package EDU.purdue.jtb.misc;

import static EDU.purdue.jtb.misc.Globals.LS;


@SuppressWarnings("javadoc")
public class NodeTCFClass extends AbstractNodeClass {

  @Override
  public String getName() {
    return "NodeTCF";
  }

  @Override
  public void genFileHeader(StringBuilder sb) {
    sb.append("#include ").append("<string>").append(LS);
    sb.append("#include ").append('"').append("NodeToken.h").append('"').append(LS);
    sb.append("#include ").append('"').append("IRetArguVisitor.h").append('"').append(LS);
   }

}

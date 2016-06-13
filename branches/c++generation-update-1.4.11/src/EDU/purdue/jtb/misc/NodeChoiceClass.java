package EDU.purdue.jtb.misc;

import static EDU.purdue.jtb.misc.Globals.LS;


@SuppressWarnings("javadoc")
public class NodeChoiceClass extends AbstractNodeClass {

  @Override
  public String getName() {
    return "NodeChoice";
  }

  @Override
  public void genFileHeader(StringBuilder sb) {
    sb.append("#include ").append('"').append("IRetArguVisitor.h").append('"').append(LS);
  }


}

package EDU.purdue.jtb.misc;


@SuppressWarnings("javadoc")
public interface VarInfo {

  String getName();

  String getType();
  
  String getProduction();

  String generateNodeDeclaration();

}

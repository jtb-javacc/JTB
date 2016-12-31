package EDU.purdue.jtb.misc;

@SuppressWarnings("javadoc")
public class VarInfoFactory {

  public static VarInfo newVarInfo(final String type, final String name, final String init) {
    VarInfo varInfo = null;
    switch (Globals.language) {
      case java:
        varInfo = new VarInfoForJava(type, name, init);
        break;
      case cpp:
        varInfo = new VarInfoForCpp(type, name, init);
       break;
    }
    return varInfo;
  }

  public static VarInfo newVarInfo(final NodeClass type, final String name, final String init) {
    VarInfo varInfo = null;
    switch (Globals.language) {
      case java:
        varInfo = new VarInfoForJava(type, name, init);
        break;
      case cpp:
        varInfo = new VarInfoForCpp(type.getQualifiedName(), name, init);
       break;
    }
    return varInfo;
  }

  public static VarInfo newVarInfo(final String type, final String name) {
    VarInfo varInfo = null;
    switch (Globals.language) {
      case java:
        varInfo = new VarInfoForJava(type, name);
        break;
      case cpp:
        varInfo = new VarInfoForCpp(type, name);
       break;
    }
    return varInfo;
  }

  public static VarInfo newVarInfo(final NodeClass type, final String name) {
    VarInfo varInfo = null;
    switch (Globals.language) {
      case java:
        varInfo = new VarInfoForJava(type, name);
        break;
      case cpp:
        varInfo = new VarInfoForCpp(type.getQualifiedName(), name);
       break;
    }
    return varInfo;
  }

  public static VarInfo newVarInfo(String resultType, String production, String name, String initialization) {
    VarInfo varInfo = null;
    switch (Globals.language) {
      case java:
        varInfo = new VarInfoForJava(resultType, production, name, initialization);
        break;
      case cpp:
        varInfo = new VarInfoForCpp(resultType, production, name, initialization);
       break;
    }
    return varInfo;
  }

}

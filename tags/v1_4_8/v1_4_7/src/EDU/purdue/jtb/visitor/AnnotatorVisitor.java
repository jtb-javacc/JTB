package EDU.purdue.jtb.visitor;

import EDU.purdue.jtb.misc.FileExistsException;
import EDU.purdue.jtb.misc.VarInfo;
import EDU.purdue.jtb.syntaxtree.NodeChoice;


@SuppressWarnings("javadoc")
public interface AnnotatorVisitor extends IVoidVisitor {

  void saveToFile(String jtbOutputFileName) throws FileExistsException;
  
  String addNodeString(String parentName, String varName);
  
  String newConstructor();
  
  String qualifier();
  
  String makeNodeToken();
  
  String niew();
  
}

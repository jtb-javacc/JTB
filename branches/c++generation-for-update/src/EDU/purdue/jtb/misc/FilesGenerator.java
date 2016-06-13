package EDU.purdue.jtb.misc;
import java.io.PrintWriter;

@SuppressWarnings("javadoc")
public interface FilesGenerator {

  void genBaseNodesFiles() throws FileExistsException;

  void genNodesFiles() throws FileExistsException;

  void genRetArguIVisitorFile() throws FileExistsException;

  void genRetIVisitorFile() throws FileExistsException;

  void genVoidArguIVisitorFile() throws FileExistsException;

  void genVoidIVisitorFile() throws FileExistsException;

  void outputFormattedNodesClassesList(PrintWriter printWriter);

  String genClassEndArgList(final boolean arg, IVisitorClass intf);
  
  String genClassBegArgList(final boolean arg, IVisitorClass intf);


}

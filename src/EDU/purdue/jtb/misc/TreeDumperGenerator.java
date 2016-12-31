package EDU.purdue.jtb.misc;


@SuppressWarnings("javadoc")
public interface TreeDumperGenerator {

  void generateTreeDumper() throws FileExistsException;

  void saveToFile() throws FileExistsException;

  String outFilename();

}

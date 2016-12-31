package EDU.purdue.jtb.misc;


public interface TreeFormatterGenerator {

  void generateTreeFormatter() throws FileExistsException;

  void saveToFile() throws FileExistsException;

  String outFilename();

}

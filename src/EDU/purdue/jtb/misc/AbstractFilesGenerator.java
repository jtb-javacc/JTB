package EDU.purdue.jtb.misc;

import static EDU.purdue.jtb.misc.Globals.astNodesDirName;
import static EDU.purdue.jtb.misc.Globals.visitorsDirName;

import java.io.File;
import java.util.List;


@SuppressWarnings("javadoc")
public abstract class AbstractFilesGenerator implements FilesGenerator {

  /** The classes list */
  protected final List<ClassInfo> classes;
  /** The (generated) nodes directory */
  protected final File                 astNodesDir;
  /** The (generated) visitors directory */
  protected final File                 visitorsDir;
  

  /**
   * Constructor. Creates the nodes and visitors directories if they do not exist.
   * 
   * @param classesList - the list of {@link ClassInfoForJava} classes instances
   */
  AbstractFilesGenerator(final List<ClassInfo> classesList) {
    classes = classesList;

    astNodesDir = new File(astNodesDirName);
    visitorsDir = new File(visitorsDirName);

    if (!astNodesDir.exists())
      if (astNodesDir.mkdirs())
        Messages.info("\"" + astNodesDirName + "\" directory created.");
      else
        Messages.softErr("Unable to create \"" + astNodesDirName + "\" directory.");
    else if (!astNodesDir.isDirectory())
      Messages.softErr("\"" + astNodesDirName + "\" exists but is not a directory.");

    if (!visitorsDir.exists())
      if (visitorsDir.mkdirs())
        Messages.info("\"" + visitorsDirName + "\" directory created.");
      else
        Messages.softErr("Unable to create \"" + visitorsDirName + "\" directory.");
    else if (!visitorsDir.isDirectory())
      Messages.softErr("\"" + visitorsDirName + "\" exists but is not a directory.");
    

  }

}

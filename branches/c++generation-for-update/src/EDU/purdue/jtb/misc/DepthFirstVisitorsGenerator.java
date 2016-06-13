package EDU.purdue.jtb.misc;


@SuppressWarnings("javadoc")
public interface DepthFirstVisitorsGenerator {

  void decreaseDepthLevel(StringBuilder sb, Spacing spc);

  void increaseDepthLevel(StringBuilder sb, Spacing spc);

  void genDepthFirstRetArguVisitorFile() throws FileExistsException;

  void genDepthFirstRetVisitorFile() throws FileExistsException;

  void genDepthFirstVoidArguVisitorFile() throws FileExistsException;

  void genDepthFirstVoidVisitorFile() throws FileExistsException;

  String getQualifier();
 }

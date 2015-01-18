package EDU.purdue.jtb.misc;


/**
 * Holds the data of a line of a comment or sub comment.
 */
public class CommentLineData {

  /** The node's bare comment (should be never null after processing) */
  public String bare  = null;
  /** The node's debug comment (null if none, or starts with " //") */
  public String debug = null;
}

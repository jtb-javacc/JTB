package EDU.purdue.jtb.misc;

/**
 * Class handling JTB internal error.
 * 
 * @author Marc Mazas
 */
public class InternalError extends Error {

  /** Default serialVersionUID */
  private static final long serialVersionUID = 1L;

  /**
   * Constructor with no message.
   */
  public InternalError() {
    super();
  }

  /**
   * Constructor with a specific message.
   * 
   * @param s - a message
   */
  public InternalError(final String s) {
    super(s);
  }
}

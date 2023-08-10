package EDU.purdue.jtb.common;

/**
 * Class {@link ProgrammaticError} reports internal programmatic unrecoverable errors.
 * <p>
 * Class is thread-safe.
 * </p>
 *
 * @author Marc Mazas
 * @version 1.5.0 : 03/2017 : MMa : created, as a replacement of InternalError
 */
public class ProgrammaticError extends Error {

  /** Default value */
  private static final long serialVersionUID = 1L;

  /**
   * Standard constructor.
   *
   * @param aMsg - a message
   */
  public ProgrammaticError(final String aMsg) {
    super(aMsg);
  }

}

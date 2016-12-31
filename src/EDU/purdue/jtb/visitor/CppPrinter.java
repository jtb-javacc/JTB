package EDU.purdue.jtb.visitor;

import EDU.purdue.jtb.misc.Spacing;


public class CppPrinter extends AbstractPrinter{

  public CppPrinter() {
    super(null, null);
  }

  /**
   * Constructor with a given buffer and which will allocate a default indentation.
   * 
   * @param aSb - the buffer to print into (will be allocated if null)
   */
  public CppPrinter(final StringBuilder aSb) {
    super(aSb, null);
  }

  /**
   * Constructor with a given indentation which will allocate a default buffer.
   * 
   * @param aSPC - the Spacing indentation object
   */
  public CppPrinter(final Spacing aSPC) {
    super(null, aSPC);
  }

  /**
   * Constructor with a given buffer and indentation.
   * 
   * @param aSb - the buffer to print into (will be allocated if null)
   * @param aSPC - the Spacing indentation object (will be allocated and set to a default if null)
   */
  public CppPrinter(StringBuilder aSb, Spacing aSPC) {
    super(aSb, aSPC);
  }

  /*
   * Base classes visit methods
   */


}

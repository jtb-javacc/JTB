package EDU.purdue.jtb.visitor;

import static EDU.purdue.jtb.misc.Globals.INDENT_AMT;
import static EDU.purdue.jtb.misc.Globals.noOverwrite;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import EDU.purdue.jtb.misc.FileExistsException;
import EDU.purdue.jtb.misc.Messages;
import EDU.purdue.jtb.misc.Spacing;


public abstract class AbstractPrinter extends DepthFirstVoidVisitor{

  /** The buffer to print into */
  protected StringBuilder    sb;
  /** Indentation object */
  protected Spacing          spc;
  /** The OS line separator */
  public static final String LS              = System.getProperty("line.separator");
  /** True to suppress printing of debug comments, false otherwise */
  public boolean             noDebugComments = false;

  /**
   * Constructor with a given buffer and indentation.
   * 
   * @param aSb - the buffer to print into (will be allocated if null)
   * @param aSPC - the Spacing indentation object (will be allocated and set to a default if null)
   */
  public AbstractPrinter(final StringBuilder aSb, final Spacing aSPC) {
    reset(aSb, aSPC);
  }

  /**
   * Resets the buffer and the indentation.
   * 
   * @param aSb - the buffer to print into (will be allocated if null)
   * @param aSPC - the Spacing indentation object (will be allocated and set to a default if null)
   */
  public void reset(final StringBuilder aSb, final Spacing aSPC) {
    sb = aSb;
    if (sb == null)
      sb = new StringBuilder(2048);
    spc = aSPC;
    if (spc == null)
      spc = new Spacing(INDENT_AMT);
  }

  /**
   * Constructor which will allocate a default buffer and indentation.
   */
  public AbstractPrinter() {
    this(null, null);
  }

  /**
   * Constructor with a given buffer and which will allocate a default indentation.
   * 
   * @param aSb - the buffer to print into (will be allocated if null)
   */
  public AbstractPrinter(final StringBuilder aSb) {
    this(aSb, null);
  }

  /**
   * Constructor with a given indentation which will allocate a default buffer.
   * 
   * @param aSPC - the Spacing indentation object
   */
  public AbstractPrinter(final Spacing aSPC) {
    this(null, aSPC);
  }

  /**
   * Saves the current buffer to an output file.
   * 
   * @param outFile - the output file
   * @throws FileExistsException - if the file exists and the noOverwrite flag is set
   */
  public void saveToFile(final String outFile) throws FileExistsException {
    try {
      final File file = new File(outFile);
      if (noOverwrite && file.exists())
        throw new FileExistsException(outFile);
      else {
        final PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(file),
                                                                   sb.length()));
        out.print(sb);
        out.close();
      }
    }
    catch (final IOException e) {
      Messages.hardErr(e);
    }
  }


}

package EDU.purdue.jtb.misc;

import static EDU.purdue.jtb.misc.Globals.getFixedName;

import java.util.ArrayList;
import java.util.List;

import EDU.purdue.jtb.syntaxtree.ExpansionChoices;
import EDU.purdue.jtb.syntaxtree.INode;
import EDU.purdue.jtb.syntaxtree.NodeToken;
import EDU.purdue.jtb.visitor.GlobalDataBuilder;


@SuppressWarnings("javadoc")
public abstract class AbstractClassInfo  implements ClassInfo {
  /** The reference to the global data builder visitor */
  final GlobalDataBuilder           gdbv;
  
  /** The corresponding ExpansionChoices node */
  private final INode               astEcNode;
  
  /** The class name (including optional prefix and suffix) */
  protected final String            className;
  
  /** The list of the types of the class fields representing the node's children */
  protected final List<String>      fieldTypes;
  
  /** The list of the names of the class fields representing the node's children */
  protected final List<String>      fieldNames;
  
  /** The list of the initializers of the class fields representing the node's children */
  protected final List<String>      fieldInitializers;
  
  /** True if the class allows specific initializing constructor(s) (without {@link NodeToken} nodes */
  protected boolean                 needInitializingConstructor = false;
  
  /** The list of the java code elements in an EUTCF */
  protected final List<String>      fieldEUTCFCodes;
  
  /** The list of the field comments data */
  protected List<CommentData>       fieldCmts;
  /**
   * The list of the sub comments data (without field comments data).<br>
   * Built and used only when the "inline accept methods" option is on.
   */
  protected List<CommentData>       subCmts;
  /**
   * The javadoc formatted field comments used by the visit methods (more than once, so that's why
   * they are stored as an optimization)
   */
  protected StringBuilder           visitFieldCmts;
  
  /**
   * Constructs an instance giving an ExpansionChoices node and a name.
   * 
   * @param aEC - the ExpansionChoices node
   * @param aCN - the class name
   * @param aGdbv - the global data builder visitor
   */
  public AbstractClassInfo(final ExpansionChoices aEC, final String aCN, final GlobalDataBuilder aGdbv) {
    astEcNode = aEC;
    className = getFixedName(aCN);
    final int nb = (aEC.f1.present() ? aEC.f1.size() + 1 : 1);
    fieldTypes = new ArrayList<String>(nb);
    fieldNames = new ArrayList<String>(nb);
    fieldInitializers = new ArrayList<String>(nb);
    fieldEUTCFCodes = new ArrayList<String>(nb);
    gdbv = aGdbv;
  }
  @Override
  public String getClassName() {
    return className;
  }

  @Override
  public INode getASTECNode() {
    return astEcNode;
  }
  @Override
  public List<String> getFieldTypes() {
    return fieldTypes;
  }

  @Override
  public List<String> getFieldNames() {
    return fieldNames;
  }

  @Override
  public List<String> getFieldInitializers() {
    return fieldInitializers;
  }

  @Override
  public List<String> getFieldEUTCFCodes() {
    return fieldEUTCFCodes;
  }
  @Override
  public List<CommentData> getFieldCmts() {
    if (fieldCmts == null)
      fieldCmts = new ArrayList<CommentData>();
    return fieldCmts;
  }

  @Override
  public List<CommentData> getSubCmts() {
    if (subCmts == null)
      subCmts = new ArrayList<CommentData>();
    return subCmts;
  }
  @Override
  public String toString() {
    return className;
  }


}

/* Generated by JTB 1.5.1 */
/**
 * Provides constants reflecting the JTB user nodes.<br>
 */
public class NodeConstants {

  /** The number of JTB user nodes */
  public static final int NB_JTB_USER_NODES = 8;

  /** The classDeclaration JTB user node's index */
  public static final int JTB_USER_CLASSDECLARATION = 0;

  /** The classDeclaration JTB control signature */
  public static final int JTB_SIG_CLASSDECLARATION = -1372830968;

  /** The className JTB user node's index */
  public static final int JTB_USER_CLASSNAME = 1;

  /** The className JTB control signature */
  public static final int JTB_SIG_CLASSNAME = -1032372970;

  /** The method JTB user node's index */
  public static final int JTB_USER_METHOD = 2;

  /** The method JTB control signature */
  public static final int JTB_SIG_METHOD = 128623837;

  /** The methodName JTB user node's index */
  public static final int JTB_USER_METHODNAME = 3;

  /** The methodName JTB control signature */
  public static final int JTB_SIG_METHODNAME = -1032372970;

  /** The instruction JTB user node's index */
  public static final int JTB_USER_INSTRUCTION = 4;

  /** The instruction JTB control signature */
  public static final int JTB_SIG_INSTRUCTION = 119476985;

  /** The bp_jual JTB user node's index */
  public static final int JTB_USER_BP_JUAL = 5;

  /** The bp_jual JTB control signature */
  public static final int JTB_SIG_BP_JUAL = -1032372970;

  /** The bp_hm JTB user node's index */
  public static final int JTB_USER_BP_HM = 6;

  /** The bp_hm JTB control signature */
  public static final int JTB_SIG_BP_HM = -1032372970;

  /** The jc_0 JTB user node's index */
  public static final int JTB_USER_JC_0 = 7;

  /** The jc_0 JTB control signature */
  public static final int JTB_SIG_JC_0 = 0;

  /** The JTB user nodes' array */
  public static final String[] JTB_USER_NODE_NAME = new String[NB_JTB_USER_NODES];

  /** Initialize the JTB user nodes' array */
  static {
  JTB_USER_NODE_NAME[0] = "classDeclaration"; //$NON-NLS-1$
  JTB_USER_NODE_NAME[1] = "className"; //$NON-NLS-1$
  JTB_USER_NODE_NAME[2] = "method"; //$NON-NLS-1$
  JTB_USER_NODE_NAME[3] = "methodName"; //$NON-NLS-1$
  JTB_USER_NODE_NAME[4] = "instruction"; //$NON-NLS-1$
  JTB_USER_NODE_NAME[5] = "bp_jual"; //$NON-NLS-1$
  JTB_USER_NODE_NAME[6] = "bp_hm"; //$NON-NLS-1$
  JTB_USER_NODE_NAME[7] = "jc_0"; //$NON-NLS-1$
  }

  /** The JTB control signatures' array */
  public static final int[] JTB_SIGNATURE = new int[NB_JTB_USER_NODES];

  /** Initialize the JTB control signatures' array */
  static {
  JTB_SIGNATURE[0] = JTB_SIG_CLASSDECLARATION;
  JTB_SIGNATURE[1] = JTB_SIG_CLASSNAME;
  JTB_SIGNATURE[2] = JTB_SIG_METHOD;
  JTB_SIGNATURE[3] = JTB_SIG_METHODNAME;
  JTB_SIGNATURE[4] = JTB_SIG_INSTRUCTION;
  JTB_SIGNATURE[5] = JTB_SIG_BP_JUAL;
  JTB_SIGNATURE[6] = JTB_SIG_BP_HM;
  JTB_SIGNATURE[7] = JTB_SIG_JC_0;
  }

}

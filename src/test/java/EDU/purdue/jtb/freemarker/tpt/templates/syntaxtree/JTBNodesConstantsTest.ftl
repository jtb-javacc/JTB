<#-- jtb_user_nodes is the Map<String, Map> of the JTB user nodes -->
<#-- parser_package is a String -->
<#-- syntaxtree_subpackage is a String -->
<#include "/commons/GeneratedBy.ftl">
package ${parser_package}.${syntaxtree_subpackage};

/**
 * Provides constants reflecting the JTB base & user nodes.
 *
 */
public class JTBNodesConstantsTest {

  /** The number of JTB user nodes */
  public static final int NB_JTB_USER_NODES = ${jtb_user_nodes?size};

<#list jtb_user_nodes as jtb_user_node_name, jtb_user_node_bag>
  /** The ${jtb_user_node_name} JTB user node's index */
  public static final int JTB_USER_${jtb_user_node_name?upper_case} = ${jtb_user_node_name?index};

</#list>
  /** The JTB user nodes' array */
  public static final String[] JTB_USER_NODE_NAME = new String[NB_JTB_USER_NODES];

  /** Initialize the JTB user nodes' array */
  static {
 <#list jtb_user_nodes as jtb_user_node_name, jtb_user_node_bag>
      JTB_USER_NODE_NAME[${jtb_user_node_name?index}] = "${jtb_user_node_name}";
</#list>
  }
}
<#-- jtb_base_nodes is the Map<String, Map> of the JTB base nodes -->
<#-- jtb_base_node_bag is a Map<?, ?> is not used -->
<#-- jtb_user_nodes is the Map<String, Map> of the JTB user nodes -->
<#-- jtb_user_node_bag is the Map<String, Map> of the JTB user nodes fields_comments and visit_code -->
<#-- fields_comments is the List<List> of the JTB user nodes field_comments for each field -->
<#-- field_comments is the List<String> of the JTB user nodes field_comment lines for a field -->
<#-- line is the String of a JTB user nodes field_comments line -->
<#-- parser_package is a String -->
<#-- syntaxtree_subpackage is a String -->
<#-- visitor_subpackage is a String -->
<#include "/commons/GeneratedBy.ftl">
package ${parser_package}.${visitor_subpackage};

import static ${parser_package}.${syntaxtree_subpackage}.JTBNodesConstantsTest.*;
import ${parser_package}.Token;
import ${parser_package}.${syntaxtree_subpackage}.*;

import java.util.Hashtable;
import java.util.Map;

/**
 * Provides default methods which visit each node in the tree in depth-first order.<br>
 * In your "Void" visitors extend this class and override part or all of these methods.
 */
public class SimpleStatsVisitorTest extends DepthFirstVoidVisitorTest {

  /*
   * Statistics
   */
 
  /** The Integer 0 */
  static final Integer ZERO = Integer.valueOf(0);
 /** Productions (JTB user nodes) counters map */
  static final Map<String, Integer> prodNameMap = new Hashtable<String, Integer>(NB_JTB_USER_NODES);
  /** Initialize the map */
  static {
    for (int i = 0; i < NB_JTB_USER_NODES; i++) {
      prodNameMap.put(JTB_USER_NODE_NAME[i], ZERO);
    }
  }

  /**
   * Adds a production name to its maps.
   *
   * @param aINode - the JTB INode
   */
  static void addProduction(final INode aINode) {
    final String cn = aINode.getClass().getName();
    final String name = cn.substring(1 + cn.lastIndexOf('.'));
    synchronized (prodNameMap) {
      Integer val = prodNameMap.get(name);
      val = Integer.valueOf(1 + val.intValue());
      prodNameMap.put(name, val);
    }
  }

  /**
   * Prints on System.out productions counters (lines of name:value).
   */
  public static void printProdStats() {
    for (int i = 0; i < NB_JTB_USER_NODES; i++) {
      final String name = JTB_USER_NODE_NAME[i];
      final Integer val = prodNameMap.get(name);
      System.out.println(name + ":" + val.toString());
    }
  }
  

  /*
   * Base nodes classes visit methods (to be overridden if necessary)
   */

<#list jtb_base_nodes as jtb_base_node_name, jtb_base_node_bag>
  /**
   * Visits a {@link ${jtb_base_node_name}} node.
   *
   * @param n - the node to visit
   */
  @Override
  public void visit(final ${jtb_base_node_name} n) {
    super.visit(n);
  }

</#list>

  /*
   * User grammar generated visit methods (to be overridden if necessary)
   */

<#list jtb_user_nodes as jtb_user_node_name, jtb_user_node_bag>
  /**
   * Visits a {@link ${jtb_user_node_name}} node, whose children are the following :
   * <p>
<#list jtb_user_node_bag.fields_comments as field_comments>
  <#list field_comments>
    <#items as comment_line>
   * ${comment_line}
   </#items>
  </#list>
</#list>
   *
   * @param n - the node to visit
   */
  @Override
  public void visit(final ${jtb_user_node_name} n) {
    addProduction(n);
    super.visit(n);
  }

</#list>
}
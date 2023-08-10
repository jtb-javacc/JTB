<#-- jtb_base_nodes is the Map<String, Map> of the JTB base nodes -->
<#-- jtb_base_node_bag is a Map<String, Map> of the JTB base nodes visit_code -->
<#-- jtb_user_nodes is the Map<String, Map> of the JTB user nodes -->
<#-- jtb_user_node_bag is the Map<String, Map> of the JTB user nodes fields_comments and visit_code -->
<#-- fields_comments is the List<List> of the JTB user nodes field_comments for each field -->
<#-- field_comments is the List<String> of the JTB user nodes field_comment lines for a field -->
<#-- comment_line is the String of a JTB user nodes field_comments line -->
<#-- visit_code is the List<String> of the JTB base and user nodes visit code lines for a node -->
<#-- code_line is the String of a JTB base or user nodes visit code line -->
<#-- parser_package is a String -->
<#-- syntaxtree_subpackage is a String -->
<#-- visitor_subpackage is a String -->
<#include "/commons/GeneratedBy.ftl">
package ${parser_package}.${visitor_subpackage};

import ${parser_package}.${syntaxtree_subpackage}.*;

/**
 * Provides default methods which visit each node in the tree in depth-first order.<br>
 * In your "Void" visitors extend this class and override part or all of these methods.
 */
@SuppressWarnings("unused")
public class DepthFirstVoidVisitorTest implements IVoidVisitorTest {

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
<#list jtb_base_node_bag.visit_code as code_line>
    ${code_line}
</#list>
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
<#list jtb_user_node_bag.visit_code as code_line>
    ${code_line}
</#list>
  }

</#list>
}
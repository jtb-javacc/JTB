package EDU.purdue.jtb.freemarker.tpt;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import freemarker.core.ParseException;
import freemarker.template.Configuration;
import freemarker.template.MalformedTemplateNameException;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import freemarker.template.TemplateNotFoundException;

/**
 * A processor for manually testing the use of Freemarker templates for outputting JTB nodes derived
 * classes.<br>
 * To be used as a standalone tool through its main. It
 * <li>works on a specific source directory structure (distinct from the JTB one),</li>
 * <li>needs the freemarker jar on the classpath (in Eclipse through the Java Build Path / Libraries / Add
 * External Jar,</li>
 * <li>builds a simple data model simulating a few class information produced by JTB on itself,</li>
 * <li>uses some simple templates and</li>
 * <li>generates some syntax tree and visitor test classes that should compile again the JTB full syntax tree
 * and visitors.</li><br>
 * It should be not necessary to integrate it in the JTB automatic build process, nor the whole specific
 * source directory structure be added to the built JTB jar. However it should be under version control.
 *
 * @author Marc Mazas
 * @version 1.5.0 : 01/2017 : MMa : created
 * @version 1.5.1 : 07/2023 : MMa : adapted for package change
 */
public class TemplateProcessorTester {
  
  /** The Freemarker configuration instance */
  Configuration fmci;
  
  /** The parser package */
  public static final String FM_PARSER_PKG = "EDU.purdue.jtb.parser";
  
  /** The generated test classes parser package directory */
  public static final String FM_GEN_CLASSES_DIR = "target/generated-tests/java/EDU/purdue/jtb/parser";
  
  /** The templates directory */
  public static final String FM_TEMPLATES_DIR = "src/test/java/EDU/purdue/jtb/freemarker/tpt/templates";
  
  /** The data model root */
  public Map<String, Object> fmdm;
  
  // /** Standard constructor */
  // public FMTemplateProcessor() {
  // }
  
  /**
   * Standard main used for direct testing.
   *
   * @param args - command line arguments (not used)
   */
  public static void main(final String[] args) {
    final TemplateProcessorTester fmtp = new TemplateProcessorTester();
    fmtp.fmci = TemplateProcessorTester.createFMConfiguration();
    fmtp.fmdm = TemplateProcessorTester.createTestDataModel();
    fmtp.testGenTemplate("syntaxtree/JTBNodesConstantsTest.ftl");
    fmtp.testGenTemplate("visitor/IVoidVisitorTest.ftl");
    fmtp.testGenTemplate("visitor/DepthFirstVoidVisitorTest.ftl");
    fmtp.testGenTemplate("visitor/SimpleStatsVisitorTest.ftl");
  }
  
  // /** Standard constructor */
  // public FMTemplateProcessor() {
  // }
  
  /**
   * Creates and initializes the Freemarker configuration.
   *
   * @return the configuration, or null if the templates directory is not found
   */
  private static Configuration createFMConfiguration() {
    final Configuration cfg = new Configuration(Configuration.VERSION_2_3_25);
    try {
      cfg.setDirectoryForTemplateLoading(new File(FM_TEMPLATES_DIR));
    } catch (final IOException ex) {
      System.out.flush();
      System.err
          .println("FMTemplateProcessorTester: error: cannot find templates directory " + FM_TEMPLATES_DIR);
      ex.printStackTrace();
      return null;
    }
    cfg.setDefaultEncoding("UTF-8");
    cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
    cfg.setLogTemplateExceptions(false);
    return cfg;
  }
  
  /**
   * @return a FM data model root Map populated with test data
   */
  private static Map<String, Object> createTestDataModel() {
    // note : branches are created and filled first then added to the nodes
    // root
    final Map<String, Object> root = new HashMap<>();
    
    // scalars
    root.put("jtb_version", "1.x.y");
    root.put("parser_package", FM_PARSER_PKG);
    root.put("syntaxtree_subpackage", "syntaxtree");
    root.put("visitor_subpackage", "visitor");
    // common lists
    List<List<String>> fields_comments;
    List<String> comment_lines;
    List<String> visit_code;
    
    // jtb_base_nodes container
    final Map<String, Object> jtb_base_nodes = new TreeMap<>();
    Map<String, Object> jtb_base_node_bag;
    
    jtb_base_node_bag = new HashMap<>();
    visit_code = Arrays.asList(new String[] {
        "int a = 0;", "return;"
    });
    jtb_base_node_bag.put("visit_code", visit_code);
    jtb_base_nodes.put("NodeChoice", jtb_base_node_bag);
    jtb_base_node_bag = new HashMap<>();
    visit_code = Arrays.asList(new String[] {
        "int b = 1;", "return;"
    });
    jtb_base_node_bag.put("visit_code", visit_code);
    jtb_base_nodes.put("NodeList", jtb_base_node_bag);
    jtb_base_node_bag = new HashMap<>();
    visit_code = Arrays.asList(new String[] {
        "int c = 2;", "return;"
    });
    jtb_base_node_bag.put("visit_code", visit_code);
    jtb_base_nodes.put("NodeToken", jtb_base_node_bag);
    
    root.put("jtb_base_nodes", jtb_base_nodes);
    
    // jtb_user_nodes container
    final Map<String, Object> jtb_user_nodes = new TreeMap<>();
    Map<String, Object> jtb_user_node_bag;
    
    jtb_user_node_bag = new HashMap<>();
    fields_comments = new ArrayList<>();
    comment_lines = Arrays.asList(new String[] {
        "f0 -> ( %0 < IDENTIFIER ><br>", ".. .. | %1 \"LOOKAHEAD\"<br>"
    });
    fields_comments.add(comment_lines);
    comment_lines = Arrays.asList(new String[] {
        "f1 -> \"=\"<br>"
    });
    fields_comments.add(comment_lines);
    jtb_user_node_bag.put("fields_comments", fields_comments);
    visit_code = Arrays.asList(new String[] {
        "int i = 0;", "return;"
    });
    jtb_user_node_bag.put("visit_code", visit_code);
    jtb_user_nodes.put("CompilationUnit", jtb_user_node_bag);
    
    jtb_user_node_bag = new HashMap<>();
    fields_comments = new ArrayList<>();
    comment_lines = Arrays.asList(new String[] {
        "f0 -> [ #0 \"options\" #1 \"{\"<br>", ".. .. . #2 ( OptionBinding() )*<br>"
    });
    fields_comments.add(comment_lines);
    jtb_user_node_bag.put("fields_comments", fields_comments);
    visit_code = Arrays.asList(new String[] {
        "int j = 1;", "return;"
    });
    jtb_user_node_bag.put("visit_code", visit_code);
    jtb_user_nodes.put("Expression", jtb_user_node_bag);
    
    jtb_user_node_bag = new HashMap<>();
    fields_comments = new ArrayList<>();
    comment_lines = Arrays.asList(new String[] {
        "f0 -> JavaCCOptions()<br>", "f1 -> \"PARSER_BEGIN\"<br>"
    });
    fields_comments.add(comment_lines);
    comment_lines = Arrays.asList(new String[] {
        "f1 -> \"=\"<br>"
    });
    fields_comments.add(comment_lines);
    jtb_user_node_bag.put("fields_comments", fields_comments);
    visit_code = Arrays.asList(new String[] {
        "int k = 2;", "return;"
    });
    jtb_user_node_bag.put("visit_code", visit_code);
    jtb_user_nodes.put("IdentifierAsString", jtb_user_node_bag);
    
    root.put("jtb_user_nodes", jtb_user_nodes);
    
    return root;
  }
  
  /**
   * Generates a template with test data.
   *
   * @param aTpName - the template absolute name
   */
  private void testGenTemplate(final String aTpName) {
    try {
      final Template temp = fmci.getTemplate(aTpName);
      final int ld = aTpName.lastIndexOf('.');
      final String of = FM_GEN_CLASSES_DIR + File.separator + aTpName.substring(0, ld) + ".java";
      final File fof = new File(of);
      final File dof = fof.getParentFile();
      if (!dof.exists() && !fof.getParentFile().mkdirs()) {
        System.out.flush();
        System.err
            .println("FMTemplateProcessorTester: error: cannot creating the path up to " + fof.getParent());
        return;
      }
      try (final Writer out = new FileWriter(new File(of))) {
        try {
          fmdm.put("template_file_name", aTpName);
          temp.process(fmdm, out);
          System.err.flush();
          System.out.println("FMTemplateProcessorTester: info: generated " + of + " from " + aTpName);
        } catch (final TemplateException ex) {
          System.out.flush();
          System.err.println("FMTemplateProcessorTester: error: cannot generate the file " + of
              + " with the test data " + fmdm);
          ex.printStackTrace();
        } catch (final IOException ex) { // for process(fmdm, out)
          System.out.flush();
          System.err.println("FMTemplateProcessorTester: erro: cannot write to the generated file " + of);
          ex.printStackTrace();
        }
      } catch (final IOException ex) { // for new FileWriter(new File(of))
        System.out.flush();
        System.err.println("FMTemplateProcessorTester: error: cannot create the generated file " + of);
        ex.printStackTrace();
      }
    } catch (final TemplateNotFoundException ex) {
      System.out.flush();
      System.err.println("FMTemplateProcessorTester: error: cannot find template file " + aTpName);
      ex.printStackTrace();
    } catch (final MalformedTemplateNameException ex) {
      System.out.flush();
      System.err.println("FMTemplateProcessorTester: error: cannot find template file name " + aTpName);
      ex.printStackTrace();
    } catch (final ParseException ex) {
      System.out.flush();
      System.err.println("FMTemplateProcessorTester: error: cannot parse the template file " + aTpName);
      ex.printStackTrace();
    } catch (final IOException ex) { // for getTemplate(aTpName)
      System.out.flush();
      System.err.println("FMTemplateProcessorTester: error: cannot read the template file " + aTpName);
      ex.printStackTrace();
    }
  }
  
}

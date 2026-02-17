package EDU.purdue.jtb.freemarker.egt;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

/**
 * External generator class, to be called by JTB, just for testing the call from JTB to a FreeMarker
 * environment.<br>
 * To use it:
 * <li>set its full qualified name {@link EDU.purdue.jtb.freemarker.egt.ExternalGeneratorTester} in the
 * grammar through the JTB_EG option,</li>
 * <li>set the (to be supplied externally) the FreeMarker jar path in the classpath
 * <ul>
 * <li>on the command line, or in an Ant build script, or in a Maven pom, or</li>
 * <li>if under the SourceForge JavaCC Eclipse plugin, set the project's JavaCC options / Global options tag /
 * Additional classpath</li>
 * </ul>
 * </li>It will dump the JTB data model and try to instantiate class freemarker.template.Configuration
 *
 * @author Marc Mazas
 * @version 1.5.0 : 01/2017 : MMa : created
 */
public class ExternalGeneratorTester {

  /** Standard constructor */
  public ExternalGeneratorTester() {
    // tbd
  }

  /**
   * Generates some output from the argument. This is the method that will be called by JTB through the Java
   * Reflection API.
   *
   * @param aDmRoot - a data model root
   * @return >=0 if successful, < 0 if error
   */
  @SuppressWarnings("static-method")
  public int generate(final Map<String, Object> aDmRoot) {
    // dump the data model
    System.out.println(aDmRoot);
    // try to call
    Class<?> egc;
    try {
      egc = Class.forName("freemarker.template.Configuration");
      egc.getDeclaredConstructor().newInstance();
      System.err.flush();
      System.out.println(
          "ExternalGeneratorTester: could successfully instantiate freemarker.template.Configuration.");
      return 0;
    } catch (final ClassNotFoundException ex) {
      System.out.flush();
      System.err.println(
          "ExternalGeneratorTester:  Error: could not find class freemarker.template.Configuration; check classpath.");
      ex.printStackTrace();
      return -128;
    } catch (final InstantiationException ex) {
      System.out.flush();
      System.err.println(
          "ExternalGeneratorTester:  Error: could not instantiate class freemarker.template.Configuration.");
      ex.printStackTrace();
      return -256;
    } catch (final IllegalAccessException ex) {
      System.out.flush();
      System.err.println(
          "ExternalGeneratorTester:  Error: could not access class freemarker.template.Configuration.");
      ex.printStackTrace();
      return -512;
    } catch (final IllegalArgumentException ex) {
      System.err.println(
          "ExternalGeneratorTester:  Error: could not pass proper arguments to class freemarker.template.Configuration.");
      ex.printStackTrace();
      return -1024;
    } catch (final InvocationTargetException ex) {
      System.err.println(
          "ExternalGeneratorTester:  Error: could not invoke target class freemarker.template.Configuration.");
      ex.printStackTrace();
      return -2048;
    } catch (final NoSuchMethodException ex) {
      System.err.println(
          "ExternalGeneratorTester:  Error: no such method in class freemarker.template.Configuration.");
      ex.printStackTrace();
      return -4096;
    } catch (final SecurityException ex) {
      System.err.println(
          "ExternalGeneratorTester:  Error: security violation on class freemarker.template.Configuration.");
      ex.printStackTrace();
      return -9192;
    }
  }
}

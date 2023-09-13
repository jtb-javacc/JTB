/* Generated by JTB 1.5.1 */
package grammars.tcf.visitor.signature;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.annotation.processing.SupportedAnnotationTypes;

import grammars.tcf.syntaxtree.NodeConstants;

/** 
 * Annotation {@link NodeFieldsSignature} enables the {@link ControlSignatureProcessor} annotation
 * processor to issue a compile error if the user visitors' visit methods are not coded against the
 * last nodes definitions.<br>
 * The user nodes signatures are generated in the {@link NodeConstants} class,<br>
 * the default visitors' visit methods are generated with the {@link NodeFieldsSignature}
 * annotation, with the 3 values {@link #value()},<br>
 * and the user visitors' visit methods can be annotated with the same annotation.
 * <p>
 * Note: the fully qualified name of this class is a parameter in the
 * {@link SupportedAnnotationTypes} annotation in {@link ControlSignatureProcessor}.
 * </p>
 *
 * @author Marc Mazas
 *  @version 1.5.0 : 02/2017 : MMa : created
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.SOURCE)
public @interface NodeFieldsSignature {

  /**
   * The array of
   * <ul>
   * <li>the "old" (usually copied) node fields signature</li>
   * <li>the "new" (newly generated) node fields signature</li>
   * <li>the JTB node index (in NodeConstants)</li>
   * </ul>
   */
  int[] value();

}
/**
 * 
 */
package org.gcube.documentstore.records.implementation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Luca Frosini (ISTI - CNR)
 *
 */
@Target(ElementType.ANNOTATION_TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface FieldDecorator {

	Class<? extends FieldAction> action();
}


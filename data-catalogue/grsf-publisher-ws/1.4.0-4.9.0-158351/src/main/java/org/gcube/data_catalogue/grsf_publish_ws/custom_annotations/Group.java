package org.gcube.data_catalogue.grsf_publish_ws.custom_annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Group annotation
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Group {

	/**
	 * Define a REGEX condition to be checked before the record
	 * is actually added to the group.
	 */
	String condition() default "";
	
	/**
	 * If this value is set, it is the name of the group to which the 
	 * record needs to be put.
	 * @return
	 */
	String groupNameOverValue() default "";
	
}

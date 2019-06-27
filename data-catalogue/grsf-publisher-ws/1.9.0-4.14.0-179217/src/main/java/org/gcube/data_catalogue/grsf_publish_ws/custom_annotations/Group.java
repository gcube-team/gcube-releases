package org.gcube.data_catalogue.grsf_publish_ws.custom_annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Group annotation
 * @author Costantino Perciante (ISTI - CNR)
 * @author Luca Frosini (ISTI - CNR)
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
	 * If this value is set, it is the name of the group (apart the source, i.e. "grsf", "ram", "firms", "fishsource" that will be prepended) to which the 
	 * record needs to be put.
	 * @return
	 */
	String groupNameOverValue() default "";
	
	/**
	 * When the group is created the source is prepended to the group name.
	 * Set to false to avoid source prepending   
	 * @return
	 */
	boolean prependSourceToGroupName() default true;
	
}

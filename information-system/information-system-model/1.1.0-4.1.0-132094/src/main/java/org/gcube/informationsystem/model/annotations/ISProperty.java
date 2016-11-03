package org.gcube.informationsystem.model.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.gcube.informationsystem.model.entity.Entity;
import org.gcube.informationsystem.types.TypeBinder;
import org.gcube.informationsystem.types.TypeBinder.Property;

/**
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 * It is used by {@link TypeBinder} to identify which getter method are
 * related to and {@link Entity} {@link Property}.
 * The name of the property is obtained by removing "get" or "is" from method 
 * name and lower casing the first letter.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ISProperty {

	String name() default "";
	String description() default "";
	boolean mandatory() default false;
	boolean readonly() default false;
	boolean nullable() default true;
	int min() default -1;
	int max() default -1;
	String regexpr() default "";

}

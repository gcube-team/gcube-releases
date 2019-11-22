package org.gcube.informationsystem.model.reference.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.gcube.informationsystem.model.reference.entity.Facet;

/**
 * @author Luca Frosini (ISTI - CNR)
 * Provide a way to identify a Key for a 
 * {@link Facet}
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Key {
	
	String name() default "";
	
	String[] fields() default {};

}

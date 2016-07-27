package org.gcube.informationsystem.model.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

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

package org.gcube.common.authorization.control.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.gcube.common.authorization.library.policies.Action;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface AuthorizationControl {
	
	Action[] actions() default {}; 
	String[] allowed() default {};
	Class<? extends RuntimeException> exception();
}

package org.gcube.common.storagehub.model.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ListNodes {

	String includeTypeStartWith() default "";
	String excludeTypeStartWith() default "";
	Class<?> listClass();
}

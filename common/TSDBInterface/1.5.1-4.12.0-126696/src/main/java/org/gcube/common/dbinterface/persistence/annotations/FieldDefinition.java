package org.gcube.common.dbinterface.persistence.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.gcube.common.dbinterface.Specification;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface FieldDefinition {
	Specification[] specifications() default {};
	int[] precision() default {};
}
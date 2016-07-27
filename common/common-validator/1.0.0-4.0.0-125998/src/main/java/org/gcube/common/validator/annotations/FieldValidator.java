package org.gcube.common.validator.annotations;

import java.lang.annotation.Annotation;

public interface FieldValidator<T extends Annotation> {

	Class<T> annotation();
	
	boolean isValid(Object toValidate);
	
	String getErrorSuffix();
}

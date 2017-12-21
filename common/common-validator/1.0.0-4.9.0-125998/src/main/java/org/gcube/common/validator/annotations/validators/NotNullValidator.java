package org.gcube.common.validator.annotations.validators;

import org.gcube.common.validator.annotations.FieldValidator;
import org.gcube.common.validator.annotations.NotNull;

public class NotNullValidator implements FieldValidator<NotNull>{

	public String getErrorSuffix() {
		return "is null";
	}

	public Class<NotNull> annotation() {
		return NotNull.class;
	}

	public boolean isValid(Object toValidate) {
		return toValidate!=null;
	}

}

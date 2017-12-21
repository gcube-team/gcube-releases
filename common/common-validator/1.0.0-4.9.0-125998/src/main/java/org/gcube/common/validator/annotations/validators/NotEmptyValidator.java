package org.gcube.common.validator.annotations.validators;

import java.util.Map;
import org.gcube.common.validator.annotations.FieldValidator;
import org.gcube.common.validator.annotations.NotEmpty;

public class NotEmptyValidator implements FieldValidator<NotEmpty>{

	public Class<NotEmpty> annotation() {
		return NotEmpty.class;
	}

	public boolean isValid(Object toValidate) {
		if (toValidate == null) return false;
		if (toValidate.getClass().isArray() ){
			return ((Object[])toValidate).length>0;
		}else if ( toValidate instanceof Iterable<?>){
			return ((Iterable<?>) toValidate).iterator().hasNext();
		} else if (toValidate instanceof Map<?,?>){
			return ((Map<?,?>) toValidate).size()>0;
		} else if (toValidate instanceof String ){
			return !((String)toValidate).isEmpty();
		} else return true;
	}

	public String getErrorSuffix() {
		return "is empty";
	}

}

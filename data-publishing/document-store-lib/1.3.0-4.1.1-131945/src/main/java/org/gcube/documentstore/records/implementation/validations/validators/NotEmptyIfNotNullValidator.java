package org.gcube.documentstore.records.implementation.validations.validators;

import java.io.Serializable;
import java.util.Map;

public class NotEmptyIfNotNullValidator extends NotEmptyValidator {

	protected boolean isValid(Serializable toValidate) {
		if (toValidate == null) return true;
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
	
}

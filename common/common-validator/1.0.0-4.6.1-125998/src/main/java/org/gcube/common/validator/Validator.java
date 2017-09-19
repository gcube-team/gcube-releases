package org.gcube.common.validator;

import java.util.List;

public interface Validator {
		
	List<ValidationError> validate(Object obj);
	
}

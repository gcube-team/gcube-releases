package org.gcube.common.validator.context;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.gcube.common.validator.ValidationError;

public class ValidatorContext {
	
	private List<ValidationError> errors = new ArrayList<ValidationError>();
		
	private Object parent;
	private Field field;
	private Object value;
		
	public ValidatorContext(Object parent, Field field, Object value) {
		super();
		this.parent = parent;
		this.field = field;
		this.value = value;
	}

	public void addError(String msg) {
		errors.add(new ValidationError(this,msg));
	}

	public void addErrors(List<ValidationError> errorsToAdd) {
		errors.addAll(errorsToAdd);
	}
	
	public List<ValidationError> errors() {
		return errors;
	}

	public Object parent() {
		return parent;
	}

	public Field field() {
		return field;
	}

	public Object value() {
		return value;
	}

	
	
}
		


package org.gcube.common.validator;

import java.lang.reflect.Field;

import org.gcube.common.validator.context.ValidatorContext;

public class ValidationError {

		
	public Field field;
	public Object parent;
	public Object value;
	public String msg;
	
	public ValidationError(ValidatorContext context, String msg) {
		this.field=context.field();
		this.parent=context.parent();
		this.value=context.value();
		this.msg=msg;
	}
		
	
	@Override
	public String toString() {
		return field.getName().toUpperCase()+" in "+value.getClass().getSimpleName()+" "+msg;
	}
}

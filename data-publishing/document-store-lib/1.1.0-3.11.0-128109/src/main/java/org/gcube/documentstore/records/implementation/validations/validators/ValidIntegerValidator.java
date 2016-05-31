package org.gcube.documentstore.records.implementation.validations.validators;

import java.io.Serializable;

import org.gcube.documentstore.exception.InvalidValueException;
import org.gcube.documentstore.records.Record;
import org.gcube.documentstore.records.implementation.FieldAction;


public class ValidIntegerValidator implements FieldAction {
	
	private static final String ERROR = String.format("Not Instance of %s", Integer.class.getSimpleName());
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Serializable validate(String key, Serializable value, Record record) throws InvalidValueException {
		if(value instanceof Integer){
			return value;
		}
		Integer integerObj = Integer.valueOf((String) value);
		if(integerObj!=null){
			return integerObj;
		}
		
		throw new InvalidValueException(ERROR);
	}

}

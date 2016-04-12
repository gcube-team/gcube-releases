package org.gcube.documentstore.records.implementation.validations.validators;

import java.io.Serializable;

import org.gcube.documentstore.exception.InvalidValueException;
import org.gcube.documentstore.records.Record;
import org.gcube.documentstore.records.implementation.FieldAction;

public class NotNullValidator implements FieldAction {
	
	private static final String ERROR = "Is Null";
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Serializable validate(String key, Serializable value, Record record) throws InvalidValueException {
		if(value!=null){
			return value;
		}
		throw new InvalidValueException(ERROR);
	}

}

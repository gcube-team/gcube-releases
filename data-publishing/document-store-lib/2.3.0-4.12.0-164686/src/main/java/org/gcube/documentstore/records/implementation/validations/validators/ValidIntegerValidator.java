package org.gcube.documentstore.records.implementation.validations.validators;

import java.io.Serializable;

import org.gcube.documentstore.exception.InvalidValueException;
import org.gcube.documentstore.records.Record;
import org.gcube.documentstore.records.implementation.FieldAction;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
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
		
		try {
			Integer integerObj = Integer.valueOf((String) value);
			if(integerObj!=null){
				return integerObj;
			}
		}catch (Exception e) {}
		
		try {
			Double doubleObj = Double.valueOf((String) value);
			if(doubleObj!=null){
				return doubleObj.intValue();
			}
		}catch (Exception e) {}
		
		throw new InvalidValueException(ERROR);
	}

}

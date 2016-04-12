package org.gcube.documentstore.records.implementation.validations.validators;

import java.io.Serializable;

import org.gcube.documentstore.exception.InvalidValueException;
import org.gcube.documentstore.records.Record;
import org.gcube.documentstore.records.implementation.FieldAction;


public class ValidBooleanValidator implements FieldAction {

	private static final String ERROR = String.format("Not Instance of %s", Boolean.class.getSimpleName());
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Serializable validate(String key, Serializable value, Record record) throws InvalidValueException {
		if(value instanceof Boolean){
			return value;
		}
		
		try {
			if(value instanceof String){
				try{
					Boolean booleanObj = Boolean.valueOf((String) value);
					if(booleanObj !=null){
						return booleanObj;
					}
				} catch(Exception e){
					// Trying another way
				}
				
				try{
					Integer integer = Integer.getInteger((String) value);
					if(integer!=null){
						value = integer;
					}
				} catch(Exception e){
					// Trying another way
				}
			}
			
			if(value instanceof Integer){
				Boolean booleanObj = ((Integer) value) == 0 ? false : true;
				if(booleanObj !=null){
					return booleanObj;
				}
				
			}
			
			
		}catch(Exception e){
			throw new InvalidValueException(ERROR, e);
		}
		
		throw new InvalidValueException(ERROR);
	}

}

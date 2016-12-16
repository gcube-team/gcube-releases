package org.gcube.accounting.datamodel.validations.validators;

import java.io.Serializable;

import org.gcube.accounting.datamodel.UsageRecord.OperationResult;
import org.gcube.accounting.datamodel.basetypes.AbstractStorageUsageRecord.OperationType;
import org.gcube.documentstore.exception.InvalidValueException;
import org.gcube.documentstore.records.Record;
import org.gcube.documentstore.records.implementation.FieldAction;

public class ValidOperationTypeValidator implements FieldAction {

	private static final String ERROR = String.format("Not Instance of %s", OperationResult.class.getSimpleName());
	
	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public Serializable validate(String key, Serializable value, Record record) throws InvalidValueException {
		if(value instanceof OperationType){
			return value;
		}
		
		try {
			if(value instanceof String){
				try{
					OperationType operationType = OperationType.valueOf((String) value);
					if(operationType !=null){
						return operationType;
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
				return OperationType.values()[(Integer) value];
			}
			
			if(value instanceof Enum){
				return OperationType.values()[((Enum) value).ordinal()];
			}
			
		}catch(Exception e){
			throw new InvalidValueException(ERROR, e);
		}
		
		throw new InvalidValueException(ERROR);
	}
	

}

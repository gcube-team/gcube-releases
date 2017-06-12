package org.gcube.accounting.datamodel.validations.validators;

import java.io.Serializable;

import org.gcube.accounting.datamodel.UsageRecord.OperationResult;
import org.gcube.documentstore.exception.InvalidValueException;
import org.gcube.documentstore.records.Record;
import org.gcube.documentstore.records.implementation.FieldAction;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
public class ValidOperationResultValidator implements FieldAction {

	private static final String ERROR = String.format("Not Instance of %s", OperationResult.class.getSimpleName());
	
	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public Serializable validate(String key, Serializable value, Record record) throws InvalidValueException {
		if(value instanceof OperationResult){
			return value;
		}
		
		try {
			if(value instanceof String){
				try{
					OperationResult operationResult = OperationResult.valueOf((String) value);
					if(operationResult !=null){
						return operationResult;
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
				return OperationResult.values()[(Integer) value];
			}
			
			if(value instanceof Enum){
				return OperationResult.values()[((Enum) value).ordinal()];
			}
			
		}catch(Exception e){
			throw new InvalidValueException(ERROR, e);
		}
		
		throw new InvalidValueException(ERROR);
	}
	

}

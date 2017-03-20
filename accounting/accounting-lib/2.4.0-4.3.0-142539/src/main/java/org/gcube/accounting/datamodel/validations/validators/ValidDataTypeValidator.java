package org.gcube.accounting.datamodel.validations.validators;

import java.io.Serializable;

import org.gcube.accounting.datamodel.basetypes.AbstractStorageUsageRecord.DataType;
import org.gcube.documentstore.exception.InvalidValueException;
import org.gcube.documentstore.records.Record;
import org.gcube.documentstore.records.implementation.FieldAction;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
public class ValidDataTypeValidator implements FieldAction {

	private static final String ERROR = String.format("Not Instance of %s", DataType.class.getSimpleName());
	
	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public Serializable validate(String key, Serializable value, Record record) throws InvalidValueException {
		if(value instanceof DataType){
			return value;
		}
		
		try {
			if(value instanceof String){
				try{
					DataType dataType = DataType.valueOf((String) value);
					if(dataType !=null){
						return dataType;
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
				return DataType.values()[(Integer) value];
			}
			
			if(value instanceof Enum){
				return DataType.values()[((Enum) value).ordinal()];
			}
			
		}catch(Exception e){
			throw new InvalidValueException(ERROR, e);
		}
		
		throw new InvalidValueException(ERROR);
	}
	

}

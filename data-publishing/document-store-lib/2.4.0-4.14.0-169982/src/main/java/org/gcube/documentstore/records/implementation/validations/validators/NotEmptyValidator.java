package org.gcube.documentstore.records.implementation.validations.validators;

import java.io.Serializable;
import java.util.Map;

import org.gcube.documentstore.exception.InvalidValueException;
import org.gcube.documentstore.records.Record;
import org.gcube.documentstore.records.implementation.FieldAction;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
public class NotEmptyValidator implements FieldAction {
	
	private static final String ERROR = "Is Empty";
	
	protected boolean isValid(Serializable toValidate) {
		if (toValidate == null) return false;
		if (toValidate.getClass().isArray() ){
			return ((Object[])toValidate).length>0;
		}else if ( toValidate instanceof Iterable<?>){
			return ((Iterable<?>) toValidate).iterator().hasNext();
		} else if (toValidate instanceof Map<?,?>){
			return ((Map<?,?>) toValidate).size()>0;
		} else if (toValidate instanceof String ){
			return !((String)toValidate).isEmpty();
		} else return true;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Serializable validate(String key, Serializable value, Record record) throws InvalidValueException {
		try{
			if(isValid((Serializable) value)){
				return value;
			}
		}catch(Exception e){
			throw new InvalidValueException(ERROR, e);
		}
		throw new InvalidValueException(ERROR);
	}

}

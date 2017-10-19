package org.gcube.accounting.datamodel.validations.validators;

import java.io.Serializable;
import java.net.URI;

import org.gcube.documentstore.exception.InvalidValueException;
import org.gcube.documentstore.records.Record;
import org.gcube.documentstore.records.implementation.FieldAction;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
public class ValidURIValidator implements FieldAction {
	
	private static final String ERROR = "Not Valid URI";
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Serializable validate(String key, Serializable value, Record record) throws InvalidValueException {
		try {
			if(value instanceof URI){
				return value;
			}
			if(value instanceof String){
				return new URI((String) value);
			}
		}catch (Exception e) {
			throw new InvalidValueException(ERROR, e);
		}
		throw new InvalidValueException(ERROR);
	}

}

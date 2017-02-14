/**
 * 
 */
package org.gcube.documentstore.records.implementation;

import java.io.Serializable;

import org.gcube.documentstore.exception.InvalidValueException;
import org.gcube.documentstore.records.Record;

/**
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 */
public interface FieldAction {

	/**
	 * Validate (and eventually convert) the value of the property identified by
	 * the key.
	 * @param key The key of the property
	 * @param value The value to be validated (and eventually converted) of the 
	 * property 
	 * @param record the record the property is attached 
	 * @return the validated (and eventually converted) value of the property
	 * @throws InvalidValueException if the validation or the eventual 
	 * conversion fails
	 */
	public Serializable validate(String key, Serializable value, Record record) throws InvalidValueException;
	
}

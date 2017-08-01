/**
 * 
 */
package org.gcube.accounting.datamodel.backwardcompatibility;

import java.io.Serializable;

import org.gcube.documentstore.exception.InvalidValueException;
import org.gcube.documentstore.records.Record;
import org.gcube.documentstore.records.implementation.FieldAction;

public class MoveToRecordTypeAction implements FieldAction {

	@Override
	public Serializable validate(String key, Serializable value, Record record) throws InvalidValueException {
		if(value instanceof String && value!= null && !((String) value).isEmpty()){
			record.setResourceProperty(Record.RECORD_TYPE, value);
		}
		return null;  //Returning null the initial key is removed from Record
	}

}
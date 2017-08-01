/**
 * 
 */
package org.gcube.accounting.datamodel.backwardcompatibility;

import java.io.Serializable;

import org.gcube.documentstore.exception.InvalidValueException;
import org.gcube.documentstore.records.Record;
import org.gcube.documentstore.records.implementation.FieldAction;

public class MoveToTaskWallDurationAction implements FieldAction {
	
	@Override
	public Serializable validate(String key, Serializable value, Record record) throws InvalidValueException  {
		return null; //Returning null the initial key is removed from Record
	}
	
}
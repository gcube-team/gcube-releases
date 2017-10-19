/**
 * 
 */
package org.gcube.accounting.datamodel.backwardcompatibility;

import java.io.Serializable;

import org.gcube.accounting.datamodel.UsageRecord;
import org.gcube.documentstore.exception.InvalidValueException;
import org.gcube.documentstore.records.Record;
import org.gcube.documentstore.records.implementation.FieldAction;
import org.gcube.documentstore.records.implementation.validations.validators.NotEmptyIfNotNullValidator;

public class MoveToConsumerIdAction implements FieldAction {
	
	@Override
	public Serializable validate(String key, Serializable value, Record record) throws InvalidValueException  {
		NotEmptyIfNotNullValidator neinnv = new NotEmptyIfNotNullValidator();
		value = neinnv.validate(key, value, record);
		record.setResourceProperty(UsageRecord.CONSUMER_ID, (String) value);
		return null;  //Returning null the initial key is removed from Record
	}
	
}
/**
 * 
 */
package org.gcube.accounting.datamodel.backwardcompatibility;

import java.io.Serializable;

import org.gcube.accounting.datamodel.UsageRecord;
import org.gcube.accounting.datamodel.UsageRecord.OperationResult;
import org.gcube.accounting.datamodel.validations.validators.ValidOperationResultValidator;
import org.gcube.documentstore.exception.InvalidValueException;
import org.gcube.documentstore.records.Record;
import org.gcube.documentstore.records.implementation.FieldAction;

/**
 * @author Luca Frosini (ISTI - CNR)
 *
 */
public class MoveToOperationResultAction implements FieldAction {
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Serializable validate(String key, Serializable value, Record record) throws InvalidValueException  {
		ValidOperationResultValidator vorv = new ValidOperationResultValidator();
		value = vorv.validate(key, value, record);
		record.setResourceProperty(UsageRecord.OPERATION_RESULT, (OperationResult) value);
		return null;  //Returning null the initial key is removed from Record
	}
	
}

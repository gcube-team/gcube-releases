/**
 * 
 */
package org.gcube.accounting.datamodel.validations.validators;

import java.io.Serializable;

import org.gcube.accounting.datamodel.basetypes.AbstractStorageUsageRecord;
import org.gcube.accounting.datamodel.basetypes.AbstractStorageUsageRecord.OperationType;
import org.gcube.documentstore.exception.InvalidValueException;
import org.gcube.documentstore.records.Record;
import org.gcube.documentstore.records.implementation.FieldAction;
import org.gcube.documentstore.records.implementation.validations.validators.ValidLongValidator;

/**
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 *
 */
public class FixDataVolumeSignAction implements FieldAction {
	
	protected Long checkIt(Long dataVolume, OperationType operationType){
		switch (operationType) {
			case CREATE:{
				dataVolume = (dataVolume > 0) ? dataVolume : -dataVolume; 
				break;
			}
			case READ:{
				dataVolume = (dataVolume > 0) ? dataVolume : -dataVolume;
				break;
			}
			case UPDATE:{
				break;
			}
			case DELETE:{
				dataVolume = (dataVolume < 0) ? dataVolume : -dataVolume; 
				break;
			}
			default:{
				break;
			}
		}
		
		return dataVolume;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Serializable validate(String key, Serializable value, Record record) throws InvalidValueException  {
		try {
			
			if(key.compareTo(AbstractStorageUsageRecord.DATA_VOLUME)==0){
				OperationType operationType = (OperationType) record.getResourceProperty(AbstractStorageUsageRecord.OPERATION_TYPE);
				if(operationType!=null){
					ValidLongValidator validLongValidator = new ValidLongValidator();
					value = validLongValidator.validate(key, value, record);
					Long dataVolume = new Long((Long) value);
					value = checkIt(dataVolume, operationType);
				}
			}
			
			if(key.compareTo(AbstractStorageUsageRecord.OPERATION_TYPE)==0){
				Long dataVolume = (Long) record.getResourceProperty(AbstractStorageUsageRecord.DATA_VOLUME);
				if(dataVolume!=null){
					ValidOperationTypeValidator v = new ValidOperationTypeValidator();
					value = v.validate(key, value, record);
					OperationType operationType = (OperationType) value;
					Long newDataVolume = checkIt(dataVolume, operationType);
					record.setResourceProperty(AbstractStorageUsageRecord.DATA_VOLUME, newDataVolume);
				}
			}
			
			
		}catch(InvalidValueException e){ }
		return value;
	}
}
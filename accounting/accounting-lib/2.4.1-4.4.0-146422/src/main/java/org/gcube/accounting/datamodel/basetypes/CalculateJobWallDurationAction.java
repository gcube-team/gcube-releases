/**
 * 
 */
package org.gcube.accounting.datamodel.basetypes;

import java.io.Serializable;

import org.gcube.documentstore.exception.InvalidValueException;
import org.gcube.documentstore.records.Record;
import org.gcube.documentstore.records.implementation.FieldAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
public class CalculateJobWallDurationAction implements FieldAction {
	
	private static final Logger logger = LoggerFactory.getLogger(CalculateJobWallDurationAction.class);
	
	@Override
	public Serializable validate(String key, Serializable value, Record record) throws InvalidValueException  {
		try {
			long wallDuration = ((AbstractJobUsageRecord) record).calculateWallDuration();
			if(key.compareTo(AbstractJobUsageRecord.DURATION)==0){
				logger.warn("{} is automatically computed using {} and {}. This invocation has the only effect of recalculating the value. Any provided value is ignored.", 
						AbstractJobUsageRecord.DURATION, AbstractJobUsageRecord.JOB_START_TIME, AbstractJobUsageRecord.JOB_END_TIME);
				value = wallDuration;
			}
		}catch(InvalidValueException e){ }
		return value;
	}
}
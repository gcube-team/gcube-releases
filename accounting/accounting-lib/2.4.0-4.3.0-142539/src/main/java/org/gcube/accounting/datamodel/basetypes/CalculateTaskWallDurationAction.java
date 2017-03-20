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
public class CalculateTaskWallDurationAction implements FieldAction {
	
	private static final Logger logger = LoggerFactory.getLogger(CalculateTaskWallDurationAction.class);
	
	@Override
	public Serializable validate(String key, Serializable value, Record record) throws InvalidValueException  {
		try {
			long wallDuration = ((AbstractTaskUsageRecord) record).calculateWallDuration();
			if(key.compareTo(AbstractTaskUsageRecord.WALL_DURATION)==0){
				logger.warn("{} is automatically computed using {} and {}. This invocation has the only effect of recalculating the value. Any provided value is ignored.", 
						AbstractTaskUsageRecord.WALL_DURATION, AbstractTaskUsageRecord.TASK_START_TIME, AbstractTaskUsageRecord.TASK_END_TIME);
				value = wallDuration;
			}else{
				record.setResourceProperty(AbstractTaskUsageRecord.WALL_DURATION, wallDuration);
			}
		}catch(InvalidValueException e){ }
		return value;
	}
}
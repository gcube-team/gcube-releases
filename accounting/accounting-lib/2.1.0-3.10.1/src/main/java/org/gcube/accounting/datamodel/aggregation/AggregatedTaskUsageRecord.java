/**
 * 
 */
package org.gcube.accounting.datamodel.aggregation;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Map;
import java.util.Set;

import org.gcube.accounting.datamodel.AggregatedUsageRecord;
import org.gcube.accounting.datamodel.basetypes.AbstractTaskUsageRecord;
import org.gcube.accounting.datamodel.usagerecords.TaskUsageRecord;
import org.gcube.documentstore.exception.InvalidValueException;
import org.gcube.documentstore.exception.NotAggregatableRecordsExceptions;
import org.gcube.documentstore.records.aggregation.AggregationUtility;

/**
 * This Class is for library internal use only
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 */
public class AggregatedTaskUsageRecord extends AbstractTaskUsageRecord implements AggregatedUsageRecord<AggregatedTaskUsageRecord, TaskUsageRecord> {
	
	/**
	 * Generated Serial version UID
	 */
	private static final long serialVersionUID = 7445526162102677455L;

	public AggregatedTaskUsageRecord(){
		super();
	}
	
	public AggregatedTaskUsageRecord(Map<String, ? extends Serializable> properties) throws InvalidValueException{
		super(properties);
	}
	
	// TODO
	public AggregatedTaskUsageRecord(TaskUsageRecord taskUsageRecord) throws InvalidValueException{
		throw new UnsupportedOperationException();
	}
	

	@Override
	public Set<String> getAggregatedFields() {
		return aggregatedFields;
	}
	
	@Override
	public int getOperationCount() {
		return super.getOperationCount();
	}
	
	@Override
	public void setOperationCount(int operationCount) throws InvalidValueException {
		super.setOperationCount(operationCount);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Calendar getStartTime() {
		return super.getStartTimeAsCalendar();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setStartTime(Calendar startTime) throws InvalidValueException {
		super.setStartTime(startTime);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Calendar getEndTime() {
		return super.getEndTimeAsCalendar();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setEndTime(Calendar endTime) throws InvalidValueException {
		super.setEndTime(endTime);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public AggregatedTaskUsageRecord aggregate(AggregatedTaskUsageRecord record)
			throws NotAggregatableRecordsExceptions {
		try {
			/* TODO
			AggregationUtility<AggregatedTaskUsageRecord> aggregationUtility = new AggregationUtility<AggregatedTaskUsageRecord>(this);
			aggregationUtility.aggregate(record);
		} catch(NotAggregatableRecordsExceptions e){
			throw e; */
		} catch(Exception ex){
			throw new NotAggregatableRecordsExceptions(ex);
		}
		//return this;
		
		throw new UnsupportedOperationException();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public AggregatedTaskUsageRecord aggregate(TaskUsageRecord record)
			throws NotAggregatableRecordsExceptions {
		try {
			return aggregate(new AggregatedTaskUsageRecord(record));
		} catch(NotAggregatableRecordsExceptions e){
			throw e;
		} catch(Exception ex){
			throw new NotAggregatableRecordsExceptions(ex);
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isAggregable(AggregatedTaskUsageRecord record) throws NotAggregatableRecordsExceptions {
		AggregationUtility<AggregatedTaskUsageRecord> aggregationUtility = new AggregationUtility<AggregatedTaskUsageRecord>(this);
		return aggregationUtility.isAggregable(record);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isAggregable(TaskUsageRecord record) throws NotAggregatableRecordsExceptions {
		try {
			return isAggregable(new AggregatedTaskUsageRecord(record));
		} catch(NotAggregatableRecordsExceptions e){
			throw e;
		} catch(Exception ex){
			throw new NotAggregatableRecordsExceptions(ex);
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Class<TaskUsageRecord> getAggregable() {
		return TaskUsageRecord.class;
	}

}

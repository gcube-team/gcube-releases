/**
 * 
 */
package org.gcube.accounting.datamodel.aggregation;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Map;
import java.util.Set;

import org.gcube.accounting.datamodel.AggregatedUsageRecord;
import org.gcube.accounting.datamodel.basetypes.AbstractJobUsageRecord;
import org.gcube.accounting.datamodel.usagerecords.JobUsageRecord;
import org.gcube.documentstore.exception.InvalidValueException;
import org.gcube.documentstore.exception.NotAggregatableRecordsExceptions;
import org.gcube.documentstore.records.aggregation.AggregationUtility;

/**
 * This Class is for library internal use only
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 */
public class AggregatedJobUsageRecord extends AbstractJobUsageRecord implements AggregatedUsageRecord<AggregatedJobUsageRecord, JobUsageRecord> {

	/**
	 * Generated Serial Version UID
	 */
	private static final long serialVersionUID = -3376423316219914682L;
	
	public AggregatedJobUsageRecord(){
		super();
	}
	
	public AggregatedJobUsageRecord(Map<String, ? extends Serializable> properties) throws InvalidValueException{
		super(properties);
	}
	
	// TODO
	public AggregatedJobUsageRecord(JobUsageRecord jobUsageRecord) throws InvalidValueException{
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
	public AggregatedJobUsageRecord aggregate(AggregatedJobUsageRecord record)
			throws NotAggregatableRecordsExceptions {
		try {
			/* TODO
			AggregationUtility<AggregatedJobUsageRecord> aggregationUtility = new AggregationUtility<AggregatedJobUsageRecord>(this);
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
	public AggregatedJobUsageRecord aggregate(JobUsageRecord record)
			throws NotAggregatableRecordsExceptions {
		try {
			return aggregate(new AggregatedJobUsageRecord(record));
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
	public boolean isAggregable(AggregatedJobUsageRecord record) throws NotAggregatableRecordsExceptions {
		AggregationUtility<AggregatedJobUsageRecord> aggregationUtility = new AggregationUtility<AggregatedJobUsageRecord>(this);
		return aggregationUtility.isAggregable(record);

	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isAggregable(JobUsageRecord record) throws NotAggregatableRecordsExceptions {
		try {
			return isAggregable(new AggregatedJobUsageRecord(record));
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
	public Class<JobUsageRecord> getAggregable() {
		return JobUsageRecord.class;
	}

}

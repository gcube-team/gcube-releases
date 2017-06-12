/**
 * 
 */
package org.gcube.accounting.datamodel.aggregation;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Map;

import org.gcube.accounting.datamodel.AggregatedUsageRecord;
import org.gcube.accounting.datamodel.basetypes.AbstractJobUsageRecord;
import org.gcube.accounting.datamodel.usagerecords.JobUsageRecord;
import org.gcube.documentstore.exception.InvalidValueException;
import org.gcube.documentstore.exception.NotAggregatableRecordsExceptions;
import org.gcube.documentstore.records.aggregation.AggregationUtility;
import org.gcube.documentstore.records.implementation.AggregatedField;
import org.gcube.documentstore.records.implementation.RequiredField;
import org.gcube.documentstore.records.implementation.validations.annotations.ValidLong;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * This Class is for library internal use only
 * @author Luca Frosini (ISTI - CNR)
 */
@JsonTypeName(value="JobUsageRecord")
public class AggregatedJobUsageRecord extends AbstractJobUsageRecord implements AggregatedUsageRecord<AggregatedJobUsageRecord, JobUsageRecord> {

	
	
	/**
	 * Generated Serial Version UID
	 */
	private static final long serialVersionUID = -3376423316219914682L;
	/*add a field max e min invocation*/
	@RequiredField @ValidLong @AggregatedField
	public static final String MAX_INVOCATION_TIME = "maxInvocationTime";
	@RequiredField @ValidLong @AggregatedField
	public static final String MIN_INVOCATION_TIME = "minInvocationTime";

	public AggregatedJobUsageRecord(){
		super();
	}

	public AggregatedJobUsageRecord(Map<String, ? extends Serializable> properties) throws InvalidValueException{
		super(properties);
	}

	// TODO	
	public AggregatedJobUsageRecord(JobUsageRecord record) throws InvalidValueException{
		//throw new UnsupportedOperationException();
		super(record.getResourceProperties());
		this.setOperationCount(1);
		long duration = record.getDuration();
		this.setMinInvocationTime(duration);
		this.setMaxInvocationTime(duration);
		Calendar creationTime = record.getCreationTime();
		this.setCreationTime(Calendar.getInstance());
		this.setStartTime(creationTime);
		this.setEndTime(creationTime);

	}
	@JsonIgnore
	@Override
	public int getOperationCount() {
		return super.getOperationCount();
	}

	@JsonIgnore
	@Override
	public void setOperationCount(int operationCount) throws InvalidValueException {
		super.setOperationCount(operationCount);
	}

	@JsonIgnore
	public long getMaxInvocationTime() {
		return (Long) this.resourceProperties.get(MAX_INVOCATION_TIME);
	}

	@JsonIgnore
	public void setMaxInvocationTime(long maxInvocationTime) throws InvalidValueException {
		super.setResourceProperty(MAX_INVOCATION_TIME, maxInvocationTime);
	}

	@JsonIgnore
	public long getMinInvocationTime() {
		return (Long) this.resourceProperties.get(MIN_INVOCATION_TIME);
	}

	@JsonIgnore
	public void setMinInvocationTime(long minInvocationTime) throws InvalidValueException {
		setResourceProperty(MIN_INVOCATION_TIME, minInvocationTime);
	}

	/**
	 * {@inheritDoc}
	 */
	@JsonIgnore
	@Override
	public Calendar getStartTime() {
		return super.getStartTimeAsCalendar();
	}

	/**
	 * {@inheritDoc}
	 */
	@JsonIgnore
	@Override
	public void setStartTime(Calendar startTime) throws InvalidValueException {
		super.setStartTime(startTime);
	}

	/**
	 * {@inheritDoc}
	 */
	@JsonIgnore
	@Override
	public Calendar getEndTime() {
		return super.getEndTimeAsCalendar();
	}

	/**
	 * {@inheritDoc}
	 */
	@JsonIgnore
	@Override
	public void setEndTime(Calendar endTime) throws InvalidValueException {
		super.setEndTime(endTime);
	}

	
	//Introduce for to serialize Java Object
	@JsonIgnore
	@Override
	public void setAggregate(Boolean aggregate) throws InvalidValueException {
		super.setAggregate(aggregate);
	}
	/**
	 * {@inheritDoc}
	 */
	@JsonIgnore
	@Override	
	public Boolean getAggregate() {
		return super.getAggregate();
	}
	//End Introduce for to serialize Java Object

	protected long durationWeightedAverage(AggregatedJobUsageRecord record) throws InvalidValueException{
		long thisDuration = this.getDuration() * this.getOperationCount();
		long recordDuration = record.getDuration() * record.getOperationCount();
		long totalOperationCount = this.getOperationCount() + record.getOperationCount();
		return (thisDuration + recordDuration) / totalOperationCount;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@JsonIgnore
	public AggregatedJobUsageRecord aggregate(AggregatedJobUsageRecord record)
			throws NotAggregatableRecordsExceptions {
		try {

			AggregationUtility<AggregatedJobUsageRecord> aggregationUtility = new AggregationUtility<AggregatedJobUsageRecord>(this);
			setDuration(durationWeightedAverage(record));
			long max = record.getMaxInvocationTime();
			if(max > this.getMaxInvocationTime()){
				this.setMaxInvocationTime(max);
			}

			long min = record.getMinInvocationTime();
			if(min < this.getMinInvocationTime()){
				this.setMinInvocationTime(min);
			}
			aggregationUtility.aggregate(record);
		} catch(NotAggregatableRecordsExceptions e){
			throw e; 
		} catch(Exception ex){
			throw new NotAggregatableRecordsExceptions(ex);
		}
		return this;
		//throw new UnsupportedOperationException();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@JsonIgnore
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
	@JsonIgnore
	public Class<JobUsageRecord> getAggregable() {
		return JobUsageRecord.class;
	}

	@Override
	protected void cleanExtraFields(){
		return;
	}
}

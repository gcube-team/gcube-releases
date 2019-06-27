/**
 * 
 */
package org.gcube.accounting.datamodel.aggregation;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Map;

import org.gcube.accounting.datamodel.AggregatedUsageRecord;
import org.gcube.accounting.datamodel.basetypes.AbstractStorageStatusRecord;
import org.gcube.accounting.datamodel.usagerecords.StorageStatusRecord;
import org.gcube.documentstore.exception.InvalidValueException;
import org.gcube.documentstore.exception.NotAggregatableRecordsExceptions;
import org.gcube.documentstore.records.aggregation.AggregationUtility;
import org.gcube.documentstore.records.implementation.AggregatedField;

import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * This Class is for library internal use only
 * @author Alessandro Pieve (ISTI - CNR) alessandro.pieve@isti.cnr.it
 */
@JsonTypeName(value="StorageStatusRecord")
public class AggregatedStorageStatusRecord extends AbstractStorageStatusRecord implements AggregatedUsageRecord<AggregatedStorageStatusRecord, StorageStatusRecord> {
	
	/**
	 * Generated Serial Version UID
	 */
	private static final long serialVersionUID = 4683337274367137236L;
	
	@AggregatedField
	public static final String DATA_VOLUME = AbstractStorageStatusRecord.DATA_VOLUME;
	
	@AggregatedField
	public static final String DATA_COUNT = AbstractStorageStatusRecord.DATA_COUNT;

	
	public AggregatedStorageStatusRecord() {
		super();
	}
	
	public AggregatedStorageStatusRecord(Map<String, ? extends Serializable> properties) throws InvalidValueException{
		super(properties);
	}

	public AggregatedStorageStatusRecord(StorageStatusRecord record) throws InvalidValueException{
		super(record.getResourceProperties());
		this.setOperationCount(1);
		Calendar creationTime = record.getCreationTime();
		this.setCreationTime(Calendar.getInstance());
		this.setStartTime(creationTime);
		this.setEndTime(creationTime);
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

	@Override
	public void setAggregated(Boolean aggregate) throws InvalidValueException {
		super.setAggregated(aggregate);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Boolean isAggregated() {
		return super.isAggregated();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public AggregatedStorageStatusRecord aggregate(
			AggregatedStorageStatusRecord record)
			throws NotAggregatableRecordsExceptions {
		try {
			AggregationUtility<AggregatedStorageStatusRecord> aggregationUtility = new AggregationUtility<AggregatedStorageStatusRecord>(this);
			aggregationUtility.aggregate(record);
			this.setDataVolume(record.getDataVolume());
			this.setDataCount(record.getDataCount());

		}catch(NotAggregatableRecordsExceptions e){
			throw e;
		} catch(Exception ex){
			throw new NotAggregatableRecordsExceptions(ex);
		}
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public AggregatedStorageStatusRecord aggregate(StorageStatusRecord record)
			throws NotAggregatableRecordsExceptions {
		try {
			return aggregate(new AggregatedStorageStatusRecord(record));
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
	public boolean isAggregable(AggregatedStorageStatusRecord record)
			throws NotAggregatableRecordsExceptions {
		AggregationUtility<AggregatedStorageStatusRecord> aggregationUtility = new AggregationUtility<AggregatedStorageStatusRecord>(this);
		return aggregationUtility.isAggregable(record);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isAggregable(StorageStatusRecord record)
			throws NotAggregatableRecordsExceptions {
		try {
			return isAggregable(new AggregatedStorageStatusRecord(record));
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
	public Class<StorageStatusRecord> getAggregable() {
		return StorageStatusRecord.class;
	}
	
}

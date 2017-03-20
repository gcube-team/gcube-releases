/**
 * 
 */
package org.gcube.accounting.datamodel.aggregation;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Map;

import org.gcube.accounting.datamodel.AggregatedUsageRecord;
import org.gcube.accounting.datamodel.basetypes.AbstractStorageUsageRecord;
import org.gcube.accounting.datamodel.usagerecords.StorageUsageRecord;
import org.gcube.documentstore.exception.InvalidValueException;
import org.gcube.documentstore.exception.NotAggregatableRecordsExceptions;
import org.gcube.documentstore.records.aggregation.AggregationUtility;
import org.gcube.documentstore.records.implementation.AggregatedField;

/**
 * This Class is for library internal use only
 * @author Luca Frosini (ISTI - CNR)
 */
public class AggregatedStorageUsageRecord extends AbstractStorageUsageRecord implements AggregatedUsageRecord<AggregatedStorageUsageRecord, StorageUsageRecord> {

	/**
	 * Generated Serial Version UID
	 */
	private static final long serialVersionUID = 1082525518686785682L;
	
	@AggregatedField
	public static final String DATA_VOLUME = AbstractStorageUsageRecord.DATA_VOLUME;

	
	public AggregatedStorageUsageRecord() {
		super();
	}
	
	public AggregatedStorageUsageRecord(Map<String, ? extends Serializable> properties) throws InvalidValueException{
		super(properties);
	}

	public AggregatedStorageUsageRecord(StorageUsageRecord record) throws InvalidValueException{
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

	/**
	 * {@inheritDoc}
	 */
	@Override
	public AggregatedStorageUsageRecord aggregate(
			AggregatedStorageUsageRecord record)
			throws NotAggregatableRecordsExceptions {
		try {
			AggregationUtility<AggregatedStorageUsageRecord> aggregationUtility = new AggregationUtility<AggregatedStorageUsageRecord>(this);
			aggregationUtility.aggregate(record);
			this.setDataVolume(this.getDataVolume() + record.getDataVolume());
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
	public AggregatedStorageUsageRecord aggregate(StorageUsageRecord record)
			throws NotAggregatableRecordsExceptions {
		try {
			return aggregate(new AggregatedStorageUsageRecord(record));
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
	public boolean isAggregable(AggregatedStorageUsageRecord record)
			throws NotAggregatableRecordsExceptions {
		AggregationUtility<AggregatedStorageUsageRecord> aggregationUtility = new AggregationUtility<AggregatedStorageUsageRecord>(this);
		return aggregationUtility.isAggregable(record);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isAggregable(StorageUsageRecord record)
			throws NotAggregatableRecordsExceptions {
		try {
			return isAggregable(new AggregatedStorageUsageRecord(record));
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
	public Class<StorageUsageRecord> getAggregable() {
		return StorageUsageRecord.class;
	}
	
}

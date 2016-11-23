/**
 * 
 */
package org.gcube.accounting.datamodel.aggregation;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Map;

import org.gcube.accounting.datamodel.AggregatedUsageRecord;
import org.gcube.accounting.datamodel.basetypes.AbstractPortletUsageRecord;
import org.gcube.accounting.datamodel.usagerecords.PortletUsageRecord;
import org.gcube.documentstore.exception.InvalidValueException;
import org.gcube.documentstore.exception.NotAggregatableRecordsExceptions;
import org.gcube.documentstore.records.aggregation.AggregationUtility;

/**
 * This Class is for library internal use only
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 */
public class AggregatedPortletUsageRecord extends AbstractPortletUsageRecord implements AggregatedUsageRecord<AggregatedPortletUsageRecord, PortletUsageRecord> {

	
	/**
	 * Generated Serial version UID
	 */
	private static final long serialVersionUID = 7445526162102677455L;

	public AggregatedPortletUsageRecord(){
		super();
	}
	
	public AggregatedPortletUsageRecord(Map<String, ? extends Serializable> properties) throws InvalidValueException{
		super(properties);
	}
	
	// TODO
	public AggregatedPortletUsageRecord(PortletUsageRecord portletUsageRecord) throws InvalidValueException {
		throw new UnsupportedOperationException();
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
	public AggregatedPortletUsageRecord aggregate(
			AggregatedPortletUsageRecord record)
			throws NotAggregatableRecordsExceptions {
		try {
			/* TODO
			AggregationUtility<AggregatedPortletUsageRecord> aggregationUtility = new AggregationUtility<AggregatedPortletUsageRecord>(this);
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
	public AggregatedPortletUsageRecord aggregate(PortletUsageRecord record)
			throws NotAggregatableRecordsExceptions {
		try {
			return aggregate(new AggregatedPortletUsageRecord(record));
		} catch (InvalidValueException e) {
			throw new NotAggregatableRecordsExceptions(e.getCause());
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isAggregable(AggregatedPortletUsageRecord record) throws NotAggregatableRecordsExceptions {
		AggregationUtility<AggregatedPortletUsageRecord> aggregationUtility = new AggregationUtility<AggregatedPortletUsageRecord>(this);
		return aggregationUtility.isAggregable(record);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isAggregable(PortletUsageRecord record) throws NotAggregatableRecordsExceptions {
		try {
			return isAggregable(new AggregatedPortletUsageRecord(record));
		} catch (InvalidValueException e) {
			throw new NotAggregatableRecordsExceptions(e.getCause());
		}
	}
	
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Class<PortletUsageRecord> getAggregable() {
		return PortletUsageRecord.class;
	}

}

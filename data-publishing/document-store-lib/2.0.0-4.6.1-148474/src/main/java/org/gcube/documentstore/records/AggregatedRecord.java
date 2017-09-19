/**
 * 
 */
package org.gcube.documentstore.records;

import java.util.Calendar;
import java.util.Set;

import org.gcube.documentstore.exception.InvalidValueException;
import org.gcube.documentstore.exception.NotAggregatableRecordsExceptions;
import org.gcube.documentstore.records.implementation.AggregatedField;
import org.gcube.documentstore.records.implementation.RequiredField;


/**
 * @author Luca Frosini (ISTI - CNR)
 */

public interface AggregatedRecord<A extends AggregatedRecord<A,R>, R extends Record> extends Record {
	
	/**
	 * KEY : Indicate that this {@link Record} is an aggregation 
	 */
	@RequiredField @AggregatedField	
	public static final String AGGREGATED = "aggregated";
	
	/**
	 * KEY : Indicate The Number of {@link AggregatedRecord}
	 */
	@RequiredField @AggregatedField
	public static final String OPERATION_COUNT = "operationCount";
	
	/**
	 * KEY : Represent the left end of the time interval covered by this 
	 * {@link AggregatedRecord}. The value will be recorded in UTC milliseconds
	 * from the epoch.
	 */
	@RequiredField @AggregatedField
	public static final String START_TIME = "startTime";
	
	/**
	 * KEY : Represent the right end of the time interval covered by this
	 * {@link AggregatedRecord}. The value will be recorded in UTC milliseconds
	 * from the epoch.
	 */
	@RequiredField @AggregatedField
	public static final String END_TIME = "endTime";
	
	/**
	 * @return a Set containing the keys of aggregated fields
	 * The returned Set MUST be a copy of the internal representation. 
	 * Any modification to the returned Set MUST not affect the object
	 */
	public Set<String> getAggregatedFields();
	
	public int getOperationCount();
	
	public void setOperationCount(int operationCount) throws InvalidValueException;

	public Calendar getStartTime();
	
	public void setStartTime(Calendar startTime) throws InvalidValueException;
	
	public Calendar getEndTime();
	
	public void setEndTime(Calendar endTime) throws InvalidValueException;
	
	public A aggregate(A record) throws NotAggregatableRecordsExceptions;
	
	public A aggregate(R record) throws NotAggregatableRecordsExceptions;
	
	public boolean isAggregable(A record) throws NotAggregatableRecordsExceptions;
	
	public boolean isAggregable(R record) throws NotAggregatableRecordsExceptions;
	
	public Class<R> getAggregable();
	
}

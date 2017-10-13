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
import org.gcube.documentstore.records.implementation.validations.annotations.ValidBoolean;
import org.gcube.documentstore.records.implementation.validations.annotations.ValidInteger;
import org.gcube.documentstore.records.implementation.validations.annotations.ValidLong;

import com.fasterxml.jackson.annotation.JsonIgnore;


/**
 * @author Luca Frosini (ISTI - CNR)
 */
public interface AggregatedRecord<A extends AggregatedRecord<A,R>, R extends Record> extends Record {
	
	/**
	 * KEY : Indicate that this {@link Record} is an aggregation 
	 */
	@RequiredField @AggregatedField	@ValidBoolean
	public static final String AGGREGATED = "aggregated";
	
	/**
	 * KEY : Indicate The Number of {@link AggregatedRecord}
	 */
	@RequiredField @AggregatedField @ValidInteger
	public static final String OPERATION_COUNT = "operationCount";
	
	/**
	 * KEY : Represent the left end of the time interval covered by this 
	 * {@link AggregatedRecord}. The value will be recorded in UTC milliseconds
	 * from the epoch.
	 */
	@RequiredField @AggregatedField @ValidLong
	public static final String START_TIME = "startTime";
	
	/**
	 * KEY : Represent the right end of the time interval covered by this
	 * {@link AggregatedRecord}. The value will be recorded in UTC milliseconds
	 * from the epoch.
	 */
	@RequiredField @AggregatedField @ValidLong
	public static final String END_TIME = "endTime";
	
	/**
	 * @return a Set containing the keys of aggregated fields
	 * The returned Set MUST be a copy of the internal representation. 
	 * Any modification to the returned Set MUST not affect the object
	 */
	@JsonIgnore
	public Set<String> getAggregatedFields();
	
	public int getOperationCount();
	
	public void setOperationCount(int operationCount) throws InvalidValueException;
	
	@JsonIgnore
	public Calendar getStartTime();
	
	@JsonIgnore
	public void setStartTime(Calendar startTime) throws InvalidValueException;
	
	@JsonIgnore
	public Calendar getEndTime();
	
	@JsonIgnore
	public void setEndTime(Calendar endTime) throws InvalidValueException;
	
	public A aggregate(A record) throws NotAggregatableRecordsExceptions;
	
	public A aggregate(R record) throws NotAggregatableRecordsExceptions;
	
	@JsonIgnore
	public boolean isAggregable(A record) throws NotAggregatableRecordsExceptions;
	
	@JsonIgnore
	public boolean isAggregable(R record) throws NotAggregatableRecordsExceptions;
	
	@JsonIgnore
	public Class<R> getAggregable();
	
}

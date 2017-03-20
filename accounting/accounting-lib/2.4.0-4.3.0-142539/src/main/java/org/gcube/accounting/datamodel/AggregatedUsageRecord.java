/**
 * 
 */
package org.gcube.accounting.datamodel;

import org.gcube.documentstore.records.AggregatedRecord;
import org.gcube.documentstore.records.implementation.validations.annotations.ValidBoolean;
import org.gcube.documentstore.records.implementation.validations.annotations.ValidInteger;
import org.gcube.documentstore.records.implementation.validations.annotations.ValidLong;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
public interface AggregatedUsageRecord<A extends AggregatedUsageRecord<A,U>, U extends UsageRecord> extends AggregatedRecord<A,U> {
	
	@ValidBoolean
	public static final String AGGREGATED = AggregatedRecord.AGGREGATED;
	
	@ValidInteger
	public static final String OPERATION_COUNT = AggregatedRecord.OPERATION_COUNT;
	
	@ValidLong
	public static final String START_TIME = AggregatedRecord.START_TIME;
	
	@ValidLong
	public static final String END_TIME = AggregatedRecord.END_TIME;
	
}

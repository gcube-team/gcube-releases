/**
 * 
 */
package org.gcube.documentstore.records.aggregation;

import java.util.Calendar;

import org.gcube.documentstore.persistence.PersistenceExecutor;

/**
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 *
 * This class implements a Simple Buffer with timeout strategy.
 * It buffer a predefined number of Records before invoking a persistence.
 */
public class BufferAggregationScheduler extends AggregationScheduler {

	/**
	 * Define the MAX number of Record to buffer.
	 * TODO Get from configuration
	 */
	protected final static int MAX_RECORDS_NUMBER = 15;
	
	/**
	 * The Max amount of time elapsed form last record before after that
	 * the buffered record are persisted even if  
	 * TODO Get from configuration
	 */
	protected final static long OLD_RECORD_MAX_TIME_ELAPSED = 1000*60*5; // 5 min  
	
	protected boolean firstOfBuffer;
	protected long firstBufferedTime;
		
	public BufferAggregationScheduler(PersistenceExecutor persistenceExecutor){
		super(persistenceExecutor);
		this.firstOfBuffer = true;
	}

	@Override
	protected void schedulerSpecificClear(){
		firstOfBuffer = true;
	}
	
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isTimeToPersist(){
		long now = Calendar.getInstance().getTimeInMillis();
		
		if(firstOfBuffer){
			firstOfBuffer = false;
			firstBufferedTime = now;
		}
		
		if(totalBufferedRecords >= MAX_RECORDS_NUMBER){
			return true;
		}
		
		if((now - firstBufferedTime) >= OLD_RECORD_MAX_TIME_ELAPSED){
			return true;
		}
		
		return false;
	}
	
	
}

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
	protected boolean isTimeToPersist(int maxRecordNumber,	long oldRecordMaxTime) {


		long now = Calendar.getInstance().getTimeInMillis();
		if(firstOfBuffer){
			firstOfBuffer = false;
			firstBufferedTime = now;
		}
		if(totalBufferedRecords >= maxRecordNumber){
			logger.trace("Time persist from maxRecordNumber:"+maxRecordNumber+" max totalBufferedRecords:"+totalBufferedRecords);
			return true;
		}

		if((now - firstBufferedTime) >= oldRecordMaxTime){
			logger.trace("Time persist from oldRecordMaxTime:"+oldRecordMaxTime+" firstBufferedTime:"+firstBufferedTime);
			return true;
		}

		return false;
	}



}

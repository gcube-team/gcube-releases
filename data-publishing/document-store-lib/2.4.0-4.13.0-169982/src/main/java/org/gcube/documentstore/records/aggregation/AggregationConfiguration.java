package org.gcube.documentstore.records.aggregation;

import java.util.concurrent.TimeUnit;

/**
 * @author Alessandro Pieve (ISTI - CNR)
 * @author Luca Frosini (ISTI - CNR)
 */
public class AggregationConfiguration {

	/**
	 * Define the default MAX number of Record to buffer.
	 */
	public static final int DEFAULT_MAX_RECORDS_NUMBER = 1000;

	/**
	 * Define the default Max amount of time elapsed from the time the first
	 * record where buffered
	 */
	public static final long DEFAULT_MAX_TIME_ELAPSED = 1000 * 60 * 30; // 30 minutes in millisec

	public static final int DEFAULT_INITIAL_DELAY = 30; // in TIME_UNIT

	public static final int DEFAULT_DELAY = 30; // in TIME_UNIT
	
	public static final TimeUnit TIME_UNIT = TimeUnit.MINUTES;

	public static AggregationConfiguration getDefaultConfiguration() {
		return new AggregationConfiguration(DEFAULT_INITIAL_DELAY, DEFAULT_DELAY, DEFAULT_MAX_RECORDS_NUMBER,
				DEFAULT_MAX_TIME_ELAPSED);
	}

	protected int initialDelay;
	protected int delay;
	protected int maxRecordsNumber;
	protected long maxTimeElapsed;

	public AggregationConfiguration(int initialDelay, int delay, int maxRecordsNumber, long maxTimeElapsed) {
		super();
		this.initialDelay = initialDelay;
		this.delay = delay;
		this.maxRecordsNumber = maxRecordsNumber;
		this.maxTimeElapsed = maxTimeElapsed;
	}

	public int getInitialDelay() {
		if(initialDelay > 0){
			return initialDelay;
		}else{
			return DEFAULT_INITIAL_DELAY;
		}
	}

	public void setInitialDelay(int initialDelay) {
		this.initialDelay = initialDelay;
	}

	public int getDelay() {
		if(delay > 0){
			return delay;
		}else{
			return DEFAULT_DELAY;
		}
	}

	public void setDelay(int delay) {
		this.delay = delay;
	}

	public int getMaxRecordsNumber() {
		if(maxRecordsNumber > 0){
			return maxRecordsNumber;
		}else{
			return DEFAULT_MAX_RECORDS_NUMBER;
		}
	}

	public void setMaxRecordsNumber(int maxRecordsNumber) {
		this.maxRecordsNumber = maxRecordsNumber;
	}

	public long getMaxTimeElapsed() {
		if(maxTimeElapsed > 0){
			return maxTimeElapsed;
		}else{
			return DEFAULT_MAX_TIME_ELAPSED;
		}
	}

	public void setMaxTimeElapsed(long maxTimeElapsed) {
		this.maxTimeElapsed = maxTimeElapsed;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + delay;
		result = prime * result + initialDelay;
		result = prime * result + maxRecordsNumber;
		result = prime * result + (int) (maxTimeElapsed ^ (maxTimeElapsed >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AggregationConfiguration other = (AggregationConfiguration) obj;
		if (delay != other.delay)
			return false;
		if (initialDelay != other.initialDelay)
			return false;
		if (maxRecordsNumber != other.maxRecordsNumber)
			return false;
		if (maxTimeElapsed != other.maxTimeElapsed)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "AggregationConfig [initialDelay=" + initialDelay + ", delay=" + delay + ", maxRecordsNumber="
				+ maxRecordsNumber + ", maxTimeElapsed=" + maxTimeElapsed + "]";
	}

}

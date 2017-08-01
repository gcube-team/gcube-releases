package org.gcube.documentstore.records.aggregation;

/**
 * @author Alessandro Pieve (ISTI - CNR)
 */
public class AggregationConfig {
	/**
	 * Define the MAX number of Record to buffer. TODO Get from configuration
	 */
	protected static final int MAX_RECORDS_NUMBER = 1000;
	/**
	 * The Max amount of time elapsed form last record before after that the
	 * buffered record are persisted even if TODO Get from configuration
	 */
	protected static final long OLD_RECORD_MAX_TIME_ELAPSED = 1000 * 60 * 30; // 30 min
	public static final int INITIAL_DELAY = 30;
	public static final int DELAY = 30;
	
	public static AggregationConfig getDefaultConfiguration(){
		return new AggregationConfig(INITIAL_DELAY, DELAY, MAX_RECORDS_NUMBER, OLD_RECORD_MAX_TIME_ELAPSED);
	}
	
	private int initialDelaySet;
	private int delaySet;
	private int maxRecordsNumberSet;
	private long oldRecordMaxTimeElapsedSet;
		

	public AggregationConfig(Integer initialDelaySet, Integer delaySet,
			int maxRecordsNumberSet, long oldRecordMaxTimeElapsedSet) {
		super();
		this.initialDelaySet = initialDelaySet;
		this.delaySet = delaySet;
		this.maxRecordsNumberSet = maxRecordsNumberSet;
		this.oldRecordMaxTimeElapsedSet = oldRecordMaxTimeElapsedSet;
	}
	
	public Integer getInitialDelaySet() {
		return initialDelaySet;
	}
	public void setInitialDelaySet(int initialDelaySet) {
		this.initialDelaySet = initialDelaySet;
	}
	public Integer getDelaySet() {
		return delaySet;
	}
	public void setDelaySet(int delaySet) {
		this.delaySet = delaySet;
	}
	public int getMaxRecordsNumberSet() {
		return maxRecordsNumberSet;
	}
	public void setMaxRecordsNumberSet(int maxRecordsNumberSet) {
		this.maxRecordsNumberSet = maxRecordsNumberSet;
	}
	public long getOldRecordMaxTimeElapsedSet() {
		return oldRecordMaxTimeElapsedSet;
	}
	public void setOldRecordMaxTimeElapsedSet(long oldRecordMaxTimeElapsedSet) {
		this.oldRecordMaxTimeElapsedSet = oldRecordMaxTimeElapsedSet;
	}

	
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + delaySet;
		result = prime * result + initialDelaySet;
		result = prime * result + maxRecordsNumberSet;
		result = prime
				* result
				+ (int) (oldRecordMaxTimeElapsedSet ^ (oldRecordMaxTimeElapsedSet >>> 32));
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
		AggregationConfig other = (AggregationConfig) obj;
		if (delaySet != other.delaySet)
			return false;
		if (initialDelaySet != other.initialDelaySet)
			return false;
		if (maxRecordsNumberSet != other.maxRecordsNumberSet)
			return false;
		if (oldRecordMaxTimeElapsedSet != other.oldRecordMaxTimeElapsedSet)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "AggregationConfig [initialDelaySet=" + initialDelaySet
				+ ", delaySet=" + delaySet + ", maxRecordsNumberSet="
				+ maxRecordsNumberSet + ", oldRecordMaxTimeElapsedSet="
				+ oldRecordMaxTimeElapsedSet + "]";
	}

}

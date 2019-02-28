package org.gcube.resource.management.quota.library.quotalist;

import org.gcube.accounting.analytics.TemporalConstraint.AggregationMode;


public enum TimeInterval {
	DAILY(1, AggregationMode.DAILY),
	MONTHLY(31,AggregationMode.MONTHLY),
	YEARLY(365,AggregationMode.YEARLY),
	FOREVER(0,null);
	private int value;
	private AggregationMode mode;
	private TimeInterval(int value,AggregationMode mode){
		this.value=value;
		this.mode=mode;
	}
	
	public int getValue(){
		return this.value;
	}
	public AggregationMode getAggregationMode(){
		return this.mode;
	}
}
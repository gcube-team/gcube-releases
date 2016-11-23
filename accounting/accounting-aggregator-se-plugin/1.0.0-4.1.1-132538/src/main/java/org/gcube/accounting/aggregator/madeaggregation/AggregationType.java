package org.gcube.accounting.aggregator.madeaggregation;

import java.util.Calendar;
/**
 * @author Alessandro Pieve (ISTI - CNR)
 *
 */
public enum AggregationType {

	HOURLY(Calendar.MINUTE, 60, "yyyy,M,d,H,m"),
	DAILY(Calendar.HOUR, 24,"yyyy,M,d,H"),
	MONTHLY(Calendar.DAY_OF_MONTH,31, "yyyy,M,d"),
	YEARLY(Calendar.MONTH,12,"yyyy,M");
	
	private int calendarField;
	private int multiplierFactor;
	
	private String dateformat;
	
	private AggregationType(int calendarField, int multipliertFactor, String dateFormat) {
		this.calendarField = calendarField;
		this.multiplierFactor = multipliertFactor;
		this.dateformat=dateFormat;
	}

	public int getCalendarField() {
		return calendarField;
	}

	public int getMultiplierFactor() {
		return multiplierFactor;
	}

	public String getDateformat() {
		return dateformat;
	}
	
	
	
}

package org.gcube.dataharvest.utils;

import java.text.DateFormat;
import java.util.Calendar;

/**
 * @author Luca Frosini  (ISTI - CNR)
 */
public enum AggregationType {
	
	DAILY(Calendar.DAY_OF_MONTH, "yyyy-MM-dd", 7),
	MONTHLY(Calendar.MONTH, "yyyy-MM", 3),
	YEARLY(Calendar.YEAR, "yyyy", 3);
	
	public static final String DATE_SEPARATOR = "-";
	
	private final int calendarField;
	
	private final String dateFormatPattern;
	private final DateFormat dateFormat;
	
	private final int notAggregableBefore;
	
	
	
	private AggregationType(int calendarField, String dateFormatPattern, int notAggregableBefore) {
		this.calendarField = calendarField;
		this.dateFormatPattern=dateFormatPattern;
		this.dateFormat = DateUtils.getUTCDateFormat(dateFormatPattern);
		this.notAggregableBefore = notAggregableBefore;
	}

	public int getCalendarField() {
		return calendarField;
	}
	
	public String getDateFormatPattern() {
		return dateFormatPattern;
	}
	
	public DateFormat getDateFormat() {
		return dateFormat;
	}

	public int getNotAggregableBefore(){
		return notAggregableBefore;
	}
	
}

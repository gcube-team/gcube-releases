package org.gcube.dataharvest.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Eric Perrone (ISTI - CNR)
 * @author Luca Frosini (ISTI - CNR)
 */
public class DateUtils {
	
	private static Logger logger = LoggerFactory.getLogger(DateUtils.class);
	
	public static TimeZone UTC_TIMEZONE = TimeZone.getTimeZone("UTC");
	public static final String DATETIME_PATTERN = "yyyy-MM-dd HH:mm:ss.SSS Z";
	public static final DateFormat DEFAULT_DATE_FORMAT;
	
	public static final String LAUNCH_DATE_FORMAT_PATTERN = "yyyy-MM-dd";
	public static final DateFormat LAUNCH_DATE_FORMAT;
	
	public static final String UTC_DATE_FORMAT_PATTERN = "yyyy-MM-dd Z";
	public static final DateFormat UTC_DATE_FORMAT;
	public static final String UTC = "+0000";
	
	static {
		DEFAULT_DATE_FORMAT = getUTCDateFormat(DATETIME_PATTERN);
		LAUNCH_DATE_FORMAT = getUTCDateFormat(LAUNCH_DATE_FORMAT_PATTERN);
		UTC_DATE_FORMAT = getUTCDateFormat(UTC_DATE_FORMAT_PATTERN);
	}
	
	public static DateFormat getUTCDateFormat(String pattern) {
		DateFormat dateFormat = new SimpleDateFormat(pattern);
		dateFormat.setTimeZone(UTC_TIMEZONE);
		return dateFormat;
	}
	
	public static Calendar getUTCCalendarInstance() {
		return Calendar.getInstance(UTC_TIMEZONE);
	}
	
	public static Calendar getPreviousPeriod(AggregationType aggregationType) {
		Calendar now = getUTCCalendarInstance();
		switch(aggregationType) {
			
			case YEARLY:
				now.add(Calendar.YEAR, -1);
				now.set(Calendar.MONTH, Calendar.JANUARY);
				now.set(Calendar.DAY_OF_MONTH, 1);
				break;
			
			case MONTHLY:
				now.add(Calendar.MONTH, -1);
				now.set(Calendar.DAY_OF_MONTH, 1);
				break;
			
			case DAILY:
				now.add(Calendar.DAY_OF_MONTH, -1);
				break;
			
			default:
				break;
		}
		
		now.set(Calendar.HOUR_OF_DAY, 0);
		now.set(Calendar.MINUTE, 0);
		now.set(Calendar.SECOND, 0);
		now.set(Calendar.MILLISECOND, 0);
		
		return now;
		
	}
	
	public static Calendar getStartCalendar(int year, int month, int day) {
		Calendar aggregationStartCalendar = getUTCCalendarInstance();
		aggregationStartCalendar.set(Calendar.YEAR, year);
		aggregationStartCalendar.set(Calendar.MONTH, month);
		aggregationStartCalendar.set(Calendar.DAY_OF_MONTH, day);
		aggregationStartCalendar.set(Calendar.HOUR_OF_DAY, 0);
		aggregationStartCalendar.set(Calendar.MINUTE, 0);
		aggregationStartCalendar.set(Calendar.SECOND, 0);
		aggregationStartCalendar.set(Calendar.MILLISECOND, 0);
		
		logger.debug("{}", DEFAULT_DATE_FORMAT.format(aggregationStartCalendar.getTime()));
		
		return aggregationStartCalendar;
	}
	
	public static Date getEndDateFromStartDate(AggregationType aggregationType, Date startDate, int offset) {
		Calendar aggregationEndDate = getUTCCalendarInstance();
		aggregationEndDate.setTimeInMillis(startDate.getTime());
		aggregationEndDate.add(aggregationType.getCalendarField(), offset);
		aggregationEndDate.add(Calendar.MILLISECOND, -1);
		return aggregationEndDate.getTime();
	}
	
	public static Calendar dateToCalendar(Date date) {
		Calendar calendar = DateUtils.getUTCCalendarInstance();
		calendar.setTime(date);
		return calendar;
	}
	
	/* OLD functions of Eric Perrone (ISTI - CNR) */
	
	public static String format(Date date) {
		return DateUtils.LAUNCH_DATE_FORMAT.format(date);
	}
	
	public static String format(Calendar calendar) {
		return format(calendar.getTime());
	}
	
	
	public static String dateToStringWithTZ(Date date) {
		DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		return formatter.format(date) + "Z";
	}
}

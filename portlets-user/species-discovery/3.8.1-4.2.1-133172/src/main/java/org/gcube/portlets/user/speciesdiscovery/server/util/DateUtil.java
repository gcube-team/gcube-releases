package org.gcube.portlets.user.speciesdiscovery.server.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.apache.log4j.Logger;
import org.gcube.portlets.user.speciesdiscovery.client.ConstantsSpeciesDiscovery;
import org.joda.time.DateTime;
import org.joda.time.Days;

public class DateUtil {
	
	public static enum TIMETYPE{DAYS, HOURS, MINUTES, SECONDS};
	
	public static Logger logger = Logger.getLogger(DateUtil.class);
	
	public static final String DATE_TIME_FORMAT = "yyyy.MM.dd 'at' HH:mm:ss";

	
	public static final String DATE_TIME_FORMAT_2 = "dd-MM, yyyy HH:mm:ss z";
	
	
	public static Date toDate(Calendar calendar)
	{
		if (calendar == null) return new Date(0);
		return calendar.getTime();

	}
	
	public static String toDateFormatToString(Calendar calendar){
		
		SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_TIME_FORMAT);
		
		Date resultdate = getDate(calendar);
		
		return dateFormat.format(resultdate);
	}
	
	
	public static Date toDateFormat(Calendar calendar){
		
		SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_TIME_FORMAT);
		
		Date resultdate = getDate(calendar);
		
		try {
			
			resultdate = dateFormat.parse(dateFormat.format(resultdate));
			
		} catch (ParseException e) {
			logger.error(e.getMessage());
			resultdate = new Date(0);
		}
		
		return resultdate;
	}
	
	
	public static Date getDateFormat(Calendar calendar){
		
		SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_TIME_FORMAT);
		Date resultdate = null;
		
		if (calendar == null)
			resultdate = new Date(0);
		else
			resultdate = getDate(calendar);
		
		try {
			
			resultdate = dateFormat.parse(dateFormat.format(resultdate));
			
		} catch (ParseException e) {
			logger.error(e.getMessage());
			resultdate = new Date(0);
		}
		
		return resultdate;
	}
	
	
	public static Date getDate(Calendar calendar) {

		Date resultdate = null;

		if (calendar == null)
			resultdate = new Date(0);
		else
			resultdate = new Date(calendar.getTimeInMillis());

		return resultdate;

	}
	
	public static Date stringToDateFormat(String date){
		
		SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_TIME_FORMAT);
		Date resultdate = null;
		
		try {
			resultdate =  dateFormat.parse(date);
		} catch (ParseException e) {
			logger.error(e.getMessage());
			resultdate = new Date(0);
		}
		
		return resultdate;
	}
	
	
	public static String dateToDateFormatString(Date date){
		
		SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_TIME_FORMAT);
		String dateString = null;
		
		if(date!=null)
			dateString =  dateFormat.format(date);
		else
			dateString = dateFormat.format(new Date(0));

		return dateString;
	}
	
	public static Date millisecondsToDate(long milliseconds){
		
		Date date = null;
		
		try {
			 date= new Date(milliseconds);
		} catch (Exception e) {
			logger.error(e.getMessage());
			date = new Date(0);
		}
		
		return date;
	}
	
	public static long getDifference(long date1, long date2, TIMETYPE timeType){
		
		// Calculates the difference in milliseconds.
		  long millisDiff = date2 - date1;

		  switch (timeType) {
		  	case DAYS: return (long) (millisDiff / 86400000);
		  	case HOURS: return (long) (millisDiff / 3600000 % 24);
		  	case MINUTES: return (long) (millisDiff / 60000 % 60);
		  	case SECONDS: return (long) (millisDiff / 1000 % 60);
		
		  	default:
		  		return (long) (millisDiff / 1000 % 60);
		} 
	}
	
	
	/**
	 * 
	 * @param date1
	 * @param date2
	 * @return difference between two date in this format: N°day/days HH:mm:ss
	 */
	public static String getDifference(long date1, long date2){
		long millisDiff = date2 - date1;
//		System.out.println("millisDiff: "+millisDiff);
		Date date = new Date(millisDiff);
		DateFormat outFormat = new SimpleDateFormat(ConstantsSpeciesDiscovery.TIMEFORMAT_HH_MM_SS);
		outFormat.setTimeZone(TimeZone.getTimeZone(ConstantsSpeciesDiscovery.TIME_ZONE_UTC));
		int days = Days.daysBetween(new DateTime(date1), new DateTime(date2)).getDays();
		
		if(days>0){
			String hours = outFormat.format(date);
			return days==1?days + " Day, "+hours:days + " Days, "+hours;
		}
		else
			return outFormat.format(date);
		
	}

}

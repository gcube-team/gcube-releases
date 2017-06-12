package org.gcube.datatransfer.scheduler.impl.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.gcube.common.core.utils.logging.GCUBELog;

/**
 *	
 * @author Nikolaos Drakopoulos(CERN)
 *
 */

public class Utils {
	static GCUBELog logger = new GCUBELog(Utils.class);

	public static String replaceUnderscore(String input) {
		return input.replaceAll("\\.", "_");
	}
	public static Calendar setCalendarComp(Calendar transferCal){
		if(transferCal==null) return null;
		Calendar calendarComp=(Calendar) transferCal.clone();
		calendarComp.set(Calendar.YEAR, Calendar.getInstance().get(Calendar.YEAR));
		calendarComp.set(Calendar.MONTH, Calendar.getInstance().get(Calendar.MONTH));
		calendarComp.set(Calendar.DAY_OF_MONTH, Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
		calendarComp.set(Calendar.HOUR_OF_DAY, Calendar.getInstance().get(Calendar.HOUR_OF_DAY));
		calendarComp.set(Calendar.MINUTE, Calendar.getInstance().get(Calendar.MINUTE));
		return calendarComp;
	}


	public static String getFormattedCalendarString(Calendar instance){
		Calendar calendar=Calendar.getInstance();
		calendar.set(Calendar.YEAR, instance.get(Calendar.YEAR));
		calendar.set(Calendar.MONTH, instance.get(Calendar.MONTH));
		calendar.set(Calendar.DAY_OF_MONTH, instance.get(Calendar.DAY_OF_MONTH));
		calendar.set(Calendar.HOUR_OF_DAY, instance.get(Calendar.HOUR_OF_DAY));
		calendar.set(Calendar.MINUTE, instance.get(Calendar.MINUTE));
		calendar.getTimeInMillis();
		//"dd.MM.yy-HH.mm"
		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yy-HH.mm");
		String date = sdf.format(calendar.getTime());
		return date;
	}
	public static Calendar getCalendarBasedOnStringDate(String stringDate){
		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yy-HH.mm");
		Date date=null;
		try {
			date = sdf.parse(stringDate);
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
		
		Calendar calendar=Calendar.getInstance();
		
		calendar.setTime(date);
		
		logger.debug("\nUTILS - getCalendarBasedOnStringDate:\n" +
				"stringDate="+stringDate+"\n" +
				"calendarBasedOnString="+getFormattedCalendarString(calendar)+"\n"+
						"timeZone="+TimeZone.getDefault()+"\n");
		return calendar;
	}
}

package org.gcube.datatransfer.scheduler.test;

import java.math.BigInteger;
import java.util.Calendar;
import java.util.Date;

/**
 *	
 * @author Nikolaos Drakopoulos(CERN)
 *
 */

public class TestTime {
	public static void main (String [] args){
	//	System.out.println(System.currentTimeMillis() - (50*60*1000));
		long timeMS=1365675319352L;
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(timeMS);
		
		Date date =new Date();
		date.setTime(timeMS);
		System.out.println(date.toString());
		
	
	}
	
	private static String getCalendarString(Calendar calendarTmp){
		String transferDate="year:"+calendarTmp.get(Calendar.YEAR)+", "+
				"month:"+calendarTmp.get(Calendar.MONTH)+", "+
				"day:"+calendarTmp.get(Calendar.DAY_OF_MONTH)+" - "+
				calendarTmp.get(Calendar.HOUR_OF_DAY)+"."+
				calendarTmp.get(Calendar.MINUTE)+".";
		return transferDate;
	}
}

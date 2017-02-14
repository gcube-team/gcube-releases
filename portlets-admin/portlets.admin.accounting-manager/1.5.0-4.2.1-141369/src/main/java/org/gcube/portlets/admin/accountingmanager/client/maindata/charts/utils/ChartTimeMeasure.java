package org.gcube.portlets.admin.accountingmanager.client.maindata.charts.utils;

import java.util.Date;

import org.gcube.portlets.admin.accountingmanager.shared.data.AccountingPeriod;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.i18n.client.DateTimeFormat;

/**
 * 
 * @author giancarlo
 * email: <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
public class ChartTimeMeasure {
	public static final long SECOND = 1000;
	public static final long MINUTE = 60*SECOND;
	public static final long HOUR = 60*MINUTE;
	public static final long DAY= 24*HOUR;
	
	public static double calculateMinRange(AccountingPeriod accountingPeriod) {
		if (accountingPeriod.getPeriod() == null) {
			Log.error("AccountingPeriodMode is null");
			return 0;
		}
		
		
		switch (accountingPeriod.getPeriod()) {
		case DAILY:
			// 1 day=24hour * 60min *60sec * 1000 millisec
			return 15 * DAY;
		case HOURLY:
			return 24 * HOUR;
		//case MILLISECONDLY:
		//	return 10 * 100;
		case MINUTELY:
			return 10 * MINUTE;
		case MONTHLY:
			return 6 * 28 * DAY;
		//case SECONDLY:
		//	return 30 * 1000;
		case YEARLY:
			return 5 * 12 * 28 * DAY;

		default:
			return 0;
		}

	}
	
	public static double calculateInterval(AccountingPeriod accountingPeriod) {
		if (accountingPeriod.getPeriod() == null) {
			Log.error("AccountingPeriodMode is null");
			return 0;
		}

		switch (accountingPeriod.getPeriod()) {
		case DAILY:
			return DAY;
		case HOURLY:
			return HOUR;
		//case MILLISECONDLY:
		//	return 100;
		case MINUTELY:
			return MINUTE;
		case MONTHLY:
			return 28 * DAY;
		//case SECONDLY:
		//	return 1000;
		case YEARLY:
			return 12 * 28 * DAY;

		default:
			return 0;
		}

	}
	
	
	public static long timeZoneOffset(){
		Date d = new Date();
		DateTimeFormat dtf = DateTimeFormat.getFormat("Z");
		String timeZone = dtf.format(d);
		
	    long timeZoneOffset = 0;
		try {

			timeZoneOffset = Integer.parseInt(timeZone.substring(0, 3)) * 60
					+ Integer.parseInt(timeZone.substring(3, 5));

		} catch (NumberFormatException e) {
			Log.error("Error parsing TimeZone: " + e.getLocalizedMessage());
		}
		
		return timeZoneOffset;
		
	}
	
	
}

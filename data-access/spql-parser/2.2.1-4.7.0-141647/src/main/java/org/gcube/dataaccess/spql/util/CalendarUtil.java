/**
 * 
 */
package org.gcube.dataaccess.spql.util;

import java.util.Calendar;
import java.util.Date;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * @author "Federico De Faveri defaveri@isti.cnr.it"
 *
 */
public class CalendarUtil {
	
	protected static final String[] DATE_PATTERNS = new String[]{
		"yyyy",
		"MM-yyyy",
		"MM/yyyy",
		"yyyyMMdd",
		"dd-MM-yyyy",
		"yyyy-MM-dd",
		"MM/dd/yyyy",
		"yyyy/MM/dd",
		"dd MMM yyyy",
		"dd MMMM yyyy",
		"yyyyMMddHHmm",
		"yyyyMMdd HHmm",
		"dd-MM-yyyy HH:mm",
		"yyyy-MM-dd HH:mm",
		"MM/dd/yyyy HH:mm",
		"yyyy/MM/dd HH:mm",
		"dd MMM yyyy HH:mm",
		"dd MMMM yyyy HH:mm",
		"yyyyMMddHHmmss",
		"yyyyMMdd HHmmss",
		"dd-MM-yyyy HH:mm:ss",
		"yyyy-MM-dd HH:mm:ss",
		"MM/dd/yyyy HH:mm:ss",
		"yyyy/MM/dd HH:mm:ss",
		"dd MMM yyyy HH:mm:ss",
	"dd MMMM yyyy HH:mm:ss"};

	protected static DateTimeFormatter[] FORMATTERS = new DateTimeFormatter[DATE_PATTERNS.length];

	static
	{
		int i = 0;
		for (String dateFormat:DATE_PATTERNS) {
			FORMATTERS[i++] = DateTimeFormat.forPattern(dateFormat);
		}
	}
	
	public static Date parse(String dateString) {
		for (DateTimeFormatter formatter:FORMATTERS) {
			try {
				return formatter.parseDateTime(dateString).toDate();
			} catch (Exception e){}
		}
		return null; // Unknown format.
	}
	
	public static Calendar parseCalendar(String value)
	{
		Date date = parse(value);
		if (date == null) return null;

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return calendar;
	}

}

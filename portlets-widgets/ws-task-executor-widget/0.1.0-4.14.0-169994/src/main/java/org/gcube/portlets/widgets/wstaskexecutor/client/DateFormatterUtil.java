/**
 *
 */
package org.gcube.portlets.widgets.wstaskexecutor.client;

import java.util.Date;

import com.google.gwt.i18n.client.DateTimeFormat;


/**
 * The Class DateFormatterUtil.
 *
 * @author Francesco Mangiacrapa at ISTI-CNR (francesco.mangiacrapa@isti.cnr.it)
 * May 17, 2018
 */
public class DateFormatterUtil {

	public static DateTimeFormat formatDate = DateTimeFormat
			.getFormat("yyyy/MM/dd");
	public static DateTimeFormat formatTime = DateTimeFormat
			.getFormat("yyyy/MM/dd 'at' HH:mm:ss");


	/**
	 * Gets the date time to string.
	 *
	 * @param time the time
	 * @return the date time to string
	 */
	public static String getDateTimeToString(Long time) {
		Date date;
		if(time!=null)
			date = new Date(time.longValue());
		else
			date = new Date();

		return formatTime.format(date);
	}

	/**
	 * Gets the date to string.
	 *
	 * @param time
	 *            the time
	 * @return the date to string
	 */
	public static String getDateToString(long time) {
		Date date = new Date(time);
		return formatDate.format(date);
	}
}

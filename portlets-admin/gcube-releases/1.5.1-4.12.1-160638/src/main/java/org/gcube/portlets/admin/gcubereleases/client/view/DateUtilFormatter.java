/**
 * 
 */
package org.gcube.portlets.admin.gcubereleases.client.view;

import java.util.Date;

import com.google.gwt.i18n.client.DateTimeFormat;

/**
 * The Class DateUtilFormatter.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it Feb 19, 2015
 */
public class DateUtilFormatter {

	public static DateTimeFormat formatDate = DateTimeFormat
			.getFormat("yyyy/MM/dd");
	public static DateTimeFormat formatTime = DateTimeFormat
			.getFormat("yyyy/MM/dd 'at' HH:mm:ss");

	/**
	 * Gets the date time to string.
	 *
	 * @param time
	 *            the time
	 * @return the date time to string
	 */
	public static String getDateTimeToString(long time) {
		Date date = new Date(time);
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

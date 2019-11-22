package org.gcube.portlets.user.workspace.server.tostoragehub;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


/**
 * The Class FormatterUtil.
 *
 * @author Francesco Mangiacrapa at ISTI-CNR (francesco.mangiacrapa@isti.cnr.it)
 * 
 * Jul 17, 2019
 */
public class FormatterUtil {
	
	/**
	 * To date.
	 *
	 * @param calendar the calendar
	 * @return the date
	 */
	public static Date toDate(Calendar calendar){
		if (calendar == null) return new Date(0);
		return calendar.getTime();

	}

	/**
	 * To date format to string.
	 *
	 * @param calendar the calendar
	 * @return the string
	 */
	protected String toDateFormatToString(Calendar calendar){

		SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM, yyyy HH:mm:ss z");
		Date resultdate = getDate(calendar);
		return dateFormat.format(resultdate);
	}

	/**
	 * To date format.
	 *
	 * @param calendar
	 *            the calendar
	 * @return the date
	 */
	protected Date toDateFormat(Calendar calendar) {

		SimpleDateFormat dateFormat =
				new SimpleDateFormat("dd-MM, yyyy HH:mm:ss z");
		Date resultdate = getDate(calendar);
		try {
			resultdate = dateFormat.parse(dateFormat.format(resultdate));
		}
		catch (ParseException e) {
			e.printStackTrace();
			resultdate = new Date(0);
		}
		return resultdate;
	}
	
	/**
	 * Gets the date.
	 *
	 * @param calendar the calendar
	 * @return the date
	 */
	private Date getDate(Calendar calendar) {

		Date resultdate = null;

		if (calendar == null)
			resultdate = new Date(0);
		else
			resultdate = new Date(calendar.getTimeInMillis());

		return resultdate;

	}
	

	/**
	 * Format file size.
	 *
	 * @param size the size
	 * @return the string
	 */
	public static String formatFileSize(long size) {
		String formattedSize = null;

		double b = size;
		double k = size/1024.0;
		double m = size/1024.0/1024.0;
		double g = size/1024.0/1024.0/1024.0;
		double t = size/1024.0/1024.0/1024.0/1024.0;

		DecimalFormat dec = new DecimalFormat("0.00");

		if ( t>1 ) {
			formattedSize = dec.format(t).concat(" TB");
		} else if ( g>1 ) {
			formattedSize = dec.format(g).concat(" GB");
		} else if ( m>1 ) {
			formattedSize = dec.format(m).concat(" MB");
		} else if ( k>1 ) {
			formattedSize = dec.format(k).concat(" KB");
		} else {
			formattedSize = dec.format(b).concat(" Bytes");
		}

		return formattedSize;
	}

}

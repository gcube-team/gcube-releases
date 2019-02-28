package org.gcube.portlets.user.accountingdashboard.client.application.utils;

import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.gcube.portlets.user.accountingdashboard.shared.exception.ServiceException;

import com.google.gwt.i18n.client.DateTimeFormat;

/**
 * 
 * @author Giancarlo Panichi
 *
 */
public class DateTimeUtils {
	private static Logger logger = java.util.logging.Logger.getLogger("");
	
	public static String toString(Date c) {
		DateTimeFormat format = DateTimeFormat.getFormat("yyyy-MM-dd'T'HH:mm:ss");
		if (c == null) {
			logger.log(Level.FINE,"DateTimeUtils: ");
			return "";
		} else {
			String s = format.format(c);
			logger.log(Level.FINE,"DateTimeUtils: " + s);

			return s;
		}
	}

	public static Date toDate(String s) throws ServiceException {

		DateTimeFormat format = DateTimeFormat.getFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		try {
			Date d = format.parse(s);
			return d;
		} catch (IllegalArgumentException e) {
			logger.log(Level.FINE,"Invalid Time Format: " + s);
			throw new ServiceException("Invalid Time Format: " + s);
		}

	}

}

package org.gcube.contentmanagement.graphtools.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DateGuesser {
	
	private static Logger LOGGER = LoggerFactory.getLogger(DateGuesser.class);

	public static Calendar convertDate(String data) {
		return convertDate(data, null);
	}

	public static String getPattern(String data) {
		return getPattern(data, null);
	}

	// private static final String[] formats = { "MM\\dd\\yyyy", "MM\\dd\\yy", "MM/dd/yy", "MM/dd/yyyy", "dd/MM/yy", "dd/MM/yyyy", "dd/MM/yyyy HH:mm:ss", "dd/MM/yy HH:mm:ss", "dd/MM/yyyy HH:mm:ss","MM/yy","MM/yyyy", "yyyy.MM.dd G 'at' HH:mm:ss z", "EEE, MMM d, ''yy", "h:mm a", "hh 'o''clock' a, zzzz", "K:mm a, z", "MM-dd-yy","MM-dd-yyyy", "dd-MMM-yy", "yyyy.MM.dd.HH.mm.ss", "E, dd MMM yyyy HH:mm:ss Z", "yyyyy.MMMMM.dd GGG hh:mm aaa", "EEE, d MMM yyyy HH:mm:ss Z", "yyMMddHHmmssZ", "yyyy-MM-dd'T'HH:mm:ss.SSSZ", "yyyy-MM-dd HH:mm","yyyy-MM-dd","yyyy-MM-dd HH:mm:ss", "h:mm a", "yyyy"};
	private static final String[] formats = { "MM\\dd\\yyyy", "MM\\dd\\yy", "MM/dd/yy", "MM/dd/yyyy", "MM/yy", "MM/yyyy", "yyyy.MM.dd G 'at' HH:mm:ss z", "MM/dd/yyyy HH:mm:ss", "MM/dd/yyyy HH:mm","dd/MM/yyyy HH:mm:ss", "EEE, MMM d, ''yy", "h:mm a", "hh 'o''clock' a, zzzz", "K:mm a, z", "MM-dd-yy", "MM-dd-yyyy", "dd-MMM-yy", "yyyy.MM.dd.HH.mm.ss", "E, dd MMM yyyy HH:mm:ss Z", "yyyyy.MMMMM.dd GGG hh:mm aaa", "EEE, d MMM yyyy HH:mm:ss Z", "yyMMddHHmmssZ", "yyyy-MM-dd'T'HH:mm:ss.SSSZ", "yyyy-MM-dd HH:mm", "yyyy-MM-dd", "yyyy-MM-dd HH:mm:ss", "MM/dd/yy KK:mm a", "MM/dd/yy KK:mm:ss a", "h:mm a", "yyyy", "s" };
	private static final String[] formatiITA = { "dd\\MM\\yyyy", "dd\\MM\\yy", "dd/MM/yy", "dd/MM/yyyy", "dd/MM/yy", "dd/MM/yyyy", "dd/MM/yyyy HH:mm:ss", "dd/MM/yy HH:mm:ss", "dd/MM/yyyy HH:mm:ss", "MM/yy", "MM/yyyy", "dd.MM.yyyy G 'alle' HH:mm:ss z", "EEE, MMM d, ''yy", "h:mm a", "hh a, zzzz", "K:mm a, z", "dd-MMM-yy", "dd.MM.yyyy.HH.mm.ss", "E, dd MMM yyyy HH:mm:ss Z", "yyyyy.MMMMM.dd GGG hh:mm aaa", "EEE, d MMM yyyy HH:mm:ss Z", "yyMMddHHmmssZ", "yyyy-MM-dd'T'HH:mm:ss.SSSZ", "dd-MMM-yyyy HH:mm", "h:mm a", "yyyy" };

	public static String getPattern(String data, String language) {
		if (isSeconds(data))
			return "s";

		
		// String language = System.getProperty("user.language");
		Locale l = Locale.UK;
		if (language != null && language.equalsIgnoreCase("it"))
			l = Locale.ITALY;

		Date dat = null;
		DateFormat formatter = null;

		String[] formati = formats;

		if (l.equals(Locale.ITALY))
			formati = formatiITA;

		int index = -1;
		for (int i = 0; i < formati.length; i++) {
			try {
				formatter = new SimpleDateFormat(formati[i], l);
				dat = (Date) formatter.parse(data);
				if (index == -1)
					index = i;
				else if (formati[index].length() <= formati[i].length())
					index = i;
				// System.out.println(formati[i]);
			} catch (Exception e) {
				// e.printStackTrace();
			}
		}

		if (dat != null) {

			return formati[index];
		} else
			return null;
	}

	public static boolean isSeconds(String timeString) {
		double seconds = -1;
		boolean isSeconds = false;
		try {
			seconds = Double.parseDouble(timeString);
			int secondsint = (int) seconds;
			if ((("" + secondsint).length() == 4) && (secondsint == seconds))
				isSeconds = false;
			else {
				LOGGER.debug("This entry contains seconds indication");
				isSeconds = true;
			}
		} catch (Exception e1) {
		}

		return isSeconds;
	}

	public static Calendar convertDate(String data, String language) {
		Date bestDate = null;
		Date dat = null;
		DateFormat formatter = null;
		Locale l = Locale.UK;
		if (language != null && language.equalsIgnoreCase("it"))
			l = Locale.ITALY;

		if (isSeconds(data)) {
			formatter = new SimpleDateFormat("s", l);
			try {
				dat = (Date) formatter.parse(data);
				bestDate = dat;
			} catch (ParseException e) {
			}
		} else {
			// String language = System.getProperty("user.language");

			String[] formati = formats;

			if (l.equals(Locale.ITALY))
				formati = formatiITA;

			int index = -1;

			for (int i = 0; i < formati.length; i++) {
				try {
					formatter = new SimpleDateFormat(formati[i], l);
					dat = (Date) formatter.parse(data);
					if (index == -1) {
						bestDate = dat;
						index = i;
					} else if (formati[index].length() <= formati[i].length()) {
						bestDate = dat;
						index = i;
					}
					// break;
				} catch (Exception e) {
					// e.printStackTrace();
				}
			}
		}
		if (bestDate != null) {
			Calendar c = Calendar.getInstance();
			c.setTime(bestDate);
			// System.out.println("data "+data+" giorno " + c.get(Calendar.DAY_OF_MONTH) + " mese " + (c.get(Calendar.MONTH) + 1) + " anno " + c.get(Calendar.YEAR));
			return c;
		} else
			return null;
	}

	public static final String YEAR = "YEAR";
	public static final String MONTH = "MONTH";
	public static final String DAY = "DAY";

	public static String granularity(String pattern) {
		SimpleDateFormat formatter = null;
		try {
			formatter = new SimpleDateFormat("MM/dd/yyyy");
			formatter.parse(pattern);
			return DAY;
		} catch (Exception e) {
		}
		try {
			formatter = new SimpleDateFormat("MM/yyyy");
			formatter.parse(pattern);
			return MONTH;
		} catch (Exception e) {
		}
		try {
			formatter = new SimpleDateFormat("MM/yy");
			formatter.parse(pattern);
			return MONTH;
		} catch (Exception e) {
		}

		return YEAR;
	}
	
	
	public static boolean isJavaDateOrigin(Date date){
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		if ((c.get(Calendar.DAY_OF_MONTH)==1) && (c.get(Calendar.MONTH) ==0 ) && (c.get(Calendar.YEAR) == 1970))
			return true;
		else
			return false;
	}

}

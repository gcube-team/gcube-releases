package org.gcube.vremanagement.resourcemanager.impl.state;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

/**
 * Transforms from {@link Date} to a valid string representation for the <em>xs:dateAndTime</em> XML Schema data type and vice versa. 
 * It can be used to write/read a {@link Date} object to/from a profile. 
 * 
 * @author Manuele Simi (ISTI-CNR)
 *
 */
public final class ProfileDate {
	
	static final DateFormat dateAndTime = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
	
	/**
	 * Transforms the input {@link Date} in a valid string representation for the <em>xs:dateAndTime</em> XML Schema data type 
	 * @param date the {@link Date} object to tranform
	 * @return the {@link String} object
	 */
	public static synchronized String toXMLDateAndTime(Date date) {
		String formatted = dateAndTime.format(date);
		StringBuilder toXS = new StringBuilder();
		toXS.append(formatted.substring(0, formatted.length()-2));
		toXS.append(":");
		toXS.append(formatted.substring(formatted.length()-2, formatted.length()));
		return toXS.toString();
		
	}
	
	/**
	 * Transforms the input <em>xs:dateAndTime</em> representation in a {@link Date} object
	 * @param date the string representation of <em>xs:dateAndTime</em> (e.g. &quot 2009-05-12T16:46:03+02:00 &quot) 
	 * @return the {@link Date} object
	 * @throws ParseException if the input date is not in an valid format
	 */
	public static synchronized Date fromXMLDateAndTime(String date) throws ParseException {		
		
		//the test is for backward compatibility, to read the old profiles that have no time zone in the dateAndTime fields
		Pattern p = Pattern.compile("^.*T\\d{2}:\\d{2}:\\d{2}$"); //ends with 'T'HH:mm:ss		
		if (p.matcher(date).matches()) {
			 return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(date);				
		} else {
			StringBuilder toDate = new StringBuilder();
			toDate.append(date.substring(0, date.length()-3));		
			toDate.append(date.substring(date.length()-2, date.length()));
			return  dateAndTime.parse(toDate.toString());		
		}
					
	}
	
}

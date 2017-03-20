/**
 *  Date validation using Pattern matching
 */
package org.gcube.data.spd.irmng;

import java.util.Date;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * @author "Federico De Faveri defaveri@isti.cnr.it"
 *
 */
public class DateUtil {
	//Unsupported:
	//2010-05-03T14:44:04Z+01:00
	//2009-04-09TMountain Da:ylight Time
	//2008-05-20T13:03:08OZ
	//2008-11-01T14:45OZ
	//0000-00-00 00:00:00

	protected static final String[] DATE_FORMATS = new String[]{
		"yyyy-MM-dd'T'HH:mm:ss.SSSZZ", 	//2005-10-24T13:33:11.000Z
		"yyyy-MM-dd'T'HH:mm:ss:mm:ssZ", //2003-02-18T11:35:13:35:13Z
		"yyyy-MM-dd' 'HH:mm:ss.SSSSSS", //2006-12-20 10:27:02.477563
		"yyyy-MM-dd'T'HH:mm:ssZZ", 		//2001-02-10T12:00:00+00:00
		"yyyy-MM-dd'T'HH:mm:sssZZ", 	//2011-09-25T11:00:000Z
		"yyyy-MM-dd' 'HH:mm:ssZZ", 		//2007-05-11 14:01:15-04
		"yyyy-MM-dd'T'HH:mm:ssz", 		//2005-10-11T15:40:00Z
		"yyyy-MM-dd' 'HH:mm:ss",		//2007-07-18 06:13:06
		"yyyy-MM-dd'T'HH:mmZ", 			//2009-10-01T01:00Z
		"dd/MM/yyyyHH:mm:ssZ",			//13/9/201012:00:00Z
		"yyyy-MM-dd' 'HH:mm",			//2005-12-20 17:12
		"yyyy-MM-dd'T'", 				//2010-06-09T
		"yyyy-MM-dd-'T'", 				//2009-08-05-T
		"yyyy-MM-dd",					//2009-09-08
		"yyyy",							//2009
		"MM-yyyy",						//09-2009
		"MMM-yyyy",						//May-2009
		"dd-MMM-yy",					//28-MAR-01 08-AUG-96
		"dd/MM/yyyy",					//11/2/2010
		"dd-MM-yyyy",					//11-02-2010
		"MM/dd/yyyy' 'HH:mm:ss",		//8/23/2010 0:00:00
		"MM/dd/yyyy",					//10/19/2010
		"yyyy/MM/dd' 'HH:mm:ss",		//2010/10/27 22:29:04

	};


	protected static DateUtil instance;

	public static DateUtil getInstance()
	{
		if (instance == null) {
			instance = new DateUtil();
			instance.initialize();
		}
		return instance;
	}

	protected DateTimeFormatter[] FORMATS;

	protected DateUtil() {
		FORMATS = new DateTimeFormatter[DATE_FORMATS.length];
	}

	protected void initialize()
	{
		int i = 0;
		for (String dateFormat:DATE_FORMATS) {
			FORMATS[i++] = DateTimeFormat.forPattern(dateFormat);
		}
	}

	public Date parse(String dateString) {
		for (DateTimeFormatter formatter:FORMATS) {
			try {
				return formatter.parseDateTime(dateString).toDate();
			} catch (Exception e){}
		}
		return null; // Unknown format.
	}

}

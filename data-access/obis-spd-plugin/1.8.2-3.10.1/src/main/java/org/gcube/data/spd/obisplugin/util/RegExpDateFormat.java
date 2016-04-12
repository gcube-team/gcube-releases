/**
 * 
 */
package org.gcube.data.spd.obisplugin.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author "Federico De Faveri defaveri@isti.cnr.it"
 *
 */
public class RegExpDateFormat {

	protected Pattern pattern;
	protected DateFormat dateFormat;
	
	public RegExpDateFormat(String regExp, String datePattern)
	{
		pattern = Pattern.compile(regExp);
		dateFormat = new SimpleDateFormat(datePattern);
	}
	
	public boolean match(String input)
	{
		Matcher m = pattern.matcher(input);
        return m.matches();
	}
	
	public Date parse(String input) throws ParseException
	{
		return dateFormat.parse(input);
	}
}

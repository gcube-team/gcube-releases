package gr.cite.gaap.utilities;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TypeUtils
{
	public static Short tryParseShort(String val)
	{
		try
		{
			return Short.parseShort(val);
		}catch(NumberFormatException e)
		{
			return null;
		}
	}
	
	public static Integer tryParseInteger(String val)
	{
		try
		{
			return Integer.parseInt(val);
		}catch(NumberFormatException e)
		{
			return null;
		}
	}
	
	public static Long tryParseLong(String val)
	{
		try
		{
			return Long.parseLong(val);
		}catch(NumberFormatException e)
		{
			return null;
		}
	}
	
	public static Float tryParseFloat(String val)
	{
		try
		{
			return Float.parseFloat(val);
		}catch(NumberFormatException e)
		{
			return null;
		}
	}
	
	public static Double tryParseDouble(String val)
	{
		try
		{
			return Double.parseDouble(val);
		}catch(NumberFormatException e)
		{
			return null;
		}
	}
	
	public static Date tryParseDate(String val, String format)
	{
		try
		{
			SimpleDateFormat sdf = new SimpleDateFormat(format);
			return sdf.parse(val);
		}catch(ParseException e)
		{
			return null;
		}
	}
}

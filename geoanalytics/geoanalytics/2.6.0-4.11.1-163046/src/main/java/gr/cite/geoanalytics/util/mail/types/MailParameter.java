package gr.cite.geoanalytics.util.mail.types;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum MailParameter
{
	DATETIME("datetime"),
	USERNAME("username"),
	PASSWORD("temppassword"),
	NUMLOGINS("numlogins"),
	LOGIN_CHECK_PERIOD("loginperiod"),
	LOGIN_CHECK_PERIOD_UNIT("loginperiodunit");
	
	private final String paramString;
	
	private static final Map<String,MailParameter> lookup  = new HashMap<String,MailParameter>();
	 
	static {
	      for(MailParameter s : EnumSet.allOf(MailParameter.class))
	           lookup.put(s.parameterString(), s);
	 }
	
	MailParameter(String parameterString)
	{
		this.paramString = parameterString;
	}
	
	public String parameterString() { return paramString; }

	public static MailParameter fromParameterString(String parameterString)
	{
		return lookup.get(parameterString);
	}
}

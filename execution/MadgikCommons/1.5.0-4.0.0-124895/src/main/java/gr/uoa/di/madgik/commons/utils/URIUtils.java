package gr.uoa.di.madgik.commons.utils;

import gr.uoa.di.madgik.commons.channel.proxy.IChannelLocator;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public class URIUtils
{
	public static final String SchemeChannel="channel";
	
	public static IChannelLocator.LocatorType GetLocatorType(URI locator)
	{
		return IChannelLocator.LocatorType.valueOf(locator.getFragment());
	}
	
	public static Map<String,String> ParseQueryString(String query)
	{
		Map<String,String> parmsMap = new HashMap<String,String>();
	    String params[] = query.split("&");
	    for (String param : params) 
	    {
	       String temp[] = param.split("=");
	       if(temp.length==2) parmsMap.put(temp[0], temp[1]);
	    }
	    return parmsMap;
	}
	
	public static String GetID(Map<String,String> params, boolean mandatory) throws Exception
	{
		if(!params.containsKey("id") && mandatory) throw new Exception("id not present");
		else if (!params.containsKey("id") && !mandatory) return null;
		else return params.get("id");
	}
	
	public static String BuildQueryString(String id)
	{
		StringBuilder buf=new StringBuilder();
		buf.append("id="+id);
		return buf.toString();
	}
}

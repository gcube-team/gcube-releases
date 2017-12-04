package org.gcube.data.access.httpproxy.utils;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Properties 
{
	private java.util.Properties properties;
	private Logger logger;
	private final String RESOURCE_NAME = "/domainFilterProperties.properties";
	private static Properties instance;
	
	
	public enum BooleanPropertyType 
	{
		DEFAULT_FORWARD ("defaultForward"),
		ENABLED ("filterEnabled");
		
		private String type;
		
		BooleanPropertyType (String type)
		{
			this.type = type;
		}
		
		public String toString ()
		{
			return this.type;
		}
	}
	
	
	public enum LongPropertyType 
	{
		DEFAULT_PERIOD ("defaultPeriod");
		
		private String type;
		
		LongPropertyType (String type)
		{
			this.type = type;
		}
		
		public String toString ()
		{
			return this.type;
		}
	}
	
	private Properties ()
	{
		this.logger = LoggerFactory.getLogger(this.getClass());
		this.properties = new java.util.Properties(getDefault());
		
		try
		{
			this.properties.load(this.getClass().getResourceAsStream(RESOURCE_NAME));
		} catch (IOException e)
		{
			this.logger.warn("Unable to load the properties",e);
		}

	}
	
	private java.util.Properties getDefault ()
	{
		java.util.Properties defaults = new java.util.Properties();
		defaults.setProperty(BooleanPropertyType.DEFAULT_FORWARD.type, "false");
		defaults.setProperty(BooleanPropertyType.ENABLED.type, "false");
		defaults.setProperty(LongPropertyType.DEFAULT_PERIOD.type, "60");
		return defaults;
	}

	public static Properties getInstance ()
	{
		if (instance == null) instance = new Properties();
		
		return instance;
	}
	
	public boolean getProperty (BooleanPropertyType propertyType)
	{
		String propString = this.properties.getProperty(propertyType.type);
		
		return (propString.equalsIgnoreCase("true"));
	}
	
	public long getProperty (LongPropertyType propertyType)
	{
		return Long.parseLong(this.properties.getProperty(propertyType.type));
		
	}
}

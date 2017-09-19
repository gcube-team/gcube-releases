package org.gcube.application.framework.core.util;

import java.io.*;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Settings {
	
	/** The logger. */
	private static final Logger logger = LoggerFactory.getLogger(Settings.class);

	static Properties props = new Properties();
	static Settings settings = null;
	
	Settings()
	{
		try
		{
				props.load(Settings.class.getResourceAsStream("/etc/settings.properties"));
			
		}
		catch (FileNotFoundException e)
		{
			// TODO Auto-generated catch block
			logger.error("Exception:", e);
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			logger.error("Exception:", e);
		}
	}
	
	public static Settings getInstance()
	{
		if (settings == null)
			settings = new Settings();
		return settings;
	}

	/**
	 * @return the props
	 */
	public String getProperty(String key) {
		String value = props.getProperty(key);
		if(value.contains("${"))
		{
			int start = 0;
			int i;
			while((i= value.indexOf("${", start)) != -1)
			{
				start = value.indexOf("}", i) +1;
				String reg = value.substring(i, start);
				logger.info(reg);
				logger.info(reg.substring(2, reg.length() -1));
				value = value.replace(reg, (System.getProperty(reg.substring(2, reg.length() -1)) != null)?System.getProperty(reg.substring(2, reg.length() -1)):"");
			}
		}
		return value;
	}
}

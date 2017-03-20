package org.gcube.vomanagement.usermanagement.impl.liferay;

import java.io.*;
import java.util.*;

public class Settings {

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
			e.printStackTrace();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
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
				//System.out.println(reg);
				//System.out.println(reg.substring(2, reg.length() -1));
				value = value.replace(reg, (System.getProperty(reg.substring(2, reg.length() -1)) != null)?System.getProperty(reg.substring(2, reg.length() -1)):"");
			}
		}
		return value;
	}
}

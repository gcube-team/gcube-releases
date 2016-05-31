package org.gcube.portlets.user.searchportlet.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Properties;

public class FileUtils {

	public static String getPropertyValue(String filePath, String propertyName) {
		Properties props = new Properties();
		String value = null;
		System.out.println("PATHH --->>> " + filePath);
		try {	
			
			File propsFile = new File(filePath);
			FileInputStream fis = new FileInputStream(propsFile);
			props.load( fis);
			value = props.getProperty(propertyName);
		}
		//catch exception in case properties file does not exist
		catch(IOException e) {
			System.out.println("Exception file does not exist");
		}
		return value;
	}

}

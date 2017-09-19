package org.gcube.documentstore.records.implementation;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfigurationGetPropertyValues {

	private static final Logger logger = LoggerFactory.getLogger(ConfigurationGetPropertyValues.class);
	public Properties getPropValues() throws IOException {
		Properties prop = null;
		String propFileName = "./config/accounting.properties";
		logger.trace("property file search"+propFileName);
		
		logger.trace("find a properties in :"+new File(".").getAbsolutePath());
		
		try (FileInputStream inputStream= new FileInputStream(propFileName)){
			if (inputStream != null) {
				prop=new Properties();
				prop.load(inputStream);
			}
		}catch (Exception e) {
			logger.trace("ConfigurationGetPropertyValues -property file error on input stream"+e.getLocalizedMessage());
		} 
		return prop;
	}
}

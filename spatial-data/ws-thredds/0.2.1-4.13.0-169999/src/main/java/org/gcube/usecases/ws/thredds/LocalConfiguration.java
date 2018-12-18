package org.gcube.usecases.ws.thredds;

import java.util.Properties;

import lombok.Synchronized;
import lombok.extern.slf4j.Slf4j;
@Slf4j
public class LocalConfiguration {

	private static LocalConfiguration instance=null;
	
	@Synchronized
	private static final LocalConfiguration get() {
		if(instance==null)
			instance=new LocalConfiguration();
		return instance;
	}
	
	public static String getProperty(String property) {
		try{
			return (String) get().props.getOrDefault(property, Constants.defaultConfigurationMap.get(property));
		}catch(Throwable t) {
			log.warn("Unable to get configuration property "+property,t);
			return Constants.defaultConfigurationMap.get(property);
		}
	}
	
	
	//***************** INSTANCE 
	
	Properties props;
	
	public LocalConfiguration() {
		props=new Properties();
		try{
			props.load(this.getClass().getResourceAsStream("configuration.properties"));
		}catch(Exception e) {
			log.warn("********************** UNABLE TO LOAD PROPERTIES **********************",e);
			log.debug("Reverting to defaults : "+Constants.defaultConfigurationMap);
		}
	}
	
	
}

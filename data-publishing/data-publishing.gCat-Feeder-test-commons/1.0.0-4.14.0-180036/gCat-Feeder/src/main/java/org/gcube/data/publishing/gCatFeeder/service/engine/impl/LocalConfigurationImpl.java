package org.gcube.data.publishing.gCatFeeder.service.engine.impl;

import java.io.IOException;
import java.util.Properties;

import javax.annotation.PostConstruct;

import org.gcube.data.publishing.gCatFeeder.service.engine.LocalConfiguration;

public class LocalConfigurationImpl implements LocalConfiguration {

	private Properties props;
	
	@PostConstruct
	public void load() {
		props=new Properties();
		try{
			props.load(this.getClass().getResourceAsStream("/gcat-feeder-config.properties"));
		}catch(IOException e) {throw new RuntimeException(e);}
	}
	
	
	@Override
	public String getProperty(String propertyName) {
		if(props.isEmpty()) throw new RuntimeException("No properties loaded");
		return props.getProperty(propertyName); 
	}

}

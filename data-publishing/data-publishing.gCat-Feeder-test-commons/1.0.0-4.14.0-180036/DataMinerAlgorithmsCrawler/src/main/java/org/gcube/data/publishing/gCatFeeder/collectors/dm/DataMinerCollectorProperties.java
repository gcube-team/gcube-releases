package org.gcube.data.publishing.gCatFeeder.collectors.dm;

import java.io.IOException;
import java.util.Properties;

public class DataMinerCollectorProperties {

	public static final String DEFAULT_AUTHOR="default_author";
	public static final String GUI_PARAM_NAME="gui_param_name";
	public static final String CKAN_RESOURCE_TYPE="ckan_resource_type";
	
	
	
	
	private static Properties props;
	
	public static void init() throws IOException {
		props=new Properties();
		props.load(DataMinerCollectorProperties.class.getResourceAsStream("config.properties"));
	}

	
	
	
	public static String getProperty(String key) {
		return props.getProperty(key);
	}
}

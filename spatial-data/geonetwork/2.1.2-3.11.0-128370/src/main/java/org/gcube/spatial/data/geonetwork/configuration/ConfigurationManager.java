package org.gcube.spatial.data.geonetwork.configuration;

public class ConfigurationManager {

	private static Class<? extends Configuration> configClass=DefaultConfiguration.class;
	
	
	public static synchronized Configuration get() throws Exception{		
		return configClass.newInstance();
	}
	public static void setConfiguration(Class<? extends Configuration>  configuration){configClass=configuration;}
	
}

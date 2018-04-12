package org.gcube.data.transfer.plugins.thredds;

import java.util.Properties;

public class LocalConfiguration {

	final static public String THREDDS_SE_CATEGORY="th.se.category";
	final static public String THREDDS_SE_PLATFORM="th.se.platform";
	final static public String THREDDS_SE_REMOTE_MANAGEMENT_ACCESS="th.se.remoteManagement.access";

	final static public String IS_REGISTRATION_TIMEOUT="is.registration.timeout";
	
	final static public String CONTEXT_LOADING_TIMETOUT="context.loading.timeout";
	
	static LocalConfiguration instance=null;
	
	
	public static synchronized LocalConfiguration get(){		
		if(instance==null)
			instance=new LocalConfiguration();
		return instance; 
	}
	
	private Properties props=new Properties();
	
	private LocalConfiguration() {
		try{			
			props.load(this.getClass().getResource("/thredds.properties").openStream());
		}catch(Exception e){
			throw new RuntimeException(e);
		}
	}
	
	public static String getProperty(String property){
		return get().props.getProperty(property);
	}
	
	public static Long getTTL(String property) {
		return Long.parseLong(getProperty(property));
	}
	
}

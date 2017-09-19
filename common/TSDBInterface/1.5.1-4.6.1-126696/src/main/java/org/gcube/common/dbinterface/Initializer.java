package org.gcube.common.dbinterface;

import java.util.Properties;

import org.gcube.common.dbinterface.pool.DBSession;

public interface Initializer {

	public void initialize(String username, String passwd,String dbPath);
	
	public Properties getQueryMappingPropertiesStream();
	
	public void postInitialization(DBSession session);
	
	public String getDriver();
	
	public String getEntireUrl();
	
}

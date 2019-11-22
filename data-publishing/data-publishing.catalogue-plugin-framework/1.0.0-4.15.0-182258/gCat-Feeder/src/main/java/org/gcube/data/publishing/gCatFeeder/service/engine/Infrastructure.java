package org.gcube.data.publishing.gCatFeeder.service.engine;

import java.util.Map;

import org.gcube.data.publishing.gCatFeeder.service.engine.impl.persistence.DatabaseConnectionDescriptor;

public interface Infrastructure {

	public String getCurrentToken();
	public String getCurrentContext();
	public String getCurrentContextName();
	public String getClientID(String token);
	public void setToken(String token);
	
	//
	
	public String encrypt(String toEncrypt);
	public String decrypt(String toDecrypt);
	
	
	//
	
	public DatabaseConnectionDescriptor queryForDatabase(String category,String name) throws InternalError;
	
	
	public Map<String,String> getEnvironmentConfigurationParameters();
}

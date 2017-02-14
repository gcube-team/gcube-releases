package org.gcube.security.soa3.configuration.impl;

import org.gcube.security.soa3.configuration.ConfigurationManager;

public class DefaultConfigurationManager implements ConfigurationManager {

	@Override
	public boolean isSecurityEnabled(String serviceName) 
	{
		return false;
	}

	@Override
	public String getServerUrl(String serviceName) 
	{
		return "http://localhost:8080";
	}

	@Override
	public boolean getCredentialPropagationPolicy(String serviceName) 
	{
		return false;
	}
//
//	public void setServiceProperties(String serviceName,Properties serviceProperties) {
//		
//
//	}
//
	@Override
	public boolean servicePropertiesSet(String serviceName) 
	{
		return true;
	}

}

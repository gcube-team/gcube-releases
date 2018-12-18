package org.gcube.datapublishing.sdmx.datasource.config.impl;

import java.net.Authenticator;
import java.util.LinkedList;
import java.util.List;


import org.gcube.datapublishing.sdmx.datasource.config.ConfigurationManager;
import org.gcube.datapublishing.sdmx.datasource.config.DataSourceConfigurationException;
import org.gcube.datapublishing.sdmx.datasource.config.ProxyAuthenticator;
import org.gcube.datapublishing.sdmx.datasource.datatype.DataTypeManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfigurationManagerImpl implements ConfigurationManager{

	private Logger logger;
	private List<String> excludedQueryParameters;
	private DataTypeManager dataTypeManager;
	
	public ConfigurationManagerImpl() {
		this.logger = LoggerFactory.getLogger(ConfigurationManagerImpl.class);
		this.excludedQueryParameters = new LinkedList<>();
	}
	
	
	@Override
	public void init() throws DataSourceConfigurationException {
		this.logger.debug("Default configuration manager");
		
	}

	
	
	public void setExcludedQueryParameters(List<String> excludedQueryParameters) {
		
		for (String parameter : excludedQueryParameters)
		{
			this.excludedQueryParameters.add(parameter.toLowerCase());
		}
	}


	
	
	
	public void setDataTypeManager(DataTypeManager dataTypeManager) {
		this.dataTypeManager = dataTypeManager;
	}


	@Override
	public List<String> getExcludedQueryParameters() {

		return excludedQueryParameters;
	}

	@Override
	public DataTypeManager getDataTypeManager() {

		return this.dataTypeManager;
	}
	
	public void setProxyAuthenticator (ProxyAuthenticator proxyAuthenticator)
	{
		if (proxyAuthenticator != null)
		{
			this.logger.debug("Proxy parameters found, configuration...");
			proxyAuthenticator.configure();
			this.logger.debug("Proxy parameters configured");
			
			if (proxyAuthenticator.isActive())
			{
				this.logger.debug("Proxy authentication found, configuration...");
				Authenticator.setDefault(proxyAuthenticator);
				this.logger.debug("Proxy authentication configured");
			}
		}
	}

	
}

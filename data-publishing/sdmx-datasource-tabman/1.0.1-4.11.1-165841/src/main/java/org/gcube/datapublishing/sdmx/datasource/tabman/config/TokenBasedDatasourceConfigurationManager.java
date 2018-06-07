package org.gcube.datapublishing.sdmx.datasource.tabman.config;

import org.gcube.common.authorization.library.provider.SecurityTokenProvider;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.datapublishing.sdmx.datasource.config.ConfigurationManager;
import org.gcube.datapublishing.sdmx.datasource.config.impl.ConfigurationManagerImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class TokenBasedDatasourceConfigurationManager extends ConfigurationManagerImpl implements ConfigurationManager {

	private Logger logger;
	private final String DEFAULT_NAME ="SDMXDataSource";
	private String name;
	
	public TokenBasedDatasourceConfigurationManager() {
		this.logger = LoggerFactory.getLogger(TokenBasedDatasourceConfigurationManager.class);
		this.name = DEFAULT_NAME;
		
	}
	

	
	
	protected void setTokens (String token, String scope)
	{
		this.logger.debug("Container token "+token);
		this.logger.debug("Container scope "+scope);
		ScopeProvider.instance.set(scope);
		SecurityTokenProvider.instance.set(token);
	}
	
	protected void setName (String newName)
	{
		if (newName != null) this.name = newName;
	}

	public String getName() {
		return name;
	}
	
	
	
	
	
}

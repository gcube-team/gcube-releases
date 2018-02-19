package org.gcube.datapublishing.sdmx.datasource.tabman.config;

import java.util.Collection;
import java.util.Set;

import org.gcube.common.resources.gcore.GCoreEndpoint;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.datapublishing.sdmx.datasource.config.ConfigurationManager;
import org.gcube.datapublishing.sdmx.datasource.config.DataSourceConfigurationException;
import org.gcube.smartgears.ContextProvider;
import org.gcube.smartgears.context.application.ApplicationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ApplicationTokenBasedDatasourceConfigurationManager extends TokenBasedDatasourceConfigurationManager implements ConfigurationManager {

	private Logger logger;
	
	public ApplicationTokenBasedDatasourceConfigurationManager() {
		this.logger = LoggerFactory.getLogger(ApplicationTokenBasedDatasourceConfigurationManager.class);
		
	}
	
	@Override
	public void init() throws DataSourceConfigurationException{
		super.init();
		ApplicationContext applicationContext = ContextProvider.get();
		this.logger.debug("Tabman related configuration");
		this.logger.debug("Setting credentials");
		String userScope = ScopeProvider.instance.get();
		this.logger.debug("User scope "+userScope );
		
		if (userScope == null)
		{			

			Set<String> tokens = applicationContext.configuration().startTokens();
			int tokensSize = tokens.size(); 
			GCoreEndpoint applicationProfile = applicationContext.profile(GCoreEndpoint.class);
			Collection<String> scopes = applicationProfile.scopes().asCollection();
			int scopesSize = scopes.size();
			
			if (tokensSize == 0 || scopesSize == 0)
			{
				this.logger.error("Application tokens: "+tokensSize);
				this.logger.error("Application Scopes: "+scopesSize);
				throw new DataSourceConfigurationException("Application tokens or scopes not found");
			}
			else if (tokensSize > 1)
			{
				this.logger.warn("The application has more than one token or more than one scope: this could lead to issues");
				this.logger.warn(String.format("%i tokens, %i scopes",tokensSize, scopesSize));
			}

			setTokens(tokens.iterator().next(), scopes.iterator().next());
		}
		
		String applicationName = applicationContext.name();
		this.logger.debug("Application name "+applicationName);
		setName(applicationName);
		
		
	}
	
	

	
	
	
	
	
}

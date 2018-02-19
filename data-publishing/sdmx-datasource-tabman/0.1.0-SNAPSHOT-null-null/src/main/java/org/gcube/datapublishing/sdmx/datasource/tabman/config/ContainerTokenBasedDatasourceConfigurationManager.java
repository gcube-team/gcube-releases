package org.gcube.datapublishing.sdmx.datasource.tabman.config;

import java.util.Collection;
import java.util.List;

import org.gcube.common.resources.gcore.HostingNode;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.datapublishing.sdmx.datasource.config.ConfigurationManager;
import org.gcube.datapublishing.sdmx.datasource.config.DataSourceConfigurationException;
import org.gcube.smartgears.ContextProvider;
import org.gcube.smartgears.context.application.ApplicationContext;
import org.gcube.smartgears.context.container.ContainerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ContainerTokenBasedDatasourceConfigurationManager extends TokenBasedDatasourceConfigurationManager implements ConfigurationManager {

	private Logger logger;
	
	public ContainerTokenBasedDatasourceConfigurationManager() {
		this.logger = LoggerFactory.getLogger(ContainerTokenBasedDatasourceConfigurationManager.class);
		
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
			//ContainerContext containerContext = context.container();
			ContainerContext containerContext = applicationContext.container();
			List<String> tokens = containerContext.configuration().startTokens();
			int tokensSize = tokens.size(); 
			HostingNode containerProfile = containerContext.profile(HostingNode.class);
			Collection<String> scopes = containerProfile.scopes().asCollection();
			int scopesSize = scopes.size();
			
			if (tokensSize == 0 || scopesSize == 0)
			{
				this.logger.error("Container tokens: "+tokensSize);
				this.logger.error("Scopes: "+scopesSize);
				throw new DataSourceConfigurationException("Container tokens or scopes not found");
			}
			else if (tokensSize > 1)
			{
				this.logger.warn("The container has more than one token or more than one scope: this could lead to issues");
				this.logger.warn(String.format("%d tokens, %d scopes",tokensSize, scopesSize));
			}

			setTokens(tokens.get(0), scopes.iterator().next());
		}
		String applicationName = applicationContext.name();
		this.logger.debug("Application name "+applicationName);
		setName(applicationName);
		
		
	}
	
	

	
	
	
	
	
}

package org.gcube.dataanalysis.dataminer.poolmanager.clients.configuration;

import static org.gcube.resources.discovery.icclient.ICFactory.clientFor;
import static org.gcube.resources.discovery.icclient.ICFactory.queryFor;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.gcube.common.resources.gcore.GenericResource;
import org.gcube.dataanalysis.dataminer.poolmanager.clients.configuration.ConfigurationImpl.CONFIGURATIONS;
import org.gcube.dataanalysis.dataminer.poolmanager.clients.configurations.AbstractConfiguration;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.api.SimpleQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientConfigurationCache 
{

	private Logger logger;
	private SVNRepository svnRepository;
	private Map<String, AbstractConfiguration> configurations;
	private long svnRepositoryTimeout;
	private Map<String, Long> 	configurationsTimeouts;
	
	private final long duration = 120000; //2 minutes
		
	ClientConfigurationCache ()
	{
		this.logger = LoggerFactory.getLogger(ClientConfigurationCache.class);
		this.svnRepository = null;
		this.configurations = new HashMap<>();
		this.svnRepositoryTimeout = 0;
		this.configurationsTimeouts = new HashMap<>();
	}
		
	public AbstractConfiguration getConfiguration (CONFIGURATIONS configuration)
	{
		Long time = this.configurationsTimeouts.get(configuration.toString());
		long currentTime = new Date().getTime();
		
		if (time == null || currentTime > time+this.duration)
		{
			this.configurations.put(configuration.toString(), getConfiguration (configuration.getType()));
			this.configurationsTimeouts.put(configuration.toString(), currentTime);
		}
		
		return this.configurations.get(configuration.toString());
	}
	
	
	public SVNRepository getSVNRepository ()
	{
		long currentTime = new Date().getTime();
		
		if (this.svnRepositoryTimeout == 0 || currentTime > this.svnRepositoryTimeout+this.duration)
		{
			this.svnRepository = queryForRepository();
			this.svnRepositoryTimeout = currentTime;
		}
		
		return this.svnRepository;
	}
	
	
	private SVNRepository queryForRepository() 
	{
			SimpleQuery query = queryFor(GenericResource.class);
			query.addCondition("$resource/Profile/SecondaryType/text() eq 'DMPMConfigurator'").setResult("$resource");
			DiscoveryClient<GenericResource> client = clientFor(GenericResource.class);
			List<GenericResource> ds = client.submit(query);
			Iterator<GenericResource> resourcesIterator = ds.iterator();
			SVNRepository response = null;
			
			while (resourcesIterator.hasNext() && response == null)
			{
				GenericResource resource = resourcesIterator.next();
				String repositoryURL = resource.profile().body().getElementsByTagName(SVNRepository.REPOSITORY_URL).item(0).getTextContent();

				if (repositoryURL != null)
				{
				
					String repositoryPath = resource.profile().body().getElementsByTagName(SVNRepository.REPOSITORY_PATH).item(0).getTextContent();
					String repositoryUsername = null;
					String repositoryPassword = null;
					
					
					try
					{
						repositoryUsername = resource.profile().body().getElementsByTagName(SVNRepository.REPOSITORY_USERNAME).item(0).getTextContent();
						repositoryPassword = resource.profile().body().getElementsByTagName(SVNRepository.REPOSITORY_PASSWORD).item(0).getTextContent();
						
						if (repositoryUsername != null && repositoryUsername.trim() == "") repositoryUsername = null;
						
						if (repositoryPassword != null && repositoryPassword.trim() == "") repositoryPassword = null;
						
						this.logger.debug("Repository username "+repositoryUsername);
						this.logger.debug("Repository password "+repositoryPassword);
			
					} catch (Exception e)
					{
						this.logger.debug("SVN Username and password not present");
					}
					
					this.logger.debug("SVN Repository URL: "+repositoryURL);
					this.logger.debug("SVN Repository path: "+repositoryPath);
				
					response = new SVNRepository(repositoryURL, repositoryPath,repositoryUsername, repositoryPassword);
				
				}
			}

		
		return response;
	}
	
	private AbstractConfiguration getConfiguration (AbstractConfiguration type)
	{
		SimpleQuery query = queryFor(GenericResource.class);
		query.addCondition("$resource/Profile/SecondaryType/text() eq 'DMPMConfigurator'").setResult(type.getXMLModel());
		DiscoveryClient<? extends AbstractConfiguration> client = clientFor(type.getClass());
		List<? extends AbstractConfiguration> configurations = client.submit(query);
		if (configurations != null && !configurations.isEmpty()) return configurations.get(0);
		else return null;
	}
	
}

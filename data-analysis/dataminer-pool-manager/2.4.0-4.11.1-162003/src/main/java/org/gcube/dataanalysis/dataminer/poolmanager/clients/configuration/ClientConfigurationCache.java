package org.gcube.dataanalysis.dataminer.poolmanager.clients.configuration;

import static org.gcube.resources.discovery.icclient.ICFactory.clientFor;
import static org.gcube.resources.discovery.icclient.ICFactory.queryFor;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.gcube.common.resources.gcore.GenericResource;
import org.gcube.dataanalysis.dataminer.poolmanager.clients.configuration.ConfigurationImpl.CONFIGURATIONS;
import org.gcube.dataanalysis.dataminer.poolmanager.clients.configuration.ConfigurationImpl.REPOSITORIES;
import org.gcube.dataanalysis.dataminer.poolmanager.clients.configurations.AbstractConfiguration;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.api.SimpleQuery;

public class ClientConfigurationCache 
{

	private Map<String, String> repositories;

	private Map<String, AbstractConfiguration> configurations;
	
	private Map<String, Long> 	repoTimeouts,
								configurationsTimeouts;
	
	private final long duration = 120000; //2 minutes
		
	public ClientConfigurationCache ()
	{
		this.repositories = new HashMap<>();
		this.configurations = new HashMap<>();
		this.repoTimeouts = new HashMap<>();
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
	
	
	public String getRepository (REPOSITORIES repository)
	{
		Long time = this.repoTimeouts.get(repository);
		long currentTime = new Date().getTime();
		
		if (time == null || currentTime > time+this.duration)
		{
			this.repositories.put(repository.toString(), getRepository (repository.toString()));
			this.repoTimeouts.put(repository.toString(), currentTime);
		}
		
		return this.repositories.get(repository.toString());
	}
	
	
	private String getRepository(String type) {
		

			String ghost = "";
			SimpleQuery query = queryFor(GenericResource.class);
			query.addCondition("$resource/Profile/SecondaryType/text() eq 'DMPMConfigurator'").setResult("$resource");
			DiscoveryClient<GenericResource> client = clientFor(GenericResource.class);
			List<GenericResource> ds = client.submit(query);
			for (GenericResource a : ds) {
				ghost = a.profile().body().getElementsByTagName(type).item(0).getTextContent();
			}
			
			
		
		return ghost.trim ();
		
		// TODO Auto-generated method stub


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

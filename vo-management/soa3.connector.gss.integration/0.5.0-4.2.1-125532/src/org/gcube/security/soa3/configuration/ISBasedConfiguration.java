package org.gcube.security.soa3.configuration;

import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.gcube.common.core.contexts.GHNContext;
import org.gcube.common.core.informationsystem.client.ISClient;
import org.gcube.common.core.informationsystem.client.queries.GCUBERuntimeResourceQuery;
import org.gcube.common.core.resources.GCUBERuntimeResource;
import org.gcube.common.core.resources.runtime.AccessPoint;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.common.scope.api.ScopeProvider;

public class ISBasedConfiguration implements GSSConfigurationManager
{
	private GSSConfigurationManagerImpl internalConfiguration;
	private GCUBELog log;
	private final String 	GET_SOA3_CONDITION = "$resource/Profile/Name eq 'SOA3'",
							BASE_ENDPOINT = "base";
	
	public ISBasedConfiguration (GSSConfigurationManagerImpl internalConfiguration)
	{
		this.log = new GCUBELog(this);
		this.log.debug("IS Based configurator");
		this.internalConfiguration = internalConfiguration;
	}
	

	@Override
	public boolean isSecurityEnabled(String serviceName) 
	{
		return this.internalConfiguration.isSecurityEnabled(serviceName);
	}

	@Override
	public String getServerUrl(String serviceName) 
	{
		String endpoint = retrieveSoa3Url();
		
		if (endpoint == null) endpoint = this.internalConfiguration.getServerUrl(serviceName);
		
		return endpoint;
	}

	@Override
	public boolean getCredentialPropagationPolicy(String serviceName) 
	{
		return this.internalConfiguration.getCredentialPropagationPolicy(serviceName);
	}

	@Override
	public void setServiceProperties(String serviceName,Properties serviceProperties) 
	{
		this.internalConfiguration.setServiceProperties(serviceName, serviceProperties);
	}

	@Override
	public boolean servicePropertiesSet(String serviceName) 
	{
		return this.internalConfiguration.servicePropertiesSet(serviceName);
	}
	
	public String retrieveSoa3Url()
	{
		String scope = ScopeProvider.instance.get();

		if (scope == null) return null;
		
		log.debug("Loading soa3 endopoint");
		log.debug("For scope "+scope);
		String endpoint = null;
		
		try
		{
			ISClient client = GHNContext.getImplementation(ISClient.class);
			GCUBERuntimeResourceQuery query = client.getQuery(GCUBERuntimeResourceQuery.class);
			query.addGenericCondition(GET_SOA3_CONDITION);
			log.debug("Executing query...");
			List<GCUBERuntimeResource> soa3Services = client.execute(query, GCUBEScope.getScope(scope));

			if(soa3Services == null || soa3Services.size() == 0) 
			{
				log.debug("No entry found");
			}
			else
			{
				log.debug("Entry found");
				GCUBERuntimeResource soa3Service = soa3Services.get(0);
				List<AccessPoint> accessPoints = soa3Service.getAccessPoints();
				Iterator<AccessPoint> accessPointIterator = accessPoints.iterator();
				
				while (accessPointIterator.hasNext() && endpoint == null)
				{
					AccessPoint accessPoint = accessPointIterator.next();
					String entryName = accessPoint.getEntryname();
					
					if (entryName.equals(BASE_ENDPOINT))
					{
						endpoint = accessPoint.getEndpoint();
						log.debug("Endpoint "+endpoint);
					}
				}
				
				
			}
		}
		catch (Exception e)
		{
			log.error("Unable to contact the IS for an internal error", e);
		}
		
		return endpoint;

	}

}

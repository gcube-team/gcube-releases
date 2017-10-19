package org.gcube.spatial.data.sdi.engine.impl;

import javax.inject.Singleton;

import org.gcube.spatial.data.sdi.LocalConfiguration;
import org.gcube.spatial.data.sdi.engine.GeoNetworkManager;
import org.gcube.spatial.data.sdi.engine.impl.faults.ConfigurationNotFoundException;
import org.gcube.spatial.data.sdi.engine.impl.faults.ServiceRegistrationException;
import org.gcube.spatial.data.sdi.engine.impl.is.Cache;
import org.gcube.spatial.data.sdi.engine.impl.is.GeoNetworkRetriever;
import org.gcube.spatial.data.sdi.model.health.ServiceHealthReport;
import org.gcube.spatial.data.sdi.model.service.GeoNetworkConfiguration;
import org.gcube.spatial.data.sdi.model.services.GeoNetworkServiceDefinition;

@Singleton
public class GeoNetworkManagerImpl implements GeoNetworkManager {

	private Cache<GeoNetworkConfiguration> cache=null;
	private GeoNetworkRetriever retriever=null;
	
	public GeoNetworkManagerImpl() {
		retriever=new GeoNetworkRetriever();
		cache=Cache.getCache(retriever, 
				Long.parseLong(LocalConfiguration.get().getProperty(LocalConfiguration.GEONETWORK_CACHE_TTL)),"GeoNetwork - cache");
	}
	
	
	@Override
	public GeoNetworkConfiguration getConfiguration() throws ConfigurationNotFoundException {
		return cache.get();
	}

	@Override
	public ServiceHealthReport getHealthReport() {
		return retriever.getHealthReport();
	}
	
	@Override
	public String registerService(GeoNetworkServiceDefinition definition) throws ServiceRegistrationException {
		return retriever.registerService(definition);	
		
	}
	
	@Override
	public String importHostFromToken(String sourceToken,String hostname) throws ServiceRegistrationException{
		return retriever.importHostFromToken(sourceToken, hostname);
	}
	
	
	
}

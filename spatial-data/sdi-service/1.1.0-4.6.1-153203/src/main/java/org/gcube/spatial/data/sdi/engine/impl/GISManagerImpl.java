package org.gcube.spatial.data.sdi.engine.impl;

import javax.inject.Singleton;

import org.gcube.spatial.data.sdi.LocalConfiguration;
import org.gcube.spatial.data.sdi.engine.GISManager;
import org.gcube.spatial.data.sdi.engine.impl.faults.ConfigurationNotFoundException;
import org.gcube.spatial.data.sdi.engine.impl.faults.ServiceRegistrationException;
import org.gcube.spatial.data.sdi.engine.impl.is.Cache;
import org.gcube.spatial.data.sdi.engine.impl.is.GeoServerClusterRetriever;
import org.gcube.spatial.data.sdi.model.health.ServiceHealthReport;
import org.gcube.spatial.data.sdi.model.service.GeoServerClusterConfiguration;
import org.gcube.spatial.data.sdi.model.services.GeoServerDefinition;

@Singleton
public class GISManagerImpl implements GISManager {

	private Cache<GeoServerClusterConfiguration> theCache=null;
	private GeoServerClusterRetriever retriever=null;
	
	
	public GISManagerImpl() {
		retriever=new GeoServerClusterRetriever();
		theCache=Cache.getCache(retriever, Long.parseLong(LocalConfiguration.get().getProperty(LocalConfiguration.GEOSERVER_CACHE_TTL)), "GeoCluster - cache");
	}
	
	
	@Override
	public GeoServerClusterConfiguration getConfiguration() throws ConfigurationNotFoundException {
		return theCache.get();
	}

	
	@Override
	public ServiceHealthReport getHealthReport() {
		return retriever.getHealthReport();
	}
	
	@Override
	public String registerService(GeoServerDefinition definition) throws ServiceRegistrationException {
		return retriever.registerService(definition);
	}
	@Override
	public String importHostFromToken(String sourceToken,String hostname) throws ServiceRegistrationException{
		return retriever.importHostFromToken(sourceToken, hostname);
	}
}

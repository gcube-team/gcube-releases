package org.gcube.spatial.data.sdi.engine.impl;

import javax.inject.Singleton;

import org.gcube.spatial.data.sdi.LocalConfiguration;
import org.gcube.spatial.data.sdi.engine.GeoNetworkManager;
import org.gcube.spatial.data.sdi.engine.impl.cache.Cache;
import org.gcube.spatial.data.sdi.engine.impl.cache.GeoNetworkRetriever;
import org.gcube.spatial.data.sdi.engine.impl.faults.ConfigurationNotFoundException;
import org.gcube.spatial.data.sdi.model.service.GeoNetworkConfiguration;

@Singleton
public class GeoNetworkManagerImpl implements GeoNetworkManager {

	private Cache<GeoNetworkConfiguration> cache=null;
	
	
	public GeoNetworkManagerImpl() {
		cache=Cache.getCache(new GeoNetworkRetriever(), 
				Long.parseLong(LocalConfiguration.get().getProperty(LocalConfiguration.GEONETWORK_CACHE_TTL)),"GeoNetwork - cache");
	}
	
	
	@Override
	public GeoNetworkConfiguration getConfiguration() throws ConfigurationNotFoundException {
		return cache.get();
	}

}

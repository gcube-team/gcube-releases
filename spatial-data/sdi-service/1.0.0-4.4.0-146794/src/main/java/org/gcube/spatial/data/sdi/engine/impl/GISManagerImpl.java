package org.gcube.spatial.data.sdi.engine.impl;

import javax.inject.Singleton;

import org.gcube.spatial.data.sdi.LocalConfiguration;
import org.gcube.spatial.data.sdi.engine.GISManager;
import org.gcube.spatial.data.sdi.engine.impl.cache.Cache;
import org.gcube.spatial.data.sdi.engine.impl.cache.GeoServerClusterRetriever;
import org.gcube.spatial.data.sdi.engine.impl.faults.ConfigurationNotFoundException;
import org.gcube.spatial.data.sdi.model.service.GeoServerClusterConfiguration;

@Singleton
public class GISManagerImpl implements GISManager {

	private Cache<GeoServerClusterConfiguration> theCache=null;
	
	public GISManagerImpl() {
		theCache=Cache.getCache(new GeoServerClusterRetriever(), Long.parseLong(LocalConfiguration.get().getProperty(LocalConfiguration.GEOSERVER_CACHE_TTL)), "GeoCluster - cache");
	}
	
	
	@Override
	public GeoServerClusterConfiguration getConfiguration() throws ConfigurationNotFoundException {
		return theCache.get();
	}

}

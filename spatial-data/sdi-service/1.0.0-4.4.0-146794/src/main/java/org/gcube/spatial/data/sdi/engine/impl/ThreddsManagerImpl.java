package org.gcube.spatial.data.sdi.engine.impl;

import javax.inject.Singleton;

import org.gcube.spatial.data.sdi.LocalConfiguration;
import org.gcube.spatial.data.sdi.engine.ThreddsManager;
import org.gcube.spatial.data.sdi.engine.impl.cache.Cache;
import org.gcube.spatial.data.sdi.engine.impl.cache.ThreddsRetriever;
import org.gcube.spatial.data.sdi.engine.impl.faults.ConfigurationNotFoundException;
import org.gcube.spatial.data.sdi.model.service.ThreddsConfiguration;

@Singleton
public class ThreddsManagerImpl implements ThreddsManager {

	private Cache<ThreddsConfiguration> cache=null;
	
	public ThreddsManagerImpl() {
		cache=Cache.getCache(new ThreddsRetriever(), 
				Long.parseLong(LocalConfiguration.get().getProperty(LocalConfiguration.THREDDS_CACHE_TTL)), "THREDDS - CACHE");
	}
	
	@Override
	public ThreddsConfiguration getConfiguration() throws ConfigurationNotFoundException {
		return cache.get();
	}

}

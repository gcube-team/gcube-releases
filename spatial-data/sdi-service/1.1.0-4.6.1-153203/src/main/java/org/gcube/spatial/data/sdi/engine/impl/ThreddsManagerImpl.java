package org.gcube.spatial.data.sdi.engine.impl;

import javax.inject.Singleton;

import org.gcube.spatial.data.sdi.LocalConfiguration;
import org.gcube.spatial.data.sdi.engine.ThreddsManager;
import org.gcube.spatial.data.sdi.engine.impl.faults.ConfigurationNotFoundException;
import org.gcube.spatial.data.sdi.engine.impl.faults.ServiceRegistrationException;
import org.gcube.spatial.data.sdi.engine.impl.is.Cache;
import org.gcube.spatial.data.sdi.engine.impl.is.ThreddsRetriever;
import org.gcube.spatial.data.sdi.model.health.ServiceHealthReport;
import org.gcube.spatial.data.sdi.model.service.ThreddsConfiguration;
import org.gcube.spatial.data.sdi.model.services.ThreddsDefinition;

@Singleton
public class ThreddsManagerImpl implements ThreddsManager {

	private Cache<ThreddsConfiguration> cache=null;
	
	private ThreddsRetriever retriever=null;
	
	public ThreddsManagerImpl() {
		retriever=new ThreddsRetriever();
		cache=Cache.getCache(retriever, 
				Long.parseLong(LocalConfiguration.get().getProperty(LocalConfiguration.THREDDS_CACHE_TTL)), "THREDDS - CACHE");
	}
	
	@Override
	public ThreddsConfiguration getConfiguration() throws ConfigurationNotFoundException {
		return cache.get();
	}

	@Override
	public ServiceHealthReport getHealthReport() {
		return retriever.getHealthReport();
	}
	
	@Override
	public String registerService(ThreddsDefinition definition) throws ServiceRegistrationException {
		return retriever.registerService(definition);
	}
	@Override
	public String importHostFromToken(String sourceToken,String hostname) throws ServiceRegistrationException{
		return retriever.importHostFromToken(sourceToken, hostname);
	}
}

package org.gcube.vremanagement.softwaregateway.impl.repositorymanager.cache;

import java.util.Properties;

import org.gcube.common.core.utils.logging.GCUBELog;

import net.sf.ehcache.CacheManager;
import net.sf.ehcache.event.CacheManagerEventListener;
import net.sf.ehcache.event.CacheManagerEventListenerFactory;


public class MyCacheManagerEventListenerFactory extends CacheManagerEventListenerFactory{

	GCUBELog logger = new GCUBELog(MyCacheManagerEventListenerFactory.class);

	public MyCacheManagerEventListenerFactory(){
		logger.debug("MyCacheEventListenerFactory constructor");
	}
	
	@Override
	public CacheManagerEventListener createCacheManagerEventListener(
			CacheManager arg0, Properties arg1) {
		logger.info("CACHE LISTENER HAS BEEN INSTANTIATED");
		return new MyCacheManagerEventListener();
	}

}

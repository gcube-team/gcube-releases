package org.gcube.vremanagement.softwaregateway.impl.repositorymanager.cache;

import org.gcube.common.core.utils.logging.GCUBELog;

import net.sf.ehcache.CacheException;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import net.sf.ehcache.Status;
import net.sf.ehcache.event.CacheEventListener;
import net.sf.ehcache.event.CacheManagerEventListener;

public class MyCacheManagerEventListener implements CacheManagerEventListener,  CacheEventListener{

	GCUBELog logger = new GCUBELog(MyCacheManagerEventListener.class);
	
	public void notifyElementRemoved(Ehcache cache, Element element)
			throws CacheException {
		// TODO Auto-generated method stub
		logger.info("CACHE EVENT LISTENER EVENT CATCH notifyElementRemoved in cache"+cache.getName()+" element: "+element.getKey());
	}


	public void notifyElementPut(Ehcache cache, Element element)
			throws CacheException {
		logger.info("CACHE EVENT LISTENER EVENT CATCH notifyElementPut in cache"+cache.getName()+" element: "+element.getKey());
	}

	public void notifyElementUpdated(Ehcache cache, Element element)
			throws CacheException {
		logger.info("CACHE EVENT LISTENER EVENT CATCH notifyElementUpdated in cache"+cache.getName()+" element: "+element.getKey());
	}

	
	public void notifyElementExpired(Ehcache cache, Element element) {
		logger.info("CACHE EVENT LISTENER EVENT CATCH notifyElementExpired in cache"+cache.getName()+" element: "+element.getKey());
	}

	@Override
	public void dispose() {
		logger.info("CACHE EVENT LISTENER EVENT CATCH dispose");
	}

	public Object clone() throws CloneNotSupportedException{
		logger.info("CACHE EVENT LISTENER EVENT CATCH clone");
		return null;
	}


	public void notifyElementEvicted(Ehcache cache, Element element) {
		logger.info("CACHE EVENT LISTENER EVENT CATCH notifyElementEvicted ");
	}


	public void notifyRemoveAll(Ehcache cache) {
		logger.info("CACHE EVENT LISTENER EVENT CATCH notifyRemoveAll");
	}

	@Override
	public Status getStatus() {
		logger.info("CACHE EVENT LISTENER EVENT CATCH getStatus");
		return null;
	}

	@Override
	public void init() throws CacheException {
		logger.info("CACHE EVENT LISTENER EVENT CATCH init");
		
	}

	@Override
	public void notifyCacheAdded(String arg0) {
		logger.info("CACHE EVENT LISTENER EVENT CATCH notifyCacheAdded");
		
	}

	@Override
	public void notifyCacheRemoved(String arg0) {
		logger.info("CACHE EVENT LISTENER EVENT CATCH notifyCacheRemoved");
		
	}
	
	
	
	
}

package org.gcube.data.spd.caching;

import net.sf.ehcache.CacheException;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import net.sf.ehcache.event.CacheEventListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MyCacheEventListener implements CacheEventListener {

	
	private static Logger logger = LoggerFactory.getLogger(MyCacheEventListener.class);
	
	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	@Override
	public void notifyElementRemoved(Ehcache cache, Element element)
			throws CacheException {
		CacheKey key = (CacheKey) element.getKey();
		QueryCache<?> value = (QueryCache<?>) element.getValue();
		logger.trace("event removed notified "+cache.getName()+" "+key.getSearchName()+" "+value.isValid());
	}

	@Override
	public void notifyElementPut(Ehcache cache, Element element)
			throws CacheException {
		CacheKey key = (CacheKey) element.getKey();
		QueryCache<?> value = (QueryCache<?>) element.getValue();
		logger.trace("event put notified "+cache.getName()+" "+key.getSearchName()+" "+value.isValid());
	}

	@Override
	public void notifyElementUpdated(Ehcache cache, Element element)
			throws CacheException {
		logger.trace("event update notified "	);
	}

	@Override
	public void notifyElementExpired(Ehcache cache, Element element) {
		CacheKey key = (CacheKey) element.getKey();
		QueryCache<?> value = (QueryCache<?>) element.getValue();
		logger.trace("event exipered notified "+cache.getName()+" "+key.getSearchName()+" "+value.isValid());
		((QueryCache<?>)element.getValue()).dispose();
	}

	@Override
	public void notifyElementEvicted(Ehcache cache, Element element) {
		CacheKey key = (CacheKey) element.getKey();
		QueryCache<?> value = (QueryCache<?>) element.getValue();
		logger.trace("event evicted notified "+cache.getName()+" "+key.getSearchName()+" "+value.isValid());
		((QueryCache<?>)element.getValue()).dispose();
		
	}

	@Override
	public void notifyRemoveAll(Ehcache cache) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dispose() {
		
	}


	
	
}

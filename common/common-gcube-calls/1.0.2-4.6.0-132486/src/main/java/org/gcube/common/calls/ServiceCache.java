package org.gcube.common.calls;

import java.util.LinkedHashMap;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Privately used by a {@link StubFactory}, caches {@link Service} instances for services with given names.
 * <p>
 * The cache is LRU and bounded at a maximum size of {@link LRUCache#max}. It is also thread-safe, though synchronisation
 * occurs on a per-key basis. 
 * 
 * @author Fabio Simeoni, Lucio Lelii
 * @see ServiceCache
 */
class ServiceCache<T> {

	private static final Logger log = LoggerFactory.getLogger(ServiceCache.class);
	
	private volatile LRUCache cache = new LRUCache();

	//holds key locks for LRU map
	private ConcurrentHashMap<Call,Lock> nameLocks = new ConcurrentHashMap<Call, Lock>();
	
	void clear(Call name) {
		
		//obtain a lock for current key
		Lock nameLock = lockFor(name);
		
		nameLock.lock();
		
		try {
			cache.remove(name);
		}
		finally {
			nameLock.unlock();
		}

	}
	
	
	T get(Call name,Callable<T> task) {
		
		//obtain a lock for current key
		Lock nameLock = lockFor(name);

		nameLock.lock();
		
		try {
			
			T service = cache.get(name);
			
			if (service==null)
				try {
					service= task.call();
					log.trace("caching stub for "+name);
					cache.put(name,service);
				}
				catch(Exception e) {
					throw new RuntimeException("could not build service",e);
				}
			else {
				log.trace("using cached stub for "+name);
			}
			
			return service;
		}
		finally {
			nameLock.unlock();
		}
	}
	
	//helper
	private Lock lockFor(Call name) {
		
		Lock nameLock = nameLocks.get(name);
		
		//no need to create a new lock a priori
		if (nameLock==null) {
			
			Lock newLock = new ReentrantLock();
			
			//get name lock, creating it if it doesn't exist
			//this is where we first synchronise: second-come thread waits for new one to have put a lock
			//then it gets that same shared lock
			nameLock = nameLocks.putIfAbsent(name,newLock);
			
			nameLock = nameLock == null? newLock : nameLock;
		}
		
		return nameLock;
	}
	
	private class LRUCache extends LinkedHashMap<Call,T> {
		
		private static final long serialVersionUID = 1L;
		
		public static final int max = 50;
		
		public LRUCache() {
			//use defaults, but indicate accessor-order as 3rd parameter (rather than default insertion order)
			super(16,.75f,true);
		}
		
		@Override
		protected boolean removeEldestEntry(java.util.Map.Entry<Call, T> eldest) {
			if (size()>=max) { 
				nameLocks.remove(eldest.getKey());
				return true;
			}
			
			return false;
		}
	}

}

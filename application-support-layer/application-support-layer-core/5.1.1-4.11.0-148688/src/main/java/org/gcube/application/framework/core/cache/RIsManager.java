//package org.gcube.application.framework.core.cache;
//
//import java.util.HashMap;
////import org.gcube.common.core.scope.GCUBEScope;
////import org.gcube.common.core.utils.logging.GCUBELog;
////import org.gcube.informationsystem.cache.ISCache;
////import org.gcube.informationsystem.cache.ISCacheManager;
//import net.sf.ehcache.Cache;
//import net.sf.ehcache.CacheManager;
//
//import org.gcube.informationsystem.cache.ISCache;
//import org.gcube.informationsystem.cache.ISCacheManager;
//import org.gcube.resources.discovery.icclient.ICClient;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//
///**
// * This class manages the running harvester threads.
// * 
// * @author Valia Tsagkalidou (KNUA)
// *
// */
//
//public class RIsManager {
//
//	
//	/**
//	 * Defines the manager's instance
//	 */
//	private static RIsManager instance = null;
//	
//	/**
//	 * keeps the ISCache per scope
//	 */
//	
////	protected HashMap<String, ISCache> isCaches;
//	
//	protected HashMap<String, ISCache> caches;  //(scope,iccache) pairs
//
//	/** Object logger. */
//	
//	protected static final Logger logger = LoggerFactory.getLogger(RIsManager.class);
//	
//	/**
//	 * Initializes RIsManager 
//	 */
//	private RIsManager() { 
////		caches = new HashMap<String, ISCache>();
//	}
//
//	/**
//	 * Retrieves the singleton
//	 * @return the only instance of RIsManager
//	 */
//	synchronized public static RIsManager getInstance() {
//		if (instance == null)
//			instance = new RIsManager();
//		return instance;
//	}
//	
//	
//	/**
//	 * @param scope the GGUBEScope for which the RIs are requested
//	 * @return the ISCache for this specific scope
//	 */
//	public synchronized ISCache getISCache(String scope)
//	{
//		ISCache info = caches.get(scope);
//		
//// TODO: UNCOMMENT when ISCache is FeatherWeight Stack compatible
//		
////		if(info == null)
////		{
////			// If the ISCache in not already created, then it creates a new instance and adds it to the HashMap 
////			try {
////				ISCacheManager.addManager(scope);
////				info = ISCacheManager.getCacheManager(scope).getManager(scope).getCache();
////				caches.put(scope, info);
////				try {
////					Thread.sleep(5000);
////				} catch (InterruptedException e1) {
////					logger.error("", e1);
////				}
////			} catch (Exception e) {
////				logger.error("", e);
////			}
////		}
//		
//		logger.debug("RI in cache is probably null");
//		logger.info("RI in cache is probably null");
//		
//		return info;
//	}
//
//}

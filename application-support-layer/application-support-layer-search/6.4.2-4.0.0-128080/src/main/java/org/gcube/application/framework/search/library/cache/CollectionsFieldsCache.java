package org.gcube.application.framework.search.library.cache;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import org.gcube.application.framework.search.library.exception.InitialBridgingNotCompleteException;
import org.gcube.application.framework.search.library.exception.InternalErrorException;
import org.gcube.application.framework.search.library.model.CollectionInfo;
import org.gcube.application.framework.search.library.util.FindFieldsInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CollectionsFieldsCache {
	
	/** The logger. */
	private static final Logger logger = LoggerFactory.getLogger(CollectionsFieldsCache.class);
	
	protected static Thread thread = new CleanCacheThread();
	
	protected static CollectionsFieldsCache collectionsFieldsCache = new CollectionsFieldsCache();
	protected HashMap<String, HashMap<CollectionInfo, ArrayList<CollectionInfo>>> scopesInfosCache;
	
	
	protected CollectionsFieldsCache() {
		scopesInfosCache = new HashMap<String, HashMap<CollectionInfo, ArrayList<CollectionInfo>>>();
		thread.setDaemon(true);
		thread.start();
	}
	
	
	public static CollectionsFieldsCache getInstance() {
		return collectionsFieldsCache;
	}
	
	public HashMap<CollectionInfo, ArrayList<CollectionInfo>> getCollectionsInfoForScope(String scope, boolean refresh) throws InitialBridgingNotCompleteException, InternalErrorException {
		HashMap<CollectionInfo, ArrayList<CollectionInfo>> info = scopesInfosCache.get(scope);
		if (info == null || info.size() == 0) {
			info = FindFieldsInfo.joinDynamicAndStaticConfiguration(scope, refresh);
			scopesInfosCache.put(scope, info);
		}
		
		return info;
	}
	
	public HashMap<CollectionInfo, ArrayList<CollectionInfo>> refreshCollectionInfoForScope(String scope, boolean refresh) throws InitialBridgingNotCompleteException, InternalErrorException {
		HashMap<CollectionInfo, ArrayList<CollectionInfo>> info = FindFieldsInfo.joinDynamicAndStaticConfiguration(scope, refresh);
		synchronized (this){
			scopesInfosCache.put(scope, info);
		}
		
		return info;
	}
	
	
	
	@Override
	protected void finalize() throws Throwable {
		thread.interrupt();
		logger.info(new Date(System.currentTimeMillis()) + " clean thread was interrupted");
		thread.join();
		logger.info(new Date(System.currentTimeMillis()) + " clean thread was joint");
		super.finalize();
	}
	
	
	protected static class CleanCacheThread extends Thread
	{
		public void run()
		{
			while(true)
			{
				logger.debug("Refreshing available collections. CollectionsFieldsCache thread");
				try {
					Thread.sleep(3600000);	// 60 minutes
				} catch (InterruptedException e) {
					logger.error("Exception:", e);
					logger.info(new Date(System.currentTimeMillis()) + " clean thread was interrupted (in clean thread)");
					break;
				}
				//TODO: Refresh cache 
				Set<String> keys = collectionsFieldsCache.scopesInfosCache.keySet();
				HashMap<String, HashMap<CollectionInfo, ArrayList<CollectionInfo>>> tempMap = new HashMap<String, HashMap<CollectionInfo, ArrayList<CollectionInfo>>>();
				for (String scope:keys) {
					HashMap<CollectionInfo, ArrayList<CollectionInfo>> info;
					try {
						info = FindFieldsInfo.joinDynamicAndStaticConfiguration(scope, false);
						tempMap.put(scope, info);
					}
					catch (InitialBridgingNotCompleteException e) {
						logger.error("Exception: ", e);
					}
					catch (InternalErrorException e) {
						logger.error("Exception: ", e);
					}
				}
				
				synchronized (this){
					collectionsFieldsCache.scopesInfosCache = tempMap;
				}
			}
			logger.info(new Date(System.currentTimeMillis()) + " clean thread was terminated");
		}

	}

}

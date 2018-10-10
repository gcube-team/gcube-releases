package org.gcube.data.transfer.library.caches;

import java.util.HashSet;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import lombok.Synchronized;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@ToString
@Slf4j
public abstract class TTLCache<T> {
	
	
	// STATIC
	
//	private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1,new ThreadFactory(){
//		public Thread newThread(Runnable r) {
//            Thread t = Executors.defaultThreadFactory().newThread(r);
//            t.setDaemon(true);
//            return t;
//        }
//	});
	private static final HashSet<TTLCache<?>> createdMaps=new HashSet<TTLCache<?>>();
	
	
	static {
//		 scheduler.scheduleAtFixedRate(new Runnable() {
//			
//			 
//			 
//			@Override
//			public void run() {
//				log.debug("Running Maps Cleaner, maps count : "+createdMaps.size());
//				int removed=0;
//				for(TTLCache<?> theMap:createdMaps)
//				theMap.purgeItems();
//				log.debug("Removed "+removed+" old tickets");
//				
//			}
//		}, 3, 3, TimeUnit.MINUTES);
//		
	}
	
	
	//************************* CACHE 
	
	
	
	protected TTLCache(Long cacheKeepAliveTime, Long objectTTL, String cacheName) {
		this.cacheKeepAliveTime = cacheKeepAliveTime;
		this.objectTTL = objectTTL;
		this.cacheName = cacheName;
		createdMaps.add(this);
		log.debug("Created Cache "+this);
	}
	
	
	
	private ConcurrentHashMap<String,TTLContainer<T>> theMap=new ConcurrentHashMap<>();

	private Long cacheKeepAliveTime;
	private Long objectTTL;
	private String cacheName;
	
	@Synchronized
	public T getObject(String id) throws Exception{
		if(!theMap.contains(id)||System.currentTimeMillis()-theMap.get(id).getCreationTime()>objectTTL)
			theMap.put(id, new TTLContainer<T>(getNew(id)));
		return theMap.get(id).getTheObject();
	}
	
	
	private int purgeItems(){
		log.debug(cacheName+" Purging objects, keep alive time is "+cacheKeepAliveTime);
		int totalCount=theMap.size();
		int removed=0;
		for(Entry<String,TTLContainer<T>> entry:theMap.entrySet())
			if(System.currentTimeMillis()-entry.getValue().getLastUsageTime()>cacheKeepAliveTime){						
				theMap.remove(entry.getKey());
				removed++;
			}
		log.debug(cacheName+" Removed "+removed+" out of "+totalCount);
		return removed;
	}
	
	protected abstract T getNew(String id) throws Exception;		
	
}

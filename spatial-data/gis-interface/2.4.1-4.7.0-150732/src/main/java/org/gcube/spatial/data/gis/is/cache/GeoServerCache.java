package org.gcube.spatial.data.gis.is.cache;

import java.util.Set;
import java.util.SortedSet;
import java.util.concurrent.ThreadLocalRandom;

import org.gcube.spatial.data.geonetwork.utils.ScopeUtils;
import org.gcube.spatial.data.gis.Configuration;
import org.gcube.spatial.data.gis.ResearchMethod;
import org.gcube.spatial.data.gis.is.AbstractGeoServerDescriptor;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class GeoServerCache<T extends AbstractGeoServerDescriptor> {

	private static final ResearchMethod DEFAULT_RESEARCH_METHOD=ResearchMethod.MOSTUNLOAD;
	


	protected GeoServerCache() {

	}

	public SortedSet<T> getDescriptorSet(Boolean forceUpdate) {
		return getTheCache(forceUpdate);
	}

	public T getDefaultDescriptor() {
		return getDefaultDescriptor(false);
	}

	public T getDefaultDescriptor(Boolean forceUpdate) {
		return getDescriptor(forceUpdate,getDefaultMethod());
	}

	public T getDescriptor(Boolean forceUpdate, ResearchMethod method) {
		SortedSet<T> cache=getTheCache(forceUpdate);
		log.debug("Access to {} instance in {} ",method,ScopeUtils.getCurrentScope());
		switch(method){
			case MOSTUNLOAD : 				
				return cache.first();
			
			case RANDOM : {
						int size=cache.size();
						int randomIndex= ThreadLocalRandom.current().nextInt(0, size);
						log.debug("Accessing {} out of {} descriptors ",randomIndex,size);
				return (T) cache.toArray()[randomIndex];
			}
			default : throw new RuntimeException("Unrecognized method "+method);
		}
	}
	
	protected ResearchMethod getDefaultMethod(){
		try{
			return ResearchMethod.valueOf(Configuration.get().getProperty(Configuration.IS_ACCESS_POLICY));
		}catch(Throwable t){
			log.warn("Unable to read research method. Using default {}. Cause : ",DEFAULT_RESEARCH_METHOD,t);
			return DEFAULT_RESEARCH_METHOD;
		}
	}
	
	
	
	
	protected abstract SortedSet<T> getTheCache(Boolean forceUpdate);
	
//	private synchronized ConcurrentSkipListSet<CachedGeoServerDescriptor> getTheCache(Boolean forceUpdate){
//		if(forceUpdate || theCache==null || System.currentTimeMillis()-lastUpdate>Configuration.getTTL(Configuration.IS_CACHE_TTL)){
//			try{
//				log.debug("Going to retrieve information from IS..");
//				List<CachedGeoServerDescriptor> retrieved=queryforGeoServer();
//				theCache=new ConcurrentSkipListSet<>(retrieved);
//				log.trace("Retrieved {} instances in {}",theCache.size(),ScopeUtils.getCurrentScope());
//				lastUpdate=System.currentTimeMillis();
//			}catch(IOException e){
//				log.error("Unable to query IS ",e);
//			}
//		}
//		return theCache;
//	}
//
//

}

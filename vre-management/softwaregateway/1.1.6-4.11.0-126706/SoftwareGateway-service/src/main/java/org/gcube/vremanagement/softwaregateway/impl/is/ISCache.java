package org.gcube.vremanagement.softwaregateway.impl.is;

import java.io.File;
import java.net.URL;
import java.util.List;

import org.gcube.vremanagement.softwaregateway.impl.coordinates.Coordinates;
import org.gcube.vremanagement.softwaregateway.impl.coordinates.GCubeCoordinates;
import org.gcube.vremanagement.softwaregateway.impl.coordinates.MavenCoordinates;
import org.gcube.vremanagement.softwaregateway.impl.packages.GCubePackage;
import org.gcube.vremanagement.softwaregateway.impl.packages.MavenPackage;
import org.gcube.vremanagement.softwaregateway.impl.porttypes.ServiceContext;
import org.gcube.common.core.informationsystem.ISException;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheException;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import net.sf.ehcache.event.CacheEventListenerAdapter;


/**
 * Implements a cache System for IS service
 * @author Roberto Cirillo (ISTI - CNR)
 *
 */
public class ISCache extends ISManager {
	
	private CacheManager cacheManager;
	private Cache cache;
//	private static final int MAXIMUM_SIZE=1000;
//	private static final boolean OVERFLOW_TO_DISK=false;
	private static final String CACHE_NAME="softwaregateway-iscache";
	private static ISCache singleton;
	public static final String CACHE_STRING_SEPARATOR="-_-";
	
	/**
	 * 
	 * @param gCubeScope
	 */
	private ISCache() {
		super(null);
//STATIC WAY	
		logger.trace("creating IS cache..");
		try {
			cacheManager = getDefaultCacheManagerConfiguration();
			logger.trace("manager created");
			String[] cacheNames = cacheManager.getCacheNames();
			for (String s : cacheNames) {
				logger.debug("ISCaChE "+s);
			}
	
			cache = cacheManager.getCache(CACHE_NAME);
			if(cache==null){
				logger.info("Adding the cache: "+CACHE_NAME);
				cacheManager.addCache(CACHE_NAME);
				cache = cacheManager.getCache(CACHE_NAME);
			}
			logger.trace("SG cache created");
			cache.getCacheEventNotificationService().registerListener(new CacheEventListenerAdapter() {
				
				@Override
				public void notifyRemoveAll(Ehcache cache) {
					logger.info(" notifyRemoveAll in cache "+cache.getName());
					
				}
				
				@Override
				public void notifyElementUpdated(Ehcache cache, Element element)
						throws CacheException {
					logger.info(" notifyElementUpdated in cache "+cache.getName()+" element: "+element.getKey());
	
				}
				
				@Override
				public void notifyElementRemoved(Ehcache cache, Element element)
						throws CacheException {
					logger.info(" notifyElementRemoved in cache "+cache.getName()+" element: "+element.getKey());
	
				}
				
				@Override
				public void notifyElementPut(Ehcache cache, Element element)
						throws CacheException {
					logger.info(" notifyElementPut in cache "+cache.getName()+" element: "+element.getKey());
	
				}
				
				@Override
				public void notifyElementExpired(Ehcache cache, Element element) {
					logger.info(" notifyElementExpired in cache "+cache.getName()+" element: "+element.getKey());
	
				}
				
				@Override
				public void notifyElementEvicted(Ehcache cache, Element element) {
					logger.info(" notifyElementEvicted in cache "+cache.getName()+" element: "+element.getKey());
	
				}
				
				@Override
				public void dispose() {
					// TODO Auto-generated method stub
					
				}
			});
			logger.trace("event listener successful instantiated");
		} catch (Exception e) {
			logger.error("Failed to create IS cache", e);
		}
		logger.trace("returning from cache creation");
	}

	public static ISCache getInstance(){
		if(singleton==null) 
			singleton=new ISCache();
		return singleton;
	}
	
	
	/**
	 * Not implemented for cache
	 * 
	 */
	public void updateProfile(String xml){
		;
	}
	
	/**
	 * Not implemented for cache
	 * 
	 */
	public List<URL> getMavenConfiguration(){
		List<URL> list=null;
		return list;
	}

	/**
	 * Put a new element in the cache
	 * @param key
	 * @param value
	 */
	protected void put( Coordinates key,  Object value)
    {
        cache.put(new Element(key, value));
    }

	/**
	 * check if the key parameter is present in the cache
	 * @param key
	 * @return
	 */
//	private MavenCoordinates get( GCubeCoordinates key) 
//    {
//        Element element = cache.get(key);
//        if (element != null) {
//            return (MavenCoordinates) element.getValue();
//        }
//        return null;
//    }

	/**
	 * check if the gCubeC parameter is present in the cache
	 */
	@Override
	public MavenCoordinates getMavenCoordinates(Coordinates gcubeC)
			throws ISException {
        Element element = cache.get(gcubeC);
        if (element != null) {
            return (MavenCoordinates) element.getValue();
        }
        return null;

	}

	/**
	 * check if the mavenC parameter is present in the cache
	 * @param mavenC
	 * @return
	 */
	public GCubeCoordinates getGcubeCoordinates(MavenCoordinates mavenC) {
	       Element element = cache.get(mavenC);
	        if (element != null) {
	        	logger.info("CACHE ELEMENT EXTRACTED");
	            return (GCubeCoordinates) element.getValue();
	        }
	        return null;

	}
	
	/**
	 * check if the gcubeC parameter is present in the cache
	 * @param gcubeC
	 * 
	 */
	@Override
	public List<MavenPackage> getMavenPackagesCoordinates(GCubeCoordinates gcubeC){
	       Element element = cache.get(gcubeC);
	        if (element != null) {
	            return (List<MavenPackage>) element.getValue();
	        }
	        return null;

	}

	/**
	 * check if the gcubeC parameter is present in the cache
	 * @param gcubeC
	 * 
	 */
	public List<GCubePackage> getPluginCoordinates(Coordinates gcubeC) {
	       Element element = cache.get(gcubeC);
	        if (element != null) {
	            return (List<GCubePackage>) element.getValue();
	        }
	        return null;

	}

	/**
	 * check if the gcubeC parameter is present in the cache
	 * @param coordinates
	 * @return
	 */
	public List<GCubePackage> getGCubePackagesCoordinates(
			Coordinates coordinates) {
	       Element element = cache.get(coordinates);
	        if (element != null) {
	            return (List<GCubePackage>) element.getValue();
	        }
	        return null;
	}

	private CacheManager getDefaultCacheManagerConfiguration() {
		//String cfgDir= (String)ServiceContext.getContext().getProperty("configDir", false);
		//ServiceContext.getContext().getFile("ehcache.xml");
		//logger.debug("ConfigDir for cache: "+cfgDir);
		//logger.debug("path to file: " +cfgDir+File.separator+"ehcache.xml");
		logger.debug("path to file " + ServiceContext.getContext().getFile("ehcache.xml").getAbsolutePath());
		return CacheManager.create(ServiceContext.getContext().getFile("ehcache.xml").getAbsolutePath());
	}

	public void remove(Coordinates coordinates) {
		logger.debug("remove from IS cache element: "+coordinates.getServiceClass()+" "+coordinates.getServiceName()+" "+coordinates.getServiceVersion()+" "+coordinates.getPackageName()+" "+coordinates.getPackageVersion());
		cache.remove(coordinates);
		
	}
	
	/**
	 * @param coordinates
	 */
	public String buildGCubeCoordinatesCacheInputString(Coordinates coordinates) {
			return coordinates.getServiceClass()+CACHE_STRING_SEPARATOR+coordinates.getServiceName()+CACHE_STRING_SEPARATOR+coordinates.getServiceVersion()+CACHE_STRING_SEPARATOR+coordinates.getPackageName()+CACHE_STRING_SEPARATOR+coordinates.getPackageVersion();
		
	}
	
	/**
	 * @param coordinates
	 */
	public String buildMavenCoordinatesCacheInputString(Coordinates coordinates) {
			return coordinates.getGroupId()+CACHE_STRING_SEPARATOR+coordinates.getArtifactId()+CACHE_STRING_SEPARATOR+coordinates.getVersion();
	}

}

package org.gcube.vremanagement.softwaregateway.impl.repositorymanager.cache;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.vremanagement.softwaregateway.impl.coordinates.Coordinates;
import org.gcube.vremanagement.softwaregateway.impl.coordinates.MavenCoordinates;
import org.gcube.vremanagement.softwaregateway.impl.exceptions.ServiceNotAvaiableFault;
import org.gcube.vremanagement.softwaregateway.impl.porttypes.ServiceContext;
import org.gcube.vremanagement.softwaregateway.impl.repositorymanager.RepositoryManager;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheException;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import net.sf.ehcache.event.CacheEventListenerAdapter;

public class NexusCache  extends RepositoryManager {

	private CacheManager cacheManager;
	private Cache cache;
	protected final GCUBELog logger = new GCUBELog(NexusCache.class);
	private static final int MAXIMUM_SIZE=1000;
	private static final boolean OVERFLOW_TO_DISK=false;
	private static final String CACHE_NAME="softwaregateway-nexuscache";
	public static final String CACHE_STRING_SEPARATOR="-_-";
	private static NexusCache singleton;
	
	private NexusCache(String[] mavenServerList) {
		super(mavenServerList);
		logger.debug("Instantiated");
//STATIC WAY	
		try{
			cacheManager = getDefaultCacheManagerConfiguration();
			logger.info("cachemanager returned is: "+cacheManager);
			String[] cacheNames = cacheManager.getCacheNames();
			logger.debug("NEXUSCACHE FOUND IN CONFIGURATION FILE: ");
			for (String s : cacheNames) {
				logger.debug("CaChE "+s);
			}

			cache = cacheManager.getCache(CACHE_NAME);

		}catch(Exception e){
			logger.error("error cache: "+e.getMessage());
			e.printStackTrace();
		}
		if(cache==null){
			logger.info("CACHE ADDED: "+CACHE_NAME);
			cacheManager.addCache(CACHE_NAME);
			cache = cacheManager.getCache(CACHE_NAME);
		}
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
				String type=getElementType(element);
				if(type.equalsIgnoreCase("url")){
					// no actions is requested
				}else if(type.equalsIgnoreCase("mavenCoordinates") || type.equalsIgnoreCase("gcubeCoordinates")){
					String url=(String)element.getValue();
					 int statusCode=0;
					try {
						statusCode = HttpClients.createSystem().execute(new HttpGet(url)).getStatusLine().getStatusCode();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					if(statusCode==HttpStatus.SC_OK){
						logger.debug("the element "+(String)element.getKey()+" is alive ");
						put(element.getKey(),element.getValue());
					}
				}else{
					logger.info("element in cache not recognized");
				}
				
			}
			
			private String getElementType(Element element) {
				logger.debug("check Element");
				String type="unknowed";
				String key=(String)element.getKey();
				String[] params=key.split(CACHE_STRING_SEPARATOR);
				if(params!= null){
					logger.debug("number of params found in cache string: "+params.length);
					if(params.length<2){
						type= "url";
					}else if(params.length==4){
						type="mavenCoordinates";
					}else if (params.length==6){
						type="gcubeCoordinates";
					}
					logger.debug("element in cache is a "+type+" object");

				}
				return type;
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
		logger.debug("cache instantiated");
	}

	public static NexusCache getInstance(String[] mavenServerList){
		if(singleton== null)
			singleton = new NexusCache(mavenServerList);
		return singleton;
	}


	@Override
	public String get(Object mavenC, String extension, String classifier)
			throws MalformedURLException, ServiceNotAvaiableFault {
		MavenCoordinates mc=(MavenCoordinates)mavenC;
		if(mc!= null){
//			MavenCoordinates mc=mcList.get(0);
//			String cacheCoordinates=mc.getGroupId()+CACHE_STRING_SEPARATOR+mc.getArtifactId()+CACHE_STRING_SEPARATOR+mc.getVersion()+CACHE_STRING_SEPARATOR+extension;
			String cacheCoordinates=buildMavenCoordinatesCacheInputString(mc, extension, classifier);
//			String url=(String)cache.get(cacheCoordinates);
			Element element =cache.get(cacheCoordinates);
		    if (element != null) {
		    	logger.info("CACHE FETCHING");
		        return (String) element.getValue();
		    }
		}
		return null;
	}

	@Override
	public String extractDepsFromMavenEmb(String url)
			throws ServiceNotAvaiableFault {
		Element element =cache.get(url);
	    if (element != null) {
	    	logger.info("CACHE FETCHING");
	        return (String) element.getValue();
	    }

		return null;
	}


	@Override
	public String searchArtifact(String baseUrl, String groupName,
			String artifact, String extension, String ver, boolean pom, String classifier)
			throws MalformedURLException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Put a new element in the cache
	 * @param key
	 * @param value
	 */
	public void put( Object key,  Object value)
    {
		logger.info("CACHE: ELEMENT INSERTING");
        cache.put(new Element(key, value));
    }

	
	public void remove(String cacheElement){
		logger.info("cache element deleted: "+cacheElement);
		cache.remove(cacheElement);
	}
	
	
	private CacheManager getDefaultCacheManagerConfiguration() {
		String cfgDir= (String)ServiceContext.getContext().getProperty("configDir", false);
		logger.debug("ConfigDir for cache: "+cfgDir);
		logger.debug("path to file: " +cfgDir+File.separator+"ehcache.xml");
		File cacheFile= new File(cfgDir+File.separator+"ehcache.xml");
		if(cacheFile.exists()){
			logger.debug("ehcache.xml exist");
			if(cacheFile.canRead())
				logger.debug("ehcache.xml canRead");
			else
				logger.debug("ehcache.xml can't Read");
			if(cacheFile.canWrite())
				logger.debug("ehcache.xml canWrite");
			else
				logger.debug("ehcache.xml can't Write");

		}else
			logger.debug("ehcache.xml is unreachable");
		logger.debug("try to create cache manager ");
		return CacheManager.create(cfgDir+File.separator+"ehcache.xml");
	}


	@Override
	public String getSALocation(File tmpTargetDirectory, List<MavenCoordinates> mcList,
			Coordinates coordinates) throws MalformedURLException,
			ServiceNotAvaiableFault, IOException, Exception {
		String cacheInputString=buildGCubeCoordinatesCacheInputString(coordinates, "tar.gz", "servicearchive");
		Element element= cache.get(cacheInputString);
		if(element!= null)
			return (String)element.getValue();
		else
			return null;
	}


	/**
	 * Build a cache input string
	 * @param coordinates
	 */
	public String buildGCubeCoordinatesCacheInputString(Coordinates coordinates, String extension, String classifier) {
		if(classifier==null)
			return coordinates.getServiceClass()+CACHE_STRING_SEPARATOR+coordinates.getServiceName()+CACHE_STRING_SEPARATOR+coordinates.getServiceVersion()+CACHE_STRING_SEPARATOR+coordinates.getPackageName()+CACHE_STRING_SEPARATOR+coordinates.getPackageVersion()+CACHE_STRING_SEPARATOR+extension;
		else
			return coordinates.getServiceClass()+CACHE_STRING_SEPARATOR+coordinates.getServiceName()+CACHE_STRING_SEPARATOR+coordinates.getServiceVersion()+CACHE_STRING_SEPARATOR+coordinates.getPackageName()+CACHE_STRING_SEPARATOR+coordinates.getPackageVersion()+CACHE_STRING_SEPARATOR+extension+CACHE_STRING_SEPARATOR+classifier;
	}
	
	/**
	 * Build a cache input string
	 * @param coordinates
	 */
	public String buildMavenCoordinatesCacheInputString(Coordinates coordinates, String extension, String classifier) {
		if(classifier==null)
			return coordinates.getGroupId()+CACHE_STRING_SEPARATOR+coordinates.getArtifactId()+CACHE_STRING_SEPARATOR+coordinates.getVersion()+CACHE_STRING_SEPARATOR+extension;
		else
			return coordinates.getGroupId()+CACHE_STRING_SEPARATOR+coordinates.getArtifactId()+CACHE_STRING_SEPARATOR+coordinates.getVersion()+CACHE_STRING_SEPARATOR+extension+CACHE_STRING_SEPARATOR+classifier;
	}

}

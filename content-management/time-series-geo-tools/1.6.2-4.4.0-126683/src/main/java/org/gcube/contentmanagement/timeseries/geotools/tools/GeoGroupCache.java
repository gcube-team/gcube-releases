package org.gcube.contentmanagement.timeseries.geotools.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.contentmanagement.timeseries.geotools.utils.Tuple;

public class GeoGroupCache implements Serializable{

	private static final long serialVersionUID = 1L;
	HashMap<String,Tuple<String>> cache;
	boolean altered = false;
	public String cacheFile;
	
	static GeoGroupCache singletonCache;
	static int maxElements = 2000;

	
	public GeoGroupCache(String cacheFile){
		cache = new HashMap<String, Tuple<String>>();
		this.cacheFile = cacheFile;
	}
	
	public Tuple<String> getCachedElement(String timeSeriesName, String geoServerGroup){
		return cache.get(timeSeriesName+":"+geoServerGroup);
	}
	
	public void removeCachedElement(String timeSeriesName, String filterName){
		cache.remove(timeSeriesName+":"+filterName);
		try{
			saveCache(this,cacheFile);
		}catch(Exception e){
			AnalysisLogger.getLogger().debug("Error: Impossible to save cache file");
		}
	}
	
	public void addCacheElement(String timeSeriesName, String filterName, String... elements){
		
		Tuple<String> groupAnFilter = new Tuple<String>(elements);
		if (cache.size()>maxElements){
			for (String key:cache.keySet()){
				cache.remove(key);
				break;
			}
		}
		cache.put(timeSeriesName+":"+filterName, groupAnFilter);
		
		try{
			saveCache(this,cacheFile);
		}catch(Exception e){
			AnalysisLogger.getLogger().debug("Error: Impossible to save cache file");
		}
		altered = true;
	}
	
	public static void initInstance(String filePath) throws Exception{
		getInstance(filePath);
	}
	
	public static GeoGroupCache getInstance(String filePath) throws Exception{
		
		if (singletonCache==null){
			try{
				singletonCache = getCache(filePath);
			}catch(Exception e){
				singletonCache = new GeoGroupCache(filePath);
			}
			/*
			if (refresher!=null)
				refresher.cancel();
			
			refresher = new Timer();
			refresher.schedule(singletonCache.new RefreshResources(filePath), refreshTime,refreshTime);
			*/
		}
		
		return singletonCache;
	}
	
	public static void addToCache(String timeSeriesName, String geoServerGroup,String filterName) throws Exception{
		if (singletonCache!=null)
			singletonCache.addCacheElement(timeSeriesName, geoServerGroup, filterName);
	}
	
		
	public static GeoGroupCache getCache(String filePath) throws Exception{
		ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(new File(filePath)));
		GeoGroupCache g = (GeoGroupCache)inputStream.readObject();
		inputStream.close();
		return g;
	}

	public static void saveCache(GeoGroupCache cache,String filePath) throws Exception{
		ObjectOutputStream outStream = new ObjectOutputStream(new FileOutputStream(new File(filePath)));
		outStream.writeObject(cache);
		outStream.close();
	}
	
	class RefreshResources extends TimerTask{

		String cacheFile;
		public RefreshResources(String cacheFile) {
			this.cacheFile = cacheFile;
		}
		
		@Override
		public void run() {
				if ((singletonCache!=null) && (singletonCache.altered)){
						try{
							saveCache(singletonCache, cacheFile);
						}catch(Exception e) {
							AnalysisLogger.getLogger().debug("RefreshResources-> unable to save cache");
						}
				}
		}
		
	}
	
}

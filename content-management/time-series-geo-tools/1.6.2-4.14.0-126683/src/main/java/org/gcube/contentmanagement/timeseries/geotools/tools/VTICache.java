package org.gcube.contentmanagement.timeseries.geotools.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;

import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.contentmanagement.timeseries.geotools.utils.Tuple;

public class VTICache implements Serializable{

	private static final long serialVersionUID = 1L;
	HashMap<String,Tuple<String>> cache;
	public String cacheFile;
	
	static VTICache singletonCache;
	static int maxElements = 2000;

	
	public VTICache(String cacheFile){
		cache = new HashMap<String, Tuple<String>>();
		this.cacheFile = cacheFile;
	}
	
	public Tuple<String> getCachedElement(String timeSeriesName){
		return cache.get(timeSeriesName);
	}
	
	public void removeCachedElement(String timeSeriesName){
		cache.remove(timeSeriesName);
		try{
			saveCache(this,cacheFile);
		}catch(Exception e){
			AnalysisLogger.getLogger().debug("Error: Impossible to save cache file");
		}
	}
	
	public void addCacheElement(String timeSeriesName, String... identifiers){
		
		Tuple<String> groupAnFilter = new Tuple<String>(identifiers);
		if (cache.size()>maxElements){
			for (String key:cache.keySet()){
				cache.remove(key);
				break;
			}
		}
		cache.put(timeSeriesName, groupAnFilter);
		
		try{
			saveCache(this,cacheFile);
		}catch(Exception e){
			AnalysisLogger.getLogger().debug("Error: Impossible to save cache file");
		}

	}
	
	public static void initInstance(String filePath) throws Exception{
		getInstance(filePath);
	}
	
	public static VTICache getInstance(String filePath) throws Exception{
		
		if (singletonCache==null){
			try{
				singletonCache = getCache(filePath);
			}catch(Exception e){
				singletonCache = new VTICache(filePath);
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
	
	public static void addToCache(String timeSeriesName, String geoServerGroup) throws Exception{
		if (singletonCache!=null)
			singletonCache.addCacheElement(timeSeriesName, geoServerGroup);
	}
	
		
	public static VTICache getCache(String filePath) throws Exception{
		ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(new File(filePath)));
		VTICache g = (VTICache)inputStream.readObject();
		inputStream.close();
		return g;
	}

	public static void saveCache(VTICache cache,String filePath) throws Exception{
		ObjectOutputStream outStream = new ObjectOutputStream(new FileOutputStream(new File(filePath)));
		outStream.writeObject(cache);
		outStream.close();
	}
	
	
}

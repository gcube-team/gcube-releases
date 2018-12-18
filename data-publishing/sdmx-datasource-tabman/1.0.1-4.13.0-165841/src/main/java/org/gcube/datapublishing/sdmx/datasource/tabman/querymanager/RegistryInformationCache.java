package org.gcube.datapublishing.sdmx.datasource.tabman.querymanager;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.gcube.datapublishing.sdmx.RegistryInformationProvider;
import org.gcube.datapublishing.sdmx.model.Registry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class RegistryInformationCache {

	private class RegistryUrlCacheElement
	{
		String url;
		long timeout;
	}
	
	private long duration;
	private Map<String, RegistryUrlCacheElement> registryUrlMap;
	private Logger logger;
	private Lock readLock;
	private Lock writeLock;
	
	public RegistryInformationCache ()
	{
		this.registryUrlMap = new HashMap<>();
		this.logger = LoggerFactory.getLogger(RegistryInformationCache.class);
		ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
		this.readLock = readWriteLock.readLock();
		this.writeLock = readWriteLock.writeLock();
	}

	public void setDuration (long duration)
	{
		this.duration = duration;
	}
	
	
	public String getRegistryUrl (String scope)
	{
		long currentTime = new Date().getTime();
		this.readLock.lock();
		RegistryUrlCacheElement element = null;

		try
		{
			 element = this.registryUrlMap.get(scope);
			
		}
		finally
		{
			this.readLock.unlock();
		}
		
		
		if (element == null || element.timeout < currentTime)
		{
			element = new RegistryUrlCacheElement();
			this.logger.debug("Url not present for this scope or expired");
			Registry registry = RegistryInformationProvider.getRegistry();
			
			if (registry != null)
			{
				this.logger.debug("Registry information obtained");
				element.url = registry.getEndpoint();
				element.timeout = currentTime+this.duration;
				this.logger.debug("Expiring time "+element.timeout);
				
				this.writeLock.lock();
				try
				{
					this.registryUrlMap.put(scope, element);
				}
				finally 
				{
					this.writeLock.unlock();
				}
				

			}

		}
		
		this.logger.debug("Registry endpoint "+element.url);
		return element.url;
	}
	
}

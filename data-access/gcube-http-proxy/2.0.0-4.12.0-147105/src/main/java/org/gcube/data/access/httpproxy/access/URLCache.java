package org.gcube.data.access.httpproxy.access;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public  class URLCache {

	private Map<String, List<String>> domainMap;
	private static URLCache instance;
	private final Lock readLock;
	private final Lock writeLock;
	private Logger logger;
	
	
	private URLCache ()
	{
		this.logger = LoggerFactory.getLogger(this.getClass());
		this.logger.debug("Generating new cache instance");
		this.domainMap = new HashMap<>();
		ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
		this.readLock = readWriteLock.readLock();
		this.writeLock = readWriteLock.writeLock();
		this.logger.debug("New cache instance generated");
	}

	
	public static URLCache getInstance ()
	{
		if (instance == null) instance = new URLCache();
		
		return instance;
	}
	
	
	public  void  setDomainList (String scope, List<String> domains)
	{
		this.logger.debug("Locking domain list for writing operations");
		this.writeLock.lock();
		
		try
		{
			this.logger.debug("Writing new domain list for thread "+Thread.currentThread().getName());
			
			if (domains == null) domains = new ArrayList<>();
			
			this.domainMap.put(scope, domains);
			this.logger.debug("Writing operation completed");
		}
		finally
		{
			
			this.writeLock.unlock();
			this.logger.debug("Object unlocked");
		}

	}
	
	public List<String> getDomainList (String scope)
	{
		this.readLock.lock();
		
		try
		{
			this.logger.debug("Reading new domain list for thread "+Thread.currentThread().getName());
			return this.domainMap.get(scope);
			
		}
		finally
		{
			this.readLock.unlock();
			this.logger.debug("Object unlocked");
		}
		
		
	}
}

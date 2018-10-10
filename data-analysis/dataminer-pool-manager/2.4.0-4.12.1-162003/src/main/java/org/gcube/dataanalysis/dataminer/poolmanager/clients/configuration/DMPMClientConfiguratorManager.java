package org.gcube.dataanalysis.dataminer.poolmanager.clients.configuration;

import java.io.IOException;
import java.util.Iterator;
import java.util.Properties;

import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.dataanalysis.dataminer.poolmanager.clients.ScopedCacheMap;
import org.gcube.dataanalysis.dataminer.poolmanager.clients.configuration.ConfigurationImpl.CONFIGURATIONS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tmatesoft.svn.core.SVNException;

public class DMPMClientConfiguratorManager
{
	private  final Logger logger;
	private Properties defaultAdmins;
	private String admins;
	
	static DMPMClientConfiguratorManager instance;
	
	private ScopedCacheMap cacheMap; 
	
	private DMPMClientConfiguratorManager ()
	{
		this.admins = null;
		this.cacheMap = new ScopedCacheMap();
		this.logger = LoggerFactory.getLogger(DMPMClientConfiguratorManager.class);
		this.defaultAdmins = new Properties();
		
		try
		{
			this.defaultAdmins.load(this.getClass().getResourceAsStream("/default.admins"));
			this.logger.debug("Default users successfully loaded");
		} catch (Exception e)
		{
			this.logger.error("Unable to get default users",e);
		}
	}
	
	private ClientConfigurationCache getCurrentCache ()
	{
		String currentScope = ScopeProvider.instance.get();
		this.logger.debug("Current scope = "+currentScope);
		this.logger.debug("Getting current configuration cache");
		ClientConfigurationCache cache = this.cacheMap.get(currentScope);
		
		if (cache == null)
		{
			this.logger.debug("Cache not created yet, creating...");
			cache = new ClientConfigurationCache ();
			this.cacheMap.put(currentScope, cache);
			
		}
		
		return cache;
		
		
	}
	

	public static DMPMClientConfiguratorManager getInstance ()
	{
		if (instance == null) instance = new DMPMClientConfiguratorManager();
		
		return instance;
	}
	
	public Configuration getProductionConfiguration ()
	{
		return new ConfigurationImpl(CONFIGURATIONS.PROD, getCurrentCache());
	}
	
	public Configuration getStagingConfiguration ()
	{
		return new ConfigurationImpl(CONFIGURATIONS.STAGE, getCurrentCache());
	}
	
	public String getDefaultAdmins ()
	{
		if (this.admins == null && this.defaultAdmins.isEmpty()) this.admins= "ciro.formisano";
		else if (admins == null)
		{
				Iterator<Object> keys = this.defaultAdmins.keySet().iterator();
				StringBuilder response = new StringBuilder();
				
				while (keys.hasNext())
				{
					String key = (String) keys.next();
					response.append(this.defaultAdmins.getProperty(key)).append(", ");
				}
				
				this.admins = response.substring(0, response.length()-2);
		}
		
		this.logger.debug("Default admins list "+this.admins);
		return this.admins;

	}
		
	
	public static void main(String[] args) throws IOException, SVNException {
		DMPMClientConfiguratorManager a = new DMPMClientConfiguratorManager();
		ScopeProvider.instance.set("/gcube/devNext/NextNext");
		//SecurityTokenProvider.instance.set("708e7eb8-11a7-4e9a-816b-c9ed7e7e99fe-98187548");
		
		System.out.println("RESULT 1"+a.getStagingConfiguration().getSVNCRANDepsList());
		System.out.println("RESULT 2"+a.getProductionConfiguration().getRepository());
		System.out.println("RESULT 3"+a.getStagingConfiguration().getSVNMainAlgoRepo());
		//System.out.println(a.getRepo());
		//System.out.println(a.getAlgoRepo());
		//System.out.println(a.getSVNRepo());

	}
}

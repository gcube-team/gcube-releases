/**
 * 
 */
package org.gcube.portlets.user.tdw.server.datasource;

import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 
 * @author "Giancarlo Panichi" 
 * <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
public class DataSourceFactoryRegistry {
	
	
	protected static DataSourceFactoryRegistry instance;
	
	public static DataSourceFactoryRegistry getInstance()
	{
		if (instance == null) instance = new DataSourceFactoryRegistry();
		return instance;
	}
	

	private static final Logger log = LoggerFactory.getLogger(DataSourceFactoryRegistry.class);
	
	protected Map<String, DataSourceFactory> instancesRegistry = new HashMap<String, DataSourceFactory>();
	
	protected DataSourceFactoryRegistry()
	{
		addInstancesInEnv();
	}
	
	protected void addInstancesInEnv()
	{
		ServiceLoader<DataSourceFactory> loader = ServiceLoader.load(DataSourceFactory.class);
		for (DataSourceFactory factory:loader) add(factory);
	}
	
	/**
	 * Adds a new {@link DataSourceFactory} to the registry.
	 * @param factory the {@link DataSourceFactory} to add.
	 */
	public void add(DataSourceFactory factory)
	{
		log.trace("add factory: "+factory.getId());
		DataSourceFactory old = instancesRegistry.put(factory.getId(), factory);
		if (old!=null) log.warn("A DataSourceFactory instance with id "+old.getId()+" and class "+old.getClass().getCanonicalName()+" was already registered. The old one has been replaced by the new one with class "+factory.getClass().getCanonicalName()+".");
	}
	
	/**
	 * Retrieves the specified {@link DataSourceFactory}.
	 * @param factoryId the {@link DataSourceFactory} id.
	 * @return the {@link DataSourceFactory} if found, <code>null</code> otherwise.
	 */
	public DataSourceFactory get(String factoryId)
	{
		return instancesRegistry.get(factoryId);
	}
	
	public boolean exists(String factoryId)
	{
		return instancesRegistry.containsKey(factoryId);
	}
	
	/**
	 * Removes the specified {@link DataSourceFactory}.
	 * @param factoryId the {@link DataSourceFactory} id.
	 */
	public void remove(String factoryId)
	{
		log.trace("remove factoryId: "+factoryId);
		instancesRegistry.remove(factoryId);
	}

}

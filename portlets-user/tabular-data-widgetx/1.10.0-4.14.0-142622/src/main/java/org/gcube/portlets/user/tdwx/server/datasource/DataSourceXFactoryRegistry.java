/**
 * 
 */
package org.gcube.portlets.user.tdwx.server.datasource;

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
public class DataSourceXFactoryRegistry {
	
	
	protected static DataSourceXFactoryRegistry instance;
	
	public static DataSourceXFactoryRegistry getInstance()
	{
		if (instance == null) instance = new DataSourceXFactoryRegistry();
		return instance;
	}
	

	private static final Logger log = LoggerFactory.getLogger(DataSourceXFactoryRegistry.class);
	
	protected Map<String, DataSourceXFactory> instancesRegistry = new HashMap<String, DataSourceXFactory>();
	
	protected DataSourceXFactoryRegistry()
	{
		addInstancesInEnv();
	}
	
	protected void addInstancesInEnv()
	{
		ServiceLoader<DataSourceXFactory> loader = ServiceLoader.load(DataSourceXFactory.class);
		for (DataSourceXFactory factory:loader) add(factory);
	}
	
	/**
	 * Adds a new {@link DataSourceXFactory} to the registry.
	 * @param factory the {@link DataSourceXFactory} to add.
	 */
	public void add(DataSourceXFactory factory)
	{
		log.trace("add factory: "+factory.getId());
		DataSourceXFactory old = instancesRegistry.put(factory.getId(), factory);
		if (old!=null) log.warn("A DataSourceFactory instance with id "+old.getId()+" and class "+old.getClass().getCanonicalName()+" was already registered. The old one has been replaced by the new one with class "+factory.getClass().getCanonicalName()+".");
	}
	
	/**
	 * Retrieves the specified {@link DataSourceXFactory}.
	 * @param factoryId the {@link DataSourceXFactory} id.
	 * @return the {@link DataSourceXFactory} if found, <code>null</code> otherwise.
	 */
	public DataSourceXFactory get(String factoryId)
	{
		return instancesRegistry.get(factoryId);
	}
	
	public boolean exists(String factoryId)
	{
		return instancesRegistry.containsKey(factoryId);
	}
	
	/**
	 * Removes the specified {@link DataSourceXFactory}.
	 * @param factoryId the {@link DataSourceXFactory} id.
	 */
	public void remove(String factoryId)
	{
		log.trace("remove factoryId: "+factoryId);
		instancesRegistry.remove(factoryId);
	}

}

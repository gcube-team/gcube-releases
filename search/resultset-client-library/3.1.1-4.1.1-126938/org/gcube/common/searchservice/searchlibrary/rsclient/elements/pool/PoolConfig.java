package org.gcube.common.searchservice.searchlibrary.rsclient.elements.pool;

import java.util.Hashtable;

/**
 * Pool configuration
 * 
 * @author UoA
 */
public class PoolConfig {
	/**
	 * The configuration
	 */
	public Hashtable<RSPoolObject.PoolObjectType,PoolObjectConfig> config=null;
	
	/**
	 * Creates a new instance
	 */
	public PoolConfig(){
		this.config=new Hashtable<RSPoolObject.PoolObjectType,PoolObjectConfig>();
	}
	
	/**
	 * Adds a new configuration
	 * 
	 * @param objectConfig teh object configuration
	 * @throws Exception unrecoverable for the operation error occured
	 */
	public void add(PoolObjectConfig objectConfig) throws Exception{
		this.config.put(objectConfig.ObjectType,objectConfig);
	}
	
	/**
	 * Retrieves the configuration for the specific type
	 * 
	 * @param type the type
	 * @return the configuration
	 */
	public PoolObjectConfig get(RSPoolObject.PoolObjectType type){
		return this.config.get(type);
	}
}

package org.gcube.common.searchservice.searchlibrary.rsclient.elements.pool;

import java.util.Calendar;
import java.util.Hashtable;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.apache.log4j.Logger;
import org.gcube.common.searchservice.searchlibrary.rsclient.elements.pool.RSPoolObject.PoolObjectResourceType;
import org.gcube.common.searchservice.searchlibrary.rsclient.elements.pool.RSPoolObject.PoolObjectType;

/**
 * The pool
 * 
 * @author UoA
 */
public class RSPool {
	/**
	 * The Logger used by this class
	 */
	private static Logger log = Logger.getLogger(RSPool.class);
	/**
	 * The pool objects
	 */
	private Hashtable<PoolObjectType,ConcurrentLinkedQueue<RSPoolObject>> pool;
	/**
	 * The pool configuration
	 */
	private PoolConfig poolConfig=null;
	/**
	 * Background updater
	 */
	private PoolPopulateThread worker=null;
	/**
	 * used for synchronization
	 */
	private Object synchMe=new Object();
	
	/**
	 * Creates a new instance
	 * 
	 * @param poolConfig the pool configuration
	 */
	public RSPool(PoolConfig poolConfig){
		this.pool=new Hashtable<PoolObjectType,ConcurrentLinkedQueue<RSPoolObject>>();
		this.poolConfig=poolConfig;
		worker=new PoolPopulateThread(this,synchMe);
		for(PoolObjectType poolType : PoolObjectType.values()){
			if(this.poolConfig.get(poolType)!=null){
				pool.put(poolType,new ConcurrentLinkedQueue<RSPoolObject>());
				worker.addType(poolType);
				log.info("initializing pool updater for type "+poolType.toString());
			}
		}
		worker.start();
	}
	
	/**
	 * Retrieves the specifyed type of object
	 * 
	 * @param type the type of object
	 * @return the object
	 * @throws Exception Unrecoverable for the operation error occured
	 */
	public RSPoolObject GetObject(PoolObjectType type) throws Exception{
		long start=Calendar.getInstance().getTimeInMillis();
		if(this.poolConfig.get(type)!=null){
			log.debug("availalbe pool of type "+type.toString()+" not initialized. Populating");
			this.populateTypeQueue(type);
		}
		RSPoolObject obj=null;
		ConcurrentLinkedQueue<RSPoolObject> objs=this.pool.get(type);
		if(objs==null){
			log.debug("type "+type.toString()+" not included in pool. Initializing single object");
			obj=this.instantiateObject(type);
			log.debug("returning newly created writer in "+(Calendar.getInstance().getTimeInMillis()-start));
			return obj;
		}
		obj=objs.poll();
		if(obj==null){
			log.debug("pool of objects "+type.toString()+" empty. Initializing single object");
			obj=this.instantiateObject(type);
		}
		else log.debug("object found in pool");
		log.debug("returning cached writer in "+(Calendar.getInstance().getTimeInMillis()-start));
		return obj;
	}
	
	/**
	 * Instantiates a new pool object
	 * 
	 * @param type the type of object
	 * @return the object
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public RSPoolObject instantiateObject(PoolObjectType type) throws Exception{
		long start=Calendar.getInstance().getTimeInMillis();
		PoolObjectConfig conf=this.poolConfig.get(type);
		if(conf==null){
			log.debug("creating default config for single instantiation");
			conf=new PoolObjectConfig();
			conf.FlowControl=false;
			conf.MaxSize=0;
			conf.MinSize=0;
			conf.ObjectType=type;
			conf.ResourceType=PoolObjectResourceType.WSRFType;
			conf.WellFormed=false;
		}
		log.debug("instantiating object");
		RSPoolObject obj=RSPoolObject.getPoolObjectInstance(type,conf);
		log.debug("writer instantiation took "+(Calendar.getInstance().getTimeInMillis()-start));
		return obj;
	}
	
	/**
	 * Retrieves the pool configuration
	 * 
	 * @return the pool configuration
	 */
	public PoolConfig getConfig(){
		return this.poolConfig;
	}
	
	/**
	 * retrieves the current number of pool objects in specified pool
	 * 
	 * @param type thge type of object 
	 * @return the number 
	 */
	public int poolSizeOfType(PoolObjectType type){
		return this.pool.get(type).size();
	}
	
	/**
	 * populates the respective pool queue with the nessecary number of objects
	 * 
	 * @param number the number of objects
	 * @param type the type of objects
	 * @throws Exception An unrecoverable for the operation error occured
	 */
	public void addToPool(int number,PoolObjectType type) throws Exception{
		for(int i=0;i<number;i+=1) this.pool.get(type).add(this.instantiateObject(type));
	}
	
	/**
	 * Warms up the specific type cache
	 * 
	 * @param type the type of object
	 */
	private void populateTypeQueue(PoolObjectType type){
		long start=Calendar.getInstance().getTimeInMillis();
		ConcurrentLinkedQueue<RSPoolObject> objs=this.pool.get(type);
		PoolObjectConfig conf=this.poolConfig.get(type);
		if(objs==null || conf==null){
			log.error("could not find  config or initialized pool for type "+type.toString());
			return;
		}
		if(objs.size()<conf.MinSize){
			log.debug("marking type "+type.toString()+" for repopulation");
			synchronized(this.synchMe){
				this.worker.addType(type);
				this.synchMe.notify();
			}
		}
		log.debug("Type queue population notifivation took "+(Calendar.getInstance().getTimeInMillis()-start));
	}
}

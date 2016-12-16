package org.gcube.common.searchservice.searchlibrary.rsclient.elements.pool;

import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.log4j.Logger;
import org.gcube.common.searchservice.searchlibrary.rsclient.elements.pool.RSPoolObject.PoolObjectType;

/**
 * Background polulation od 
 * 
 * @author UoA
 */
public class PoolPopulateThread extends Thread{
	/**
	 * The Logger used by this class
	 */
	private static Logger log = Logger.getLogger(PoolPopulateThread.class);
	/**
	 * the pool configuration
	 */
	private RSPool pool=null;
	/**
	 * Used for synchronization
	 */
	private Object synchMe=new Object();
	/**
	 * The types of pool objects to update
	 */
	private ConcurrentLinkedQueue<PoolObjectType> typesToInclude=null;
	
	/**
	 * Creates a new instance
	 * 
	 * @param pool the pool
	 * @param synchMe used for synchronization
	 */
	public PoolPopulateThread(RSPool pool,Object synchMe){
		this.pool=pool;
		this.synchMe=synchMe;
		this.typesToInclude=new ConcurrentLinkedQueue<PoolObjectType>();
	}
	
	/**
	 * Add a type to include in the population
	 * 
	 * @param type the type
	 */
	public void addType(PoolObjectType type){
		log.debug("Pool to be populated with "+type.toString());
		this.typesToInclude.add(type);
	}
	
	/**
	 * get next type to include
	 * 
	 * @return the type
	 */
	public PoolObjectType getType(){
		return this.typesToInclude.poll();
	}
	
	/**
	 * @see java.lang.Runnable#run()
	 */
	public void run(){
		while(true){
			try{
				PoolObjectType nextType=null;
				synchronized(this.synchMe){
					while(true){
						nextType=this.getType();
						if (nextType!=null) break;
						try{
							log.debug("pool thread yielding");
							this.synchMe.wait();
						}catch(Exception e){}
					}
				}
				log.debug("pool thread awake");
				int toAdd=this.pool.getConfig().get(nextType).MaxSize-this.pool.poolSizeOfType(nextType);
				log.debug("populating pool of "+nextType.toString()+" with "+toAdd+" objects");
				this.pool.addToPool(toAdd,nextType);
			}catch(Exception e){
				log.error("error populating pool. continuing",e);
			}
		}
	}
}

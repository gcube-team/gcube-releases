/**
 * 
 */
package org.gcube.portlets.admin.gcubereleases.server.pool;

import java.util.HashMap;

import javax.persistence.EntityManagerFactory;

/**
 * 
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Mar 2, 2015
 */
public class ThreadPoolMng {
	

	private static HashMap<String, ThreadPoolQuery> INSTANCES = new HashMap<String, ThreadPoolQuery>(2);
	
	/**
	 * Instantiates a new thread pool mng.
	 */
	private ThreadPoolMng() {
	}
	
	/**
	 * Gets the single instance of ThreadPoolMng.
	 * @param <T>
	 *
	 * @param queryIdentifier the query identifier
	 * @param entityMngFactory the entity mng factory
	 * @return single instance of ThreadPoolMng
	 * @throws Exception the exception
	 */
	public static synchronized <T> ThreadPoolQuery<T> getInstance(String queryIdentifier, EntityManagerFactory entityMngFactory) throws Exception{
		
		if(queryIdentifier==null)
			throw new Exception("Query Identifier is null");
		
		ThreadPoolQuery threadPool = INSTANCES.get(queryIdentifier);
		
		if(threadPool==null){
			threadPool = new ThreadPoolQuery(entityMngFactory, 1);
			INSTANCES.put(queryIdentifier, threadPool);
		}
		
		return threadPool;

	}
}

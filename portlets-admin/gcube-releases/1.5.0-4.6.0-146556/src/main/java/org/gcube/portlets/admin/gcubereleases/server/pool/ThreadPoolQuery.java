/**
 * 
 */
package org.gcube.portlets.admin.gcubereleases.server.pool;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Jul 30, 2013
 *
 */
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.persistence.EntityManagerFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
 
/**
 * The Class ThreadPoolQuery.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Mar 2, 2015
 * @param <T> the generic type
 */
public class ThreadPoolQuery<T> {
	
	/** The executor. */
	private ExecutorService executor = null;
	private EntityManagerFactory entityMngFactory;
	private int fixedThreadPool = 1;
	protected static Logger logger = LoggerFactory.getLogger(ThreadPoolQuery.class);
	
	/**
	 * Instantiates a new thread pool query.
	 *
	 * @param entityMngFactory the entity mng factory
	 */
	public ThreadPoolQuery(EntityManagerFactory entityMngFactory, int fixedThreadPool) {
		this.entityMngFactory = entityMngFactory;
		this.fixedThreadPool = fixedThreadPool;
		instanceExecutorService();
	}
	
	
	/**
	 * Adds the query worker.
	 *
	 * @param query the query
	 * @return the runnable
	 * @throws Exception the exception
	 */
	public ThreadWorker<T> createWorker(String query) throws Exception{
		if(query==null)
			throw new Exception("Query is null!");
		
		try{
			
			if(executor.isShutdown()){
				logger.error("ExecutorService is Shutdown instancing new");
				instanceExecutorService();
			}
			
			return new ThreadWorker<T>(entityMngFactory.createEntityManager(), query);
		}catch (Exception ex) {
			logger.error("Exception on create worker! Instancing new ExecutorService");
			executor.shutdown();
			instanceExecutorService();
	    }
		
		return null;
	}
	
	/**
	 * Instance executor service.
	 */
	private synchronized void instanceExecutorService(){
		executor = Executors.newFixedThreadPool(fixedThreadPool);
	}
	
	/**
	 * Execute worker.
	 *
	 * @param worker the worker
	 */
	public void executeWorker(Runnable worker){
		executor.execute(worker);
	}
	
	/**
	 * Gets the executor.
	 *
	 * @return the executor
	 */
	public ExecutorService getExecutor() {
		return executor;
	}
}
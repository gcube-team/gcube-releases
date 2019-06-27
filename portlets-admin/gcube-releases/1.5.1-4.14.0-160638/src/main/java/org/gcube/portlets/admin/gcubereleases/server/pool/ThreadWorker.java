/**
 * 
 */
package org.gcube.portlets.admin.gcubereleases.server.pool;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class WorkerThread.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @param <T> the generic type
 * @Jul 30, 2013
 */

public class ThreadWorker<T> implements Runnable {

	private String queryString;
	private EntityManager entityManager;
	protected static Logger logger = LoggerFactory.getLogger(ThreadWorker.class);
	private List<T> queryResults = new ArrayList<T>();
	

	/**
	 * Instantiates a new thread worker.
	 *
	 * @param createEntityManager the create entity manager
	 * @param query the query
	 * @param callback the callback
	 */
	public ThreadWorker(EntityManager createEntityManager, String query) {
		this.entityManager = createEntityManager;
		this.queryString = query;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		synchronized (this) {
			try {

				logger.trace("Run query: " + queryString);
				Query q = entityManager.createQuery(queryString);
				queryResults = (List<T>) q.getResultList();
				this.notify();
			} catch (Exception e) {
				logger.error("Error in WorkerThread queryString: " + queryString, e);

			} finally {
				logger.trace("Closing EntitManager for: "+queryString);
				entityManager.close();
			}
		}
	}

	/**
	 * @return the queryResults
	 */
	public List<T> getQueryResults() {
		return queryResults;
	}
}

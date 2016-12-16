/**
 * 
 */
package org.gcube.informationsystem.cache.consistency.manager.poll;

import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;

import org.apache.axis.message.addressing.EndpointReference;
import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.informationsystem.cache.Srv;
import org.gcube.informationsystem.cache.SrvRegistry;

/**
 * Basic cache refreshment component. It operates based on a simple poll
 * algorithm. On regular time intervals it retrieves from the IS server all the
 * RIs of the services registered to be monitored. It build a new cache object
 * on every refresh cycle and simply assigns it to the original cache object.
 * 
 * @author UoA
 * 
 */
public class CacheRefresher extends Thread {

	/** true if the cache refresher is currently operating */
	private boolean isRefreshing;

	/** time-to-refresh; the thread sleeps for that period of time */
	private long ttr;

	/** time of the last refresh cycle */
	private long refreshCycleTime;

	/** timestamp of the last refresh cycle*/
	private Date lastRefreshDate;

	/** logger */
	private static GCUBELog log = new GCUBELog(CacheRefresher.class);
	
	private SrvRegistry registry = null;

	/**
	 * Public constructor
	 * @param ttr {@link #ttr}
	 * @param registry {@link SrvRegistry} instance
	 * @throws Exception in case of error
	 */
	public CacheRefresher(long ttr, SrvRegistry registry) throws Exception {
		try {
			this.setTtr(ttr);
			this.setRefreshCycleTime(0L);
			this.setRegistry(registry);
		} catch (Exception e) {
			throw new Exception(e);
		}
	}

	/**
	 * Thread operation cycle
	 */
	public void run() {
		while (true) {
			log.info("Initiate Cache Refresh.");
			long start = System.currentTimeMillis();

			Set<Srv> srvSet = this.getRegistry().getSrvs();
			Iterator<Srv> srvIt = srvSet.iterator();
			log.info("Refreshing " + srvSet.size() + " services");
			while(srvIt.hasNext()) {
				Srv srv = srvIt.next();
				try {
					CacheRefreshUtil.refreshService(srv);
					if(log.isDebugEnabled()) {
						EndpointReference[] EPRs = this.getRegistry().getEPRsFor(srv);
						for(int i=0;i<EPRs.length;i++)
							log.debug(EPRs[i].toString());
					}
				} catch (Exception e) {
					log.error("Error in retrieving the EPRs of service {"
							+ srv.toString() + "}");
					log.error(e);
					continue;
				}
			}

			long end = System.currentTimeMillis();
			this.setRefreshCycleTime((end - start));
			this.setLastRefreshDate(Calendar.getInstance().getTime());
			try {
				log.info("Hibernate for " + this.getTtr() + " millis");
				Thread.sleep(this.getTtr());
				log
						.info("Woken Up! This means that I should initiate the refresh procedure.");
			} catch (InterruptedException e) {
				log
						.warn("Got an interrupt. This means that I should initiate the refresh procedure.");
			}
		}
	}

	// Getters / Setters

	/**
	 * @param isRefreshing
	 *            the isRefreshing to set
	 */
	protected void setRefreshing(boolean isRefreshing) {
		this.isRefreshing = isRefreshing;
	}

	/**
	 * @return the isRefreshing
	 */
	protected boolean isRefreshing() {
		return isRefreshing;
	}

	/**
	 * @param ttr
	 *            the ttr to set
	 */
	protected void setTtr(long ttr) {
		this.ttr = ttr;
	}

	/**
	 * @return the ttr
	 */
	protected long getTtr() {
		return ttr;
	}

	/**
	 * @param refreshCycleTime
	 *            the refreshCycleTime to set
	 */
	private void setRefreshCycleTime(long refreshCycleTime) {
		this.refreshCycleTime = refreshCycleTime;
	}

	/**
	 * @return the refreshCycleTime
	 */
	protected long getRefreshCycleTime() {
		return refreshCycleTime;
	}

	/**
	 * @param lastRefreshDate
	 *            the lastRefreshDate to set
	 */
	private void setLastRefreshDate(Date lastRefreshDate) {
		this.lastRefreshDate = lastRefreshDate;
	}

	/**
	 * @return the lastRefreshDate
	 */
	protected Date getLastRefreshDate() {
		return lastRefreshDate;
	}

	/**
	 * @param registry the registry to set
	 */
	private void setRegistry(SrvRegistry registry) {
		this.registry = registry;
	}

	/**
	 * @return the registry
	 */
	private SrvRegistry getRegistry() {
		return registry;
	}

}

/**
 * 
 */
package org.gcube.informationsystem.cache.consistency.manager.poll;

import java.util.Date;
import java.util.Set;

import org.apache.axis.message.addressing.EndpointReference;
import org.gcube.informationsystem.cache.Srv;
import org.gcube.informationsystem.cache.SrvRegistry;
import org.gcube.informationsystem.cache.consistency.manager.ConsistencyManagerIF;

/**
 * Implements the {@link PollManagerMBean} interface. Basically it exposes the
 * CacheRefresher instance
 * 
 * @author UoA
 * 
 */
public class PollManager implements PollManagerMBean {

	/** instance of the {@link CacheRefresher} class */
	CacheRefresher cr = null;

	/** time to refresh; default value set to 10 minutes */
	long ttr = (long) (1000 * 60 * 10);

	/** Constructor */
	public PollManager() {
	}

	/**
	 * @see org.gcube.informationsystem.cache.consistency.manager.poll.PollManagerMBean#getLastRefreshTimestamp()
	 */
	public Date getLastRefreshTimestamp() {
		return this.cr.getLastRefreshDate();
	}

	/**
	 * Get refresh interval in milliseconds
	 * @return Refresh interval in milliseconds
	 * @see org.gcube.informationsystem.cache.consistency.manager.poll.PollManagerMBean#getRefreshTimeInMillis()
	 */
	public long getRefreshTimeInMillis() {
		return this.cr.getTtr();
	}

	/**
	 * @see org.gcube.informationsystem.cache.consistency.manager.poll.PollManagerMBean#refresh()
	 */
	public void refresh() {
		cr.interrupt();
	}

	/**
	 * Get refresh cycle time
	 * @return Refresh cycle time
	 * @see org.gcube.informationsystem.cache.consistency.manager.poll.PollManagerMBean#refreshCycleTime()
	 */
	public long refreshCycleTime() {
		return this.cr.getRefreshCycleTime();
	}

	/**
	 * Set refresh interval in milliseconds
	 * @param refreshTimeInMillis Refresh interval in milliseconds
	 * @see org.gcube.informationsystem.cache.consistency.manager.poll.PollManagerMBean#setRefreshTimeInMillis(long)
	 */
	public void setRefreshTimeInMillis(long refreshTimeInMillis) {
		this.cr.setTtr(refreshTimeInMillis);
	}

	/**
	 * True if currently the cache is being refreshed
	 * @return true if currently the cache is being refreshed
	 * @see org.gcube.informationsystem.cache.consistency.manager.poll.PollManagerMBean#isRefreshing()
	 */
	public boolean isRefreshing() {
		return this.cr.isRefreshing();
	}

	/**
	 * Initialize PollManager
	 * @param registry {@link SrvRegistry} instance
	 * @throws Exception in case of error
	 * @see ConsistencyManagerIF#initialize()
	 */
	public void initialize(SrvRegistry registry) throws Exception {
		this.cr = new CacheRefresher(ttr, registry);
		this.cr.start();
	}

	/**
	 * Get EPRs for the given service
	 * @param service service
	 * @return EPRs for the given type of the given service
	 * @throws Exception in case of error
	 */
	public Set<EndpointReference> getEPRs(Srv service) throws Exception {
		return CacheRefreshUtil.getEPRs(service);
	}

	/**
	 * Get EPRs for the given type of the given service
	 * @param service service
	 * @param serviceType service type
	 * @return EPRs for the given type of the given service
	 * @throws Exception in case of error
	 */
	public Set<EndpointReference> getEPRs(Srv service, String serviceType) throws Exception {
		return CacheRefreshUtil.getEPRs(service, serviceType);
	}
	
	protected static void refreshService(Srv service) throws Exception {
		CacheRefreshUtil.refreshService(service);
	}

}

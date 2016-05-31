/**
 * 
 */
package org.gcube.informationsystem.cache.consistency.manager.poll;

import java.util.Date;

import org.gcube.informationsystem.cache.consistency.manager.ConsistencyManagerIF;

/**
 * Cache Consistency Manager MBean. It defines the interface for the
 * {@link PollManager} component, which acts on polling. More accurately, it
 * polls the IS server, at regular time intervals and refreshes the stale cache.
 * 
 * @author UoA
 * 
 */
public interface PollManagerMBean extends ConsistencyManagerIF {

	/**
	 * Get the refresh time interval in milliseconds (1/1000 second)
	 * @return the refresh time interval in milliseconds (1/1000 second)
	 */
	public long getRefreshTimeInMillis();

	/**
	 * Set the refresh time interval in milliseconds (1/1000 second)
	 * @param refreshTimeInMillis the refresh time interval in milliseconds (1/1000 second)
	 */
	public void setRefreshTimeInMillis(long refreshTimeInMillis);

	/**
	 * Perform an on-demand cache refresh
	 *
	 */
	public void refresh();

	/**
	 * Get the last cache refresh timestamp
	 * @return the last cache refresh timestamp
	 */
	public Date getLastRefreshTimestamp();

	/**
	 * Time needed for the last refresh cycle
	 * @return the time needed for the last refresh cycle
	 */
	public long refreshCycleTime();

	/**
	 * Return true if cache refresh is currently performed; false otherwise
	 * @return true if cache refresh is currently performed; false otherwise 
	 */
	public boolean isRefreshing();

}

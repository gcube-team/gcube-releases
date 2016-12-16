package org.gcube.portlet.user.userstatisticsportlet.server.cache;

/**
 * Utility functions for caches
 * @author Costantino Perciante at ISTI-CNR 
 * (costantino.perciante@isti.cnr.it)
 *
 */
public class CacheUtilities {
	/**
	 * Check if the bean expired
	 * @param beanTimestamp
	 * @param ttl
	 * @return <true> if expired, <false> otherwise
	 */
	public static boolean expired(long beanTimestamp, long ttl){
		
		long currentTime = System.currentTimeMillis();
		
		if((beanTimestamp + ttl) <= currentTime)
			return true;
		else
			return false;
		
	}
}

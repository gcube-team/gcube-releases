/**
 * 
 */
package org.gcube.data.speciesplugin.utils;

import java.util.concurrent.ScheduledThreadPoolExecutor;

/**
 * @author "Federico De Faveri defaveri@isti.cnr.it"
 *
 */
public class SpeciesUpdateScheduler {
	
	protected static final int POOL_SIZE = 5;
	
	protected static ScheduledThreadPoolExecutor executor;

	public static synchronized ScheduledThreadPoolExecutor getInstance()
	{
		if (executor == null) executor = new ScheduledThreadPoolExecutor(POOL_SIZE);
		return executor;
	}
}

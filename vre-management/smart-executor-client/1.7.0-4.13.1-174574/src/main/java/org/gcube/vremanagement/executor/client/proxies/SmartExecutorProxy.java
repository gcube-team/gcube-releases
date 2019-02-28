/**
 * 
 */
package org.gcube.vremanagement.executor.client.proxies;

import org.gcube.vremanagement.executor.api.SmartExecutor;
import org.gcube.vremanagement.executor.client.SmartExecutorClientFactory;
import org.gcube.vremanagement.executor.exception.ExecutorException;

/**
 * @author Luca Frosini (ISTI - CNR)
 * Use {@link SmartExecutorClientFactory} instead to obtain {@link org.gcube.vremanagement.executor.api.rest.SmartExecutor}
 * instead
 */
@Deprecated
public interface SmartExecutorProxy extends SmartExecutor {
	
	/**  
	 * It is the same of calling  
	 * unSchedule(String executionIdentifier, boolean globally)
	 * Passing false to globally 
	 */
	public boolean unSchedule(final String executionIdentifier) throws ExecutorException;
	
}

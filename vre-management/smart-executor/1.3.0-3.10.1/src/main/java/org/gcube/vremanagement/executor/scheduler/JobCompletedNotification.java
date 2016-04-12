/**
 * 
 */
package org.gcube.vremanagement.executor.scheduler;

import java.util.Map;

import org.gcube.vremanagement.executor.plugin.PluginState;
import org.gcube.vremanagement.executor.plugin.PluginStateEvolution;
import org.gcube.vremanagement.executor.plugin.PluginStateNotification;

/**
 * This class is useless is just used to simulate and indicate the code 
 * insertion point the possibility to add multiple notification of an event
 * in the running plugin evolution.
 * Future use of this possibility are possibility to send an email to
 * the job owner, notify a registered process. Send a tweet and so on. 
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 */
@Deprecated
public class JobCompletedNotification implements PluginStateNotification {
	
	/**
	 * Maintain the Execution State 
	 * Iteration - State
	 */
	protected final Map<Integer, PluginState> executionsState;
	
	public JobCompletedNotification(Map<Integer, PluginState> executionsState){
		this.executionsState = executionsState;
	}
	
	@Override
	public void pluginStateEvolution(PluginStateEvolution pluginStateEvolution) throws Exception {
		executionsState.put(pluginStateEvolution.getIteration(), pluginStateEvolution.getPluginState());
	}

}

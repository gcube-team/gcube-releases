/**
 * 
 */
package org.gcube.vremanagement.executor.pluginmanager;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.gcube.vremanagement.executor.exception.AlreadyInFinalStateException;
import org.gcube.vremanagement.executor.plugin.Plugin;
import org.gcube.vremanagement.executor.plugin.PluginDeclaration;
import org.gcube.vremanagement.executor.plugin.PluginState;
import org.gcube.vremanagement.executor.plugin.PluginStateEvolution;
import org.gcube.vremanagement.executor.plugin.PluginStateNotification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 *
 */
public class RunnablePlugin<T extends Plugin<? extends PluginDeclaration>> implements Runnable {
	
	/**
	 * Logger
	 */
	private static Logger logger = LoggerFactory.getLogger(RunnablePlugin.class);
	
	public static final String MAX_LAUNCH_TIMES = "___max_launch_times___";
	
	protected static final String SEPARATOR = "---";
	
	protected final T plugin;
	
	protected final Map<String, Object> inputs;
	
	protected final UUID uuid;
	protected final int iterationNumber;
	protected final List<PluginStateNotification> pluginStateNotifications;
	
	protected PluginState actualState;
	
	public RunnablePlugin(T plugin, Map<String, Object> inputs, 
			UUID uuid, int iterationNumber, List<PluginStateNotification> pluginStateNotifications){
		this.plugin = plugin;
		this.inputs = inputs;
		this.uuid = uuid;
		this.iterationNumber = iterationNumber;
		this.pluginStateNotifications = pluginStateNotifications;
		try {
			setState(PluginState.CREATED);
		} catch (AlreadyInFinalStateException e) {
			logger.error(" --- You should not be here. Seem that the {} is suspended before the istance is created. This is really STRANGE.", 
					uuid);
			throw new RuntimeException(e);
		}
		
	}
	
	
	@Override
	public void run(){
		try {
			setState(PluginState.RUNNING);
			plugin.launch(inputs);
			setState(PluginState.DONE);
		} catch (AlreadyInFinalStateException e1) {
			return;
		} catch(Exception e) {
			logger.trace(String.format("Thread %s failed", this.toString()),e);
			try {
				setState(PluginState.FAILED);
			} catch (AlreadyInFinalStateException e1) {
				return;
			}
			throw new RuntimeException(e);
		} 
	}
	
	/**
	 * @return the plugin
	 */
	public T getPlugin() {
		return plugin;
	}

	/**
	 * @return the launchInputs
	 */
	public Map<String, Object> getInputs() {
		return inputs;
	}
	
	/**
	 * It is up to the plugin update the State of the Running Plugin using 
	 * this facilities function.
	 * @param pluginState
	 * @throws Exception 
	 */
	public synchronized void setState(PluginState pluginState) throws AlreadyInFinalStateException {
		long timestamp =  new Date().getTime();
		if(actualState!=null && actualState.isFinalState()){
			logger.trace("At {} Trying to set {} in {} state, but it was already in the final state {}", timestamp,
					uuid, pluginState.toString(), actualState.toString());
			throw new AlreadyInFinalStateException();
		}
		
		actualState = pluginState;
		for(PluginStateNotification pluginStateNotification : pluginStateNotifications){
			String pluginStateNotificationName = pluginStateNotification.getClass().getSimpleName();
			PluginStateEvolution pluginStateEvolution = new PluginStateEvolution(uuid, iterationNumber, timestamp, plugin.getPluginDeclaration(), pluginState);
			try {
				logger.debug("Adding Plugin State Evolution {} with {}.", pluginStateEvolution, pluginStateNotificationName);
				pluginStateNotification.pluginStateEvolution(pluginStateEvolution);
			} catch(Exception e) {
				logger.error("Unable to Persist Plugin State Evolution {} with {}.", 
						pluginStateEvolution, pluginStateNotificationName);
			}
		}
	}
	
	@Override
	public String toString(){
		return String.format("UUID : %s, Iteration : %d,  Plugin : %s", 
				uuid.toString(),  iterationNumber,  
				plugin.getPluginDeclaration().getName());
	}
	
	/**
	 * Stop the Plugin setting state to {@link PluginState#CANCELLED}
	 * @throws Exception
	 */
	public void stop() throws Exception {
		try{
			setState(PluginState.STOPPED);
			plugin.stop();
		}catch(AlreadyInFinalStateException e){}
		
		Thread.currentThread().interrupt();
	}

}

/**
 * 
 */
package org.gcube.vremanagement.executor.plugin;

import java.util.UUID;

/**
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 *
 */
public class PluginStateEvolution {

	protected final UUID uuid;
	protected final int iteration;
	protected final long timestamp;
	protected final PluginDeclaration pluginDeclaration;
	protected final PluginState pluginState;
	
	/**
	 * 
	 * @param uuid the UUID which identify the current execution
	 * @param timestamp the time of the new {@link PluginState}
	 * @param pluginDeclaration the pluginDeclaration
	 * @param pluginState the {@link PluginState} value
	 * @throws Exception if fails
	 */
	public PluginStateEvolution(UUID uuid, int iteration, long timestamp,
			PluginDeclaration pluginDeclaration, PluginState pluginState) {
		this.uuid = uuid;
		this.iteration = iteration;
		this.timestamp = timestamp;
		this.pluginDeclaration = pluginDeclaration;
		this.pluginState = pluginState;
	}

	/**
	 * @return the uuid
	 */
	public UUID getUuid() {
		return uuid;
	}

	/**
	 * @return the iteration
	 */
	public int getIteration() {
		return iteration;
	}

	/**
	 * @return the timestamp
	 */
	public long getTimestamp() {
		return timestamp;
	}

	/**
	 * @return the pluginDeclaration
	 */
	public PluginDeclaration getPluginDeclaration() {
		return pluginDeclaration;
	}

	/**
	 * @return the pluginState
	 */
	public PluginState getPluginState() {
		return pluginState;
	}
	
	@Override
	public String toString(){
		return String.format("%s :: %s : %s - iteration : %d - timestamp - %d - [%s] - %s", 
				this.getClass().getSimpleName(), 
				uuid.getClass().getSimpleName(), uuid.toString(), 
				iteration, timestamp, pluginDeclaration, pluginState.toString());
	}

}

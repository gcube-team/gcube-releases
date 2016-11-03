/**
 * 
 */
package org.gcube.vremanagement.executor.plugin;

import java.util.UUID;

import javax.xml.bind.annotation.XmlElement;

import org.gcube.vremanagement.executor.exception.InvalidPluginStateEvolutionException;

/**
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 *
 */
public class PluginStateEvolution {

	
	@XmlElement
	protected UUID uuid;
	@XmlElement
	protected int iteration;
	@XmlElement
	protected long timestamp;
	protected PluginDeclaration pluginDeclaration;
	@XmlElement
	protected PluginState pluginState;
	@XmlElement
	protected int percentage;
	
	public PluginStateEvolution(){
		
	}
	
	/**
	 * 
	 * @param uuid the UUID which identify the current execution
	 * @param timestamp the time of the new {@link PluginState}
	 * @param pluginDeclaration the pluginDeclaration
	 * @param pluginState the {@link PluginState} value
	 * @throws Exception if fails
	 */
	public PluginStateEvolution(UUID uuid, int iteration, long timestamp,
			PluginDeclaration pluginDeclaration, PluginState pluginState, Integer percentage) throws InvalidPluginStateEvolutionException {
		this.uuid = uuid;
		this.iteration = iteration;
		this.timestamp = timestamp;
		this.pluginDeclaration = pluginDeclaration;
		this.pluginState = pluginState;
		switch (pluginState) {
			case CREATED:
				this.percentage = 0;
				break;
	
			default:
				if(percentage<0 || percentage>100){
					throw new InvalidPluginStateEvolutionException("Percentage must be beetween 0 and 100");
				}
				this.percentage = percentage;
				break;
		}
		
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
	
	/**
	 * @return
	 */
	public Integer getPercentage() {
		return this.percentage;
	}
	
	@Override
	public String toString(){
		return String.format("%s : { %s : %s - iteration : %d - timestamp - %d - {%s} - %s - Percentage : %d }}", 
				this.getClass().getSimpleName(), 
				uuid.getClass().getSimpleName(), uuid.toString(), 
				iteration, timestamp, pluginDeclaration, pluginState.toString(), percentage);
	}

}

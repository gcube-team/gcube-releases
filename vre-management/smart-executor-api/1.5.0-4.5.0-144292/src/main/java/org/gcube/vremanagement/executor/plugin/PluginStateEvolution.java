/**
 * 
 */
package org.gcube.vremanagement.executor.plugin;

import java.util.UUID;

import javax.xml.bind.annotation.XmlElement;

import org.gcube.vremanagement.executor.api.types.Scheduling;
import org.gcube.vremanagement.executor.exception.InvalidPluginStateEvolutionException;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * @author Luca Frosini (ISTI - CNR)
 *
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property=Scheduling.CLASS_PROPERTY)
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
	
	@XmlElement
	protected RunOn runOn;
	
	
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
			PluginDeclaration pluginDeclaration, 
			PluginState pluginState, Integer percentage) throws InvalidPluginStateEvolutionException {
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
	@JsonGetter
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

	/**
	 * @return the runOn
	 */
	protected RunOn getRunOn() {
		return runOn;
	}

	/**
	 * @param runOn the runOn to set
	 */
	protected void setRunOn(RunOn runOn) {
		this.runOn = runOn;
	}
	
	
	
	@Override
	public String toString(){
		return String.format("{"
					+ "uuid:%s,"
					+ "iteration:%d,"
					+ "timestamp:%d,"
					+ "pluginDeclaration:%s,"
					+ "pluginState:%s,"
					+ "percentage:%d"
				+ "}",
				uuid, 
				iteration, 
				timestamp, 
				pluginDeclaration, 
				pluginState, 
				percentage);
	}

}

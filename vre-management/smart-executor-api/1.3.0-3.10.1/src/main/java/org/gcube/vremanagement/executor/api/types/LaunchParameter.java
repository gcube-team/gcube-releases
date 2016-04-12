/**
 * 
 */
package org.gcube.vremanagement.executor.api.types;

import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.gcube.vremanagement.executor.api.types.adapter.MapAdapter;
import org.gcube.vremanagement.executor.utils.MapCompare;
import org.gcube.vremanagement.executor.utils.ObjectCompare;

/**
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 */
@XmlRootElement()
@XmlAccessorType(XmlAccessType.FIELD)
public class LaunchParameter implements Comparable<LaunchParameter>{

	@XmlElement
	/**
	 * The name of the plugin to launch
	 */
	protected String pluginName;
	
	@XmlElement
	/**
	 * The version of the plugin to launch. Version can be null, this means 
	 * that no specific version is required. In other words, null means any 
	 * version.
	 */
	protected String pluginVersion;
	
	@XmlJavaTypeAdapter(MapAdapter.class)
	/**
	 * The Plugin Capabilities which has to be satisfied to launch the
	 * plugin instance execution. The SmartExectuor Service check that this 
	 * capabilities match the capabilities declared from the discovered plugin.
	 * If the capabilities does not match the service will not launch the 
	 * execution.
	 * Plugin Capabilities can be null, this means that no capabilities have to
	 * be satisfied. In other words, null means that no constraint are required
	 * to launch the execution
	 */
	protected Map<String, String> pluginCapabilities;
	
	@XmlJavaTypeAdapter(MapAdapter.class)
	/**
	 * Inputs to provide to the plugin instance which have to be executed.
	 */
	protected Map<String, Object> inputs;

	@XmlElement
	/**
	 * Scheduling parameters. See {#Scheduling} for further details
	 */
	protected Scheduling scheduling;
	
	@XmlElement
	/**
	 * Used only for scheduled tasks. Indicate if the task has to be persisted
	 * so that if the SmartExectuor Service instance die, another one take in 
	 * charge that execution.
	 */
	@Deprecated
	protected boolean persist;

	protected LaunchParameter(){}
	
	public LaunchParameter(String pluginName, Map<String, Object> inputs) {
		this(pluginName, null, null, inputs, null);
	}
	
	public LaunchParameter(String pluginName, Map<String, String> pluginCapabilities, Map<String, Object> inputs) {
		this(pluginName, null, pluginCapabilities, inputs, null);
	}
	
	public LaunchParameter(String pluginName, Map<String, Object> inputs, Scheduling scheduling) {
		this(pluginName, null, null, inputs, scheduling);
	}
	
	public LaunchParameter(String pluginName, Map<String, String> pluginCapabilities, Map<String, Object> inputs, Scheduling scheduling) {
		this(pluginName, null, pluginCapabilities, inputs, scheduling); 
	}
	
	public LaunchParameter(String pluginName, String pluginVersion, Map<String, String> pluginCapabilities, Map<String, Object> inputs, Scheduling scheduling) {
		this.pluginName = pluginName;
		this.pluginVersion = pluginVersion;
		this.pluginCapabilities = pluginCapabilities;
		this.inputs = inputs;
		this.scheduling = scheduling;
	}
	
	@Deprecated
	public LaunchParameter(String pluginName, Map<String, Object> inputs, Scheduling scheduling, boolean persist) {
		this(pluginName, null, null, inputs, scheduling); 
		setPersist(persist);
	}
	
	@Deprecated
	public LaunchParameter(String pluginName, Map<String, String> pluginCapabilities, Map<String, Object> inputs, Scheduling scheduling, boolean persist) {
		this(pluginName, null, pluginCapabilities, inputs, scheduling);
		setPersist(persist);
	}
	
	/**
	 * @return the persist
	 */
	@Deprecated
	public boolean isPersist() {
		return persist;
	}

	/**
	 * This method is deprecated, use {@link Scheduling#setGlobal(Boolean)} 
	 * instead. This method has no side effect if scheduling is null.
	 * Moreover has no side effect when {@link Scheduling#global} has been
	 * already initialized.  
	 * @param persist the persist to set
	 */
	@Deprecated
	public void setPersist(boolean persist) {
		if(scheduling==null){
			return;
		}
		if(this.scheduling.global==null){
			this.scheduling.global = persist;
			this.persist = persist;
		}else{
			this.persist = this.scheduling.global;
		}
	}
	
	/**
	 * @return the name
	 */
	@Deprecated
	public String getName() {
		return getPluginName();
	}
	
	/**
	 * @return the name
	 */
	public String getPluginName() {
		return pluginName;
	}

	/**
	 * @return the pluginCapabilities
	 */
	public Map<String, String> getPluginCapabilities() {
		return pluginCapabilities;
	}

	/**
	 * @param pluginCapabilities the pluginCapabilities to set
	 */
	public void setPluginCapabilities(Map<String, String> pluginCapabilities) {
		this.pluginCapabilities = pluginCapabilities;
	}


	/**
	 * @return the inputs
	 */
	public Map<String, Object> getInputs() {
		return inputs;
	}
	
	
	/**
	 * @return the scheduling
	 */
	public Scheduling getScheduling() {
		return scheduling;
	}
	
	/**
	 * @param scheduling the scheduling
	 */
	public void setScheduling(Scheduling scheduling) {
		this.scheduling = scheduling;
	}
	
	@Override
	public String toString(){
		return String.format("%s : { Plugin : { Name: %s , Capabilites : %s}, Persist : %b, Scheduling : {%s}, Inputs : %s}", 
				this.getClass().getSimpleName(), pluginName, pluginCapabilities,
				persist, scheduling, inputs);
	}

	/** {@inheritDoc}	 */
	@Override
	public int compareTo(LaunchParameter launchParameter) {
		int compareResult = 0;
		
		compareResult = new ObjectCompare<Boolean>().compare(new Boolean(persist),new Boolean(launchParameter.persist));
		if(compareResult!=0){
			return compareResult;
		}
		
		compareResult = new ObjectCompare<String>().compare(pluginName,launchParameter.pluginName);
		if(compareResult!=0){
			return compareResult;
		}
		
		compareResult = new ObjectCompare<Scheduling>().compare(scheduling,launchParameter.scheduling);
		if(compareResult!=0){
			return compareResult;
		}
		
		return new MapCompare<Map<String, Object>, String, Object>().compareMaps(inputs, launchParameter.inputs);
	}


}

/**
 * 
 */
package org.gcube.vremanagement.executor.api.types;

import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.gcube.vremanagement.executor.api.types.adapter.MapAdapter;
import org.gcube.vremanagement.executor.json.SEMapper;
import org.gcube.vremanagement.executor.plugin.PluginStateNotification;
import org.gcube.vremanagement.executor.utils.MapCompare;
import org.gcube.vremanagement.executor.utils.ObjectCompare;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
@XmlRootElement()
@XmlAccessorType(XmlAccessType.FIELD)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property=SEMapper.CLASS_PROPERTY)
public class LaunchParameter implements Comparable<LaunchParameter> {
	
	public static final String PLUGIN_NAME = "pluginName";
	public static final String PLUGIN_VERSION = "pluginVersion";
	
	/**
	 * The name of the plugin to launch
	 */
	@XmlElement
	@JsonProperty(value=PLUGIN_NAME)
	protected String pluginName;
	

	/**
	 * The version of the plugin to launch. Version can be null, this means 
	 * that no specific version is required. In other words, null means any 
	 * version.
	 */
	@XmlElement
	@JsonProperty(value=PLUGIN_VERSION)
	protected String pluginVersion;
	
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
	@XmlJavaTypeAdapter(MapAdapter.class)
	protected Map<String, String> pluginCapabilities;
	
	/**
	 * Inputs to provide to the plugin instance which have to be executed.
	 */
	@XmlJavaTypeAdapter(MapAdapter.class)
	protected Map<String, Object> inputs;

	/**
	 * Plugin State Notification to be used and inputs to be provided
	 * when instantiated  
	 */
	@XmlJavaTypeAdapter(MapAdapter.class)
	protected Map<String, Map<String, String>> pluginStateNotifications;
	
	/**
	 * Scheduling parameters. See {#Scheduling} for further details
	 */
	@XmlElement
	protected Scheduling scheduling;
	
	protected LaunchParameter(){}
	
	public LaunchParameter(LaunchParameter launchParameter){
		this.pluginName = launchParameter.pluginName;
		this.pluginVersion = launchParameter.pluginVersion;
		this.pluginCapabilities = launchParameter.pluginCapabilities;
		this.inputs = launchParameter.inputs;
		this.scheduling = launchParameter.scheduling;
		this.pluginStateNotifications = launchParameter.pluginStateNotifications;
	}
	
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
		this.pluginStateNotifications = new HashMap<>();
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
	
	public void addPluginStateNotifications(Class<? extends PluginStateNotification> pluginStateNotification, Map<String, String> inputs){
		this.pluginStateNotifications.put(pluginStateNotification.getName(), inputs);
	}
	
	public Map<String, Map<String, String>> getPluginStateNotifications(){
		return this.pluginStateNotifications;
	}

	public void setPluginStateNotifications(Map<String, Map<String, String>> pluginStateNotifications) {
		this.pluginStateNotifications = pluginStateNotifications;
	}
	
	@Override
	public String toString(){
		return String.format("{"
				+ "pluginName:%s,"
				+ "pluginVersion:%s,"
				+ "pluginCapabilities:%s,"
				+ "scheduling:%s,"
				+ "inputs:%s,"
				+ "pluginStateNotifications:%s"
				+ "}", 
 				pluginName,
				pluginVersion, 
				pluginCapabilities, 
				scheduling, 
				inputs, 
				pluginStateNotifications);
	}
	
	/** {@inheritDoc} */
	@Override
	public int compareTo(LaunchParameter launchParameter) {
		int compareResult = 0;
		
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

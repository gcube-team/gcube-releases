/**
 * 
 */
package org.gcube.vremanagement.executor.configuration.jsonbased;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

import org.gcube.vremanagement.executor.SmartExecutorInitializator;
import org.gcube.vremanagement.executor.api.types.LaunchParameter;
import org.gcube.vremanagement.executor.api.types.Scheduling;
import org.gcube.vremanagement.executor.exception.ScopeNotMatchException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 *
 */
public class JSONLaunchParameter extends LaunchParameter {
	
	private static Logger logger = LoggerFactory.getLogger(JSONLaunchParameter.class);
	
	public static final String PLUGIN_NAME = "pluginName";
	public static final String PLUGIN_CAPABILITIES = "pluginCapabilites";
	public static final String INPUTS = "inputs";
	public static final String SCHEDULING = "scheduling";
	public static final String USED_BY = "usedBy";
	public static final String SCOPE = "SCOPE";
	
	/**
	 * Contains the GCOREEndpoint (aka Running Instance) ID 
	 */
	protected String usedBy; 
	
	protected String scope;
	
	@SuppressWarnings("unused")
	private JSONLaunchParameter(){}
	
	public JSONLaunchParameter(String pluginName, Map<String, Object> inputs) {
		super(pluginName, inputs);
	}
	
	public JSONLaunchParameter(String pluginName, Map<String, String> pluginCapabilities, Map<String, Object> inputs) {
		super(pluginName, pluginCapabilities, inputs);
		this.scope = SmartExecutorInitializator.getScopeFromToken();
	}

	public JSONLaunchParameter(String pluginName, Map<String, Object> inputs, Scheduling scheduling) throws ParseException {
		super(pluginName, inputs, scheduling);
		this.scope = SmartExecutorInitializator.getScopeFromToken();
	}
	
	public JSONLaunchParameter(String pluginName, Map<String, String> pluginCapabilities, Map<String, Object> inputs, Scheduling scheduling) throws ParseException {
		super(pluginName, pluginCapabilities, inputs, scheduling);
		this.scope = SmartExecutorInitializator.getScopeFromToken();
	}
	
	public JSONLaunchParameter(LaunchParameter parameter) throws ParseException {
		super(parameter.getPluginName(), parameter.getPluginCapabilities(), parameter.getInputs(), parameter.getScheduling());
		this.scheduling = new JSONScheduling(parameter.getScheduling());
		this.scope = SmartExecutorInitializator.getScopeFromToken();
	}
	
	public JSONLaunchParameter(JSONObject jsonObject) throws JSONException, ParseException, ScopeNotMatchException {
		super();
		
		this.pluginName = jsonObject.getString(PLUGIN_NAME);
		
		this.pluginCapabilities = null;
		if(jsonObject.has(PLUGIN_CAPABILITIES)){
			this.pluginCapabilities = new HashMap<String, String>();
			JSONObject capabilities = jsonObject.getJSONObject(PLUGIN_CAPABILITIES);
			JSONArray names = capabilities.names();
			for(int j=0; j<names.length(); j++){
				String key = names.getString(j);
				this.pluginCapabilities.put(key, capabilities.getString(key));
			}
		}
		
		this.inputs = new HashMap<String, Object>();
		JSONObject inputsJsonObject = jsonObject.getJSONObject(INPUTS);
		JSONArray names = inputsJsonObject.names();
		for(int j=0; j<names.length(); j++){
			String key = names.getString(j);
			this.inputs.put(key, inputsJsonObject.get(key));
		}
		
		if(jsonObject.has(SCHEDULING)){
			JSONObject schedulingJsonObject = jsonObject.getJSONObject(SCHEDULING);
			this.scheduling = new JSONScheduling(schedulingJsonObject);
		}
		
		if(jsonObject.has(USED_BY)){
			this.usedBy = jsonObject.getString(USED_BY);
		}
		
		this.scope = SmartExecutorInitializator.getScopeFromToken();
		if(jsonObject.has(SCOPE)){
			String jsonScope = jsonObject.getString(SCOPE);
			if(jsonScope.compareTo(scope)!=0){
				String message = String.format("The current scope %s differs from the one provide in %s provided as argument %s.",
						scope, JSONObject.class.getSimpleName(), jsonScope);
				logger.error(message);
				throw new ScopeNotMatchException(message);
			}
		}
		
	}

	/**
	 * @return the scheduling
	 */
	public JSONScheduling getScheduling() {
		return (JSONScheduling) scheduling;
	}

	/**
	 * @param scheduling the scheduling to set
	 */
	public void setScheduling(JSONScheduling scheduling) {
		this.scheduling = scheduling;
	}
	
	public JSONObject toJSON() throws JSONException {
		JSONObject obj = new JSONObject();
		obj.put(PLUGIN_NAME, pluginName);
		
		if(pluginCapabilities!=null && !pluginCapabilities.isEmpty()){
			JSONObject capabilities = new JSONObject();
			for(String id : pluginCapabilities.keySet()){
				capabilities.put(id, pluginCapabilities.get(id));
			}
			obj.put(PLUGIN_CAPABILITIES, capabilities);
		}
		
		JSONObject inputJsonObject = new JSONObject();
		for(String id : inputs.keySet()){
			inputJsonObject.put(id, inputs.get(id));
		}
		obj.put(INPUTS, inputJsonObject);
		
		if(scheduling!=null){
			obj.put(SCHEDULING, getScheduling().toJSON());
		}
		
		if(usedBy!=null){
			obj.put(USED_BY, usedBy);
		}
		
		if(scope!=null){
			obj.put(SCOPE, scope);
		}
		return obj;
	}
	
	public String toString(){
		try {
			return toJSON().toString();
		} catch (JSONException e) {
			return String.format("%s : %s", this.getClass().getSimpleName(), 
					super.toString());
		}
	}

	/**
	 * @return the usedBy
	 */
	public String getUsedBy() {
		return usedBy;
	}

	/**
	 * @param usedBy the usedBy to set
	 */
	public void setUsedBy(String usedBy) {
		this.usedBy = usedBy;
	}

	/**
	 * @return the scope
	 */
	public String getScope() {
		return scope;
	}

	/**
	 * @param scope the scope to set
	 */
	public void setScope(String scope) {
		this.scope = scope;
	}
	
}

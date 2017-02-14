/**
 * 
 */
package org.gcube.vremanagement.executor.persistence.couchdb;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;
import org.gcube.common.resources.gcore.GCoreEndpoint;
import org.gcube.smartgears.ContextProvider;
import org.gcube.vremanagement.executor.SmartExecutorInitializator;
import org.gcube.vremanagement.executor.exception.InvalidPluginStateEvolutionException;
import org.gcube.vremanagement.executor.plugin.Plugin;
import org.gcube.vremanagement.executor.plugin.PluginDeclaration;
import org.gcube.vremanagement.executor.plugin.PluginState;
import org.gcube.vremanagement.executor.plugin.PluginStateEvolution;

/**
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 *
 */
public class PluginStateEvolutionObjectNode {

	protected static final String EVOLUTION_TYPE = "evolution";
	
	public final static String UUID_FIELD = "uuid";
	public final static String ITERATION_FIELD = "iteration";
	
	public final static String PLUGIN_DECLARATION_FIELD = "pluginDeclaration";
	public final static String PLUGIN_DECLARATION_NAME_FIELD = "name";
	public final static String PLUGIN_DECLARATION_DESCRIPTION_FIELD = "description";
	public final static String PLUGIN_DECLARATION_VERSION_FIELD = "version";
	public final static String PLUGIN_DECLARATION_HOST_DISCOVERED_CAPABILITIES_FIELD = "hostDiscoveredCapabilities";
		
	public final static String TIMESTAMP_FIELD = "timestamp";
	public final static String STATE_FIELD = "state";
	
	protected static final String RUN_ON_FIELD = "runOn";
	public final static String GHN_HOSTNAME_FIELD = "ghnHostname";
	public final static String GHN_ID_FIELD = "ghnID";
	
	public final static String SCOPE_FIELD = "scope";
	
	public final static String LOCALHOST = "localhost";
	
	public final static String PERCENTAGE = "percentage";
	
	protected static ObjectNode getRunOn(){
		ObjectMapper objectMapper = new ObjectMapper();
		ObjectNode objectNode = objectMapper.createObjectNode();
		try {
			GCoreEndpoint gCoreEndpoint = ContextProvider.get().profile(GCoreEndpoint.class);
			objectNode.put(GHN_ID_FIELD, gCoreEndpoint.profile().ghnId());
			objectNode.put(GHN_HOSTNAME_FIELD, ContextProvider.get().container().configuration().hostname());
		}catch(Exception e){
			objectNode.put(GHN_ID_FIELD, LOCALHOST + "_" + UUID.randomUUID());
			objectNode.put(GHN_HOSTNAME_FIELD, LOCALHOST);
		}
		return objectNode;
	}
	
	protected static ObjectNode getPluginInfo(PluginDeclaration pluginDeclaration){
		ObjectMapper objectMapper = new ObjectMapper();
		ObjectNode objectNode = objectMapper.createObjectNode();
		objectNode.put(PLUGIN_DECLARATION_NAME_FIELD, pluginDeclaration.getName());
		objectNode.put(PLUGIN_DECLARATION_DESCRIPTION_FIELD, pluginDeclaration.getDescription());
		objectNode.put(PLUGIN_DECLARATION_VERSION_FIELD, pluginDeclaration.getVersion());
		
		Map<String, String> capabilites = pluginDeclaration.getSupportedCapabilities();
		ObjectNode capabilitiesObjectNode = objectMapper.createObjectNode();
		if(capabilites!=null){
			for(String key : capabilites.keySet()){
				capabilitiesObjectNode.put(key, capabilites.get(key));
			}
		}
		objectNode.put(PLUGIN_DECLARATION_HOST_DISCOVERED_CAPABILITIES_FIELD, capabilitiesObjectNode);
		
		return objectNode;
	}
	
	protected static PluginDeclaration getPluginDeclaration(final JsonNode jsonNode){
		PluginDeclaration pluginDeclaration = new PluginDeclaration() {
			
			@Override
			public void init() throws Exception {}
			
			@Override
			public String getVersion() {
				return jsonNode.get(PLUGIN_DECLARATION_VERSION_FIELD).asText();
			}
			
			@Override
			public Map<String, String> getSupportedCapabilities() {
				Map<String, String> capabilities = new HashMap<>();
				JsonNode node = jsonNode.get(PLUGIN_DECLARATION_VERSION_FIELD);
				Iterator<String> iterator = node.getFieldNames();
				while(iterator.hasNext()) {
					String key = iterator.next();
					capabilities.put(key, node.get(key).asText());
				}
				return capabilities;
			}
			
			@Override
			public Class<? extends Plugin<? extends PluginDeclaration>> getPluginImplementation() {
				return null;
			}
			
			@Override
			public String getName() {
				return jsonNode.get(PLUGIN_DECLARATION_NAME_FIELD).asText();
			}
			
			@Override
			public String getDescription() {
				return jsonNode.get(PLUGIN_DECLARATION_DESCRIPTION_FIELD).asText();
			}
		};
		
		return pluginDeclaration;
	}
	
	
	public static void addScope(ObjectNode objectNode){
		objectNode.put(SCOPE_FIELD, SmartExecutorInitializator.getScopeFromToken());
	}
	
	public static ObjectNode getObjectMapper(PluginStateEvolution pluginStateEvolution){
		ObjectMapper objectMapper = new ObjectMapper();
		ObjectNode objectNode = objectMapper.createObjectNode();
		objectNode.put(UUID_FIELD, pluginStateEvolution.getUuid().toString());
		objectNode.put(ITERATION_FIELD, pluginStateEvolution.getIteration());
		objectNode.put(TIMESTAMP_FIELD, pluginStateEvolution.getTimestamp());
		
		objectNode.put(PLUGIN_DECLARATION_FIELD, getPluginInfo(pluginStateEvolution.getPluginDeclaration()));
		
		objectNode.put(STATE_FIELD, pluginStateEvolution.getPluginState().toString());
		
		objectNode.put(PERCENTAGE, pluginStateEvolution.getPercentage());
		
		addScope(objectNode);
		
		objectNode.put(CouchDBPersistenceConnector.TYPE_JSON_FIELD, EVOLUTION_TYPE);
		try {
			objectNode.put(RUN_ON_FIELD, getRunOn());
		}catch(Exception e){
			// TODO
		}
		
		return objectNode;
	}
	
	
	public static PluginStateEvolution getPluginStateEvolution(JsonNode jsonNode) 
			throws InvalidPluginStateEvolutionException{
		UUID uuid = UUID.fromString(jsonNode.get(UUID_FIELD).asText());
		int iteration = jsonNode.get(ITERATION_FIELD).asInt();
		long timestamp = jsonNode.get(TIMESTAMP_FIELD).asInt();
		PluginDeclaration pluginDeclaration = getPluginDeclaration(jsonNode.get(PLUGIN_DECLARATION_FIELD));
		PluginState pluginState = PluginState.valueOf(jsonNode.get(STATE_FIELD).asText());
		int percentage = jsonNode.get(PERCENTAGE).asInt();
		return new PluginStateEvolution(uuid, iteration, timestamp, pluginDeclaration, pluginState, percentage);
	}
}

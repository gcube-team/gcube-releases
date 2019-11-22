//package gr.cite.geoanalytics.web;
//
//import java.util.HashMap;
//import java.util.Map;
//import java.util.Set;
//
//import javax.annotation.PostConstruct;
//import javax.inject.Inject;
//import javax.ws.rs.core.MediaType;
//
//import com.sun.jersey.api.client.Client;
//import com.sun.jersey.api.client.WebResource;
//
//import gr.cite.gaap.servicelayer.ConfigurationManager;
//import gr.cite.gaap.servicelayer.GeocodeManager;
//import gr.cite.geoanalytics.context.Configuration;
//import gr.cite.geoanalytics.logicallayer.LogicalLayerBroker;
//import gr.cite.geoanalytics.logicallayer.NodePicker;
//
//public class ClusterGeoserverPicker implements GeoserverPicker {
//
//	private NodePicker nodePicker;
//	private LogicalLayerBroker logicalLayerBroker;
//	private ConfigurationManager configurationManager = null;
//	private GeocodeManager taxonomyManager = null;
//	private Client client;
//	
//	@Inject
//	public void setNodePicker(NodePicker nodePicker) {
//		this.nodePicker = nodePicker;
//	}
//	
//	@Inject
//	public void setLogicalLayerBroker(LogicalLayerBroker logicalLayerBroker) {
//		this.logicalLayerBroker = logicalLayerBroker;
//	}
//	
//	@Inject
//	public void setConfigurationManager(ConfigurationManager configurationManager) {
//		this.configurationManager = configurationManager;
//	}
//	
//	@Inject
//	public void setClient(Client client) {
//		this.client = client;
//	}
//	
//	@Inject
//	public void setTaxonomyManager(GeocodeManager taxonomyManager) {
//		this.taxonomyManager = taxonomyManager;
//	}
//	
//	private String getGeoserverEndpoint(String node) {
//		return logicalLayerBroker.getServerToGeoserverData().get(node);
//	}
//	
//	@Override
//	public String pickGeoserverByLayerName(String layerName) throws Exception {
//		//TODO add retrying logic (this implementation should somehow retain state on which nodes have already been picked,
//		//or obtain this state as an argument)
//		return getGeoserverEndpoint(nodePicker.pickNodeForLayer(layerName));
//	
//	}
//
//	@Override
//	public String pickGeoserverByLayerId(String layerId) throws Exception {
//		return getGeoserverEndpoint(nodePicker.pickNodeForLayerId(layerId));
//	}
//
//	@Override
//	public Map<String, Set<String>> pickGeoserversByLayerNames(Set<String> layerNames) throws Exception {
//		
//		Map<String, Set<String>> result = new HashMap<>();
//		
//		//TODO uncomment the following code and remove line containing the call to pickNodeHostingAllLayers when nodePicker.pickNodesForLayers is implemented
////		Map<String, Set<String>> nodes = nodePicker.pickNodesForLayers(layerNames);
////		for(Map.Entry<String, Set<String>> entry : nodes.entrySet())
////			result.put(getGeoserverEndpoint(entry.getKey()), entry.getValue());
////		return result;
//		
//		result.put(getGeoserverEndpoint(nodePicker.pickNodeHostingAllLayers(layerNames)), layerNames);
//		return result;
//	}
//
//	@Override
//	public Map<String, Set<String>> pickGeoserversByLayerTermIds(Set<String> layerTermIds) throws Exception {
//		Map<String, Set<String>> result = new HashMap<>();
//		
//		//TODO uncomment the following code and remove line containing the call to pickNodeHostingAllLayerTermIds  when nodePicker.pickNodesForLayerTermIds is implemented
////		Map<String, Set<String>> nodes = nodePicker.pickNodesForLayerTermIds(layerTermIds);
////		for(Map.Entry<String, Set<String>> entry : nodes.entrySet())
////			result.put(getGeoserverEndpoint(entry.getKey()), entry.getValue());
////		return result;
//		
//		result.put(getGeoserverEndpoint(nodePicker.pickNodeHostingAllLayerTermIds(layerTermIds)), layerTermIds);
//		return result;
//	}
//	
//
//}

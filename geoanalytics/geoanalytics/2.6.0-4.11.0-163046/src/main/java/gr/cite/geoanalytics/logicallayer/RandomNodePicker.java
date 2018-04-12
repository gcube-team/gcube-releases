package gr.cite.geoanalytics.logicallayer;

import gr.cite.clustermanager.model.layers.ZNodeData;
import gr.cite.gaap.servicelayer.ConfigurationManager;
import gr.cite.gaap.servicelayer.GeocodeManager;
import gr.cite.geoanalytics.dataaccess.entities.geocode.Geocode;
import gr.cite.geoanalytics.dataaccess.entities.layer.Layer;
import gr.cite.geoanalytics.dataaccess.entities.sysconfig.xml.layer.LayerConfig;
import gr.cite.geoanalytics.manager.LayerManager;

import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import javax.inject.Inject;

public class RandomNodePicker implements NodePicker {
	
	protected GeocodeManager taxonomyManager;
	protected LayerManager layerManager;
	protected ConfigurationManager configurationManager;
	private NodeDataSource nodeDataSource;
	
	@Inject
	public void setLayerManager(LayerManager layerManager) {
		this.layerManager = layerManager;
	}
	
	@Inject
	public void setNodeDataSource(NodeDataSource nodeDataSource) {
		this.nodeDataSource = nodeDataSource;
	}
	
	@Inject
	public void setTaxonomyManager(GeocodeManager taxonomyManager) {
		this.taxonomyManager = taxonomyManager;
	}
	
	@Inject
	public void setConfigurationManager(ConfigurationManager configurationManager) {
		this.configurationManager = configurationManager;
	}
	
	@Override
	public URL getNodeURL(String node) throws Exception {
		return nodeDataSource.getNodeURL(node);
	}
	
	private String getLayerNameByLayerId(String layerID) throws Exception {
		
		Layer layer = layerManager.findLayerById(UUID.fromString(layerID));
		
		if(layer == null)
			throw new Exception("Layer term " + layerID + " not found");
		LayerConfig layerCfg = configurationManager.getLayerConfig(UUID.fromString(layerID));
		if(layerCfg == null)
			throw new Exception("Could not find layer configuration for term " + layerID);
		return layerCfg.getName();
	}
	
	@Override
	public Set<String> getNodesForLayerId(String layerTermId) throws Exception {
		return getNodesForLayer(getLayerNameByLayerId(layerTermId));
	}
	
	
	@Override
	public String pickNodeForLayerId(String layerTermId) throws Exception {
		return pickNode(getNodesForLayerId(layerTermId));
	}
	
	@Override
	public Set<String> getNodesForLayer(String layerName) throws Exception {
		Set<String> nodes = nodeDataSource.getLayerToServerData().get(layerName);
		if(nodes == null)
			throw new Exception("Could not find nodes containing layer " + layerName);
		return nodes;
	}
	
	@Override
	public String pickNodeForLayer(String layerName) throws Exception {
		return pickNode(getNodesForLayer(layerName));
	}
	
	@Override
	public String pickNode(Set<String> nodes) {
		// TODO load balancing etc
		return nodes.toArray(new String[nodes.size()])[new Random().nextInt(nodes.size())];
	}
	
	@Override
	public String pickNodeHostingAllLayerTermIds(Set<String> layerTermIds) throws Exception {
		return pickNodeHostingAllLayers(getLayerNamesByTermIds(layerTermIds));
	}

	@Override
	public String pickNodeHostingAllLayers(Set<String> layerNames) throws Exception {
		Set<String> nodes = new HashSet<>();
		for(Map.Entry<String, Set<ZNodeData>> skd : nodeDataSource.getServerToLayerData().entrySet()) {
			if(skd.getValue().containsAll(layerNames))
				nodes.add(skd.getKey());
		}
		return pickNode(nodes);
		
	}

	@Override
	public Map<String, Set<String>> pickNodesForLayers(Set<String> layerNames) throws Exception {
		Map<String, Set<String>> nodesToLayers = new HashMap<>();
		//TODO obtain info from serverKeyData or layerKeyData and find out which nodes contain
		//as much layers as possible. e.g. if some nodes contain all layers, get those nodes, call
		//pickNode and return a map with only one entry (node -> all layers). If there is not a node
		//containing all layers, return as few nodes as possible (prefer nodes having as many layers as
		//possible and then call pickNode a sufficient number of times).s
		throw new UnsupportedOperationException();
	}

	@Override
	public Map<String, Set<String>> pickNodesForLayerTermIds(Set<String> layerTermIds) throws Exception {
		return pickNodesForLayers(getLayerNamesByTermIds(layerTermIds));
	}
	
	private Set<String> getLayerNamesByTermIds(Set<String> layerTermIds) throws Exception {
		Set<String> layerNames = new HashSet<>();
		for(String layerTermId : layerTermIds)
			layerNames.add(getLayerNameByLayerId(layerTermId));
		return layerNames;
	}
}

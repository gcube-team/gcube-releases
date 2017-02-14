package gr.cite.geoanalytics.logicallayer;

import gr.cite.gaap.servicelayer.ConfigurationManager;
import gr.cite.gaap.servicelayer.TaxonomyManager;
import gr.cite.geoanalytics.dataaccess.entities.sysconfig.xml.layer.LayerConfig;
import gr.cite.geoanalytics.dataaccess.entities.taxonomy.TaxonomyTerm;

import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import javax.inject.Inject;

public class RandomNodePicker implements NodePicker {
	
	protected TaxonomyManager taxonomyManager;
	protected ConfigurationManager configurationManager;
	private NodeDataSource nodeDataSource;
	
	@Inject
	public void setNodeDataSource(NodeDataSource nodeDataSource) {
		this.nodeDataSource = nodeDataSource;
	}
	
	@Inject
	public void setTaxonomyManager(TaxonomyManager taxonomyManager) {
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
	
	private String getLayerNameByTermId(String layerTermId) throws Exception {
		TaxonomyTerm tt = taxonomyManager.findTermById(layerTermId, false);
		if(tt == null)
			throw new Exception("Layer term " + layerTermId + " not found");
		LayerConfig layerCfg = configurationManager.getLayerConfig(tt);
		if(layerCfg == null)
			throw new Exception("Could not find layer configuration for term " + layerTermId);
		return layerCfg.getName();
	}
	
	@Override
	public Set<String> getNodesForLayerTermId(String layerTermId) throws Exception {
		return getNodesForLayer(getLayerNameByTermId(layerTermId));
	}
	
	
	@Override
	public String pickNodeForLayerTermId(String layerTermId) throws Exception {
		return pickNode(getNodesForLayerTermId(layerTermId));
	}
	
	@Override
	public Set<String> getNodesForLayer(String layerName) throws Exception {
		Set<String> nodes = nodeDataSource.getLayerKeyData().get(layerName);
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
		for(Map.Entry<String, Set<String>> skd : nodeDataSource.getServerKeyData().entrySet()) {
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
			layerNames.add(getLayerNameByTermId(layerTermId));
		return layerNames;
	}
}

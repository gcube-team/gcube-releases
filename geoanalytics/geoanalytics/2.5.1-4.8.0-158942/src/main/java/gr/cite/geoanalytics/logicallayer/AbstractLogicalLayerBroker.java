package gr.cite.geoanalytics.logicallayer;

import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import org.springframework.beans.factory.annotation.Qualifier;

import gr.cite.gaap.servicelayer.ConfigurationManager;
import gr.cite.gaap.servicelayer.GeocodeManager;

public abstract class AbstractLogicalLayerBroker implements LogicalLayerBroker {

	protected GeocodeManager taxonomyManager;
	protected ConfigurationManager configurationManager;
	private NodePicker nodePicker;
	
	@Inject
	@Qualifier("randomNodePicker")
	public void setNodePicker(NodePicker nodePicker) {
		this.nodePicker = nodePicker;
	}
	
	@Inject
	public void setTaxonomyManager(GeocodeManager taxonomyManager) {
		this.taxonomyManager = taxonomyManager;
	}
	
	@Inject
	public void setConfigurationManager(ConfigurationManager configurationManager) {
		this.configurationManager = configurationManager;
	}
	
	
	public Set<String> getNodesForLayerId(String layerTermId) throws Exception {
		return nodePicker.getNodesForLayerId(layerTermId);
	}
	
	
	@Override
	public String pickNodeForLayerId(String layerTermId) throws Exception {
		return nodePicker.pickNodeForLayerId(layerTermId);
	}
	
	public Set<String> getNodesForLayer(String layerName) throws Exception {
		return nodePicker.getNodesForLayer(layerName);
	}
	
	@Override
	public String pickNodeForLayer(String layerName) throws Exception {
		return nodePicker.pickNodeForLayer(layerName);
	}
	
	@Override
	public String pickNode(Set<String> nodes) throws Exception {
		return nodePicker.pickNode(nodes);
	}
	
	@Override
	public String pickNodeHostingAllLayerTermIds(Set<String> layerTermIds) throws Exception {
		return nodePicker.pickNodeHostingAllLayerTermIds(layerTermIds);
	}

	@Override
	public String pickNodeHostingAllLayers(Set<String> layerNames) throws Exception {
		return nodePicker.pickNodeHostingAllLayers(layerNames);
	}
	
	@Override
	public Map<String, Set<String>> pickNodesForLayers(Set<String> layerNames) throws Exception {
		return nodePicker.pickNodesForLayers(layerNames);
	}

	@Override
	public Map<String, Set<String>> pickNodesForLayerTermIds(Set<String> layerTermIds) throws Exception {
		return nodePicker.pickNodesForLayerTermIds(layerTermIds);
	}
}

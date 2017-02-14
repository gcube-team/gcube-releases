package gr.cite.geoanalytics.logicallayer;

import java.net.URL;
import java.util.Map;
import java.util.Set;

public interface NodePicker {
	
	public URL getNodeURL(String node) throws Exception;
	
	public Set<String> getNodesForLayerTermId(String layerTermId) throws Exception;
	public Set<String> getNodesForLayer(String layerName) throws Exception;
	
	public String pickNode(Set<String> nodes) throws Exception;
	public String pickNodeForLayerTermId(String layerTermId) throws Exception;
	public String pickNodeForLayer(String layerName) throws Exception;
	public String pickNodeHostingAllLayerTermIds(Set<String> layerTermIds) throws Exception;
	public String pickNodeHostingAllLayers(Set<String> layerNames) throws Exception;
	public Map<String, Set<String>> pickNodesForLayers(Set<String> layerNames) throws Exception;
	public Map<String, Set<String>> pickNodesForLayerTermIds(Set<String> layerTermIds) throws Exception;
}

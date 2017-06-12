package gr.cite.geoanalytics.logicallayer;

import java.net.URL;
import java.util.Map;
import java.util.Set;

import gr.cite.clustermanager.model.ZNodeData;

public interface NodeDataSource {

	public URL getNodeURL(String node) throws Exception;
	
	public Map<String, Set<ZNodeData>> getServerToLayerData();
	public Map<String, Set<String>> getLayerToServerData();
	public Map<String, String> getServerToGeoserverData();
	
}

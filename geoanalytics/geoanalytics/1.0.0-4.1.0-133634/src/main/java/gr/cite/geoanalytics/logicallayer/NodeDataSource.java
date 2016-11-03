package gr.cite.geoanalytics.logicallayer;

import java.net.URL;
import java.util.Map;
import java.util.Set;

public interface NodeDataSource {

	public URL getNodeURL(String node) throws Exception;
	
	public Map<String, Set<String>> getServerKeyData();
	public Map<String, Set<String>> getLayerKeyData();
	public Map<String, String> getServerToGeoserverData();
	
}

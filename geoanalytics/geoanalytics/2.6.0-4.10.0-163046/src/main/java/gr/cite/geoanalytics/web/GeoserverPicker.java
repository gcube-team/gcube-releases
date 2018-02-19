package gr.cite.geoanalytics.web;

import java.net.URL;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public interface GeoserverPicker {
	public String pickGeoserverByLayerName(String layerName) throws Exception;
	public String pickGeoserverByLayerId(String layerTermId) throws Exception;
	
	//geoserver node -> layer set
	public Map<String, Set<String>> pickGeoserversByLayerNames(Set<String> layerNames) throws Exception;
	public Map<String, Set<String>> pickGeoserversByLayerTermIds(Set<String> layerTermIds) throws Exception;
}

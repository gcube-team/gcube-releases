//package gr.cite.geoanalytics.web;
//
//import java.net.URL;
//import java.util.HashMap;
//import java.util.HashSet;
//import java.util.Map;
//import java.util.Set;
//import java.util.UUID;
//
//import javax.inject.Inject;
//
//import org.springframework.stereotype.Component;
//
//import gr.cite.gaap.servicelayer.ConfigurationManager;
//import gr.cite.gaap.servicelayer.GeocodeManager;
//import gr.cite.geoanalytics.context.Configuration;
//import gr.cite.geoanalytics.dataaccess.entities.geocode.Geocode;
//import gr.cite.geoanalytics.dataaccess.entities.layer.Layer;
//import gr.cite.geoanalytics.dataaccess.entities.sysconfig.xml.layer.LayerConfig;
//import gr.cite.geoanalytics.manager.LayerManager;
//
//@Component
//public class MonolithicGeoserverPicker implements GeoserverPicker {
//
//	private Configuration configuration = null;
//	private ConfigurationManager configurationManager = null;
//	private GeocodeManager taxonomyManager = null;
//	private LayerManager layerManager = null;
//	
//	@Inject
//	public void setLayerManager(LayerManager layerManager) {
//		this.layerManager = layerManager;
//	}
//	
//	@Inject
//	public void setConfiguration(Configuration configuration) {
//		this.configuration = configuration;
//	}
//	
//	@Inject
//	public void setTaxonomyManager(GeocodeManager taxonomyManager) {
//		this.taxonomyManager = taxonomyManager;
//	}
//	
//	@Inject
//	public void setConfigurationManager(ConfigurationManager configurationManager) {
//		this.configurationManager = configurationManager;
//	}
//	
//	@Override
//	public String pickGeoserverByLayerName(String layerName) {
//		return configuration.getGeoServerBridgeConfig().getGeoServerBridgeUrl();
//	}
//
//	private String getLayerNameById(UUID layerId) throws Exception {
//		
////		TaxonomyTerm tt = taxonomyManager.findTermById(layerId.toString(), false);
//		Layer layer = layerManager.findLayerById(layerId);
//		
//		if(layer == null)
//			throw new Exception("Layer term " + layerId + " not found");
//		LayerConfig layerCfg = configurationManager.getLayerConfig(layerId);
//		if(layerCfg == null)
//			throw new Exception("Could not find layer configuration for term " + layerId.toString());
//		return layerCfg.getName();
//	}
//	
//	@Override
//	public String pickGeoserverByLayerId(String layerId) throws Exception {
//		return pickGeoserverByLayerName(getLayerNameById(UUID.fromString(layerId)));
//	}
//
//	@Override
//	public Map<String, Set<String>> pickGeoserversByLayerNames(Set<String> layerNames) throws Exception {
//		Map<String, Set<String>> result = new HashMap<>();
//		result.put(configuration.getGeoServerBridgeConfig().getGeoServerBridgeUrl(), new HashSet<>(layerNames));
//		return result;
//	}
//
//	@Override
//	public Map<String, Set<String>> pickGeoserversByLayerTermIds(Set<String> layerTermIds) throws Exception {
//		Map<String, Set<String>> result = new HashMap<>();
//		result.put(configuration.getGeoServerBridgeConfig().getGeoServerBridgeUrl(), new HashSet<>(layerTermIds));
//		return result;
//	}
//
//}

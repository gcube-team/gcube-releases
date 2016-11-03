package gr.cite.geoanalytics.web;

import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import gr.cite.gaap.servicelayer.ConfigurationManager;
import gr.cite.gaap.servicelayer.TaxonomyManager;
import gr.cite.geoanalytics.context.Configuration;
import gr.cite.geoanalytics.dataaccess.entities.sysconfig.xml.layer.LayerConfig;
import gr.cite.geoanalytics.dataaccess.entities.taxonomy.TaxonomyTerm;

@Component
public class MonolithicGeoserverPicker implements GeoserverPicker {

	private Configuration configuration = null;
	private ConfigurationManager configurationManager = null;
	private TaxonomyManager taxonomyManager = null;
	
	@Inject
	public void setConfiguration(Configuration configuration) {
		this.configuration = configuration;
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
	public String pickGeoserverByLayerName(String layerName) {
		return configuration.getGeoServerBridgeConfig().getGeoServerBridgeUrl();
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
	public String pickGeoserverByLayerTermId(String layerTermId) throws Exception {
		return pickGeoserverByLayerName(getLayerNameByTermId(layerTermId));
	}

	@Override
	public Map<String, Set<String>> pickGeoserversByLayerNames(Set<String> layerNames) throws Exception {
		Map<String, Set<String>> result = new HashMap<>();
		result.put(configuration.getGeoServerBridgeConfig().getGeoServerBridgeUrl(), new HashSet<>(layerNames));
		return result;
	}

	@Override
	public Map<String, Set<String>> pickGeoserversByLayerTermIds(Set<String> layerTermIds) throws Exception {
		Map<String, Set<String>> result = new HashMap<>();
		result.put(configuration.getGeoServerBridgeConfig().getGeoServerBridgeUrl(), new HashSet<>(layerTermIds));
		return result;
	}

}

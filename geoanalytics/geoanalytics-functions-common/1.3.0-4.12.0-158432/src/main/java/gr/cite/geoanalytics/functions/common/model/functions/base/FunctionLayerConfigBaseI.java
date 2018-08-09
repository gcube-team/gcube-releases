package gr.cite.geoanalytics.functions.common.model.functions.base;

import java.util.Set;

import gr.cite.geoanalytics.functions.common.model.functions.LayerConfig;


public interface FunctionLayerConfigBaseI {

	public Set<LayerConfig> getLayerConfigs();

	public LayerConfig getLayerConfigByObjectID(String objID);
	
	public boolean isValidForSubmission();
	
}

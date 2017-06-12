package gr.cite.geoanalytics.functions.common.model.functions.base;

import java.util.List;

import gr.cite.geoanalytics.functions.common.model.LayerConfig;

public interface FunctionExecConfigBaseI {

	public List<LayerConfig> getLayerConfigs();

	public void setLayerConfigs(List<LayerConfig> layerConfigs);

	public LayerConfig getLayerConfigByRequiredName(String requiredName);
	
}

package gr.cite.geoanalytics.functions.common.model.functions.base;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import gr.cite.geoanalytics.functions.common.model.LayerConfig;

public class FunctionExecConfigBase implements FunctionExecConfigBaseI {

	private List<LayerConfig> layerConfigs = new ArrayList<LayerConfig>();
	
	public List<LayerConfig> getLayerConfigs() {
		return layerConfigs;
	}

	public void setLayerConfigs(List<LayerConfig> layerConfigs) {
		this.layerConfigs = layerConfigs;
	}

	public LayerConfig getLayerConfigByRequiredName(String requiredName){
		return layerConfigs.stream().filter(lc -> requiredName.equals(lc.getRequiredLayerName())).findFirst().get();
	}

	
}

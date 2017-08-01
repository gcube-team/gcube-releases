package gr.cite.geoanalytics.functions.common.model.functions;


import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import gr.cite.geoanalytics.functions.common.model.functions.base.FunctionLayerConfigBase;

public class SeaTemperatureEvaluator extends FunctionLayerConfigBase implements FunctionLayerConfigI {

//	private static final String [] USER_FRIENDLY_LAYER_NAMES = {"Coastal areas","Cities locations","Natura 2000 regions"};

	private static final Set<LayerConfig> layerConfigs = 
			Stream.of(
					new LayerConfig("0", "Sea Temperature")
					)
            	  .collect(Collectors.toCollection(HashSet::new));
	
	
	
	public SeaTemperatureEvaluator(){
		super(layerConfigs);
	}
	
	
}

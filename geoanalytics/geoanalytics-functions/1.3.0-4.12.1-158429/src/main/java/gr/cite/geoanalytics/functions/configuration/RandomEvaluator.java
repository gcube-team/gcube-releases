package gr.cite.geoanalytics.functions.configuration;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import gr.cite.geoanalytics.functions.common.model.functions.FunctionLayerConfigI;
import gr.cite.geoanalytics.functions.common.model.functions.LayerConfig;
import gr.cite.geoanalytics.functions.common.model.functions.base.FunctionLayerConfigBase;

public class RandomEvaluator extends FunctionLayerConfigBase implements FunctionLayerConfigI {

//	private static final String [] USER_FRIENDLY_LAYER_NAMES = {"Coastal areas","Cities locations","Natura 2000 regions"};

	private static final Set<LayerConfig> layerConfigs = 
			Stream.of(
					new LayerConfig("0", "Coastal areas"), 
					new LayerConfig("1", "Cities locations"),
					new LayerConfig("2", "Natura 2000 regions")
					)
            	  .collect(Collectors.toCollection(HashSet::new));
	
	
	
	public RandomEvaluator(){
		super(layerConfigs);
	}
	
	
}


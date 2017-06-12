package gr.cite.geoanalytics.functions.common.model.functions;

import gr.cite.geoanalytics.functions.common.model.functions.base.FunctionExecConfigBase;

public class RandomEvaluator extends FunctionExecConfigBase implements FunctionExecConfigI {

	private final String [] REQUIRED_LAYERS = {"Coastal areas","Cities locations","Natura 2000 regions"};

	@Override
	public String[] getRequiredLayers() {
		return REQUIRED_LAYERS;
	}
	
}

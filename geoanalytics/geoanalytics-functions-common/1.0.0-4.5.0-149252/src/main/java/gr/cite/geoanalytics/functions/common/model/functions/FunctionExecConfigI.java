package gr.cite.geoanalytics.functions.common.model.functions;

import gr.cite.geoanalytics.functions.common.model.functions.base.FunctionExecConfigBaseI;

public interface FunctionExecConfigI extends FunctionExecConfigBaseI {

	public String[] getRequiredLayers();
	
}

package org.gcube.dataanalysis.ecoengine.interfaces;

import org.gcube.dataanalysis.ecoengine.configuration.ALG_PROPS;
import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;
import org.gcube.dataanalysis.ecoengine.configuration.INFRASTRUCTURE;

public interface Modeler extends ComputationalAgent{

	public ALG_PROPS[] getSupportedModels();
	
	public Model getModel();
	
	public void setmodel(Model model);
	
	public void model(Model previousModel);
	
}

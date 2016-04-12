package org.gcube.dataanalysis.ecoengine.interfaces;

import org.gcube.dataanalysis.ecoengine.configuration.ALG_PROPS;
import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;
import org.gcube.dataanalysis.ecoengine.configuration.INFRASTRUCTURE;

public interface Generator extends ComputationalAgent{

	public ALG_PROPS[] getSupportedAlgorithms();
	
	public GenericAlgorithm getAlgorithm();

	public String getLoad();
	
}

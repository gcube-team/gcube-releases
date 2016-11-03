package org.gcube.dataanalysis.ecoengine.spatialdistributions;

import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;
import org.hibernate.SessionFactory;


public class AquamapsNative2050 extends AquamapsNative {
	
	public void init(AlgorithmConfiguration config,SessionFactory dbHibConnection) {
		super.init(config, dbHibConnection);
		type = "2050";
	}
	
	@Override
	public String getName() {
		return "AQUAMAPS_NATIVE_2050";
	}
	
	@Override
	public String getDescription() {
		return "Algorithm for Native 2050 Distribution by AquaMaps. A distribution algorithm that generates a table containing  species distribution probabilities on half-degree cells according to the AquaMaps approach with native distribution estimated for 2050.";
	}
}

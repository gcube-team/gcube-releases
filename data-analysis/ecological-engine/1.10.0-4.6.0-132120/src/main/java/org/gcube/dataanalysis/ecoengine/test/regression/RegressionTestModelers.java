package org.gcube.dataanalysis.ecoengine.test.regression;

import java.util.List;

import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;
import org.gcube.dataanalysis.ecoengine.interfaces.ComputationalAgent;
import org.gcube.dataanalysis.ecoengine.interfaces.Generator;
import org.gcube.dataanalysis.ecoengine.interfaces.Modeler;
import org.gcube.dataanalysis.ecoengine.processing.factories.GeneratorsFactory;
import org.gcube.dataanalysis.ecoengine.processing.factories.ModelersFactory;

public class RegressionTestModelers {
	/**
	 * example of parallel processing on a single machine the procedure will generate a new table for a distribution on suitable species
	 * 
	 */

public static void main(String[] args) throws Exception {
		
		System.out.println("TEST 1");

		List<ComputationalAgent> modelers = ModelersFactory.getModelers(testConfigLocal());
		modelers.get(0).init();
		Regressor.process(modelers.get(0));
		modelers = null;
		
}

	
	private static AlgorithmConfiguration testConfigLocal() {
		
		AlgorithmConfiguration config = Regressor.getConfig();
		config.setNumberOfResources(2);
		config.setModel("HSPEN");
		
		config.setParam("OuputEnvelopeTable","hspen_trained");
		config.setParam("OccurrenceCellsTable","occurrencecells");
		config.setParam("EnvelopeTable","hspen_mini");
		config.setParam("CsquarecodesTable", "hcaf_d");
		config.setParam("CreateTable","true");

		return config;
	}
}

package org.gcube.dataanalysis.ecoengine.test.regression;

import java.util.List;

import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;
import org.gcube.dataanalysis.ecoengine.interfaces.ComputationalAgent;
import org.gcube.dataanalysis.ecoengine.interfaces.Generator;
import org.gcube.dataanalysis.ecoengine.processing.factories.GeneratorsFactory;

public class RegressionTestGenerators {
	/**
	 * example of parallel processing on a single machine the procedure will generate a new table for a distribution on suitable species
	 * 
	 */

public static void main(String[] args) throws Exception {
		
		System.out.println("TEST 1");

		List<ComputationalAgent> generators = GeneratorsFactory.getGenerators(testConfigLocal());
		generators.get(0).init();
		Regressor.process(generators.get(0));
		generators = null;
		
}

	
	private static AlgorithmConfiguration testConfigLocal() {
		
		AlgorithmConfiguration config = Regressor.getConfig();
		config.setNumberOfResources(5);
		config.setModel("AQUAMAPS_SUITABLE");
		
		config.setParam("DistributionTable","hspec_suitable_test_gp");
		config.setParam("CsquarecodesTable","hcaf_d");
		config.setParam("EnvelopeTable","hspen_micro_1");
		config.setParam("OccurrencePointsTable", "occurrencecells");
		config.setParam("CreateTable","true");

		return config;
	}
}

package org.gcube.dataanalysis.executor.tests;

import java.util.List;

import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;
import org.gcube.dataanalysis.ecoengine.interfaces.ComputationalAgent;
import org.gcube.dataanalysis.ecoengine.processing.factories.GeneratorsFactory;

public class RegressionTestGenerators {
	/**
	 * example of parallel processing on a single machine the procedure will generate a new table for a distribution on suitable species
	 * 
	 */

	public static void main(String[] args) throws Exception {

		System.out.println("TEST 1");

		List<ComputationalAgent> generators = GeneratorsFactory.getGenerators(testAquamapsSuitable());
		generators.get(0).init();
		CustomRegressor.process(generators.get(0));
		generators = null;

	}

	private static AlgorithmConfiguration testAquamapsSuitable() {

		AlgorithmConfiguration config = CustomRegressor.getConfig();
		config.setNumberOfResources(5);
		config.setModel("AQUAMAPS_SUITABLE");

		config.setParam("UserName", "gianpaolo.coro");
		config.setGcubeScope("/gcube");
		config.setParam("ServiceUserName", "gianpaolo.coro");

		config.setParam("DistributionTable", "hspec_suitable_test_parallel");
		config.setParam("CsquarecodesTable", "hcaf_d");
//		config.setParam("EnvelopeTable", "hspen_filteredid_3a150e47_274e_47fc_b257_6a2933ec8e67");
		config.setParam("EnvelopeTable", "hspen1000");
		config.setParam("OccurrencePointsTable", "occurrencecells");
		config.setParam("CreateTable", "true");

		return config;
	}

	private static AlgorithmConfiguration testAquamapsNative() {

		AlgorithmConfiguration config = CustomRegressor.getConfig();
		config.setNumberOfResources(5);
		config.setModel("AQUAMAPS_NATIVE");

		config.setParam("UserName", "gianpaolo.coro");
		config.setGcubeScope("/gcube");
		config.setParam("ServiceUserName", "gianpaolo.coro");

		config.setParam("DistributionTable", "hspec_native_test_parallel2_1000");
		config.setParam("CsquarecodesTable", "hcaf_d");
//		config.setParam("EnvelopeTable", "hspen_micro_1");
//		config.setParam("EnvelopeTable", "hspen_mini_1000");
		config.setParam("EnvelopeTable", "hspen_filteredid_3a150e47_274e_47fc_b257_6a2933ec8e67");
		
		config.setParam("OccurrencePointsTable", "occurrencecells");
		config.setParam("CreateTable", "true");

		return config;
	}
	
	
	private static AlgorithmConfiguration testAquamapsNative2050() {

		AlgorithmConfiguration config = CustomRegressor.getConfig();
		config.setNumberOfResources(5);
		config.setModel("AQUAMAPS_NATIVE_2050");

		config.setParam("UserName", "gianpaolo.coro");
		config.setGcubeScope("/gcube");
		config.setParam("ServiceUserName", "gianpaolo.coro");

		config.setParam("DistributionTable", "hspec_native2050_test_parallel");
		config.setParam("CsquarecodesTable", "hcaf_d");
//		config.setParam("EnvelopeTable", "hspen_micro_1");
//		config.setParam("EnvelopeTable", "hspen_validation");
		config.setParam("EnvelopeTable", "hspen_filteredid_3a150e47_274e_47fc_b257_6a2933ec8e67");//1species
		config.setParam("OccurrencePointsTable", "occurrencecells");
		config.setParam("CreateTable", "true");

		return config;
	}
	
	private static AlgorithmConfiguration testAquamapsSuitable2050() {

		AlgorithmConfiguration config = CustomRegressor.getConfig();
		config.setNumberOfResources(5);
		config.setModel("AQUAMAPS_SUITABLE_2050");

		config.setParam("UserName", "gianpaolo.coro");
		config.setGcubeScope("/gcube");
		config.setParam("ServiceUserName", "gianpaolo.coro");

		config.setParam("DistributionTable", "hspec_suitable2050_test_parallel_validation2");
		config.setParam("CsquarecodesTable", "hcaf_d");
//		config.setParam("EnvelopeTable", "hspen_micro_1");
		config.setParam("EnvelopeTable", "hspen_filteredid_3a150e47_274e_47fc_b257_6a2933ec8e67");
		
//		config.setParam("EnvelopeTable", "hspen_validation");
		config.setParam("OccurrencePointsTable", "occurrencecells");
		config.setParam("CreateTable", "true");

		return config;
	}
}

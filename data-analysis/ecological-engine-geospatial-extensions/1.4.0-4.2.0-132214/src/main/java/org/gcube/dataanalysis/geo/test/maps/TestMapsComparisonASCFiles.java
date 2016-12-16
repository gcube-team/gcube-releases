package org.gcube.dataanalysis.geo.test.maps;

import java.util.List;

import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;
import org.gcube.dataanalysis.ecoengine.interfaces.ComputationalAgent;
import org.gcube.dataanalysis.ecoengine.interfaces.Evaluator;
import org.gcube.dataanalysis.ecoengine.processing.factories.EvaluatorsFactory;
import org.gcube.dataanalysis.ecoengine.test.regression.Regressor;

public class TestMapsComparisonASCFiles {
	/**
	 * example of parallel processing on a single machine the procedure will generate a new table for a distribution on suitable species
	 * 
	 */

	public static void main(String[] args) throws Exception {

		List<ComputationalAgent>  evaluators = EvaluatorsFactory.getEvaluators(testConfig1());
		evaluators.get(0).init();
		Regressor.process(evaluators.get(0));
		evaluators = null;
	}

	private static AlgorithmConfiguration testConfig1() {

		AlgorithmConfiguration config = Regressor.getConfig();
		config.setNumberOfResources(1);
		config.setConfigPath("./cfg");
		config.setPersistencePath("./");
		config.setAgent("MAPS_COMPARISON");
		config.setParam("DatabaseUserName","gcube");
		config.setParam("DatabasePassword","d4science2");
		config.setParam("DatabaseURL","jdbc:postgresql://localhost/testdb");
		config.setParam("DatabaseDriver","org.postgresql.Driver");

		config.setParam("Layer_1","Bio-Oracle Chlorophyll A Concentration (Mean)");
		config.setParam("Layer_2","Bio-Oracle Chlorophyll A Concentration (Max)");
		
		config.setParam("ValuesComparisonThreshold",""+0.1);
		config.setParam("Z","0");
		config.setGcubeScope("/gcube");

		return config;
	}
}

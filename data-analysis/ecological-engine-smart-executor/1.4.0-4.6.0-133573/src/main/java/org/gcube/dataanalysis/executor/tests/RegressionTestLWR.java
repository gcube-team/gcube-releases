package org.gcube.dataanalysis.executor.tests;

import java.util.List;

import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;
import org.gcube.dataanalysis.ecoengine.interfaces.ComputationalAgent;
import org.gcube.dataanalysis.ecoengine.processing.factories.GeneratorsFactory;
import org.gcube.dataanalysis.ecoengine.test.regression.Regressor;

public class RegressionTestLWR {
	/**
	 * example of parallel processing on a single machine the procedure will generate a new table for a distribution on suitable species
	 * 
	 */

public static AlgorithmConfiguration getConfig() {
		
		AlgorithmConfiguration config = new AlgorithmConfiguration();
		
		config.setConfigPath("./cfg/");
		config.setPersistencePath("./");
		config.setParam("DatabaseUserName","utente");
		config.setParam("DatabasePassword","d4science");
		config.setParam("DatabaseURL","jdbc:postgresql://statistical-manager.d.d4science.org/testdb");
		config.setParam("DatabaseDriver","org.postgresql.Driver");
		AnalysisLogger.setLogger(config.getConfigPath()+AlgorithmConfiguration.defaultLoggerFile);
		return config;
	}

	public static void main(String[] args) throws Exception {

		System.out.println("TEST 1");

		List<ComputationalAgent> generators = GeneratorsFactory.getGenerators(testLWR());
		generators.get(0).init();
		CustomRegressor.process(generators.get(0));
		generators = null;

	}
	
	private static AlgorithmConfiguration testLWR() {

		AlgorithmConfiguration config = getConfig();
		config.setNumberOfResources(5);
		config.setModel("LWR");

		config.setParam("UserName", "gianpaolo.coro");
		config.setGcubeScope("/gcube/devsec");
//		config.setGcubeScope("/d4science.research-infrastructures.eu");
		config.setParam("ServiceUserName", "gianpaolo.coro");
		
		//config.setParam("LWR_Input", "generic_iddd4bbfc2_12bd_4132_a4fc_2d64078e90e9");
		config.setParam("LWR_Input", "generic_idbf63d6cd_3001_419d_9670_1dbd5ccfbcbd");
		
		config.setParam("TableLabel", "lwrout");
		
		config.setParam("FamilyColumn", "\"family\"");
		config.setParam("RealOutputTable", "lwr6");

		return config;
	}
}

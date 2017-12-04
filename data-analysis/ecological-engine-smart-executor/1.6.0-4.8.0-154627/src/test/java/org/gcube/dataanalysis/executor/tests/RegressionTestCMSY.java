package org.gcube.dataanalysis.executor.tests;

import java.util.List;

import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;
import org.gcube.dataanalysis.ecoengine.interfaces.ComputationalAgent;
import org.gcube.dataanalysis.ecoengine.processing.factories.GeneratorsFactory;

public class RegressionTestCMSY {
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
		config.setParam("DatabaseURL","jdbc:postgresql://dbtest.research-infrastructures.eu/testdb");
		config.setParam("DatabaseDriver","org.postgresql.Driver");
		AnalysisLogger.setLogger(config.getConfigPath()+AlgorithmConfiguration.defaultLoggerFile);
		return config;
	}

	public static void main(String[] args) throws Exception {

		System.out.println("TEST 1");

		List<ComputationalAgent> generators = GeneratorsFactory.getGenerators(testCMSY());
		generators.get(0).init();
		CustomRegressor.process(generators.get(0));
		generators = null;

	}
	
	private static AlgorithmConfiguration testCMSY() {

		AlgorithmConfiguration config = getConfig();
		config.setNumberOfResources(5);
		config.setModel("CMSY");

		config.setParam("UserName", "gianpaolo.coro");
		config.setGcubeScope("/gcube");
		config.setParam("ServiceUserName", "gianpaolo.coro");
		

		
		config.setParam("IDsFile","https://dl.dropboxusercontent.com/u/12809149/WKLIFE4ID.csv");
		config.setParam("StocksFile","https://dl.dropboxusercontent.com/u/12809149/WKLIFE4Stocks.csv");
		
		//config.setParam("IDsFile","http://goo.gl/9rg3qK");
		//		config.setParam("StocksFile","http://goo.gl/Mp2ZLY");
//		config.setParam("StocksFile","http://goo.gl/btuIIe");
//		config.setParam("SelectedStock","Pan_bor_1");
		config.setParam("SelectedStock","HLH_M08");
		
		return config;
	}
}

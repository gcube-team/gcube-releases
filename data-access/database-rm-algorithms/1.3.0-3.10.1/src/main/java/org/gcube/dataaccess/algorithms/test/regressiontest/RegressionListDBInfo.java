package org.gcube.dataaccess.algorithms.test.regressiontest;

import java.util.List;

import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;
import org.gcube.dataanalysis.ecoengine.datatypes.StatisticalType;
import org.gcube.dataanalysis.ecoengine.interfaces.ComputationalAgent;
import org.gcube.dataanalysis.ecoengine.processing.factories.TransducerersFactory;
import org.gcube.dataanalysis.ecoengine.test.regression.Regressor;

public class RegressionListDBInfo {

//	static String[] algorithms = { "Postgres", "NullInputValue"};

	// static AlgorithmConfiguration[] configs = { testPostgres1(),
	// testPostgres2(), testPostgis(), Mysql(), NullInputValue(),
	// Postgres3() };

//	static AlgorithmConfiguration[] configs = { testPostgres1(), NullInputValue()};
	static String[] algorithms = { "Postgres"};
	static AlgorithmConfiguration[] configs = { testPostgres1()};

	public static void main(String[] args) throws Exception {

		// System.out.println("TEST 1");

		for (int i = 0; i < algorithms.length; i++) {
			AnalysisLogger.getLogger().debug("Executing:" + algorithms[i]);

			// ComputationalAgent trans = new WPSProcess(wps, algorithms[i]);

			List<ComputationalAgent> trans = null;
			trans = TransducerersFactory.getTransducerers(configs[i]);
			trans.get(0).init();

			// trans.setConfiguration(configs[i]);
			// trans.init();
			Regressor.process(trans.get(0));
			StatisticalType st = trans.get(0).getOutput();

			// Print Result
			AnalysisLogger.getLogger().debug("ST:" + st);
			trans = null;
		}

	}

	private static AlgorithmConfiguration testPostgres1() {

		System.out.println("TEST 1: Postgres");

		AlgorithmConfiguration config = Regressor.getConfig();

		config.setAgent("LISTDBINFO");

		// A test with a database postgres
//		config.setParam("ResourceName", "GP DB");
		
//		config.setParam("ResourceName", "AquaMaps Service DataBase");
		
//		config.setParam("ResourceName", "StatisticalManagerDataBase");
		config.setParam("ResourceName", "TabularData Database");
		
		config.setGcubeScope("/gcube/devsec");

		return config;

	}
	
	private static AlgorithmConfiguration NullInputValue() {

		System.out.println("TEST 2: NullInputValue");

		AlgorithmConfiguration config = Regressor.getConfig();

		config.setAgent("LISTDBINFO");

		// A test with a database postgres
//		config.setParam("ResourceName", "GP DB");

		config.setGcubeScope("/gcube/devsec");

		return config;

	}

}

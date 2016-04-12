package org.gcube.dataaccess.algorithms.test.regressiontest;

import java.util.List;

import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;
import org.gcube.dataanalysis.ecoengine.datatypes.StatisticalType;
import org.gcube.dataanalysis.ecoengine.interfaces.ComputationalAgent;
import org.gcube.dataanalysis.ecoengine.processing.factories.TransducerersFactory;
import org.gcube.dataanalysis.ecoengine.test.regression.Regressor;

public class RegressionListTables {

//	static String[] algorithms = { "Postgres1", "Mysql", "NullInputValue1", "NullInputValue2", "NullInputValue3" };

//	static AlgorithmConfiguration[] configs = { testPostgres1(),Mysql(), NullInputValue1(), NullInputValue2(), NullInputValue3()};

	
	static String[] algorithms = { "Postgres1"};
	static AlgorithmConfiguration[] configs = { Mysql()};
	
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

		System.out.println("TEST 1: Postgres table without rows");

		AlgorithmConfiguration config = Regressor.getConfig();

		config.setAgent("LISTTABLES");

		// A test with a database postgres
//		config.setParam("ResourceName", "GP DB");
//
//		config.setParam("DatabaseName", "aquamapsdb");
		
		config.setParam("ResourceName", "StatisticalManagerDataBase");
		config.setParam("DatabaseName", "testdb"); 
		config.setParam("SchemaName", "public");
//		config.setParam("SchemaName", "aquamapsvre");
		
		
//		config.setParam("ResourceName", "CatalogOfLife2010");
//		config.setParam("DatabaseName", "col2oct2010"); 
////		config.setParam("SchemaName", "");
		


		config.setGcubeScope("/gcube/devsec");

		return config;

	}

	private static AlgorithmConfiguration Mysql() {

		System.out.println("TEST 2: Mysql");

		AlgorithmConfiguration config = Regressor.getConfig();

		config.setAgent("LISTTABLES");

	    // a test with a database mysql
		config.setParam("ResourceName", "CatalogOfLife2010");
		config.setParam("DatabaseName", "col2oct2010");
		config.setParam("SchemaName", "");


		config.setGcubeScope("/gcube/devsec");

		return config;

	}
	
	private static AlgorithmConfiguration NullInputValue1() {

		System.out.println("TEST 3: Postgis NullInputValue1");

		AlgorithmConfiguration config = Regressor.getConfig();

		config.setAgent("LISTTABLES");

		// a test with postgis
//		config.setParam("ResourceName", "Geoserver database ");
		config.setParam("DatabaseName", "aquamapsdb");
		config.setParam("SchemaName", "public");
		
		

		config.setGcubeScope("/gcube/devsec");

		return config;

	}
	
	
	private static AlgorithmConfiguration NullInputValue2() {

		System.out.println("TEST 4: Postgis NullInputValue2");

		AlgorithmConfiguration config = Regressor.getConfig();

		config.setAgent("LISTTABLES");

		// a test with postgis
		config.setParam("ResourceName", "Geoserver database ");
//		config.setParam("DatabaseName", "aquamapsdb");
		config.setParam("SchemaName", "public");
		
		

		config.setGcubeScope("/gcube/devsec");

		return config;

	}
	
	private static AlgorithmConfiguration NullInputValue3() {

		System.out.println("TEST 5: Postgis NullInputValue3");

		AlgorithmConfiguration config = Regressor.getConfig();

		config.setAgent("LISTTABLES");

		// a test with postgis
     	config.setParam("ResourceName", "Geoserver database ");
		config.setParam("DatabaseName", "aquamapsdb");
//		config.setParam("SchemaName", "public");
		
		

		config.setGcubeScope("/gcube/devsec");

		return config;

	}
	
	
	

}

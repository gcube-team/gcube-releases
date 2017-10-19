package org.gcube.dataaccess.algorithms.test.regressiontest;

import java.util.List;

import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;
import org.gcube.dataanalysis.ecoengine.datatypes.StatisticalType;
import org.gcube.dataanalysis.ecoengine.interfaces.ComputationalAgent;
import org.gcube.dataanalysis.ecoengine.processing.factories.TransducerersFactory;
import org.gcube.dataanalysis.ecoengine.test.regression.Regressor;

public class RegressionRandomSampleOnTable {

	// static AlgorithmConfiguration[] configs = { testPostgres1(),
	// testPostgis(), testMysql1(),testMysql2(), NullInputValue1(),
	// NullInputValue2(), NullInputValue3(), NullInputValue4()};

	// static String[] algorithms = { "Postgres1", "Postgis", "Mysql1",
	// "Mysql2", "NullInputValue1", "NullInputValue2", "NullInputValue3",
	// "NullInputValue4"};

	static AlgorithmConfiguration[] configs = { testMysql1() };
	static String[] algorithms = { "Postgres1" };

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

	// Posgresql database

	@SuppressWarnings("unused")
	private static AlgorithmConfiguration testPostgres1() {

		System.out.println("TEST 1: Postgres table without rows");

		AlgorithmConfiguration config = Regressor.getConfig();

		config.setAgent("RANDOMSAMPLEONTABLE");

		// A test with a database postgres
		// config.setParam("ResourceName", "GP DB");
		//
		// config.setParam("DatabaseName", "aquamapsdb");
		// config.setParam("SchemaName", "public");
		// config.setParam("TableName", "area"); // it has not rows

//		 config.setParam("ResourceName", "StatisticalManagerDataBase");
//		 config.setParam("DatabaseName", "testdb");
//		 config.setParam("SchemaName", "public");
//		 config.setParam("TableName", "hcaf_d");
		// // config.setParam("TableName",
		// "hspec_id_3f4c79fa_442e_42ba_9344_1b3e64dc3326");

		// config.setParam("TableName",
		// "hspec_id_c8e87e16_a0b4_4f9b_b48e_f1cf60ab104c");
		// // config.setParam("TableName",
		// "generic_id037d302d_2ba0_4e43_b6e4_1a797bb91728");
		// // config.setParam("TableName", "bionymoutlevfaked2csvpreprcsv");
		//
		// // config.setParam("TableName",
		// "timeseries_id08b3abb9_c7b0_4b82_8117_64b69055416f");
		// // config.setParam("TableName",
		// "occurrence_species_idaf35d737_fb3e_43a7_b13a_611dfa97b064");
		// config.setParam("TableName",
		// "generic_id037d302d_2ba0_4e43_b6e4_1a797bb91728");

		// config.setParam("ResourceName", "AquaMaps Service DataBase");
		// config.setParam("DatabaseName", "aquamapsorgupdated");
		// config.setParam("SchemaName", "public");
		// //
		// //
		// //
		// // config.setParam("TableName", "hspec2012_07_11_12_33_05_526");
		//
		// // config.setParam("TableName", "custom2013_12_04_15_27_16_493_cet");
		//
		// config.setParam("TableName", "hspec_suitable_peng_test_tbsp_1");
		
//		config.setParam("ResourceName", "AquaMaps Service DataBase");
//		config.setParam("DatabaseName", "aquamapsorgupdated"); 
//		config.setParam("SchemaName", "public");
////		config.setParam("TableName", "hspec_suitable_peng_test_tbsp_1");
//		config.setParam("TableName", "hspen");
		
//		config.setParam("ResourceName", "Geoserver database ");
//		config.setParam("DatabaseName", "aquamapsdb");
//		config.setParam("SchemaName", "public");
//		config.setParam("TableName", "SeaVoX_sea_areas_polygons_v14");

		// Obis
		config.setParam("ResourceName", "Obis2Repository");
		config.setParam("DatabaseName", "obis");
//		config.setParam("SchemaName", "calc");
//		config.setParam("TableName", "map1d");
		config.setParam("SchemaName", "newd20110525");
		config.setParam("TableName", "edc");

		// config.setParam("Query", "select * from area limit 3");

		// config.setParam("Query",
		// "select text(the_geom) from \"Divisions\" limit 1");

		// config.setParam("TableName", "all_world");
		// config.setParam("TableName", "biodiversity_lme");

		config.setGcubeScope("/gcube/devsec");

		return config;

	}

	@SuppressWarnings("unused")
	private static AlgorithmConfiguration testPostgis() {

		System.out.println("TEST 2: Postgis");

		AlgorithmConfiguration config = Regressor.getConfig();

		config.setAgent("RANDOMSAMPLEONTABLE");

		// A test with a database postgres
		config.setParam("ResourceName", "GP DB");

		config.setParam("DatabaseName", "aquamapsdb");
		config.setParam("SchemaName", "public");
		config.setParam("TableName", "Divisions");

		config.setGcubeScope("/gcube/devsec");

		return config;

	}

	private static AlgorithmConfiguration testMysql1() {

		System.out.println("TEST 3: Mysql1");

		AlgorithmConfiguration config = Regressor.getConfig();

		config.setAgent("RANDOMSAMPLEONTABLE");

		// A test with a database mysql
		config.setParam("ResourceName", "CatalogOfLife2010");
		config.setParam("DatabaseName", "col2oct2010");
		config.setParam("TableName", "common_names");
		// config.setParam("TableName", "Common_names"); // mysql is not case
		// sensitive
//		config.setParam("TableName", "databases");
//		config.setParam("TableName", "simple_search");

		config.setGcubeScope("/gcube/devsec");

		return config;

	}

	@SuppressWarnings("unused")
	private static AlgorithmConfiguration testMysql2() {

		System.out.println("TEST 4: Mysql2");

		AlgorithmConfiguration config = Regressor.getConfig();

		config.setAgent("RANDOMSAMPLEONTABLE");

		// A test with a database mysql
		config.setParam("ResourceName", "CatalogOfLife2010");
		config.setParam("DatabaseName", "col2oct2010");
		config.setParam("TableName", "example"); // the table does not exist

		config.setGcubeScope("/gcube/devsec");

		return config;

	}

	@SuppressWarnings("unused")
	private static AlgorithmConfiguration NullInputValue1() {

		System.out.println("TEST 5: Postgis NullInputValue1");

		AlgorithmConfiguration config = Regressor.getConfig();

		config.setAgent("RANDOMSAMPLEONTABLE");

		// a test with postgis
		// config.setParam("ResourceName", "Geoserver database ");
		config.setParam("DatabaseName", "aquamapsdb");
		config.setParam("SchemaName", "public");
		config.setParam("TableName", "Divisions");

		config.setGcubeScope("/gcube/devsec");

		return config;

	}

	@SuppressWarnings("unused")
	private static AlgorithmConfiguration NullInputValue2() {
		System.out.println("TEST 6: Postgis NullInputValue2");

		AlgorithmConfiguration config = Regressor.getConfig();

		config.setAgent("RANDOMSAMPLEONTABLE");

		// a test with postgis
		config.setParam("ResourceName", "Geoserver database ");
		// config.setParam("DatabaseName", "aquamapsdb");
		config.setParam("SchemaName", "public");
		config.setParam("TableName", "Divisions");

		config.setGcubeScope("/gcube/devsec");

		return config;

	}

	@SuppressWarnings("unused")
	private static AlgorithmConfiguration NullInputValue3() {

		System.out.println("TEST 7: Postgis NullInputValue3");

		AlgorithmConfiguration config = Regressor.getConfig();

		config.setAgent("RANDOMSAMPLEONTABLE");

		// a test with postgis
		config.setParam("ResourceName", "Geoserver database ");
		config.setParam("DatabaseName", "aquamapsdb");
		// config.setParam("SchemaName", "public");
		config.setParam("TableName", "Divisions");

		config.setGcubeScope("/gcube/devsec");

		return config;

	}

	@SuppressWarnings("unused")
	private static AlgorithmConfiguration NullInputValue4() {
		System.out.println("TEST 8: Postgis NullInputValue4");

		AlgorithmConfiguration config = Regressor.getConfig();

		config.setAgent("RANDOMSAMPLEONTABLE");

		// a test with postgis
		config.setParam("ResourceName", "Geoserver database ");
		config.setParam("DatabaseName", "aquamapsdb");
		config.setParam("SchemaName", "public");
		// config.setParam("TableName", "Divisions");

		config.setGcubeScope("/gcube/devsec");

		return config;

	}

}

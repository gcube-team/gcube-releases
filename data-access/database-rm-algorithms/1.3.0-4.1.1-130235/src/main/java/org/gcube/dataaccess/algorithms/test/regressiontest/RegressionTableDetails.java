package org.gcube.dataaccess.algorithms.test.regressiontest;

import java.util.List;

import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;
import org.gcube.dataanalysis.ecoengine.datatypes.StatisticalType;
import org.gcube.dataanalysis.ecoengine.test.regression.Regressor;
import org.gcube.dataanalysis.ecoengine.interfaces.ComputationalAgent;
import org.gcube.dataanalysis.ecoengine.processing.factories.TransducerersFactory;

public class RegressionTableDetails {

	// static String[] algorithms = { "Postgres1", "Postgres2", "Postgis",
	// "Mysql", "NullInputValue", "Postgres3" };

	// static AlgorithmConfiguration[] configs = { testPostgres1(),
	// testPostgres2(), testPostgis(), Mysql(), NullInputValue(), Postgres3()};

	static String[] algorithms = { "Postgres1" };
	static AlgorithmConfiguration[] configs = { testPostgis() };

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

		config.setAgent("GETTABLEDETAILS");

		// A test with a database postgres
		// config.setParam("ResourceName", "GP DB");
		//
		// config.setParam("DatabaseName", "aquamapsdb");
		// config.setParam("SchemaName", "public");
		// config.setParam("TableName", "Divisions");

		// config.setParam("TableName", "all_world");
		// config.setParam("TableName", "biodiversity_lme");

		// StatisticalManager
		// config.setParam("ResourceName", "StatisticalManagerDataBase");
		// config.setParam("DatabaseName", "testdb");
		// // config.setParam("SchemaName", "publicd");
		//
		// config.setParam("SchemaName", "public");
		//
		// config.setParam("TableName", "hcaf_d");

		// config.setParam("TableName",
		// "hspec_id_c8e87e16_a0b4_4f9b_b48e_f1cf60ab104c");

		// // config.setParam("TableName",
		// "timeseries_id08b3abb9_c7b0_4b82_8117_64b69055416f");
		// // config.setParam("TableName",
		// "occurrence_species_idaf35d737_fb3e_43a7_b13a_611dfa97b064");
		// // config.setParam("TableName",
		// "occcluster_id_15271993_5129_4eda_92a2_fe8d22737007");
		// config.setParam("TableName",
		// "hspec_id_3f4c79fa_442e_42ba_9344_1b3e64dc3326");

		//AquaMaps
		 config.setParam("ResourceName", "AquaMaps Service DataBase");
		 config.setParam("DatabaseName", "aquamapsorgupdated");
		 config.setParam("SchemaName", "public");
//		 config.setParam("TableName", "geometry_columns");
		 config.setParam("TableName", "source_generation_requests");
		// //// config.setParam("TableName", "hspec2012_07_11_12_33_05_483");
		// config.setParam("TableName", "hspec_suitable_peng_test_tbsp_1");

		// Obis
//		config.setParam("ResourceName", "Obis2Repository");
//		config.setParam("DatabaseName", "obis");
//		config.setParam("SchemaName", "calc");
//		config.setParam("TableName", "map1d");

		config.setGcubeScope("/gcube/devsec");

		return config;

	}

	private static AlgorithmConfiguration testPostgres2() {

		System.out.println("TEST 2: Postgres");

		AlgorithmConfiguration config = Regressor.getConfig();

		config.setAgent("TableDetails");

		// A test with a database postgres
		config.setParam("ResourceName", "GP DB");

		config.setParam("DatabaseName", "aquamapsdb");
		config.setParam("SchemaName", "public");
		config.setParam("TableName", "area"); // it has not rows

		// config.setParam("TableName", "all_world");
		// config.setParam("TableName", "biodiversity_lme");

		config.setGcubeScope("/gcube/devsec");

		return config;

	}

	private static AlgorithmConfiguration testPostgis() {

		System.out.println("TEST 3: Postgis");

		AlgorithmConfiguration config = Regressor.getConfig();

		config.setAgent("GETTABLEDETAILS");
				
		// a test with postgis
		config.setParam("ResourceName", "Geoserver database ");
		config.setParam("DatabaseName", "aquamapsdb");
		config.setParam("SchemaName", "public");
		// config.setParam("TableName", "Divisions");
//		config.setParam("TableName", "ContinentalMargins");
		
//		config.setParam("TableName", "SeaVoX_sea_areas_polygons_v14");
		config.setParam("TableName", "laldrovandiaoleosa20130718230308233cest ");

		config.setGcubeScope("/gcube/devsec");

		return config;

	}

	private static AlgorithmConfiguration Mysql() {

		System.out.println("TEST 4: Mysql");

		AlgorithmConfiguration config = Regressor.getConfig();

		config.setAgent("GETTABLEDETAILS");

		// // a test with postgis
		// config.setParam("ResourceName", "Geoserver database ");
		// config.setParam("SchemaName", "public");
		// config.setParam("DatabaseName", "aquamapsdb");
		// config.setParam("TableName", "Divisions");

		// a test with a database mysql
//		config.setParam("ResourceName", "CatalogOfLife2010");
		config.setParam("ResourceName", "CatalogOfLife2010");
		config.setParam("DatabaseName", "col2oct2010");
		// config.setParam("TableName", "Common_names"); //mysql is not case
		// sensitive

		config.setParam("TableName", "databases");

		// config.setParam("Query", "select * from common_names limit 3");

		config.setGcubeScope("/gcube/devsec");

		return config;

	}

	private static AlgorithmConfiguration NullInputValue() {

		System.out.println("TEST 5: Postgis NullInputValue");

		AlgorithmConfiguration config = Regressor.getConfig();

		config.setAgent("LISTTABLEDETAILS");

		// a test with postgis
		config.setParam("ResourceName", "Geoserver database ");
		config.setParam("DatabaseName", "aquamapsdb");
		// config.setParam("SchemaName", "public");
		config.setParam("TableName", "Divisions");

		config.setGcubeScope("/gcube/devsec");

		return config;

	}

	private static AlgorithmConfiguration Postgres3() {

		System.out.println("TEST 6: Postgres");

		AlgorithmConfiguration config = Regressor.getConfig();

		config.setAgent("LISTTABLEDETAILS");

		// a test with postgis
		config.setParam("ResourceName", "Geoserver database ");
		config.setParam("DatabaseName", "aquamapsdb");
		config.setParam("SchemaName", "public");
		config.setParam("TableName", "divisions"); // postgres is case sensitive

		config.setGcubeScope("/gcube/devsec");

		return config;

	}

}

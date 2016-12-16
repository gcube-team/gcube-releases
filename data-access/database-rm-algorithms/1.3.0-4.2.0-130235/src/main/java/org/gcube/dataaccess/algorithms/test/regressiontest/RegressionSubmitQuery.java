package org.gcube.dataaccess.algorithms.test.regressiontest;

import java.util.List;

import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;
import org.gcube.dataanalysis.ecoengine.datatypes.StatisticalType;
import org.gcube.dataanalysis.ecoengine.interfaces.ComputationalAgent;
import org.gcube.dataanalysis.ecoengine.processing.factories.TransducerersFactory;
import org.gcube.dataanalysis.ecoengine.test.regression.Regressor;

public class RegressionSubmitQuery {

//	static String[] algorithms = { "Postgres1", "Mysql", "Postgres3", "Postgres4", "NullInputValue1", "NullInputValue2", "NullInputValue3", "NullInputValue4", "NullInputValue5", "Postgis"};

//	static AlgorithmConfiguration[] configs = { testPostgres1(),
//			testPostgres2(), testPostgis(), Mysql(), NullInputValue(),
//			Postgres3() };
	
//	static AlgorithmConfiguration[] configs = { testPostgres1(), Mysql(), Postgres3(), Postgres4(), NullInputValue1(), NullInputValue2(), NullInputValue3(), NullInputValue4(), NullInputValue5(), Postgis()};

	static String[] algorithms = {"Postgres1"};
	static AlgorithmConfiguration[] configs = { testPostgres1() };
	
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

		config.setAgent("LISTSUBMITQUERY");

//		// A test with a database postgres
//		config.setParam("ResourceName", "StatisticalManagerDataBase");
//		config.setParam("DatabaseName", "testdb");
//////		config.setParam("", "TRUE");
//		config.setParam("Read-Only Query", "true");
//		config.setParam("Apply Smart Correction", "FALSE");
//		config.setParam("Language", "NONE");
////		config.setParam("Query", "DELETE from test_gsay_03217cfda4244870b4d11f9e0eca58fe");
//		config.setParam("Query", "select * from hcaf_d limit 20");
		
//		config.setParam("Apply Smart Correction", "TRUE");
//		config.setParam("Language", "POSTGRES");
		
		
		
//		config.setParam("SchemaName", "public");
//		config.setParam("TableName", "hcaf_d");
		
		
//		
//
//		config.setParam("Query", "select * from hcaf_d limit 1");
//		config.setParam("Query", "select * from hspen limit 6");
		
//		config.setParam("Query", "select * from bionymfaked18csvpreprcsv limit 2");
		
//		config.setParam("Query", "select count (*)from (select csquarecode from hcaf_d)");
		
//		config.setParam("Query", "select csquarecode,months,sum(effort) as effort, sum(total_yft_catch) as total_yft_catch from (select csquarecode,to_char(time,'MM') months,sum(effort) as effort,sum(total_yft_catch) as total_yft_catch from timeseries_idacdbb646_7500_4920_8e0d_aa38cc99a4a6 group by csquarecode,time order by time ASC) as a group by csquarecode,months order by csquarecode");
		
//		config.setParam("Query", "select csquarecode, total_yft_catch from timeseries_idacdbb646_7500_4920_8e0d_aa38cc99a4a6 limit 3");
		
//		config.setParam("ResourceName", "DionysusDB");
//		config.setParam("DatabaseName", "World"); 
//		config.setParam("SchemaName", "public");
//		config.setParam("TableName", "countrylanguage");    //mysql is not case sensitive
////		config.setParam("Query", "select * from countrylanguage limit 10");
//		config.setParam("Query", "SELECT * FROM information_schema.COLUMNS WHERE table_name ='countrylanguage' and table_schema='public'");
		
//		config.setParam("ResourceName", "GP DB");
//
//		config.setParam("DatabaseName", "aquamapsdb");
//		config.setParam("", "TRUE");
//		config.setParam("SchemaName", "public");
//		config.setParam("TableName", "Divisions"); 
//		config.setParam("Query","select gid, area, perimeter, nafo_, nafo_id, zone from \"Divisions\" limit 100");
		
//		config.setParam("TableName", "area");        // it has not rows
		
//		config.setParam("Query", "select * from area limit 3");
//		config.setParam("Query", "select gid, area, perimeter, CAST(the_geom as text) from \"Divisions\" limit 10");
		
//		config.setParam("Query", "select text(the_geom) from \"Divisions\" limit 1");
		
//		config.setParam("Query", "select perimeter,zone from \"Divisions\" where gid='7'");
		
//		config.setParam("Query", "select area, CAST(perimeter as text) from \"Divisions\" order by random() limit 2");

		// config.setParam("TableName", "all_world");
		// config.setParam("TableName", "biodiversity_lme");
		
//		//Obis
//		config.setParam("ResourceName", "Obis2Repository");
//		config.setParam("DatabaseName", "obis");
//		config.setParam("Read-Only Query", "trie");
//		config.setParam("Apply Smart Correction", "FALSE");
//		config.setParam("Language", "NONE");
////		config.setParam("Query", "select id from fmap.randomdrs limit 1");
////		config.setParam("Query", "select lifestage from randomdrs");
//		config.setParam("Query", "select * from newd20110525."+"\""+"edc"+ "\""+" where id='76864082'");
		
		
//		config.setParam("ResourceName", "AquaMaps Service DataBase");
//		config.setParam("DatabaseName", "aquamapsorgupdated");
//		config.setParam("Read-Only Query", "TRUE");
//		config.setParam("Apply Smart Correction", "FALSE");
//		config.setParam("Language", "NONE");
////		config.setParam("Query", "select id from fmap.randomdrs limit 1");
////		config.setParam("Query", "select * from (select * from maxminlat_hspen2012_02_28_17_45_49_572 as a join maxminlat_hspen2011_09_23_15_31_47_530 as b on a.maxclat=b.maxclat limit 2");
//		
////		config.setParam("Query", "select * from maxminlat_hspen2012_02_28_17_45_49_572 as a join maxminlat_hspen2011_09_23_15_31_47_530 as b on a.maxclat=b.maxclat limit 2");
//		
////		config.setParam("Query", "select * from hcaf_d_2018_linear_01341919234605 as a join hcaf_d_2024_linear_11341919235343 as b on a.csquarecode = b.csquarecode limit 1");
//		
//		config.setParam("Query", "select * from hcaf_d_2018_linear_01341919234605 as a, hcaf_d_2024_linear_11341919235343 as b where a.csquarecode = b.csquarecode limit 1");
//		
		
		
		config.setParam("ResourceName", "Geoserver database ");
		config.setParam("DatabaseName", "aquamapsdb");
		config.setParam("SchemaName", "public");
		config.setParam("Read-Only Query", "true");
		config.setParam("Apply Smart Correction", "FALSE");
		config.setParam("Language", "NONE");
    	config.setParam("Query", "select st_astext(the_geom) from" +"\""+"SeaVoX_sea_areas_polygons_v14"+"\""+"limit 1");
//		config.setParam("Query", "select * from public.depthmean limit 10");
		

		config.setGcubeScope("/gcube/devsec");

		return config;

	}
	
	private static AlgorithmConfiguration Mysql() {

		System.out.println("TEST 2: Mysql");

		AlgorithmConfiguration config = Regressor.getConfig();

		config.setAgent("LISTSUBMITQUERY");

//		// a test with postgis
//		config.setParam("ResourceName", "Geoserver database ");
//		config.setParam("SchemaName", "public");
//		config.setParam("DatabaseName", "aquamapsdb");
//		config.setParam("TableName", "Divisions");
		
		
		
		//a test with a database mysql
		config.setParam("ResourceName", "CatalogOfLife2010");
		config.setParam("DatabaseName", "col2oct2010"); 
		config.setParam("TableName", "Common_names");    //mysql is not case sensitive
//		config.setParam("Query", "select * from common_names limit 10");
		config.setParam("Read-Only Query", "TRUE");
		config.setParam("Apply Smart Correction", "FALSE");
		config.setParam("Language", "NONE");
		
//		config.setParam("Query", "select a.name_code as uno, b.name_code as due from common_names as a join distribution as b on a.name_code=b.name_code limit 2");		
		config.setParam("Query", "select * from common_names as a join distribution as b on a.name_code=b.name_code");
		//		config.setParam("TableName", "specialists");    
		
//		config.setParam("Query", "select * from specialists limit 3");

		config.setGcubeScope("/gcube/devsec");

		return config;

	}
	
	private static AlgorithmConfiguration Postgres3() {

		System.out.println("TEST 3: Postgis");

		AlgorithmConfiguration config = Regressor.getConfig();

		config.setAgent("LISTSUBMITQUERY");

		// a test with postgis
		config.setParam("ResourceName", "Geoserver database ");
		config.setParam("DatabaseName", "aquamapsdb");
		config.setParam("SchemaName", "public");
		config.setParam("TableName", "Divisions");    //postgres is case sensitive
		config.setParam("Query", "select the_geom from Divisions limit 3");
		

		config.setGcubeScope("/gcube/devsec");

		return config;

	}
	
	private static AlgorithmConfiguration Postgres4() {

		System.out.println("TEST 4: Postgis filter query");

		AlgorithmConfiguration config = Regressor.getConfig();

		config.setAgent("LISTSUBMITQUERY");

		// a test with postgis
		config.setParam("ResourceName", "Geoserver database ");
		config.setParam("DatabaseName", "aquamapsdb");
		config.setParam("SchemaName", "public");
		config.setParam("TableName", "Divisions");    //postgres is case sensitive
//		config.setParam("Query", "select the_geom from Divisions limit 30");
		config.setParam("Query", "EXPLAIN ANALYZE select gid from Divisions limit 3");
		

		config.setGcubeScope("/gcube/devsec");

		return config;

	}
	
	private static AlgorithmConfiguration NullInputValue1() {

		System.out.println("TEST 5: Postgis NullInputValue1");

		AlgorithmConfiguration config = Regressor.getConfig();

		config.setAgent("LISTSUBMITQUERY");

		// a test with postgis
//		config.setParam("ResourceName", "Geoserver database ");
		config.setParam("DatabaseName", "aquamapsdb");
		config.setParam("SchemaName", "public");
		config.setParam("TableName", "Divisions");
		config.setParam("Query", "EXPLAIN ANALYZE select gid from Divisions limit 3");

		config.setGcubeScope("/gcube/devsec");

		return config;

	}
	
	private static AlgorithmConfiguration NullInputValue2() {

		System.out.println("TEST 5: Postgis NullInputValue2");

		AlgorithmConfiguration config = Regressor.getConfig();

		config.setAgent("LISTSUBMITQUERY");

		// a test with postgis
		config.setParam("ResourceName", "Geoserver database ");
//		config.setParam("DatabaseName", "aquamapsdb");
		config.setParam("SchemaName", "public");
		config.setParam("TableName", "Divisions");
		config.setParam("Query", "EXPLAIN ANALYZE select gid from Divisions limit 3");

		config.setGcubeScope("/gcube/devsec");

		return config;

	}
	
	private static AlgorithmConfiguration NullInputValue3() {

		System.out.println("TEST 5: Postgis NullInputValue3");

		AlgorithmConfiguration config = Regressor.getConfig();

		config.setAgent("LISTSUBMITQUERY");

		// a test with postgis
		config.setParam("ResourceName", "Geoserver database ");
		config.setParam("DatabaseName", "aquamapsdb");
//		config.setParam("SchemaName", "public");
		config.setParam("TableName", "Divisions");
		config.setParam("Query", "EXPLAIN ANALYZE select gid from Divisions limit 3");

		config.setGcubeScope("/gcube/devsec");

		return config;

	}
	
	private static AlgorithmConfiguration NullInputValue4() {

		System.out.println("TEST 5: Postgis NullInputValue4");

		AlgorithmConfiguration config = Regressor.getConfig();

		config.setAgent("LISTSUBMITQUERY");

		// a test with postgis
		config.setParam("ResourceName", "Geoserver database ");
		config.setParam("DatabaseName", "aquamapsdb");
		config.setParam("SchemaName", "public");
//		config.setParam("TableName", "Divisions");
		config.setParam("Query", "EXPLAIN ANALYZE select gid from Divisions limit 3");

		config.setGcubeScope("/gcube/devsec");

		return config;

	}
	
	private static AlgorithmConfiguration NullInputValue5() {

		System.out.println("TEST 6: Postgis NullInputValue5");

		AlgorithmConfiguration config = Regressor.getConfig();

		config.setAgent("LISTSUBMITQUERY");

		// a test with postgis
		config.setParam("ResourceName", "Geoserver database ");
		config.setParam("DatabaseName", "aquamapsdb");
		config.setParam("SchemaName", "public");
		config.setParam("TableName", "Divisions");
//		config.setParam("Query", "EXPLAIN ANALYZE select gid from Divisions limit 3");

		config.setGcubeScope("/gcube/devsec");

		return config;

	}
	
	private static AlgorithmConfiguration Postgis() {

		System.out.println("TEST 7: Postgis");

		AlgorithmConfiguration config = Regressor.getConfig();

		config.setAgent("LISTSUBMITQUERY");

		// a test with postgis
		config.setParam("ResourceName", "Geoserver database ");
		config.setParam("DatabaseName", "aquamapsdb");
		config.setParam("SchemaName", "public");
		config.setParam("TableName", "Divisions");    //postgres is case sensitive
		config.setParam("Query", "select * from \"Divisions\" limit 1");
		

		config.setGcubeScope("/gcube/devsec");

		return config;

	}
	


}

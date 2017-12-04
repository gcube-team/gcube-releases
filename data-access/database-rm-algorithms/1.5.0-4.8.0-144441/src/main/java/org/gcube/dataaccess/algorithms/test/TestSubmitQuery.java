package org.gcube.dataaccess.algorithms.test;

import java.util.List;

import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;
import org.gcube.dataanalysis.ecoengine.interfaces.ComputationalAgent;
import org.gcube.dataanalysis.ecoengine.processing.factories.TransducerersFactory;
import org.gcube.dataanalysis.ecoengine.test.regression.Regressor;

public class TestSubmitQuery {

	public static void main(String[] args) throws Exception {
		System.out.println("TEST 1");
			
		List<ComputationalAgent> trans = null;
		trans = TransducerersFactory.getTransducerers(testConfigLocal());
		trans.get(0).init();

		Regressor.process(trans.get(0));
		trans.get(0).getOutput();

		trans = null;
	}

	private static AlgorithmConfiguration testConfigLocal() {

		AlgorithmConfiguration config = Regressor.getConfig();

		config.setAgent("LISTSUBMITQUERY");

//		// //A test with a database postgres
//		 config.setParam("ResourceName", "GP DB");
//		
//		 //connection's parameters for a database postgres
//		 config.setParam("DatabaseName", "aquamapsdb");
//		 config.setParam("SchemaName", "public");
//		 config.setParam("TableName","area" );
//		 config.setParam("Query","select * from area limit 3" );
		 
		 
		// // config.setParam("TableName", "Divisions");
		// config.setParam("TableName", "all_world");
		// config.setParam("Query", "select * from all_world");

		// config.setParam("userName", "postgres");
		// config.setParam("password", "d4science2");
		// config.setParam("driverName", "org.postgresql.Driver");
		// config.setParam("URL",
		// "jdbc:postgresql://geoserver-dev.d4science-ii.research-infrastructures.eu:5432/aquamapsdb");
		// config.setParam("dialect",
		// "org.hibernate.dialect.PostgreSQLDialect");
		

		// a test with a database mysql
//		config.setParam("ResourceName", "CatalogOfLife2010");
//		config.setParam("DatabaseName", "col2oct2010"); 
//		config.setParam("TableName", "common_names");
////		config.setParam("Query", "select record_id, name_code from common_names limit 3");
////		config.setParam("Query", "select record_id as a, name_code as b from common_names limit 3");
//		
////		config.setParam("Query", "select  name_code, record_id from common_names limit 3");
//		
//		config.setParam("Query", "select record_id, name_code from common_names limit 3");
		
		config.setParam("ResourceName", "StatisticalManagerDataBase");
		config.setParam("DatabaseName", "testdb"); 
		config.setParam("Query", "select count (*)from (select csquarecode from hcaf_d)");	
		
		

//		// a test with postgis
//		config.setParam("ResourceName", "Geoserver database ");
//		config.setParam("DatabaseName", "aquamapsdb");
//		config.setParam("SchemaName", "public");
//
//		config.setParam("TableName", "Divisions");
//	config.setParam("Query", "select gid, area from \"Divisions\" limit 30");
//		
////		config.setParam("Query", "select the_geom from Divisions limit 30");
//		
////		config.setParam("Query", "select text(the_geom) from Divisions limit 30");
//		
//		config.setParam("Query", "EXPLAIN ANALYZE select gid from \"Divisions\" limit 30");
		
		config.setGcubeScope("/gcube/devsec");

		// config.setGcubeScope("/gcube");
		return config;

	}

}

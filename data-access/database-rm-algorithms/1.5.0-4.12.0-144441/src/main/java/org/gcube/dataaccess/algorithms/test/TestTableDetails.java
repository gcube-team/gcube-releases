package org.gcube.dataaccess.algorithms.test;

import java.util.List;

import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;
import org.gcube.dataanalysis.ecoengine.interfaces.ComputationalAgent;
import org.gcube.dataanalysis.ecoengine.processing.factories.TransducerersFactory;
import org.gcube.dataanalysis.ecoengine.test.regression.Regressor;

public class TestTableDetails {

	
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
		 
		 config.setAgent("LISTTABLEDETAILS");
		 
//			//A test with a database postgres
//			config.setParam("ResourceName", "GP DB");
//			
//	        //connection's parameters for a database postgres
//			config.setParam("DatabaseName", "aquamapsdb"); 
//			config.setParam("SchemaName", "public");
////			config.setParam("TableName", "Divisions");
//			
////			config.setParam("TableName", "all_world");
//			
//			config.setParam("TableName", "biodiversity_lme");		
		 
		 
//			
//			config.setParam("userName", "postgres");
//			config.setParam("password", "d4science2");
//			config.setParam("driverName", "org.postgresql.Driver");
//			config.setParam("URL",
//					"jdbc:postgresql://geoserver-dev.d4science-ii.research-infrastructures.eu:5432/aquamapsdb");
//			config.setParam("dialect", "org.hibernate.dialect.PostgreSQLDialect");
		 
		 
//			//A test with a database postgres
//			config.setParam("ResourceName", "StatisticalManagerDataBase");
//			
//	        //connection's parameters for a database postgres
//			config.setParam("DatabaseName", "testdb"); 
//			config.setParam("SchemaName", "public");
////			config.setParam("TableName", "Divisions");
//			
////			config.setParam("TableName", "all_world");
//			
//			config.setParam("TableName", "hcaf_d");		
////			
////			config.setParam("userName", "utente");
////			config.setParam("password", "d4science");
////			config.setParam("driverName", "org.postgresql.Driver");
////			config.setParam("URL",
////					"jdbc:postgresql://geoserver-dev.d4science-ii.research-infrastructures.eu:5432/aquamapsdb");
////			config.setParam("dialect", "org.hibernate.dialect.PostgreSQLDialect");
		 
		 
		 
			
			
//			//a test with a database mysql
//			config.setParam("ResourceName", "CatalogOfLife2010");
//			config.setParam("DatabaseName", "col2oct2010"); 
//			config.setParam("TableName", "Common_names");
		 
		 
		 //a test with postgis
			config.setParam("ResourceName", "Geoserver database ");
			
//			config.setParam("DatabaseName", "aquamapsdb"); 
			config.setParam("DatabaseName", "aquamapsdb"); 
			
			config.setParam("SchemaName", "public");
			config.setParam("TableName", "divisions");
		 
			
			
		 
		 
		 
		 config.setGcubeScope("/gcube/devsec");	
		 return config;
			 
		}
	
	
	

}

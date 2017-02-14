package org.gcube.dataaccess.algorithms.test;

import java.util.List;

import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;
import org.gcube.dataanalysis.ecoengine.interfaces.ComputationalAgent;
import org.gcube.dataanalysis.ecoengine.processing.factories.TransducerersFactory;
import org.gcube.dataanalysis.ecoengine.test.regression.Regressor;

public class TestListTables {

	
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
		 
		 config.setAgent("LISTTABLES");
		 
			//A test with a database postgres
         	config.setParam("ResourceName", "GP DB");
			
	        //connection's parameters for a database postgres

			config.setParam("DatabaseName", "aquamapsdb"); 
			config.setParam("SchemaName", "public");
			
//			config.setParam("userName", "postgres");
//			config.setParam("password", "d4science2");
//			config.setParam("driverName", "org.postgresql.Driver");
//			config.setParam("URL",
//					"jdbc:postgresql://geoserver-dev.d4science-ii.research-infrastructures.eu:5432/aquamapsdb");
			
			
			//a test with a database mysql
		 
		 
		 
		 config.setGcubeScope("/gcube");	
		 return config;
			 
		}

}

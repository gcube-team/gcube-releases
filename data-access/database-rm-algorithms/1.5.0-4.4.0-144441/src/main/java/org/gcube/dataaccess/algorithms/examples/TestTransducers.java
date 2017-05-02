package org.gcube.dataaccess.algorithms.examples;

import java.util.List;

import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;
import org.gcube.dataanalysis.ecoengine.interfaces.ComputationalAgent;
import org.gcube.dataanalysis.ecoengine.processing.factories.TransducerersFactory;
import org.gcube.dataanalysis.ecoengine.test.regression.Regressor;

public class TestTransducers {
	 
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
	
//	AlgorithmConfiguration config=new AlgorithmConfiguration();
	
		 
	    config.setConfigPath("./cfg");
	    
	    config.setParam("DatabaseName", "Obis2Repository");
	    config.setParam("DatabaseUserName","postgres");
	    config.setParam("DatabasePassword","0b1s@d4sc13nc3");
		config.setParam("databaseNamebaseDriver","org.postgresql.Driver");
		config.setParam("DatabaseURL", "jdbc:postgresql://obis2.i-marine.research-infrastructures.eu:5432/obis");
		

		System.out.println("config: " + config.getParam("DatabaseUserName"));
	
	
	
	 
//	System.out.println(config.getDatabaseURL()); 
	 
	 config.setAgent("LISTNAMES_TABLES");
	 
	 config.setParam("longitudeColumn", "decimallongitude");
	 config.setParam("latitudeColumn", "decimallatitude");
	 config.setParam("recordedByColumn", "recordedby");
	 config.setParam("scientificNameColumn", "scientificname");
	 config.setParam("eventDateColumn", "eventdate");
	 config.setParam("lastModificationColumn", "modified");
	 config.setParam("OccurrencePointsTableName", "whitesharkoccurrences2");
	 config.setParam("finalTableName", "whitesharkoccurrencesnoduplicates");
	 config.setParam("spatialTolerance", "0.5");
	 config.setParam("confidence", "80");
	 
	
	 
	 return config;
	 
}

}

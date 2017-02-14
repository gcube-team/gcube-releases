package org.gcube.dataanalysis.test;

import java.util.List;
import java.util.UUID;

import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;
import org.gcube.dataanalysis.ecoengine.datatypes.StatisticalType;
import org.gcube.dataanalysis.ecoengine.interfaces.ComputationalAgent;
import org.gcube.dataanalysis.ecoengine.processing.factories.TransducerersFactory;
import org.gcube.dataanalysis.ecoengine.test.regression.Regressor;
import org.gcube.dataanalysis.geothermal.AbstractProcess;

public class TestGeothermalProcessing {

	
public static void main(String[] args) throws Exception {
		AbstractProcess.display=true;
		System.out.println("TEST 1");
		List<ComputationalAgent> trans = null;
		trans = TransducerersFactory.getTransducerers(testConfig());
		trans.get(0).init();
		Regressor.process(trans.get(0));
		StatisticalType st = trans.get(0).getOutput();
		trans = null;
}

private static AlgorithmConfiguration testConfig() {
		
		AlgorithmConfiguration config = Regressor.getConfig();
		config.setConfigPath("./cfg/");
		config.setPersistencePath("./");
		
		config.setAgent("TESTGEO4");
		config.setGcubeScope("/gcube/devsec/statVRE");
		
		config.setParam("DatabaseUserName","gcube");
		config.setParam("DatabasePassword","d4science2");
		config.setParam("DatabaseURL","jdbc:postgresql://146.48.87.169/testdb");
		config.setParam("DatabaseDriver","org.postgresql.Driver");
		config.setParam("CountryName","ALL");
		config.setParam("StartYear","1000");
		config.setParam("EndYear","3000");
		config.setParam("Aggregation","SUM");
		
		return config;
	}

}

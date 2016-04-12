package org.gcube.dataanalysis.test;

import java.util.List;

import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;
import org.gcube.dataanalysis.ecoengine.datatypes.StatisticalType;
import org.gcube.dataanalysis.ecoengine.interfaces.ComputationalAgent;
import org.gcube.dataanalysis.ecoengine.processing.factories.TransducerersFactory;
import org.gcube.dataanalysis.ecoengine.test.regression.Regressor;

public class TestFinTaxaMatch {

	
public static void main(String[] args) throws Exception {
		
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
		config.setAgent("FIN_TAXA_MATCH");
		config.setParam("Genus", "gadus");
		config.setParam("Species", "");

		config.setParam("ComparisonOperatorforGenus", "EQUAL");
		config.setParam("ComparisonOperatorforSpecies", "EQUAL");
		
		return config;
	}

	
}

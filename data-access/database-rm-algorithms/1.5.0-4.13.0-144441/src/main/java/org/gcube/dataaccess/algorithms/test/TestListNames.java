package org.gcube.dataaccess.algorithms.test;

import java.util.List;

import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;
import org.gcube.dataanalysis.ecoengine.datatypes.StatisticalType;
import org.gcube.dataanalysis.ecoengine.interfaces.ComputationalAgent;
import org.gcube.dataanalysis.ecoengine.processing.factories.TransducerersFactory;
import org.gcube.dataanalysis.ecoengine.test.regression.Regressor;

public class TestListNames {

	public static void main(String[] args) throws Exception {
		System.out.println("TEST 1");
		List<ComputationalAgent> trans = null;
		trans = TransducerersFactory.getTransducerers(testConfigLocal());
		trans.get(0).init();


		Regressor.process(trans.get(0));
		StatisticalType output = trans.get(0).getOutput();
		System.out.println(output);
		trans = null;
		}

		private static AlgorithmConfiguration testConfigLocal() {
			 
		 AlgorithmConfiguration config = Regressor.getConfig();
		 
		 config.setAgent("LISTDBNAMES");
		 config.setGcubeScope("/gcube/devNext/NextNext");	
		 return config;
			 
		}

}

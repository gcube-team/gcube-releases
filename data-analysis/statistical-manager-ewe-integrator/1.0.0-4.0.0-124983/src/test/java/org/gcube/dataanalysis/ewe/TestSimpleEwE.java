package org.gcube.dataanalysis.ewe;

import java.util.List;

import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;
import org.gcube.dataanalysis.ecoengine.interfaces.ComputationalAgent;
import org.gcube.dataanalysis.ecoengine.processing.factories.TransducerersFactory;
import org.gcube.dataanalysis.ecoengine.test.regression.Regressor;

public class TestSimpleEwE {

	public static void main(String[] args) throws Exception {
		List<ComputationalAgent> trans = TransducerersFactory
				.getTransducerers(testConfigLocal());
		trans.get(0).init();
		Regressor.process(trans.get(0));
		trans = null;
	}

	private static AlgorithmConfiguration testConfigLocal() {
		AlgorithmConfiguration config = Regressor.getConfig();
		config.setAgent("ECOPATH_WITH_ECOSIM");

		config.setParam("Model File", "/tmp/ewe/input/Georgia_Strait2.eiixml");
		config.setParam("Config File", "/tmp/ewe/input/run_config.xml");
//		config.setParam("Model Name", "Georgia_Strait.eiixml");
		
		config.setConfigPath("./cfg/");
		config.setPersistencePath("/tmp/ewe/persistence");
		
		return config;
	}

}
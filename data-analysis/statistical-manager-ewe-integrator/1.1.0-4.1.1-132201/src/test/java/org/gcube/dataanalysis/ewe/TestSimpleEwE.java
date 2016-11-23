package org.gcube.dataanalysis.ewe;

import java.net.ProxySelector;
import java.util.List;
import java.util.UUID;

import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;
import org.gcube.dataanalysis.ecoengine.interfaces.ComputationalAgent;
import org.gcube.dataanalysis.ecoengine.processing.factories.TransducerersFactory;
import org.gcube.dataanalysis.ecoengine.test.regression.Regressor;
import org.gcube.dataanalysis.ewe.util.PropertiesBasedProxySelector;

public class TestSimpleEwE {

	public static void main(String[] args) throws Exception {

    ProxySelector.setDefault(new PropertiesBasedProxySelector("/home/paolo/.proxy-settings"));
	  
	  List<ComputationalAgent> trans = TransducerersFactory
				.getTransducerers(testConfigLocal());
		trans.get(0).init();
		Regressor.process(trans.get(0));
		trans = null;
	}

	private static AlgorithmConfiguration testConfigLocal() {
	  

		AlgorithmConfiguration config = Regressor.getConfig();
		config.setAgent("ECOPATH_WITH_ECOSIM");

    config.setGcubeScope("/gcube/devNext/NextNext");

		config.setParam("Model File", "/tmp/ewe/input/Georgia_Strait.eiixml");
		config.setParam("Config File", "/tmp/ewe/input/run_config.xml");
		
		config.setTaskID(UUID.randomUUID().toString());
		
		config.setConfigPath("./cfg/");
		config.setPersistencePath("/tmp/ewe/persistence");

		return config;
	}

}
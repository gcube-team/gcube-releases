package org.gcube.dataanalysis.geo.wps.test.regression;

import java.util.List;

import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;
import org.gcube.dataanalysis.ecoengine.datatypes.StatisticalType;
import org.gcube.dataanalysis.ecoengine.interfaces.ComputationalAgent;
import org.gcube.dataanalysis.ecoengine.processing.factories.TransducerersFactory;
import org.gcube.dataanalysis.ecoengine.test.regression.Regressor;
import org.gcube.dataanalysis.geo.wps.interfaces.WPSProcess;

public class RegressionTestWPSProcess {

	public static void main(String[] args) throws Exception {

		System.out.println("TEST 1");
		
		ComputationalAgent trans = new WPSProcess("http://geoprocessing.demo.52north.org:8080/wps/WebProcessingService","org.n52.wps.extension.GetFuelPriceProcess");
		
		trans.setConfiguration(testConfig());
		trans.init();
		Regressor.process(trans);
		StatisticalType st = trans.getOutput();
		trans = null;
	}

	private static AlgorithmConfiguration testConfig() {
		AlgorithmConfiguration config = Regressor.getConfig();
		config.setParam("fuelType", "gasoline");
		return config;
	}
}

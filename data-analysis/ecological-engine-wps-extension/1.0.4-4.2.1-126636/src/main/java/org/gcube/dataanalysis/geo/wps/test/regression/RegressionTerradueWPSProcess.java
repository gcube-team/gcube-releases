package org.gcube.dataanalysis.geo.wps.test.regression;

import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;
import org.gcube.dataanalysis.ecoengine.datatypes.StatisticalType;
import org.gcube.dataanalysis.ecoengine.interfaces.ComputationalAgent;
import org.gcube.dataanalysis.ecoengine.test.regression.Regressor;
import org.gcube.dataanalysis.geo.wps.interfaces.WPSProcess;

public class RegressionTerradueWPSProcess {

	public static void main(String[] args) throws Exception {

		System.out.println("TEST 1");
		
		ComputationalAgent trans = new WPSProcess("http://wps01.i-marine.d4science.org/wps/WebProcessingService","com.terradue.wps_hadoop.processes.examples.async.Async");
		trans.setConfiguration(testConfig());
		trans.init();
		Regressor.process(trans);
		StatisticalType st = trans.getOutput();
		AnalysisLogger.getLogger().debug("ST:"+st);
		trans = null;
	}

	
	private static AlgorithmConfiguration testConfig() {

		AlgorithmConfiguration config = Regressor.getConfig();
		
		config.setParam("secondsDelay", "30");
		
		
		return config;
	}
	
	
	
	
	
}

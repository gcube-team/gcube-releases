package org.gcube.dataanalysis.executor.tests;

import java.util.List;

import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;
import org.gcube.dataanalysis.ecoengine.datatypes.PrimitiveType;
import org.gcube.dataanalysis.ecoengine.datatypes.StatisticalType;
import org.gcube.dataanalysis.ecoengine.interfaces.ComputationalAgent;
import org.gcube.dataanalysis.ecoengine.processing.factories.TransducerersFactory;

public class TestKnitrCompiler {

	public static void main(String[] args) throws Exception {
		
		// setup the configuration
		AlgorithmConfiguration config = new AlgorithmConfiguration();
		// set the path to the cfg folder and to the PARALLEL_PROCESSING folder
		config.setConfigPath("./cfg/");
		config.setPersistencePath("./PARALLEL_PROCESSING");
		AnalysisLogger.setLogger(config.getConfigPath()+AlgorithmConfiguration.defaultLoggerFile);
		//set the scope and the user (optional for this test)
		config.setGcubeScope( "/gcube/devsec/devVRE");
		config.setParam("ServiceUserName", "test.user");
		config.setParam("zipfile", "C:/Users/coro/Desktop/WorkFolder/Workspace/EcologicalEngineSmartExecutor/cfg/knitr_test_julien_wfs.zip");
		config.setParam("file.inout", "test_julien_zip");
		
		//set the name of the algorithm to call, as is is in the transducerer.properties file
		config.setAgent("KNITR_COMPILER");
			
		//recall the transducerer with the above name 
		List<ComputationalAgent> transducers = TransducerersFactory.getTransducerers(config);
		ComputationalAgent transducer =transducers.get(0);
		//init the transducerer
		transducer.init();
		//start the process
		CustomRegressor.process(transducer);
		//retrieve the output
		StatisticalType st = transducer.getOutput();
		System.out.println("st:"+((PrimitiveType)st).getContent());
	}
	
}

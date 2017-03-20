package org.gcube.dataanalysis.executor.tests;

import java.util.List;

import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;
import org.gcube.dataanalysis.ecoengine.datatypes.PrimitiveType;
import org.gcube.dataanalysis.ecoengine.datatypes.StatisticalType;
import org.gcube.dataanalysis.ecoengine.interfaces.ComputationalAgent;
import org.gcube.dataanalysis.ecoengine.processing.factories.TransducerersFactory;
import org.gcube.dataanalysis.ecoengine.test.regression.Regressor;

public class TestSGVMInterpolation {

	public static void main(String[] args) throws Exception {
		// setup the configuration
		AlgorithmConfiguration config = new AlgorithmConfiguration();
		// set the path to the cfg folder and to the PARALLEL_PROCESSING folder
		config.setConfigPath("./cfg/");
		config.setPersistencePath("./PARALLEL_PROCESSING");
		//set the user's inputs. They will passed by the SM to the script in the following way:
//		config.setParam("InputFile", "<absolute path to the file>/tacsatmini.csv"); //put the absolute path to the input file
		config.setParam("InputFile", "./tacsatmini.csv"); //put the absolute path to the input file
		config.setParam("npoints", "10");
		config.setParam("interval", "120");
		config.setParam("margin", "10");
		config.setParam("res", "100");
		config.setParam("method", "SL");
		config.setParam("fm", "0.5");
		config.setParam("distscale", "20");
		config.setParam("sigline", "0.2");
		config.setParam("minspeedThr", "2");
		config.setParam("maxspeedThr", "6");
		config.setParam("headingAdjustment", "0");
		config.setParam("equalDist", "true");
		
		//set the scope and the user (optional for this test)
		config.setGcubeScope( "/gcube/devsec/devVRE");
		config.setParam("ServiceUserName", "test.user");
		
		//set the name of the algorithm to call, as is is in the transducerer.properties file
		config.setAgent("SGVM_INTERPOLATION");
			
		//recall the transducerer with the above name 
		List<ComputationalAgent> transducers = TransducerersFactory.getTransducerers(config);
		ComputationalAgent transducer =transducers.get(0);
		//init the transducerer
		transducer.init();
		//start the process
		Regressor.process(transducer);
		//retrieve the output
		StatisticalType st = transducer.getOutput();
		System.out.println("st:"+((PrimitiveType)st).getContent());
	}
	
}

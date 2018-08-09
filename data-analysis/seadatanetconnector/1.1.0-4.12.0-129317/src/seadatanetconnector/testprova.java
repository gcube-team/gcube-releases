package seadatanetconnector;

import java.io.File;
import java.util.List;

import org.gcube.contentmanagement.lexicalmatcher.utils.FileTools;
import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;
import org.gcube.dataanalysis.ecoengine.datatypes.PrimitiveType;
import org.gcube.dataanalysis.ecoengine.datatypes.StatisticalType;
import org.gcube.dataanalysis.ecoengine.interfaces.ComputationalAgent;
import org.gcube.dataanalysis.ecoengine.interfaces.StandardLocalExternalAlgorithm;
import org.gcube.dataanalysis.ecoengine.processing.factories.TransducerersFactory;
import org.gcube.dataanalysis.ecoengine.test.regression.Regressor;


public class testprova {

	 
	 
	public static void main(String[] args) throws Exception {
	System.out.println("TEST 1");
	List<ComputationalAgent> trans = null;
	
	trans = TransducerersFactory.getTransducerers(testConfigLocal());
	ComputationalAgent prova = trans.get(0);
	prova.init();
	prova.compute();
	StatisticalType output = prova.getOutput();
	System.out.println(output.getDescription());
	System.out.println(output.getClass());
	if (output instanceof PrimitiveType){
		PrimitiveType ptype = (PrimitiveType) output;
		File file = (File)ptype.getContent();
		
		System.out.println(file.getAbsolutePath());
		System.out.println(FileTools.loadString(file.getAbsolutePath(), "UTF-8"));
	}
	trans = null;
	}
	 
	private static AlgorithmConfiguration testConfigLocal() {
	 
	 AlgorithmConfiguration config = Regressor.getConfig();
	 config.setAgent("PROVA");
	 
	 config.setParam("fileName", "test.txt");
	 config.setParam("fileName", "test2.txt");
	 
	return config;
	}
	 
	}


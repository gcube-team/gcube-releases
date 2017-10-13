package org.gcube.dataanalysis.seadatanet.test;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.List;

import org.gcube.contentmanagement.lexicalmatcher.utils.FileTools;
import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;
import org.gcube.dataanalysis.ecoengine.datatypes.PrimitiveType;
import org.gcube.dataanalysis.ecoengine.datatypes.StatisticalType;
import org.gcube.dataanalysis.ecoengine.interfaces.ComputationalAgent;
import org.gcube.dataanalysis.ecoengine.processing.factories.TransducerersFactory;
import org.gcube.dataanalysis.ecoengine.test.regression.Regressor;

public class TestSeaDataNetConnector {


	 
	 
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
		LinkedHashMap<String, StatisticalType> map = (LinkedHashMap<String, StatisticalType>)ptype.getContent();
		
		System.out.println(map);
		//System.out.println(FileTools.loadString(file.getAbsolutePath(), "UTF-8"));
	}
	trans = null;
	}
	 
	private static AlgorithmConfiguration testConfigLocal() {
	 
	 AlgorithmConfiguration config = Regressor.getConfig();
	 config.setAgent("SEADATANET_CONNECTOR");
	 
	 config.setParam("InputTable", "hcaf_d_mini");
	 config.setParam("Longitude", "centerlong");
	 config.setParam("Latitude", "centerlat");
	 config.setParam("Quantity", "landdist");
	 
	 config.setParam("LongitudeMinValue", "-180");
	 config.setParam("LongitudeMaxValue", "180");
	 config.setParam("LongitudeResolution", "3");
	 
	 config.setParam("LatitudeMinValue", "-80");
	 config.setParam("LatitudeMaxValue", "80");
	 config.setParam("LatitudeResolution", "3");
	 
	 config.setParam("CorrelationLength", "10");
	 config.setParam("SignalNoise", "0.8");
	 config.setParam("DepthLevel", "1");
	 
	 config.setParam("DatabaseUserName","utente");
	 config.setParam("DatabasePassword","d4science");
	 config.setParam("DatabaseURL","jdbc:postgresql://statistical-manager.d.d4science.org/testdb");
	 config.setParam("DatabaseDriver","org.postgresql.Driver");
		
	return config;
	}
}

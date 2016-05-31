package org.gcube.dataanalysis.geo.wps.test.regression;

import java.util.HashMap;

import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;
import org.gcube.dataanalysis.ecoengine.datatypes.PrimitiveType;
import org.gcube.dataanalysis.ecoengine.datatypes.StatisticalType;
import org.gcube.dataanalysis.ecoengine.interfaces.ComputationalAgent;
import org.gcube.dataanalysis.ecoengine.test.regression.Regressor;
import org.gcube.dataanalysis.geo.wps.interfaces.WPSProcess;

public class RegressionStatisticalWPSProcess {

	public static void main(String[] args) throws Exception {

		System.out.println("TEST 1");
		
		ComputationalAgent trans = new WPSProcess("http://146.48.87.227:8080/wps/WebProcessingService","org.gcube.dataanalysis.statistical_manager_wps_algorithms.generated.Bionym_local");
		trans.setConfiguration(testConfig());
		trans.init();
		Regressor.process(trans);
		StatisticalType st = trans.getOutput();
		AnalysisLogger.getLogger().debug("ST:"+st);
		PrimitiveType p = (PrimitiveType)st;
		HashMap map = (HashMap) p.getContent();
		for (Object v:map.values())
			System.out.println("Out: "+((PrimitiveType)v).getContent());
		trans = null;
	}

	
	private static AlgorithmConfiguration testConfig() {

		AlgorithmConfiguration config = Regressor.getConfig();
		config.setParam("username", "gadus.morhua");
		config.setParam("SpeciesAuthorName", "Gadus morhua (Linnaeus, 1758)");
		config.setParam("Taxa_Authority_File", "FISHBASE");
		config.setParam("Parser_Name", "SIMPLE");
		config.setParam("Activate_Preparsing_Processing", "true");
		config.setParam("Use_Stemmed_Genus_and_Species", "false");
		config.setParam("Accuracy_vs_Speed", "false");
		config.setParam("Matcher_1", "LEVENSHTEIN");
		config.setParam("Threshold_1", "0.6");
		config.setParam("MaxResults_1", "10");
		
		return config;
	}
	
	
	
	
	
}

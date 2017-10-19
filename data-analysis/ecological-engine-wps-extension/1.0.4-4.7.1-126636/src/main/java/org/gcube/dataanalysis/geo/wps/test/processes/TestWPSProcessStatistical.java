package org.gcube.dataanalysis.geo.wps.test.processes;

import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;
import org.gcube.dataanalysis.ecoengine.datatypes.StatisticalType;
import org.gcube.dataanalysis.ecoengine.interfaces.ComputationalAgent;
import org.gcube.dataanalysis.ecoengine.test.regression.Regressor;
import org.gcube.dataanalysis.geo.wps.interfaces.WPSProcess;

public class TestWPSProcessStatistical {

	static String[] algorithms = { "org.gcube.dataanalysis.statistical_manager_wps_algorithms.generated.Bionym_local"};
	
//	static String wps = "http://146.48.87.227:8080/wps/WebProcessingService";
	static String wps = "http://wps.statistical.d4science.org/wps/WebProcessingService";
	
	static AlgorithmConfiguration[] configs = { testBionymLocal()};

	public static void main(String[] args) throws Exception {

		System.out.println("TEST 1");

		for (int i = 0; i < algorithms.length; i++) {
			AnalysisLogger.getLogger().debug("Executing:" + algorithms[i]);
			
			ComputationalAgent trans = new WPSProcess(wps, algorithms[i]);

			trans.setConfiguration(configs[i]);
			trans.init();
			Regressor.process(trans);
			StatisticalType st = trans.getOutput();
			AnalysisLogger.getLogger().debug("ST:" + st);
			trans = null;
		}
	}

		private static AlgorithmConfiguration testBionymLocal() {
		// dataInputs=geoColumn=field0;quantityColumn=field4;sourceAreaLayerName=FAO_AREAS;targetAreaLayerName=EEZ_HIGHSEAS;dataUrls=https://dl.dropboxusercontent.com/u/24368142/timeseries_100.json;&ResponseDocument=result

		AlgorithmConfiguration config = Regressor.getConfig();
		config.setParam("username", "captain.buccaneer");
		config.setParam("SpeciesAuthorName", "Gadus morhua (Linnaeus, 1758)");
		config.setParam("Taxa_Authority_File", "FISHBASE");
		config.setParam("Parser_Name", "SIMPLE");
		config.setParam("Activate_Preparsing_Processing", "true");
		config.setParam("Use_Stemmed_Genus_and_Species", "false");
		config.setParam("Accuracy_vs_Speed", "MAX_ACCURACY");
		config.setParam("Matcher_1", "LEVENSHTEIN");
		config.setParam("Threshold_1", "0.7");
		config.setParam("MaxResults_1", "10");
		return config;
		}
	
}

package org.gcube.dataanalysis.ecoengine.test.experiments.latimeria;

import java.util.HashMap;
import java.util.List;

import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;
import org.gcube.dataanalysis.ecoengine.datatypes.PrimitiveType;
import org.gcube.dataanalysis.ecoengine.evaluation.DiscrepancyAnalysis;
import org.gcube.dataanalysis.ecoengine.interfaces.ComputationalAgent;
import org.gcube.dataanalysis.ecoengine.processing.factories.EvaluatorsFactory;
import org.gcube.dataanalysis.ecoengine.test.PresetConfigGenerator;
import org.gcube.dataanalysis.ecoengine.test.regression.Regressor;

public class TablesComparisonForLatimeria {

	static String absenceRandomTable = "absence_data_latimeria_random";
	static String absenceStaticTable = "absence_data_latimeria";
	static String presenceTable = "presence_data_latimeria_2";
	static String presenceTableNoEarth = "presence_data_latimeria_sea";
	static String envelopeTable = "hspen_latimeria";

	static String aquamapsSuitableTable = "hspec_suitable_latimeria_chalumnae";
	static String aquamapsNativeTable = "hspec_native_latimeria_chalumnae";
	static String nnsuitableTable = "hspec_suitable_neural_latimeria_chalumnae";
	static String nnsuitableRandomTable = "hspec_suitable_neural_latimeria_chalumnae_random";
	static String nnnativeTable = "hspec_native_neural_latimeria_chalumnae";
	static String nnnativeRandomTable = "hspec_native_neural_latimeria_chalumnae_random";
	static String hcaf = "hcaf_d";
	static String filteredhcaf = "bboxed_hcaf_d";

	static String speciesID = "Fis-30189";
	static String staticsuitable = "staticsuitable";
	static String randomsuitable = "randomsuitable";
	static String staticnative = "staticnative";
	static String randomnative = "randomnative";
	static int numberOfPoints = 34;

	static String nnname = "neuralname";
	static float x1 = 95.346678f;
	static float y1 = -9.18887f;
	static float x2 = 125.668944f;
	static float y2 = 12.983148f;

	public static AlgorithmConfiguration configDiscrepancyAnalysis(String table1, String table2) {

		AlgorithmConfiguration config = getConfig();
		config.setNumberOfResources(1);
		config.setAgent("DISCREPANCY_ANALYSIS");
		config.setParam("FirstTable", table1);
		config.setParam("SecondTable", table2);
		config.setParam("FirstTableCsquareColumn", "csquarecode");
		config.setParam("SecondTableCsquareColumn", "csquarecode");
		config.setParam("FirstTableProbabilityColumn", "probability");
		config.setParam("SecondTableProbabilityColumn", "probability");
		config.setParam("ComparisonThreshold", "0.1");

		return config;
	}

	public static AlgorithmConfiguration getConfig() {

		AlgorithmConfiguration config = new AlgorithmConfiguration();

		config.setConfigPath("./cfg/");
		config.setPersistencePath("./");
		config.setParam("DatabaseUserName", "postgres");
		config.setParam("DatabasePassword", "d4science2");
		config.setParam("DatabaseURL", "jdbc:postgresql://geoserver-dev.d4science-ii.research-infrastructures.eu/aquamapsdb");
		config.setParam("DatabaseDriver", "org.postgresql.Driver");
		AnalysisLogger.setLogger(config.getConfigPath() + AlgorithmConfiguration.defaultLoggerFile);
		return config;
	}

	public static void calcdiscrepancy(String table1, String table2) throws Exception {
		System.out.println("*****************************DISCREPANCY: " + table1 + " vs " + table2 + "************************************");
		List<ComputationalAgent> evaluators = null;
		evaluators = EvaluatorsFactory.getEvaluators(configDiscrepancyAnalysis(table1, table2));
		evaluators.get(0).init();
		Regressor.process(evaluators.get(0));
		PrimitiveType output = (PrimitiveType) evaluators.get(0).getOutput();
		HashMap<String, Object> out = (HashMap<String, Object>) output.getContent();
		DiscrepancyAnalysis.visualizeResults(out);
		evaluators = null;
	}

	public static void calcquality(String table, String presenceTable, String absenceTable) throws Exception {
		System.out.println("*****************************QUALITY: " + table + " vs " + presenceTable + " and " + absenceTable + "************************************");
		List<ComputationalAgent> evaluators = null;
		evaluators = EvaluatorsFactory.getEvaluators(PresetConfigGenerator.configQualityAnalysis(presenceTable, absenceTable, table));
		evaluators.get(0).init();
		Regressor.process(evaluators.get(0));
		PrimitiveType output = (PrimitiveType) evaluators.get(0).getOutput();
		HashMap<String, Object> out = (HashMap<String, Object>) output.getContent();
		DiscrepancyAnalysis.visualizeResults(out);
		evaluators = null;
	}

	public static void main(String[] args) throws Exception {
//		calcdiscrepancy(aquamapsSuitableTable, nnsuitableTable);
		calcdiscrepancy(aquamapsNativeTable, nnnativeTable);
	}

}

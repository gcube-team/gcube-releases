package org.gcube.dataanalysis.ecoengine.test.regression;

import java.util.List;

import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;
import org.gcube.dataanalysis.ecoengine.interfaces.ComputationalAgent;
import org.gcube.dataanalysis.ecoengine.processing.factories.EvaluatorsFactory;

public class RegressionTestEvaluators {
	/**
	 * example of parallel processing on a single machine the procedure will generate a new table for a distribution on suitable species
	 * 
	 */

	public static void main(String[] args) throws Exception {
		AnalysisLogger.setLogger("./cfg/ALog.properties");
		List<ComputationalAgent>  evaluators = null;
		/*
		List<ComputationalAgent>  evaluators = EvaluatorsFactory.getEvaluators(testConfig1());
		evaluators.get(0).init();
		Regressor.process(evaluators.get(0));
		evaluators = null;
		*/
		System.out.println("\n**********-------************\n");
		
		//test Discrepancy
		evaluators = EvaluatorsFactory.getEvaluators(testMapsComparison());
		evaluators.get(0).init();
		Regressor.process(evaluators.get(0));
		evaluators = null;
		

	}

	private static AlgorithmConfiguration testConfig1() {

		AlgorithmConfiguration config = Regressor.getConfig();
		config.setNumberOfResources(1);
		config.setAgent("DISCREPANCY_ANALYSIS");
		config.setParam("FirstTable", "hspec_native_baskingshark_aquamaps");
		config.setParam("SecondTable", "hspec_suitable_nn_Fis22747");
		config.setParam("FirstTableCsquareColumn", "csquarecode");
		config.setParam("SecondTableCsquareColumn", "csquarecode");
		config.setParam("FirstTableProbabilityColumn", "probability");
		config.setParam("SecondTableProbabilityColumn", "probability");
		config.setParam("ComparisonThreshold", "0.1");

		return config;
	}

	private static AlgorithmConfiguration testConfig2() {

		AlgorithmConfiguration config = Regressor.getConfig();
		config.setNumberOfResources(1);
		config.setNumberOfResources(1);
		config.setAgent("QUALITY_ANALYSIS");

		config.setParam("PositiveCasesTable", "presence_data_baskingshark");
		config.setParam("NegativeCasesTable", "absence_data_baskingshark2");
		config.setParam("PositiveCasesTableKeyColumn", "csquarecode");
		config.setParam("NegativeCasesTableKeyColumn", "csquarecode");
		config.setParam("DistributionTable", "hspec_native_baskingshark_aquamaps");
		config.setParam("DistributionTableKeyColumn", "csquarecode");
		config.setParam("DistributionTableProbabilityColumn", "probability");
		config.setParam("PositiveThreshold", "0.5");
		config.setParam("NegativeThreshold", "0.5");
		return config;

	}

	private static AlgorithmConfiguration testMapsComparison() {

		AlgorithmConfiguration config = Regressor.getConfig();
		config.setNumberOfResources(1);
		
		config.setAgent("DISCREPANCY_ANALYSIS");
		config.setParam("DatabaseUserName","utente");
		config.setParam("DatabasePassword","d4science");
		config.setParam("DatabaseURL","jdbc:postgresql://statistical-manager.d.d4science.org/testdb");
		config.setParam("DatabaseDriver","org.postgresql.Driver");
		config.setNumberOfResources(1);
		
		config.setParam("FirstTable", "rstr280e0453e8c7408c96edd49a8dcb5986");
		config.setParam("SecondTable", "rstr11b3b436ddaf4ae5ae5227ea8e0658ba");
		config.setParam("FirstTableCsquareColumn", "csquarecode");
		config.setParam("SecondTableCsquareColumn", "csquarecode");
		config.setParam("FirstTableProbabilityColumn", "f_probability");
		config.setParam("SecondTableProbabilityColumn", "f_probability");
		config.setParam("ComparisonThreshold", "0.5");
		config.setParam("KThreshold", "0.5");
		config.setParam("MaxSamples", "45000");
		
		
		return config;

	}
		

}

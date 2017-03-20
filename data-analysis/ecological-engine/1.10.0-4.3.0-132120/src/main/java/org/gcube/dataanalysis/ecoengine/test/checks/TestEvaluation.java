package org.gcube.dataanalysis.ecoengine.test.checks;

import java.util.HashMap;
import java.util.List;

import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;
import org.gcube.dataanalysis.ecoengine.datatypes.PrimitiveType;
import org.gcube.dataanalysis.ecoengine.evaluation.DiscrepancyAnalysis;
import org.gcube.dataanalysis.ecoengine.interfaces.ComputationalAgent;
import org.gcube.dataanalysis.ecoengine.interfaces.Evaluator;
import org.gcube.dataanalysis.ecoengine.processing.factories.EvaluatorsFactory;

public class TestEvaluation {
	/**
	 * example of parallel processing on a single machine the procedure will generate a new table for a distribution on suitable species
	 * 
	 */

public static void main(String[] args) throws Exception {
		
		//test Quality
		List<ComputationalAgent>  evaluators = EvaluatorsFactory.getEvaluators(testQuality());
		evaluate(evaluators.get(0),testQuality());
		evaluators = null;
		
		System.out.println("\n**********-------************\n");
		
		//test Discrepancy
		evaluators = EvaluatorsFactory.getEvaluators(testDiscrepancy());
		evaluate(evaluators.get(0),testDiscrepancy());
		evaluators = null;
		
}

	
	private static void evaluate(ComputationalAgent evaluator, AlgorithmConfiguration config) throws Exception {

		if (evaluator != null) {
			TestEvaluation tgs = new TestEvaluation();
			ThreadCalculator tc = tgs.new ThreadCalculator(evaluator,config);
			Thread t = new Thread(tc);
			t.start();
			while (evaluator.getStatus() < 100) {

				String resLoad = evaluator.getResourceLoad();
				String ress = evaluator.getResources();
				
				System.out.println("LOAD: " + resLoad);
				System.out.println("RESOURCES: " + ress);
				
				System.out.println("STATUS: " + evaluator.getStatus());
				Thread.sleep(1000);
			}
			
			String resLoad = evaluator.getResourceLoad();
			String ress = evaluator.getResources();
			System.out.println("\nLOAD: " + resLoad);
			System.out.println("RESOURCES: " + ress);
			System.out.println("STATUS: " + evaluator.getStatus());
			
		} else
			AnalysisLogger.getLogger().trace("Generator Algorithm Not Supported");

	}

	public class ThreadCalculator implements Runnable {
		ComputationalAgent dg;
		AlgorithmConfiguration config;
		
		public ThreadCalculator(ComputationalAgent dg, AlgorithmConfiguration config) {
			this.dg = dg;
			this.config = config;
		}

		public void run() {
			try {
				dg.compute();
				PrimitiveType output = (PrimitiveType) dg.getOutput();
				HashMap<String, Object> out = (HashMap<String, Object>)output.getContent();
				DiscrepancyAnalysis.visualizeResults(out);
				
			} catch (Exception e) {
			}
		}

	}
	

	private static AlgorithmConfiguration testQuality() {
		
		AlgorithmConfiguration config = new AlgorithmConfiguration();
		config.setConfigPath("./cfg/");
		config.setPersistencePath("./cfg/");
		config.setNumberOfResources(1);
		config.setAgent("QUALITY_ANALYSIS");
		
		config.setParam("PositiveCasesTable","presence_data_baskingshark");
		config.setParam("NegativeCasesTable","absence_data_baskingshark2");
		
		config.setParam("PositiveCasesTableKeyColumn","csquarecode");
		config.setParam("NegativeCasesTableKeyColumn","csquarecode");
		
		config.setParam("DistributionTable","hspec_native_baskingshark_aquamaps");
		config.setParam("DistributionTableKeyColumn","csquarecode");
		config.setParam("DistributionTableProbabilityColumn","probability");
		
		config.setParam("PositiveThreshold","0.5");
		config.setParam("NegativeThreshold","0.5");
		
		
		return config;
		
		}
	
	
	private static AlgorithmConfiguration testDiscrepancy() {
		
		AlgorithmConfiguration config = new AlgorithmConfiguration();
		
		config.setConfigPath("./cfg/");
		config.setPersistencePath("./cfg/");
		config.setNumberOfResources(1);
		config.setAgent("DISCREPANCY_ANALYSIS");
		
		config.setParam("FirstTable","hspec_native_baskingshark_aquamaps");
		config.setParam("SecondTable","hspec_suitable_nn_Fis22747");
		
		config.setParam("FirstTableCsquareColumn","csquarecode");
		config.setParam("SecondTableCsquareColumn","csquarecode");
		
		config.setParam("FirstTableProbabilityColumn","probability");
		config.setParam("SecondTableProbabilityColumn","probability");
		
		config.setParam("ComparisonThreshold","0.1");
		
		
		return config;
		
		}
	
}

package org.gcube.dataanalysis.ecoengine.user;

import java.util.HashMap;
import java.util.List;

import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;
import org.gcube.dataanalysis.ecoengine.datatypes.PrimitiveType;
import org.gcube.dataanalysis.ecoengine.evaluation.DiscrepancyAnalysis;
import org.gcube.dataanalysis.ecoengine.interfaces.ComputationalAgent;
import org.gcube.dataanalysis.ecoengine.interfaces.Evaluator;
import org.gcube.dataanalysis.ecoengine.processing.factories.EvaluatorsFactory;

public class EvaluatorT implements Runnable{
	
	ComputationalAgent dg;
	AlgorithmConfiguration config;
	
	
	public void run() {
		try {
			dg.compute();
			PrimitiveType output = (PrimitiveType) dg.getOutput(); 
			HashMap<String, Object> out = (HashMap<String, Object>)output.getContent();
			DiscrepancyAnalysis.visualizeResults(out);
			
		} catch (Exception e) {
		}
	}
	
	public EvaluatorT(ComputationalAgent dg, AlgorithmConfiguration config) {
		this.dg = dg;
		this.config = config;
	}
	
	public static void evaluate(AlgorithmConfiguration config) throws Exception {

		List<ComputationalAgent>  evaluators = EvaluatorsFactory.getEvaluators(config);
		ComputationalAgent evaluator = evaluators.get(0);
		
		if (evaluator != null) {
			EvaluatorT tc = new EvaluatorT(evaluator,config);
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
			System.out.println("Generator Algorithm Not Supported");

	}

		
		
	public static AlgorithmConfiguration getEvaluationConfiguration(String configPath,String operationName,String positiveCasesTable,String negativeCasesTable,String positiveCasesTableKeyColumn,
		String negativeCasesKeyColumn, String finalDistributionTable,String finalDistributionTableKeyColumn,String finalDistributionTableProbabilityColumn,String positiveThreshold,String negativeThreshold) {
		
		AlgorithmConfiguration config = new AlgorithmConfiguration();
		config.setConfigPath(configPath);
		config.setPersistencePath(configPath);
		config.setNumberOfResources(1);
		config.setAgent(operationName);
		
		config.setParam("PositiveCasesTable",positiveCasesTable);
		config.setParam("NegativeCasesTable",negativeCasesTable);
		
		config.setParam("PositiveCasesTableKeyColumn",positiveCasesTableKeyColumn);
		config.setParam("NegativeCasesTableKeyColumn",negativeCasesKeyColumn);
		
		config.setParam("DistributionTable",finalDistributionTable);
		config.setParam("DistributionTableKeyColumn",finalDistributionTableKeyColumn);
		config.setParam("DistributionTableProbabilityColumn",finalDistributionTableProbabilityColumn);
		
		config.setParam("PositiveThreshold",positiveThreshold);
		config.setParam("NegativeThreshold",negativeThreshold);
		
		
		return config;
		
		}

	public static AlgorithmConfiguration getDiscrepancyConfiguration(String operationName, String firstTable, String secondTable, String firstTableCsquareColumn, 
			String secondTableCsquareColumn, String firstTableProbabilityColumn, String secondTableProbabilityColumn, String comparisonThreshold, String configPath) {

		AlgorithmConfiguration config = new AlgorithmConfiguration();
			config.setConfigPath(configPath);
			config.setPersistencePath(configPath);
			config.setNumberOfResources(1);
			config.setAgent(operationName);
		
			config.setParam("FirstTable",firstTable);
			config.setParam("SecondTable",secondTable);
			config.setParam("FirstTableCsquareColumn",firstTableCsquareColumn);
			config.setParam("SecondTableCsquareColumn",secondTableCsquareColumn);
			config.setParam("FirstTableProbabilityColumn",firstTableProbabilityColumn);
			config.setParam("SecondTableProbabilityColumn",secondTableProbabilityColumn);
			config.setParam("ComparisonThreshold",comparisonThreshold);
			
			return config;
			
			}

	

}

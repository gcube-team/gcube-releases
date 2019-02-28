package org.gcube.dataanalysis.ecoengine.processing.factories;

import java.util.ArrayList;
import java.util.List;

import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;
import org.gcube.dataanalysis.ecoengine.datatypes.StatisticalType;
import org.gcube.dataanalysis.ecoengine.interfaces.ComputationalAgent;
import org.gcube.dataanalysis.ecoengine.interfaces.Evaluator;

public class EvaluatorsFactory {

	
	public static Evaluator getEvaluator(AlgorithmConfiguration config) throws Exception {
		Evaluator evaler = (Evaluator) ProcessorsFactory.getProcessor(config, config.getConfigPath() + AlgorithmConfiguration.evaluatorsFile, config.getAlgorithmClassLoader());
		return evaler;
	}
	
	public static List<String> getAllEvaluators(String configPath) throws Exception{
		List<String> evaluators = ProcessorsFactory.getClasses(configPath + AlgorithmConfiguration.evaluatorsFile);
		return evaluators;
	}
	
	public static List<StatisticalType> getEvaluatorParameters(String configPath, String algorithmName, AlgorithmConfiguration config) throws Exception{
		List<StatisticalType> inputs = ProcessorsFactory.getParameters(configPath + AlgorithmConfiguration.evaluatorsFile, algorithmName, config.getAlgorithmClassLoader());
		return inputs;
	}

	public static StatisticalType getEvaluatorOutput(String configPath, String algorithmName, AlgorithmConfiguration config) throws Exception {
		StatisticalType output = ProcessorsFactory.getOutputDescriptions(configPath + AlgorithmConfiguration.evaluatorsFile, algorithmName, config.getAlgorithmClassLoader());
		return output;
	}
	
	public static String getDescription(String configPath, String algorithmName, AlgorithmConfiguration config) throws Exception{
		String input = ProcessorsFactory.getDescription(configPath + AlgorithmConfiguration.evaluatorsFile, algorithmName, config.getAlgorithmClassLoader());
		return input;
		}
	
	public static List<ComputationalAgent> getEvaluators(AlgorithmConfiguration config) throws Exception {
		List<ComputationalAgent> evaluators = new ArrayList<ComputationalAgent>();
		evaluators.add(getEvaluator(config));
		ProcessorsFactory.addAgent2List(evaluators,GeneratorsFactory.getGenerator(config));
		return evaluators;
	}
	
	
}

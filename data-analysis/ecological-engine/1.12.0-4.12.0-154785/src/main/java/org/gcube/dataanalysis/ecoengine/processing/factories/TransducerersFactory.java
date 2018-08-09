package org.gcube.dataanalysis.ecoengine.processing.factories;

import java.util.ArrayList;
import java.util.List;

import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;
import org.gcube.dataanalysis.ecoengine.datatypes.StatisticalType;
import org.gcube.dataanalysis.ecoengine.interfaces.ComputationalAgent;
import org.gcube.dataanalysis.ecoengine.interfaces.Transducerer;

public class TransducerersFactory {

	public TransducerersFactory() {

	}

	public static Transducerer getTransducerer(AlgorithmConfiguration config) throws Exception {
		ComputationalAgent ca = ProcessorsFactory.getProcessor(config, config.getConfigPath() + AlgorithmConfiguration.transducererFile, config.getAlgorithmClassLoader());
		if (ca != null){
			ca.setConfiguration(config);
			return (Transducerer) ca;
		}
		else
			return DynamicTransducerersFactory.getTransducerer(config);
	}

	public static List<String> getAllTransducerers(AlgorithmConfiguration config) throws Exception {
		List<String> trans = ProcessorsFactory.getClasses(config.getConfigPath() + AlgorithmConfiguration.transducererFile);
		List<String> dtrans = DynamicTransducerersFactory.getTransducerersNames(config);
		trans.addAll(dtrans);
		return trans;
	}

	public static List<StatisticalType> getTransducerParameters(AlgorithmConfiguration config, String algorithmName) throws Exception {
		List<StatisticalType> inputs = ProcessorsFactory.getParameters(config.getConfigPath() + AlgorithmConfiguration.transducererFile, algorithmName, config.getAlgorithmClassLoader());
		if (inputs != null)
			return inputs;
		else {
			config.setAgent(algorithmName);
			inputs = DynamicTransducerersFactory.getTransducerer(config).getInputParameters();
			AnalysisLogger.getLogger().debug("Dynamic INPUTS:"+inputs);
			return inputs;
		}
	}

	
	public static StatisticalType getTransducerOutput(AlgorithmConfiguration config, String algorithmName) throws Exception {
		StatisticalType output = ProcessorsFactory.getOutputDescriptions(config.getConfigPath()+ AlgorithmConfiguration.transducererFile, algorithmName, config.getAlgorithmClassLoader());
		if (output != null)
			return output;
		else {
			config.setAgent(algorithmName);
			output = DynamicTransducerersFactory.getTransducerer(config).getOutput();
			AnalysisLogger.getLogger().debug("Dynamic Output:"+output);
			return output;
		}
	}
	
	public static StatisticalType getModelOutput(String configPath, String algorithmName, AlgorithmConfiguration config) throws Exception {
		StatisticalType output = ProcessorsFactory.getOutputDescriptions(configPath + AlgorithmConfiguration.modelsFile, algorithmName, config.getAlgorithmClassLoader());
		return output;
	}
	
	
	public static String getDescription(AlgorithmConfiguration config, String algorithmName) throws Exception {
		String input = ProcessorsFactory.getDescription(config.getConfigPath()+ AlgorithmConfiguration.transducererFile, algorithmName, config.getAlgorithmClassLoader());
		if (input!=null)
			return input;
		else{
			config.setAgent(algorithmName);
			input = DynamicTransducerersFactory.getTransducerer(config).getDescription();
			AnalysisLogger.getLogger().debug("Dynamic DESCRIPTION:"+input);
			return input;
		}
	}

	public static List<ComputationalAgent> getTransducerers(AlgorithmConfiguration config) throws Exception {
		List<ComputationalAgent> trans = new ArrayList<ComputationalAgent>();
		trans.add(getTransducerer(config));
		ProcessorsFactory.addAgent2List(trans, GeneratorsFactory.getGenerator(config));
		Transducerer dynamicTransducer = DynamicTransducerersFactory.getTransducerer(config);
		if (dynamicTransducer!=null)
			trans.add(dynamicTransducer);
		return trans;
	}

}

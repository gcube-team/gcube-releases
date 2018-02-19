package org.gcube.dataanalysis.ecoengine.processing.factories;

import java.util.ArrayList;
import java.util.List;

import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;
import org.gcube.dataanalysis.ecoengine.datatypes.StatisticalType;
import org.gcube.dataanalysis.ecoengine.interfaces.Clusterer;
import org.gcube.dataanalysis.ecoengine.interfaces.ComputationalAgent;

public class ClusterersFactory {

	public static Clusterer getClusterer(AlgorithmConfiguration config) throws Exception {
		Clusterer clus = (Clusterer) ProcessorsFactory.getProcessor(config, config.getConfigPath() + AlgorithmConfiguration.clusterersFile, config.getAlgorithmClassLoader());
		return clus;
	}

	public static List<String> getAllClusterers(String configPath) throws Exception {
		List<String> cluss = ProcessorsFactory.getClasses(configPath + AlgorithmConfiguration.clusterersFile);
		return cluss;
	}

	public static List<StatisticalType> getClustererParameters(String configPath, String algorithmName, AlgorithmConfiguration config) throws Exception {
		List<StatisticalType> inputs = ProcessorsFactory.getParameters(configPath + AlgorithmConfiguration.clusterersFile, algorithmName, config.getAlgorithmClassLoader());
		return inputs;
	}

	public static StatisticalType getClustererOutput(String configPath, String algorithmName, AlgorithmConfiguration config) throws Exception {
		StatisticalType output = ProcessorsFactory.getOutputDescriptions(configPath + AlgorithmConfiguration.clusterersFile, algorithmName, config.getAlgorithmClassLoader());
		return output;
	}
	
	public static String getDescription(String configPath, String algorithmName, AlgorithmConfiguration config) throws Exception{
		String input = ProcessorsFactory.getDescription(configPath + AlgorithmConfiguration.clusterersFile, algorithmName, config.getAlgorithmClassLoader());
		return input;
		}
	
	public static List<ComputationalAgent> getClusterers(AlgorithmConfiguration config) throws Exception {
		List<ComputationalAgent> clusterers = new ArrayList<ComputationalAgent>();
		clusterers.add(getClusterer(config));
		ProcessorsFactory.addAgent2List(clusterers,GeneratorsFactory.getGenerator(config));
		return clusterers;
	}

}

package org.gcube.dataanalysis.ecoengine.processing.factories;

import java.util.ArrayList;
import java.util.List;

import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;
import org.gcube.dataanalysis.ecoengine.datatypes.StatisticalType;
import org.gcube.dataanalysis.ecoengine.interfaces.Clusterer;
import org.gcube.dataanalysis.ecoengine.interfaces.ComputationalAgent;
import org.gcube.dataanalysis.ecoengine.interfaces.Generator;

public class ClusterersFactory {

	public static Clusterer getClusterer(AlgorithmConfiguration config) throws Exception {
		Clusterer clus = (Clusterer) ProcessorsFactory.getProcessor(config, config.getConfigPath() + AlgorithmConfiguration.clusterersFile);
		return clus;
	}

	public static List<String> getAllClusterers(String configPath) throws Exception {
		List<String> cluss = ProcessorsFactory.getClasses(configPath + AlgorithmConfiguration.clusterersFile);
		return cluss;
	}

	public static List<StatisticalType> getClustererParameters(String configPath, String algorithmName) throws Exception {
		List<StatisticalType> inputs = ProcessorsFactory.getParameters(configPath + AlgorithmConfiguration.clusterersFile, algorithmName);
		return inputs;
	}

	public static StatisticalType getClustererOutput(String configPath, String algorithmName) throws Exception {
		StatisticalType output = ProcessorsFactory.getOutputDescriptions(configPath + AlgorithmConfiguration.clusterersFile, algorithmName);
		return output;
	}
	
	public static String getDescription(String configPath, String algorithmName) throws Exception{
		String input = ProcessorsFactory.getDescription(configPath + AlgorithmConfiguration.clusterersFile, algorithmName);
		return input;
		}
	
	public static List<ComputationalAgent> getClusterers(AlgorithmConfiguration config) throws Exception {
		List<ComputationalAgent> clusterers = new ArrayList<ComputationalAgent>();
		clusterers.add(getClusterer(config));
		ProcessorsFactory.addAgent2List(clusterers,GeneratorsFactory.getGenerator(config));
		return clusterers;
	}

}

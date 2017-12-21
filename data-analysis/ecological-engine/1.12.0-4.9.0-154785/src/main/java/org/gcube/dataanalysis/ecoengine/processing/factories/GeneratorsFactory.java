package org.gcube.dataanalysis.ecoengine.processing.factories;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.dataanalysis.ecoengine.configuration.ALG_PROPS;
import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;
import org.gcube.dataanalysis.ecoengine.datatypes.StatisticalType;
import org.gcube.dataanalysis.ecoengine.interfaces.ComputationalAgent;
import org.gcube.dataanalysis.ecoengine.interfaces.Generator;
import org.gcube.dataanalysis.ecoengine.interfaces.GenericAlgorithm;

public class GeneratorsFactory {

	public GeneratorsFactory(){
		
	}
	
	
	public static List<String> getProbabilityDistributionAlgorithms(String configPath) throws Exception{
		List<String> algs = ProcessorsFactory.getClasses(configPath + AlgorithmConfiguration.algorithmsFile);
		return algs;
	}
	
	public static List<String> getAllGenerators(String configPath) throws Exception{
		
		List<String> gens = ProcessorsFactory.getClasses(configPath + AlgorithmConfiguration.generatorsFile);
		return gens;
	}
	
	public static String getDescription(String configPath, String algorithmName, AlgorithmConfiguration config) throws Exception{
		String input = ProcessorsFactory.getDescription(configPath + AlgorithmConfiguration.algorithmsFile, algorithmName, config.getAlgorithmClassLoader());
		return input;
		}
	
	public static List<StatisticalType> getAlgorithmParameters(String configPath, String algorithmName, AlgorithmConfiguration config) throws Exception{
		List<StatisticalType> inputs = ProcessorsFactory.getParameters(configPath + AlgorithmConfiguration.algorithmsFile, algorithmName, config.getAlgorithmClassLoader());
			return inputs;
		}
		
	public static StatisticalType getAlgorithmOutput(String configPath, String algorithmName, AlgorithmConfiguration config) throws Exception {
		StatisticalType output = ProcessorsFactory.getOutputDescriptions(configPath + AlgorithmConfiguration.algorithmsFile, algorithmName, config.getAlgorithmClassLoader());
		return output;
	}
	
	public static Generator getGenerator(AlgorithmConfiguration config) throws Exception {
		return (Generator)ProcessorsFactory.getProcessor(config, config.getConfigPath() + AlgorithmConfiguration.generatorsFile, config.getAlgorithmClassLoader());
	}
	
	
	public static List<ComputationalAgent> getGenerators(AlgorithmConfiguration config) throws Exception {
		
		//modify this class in order to manage generators weight and match algorithm vs generators
		List<ComputationalAgent> generators = new ArrayList<ComputationalAgent>();
		try {
			//initialize the logger
			AnalysisLogger.setLogger(config.getConfigPath() + AlgorithmConfiguration.defaultLoggerFile);
			//take the algorithm
			String algorithm = config.getModel();
			//take the algorithms list
			Properties p = AlgorithmConfiguration.getProperties(config.getConfigPath() + AlgorithmConfiguration.algorithmsFile);
			String algorithmclass = p.getProperty(algorithm);
			Object algclass = Class.forName(algorithmclass, true, config.getAlgorithmClassLoader()).newInstance();
			//if the algorithm is a generator itself then execute it
			if (algclass instanceof Generator){
				Generator g = (Generator) algclass;
				g.setConfiguration(config);
				generators.add(g);
			}
			else	
			{
				GenericAlgorithm sp = (GenericAlgorithm) algclass;
				//take alg's properties
				ALG_PROPS[] algp = sp.getProperties();
				//take all generators
				Properties pg = AlgorithmConfiguration.getProperties(config.getConfigPath() + AlgorithmConfiguration.generatorsFile);
				//investigate on possible suitable generators
				for (Object generatorName:pg.values()){
					Generator gen = (Generator)Class.forName((String)generatorName, true, config.getAlgorithmClassLoader()).newInstance();
					gen.setConfiguration(config);
					ALG_PROPS[] supportedAlgs = gen.getSupportedAlgorithms();
					boolean genSuitable = false;
					for (ALG_PROPS prop:algp){
						for (ALG_PROPS gprop:supportedAlgs){
							if (gprop == prop){
								genSuitable = true;
								break;
							}
						}
					}
					//if suitable generator was found then add it at the right place in the list  
					if (genSuitable){
						gen.setConfiguration(config);
						addGenerator2List(generators,gen);
					}
				}
			}

			return generators;

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	
	
	}
	
	//adds a generator to a sorted generators list
	public static void addGenerator2List(List<ComputationalAgent> generators, Generator generator){
		if (generator == null)
			return;
		int i=0;
		boolean inserted = false;
		for (ComputationalAgent g: generators){
			if (g.getInfrastructure().compareTo(generator.getInfrastructure())>0){
				generators.add(i, generator);
				inserted = true;
				break;
			}
			i++;
		}
		if (!inserted)
			generators.add(generator);
	}

	
	
}

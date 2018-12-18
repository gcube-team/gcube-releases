package org.gcube.dataanalysis.ecoengine.processing.factories;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.dataanalysis.ecoengine.configuration.ALG_PROPS;
import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;
import org.gcube.dataanalysis.ecoengine.datatypes.StatisticalType;
import org.gcube.dataanalysis.ecoengine.interfaces.ComputationalAgent;
import org.gcube.dataanalysis.ecoengine.interfaces.Model;
import org.gcube.dataanalysis.ecoengine.interfaces.Modeler;

public class ModelersFactory {

	
	public static Modeler getModeler(AlgorithmConfiguration config) throws Exception {
		Modeler modlr = (Modeler)ProcessorsFactory.getProcessor(config, config.getConfigPath() + AlgorithmConfiguration.generatorsFile, config.getAlgorithmClassLoader());
		return modlr;
	}
	
	public static List<String> getModels(String configPath) throws Exception{
		List<String> models = ProcessorsFactory.getClasses(configPath + AlgorithmConfiguration.modelsFile);
		return models;
	}
	
	public static List<String> getModelers(String configPath) throws Exception{
		List<String> modelers = ProcessorsFactory.getClasses(configPath + AlgorithmConfiguration.modelersFile);
		return modelers;
	}
	
	public static List<StatisticalType> getModelParameters(String configPath, String algorithmName, AlgorithmConfiguration config) throws Exception{
		List<StatisticalType> inputs = ProcessorsFactory.getParameters(configPath + AlgorithmConfiguration.modelsFile, algorithmName, config.getAlgorithmClassLoader());
		return inputs;
	}

	public static StatisticalType getModelOutput(String configPath, String algorithmName, AlgorithmConfiguration config) throws Exception {
		StatisticalType output = ProcessorsFactory.getOutputDescriptions(configPath + AlgorithmConfiguration.modelsFile, algorithmName, config.getAlgorithmClassLoader());
		return output;
	}
	
	public static String getDescription(String configPath, String algorithmName, AlgorithmConfiguration config) throws Exception{
		String input = ProcessorsFactory.getDescription(configPath + AlgorithmConfiguration.modelsFile, algorithmName, config.getAlgorithmClassLoader());
		return input;
	}
	
	public static List<ComputationalAgent> getModelers(AlgorithmConfiguration config) throws Exception {
		
		//modify this class in order to manage generators weight and match algorithm vs generators
		List<ComputationalAgent> modelers = new ArrayList<ComputationalAgent>();
		try {
			//initialize the logger
			AnalysisLogger.setLogger(config.getConfigPath() + AlgorithmConfiguration.defaultLoggerFile);
			//take the algorithm
			String algorithm = config.getModel();
			//take the algorithms list
			Properties p = AlgorithmConfiguration.getProperties(config.getConfigPath() + AlgorithmConfiguration.modelsFile);
			String algorithmclass = p.getProperty(algorithm);
			Object algclass = Class.forName(algorithmclass,true,  config.getAlgorithmClassLoader()).newInstance();
			//if the algorithm is a generator itself then execute it
			if (algclass instanceof Modeler){
				Modeler g = (Modeler) algclass;
				g.setConfiguration(config);
				modelers.add(g);
			}
			else	
			{
				Model mod = (Model) algclass;
				//take alg's properties
				ALG_PROPS[] algp = mod.getProperties();
				//take all generators
				Properties pg = AlgorithmConfiguration.getProperties(config.getConfigPath() + AlgorithmConfiguration.modelersFile);
				//investigate on possible suitable modelers
				for (Object modelerName:pg.values()){
					Modeler gen = (Modeler)Class.forName((String)modelerName, true,config.getAlgorithmClassLoader()).newInstance();
					gen.setConfiguration(config);
					ALG_PROPS[] supportedAlgs = gen.getSupportedModels();
					boolean genSuitable = false;
					for (ALG_PROPS prop:algp){
						for (ALG_PROPS gprop:supportedAlgs){
							if (gprop == prop){
								genSuitable = true;
								gen.setmodel(mod);
								break;
							}
						}
					}
					//if suitable generator was found then add it at the right place in the list  
					if (genSuitable){
						addModeler2List(modelers,gen);
					}
				}
			}

			return modelers;

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	
	
	}
	
	//adds a generator to a sorted generators list
	public static void addModeler2List(List<ComputationalAgent> modelers, Modeler mod){
		int i=0;
		boolean inserted = false;
		for (ComputationalAgent g: modelers){
			if (g.getInfrastructure().compareTo(mod.getInfrastructure())>0){
				modelers.add(i, mod);
				inserted = true;
				break;
			}
			i++;
		}
		if (!inserted)
			modelers.add(mod);
	}

}

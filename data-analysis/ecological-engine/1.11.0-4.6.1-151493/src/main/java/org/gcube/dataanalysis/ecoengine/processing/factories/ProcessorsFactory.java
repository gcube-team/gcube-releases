package org.gcube.dataanalysis.ecoengine.processing.factories;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Properties;

import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;
import org.gcube.dataanalysis.ecoengine.datatypes.StatisticalType;
import org.gcube.dataanalysis.ecoengine.interfaces.Clusterer;
import org.gcube.dataanalysis.ecoengine.interfaces.ComputationalAgent;
import org.gcube.dataanalysis.ecoengine.interfaces.Evaluator;
import org.gcube.dataanalysis.ecoengine.interfaces.Generator;
import org.gcube.dataanalysis.ecoengine.interfaces.GenericAlgorithm;
import org.gcube.dataanalysis.ecoengine.interfaces.Model;
import org.gcube.dataanalysis.ecoengine.interfaces.Modeler;
import org.gcube.dataanalysis.ecoengine.interfaces.Transducerer;
import org.gcube.dataanalysis.ecoengine.utils.DatabaseFactory;

public class ProcessorsFactory {

	public static List<StatisticalType> getDefaultDatabaseConfiguration(String cfgPath) {
		String databasecfgfile = cfgPath + AlgorithmConfiguration.defaultConnectionFile;
		try {
			return DatabaseFactory.getDefaultDatabaseConfiguration(databasecfgfile);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static List<String> getClasses(String file) throws Exception {

		Properties p = AlgorithmConfiguration.getProperties(file);
		List<String> algs = new ArrayList<String>();
		for (Object algName : p.keySet()) {
			algs.add((String) algName);
		}
		return algs;
	}

	
	public static String getDescription(String file, String algorithmName) throws Exception {

		Properties p = AlgorithmConfiguration.getProperties(file);
		String algorithmclass = p.getProperty(algorithmName);
		if (algorithmclass==null) return null;
		Object algclass = Class.forName(algorithmclass).newInstance();
		// if the algorithm is a generator itself then take it
		if (algclass instanceof Generator) {
			return ((Generator) algclass).getDescription();
		} else if (algclass instanceof Modeler) {
			return ((Modeler) algclass).getDescription();
		} else if (algclass instanceof Model) {
			return ((Model) algclass).getDescription();
		} 
		else if (algclass instanceof Evaluator) {
			return ((Evaluator) algclass).getDescription();
		}
		else if (algclass instanceof Clusterer) {
			return ((Clusterer) algclass).getDescription();
		} 
		else if (algclass instanceof Transducerer) {
			return ((Transducerer) algclass).getDescription();
		} 
		else
			return ((GenericAlgorithm) algclass).getDescription();

	}
	
	public static List<StatisticalType> getParameters(String file, String algorithmName) throws Exception {

		Properties p = AlgorithmConfiguration.getProperties(file);
		String algorithmclass = p.getProperty(algorithmName);
		if (algorithmclass==null) return null;
		Object algclass = Class.forName(algorithmclass).newInstance();
		// if the algorithm is a generator itself then take it
		if (algclass instanceof Generator) {
			return ((Generator) algclass).getInputParameters();
		} else if (algclass instanceof Modeler) {
			return ((Modeler) algclass).getInputParameters();
		} else if (algclass instanceof Model) {
			return ((Model) algclass).getInputParameters();
		} 
		else if (algclass instanceof Evaluator) {
			return ((Evaluator) algclass).getInputParameters();
		}
		else if (algclass instanceof Clusterer) {
			return ((Clusterer) algclass).getInputParameters();
		} 
		else if (algclass instanceof Transducerer) {
			return ((Transducerer) algclass).getInputParameters();
		} 
		else
			return ((GenericAlgorithm) algclass).getInputParameters();

	}

	public static StatisticalType getOutputDescriptions(String file, String algorithmName) {

		try{
		Properties p = AlgorithmConfiguration.getProperties(file);
		String algorithmclass = p.getProperty(algorithmName);
		if (algorithmclass==null) return null;
		Object algclass = Class.forName(algorithmclass).newInstance();
		// if the algorithm is a generator itself then take it
		if (algclass instanceof Generator) {
			return ((Generator) algclass).getOutput();
		} else if (algclass instanceof Modeler) {
			return ((Modeler) algclass).getOutput();
		} else if (algclass instanceof Model) {
			return ((Model) algclass).getOutput();
		} 
		else if (algclass instanceof Evaluator) {
			return ((Evaluator) algclass).getOutput();
		}
		else if (algclass instanceof Clusterer) {
			return ((Clusterer) algclass).getOutput();
		} 
		else if (algclass instanceof Transducerer) {
			return ((Transducerer) algclass).getOutput();
		} 
		else
			return ((GenericAlgorithm) algclass).getOutput();
		}catch (Exception e){
			return null;
		}
	}
	
	public static ComputationalAgent getProcessor(AlgorithmConfiguration config, String file) throws Exception {
		return getProcessor(config, file,null);
	}
	public static ComputationalAgent getProcessor(AlgorithmConfiguration config, String file,String explicitAlgorithm) throws Exception {
		// modify this class in order to take the right generator algorithm
		try {
			// initialize the logger
			AnalysisLogger.setLogger(config.getConfigPath() + AlgorithmConfiguration.defaultLoggerFile);
			// take the algorithm
			String algorithm = explicitAlgorithm;
			if (explicitAlgorithm==null)
				algorithm = config.getAgent();
			if (algorithm == null)
				throw new Exception("PROCESSOR NOT SPECIFIED");
			// take the algorithms list
			Properties p = AlgorithmConfiguration.getProperties(file);
			String algorithmclass = p.getProperty(algorithm);
			if (algorithmclass==null)
				return null;
			Object algclass = Class.forName(algorithmclass).newInstance();
			if (algclass instanceof Generator) {
				Generator g = (Generator) algclass;
				g.setConfiguration(config);
				return g;
			} else if (algclass instanceof Modeler) {
				Modeler m = (Modeler) algclass;
				m.setConfiguration(config);
				return m;
			} 
			else if (algclass instanceof Evaluator) {
				Evaluator m = (Evaluator) algclass;
				m.setConfiguration(config);
				return m;
			}
			else if (algclass instanceof Clusterer) {
				Clusterer m = (Clusterer) algclass;
				AnalysisLogger.getLogger().debug("algoritm is null !!!! "+(m==null));
				m.setConfiguration(config);
					return m;
			}
			else if (algclass instanceof Transducerer) {
				Transducerer m = (Transducerer) algclass;
				m.setConfiguration(config);
					return m;
			}
			else
				return null;

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	public static HashMap<String,List<String>> getAllFeatures(AlgorithmConfiguration config) throws Exception{
		HashMap<String,List<String>> map = new HashMap<String, List<String>>();
		
		map.put("DISTRIBUTIONS", GeneratorsFactory.getProbabilityDistributionAlgorithms(config.getConfigPath()));
		map.put("MODELS", ModelersFactory.getModels(config.getConfigPath()));
		map.put("EVALUATORS", EvaluatorsFactory.getAllEvaluators(config.getConfigPath()));
		map.put("CLUSTERERS", ClusterersFactory.getAllClusterers(config.getConfigPath()));
		map.put("TRANSDUCERS", TransducerersFactory.getAllTransducerers(config));
		
		return map;
	}
	
	public static HashMap<String,List<String>> getAllFeaturesUser(AlgorithmConfiguration config) throws Exception{
		
		BufferedReader br = new BufferedReader(new FileReader(new File(config.getConfigPath(),AlgorithmConfiguration.userperspectiveFile)));
		LinkedHashMap<String,List<String>> map = new LinkedHashMap<String, List<String>>();
		String line = br.readLine();
		while (line!=null){
			int eq = line.indexOf("=");
			String key = line.substring(0,eq);
			String values = line.substring(eq+1);
			String [] algoNames = values.split(",");
			List<String> list = Arrays.asList(algoNames);
			map.put(key, list);
			line = br.readLine();
		}
		
		List<String> externalAlgorithms = DynamicTransducerersFactory.getTransducerersNames(config);
		if (externalAlgorithms!=null && externalAlgorithms.size()>0)
			map.put("EXTERNAL", externalAlgorithms);
		
		br.close();
		return map;
	}
	
	//adds a generator to a sorted generators list
		public static void addAgent2List(List<ComputationalAgent> agents, ComputationalAgent agent){
			if (agent == null)
				return;
			int i=0;
			boolean inserted = false;
			for (ComputationalAgent g: agents){
				if (g.getInfrastructure().compareTo(agent.getInfrastructure())>0){
					agents.add(i, agent);
					inserted = true;
					break;
				}
				i++;
			}
			if (!inserted)
				agents.add(agent);
		}
}

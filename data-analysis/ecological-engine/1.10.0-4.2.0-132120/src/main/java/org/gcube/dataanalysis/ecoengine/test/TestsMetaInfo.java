package org.gcube.dataanalysis.ecoengine.test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;
import org.gcube.dataanalysis.ecoengine.datatypes.StatisticalType;
import org.gcube.dataanalysis.ecoengine.interfaces.ComputationalAgent;
import org.gcube.dataanalysis.ecoengine.interfaces.Generator;
import org.gcube.dataanalysis.ecoengine.processing.factories.ClusterersFactory;
import org.gcube.dataanalysis.ecoengine.processing.factories.EvaluatorsFactory;
import org.gcube.dataanalysis.ecoengine.processing.factories.GeneratorsFactory;
import org.gcube.dataanalysis.ecoengine.processing.factories.ModelersFactory;
import org.gcube.dataanalysis.ecoengine.processing.factories.ProcessorsFactory;
import org.gcube.dataanalysis.ecoengine.processing.factories.TransducerersFactory;

public class TestsMetaInfo {
	/**
	 * example of parallel processing on a single machine the procedure will generate a new table for a distribution on suitable species
	 * 
	 */

public static void main(String[] args) throws Exception {
		
		System.out.println("***TEST 1 - Get Generation Algorithm Parameters***");
		List<StatisticalType> map;
//		List<StatisticalType> map = GeneratorsFactory.getAlgorithmParameters("./cfg/","DUMMY");
//		System.out.println("input for DUMMY algorithm: "+map);
		AlgorithmConfiguration config = new AlgorithmConfiguration();
		config.setConfigPath("./cfg/");
		
		StatisticalType type = GeneratorsFactory.getAlgorithmOutput("./cfg/","AQUAMAPS_SUITABLE");
		System.out.println("output for AQUAMAPS_SUITABLE algorithm: "+type);
		type = ModelersFactory.getModelOutput("./cfg/","HSPEN");
		System.out.println("output for HSPEN algorithm: "+type);
		type = EvaluatorsFactory.getEvaluatorOutput("./cfg/","HRS");
		System.out.println("output for HRS  algorithm: "+type);
		type = ClusterersFactory.getClustererOutput("./cfg/","DBSCAN");
		System.out.println("output for DBSCAN algorithm: "+type);
		type = TransducerersFactory.getTransducerOutput(config,"BIOCLIMATE_HSPEC");
		System.out.println("output for BIOCLIMATE_HSPEC algorithm: "+type);
		
		map = GeneratorsFactory.getAlgorithmParameters("./cfg/","AQUAMAPS_SUITABLE");
		System.out.println("input for AQUAMAPS_SUITABLE algorithm: "+map);
		
		System.out.println("\n***TEST 2 - Get Generator***");
		Generator g = GeneratorsFactory.getGenerator(testConfig());
		System.out.println("Found generator "+g);
		
		System.out.println("\n***TEST 3 - Get All Generation Algorithms ***");
		System.out.println("Algs: "+GeneratorsFactory.getProbabilityDistributionAlgorithms("./cfg/"));
		
		System.out.println("\n***TEST 4 - Get All Generators ***");
		System.out.println("Gens: "+GeneratorsFactory.getAllGenerators("./cfg/"));
		
		System.out.println("\n***TEST 5 - Get All Models to be trained ***");
		System.out.println("Models: "+ModelersFactory.getModels("./cfg/"));
		
		System.out.println("\n***TEST 6 - Get All Modelers ***");
		System.out.println("Models: "+ModelersFactory.getModelers("./cfg/"));
		
		System.out.println("\n***TEST 7- Get Model parameters ***");
		map = ModelersFactory.getModelParameters("./cfg/","HSPEN");
		System.out.println("input for HSPEN algorithm: "+map);

		System.out.println("\n***TEST 8- Get Database Default Parameters***");
		map = ProcessorsFactory.getDefaultDatabaseConfiguration("./cfg/");
		System.out.println("Database Default Values: "+map);
		
		System.out.println("\n***TEST 9- Get All Evaluators ***");
		System.out.println("Database Default Values: "+EvaluatorsFactory.getAllEvaluators("./cfg/"));
		
		System.out.println("\n***TEST 10- Get Evaluator Parameters ***");
		map = EvaluatorsFactory.getEvaluatorParameters("./cfg/","QUALITY_ANALYSIS");
		System.out.println("Database Default Values: "+map);
		
		System.out.println("\n***TEST 11- Get Evaluators with a config***");
		List<ComputationalAgent> eval = EvaluatorsFactory.getEvaluators(testConfigEvaluator());
		System.out.println("Database Default Values: "+eval);

		System.out.println("\n***TEST 12- Get All Supported features***");
		
		HashMap<String,List<String>> features = ProcessorsFactory.getAllFeatures(config);
		System.out.println("Database Default Values: "+features);
		
		System.out.println("\n***TEST 13- Get All Clusterers***");
		System.out.println("All Clusterers: "+ClusterersFactory.getAllClusterers("./cfg/"));
		
		System.out.println("\n***TEST 14- Get Clusterer Parameters ***");
		map = ClusterersFactory.getClustererParameters("./cfg/","DBSCAN");
		System.out.println("Clusterers Params: "+map);
		
		System.out.println("\n***TEST 15- Get Clusterers with a config***");
//		List<Clusterer> clus = ClusterersFactory.getClusterers(testConfigClusterer());
//		System.out.println("Clusterers list: "+clus);
		
		System.out.println("\n***TEST 16- Get All Transducerers***");
		System.out.println("All Transducers: "+TransducerersFactory.getAllTransducerers(config));
		
		System.out.println("\n***TEST 17- Get Transducerers Parameters ***");
		map = TransducerersFactory.getTransducerParameters(config,"BIOCLIMATE_HSPEC");
		System.out.println("Transducerers Params: "+map);
		
		System.out.println("\n***TEST 18- Get Transducerers with a config***");
//		List<Transducerer> trans = TransducerersFactory.getTransducerers(testConfigTrans());
//		System.out.println("Transducerers list: "+trans);
		
		
		System.out.println("\n***TEST 19- Get Agent Description***");
		String desc = ClusterersFactory.getDescription("./cfg/","DBSCAN");
		System.out.println("DESCRIPTION: "+desc);
		
		System.out.println("\n***TEST 20- Get USER perspective***");
		Map m = ProcessorsFactory.getAllFeaturesUser(config);
		System.out.println("USER PERSPECTIVE: "+m);
		
		
		int cores = Runtime.getRuntime().availableProcessors();
		System.out.println("Number of cores: "+cores);
}

	
	private static AlgorithmConfiguration testConfigTrans() {
		AlgorithmConfiguration config = new AlgorithmConfiguration();
		config.setConfigPath("./cfg/");
		config.setPersistencePath("./");
		config.setNumberOfResources(1);
		
		config.setAgent("TESTTRANS");
		
		return config;
	}
	
	private static AlgorithmConfiguration testConfigClusterer() {
		AlgorithmConfiguration config = new AlgorithmConfiguration();
		config.setConfigPath("./cfg/");
		config.setPersistencePath("./");
		config.setNumberOfResources(1);
		
		config.setAgent("DBSCAN");
		
		return config;
	}
	
	private static AlgorithmConfiguration testConfigEvaluator() {
		AlgorithmConfiguration config = new AlgorithmConfiguration();
		config.setConfigPath("./cfg/");
		config.setPersistencePath("./");
		config.setNumberOfResources(2);
		
		config.setAgent("QUALITY_ANALYSIS");
		
		return config;
	}
	
	private static AlgorithmConfiguration testConfig() {
		AlgorithmConfiguration config = new AlgorithmConfiguration();
		config.setConfigPath("./cfg/");
		config.setPersistencePath("./");
		config.setNumberOfResources(2);
		config.setModel("TEST");
		
		config.setAgent("SIMPLE_LOCAL");
		
		return config;
	}
	
}

package org.gcube.dataanalysis.ecoengine.test.checks;

import java.util.List;

import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;
import org.gcube.dataanalysis.ecoengine.datatypes.ColumnTypesList;
import org.gcube.dataanalysis.ecoengine.datatypes.InputTable;
import org.gcube.dataanalysis.ecoengine.datatypes.PrimitiveType;
import org.gcube.dataanalysis.ecoengine.datatypes.ServiceType;
import org.gcube.dataanalysis.ecoengine.datatypes.enumtypes.PrimitiveTypes;
import org.gcube.dataanalysis.ecoengine.datatypes.enumtypes.ServiceParameters;
import org.gcube.dataanalysis.ecoengine.interfaces.ComputationalAgent;
import org.gcube.dataanalysis.ecoengine.interfaces.Generator;
import org.gcube.dataanalysis.ecoengine.interfaces.Modeler;
import org.gcube.dataanalysis.ecoengine.processing.factories.GeneratorsFactory;
import org.gcube.dataanalysis.ecoengine.processing.factories.ModelersFactory;
import org.gcube.dataanalysis.ecoengine.test.regression.Regressor;

public class TestBayesianModels {
	/**
	 * example of parallel processing on a single machine the procedure will generate a new table for a distribution on suitable species
	 * 
	 */

public static void main(String[] args) throws Exception {
		/*
		List<Generator> generators = GeneratorsFactory.getGenerators(testConfigLocal1());
		generators.get(0).init();
		Regressor.process(generators.get(0));
		generators = null;
		*/
		List<ComputationalAgent> generators = ModelersFactory.getModelers(testConfigLocal1());
		generators.get(0).init();
		Regressor.process(generators.get(0));
		generators = null;

	
}

public static void main1(String[] args) throws Exception {
	
	List<ComputationalAgent> generators = GeneratorsFactory.getGenerators(testConfigLocal2());
	generators.get(0).init();
	Regressor.process(generators.get(0));
	generators = null;
}
	
	private static AlgorithmConfiguration testConfigLocal2() {
		
		AlgorithmConfiguration config = Regressor.getConfig();
		config.setPersistencePath("./cfg/");
		config.setNumberOfResources(1);
		config.setModel("FEED_FORWARD_A_N_N_DISTRIBUTION");
		
		config.setParam("FeaturesTable","hcaf_d");
		config.setParam("FeaturesColumnNames","depthmin"+AlgorithmConfiguration.getListSeparator()+
				"depthmean"+AlgorithmConfiguration.getListSeparator()+
				"depthsd"+AlgorithmConfiguration.getListSeparator()+
				"sstanmean"+AlgorithmConfiguration.getListSeparator()+
				"sstansd"+AlgorithmConfiguration.getListSeparator()+
				"sstmnmax"+AlgorithmConfiguration.getListSeparator()+
				"sstmnmin"+AlgorithmConfiguration.getListSeparator()+
				"sstmnrange"+AlgorithmConfiguration.getListSeparator()+
				"sbtanmean"+AlgorithmConfiguration.getListSeparator());
	
		config.setParam("FinalTableLabel","bayesian_1");
		config.setParam("FinalTableName", "bayesian_1");
		config.setParam("GroupingFactor","");
		config.setParam("ModelName","neuralnetwork");
		config.setParam("UserName","gianpaolo.coro");
		
		return config;
	}
	
	

	private static AlgorithmConfiguration testConfigLocal1() {
		
		AlgorithmConfiguration config = Regressor.getConfig();
		config.setPersistencePath("./cfg/");
		config.setNumberOfResources(1);
		config.setModel("FEED_FORWARD_ANN");
		
		config.setParam("TrainingDataSet","hcaf_d");
		config.setParam("TrainingColumns","depthmin"+AlgorithmConfiguration.getListSeparator()+
				"depthmean"+AlgorithmConfiguration.getListSeparator()+
				"depthsd"+AlgorithmConfiguration.getListSeparator()+
				"sstanmean"+AlgorithmConfiguration.getListSeparator()+
				"sstansd"+AlgorithmConfiguration.getListSeparator()+
				"sstmnmax"+AlgorithmConfiguration.getListSeparator()+
				"sstmnmin"+AlgorithmConfiguration.getListSeparator()+
				"sstmnrange"+AlgorithmConfiguration.getListSeparator()+
				"sbtanmean"+AlgorithmConfiguration.getListSeparator());
	
		config.setParam("TargetColumn","primprodmean");
		config.setParam("LayersNeurons", "100"+AlgorithmConfiguration.getListSeparator()+"2");
		config.setParam("NeuralNetworkName","neuralnetwork");
		config.setParam("UserName","gianpaolo.coro");
		config.setParam("Reference","1");
		config.setParam("ModelName","neuralnetwork");
		
		return config;
	}
	
}

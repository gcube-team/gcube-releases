package org.gcube.dataanalysis.ecoengine.test.checks;

import java.util.HashMap;
import java.util.List;

import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;
import org.gcube.dataanalysis.ecoengine.datatypes.InputTable;
import org.gcube.dataanalysis.ecoengine.datatypes.PrimitiveType;
import org.gcube.dataanalysis.ecoengine.datatypes.StatisticalType;
import org.gcube.dataanalysis.ecoengine.datatypes.enumtypes.PrimitiveTypes;
import org.gcube.dataanalysis.ecoengine.evaluation.DiscrepancyAnalysis;
import org.gcube.dataanalysis.ecoengine.evaluation.bioclimate.InterpolateTables.INTERPOLATIONFUNCTIONS;
import org.gcube.dataanalysis.ecoengine.interfaces.Clusterer;
import org.gcube.dataanalysis.ecoengine.interfaces.ComputationalAgent;
import org.gcube.dataanalysis.ecoengine.interfaces.Evaluator;
import org.gcube.dataanalysis.ecoengine.interfaces.Transducerer;
import org.gcube.dataanalysis.ecoengine.processing.factories.ClusterersFactory;
import org.gcube.dataanalysis.ecoengine.processing.factories.EvaluatorsFactory;
import org.gcube.dataanalysis.ecoengine.processing.factories.TransducerersFactory;
import org.gcube.dataanalysis.ecoengine.test.regression.Regressor;

public class TestMahoutComparison {
	
public static void main(String[] args) throws Exception {
		
		System.out.println("TEST 1");
		
		/*
		trans = TransducerersFactory.getTransducerers(testConfigLocal());
		trans.get(0).init();
		Regressor.process(trans.get(0));
		trans = null;
		
		trans = TransducerersFactory.getTransducerers(testConfigLocal2());
		trans.get(0).init();
		Regressor.process(trans.get(0));
		trans = null;
		
		trans = TransducerersFactory.getTransducerers(testConfigLocal3());
		trans.get(0).init();
		Regressor.process(trans.get(0));
		trans = null;
		
		trans = TransducerersFactory.getTransducerers(testConfigLocal4());
		trans.get(0).init();
		Regressor.process(trans.get(0));
		trans = null;
		*/
		
		/*	
		trans = TransducerersFactory.getTransducerers(testConfigLocal8());
		trans.get(0).init();
		Regressor.process(trans.get(0));
		trans = null;
	
		trans = TransducerersFactory.getTransducerers(testConfigLocal6());
		trans.get(0).init();
		Regressor.process(trans.get(0));
		trans = null;
	*/
//		List<Evaluator> trans = null;
//		trans = EvaluatorsFactory.getEvaluators(testConfigLocal12());
		List<ComputationalAgent> trans = ClusterersFactory.getClusterers(testConfigLocal2());
		trans.get(0).init();
		Regressor.process(trans.get(0));
		
//		PrimitiveType output = (PrimitiveType) trans.get(0).getOutput(); 
//		HashMap<String, String> out = (HashMap<String, String>)output.getContent();
//		DiscrepancyAnalysis.visualizeResults(out);
		
		StatisticalType output = trans.get(0).getOutput();
		
		
		trans = null;
		
}

	
	private static AlgorithmConfiguration testConfigLocal() {
		
		AlgorithmConfiguration config = Regressor.getConfig();
		config.setNumberOfResources(1);
		config.setAgent("DBSCAN");

		config.setParam("OccurrencePointsTable","mahoutclustering");
		config.setParam("FeaturesColumnNames","field0#field1#field2#field3#field4#field5#field6#field7#field8#field9#field10#field11#field12#field13#field14#field15#field16#field17#field18#field19#field20#field21#field22#field23#field24#field25#field26#field27#field28#field29#field30#field31#field32#field33#field34#field35#field36#field37#field38#field39#field40#field41#field42#field43#field44#field45#field46#field47#field48#field49#field50#field51#field52#field53#field54#field55#field56#field57#field58#field59");
		config.setParam("OccurrencePointsClusterTable","occCluster_mahout");
		config.setParam("epsilon","10");
		config.setParam("min_points","1");
		
		
		return config;
	}
	
	private static AlgorithmConfiguration testConfigLocal2() {
		
		AlgorithmConfiguration config = Regressor.getConfig();
		config.setNumberOfResources(1);
		config.setAgent("KMEANS");
		config.setParam("OccurrencePointsTable","mahoutclustering");
		config.setParam("FeaturesColumnNames","field0#field1#field2#field3#field4#field5#field6#field7#field8#field9#field10#field11#field12#field13#field14#field15#field16#field17#field18#field19#field20#field21#field22#field23#field24#field25#field26#field27#field28#field29#field30#field31#field32#field33#field34#field35#field36#field37#field38#field39#field40#field41#field42#field43#field44#field45#field46#field47#field48#field49#field50#field51#field52#field53#field54#field55#field56#field57#field58#field59");
		config.setParam("OccurrencePointsClusterTable","occCluster_mahout");

		config.setParam("k","20");
		config.setParam("max_runs","10");
		config.setParam("max_optimization_steps","1");
		config.setParam("min_points","1");
		
		
		return config;
	}

}

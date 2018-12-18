package org.gcube.dataanalysis.ecoengine.test.regression;

import java.util.List;

import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;
import org.gcube.dataanalysis.ecoengine.datatypes.PrimitiveType;
import org.gcube.dataanalysis.ecoengine.datatypes.enumtypes.PrimitiveTypes;
import org.gcube.dataanalysis.ecoengine.interfaces.Clusterer;
import org.gcube.dataanalysis.ecoengine.interfaces.ComputationalAgent;
import org.gcube.dataanalysis.ecoengine.processing.factories.ClusterersFactory;

public class RegressionTestClusterers {
	/**
	 * example of parallel processing on a single machine the procedure will generate a new table for a distribution on suitable species
	 * 
	 */

public static void main(String[] args) throws Exception {
		
		System.out.println("TEST 1");
		List<ComputationalAgent> clus;
		
		
		clus = ClusterersFactory.getClusterers(testConfigLocal());
		clus.get(0).init();
		Regressor.process(clus.get(0));
		clus = null;
		
		
		clus = ClusterersFactory.getClusterers(testConfigLocal2());
		clus.get(0).init();
		Regressor.process(clus.get(0));
		clus = null;
		
		clus = ClusterersFactory.getClusterers(testConfigLocal3());
		clus.get(0).init();
		Regressor.process(clus.get(0));
		clus = null;
		
}

	
	private static AlgorithmConfiguration testConfigLocal() {
		
		AlgorithmConfiguration config = Regressor.getConfig();
		config.setNumberOfResources(1);
		config.setAgent("DBSCAN");
		config.setParam("OccurrencePointsTable","presence_basking_cluster");
		config.setParam("FeaturesColumnNames","centerlong"+AlgorithmConfiguration.getListSeparator()+"centerlat");
		config.setParam("OccurrencePointsClusterTable","occcluster_dbscan");
		config.setParam("epsilon","10");
		config.setParam("min_points","1");
		
		return config;
	}
	
	
	private static AlgorithmConfiguration testConfigLocal2() {
		
		AlgorithmConfiguration config = Regressor.getConfig();
		config.setNumberOfResources(1);
		config.setAgent("KMEANS");
		config.setParam("OccurrencePointsTable","presence_basking_cluster");
		config.setParam("FeaturesColumnNames","centerlong"+AlgorithmConfiguration.getListSeparator()+"centerlat");
		config.setParam("OccurrencePointsClusterTable","occcluster_kmeans");
		config.setParam("k","24");
		config.setParam("max_runs","1000");
		config.setParam("max_optimization_steps","1000");
		config.setParam("min_points","1");
		
		return config;
	}


	private static AlgorithmConfiguration testConfigLocal3() {
		
		AlgorithmConfiguration config = Regressor.getConfig();
		config.setNumberOfResources(1);
		config.setAgent("XMEANS");
		config.setParam("OccurrencePointsTable","presence_basking_cluster");
		config.setParam("FeaturesColumnNames","centerlong"+AlgorithmConfiguration.getListSeparator()+"centerlat");
		config.setParam("OccurrencePointsClusterTable","occcluster_xmeans");
		config.setParam("maxIterations","1000");
		config.setParam("minClusters","20");
		config.setParam("maxClusters","30");
		config.setParam("min_points","1");
		
		return config;
	}
}

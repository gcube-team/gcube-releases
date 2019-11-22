package org.gcube.dataanalysis.ecoengine.test.checks;

import java.util.List;

import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.dataanalysis.ecoengine.clustering.DBScan;
import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;
import org.gcube.dataanalysis.ecoengine.interfaces.Clusterer;
import org.gcube.dataanalysis.ecoengine.interfaces.ComputationalAgent;
import org.gcube.dataanalysis.ecoengine.interfaces.Transducerer;
import org.gcube.dataanalysis.ecoengine.processing.factories.ClusterersFactory;

public class TestClusterer {
	/**
	 * example of parallel processing on a single machine the procedure will generate a new table for a distribution on suitable species
	 * 
	 */

	public static void main(String[] args) throws Exception {

		System.out.println("TEST 1");
		List<ComputationalAgent> clus = ClusterersFactory.getClusterers(testConfigLocalSpeciesl());
		clus .get(0).init();
		cluster(clus .get(0));
		clus = null;

	}

	private static void cluster(ComputationalAgent clus) throws Exception {

		if (clus != null) {
			TestClusterer tgs = new TestClusterer();
			ThreadCalculator tc = tgs.new ThreadCalculator(clus);
			Thread t = new Thread(tc);
			t.start();
			while (clus.getStatus() < 100) {

				System.out.println("STATUS: " + clus.getStatus());
				Thread.sleep(1000);
			}
		} else
			AnalysisLogger.getLogger().trace("Generator Algorithm Not Supported");

	}

	public class ThreadCalculator implements Runnable {
		ComputationalAgent dg;

		public ThreadCalculator(ComputationalAgent dg) {
			this.dg = dg;
		}

		public void run() {
			try {

				dg.compute();

			} catch (Exception e) {
			}
		}

	}

	private static AlgorithmConfiguration testConfigLocal() {

		AlgorithmConfiguration config = new AlgorithmConfiguration();
		config.setConfigPath("./cfg/");
		config.setPersistencePath("./");
		config.setParam("OccurrencePointsTable","mahoutclustering");
		config.setParam("FeaturesColumnNames","recordedby#basisofrecord#locality");
		config.setParam("OccurrencePointsClusterTable","occCluster_mahout");
		config.setParam("epsilon","10");
		config.setParam("min_points","1");

		config.setConfigPath("./cfg/");
		config.setPersistencePath("./");
		config.setNumberOfResources(1);
		config.setAgent("DBSCAN");
		
		config.setParam("DatabaseUserName","utente");
		config.setParam("DatabasePassword","d4science");
		config.setParam("DatabaseURL","jdbc:postgresql://dbtest.next.research-infrastructures.eu/testdb");
		config.setParam("DatabaseDriver","org.postgresql.Driver");
		
		return config;
	}

	
	private static AlgorithmConfiguration testConfigLocalSpeciesl() {

		AlgorithmConfiguration config = new AlgorithmConfiguration();
		config.setConfigPath("./cfg/");
		config.setPersistencePath("./");
		config.setParam("OccurrencePointsTable","commonspraw");
		config.setParam("FeaturesColumnNames","f1#f2#f3#f4#f5#f6");
		config.setParam("OccurrencePointsClusterTable","commonsprawclustered");
		config.setParam("epsilon","3");
		config.setParam("min_points","5");

		config.setConfigPath("./cfg/");
		config.setPersistencePath("./");
		config.setNumberOfResources(1);
		config.setAgent("DBSCAN");
		
		config.setParam("DatabaseUserName","gcube");
		config.setParam("DatabasePassword","d4science2");
		config.setParam("DatabaseURL","jdbc:postgresql://localhost/testdb");
		config.setParam("DatabaseDriver","org.postgresql.Driver");
		
		return config;
	}
	
	private static AlgorithmConfiguration testConfigLocal2() {

		AlgorithmConfiguration config = new AlgorithmConfiguration();
		config.setConfigPath("./cfg/");
		config.setPersistencePath("./");
		config.setParam("OccurrencePointsTable","generic_idbff59a4c_cada_4447_96b1_5d9a67f89fe8");
		config.setParam("FeaturesColumnNames","x#y");
		config.setParam("OccurrencePointsClusterTable","occCluster_nafo");
		config.setParam("k","10");
		config.setParam("max_runs","300");
		config.setParam("max_optimization_steps","1");
		config.setParam("min_points","1");
		
		config.setConfigPath("./cfg/");
		config.setPersistencePath("./");
		config.setNumberOfResources(1);
		config.setAgent("KMEANS");
		
		/*
		config.setParam("DatabaseUserName","utente");
		config.setParam("DatabasePassword","d4science");
		config.setParam("DatabaseURL","jdbc:postgresql://dbtest.next.research-infrastructures.eu/testdb");
		config.setParam("DatabaseDriver","org.postgresql.Driver");
		*/
		return config;
	}
	
}

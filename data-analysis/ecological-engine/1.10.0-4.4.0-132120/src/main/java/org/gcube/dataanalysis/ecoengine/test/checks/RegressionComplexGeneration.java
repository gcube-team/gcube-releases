package org.gcube.dataanalysis.ecoengine.test.checks;

import java.util.HashMap;
import java.util.List;

import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;
import org.gcube.dataanalysis.ecoengine.interfaces.ComputationalAgent;
import org.gcube.dataanalysis.ecoengine.interfaces.Generator;
import org.gcube.dataanalysis.ecoengine.processing.factories.GeneratorsFactory;

public class RegressionComplexGeneration {
	/**
	 * example of parallel processing on a single machine the procedure will generate a new table for a distribution on suitable species
	 * 
	 */

public static void main(String[] args) throws Exception {
		
		System.out.println("TEST 1");
		/*
		List<Generator> generators = GeneratorsFactory.getGenerators(testConfigRemote());
		generators.get(0).init();
//		generate(generators.get(0));
		generators = null;
		*/
		
		System.out.println("TEST 2");
		List<ComputationalAgent> generators = GeneratorsFactory.getGenerators(testConfigLocal());
		generators.get(0).init();
		generate(generators.get(0));
		generators = null;
		
}

	
	private static void generate(ComputationalAgent generator) throws Exception {

		if (generator != null) {
			RegressionComplexGeneration tgs = new RegressionComplexGeneration();
			ThreadCalculator tc = tgs.new ThreadCalculator(generator);
			Thread t = new Thread(tc);
			t.start();
			while (generator.getStatus() < 100) {

				String resLoad = generator.getResourceLoad();
				String ress = generator.getResources();
//				String species = generator.getLoad();
				System.out.println("LOAD: " + resLoad);
				System.out.println("RESOURCES: " + ress);
//				System.out.println("SPECIES: " + species);
				System.out.println("STATUS: " + generator.getStatus());
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
	

	private static AlgorithmConfiguration testConfigRemote() {
		
		AlgorithmConfiguration config = new AlgorithmConfiguration();
		config.setConfigPath("./cfg/");
		config.setPersistencePath("./");
		config.setNumberOfResources(5);
		config.setModel("REMOTE_AQUAMAPS_SUITABLE");
		
		config.setParam("DistributionTable","hspec_suitable_remote_test");
		config.setParam("CsquarecodesTable","hcaf_d");
		config.setParam("EnvelopeTable","hspen_micro");
		config.setParam("CreateTable","true");
		config.setParam("DatabaseUserName","gcube");
		config.setParam("DatabasePassword","d4science2");
		config.setParam("DatabaseURL","jdbc:postgresql://146.48.87.169/testdb");
		config.setParam("DatabaseDriver","org.postgresql.Driver");
		config.setParam("RemoteCalculator","http://node1.d.venusc.research-infrastructures.eu:5942/api/");
		config.setParam("ServiceUserName","gianpaolo.coro");
		config.setParam("RemoteEnvironment","windows azure");

		HashMap<String, String> properties = new HashMap<String, String>();
		properties.put("property1", "value1");
		properties.put("property2", "value2");
		config.addGeneralProperties(properties);
		
		return config;
	}
	
	
	private static AlgorithmConfiguration testConfigLocal() {
		
		AlgorithmConfiguration config = new AlgorithmConfiguration();
		config.setConfigPath("./cfg/");
		config.setPersistencePath("./");
		config.setNumberOfResources(5);
		config.setModel("AQUAMAPS_SUITABLE");
		
		config.setParam("DistributionTable","hspec_suitable_test_gp");
		config.setParam("CsquarecodesTable","hcaf_d");
		config.setParam("EnvelopeTable","hspen_micro");
		config.setParam("PreprocessedTable", "maxminlat_hspen");
		config.setParam("CreateTable","true");
		
		config.setParam("DatabaseUserName","gcube");
		config.setParam("DatabasePassword","d4science2");
		config.setParam("DatabaseURL","jdbc:postgresql://dbtest.next.research-infrastructures.eu/testdb");
		config.setParam("DatabaseDriver","org.postgresql.Driver");
		
		
		return config;
	}
}

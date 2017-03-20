package org.gcube.dataanalysis.ecoengine.test.checks;

import java.util.List;

import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;
import org.gcube.dataanalysis.ecoengine.interfaces.ComputationalAgent;
import org.gcube.dataanalysis.ecoengine.interfaces.Generator;
import org.gcube.dataanalysis.ecoengine.processing.factories.GeneratorsFactory;

public class RegressionSimpleGeneration {
	/**
	 * example of parallel processing on a single machine the procedure will generate a new table for a distribution on suitable species
	 * 
	 */

public static void main(String[] args) throws Exception {
		
		System.out.println("TEST 1");
		List<ComputationalAgent> generators = GeneratorsFactory.getGenerators(testConfig1());
		generators.get(0).init();
		generate(generators.get(0));
		generators = null;
		
		System.out.println("TEST 2");
		generators = GeneratorsFactory.getGenerators(testConfig2());
		generators.get(0).init();
		generate(generators.get(0));
		generators = null;
		
}

	
	private static void generate(ComputationalAgent generator) throws Exception {

		if (generator != null) {
			RegressionSimpleGeneration tgs = new RegressionSimpleGeneration();
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
	

	private static AlgorithmConfiguration testConfig1() {
		AlgorithmConfiguration config = new AlgorithmConfiguration();
		config.setConfigPath("./cfg/");
		config.setPersistencePath("./");
		config.setNumberOfResources(5);
		config.setModel("TEST");
		return config;
	}
	
	
	private static AlgorithmConfiguration testConfig2() {
		AlgorithmConfiguration config = new AlgorithmConfiguration();
		config.setConfigPath("./cfg/");
		config.setPersistencePath("./");
		config.setNumberOfResources(5);
		config.setModel("DUMMY");
		return config;
	}
}

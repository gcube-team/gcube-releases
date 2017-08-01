package org.gcube.dataanalysis.ecoengine.test.checks;

import java.util.List;

import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;
import org.gcube.dataanalysis.ecoengine.interfaces.ComputationalAgent;
import org.gcube.dataanalysis.ecoengine.interfaces.Modeler;
import org.gcube.dataanalysis.ecoengine.processing.factories.ModelersFactory;

public class TestsHSPENTraining {
	/**
	 * example of parallel processing on a single machine the procedure will generate a new table for a distribution on suitable species
	 * 
	 */

public static void main(String[] args) throws Exception {
		
		System.out.println("TEST 1");
		List<ComputationalAgent> modelers=ModelersFactory.getModelers(testConfig());
		train(modelers.get(0),testConfig());
		modelers = null;
}

	
	private static void train(ComputationalAgent modeler,AlgorithmConfiguration config) throws Exception {

		if (modeler != null) {
			TestsHSPENTraining tgs = new TestsHSPENTraining();
			ThreadCalculator tc = tgs.new ThreadCalculator(modeler, config);
			Thread t = new Thread(tc);
			t.start();
			while (modeler.getStatus() < 100) {

				String resLoad = modeler.getResourceLoad();
				String ress = modeler.getResources();
				System.out.println("LOAD: " + resLoad);
				System.out.println("RESOURCES: " + ress);
				System.out.println("STATUS: " + modeler.getStatus());
				Thread.sleep(1000);
			}
			System.out.println("FINAL STATUS: " + modeler.getStatus());
		} else
			AnalysisLogger.getLogger().trace("Generator Algorithm Not Supported");

	}

	public class ThreadCalculator implements Runnable {
		ComputationalAgent dg;
		AlgorithmConfiguration config;
		
		public ThreadCalculator(ComputationalAgent dg,AlgorithmConfiguration config) {
			this.dg = dg;
			this.config = config;
		}

		public void run() {
			try {

				dg.compute();

			} catch (Exception e) {
			}
		}

	}
	

	private static AlgorithmConfiguration testConfig() {
		AlgorithmConfiguration config = new AlgorithmConfiguration();
		config.setConfigPath("./cfg/");
		config.setPersistencePath("./");
		config.setNumberOfResources(2);
		config.setModel("HSPEN");
		
		config.setParam("OuputEnvelopeTable","hspen_trained");
		config.setParam("OccurrenceCellsTable","occurrencecells");
		config.setParam("EnvelopeTable","hspen_mini");
		config.setParam("CsquarecodesTable", "hcaf_d");
		config.setParam("CreateTable","true");
		
		/*
		config.setParam("DatabaseUserName","gcube");
		config.setParam("DatabasePassword","d4science2");
		config.setParam("DatabaseURL","jdbc:postgresql://localhost/testdb");
		config.setParam("DatabaseDriver","org.postgresql.Driver");
		*/
		
		return config;
	}
	
}

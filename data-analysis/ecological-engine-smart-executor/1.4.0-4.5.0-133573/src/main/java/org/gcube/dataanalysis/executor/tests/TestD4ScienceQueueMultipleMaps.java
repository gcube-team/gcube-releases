package org.gcube.dataanalysis.executor.tests;

import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;
import org.gcube.dataanalysis.ecoengine.user.GeneratorT;
import org.gcube.dataanalysis.executor.generators.D4ScienceDistributedProcessing;

public class TestD4ScienceQueueMultipleMaps {

	public static void main(String[] args) throws Exception {
		String configPath = "./cfg/";
//		String csquareTable = "hcaf_d";
		String csquareTable[] = {"hcaf_d",
				"hcaf_d_2018_LINEAR_01341919234605",
				"hcaf_d_2024_LINEAR_11341919235343",
				"hcaf_d_2030_LINEAR_21341919235554",
				"hcaf_d_2036_LINEAR_31341919235781",
				"hcaf_d_2042_LINEAR_41341919235986",
				"hcaf_d_2050"
		};
		
		String years[] = {
				"2012",
				"2018",
				"2024",
				"2030",
				"2036",
				"2042",
				"2050"
		};
		
		String preprocessedTable = "maxminlat_hspen";
		String envelopeTable = "hspen_mini_10";
		int numberOfResources = 1;
		String userName = "gianpaolo.coro";
		String generatorName = "AQUAMAPS_SUITABLE";
		String scope = "/gcube";
		int k = 2;
		long t0= System.currentTimeMillis();
		String finalDistributionTable = null;
		for (int i = 0; i < k; i++) {
			finalDistributionTable = "hspec_suitable_executor_" + years[i];
			System.out.println("Generating hspec "+finalDistributionTable);
			AlgorithmConfiguration config = GeneratorT.getGenerationConfig(numberOfResources, generatorName, envelopeTable, preprocessedTable, "", userName, csquareTable[i], finalDistributionTable, configPath);
			config.setPersistencePath("./");
			config.setGcubeScope(scope);
			config.setParam("ServiceUserName", "gianpaolo.coro");
			config.setParam("DatabaseUserName", "utente");
			config.setParam("DatabasePassword", "d4science");
			config.setParam("DatabaseURL", "jdbc:postgresql://dbtest.research-infrastructures.eu/aquamapsorgupdated");
			config.setParam("DatabaseDriver", "org.hibernate.dialect.PostgreSQLDialect");
			config.setTableSpace("tbsp_1");
			Thread t = new Thread((new TestD4ScienceQueueMultipleMaps()).new ThreadGenerator(config));
			t.start();
//			generate(config);
		}
		
		long t1= System.currentTimeMillis();
		System.out.println("OVERALL LAUNCH TIME COMPUTATION ON "+k+" REQUESTS IS "+(t1-t0));
	}

	
	public class ThreadGenerator implements Runnable {
		AlgorithmConfiguration c;

		public ThreadGenerator(AlgorithmConfiguration c) {
			this.c = c;
		}

		public void run() {
			try {
				generate(c);

			} catch (Exception e) {
			}
		}

	}
	
	static long globalt0=System.currentTimeMillis();
	static long total=0;
	public static void generate(AlgorithmConfiguration config) throws Exception {

		D4ScienceDistributedProcessing generator = new D4ScienceDistributedProcessing(config);
		generator.init();

		if (generator != null) {
			long t0 = System.currentTimeMillis();
			TestGenerator tgs = new TestGenerator(generator);
			Thread t = new Thread(tgs);
			t.start();
			while (generator.getStatus() < 100) {

				String resLoad = generator.getResourceLoad();
				String ress = generator.getResources();
				String species = generator.getLoad();
				System.out.println("LOAD : " + resLoad);
				System.out.println("RESOURCES: " + ress);
				System.out.println("SPECIES: " + species);
				System.out.println("STATUS: " + generator.getStatus());
				Thread.sleep(10000);
			}
			System.out.println("FINAL STATUS: " + generator.getStatus() + " ELAPSED " + (System.currentTimeMillis() - t0));
			
		} else
			System.out.println("Generator Algorithm Not Supported");
		
		total = System.currentTimeMillis()-globalt0;
		System.err.println("TOTAL TIME: " + total);
		
		// generator.generate();
		// }
	}
}

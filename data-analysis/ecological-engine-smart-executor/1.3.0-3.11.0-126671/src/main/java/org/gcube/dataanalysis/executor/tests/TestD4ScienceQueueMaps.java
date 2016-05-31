package org.gcube.dataanalysis.executor.tests;

import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;
import org.gcube.dataanalysis.ecoengine.user.GeneratorT;
import org.gcube.dataanalysis.executor.generators.D4ScienceDistributedProcessing;

public class TestD4ScienceQueueMaps {

	public static void main(String[] args) throws Exception {
		int k1=0;
		int k2=3;
		for (int i=k1;i<k2;i++){
			ThreadGenerator tg = (new TestD4ScienceQueueMaps()).new ThreadGenerator("hspec_suitable_executor_chunkizedall"+(i+1));
			Thread t = new Thread(tg);
			t.start();
			Thread.sleep(2000);
		}
	}

	public static void produce(String output) throws Exception{
		String configPath = "./cfg/";
		String csquareTable = "hcaf_d";
		String preprocessedTable = "maxminlat_hspen";
//		String envelopeTable = "hspen_mini_50";
		String envelopeTable = "hspen";
		int numberOfResources = 1;
		String userName = "gianpaolo.coro";
		String generatorName = "AQUAMAPS_SUITABLE";
		String scope = "/gcube";
		String finalDistributionTable = output;
		AlgorithmConfiguration config = GeneratorT.getGenerationConfig(numberOfResources, generatorName, envelopeTable, preprocessedTable, "", userName, csquareTable, finalDistributionTable, configPath);
		config.setPersistencePath("./");
		config.setGcubeScope(scope);
		config.setParam("ServiceUserName", "gianpaolo.coro");
		config.setParam("DatabaseUserName","utente");
		config.setParam("DatabasePassword","d4science");
		config.setParam("DatabaseURL","jdbc:postgresql://dbtest.research-infrastructures.eu/aquamapsorgupdated");
		config.setParam("DatabaseDriver","org.hibernate.dialect.PostgreSQLDialect");
		config.setTableSpace("tbsp_1");
		generate(config);
		
	}
	
	public class ThreadGenerator implements Runnable {
		String table;

		public ThreadGenerator(String table) {
			this.table = table;
		}

		public void run() {
			try {
				produce(table);

			} catch (Exception e) {
			}
		}

	}
	
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
				System.out.println("LOAD: " + resLoad);
				System.out.println("RESOURCES: " + ress);
				System.out.println("SPECIES: " + species);
				System.out.println("STATUS: " + generator.getStatus());
				Thread.sleep(10000);
			}
			System.out.println("FINAL STATUS: " + generator.getStatus()+ " ELAPSED "+(System.currentTimeMillis()-t0));
			
		} 
		else
			System.out.println("Generator Algorithm Not Supported");
			
//			generator.generate();
//	}
	}
}

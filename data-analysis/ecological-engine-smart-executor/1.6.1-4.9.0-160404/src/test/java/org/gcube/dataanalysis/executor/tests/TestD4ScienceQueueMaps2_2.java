package org.gcube.dataanalysis.executor.tests;

import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;
import org.gcube.dataanalysis.executor.generators.D4ScienceDistributedProcessing;

public class TestD4ScienceQueueMaps2_2 {

	public static void main(String[] args) throws Exception {
		String configPath = "./cfg/";
		String csquareTable = "hcaf_filteredid_e7118301_bb63_4314_ae88_f76a00da5e9a";
		String occurrencesTable = "occurrencecells";
		String envelopeTable = "hspen_filteredid_71f82462_0aa4_4e02_b76e_55214f250c35";
//		String envelopeTable = "hspen";
		int numberOfResources = 1;
		String userName = "gianpaolo.coro";
		String generatorName = "AQUAMAPS_SUITABLE";
		String scope = "/gcube";
		String finalDistributionTable = "hspec_suitable_executorsingle_t3";

		// Generate
		AlgorithmConfiguration config = CustomRegressor.getConfig();
		
		config.setParam("DatabaseUserName","utente");
		config.setParam("DatabasePassword","d4science");
		config.setParam("DatabaseURL","jdbc:postgresql://statistical-manager.d.d4science.research-infrastructures.eu/testdb");
		
		config.setConfigPath(configPath);
		config.setNumberOfResources(numberOfResources);
		config.setModel(generatorName);
		
		config.setParam("EnvelopeTable", envelopeTable);
		config.setParam("CsquarecodesTable", csquareTable);
		config.setParam("DistributionTable", finalDistributionTable);
		config.setParam("CreateTable", "true");
		config.setParam("UserName",userName);
		config.setPersistencePath("./");
		config.setParam("OccurrencePointsTable", occurrencesTable);
		config.setGcubeScope(scope);
		config.setParam("ServiceUserName", "gianpaolo.coro");
		
		/*
		config.setParam("DatabaseUserName","utente");
		config.setParam("DatabasePassword","d4science");
		config.setParam("DatabaseURL","jdbc:postgresql://dbtest.research-infrastructures.eu/aquamapsorgupdated");
		config.setParam("DatabaseDriver","org.postgresql.Driver");
		*/
		
		generate(config);
		/*
		finalDistributionTable = "hspec_suitable_executor_2";
		config.setParam("DistributionTable", finalDistributionTable);
		generate(config);
		*/
		
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
				Thread.sleep(20000);
			}
			System.out.println("FINAL STATUS: " + generator.getStatus()+ " ELAPSED "+(System.currentTimeMillis()-t0));
			
		} 
		else
			System.out.println("Generator Algorithm Not Supported");
			
//			generator.generate();
//	}
	}
}

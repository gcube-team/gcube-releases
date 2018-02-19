package org.gcube.dataanalysis.executor.tests;

import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;
import org.gcube.dataanalysis.ecoengine.user.GeneratorT;
import org.gcube.dataanalysis.executor.generators.D4ScienceDistributedProcessing;

public class TestD4ScienceQueueMaps2 {

	public static void main(String[] args) throws Exception {
		String configPath = "./cfg/";
		String csquareTable = "hcaf_d";
		String preprocessedTable = "maxminlat_hspen";
		String occurrencesTable = "occurrencecells";
		String envelopeTable = "hspen_mini_10";
//		String envelopeTable = "hspen";
		int numberOfResources = 1;
		String userName = "gianpaolo.coro";
		String generatorName = "AQUAMAPS_SUITABLE";
		String scope = "/gcube";
		String finalDistributionTable = "hspec_suitable_executorsingle2";
// Train
//		 ModelerT.train(ModelerT.getTrainingConfig(modelName, absenceTable, presenceTable, speciesCode, userName, neuralNetworkLayers, configPath));

		// Generate
		AlgorithmConfiguration config = GeneratorT.getGenerationConfig(numberOfResources, generatorName, envelopeTable, preprocessedTable, "", userName, csquareTable, finalDistributionTable, configPath);
		config.setPersistencePath("./");
		config.setParam("OccurrencePointsTable", occurrencesTable);
		config.setGcubeScope(scope);
		config.setParam("ServiceUserName", "gianpaolo.coro");
		config.setParam("DatabaseUserName","utente");
		config.setParam("DatabasePassword","d4science");
		config.setParam("DatabaseURL","jdbc:postgresql://dbtest.research-infrastructures.eu/aquamapsorgupdated");
//		config.setParam("DatabaseDriver","org.hibernate.dialect.PostgreSQLDialect");
		config.setParam("DatabaseDriver","org.postgresql.Driver");
		
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

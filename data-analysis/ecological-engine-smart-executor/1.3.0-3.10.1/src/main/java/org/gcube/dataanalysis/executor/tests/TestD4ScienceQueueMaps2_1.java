package org.gcube.dataanalysis.executor.tests;

import java.io.File;

import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;
import org.gcube.dataanalysis.ecoengine.test.regression.Regressor;
import org.gcube.dataanalysis.ecoengine.utils.Transformations;
import org.gcube.dataanalysis.executor.generators.D4ScienceDistributedProcessing;
import org.gcube.dataanalysis.executor.nodes.transducers.OccurrenceMergingNode;

public class TestD4ScienceQueueMaps2_1 {

	public static void main(String[] args) throws Exception {
		String scope = "/gcube";
		
		AlgorithmConfiguration config = Regressor.getConfig();
		config.setConfigPath("./cfg/");
		config.setPersistencePath("./");
		
//		config.setAgent("OCCURRENCES_MERGER");
//		config.setAgent("OCCURRENCES_INTERSECTOR");
		config.setAgent("OCCURRENCES_SUBTRACTION");
		
		config.setParam("longitudeColumn", "decimallongitude");
		config.setParam("latitudeColumn", "decimallatitude");
		config.setParam("recordedByColumn", "recordedby");
		config.setParam("scientificNameColumn", "scientificname");
		config.setParam("eventDateColumn", "eventdate");
		config.setParam("lastModificationColumn", "modified");
		/*
		config.setParam("leftTableName", "occurrencesintersected");
		config.setParam("rightTableName", "occurrencesintersected");
		*/
		/*
		config.setParam("leftTableName", "occurrence_species_id0045886b_2a7c_4ede_afc4_3157c694b893");
		config.setParam("rightTableName", "occurrence_species_id0045886b_2a7c_4ede_afc4_3157c694b893");
		*/
		
//		config.setParam("leftTableName", "processedoccurrences_id_a07a7574_5ab0_49e3_ac36_6d1158eea01f");
//		config.setParam("rightTableName", "processedoccurrences_id_68fb454f_4c32_43c1_8872_c1b7020ebda3");
		
//		config.setParam("leftTableName", "occurrence_species_idb1a80ed3_0b07_4481_b8f0_95b821b2d4c9");
//		config.setParam("rightTableName", "occurrence_species_idb1a80ed3_0b07_4481_b8f0_95b821b2d4c9");
		config.setParam("leftTableName", "occurrencesdeleted");
		config.setParam("rightTableName", "occurrencesdeleted");
		
//		config.setParam("leftTableName", "speciesset1");
//		config.setParam("rightTableName", "speciesset2");
		
		config.setParam("finalTableName", "occurrencessubtract_distibG_SP_0_0");
		config.setParam("spatialTolerance", "0.0");
		config.setParam("confidence", "0");
		
		
		config.setParam("DatabaseUserName","utente");
		config.setParam("DatabasePassword","d4science");
		config.setParam("DatabaseURL","jdbc:postgresql://dbtest.research-infrastructures.eu/testdb");
		config.setParam("DatabaseDriver","org.postgresql.Driver");
		
		config.setGcubeScope(scope);
		config.setParam("ServiceUserName", "gianpaolo.coro");
		
		generate(config);
		
		/*
		OccurrenceProcessingNode opn = new OccurrenceProcessingNode();
		opn.setup(config);
		String dump = "./cfg/dumped.dat";
		Transformations.dumpConfig(dump, config);
		opn.executeNode(0, 1, 0, 1, false, "./cfg/", dump, "");
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

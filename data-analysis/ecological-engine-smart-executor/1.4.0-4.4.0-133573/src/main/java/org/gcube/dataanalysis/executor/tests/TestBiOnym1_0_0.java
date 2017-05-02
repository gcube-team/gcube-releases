package org.gcube.dataanalysis.executor.tests;

import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;
import org.gcube.dataanalysis.executor.generators.D4ScienceDistributedProcessing;
import org.gcube.dataanalysis.executor.nodes.transducers.bionym.BionymWorkflow;
import org.gcube.dataanalysis.executor.nodes.transducers.bionym.CometMatcherManager;
import org.gcube.dataanalysis.executor.nodes.transducers.bionym.EVBPreprocessing;

public class TestBiOnym1_0_0 {

	public static void main(String[] args) throws Exception {
		// Generate
		AlgorithmConfiguration config = new AlgorithmConfiguration();
		config.setConfigPath("./cfg/");

		config.setParam(BionymWorkflow.destinationTableParam, "taxamatchoutput");
		config.setParam(BionymWorkflow.destinationTableLable, "taxamatchoutputlabel");
		config.setParam(BionymWorkflow.originTableParam, "taxamatchinput");
		config.setParam(BionymWorkflow.rawnamesColumnParam, "rawstrings");
		config.setParam(BionymWorkflow.parserParam, CometMatcherManager.Parsers.SIMPLE.name());
		config.setParam(BionymWorkflow.referenceParam, CometMatcherManager.Reference.ASFIS.name());
		config.setParam(BionymWorkflow.soundexweightParam, CometMatcherManager.Weights.EDIT_DISTANCE.name());
		config.setParam(BionymWorkflow.doPreprocessParam , EVBPreprocessing.Preprocessors.EXPERT_RULES.name());
		config.setParam(BionymWorkflow.maxMatchesParam , "10");
		config.setAgent("BIONYM");
		
		config.setPersistencePath("./");
		config.setGcubeScope( "/gcube");
//		config.setGcubeScope( "/d4science.research-infrastructures.eu");
		config.setParam("ServiceUserName", "gianpaolo.coro");
		
		config.setParam("DatabaseUserName","utente");
		config.setParam("DatabasePassword","d4science");
//		config.setParam("DatabaseURL","jdbc:postgresql://dbtest.research-infrastructures.eu/testdb");
		config.setParam("DatabaseURL","jdbc:postgresql://statistical-manager.d.d4science.research-infrastructures.eu/testdb");
		
//		config.setParam("DatabaseUserName", "gcube");
//		config.setParam("DatabasePassword", "d4science2");
//		config.setParam("DatabaseURL", "jdbc:postgresql://146.48.87.169/testdb");
		config.setParam("DatabaseDriver", "org.postgresql.Driver");
		
		generate(config);
		
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
				Thread.sleep(2000);
			}
			System.out.println("FINAL STATUS: " + generator.getStatus()+ " ELAPSED "+(System.currentTimeMillis()-t0));
			
		} 
		else
			System.out.println("Generator Algorithm Not Supported");
			
//			generator.generate();
//	}
	}
}

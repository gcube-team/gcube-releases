package org.gcube.dataanalysis.executor.tests;

import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;
import org.gcube.dataanalysis.executor.generators.D4ScienceDistributedProcessing;
import org.gcube.dataanalysis.executor.nodes.transducers.bionym.BionymFlexibleWorkflowTransducer;
import org.gcube.dataanalysis.executor.nodes.transducers.bionym.utils.YasmeenGlobalParameters;

public class RegressionTestBiOnym1Matcher {

	public static void executeWF(String[] args) throws Exception {
		// Generate
		AlgorithmConfiguration config = new AlgorithmConfiguration();
		config.setConfigPath("./cfg/");

		config.setParam("DatabaseUserName","utente");
		config.setParam("DatabasePassword","d4science");
		config.setParam("DatabaseURL","jdbc:postgresql://statistical-manager.d.d4science.research-infrastructures.eu/testdb");
		
		config.setParam(YasmeenGlobalParameters.parserNameParam,YasmeenGlobalParameters.BuiltinParsers.SIMPLE.name());
		config.setParam(YasmeenGlobalParameters.taxaAuthorityFileParam,YasmeenGlobalParameters.BuiltinDataSources.ASFIS.name());
		config.setParam(YasmeenGlobalParameters.activatePreParsingProcessing,"true");
		config.setParam(YasmeenGlobalParameters.useStemmedGenusAndSpecies,"false");
		
		
		config.setParam(BionymFlexibleWorkflowTransducer.matcherParamPrefix+"_"+1,YasmeenGlobalParameters.BuiltinMatchers.GSAy.name());
		config.setParam(BionymFlexibleWorkflowTransducer.thresholdParamPrefix+"_"+1,"0.6");
		config.setParam(BionymFlexibleWorkflowTransducer.maxresultsParamPrefix+"_"+1,"10");

		config.setParam(BionymFlexibleWorkflowTransducer.destinationTableParam, "taxamatchoutputlocal");
		config.setParam(BionymFlexibleWorkflowTransducer.destinationTableLableParam, "taxamatchoutputlabel");
		//2
//		config.setParam(BionymFlexibleWorkflowTransducer.originTableParam, "generic_id471e6d50_d243_4112_bc07_e22152438e5c");
//		config.setParam(BionymFlexibleWorkflowTransducer.rawnamesColumnParam, "field0");
		//1000
		config.setParam(BionymFlexibleWorkflowTransducer.originTableParam, "taxamatchinput1000");
		config.setParam(BionymFlexibleWorkflowTransducer.rawnamesColumnParam, "rawstrings");
		
		config.setAgent("BIONYM");
		
		config.setPersistencePath("./");
		config.setGcubeScope( "/gcube");
//		config.setGcubeScope( "/d4science.research-infrastructures.eu");
		config.setParam("ServiceUserName", "gianpaolo.coro");
		
		config.setParam("DatabaseDriver", "org.postgresql.Driver");
		
		generate(config);
		
	}

	public static void main(String []args) throws Exception{
		
		for (int i=0;i<1;i++){
			executeWF(args);
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
				Thread.sleep(5000);
			}
			System.out.println("FINAL STATUS: " + generator.getStatus()+ " ELAPSED "+(System.currentTimeMillis()-t0));
			
		} 
		else
			System.out.println("Generator Algorithm Not Supported");
			
//			generator.generate();
//	}
	}
}

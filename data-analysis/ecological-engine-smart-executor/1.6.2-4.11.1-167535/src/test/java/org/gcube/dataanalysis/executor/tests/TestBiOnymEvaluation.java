package org.gcube.dataanalysis.executor.tests;

import java.io.File;

import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;
import org.gcube.dataanalysis.executor.generators.D4ScienceDistributedProcessing;
import org.gcube.dataanalysis.executor.nodes.transducers.bionym.BionymFlexibleWorkflowTransducer;
import org.gcube.dataanalysis.executor.nodes.transducers.bionym.utils.YasmeenGlobalParameters;

public class TestBiOnymEvaluation {

	public static void main(String[] args) throws Exception {
		// Generate
		AlgorithmConfiguration config = new AlgorithmConfiguration();
		config.setConfigPath("./cfg/");

		config.setParam("DatabaseUserName","utente");
		config.setParam("DatabasePassword","d4science");
		config.setParam("DatabaseURL","jdbc:postgresql://statistical-manager.d.d4science.research-infrastructures.eu/testdb");
		
		config.setParam(YasmeenGlobalParameters.parserNameParam,YasmeenGlobalParameters.BuiltinParsers.GNI.name());
		config.setParam(YasmeenGlobalParameters.taxaAuthorityFileParam,YasmeenGlobalParameters.BuiltinDataSources.WORMS_PISCES.name());
		config.setParam(YasmeenGlobalParameters.activatePreParsingProcessing,"true");
		config.setParam(YasmeenGlobalParameters.useStemmedGenusAndSpecies,"false");
		
		/*
		config.setParam(BionymFlexibleWorkflowTransducer.matcherParamPrefix+"_"+1,YasmeenGlobalParameters.BuiltinMatchers.FUZZYMATCH.name());
		config.setParam(BionymFlexibleWorkflowTransducer.thresholdParamPrefix+"_"+1,"0.4");
		config.setParam(BionymFlexibleWorkflowTransducer.maxresultsParamPrefix+"_"+1,"10");
		*/
		
		config.setParam(BionymFlexibleWorkflowTransducer.matcherParamPrefix+"_"+1,YasmeenGlobalParameters.BuiltinMatchers.GSAy.name());
		config.setParam(BionymFlexibleWorkflowTransducer.thresholdParamPrefix+"_"+1,"0.6");
		config.setParam(BionymFlexibleWorkflowTransducer.maxresultsParamPrefix+"_"+1,"10");
		
		
		config.setParam(BionymFlexibleWorkflowTransducer.matcherParamPrefix+"_"+2,YasmeenGlobalParameters.BuiltinMatchers.FUZZYMATCH.name());
		config.setParam(BionymFlexibleWorkflowTransducer.thresholdParamPrefix+"_"+2,"0.6");
		config.setParam(BionymFlexibleWorkflowTransducer.maxresultsParamPrefix+"_"+2,"10");
		
		config.setParam(BionymFlexibleWorkflowTransducer.matcherParamPrefix+"_"+3,YasmeenGlobalParameters.BuiltinMatchers.LEVENSHTEIN.name());
		config.setParam(BionymFlexibleWorkflowTransducer.thresholdParamPrefix+"_"+3,"0.4");
		config.setParam(BionymFlexibleWorkflowTransducer.maxresultsParamPrefix+"_"+3,"10");
		config.setParam(BionymFlexibleWorkflowTransducer.matcherParamPrefix+"_"+4,YasmeenGlobalParameters.BuiltinMatchers.TRIGRAM.name());
		config.setParam(BionymFlexibleWorkflowTransducer.thresholdParamPrefix+"_"+4,"0.4");
		config.setParam(BionymFlexibleWorkflowTransducer.maxresultsParamPrefix+"_"+4,"10");

//		System.exit(0);
		config.setAgent("BIONYM");
		config.setPersistencePath("./");
		config.setGcubeScope( "/gcube");
//		config.setGcubeScope( "/d4science.research-infrastructures.eu");
		config.setParam("ServiceUserName", "gianpaolo.coro");
		config.setParam("DatabaseDriver", "org.postgresql.Driver");
//		String directory = "C:\\Users\\coro\\Desktop\\DATABASE e NOTE\\Experiments\\BiOnym TaxaMatch\\BenchmarkTablesPrepared\\";
//		String directory = "C:\\Users\\coro\\Desktop\\DATABASE e NOTE\\Experiments\\BiOnym TaxaMatch\\BenchmarkTablesPreparedReduced\\";
		String directory = "C:\\Users\\coro\\Desktop\\DATABASE e NOTE\\Experiments\\BiOnym TaxaMatch\\BenchmarkSimulatedReduced\\";
//		String directory = "C:\\Users\\coro\\Desktop\\DATABASE e NOTE\\Experiments\\BiOnym TaxaMatch\\BenchmarkWard\\";
		File[] files = new File(directory).listFiles();
		int counter = 1;
		long t0=System.currentTimeMillis();
		for (File file:files){
			if (file.getName().endsWith(".prepr.csv")){
				
//				if (file.getName().startsWith("real9.csv")){
						
				if (counter>=0){
//				String tablename = "bionymreal5csv6";
				String tablename = "bionym" + file.getName().replace(".", "");
				System.out.println("Processing table "+tablename+" number:"+counter);
				String outputtablename = "bionymoutsimulgni2"+file.getName().replace(".","");
				config.setParam(BionymFlexibleWorkflowTransducer.originTableParam, tablename);
				config.setParam(BionymFlexibleWorkflowTransducer.rawnamesColumnParam, "sname");
				config.setParam(BionymFlexibleWorkflowTransducer.destinationTableParam, outputtablename);
				config.setParam(BionymFlexibleWorkflowTransducer.destinationTableLableParam, outputtablename+"label");
				generate(config);
				
				System.out.println("STOP FOR A WHILE "+counter);
				Thread.sleep(1000);
//				break;
			}
				counter++;
				
			}
			
		}
		
		System.out.println("OVERALL COMPUTATION ON ALL TABLES: "+(System.currentTimeMillis()-t0)+" ms");
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

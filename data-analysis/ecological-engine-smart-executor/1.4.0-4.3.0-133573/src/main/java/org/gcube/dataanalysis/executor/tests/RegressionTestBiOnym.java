package org.gcube.dataanalysis.executor.tests;

import org.gcube.common.authorization.library.provider.AuthorizationProvider;
import org.gcube.common.authorization.library.provider.SecurityTokenProvider;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;
import org.gcube.dataanalysis.executor.generators.D4ScienceDistributedProcessing;
import org.gcube.dataanalysis.executor.nodes.transducers.bionym.BionymFlexibleWorkflowTransducer;
import org.gcube.dataanalysis.executor.nodes.transducers.bionym.utils.YasmeenGlobalParameters;

public class RegressionTestBiOnym {

	public static void executeWF(String[] args) throws Exception {
		// Generate
		AlgorithmConfiguration config = new AlgorithmConfiguration();
		config.setConfigPath("./cfg/");
		
		config.setParam("DatabaseUserName","utente");
		config.setParam("DatabasePassword","d4science");
		config.setParam("DatabaseURL","jdbc:postgresql://statistical-manager.d.d4science.research-infrastructures.eu/testdb");
		
		config.setParam(YasmeenGlobalParameters.parserNameParam,YasmeenGlobalParameters.BuiltinParsers.SIMPLE.name());
		config.setParam(YasmeenGlobalParameters.taxaAuthorityFileParam,YasmeenGlobalParameters.BuiltinDataSources.WORMS_PISCES.name());
		config.setParam(YasmeenGlobalParameters.activatePreParsingProcessing,"true");
		config.setParam(YasmeenGlobalParameters.useStemmedGenusAndSpecies,"false");
		
		
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
		
		config.setParam(BionymFlexibleWorkflowTransducer.destinationTableParam, "taxamatchoutputlocal");
		config.setParam(BionymFlexibleWorkflowTransducer.destinationTableLableParam, "taxamatchoutputlabel");
		//1000
		config.setParam(BionymFlexibleWorkflowTransducer.originTableParam, "taxamatchinput1000");
		config.setParam(BionymFlexibleWorkflowTransducer.rawnamesColumnParam, "rawstrings");
		
//		config.setParam(BionymFlexibleWorkflowTransducer.originTableParam, "taxamatchinput");
//		config.setParam(BionymFlexibleWorkflowTransducer.rawnamesColumnParam, "rawstrings");
		//4
		//config.setParam(BionymFlexibleWorkflowTransducer.originTableParam, "generic_id1ecb405c_980f_47a4_926a_3043d065fc7d");
		//config.setParam(BionymFlexibleWorkflowTransducer.rawnamesColumnParam, "field0");
		
		//2
//		config.setParam(BionymFlexibleWorkflowTransducer.originTableParam, "generic_id471e6d50_d243_4112_bc07_e22152438e5c");
//		config.setParam(BionymFlexibleWorkflowTransducer.rawnamesColumnParam, "field0");
		
		//FABIO DS:
//		config.setParam(BionymTransducer.originTableParam, "generic_ide43477df_d9e6_4191_8a81_e94a0a2d16f8");
//		config.setParam(BionymTransducer.rawnamesColumnParam, "field0");
		
		config.setAgent("BIONYM");
		
		config.setPersistencePath("./");
		String scope = "/gcube/devNext/NextNext";
		config.setGcubeScope( scope );
		ScopeProvider.instance.set(scope);
		String authorizationToken = "cb289202-e7d6-45ee-8076-a80bc4d4be51-98187548";
		SecurityTokenProvider.instance.set(authorizationToken);
		config.setGcubeToken(authorizationToken);
		config.setGcubeUserName("gianpaolo.coro");
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

package org.gcube.dataanalysis.executor.tests;

import java.io.File;
import java.util.List;

import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;
import org.gcube.dataanalysis.ecoengine.datatypes.StatisticalType;
import org.gcube.dataanalysis.ecoengine.interfaces.ComputationalAgent;
import org.gcube.dataanalysis.ecoengine.processing.factories.GeneratorsFactory;
import org.gcube.dataanalysis.ecoengine.processing.factories.TransducerersFactory;
import org.gcube.dataanalysis.ecoengine.test.regression.Regressor;
import org.gcube.dataanalysis.executor.generators.D4ScienceDistributedProcessing;
import org.gcube.dataanalysis.executor.nodes.transducers.bionym.BionymFlexibleWorkflowTransducer;
import org.gcube.dataanalysis.executor.nodes.transducers.bionym.BionymLocalTransducer;
import org.gcube.dataanalysis.executor.nodes.transducers.bionym.utils.YasmeenGlobalParameters;

public class TestBiOnymLocal {

	public static void main(String[] args) throws Exception {
		// Generate
		AlgorithmConfiguration config = new AlgorithmConfiguration();
		config.setConfigPath("./cfg/");

		config.setParam("DatabaseUserName","utente");
		config.setParam("DatabasePassword","d4science");
		config.setParam("DatabaseURL","jdbc:postgresql://statistical-manager.d.d4science.research-infrastructures.eu/testdb");
		
		config.setParam(YasmeenGlobalParameters.parserNameParam,YasmeenGlobalParameters.BuiltinParsers.SIMPLE.name());
		config.setParam(YasmeenGlobalParameters.taxaAuthorityFileParam,YasmeenGlobalParameters.BuiltinDataSources.FISHBASE.name());
		config.setParam(YasmeenGlobalParameters.activatePreParsingProcessing,"true");
		config.setParam(YasmeenGlobalParameters.useStemmedGenusAndSpecies,"false");
		
		config.setParam(YasmeenGlobalParameters.performanceParam,YasmeenGlobalParameters.Performance.HIGH_SPEED.name());
		
		config.setParam(BionymFlexibleWorkflowTransducer.matcherParamPrefix+"_"+3,YasmeenGlobalParameters.BuiltinMatchers.LEVENSHTEIN.name());
		config.setParam(BionymFlexibleWorkflowTransducer.thresholdParamPrefix+"_"+3,"0.4");
		config.setParam(BionymFlexibleWorkflowTransducer.maxresultsParamPrefix+"_"+3,"10");
		
		
		config.setParam(BionymFlexibleWorkflowTransducer.matcherParamPrefix+"_"+1,YasmeenGlobalParameters.BuiltinMatchers.GSAy.name());
		config.setParam(BionymFlexibleWorkflowTransducer.thresholdParamPrefix+"_"+1,"0.6");
		config.setParam(BionymFlexibleWorkflowTransducer.maxresultsParamPrefix+"_"+1,"10");
		
		
		config.setParam(BionymFlexibleWorkflowTransducer.matcherParamPrefix+"_"+2,YasmeenGlobalParameters.BuiltinMatchers.FUZZYMATCH.name());
		config.setParam(BionymFlexibleWorkflowTransducer.thresholdParamPrefix+"_"+2,"0.6");
		config.setParam(BionymFlexibleWorkflowTransducer.maxresultsParamPrefix+"_"+2,"10");
		
		/*
		config.setParam(BionymFlexibleWorkflowTransducer.matcherParamPrefix+"_"+3,YasmeenGlobalParameters.BuiltinMatchers.LEVENSHTEIN.name());
		config.setParam(BionymFlexibleWorkflowTransducer.thresholdParamPrefix+"_"+3,"0.4");
		config.setParam(BionymFlexibleWorkflowTransducer.maxresultsParamPrefix+"_"+3,"10");
		
		config.setParam(BionymFlexibleWorkflowTransducer.matcherParamPrefix+"_"+4,YasmeenGlobalParameters.BuiltinMatchers.TRIGRAM.name());
		config.setParam(BionymFlexibleWorkflowTransducer.thresholdParamPrefix+"_"+4,"0.4");
		config.setParam(BionymFlexibleWorkflowTransducer.maxresultsParamPrefix+"_"+4,"10");
		*/
		config.setAgent("BIONYM_LOCAL");
		config.setPersistencePath("./");
		config.setGcubeScope( "/gcube");
		config.setParam("ServiceUserName", "gianpaolo.coro");
		config.setParam("DatabaseDriver", "org.postgresql.Driver");
		config.setParam(BionymLocalTransducer.speciesName, "Gadus morhua (Linnaeus, 1758)");
		List<ComputationalAgent> transducers = TransducerersFactory.getTransducerers(config);
		ComputationalAgent transducer =transducers.get(0);
		transducer.init();
		
		List<StatisticalType> types = transducer.getInputParameters();
//		System.out.println(types);
		Regressor.process(transducer);
	}
	
}

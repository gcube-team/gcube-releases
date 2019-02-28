package org.gcube.dataanalysis.executor.tests;

import java.util.ArrayList;
import java.util.List;

import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;
import org.gcube.dataanalysis.executor.job.management.GenericWorker;
import org.gcube.dataanalysis.executor.job.management.WPSJobManager;
import org.gcube.dataanalysis.executor.job.management.WPSJobManager.TasksWatcher;
import org.gcube.dataanalysis.executor.nodes.transducers.bionym.BionymFlexibleWorkflowTransducer;
import org.gcube.dataanalysis.executor.nodes.transducers.bionym.utils.YasmeenGlobalParameters;

public class TestWPSJobs {

	public static AlgorithmConfiguration buildTestConfiguration(){
		AlgorithmConfiguration config = new AlgorithmConfiguration();
		config.setConfigPath("./cfg/");
		AnalysisLogger.setLogger(config.getConfigPath() + AlgorithmConfiguration.defaultLoggerFile);
		config.setParam("DatabaseUserName", "utente");
		config.setParam("DatabasePassword", "d4science");
		config.setParam("DatabaseURL", "jdbc:postgresql://statistical-manager.d.d4science.research-infrastructures.eu/testdb");

		config.setParam(YasmeenGlobalParameters.parserNameParam, YasmeenGlobalParameters.BuiltinParsers.SIMPLE.name());
		config.setParam(YasmeenGlobalParameters.taxaAuthorityFileParam, YasmeenGlobalParameters.BuiltinDataSources.FISHBASE.name());
		config.setParam(YasmeenGlobalParameters.performanceParam, YasmeenGlobalParameters.Performance.MAX_SPEED.name());
		
		config.setParam(YasmeenGlobalParameters.activatePreParsingProcessing, "true");
		config.setParam(YasmeenGlobalParameters.useStemmedGenusAndSpecies, "false");

		config.setParam(BionymFlexibleWorkflowTransducer.matcherParamPrefix + "_" + 1, YasmeenGlobalParameters.BuiltinMatchers.GSAy.name());
		config.setParam(BionymFlexibleWorkflowTransducer.thresholdParamPrefix + "_" + 1, "0.6");
		config.setParam(BionymFlexibleWorkflowTransducer.maxresultsParamPrefix + "_" + 1, "10");

		config.setParam(BionymFlexibleWorkflowTransducer.matcherParamPrefix + "_" + 2, YasmeenGlobalParameters.BuiltinMatchers.FUZZYMATCH.name());
		config.setParam(BionymFlexibleWorkflowTransducer.thresholdParamPrefix + "_" + 2, "0.6");
		config.setParam(BionymFlexibleWorkflowTransducer.maxresultsParamPrefix + "_" + 2, "10");

		config.setParam(BionymFlexibleWorkflowTransducer.matcherParamPrefix + "_" + 3, YasmeenGlobalParameters.BuiltinMatchers.LEVENSHTEIN.name());
		config.setParam(BionymFlexibleWorkflowTransducer.thresholdParamPrefix + "_" + 3, "0.4");
		config.setParam(BionymFlexibleWorkflowTransducer.maxresultsParamPrefix + "_" + 3, "10");

		config.setParam(BionymFlexibleWorkflowTransducer.matcherParamPrefix + "_" + 4, YasmeenGlobalParameters.BuiltinMatchers.TRIGRAM.name());
		config.setParam(BionymFlexibleWorkflowTransducer.thresholdParamPrefix + "_" + 4, "0.4");
		config.setParam(BionymFlexibleWorkflowTransducer.maxresultsParamPrefix + "_" + 4, "10");

		config.setParam(BionymFlexibleWorkflowTransducer.destinationTableParam, "taxamatchoutputlocal");
		config.setParam(BionymFlexibleWorkflowTransducer.destinationTableLableParam, "taxamatchoutputlabel");

		// 4
		//config.setParam(BionymFlexibleWorkflowTransducer.originTableParam, "generic_id1ecb405c_980f_47a4_926a_3043d065fc7d");
		//config.setParam(BionymFlexibleWorkflowTransducer.rawnamesColumnParam, "field0");

		config.setParam(BionymFlexibleWorkflowTransducer.originTableParam, "taxamatchinput1000");
		config.setParam(BionymFlexibleWorkflowTransducer.rawnamesColumnParam, "rawstrings");
		
		config.setAgent("BIONYM");
		config.setPersistencePath("./");
		config.setGcubeScope("/gcube/devNext/NextNext");
//		config.setGcubeScope("/gcube/devsec/devVRE");
		config.setParam("ServiceUserName", "gianpaolo.coro");
		config.setParam("DatabaseDriver", "org.postgresql.Driver");
		config.setGcubeUserName("gianpaolo.coro");
		config.setGcubeToken("f9d49d76-cd60-48ed-9f8e-036bcc1fc045-98187548");

		return config;
	}
	
	public static void main1(String[] args) throws Exception {

		String host = "dataminer1-devnext.d4science.org";
		String session = "12345";
		int port = 80;
		String algorithm = "org.gcube.dataanalysis.executor.nodes.transducers.bionym.BionymFlexibleWorkflowTransducer";
		AlgorithmConfiguration config = buildTestConfiguration();
		
		WPSJobManager manager = new WPSJobManager();
		TasksWatcher taskWatcher = manager.new TasksWatcher(algorithm, config.getGcubeUserName(), config.getGcubeToken(), host, port, session, 1, config, 1, 1, 1, 1,"",1);
		Thread t = new Thread(taskWatcher);
		t.start();
		
		while (taskWatcher.exitstatus.equals(GenericWorker.TASK_UNDEFINED)){
			Thread.sleep(1000);
			System.out.print(".");
		}
			
		AnalysisLogger.getLogger().debug("Task 1 terminated with output "+taskWatcher.exitstatus );
		//taskWatcher.run();
	}
	
	public static void main(String[] args) throws Exception {
		AlgorithmConfiguration config = buildTestConfiguration();
		String algorithm = "org.gcube.dataanalysis.executor.nodes.transducers.bionym.BionymFlexibleWorkflowTransducer";		
		ScopeProvider.instance.set(config.getGcubeScope());
		WPSJobManager jobmanager = new WPSJobManager();
		//int nArguments = 100;
		int nArguments = 20;
		List<String> arguments = new ArrayList<String>();
		for (int i=1;i<=nArguments;i++){
			String argument = "1 1 "+i+" 1";
			arguments.add(argument);
		}
		String sessionID ="1234";
		jobmanager.uploadAndExecuteChunkized(config, algorithm, arguments,sessionID);
		
	}
}

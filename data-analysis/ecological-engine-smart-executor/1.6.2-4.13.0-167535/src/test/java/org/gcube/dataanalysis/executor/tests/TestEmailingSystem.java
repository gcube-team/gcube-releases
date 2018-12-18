package org.gcube.dataanalysis.executor.tests;

import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;
import org.gcube.dataanalysis.ecoengine.interfaces.StandardLocalInfraAlgorithm;
import org.gcube.dataanalysis.executor.nodes.transducers.bionym.BionymFlexibleWorkflowTransducer;
import org.gcube.dataanalysis.executor.nodes.transducers.bionym.utils.YasmeenGlobalParameters;

public class TestEmailingSystem {

	public static void main (String args[]) throws Exception{
		AlgorithmConfiguration config = new AlgorithmConfiguration();
		config.setConfigPath("./cfg/");
		
		AnalysisLogger.setLogger(config.getConfigPath() + AlgorithmConfiguration.defaultLoggerFile);
		config.setParam("DatabaseUserName", "utente");
		config.setParam("DatabasePassword", "d4science");
		config.setParam("DatabaseURL", "jdbc:postgresql://statistical-manager.d.d4science.research-infrastructures.eu/testdb");
		config.setPersistencePath("./");
		//config.setGcubeScope("/gcube/devNext/NextNext");
//		config.setGcubeScope("/gcube/devNext/NextNext");
		config.setGcubeScope("/gcube");
		config.setParam("ServiceUserName", "gianpaolo.coro");
		config.setParam("DatabaseDriver", "org.postgresql.Driver");
		config.setGcubeUserName("gianpaolo.coro");
		config.setGcubeToken("f9d49d76-cd60-48ed-9f8e-036bcc1fc045-98187548");
		
		ScopeProvider.instance.set(config.getGcubeScope());
		StandardLocalInfraAlgorithm infraAlg = new StandardLocalInfraAlgorithm() {
			
			@Override
			public void shutdown() {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			protected void setInputParameters() {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			protected void process() throws Exception {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void init() throws Exception {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public String getDescription() {
				// TODO Auto-generated method stub
				return null;
			}
		};
		infraAlg.setConfiguration(config);
		infraAlg.sendNotification("hello&ernrinndnknd","test++èèééé222");
		
		
	}
	


}

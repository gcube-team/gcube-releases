package org.gcube.dataanalysis.executor.tests;

import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;
import org.gcube.dataanalysis.ecoengine.interfaces.ComputationalAgent;

public class CustomRegressor {

	
	public static void process(ComputationalAgent agent) throws Exception {

		if (agent != null) {
			CustomRegressor tgs = new CustomRegressor();
			ThreadCalculator tc = tgs.new ThreadCalculator(agent);
			Thread t = new Thread(tc);
			t.start();
			while (agent.getStatus() < 100) {

				String resLoad = agent.getResourceLoad();
				String ress = agent.getResources();
				
				System.out.println("LOAD: " + resLoad);
				System.out.println("RESOURCES: " + ress);
				System.out.println("STATUS: " + agent.getStatus());
				
				Thread.sleep(10000);
//				agent.shutdown();
			}
		} else
			System.out.println("Generator Algorithm Not Supported" );

		System.out.println("-|"+agent.getStatus()); 
	}

	public class ThreadCalculator implements Runnable {
		ComputationalAgent dg;

		public ThreadCalculator(ComputationalAgent dg) {
			this.dg = dg;
		}

		public void run() {
			try {

				dg.compute();

			} catch (Exception e) {
			}
		}

	}
	
	public static AlgorithmConfiguration getConfig() {
		
		AlgorithmConfiguration config = new AlgorithmConfiguration();
		
		config.setConfigPath("./cfg/");
		config.setPersistencePath("./");
		/*
		config.setParam("DatabaseUserName","gcube");
		config.setParam("DatabasePassword","d4science2");
		config.setParam("DatabaseURL","jdbc:postgresql://146.48.87.169/testdb");
		*/
		config.setParam("DatabaseUserName","utente");
		config.setParam("DatabasePassword","d4science");
		config.setParam("DatabaseURL","jdbc:postgresql://statistical-manager.d.d4science.research-infrastructures.eu/testdb");
		
		config.setParam("DatabaseDriver","org.postgresql.Driver");
		AnalysisLogger.setLogger(config.getConfigPath()+AlgorithmConfiguration.defaultLoggerFile);
		return config;
	}
}

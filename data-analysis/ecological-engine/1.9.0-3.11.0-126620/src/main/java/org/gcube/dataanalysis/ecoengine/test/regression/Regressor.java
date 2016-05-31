package org.gcube.dataanalysis.ecoengine.test.regression;

import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;
import org.gcube.dataanalysis.ecoengine.interfaces.ComputationalAgent;

public class Regressor {

	
	public static void process(ComputationalAgent agent) throws Exception {

		if (agent != null) {
			Regressor tgs = new Regressor();
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
		config.setParam("DatabaseUserName","gcube");
		config.setParam("DatabasePassword","d4science2");
		config.setParam("DatabaseURL","jdbc:postgresql://146.48.87.169/testdb");
		config.setParam("DatabaseDriver","org.postgresql.Driver");
		AnalysisLogger.setLogger(config.getConfigPath()+AlgorithmConfiguration.defaultLoggerFile);
		return config;
	}
}

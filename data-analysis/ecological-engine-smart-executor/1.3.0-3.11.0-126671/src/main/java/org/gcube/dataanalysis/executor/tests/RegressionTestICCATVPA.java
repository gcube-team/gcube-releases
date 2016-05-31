package org.gcube.dataanalysis.executor.tests;

import java.util.List;

import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;
import org.gcube.dataanalysis.ecoengine.interfaces.ComputationalAgent;
import org.gcube.dataanalysis.ecoengine.processing.factories.GeneratorsFactory;
import org.gcube.dataanalysis.ecoengine.test.regression.Regressor;
import org.gcube.dataanalysis.ecoengine.utils.IOHelper;

public class RegressionTestICCATVPA {
	/**
	 * example of parallel processing on a single machine the procedure will generate a new table for a distribution on suitable species
	 * 
	 */

public static AlgorithmConfiguration getConfig() {
		
		AlgorithmConfiguration config = new AlgorithmConfiguration();
		
		config.setConfigPath("./cfg/");
		config.setPersistencePath("./");
		config.setParam("DatabaseUserName","utente");
		config.setParam("DatabasePassword","d4science");
		config.setParam("DatabaseURL","jdbc:postgresql://dbtest.research-infrastructures.eu/testdb");
		config.setParam("DatabaseDriver","org.postgresql.Driver");
		AnalysisLogger.setLogger(config.getConfigPath()+AlgorithmConfiguration.defaultLoggerFile);
		return config;
	}

	public static void main(String[] args) throws Exception {

		System.out.println("TEST 1");

		List<ComputationalAgent> generators = GeneratorsFactory.getGenerators(testICCATVPA());
		generators.get(0).init();
		CustomRegressor.process(generators.get(0));
		generators = null;

	}
	
	private static AlgorithmConfiguration testICCATVPA() {

		AlgorithmConfiguration config = getConfig();
		config.setNumberOfResources(5);
		config.setModel("ICCAT_VPA");

		config.setParam("UserName", "gianpaolo.coro");
		config.setGcubeScope("/gcube/devsec");
		config.setParam("ServiceUserName", "gianpaolo.coro");
		
		config.setParam("StartYear","1950");
		config.setParam("EndYear","2013");
		config.setParam("CAAFile","CAA_Age1_25.csv");
		config.setParam("PCAAFile","PCAA_Age1_25_Run3.csv");
		config.setParam("CPUEFile","CPUE_Run3.csv");
		config.setParam("PwaaFile","waa.csv");
		config.setParam("waaFile","fecaa.csv");
		
		config.setParam("shortComment","split JP_LL NEAST and wihtout last 1 year in ESPMARTrap");
		config.setParam("nCPUE","8");
		config.setParam("CPUE_cut","1");
		config.setParam("n_remove_year","1");
		config.setParam("age_plus_group","10");
		
		return config;
	}
}

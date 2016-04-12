package org.gcube.dataanalysis.ecoengine.test;

import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;
import org.gcube.dataanalysis.ecoengine.user.GeneratorT;
import org.gcube.dataanalysis.ecoengine.user.ModelerT;

public class PerformanceTests {

	public static void main(String[] args) throws Exception {
		String configPath = "./cfg/";
		String csquareTable = "hcaf_d";
		String preprocessedTable = "maxminlat_hspen";
		String envelopeTable = "hspen_mini_1";
		int numberOfResources = 1;
		String speciesCode = "Fis-22747";
		String userName = "gianpaolo.coro";
		String generatorName = "AQUAMAPS_SUITABLE";

		String finalDistributionTable = "hspec_suitable_local";

		// Generate
		long t0 = System.currentTimeMillis();
		// Generate
		AlgorithmConfiguration config = GeneratorT.getGenerationConfig(numberOfResources, generatorName, envelopeTable, preprocessedTable, "", userName, csquareTable, finalDistributionTable, configPath);
		config.setPersistencePath("./");
		config.setParam("ServiceUserName", "gianpaolo.coro");
		config.setParam("DatabaseUserName","utente");
		config.setParam("DatabasePassword","d4science");
		config.setParam("DatabaseURL","jdbc:postgresql://dbtest.research-infrastructures.eu/aquamapsorgupdated");
		config.setParam("DatabaseDriver","org.hibernate.dialect.PostgreSQLDialect");
				
		GeneratorT.generate(config);
		
		System.out.println("OVERALL ELAPSED: "+(System.currentTimeMillis()-t0));
	}

}

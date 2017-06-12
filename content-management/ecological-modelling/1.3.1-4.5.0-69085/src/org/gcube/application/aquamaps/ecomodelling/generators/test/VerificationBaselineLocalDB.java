package org.gcube.application.aquamaps.ecomodelling.generators.test;

import org.gcube.application.aquamaps.ecomodelling.generators.configuration.EngineConfiguration;
import org.gcube.application.aquamaps.ecomodelling.generators.processing.DistributionGenerator;

public class VerificationBaselineLocalDB {
	/**
	 * example of parallel processing on a single machine
	 * the procedure will generate a new table for a distribution on suitable species
	 *  
	 */
	
	public static void main(String[] args) throws Exception{

		EngineConfiguration e = new EngineConfiguration();
		//path to the cfg directory containing default parameters
		e.setConfigPath("./cfg/");
		e.setDatabaseUserName("gcube");
		e.setDatabasePassword("d4science2");
		e.setDatabaseURL("jdbc:postgresql://localhost/testdb");
		e.setHcafTable("hcaf_d");
		e.setHspenTable("hspen_micro");
		e.setDistributionTable("hspec_validation_automatic_micro");
		e.setNativeGeneration(false);
		e.setType2050(false);
		e.setNumberOfThreads(2);
		e.setCreateTable(true);
		e.setTableStore("tablesp_2");
		
		DistributionGenerator dg = new DistributionGenerator(e);
		dg.generateHSPEC();
	}
}

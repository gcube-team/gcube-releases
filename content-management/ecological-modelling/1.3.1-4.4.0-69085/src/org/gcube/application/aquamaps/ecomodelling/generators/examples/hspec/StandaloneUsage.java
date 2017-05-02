package org.gcube.application.aquamaps.ecomodelling.generators.examples.hspec;

import org.gcube.application.aquamaps.ecomodelling.generators.configuration.EngineConfiguration;
import org.gcube.application.aquamaps.ecomodelling.generators.processing.DistributionGenerator;

public class StandaloneUsage {

	
	/**
	 * This example sequentially generates distributions for hspec suitable, native, 2050 suitable and 2050 native
	 * the calculation is performed by the library, basing on the reference DB
	 * Species list is taken by the species.txt configuration file
	 * if this file is not present, species list is taken from the indicated hspen table
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception{
		
		DistributionGenerator dg; 
		
		//SET UP OF THE CONFIGURATION OBJECT
		EngineConfiguration e = new EngineConfiguration();
		//path to the configuration directory
		e.setConfigPath("./cfg/");
		//remote db username (default defined in the configuration)
		e.setDatabaseUserName("utente");
		//remote db password (default defined in the configuration)
		e.setDatabasePassword("d4science");
		//remote db URL (default defined in the configuration)
		e.setDatabaseURL("jdbc:postgresql://dbtest.research-infrastructures.eu/aquamapsorgupdated");
		//number of threads to use in the calculation
		e.setNumberOfThreads(2);
		//create table if it doesn't exist
		e.setCreateTable(true);
		//hspen reference table
		e.setHspenTable("hspen");

		//EXAMPLE OF "HSPEC SUITABLE" GENERATION
		//hcaf reference table
		e.setHcafTable("hcaf_d");
		//output table - created if the CreateTable flag is true
		e.setDistributionTable("hspec_suitable_standalone");
		//native generation flag set to false - default value
		e.setNativeGeneration(false);
		//2050 generation flag set to false - default value
		e.setType2050(false);
		//generator object setup
		dg = new DistributionGenerator(e);
		//calculation
		dg.generateHSPEC();
		
		//EXAMPLE OF "HSPEC 2050 SUITABLE" GENERATION
		e.setHcafTable("hcaf_d_2050");
		e.setDistributionTable("hspec_2050_suitable_standalone");
		e.setNativeGeneration(false);
		e.setType2050(true);
		dg = new DistributionGenerator(e);
		dg.generateHSPEC();
		
		//EXAMPLE OF "HSPEC NATIVE" GENERATION
		e.setHcafTable("hcaf_d");
		e.setDistributionTable("hspec_native_standalone");
		e.setNativeGeneration(true);
		e.setType2050(false);
		dg = new DistributionGenerator(e);
		dg.generateHSPEC();
		
		//EXAMPLE OF "HSPEC 2050 NATIVE" GENERATION
		e.setHcafTable("hcaf_d_2050");
		e.setDistributionTable("hspec_2050_native_standalone");
		e.setNativeGeneration(true);
		e.setType2050(true);
		dg = new DistributionGenerator(e);
		dg.generateHSPEC();

	}
}

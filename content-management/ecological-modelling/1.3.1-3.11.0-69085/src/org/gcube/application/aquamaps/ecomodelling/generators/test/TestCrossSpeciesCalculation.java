package org.gcube.application.aquamaps.ecomodelling.generators.test;

import org.gcube.application.aquamaps.ecomodelling.generators.configuration.EngineConfiguration;
import org.gcube.application.aquamaps.ecomodelling.generators.processing.DistributionGenerator;
import org.gcube.application.aquamaps.ecomodelling.generators.processing.experiments.CrossSpeciesCalculator;
import org.gcube.application.aquamaps.ecomodelling.generators.processing.experiments.CrossSpeciesGenerator;

public class TestCrossSpeciesCalculation {
	/**
	 * example of parallel processing on a single machine
	 * the procedure will generate a new table for a distribution on suitable species
	 *  
	 */
	
	public static void main(String[] args) throws Exception{

		EngineConfiguration e = new EngineConfiguration();
		//path to the cfg directory containing default parameters
		e.setConfigPath("./cfg/");
		e.setDatabaseUserName("utente");
		e.setDatabasePassword("d4science");
		e.setDatabaseURL("jdbc:postgresql://dbtest.research-infrastructures.eu/aquamapsorgupdatedOLD");
		e.setHcafTable("hcaf_d");
		e.setHspenTable("hspen");
		e.setDistributionTable("hspec_suitable");
		String crossSpeciesTable = "crossspecies";
		CrossSpeciesCalculator csg = new CrossSpeciesCalculator(e);
		csg.generateCrossSpeciesMap("hspen","hspec_suitable");
	}
}

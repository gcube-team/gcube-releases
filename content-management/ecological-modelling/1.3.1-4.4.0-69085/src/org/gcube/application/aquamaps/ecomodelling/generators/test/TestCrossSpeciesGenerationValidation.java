package org.gcube.application.aquamaps.ecomodelling.generators.test;

import org.gcube.application.aquamaps.ecomodelling.generators.configuration.EngineConfiguration;
import org.gcube.application.aquamaps.ecomodelling.generators.processing.DistributionGenerator;
import org.gcube.application.aquamaps.ecomodelling.generators.processing.experiments.CrossSpeciesGenerator;

public class TestCrossSpeciesGenerationValidation {
	
	//validation of cross species generation
	public static void main(String[] args) throws Exception{

		EngineConfiguration e = new EngineConfiguration();
		//path to the cfg directory containing default parameters
		e.setConfigPath("./cfg/");
		e.setDatabaseUserName("utente");
		e.setDatabasePassword("d4science");
		e.setDatabaseURL("jdbc:postgresql://dbtest.research-infrastructures.eu/aquamapsorgupdated");
		e.setHcafTable("hcaf_d");
		e.setHspenTable("hspen");
		e.setDistributionTable("hcaf_species_native_unreviewed");
		String crossSpeciesTable = "crossspecies_nonreviewed";
		CrossSpeciesGenerator csg = new CrossSpeciesGenerator();
		csg.generateCrossSpeciesTable(e, crossSpeciesTable);
		
	}
}

package org.gcube.application.aquamaps.ecomodelling.generators.examples.hspen;

import java.util.ArrayList;

import org.gcube.application.aquamaps.ecomodelling.generators.configuration.EngineConfiguration;
import org.gcube.application.aquamaps.ecomodelling.generators.connectors.EnvelopeModel;
import org.gcube.application.aquamaps.ecomodelling.generators.processing.EnvelopeGenerator;

public class StandaloneUsage {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception{
		//CONFIGURATION
		//setup a configuration for the service
		EngineConfiguration e = new EngineConfiguration();
		//path to the cfg directory containing default parameters
		e.setConfigPath("./cfg/");
		//database information
		e.setDatabaseUserName("utente");
		e.setDatabasePassword("d4science");
		e.setDatabaseURL("jdbc:postgresql://dbtest.research-infrastructures.eu/aquamapsorgupdated");
		//..set other DB parameters if you want
		//set the reference hcaf table
		e.setHcafTable("hcaf_d");
		//set the new hspen table you want to generate 
		e.setHspenTable("hspen_new");
		//set the original hspen table you want to regenerate
		e.setOriginHspenTable("hspen");
		//set the occurrence cells table name
		e.setOccurrenceCellsTable("occurrencecells");
		//set the HSpen generation model youwant to apply
		e.setEnvelopeGenerator(EnvelopeModel.AQUAMAPS);
		//set the number of thread for the computation
		e.setNumberOfThreads(2);
		//the table will be created if it doesn't exist yet
		e.setCreateTable(true);
		
		//EXAMPLE 1 - "Blind" generation from the DB

		EnvelopeGenerator eg = new EnvelopeGenerator(e);
		//species list will be extracted from an internal config file or from the database (as second choice)
		eg.reGenerateEnvelopes(); //output will be the hspen_new table on the database
		//EXAMPLE 1 END
		
		//EXAMPLE 2 - Species List suggested from outside		

		EnvelopeGenerator eg2 = new EnvelopeGenerator(e);
		//species list is passed from outside
		ArrayList<String> speciesList = new ArrayList<String>();
		speciesList.add("Fis-116939");
		//give the list to the generation model
		eg2.setSelectedSpecies(speciesList);
		//generation based on the outside list
		eg2.reGenerateEnvelopes(); //output will be the hspen_new table on the database
		//EXAMPLE 2 END
		
	}

}

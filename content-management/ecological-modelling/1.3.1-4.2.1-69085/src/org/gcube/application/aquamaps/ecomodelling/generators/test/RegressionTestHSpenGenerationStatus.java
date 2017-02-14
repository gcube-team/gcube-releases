package org.gcube.application.aquamaps.ecomodelling.generators.test;

import org.gcube.application.aquamaps.ecomodelling.generators.configuration.EngineConfiguration;
import org.gcube.application.aquamaps.ecomodelling.generators.connectors.EnvelopeModel;
import org.gcube.application.aquamaps.ecomodelling.generators.processing.EnvelopeGenerator;

public class RegressionTestHSpenGenerationStatus {

	
	public static void main(String[] args) throws Exception{

		RegressionTestHSpenGenerationStatus tgs = new RegressionTestHSpenGenerationStatus();
		
		EngineConfiguration e = new EngineConfiguration();
		//path to the cfg directory containing default parameters
		e.setConfigPath("./cfg/");
		e.setDatabaseUserName("gcube");
		e.setDatabasePassword("d4science2");
		e.setDatabaseURL("jdbc:postgresql://localhost/testdb");
		//reference hcaf table
		e.setHcafTable("hcaf_d");
		//destination hspen table
		e.setHspenTable("hspen_test");
		//origin hspen table
		e.setOriginHspenTable("hspen");
		//occurrence cells table
		e.setOccurrenceCellsTable("occurrencecells");
		//envelope algorithm to use
		e.setEnvelopeGenerator(EnvelopeModel.AQUAMAPS);
		//number of threads to use
		e.setNumberOfThreads(16);
		//set this to true if you want the procedure to generate the table autonomously
		e.setCreateTable(true);
		
		EnvelopeGenerator eg = new EnvelopeGenerator(e);
		
		ThreadCalculator tc = tgs.new ThreadCalculator(eg);
		
		Thread t = new Thread(tc);
		t.start();
		
		while (eg.getStatus()<100.00){
		//get status
			System.out.println("STATUS: "+eg.getStatus());
			System.out.println("LOAD: "+eg.getResourceLoad());
			System.out.println("SPECIES LOAD: "+eg.getSpeciesLoad());
			System.out.println("RESOURCES: "+eg.getResources());
			Thread.sleep(4000);
		}
		
	}
	
	
	
	public class ThreadCalculator implements Runnable {
		EnvelopeGenerator eg ;
		public ThreadCalculator(EnvelopeGenerator dg) {
			this.eg = dg; 
		}

		public void run() {
			try{
				//execute generation
				eg.reGenerateEnvelopes();
				
			}catch(Exception e){}
		}

	}
	
	
}

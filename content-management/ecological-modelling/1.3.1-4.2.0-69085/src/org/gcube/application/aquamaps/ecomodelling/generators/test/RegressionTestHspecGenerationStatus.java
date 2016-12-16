package org.gcube.application.aquamaps.ecomodelling.generators.test;

import java.io.File;
import java.io.FileWriter;

import org.gcube.application.aquamaps.ecomodelling.generators.configuration.EngineConfiguration;
import org.gcube.application.aquamaps.ecomodelling.generators.connectors.GenerationModel;
import org.gcube.application.aquamaps.ecomodelling.generators.processing.DistributionGenerator;

public class RegressionTestHspecGenerationStatus {
	/**
	 * example of parallel processing on a single machine
	 * the procedure will generate a new table for a distribution on suitable species
	 *  
	 */
	
	public static void main(String[] args) throws Exception{

		RegressionTestHspecGenerationStatus tgs = new RegressionTestHspecGenerationStatus();
		
		EngineConfiguration e = new EngineConfiguration();
		//path to the cfg directory containing default parameters
		e.setConfigPath("./cfg/");
		
		e.setHcafTable("hcaf_d");
		e.setHspenTable("hspen");
		e.setDistributionTable("hspec_suitable_automatic_3");
		
		e.setNativeGeneration(false);
		e.setType2050(false);
		e.setCreateTable(true);
		e.setNumberOfThreads(10);
		
		e.setDatabaseUserName("gcube");
		e.setDatabasePassword("d4science2");
		e.setDatabaseURL("jdbc:postgresql://localhost/testdb");

		e.setCachePath("/tmp/");
		e.setWriteSummaryLog(false);
		
		e.setGenerator(GenerationModel.AQUAMAPS);
		
		DistributionGenerator dg = new DistributionGenerator(e);
		
		ThreadCalculator tc = tgs.new ThreadCalculator(dg);
		
		Thread t = new Thread(tc);
		t.start();
		String pathResources = "/mnt/win/resources.txt";
		String pathLoad = "/mnt/win/resourceLoad.txt";
		
		while (dg.getStatus()<100.00){

			String resLoad = dg.getResourceLoad();
			String ress = dg.getResources();
			String species = dg.getSpeciesLoad();
			
			System.out.println("STATUS: "+dg.getStatus());
			System.out.println("LOAD: "+resLoad);
			System.out.println("RESOURCES: "+ress);
			System.out.println("SPECIES: "+species);
			Thread.sleep(3000);
		}
		
	}
	
	
	
	public class ThreadCalculator implements Runnable {
		DistributionGenerator dg ;
		public ThreadCalculator(DistributionGenerator dg) {
			this.dg = dg; 
		}

		public void run() {
			try{
				
				dg.generateHSPEC();
				
			}catch(Exception e){}
		}

	}
}

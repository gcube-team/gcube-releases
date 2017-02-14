package org.gcube.application.aquamaps.ecomodelling.generators.test;

import java.io.File;
import java.io.FileWriter;

import org.gcube.application.aquamaps.ecomodelling.generators.configuration.EngineConfiguration;
import org.gcube.application.aquamaps.ecomodelling.generators.connectors.GenerationModel;
import org.gcube.application.aquamaps.ecomodelling.generators.processing.DistributionGenerator;

public class TestGenerationbyCopy {
	/**
	 * example of parallel processing on a single machine
	 * the procedure will generate a new table for a distribution on suitable species
	 *  
	 */
	
	public static void main(String[] args) throws Exception{

		TestGenerationbyCopy tgs = new TestGenerationbyCopy();
		
		EngineConfiguration e = new EngineConfiguration();
		//path to the cfg directory containing default parameters
		e.setConfigPath("./cfg/");
		
		e.setHcafTable("hcaf_d");
		e.setHspenTable("hspen");
		e.setDistributionTable("hspec_suitable_automatic_4");
		
		e.setNativeGeneration(false);
		e.setType2050(false);
		e.setCreateTable(true);
		e.setNumberOfThreads(16);
		e.setDatabaseUserName("utente");
		e.setDatabasePassword("d4science");
		e.setDatabaseURL("jdbc:postgresql://dbtest.research-infrastructures.eu/aquamapsorgupdated");
		e.setCachePath("/tmp/");
		e.setGenerator(GenerationModel.AQUAMAPS);
		
		DistributionGenerator dg = new DistributionGenerator(e);
		
		ThreadCalculator tc = tgs.new ThreadCalculator(dg);
		
		Thread t = new Thread(tc);
		
		t.start();
		
		while (dg.getStatus()<99.00){
			System.out.println("STATUS: "+dg.getStatus());
			String resLoad = dg.getResourceLoad();
			String ress = dg.getResources();
			System.out.println("LOAD: "+resLoad);
			System.out.println("RESOURCES: "+ress);
			
			Thread.sleep(500);
		}
		
		System.out.println("COMPUTATION FINISHED!");
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

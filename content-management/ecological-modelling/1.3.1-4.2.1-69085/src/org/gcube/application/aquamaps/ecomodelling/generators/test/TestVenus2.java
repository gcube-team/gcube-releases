package org.gcube.application.aquamaps.ecomodelling.generators.test;

import java.util.HashMap;

import org.gcube.application.aquamaps.ecomodelling.generators.configuration.EngineConfiguration;
import org.gcube.application.aquamaps.ecomodelling.generators.connectors.GenerationModel;
import org.gcube.application.aquamaps.ecomodelling.generators.processing.DistributionGenerator;
import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;

public class TestVenus2 {
	/**
	 * example of parallel processing on a single machine
	 * the procedure will generate a new table for a distribution on suitable species
	 *  
	 */
	
	public static void main(String[] args) throws Exception{

		TestVenus2 tgs = new TestVenus2();
		
		EngineConfiguration e = new EngineConfiguration();
		//path to the cfg directory containing default parameters
		e.setConfigPath("./cfg/");
		
		e.setHcafTable("hcaf_d");
		e.setHspenTable("hspen");
		e.setDistributionTable("hspec_suitable_automatic_2");
		
		e.setNativeGeneration(false);
		e.setType2050(false);
		e.setNumberOfThreads(3);
		
		e.setRemoteCalculator("http://node2.d.d4science.research-infrastructures.eu:8080/rainycloud-1.02.04/");
		e.setServiceUserName("gianpaolo.coro");
		
		e.setDatabaseUserName("utente");
		e.setDatabasePassword("d4science");
		e.setDatabaseURL("jdbc:postgresql://dbtest.research-infrastructures.eu/aquamapsorgupdated");
		
		//new parameters
		e.setRemoteEnvironment("Venus Infrastructure");
		HashMap<String,String> properties = new HashMap<String, String>();
		properties.put("property1", "value1");
		properties.put("property2", "value2");
		e.setGeneralProperties(properties);
		//

		e.setGenerator(GenerationModel.REMOTE_AQUAMAPS);
		
		DistributionGenerator dg = new DistributionGenerator(e);
		
		ThreadCalculator tc = tgs.new ThreadCalculator(dg);
		
		Thread t = new Thread(tc);
		t.start();
		
		
		while (dg.getStatus()<99.00){
			
			AnalysisLogger.getLogger().debug("OVERALL STATUS: "+dg.getStatus());
			AnalysisLogger.getLogger().debug("LOAD: "+dg.getResourceLoad());
			AnalysisLogger.getLogger().debug("RESOURCES: "+dg.getResources());
//			Thread.sleep(5000);
		
			//if we wanto to stop process invoke:
//			dg.stopProcess();
			Thread.sleep(1000);
		}
		
		AnalysisLogger.getLogger().debug("FINAL STATUS: "+dg.getStatus());
	}
	
	
	public class ThreadCalculator implements Runnable {
		DistributionGenerator dg ;
		public ThreadCalculator(DistributionGenerator dg) {
			this.dg = dg; 
		}

		public void run() {
			try{
				
				dg.generateHSPEC();
				
			}catch(Exception e){e.printStackTrace();}
		}

	}
	
	
}

package org.gcube.dataanalysis.executor.tests;

import java.util.List;

import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;
import org.gcube.dataanalysis.ecoengine.interfaces.ComputationalAgent;
import org.gcube.dataanalysis.ecoengine.interfaces.Generator;
import org.gcube.dataanalysis.ecoengine.processing.factories.GeneratorsFactory;

public class TestGenerator implements Runnable{
	ComputationalAgent dg;
	
	public TestGenerator(ComputationalAgent dg) {
		this.dg = dg;
	}

	public void run() {
		try {

			dg.compute();

		} catch (Exception e) {
		}
	}

	
	public static void generate(AlgorithmConfiguration config) throws Exception {

		List<ComputationalAgent> generators = GeneratorsFactory.getGenerators(config);
		ComputationalAgent generator = generators.get(0); 

		if (generator != null) {
			generator.init();
			TestGenerator tgs = new TestGenerator(generator);
			Thread t = new Thread(tgs);
			t.start();
			while (generator.getStatus() < 100) {

				String resLoad = generator.getResourceLoad();
				String ress = generator.getResources();
//				String species = generator.getLoad();
				System.out.println("LOAD: " + resLoad);
				System.out.println("RESOURCES: " + ress);
//				System.out.println("SPECIES: " + species);
				System.out.println("STATUS: " + generator.getStatus());
				Thread.sleep(1000);
			}
		} 
		else
			System.out.println("Generator Algorithm Not Supported");
	}
	
	
	public static AlgorithmConfiguration getGenerationConfig(int numberOfResources,String algorithmName,String envelopeTable,String preprocessedTable,String speciesName,String userName,String csquareTable,String finalDistributionTable,String configPath) {
		AlgorithmConfiguration config = new AlgorithmConfiguration();
		config.setConfigPath(configPath);
		config.setPersistencePath(configPath);
		config.setNumberOfResources(numberOfResources);
		config.setModel(algorithmName);
		
		config.setParam("EnvelopeTable", envelopeTable);
		config.setParam("PreprocessedTable", preprocessedTable);
		
		config.setParam("SpeciesName", speciesName);
		config.setParam("CsquarecodesTable", csquareTable);
		config.setParam("DistributionTable", finalDistributionTable);
		config.setParam("CreateTable", "true");
		config.setParam("UserName",userName);
		
		return config;
	}

	

}

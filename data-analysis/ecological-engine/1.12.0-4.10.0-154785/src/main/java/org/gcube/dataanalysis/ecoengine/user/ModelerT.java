package org.gcube.dataanalysis.ecoengine.user;

import java.util.List;

import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;
import org.gcube.dataanalysis.ecoengine.interfaces.ComputationalAgent;
import org.gcube.dataanalysis.ecoengine.interfaces.Modeler;
import org.gcube.dataanalysis.ecoengine.processing.factories.ModelersFactory;

public class ModelerT implements Runnable {

	ComputationalAgent dg;
	AlgorithmConfiguration config;

	public ModelerT(ComputationalAgent dg, AlgorithmConfiguration config) {
		this.dg = dg;
		this.config = config;
	}

	public void run() {
		try {

			dg.compute();

		} catch (Exception e) {
		}
	}

	public static void train(AlgorithmConfiguration config) throws Exception {

		List<ComputationalAgent> modelers = ModelersFactory.getModelers(config);
		ComputationalAgent modeler = modelers.get(0);

		if (modeler != null) {
			ModelerT tc = new ModelerT(modeler, config);
			Thread t = new Thread(tc);
			t.start();
			while (modeler.getStatus() < 100) {

				String resLoad = modeler.getResourceLoad();
				String ress = modeler.getResources();
				System.out.println("LOAD: " + resLoad);
				System.out.println("RESOURCES: " + ress);
				System.out.println("STATUS: " + modeler.getStatus());
				Thread.sleep(1000);
			}
			System.out.println("FINAL STATUS: " + modeler.getStatus());
		} else
			System.out.println("Generator Algorithm Not Supported");

	}

	public static AlgorithmConfiguration getTrainingConfigHSPEN(String modelName, String outputTable, String occurrenceCells, String hspenOrigin, String hcaf, String configPath) {

		AlgorithmConfiguration config = new AlgorithmConfiguration();
		config.setConfigPath(configPath);
		config.setPersistencePath("./");
		config.setNumberOfResources(4);
		config.setModel("HSPEN");

		config.setParam("OuputEnvelopeTable", outputTable);
		config.setParam("OccurrenceCellsTable", occurrenceCells);
		config.setParam("EnvelopeTable", hspenOrigin);
		config.setParam("CsquarecodesTable", hcaf);
		config.setParam("CreateTable", "true");

		return config;
	}

	public static AlgorithmConfiguration getTrainingConfig(String modelName, String absenceTable, String presenceTable, String speciesCode, String userName, String layersNeurons, String configPath) {

		AlgorithmConfiguration config = new AlgorithmConfiguration();
		config.setConfigPath(configPath);
		config.setPersistencePath(configPath);
		config.setNumberOfResources(1);

		config.setModel(modelName);
		config.setParam("AbsenceDataTable", absenceTable);
		config.setParam("PresenceDataTable", presenceTable);

		config.setParam("SpeciesName", speciesCode);
		config.setParam("UserName", userName);
		config.setParam("LayersNeurons", layersNeurons);

		return config;
	}

}

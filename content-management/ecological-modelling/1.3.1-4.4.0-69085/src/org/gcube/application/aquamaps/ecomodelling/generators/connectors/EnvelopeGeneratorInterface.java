package org.gcube.application.aquamaps.ecomodelling.generators.connectors;

import java.util.ArrayList;
import java.util.List;

import org.gcube.application.aquamaps.ecomodelling.generators.configuration.EngineConfiguration;
import org.gcube.application.aquamaps.ecomodelling.generators.connectors.subconnectors.OccurrencePointSets;
import org.gcube.application.aquamaps.ecomodelling.generators.processing.EnvelopeGenerator;

public class EnvelopeGeneratorInterface {

	double status;
	EnvelopeModel model;
	String configPath;

	/**
	 * initializes the calculator
	 * 
	 * @param model
	 * @param configPath
	 */
	public EnvelopeGeneratorInterface(EnvelopeModel model, String configPath) {
		status = 0;
		this.model = model;
		this.configPath = configPath;
	}

	/**
	 * gets the status in percentage of the current calculation
	 * 
	 * @return
	 */
	public double getStatus() {
		return status;
	}

	/**
	 * recalculates the Hspen given a previous Hspen and a database configuration, for a single species it needs the configuration to specify the occurrencecells and hcaf tables;
	 * 
	 * @param configuration
	 * @param species
	 * @param previousHspen
	 * @return
	 */
	public Hspen reCalculateEnvelope(EngineConfiguration configuration, Hspen previousHspen) {
		Hspen newHspen = null;

		try {
			configuration.setConfigPath(configPath);
			configuration.setUseDB(true);
			configuration.setEnvelopeGenerator(model);

			EnvelopeGenerator eg = new EnvelopeGenerator(configuration);
			newHspen = eg.calcEnvelopes(previousHspen, configuration);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return newHspen;
	}

	/**
	 * recalculates the hspen for the given hspen list, according to a database configuration
	 * 
	 * @param configuration
	 * @param previousHspenList
	 * @return
	 */
	public List<Hspen> reCalculateEnvelope(EngineConfiguration configuration, List<Hspen> previousHspenList) {

		ArrayList<Hspen> hspenArray = new ArrayList<Hspen>();

		try {
			int size = previousHspenList.size();
			int i = 0;

			configuration.setConfigPath(configPath);
			configuration.setUseDB(true);
			configuration.setEnvelopeGenerator(model);

			for (Hspen previousHspen : previousHspenList) {
				hspenArray.add(reCalculateEnvelope(configuration, previousHspen));
				status = Double.valueOf(i) / Double.valueOf(size);
				i++;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return hspenArray;

	}

	/**
	 * recalculates the hspen for the given set of values, based on a previous hspen
	 * 
	 * @param previousHspen
	 * @param occPointLists
	 * @return
	 */
	public Hspen reCalculateEnvelope(Hspen previousHspen, OccurrencePointSets occPointLists) {

		Hspen newHspen = null;
		try {
			EngineConfiguration configuration = new EngineConfiguration();
			configuration.setConfigPath(configPath);
			configuration.setUseDB(false);
			configuration.setCreateTable(false);
			configuration.setEnvelopeGenerator(model);
			EnvelopeGenerator eg = new EnvelopeGenerator(configuration);
			newHspen = eg.calcEnvelopes(previousHspen, occPointLists);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return newHspen;

	}

}

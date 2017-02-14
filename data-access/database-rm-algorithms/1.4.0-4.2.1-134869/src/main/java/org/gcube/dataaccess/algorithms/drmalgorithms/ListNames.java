package org.gcube.dataaccess.algorithms.drmalgorithms;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;

import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.dataaccess.databases.access.DatabasesDiscoverer;
import org.gcube.dataaccess.databases.resources.DBResource;
import org.gcube.dataanalysis.ecoengine.datatypes.PrimitiveType;
import org.gcube.dataanalysis.ecoengine.datatypes.StatisticalType;
import org.gcube.dataanalysis.ecoengine.datatypes.enumtypes.PrimitiveTypes;
import org.gcube.dataanalysis.ecoengine.interfaces.StandardLocalExternalAlgorithm;
import org.gcube.resources.discovery.client.api.DiscoveryException;
import org.gcube.resources.discovery.client.api.InvalidResultException;

/** Class that allows to retrieve a list of database resources */
public class ListNames extends StandardLocalExternalAlgorithm {

	private static final String RESOURCE_NAME = "Resource Name ";
	private LinkedHashMap<String, StatisticalType> map = new LinkedHashMap<String, StatisticalType>();
	
	@Override
	public void init() throws Exception {

		AnalysisLogger.getLogger().debug("In ListNames->Initialization");

		String scope = config.getGcubeScope();

		AnalysisLogger.getLogger().debug(
				"In ListNames->scope set by config object: " + scope);

		if (scope == null || scope.length() == 0) {

			scope = ScopeProvider.instance.get();

			AnalysisLogger.getLogger().debug(
					"In ListNames->scope set by ScopeProvider: " + scope);

		} else {

			ScopeProvider.instance.set(scope);

		}
	}

	@Override
	public String getDescription() {
		// add a simple description for the algorithm
		return "Algorithm that allows to view the available database resources names in the Infrastructure";

	}

	@Override
	protected void process() throws Exception {

		AnalysisLogger.getLogger().debug("In ListNames->Processing");

		String scope = ScopeProvider.instance.get();

		if (scope != null) {

			AnalysisLogger.getLogger().debug("getting scope: " + scope);

			AnalysisLogger.getLogger().debug(
					"getting scope through config: " + config.getGcubeScope());

		}

		try {
			// retrieve resources
			List<DBResource> resources = this.retrieveResources();

			// add the name to the list

			// list that contains the resource's names
			ArrayList<String> listnames = new ArrayList<String>();

			for (int i = 0; i < resources.size(); i++) {
				String name = RESOURCE_NAME+(i+1);
				PrimitiveType val = new PrimitiveType(String.class.getName(),
						null, PrimitiveTypes.STRING, name, name);
				
				AnalysisLogger.getLogger().debug(
						"In ListNames->Resource's name: "
								+ resources.get(i).getResourceName());

				listnames.add(resources.get(i).getResourceName());

				val.setContent(listnames.get(i));

				map.put(name, val);

			}

			AnalysisLogger.getLogger().debug(
					"In ListNames->Output Map Size: " + map.size());

		} catch (IllegalStateException e) {
			// e.printStackTrace();

			AnalysisLogger.getLogger().debug(
					"In ListNames-> ERROR " + e.getMessage());
			throw e;

		} catch (DiscoveryException e1) {
			// e1.printStackTrace();

			AnalysisLogger.getLogger().debug(
					"In ListNames-> ERROR " + e1.getMessage());

			throw e1;

		} catch (InvalidResultException e2) {
			// e2.printStackTrace();

			AnalysisLogger.getLogger().debug(
					"In ListNames-> ERROR " + e2.getMessage());

			throw e2;

		}

	}

	@Override
	protected void setInputParameters() {

//		AnalysisLogger.getLogger().debug("In ListNames->setting inputs");
		addStringInput("MaxNumber", "Max Number of Resources (-1 for all)",
				"-1");

	}

	@Override
	public void shutdown() {
		AnalysisLogger.getLogger().debug("In ListNames->Shutdown");

	}

	@Override
	public StatisticalType getOutput() {

		AnalysisLogger.getLogger().debug("In ListNames->retrieving outputs");

		// generate a primitive type for the collection
		PrimitiveType output = new PrimitiveType(LinkedHashMap.class.getName(),
			map, PrimitiveTypes.MAP, "ResultsMap"+UUID.randomUUID(), "Results Map");

		return output;

	}
	
	
	private List<DBResource> retrieveResources() throws IllegalStateException,
			DiscoveryException, InvalidResultException {

		AnalysisLogger.getLogger().debug("In ListNames->retrieving resources");
		List<DBResource> resources = new ArrayList<DBResource>();

		// retrieve the resources
		DatabasesDiscoverer discovery = new DatabasesDiscoverer();

		resources = discovery.discover();

		AnalysisLogger.getLogger().debug(
				"In ListNames->number of database resources: "
						+ resources.size());

		return resources;

	}

}

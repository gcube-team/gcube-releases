package org.gcube.dataaccess.algorithms.drmalgorithms;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;

import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.dataaccess.databases.access.DatabasesDiscoverer;
import org.gcube.dataaccess.databases.resources.DBResource;
import org.gcube.dataaccess.databases.utils.DatabaseManagement;
import org.gcube.dataanalysis.ecoengine.datatypes.PrimitiveType;
import org.gcube.dataanalysis.ecoengine.datatypes.StatisticalType;
import org.gcube.dataanalysis.ecoengine.datatypes.enumtypes.PrimitiveTypes;
import org.gcube.dataanalysis.ecoengine.interfaces.StandardLocalInfraAlgorithm;
import org.gcube.resources.discovery.client.api.DiscoveryException;
import org.gcube.resources.discovery.client.api.InvalidResultException;
import org.hibernate.HibernateException;

/**
 * Class that allows to perform a random sample operation on a table of a chosen
 * database. It retrieves 100 rows of a table randomly.
 */

public class RandomSampleOnTable extends StandardLocalInfraAlgorithm {

	private LinkedHashMap<String, StatisticalType> map = new LinkedHashMap<String, StatisticalType>();

	// object that allows to manage some operations on a database
	private DatabaseManagement mgt;
	// database's parameters specified by the user
	private String resourceName = null;
	private String databaseName = null;
	private String schemaName = null;
	private String tableName = null;

	// private SessionFactory sf;
	// variable that keeps track of the driver information
	private String driverInfo;

	@Override
	public void init() throws Exception {

		mgt = new DatabaseManagement(config.getConfigPath());
		AnalysisLogger.getLogger().debug(
				"In RandomSmartSampleOnTable->Initialization");

		String scope = config.getGcubeScope();
		AnalysisLogger.getLogger().debug(
				"In RandomSmartSampleOnTable->scope set by config object: "
						+ scope);

	}

	@Override
	public String getDescription() {
		return "Algorithm that allows to perform a sample operation on a table randomly";
	}

	@Override
	protected void process() throws Exception {
		AnalysisLogger.getLogger().debug(
				"In RandomSmartSampleOnTable->Processing");

		try {
			// retrieve information
			List<String> Info = retrieveInfo();

			// check on table name field
			tableName = getInputParameter("TableName");
			if (tableName != null) {
				tableName = getInputParameter("TableName").trim();
			}
			if ((tableName == null) || (tableName.equals(""))) {
				throw new Exception("Warning: insert the table name");
			}

			// check on schema name field
			if (driverInfo.toLowerCase().contains("postgres")) {
				schemaName = getInputParameter("SchemaName");
				if (schemaName != null) {
					schemaName = getInputParameter("SchemaName").trim();
				}
				if ((schemaName == null) || (schemaName.equals(""))) {
					throw new Exception("Warning: insert the schema name");
				}
			}

			// create the connection
			getConnection(Info);

			// smart sample operation on table
			map = randomSampleOnTable();

		} catch (HibernateException h) {
			AnalysisLogger.getLogger().debug(
					"In RandomSmartSampleOnTable-> ERROR " + h.getMessage());
			throw h;
		}

		catch (IllegalStateException e) {
			// e.printStackTrace();
			AnalysisLogger.getLogger().debug(
					"In RandomSmartSampleOnTable-> ERROR " + e.getMessage());
			throw e;

		} catch (DiscoveryException e1) {
			// e1.printStackTrace();
			AnalysisLogger.getLogger().debug(
					"In RandomSmartSampleOnTable-> ERROR " + e1.getMessage());
			throw e1;

		} catch (InvalidResultException e2) {
			// e2.printStackTrace();
			AnalysisLogger.getLogger().debug(
					"In RandomSmartSampleOnTable-> ERROR " + e2.getMessage());
			throw e2;

		} catch (IOException e3) {
			// e3.printStackTrace();
			AnalysisLogger.getLogger().debug(
					"In RandomSmartSampleOnTable-> ERROR " + e3.getMessage());
			throw e3;
		}

		catch (Exception e4) {
			// e4.printStackTrace();
			AnalysisLogger.getLogger().debug(
					"In RandomSmartSampleOnTable-> Exception "
							+ e4.getMessage());
			throw e4;

		} finally {
			// close the connection
			mgt.closeConnection();
		}
	}

	@Override
	protected void setInputParameters() {
		// parameters specified by the user
		addStringInput("ResourceName", "The name of the resource", "");
		addStringInput("DatabaseName", "The name of the database", "");
		addStringInput("SchemaName", "The name of the schema", "");
		addStringInput("TableName", "The name of the table", "");
	}

	public StatisticalType getOutput() {

		AnalysisLogger.getLogger().debug(
				"In RandomSampleOnTable->retrieving outputs");
		// generate a primitive type for the collection
		PrimitiveType output = new PrimitiveType(LinkedHashMap.class.getName(),
				map, PrimitiveTypes.MAP, "ResultsMap" + UUID.randomUUID(),
				"Results Map");
		return output;
	}

	@Override
	public void shutdown() {
		AnalysisLogger.getLogger().debug("In RandomSampleOnTable->Shutdown");
	}

	// Method that recovers the info useful for the connection
	private List<String> retrieveInfo() throws Exception,
			IllegalStateException, DiscoveryException, InvalidResultException {

		resourceName = getInputParameter("ResourceName");

		if (resourceName != null) {
			resourceName = getInputParameter("ResourceName").trim();
		}

		if ((resourceName == null) || (resourceName.equals(""))) {
			throw new Exception("Warning: insert the resource name");
		}

		databaseName = getInputParameter("DatabaseName");

		if (databaseName != null) {
			databaseName = getInputParameter("DatabaseName").trim();
		}
		if ((databaseName == null) || (databaseName.equals(""))) {
			throw new Exception("Warning: insert the database name");
		}

		// retrieve the chosen resource
		DatabasesDiscoverer discovery = new DatabasesDiscoverer();
		List<DBResource> resources = discovery.discover();
		AnalysisLogger.getLogger().debug(
				"In RandomSampleOnTable->number of elements: "
						+ resources.size());

		// list that contains information useful for the connection
		List<String> info = new ArrayList<String>();

		check: for (int i = 0; i < resources.size(); i++) {
			if (resources.get(i).getResourceName().toLowerCase()
					.equals(resourceName.toLowerCase())) {
				normalizeDBInfo(resources.get(i));
				for (int j = 0; j < resources.get(i).getAccessPoints().size(); j++) {

					if (resources.get(i).getAccessPoints().get(j)
							.getDatabaseName().toLowerCase()
							.equals(databaseName.toLowerCase())) {

						info.add(resources.get(i).getAccessPoints().get(j)
								.getUsername());
						AnalysisLogger.getLogger().debug(
								"In RandomSampleOnTable->username: "
										+ resources.get(i).getAccessPoints()
												.get(j).getUsername());

						info.add(resources.get(i).getAccessPoints().get(j)
								.getPassword());
						AnalysisLogger.getLogger().debug(
								"In RandomSampleOnTable->password: "
										+ resources.get(i).getAccessPoints()
												.get(j).getPassword());

						info.add(resources.get(i).getAccessPoints().get(j)
								.getDriver());
						driverInfo = resources.get(i).getAccessPoints().get(j)
								.getDriver();

						AnalysisLogger.getLogger().debug(
								"In RandomSampleOnTable->driver: "
										+ resources.get(i).getAccessPoints()
												.get(j).getDriver());

						// driverInfo =
						// resources.get(i).getAccessPoints().get(j)
						// .getDriver();

						info.add(resources.get(i).getAccessPoints().get(j)
								.getDialect());
						AnalysisLogger.getLogger().debug(
								"In RandomSampleOnTable->dialect: "
										+ resources.get(i).getAccessPoints()
												.get(j).getDialect());

						info.add(resources.get(i).getAccessPoints().get(j)
								.address());
						AnalysisLogger.getLogger().debug(
								"In RandomSampleOnTable->url: "
										+ resources.get(i).getAccessPoints()
												.get(j).address());

						info.add(databaseName);
						AnalysisLogger.getLogger().debug(
								"In RandomSampleOnTable->databasename: "
										+ resources.get(i).getAccessPoints()
												.get(j).getDatabaseName());
						break check;
					}
				}
			}
		}

		AnalysisLogger
				.getLogger()
				.debug("In RandomSampleOnTable->information useful for connection: retrieved");
		return info;
	}

	// to normalize the information related to a database
	private void normalizeDBInfo(DBResource resource) throws Exception {

		try {
			int ap = resource.getAccessPoints().size();

			for (int i = 0; i < ap; i++) {
				resource.normalize(i);
			}

		} catch (Exception e) {
			AnalysisLogger.getLogger().debug(
					"In RandomSampleOnTable->: Error in normalization process"
							+ e.getMessage());

			throw e;
		}

		// int ap = resource.getAccessPoints().size();
		// for (int i = 0; i < ap; i++) {
		// try {
		// resource.normalize(i);
		// } catch (IOException e) {
		// // e.printStackTrace();
		// AnalysisLogger.getLogger().debug(
		// "In RandomSampleOnTable->: Error in normalization process"
		// + e.getMessage());
		// throw e;
		// }
		// }
	}

	// create the database's connection
	private void getConnection(List<String> Info) throws IOException {

		// create the connection
		Iterator<String> iterator = Info.iterator();

		String DatabaseUserName = iterator.next();
		String DatabasePassword = iterator.next();
		String DatabaseDriver = iterator.next();
		String DatabaseDialect = iterator.next();
		String DatabaseURL = iterator.next();
		String DatabaseName = iterator.next();

		mgt.createConnection(DatabaseUserName, DatabasePassword,
				DatabaseDriver, DatabaseDialect, DatabaseURL, DatabaseName);

		AnalysisLogger.getLogger().debug(
				"In RandomSampleOnTable->database " + DatabaseName
						+ ": connected");

	}

	// to perform the sample operation on the table randomly
	private LinkedHashMap<String, StatisticalType> randomSampleOnTable()
			throws Exception {

		LinkedHashMap<String, StatisticalType> mapResults = new LinkedHashMap<String, StatisticalType>();
		AnalysisLogger
				.getLogger()
				.debug("In RandomSampleOnTable->starting the sample operation on table randomly");

		// sample on table operation
		// List<Object> resultSet = null;

		if (driverInfo.toLowerCase().contains("postgres")) {
			// for a postgres database the second parameter is the schema name
			// resultSet = mgt.randomSampleOnTable(tableName, schemaName,
			// config.getPersistencePath());
			mgt.randomSampleOnTable(tableName, schemaName,
					config.getPersistencePath());
		}

		if (driverInfo.toLowerCase().contains("mysql")) {
			// for a mysql database the second parameter is the database name
			// resultSet = mgt.randomSampleOnTable(tableName, databaseName,
			// config.getPersistencePath());
			mgt.randomSampleOnTable(tableName, databaseName,
					config.getPersistencePath());
		}

		AnalysisLogger.getLogger().debug(
				"In RandomSampleOnTable->result retrieved");

		// to add the results to the variable map
		// to add the map

		HashMap<String, String> mapResult = new HashMap<String, String>();
		mapResult = mgt.getMapSampleTableResult();

		String encoded = null;
		encoded = new String(mapResult.get("HEADERS").getBytes(), "UTF-8");

		// // check the encoded value
		// AnalysisLogger.getLogger().debug(
		// "In RandomSampleOnTable->string encoded: " + encoded);

		PrimitiveType val = new PrimitiveType(String.class.getName(), encoded,
				PrimitiveTypes.STRING, "Row Header", "Row Header");
		mapResults.put("HEADERS", val);

		for (int i = 0; i < mapResult.size() - 1; i++) {
			encoded = new String(mapResult.get(String.valueOf(i)).getBytes(),
					"UTF-8");

			// // check the encoded value
			// AnalysisLogger.getLogger().debug(
			// "In RandomSampleOnTable->string encoded: " + encoded);
			String row = "Row " + Integer.toString(i + 1);
			PrimitiveType val1 = new PrimitiveType(String.class.getName(),
					encoded, PrimitiveTypes.STRING, row, row);
			mapResults.put(String.valueOf(i), val1);
			// //check value contained in map
			// String value = (String) val1.getContent();
			//
			// AnalysisLogger.getLogger().debug(
			// "In RandomSampleOnTable->value: " + value);
		}

		// to add the file
		PrimitiveType fileResult = new PrimitiveType(File.class.getName(),
				mgt.getFile(), PrimitiveTypes.FILE, "File", "File");
		mapResults.put("File", fileResult);

		return mapResults;

	}

}

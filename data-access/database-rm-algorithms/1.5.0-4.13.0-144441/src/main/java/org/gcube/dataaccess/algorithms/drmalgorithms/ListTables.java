package org.gcube.dataaccess.algorithms.drmalgorithms;

import java.io.IOException;
import java.util.ArrayList;
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

/** Class that allows to retrieve the tables's names of a chosen database */
public class ListTables extends StandardLocalInfraAlgorithm {

	private static final String TABLE_NAME = "Table Name ";
	private LinkedHashMap<String, StatisticalType> map = new LinkedHashMap<String, StatisticalType>();
	private DatabaseManagement mgt;
	// variable that keeps track of the database's type
	private String driverInfo;

	// database's parameters specified by the user
	private String resourceName = null;
	private String databaseName = null;
	private String schemaName = null;

	// private SessionFactory sf;

	@Override
	public void init() throws Exception {
		mgt = new DatabaseManagement(config.getConfigPath());
		AnalysisLogger.getLogger().debug("In ListTables->Initialization");

		String scope = config.getGcubeScope();

		AnalysisLogger.getLogger().debug(
				"In ListTables->scope set by config object: " + scope);

	}

	@Override
	public String getDescription() {
		// add a simple description for the algorithm

		return "Algorithm that allows to view the table names of a chosen database";

	}

	@Override
	protected void process() throws Exception, IOException,
			IllegalStateException, DiscoveryException, InvalidResultException,
			HibernateException {

		AnalysisLogger.getLogger().debug("In ListTables->Processing");

		try {

			// retrieve information useful for the connection

			List<String> Info = retrieveInfo();

			// create the connection
			getConnection(Info);

			// get the table' list

			List<String> listTables = new ArrayList<String>();

			if (driverInfo.toLowerCase().contains("postgres")) {

				schemaName = getInputParameter("SchemaName");
				if (schemaName != null) {
					schemaName = getInputParameter("SchemaName").trim();
				}
				if ((schemaName == null) || (schemaName.equals(""))) {
					throw new Exception("Warning: insert the schema name");
				}

				// if (!schemaName.equals("")) {

				listTables = mgt.getTables(databaseName, schemaName);

				AnalysisLogger
						.getLogger()
						.debug("In ListTables->getting table's name for database postgres");

				// }

			}

			if (driverInfo.toLowerCase().contains("mysql")) {

				listTables = mgt.getTables(databaseName, null);

				AnalysisLogger
						.getLogger()
						.debug("In ListTables->getting table's name for database mysql");

			}

			// if (listTables.size()==0){
			//
			// AnalysisLogger.getLogger().debug("In ListTables->Warning: no table available");
			//
			// }

			// TODO: manage also the oracle type

			if (listTables == null) {

				AnalysisLogger.getLogger().debug(
						"In ListTables->Warning: no tables available");

			} else {

				for (int i = 0; i < listTables.size(); i++) {
					String tableId = TABLE_NAME + (i + 1);

					PrimitiveType val = new PrimitiveType(
							String.class.getName(), null,
							PrimitiveTypes.STRING, tableId, tableId);

					val.setContent(listTables.get(i));

					map.put(tableId, val);

					// AnalysisLogger.getLogger().debug(
					// "In ListTables->getting table's name: "
					// + val.getContent());

				}
			}

		} catch (HibernateException h) {

			AnalysisLogger.getLogger().debug(
					"In ListTables-> ERROR " + h.getMessage());
			throw h;
		} catch (IllegalStateException e) {
			// e.printStackTrace();

			AnalysisLogger.getLogger().debug(
					"In ListTables-> ERROR " + e.getMessage());
			throw e;

		} catch (DiscoveryException e1) {
			// e1.printStackTrace();

			AnalysisLogger.getLogger().debug(
					"In ListTables-> ERROR " + e1.getMessage());

			throw e1;

		} catch (InvalidResultException e2) {
			// e2.printStackTrace();

			AnalysisLogger.getLogger().debug(
					"In ListTables-> ERROR " + e2.getMessage());

			throw e2;

		} catch (IOException e3) {
			// e3.printStackTrace();

			AnalysisLogger.getLogger().debug(
					"In ListTables-> Exception " + e3.getMessage());

			throw e3;
		}

		catch (Exception e4) {
			// e4.printStackTrace();

			AnalysisLogger.getLogger().debug(
					"In ListTables-> Exception " + e4.getMessage());

			throw e4;

		} finally {
			// close the connection
			mgt.closeConnection();
		}
	}

	@Override
	protected void setInputParameters() {

		// AnalysisLogger.getLogger().debug("In ListTables->setting inputs");

		// parameters specified by the user
		addStringInput("ResourceName", "The name of the resource", "");
		addStringInput("DatabaseName", "The name of the database", "");
		addStringInput("SchemaName", "The name of the schema", "");

	}

	@Override
	public void shutdown() {
		AnalysisLogger.getLogger().debug("In ListTables->Shutdown");

	}

	@Override
	public StatisticalType getOutput() {

		AnalysisLogger.getLogger().debug("In ListTables->retrieving outputs");

		// generate a primitive type for the collection
		PrimitiveType output = new PrimitiveType(LinkedHashMap.class.getName(),
				map, PrimitiveTypes.MAP, "ResultsMap" + UUID.randomUUID(),
				"Results Map");

		return output;

	}

	private List<String> retrieveInfo() throws Exception,
			IllegalStateException, DiscoveryException, InvalidResultException {

		// parameters specified by the user
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
				"In ListTables->number of database resources: "
						+ resources.size());

		for (int i = 0; i < resources.size(); i++) {

			AnalysisLogger.getLogger().debug(
					"In ListTables->Resource's name: "
							+ resources.get(i).getResourceName());

		}

		// list that contains information useful for the connection
		List<String> info = new ArrayList<String>();

		check: for (int i = 0; i < resources.size(); i++) {

			if (resources.get(i).getResourceName().toLowerCase()
					.equals(resourceName.toLowerCase())) {

				normalizeDBInfo(resources.get(i));

				for (int j = 0; j < resources.get(i).getAccessPoints().size(); j++) {

					// if (resources.get(i).getAccessPoints().get(j)
					// .getDatabaseName().equals(databaseName)) {
					//
					// info.add(resources.get(i).getAccessPoints().get(j)
					// .getUsername());
					//
					// info.add(resources.get(i).getAccessPoints().get(j)
					// .getPassword());
					//
					// info.add(resources.get(i).getAccessPoints().get(j)
					// .getDriver());
					//
					// // driverInfo =
					// // resources.get(i).getAccessPoints().get(j)
					// // .getDriver();
					//
					// info.add(resources.get(i).getAccessPoints().get(j)
					// .getDialect());
					//
					// info.add(resources.get(i).getAccessPoints().get(j)
					// .address());
					//
					// info.add(databaseName);
					//
					// break check;
					//
					// }

					// if (resources.get(i).getAccessPoints().get(j)
					// .address().equals(url)){
					//
					// System.out.println("url selezionato");
					//
					//
					//
					// }

					if (resources.get(i).getAccessPoints().get(j)
							.getDatabaseName().toLowerCase()
							.equals(databaseName.toLowerCase())) {

						info.add(resources.get(i).getAccessPoints().get(j)
								.getUsername());

						AnalysisLogger.getLogger().debug(
								"In ListTables->username: "
										+ resources.get(i).getAccessPoints()
												.get(j).getUsername());

						info.add(resources.get(i).getAccessPoints().get(j)
								.getPassword());

						AnalysisLogger.getLogger().debug(
								"In ListTables->password: "
										+ resources.get(i).getAccessPoints()
												.get(j).getPassword());

						info.add(resources.get(i).getAccessPoints().get(j)
								.getDriver());

						driverInfo = resources.get(i).getAccessPoints().get(j)
								.getDriver();

						AnalysisLogger.getLogger().debug(
								"In ListTables->driver: "
										+ resources.get(i).getAccessPoints()
												.get(j).getDriver());

						// driverInfo =
						// resources.get(i).getAccessPoints().get(j)
						// .getDriver();

						info.add(resources.get(i).getAccessPoints().get(j)
								.getDialect());

						AnalysisLogger.getLogger().debug(
								"In ListTables->dialect: "
										+ resources.get(i).getAccessPoints()
												.get(j).getDialect());

						info.add(resources.get(i).getAccessPoints().get(j)
								.address());

						AnalysisLogger.getLogger().debug(
								"In ListTables->url: "
										+ resources.get(i).getAccessPoints()
												.get(j).address());

						info.add(databaseName);

						AnalysisLogger.getLogger().debug(
								"In ListTables->databasename: "
										+ resources.get(i).getAccessPoints()
												.get(j).getDatabaseName());

						break check;

					}

				}

			}

		}

		AnalysisLogger.getLogger().debug(
				"In ListTables->information useful for connection: retrieved");

		return info;

	}

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
				"In ListTables->database " + DatabaseName + ": connected");
	}

	private void normalizeDBInfo(DBResource resource) throws Exception {

		try {
			int ap = resource.getAccessPoints().size();

			for (int i = 0; i < ap; i++) {
				resource.normalize(i);
			}

		} catch (Exception e) {
			AnalysisLogger.getLogger().debug(
					"In ListTables->: Error in normalization process"
							+ e.getMessage());

			throw e;
		}

		// int ap = resource.getAccessPoints().size();
		//
		// for (int i = 0; i < ap; i++) {
		//
		// try {
		// resource.normalize(i);
		// } catch (IOException e) {
		// // e.printStackTrace();
		//
		// AnalysisLogger.getLogger().debug(
		// "In ListTables->: Error in normalization process"
		// + e.getMessage());
		//
		// throw e;
		// }
		//
		// }
	}

}

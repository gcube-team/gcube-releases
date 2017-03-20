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

/** Class that allows to retrieve some information about the chosen table */
public class GetTableDetails extends StandardLocalInfraAlgorithm {

	private static final String ROWS_NUMBER = "Rows Number";
	private static final String NAME_OF_COLUMNS = "Name Of Columns";
	private static final String CREATE_TABLE_STATEMENT = "Create Table Statement";
	private LinkedHashMap<String, StatisticalType> map = new LinkedHashMap<String, StatisticalType>();
	// object that allows to manage some operations on a database
	private DatabaseManagement mgt;
	// variable that keeps track of the database's type
	private String driverInfo;

	// database's parameters specified by the user
	private String resourceName = null;
	private String databaseName = null;
	private String schemaName = null;
	private String tableName = null;

	// private SessionFactory sf;

	@Override
	public void init() throws Exception {

		mgt = new DatabaseManagement(config.getConfigPath());

		AnalysisLogger.getLogger().debug("In GetTableDetails->Initialization");

		String scope = config.getGcubeScope();

		AnalysisLogger.getLogger().debug(
				"In GetTableDetails->scope set by config: " + scope);

	}

	@Override
	public String getDescription() {
		// add a simple description for the algorithm

		return "Algorithm that allows to view table details of a chosen database";

	}

	@Override
	protected void process() throws Exception, IOException,
			IllegalStateException, DiscoveryException, InvalidResultException,
			HibernateException {

		AnalysisLogger.getLogger().debug("In GetTableDetails->Processing");

		try {

			// retrieve information
			List<String> Info = retrieveInfo();

			// create the connection
			getConnection(Info);

			// get table's details

			// recover information about the "CreateTableStatement" and
			// "Number of rows" of the table chosen by the user

			map = getDetails();

		} catch (HibernateException h) {

			AnalysisLogger.getLogger().debug(
					"In GetTableDetails-> ERROR " + h.getMessage());
			throw h;
		} catch (IllegalStateException e) {
			// e.printStackTrace();

			AnalysisLogger.getLogger().debug(
					"In GetTableDetails-> ERROR " + e.getMessage());

			throw e;

		} catch (DiscoveryException e1) {
			// e1.printStackTrace();

			AnalysisLogger.getLogger().debug(
					"In GetTableDetails-> ERROR " + e1.getMessage());

			throw e1;

		} catch (InvalidResultException e2) {
			// e2.printStackTrace();

			AnalysisLogger.getLogger().debug(
					"In GetTableDetails-> ERROR " + e2.getMessage());

			throw e2;

		} catch (IOException e3) {
			// e3.printStackTrace();

			AnalysisLogger.getLogger().debug(
					"In GetTableDetails-> Exception " + e3.getMessage());

			throw e3;
		}

		catch (Exception e4) {
			// e4.printStackTrace();
			AnalysisLogger.getLogger().debug(
					"In GetTableDetails-> Exception " + e4.getMessage());

			throw e4;

		} finally {
			// close the connection
			mgt.closeConnection();
		}

	}

	@Override
	protected void setInputParameters() {

		// AnalysisLogger.getLogger().debug("In GetTableDetails->setting inputs");

		// parameters specified by the user
		addStringInput("ResourceName", "The name of the resource", "");
		addStringInput("DatabaseName", "The name of the database", "");
		addStringInput("SchemaName", "The name of the schema", "");
		addStringInput("TableName", "The name of the table", "");

	}

	@Override
	public void shutdown() {
		AnalysisLogger.getLogger().debug("In GetTableDetails->Shutdown");

	}

	@Override
	public StatisticalType getOutput() {

		AnalysisLogger.getLogger().debug(
				"In GetTableDetails->retrieving outputs");

		// generate a primitive type for the collection
		PrimitiveType output = new PrimitiveType(LinkedHashMap.class.getName(),
				map, PrimitiveTypes.MAP, "ResultsMap" + UUID.randomUUID(),
				"Results Map");

		return output;

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
				"In GetTableDetails->database " + DatabaseName + ": connected");
	}

	// Method that recover the info useful for the connection
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
				"In GetTableDetails->number of database resources: "
						+ resources.size());

		for (int i = 0; i < resources.size(); i++) {

			AnalysisLogger.getLogger().debug(
					"In GetTableDetails->Resource's name: "
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
								"In GetTableDetails->username: "
										+ resources.get(i).getAccessPoints()
												.get(j).getUsername());

						info.add(resources.get(i).getAccessPoints().get(j)
								.getPassword());

						AnalysisLogger.getLogger().debug(
								"In GetTableDetails->password: "
										+ resources.get(i).getAccessPoints()
												.get(j).getPassword());

						info.add(resources.get(i).getAccessPoints().get(j)
								.getDriver());

						driverInfo = resources.get(i).getAccessPoints().get(j)
								.getDriver();

						AnalysisLogger.getLogger().debug(
								"In GetTableDetails->driver: "
										+ resources.get(i).getAccessPoints()
												.get(j).getDriver());

						// driverInfo =
						// resources.get(i).getAccessPoints().get(j)
						// .getDriver();

						info.add(resources.get(i).getAccessPoints().get(j)
								.getDialect());

						AnalysisLogger.getLogger().debug(
								"In GetTableDetails->dialect: "
										+ resources.get(i).getAccessPoints()
												.get(j).getDialect());

						info.add(resources.get(i).getAccessPoints().get(j)
								.address());

						AnalysisLogger.getLogger().debug(
								"In GetTableDetails->url: "
										+ resources.get(i).getAccessPoints()
												.get(j).address());

						info.add(databaseName);

						AnalysisLogger.getLogger().debug(
								"In GetTableDetails->databasename: "
										+ resources.get(i).getAccessPoints()
												.get(j).getDatabaseName());

						break check;

					}

				}

			}

		}

		AnalysisLogger
				.getLogger()
				.debug("In GetTableDetails->information useful for connection: retrieved");
		return info;

	}

	// method that retrieves information such as "CreateTableStatement" and the
	// number of rows about the table chosen by the user
	private LinkedHashMap<String, StatisticalType> getDetails()
			throws Exception {

		tableName = getInputParameter("TableName");

		if (tableName != null) {
			tableName = getInputParameter("TableName").trim();
		}
		if ((tableName == null) || (tableName.equals(""))) {
			throw new Exception("Warning: insert the table name");
		}

		if (driverInfo.toLowerCase().contains("postgres")) {

			schemaName = getInputParameter("SchemaName");
			if (schemaName != null) {
				schemaName = getInputParameter("SchemaName").trim();
			}
			if ((schemaName == null) || (schemaName.equals(""))) {
				throw new Exception("Warning: insert the schema name");
			}
		}

		AnalysisLogger.getLogger().debug(
				"In GetTableDetails->getting details on the table: "
						+ tableName);

		// recover metadata of the table

		// recover the "show create" statement

		String createTable = null;

		if ((driverInfo.toLowerCase().contains("postgres"))) {

			createTable = mgt.getCreateTable(tableName, schemaName);

		}

		if ((driverInfo.toLowerCase().contains("mysql"))) {

			createTable = mgt.getCreateTable(tableName, databaseName);

		}

		PrimitiveType valCreateTable = new PrimitiveType(
				String.class.getName(), createTable, PrimitiveTypes.STRING,
				CREATE_TABLE_STATEMENT, CREATE_TABLE_STATEMENT);

		map.put(CREATE_TABLE_STATEMENT, valCreateTable);

		AnalysisLogger.getLogger().debug(
				"In GetTableDetails->getting the \"CreateTableStatement\": "
						+ createTable);

		// to retrieve the column names of a table
		List<String> listColumnNamesTable = mgt.getListColumnNamesTable();

		String ColumnName = "";

		for (int i = 0; i < listColumnNamesTable.size(); i++) {

			if (i != listColumnNamesTable.size() - 1) {
				ColumnName = ColumnName + listColumnNamesTable.get(i) + ",";
			} else {
				ColumnName = ColumnName + listColumnNamesTable.get(i);

			}

		}

		AnalysisLogger.getLogger().debug(
				"In GetTableDetails->column names: " + ColumnName);

		PrimitiveType valListColumnNamesTable = new PrimitiveType(
				String.class.getName(), ColumnName, PrimitiveTypes.STRING,
				NAME_OF_COLUMNS, NAME_OF_COLUMNS);

		map.put(NAME_OF_COLUMNS, valListColumnNamesTable);

		AnalysisLogger.getLogger().debug(
				"In GetTableDetails->getting the column names list: "
						+ createTable);

		// recover the number of rows
		// BigInteger rows = mgt.getNumberOfRows(tableName);

		long rows = 0;

		if ((driverInfo.toLowerCase().contains("postgres"))) {
			AnalysisLogger.getLogger().debug(
					"In GetTableDetails->schemaName:" + schemaName);
			;

			rows = mgt.getNumberOfRows(tableName, schemaName);

		}

		if ((driverInfo.toLowerCase().contains("mysql"))) {

			AnalysisLogger.getLogger().debug(
					"In GetTableDetails->*databasename:" + databaseName);
			;

			rows = mgt.getNumberOfRows(tableName, databaseName);

		}

		PrimitiveType valRows = new PrimitiveType(String.class.getName(),
				Long.toString(rows), PrimitiveTypes.STRING, ROWS_NUMBER,
				ROWS_NUMBER);

		map.put(ROWS_NUMBER, valRows);

		AnalysisLogger.getLogger().debug(
				"In GetTableDetails->getting the number of rows: "
						+ Long.toString(rows));

		return map;

	}

	private void normalizeDBInfo(DBResource resource) throws Exception {

		try {
			int ap = resource.getAccessPoints().size();

			for (int i = 0; i < ap; i++) {
				resource.normalize(i);
			}

		} catch (Exception e) {
			AnalysisLogger.getLogger().debug(
					"In GetTableDetails->: Error in normalization process"
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
		//
		// // e.printStackTrace();
		// AnalysisLogger.getLogger().debug(
		// "In GetTableDetails->: Error in normalization process"
		// + e.getMessage());
		//
		// throw e;
		//
		// }
		//
		// }
	}

}

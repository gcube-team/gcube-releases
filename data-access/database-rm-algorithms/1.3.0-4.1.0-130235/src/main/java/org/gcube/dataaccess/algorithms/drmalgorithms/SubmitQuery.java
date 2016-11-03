package org.gcube.dataaccess.algorithms.drmalgorithms;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.dataaccess.databases.access.DatabasesDiscoverer;
import org.gcube.dataaccess.databases.lexer.MySQLLexicalAnalyzer;
import org.gcube.dataaccess.databases.lexer.PostgresLexicalAnalyzer;
import org.gcube.dataaccess.databases.resources.DBResource;
import org.gcube.dataaccess.databases.utils.DatabaseManagement;
import org.gcube.dataanalysis.ecoengine.datatypes.PrimitiveType;
import org.gcube.dataanalysis.ecoengine.datatypes.StatisticalType;
import org.gcube.dataanalysis.ecoengine.datatypes.enumtypes.PrimitiveTypes;
import org.gcube.dataanalysis.ecoengine.interfaces.StandardLocalExternalAlgorithm;
import org.gcube.resources.discovery.client.api.DiscoveryException;
import org.gcube.resources.discovery.client.api.InvalidResultException;
import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;

import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.parser.ParseException;

/**
 * Class that allows to submit a query. It retrieves results in a file and in a
 * map.
 */
public class SubmitQuery extends StandardLocalExternalAlgorithm {
	
	static long maximum_execution_time = 30*60*1000;   //time to stop execution query
	private LinkedHashMap<String, StatisticalType> map = new LinkedHashMap<String, StatisticalType>();
	// object that allows to manage some operations on a database
	private DatabaseManagement mgt;
	// variable that keeps track of the database's type
	private String driverInfo;

	private Connection dbconnection;
	// database's parameters specified by the user
	private String resourceName = null;
	private String databaseName = null;
	private String schemaName = null;
	private String tableName = null;
	private String query = null;

	private String valueReadOnly = "Read-Only Query";
	private String smartCorrection = "Apply Smart Correction";
	private String dialect = "Language";

	private String valueRO;
	private String valueSC;
	private String valueDialect = "";

	// variable used to filter the disallowed queries
	private boolean NotAllowedQuery = false;

	//class for the timer to stop execution query
	private class ExecutionStopper extends TimerTask {
		@Override
		public void run() {
			AnalysisLogger.getLogger().debug("ExecutionStopper: Stopping execution");
			shutdown();
		}
 	}
 
	
	@Override
	public void init() throws Exception {

		mgt = new DatabaseManagement(config.getConfigPath());
		AnalysisLogger.getLogger().debug("In SubmitQuery->Initialization");

		String scope = config.getGcubeScope();
		AnalysisLogger.getLogger().debug(
				"In SubmitQuery->scope set by config object: " + scope);

		if (scope == null || scope.length() == 0) {
			scope = ScopeProvider.instance.get();
			AnalysisLogger.getLogger().debug(
					"In SubmitQuery->scope set by ScopeProvider: " + scope);
		} else {
			ScopeProvider.instance.set(scope);
		}

		valueRO = config.getParam(valueReadOnly);
		valueSC = config.getParam(smartCorrection);
	}

	@Override
	public String getDescription() {
		return "Algorithm that allows to submit a query";
	}

	@Override
	protected void process() throws Exception, IOException,
			IllegalStateException, DiscoveryException, InvalidResultException,
			HibernateException {

		AnalysisLogger.getLogger().debug("In SubmitQuery->Processing");
		
		Timer stopper = new Timer();
		stopper.schedule(new ExecutionStopper(),maximum_execution_time); 
		
		try {
			// retrieve information
			List<String> Info = retrieveInfo();

			// create the connection
			dbconnection = getConnection(Info);

			// submit a query
			map = submitQuery();

//			// close the connection
//			dbconnection.close();

		} catch (HibernateException h) {

			// AnalysisLogger.getLogger().debug(
			// "In SubmitQuery-> ERROR " + h.getMessage());
			AnalysisLogger.getLogger().debug(
					"In SubmitQuery-> " + h.getMessage());
			throw h;
		}

		catch (IllegalStateException e) {
			// e.printStackTrace();

			// AnalysisLogger.getLogger().debug(
			// "In SubmitQuery-> ERROR " + e.getMessage());
			AnalysisLogger.getLogger().debug(
					"In SubmitQuery-> " + e.getMessage());
			throw e;

		} catch (DiscoveryException e1) {
			// e1.printStackTrace();

			// AnalysisLogger.getLogger().debug(
			// "In SubmitQuery-> ERROR " + e1.getMessage());
			AnalysisLogger.getLogger().debug(
					"In SubmitQuery-> " + e1.getMessage());
			throw e1;

		} catch (InvalidResultException e2) {
			// e2.printStackTrace();

			// AnalysisLogger.getLogger().debug(
			// "In SubmitQuery-> ERROR " + e2.getMessage());
			AnalysisLogger.getLogger().debug(
					"In SubmitQuery-> " + e2.getMessage());
			throw e2;

		} catch (IOException e3) {
			// e3.printStackTrace();

			// AnalysisLogger.getLogger().debug(
			// "In SubmitQuery-> ERROR " + e3.getMessage());
			AnalysisLogger.getLogger().debug(
					"In SubmitQuery-> " + e3.getMessage());
			throw e3;
		}

		catch (Exception e4) {
			// e4.printStackTrace();

			// AnalysisLogger.getLogger().debug(
			// "In SubmitQuery-> Exception " + e4.getMessage());
			AnalysisLogger.getLogger().debug(
					"In SubmitQuery-> " + e4.getMessage());
			throw e4;

		} finally {
			if (dbconnection!=null) {
				dbconnection.close();
				AnalysisLogger.getLogger().debug("In SubmitQuery-> Connection closed");
				dbconnection=null;
			}
			//remove the timer if the execution query has already terminated
			if (stopper!=null){
				try{
				stopper.cancel();
				stopper.purge();
				AnalysisLogger.getLogger().debug("In SubmitQuery-> Execution stopper terminated");
				}catch(Exception e){
					AnalysisLogger.getLogger().debug("In SubmitQuery-> Could not stop execution stopper "+e.getMessage() );
				}
			}
		}
	}

	public StatisticalType getOutput() {

		AnalysisLogger.getLogger().debug("In SubmitQuery->retrieving outputs");
		// generate a primitive type for the collection
		PrimitiveType output = new PrimitiveType(LinkedHashMap.class.getName(),
				map, PrimitiveTypes.MAP, "ResultsMap"+UUID.randomUUID(), "Results Map");
		return output;
	}

	public List<StatisticalType> getInputParameters() {
		List<StatisticalType> parameters = new ArrayList<StatisticalType>();

		// parameters specified by the user
		PrimitiveType p0 = new PrimitiveType(String.class.getName(), "",
				PrimitiveTypes.STRING, "ResourceName",
				"The name of the resource");

		PrimitiveType p1 = new PrimitiveType(String.class.getName(), "",
				PrimitiveTypes.STRING, "DatabaseName",
				"The name of the database");

		PrimitiveType p2 = new PrimitiveType(Boolean.class.getName(), null,
				PrimitiveTypes.BOOLEAN, valueReadOnly,
				"Check the box if the query must be read-only", "true");

		PrimitiveType p3 = new PrimitiveType(Boolean.class.getName(), null,
				PrimitiveTypes.BOOLEAN, smartCorrection,
				"Check the box for smart correction", "true");

		// addEnumerateInput(SmartCorrectionEnum.values(), dialect, "Language",
		// "");
		PrimitiveType p4 = new PrimitiveType(Enum.class.getName(),
				SmartCorrectionEnum.values(), PrimitiveTypes.ENUMERATED,
				dialect, "Language", SmartCorrectionEnum.NONE.name());

		PrimitiveType p5 = new PrimitiveType(String.class.getName(), "",
				PrimitiveTypes.STRING, "Query", "query");

		parameters.add(p0);
		parameters.add(p1);
		parameters.add(p2);
		parameters.add(p3);
		parameters.add(p4);
		parameters.add(p5);

		return parameters;

	}

	@Override
	protected void setInputParameters() {

		// AnalysisLogger.getLogger().debug("In SubmitQuery->setting inputs");

		// parameters specified by the user
		// addStringInput("ResourceName", "The name of the resource", "");
		// addStringInput("DatabaseName", "The name of the database", "");

		// PrimitiveType p2 = new PrimitiveType(Boolean.class.getName(), null,
		// PrimitiveTypes.BOOLEAN, valueReadOnly,
		// "Check the box if the query must be read-only","true");

		// addEnumerateInput(SubmitQueryEnum.values(), valueReadOnly,
		// "Check the box if the query must be read-only",
		// SubmitQueryEnum.TRUE.name());
		// addStringInput("SchemaName", "The name of the schema", "");
		// addStringInput("TableName", "The name of the table", "");

		// PrimitiveType p3 = new PrimitiveType(Boolean.class.getName(), null,
		// PrimitiveTypes.BOOLEAN, smartCorrection,
		// "Check the box for smart correction","true");
		// List<StatisticalType> parameters = new ArrayList<StatisticalType>();
		// parameters.add(p2);
		// parameters.add(p3);

		// addEnumerateInput(
		// SubmitQueryEnum.values(),
		// smartCorrection,
		// "Check the box for smart correction",
		// SubmitQueryEnum.TRUE.name());

		// addEnumerateInput(SmartCorrectionEnum.values(), dialect, "Language)",
		// SmartCorrectionEnum.POSTGRES.name());

		// addEnumerateInput(SmartCorrectionEnum.values(), dialect, "Language",
		// "");
		// addEnumerateInput(SmartCorrectionEnum.values(), dialect, "Language",
		// "");

		// addStringInput("Query", "query", "");

	}

	@Override
	public void shutdown() {
				
		AnalysisLogger.getLogger().debug("In SubmitQuery->Shutdown");
		try{
		if (dbconnection!=null) {
			dbconnection.close();
			AnalysisLogger.getLogger().debug("In SubmitQuery-> Connection closed");
			dbconnection=null;
		}
		}catch(Exception e){
			AnalysisLogger.getLogger().debug("In SubmitQuery->Unable to close connection "+e.getMessage());
		}
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
				"In SubmitQuery->number of elements: " + resources.size());

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
								"In SubmitQuery->username: "
										+ resources.get(i).getAccessPoints()
												.get(j).getUsername());

						info.add(resources.get(i).getAccessPoints().get(j)
								.getPassword());
						AnalysisLogger.getLogger().debug(
								"In SubmitQuery->password: "
										+ resources.get(i).getAccessPoints()
												.get(j).getPassword());

						info.add(resources.get(i).getAccessPoints().get(j)
								.getDriver());
						driverInfo = resources.get(i).getAccessPoints().get(j)
								.getDriver();
						AnalysisLogger.getLogger().debug(
								"In SubmitQuery->driver: "
										+ resources.get(i).getAccessPoints()
												.get(j).getDriver());

						// driverInfo =
						// resources.get(i).getAccessPoints().get(j)
						// .getDriver();

						info.add(resources.get(i).getAccessPoints().get(j)
								.getDialect());
						AnalysisLogger.getLogger().debug(
								"In SubmitQuery->dialect: "
										+ resources.get(i).getAccessPoints()
												.get(j).getDialect());

						info.add(resources.get(i).getAccessPoints().get(j)
								.address());
						AnalysisLogger.getLogger().debug(
								"In SubmitQuery->url: "
										+ resources.get(i).getAccessPoints()
												.get(j).address());

						info.add(databaseName);
						AnalysisLogger.getLogger().debug(
								"In SubmitQuery->databasename: "
										+ resources.get(i).getAccessPoints()
												.get(j).getDatabaseName());

						break check;
					}
				}
			}
		}

		AnalysisLogger.getLogger().debug(
				"In SubmitQuery->information useful for connection: retrieved");
		return info;
	}

	// create the database's connection
	private Connection getConnection(List<String> Info) throws Exception {

		// create the connection
		Iterator<String> iterator = Info.iterator();

		String DatabaseUserName = iterator.next();
		String DatabasePassword = iterator.next();
		String DatabaseDriver = iterator.next();
		String DatabaseDialect = iterator.next();
		String DatabaseURL = iterator.next();
		String DatabaseName = iterator.next();

		// Load the database driver
		Class.forName(DatabaseDriver) ;
		// Get a connection to the database
	    Connection conn = DriverManager.getConnection(DatabaseURL,DatabaseUserName,DatabasePassword) ;
		if (conn!=null){
			AnalysisLogger.getLogger().debug(
					"In SubmitQuery->database " + DatabaseName + ": connected");	
		}
		return conn;
	}

	// Method that allows to submit a query
	private LinkedHashMap<String, StatisticalType> submitQuery()
			throws Exception, ParseException, ConvertException {

		// LinkedHashMap<String, StatisticalType> results = new
		// LinkedHashMap<String, StatisticalType>();

		LinkedHashMap<String, StatisticalType> mapResults = new LinkedHashMap<String, StatisticalType>();

		query = getInputParameter("Query");
		if ((query == null) || (query.equals(""))) {
			throw new Exception("Warning: insert the query");
		}

		// analyze the query to filter it if it is not read-only compliant
		// String valueRO = getInputParameter(valueReadOnly);

		// //print check
		AnalysisLogger.getLogger().debug("In SubmitQuery->valueRO: " + valueRO);

		if (valueRO.equals("true")) {
			AnalysisLogger.getLogger().debug(
					"In SubmitQuery->Analyzing the query: " + query);
			NotAllowedQuery = analyzeQuery(query);

			// //print check
			// AnalysisLogger.getLogger().debug(
			// "In SubmitQuery->NotAllowedQuery valueRO: " + NotAllowedQuery);
		}

		if (NotAllowedQuery == false) {

			// formatWithQuotes(query);

			// submit query
			List<Object> result = new ArrayList<Object>();

			// path file
			// AnalysisLogger.getLogger()
			// .debug("In SubmitQuery->path file: "
			// + config.getPersistencePath());

			// if user specifies to use the smart correction a translation in
			// applied on the query
			// String valueSC = getInputParameter(smartCorrection);

			// //print check
			AnalysisLogger.getLogger().debug(
					"In SubmitQuery->valueSC: " + valueSC);

			// dialect to which a query is converted
			// String valueDialect = getInputParameter(dialect);
			valueDialect = getInputParameter(dialect);

			// //print check
			AnalysisLogger.getLogger().debug(
					"In SubmitQuery->valueDialect: " + valueDialect);

			if ((valueSC.equals("true")) && (!(valueDialect.equals("NONE")))) {
				String smartCorrectedQuery = "";

				AnalysisLogger.getLogger().debug(
						"In SubmitQuery->applying smart correction on the query: "
								+ query);

				if (valueDialect.equals("POSTGRES")) {

					// //print check
					// AnalysisLogger.getLogger().debug(
					// "In SubmitQuery->query: " + query);
					//
					// AnalysisLogger.getLogger().debug(
					// "In SubmitQuery->dialect: " +
					// DatabaseManagement.POSTGRESQLDialect);

					// call the SwisSQL library functionality
					smartCorrectedQuery = mgt.smartCorrectionOnQuery(query,
							DatabaseManagement.POSTGRESQLDialect);
				}

				if (valueDialect.equals("MYSQL")) {
					// call the SwisSQL library functionality
					smartCorrectedQuery = mgt.smartCorrectionOnQuery(query,
							DatabaseManagement.MYSQLDialect);
				}

				AnalysisLogger.getLogger().debug(
						"In SubmitQuery-> query converted: "
								+ smartCorrectedQuery);
				query = smartCorrectedQuery;

				if (!(smartCorrectedQuery.equals(""))) {
					PrimitiveType valQuery = new PrimitiveType(
							String.class.getName(), smartCorrectedQuery,
							PrimitiveTypes.STRING, "Converted Query",
							"Query Converted");

					mapResults.put("Query Converted", valQuery);
				}
			}

			// else if ((valueSC.equals("true")) &&
			// (valueDialect.equals("NONE"))) {
			//
			// throw new Exception("Warning: specify the language");
			//
			// }

			AnalysisLogger.getLogger().debug(
					"In SubmitQuery->Submitting the query: " + query);

			if (driverInfo.toLowerCase().contains("postgres")) {

				// for a postgres database the second parameter is the
				// schema
				// name

				// result = mgt.submitQuery(query, tableName, schemaName,
				// sf, config.getPersistencePath());

				result = mgt
						.submitQuery(query, dbconnection, config.getPersistencePath());
			}

			if (driverInfo.toLowerCase().contains("mysql")) {
				// for a mysql database the second parameter is the database
				// name

				// result = mgt.submitQuery(query, tableName, databaseName,
				// sf, config.getPersistencePath());

				result = mgt
						.submitQuery(query, dbconnection, config.getPersistencePath());
			}

			if (result == null) {
				throw new Exception("Warning: the table has not rows");
			}

			AnalysisLogger.getLogger().debug(
					"In SubmitQuery->Query's Result retrieved");

			HashMap<String, String> mapResult = new HashMap<String, String>();
			mapResult = mgt.getMapQueryResult();
//			System.out.println("map size alg with header: " + mapResult.size());
		
			String encoded = null;
			encoded = new String(mapResult.get("HEADERS").getBytes(), "UTF-8");

			// // check the encoded value
			// AnalysisLogger.getLogger().debug(
			// "In RandomSampleOnTable->string encoded: " + encoded);

			PrimitiveType val = new PrimitiveType(String.class.getName(),
					encoded, PrimitiveTypes.STRING, "Row", "Row");

			mapResults.put("HEADERS", val);

			// to add the rows (result of the query)
			for (int i = 0; i < mapResult.size() - 1; i++) {
				encoded = new String(mapResult.get(String.valueOf(i))
						.getBytes(), "UTF-8");

				// // check the encoded value
//				 AnalysisLogger.getLogger().debug(
//				 "In RandomSampleOnTable->string encoded: " + encoded);

				PrimitiveType val1 = new PrimitiveType(String.class.getName(),
						encoded, PrimitiveTypes.STRING, "Row", "Row");
				mapResults.put(String.valueOf(i), val1);
				// //check value contained in map
				// String value = (String) val1.getContent();
				//
//				 AnalysisLogger.getLogger().debug(
//				 "In RandomSampleOnTable->value: " + value);
			}

			// to add the file
			 PrimitiveType fileResult = new
			 PrimitiveType(File.class.getName(),
			 mgt.getFile(), PrimitiveTypes.FILE, "File",
			 "File");
			 mapResults.put("File", fileResult);
			 
//			 //to add the number of total rows for a result of a submit query operation
			 PrimitiveType totalRows = new PrimitiveType(String.class.getName(),
					 String.valueOf(mgt.getSubmitQueryTotalRows()), PrimitiveTypes.STRING, "Total Rows",
					 "Total Rows");
			mapResults.put("Total Rows", totalRows);
			 
		}
		return mapResults;
	}

	// method that allows to analyze the query in order to filter it if it is
	// not read-only compliant
	private boolean analyzeQuery(String query) throws Exception {
		boolean NotAllowed = false;
		// check if the query is allowed

		// TODO: check also the oracle case
		if (driverInfo.toLowerCase().contains("postgres")) {
			PostgresLexicalAnalyzer obj = new PostgresLexicalAnalyzer();
			NotAllowed = obj.analyze(query);
		}
		if (driverInfo.toLowerCase().contains("mysql")) {
			MySQLLexicalAnalyzer obj = new MySQLLexicalAnalyzer();
			NotAllowed = obj.analyze(query);
		}

		AnalysisLogger.getLogger().debug(
				"In SubmitQuery->Warning: query filtered: " + NotAllowed);
		return NotAllowed;
	}

	private void normalizeDBInfo(DBResource resource) throws Exception {
		
		
		try{
			int ap = resource.getAccessPoints().size();

			for (int i = 0; i < ap; i++) {
				resource.normalize(i);
			}
			
		}catch (Exception e) {
			AnalysisLogger.getLogger().debug(
					"In SubmitQuery->: Error in normalization process"
							+ e.getMessage());
			
			throw e;
		}
		
//		int ap = resource.getAccessPoints().size();
//		for (int i = 0; i < ap; i++) {
//			try {
//				resource.normalize(i);
//			} catch (IOException e) {
//				// e.printStackTrace();
//				AnalysisLogger.getLogger().debug(
//						"In SubmitQuery->: Error in normalization process"
//								+ e.getMessage());
//				throw e;
//			}
//		}
	}

//	private void formatWithQuotes(String Query) {
//		if (driverInfo.toLowerCase().contains("postgres")) {
//			if (Query.contains(tableName)) {
//				query = Query.replaceAll(tableName, "\"" + tableName + "\"");
//			}
//			if (driverInfo.toLowerCase().contains("mysql")) {
//				query = Query.replaceAll(tableName, "\"" + tableName + "\"");
//			}
//		}
//	}
}

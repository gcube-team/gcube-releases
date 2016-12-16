package org.gcube.dataaccess.algorithms.drmalgorithms;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;

import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.dataaccess.databases.access.DatabasesDiscoverer;
import org.gcube.dataaccess.databases.resources.DBResource;
import org.gcube.dataaccess.databases.utils.DatabaseManagement;
import org.gcube.dataanalysis.ecoengine.datatypes.PrimitiveType;
import org.gcube.dataanalysis.ecoengine.datatypes.StatisticalType;
import org.gcube.dataanalysis.ecoengine.datatypes.enumtypes.PrimitiveTypes;
import org.gcube.dataanalysis.ecoengine.interfaces.StandardLocalExternalAlgorithm;
import org.gcube.resources.discovery.client.api.DiscoveryException;
import org.gcube.resources.discovery.client.api.InvalidResultException;
import org.hibernate.HibernateException;

/**
 * class that allows to retrieve schema's names of a chosen database. In this
 * case the database's type is "postgresql"
 */
public class ListSchemas extends StandardLocalExternalAlgorithm {

	private static final String SCHEMA_NAME = "Schema Name ";
	private LinkedHashMap<String, StatisticalType> map = new LinkedHashMap<String, StatisticalType>();
	private DatabaseManagement mgt;

	// database's parameters specified by the user
	private String resourceName = null;
	private String databaseName = null;

	//private SessionFactory sf;

	@Override
	public void init() throws Exception {
		mgt = new DatabaseManagement(config.getConfigPath());
		AnalysisLogger.getLogger().debug("In ListSchemas->Initialization");

		String scope = config.getGcubeScope();

		AnalysisLogger.getLogger().debug(
				"In ListSchemas->scope set by config object: " + scope);

		if (scope == null || scope.length() == 0) {

			scope = ScopeProvider.instance.get();

			AnalysisLogger.getLogger().debug(
					"In ListSchemas->scope set by ScopeProvider: " + scope);

		} else {

			ScopeProvider.instance.set(scope);

		}
	}

	@Override
	public String getDescription() {
		// add a simple description for the algorithm

		return "Algorithm that allows to view the schema names of a chosen database for which the type is Postgres";

	}

	@Override
	protected void process() throws Exception, IOException,
			IllegalStateException, DiscoveryException, InvalidResultException,
			HibernateException {

		AnalysisLogger.getLogger().debug("In ListSchemas->Processing");

		try {

			// retrieve information useful for connection

			List<String> Info = retrieveInfo();

			// create the connection
			getConnection(Info);

			// get the schema's list

			List<String> listSchemas = new ArrayList<String>();
			listSchemas = getSchemas();

			if (listSchemas.size() == 0) {

				AnalysisLogger.getLogger().debug(
						"In ListSchemas->Warning: no schema available");

			}

			for (int i = 0; i < listSchemas.size(); i++) {
				String schemaId = SCHEMA_NAME+(i+1);
				PrimitiveType val = new PrimitiveType(String.class.getName(),
						null, PrimitiveTypes.STRING, schemaId,
						schemaId);

				val.setContent(listSchemas.get(i));

				map.put(schemaId, val);

				// AnalysisLogger.getLogger().debug(
				// "In ListSchemas->getting schema's name: "
				// + val.getContent());

			}

		} catch (HibernateException h) {

			AnalysisLogger.getLogger().debug(
					"In ListSchemas-> ERROR " + h.getMessage());
			throw h;
		}

		catch (IllegalStateException e) {
			// e.printStackTrace();

			AnalysisLogger.getLogger().debug(
					"In ListSchemas-> ERROR " + e.getMessage());

			throw e;

		} catch (DiscoveryException e1) {
			// e1.printStackTrace();

			AnalysisLogger.getLogger().debug(
					"In ListSchemas-> ERROR " + e1.getMessage());

			throw e1;

		} catch (InvalidResultException e2) {
			// e2.printStackTrace();

			AnalysisLogger.getLogger().debug(
					"In ListSchemas-> ERROR " + e2.getMessage());

			throw e2;

		} catch (IOException e3) {
			// e3.printStackTrace();

			AnalysisLogger.getLogger().debug(
					"In ListSchemas-> Exception " + e3.getMessage());

			throw e3;
		}

		catch (Exception e4) {
			// e4.printStackTrace();

			AnalysisLogger.getLogger().debug(
					"In ListSchemas-> Exception " + e4.getMessage());

			throw e4;

		} finally {
			// close the connection
			mgt.closeConnection();
		}
	}

	@Override
	protected void setInputParameters() {

		// AnalysisLogger.getLogger().debug("In ListSchemas->setting inputs");

		// resource and database's name specified by the user

		addStringInput("ResourceName", "The name of the resource", "");
		addStringInput("DatabaseName", "The name of the database", "");

	}

	@Override
	public void shutdown() {
		AnalysisLogger.getLogger().debug("In ListSchemas->Shutdown");

	}

	@Override
	public StatisticalType getOutput() {

		AnalysisLogger.getLogger().debug("In ListSchemas->retrieving outputs");

		// generate a primitive type for the collection
		PrimitiveType output = new PrimitiveType(LinkedHashMap.class.getName(),
				map, PrimitiveTypes.MAP, "ResultsMap" + UUID.randomUUID(),
				"Results Map");

		return output;

	}

	// method that retrieves the schema's list
	private List<String> getSchemas() throws Exception {

		List<String> listSchemas = new ArrayList<String>();

		try {

			listSchemas = mgt.getSchemas();

		} catch (Exception e) {

			// e.printStackTrace();

			// System.out.println(e.getMessage());

			throw e;

		}

//		finally {
//			if (sf.isClosed() == false) {
//				mgt.closeConnection();
//			}
//		}

		return listSchemas;

	}

	// method that retrieves information useful for the connection
	private List<String> retrieveInfo() throws Exception,
			IllegalStateException, DiscoveryException, InvalidResultException {

		// the user specifies the resource and the database'name

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

		// list that contains information useful for the connection
		List<String> info = new ArrayList<String>();

		// try{

		List<DBResource> resources = discovery.discover();

		AnalysisLogger.getLogger().debug(
				"In ListSchemas->number of database resources: "
						+ resources.size());

		for (int i = 0; i < resources.size(); i++) {

			AnalysisLogger.getLogger().debug(
					"In ListSchemas->Resource's name: "
							+ resources.get(i).getResourceName());

		}

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
								"In ListSchemas->username: "
										+ resources.get(i).getAccessPoints()
												.get(j).getUsername());

						info.add(resources.get(i).getAccessPoints().get(j)
								.getPassword());

						AnalysisLogger.getLogger().debug(
								"In ListSchemas->password: "
										+ resources.get(i).getAccessPoints()
												.get(j).getPassword());

						info.add(resources.get(i).getAccessPoints().get(j)
								.getDriver());

						AnalysisLogger.getLogger().debug(
								"In ListSchemas->driver: "
										+ resources.get(i).getAccessPoints()
												.get(j).getDriver());

						// driverInfo =
						// resources.get(i).getAccessPoints().get(j)
						// .getDriver();

						info.add(resources.get(i).getAccessPoints().get(j)
								.getDialect());

						AnalysisLogger.getLogger().debug(
								"In ListSchemas->dialect: "
										+ resources.get(i).getAccessPoints()
												.get(j).getDialect());

						info.add(resources.get(i).getAccessPoints().get(j)
								.address());

						AnalysisLogger.getLogger().debug(
								"In ListSchemas->url: "
										+ resources.get(i).getAccessPoints()
												.get(j).address());

						info.add(databaseName);

						AnalysisLogger.getLogger().debug(
								"In ListSchemas->databasename: "
										+ resources.get(i).getAccessPoints()
												.get(j).getDatabaseName());

						break check;

					}

				}

			}

		}

		AnalysisLogger.getLogger().debug(
				"In ListSchemas->information useful for connection: retrieved");

		// }
		// catch(IllegalStateException e)
		// {
		// // e.printStackTrace();
		// throw e;
		// }
		// catch(DiscoveryException e1)
		// {
		// e1.printStackTrace();
		// throw e1;
		// }
		// catch(InvalidResultException e2)
		// {
		// e2.printStackTrace();
		// throw e2;
		// }

		return info;

	}

	// method that allows to create the connection
	private void getConnection(List<String> Info) throws IOException {

		// create the connection
		Iterator<String> iterator = Info.iterator();

		String DatabaseUserName = iterator.next();
		String DatabasePassword = iterator.next();
		String DatabaseDriver = iterator.next();
		String DatabaseDialect = iterator.next();
		String DatabaseURL = iterator.next();
		String DatabaseName = iterator.next();

		mgt.createConnection(DatabaseUserName,
				DatabasePassword, DatabaseDriver, DatabaseDialect, DatabaseURL,
				DatabaseName);

		// if (sf.isClosed()){

		// AnalysisLogger.getLogger().debug("In ListSchemas->database "+DatabaseName+": connected");

		// }
	}

	private void normalizeDBInfo(DBResource resource) throws Exception {
		
		try{
			int ap = resource.getAccessPoints().size();

			for (int i = 0; i < ap; i++) {
				resource.normalize(i);
			}
			
		}catch (Exception e) {
			AnalysisLogger.getLogger().debug(
					"In ListSchemas->: Error in normalization process"
							+ e.getMessage());
			
			throw e;
		}

//		int ap = resource.getAccessPoints().size();
//
//		for (int i = 0; i < ap; i++) {
//
////			try {
//				resource.normalize(i);
////			} catch (IOException e) {
//
//				// e.printStackTrace();
////				AnalysisLogger.getLogger().debug(
////						"In ListTables->: Error in normalization process"
////								+ e.getMessage());
//
////				throw e;
//
////			}
//
//		}
	}

}

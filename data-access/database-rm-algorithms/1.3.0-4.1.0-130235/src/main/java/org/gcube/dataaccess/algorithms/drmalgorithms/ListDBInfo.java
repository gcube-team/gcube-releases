package org.gcube.dataaccess.algorithms.drmalgorithms;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;

import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.dataaccess.databases.access.DatabasesDiscoverer;
import org.gcube.dataaccess.databases.resources.DBResource;
import org.gcube.dataaccess.databases.resources.DBResource.AccessPoint;
import org.gcube.dataanalysis.ecoengine.datatypes.PrimitiveType;
import org.gcube.dataanalysis.ecoengine.datatypes.StatisticalType;
import org.gcube.dataanalysis.ecoengine.datatypes.enumtypes.PrimitiveTypes;
import org.gcube.dataanalysis.ecoengine.interfaces.StandardLocalExternalAlgorithm;
import org.gcube.resources.discovery.client.api.DiscoveryException;
import org.gcube.resources.discovery.client.api.InvalidResultException;

/** Class that allows to retrieve information about the chosen resource */
public class ListDBInfo extends StandardLocalExternalAlgorithm {

	private LinkedHashMap<String, StatisticalType> map = new LinkedHashMap<String, StatisticalType>();

	// database's parameters specified by the user
	private String resourceName = null;

	// list that contains information about the resource
	private List<AccessPoint> ap = new ArrayList<AccessPoint>();
	
	// variable that keeps track of database platform version
	private String platformVersion = "";

	@Override
	public void init() throws Exception {

		AnalysisLogger.getLogger().debug("In ListDBInfo->Initialization");
		
		String scope = config.getGcubeScope();
		
	    AnalysisLogger.getLogger().debug("In ListDBInfo->scope set by config object: " + scope);
		
		
		
		if (scope == null || scope.length() == 0) {
			
			scope = ScopeProvider.instance.get();
			AnalysisLogger.getLogger().debug("In ListDBInfo->scope set by ScopeProvider: " + scope);
			
			
		}else{
			
			ScopeProvider.instance.set(scope);
			
		}
	}

	@Override
	public String getDescription() {
		// add a simple description for the algorithm
		return "Algorithm that allows to view information about one chosen resource of Database Type in the Infrastructure";
	}

	@Override
	protected void process() throws Exception, IOException, IllegalStateException, DiscoveryException, InvalidResultException  {
		AnalysisLogger.getLogger().debug("In ListDBInfo->Processing");
		
		AnalysisLogger.getLogger().debug("Scope: " + ScopeProvider.instance.get());
		
	
		try{
		// retrieve information
		List<AccessPoint> apInfo = retrieveInfo();
		
		AnalysisLogger.getLogger().debug("access point dimension: " + apInfo.size());
		
		for (int i = 0; i < apInfo.size(); i++) {

			PrimitiveType DBName = new PrimitiveType(String.class.getName(),
					apInfo.get(i).getDatabaseName(), PrimitiveTypes.STRING,
					"Database Name "
					, "Database Name");
			
            int index = i+1;
            
			map.put("Database Name "+index, DBName);

			AnalysisLogger.getLogger().debug(
					"In ListDBInfo->Database Name: "
							+ apInfo.get(i).getDatabaseName());

			PrimitiveType url = new PrimitiveType(String.class.getName(),
					apInfo.get(i).address(), PrimitiveTypes.STRING, "URL",
					"URL");

			map.put("URL " +index, url);

			AnalysisLogger.getLogger().debug(
					"In ListDBInfo->URL: " + apInfo.get(i).address());

			PrimitiveType driver = new PrimitiveType(String.class.getName(),
					apInfo.get(i).getDriver(), PrimitiveTypes.STRING,
					"Driver Name", "Driver Name");

			map.put("Driver Name "+index, driver);

			AnalysisLogger.getLogger().debug(
					"In ListDBInfo->Driver Name: "
							+ apInfo.get(i).getDriver());

			PrimitiveType dialect = new PrimitiveType(String.class.getName(),
					apInfo.get(i).getDialect(), PrimitiveTypes.STRING,
					"Dialect Name", "Dialect Name");

			map.put("Dialect Name " +index, dialect);

			AnalysisLogger.getLogger().debug(
					"In ListDBInfo->Dialect Name: "
							+ apInfo.get(i).getDialect());
			
			PrimitiveType platformVersionValue = new PrimitiveType(String.class.getName(),
					platformVersion, PrimitiveTypes.STRING,
					"Platform Version", "Platform Version");

			map.put("Platform Version " +index, platformVersionValue);

			AnalysisLogger.getLogger().debug(
					"In ListDBInfo->Platform Version: "
							+ platformVersion);
			
			

		}
		
		}
		
		catch (IllegalStateException e) {
//			e.printStackTrace();
			
			 AnalysisLogger.getLogger().debug("In ListDBInfo-> ERROR " + e.getMessage());
			throw e;

		} catch (DiscoveryException e1) {
//			e1.printStackTrace();
			
			AnalysisLogger.getLogger().debug("In ListDBInfo-> ERROR " + e1.getMessage());
			
			throw e1;

		} catch (InvalidResultException e2) {
//			e2.printStackTrace();
			
			AnalysisLogger.getLogger().debug("In ListDBInfo-> ERROR " + e2.getMessage());
			
			
			throw e2;

		}
//		catch(IOException e3){
////			e3.printStackTrace();
//			
//			AnalysisLogger.getLogger().debug("In ListDBInfo-> Exception " + e3.getMessage());
//			
//			throw e3;
//		}
		
		catch(Exception e4){
			
//			e4.printStackTrace();
			AnalysisLogger.getLogger().debug("In ListDBInfo-> Exception " + e4.getMessage());
			
			throw e4;
			
		}
		
		
	}

	@Override
	protected void setInputParameters() {

//		AnalysisLogger.getLogger().debug("In ListDBInfo->setting inputs");

		// resource name specified by the user
		addStringInput("ResourceName", "The name of the resource", "");

	}

	@Override
	public void shutdown() {
		AnalysisLogger.getLogger().debug("In ListDBInfo->Shutdown");

	}

	@Override
	public StatisticalType getOutput() {

		AnalysisLogger.getLogger().debug("In ListDBInfo->retrieving outputs");

		// generate a primitive type for the collection
		PrimitiveType output = new PrimitiveType(LinkedHashMap.class.getName(),
				map, PrimitiveTypes.MAP, "ResultsMap"+UUID.randomUUID(), "Results Map");

		return output;

	}

	private List<AccessPoint> retrieveInfo() throws Exception, IllegalStateException, DiscoveryException, InvalidResultException {

		resourceName = getInputParameter("ResourceName");

		if (resourceName != null) {
			resourceName = getInputParameter("ResourceName").trim();
		}

		if ((resourceName == null) || (resourceName.equals(""))) {
			throw new Exception("Warning: insert the resource name");
		}

		// retrieve information about the chosen resource
		DatabasesDiscoverer discovery = new DatabasesDiscoverer();

		List<DBResource> resources = discovery.discover();

		AnalysisLogger.getLogger().debug(
				"In ListDBInfo->number of database resources: "
						+ resources.size());

		check: for (int i = 0; i < resources.size(); i++) {

			if (resources.get(i).getResourceName().toLowerCase().equals(resourceName.toLowerCase())) {
				
				platformVersion = resources.get(i).getPlatformVersion();

				// ap = resources.get(i).getAccessPoints();

				normalizeDBInfo(resources.get(i));

				ap = resources.get(i).getAccessPoints();

				break check;

			}

		}
		return ap;

	}

	private void normalizeDBInfo(DBResource resource) throws Exception {
		
		try{
			int ap = resource.getAccessPoints().size();

			for (int i = 0; i < ap; i++) {
				resource.normalize(i);
			}
			
		}catch (Exception e) {
			AnalysisLogger.getLogger().debug(
					"In ListDBInfo->: Error in normalization process"
							+ e.getMessage());
			
			throw e;
		}

//		int ap = resource.getAccessPoints().size();
//
//		for (int i = 0; i < ap; i++) {
//
//			try {
//				resource.normalize(i);
//			} 
////			catch (IOException e) {
//			catch (IOException e) {
//				// TODO Auto-generated catch block
////				e.printStackTrace();
//				AnalysisLogger.getLogger().debug(
//						"In ListDBInfo->: Error in normalization process"
//								+ e.getMessage());
//				throw e;
//
//			}
//
//		}

	}

}

package org.gcube.dataanalysis.ecoengine.transducers;

import java.util.List;

import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.contentmanagement.lexicalmatcher.utils.DatabaseFactory;
import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;
import org.gcube.dataanalysis.ecoengine.configuration.INFRASTRUCTURE;
import org.gcube.dataanalysis.ecoengine.datatypes.StatisticalType;
import org.gcube.dataanalysis.ecoengine.interfaces.Transducerer;
import org.gcube.dataanalysis.ecoengine.utils.DatabaseUtils;
import org.gcube.dataanalysis.ecoengine.utils.ResourceFactory;
import org.hibernate.SessionFactory;


//implements a creator of tables which simply executes a query

public class QueryExecutor  implements Transducerer {

	protected String query = "";
	protected String finalTableName = "";
	protected String finalTableLabel = "";
	protected float status = 0;
	protected AlgorithmConfiguration config;
	protected static String finalTable = "Table_Name";
	protected static String finalTableLabel$ = "Table_Label";
	
	@Override
	public List<StatisticalType> getInputParameters() {
		return null;
	}

	@Override
	public String getResourceLoad() {
			if ((status > 0) && (status < 100))
				return ResourceFactory.getResources(100f);
			else
				return ResourceFactory.getResources(0f);
	}

	ResourceFactory resourceManager;
	@Override
	public String getResources() {
		if (resourceManager == null)
			resourceManager = new ResourceFactory();
		return resourceManager.getResourceLoad(1);
	}

	@Override
	public float getStatus() {
		return status;
	}

	@Override
	public INFRASTRUCTURE getInfrastructure() {
		return INFRASTRUCTURE.LOCAL;
	}

	@Override
	public StatisticalType getOutput() {
		return null;
	}

	@Override
	public void init() throws Exception {
		
	}

	@Override
	public void setConfiguration(AlgorithmConfiguration config) {
		this.config = config;
	}

	@Override
	public void shutdown() {
	}

	@Override
	public String getDescription() {
		return null;
	}

	@Override
	public void compute() throws Exception {
		SessionFactory dbconnection = null;
		try{
		
		AnalysisLogger.getLogger().trace("Initializing DB Connection");
		dbconnection = DatabaseUtils.initDBSession(config);
		AnalysisLogger.getLogger().trace("Deleting Previous Table "+DatabaseUtils.dropTableStatement(finalTableName));
		try{
			DatabaseFactory.executeSQLUpdate(DatabaseUtils.dropTableStatement(finalTableName), dbconnection);
		}catch(Exception ee){
			
		}
		status = 10;
		AnalysisLogger.getLogger().trace("Deleted");
		AnalysisLogger.getLogger().trace("Executing query: "+query);
		DatabaseFactory.executeSQLUpdate(query, dbconnection);
		AnalysisLogger.getLogger().trace("Executed!");
	} catch (Exception e) {
		AnalysisLogger.getLogger().trace("ERROR:",e);
		throw e;
	} finally {
		if (dbconnection != null)
			try{
			dbconnection.close();
			}catch(Exception e2){}
		status = 100;
		AnalysisLogger.getLogger().trace("Processing Finished and db closed");
	}
		
	}
	
	
	
}

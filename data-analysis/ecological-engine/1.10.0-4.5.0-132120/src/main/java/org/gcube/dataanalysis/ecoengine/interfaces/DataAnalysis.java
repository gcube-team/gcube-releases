package org.gcube.dataanalysis.ecoengine.interfaces;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;
import org.gcube.dataanalysis.ecoengine.configuration.INFRASTRUCTURE;
import org.gcube.dataanalysis.ecoengine.datatypes.PrimitiveType;
import org.gcube.dataanalysis.ecoengine.datatypes.StatisticalType;
import org.gcube.dataanalysis.ecoengine.datatypes.enumtypes.PrimitiveTypes;
import org.gcube.dataanalysis.ecoengine.utils.ResourceFactory;
import org.hibernate.SessionFactory;

/**
 * Implements a mono-thread data analysis process
 * Status is managed outside the class and analysis is simply conducted by initializing and applying procedure 
 * @author coro
 *
 */
public abstract class DataAnalysis implements Evaluator{
	
	protected ResourceFactory resourceManager;
	protected int processedRecords;
	protected float status;
	protected AlgorithmConfiguration config;
	protected SessionFactory connection;
	
	public abstract LinkedHashMap<String, String> analyze() throws Exception;
	
	/**
	 * Processing skeleton : init-analyze-end
	 * @param config
	 * @return
	 * @throws Exception
	 */
	LinkedHashMap<String, String> out;
	public void compute() throws Exception{
		status = 0;
		out = new LinkedHashMap<String, String>();
		try{
			out = analyze();
			
		}catch(Exception e){
			e.printStackTrace();
			throw e;
		}
		finally{
			shutdown();
			status = 100;
		}
		
	}
	
	@Override
	public StatisticalType getOutput() {
		PrimitiveType p = new PrimitiveType(Map.class.getName(), PrimitiveType.stringMap2StatisticalMap(out), PrimitiveTypes.MAP, "AnalysisResult","Analysis Values");
		return p;
	}
	
		
	/**
	 * calculates the number of processed records per unity of time: the timing is calculated internally by the resourceManager and used when the method is interrogated
	 */
	@Override
	public String getResourceLoad() {
		if (resourceManager==null)
			resourceManager = new ResourceFactory();
		return resourceManager.getResourceLoad(processedRecords);
	}

	/**
	 * gets the occupancy of the resource: in this case one thread
	 */
	@Override
	public String getResources() {
		return ResourceFactory.getResources(100f);
	}

	/**
	 * The weight of this procedure is the lowest as it runs on local machine
	 */
	@Override
	public INFRASTRUCTURE getInfrastructure() {
		return INFRASTRUCTURE.LOCAL;
	}

	/**
	 * gets the internal status of the operation
	 */
	@Override
	public float getStatus() {
		return status;
	}

	/**
	 * visualizes the results of the analysis
	 * @param results
	 */
	public static void visualizeResults(HashMap<String,Object> results){

		for (Object key:results.keySet()){
			PrimitiveType keyp = (PrimitiveType) results.get(key);
			System.out.println(key+":"+keyp.getContent());
		}
	} 

	
	public void init() throws Exception {
		init(true);
	}

	public void init(boolean initRapidMiner) throws Exception {
		AnalysisLogger.setLogger(config.getConfigPath() + AlgorithmConfiguration.defaultLoggerFile);
		// init db connection
		connection = AlgorithmConfiguration.getConnectionFromConfig(config);
		if (initRapidMiner)
			config.initRapidMiner();
	}
	
	
	public void setConfiguration(AlgorithmConfiguration config) {
		this.config = config;
	}
	
	public void shutdown() {
		try {
				AnalysisLogger.getLogger().debug("Closing DB connections");
				connection.close();
		} catch (Exception e) {
				AnalysisLogger.getLogger().debug("Error in closing DB connections "+e.getLocalizedMessage());
		}
	}
	
}

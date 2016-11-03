package org.gcube.data.harmonization.occurrence;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.gcube.data.harmonization.occurrence.impl.model.Computation;
import org.gcube.data.harmonization.occurrence.impl.model.PagedRequestSettings;
import org.gcube.data.harmonization.occurrence.impl.model.Resource;
import org.gcube.data.harmonization.occurrence.impl.model.statistical.StatisticalComputation;
import org.gcube.data.harmonization.occurrence.impl.model.statistical.StatisticalFeature;
import org.gcube.data.harmonization.occurrence.impl.model.types.OperationType;
import org.gcube.data.harmonization.occurrence.impl.readers.ParserConfiguration;

public interface Reconciliation {

	/**
	 * 
	 * 
	 * @return
	 */
	public List<Resource> getDataSets()throws Exception;
	
	
	/**
	 * Get Json view of currently connected table
	 * 
	 * @param importedId
	 * @param settings
	 * @return
	 */
	public String getJSONImported(PagedRequestSettings settings)throws Exception;
	
	
	/**
	 * Opens connection to a table for direct inspection
	 * 
	 * @param tableId
	 * @return list of field of selected table
	 * @throws Exception
	 */
	public List<String> openTableInspection(String tableId)throws Exception;
	
	/**
	 * Closes current table connection if any
	 * 
	 * @throws Exception
	 */
	public void closeTableConnection()throws Exception;
	
	
	/**
	 * Returns connection url to the selected table
	 * 
	 * @param tableId
	 * @return
	 * @throws Exception
	 */
	public String getTableUrl(String tableId)throws Exception;
	
	/**
	 * Gets information about available operation on occurrence point data sets
	 * 
	 * @return list of Statistical Operation, one for each available algorithm
	 * 
	 */
	public List<StatisticalFeature> getCapabilities()throws Exception;
	
	
	
	
	/**
	 * Save selected result to workspace 
	 * 
	 * @param operationId
	 * @return saved Item id
	 */
	public File getResourceAsFile(String operationId,OperationType type) throws Exception ;
	
	
	
	/**
	 * Submits a computation to the statistical service
	 * 
	 * @param comp
	 * @param parameters
	 * @return
	 * @throws Exception
	 */
	public String submitOperation(StatisticalComputation comp,Map<String,String> parameters, String title, String description)throws Exception;
	
	/**
	 * 
	 * @return submitted operation list
	 * @throws Exception
	 */
	public List<Computation> getSubmittedOperationList()throws Exception;
	
	/**
	 * Instatiates an OccurrenceStreamer able to parse the passed File and send data to the Statistical Manager 
	 * 
	 * @param toStream
	 * @param configuration
	 * @param tableName
	 * @param description
	 * @return
	 * @throws Exception
	 */
	public OccurrenceStreamer getStreamer(File toStream, ParserConfiguration configuration,String tableName, String description)throws Exception;
	
	
	public void removeComputationById(String id)throws Exception;
}

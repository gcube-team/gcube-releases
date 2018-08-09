package org.gcube.data.analysis.sdmx;

import java.util.Map;

import org.gcube.common.resources.gcore.GenericResource;
import org.gcube.data.analysis.sdmx.converter.BaseTabmanColumnIDConverter;
import org.gcube.data.analysis.sdmx.converter.ColumnIdConverter;
import org.gcube.data.analysis.sdmx.is.ISDataWriter;
import org.gcube.data.analysis.sdmx.is.ISTableIDDataReader;
import org.gcube.data.analysis.sdmx.model.TableAssociationResource;
import org.gcube.data.analysis.sdmx.model.TableIdentificators;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DataInformationProvider {

//	private class DataInformationProviderCache 
//	{
//		private Map<String, TableIdentificators> associations;
//		private final long TIMEOUT = 300000; //5 min
//		private long expiringTime;
//		
//		DataInformationProviderCache() {
//			this.expiringTime = 0;
//		}
//		
//		public void setAssociations (Map<String, GenericResource> resources)
//		{
//			if (associations != null)
//			{
//				this.associations = associations;
//				this.expiringTime = TIMEOUT + new Date().getTime();
//			}
//		}
//		
//		public Map<String, TableIdentificators> getAssociations ()
//		{
//			if (new Date().getTime()> this.expiringTime) return null;
//			else return this.associations;
//				
//		}
//		
//	}

	private static final Logger logger = LoggerFactory.getLogger(DataInformationProvider.class);

	private static  DataInformationProvider instance;
	
	private DataInformationProvider() 
	{
		
	}
	
	public static DataInformationProvider getInstance ()
	{
		if (instance == null) instance = new DataInformationProvider();
		
		return instance;
	}
	
//	private Map<String, TableIdentificators> getAssociations ()
//	{
//		Map<String, TableIdentificators> associations = this.cache.getAssociations();
//		
//		if (associations == null)
//		{
//			ISTableIDDataReader reader = new ISTableIDDataReader();
//			associations = reader.getAssociations();
//			
//			if (associations != null) this.cache.setAssociations(associations);
//		}
//		
//		return associations;
//	}
	
	public TableIdentificators getTableId (String dsName, String dataFlowKey)
	{
		logger.debug("Looking for table id of data flow "+dataFlowKey);
		TableAssociationResource tableAssociations = getTableAssociationResource(dsName);
		TableIdentificators tableId = null;
		
		if (tableAssociations == null) logger.error("Information system not initialized: data not present");
		else
		{
			Map<String, TableIdentificators> associations = tableAssociations.getAssociationsTable();
			tableId = associations.get(dataFlowKey);
		}

		logger.debug("Table id "+tableId);
		return tableId;
		
	}
	
	private TableAssociationResource getTableAssociationResource (String dsName)
	{
		ISTableIDDataReader reader = new ISTableIDDataReader(dsName);
		GenericResource resource = reader.executeQuery();
		TableAssociationResource tableAssociations = null;
		
		if (resource != null) tableAssociations = new TableAssociationResource(resource);
		
		return tableAssociations;
	}
	
	public boolean addNewAssociation (String dsName,String dataFlowKey, String tabularResourceID, String tableID,String timeDimension, String primaryMeasure)
	{
		logger.debug("Updating associations");
		logger.debug("Adding tabular resource id "+tabularResourceID+" table id "+tableID + " for dataflow "+dataFlowKey);
		TableAssociationResource tableAssociations = getTableAssociationResource(dsName);
		ISDataWriter dataWriter = null;
		boolean response = false;
		
		if (tableAssociations == null)
		{
			logger.debug("Data not present for this scope: creating new data");
			dataWriter = new ISDataWriter(dsName);
			dataWriter.addAssociation(dataFlowKey, tabularResourceID, tableID,timeDimension,primaryMeasure);
			response = dataWriter.commit();
		}
		else if (tableAssociations.getAssociationsTable().get(dataFlowKey) == null)
		{
			logger.debug("Current data flow not present: creating a new one...");
			dataWriter = new ISDataWriter(tableAssociations);
			dataWriter.addAssociation(dataFlowKey, tabularResourceID, tableID,timeDimension,primaryMeasure);
			response = dataWriter.commit();
		}
		else
		{
			logger.debug("Data already present for this data flow: nothing to do");
			response = true;
		}
		
		
		logger.debug("Final result "+response);
		return response;
		
	}
	
	public ColumnIdConverter getColumnConverter ()
	{
		return new BaseTabmanColumnIDConverter();
	}
	
	public static String getDataFlowKey (String dataFlowAgency, String dataFlowId, String dataFlowVersion)
	{
		StringBuilder keyBuilder = new StringBuilder(dataFlowAgency);
		keyBuilder.append('.').append(dataFlowId).append('.').append(dataFlowVersion);
		String key = keyBuilder.toString();
		logger.debug("Key "+key);
		return key;
	}

}

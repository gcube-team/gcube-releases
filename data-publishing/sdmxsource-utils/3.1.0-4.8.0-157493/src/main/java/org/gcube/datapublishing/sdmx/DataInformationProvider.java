package org.gcube.datapublishing.sdmx;

import org.gcube.common.resources.gcore.GenericResource;
import org.gcube.datapublishing.sdmx.is.ISDataWriter;
import org.gcube.datapublishing.sdmx.is.ISTableIDDataReader;
import org.gcube.datapublishing.sdmx.model.TableAssociationResource;
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
	
//	public TableIdentificators getTableId (String dataFlowKey)
//	{
//		logger.debug("Looking for table id of data flow "+dataFlowKey);
//		Map<String, TableIdentificators> associations = getAssociations();
//		TableIdentificators tableId = null;
//		
//		if (associations == null) logger.error("Information system not initialized: data not present");
//		else tableId = associations.get(dataFlowKey);
//		
//		logger.debug("Table id "+tableId);
//		return tableId;
//		
//	}
	
	private TableAssociationResource getTableAssociationResource ()
	{
		ISTableIDDataReader reader = new ISTableIDDataReader();
		GenericResource resource = reader.executeQuery();
		TableAssociationResource tableAssociations = null;
		
		if (resource != null) tableAssociations = new TableAssociationResource(resource);
		
		return tableAssociations;
	}
	
	public boolean addNewAssociation (String dataFlowKey, String tabularResourceID, String tableID)
	{
		logger.debug("Updating associations");
		logger.debug("Adding tabular resource id "+tabularResourceID+" table id "+tableID + " for dataflow "+dataFlowKey);
		TableAssociationResource tableAssociations = getTableAssociationResource();
		ISDataWriter dataWriter = null;
		boolean response = false;
		
		if (tableAssociations == null)
		{
			logger.debug("Data not present for this scope: creating new data");
			dataWriter = new ISDataWriter();
			dataWriter.addAssociation(dataFlowKey, tabularResourceID, tableID);
			response = dataWriter.commit();
		}
		else if (tableAssociations.getAssociationsTable().get(dataFlowKey) == null)
		{
			logger.debug("Current data flow not present: creating a new one...");
			dataWriter = new ISDataWriter(tableAssociations);
			dataWriter.addAssociation(dataFlowKey, tabularResourceID, tableID);
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
	
	
	public static String getDataFlowKey (String dataFlowAgency, String dataFlowId, String dataFlowVersion)
	{
		StringBuilder keyBuilder = new StringBuilder(dataFlowAgency);
		keyBuilder.append('.').append(dataFlowId).append('.').append(dataFlowVersion);
		String key = keyBuilder.toString();
		logger.debug("Key "+key);
		return key;
	}
}

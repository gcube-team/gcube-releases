package org.gcube.data.analysis.tabulardata.operation.sdmx.datastructuredefinition.executors;

import org.gcube.data.analysis.tabulardata.model.metadata.common.TableDescriptorMetadata;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.gcube.data.analysis.tabulardata.operation.invocation.OperationInvocation;
import org.gcube.data.analysis.tabulardata.operation.sdmx.configuration.ConfigurationManager;
import org.gcube.data.analysis.tabulardata.operation.sdmx.datastructuredefinition.DataStructureDefinitionWorkerUtils;
import org.gcube.data.analysis.tabulardata.operation.sdmx.datastructuredefinition.SDMXDataOperationExecutor;
import org.gcube.data.analysis.tabulardata.operation.sdmx.datastructuredefinition.beans.SDMXDataBean;
import org.gcube.data.analysis.tabulardata.operation.sdmx.datastructuredefinition.beans.SDMXDataResultBean;
import org.gcube.datapublishing.sdmx.DataInformationProvider;
import org.gcube.datapublishing.sdmx.RegistryInformationProvider;
import org.gcube.datapublishing.sdmx.api.registry.SDMXRegistryClient;
import org.gcube.datapublishing.sdmx.impl.exceptions.SDMXRegistryClientException;
import org.gcube.datapublishing.sdmx.impl.exceptions.SDMXVersionException;
import org.gcube.datapublishing.sdmx.security.model.impl.BasicCredentials;
import org.sdmxsource.sdmx.api.model.beans.codelist.CodelistBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SDMXDataStructurePublisher implements SDMXDataOperationExecutor {

	private final String OPERATION_NAME = "Publishing";
	private Logger log;
	
	private final String 	CODELIST_VERSION_ACTION_PROPERTY = "codelist.version.action";
	
	private enum CODELIST_ACTIONS
	{
		BLOCK ("block"),
		WARNING ("warning"),
		IGNORE ("ignore");
		
		private String action;
		
		private CODELIST_ACTIONS(String action) 
		{
			this.action = action;
		}
		

		public boolean equals (String actionString)
		{
			return this.action.equalsIgnoreCase(actionString);
		}
		
		@Override
		public String toString() {
			return this.action;
		}
	}
	
	private boolean primary;
	
	public  SDMXDataStructurePublisher(boolean isPrimary) {
		this.log = LoggerFactory.getLogger(this.getClass());
		this.primary = isPrimary;
	}
	
	@Override
	public String getOperationName() {
		return OPERATION_NAME;
	}

	private String getTabularResourceId (Table table)
	{
		this.log.debug("Getting tabular resource id");
		String response = null;
		
		try
		{
			TableDescriptorMetadata metadata = table.getMetadata(TableDescriptorMetadata.class);
			response = String.valueOf(metadata.getRefId());
			this.log.debug("Tabular resource id "+response);
			
		} catch (RuntimeException e)
		{
			this.log.error("Unable to get tabular resource id", e);
		}
		

		return response;
	}
	
	@Override
	public SDMXDataResultBean executeOperation(SDMXDataBean inputData, OperationInvocation invocation) {
		this.log.debug("Executing SDMX publishing");
		String tabularResourceID = getTabularResourceId(inputData.getTableBean().getTable());
		String registryUrl = (String) invocation.getParameterInstances().get(DataStructureDefinitionWorkerUtils.REGISTRY_BASE_URL);
		BasicCredentials credentials = RegistryInformationProvider.retrieveCredentials(registryUrl);
		SDMXRegistryClient registryClient = DataStructureDefinitionWorkerUtils.initSDMXClient(registryUrl,credentials.getUsername(), credentials.getPassword());
		String currentType = null;
		SDMXDataResultBean result = new SDMXDataResultBean ();
		
		try {
			
			this.log.debug("Publishing associated codelists...");
			currentType = "codelists";
			
			for (CodelistBean codelist : inputData.getAllCodelists())
			{
				try
				{
					this.log.debug("Publishing codelist "+codelist.getId());
					registryClient.publish(codelist);
					this.log.debug("Codelist published");
				} catch (SDMXVersionException e)
				{
					switch (checkCodelistVersionAction())
					{
					case BLOCK:
						this.log.error("Codelist invalid version action: BLOCK. Abort: sending error message");
						result.setError(this.primary);
						result.addMessage("Invalid codelist version");
						result.setException(e);
						return result;
					case WARNING:
						this.log.warn("Codelist invalid version action: WARNING. Sending warning message");
						result.addMessage(e.getMessage());
					default:
							this.log.warn(e.getMessage(),e);
					}
				}

			}
		
			this.log.debug("Operation on codelists completed");
			currentType = "concepts";
			this.log.debug("Publishing concepts...");
			registryClient.publish(inputData.getConcepts().getImmutableInstance());
			this.log.debug("Concepts published");
			currentType = "data structure definitions";
			this.log.debug("Publishing dsd...");
			registryClient.publish(inputData.getDsd().getImmutableInstance());
			this.log.debug("DSD published");
			currentType = "data flow";
			this.log.debug("Publishing data flow...");
			registryClient.publish(inputData.getDataFlow().getImmutableInstance());
			this.log.debug("Data flow published");
			this.log.debug("Updating dataflow-table association on the Information System");
			registerIdsOnInformationSystem(result,tabularResourceID, inputData.getTableBean().getTable().getId().getValue(), inputData.getDataFlow().getAgencyId(), inputData.getDataFlow().getId(), inputData.getDataFlow().getVersion());
			this.log.debug("Operation completed");
	
			
		} 		
		catch (SDMXVersionException e) {
			log.error("Version error in the pubblication of "+currentType,e);
			result.setError(this.primary);
			result.addMessage("Version error in the pubblication of "+currentType);
			result.setException(e);
		}
		 
		catch (SDMXRegistryClientException e) {
			log.error("SDMX Client error in the pubblication of "+currentType,e);
			result.setError(this.primary);
			result.addMessage("SDMX Client error in the pubblication of "+currentType);
			result.setException(e);
		
		}
		
		return result;
	}
	
	private void registerIdsOnInformationSystem (SDMXDataResultBean result,String tabularResourceID, long tableIDLong, String dataFlowAgency, String dataFlowId, String dataFlowVersion)
	{
		
		this.log.debug("Registering new dataflow on the Information System");
		String dataFlowkey = DataInformationProvider.getDataFlowKey(dataFlowAgency, dataFlowId, dataFlowVersion);
		String tableID = String.valueOf(tableIDLong);
		if (DataInformationProvider.getInstance().addNewAssociation(dataFlowkey, tabularResourceID, tableID)) this.log.debug("Association updated on the Information System");
		else // Unable to update the association 
		{
			this.log.error("Unable to update the association");
			result.addMessage("Unable to upload data on the Information System: data source could not be available");
			
		}

	}
	
	private CODELIST_ACTIONS checkCodelistVersionAction ()
	{
		//0 = ignore 1 = warning 2 = block
		String actionParameter = ConfigurationManager.getInstance().getValue(CODELIST_VERSION_ACTION_PROPERTY);
		this.log.debug("Codelist action parameter "+actionParameter);
		
		if (actionParameter == null || CODELIST_ACTIONS.BLOCK.equals(actionParameter)) return CODELIST_ACTIONS.BLOCK;
		else if (CODELIST_ACTIONS.WARNING.equals(actionParameter)) return CODELIST_ACTIONS.WARNING;
		else return CODELIST_ACTIONS.IGNORE;
		
	}

	@Override
	public boolean isPrimaryOperation() {

		return this.primary;
	}

	@Override
	public boolean isDataAware() {

		return false;
	}


}

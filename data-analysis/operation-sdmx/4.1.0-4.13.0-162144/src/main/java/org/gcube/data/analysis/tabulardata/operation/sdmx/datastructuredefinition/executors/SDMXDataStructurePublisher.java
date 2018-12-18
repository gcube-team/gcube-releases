package org.gcube.data.analysis.tabulardata.operation.sdmx.datastructuredefinition.executors;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.gcube.data.analysis.sdmx.DataInformationProvider;
import org.gcube.data.analysis.tabulardata.model.metadata.common.TableDescriptorMetadata;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.gcube.data.analysis.tabulardata.operation.invocation.OperationInvocation;
import org.gcube.data.analysis.tabulardata.operation.sdmx.configuration.ConfigurationManager;
import org.gcube.data.analysis.tabulardata.operation.sdmx.datastructuredefinition.DataStructureDefinitionWorkerUtils;
import org.gcube.data.analysis.tabulardata.operation.sdmx.datastructuredefinition.SDMXDataOperationExecutor;
import org.gcube.data.analysis.tabulardata.operation.sdmx.datastructuredefinition.beans.SDMXDataBean;
import org.gcube.data.analysis.tabulardata.operation.sdmx.datastructuredefinition.beans.SDMXDataResultBean;
import org.gcube.data.analysis.tabulardata.operation.sdmx.excel.impl.beans.TableBean;
import org.gcube.datapublishing.sdmx.DataSourceInformationProvider;
import org.gcube.datapublishing.sdmx.RegistryInformationProvider;
import org.gcube.datapublishing.sdmx.api.registry.SDMXRegistryClient;
import org.gcube.datapublishing.sdmx.api.registry.SDMXRegistryClient.Detail;
import org.gcube.datapublishing.sdmx.api.registry.SDMXRegistryClient.References;
import org.gcube.datapublishing.sdmx.impl.exceptions.SDMXRegistryClientException;
import org.gcube.datapublishing.sdmx.impl.exceptions.SDMXVersionException;
import org.gcube.datapublishing.sdmx.model.DataSource;
import org.gcube.datapublishing.sdmx.security.model.impl.BasicCredentials;
import org.sdmxsource.sdmx.api.model.beans.SdmxBeans;
import org.sdmxsource.sdmx.api.model.beans.base.DataProviderBean;
import org.sdmxsource.sdmx.api.model.beans.base.DataProviderSchemeBean;
import org.sdmxsource.sdmx.api.model.beans.base.TextTypeWrapper;
import org.sdmxsource.sdmx.api.model.beans.codelist.CodelistBean;
import org.sdmxsource.sdmx.api.model.beans.datastructure.DataflowBean;
import org.sdmxsource.sdmx.api.model.mutable.base.DataProviderMutableBean;
import org.sdmxsource.sdmx.api.model.mutable.base.DataProviderSchemeMutableBean;
import org.sdmxsource.sdmx.api.model.mutable.base.TextTypeWrapperMutableBean;
import org.sdmxsource.sdmx.api.model.mutable.registry.ProvisionAgreementMutableBean;
import org.sdmxsource.sdmx.api.model.mutable.registry.RegistrationMutableBean;
import org.sdmxsource.sdmx.sdmxbeans.model.mutable.base.DataProviderMutableBeanImpl;
import org.sdmxsource.sdmx.sdmxbeans.model.mutable.base.TextTypeWrapperMutableBeanImpl;
import org.sdmxsource.sdmx.sdmxbeans.model.mutable.registry.ProvisionAgreementMutableBeanImpl;
import org.sdmxsource.sdmx.sdmxbeans.model.mutable.registry.RegistrationMutableBeanImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

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
	
	private DataProviderBean getDataSource (SDMXRegistryClient registryClient, String dataFlowAgency)
	{
		this.log.debug("Checking data sources and registrations");
		DataSource dataSource = DataSourceInformationProvider.getDataSource();
		DataProviderBean response = null;
		
		if (dataSource == null)
		{
			this.log.debug("No Data Source found for this VRE: data will not be exported");
		}
		else
		{
			this.log.debug("Data Source found");
			
			try
			{
				SdmxBeans dataProviderSchemeBean =registryClient.getDataProviderScheme(dataFlowAgency, "", "", Detail.full, References.none);
				Set<DataProviderSchemeBean> dataProviderSchemas = dataProviderSchemeBean.getDataProviderSchemes();
				Iterator<DataProviderSchemeBean> dataProviderSchemeIterator = dataProviderSchemas.iterator();
				this.log.debug("Found "+dataProviderSchemas.size()+ " schemas");
				
				if (dataProviderSchemeIterator.hasNext())
				{
					DataProviderSchemeBean dataProviderScheme = dataProviderSchemeIterator.next();
					this.log.debug("Data provider scheme found "+dataProviderScheme.getId());

					List<DataProviderBean> dataProviders = dataProviderScheme.getItems();
					this.log.debug("Data providers "+dataProviders.size());
					
					Iterator<DataProviderBean> dataProvidersIterator = dataProviders.iterator();
					this.log.debug("Data providr");
					
					
					while (dataProvidersIterator.hasNext() && response ==null)
					{
						DataProviderBean dataProvider = dataProvidersIterator.next();
						this.log.debug("Data Provider ID "+dataProvider.getId());
						
						if (dataSource.getName().equals(dataProvider.getId()))
						{
							this.log.debug("Data Provider found");
							response = dataProvider;
							
						}

					}
					
					if (response == null)
					{
						this.log.debug("Unable to find a suitable data provider");
						this.log.debug("Creating...");
						DataProviderMutableBean dataProviderMutableBean = new DataProviderMutableBeanImpl();
						dataProviderMutableBean.setId(dataSource.getName());
						List<TextTypeWrapperMutableBean> names = Lists.newArrayList();
						names.add(new TextTypeWrapperMutableBeanImpl("en",dataSource.getName()));
						dataProviderMutableBean.setNames(names);
						DataProviderSchemeMutableBean dataProviderSchemeMutable = dataProviderScheme.getMutableInstance();
						dataProviderSchemeMutable.addItem(dataProviderMutableBean);
						DataProviderSchemeBean updatedDataProviderScheme = dataProviderSchemeMutable.getImmutableInstance();
						registryClient.publish(updatedDataProviderScheme,false);
						this.log.debug("New Data Provider added");
						response = updatedDataProviderScheme.getItems().get(updatedDataProviderScheme.getItems().size()-1);
						
						
					}
				}
					
				
				
				
			} catch (SDMXRegistryClientException e)
			{
				this.log.debug("Unable to check Data Sources Registrations on the registry: data will not be exported",e);
			}
			
			
		}
		
		return response;
	}
	
	private void defineProvisionAgreement (DataProviderBean dataProvider,SDMXRegistryClient registryClient,DataflowBean dataFlow,SDMXDataResultBean result)
	{
		this.log.debug("Defining a provision agreement");		

		try
		{
			this.log.debug("Defining provision agreement");
			ProvisionAgreementMutableBean provisionAgreement = new ProvisionAgreementMutableBeanImpl();
			provisionAgreement.setAgencyId(dataFlow.getAgencyId());
			provisionAgreement.setVersion(dataFlow.getVersion());
			provisionAgreement.setId(dataFlow.getId());
			
			List<TextTypeWrapperMutableBean> names = new ArrayList<>();
			
			for (TextTypeWrapper ttw :dataFlow.getNames())
			{
				this.log.debug("Adding dataflow name "+ttw);
				names.add(new TextTypeWrapperMutableBeanImpl(ttw));
			}
			
			provisionAgreement.setNames(names);
			provisionAgreement.setDataproviderRef(dataProvider.asReference());
			provisionAgreement.setStructureUsage(dataFlow.asReference());
			this.log.debug("Provision agreement defined");
			registryClient.publish(provisionAgreement.getImmutableInstance());
		} catch (SDMXRegistryClientException e)
		{
			this.log.error("Unable to publish the new provision agreement",e);
			result.addMessage("Unable to publish the new provision agreement: "+e.getMessage());
		}

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
			DataflowBean dataFlow = inputData.getDataFlow().getImmutableInstance();
			registryClient.publish(dataFlow);
			this.log.debug("Data flow published");
			DataProviderBean dataProvider = getDataSource(registryClient, dataFlow.getAgencyId());
			
			if (dataProvider == null)
			{
				
				this.log.warn("Unable to find a suitable data provider on the registry: data will not be exported");
				result.addMessage("Data Provider not found");
			}
			else
			{
				this.log.debug("Data provider found");
				defineProvisionAgreement (dataProvider,registryClient,dataFlow,result);
				this.log.debug("Updating dataflow-table association on the Information System");
				registerIdsOnInformationSystem(result,dataProvider.getId(),tabularResourceID, inputData.getTableBean(), dataFlow.getAgencyId(),dataFlow.getId(), dataFlow.getVersion());
				this.log.debug("Operation completed");
			}
	
			
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
	
	//private void registerIdsOnInformationSystem (SDMXDataResultBean result,String tabularResourceID, long tableIDLong, String dataFlowAgency, String dataFlowId, String dataFlowVersion)

	private void registerIdsOnInformationSystem (SDMXDataResultBean result, String dataSourceId, String tabularResourceID, TableBean tableBean, String dataFlowAgency, String dataFlowId, String dataFlowVersion)
	{
		
		this.log.debug("Registering new dataflow on the Information System");
		String dataFlowkey = DataInformationProvider.getDataFlowKey(dataFlowAgency, dataFlowId, dataFlowVersion);
		String tableID = String.valueOf(tableBean.getTable().getId().getValue());
		if (DataInformationProvider.getInstance().addNewAssociation(dataSourceId,dataFlowkey, tabularResourceID, tableID,tableBean.getTimeDimensionColumn().getColumn().getLocalId().getValue(), tableBean.getPrimaryMeasure().getColumn().getLocalId().getValue()))
		{
			this.log.debug("Association updated on the Information System");
			
		}
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

package org.gcube.data.analysis.tabulardata.operation.sdmx.conceptscheme;

import java.net.URI;
import java.net.URISyntaxException;

import org.gcube.data.analysis.tabulardata.model.resources.InternalURI;
import org.gcube.data.analysis.tabulardata.model.resources.ResourceType;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.gcube.data.analysis.tabulardata.operation.invocation.OperationInvocation;
import org.gcube.data.analysis.tabulardata.operation.sdmx.WorkerUtils;
import org.gcube.data.analysis.tabulardata.operation.sdmx.datastructuredefinition.ds.DataSourceConfigurationBean;
import org.gcube.data.analysis.tabulardata.operation.sdmx.security.Credentials;
import org.gcube.data.analysis.tabulardata.operation.worker.exceptions.WorkerException;
import org.gcube.data.analysis.tabulardata.operation.worker.results.ResourcesResult;
import org.gcube.data.analysis.tabulardata.operation.worker.results.resources.ImmutableURIResult;
import org.gcube.data.analysis.tabulardata.operation.worker.types.ResourceCreatorWorker;
import org.gcube.datapublishing.sdmx.api.registry.SDMXRegistryClient;
import org.gcube.datapublishing.sdmx.impl.exceptions.SDMXRegistryClientException;
import org.sdmxsource.sdmx.api.model.mutable.conceptscheme.ConceptSchemeMutableBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SDMXConceptSchemeExporter extends ResourceCreatorWorker {

	private Logger log;

	private Table table;

	private OperationInvocation invocation;

	private String registryUrl;

	private String targetAgency;

	private String targetId;

	private String targetVersion;

	private String username;
	
	private String password;
	
	private static String errorMessage = "Unable to complete export procedure";

	
	
	public SDMXConceptSchemeExporter(Table table, OperationInvocation invocation) {
		super(invocation);
		log = LoggerFactory.getLogger(this.getClass());
		this.table = table;
		this.invocation = invocation;
		this.username = null;
		this.password = null;
	}
	
	@Override
	protected ResourcesResult execute() throws WorkerException {
		try {
			DataSourceConfigurationBean datasourceConfigurationBean = new DataSourceConfigurationBean();
			datasourceConfigurationBean.setTable_id(this.table.getName());
			retrieveParameters(datasourceConfigurationBean);
			updateProgress(0.1f,"Creating beans");
			SDMXConceptSchemeGenerator conceptSchemeGenerator = new SDMXConceptSchemeGenerator(this.table, this.targetId,this.targetAgency, this.targetVersion);
			ConceptSchemeMutableBean conceptScheme = conceptSchemeGenerator.createConceptSchemeBean();
			updateProgress(0.2f,"Populating data structure");
			conceptSchemeGenerator.populateConceptsScheme(conceptScheme);
			updateProgress(0.6f,"Publishing");
			publishData(conceptScheme);
			updateProgress(0.8f,"Finalizing");

			return new ResourcesResult(new ImmutableURIResult(new InternalURI(new URI(registryUrl)), "Dataset SDMX export" , 
					String.format("%dataset/%s/%s/%s/", registryUrl, targetAgency, targetId, targetVersion), ResourceType.SDMX));
		} catch (RuntimeException e) {
			log.error(errorMessage, e);
			throw new WorkerException(errorMessage, e);
		} catch (URISyntaxException e) {
			throw new WorkerException(String.format("exported url %s not valid",registryUrl),e);
		}
	}


	private void retrieveParameters(DataSourceConfigurationBean datasourceConfigurationBean) {
		registryUrl = (String) invocation.getParameterInstances().get(ConceptSchemeWorkerUtils.REGISTRY_BASE_URL);
		targetAgency = (String) invocation.getParameterInstances().get(ConceptSchemeWorkerUtils.AGENCY);
		targetId = (String) invocation.getParameterInstances().get(ConceptSchemeWorkerUtils.ID);
		targetVersion = (String) invocation.getParameterInstances().get(ConceptSchemeWorkerUtils.VERSION);
		Credentials credentials = WorkerUtils.retrieveCredentials(registryUrl);
		username = credentials.getUsername();
		password = credentials.getPassword();
		String observationValue = (String) this.invocation.getParameterInstances().get(ConceptSchemeWorkerUtils.OBS_VALUE_COLUMN);
		datasourceConfigurationBean.setObservationValue(observationValue);
	}


	/**
	 * 
	 * @param dsd
	 * @param concepts
	 * @param dataFlow
	 * @throws WorkerException
	 */
	private void publishData(ConceptSchemeMutableBean concepts) throws WorkerException {
		String url = (String) invocation.getParameterInstances().get(ConceptSchemeWorkerUtils.REGISTRY_BASE_URL);
		SDMXRegistryClient registryClient = ConceptSchemeWorkerUtils.initSDMXClient(url,this.username, this.password);

		try {
			log.debug("Publishing concepts...");
			registryClient.publish(concepts.getImmutableInstance());
			log.debug("Concepts published");
		} catch (SDMXRegistryClientException e) {
			throw new WorkerException("Unable to publish concepts on registry.", e);
		}
		
	}
	


	
	

}
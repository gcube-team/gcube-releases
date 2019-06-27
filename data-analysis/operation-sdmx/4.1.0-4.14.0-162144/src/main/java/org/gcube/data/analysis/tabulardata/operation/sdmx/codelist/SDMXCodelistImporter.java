package org.gcube.data.analysis.tabulardata.operation.sdmx.codelist;

import java.util.Map;
import java.util.Set;

import org.gcube.data.analysis.tabulardata.cube.CubeManager;
import org.gcube.data.analysis.tabulardata.cube.data.connection.DatabaseConnectionProvider;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.gcube.data.analysis.tabulardata.operation.invocation.OperationInvocation;
import org.gcube.data.analysis.tabulardata.operation.sdmx.WorkerUtils;
import org.gcube.data.analysis.tabulardata.operation.worker.exceptions.WorkerException;
import org.gcube.data.analysis.tabulardata.operation.worker.results.ImmutableWorkerResult;
import org.gcube.data.analysis.tabulardata.operation.worker.results.WorkerResult;
import org.gcube.data.analysis.tabulardata.operation.worker.types.DataWorker;
import org.gcube.datapublishing.sdmx.RegistryInformationProvider;
import org.gcube.datapublishing.sdmx.api.registry.SDMXRegistryClient;
import org.gcube.datapublishing.sdmx.api.registry.SDMXRegistryClient.Detail;
import org.gcube.datapublishing.sdmx.api.registry.SDMXRegistryClient.References;
import org.gcube.datapublishing.sdmx.impl.exceptions.SDMXRegistryClientException;
import org.gcube.datapublishing.sdmx.security.model.impl.BasicCredentials;
import org.sdmxsource.sdmx.api.model.beans.SdmxBeans;
import org.sdmxsource.sdmx.api.model.beans.codelist.CodelistBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SDMXCodelistImporter extends DataWorker {

	private CubeManager cubeManager;
	private DatabaseConnectionProvider connectionProvider;
	private Logger log;
	private OperationInvocation operationInvocation;
	private String url;
	private String agency;
	private String id;
	private String version;
	private String username;
	private String password;

	
	public SDMXCodelistImporter(CubeManager cubeManager, DatabaseConnectionProvider connectionProvider,
			OperationInvocation operationInvocation) {
		super(operationInvocation);
		this.log = LoggerFactory.getLogger(this.getClass());
		this.cubeManager = cubeManager;
		this.connectionProvider = connectionProvider;
		this.operationInvocation = operationInvocation;
		this.username = null;
		this.password = null;
		
	}
	
	@Override
	protected WorkerResult execute() throws WorkerException {
		retrieveParameters();
		updateProgress(0.1f,"Connecting to repository");
		try {
			SDMXRegistryClient registryClient = WorkerUtils.initSDMXClient(url,this.username, this.password);
			updateProgress(0.2f,"Getting beans");
			CodelistBean codelist = getCodelistBean(registryClient);
			SDMXBaseCodelistImporter baseImporter = new SDMXBaseCodelistImporter(cubeManager, codelist, connectionProvider, url);
			updateProgress(0.4f,"Importing data");
			Table table = baseImporter.importCodelist();
			updateProgress(0.8f,"Creating resource");
			Table agencyTable = baseImporter.addStructureMetadata(codelist.getAgencyId(),codelist.getVersion(),table);
			Table resultTable = baseImporter.addImportMetadata(agencyTable.getId());
			return new ImmutableWorkerResult(resultTable);
		} catch (RuntimeException e) {
			log.error("Unable to complete import procedure", e);
			throw new WorkerException("Unable to complete import procedure", e);
			
		}
	}

	

	private void retrieveParameters() {
		Map<String, Object> parameters = operationInvocation.getParameterInstances();
		url = (String) parameters.get(WorkerUtils.REGISTRY_BASE_URL);
		agency = (String) parameters.get(WorkerUtils.AGENCY);
		id = (String) parameters.get(WorkerUtils.ID);
		version = (String) parameters.get(WorkerUtils.VERSION);
		BasicCredentials credentials = RegistryInformationProvider.retrieveCredentials(url);
		username = credentials.getUsername();
		password = credentials.getPassword();


	}

	

	private CodelistBean getCodelistFromBeans(SdmxBeans sdmxBeans, String agencyId, String id, String version) {
		Set<CodelistBean> codelistBeans = sdmxBeans.getCodelists();
		log.debug("Retrieved codelists: " + codelistBeans);

		if (codelistBeans.size() < 1)
			throw new RuntimeException(String.format(
					"Unable to find a codelist with the given coordinates: [%s,%s,%s]", agencyId, id, version));

		if (codelistBeans.size() > 1)
			throw new RuntimeException(String.format(
					"Found too many codelists for the given coordinates: [%s,%s,%s]", agencyId, id, version));

		return codelistBeans.iterator().next();
	}

	private CodelistBean getCodelistBean(SDMXRegistryClient registryClient) {
		SdmxBeans sdmxBeans = null;
		try {
			sdmxBeans = registryClient.getCodelist(agency, id, version, Detail.full, References.none);
			log.debug("Retrieved beans: " + sdmxBeans);

		} catch (SDMXRegistryClientException e) {
			String msg = "Error occurred while retrieving codelist.";
			log.error(msg, e);
			throw new RuntimeException(msg);
		}

		CodelistBean codelist = getCodelistFromBeans(sdmxBeans, agency, id, version);
		return codelist;
	}







}
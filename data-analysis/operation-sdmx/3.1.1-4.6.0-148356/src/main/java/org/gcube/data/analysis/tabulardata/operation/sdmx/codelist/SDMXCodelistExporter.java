package org.gcube.data.analysis.tabulardata.operation.sdmx.codelist;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.SQLException;

import org.gcube.data.analysis.tabulardata.cube.data.connection.DatabaseConnectionProvider;
import org.gcube.data.analysis.tabulardata.model.resources.InternalURI;
import org.gcube.data.analysis.tabulardata.model.resources.ResourceType;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.gcube.data.analysis.tabulardata.operation.invocation.OperationInvocation;
import org.gcube.data.analysis.tabulardata.operation.sdmx.WorkerUtils;
import org.gcube.data.analysis.tabulardata.operation.sdmx.security.Credentials;
import org.gcube.data.analysis.tabulardata.operation.worker.exceptions.WorkerException;
import org.gcube.data.analysis.tabulardata.operation.worker.results.ResourcesResult;
import org.gcube.data.analysis.tabulardata.operation.worker.results.resources.ImmutableURIResult;
import org.gcube.data.analysis.tabulardata.operation.worker.types.ResourceCreatorWorker;
import org.gcube.datapublishing.sdmx.api.registry.SDMXRegistryClient;
import org.gcube.datapublishing.sdmx.impl.exceptions.SDMXRegistryClientException;
import org.sdmxsource.sdmx.api.model.beans.codelist.CodelistBean;
import org.sdmxsource.sdmx.api.model.mutable.codelist.CodelistMutableBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SDMXCodelistExporter extends ResourceCreatorWorker {

	private static Logger log = LoggerFactory.getLogger(SDMXCodelistExporter.class);

	private Table table;

	private OperationInvocation invocation;

	private DatabaseConnectionProvider connectionProvider;

	private String targetUrl;

	private String targetAgency;

	private String targetId;

	private String targetVersion;
	
	private String username;
	
	private String password;

	private static String errorMessage = "Unable to complete export procedure";;

	public SDMXCodelistExporter(Table table, OperationInvocation invocation,
			DatabaseConnectionProvider connectionProvider) {
		super(invocation);
		this.table = table;
		this.invocation = invocation;
		this.connectionProvider = connectionProvider;
		this.username = null;
		this.password = null;
	}
	
	@Override
	protected ResourcesResult execute() throws WorkerException {
		try {
			retrieveParameters();
			SDMXCodelistGenerator codelistGenerator = new SDMXCodelistGenerator(this.table, this.connectionProvider, this.targetAgency,
					this.targetId, this.targetVersion);
			updateProgress(0.1f,"Creating beans");
			CodelistMutableBean codelist = codelistGenerator.createBaseCodelistBean();
			updateProgress(0.2f,"Populating codelist");
			codelistGenerator.populateCodelistWithCodes(codelist);
			updateProgress(0.6f,"Publishing");
			publishCodelist(codelist);
			updateProgress(0.8f,"Finalizing");
			
			return new ResourcesResult(new ImmutableURIResult(new InternalURI(new URI(targetUrl)), "Codelist SDMX export" , 
					String.format("%scodelist/%s/%s/%s/", targetUrl, targetAgency, targetId, targetVersion), ResourceType.SDMX));
		} catch (RuntimeException e) {
			log.error(errorMessage, e);
			throw new WorkerException(errorMessage, e);
		}catch (SQLException e) {
			log.error(errorMessage, e);
			throw new WorkerException(errorMessage, e);
		}
		
		catch (URISyntaxException e) {
			throw new WorkerException(String.format("exported url %s not valid",targetUrl),e);
		}
	}

	private void retrieveParameters() {
		targetUrl = (String) invocation.getParameterInstances().get(WorkerUtils.REGISTRY_BASE_URL);
		targetAgency = (String) invocation.getParameterInstances().get(WorkerUtils.AGENCY);
		targetId = (String) invocation.getParameterInstances().get(WorkerUtils.ID);
		targetVersion = (String) invocation.getParameterInstances().get(WorkerUtils.VERSION);
		Credentials credentials = WorkerUtils.retrieveCredentials(targetUrl);
		username = credentials.getUsername();
		password = credentials.getPassword();
	}


	private void publishCodelist(CodelistMutableBean codelist) throws WorkerException {
		String url = (String) invocation.getParameterInstances().get(WorkerUtils.REGISTRY_BASE_URL);
		log.debug("Publishing data in "+url);
		SDMXRegistryClient registryClient = WorkerUtils.initSDMXClient(url,this.username,this.password);

		log.debug("SDMX client intialized");
		
		try {
			log.debug("Generating immutable instance");
			CodelistBean codeList = codelist.getImmutableInstance();
			log.debug("Immutable instance generated");
			registryClient.publish(codeList);
			log.debug("Codelist publised");
		} catch (SDMXRegistryClientException e) {
			throw new WorkerException("Unable to publish codelist on registry.", e);
		}
	}




//	public static void main(String[] args) throws WorkerException {
//
//		ProxyAuthenticator authenticator = new ProxyAuthenticator();
//		authenticator.setProxyHost("proxy.eng.it");
//		authenticator.setProxyPort("3128");
//		authenticator.setProxyUserName("cirformi");
//		authenticator.setProxyPassword("sys64738");
//		authenticator.configure();
//		if (authenticator.isActive()) Authenticator.setDefault(authenticator);
//		
//		CodelistMutableBean bean = new CodelistMutableBeanImpl();
//		bean.setAgencyId("ENG");
//		bean.setVersion("1.0");
//		bean.setId("NEW_CODE_LIST");
//		bean.addName("en", "table4ciro_cloned");
//		SDMXCodelistExporter exporter = new SDMXCodelistExporter(null, null, null);
//		CodeMutableBean code = new CodeMutableBeanImpl();
//		code.addName("IT", "ciao");
//		code.setId("UK");
//		bean.addItem(code);
//		exporter.publishCodelist2(bean);
//		
//	}
	

}
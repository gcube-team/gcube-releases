package org.gcube.data.analysis.tabulardata.operation.sdmx.codelist;

import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLException;

import org.gcube.data.analysis.tabulardata.cube.data.connection.DatabaseConnectionProvider;
import org.gcube.data.analysis.tabulardata.model.metadata.common.TableDescriptorMetadata;
import org.gcube.data.analysis.tabulardata.model.resources.ResourceType;
import org.gcube.data.analysis.tabulardata.model.resources.SDMXResource;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.gcube.data.analysis.tabulardata.operation.invocation.OperationInvocation;
import org.gcube.data.analysis.tabulardata.operation.sdmx.WorkerUtils;
import org.gcube.data.analysis.tabulardata.operation.sdmx.agencies.AgencyProvider;
import org.gcube.data.analysis.tabulardata.operation.sdmx.agencies.exceptions.AgencyException;
import org.gcube.data.analysis.tabulardata.operation.sdmx.configuration.ConfigurationManager;
import org.gcube.data.analysis.tabulardata.operation.worker.exceptions.WorkerException;
import org.gcube.data.analysis.tabulardata.operation.worker.results.ResourcesResult;
import org.gcube.data.analysis.tabulardata.operation.worker.results.resources.ImmutableSDMXResource;
import org.gcube.data.analysis.tabulardata.operation.worker.types.ResourceCreatorWorker;
import org.gcube.datapublishing.sdmx.RegistryInformationProvider;
import org.gcube.datapublishing.sdmx.api.registry.SDMXRegistryClient;
import org.gcube.datapublishing.sdmx.impl.exceptions.SDMXRegistryClientException;
import org.gcube.datapublishing.sdmx.security.model.impl.BasicCredentials;
import org.sdmxsource.sdmx.api.model.beans.codelist.CodelistBean;
import org.sdmxsource.sdmx.api.model.mutable.codelist.CodelistMutableBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SDMXCodelistExporter extends ResourceCreatorWorker {

	private final Logger log;
	private Table table;
	private OperationInvocation invocation;
	private DatabaseConnectionProvider connectionProvider;
	private String targetUrl;
	private String targetId;
	private String username;
	private String password;
	private final String errorMessage = "Unable to complete codelist export procedure";
	private boolean codelistVersionUser;
	private final String CODELIST_VERSION_USER = "codelist.version.user";

	public SDMXCodelistExporter(Table table, OperationInvocation invocation,
			DatabaseConnectionProvider connectionProvider) {
		super(invocation);
		this.log  = LoggerFactory.getLogger(this.getClass());
		this.table = table;
		this.invocation = invocation;
		this.connectionProvider = connectionProvider;
		this.username = null;
		this.password = null;
		String codelistVersionUserString = ConfigurationManager.getInstance().getValue(CODELIST_VERSION_USER);
		this.codelistVersionUser =  codelistVersionUserString != null && codelistVersionUserString.equalsIgnoreCase("true");
	}
	

	
	@Override
	protected ResourcesResult execute() throws WorkerException 
	{
		try {
			retrieveParameters();
			TableDescriptorMetadata tableDescriptorMetadata = this.table.getMetadata(TableDescriptorMetadata.class);
			String targetAgency = getTargetAgency(tableDescriptorMetadata);
			String targetVersion = getTargetVersion (tableDescriptorMetadata);
			SDMXCodelistGenerator codelistGenerator = new SDMXCodelistGenerator(this.table, this.connectionProvider, targetAgency,
					this.targetId, targetVersion);
			updateProgress(0.1f,"Creating beans");
			CodelistMutableBean codelist = codelistGenerator.createBaseCodelistBean();
			updateProgress(0.2f,"Populating codelist");
			codelistGenerator.populateCodelistWithCodes(codelist);
			updateProgress(0.6f,"Publishing");
			publishCodelist(codelist);
			updateProgress(0.8f,"Finalizing");
			SDMXResource sdmxResource = new SDMXResource(new URL(WorkerUtils.getResourceURI(this.targetUrl)), this.targetId, targetVersion, targetAgency,  SDMXResource.TYPE.CODE_LIST);
						
			return new ResourcesResult(new ImmutableSDMXResource(sdmxResource, "Codelist SDMX export" , 
					sdmxResource.toString(), ResourceType.SDMX));
		} catch (RuntimeException e) 
		{
			log.error(errorMessage, e);
			throw new WorkerException(errorMessage, e);
		}catch (SQLException e) 
		{
			log.error(errorMessage, e);
			throw new WorkerException(errorMessage, e);
		}
		
		catch (MalformedURLException e) 
		{
			throw new WorkerException(String.format("exported url %s not valid",targetUrl),e);
		}
	}

	
	
	private void retrieveParameters() 
	{
		targetUrl = (String) invocation.getParameterInstances().get(WorkerUtils.REGISTRY_BASE_URL);
		targetId = (String) invocation.getParameterInstances().get(WorkerUtils.ID);
		BasicCredentials credentials = RegistryInformationProvider.retrieveCredentials(targetUrl);
		username = credentials.getUsername();
		password = credentials.getPassword();
		
	}

	private String getTargetAgency (TableDescriptorMetadata metadata)
	{
		String userAgency = (String) this.invocation.getParameterInstances().get(WorkerUtils.AGENCY);
		
		if (metadata != null)
		{
			try
			{
				return AgencyProvider.getInstance().getAgency(metadata.getAgency(), userAgency, new String [] {this.targetUrl,this.username,this.password});
							
			} catch (AgencyException e)
			{
				this.log.error("Unable contact the registry to manage agencies",e);
			}
		}
		
		return userAgency;
		
		
	}
	
	private String getTargetVersion (TableDescriptorMetadata metadata)
	{
		if (metadata != null && !this.codelistVersionUser)
		{
			String metadataVersion = metadata.getVersion();
			this.log.debug("Table version = "+metadataVersion);
			return metadataVersion;
		}
		
		else return (String) this.invocation.getParameterInstances().get(WorkerUtils.VERSION);
			
	}

	private void publishCodelist(CodelistMutableBean codelist) throws WorkerException 
	{
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
			log.error("Unable to publish codelist on registry",e);
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
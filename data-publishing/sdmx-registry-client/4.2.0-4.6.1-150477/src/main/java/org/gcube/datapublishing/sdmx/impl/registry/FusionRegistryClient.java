package org.gcube.datapublishing.sdmx.impl.registry;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Calendar;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.io.IOUtils;
import org.gcube.datapublishing.sdmx.SDMXSourceProvider;
import org.gcube.datapublishing.sdmx.api.model.SDMXRegistryDescriptor;
import org.gcube.datapublishing.sdmx.api.model.SDMXRegistryInterfaceType;
import org.gcube.datapublishing.sdmx.api.model.security.Credentials;
import org.gcube.datapublishing.sdmx.api.model.versioning.Version;
import org.gcube.datapublishing.sdmx.api.registry.SDMXRegistryClient;
import org.gcube.datapublishing.sdmx.impl.exceptions.RegistryClientExceptionFactory;
import org.gcube.datapublishing.sdmx.impl.exceptions.SDMXRegistryClientException;
import org.gcube.datapublishing.sdmx.impl.exceptions.SDMXVersionException;
import org.gcube.datapublishing.sdmx.impl.model.Base64Credentials;
import org.gcube.datapublishing.sdmx.impl.reports.OperationStatus;
import org.gcube.datapublishing.sdmx.impl.reports.SubmissionReport;
import org.sdmx.resources.sdmxml.schemas.v21.message.BaseHeaderType;
import org.sdmx.resources.sdmxml.schemas.v21.message.RegistryInterfaceDocument;
import org.sdmx.resources.sdmxml.schemas.v21.message.RegistryInterfaceType;
import org.sdmx.resources.sdmxml.schemas.v21.registry.QueryRegistrationRequestType;
import org.sdmx.resources.sdmxml.schemas.v21.registry.QueryTypeType;
import org.sdmxsource.sdmx.api.constants.STRUCTURE_OUTPUT_FORMAT;
import org.sdmxsource.sdmx.api.manager.output.StructureWriterManager;
import org.sdmxsource.sdmx.api.manager.parse.StructureParsingManager;
import org.sdmxsource.sdmx.api.model.StructureWorkspace;
import org.sdmxsource.sdmx.api.model.beans.SdmxBeans;
import org.sdmxsource.sdmx.api.model.beans.base.AgencySchemeBean;
import org.sdmxsource.sdmx.api.model.beans.base.DataProviderSchemeBean;
import org.sdmxsource.sdmx.api.model.beans.base.MaintainableBean;
import org.sdmxsource.sdmx.api.model.beans.codelist.CodelistBean;
import org.sdmxsource.sdmx.api.model.beans.conceptscheme.ConceptSchemeBean;
import org.sdmxsource.sdmx.api.model.beans.datastructure.DataStructureBean;
import org.sdmxsource.sdmx.api.model.beans.datastructure.DataflowBean;
import org.sdmxsource.sdmx.api.model.beans.registry.ProvisionAgreementBean;
import org.sdmxsource.sdmx.api.model.beans.registry.RegistrationBean;
import org.sdmxsource.sdmx.api.util.ReadableDataLocation;
import org.sdmxsource.sdmx.sdmxbeans.model.SdmxStructureFormat;
import org.sdmxsource.util.io.ReadableDataLocationTmp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

@FusionRegistry
public class FusionRegistryClient implements SDMXRegistryClient {
	
	private class VersionResult {
		public boolean result;
		public String 	beanVersion,
						serverVersion;
		
		public VersionResult (boolean result, String beanVersion, String serverVersion)
		{
			this.result = result;
			this.beanVersion = beanVersion;
			this.serverVersion = serverVersion;
		}
	}
	
	private boolean checkVersion;
	private Logger log = LoggerFactory.getLogger(FusionRegistryClient.class);

	private SDMXRegistryDescriptor registry = null;
	private SDMXRegistryInterfaceType interfaceType = SDMXRegistryInterfaceType.RESTV2_1;

	private StructureWriterManager structureWriterManager;
	private StructureParsingManager structureParsingManager;
	
	private static SDMXSourceProvider sdmxSourceProvider = new SDMXSourceProvider();
	
	public FusionRegistryClient() {
		structureWriterManager = sdmxSourceProvider.getStructureWriterManager();
		structureParsingManager = sdmxSourceProvider.getStructureParsingManager();
	}
	
	public FusionRegistryClient(SDMXRegistryDescriptor registry) {
		this();
		this.registry = registry;
		this.checkVersion = registry.versionAware();
	}

	public void setInterfaceType(SDMXRegistryInterfaceType interfaceType) {
		switch(interfaceType){
		case SOAPV1:
		case SOAPV2:
		case SOAPV2_1:
			throw new IllegalArgumentException("Fusion Registry does only support REST protocols.");
		default:
			this.interfaceType = interfaceType;
		}
	}

	@Override
	public void setRegistry(SDMXRegistryDescriptor descriptor) {
		this.registry = descriptor;		
	}
	
	@Override
	public SDMXRegistryDescriptor getRegistry() {
		return registry;
	}

	@Override
	public SubmissionReport publish(AgencySchemeBean agencyScheme)
			throws SDMXRegistryClientException {

		return publishMaintanableArtefact(agencyScheme);
		
		
	}
	@Override
	public SubmissionReport publish(CodelistBean codelist)
			throws SDMXRegistryClientException {
		return publishMaintanableArtefact(codelist);
	}
	@Override
	public SubmissionReport publish(ConceptSchemeBean conceptscheme)
			throws SDMXRegistryClientException {
		return publishMaintanableArtefact(conceptscheme);
	}
	@Override
	public SubmissionReport publish(DataStructureBean datastructure)
			throws SDMXRegistryClientException {
		return publishMaintanableArtefact(datastructure);
	}
	@Override
	public SubmissionReport publish(DataflowBean dataflow)
			throws SDMXRegistryClientException {
		return publishMaintanableArtefact(dataflow);
	}
	@Override
	public SubmissionReport publish(DataProviderSchemeBean dataproviderscheme)
			throws SDMXRegistryClientException {
		return publishMaintanableArtefact(dataproviderscheme);
	}
	@Override
	public SubmissionReport publish(ProvisionAgreementBean provisionagreement)
			throws SDMXRegistryClientException {
		return publishMaintanableArtefact(provisionagreement);
	}
	
	
	@Override
	public SubmissionReport publish(RegistrationBean subscription)
			throws SDMXRegistryClientException {

		log.debug("Generating SDMX document");
		String xmlDocument = generateSDMXDocument(subscription);
		log.info("Submitting to registry Maintainable Artifact with URN: "
				+ subscription.getUrn());

		InputStream is = POSTQuery(getWebServiceUrl(), getWebServiceCredentials(),xmlDocument);

		String serverResponse;
		try {
			serverResponse = IOUtils.toString(is);
		} catch (IOException e) {
			String errorMsg = "Unable to read server response";
			SDMXRegistryClientException exception= new SDMXRegistryClientException(errorMsg);
			exception.initCause(e);
			throw exception;
		}

		logServerMessage(serverResponse);

		testForErrorMessage(serverResponse);

		XPath xpath = XPathFactory.newInstance().newXPath();

		try {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document document = dBuilder.parse(IOUtils
					.toInputStream(serverResponse));

			SubmissionReport report = new SubmissionReport();

			String id = (String) xpath
					.evaluate(
							"//RegistryInterface/SubmitRegistrationsResponse/RegistrationStatus[1]/Registration/@id",
							document, XPathConstants.STRING);
			report.setId(id);

			String operationStatus = (String) xpath
					.evaluate(
							"//RegistryInterface/SubmitRegistrationsResponse/RegistrationStatus[1]/StatusMessage/@status",
							document, XPathConstants.STRING);
			report.setStatus(OperationStatus.valueOf(operationStatus));

			NodeList nl = (NodeList) xpath
					.evaluate(
							"//RegistryInterface/SubmitRegistrationsResponse/RegistrationStatus[1]/StatusMessage/MessageText/Text",
							document, XPathConstants.NODESET);

			for (int i = 0; i < nl.getLength(); i++) {
				report.addMessage(nl.item(i).getTextContent());
			}

			log.debug("Submission report: " + report);
			return report;
		} catch (Exception e) {
			SDMXRegistryClientException exception= new SDMXRegistryClientException(
					"Unable to parse registry response");
			exception.initCause(e);
			throw exception;
		}
	}

	private VersionResult versionOk (String beanVersion, String agencyId, String id, String typeCode) throws SDMXRegistryClientException
	{
		
		if (this.checkVersion)
		{
			log.debug("Checking version: bean version "+beanVersion);
			
			Version serverVersion = null;
			try
			{
				serverVersion = getVersion(agencyId, id, typeCode);
				
			} catch (SDMXRegistryClientException e)
			{
				log.debug("Unable to get data on the server, setting version to 0");
				serverVersion = new Version(null);
			}
			
			Version beanVersionObject = new Version(beanVersion);

			return new VersionResult ((serverVersion.compareTo(beanVersionObject)<0), beanVersion,serverVersion.getVersion());
			
		}
		else
		{
			log.debug("Version check disabled");
			return new VersionResult(true, null, null);
			
		}
	}
	
	private SubmissionReport publishMaintanableArtefact(MaintainableBean bean)
			throws SDMXRegistryClientException {

		VersionResult versionResult = versionOk(bean.getVersion(), bean.getAgencyId(), bean.getId(), bean.getStructureType().getUrnClass());
		
		if (versionResult.result)
		{
			log.debug("Generating SDMX document");
			String xmlDocument = generateSDMXDocument(bean);
			log.info("Submitting to registry Maintainable Artifact with URN: "
					+ bean.getUrn());

			InputStream is = POSTQuery(getWebServiceUrl(), getWebServiceCredentials(),xmlDocument);

			String serverResponse;
			try {
				serverResponse = IOUtils.toString(is);
			} catch (IOException e) {
				String errorMsg = "Unable to read server response";
				
				SDMXRegistryClientException exception =  new SDMXRegistryClientException(errorMsg);
				exception.initCause(e);
				throw exception;
			}

			logServerMessage(serverResponse);
			
			testForErrorMessage(serverResponse);

			try {
				DocumentBuilderFactory dbFactory = DocumentBuilderFactory
						.newInstance();
				DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
				Document document = dBuilder.parse(IOUtils
						.toInputStream(serverResponse));

				XPath xpath = XPathFactory.newInstance().newXPath();
				SubmissionReport report = new SubmissionReport();

				String id = (String) xpath
						.evaluate(
								"//RegistryInterface/SubmitStructureResponse/SubmissionResult[1]/SubmittedStructure/MaintainableObject/URN/text()",
								document, XPathConstants.STRING);
				report.setId(id);

				String operationStatus = (String) xpath
						.evaluate(
								"//RegistryInterface/SubmitStructureResponse/SubmissionResult[1]/StatusMessage/@status",
								document, XPathConstants.STRING);
				report.setStatus(OperationStatus.valueOf(operationStatus));

				NodeList nl = (NodeList) xpath
						.evaluate(
								"//RegistryInterface/SubmitRegistrationsResponse/RegistrationStatus/StatusMessage/MessageText/Text",
								document, XPathConstants.NODESET);
				for (int i = 0; i < nl.getLength(); i++) {
					report.addMessage(nl.item(i).getNodeValue());
				}

				log.trace("Registration report: " + report);
				return report;
			} catch(Exception e){
				String errorMsg = "Error occurred while parsing registry response";
				SDMXRegistryClientException exception =  new SDMXRegistryClientException(errorMsg);
				exception.initCause(e);
				throw exception;
			} 
		}
		
		else
		{
			log.debug("The version on Fusion Registry is higher");
			String errorMsg = "Invalid version: the version on the server is "+versionResult.serverVersion+" the version to upload is "+versionResult.beanVersion;
			log.error(errorMsg);
			throw new SDMXVersionException(bean.getStructureType().getType(),versionResult.beanVersion,versionResult.serverVersion);

		}
		
		


	}
	@Override
	public SdmxBeans getAgencyScheme(String agencyId, String id,
			String version, Detail details, References references)
			throws SDMXRegistryClientException {
		return getMaintainableArtifacts(agencyId, id, version, details,
				references, "agencyscheme");
	}
	@Override
	public SdmxBeans getCodelist(String agencyId, String id, String version,
			Detail details, References references)
			throws SDMXRegistryClientException {
		return getMaintainableArtifacts(agencyId, id, version, details,
				references, "codelist");
	}
	@Override
	public SdmxBeans getConceptScheme(String agencyId, String id,
			String version, Detail details, References references)
			throws SDMXRegistryClientException {
		return getMaintainableArtifacts(agencyId, id, version, details,
				references, "conceptscheme");
	}
	@Override
	public SdmxBeans getDataStructure(String agencyId, String id,
			String version, Detail details, References references)
			throws SDMXRegistryClientException {
		return getMaintainableArtifacts(agencyId, id, version, details,
				references, "datastructure");
	}
	@Override
	public SdmxBeans getDataFlow(String agencyId, String id, String version,
			Detail details, References references)
			throws SDMXRegistryClientException {
		return getMaintainableArtifacts(agencyId, id, version, details,
				references, "dataflow");
	}
	@Override
	public SdmxBeans getDataProviderScheme(String agencyId, String id,
			String version, Detail details, References references)
			throws SDMXRegistryClientException {
		return getMaintainableArtifacts(agencyId, id, version, details,
				references, "dataprovider");
	}
	@Override
	public SdmxBeans getProvisionAgreement(String agencyId, String id,
			String version, Detail details, References references)
			throws SDMXRegistryClientException {
		return getMaintainableArtifacts(agencyId, id, version, details,
				references, "provisionagreement");
	}
	@Override
	public SdmxBeans getAllDataSetRegistrations()
			throws SDMXRegistryClientException {
		RegistryInterfaceDocument document = RegistryInterfaceDocument.Factory
				.newInstance();
		RegistryInterfaceType registryInterfaceType = document
				.addNewRegistryInterface();
		BaseHeaderType baseHeader = registryInterfaceType.addNewHeader();
		baseHeader.setID("UNKNOWN");
		baseHeader.setTest(false);
		baseHeader.setPrepared(Calendar.getInstance());

		baseHeader.addNewSender().setId("sdmx-publisher");
		baseHeader.addNewReceiver().setId("REGISTRY");
		registryInterfaceType.setHeader(baseHeader);
		QueryRegistrationRequestType queryRegistrationRequestType = registryInterfaceType
				.addNewQueryRegistrationRequest();
		queryRegistrationRequestType.setQueryType(QueryTypeType.DATA_SETS);
		queryRegistrationRequestType.addNewAll();

		String xmlDocument = document.toString();

		log.trace("Generated registration query document:\n" + xmlDocument);

		InputStream is = POSTQuery(getWebServiceUrl(), getWebServiceCredentials(),xmlDocument);

		String serverResponse;
		try {
			serverResponse = IOUtils.toString(is);
		} catch (IOException e) {
			String errorMsg = "Unable to read server response";
			SDMXRegistryClientException exception =  new SDMXRegistryClientException(errorMsg);
			exception.initCause(e);
			throw exception;
		}

		logServerMessage(serverResponse);

		testForErrorMessage(serverResponse);

		return getStructureBeansFromStream(IOUtils
				.toInputStream(serverResponse));
	}

	private SdmxBeans getMaintainableArtifacts(String agencyId, String id,
			String version, Detail details, References references,
			String typeCode) throws SDMXRegistryClientException {

		log.info("Retrieving Maintainable Artifact (" + typeCode
				+ ") with ref: " + agencyId + ", " + id + ", " + version
				+ " from Registry");

		String webService = getWebServiceUrl();

		webService += typeCode + "/" + agencyId + "/" + id + "/" + version
				+ "/?detail=" + details.toString() + "&references="
				+ references.toString();

		InputStream is = GETQuery(webService, getWebServiceCredentials());

		ReadableDataLocation structureLocation = new ReadableDataLocationTmp(is);
		StructureWorkspace workspace = structureParsingManager
				.parseStructures(structureLocation);
		return workspace.getStructureBeans(false);
	}
	
	

	private String getWebServiceUrl() throws SDMXRegistryClientException {
		String webService = null;
		switch (interfaceType) {
		case RESTV1:
		case RESTV2:
		case RESTV2_1:
			webService = registry.getUrl(interfaceType);
			break;
		default:
			throw new SDMXRegistryClientException("Interface "
					+ interfaceType.getName() + " not Implemented");
		}
		log.debug("Using web service URL: " + webService);
		return webService;
	}
	
	private String getWebServiceCredentials () throws SDMXRegistryClientException 
	{
		String base64Credentials = null;
		
		if (registry.getUrl(interfaceType).startsWith("https"))
		{
			switch (interfaceType) {
			case RESTV1:
			case RESTV2:
			case RESTV2_1:
				Credentials credentials = this.registry.getCredentials();
				
				if (credentials != null && credentials.getType().equals(Base64Credentials.CREDENTIAL_TYPE))
									base64Credentials = ((Base64Credentials) credentials).getBase64Encoding();
				break;
			default:
				throw new SDMXRegistryClientException("Interface "
						+ interfaceType.getName() + " not Implemented");
			}
			log.debug("Credentials: " + base64Credentials);
		}
		
		else log.debug("No secure connection: credentials will not be sent");
		

		return base64Credentials;
	
	}

	private String generateSDMXDocument(MaintainableBean bean)
			throws SDMXRegistryClientException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		switch (interfaceType) {
		case RESTV1:
			structureWriterManager.writeStructure(bean, null,new SdmxStructureFormat(
					STRUCTURE_OUTPUT_FORMAT.SDMX_V1_STRUCTURE_DOCUMENT), baos);
			break;
		case RESTV2:
			structureWriterManager.writeStructure(bean, null,new SdmxStructureFormat(
					STRUCTURE_OUTPUT_FORMAT.SDMX_V2_STRUCTURE_DOCUMENT), baos);
			break;
		case RESTV2_1:
			structureWriterManager.writeStructure(bean, null,new SdmxStructureFormat(
					STRUCTURE_OUTPUT_FORMAT.SDMX_V21_STRUCTURE_DOCUMENT), baos);
			break;
		default:
			throw new SDMXRegistryClientException("Interface "
					+ interfaceType.getName() + " not Implemented");
		}

		return baos.toString();
	}

	private InputStream POSTQuery(String queryUrl, String base64Credentials,String queryDocument)
			throws SDMXRegistryClientException {

		log.trace("Submitting SDMX document to Registry URL: " + queryUrl
				+ ", Document: " + queryDocument);

		URL url;
		try {
			url = new URL(queryUrl);
		} catch (MalformedURLException e) {
			String msg = "Malformed query URL";
			SDMXRegistryClientException exception =  new SDMXRegistryClientException(msg);
			exception.initCause(e);
			throw exception;
		}
		URLConnection urlc;
		try {
			urlc = url.openConnection();
		} catch (IOException e) {
			String msg = "Unable to open a connection to the registry";
			SDMXRegistryClientException exception =  new SDMXRegistryClientException(msg);
			exception.initCause(e);
			throw exception;
		}
		

		urlc.setDoOutput(true);
		urlc.setAllowUserInteraction(false);
		urlc.addRequestProperty("Accept", "application/xml;version="
				+ interfaceType.getModelVersion());		
		// urlc.addRequestProperty("Content-Type", "application/text;version="
		// + interfaceType.getModelVersion());
		urlc.addRequestProperty("Content-Type", "application/text");
		
		if (base64Credentials != null) urlc.addRequestProperty("Authorization", "Basic "+base64Credentials);
		
		PrintStream ps;
		try {
			ps = new PrintStream(urlc.getOutputStream());
		} catch (IOException e) {
			String msg = "Unable to send message to the registry";
			SDMXRegistryClientException exception =  new SDMXRegistryClientException(msg);
			exception.initCause(e);
			throw exception;
		}
		ps.print(queryDocument);
		ps.close();
		InputStream is;
		try {
			is = urlc.getInputStream();
		} catch (IOException e) {
			String msg = "Unable to read response from registry";
			SDMXRegistryClientException exception =  new SDMXRegistryClientException(msg);
			exception.initCause(e);
			throw exception;
		}

		return is;
	}

	private InputStream GETQuery(String queryUrl,String base64Credentials)
			throws SDMXRegistryClientException {
		URL url;
		try {
			url = new URL(queryUrl);
		} catch (MalformedURLException e) {
			log.error("Invalid query URL was generated: " + queryUrl);
			throw new SDMXRegistryClientException("Syntax error");
		}
		URLConnection urlc;
		try {
			urlc = url.openConnection();
		} catch (IOException e) {
			log.error("Unable to open a connection to the registry", e);
			throw new SDMXRegistryClientException("Unable to contact registry");
		}
		urlc.setDoOutput(false);
		urlc.setAllowUserInteraction(false);
		urlc.setRequestProperty("Accept", "application/xml;version="
				+ interfaceType.getModelVersion());
		// urlc.addRequestProperty("Content-Type",
		// "application/xml;version="+interfaceType.getModelVersion());
		
		if (base64Credentials != null) urlc.addRequestProperty("Authorization", "Basic "+base64Credentials);
		
		InputStream is;
		String response;
		try {
			log.trace("Performing GET Query with URL: " + urlc.getURL());
			is = urlc.getInputStream();
			response = IOUtils.toString(is);
			log.trace("Registry response:\n" + response);
		} catch (IOException e) {
			log.error("Unable to open a connection to the registry");
			
			SDMXRegistryClientException exception = new SDMXRegistryClientException(
					"Unable to read response message from registry");
			exception.initCause(e);
			
			throw exception;
		}

		return IOUtils.toInputStream(response);
	}

	private SdmxBeans getStructureBeansFromStream(InputStream is) {
		ReadableDataLocation structureLocation = new ReadableDataLocationTmp(is);
		StructureWorkspace workspace = structureParsingManager
				.parseStructures(structureLocation);
		return workspace.getStructureBeans(false);
	}

	private void logServerMessage(String serverResponse) {
		log.trace("Registry response:\n" + serverResponse);
	}

	private void testForErrorMessage(String serverResponse)
			throws SDMXRegistryClientException {
		log.debug("Server Response:");
		log.debug(serverResponse);

		
		try {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document document = dBuilder.parse(IOUtils.toInputStream(serverResponse));

			XPath xpath = XPathFactory.newInstance().newXPath();

			Node errorNode = (Node) xpath.evaluate("/Error", document,
					XPathConstants.NODE);

			if (errorNode == null)
				return;

			Double code = (Double) xpath.evaluate("/Error/ErrorMessage/@code",
					document, XPathConstants.NUMBER);
			log.trace("Error message code: " + String.valueOf(code.intValue()));
			String errorMsg = (String) xpath.evaluate(
					"/Error/ErrorMessage/Text/text()", document,
					XPathConstants.STRING);
			log.trace("Error message text: " + errorMsg);

			throw RegistryClientExceptionFactory.getException(errorMsg,
					code.intValue());
		} catch (SDMXRegistryClientException e) {
			log.info("Caught error message from registry: " + e);
			throw e;
		} catch (Exception e) {
			String msg = "Exception caught while checking for error messages in server response";
			SDMXRegistryClientException exception =  new SDMXRegistryClientException(msg);
			exception.initCause(e);
			throw exception;
		}

	}
	
	
	private Version getVersion (String agencyId, String id, String typeCode) throws SDMXRegistryClientException
	{
		log.debug("Checking if the a newer version exists on the registry");
		SdmxBeans beans = getMaintainableArtifacts(agencyId, id, "", Detail.referencestubs, References.none, typeCode);

		Version response = new Version (null);
		Set<MaintainableBean> responseBeans = beans.getAllMaintainables();

		for (MaintainableBean responseBean : responseBeans)
		{
			Version version = new Version(responseBean.getVersion());
			
			if (version.compareTo(response)>0) response = version;
			
			
		}
		
		log.debug("Registry version "+response);
		return response;
		
	}
	

	
//	public static void main(String[] args) throws Exception{
//		ProxyAuthenticator authenticator = new ProxyAuthenticator();
//		authenticator.setProxyHost("proxy.eng.it");
//		authenticator.setProxyPort("3128");
//		authenticator.setProxyUserName("cirformi");
//		authenticator.setProxyPassword("sys64738");
//		authenticator.configure();
//		if (authenticator.isActive()) Authenticator.setDefault(authenticator);
//		
//		SDMXRegistryDescriptorImpl descriptor = new SDMXRegistryDescriptorImpl();
//		descriptor.setUrl(SDMXRegistryInterfaceType.RESTV2_1, "http://node8.d.d4science.research-infrastructures.eu:8080/FusionRegistry/ws/rest/");
//		//SDMXRegistryClient registryClient = new FusionRegistryClient(descriptor);
//		SDMXRegistryClient registryClient = new FusionRegistryClient(descriptor);
//		SdmxBeans beans = registryClient.getConceptScheme("ENG", "NEW_DS_DIVISION_concepts", "", Detail.referencestubs, References.none);
//		
//		
//		Set<ConceptSchemeBean> cls = beans.getConceptSchemes();
//		System.out.println(cls.size());
//		Set<IdentifiableBean> concepts= cls.iterator().next().getIdentifiableComposites(); 
//		
//		for (IdentifiableBean concept : concepts)
//		{
//			System.out.println(concept.getId());
//			System.out.println(concept.getAllTextTypes().get(0).getValue());
//			Set<CrossReferenceBean> crossReferences = concept.getCrossReferences();
//			
//			for (CrossReferenceBean crossReference : crossReferences)
//			{
//				System.out.println(crossReference.getMaintainableStructureType());
//				
//				System.out.println(crossReference.getMaintainableId());
//
//			}
//			
//	
//		}
//		
//		
//	}


}

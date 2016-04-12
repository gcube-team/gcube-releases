package org.gcube.spatial.data.geonetwork.iso;

import static org.gcube.resources.discovery.icclient.ICFactory.clientFor;
import static org.gcube.resources.discovery.icclient.ICFactory.queryFor;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;


import org.gcube.common.resources.gcore.GenericResource;
import org.gcube.informationsystem.publisher.RegistryPublisher;
import org.gcube.informationsystem.publisher.RegistryPublisherFactory;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.api.SimpleQuery;
import org.gcube.spatial.data.geonetwork.utils.RuntimeParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("EnvironmentConfiguration")
public class EnvironmentConfiguration implements Cloneable{

	private static final Logger logger = LoggerFactory.getLogger(EnvironmentConfiguration.class);

	private static EnvironmentConfiguration current=null;
	private static long lastUpdate=0;

	private static TransformerFactory tf = TransformerFactory.newInstance();
	private static Transformer transformer;
	private static XStream xstream=new XStream();

	private static Properties props;
	
	static {
		try{
			transformer = tf.newTransformer();
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			transformer.setOutputProperty(OutputKeys.INDENT, "no");
		}catch(Exception e){
			logger.error("Unable to initialize transformer ",e);
		}
		try{
			props=new RuntimeParameters().getProps();
		}catch(IOException e){
			logger.error("Unable to read properties file ",e);
		}
		xstream.processAnnotations(EnvironmentConfiguration.class);
		xstream.processAnnotations(Thesaurus.class);
	}



	public static synchronized EnvironmentConfiguration getConfiguration() throws Exception{
		if(System.currentTimeMillis()-lastUpdate>Long.parseLong(props.getProperty("metadataConfiguration.ttl")))
			current=getFromIS();
		return current;		
	}

	private static EnvironmentConfiguration getFromIS() throws Exception{
		SimpleQuery query = queryFor(GenericResource.class);
		query.addCondition("$resource/Profile/SecondaryType/text() eq '"+props.getProperty("genericResource.SecondaryType")+"'").
		addCondition("$resource/Profile/Name/text() eq '"+props.getProperty("genericResource.Name")+"'");
		DiscoveryClient<GenericResource> client = clientFor(GenericResource.class);

		for(GenericResource resource : client.submit(query)){
			try{
				
				// Refactor logic to integrate 
				
//				// parse body as a XML serialization of a Computational Infrastructure
//				StringWriter writer = new StringWriter();
//				transformer.transform(new DOMSource(resource.profile().body()), new StreamResult(writer));
//				String theXML=writer.getBuffer().toString();
//				//				String theXML = writer.getBuffer().toString().replaceAll("\n|\r", "");
				EnvironmentConfiguration config=(EnvironmentConfiguration) xstream.fromXML(resource.profile().bodyAsString());
				return config;
			}catch(Exception e){
				logger.warn("Unable to parse resource [ID :"+resource.id()+"]",e);
			}
		}
		throw new Exception("No Configuration Found");
	}


	private static GenericResource publishResource(EnvironmentConfiguration config) throws SAXException, IOException, ParserConfigurationException{
		Element toAppend=DocumentBuilderFactory
				.newInstance()
				.newDocumentBuilder()
				.parse(new ByteArrayInputStream(config.toXML().getBytes()))
				.getDocumentElement();
		GenericResource toPublish=new GenericResource();
		Document document= toPublish.newProfile().description("Common configuration for ISO Metadata publishing")
				.name(props.getProperty("genericResource.Name"))
				.type(props.getProperty("genericResource.SecondaryType")).newBody().getOwnerDocument();

		toPublish.profile().body().appendChild(document.importNode(toAppend, true));
		RegistryPublisher rp=RegistryPublisherFactory.create();
		toPublish=rp.create(toPublish);
		return toPublish;
	}


	//**************Instance values

	private String wmsProtocolDeclaration;
	private String wfsProtocolDeclaration;
	private String wcsProtocolDeclaration;
	private String httpProtocolDeclaration;
	private String defaultCRS;
	//*** Project
	private String projectName;
	private String projectCitation;

	//*** Common ResponsibleParties
	private String distributorIndividualName;
	private String distributorOrganisationName;
	private String distributorEMail;
	private String distributorSite;

	private String providerIndividualName;
	private String providerOrganisationName;
	private String providerEMail;
	private String providerSite;

	private String license;


	//**** Thesauri

	private HashMap<String,Thesaurus> thesauri=new HashMap<String, Thesaurus>();


	public EnvironmentConfiguration() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @return the wmsProtocolDeclaration
	 */
	public String getWmsProtocolDeclaration() {
		return wmsProtocolDeclaration;
	}

	/**
	 * @param wmsProtocolDeclaration the wmsProtocolDeclaration to set
	 */
	public void setWmsProtocolDeclaration(String wmsProtocolDeclaration) {
		this.wmsProtocolDeclaration = wmsProtocolDeclaration;
	}

	/**
	 * @return the wfsProtocolDeclaration
	 */
	public String getWfsProtocolDeclaration() {
		return wfsProtocolDeclaration;
	}

	/**
	 * @param wfsProtocolDeclaration the wfsProtocolDeclaration to set
	 */
	public void setWfsProtocolDeclaration(String wfsProtocolDeclaration) {
		this.wfsProtocolDeclaration = wfsProtocolDeclaration;
	}

	/**
	 * @return the wcsProtocolDeclaration
	 */
	public String getWcsProtocolDeclaration() {
		return wcsProtocolDeclaration;
	}

	/**
	 * @param wcsProtocolDeclaration the wcsProtocolDeclaration to set
	 */
	public void setWcsProtocolDeclaration(String wcsProtocolDeclaration) {
		this.wcsProtocolDeclaration = wcsProtocolDeclaration;
	}

	/**
	 * @return the httpProtocolDeclaration
	 */
	public String getHttpProtocolDeclaration() {
		return httpProtocolDeclaration;
	}

	/**
	 * @param httpProtocolDeclaration the httpProtocolDeclaration to set
	 */
	public void setHttpProtocolDeclaration(String httpProtocolDeclaration) {
		this.httpProtocolDeclaration = httpProtocolDeclaration;
	}

	/**
	 * @return the defaultCRS
	 */
	public String getDefaultCRS() {
		return defaultCRS;
	}

	/**
	 * @param defaultCRS the defaultCRS to set
	 */
	public void setDefaultCRS(String defaultCRS) {
		this.defaultCRS = defaultCRS;
	}

	/**
	 * @return the projectName
	 */
	public String getProjectName() {
		return projectName;
	}

	/**
	 * @param projectName the projectName to set
	 */
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	/**
	 * @return the projectCitation
	 */
	public String getProjectCitation() {
		return projectCitation;
	}

	/**
	 * @param projectCitation the projectCitation to set
	 */
	public void setProjectCitation(String projectCitation) {
		this.projectCitation = projectCitation;
	}

	/**
	 * @return the distributorIndividualName
	 */
	public String getDistributorIndividualName() {
		return distributorIndividualName;
	}

	/**
	 * @param distributorIndividualName the distributorIndividualName to set
	 */
	public void setDistributorIndividualName(String distributorIndividualName) {
		this.distributorIndividualName = distributorIndividualName;
	}

	/**
	 * @return the distributorOrganisationName
	 */
	public String getDistributorOrganisationName() {
		return distributorOrganisationName;
	}

	/**
	 * @param distributorOrganisationName the distributorOrganisationName to set
	 */
	public void setDistributorOrganisationName(String distributorOrganisationName) {
		this.distributorOrganisationName = distributorOrganisationName;
	}

	/**
	 * @return the distributorEMail
	 */
	public String getDistributorEMail() {
		return distributorEMail;
	}

	/**
	 * @param distributorEMail the distributorEMail to set
	 */
	public void setDistributorEMail(String distributorEMail) {
		this.distributorEMail = distributorEMail;
	}

	/**
	 * @return the distributorSite
	 */
	public String getDistributorSite() {
		return distributorSite;
	}

	/**
	 * @param distributorSite the distributorSite to set
	 */
	public void setDistributorSite(String distributorSite) {
		this.distributorSite = distributorSite;
	}

	/**
	 * @return the providerIndividualName
	 */
	public String getProviderIndividualName() {
		return providerIndividualName;
	}

	/**
	 * @param providerIndividualName the providerIndividualName to set
	 */
	public void setProviderIndividualName(String providerIndividualName) {
		this.providerIndividualName = providerIndividualName;
	}

	/**
	 * @return the providerOrganisationName
	 */
	public String getProviderOrganisationName() {
		return providerOrganisationName;
	}

	/**
	 * @param providerOrganisationName the providerOrganisationName to set
	 */
	public void setProviderOrganisationName(String providerOrganisationName) {
		this.providerOrganisationName = providerOrganisationName;
	}

	/**
	 * @return the providerEMail
	 */
	public String getProviderEMail() {
		return providerEMail;
	}

	/**
	 * @param providerEMail the providerEMail to set
	 */
	public void setProviderEMail(String providerEMail) {
		this.providerEMail = providerEMail;
	}

	/**
	 * @return the providerSite
	 */
	public String getProviderSite() {
		return providerSite;
	}

	/**
	 * @param providerSite the providerSite to set
	 */
	public void setProviderSite(String providerSite) {
		this.providerSite = providerSite;
	}

	/**
	 * @return the license
	 */
	public String getLicense() {
		return license;
	}

	/**
	 * @param license the license to set
	 */
	public void setLicense(String license) {
		this.license = license;
	}

	/**
	 * @return the thesauri
	 */
	public HashMap<String, Thesaurus> getThesauri() {
		return thesauri;
	}

	/**
	 * @param thesauri the thesauri to set
	 */
	public void setThesauri(HashMap<String, Thesaurus> thesauri) {
		this.thesauri = thesauri;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("EnvironmentConfiguration [wmsProtocolDeclaration=");
		builder.append(wmsProtocolDeclaration);
		builder.append(", wfsProtocolDeclaration=");
		builder.append(wfsProtocolDeclaration);
		builder.append(", wcsProtocolDeclaration=");
		builder.append(wcsProtocolDeclaration);
		builder.append(", httpProtocolDeclaration=");
		builder.append(httpProtocolDeclaration);
		builder.append(", defaultCRS=");
		builder.append(defaultCRS);
		builder.append(", projectName=");
		builder.append(projectName);
		builder.append(", projectCitation=");
		builder.append(projectCitation);
		builder.append(", distributorIndividualName=");
		builder.append(distributorIndividualName);
		builder.append(", distributorOrganisationName=");
		builder.append(distributorOrganisationName);
		builder.append(", distributorEMail=");
		builder.append(distributorEMail);
		builder.append(", distributorSite=");
		builder.append(distributorSite);
		builder.append(", providerIndividualName=");
		builder.append(providerIndividualName);
		builder.append(", providerOrganisationName=");
		builder.append(providerOrganisationName);
		builder.append(", providerEMail=");
		builder.append(providerEMail);
		builder.append(", providerSite=");
		builder.append(providerSite);
		builder.append(", license=");
		builder.append(license);
		builder.append(", thesauri=");
		builder.append(thesauri);
		builder.append("]");
		return builder.toString();
	}

	public String toXML(){
		return xstream.toXML(this);
	}

	/**
	 * Publish @this as a gCube Generic Resource in the current scope
	 * 
	 * @return the published Generic Resource
	 * @throws ParserConfigurationException 
	 * @throws IOException 
	 * @throws SAXException 
	 */
	public GenericResource publish() throws SAXException, IOException, ParserConfigurationException{
		return publishResource(this);
	}
}

package org.gcube.common.core.security.utils;

import java.io.File;
import java.io.StringReader;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.gcube.common.core.security.GCUBEDefaultSecurityConfiguration;
import org.gcube.common.core.utils.logging.GCUBELog;
import org.globus.wsrf.impl.security.descriptor.ServiceSecurityDescriptor;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.InputSource;

/**
 * 
 * 
 * Builds default incoming and outgoing security descriptor on the basis of the default security configuration  
 * 
 * @author Ciro Formisano
 *
 */

public class DefaultSecurityDescriptorBuilder 
{
	private GCUBELog logger;

	/*
	 * 
	 * Security descriptor elements
	 * 
	 */

	
	private final String SERVICE_SEC_DESC_MODEL = "<securityConfig xmlns=\"%NAMESPACE%\">" +
													"<auth-method>" +
													"%AUTH_METHOD%" +
													"<protection-level>%PROT_LEVELS%</protection-level>" +
													"%_AUTH_METHOD%" +
													"</auth-method>" +
													"</securityConfig>";

	private final String 	SD_NAMESPACE = "http://www.globus.org";

	/*
	 * Default services security elements
	 */
	
	private final String 	//DS_CONFIGURATION_ROOT = "services_security",
							DS_IN_ELEMENT = "in",
							DS_OUT_ELEMENT = "out",
							DS_AUTH_METHOD_ELEMENT = "auth_method",
							DS_PROTECTION_LEVEL_ELEMENT = "protection_level",
							//DS_DELEGATION_ELEMENT = "delegation",
							DS_ENABLED_ATTRIBUTE = "enabled",
							DS_OVERRIDE_ATTRIBUTE = "override",
							DS_CRED_PROP_ELEMENT = "propagateCallerCredentials",
							DS_CRED_VALUE_ATTRIBUTE = "value";
							//DS_DEFAULT_NS = "http://www.d4science.eu";
							
	
							
	/**
	 *  Service security descriptors
	 */			
	private ServiceSecurityDescriptor 	incomingSecurityDescriptor,
										outgoingSecurityDescriptor;
	
	/**
	 * default security configurations
	 */
	private DefaultSecurityConfigurationBean 	defaultIncomingConfiguration,
												defaultOutgoinfConfiguration;
	/**
	 * credential propagation default policy
	 */
	private CredentialPropagationStatus credentialPropagationStatus;
	
	/**
	 * 
	 * @param defaultSecConfPath path of the default security configuration file
	 * @throws Exception
	 */
	public DefaultSecurityDescriptorBuilder (String defaultSecConfPath) throws Exception
	{
		logger = new GCUBELog(this);
		
		if (defaultSecConfPath == null) throw new Exception("Null default path");
		else
		{
			Document defaultServiceSecConfDocument = loadDefaultSecConfigurationDocument(defaultSecConfPath);
			Element rootElement = defaultServiceSecConfDocument.getDocumentElement();
			this.logger.debug("loading default ingoing security configuration");
			this.defaultIncomingConfiguration = generateDefaultSecurityConfiguration(rootElement,DS_IN_ELEMENT);
			this.logger.debug("default ingoing security configuration loaded");
			this.logger.debug("loading default outgoing security configuration");
			this.defaultOutgoinfConfiguration = generateDefaultSecurityConfiguration(rootElement,DS_OUT_ELEMENT);
			this.logger.debug("default outgoing security configuration loaded");
			logger.debug("Trying to build default ingoing service security descriptor...");
			this.incomingSecurityDescriptor = buildServiceSecurityDescriptor(this.defaultIncomingConfiguration);
			logger.debug("Default ingoing service security descriptor generated");
			logger.debug("Trying to build default outgoing service security descriptor...");
			this.outgoingSecurityDescriptor = buildServiceSecurityDescriptor(this.defaultOutgoinfConfiguration);
			logger.debug("Default outgoing service security descriptor generated");
			logger.debug("Trying to get the credential propagation configuration");
			this.credentialPropagationStatus = buildCredentialPropagationConfiguration (rootElement);
			logger.debug("Credential propagation configuration gotten");
		}
	}
	
	
	/**
	 * 
	 * @param defaultConfiguration the default security configuraion
	 * @return a secvice security descriptor
	 * @throws Exception
	 */
	private  ServiceSecurityDescriptor buildServiceSecurityDescriptor (DefaultSecurityConfigurationBean defaultConfiguration) throws Exception
	{
		logger.debug("Trying to build default service security descriptor...");
		ServiceSecurityDescriptor response = null;
		
		if (defaultConfiguration.isEnabled())
		{
			logger.debug("Default ingoing service security descriptor is enabled");
			response = generateServiceSecurityDescriptor(defaultConfiguration);
		}
		else
		{
			logger.debug("Default service security configuration disabled");
		}		
		return response;
	}
	


	/**
	 * 
	 * @param defaultConfiguration the default security configuration
	 * @return a secvice security descriptor
	 * @throws Exception
	 */
	private ServiceSecurityDescriptor generateServiceSecurityDescriptor (DefaultSecurityConfigurationBean defaultConfiguration) throws Exception
	{
		logger.debug("Generating service security descriptor...");
		ServiceSecurityDescriptor response = null;
		String authMethod = defaultConfiguration.getIn_auth_method();
		Set<String> protectionLevels = defaultConfiguration.getIn_protection_levels();
		String secDescDocument = generateServiceSecurityDescriptorDom(authMethod, protectionLevels);
		logger.debug(secDescDocument);
		Element input = stringToElement(secDescDocument); 
		logger.debug("Element generated");
		response = new ServiceSecurityDescriptor();
		response.parse (input);
		logger.debug("Sec descriptor generated");
		return response;
	}
	
	
	/**
	 * 
	 * @param rootElement
	 * @return
	 */
	private CredentialPropagationStatus buildCredentialPropagationConfiguration (Element rootElement)
	{
		logger.debug("Getting credential propagation configuration");
		CredentialPropagationStatus response = new CredentialPropagationStatus();
		NodeList credPropNodes = rootElement.getElementsByTagName(DS_CRED_PROP_ELEMENT);
		
		if (credPropNodes != null && credPropNodes.getLength() >0)
		{
			logger.debug("Credential propagation configuration found");
			Element credPropElement = (Element) credPropNodes.item(0);
			String value = credPropElement.getAttribute(DS_CRED_VALUE_ATTRIBUTE);
			
			if (value != null)
			{
				logger.debug("Credential propagation value = "+value);
				
				if (value.equalsIgnoreCase("false"))
				{
					response.setPropagate(false);
					response.setOverride(getCredentialPropagationOverrideAttribute(credPropElement));
				}
				else if (value.equalsIgnoreCase("true"))
				{
					response.setPropagate(true);
					response.setOverride(getCredentialPropagationOverrideAttribute(credPropElement));
				}
				else logger.warn("Unable to find a correct credential propagation value attribute: default credential propagation behaviour disabled");
			}
			else
			{
				logger.warn("Unable to find a correct credential propagation value attribute: default credential propagation behaviour disabled");
			}
			
		}
		
		return response;
		
	}
	
	/**
	 * 
	 * @param credPropElement
	 * @return
	 */
	private boolean getCredentialPropagationOverrideAttribute (Element credPropElement)
	{
		logger.debug("getting cred propagation override attribute");
		String override = credPropElement.getAttribute(DS_OVERRIDE_ATTRIBUTE);
		logger.debug("override value = "+override);
		
		if (override == null)
		{
			logger.debug("Override value not set, returning false");
			return false;
		}
		else if (override.equalsIgnoreCase("true"))
		{
			logger.debug("Override value true");
			return true;
		}
		else if (override.equalsIgnoreCase("false"))
		{
			logger.debug("Override value false");
			return false;
		}
		else
		{
			logger.warn("Invalid override value "+override + " returning the default value false");
			return false;
		}
		
	}
	
	
	/**
	 * 
	 * provides the incoming service security descriptor
	 * 
	 * @return the incoming service security descriptor
	 */
	public ServiceSecurityDescriptor getIncomingMessagesSecurityDescriptor() 
	{
		return incomingSecurityDescriptor;
	}

	/**
	 * 
	 * provides the outgoing service security descriptor
	 * 
	 * @return the outgoing service security descriptor
	 */
	public ServiceSecurityDescriptor getOutgoingMessagesSecurityDescriptor() 
	{
		return outgoingSecurityDescriptor;
	}
	

	

	
	public GCUBEDefaultSecurityConfiguration getGCUBEDefaultSecurityConfiguration ()
	{
		GCUBEDefaultSecurityConfiguration response  = new GCUBEDefaultSecurityConfiguration();
		response.setDefaultCredentialPropagationSet(this.credentialPropagationStatus.isSet());
		response.setPropagateCallerCredentials(this.credentialPropagationStatus.isPropagate());
		response.setPropagateCallerCredentialsOverride(this.credentialPropagationStatus.isOverride());
		response.setInEnabled(this.defaultIncomingConfiguration.isEnabled());
		response.setOutEnabled(this.defaultOutgoinfConfiguration.isEnabled());
		response.setInOverride(defaultIncomingConfiguration.isOverride());
		response.setOutOverride(this.defaultOutgoinfConfiguration.isOverride());
		
		
		return response;
	}
	
	/**
	 * 
	 * @param path the document path
	 * @return the document
	 * @throws Exception
	 */
	private Document loadDefaultSecConfigurationDocument (String path) throws Exception
	{
		logger.debug("Building dom...");
		DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		Document document = builder.parse(new File(path));
		logger.debug("document loaded");
		return document;
	}
	
	
	/**
	 * 
	 * Generates the document
	 * 
	 * @param root
	 * @param baseElementTagName
	 * @return
	 * @throws Exception
	 */
	private DefaultSecurityConfigurationBean generateDefaultSecurityConfiguration (Element root, String baseElementTagName) throws Exception
	{
		logger.debug("Parsing default sec configuration...");
		DefaultSecurityConfigurationBean defaultConfiguration = new DefaultSecurityConfigurationBean();
		logger.debug("Default Security document root "+root.getNamespaceURI()+" "+root.getNodeName());
		NodeList baseElements = root.getElementsByTagName(baseElementTagName);
		
		if (baseElements != null && baseElements.getLength() !=0)
		{
			logger.debug("security configuration");
			Element baseElement = (Element) baseElements.item(0);
			String enabledString = baseElement.getAttribute(DS_ENABLED_ATTRIBUTE);
			if (enabledString == null || enabledString.equalsIgnoreCase("true"))
			{
				defaultConfiguration.setEnabled(true);
				String override = baseElement.getAttribute(DS_OVERRIDE_ATTRIBUTE);
				logger.debug("override = "+override);
				defaultConfiguration.setOverride(override);
				logger.debug("loading auth method element...");
				String authMethod = getText(DS_AUTH_METHOD_ELEMENT, baseElement,true);
				defaultConfiguration.setAuth_method(authMethod);
				logger.debug("auth method element loaded");
				logger.debug("loading protection level element...");
				String protectionLevelList =  getText(DS_PROTECTION_LEVEL_ELEMENT, baseElement,true);
				String [] protectionLevel = protectionLevelList.split(",");
				for (String p : protectionLevel) defaultConfiguration.addProtection_level(p);
				logger.debug("protetion level loaded");
			}
			else
			{
				logger.debug("default security configuration disabled");
			}
		}
		
		return defaultConfiguration;
	}
	
	/**
	 * 
	 * @param element
	 * @param required
	 * @return
	 * @throws Exception
	 */
	private String findInternalText (Element element, boolean required) throws Exception
	{
		
		logger.debug("Adding text");
		String data = null;
		Text text = (Text) element.getFirstChild();
		
		if (text != null)
		{
			data = text.getData();
			logger.debug("data "+data);
			return data;
		}
		else if (required)
		{
			logger.error("No text found");
			throw new Exception("No text found");
		}
		else
		{
			logger.debug("Text element not found");
		}
		return data;
	}
	
	/**
	 * 
	 * @param elementName
	 * @param parent
	 * @param required
	 * @return
	 * @throws Exception
	 */
	private String getText (String elementName,Element parent, boolean required) throws Exception
	{
		logger.debug("Founding text element "+elementName);
		String data = null;
		NodeList elementList = parent.getElementsByTagName(elementName);
		if (elementList != null && elementList.getLength() > 0)
		{
			Element element = (Element) elementList.item(0);
			data = findInternalText(element, required);	

		}
		else if (required)
		{
			logger.error("Root Element not found");
			throw new Exception("Invalid auth configuration: required element not found");
		}
		else
		{
			logger.debug("Delegation element not found");
		}


	
		return data;

	}
	
	/**
	 * 
	 * @param authMethod
	 * @param protectionLevels
	 * @return
	 * @throws ParserConfigurationException
	 */
	private String generateServiceSecurityDescriptorDom (String authMethod, Set<String> protectionLevels) throws ParserConfigurationException
	{
		logger.debug("Building dom...");
		String builder = new String(SERVICE_SEC_DESC_MODEL);
		builder = builder.replace("%NAMESPACE%", SD_NAMESPACE);
		String authMethodElement = "<"+authMethod+">";
		String authCloseMethodElement = "</"+authMethod+">";
		builder = builder.replace("%AUTH_METHOD%", authMethodElement);
		builder = builder.replace("%_AUTH_METHOD%", authCloseMethodElement);
		StringBuilder protLevel = new StringBuilder ();
		
		for (String protectionLevel :protectionLevels)
		{
			protLevel.append("<").append(protectionLevel).append("/>");
		}
		
		builder = builder.replace("%PROT_LEVELS%", protLevel.toString());
		return builder;
				
	}
	
	/**
	 * 
	 * @param nodeAsString
	 * @return
	 * @throws Exception
	 */
	private  Element stringToElement(String nodeAsString) throws Exception 
	{
		Document xml = string2Document(nodeAsString);
		Element element = xml.getDocumentElement();
		return element;

	}
	
	/**
	 * 
	 * @param xmlString
	 * @return
	 */
	private  Document string2Document(String xmlString) 
	{
		Document doc = null;
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			factory.setNamespaceAware(true);
			DocumentBuilder builder = factory.newDocumentBuilder();

			doc = builder.parse(new InputSource(new StringReader(xmlString)));

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return doc;
	}

}

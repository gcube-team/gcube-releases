package org.gcube.security.soa3.connector.integration.utils;

import java.io.File;
import java.io.FileReader;
import java.util.Properties;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPException;

import org.apache.axis.message.SOAPHeaderElement;
import org.gcube.common.core.contexts.GCUBEServiceContext;
import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.security.soa3.configuration.ConfigurationManager;
import org.gcube.security.soa3.configuration.ConfigurationManagerFactory;
import org.gcube.security.soa3.configuration.GSSConfigurationManager;

public class GSSIntegrationUtils 
{
	public static final String SERVICE_PROPERTIES = "serviceSecurityProperties.props";

	
	public static void setServiceProperties (GCUBEServiceContext ctxt,String serviceName)
	{
		
		GCUBELog log = new GCUBELog(Utils.class);
		
		Properties properties = null;
		log.debug("Service name "+serviceName);

		try
		{
			File servicePropertiesFile = ctxt.getFile(SERVICE_PROPERTIES, false);
			log.debug("Service Property file = "+servicePropertiesFile);
			
			if (servicePropertiesFile != null && servicePropertiesFile.exists())
			{
				properties = new Properties();
				properties.load(new FileReader(servicePropertiesFile));
			
			}
			
		} catch (Exception e)
		{
			log.debug("Unable to load local property file");
		}
		
		ConfigurationManager cm = ConfigurationManagerFactory.getConfigurationManager();;
		
		if (properties != null && cm instanceof GSSConfigurationManager) ((GSSConfigurationManager) cm).setServiceProperties(serviceName, properties);
		
	}
	

	
	public static SOAPHeaderElement generateSoapHeaderBinaryTokenElement (String type,String binaryTokenValue) throws SOAPException
	{
		GCUBELog log = new GCUBELog(Utils.class);
		log.debug("Generating token SOAP element");
		log.debug("Type = "+type);
		log.debug("Value = "+binaryTokenValue);
		SOAPHeaderElement headerElement = new SOAPHeaderElement(new QName(Utils.WSSE_NAMESPACE,Utils.BINARY_SECURITY_TOKEN_PREFIX+":"+Utils.BINARY_SECURITY_TOKEN_LABEL));
		headerElement.addAttribute (Utils.WSSE_NAMESPACE,Utils.VALUE_TYPE_LABEL,type);
		headerElement.addAttribute (Utils.WSSE_NAMESPACE,Utils.ENCODING_TYPE_LABEL,Utils.BASE64);
		headerElement.addAttribute (Utils.WSSE_NAMESPACE,Utils.ID_LABEL,Utils.SECURITY_TOKEN_ATTR);
		headerElement.setValue(binaryTokenValue);
		log.debug("Header completed");
		return headerElement;

	}
	

}

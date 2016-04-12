package org.gcube.security.soa3.connector.integration.utils;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.gcube.soa3.connector.common.security.MessageConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class Utils 
{
	//public static final String SERVICE_PROPERTIES = "serviceSecurityProperties.props";
	public static final String SECURITY_TOKEN = "SECURITY_TOKEN";
	public static final String 	BINARY_SECURITY_TOKEN_LABEL = "BinarySecurityToken",
								BINARY_SECURITY_TOKEN_PREFIX = "wsse",
								WSSE_NAMESPACE = "http://schemas.xmlsoap.org/ws/2002/04/secext",
								VALUE_TYPE_LABEL = "ValueType",
								ENCODING_TYPE_LABEL = "EncodingType",
								BASE64 = "wsse:Base64Binary",
								ID_LABEL = "Id",
								SECURITY_TOKEN_ATTR = "SecurityToken",
								SOA3_ACCESS_SERVICE = "access";
	

	
	public static Element generateBinaryTokenElement (String type,String binaryTokenValue) throws Exception
	{
		Logger log = LoggerFactory.getLogger(Utils.class);
		log.debug("Generating token SOAP element");
		log.debug("Type = "+type);
		log.debug("Value = "+binaryTokenValue);
		DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		Document tokenDocument = builder.newDocument();
		Element tokenElement = tokenDocument.createElementNS(MessageConstants.WSSE_NAMESPACE, MessageConstants.BINARY_SECURITY_TOKEN_PREFIX+":"+MessageConstants.BINARY_SECURITY_TOKEN_LABEL);
		tokenElement.setAttribute(MessageConstants.VALUE_TYPE_LABEL, type);
		tokenElement.setAttribute(MessageConstants.ENCODING_TYPE_LABEL,MessageConstants.BASE64 );
		tokenElement.setAttribute(MessageConstants.ID_LABEL, SECURITY_TOKEN);
		tokenElement.setTextContent(binaryTokenValue);
		log.debug("Header completed");
		return tokenElement;

	}
	

	

}

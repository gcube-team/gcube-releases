package it.eng.rdlab.soa3.pm.connector.impl;

import it.eng.rdlab.soa3.connector.utils.SoapUtils;
import it.eng.rdlab.soa3.connector.utils.XMLUtils;
import it.eng.rdlab.soa3.pm.connector.interfaces.PolicyManagerConstants;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.soap.SOAPMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * 
 * Utilities for the Policy Getter
 * 
 * @author Ciro Formisano (ENG)
 *
 */
class PolicyGetUtils 
{
	private static Logger logger = LoggerFactory.getLogger(PolicyGetUtils.class);
	protected String url;
	
	
	/**
	 * 
	 * @param alias
	 * @param elementId
	 * @param tagRequest
	 * @param tagElementId
	 * @return
	 * @throws Exception
	 */
	private static SOAPMessage generateSoapMessage (String alias,String elementId,String tagRequest,String tagElementId) throws Exception
	{
		DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		Document requestDocument = builder.newDocument();

		Element root = requestDocument.createElementNS(PolicyManagerConstants.PAP_SERVICE_NAMESPACE, "ser:"+tagRequest);
		if (alias != null)
		{
			logger.debug("Creating alias element...");
			root.appendChild(XMLUtils.createElement(requestDocument, PolicyManagerConstants.PAP_ALIAS_TAG, alias,null));
			logger.debug("Alias element created");
		}
		if (elementId != null) root.appendChild(XMLUtils.createElement(requestDocument, tagElementId, elementId,null));
		requestDocument.appendChild(root);
		
		return SoapUtils.generateSoapMessage(requestDocument);
	}
	
	/**
	 * 
	 * @param alias
	 * @param elementId
	 * @param tagRequest
	 * @param tagElementId
	 * @return
	 * @throws Exception
	 */
	public static SOAPMessage identifyElement (String url,String alias, String elementId, String tagRequest, String tagElementId) throws Exception
	{
		logger.debug("Generating request soap message");
		SOAPMessage message = generateSoapMessage (alias,elementId,tagRequest,tagElementId);
		logger.debug("Soap Message generated");
		
		try
		{
			logger.debug("Sending message");
			SOAPMessage response = SoapUtils.performCall(message,url);
			logger.debug("Message sent");
			return response;
		} catch (Exception e)
		{
			logger.error("Error in sending the message",e);
			return null;
		}


	}
	



}

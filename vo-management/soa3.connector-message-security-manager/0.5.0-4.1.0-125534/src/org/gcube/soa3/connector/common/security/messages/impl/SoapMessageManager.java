package org.gcube.soa3.connector.common.security.messages.impl;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFactory;
import javax.xml.soap.SOAPMessage;

import org.gcube.soa3.connector.common.security.Credentials;
import org.gcube.soa3.connector.common.security.MessageConstants;
import org.gcube.soa3.connector.common.security.messages.MessageManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * Specification of {@link MessageManager} class for REST messages
 * 
 * @author Ciro Formisano (ENG)
 *
 */
public class SoapMessageManager implements MessageManager {

	private Logger log;
	private SOAPMessage message;

							
	
	public SoapMessageManager(SOAPMessage message) 
	{
		this.log = LoggerFactory.getLogger(this.getClass());
		this.message = message;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setCredentials(Credentials credentials) 
	{
		log.debug("Setting credentials..");
		credentials.prepareCredentials();
		String headerString = credentials.getHeaderString();
		log.debug("Header string = "+headerString);
		
		if (headerString != null)
		{
			log.debug("Inserting credentials string into the header");
			
			try 
			{
				log.debug("Inserting the header in the message");
				this.message.getSOAPHeader().addChildElement(generateBinaryTokenElement(credentials.getAuthenticationType(), headerString));
				log.debug("Header added");
			}
			catch (SOAPException e)
			{
				log.error("Unable to generate token security header",e);
			}
		}
		else
		{
			log.debug("No headers to be inserted in the message");
		}
			
			
	}
	
	
	private SOAPElement generateBinaryTokenElement (String type,String binaryTokenValue) throws SOAPException
	{
		log.debug("Generating token SOAP element");
		log.debug("Type = "+type);
		log.debug("Value = "+binaryTokenValue);
		SOAPFactory sf = SOAPFactory.newInstance();
		SOAPElement tokenElement = sf.createElement(new QName(MessageConstants.WSSE_NAMESPACE,MessageConstants.BINARY_SECURITY_TOKEN_PREFIX+":"+MessageConstants.BINARY_SECURITY_TOKEN_LABEL));
		tokenElement.setAttribute(MessageConstants.VALUE_TYPE_LABEL, type);
		tokenElement.setAttribute(MessageConstants.ENCODING_TYPE_LABEL,MessageConstants.BASE64 );
		tokenElement.setAttribute(MessageConstants.ID_LABEL, MessageConstants.SECURITY_TOKEN);
		tokenElement.setValue(binaryTokenValue);
		log.debug("Header completed");
		return tokenElement;

	}

}

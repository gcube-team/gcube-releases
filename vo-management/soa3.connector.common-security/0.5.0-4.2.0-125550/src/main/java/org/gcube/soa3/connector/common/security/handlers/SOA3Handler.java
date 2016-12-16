package org.gcube.soa3.connector.common.security.handlers;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPHeaderElement;
import javax.xml.ws.handler.soap.SOAPMessageContext;

import org.gcube.common.clients.stubs.jaxws.GCoreService;
import org.gcube.common.clients.stubs.jaxws.handlers.AbstractHandler;
import org.gcube.soa3.connector.common.security.CredentialManager;
import org.gcube.soa3.connector.common.security.Credentials;
import org.gcube.soa3.connector.common.security.MessageConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * Soap Handler inserrting authorization credentials in the header of a soap message
 * 
 * @author Ciro Formisano (ENG)
 *
 */
public class SOA3Handler extends AbstractHandler 
{
	private Logger log;
	
	public SOA3Handler ()
	{
		this.log = LoggerFactory.getLogger(this.getClass());
	}
	
	@Override
	public void handleRequest(GCoreService<?> target, SOAPHeader header, SOAPMessageContext context) throws Exception 
	{
		log.debug("Handling request");
	
		try 
		{
			addCurrentCredentials(header);
	
		} catch (Exception e) 
		{
			log.error("cannot configure outgoing message",e);
			throw new RuntimeException("cannot configure outgoing message", e);
		}
		
	}

	//helper
	private void addCurrentCredentials(SOAPHeader header) throws Exception {
		
		Credentials credentials = CredentialManager.instance.get();
		
		if (credentials!=null)
		{
			log.debug("Credentials found");
			
			if (credentials.getHeaderString() != null)
			{
				log.debug("Message level security: generating new SOAP security header");
				SOAPHeaderElement securityHeader = header.addHeaderElement(new QName(MessageConstants.WSSE_NAMESPACE,MessageConstants.BINARY_SECURITY_TOKEN_LABEL, MessageConstants.BINARY_SECURITY_TOKEN_PREFIX));
				securityHeader.setAttribute(MessageConstants.VALUE_TYPE_LABEL, credentials.getAuthenticationType());
				securityHeader.setAttribute(MessageConstants.ENCODING_TYPE_LABEL,MessageConstants.BASE64 );
				securityHeader.setAttribute(MessageConstants.ID_LABEL, MessageConstants.SECURITY_TOKEN);
				securityHeader.setValue(credentials.getHeaderString());
				log.debug("Security header created");
			}
			else
			{
				log.debug("No message level security needed");
			}
			

		}
		else
		{
			log.debug("No credentials found in the current context");
		}
		
		
	}

}

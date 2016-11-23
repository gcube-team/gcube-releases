package org.gcube.security.soa3.connector.integration;

import javax.xml.soap.SOAPException;

import org.apache.axis.client.Stub;
import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.security.soa3.connector.integration.utils.GSSIntegrationUtils;
import org.gcube.soa3.connector.common.security.Credentials;
import org.gcube.soa3.connector.common.security.messages.MessageManager;

/**
 * 
 * Specification of {@link MessageManager} class for REST messages
 * 
 * @author Ciro Formisano (ENG)
 *
 */
public class StubMessageManager implements MessageManager {

	private GCUBELog log;
	private Stub stub;

							
	
	public StubMessageManager(Stub stub) 
	{
		this.log = new GCUBELog(this);
		this.stub = stub;
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
				this.stub.setHeader(GSSIntegrationUtils.generateSoapHeaderBinaryTokenElement(credentials.getAuthenticationType(), headerString));
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
	
	


}

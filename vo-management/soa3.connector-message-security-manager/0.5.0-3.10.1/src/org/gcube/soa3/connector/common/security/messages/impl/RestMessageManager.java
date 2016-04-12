package org.gcube.soa3.connector.common.security.messages.impl;


import org.gcube.soa3.connector.common.security.Credentials;
import org.gcube.soa3.connector.common.security.messages.MessageManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jersey.api.client.WebResource;

/**
 * 
 * Specification of {@link MessageManager} class for REST messages
 * 
 * @author Ciro Formisano (ENG)
 *
 */
public class RestMessageManager implements MessageManager {

	private Logger log;
	private WebResource resource;
	private String AUTHORIZATION_HEADER = "Authorization";
	
	public RestMessageManager (WebResource resource)
	{
		this.log = LoggerFactory.getLogger(this.getClass());
		this.resource = resource;
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
		
		if (headerString!= null)
		{
			log.debug("Inserting the header in the message");
			resource.header(AUTHORIZATION_HEADER, headerString);
			log.debug("Header added");
		}
		else
		{
			log.debug("No headers to be inserted in the message");
		}
		
		log.debug("Operation completed");

	}

}

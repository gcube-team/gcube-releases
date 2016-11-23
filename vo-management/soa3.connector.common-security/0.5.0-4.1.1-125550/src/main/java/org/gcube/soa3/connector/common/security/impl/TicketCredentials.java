package org.gcube.soa3.connector.common.security.impl;

import org.gcube.soa3.connector.common.security.Credentials;

/**
 * 
 * Ticket credentials actually are represented by a ticket
 * 
 * @author Ciro Formisano (ENG)
 *
 */
public class TicketCredentials implements Credentials {

	private final String 	SESSION = "SES";
	private String ticket;
	private String headerString;
	private boolean isReady;
	
	public TicketCredentials(String ticket) 
	{
		this.ticket = ticket;
		this.isReady = false;

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void prepareCredentials() 
	{
		if (!this.isReady)
		{
			this.headerString = SESSION+" "+this.ticket;
			this.isReady = true;
		}
	}

	/**
	 * Returns "SES"
	 */
	@Override
	public String getAuthenticationType() 
	{
		return SESSION;
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getHeaderString() 
	{
		return headerString;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getAuthenticationString() 
	{
		return this.ticket;
	}
	
	@Override
	public void disposeCredentials() 
	{
		this.headerString = null;
		this.isReady = false;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isPrepared() 
	{

		return isReady;
	}

}

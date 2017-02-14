package org.gcube.soa3.connector.common.security.impl;

import org.gcube.soa3.connector.common.security.Credentials;

/**
 * 
 * Federated credentials actually are represented by an assertion id
 * 
 * @author Ciro Formisano (ENG)
 *
 */
public class FederatedCredentials implements Credentials {

	private final String 	FEDERATED = "FED";
	private 				String assertionId;
	private String headerString;
	private boolean isReady;
	
	public FederatedCredentials(String assertionId) 
	{
		this.assertionId = assertionId;
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
			this.headerString = FEDERATED+" "+this.assertionId;
			this.isReady = true;
		}
	}

	/**
	 * Returns "FED"
	 */
	@Override
	public String getAuthenticationType() 
	{
		return FEDERATED;
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
		return this.assertionId;
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

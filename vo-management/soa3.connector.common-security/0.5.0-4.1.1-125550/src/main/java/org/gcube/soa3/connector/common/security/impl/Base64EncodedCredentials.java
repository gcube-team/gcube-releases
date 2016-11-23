package org.gcube.soa3.connector.common.security.impl;

import org.bouncycastle.util.encoders.Base64;
import org.gcube.soa3.connector.common.security.Credentials;


/**
 * 
 * Implementation of {@link Credentials} encoded in Base 64
 * 
 * @author Ciro Formisano (ENG)
 *
 */
public class Base64EncodedCredentials implements Credentials {

	private Credentials actualCredentials;
	private String headerCredentials;
	private boolean isReady;
	
	public Base64EncodedCredentials(Credentials actualCredentials) 
	{
		this.actualCredentials = actualCredentials;
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
			this.actualCredentials.prepareCredentials();
			
			if (this.actualCredentials.getHeaderString() != null) this.headerCredentials = this.actualCredentials.getAuthenticationType()+" "+new String (Base64.encode(this.actualCredentials.getAuthenticationString().getBytes()));
			
			this.isReady = true;
		}
		

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getAuthenticationType() 
	{
		return this.actualCredentials.getAuthenticationType();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getAuthenticationString() 
	{
		return this.actualCredentials.getAuthenticationString();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getHeaderString() 
	{
		return this.headerCredentials;
	}
	
	@Override
	public void disposeCredentials() 
	{
		
		this.actualCredentials.disposeCredentials();
		this.headerCredentials = null;
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

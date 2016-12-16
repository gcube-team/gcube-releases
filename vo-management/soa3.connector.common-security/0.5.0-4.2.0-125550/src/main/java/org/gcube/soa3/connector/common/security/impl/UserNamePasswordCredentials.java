package org.gcube.soa3.connector.common.security.impl;

import org.gcube.soa3.connector.common.security.Credentials;

/**
 * 
 * Username and password credentials
 * 
 * @author Ciro Formisano (ENG)
 *
 */
public class UserNamePasswordCredentials implements Credentials {

	private final String 	PASSWORD_SEPARATOR = ":",
							BASIC = "BASIC";
	private String userName;
	private char [] password;
	private String 	headerString,
					authenticationString;
	
	private boolean isReady;
	
	public UserNamePasswordCredentials(String userName, char [] password) 
	{
		this.userName = userName;
		this.password = password;
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
			this.headerString = BASIC + " "+this.getAuthenticationString(); 
			this.isReady = true;
		}
	}

	/**
	 * Returns "BASIC"
	 */
	@Override
	public String getAuthenticationType() 
	{
		return BASIC;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getAuthenticationString() 
	{
		if (this.authenticationString == null) this.authenticationString = new StringBuilder(this.userName).append(PASSWORD_SEPARATOR).append(this.password).toString();
		
		return this.authenticationString;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getHeaderString() 
	{
		return headerString;
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

package org.gcube.soa3.connector.common.security.impl;

import org.gcube.soa3.connector.common.security.Credentials;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * Implementation of {@link Credentials} combining TLS and another string based credential model (extraCredentials)
 * 
 * @author Ciro Formisano (ENG)
 *
 */
public class X509CombinedCredentials extends X509TLSCredentials 
{
	private Logger log;
	private Credentials extraCredentials;
	
	public X509CombinedCredentials(Credentials extraCredentials) 
	{
		this (extraCredentials,null,null,null,null,null);
		
	}
	

	public X509CombinedCredentials(Credentials extraCredentials,String certFile, String keyFile, char[] keyPassword,String trustDir, String trustExt) 
	{
		super (certFile,keyFile,keyPassword,trustDir,trustExt);
		this.log = LoggerFactory.getLogger(this.getClass());
		this.extraCredentials = extraCredentials;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getAuthenticationType() 
	{
		if (extraCredentials != null) return super.getAuthenticationType()+"-"+this.extraCredentials.getAuthenticationString();
		else return super.getAuthenticationType();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void prepareCredentials() 
	{
		log.debug("Setting X509 certificate...");
		super.prepareCredentials();
		log.debug("Certificates loaded");
		if (this.extraCredentials != null)
		{
			log.debug("Generating string based credentials...");
			this.extraCredentials.prepareCredentials();
			log.debug("String based credentials generated");
		}
		
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getAuthenticationString() 
	{
		if (this.extraCredentials != null) return this.extraCredentials.getAuthenticationString();
		else return super.getAuthenticationString();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getHeaderString() 
	{
		if (this.extraCredentials != null) return this.extraCredentials.getHeaderString();
		else return null;
	}
	
	@Override
	public void disposeCredentials() 
	{
		if (this.extraCredentials != null) this.extraCredentials.disposeCredentials();
		
		super.disposeCredentials();
	}

}

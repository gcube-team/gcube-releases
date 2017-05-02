package org.gcube.soa3.connector.common.security.impl;

import org.gcube.soa3.connector.common.security.CredentialManager;
import org.gcube.soa3.connector.common.security.Credentials;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * Credential Manager singleton class is based on an {@link InheritableThreadLocal} internal variable storing the {@link Credentials} object. The credentials
 * stored are valid for the current thread and for all the descendants 
 * 
 * @author Ciro Formisano (ENG)
 *
 */
public class CredentialManagerImpl implements CredentialManager
{
	
	private InheritableThreadLocal<Credentials> credentials;
	private Logger logger;

	CredentialManagerImpl ()
	{
		this.logger = LoggerFactory.getLogger(this.getClass());
		this.credentials = new InheritableThreadLocal<Credentials>();
		
	}
	
	
	
	/*
	 * (non-Javadoc)
	 * @see org.gcube.soa3.connector.common.security.CredentialManager#set(org.gcube.soa3.connector.common.security.Credentials)
	 */
	public void set (Credentials credentials)
	{
		logger.debug("Setting credentials for thread "+Thread.currentThread().getId());
		credentials.prepareCredentials();
		this.credentials.set(credentials);
	}
	

	/*
	 * (non-Javadoc)
	 * @see org.gcube.soa3.connector.common.security.CredentialManager#get()
	 */
	public Credentials get ()
	{
		logger.debug("Getting credentials for thread "+Thread.currentThread().getId());
		return this.credentials.get();
	}
	

	/*
	 * (non-Javadoc)
	 * @see org.gcube.soa3.connector.common.security.CredentialManager#reset()
	 */
	public void reset ()
	{
		logger.debug("Resetting and disposing credentials for thread "+Thread.currentThread().getId());
		this.credentials.get().disposeCredentials();
		this.credentials.remove();
	}
	

}

package org.gcube.soa3.connector.common.security;

import org.gcube.soa3.connector.common.security.impl.CredentialManagerFactory;

/**
 * 
 * Credential Manager singleton class is based on an {@link InheritableThreadLocal} internal variable storing the {@link Credentials} object. The credentials
 * stored are valid for the current thread and for all the descendants 
 * 
 * @author Ciro Formisano (ENG)
 *
 */
public interface CredentialManager 
{
	public static final CredentialManager instance = CredentialManagerFactory.getCredentialManager();
	

	/**
	 * 
	 * Sets the credentials for this Thread and the descendants 
	 * 
	 * @param credentials
	 */
	public void set (Credentials credentials);
	
	/**
	 * 
	 * Gets the credentials (with {@link InheritableThreadLocal} logic)
	 * 
	 * @return the credentials
	 */
	public Credentials get ();
	
	/**
	 * 
	 * Removes the credentials
	 * 
	 */
	public void reset ();
	

}

package org.gcube.common.core.security;

/**
 * 
 * Utility interface that wraps the credential object
 * 
 * @author Ciro Formisano
 *
 */
public interface SecurityCredentials 
{
	/**
	 * 
	 * Provides the credentials in String form
	 * 
	 * @return
	 * @throws Exception
	 */
	public String getCredentialsAsString () throws Exception;
	
	/**
	 * 
	 * Provides the original credential object
	 * 
	 * @return
	 */
	public Object getCredentialsAsObject ();
}

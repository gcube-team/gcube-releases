package org.gcube.soa3.connector.common.security;


/**
 * 
 * Interface representing the credentials
 * 
 * @author Ciro Formisano (ENG)
 *
 */
public interface Credentials 
{

	/**
	 * 
	 * Prepares the credentials to be sent with the message: if the credentials are
	 * string based and should be set in an header, they are returned as a String object
	 * 
	 * @return 
	 */
	public void prepareCredentials ();
	
	/**
	 * 
	 * Disposes the credentials making them unusable
	 * 
	 */
	public void disposeCredentials ();
	
	/**
	 * 
	 * The credentials type
	 * 
	 * @return the credentials type
	 */
	public String getAuthenticationType ();
	
	/**
	 * 
	 * Gets the raw credentialS String if exists, a String representiation of the credentials used otherwise
	 * 
	 * @return the credentials String
	 */
	public String getAuthenticationString ();
	
	/**
	 * 
	 * Gets the credential string to be set in the header of the message
	 * @return the credentials string if it is required to be set in an header, null otherwise
	 */
	public String getHeaderString ();
	
	
	/**
	 * 
	 * 
	 * 
	 * @return true if the credentials have been correctly prepared
	 */
	public boolean isPrepared ();


}

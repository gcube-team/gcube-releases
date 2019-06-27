package org.gcube.common.core.security;

import java.rmi.Remote;

import org.ietf.jgss.GSSCredential;

/**
 * Defines the behaviour of <em>security managers</em>, i.e. providers of security-related facilities.
 * <p>Security managers keep track of credentials to use for outgoing calls in one or more concurrent threads.
 * They allow different clients which execute within a thread to exchange credentials asynchronously; a client which
 * obtains credentials hands them over to the manager where clients which require them, typically in order to
 * make a call, may later find them.  Clients may also delegate credentials across different threads (e.g. when they
 * spawn them).
 * <p> Security managers mediate also between clients and lower-level facilities to set security settings on port-type
 * stubs before making calls.
 * <p> Security managers are transparent in an unsecure context, where they can be safely invoked to no effect.
 * Implementations ought to implement the method {@link #isSecurityEnabled()} to discriminate secure from unsecure contexts.
 *
 * @author Fabio Simeoni (University of Strathclyde), Ciro Formisano (ENG)
 *
 */
public interface GCUBESecurityManager {

	/** Enumeration for levels of encryption of outgoing calls. */
	public enum AuthMode{INTEGRITY,PRIVACY,BOTH,NONE}; 
	
	/** Enumeration for levels of encryption of outgoing calls. */
	public enum AuthMethod{GSI_CONV, GSI_TRANS,NONE}; 
	
	/** Enumeration for modes of credential delegation for outgoing calls. */  
	public enum DelegationMode{FULL,LIMITED,NONE}; 	
	
	/**
	 * Indicates whether the manager is operating in a secure context. If not, invoking its methods
	 * will have no effect.
	 * 
	 * @return <code>true</code> if security is enabled, <code>false</code> otherwise. 
	 */
	public boolean isSecurityEnabled();
	
	/**
	 * @deprecated: use useCredentials(SecurityCredentials credentials) instead
	 * If security is enabled, it sets given credentials for outgoing calls in the current thread. 
	 * It has no effect otherwise.
	 * @param credentials the credentials.
	 * @throws Exception if security is enabled, but credentials are corrupt.
	 */
	@Deprecated
	public void useCredentials(GSSCredential credentials) throws Exception;
	

	
	/**
	 * If security is enabled, it sets given credentials for outgoing calls in the current thread. 
	 * It has no effect otherwise.
	 * @param credentials the credentials.
	 * @throws Exception if security is enabled, but credentials are corrupt.
	 */
	public void useCredentials(SecurityCredentials credentials) throws Exception;
	
	/**
	 * If security is enabled, it sets given credentials for outgoing calls in a
	 * given thread.
	 * @param thread the thread.
	 * @param credentials (optional) the credentials. If omitted, it delegates the credentials used for 
	 * the current thread to the given thread.
	 * @throws Exception if security is enabled, but credentials are corrupt.
	 */
	public void useCredentials(Thread thread, SecurityCredentials... credentials) throws Exception;

	/**
	 * It returns the credentials for outgoing calls currently set in the current thread.
	 * @return the credentials.
	 */
	public SecurityCredentials getCredentials();

	/**
	 * If security is enabled,  sets the desired level of encryption and the mode of credential delegation
	 * on the stub of a remote porttype.  It has no effect otherwise.
	 * @param s the stub.
	 * @param e the encryption level.
	 * @param d the delegation mode.
	 * @throws Exception if the settings could not be enforced.
	 */
	public void setSecurity(Remote s, AuthMode e, DelegationMode d) throws Exception;
	
	/**
	 * 
	 * Adds an authentication method to be used in this security context
	 * @param m the authentication method
	 */
	public void setAuthMethod (AuthMethod m);
	
	
}
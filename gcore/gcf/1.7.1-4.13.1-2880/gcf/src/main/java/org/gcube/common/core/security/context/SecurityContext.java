package org.gcube.common.core.security.context;

import javax.security.auth.Subject;

import org.gcube.common.core.security.GCUBEDefaultSecurityConfiguration;
import org.gcube.common.core.security.GCUBESecurityManager;
import org.gcube.common.core.security.impl.GCUBECredentialAdder;
import org.globus.wsrf.impl.security.descriptor.ServiceSecurityDescriptor;
import org.ietf.jgss.GSSCredential;

public interface SecurityContext {

	/** Default Security Configuration option name. */
	public static final String DEFAULT_SECURITY_CONFIGURATION = "defaultSecurityConfiguration";

	/**
	 * Returns an object able to build a default security configuration for the services
	 * @return the default service security descriptor builder, null if not set
	 */

	public abstract GCUBEDefaultSecurityConfiguration getDefaultServiceSecurityConfiguration();

	/**
	 * 
	 * provides the incoming service security descriptor
	 * 
	 * @return the incoming service security descriptor
	 */
	public abstract ServiceSecurityDescriptor getDefaultIncomingMessagesSecurityDescriptor();

	/**
	 * 
	 * provides the outgoing service security descriptor
	 * 
	 * @return the outgoing service security descriptor
	 */
	public abstract ServiceSecurityDescriptor getDefaultOutgoingMessagesSecurityDescriptor();

	/**
	 * 
	 * Provides the subject of the related container
	 * 
	 * @return the subject containing the container credentials
	 */
	public abstract Subject getDefaultSubject();

	/**
	 * 
	 * Gets the container credentials if exists
	 * 
	 * @return the container credentials
	 */
	public abstract GSSCredential getDefaultCredentials();

	/**
	 * 
	 * Utility method to generate a default security manager based on implementations.properties
	 * 
	 * @return the Security Manager instance
	 * @throws Exception if something goes wrong
	 */
	public abstract GCUBESecurityManager getDefaultSecurityManager()
			throws Exception;

	/**
	 * 
	 * gets a simple security manager that adds the credentials to the request
	 * 
	 * @return the credential adder
	 */
	public GCUBECredentialAdder getCredentialsAdder ();
	
}
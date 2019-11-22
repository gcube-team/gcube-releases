package org.gcube.common.core.security.context.impl;

import java.io.File;

import javax.security.auth.Subject;

import org.gcube.common.core.contexts.GHNContext;
import org.gcube.common.core.security.GCUBEDefaultSecurityConfiguration;
import org.gcube.common.core.security.context.SecurityContext;
import org.gcube.common.core.security.impl.GCUBECredentialAdder;
import org.gcube.common.core.security.utils.DefaultSecurityDescriptorBuilder;
import org.gcube.common.core.utils.logging.GCUBELog;
import org.globus.wsrf.impl.security.descriptor.ServiceSecurityDescriptor;
import org.ietf.jgss.GSSCredential;

public abstract class DefaultGHNSecurityContext implements SecurityContext
{
	private GCUBELog logger;
	
	protected DefaultSecurityDescriptorBuilder defaultSecurityDescriptorBuilder;
	/** Default credentials  */
	protected GSSCredential defaultCredentials;
	/** Default subject  */
	protected Subject defaultSubject;
	
	public DefaultGHNSecurityContext() 
	{
		this.logger = new GCUBELog(this);
	}
	
	protected void init (String pathToDefaultSecurityConfiguration)
	{
		if (pathToDefaultSecurityConfiguration != null)
		{
			pathToDefaultSecurityConfiguration = GHNContext.getContext().getLocation()+File.separatorChar+pathToDefaultSecurityConfiguration;
			logger.debug("Default security configuration "+pathToDefaultSecurityConfiguration);
			try 
			{
				defaultSecurityDescriptorBuilder = new DefaultSecurityDescriptorBuilder (pathToDefaultSecurityConfiguration);
				
			} catch (Exception e) 
			{
				logger.error("Invalid default security configuration",e);
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see org.gcube.common.core.security.context.impl.SecurityContext#getDefaultServiceSecurityConfiguration()
	 */
	
	@Override
	public GCUBEDefaultSecurityConfiguration getDefaultServiceSecurityConfiguration ()
	{
		if (defaultSecurityDescriptorBuilder != null) return defaultSecurityDescriptorBuilder.getGCUBEDefaultSecurityConfiguration();
		else return null;
	}
	
	/* (non-Javadoc)
	 * @see org.gcube.common.core.security.context.impl.SecurityContext#getDefaultIncomingMessagesSecurityDescriptor()
	 */
	@Override
	public ServiceSecurityDescriptor getDefaultIncomingMessagesSecurityDescriptor ()
	{
		if (defaultSecurityDescriptorBuilder != null)  return defaultSecurityDescriptorBuilder.getIncomingMessagesSecurityDescriptor();
		else return null;
	}
	
	/* (non-Javadoc)
	 * @see org.gcube.common.core.security.context.impl.SecurityContext#getDefaultOutgoingMessagesSecurityDescriptor()
	 */
	@Override
	public ServiceSecurityDescriptor getDefaultOutgoingMessagesSecurityDescriptor ()
	{
		if (defaultSecurityDescriptorBuilder != null)  return defaultSecurityDescriptorBuilder.getOutgoingMessagesSecurityDescriptor();
		else return null;
	}

	/* (non-Javadoc)
	 * @see org.gcube.common.core.security.context.impl.SecurityContext#getCredentialsAdder()
	 */
	@Override
	public GCUBECredentialAdder getCredentialsAdder()
	{
		return new GCUBECredentialAdder ();
		
	}
	

	
	

}

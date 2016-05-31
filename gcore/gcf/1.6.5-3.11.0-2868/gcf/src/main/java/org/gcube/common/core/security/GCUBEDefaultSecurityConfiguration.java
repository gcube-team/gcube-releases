package org.gcube.common.core.security;

import org.gcube.common.core.contexts.GHNContext;
import org.gcube.common.core.utils.logging.GCUBELog;


/**
 * 
 * Default security configuration
 * 
 * @author Ciro Formisano (ENG)
 *
 */
public class GCUBEDefaultSecurityConfiguration 
{

	private boolean defaultCredentialPropagationSet,
					propagateCallerCredentials,
					propagateCallerCredentialsOverride,
					inEnabled,
					outEnabled,
					inOverride,
					outOverride;
	
	private static final GCUBELog logger = new GCUBELog(GCUBEDefaultSecurityConfiguration.class);

	
	/**
	 * 
	 * Tells if the security is enabled for incoming messages
	 * 
	 * @return true if the security is enabled for incoming messages
	 */
	public boolean isInEnabled() 
	{
		return inEnabled;
	}

	public void setInEnabled(boolean inEnabled) 
	{
		this.inEnabled = inEnabled;
	}

	/**
	 * 
	 * Tells if the security is enabled for outgoing messages
	 * 
	 * @return true if the security is enabled for outgoing messages
	 */
	public boolean isOutEnabled() 
	{
		return outEnabled;
	}

	public void setOutEnabled(boolean outEnabled) 
	{
		this.outEnabled = outEnabled;
	}

	/**
	 * 
	 * Tells if the global security configuration must override local security configuration for incoming messages
	 * 
	 * @return true if the global configuration must override the service configuration
	 */
	public boolean isInOverride() 
	{
		return inOverride;
	}

	public void setInOverride(boolean inOverride) 
	{
		this.inOverride = inOverride;
	}

	/**
	 * 
	 * Tells if the global security configuration must override local security configuration for outgoing messages
	 * 
	 * @return true if the global configuration must override the service configuration
	 */
	public boolean isOutOverride() 
	{
		return outOverride;
	}

	public void setOutOverride(boolean outOverride) 
	{
		this.outOverride = outOverride;
	}

	public void setDefaultCredentialPropagationSet(boolean defaultCredentialPropagationSet) 
	{
		this.defaultCredentialPropagationSet = defaultCredentialPropagationSet;
	}

	public void setPropagateCallerCredentials(boolean propagateCallerCredentials) 
	{
		this.propagateCallerCredentials = propagateCallerCredentials;
	}

	public void setPropagateCallerCredentialsOverride(boolean propagateCallerCredentialsOverride) 
	{
		this.propagateCallerCredentialsOverride = propagateCallerCredentialsOverride;
	}

	/**
	 * 
	 * 
	 * @return true if the default credential propagation policy is set
	 */
	public boolean defaultCredentialPropagationSet ()
	{
		return defaultCredentialPropagationSet;
	}
	
	/**
	 * 
	 * @return true if the caller credentials should be propagated, false if the service credentials should be propagated, always false in client mode
	 */
	public boolean propagateCallerCredentials ()
	{
		if (GHNContext.getContext().isClientMode())
		{ 
			logger.debug("A client doesn't propagate caller credentials: the caller doesn't exists");
			return false;
		
		}
		else return propagateCallerCredentials;
	}

	/**
	 * 
	 * @return true if the default propagation policy must override the service policy, always true in client mode
	 */
	public boolean propagateCallerCredentialsOverride ()
	{
		if (GHNContext.getContext().isClientMode())
		{
			logger.debug("A client doesn't propagate caller credentials: the caller doesn't exists");
			return true;
		}
		else return propagateCallerCredentialsOverride;
	}
}

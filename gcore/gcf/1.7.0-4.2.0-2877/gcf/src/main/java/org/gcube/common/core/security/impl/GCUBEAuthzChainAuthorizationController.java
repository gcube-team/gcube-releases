/*
 * Portions of this file Copyright 1999-2005 University of Chicago
 * Portions of this file Copyright 1999-2005 The University of Southern California.
 *
 * This file or a portion of this file is licensed under the
 * terms of the Globus Toolkit Public License, found at
 * http://www.globus.org/toolkit/download/license.html.
 * If you redistribute this file, with or without
 * modifications, you must include this notice in the file.
 */
package org.gcube.common.core.security.impl;

import java.util.Map;

import javax.security.auth.Subject;

import org.apache.axis.MessageContext;
import org.gcube.common.core.contexts.GCUBEServiceContext;
import org.gcube.common.core.faults.GCUBEException;
import org.gcube.common.core.faults.GCUBEUnrecoverableException;
import org.gcube.common.core.security.GCUBEServiceAuthorizationController;
import org.gcube.common.core.security.GCUBEServiceSecurityController;
import org.gcube.common.core.security.GCUBEServiceSecurityManager;
import org.gcube.common.core.utils.logging.GCUBELog;
import org.globus.wsrf.Resource;
import org.globus.wsrf.ResourceContext;
import org.globus.wsrf.ResourceContextException;
import org.globus.wsrf.ResourceException;
import org.globus.wsrf.config.ConfigException;
import org.globus.wsrf.impl.security.authentication.Constants;
import org.globus.wsrf.impl.security.authorization.Authorization;
import org.globus.wsrf.impl.security.authorization.ServiceAuthorizationChain;
import org.globus.wsrf.impl.security.authorization.exceptions.AuthorizationException;
import org.globus.wsrf.impl.security.authorization.exceptions.CloseException;
import org.globus.wsrf.impl.security.descriptor.ContainerSecurityConfig;
import org.globus.wsrf.impl.security.descriptor.SecureResourcePropertiesHelper;
import org.globus.wsrf.impl.security.descriptor.SecurityDescriptor;
import org.globus.wsrf.impl.security.descriptor.SecurityPropertiesHelper;
import org.globus.wsrf.impl.security.descriptor.ServiceSecurityDescriptor;
import org.globus.wsrf.impl.security.util.AuthUtil;
import org.globus.wsrf.impl.security.util.PDPUtils;
import org.globus.wsrf.utils.ContextUtils;


/**
 * 
 * Authorization controller based on the authorization chain configured in the security descriptor
 * 
 * @author Ciro Formisano (ENG)
 *
 */
public class GCUBEAuthzChainAuthorizationController implements GCUBEServiceAuthorizationController 
{

	/** Object logger. */
	protected GCUBELog logger; //object logger
	/** Authorization chain **/
	private ServiceAuthorizationChain authzChain = null;
	/** Security configuration **/
	private GCUBEServiceSecurityManager securityManager;
	
	
	/**
	 * 
	 */
	public GCUBEAuthzChainAuthorizationController() 
	{
		this.logger  = new GCUBELog(this);
	}
	
	@Override
	/**
	 * {@inheritDoc}
	 */
	public void initialise(GCUBEServiceContext ctxt, GCUBEServiceSecurityManager securityManager) throws Exception 
	{
		this.authzChain = null;
		this.securityManager = securityManager;
	}

	@Override
    /** {@inheritDoc} */
    public void authoriseCall(Map<String, Object> authzMap) throws GCUBEException 
    {
		logger.debug("starting authorization process...");
		
    	if (isSecurityEnabled())
    	{
            MessageContext messageContext = (MessageContext) authzMap.get(GCUBEServiceSecurityController.MESSAGE_CONTEXT);
            
            if (messageContext != null) 
            {
            	logger.debug("authorizing...");
            	performAuthorisation(messageContext);
            	logger.debug("authorization process completed: authorization granted");
            }
            else throw new GCUBEUnrecoverableException("Message context not found");
    	}
    	else logger.debug("Security not enabled");
    }
	
	/**
	 * {@inheritDoc}
	 */
	public boolean isSecurityEnabled() 
	{
		return this.securityManager.isSecurityEnabled();
	}
    
    /**
     * 
     * Performs authorisation operation
     * 
     * @param messageContext
     * @throws GCUBEUnrecoverableException
     */
    
    private void performAuthorisation (MessageContext messageContext) throws GCUBEUnrecoverableException
    {
        logger.debug("Authorization");
        Subject subject = (Subject) messageContext.getProperty(Constants.PEER_SUBJECT);

        // If subject is null, no authorization is done.
        if (subject == null) {
            logger.debug("No authenticaiton done, so no authz");
            return;
        }

        String servicePath = ContextUtils.getTargetServicePath(messageContext);
        // If null will fail further along chain, so return.GCUBEException
        if (servicePath == null) {
            return;
        }
        logger.debug("Service path " + servicePath);

        // If no auth mechanism was enforced for this operation, no
        // need to do authz.
//        Boolean authzReq =
//            (Boolean)messageContext.getProperty(Constants.AUTHZ_REQUIRED);
//        if ((authzReq != null) && (authzReq.equals(Boolean.FALSE))) {
//            logger.debug("Authz not required, since auth not enforced");
//            return;
//        }

        if (this.authzChain == null) this.authzChain = generateServiceAuthzChain(messageContext, servicePath);
        
        if (this.authzChain != null)
        {

	        // AuthzChain cannot be null here
	        logger.debug("Invoking authorize on authz chain");
	        try {
	
	            authzChain.authorize(subject, messageContext, servicePath);
	
	        } catch (AuthorizationException e) {
	        	logger.error("Authorization failed",e);
	            throw new GCUBEUnrecoverableException(e);
	        } finally {
	            try {
	                authzChain.close();
	            } catch (CloseException e) {
	                throw new GCUBEUnrecoverableException(e);
	            }
	        }
        }
        else logger.debug("Unable to retrieve authz chain");
    }

    /**
     * 
     * Retrieves the authz chain if it is not yet defined
     * 
     * @param messageContext
     * @param servicePath
     * @return
     * @throws GCUBEUnrecoverableException
     */
    
    private ServiceAuthorizationChain generateServiceAuthzChain (MessageContext messageContext,String servicePath) throws GCUBEUnrecoverableException
    {
    	logger.debug("Generating Security Descriptor");
    	ServiceAuthorizationChain authzChain = null;
        // get resource
        Resource resource = null;
        try {
            ResourceContext context =
                ResourceContext.getResourceContext(messageContext);
            resource = context.getResource();
        } catch (ResourceContextException exp) {
            // FIXME: quiet catch, set resource to null
            resource = null;
            logger.debug("Error getting resource/may not exist", exp);
        } catch (ResourceException exp) {
            // FIXME: quiet catch, set resource to null
            resource = null;
            logger.debug("Error getting resource/may not exist", exp);
        }
        logger.debug("Resource is null: " + (resource == null));

        // Subject is not null, but check if
        // resource/service/container required security. If no
        // security descriptor was present, return.
        
        SecurityDescriptor secDesc = null;
        
        if (resource != null) {
            secDesc = (ServiceSecurityDescriptor)SecureResourcePropertiesHelper
                .getResourceSecDescriptor(resource);
            if (secDesc != null) {
                // use helper class, so initialization is done if
                // required.
                try {
                    authzChain =
                        SecureResourcePropertiesHelper.getAuthzChain(resource);
                } catch (ConfigException exp) {
                    throw new GCUBEUnrecoverableException(exp);
                }
            }
        }
     
        if (authzChain == null) 
        {
            try {
                ContainerSecurityConfig config =
                    ContainerSecurityConfig.getConfig();
                secDesc = config.getSecurityDescriptor();
                if (secDesc != null) {
                    authzChain = secDesc.getAuthzChain();
                }
                // Check if insecure container
                if (authzChain == null) {
                    if (config.getSecurityDescriptorFile() == null) {
                        logger.debug("Insecure container");
                        secDesc = null;
                    }
                }
            } catch (ConfigException exp) {
                throw new GCUBEUnrecoverableException(exp);
            }
        }

        logger.debug("Sec desc after container is not null: " 
                     + (secDesc != null));

        // No security descriptor, return
        if (secDesc != null && authzChain == null) {
            logger.debug("Insecure setting, return");
	        // Security descriptor present and subject not null, resort to
	        // default authorization
            logger.debug("Sec desc is present, default authz chain");
            String authzString = getDefaultAuthzChain(servicePath,
                                                      resource);
            try 
            {
                authzChain = PDPUtils.getServiceAuthzChain(authzString,
                                                           servicePath);
            } catch (ConfigException exp) {
                throw new GCUBEUnrecoverableException(exp);
            }
        }
        
        return authzChain;

    }
    
    /**
     * 
     * @param servicePath
     * @param resource
     * @return
     */
    private String getDefaultAuthzChain(String servicePath, Resource resource) 
    {

    	logger.debug("Getting the default authorization chain");
		String interceptor = null;
		boolean gridMapPresent;
		try 
		{
			gridMapPresent = SecurityPropertiesHelper.gridMapPresent(servicePath, resource);
			logger.debug("gridmap present"+gridMapPresent);
		} 
		catch (ConfigException exp) 
		{
		// FIXME: throw error ?
			gridMapPresent = false;
			logger.debug("gridmap present"+gridMapPresent+" for configuration error");
		}
		
		if (!gridMapPresent) 
		{
			logger.debug("Self authorization");
			interceptor = AuthUtil.getPDPName(Authorization.AUTHZ_SELF);
		} 
		else 
		{
			logger.debug("gridmap authorization");
			interceptor = AuthUtil.getPDPName(Authorization.AUTHZ_GRIDMAP);
		}
		return interceptor;
}



}

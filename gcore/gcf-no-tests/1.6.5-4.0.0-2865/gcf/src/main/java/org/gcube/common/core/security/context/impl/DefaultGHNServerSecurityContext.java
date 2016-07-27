package org.gcube.common.core.security.context.impl;

import javax.security.auth.Subject;

import org.gcube.common.core.contexts.GHNContext;
import org.gcube.common.core.security.GCUBESecurityManager;
import org.gcube.common.core.security.GCUBEServiceSecurityManager;
import org.gcube.common.core.utils.logging.GCUBELog;
import org.globus.gsi.jaas.JaasGssUtil;
import org.globus.wsrf.config.ContainerConfig;
import org.globus.wsrf.impl.security.descriptor.ContainerSecurityConfig;
import org.globus.wsrf.impl.security.descriptor.ContainerSecurityDescriptor;
import org.ietf.jgss.GSSCredential;


/**
 * 
 * A context that manages all the security related operations
 * 
 * @author Ciro Formisano
 *
 */
public class DefaultGHNServerSecurityContext extends DefaultGHNSecurityContext 
{
	private GCUBELog logger;
	
	public DefaultGHNServerSecurityContext() 
	{
		super ();
		this.logger = new GCUBELog(this);
		init();
	}
	
	/**
	 * 
	 * Inits the SecurityContext
	 * 
	 */
	private void init ()
	{
		logger.debug("Loading server mode default security configuration");
		String pathToDefaultSecurityConfiguration = ContainerConfig.getConfig().getOption(DEFAULT_SECURITY_CONFIGURATION);
		super.init(pathToDefaultSecurityConfiguration);
	
	}
	


	
	
//	/**
//	 * 
//	 * Generates a Security manager for the provided {@link GCUBEServiceContext}
//	 * 
//	 * @param serviceContext the service context
//	 * @return
//	 * @throws Exception if something goes wrong
//	 */
//	 @SuppressWarnings({ "unchecked", "rawtypes" })
//	GCUBEServiceSecurityManager generateServiceSecurityManager (GCUBEServiceContext serviceContext) throws Exception
//	{
//	   	logger.info("Generating security manager...");
//    	GCUBEServiceSecurityManager securityManager = null;
//	    //configure security manager
//	    String securityManagerClassName = (String) serviceContext.getProperty(SECURITY_MANAGER_JNDI_NAME);
//    	if (securityManagerClassName!=null) {
//    		//get class
//    		Class<GCUBEServiceSecurityManager> securityManagerClass = (Class) Class.forName(securityManagerClassName);//placate compiler
//    		if (!GCUBEServiceSecurityManager.class.isAssignableFrom(securityManagerClass)) //but do run-time check
//    			throw new Exception(securityManagerClassName+" does not implement "+GCUBEServiceSecurityManager.class.getName());
//    		//invoke constructor reflectively
//    		securityManager=securityManagerClass.newInstance();
//    	}
//    	else securityManager=GHNContext.getImplementation(GCUBEServiceSecurityManager.class);
//    	securityManager.initialise(serviceContext);
//    	
//    	GCUBEDefaultSecurityConfiguration defaultSecurityConfiguration = SecurityContext.getInstance().getDefaultServiceSecurityConfiguration();
//    	
//    	if (defaultSecurityConfiguration != null && defaultSecurityConfiguration.defaultCredentialPropagationSet()&& defaultSecurityConfiguration.propagateCallerCredentialsOverride())
//    	{
//    		logger.debug("Override every propagate configuration with the container configuration");
//    		securityManager.propagateCallerCredentials(defaultSecurityConfiguration.propagateCallerCredentials());
//    	}
//    	else 
//    	{
//    		logger.debug("Loading credentials propagation property from jndi service environment");
//    		Boolean useCallerCredentials = (Boolean) serviceContext.getProperty(PROPAGATE_CALLER_CREDENTIALS_JNDI_NAME);
//    		logger.debug("Credential propagation property = "+useCallerCredentials);
//    		
//    		if (useCallerCredentials != null) securityManager.propagateCallerCredentials(useCallerCredentials);
//    		else if (defaultSecurityConfiguration != null && defaultSecurityConfiguration.defaultCredentialPropagationSet())
//    		{
//    			logger.debug("Loading default configuration");
//    			logger.debug("Default credential propagation property "+defaultSecurityConfiguration.propagateCallerCredentials());
//    			securityManager.propagateCallerCredentials(defaultSecurityConfiguration.propagateCallerCredentials());
//    		}
//    		else
//    		{
//    			logger.warn("No service propagation property set and no default configuration available");
//    		}
//    		
//    		
//    	}
//    		
//    	logger.info("managing security with a "+securityManager.getClass().getSimpleName());
//    	return securityManager;
//	}
	
	/* (non-Javadoc)
	* @see org.gcube.common.core.security.context.impl.SecurityContext#getDefaultrCredentials()
	*/
		@Override
		public GSSCredential getDefaultCredentials ()
		{
			if (this.defaultCredentials == null) 
			{

				try
				{
					Subject systemSubject = getDefaultSubject();
					this.logger.debug("subject = "+systemSubject);
				defaultCredentials = JaasGssUtil.getCredential(systemSubject);
				}
				catch (Exception e)
				{
					this.logger.error("Unable to load container credentials, some operations could be not available", e);
				}
			}
			
			return defaultCredentials;
		}
	 
	 /* (non-Javadoc)
	 * @see org.gcube.common.core.security.context.impl.SecurityContext#getDefaultSubject()
	 */
	 @Override
	public Subject getDefaultSubject ()
	 {
		 if (this.defaultSubject == null)
		 {
			try 
			{
			   logger.debug("Loading server mode container credentials");
			   ContainerSecurityDescriptor containerDescriptor = ContainerSecurityConfig.getConfig().getSecurityDescriptor();
			   this.defaultSubject = containerDescriptor.getSubject();
			}
			catch (Exception e)
			{
				this.logger.error("Unable to load container credentials, some operations could be not available", e);
			}
		 }
		 
		 return this.defaultSubject;
			
			
	 }
	 

		
		/* (non-Javadoc)
		 * @see org.gcube.common.core.security.context.impl.SecurityContext#getDefaultSecurityManager()
		 */
		@Override
		public GCUBESecurityManager getDefaultSecurityManager () throws Exception
		{
			logger.debug("Generate default security manager");
			logger.debug("Server mode: generating a Service security manager");
			Class<? extends GCUBESecurityManager>  securityManagerClass = GCUBEServiceSecurityManager.class;
			
			return GHNContext.getImplementation(securityManagerClass);
		}
	 
//     /**
//	     * Service security manager generator class
//	     * @param serviceContext the security context
//	     * @param securityManagerJNDIName the JNDI name of the class to be initialised
//	     * @param securityManagerClass the Class (in implementation.properties) to be initialised 
//	     * @return
//	     * @throws Exception
//	     */
//	    @SuppressWarnings ({ "unchecked"})
//	    private GCUBEServiceSecurityController generateServiceSecurityControlManager (GCUBEServiceContext serviceContext,String securityControlManagerJNDIName, Class<? extends GCUBEServiceSecurityController> securityControlManagerClass, GCUBEServiceSecurityManager securityManager) throws Exception
//	    {
//	    	logger.info("Generating security control manager "+securityControlManagerClass.getCanonicalName());
//	    	GCUBEServiceSecurityController securityControlManager = null;
//		    //configure security manager
//		    String securityControlManagerClassName = (String) serviceContext.getProperty(securityControlManagerJNDIName);
//	    	if (securityControlManagerClassName!=null) {
//	    		//get class
//	    		Class<GCUBEServiceSecurityController> securityManagerConcreteClass = (Class<GCUBEServiceSecurityController>) Class.forName(securityControlManagerClassName);//placate compiler
//	    		if (!GCUBEServiceSecurityController.class.isAssignableFrom(securityManagerConcreteClass)) //but do run-time check
//	    			throw new Exception(securityControlManagerClassName+" does not implement "+GCUBEServiceSecurityController.class.getName());
//	    		//invoke constructor reflectively
//	    		securityControlManager=securityManagerConcreteClass.newInstance();
//	    	}
//	    	else securityControlManager=GHNContext.getImplementation(securityControlManagerClass);
//	    	securityControlManager.initialise(serviceContext,securityManager);
//	    	logger.info("managing security control with a "+securityControlManager.getClass().getSimpleName());
//	    	return securityControlManager;
//	    }
//	    
//	    /**
//	     * 
//	     * Generates an authentication controller for the {@link GCUBEServiceContext} in input
//	     * 
//	     * @param serviceContext
//	     * @param securityManager
//	     * @return the authentication controller
//	     * @throws Exception
//	     */
//	    GCUBEServiceAuthenticationController generateServiceAuthenticationController (GCUBEServiceContext serviceContext, GCUBEServiceSecurityManager securityManager) throws Exception
//	    {
//	    	logger.debug("Generating service authentication controller");
//	    	return (GCUBEServiceAuthenticationController) generateServiceSecurityControlManager (serviceContext,AUTHENTICATION_MANAGER_JNDI_NAME,GCUBEServiceAuthenticationController.class,securityManager);
//	    }
//
//	    /**
//	     * 
//	     * Generates an authorization controller for the {@link GCUBEServiceContext} in input
//	     * 
//	     * @param serviceContext
//	     * @param securityManager
//	     * @return the authorization controller
//	     * @throws Exception
//	     */
//	    GCUBEServiceAuthorizationController generateServiceAuthorizationController (GCUBEServiceContext serviceContext, GCUBEServiceSecurityManager securityManager) throws Exception
//	    {
//	    	logger.debug("Generating service authorization controller");
//	    	return (GCUBEServiceAuthorizationController) generateServiceSecurityControlManager (serviceContext,AUTHORISATION_MANAGER_JNDI_NAME,GCUBEServiceAuthorizationController.class,securityManager);
//	    }

}

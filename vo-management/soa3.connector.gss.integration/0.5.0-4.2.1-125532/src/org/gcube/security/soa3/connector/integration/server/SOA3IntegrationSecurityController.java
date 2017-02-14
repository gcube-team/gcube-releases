package org.gcube.security.soa3.connector.integration.server;

import java.security.Provider;
import java.security.Security;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.xml.rpc.handler.MessageContext;

import org.gcube.common.core.contexts.GCUBEServiceContext;
import org.gcube.common.core.contexts.GHNContext;
import org.gcube.common.core.faults.GCUBEException;
import org.gcube.common.core.faults.GCUBEUnrecoverableException;
import org.gcube.common.core.security.GCUBEServiceAuthorizationController;
import org.gcube.common.core.security.GCUBEServiceSecurityController;
import org.gcube.common.core.security.GCUBEServiceSecurityManager;
import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.security.soa3.configuration.ConfigurationManagerFactory;
import org.gcube.security.soa3.configuration.GSSIntegrationInit;
import org.gcube.security.soa3.connector.GCUBESecurityController;
import org.gcube.security.soa3.connector.impl.SOA3SecurityController;
import org.gcube.security.soa3.connector.integration.utils.GSSIntegrationUtils;
import org.gcube.security.soa3.connector.integration.utils.Utils;
import org.globus.wsrf.impl.security.authentication.Constants;


/**
 * 
 * Implementation of {@link GCUBEServiceAuthorizationController} to integrate the old security management system of gCube with the new one. 
 * It will become deprecated soon
 * 
 * @author Ciro Formisano (ENG)
 *
 */
public class SOA3IntegrationSecurityController implements GCUBEServiceAuthorizationController 
{

	private GCUBELog log;
	private GCUBESecurityController controller;
	private boolean isSecurityEnabled = false;
	private String serviceString;
	
	/**
	 * 
	 */
	public SOA3IntegrationSecurityController() 
	{
		this.log = new GCUBELog(this);
		this.controller = new SOA3SecurityController();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void initialise(GCUBEServiceContext ctxt, GCUBEServiceSecurityManager securityManager) throws Exception 
	{
		log.debug("Initializing...");
		GSSIntegrationInit.init();
		String serviceName = ctxt.getName();
		String serviceClass = ctxt.getServiceClass();
		log.debug("Service name "+serviceName);
		log.debug("Service class "+serviceClass);
		Map<String, String> properties = new HashMap<String, String> ();
		properties.put(GCUBESecurityController.SERVICE_NAME, serviceName);
		
		if (!ConfigurationManagerFactory.getConfigurationManager().servicePropertiesSet(serviceName)) GSSIntegrationUtils.setServiceProperties(ctxt,serviceName);

		log.debug("Initializing the controller");
		this.isSecurityEnabled = ctxt.isSecurityEnabled();
		this.controller.init(properties);
		this.serviceString = serviceClass+":"+serviceName;
		this.log.debug("Service string "+serviceString);
		log.debug("Init completed");
		
		if (this.log.isDebugEnabled())
		{
			Provider[] provs = Security.getProviders();
			
			for (Provider p : provs)
			{
				this.log.debug("name "+p.getName());
				this.log.debug("version "+p.getVersion());
				this.log.debug(p.getClass());
				Set<Provider.Service> services = p.getServices();
				
				if (services!=null && !services.isEmpty())
				{
					this.log.debug("Services:");
					
					for (Provider.Service s : services)
					{
						this.log.debug(s.getAlgorithm());
					}
					
					this.log.debug("********************");
					
				}
			}
						
		}		

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isSecurityEnabled() 
	{
		return isSecurityEnabled;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@SuppressWarnings("unchecked")
	public void authoriseCall(Map<String, Object> parameters) throws GCUBEException 
	{
		log.debug("Checking the privileges...");
		log.debug("Loading GSS parameters...");
		Map<String, String> headers = (Map<String, String>) parameters.get(GCUBEServiceSecurityController.HEADERS);
		String securityHeader = headers.get(Utils.BINARY_SECURITY_TOKEN_LABEL);		
		MessageContext messageContext = (MessageContext) parameters.get(GCUBEServiceSecurityController.MESSAGE_CONTEXT);
		log.debug("Storing parameters...");
		Map<String, Object> internalParameters = new HashMap<String, Object>();
		internalParameters.put(Utils.BINARY_SECURITY_TOKEN_LABEL, securityHeader);
		internalParameters.put(GCUBESecurityController.PEER_SUBJECT, messageContext.getProperty(Constants.PEER_SUBJECT));
		internalParameters.put(GCUBESecurityController.SERVICE_STRING, this.serviceString);
		internalParameters.put(GCUBESecurityController.SERVICE_INSTANCE, GHNContext.getContext().getHostname());
		log.debug("Calling security controller");
		
		if (!this.controller.checkAccess(internalParameters))
		{
			log.debug("Access not granted");
			throw new GCUBEUnrecoverableException("Security exception: the requirer is not authorized to perform the selected operation");
		}
		log.debug("Access granted");
		log.debug("Loading security token...");
		Object token = internalParameters.get(Utils.SECURITY_TOKEN);
		
		if (token != null)
		{
			messageContext.setProperty(Utils.SECURITY_TOKEN, token);
			log.debug("Token added to message context");
			
		}
		else log.debug("Token not present");
	}
//	
//	/**
//	 * 
//	 * @param ctx
//	 * @return
//	 */
//	private String getOperationName (MessageContext ctx)
//	{
//		String response = null;
//		
//		try
//		{
//			java.net.URL resource =  AuthUtil .getEndpointAddressURL(ctx);
//			
//			if (resource != null)
//			{
//				String servicePath = resource.getPath();
//				response = servicePath;
//			}
//			
//		}
//		catch (Exception e)
//		{
//			log.error("Unable to generate the method name",e);
//		}
//
//		log.debug("Resource being accessed is " + response);
//		return response;
//	}

}

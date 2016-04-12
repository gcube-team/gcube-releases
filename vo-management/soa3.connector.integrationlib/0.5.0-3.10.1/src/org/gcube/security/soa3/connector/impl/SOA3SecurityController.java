package org.gcube.security.soa3.connector.impl;

import java.security.Principal;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.security.auth.Subject;
import javax.ws.rs.core.MediaType;

import org.bouncycastle.util.encoders.Base64;
import org.gcube.security.soa3.cache.SOA3EhcacheWrapper;
import org.gcube.security.soa3.configuration.ConfigurationManagerFactory;
import org.gcube.security.soa3.connector.GCUBESecurityController;
import org.gcube.security.soa3.connector.engine.RestManager;
import org.gcube.security.soa3.connector.integration.utils.Utils;
import org.gcube.soa3.connector.common.security.CredentialManager;
import org.gcube.soa3.connector.common.security.impl.TicketCredentials;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

/**
 * 
 * Implementation of the security controller for using the SOA3 Security Framework
 * 
 * @author Ciro Formisano (ENG)
 *
 */
public class SOA3SecurityController implements GCUBESecurityController
{

	private Logger log;
	private static final String 	DN = "DN";
	private final String AUTHORIZATION_HEADER = "Authorization";
	private final String SERVICE_STRING_HEADER = "Servicestring";
	private final String SERVICE_INSTANCE_HEADER = "Serviceinstance";
	private String serviceName;
	private String defaultSoa3Endpoint;
	private boolean credentialPropagationPolicy;
	private boolean securityEnabled;
	
	/**
	 * 
	 */
	public SOA3SecurityController ()
	{
		this.log = LoggerFactory.getLogger(this.getClass());
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void init(Map<String, String> parameters) 
	{
		this.serviceName = parameters.get(SERVICE_NAME);
		log.debug("Initializing security manager for service "+serviceName);
		this.defaultSoa3Endpoint = ConfigurationManagerFactory.getConfigurationManager().getServerUrl(serviceName);
		this.credentialPropagationPolicy = ConfigurationManagerFactory.getConfigurationManager().getCredentialPropagationPolicy(serviceName);
		this.securityEnabled = ConfigurationManagerFactory.getConfigurationManager().isSecurityEnabled(serviceName);
	}


	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean checkAccess(Map<String, Object> parameters) 
	{
		log.debug("Checking access");
		
		if (!this.securityEnabled)
		{
			log.debug("Security disabled");
			return true;
		}
		else
		{
			log.debug("Security enabled");
			return applySecurityPolicies(parameters);
		}
	}
	
	/**
	 * 
	 * @param parameters
	 * @return 
	 */
	private boolean applySecurityPolicies (Map<String, Object> parameters)
	{
		log.debug("Checking the acces rights");
//		Map<String, String> headers = (Map<String, String>) parameters.get(GCUBEServiceSecurityController.HEADERS);
//		String securityHeader = headers.get(Utils.BINARY_SECURITY_TOKEN_LABEL);		
//		MessageContext messageContext = (MessageContext) parameters.get(GCUBEServiceSecurityController.MESSAGE_CONTEXT);
		String ticket = checkAccessPrivileges(parameters);
		log.debug("Response = "+ticket);
		setCredentials(ticket, parameters);
		return ticket != null;
	}
	
	/**
	 * 
	 * @param credentials
	 * @param messageContext
	 */
	private void setCredentials (String ticket,Map<String, Object> parameters)
	{
		log.debug("Setting credentials in the messageContext");
		
		if (ticket != null && this.credentialPropagationPolicy)
		{
			log.debug("Setting...");
			
			try 
			{
				log.debug("Adding the credentials to the security manager");
				TicketCredentials credentials = new TicketCredentials(ticket);
				credentials.prepareCredentials();
				CredentialManager.instance.set(credentials);
				log.debug("Generating security header");
				Element element = Utils.generateBinaryTokenElement(credentials.getAuthenticationType(), ticket);
				log.debug("Security Header generated");
				parameters.put(Utils.SECURITY_TOKEN, element);
			} 
			catch (Exception e) 
			{
				log.debug("Unable to generate the security header",e);
			}
		}
		else log.debug("Propagation not set");
	}
	
	/**
	 * 
	 * @param messageContext
	 * @param securityHeader
	 * @return the ticket
	 */
	private String checkAccessPrivileges (Map<String, Object> parameters)
	{
		this.log.debug("Get Credentials bean");
		String ticket = null;
		String securityHeader = (String) parameters.get(Utils.BINARY_SECURITY_TOKEN_LABEL);
		String serviceString = (String) parameters.get(SERVICE_STRING);
		String instance = (String) parameters.get(SERVICE_INSTANCE);
		this.log.debug("Security Header "+securityHeader);
		this.log.debug("Service string "+serviceString);
		this.log.debug("Service instance "+instance);
		
		if (securityHeader != null)
		{
			log.debug("Security Header not null");
			
			try 
			{
				String [] secHeaderElements = securityHeader.split(" ");
				String type = secHeaderElements [0];
				String value = secHeaderElements [1];
				log.debug("Type = "+type);
				log.debug("id = "+value);
				ticket = performAuthentication(type, value,serviceString,instance);
				
			} catch (Exception e)
			{
				log.error("Invalid auth header, triyng to find DN");
				ticket = performDnBasedAuthentication((Subject)parameters.get(PEER_SUBJECT),serviceString,instance);
				log.debug("ticket = "+ticket);
			}
			
//			messageContext.removeProperty(SECURITY_TOKEN);
//			type = securityHeader.getAttribute(SoapMessageManager.ENCODING_TYPE_LABEL);
//			String value = securityHeader.getAttribute(SoapMessageManager.ID_LABEL);

		}
		else 
		{
			log.debug("Security Header null, trying to find DN");
			ticket = performDnBasedAuthentication((Subject)parameters.get(PEER_SUBJECT),serviceString,instance);
			log.debug("Ticket = "+ticket);
			
		}
		
		this.log.debug("Operation completed");
		return ticket;
	}
	
	
	/**
	 * 
	 * @param messageContext
	 * @return
	 */
	private String performDnBasedAuthentication (Subject subject, String serviceString, String operationName)
	{
		log.debug("No security header found");
		log.debug("Looking for the Distinguished Name");
		String response = null;	
		
		if (subject == null)
		{
			log.error("No Distinguished name found");
		}
		else
		{
			log.debug("External subject "+subject);
			Set<Principal> principals = subject.getPrincipals();
			
			if (principals == null || principals.isEmpty())
			{
				log.error("Unable to find subject identity");
			}
			else 
			{
				log.debug("Identities found, looking for the DNs");
				Iterator<Principal> principalIterator = principals.iterator();
				
				while (principalIterator.hasNext() && response == null)
				{
					String dn = principalIterator.next().getName();
					log.debug("Distinguished name "+dn);
					response = performAuthentication(DN, new String(Base64.encode(dn.getBytes())),serviceString,operationName);
					log.debug("Response = "+response);
					
				}

			}

		}
		
		return response;
	}
	
	/**
	 * 
	 * @param type the type of authentication parameter
	 * @param value the authentication parameter
	 * @return the ticket if the authentication is OK, null otherwise
	 */
	private String performAuthentication (String type, String value, String serviceString,String instance)
	{
		if (serviceString == null || instance == null)
		{
			log.error("Unable to find service string or operation name");
			return null;
		}
		else
		{
			log.debug("Asking the cache...");
			String cacheString = type+value+serviceString+instance;
			log.debug("Cache string "+cacheString);
			String response = SOA3EhcacheWrapper.getInstance().get(cacheString);
			
			if (response == null)
			{
				log.debug("Response null, asking SOA3");
				response = askSoa3(type, value,serviceString,instance);
				
				if (response != null) 
				{
					log.debug("Response found populating the cache");
					SOA3EhcacheWrapper.getInstance().put(cacheString, response);
					log.debug("Cache populated");
				}
				else log.debug("No response from SOA3");
			}
			else
			{
				log.debug("Response found in the cache");
			}
			
			log.debug("Response = "+response);
			return response;
		}
		

	}
	
	/**
	 * 
	 * @param type
	 * @param value
	 * @return
	 */
	private String askSoa3 (String type, String value, String serviceString, String instance)
	{
		log.debug("Sending authentication message to SOA3");
		Map<String, String> headers = new HashMap<String, String> ();
		headers.put(AUTHORIZATION_HEADER, type+ " "+value);
		headers.put(SERVICE_STRING_HEADER, serviceString);
		headers.put(SERVICE_INSTANCE_HEADER, instance);
		String response = RestManager.getInstance(getSoa3Endpoint()).sendMessage(Utils.SOA3_ACCESS_SERVICE, headers, null, MediaType.APPLICATION_JSON_TYPE, MediaType.APPLICATION_JSON_TYPE);
		log.debug("Authentication response = "+response);
		return response;

	}

	@Override
	public boolean isSecurityEnabled() 
	{
		return securityEnabled;
	}

	@Override
	public void setSecurityEnabled(boolean securityEnabled) 
	{
		this.securityEnabled = securityEnabled;	
	}
	


	private String getSoa3Endpoint ()
	{
		log.debug("Loading soa3 endpoint for the current operation and service");
		String endpoint = ConfigurationManagerFactory.getConfigurationManager().getServerUrl(this.serviceName);
		
		if (endpoint == null) endpoint = defaultSoa3Endpoint;
		
		log.debug("Actual endpoint "+endpoint);
		return endpoint;
	}



}

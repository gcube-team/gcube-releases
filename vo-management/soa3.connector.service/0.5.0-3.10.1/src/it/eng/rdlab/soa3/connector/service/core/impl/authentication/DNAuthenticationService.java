package it.eng.rdlab.soa3.connector.service.core.impl.authentication;

import it.eng.rdlab.soa3.connector.beans.UserBean;
import it.eng.rdlab.soa3.connector.service.beans.AccessControlBean;
import it.eng.rdlab.soa3.connector.service.configuration.Configuration;
import it.eng.rdlab.soa3.connector.service.core.AuthenticationInternalService;
import it.eng.rdlab.soa3.connector.service.core.IdentityControlManager;
import it.eng.rdlab.soa3.connector.service.core.TicketControlManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gcube.soa3.connector.Authenticate;
import org.gcube.soa3.connector.impl.ServiceDNAuthentication;
import org.gcube.soa3.connector.impl.UserDNAuthentication;

/**
 * 
 * Specification for dn based authentication of {@link AuthenticationInternalService}
 * 
 * @author Ciro Formisano (ENG)
 *
 */
public class DNAuthenticationService  extends AuthenticationInternalServiceImpl implements AuthenticationInternalService 
{
	private Log log;
	private String soa3Endpoint;
	private String defaultOrganization;
	private String scope;
	
	public DNAuthenticationService(String soa3Endpoint,String defaultOrganization, String scope) 
	{
		super (IdentityControlManager.getInstance());
		this.log = LogFactory.getLog(this.getClass());
		this.soa3Endpoint = soa3Endpoint;
		this.defaultOrganization = defaultOrganization;
		this.scope = scope;
		
	}
	
	/*
	 * (non-Javadoc)
	 * @see it.eng.rdlab.soa3.connector.service.core.impl.AuthenticationInternalServiceImpl#askToSOA3(java.lang.String, long)
	 */
	@Override
	protected AccessControlBean askToSOA3(String id, long currentTime) 
	{
		log.debug("entry not found, calling SOA3");
		log.debug("Looking if the DN is associated to a service");
		AccessControlBean bean = getAccessControlBean(id);

		if (bean != null)
		{
			log.debug("Authentication OK");
			bean.setSessionStart(currentTime);
			bean.setSessionEnd(currentTime+Configuration.getInstance().getAuthValidity());
			IdentityControlManager.getInstance().setAccessGrantEntry(id, bean);
			TicketControlManager.getInstance().setAccessGrantEntry(bean.getTicket(), bean);
			log.debug("Cache updated");
		}
		else
		{
			log.debug("Authentication failed: inserting new value in the black list");
			IdentityControlManager.getInstance().setAccessDeniedEntry(id, currentTime+Configuration.getInstance().getAuthValidity());
			log.debug("Black list updated");
		}
		
		return bean;

	}
	
	/**
	 * 
	 * @param id
	 * @return
	 */
	private AccessControlBean getAccessControlBean (String id)
	{
		log.debug("Loading access bean for "+id);
		AccessControlBean bean = performAuthentication(new ServiceDNAuthentication(this.scope),id);
		
		if (bean == null) 
		{
			log.debug("DN not associated to a service: looking if the DN is associated to an user");
			bean = performAuthentication(new UserDNAuthentication(this.soa3Endpoint),id);
		}
		
		
		log.debug("Operation completed");
		return bean;
	}
	
	/**
	 * 
	 * @param authenticationManager
	 * @param id
	 * @return
	 */
	private AccessControlBean performAuthentication (Authenticate authenticationManager, String id)
	{
		UserBean response = authenticationManager.authenticate(id,defaultOrganization);
		AccessControlBean bean = null;
		
		if (response != null) 
		{
			log.debug("Login ok, generating the access control bean");
			bean = new AccessControlBean();
			bean.setUsername(response.getUserName());

			
			if (response.getRoles() != null && response.getRoles().size()>0) 
			{
				log.debug("Loading roles from response");
				bean.setRolesLoaded(true);
				bean.getRoles().addAll(response.getRoles());
				log.debug("Roles loaded");
			}
			
			String ticket = generateTicket ();
			log.debug("Ticket = "+ticket);
			bean.setTicket(ticket);
			log.debug("Access control bean added in the cache");

		}
		else log.debug("DN not associated to any record");
		
		return bean;

	}
	
}
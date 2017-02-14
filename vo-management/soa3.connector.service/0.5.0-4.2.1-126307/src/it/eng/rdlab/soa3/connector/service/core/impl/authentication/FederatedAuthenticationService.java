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
import org.gcube.soa3.connector.impl.FederatedAuthentication;

/**
 * 
 * Specification for federated authentication of {@link AuthenticationInternalService}
 * 
 * @author Ciro Formisano (ENG)
 *
 */
public class FederatedAuthenticationService extends AuthenticationInternalServiceImpl implements AuthenticationInternalService 
{
	private Log log;
	private String soa3Endpoint;

	public FederatedAuthenticationService(String soa3Endpoint) 
	{
		super (IdentityControlManager.getInstance());
		this.soa3Endpoint = soa3Endpoint;
		this.log = LogFactory.getLog(this.getClass());
	}
	
	/*
	 * (non-Javadoc)
	 * @see it.eng.rdlab.soa3.connector.service.core.impl.AuthenticationInternalServiceImpl#askToSOA3(java.lang.String, long)
	 */
	@Override
	protected AccessControlBean askToSOA3(String id, long currentTime) 
	{
		log.debug("entry not found, calling SOA3");
		Authenticate soa3Authentication = new FederatedAuthentication(this.soa3Endpoint);
		UserBean response = soa3Authentication.authenticate(id,null);
		AccessControlBean bean = null;
		
		if (response != null) 
		{
			log.debug("Login ok, generating the access control bean");
			bean = new AccessControlBean();
			bean.setUsername(response.getUserName());
			bean.setSessionStart(currentTime);
			bean.setSessionEnd(currentTime+Configuration.getInstance().getAuthValidity());
			log.debug("Loading federated roles...");
			bean.setRolesLoaded(true);
			bean.getRoles().addAll(response.getRoles());
			log.debug("Roles loaded");
			String ticket = generateTicket();
			log.debug("Ticket = "+ticket);
			bean.setTicket(ticket);
			IdentityControlManager.getInstance().setAccessGrantEntry(id, bean);
			TicketControlManager.getInstance().setAccessGrantEntry(ticket, bean);
			log.debug("Access control bean added in the cache");
		}
		else
		{
			log.debug("Authentication failed: inserting new value in the black list");
			IdentityControlManager.getInstance().setAccessDeniedEntry(id, currentTime+Configuration.getInstance().getAuthValidity());
			log.debug("Black list updated");
		}
		
		return bean;
	}

}

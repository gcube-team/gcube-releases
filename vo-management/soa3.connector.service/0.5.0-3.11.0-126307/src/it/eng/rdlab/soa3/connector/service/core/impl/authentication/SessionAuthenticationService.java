package it.eng.rdlab.soa3.connector.service.core.impl.authentication;

import it.eng.rdlab.soa3.connector.beans.SessionBean;
import it.eng.rdlab.soa3.connector.service.beans.AccessControlBean;
import it.eng.rdlab.soa3.connector.service.configuration.Configuration;
import it.eng.rdlab.soa3.connector.service.core.AuthenticationInternalService;
import it.eng.rdlab.soa3.connector.service.core.TicketControlManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gcube.soa3.connector.impl.SessionAuthentication;

import com.sun.jersey.core.util.Base64;

/**
 * 
 * Session based authentication
 * 
 * @author Ciro Formisano (ENG)
 *
 */
public class SessionAuthenticationService extends AuthenticationInternalServiceImpl implements AuthenticationInternalService 
{
	private Log log;
	private boolean local;
	
	public SessionAuthenticationService(boolean local) 
	{
		super (TicketControlManager.getInstance());
		this.log = LogFactory.getLog(this.getClass());
		this.local = local;
	}
	
	public SessionAuthenticationService() 
	{
		this (false);
	}
	
	/*
	 * (non-Javadoc)
	 * @see it.eng.rdlab.soa3.connector.service.core.impl.AuthenticationInternalServiceImpl#askToSOA3(java.lang.String, long)
	 */
	@Override
	protected AccessControlBean askToSOA3(String id, long currentTime) 
	{
		if (!this.local)
		{
			
			
			log.debug("Ticket not found in the internal cache");
			log.debug("Parsing the ticket...");
			
			try
			{
				String [] decodedTicket = Base64.base64Decode(id).split("::");
				String ticketServiceName = decodedTicket [0].trim();
				String serviceName = Configuration.getInstance().getServiceName();
				log.debug("Requested soa3 instance "+ticketServiceName);
				log.debug("Current service instance "+serviceName);
				
				if (serviceName.equalsIgnoreCase(ticketServiceName))
				{
					log.error("Ticket is for this service but is not found in the cache: authentication error");
					log.debug("Authentication failed: inserting new value in the black list");
					TicketControlManager.getInstance().setAccessDeniedEntry(id, currentTime+Configuration.getInstance().getAuthValidity());
					log.debug("Black list updated");
					return null;
				}
				else
				{
					String serviceUrl = Configuration.getInstance().getService(ticketServiceName);
					
					if (serviceUrl == null)
					{
						log.error("Service url not found");
						log.debug("Authentication failed: inserting new value in the black list");
						TicketControlManager.getInstance().setAccessDeniedEntry(id, currentTime+Configuration.getInstance().getAuthValidity());
						log.debug("Black list updated");
						return null;
					}
					else
					{
						log.debug("Calling "+serviceUrl);
						SessionAuthentication soa3Authentication = new SessionAuthentication(serviceUrl);
						SessionBean response = soa3Authentication.getRemoteBean(id);
						
						if (response == null)
						{
							log.error("Ticket not found");
							log.debug("Authentication failed: inserting new value in the black list");
							TicketControlManager.getInstance().setAccessDeniedEntry(id, currentTime+Configuration.getInstance().getAuthValidity());
							log.debug("Black list updated");
							return null;
						}
						else
						{
							log.debug("Session expires on "+response);
							AccessControlBean bean = new AccessControlBean();
							bean.setTicket(id);
							bean.setSessionStart(Long.parseLong(response.getSessionStart()));
							bean.setSessionEnd(Long.parseLong(response.getSessionEnd()));
							bean.setUsername(response.getUserId());
							
							if (response.getRoles().size()>0) 
							{
								log.debug("Loading roles from response");
								bean.setRolesLoaded(true);
								bean.getRoles().addAll(response.getRoles());
								log.debug("Roles loaded");
							}

							TicketControlManager.getInstance().setAccessGrantEntry(id, bean);
							return bean;
						}
						
					}
				}
				
				
			} catch (RuntimeException e)
			{
				log.error("Unable to determine the ticket",e);
				log.debug("Authentication failed: inserting new value in the black list");
				TicketControlManager.getInstance().setAccessDeniedEntry(id, currentTime+Configuration.getInstance().getAuthValidity());
				log.debug("Black list updated");
				return null;
			}
		
		}
		else
		{
			log.debug("external request disabled");
			return null;
		}
		
		
		
	}
	
	public SessionBean getSessionBean (String id)
	{
		log.debug("Getting the end of the session");
		AccessControlBean accessBean = getAccessBean(id);
		
		if (accessBean == null)
		{
			log.debug("No session");
			return null;
		}
		else
		{
			SessionBean sessionBean = new SessionBean();
			sessionBean.setUserId(accessBean.getUsername());
			sessionBean.setRoles(accessBean.getRoles());
			sessionBean.setSessionStart(String.valueOf(accessBean.getSessionStart()));
			sessionBean.setSessionEnd(String.valueOf(accessBean.getSessionEnd()));
			log.debug("Session = "+sessionBean);
			return sessionBean;
		}
	}
	
//	public String getSessionEnd (String id)
//	{
//		log.debug("Getting the end of the session");
//		AccessControlBean accessBean = getAccessBean(id);
//		
//		if (accessBean == null)
//		{
//			log.debug("No session");
//			return null;
//		}
//		else
//		{
//			log.debug("Session end = "+accessBean.getSessionEnd());
//			return String.valueOf(accessBean.getSessionEnd());
//		}
//	}
	
}

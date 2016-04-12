package it.eng.rdlab.soa3.connector.service.core.impl.authentication;

import it.eng.rdlab.soa3.connector.service.beans.AccessControlBean;
import it.eng.rdlab.soa3.connector.service.configuration.Configuration;
import it.eng.rdlab.soa3.connector.service.core.AccessCache;
import it.eng.rdlab.soa3.connector.service.core.AuthenticationInternalService;

import java.util.Date;
import java.util.UUID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sun.jersey.core.util.Base64;


/**
 * 
 * Abstract implementation of the authentication internal service
 * 
 * @author Ciro Formisano (ENG)
 *
 */
public abstract class AuthenticationInternalServiceImpl implements AuthenticationInternalService
{
	private static Log log = LogFactory.getLog(AuthenticationInternalServiceImpl.class);
	private AccessCache accessCache;

	
	public AuthenticationInternalServiceImpl (AccessCache accessCache)
	{
		this.accessCache = accessCache;
	}
	
	/**
	 * {@inheritDoc} 
	 */
	@Override
	public String authenticate(String id) 
	{
		log.debug("Authenticating...");
		AccessControlBean accessBean = getAccessBean(id);
		
		if (accessBean == null)
		{
			log.debug("Access denied");
			return null;
		}
		else
		{
			String ticket = accessBean.getTicket();
			log.debug("Ticket = "+ticket);
			return ticket;
		}

	}
	
	/**
	 * 
	 * @param id
	 * @return
	 */
	protected AccessControlBean getAccessBean (String id)
	{
		log.debug("Initialization");
		long currentTime = new Date().getTime();
		AccessControlBean responseBean = accessCache.getAccessGrantEntry(id);
		log.debug("Checking the cache");
		AccessControlBean response = null;
		
		if (responseBean != null)
		{
			if (!checkLocalCache(id, responseBean, currentTime))
			{
				log.debug("Invalid authentication parameter found");
				response = null;
			}
			else response = responseBean;
		}
		
		if (response ==null && !isInBlackList(id, currentTime)) response = askToSOA3(id,currentTime);
		
		log.debug("Operation completed");
		return response;
	}
	
	/**
	 * 
	 * @param id
	 * @param accessControlBean
	 * @param currentTime
	 * @return
	 */
	private boolean checkLocalCache (String id,AccessControlBean accessControlBean,long currentTime)
	{
		log.debug("Username = "+accessControlBean.getUsername());
		long sessionEnd = accessControlBean.getSessionEnd();
		log.debug("Session end "+sessionEnd);
		boolean response = false;
		
		if (currentTime > sessionEnd) 
		{
			log.debug("Login expired");
			this.accessCache.removeAccessGrantEntry(id);
		}
		else
		{
			log.debug("Entry found in the cache");
			response = true;
		}
		
		return response;
	}
	
	/**
	 * 
	 * @param id
	 * @param currentTime
	 * @return
	 */
	private boolean isInBlackList (String id, long currentTime)
	{
		log.debug("Checking access denied cache...");
		long blackListTime = this.accessCache.getAccessDeniedEntry(id);
		
		if (blackListTime > -1 && currentTime <= blackListTime) 
		{
			log.debug("Access attempt in the blacklist");
			return true;
		}
		else if (blackListTime != -1) this.accessCache.removeAccessDeniedEntry(id);
		
		return false;
	}
	
	/**
	 * 
	 * @return
	 */
	protected static String generateTicket ()
	{
		String serviceName = Configuration.getInstance().getServiceName();
		log.debug("Service name = "+serviceName);
		StringBuilder builder = new StringBuilder(serviceName).append("::").append(UUID.randomUUID().toString());
		String response = new String (Base64.encode(builder.toString().getBytes()));
		log.debug("Complete ticket = "+response);
		return response;
	}
	
	/**
	 * 
	 * Sends the request to SOA3
	 * 
	 * @param id
	 * @param currentTime
	 * @return
	 */
	protected abstract AccessControlBean askToSOA3 (String id, long currentTime);
	
}

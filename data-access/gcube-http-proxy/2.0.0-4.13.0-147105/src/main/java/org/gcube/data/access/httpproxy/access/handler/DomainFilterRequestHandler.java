package org.gcube.data.access.httpproxy.access.handler;

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.annotation.XmlRootElement;

import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.data.access.httpproxy.access.URLCache;
import org.gcube.data.access.httpproxy.utils.Properties;
import org.gcube.data.access.httpproxy.utils.Properties.BooleanPropertyType;
import org.gcube.data.access.httpproxy.utils.Utils;
import org.gcube.smartgears.handlers.application.ApplicationHandler;
import org.gcube.smartgears.handlers.application.RequestEvent;
import org.gcube.smartgears.handlers.application.RequestHandler;
import org.gcube.smartgears.handlers.application.request.RequestError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@XmlRootElement (name= DomainFilterRequestHandler.REQUEST_HANDLER_NAME)
public class DomainFilterRequestHandler extends RequestHandler {

	public static final String REQUEST_HANDLER_NAME = "domain-filter";
	private boolean enabled,
					defaultForward;
	private Logger logger;
	
	public DomainFilterRequestHandler() 
	{
		this.logger = LoggerFactory.getLogger(this.getClass());
		this.enabled = Properties.getInstance().getProperty(BooleanPropertyType.ENABLED);
		this.defaultForward = Properties.getInstance().getProperty(BooleanPropertyType.DEFAULT_FORWARD);
	}
	
	
	@Override
	public String getName() 
	{
		return REQUEST_HANDLER_NAME;
	}

	@Override
	public void handleRequest(RequestEvent e) {
		
		this.logger.debug("Handling request");
		
		if (this.enabled)
		{
			HttpServletRequest request = e.request();
			String path = ((HttpServletRequest) request).getPathInfo();

			String address = Utils.getAddress(path, request, false);

			if (address == null)
			{
				logger.debug("Address not found or invalid");
			}
			else
			{
				logger.debug("Loading domain list");
				String scope = ScopeProvider.instance.get();
				List<String> domains = URLCache.getInstance().getDomainList(scope);
				
				if (domains.size()==0 && this.defaultForward)
				{
					this.logger.debug("No domains found: forward");	
				}

				else
				{
					boolean found = false;
					Iterator<String> domainsIterator = domains.iterator();
					address = address.toLowerCase();
					
					while (domainsIterator.hasNext() && ! found)
					{
						String domain = domainsIterator.next().trim().toLowerCase();
						
						if (!domain.startsWith("http") ) domain = "http://"+domain;

						logger.debug("Domain "+domain);
						
						
						if (address.startsWith(domain)) found = true;
						
					}
					
					if (found)
					{
						logger.debug("Domain accepted");
					}
					else
					{
						logger.debug("Domain refused");
						RequestError.invalid_request_error.fire("Target address not present among allowed domains");
					}
					
				}
			}
			
		}
		else 
		{
			this.logger.debug("Filter not enabled");

		}
		
	}

	@Override
	public String toString() {
		return getName();
	}
	
	
	public static void main(String[] args) throws IOException {
		
	    String PREFIX = "META-INF/services/";
		
		ClassLoader cl = Thread.currentThread().getContextClassLoader();
        
		String fullName = PREFIX + ApplicationHandler.class.getName();
        
        Enumeration<URL>  urls  = cl.getResources(fullName);
		
		System.out.println(urls.nextElement());
	}

}

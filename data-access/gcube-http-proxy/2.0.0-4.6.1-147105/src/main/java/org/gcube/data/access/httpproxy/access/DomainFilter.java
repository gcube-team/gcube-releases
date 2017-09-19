package org.gcube.data.access.httpproxy.access;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.gcube.data.access.httpproxy.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DomainFilter implements Filter {

	private Logger logger;
	private final String 	ENABLED = "enabled",
							DEFAULT_FORWARD = "forward";
	
	private boolean 	enabled,
						defaultForward;
	
	public DomainFilter() {
		this.logger = LoggerFactory.getLogger(this.getClass());
		this.enabled = true;
		this.defaultForward = false;
	}
	
	@Override
	public void destroy() 
	{
		logger.debug("Filter destroyed");
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		if (this.enabled)
		{

			String path = ((HttpServletRequest) request).getPathInfo();

			String address = Utils.getAddress(path, request, false);

			if (address == null)
			{
				logger.debug("Address not found or invalid");
				chain.doFilter(request, response);
			}
			else
			{
				logger.debug("Starting is client");
				ISManager isManager = new ISManager();
				List<String> domains = isManager.getDomains();
				
				if (domains == null || domains.size()==0 && this.defaultForward)
				{
					this.logger.debug("No domains: forward");
					chain.doFilter(request, response);
				}
				else if (domains == null || domains.size()==0 && !this.defaultForward)
				{
						
					this.logger.debug("No domains: blocked");
					((HttpServletResponse) response).setStatus(403);
					response.getWriter().println("<html><body><p>Invalid domain "+address+"</p></body></html>");	
					
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
						chain.doFilter(request, response);
					}
					else
					{
						logger.debug("Domain refused");
						((HttpServletResponse) response).setStatus(403);
						response.getWriter().println("<html><body><p>Invalid domain "+address+"</p></body></html>");
					}
					
				}
			}
			
		}
		else 
		{
			this.logger.debug("Filter not enabled");
			chain.doFilter(request, response);
		}

	}

	@Override
	public void init(FilterConfig config) throws ServletException {
		String enabled = config.getInitParameter(ENABLED);
		
		if (enabled != null && enabled.equalsIgnoreCase("false")) this.enabled = false;
		
		logger.debug("Domain filter enabled "+this.enabled);
		
		String forward = config.getInitParameter(DEFAULT_FORWARD);
		
		if (forward != null && forward.equalsIgnoreCase("true")) this.defaultForward = true;
		
		logger.debug("Default forward "+this.defaultForward);
		
	}
	

	

}

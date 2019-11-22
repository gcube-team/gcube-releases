package org.gcube.data.access.connector;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Base64;
import org.gcube.data.access.connector.utils.AuthenticationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

public class GeoNetworkFilter implements Filter {
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		logger.warn("init() method");
	}

	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
			throws IOException, ServletException {
		logger.warn("doFilter() method");

		ServletRequestWrapper request = new ServletRequestWrapper((HttpServletRequest) servletRequest);
		HttpServletResponse response = (HttpServletResponse) servletResponse;

		// get credentials
		String username = (String) request.getAttribute(AuthenticationUtils.USERNAME);
		String password = (String) request.getAttribute(AuthenticationUtils.PASSWORD);

		if (StringUtils.hasText(username) && StringUtils.hasText(password)){	
			request.addHeader("Accept", "application/json");
			
			// set authorization header
			String token = username + ":" + password;
			String basic_authentication = AuthenticationUtils.BASIC + AuthenticationUtils.WHITESPACE
					+ Base64.getEncoder().encodeToString(token.getBytes());

			request.addHeader(AuthenticationUtils.AUTHORIZATION, basic_authentication);
			logger.warn("Added authorization header : " + request.getHeader(AuthenticationUtils.AUTHORIZATION));

			request.addParameter(AuthenticationUtils.USERNAME, username);
			request.addParameter(AuthenticationUtils.PASSWORD, password);
			logger.warn("Added parameters in the request : " + username + "/" + password);
		}			
		
		filterChain.doFilter(request, response);
	}

	@Override
	public void destroy() {
		logger.warn("destroy() method");
	}

}

package org.gcube.smartgears.extensions;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.gcube.smartgears.handlers.application.request.RequestException;
import org.gcube.smartgears.utils.Utils;

/**
 * A {@link Filter} that maps {@link RequestException}s onto error responses.
 *   
 * 
 * @author Fabio Simeoni
 *
 */
public class RequestExceptionBarrier implements Filter {

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException {

		try {


			chain.doFilter(request, response);
			
			
		} catch (Throwable t) {

			Utils.handleError(HttpServletRequest.class.cast(request), HttpServletResponse.class.cast(response), t);
		}
	}

	@Override
	public void destroy() {
	}

}

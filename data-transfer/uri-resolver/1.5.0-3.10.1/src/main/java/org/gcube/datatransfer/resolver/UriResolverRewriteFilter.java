/**
 *
 */
package org.gcube.datatransfer.resolver;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Nov 20, 2015
 */
public class UriResolverRewriteFilter implements Filter{

	protected static final String SMP_ID = "smp-id";
	protected static final String SERVLET_RESOLVER_BY_ID = "id";
	protected static final Logger logger = LoggerFactory.getLogger(UriResolverRewriteFilter.class);
	/* (non-Javadoc)
	 * @see javax.servlet.Filter#destroy()
	 */
	@Override
	public void destroy() {
		  logger.trace("run destroy");
	}

	/* (non-Javadoc)
	 * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse, javax.servlet.FilterChain)
	 */
	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {

		HttpServletRequest request = (HttpServletRequest) req;
		String requestURI = request.getRequestURI();
		String queryString = request.getQueryString();
		logger.debug("Request URI: " + requestURI + ", QueryString: " +queryString);
		if (queryString == null) { // IS A /XXXXX
			logger.debug("QueryString is null, is It a new SMP public uri by ID?");
			int lastSlash = requestURI.lastIndexOf("/");
			if (lastSlash + 1 == requestURI.length()) {
				logger.debug("'/' is last index, doFilter Request");
				// req.getRequestDispatcher("/").forward(req, res);
				chain.doFilter(req, res);
			}
			else {
				String toStorageID = requestURI.substring(lastSlash + 1, requestURI.length());
				// String newURI = requestURI.replace(toReplace,
				// SERVLET_RESOLVER_BY_ID+"?"+SMP_ID+"="+toReplace);
				String newURI = SERVLET_RESOLVER_BY_ID + "?" + SMP_ID + "=" + toStorageID;
				logger.debug("forward to: " + newURI);
				req.getRequestDispatcher(newURI).forward(req, res);
			}
		}
		else {
			logger.debug("is NOT a SMP public uri by ID, doFilter Request");
			chain.doFilter(req, res);
		}
	}

	/* (non-Javadoc)
	 * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
	 */
	@Override
	public void init(FilterConfig arg0) throws ServletException {
		 logger.trace("run init");
	}

}

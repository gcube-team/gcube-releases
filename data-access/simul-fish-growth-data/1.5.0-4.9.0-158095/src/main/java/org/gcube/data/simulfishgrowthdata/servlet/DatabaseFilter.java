package org.gcube.data.simulfishgrowthdata.servlet;

import java.io.IOException;
import java.util.Enumeration;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.http.util.TextUtils;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.data.simulfishgrowthdata.util.GCubeUtils;
import org.gcube.data.simulfishgrowthdata.util.HibernateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Servlet Filter implementation class DatabaseFilter
 */
public class DatabaseFilter implements Filter {
	private static final Logger logger = LoggerFactory.getLogger(DatabaseFilter.class);

	public static final String CTX_PARAM_DB_ENDPOINT_NAME = "DBEndpointName";
	public static final String CTX_PARAM_ADDITONAL_SIMILARITY_CONSTRAINT = "AdditionalSimilarityConstraint";
	static final String debugHibernateConfig = "debug.HibernateConfig";

	// this should be local but i can only get it via init()
	String dbEndpointName;
	static public String additionalSimilarityConstraint;

	/**
	 * Default constructor.
	 */
	public DatabaseFilter() {
		logger.error(String.format("DatabaseFilter ctor : test error"));
		logger.debug(String.format("DatabaseFilter ctor : test debug"));
		logger.trace(String.format("DatabaseFilter ctor : test trace"));
		System.out.println(String.format("DatabaseFilter ctor on system.out"));
	}

	/**
	 * @see Filter#destroy()
	 */
	public void destroy() {

	}

	/**
	 * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
	 */
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		if (logger.isDebugEnabled()) {
			logger.debug("in doFilter");
		}
		HttpServletRequest httpRequest = (HttpServletRequest) request;

		Enumeration headerNames = httpRequest.getHeaderNames();
		while (headerNames.hasMoreElements()) {
			String key = (String) headerNames.nextElement();
			String value = httpRequest.getHeader(key);
			if (logger.isDebugEnabled()) {
				logger.debug(String.format("Header [%s]=[%s]", key, value));
			}
		}

		String dbhost = httpRequest.getHeader("dbhost");
		if (logger.isTraceEnabled()) {
			logger.trace(String.format("dbhost [%s]", dbhost));
		}
		if (dbhost != null) {
			if (logger.isTraceEnabled()) {
				logger.trace(String.format("the user used alternate connection: custom connection"));
			}
			String dbname = httpRequest.getHeader("dbname");
			String dbuser = httpRequest.getHeader("dbuser");
			String dbpass = httpRequest.getHeader("dbpass");
			GCubeUtils.prefillDBCredentials(dbhost, dbname, dbuser, dbpass);
		} else {
			String endpoint = httpRequest.getHeader("dbendpooint");
			if (!TextUtils.isEmpty(endpoint)) {
				if (logger.isTraceEnabled()) {
					logger.trace(String.format("the user used alternate connection: custom database endpoint [%s]",
							endpoint));
				}
				dbEndpointName = endpoint;
			}
		}

		// the purpose of the "scope" header is to establish the connection
		// the scope of the call (eg for data retrieval) is determined by the
		// token
		String scope = httpRequest.getHeader("scope");
		ScopeProvider.instance.set(scope);
		if (logger.isTraceEnabled()) {
			logger.trace(String.format("configuring Hibernate"));
		}
		HibernateUtil.configGently(dbEndpointName, scope);
		if (logger.isTraceEnabled()) {
			logger.trace(String.format("configuring Hibernate - done"));
		}

		// pass the request along the filter chain
		chain.doFilter(request, response);
	}

	/**
	 * @see Filter#init(FilterConfig)
	 */
	public void init(FilterConfig fConfig) throws ServletException {
		if (logger.isTraceEnabled()) {
			logger.trace(String.format("DatabaseFilter init"));
		}
		dbEndpointName = fConfig.getInitParameter(CTX_PARAM_DB_ENDPOINT_NAME);
		if (logger.isTraceEnabled()) {
			logger.trace(String.format("DatabaseFilter [%s]=[%s]", CTX_PARAM_DB_ENDPOINT_NAME, dbEndpointName));
		}
		additionalSimilarityConstraint = fConfig.getInitParameter(CTX_PARAM_ADDITONAL_SIMILARITY_CONSTRAINT);
		if (logger.isTraceEnabled()) {
			logger.trace(String.format("DatabaseFilter [%s]=[%s]", CTX_PARAM_ADDITONAL_SIMILARITY_CONSTRAINT,
					additionalSimilarityConstraint));
		}
		String debugHibernateConfigFilename = fConfig.getInitParameter(debugHibernateConfig);
		if (debugHibernateConfigFilename != null && !debugHibernateConfigFilename.isEmpty()) {
			HibernateUtil.debugLoadFromLocalXml = debugHibernateConfigFilename;
			logger.info(String.format("Using debug hibernate config [%s]", debugHibernateConfigFilename));
		}
	}

}

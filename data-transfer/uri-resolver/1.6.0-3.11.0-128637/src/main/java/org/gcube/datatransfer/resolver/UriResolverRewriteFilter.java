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

import org.gcube.datatransfer.resolver.gis.geonetwork.GeonetworkResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * The Class UriResolverRewriteFilter.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Apr 5, 2016
 */
public class UriResolverRewriteFilter implements Filter{

	public static final String SERVLET_GEONETWORK = "/geonetwork";
	protected static final String SMP_ID = "smp-id";
	protected static final String SERVLET_RESOLVER_BY_ID = "id";
	protected static final Logger logger = LoggerFactory.getLogger(UriResolverRewriteFilter.class);
	private FilterConfig config;

	/**
	 * Gets the config.
	 *
	 * @return the config
	 */
	public FilterConfig getConfig() {

		return config;
	}

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
	public void doFilter(ServletRequest req, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		/* wrap the request in order to read the inputstream multiple times */
	    MultiReadHttpServletRequest multiReadRequest = new MultiReadHttpServletRequest((HttpServletRequest) req);
		String requestURI = multiReadRequest.getRequestURI();
		String queryString = multiReadRequest.getQueryString();
		logger.debug("Request URI: " + requestURI + ", QueryString: " +queryString+ ", Servlet path: "+multiReadRequest.getServletPath());

		//IS A REQUEST FOR GEONETWORK AUTHENTICATION? (CKAN HARVESTING?)
		if(isGeonetworkRequest(multiReadRequest.getServletPath())){
			logger.debug("is geonetwork");
			String path = multiReadRequest.getServletPath();
			String pathWithoutGN = path.substring(SERVLET_GEONETWORK.length()+1, path.length());
			logger.debug("servlet path without "+SERVLET_GEONETWORK + " is: " +pathWithoutGN);
			String[] params = pathWithoutGN.split("/");
			if(params[0]==null || params[0].isEmpty()){
				logger.error("Scope is null or empty, you must set a valid scope /geonetwork/root_vo_vre");
				throw new ServletException("Scope is null or empty, you must set a valid scope /geonetwork/root_vo_vre");
			}

			String scopeValue = getScope(params[0]);
			logger.debug("scope value is: "+scopeValue);
			String newURI = SERVLET_GEONETWORK + "?" + GeonetworkResolver.SCOPE + "=" + scopeValue;

			if(params.length>1){
				String remainPath = "";
//				newURI +="&remainPath=";
				for (int i = 1; i < params.length; i++) {
					String httpGetParam = params[i];
					if(httpGetParam!=null && !httpGetParam.isEmpty())
						remainPath+="/"+httpGetParam;
				}
				newURI +="&"+GeonetworkResolver.REMAIN_PATH+"="+remainPath;
			}

			if(queryString!=null && !queryString.isEmpty())
				newURI+="&"+queryString;

			logger.debug("forward "+newURI);
			//BODY DEBUG
			/*
			String readBody = IOUtils.toString(multiReadRequest.getReader());
			logger.debug("Read body request: "+readBody);
			*/
			multiReadRequest.getRequestDispatcher(newURI).forward(multiReadRequest, response);
		}else{
			//IS WORKSPACE REQUEST?
			if (queryString == null) { // IS A /XXXXX
				logger.debug("QueryString is null, is It a new SMP public uri by ID?");
				int lastSlash = requestURI.lastIndexOf("/");
				if (lastSlash + 1 == requestURI.length()) {
					logger.debug("'/' is last index, doFilter Request");
					// req.getRequestDispatcher("/").forward(req, res);
					chain.doFilter(multiReadRequest, response);
				}
				else {
					String toStorageID = requestURI.substring(lastSlash + 1, requestURI.length());
					// String newURI = requestURI.replace(toReplace,
					// SERVLET_RESOLVER_BY_ID+"?"+SMP_ID+"="+toReplace);
					String newURI = SERVLET_RESOLVER_BY_ID + "?" + SMP_ID + "=" + toStorageID;
					logger.debug("forward to: " + newURI);
					multiReadRequest.getRequestDispatcher(newURI).forward(multiReadRequest, response);
				}
			}
			else {
				logger.debug("is NOT a SMP public uri by ID, doFilter Request");
				chain.doFilter(multiReadRequest, response);
			}
		}
	}

	/**
	 * Gets the scope.
	 *
	 * @param scope the scope
	 * @return the scope
	 */
	private static String getScope(String scope){
		logger.debug("Read scope path: "+scope);
//		String scope = servletPath.substring(servletPath.indexOf("/"), servletPath.length());
		return "/"+scope.replaceAll("_", "/");
	}

	/**
	 * Checks if is geonetwork request.
	 *
	 * @param servletPath the servlet path
	 * @return true, if is geonetwork request
	 */
	private boolean isGeonetworkRequest(String servletPath){
		return servletPath.startsWith(SERVLET_GEONETWORK);
	}

	/* (non-Javadoc)
	 * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
	 */
	@Override
	public void init(FilterConfig config) throws ServletException {
		 logger.trace("run init");
		 this.config = config;
	}
	/*
	public static void main(String[] args) throws ServletException {

		String path = "/geonetwork/gcube_devsec_devVRE/srv/en/mef.export";
		String queryString = "p1=1&p2=2";
		String pathWithoutGN = path.substring(SERVLET_GEONETWORK.length()+1, path.length());
		logger.debug("servlet path without "+SERVLET_GEONETWORK + " is:" +pathWithoutGN);
		String[] params = pathWithoutGN.split("/");

		System.out.println(Arrays.asList(params));

		if(params[0]==null || params[0].isEmpty()){
			logger.error("Scope is null or empty, you must set a valid scope /geonetwork/root_vo_vre");
			throw new ServletException("Scope is null or empty, you must set a valid scope /geonetwork/root_vo_vre");
		}

		String scopeValue = getScope(params[0]);
		logger.debug("scope value is: "+scopeValue);
		String newURI = SERVLET_GEONETWORK + "?" + GeonetworkResolver.SCOPE + "=" + scopeValue;

		if(params.length>1){
			String remainPath = "";
//			newURI +="&remainPath=";
			for (int i = 1; i < params.length; i++) {
				String httpGetParam = params[i];
				if(httpGetParam!=null && !httpGetParam.isEmpty())
					remainPath+="/"+httpGetParam;
			}

			newURI +="&"+GeonetworkResolver.REMAIN_PATH+"="+remainPath;
		}


		if(queryString!=null && !queryString.isEmpty())
			newURI+="&"+queryString;

		logger.debug("forward "+newURI);
	}*/

}

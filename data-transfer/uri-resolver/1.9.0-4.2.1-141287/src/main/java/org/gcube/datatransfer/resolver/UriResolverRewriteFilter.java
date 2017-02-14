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

import org.gcube.datatransfer.resolver.catalogue.CatalogueRequestParameter;
import org.gcube.datatransfer.resolver.catalogue.UrlEncoderUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



/**
 * The Class UriResolverRewriteFilter.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Dec 5, 2016
 */
public class UriResolverRewriteFilter implements Filter{

	/**
	 *
	 */
	public static final String SERVLET_URI_RESOLVER = "/uri-resolver";

	private static final String PATH_SEPARATOR = "/";
	public static final String SERVLET_GEONETWORK = "/geonetwork";
	public static final String PARAMETER_FILTER_PUBLIC_IDS = "filterpublicids";
	public static final String PARAMETER_NO_AUTHENTICATION = "noauthentication";
	public static final String REQUEST_PARAMETER_SEPARATOR = "#";

	public static final String PARAMETER_SMP_ID = "smp-id";
	public static final String SERVLET_STORAGE_ID = "id";

	public static final String PARAMETER_ENC_CATALOGUE_LINK = "cl";
	public static final String PARAMETER_DIRECT_CATALOGUE_LINK = "dl";
	public static final String SERVLET_CATALOGUE = "/catalogue";

	protected static final Logger logger = LoggerFactory.getLogger(UriResolverRewriteFilter.class);
	private FilterConfig config;

	//private ApplicationProfileReaderForCatalogueResolver appPrfCatResolver = new ApplicationProfileReaderForCatalogueResolver(scope, useRootScope)

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
		if(multiReadRequest.getServletPath().startsWith(SERVLET_GEONETWORK) || multiReadRequest.getServletPath().startsWith(SERVLET_URI_RESOLVER+SERVLET_GEONETWORK)){
			logger.debug("is geonetwork request");
			GeonetworkRequestDecoder grd = new GeonetworkRequestDecoder(multiReadRequest.getServletPath(), queryString);
			logger.debug("forward to: "+grd.getNewURI());
			multiReadRequest.getRequestDispatcher(grd.getNewURI()).forward(multiReadRequest, response);

		}else if(multiReadRequest.getServletPath().startsWith(SERVLET_CATALOGUE) || multiReadRequest.getServletPath().startsWith(SERVLET_URI_RESOLVER+SERVLET_CATALOGUE)){
			logger.debug("is a catalogue request");

			//int startIndex = requestURI.indexOf(SERVLET_CATALOGUE))+SERVLET_CATALOGUE.length();
			//String vreName = requestURI.substring(beginIndex)
			HttpServletRequest request = (HttpServletRequest) req;
			logger.debug("method is: "+request.getMethod());

			if(request.getMethod().compareTo("POST")==0){
				logger.debug("is POST request...");
				logger.debug("forward to: " + SERVLET_CATALOGUE);
				multiReadRequest.getRequestDispatcher(SERVLET_CATALOGUE).forward(multiReadRequest, response);
			}
			else{
				logger.debug("is GET request...");
				String[] pathSplit = multiReadRequest.getServletPath().split(PATH_SEPARATOR);
				String newURI = SERVLET_CATALOGUE;
				if(pathSplit.length==5){
					logger.info("Resolving a clear URL to catalogue...");
					logger.debug("found VRE name: "+pathSplit[2]);
					String gcubeScope = CatalogueRequestParameter.GCUBE_SCOPE.getKey() +"="+pathSplit[2];
					String eC = CatalogueRequestParameter.ENTITY_CONTEXT.getKey() +"="+pathSplit[3];
					logger.debug("found context name: "+eC);
					String eN = CatalogueRequestParameter.ENTITY_NAME.getKey() +"="+pathSplit[4];
					logger.debug("found entity name: "+eN);
					String encodedQuery = UrlEncoderUtil.encodeQuery(gcubeScope,eC,eN);
					newURI+=  "?" + PARAMETER_DIRECT_CATALOGUE_LINK + "=" + encodedQuery;
				}else{
					logger.info("Resolving an encrypted URL to catalogue...");
					int lastSlash = requestURI.lastIndexOf(PATH_SEPARATOR);
					String toCatalogueLink = requestURI.substring(lastSlash + 1, requestURI.length());
					newURI+=  "?" + PARAMETER_ENC_CATALOGUE_LINK + "=" + toCatalogueLink;
				}

				logger.debug("forward to: " + newURI);
				multiReadRequest.getRequestDispatcher(newURI).forward(multiReadRequest, response);
			}

			//chain.doFilter(multiReadRequest, response);
		}else{
			//IS WORKSPACE REQUEST?
			if (queryString == null) { // IS A /XXXXX
				logger.debug("QueryString is null, is It a new SMP public uri by ID?");
				int lastSlash = requestURI.lastIndexOf(PATH_SEPARATOR);
				if (lastSlash + 1 == requestURI.length()) {
					logger.debug("'/' is last index, doFilter Request");
					// req.getRequestDispatcher("/").forward(req, res);
					chain.doFilter(multiReadRequest, response);
				}
				else {
					String toStorageID = requestURI.substring(lastSlash + 1, requestURI.length());
					// String newURI = requestURI.replace(toReplace,
					// SERVLET_RESOLVER_BY_ID+"?"+SMP_ID+"="+toReplace);
					String newURI = SERVLET_STORAGE_ID + "?" + PARAMETER_SMP_ID + "=" + toStorageID;
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
		return PATH_SEPARATOR+scope.replaceAll("_", PATH_SEPARATOR);
	}

	/* (non-Javadoc)
	 * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
	 */
	@Override
	public void init(FilterConfig config) throws ServletException {
		 logger.trace("run init");
		 this.config = config;
	}

	/*public static void main(String[] args) {

		String split = "/catalogue/NextNext/dataset/sarda-sarda";

		String[] array = split.split("/");

		System.out.println(array.length);

		for (int i = 0; i < array.length; i++) {
			System.out.println(i+" "+array[i]);
		}

		System.out.println(array[2]);

	}*/
}

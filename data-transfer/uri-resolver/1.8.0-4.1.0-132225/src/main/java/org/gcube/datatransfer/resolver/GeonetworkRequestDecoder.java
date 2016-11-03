/**
 *
 */
package org.gcube.datatransfer.resolver;

import javax.servlet.ServletException;

import org.apache.commons.lang.StringUtils;
import org.gcube.datatransfer.resolver.gis.geonetwork.GeonetworkResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * The Class GeonetworkRequestDecoder.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Jul 27, 2016
 *
 * This class parses a request from servletpath and queryString
 *
 * The request must be: SCOPE#PARMETERS
 * SCOPE must be: separated by {@link GeonetworkRequestDecoder#SCOPE_SEPARATOR}
 * PARAMETERS can be:
 * {@link GeonetworkResolver#PARAMETER_FILTER_PUBLIC_IDS}
 * {@link GeonetworkResolver#PARAMETER_NO_AUTHENTICATION}
 */
public class GeonetworkRequestDecoder {

	/**
	 *
	 */
	public static final String SCOPE_SEPARATOR = "|";

	public static final Logger logger = LoggerFactory.getLogger(GeonetworkRequestDecoder.class);

	private String newURI;

	private GeonetworkRequestCriteria geonetworkRequestCriteria;

	/**
	 * Instantiates a new geonetwork request decoder.
	 *
	 * @param theServletPath the the servlet path
	 * @param queryString the query string
	 * @throws ServletException the servlet exception
	 */
	public GeonetworkRequestDecoder(String theServletPath, String queryString) throws ServletException{
		String path = theServletPath;
		String pathWithoutGN = path.substring(UriResolverRewriteFilter.SERVLET_GEONETWORK.length()+1, path.length());
		logger.debug("servlet path without "+UriResolverRewriteFilter.SERVLET_GEONETWORK + " is: " +pathWithoutGN);
		String[] params = pathWithoutGN.split("/");
		if(params[0]==null || params[0].isEmpty()){
			logger.error("Scope is null or empty, you must set a valid scope /geonetwork/root_vo_vre");
			throw new ServletException("Scope is null or empty, you must set a valid scope /geonetwork/root_vo_vre");
		}

		geonetworkRequestCriteria = getGeonetworkRequestCriteria(params[0]);
		logger.debug("scope value is: "+geonetworkRequestCriteria.getScope());
		newURI = UriResolverRewriteFilter.SERVLET_GEONETWORK + "?" + GeonetworkResolver.SCOPE + "=" + geonetworkRequestCriteria.getScope() +"&"+ GeonetworkResolver.PARAMETER_FILTER_PUBLIC_IDS +"="+geonetworkRequestCriteria.isValueOfFilterPublicIds() +"&"+GeonetworkResolver.PARAMETER_NO_AUTHENTICATION+"="+geonetworkRequestCriteria.isNoAuthOnGeonetwork();
		logger.debug(GeonetworkResolver.PARAMETER_FILTER_PUBLIC_IDS +" is: "+geonetworkRequestCriteria.isValueOfFilterPublicIds());
		logger.debug(GeonetworkResolver.PARAMETER_NO_AUTHENTICATION +" is: "+geonetworkRequestCriteria.isNoAuthOnGeonetwork());

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
	}


	/**
	 * Gets the geonetwork request criteria.
	 * Parses a request like root_vo_vre#filterPublicIds or root_vo_vre
	 *
	 * @param request the request
	 * @return the geonetwork request criteria
	 */
	private static GeonetworkRequestCriteria getGeonetworkRequestCriteria(String request){
		logger.debug("Read request: "+request);
		int index = request.indexOf(UriResolverRewriteFilter.REQUEST_PARAMETER_SEPARATOR);
		String scope = "";
		boolean filterPublicIds = false;
		boolean authOnGN = false;
		logger.trace("Index of "+UriResolverRewriteFilter.REQUEST_PARAMETER_SEPARATOR+ " is "+index);
		if(index!=-1){
			scope = request.substring(0,index);
			filterPublicIds = StringUtils.containsIgnoreCase(request,UriResolverRewriteFilter.PARAMETER_FILTER_PUBLIC_IDS);
			authOnGN = StringUtils.containsIgnoreCase(request,UriResolverRewriteFilter.PARAMETER_NO_AUTHENTICATION);
		}else
			scope = request;

		return new GeonetworkRequestCriteria("/"+scope.replaceAll("\\"+SCOPE_SEPARATOR, "/"), filterPublicIds, authOnGN);
	}


	/**
	 * Gets the new uri.
	 *
	 * @return the newURI
	 */
	public String getNewURI() {

		return newURI;
	}

	/**
	 * @return the geonetworkRequestCriteria
	 */
	public GeonetworkRequestCriteria getGeonetworkRequestCriteria() {

		return geonetworkRequestCriteria;
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {

		StringBuilder builder = new StringBuilder();
		builder.append("GeonetworkRequestDecoder [newURI=");
		builder.append(newURI);
		builder.append(", geonetworkRequestCriteria=");
		builder.append(geonetworkRequestCriteria);
		builder.append("]");
		return builder.toString();
	}

}

/**
 *
 */
package org.gcube.datatransfer.resolver;

import javax.servlet.ServletException;

import org.gcube.datatransfer.resolver.GeonetworkRequestFilterParameters.MODE;
import org.gcube.datatransfer.resolver.GeonetworkRequestFilterParameters.VISIBILITY;
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

	private String gnResolverUriRequest;

	private GeonetworkRequestCriteria geonetworkRequestCriteria;

	/**
	 * Instantiates a new geonetwork request decoder.
	 *
	 * @param theServletPath the the servlet path
	 * @param queryString the query string
	 * @throws ServletException the servlet exception
	 */
	public GeonetworkRequestDecoder(String theServletPath, String queryString) throws ServletException{

		int index = theServletPath.indexOf(GeonetworkRequestFilterParameters.REQUEST_DELIMITIER);

		if(index==-1)
			throw new BadRequestException("Invalid request. Your request must append the '"+GeonetworkRequestFilterParameters.REQUEST_DELIMITIER+"' as final delimiter");

		int delimiterIndex = index+GeonetworkRequestFilterParameters.REQUEST_DELIMITIER.length();

		String pathWithoutGN = theServletPath.substring(UriResolverRewriteFilter.SERVLET_GEONETWORK.length()+1, index);
		logger.debug("servlet path without "+UriResolverRewriteFilter.SERVLET_GEONETWORK + " is: " +pathWithoutGN);
		geonetworkRequestCriteria = getGeonetworkRequestCriteria(pathWithoutGN);
		logger.info("performing query by filters: "+geonetworkRequestCriteria);
		logger.debug("scope value is: "+geonetworkRequestCriteria.getScope());
		//newURI = UriResolverRewriteFilter.SERVLET_GEONETWORK + "?" + GeonetworkResolver.SCOPE + "=" + geonetworkRequestCriteria.getScope() +"&"+ GeonetworkResolver.PARAMETER_FILTER_PUBLIC_IDS +"="+geonetworkRequestCriteria.isValueOfFilterPublicIds() +"&"+GeonetworkResolver.PARAMETER_NO_AUTHENTICATION+"="+geonetworkRequestCriteria.isNoAuthOnGeonetwork();

		gnResolverUriRequest = UriResolverRewriteFilter.SERVLET_GEONETWORK + "?"
		+ GeonetworkResolver.SCOPE + "=" + geonetworkRequestCriteria.getScope() +"&"
		+ GeonetworkRequestFilterParameters.MODE.class.getSimpleName() +"="+geonetworkRequestCriteria.getMode() +"&"
		+ GeonetworkRequestFilterParameters.VISIBILITY.class.getSimpleName()+"="+geonetworkRequestCriteria.getVisibility();

		if(geonetworkRequestCriteria.getOwner()!=null){
			gnResolverUriRequest+="&"+GeonetworkRequestFilterParameters.OWNER_PARAM +"="+geonetworkRequestCriteria.getOwner();
		}

		//BUILDING REMAINING PATH WITHOUT GeonetworkRequestFilterParameters.REQUEST_DELIMITIER
		if(delimiterIndex<theServletPath.length()){
			String remainPath = theServletPath.substring(delimiterIndex, theServletPath.length());
			gnResolverUriRequest +="&"+GeonetworkResolver.REMAIN_PATH_PARAM+"="+remainPath;
		}

		if(queryString!=null && !queryString.isEmpty())
			gnResolverUriRequest+="&"+queryString;

		logger.info("built Geonetwork Resolver GET Request: "+gnResolverUriRequest);
		/*
		String[] params = pathWithoutGN.split("/");
		if(params[0]==null || params[0].isEmpty()){
			logger.error("Scope is null or empty, you must set a valid scope /geonetwork/root|vo|vre");
			throw new ServletException("Scope is null or empty, you must set a valid scope /geonetwork/root"+SCOPE_SEPARATOR+"vo"+SCOPE_SEPARATOR+"vre");
		}

		geonetworkRequestCriteria = getGeonetworkRequestCriteria(params[0]);
		logger.debug("scope value is: "+geonetworkRequestCriteria.getScope());
		newURI = UriResolverRewriteFilter.SERVLET_GEONETWORK + "?" + GeonetworkResolver.SCOPE + "=" + geonetworkRequestCriteria.getScope() +"&"+ GeonetworkResolver.PARAMETER_FILTER_PUBLIC_IDS +"="+geonetworkRequestCriteria.isValueOfFilterPublicIds() +"&"+GeonetworkResolver.PARAMETER_NO_AUTHENTICATION+"="+geonetworkRequestCriteria.isNoAuthOnGeonetwork();
		logger.debug(GeonetworkResolver.PARAMETER_FILTER_PUBLIC_IDS +" is: "+geonetworkRequestCriteria.isValueOfFilterPublicIds());
		logger.debug(GeonetworkResolver.PARAMETER_NO_AUTHENTICATION +" is: "+geonetworkRequestCriteria.isNoAuthOnGeonetwork());
		*/
//		if(params.length>1){
//			String remainPath = "";
////			newURI +="&remainPath=";
//			for (int i = 1; i < params.length; i++) {
//				String httpGetParam = params[i];
//				if(httpGetParam!=null && !httpGetParam.isEmpty())
//					remainPath+="/"+httpGetParam;
//			}
//			newURI +="&"+GeonetworkResolver.REMAIN_PATH+"="+remainPath;
//		}
//
//		if(queryString!=null && !queryString.isEmpty())
//			newURI+="&"+queryString;

	}


	/**
	 * Gets the geonetwork request criteria.

	 * Creates a request criteria from input parameter pathWithoutGN
	 * The parameter pathWithoutGN should be an ordered string (like REST request):
	 * MODE/SCOPE/VISIBILITY/OWNER
	 * MODE must be: {@link MODE}
	 * SCOPE must be: ROOT|VO|VRE
	 * VISIBILITY must be: {@link VISIBILITY}
	 * OWNER (is optional): filter by owner
	 * @param pathWithoutGN the path without Geonetwork base URL
	 *
	 * @return the geonetwork request criteria
	 * @throws ServletException the servlet exception
	 */
	private static GeonetworkRequestCriteria getGeonetworkRequestCriteria(String pathWithoutGN) throws ServletException{

		String[] params = pathWithoutGN.split("/");
		MODE mode = null;
		String theScope = null;
		VISIBILITY visibility = null;
		String owner = null;

		if(params.length < 3){
			throw new BadRequestException("Bad request. Read the request "+pathWithoutGN+". You must pass a valid request like [GEONETWORK_BASE_URL]/SCOPE/MODE/VISIBILITY/OWNER");
		}

		//SCOPE
		if(params[0]!=null && !params[0].isEmpty()){
			theScope = params[0];
			logger.debug("Read parameter scope: "+theScope);
			theScope = theScope.replaceAll("\\"+SCOPE_SEPARATOR, "/");
			if (!theScope.startsWith("/"))
				theScope="/"+theScope;
		}else{
			logger.error("The first parameter 'scope' is null or empty, you must set a valid scope as ROOT"+SCOPE_SEPARATOR+"VO"+SCOPE_SEPARATOR+"VRE as first parameter");
			throw new ServletException("Scope is null or empty. You must pass a valid scope as ROOT"+SCOPE_SEPARATOR+"VO"+SCOPE_SEPARATOR+"VRE as first parameter");
		}

		//MODE
		if(params[1]!=null && !params[1].isEmpty()){
			String modeTU = params[1].toUpperCase();
			logger.debug("Read parameter mode (to upper case): "+modeTU);
			try{
				mode = MODE.valueOf(modeTU);
				if(mode==null){
					logger.error("Mode is null");
					throw new Exception("Mode is null");
				}

				logger.info("MODE IS: "+mode);

			}catch (Exception e) {
				logger.error("The second parameter is wrong, Have you pass a valid parameter MODE like vre/harvest?");
				throw new BadRequestException("Bad parameter. You must set a valid MODE parameter as "+MODE.VRE + " or "+MODE.HARVEST+" as second parameter");
			}
		}else
			throw new BadRequestException("The parameter MODE is null or empty. You must pass a valid MODE parameter as "+MODE.VRE + " or "+MODE.HARVEST +" as second parameter");

		//VISIBILITY
		if(params[2]!=null && !params[2].isEmpty()){
			String visTU = params[2].toUpperCase();
			logger.debug("Read parameter mode (to upper case): "+visTU);
			try{
				visibility = VISIBILITY.valueOf(visTU);
				if(visibility==null){
					logger.error("VISIBILITY is null");
					throw new Exception("VISIBILITY is null");
				}

				logger.info("VISIBILITY IS: "+visibility);

			}catch (Exception e) {
				logger.error("The third parameter is wrong, Have you pass a valid parameter VISIBILITY like vre/harvest?");
				throw new BadRequestException("Bad parameter. You must set a valid VISIBILITY parameter as "+VISIBILITY.PRV + " or "+VISIBILITY.PUB+ " as third parameter");
			}
		}else
			throw new BadRequestException("The parameter VISIBILITY is null or empty. You must pass a valid VISIBILITY parameter as "+VISIBILITY.PRV + " or "+VISIBILITY.PUB +" as third parameter");


		//OWNER
		if(params.length > 3 && params[3]!=null && params[3]!=GeonetworkRequestFilterParameters.REQUEST_DELIMITIER){
			owner = params[3];
			logger.debug("Read parameter owner: "+owner);
		}

		return new GeonetworkRequestCriteria(theScope, mode, owner, visibility);
	}


	/**
	 * Gets the geonetwork resolver uri request.
	 *
	 * @return the geonetwork resolver uri request
	 */
	public String getGeonetworkResolverURIRequest() {

		return gnResolverUriRequest;
	}

	/**
	 * Gets the geonetwork request criteria.
	 *
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
		builder.append("GeonetworkRequestDecoder [gnResolverUriRequest=");
		builder.append(gnResolverUriRequest);
		builder.append(", geonetworkRequestCriteria=");
		builder.append(geonetworkRequestCriteria);
		builder.append("]");
		return builder.toString();
	}



}

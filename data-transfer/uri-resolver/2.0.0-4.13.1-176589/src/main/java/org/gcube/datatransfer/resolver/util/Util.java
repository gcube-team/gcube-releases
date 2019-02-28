/**
 *
 */
package org.gcube.datatransfer.resolver.util;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * The Class Util.
 *
 * @author Francesco Mangiacrapa at ISTI-CNR (francesco.mangiacrapa@isti.cnr.it)
 * Nov 30, 2018
 */
public class Util {

	private static final Logger log = LoggerFactory.getLogger(Util.class);

	/**
	 * Gets the full url.
	 *
	 * @param request the request
	 * @return the full url
	 */
	public static String getFullURL(HttpServletRequest request) {
	    StringBuilder requestURL = new StringBuilder(request.getRequestURL().toString());
	    String queryString = request.getQueryString();

	    if (queryString == null) {
	        return requestURL.toString();
	    } else {
	        return requestURL.append('?').append(queryString).toString();
	    }
	}


	/**
	 * Gets the server url.
	 *
	 * @param req the req
	 * @return the server url
	 */
	public static String getServerURL(HttpServletRequest req) {

		String scheme = req.getScheme();             // http
		String serverName = req.getServerName();     // hostname.com
		int serverPort = req.getServerPort();        // 80
		//String contextPath = req.getContextPath();   // /mywebapp

		// Reconstruct original requesting URL
		StringBuffer url =  new StringBuffer();
		url.append(scheme).append("://").append(serverName);

		if (serverPort != 80 && serverPort != 443)
			url.append(":").append(serverPort);

		//	    if(contextPath!=null)
		//	        url.append(":").append(contextPath);

		String uToS = url.toString();
		log.debug("returning servlet context URL: "+uToS);
		return uToS;
	}


	/**
	 * Gets the server url.
	 *
	 * @param req the req
	 * @return the server url
	 */
	public static String getContextURL(HttpServletRequest req) {

		String serverURL = getServerURL(req);
		return String.format("%s/%s",serverURL,req.getContextPath());
	}

	/**
	 * Removes the last char.
	 *
	 * @param string the string
	 * @return the string
	 */
	public static String removeLastChar(String string){

		if(string == null)
			return null;

		if(string.length()>0)
			return string.substring(0, string.length()-1);

		return string;
	}

}

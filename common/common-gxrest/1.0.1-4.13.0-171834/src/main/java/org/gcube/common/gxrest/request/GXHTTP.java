package org.gcube.common.gxrest.request;

import org.gcube.common.gxrest.response.inbound.GXInboundResponse;

/**
 * 
 * HTTP methods for requests.
 * 
 * @author Manuele Simi (ISTI-CNR)
 *
 * @param <BODY> the type of the body request
 */
interface GXHTTP<BODY> {
	
	/**
	 * Sends the PUT request to the web application.
	 * @param body the body of the request
	 * @return the response
	 */
	GXInboundResponse put(BODY body) throws Exception;
	
	/**
	 * Sends the DELETE request to the web application.
	 * @return the response
	 */
	GXInboundResponse delete() throws Exception;
	
	/**
	 * Sends the HEAD request to the web application.
	 * @return the response
	 */
	GXInboundResponse head() throws Exception;
	
	/**
	 * Sends the GET request to the web application.
	 * @return the response
	 */
	GXInboundResponse get() throws Exception;

	/**
	 * Sends the POST request to the web application.
	 * @param body the body of the request
	 * @return the response
	 * @throws Exception
	 */
	GXInboundResponse post(BODY body) throws Exception;
	
	/**
	 * Overrides the default security token.
	 * @param token the new token
	 */
	void setSecurityToken(String token);

}

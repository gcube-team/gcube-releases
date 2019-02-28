package org.gcube.common.gxhttp.reference;

/**
 * 
 * HTTP methods for requests.
 * 
 * @author Manuele Simi (ISTI-CNR)
 *
 * @param <BODY> the type of the body request
 * @param <RESPONSE> the type of the response

 */
public interface GXHTTP<BODY,RESPONSE> {
	
	/**
	 * Sends the PUT request to the web application.
	 * @param body the body of the request
	 * @return the response
	 */
	RESPONSE put(BODY body) throws Exception;
	
	/**
	 * Sends the PUT request to the web application with no body.
	 * @return the response
	 */
	RESPONSE put() throws Exception;
	
	/**
	 * Sends the DELETE request to the web application.
	 * @return the response
	 */
	RESPONSE delete() throws Exception;
	
	/**
	 * Sends the HEAD request to the web application.
	 * @return the response
	 */
	RESPONSE head() throws Exception;
	
	/**
	 * Sends the GET request to the web application.
	 * @return the response
	 */
	RESPONSE get() throws Exception;

	/**
	 * Sends the POST request to the web application.
	 * @param body the body of the request
	 * @return the response
	 * @throws Exception
	 */
	RESPONSE post(BODY body) throws Exception;
	
	/**
	 * Sends the POST request to the web application with no body.
	 * @return the response
	 * @throws Exception
	 */
	RESPONSE post() throws Exception;
	
	/**
	 * Sends the TRACE request to the web application with no body.
	 * @return the response
	 * @throws Exception
	 */
	RESPONSE trace() throws Exception;
	
	/**
	 * Sends the PATCH request to the web application with no body.
	 * @return the response
	 * @throws Exception
	 */
	RESPONSE patch() throws Exception;
	
	/**
	 * Sends the OPTIONS request to the web application with no body.
	 * @return the response
	 * @throws Exception
	 */
	RESPONSE options() throws Exception;
	
	/**
	 * Sends the CONNECT request to the web application with no body.
	 * @return the response
	 * @throws Exception
	 */
	RESPONSE connect() throws Exception;
	
	/**
	 * Overrides the default security token.
	 * @param token the new token
	 */
	void setSecurityToken(String token);
	
	/**
	 * States if the service being called in an external service (not gCube).
	 * @param ext true if external, false otherwise
	 */
	void isExternalCall(boolean ext);

}

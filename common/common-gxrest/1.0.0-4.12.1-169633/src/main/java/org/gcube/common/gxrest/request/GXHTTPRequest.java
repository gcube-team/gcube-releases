package org.gcube.common.gxrest.request;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Objects;
import java.util.Map.Entry;

import org.gcube.common.gxrest.request.GXConnection.HTTPMETHOD;
import org.gcube.common.gxrest.response.inbound.GXInboundResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A context-aware request to a web application.
 * 
 * @author Manuele Simi (ISTI-CNR)
 * @author Luca Frosini (ISTI-CNR)
 */
public class GXHTTPRequest implements GXHTTP<String> {

	private static final Logger logger = LoggerFactory.getLogger(GXHTTPRequest.class);
	
	private final GXConnection connection;

	/**
	 * A new request.
	 */
	private GXHTTPRequest(String address) {
		this.connection = new GXConnection(address);
	}

	/**
	 * Creates a new request.
	 * @param address the address of the web app to call
	 * @return the request
	 */
	public static GXHTTPRequest newRequest(String address) {
		GXHTTPRequest request = new GXHTTPRequest(address);
		return request;
	}
	
	/**
	 * Sets the identity user agent associated to the request.
	 * @param agent
	 * @return the request
	 */
	public GXHTTPRequest from(String agent) {
		this.connection.setAgent(agent);
		return this;
	}
	
	/**
	 * Adds s positional path parameter to the request.
	 * @param path
	 * @return the request
	 * @throws UnsupportedEncodingException 
	 */
	public GXHTTPRequest path(String path) throws UnsupportedEncodingException {
		this.connection.addPath(path);
		return this;
	}
	
	/**
	 * Sets the body of the request.
	 * @param body
	 * @return the request
	 */
	public GXHTTPRequest withBody(String body) {
		this.connection.addBody(body);
		return this;
	}
	
	/**
	 * Sets the query parameters for the request.
	 * @param parameters the parameters that go in the URL after the address and the path params.
	 * @return the request
	 * @throws UnsupportedEncodingException
	 */
	public GXHTTPRequest queryParams(Map<String, String> parameters) throws UnsupportedEncodingException {
		if (Objects.nonNull(parameters) && ! parameters.isEmpty()) {
			StringBuilder result = new StringBuilder();
			boolean first = true;
			for (Entry<String, String> parameter: parameters.entrySet()){
				if (first) {
					first = false;
				} else {
					result.append(GXConnection.PARAM_SEPARATOR);
				}
				result.append(URLEncoder.encode(parameter.getKey(), GXConnection.UTF8));
				result.append(GXConnection.PARAM_EQUALS);
				result.append(URLEncoder.encode(parameter.getValue(), GXConnection.UTF8));
			}
			connection.setQueryParameters(result.toString());
		}
		return this;
	}
	
	/**
	 * Overrides the default security token.
	 * @param token
	 */
	@Override
	public void setSecurityToken(String token) {
		this.connection.setProperty(org.gcube.common.authorization.client.Constants.TOKEN_HEADER_ENTRY,
				token);
	}
	
	/**
	 * Sends the GET request to the web application.
	 * @return the response
	 */
	@Override
	public GXInboundResponse get() throws Exception {
		logger.trace("Sending a GET request...");
		return this.connection.send(HTTPMETHOD.GET);
	}
	
	/**
	 * Sends the DELETE request to the web application.
	 * @return the response
	 * @throws Exception 
	 */
	@Override
	public GXInboundResponse delete() throws Exception {
		logger.trace("Sending a DELETE request...");
		return this.connection.send(HTTPMETHOD.DELETE);
	}
	
	/**
	 * Sends the HEAD request to the web application.
	 * @return the response
	 * @throws Exception 
	 */
	@Override
	public GXInboundResponse head() throws Exception {
		logger.trace("Sending a HEAD request...");
		return this.connection.send(HTTPMETHOD.HEAD);
	}
	
	/**
	 * Clears all the parameter except the address.
	 */
	public void clear() {
		this.connection.reset();
	}

	/* (non-Javadoc)
	 * @see org.gcube.common.gxrest.request.GXHTTP#put(java.lang.Object)
	 */
	@Override
	public GXInboundResponse put(String body) throws Exception {
		this.connection.addBody(body);
		logger.trace("Sending a PUT request...");
		return this.connection.send(HTTPMETHOD.PUT);
	}

	/* (non-Javadoc)
	 * @see org.gcube.common.gxrest.request.GXHTTP#post(java.lang.Object)
	 */
	@Override
	public GXInboundResponse post(String body) throws Exception {
		this.connection.addBody(body);
		logger.trace("Sending a POST request...");
		return this.connection.send(HTTPMETHOD.POST);
	}

}

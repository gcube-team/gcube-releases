package org.gcube.common.gxhttp.reference;

import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Objects;
import java.util.Map.Entry;

import org.gcube.common.gxhttp.reference.GXConnection.HTTPMETHOD;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Common logic for all GXHTTPRequests.
 * 
 * @author Manuele Simi (ISTI CNR)
 *
 */
public class GXHTTPRequestBuilder {

	protected static final Logger logger = LoggerFactory.getLogger(GXHTTPRequestBuilder.class);

	public GXConnection connection;

	/**
	 * Sets the identity user agent associated to the request.
	 * 
	 * @param agent
	 * @return the request
	 */
	public GXHTTPRequestBuilder from(String agent) {
		this.connection.setAgent(agent);
		return this;
	}

	/**
	 * Adds s positional path parameter to the request.
	 * 
	 * @param path
	 * @return the request
	 * @throws UnsupportedEncodingException
	 */
	public GXHTTPRequestBuilder path(String path) throws UnsupportedEncodingException {
		this.connection.addPath(path);
		return this;
	}

	/**
	 * Sets the query parameters for the request.
	 * 
	 * @param parameters
	 *            the parameters that go in the URL after the address and the
	 *            path params.
	 * @return the request
	 * @throws UnsupportedEncodingException
	 */
	public GXHTTPRequestBuilder queryParams(Map<String, String> parameters) throws UnsupportedEncodingException {
		if (Objects.nonNull(parameters) && !parameters.isEmpty()) {
			StringBuilder result = new StringBuilder();
			boolean first = true;
			for (Entry<String, String> parameter : parameters.entrySet()) {
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
	 * 
	 * @param token
	 */
	public void setSecurityToken(String token) {
		if (!this.connection.isExtCall())
			this.connection.setProperty(org.gcube.common.authorization.client.Constants.TOKEN_HEADER_ENTRY, token);
		else
			throw new UnsupportedOperationException("Cannot set the security token on an external call");
	}

	/**
	 * Add headers to the request.
	 * 
	 * @param name
	 * @param value
	 */
	public GXHTTPRequestBuilder header(String name, String value) {
		this.connection.setProperty(name, value);
		return this;
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.gcube.common.gxrest.request.GXHTTP#put()
	 */
	public HttpURLConnection put() throws Exception {
		logger.trace("Sending a PUT request...");
		return this.connection.send(HTTPMETHOD.PUT);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.gcube.common.gxrest.request.GXHTTP#post()
	 */
	public HttpURLConnection post() throws Exception {
		logger.trace("Sending a POST request...");
		return this.connection.send(HTTPMETHOD.POST);
	}
	/**
	 * Sends the GET request to the web application.
	 * 
	 * @return the response
	 */
	public HttpURLConnection get() throws Exception {
		logger.trace("Sending a GET request...");
		return this.connection.send(HTTPMETHOD.GET);
	}

	/**
	 * Sends the DELETE request to the web application.
	 * 
	 * @return the response
	 * @throws Exception
	 */
	public HttpURLConnection delete() throws Exception {
		logger.trace("Sending a DELETE request...");
		return this.connection.send(HTTPMETHOD.DELETE);
	}

	/**
	 * Sends the HEAD request to the web application.
	 * 
	 * @return the response
	 * @throws Exception
	 */
	public HttpURLConnection head() throws Exception {
		logger.trace("Sending a HEAD request...");
		return this.connection.send(HTTPMETHOD.HEAD);
	}

	/**
	 * Clears all the parameter except the address.
	 */
	public void clear() {
		this.connection.reset();
	}

	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.gcube.common.gxrest.request.GXHTTP#trace()
	 */
	public HttpURLConnection trace() throws Exception {
		logger.trace("Sending a TRACE request...");
		return this.connection.send(HTTPMETHOD.TRACE);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.gcube.common.gxrest.request.GXHTTP#patch()
	 */
	public HttpURLConnection patch() throws Exception {
		logger.trace("Sending a TRACE request...");
		return this.connection.send(HTTPMETHOD.PATCH);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.gcube.common.gxrest.request.GXHTTP#options()
	 */
	public HttpURLConnection options() throws Exception {
		logger.trace("Sending an OPTIONS request...");
		return this.connection.send(HTTPMETHOD.OPTIONS);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.gcube.common.gxrest.request.GXHTTP#connect()
	 */
	public HttpURLConnection connect() throws Exception {
		logger.trace("Sending a CONNECT request...");
		return this.connection.send(HTTPMETHOD.CONNECT);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.gcube.common.gxrest.request.GXHTTP#isExternalCall(boolean)
	 */
	public void isExternalCall(boolean ext) {
		this.connection.setExtCall(ext);
	}
}

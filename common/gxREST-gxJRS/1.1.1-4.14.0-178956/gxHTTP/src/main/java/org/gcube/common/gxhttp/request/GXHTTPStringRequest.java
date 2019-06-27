package org.gcube.common.gxhttp.request;

import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.util.Map;
import java.util.Objects;

import org.gcube.common.gxhttp.reference.GXConnection;
import org.gcube.common.gxhttp.reference.GXHTTP;
import org.gcube.common.gxhttp.reference.GXConnection.HTTPMETHOD;

/**
 * A context-aware request to a web application.
 * It supports sending strings through Put/Post requests.
 *  
 * @author Manuele Simi (ISTI CNR)
 *
 */
public class GXHTTPStringRequest extends GXHTTPCommonRequest implements GXHTTP<String,HttpURLConnection> {
	
	/**
	 * A new request.
	 */
	private GXHTTPStringRequest(String address) {
		builder.connection = new GXConnection(address);
	}

	/**
	 * Creates a new request.
	 * 
	 * @param address
	 *            the address of the web app to call
	 * @return the request
	 */
	public static GXHTTPStringRequest newRequest(String address) {
		return new GXHTTPStringRequest(address);
	}
	
	/**
	 * Sets the body of the request.
	 * 
	 * @param body
	 * @return the request
	 */
	public GXHTTPStringRequest withBody(String body) {
		builder.connection.addBody(body);
		return this;
	}
	
	/* (non-Javadoc)
	 * @see org.gcube.common.gxhttp.reference.GXHTTP#put(java.lang.Object)
	 */
	@Override
	public HttpURLConnection put(String body) throws Exception {
		if (Objects.nonNull(body))
			builder.connection.addBody(body);
		logger.trace("Sending a PUT request...");
		return builder.connection.send(HTTPMETHOD.PUT);
	}

	/* (non-Javadoc)
	 * @see org.gcube.common.gxhttp.reference.GXHTTP#post(java.lang.Object)
	 */
	@Override
	public HttpURLConnection post(String body) throws Exception {
		logger.trace("Sending a POST request...");
		if (Objects.nonNull(body))
			builder.connection.addBody(body);
		return builder.connection.send(HTTPMETHOD.POST);
	}
	
	/**
	 * @param string
	 * @return the request
	 */
	public GXHTTPStringRequest from(String agent) {
		builder.from(agent)	;
		return this;
	}
	
	/**
	 * @param string
	 * @return the request
	 * @throws UnsupportedEncodingException 
	 * 
	 */
	public GXHTTPStringRequest path(String path) throws UnsupportedEncodingException {
		builder.path(path);
		return this;
	}

	/**
	 * @param name
	 * @param value
	 * @return the request
	 */
	public GXHTTPStringRequest header(String name, String value) {
		builder.header(name, value);
		return this;
	}

	/**
	 * @param queryParams
	 * @return the request
	 * @throws UnsupportedEncodingException 
	 */
	public GXHTTPStringRequest queryParams(Map<String, String> queryParams) throws UnsupportedEncodingException {
		builder.queryParams(queryParams);
		return this;
	}



}

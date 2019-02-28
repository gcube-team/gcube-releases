package org.gcube.common.gxhttp.request;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.util.Map;
import java.util.Objects;

import org.gcube.common.gxhttp.reference.GXConnection;
import org.gcube.common.gxhttp.reference.GXHTTP;
import org.gcube.common.gxhttp.reference.GXConnection.HTTPMETHOD;

/**
 * A context-aware request to a web application.
 * It supports sending streams through Put/Post requests.
 *  
 * @author Manuele Simi (ISTI CNR)
 *
 */
public class GXHTTPStreamRequest extends GXHTTPCommonRequest 
	implements GXHTTP<InputStream,HttpURLConnection> {

	/**
	 * A new request.
	 */
	private GXHTTPStreamRequest(String address) {
		builder.connection = new GXConnection(address);
	}

	/**
	 * Creates a new request.
	 * 
	 * @param address
	 *            the address of the web app to call
	 * @return the request
	 */
	public static GXHTTPStreamRequest newRequest(String address) {
		return new GXHTTPStreamRequest(address);
	}
	
	/* (non-Javadoc)
	 * @see org.gcube.common.gxhttp.reference.GXHTTP#put(java.lang.Object)
	 */
	@Override
	public HttpURLConnection put(InputStream body) throws Exception {
		if (Objects.nonNull(body))
			builder.connection.addBodyAsStream(body);
		logger.trace("Sending a PUT request...");
		return builder.connection.send(HTTPMETHOD.PUT);
	}

	/* (non-Javadoc)
	 * @see org.gcube.common.gxhttp.reference.GXHTTP#post(java.lang.Object)
	 */
	@Override
	public HttpURLConnection post(InputStream body) throws Exception {
		logger.trace("Sending a POST request...");
		if (Objects.nonNull(body))
			builder.connection.addBodyAsStream(body);
		return builder.connection.send(HTTPMETHOD.POST);
	}

	/**
	 * @param string
	 * @return the request
	 */
	public GXHTTPStreamRequest from(String agent) {
		builder.from(agent)	;
		return this;
	}
	
	/**
	 * @param path
	 * @return the request
	 * @throws UnsupportedEncodingException 
	 * 
	 */
	public GXHTTPStreamRequest path(String path) throws UnsupportedEncodingException {
		builder.path(path);
		return this;
	}

	/**
	 * @param name
	 * @param value
	 * @return the request
	 */
	public GXHTTPStreamRequest header(String name, String value) {
		builder.header(name, value);
		return this;
	}

	/**
	 * @param queryParams
	 * @return the request
	 * @throws UnsupportedEncodingException 
	 */
	public GXHTTPStreamRequest queryParams(Map<String, String> queryParams) throws UnsupportedEncodingException {
		builder.queryParams(queryParams);
		return this;
	}
}

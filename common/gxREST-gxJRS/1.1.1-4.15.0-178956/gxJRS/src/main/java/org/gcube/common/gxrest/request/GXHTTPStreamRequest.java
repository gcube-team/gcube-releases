package org.gcube.common.gxrest.request;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.Objects;

import org.gcube.common.gxhttp.reference.GXConnection;
import org.gcube.common.gxhttp.reference.GXHTTP;
import org.gcube.common.gxhttp.reference.GXHTTPRequestBuilder;
import org.gcube.common.gxhttp.reference.GXConnection.HTTPMETHOD;
import org.gcube.common.gxrest.response.inbound.GXInboundResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A context-aware request to a web application.
 * It supports sending streams through Put/Post requests.
 *  
 * @author Manuele Simi (ISTI-CNR)
 * @author Luca Frosini (ISTI-CNR)
 *
 * Please use {@link org.gcube.common.gxhttp.request.GXHTTPStreamRequest} instead
 */
@Deprecated
public class GXHTTPStreamRequest implements GXHTTP<InputStream,GXInboundResponse> {
	
	protected static final Logger logger = LoggerFactory.getLogger(GXHTTPStreamRequest.class);

	GXHTTPRequestBuilder builder = new GXHTTPRequestBuilder();
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
	 * @see org.gcube.common.gxrest.request.GXHTTP#put(java.lang.Object)
	 */
	@Override
	public GXInboundResponse put(InputStream body) throws Exception {
		if (Objects.nonNull(body))
			builder.connection.addBodyAsStream(body);
		logger.trace("Sending a PUT request...");
		return new GXInboundResponse(builder.connection.send(HTTPMETHOD.PUT));
	}

	/* (non-Javadoc)
	 * @see org.gcube.common.gxrest.request.GXHTTP#post(java.lang.Object)
	 */
	@Override
	public GXInboundResponse post(InputStream body) throws Exception {
		logger.trace("Sending a POST request...");
		if (Objects.nonNull(body))
			builder.connection.addBodyAsStream(body);
		return new GXInboundResponse(builder.connection.send(HTTPMETHOD.POST));
	}

	/* (non-Javadoc)
	 * @see org.gcube.common.gxrest.request.GXHTTP#put()
	 */
	@Override
	public GXInboundResponse put() throws Exception {
		return new GXInboundResponse(builder.put());
	}

	/* (non-Javadoc)
	 * @see org.gcube.common.gxrest.request.GXHTTP#delete()
	 */
	@Override
	public GXInboundResponse delete() throws Exception {
		return new GXInboundResponse(builder.delete());
	}

	/* (non-Javadoc)
	 * @see org.gcube.common.gxrest.request.GXHTTP#head()
	 */
	@Override
	public GXInboundResponse head() throws Exception {
		return new GXInboundResponse(builder.head());
	}

	/* (non-Javadoc)
	 * @see org.gcube.common.gxrest.request.GXHTTP#get()
	 */
	@Override
	public GXInboundResponse get() throws Exception {
		return new GXInboundResponse(builder.get());
	}

	/* (non-Javadoc)
	 * @see org.gcube.common.gxrest.request.GXHTTP#post()
	 */
	@Override
	public GXInboundResponse post() throws Exception {
		return new GXInboundResponse(builder.post());
	}

	/* (non-Javadoc)
	 * @see org.gcube.common.gxrest.request.GXHTTP#trace()
	 */
	@Override
	public GXInboundResponse trace() throws Exception {
		return new GXInboundResponse(builder.trace());
	}

	/* (non-Javadoc)
	 * @see org.gcube.common.gxrest.request.GXHTTP#patch()
	 */
	@Override
	public GXInboundResponse patch() throws Exception {
		return new GXInboundResponse(builder.patch());
	}

	/* (non-Javadoc)
	 * @see org.gcube.common.gxrest.request.GXHTTP#options()
	 */
	@Override
	public GXInboundResponse options() throws Exception {
		return new GXInboundResponse(builder.options());
	}

	/* (non-Javadoc)
	 * @see org.gcube.common.gxrest.request.GXHTTP#connect()
	 */
	@Override
	public GXInboundResponse connect() throws Exception {
		return new GXInboundResponse(builder.connect());
	}

	/* (non-Javadoc)
	 * @see org.gcube.common.gxrest.request.GXHTTP#setSecurityToken(java.lang.String)
	 */
	@Override
	public void setSecurityToken(String token) {
		builder.setSecurityToken(token);
	}

	/* (non-Javadoc)
	 * @see org.gcube.common.gxrest.request.GXHTTP#isExternalCall(boolean)
	 */
	@Override
	public void isExternalCall(boolean ext) {
		builder.isExternalCall(ext);
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
	 * Clear up the request.
	 */
	public void clear() {
		builder.clear();		
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

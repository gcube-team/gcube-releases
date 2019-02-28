package org.gcube.common.gxrest.request;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Map;
import java.util.Objects;
import java.util.Map.Entry;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import org.gcube.common.authorization.library.provider.SecurityTokenProvider;
import org.gcube.common.gxhttp.reference.GXConnection;
import org.gcube.common.gxhttp.reference.GXHTTP;
import org.gcube.common.gxrest.response.inbound.GXInboundResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A GX request based on JAX-RS. It requires a runtime implementation of JAX-RS
 * on the classpath (e.g. Jersey) to work.
 * 
 * @author Manuele Simi (ISTI CNR)
 *
 */
public class GXWebTargetAdapterRequest implements GXHTTP<Entity<?>,GXInboundResponse> {

	private WebTarget adaptee;
	private static final Logger logger = LoggerFactory.getLogger(GXWebTargetAdapterRequest.class);
	private MediaType[] mediaType;
	MultivaluedMap<String, Object> headers = new MultivaluedHashMap<String, Object>();
	private boolean extCall = false;

	/**
	 * Creates a new request.
	 * 
	 * @param address
	 *            the address of the web app to call
	 * @return the request
	 */
	public static GXWebTargetAdapterRequest newRequest(String address) {
		return new GXWebTargetAdapterRequest(address, false);
	}

	public static GXWebTargetAdapterRequest newHTTPSRequest(String address) {
		return new GXWebTargetAdapterRequest(address, true);
	}

	/**
	 * @param address
	 */
	private GXWebTargetAdapterRequest(String address, boolean withHTTPS) {
		Client client = ClientBuilder.newClient();
		if (withHTTPS) {
			try {
				SSLContext sc = SSLContext.getInstance("TLSv1");
				System.setProperty("https.protocols", "TLSv1");
				TrustManager[] certs = new TrustManager[] { new X509TrustManager() {
					@Override
					public X509Certificate[] getAcceptedIssuers() {
						return null;
					}

					@Override
					public void checkServerTrusted(X509Certificate[] chain, String authType)
							throws CertificateException {
					}

					@Override
					public void checkClientTrusted(X509Certificate[] chain, String authType)
							throws CertificateException {
					}
				} };
				sc.init(null, certs, new java.security.SecureRandom());
				HostnameVerifier allHostsValid = new HostnameVerifier() {
					// insecure host verifier
					@Override
					public boolean verify(String hostname, SSLSession session) {
						return true;
					}
				};
				client = ClientBuilder.newBuilder().sslContext(sc).hostnameVerifier(allHostsValid).build();
			} catch (KeyManagementException | NoSuchAlgorithmException e) {
				client = ClientBuilder.newClient();
			}
		}
		this.adaptee = client.target(address);
		this.headers.add(org.gcube.common.authorization.client.Constants.TOKEN_HEADER_ENTRY,
				SecurityTokenProvider.instance.get());
		this.headers.add("User-Agent", this.getClass().getSimpleName());
	}

	/**
	 * Overrides the default security token.
	 * 
	 * @param token
	 *            the new token
	 */
	@Override
	public void setSecurityToken(String token) {
		if (!this.extCall)
			this.headers.add(org.gcube.common.authorization.client.Constants.TOKEN_HEADER_ENTRY, token);

		else
			throw new UnsupportedOperationException("Cannot set the security token on an external call");
	}

	/**
	 * Sets the identity user agent associated to the request.
	 * 
	 * @param agent
	 * @return the request
	 */
	public GXWebTargetAdapterRequest from(String agent) {
		this.headers.add("User-Agent", this.getClass().getSimpleName());
		return this;
	}

	/**
	 * Sets a new property in the request.
	 * 
	 * @param name
	 *            the name of the property
	 * @param value
	 *            the value of the property
	 * @return the request
	 */
	public GXWebTargetAdapterRequest configProperty(String name, String value) {
		this.adaptee = this.adaptee.property(name, value);
		return this;
	}

	/**
	 * Registers an instance of a custom JAX-RS component (such as an extension
	 * provider or a {@link javax.ws.rs.core.Feature feature} meta-provider) to
	 * be instantiated and used in the scope of this request.
	 * 
	 * @param component
	 *            the component to register
	 * @return the request
	 */
	public GXWebTargetAdapterRequest register(Object component) {
		this.adaptee = this.adaptee.register(component);
		return this;

	}

	/**
	 * Registers a class of a custom JAX-RS component (such as an extension
	 * provider or a {@link javax.ws.rs.core.Feature feature} meta-provider) to
	 * be instantiated and used in the scope of this request.
	 * 
	 * @param component
	 *            the class of the component to register
	 * @return the request
	 */
	public GXWebTargetAdapterRequest register(Class<?> component) {
		this.adaptee = this.adaptee.register(component);
		return this;

	}

	/**
	 * Adds a positional path parameter to the request.
	 * 
	 * @param path
	 *            the new token in the path
	 * @return the request
	 * @throws UnsupportedEncodingException
	 */
	public GXWebTargetAdapterRequest path(String path) throws UnsupportedEncodingException {
		this.adaptee = this.adaptee.path(path);
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
	public GXWebTargetAdapterRequest queryParams(Map<String, Object[]> parameters) throws UnsupportedEncodingException {
		if (Objects.nonNull(parameters) && !parameters.isEmpty()) {
			for (Entry<String, Object[]> parameter : parameters.entrySet()) {
				this.adaptee = this.adaptee.queryParam(URLEncoder.encode(parameter.getKey(), GXConnection.UTF8),
						parameter.getValue());
			}
		}
		return this;
	}

	/**
	 * Defines the accepted response media types.
	 *
	 * @param acceptedResponseTypes
	 *            accepted response media types.
	 * @return builder for a request targeted at the URI referenced by this
	 *         target instance.
	 */
	public GXWebTargetAdapterRequest setAcceptedResponseType(MediaType... acceptedResponseTypes) {
		this.mediaType = acceptedResponseTypes;
		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.gcube.common.gxrest.request.GXHTTP#delete()
	 */
	@Override
	public GXInboundResponse delete() throws Exception {
		logger.trace("Sending a DELETE request...");
		Response response = this.buildRequest().delete();
		return buildGXResponse(response);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.gcube.common.gxrest.request.GXHTTP#head()
	 */
	@Override
	public GXInboundResponse head() throws Exception {
		logger.trace("Sending a HEAD request...");
		Response response = this.buildRequest().head();
		return buildGXResponse(response);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.gcube.common.gxrest.request.GXHTTP#get()
	 */
	@Override
	public GXInboundResponse get() throws Exception {
		logger.trace("Sending a GET request...");
		Response response = this.buildRequest().get(Response.class);
		return buildGXResponse(response);
	}

	/**
	 * Builds the request builder.
	 * 
	 * @return the builder
	 */
	private Builder buildRequest() {
		Builder builder = this.adaptee.request();
		builder.headers(this.headers);
		return builder;
	}

	/**
	 * Add an arbitrary header.
	 * 
	 * @return the builder
	 */
	public GXWebTargetAdapterRequest header(String name, Object value) {
		headers.add(name, value);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.gcube.common.gxrest.request.GXHTTP#put(java.lang.Object)
	 */
	@Override
	public GXInboundResponse put(Entity<?> body) throws Exception {
		logger.trace("Sending a PUT request...");
		if (Objects.nonNull(body))
			return buildGXResponse(this.buildRequest().put(body));
		else
			throw new IllegalArgumentException("Invalid body for the PUT request");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.gcube.common.gxrest.request.GXHTTP#put()
	 */
	@Override
	public GXInboundResponse put() throws Exception {
		logger.trace("Sending a PUT request with no body...");
		return buildGXResponse(this.buildRequest().put(null));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.gcube.common.gxrest.request.GXHTTP#post(java.lang.Object)
	 */
	@Override
	public GXInboundResponse post(Entity<?> body) throws Exception {
		Objects.requireNonNull(body, "Cannot send a POST request with a null body.");
		logger.trace("Sending a POST request...");
		Response response = this.buildRequest().post(body, Response.class);
		return buildGXResponse(response);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.gcube.common.gxrest.request.GXHTTP#post()
	 */
	@Override
	public GXInboundResponse post() throws Exception {
		logger.trace("Sending a POST request with no body...");
		Response response = this.buildRequest().post(null, Response.class);
		return buildGXResponse(response);
	}

	/**
	 * Builds the response.
	 * 
	 * @param source
	 *            the original response returned by the JAX-RS implementation
	 * @return the inbound response
	 */
	private GXInboundResponse buildGXResponse(Response source) {
		return (Objects.isNull(this.mediaType)) ? new GXInboundResponse(source)
				: new GXInboundResponse(source, this.mediaType);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.gcube.common.gxrest.request.GXHTTP#trace()
	 */
	@Override
	public GXInboundResponse trace() throws Exception {
		logger.trace("Sending a TRACE request with no body...");
		Response response = this.buildRequest().trace(Response.class);
		return buildGXResponse(response);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.gcube.common.gxrest.request.GXHTTP#patch()
	 */
	@Override
	public GXInboundResponse patch() throws Exception {
		throw new UnsupportedOperationException("WebTarget does not support PATCH");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.gcube.common.gxrest.request.GXHTTP#options()
	 */
	@Override
	public GXInboundResponse options() throws Exception {
		logger.trace("Sending an OPTIONS request with no body...");
		Response response = this.buildRequest().options(Response.class);
		return buildGXResponse(response);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.gcube.common.gxrest.request.GXHTTP#connect()
	 */
	@Override
	public GXInboundResponse connect() throws Exception {
		throw new UnsupportedOperationException("WebTarget does not support CONNECT");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.gcube.common.gxrest.request.GXHTTP#isExternalCall(boolean)
	 */
	@Override
	public void isExternalCall(boolean ext) {
		this.extCall = ext;
	}
}

package org.gcube.common.gxrest.request;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Objects;
import java.util.Map.Entry;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.gcube.common.authorization.library.provider.SecurityTokenProvider;
import org.gcube.common.gxrest.response.inbound.GXInboundResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * A GX request based on JAX-RS. 
 * It requires a runtime implementation of JAX-RS on the classpath (e.g. Jersey) to work.
 * 
 * @author Manuele Simi (ISTI CNR)
 *
 */
public class GXWebTargetAdapterRequest implements GXHTTP<Entity<?>> {

	private WebTarget adaptee;
	private static final Logger logger = LoggerFactory.getLogger(GXWebTargetAdapterRequest.class);
	private String token, agent;
	private MediaType[] mediaType;
	
	/**
	 * Creates a new request.
	 * @param address the address of the web app to call
	 * @return the request
	 */
	public static GXWebTargetAdapterRequest newRequest(String address) {
		return new GXWebTargetAdapterRequest(address);
	}
	/**
	 * @param address
	 */
	private GXWebTargetAdapterRequest(String address) {
		Client client = ClientBuilder.newClient();
		this.adaptee = client.target(address);
		this.token = SecurityTokenProvider.instance.get();
		this.agent = this.getClass().getSimpleName(); 
	}
	
	/**
	 * Overrides the default security token.
	 * @param token
	 */
	@Override
	public void setSecurityToken(String token) {
		this.token = token;
	}
	
	/**
	 * Sets the identity user agent associated to the request.
	 * @param agent
	 * @return the request
	 */
	public GXWebTargetAdapterRequest from(String agent) {
		this.agent = agent;
		return this;
	}
	
	/**
	 * Sets a new property in the request.
	 * @param name
	 * @param value
	 * @return the request
	 */
	public GXWebTargetAdapterRequest configProperty(String name, String value) {
		this.adaptee = this.adaptee.property(name,value);
		return this;
	}
	
	/**
	 * Register an instance of a custom JAX-RS component (such as an extension provider or
     * a {@link javax.ws.rs.core.Feature feature} meta-provider) to be instantiated
     * and used in the scope of this request.
	 * @param component the compontent to register
	 * @return the request
	 */
	public GXWebTargetAdapterRequest register(Object component) {
		this.adaptee = this.adaptee.register(component);
		return this;

	}

	/**
	 * Adds s positional path parameter to the request.
	 * @param path
	 * @return the request
	 * @throws UnsupportedEncodingException 
	 */
	public GXWebTargetAdapterRequest path(String path) throws UnsupportedEncodingException {
		this.adaptee = this.adaptee.path(path);
		return this;
	}
	
	
	/**
	 * Sets the query parameters for the request.
	 * @param parameters the parameters that go in the URL after the address and the path params.
	 * @return the request
	 * @throws UnsupportedEncodingException
	 */
	public GXWebTargetAdapterRequest queryParams(Map<String, Object[]> parameters) throws UnsupportedEncodingException {
		if (Objects.nonNull(parameters) && ! parameters.isEmpty()) {
			for (Entry<String, Object[]> parameter: parameters.entrySet()){
				this.adaptee = this.adaptee.queryParam(URLEncoder.encode(parameter.getKey(), GXConnection.UTF8), 
						parameter.getValue());
			}
		}
		return this;
	}
	
	/**
     * Defines the accepted response media types.
     *
     * @param acceptedResponseTypes accepted response media types.
     * @return builder for a request targeted at the URI referenced by this target instance.
     */
	public GXWebTargetAdapterRequest setAcceptedResponseType(MediaType... acceptedResponseTypes) {
		this.mediaType = acceptedResponseTypes;
		return this;
	}

	/* (non-Javadoc)
	 * @see org.gcube.common.gxrest.request.GXHTTP#delete()
	 */
	@Override
	public GXInboundResponse delete() throws Exception {
		logger.trace("Sending a DELETE request...");
		Response response =  this.buildRequest().delete();
		return buildGXResponse(response);
	}

	/* (non-Javadoc)
	 * @see org.gcube.common.gxrest.request.GXHTTP#head()
	 */
	@Override
	public GXInboundResponse head() throws Exception {
		logger.trace("Sending a HEAD request...");
		Response response =  this.buildRequest().head();
		return buildGXResponse(response);
	}

	/* (non-Javadoc)
	 * @see org.gcube.common.gxrest.request.GXHTTP#get()
	 */
	@Override
	public GXInboundResponse get() throws Exception {
		logger.trace("Sending a GET request...");
		Response response =  this.buildRequest().get(Response.class);
		return buildGXResponse(response);
	}

	/**
	 * Builds the request builder.
	 * @return the builder
	 */
	private Builder buildRequest() {
		Builder builder = this.adaptee.request();
		builder.header(org.gcube.common.authorization.client.Constants.TOKEN_HEADER_ENTRY, token);
		builder.header("User-Agent", agent);
		return builder;
	}
	/* (non-Javadoc)
	 * @see org.gcube.common.gxrest.request.GXHTTP#put(java.lang.Object)
	 */
	@Override
	public GXInboundResponse put(Entity<?> body) throws Exception {
		Objects.requireNonNull(body, "Cannot send a PUT request with a null body.");
		logger.trace("Sending a PUT request...");
		Response response =  this.buildRequest().put(body);
		return buildGXResponse(response);
	}
	/* (non-Javadoc)
	 * @see org.gcube.common.gxrest.request.GXHTTP#post(java.lang.Object)
	 */
	@Override
	public GXInboundResponse post(Entity<?> body) throws Exception {
		Objects.requireNonNull(body, "Cannot send a POST request with a null body.");
		logger.trace("Sending a POST request...");	
		Response response =  this.buildRequest().post(body, Response.class);
		return buildGXResponse(response);
	}
	
	/**
	 * Builds the response.
	 * 
	 * @param source the original response returned by the JAX-RS implementation
	 * @return the inbound response
	 */
	private GXInboundResponse buildGXResponse(Response source) {
		return (Objects.nonNull(this.mediaType))?
				new GXInboundResponse(source) : new GXInboundResponse(source, this.mediaType);

	}
}

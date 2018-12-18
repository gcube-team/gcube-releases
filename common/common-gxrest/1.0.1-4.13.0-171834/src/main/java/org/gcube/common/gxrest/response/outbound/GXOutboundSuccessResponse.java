package org.gcube.common.gxrest.response.outbound;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.gcube.common.gxrest.response.entity.EntityTag;

/**
 * An outbound success response message for applications.
 * 
 * @author Manuele Simi (ISTI CNR)
 *
 */
public class GXOutboundSuccessResponse {

	private ResponseBuilder delegate;

	private GXOutboundSuccessResponse() {
	}

	/**
	 * Builds a new response with the OK HTTP status.
	 * 
	 * @return the updated response
	 */
	public static GXOutboundSuccessResponse newOKResponse() {
		GXOutboundSuccessResponse response = new GXOutboundSuccessResponse();
		response.delegate = Response.ok();
		response.delegate.tag(EntityTag.gxSuccess);
		return response;
	}

	/**
	 * Builds a new response with the CREATE HTTP status.
	 * 
	 * @return the updated response
	 */
	public static GXOutboundSuccessResponse newCREATEResponse(URI location) {
		GXOutboundSuccessResponse response = new GXOutboundSuccessResponse();
		response.delegate = Response.created(location);
		response.delegate.tag(EntityTag.gxSuccess);
		return response;
	}
	
	/**
	 * Sets the object as response's content.
	 * Any Java type instance for a response entity, that is supported by the
     * runtime can be passed. 
	 * 
	 * @param o the content
	 * @return the updated response
	 * @throws IOException 
	 */
	public GXOutboundSuccessResponse withContent(Object o) throws IOException {
		this.delegate.entity(o);
		return this;
	}
	
	/**
	 * Reads from the stream the content to set in the response. 
	 * 
	 * @param is the stream
	 * @return the updated response
	 * @throws IOException 
	 */
	public GXOutboundSuccessResponse withContent(InputStream is) throws IOException {
		this.delegate.entity(is);
		return this;
	}
	
	/**
	 * Sets the message as the response's content.
	 * 
	 * @param message
	 * @return the updated response
	 */
	public GXOutboundSuccessResponse withContent(String message) {
		this.delegate.entity(message);
		this.delegate.type(MediaType.TEXT_PLAIN);
		return this;
	}
	
	/**
	 * Adds a type to the response message.
	 * 
	 * @param type
	 * @return the updated response
	 */
	public GXOutboundSuccessResponse ofType(MediaType type) {
		this.delegate.type(type);
		return this;
	}
	
	/**
	 * Adds a type to the response message.
	 * 
	 * @param type
	 * @return the updated response
	 */
	public GXOutboundSuccessResponse ofType(String type) {
		this.delegate.type(type);
		return this;
	}
	/**
     * Add an arbitrary header.
     *
     * @param name  the name of the header
     * @param value the value of the header, the header will be serialized
     *              using a {@link javax.ws.rs.ext.RuntimeDelegate.HeaderDelegate} if
     *              one is available via {@link javax.ws.rs.ext.RuntimeDelegate#createHeaderDelegate(java.lang.Class)}
     *              for the class of {@code value} or using its {@code toString} method
     *              if a header delegate is not available. If {@code value} is {@code null}
     *              then all current headers of the same name will be removed.
     * @return the updated response.
     */
	public GXOutboundSuccessResponse withHeader(String name, Object value) {
		this.delegate.header(name, value);
		return this;
	}
	/**
	 * Builds the response to return.
	 * 
	 * @return the response
	 */
	public Response build() {
		return this.delegate.build();
	}
}

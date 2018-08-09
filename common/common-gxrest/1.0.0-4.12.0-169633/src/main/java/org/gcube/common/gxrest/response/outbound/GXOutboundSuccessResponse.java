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
	 * @return the response
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
	 * @return the response
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
	 * @param o
	 * @return the response
	 * @throws IOException 
	 */
	public GXOutboundSuccessResponse withContent(Object o) throws IOException {
		this.delegate.entity(o);
		return this;
	}
	
	/**
	 * Reads from the stream the content to set in the response. 
	 * 
	 * @param o
	 * @return the response
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
	 * @return the response
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
	 * @return the response
	 */
	public GXOutboundSuccessResponse ofType(MediaType type) {
		this.delegate.type(type);
		return this;
	}
	
	/**
	 * Adds a type to the response message.
	 * 
	 * @param type
	 * @return the response
	 */
	public GXOutboundSuccessResponse ofType(String type) {
		this.delegate.type(type);
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

package org.gcube.common.gxrest.response.outbound;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import org.gcube.common.gxrest.response.entity.CodeEntity;
import org.gcube.common.gxrest.response.entity.EntityTag;
import org.gcube.common.gxrest.response.entity.SerializableErrorEntity;

/**
 * Exception with error code returned by a resource method.
 * 
 * @author Manuele Simi (ISTI CNR)
 *
 */
final class WebCodeException extends WebApplicationException {

	private static final long serialVersionUID = 333945715086602250L;

	protected WebCodeException() {
		super(Response.status(Response.Status.NOT_ACCEPTABLE).build());
	}
	
	protected WebCodeException(ErrorCode code, Response.Status status) {
		super(Response.status(status).entity(new CodeEntity(new SerializableErrorEntity(code))).tag(EntityTag.gxError)
				.build());
	}
	
	protected WebCodeException(ErrorCode code) {
		super(Response.status(Response.Status.NOT_ACCEPTABLE)
				.entity(new CodeEntity(new SerializableErrorEntity(code))).tag(EntityTag.gxError).build());
	}
	
}

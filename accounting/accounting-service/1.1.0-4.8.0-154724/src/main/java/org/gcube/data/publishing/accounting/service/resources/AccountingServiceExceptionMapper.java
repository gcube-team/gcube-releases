package org.gcube.data.publishing.accounting.service.resources;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
@Provider
public class AccountingServiceExceptionMapper implements ExceptionMapper<Exception>{

	@Override
	public Response toResponse(Exception exception) {
		Status status = Status.INTERNAL_SERVER_ERROR;
		String entity = exception.getMessage();
		MediaType mediaType = MediaType.TEXT_PLAIN_TYPE;
		return Response.status(status).entity(entity).type(mediaType).build();
	}
	
}

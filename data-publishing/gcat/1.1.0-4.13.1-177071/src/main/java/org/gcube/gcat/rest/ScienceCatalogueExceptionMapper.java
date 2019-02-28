package org.gcube.gcat.rest;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
 @Provider
public class ScienceCatalogueExceptionMapper  implements ExceptionMapper<Exception> {
	
	@Override
	public Response toResponse(Exception exception) {
		
		Status status = Status.INTERNAL_SERVER_ERROR;
		String exceptionMessage = exception.getMessage();
		MediaType mediaType = MediaType.TEXT_PLAIN_TYPE;
		
		
		if(WebApplicationException.class.isAssignableFrom(exception.getClass())) {
			Response gotResponse = ((WebApplicationException) exception).getResponse();
			status = Status.fromStatusCode(gotResponse.getStatusInfo().getStatusCode());
		}
		
		return Response.status(status).entity(exceptionMessage).type(mediaType).build();
	}
	
}

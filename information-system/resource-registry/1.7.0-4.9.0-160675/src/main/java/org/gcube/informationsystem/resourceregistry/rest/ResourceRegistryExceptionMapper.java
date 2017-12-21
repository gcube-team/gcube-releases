package org.gcube.informationsystem.resourceregistry.rest;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.gcube.informationsystem.resourceregistry.api.exceptions.ResourceRegistryException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.er.ERAlreadyPresentException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.er.ERAvailableInAnotherContextException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.er.ERNotFoundException;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
@Provider
public class ResourceRegistryExceptionMapper implements ExceptionMapper<ResourceRegistryException> {
	
	@Override
	public Response toResponse(ResourceRegistryException exception) {
		
		Status status = Status.BAD_REQUEST;
		
		if(ERNotFoundException.class.isAssignableFrom(exception.getClass())) {
			status = Status.NOT_FOUND;
		} else if(ERAlreadyPresentException.class.isAssignableFrom(exception.getClass())) {
			status = Status.CONFLICT;
		} else if(ERAvailableInAnotherContextException.class.isAssignableFrom(exception.getClass())) {
			status = Status.FORBIDDEN;
		} else if(exception.getClass() == ResourceRegistryException.class) {
			status = Status.INTERNAL_SERVER_ERROR;
		}
		
		try {
			String entity = org.gcube.informationsystem.resourceregistry.api.exceptions.ExceptionMapper
					.marshal(exception);
			MediaType mediaType = MediaType.APPLICATION_JSON_TYPE;
			return Response.status(status).entity(entity).type(mediaType).build();
		} catch(Exception e) {
			String entity = exception.getMessage();
			MediaType mediaType = MediaType.TEXT_PLAIN_TYPE;
			return Response.status(status).entity(entity).type(mediaType).build();
		}
		
	}
	
}

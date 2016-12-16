package org.gcube.informationsystem.resourceregistry.resources;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.gcube.informationsystem.resourceregistry.api.exceptions.InternalException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.NotAllowedException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.ObjectNotFound;
import org.gcube.informationsystem.resourceregistry.api.exceptions.ResourceRegistryException;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
@Provider
public class ResourceRegistryExceptionMapper implements ExceptionMapper<ResourceRegistryException>{

	@Override
	public Response toResponse(ResourceRegistryException exception) {
		Status status = Status.BAD_REQUEST;
		String entity = exception.getMessage();
		MediaType mediaType = MediaType.TEXT_PLAIN_TYPE;
		
		if(ObjectNotFound.class.isAssignableFrom(exception.getClass())){
			status = Status.NOT_FOUND;
		}else if(NotAllowedException.class.isAssignableFrom(exception.getClass())){
			status = Status.FORBIDDEN;
		} else if(InternalException.class.isAssignableFrom(exception.getClass())){
			status = Status.INTERNAL_SERVER_ERROR;
		}
		
		return Response.status(status).entity(entity).type(mediaType).build();
	}
	
}

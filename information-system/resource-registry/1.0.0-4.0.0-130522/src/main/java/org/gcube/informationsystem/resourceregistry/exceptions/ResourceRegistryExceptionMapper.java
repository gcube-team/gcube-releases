package org.gcube.informationsystem.resourceregistry.exceptions;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;

import org.gcube.informationsystem.resourceregistry.api.exceptions.NotAllowedException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.ObjectNotFound;
import org.gcube.informationsystem.resourceregistry.api.exceptions.ResourceRegistryException;

public class ResourceRegistryExceptionMapper implements ExceptionMapper<ResourceRegistryException>{

	@Override
	public Response toResponse(ResourceRegistryException exception) {
		if(ObjectNotFound.class.isAssignableFrom(exception.getClass())){
			return Response.status(Status.NOT_FOUND).entity(exception.getMessage()).build();
		}
		
		if(NotAllowedException.class.isAssignableFrom(exception.getClass())){
			return Response.status(Status.FORBIDDEN).entity(exception.getMessage()).build();
		}
		
		return Response.status(Status.BAD_REQUEST).entity(exception.getMessage()).build();
		
		
	}
}

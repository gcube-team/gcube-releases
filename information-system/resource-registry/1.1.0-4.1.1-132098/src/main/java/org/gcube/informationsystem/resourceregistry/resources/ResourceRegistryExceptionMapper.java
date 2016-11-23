package org.gcube.informationsystem.resourceregistry.resources;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.gcube.informationsystem.resourceregistry.api.exceptions.InternalException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.NotAllowedException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.ObjectNotFound;
import org.gcube.informationsystem.resourceregistry.api.exceptions.ResourceRegistryException;

@Provider
public class ResourceRegistryExceptionMapper implements ExceptionMapper<ResourceRegistryException>{

	@Override
	public Response toResponse(ResourceRegistryException exception) {
		
		if(ObjectNotFound.class.isAssignableFrom(exception.getClass())){
			return Response.status(Status.NOT_FOUND).entity(exception.getMessage()).build();
		}
		
		if(NotAllowedException.class.isAssignableFrom(exception.getClass())){
			return Response.status(Status.FORBIDDEN).entity(exception.getMessage()).build();
		}
		
		if(InternalException.class.isAssignableFrom(exception.getClass())){
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(exception.getMessage()).build();
		}
		
		return Response.status(Status.BAD_REQUEST).entity(exception.getMessage()).build();
		
	}
	
}

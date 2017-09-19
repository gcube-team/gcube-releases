package org.gcube.resources.federation.fhnmanager.service.rest;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.gcube.resources.federation.fhnmanager.api.exception.FHNManagerException;

@Provider

public class RESTExceptionMapper implements ExceptionMapper<FHNManagerException> {  
	
    @Override  
    public Response toResponse(FHNManagerException e) {
    	String responseBody = e.getClass().getName() + "\n" + e.getMessage();
        return Response.status(Status.INTERNAL_SERVER_ERROR)  
            .type(MediaType.TEXT_PLAIN)  
            .entity(responseBody).build();  
    }  
  
}  
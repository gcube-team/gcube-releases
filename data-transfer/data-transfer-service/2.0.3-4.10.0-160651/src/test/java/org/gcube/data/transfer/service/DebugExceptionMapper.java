package org.gcube.data.transfer.service;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class DebugExceptionMapper implements ExceptionMapper<Exception> {

    @Override
    public Response toResponse(Exception exception) {
        exception.printStackTrace();
        Response toReturn=null;
        if(exception instanceof WebApplicationException)
        toReturn=((WebApplicationException)exception).getResponse();
        else
        toReturn=Response.serverError().entity(exception.getMessage()).build();
        return toReturn;
    } 
}

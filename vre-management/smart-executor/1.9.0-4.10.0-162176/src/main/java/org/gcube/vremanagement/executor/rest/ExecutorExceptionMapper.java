package org.gcube.vremanagement.executor.rest;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.gcube.vremanagement.executor.exception.ExecutorException;
import org.gcube.vremanagement.executor.exception.PluginInstanceNotFoundException;
import org.gcube.vremanagement.executor.exception.PluginNotFoundException;
import org.gcube.vremanagement.executor.exception.SmartExecutorExceptionMapper;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
@Provider
public class ExecutorExceptionMapper implements ExceptionMapper<ExecutorException> {
	
	@Override
	public Response toResponse(ExecutorException exception) {
		
		Status status = Status.BAD_REQUEST;
		
		if(PluginInstanceNotFoundException.class.isAssignableFrom(exception.getClass()) || PluginNotFoundException.class.isAssignableFrom(exception.getClass())) {
			status = Status.NOT_FOUND;
		} else if(exception.getClass() == ExecutorException.class) {
			status = Status.INTERNAL_SERVER_ERROR;
		}
		
		try {
			String entity = SmartExecutorExceptionMapper.marshal(exception);
			MediaType mediaType = MediaType.APPLICATION_JSON_TYPE;
			return Response.status(status).entity(entity).type(mediaType).build();
		} catch(Exception e) {
			String entity = exception.getMessage();
			MediaType mediaType = MediaType.TEXT_PLAIN_TYPE;
			return Response.status(status).entity(entity).type(mediaType).build();
		}
		
	}
	
}

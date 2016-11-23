package org.gcube.portal.social.networking.ws.ex;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.gcube.portal.social.networking.ws.outputs.ResponseBean;
import org.slf4j.LoggerFactory;


/**
 * Exception thrown when @Valid fail
 * @author Costantino Perciante at ISTI-CNR
 */
@Provider
public class ApplicationException implements ExceptionMapper<Exception> {

	private static final org.slf4j.Logger logger = LoggerFactory.getLogger(ApplicationException.class);

	public Response toResponse(Exception e) {
		logger.warn("ApplicationException invoked for exception " + e);
		return Response
				.status(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode())
				.type(MediaType.APPLICATION_JSON)
				.entity(new ResponseBean(false, e.getMessage(), null))
				.build();
	}
}
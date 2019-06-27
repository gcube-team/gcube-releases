package org.gcube.portal.social.networking.ws.ex;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.gcube.portal.social.networking.ws.outputs.ResponseBean;
import org.slf4j.LoggerFactory;

/**
 * Exception thrown on fail
 * @author Costantino Perciante at ISTI-CNR
 */
@Provider
public class ValidationException implements ExceptionMapper<javax.validation.ValidationException> {

	private static final org.slf4j.Logger logger = LoggerFactory.getLogger(ValidationException.class);
	@Override
	public Response toResponse(javax.validation.ValidationException e) {
		final StringBuilder strBuilder = new StringBuilder();
		for (ConstraintViolation<?> cv : ((ConstraintViolationException) e).getConstraintViolations()) {
			strBuilder.append(cv.getMessage());
			break;
		}

		logger.warn("ValidationException invoked, returning " + strBuilder.toString());
		return Response
				.status(Response.Status.BAD_REQUEST.getStatusCode())
				.type(MediaType.APPLICATION_JSON)
				.entity(new ResponseBean(false, strBuilder.toString(), null))
				.build();
	}
}
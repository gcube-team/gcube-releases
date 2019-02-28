/**
 *
 */
package org.gcube.datatransfer.resolver.services.exceptions;


import java.net.URI;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.gcube.datatransfer.resolver.services.error.ErrorReport;
import org.gcube.datatransfer.resolver.services.error.ExceptionReport;
import org.gcube.datatransfer.resolver.util.Util;
;

/**
 * The Class BadParameterException.
 *
 * @author Francesco Mangiacrapa at ISTI-CNR (francesco.mangiacrapa@isti.cnr.it)
 * Oct 22, 2018
 */
public class BadRequestException extends WebApplicationException {



	/**
	 *
	 */
	private static final long serialVersionUID = -6095255435634429757L;

	/**
	 * Instantiates a new bad parameter exception.
	 *
	 * @param request the request
	 * @param httpReturnStatus the http return status
	 * @param message the message
	 * @param thrownBySource the thrown by source
	 * @param help the help
	 */
	public BadRequestException(HttpServletRequest request, Status httpReturnStatus, String message, Class thrownBySource, URI help) {
		super(Response.status(httpReturnStatus).entity(
			ExceptionReport.builder().
			request(Util.getFullURL(request)).
			method(request.getMethod()).
			success(false).
			help(help).
			error(
				ErrorReport.builder().
				httpErrorCode(Status.BAD_REQUEST.getStatusCode()).
				name(Status.BAD_REQUEST.name())
				.message(message).
				thrownBy(thrownBySource.getName()).build())
		    .build())
			.type(MediaType.APPLICATION_XML).build());

    }
}
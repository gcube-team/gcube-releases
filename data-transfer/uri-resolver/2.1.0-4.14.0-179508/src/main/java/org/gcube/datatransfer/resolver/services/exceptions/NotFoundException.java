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


/**
 * The Class NotFoundException.
 *
 * @author Francesco Mangiacrapa at ISTI-CNR (francesco.mangiacrapa@isti.cnr.it)
 * Oct 23, 2018
 */
public class NotFoundException extends WebApplicationException {


	/**
	 *
	 */
	private static final long serialVersionUID = 7916339601596360889L;


	/**
	 * Instantiates a new not found exception.
	 *
	 * @param request the request
	 * @param httpReturnStatus the http return status
	 * @param message the message
	 * @param thrownBySource the thrown by source
	 * @param help the help
	 */
	public NotFoundException(HttpServletRequest request, Status httpReturnStatus, String message, Class thrownBySource, URI help) {

		super(Response.status(httpReturnStatus).entity(
			ExceptionReport.builder().
			request(Util.getFullURL(request)).
			method(request.getMethod()).
			success(false).
			help(help).
			error(
				ErrorReport.builder().
				httpErrorCode(Status.NOT_FOUND.getStatusCode()).
				name(Status.NOT_FOUND.name())
				.message(message).
				thrownBy(thrownBySource.getName()).build())
		    .build())
			.type(MediaType.APPLICATION_XML).build());

    }

}
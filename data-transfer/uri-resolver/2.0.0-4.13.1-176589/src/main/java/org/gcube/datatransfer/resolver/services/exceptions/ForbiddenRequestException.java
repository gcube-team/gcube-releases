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
 * The Class ForbiddenRequestException.
 *
 * @author Francesco Mangiacrapa at ISTI-CNR (francesco.mangiacrapa@isti.cnr.it)
 * Oct 23, 2018
 */
public class ForbiddenRequestException extends WebApplicationException {




	/**
	 *
	 */
	private static final long serialVersionUID = -7693811244686509390L;

	/**
	 * Instantiates a new forbidden request exception.
	 *
	 * @param request the request
	 * @param httpReturnStatus the http return status
	 * @param message the message
	 * @param thrownBySource the thrown by source
	 * @param help the help
	 */
	public ForbiddenRequestException(HttpServletRequest request, Status httpReturnStatus, String message, Class thrownBySource, URI help) {

		super(Response.status(httpReturnStatus).entity(
			ExceptionReport.builder().
			request(Util.getFullURL(request)).
			method(request.getMethod()).
			success(false).
			help(help).
			error(
				ErrorReport.builder().
				httpErrorCode(Status.FORBIDDEN.getStatusCode()).
				name(Status.FORBIDDEN.name())
				.message(message).
				thrownBy(thrownBySource.getName()).build())
		    .build())
			.type(MediaType.APPLICATION_XML).build());

    }

}
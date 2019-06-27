/**
 *
 */
package org.gcube.datatransfer.resolver.services.error;

import java.net.URI;
import java.net.URISyntaxException;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response.Status;

import org.gcube.datatransfer.resolver.services.exceptions.BadRequestException;
import org.gcube.datatransfer.resolver.services.exceptions.ForbiddenRequestException;
import org.gcube.datatransfer.resolver.services.exceptions.InternalServerException;
import org.gcube.datatransfer.resolver.services.exceptions.NotAuthorizedRequestException;
import org.gcube.datatransfer.resolver.services.exceptions.NotFoundException;
import org.gcube.datatransfer.resolver.services.exceptions.WrongParameterException;


/**
 * The Class ExceptionManager.
 *
 * @author Francesco Mangiacrapa at ISTI-CNR (francesco.mangiacrapa@isti.cnr.it)
 * Dec 14, 2018
 */
public class ExceptionManager {

	/**
	 * Internal error exception.
	 *
	 * @param httpRequest the http request
	 * @param errorMessage the error message
	 * @param thrownBy the thrown by
	 * @param helpURI the help uri
	 * @return the internal server exception
	 */
	public static InternalServerException internalErrorException(HttpServletRequest httpRequest, String errorMessage, Class thrownBy, String helpURI){

		URI theURI = checkURI(helpURI);
		return new InternalServerException(httpRequest, Status.INTERNAL_SERVER_ERROR, errorMessage, thrownBy, theURI);

	}

	/**
	 * Bad request exception.
	 *
	 * @param httpRequest the http request
	 * @param errorMessage the error message
	 * @param thrownBy the thrown by
	 * @param helpURI the help uri
	 * @return the bad request exception
	 */
	public static BadRequestException badRequestException(HttpServletRequest httpRequest, String errorMessage, Class thrownBy, String helpURI){

		URI theURI = checkURI(helpURI);
		return new BadRequestException(httpRequest, Status.BAD_REQUEST, errorMessage, thrownBy, theURI);

	}

	/**
	 * Wrong parameter exception.
	 *
	 * @param httpRequest the http request
	 * @param errorMessage the error message
	 * @param thrownBy the thrown by
	 * @param helpURI the help uri
	 * @return the wrong parameter exception
	 */
	public static WrongParameterException wrongParameterException(HttpServletRequest httpRequest, String errorMessage, Class thrownBy, String helpURI) {

		URI theURI = checkURI(helpURI);
		return new WrongParameterException(httpRequest, Status.BAD_REQUEST, errorMessage, thrownBy, theURI);

	}


	/**
	 * Not found exception.
	 *
	 * @param httpRequest the http request
	 * @param errorMessage the error message
	 * @param thrownBy the thrown by
	 * @param helpURI the help uri
	 * @return the not found exception
	 */
	public static NotFoundException notFoundException(HttpServletRequest httpRequest, String errorMessage, Class thrownBy, String helpURI) {

		URI theURI = checkURI(helpURI);
		return new NotFoundException(httpRequest, Status.NOT_FOUND, errorMessage, thrownBy, theURI);

	}



	/**
	 * Unauthorized exception.
	 *
	 * @param httpRequest the http request
	 * @param errorMessage the error message
	 * @param thrownBy the thrown by
	 * @param helpURI the help uri
	 * @return the not authorized request exception
	 */
	public static NotAuthorizedRequestException unauthorizedException(HttpServletRequest httpRequest, String errorMessage, Class thrownBy, String helpURI) {

		URI theURI = checkURI(helpURI);
		return new NotAuthorizedRequestException(httpRequest, Status.UNAUTHORIZED, errorMessage, thrownBy, theURI);

	}

	/**
	 * Forbidden exception.
	 *
	 * @param httpRequest the http request
	 * @param errorMessage the error message
	 * @param thrownBy the thrown by
	 * @param helpURI the help uri
	 * @return the forbidden request exception
	 */
	public static ForbiddenRequestException forbiddenException(HttpServletRequest httpRequest, String errorMessage, Class thrownBy, String helpURI) {

		URI theURI = checkURI(helpURI);
		return new ForbiddenRequestException(httpRequest, Status.FORBIDDEN, errorMessage, thrownBy, theURI);

	}


	/**
	 * Check uri.
	 *
	 * @param helpURI the help uri
	 * @return the uri
	 */
	public static URI checkURI(String helpURI){
		URI theURI = null;
		try {
			theURI = helpURI!=null?new URI(helpURI):null;
		}
		catch (URISyntaxException e) {
			//silent
		}
		return theURI;
	}
}

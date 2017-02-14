package org.gcube.portal.social.networking.ws.utils;

import javax.ws.rs.core.Response;

/**
 * Class that contains error messages to be returned in the HTTP responses.
 * @author Costantino Perciante at ISTI-CNR
 */
public class ErrorMessages {

	public static final String missingToken = "Missing token.";
	public static final String missingParameters = "Missing request parameters.";
	public static final String invalidToken = "Invalid token.";
	public static final String tokenGenerationFailed = "Token generation failed.";
	public static final String tokenNotApp  = "Invalid token: not belonging to an application.";
	public static final String idNotApp = "Invalid application id: it doesn't belong to an application.";
	public static final String noApplicationProfileAvailable = "There is no application profile for this app id/scope.";
	public static final String badRequest = "Please check the parameter you passed, it seems a bad request";
	public static final String errorMessageApiResult = "The error is reported into the 'message' field of the returned object";
	
	/**
	 * Used for not yet implemented services.
	 * @param _log
	 * @return
	 */
	public static Response serviceNotYetImplemented(org.slf4j.Logger logger){
		
		logger.info("Service not implemented yet...");
		return Response.ok("<html><body><h2>Web service not implemented yet.</h2></body></html>").build();
		
	}

}

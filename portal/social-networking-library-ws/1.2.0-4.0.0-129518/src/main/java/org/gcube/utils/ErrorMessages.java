package org.gcube.utils;

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
	public static final String noApplicationProfileAvailable = "There is no application profile for this app id/scope.";
	
	/**
	 * Used for not yet implemented services.
	 * @param _log
	 * @return
	 */
	public static Response serviceNotYetImplemented(org.slf4j.Logger _log){
		
		_log.info("Service not implemented yet...");
		return Response.ok("<html><body><h2>Web service not implemented yet.</h2></body></html>").build();
		
	}

}
